package com.ibm.rdf.store.hashing;

/**
 * Interface that offers a method to hash strings to integers.
 */
public interface IHashingFamily {
	/** Returns a hash of string s. */
	public int hash(String s, int HashId);
	// some hashing family sizes might be dependent on a specific predicate
	public int getFamilySize(String predicate);
	public void computeHash(String s);
	
	// Added for Single triple operations.
	public int[] getHash(String s);
}
