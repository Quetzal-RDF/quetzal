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
 package com.ibm.research.rdf.store.schema;

import java.io.Serializable;

/**
 * Represents a pair of objects of type t (ordered or not).
 */
public class Pair<T>  implements Serializable {
	private boolean ordered = false;
	private T first;
	private T second;
	
	/**
	 * Creates an unordered pair of objects.
	 */
	public Pair(T first, T second) {
		this.first = first;
		this.second = second;
	}
	
	/**
	 * Creates a pair of objects with the specified ordering
	 */
	public Pair(T first, T second, boolean ordered) {
		this(first, second);
		this.ordered = ordered;
	}

	/**
	 * @return the ordered
	 */
	public boolean isOrdered() {
		return ordered;
	}

	/**
	 * @return the first
	 */
	public T getFirst() {
		return first;
	}

	/**
	 * @return the second
	 */
	public T getSecond() {
		return second;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		if(ordered) {
			result = prime * result + ((first == null) ? 0 : first.hashCode());
			result = prime * result + ((second == null) ? 0 : second.hashCode());
		} else 
			result = prime * result + ((first == null) ? 0 : first.hashCode()) +
									  ((second == null) ? 0 : second.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pair<? extends T> other = (Pair<? extends T>) obj;
		if(ordered)
			return equalsWithNulls(first, other.first) && equalsWithNulls(second, other.second);
		else 
			return (equalsWithNulls(first, other.first) && equalsWithNulls(second, other.second)) ||
				   (equalsWithNulls(first, other.second) && equalsWithNulls(second, other.first));
	}
	
	protected boolean equalsWithNulls(T o1, T o2) {
		if(o1 == null) {
			return o2 == null;
		} else {
			if(o2 == null) return false;
			return o1.equals(o2);
		}
	}
	
	public String toString() {
		return "pair first:" + first + " second:" + second;
	}
	
}
