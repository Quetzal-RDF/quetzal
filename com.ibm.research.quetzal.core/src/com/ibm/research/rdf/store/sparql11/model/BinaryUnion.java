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
 * A template class containing either an object of type T or an object of type U (but not
 * both).
 */
public class BinaryUnion<T, U> {
	private T obj1 = null;
	private U obj2 = null;
	
	public boolean isFirstType() { return obj1 != null; }
	public boolean isSecondType() { return obj2 != null; }
	public T getFirst() { return obj1; }
	public U getSecond() { return obj2; }
	public void setFirst(T o) { obj1 = o; }
	public void setSecond(U o) { obj2 = o; }
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((obj1 == null) ? 0 : obj1.hashCode());
		result = prime * result + ((obj2 == null) ? 0 : obj2.hashCode());
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
		BinaryUnion<?,?> other = (BinaryUnion<?,?>) obj;
		if (obj1 == null) {
			if (other.obj1 != null)
				return false;
		} else if (!obj1.equals(other.obj1))
			return false;
		if (obj2 == null) {
			if (other.obj2 != null)
				return false;
		} else if (!obj2.equals(other.obj2))
			return false;
		return true;
	}

	public String toString() {
		if(isFirstType()) return getFirst().toString();
		else return getSecond().toString();
	}
	
}
