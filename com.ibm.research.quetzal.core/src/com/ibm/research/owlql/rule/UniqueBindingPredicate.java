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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UniqueBindingPredicate extends Predicate {

	private List<Integer> boundArguments;
	
	public static UniqueBindingPredicate createUniqueBindingPredicate(PredicateAdornment predAd) {
		StringBuffer buf = new StringBuffer();
		buf.append(predAd.getPredicate().getName()+"_");
		for (int i=0;i<predAd.getPredicate().getArity();i++) {
			if (predAd.getBoundArguments().contains(i)) {
				buf.append("b");
			} else {
				buf.append("f");
			}
		}
		return new UniqueBindingPredicate(buf.toString(),predAd.getPredicate().getArity(),predAd.getBoundArguments(),
				predAd.getPredicate().isNegated(), predAd.getPredicate().isOptional());
	}
	private UniqueBindingPredicate(String name, int arity, List<Integer> boundArgumentPositions) {
		this(name, arity, boundArgumentPositions, false, false);
	}
	private UniqueBindingPredicate(String name, int arity, List<Integer> boundArgumentPositions, boolean isNegated, boolean isOptional) {
		super(name, arity, isNegated, isOptional);
		this.boundArguments = new ArrayList<Integer>(boundArgumentPositions);
	}
	public UniqueBindingPredicate clone() {
		return new UniqueBindingPredicate(getName(),getArity(), new ArrayList<Integer>(boundArguments), isNegated(), isOptional());
	}
	
	public Predicate negate() {
		return  new UniqueBindingPredicate(getName(),getArity(), new ArrayList<Integer>(boundArguments), !isNegated(), isOptional());
	}
	public Predicate switchOptionalFlag() {
		return new UniqueBindingPredicate(getName(),getArity(), new ArrayList<Integer>(boundArguments), isNegated(), !isOptional());
	}
	public Predicate withoutQualification() {
		return  new UniqueBindingPredicate(getName(),getArity(), new ArrayList<Integer>(boundArguments), false, false);
	}
	
	UniqueBindingPredicate cloneWithNonZeroArity() {
		return getArity()!=0?
				new UniqueBindingPredicate(getName(),getArity(), new ArrayList<Integer>(boundArguments),isNegated(),isOptional())
				:  new UniqueBindingPredicate(getName(),1, new ArrayList<Integer>(boundArguments), isNegated(), isOptional());
				 
		
	}
	public List<Integer> getBoundArguments() {
		return Collections.unmodifiableList(boundArguments);
	}
	boolean isBoundArgument(int pos) {
		return boundArguments.contains(pos);
	}
	List<VariableExpr> getBoundVariablesList(AtomicFormula f) {
		
		assert this.equals(f.getPredicate());
		List<VariableExpr> ret = new ArrayList<VariableExpr>();
		Set<VariableExpr> alreadySeenVars = new HashSet<VariableExpr>();
		for (int i=0;i<f.getArguments().size();i++) {
			Expr e = f.getArguments().get(i);
			if (isBoundArgument(i) 
			&& e.isVariable() 
			&& alreadySeenVars.add((VariableExpr) e)) {
				ret.add((VariableExpr)e);
			}
		}
		return ret;
	}
	/*@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = super.hashCode();
		result = PRIME * result + ((boundArguments == null) ? 0 : boundArguments.hashCode());
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
		final UniqueBindingPredicate other = (UniqueBindingPredicate) obj;
		if (boundArguments == null) {
			if (other.boundArguments != null)
				return false;
		} else if (!boundArguments.equals(other.boundArguments))
			return false;
		return true;
	}*/
	

}
