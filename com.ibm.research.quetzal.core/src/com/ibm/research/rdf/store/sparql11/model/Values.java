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

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.research.rdf.store.sparql11.model.Expression.EExpressionType;
import com.ibm.wala.util.collections.HashMapFactory;
import com.ibm.wala.util.collections.HashSetFactory;

public class Values {
	private List<Variable> variables;
	
	public List<Variable> getVariables() {
		return variables;
	}

	public List<List<Expression>> getExpressions() {
		return expressions;
	}

	private List<List<Expression>> expressions;
	
	public Values(List<Variable> variables, List<List<Expression>> expressions) {
		this.variables = variables;
		this.expressions = expressions;
	}

	public Set<Variable> determineUNDEFVariables() {
		Set<Variable> undefVariables = HashSetFactory.make();

		Map<Integer, Variable> indexToVar = HashMapFactory.make();
		int i = 0;
		for (Variable v : variables) {
			indexToVar.put(i, v);
			i++;
		}
		for (List<Expression> l : expressions) {
			int k = 0;
			for (Expression e : l) {
				if (e.getType() == EExpressionType.UNDEF) {
					undefVariables.add(indexToVar.get(k));
				}
				k++;
			}
		}
		return undefVariables;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("VALUES (");
		for (Variable v : variables) {
			sb.append(v).append(" ");
		}
		sb.append(") {");
		for (List<Expression> l : expressions) {
			sb.append("(");
			for (Expression e : l) {
				sb.append(e).append (" ");
			}
			sb.append(")");
		}
		sb.append("}");
		return sb.toString();
	}
}
