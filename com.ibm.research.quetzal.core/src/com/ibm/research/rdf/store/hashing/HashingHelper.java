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

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;

import com.ibm.research.rdf.store.config.Constants;

/**
 * @author oudrea
 * Helper methods for hashing.
 */
public class HashingHelper {
	
	private static class _HashingFunction implements IHashingFunction {

		private int maxSize;
		private Method method = null;
		
		public _HashingFunction(String hashName, int maxSize) throws HashingException {
			this.maxSize = maxSize;
			try {
				method = HashingFunctions.class.getMethod("hashString_" + hashName, String.class);
			} catch (SecurityException e) {
				throw new HashingException(e);
			} catch (NoSuchMethodException e) {
				throw new HashingException(e);
			}
		}
		
		/* (non-Javadoc)
		 * @see com.ibm.research.rdf.store.hashing.IHashingFunction#hash(java.lang.String)
		 */
		public int hash(String s) {
			if(method == null) return -1;
			try {
				long ret = (Long)method.invoke(null, s);
				return Math.abs((int)(ret % (long)maxSize));
			} catch (IllegalArgumentException e) {
				System.err.println("Method call to " + method.getName() + " failed.");
				e.printStackTrace(System.err);
				return -1;
			} catch (IllegalAccessException e) {
				System.err.println("Method call to " + method.getName() + " failed.");
				e.printStackTrace(System.err);
				return -1;
			} catch (InvocationTargetException e) {
				System.err.println("Method call to " + method.getName() + " failed.");
				e.printStackTrace(System.err);
				return -1;
			}
		}
	}
	
	/**
	 * Returns an object that can computes hashes using the given method name and the given maximum hash value (exclusive).
	 * @param hashName The hash method name. One of: djb2_xor, djb2, sdbm, naive, folding, RS, JS, PWJ, ELF, SKDR, DEK, BP, FNV, AP
	 * @param maxSize The upper bound (exclusive) of the returned hash value. Usually, the hash table size.
	 * @return An object with a hash method that returns a number between 0 (inclusive) and maxSize (exclusive) for any string using the specified hashing method.
	 * @throws HashingException 
	 */
	public static IHashingFunction getHashingFunction(String hashName, int maxSize) throws HashingException {
		return new _HashingFunction(hashName, maxSize);
	}
	
	/**
	 * Hashes a long string into a shorter string using the default long string hashing method specified in Constants.LONG_STRINGS_HASHING_METHOD.
	 * @param longString The long string to hash to a shorter string.
	 * @return The resulting shorter string.
	 * @throws HashingException If critical failure occurs.
	 * @throws UnsupportedEncodingException 
	 */
	public static String hashLongString(String longString) throws HashingException, UnsupportedEncodingException {
		try {
			return hash(longString, Constants.LONG_STRINGS_HASHING_METHOD);
		} catch (NoSuchAlgorithmException e) {
			throw new HashingException(e);
		}
	}
	
	private static byte[] checksum(String msg, String method) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		java.security.MessageDigest complete = java.security.MessageDigest.getInstance(method);
		complete.update(msg.getBytes("UTF-8"));
		return complete.digest();
	}
	
	private static String hash(String msg, String method) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		byte[] b = checksum(msg, method);
		String result = "";
		for (int i = 0; i < b.length; i++) {
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}
}
