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

import junit.framework.TestSuite;

import com.ibm.rdf.store.sparql11.TestRunner.DB2Engine;
import com.ibm.rdf.store.sparql11.TestRunner.DB2TestData;

public class DB2JazzNewTest extends JazzNewTest {
	public static TestSuite suite() {
		TestSuite jazzTests = new TestSuite();
		DB2TestData data = new DB2TestData("jdbc:db2://9.47.204.38:60000/Rational", "large", "db2inst1", "db2admin", "db2inst1", true);
		DB2Engine engine = new DB2Engine();
		addTests(jazzTests, engine, data);	
		return jazzTests;
	}
}
