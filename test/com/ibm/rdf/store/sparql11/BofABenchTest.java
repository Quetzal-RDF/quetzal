package com.ibm.rdf.store.sparql11;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.ibm.rdf.store.sparql11.TestRunner.DB2TestData;
import com.ibm.rdf.store.testing.RandomizedRepeat;

public abstract class BofABenchTest<D extends DB2TestData> extends TestRunner<D> {
	private final String queryDir;
	
	static final int[] BofAanswers = {100, 25, 5, 100};
	
	protected BofABenchTest(DatabaseEngine<D> engine, D data, String queryDir, int[] ans) {
		super(data, engine, ans);
		this.queryDir = queryDir;
	}

	@RunWith(com.ibm.rdf.store.testing.RandomizedRepeatRunner.class)
	@RandomizedRepeat(8)
	public static class Reversed75M extends BofABenchTest<DB2TestData> {
		private static final DB2TestData data = TestRunner.DB2TestData.getStore("jdbc:db2://9.47.205.206:50000/bofaben", "bofa", "db2inst1", "db2admin", "db2inst1", false);

		public Reversed75M() {
			super(new DB2Engine(), data, "../rdfstore-data/bofa/reversedQueries/", BofAanswers);
		}
	}
	
	@Test
	public  void testQueryQObject() throws Exception {
		String file = queryDir + "q-object.sparql";
		System.err.println("Testing:" + file);
		int result = executeQuery(file, 0);
		System.out.println(file + " has : " + result + " rows");
	}
	
	@Test
	public  void testQueryQSystime() throws Exception {
		String file = queryDir + "q-systime.sparql";
		System.err.println("Testing:" + file);
		int result = executeQuery(file, 1);
		System.out.println(file + " has : " + result + " rows");
	}
	
	@Test
	public  void testQueryQOvertime() throws Exception {
		String file = queryDir + "q-overtime.sparql";
		System.err.println("Testing:" + file);
		int result = executeQuery(file, 2);
		System.out.println(file + " has : " + result + " rows");
	}
	
	
	@Test
	public  void testQueryQTypeahead() throws Exception {
		String file = queryDir + "q-typehead.sparql";
		System.err.println("Testing:" + file);
		int result = executeQuery(file, 3);
		System.out.println(file + " has : " + result + " rows");
	}

}
