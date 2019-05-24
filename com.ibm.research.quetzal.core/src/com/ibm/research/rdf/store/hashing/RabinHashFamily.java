/******************************************************************************
hello  * Copyright (c) 2015 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *****************************************************************************/
 package com.ibm.research.rdf.store.hashing;

import java.util.ArrayList;

import org.rabinfingerprint.polynomial.Polynomial;

public class RabinHashFamily implements IHashingFamily {
	int targetRange;
	int fSize;
	Polynomial[] hashes;
	ArrayList<Integer> hashValues;

	public RabinHashFamily(int domain, int familySize) {
		targetRange = domain;
		fSize = familySize;
		hashes = new Polynomial[ fSize ];
		hashValues = new ArrayList<Integer>();

		for(int i = 0; i < fSize; i++) {
			hashes[i] = Polynomial.createIrreducible(53);
		}
	}

	public int getFamilySize(String predicate) {
		return fSize;
	}

	public void computeHash(String value) {
		hashValues.clear();

		for (int hashID = 0; hashID < fSize; hashID++) {
			Polynomial h = hashes[hashID];

			int hashValue = Math.abs((int)(h.toBigInteger().longValue() % targetRange));
			hashValues.add(hashValue);
		}
	}

	public int hash(String value, int hashID) {
		return hashValues.get(hashID);
	}

	// Required for Single triple operations.
	public int[] getHash(String value) {
		int[] hashVals = new int[getFamilySize(value)];
		for (int i = 0; i < hashVals.length; i++) {
			hashVals[i] = hash(value, i);
		}
		return hashVals;
	}

	public static void main(String args[]) {
		for (int i = 0; i < 4; i++) {
			new RabinHashFamily(4, i);
		}
	}
}
