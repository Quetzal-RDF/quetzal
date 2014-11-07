package com.ibm.rdf.store.sparql11;

import java.util.Collections;

import org.junit.Test;

import com.ibm.rdf.store.sparql11.TestRunner.DB2TestData;
import com.ibm.rdf.store.sparql11.model.Query;
import com.ibm.rdf.store.utilities.MinusRewriter;

import static com.ibm.rdf.store.sparql11.TestRunner.DB2TestData.*;

public class BSBMQueryTest extends TestRunner<DB2TestData> {
	private final String queryFile;
	
	public static class BSBMTest extends BSBMQueryTest {
		private static final DB2TestData data = getStore("jdbc:db2://helix1.pok.ibm.com:50001/bsbm",
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
		this.executeQuery(queryFile + "bsbmq1.sparql");
	}

}
