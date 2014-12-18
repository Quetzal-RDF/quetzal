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
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.runtime.service.types.TypeMap.TypeCategory;
import com.ibm.research.rdf.store.sparql11.sqlwriter.FilterContext;
import com.ibm.wala.util.collections.HashSetFactory;

public class OneOfExpression extends Expression {

	private final EOneOfOp op;
	private final Expression left;
	private final List<Expression> right;
	
	public OneOfExpression(EOneOfOp type, Expression left, List<Expression> right) {
		super(EExpressionType.ONE_OF);
		this.op = type;
		this.left = left;
		this.right = right;
	}

	@Override
	public Short getReturnType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public short getTypeEquality(Variable v) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public TypeCategory getTypeRestriction(Variable v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean containsCast(Variable v) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsEBV() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsBound() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsNotBound() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void renamePrefixes(String base, Map<String, String> declared,
			Map<String, String> internal) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reverseIRIs() {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<BlankNodeVariable> gatherBlankNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Variable> gatherVariables() {
		Set<Variable> vars = HashSetFactory.make(left.gatherVariables());
		for(Expression e : right) {
			vars.addAll(e.gatherVariables());
		}
		return vars;
	}

	@Override
	public Set<Variable> getVariables() {
		return Collections.emptySet();
	}

	@Override
	public void traverse(IExpressionTraversalListener l) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getStringWithVarName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visit(FilterContext context, Store store) {
		throw new UnsupportedOperationException("OneOf expressins are not supported");
	}

}
