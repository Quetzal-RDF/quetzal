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

import java.util.Iterator;
import java.util.List;

public class PredicateAdornment implements Adornment {

	private Predicate predicate;
	private List<Integer> boundArguments;
	
	
	public PredicateAdornment(Predicate predicate, List<Integer> boundArgumentPositions) {
		super();
		this.predicate = predicate;
		this.boundArguments = boundArgumentPositions;
		assert checkConsistency();
	}
	
	public boolean checkConsistency() {
		for (Iterator<Integer> it = boundArguments.iterator();it.hasNext();) {
			int pos = it.next();
			assert pos<predicate.getArity()
				: "bound argument index "+pos+" is greater or equals to the arity ("+predicate.getArity()
				+") of the predicate " + predicate.getName();
		}
		return true;
	} 

	/**
	 * returns the list of indexes of bound arguments (the first argument has index 0).
	 * @return
	 */
	public List<Integer> getBoundArguments() {
		return boundArguments;
	}
	
	public boolean isBoundArgument(int pos) {
		return boundArguments.contains(pos);
	}
	
	/**
	 * returns the list of free arguments. Note that list is 
	 * compute as the difference between {@link AtomicFormula#getArguments()}
	 * and {@link #getBoundArguments()}. Therefore, it should be 
	 * recomputed if one of those two lists is updated. 
	 * @return
	 */
	/*public List<VariableExpr> getFreeVariables() {
		List ret = new ArrayList<Expr>(pred.getArguments());
		ret.removeAll(boundArguments);
		return new ArrayList<VariableExpr>(ret);
	}*/
	
	public boolean equals(Object o) {
		if (o==this) {
			return true;
		} 
		if (o instanceof PredicateAdornment) {
			PredicateAdornment other =(PredicateAdornment)o;
			return predicate.equals(other.predicate)
				   && boundArguments.equals(other.boundArguments);
		}
		return false;
	}
	
	public int hashCode() {
		return predicate.hashCode()+ 31*boundArguments.hashCode();
	}


	
	public Predicate getPredicate() {
		return predicate;
	}

	public boolean isAdornmentOnPredicate() {
		return true;
	}

	public boolean isAdornmentOnRule() {
	return false;
	}

	public PredicateAdornment asPredicateAdornment() {
		return this;
	}

	public RuleAdornment asRuleAdornment() {
		return null;
	}

	@Override
	public String toString() {
		return "PredicateAdornment [predicate=" + predicate
				+ ", boundArguments=" + boundArguments + "]";
	}
	
	
	
	

}
