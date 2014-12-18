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

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Properties;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.ibm.research.owlql.ruleref.OWLQLSPARQLCompiler;
import com.ibm.research.rdf.store.Context;
import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.StoreManager;
import com.ibm.research.rdf.store.jena.RdfStoreFactory;
import com.ibm.research.rdf.store.jena.RdfStoreQueryExecutionFactory;
import com.ibm.research.rdf.store.jena.RdfStoreQueryFactory;
import com.ibm.research.rdf.store.query.QueryProcessor;
import com.ibm.research.rdf.store.query.QueryProcessorFactory;
import com.ibm.research.rdf.store.runtime.service.types.LiteralInfoResultSet;
import com.ibm.research.rdf.store.sparql11.SparqlParserUtilities;
import com.ibm.research.rdf.store.sparql11.model.Query;

public class TestRunner<D> {

	public static abstract class TestData {
		final public Connection conn;
		final public Store      store;
		final public Context    ctx;
		final public Dataset    ds;

		protected TestData(Connection conn, Store store, Context ctx, Dataset ds) {
			super();
			this.conn = conn;
			this.store = store;
			this.ctx = ctx;
			this.ds = ds;
		}

		public Connection getConnection() {
			return conn;
		}

		public Store getStore() {
			return store;
		}

		public Context getContext() {
			return ctx;
		}

		public Dataset getDataset() {
			return ds;
		}

	}
	
   //
   // DB2 Test Data
   //
   public static class DB2TestData extends TestData
      {
      public DB2TestData(Connection conn, Store store, Context ctx, Dataset ds)
         {
         super(conn, store, ctx, ds);
         }

      public static DB2TestData getStore(String jdbcurl, String dataset, String username, String password, String schemaName,
            boolean defUnionGraph)
         {
         try
            {
            Connection conn = getConnection(jdbcurl, username, password);
            return getStore(conn, dataset, schemaName, defUnionGraph);
            }
         catch (ClassNotFoundException e)
            {
            e.printStackTrace();
            return null;
            }
         catch (SQLException e)
            {
            e.printStackTrace();
            return null;
            }
         }

      public static Connection
            getConnection(String jdbcurl, String username, String password) throws ClassNotFoundException, SQLException
         {
         Class.forName("com.ibm.db2.jcc.DB2Driver");
         Properties info = new Properties();
         info.setProperty("user", username);
         info.setProperty("password", password);
         info.setProperty("prompt", "false");
         info.setProperty("access", "read only");
         info.setProperty("big decimal", "false");
         info.setProperty("lazy close", "true");
         info.setProperty("block criteria", "2");
         info.setProperty("block size", "1024");
         info.setProperty("data compression", "true");
         Connection conn = DriverManager.getConnection(jdbcurl, info);
         return conn;
         }

      public static DB2TestData getStore(Connection conn, String dataset, String schemaName, boolean defUnionGraph)
         {
         Context ctx = new Context();
         Store store = StoreManager.connectStore(conn, "db2", schemaName, dataset, ctx);
         if (defUnionGraph)
            ctx.set(Context.unionDefaultGraph, Boolean.TRUE);
         Dataset ds = RdfStoreFactory.connectDataset(store, conn, "db2");
         return new DB2TestData(conn, store, ctx, ds);
         }

      }

   //
   // PostgreSQL Test Data
   //
   public static class PSQLTestData extends TestData {
  
      public PSQLTestData(Connection conn, Store store, Context ctx, Dataset ds)
         {
         super(conn, store, ctx, ds);
         }

      public static PSQLTestData getStore(String jdbcurl, String dataset, String username, String password, String schemaName,
            boolean defUnionGraph)
         {
         try
            {
            Connection conn = getConnection(jdbcurl, username, password);
            return getStore(conn, dataset, schemaName, defUnionGraph);
            }
         catch (ClassNotFoundException e)
            {
            e.printStackTrace();
            return null;
            }
         catch (SQLException e)
            {
            e.printStackTrace();
            return null;
            }
         }

      public static Connection
            getConnection(String jdbcurl, String username, String password) throws ClassNotFoundException, SQLException
         {
         Class.forName("org.postgresql.Driver");
         Properties info = new Properties();
         info.setProperty("user", username);
         info.setProperty("password", password);
         info.setProperty("socketTimeout", "1200");
         Connection conn = DriverManager.getConnection(jdbcurl, info);
         return conn;
         }

      public static PSQLTestData getStore(Connection conn, String dataset, String schemaName, boolean defUnionGraph)
         {
         Context ctx = new Context();
         Store store = StoreManager.connectStore(conn, "postgresql", schemaName, dataset, ctx);
         if (defUnionGraph)
            ctx.set(Context.unionDefaultGraph, Boolean.TRUE);
         Dataset ds = RdfStoreFactory.connectDataset(store, conn, "postgresql");
         return new PSQLTestData(conn, store, ctx, ds);
         }

      }
   
   //
   // Shark Test Data
   //
   public static class SharkTestData extends TestData {
  
      public SharkTestData(Connection conn, Store store, Context ctx, Dataset ds)
         {
         super(conn, store, ctx, ds);
         }

      public static SharkTestData getStore(String jdbcurl, String dataset, String username, String password, String schemaName,
            boolean defUnionGraph)
         {
         try
            {
            Connection conn = getConnection(jdbcurl, username, password);
            return getStore(conn, dataset, schemaName, defUnionGraph);
            }
         catch (ClassNotFoundException e)
            {
            e.printStackTrace();
            return null;
            }
         catch (SQLException e)
            {
            e.printStackTrace();
            return null;
            }
         }

      public static Connection
            getConnection(String jdbcurl, String username, String password) throws ClassNotFoundException, SQLException
         {
         Class.forName("org.apache.hive.jdbc.HiveDriver");
         Properties info = new Properties();
         info.setProperty("user", username);
         info.setProperty("password", password);
         Connection conn = DriverManager.getConnection(jdbcurl, info);
         return conn;
         }

      public static SharkTestData getStore(Connection conn, String dataset, String schemaName, boolean defUnionGraph)
         {
         Context ctx = new Context();
         Store store = StoreManager.connectStore(conn, Store.Backend.shark.name(), schemaName, dataset, ctx);
         if (defUnionGraph)
            ctx.set(Context.unionDefaultGraph, Boolean.TRUE);
         Dataset ds = RdfStoreFactory.connectDataset(store, conn, Store.Backend.shark.name());
         return new SharkTestData(conn, store, ctx, ds);
         }

      }


   public interface DatabaseEngine<D>
      {

      int executeQuery(D data, String file);

      int executeStringQuery(D data, String query);

      int execute(D data, String file, boolean isDescribeWithoutRows);

      int executeSQLTest(D data, String file, String sqlCode);

      }

   protected final D                 data;
   protected final DatabaseEngine<D> engine;
   protected final int[]             answers;

   // initialize the store
   public TestRunner(D data, DatabaseEngine<D> engine, int[] answers)
      {
      this.data = data;
      this.engine = engine;
      this.answers = answers;
      }

   protected int executeQuery(String file)
      {
      long time = System.currentTimeMillis();
      int v = engine.executeQuery(data, file);
      System.err.println("Query: " + file + " time:" + (System.currentTimeMillis() - time));
      return v;
      }

   protected int execute(String file, boolean isDescribeWithoutRows)
      {
      long time = System.currentTimeMillis();
      int v = engine.execute(data, file, isDescribeWithoutRows);
      System.err.println("Query: " + file + " time:" + (System.currentTimeMillis() - time));
      return v;
      }

   protected int executeSQLTest(String file, String sqlCode)
      {
      long time = System.currentTimeMillis();
      int v = engine.executeSQLTest(data, file, sqlCode);
      System.err.println("Query: " + file + " time:" + (System.currentTimeMillis() - time));
      return v;
      }

   protected int executeQuery(String file, int queryNum)
      {
      long time = System.currentTimeMillis();
      int nOR = engine.execute(data, file, false);
      if (answers != null && answers.length > queryNum && answers[queryNum] >= -1)
         {
         Assert.assertEquals(answers[queryNum], nOR);
         }
      System.err.println("Query: " + file + " time:" + (System.currentTimeMillis() - time) + " ");
      System.err.println(file + " has : " + nOR + " rows");
      return nOR;
      }

   protected int executeSparql(String sparql, int answers) {
	   long time = System.currentTimeMillis();
	   int nOR = engine.executeStringQuery(data, sparql);
	   if (answers != -1)
	   {
		   Assert.assertEquals(answers, nOR);
	   }
	   System.err.println("Query time:" + (System.currentTimeMillis() - time) + " ");
	   System.err.println("query has : " + nOR + " rows");
	   return nOR;
   }

   public static class DB2Engine extends AbstractEngine<DB2TestData>
      {
      private OWLQLSPARQLCompiler compiler;

      public DB2Engine()
         {
         this.compiler = null;
         }

      public DB2Engine(OWLQLSPARQLCompiler compiler)
         {
         this.compiler = compiler;
         }
      
	 protected  QueryProcessor createQueryProcessor(DB2TestData data, Query q) {
		   return QueryProcessorFactory.create(q, data.conn, data.store, data.ctx, compiler);
	 }
	   
 

      }

   public static abstract class AbstractEngine<D extends TestData> implements DatabaseEngine<D>
      {
      protected OWLQLSPARQLCompiler compiler;
      //protected boolean isWrapperEnabled = true;
      protected boolean isWrapperEnabled = true;

      public AbstractEngine()
         {
         this.compiler = null;
         }

      public AbstractEngine(OWLQLSPARQLCompiler compiler)
         {
         this.compiler = compiler;
         }

      public int executeQuery(D data, String file)
         {
         return execute(data, file, false);
         }

      @Override
      public int executeStringQuery(D data, String query)
         {
         if (isWrapperEnabled)
            {
             com.hp.hpl.jena.query.Query q = RdfStoreQueryFactory.create(query);
            //com.hp.hpl.jena.query.Query q = QueryFactory.create(query);
            return executeWithInternal(data, q);
            }
         else
            {
            Query q = SparqlParserUtilities.parseSparqlString(query);
            return executeQuery(data, false, q);
            }
         }

      public int execute(D data, String file, boolean isDescribeWithoutRows)
      {
      if (isWrapperEnabled)
         {
         return executeWith(data, file, isDescribeWithoutRows);
         }
      else
         {
          File f = new File(file);
          Query q = SparqlParserUtilities.parseSparql(f, Collections.<String, String> emptyMap());
          return executeQuery(data, isDescribeWithoutRows, q);
         }
      }
      
      protected int executeWith(D data, String file, boolean isDescribeWithoutRows)
      {
      com.hp.hpl.jena.query.Query q = RdfStoreQueryFactory.read(file, null, null);

      return executeWithInternal(data, q);
      }

   private int executeWithInternal(D data, com.hp.hpl.jena.query.Query q)
      {
      int count = 0;
      long time = System.currentTimeMillis();

      QueryExecution qe = RdfStoreQueryExecutionFactory.create(q, data.ds, compiler);
      if (data.ctx.get(Context.unionDefaultGraph) != null)
         qe.getContext().set(Context.unionDefaultGraph, new Boolean(true));

      if (q.isSelectType())
         {
         com.hp.hpl.jena.query.ResultSet rs = qe.execSelect();
         while (rs.hasNext())
            {
        	rs.next();
            count++;
            }
         }
      else if (q.isDescribeType())
         {
         Model m = qe.execDescribe();
         count = (int) m.size();
         }
      else if (q.isConstructType())
         {
         Model m = qe.execConstruct();
         count = (int) m.size();
         }
      else if (q.isAskType())
         {
         boolean ask = qe.execAsk();
         if (ask)
            {
            count = 1;
            }
         }
      System.err.println("Query: time:" + (System.currentTimeMillis() - time));

      qe.close();
      return count;
      }

      public int executeSQLTest(D data, String file, String sqlCode)
         {
         // TODO Auto-generated method stub
         return 0;
         }
      
      protected abstract QueryProcessor createQueryProcessor(D data, Query q); // QueryProcessorFactory.create(q, data.conn, data.store, data.ctx, compiler);

      private int executeQuery(D data, boolean isDescribeWithoutRows, Query q)
         {
         int nOR = -1;

         QueryProcessor qp = createQueryProcessor(data, q);
         LiteralInfoResultSet rs = null;
         if (q.isSelectQuery())
            {
            rs = qp.execSelect();
            nOR = printResult(rs.getResultSet());
            }
         else if (q.isDescribeQuery())
            {
            rs = qp.execDescribe();
            if (isDescribeWithoutRows)
               {
               nOR = 0;
               }
            else
               {
               if (rs != null && rs.getResultSet() != null)
                  {
                  nOR = printResult(rs.getResultSet());
                  }
               }
            }
         else if (q.isAskQuery())
            {
            nOR = qp.execAsk() ? 1 : 0;

            }
         else if (q.isConstructQuery())
            {
            rs = qp.execConstruct();
            if (rs != null && rs.getResultSet() != null)
               {
               nOR = printResult(rs.getResultSet());
               }
            }

         try
            {
            rs.getResultSet().getStatement().close();
            }
         catch (Throwable e)
            {

            }

         return nOR;
         }

      protected static int printResult(/* String sparqlQuery, */ResultSet rst)
         {
         // System.err.println("QUERY: "+sparqlQuery);
         // returns the number of rows returned

         System.err.println("RESULT: ");

         int i, sz = 0;
         int numberOfRows = 0;
         try
            {
            ResultSetMetaData rsmd = rst.getMetaData();
            int numberOfColumns = rsmd.getColumnCount();
            StringBuffer resultHeader = new StringBuffer();
            for (int nCols = 1; nCols <= numberOfColumns; nCols++)
               {
               resultHeader.append(rsmd.getColumnLabel(nCols));
               resultHeader.append("|||");
               }
            System.err.println("============================================================");
            System.err.println(resultHeader.toString());
            System.err.println("============================================================");
            String[] row = null;
            while (rst.next())
               {
               numberOfRows++;
               if (row == null)
                  {
                  sz = rst.getMetaData().getColumnCount();
                  row = new String[sz];
                  }

               for (i = 1; i <= sz; i++)
                  row[i - 1] = rst.getString(i);
               	 // System.err.println(Arrays.toString(row));
               }
            }
         catch (SQLException e)
            {
            // TODO Auto-generated catch block
            e.printStackTrace();
            }
         System.err.println("============================================================");
         return numberOfRows;
         }

      }

   public static class PSQLEngine extends AbstractEngine<PSQLTestData>
   {
	 
	   public PSQLEngine()
	      {
		   super();
	      }
	
	   public PSQLEngine(OWLQLSPARQLCompiler compiler)
	      {
	      super(compiler);
	      }
	   protected  QueryProcessor createQueryProcessor(PSQLTestData data, Query q) {
		   return QueryProcessorFactory.create(q, data.conn, data.store, data.ctx, compiler);
	   }

	    
   }
   public static class SharkEngine extends AbstractEngine<SharkTestData>
   {
	   private OWLQLSPARQLCompiler compiler;

	   public SharkEngine()
	      {
	      super();
	      }
	
	   public SharkEngine(OWLQLSPARQLCompiler compiler)
	      {
	      super(compiler);
	      }
	   protected  QueryProcessor createQueryProcessor(SharkTestData data, Query q) {
		   return QueryProcessorFactory.create(q, data.conn, data.store, data.ctx, compiler);
	   }
  

   }
}
