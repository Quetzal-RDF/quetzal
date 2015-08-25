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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ibm.rdf.store.sparql11.TestRunner.DB2Engine;
import com.ibm.rdf.store.sparql11.TestRunner.DB2TestData;
import com.ibm.rdf.store.sparql11.TestRunner.DatabaseEngine;
import com.ibm.rdf.store.sparql11.TestRunner.PSQLEngine;
import com.ibm.rdf.store.sparql11.TestRunner.PSQLTestData;
import com.ibm.rdf.store.sparql11.TestRunner.SharkEngine;
import com.ibm.rdf.store.sparql11.TestRunner.SharkTestData;
import com.ibm.rdf.store.sparql11.TestRunner.TestData;
import com.ibm.research.rdf.store.sparql11.SparqlParserUtilities;

public class CommandLineDriver {

	public static void checkExtensions() {
		String ext = System.getenv("QUETZAL_EXTENSIONS");
		if (ext != null && Boolean.parseBoolean(ext)) {
			SparqlParserUtilities.USE_EXTENSIONS = true;
		}
	}

	private static String getProtocol() {
		String dataClass = System.getenv("DB_ENGINE");
		if (dataClass.contains("db2")) {
			return "db2";
		} else if (dataClass.contains("postgresql")) {
			return "postgresql";
		} else if (dataClass.contains("shark")) {
			return "hive2";
		} else {
			assert false;
			return null;
		}
	}

	private static Class<? extends TestData> getDataClass() {
		String dataClass = System.getenv("DB_ENGINE");
		System.out.println("DB_ENGINE is set to:" + System.getenv("DB_ENGINE"));
		if (dataClass.contains("db2")) {
			return DB2TestData.class;
		} else if (dataClass.contains("postgresql")) {
			return PSQLTestData.class;
		} else if (dataClass.contains("shark")) {
			return SharkTestData.class;
		} else {
			assert false;
			return null;
		}
	}

	public static TestData getData() throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		String dataset = System.getenv("KNOWLEDGE_BASE");
		Class<? extends TestData> cls = getDataClass();
		System.out.println("connecting to:" + "jdbc:" + getProtocol() + "://" + System.getenv("DB2_HOST") + ":" + System.getenv("DB2_PORT") + "/" + System.getenv("DB2_DB"));
		Constructor<? extends TestData> factory = cls.getConstructor(String.class, String.class, String.class, String.class, String.class, boolean.class);
		return (TestData) factory.newInstance(
			"jdbc:" + getProtocol() + "://" + System.getenv("DB2_HOST") + ":" + System.getenv("DB2_PORT") + "/" + System.getenv("DB2_DB"), 
			dataset, 
			System.getenv("DB2_USER"),
			System.getenv("DB2_PASSWORD"),
			System.getenv("DB2_SCHEMA"),
			false);
	}

	public static void checkEnvironment() {
		if (System.getenv("KNOWLEDGE_BASE") == null) {
			System.out.println("KNOWLEDGE_BASE is not set in environment");
		} else {
			System.out.println("KNOWLEDGE_BASE is set to:" + System.getenv("KNOWLEDGE_BASE"));
		}
		if (System.getenv("DB2_HOST") == null) {
			System.out.println("DB2_HOST is not set in environment");
		} else {
			System.out.println("DB2_HOST is set to:" + System.getenv("DB2_HOST"));
		}
		if (System.getenv("DB2_PORT") == null) {
			System.out.println("DB2_PORT is not set in environment");
		} else {
			System.out.println("DB2_PORT is set to:" + System.getenv("DB2_PORT"));
		}
		if (System.getenv("DB2_DB") == null) {
			System.out.println("DB2_DB is not set in environment");
		} else {
			System.out.println("DB2_DB is set to:" + System.getenv("DB2_DB"));
		}
		if (System.getenv("DB2_USER") == null) {
			System.out.println("DB2_USER is not set in environment");
		} else {
			System.out.println("DB2_USER is set to:" + System.getenv("DB2_USER"));
		}
		if (System.getenv("DB2_PASSWORD") == null) {
			System.out.println("DB2_PASSWORD is not set in environment");
		} else {
			System.out.println("DB2_PASSWORD is set to:" + System.getenv("DB2_PASSWORD"));
		}
		if (System.getenv("DB2_SCHEMA") == null) {
			System.out.println("DB2_SCHEMA is not set in environment");
		} else {
			System.out.println("DB2_SCHEMA is set to:" + System.getenv("DB2_SCHEMA"));
		}
	}
	
	protected static int[] getAnswers(String answers) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field f = TestConstants.class.getField(answers);
		return (int[])f.get(null);
	}
	
	protected static void run(Object x, int repeat, boolean random) {
		List<Method> methods = new ArrayList<Method>();
		for(int i = 0; i < repeat; i++) {
			for(Method m : x.getClass().getDeclaredMethods()) {
				if (m.getAnnotation(org.junit.Test.class) != null) {
					methods.add(m);
				}
			}
		}
	
		if (random) {
			Collections.shuffle(methods);
		}
		
		for(Method m : methods) {
			try {
				m.invoke(x);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	protected static DatabaseEngine<? extends TestData> getEngine(TestData data, final boolean repeat) {
		if (data instanceof DB2TestData) {
			return new DB2Engine() { { print=!repeat; } };
		} else if (data instanceof PSQLTestData) {
			return new PSQLEngine() { { print=!repeat; } };
		} else if (data instanceof SharkTestData) {
			return new SharkEngine() { { print=!repeat; } };
		} else {
			assert false;
			return null;
		}
	}

	public static void main(String[] args) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InstantiationException, InvocationTargetException {
		@SuppressWarnings("unchecked")
		Class<TestRunner<? extends TestData>> cls = (Class<TestRunner<? extends TestData>>) Class.forName(args[0]);
		Constructor<TestRunner<? extends TestData>> ctor = cls.getDeclaredConstructor(DatabaseEngine.class, Object.class, int[].class, String.class);
		TestData data = getData();
		int repeatCount = Integer.parseInt(args[2]);
		run(ctor.newInstance(getEngine(data, repeatCount == 1), data, getAnswers(args[1]), System.getenv("TEST_DIR")), repeatCount, Boolean.parseBoolean(args[3]));
	}

}

