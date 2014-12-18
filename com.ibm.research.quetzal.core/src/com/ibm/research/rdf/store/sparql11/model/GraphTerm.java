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

/**
 * models a graph term.
 */
public class GraphTerm {
	private TernaryUnion<IRI, Constant, BlankNode> value;
	
	public GraphTerm(IRI i) {
		value = new TernaryUnion<IRI, Constant, BlankNode>();
		value.setFirst(i);
	}

	public GraphTerm(Constant c) {
		value = new TernaryUnion<IRI, Constant, BlankNode>();
		value.setSecond(c);
	}

	public GraphTerm(BlankNode bn) {
		value = new TernaryUnion<IRI, Constant, BlankNode>();
		value.setThird(bn);
	}
	
	public boolean isIRI() { return value.isFirstType(); }
	public boolean isConstant() { return value.isSecondType(); }
	public boolean isBlankNode() { return value.isThirdType(); }
	
	public IRI getIRI() { return value.getFirst(); }
	public Constant getConstant() { return value.getSecond(); }
	public BlankNode getBlankNode() { return value.getThird(); }
	
	public String toString() { return value.toString(); }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		GraphTerm other = (GraphTerm) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
}
