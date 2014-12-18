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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLDataFactory;

import com.ibm.research.owlql.rule.AtomicFormula;
import com.ibm.research.owlql.rule.Expr;
import com.ibm.research.owlql.rule.Predicate;
import com.ibm.research.owlql.rule.Rule;
import com.ibm.research.owlql.rule.RuleSystem;
import com.ibm.research.owlql.rule.VariableExpr;

public class Split {

	private static final String GENERATED_PREDICATE_PREFIX = "genPred_";
	private static class Component {
		private List<AtomicFormula> formulas;
		private List<VariableExpr> joinVars;
		public Component() {
			this(new LinkedList<AtomicFormula>(), new LinkedList<VariableExpr>());
		}
		public Component(List<AtomicFormula> formulas,
				List<VariableExpr> joinVars) {
			super();
			this.formulas = formulas;
			this.joinVars = joinVars;
		}
		public void add(Component comp) {
			formulas.addAll(comp.formulas);
			joinVars.addAll(comp.joinVars);
		}
		
		
	}
	protected OWLDataFactory fac;

	public Split(OWLDataFactory fac) {
		super();
		this.fac = fac;
	}
	
	/**
	 * rule body split or null if it cannot be split 
	 * @param r
	 * @return
	 */
	protected List<List<AtomicFormula>> splitRuleBody(Rule r) {
		Set<VariableExpr> joinVars = new HashSet<VariableExpr>(r.getAllRuleVariables());
		joinVars.removeAll(r.getHead().getAllVariables());
		joinVars.removeAll(r.getUnboundVariables());
		joinVars.removeAll(TriplePredicate.getPredicateVariables(r)); 
				
		if (joinVars.isEmpty()) {
			return null;
		}
		List<AtomicFormula> formulasWithoutJoinVars = new LinkedList<AtomicFormula>();
		Map<VariableExpr, Component> joinVar2JoinComponent = new HashMap<VariableExpr, Component>();
		for (AtomicFormula af : r.getBody()) {
			Component component = null;
			for (VariableExpr v: af.getAllVariables()) {
				if (joinVars.contains(v)) {
					if (component==null) {
						component = joinVar2JoinComponent.get(v);
						if (component == null) {
							component = new Component();
							component.joinVars.add(v);
							joinVar2JoinComponent.put(v, component);
						}
						component.formulas.add(af.clone());						
					} else {
						Component oldComponent =  joinVar2JoinComponent.get(v);
						if (oldComponent != null) {
							if (oldComponent!=component) {
								component.add(oldComponent);
								for (VariableExpr jv: oldComponent.joinVars) {
									joinVar2JoinComponent.put(jv, component);
								}
							} 
						} else {
							joinVar2JoinComponent.put(v, component);
							component.joinVars.add(v); 
						}
					}
					
					
				}
			}
			if (component == null) {
				formulasWithoutJoinVars.add(af.clone());
			}
			
		}
		List<List<AtomicFormula>> ret = new LinkedList<List<AtomicFormula>>();
		Set<Component> alreadySeen = new HashSet<Component>();
		for (Component comp: joinVar2JoinComponent.values()) {
			if (alreadySeen.add(comp)) {
				ret.add(comp.formulas);
			}
		}
		if (!formulasWithoutJoinVars.isEmpty()) {
			ret.add(formulasWithoutJoinVars);
		}
		return ret;
	}
	
	protected List<Rule> split(Rule r, int[] nextAvalableSuffix, int[] nextAvailableRuleId) {
		List<List<AtomicFormula>> components = splitRuleBody(r);
		if (components == null || components.size()<2) {
			return Collections.singletonList(r);
		}
		Set<VariableExpr> headVars = r.getHead().getAllVariables();
		List<Rule> rules = new LinkedList<Rule>();
		List<AtomicFormula> newBody = new LinkedList<AtomicFormula>();
		
		for (List<AtomicFormula> comp: components) {
			Set<VariableExpr> compVars = new HashSet<VariableExpr>();
			for (AtomicFormula af: comp) {
				compVars.addAll(af.getAllVariables());
			}
			compVars.retainAll(headVars);
			Predicate compPred = new Predicate(generatePredicateName(nextAvalableSuffix[0]++),
					compVars.size());
			List<VariableExpr> compVarList = getHeadVars(r, compVars);
			newBody.add(new AtomicFormula(compPred, compVarList));
			AtomicFormula compRuleHead = new AtomicFormula(compPred, new LinkedList<Expr>(compVarList));
			Rule compRule = new Rule(compRuleHead, comp,nextAvailableRuleId[0]++);
			rules.add(compRule);
			
		}
		Rule newRule = new Rule(r.getHead().clone(), newBody, r.getId());
		rules.add(0, newRule);
		return rules;
	}
	
	public RuleSystem split(RuleSystem rs) {
		List<Rule> newRules = new LinkedList<Rule>();
		int[] nextAvailabeSuffix = new int[]{nextAvailableSuffix(rs)};
		int[] nextAvailableId = new int[]{nextAvailableRuleId(rs)};
		for (Rule r:rs.getRules()) {
			newRules.addAll(split(r,nextAvailabeSuffix, nextAvailableId));
		}
		return new RuleSystem(newRules);
	}
	protected List<VariableExpr> getHeadVars(Rule r, Set<VariableExpr> compVars) {
		List<VariableExpr> ret = new LinkedList<VariableExpr>();
		for (Expr e : r.getHead().getArguments()) {
			if (compVars.contains(e)) {
				ret.add((VariableExpr)e);
			}
 		}
		return ret;
	}
	
	protected String generatePredicateName(int suffix) {
		return GENERATED_PREDICATE_PREFIX+suffix;
	}
	
	protected int nextAvailableSuffix(RuleSystem rs) {
		return RuleSystem.nextAvailableSuffixForGeneratedPredicate(rs,
				GENERATED_PREDICATE_PREFIX);
	}
	protected int nextAvailableRuleId(RuleSystem rs) {
		return RuleSystem.nextAvailableRuleId(rs);
	}
	
}
