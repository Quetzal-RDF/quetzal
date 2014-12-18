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
 package com.ibm.research.rdf.store.jena;

import java.io.File;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.Syntax;

public class RdfStoreQueryFactory {

	// should we support overloaded create methods with following params
	// baseURI
	// Syntax
	

	public static Query create(String queryString) {
		checkArg(queryString);
		Query db2query = new com.ibm.research.rdf.store.jena.Query(queryString);
		return db2query;
	}
	
	private static void checkArg(String queryStr) {
		checkNotNull(queryStr, "Query string is null");
	}

	private static void checkNotNull(Object obj, String msg) {
		if (obj == null)
			throw new IllegalArgumentException(msg);
	}

	public static Query read(String queryFile,
			Object object, Syntax fileSyntax) {
		File f = null;
		if (queryFile.startsWith("file:///")) {
			f = new File(queryFile.substring(8));
		} else {
			f = new File(queryFile);
		}
		
		Query db2query = new com.ibm.research.rdf.store.jena.Query(f);

		return db2query;
	}
}