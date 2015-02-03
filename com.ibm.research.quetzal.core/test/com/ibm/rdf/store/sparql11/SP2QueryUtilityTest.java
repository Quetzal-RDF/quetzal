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


//@RunWith(com.ibm.research.rdf.store.testing.RandomizedRepeatRunner.class)
//@RandomizedRepeat(1)
public class SP2QueryUtilityTest<D> extends TestRunner<D>
   {
   private final String queryDir;

   public SP2QueryUtilityTest(DatabaseEngine<D> engine, D data, int[] answers, String queryDir)
      {
      super(data, engine, answers);
      this.queryDir = queryDir;
      }

   public static class DockerDB2 extends SP2QueryUtilityTest<DB2TestData> {
		private static final DB2TestData data = new DB2TestData(
				System.getenv("JDBC_URL"), System.getenv("KB"),
				System.getenv("DB_USER"), System.getenv("DB_PASSWORD"),
				System.getenv("DB_SCHEMA"), false);
		static int[] answers;
		static {
			String kbSize = System.getenv("KB_SIZE");
			
			if (kbSize.equals("1m")) {
				answers = TestConstants.sp2b1MAnswers;
			} else if (kbSize.equals("10m")) {
				answers = TestConstants.sp2b10MAnswers;
			} else if (kbSize.equals("100m")) {
				answers = TestConstants.sp2b100MAnswers;
			} 
		}

		public DockerDB2() {
				
			super(new DB2Engine(), data, answers, System.getenv("QUERY_DIR"));
		}
	}
	
	public static class DockerPostgresql extends SP2QueryUtilityTest<PSQLTestData> {
		private static final PSQLTestData data = new PSQLTestData(
				System.getenv("JDBC_URL"), System.getenv("KB"),
				System.getenv("DB_USER"), System.getenv("DB_PASSWORD"),
				System.getenv("DB_SCHEMA"), false);
		static int[] answers;
		static {
			String kbSize = System.getenv("KB_SIZE");
			
			if (kbSize.equals("1m")) {
				answers = TestConstants.sp2b1MAnswers;
			} else if (kbSize.equals("10m")) {
				answers = TestConstants.sp2b10MAnswers;
			} else if (kbSize.equals("100m")) {
				answers = TestConstants.sp2b100MAnswers;
			} 
		}

		public DockerPostgresql() {
				
			super(new PSQLEngine(), data, answers, System.getenv("QUERY_DIR"));
		}
	}
	
	public static class DockerShark extends SP2QueryUtilityTest<SharkTestData> {
		private static final SharkTestData data = new SharkTestData(
				System.getenv("JDBC_URL"), System.getenv("KB"),
				System.getenv("DB_USER"), System.getenv("DB_PASSWORD"),
				System.getenv("DB_SCHEMA"), false);
		static int[] answers;
		static {
			String kbSize = System.getenv("KB_SIZE");
			if (kbSize.equals("1m")) {
				answers = TestConstants.sp2b1MAnswers;
			} else if (kbSize.equals("10m")) {
				answers = TestConstants.sp2b10MAnswers;
			} else if (kbSize.equals("100m")) {
				answers = TestConstants.sp2b100MAnswers;
			} 
		}

		public DockerShark() {
				
			super(new SharkEngine(), data, answers, System.getenv("QUERY_DIR"));
		}
	}
   public static class DB2SP2B100MHelix1 extends SP2QueryUtilityTest<DB2TestData>
      {
      private static final DB2TestData data = new DB2TestData("jdbc:db2://helix1.pok.ibm.com:50001/sp2b", "sp2b_100m_r", "db2inst1",
                                                  "db2admin", "db2inst1", false);

      public DB2SP2B100MHelix1()
         {
         super(new DB2Engine(), data, TestConstants.sp2b100MAnswers, System.getProperty("sp2b.queries.rev"));
         }
      }

   public static class PSQLSP2B10MHelix1 extends SP2QueryUtilityTest<PSQLTestData>
      {
      private static final PSQLTestData data = new PSQLTestData("jdbc:postgresql://helix1.pok.ibm.com:5432/sp2b10m", "sp2b10m", "akement",
                                                  "passw0rd", "db2inst1", false);

      public PSQLSP2B10MHelix1()
         {
         super(new PSQLEngine(), data, TestConstants.sp2b10MAnswers, "../rdfstore-data/sp2b_queries_rev/");
         }
      }

   public static class PSQLSP2B100MHelix1 extends SP2QueryUtilityTest<PSQLTestData>
      {
      private static final PSQLTestData data = new PSQLTestData("jdbc:postgresql://localhost:8996/sp2b", "sp2b_100m", "akement",
                                                  "passw0rd", "db2inst1", false);

      public PSQLSP2B100MHelix1()
         {
         super(new PSQLEngine(), data, TestConstants.sp2b100MAnswers, "/Users/ksrinivs/Documents/workspace/rdfstore-data/sp2b_queries/");
         }
      }

   public static class DB2SP2B1MHelix1 extends SP2QueryUtilityTest<DB2TestData>
   {
   private static final DB2TestData data = new DB2TestData("jdbc:db2://helix1.pok.ibm.com:50001/sp2b", "sp2b1m", "db2inst1",
                                               "db2inst1", "db2inst1", false);

   public DB2SP2B1MHelix1()
      {
      super(new DB2Engine(), data, TestConstants.sp2b1MAnswers, "../rdfstore-data/sp2b_queries_rev/");
      }
   }

   public static class DB2SP2B1M_SLSSD extends SP2QueryUtilityTest<DB2TestData>
   {
   private static final DB2TestData data = new DB2TestData("jdbc:db2://localhost:50002/sp2b", "sp2b_1m_r", "db2inst1",
                                               "db2admin", "db2inst1", false);

   public DB2SP2B1M_SLSSD()
      {
      super(new DB2Engine(), data, TestConstants.sp2b1MAnswers, "/Users/dolby/RdfStoreGitWorkspace/rdfstore-data/sp2b_queries_rev/");
      }
   }

   public static class DB2SP2B100M_SLSSD extends SP2QueryUtilityTest<DB2TestData>
   {
   private static final DB2TestData data = new DB2TestData("jdbc:db2://localhost:50002/sp2b", "sp2b_100m_r", "db2inst1",
                                               "db2admin", "db2inst1", false);

   public DB2SP2B100M_SLSSD()
      {
      super(new DB2Engine(), data, TestConstants.sp2b100MAnswers, "/Users/dolby/RdfStoreGitWorkspace/rdfstore-data/sp2b_queries_rev/");
      }
   }

   public static class SharkSP2B1MVM9_12_196_243 extends SP2QueryUtilityTest<SharkTestData>
      {
	   private static final SharkTestData data =  new SharkTestData("jdbc:hive2://9.12.196.243:10000/default", "sp2b1m", "root", "nkoutche",
    		   "default", false);
 

        public SharkSP2B1MVM9_12_196_243()
         {
         super(new SharkEngine(), data, TestConstants.sp2b1MAnswers, "../rdfstore-data/sp2b_queries_rev/");
         }
      }

   public static class PSQLSP2B1MHelix1 extends SP2QueryUtilityTest<PSQLTestData>
      {
      private static final PSQLTestData data = new PSQLTestData("jdbc:postgresql://helix1.pok.ibm.com:5432/sp2b1m", "sp2b1m", "akement",
                                                  "passw0rd", "db2inst1", false);

      public PSQLSP2B1MHelix1()
         {
         super(new PSQLEngine(), data, TestConstants.sp2b1MAnswers, "../rdfstore-data/sp2b_queries_rev/");
         }
      }

   public static class SP2B10M_R_MIN1 extends SP2QueryUtilityTest<DB2TestData>
      {
      private static final DB2TestData data = new DB2TestData("jdbc:db2://min-1.watson.ibm.com:50001/testrev", "sp2b10m_r", "db2inst1",
                                                  "sheruser", "db2inst1", false);

      public SP2B10M_R_MIN1()
         {
         super(new DB2Engine(), data, null, "../rdfstore-data/sp2b_queries_rev/");
         }
      }

   public static class SP2B10K_R_MIN1 extends SP2QueryUtilityTest<DB2TestData>
      {
      private static final DB2TestData data = new DB2TestData("jdbc:db2://min-1.watson.ibm.com:50001/testrev", "sp2b10k_r", "db2inst1",
                                                  "sheruser", "db2inst1", false);

      public SP2B10K_R_MIN1()
         {
         super(new DB2Engine(), data, null, "../rdfstore-data/sp2b_queries_rev/");
         }
      }

   public static class SP2B100M_SL extends SP2QueryUtilityTest<DB2TestData>
      {
      private static final DB2TestData data = new DB2TestData("jdbc:db2://localhost:9997/lubm", "sp2b100m", "db2inst2", "db2admin",
                                                  "db2inst2", false);

      public SP2B100M_SL()
         {
         super(new DB2Engine(), data, TestConstants.sp2b100MAnswers, "test/sp2b_queries/");
         }
      }

   public static class SP2B100M_R_RC2 extends SP2QueryUtilityTest<DB2TestData>
      {
      private static final DB2TestData data = new DB2TestData("jdbc:db2://9.47.202.45:50001/sp2b", "sp2b100m", "db2inst2", "db2admin",
                                                  "db2inst2", false);

      public SP2B100M_R_RC2()
         {
         super(new DB2Engine(), data, TestConstants.sp2b100MAnswers, "../rdfstore-data/sp2b_queries_rev/");
         }
      }

   public static class SP2B10M_R_RC2 extends SP2QueryUtilityTest<DB2TestData>
      {
      private static final DB2TestData data = new DB2TestData("jdbc:db2://9.47.202.45:50001/sp2b", "sp2b10m", "db2inst2", "db2admin",
                                                  "db2inst2", false);

      public SP2B10M_R_RC2()
         {
         super(new DB2Engine(), data, null, "../rdfstore-data/sp2b_queries_rev/");
         }
      }

   public static class SP2B100K_R_RC2 extends SP2QueryUtilityTest<DB2TestData>
      {
      private static final DB2TestData data = new DB2TestData("jdbc:db2://9.47.202.45:50001/sp2b", "sp2b100k", "db2inst2", "db2admin",
                                                  "db2inst2", false);

      public SP2B100K_R_RC2()
         {
         super(new DB2Engine(), data, null, "../rdfstore-data/sp2b_queries_rev/");
         }
      }

	@Test
   public void testQueryQ1() throws Exception
      {
      String file = queryDir + "q1.sparql";
      System.err.println("Testing:" + file);
      executeQuery(file, 0);
      }

   @Test
   public void testQueryQ2() throws Exception
      {
      String file = queryDir + "q2.sparql";
      System.err.println("Testing:" + file);
      executeQuery(file, 1);
      }

   @Test
   public void testQueryQ3a() throws Exception
      {
      String file = queryDir + "q3a.sparql";
      System.err.println("Testing:" + file);
      executeQuery(file, 2);
      }

   @Test
   public void testQueryQ3b() throws Exception
      {
      String file = queryDir + "q3b.sparql";
      System.err.println("Testing:" + file);
      executeQuery(file, 3);
      }

   @Test
   public void testQueryQ3c() throws Exception
      {
      String file = queryDir + "q3c.sparql";
      System.err.println("Testing:" + file);
      executeQuery(file, 4);
      }

   //@Test
   public void testQueryQ4() throws Exception
      {
      String file = queryDir + "q4.sparql";
      System.err.println("Testing:" + file);
      int result = executeQuery(file);
      executeQuery(file, 5);
      System.out.println(file + " has : " + result + " rows");
      }

   @Test
   public void testQueryQ5a() throws Exception
      {
      String file = queryDir + "q5a.sparql";
      System.err.println("Testing:" + file);
      executeQuery(file, 6);
      }

   @Test
   public void testQueryQ5b() throws Exception
      {
      String file = queryDir + "q5b.sparql";
      System.err.println("Testing:" + file);
      executeQuery(file, 7);
      }

   @Test
   public void testQueryQ6() throws Exception
      {
      String file = queryDir + "q6.sparql";
      System.err.println("Testing:" + file);
      executeQuery(file, 8);
      }

   @Test
   public void testQueryQ7() throws Exception
      {
      String file = queryDir + "q7.sparql";
      System.err.println("Testing:" + file);
      executeQuery(file, 9);
      }

   //@Test
   public void testQueryQ7a() throws Exception
      {
      String file = queryDir + "q7a.sparql";
      System.err.println("Testing:" + file);
      executeQuery(file, -1);
      }

   @Test
   public void testQueryQ8() throws Exception
      {
      String file = queryDir + "q8.sparql";
      System.err.println("Testing:" + file);
      executeQuery(file, 10);
      }

   @Test
   public void testQueryQ9() throws Exception
      {
      String file = queryDir + "q9.sparql";
      System.err.println("Testing:" + file);
      executeQuery(file, 11);
      }

   @Test
   public void testQueryQ10() throws Exception
      {
      String file = queryDir + "q10.sparql";
      System.err.println("Testing:" + file);
      executeQuery(file, 12);
      }

   @Test
   public void testQueryQ11() throws Exception
      {
      String file = queryDir + "q11.sparql";
      System.err.println("Testing:" + file);
      executeQuery(file, 13);
      }

    @Test
   public void testQueryQ12a() throws Exception
      {
      String file = queryDir + "q12a.sparql";
      System.err.println("Testing:" + file);
      executeQuery(file, 14);
      }

   @Test
   public void testQueryQ12b() throws Exception
      {
      String file = queryDir + "q12b.sparql";
      System.err.println("Testing:" + file);
      executeQuery(file, 15);
      }

   @Test
   public void testQueryQ12c() throws Exception
      {
      String file = queryDir + "q12c.sparql";
      System.err.println("Testing:" + file);
      executeQuery(file, 16);
      }
}
