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


public class QueryTriplePredicate {
	
	private TernaryUnion<IRI, Variable, Path> value;
	
	public QueryTriplePredicate(IRI iri) {
		value = new TernaryUnion<IRI, Variable, Path>();
		value.setFirst(iri);
	}
	public QueryTriplePredicate(Variable var) {
		value = new TernaryUnion<IRI, Variable, Path>();
		value.setSecond(var);
	}
	public QueryTriplePredicate(Path path) {
		value = new TernaryUnion<IRI, Variable, Path>();
		value.setThird(path);
	}
	
	public boolean isIRI() { return value.isFirstType(); }
	public boolean isVariable() { return value.isSecondType(); }
	public boolean isPath() { return value.isThirdType(); }
	
	public IRI getIRI() { return value.getFirst(); }
	public Variable getVariable() { return value.getSecond(); }
	public Path getPath() { return value.getThird(); }
	
	public void renamePrefixes(String base, Map<String, String> declared, Map<String, String> internal) {
		if(isIRI()) {
			getIRI().rename(base, declared, internal);
		} 
	}
	
	public void reverse() {
		if(isIRI()) {
			getIRI().reverse();
		} 
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
		QueryTriplePredicate other = (QueryTriplePredicate) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	

}
