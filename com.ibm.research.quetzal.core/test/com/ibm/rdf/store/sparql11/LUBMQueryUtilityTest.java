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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.ibm.rdf.store.testing.RandomizedRepeat;
import com.ibm.research.owlql.ruleref.OWLQLSPARQLCompiler;
import com.ibm.research.rdf.store.sparql11.model.FunctionCall;
import com.ibm.wala.util.collections.HashMapFactory;

public class LUBMQueryUtilityTest<D> extends TestRunner<D> {
	private final String queryDir;
	protected final static Logger logger = LoggerFactory
			.getLogger(LUBMQueryUtilityTest.class);

	public LUBMQueryUtilityTest(DatabaseEngine<D> engine, D data,
			int[] ans, String queryDir) {
		super(data, engine, ans);
		this.queryDir = queryDir;
	}

	public static class DockerDB2 extends LUBMQueryUtilityTest<DB2TestData> {
		private static final DB2TestData data = new DB2TestData(
				System.getenv("JDBC_URL"), System.getenv("KB"),
				System.getenv("DB_USER"), System.getenv("DB_PASSWORD"),
				System.getenv("DB_SCHEMA"), false);
		static int[] answers;
		static {
			String kbSize = System.getenv("KB_SIZE");
			
			if (kbSize.equals("100k")) {
				answers = TestConstants.lubm100kAnswers;
			} else if (kbSize.equals("10m")) {
				answers = TestConstants.lubm10mAnswers;
			} else if (kbSize.equals("100m")) {
				answers = TestConstants.lubm100mAnswers;
			} else if (kbSize.equals("1B")) {
				answers = TestConstants.lubm8000uAnswers;
			} 
		}

		public DockerDB2() {
				
			super(new DB2Engine(), data, answers,
					System.getenv("QUERY_DIR"));
		}
	}
	
	public static class DockerPostgresql extends LUBMQueryUtilityTest<PSQLTestData> {
		private static final PSQLTestData data = new PSQLTestData(
				"jdbc:postgresql://" + System.getenv("POSTGRES_PORT_5432_TCP_ADDR") + ":"+ System.getenv("POSTGRES_PORT_5432_TCP_PORT") + "/quetzal", System.getenv("KB"),
				System.getenv("DB_USER"), System.getenv("DB_PASSWORD"),
				System.getenv("DB_SCHEMA"), false);
		
		static int[] answers;
		static {
			String kbSize = System.getenv("KB_SIZE");
			
			if (kbSize.equals("100k")) {
				answers = TestConstants.lubm100kAnswers;
			} else if (kbSize.equals("10m")) {
				answers = TestConstants.lubm10mAnswers;
			} else if (kbSize.equals("100m")) {
				answers = TestConstants.lubm100mAnswers;
			} else if (kbSize.equals("1B")) {
				answers = TestConstants.lubm8000uAnswers;
			}
		}

		public DockerPostgresql() {
			super(new PSQLEngine(), data, answers,
					System.getenv("QUERY_BASE"));
		}
	}
	
	public static class DockerShark extends LUBMQueryUtilityTest<SharkTestData> {
		private static final SharkTestData data = new SharkTestData(
				System.getenv("JDBC_URL"), System.getenv("KB"),
				System.getenv("DB_USER"), System.getenv("DB_PASSWORD"),
				System.getenv("DB_SCHEMA"), false);
		static int[] answers;
		static {
			String kbSize = System.getenv("KB_SIZE");
			
			if (kbSize.equals("100k")) {
				answers = TestConstants.lubm100kAnswers;
			} else if (kbSize.equals("10m")) {
				answers = TestConstants.lubm10mAnswers;
			} else if (kbSize.equals("100m")) {
				answers = TestConstants.lubm100mAnswers;
			} else if (kbSize.equals("1B")) {
				answers = TestConstants.lubm8000uAnswers;
			}
		}

		public DockerShark() {
				
			super(new SharkEngine(), data, answers,
					System.getenv("QUERY_DIR"));
		}
	}

	public static class DB2LUBM10MHelix1 extends
			LUBMQueryUtilityTest<DB2TestData> {
		private static final DB2TestData data = new DB2TestData(
				"jdbc:db2://helix1.pok.ibm.com:50001/lubm10m", "lubm10m",
				"db2inst1", "db2inst1", "db2inst2", false);

		public DB2LUBM10MHelix1() {
			super(new DB2Engine(), data,
					TestConstants.lubm10mAnswers,
					"../rdfstore-data/lubm_queries_QL_reversed_proppath/");
		}
	}

	// private static final SharkTestData data =
	// SharkTestData.getStore("jdbc:hive2://9.12.196.243:10000/default",
	// "uobm1a", "root", "nkoutche",
	// "default", false);
	public static class SharkLUBM100KOnVM_9_51_154_25 extends
			LUBMQueryUtilityTest<SharkTestData> {
		private static final SharkTestData data = new SharkTestData(
				"jdbc:hive2://9.51.154.25:10000/default", "lubm100k", "root",
				"KX2ETEp6", "default", false);
		
		public SharkLUBM100KOnVM_9_51_154_25() {
			super(new SharkEngine(), data,
					TestConstants.lubm100kAnswers,
					"/Users/dolby/RdfStoreGitWorkspace/rdfstore-data/lubm_queries_QL_reversed/");
		}
	}
		
	public static class DB2LUBM100KHelix1 extends
			LUBMQueryUtilityTest<DB2TestData> {
		private static final DB2TestData data = new DB2TestData(
				"jdbc:db2://helix1.pok.ibm.com:50001/lubm100k", "lubm100k",
				"db2inst1", "db2inst1", "db2inst1", false);

		public DB2LUBM100KHelix1() {
			super(new DB2Engine(), data,
					TestConstants.lubm100kAnswers,
					"../rdfstore-data/lubm_queries_QL_reversed/");
		}
	}

	public static class PSQLLUBM10MHelix1 extends
			LUBMQueryUtilityTest<PSQLTestData> {
		private static final PSQLTestData data = new PSQLTestData(
				"jdbc:postgresql://helix1.pok.ibm.com:24973/lubm10m",
				"lubm10m", "akement", "passw0rd", "db2inst2", false);

		public PSQLLUBM10MHelix1() {
			super(new PSQLEngine(), data,
					TestConstants.lubm10mAnswers,
					"../rdfstore-data/lubm_queries_QL_reversed/");
		}
	}

	@RunWith(com.ibm.rdf.store.testing.RandomizedRepeatRunner.class)
	@RandomizedRepeat(8)
	public static class PSQLLUBM100MHelix1 extends
			LUBMQueryUtilityTest<PSQLTestData> {
		private static final PSQLTestData data = new PSQLTestData(
				"jdbc:postgresql://helix1.pok.ibm.com:5432/lubm100m",
				"lubm100m", "akement", "passw0rd", "db2inst2", false);

		public PSQLLUBM100MHelix1() {
			super(new PSQLEngine(), data,
					TestConstants.lubm100mAnswers,
					"../rdfstore-data/lubm_queries_QL_reversed/");
		}
	}

	//
	// connect to SL to run this config:
	// ssh -v -L 9999:localhost:5432 kavitha@174.37.245.155
	//
	public static class PSQLLUBM100MSL extends
			LUBMQueryUtilityTest<PSQLTestData> {
		private static final PSQLTestData data = new PSQLTestData(
				"jdbc:postgresql://localhost:9999/lubm", "l100m", "kavitha",
				"sherlives", "db2inst1", false);

		public PSQLLUBM100MSL() {
			super(new PSQLEngine(), data, TestConstants.lubm100mAnswers, System.getProperty("rdfstore_data")
							+ "/lubm_queries_QL_reversed/");
		}
	}

	// @RunWith(com.ibm.research.rdf.store.testing.RandomizedRepeatRunner.class)
	// @RandomizedRepeat(1)
	public static class PSQLLUBMPropPathHelix1 extends
			LUBMQueryUtilityTest<PSQLTestData> {
		private static final PSQLTestData data = new PSQLTestData(
				"jdbc:postgresql://helix1.pok.ibm.com:24973/lubm100m",
				"lubm100m", "akement", "passw0rd", "db2inst2", false);

		public PSQLLUBMPropPathHelix1() {
			super(new PSQLEngine(), data,
					TestConstants.lubm100mAnswers,
					"../rdfstore-data/lubm_queries_QL_reversed_proppath/");
		}

		/*
		 * @Test public void testQueryQ1() throws Exception { //Too slow to
		 * include in the regression test at this point. }
		 */
	}

	public static class PSQLLUBMCastTestHelix1 extends
			LUBMQueryUtilityTest<PSQLTestData> {
		private static final PSQLTestData data = new PSQLTestData(
				"jdbc:postgresql://helix1.pok.ibm.com:24973/lubm100m",
				"lubm100m", "akement", "passw0rd", "db2inst2", false);
		private static String queryDir = "../rdfstore-data/uobm_queries_proppaths/";

		public PSQLLUBMCastTestHelix1() {
			super(new PSQLEngine(), data, new int[] { -2 }, queryDir);
		}

		@Test
		public void testQueryQ1() throws Exception {

			String file = queryDir + "22.ql.sparql";
			System.err.println("Testing:" + file);
			executeQuery(file, 0);
		}

		@Test
		public void testQueryQ2() throws Exception {

		}

		@Test
		public void testQueryQ3() throws Exception {

		}

		@Test
		public void testQueryQ4() throws Exception {

		}

		@Test
		public void testQueryQ5() throws Exception {

		}

		@Test
		public void testQueryQ6() throws Exception {

		}

		@Test
		public void testQueryQ7() throws Exception {

		}

		@Test
		public void testQueryQ8() throws Exception {

		}

		@Test
		public void testQueryQ9() throws Exception {

		}

		@Test
		public void testQueryQ10() throws Exception {

		}

		@Test
		public void testQueryQ13() throws Exception {

		}

		@Test
		public void testQueryQ14() throws Exception {

		}

	}

	// @RunWith(com.ibm.research.rdf.store.testing.RandomizedRepeatRunner.class)
	// @RandomizedRepeat(1)
	public static class DB2LUBMHelix1 extends LUBMQueryUtilityTest<DB2TestData> {
		private static final DB2TestData data = new DB2TestData(
				"jdbc:db2://helix1.pok.ibm.com:50001/lubm", "lubm_100m_r",
				"db2inst1", "db2admin", "db2inst1", false);

		public DB2LUBMHelix1() {
			super(new DB2Engine(), data,
					TestConstants.lubm100mAnswers,
					System.getProperty("dataDir") + "/lubm_queries_QL_reversed/");
		}
	}

	// @RunWith(com.ibm.research.rdf.store.testing.RandomizedRepeatRunner.class)
	// @RandomizedRepeat(1)
	public static class DB2LUBMPropPathHelix1 extends
			LUBMQueryUtilityTest<DB2TestData> {
		private static final DB2TestData data = new DB2TestData(
				"jdbc:db2://helix1.pok.ibm.com:50001/lubm100m", "lubm100m",
				"db2inst1", "db2inst1", "db2inst2", false);

		public DB2LUBMPropPathHelix1() {
			super(new DB2Engine(), data,
					TestConstants.lubm100mAnswers,
					"../rdfstore-data/lubm_queries_QL_reversed_proppath/");
		}
	}

	// @RunWith(com.ibm.research.rdf.store.testing.RandomizedRepeatRunner.class)
	// @RandomizedRepeat(1)
	public static class DB2LUBM10MHelix2 extends
			LUBMQueryUtilityTest<DB2TestData> {
		private static final DB2TestData data = new DB2TestData(
				"jdbc:db2://helix2.pok.ibm.com:50001/lubm", "lubm10m",
				"db2inst1", "db2inst1", "db2inst2", false);

		public DB2LUBM10MHelix2() {
			super(new DB2Engine(), data,
					TestConstants.lubm10mAnswers,
					"../rdfstore-data/lubm_queries_QL_reversed/");
		}
	}

	@RunWith(com.ibm.rdf.store.testing.RandomizedRepeatRunner.class)
	@RandomizedRepeat(1)
	public static class DB2LUBM100MHelix1 extends
			LUBMQueryUtilityTest<DB2TestData> {
		private static final DB2TestData data = new DB2TestData(
				"jdbc:db2://helix1.pok.ibm.com:50001/lubm", "lubm_100m_r",
				"db2inst1", "db2admin", "db2inst1", false);

		public DB2LUBM100MHelix1() {
			super(new DB2Engine(), data,
					TestConstants.lubm100mAnswers,
					"/Users/ksrinivs/Documents/workspace/rdfstore-data/lubm_queries_QL_reversed/");
		}
	}

	@RunWith(com.ibm.rdf.store.testing.RandomizedRepeatRunner.class)
	@RandomizedRepeat(8)
	public static class Reversed100MSL extends
			LUBMQueryUtilityTest<DB2TestData> {
		private static final DB2TestData data = new DB2TestData(
				"jdbc:db2://10.80.29.218:50000/lubm", "lubm100m", "db2inst1",
				"db2admin", "db2inst1", false);

		public Reversed100MSL() {
			super(new DB2Engine(), data,
					TestConstants.lubm100mAnswers,
					"../rdfstore-data/lubm_queries_QL_reversed/");
		}
	}

	// @RunWith(com.ibm.research.rdf.store.testing.RandomizedRepeatRunner.class)
	// @RandomizedRepeat(1)
	public static class DB2LUBM100M_SL extends
			LUBMQueryUtilityTest<DB2TestData> {
		private static final DB2TestData data = new DB2TestData(
				"jdbc:db2://localhost:50002/lubm", "lubm_100m_r", "db2inst1",
				"db2admin", "db2inst1", false);

		public DB2LUBM100M_SL() {
			super(new DB2Engine(), data,
					TestConstants.lubm100mAnswers,
					"/Users/dolby/RdfStoreGitWorkspace/rdfstore-data/lubm_queries_QL_reversed/");
		}
	}

	// @RunWith(com.ibm.research.rdf.store.testing.RandomizedRepeatRunner.class)
	// @RandomizedRepeat(1)
	public static class DB2LUBM10MRC2 extends LUBMQueryUtilityTest<DB2TestData> {
		private static final DB2TestData data = new DB2TestData(
				"jdbc:db2://9.47.202.45:50001/lubm", "lubm10m", "db2inst2",
				"db2admin", "db2inst2", false);

		public DB2LUBM10MRC2() {
			super(new DB2Engine(), data,
					TestConstants.lubm10mAnswers,
					"../rdfstore-data/lubm_queries_QL_reversed/");
		}
	}

	// @RunWith(com.ibm.research.rdf.store.testing.RandomizedRepeatRunner.class)
	// @RandomizedRepeat(1)
	public static class Reversed100M_WithOWLQLCompilation extends
			LUBMQueryUtilityTest<DB2TestData> {
		private static final DB2TestData data = new DB2TestData(
				"jdbc:db2://9.47.202.45:50001/lubm", "lubm100m", "db2inst2",
				"db2admin", "db2inst2", false);
		private static final File tbox = new File(
				"../rdfstore-data/lubm_queries_original/univ-bench.owl");

		private static final OWLQLSPARQLCompiler compiler;
		static {
			try {
				compiler = new OWLQLSPARQLCompiler(null, null,
						tbox.getAbsolutePath()) {

					@Override
					public Query compile(Query query) {
						try {
							String revQuery = reverse(query.toString());
							System.out.println("Query reversed: " + revQuery);
							Query revQ = QueryFactory.create(revQuery);
							Query ret = QueryFactory.create(reverse(super
									.compile(revQ).toString()));
							System.out.println("Compilation result: " + ret);
							return ret;
						} catch (IOException e) {
							throw new RuntimeException(e);
						} catch (InterruptedException e) {
							throw new RuntimeException(e);
						}
					}

				};
			} catch (OWLOntologyCreationException e) {
				throw new RuntimeException(e);
			}
		}

		public Reversed100M_WithOWLQLCompilation() {
			super(new DB2Engine(compiler), data,
					TestConstants.lubm100mAnswers,
					"../rdfstore-data/lubm_queries_original_rev/");
		}

		private static String primReverse(String query) throws IOException,
				InterruptedException {
			File tmp = File.createTempFile("tmp", "sparql");
			BufferedWriter out = new BufferedWriter(new FileWriter(tmp));
			out.append(query);
			out.close();
			File workingDir = new File("scripts/");
			String command = "awk -f reverse_uris.awk -f strings.awk -f parse.awk "
					+ tmp;
			final StringBuffer buf = new StringBuffer();
			// logger.info("Base dir: {}", workingDir.getAbsolutePath());
			assert workingDir.exists() : workingDir.getAbsolutePath();
			final Process proc = Runtime.getRuntime().exec(command, null,
					workingDir);
			Thread t = new Thread() {

				@Override
				public void run() {
					try {
						BufferedReader in = new BufferedReader(
								new InputStreamReader(proc.getInputStream()));
						String line;
						while ((line = in.readLine()) != null) {
							buf.append(line + "\n");
							// System.out.println(line+"\n");
						}
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			};
			t.start();
			Thread errorT = new Thread() {

				@Override
				public void run() {
					try {
						BufferedReader in = new BufferedReader(
								new InputStreamReader(proc.getErrorStream()));
						String line;
						while ((line = in.readLine()) != null) {
							logger.error(line + "\n");
						}
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			};
			errorT.start();
			proc.waitFor();
			t.join();
			errorT.join();
			String ret = buf.toString();
			return ret;
		}

		private static Map<String, String> fn2Reverse = HashMapFactory.make();

		protected static String reverse(String query) throws IOException,
				InterruptedException {
			String ret = primReverse(query);
			String[] functions = new String[] { FunctionCall.STARTS_WITH,
					FunctionCall.ENDS_WITH, FunctionCall.XSD_DATETIME,
					FunctionCall.XSD_BOOLEAN, FunctionCall.XSD_DOUBLE,
					FunctionCall.XSD_FLOAT, FunctionCall.XSD_DECIMAL,
					FunctionCall.XSD_INTEGER, FunctionCall.XSD_STRING };
			for (String fn : functions) {
				String reverse = fn2Reverse.get(fn);
				if (reverse == null) {
					char[] reverseChars = new char[fn.length()];
					for (int i = 0; i < fn.length(); i++) {
						reverseChars[i] = fn.charAt(fn.length() - 1 - i);
					}
					reverse = new String(reverseChars);
					fn2Reverse.put(fn, reverse);
				}
				ret = ret.replace("<" + reverse + ">", "<" + fn + ">");
			}
			return ret;
		}

	}

	// @RunWith(com.ibm.research.rdf.store.testing.RandomizedRepeatRunner.class)
	// @RandomizedRepeat(8)
	// public static class Reversed100K extends LUBMQueryUtilityTest {
	// private static final TestRunnerData data =
	// getStore("jdbc:db2://helix1.pok.ibm.com:50001/lubm100K", "lubm100k",
	// "db2inst1", "db2inst1",
	// "db2inst1", false);
	//
	// public Reversed100K() {
	// super(data, "../rdfstore-data/lubm_queries_QL_reversed/",
	// lubm10mAnswers);
	// }
	// }
	// @RunWith(com.ibm.research.rdf.store.testing.RandomizedRepeatRunner.class)
	// @RandomizedRepeat(8)
	// public static class Reversed100M_RC2_OLDDB2 extends LUBMQueryUtilityTest
	// {
	// private static final TestRunnerData data =
	// getStore("jdbc:db2://9.47.205.206:50000/test", "lubm100m", "db2inst1",
	// "db2admin", "db2inst1",
	// false);
	//
	// public Reversed100M_RC2_OLDDB2() {
	// super(data, "../rdfstore-data/lubm_queries_QL_reversed/",
	// lubm100mAnswers);
	// }
	// }

	// @RunWith(com.ibm.research.rdf.store.testing.RandomizedRepeatRunner.class)
	// @RandomizedRepeat(8)
	// public static class XIV8000U extends LUBMQueryUtilityTest {
	// private static final TestRunnerData data =
	// getStore("jdbc:db2://localhost:50000/xivdb", "lubm8000u", "db2inst1",
	// "ihaterc2", "db2inst1", false);
	//
	// public XIV8000U() {
	// super(data, "../rdfstore-data/lubm_queries_QL_reversed_rel/",
	// lubm8000uAnswers);
	// }
	// }

	// @RunWith(com.ibm.research.rdf.store.testing.RandomizedRepeatRunner.class)
	// @RandomizedRepeat(8)
	// public static class TMS8000U extends LUBMQueryUtilityTest {
	// private static final TestRunnerData data =
	// getStore("jdbc:db2://localhost:50000/tmsdb", "lubm8000u", "db2inst1",
	// "ihaterc2", "db2inst1", false);
	//
	// public TMS8000U() {
	// super(data, "../rdfstore-data/lubm_queries_QL_reversed_rel/",
	// lubm8000uAnswers);
	// }
	// }

	@Test
	public void testQueryQ1() throws Exception {
		String file = queryDir + "q1.sparql";
		System.err.println("Testing:" + file);
		executeQuery(file, 0);
	}

	@Test
	public void testQueryQ2() throws Exception {
		String file = queryDir + "q2.sparql";
		System.err.println("Testing:" + file);
		executeQuery(file, 1);
	}

	@Test
	public void testQueryQ3() throws Exception {
		String file = queryDir + "q3.sparql";
		System.err.println("Testing:" + file);
		executeQuery(file, 2);
	}

	@Test
	public void testQueryQ4() throws Exception {
		String file = queryDir + "q4.sparql";
		System.err.println("Testing:" + file);
		executeQuery(file, 3);
	}

	@Test
	public void testQueryQ5() throws Exception {
		String file = queryDir + "q5.sparql";
		System.err.println("Testing:" + file);
		executeQuery(file, 4);
	}

	@Test
	public void testQueryQ6() throws Exception {
		String file = queryDir + "q6.sparql";
		System.err.println("Testing:" + file);
		executeQuery(file, 5);
	}

	@Test
	public void testQueryQ7() throws Exception {
		String file = queryDir + "q7.sparql";
		System.err.println("Testing:" + file);
		executeQuery(file, 6);
	}

	@Test
	public void testQueryQ8() throws Exception {
		String file = queryDir + "q8.sparql";
		System.err.println("Testing:" + file);
		executeQuery(file, 7);
	}

	@Test
	public void testQueryQ9() throws Exception {
		String file = queryDir + "q9.sparql";
		System.err.println("Testing:" + file);
		executeQuery(file, 8);
	}

	@Test
	public void testQueryQ10() throws Exception {
		String file = queryDir + "q10.sparql";
		System.err.println("Testing:" + file);
		executeQuery(file, 9);
	}

	@Test
	public void testQueryQ13() throws Exception {
		String file = queryDir + "q13.sparql";
		System.err.println("Testing:" + file);
		executeQuery(file, 12);
	}

	@Test
	public void testQueryQ14() throws Exception {
		String file = queryDir + "q14.sparql";
		System.err.println("Testing:" + file);
		executeQuery(file, 13);
	}

}
