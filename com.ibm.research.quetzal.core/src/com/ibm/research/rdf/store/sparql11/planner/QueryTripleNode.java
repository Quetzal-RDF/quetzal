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

import java.math.BigInteger;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.research.rdf.store.sparql11.model.BinaryUnion;
import com.ibm.research.rdf.store.sparql11.model.Constant;
import com.ibm.research.rdf.store.sparql11.model.Expression;
import com.ibm.research.rdf.store.sparql11.model.IRI;
import com.ibm.research.rdf.store.sparql11.model.Pattern;
import com.ibm.research.rdf.store.sparql11.model.PropertyTerm;
import com.ibm.research.rdf.store.sparql11.model.Query;
import com.ibm.research.rdf.store.sparql11.model.QueryTriple;
import com.ibm.research.rdf.store.sparql11.model.QueryTripleTerm;
import com.ibm.research.rdf.store.sparql11.model.Variable;
import com.ibm.research.rdf.store.sparql11.planner.Planner.Key;
import com.ibm.research.rdf.store.sparql11.planner.Planner.Kind;
import com.ibm.research.rdf.store.sparql11.planner.statistics.SPARQLOptimizerStatistics;
import com.ibm.wala.util.collections.HashSetFactory;
import com.ibm.wala.util.collections.Pair;
import com.ibm.wala.util.graph.NumberedGraph;

public class QueryTripleNode {

	public static final int DPH = 1;
	public static final int RPH = 2;
	public static final int FILTER_DPH = 3;
	public static final int FILTER_RPH = 4;
	public static final int GDPH = 5;
	public static final int SCAN = 6;

	public static final int  COSTMODEL_DB2RDF = 1;
	public static final int  COSTMODEL_JENA = 2;
	public static final int  COSTMODEL_VIRTUOSO = 3;
	
	public static  int costModelType = COSTMODEL_DB2RDF;
	
	private final static double PROP_PATH_COST_MULTIPLYING_FACTOR_INDEX_LOOK_UP = 10;
	private final static double PROP_PATH_COST_MULTIPLYING_FACTOR_POLL_LOOK_UP = 100;
	private final static double PROP_PATH_COST_MULTIPLYING_FACTOR_SCAN_LOOK_UP = 1000;

	
	final QueryTriple queryTriple;
	final Pattern pattern;
	final int accessMethod;
	final Set<Variable> producedVariables = new HashSet<Variable>();
	final Set<Variable> requiredVariables = new HashSet<Variable>();
	protected Map<Variable, Set<Constant>> filterBindings;
	final Pair<Double, Double> cost;
	final int id;
	

	private QueryTripleNode() {
		queryTriple = null;
		accessMethod = -1;
		pattern = null;
		cost = Pair.make(Double.MAX_VALUE, Double.MAX_VALUE);
		id = 0;
	}

	private static class FakeRoot extends QueryTripleNode implements Planner.Node {
		public String toString() {
			return "root";
		}

		@Override
		public PlanNode augmentPlan(Query q, PlanNode lhs, NumberedGraph<PlanNode> g, List<Expression> filters, Set<Variable> availableVars, Set<Variable> liveVars) {
			assert false;
			return null;
		}
		@Override
		public Set<? extends Key> getKeys() {
			return Collections.singleton(getKey());
		}
	}
	
	public static QueryTripleNode.FakeRoot fakeRoot = new FakeRoot();

	public QueryTripleNode(int accessMethod, QueryTriple queryTriple,
			Pattern p, SPARQLOptimizerStatistics stats, int id) {
		this.accessMethod = accessMethod;
		this.queryTriple = queryTriple;
		this.pattern = p;
		this.id = id;
		this.filterBindings = p.getFilterBindings();
		this.cost = computeCost(accessMethod, queryTriple, pattern
				.getFilterBindings(), pattern.getGraphRestriction(), stats);
		

		switch (accessMethod) {
		case FILTER_RPH:
		case FILTER_DPH: {
			if (isNotBoundVariable(queryTriple.getSubject(), filterBindings)) {
				requiredVariables.add(queryTriple.getSubject()
						.getVariable());
			// it is not required, yet it is a variable.  we must be producing
			// it somehow, most likely through some kind of filter constant 
			} else if (queryTriple.getSubject().isVariable()) {
				producedVariables.add(queryTriple.getSubject().getVariable());
			}
			
			if (isNotBoundVariable(queryTriple.getObject(), filterBindings)) {
				requiredVariables.add(queryTriple.getObject()
						.getVariable());
			// it is not required, yet it is a variable.  we must be producing
			// it somehow, most likely through some kind of filter constant 
			} else if (queryTriple.getObject().isVariable()) {
				producedVariables.add(queryTriple.getObject().getVariable());
			}

			if (queryTriple.getPredicate().isVariable()) {
				producedVariables.add(queryTriple.getPredicate()
						.getVariable());
			}
			if (p.getGraphRestriction() != null
					&& p.getGraphRestriction().getFirst() != null) {
				producedVariables.add(p.getGraphRestriction().getFirst());
			}
			producedVariables.removeAll(requiredVariables);
			break;				
		}
		case DPH: {
			if (isNotBoundVariable(queryTriple.getSubject(), filterBindings)) {
				requiredVariables.add(queryTriple.getSubject()
						.getVariable());
			// it is not required, yet it is a variable.  we must be producing
			// it somehow, most likely through some kind of filter constant 
			} else if (queryTriple.getSubject().isVariable()) {
				producedVariables.add(queryTriple.getSubject().getVariable());
			}

			if (queryTriple.getObject().isVariable()) {
				producedVariables
						.add(queryTriple.getObject().getVariable());
			}
			if (queryTriple.getPredicate().isVariable()) {
				producedVariables.add(queryTriple.getPredicate()
						.getVariable());
			}
			if (p.getGraphRestriction() != null
					&& p.getGraphRestriction().getFirst() != null) {
				producedVariables.add(p.getGraphRestriction().getFirst());
			}
			producedVariables.removeAll(requiredVariables);
			break;
		}
		case RPH: {
			if (queryTriple.getSubject().isVariable()) {
				producedVariables.add(queryTriple.getSubject()
						.getVariable());
			}
			if (isNotBoundVariable(queryTriple.getObject(), filterBindings)) {
				requiredVariables
						.add(queryTriple.getObject().getVariable());
			// it is not required, yet it is a variable.  we must be producing
			// it somehow, most likely through some kind of filter constant 
			} else if (queryTriple.getObject().isVariable()) {
				producedVariables.add(queryTriple.getObject().getVariable());
			}
			
			if (queryTriple.getPredicate().isVariable()) {
				producedVariables.add(queryTriple.getPredicate()
						.getVariable());
			}
			if (p.getGraphRestriction() != null
					&& p.getGraphRestriction().getFirst() != null) {
				producedVariables.add(p.getGraphRestriction().getFirst());
			}
			producedVariables.removeAll(requiredVariables);
			break;
		}
		case GDPH: {
			if (p.getGraphRestriction() != null) {

				if (queryTriple.getSubject().isVariable()) {
					producedVariables.add(queryTriple.getSubject()
							.getVariable());
				}
				if (queryTriple.getObject().isVariable()) {
					producedVariables.add(queryTriple.getObject()
							.getVariable());
				}
				if (queryTriple.getPredicate().isVariable()) {
					producedVariables.add(queryTriple.getPredicate()
							.getVariable());
				}
				if (p.getGraphRestriction() != null
						&& p.getGraphRestriction().getFirst() != null) {
					requiredVariables.add(p.getGraphRestriction()
							.getFirst());
				}
			} else {
				throw new RuntimeException(
						"GDPH without graph restriction " + p);
			}
			producedVariables.removeAll(requiredVariables);
			break;
		}
		case SCAN: {
			if (queryTriple.getSubject().isVariable()) {
				producedVariables.add(queryTriple.getSubject()
						.getVariable());
			}
			if (queryTriple.getObject().isVariable()) {
				producedVariables
						.add(queryTriple.getObject().getVariable());
			}
			if (queryTriple.getPredicate().isVariable()) {
				producedVariables.add(queryTriple.getPredicate()
						.getVariable());
			}
			if (p.getGraphRestriction() != null
					&& p.getGraphRestriction().getFirst() != null) {
				producedVariables.add(p.getGraphRestriction().getFirst());
			}
			producedVariables.removeAll(requiredVariables);
			break;
		}
		default:
			throw new RuntimeException("Unexpected access method "
					+ accessMethod);
		}
	}


	private static AccessMethodType getSTAccessMethodForPropPath(QueryTriple triple,
			int accessMethod, Map<Variable, Set<Constant>> filterBindings) {
		AccessMethodType ret;
		switch (accessMethod) {
		case QueryTripleNode.FILTER_DPH:
		case QueryTripleNode.DPH: {
			if (QueryTripleNode.isNotBoundVariable(triple.getSubject(), filterBindings)) { // subject
				// is a
				// variable
				ret =AccessMethodType.DPH_POLL_SUBJECT;
			} else if (filterBindings.containsKey(
					triple.getSubject().getVariable())) {
				ret = AccessMethodType.DPH_INDEX_SUBJECT;
			} else {
				assert (!triple.getSubject().isVariable());
				ret = AccessMethodType.DPH_INDEX_SUBJECT;
			}
			break;
		}
		case QueryTripleNode.FILTER_RPH:
		case QueryTripleNode.RPH: {
			if (QueryTripleNode.isNotBoundVariable(triple.getObject(), filterBindings)) { // object
																		// is
				// a
				// variable
				ret = AccessMethodType.RPH_POLL_OBJECT;
			} else if (filterBindings.containsKey(
					triple.getObject().getVariable())) {
				ret = AccessMethodType.RPH_INDEX_OBJECT;
			} else {
				assert (!triple.getObject().isVariable());
				ret = AccessMethodType.RPH_INDEX_OBJECT;
			}
			break;
		}
		case QueryTripleNode.GDPH: {
			ret = null; // graph access not supported for proppath
			break;
		}
		case QueryTripleNode.SCAN:
			
			ret = AccessMethodType.DPH_SCAN;
			break;
		default: 
			throw new RuntimeException("Unknon access method: "+accessMethod);
		}
		return ret;
	}
	public static Pair<Double, Double> computeCost(int accessMethod,
			QueryTriple queryTriple,
			Map<Variable, Set<Constant>> filterBindings,
			BinaryUnion<Variable, IRI> graphRestriction,
			SPARQLOptimizerStatistics stats) {
		double edgeCost;
		if (queryTriple.getPredicate().isIRI()) {
			String pred = queryTriple.getPredicate().getIRI().toString();
			if (stats.globalStatistics.getTopPredicates().containsKey(pred)) {
				edgeCost = stats.globalStatistics.getTopPredicates().get(pred).doubleValue();
			} else {
				edgeCost = stats.globalStatistics.getPredicateStatistic().average();
			}
		} else if (isNotBoundVariable(queryTriple.getPredicate(), filterBindings)) {
			edgeCost = 0;
			for (BigInteger n : stats.globalStatistics.getTopPredicates().values()) {
				edgeCost += n.doubleValue();
			}
		} else {
			edgeCost = stats.globalStatistics.getPredicateStatistic().average();
		}
		Pair<Double,Double> ret;
		switch (accessMethod) {
		case FILTER_DPH: {
			Pair<Double,Double> dph = computeDPHCost(queryTriple, filterBindings, stats, edgeCost);
			ret =  Pair.make(.8 * dph.fst, .8 * dph.snd);
			break;
		}
		case FILTER_RPH: {
			Pair<Double,Double> rph = computeRPHCost(queryTriple, filterBindings, stats, edgeCost);
			ret =  Pair.make(.8 * rph.fst, .8 * rph.snd);
			break;
		}
		case DPH: {
			ret =  computeDPHCost(queryTriple, filterBindings, stats,
				edgeCost);
			break;
			//
		}
		case RPH: {
			ret = computeRPHCost(queryTriple, filterBindings, stats,
					edgeCost);
			break;
		}
		case GDPH: {
			if (graphRestriction != null) {
				if (graphRestriction.getFirst() != null) {
					ret =  Pair.make(getCost(graphRestriction.getFirst(),
							stats, filterBindings,
							stats.perGraphSingleStatistic.getGraphStatistic()
									.average()), edgeCost);
				} else { // if its a constant
					ret = Pair.make(getCost(graphRestriction.getSecond(),
							stats,
							stats.perGraphSingleStatistic.getGraphStatistic()
									.average()), edgeCost);
				}
			} else {
				throw new RuntimeException("GDPH without graph restriction");
			}
			break;
		}
		case SCAN: {
			if (costModelType == COSTMODEL_JENA 
			|| costModelType == COSTMODEL_VIRTUOSO) {
				edgeCost = Double.POSITIVE_INFINITY;
				for (BigInteger v: stats.globalStatistics.getTopPredicates().values()) {
					if (edgeCost>v.doubleValue()) {
						edgeCost = v.doubleValue();
					}
				}
				assert edgeCost!=Double.POSITIVE_INFINITY : edgeCost;
				if (!queryTriple.getPredicate().isIRI() 
				&& !isNotBoundVariable(queryTriple.getPredicate(), filterBindings)) {
					assert queryTriple.getPredicate().isVariable() : queryTriple.getPredicate();
					if (filterBindings.containsKey(queryTriple.getPredicate().getVariable())) {
						Set<Constant> constants = filterBindings.get(queryTriple.getPredicate().getVariable());
						boolean first = true;
						for (Constant c : constants) {
							BigInteger cost = stats.globalStatistics.getTopPredicates().get(c);
							if (cost!=null && (first || cost.doubleValue() < edgeCost)) {
								edgeCost =cost.doubleValue();
								first = false;
							}
						}
					}
				}
				ret = Pair.make(edgeCost, edgeCost);
			} else {
				ret = Pair.make(Double.MAX_VALUE/PROP_PATH_COST_MULTIPLYING_FACTOR_SCAN_LOOK_UP, Double.MAX_VALUE/PROP_PATH_COST_MULTIPLYING_FACTOR_SCAN_LOOK_UP);
			}
			break;
		}
		default:
			throw new RuntimeException("Unexpected access method "
					+ accessMethod);
		}
		if (queryTriple.getPredicate().isComplexPath() && queryTriple.getPredicate().getPath().isRecursive()) {
			AccessMethodType am = getSTAccessMethodForPropPath(queryTriple, accessMethod,  filterBindings );
			if (am!=null) {
				double m = 1.0;
				if (AccessMethodType.isIndexAccess(am)) {
					m = PROP_PATH_COST_MULTIPLYING_FACTOR_INDEX_LOOK_UP;
				} else if (AccessMethodType.isPollAccess(am)) {
					m = PROP_PATH_COST_MULTIPLYING_FACTOR_POLL_LOOK_UP;
				} else if (AccessMethodType.isScanAccess(am)) {
					m = PROP_PATH_COST_MULTIPLYING_FACTOR_SCAN_LOOK_UP;
				}
				ret = Pair.make(m*ret.fst, m*ret.snd);
			}
		}
		return ret;
	}

	private static Pair<Double, Double> computeRPHCost(
			QueryTriple queryTriple,
			Map<Variable, Set<Constant>> filterBindings,
			SPARQLOptimizerStatistics stats, double edgeCost) {
		if (isNotBoundVariable(queryTriple.getObject(), filterBindings)) {
			return Pair.make(stats.globalStatistics.getObjectStatistic()
					.average(), edgeCost);
		} else { // if its a constant
			return Pair.make(getCost(queryTriple.getObject(), stats,
					filterBindings,
					stats.globalStatistics.getObjectStatistic().average()),
					edgeCost);
		}
	}

	private static Pair<Double, Double> computeDPHCost(
			QueryTriple queryTriple,
			Map<Variable, Set<Constant>> filterBindings,
			SPARQLOptimizerStatistics stats, double edgeCost) {
		if (isNotBoundVariable(queryTriple.getSubject(), filterBindings)) {
			return Pair.make(stats.globalStatistics.getSubjectStatistic()
					.average(), edgeCost);
		} else { // if its a constant
			return Pair.make(getCost(queryTriple.getSubject(), stats,
					filterBindings,
					stats.globalStatistics.getSubjectStatistic().average()),
					edgeCost);
		}
	}

	public Pair<Double, Double> getCost(NumberedGraph<PlanNode> g) {
		// assert cost.fst > 0.0;
		return cost;
	}

	public int getId() {
		return id;
	}

	public Set<Variable> getProducedVariables() {
		return HashSetFactory.make(producedVariables);
	}

	public Set<Variable> getRequiredVariables() {
		return HashSetFactory.make(requiredVariables);
	}

	private static double getCost(String s,
			SPARQLOptimizerStatistics stats, double defaultValue) {
		double c = defaultValue;
		if (stats.globalStatistics.getTopSubjects().containsKey(s)) {
			c = stats.globalStatistics.getTopSubjects().get(s).doubleValue();
		}
		if (stats.globalStatistics.getTopObjects().containsKey(s)) {
			c = stats.globalStatistics.getTopObjects().get(s).doubleValue();
		}
		if (stats.perGraphSingleStatistic != null && stats.perGraphSingleStatistic.getTopGraphs().containsKey(s)) {
			c = stats.perGraphSingleStatistic.getTopGraphs().get(s)
					.doubleValue();
		}
		return c;
	}

	private static double getCost(QueryTripleTerm t,
			SPARQLOptimizerStatistics stats,
			Map<Variable, Set<Constant>> filterBindings, double defaultValue) {
		double c = defaultValue;
		if (t.isVariable()) {
			c = getCost(t.getVariable(), stats, filterBindings,
					defaultValue);
		} else {
			c = getCost(t.isConstant()? t.getConstant().toDataString(): t.toString(), stats, defaultValue);
		}
		return c;
	}

	private static double getCost(IRI i, SPARQLOptimizerStatistics stats,
			double defaultValue) {
		return getCost(i.toString(), stats, defaultValue);
	}

	private static double getCost(Variable v,
			SPARQLOptimizerStatistics stats,
			Map<Variable, Set<Constant>> filterBindings, double defaultValue) {
		double co = defaultValue;
		if (filterBindings.containsKey(v)) {
			Set<Constant> constants = filterBindings.get(v);
			for (Constant c : constants) {
				if (getCost(c.toString(), stats, defaultValue) < co) {
					co = getCost(c.toString(), stats, defaultValue);
				}
			}
		}
		return co;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + accessMethod;
		result = prime * result
				+ ((queryTriple == null) ? 0 : queryTriple.hashCode());
		return result;
	}

	public int getAccessMethod() {
		return accessMethod;
	}

	public QueryTriple getQueryTriple() {
		return queryTriple;
	}

	public Pattern getPattern() {
		return pattern;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		QueryTripleNode other = (QueryTripleNode) obj;
		if (accessMethod != other.accessMethod)
			return false;
		if (queryTriple != other.queryTriple) {
			return false;
		}
		if (pattern != other.pattern) {
			return false;
		}

		return true;
	}

	public static boolean isNotBoundVariable(QueryTripleTerm v,
			Map<Variable, Set<Constant>> filterBindings) {
		return v.isVariable()
				&& !filterBindings.containsKey(v.getVariable());
	}
	public static boolean isNotBoundVariable(PropertyTerm v,
			Map<Variable, Set<Constant>> filterBindings) {
		return v.isVariable()
				&& !filterBindings.containsKey(v.getVariable());
	}

	@Override
	public String toString() {
		return "QueryTripleNode [accessMethod=" + accessMethod
				+ ", queryTriple=" + queryTriple + ", cost= " + cost
				+ ", id=" + id + "]";
	}

	public Map<Variable, Set<Constant>> getFilterBindings() {
		return filterBindings;
	}

	public Kind kind() {
		return Kind.TRIPLE;
	}

	public Planner.Key getKey() {
		return queryTriple;
	}

}