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
 package com.ibm.rdf.store.sparql11;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.ibm.rdf.store.sparql11.TestRunner.DatabaseEngine;


public abstract class JazzNewTest extends TestCase {
	static boolean debug = true;
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return JazzNewTest.class.getName();
	}


	private static List<String> projects = new LinkedList<String>();
	
	static {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					"../rdfstore-data/jazzNew/DS1_projects.txt"));
			String line = null;
			while ((line = reader.readLine()) != null) {
				StringTokenizer tokenizer = new StringTokenizer(line, " ");
				assert tokenizer.countTokens() >= 1;
				projects.add(tokenizer.nextToken().trim());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	private static class JazzTest<D> extends TestCase {
		private final String testFile;
		private final String substitute;
		private String query = null;
		private DatabaseEngine<D> engine;
		private D data;

		public JazzTest(String testFile, String substitute, DatabaseEngine<D> engine, D data) {
			super(testFile + " - " + substitute);
			this.testFile = testFile + "-" + substitute;
			this.substitute = substitute;
			this.engine = engine;
			this.data = data;
			
			try {
				BufferedReader reader = new BufferedReader(new FileReader(
						testFile));
				StringBuffer buf = new StringBuffer();
				String line = null;
				while ((line = reader.readLine()) != null) {
					buf.append(line).append("\n");
				}
				query = buf.toString();
				query = query.replaceAll("%s", substitute);
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		@Override
		protected void runTest() {
			try {
				long time = System.currentTimeMillis();				
				int rows = engine.executeStringQuery(data, query);
				System.out.println(getName() + " has: " + rows);
				System.out.println(getName() + " time:" + (System.currentTimeMillis() - time));
				
			} catch (Exception e) {
				System.err.println(e);
			}
		}
		
		@Override
		public String getName() {
			return testFile;
		}
		
	}


	protected static <T> void addTests(TestSuite jazzTests, DatabaseEngine<T> engine, T data) {
		File f = new File("../rdfstore-data/jazzNew/");
		assert f.isDirectory();
		File[] files = f.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				if (name.equals("DS1_projects.txt") || name.endsWith("CVS")) {
					return false;
				}
				return true;
			}
		});
		if (debug) {
			//jazzTests.addTest(new JazzTest(files[0].getAbsolutePath(), projects.get(0), engine, data));
			jazzTests.addTest(new JazzTest("/Users/ksrinivs/Documents/workspace/rdfstore-data/Bind-rev.sparql", projects.get(0), engine, data));
		} else {
			for (File fl: files) {
				for (String p : projects) {
					jazzTests.addTest(new JazzTest(fl.getAbsolutePath(), p, engine, data));
				}
			}
		}
	}
	

}
