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
 package com.ibm.research.rdf.store.sparql11.model;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.hp.hpl.jena.query.QueryFactory;
import com.ibm.research.rdf.store.sparql11.SparqlParserUtilities;

public class TestQueryToStrring {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		List<File> files = new LinkedList<File>();
		for (String arg : args) {
			File f = new File(arg);
			if (f.isDirectory()) {
				for (File sf: f.listFiles()) {
					if (sf.getName().endsWith(".sparql")) {
						files.add(sf);
					}
				}
			} else {
				files.add(f);
			}
		}
		int successes = 0;
		for (File file: files) {
			System.out.println("Processing file: "+file.getAbsolutePath());
			try {
				boolean success = test(file.getAbsolutePath());
				if (success) {
					successes++;
				}
			} catch (Exception e) {
				e.printStackTrace(System.out);
			}
		}
		System.out.println("total: "+files.size()+"\nsuccesses: "+successes+"\nsuccess rate: "+((float) successes)/files.size());
			
	}

	protected static boolean test(String fileName) {
		 Query q  = SparqlParserUtilities.parseSparqlFile(fileName, Collections.<String,String>emptyMap());
		 System.out.println("Query:\n"+q);
		 try {
			 com.hp.hpl.jena.query.Query jenaq = QueryFactory.create(q.toString());
		 } catch (Exception e) {
			 e.printStackTrace(System.out);
			 return false;
		 }
		 return true;
	}
}
