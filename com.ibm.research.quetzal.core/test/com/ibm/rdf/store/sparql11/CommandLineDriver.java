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

public class CommandLineDriver  {

	private static String getProtocol(String dataClass) {
		if (dataClass.contains("DB2")) {
			return "db2";
		} else if (dataClass.contains("PSQL")) {
			return "postgresql";
		} else if (dataClass.contains("Shark")) {
			return "hive2";
		} else {
			assert false;
			return null;
		}
	}
	
	protected static TestData getData(String dataClass, String dataset) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Class<? extends TestData> cls = (Class<? extends TestData>) Class.forName(dataClass);
		Method factory = cls.getMethod("getStore", String.class, String.class, String.class, String.class, String.class, boolean.class);
		return (TestData) factory.invoke(null,
			"jdbc:" + getProtocol(dataClass) + "://" + System.getenv("DB2_HOST") + ":" + System.getenv("DB2_PORT") + "/" + System.getenv("DB2_DB"), 
			dataset, 
			System.getenv("DB2_USER"),
			System.getenv("DB2_PASSWORD"),
			System.getenv("DB2_SCHEMA"),
			false);
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

	private static DatabaseEngine<?> getEngine(TestData data) {
		if (data instanceof DB2TestData) {
			return new DB2Engine();
		} else if (data instanceof PSQLTestData) {
			return new PSQLEngine();
		} else if (data instanceof SharkTestData) {
			return new SharkEngine();
		} else {
			assert false;
			return null;
		}
	}

	public static void main(String[] args) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InstantiationException, InvocationTargetException {
		Class<TestRunner<? extends TestData>> cls = (Class<TestRunner<? extends TestData>>) Class.forName(args[0]);
		Constructor<TestRunner<? extends TestData>> ctor = cls.getDeclaredConstructor(DatabaseEngine.class, Object.class, int[].class, String.class);
		TestData data = getData(args[1], args[2]);
		run(ctor.newInstance(getEngine(data), data, getAnswers(args[3]), args[4]), Integer.parseInt(args[5]), Boolean.parseBoolean(args[6]));
	}

}

