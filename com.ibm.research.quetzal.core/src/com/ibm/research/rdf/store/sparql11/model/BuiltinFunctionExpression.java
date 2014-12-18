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

import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.runtime.service.types.TypeMap;
import com.ibm.research.rdf.store.sparql11.sqlwriter.FilterContext;
import com.ibm.wala.util.collections.HashSetFactory;

/**
 * Builtin function expressions.
 */
public class BuiltinFunctionExpression extends Expression {

	private EBuiltinType builtinType;
	private List<Expression> arguments = new ArrayList<Expression>();
	private Pattern patternArgs = null; // used by certain built-in calls like
										// EXITS, NOT_EXISTS etc.,
	private short returnType;

	public BuiltinFunctionExpression(EBuiltinType builtin) {
		super(EExpressionType.BUILTIN_FUNC);
		builtinType = builtin;
		setReturnType(builtinType);
	}

	public BuiltinFunctionExpression(EBuiltinType builtin,
			Collection<? extends Expression> args) {
		super(EExpressionType.BUILTIN_FUNC);
		builtinType = builtin;
		arguments.addAll(args);
		setReturnType(builtinType);
	}

	public BuiltinFunctionExpression(EBuiltinType builtin, Expression... args) {
		super(EExpressionType.BUILTIN_FUNC);
		builtinType = builtin;
		setReturnType(builtinType);
		for (Expression e : args)
			if (e != null)
				arguments.add(e);
	}

	public BuiltinFunctionExpression(EBuiltinType builtin, Pattern args) {
		super(EExpressionType.BUILTIN_FUNC);
		builtinType = builtin;
		setReturnType(builtinType);
		patternArgs = args;
	}

	public EBuiltinType getBuiltinType() {
		return builtinType;
	}

	private void setReturnType(EBuiltinType type) {
		switch (builtinType) {
		case REGEX:
		case BOUND:
		case ISBLANK:
		case ISLITERAL:
		case ISIRI:
		case LANGMATCHES:
		case EXISTS:
		case NOT_EXISTS:
		case ISNUMERIC:
		case SAMETERM:
			returnType = TypeMap.BOOLEAN_ID;
			break;
		case COALESCE:
			short max = 0;
			for (Expression e : arguments) {
				if (e.getReturnType() > max) {
					System.out.println(e + " has: " + e.getReturnType());
					max = e.getReturnType();
				}
			}
			returnType = max;
			break;
		case STR:
			returnType = TypeMap.SIMPLE_LITERAL_ID;
			break;
		default:
			returnType = TypeMap.STRING_ID;
			break;
		}
	}

	public TypeMap.TypeCategory getTypeRestriction(Variable v) {
		if (!this.gatherVariables().contains(v))
			return TypeMap.TypeCategory.NONE;
		switch (builtinType) {
		case REGEX:
		case LANG:
		case STR:
		case STRBEFORE:
		case STRAFTER:
		case LANGMATCHES:
			return TypeMap.TypeCategory.STRING;
		case IF:
		case COALESCE:
			for (Expression e : getArguments()) {
				if (e.getTypeRestriction(v) != TypeMap.TypeCategory.NONE) {
					return e.getTypeRestriction(v);
				}
			}

		}
		return TypeMap.TypeCategory.NONE;
	}

	public Short getReturnType() {
		return returnType;
	}

	public short getTypeEquality(Variable v) {
		return TypeMap.NONE_ID;
	}

	public List<Expression> getArguments() {
		return arguments;
	}

	public Pattern getPatternArguments() {
		return patternArgs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		switch (builtinType) {
		case EXISTS:
			builder.append("EXISTS (");
			builder.append(patternArgs.toString());
			break;
		case NOT_EXISTS:
			builder.append(" NOT EXISTS (");
			builder.append(patternArgs.toString());
			break;
		default:
			builder.append(builtinType + "(");
			for (int i = 0; i < arguments.size(); i++) {
				if (i > 0)
					builder.append(", ");
				builder.append(arguments.get(i));
			}
		}
		builder.append(")");

		return builder.toString();
	}

	@Override
	public String getStringWithVarName() {
		StringBuilder builder = new StringBuilder();
		switch (builtinType) {
		case EXISTS:
			builder.append("EXISTS (");
			builder.append(patternArgs.toString());
			break;
		case NOT_EXISTS:
			builder.append(" NOT EXISTS (");
			builder.append(patternArgs.toString());
			break;
		default:
			builder.append(builtinType + "(");
			for (int i = 0; i < arguments.size(); i++) {
				if (i > 0)
					builder.append(", ");
				builder.append(arguments.get(i).getStringWithVarName());
			}
		}
		builder.append(")");

		return builder.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((arguments == null) ? 0 : arguments.hashCode());
		result = prime * result
				+ ((builtinType == null) ? 0 : builtinType.hashCode());
		result = prime * result
				+ ((patternArgs == null) ? 0 : patternArgs.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BuiltinFunctionExpression other = (BuiltinFunctionExpression) obj;
		if (arguments == null) {
			if (other.arguments != null)
				return false;
		} else if (!arguments.equals(other.arguments))
			return false;
		if (builtinType != other.builtinType)
			return false;
		if (patternArgs == null) {
			if (other.patternArgs != null) {
				return false;
			}
		} else if (!patternArgs.equals(other.patternArgs)) {
			return false;
		}
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
		for (Expression e : arguments)
			e.renamePrefixes(base, declared, internal);
		if (patternArgs != null)
			patternArgs.renamePrefixes(base, declared, internal);
	}

	@Override
	public void reverseIRIs() {
		for (Expression e : arguments)
			e.reverseIRIs();
		if (patternArgs != null)
			patternArgs.reverseIRIs();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.research.rdf.store.sparql11.model.Expression#gatherBlankNodes()
	 */
	@Override
	public Set<BlankNodeVariable> gatherBlankNodes() {
		Set<BlankNodeVariable> ret = new HashSet<BlankNodeVariable>();
		for (Expression e : arguments)
			ret.addAll(e.gatherBlankNodes());
		if (patternArgs != null)
			ret.addAll(patternArgs.gatherBlankNodes());
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.research.rdf.store.sparql11.model.Expression#gatherVariables()
	 */
	@Override
	public Set<Variable> gatherVariables() {
		Set<Variable> ret = HashSetFactory.make(getVariables());
		for (Expression e : arguments)
			ret.addAll(e.gatherVariables());
		return ret;
	}

	@Override
	public Set<Variable> getVariables() {
		if (patternArgs != null) {
			return patternArgs.gatherVariables();
		} else {
			return Collections.emptySet();
		}
	}

	public boolean isExists() {
		return builtinType == EBuiltinType.EXISTS
				|| builtinType == EBuiltinType.NOT_EXISTS;
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
		for (Expression e : arguments)
			e.traverse(l);
		l.endExpression(this);
	}

	public boolean containsEBV() {
		return false;
	}

	public boolean containsBound() {
		if (builtinType == EBuiltinType.BOUND)
			return true;
		else
			return false;
	}

	public boolean containsNotBound() {
		return false;
	}

	public boolean containsCast(Variable v) {
		for (Expression e : arguments) {
			if (e.containsCast(v))
				return true;
		}
		return false;
	}

	public void setPatternArgument(Pattern nc) {
		patternArgs = nc;
	}

	@Override
	public String visit(FilterContext context, Store store) {
		return builtinType.visit(store, context, this);
	}
}
