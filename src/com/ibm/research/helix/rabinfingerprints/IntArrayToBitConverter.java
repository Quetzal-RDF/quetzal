package com.ibm.research.helix.rabinfingerprints;

import java.util.BitSet;

public class IntArrayToBitConverter implements IToBitConverter<int[]> {

	public BitSet toBit(int[] iArray) {
		BitSet bs = new BitSet();
		int len = 0, k;
		for (int i = 0; i < iArray.length; ++i) {
			k = 0;
			int tmp = iArray[i];
			while (tmp > 0) {
				if ((tmp & 1) == 1)
					bs.set(len + k);
				tmp >>= 1;
				k++;
			}
			len += 32;			
		}
		return bs;
	}

	/**
	 * returns s[0]+s[1]*61+...+s[n-1]*61^(n-1)
	 * @param str
	 * @return
	 */
	public long toLong(int[] iArray) {
		int ret=0;
		int length = iArray.length;
		for (int i = length -1; i >=0 ; i--) {
	       ret = 61*ret + iArray[i];
		}
	    return ret;
	}
}
