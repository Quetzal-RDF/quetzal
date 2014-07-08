package com.ibm.research.helix.hash;


/**
 * A hash function
 * @author achille
 */
public interface IHashFunction<T> {
	public long hash(T object);
}
