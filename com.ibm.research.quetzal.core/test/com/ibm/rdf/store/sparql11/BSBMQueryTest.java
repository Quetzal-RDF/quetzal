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


import org.junit.Test;

import com.ibm.rdf.store.sparql11.TestRunner.DB2TestData;

public class BSBMQueryTest extends TestRunner<DB2TestData> {
	private final String queryFile;
	
	public static class BSBMTest extends BSBMQueryTest {
		private static final DB2TestData data = new DB2TestData("jdbc:db2://helix1.pok.ibm.com:50001/bsbm",
				"bsbm_1m", "db2inst1","db2admin","db2inst1",false);
		
		public BSBMTest() {
			super(data, "../rdfstore-data/bsbm/");
		}
	}

	public BSBMQueryTest(DB2TestData data, String queryDir) {
		super(data, new DB2Engine(), null);
		this.queryFile = queryDir;
	}

	@Test
	public void testQuery1() throws Exception {
		this.executeQuery(queryFile + "bsbmq5.sparql");
	}

}
