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
import org.junit.runner.RunWith;

import com.ibm.rdf.store.sparql11.TestRunner.DB2TestData;
import com.ibm.rdf.store.testing.RandomizedRepeat;

public abstract class UniprotQueryUtilityTest<D extends DB2TestData> extends TestRunner<D> {

	private final String queryDir;
	
	protected UniprotQueryUtilityTest(DatabaseEngine<D> engine, String queryDir, D data) {
		super(data, engine, null);
		this.queryDir = queryDir;
	}

	@RunWith(com.ibm.rdf.store.testing.RandomizedRepeatRunner.class)
	@RandomizedRepeat(5)
	public static class XIVC6 extends UniprotQueryUtilityTest<DB2TestData> {
		private static final DB2TestData data = new DB2TestData("jdbc:db2://localhost:50000/xivdb_c6", "uniprot", "db2inst1", "ihaterc2", "db2inst1", false);

		public XIVC6() {
			super(new DB2Engine(), "../rdfstore-data/uniprot_queries_rev/", data);
		}
	}
	
	@RunWith(com.ibm.rdf.store.testing.RandomizedRepeatRunner.class)
	@RandomizedRepeat(5)
	public static class TMSC6 extends UniprotQueryUtilityTest<DB2TestData> {
		private static final DB2TestData data = new DB2TestData("jdbc:db2://localhost:50000/tmsdb_c6", "uniprot", "db2inst1", "ihaterc2", "db2inst1", false);

		public TMSC6() {
			super(new DB2Engine(), "../rdfstore-data/uniprot_queries_rev/", data);
		}
	}

	public  void testBetaQuery(int queryNumber) throws Exception {
		String file = queryDir + "uniprot-beta-example-q" + queryNumber + ".sparql";
		int result = execute(file, false);
		System.out.println(file + " has : " + result + " rows");
	}

	@Test
	public  void testBetaQueryQ1() throws Exception {
		testBetaQuery(1);
	}

	@Test
	public  void testBetaQueryQ2() throws Exception {
		testBetaQuery(2);
	}
@Test
	public  void testBetaQueryQ3() throws Exception {
		testBetaQuery(3);
	}
@Test
	public  void testBetaQueryQ4() throws Exception {
		testBetaQuery(4);
	}
@Test
	public  void testBetaQueryQ5() throws Exception {
		testBetaQuery(5);
	}
@Test
	public  void testBetaQueryQ6() throws Exception {
		testBetaQuery(6);
	}
@Test
	public  void testBetaQueryQ7() throws Exception {
		testBetaQuery(7);
	}
@Test
	public  void testBetaQueryQ8() throws Exception {
		testBetaQuery(8);
	}
@Test
	public  void testBetaQueryQ9() throws Exception {
		testBetaQuery(9);
	}
@Test
	public  void testBetaQueryQ10() throws Exception {
		testBetaQuery(10);
	}
@Test
	public  void testBetaQueryQ11() throws Exception {
		testBetaQuery(11);
	}
@Test
	public  void testBetaQueryQ12() throws Exception {
		testBetaQuery(12);
	}
@Test
	public  void testBetaQueryQ13() throws Exception {
		testBetaQuery(13);
	}
@Test
	public  void testBetaQueryQ14() throws Exception {
		testBetaQuery(14);
	}
@Test
	public  void testBetaQueryQ15() throws Exception {
		testBetaQuery(15);
	}
@Test
	public  void testBetaQueryQ16() throws Exception {
		testBetaQuery(16);
	}
@Test
	public  void testBetaQueryQ17() throws Exception {
		testBetaQuery(17);
	}
@Test
	public  void testBetaQueryQ18() throws Exception {
		testBetaQuery(18);
	}

	public  void testQuery(int queryNumber) throws Exception {
		String file = queryDir + "query_" + queryNumber + ".sparql";
		int result = execute(file, false);
		System.out.println(file + " has : " + result + " rows");
	}
	
	@Test
	public  void testQueryQ1() throws Exception {
		testQuery(1);
	}

	@Test
	public  void testQueryQ2() throws Exception {
		testQuery(2);
	}

	@Test
	public  void testQueryQ4() throws Exception {
		testQuery(4);
	}

	@Test
	public  void testQueryQ5() throws Exception {
		testQuery(5);
	}

	@Test
	public  void testQueryQ6() throws Exception {
		testQuery(6);
	}

	@Test
	public  void testQueryQ9() throws Exception {
		String file = queryDir + "query_9r.sparql";
		int result = execute(file, false);
		System.out.println(file + " has : " + result + " rows");
	}

	@Test
	public  void testQueryQ10() throws Exception {
		testQuery(10);
	}

	@Test
	public  void testQueryQ11() throws Exception {
		testQuery(11);
	}

	@Test
	public  void testQueryQ13() throws Exception {
		testQuery(13);
	}

	@Test
	public  void testQueryQ14() throws Exception {
		testQuery(14);
	}

	@Test
	public  void testQueryQ17() throws Exception {
		testQuery(17);
	}

	@Test
	public  void testQueryQ19() throws Exception {
		testQuery(19);
	}

	@Test
	public  void testQueryQ20() throws Exception {
		testQuery(20);
	}

	@Test
	public  void testQueryQ21() throws Exception {
		testQuery(21);
	}

	@Test
	public  void testQueryQ15() throws Exception {
		testQuery(15);
	}

	@Test
	public  void testQueryQ23() throws Exception {
		testQuery(23);
	}
	
	@Test
	public  void testQueryQ3() throws Exception {
		testQuery(3);
	}

	@Test
	public  void testQueryQ12() throws Exception {
		testQuery(12);
	}
 
	@Test
	public  void testQueryQ22() throws Exception {
		testQuery(22);
	}

	@Test
	public  void testQueryQ18() throws Exception {
		testQuery(18);
	}

	/*
	@Test
	public  void testQueryQ16() throws Exception {
		testQuery(16);
	}
	*/

	@Test
	public  void testQueryQ8() throws Exception {
		testQuery(8);
	}

	/*
	@Test
	public  void testQueryQ7() throws Exception {
		testQuery(7);
	}
    */
}
