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

import java.net.URI;


/**
 * a constant argument of a {@link AtomicFormula}
 * @author <a href="mailto:achille@us.ibm.com">Achille Fokoue</a>
 *
 */
public class ConstantExpr extends Expr {

	private Object value;
	
	
	public ConstantExpr(String value) {
		super();
		this.value = value;
	}
	public ConstantExpr(Integer value) {
		super();
		this.value = value;
	}
	public ConstantExpr(Double value) {
		super();
		this.value = value;
	}
	public ConstantExpr(Long value) {
		super();
		this.value = value;
	}
	public ConstantExpr(URI value) {
		super();
		this.value = value;
	}

	public ConstantExpr(Object value) {
		super();
		this.value = value;
	}
	@Override
	public boolean isConstant() {
		return true;
	}
	@Override
	public boolean isVariable() {
		return false;
	}

	public Object getValue() {
		return value;
	}

	
	
	
	
	@Override
	public ConstantExpr asConstant() {
		return this;
	}
	@Override
	public VariableExpr asVariable() {
		return null;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		ConstantExpr other = (ConstantExpr) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	public String toString() {
		if (value instanceof String) {
			return "'"+ value.toString()+"'";
		} else if (value instanceof URI){
			return "<"+value.toString()+">";
		} 
		return value.toString();
	}
	@Override
	public ConstantExpr clone() {
		if (value instanceof Integer) {
			return new ConstantExpr((Integer) value);
		} else if (value instanceof Long) {
			return new ConstantExpr((Long) value);
		} else if (value instanceof Double) {
			return new ConstantExpr((Double) value);
		} else if (value instanceof URI) {
			return new ConstantExpr((URI) value);
		} else  {
		
			return new ConstantExpr(value.toString());
		}
	}
	
	

}
