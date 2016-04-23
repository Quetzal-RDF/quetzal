/******************************************************************************
 * Copyright (c) 2015 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *****************************************************************************/
 package com.ibm.research.rdf.store.sparql11.planner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.research.rdf.store.Context;
import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.Store.PredicateTable;
import com.ibm.research.rdf.store.sparql11.model.AggregateExpression;
import com.ibm.research.rdf.store.sparql11.model.BinaryUnion;
import com.ibm.research.rdf.store.sparql11.model.BindFunctionPattern;
import com.ibm.research.rdf.store.sparql11.model.BindPattern;
import com.ibm.research.rdf.store.sparql11.model.BuiltinFunctionExpression;
import com.ibm.research.rdf.store.sparql11.model.ConstantExpression;
import com.ibm.research.rdf.store.sparql11.model.Expression;
import com.ibm.research.rdf.store.sparql11.model.Expression.EBuiltinType;
import com.ibm.research.rdf.store.sparql11.model.Expression.EExpressionType;
import com.ibm.research.rdf.store.sparql11.model.Expression.ERelationalOp;
import com.ibm.research.rdf.store.sparql11.model.FunctionBase;
import com.ibm.research.rdf.store.sparql11.model.FunctionBase.ServiceKind;
import com.ibm.research.rdf.store.sparql11.model.IRI;
import com.ibm.research.rdf.store.sparql11.model.LogicalExpression;
import com.ibm.research.rdf.store.sparql11.model.Pattern;
import com.ibm.research.rdf.store.sparql11.model.Pattern.EPatternSetType;
import com.ibm.research.rdf.store.sparql11.model.PatternSet;
import com.ibm.research.rdf.store.sparql11.model.ProjectedVariable;
import com.ibm.research.rdf.store.sparql11.model.Query;
import com.ibm.research.rdf.store.sparql11.model.QueryTriple;
import com.ibm.research.rdf.store.sparql11.model.QueryTripleTerm;
import com.ibm.research.rdf.store.sparql11.model.RelationalExpression;
import com.ibm.research.rdf.store.sparql11.model.Service;
import com.ibm.research.rdf.store.sparql11.model.ServiceFunction;
import com.ibm.research.rdf.store.sparql11.model.ServicePattern;
import com.ibm.research.rdf.store.sparql11.model.SimplePattern;
import com.ibm.research.rdf.store.sparql11.model.SubSelectPattern;
import com.ibm.research.rdf.store.sparql11.model.Values;
import com.ibm.research.rdf.store.sparql11.model.ValuesPattern;
import com.ibm.research.rdf.store.sparql11.model.Variable;
import com.ibm.research.rdf.store.sparql11.model.VariableExpression;
import com.ibm.research.rdf.store.sparql11.planner.statistics.SPARQLOptimizerStatistics;
import com.ibm.wala.util.collections.HashMapFactory;
import com.ibm.wala.util.collections.HashSetFactory;
import com.ibm.wala.util.collections.Pair;
import com.ibm.wala.util.graph.Graph;
import com.ibm.wala.util.graph.NumberedGraph;
import com.ibm.wala.util.graph.impl.ExtensionGraph;
import com.ibm.wala.util.graph.impl.SlowSparseNumberedGraph;

import akka.remote.ContainerFormats.PatternType;

@SuppressWarnings("unused")
public class Planner {

	final static Logger logger = LoggerFactory.getLogger(Planner.class);

	private final boolean REVERSE_COSTS = false;

	protected final boolean TEST_PLANNER = false;

	private final boolean BINARY_PLANS = true;
	
	private final boolean GATHER_STARS = true;
	
	private final boolean CHECK_PRODUCT = true;
	
	private final boolean CHECK_MINUS;
	
	protected int graphCounterId = 0;

	private final PlanNodeCreator planFactory;
	
	private PredicateTable reversePreds;
	
	private PredicateTable forwardPreds;

	public Planner(PlanNodeCreator planFactory, boolean checkMinus) {
		this.planFactory = planFactory;
		this.CHECK_MINUS = checkMinus;
	}

	public Planner(boolean checkMinus) {
		this(new PlanNodeCreator() {

			public PlanNode createSTPlanNode(QueryTriple triple,
					AccessMethod method,
					com.ibm.research.rdf.store.schema.Pair<Set<Variable>> variablePair,
					Pattern pattern) {
				return new PlanNode(triple, method, variablePair, pattern);
			}

			public PlanNode createSTPlanNode(PlanNodeType type,
					Set<Variable> operatorVariables, Pattern pattern) {
				return new PlanNode(type, operatorVariables, pattern);
			}
			
			public PlanNode createSTPlanNode(PlanNode reuseNode, Map<Variable, Variable> reuseVarMappings) {
				return new PlanNode(reuseNode, reuseVarMappings);
			}
			
			public PlanNode createSTPlanNode(Values values) {
				return new PlanNode(values);
			}

			@Override
			public PlanNode createSTPlanNode(
					BinaryUnion<Variable, IRI> graphRestriction) {
				return new PlanNode(graphRestriction);
			}
			
			@Override
			public PlanNode createSTPlanNode(
					ServiceNode service) {
				PlanNode p = new PlanNode(service);
				if (service instanceof BindFunctionNode) {
					BindFunctionNode b = (BindFunctionNode) service;
					p.setPost(b.isPost());
				}
				p.setProducedVariables(service.getProducedVariables());

				return p;
			}
			
			
		}, checkMinus);
	}
	
	public Planner() {
		this(true);
	}
	
	public class PlannerError extends Error {

		private static final long serialVersionUID = 4433645078459226747L;

		public PlannerError(String arg0) {
			super(arg0);
		}
		
	}
	
	public enum Kind {
		TRIPLE, UNION, OPTIONAL, MINUS, VALUES, BIND, NOT_EXISTS, EXISTS, JOIN, PINVARIABLE, PINKEY, BLACKBOX, SUBSELECT, STAR, REUSE,MATERIALIZED_TABLE, PARTITION, SERVICE, GRAPH
	}

	public interface Key {
		Set<Variable> gatherVariables();

		/**
		 * indicates whether this key is mandatory (i.e. in each valid plan
		 * exactly one instance of this key must be present). If it is not
		 * mandatory key, a valid plan may have either exactly one instance of
		 * this key or no instance.
		 * 
		 * the greedy planner never generates anything except mandatory keys;
		 * the ILP planner uses optional keys for its own purposes.
		 * 
		 * @return
		 */
		boolean isMandatory();
		
	}

	public interface Node {
		PlanNode augmentPlan(Query q, PlanNode currentHead, NumberedGraph<PlanNode> g, List<Expression> filters, Set<Variable> availableVars, Set<Variable> liveVars);

		Set<? extends Key> getKeys();

		int getId();

		Kind kind();

		Pair<Double, Double> getCost(NumberedGraph<PlanNode> g);

		Set<Variable> getRequiredVariables();

		Set<Variable> getProducedVariables();
	}

	public interface PlanNodeCreator {
		
		PlanNode createSTPlanNode(QueryTriple triple,
				AccessMethod method,
				com.ibm.research.rdf.store.schema.Pair<Set<Variable>> variablePair,
				Pattern pattern);
		
		PlanNode createSTPlanNode(PlanNodeType type, 
				Set<Variable> operatorVariables, 
				Pattern pattern);
		
		PlanNode createSTPlanNode(PlanNode reuseNode, Map<Variable, Variable> reuseVarMappings);
		
		PlanNode createSTPlanNode(Values values);
		
		PlanNode createSTPlanNode(ServiceNode node);
		
		PlanNode  createSTPlanNode(BinaryUnion<Variable, IRI> graphRestriction);
		

	}
	
	public interface RegionPlanner {
		Set<Key> computeNeededKeys(List<Pattern> region);
		
		Set<Node> getApplicableNodes(List<Pattern> region, 
				Set<Variable> availableVars,
				Set<Variable> liveVars,
				Set<Key> neededKeys, 
				Walker walker);

		PlanNode plan(Node parentNode, Pattern topLevelPattern,
				List<Pattern> region, Walker walker, Set<Variable> availableVars,
				Set<Variable> liveVariables, NumberedGraph<PlanNode> g);
	}

	public interface Walker {
		void gatherRegion(Pattern p, List<Pattern> region);
		
		Graph<PlanNode> topPlan();
		
		Plan plan(Query q);

		PlanNode plan(Node parentNode, Pattern p,	Set<Variable> availableVars, Set<Variable> liveVariables, NumberedGraph<PlanNode> g);
	}

	public abstract class AbstractNode implements Node {
		private final int id = ++graphCounterId;
		protected final Pattern p;

		protected AbstractNode(Pattern p) {
			this.p = p;
		}
		
		@Override
		public Set<? extends Key> getKeys() {
			return Collections.singleton(p);
		}

		@Override
		public int getId() {
			return id;
		}

		@Override
		public String toString() {
			return "<" + kind() + ":" + getId() + ":" 
					+  getRequiredVariables() + ":" + getProducedVariables()  + ">";
		}
	}
	public static Set<Variable> gatherInScopeVariables(Pattern p) {
		Set<Pattern> patterns = p.gatherSubPatterns();
		Set<Pattern> subSelects = HashSetFactory.make();
		Set<Pattern> subSelectChildren = HashSetFactory.make();
		Set<Variable> vars = HashSetFactory.make();
		for (Pattern c : patterns) {
			if (c.getType() == EPatternSetType.SUBSELECT) {
				subSelects.add(c);
				for(Pattern s : c.gatherSubPatterns()) {
					if (s != c) {
						subSelectChildren.add(s);
					}
				}
			}
		}

		patterns.removeAll(subSelectChildren);
		patterns.removeAll(subSelects);

		for(Pattern s : subSelects) {
			if (! subSelectChildren.contains(s)) {
				vars.addAll((( SubSelectPattern)s).gatherProjectedVariables());
			}
		}
		
		for (Pattern c : patterns) {
			vars.addAll(c.getVariables());
		}

		return vars;
	}
	
	public static abstract class ApplicableNodes {

		public abstract Set<Key> computeNeededKeys(List<Pattern> region);

		public void getApplicableNodesForSimplePattern(Pattern p,
				Set<Variable> availableVars, Set<Variable> liveVariables, Walker walker, List<Pattern> region, Set<Node> result) {
			// do nothing
		}

		void getApplicableNodesForNotExists(Pattern p,
				Set<Variable> availableVars, Set<Variable> liveVariables, Walker walker, List<Pattern> region, Set<Node> result) {
			// do nothing
		}

		void getApplicableNodesForBind(final Pattern p,
				Set<Variable> availableVars, Set<Variable> liveVariables, final Walker walker,
				List<Pattern> region, Set<Node> result) {
			// do nothing
		}
		
		void getApplicableNodesForBindFunc(final Pattern p,
				Set<Variable> availableVars, Set<Variable> liveVariables, final Walker walker,
				List<Pattern> region, Set<Node> result) {
			// do nothing
		}
		
		void getApplicableNodesForValues(final Pattern p,
				Set<Variable> availableVars, Set<Variable> liveVariables, final Walker walker,
				List<Pattern> region, Set<Node> result) {
			// do nothing
		}


		void getApplicableNodesForMinus(final Pattern p,
				Set<Variable> availableVars, Set<Variable> liveVariables, final Walker walker,
				List<Pattern> region, Set<Node> result) {
			// do nothing
		}

		void getApplicableNodesForUnion(final Pattern p,
				Set<Variable> availableVars, Set<Variable> liveVariables, final Walker walker,
				List<Pattern> region, Set<Node> result) {
			// do nothing
		}

		void getApplicableNodesForExists(final Pattern p,
				Set<Variable> availableVars, Set<Variable> liveVariables, final Walker walker, List<Pattern> region, Set<Node> result) {
			// do nothing
		}

		void getApplicableNodesForSubselect(final Pattern p,
				Set<Variable> availableVars, Set<Variable> liveVariables, final Walker walker,
				List<Pattern> region, Set<Node> result) {
			// do nothing
		}

		void getApplicableNodesForOptional(final Pattern p, Pattern op,
				Set<Variable> availableVars, Set<Variable> liveVariables, final Walker walker,
				List<Pattern> region, Set<Node> result) {
			// do nothing
		}
		
		void getApplicableNodesForGraphRestrictionPattern(final Pattern p,
				Set<Variable> availableVars, Set<Variable> liveVariables, final Walker walker,
				List<Pattern> region, Set<Node> result) {
			// do nothing
		}

		void getApplicableNodesForScope(Pattern p,
				Set<Variable> availableVars, Set<Variable> liveVariables,
				Walker walker, List<Pattern> region, Set<Node> result) {
			// do nothing
		}
		
		void getApplicableNodesForService(Pattern p,
				Set<Variable> availableVars, Set<Variable> liveVariables,
				Walker walker, List<Pattern> region, Set<Node> result) {
			// do nothing
		}

		private Set<Variable> getUnproducedVars(Pattern p ) {
			Set<Variable> result = HashSetFactory.make();
			for(Pattern s : p.gatherSubPatterns()) {
				if (s.getType() == EPatternSetType.BIND) {
					result.addAll(((BindPattern)s).getExpression().gatherVariables());
				}
				for (Expression f : s.gatherFilters()) {
					result.addAll(f.gatherVariables());
				}
			}
			result.removeAll(p.gatherVariables());
			return result;
		}
		
		public final Set<Node> getApplicableNodes(Pattern p,
				Set<Variable> availableVars, Set<Variable> liveVariables,
				Set<Key> neededKeys, Walker walker, List<Pattern> region) {
			Set<Node> result = HashSetFactory.make();

			if (p.getType().equals(EPatternSetType.AND) && region.contains(p.getParent()) && p.getGroup() == p && !p.potentialScopeClashes().isEmpty()) {
				getApplicableNodesForScope(p, availableVars, liveVariables, walker, region, result);
				return result;
			}
			
			switch (p.getType()) {
			case SIMPLE:
				getApplicableNodesForSimplePattern(p, availableVars, liveVariables, walker, region, result);
				break;
			case NOT_EXISTS:
				getApplicableNodesForNotExists(p, availableVars, liveVariables, walker, region, result);
				break;
			case BIND:
				getApplicableNodesForBind(p, availableVars, liveVariables, walker, region, result);
				break;
			case MINUS:
				getApplicableNodesForMinus(p, availableVars, liveVariables, walker, region, result);
				break;
			case UNION:
				getApplicableNodesForUnion(p, availableVars, liveVariables, walker, region, result);
				break;
			case EXISTS:
				getApplicableNodesForExists(p, availableVars, liveVariables, walker, region, result);
				break;
			case SUBSELECT:
				getApplicableNodesForSubselect(p, availableVars, liveVariables, walker, region, result);
				break;
			case GRAPH:
				getApplicableNodesForGraphRestrictionPattern(p, availableVars, liveVariables, walker, region, result);
				break;
			case VALUES:
				getApplicableNodesForValues(p, availableVars, liveVariables, walker, region, result);
				break;
			case SERVICE:
				getApplicableNodesForService(p, availableVars, liveVariables, walker, region, result);
				break;
			case BINDFUNC:
				getApplicableNodesForBindFunc(p, availableVars, liveVariables, walker, region, result);
				break;
			default:
				assert p.getType() == EPatternSetType.AND;
				break;
			}

			if (p.getOptionalPatterns() != null) {
				for (Pattern op : p.getOptionalPatterns()) {
					if (neededKeys.contains(op)) {
						getApplicableNodesForOptional(p, op, availableVars, liveVariables, walker, region, result);
						break;
					}
				}
			}

			return result;
		}
		
		public Set<Node> getApplicableNodes(List<Pattern> region,
				Set<Variable> availableVars, Set<Variable> liveVariables, Set<Key> neededKeys, final Walker walker) {
			Set<Node> result = HashSetFactory.make();
			for(Pattern p : region) {
				result.addAll(getApplicableNodes(p, availableVars, liveVariables, neededKeys, walker, region));
			}
			return result;
		}

	}

	private static Set<Variable> computeLiveVariables(Set<Variable> liveVariables, Set<Key> neededKeys) {
		Set<Variable> newLiveVariables = HashSetFactory.make(liveVariables);
		for(Key key : neededKeys) {
			newLiveVariables.addAll(key.gatherVariables());
		}
		return newLiveVariables;
	}
	
	class BindFunctionNode extends ServiceNode {
		public BindFunctionNode(Service p, Set<Variable> availableVars, Set<Variable> liveVars) {
			super(p, availableVars, liveVars);
		}
		
		public boolean isPost() {
			FunctionBase fun = ((BindFunctionPattern)p).getFuncCall().getFunction();
			return fun.kind() == ServiceKind.POST || 
				   (fun instanceof ServiceFunction && ((ServiceFunction)fun).tableFunction()) ;
		}
		
		
		@Override
		public Set<Variable> getRequiredVariables() {
			assert p instanceof BindFunctionPattern;
			return HashSetFactory.make(((BindFunctionPattern)p).getFuncCall().getVariables());
		}

		@Override
		public Set<Variable> getProducedVariables() {
			assert p instanceof BindFunctionPattern;
			return HashSetFactory.make(((BindFunctionPattern)p).getVariables());
		}
	}
	
	class ServiceNode extends AbstractNode {

		private Set<Variable> availableVars;
		
		public ServiceNode(Service p, Set<Variable> availableVars, Set<Variable> liveVars) {
			super((Pattern) p);
			this.availableVars  = availableVars;
		}
		
		@Override
		public PlanNode augmentPlan(Query q, PlanNode currentHead,
				NumberedGraph<PlanNode> g, List<Expression> filters,
				Set<Variable> availableVars, Set<Variable> liveVars) {
				// compute required vars based on what is available and whats will be produced
				Set<Variable> producedVariables = getProducedVariables();
				Set<Variable> requiredVariables = getRequiredVariables();
				
				PlanNode node = 
					planFactory.createSTPlanNode(this);
				
				node.setFilters(filters);
				
				node.cost = getCost(g);
				assert node.cost.fst > 0.0;
				// Set<Variable> vars = HashSetFactory.make(this.availableVars);
				Set<Variable> vars = HashSetFactory.make(availableVars);
				vars.retainAll(liveVars);
				vars.addAll(producedVariables);
				node.setAvailableVariables(vars);
				node.setProducedVariables(producedVariables);
				g.addNode(node);
				if (currentHead != null) {
					return join(JoinTypes.AND, q, g, currentHead, node, liveVars);		
				} else {
					return node;
				}
		}

		@Override
		public Kind kind() {
			return Kind.SERVICE;
		}

		@Override
		public Pair<Double, Double> getCost(NumberedGraph<PlanNode> g) {
			return Pair.make(Double.MAX_VALUE, Double.MAX_VALUE);
		}

		@Override
		public Set<Variable> getRequiredVariables() {
			return Collections.emptySet();
		}

		@Override
		public Set<Variable> getProducedVariables() {
			Set<Variable> vars = HashSetFactory.make();
			Set<Pattern> subPatterns = p.getSubPatterns(false);
			assert !subPatterns.isEmpty() && subPatterns.size() == 1;
			Pattern subp = subPatterns.iterator().next();
		
			if (subp.getType() == EPatternSetType.SUBSELECT) {
				for (ProjectedVariable v : ((SubSelectPattern) subp).getSelectClause().getProjectedVariables()) {
					vars.add(v.getVariable());
				}
			} else {
				vars.addAll(p.gatherVariables());
			}
			return vars;
		}
		
	}
	
	class ReUseNode implements Node {

		private PlanNode nodeToReUse;
		private Map<Variable, Variable> variableMappings;
		private Set<Key> keys;
		private final int id = ++graphCounterId;
		private Set<Variable> produced = HashSetFactory.make();

		public ReUseNode(PlanNode nodeToReUse, Map<Variable, Variable> variableMappings, Set<Key> keys) {
			this.nodeToReUse = nodeToReUse;
			this.variableMappings = variableMappings;
			this.keys = keys;
			computeProduced();
		}
		
		private void computeProduced() {
			Set<Variable> oldVars = nodeToReUse.getProducedVariables();
			for (Variable v : oldVars) {
				assert variableMappings.containsKey(v) : v;
				produced.add(variableMappings.get(v));
			}
			oldVars = nodeToReUse.getAvailableVariables();
			for (Variable v : oldVars) {
				assert variableMappings.containsKey(v) : v;
				produced.add(variableMappings.get(v));
			}
		}
		
		@Override
		public PlanNode augmentPlan(Query q, PlanNode currentHead,
				NumberedGraph<PlanNode> g, List<Expression> filters,
				Set<Variable> availableVars, Set<Variable> liveVars) {
			assert currentHead == null : " current head: " + currentHead;
			
			for (Variable v : produced) {
				if (liveVars.contains(v)) {
					availableVars.add(v);
				}
			}
			
			// If the live variable in this node is mapped to a variable in the re-use node that is 'not live'
			// we will be in trouble.  Ensure we add it. Note variableMappings is from old to new, so we need to do a reverse lookup
			// from the map
			for (Map.Entry<Variable, Variable> entry : variableMappings.entrySet()) {
				Variable key = entry.getKey();
				Variable value = entry.getValue();
				if (liveVars.contains(value)) {
					nodeToReUse.getAvailableVariables().add(key);
				}
			}
			
			// reuse nodes don't have any required variables
			PlanNode n = planFactory.createSTPlanNode(nodeToReUse, variableMappings);		
		
			n.setProducedVariables(produced);
			n.setOperatorsVariables(produced);
			Set<Variable> empty = Collections.emptySet();
			n.setRequiredVariables(empty);
			Set<Variable> avail = new HashSet<Variable>(availableVars);
			avail.addAll(produced);
			n.setAvailableVariables(avail);
			n.cost = getCost(g);
			n.setFilters(filters);
			
			g.addNode(n);
			return n;
		}

		@Override
		public Set<? extends Key> getKeys() {
			return keys;
		}

		@Override
		public int getId() {
			return id;
		}

		@Override
		public Kind kind() {
			return Kind.REUSE;
		}

		@Override
		public Pair<Double, Double> getCost(NumberedGraph<PlanNode> g) {
			return Pair.make(-1.0 * keys.size(), 0.0);
		}

		@Override
		public Set<Variable> getRequiredVariables() {
			return Collections.emptySet();		
		}

		@Override
		public Set<Variable> getProducedVariables() {
			return produced;
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return "Reuse node:" + nodeToReUse + " has keys: " + keys;
		}
		
	}
	
	abstract class ExpressionNode implements Node {
		private final int id = ++graphCounterId;

		Expression exp;
		Set<Variable> requiredVariables;
		Set<Variable> producedVariables;
		SPARQLOptimizerStatistics stats;

		ExpressionNode(Expression exp, Set<Variable> requiredVariables,
				Set<Variable> producedVariables, SPARQLOptimizerStatistics stats) {
			this.exp = exp;
			this.requiredVariables = requiredVariables;
			this.producedVariables = producedVariables;
			this.stats = stats;
		}

		@Override
		public Set<Variable> getRequiredVariables() {
			// TODO Auto-generated method stub
			return requiredVariables;
		}

		@Override
		public Set<Variable> getProducedVariables() {
			// TODO Auto-generated method stub
			return producedVariables;
		}

		@Override
		public int getId() {
			// TODO Auto-generated method stub
			return id;
		}
		

		@Override
		public Pair<Double, Double> getCost(NumberedGraph<PlanNode> g) {
			if (exp.getType() == EExpressionType.BUILTIN_FUNC) {
				BuiltinFunctionExpression e = (BuiltinFunctionExpression) exp;
				if (e.getBuiltinType().equals(EBuiltinType.REGEX)) {
					return Pair.make(Double.MAX_VALUE, Double.MAX_VALUE);
				}
			}
			// Take a minimum value of the stats here so the planner tries
			// to apply this bind expression eagerly, unless its an XML
			// function
			double min = 0;
			if (min < stats.globalStatistics.getObjectStatistic().average()) {
				min = stats.globalStatistics.getObjectStatistic().average();
			} else if (min < stats.globalStatistics.getSubjectStatistic()
					.average()) {
				min = stats.globalStatistics.getSubjectStatistic()
						.average();
			}

			min = Math.abs(Math.min(min - 1.0, 1.0));
			return Pair.make(min, stats.globalStatistics
					.getPredicateStatistic().average());
		}

		public String toString() {
			return this.getClass() +  exp.toString() + " produced: " + producedVariables + " required: " + requiredVariables;
		}
	}
	
	public class ValuesNode implements Node {

		private final ValuesPattern vp;
		private final Set<Variable> producedVariables;
		private final Set<Variable> requiredVariables;
		private final int id = ++graphCounterId;

		public ValuesNode(ValuesPattern vp, Set<Variable> requiredVariables, Set<Variable> producedVariables) {
			this.vp = vp;
			this.requiredVariables = HashSetFactory.make(requiredVariables);
			this.producedVariables = HashSetFactory.make(producedVariables);
		}
		
		@Override
		public PlanNode augmentPlan(Query q, PlanNode currentHead,
				NumberedGraph<PlanNode> g, List<Expression> filters,
				Set<Variable> availableVars, Set<Variable> liveVars) {
			PlanNode n = planFactory.createSTPlanNode(vp.getValues());
			Set<Variable> vars = HashSetFactory.make();
			// vars.addAll(availableVars);
			vars.addAll(producedVariables);
			n.setAvailableVariables(vars);
			n.setRequiredVariables(requiredVariables);
			n.cost = getCost(g);
			n.setFilters(filters);	
			g.addNode(n);
			if (currentHead != null) {
				for (Variable v: availableVars) {
					if (liveVars.contains(v)) {
						n.getAvailableVariables().add(v);
					}
				}
				return join(JoinTypes.AND, q, g, currentHead, n, liveVars);  
			}
			return n;
		}

		@Override
		public Set<? extends Key> getKeys() {
			return Collections.singleton(vp);
		}

		@Override
		public int getId() {
			return id;
		}

		@Override
		public Kind kind() {
			// TODO Auto-generated method stub
			return Kind.VALUES;
		}

		@Override
		public Pair<Double, Double> getCost(NumberedGraph<PlanNode> g) {
			return Pair.make(1.0, 1.0);
		}

		@Override
		public Set<Variable> getRequiredVariables() {
			return requiredVariables;
		}

		@Override
		public Set<Variable> getProducedVariables() {
			return HashSetFactory.make(producedVariables);
		}

		
	}
	
	public class BindExpressionNode extends ExpressionNode {
		BindPattern bp;
		BindExpressionNode(Expression exp, Set<Variable> requiredVariables,
				Set<Variable> producedVariables, SPARQLOptimizerStatistics stats, BindPattern bp) {
			super(exp, requiredVariables, producedVariables, stats);
			this.bp = bp;
		}

		@Override
		public Kind kind() {
			return Kind.BIND;
		}

		@Override
		public PlanNode augmentPlan(Query q, PlanNode currentHead,
				NumberedGraph<PlanNode> g, List<Expression> filters,
				Set<Variable> availableVars, Set<Variable> liveVars) {

			
			if (!bp.isIndependentBind()) {
				assert currentHead != null;
				PlanNode n = null;
				// if a bind pattern exists on an AND node, we need to move it to one of the successors, and handle all its side effects (adding produced and available variables)
				// because ANDs don't have a corresponding SQL template.  This is ugly, but we cannot take the approach of doing what we do with filters or graph restrictions
				// because binds are patterns that cannot be 'pushed' into other patterns
				if (currentHead.getType() == PlanNodeType.AND) {
					Iterator<PlanNode> succs = g.getSuccNodes(currentHead);
					while (succs.hasNext()) {
						PlanNode succ = succs.next();
						if (succ.getProducedVariables().containsAll(requiredVariables)) {
							n = succ;
							break;
						}
					}
					if (n != null) {
						n.addBindPattern(bp);
						n.addProducedVariables(bp.getVariables());
						n.addAvailableVariables(availableVars);
						n.addAvailableVariables(bp.getVariables());
					}
				} 

				currentHead.addBindPattern(bp);
				currentHead.addProducedVariables(bp.getVariables());
				currentHead.addAvailableVariables(bp.getVariables());
			
				availableVars.add(bp.getVar());
				return currentHead;
			} else {
				
				List<Expression> l = new LinkedList<Expression>();
				l.add(bp.getExpression());
				List<List<Expression>> list = new LinkedList<List<Expression>>();
				list.add(l);
				PlanNode n = planFactory.createSTPlanNode(new Values(Collections.singletonList(bp.getVar()), list));
				n.setAvailableVariables(Collections.singleton(bp.getVar()));
				n.cost = getCost(g);
				n.setFilters(filters);				
				g.addNode(n);
				
				if (currentHead != null) {
					for (Variable v: availableVars) {
						if (liveVars.contains(v)) {
							n.getAvailableVariables().add(v);
						}
					}
					return join(JoinTypes.AND, q, g, currentHead, n, liveVars);  
				}

				return n;
			}
		}

		@Override
		public Set<? extends Key> getKeys() {
			return Collections.singleton(bp);
		}
	}
	
	public class FilterBindExpressionNode extends BindExpressionNode {
		Expression expression;
		
		FilterBindExpressionNode(Expression exp, Set<Variable> requiredVariables,
				Set<Variable> producedVariables, SPARQLOptimizerStatistics stats, BindPattern bp) {
			super(exp, requiredVariables, producedVariables, stats, bp);
			Expression e = new RelationalExpression(new VariableExpression(bp.getVar().getName()), bp.getExpression(), ERelationalOp.EQUAL);
			this.expression = e;
	
		}
		
		@Override
		public PlanNode augmentPlan(Query q, PlanNode currentHead,
				NumberedGraph<PlanNode> g, List<Expression> filters,
				Set<Variable> availableVars, Set<Variable> liveVars) {

				//assert currentHead != null;
			if (currentHead == null) {
				throw new PlannerError("bind with no currentHead");
			}
			
				List<Expression> exp = null;
				if (currentHead.getType() == PlanNodeType.AND) {
					Iterator<PlanNode> succs = g.getSuccNodes(currentHead);
					succs.next();
					PlanNode right = succs.next();
					exp = right.getFilters();
				}  else {
					exp = currentHead.getFilters();
				}
				exp.add(expression);
				currentHead.addProducedVariables(bp.getVariables());
				currentHead.addAvailableVariables(bp.getVariables());
				availableVars.add(bp.getVar());
				return currentHead;
		}
		
		@Override
		public Pair<Double, Double> getCost(NumberedGraph<PlanNode> g) {
			return Pair.make(1.0, 1.0);	// cost is negligible here, reflected by 1.0 (0 cannot be used)
		}
	}
	
	public abstract class CompoundNode extends AbstractNode {
		private final Walker walker;
		private final Set<Variable> availableVars;
		private final Set<Variable> liveVars;

		protected CompoundNode(Pattern pattern, Walker walker,
				Set<Variable> availableVars, Set<Variable> liveVariables) {
			super(pattern);
			this.walker = walker;
			this.liveVars = liveVariables;
			this.availableVars = HashSetFactory.make(availableVars);
		}

		@Override
		public Pair<Double, Double> getCost(NumberedGraph<PlanNode> g) {
			double p1 = 0, p2 = 0;
			for (Pattern disjunct : ((PatternSet)p).getPatterns()) {
				PlanNode dp = walker.plan(this,
						disjunct, HashSetFactory.make(availableVars),
						liveVars, SlowSparseNumberedGraph.<PlanNode> make());
				Pair<Double, Double> dc = dp.cost;
				p1 += dc.fst;
				p2 += dc.snd;
			}
			return Pair.make(p1, p2);
		}

		@Override
		public Set<Variable> getRequiredVariables() {
			return availableVars;
		}

		@Override
		public Set<Variable> getProducedVariables() {
			Set<Variable> vs = p.gatherVariablesWithOptional();
			vs.removeAll(availableVars);
			return vs;
		}

		protected abstract PlanNodeType type();

		protected List<Pattern> getChildPatterns(Pattern p) {
			return ((PatternSet) p).getPatterns();
		}

		protected Set<Variable> combineVars(Set<Variable> opVars, Set<Variable> availableVars, PlanNode dp) {
			opVars.addAll(dp.getProducedVariables());
			opVars.addAll(dp.getRequiredVariables());
			return availableVars;
		}

		protected void setProducedAndRequired(PlanNode up) {
			up.setRequiredVariables(getRequiredVariables());
			up.setProducedVariables(getProducedVariables());
		}

		@Override
		public PlanNode augmentPlan(Query q, PlanNode currentHead, NumberedGraph<PlanNode> g, List<Expression> filters, Set<Variable> availableVars, Set<Variable> liveVars) {
			Set<PlanNode> disjunctPlans = HashSetFactory.make();
			Set<Variable> opVars = HashSetFactory.make();
			Set<Variable> incomingAvailableVars = availableVars;
			Pair<Double, Double> cost = null;
			PlanNode up;
			List<Pattern> childPatterns = getChildPatterns(p);
			if (BINARY_PLANS && childPatterns.size() > 1) {
				Iterator<Pattern> cs = childPatterns.iterator();
				up = walker.plan(this, cs.next(), HashSetFactory.make(incomingAvailableVars), liveVars, g);
				cost = up.cost;
				while (cs.hasNext()) {
					PlanNode dp = walker.plan(this, cs.next(), HashSetFactory.make(incomingAvailableVars), liveVars, g);
					incomingAvailableVars = combineVars(opVars, incomingAvailableVars, dp);
					dp.setRequiredVariables(incomingAvailableVars);

					PlanNode merge = planFactory.createSTPlanNode(type(), opVars, p);
					merge.setFilters(filters);
					setProducedAndRequired(merge);
					
					Set<Variable> available = HashSetFactory.make(incomingAvailableVars);
					available.addAll(merge.getProducedVariables());
					available.retainAll(liveVars);
					merge.setAvailableVariables(available);
					
					cost = add(dp.cost, cost);
					merge.cost = cost;
					
					g.addNode(merge);
					g.addEdge(merge, up);
					g.addEdge(merge, dp);
					
					up.setAvailableVariables(HashSetFactory.make(availableVars));
					up = merge;
				}
				
			} else {
				for (Pattern disjunct : childPatterns) {
					PlanNode dp = walker.plan(this,
							disjunct, HashSetFactory.make(incomingAvailableVars), liveVars, g);
					dp.setFilters(filters);
					disjunctPlans.add(dp);
					incomingAvailableVars = combineVars(opVars, incomingAvailableVars, dp);
					if (cost == null) {
						cost = dp.cost;
					} else {
						cost = add(cost, dp.cost);
					}
				}
				up = planFactory.createSTPlanNode(type(), opVars, p);
				setProducedAndRequired(up);
				up.setAvailableVariables(HashSetFactory.make(availableVars));

				g.addNode(up);
				for (PlanNode dp : disjunctPlans) {
					dp.setRequiredVariables(incomingAvailableVars);
					g.addEdge(up, dp);
				}

				up.cost = cost;
			}

			
			return join(JoinTypes.AND, q, g, currentHead, up, liveVars);
		}
	}
	

	public class StandardApplicableNodes extends ApplicableNodes {
		private final SPARQLOptimizerStatistics stats;
		private final boolean defaultUnionGraph;
		
		public StandardApplicableNodes(boolean defaultUnionGraph, SPARQLOptimizerStatistics stats) {
			this.stats = stats;
			this.defaultUnionGraph = defaultUnionGraph;
		}

		
		private void addKeysForPattern(final Set<Key> neededKeys, Pattern p, List<Pattern> all) {
			if (p.getType() == EPatternSetType.SIMPLE) {
				for (QueryTriple qt : ((SimplePattern) p).getQueryTriples()) {
					neededKeys.add(qt);
				}
			} else {
				neededKeys.add(p);
			}
		}

		@Override
		public Set<Key> computeNeededKeys(List<Pattern> region) {
			final Set<Key> neededKeys = HashSetFactory.make();
			for (Pattern p : region) {
				if (! isDeadPattern(p)) {
					
					if (p.getType() != EPatternSetType.AND) {
						addKeysForPattern(neededKeys, p, region);
					}
					if (region.contains(p.getParent()) && p.getGroup() == p && !p.potentialScopeClashes().isEmpty()) {
						neededKeys.add(p);
					}
					if ((!region.contains(p.getParent()) || p.getGroup() != p || p.potentialScopeClashes().isEmpty()) && p.getOptionalPatterns() != null) {
						for (Pattern op : p.getOptionalPatterns()) {
							if (! isDeadPattern(op)) {
								neededKeys.add(op);
							}
						}
					}
					//neededKeys.addAll(getNeededBuiltInExpressionsPatterns(p));

				}
			}
			return neededKeys;
		}

		class SimpleNode extends QueryTripleNode implements Node {
			private final Set<Variable> liveVars;
			
			public SimpleNode(int accessMethod, QueryTriple queryTriple, Set<Variable> liveVars,
					Pattern p, SPARQLOptimizerStatistics stats, int id) {
				super(accessMethod, queryTriple, p, stats, id);
				this.liveVars = liveVars;
			}

			@Override
			public PlanNode augmentPlan(Query q, PlanNode top, NumberedGraph<PlanNode> g, List<Expression> filters, Set<Variable> availableVars, Set<Variable> liveVars) {
				PlanNode node = 
					planFactory.createSTPlanNode(getQueryTriple(), 
						Plan.getSTAccessMethod(getQueryTriple(), stats, this, q, reversePreds),
						new com.ibm.research.rdf.store.schema.Pair<Set<Variable>>(
								getProducedVariables(), getRequiredVariables()),
								getPattern());
				node.cost = getCost(g);
				assert node.cost.fst > 0.0;
				node.setFilters(filters);
				Set<Variable> vars = HashSetFactory.make(availableVars);
				vars.addAll(getProducedVariables());
				vars.retainAll(liveVars);
				node.setAvailableVariables(vars);
				return join(JoinTypes.AND, q, g, top, node, liveVars);
			}

			@Override
			public Set<Key> getKeys() {
				return Collections.singleton(getKey());
			}
			
		}
		
		class FilterExistsNotExistsNode extends AbstractNode {
			private final Set<Variable> availableVars;
			private final Walker walker;
			private final Set<Variable> neededVars;
			private boolean isNegated;
			private Pattern p;
			private Pattern left;
			private Pattern right;
			
			FilterExistsNotExistsNode(Pattern p, boolean isNegated, Walker walker, Set<Variable> availableVars, Set<Variable> neededVars) {
				super(p);
				this.left = ((PatternSet) p).getPatterns().get(0);
				this.right = ((PatternSet) p).getPatterns().get(1);
				this.walker = walker;
				this.availableVars = HashSetFactory.make(availableVars);
				this.neededVars = HashSetFactory.make(neededVars);
				this.isNegated = isNegated;
			}

			@Override
			public Kind kind() {
				if (isNegated) {
					return Kind.NOT_EXISTS;
				} else {
					return Kind.EXISTS;
				}
			}

			@Override
			public PlanNode augmentPlan(Query q, PlanNode currentHead,
					NumberedGraph<PlanNode> g, List<Expression> filters,
					Set<Variable> availableVars, Set<Variable> liveVars) {
				assert currentHead != null;
	
				Set<Variable> vars = HashSetFactory.make(availableVars);
				vars.retainAll(liveVars);
				
				PlanNode rightNode = walker.plan(this,
						right,
						HashSetFactory.make(this.availableVars.isEmpty()? this.availableVars: availableVars), 
						vars, 
						g);
				
				PlanNode filterNode = null;
				
				if (isNegated) {
					filterNode = join(JoinTypes.NOT_EXISTS, q, g, currentHead, rightNode, liveVars);
				} else {
					filterNode = join(JoinTypes.EXISTS, q, g, currentHead, rightNode, liveVars);

				}
				
				
				// KAVITHA: Need to add filters from the LHS if they exist only there
				filterNode.setFilters(currentHead.getFilters());
				
				filterNode.setCost(getCost(g));
				Set<Variable> av = HashSetFactory.make(currentHead.getAvailableVariables());
				av.retainAll(liveVars);
				
				filterNode.setProducedVariables(av);
				filterNode.setAvailableVariables(av);

				return filterNode;
			}

			@Override
			public Pair<Double, Double> getCost(NumberedGraph<PlanNode> g) {
				try {
					PlanNode rp = walker.plan(this,
						right,
						HashSetFactory.make(availableVars),
						Collections.<Variable> emptySet(), new ExtensionGraph<PlanNode>(g));
					return rp.cost;
				} catch (PlannerError e) {
					return Pair.make(Double.MAX_VALUE, Double.MAX_VALUE);
				}
			}

			@Override
			public Set<Variable> getRequiredVariables() {
				return neededVars;
			}

			@Override
			public Set<Variable> getProducedVariables() {
				return Collections.emptySet();
			}

		}

		@Override
		public void getApplicableNodesForSimplePattern(Pattern p,
				Set<Variable> availableVars, Set<Variable> liveVars, Walker walker, List<Pattern> region, Set<Node> result) {
			for (QueryTriple q : ((SimplePattern) p).getQueryTriples()) {
				for (int accessMethod = 1; accessMethod <= 6; accessMethod++) {
					if ( (p.getGraphRestriction() != null && !q.getPredicate().isComplexPath()) || accessMethod != QueryTripleNode.GDPH) {
						// graph access  not allow for complex property paths
						Node n = new SimpleNode(accessMethod, q, liveVars, p, stats, ++graphCounterId);
						if (availableVars.containsAll(n.getRequiredVariables())) {
							result.add(n);
						}
					}
				}
			}
		}

		
		
		@Override
		void getApplicableNodesForGraphRestrictionPattern(final Pattern p,
				Set<Variable> availableVars, Set<Variable> liveVariables,
				Walker walker, List<Pattern> region, Set<Node> result) {
			assert p.getType().equals(EPatternSetType.GRAPH) : p.getType();
			AbstractNode node = new AbstractNode(p) {
				
				@Override
				public Kind kind() {
					return Kind.GRAPH;
				}
				
				@Override
				public Set<Variable> getRequiredVariables() {
					return Collections.emptySet();
				}
				
				@Override
				public Set<Variable> getProducedVariables() {
					return p.gatherVariables();
				}
				
				@Override
				public Pair<Double, Double> getCost(NumberedGraph<PlanNode> g) {
					// TODO: Provide a better cost estimate
					return Pair.make(0.0, 0.0);
				}
				
				@Override
				public PlanNode augmentPlan(Query q, PlanNode currentHead,
						NumberedGraph<PlanNode> g, List<Expression> filters,
						Set<Variable> availableVars, Set<Variable> liveVars) {
					PlanNode node = 
							planFactory.createSTPlanNode(p.getGraphRestriction());
					    node.setRequiredVariables(getRequiredVariables());
					    node.setProducedVariables(getProducedVariables());
						node.cost = getCost(g);
						//assert node.cost.fst > 0.0;
						node.setFilters(filters);
						Set<Variable> vars = HashSetFactory.make(availableVars);
						vars.addAll(getProducedVariables());
						vars.retainAll(liveVars);
						node.setAvailableVariables(vars);
						return join(JoinTypes.AND, q, g, currentHead, node, liveVars);
				}
			};
			result.add(node);
		}

		@Override
		void getApplicableNodesForExists(Pattern p,
				final Set<Variable> availableVars,
				final Set<Variable> liveVars, final Walker walker,
				List<Pattern> region, Set<Node> result) {
			final Set<Variable> neededVars = getDependentVariables(region, p);
			if (availableVars.containsAll(neededVars)) {
				result.add(new FilterExistsNotExistsNode(p, false, walker, availableVars, neededVars));
				result.add(new FilterExistsNotExistsNode(p, false, walker, Collections.<Variable>emptySet(), neededVars));
			}
			
		}
		
		@Override
		void getApplicableNodesForNotExists(Pattern p,
				final Set<Variable> availableVars, final Set<Variable> liveVars, final Walker walker, List<Pattern> region, Set<Node> result) {
			final Set<Variable> neededVars = getDependentVariables(region, ((FilterNotExistsPattern)p).getPatterns().get(1));
			if (availableVars.containsAll(neededVars)) {
				result.add(new FilterExistsNotExistsNode(p, true, walker, availableVars, neededVars));
				result.add(new FilterExistsNotExistsNode(p, true, walker, Collections.<Variable>emptySet(), neededVars));
			}
		}

		private Set<Variable> getDependentVariables(List<Pattern> region, Pattern p) {
			Set<Pattern> subtrahends = HashSetFactory.make();
			for(Pattern x : region) {
				for(Pattern y : x.gatherSubPatternsExcluding(p, false)) {
					switch (y.getType()) {
					case EXISTS:
					case NOT_EXISTS:
						subtrahends.addAll( ((PatternSet)y).getPatterns().get(1).gatherSubPatterns() );
						break;
					case MINUS: 
						subtrahends.addAll( y.gatherSubPatterns() );
						break;
					default:
					}
				}
			}
			
			Set<Variable> result = HashSetFactory.make();
			for(Pattern x : region) {
				for(Pattern y : x.gatherSubPatternsExcluding(p, false)) {
					if (!(y instanceof PatternSet || subtrahends.contains(y))) {
						result.addAll(y.getVariables());
					}
				}
			}
			
			Set<Variable> mine = HashSetFactory.make();
			mine.addAll(p.gatherVariablesWithOptional());
			for(Expression e : p.gatherFilters()) {
				mine.addAll(e.gatherVariables());
			}
			
			result.retainAll(mine);
			
			return result;
		}
				
		@Override
		void getApplicableNodesForMinus(Pattern p, final Set<Variable> availableVars, final Set<Variable> liveVars,
				final Walker walker, List<Pattern> region, Set<Node> result) {
			final Set<Variable> allDeps = getDependentVariables(region, p);
			
			final Set<Variable> unscoped = p.optionalOnlyVars();
			
			System.err.println("allDeps " + allDeps);
			System.err.println("availableVars " + availableVars);
			System.err.println("liveVars " + liveVars);

			Set<Variable> willKill = HashSetFactory.make(availableVars);
			willKill.removeAll(allDeps);
			boolean tryNoFinalJoin = Collections.disjoint(liveVars, willKill);
						
			if (availableVars.containsAll(allDeps)) {
				System.err.println("tryNoFinalJoin: " + tryNoFinalJoin);
				class MinusNode extends AbstractNode {
					final boolean noFinalJoin;
					
					public MinusNode(Boolean noFinalJoin, Pattern p) {
						super(p);
						this.noFinalJoin = noFinalJoin;
					}

					@Override
					public Kind kind() {
						return Kind.MINUS;
					}

					@Override
					public PlanNode augmentPlan(Query q, PlanNode currentHead,
							NumberedGraph<PlanNode> g, List<Expression> filters, Set<Variable> availableVars, Set<Variable> liveVars) {
						assert currentHead != null;
						
						Pattern toSubtract = ((PatternSet)p).getPatterns().get(0);
						
						Set<Variable> myVars = HashSetFactory.make(availableVars);
						myVars.removeAll(unscoped);
						
						Set<Variable> rhsAvailable, rhsLive;
						if (noFinalJoin) {
							rhsAvailable = HashSetFactory.make();
							rhsLive = HashSetFactory.make(allDeps);
						} else {
							rhsAvailable = HashSetFactory.make(myVars);
							
							rhsLive = HashSetFactory.make(liveVars);
							rhsLive.addAll(allDeps);
						}
						
						PlanNode subtractNode;
						if (noFinalJoin) {
							try {
								subtractNode = walker.plan(this, toSubtract, rhsAvailable, rhsLive, g); 								
							} catch (PlannerError e) {
								rhsAvailable = HashSetFactory.make(myVars);
								subtractNode = walker.plan(this, toSubtract, rhsAvailable, rhsLive, g); 								
							}
						} else {
							subtractNode = walker.plan(this, toSubtract, rhsAvailable, rhsLive, g);
						}
						PlanNode x = planFactory.createSTPlanNode(PlanNodeType.MINUS, noFinalJoin? allDeps: myVars, p);

						g.addNode(subtractNode);
						g.addNode(x);
						g.addEdge(x, currentHead);
						g.addEdge(x, subtractNode);

						x.setRequiredVariables(allDeps);
						Set<Variable> produced = HashSetFactory.make(noFinalJoin? allDeps: myVars);
						x.setProducedVariables(produced);
						x.setCost(subtractNode.cost);
						Set<Variable> av = HashSetFactory.make(availableVars);
						av.retainAll(liveVars);
						x.setAvailableVariables(av);

						return x;
					}

					@Override
					public Pair<Double, Double> getCost(NumberedGraph<PlanNode> g) {
						try {
							PlanNode dp = walker.plan(this,
									((PatternSet)p).getPatterns().get(0), HashSetFactory.make(noFinalJoin? Collections.<Variable>emptySet(): availableVars),
									HashSetFactory.make(liveVars), noFinalJoin? SlowSparseNumberedGraph.duplicate(g): SlowSparseNumberedGraph.<PlanNode> make());
							assert dp.cost != null : ((PatternSet)p).getPatterns().get(0);
							return dp.cost;
						} catch (PlannerError | IllegalArgumentException e) {
							return Pair.make(Double.MAX_VALUE, Double.MAX_VALUE);
						}
					}

					@Override
					public Set<Variable> getRequiredVariables() {
						return allDeps;
					}

					@Override
					public Set<Variable> getProducedVariables() {
						Set<Variable> vs = ((PatternSet)p).getPatterns().iterator().next().gatherVariables();
						vs.retainAll(availableVars);
						vs.removeAll(allDeps);
						return vs;
					}

				}
				
				result.add(new MinusNode(false, p));
				if (tryNoFinalJoin) {
					result.add(new MinusNode(true, p));
				}
			}
		}

		@Override
		void getApplicableNodesForUnion(Pattern p, final Set<Variable> availableVars, final Set<Variable> liveVars,
				final Walker walker, List<Pattern> region, Set<Node> result) {
			result.add(new AbstractNode(p) {
				private Set<Variable> required;
				private Set<Variable> produced;
				
				{
					required = HashSetFactory.make(availableVars);
					required.retainAll(p.gatherVariablesWithOptional());

					produced = p.gatherVariablesWithOptional();
					produced.removeAll(availableVars);
					produced.retainAll(liveVars);
				}
				
				@Override
				public PlanNode augmentPlan(Query q, PlanNode currentHead,
						NumberedGraph<PlanNode> g, List<Expression> filters, Set<Variable> availableVars, Set<Variable> liveVars) {
					if (BINARY_PLANS) {
						Iterator<Pattern> disjuncts = ((PatternSet)p).getPatterns().iterator();
						
						Pattern disjunct = disjuncts.next();
						while (isDeadPattern(disjunct)) {
							disjunct = disjuncts.next();
						}
						
						PlanNode x = walker.plan(this, disjunct, HashSetFactory.make(availableVars), liveVars, g);

						Set<Variable> required = HashSetFactory.make(x.getRequiredVariables());

						Set<Variable> avs = HashSetFactory.make(availableVars);
						avs.retainAll(liveVars);
						
						while (disjuncts.hasNext()) {
							disjunct = disjuncts.next();
							if (isDeadPattern(disjunct)) {
								continue;
							}
							
							PlanNode y = walker.plan(this, disjunct, HashSetFactory.make(availableVars), liveVars, g);
							required.addAll(y.getRequiredVariables());
							
							PlanNode merge = planFactory.createSTPlanNode(PlanNodeType.UNION, avs, p);
							merge.setFilters(filters);
							merge.cost = add(x.cost, y.cost);
							avs.addAll(x.getProducedVariables());
							avs.addAll(y.getProducedVariables());
							avs.retainAll(liveVars);
							merge.setAvailableVariables(HashSetFactory.make(avs));
							merge.setProducedVariables(HashSetFactory.make(avs));
							merge.setRequiredVariables(HashSetFactory.make(required));
							
							g.addNode(merge);
							g.addEdge(merge, x);
							g.addEdge(merge, y);
							
							x = merge;

						} 
						return join(JoinTypes.AND, q, g, currentHead, x, liveVars);
					} else {
						assert false;
					}
					return null;
				}

				@Override
				public Kind kind() {
					return Kind.UNION;
				}

				@Override
				public Pair<Double, Double> getCost(NumberedGraph<PlanNode> g) {
					Pair<Double,Double> cost = null;
					for (Pattern disjunct : ((PatternSet)p).getPatterns()) {
						if (isDeadPattern(disjunct)) {
							continue;
						}
						PlanNode dp = walker.plan(this,
								disjunct, HashSetFactory.make(availableVars),
								liveVars, SlowSparseNumberedGraph.<PlanNode> make());
						cost = (cost == null)? dp.cost: add(cost, dp.cost);
					}
					assert cost != null;
					return cost;
				}

				@Override
				public Set<Variable> getRequiredVariables() {
					return required;
				}

				@Override
				public Set<Variable> getProducedVariables() {
					return produced;
				}
			});
		}

		@Override
		void getApplicableNodesForSubselect(Pattern p,
				final Set<Variable> availableVars, final Set<Variable> liveVars, final Walker walker,
				List<Pattern> region, Set<Node> result) {
			final SubSelectPattern sub = (SubSelectPattern) p;

			final Map<Variable, Variable> vars = HashMapFactory.make();
			for (ProjectedVariable v : sub.gatherRealProjectedVariables()) {
				if (v.getExpression() != null) {
					if (v.getExpression().getType() == EExpressionType.VAR) {
						Variable inner = new Variable(
								((VariableExpression) v.getExpression())
										.getVariable());
						vars.put(v.getVariable(), inner);
					} else {
						vars.put(v.getVariable(), v.getVariable());
					}
				} else {
					vars.put(v.getVariable(), v.getVariable());
				}
			}

			// final Set<Variable> innerLive= HashSetFactory.make(liveVars);
			final Set<Variable> innerLive= HashSetFactory.make();
			boolean expr = sub.getSolutionModifier() != null;
			for (ProjectedVariable v : sub.gatherRealProjectedVariables()) {
				if (v.getExpression() == null) {
					innerLive.add(v.getVariable());
				} else {
					expr = true;
					innerLive.addAll(v.getExpression().gatherVariables());
				}
			}
			
			System.err.println("pattern: " + p);
			System.err.println("vars: " + uncomputable(p));
			
			if (! availableVars.containsAll(uncomputable(p))) {
				return;
			}
			
			final boolean noPassThrough = expr;
			
			result.add(new AbstractNode(sub) {
				private final Set<Variable> required;
				private final Set<Variable> produced;

				{
					required = HashSetFactory.make(availableVars);
					required.retainAll(vars.keySet());
					
					produced = HashSetFactory.make(vars.keySet());
					produced.removeAll(required);
				}

				@Override
				public PlanNode augmentPlan(Query q, PlanNode top, NumberedGraph<PlanNode> g, List<Expression> filters, Set<Variable> availableVars, Set<Variable> liveVars) {
					PlanNode ss = walker.plan(this, sub.getPattern(), HashSetFactory.make(availableVars), innerLive, g);
					
					Set<Variable> vars = HashSetFactory.make(availableVars);
					vars.addAll(ss.getProducedVariables());
					vars.retainAll(innerLive);
					if (noPassThrough) {
						vars.retainAll(sub.getAllPatternVariables());
					}
					
					PlanNode select = planFactory.createSTPlanNode(PlanNodeType.SUBSELECT, vars, sub);
					select.setFilters(filters);
					select.cost = ss.cost;

					select.setRequiredVariables(required);

					if (! ss.getAvailableVariables().containsAll(availableVars) && top != null) {
						Set<Variable> operatorVars = HashSetFactory.make(availableVars);
						operatorVars.retainAll(ss.getAvailableVariables());
						PlanNode join = planFactory.createSTPlanNode(PlanNodeType.JOIN, operatorVars, p);
						join.setCost(ss.cost);
						
						availableVars = HashSetFactory.make(availableVars);
						availableVars.addAll(ss.getAvailableVariables());
						join.setAvailableVariables(availableVars);
						select.setAvailableVariables(ss.getAvailableVariables());
						select.setProducedVariables(ss.getProducedVariables());

						g.addNode(ss);
						g.addNode(select);
						g.addEdge(select, ss);

						g.addNode(join);
						g.addEdge(join, top);
						g.addEdge(join, select);
					
						return join;

					} else {
					Set<Variable> producedVars = HashSetFactory.make(produced);
					producedVars.addAll(availableVars);
					producedVars.retainAll(liveVars);
					select.setProducedVariables(producedVars);
					vars.addAll(producedVars);
					vars.retainAll(liveVars);
					select.setAvailableVariables(vars);
					//select.setAvailableVariables(producedVars);
					
					g.addNode(ss);
					g.addNode(select);
					g.addEdge(select, ss);
					
					return join(JoinTypes.AND, q, g, top, select, liveVars);
					}
				}

				@Override
				public Kind kind() {
					return Kind.SUBSELECT;
				}

				@Override
				public Pair<Double, Double> getCost(NumberedGraph<PlanNode> g) {
					try {
					PlanNode dp = walker.plan(this,
							sub.getPattern(), HashSetFactory.make(vars.values()),
							innerLive, SlowSparseNumberedGraph.<PlanNode> make());
					List<ProjectedVariable> pv = sub.gatherRealProjectedVariables();
					for (ProjectedVariable v : pv) {
						if (v.getExpression() != null && v.getExpression() instanceof AggregateExpression) {
							return Pair.make(1.0, 1.0);
						}
					}
					return dp.cost;
					} catch (PlannerError e) {
						return Pair.make(Double.MAX_VALUE, Double.MAX_VALUE);
					}

				}

				@Override
				public Set<Variable> getRequiredVariables() {
					return required;
				}

				@Override
				public Set<Variable> getProducedVariables() {
					return produced;
				}
			});
		}
		
		@Override
		void getApplicableNodesForValues(Pattern p, Set<Variable> availableVars, Set<Variable> liveVars,
				Walker walker, List<Pattern> region, Set<Node> result) {
			final ValuesPattern vp = (ValuesPattern) p;
			Set<Variable> producedVariables = HashSetFactory.make();
			producedVariables.addAll(vp.getVariables());
			Set<Variable> required = vp.getValues().determineUNDEFVariables();
			if (availableVars.containsAll(required)) {
				result.add(new ValuesNode(vp, required, producedVariables));
			}
		}

		@Override
		void getApplicableNodesForBind(Pattern p, Set<Variable> availableVars, Set<Variable> liveVars,
				Walker walker, List<Pattern> region, Set<Node> result) {
			final BindPattern bp = (BindPattern) p;
			final Variable projectedVar = bp.getVar();
			Expression e = bp.getExpression();
			Set<Variable> producedVariables = HashSetFactory.make();
			Set<Variable> requiredVariables = HashSetFactory.make();
			producedVariables = Collections.singleton(projectedVar);
			requiredVariables = e.gatherVariables();
			

			Set<Variable> vars = new HashSet<Variable>();
			vars.addAll(requiredVariables);
			vars.addAll(producedVariables);
			
			// This is the case where all variables in the bind expression are already bound by other patterns (including the produced one)
			// this is a cheap expression to evaluate and produces nothing.  So the cost is minimal (a constant of 1.0).
			if (availableVars.containsAll(vars)) {
				result.add(new FilterBindExpressionNode(e, vars, Collections.<Variable>emptySet(), stats, bp));
			} else if (availableVars.containsAll(requiredVariables)) {
				// Standard bind expressions require a set of variables and produce another. Because this is an operation on variables
				// that already exist in the db, their cost is relatively cheap (cheaper than the cheapest cost) unless its an expensive
				// function call. Cost is encoded in an expression node because its true of all expression nodes
				result.add(new BindExpressionNode(e, requiredVariables,
							producedVariables, stats, bp));
			}
		}
		
		@Override
		void getApplicableNodesForBindFunc(Pattern p, Set<Variable> availableVars, Set<Variable> liveVars,
				Walker walker, List<Pattern> region, Set<Node> result) {
			final BindFunctionPattern bfunct = (BindFunctionPattern) p;
			BindFunctionNode node = new BindFunctionNode(bfunct, availableVars, liveVars);
			if (availableVars.containsAll(node.getRequiredVariables())) {
				result.add(node);
			}
		}

		@Override
		void getApplicableNodesForOptional(Pattern p, final Pattern op,
				Set<Variable> availableVars, final Set<Variable> liveVars, final Walker walker, List<Pattern> region, Set<Node> result) {
				final Set<Variable> relevantVars = getRelevantVars(op, availableVars);
				if (isDeadPattern(op) || !availableVars.containsAll(getDependentVariables(region, op))) {
					return;
				}
				
				result.add(new AbstractNode(op) {
					private final int id = ++graphCounterId;

					@Override
					public Kind kind() {
						return Kind.OPTIONAL;
					}

					@Override
					public Pair<Double, Double> getCost(NumberedGraph<PlanNode> g) {
						PlanNode p = walker.plan(this,
								op, HashSetFactory.make(relevantVars),
								liveVars, SlowSparseNumberedGraph.<PlanNode> make());
						assert p.cost.fst > 0.0;
						return p.cost;
					}

					@Override
					public Set<Variable> getRequiredVariables() {
						return relevantVars;
					}

					@Override
					public Set<Variable> getProducedVariables() {
						Set<Variable> vars = op.gatherVariables();
						vars.removeAll(relevantVars);
						return vars;
					}

					@Override
					public int getId() {
						return id;
					}

					@Override
					public PlanNode augmentPlan(Query q, PlanNode lhs, NumberedGraph<PlanNode> g, List<Expression> filters, Set<Variable> availableVars, Set<Variable> liveVars) {
						Set<Variable> liveForJoin = HashSetFactory.make(availableVars);
						liveForJoin.addAll(liveVars);

						Set<Variable> newAvail = HashSetFactory.make(availableVars);
						Set<Variable> vars = HashSetFactory.make(op.gatherVariablesWithOptional());
						vars.addAll(liveVars);
						newAvail.retainAll(vars);
						PlanNode rhs = walker.plan(this, op, newAvail, liveForJoin, g);
						
						return join(JoinTypes.LEFT, q, g, lhs, rhs, liveVars);
					}
				});
		}

		@Override
		void getApplicableNodesForService(Pattern p,
				Set<Variable> availableVars, Set<Variable> liveVariables,
				Walker walker, List<Pattern> region, Set<Node> result) {
			ServicePattern sp = (ServicePattern) p;
			Set<Variable> pvs = sp.gatherVariables();			
			result.add(new ServiceNode(sp, availableVars, liveVariables));
		}
		
		@Override
		void getApplicableNodesForScope(final Pattern scope,
				Set<Variable> availableVars, final Set<Variable> liveVariables,
				final Walker walker, List<Pattern> region, Set<Node> result) {
			final Set<Variable> conflicts = scope.potentialScopeClashes();
			assert !conflicts.isEmpty();
			
			final Set<Variable> usableVars = HashSetFactory.make(availableVars);
			usableVars.retainAll(conflicts);
			
			result.add(new AbstractNode(scope) {

				@Override
				public PlanNode augmentPlan(Query q, PlanNode currentHead,
						NumberedGraph<PlanNode> g, List<Expression> filters,
						Set<Variable> availableVars, Set<Variable> liveVars) {
					Set<Variable> usableVars = HashSetFactory.make(availableVars);
					usableVars.removeAll(conflicts);
					PlanNode p = walker.plan(this, scope, HashSetFactory.make(usableVars), liveVars, g);

					Set<Variable> operatorVars = HashSetFactory.make(availableVars);
					operatorVars.retainAll(p.getAvailableVariables());
					g.addNode(p);
					
					if (currentHead == null) {
						return p;
					} else {
						PlanNode join = planFactory.createSTPlanNode(PlanNodeType.JOIN, operatorVars, scope);
						
						availableVars = HashSetFactory.make(availableVars);
						availableVars.addAll(p.getAvailableVariables());
						join.setAvailableVariables(availableVars);
						
						g.addNode(join);
						g.addEdge(join, currentHead);
						g.addEdge(join, p);
					
						return join;
					}
				}

				@Override
				public Kind kind() {
					return Kind.JOIN;
				}

				@Override
				public Pair<Double, Double> getCost(NumberedGraph<PlanNode> g) {
					PlanNode p = walker.plan(this,
							scope, HashSetFactory.make(usableVars),
							liveVariables, SlowSparseNumberedGraph.<PlanNode> make());
					assert p.cost.fst > 0.0;
					return p.cost;
				}

				@Override
				public Set<Variable> getRequiredVariables() {
					return usableVars;
				}

				@Override
				public Set<Variable> getProducedVariables() {
					Set<Variable> vars = p.gatherVariablesWithOptional();
					vars.removeAll(usableVars);
					return vars;
				}
				
			});
		}

		private Map<Pair<QueryTripleTerm,BinaryUnion<Variable,IRI>>,Set<QueryTriple>> buildStarTripleMap(
				final List<Pattern> region, Set<Variable> availableVars,
				final boolean forward, final Map<QueryTriple, Pattern> parents, Set<Key> neededKeys) {
			Map<Pair<QueryTripleTerm,BinaryUnion<Variable,IRI>>,Set<QueryTriple>> stars = HashMapFactory.make();
			for(Pattern p : region) {
				if (p instanceof SimplePattern) {
					for(QueryTriple triple : ((SimplePattern)p).getQueryTriples()) {
						QueryTripleTerm idx = forward? triple.getSubject(): triple.getObject();
						if ((idx.isVariable() || idx.isIRI()) && 
								triple.getPredicate().isIRI() &&
								neededKeys.contains(triple) &&
								!(forward? forwardPreds: reversePreds).hasSpills(triple.getPredicate().getIRI().getValue()) &&
								(p.getGraphRestriction() != null || !defaultUnionGraph) &&
								(availableVars == null || 
								 !idx.isVariable() ||
								 availableVars.contains(idx.getVariable()) ||
								 p.getFilterBindings().containsKey(idx.getVariable())
							)) 
						{
							if (parents != null) {
								parents.put(triple, p);
							}
							
							Pair<QueryTripleTerm,BinaryUnion<Variable,IRI>> v = Pair.make(idx,p.getGraphRestriction());
							if (! stars.containsKey(v)) {
								stars.put(v, HashSetFactory.<QueryTriple>make());
							}
							stars.get(v).add(triple);	
						}
					}
				}
			}
			return stars;
		}

		private Set<IRI> getPredicates(Set<QueryTriple> star) {
			Set<IRI> result = HashSetFactory.make();
			for(QueryTriple t : star) {
				if (t.getPredicate().isIRI()) {
					result.add(t.getPredicate().getIRI());
				}
			}
			return result;
		}
		
		private Set<Node> gatherStars(final List<Pattern> region, Set<Variable> availableVars, final Set<Key> neededKeys, final boolean forward, final Walker walker) {
			final Map<QueryTriple,Pattern> parents = HashMapFactory.make();
			
			Map<Pair<QueryTripleTerm,BinaryUnion<Variable,IRI>>,Set<QueryTriple>> stars = buildStarTripleMap(region, availableVars, forward, parents, neededKeys);
			
			Set<Node> result = HashSetFactory.make();
			for(final Map.Entry<Pair<QueryTripleTerm,BinaryUnion<Variable,IRI>>, Set<QueryTriple>> star : stars.entrySet()) {
				if (star.getValue().size() > 1 && getPredicates(star.getValue()).size() > 1) {
					result.add(new Node() {
						private final int id = ++graphCounterId;

						private Set<Pattern> opts = null;
						{
							for(Pattern p : region) {
								if (p.getOptionalPatterns() != null) {
									patterns: for(Pattern o : p.getOptionalPatterns()) {
										if (neededKeys.contains(o)) {
											List<Pattern> optionalRegion = new LinkedList<Pattern>();
											walker.gatherRegion(o, optionalRegion);
											for(Pattern x : optionalRegion) {
												if (x.getType() == EPatternSetType.SIMPLE) {
													for(QueryTriple q : ((SimplePattern)x).getQueryTriples()) {
														QueryTripleTerm y = forward? q.getSubject(): q.getObject();
														if (! y.equals(star.getKey().fst)) {
															continue patterns;
														}
													}
												} else if (x != o) {
													continue patterns;
												} else if (x.getFilters() != null && ! x.getFilters().isEmpty()) {
													continue patterns;
												}
											}
											if (opts == null) {
												opts = HashSetFactory.make();
											}
											opts.add(o);
										}
									}
								}
							}
						}

						private void handleProduced(QueryTriple triple) {
							QueryTripleTerm val = star.getKey().fst;
							if (val.isVariable() && parents.containsKey(triple) && parents.get(triple).getFilterBindings().containsKey(val.getVariable())) {
								produced.add(val.getVariable());
							}
							if (forward) {
								if (triple.getObject().isVariable()) {
									produced.add(triple.getObject().getVariable());
								}									 
							} else {
								if (triple.getSubject().isVariable()) {
									produced.add(triple.getSubject().getVariable());
								}									 									
							}
						}
						
						private Set<Variable> produced = HashSetFactory.make();						
						{
							for(QueryTriple triple : star.getValue()) {
								handleProduced(triple);
							}
							if (opts != null) {
								for(Pattern opt : opts) {
									List<Pattern> optionalRegion = new LinkedList<Pattern>();
									walker.gatherRegion(opt, optionalRegion);
									for(Pattern x : optionalRegion) {
										if (x.getType() == EPatternSetType.SIMPLE) {
											for(QueryTriple t : ((SimplePattern)x).getQueryTriples()) {
												handleProduced(t);
											}
										}
									}
								}
							}
						}
									
						@Override
						public PlanNode augmentPlan(Query q, PlanNode lhs, NumberedGraph<PlanNode> g, List<Expression> filters, Set<Variable> availableVars, Set<Variable> liveVars) {
							QueryTripleTerm val = star.getKey().fst;
							Set<Variable> op = null;
							for(QueryTriple triple : star.getValue()) {
								if (op == null) {
									op = HashSetFactory.make(triple.gatherVariables());
								} else {
									op.retainAll(triple.gatherVariables());
								}
							}
							PlanNode starNode = planFactory.createSTPlanNode(
								PlanNodeType.STAR, op, region.iterator().next());
							
							starNode.cost = getCost(g);
							starNode.setFilters(filters);
							starNode.starTriples = star.getValue();
							com.ibm.research.rdf.store.schema.Pair<Double> cost = new com.ibm.research.rdf.store.schema.Pair<Double>(starNode.cost.fst, starNode.cost.snd);
							starNode.setAccessMethod(new AccessMethod(forward? AccessMethodType.DPH_INDEX_SUBJECT: AccessMethodType.RPH_INDEX_OBJECT, cost));
						
							if (opts != null) {
								starNode.starOptionalTriples = HashSetFactory.make();
								for(Pattern p : opts) {
									List<Pattern> optionalRegion = new LinkedList<Pattern>();
									walker.gatherRegion(p, optionalRegion);
									for(Pattern x : optionalRegion) {
										if (x != p) {
											starNode.starOptionalTriples.addAll(((SimplePattern)x).getQueryTriples());
										}
									}
								}
							}
							
							Set<Variable> filterVars = HashSetFactory.make(getProducedVariables());
							filterVars.retainAll(availableVars);
							
							Set<Variable> requiredVars = HashSetFactory.make(getRequiredVariables());
							requiredVars.addAll(filterVars);
							starNode.setRequiredVariables(requiredVars);
		
							Set<Variable> producedVars = HashSetFactory.make(produced);
							producedVars.removeAll(op);
							producedVars.removeAll(filterVars);
							if (getRequiredVariables().isEmpty() & val.isVariable()) {
								producedVars.add(val.getVariable());
							}
							starNode.setProducedVariables(producedVars);
							
							Set<Variable> vars = HashSetFactory.make(availableVars);
							vars.addAll(producedVars);
							vars.retainAll(liveVars);
							starNode.setAvailableVariables(vars);

							g.addNode(starNode);
								
							return join(JoinTypes.AND, q, g, lhs, starNode, liveVars);
						}

						@Override
						public Set<? extends Key> getKeys() {
							if (opts != null) {
								Set<Key> x = HashSetFactory.make();
								x.addAll(star.getValue());
								x.addAll(opts);
								return x;
							} else {
								return star.getValue();
							}
						}

						@Override
						public int getId() {
							return id;
						}

						@Override
						public Kind kind() {
							return Kind.STAR;
						}

						private int getAccessMethod() {
							return forward? QueryTripleNode.DPH: QueryTripleNode.RPH;
						}
						
						public Pair<Double, Double> getCost(NumberedGraph<PlanNode> g) {
							double c1 = Double.MAX_VALUE;
							double c2 = Double.MAX_VALUE;
							for(QueryTriple triple : star.getValue()) {
								Pair<Double,Double> d = QueryTripleNode.computeCost(
										getAccessMethod(),
										triple, 
										parents.get(triple).getFilterBindings(), 
										parents.get(triple).getGraphRestriction(), 
										stats);
								c1 = Math.min(c1, d.fst);
								c2 = Math.min(c2, d.snd);
							}
							
							c1 /= star.getValue().size();
							c2 /= star.getValue().size();
							
							return Pair.make(c1,c2);
						}

						@Override
						public Set<Variable> getRequiredVariables() {
							QueryTripleTerm val = star.getKey().fst;
							for(QueryTriple t : star.getValue()) {
								if (val.isVariable() && parents.get(t).getFilterBindings().containsKey(val.getVariable())) {
									return Collections.emptySet();
								}
							}
							
							if (val.isVariable()) {
								return Collections.singleton(val.getVariable());
							} else {
								return Collections.emptySet();
							}
						}

						@Override
						public Set<Variable> getProducedVariables() {
							return produced;
						}
						
					});
				}
			}
			
			return result;
		}

		
		@Override
		public Set<Node> getApplicableNodes(List<Pattern> region,
				Set<Variable> availableVars, Set<Variable> liveVars, Set<Key> neededKeys, Walker walker) {
			Set<Node> result = super.getApplicableNodes(region, availableVars, liveVars, neededKeys, walker);
		
			if (GATHER_STARS) {
				result.addAll(gatherStars(region, availableVars, neededKeys, true, walker));
				result.addAll(gatherStars(region, availableVars, neededKeys, false, walker));
			}
			
			return result;
		}
		
		
	}

	private static Set<Variable> getRelevantVars(final Pattern p,
			Set<Variable> availableVars) {
		Set<Variable> vs = p.gatherVariablesWithOptional();
		final Set<Variable> relevantVars = HashSetFactory.make(vs);
		relevantVars.retainAll(availableVars);
		return relevantVars;
	}

	private static Pair<Double, Double> add(Pair<Double, Double> l,
			Pair<Double, Double> r) {
		return Pair.make(l.fst + r.fst, l.snd + r.snd);
	}
	
	private void applyFilter(Expression e, Set<Variable> availableVars) {
		switch(e.getType()) {
		case AND: {
			LogicalExpression le = (LogicalExpression) e;
			for(Expression c : le.getComponents()) {
				applyFilter(c, availableVars);
			}
			break;
		}
		
		case RELATIONAL: {
			RelationalExpression re = (RelationalExpression) e;
			if (re.getOperator().equals(ERelationalOp.EQUAL)) {
				if (re.getLeft() instanceof VariableExpression && re.getRight() instanceof VariableExpression) {
					Variable lv = new Variable(((VariableExpression)re.getLeft()).getVariable());
					Variable rv = new Variable(((VariableExpression)re.getRight()).getVariable());
					if (availableVars.contains(rv) || availableVars.contains(lv)) {
						availableVars.add(lv);
						availableVars.add(rv);
					}
				}
			}
			break;
		}
		
		default:
			break;
		}
	}
	
	private void applyFilters(Pattern p, Set<Variable> availableVars) {
		for (Expression e : p.getFilters()) {
			applyFilter(e, availableVars);
		}
	}
	
	protected class GreedyPlanner implements RegionPlanner {
		private final ApplicableNodes applicableNodes;
		private final Query q;
		
		

		@Override
		public Set<Key> computeNeededKeys(List<Pattern> region) {
			return applicableNodes.computeNeededKeys(region);
		}

		private void debug(Collection<?> set) {
			for (Object o : set) {
				System.out.println("Element:" + o);
			}
		}


		@Override
		public PlanNode plan(Node parentNode, 
				Pattern topLevelPattern,
				List<Pattern> region, 
				Walker walker, 
				Set<Variable> availableVars, 
				Set<Variable> liveVariables, 
				final NumberedGraph<PlanNode> g) 
		{
			if (isDeadPattern(topLevelPattern)) {
				return null;
			}
			
			applyFilters(topLevelPattern, availableVars);
			
			if (topLevelPattern.gatherFilters() != null) {
				liveVariables = HashSetFactory.make(liveVariables);
				for (Expression e : topLevelPattern.gatherFilters()) {
					liveVariables.addAll(e.gatherVariables());
				}
			}

			List<Expression> filters = new ArrayList<Expression>();
			for(Pattern p : region) {
				for(Expression e : p.getFilters()) {
					if (!filters.contains(e)) {
						filters.add(e);
					}
				}
			}
			
			final Set<Key> neededKeys = computeNeededKeys(region);
			
//			System.out.println("Needed keys:" + neededKeys.size());
//			debug(neededKeys);
//			System.out.println("Region:" + region.size());
//			debug(region);
			
			Collection<ReUseNode> reUseNodes = null;
			
			if (availableVars == null || availableVars.isEmpty()) {
				reUseNodes = traverseGraphForReUse(neededKeys, g);
			}
			
			class Edge extends Pair<Node, Node> {
				private Edge(Node fst, Node snd) {
					super(fst, snd);
				}
			}

			class EdgeComparator implements Comparator<Edge> {
				private int countSuccessors(Node n) {
					int count = 0;
					Set<Variable> vars = n.getProducedVariables();
					for (Key p : neededKeys) {
						if (!Collections.disjoint(p.gatherVariables(), vars)) {
							count++;
						}
					}
					return count;
				}

				@Override
				public int compare(Edge o1, Edge o2) {
					int max = 1;
					int min = -1;
					if (REVERSE_COSTS) {
						max = -1;
						min = 1;
					}
					double o1Cost = o1.snd.getCost(g).fst;
					if (o1.fst != QueryTripleNode.fakeRoot) {
						o1Cost *= .75;
					}
					double o2Cost = o2.snd.getCost(g).fst;
					if (o2.fst != QueryTripleNode.fakeRoot) {
						o2Cost *= .75;
					}
					if (o1Cost > o2Cost) {
						return max;
					} else if (o1Cost < o2Cost) {
						return min;
					} else {
						// try ordering by predicate costs
						o1Cost = o1.snd.getCost(g).snd;
						o2Cost = o2.snd.getCost(g).snd;
						if (o1Cost > o2Cost) {
							return max;
						} else if (o1Cost < o2Cost) {
							return min;
						} else {
							// try ordering by num of successors
							int o1out = countSuccessors(o1.snd);
							int o2out = countSuccessors(o2.snd);
							if (o1out < o2out) {
								return max;
							} else if (o1out > o2out) {
								return min;
							} else {
								// all else is equal, order by ids
								if (o1.snd.getId() < o2.snd.getId()) {
									return max;
								} else if (o1.snd.getId() > o2.snd.getId()) {
									return min;
								} else if (o1.fst.getId() < o2.fst.getId()) {
									return max;
								} else if (o1.fst.getId() > o2.fst.getId()) {
									return min;
								} else {
									assert o1.equals(o2);
									return 0;
								}
							}
						}
					}
				}
			}

			Set<Variable> liveVars = computeLiveVariables(liveVariables, neededKeys);

			PlanNode top = null;

			SortedSet<Edge> orderedEdges = new TreeSet<Edge>(new EdgeComparator());
			for (Node n : getApplicableNodes(region, availableVars, liveVars, neededKeys, walker)) {
				orderedEdges.add(new Edge(QueryTripleNode.fakeRoot, n));
			}
		
			if (reUseNodes != null) {
				for (Node n : reUseNodes) {
					orderedEdges.add(new Edge(QueryTripleNode.fakeRoot, n));
				}
			}
			
			process: while (!neededKeys.isEmpty()) {
				applyFilters(topLevelPattern, availableVars);

				prune: for (Iterator<Edge> es = orderedEdges.iterator(); es
						.hasNext();) {
					Node s = es.next().snd;
					if (neededKeys.containsAll(s.getKeys())) {
						continue prune;
					}
					es.remove();
				}

				Edge savedProductEdge = null;
				Edge savedMinusEdge = null;
				
				edges: for (Iterator<Edge> es = orderedEdges.iterator(); es.hasNext();) {
					Edge e = es.next();
					if (neededKeys.containsAll(e.snd.getKeys())) {						

						if ((e.snd instanceof ReUseNode && top != null) || (e.snd.kind() == Kind.MINUS && top == null)) {
							continue edges;
						}
						
						if (CHECK_PRODUCT) {
							if (!availableVars.isEmpty()
								&& Collections.disjoint(availableVars,
											e.snd.getRequiredVariables())
											&& Collections.disjoint(availableVars,
													e.snd.getProducedVariables())) {
								if (es.hasNext()) {
									if (savedProductEdge == null) {
										savedProductEdge = e;
									} 
									continue;
								} else {
									if (savedProductEdge != null) {
										e = savedProductEdge;
										savedProductEdge = null;
									}
								}
							}
						}

						if (CHECK_MINUS) {
							Set<? extends Key> mine = e.snd.getKeys();
							keys: for(Key k : neededKeys) {
								if (k instanceof Pattern && 
								    ((Pattern)k).getType() == EPatternSetType.MINUS &&
									!mine.contains(k)) 
								{
									Set<Variable> myVars = e.snd.getProducedVariables();
									myVars.removeAll(availableVars);
									
									Set<Variable> minusVars = k.gatherVariables();
									
									myVars.removeAll(minusVars);
									myVars.retainAll(liveVariables);
									
									if (!myVars.isEmpty()) {
										if (es.hasNext()) {
											if (savedMinusEdge == null) {
												savedMinusEdge = e;
											}
											if (g == walker.topPlan()) {
												System.err.println("skipping " + e.snd);
												System.err.println(liveVariables);
											}
											continue edges;
										} else {
											if (savedMinusEdge != null) {
												e = savedMinusEdge;
												savedMinusEdge = null;
											}
											break keys;
										}
									}
								}
							}
						}
						
						neededKeys.removeAll(e.snd.getKeys());
						
						Set<Variable> oldVars = availableVars;
						availableVars = HashSetFactory.make(availableVars);
						availableVars.addAll(e.snd.getProducedVariables());
	
						liveVars = computeLiveVariables(liveVariables, neededKeys);
				
						if (g == walker.topPlan()) {
							//System.err.println("adding " + e + " available: " + availableVars + ", live: " + liveVars);
						}
				
						System.out.println(e + " has available vars:" + availableVars);
						for (Node s : getApplicableNodes(region, availableVars, liveVars, neededKeys, walker)) {
							if (neededKeys.containsAll(s.getKeys())) {
								orderedEdges.add(new Edge(e.snd, s));
							}
						}

						top = e.snd.augmentPlan(q, top, g, filters, oldVars, liveVars);
						
						continue process;
					}
					
				}
				
				if (g == walker.topPlan()) {
					System.err.println("fail");
				}
								
				throw new PlannerError("cannot make progress planning " + region + "\n"
								+ availableVars + " --> " + neededKeys + "\n" 
								+ "nodes: " + orderedEdges);
			}
			
			return top;
		}
		
	
		
		public Collection<ReUseNode> traverseGraphForReUse(Set<Key> neededKeys, Graph<PlanNode> g) {
			// check for empty graph
			Iterator<PlanNode> it = g.iterator();
			if (!it.hasNext()) {
				return null;
			}
			
			// find root
			Map<PlanNode, ReUseNode> nodesForReUse = HashMapFactory.make();

			Set<PlanNode> roots = HashSetFactory.make();
			while (it.hasNext()) {
				PlanNode n = it.next();
				if ((g.getPredNodes(n) == null || !g.getPredNodes(n).hasNext()) && n.getType() != PlanNodeType.REUSE) {
					roots.add(n);
				}
			}
			System.out.println("roots:" + roots);
			for (PlanNode n : roots) {
				traverse(neededKeys, g, nodesForReUse, n, new HashMap<Variable, Variable>());
				
			}
			System.out.println("Nodes for re-use");
			for (ReUseNode n: nodesForReUse.values()) {
				if (n.nodeToReUse.getType() == PlanNodeType.AND) {
					System.out.println("Can reuse:" + n);
				}
			}
			return nodesForReUse.values();
		}
		
		public void traverse(Set<Key> neededKeys, Graph<PlanNode> g, Map<PlanNode, ReUseNode> nodesForReUse, PlanNode node, Map<Variable, Variable> accumulatedMappings) {
				
			boolean canReUse = false;

			if (node.getType() == PlanNodeType.STAR) {
				canReUse = reuseStarNode(neededKeys, nodesForReUse, node, accumulatedMappings);
			} else if (node.getType() == PlanNodeType.TRIPLE) {
				canReUse = reuseTripleNode(node.getTriple(), neededKeys, nodesForReUse, node,
						accumulatedMappings);  
				return;
			} else if (node.getType() == PlanNodeType.AND) {
				canReUse = reuseAnd(node, g, nodesForReUse, neededKeys, accumulatedMappings);
				return;
			} 
			
			Iterator<PlanNode> it = g.getSuccNodes(node);
			
			while (it.hasNext()) {
				PlanNode n = it.next();
				traverse(neededKeys, g, nodesForReUse, n, accumulatedMappings);
				
			}
						
		}
		
		private boolean reuseStarNode(Set<Key> neededKeys,
				Map<PlanNode, ReUseNode> nodesForReUse, PlanNode node,
				Map<Variable, Variable> accumulatedMappings) {
			if (! reuseCheckFilters(node)) {			
				return false;
			}

			assert node.starTriples != null;
			
			Map<Variable, Variable> accMappingsCopy = new HashMap<Variable, Variable>(accumulatedMappings);
			Set<Variable> allVars = node.getAvailableVariables();
			Set<Key> keys = HashSetFactory.make();			
			for (Key k : neededKeys) {
				if (k instanceof QueryTriple) {
					QueryTriple qtk = (QueryTriple) k;
					triples: for(QueryTriple triple : node.starTriples) {
						if (triple.isSimilarTo(qtk) && triple.hasSimilarGraphRestrictionTo(qtk)) {		
							Map<Variable, Variable> variableMappings = triple.getVariableMappings(qtk);
							if (!addGraphRestriction(variableMappings, triple, qtk)) {
								continue triples;
							}
						
							if (variableMappings != null) {
								for (Map.Entry<Variable, Variable> entry : variableMappings.entrySet()) {
									// check if any of the triple's variable-to-variable mappings violate any existing mappings so far
									if (accMappingsCopy.containsKey(entry.getKey()) && !accMappingsCopy.get(entry.getKey()).equals(entry.getValue())) {
										continue triples;
									}								
								}
							
								keys.add(k);
								accMappingsCopy.putAll(variableMappings);
							}
						}
					}
				}
			}
			
			if (accMappingsCopy.keySet().containsAll(node.getAvailableVariables())) {
				nodesForReUse.put(node, new ReUseNode(node, accMappingsCopy, keys));
				accumulatedMappings.putAll(accMappingsCopy);
				return true;
			} else {
				return false;
			}
		}

		private boolean addGraphRestriction(Map<Variable, Variable> variableMappings, QueryTriple old, QueryTriple nu) {
			if (old.getGraphRestriction() == null || nu.getGraphRestriction() == null) {
				return true;
			}
			if (old.getGraphRestriction().isFirstType() && nu.getGraphRestriction().isFirstType()) {
				if (!variableMappings.containsKey(old.getGraphRestriction().getFirst())) {
					variableMappings.put(old.getGraphRestriction().getFirst(), nu.getGraphRestriction().getFirst());
				} else {
					return false;
				}
			}
			return true;
		}
		
		private boolean reuseCheckFilters(PlanNode node) {
			/*
			 * plain EXISTS filters get handled with additional CTEs, so they are not a problem 
			 * since there will be CTEs that correspond to the unfiltered nodes.
			 */
			if (!node.getFilters().isEmpty()) {
				for(Expression e : node.getFilters()) {
					if (! (e.getType().equals(EExpressionType.BUILTIN_FUNC) && ((BuiltinFunctionExpression)e).isExists())) {
						return false;
					}
				}
			}
			return true;
		}
		
		private boolean reuseTripleNode(QueryTriple triple, Set<Key> neededKeys,
				Map<PlanNode, ReUseNode> nodesForReUse, PlanNode node,
				Map<Variable, Variable> accumulatedMappings) {
			
			if (! reuseCheckFilters(node)) {			
				return false;
			}
			
			keys: for (Key k : neededKeys) {
				if (k instanceof QueryTriple) {
					QueryTriple qtk = (QueryTriple) k;
					if (triple.isSimilarTo(qtk) && triple.hasSimilarGraphRestrictionTo(qtk)) {		
						Map<Variable, Variable> variableMappings = triple.getVariableMappings(qtk);
						if (!addGraphRestriction(variableMappings, triple, qtk)) {
							continue keys;
						}
						boolean hasConsistentMappings = true;
						Map<Variable, Variable> accMappingsCopy = new HashMap<Variable, Variable>(accumulatedMappings);
						
						if (variableMappings != null && hasConsistentMappings) {
							for (Map.Entry<Variable, Variable> entry : variableMappings.entrySet()) {
								// check if any of the triple's variable-to-variable mappings violate any existing mappings so far
								if (accMappingsCopy.containsKey(entry.getKey()) && !accMappingsCopy.get(entry.getKey()).equals(entry.getValue())) {
									continue keys;
								} else {
									accMappingsCopy.put(entry.getKey(), entry.getValue());
								}
							}
							if (hasConsistentMappings && accMappingsCopy.keySet().containsAll(node.getAvailableVariables())) {
								accumulatedMappings.putAll(variableMappings);
								nodesForReUse.put(node, new ReUseNode(node, accumulatedMappings, Collections.singleton(k)));
								return true;
							}			
						}							
					}
				}
			}
			return false;
		}
		
		private boolean reuseAnd(PlanNode node, Graph<PlanNode> g, Map<PlanNode, ReUseNode> nodesForReUse, Set<Key> neededKeys, Map<Variable, Variable> accumulatedMappings) {
			Iterator<PlanNode> succs = g.getSuccNodes(node);
			PlanNode left = succs.next();
			PlanNode right = succs.next();
			
			// check if we have the right types of successors, and if we can re-use this AND potentially
			if (! (left.getType() == PlanNodeType.AND || left.getType() == PlanNodeType.STAR || left.getType() == PlanNodeType.TRIPLE)) {
				return false;
			}
			
			if (!left.getRequiredVariables().isEmpty() || !node.getRequiredVariables().isEmpty()) {
				return false;
			}
			
			boolean reuseLeft = false;
			// reuse the code
			if (left.getType() == PlanNodeType.STAR) {
				reuseLeft = reuseStarNode(neededKeys, nodesForReUse, left, accumulatedMappings);
			} else if (left.getType() == PlanNodeType.AND) {
				reuseLeft = reuseAnd(left, g, nodesForReUse, neededKeys, accumulatedMappings);
			} else if (left.getType() == PlanNodeType.TRIPLE) {
				reuseLeft = reuseTripleNode(left.getTriple(), neededKeys, nodesForReUse, left, accumulatedMappings);

			}
			
			if (!reuseLeft) {
				return false;
			}
			

			boolean reuseRight = false;
			
			if (right.getType() == PlanNodeType.TRIPLE) {
				reuseRight = reuseTripleNode(right.getTriple(), neededKeys, nodesForReUse, right, accumulatedMappings);
			} else if (right.getType() == PlanNodeType.STAR) {
				reuseRight = reuseStarNode(neededKeys, nodesForReUse, right, accumulatedMappings);
			} else if (right.getType() == PlanNodeType.AND) {
				reuseRight = reuseAnd(right, g, nodesForReUse, neededKeys, accumulatedMappings);
			}
			

			if (!reuseRight) {
				return false;
			}
			

			// check all successors transitively, ensuring that every triple in the AND list respects the variable mappings at the level of each query triple
			// add keys
			List<PlanNode> successors = new LinkedList<PlanNode>();
			Set<Key> succKeys = new HashSet<Key>();

			getAndSuccessors(g, node, successors);
			

			for (PlanNode n : successors) {
				if (n == node) {
					continue;
				}
				assert n.getType() == PlanNodeType.TRIPLE || n.getType() == PlanNodeType.AND || n.getType() == PlanNodeType.STAR;


				assert nodesForReUse.containsKey(n) : "cant reuse node:" + node + " because " + n  + " n is not found ";
				ReUseNode rn = nodesForReUse.get(n);

				if (n.getType() == PlanNodeType.TRIPLE) {
					QueryTriple qt = n.getTriple();
					assert rn.getKeys().size() == 1;
					Key k = rn.getKeys().iterator().next();
					
					assert k instanceof QueryTriple;
					QueryTriple other = (QueryTriple) k;
					if (!qt.isEquivalentTo(other, accumulatedMappings)) {
						return false;
					}
				} 
				
				succKeys.addAll(rn.getKeys());
			}
		
			nodesForReUse.put(node, new ReUseNode(node, accumulatedMappings, succKeys));			
			return true;
		}

		
		// gather all successors of this AND, so we can get the set of keys for this node
		private void getAndSuccessors(Graph<PlanNode> g, PlanNode n, List<PlanNode> successors) {
			Iterator<PlanNode> succs = g.getSuccNodes(n);
			if (succs.hasNext()) {
				PlanNode left = succs.next();
				PlanNode right = succs.next();
	
				getAndSuccessors(g, left , successors);
				getAndSuccessors(g, right, successors);
			}
			
			if (n.getType() == PlanNodeType.AND || n.getType() == PlanNodeType.STAR || n.getType() == PlanNodeType.TRIPLE) {
				successors.add(n);
			}
		}

		@Override
		public Set<Node> getApplicableNodes(final List<Pattern> region,
				Set<Variable> availableVars, Set<Variable> liveVars, Set<Key> neededKeys, final Walker walker) {
			return applicableNodes.getApplicableNodes(region, availableVars, liveVars, neededKeys, walker);
		}

		public GreedyPlanner(Query q, ApplicableNodes applicableNodes) {
			this.applicableNodes = applicableNodes;
			this.q = q;
		}
	}

	protected class RecursiveWalker implements Walker {
		private final RegionPlanner planner;
		private final Plan topPlan;
		
		public RecursiveWalker(RegionPlanner planner) {
			this.planner = planner;
			this.topPlan = new Plan();
		}

		public Graph<PlanNode> topPlan() {
			return topPlan.getPlanTree();
		}
		
		@Override
		public Plan plan(Query q) {
			Set<Variable> projectedVars = HashSetFactory.make();
			setVars: {
				if (q.isSelectQuery()) {					
					List<ProjectedVariable> vars = q.getSelectQuery().getSelectClause().getProjectedVariables();
					
					if (q.getSelectQuery().getSolutionModifier() != null) {
						projectedVars.addAll(q.getSelectQuery().getSolutionModifier().gatherVariables());
					}
					
					checkVars: {
						if (! vars.isEmpty()) {
							for(ProjectedVariable var : vars) {
								if (var.getExpression() != null) {
									break checkVars;
								} else {
									projectedVars.add(var.getVariable());
								}
							}
							
							break setVars;
						}
					}
				}
				
				projectedVars.addAll(q.getMainPattern().gatherVariablesWithOptional());
			}
		
			// System.err.println("projected vars are "+ projectedVars);
			
			Pattern topPattern = q.getMainPattern();
			NumberedGraph<PlanNode> g = topPlan.getPlanTree();
			PlanNode root = plan(null, topPattern, HashSetFactory.<Variable> make(), projectedVars, g);
			topPlan.planTreeRoot = root;
			topPlan.setPattern(topPattern);
			topPlan.findProductsInPlan();
			return topPlan;
		}

		@Override
		public PlanNode plan(Node parentNode, final Pattern p, Set<Variable> availableVars, Set<Variable> liveVariables, NumberedGraph<PlanNode> g) {
			List<Pattern> region = new ArrayList<Pattern>();
			gatherRegion(p, region);

			// System.err.println("planning region " + region);
			
			return planner.plan(parentNode, p, region, this, availableVars, liveVariables, g);
		}
		@Override
		public void gatherRegion(Pattern p, List<Pattern> region) {
			gatherRegion(p, region, HashSetFactory.<BinaryUnion<Variable, IRI>>make(), true);
		}
		
		public void gatherRegion(Pattern p, List<Pattern> region, Set<BinaryUnion<Variable, IRI>> graphRestrictions, boolean top) {
			switch (p.getType()) {
			case AND:
				region.add(p);

				// code to handle  graph restriction on 
				// an AndPattern without consisting exclusively of 
				// nodes on which graph restrictions are not applicables
				// (e.g., BIND or SUBSELECT)
				if (p.getGraphRestriction()!=null) {
					boolean allChildrenWithoutGraphRestrictions = true;
					for (Pattern cp : p.getSubPatterns(false)) {
						if (cp.getGraphRestriction()!=null) {
							//assert cp.getGraphRestriction().equals(p.getGraphRestriction())
							//	: cp.getGraphRestriction() +"\n"+p.getGraphRestriction();
							if (!cp.getType().equals(EPatternSetType.SIMPLE)
							||  !((SimplePattern)cp).getQueryTriples().isEmpty()) {
								allChildrenWithoutGraphRestrictions = false;
								break;
							} else {
								// empty simple pattern: do nothing
							}
						}
					}
					if (allChildrenWithoutGraphRestrictions) {
						GraphRestrictionPattern grp = new GraphRestrictionPattern(p.getGraphRestriction());
						// explicitly add a graph  look up key
						if (graphRestrictions.add(grp.getGraphRestriction())) {
							region.add(grp);
						}
					}
					
				}

				//
				if (p.getGroup() == p) {
				// System.err.println(p + " clashes " + p.potentialScopeClashes());
				}
				if (top || p.getGroup() != p || p.potentialScopeClashes().isEmpty()) {
					for (Pattern c : ((PatternSet) p).getPatterns()) {
						gatherRegion(c, region, graphRestrictions, false);
					}
				}
				region.addAll(getNeededBuiltInExpressionsPatterns(p));
				break;
				
			default:
				region.add(p);
				region.addAll(getNeededBuiltInExpressionsPatterns(p));
			}
		}

	}

	public Plan plan(Query q, Store store, SPARQLOptimizerStatistics stats) {
		boolean defaultUnionGraph = Boolean.TRUE.equals(store.getContext().get(Context.unionDefaultGraph)); 
		reversePreds = store.getReversePredicates();
		forwardPreds = store.getDirectPredicates();
		
		Plan p = plan(q, new StandardApplicableNodes(defaultUnionGraph, stats));
		System.out.println("Planner has query:" + q);

		if (TEST_PLANNER) {

			Iterator<PlanNode> it = p.getPlanTree().iterator();
			boolean hasScan = false;
			while (it.hasNext()) {
				PlanNode n = it.next();
				AccessMethod m = n.getMethod();
				if (m == null) {
					continue;
				}
				if (m.getType().equals(AccessMethodType.DPH_SCAN)
						|| m.getType().equals(AccessMethodType.RPH_SCAN)) {
					hasScan = true;
				}
			}
			if (hasScan) {
				System.out.println("Plan has a scan");
				System.out.println("Plan:" + p);
			}
		}
		
		TypeInference typeInf = new TypeInference(p, store, q);
		System.out.println("After type inference:" + p);
		
		return p;
	}
	
	public Plan plan(Query q, ApplicableNodes applicableNodes) {
		return new RecursiveWalker(
				new GreedyPlanner(q, applicableNodes)).plan(q);
	}
	

	public enum JoinTypes {
		LEFT {
			@Override
			protected PlanNodeType type() {
				return PlanNodeType.LEFT;
			}
		}, EXISTS {

			@Override
			protected PlanNodeType type() {
				return PlanNodeType.EXISTS;
			}

		}, AND {
			@Override
			protected PlanNodeType type() {
				return PlanNodeType.AND;
			}	
		}, PRODUCT {
			@Override
			protected PlanNodeType type() {
				return PlanNodeType.PRODUCT;
			}	
		}, NOT_EXISTS {
			@Override
			protected PlanNodeType type() {
				return PlanNodeType.NOT_EXISTS;
			}	
		};
		
		protected abstract PlanNodeType type();
	}

	public PlanNode join(JoinTypes type, Query query, Graph<PlanNode> plan, PlanNode lhs, PlanNode rhs, Set<Variable> liveVars) {
		plan.addNode(rhs);
		if (lhs == null) {
			return rhs;
		} else if (type.type() == PlanNodeType.AND && !BINARY_PLANS && lhs.getType() == PlanNodeType.AND) {
			plan.addEdge(lhs, rhs);
			return lhs;
		} else {
			Set<Variable> operator = HashSetFactory.make(lhs.getAvailableVariables());
			Set<Variable> x = HashSetFactory.make(rhs.getRequiredVariables());
			x.addAll(rhs.getProducedVariables());
			operator.retainAll(x);
						
			PlanNode and = planFactory.createSTPlanNode(
					type.type(),
					Collections.<Variable> emptySet(),
					query.getMainPattern());
			Set<Variable> availableVariables; 
			if (lhs.getAvailableVariables() == null) {
				availableVariables = HashSetFactory.make();
			} else {
				availableVariables = HashSetFactory.make(lhs.getAvailableVariables());
			}
			if (rhs.getProducedVariables() != null && !type.equals(JoinTypes.NOT_EXISTS) && !type.equals(JoinTypes.EXISTS)) {
				availableVariables.addAll(rhs.getProducedVariables());
			}
			Set<Variable> allVars = HashSetFactory.make(liveVars);
			availableVariables.retainAll(allVars);
			
			and.setProducedVariables(HashSetFactory.make(availableVariables));
			and.setAvailableVariables(HashSetFactory.make(availableVariables));
			
			//assert !operator.isEmpty();
			and.setOperatorsVariables(operator);
										
			plan.addNode(and);
			plan.addEdge(and, lhs);
			plan.addEdge(and, rhs);
			
			and.cost = add(lhs.cost, rhs.cost);

			if (lhs.getFilters() != null || rhs.getFilters() != null) {
				List<Expression> filters = lhs.getFilters()!=null? new LinkedList<Expression>(lhs.getFilters()): new LinkedList<Expression>();
				if (type.type() == PlanNodeType.AND && rhs.getFilters() != null) {
					filters.addAll(rhs.getFilters());
				}
				and.setFilters(filters);
			}
			
			return and;
		}
	}
	
	protected Set<Pattern> getNeededBuiltInExpressionsPatterns(Pattern p) {
		Set<Pattern> keys = HashSetFactory.make();
		// gather any complex expressions in the filters that need planning
		// e.g., graph patterns buried in the not exists or exists func calls
		Set<BuiltinFunctionExpression> exps = p.getBuiltInExpressionsForPlanning();

		if (!exps.isEmpty()) {
			for (BuiltinFunctionExpression e : exps) {
				EBuiltinType type = e.getBuiltinType();
				EPatternSetType pt = type == EBuiltinType.EXISTS ? EPatternSetType.EXISTS : EPatternSetType.NOT_EXISTS;
				FilterNotExistsPattern filterNode = new FilterNotExistsPattern(pt);
				filterNode.addPattern(p);
				filterNode.addPattern(e.getPatternArguments());
				keys.add(filterNode);
			}
		}
		return keys;
	}
	private boolean isDeadPattern(Pattern p) {
		
		if (p.getFilterBindings() != null) {
			for(Expression e : p.getFilters()) {
				if (e instanceof ConstantExpression && Boolean.FALSE.equals(((ConstantExpression)e).getConstant().getBoolean())) {
					return true;
				}
			}
		}
		
		switch (p.getType()) {
		
		case PRODUCT:
		case AND: {
			for (Pattern conjunct : ((PatternSet)p).getPatterns()) {
				if (isDeadPattern(conjunct)) {
					return true;
				}
			}
			return false;
		}
		
		case SIMPLE: {
			for(QueryTriple t : ((SimplePattern)p).getQueryTriples()) {
				if (t.getPredicate().isIRI() &&
					(forwardPreds.getHashes(t.getPredicate().getIRI().getValue()) == null ||
					reversePreds.getHashes(t.getPredicate().getIRI().getValue()) == null)) {
					System.out.println("WARNING: " + t.getPredicate().getIRI().getValue() + " does not appear in the database");
					return true;
				}
			}
			return false;
		}

		case UNION: {
			for (Pattern conjunct : ((PatternSet)p).getPatterns()) {
				if (! isDeadPattern(conjunct)) {
					return false;
				}
			}
			return true;
		}

		case EXISTS:
		case MINUS:
		case NOT_EXISTS:
		case SUBSELECT:
		case OPTIONAL:
		case BIND:
		case GRAPH:
			return false;

		default:
			return false;
		}
	}

	private Set<Variable> uncomputable(Pattern p) {
		Set<Variable> x = HashSetFactory.make(p.gatherVariables());
		x.removeAll(computable(p));
		return x;
	}
	
	private Set<Variable> computable(Pattern p) {
		Set<Variable> result = HashSetFactory.make();
		switch (p.getType()) {

		case VALUES:
		case PRODUCT:
		case SIMPLE: {
			result.addAll(p.getVariables());
			break;
		}

		case UNION:
		case AND: {
			PatternSet ap = (PatternSet)p;
			for(Pattern sp : ap.getSubPatterns(false)) {
				result.addAll(computable(sp));
			}
		}

		case SERVICE: {
			result.addAll(p.getVariables());
			break;
		}

		case SUBSELECT: {
			SubSelectPattern sp = (SubSelectPattern)p;
			result.addAll(sp.gatherProjectedVariables());
			for(Pattern cp : sp.getSubPatterns(false)) {
				result.addAll(computable(cp));
			}
			break;
		}

		case BIND: {
			BindPattern bp = (BindPattern)p;
			result.add(bp.getVar());
			break;
		}
		
		case BINDFUNC:{
			BindFunctionPattern bp = (BindFunctionPattern)p;
			result.addAll(bp.getVars());
			break;
		}

		case GRAPH:
		case EXISTS:
		case MINUS:
		case NOT_EXISTS: 
		case OPTIONAL: {
			break; 
		}
		}
		
		return result;
	}
}
