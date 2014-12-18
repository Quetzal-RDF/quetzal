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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.runtime.service.types.TypeMap;
import com.ibm.research.rdf.store.sparql11.sqlwriter.FilterContext;

/**
 * Expression consisting of a single literal.
 */
public class ConstantExpression extends Expression {
	private Constant constant;

	ConstantExpression() {
		super(EExpressionType.CONSTANT);
	}

	public ConstantExpression(StringLiteral literal) {
		super(EExpressionType.CONSTANT);
		constant = new Constant(literal);

	}

	public ConstantExpression(IRI i) {
		super(EExpressionType.CONSTANT);
		constant = new Constant(i);

	}

	public ConstantExpression(String s, Number n) {
		super(EExpressionType.CONSTANT);
		constant = new Constant(s, n);

	}

	public ConstantExpression(Boolean b) {
		super(EExpressionType.CONSTANT);
		constant = new Constant(b);

	}

	public ConstantExpression(Constant c) {
		super(EExpressionType.CONSTANT);
		constant = c;
	}

	public ConstantExpression(Number n) {
		super(EExpressionType.CONSTANT);
		constant = new Constant(n);
	}

	public Constant getConstant() {
		return constant;
	}

	public Short getReturnType() {
		return constant == null ? TypeMap.NONE_ID : constant.toDataType();
	}

	public TypeMap.TypeCategory getTypeRestriction(Variable v) {
		return TypeMap.TypeCategory.NONE;
	}

	public short getTypeEquality(Variable v) {
		return TypeMap.NONE_ID;
	}

	public String toString() {
		return constant == null ? "NULL" : constant.toString();
	}

	@Override
	public String getStringWithVarName() {
		return constant == null ? "NULL" : constant.toString();
	}

	public String toTypedString() {
		return constant == null ? "NULL" : constant.toTypedString();
	}

	public String toDataString() {
		return constant == null ? "NULL" : constant.toDataString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((constant == null) ? 0 : constant.hashCode());
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
		ConstantExpression other = (ConstantExpression) obj;
		if (constant == null) {
			if (other.constant != null)
				return false;
		} else if (!constant.equals(other.constant))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.research.rdf.store.sparql11.model.Expression#renamePrefixes(java.lang.
	 * String, java.util.Map, java.util.Map)
	 */
	@Override
	public void renamePrefixes(String base, Map<String, String> declared,
			Map<String, String> internal) {
		if (constant == null) {
			return;
		}
		EConstantType type = constant.getConstType();
		if (type == EConstantType.IRI) {
			constant.getIRI().rename(base, declared, internal);
		} else if ((type == EConstantType.LITERAL)
				&& (constant.getLiteral().getType() != null)) {
			constant.getLiteral().getType().rename(base, declared, internal);
		}
	}

	@Override
	public void reverseIRIs() {
		if (constant == null) {
			return;
		}
		EConstantType type = constant.getConstType();
		if (type == EConstantType.IRI) {
			constant.getIRI().reverse();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.research.rdf.store.sparql11.model.Expression#gatherBlankNodes()
	 */
	@Override
	public Set<BlankNodeVariable> gatherBlankNodes() {
		return new HashSet<BlankNodeVariable>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.research.rdf.store.sparql11.model.Expression#gatherVariables()
	 */
	@Override
	public Set<Variable> gatherVariables() {
		return getVariables();
	}

	@Override
	public Set<Variable> getVariables() {
		return new HashSet<Variable>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.research.rdf.store.sparql11.model.Expression#traverse(com.ibm.research.rdf.store
	 * .sparql11.model.IExpressionTraversalListener)
	 */
	@Override
	public void traverse(IExpressionTraversalListener l) {
		l.startExpression(this);
		l.endExpression(this);
	}

	public boolean containsEBV() {
		return true;
	}

	public boolean containsBound() {
		return false;
	}

	public boolean containsNotBound() {
		return false;
	}

	public boolean containsCast(Variable v) {
		return false;
	}

	@Override
	public String visit(FilterContext context, Store store) {
		return "'" + getSID(toDataString(), store.getMaxStringLen()) + "'";
	}
	
	public String getSQLConstantExpressionAsType(FilterContext context,
			Store store) {
		assert this instanceof ConstantExpression;
		if (getReturnType() >= TypeMap.DATATYPE_NUMERICS_IDS_START
				&& getReturnType() <= TypeMap.DATATYPE_NUMERICS_IDS_END) {
			return getSID(toDataString(), store.getMaxStringLen());
		}
		if (getReturnType() == TypeMap.DATE_ID) {
			return "'" + toDataString() + "'";
		}
		return visit(context, store);
	}
}
