package com.ibm.rdf.store.hashing;

/**
 * Interface that offers a method to hash strings to integers.
 */
public interface IHashingFunction {
	/** Returns a hash of string s. */
	public int hash(String s);
}
