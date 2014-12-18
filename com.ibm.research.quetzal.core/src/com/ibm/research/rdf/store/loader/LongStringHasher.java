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
 package com.ibm.research.rdf.store.loader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.ibm.research.rdf.store.config.Constants;
import com.ibm.research.rdf.store.hashing.HashingException;
import com.ibm.research.rdf.store.hashing.HashingHelper;

public class LongStringHasher {

	/**
	 * @param args
	 * @throws HashingException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws HashingException, IOException {
		int cutoff = Integer.parseInt(args[0]);
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		String line;
		while ((line = input.readLine()) != null) {
			if (line.getBytes().length > cutoff) {
				System.out.println(Constants.PREFIX_SHORT_STRING + HashingHelper.hashLongString(line));
			} else {
				System.out.println(line);
			}
		}
	}

}
