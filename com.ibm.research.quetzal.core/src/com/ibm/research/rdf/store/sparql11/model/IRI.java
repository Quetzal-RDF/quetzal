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
 * an IRI
 */
public class IRI {
	private String value;
	
	public IRI(String value) { this.value = value; }
	
	public String getValue() { return value; }
	
	public String getSqlDataString() {return value.replace("'", "''"); }
	
	public String toString() { return value; }
	
	public boolean isAbsolute() { 
		return isAbsolute(value);
	}

	private static final java.util.regex.Pattern scheme = java.util.regex.Pattern.compile("\\A[a-z]+:/");
	
	private static boolean isAbsolute(String v) { 
		return scheme.matcher(v).find();
	}

	public void rename(String base, Map<String, String> declaredPrefixes, Map<String, String> internalPrefixes) {
		String v = value;
		
		// go through list of declared prefixes, replace if any prefix is found to the full path
		for(String pref: declaredPrefixes.keySet()) {
			if(v.startsWith(pref + ":")) {
				v = v.replaceFirst(pref + ":", declaredPrefixes.get(pref));
			}
		}
		
		// now go through the internal prefixes, replace any long prefixes with the short form
		for(String ns: internalPrefixes.keySet()) {
			String pref = internalPrefixes.get(ns);
			if(v.startsWith(pref)) {
				v = v.replaceFirst(pref, ns + ":");
			}
		}
		
		if (!isAbsolute(v) && (base != null)) {
			v = base + v;
		}
		
		value = v;
	}

	public void reverse(){
		value=new StringBuffer(value).reverse().toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IRI other = (IRI) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
	
}
