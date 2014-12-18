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
import java.util.List;

/**
 * models a triplesNode
 */
public class TriplesNode {
	
	private BinaryUnion<RDFCollection, PropertyList> value;
	
	public TriplesNode(RDFCollection c) {
		value = new BinaryUnion<RDFCollection, PropertyList>();
		value.setFirst(c);
	}
	public TriplesNode(PropertyList p) {
		value = new BinaryUnion<RDFCollection, PropertyList>();
		value.setSecond(p);
	}
	
	public boolean isCollection() { return value.isFirstType(); }
	public boolean isPropertyList() { return value.isSecondType(); }
	public RDFCollection getCollection() { return value.getFirst(); }
	public PropertyList getPropertyList() { return value.getSecond(); }
	
	public String toString() { 
		if (this.isCollection()) {
			return "("+value.toString()+")";
		} else {
			return "["+value.toString()+"]";
		}
	}
	
	public TriplesNodeExpansion expand() {
		if(isCollection()) {
			// expand RDFCollection here
			return getCollection().expand();
		} else if(isPropertyList()) {
			// expand property list here
			return getPropertyList().expand();
		} else return null;
	}
	
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
		TriplesNode other = (TriplesNode) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

}

class TriplesNodeExpansion {
	public QueryTripleTerm node;
	public List<QueryTriple> expandedTriples = new ArrayList<QueryTriple>();
}

