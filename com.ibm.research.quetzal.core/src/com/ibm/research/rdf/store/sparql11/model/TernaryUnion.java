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
 * A ternary union of objects
 */
public class TernaryUnion<T, U, V> {
	private BinaryUnion<T, BinaryUnion<U,V>> data;
	
	public TernaryUnion() {
		data = new BinaryUnion<T, BinaryUnion<U,V>>();
	}
	
	public boolean isFirstType() { return data.isFirstType(); }
	public boolean isSecondType() { return data.isSecondType() && data.getSecond().isFirstType(); }
	public boolean isThirdType() { return data.isSecondType() && data.getSecond().isSecondType(); }
	public T getFirst() { return data.getFirst(); }
	public U getSecond() { return (data.isSecondType() ? data.getSecond().getFirst() : null); }
	public V getThird() { return (data.isSecondType() ? data.getSecond().getSecond() : null); }	
	public void setFirst(T x) { data.setFirst(x); }
	public void setSecond(U x) { 
		BinaryUnion<U, V> bu = new BinaryUnion<U,V>();
		bu.setFirst(x);
		data.setSecond(bu);
	}
	public void setThird(V x) {
		BinaryUnion<U, V> bu = new BinaryUnion<U,V>();
		bu.setSecond(x);
		data.setSecond(bu);		
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TernaryUnion other = (TernaryUnion) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		return true;
	}
	
	public String toString() { 
		if(isFirstType()) return getFirst().toString();
		else if(isSecondType()) return getSecond().toString();
		else if(isThirdType()) return getThird().toString();
		else return "";
	}
	
}
