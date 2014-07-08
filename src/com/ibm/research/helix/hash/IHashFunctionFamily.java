package com.ibm.research.helix.hash;

import java.util.Iterator;
import java.util.Random;

/**
 * A family of hash functions
 * 
 * @author achille
 *
 */
public interface IHashFunctionFamily<T> {

	/**
	 * returns the size of the family (i.e. the number of hash functions 
	 * in the family). It returns -1 if this number is greater than 
	 * {@link Long#MAX_VALUE}, but less than infinity.  It returns -2 if the
	 * family has an infinite number of hash functions.
	 * @return
	 */
	public long getSize();
	
	
	/**
	 * returns k randomly selected members of this {@link IHashFunctionFamily}
	 * @param k
	 * @param rdm
	 * @return
	 */
	public Iterator<IHashFunction<T>> getRandomMembers(int k, Random rdm);
	
}
