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

import java.util.Map;

/**
 * models the dataset clause
 */
public class DatasetClause {
	private IRI iri;
	private boolean named;
	
	public DatasetClause(IRI i, boolean b) {
		iri = i;
		named = b;
	}
	
	public DatasetClause(IRI i) { 
		this(i, false);
	}

	public IRI getIri() {
		return iri;
	}

	public boolean isNamed() {
		return named;
	}
	
	public void renamePrefixes(String base, Map<String, String> declared, Map<String, String> internal) {
		iri.rename(base, declared, internal);
	}
	
	public void reverseIRIs() {
		iri.reverse();
	}
	
	public String toString() {
		return "FROM " + (named ? "NAMED ": "") + iri.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((iri == null) ? 0 : iri.hashCode());
		result = prime * result + (named ? 1231 : 1237);
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
		DatasetClause other = (DatasetClause) obj;
		if (iri == null) {
			if (other.iri != null)
				return false;
		} else if (!iri.equals(other.iri))
			return false;
		if (named != other.named)
			return false;
		return true;
	}
}
