package com.ibm.research.helix.minhash;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import com.ibm.research.helix.hash.IHashFunction;
import com.ibm.research.helix.hash.IHashFunctionFamily;

public class MinHashFamily<T> implements IHashFunctionFamily<Collection<T>> {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	private IHashFunctionFamily<T> underlyingFamily;
	public MinHashFamily(IHashFunctionFamily<T> underlyingFamily) {
		super();
		this.underlyingFamily = underlyingFamily;
	}

	public Iterator<IHashFunction<Collection<T>>> getRandomMembers(int k, Random rdm) {
		final Iterator<IHashFunction<T>> uhfs = underlyingFamily.getRandomMembers(k, rdm);
		return new Iterator<IHashFunction<Collection<T>>>() {
			public boolean hasNext() {
				return uhfs.hasNext();
			}
			public IHashFunction<Collection<T>> next() {
				IHashFunction<Collection<T>> ret = new MinHashFunction<T>(uhfs.next());
				return ret;
			}
			public void remove() {
				throw new UnsupportedOperationException();
				
			}
		};
	}

	public long getSize() {
		return underlyingFamily.getSize();
	}
}