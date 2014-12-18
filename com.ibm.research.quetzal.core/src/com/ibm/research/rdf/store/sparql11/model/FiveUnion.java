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

public class FiveUnion<S, T, U, V, W> 
{
		private BinaryUnion<FourUnion<S, T, U, V>, W> data = new BinaryUnion<FourUnion<S, T, U, V>, W>();
		
		public FiveUnion() 
		{
			
		}
		
		public boolean isFirstType() 
		{ 
			return (data.isFirstType() && data.getFirst().isFirstType()); 
		}
		
		public boolean isSecondType() 
		{ 
			return (data.isFirstType() && data.getFirst().isSecondType()); 
		}
		
		public boolean isThirdType() 
		{ 
			return (data.isFirstType() && data.getFirst().isThirdType()); 
		}
		
		public boolean isFourthType() 
		{ 
			return (data.isFirstType() && data.getFirst().isFourthType()); 
		}
		
		public boolean isFifthType() 
		{ 
			return (data.isSecondType()); 
		}
		
		public S getFirst() 
		{ 
			return (data.isFirstType()? data.getFirst().getFirst() : null); 
		}
		
		public T getSecond() 
		{ 
			return (data.isFirstType()? data.getFirst().getSecond() : null); 
		}
		
		public U getThird() 
		{ 
			return (data.isFirstType()? data.getFirst().getThird() : null); 
		}
		
		public V getFourth() 
		{ 
			return (data.isFirstType()? data.getFirst().getFourth() : null); 
		}
		
		public W getFifth() 
		{ 
			return (data.isSecondType()? data.getSecond() : null); 
		}
		
		public void setFirst(S x) {
			FourUnion<S, T, U, V> f = new FourUnion<S, T, U, V>();
			f.setFirst(x);
			data.setFirst(f);
		}
		
		public void setSecond(T x) {
			FourUnion<S, T, U, V> f = new FourUnion<S, T, U, V>();
			f.setSecond(x);
			data.setFirst(f);
		}
		
		public void setThird(U x) {
			FourUnion<S, T, U, V> f = new FourUnion<S, T, U, V>();
			f.setThird(x);
			data.setFirst(f);
		}
		
		public void setFourth(V x) {
			FourUnion<S, T, U, V> f = new FourUnion<S, T, U, V>();
			f.setFourth(x);
			data.setFirst(f);
		}
		
		public void setFifth(W x) {
			data.setSecond(x);
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
			FiveUnion other = (FiveUnion)obj;
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
			else if(isFifthType()) return getFifth().toString();
			else return "";
		}
}
