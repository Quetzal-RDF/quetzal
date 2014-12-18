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
 * A union between four types
 */
public class FourUnion<T, U, V, W> {
	
	private BinaryUnion<BinaryUnion<T, U>, BinaryUnion<V, W>> data = new BinaryUnion<BinaryUnion<T,U>, BinaryUnion<V,W>>();
	
	public boolean isFirstType() { return data.isFirstType() && data.getFirst().isFirstType(); }
	public boolean isSecondType() { return data.isFirstType() && data.getFirst().isSecondType(); }
	public boolean isThirdType() { return data.isSecondType() && data.getSecond().isFirstType(); }
	public boolean isFourthType() { return data.isSecondType() && data.getSecond().isSecondType(); }
	
	public T getFirst() { return data.isFirstType() ? data.getFirst().getFirst() : null; }
	public U getSecond() { return data.isFirstType() ? data.getFirst().getSecond() : null; }
	public V getThird() { return data.isSecondType() ? data.getSecond().getFirst() : null; }
	public W getFourth() { return data.isSecondType() ? data.getSecond().getSecond() : null; }
	
	public void setFirst(T x) {
		BinaryUnion<T, U> b = new BinaryUnion<T, U>();
		b.setFirst(x);
		data.setFirst(b);
	}
	public void setSecond(U x) {
		BinaryUnion<T, U> b = new BinaryUnion<T, U>();
		b.setSecond(x);
		data.setFirst(b);
	}
	public void setThird(V x) {
		BinaryUnion<V, W> b = new BinaryUnion<V, W>();
		b.setFirst(x);
		data.setSecond(b);
	}
	public void setFourth(W x) {
		BinaryUnion<V, W> b = new BinaryUnion<V, W>();
		b.setSecond(x);
		data.setSecond(b);
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
		FourUnion other = (FourUnion) obj;
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
		else if(isFourthType()) return getFourth().toString();
		else return "";
	}
	
}
