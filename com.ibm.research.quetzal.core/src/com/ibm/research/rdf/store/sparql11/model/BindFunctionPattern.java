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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.wala.util.collections.HashSetFactory;

public class BindFunctionPattern extends Pattern implements Service {

	protected List<Variable> vars;
	protected BindFunctionCall funcCall;

	public BindFunctionPattern() {
		super(EPatternSetType.BINDFUNC);
		this.vars = new ArrayList<Variable>();
		this.funcCall = null;
	}
	

	public List<Variable> getVars() {
		return vars;
	}
	
	public void addVar(Variable v) {
		vars.add(v);
	}
	
	public void addVar(String v) {
		vars.add(new Variable(v));
	}
	
	public  BindFunctionCall getFuncCall() {
		return funcCall;
	}
	
	public  void setFuncCall(BindFunctionCall f) {
		funcCall = f;
	}

	@Override
	public Set<BlankNodeVariable> gatherBlankNodes() {
		return Collections.emptySet();
	}

	@Override
	public Set<Variable> gatherVariables() {
		Set<Variable> ret = new HashSet<Variable>();
		ret.addAll(vars);
		if (funcCall != null) {
			ret.addAll(funcCall.vars);
		}
		return ret;
	}
	
	/**
	 * Return true if the bind expression is a bind with no dependencies
	 * on prior vaiable bindings
	 * @return
	 */
	public boolean isIndependentBind() {
		return false;
	}

	public Set<Variable> getVariables() {
		Set<Variable> ret = HashSetFactory.make();
		ret.addAll(vars);
		return ret;
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
		//if (expr.getReturnType() == TypeMap.IRI_ID || expr.getReturnType() == TypeMap.BLANK_NODE_ID)
		//	return Collections.singleton(var);			// ask Mihaela if this is ever used
		//else 
			return new HashSet<Variable>();
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
		funcCall.getIri().rename(base, declared, internal);
	}
	
	@Override
	public boolean isEmpty() {
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("BIND ("+funcCall.toString())
		  	.append(" AS (");
		for(Variable v: vars)
		{
			sb.append(v+" ");
		}
		sb.append(")");
		return sb.toString();
	}
	@Override
	public void reverse() {
	//	this.expr.reverseIRIs();
		
	}

	public boolean isUnscopedBind() {
		Set<Variable> scope = getGroup() != null ? getGroup().inScopeVars() : Collections.<Variable>emptySet();
		return !scope.containsAll(funcCall.gatherVariables());
	}


	@Override
	public Set<Variable> gatherVariablesInTransitiveClosure() {
		return Collections.EMPTY_SET;
	}


	@Override
	public QueryTripleTerm getService() {
		// TODO Auto-generated method stub
		return new QueryTripleTerm(getFuncCall().getIri());
	}


	@Override
	public EServiceType getServiceType() {
		// TODO Auto-generated method stub
		return EServiceType.POST;
	}
	
}
