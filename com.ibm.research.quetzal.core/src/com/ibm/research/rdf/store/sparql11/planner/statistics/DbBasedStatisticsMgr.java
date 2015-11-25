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
package com.ibm.research.rdf.store.sparql11.planner.statistics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ibm.research.proppaths.CTEToNestedQueryConverter;
import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.Store.Backend;
import com.ibm.research.rdf.store.config.Constants;
import com.ibm.research.rdf.store.config.Statistics;
import com.ibm.research.rdf.store.config.StatisticsImpl;
import com.ibm.research.rdf.store.jena.impl.DB2CloseObjects;
import com.ibm.research.rdf.store.runtime.service.sql.SQLExceptionWrapper;
import com.ibm.research.rdf.store.runtime.service.sql.SQLExecutor;
import com.ibm.research.rdf.store.runtime.service.sql.SingleRowResultSetProcessor;
import com.ibm.research.rdf.store.runtime.service.sql.Sqls;
import com.ibm.research.rdf.store.schema.Pair;

public class DbBasedStatisticsMgr
   {

   private Store               store                     = null;
   private Connection          con                       = null;
   private Backend              backend                   = null;

   private static final String BASE_STAT_GRAPH           = "GRAPH";
   private static final String BASE_STAT_GRAPH_OBJ       = "graph_obj";
   private static final String BASE_STAT_GRAPH_OBJ_PRED  = "graph_obj_pred";
   private static final String BASE_STAT_GRAPH_PRED      = "graph_pred";
   private static final String BASE_STAT_GRAPH_SUBJ      = "graph_subj";
   private static final String BASE_STAT_GRAPH_SUBJ_PRED = "graph_subj_pred";
   private static final String BASE_STAT_OBJ             = "obj";
   private static final String BASE_STAT_OBJ_PRED        = "obj_pred";
   private static final String BASE_STAT_PRED            = "pred";
   private static final String BASE_STAT_SUBJ            = "subj";
   private static final String BASE_STAT_SUBJ_PRED       = "subj_pred";
   private static final String NUM_TRIPLES               = "nr_triples";

   private int                 nrTriples                 = 0;

   public DbBasedStatisticsMgr(Connection con, Backend backend, Store store)
      {
      super();
      this.store = store;
      this.con = con;
      this.backend = backend;
      }

   public void runDbEngineStatsCollection()
      {

      }

   public void populateStatsSchema()
      {
	   if (store.getStoreBackend() == Store.Backend.shark) {
			  int reducers = Store.SHARK_REDUCERS;
			  String prop = System.getProperty("mapred.reduce.tasks");
			  if (prop!=null) {
				  try {
					  reducers = Integer.parseInt(prop);
					  if (reducers<=0) {
						  System.err.println("WARNING: Invalid number of reducers specified as value of 'mapred.reduce.tasks': "+reducers);
						  System.err.println("WARNING: The default value will be used: "+ Store.SHARK_REDUCERS);
						  reducers = Store.SHARK_REDUCERS;
					  }
				  } catch (NumberFormatException ex ) {
					  System.err.println("WARNING: Invalid number of reducers specified as value of 'mapred.reduce.tasks': "+reducers);
					  System.err.println("WARNING: The default value will be used: "+ Store.SHARK_REDUCERS);
				  }
			  }
	 		  SQLExecutor.executeUpdate(con,"SET mapred.reduce.tasks = "+ reducers);
	   }
      populateBasicStats();
      populateTopKStats();
      }

   private boolean isSharkEngine() {
	   return  this.backend == Store.Backend.shark;
   }
   private boolean isPostgresqlEngine() {
	   return  this.backend == Store.Backend.postgresql;
   }
   private boolean isDB2Engine() {
	   return  this.backend == Store.Backend.db2;
   }
   private void populateTopKStats()
      {
	  boolean sharkEngine = isSharkEngine();
	  
      // Delete topk stats
      if (!sharkEngine) {
    	  SQLExecutor.executeUpdate(con, "delete from " + store.getTopKStatsTable());
      } else {
    	  //delete is only supported starting at hive 0.14
    	  
      }
      String topKInsert = Sqls.getSqls(this.backend).getSql("insertTopK").replaceFirst("%s", store.getTopKStatsTable());

      // Top Subject
      StringBuffer query = new StringBuffer();
      if (!sharkEngine) {
    	  query.append(topKInsert.replaceFirst("%c", "TYPE, SUBJECT"));
      } else {
    	  query.append(topKInsert);
      }
      query.append(getQueryForTopSubjects());   
      System.out.println("Query: "+query);
      try {
    	  SQLExecutor.executeUpdate(con, query.toString());
      } catch (RuntimeException e) {
    	  if (isSharkEngine()
				   && (e.getCause() instanceof SQLException)
					&& ((SQLException) e.getCause()).getErrorCode() == -101) {
					e.printStackTrace();
					
			} else {
				throw e;
			}
      }

      // Top Objects
      query = new StringBuffer();
      if (!sharkEngine) {
    	  query.append(topKInsert.replaceFirst("%c", "TYPE, OBJECT"));
      } else {
    	  query.append(topKInsert);
      }
      query.append(getQueryForTopObjects());
      try {
    	  SQLExecutor.executeUpdate(con, query.toString());
      } catch (RuntimeException e) {
    	  if (isSharkEngine()
				   && (e.getCause() instanceof SQLException)
					&& ((SQLException) e.getCause()).getErrorCode() == -101) {
					e.printStackTrace();
					
			} else {
				throw e;
			}
      }

      // Top Predicates
      query = new StringBuffer();
      if (!sharkEngine) {
    	  query.append(topKInsert.replaceFirst("%c", "TYPE, PREDICATE"));
      } else {
    	  query.append(topKInsert);
      }
      query.append(getQueryForTopPredicates());
      try {
    	  SQLExecutor.executeUpdate(con, query.toString());
      } catch (RuntimeException e) {
    	  if (isSharkEngine()
				   && (e.getCause() instanceof SQLException)
					&& ((SQLException) e.getCause()).getErrorCode() == -101) {
					e.printStackTrace();
					
			} else {
				throw e;
			}
      }

      // Top Graphs
      query = new StringBuffer();
      if (!sharkEngine) {
    	  query.append(topKInsert.replaceFirst("%c", "TYPE, GRAPH"));
      } else {
    	  query.append(topKInsert);
      }
      query.append(sharkEngine?"select 'graph', GID, 'null', 'null', 'null', count(*) as COUNT ": "select 'graph',gid,count(*) as COUNT ")
      	   .append(" from " + store.getDirectSecondary() + " group by GID having count(*) > "
            + String.valueOf(Constants.LID_COUNT_THRESHOLD / store.getDPrimarySize())
            + " order by "
            +(sharkEngine? " count ": " count(*) ")
            +(sharkEngine? " limit 5000": "fetch first 5000 rows only"));
      try {
    	  SQLExecutor.executeUpdate(con, query.toString());
      } catch (RuntimeException e) {
    	  if (isSharkEngine()
				   && (e.getCause() instanceof SQLException)
					&& ((SQLException) e.getCause()).getErrorCode() == -101) {
					e.printStackTrace();
					
			} else {
				throw e;
			}
      }

      // Number of triples
      query = new StringBuffer();
      if (sharkEngine) {
    	  loadValuesToShark("topK", store.getTopKStatsTable(),"nr_triples", "\\N","\\N","\\N","\\N",  nrTriples);
      } else {
    	  query.append(topKInsert.replaceFirst("%c", "TYPE"));
    	  query.append("VALUES('nr_triples'," + nrTriples + ")");
    	  SQLExecutor.executeUpdate(con, query.toString());

      }
     
      }

   private String getTopEntities(boolean direct)
      {
	  boolean sharkEngine = isSharkEngine();
      String type = (direct ? "'subj'" : "'obj'");
      String secondary = direct ? store.getDirectSecondary() : store.getReverseSecondary();
      String preds = store.getSchemaName() + "." + store.getStoreName() + "_" + (direct ? "direct" : "reverse") + "_preds";
      String ret= "WITH Q0 AS (select entity, count(*) as count from " + secondary + " group by entity)," + "Q1 AS ("
            + flipPrimaryComponent(direct, "SELECT entry AS entity, count(*) as count ", false)
            + (isPostgresqlEngine()? " WHERE": " AND")
            + (sharkEngine? " LT.VAL NOT LIKE 'lid:%'": " LT.PROP in (select pred from " + preds + " where onetoone = 1) ")
            + " group by entry),"
            + "Q2 AS (select entity, count from Q0 union all select entity, count from Q1) "
            + (sharkEngine?"select "+ type+", 'null', "+(direct?"entity, 'null', 'null', ":"'null', 'null', entity, ")+" sum(count)": "select " + type + ", entity, sum(count) ")
            +" from Q2 "
            //+" where count > " + Constants.LID_COUNT_THRESHOLD
      		+ " GROUP BY entity HAVING sum(COUNT) > "+ Constants.LID_COUNT_THRESHOLD;
      System.out.println("Query before transformation:\n"+ret);
      ret = sharkEngine? new CTEToNestedQueryConverter(Store.Backend.shark).transform(ret): ret;
      System.out.println("Query after transformation:\n"+ret);
      
       return ret;
      }

   private String getQueryForTopSubjects()
      {
      return getTopEntities(true);
      }

   private String getQueryForTopObjects()
      {
      return getTopEntities(false);
      }

   private String getQueryForTopPredicates()
      {
	   boolean sharkEngine = isSharkEngine();
      StringBuffer query = new StringBuffer();
      query.append("WITH Q0 AS (");
      query.append(flipPrimaryComponent(true, "SELECT ENTRY as ENTRY, LT.PROP AS PREDICATE, LT.VAL AS VAL ",
            false));
      query.append("),");
      query.append("Q1 AS (");
      query.append(getSecondaryComponent(true, "Q0"));
      query.append(")");
      query.append(sharkEngine?"select 'pred', 'null', 'null', PREDICATE, 'null', COUNT(*) AS CNT ":" SELECT 'pred',PREDICATE, COUNT(*) AS CNT ")
      		.append(" FROM Q1 GROUP BY PREDICATE HAVING COUNT(*) > ");
      query.append(Constants.LID_COUNT_THRESHOLD);
      if (sharkEngine) {
    	  query.append(" ORDER BY CNT DESC LIMIT 5000 "); 
      } else {
    	  query.append(" ORDER BY CNT DESC FETCH FIRST 5000 ROWS ONLY ");
      }
      String ret= query.toString();
      System.out.println("Before query transformation: "+ret );
      ret = sharkEngine? new CTEToNestedQueryConverter(Store.Backend.shark).transform(ret): ret;
      System.out.println("After query transformation: "+ret );
      
      return ret;
      }

   private void populateBasicStats()
      {

      int distinctGraphs = 0;
      int distinctSubjects = 0;
      int distinctObjects = 0;

      // first delete all stats
      if (isPostgresqlEngine())
         {
         SQLExecutor.executeUpdate(con, "delete from " + store.getSchemaName() + "." + store.getBasicStatsTable());
         }
      else if (!isSharkEngine())
         {
    	  
    	  SQLExecutor.executeUpdate(con, "delete from " + store.getBasicStatsTable());
         } else {
       	  //delete is only supported starting at hive 0.14
       	  
         }

      // Total number of values in DPH
      StringBuffer query = new StringBuffer();
      query.append(flipPrimaryComponent(true, "SELECT COUNT(*) AS COUNT ", true));
      System.err.println(query);
      nrTriples = new SQLExecutor().executeQuery(con, query.toString(), new SingleRowResultSetProcessor<Integer>()
         {
            public Integer processRow(Connection conn, ResultSet rs) throws SQLException
               {
               return rs.getInt("COUNT");
               }
         });

      System.err.println("Total number of values in DPH " + nrTriples);

      // Total number of triples = Total number of values in DPH
      // + ( Total records in DS - Unique lids in DS)
      nrTriples += new SQLExecutor().executeQuery(con,
            "SELECT (-COUNT(DISTINCT list_id) + COUNT(*) ) AS COUNT FROM " + store.getDirectSecondary(),
            new SingleRowResultSetProcessor<Integer>()
               {
                  public Integer processRow(Connection conn, ResultSet rs) throws SQLException
                     {
                     return rs.getInt("COUNT");
                     }
               });

      System.err.println("Total number of values in DPH + ( Total records in DS - Unique lids in DS) " + nrTriples);

      // Distinct Graphs
      String distinctCountQuery = Sqls.getSqls(this.backend).getSql("distinctCountQuery");

      distinctGraphs = new SQLExecutor().executeQuery(con, distinctCountQuery.replaceFirst("%table", store.getDirectPrimary())
            .replaceFirst("%type", Constants.NAME_COLUMN_GRAPH_ID), new SingleRowResultSetProcessor<Integer>()
         {
            public Integer processRow(Connection conn, ResultSet rs) throws SQLException
               {
               return rs.getInt("COUNT");
               }
         });

      System.err.println("Distinct Graphs " + distinctGraphs);

      // Distinct Subjects
      distinctSubjects = new SQLExecutor().executeQuery(con, distinctCountQuery.replaceFirst("%table", store.getDirectPrimary())
            .replaceFirst("%type", Constants.NAME_COLUMN_ENTRY), new SingleRowResultSetProcessor<Integer>()
         {
            public Integer processRow(Connection conn, ResultSet rs) throws SQLException
               {
               return rs.getInt("COUNT");
               }
         });

      System.err.println("Distinct Subjects " + distinctSubjects);

      // Distinct Objects
      distinctObjects = new SQLExecutor().executeQuery(con, distinctCountQuery.replaceFirst("%table", store.getReversePrimary())
            .replaceFirst("%type", Constants.NAME_COLUMN_ENTRY), new SingleRowResultSetProcessor<Integer>()
         {
            public Integer processRow(Connection conn, ResultSet rs) throws SQLException
               {
               return rs.getInt("COUNT");
               }
         });

      System.err.println("Distinct Objects " + distinctObjects);

      // Insert into basic stats table
      String insertBasics = Sqls.getSqls(this.backend).getSql("insertBasicStats").replaceFirst("%s", store.getBasicStatsTable());

      double triplesPerGraph = distinctGraphs == 0 ? 0 : (double) nrTriples / distinctGraphs;
      double triplesPerSubject = distinctSubjects == 0 ? 0 : (double) nrTriples / distinctSubjects;
      double triplesPerObject = distinctObjects == 0 ? 0 : (double) nrTriples / distinctObjects;

      System.err.println("triplesPerGraph " + triplesPerGraph);
      System.err.println("triplesPerSubject " + triplesPerSubject);
      System.err.println("triplesPerObject " + triplesPerObject);
      if (isSharkEngine()) {
    	  Object[] stats = new Object[]{BASE_STAT_GRAPH, triplesPerGraph,BASE_STAT_SUBJ, triplesPerSubject, BASE_STAT_OBJ, triplesPerObject};
    	  for (int i=0; i<stats.length/2;i++) {
    		  loadValuesToShark("loadBasicStats", store.getBasicStatsTable(), stats[2*i], stats[2*i+1]);
	    	/*try {
	    		  File loadFile = File.createTempFile("loadBasicStats", ".load");
				  BufferedWriter out = new BufferedWriter(new FileWriter(loadFile, false));
				  out.write(stats[i]+"\t"+stats[i+1]);
				  out.flush();
				  out.close();
				  insertBasics = insertBasics.replaceFirst("%c", loadFile.getAbsolutePath());
				  SQLExecutor.executeUpdate(con, insertBasics);
			} catch (IOException e) {
				throw new SQLExceptionWrapper(e);
			}*/
    	  }
    	  
      } else {
	      SQLExecutor.executeUpdate(con, insertBasics, BASE_STAT_GRAPH, triplesPerGraph);
	      SQLExecutor.executeUpdate(con, insertBasics, BASE_STAT_SUBJ, triplesPerSubject);
	      SQLExecutor.executeUpdate(con, insertBasics, BASE_STAT_OBJ, triplesPerObject);
      }

      }
   private void loadValuesToShark(String optionalTempFilePrefix, String table,  Object... values) {
	   String insertBasics = Sqls.getSqls(Store.Backend.shark).getSql("insertLoad").replaceFirst("%s", table);
	   try {
 		  File loadFile = File.createTempFile(optionalTempFilePrefix!=null?optionalTempFilePrefix:"load", ".load");
			  BufferedWriter out = new BufferedWriter(new FileWriter(loadFile, false));
			  for (int i=0; i< values.length; i++) {
				  if (i>0) {
					  out.write("\t");
				  }
				  out.write(values[i].toString());
			  }
			  out.flush();
			  out.close();
			  insertBasics = insertBasics.replaceFirst("%c", loadFile.getAbsolutePath());
			  SQLExecutor.executeUpdate(con, insertBasics);
		} catch (IOException e) {
			throw new SQLExceptionWrapper(e);
		}
   }
   public Statistics[] loadStatsFromSchema()
      {

      Statistics perGraphStatistics = new StatisticsImpl();
      Statistics overallStatistics = new StatisticsImpl();
      overallStatistics.setGraphStatistic(new AverageStatistic());

      loadBaseStats(perGraphStatistics, overallStatistics);
      loadTopkStats(perGraphStatistics, overallStatistics);
      perGraphStatistics.setPerGraphStatistics(true);

      Statistics[] stats = new Statistics[2];
      stats[0] = perGraphStatistics;
      stats[1] = overallStatistics;
      return stats;

      }

   private void loadTopkStats(Statistics perGraphStatistics, Statistics overallStatistics)
      {

      String selecttopKStats = Sqls.getSqls(this.backend).getSql("getTopKStats").replaceFirst("%s", store.getTopKStatsTable());

      PreparedStatement stmt = null;
      ResultSet rst = null;
      try
         {
         stmt = con.prepareStatement(selecttopKStats);
         rst = stmt.executeQuery();

         while (rst.next())
            {

            String type = rst.getString("TYPE");

            if (type.equalsIgnoreCase(BASE_STAT_GRAPH))
               {
               perGraphStatistics.getTopGraphs().put(rst.getString("GRAPH"), new BigInteger(rst.getString("CNT")));
               }
            else if (type.equalsIgnoreCase(BASE_STAT_GRAPH_OBJ))
               {
               perGraphStatistics.getTopObjects().put(rst.getString("OBJECT"), new BigInteger(rst.getString("CNT")));
               }
            else if (type.equalsIgnoreCase(BASE_STAT_OBJ))
               {
               overallStatistics.getTopObjects().put(rst.getString("OBJECT"), new BigInteger(rst.getString("CNT")));
               }
            else if (type.equalsIgnoreCase(BASE_STAT_GRAPH_SUBJ))
               {
               perGraphStatistics.getTopSubjects().put(rst.getString("SUBJECT"), new BigInteger(rst.getString("CNT")));
               }
            else if (type.equalsIgnoreCase(BASE_STAT_SUBJ))
               {
               overallStatistics.getTopSubjects().put(rst.getString("SUBJECT"), new BigInteger(rst.getString("CNT")));
               }
            else if (type.equalsIgnoreCase(BASE_STAT_GRAPH_PRED))
               {
               perGraphStatistics.getTopPredicates().put(rst.getString("PREDICATE"), new BigInteger(rst.getString("CNT")));
               }

            else if (type.equalsIgnoreCase(BASE_STAT_PRED))
               {
               overallStatistics.getTopPredicates().put(rst.getString("PREDICATE"), new BigInteger(rst.getString("CNT")));
               }
            else if (type.equalsIgnoreCase(BASE_STAT_GRAPH_SUBJ_PRED))
               {
               Pair<String> p = new Pair<String>(rst.getString("SUBJECT"), rst.getString("PREDICATE"));
               perGraphStatistics.getTopSubject_Predicate_Pairs().put(p, new BigInteger(rst.getString("CNT")));
               }
            else if (type.equalsIgnoreCase(BASE_STAT_SUBJ_PRED))
               {
               Pair<String> p = new Pair<String>(rst.getString("SUBJECT"), rst.getString("PREDICATE"));
               overallStatistics.getTopSubject_Predicate_Pairs().put(p, new BigInteger(rst.getString("CNT")));
               }
            else if (type.equalsIgnoreCase(BASE_STAT_GRAPH_OBJ_PRED))
               {
               Pair<String> p = new Pair<String>(rst.getString("OBJECT"), rst.getString("PREDICATE"));
               perGraphStatistics.getTopObject_Predicate_Pairs().put(p, new BigInteger(rst.getString("CNT")));
               }
            else if (type.equalsIgnoreCase(BASE_STAT_OBJ_PRED))
               {
               Pair<String> p = new Pair<String>(rst.getString("OBJECT"), rst.getString("PREDICATE"));
               overallStatistics.getTopObject_Predicate_Pairs().put(p, new BigInteger(rst.getString("CNT")));
               }
            else if (type.equalsIgnoreCase(NUM_TRIPLES))
               {
               overallStatistics.setTripleCount(new BigInteger(rst.getString("CNT")));
               perGraphStatistics.setTripleCount(new BigInteger(rst.getString("CNT")));
               }

            }
         }
      catch (SQLException e)
         {
    	  throw new RuntimeException(e);
         }
      finally
         {
         DB2CloseObjects.close(rst, stmt);
         }
      }

   private void loadBaseStats(Statistics perGraphStatistics, Statistics overallStatistics)
      {

      PreparedStatement stmt = null;
      ResultSet rst = null;

      try
         {

         String basicStatsQ = Sqls.getSqls(this.backend).getSql("getBasicStats").replaceFirst("%s", store.getBasicStatsTable());
         stmt = con.prepareStatement(basicStatsQ);
         rst = stmt.executeQuery();

         while (rst.next())
            {

            String type = rst.getString("TYPE");

            if (type.equalsIgnoreCase(BASE_STAT_GRAPH))
               {
               perGraphStatistics.setGraphStatistic(new AverageStatistic(rst.getDouble("AVG"), rst.getDouble("STDDEV")));
               }
            else if (type.equalsIgnoreCase(BASE_STAT_GRAPH_OBJ))
               {
               perGraphStatistics.setObjectStatistic(new AverageStatistic(rst.getDouble("AVG"), rst.getDouble("STDDEV")));
               }
            else if (type.equalsIgnoreCase(BASE_STAT_OBJ))
               {
               overallStatistics.setObjectStatistic(new AverageStatistic(rst.getDouble("AVG"), rst.getDouble("STDDEV")));
               }
            else if (type.equalsIgnoreCase(BASE_STAT_GRAPH_SUBJ))
               {
               perGraphStatistics.setSubjectStatistic(new AverageStatistic(rst.getDouble("AVG"), rst.getDouble("STDDEV")));
               }
            else if (type.equalsIgnoreCase(BASE_STAT_SUBJ))
               {
               overallStatistics.setSubjectStatistic(new AverageStatistic(rst.getDouble("AVG"), rst.getDouble("STDDEV")));

               }
            else if (type.equalsIgnoreCase(BASE_STAT_GRAPH_PRED))
               {
               perGraphStatistics.setPredicateStatistic(new AverageStatistic(rst.getDouble("AVG"), rst.getDouble("STDDEV")));
               }
            else if (type.equalsIgnoreCase(BASE_STAT_PRED))
               {
               overallStatistics.setPredicateStatistic(new AverageStatistic(rst.getDouble("AVG"), rst.getDouble("STDDEV")));

               }
            else if (type.equalsIgnoreCase(BASE_STAT_GRAPH_SUBJ_PRED))
               {
               perGraphStatistics.setSubjectPredicatePairs(new AverageStatistic(rst.getDouble("AVG"), rst.getDouble("STDDEV")));
               }
            else if (type.equalsIgnoreCase(BASE_STAT_SUBJ_PRED))
               {
               overallStatistics.setSubjectPredicatePairs(new AverageStatistic(rst.getDouble("AVG"), rst.getDouble("STDDEV")));
               }
            else if (type.equalsIgnoreCase(BASE_STAT_GRAPH_OBJ_PRED))
               {
               perGraphStatistics.setObjectPredicatePairs(new AverageStatistic(rst.getDouble("AVG"), rst.getDouble("STDDEV")));
               }
            else if (type.equalsIgnoreCase(BASE_STAT_OBJ_PRED))
               {
               overallStatistics.setObjectPredicatePairs(new AverageStatistic(rst.getDouble("AVG"), rst.getDouble("STDDEV")));
               }
            }
         }
      catch (SQLException e)
         {
    	  	throw new RuntimeException(e);
         }
      finally
         {
         DB2CloseObjects.close(rst, stmt);
         }

      }

   private String flipPrimaryComponent(boolean direct, String selectClause, boolean predOnly)
      {
      StringBuffer query = new StringBuffer(selectClause);
      int maxPreds = (direct) ? store.getDPrimarySize() : store.getRPrimarySize();
      query.append(" FROM \n");

      if (isPostgresqlEngine())
         {
         query.append(" (SELECT ENTRY, GID, UNNEST(ARRAY_REMOVE(ARRAY[");
         int nrPreds = 0;
         for (; nrPreds < maxPreds; nrPreds++)
            {
            if (nrPreds > 0)
               {
               query.append(",");
               }
            query.append("prop" + nrPreds);
            }
         query.append("], NULL)) AS prop");

         if (!predOnly)
            {
            query.append(", UNNEST(ARRAY_REMOVE(ARRAY[");
            nrPreds = 0;
            for (; nrPreds < maxPreds; nrPreds++)
               {
               if (nrPreds > 0)
                  {
                  query.append(",");
                  }
               query.append("val" + nrPreds);
               }
            query.append("], NULL)) AS val");
            }
         
         query.append(" FROM " + ((direct)?store.getDirectPrimary():store.getReversePrimary()) + ") AS LT");
         }
      else if (isSharkEngine()) {
    	  query.append("( ");
    	  if (predOnly) {
	    	  query.append("select entry, gid, prop");
	    	  query.append(" from ")
	    	  	   .append(direct?store.getDirectPrimary():store.getReversePrimary())
	    	  	   .append(" lateral view explode(array(");
	    	  	for (int nrPreds = 0; nrPreds< maxPreds;nrPreds++) {
				  if (nrPreds>0) {
					  query.append(", ");
				  }
				  query.append("prop"+nrPreds);
	    	  	}
	    	  query.append(")) a AS prop ");
    	  } else {
    		  query.append("select entry, gid, prop, val");
	    	  query.append(" from ")
	    	  	   .append(direct?store.getDirectPrimary():store.getReversePrimary())
	    	  	   .append(" lateral view explode(map(");
	    	  	for (int nrPreds = 0; nrPreds< maxPreds;nrPreds++) {
				  if (nrPreds>0) {
					  query.append(", ");
				  }
				  query.append("prop"+nrPreds+", val"+nrPreds);
	    	  	}
	    	  query.append(")) m AS prop, val ");
    	  }
    	 
    	  query.append(") LT");
    	  query.append(" WHERE LT.PROP IS NOT NULL");
      } else  {
         if (direct)
            {
            query.append(store.getDirectPrimary());
            maxPreds = store.getDPrimarySize();
            }
         else
            {
            query.append(store.getReversePrimary());
            maxPreds = store.getRPrimarySize();
            }
         query.append(" AS T,");
         query.append("TABLE(VALUES ");
         int nrPreds = 0;
         for (; nrPreds < maxPreds; nrPreds++)
            {
            if (nrPreds > 0)
               query.append(",");
            query.append("(");

            query.append("T.PROP");
            query.append(nrPreds);

            if (!predOnly)
               {
               query.append(",");
               query.append("T.VAL");
               query.append(nrPreds);
               }
            query.append(")");
            }

         if (predOnly)
            {
            query.append(") AS LT(PROP) ");
            }
         else
            {
            query.append(") AS LT(PROP,VAL) ");
            }
         query.append(" WHERE LT.PROP IS NOT NULL");
         }

      return query.toString();
      }

   private String getSecondaryComponent(boolean isDirect, String queryPart)
      {
      String secondaryTable = (isDirect) ? store.getDirectSecondary() : store.getReverseSecondary();
      StringBuffer tmpQuery = new StringBuffer();
      tmpQuery.append(" SELECT T.ENTRY AS ENTRY, ");
      if (!isDirect)
         {
         tmpQuery.append("T.TYP AS TYP, ");
         }
      tmpQuery.append("T.PREDICATE");
      tmpQuery.append(" AS PREDICATE,");
      tmpQuery.append("COALESCE(S.ELEM,T.VAL)");
      tmpQuery.append(" AS VAL ");
      tmpQuery.append(" FROM " + secondaryTable )
      //		 .append(sharkEngine?" S ":" as S ")
      		 .append(" AS S ")
      		 .append(" RIGHT OUTER JOIN " + queryPart)
     // tmpQuery.append(sharkEngine?" T ": " AS T ")
      		 .append(" AS T ")
      		 .append(" ON S.LIST_ID=T.VAL");
      tmpQuery.append(" AND S.ELEM ");
      tmpQuery.append("IS NOT NULL");
      return tmpQuery.toString();
      }

   }
