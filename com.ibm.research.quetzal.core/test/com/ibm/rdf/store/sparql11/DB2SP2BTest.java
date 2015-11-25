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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ibm.rdf.store.testing.RandomizedRepeat;
import com.ibm.research.rdf.store.Context;
import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.StoreManager;
import com.ibm.research.rdf.store.Store.Backend;
import com.ibm.research.rdf.store.query.QueryProcessor;
import com.ibm.research.rdf.store.query.QueryProcessorFactory;
import com.ibm.research.rdf.store.runtime.service.types.LiteralInfoResultSet;
import com.ibm.research.rdf.store.sparql11.SparqlParserUtilities;
import com.ibm.research.rdf.store.sparql11.model.Query;

@RunWith(com.ibm.rdf.store.testing.RandomizedRepeatRunner.class)
@RandomizedRepeat(8) 
public class DB2SP2BTest
   {
   static Connection conn = null;
   static Store store = null;

   static final int[] sp2b5MAnswers = new int[]{1, 248738, 192373, 1317, 0, 18362955, 210662, 210662, 417625, 1200, 493, 4, 656, 10, 1, 1, 0};
   private final static int[] sp2b100MAnswers = new int[]
            { 1, 9050604, 1466402, 10143, 0, -1, 2016996, 2016996, 9812030, 14645,
                  493, 4, 656, 10, 1, 1, 0 };
   
   private int[] answers = sp2b100MAnswers;;
   private String datasetName = "sp2b100m_r";
   private String jdbcUri = "jdbc:db2://localhost:50000/testrev";
   private String passwd = "ihaterc2";
   private String username = "db2inst1";
   private String schemaname = "db2inst1";
   private String queryDir = "/rdf_stores/data/sp2b_queries_rev/";
   //private String queryDir = "../rdfstore-data/sp2b_queries_rev/";
   // initialize the store
   @Before public void init()
      {
      try
         {
         Class.forName("com.ibm.db2.jcc.DB2Driver");
         conn = DriverManager.getConnection(jdbcUri, username, passwd);
         store = StoreManager.connectStore(conn, Backend.valueOf("db2"), schemaname, datasetName, Context.defaultContext);

         }
      catch (ClassNotFoundException e)
         {
         e.printStackTrace();
         }
      catch (SQLException e)
         {
         e.printStackTrace();
         }
      }

   private int executeQuery(String file)
      {
      return execute(file, false);
      }

   private int execute(String file, boolean isDescribeWithoutRows)
      {
      int nOR = -1;
      @SuppressWarnings("unchecked") Query q = SparqlParserUtilities
            .parseSparqlFile(file, Collections.EMPTY_MAP);
      QueryProcessor qp = QueryProcessorFactory.create(file, conn, store, store.getContext());
      LiteralInfoResultSet rs = null;
      long time = System.currentTimeMillis();

      //
      // If this is a select query
      //
      if (q.isSelectQuery())
         {
         rs = qp.execSelect();
         nOR = printResult(rs.getResultSet());
         System.out.println(file + " has : " + nOR + " rows");
         }

      //
      // if this is a describe query
      //
      if (q.isDescribeQuery())
         {
         rs = qp.execDescribe();
         if (isDescribeWithoutRows)
            {
            nOR = 0;
            }
         else
            {
            nOR = printResult(rs.getResultSet());
            }
         }

      //
      // if this is an ask query
      //
      if (q.isAskQuery())
         {
         boolean askResult = qp.execAsk();
         System.out.println(file + " returns " + askResult);
         if (askResult)
            {
            nOR = 1;
            }
         else
            {
            nOR = 0;
            }
         }
      System.out.println("Query: " + file + " time:"
            + (System.currentTimeMillis() - time));

      return nOR;

      }

   private boolean check(int numberOfAnswers, int queryIndex)
      {
      System.err.println("got " + numberOfAnswers + " answers");
      return answers == null || answers[queryIndex] == -1
            || numberOfAnswers == answers[queryIndex];
      }

   @Test
   public void testQueryQ1() throws Exception
      {
      String file = queryDir + "q1.sparql";
      System.err.println("Testing:" + file);
      Assert.assertTrue(check(executeQuery(file), 0));
      }

   @Test
   public void testQueryQ2() throws Exception
      {
      String file = queryDir + "q2.sparql";
      System.err.println("Testing:" + file);
      Assert.assertTrue(check(executeQuery(file), 1));
      }

   @Test
   public void testQueryQ3a() throws Exception
      {
      String file = queryDir + "q3a.sparql";
      System.err.println("Testing:" + file);
      Assert.assertTrue(check(executeQuery(file), 2));
      }

   @Test
   public void testQueryQ3b() throws Exception
      {
      String file = queryDir + "q3b.sparql";
      System.err.println("Testing:" + file);
      Assert.assertTrue(check(executeQuery(file), 3));
      }

   @Test
   public void testQueryQ3c() throws Exception
      {
      String file = queryDir + "q3c.sparql";
      System.err.println("Testing:" + file);
      Assert.assertTrue(check(executeQuery(file), 4));
      }

   
//	@Test
//	public void testQueryQ4() throws Exception {
//		String file = queryDir + "q4.sparql";
//		System.err.println("Testing:" + file);
//		Assert.assertTrue(check(executeQuery(file), 5));
//	}

   @Test
   public void testQueryQ5a() throws Exception
      {
      String file = queryDir + "q5a.sparql";
      System.err.println("Testing:" + file);
      Assert.assertTrue(check(executeQuery(file), 6));
      }

   @Test
   public void testQueryQ5b() throws Exception
      {
      String file = queryDir + "q5b.sparql";
      System.err.println("Testing:" + file);
      Assert.assertTrue(check(executeQuery(file), 7));
      }

    @Test
    public void testQueryQ6() throws Exception {
    String file = queryDir + "q6.sparql";
    System.err.println("Testing:" + file);
    Assert.assertTrue(check(executeQuery(file), 8));
    }

    @Test 
    public void testQueryQ7() throws Exception {
    String file = queryDir + "sp2b_q7_rev.sparql";
    System.err.println("Testing:" + file);
    Assert.assertTrue(check(executeQuery(file), 9));
    }

   @Test 
   public void testQueryQ8() throws Exception
      {
      String file = queryDir + "q8.sparql";
      System.err.println("Testing:" + file);
      Assert.assertTrue(check(executeQuery(file), 10));
      }

   @Test 
   public void testQueryQ9() throws Exception
      {
      String file = queryDir + "q9.sparql";
      System.err.println("Testing:" + file);
      Assert.assertTrue(check(executeQuery(file), 11));
      }

   @Test
   public void testQueryQ10() throws Exception
      {
      String file = queryDir + "q10.sparql";
      System.err.println("Testing:" + file);
      Assert.assertTrue(check(executeQuery(file), 12));
      }

   @Test 
   public void testQueryQ11() throws Exception
      {
      String file = queryDir + "q11.sparql";
      System.err.println("Testing:" + file);
      Assert.assertTrue(check(executeQuery(file), 13));
      }

   @Test 
   public void testQueryQ12a() throws Exception
      {
      String file = queryDir + "q12a.sparql";
      System.err.println("Testing:" + file);
      Assert.assertTrue(check(executeQuery(file), 14));
      }

   @Test
   public void testQueryQ12b() throws Exception
      {
      String file = queryDir + "q12b.sparql";
      System.err.println("Testing:" + file);
      Assert.assertTrue(check(executeQuery(file), 15));
      }

   @Test
   public void testQueryQ12c() throws Exception
      {
      String file = queryDir + "q12c.sparql";
      System.err.println("Testing:" + file);
      Assert.assertTrue(check(executeQuery(file), 16));
      }

   private static int printResult(/* String sparqlQuery, */ResultSet rst)
      {
      // System.out.println("QUERY: "+sparqlQuery);
      // returns the number of rows returned

//      System.out.println("RESULT: ");

      // int i;
      int numberOfRows = 0;
      try
         {
//         ResultSetMetaData rsmd = rst.getMetaData();
//         int numberOfColumns = rsmd.getColumnCount();
//         StringBuffer resultHeader = new StringBuffer();
//         for (int nCols = 1; nCols <= numberOfColumns; nCols++)
//            {
//            resultHeader.append(rsmd.getColumnLabel(nCols));
//            resultHeader.append("|||");
//            }
//         System.out
//               .println("============================================================");
//         System.out.println(resultHeader.toString());
//         System.out
//               .println("============================================================");
//         String[] row = new String[numberOfColumns];
         while (rst.next())
            {
            numberOfRows++;
            //            for (i = 1; i <= numberOfColumns; i++)
            //               row[i - 1] = rst.getString(i);
            // for(i = 1; i <= sz; i++) row.add(rst.getString(i));
            // System.out.println(row);
            }
         rst.close();
         }
      catch (SQLException e)
         {
         // TODO Auto-generated catch block
         e.printStackTrace();
         }
//      System.out
//            .println("============================================================");
      return numberOfRows;
      }
   }
