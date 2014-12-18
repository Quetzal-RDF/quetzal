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
package com.ibm.research.owlql.rule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;



public class SupplementaryPredicate extends Predicate {

	private Rule rule;
	private int position;
	
	static SupplementaryPredicate createSupplementaryPredicate(Rule rule, int position) {
		return new SupplementaryPredicate("sup_"+rule.getId()+"_"+position, getRelevantBoundVariables(rule, position).size(), rule, position);
	}
	static AtomicFormula createSupplementatryAtomicFormula(Rule rule, int position) {
		SupplementaryPredicate supPred = SupplementaryPredicate.createSupplementaryPredicate(rule, position);
		List<Expr> args = new ArrayList<Expr>(SupplementaryPredicate.getRelevantBoundVariables(rule, position));
		AtomicFormula supF = new AtomicFormula(supPred,args);
		return  supF;
	}
	
	static List<VariableExpr> getRelevantBoundVariables(Rule rule, int position) {
		assert position <rule.getBody().size() : ""+rule+" "+position;
		AtomicFormula head = rule.getHead();
		assert head.getPredicate() instanceof UniqueBindingPredicate;
		
		List<VariableExpr> ret =  new ArrayList<VariableExpr>();
		Set<VariableExpr> alreadySeenVars = new HashSet<VariableExpr>();
		ret.addAll(((UniqueBindingPredicate) head.getPredicate()).getBoundVariablesList(head));
		alreadySeenVars.addAll(ret);
		for (int i=0;i<position;i++) {
			AtomicFormula f = rule.getBody().get(i);
			if (!f.getPredicate().isNegated()
			&& !f.getPredicate().isOptional()) { // we do not add variables in negated or optional atomic formulas
				//ret.addAll(f.getAllVariables());
				for (Iterator<? extends Expr> it=f.getArguments().iterator();it.hasNext();) {
					Expr e = it.next();
					if (e.isVariable() 
					&& alreadySeenVars.add( (VariableExpr) e)) {
						ret.add((VariableExpr)e);
					}
				}
			}
		}
		ret.retainAll(getRelevantVariables(rule, position));
		return ret;
	}
	
	/*static SortedSet<VariableExpr> getRelevantFreeVariables(Rule rule, int position) {
		assert position <rule.getBody().size();
		AtomicFormula head = rule.getHead();
		assert head.getPredicate() instanceof UniqueBindingPredicate;
		Comparator<VariableExpr> comp = new Comparator<VariableExpr>() {

			public int compare(VariableExpr o1, VariableExpr o2) {
				return o1.getName().compareTo(o2.getName());
			}
			
		};
		SortedSet<VariableExpr> ret =  new TreeSet<VariableExpr>(comp);
		ret.addAll(((UniqueBindingPredicate) head.getPredicate()).getBoundVariables(head));
		for (int i=0;i<position;i++) {
			AtomicFormula f = rule.getBody().get(i);
			ret.addAll(f.getAllVariables());
			
		}
		ret.retainAll(getRelevantVariables(rule, position));
		return ret;
	}*/
	static Set<VariableExpr> getRelevantVariables(Rule rule, int position) {
		Set<VariableExpr> ret = new HashSet<VariableExpr>();
		for (int i=position; i<rule.getBody().size();i++) {
			AtomicFormula f = rule.getBody().get(i);
			ret.addAll(f.getAllVariables());
		}
		ret.addAll(rule.getHead().getAllVariables());
		return ret;
	}
	private  SupplementaryPredicate(String name, int arity, Rule rule, int position) {
		super(name,arity);
		this.rule= rule;
		this.position = position;
	}
	public SupplementaryPredicate clone() {
		return new SupplementaryPredicate(getName(), getArity(),rule.clone(), position);
	}

	SupplementaryPredicate cloneWithNonZeroArity() {
		return getArity()!=0?
			new SupplementaryPredicate(getName(), getArity(),rule.clone(), position)
			: new SupplementaryPredicate(getName(), 1,rule.clone(), position);
	}
	public int getPosition() {
		return position;
	}


	public Rule getRule() {
		return rule;
	}
	public Predicate negate() {
		throw new RuntimeException("Cannot negate a supplementary predicate: "+ getName());
		
	}
	public Predicate switchOptionalFlag() {
		throw new RuntimeException("Cannot make a supplementary predicate optional: "+ getName());
	}
	
	public Predicate withoutQualification() {
		return clone();
	}
/*	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = super.hashCode();
		result = PRIME * result + position;
		result = PRIME * result + ((rule == null) ? 0 : rule.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!obj.getClass().equals(getClass()))
			return false;
		final SupplementaryPredicate other = (SupplementaryPredicate) obj;
		if (position != other.position)
			return false;
		if (rule == null) {
			if (other.rule != null)
				return false;
		} else if (!rule.equals(other.rule))
			return false;
		return true;
	}*/
}
