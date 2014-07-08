package com.ibm.rdf.store.sparql11;


import org.junit.Test;

import com.ibm.rdf.store.sparql11.TestRunner.DB2TestData;

import static com.ibm.rdf.store.sparql11.TestRunner.DB2TestData.*;

public class RDFStoreExerciser extends TestRunner<DB2TestData> {
	private final String queryDir;
	
	public static class Min2TestRunner extends RDFStoreExerciser {
		private static final DB2TestData data = getStore("jdbc:db2://min-2.pok.ibm.com:50002/testrdf",
				"uobm1", "db2inst2","db2inst2","db2inst2",false);
		
		public Min2TestRunner() {
			super(data, "individual_test_queries/");
		}
	}
	
	public static class UOBM1uTestRunner extends RDFStoreExerciser {
		private static final DB2TestData data = getStore("jdbc:db2://9.47.202.45:50001/UOBM",
				"uobm1u", "db2inst2","db2admin","db2inst2",false);
		
		public UOBM1uTestRunner() {
			super(data, "individual_test_queries/");
		}
	}

	public RDFStoreExerciser(DB2TestData data, String queryDir) {
		super(data, new DB2Engine(), null);
		this.queryDir = queryDir;
	}

	
	@Test
	public void testQuery() throws Exception {
		String file = queryDir + "bad_lubm.sparql";
		executeQuery(file);
	}
}
