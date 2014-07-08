package com.ibm.research.helix.rabinfingerprints;

import java.util.BitSet;

public class StringToBitConverter implements IToBitConverter<String> {

	public BitSet toBit(String str) {
		assert str!= null;
		BitSet bs = new BitSet();
		int len = 0, k;
		for (int i = 0; i < str.length(); ++i) {
			k = 0;
			int tmp = str.charAt(i);
			while (tmp > 0) {
				if ((tmp & 1) == 1)
					bs.set(len + k);
				tmp >>= 1;
				k++;
			}
			len += 16;			
		}
		return bs;
	}

	/**
	 * returns str[0]+str[1]*61+...+str[n-1]*61^(n-1)
	 * @param str
	 * @return
	 */
	public long toLong(String str) {
		int ret=0;
		int length = str.length();
		for (int i = length -1; i >=0 ; i--) {
	       ret = 61*ret + str.charAt(i);
		}
	    return ret;
	}

}
