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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.wala.util.collections.HashSetFactory;

public class ValuesPattern extends Pattern {


	private Values values;

	public ValuesPattern(Values values) {
		super(EPatternSetType.VALUES);
		this.values = values;
	}

	public Values getValues() {
		return values;
	}

	@Override
	public void renamePrefixes(String base, Map<String, String> declared,
			Map<String, String> internal) {
		super.renamePrefixes(base, declared, internal);
		for (List<Expression> l  : values.getExpressions()) {
			for (Expression e : l) {
				e.renamePrefixes(base, declared, internal);
			}
		}
	}

	@Override
	public Collection<? extends Variable> getVariables() {
		
	//	System.err.println("my variables " + values.getVariables());
		
		return values.getVariables();
	}

	@Override
	public void reverse() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<BlankNodeVariable> gatherBlankNodes() {
		return Collections.EMPTY_SET;
	}

	@Override
	public Set<Variable> gatherVariables() {
		return new HashSet(values.getVariables());
	}

	@Override
	public Set<Variable> gatherOptionalVariablesWithMultipleBindings() {
		return Collections.EMPTY_SET;
	}

	@Override
	public Set<Variable> gatherVariablesWithOptional() {
		return gatherVariables();
	}

	@Override
	public Set<Variable> gatherIRIBoundVariables() {
		Set<Variable> ret = HashSetFactory.make();
		assert values.getExpressions() != null;
		assert !values.getExpressions().isEmpty();
		
		List<Expression> l = values.getExpressions().get(0);
		for (int i = 0; i < l.size(); i++) {
			Expression e = l.get(i);
			if (e instanceof ConstantExpression) {
				if ( ((ConstantExpression) e).getConstant().getIRI() != null) {
					ret.add(values.getVariables().get(i));
				}
			}
		}
		return ret;
	}

	@Override
	public void replaceFilterBindings() {
		return;
		
	}

	@Override
	public int getNumberTriples() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<Pattern> gatherSubPatterns(boolean includeOptionals) {
		return Collections.singleton((Pattern)this);
	}

	@Override
	public Set<Pattern> gatherSubPatternsExcluding(Pattern except,
			boolean includeOptionals) {
		return this!=except? Collections.singleton((Pattern)this): Collections.<Pattern>emptySet();
	}

	@Override
	public Set<Pattern> getSubPatterns(boolean includeOptionals) {
		return Collections.EMPTY_SET;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("VALUES ")
		  .append(values);
		return sb.toString();
	}

	@Override
	public Set<Variable> gatherVariablesInTransitiveClosure() {
		return Collections.EMPTY_SET;
	}

}
