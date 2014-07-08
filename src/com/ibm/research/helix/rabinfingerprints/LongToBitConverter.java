package com.ibm.research.helix.rabinfingerprints;

import java.util.BitSet;

public class LongToBitConverter implements IToBitConverter<Long> {

	public BitSet toBit(Long value) {
		BitSet bs = new BitSet();
		int pos = 0;
		while (value > 0) {
			if ((value & 1) == 1) {
				bs.set(pos);
			}
			value >>= 1;
			++pos;
		}
		return bs;
	}

	public long toLong(Long value) {
		return value;
	}
}
