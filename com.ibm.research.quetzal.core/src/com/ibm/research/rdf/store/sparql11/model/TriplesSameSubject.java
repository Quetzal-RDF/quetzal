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

public class TriplesSameSubject {
	private QueryTripleTerm subject;
	private PropertyList properties;
	
	public TriplesSameSubject()
	{
		properties = new PropertyList();
	}
	
	public TriplesSameSubject(QueryTripleTerm s)
	{
		properties = new PropertyList();
		this.subject = s;
	}
	
	public void setSubject(QueryTripleTerm s)
	{
		this.subject = s;
	}
	
	public void addProperty(PropertyListElement e) 
	{
		properties.add(e);
	}
	
	public List<QueryTriple> expand()
	{
		List<QueryTriple> r = new ArrayList<QueryTriple>();
		
		return r;
	}
	
	@Override
	public String toString() { 
		if (subject != null) {
			return subject.toString() + " " + properties.toString() + " . ";
		} else {
			return "";
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
		result = prime * result
				+ ((properties == null) ? 0 : properties.hashCode());
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
		TriplesSameSubject other = (TriplesSameSubject) obj;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		if (properties == null) {
			if (other.properties != null)
				return false;
		} else if (!properties.equals(other.properties))
			return false;
		return true;
	}
}
