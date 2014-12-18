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
 package com.ibm.research.rdf.store.sparql11.model;

import java.util.Map;

/**
 * models the order condition
 */
public class OrderCondition {
	public static enum EOrderType { ASC, DESC };
	
	private EOrderType type;
	private Expression expression;
	
	public OrderCondition(Expression e) {
		this(e, EOrderType.ASC);
	}
	
	public OrderCondition(Expression e, EOrderType t) {
		expression = e;
		type = t;
	}

	public EOrderType getType() {
		return type;
	}

	public Expression getExpression() {
		return expression;
	}
	
	public void renamePrefixes(String base, Map<String, String> declared, Map<String, String> internal) {
		expression.renamePrefixes(base, declared, internal);
	}
	
	public void reverseIRIs() {
		expression.reverseIRIs();
	}
	
	@Override
	public String toString() {
		return type + " (" + expression.toString()+")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((expression == null) ? 0 : expression.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		OrderCondition other = (OrderCondition) obj;
		if (expression == null) {
			if (other.expression != null)
				return false;
		} else if (!expression.equals(other.expression))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
}
