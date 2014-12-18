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
 * Models a literal.
 */
public class StringLiteral {
	private String value;
	boolean tripleQuotes = false;
	private String language = null;
	private IRI type = null;

	public StringLiteral(String v) {
		if (v.startsWith("\"\"\"") && v.endsWith("\"\"\"")) {
			this.value = v.substring(3, v.length() - 3);
			tripleQuotes = true;
		} else if (v.startsWith("\"") && v.endsWith("\"")) {
			this.value = v.substring(1, v.length() - 1);
		} else {
			this.value = v;
		}
		/*
		 * // Remove the quotes single or triple StringBuffer b = new
		 * StringBuffer(v); int n = 0; // num quotes int i; int l = v.length();
		 * // leading quotes for (i=0; (i <
		 * l)&&((b.charAt(i)=='"')||(b.charAt(i)=='\'')); i++) { b.setCharAt(i,
		 * ' '); n++; } if (n > 1) tripleQuotes = true; // trailing quotes n =
		 * 0; i = l - 1; for (; (i >
		 * 0)&&((b.charAt(i)=='"')||(b.charAt(i)=='\'')); i--) { b.setCharAt(i,
		 * ' '); n++; } if (n > 1) tripleQuotes = true; this.value =
		 * b.toString().trim();
		 */
	}

	/**
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * @param language
	 *            the language to set
	 */
	public void setLanguage(String language) {
		this.language = language; // .toLowerCase();
		// For literals with language remove the quotes
		if (value.length() > 0) {
			StringBuffer b = new StringBuffer(value);
			int i;
			// leading quotes
			for (i = 0; (b.charAt(i) == '"') || (b.charAt(i) == '\''); i++) {
				b.setCharAt(i, ' ');
			}
			// trailing quotes
			i = b.length() - 1;
			for (; (b.charAt(i) == '"') || (b.charAt(i) == '\''); i--) {
				b.setCharAt(i, ' ');
			}
			value = b.toString().trim();
		}
	}

	/**
	 * @return the type
	 */
	public IRI getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(IRI type) {
		this.type = type;
		// For typed literals remove the quotes
		StringBuffer b = new StringBuffer(value);
		int i;
		// leading quotes
		for (i = 0; i < b.length() && ((b.charAt(i) == '"') || (b.charAt(i) == '\'')); i++) {
			b.setCharAt(i, ' ');
		}
		// trailing quotes
		i = b.length() - 1;
		for (; i >= 0 && ((b.charAt(i) == '"') || (b.charAt(i) == '\'')); i--) {
			b.setCharAt(i, ' ');
		}
		value = b.toString().trim();
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	public String toString() {

		String v;
		if (tripleQuotes)
			v = "\"\"\"" + value + "\"\"\"";
		else
			v = "\"" + value + "\"";

		if (getType() != null)
			return v + "^^" + "<" + getType() + ">";
		else if (getLanguage() != null)
			return v + "@" + getLanguage();
		else
			return v; // value;
	}

	public String toTypedString() {
		if (getType() != null)
			return "\"" + value + "\"^^" + getType();
		else if (getLanguage() != null)
			return value + "@" + getLanguage();
		else
			return value;
		/*
		 * { if (tripleQuotes) return "\"\"\"" + value + "\"\"\""; else return
		 * "\"" + value + "\""; }
		 */
	}

	public String toDataString() {
	//	if (type != null && TypeMap.isDate(TypeMap.getDatatypeType(type.getValue()))) {
	//		return "'" + value + "'";
	//	}
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((language == null) ? 0 : language.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
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
		StringLiteral other = (StringLiteral) obj;
		if (language == null) {
			if (other.language != null)
				return false;
		} else if (!language.equals(other.language))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

}
