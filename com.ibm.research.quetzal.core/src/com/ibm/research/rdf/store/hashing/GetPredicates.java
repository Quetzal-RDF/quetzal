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

import java.io.File;
import java.util.Set;

public class GetPredicates {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String queryDir = args[0];
		
		Set<String> predicates = FindWorkloadProxies.getPredicates(new File(queryDir));
		for(String s : predicates) {
			System.out.println(s);
		}
	}

}
