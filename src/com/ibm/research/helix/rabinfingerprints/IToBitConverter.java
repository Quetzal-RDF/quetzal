package com.ibm.research.helix.rabinfingerprints;

import java.util.BitSet;

/**
 * convert an object into an array of bit representation so that
 * its Rabin Fingerprint can be computed
 * @author achille
 *
 */
public interface IToBitConverter<T> {
	
	public BitSet toBit(T object);
	public long toLong(T object);
}
