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
import java.util.Collection;

/**
 * implements a property list
 */
public class PropertyList extends ArrayList<PropertyListElement> {

	private static final long serialVersionUID = 1L;

	public PropertyList() {
		super();
	}

	public PropertyList(Collection<? extends PropertyListElement> collection) {
		super(collection);
	}

	public PropertyList(int capacity) {
		super(capacity);
	}

	public TriplesNodeExpansion expand() {
		BlankNode bn = new BlankNode();
		TriplesNodeExpansion exp = new TriplesNodeExpansion();
		for(PropertyListElement ple: this) {
			exp.expandedTriples.addAll(ple.expand(bn));
		}
		exp.node = new QueryTripleTerm(bn);
		return exp;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < size(); i++) {
			if(i > 0) sb.append("; ");
			sb.append(get(i).toString());
		}
		return sb.toString();
	}
}
