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
 package com.ibm.research.rdf.store.sparql11.planner;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.ibm.research.rdf.store.sparql11.model.BinaryUnion;
import com.ibm.research.rdf.store.sparql11.model.BlankNodeVariable;
import com.ibm.research.rdf.store.sparql11.model.Expression;
import com.ibm.research.rdf.store.sparql11.model.IRI;
import com.ibm.research.rdf.store.sparql11.model.Pattern;
import com.ibm.research.rdf.store.sparql11.model.Variable;
import com.ibm.research.rdf.store.sparql11.planner.Planner.Key;
import com.ibm.wala.util.collections.HashSetFactory;

public class GraphRestrictionPattern extends Pattern implements Key {

	//private  BinaryUnion<Variable, IRI> graphRestriction;
	
	public GraphRestrictionPattern(BinaryUnion<Variable, IRI> graphRestriction) {
		super( EPatternSetType.GRAPH);
		this.graphRestriction = graphRestriction;
		if (graphRestriction == null) {
			throw new NullPointerException();
		}
	}

	
	
	@Override
	public Set<Variable> gatherVariables() {
		if (graphRestriction.isFirstType()) {
			return Collections.singleton(graphRestriction.getFirst());
		} else {
			return Collections.emptySet();
		}
	}

	@Override
	public boolean isMandatory() {
		return true;
	}

	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GraphRestrictionPatterny [graphRestriction=");
		builder.append(graphRestriction);
		builder.append("]");
		return builder.toString();
	}
		

	
	
	@Override
	public void setGraphRestriction(BinaryUnion<Variable, IRI> graphRestriction) {
		if (!this.graphRestriction.equals(graphRestriction)) {
			throw new RuntimeException("Graph restriction cannot be modified!");
		}
	}



	@Override
	public void addFilter(Expression e) {
		throw new UnsupportedOperationException();
	}



	@Override
	public void addOptional(Pattern optional) {
		throw new UnsupportedOperationException();
	}





	@Override
	public Collection<? extends Variable> getVariables() {
		return gatherVariables();
	}

	@Override
	public void reverse() {
		if (graphRestriction.isSecondType()) {
			graphRestriction.getSecond().reverse();
		}
		
	}

	@Override
	public Set<BlankNodeVariable> gatherBlankNodes() {
		return Collections.emptySet();
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
		// no filter allowed
		return Collections.emptySet();
	}
	
	@Override
	public Set<Variable> gatherVariablesInTransitiveClosure() {
		// no filter allowed
		return Collections.emptySet();
	}

	@Override
	public void replaceFilterBindings() {
		// no filter allowed
		
	}

	@Override
	public int getNumberTriples() {
		int nrTriples=0;
		return nrTriples;
	}

	@Override
	public Set<Pattern> gatherSubPatterns(boolean includeOptionals) {
		Set<Pattern> ret = HashSetFactory.make();
		ret.add(this);
		return ret;
	}

	@Override
	public Set<Pattern> gatherSubPatternsExcluding(Pattern except,
			boolean includeOptionals) {
		if (! except.equals(this)) {
			Set<Pattern> ret = HashSetFactory.make();
			ret.add(this);
			return ret;
		} else {
			return Collections.emptySet();
		}
	}

	@Override
	public Set<Pattern> getSubPatterns(boolean includeOptionals) {
		return HashSetFactory.make();
	}

	@Override
	public boolean isEmpty() {
		return true;
	}
	
	//Pattern methods
	

}
