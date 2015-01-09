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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.rdf.store.testing.RandomizedRepeat;

public abstract class JazzQueryUtilityTest<D> extends TestRunner<D> {
	final static Logger   logger         = LoggerFactory.getLogger(JazzQueryUtilityTest.class);
	
	private static final String reverseQueryDir = "../rdfstore-data/jazz_queries_rev/";
	private static final String normalQueryDir = "../rdfstore-data/jazz_queries/";
	
	private final String queryDir;

	protected JazzQueryUtilityTest(DatabaseEngine<D> engine, D data, boolean reverse) {
		super(data, engine, TestConstants.jazzAnswers);
		queryDir = reverse? reverseQueryDir: normalQueryDir;
	}

   // @RunWith(com.ibm.research.rdf.store.testing.RandomizedRepeatRunner.class)
   // @RandomizedRepeat(1)
   public static class DB2JazzHelix1 extends JazzQueryUtilityTest<DB2TestData>
      {
      private static final DB2TestData data = new DB2TestData("jdbc:db2://helix1.pok.ibm.com:50001/jazz", "jazz",
                                                  "db2inst1", "db2inst1", "db2inst1", true);

      public DB2JazzHelix1()
         {
         super(new DB2Engine(), data, true);
         }
      }

   // @RunWith(com.ibm.research.rdf.store.testing.RandomizedRepeatRunner.class)
   // @RandomizedRepeat(1)
   public static class SharkJazzVM_9_12_196_243 extends JazzQueryUtilityTest<SharkTestData>
      {
      private static final SharkTestData data = new SharkTestData("jdbc:hive2://9.12.196.243:10000/default", "jazz", "root", "nkoutche",
   		   "default", false);
      public SharkJazzVM_9_12_196_243()
         {
         super(new SharkEngine(), data, true);
         }
      }

   // @RunWith(com.ibm.research.rdf.store.testing.RandomizedRepeatRunner.class)
   // @RandomizedRepeat(1)
   public static class PSQLJazzHelix1 extends JazzQueryUtilityTest<PSQLTestData>
      {
      private static final PSQLTestData data = new PSQLTestData("jdbc:postgresql://helix1.pok.ibm.com:24973/jazz", "jazz",
                                                  "akement", "passw0rd", "db2inst1", true);

      public PSQLJazzHelix1()
         {
         super(new PSQLEngine(), data, true);
         }
      }

	//@RunWith(com.ibm.research.rdf.store.testing.RandomizedRepeatRunner.class)
	//@RandomizedRepeat(1)
	public static class JazzOptimizedStore extends JazzQueryUtilityTest<DB2TestData> {
		private static final DB2TestData data =  new DB2TestData("jdbc:db2://9.47.202.45:50001/jazz", "jazz", "db2inst2", "db2admin", "db2inst2", true);
		public JazzOptimizedStore() {
			super(new DB2Engine(), data, true);
		}
	}

	@RunWith(com.ibm.rdf.store.testing.RandomizedRepeatRunner.class)
	@RandomizedRepeat(8)
	public static class JazzOptimizedStoreSL extends JazzQueryUtilityTest<DB2TestData> {
		private static final DB2TestData data = new DB2TestData("jdbc:db2://9.47.204.38:60000/Rational", "old", "db2inst1", "db2admin", "db2inst1", true);
		public JazzOptimizedStoreSL() {
			super(new DB2Engine(), data, true);
		}
	}
	

	//@RunWith(com.ibm.research.rdf.store.testing.RandomizedRepeatRunner.class)
	//@RandomizedRepeat(8)
	public static class JazzOptimizedStoreOld extends JazzQueryUtilityTest<DB2TestData> {
		private static final DB2TestData data = new DB2TestData("jdbc:db2://min-1.watson.ibm.com:50001/jaztypes", "jazzopt", "db2inst1", "db2inst1", "db2inst1", true);
		public JazzOptimizedStoreOld() {
			super(new DB2Engine(), data, true);
		}
	}
	
	@RunWith(com.ibm.rdf.store.testing.RandomizedRepeatRunner.class)
	@RandomizedRepeat(8)
	public static class JazzDefaultStore extends JazzQueryUtilityTest<DB2TestData> {
		private static final DB2TestData data = new DB2TestData("jdbc:db2://min-1.watson.ibm.com:50001/jaztypes", "defjazzstore", "db2inst1", "db2inst1", "db2inst1", true);
		public JazzDefaultStore() {
			super(new DB2Engine(), data, true);
		}
	}
			
	@Test
	public  void testQueryQ1() throws Exception {
		String file = queryDir + "jq1.sparql";
		logger.info("Testing:" + file);
		int result = executeQuery(file, 0);
		logger.info(file + " has : " + result + " rows");
	}
	
	@Test
	public  void testQueryQ2() throws Exception {
		String file = queryDir + "jq2.sparql";
		logger.info("Testing:" + file);
		int result = executeQuery(file, 1);
		logger.info(file + " has : " + result + " rows");
	}
	
	@Test
	public  void testQueryQ3() throws Exception {
		String file = queryDir + "jq3.sparql";
		logger.info("Testing:" + file);
		int result = executeQuery(file, 2);
		logger.info(file + " has : " + result + " rows");
	}
	
	@Test
	public  void testQueryQ4() throws Exception {
		String file = queryDir + "jq4.sparql";
		logger.info("Testing:" + file);
		int result = executeQuery(file, 3);
		logger.info(file + " has : " + result + " rows");
	}
		
	
	@Test
	public  void testQueryQ5() throws Exception {
		String file = queryDir + "jq5.sparql";
		logger.info("Testing:" + file);
		int result = executeQuery(file, 4);
		logger.info(file + " has : " + result + " rows");
	}
	
	@Test
	public  void testQueryQ6() throws Exception {
		String file = queryDir + "jq6.sparql";
		logger.info("Testing:" + file);
		int result = executeQuery(file, 5);
		logger.info(file + " has : " + result + " rows");
	}
			
	@Test
	public  void testQueryQ7() throws Exception {
		String file = queryDir + "jq7.sparql";
		logger.info("Testing:" + file);
		int result = executeQuery(file, 6);
		logger.info(file + " has : " + result + " rows");
	}

	
	@Test
	public  void testQueryQ8() throws Exception {
		String file = queryDir + "jq8.sparql";
		logger.info("Testing:" + file);
		int result = executeQuery(file, 7);
		logger.info(file + " has : " + result + " rows");
	}
	
	@Test
	public  void testQueryQ9() throws Exception {
		String file = queryDir + "jq9.sparql";
		logger.info("Testing:" + file);
		int result = executeQuery(file, 8);
		logger.info(file + " has : " + result + " rows");
	}
	
	
	@Test
	public  void testQueryQ10() throws Exception {
		String file = queryDir + "jq10.sparql";
		logger.info("Testing:" + file);
		int result = executeQuery(file, 9);
		logger.info(file + " has : " + result + " rows");
	}
	
	@Test
	public  void testQueryQ11() throws Exception {
		String file = queryDir + "jq11.sparql";
		logger.info("Testing:" + file);
		int result = executeQuery(file, 10);
		logger.info(file + " has : " + result + " rows");
	}
	
	@Test
	public  void testQueryQ12() throws Exception {
		String file = queryDir + "jq12.sparql";
		logger.info("Testing:" + file);
		int result = executeQuery(file, 11);
		logger.info(file + " has : " + result + " rows");
	}
	
	@Test
	public  void testQueryQ13() throws Exception {
		String file = queryDir + "jq13.sparql";
		logger.info("Testing:" + file);
		int result = executeQuery(file, 12);
		logger.info(file + " has : " + result + " rows");
	}
	
	@Test
	public  void testQueryQ14() throws Exception {
		String file = queryDir + "jq14.sparql";
		logger.info("Testing:" + file);
		int result = executeQuery(file, 13);
		logger.info(file + " has : " + result + " rows");
	}
	
	@Test
	public  void testQueryQ15() throws Exception {
		String file = queryDir + "jq15.sparql";
		logger.info("Testing:" + file);
		int result = executeQuery(file, 14);
		logger.info(file + " has : " + result + " rows");
	}	
	@Test
	public  void testQueryQ16() throws Exception {
		String file = queryDir + "jq16.sparql";
		logger.info("Testing:" + file);
		int result = executeQuery(file, 15);
		logger.info(file + " has : " + result + " rows");
	}	
	@Test
	public  void testQueryQ17() throws Exception {
		String file = queryDir + "jq17.sparql";
		logger.info("Testing:" + file);
		int result = executeQuery(file, 16);
		logger.info(file + " has : " + result + " rows");
	}	
	@Test
	public  void testQueryQ18() throws Exception {
		String file = queryDir + "jq18.sparql";
		logger.info("Testing:" + file);
		int result = executeQuery(file, 17);
		logger.info(file + " has : " + result + " rows");
	}	
	@Test
	public  void testQueryQ19() throws Exception {
		String file = queryDir + "jq19.sparql";
		logger.info("Testing:" + file);
		int result = executeQuery(file, 18);
		logger.info(file + " has : " + result + " rows");
	}	
	@Test
	public  void testQueryQ20() throws Exception {
		String file = queryDir + "jq20.sparql";
		logger.info("Testing:" + file);
		int result = executeQuery(file, 19);
		logger.info(file + " has : " + result + " rows");
	}
	@Test
	public  void testQueryQ21() throws Exception {
		String file = queryDir + "jq21.sparql";
		logger.info("Testing:" + file);
		int result = executeQuery(file, 20);
		logger.info(file + " has : " + result + " rows");
	}
	@Test
	public  void testQueryQ22() throws Exception {
		String file = queryDir + "jq22.sparql";
		logger.info("Testing:" + file);
		int result = executeQuery(file, 21);
		logger.info(file + " has : " + result + " rows");
	}
	@Test
	public  void testQueryQ23() throws Exception {
		String file = queryDir + "jq23.sparql";
		logger.info("Testing:" + file);
		int result = executeQuery(file, 22);
		logger.info(file + " has : " + result + " rows");
	}
	@Test
	public  void testQueryQ24() throws Exception {
		String file = queryDir + "jq24.sparql";
		logger.info("Testing:" + file);
		int result = executeQuery(file, 23);
		logger.info(file + " has : " + result + " rows");
	}
	@Test
	public  void testQueryQ25() throws Exception {
		String file = queryDir + "jq25.sparql";
		logger.info("Testing:" + file);
		int result = executeQuery(file, 24);
		logger.info(file + " has : " + result + " rows");
	}
	@Test
	public  void testQueryQ26() throws Exception {
		String file = queryDir + "jq26.sparql";
		logger.info("Testing:" + file);
		int result = executeQuery(file, 25);
		logger.info(file + " has : " + result + " rows");
	}
	@Test
	public  void testQueryQ27() throws Exception {
		String file = queryDir + "jq27.sparql";
		logger.info("Testing:" + file);
		int result = executeQuery(file, 26);
		logger.info(file + " has : " + result + " rows");
	}
	@Test
	public  void testQueryQ28() throws Exception {
		String file = queryDir + "jq28.sparql";
		logger.info("Testing:" + file);
		int result = executeQuery(file, 27);
		logger.info(file + " has : " + result + " rows");
	}
	@Test
	public  void testQueryQ29() throws Exception {
		String file = queryDir + "jq29.sparql";
		logger.info("Testing:" + file);
		int result = executeQuery(file, 28);
		logger.info(file + " has : " + result + " rows");
	}
}
