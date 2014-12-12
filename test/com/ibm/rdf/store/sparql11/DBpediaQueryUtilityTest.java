package com.ibm.rdf.store.sparql11;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.ibm.rdf.store.testing.RandomizedRepeat;

public class DBpediaQueryUtilityTest<D> extends TestRunner<D> {
	private final String queryDir;
	
	public DBpediaQueryUtilityTest(DatabaseEngine<D> engine, D data, int[] answers, String queryDir) {
		super(data, engine, answers);
		this.queryDir = queryDir;
	}

	//@RunWith(com.ibm.research.rdf.store.testing.RandomizedRepeatRunner.class)
	//@RandomizedRepeat(8)
	public static class Reversed10M extends DBpediaQueryUtilityTest<DB2TestData> {
		private static final DB2TestData data = DB2TestData.getStore("jdbc:db2://pasta-dev.watson.ibm.com:50002/testrev", "dbp10m_r", "db2inst4", "sheruser", "db2inst4", false);
		public Reversed10M() {
			super(new DB2Engine(), data, TestConstants.dbpedia10mAnswers, "../rdfstore-data/dbpedia_queries_rev/");
		}
	}

	//@RunWith(com.ibm.research.rdf.store.testing.RandomizedRepeatRunner.class)
	//@RandomizedRepeat(8)
	public static class RDFStoreDBpedia37 extends DBpediaQueryUtilityTest<DB2TestData> {
		private static final DB2TestData data = DB2TestData.getStore("jdbc:db2://localhost:8997/dbp", "dbp37_r", "db2inst1", "db2admin", "db2inst1", false);
		public RDFStoreDBpedia37() {
			super(new DB2Engine(), data, TestConstants.dbpedia100mAnswers, "/Users/ksrinivs/Documents/workspace/rdfstore-data/dbpedia3.7_queries_rev/");
		}
	}

	
	@RunWith(com.ibm.rdf.store.testing.RandomizedRepeatRunner.class)
	@RandomizedRepeat(8)
	public static class RDFStoreDBpediaRC237 extends DBpediaQueryUtilityTest<DB2TestData> {
		private static final DB2TestData data = DB2TestData.getStore("jdbc:db2://9.47.202.45:50001/dbpedia", "dbpedia", "db2inst2", "db2admin", "db2inst2", false);
		public RDFStoreDBpediaRC237() {
			super(new DB2Engine(), data, TestConstants.dbpedia100mAnswers, "../rdfstore-data/dbpedia3.7_queries_rev/");
		}
	}
	

	public static class RDFStoreDBpedia37_RC2 extends DBpediaQueryUtilityTest<DB2TestData> {
		private static final DB2TestData data = DB2TestData.getStore("jdbc:db2://9.47.202.45:50001/dbpedia", "dbpedia", "db2inst2", "db2admin", "db2inst2", false);
		public RDFStoreDBpedia37_RC2() {
			super(new DB2Engine(), data, TestConstants.dbpedia100mAnswers, "../rdfstore-data/dbpedia3.7_queries_rev/");
		}
	}
		
	@Test
	public  void testQueryQ1() throws Exception {
		String file = queryDir + "dq.1.sparql";
		System.out.println("Testing:" + file);
		int result = executeQuery(file, 0);
		System.out.println(file + " has : " + result + " rows");
	}
	
	@Test
	public  void testQueryQ2() throws Exception {
		String file = queryDir + "dq.2.sparql";
		System.out.println("Testing:" + file);
		int result = executeQuery(file, 1);
		System.out.println(file + " has : " + result + " rows");
	}
	
	@Test
	public  void testQueryQ3() throws Exception {
		String file = queryDir + "dq.3.sparql";
		System.out.println("Testing:" + file);
		int result = executeQuery(file, 2);
		System.out.println(file + " has : " + result + " rows");
	}
	
	@Test
	public  void testQueryQ4() throws Exception {
		String file = queryDir + "dq.4.sparql";
		System.out.println("Testing:" + file);
		int result = executeQuery(file, 3);
		System.out.println(file + " has : " + result + " rows");
	}
		
	
	@Test
	public  void testQueryQ5() throws Exception {
		String file = queryDir + "dq.5.sparql";
		System.out.println("Testing:" + file);
		int result = executeQuery(file, 4);
		System.out.println(file + " has : " + result + " rows");
	}
	
	@Test
	public  void testQueryQ6() throws Exception {
		String file = queryDir + "dq.6.sparql";
		System.out.println("Testing:" + file);
		int result = executeQuery(file, 5);
		System.out.println(file + " has : " + result + " rows");
	}
			
	@Test
	public  void testQueryQ7() throws Exception {
		String file = queryDir + "dq.7.sparql";
		System.out.println("Testing:" + file);
		int result = executeQuery(file, 6);
		System.out.println(file + " has : " + result + " rows");
	}

	
	@Test
	public  void testQueryQ8() throws Exception {
		String file = queryDir + "dq.8.sparql";
		System.out.println("Testing:" + file);
		int result = executeQuery(file, 7);
		System.out.println(file + " has : " + result + " rows");
	}
	
	@Test
	public  void testQueryQ9() throws Exception {
		String file = queryDir + "dq.9.sparql";
		System.out.println("Testing:" + file);
		int result = executeQuery(file, 8);
		System.out.println(file + " has : " + result + " rows");
	}
	
	
	@Test
	public  void testQueryQ10() throws Exception {
		String file = queryDir + "dq.10.sparql";
		System.out.println("Testing:" + file);
		int result = executeQuery(file, 9);
		System.out.println(file + " has : " + result + " rows");
	}
	
	@Test
	public  void testQueryQ11() throws Exception {
		String file = queryDir + "dq.11.sparql";
		System.out.println("Testing:" + file);
		int result = executeQuery(file, 10);
		System.out.println(file + " has : " + result + " rows");
	}
	
	@Test
	public  void testQueryQ12() throws Exception {
		String file = queryDir + "dq.12.sparql";
		System.out.println("Testing:" + file);
		int result = executeQuery(file, 11);
		System.out.println(file + " has : " + result + " rows");
	}
	
	@Test
	public  void testQueryQ13() throws Exception {
		String file = queryDir + "dq.13.sparql";
		System.out.println("Testing:" + file);
		int result = executeQuery(file, 12);
		System.out.println(file + " has : " + result + " rows");
	}
	
	@Test
	public  void testQueryQ14() throws Exception {
		String file = queryDir + "dq.14.sparql";
		System.out.println("Testing:" + file);
		int result = executeQuery(file, 13);
		System.out.println(file + " has : " + result + " rows");
	}
	
	@Test
	public  void testQueryQ15() throws Exception {
		String file = queryDir + "dq.15.sparql";
		System.out.println("Testing:" + file);
		int result = executeQuery(file, 14);
		System.out.println(file + " has : " + result + " rows");
	}	
	@Test
	public  void testQueryQ16() throws Exception {
		String file = queryDir + "dq.16.sparql";
		System.out.println("Testing:" + file);
		int result = executeQuery(file, 15);
		System.out.println(file + " has : " + result + " rows");
	}	
	@Test
	public  void testQueryQ17() throws Exception {
		String file = queryDir + "dq.17.sparql";
		System.out.println("Testing:" + file);
		int result = executeQuery(file, 16);
		System.out.println(file + " has : " + result + " rows");
	}	
	@Test
	public  void testQueryQ18() throws Exception {
		String file = queryDir + "dq.18.sparql";
		System.out.println("Testing:" + file);
		int result = executeQuery(file, 17);
		System.out.println(file + " has : " + result + " rows");
	}	
	@Test
	public  void testQueryQ19() throws Exception {
		String file = queryDir + "dq.19.sparql";
		System.out.println("Testing:" + file);
		int result = executeQuery(file, 18);
		System.out.println(file + " has : " + result + " rows");
	}	
	@Test
	public  void testQueryQ20() throws Exception {
		String file = queryDir + "dq.20.sparql";
		System.out.println("Testing:" + file);
		int result = executeQuery(file, 19);
		System.out.println(file + " has : " + result + " rows");
	}
	
}
