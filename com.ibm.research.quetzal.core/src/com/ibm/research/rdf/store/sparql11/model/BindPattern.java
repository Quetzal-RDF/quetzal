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

import com.ibm.research.rdf.store.runtime.service.types.TypeMap;

public class BindPattern extends Pattern {

	protected Variable var;
	protected Expression expr;
	
	
	public BindPattern(String var, Expression expr) {
		this(new Variable(var), expr);
	}
	public BindPattern(Variable var, Expression expr) {
		super(EPatternSetType.BIND);
		this.var = var;
		this.expr = expr;
	}
	

	public Variable getVar() {
		return var;
	}
	
	public Expression getExpression() {
		return expr;
	}

	@Override
	public Set<BlankNodeVariable> gatherBlankNodes() {
		return Collections.emptySet();
	}

	@Override
	public Set<Variable> gatherVariables() {
		Set<Variable> ret = new HashSet<Variable>();
		ret.add(var);
		if (expr != null) {
			ret.addAll(expr.gatherVariables());
		}
		return ret;
	}
	
	/**
	 * Return true if the bind expression is a bind with no dependencies
	 * on prior vaiable bindings
	 * @return
	 */
	public boolean isIndependentBind() {
		return expr.gatherVariables() != null ? expr.gatherVariables().isEmpty() : true;
	}

	public Set<Variable> getVariables() {
		return Collections.singleton(var);
	}
	
	@Override
	public Set<Variable> gatherOptionalVariablesWithMultipleBindings() {
		return Collections.emptySet();
	}

	@Override
	public Set<Variable> gatherVariablesWithOptional() {
		return gatherVariables();
	}

	@Override
	public Set<Variable> gatherIRIBoundVariables() {
		if (expr.getReturnType() == TypeMap.IRI_ID || expr.getReturnType() == TypeMap.BLANK_NODE_ID)
			return Collections.singleton(var);			// ask Mihaela if this is ever used
		else return new HashSet<Variable>();
	}

	@Override
	public void replaceFilterBindings() {
		// Do nothing no variable predicate and no filter

	}


	@Override
	public int getNumberTriples() {
		return 0;
	}

	@Override
	public Set<Pattern> gatherSubPatterns(boolean includeOptionals) {
		Set<Pattern> ret = new HashSet<Pattern>();
		ret.add(this);
		return ret;
	}

	@Override
	public Set<Pattern> gatherSubPatternsExcluding(Pattern except, boolean includeOptionals) {
		if (except.equals(this)) {
			return Collections.emptySet();
		} else {
			return gatherSubPatterns(includeOptionals);
		}
	}
	
	@Override
	public Set<Pattern> getSubPatterns(boolean includeOptionals) {
		Set<Pattern> ret = new HashSet<Pattern>();
		ret.add(this);
		return ret;
	}

	@Override
	public void renamePrefixes(String base, Map<String, String> declared,
			Map<String, String> internal) {
		super.renamePrefixes(base, declared, internal);
		expr.renamePrefixes(base, declared, internal);
	}
	
	@Override
	public boolean isEmpty() {
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("BIND")
		  .append("(")
		  	.append(expr)
		  	.append(" AS ")
		  	.append(var)
		  .append(")");
		if (isUnscopedBind()) {
			sb.append(" (expr vars out of scope)");
		}
		return sb.toString();
	}
	@Override
	public void reverse() {
		this.expr.reverseIRIs();
		
	}

	public boolean isUnscopedBind() {
		Set<Variable> scope = getGroup() != null ? getGroup().inScopeVars() : Collections.<Variable>emptySet();
		return !scope.containsAll(expr.gatherVariables());
	}
	@Override
	public Set<Variable> gatherVariablesInTransitiveClosure() {
		return Collections.EMPTY_SET;
	}
	
	
}
