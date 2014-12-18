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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class RuleAdornment implements Adornment{
	
	private Rule rule;	
	private int position;
	private Set<VariableExpr> boundVariables;
	private Set<VariableExpr> freeVariables;
	
	public RuleAdornment(Rule rule, Set<VariableExpr> boundVariables, Set<VariableExpr> freeVariables, int position) {
		super();
		this.rule = rule;
		this.boundVariables = boundVariables;
		this.freeVariables = freeVariables;
		this.position = position;
		assert checkConsistency();
	}
	
	private boolean checkConsistency() {
		Set<VariableExpr> ruleVars = getAllRuleVariables();
		Set<Expr> boundAndFreeVariables = new HashSet<Expr>(boundVariables);
		boundAndFreeVariables.addAll(freeVariables);
		assert boundAndFreeVariables.equals(ruleVars)
		:"freeVars: "+freeVariables+"\n boundVars: "+boundVariables
		+"\n all rule vars: "+getAllRuleVariables();
		Set<VariableExpr> inter = new HashSet<VariableExpr>(boundVariables);
		inter.retainAll(freeVariables);
		assert inter.isEmpty()
		:"The following variables appear to be both free and bound: "+ inter;
		return true;
	}
	
	public  Set<VariableExpr> getBoundVariables() {
		return boundVariables;
	}
	
	public Set<VariableExpr> getFreeVariables() {
		return freeVariables;
	}
	
	public Set<VariableExpr> getAllRuleVariables() {
		return new HashSet<VariableExpr>(rule.getAllRuleVariables());
	}
	
	public boolean equals(Object o) {
		if (o==this) {
			return true;
		} 
		if (o instanceof RuleAdornment) {
			RuleAdornment other =(RuleAdornment)o;
			return rule.equals(other.rule)
				   && position == other.position
				   && boundVariables.equals(other.boundVariables);
		}
		return false;
	}
	
	public int hashCode() {
		return rule.hashCode()+ 31*(boundVariables.hashCode()+31*position);
	}
	
	
	

	
	public int getPosition() {
		return position;
	}
	
	public RuleAdornment next() {
		if (position+1<rule.getBody().size()) {
			int newPos = position+1;
			Set<VariableExpr> newFreeVars  = new HashSet<VariableExpr>(getFreeVariables());
			Set<VariableExpr> newBoundVars = new HashSet<VariableExpr>(getBoundVariables());
			AtomicFormula curAf = rule.getBody().get(position);
			if (!curAf.getPredicate().isNegated()
			&& !curAf.getPredicate().isOptional()) {
				// make sure that the current atomic formula is not negated or optional
				// before changing the adornment
				for (Iterator<? extends Expr> it= curAf.getArguments().iterator();it.hasNext();) {
					Expr e = it.next();
					if (e.isVariable()) {
						VariableExpr var = (VariableExpr) e;
						newBoundVars.add(var);
						newFreeVars.remove(var);
					} else {
						assert e.isConstant();
					}
				}
			}
			return new RuleAdornment(rule,newBoundVars,newFreeVars,newPos);
	
		} else {
			return null;
		}
	}

	public PredicateAdornment asPredicateAdornment() {
		return null;
	}

	public RuleAdornment asRuleAdornment() {
		return this;
	}

	public boolean isAdornmentOnPredicate() {
		return false;
	}

	public boolean isAdornmentOnRule() {
		return true;
	}

	public Rule getRule() {
		return rule;
	}
	
	
	
}
