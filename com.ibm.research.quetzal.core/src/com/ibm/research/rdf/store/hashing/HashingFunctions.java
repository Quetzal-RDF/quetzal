/******************************************************************************
 * Copyright (c) 2015 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *****************************************************************************/
 package com.ibm.research.rdf.store.hashing;

public class HashingFunctions {
	
	public static long hashString_djb2_xor(String s) {
		long hash = 5381;
		int c, len;
		len = s.length();
		for(int i = 0;i < len;i++) {
			c = s.charAt(i);
			hash = ((hash << 5) + hash) ^ c;
		}
		return hash;
	}

	public static long hashString_djb2(String s) {
		long hash = 5381;
		int c, len;
		len = s.length();
		for(int i = 0;i < len;i++) {
			c = s.charAt(i);
			hash = ((hash << 5) + hash) + c;
		}
		return hash;
	}
	
	public static long hashString_sdbm(String s) {
		long hash = 0;
		int c, len;
		len = s.length();
		for(int i = 0;i < len;i++) {
			c = s.charAt(i);
			hash = (hash << 6) + (hash << 16) - hash + c;
		}
		return hash;		
	}

	public static long hashString_naive(String s) {
		long hash = 0;
		int len;
		len = s.length();
		for(int i = 0;i < len;i++)
			hash += s.charAt(i);
		return hash;		
	}

	public static long hashString_folding(String s) {
		long hash = 0, sum = 0;
		int ilen, len, i;
		len = s.length();
		ilen = len / 4;
		
		for(i = 0; i < ilen; i++)
			sum += makeLong(s.charAt(i*4),s.charAt(i*4 + 1),s.charAt(i*4 + 2),s.charAt(i*4 + 3));

		if((len % 4) != 0)
			sum += makeLong(s.substring(ilen));
		
		return hash;		
	}
	
	protected static long makeLong(char c1, char c2, char c3, char c4) {
		long l = c1;
		l = (l << 8) + c2;
		l = (l << 8) + c3;
		l = (l << 8) + c4;
		return l;
	}
	
	protected static long makeLong(String s) {
		if(s.length() == 0) return 0;
		long l = s.charAt(0);
		int len = s.length();
		
		for(int i = 1;i < len;i++) {
			l = (l << 8) + s.charAt(i);
		}
		return l;
	}

	   public static long hashString_RS(String str) {
	      int b     = 378551;
	      int a     = 63689;
	      long hash = 0;

	      for(int i = 0; i < str.length(); i++) {
	         hash = hash * a + str.charAt(i);
	         a    = a * b;
	      }

	      return hash;
	   }

	   public static long hashString_JS(String str) {
	      long hash = 1315423911;

	      for(int i = 0; i < str.length(); i++)
	         hash ^= ((hash << 5) + str.charAt(i) + (hash >> 2));

	      return hash;
	   }

	   public static long hashString_PWJ(String str) {
	      long BitsInUnsignedInt = (long)(4 * 8);
	      long ThreeQuarters     = (long)((BitsInUnsignedInt  * 3) / 4);
	      long OneEighth         = (long)(BitsInUnsignedInt / 8);
	      long HighBits          = (long)(0xFFFFFFFF) << (BitsInUnsignedInt - OneEighth);
	      long hash              = 0;
	      long test              = 0;

	      for(int i = 0; i < str.length(); i++) {
	         hash = (hash << OneEighth) + str.charAt(i);

	         if((test = hash & HighBits)  != 0)
	            hash = (( hash ^ (test >> ThreeQuarters)) & (~HighBits));
	      }

	      return hash;
	   }

	   public static long hashString_ELF(String str) {
	      long hash = 0, x = 0;

	      for(int i = 0; i < str.length(); i++) {
	         hash = (hash << 4) + str.charAt(i);

	         if((x = hash & 0xF0000000L) != 0)
	            hash ^= (x >> 24);
	         hash &= ~x;
	      }

	      return hash;
	   }

	   public static long hashString_SKDR(String str) {
	      long seed = 131; // 31 131 1313 13131 131313 etc..
	      long hash = 0;

	      for(int i = 0; i < str.length(); i++)
	         hash = (hash * seed) + str.charAt(i);

	      return hash;
	   }


	   public static long hashString_DEK(String str) {
	      long hash = str.length();

	      for(int i = 0; i < str.length(); i++)
	         hash = ((hash << 5) ^ (hash >> 27)) ^ str.charAt(i);

	      return hash;
	   }

	   public static long hashString_BP(String str) {
	      long hash = 0;

	      for(int i = 0; i < str.length(); i++)
	         hash = hash << 7 ^ str.charAt(i);

	      return hash;
	   }

	   public static long hashString_FNV(String str) {
	      long fnv_prime = 0x811C9DC5;
	      long hash = 0;

	      for(int i = 0; i < str.length(); i++) {
	    	  hash *= fnv_prime;
	    	  hash ^= str.charAt(i);
	      }

	      return hash;
	   }


	   public static long hashString_AP(String str) {
	      long hash = 0xAAAAAAAA;

	      for(int i = 0; i < str.length(); i++) {
	         if ((i & 1) == 0)
	            hash ^= ((hash << 7) ^ str.charAt(i) * (hash >> 3));
	         else hash ^= (~((hash << 11) + str.charAt(i) ^ (hash >> 5)));
	      }

	      return hash;
	   }
}
