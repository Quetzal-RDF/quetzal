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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.E_Equals;
import com.hp.hpl.jena.sparql.expr.E_NotEquals;
import com.hp.hpl.jena.sparql.expr.E_Str;
import com.hp.hpl.jena.sparql.expr.ExprVar;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.expr.nodevalue.NodeValueNode;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementBind;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;
import com.hp.hpl.jena.sparql.syntax.ElementUnion;
import com.ibm.research.owlql.ConjunctiveQuery;
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
import com.ibm.wala.util.collections.Pair;

public class RuleSystemToQueries {

	private static final Logger logger = LoggerFactory.getLogger(RuleSystemToQueries.class);
	public static Set<ConjunctiveQuery>  toConjunctiveQueries(RuleSystem nonRecursiveSys) {
		 Map<Predicate, Set<ConjunctiveQuery>> headPred2ConjQueries = new HashMap<Predicate, Set<ConjunctiveQuery>>();
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
		 return  getConjunctiveQueries(newSys,nonRecursiveSys.getMainHeadFormula().getPredicate() , headPred2ConjQueries, varGen);
	}
	
	
	protected static Set<ConjunctiveQuery> getConjunctiveQueries(RuleSystem rules, Predicate headPredicate,  Map<Predicate, Set<ConjunctiveQuery>> headPred2ConjQueries, NewVariableGenerator varGen) {
		assert rules.isIDB(headPredicate) : headPredicate;
		Set<ConjunctiveQuery> ret = headPred2ConjQueries.get(headPredicate);
		if (ret==null) {
			ret = new HashSet<ConjunctiveQuery>();
			for (Rule rule: rules.getRulesForHead(headPredicate)) {
				ret.addAll(ruleToConjunctiveQueries(rules, rule, headPred2ConjQueries, varGen));
			}		
			headPred2ConjQueries.put(headPredicate, ret);
		}
		return ret;
	}
	
	protected static Set<ConjunctiveQuery> ruleToConjunctiveQueries(RuleSystem rules, Rule rule,
			Map<Predicate, Set<ConjunctiveQuery>> headPred2ConjQueries, NewVariableGenerator varGen) {
		Set<ConjunctiveQuery> ret =  new HashSet<ConjunctiveQuery>();
		ConjunctiveQuery cq = new ConjunctiveQuery();
		for (Expr e: rule.getHead().getArguments()) {
			 assert e.isVariable() : e;
			 cq.addResultVar(((VariableExpr)e).getName());
			
		}
		cq.setQueryPattern(new ElementTriplesBlock());
		ret.add(cq);
		Set<VariableExpr> unboundVars = new HashSet<VariableExpr>(rule.getUnboundVariables());
		for (AtomicFormula af : rule.getBody()) {
			 af = renameUnboundVariables(af, unboundVars, varGen);
			 Predicate pred = af.getPredicate();
			 if (rules.isIDB(pred)) {
				 Set<ConjunctiveQuery> subcqs = getConjunctiveQueries(rules,pred, headPred2ConjQueries, varGen);
				 ret = expand(ret, subcqs, af.getArguments());
			 } else {
				 ret = expand(ret, af, af.getArguments());
			 }
		 }
		 return ret;
	}
	
	protected static AtomicFormula renameUnboundVariables(AtomicFormula af,
			Set<VariableExpr> unboundVars,  NewVariableGenerator varGen) {
		List<Expr> args = new ArrayList<Expr>(af.getArguments().size());
		for ( Expr e: af.getArguments()) {
			if (unboundVars.contains(e)) {
				args.add(new VariableExpr(varGen.createNewVariable()));
			} else {
				args.add(e);
			}
		}
		return new AtomicFormula(af.getPredicate(), args);
	}
		
	protected static Set<ConjunctiveQuery> expand(Set<ConjunctiveQuery> startcqs,
			Set<ConjunctiveQuery> flatteningOfNewConjuncts, List<? extends Expr> argsForNewConjunct) {
		Set<ConjunctiveQuery> ret = new HashSet<ConjunctiveQuery>();
		for (ConjunctiveQuery startcq : startcqs) {
			for (ConjunctiveQuery newconjunct: flatteningOfNewConjuncts) {
				ConjunctiveQuery newcq = startcq.cloneConjQuery();
				Pair<List<Triple>,  List<E_Equals>> conjuncts = instantiateBody(newconjunct, argsForNewConjunct);
				for (Triple t: conjuncts.fst) {
					newcq.addTriple(t);
				}
				for (E_Equals ef: conjuncts.snd) {
					newcq.addFilter(ef);
				}
				ret.add(newcq);
			}
		}
		return ret;
		
		
	}
	protected static Set<ConjunctiveQuery> expand(Set<ConjunctiveQuery> startcqs,
			AtomicFormula newConjunct, List<? extends Expr> argsForNewConjunct) {
		assert newConjunct.getArity() == argsForNewConjunct.size();
		Set<ConjunctiveQuery> ret = new HashSet<ConjunctiveQuery>();
		for (ConjunctiveQuery startcq : startcqs) {
			ConjunctiveQuery newcq = startcq.cloneConjQuery();
			if (Rule.isBuiltInPredicate(newConjunct.getPredicate())){
				if ( newConjunct.getPredicate().getName().equals(Rule.BUILT_IN_EQUAL)) {
					assert  newConjunct.getArity() == 2 : newConjunct.getArity();
					newcq.addFilter(toEqualsFilter(newConjunct.getArguments().get(0), 
							newConjunct.getArguments().get(1)));
				} else if ( newConjunct.getPredicate().getName().equals(Rule.BUILT_IN_IRI_EQUAL)) {
					assert  newConjunct.getArity() == 2 : newConjunct.getArity();
					newcq.addFilter(toIRIEqualsFilter(newConjunct.getArguments().get(0), 
							newConjunct.getArguments().get(1)));
				} else if (newConjunct.getPredicate().getName().equals(Rule.BUILT_IN_DIFF) ) {
					assert  newConjunct.getArity() == 2 : newConjunct.getArity();
					newcq.addFilter(toNotEqualsFilter(newConjunct.getArguments().get(0), 
							newConjunct.getArguments().get(1)));
				} else if ( newConjunct.getPredicate().getName().equals(Rule.BUILT_IN_IRI_DIFF)) {
					newcq.addFilter(toIRINotEqualsFilter(newConjunct.getArguments().get(0), 
							newConjunct.getArguments().get(1)));
				} else {
					assert  newConjunct.getPredicate().getName().equals(Rule.BUILT_IN_BOUND_VAR) : newConjunct;
					newcq.add(processBoundFormula(newConjunct));
				}
			} else {
				assert newConjunct.getArity() == 3 : newConjunct;
				List<? extends Expr> args = newConjunct.getArguments();
				Expr s = args.get(0);
				Expr p = args.get(1);
				Expr o = args.get(2);
				newcq.addTriple(toTriple(s, p, o));				
			}
			ret.add(newcq);
		}
		return ret;
		
	}

	protected static Triple toTriple(Expr s, Expr p, Expr o  ) {
		return new Triple(toNode(s), toNode(p), toNode(o));
	}
	
	protected static E_Equals toEqualsFilter(Expr left, Expr right) {
		Node ln = toNode(left);
		com.hp.hpl.jena.sparql.expr.Expr le = ln.isVariable()? new ExprVar(ln.getName()) : new NodeValueNode(ln);
		Node rn = toNode(right);
		com.hp.hpl.jena.sparql.expr.Expr re = rn.isVariable()? new ExprVar(rn.getName()) : new NodeValueNode(rn);
		return new E_Equals(le, re);
	}
	protected static E_Equals toIRIEqualsFilter(Expr left, Expr right) {
		Node ln = toNode(left);
		com.hp.hpl.jena.sparql.expr.Expr le = ln.isVariable()? new ExprVar(ln.getName()) : new NodeValueNode(ln);
		Node rn = toNode(right);
		com.hp.hpl.jena.sparql.expr.Expr re = rn.isVariable()? new ExprVar(rn.getName()) : new NodeValueNode(rn);
		return new E_Equals(new E_Str(le), new E_Str(re));
	}
	
	
	protected static E_NotEquals toNotEqualsFilter(Expr left, Expr right) {
		Node ln = toNode(left);
		com.hp.hpl.jena.sparql.expr.Expr le = ln.isVariable()? new ExprVar(ln.getName()) : new NodeValueNode(ln);
		Node rn = toNode(right);
		com.hp.hpl.jena.sparql.expr.Expr re = rn.isVariable()? new ExprVar(rn.getName()) : new NodeValueNode(rn);
		return new E_NotEquals(le, re);
	}
	
	protected static E_NotEquals toIRINotEqualsFilter(Expr left, Expr right) {
		Node ln = toNode(left);
		com.hp.hpl.jena.sparql.expr.Expr le = ln.isVariable()? new ExprVar(ln.getName()) : new NodeValueNode(ln);
		Node rn = toNode(right);
		com.hp.hpl.jena.sparql.expr.Expr re = rn.isVariable()? new ExprVar(rn.getName()) : new NodeValueNode(rn);
		return new E_NotEquals(new E_Str(le), new E_Str(re));
	}
	
	protected static Element processBoundFormula(AtomicFormula af) {
		assert af.getPredicate().getName().equals(Rule.BUILT_IN_BOUND_VAR) : af;
		assert af.getArity() >1 : af;
		assert af.getArguments().get(0).isVariable() : af;
		VariableExpr var = (VariableExpr) af.getArguments().get(0);
		Element ret;
		if (af.getArity() == 2) {
			Expr e1 = af.getArguments().get(1);
			ret = new ElementBind(Var.alloc(var.getName()), toJenaExpr(e1));
		} else {
			assert af.getArguments().size() == af.getArity();
			ElementUnion  union = new ElementUnion();
			for (int i=1;i<af.getArity();i++) {
				Expr e = af.getArguments().get(i);
				union.addElement(new ElementBind(Var.alloc(var.getName()), toJenaExpr(e)));
			}
			ret = union;
		}
		return ret;
	}
	protected static com.hp.hpl.jena.sparql.expr.Expr toJenaExpr(Expr e        ) {
		
		com.hp.hpl.jena.sparql.expr.Expr ret;
		if(e.isVariable()) {
			ret = new ExprVar(((VariableExpr)e).getName());
		} else {
			//Object v = ((ConstantExpr) e).getValue();
			ret = NodeValue.makeNode(toNode(e));
		}
		return ret;
	}
	protected static Node toNode(Expr e        ) {
		
		Node ret;
		if(e.isVariable()) {
			ret = Node.createVariable(((VariableExpr)e).getName());
		} else {
			Object v = ((ConstantExpr) e).getValue();
			if (v instanceof URI) {
				ret = Node.createURI(v.toString());
			} /*else if (predicatePosition) {
				ret = Node.createURI(v.toString());
			}*/ else
			{
				ret = Node.createLiteral(v.toString());
			}
		}
		return ret;
	}
	
	protected static Pair<List<Triple>,  List<E_Equals>> instantiateBody(ConjunctiveQuery cq, List<? extends Expr> args ) {
		List<Triple> triples = new LinkedList<Triple>();
		List<E_Equals> filters = new LinkedList<E_Equals>();
		Map<String, Node> oldVar2NewValue = new HashMap<String, Node>();
		List<String> resultVars = cq.getResultVars();
		assert resultVars.size() == args.size() : resultVars+", " +args	;
		for (int i=0;i<resultVars.size();i++) {
			String  old = resultVars.get(i);	
			Expr newE = args.get(i);
			Node newN = toNode(newE);
			oldVar2NewValue.put(old, newN);
		}
		// triples
		for (Triple triple: cq.getTriples()) {
			Node subj = triple.getSubject();
			Node obj = triple.getObject();
			Node[] subjObj = new Node[]{subj, obj};
			Node pred = triple.getPredicate();
			assert pred.isURI() : "predicate must be an IRI: "+ triple;
			Node newSubj = subj;
			Node newObj = obj;
			for (int i=0;i<2;i++) {
				Node t = subjObj[i];
				if (t.isVariable()) {
					String var = t.getName();
					Node newN = oldVar2NewValue.get(var);
					if (newN!=null) {
						if (i==0) {
							newSubj = newN;
						} else {
							newObj = newN;
						}
					}
				}
			}
			Triple newTriple = new Triple(newSubj, pred, newObj);
			triples.add(newTriple);
		}
		// TODO: replace this by result var substitution
		//Perform renaming on filters
		for (ElementFilter ef: cq.getFilters()) {
			assert  (ef.getExpr() instanceof E_Equals ): ef;
			E_Equals e = (E_Equals) ef.getExpr();
			com.hp.hpl.jena.sparql.expr.Expr left = e.getArg1();
			com.hp.hpl.jena.sparql.expr.Expr right = e.getArg2();
			com.hp.hpl.jena.sparql.expr.Expr newLeft = left;
			com.hp.hpl.jena.sparql.expr.Expr newRight = right;
			for (int i=0;i<2;i++) {
				com.hp.hpl.jena.sparql.expr.Expr  expr = i==0? left: right;
				com.hp.hpl.jena.sparql.expr.Expr  newExpr = null;
				if (expr instanceof ExprVar) {
					ExprVar evar = (ExprVar) expr;
					Node newVar = oldVar2NewValue.get(evar.getVarName());
					if (newVar != null) {
						if (newVar.isVariable()) {
							newExpr = new ExprVar(newVar);
						} else {
							newExpr = new NodeValueNode(newVar);
						}
					}
				}
				if (newExpr != null) {
					if (i==0) {
						newLeft = newExpr;
					} else {
						newRight = newExpr;
					}
				}
			}
			filters.add(new E_Equals(newLeft, newRight));
		}
		//
		return Pair.make(triples, filters);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
