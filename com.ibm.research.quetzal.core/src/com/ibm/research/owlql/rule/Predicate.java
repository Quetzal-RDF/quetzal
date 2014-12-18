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

public class Predicate {

	public static final char NEGATION_SYMBOL = '!';
	public static final char OPTIONAL_SYMBOL = '*';
	private boolean isNegated;
	private boolean isOptional;
	private String name;
	protected int arity;
	public Predicate(String name, int arity,  boolean isNegated, boolean isOptional) {
		super();
		if (isNegated && isOptional) {
			throw new RuntimeException("Negated and optional predicate not allowed!");
		}
		this.name = name;
		this.arity = arity;
		this.isNegated = isNegated;
		this.isOptional = isOptional;
	}
	public Predicate(String name, int arity) {
		this(name, arity,false, false);
	}
	public Predicate clone() {
		return new Predicate(name,arity,isNegated, isOptional);
	}
	Predicate cloneWithNonZeroArity() {
		return arity!=0?
				new Predicate(name,arity): new Predicate(name, 1);
	}
	public int getArity() {
		return arity;
	}
	public String getName() {
		return name;
	}
	public boolean isNegated() {
		return isNegated;
	}
	
	public Predicate negate() {
		if (!isNegated && isOptional) {
			throw new RuntimeException("Negated and optional predicate not allowed!");
		}
		return new Predicate(name, arity, !isNegated, isOptional);
	}
	public boolean isOptional() {
		return isOptional;
	}
	public Predicate switchOptionalFlag() {
		if (isNegated && !isOptional) {
			throw new RuntimeException("Negated and optional predicate not allowed!");
		}
		return new Predicate(name, arity, isNegated, !isOptional);
	}
	
	public Predicate withoutQualification() {
		return new Predicate(name, arity, false, false);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + arity;
		result = prime * result + (isNegated ? 1231 : 1237);
		result = prime * result + (isOptional ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Predicate other = (Predicate) obj;
		if (arity != other.arity)
			return false;
		if (isNegated != other.isNegated)
			return false;
		if (isOptional != other.isOptional)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	public String toString() {
		return (isNegated? NEGATION_SYMBOL+"":"")+(isOptional? OPTIONAL_SYMBOL:"")+name;
	}
	
	
}
