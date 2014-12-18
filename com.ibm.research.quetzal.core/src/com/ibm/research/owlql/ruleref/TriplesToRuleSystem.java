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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.vocabulary.RDF;
import com.ibm.research.owlql.ConjunctiveQuery;
import com.ibm.research.owlql.NormalizedOWLQLTbox;
import com.ibm.research.owlql.rule.AtomicFormula;
import com.ibm.research.owlql.rule.ConstantExpr;
import com.ibm.research.owlql.rule.Expr;
import com.ibm.research.owlql.rule.Predicate;
import com.ibm.research.owlql.rule.Rule;
import com.ibm.research.owlql.rule.RuleSystem;
import com.ibm.research.owlql.rule.VariableExpr;
import com.ibm.research.utils.OCUtils;

/**
 * 
 * @author fokoue
 *
 */
public class TriplesToRuleSystem {
	private static final Logger logger = LoggerFactory.getLogger(TriplesToRuleSystem.class);

	/**
	 * prefix of the predicate for the main head predicate of the rule system
	 */
	private static final String MAIN_HEAD_FORMULA_PREFIX = "query_";
	/**
	 * normalized TBox
	 */
	protected NormalizedOWLQLTbox tbox;

	
	
	
	
	public TriplesToRuleSystem(NormalizedOWLQLTbox tbox) {
		super();
		this.tbox = tbox;
	}
	/**
	 * converts a set of triples in a basic graph pattern into a rule system.
	 * @param triples the input set of triples
	 * @param allVarNamesInQuery all the names of variables (including both variables appearing in the input set of triples 
	 * and variables appearing in sparql query containing the input set of triples)
	 * @param distinct whether results must be distinct 
	 *
	 */
	public RuleSystem toRules(Collection<Triple> triples, Set<String> allVarNamesInQuery, boolean distinct) {
		return toRules(triples,new LinkedList<String>(getAllVariables(triples)), allVarNamesInQuery, distinct);
	}
	
	public RuleSystem toRules(ConjunctiveQuery q) {
			Set<String> allVars = q.getAllVariables();
		Set<Triple> allTriples = new HashSet<Triple>(q.getTriples());
		return toRules(allTriples,q.getResultVars(), allVars, q.isDistinct());
		
	}
	/**
	 * converts a set of triples in a basic graph pattern into a rule system.
	 * @param triples the input set of triples
	 * @param resultVars set of results variables
	 * @param allVarNamesInQuery all the names of variables (including both variables appearing in the input set of triples 
	 * and variables appearing in sparql query containing the input set of triples)
	 * @param distinct whether results must be distinct 
	 *
	 */
	public RuleSystem toRules(Collection<Triple> triples, List<String> resultVars, Set<String> allVarNamesInQuery,  boolean distinct) {
		
		assert allVarNamesInQuery.containsAll(getAllVariables(triples)): allVarNamesInQuery+"\n"+getAllVariables(triples);
		
		List<AtomicFormula> body = new LinkedList<AtomicFormula>();
		Set<Predicate> bodyPreds = new HashSet<Predicate>();
		/*
		 boolean distinct = query.isDistinct();
		 if (!query.getFilters().isEmpty()) {
			throw new RuntimeException("Queries with filters are not supported yet!");
		}*/
		
		for (Triple t: triples) {
			//boolean isWildcard = false;
			Node sub = t.getSubject();
			Node pred = t.getPredicate();
			Node obj = t.getObject();
			
			Expr s = toExpr(sub);
			
			Predicate predicate;
			AtomicFormula af;
			if (pred.isURI() && pred.getURI().equals(RDF.type.getURI()) && obj.isURI()) {
				// sub rdf:type obj
				predicate = new Predicate(obj.getURI(), 1);
				af = new AtomicFormula(predicate, s);
			} else if (pred.isURI() && !pred.getURI().equals(RDF.type.getURI())){
				// sub R obj
				Expr o = toExpr(obj);
				predicate = new Predicate(pred.getURI(), 2);
				af = new AtomicFormula(predicate, s, o);
			} else {
				// Wildcard
				Expr o  = toExpr(obj);
				predicate = new TriplePredicate();
				af = new AtomicFormula(predicate, s, toExpr(pred), o);
			}
			body.add(af);
			bodyPreds.add(predicate);
		}
		List<VariableExpr> headVars = new LinkedList<VariableExpr>();
		Set<String> headVarSet = new HashSet<String>(); 
		Set<String> genVars = new HashSet<String>();
		for (String var: resultVars) {
			if (headVarSet.add(var)) {
				headVars.add(new VariableExpr(var));
			} else {
				// var appears more than once in the list of result vars
				// create new var v and add the formula  v = var in the body
				int suffix = nextAvailableSuffixVariable(allVarNamesInQuery, genVars, var+"_");
				String newVar = var+"_"+suffix;
				genVars.add(newVar);
				VariableExpr newVarExp =  new VariableExpr(newVar);
				headVars.add(newVarExp);
				Predicate equalPred = new Predicate(Rule.BUILT_IN_EQUAL, 2);
				AtomicFormula newAf = new AtomicFormula(equalPred,
						new VariableExpr(var),newVarExp);
				body.add(newAf);
				//
			}
		}
		int suffix = RuleSystem.nextAvailableSuffixForGeneratedPredicate(bodyPreds,
				MAIN_HEAD_FORMULA_PREFIX);
		Predicate headPred = new Predicate(MAIN_HEAD_FORMULA_PREFIX+suffix,headVars.size());
		AtomicFormula head = new AtomicFormula(headPred, headVars);
		Rule r = new Rule(head, body, 0);
		RuleSystem ret = new RuleSystem(Collections.singletonList(r), head,distinct);
		logger.debug("{}\n==>\n{}",triples, ret);
		return ret;
	} 
	/**
	 * converts a {@link Node} into an {l@ink Expr}
	 * @param n
	 * @return
	 */
	protected Expr	toExpr(Node n) {
		if (n.isVariable()) {
			return new VariableExpr(n.getName());
		} else if (n.isURI()) {
			try {
				return new ConstantExpr(new URI(n.getURI()));
			} catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}
		} else if (n.isLiteral()) {
			Literal l = (Literal) n;
			return new ConstantExpr(l.getValue());
		} else {
			throw new RuntimeException("Unsuported node type in query : "+n);
		}
	}
	/**
	 * returns the next suffix that can be used for a variable without name conflict.
	 * @param vars
	 * @param additionaVars
	 * @param prefix
	 * @return
	 */
	protected static  int nextAvailableSuffixVariable(Set<String> vars,  Set<String> additionaVars, String prefix) {
	    vars = new HashSet<String>(vars);
		vars.addAll(additionaVars);
		return OCUtils.nextAvailableSuffixVariable(vars, prefix);
	}
	/**
	 * returns the set of variables appearing in a set of triples
	 * @param triples
	 * @return
	 */
	protected Set<String> getAllVariables(Collection<Triple> triples) {
		Set<String> ret = new HashSet<String>();
		for (Triple t:triples) {
			if (t.getSubject().isVariable()) {
				ret.add(t.getSubject().getName());
			}
			if (t.getPredicate().isVariable()) {
				ret.add(t.getPredicate().getName());
			}
			if (t.getObject().isVariable()) {
				ret.add(t.getObject().getName());
			}
		}
		return ret;
	}
}
