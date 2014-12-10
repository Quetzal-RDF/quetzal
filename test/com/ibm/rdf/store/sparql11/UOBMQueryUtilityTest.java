package com.ibm.rdf.store.sparql11;

import static com.ibm.rdf.store.sparql11.TestRunner.DB2TestData.getStore;
import junit.framework.Assert;

import org.junit.Test;

import com.ibm.rdf.store.sparql11.TestRunner.DB2TestData;
import com.ibm.rdf.store.sparql11.TestRunner.PSQLTestData;

public class UOBMQueryUtilityTest<D> extends TestRunner<D> {
	protected final String queryDir;
	static final int[] answers_original = {0, 0, 25, 533, 0, 5112, 1, 29, 34, -2, 47, 0, 0, 376};
	static final int[] answers_proppaths = {2343, 38135, 25, 533, 322, 5112, 1, 29, 34, 27355, 47, 0, 0, 376, 353, 0, 9931, -2,  28898, -2, 354, 1152768, 23, 322,0,0, 9, 1162699};
	static final int[] answers_proppaths_expansion = { 2343, 38135, 25, 533, 322, 5112, 1, 29, 34, 27355, 47, 0, 0, 376, 353,  0, 9931, -2,  28898, -2, 354,  1152768, 23, 322,0,0, 9, 1162699};
	static final int[] answers_proppaths_expansion_uobm1 = { 2352, 37808, 22, 533, 322, 184, 4, 36, 26, -2, 90, 0, 0, -2, 350,  0, 350, -2,  1017, -2, 351,  1152768, 23, 322,0,0, 6, -2};
	
	
	protected UOBMQueryUtilityTest(DatabaseEngine<D> engine, String queryDir, D data, int[] answers) {
		super(data, engine, answers);
		this.queryDir = queryDir;
	}

	/*public static class UOBM1 extends UOBMQueryUtilityTest<DB2TestData> {
		private static final DB2TestData data = getStore("jdbc:db2://9.47.202.45:50001/uobm",
				"uobm1u", "db2inst2","db2admin","db2inst2",false);
		public UOBM1() {
			super(new DB2Engine(), "../OWLQLTestcases/UOBM/sparql_queries/", data);
		}
	}*/

	public static class UOBM30 extends UOBMQueryUtilityTest<DB2TestData> {
		private static final DB2TestData data = getStore("jdbc:db2://9.47.202.45:50001/uobm",
				"uobm30", "db2inst2","db2admin","db2inst2",false);
		public UOBM30() {
			super(new DB2Engine(),"../rdfstore-data/uobm_queries_original/", data, answers_original);
		}
	}
	public static class DB2UOBM30ALLPropPathsVM9_47_202_45 extends DB2UOBM30PropPathsVM9_47_202_45  {
		@Test 
		public void testQ10() {
			test(9);
		}
		@Test 
		public void testQ14() {
			test(13);
		}
		@Test 
		public void testQ18() {
			test(17);
		}
		@Test 
		public void testQ28() {
			test(27);
		}
	}
	public static class DB2UOBM30PropPathsVM9_47_202_45 extends UOBMQueryUtilityTest<DB2TestData> {
		private static final DB2TestData data = getStore("jdbc:db2://9.47.202.45:50001/uobm",
				"uobm30", "db2inst2","db2admin","db2inst2",false);
		public DB2UOBM30PropPathsVM9_47_202_45() {
			super(new DB2Engine(),"../rdfstore-data/uobm_queries_proppaths/", data, answers_proppaths);
		}
		@Test 
		public void testQ15() {
			test(14);
		}
		@Test 
		public void testQ16() {
			test(15);
		}
		@Test 
		public void testQ17() {
			test(16);
		}
		/*@Test 
		public void testQ18() {
			test(17);
		}*/
		@Test 
		public void testQ19() {
			test(18);
		}
		@Test 
		public void testQ20() {
			//test(19);
		}
		@Test 
		public void testQ21() {
			test(20);
		}
		@Test 
		public void testQ22() {
			//test(21);
			// tested in property path expansion regression test
		}
		@Test 
		public void testQ23() {
			test(22);
		}
		@Test 
		public void testQ24() {
			test(23);
		}
		@Test 
		public void testQ25() {
			test(24);
		}
		@Test 
		public void testQ26() {
			test(25);
		}
		@Test 
		public void testQ27() {
			test(26);
		}
		/*@Test 
		public void testQ28() {
			test(27);
		}*/
	}
	public static class PSQLUOBM30MALLPropPathsHelix extends PSQLUOBM30PropPathsHelix  {
		@Test 
		public void testQ10() {
			test(9);
		}
		@Test 
		public void testQ14() {
			test(13);
		}
		@Test 
		public void testQ18() {
			test(17);
		}
		@Test 
		public void testQ28() {
			test(27);
		}
	}
	
	public static class PSQLUOBM30PropPathsHelix extends UOBMQueryUtilityTest<PSQLTestData> {
		private static final PSQLTestData data =PSQLTestData.getStore("jdbc:postgresql://localhost:8996/uobm", "uobm_30", "akement", "passw0rd",
                "db2inst1", false);
		public PSQLUOBM30PropPathsHelix() {
			super(new PSQLEngine(),"../rdfstore-data/uobm_queries_proppaths/", data, answers_proppaths);
		}
		@Test 
		public void testQ15() {
			test(14);
		}
		@Test 
		public void testQ16() {
			test(15);
		}
		@Test 
		public void testQ17() {
			test(16);
		}
		/*@Test 
		public void testQ18() {
			test(17);
		}*/
		@Test 
		public void testQ19() {
			test(18);
		}
		@Test 
		public void testQ20() {
			//test(19);
		}
		@Test 
		public void testQ21() {
			test(20);
		}
		@Test 
		public void testQ22() {
			//test(21);
			// tested in property path expansion regression test
		}
		@Test 
		public void testQ23() {
			test(22);
		}
		@Test 
		public void testQ24() {
			test(23);
		}
		@Test 
		public void testQ25() {
			test(24);
		}
		@Test 
		public void testQ26() {
			test(25);
		}
		@Test 
		public void testQ27() {
			test(26);
		}
		/*@Test 
		public void testQ28() {
			test(27);
		}*/
	}
	public static class PSQLUOBM30MALLPropPathsExpHelix1  extends PSQLUOBM30MPropPathsExpHelix1 {
		@Test 
		public void testQ10() {
			test(9);
		}
		@Test 
		public void testQ14() {
			test(13);
		}
		@Test 
		public void testQ18() {
			test(17);
		}
		@Test 
		public void testQ28() {
			test(27);
		}
	}
	public static class SharkUOBM1MPropPathsExpVM_9_12_196_243 extends UOBMQueryUtilityTest<SharkTestData> {
		private static final SharkTestData data = SharkTestData.getStore("jdbc:hive2://9.12.196.243:10000/default", "uobm1a", "root", "nkoutche",
                "default", false);
			//getStore("jdbc:db2://9.47.202.45:50001/uobm",
			//	"uobm30", "db2inst2","db2admin","db2inst2",false);
		public SharkUOBM1MPropPathsExpVM_9_12_196_243() {
			super(new SharkEngine(),"../rdfstore-data/uobm_queries_proppaths_expansion/", data, 
					answers_proppaths_expansion_uobm1);
		}
		@Test 
		public void testQ15() {
			test(14);
		}
		@Test 
		public void testQ16() {
			test(15);
		}
		@Test 
		public void testQ17() {
			test(16);
		}
		/*@Test 
		public void testQ18() {
			test(17);
		}*/
		@Test 
		public void testQ19() {
			test(18);
		}
		@Test 
		public void testQ20() {
			//test(19);
		}
		@Test 
		public void testQ21() {
			test(20);
		}
		@Test 
		public void testQ22() {
			//test(21);
		}
		@Test 
		public void testQ23() {
			test(22);
		}
		@Test 
		public void testQ24() {
			test(23);
		}
		@Test 
		public void testQ25() {
			test(24);
		}
		@Test 
		public void testQ26() {
			test(25);
		}
		@Test 
		public void testQ27() {
			test(26);
		}
		/*@Test 
		public void testQ28() {
			test(27);
		}*/
	}
	
	public static class PSQLUOBM30MPropPathsExpHelix1 extends UOBMQueryUtilityTest<PSQLTestData> {
		private static final PSQLTestData data = PSQLTestData.getStore("jdbc:postgresql://localhost:8996/uobm", "uobm_30", "akement", "passw0rd",
                "db2inst1", false);
			//getStore("jdbc:db2://9.47.202.45:50001/uobm",
			//	"uobm30", "db2inst2","db2admin","db2inst2",false);
		public PSQLUOBM30MPropPathsExpHelix1() {
			super(new PSQLEngine(),"../rdfstore-data/uobm_queries_proppaths_expansion/", data, 
					answers_proppaths_expansion);
		}
		@Test 
		public void testQ15() {
			test(14);
		}
		@Test 
		public void testQ16() {
			test(15);
		}
		@Test 
		public void testQ17() {
			test(16);
		}
		/*@Test 
		public void testQ18() {
			test(17);
		}*/
		@Test 
		public void testQ19() {
			test(18);
		}
		@Test 
		public void testQ20() {
			//test(19);
		}
		@Test 
		public void testQ21() {
			test(20);
		}
		@Test 
		public void testQ22() {
			//test(21);
		}
		@Test 
		public void testQ23() {
			test(22);
		}
		@Test 
		public void testQ24() {
			test(23);
		}
		@Test 
		public void testQ25() {
			test(24);
		}
		@Test 
		public void testQ26() {
			test(25);
		}
		@Test 
		public void testQ27() {
			test(26);
		}
		/*@Test 
		public void testQ28() {
			test(27);
		}*/
	}
	public static class DB2UOBM30MALLPropPathsExpVM9_47_202_45  extends DB2UOBM30MPropPathsExpVM9_47_202_45 {
		@Test 
		public void testQ10() {
			test(9);
		}
		@Test 
		public void testQ14() {
			test(13);
		}
		@Test 
		public void testQ18() {
			test(17);
		}
		@Test 
		public void testQ28() {
			test(27);
		}
	}
	public static class DB2UOBM1MPropPathsExpVM9_47_202_45 extends AbstractDB2UOBM30MPropPathsExpVM9_47_202_45 {
		private static final DB2TestData data = getStore("jdbc:db2://9.47.202.45:50001/uobm",
				"uobm1u", "db2inst2","db2admin","db2inst2",false);
		public DB2UOBM1MPropPathsExpVM9_47_202_45() {
			super(new DB2Engine(),"../rdfstore-data/uobm_queries_proppaths_expansion/", data, 
					answers_proppaths_expansion_uobm1);
		}
		
	}
	public static class DB2UOBM30MPropPathsExpVM9_47_202_45 extends AbstractDB2UOBM30MPropPathsExpVM9_47_202_45 {
		public DB2UOBM30MPropPathsExpVM9_47_202_45() {
			super();
		}
	}
	public static abstract class AbstractDB2UOBM30MPropPathsExpVM9_47_202_45 extends UOBMQueryUtilityTest<DB2TestData> {
		private static final DB2TestData data = getStore("jdbc:db2://9.47.202.45:50001/uobm",
				"uobm30", "db2inst2","db2admin","db2inst2",false);
		public AbstractDB2UOBM30MPropPathsExpVM9_47_202_45() {
			this(new DB2Engine(),"../rdfstore-data/uobm_queries_proppaths_expansion/", data, 
					answers_proppaths_expansion);
		}
		
		public AbstractDB2UOBM30MPropPathsExpVM9_47_202_45(
				com.ibm.rdf.store.sparql11.TestRunner.DatabaseEngine<com.ibm.rdf.store.sparql11.TestRunner.DB2TestData> engine,
				String queryDir,
				com.ibm.rdf.store.sparql11.TestRunner.DB2TestData data,
				int[] answers) {
			super(engine, queryDir, data, answers);
		}

		@Test 
		public void testQ15() {
			test(14);
		}
		@Test 
		public void testQ16() {
			test(15);
		}
		@Test 
		public void testQ17() {
			test(16);
		}
		/*@Test 
		public void testQ18() {
			test(17);
		}*/
		@Test 
		public void testQ19() {
			test(18);
		}
		@Test 
		public void testQ20() {
			//test(19);
		}
		@Test 
		public void testQ21() {
			test(20);
		}
		@Test 
		public void testQ22() {
			//test(21);
		}
		@Test 
		public void testQ23() {
			test(22);
		}
		@Test 
		public void testQ24() {
			test(23);
		}
		@Test 
		public void testQ25() {
			test(24);
		}
		@Test 
		public void testQ26() {
			test(25);
		}
		@Test 
		public void testQ27() {
			test(26);
		}
		/*@Test 
		public void testQ28() {
			test(27);
		}*/
	}
	
	/*public static class UOBM30_GreedySub extends UOBMQueryUtilityTest<DB2TestData> {
		private static final DB2TestData data = getStore("jdbc:db2://9.47.202.45:50001/uobm",
				"uobm30", "db2inst2","db2admin","db2inst2",false);
		public UOBM30_GreedySub() {
			super(new DB2Engine(), "../OWLQLTestcases/UOBM/sparql_queries_greedysub/", data);
		}
	}*/
	
	@Test 
	public void testQ1() {
		test(0);
	}

	@Test 
	public void testQ2() {
		test(1);
	}
	@Test 
	public void testQ3() {
		test(2);
	}
	@Test 
	public void testQ4() {
		test(3);
	}
	@Test 
	public void testQ5() {
		test(4);
	}
	@Test 
	public void testQ6() {
		test(5);
	}
	@Test 
	public void testQ7() {
		test(6);
	}
	@Test 
	public void testQ8() {
		test(7);
	}
	@Test 
	public void testQ9() {
		test(8);
	}
	/*@Test 
	public void testQ10() {
		test(9);
	}*/
	@Test 
	public void testQ11() {
		test(10);
	}
	@Test 
	public void testQ12() {
		test(11);
	}
	@Test 
	public void testQ13() {
		test(12);
	}
	/*@Test 
	public void testQ14() {
		test(13);
	}*/
	/*private boolean check(int numberOfAnswers, int queryIndex) {
		System.err.println("got " + numberOfAnswers + " answers");
		boolean ret = answers == null || answers[ queryIndex ] == -1 || numberOfAnswers == answers[ queryIndex ];
		if (!ret) {
			System.err.println("expected " + answers[ queryIndex ] + " answers");
		}
		return ret;
	}*/
	
	protected void test(int queryIndex) {
		String file = queryDir + (queryIndex+1)+".ql.sparql";
		System.err.println("Testing:" + file);
		executeQuery(file, queryIndex);
		 
	}
}
