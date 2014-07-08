package com.ibm.research.helix.minhash;

import java.util.Collection;

import com.ibm.research.helix.hash.IHashFunction;

public class MinHashFunction<T> implements IHashFunction<Collection<T>> {

	protected IHashFunction<T> underlyingHashFunction;

	public MinHashFunction(IHashFunction<T> underlyingHashFunction) {
		super();
		this.underlyingHashFunction = underlyingHashFunction;
	}

	public long hash(Collection<T> objects) {
		long min = Long.MAX_VALUE;
		for (T o : objects) {
			min = Math.min(min, underlyingHashFunction.hash(o));
		}
		return min;
	}

}
