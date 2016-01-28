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
import java.util.Map;
import java.util.Set;

import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.runtime.service.types.TypeMap;
import com.ibm.research.rdf.store.sparql11.sqlwriter.FilterContext;

/**
 *
 */
public class VariableExpression extends Expression {
	private Expression expression;
	private Variable variable;

	public VariableExpression(String v) {
		this(new Variable(v));
	}
	public VariableExpression(Variable v) {
		this(null, v);
	}
	

	public VariableExpression(Expression e) {
		this(e,(Variable) null);
	}

	public VariableExpression(Expression e, Variable v) {
		super(EExpressionType.VAR);
		expression = e;
		variable = v;
	}
	public VariableExpression(Expression e, String v) {
		this(e, new Variable(v));
	}
	
	

	public String getVariable() {
		return variable==null? null: variable.getName();
	}

	public void setVariable(Variable v) {
		variable = v;
	}
	public void setVariable(String v) {
		setVariable(new Variable(v));
	}
	

	public Expression getExpression() {
		return expression;
	}

	public void setExpression(Expression e) {
		expression = e;
	}

	@Override
	public Short getReturnType() {
		if (expression != null) {
			return expression.getReturnType();
		} else {
			return TypeMap.NONE_ID;
		}
	}

	public short getTypeEquality(Variable v) {
		return TypeMap.NONE_ID;
	}

	public TypeMap.TypeCategory getTypeRestriction(Variable v) {
		if (!this.gatherVariables().contains(v))
			return TypeMap.TypeCategory.NONE;
		else if (expression != null)
			return expression.getTypeRestriction(v);
		else
			return TypeMap.TypeCategory.NONE;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((variable == null) ? 0 : variable.hashCode());
		result = prime * result
				+ ((expression == null) ? 0 : expression.hashCode());
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
		VariableExpression other = (VariableExpression) obj;
		if (variable == null) {
			if (other.variable != null)
				return false;
		} else if (!variable.equals(other.variable))
			return false;
		if (expression == null) {
			if (other.expression != null)
				return false;
		} else if (!expression.equals(other.expression))
			return false;
		return true;
	}

	@Override
	public String toString() {
		if ((expression == null) && (variable == null)) {
			return "";
		}
		if (variable == null) {
			return expression.toString();
		}
		if (expression == null) {
			return variable.toString();
		}
		return "(" + expression.toString() + " AS " + variable + ")";
	}

	@Override
	public String getStringWithVarName() {
		if ((expression == null) && (variable == null)) {
			return "";
		}
		if (variable == null) {
			return expression.getStringWithVarName();
		}
		if (expression == null) {
			return variable.getName();
		}
		return "(" + expression.getStringWithVarName() + " AS " + variable.getName()
				+ ")";
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
		if (expression != null) {
			expression.renamePrefixes(base, declared, internal);
		}
	}

	@Override
	public void reverseIRIs() {
		if (expression != null) {
			expression.reverseIRIs();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.research.rdf.store.sparql11.model.Expression#gatherBlankNodes()
	 */
	@Override
	public Set<BlankNodeVariable> gatherBlankNodes() {
		Set<BlankNodeVariable> bnodes = new HashSet<BlankNodeVariable>();
		if (expression != null) {
			bnodes.addAll(expression.gatherBlankNodes());
		}
		return bnodes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.research.rdf.store.sparql11.model.Expression#gatherVariables()
	 */
	@Override
	public Set<Variable> gatherVariables() {
		Set<Variable> ret = new HashSet<Variable>();
		if (variable != null) {
			ret.add(variable);
		}
		if (expression != null) {
			ret.addAll(expression.gatherVariables());
		}
		return ret;
	}

	@Override
	public Set<Variable> getVariables() {
		if (variable != null) {
			return Collections.singleton(variable);
		} else {
			return Collections.emptySet();
		}
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

	@Override
	public boolean containsEBV() {
		if (expression == null) {
			return true;
		} else
			return false;
	}

	@Override
	public boolean containsBound() {
		return false;
	}

	@Override
	public boolean containsNotBound() {
		return false;
	}

	@Override
	public boolean containsCast(Variable v) {
		return false;
	}

	@Override
	public String visit(FilterContext context, Store store) {
		String v = getVariable();
		if (context.getVarMap() != null && context.getVarMap().keySet().contains(v)) {
			String e = null;
			if (getExpression() != null) {
				try {
					e = getExpression().visit(context, store);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			if (e != null) {
				return "(" + e + ") AS " + context.getVarMap().get(v).fst;
			}
			return (context.getVarMap().get(v)).fst;
		} else {
			return getVariable().toString();
		}
	}
}
