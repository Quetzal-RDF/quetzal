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

import java.util.Vector;

import com.ibm.research.helix.hash.IHashFunction;
import com.ibm.research.helix.rabinfingerprints.RabinFingerprintsFamily;

public class StringHashFamily implements IHashingFamily {
	int targetRange;

	int fSize;

	RabinFingerprintsFamily<String> setOfHash;

	Vector<IHashFunction<String>> hashes;

	public StringHashFamily(int domain, int familySize) {
		targetRange = domain;
		fSize = familySize;
	}

	public int getFamilySize(String predicate) {
		return fSize;
	}

	public int hash(String value, int hashID) {
		if (hashID == 0)
			return Math
					.abs((int) (HashingFunctions.hashString_ELF(value) % targetRange));
		if (hashID == 1)
			return Math
					.abs((int) (HashingFunctions.hashString_DEK(value) % targetRange));
		if (hashID == 2)
			return Math
					.abs((int) (HashingFunctions.hashString_PWJ(value) % targetRange));
		return 0;
	}

	public void computeHash(String s) {

	}

	public int[] getHash(String s) {
		int[] hashVals = new int[getFamilySize(s)];
		for (int i = 0; i < hashVals.length; i++) {
			hashVals[i] = hash(s, i);
		}
		return hashVals;
	}
}
