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
import java.util.List;

public class MagicSetPredicate extends Predicate {

	private UniqueBindingPredicate predicate;
	
	static MagicSetPredicate createMagicSetPredicate(UniqueBindingPredicate predicate) {
		int arity = 0;
		for (int i=0;i<predicate.getArity();i++) {
			if (predicate.getBoundArguments().contains(i)) {
				arity++;
			}
		}
		return new MagicSetPredicate("m_"+predicate.getName(),arity,predicate);
	}
	static AtomicFormula createMagicSetAtomicFormula(UniqueBindingPredicate predicate, AtomicFormula af) {
		return createMagicSetAtomicFormula(predicate, af,false);
	}
	static AtomicFormula createMagicSetAtomicFormula(UniqueBindingPredicate predicate, AtomicFormula af, boolean isGoal) {
		assert predicate.equals(af.getPredicate());
		MagicSetPredicate magicPred= MagicSetPredicate.createMagicSetPredicate(predicate);
		List<Expr> args = new ArrayList<Expr>(predicate.getArity());
		for (int i=0;i<af.getArguments().size();i++) {
			if (predicate.isBoundArgument(i)) {
				Expr arg = af.getArguments().get(i);
				if (!isGoal) {
					assert arg.isVariable()
						:"Rule is not rectified. "+af+" has a constant argument: "+ arg;
				}
				args.add(arg);
			}
		}
		return new AtomicFormula(magicPred,args);
	}
	private MagicSetPredicate(String name, int arity, UniqueBindingPredicate predicate) {
		super(name,arity);
		this.predicate = predicate;
	}

	public UniqueBindingPredicate getUnderlyingPredicate() {
		return predicate;
	}
	@Override
	public MagicSetPredicate clone() {
		return new MagicSetPredicate(getName(),getArity(), predicate.clone());
	}
	MagicSetPredicate cloneWithNonZeroArity() {
		return getArity()!=0 ? 
				new MagicSetPredicate(getName(),getArity(), predicate.clone())
				: new MagicSetPredicate(getName(),1, predicate.cloneWithNonZeroArity());
	}
	
	public Predicate negate() {
		throw new RuntimeException("Cannot negate a magic-set predicate: "+ getName());
		
	}
	public Predicate switchOptionalFlag() {
		throw new RuntimeException("Cannot make a magic-set predicate optional: "+ getName());
	}
	
	public Predicate withoutQualification() {
		return clone();
	}
	/*@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = super.hashCode();
		result = PRIME * result + ((predicate == null) ? 0 : predicate.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final MagicSetPredicate other = (MagicSetPredicate) obj;
		if (predicate == null) {
			if (other.predicate != null)
				return false;
		} else if (!predicate.equals(other.predicate))
			return false;
		return true;
	}*/
	
}
