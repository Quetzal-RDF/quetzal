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
 * models the graphNode
 */
public class GraphNode {
	
	private TernaryUnion<Variable, GraphTerm, TriplesNode> value;
	
	public GraphNode(Variable v) {
		value = new TernaryUnion<Variable, GraphTerm, TriplesNode>();
		value.setFirst(v);
	}
	public GraphNode(GraphTerm gt) {
		value = new TernaryUnion<Variable, GraphTerm, TriplesNode>();
		value.setSecond(gt);
	}
	public GraphNode(TriplesNode tn) {
		value = new TernaryUnion<Variable, GraphTerm, TriplesNode>();
		value.setThird(tn);
	}	
	
	public boolean isVariable() { return value.isFirstType(); }
	public boolean isGraphTerm() { return value.isSecondType(); }
	public boolean isTriplesNode() { return value.isThirdType(); }
	
	public Variable getVariable() { return value.getFirst(); }
	public GraphTerm getGraphTerm() { return value.getSecond(); }
	public TriplesNode getTriplesNode() { return value.getThird(); }
	
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
		GraphNode other = (GraphNode) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
}
