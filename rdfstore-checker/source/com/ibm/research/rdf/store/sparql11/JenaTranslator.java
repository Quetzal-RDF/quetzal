package com.ibm.research.rdf.store.sparql11;

import static com.ibm.research.rdf.store.sparql11.ExpressionUtil.bitWidth;
import static com.ibm.research.rdf.store.sparql11.ExpressionUtil.divide;
import static com.ibm.research.rdf.store.sparql11.ExpressionUtil.equal_test;
import static com.ibm.research.rdf.store.sparql11.ExpressionUtil.greater_test;
import static com.ibm.research.rdf.store.sparql11.ExpressionUtil.intValue;
import static com.ibm.research.rdf.store.sparql11.ExpressionUtil.isBlank;
import static com.ibm.research.rdf.store.sparql11.ExpressionUtil.isLanguage;
import static com.ibm.research.rdf.store.sparql11.ExpressionUtil.isLiteral;
import static com.ibm.research.rdf.store.sparql11.ExpressionUtil.isNumeric;
import static com.ibm.research.rdf.store.sparql11.ExpressionUtil.isSimple;
import static com.ibm.research.rdf.store.sparql11.ExpressionUtil.isStringOrSimple;
import static com.ibm.research.rdf.store.sparql11.ExpressionUtil.less_test;
import static com.ibm.research.rdf.store.sparql11.ExpressionUtil.minus;
import static com.ibm.research.rdf.store.sparql11.ExpressionUtil.not_equal_test;
import static com.ibm.research.rdf.store.sparql11.ExpressionUtil.plus;
import static com.ibm.research.rdf.store.sparql11.ExpressionUtil.string_greater;
import static com.ibm.research.rdf.store.sparql11.ExpressionUtil.string_less;
import static com.ibm.research.rdf.store.sparql11.ExpressionUtil.times;
import static com.ibm.research.rdf.store.sparql11.ExpressionUtil.xsdBooleanType;
import static com.ibm.research.rdf.store.sparql11.ExpressionUtil.xsdStringType;
import static com.ibm.research.rdf.store.sparql11.ExpressionUtil.zero;
import static com.ibm.research.rdf.store.sparql11.FloatingPoint.floatAdd;
import static com.ibm.research.rdf.store.sparql11.FloatingPoint.floatCompare;
import static com.ibm.research.rdf.store.sparql11.FloatingPoint.floatDivide;
import static com.ibm.research.rdf.store.sparql11.FloatingPoint.floatMinus;
import static com.ibm.research.rdf.store.sparql11.FloatingPoint.floatMultiply;
import static com.ibm.research.rdf.store.sparql11.FloatingPoint.intToFloat;
import static com.ibm.research.rdf.store.sparql11.QuadTableRelations.NULL;
import static com.ibm.research.rdf.store.sparql11.QuadTableRelations.literalDatatypes;
import static com.ibm.research.rdf.store.sparql11.QuadTableRelations.literalValues;
import static com.ibm.research.rdf.store.sparql11.QuadTableRelations.nodes;
import static com.ibm.research.rdf.store.sparql11.QuadTableRelations.numericTypes;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import kodkod.ast.Decl;
import kodkod.ast.Decls;
import kodkod.ast.Expression;
import kodkod.ast.Formula;
import kodkod.ast.IntConstant;
import kodkod.ast.IntExpression;
import kodkod.ast.Relation;
import kodkod.ast.Variable;
import kodkod.ast.operator.IntCompOperator;
import kodkod.ast.operator.Multiplicity;
import kodkod.engine.Evaluator;
import kodkod.engine.Solution;
import kodkod.engine.Solution.Outcome;
import kodkod.engine.Solver;
import kodkod.engine.config.Options.IntEncoding;
import kodkod.engine.satlab.SATFactory;
import kodkod.instance.Bounds;
import kodkod.instance.Instance;
import kodkod.instance.Tuple;
import kodkod.instance.TupleFactory;
import kodkod.instance.TupleSet;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.OpVisitor;
import com.hp.hpl.jena.sparql.algebra.op.OpAssign;
import com.hp.hpl.jena.sparql.algebra.op.OpBGP;
import com.hp.hpl.jena.sparql.algebra.op.OpConditional;
import com.hp.hpl.jena.sparql.algebra.op.OpDatasetNames;
import com.hp.hpl.jena.sparql.algebra.op.OpDiff;
import com.hp.hpl.jena.sparql.algebra.op.OpDisjunction;
import com.hp.hpl.jena.sparql.algebra.op.OpDistinct;
import com.hp.hpl.jena.sparql.algebra.op.OpExt;
import com.hp.hpl.jena.sparql.algebra.op.OpExtend;
import com.hp.hpl.jena.sparql.algebra.op.OpFilter;
import com.hp.hpl.jena.sparql.algebra.op.OpGraph;
import com.hp.hpl.jena.sparql.algebra.op.OpGroup;
import com.hp.hpl.jena.sparql.algebra.op.OpJoin;
import com.hp.hpl.jena.sparql.algebra.op.OpLabel;
import com.hp.hpl.jena.sparql.algebra.op.OpLeftJoin;
import com.hp.hpl.jena.sparql.algebra.op.OpList;
import com.hp.hpl.jena.sparql.algebra.op.OpMinus;
import com.hp.hpl.jena.sparql.algebra.op.OpNull;
import com.hp.hpl.jena.sparql.algebra.op.OpOrder;
import com.hp.hpl.jena.sparql.algebra.op.OpPath;
import com.hp.hpl.jena.sparql.algebra.op.OpProcedure;
import com.hp.hpl.jena.sparql.algebra.op.OpProject;
import com.hp.hpl.jena.sparql.algebra.op.OpPropFunc;
import com.hp.hpl.jena.sparql.algebra.op.OpQuad;
import com.hp.hpl.jena.sparql.algebra.op.OpQuadBlock;
import com.hp.hpl.jena.sparql.algebra.op.OpQuadPattern;
import com.hp.hpl.jena.sparql.algebra.op.OpReduced;
import com.hp.hpl.jena.sparql.algebra.op.OpSequence;
import com.hp.hpl.jena.sparql.algebra.op.OpService;
import com.hp.hpl.jena.sparql.algebra.op.OpSlice;
import com.hp.hpl.jena.sparql.algebra.op.OpTable;
import com.hp.hpl.jena.sparql.algebra.op.OpTopN;
import com.hp.hpl.jena.sparql.algebra.op.OpTriple;
import com.hp.hpl.jena.sparql.algebra.op.OpUnion;
import com.hp.hpl.jena.sparql.core.Quad;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.expr.E_Add;
import com.hp.hpl.jena.sparql.expr.E_Bound;
import com.hp.hpl.jena.sparql.expr.E_Coalesce;
import com.hp.hpl.jena.sparql.expr.E_Datatype;
import com.hp.hpl.jena.sparql.expr.E_Divide;
import com.hp.hpl.jena.sparql.expr.E_Equals;
import com.hp.hpl.jena.sparql.expr.E_Exists;
import com.hp.hpl.jena.sparql.expr.E_GreaterThan;
import com.hp.hpl.jena.sparql.expr.E_GreaterThanOrEqual;
import com.hp.hpl.jena.sparql.expr.E_IsBlank;
import com.hp.hpl.jena.sparql.expr.E_IsIRI;
import com.hp.hpl.jena.sparql.expr.E_IsLiteral;
import com.hp.hpl.jena.sparql.expr.E_IsNumeric;
import com.hp.hpl.jena.sparql.expr.E_Lang;
import com.hp.hpl.jena.sparql.expr.E_LessThan;
import com.hp.hpl.jena.sparql.expr.E_LessThanOrEqual;
import com.hp.hpl.jena.sparql.expr.E_LogicalAnd;
import com.hp.hpl.jena.sparql.expr.E_LogicalNot;
import com.hp.hpl.jena.sparql.expr.E_LogicalOr;
import com.hp.hpl.jena.sparql.expr.E_Multiply;
import com.hp.hpl.jena.sparql.expr.E_NotEquals;
import com.hp.hpl.jena.sparql.expr.E_NotExists;
import com.hp.hpl.jena.sparql.expr.E_SameTerm;
import com.hp.hpl.jena.sparql.expr.E_Subtract;
import com.hp.hpl.jena.sparql.expr.E_UnaryMinus;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.ExprAggregator;
import com.hp.hpl.jena.sparql.expr.ExprFunction0;
import com.hp.hpl.jena.sparql.expr.ExprFunction1;
import com.hp.hpl.jena.sparql.expr.ExprFunction2;
import com.hp.hpl.jena.sparql.expr.ExprFunction3;
import com.hp.hpl.jena.sparql.expr.ExprFunctionN;
import com.hp.hpl.jena.sparql.expr.ExprFunctionOp;
import com.hp.hpl.jena.sparql.expr.ExprVar;
import com.hp.hpl.jena.sparql.expr.ExprVisitor;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.expr.aggregate.AggCount;
import com.hp.hpl.jena.sparql.expr.aggregate.AggCountVar;
import com.hp.hpl.jena.sparql.expr.aggregate.AggCountVarDistinct;
import com.hp.hpl.jena.sparql.expr.aggregate.AggMax;
import com.hp.hpl.jena.sparql.expr.aggregate.AggMin;
import com.hp.hpl.jena.sparql.expr.aggregate.AggSample;
import com.hp.hpl.jena.sparql.expr.aggregate.Aggregator;
import com.hp.hpl.jena.sparql.path.P_Alt;
import com.hp.hpl.jena.sparql.path.P_Distinct;
import com.hp.hpl.jena.sparql.path.P_FixedLength;
import com.hp.hpl.jena.sparql.path.P_Inverse;
import com.hp.hpl.jena.sparql.path.P_Link;
import com.hp.hpl.jena.sparql.path.P_Mod;
import com.hp.hpl.jena.sparql.path.P_Multi;
import com.hp.hpl.jena.sparql.path.P_NegPropSet;
import com.hp.hpl.jena.sparql.path.P_OneOrMore1;
import com.hp.hpl.jena.sparql.path.P_OneOrMoreN;
import com.hp.hpl.jena.sparql.path.P_Path0;
import com.hp.hpl.jena.sparql.path.P_ReverseLink;
import com.hp.hpl.jena.sparql.path.P_Seq;
import com.hp.hpl.jena.sparql.path.P_Shortest;
import com.hp.hpl.jena.sparql.path.P_ZeroOrMore1;
import com.hp.hpl.jena.sparql.path.P_ZeroOrMoreN;
import com.hp.hpl.jena.sparql.path.P_ZeroOrOne;
import com.hp.hpl.jena.sparql.path.PathVisitor;
import com.ibm.rdf.store.dawg.queries.QueryTests.QueryTest;
import com.ibm.rdf.store.dawg.queries.SparqlSelectResult;
import com.ibm.research.rdf.store.sparql11.DawgUtil.DawgVerification;
import com.ibm.research.rdf.store.sparql11.JenaTranslator.Continuation;
import com.ibm.research.sparql.rewriter.OpVariableVistor;
import com.ibm.wala.util.Predicate;
import com.ibm.wala.util.collections.HashMapFactory;
import com.ibm.wala.util.collections.HashSetFactory;
import com.ibm.wala.util.collections.Iterator2Collection;
import com.ibm.wala.util.collections.Pair;
import com.ibm.wala.util.functions.Function;

@SuppressWarnings("unused")
public class JenaTranslator implements OpVisitor {
	
	private interface Domain {		
		Expression bound();
	}
	
	private static class And implements Domain {
		private final Domain[] domains;
		
		private And(Domain... domains) {
			for(int i = 0; i < domains.length; i++) {
				assert domains[i] != null;
			}
			this.domains = domains;
		}

		@Override
		public Expression bound() {
			Expression e = domains[0].bound();
			for(int i = 1; i < domains.length; i++) {
				e = e.intersection(domains[i].bound());
			}
			return e;
		}
		
		public String toString() {
			StringBuffer sb = new StringBuffer("AND(");
			sb.append(domains[0].toString());
			for(int i = 1; i < domains.length; i++) {
				sb.append(",").append(domains[i].toString());
			}
			sb.append(")");
			return sb.toString();
		}
	}
	
	private static class Or implements Domain {
		private final Domain[] domains;
		
		private Or(Domain... domains) {
			this.domains = domains;
		}

		@Override
		public Expression bound() {
			Expression e = domains[0].bound();
			for(int i = 1; i < domains.length; i++) {
				e = e.union(domains[i].bound());
			}
			return e;
		}
		
		public String toString() {
			StringBuffer sb = new StringBuffer("OR(");
			sb.append(domains[0].toString());
			for(int i = 1; i < domains.length; i++) {
				sb.append(",").append(domains[i].toString());
			}
			sb.append(")");
			return sb.toString();
		}
	}

	private enum Leaf implements Domain { 
		NULL {
			@Override
			public Relation bound() {
				return QuadTableRelations.NULL;
			}
		}, GRAPH {
			@Override
			public Expression bound() {
				return QuadTableRelations.graphs;
			}
		}, SUBJECT {
			@Override
			public Expression bound() {
				return QuadTableRelations.subjects;
			}
		}, PREDICATE {
			public Expression bound() {
				return QuadTableRelations.predicates;
			}		
		}, OBJECT {
			@Override
			public Expression bound() {
				return QuadTableRelations.objects;
			}
		}, ANY {
			@Override
			public Expression bound() {
				return QuadTableRelations.nodes;
			}			
		}, CHOICE {
			@Override
			public Expression bound() {
				return QuadTableRelations.choice;
			}
		}
	}
	
	private interface TranslatorContext {

		public abstract Map<String, Variable> getVars();

		public abstract Map<String, Object> constants();

		public abstract Expression getActiveGraph();

		public abstract Formula getCurrentQuery();

		public abstract void setCurrentQuery(Formula currentQuery);

		public abstract Expression getDynamicBinding();

		public abstract void setDynamicBinding(Expression dynamicBinding);

		public abstract Set<Variable> getStaticBinding();

		public abstract void setStaticBinding(Set<Variable> staticBinding);

		public abstract Domain getDomain(Variable v);
		
		public abstract void setDomain(Variable v, Domain d);
		
		public abstract Op top();
		
		public abstract Op scope();
		
		public boolean explicitChoices();
		
		public Continuation getCurrentContinuation();

		public void setCurrentContinuation(Continuation currentContinuation);

	}

	@FunctionalInterface
	interface Continuation {
		void next(TranslatorContext context, Formula ret);
	}
	
	private abstract static class DelegatingContext implements TranslatorContext {
		protected final TranslatorContext parent;

		public DelegatingContext(TranslatorContext parent) {
			this.parent = parent;
		}

		@Override
		public boolean explicitChoices() {
			return parent.explicitChoices();
		}

		public Map<String, Variable> getVars() {
			return parent.getVars();
		}

		public Map<String, Object> constants() {
			return parent.constants();
		}

		public Expression getActiveGraph() {
			return parent.getActiveGraph();
		}

		public Formula getCurrentQuery() {
			return parent.getCurrentQuery();
		}

		public void setCurrentQuery(Formula currentQuery) {
			parent.setCurrentQuery(currentQuery);
		}

		@Override
		public Continuation getCurrentContinuation() {
			return parent.getCurrentContinuation();
		}

		@Override
		public void setCurrentContinuation(Continuation currentContinuation) {
			parent.setCurrentContinuation(currentContinuation);
		}

		public Expression getDynamicBinding() {
			return parent.getDynamicBinding();
		}

		public void setDynamicBinding(Expression dynamicBinding) {
			parent.setDynamicBinding(dynamicBinding);
		}

		public Set<Variable> getStaticBinding() {
			return parent.getStaticBinding();
		}

		public void setStaticBinding(Set<Variable> staticBinding) {
			parent.setStaticBinding(staticBinding);
		}

		@Override
		public Domain getDomain(Variable v) {
			return parent.getDomain(v);
		}

		@Override
		public void setDomain(Variable v, Domain d) {
			parent.setDomain(v, d);
		}

		@Override
		public Op top() {
			return parent.top();
		}					

		@Override
		public Op scope() {
			return parent.scope();
		}					
}
	
	private static class BaseTranslatorContext implements Cloneable, TranslatorContext {
		private final Map<String, Variable> vars;
		private final Map<String, Object> constants;
		private final Expression activeGraph;
		private final Map<Variable,Domain> domain = HashMapFactory.make();
		private final Op top;
		private final boolean choices;
		
		private Formula currentQuery;
		private Continuation currentContinuation;
		private Expression dynamicBinding;
		private Set<Variable> staticBinding;
		
		public BaseTranslatorContext(Map<String, Variable> vars, Map<String, Object> bindings, Expression activeGraph, Op top, boolean choices) {
			this.vars = vars;
			this.activeGraph = activeGraph;
			this.top = top;
			this.constants = bindings;
			this.choices = choices;
		}

		public BaseTranslatorContext(Map<String, Variable> vars, Expression activeGraph, Op top, boolean choices) {
			this(vars, HashMapFactory.<String, Object>make(), activeGraph, top, choices);
		}
		
		/* (non-Javadoc)
		 * @see com.ibm.rdf.store.sparql11.TranslatorContext#getVars()
		 */
		@Override
		public Map<String, Variable> getVars() {
			return vars;
		}

		@Override
		public Map<String, Object> constants() {
			return constants;
		}

		/* (non-Javadoc)
		 * @see com.ibm.rdf.store.sparql11.TranslatorContext#getActiveGraph()
		 */
		@Override
		public Expression getActiveGraph() {
			return activeGraph;
		}

		/* (non-Javadoc)
		 * @see com.ibm.rdf.store.sparql11.TranslatorContext#getCurrentQuery()
		 */
		@Override
		public Formula getCurrentQuery() {
			return currentQuery;
		}

		/* (non-Javadoc)
		 * @see com.ibm.rdf.store.sparql11.TranslatorContext#setCurrentQuery(kodkod.ast.Formula)
		 */
		@Override
		public void setCurrentQuery(Formula currentQuery) {
			this.currentQuery = currentQuery;
		}

		public Continuation getCurrentContinuation() {
			return currentContinuation;
		}

		public void setCurrentContinuation(Continuation currentContinuation) {
			this.currentContinuation = currentContinuation;
		}

		/* (non-Javadoc)
		 * @see com.ibm.rdf.store.sparql11.TranslatorContext#getDynamicBinding()
		 */
		@Override
		public Expression getDynamicBinding() {
			if (dynamicBinding == null) {
				return Expression.NONE;
			} else {
				return dynamicBinding;
			}
		}

		/* (non-Javadoc)
		 * @see com.ibm.rdf.store.sparql11.TranslatorContext#setDynamicBinding(kodkod.ast.Expression)
		 */
		@Override
		public void setDynamicBinding(Expression dynamicBinding) {
			this.dynamicBinding = dynamicBinding;
		}

		/* (non-Javadoc)
		 * @see com.ibm.rdf.store.sparql11.TranslatorContext#getStaticBinding()
		 */
		@Override
		public Set<Variable> getStaticBinding() {
			return staticBinding;
		}

		/* (non-Javadoc)
		 * @see com.ibm.rdf.store.sparql11.TranslatorContext#setStaticBinding(java.util.Set)
		 */
		@Override
		public void setStaticBinding(Set<Variable> staticBinding) {
			this.staticBinding = staticBinding;
		}

		@Override
		public Domain getDomain(Variable v) {
			return domain.containsKey(v)? domain.get(v): null;
		}

		@Override
		public void setDomain(Variable v, Domain d) {
			domain.put(v, d);
		}

		@Override
		public Op top() {
			return top;
		}

		@Override
		public Op scope() {
			return top;
		}

		@Override
		public boolean explicitChoices() {
			return choices;
		}
}

	private static class ScopeContext extends DelegatingContext {
		private final Op scope;
		
		private ScopeContext(TranslatorContext parent, Op scope) {
			super(parent);
			this.scope = scope;
		}

		public Op scope() {
			return scope;
		}
	}
	
	private static class DomainContext extends DelegatingContext {

		private DomainContext(TranslatorContext parent) {
			super(parent);
		}

		private final Map<Variable,Domain> domain = HashMapFactory.make();
		
		@Override
		public Domain getDomain(Variable v) {
			if (domain.containsKey(v)) {
				if (parent.getDomain(v) != null && !domain.get(v).equals(parent.getDomain(v))) {
					return new And(domain.get(v), parent.getDomain(v));
				} else {
					return domain.get(v);
				}
			} else {
				return parent.getDomain(v);
			}
		}

		@Override
		public void setDomain(Variable v, Domain d) {
			if (parent.getDomain(v) == null) {
				parent.setDomain(v, d);
			} else {
				domain.put(v, d);
			}
		}		
	};
	
	private static Set<String> gatherVariableNames(Collection<Op> queries) {
		Set<String> result =  HashSetFactory.make();
		for (Op query : queries) {
			result.addAll(getVariableNames(query));
		}
		return result;
	}
	
	private static <T> int count(List<T> lst, T elt) {
		int count = 0;
		for(T e : lst) {
			if (elt.equals(e)) {
				count++;
			}
		}
		return count;
	}
	
	private Set<String> privateVariableNames(Op inner, Op outer) {
		List<String> outerNames = getVariableNames(outer);
		List<String> innerNames = getVariableNames(inner);
		
		Set<String> result = HashSetFactory.make(innerNames);
		for(Iterator<String> vars = result.iterator(); vars.hasNext(); ) {
			String var = vars.next();
			if (count(innerNames, var) < count(outerNames, var)) {
				vars.remove();
			}
		}
		
		for (Variable v : projectedVars) {
			if (result.contains(v.name())) {
				result.remove(v.name());
			}
		}
		
		return result;
	}
	
	private static List<String> getVariableNames(final Op query) {
		return new OpVariableVistor<String>(query, true) {
			@Override
			protected String processVar(Node v) {
				return v.getName();
			} 
		}.getResult();
	}

	private List<Variable> getVariables(Op query, TranslatorContext context) {
		final Map<String,Variable> vars = context.getVars();
		return new OpVariableVistor<Variable>(query, false) {
			@Override
			protected Variable processVar(Node v) {
				return vars.get(v.getName());
			} 
		}.getResult();
	}

	private final UniverseFactory universe;
	
	private final Map<String, Variable> allVars;

	private final List<Variable> projectedVars;

	private final SolutionRelation solution;
	
	private final Collection<Op> queries;
	
	private JenaTranslator(Map<String,Variable> allVars, List<Variable> projectedVars, 
			Collection<Op> queries, UniverseFactory universe, SolutionRelation solution) {
		this.queries = queries;
		this.universe = universe;
		this.allVars = allVars; 
		this.projectedVars = projectedVars;
		this.solution = solution;
	}
	
	public static JenaTranslator make(List<Var> projectedVars, Op query, UniverseFactory universe, SolutionRelation solution) {
		return make(projectedVars, Collections.singleton(query), universe, solution);
	}

	public static JenaTranslator make(List<Var> projectedVars, Collection<Op> queries, UniverseFactory universe, SolutionRelation solution) {
		Map<String,Variable> allVars = makeAllVars(queries);
		return new JenaTranslator(allVars, makeProjectedVars(projectedVars, allVars), queries, universe, solution);
	}

	public static JenaTranslator make(List<Var> projectedVars, Op query, UniverseFactory universe) {
		return make(projectedVars, Collections.singleton(query), universe, null);
	}
	
	private static Map<String,Variable> makeAllVars(Collection<Op> queries) {
		Map<String,Variable> allVars = HashMapFactory.make();
		List<String> varNames = new ArrayList<String>(gatherVariableNames(queries));
		Collections.sort(varNames);
		for(String name : varNames) {
			allVars.put(name, Variable.unary(name));
		}
		return allVars;
	}
	
	private static List<Variable> makeProjectedVars(List<Var> projectedVars, Map<String,Variable> allVars) {
		List<Variable> result = new LinkedList<Variable>();
		for(Var v : projectedVars) {
			result.add(allVars.get(v.getName()));
		}
		return result;
	}
	
	private Pair<String,URI> TRUE = null;
	private Pair<String,URI> FALSE = null;
	private Pair<String,Object> TRUEL = null;
	private Pair<String,Object> FALSEL = null;
	private Pair<String,URI> EMPTY = null;

	private Pair<String,URI> EMPTY() { 
		if (EMPTY == null) {
			try {
				EMPTY = Pair.make("", new URI(xsdStringType));
				universe.ensureLiteral(EMPTY);
			} catch (URISyntaxException e) {
				assert false : e.toString();
			}
		}
		return EMPTY; 
	};


	private Pair<String,URI> TRUE() { 
		if (TRUE == null) {
			try {
				TRUE = Pair.make("true", new URI(xsdBooleanType));
				universe.ensureLiteral(TRUE);
			} catch (URISyntaxException e) {
				assert false : e.toString();
			}
		}
		return TRUE; 
	};

	private Pair<String,URI> FALSE() { 
		if (FALSE == null) {
			try {
				FALSE = Pair.make("false", new URI(xsdBooleanType));
				universe.ensureLiteral(FALSE);
			} catch (URISyntaxException e) {
				assert false : e.toString();
			}
		}
		return FALSE; 
	};

	private Pair<String,Object> TRUEL() { 
		if (TRUEL == null) {
			TRUEL = Pair.make("true", null);
			universe.ensureLiteral(TRUEL);
		}
		return TRUEL; 
	};

	private Pair<String,Object> FALSEL() { 
		if (FALSEL == null) {
			FALSEL = Pair.make("false", null);
			universe.ensureLiteral(FALSEL);
		}
		return FALSEL; 
	};

	private Expression castToBoolean(ExpressionContext e) {
		return ebv(e).thenElse(universe.atomRelation(TRUE()), universe.atomRelation(FALSE()));
	}
	
	private Formula ebv(ExpressionContext e) {
		return 
			isBooleanType(e.type()).and(e.value().eq(universe.atomRelation(TRUE()))).or(
			isIntegerType(e.type()).and(e.intValue().compare(IntCompOperator.EQ, IntConstant.constant(0)).not())).or(
			isSomeFloatType(e.type()).and(e.floatValue().compare(IntCompOperator.EQ, IntConstant.constant(0)).not())).or(
			isStringOrSimple(e.value()).and(e.value().join(QuadTableRelations.literalValues).eq(universe.atomRelation(EMPTY()).join(QuadTableRelations.literalValues)).not()));	
	}
	
	private Formula validEBV(ExpressionContext v) {
		return isNumericType(v.type())
				.or(isBooleanType(v.type()))
				.or(isStringOrSimple(v.value()));
	}

	private Expression scope(Formula f, Expression dynamicBinding, Collection<Variable> neededVars) {
		Pair<Formula,Decls> x = scopeInternal(f, dynamicBinding, neededVars);
		return x.fst.comprehension(x.snd);
	}

	private Formula existentialScope(Collection<Variable> vars, Formula f, Expression dynamicBinding) {
		Pair<Formula,Decls> x = scopeInternal(f, dynamicBinding, Collections.<Variable>emptySet());
		if (x.snd == null) {
			return x.fst;
		} else {
			return x.fst.forSome(x.snd);
		}
	}

	private Pair<Formula, Decls> scopeInternal(Formula f, Expression dynamicBinding, Collection<Variable> projectedVars) {
		Decls ds = null, eds = null;
		Set<Variable> used = ASTUtils.gatherVariables(f);
		if (dynamicBinding != null) {
			used.addAll(ASTUtils.gatherVariables(dynamicBinding));
		}
		List<Variable> orderedVars = sortVars(used);
		for(Variable v : orderedVars) {
			Expression r;
			if (context.getDomain(v) != null ) {
				r = context.getDomain(v).bound();
				if (r == null) {
					r = NULL;
				}
			} else {
				r = NULL;
			}
			Decl d = v.declare(Multiplicity.ONE, context.getStaticBinding().contains(v)? r: r.union(NULL));
			if (! projectedVars.contains(v)) {
				eds = eds==null? d: eds.and(d);
			} else if (ds == null) {
				ds = d;
			} else {
				ds = ds.and(d);
			}
			if (! context.getStaticBinding().contains(v)) {
				if (context.getDynamicBinding() == null) {
					f = f.and(v.eq(NULL));					
				} else {
					f = f.and(varExpr(v).in(context.getDynamicBinding()).or(v.eq(NULL)));
				}
			}
		}

		if (eds != null) {
			f = f.forSome(eds);
		}
		
		System.err.println("scope: " + eds + " -- " + ds);
		
		return Pair.make(f, ds);
	}

	public static List<Variable> sortVars(Collection<Variable> vars) {
		return sortVars(vars, new Function<Variable,String>() {
			@Override
			public String apply(Variable arg0) {
				return arg0.name();
			}			
		});
	}
	
	public static <T> List<T> sortVars(Collection<T> vars, final Function<T,String> name) {
		List<T> orderedVars = new ArrayList<T>(vars);
		Collections.sort(orderedVars, new Comparator<T>() {
			@Override
			public int compare(T o1, T o2) {
				return name.apply(o1).compareTo(name.apply(o2));
			}
		});
		return orderedVars;
	}

	public Pair<Formula,Pair<Formula,Formula>> translateMulti(boolean leftNonEmpty, boolean rightNonEmpty) {	
		Set<Pair<Formula,Pair<Formula,Formula>>> stuff = HashSetFactory.make();
		assert queries.size() == 2;

		Iterator<Op> qs = queries.iterator();

		Op left = qs.next();
		this.context = new BaseTranslatorContext(allVars, universe.atomRelation(QuadTableRelations.defaultGraph), left, false);
		visit(left, (TranslatorContext context1, Formula lq) -> {
			Expression ls = scope(lq, context.getDynamicBinding(), projectedVars);

			Op right = qs.next();
			this.context = new BaseTranslatorContext(allVars, universe.atomRelation(QuadTableRelations.defaultGraph), right, false);
			visit(right, (TranslatorContext context2, Formula rq) -> {
				Expression rs = scope(rq, context.getDynamicBinding(), projectedVars);

				Relation leftSolution = Relation.nary("left_solution", ls.arity());
				universe.nodesRelation(leftSolution);

				Relation rightSolution = Relation.nary("right_solution", rs.arity());
				universe.nodesRelation(rightSolution);

				Formula q = rs.eq(ls).not().and(ls.eq(leftSolution)).and(rs.eq(rightSolution));

				if (leftNonEmpty) {
					q = q.and(ls.some());
				}

				if (rightNonEmpty) {
					q = q.and(rs.some());
				}

				for(Formula f : relationBindings) {
					q = q.and(f);
				}

				Relation leftExtra = Relation.nary("left_extra", ls.arity());
				universe.nodesRelation(leftExtra);
				Formula getLeftExtra = leftExtra.eq(ls.difference(rs));

				Relation rightExtra = Relation.nary("right_extra", ls.arity());
				universe.nodesRelation(rightExtra);
				Formula getRightExtra = rightExtra.eq(rs.difference(ls));

				stuff.add(Pair.make(Formula.TRUE, Pair.make(q, getLeftExtra.and(getRightExtra))));
			});
		});

		return stuff.iterator().next();
	}

	public Set<Pair<Formula,Pair<Formula,Formula>>> translateSingle(Map<String, Object> bindings, boolean expand) {	
		Set<Pair<Formula,Pair<Formula,Formula>>> stuff = HashSetFactory.make();
		assert queries.size() == 1;
		Op query = queries.iterator().next();

		this.context = new BaseTranslatorContext(allVars, bindings, universe.atomRelation(QuadTableRelations.defaultGraph), query, expand);
		visit(query, (TranslatorContext context1, Formula q) -> {
			Expression actualSolution;
			Formula correctness = null;
			Formula differences = null;
			Formula s = Formula.TRUE;	

			if (solution != null && solution.variables().isEmpty()) {
				actualSolution = null;

				q = existentialScope(context.getVars().values(), q, context.getDynamicBinding());
				if (solution.hasSolutions()) {
					s = q; 
				} else {
					s = q.not();
				} 

			} else if (solution != null) {
				Set<Variable> projectedVars = HashSetFactory.make();
				for(Variable name : sortVars(context.getVars().values())) {
					com.ibm.research.rdf.store.sparql11.model.Variable key = new com.ibm.research.rdf.store.sparql11.model.Variable(name.name());
					if (solution.variables().contains(key)) {
						projectedVars.add(name);
					}
				}

				actualSolution = scope(q, context.getDynamicBinding(), projectedVars);

				Expression x;
				Set<Variable> liveVars = ASTUtils.gatherVariables(actualSolution);

				Expression sol = solution.mappedSolution(universe);
				Expression expectedSolution = sol;
				if (expectedSolution == null) {
					Relation r = solution.solutionRelation();
					expectedSolution = r;
					universe.nodesRelation(r);
				}

				Domain[] domain = new Domain[expectedSolution.arity()];
				if (expectedSolution.arity() != actualSolution.arity()) {
					int i = 0, index = 0;
					List<IntConstant> cols = new LinkedList<IntConstant>();
					for(Variable name : sortVars(context.getVars().values())) {
						if (liveVars.contains(name)) {
							com.ibm.research.rdf.store.sparql11.model.Variable key = new com.ibm.research.rdf.store.sparql11.model.Variable(name.name());
							if (solution.variables().contains(key)) {
								domain[i] = context.getDomain(name);
								cols.add(IntConstant.constant(index));
							}
							index++;
						}	
					}	
					x = actualSolution.project(cols.toArray(new IntConstant[cols.size()]));
				} else {
					int i = 0;
					for(Variable name : sortVars(projectedVars)) {
						if (liveVars.contains(name)) {
							domain[i++] = context.getDomain(name);
						}
					}

					x = actualSolution;
				}

				if (solution.hasBlankNodes()) {
					Variable m1 = Variable.unary("x");
					Variable m2 = Variable.unary("y");
					Variable m3 = Variable.unary("a");
					Variable m4 = Variable.unary("b");
					Decls md = m4.oneOf(QuadTableRelations.blankNodes)
							.and(m3.oneOf(QuadTableRelations.blankNodes))
							.and(m2.oneOf(QuadTableRelations.blankNodes))
							.and(m1.oneOf(QuadTableRelations.blankNodes));
					Formula map = 
							m1.product(m2).in(QuadTableRelations.blankNodeRenaming)
							.and(m3.product(m4).in(QuadTableRelations.blankNodeRenaming))
							.implies(m1.eq(m3).iff(m2.eq(m4))).forAll(md);
					s = s.and(map);

					if (sol != null) {
						correctness = x.eq(sol);
					} else {
						correctness = x.no();
					}
					System.err.println("adding verification constraint\n");
				} else {
					correctness = x.eq(expectedSolution);				
					System.err.println("adding verification constraint for \n" + expectedSolution);
				}

				Relation extraSolutions = Relation.nary("extra_solutions", actualSolution.arity());
				universe.nodesRelation(extraSolutions);
				differences = extraSolutions.eq(x.difference(expectedSolution)); 

				Relation missingSolutions = Relation.nary("missing_solutions", actualSolution.arity());
				universe.nodesRelation(missingSolutions);
				differences = differences.and(missingSolutions.eq(expectedSolution.difference(x))); 

			} else {
				actualSolution = scope(q, context.getDynamicBinding(), context.getVars().values());
				System.err.println(actualSolution);
			}

			for(Formula f : relationBindings) {
				s = s.and(f);
			}

			if (actualSolution != null) {
				Relation solutionRelation = Relation.nary("solution", actualSolution.arity());
				universe.nodesRelation(solutionRelation);
				s = s.and(actualSolution.eq(solutionRelation));
			}

			stuff.add(Pair.make(s, Pair.make(correctness,  differences)));
		});
		return stuff;
	}

	private TranslatorContext context;

	private Expression toTerm(Node n) {
		if (n.isVariable()) {
			if (context.constants().containsKey(n.getName())) {
				return universe.atomRelation(context.constants().get(n.getName()));
			} else {
				assert context.getVars().containsKey(n.getName()) : "cannot find " + n.getName();
				return context.getVars().get(n.getName());
			}
		} else {
			try {
				if (n.isLiteral()) {
					universe.ensureLiteral(JenaUtil.toAtom(n.getLiteral()));
					return universe.atomRelation(JenaUtil.toAtom(n.getLiteral()));
				} else if (n.isURI()) {
					universe.ensureIRI(JenaUtil.toURI(n));
				}
				return universe.atomRelation(JenaUtil.toAtom(n));
			} catch (URISyntaxException e) {
				assert false : e.toString();	
				return universe.atomRelation(QuadTableRelations.NULL_atom);
			}
		}
	}

	private Expression toType(Node n) {
		return toTerm(n).join(QuadTableRelations.literalDatatypes).union(toTerm(n).join(QuadTableRelations.literalLanguages));
	}

	private Object varAtom(Variable v) {
		return "?" + String.valueOf(v);
	}
	
	private Expression varExpr(Variable v) {
		return universe.atomRelation(varAtom(v));
	}

	// normally, all variables mentioned must be bound
	private <T extends kodkod.ast.Node> T doStandardBindings(T ast, Op op) {		
		return doStandardBindings(ast, ASTUtils.gatherVariables(ast));
	}
	
	private <T extends kodkod.ast.Node> T doStandardBindings(T ast, Set<Variable> vars) {		
		if (context.getStaticBinding() == null) {
			context.setStaticBinding(vars);
		} else {
			context.setStaticBinding(HashSetFactory.make(context.getStaticBinding()));
			context.getStaticBinding().addAll(vars);
		}
		
		Expression boundVars = null;
		for(Variable v : vars) {
			Expression token = varExpr(v);
			boundVars = (boundVars==null)? token : boundVars.union(token);
		}
		if (boundVars != null) {
			if (context.getDynamicBinding() == null) {
				context.setDynamicBinding(boundVars);
			} else {
				context.setDynamicBinding(context.getDynamicBinding().union(boundVars));
			}
		}
		
		return ast;
	}
	
	private Expression constrainDomain(Expression e, Domain d) {
		if (e instanceof Variable) {
			if (context.getDomain((Variable)e) != null) {
				if (! d.equals(context.getDomain((Variable)e))) {
					context.setDomain((Variable)e, new And(d, context.getDomain((Variable)e)));
				}
			} else {
				context.setDomain((Variable)e, d);
			}
			System.err.println("constraining " + e + " to be " + context.getDomain((Variable)e));
		}
		return e;
	}
	private Formula handleTriple(Triple arg0) {
		return context.getActiveGraph()
		.product(constrainDomain(toTerm(arg0.getSubject()), Leaf.SUBJECT))
		.product(constrainDomain(toTerm(arg0.getPredicate()), Leaf.PREDICATE))
		.product(constrainDomain(toTerm(arg0.getObject()), Leaf.OBJECT))
		.in(QuadTableRelations.quads);
	}

	private Formula handleQuad(Quad arg0) {
		return constrainDomain(toTerm(arg0.getGraph()), Leaf.GRAPH)
		.product(constrainDomain(toTerm(arg0.getSubject()), Leaf.SUBJECT))
		.product(constrainDomain(toTerm(arg0.getPredicate()), Leaf.PREDICATE))
		.product(constrainDomain(toTerm(arg0.getObject()), Leaf.OBJECT))
		.in(QuadTableRelations.quads);
	}

	private void conj(Formula conjunct) {
		if (context.getCurrentQuery() == null) {
			context.setCurrentQuery(conjunct);
		} else {
			context.setCurrentQuery(context.getCurrentQuery().and(conjunct));
		}
	}
	
	private Expression restrictExistsWithFilter(Set<Variable> lhsVars, Pair<Relation, List<Pair<Variable, Domain>>> rightRelation, Formula filters, Expression tuples) {
		Expression t = null;
		Decls d = null;
		Set<Variable> filterVars = filters==null? Collections.<Variable>emptySet(): ASTUtils.gatherVariables(filters);
		for(Pair<Variable,Domain> v : rightRelation.snd) {
			if (lhsVars.contains(v.fst) || filterVars.contains(v.fst)) {
				t = t==null? v.fst: t.product(v.fst);
				if (!lhsVars.contains(v.fst)) {
					Decl x = v.fst.oneOf(QuadTableRelations.nodes.union(NULL));
					d = d==null? x: d.and(x);
				}
			}
		}
		return t.in(tuples).and(filters).comprehension(d);
	}
	
	private Formula checkExists(Set<Variable> lhsVars, Set<Variable> leftStaticBinding, Expression leftDynamicBinding,
			Pair<Relation, List<Pair<Variable, Domain>>> rightRelation,
			boolean isNegated, boolean checkOverlap, Formula filters) {
		Expression rhs = rightRelation.fst;

		Set<Variable> filterVars = filters==null? Collections.<Variable>emptySet(): ASTUtils.gatherVariables(filters);
		
		List<IntConstant> indices = new LinkedList<IntConstant>();
		for(Pair<Variable,Domain> v : rightRelation.snd) {
			if (lhsVars.contains(v.fst) || filterVars.contains(v.fst)) {
				indices.add(IntConstant.constant(rightRelation.snd.indexOf(v)));
			}
		}
		
		if (indices.size() == 0) {
			if (checkOverlap) {
				return Formula.FALSE;
			} else {
				if (filters != null) {
					return isNegated? rhs.no().or(filters.not()): rhs.some().and(filters);
				} else {
					return isNegated? rhs.no(): rhs.some();
				}
			}
		} 
		
		rhs = rhs.project(indices.toArray(new IntConstant[ indices.size() ]));

		Formula overlap = null;
		Expression nullTuple = null;
		Expression tuple = null;
		for(Pair<Variable,Domain> v : rightRelation.snd) {
			if (lhsVars.contains(v.fst) || filterVars.contains(v.fst)) {
				if (leftStaticBinding.contains(v.fst) && !checkOverlap) {
					tuple = tuple==null? v.fst: tuple.product(v.fst);
				} else {
					nullTuple = nullTuple==null? NULL: nullTuple.product(NULL);
					Formula isBound = varExpr(v.fst).in(leftDynamicBinding);
					Expression elt = isBound.thenElse(checkOverlap? v.fst.union(NULL): v.fst, QuadTableRelations.nodes.union(NULL));
					tuple = tuple==null? elt: tuple.product(elt);					
					overlap = overlap==null? isBound: overlap.or(isBound);
				}
			}
		}

		Formula rf;
		Expression tuples;
		if (checkOverlap) {
			tuples = tuple.intersection(rhs).difference(nullTuple);
		} else {
			tuples = tuple.intersection(rhs);
		}
		
		if (filters != null) {
			tuples = restrictExistsWithFilter(lhsVars, rightRelation, filters, tuples);
		}
		
		if (checkOverlap) {
			rf = tuples.some().and(overlap);
		} else {
			rf = tuples.some();
		}

		if (isNegated) {
			rf = rf.not();
		}
		
		return rf;
	}

	private Relation typeRelation(String typeName) {
		return universe.atomRelation(ExpressionUtil.typeURI(typeName));
	}
	
	private Formula isType(Expression type, String typeName) {
		return type.intersection(typeRelation(typeName)).one();
	}

	private Formula isDoubleType(Expression type) {
		return isType(type, ExpressionUtil.xsdDoubleType);
	}

	private Formula isFloatType(Expression type) {
		return isType(type, ExpressionUtil.xsdFloatType);
	}

	private Formula isDecimalType(Expression type) {
		return isType(type, ExpressionUtil.xsdDecimalType);
	}

	private Formula isIntegerType(Expression type) {
		return isType(type, ExpressionUtil.xsdIntegerType);
	}

	private Formula isNumericType(Expression type) {
		return isIntegerType(type).or(isSomeFloatType(type));
	}

	private Formula isSomeFloatType(Expression type) {
		return isDecimalType(type)
		.or(isFloatType(type))
		.or(isDoubleType(type));
	}
	private Formula isBooleanType(Expression type) {
		return isType(type, ExpressionUtil.xsdBooleanType);
	}

	private Expression promoteType(Expression type1, Expression type2) {
		return isNumericType(type1).and(isNumericType(type2)).thenElse(
			promoteNumericType(type1, type2),
			NULL);
	}
	
	private Expression promoteNumericType(Expression type1, Expression type2) {
		return isDoubleType(type1).or(isDoubleType(type2))
			.thenElse(
				typeRelation(ExpressionUtil.xsdDoubleType),
				isFloatType(type1).or(isFloatType(type2))
					.thenElse(
						typeRelation(ExpressionUtil.xsdFloatType), 
						isDecimalType(type1).or(isDecimalType(type2))
							.thenElse(
								typeRelation(ExpressionUtil.xsdDecimalType), 
								typeRelation(ExpressionUtil.xsdIntegerType))));
	}
	
	interface ExpressionContext {
		Formula guard();
		
		Expression type();
		
		Formula booleanValue();
		
		Expression value();
		
		IntExpression intValue();
		
		IntExpression floatValue();
	}

	abstract class DefaultExpressionContext implements ExpressionContext {
		public Formula guard() {
			return Formula.TRUE;
		}
		
		public Formula booleanValue() {
//			return value().eq(universe.atomRelation(TRUE()));
			return ebv(this);
		}
		
		public IntExpression intValue() {
			return value().join(literalValues).sum();
		}
		
		public IntExpression floatValue() {
			return 
			  isSomeFloatType(type()).thenElse(intValue(), intToFloat(intValue()));
		}
	}

	private ExpressionContext handleExpression(Expr e) {
		return new ExprVisitor() {
			private final Set<String> scope = JenaUtil.scope(context.scope());

			private ExpressionContext currentExpr = null;
			
			private ExpressionContext visit(Expr e) {
				e.visit(this);
				ExpressionContext ret = currentExpr;
				currentExpr = null;
				return ret;
			}

			private void ret(final Formula f, final Formula guard) {
				currentExpr = new DefaultExpressionContext() {
					@Override
					public Expression type() {
						return typeRelation(xsdBooleanType);
					}
					@Override
					public Formula booleanValue() {
						return f;
					}
					@Override
					public Expression value() {
						return f.thenElse(universe.atomRelation(TRUE()), universe.atomRelation(FALSE()));
					}
					@Override
					public Formula guard() {
						return guard;
					}
				};
			}

			private void ret(final Expression e, final Expression type, final Formula guard) {
				currentExpr = new DefaultExpressionContext() {
					@Override
					public Expression type() {
						return type;
					}
					@Override
					public Expression value() {
						return e;
					}
					@Override
					public Formula guard() {
						return guard;
					}					
				};			
			}
			
			private void ret(final IntExpression e, final Expression type, final Formula guard) {
				currentExpr = new DefaultExpressionContext() {
					@Override
					public Expression type() {
						return type;
					}
					@Override
					public IntExpression intValue() {
						return e;
					}
					@Override
					public Expression value() {
						return e.toExpression();
					}
					@Override
					public Formula guard() {
						return guard;
					}
				};			
			}
			
			@Override
			public void finishVisit() {
				// do nothing
			}

			@Override
			public void startVisit() {
				// do nothing
			}

			@Override
			public void visit(ExprFunction0 arg0) {
				assert false;					
			}

			@Override
			public void visit(ExprFunction1 e) {
				ExpressionContext v = visit(e.getArg());
				if (e instanceof E_Bound) {
					ret(v.value().eq(NULL).not(), Formula.TRUE);
				} else if (e instanceof E_Lang) {
					ret(v.type().intersection(QuadTableRelations.literalLanguages.project(IntConstant.constant(1))).one().thenElse(
							v.type(), 
							universe.atomRelation(EMPTY())),
						typeRelation(ExpressionUtil.xsdStringType), 
						v.guard().and(isLiteral(v.value())));	
				} else if (e instanceof E_LogicalNot) {
					ret(ebv(v).not(), v.guard().and(validEBV(v)));
				} else if (e instanceof E_IsNumeric) {
					ret(isNumeric(v.value()), v.guard());
				} else if (e instanceof E_IsLiteral) {
					ret(isLiteral(v.value()), v.guard());
				} else if (e instanceof E_IsIRI) {
					ret((isLiteral(v.value()).or(isBlank(v.value()))).not(), v.guard());
				} else if (e instanceof E_Datatype) {
					ret(isSimple(v.value()).thenElse(
							typeRelation(ExpressionUtil.xsdStringType), 
							v.value().join(QuadTableRelations.literalLanguages).one().thenElse(
									typeRelation(ExpressionUtil.rdfLangStringType),
									v.type())), 
						universe.atomRelation(ExpressionUtil.xsdStringType), 
						v.guard().and(isLiteral(v.value()).or(isLanguage(v.value()))));
				} else if (e instanceof E_UnaryMinus) {
					ret(isSomeFloatType(v.type()).thenElse(
							floatMinus(v.floatValue()), 
							minus(v.intValue())),
						v.type(),
						v.guard().and(isNumericType(v.type())));
				} else if (e instanceof E_IsBlank) {
					ret(v.value().in(QuadTableRelations.blankNodes), v.guard());
				} else {
					assert false : e;
				}	
			}
			
			@Override
			public void visit(ExprFunction2 e) {
				ExpressionContext arg1 = visit(e.getArg1());
				ExpressionContext arg2 = visit(e.getArg2());
				Expression type = promoteType(arg1.type(), arg2.type());
				if (e instanceof E_Equals) {
					ret( (isIntegerType(type).and(arg1.intValue().eq(arg2.intValue()))).or(
							isSomeFloatType(type).and(floatCompare(arg1.floatValue(), arg2.floatValue()).eq(zero)).or(
									equal_test(arg1.value(), arg2.value()))),
									arg1.guard().and(arg2.guard()));
				} else if (e instanceof E_NotEquals) {
					ret( (isIntegerType(type).and(arg1.intValue().eq(arg2.intValue()).not())).or(
							isSomeFloatType(type).and(floatCompare(arg1.floatValue(), arg2.floatValue()).eq(zero)).or(
									not_equal_test(arg1.value(), arg2.value()))),
									arg1.guard().and(arg2.guard()) );
				} else if (e instanceof E_LessThan) {
					ret( (isIntegerType(type).and(arg1.intValue().lt(arg2.intValue()))).or(
							isSomeFloatType(type).and(floatCompare(arg1.floatValue(), arg2.floatValue()).lt(zero))).or(
									string_less(arg1.value(), arg2.value())),
									arg1.guard().and(arg2.guard()).and(isNumericType(type).or(isStringOrSimple(arg1.value()).and(isStringOrSimple(arg2.value())))) );
				} else if (e instanceof E_LessThanOrEqual) {
					ret( (isIntegerType(type).and(arg1.intValue().lte(arg2.intValue()))).or(
							isFloatType(type).and(floatCompare(arg1.floatValue(), arg2.floatValue()).lte(zero))),
							isNumericType(type) );
				} else if (e instanceof E_GreaterThan) {
					ret( (isIntegerType(type).and(arg1.intValue().gt(arg2.intValue()))).or(
							isSomeFloatType(type).and(floatCompare(arg1.floatValue(), arg2.floatValue()).gt(zero)).or(
									string_greater(arg1.value(), arg2.value()))),
									isNumericType(type).or(isStringOrSimple(arg1.value()).and(isStringOrSimple(arg2.value()))) );
				} else if (e instanceof E_GreaterThanOrEqual) {
					ret( (isIntegerType(type).and(arg1.intValue().gte(arg2.intValue()))).or(
							isSomeFloatType(type).and(floatCompare(arg1.floatValue(), arg2.floatValue()).gte(zero))),
							isNumericType(type) );
				} else if (e instanceof E_LogicalAnd) {
					Formula lv = ebv(arg1);
					Formula rv = ebv(arg2);
					Formula g1 = arg1.guard();
					Formula g2 = arg2.guard();
					ret(lv.and(rv), g1.and(g2).or(lv.not().implies(g1)).or(rv.not().implies(g2)));
				} else if (e instanceof E_LogicalOr) {
					Formula lv = ebv(arg1);
					Formula rv = ebv(arg2);
					Formula g1 = arg1.guard();
					Formula g2 = arg2.guard();
					ret(lv.or(rv), g1.and(g2).or(lv.implies(g1)).or(rv.implies(g2)));
				} else if (e instanceof E_Add) {
					ret(isIntegerType(type).thenElse(
							plus(arg1.intValue(), arg2.intValue()),				
							floatAdd(arg1.floatValue(), arg2.floatValue())),
							type,
							isNumericType(type).and(arg1.guard()).and(arg2.guard()));
				} else if (e instanceof E_Subtract) {
					ret(isIntegerType(type).thenElse(
							minus(arg1.intValue(), arg2.intValue()),				
							floatMinus(arg1.floatValue(), arg2.floatValue())),
							type,
							isNumericType(type));
				} else if (e instanceof E_Multiply) {
					ret(isIntegerType(type).thenElse(
							times(arg1.intValue(), arg2.intValue()),				
							floatMultiply(arg1.floatValue(), arg2.floatValue())),
							type,
							isNumericType(type));
				} else if (e instanceof E_Divide) {
					ret(isIntegerType(type).thenElse(
							divide(arg1.intValue(), arg2.intValue()),				
							floatDivide(arg1.floatValue(), arg2.floatValue())),
							type,
							isNumericType(type).and(arg2.intValue().eq(zero).not()));
				} else if (e instanceof E_SameTerm) {
					ret(arg1.value().eq(arg2.value()), Formula.TRUE);
				} else {
					assert false : e;
				}
			}

			@Override
			public void visit(ExprFunction3 arg0) {
				assert false : arg0.getOpName();					
			}

			@Override
			public void visit(ExprFunctionN arg0) {
				String iri = arg0.getFunctionIRI();
				Expression v = null;
				Expression type = null;
				if (arg0 instanceof E_Coalesce) {
					List<Expr> args = new ArrayList<Expr>(((E_Coalesce)arg0).getArgs());
					Collections.reverse(args);
					for(Expr e : args) {
						ExpressionContext ec = handleExpression(e);
						Expression ev = ec.value();
						Expression et = ec.type();
						Formula cond = ec.guard().and(ev.eq(NULL).not());
						v = v==null? ev: cond.thenElse(ev, v);
						type = type==null? et: cond.thenElse(et, type);
					}
					ret(v, type, Formula.TRUE);
				} else if (ExpressionUtil.xsdBooleanType.equals(iri)) {
					ExpressionContext arg = visit(arg0.getArg(1));
					ret(castToBoolean(arg), universe.atomRelation(ExpressionUtil.xsdBooleanType), arg.guard().and(validEBV(arg)));
				} else {
					assert false : iri;	
				}
			}

			@Override
			public void visit(ExprFunctionOp arg0) {
				if (arg0 instanceof E_Exists || arg0 instanceof E_NotExists) {
					Set<Variable> leftStaticBinding = context.getStaticBinding();
					Expression leftDynamicBinding = context.getDynamicBinding();
					
					Pair<Relation, List<Pair<Variable, Domain>>> rightRelation = reifyOpAsRelation(arg0.getGraphPattern(), context);
					boolean isNegated = arg0 instanceof E_NotExists;

					Formula rf;
					if (rightRelation == null) {
						TranslatorContext save = context;
						context = new DomainContext(save) {
							private Formula existsQuery;
							
							@Override
							public Formula getCurrentQuery() {
								return existsQuery;
							}

							@Override
							public void setCurrentQuery(Formula currentQuery) {
								existsQuery = currentQuery;
							}
						};
						JenaTranslator.this.visit(arg0.getGraphPattern(), (TranslatorContext context1, Formula rf1) -> {
							if (isNegated) {
								rf1 = rf1.not();
							}
							context = save;
							ret(rf1, Formula.TRUE);
							
							context.setStaticBinding(leftStaticBinding);
							context.setDynamicBinding(leftDynamicBinding);
							context.getCurrentContinuation().next(context, rf1);
						});
					} else {
						rf = checkExists(ASTUtils.gatherVariables(context.getCurrentQuery()), leftStaticBinding, leftDynamicBinding, rightRelation, isNegated, false, null);
					
						ret(rf, Formula.TRUE);
			
						context.setStaticBinding(leftStaticBinding);
						context.setDynamicBinding(leftDynamicBinding);
					}
				} else {
					assert false : arg0.getFunctionIRI();
				}
			}

			@Override
			public void visit(NodeValue arg0) {
				ret(toTerm(arg0.getNode()), toType(arg0.getNode()), Formula.TRUE);
			}

			@Override
			public void visit(ExprVar arg0) {
				if (! scope.contains(arg0.getVarName())) {
					System.err.println("out of scope " + arg0.getVarName());
				}
				ret(toTerm(arg0.getAsNode()), toType(arg0.getAsNode()), scope.contains(arg0.getVarName())? Formula.TRUE: Formula.FALSE);
			}

			@Override
			public void visit(ExprAggregator arg0) {
				currentExpr = visit(arg0.getAggregator().getExpr());		
			}
		}.visit(e);
	}

	private void visit(Op op, Continuation next) {
		assert context.getCurrentQuery() == null;
		if (context.getCurrentContinuation() == null) {
			context.setCurrentContinuation(next);
		} else {
			Continuation c = context.getCurrentContinuation();
			context.setCurrentContinuation((TranslatorContext cc, Formula ff) -> {
				context.setCurrentContinuation(c);
				next.next(cc, ff);
			});
		}
		
		if (op != context.top()) {
			Set<String> privateVars = privateVariableNames(op, context.top());
			if (! privateVars.isEmpty()) {
				System.err.println("private vars " + privateVars + " for " + op);
			}
		}	
		
		op.visit(this);
	}

	@Override
	public void visit(OpBGP arg0) {
		for(Triple t : arg0.getPattern().getList()) {
			conj(handleTriple(t));
		}
		doStandardBindings(context.getCurrentQuery(), arg0);
		context.getCurrentContinuation().next(context, context.getCurrentQuery());
	}

	@Override
	public void visit(OpQuadPattern arg0) {
		for(Quad q : arg0.getPattern().getList()) {
			conj(handleQuad(q));
		}
		doStandardBindings(context.getCurrentQuery(), arg0);
		context.getCurrentContinuation().next(context, context.getCurrentQuery());
	}

	@Override
	public void visit(OpQuadBlock arg0) {
		for(Quad q : arg0.getPattern().getList()) {
			conj(handleQuad(q));
		}
		doStandardBindings(context.getCurrentQuery(), arg0);
		context.getCurrentContinuation().next(context, context.getCurrentQuery());
	}
	
	@Override
	public void visit(OpTriple arg0) {
		context.setCurrentQuery(doStandardBindings(handleTriple(arg0.getTriple()), arg0));
		context.getCurrentContinuation().next(context, context.getCurrentQuery());
	}

	@Override
	public void visit(OpQuad arg0) {
		context.setCurrentQuery(doStandardBindings(handleQuad(arg0.getQuad()), arg0));
		context.getCurrentContinuation().next(context, context.getCurrentQuery());
	}

	@Override
	public void visit(final OpPath arg0) {
		Expression relation = new PathVisitor() {
			private Expression result;
						
			{
				arg0.getTriplePath().getPath().visit(this);
			}
			
			private Expression toSet(List<Node> l) {
				Expression set = null;
				for(Node n : l) {
					set = (set == null)? toTerm(n): set.union(toTerm(n));
				}
				return set;
			}

			private Expression zeroPath(Expression e) {
				return context.getActiveGraph().join(QuadTableRelations.quads).project(IntConstant.constant(0)).union(context.getActiveGraph().join(QuadTableRelations.quads).project(IntConstant.constant(2))).project(IntConstant.constant(0), IntConstant.constant(0));
			}

			private void visitLink(P_Path0 arg0) {
				Expression p = toTerm(arg0.getNode());
				int s = arg0.isForward()? 1: 3;
				int o = arg0.isForward()? 3: 1;
				result = QuadTableRelations.quads.project(IntConstant.constant(s), IntConstant.constant(o), IntConstant.constant(2), IntConstant.constant(0)).join(context.getActiveGraph()).join(p);
			}

			@Override
			public void visit(P_Link arg0) {
				visitLink(arg0);
			}

			@Override
			public void visit(P_ReverseLink arg0) {
				visitLink(arg0);
			}
			
			@Override
			public void visit(P_NegPropSet arg0) {
				Expression rel = null;
				if (! arg0.getFwdNodes().isEmpty()) {
					Expression fps = QuadTableRelations.quads.project(IntConstant.constant(2)).difference(toSet(arg0.getFwdNodes()));
					rel = QuadTableRelations.quads.project(IntConstant.constant(1), IntConstant.constant(3), IntConstant.constant(2)).join(fps);
				}
				if (! arg0.getBwdNodes().isEmpty()) {
					Expression bps = QuadTableRelations.quads.project(IntConstant.constant(2)).difference(toSet(arg0.getBwdNodes()));
					Expression x = QuadTableRelations.quads.project(IntConstant.constant(3), IntConstant.constant(1), IntConstant.constant(2)).join(bps);
					rel = (rel == null)? x: rel.union(x);
				}
				result = rel;
			}

			@Override
			public void visit(P_Inverse arg0) {
				arg0.getSubPath().visit(this);
				result = result.project(IntConstant.constant(1), IntConstant.constant(0));
			}

			@Override
			public void visit(P_Mod arg0) {
				assert false;
			}

			@Override
			public void visit(P_FixedLength arg0) {
				assert false;
			}

			@Override
			public void visit(P_Distinct arg0) {
				assert false;
			}

			@Override
			public void visit(P_Multi arg0) {
				assert false;
			}

			@Override
			public void visit(P_Shortest arg0) {
				assert false;
			}

			@Override
			public void visit(P_ZeroOrOne arg0) {
				arg0.getSubPath().visit(this);
				result = result.union(zeroPath(result));
			}

			@Override
			public void visit(P_ZeroOrMore1 arg0) {
				arg0.getSubPath().visit(this);
				result = result.closure().union(zeroPath(result));
			}

			@Override
			public void visit(P_ZeroOrMoreN arg0) {
				assert false;
			}

			@Override
			public void visit(P_OneOrMore1 arg0) {
				arg0.getSubPath().visit(this);
				result = result.closure();
			}

			@Override
			public void visit(P_OneOrMoreN arg0) {
				assert false;				
			}

			@Override
			public void visit(P_Alt arg0) {
				arg0.getLeft().visit(this);
				Expression l = result;
				
				arg0.getRight().visit(this);
				Expression r = result;
				
				result = l.union(r);
			}

			@Override
			public void visit(P_Seq arg0) {
				arg0.getLeft().visit(this);
				Expression left = result;

				arg0.getRight().visit(this);
				Expression right = result;

				result = left.join(right);
			}
		}.result;
		
		Expression s = constrainDomain(toTerm(arg0.getTriplePath().getSubject()), new Or(Leaf.OBJECT, Leaf.SUBJECT));
		Expression o = constrainDomain(toTerm(arg0.getTriplePath().getObject()), new Or(Leaf.OBJECT, Leaf.SUBJECT));
		context.setCurrentQuery(doStandardBindings(s.product(o).in(relation), arg0));
		context.getCurrentContinuation().next(context, context.getCurrentQuery());
	}

	@Override
	public void visit(OpFilter arg0) {
		Continuation next = context.getCurrentContinuation();
		visit(arg0.getSubOp(), (TranslatorContext context, Formula f) -> {
			context.setCurrentQuery(f);

			for(Expr e : arg0.getExprs()) {
				ExpressionContext val = handleExpression(e);
				f = f.and(val.guard()).and(ebv(val));
			}
			context.setCurrentQuery(f);
			next.next(context, f);			
		});
	}


	@Override
	public void visit(OpGraph arg0) {
		final Expression g = toTerm(arg0.getNode());
		
		constrainDomain(g, Leaf.GRAPH);
		
		TranslatorContext save = context;
		context = new DelegatingContext(save) {
			@Override
			public Expression getActiveGraph() {
				return g;
			}
		};
		
		visit(arg0.getSubOp(), (TranslatorContext context, Formula f) -> {

			context = save;
			context.setCurrentQuery(f.and(g.eq(universe.atomRelation(QuadTableRelations.defaultGraph)).not()));
		});
	}

	private final Map<Pair<Op,String>, Variable> choiceVariables = HashMapFactory.make();
	
	private Formula choose(Variable choice) {
		return universe.atomRelation(0).eq(choice);
	}
	
	private Variable choice(Op arg0, String name) {
		Pair<Op, String> key = Pair.make(arg0, name);
		if (! choiceVariables.containsKey(key)) {
			Variable x = Variable.unary(name + choiceVariables.size());
			choiceVariables.put(key, x);
		}
		Variable choice = choiceVariables.get(key);
		return choice;
	}
	
	@Override
	public void visit(OpUnion arg0) {
		class UnionContext extends DomainContext {
			private final Set<Variable> staticBinding;
			private Expression dynamicBinding;
			private Formula query;
			private UnionContext(TranslatorContext parent) {
				super(parent);
				staticBinding = parent.getStaticBinding()==null? HashSetFactory.<Variable>make(): HashSetFactory.make(parent.getStaticBinding());
				dynamicBinding = parent.getDynamicBinding();
			}

			@Override
			public Expression getDynamicBinding() {
				return dynamicBinding;
			}

			@Override
			public void setDynamicBinding(Expression dynamicBinding) {
				this.dynamicBinding = dynamicBinding;
			}

			@Override
			public Set<Variable> getStaticBinding() {
				return staticBinding;
			}

			@Override
			public void setStaticBinding(Set<Variable> staticBinding) {
				this.staticBinding.addAll(staticBinding);
			}

			@Override
			public Formula getCurrentQuery() {
				return query;
			}

			@Override
			public void setCurrentQuery(Formula currentQuery) {
				query = currentQuery;
			}
		};

		TranslatorContext save = context;
		Continuation c = save.getCurrentContinuation();
		TranslatorContext scope = new ScopeContext(save, arg0);

		TranslatorContext left = context = new UnionContext(scope);

		if (context.explicitChoices()) {
			visit(arg0.getLeft(), (TranslatorContext context1, Formula l) -> {
				context1.setCurrentQuery(l);
				c.next(context1, l);
			});

			TranslatorContext right = context = new UnionContext(scope);
			visit(arg0.getRight(), (TranslatorContext context1, Formula r) -> {
				context1.setCurrentQuery(r);
				c.next(context1, r);
			});

		} else {
			visit(arg0.getLeft(), (TranslatorContext context1, Formula l) -> {

				TranslatorContext right = context = new UnionContext(scope);
				visit(arg0.getRight(), (TranslatorContext context2, Formula r) -> {

					context = save;

					Variable choice = choice(arg0, "union");
					context.setDomain(choice, Leaf.CHOICE);

					Set<Variable> newBindings = HashSetFactory.make(left.getStaticBinding());
					newBindings.retainAll(right.getStaticBinding());
					newBindings.add(choice);
					context.setStaticBinding(newBindings);

					Expression nb = 
							l.thenElse(
									r.thenElse(
											choose(choice).thenElse(left.getDynamicBinding(), right.getDynamicBinding()), 
											left.getDynamicBinding()), 
									right.getDynamicBinding());
					context.setDynamicBinding(nb);

					context.setCurrentQuery(l.or(r));


					for(Variable v : context.getVars().values()) {
						Domain n = union(left.getDomain(v), right.getDomain(v));
						if (n != null) {
							if (context.getDomain(v) != null) {
								context.setDomain(v, new And(n, context.getDomain(v)));
							} else {
								context.setDomain(v, n);
							}
						}	 
					}

					//				context.getCurrentContinuation().next(context, context.getCurrentQuery());
				});
			});
		}
	}

	private Domain union(Domain l, Domain r) {
		if (l != null) {
			if (r != null) {
				return new Or(r, new Or(l, Leaf.NULL));
			} else {
				return new Or(l, Leaf.NULL);
			}
		} else {
			if (r != null) {
				return new Or(r, Leaf.NULL);				
			} else {
				return null;
			}
		}
	}
	
	@Override
	public void visit(OpJoin arg0) {
		TranslatorContext save = context;
		context = new ScopeContext(save, arg0);
		
		visit(arg0.getLeft(), (TranslatorContext context1, Formula l) -> {	
			
			visit(arg0.getRight(), (TranslatorContext context2, Formula r) -> {
				
				context = save;
				context.setCurrentQuery(l.and(r));
				
				context.getCurrentContinuation().next(save, context.getCurrentQuery());
			});
		});
	}

	private final Set<Formula> relationBindings = HashSetFactory.make();
	private final Map<Op,Pair<Relation,List<Pair<Variable,Domain>>>> relations = HashMapFactory.make();
	
	
	private Pair<Relation,List<Pair<Variable,Domain>>> reifyOpAsRelation(Op rhs, TranslatorContext context) {
		List<Variable> rvs = getVariables(rhs, context);		
		
		System.err.println(rhs + " -- " + context.top());

		Set<String> privateNames = privateVariableNames(rhs, context.top());
		for(Iterator<Variable> vs = rvs.iterator(); vs.hasNext(); ) {
			if (privateNames.contains(vs.next().name())) {
				vs.remove();
			}
		}

		Set<String> constants = context.constants().keySet();
		for(Iterator<Variable> vs = rvs.iterator(); vs.hasNext(); ) {
			if (constants.contains(vs.next().name())) {
				vs.remove();
			}
		}

		rvs = sortVars(rvs);
		
		return reifyOpAsRelation(rhs, rvs);
	}
	
	private Pair<Relation,List<Pair<Variable,Domain>>> reifyOpAsRelation(Op rhs, Collection<Variable> neededVars) {
		if (! relations.containsKey(rhs)) {
			Set<String> rvs = gatherVariableNames(Collections.singleton(rhs));	
			if (context.constants() != null) {
				rvs.removeAll(context.constants().keySet());
			}
			
			final Map<String,Variable> newVars = HashMapFactory.make();
			for(Map.Entry<String,Variable> var : context.getVars().entrySet()) {
				if (rvs.contains(var.getKey())) {
					newVars.put(var.getKey(), var.getValue()); 
				}
			}
		
			TranslatorContext save = context;
			
			context = new BaseTranslatorContext(newVars, context.constants(), context.getActiveGraph(), context.top(), context.explicitChoices());
			visit(rhs, (TranslatorContext context, Formula nr) -> {
				Set<Variable> rhsVars = ASTUtils.gatherVariables(nr);
				neededVars.retainAll(rhsVars);
				if (rhsVars.isEmpty() || neededVars.isEmpty()) {
					context = save;
					return;
				}
				Relation opRelation = Relation.nary("rel" + relationBindings.size(), neededVars.size());
				Expression rel = scope(nr, context.getDynamicBinding(), neededVars);
				// System.err.println(rel);
				relationBindings.add(opRelation.eq(rel));
				universe.nodesRelation(opRelation);
			
				List<Pair<Variable,Domain>> vars = new LinkedList<Pair<Variable,Domain>>();
				for(Variable v : neededVars) {
					vars.add(Pair.make(v, context.getDomain(v)));
				}
			
				relations.put(rhs, Pair.make(opRelation, vars));
			
				System.err.println(relations.get(rhs));
			
				context = save;
			});
		}
		
		return relations.get(rhs);
	}
	
	private Formula joinWithReified(Map<String, Variable> lhsVars, Set<Variable> leftStaticBinding, Expression leftDynamicBinding, Op op, Pair<Relation,List<Pair<Variable,Domain>>> rhs) {
		Set<String> privateVarNames = privateVariableNames(op, context.top());
		lhsVars = HashMapFactory.make(lhsVars);
		for(String name : privateVarNames) {
			lhsVars.remove(name);
		}
		
		List<IntConstant> indices = new LinkedList<IntConstant>();
		for(Pair<Variable,Domain> v : rhs.snd) {
			if (lhsVars.values().contains(v.fst)) {
				indices.add(IntConstant.constant(rhs.snd.indexOf(v)));
			}
		}
				
		if (indices.isEmpty()) {
			return rhs.fst.some();
		}
		
		Expression rhsRelation = rhs.fst.project(indices.toArray(new IntConstant[ indices.size() ]));

		Expression tuple = null;
		for(Pair<Variable,Domain> v : rhs.snd) {
			context.setDomain(v.fst, context.getDomain(v.fst)!= null? new And(context.getDomain(v.fst), v.snd): v.snd);
			if (lhsVars.values().contains(v.fst)) {
				tuple = tuple==null? v.fst: tuple.product(v.fst);
			}
		}
	
		Expression join = tuple.intersection(rhsRelation);

		Expression dynamicBinding = context.getDynamicBinding();
		for(Pair<Variable,Domain> v : rhs.snd) {
			dynamicBinding = join.project(IntConstant.constant(rhs.snd.indexOf(v))).eq(NULL).thenElse(dynamicBinding, dynamicBinding.union(varExpr(v.fst)));
		}
		context.setDynamicBinding(dynamicBinding);
		
		return join.some();
	}
		
	@Override
	public void visit(OpLeftJoin arg0) {		
		TranslatorContext outerSave = context;
		context = new ScopeContext(outerSave, arg0);

		visit(arg0.getLeft(), (TranslatorContext context1, Formula l) -> {
			Set<Variable> lhsVars = ASTUtils.gatherVariables(l);

			Set<Variable> leftStaticBinding = HashSetFactory.make(context.getStaticBinding());
			Expression leftDynamicBinding = context.getDynamicBinding();
			System.err.println(context.getDynamicBinding());
		
			final TranslatorContext save = context;
			context = new DomainContext(context);
			visit(arg0.getRight(), (TranslatorContext context2, Formula r) -> {
				Expression rightDynamicBinding = context.getDynamicBinding();

				Formula filters = null;
				if (arg0.getExprs() != null) {
					for(Expr e : arg0.getExprs()) {
						ExpressionContext ec = handleExpression(e);
						Formula x = ec.booleanValue().and(ec.guard());
						filters = filters==null? x: filters.and(x);
					}
					assert filters != null;
				}

				Set<Variable> neededVars = HashSetFactory.make(lhsVars);
				if (filters != null) {
					neededVars.addAll(ASTUtils.gatherVariables(filters));
				}
				neededVars.retainAll(ASTUtils.gatherVariables(r));

				Pair<Relation, List<Pair<Variable, Domain>>> rhs = reifyOpAsRelation(arg0.getRight(), context);
				
				context = save;
				
				Formula both = r;
				if (filters != null) {
					both = both.and(filters);
				}

				Formula leftOnly;
				if (rhs != null) {
					leftOnly = checkExists(lhsVars, leftStaticBinding, leftDynamicBinding, rhs, true, false, filters);
				} else {
					Set<Variable> rvs = ASTUtils.gatherVariables(r);
					if (rvs.isEmpty()) {
						leftOnly = r.not();
					} else {
						leftOnly = existentialScope(rvs, r, rightDynamicBinding).not();
					}
					if (filters != null) {
						leftOnly = leftOnly.or(filters.not());
					}
				}
				
				context = outerSave;
		
				context.setStaticBinding(leftStaticBinding);
				context.setDynamicBinding(both.thenElse(rightDynamicBinding, leftDynamicBinding));
				context.setCurrentQuery(l.and(both.or(leftOnly)));
				
				context.getCurrentContinuation().next(context, context.getCurrentQuery());
			});
		});
	}
	
	@Override
	public void visit(OpMinus arg0) {	
		visit(arg0.getLeft(), (TranslatorContext context, Formula l) -> {
			Set<Variable> leftStaticBinding = HashSetFactory.make(context.getStaticBinding());
			Expression leftDynamicBinding = context.getDynamicBinding();
			System.err.println(context.getDynamicBinding());
		
			Pair<Relation, List<Pair<Variable, Domain>>> r = reifyOpAsRelation(arg0.getRight(), new ScopeContext(context, arg0.getRight()));
		
			context.setStaticBinding(leftStaticBinding);
			context.setDynamicBinding(leftDynamicBinding);
			context.setCurrentQuery(l.and(checkExists(ASTUtils.gatherVariables(l), leftStaticBinding, leftDynamicBinding, r, true, true, null)));
			
			context.getCurrentContinuation().next(context, context.getCurrentQuery());
		});
	}

	@Override
	public void visit(OpExtend arg0) {
		visit(arg0.getSubOp(), (TranslatorContext context, Formula f) -> {
			for(Map.Entry<Var,Expr> ve : arg0.getVarExprList().getExprs().entrySet()) {
				Variable v = context.getVars().get(ve.getKey().getName());
				ExpressionContext expr = handleExpression(ve.getValue());

				Formula bound = 
						expr.guard().and( (isSomeFloatType(expr.type()).and(intValue(v).eq(expr.floatValue())))
								.or(isIntegerType(expr.type()).and(intValue(v).eq(expr.intValue())))
								.or(v.eq(expr.value())));

				Formula notBound = expr.guard().not().and(v.eq(NULL));

				f = f.and(bound.or(notBound));

				Expression bind = context.getDynamicBinding();
				if (bind == null) {
					bind = Expression.NONE;
				}
				context.setDynamicBinding(expr.guard().thenElse(bind.union(varExpr(v)), bind));

				context.setDomain(v, Leaf.ANY);
			}
			context.setCurrentQuery(f);

			context.getCurrentContinuation().next(context, context.getCurrentQuery());
		});
	}

	@Override
	public void visit(OpTable arg0) {	
		Set<Variable> vars = HashSetFactory.make();
		Formula f = null;
		for(Iterator<Binding> bs = arg0.getTable().rows(); bs.hasNext(); ) {
			Formula rf = null;
			Binding b = bs.next();
			for(Var jv : arg0.getTable().getVars()) {
				if (b.get(jv) != null) {
					Expression value = toTerm(b.get(jv));
					Variable var = context.getVars().get(jv.getName());
					vars.add(var);
					Formula ef = var.eq(value);
					rf = rf==null? ef: rf.and(ef);
				}
			}
			f = f==null? rf: f.or(rf);
		}
		
		context.setCurrentQuery(f==null? Formula.TRUE: f);
	
		if (context.getStaticBinding() != null) {
			context.getStaticBinding().addAll(vars);
		} else {
			context.setStaticBinding(vars);
		}
		
		Expression bound = null;
		for(Variable v : vars) {
			bound = bound==null? varExpr(v): bound.union(varExpr(v));
		}
		if (bound != null) {
			if (context.getDynamicBinding() != null) {
				context.setDynamicBinding(context.getDynamicBinding().union(bound));
			} else {
				context.setDynamicBinding(bound);
			}
		}
		
		context.getCurrentContinuation().next(context, context.getCurrentQuery());
	}

	@Override
	public void visit(OpNull arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OpProcedure arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OpPropFunc arg0) {
		// TODO Auto-generated method stub

	}
	@Override
	public void visit(OpService arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OpDatasetNames arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OpLabel arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OpAssign arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OpDiff arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OpConditional arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OpSequence arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OpDisjunction arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OpExt arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OpList arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OpSlice arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OpGroup arg0) {
		final TranslatorContext save = context;
		context = new DomainContext(context);
		visit(arg0.getSubOp(), (TranslatorContext context, Formula r) -> {
			Set<Variable> subStaticBinding = HashSetFactory.make(context.getStaticBinding());
			Expression subDynamicBinding = context.getDynamicBinding();
			context = save;

			Pair<Relation, List<Pair<Variable, Domain>>> subRel = reifyOpAsRelation(arg0.getSubOp(), context);

			Expression tuple = null;
			for(Pair<Variable, Domain> x : subRel.snd) {
				tuple = tuple==null? x.fst: tuple.product(x.fst);
				context.setDomain(x.fst, x.snd);
			}

			Formula outer = Formula.TRUE;
			Formula inner = tuple.in(subRel.fst);

			Decls dd = null;
			for(Entry<Var, Expr> e : arg0.getGroupVars().getExprs().entrySet()) {
				Variable pv = (Variable) toTerm(e.getKey());
				if (e.getValue() != null) {
					ExpressionContext ec = handleExpression(e.getValue());
					//				Decl d = pv.oneOf(ec.value());
					//				dd = dd==null? d: dd.and(d);
					outer = outer.and(ec.guard()).and(pv.eq(ec.value()));
					context.setDomain(pv, Leaf.ANY);
					subStaticBinding.add(pv);
				}
			}

			grouped_vars: for(Pair<Variable, Domain> x : subRel.snd) {
				for(Var v : arg0.getGroupVars().getVars()) {
					Variable pv = (Variable) toTerm(v);
					if (x.fst.equals(pv)) {
						continue grouped_vars;
					}
				}
				for(Expr e : arg0.getGroupVars().getExprs().values()) {
					for(Var ev : e.getVarsMentioned()) {
						Variable pev = (Variable) toTerm(ev);
						if (x.fst.equals(pev)) {
							continue grouped_vars;
						}
					}
				}

				Decl d = x.fst.oneOf(nodes.union(NULL));
				dd = dd==null? d: dd.and(d);
			}

			Formula query = outer.and(dd != null? inner.forSome(dd): inner);

			for(ExprAggregator e : arg0.getAggregators()) {
				Variable agg = (Variable) toTerm(e.getAggVar().asVar());
				context.setDomain(agg, Leaf.ANY);
				subStaticBinding.add(agg);

				Expression table;
				if (e.getExpr() != null) {
					ExpressionContext ec = handleExpression(e.getExpr());

					Decls ad = agg.oneOf(nodes.union(NULL)).and(dd);

					table = inner.and(ec.guard()).and(ec.value().eq(agg)).comprehension(ad);
				} else {
					table = inner.comprehension(dd);
				}

				System.err.println("group=" + table);

				query = query.and(handleAggregator(e, table.project(IntConstant.constant(0))));
			}

			System.err.println("group query: " + query);

			context.setStaticBinding(subStaticBinding);
			context.setDynamicBinding(subDynamicBinding);
			//doStandardBindings(query, ASTUtils.gatherVariables(query));
			context.setCurrentQuery(query);

			context.getCurrentContinuation().next(context, context.getCurrentQuery());
		});
	}

	private Formula handleAggregator(ExprAggregator e, Expression table) {
		Expression lv = toTerm(e.getAggVar().asVar());
		Aggregator agg = e.getAggregator();
		if (agg instanceof AggSample) {
			return lv.in(table);
		} else if (agg instanceof AggMax) {
			Variable elt = Variable.unary("elt");
			return lv.in(table).and(lv.eq(elt).not().and(less_test(lv, elt)).forSome(elt.oneOf(table)).not());		
		} else if (agg instanceof AggMin) {
			Variable elt = Variable.unary("elt");
			return lv.in(table).and(lv.eq(elt).not().and(greater_test(lv, elt)).forSome(elt.oneOf(table)).not());		
		} else if (agg instanceof AggCount || agg instanceof AggCountVar || agg instanceof AggCountVarDistinct) {
			return intValue(lv).eq(table.count());
		} else {
			assert false : agg;
			return null;
		}
	}

	@Override
	public void visit(OpTopN arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OpProject arg0) {
		visit(arg0.getSubOp(), (TranslatorContext context, Formula f) -> {
			context.setCurrentQuery(f);
			context.getCurrentContinuation().next(context, f);
		});
	}

	@Override
	public void visit(OpReduced arg0) {
		visit(arg0.getSubOp(), (TranslatorContext context, Formula f) -> {
			context.setCurrentQuery(f);
			context.getCurrentContinuation().next(context, f);
		});
	}

	@Override
	public void visit(OpDistinct arg0) {
		visit(arg0.getSubOp(), (TranslatorContext context, Formula f) -> {
			context.setCurrentQuery(f);
			context.getCurrentContinuation().next(context, f);
		});
	}

	@Override
	public void visit(OpOrder arg0) {
		visit(arg0.getSubOp(), (TranslatorContext context, Formula f) -> {
			context.setCurrentQuery(f);
			context.getCurrentContinuation().next(context, f);
		});
	}

	public static abstract class TranslatorTest {
		protected final UniverseFactory universe;
		protected final Map<String,Variable> variables;
		protected final JenaTranslator xlator;
		protected Relation r;

		private static Map<String,Variable> makeVars(List<Variable> variables) {
			Map<String,Variable> result = HashMapFactory.make();
			for(Variable v : variables) {
				result.put(v.name(), v);
			}
			return result;
		}
		
		protected TranslatorTest(UniverseFactory universe, List<Variable> variables) {
			this.universe = universe;
			Map<String, Variable> varMap = makeVars(variables);
			this.variables = varMap;
			this.xlator = new JenaTranslator(varMap, variables, Collections.<Op>emptySet(), universe, null);
		}
		
		public static UniverseFactory getUniverse(final String path) throws SQLException, IOException, URISyntaxException {
			Set<DawgVerification> v = getDawgTest(path);
			assert v.size() == 1;
			return new DatasetUniverse(v.iterator().next().getDataset());
		}

		public static SolutionRelation getSolution(final String path) throws SQLException, IOException, URISyntaxException, ParserConfigurationException, SAXException {
			Set<DawgVerification> v = getDawgTest(path);
			assert v.size() == 1;
			SparqlSelectResult result = v.iterator().next().getResult();
			return new SolutionRelation(result, Iterator2Collection.toSet(result.variables()), Collections.<String,Object>emptyMap());
		}

		private static Set<DawgVerification> getDawgTest(final String path)
				throws SQLException, IOException {
			Set<DawgVerification> v = DawgUtil.dawgTests(new Predicate<QueryTest>() {
				@Override
				public boolean test(QueryTest arg0) {
					return arg0.getQuery().contains(path);
				}
			});
			return v;
		}
		
		public TranslatorTest(String test, List<Variable> variables) throws SQLException, IOException, URISyntaxException {
			this(getUniverse(test), variables);
		}
		
		protected Instance test(Expression e) throws URISyntaxException {
			r = Relation.nary("test_results", e.arity());
			return test(r.eq(e).and(e.some()));
		}
		
		protected Instance test(Formula qf) throws URISyntaxException {
			Set<Relation> liveRelations = ASTUtils.gatherRelations(qf);

			if (liveRelations.contains(literalDatatypes)) {
				liveRelations.add(nodes);
				qf = qf.and(literalDatatypes.partialFunction(nodes, nodes));
			}
			
			Bounds b = universe.boundUniverse(liveRelations);
	
			if (r != null) {
				TupleFactory tf = b.universe().factory();
				TupleSet all = tf.noneOf(1);
				for(Iterator<?> os = b.universe().iterator(); os.hasNext(); ) {
					all.add(tf.tuple(os.next()));
				}
				TupleSet base = all;
				for(int i = 1; i < r.arity(); i++) {
					all = all.product(base);
				}
				b.bound(r, all);
			}
			
			System.err.println(b);
			System.err.println(qf);

			Solver solver = new Solver();
			solver.options().setSolver(SATFactory.MiniSat);
			solver.options().setIntEncoding(IntEncoding.TWOSCOMPLEMENT);
			solver.options().setBitwidth(bitWidth);
			Solution s = solver.solve(qf, b);

			System.err.println(s);
			
			Assert.assertTrue(s.outcome() == Outcome.SATISFIABLE || s.outcome() == Outcome.TRIVIALLY_SATISFIABLE);

			if (r != null) {
				System.err.println("test result");
				for(Tuple t : s.instance().tuples(r)) {
					System.err.println(t);
				}
			}
		
			return s.instance();
		}
		
		protected ExpressionContext testContext(final Expression e) {
			return xlator.new DefaultExpressionContext() {
				@Override
				public Expression type() {
					return e.join(literalDatatypes);
				}

				@Override
				public Expression value() {
					return e;
				}
			};
		}
		
		protected Relation atomRelation(Instance answer, Object n1) {
			Relation r1 = universe.atomRelation(n1);
			if (! answer.contains(r1)) {
				answer.add(r1, answer.universe().factory().setOf(n1));
			}
			return r1;
		}
		
		public static class OpFilter1UnitTests extends TranslatorTest {
			protected OpFilter1UnitTests(UniverseFactory universe) throws SQLException, IOException, URISyntaxException {
				super(universe, Arrays.asList(Variable.unary("book"), Variable.unary("title"), Variable.unary("price")));
			}
			
			public OpFilter1UnitTests() throws SQLException, IOException, URISyntaxException {
				super("optional-filter/expr-1.rq", Arrays.asList(Variable.unary("book"), Variable.unary("title"), Variable.unary("price")));
			}
			
			@Test
			public void testType() throws URISyntaxException {
				Expression type = nodes.join(literalDatatypes);
				test(type);
			}

			@Test
			public void testValue() throws URISyntaxException {
				Expression type = nodes.join(literalValues);
				test(type);
			}

			@Test
			public void testNumericTypes() throws URISyntaxException {
				Expression type = nodes.join(literalDatatypes).intersection(numericTypes);
				test(type);
			}

			@Test
			public void testNumericValues() throws URISyntaxException {
				Variable price = variables.get("price");
				Expression type = price.join(literalDatatypes);
				Formula check = xlator.isNumericType(type);
				Expression x = check.comprehension(price.oneOf(QuadTableRelations.nodes));
				test(x.join(literalValues));
			}

			@Test
			public void testNumericLiterals() throws URISyntaxException {
				Variable price = variables.get("price");
				Variable val = Variable.unary("value");
				Expression type = price.join(literalDatatypes);
				Formula check = xlator.isNumericType(type);
				Expression x = check.comprehension(
					price.oneOf(QuadTableRelations.nodes).and(
					val.oneOf(price.join(literalValues))));
				test(x);
			}

			@Test
			public void testNumericLiteralValues() throws URISyntaxException {
				Variable price = variables.get("price");
				Variable val = Variable.unary("value");
				Expression type = price.join(literalDatatypes);
				Formula check = xlator.isNumericType(type);
				Expression x = check.comprehension(
					price.oneOf(QuadTableRelations.nodes).and(
					val.oneOf(price.join(literalValues).sum().toBitset())));
				test(x);
			}

			@Test
			public void testIsNumericType() throws URISyntaxException {
				Expression types = nodes.join(literalDatatypes);
				Variable price = variables.get("price");
				Expression x = xlator.isNumericType(price).comprehension(price.oneOf(types));
				test(x);
			}

			@Test
			public void testFilterType() throws URISyntaxException {
				Variable price = variables.get("price");
				Expression type = price.join(literalDatatypes);
				Formula check = xlator.isNumericType(type);
				Expression x = check.comprehension(price.oneOf(QuadTableRelations.nodes));
				test(x);
			}

			@Test
			public void testFilterValue() throws URISyntaxException {
				Variable price = variables.get("price");
				Expression type = price.join(literalDatatypes);
				Expression value = price.join(literalValues);
				Formula check = xlator.isNumericType(type).and(value.sum().gt(IntConstant.constant(0)));
				Expression x = check.comprehension(price.oneOf(QuadTableRelations.nodes));
				test(x);
			}

			@Test
			public void testPromote() throws URISyntaxException {
				Variable price1 = Variable.unary("price1");
				Expression type1 = price1.join(literalDatatypes);
				Variable price2 = Variable.unary("price2");
				Expression type2 = price2.join(literalDatatypes);
				Variable type3 = Variable.unary("type3");
				
				Formula check = xlator.isNumericType(type1).and(
					xlator.isNumericType(type2));
				
				Expression x = check.comprehension(
					price1.oneOf(QuadTableRelations.nodes).and(
					price2.oneOf(QuadTableRelations.nodes)).and(
					type3.oneOf(xlator.promoteType(type1, type2))));
							
				test(x);
			}

			@Test
			public void testContext() throws URISyntaxException {
				Variable price1 = Variable.unary("price1");
				ExpressionContext arg1 = testContext(price1);
						
				Formula check = xlator.isIntegerType(arg1.type());
				
				Expression x = check.comprehension(
				    price1.oneOf(QuadTableRelations.nodes));
							
				test(x);
			}

			@Test
			public void testContextCompareConstant() throws URISyntaxException {
				Variable price1 = Variable.unary("price1");
				ExpressionContext arg1 = testContext(price1);
						
				Formula check = xlator.isIntegerType(arg1.type()).and(arg1.intValue().gt(IntConstant.constant(10)));
				
				Expression x = check.comprehension(
				    price1.oneOf(QuadTableRelations.nodes));
							
				test(x);
			}

			@Test
			public void testContextCompare() throws URISyntaxException {
				Variable price1 = Variable.unary("price1");
				ExpressionContext arg1 = testContext(price1);

				Variable price2 = Variable.unary("price2");
				ExpressionContext arg2 = testContext(price2);

				Formula check = xlator.isIntegerType(arg1.type()).and(
						xlator.isIntegerType(arg2.type())).and(
						arg1.intValue().lt(arg2.intValue()));
				
				Expression x = check.comprehension(
				    price1.oneOf(QuadTableRelations.nodes).and(
				    price2.oneOf(QuadTableRelations.nodes)));
				    				
				test(x);
			}

			@Test
			public void testIntegerLessThan() throws URISyntaxException {
				Variable price1 = Variable.unary("price1");
				ExpressionContext arg1 = testContext(price1);

				Variable price2 = Variable.unary("price2");
				ExpressionContext arg2 = testContext(price2);

				Expression type3 = xlator.promoteNumericType(arg1.type(), arg2.type());

				Formula check = xlator.isIntegerType(type3).and(
						arg1.intValue().lt(arg2.intValue()));
				
				Expression x = check.comprehension(
				    price1.oneOf(QuadTableRelations.nodes).and(
				    price2.oneOf(QuadTableRelations.nodes)));
				    				
				test(x);
			}

			@Test
			public void testFullLessThan() throws URISyntaxException {
				Variable price1 = Variable.unary("price1");
				ExpressionContext arg1 = testContext(price1);
				Variable price2 = Variable.unary("price2");
				ExpressionContext arg2 = testContext(price2);
				Expression type3 = xlator.promoteType(arg1.type(), arg2.type());
				
				Formula check = 
					(xlator.isIntegerType(type3).and(arg1.intValue().lt(arg2.intValue()))).or(
					(xlator.isSomeFloatType(type3).and(floatCompare(arg1.floatValue(), arg2.floatValue()).lt(zero)))).or(
					string_less(arg1.value(), arg2.value()));
						    
				Expression x = check.comprehension(
					price1.oneOf(QuadTableRelations.nodes).and(
					price2.oneOf(QuadTableRelations.nodes)));
							
				test(x);
			}
		}
		
		public static class ExprEquals1UnitTests extends TranslatorTest {
			protected ExprEquals1UnitTests(UniverseFactory universe) throws SQLException, IOException, URISyntaxException {
				super(universe, Arrays.asList(Variable.unary("x"), Variable.unary("v")));
			}

			public ExprEquals1UnitTests() throws SQLException, IOException, URISyntaxException {
				super("expr-equals/query-eq-1.rq", Arrays.asList(Variable.unary("x"), Variable.unary("v")));
			}

			@Test
			public void testNumericEquals() throws URISyntaxException {
				Variable v1 = Variable.unary("v1");
				ExpressionContext arg1 = testContext(v1);
				Variable v2 = Variable.unary("v2");
				ExpressionContext arg2 = testContext(v2);
				
				Formula check = 
					(xlator.isNumericType(arg1.type())).and(
					(xlator.isNumericType(arg2.type()))).and(
					floatCompare(arg1.floatValue(), arg2.floatValue()).eq(zero));
						    
				Expression x = check.comprehension(
					v1.oneOf(QuadTableRelations.nodes).and(
					v2.oneOf(QuadTableRelations.nodes)));
							
				test(x);
			}

			@Test
			public void testFullEquals() throws URISyntaxException {
				Variable v1 = Variable.unary("v1");
				ExpressionContext arg1 = testContext(v1);
				Variable v2 = Variable.unary("v2");
				ExpressionContext arg2 = testContext(v2);
				Expression type3 = xlator.promoteType(arg1.type(), arg2.type());
				
				Formula check = 
					(xlator.isIntegerType(type3).and(arg1.intValue().eq(arg2.intValue()))).or(
					(xlator.isSomeFloatType(type3).and(floatCompare(arg1.floatValue(), arg2.floatValue()).eq(zero)))).or(
					equal_test(arg1.value(), arg2.value()));
						    
				Expression x = check.comprehension(
					v1.oneOf(QuadTableRelations.nodes).and(
					v2.oneOf(QuadTableRelations.nodes)));
							
				test(x);
			}

			@Test
			public void testPlus() throws URISyntaxException {
				Variable v1 = Variable.unary("v1");
				ExpressionContext arg1 = testContext(v1);
				Variable v2 = Variable.unary("v2");
				ExpressionContext arg2 = testContext(v2);
				Expression type3 = xlator.promoteType(arg1.type(), arg2.type());
				
				Variable v3 = Variable.unary("sum");
				
				Formula check = xlator.isNumericType(type3);
						    
				Expression x = check.comprehension(
					v1.oneOf(QuadTableRelations.nodes).and(
					v2.oneOf(QuadTableRelations.nodes).and(
					v3.oneOf(
						xlator.isIntegerType(type3).thenElse(
							arg1.intValue().plus(arg2.intValue()),
							floatAdd(arg1.floatValue(), arg2.floatValue())).toBitset()))));
							
				Instance answer = test(x);
				Evaluator eval = new Evaluator(answer);
				TupleFactory tf = answer.universe().factory();
				for(Tuple row : answer.tuples(r)) {
					Tuple v = tf.tuple(row.atom(2));
					Relation r1 = atomRelation(answer, row.atom(0));
					Relation r2 = atomRelation(answer, row.atom(1));
					
					Expression r1Type = r1.join(literalDatatypes);
					Expression r2Type = r2.join(literalDatatypes);
					IntExpression r1Value = r1.join(literalValues).sum();
					IntExpression r2Value = r2.join(literalValues).sum();
					if (eval.evaluate(xlator.isSomeFloatType(xlator.promoteType(r1Type, r2Type)))) {
						System.err.println(Float.intBitsToFloat(eval.evaluate(floatAdd(r1Value, r2Value))));
					} else {
						System.err.println(eval.evaluate(r1Value.plus(r2Value)));
					}
				}
			}
		}
		
		public static class OpFilter1UnitTestsOpen extends OpFilter1UnitTests {
			public OpFilter1UnitTestsOpen() throws SQLException, IOException, URISyntaxException {
				super(new BoundedUniverse());
			}
		}

		public static class ExprEquals1UnitTestsOpen extends ExprEquals1UnitTests {
			public ExprEquals1UnitTestsOpen() throws SQLException, IOException, URISyntaxException {
				super(new BoundedUniverse());
			}
		}
		
		public static class OpenWorldExpr1AnswerTests extends TranslatorTest {
			protected final SolutionRelation solution;
			
			public OpenWorldExpr1AnswerTests() throws URISyntaxException, SQLException, IOException, ParserConfigurationException, SAXException {
				super(new BoundedUniverse(), Arrays.asList(Variable.unary("title"), Variable.unary("price")));
				solution = getSolution("optional-filter/expr-1");
				universe.addSolution(solution);
			}
			
			@Test
			public void testSolutionTable() throws URISyntaxException {
				Relation s = solution.solutionRelation();
				Expression data = s.project(IntConstant.constant(1));
				Expression e = universe.atomRelation(QuadTableRelations.defaultGraph).join(QuadTableRelations.quads).project(IntConstant.constant(2));
				test(data.eq(e));
			}

			@Test
			public void testSolutionTableMoreData() throws URISyntaxException {
				Relation s = solution.solutionRelation();
				Expression data = s.project(IntConstant.constant(1)).union(s.project(IntConstant.constant(0)).difference(QuadTableRelations.NULL));
				Expression e = universe.atomRelation(QuadTableRelations.defaultGraph).join(QuadTableRelations.quads).project(IntConstant.constant(2));
				test(data.eq(e));
			}

			@Test
			public void testSolutionTableMoreDataExpr() throws URISyntaxException {
				Relation s = solution.solutionRelation();
				Expression data = s.project(IntConstant.constant(1)).union(s.project(IntConstant.constant(0)).difference(QuadTableRelations.NULL));
				Expression e = universe.atomRelation(QuadTableRelations.defaultGraph).join(QuadTableRelations.quads).project(IntConstant.constant(2));
				test(data.intersection(e));
			}

			@Test
			public void testSolutionTableTypeFilter() throws URISyntaxException {
				Relation s = solution.solutionRelation();
				Expression data = s.project(IntConstant.constant(1)).union(s.project(IntConstant.constant(0)).difference(QuadTableRelations.NULL));
				Expression e = universe.atomRelation(QuadTableRelations.defaultGraph).join(QuadTableRelations.quads).project(IntConstant.constant(2));
				
				Variable price = Variable.unary("price");
				ExpressionContext pc = testContext(price);
				Formula filter = xlator.isIntegerType(pc.type());
				Expression results = filter.comprehension(price.oneOf(e.intersection(data)));
				test(results);
			}

			@Test
			public void testSolutionTableFilter() throws URISyntaxException {
				Variable price = Variable.unary("price");
				ExpressionContext pc = testContext(price);
				Relation s = solution.solutionRelation();
				Expression data = s.project(IntConstant.constant(1)).union(s.project(IntConstant.constant(0)).difference(QuadTableRelations.NULL));
				Expression e = universe.atomRelation(QuadTableRelations.defaultGraph).join(QuadTableRelations.quads).project(IntConstant.constant(2));
				Formula filter = xlator.isIntegerType(pc.type()).and(pc.intValue().eq(IntConstant.constant(10)));
				Expression results = filter.comprehension(price.oneOf(e.intersection(data)));
				test(results);
			}
			
			@Test
			public void testSolutionPartialQuery() throws URISyntaxException {
				Relation s = solution.solutionRelation();
				Expression data = s.project(IntConstant.constant(1));
				
				Variable title = variables.get("title");
				Variable book = Variable.unary("book");
				URI titleIRI = new URI("http://purl.org/dc/elements/1.1/title");
				universe.ensureIRI(titleIRI);
				Expression pred = universe.atomRelation(titleIRI);
				Formula f = universe.atomRelation(QuadTableRelations.defaultGraph).product(book).product(pred).product(title).in(QuadTableRelations.quads);
				Expression e = f.forSome(book.oneOf(QuadTableRelations.nodes)).comprehension(title.oneOf(QuadTableRelations.nodes));
			
				test(e.eq(data));
			}

			@Test
			public void testSolutionPartialQueryWithTest() throws URISyntaxException {
				Relation s = solution.solutionRelation();
				Variable book = Variable.unary("book");

				Variable price = variables.get("price");
				ExpressionContext pc = testContext(price);
				URI priceIRI = new URI("http://example.org/ns#price");
				universe.ensureIRI(priceIRI);
				Expression pricePred = universe.atomRelation(priceIRI);
				Formula priceFilter = 
					universe.atomRelation(QuadTableRelations.defaultGraph).product(book).product(pricePred).product(price).in(QuadTableRelations.quads).and(
					xlator.isIntegerType(pc.type()).and(pc.intValue().lt(IntConstant.constant(15))));

				Variable title = variables.get("title");
				URI titleIRI = new URI("http://purl.org/dc/elements/1.1/title");
				universe.ensureIRI(titleIRI);
				Expression pred = universe.atomRelation(titleIRI);
				Formula titleFilter = universe.atomRelation(QuadTableRelations.defaultGraph).product(book).product(pred).product(title).in(QuadTableRelations.quads);
				
				Formula filter = titleFilter.and(priceFilter);
				Expression e = filter.forSome(book.oneOf(QuadTableRelations.nodes))
						.comprehension(price.oneOf(QuadTableRelations.nodes)
								.and(title.oneOf(QuadTableRelations.nodes)));
			
				test(e.intersection(s));
			}

		}
		
		public static class OpenWorldTests extends TranslatorTest {

			public OpenWorldTests() throws URISyntaxException {
				super(new BoundedUniverse(), Arrays.asList(Variable.unary("x"), Variable.unary("y"), Variable.unary("z")));
			}
			
			@Test
			public void testSolutionPartialQueryWithTest() throws URISyntaxException {
				Variable x = variables.get("x");

				Variable y = variables.get("y");
				ExpressionContext yc = testContext(y);
				URI priceIRI = new URI("http://example.org/ns#price");
				universe.ensureIRI(priceIRI);
				Expression pricePred = universe.atomRelation(priceIRI);
				Formula priceFilter1 = 
					universe.atomRelation(QuadTableRelations.defaultGraph).product(x).product(pricePred).product(y).in(QuadTableRelations.quads).and(
					xlator.isIntegerType(yc.type()).and(yc.intValue().gt(IntConstant.constant(10))));
				Formula priceFilter2 = 
						universe.atomRelation(QuadTableRelations.defaultGraph).product(x).product(pricePred).product(y).in(QuadTableRelations.quads).and(
						xlator.isIntegerType(yc.type()).and(yc.intValue().gt(IntConstant.constant(20))));

				Variable z = variables.get("z");
				URI titleIRI = new URI("http://purl.org/dc/elements/1.1/title");
				universe.ensureIRI(titleIRI);
				Expression pred = universe.atomRelation(titleIRI);
				Formula titleFilter = universe.atomRelation(QuadTableRelations.defaultGraph).product(x).product(pred).product(z).in(QuadTableRelations.quads);
				
				Formula filter1 = titleFilter.and(priceFilter1);
				Expression e1 = filter1.forSome(x.oneOf(QuadTableRelations.nodes))
						.comprehension(y.oneOf(QuadTableRelations.nodes)
								.and(z.oneOf(QuadTableRelations.nodes)));

				Relation r1 = Relation.nary("r1", e1.arity());
				universe.nodesRelation(r1);
				
				Formula filter2 = titleFilter.and(priceFilter2);
				Expression e2 = filter2.forSome(x.oneOf(QuadTableRelations.nodes))
						.comprehension(y.oneOf(QuadTableRelations.nodes)
								.and(z.oneOf(QuadTableRelations.nodes)));
			
				Relation r2 = Relation.nary("r2", e2.arity());
				universe.nodesRelation(r2);
				
				test(e1.some().and(e2.some()).and(e1.eq(r1)).and(e2.eq(r2)).and(e1.eq(e2).not()));
			}

		}
	}
}
