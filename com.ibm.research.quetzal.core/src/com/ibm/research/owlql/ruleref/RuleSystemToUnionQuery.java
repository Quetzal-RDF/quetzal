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
package com.ibm.research.owlql.ruleref;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.core.Prologue;
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.core.VarExprList;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.path.Path;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementAssign;
import com.hp.hpl.jena.sparql.syntax.ElementBind;
import com.hp.hpl.jena.sparql.syntax.ElementData;
import com.hp.hpl.jena.sparql.syntax.ElementDataset;
import com.hp.hpl.jena.sparql.syntax.ElementExists;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementMinus;
import com.hp.hpl.jena.sparql.syntax.ElementNamedGraph;
import com.hp.hpl.jena.sparql.syntax.ElementNotExists;
import com.hp.hpl.jena.sparql.syntax.ElementOptional;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;
import com.hp.hpl.jena.sparql.syntax.ElementService;
import com.hp.hpl.jena.sparql.syntax.ElementSubQuery;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;
import com.hp.hpl.jena.sparql.syntax.ElementUnion;
import com.hp.hpl.jena.sparql.syntax.ElementVisitor;
import com.ibm.research.owlql.NewVariableGenerator;
import com.ibm.research.owlql.OWLQLCompiler;
import com.ibm.research.owlql.rule.AtomicFormula;
import com.ibm.research.owlql.rule.ConstantExpr;
import com.ibm.research.owlql.rule.Expr;
import com.ibm.research.owlql.rule.Predicate;
import com.ibm.research.owlql.rule.Rule;
import com.ibm.research.owlql.rule.RuleSystem;
import com.ibm.research.owlql.rule.VariableExpr;
import com.ibm.research.utils.OCUtils;

public class RuleSystemToUnionQuery extends RuleSystemToQueries {
	
	public static class QueryWithRepeatedResultVars extends Query {

		/**
		 * Allows for repeated variables in vars list
		 * @author fokoue
		 *
		 */
		public static class QWRRV_VarExprList extends VarExprList {

			public QWRRV_VarExprList() {
				super();
			}

			public QWRRV_VarExprList(List<Var> vars) {
				super(vars);
			}

			public QWRRV_VarExprList(VarExprList other) {
				super(other);
			}

			@Override
			public void add(Var var) {
				 getVars().add(var) ;
			}
			
			
		}
		public QueryWithRepeatedResultVars() {
			super();
			projectVars = new QWRRV_VarExprList();
		}

		public QueryWithRepeatedResultVars(Prologue prologue) {
			super(prologue);
			projectVars = new QWRRV_VarExprList();
		}
		
	}
	
	public static class VariableSubstitutionElementVisitor implements ElementVisitor, Binding  {

		private Map<Var, Node> oldVar2NewValue;
		private Element result;
		
		public VariableSubstitutionElementVisitor(
				Map<Var, Node> oldVar2NewValue) {
			super();
			this.oldVar2NewValue = oldVar2NewValue;
		}
		
		public Element getResult() {
			return result;
		}

		/*@Override
		public void add(Var arg0, Node arg1) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addAll(Binding arg0) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public Binding getParent() {
			throw new UnsupportedOperationException();
		}
		
		*/



		@Override
		public boolean contains(Var var) {
			return oldVar2NewValue.containsKey(var);
		}



		@Override
		public Node get(Var var) {
			return oldVar2NewValue.get(var);
		}



		



		@Override
		public boolean isEmpty() {
			return oldVar2NewValue.isEmpty();
		}



		@Override
		public int size() {
			return oldVar2NewValue.size();
		}



		@Override
		public Iterator<Var> vars() {
			return oldVar2NewValue.keySet().iterator();
		}



		@Override
		public void visit(ElementAssign assign) {
			com.hp.hpl.jena.sparql.expr.Expr expr = assign.getExpr().copySubstitute(this);
			Var v = assign.getVar();
			result = new ElementAssign(v, expr);
		}
		
		@Override
		public void visit(ElementBind eb) {
			com.hp.hpl.jena.sparql.expr.Expr expr = eb.getExpr().copySubstitute(this);
			Var v = eb.getVar();
			result = new ElementBind(v, expr);
		}

		@Override
		public void visit(ElementDataset e) {
			e.getPatternElement().visit(this);
			result = new ElementDataset(e.getDataset(), result);
		}

		@Override
		public void visit(ElementExists exists) {
			Element arg = exists.getElement();
			arg.visit(this);
			result = new ElementExists(result);
		}
		

		@Override
		public void visit(ElementMinus e) {
			e.getMinusElement().visit(this);
			result = new ElementMinus(result);
			
		}

		/*@Override
		public void visit(ElementFetch e) {
			result = e;
		}*/
		

		@Override
		public void visit(ElementFilter filter) {
			com.hp.hpl.jena.sparql.expr.Expr expr = filter.getExpr();
			expr = expr.copySubstitute(this);
			result = new ElementFilter(expr);
		}
		
		@Override
		public void visit(ElementData e) {
			result = e;
		}

		@Override
		public void visit(ElementGroup group) {
			ElementGroup ret = new ElementGroup();
			for (Element elt: group.getElements() ) {
				elt.visit(this);
				ret.addElement(result);
			}
			result = ret;
		}

		@Override
		public void visit(ElementNamedGraph ng) {
			Element elt = ng.getElement();
			elt.visit(this);
			elt = result;
			Node n = ng.getGraphNameNode();
			if ( n == null) {
				result = new ElementNamedGraph(elt);
			} else {
				result = new ElementNamedGraph(n, elt);
			}
			
		}

		@Override
		public void visit(ElementNotExists notExists) {
			Element arg = notExists.getElement();
			arg.visit(this);
			result = new ElementNotExists(result);
			
		}

		@Override
		public void visit(ElementOptional opt) {
			Element elt = opt.getOptionalElement();
			elt.visit(this);
			result = new ElementOptional(result);
			
		}

		@Override
		public void visit(ElementPathBlock pb) {
			ElementPathBlock ret = new ElementPathBlock();
			for (Iterator<TriplePath> it= 	pb.patternElts();it.hasNext(); ) {
				TriplePath tp = it.next();
				ret.addTriplePath(copySubstitute(tp));
			}
			result = ret;
		}

		@Override
		public void visit(ElementService es) {
			Node n = es.getServiceNode();
			String uri = n!=null && n.isURI()? n.getURI(): null;
			es.getElement().visit(this);
			if (uri!=null) {
				result = new ElementService(uri, result, es.getSilent());
			}  else if (n!=null) {
				result = new ElementService(n, result,es.getSilent());
			} else {
				result = new ElementService((Node) null, result, es.getSilent() );
			}
		}

		@Override
		public void visit(ElementSubQuery e) {
			Query sq = e.getQuery();
			VariableSubstitutionElementVisitor qps = new VariableSubstitutionElementVisitor(oldVar2NewValue);
			sq.getQueryPattern().visit(qps);
			Element newelt = qps.getResult();
			Query newsq = sq.cloneQuery();
			newsq.setQueryPattern(newelt);
			result = new ElementSubQuery(newsq);
		}

		@Override
		public void visit(ElementTriplesBlock tb) {
			ElementTriplesBlock ret = new ElementTriplesBlock();
			for (Triple t:tb.getPattern().getList()) {
				ret.addTriple(copySubstitute(t));
			}
			result = ret;
		}

		@Override
		public void visit(ElementUnion union) {
			ElementUnion ret = new ElementUnion();
			for (Element elt: union.getElements()) {
				elt.visit(this);
				ret.addElement(result);
			}
			result = ret;
			
		}
		
		public TriplePath copySubstitute(TriplePath triple) {
			
			Node newSubj = replace(triple.getSubject());
			Node newObj =  replace(triple.getObject());			
			Path path = triple.getPath();
			TriplePath newTriple;
			if (path!=null) {
				newTriple = new TriplePath(newSubj, path, newObj);
			} else {
				Node pred = replace(triple.getPredicate());
				assert pred!=null : triple;
				//assert pred.isURI() : "predicate must be an IRI: "+ triple;
				newTriple = new TriplePath(new Triple(newSubj, pred, newObj));
			}
			return newTriple;
		}
		public Triple copySubstitute(Triple triple) {
			Node newSubj = replace( triple.getSubject());
			Node newObj =  replace(triple.getObject());			
			Node pred = replace(triple.getPredicate());
			return new Triple(newSubj, pred, newObj);
			

		}
		
		protected Node replace(Node old) {
			if (old!=null && old.isVariable()) {
				Var var = Var.alloc(old);
				Node newN = oldVar2NewValue.get(var);
				if (newN!=null) {
					return newN;
				}
			}
			return old;
		}
		
	}
	private static final Logger logger = LoggerFactory.getLogger(RuleSystemToUnionQuery.class);
	public static Query  toUnionQuery(RuleSystem nonRecursiveSys) {
		 Map<Predicate, Query> headPred2Query = new HashMap<Predicate, Query>();
		 AbstractRDFStoreDBSchemaProcessor pr = new  AbstractRDFStoreDBSchemaProcessor() {
			protected Object getID(ConstantExpr exp) {
				return exp.getValue();
			}
			protected Object getID(Predicate p) {
				try {
					return new URI(p.getName());
				} catch (URISyntaxException e) {
					throw new RuntimeException(e);
				}
			}
		 };
		 pr.setRuleSystem(nonRecursiveSys);
		 RuleSystem newSys = pr.convertDLPredicateToDBTablePredicate();
		 logger.debug("Rulesystem after transformation of dl predicate: {}", newSys);
		 
		 String prefix =  OWLQLCompiler.UNBOUND_VARIABLE_PREFIX;
		 int suffixStart = OCUtils.nextAvailableSuffixVariable(newSys.getAllVariableNames(), prefix	);
		 NewVariableGenerator varGen = new NewVariableGenerator(prefix, suffixStart);
		 Query ret =  getUnionQuery(newSys,nonRecursiveSys.getMainHeadFormula().getPredicate() , headPred2Query, varGen);
		 ret.setDistinct(nonRecursiveSys.areResultsForMainHeadFormulaDistinct());
		 return ret;
	}
	
	
	protected static Query getUnionQuery(RuleSystem rules, Predicate headPredicate,   Map<Predicate, Query> headPred2Query, NewVariableGenerator varGen) {
		assert rules.isIDB(headPredicate) : headPredicate;
		Query ret = headPred2Query.get(headPredicate);
		if (ret==null) {
			Set<Rule> relevantRules =  rules.getRulesForHead(headPredicate);
			if (relevantRules.size() == 1) {
				ret = ruleToQuery(rules, relevantRules.iterator().next(), headPred2Query, varGen);
			} else {
				List<? extends Expr> firstRuleHeadVars = null; 
				for (Rule rule: rules.getRulesForHead(headPredicate)) {
					Query q =  ruleToQuery(rules, rule, headPred2Query, varGen);
					if (ret==null) {
						ret = q;
						firstRuleHeadVars = rule.getHead().getArguments();
						ElementUnion union = new ElementUnion();
						Element elt = q.getQueryPattern();
						union.addElement(elt);
						ret.setQueryPattern(union);
					} else {
						ElementUnion union = (ElementUnion) ret.getQueryPattern();
						Element elt = instantiateBody(q, firstRuleHeadVars);
						union.addElement(elt);
					}
				}
			}
			QueryPatternSimplification simpl = new QueryPatternSimplification();
			ret.getQueryPattern().visit(simpl);
			ret.setQueryPattern(simpl.getResult());
			headPred2Query.put(headPredicate, ret);
		}
		return ret;
	}
	
	protected static Query ruleToQuery(RuleSystem rules, Rule rule,
			Map<Predicate, Query> headPred2Query, NewVariableGenerator varGen) {
		Query cq = new QueryWithRepeatedResultVars();
		cq.setQuerySelectType();
		for (Expr e: rule.getHead().getArguments()) {
			 assert e.isVariable() : e;
			 cq.addResultVar(((VariableExpr)e).getName());
			
		}
		ElementGroup group = new ElementGroup();
		cq.setQueryPattern(group);
		Set<VariableExpr> unboundVars = new HashSet<VariableExpr>(rule.getUnboundVariables());
		for (AtomicFormula af :rule.getBody()) {
			 af = renameUnboundVariables(af, unboundVars, varGen);
			 Predicate pred = af.getPredicate();
			 Element elt;
			 if (rules.isIDB(pred)) {
				 Query conjunctQuery = getUnionQuery(rules,pred, headPred2Query, varGen);
				 elt = instantiateBody(conjunctQuery, af.getArguments());
			 } else {
				 elt = instantiateBody( af, af.getArguments());
			 }
			 group.addElement(elt);
		 }
		 return cq;
	}
	
	
		
	
	protected static Element instantiateBody(AtomicFormula newConjunct, List<? extends Expr> argsForNewConjunct) {
		assert newConjunct.getArity() == argsForNewConjunct.size();
		
		if (Rule.isBuiltInPredicate(newConjunct.getPredicate())){
			
			if (newConjunct.getPredicate().getName().equals(Rule.BUILT_IN_EQUAL)) {
				assert  newConjunct.getArity() == 2 : newConjunct.getArity();
				ElementFilter filter= new ElementFilter(toEqualsFilter(newConjunct.getArguments().get(0), 
					newConjunct.getArguments().get(1)));
				return filter;
			} else if  (newConjunct.getPredicate().getName().equals(Rule.BUILT_IN_IRI_EQUAL)) {
				assert  newConjunct.getArity() == 2 : newConjunct.getArity();
				ElementFilter filter= new ElementFilter(toIRIEqualsFilter(newConjunct.getArguments().get(0), 
					newConjunct.getArguments().get(1)));
				return filter;
			}
			else if  (newConjunct.getPredicate().getName().equals(Rule.BUILT_IN_DIFF)) {
				assert  newConjunct.getArity() == 2 : newConjunct.getArity();
				ElementFilter filter= new ElementFilter(toNotEqualsFilter(newConjunct.getArguments().get(0), 
					newConjunct.getArguments().get(1)));
				return filter;
			}
			else if  (newConjunct.getPredicate().getName().equals(Rule.BUILT_IN_IRI_DIFF)) {
				assert  newConjunct.getArity() == 2 : newConjunct.getArity();
				ElementFilter filter= new ElementFilter(toIRINotEqualsFilter(newConjunct.getArguments().get(0), 
					newConjunct.getArguments().get(1)));
				return filter;
			}
			else {
				assert newConjunct.getPredicate().getName().equals(Rule.BUILT_IN_BOUND_VAR) : newConjunct;
				return  processBoundFormula(newConjunct);
			}
			
		} else {
			assert newConjunct.getArity() == 3 : newConjunct;
			List<? extends Expr> args = newConjunct.getArguments();
			Expr s = args.get(0);
			Expr p = args.get(1);
			Expr o = args.get(2);
			Triple triple = toTriple(s, p, o);	
			ElementTriplesBlock ret = new ElementTriplesBlock();
			ret.addTriple(triple);
			return  ret;
		}
	}


	
	protected static Element instantiateBody(Query cq, List<? extends Expr> args ) {
		
		Map<Var, Node> oldVar2NewValue = new HashMap<Var, Node>();
		List<String> resultVars = cq.getResultVars();
		Set<String> alreadySeenVars = new HashSet<String>();
		
		assert resultVars.size() == args.size() : resultVars+", " +args	;
		for (int i=0;i<resultVars.size();i++) {
			String  old = resultVars.get(i);	
			if (alreadySeenVars.add(old)) {
				Expr newE = args.get(i);
				Node newN = toNode(newE);
				oldVar2NewValue.put(Var.alloc(old), newN);
			}		
		}
		VariableSubstitutionElementVisitor visitor = new VariableSubstitutionElementVisitor(oldVar2NewValue);
		cq.getQueryPattern().visit(visitor);
		Element result = visitor.getResult();
		return result;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
