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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.stringtemplate.StringTemplate;

import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.runtime.service.types.TypeMap;
import com.ibm.research.rdf.store.sparql11.sqlwriter.FilterContext;

/**
 * models an AND or OR between expressions
 */
public class LogicalExpression extends Expression {

	public static enum ELogicalType {
		AND, OR
	};

	private List<Expression> components = new ArrayList<Expression>();

	public LogicalExpression(ELogicalType ltype) {
		super((ltype == ELogicalType.AND) ? EExpressionType.AND
				: EExpressionType.OR);
	}

	public LogicalExpression(Collection<? extends Expression> ex,
			ELogicalType ltype) {
		this(ltype);
		if (ex != null)
			components.addAll(ex);
	}

	public List<Expression> getComponents() {
		return components;
	}

	public List<Expression> getSubExpressions() {
		return components;
	}

	public void addComponent(Expression e) {
		components.add(e);
	}

	@Override
	public Short getReturnType() {
		return TypeMap.BOOLEAN_ID;
	}

	public TypeMap.TypeCategory getTypeRestriction(Variable v) {

		TypeMap.TypeCategory returnType = TypeMap.TypeCategory.NONE;
		for (Expression e : components) {
			if (!e.gatherVariables().contains(v))
				continue;
			returnType = e.getTypeRestriction(v);
		}
		return returnType;
	}

	public short getTypeEquality(Variable v) {
		short returnType = TypeMap.NONE_ID;
		for (Expression e : components) {
			if (!e.gatherVariables().contains(v))
				continue;
			returnType = e.getTypeEquality(v);
		}
		return returnType;
	}

	public String toString() {
		String separator = (getType() == EExpressionType.OR) ? "||" : "&&";
		StringBuilder sb = new StringBuilder();

		sb.append("(");
		for (int i = 0; i < components.size(); i++) {
			if (i > 0)
				sb.append(" " + separator + " ");
			Expression e = components.get(i);
			sb.append(e.toString());
		}
		sb.append(")");

		return sb.toString();
	}

	@Override
	public String getStringWithVarName() {
		String separator = (getType() == EExpressionType.OR) ? "OR" : "AND";
		StringBuilder sb = new StringBuilder();

		sb.append("(");
		for (int i = 0; i < components.size(); i++) {
			if (i > 0)
				sb.append(" " + separator + " ");
			Expression e = components.get(i);
			sb.append(e.getStringWithVarName());
		}
		sb.append(")");

		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((components == null) ? 0 : components.hashCode());
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
		LogicalExpression other = (LogicalExpression) obj;
		if (components == null) {
			if (other.components != null)
				return false;
		} else if (!components.equals(other.components))
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
		for (Expression e : components)
			e.renamePrefixes(base, declared, internal);
	}

	@Override
	public void reverseIRIs() {
		for (Expression e : components)
			e.reverseIRIs();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.research.rdf.store.sparql11.model.Expression#gatherBlankNodes()
	 */
	@Override
	public Set<BlankNodeVariable> gatherBlankNodes() {
		Set<BlankNodeVariable> ret = new HashSet<BlankNodeVariable>();
		for (Expression e : components)
			ret.addAll(e.gatherBlankNodes());
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.research.rdf.store.sparql11.model.Expression#gatherVariables()
	 */
	@Override
	public Set<Variable> gatherVariables() {
		Set<Variable> ret = new HashSet<Variable>();
		for (Expression e : components)
			ret.addAll(e.gatherVariables());
		return ret;
	}

	@Override
	public Set<Variable> getVariables() {
		return Collections.emptySet();
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
		for (Expression e : components)
			e.traverse(l);
		l.endExpression(this);
	}

	public boolean containsEBV() {
		boolean retType = false;
		for (Expression e : components) {
			if (e.containsEBV())
				retType = true;
		}
		return retType;
	}

	public boolean containsBound() {
		boolean retType = false;
		for (Expression e : components) {
			if (e.containsBound())
				retType = true;
		}
		return retType;
	}

	public boolean containsNotBound() {
		boolean retType = false;
		for (Expression e : components) {
			if (e.containsNotBound())
				retType = true;
		}
		return retType;
	}

	public boolean containsCast(Variable v) {
		boolean retType = false;
		for (Expression e : components) {
			if (e.containsCast(v))
				retType = true;
		}
		return retType;
	}

	@Override
	public String visit(FilterContext context, Store store) {
		StringTemplate t;
		String s;
		List<String> l = new ArrayList<String>();

		for (Expression e : getComponents()) {
			s = e.visit(context, store);
			if (s.equals(""))
				continue;
			// Mihaela: ToAsk why only var exp where the exp is null become EBV?
			if ((e instanceof ConstantExpression)
					|| (e instanceof VariableExpression && ((VariableExpression) e)
							.getExpression() == null)) {
				t = store.getInstanceOf("RDF_EBV");

				String fTerm = null;
				String fType = null;

				s = e.visit(context, store);
				fTerm = s;
				Set<Variable> variables = e.gatherVariables();
				assert (variables.size() < 2);
				if (variables.size() == 0) {
					short constType = e.getReturnType();
					String constValue = ((ConstantExpression) e).getConstant()
							.toDataString();
					fTerm = "'" + getSID(constValue, store.getMaxStringLen())
							+ "'";
					if (constType >= TypeMap.DATATYPE_NUMERICS_IDS_START
							&& constType <= TypeMap.DATATYPE_NUMERICS_IDS_END) {
						// cast((value as decfloat) <> 0)
						fTerm = "CAST('" + constValue + "' AS "
								+ store.getDatatype("NUMERIC") + ")";
						t = store.getInstanceOf("RDF_NE");
						t.setAttribute("left", fTerm);
						t.setAttribute("right", 0);
					} else if (constType == TypeMap.BOOLEAN_ID) {
						// value=true
						t = store.getInstanceOf("RDF_EQ");
						t.setAttribute("left", fTerm);
						t.setAttribute("right", "'true'");
					} else {
						// value <> "";
						t = store.getInstanceOf("RDF_NE");
						t.setAttribute("left", fTerm);
						t.setAttribute("right", "''");
					}
				} else {
					for (Variable v : variables) {
						fType = context.getVarMap().get(v.getName()).snd;
						fTerm = context.getVarMap().get(v.getName()).fst;
					}
					if (fType == null)
						fType = TypeMap.IRI_ID + "";
					t.setAttribute("fterm", fTerm);
					t.setAttribute("ftype", fType);
					t.setAttribute("nrstart",
							TypeMap.DATATYPE_NUMERICS_IDS_START);
					t.setAttribute("nrend", TypeMap.DATATYPE_NUMERICS_IDS_END);
					t.setAttribute("tstring", TypeMap.STRING_ID);
					t.setAttribute("pstring", TypeMap.SIMPLE_LITERAL_ID);
					t.setAttribute("tboolean", TypeMap.BOOLEAN_ID);
				}

				s = t.toString();
			}
			l.add(s);
		}

		if (getType() == Expression.EExpressionType.OR) {
			t = store.getInstanceOf("logical_or_expressions");
		} else {
			t = store.getInstanceOf("logical_or_expressions");
		}
		t.setAttribute("expressions", l);

		return t.toString();
	}
}
