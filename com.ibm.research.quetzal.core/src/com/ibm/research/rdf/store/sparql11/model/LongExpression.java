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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.runtime.service.types.TypeMap;
import com.ibm.research.rdf.store.sparql11.sqlwriter.FilterContext;

/**
 * a "long" expression is something like a + b + c - d - e or something like a * b * c / d / e. The parameter type T is simply the type of the intermediate operators
 * Examples for T: Expression.EAdditiveOp or Expression.EMultiplicativeOp 
 */
public class LongExpression<T> extends Expression {

	private List<Expression> subExpressions;
	private List<T> operators;
	
	/**
	 * @param type
	 */
	public LongExpression(EExpressionType type, List<Expression> expr, List<T> ops) {
		super(type);
		subExpressions = expr;
		operators = ops;
	}
	
	public List<Expression> getSubExpressions() {
		return subExpressions;
	}

	@Override
	public Short getReturnType() {
		return TypeMap.NONE_ID;
	}
	
	public short getTypeEquality(Variable v) {
		return TypeMap.NONE_ID;
	}
	
	
	public TypeMap.TypeCategory getTypeRestriction(Variable v){
		if(!this.gatherVariables().contains(v))return TypeMap.TypeCategory.NONE;
		else return TypeMap.TypeCategory.NUMERIC;
	}
	
	public List<T> getOperators() {
		return operators;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((operators == null) ? 0 : operators.hashCode());
		result = prime * result
				+ ((subExpressions == null) ? 0 : subExpressions.hashCode());
		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LongExpression other = (LongExpression) obj;
		if (operators == null) {
			if (other.operators != null)
				return false;
		} else if (!operators.equals(other.operators))
			return false;
		if (subExpressions == null) {
			if (other.subExpressions != null)
				return false;
		} else if (!subExpressions.equals(other.subExpressions))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(subExpressions.get(0).toString());
		for(int i = 0; i < operators.size(); i++) {
			sb.append(" " + operators.get(i).toString() + " ");
			sb.append(subExpressions.get(i + 1).toString());
		}
		sb.append(")");
		return sb.toString();
	}

	@Override
	public String getStringWithVarName() {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(subExpressions.get(0).getStringWithVarName());
		for(int i = 0; i < operators.size(); i++) {
			sb.append(" " + operators.get(i).toString() + " ");
			sb.append(subExpressions.get(i + 1).getStringWithVarName());
		}
		sb.append(")");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see com.ibm.research.rdf.store.sparql11.model.Expression#renamePrefixes(java.lang.String, java.util.Map, java.util.Map)
	 */
	@Override
	public void renamePrefixes(String base, Map<String, String> declared, Map<String, String> internal) {
		for(Expression e: subExpressions) e.renamePrefixes(base, declared, internal);
	}
	
	@Override
	public void reverseIRIs() {
		for(Expression e: subExpressions) e.reverseIRIs();
	}

	/* (non-Javadoc)
	 * @see com.ibm.research.rdf.store.sparql11.model.Expression#gatherBlankNodes()
	 */
	@Override
	public Set<BlankNodeVariable> gatherBlankNodes() {
		Set<BlankNodeVariable> ret = new HashSet<BlankNodeVariable>();
		for(Expression e: subExpressions) ret.addAll(e.gatherBlankNodes());
		return ret;
	}

	/* (non-Javadoc)
	 * @see com.ibm.research.rdf.store.sparql11.model.Expression#gatherVariables()
	 */
	@Override
	public Set<Variable> gatherVariables() {
		Set<Variable> ret = new HashSet<Variable>();
		for(Expression e: subExpressions) ret.addAll(e.gatherVariables());
		return ret;
	}

	@Override
	public Set<Variable> getVariables() {
		return Collections.emptySet();
	}

	/* (non-Javadoc)
	 * @see com.ibm.research.rdf.store.sparql11.model.Expression#traverse(com.ibm.research.rdf.store.sparql11.model.IExpressionTraversalListener)
	 */
	@Override
	public void traverse(IExpressionTraversalListener l) {
		l.startExpression(this);
		for(Expression e: subExpressions) e.traverse(l); 
		l.endExpression(this);
	}

	public boolean containsEBV(){
		return false;
	}
	
	public boolean containsBound(){
		return false;
	}
	
	public boolean containsNotBound(){
		return false;
	}
	
	@Override
	public boolean containsCast(Variable v) {		
		for(Expression e: subExpressions) 
			if(e.containsCast(v)) return true;
		return false;
	}

	@Override
	public String visit(FilterContext context, Store store) {
		throw new UnsupportedOperationException("Long expressions aren't supported in SQL");
	}
	
}
