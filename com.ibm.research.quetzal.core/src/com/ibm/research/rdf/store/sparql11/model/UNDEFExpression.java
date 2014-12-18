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
import java.util.Map;
import java.util.Set;

import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.runtime.service.types.TypeMap;
import com.ibm.research.rdf.store.runtime.service.types.TypeMap.TypeCategory;
import com.ibm.research.rdf.store.sparql11.sqlwriter.FilterContext;

public class UNDEFExpression extends Expression {
	
	public UNDEFExpression(EExpressionType type) {
		super(type);
	}

	public UNDEFExpression() {
		super(EExpressionType.UNDEF);
	}
	public Short getReturnType() {
		return TypeMap.NONE_ID;
	}
	
	@Override
	public short getTypeEquality(Variable v) {
		return TypeMap.NONE_ID;
	}

	@Override
	public TypeCategory getTypeRestriction(Variable v) {
		return TypeMap.TypeCategory.NONE;
	}

	@Override
	public boolean containsCast(Variable v) {
		return false;
	}

	@Override
	public boolean containsEBV() {
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
	public void renamePrefixes(String base, Map<String, String> declared,
			Map<String, String> internal) {
		
	}

	@Override
	public void reverseIRIs() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<BlankNodeVariable> gatherBlankNodes() {
		return null;
	}

	@Override
	public Set<Variable> gatherVariables() {
		return getVariables();
	}

	@Override
	public Set<Variable> getVariables() {
		return Collections.emptySet();
	}

	@Override
	public void traverse(IExpressionTraversalListener l) {
		
	}

	@Override
	public String getStringWithVarName() {
		return null;
	}

	public String toDataString() {
		return "NULL";
	}

	@Override
	public String visit(FilterContext context, Store store) {
	     return "'" + toDataString() + "'";
	}

}
