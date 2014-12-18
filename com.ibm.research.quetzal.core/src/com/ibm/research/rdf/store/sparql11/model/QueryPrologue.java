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

import java.util.HashMap;
import java.util.Map;

/**
 * models a query prologue
 */
public class QueryPrologue {
	private IRI base = null;
	private Map<String, IRI> prefixes = new HashMap<String, IRI>();
	
	public QueryPrologue() {}

	public IRI getBase() {
		return base;
	}

	public void setBase(IRI base) {
		this.base = base;
	}

	public Map<String, IRI> getPrefixes() {
		return prefixes;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		if(base != null) sb.append("BASE " + base.toString() + "\n");
		for(Map.Entry<String, IRI> e: prefixes.entrySet()) {
			sb.append("PREFIX " + e.getKey() + ": " + e.getValue().toString() + "\n");
		}
		
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((base == null) ? 0 : base.hashCode());
		result = prime * result
				+ ((prefixes == null) ? 0 : prefixes.hashCode());
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
		QueryPrologue other = (QueryPrologue) obj;
		if (base == null) {
			if (other.base != null)
				return false;
		} else if (!base.equals(other.base))
			return false;
		if (prefixes == null) {
			if (other.prefixes != null)
				return false;
		} else if (!prefixes.equals(other.prefixes))
			return false;
		return true;
	}
	
	
}
