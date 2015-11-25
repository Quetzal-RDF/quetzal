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
package com.ibm.research.rdf.store.runtime.service.sql;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.ibm.research.proppaths.StoreProcedure;
import com.ibm.research.proppaths.TemporaryTableSpaceCreation;
import com.ibm.research.rdf.store.Context;
import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.Store.Backend;
import com.ibm.research.rdf.store.StoreManager;
import com.ibm.research.rdf.store.config.Constants;
import com.ibm.research.rdf.store.hashing.GraphColoringHashingFamily;
import com.ibm.research.rdf.store.jena.RdfStoreException;

public class StoreHelper
   {

   private static final Log log = LogFactory
			.getLog(StoreHelper.class);
   public static final int     TBLNAMELEN                    = 99;

   private static final String USERTEMPTABLESPACE_NAME       = "tmpspace";
   private static final String USERTEMPTABLESPACE_FILE       = "~/usertmpspace";

   private static final String REACHABLENODES_PROC_DEF;
   private static final String REACHABLENODES_PROC_FILE_NAME = "reachableNodes.proc";
   static
      {
      try
         {
         InputStream in = StoreProcedure.class.getResourceAsStream(REACHABLENODES_PROC_FILE_NAME);
         BufferedReader r = new BufferedReader(new InputStreamReader(in));
         String line;
         StringBuffer buf = new StringBuffer();
         while ((line = r.readLine()) != null)
            {
            buf.append(line).append("\n");
            }
         REACHABLENODES_PROC_DEF = buf.toString();
         in.close();
         r.close();
         }
      catch (IOException e)
         {
         throw new RuntimeException(e);
         }
      }

   protected static void
         ensureExistenceOfUserTablespaceAndBuiltInRecursiveStoreProcedure(Connection con, Store store) throws RdfStoreException
      {
      if (!(store.getStoreBackend() == Backend.db2))
         {
         // TODO for postgres
         return;
         }
      TemporaryTableSpaceCreation cmd = new TemporaryTableSpaceCreation(USERTEMPTABLESPACE_NAME, USERTEMPTABLESPACE_FILE);
      Statement st = null;
      try
         {
         try
            {
            st = con.createStatement();
            st.execute(cmd.toSQL());
            store.setUserTablespace(USERTEMPTABLESPACE_NAME);
            st.execute(REACHABLENODES_PROC_DEF);
            }
         catch (SQLException ex)
            {
            }

         }
      finally
         {
         if (st != null)
            {
            try
               {
               st.close();
               }
            catch (SQLException ex)
               {
               }
            }
         }
      }

   public static StringBuffer readPredicateMappings(String predicateMappings)
      {
      StringBuffer buff = new StringBuffer();
      try
         {
         BufferedReader bfr = new BufferedReader(new InputStreamReader(new FileInputStream(predicateMappings)));
         String line = null;
         while ((line = bfr.readLine()) != null)
            {
            buff.append(line);
            buff.append("\r\n");
            }
         bfr.close();
         }
      catch (IOException e2)
         {
         }
      return buff;
      }

   /**
    * Creates the named RDFStore Config table in a schema
    * 
    * @param conn
    * @param systemPredicates
    * @return
    */
   public static boolean createRdfStoreTable(Connection conn, Backend backend, String schemaName, String storeName,
         String tablespaceName, String predicateMappings)
      {

      //
      // create the master table
      //

      //
      // For PostgreSQL, a sequence must be defined for the master table
      //
      if (backend == Store.Backend.postgresql)
         {
         String newSeq = Sqls.getSqls(backend).getSql("entry_id_seq");
         newSeq = newSeq.replaceFirst("%s", storeName);
         doSql(conn, backend, newSeq);
         }

      long length = new File(predicateMappings).length();
      String t = Sqls.getSqls(backend).getSql("storeCfgTable");
      t = t.replaceFirst("%s", storeName);
      if  (Backend.shark == backend) {
     	 t = t.replaceFirst("%s",storeName); // needed for shark in "CACHE TABLE %s"
      }

      //
      // In both DB2 and PostgreSQL, there is a second %s, but for different reasons in each one.
      //

      //
      // In DB2, there is a CLOB object to hold the predicate mappings. We need to replace the %s with the size of the predicate_mappings file.
      // In PostgreSQL, the same attribute has type TEXT. So, no need to set it's size
      //
      if (backend == Backend.db2)
         {
         t = t.replaceFirst("%s", predicateMappings == null ? "2M" : String.valueOf(Math.round(length * 1.2)));
         }

      //
      // In PostgreSQL, entry_ID is a sequence, so we need to properly set it. This is where the second %s appears in PostgreSQL
      //
      if (backend == Backend.postgresql)
         {
         t = t.replace("%s", storeName);
         }

      if (tablespaceName != null)
         {
         t = t + " IN " + tablespaceName;
         }
      doSql(conn, backend, t);

      return true;

      }

   public static boolean checkProperty(String property)
      {
      // return !System.getProperties().containsKey(property) || Boolean.parseBoolean(System.getProperty(property));
      return (System.getProperties().containsKey(property) && Boolean.parseBoolean(System.getProperty(property)));
      }

   /**
    * Creates a tripleStore with defaults
    * 
    * @param conn
    * @param datasetName
    * @param names
    */
   public static Store createDefaultStore(Connection conn, Backend backend, String schemaName, String datasetName,
         String predicateMappings, String tablespaceName)
      {
      boolean storeCreation = checkProperty("com.ibm.rdf.createStore");
      boolean indexCreation = checkProperty("com.ibm.rdf.createIndex");
      boolean generateSqlOnly = System.getProperty("com.ibm.rdf.generateStoreSQL") != null;

      int gidLenght = Constants.DEFAULT_GID_LENGTH;
      int colLenght = Constants.DEFAULT_COL_LENGTH;
      int bucketSizeDPH = Constants.DEFAULT_DPH_BUCKET_SIZE;
      int bucketSizeRPH = Constants.DEFAULT_RPH_BUCKET_SIZE;
      int defaultStoreStatus = Constants.DEFAULT_STORE_STATUS;

      if (predicateMappings != null)
         {
         DatasetGraph dsg = GraphColoringHashingFamily.loadDataset(predicateMappings);
         bucketSizeDPH = new GraphColoringHashingFamily(dsg, true, 3).getMaxColor() + 1;
         bucketSizeRPH = new GraphColoringHashingFamily(dsg, false, 3).getMaxColor() + 1;
         }

      java.util.Properties names = generateInternalNames(datasetName);

      String currentObj = "";

      if (storeCreation)
         {
         SystemPredicateTableGeneration generation = new SystemPredicateTableGeneration();

         //
         // create the DPH table
         //
         String t;
         if (bucketSizeDPH != Constants.DEFAULT_DPH_BUCKET_SIZE)
            {
            t = generation.getDirectPrimaryTableStatement(backend, gidLenght, colLenght, bucketSizeDPH);
            bucketSizeDPH = generation.getActualBucketSize();
            }
         else
            {
            t = Sqls.getSqls(backend).getSql("defaultPrimarys");
            }
         currentObj = names.getProperty(Constants.NAME_TABLE_DIRECT_PRIMARY_HASH);
         t = t.replaceFirst("%s", currentObj);
         if  (Backend.shark == backend) {
        	 t = t.replaceFirst("%s", currentObj); // needed for shark in "CACHE TABLE %s"
         }
         if (tablespaceName != null)
            {
            t = t + " IN " + tablespaceName;
            }
         doSql(conn, backend, t);

         //
         // create the RPH table
         //
         currentObj = names.getProperty(Constants.NAME_TABLE_REVERSE_PRIMARY_HASH);
         if (bucketSizeRPH != Constants.DEFAULT_RPH_BUCKET_SIZE)
            {
            t = generation.getReversePrimaryTableStatement(backend, gidLenght, colLenght, bucketSizeRPH);
            bucketSizeRPH = generation.getActualBucketSize();
            }
         else
            {
            t = Sqls.getSqls(backend).getSql("defaultReversePrimary");
            }
         t = t.replaceFirst("%s", currentObj);
         if  (Backend.shark == backend) {
        	 t = t.replaceFirst("%s", currentObj); // needed for shark in "CACHE TABLE %s"
         }
         if (tablespaceName != null)
            {
            t = t + " IN " + tablespaceName;
            }
         doSql(conn, backend, t);

         //
         // crerate the DS table
         //
         t = Sqls.getSqls(backend).getSql("defaultDirectSecondary");
         currentObj = names.getProperty(Constants.NAME_TABLE_DIRECT_SECONDARY_HASH);
         t = t.replaceFirst("%s", currentObj);
         if  (Backend.shark == backend) {
        	 t = t.replaceFirst("%s", currentObj); // needed for shark in "CACHE TABLE %s"
         }
         if (tablespaceName != null)
            {
            t = t + " IN " + tablespaceName;
            }
         doSql(conn, backend, t);

         if (backend == Backend.postgresql)
            {
            t = Sqls.getSqls(backend).getSql("triggerFunction");
            doSql(conn, backend, t);

            t = Sqls.getSqls(backend).getSql("typeofTrigger");
            t = t.replaceFirst("%s", names.getProperty(Constants.NAME_TABLE_DIRECT_SECONDARY_HASH));
            doSql(conn, backend, t);
            }

         //
         // crerate the RS table
         //
         t = Sqls.getSqls(backend).getSql("defaultReverseSecondary");
         currentObj = names.getProperty(Constants.NAME_TABLE_REVERSE_SECONDARY_HASH);
         t = t.replaceFirst("%s", currentObj);
         if  (Backend.shark == backend) {
        	 t = t.replaceFirst("%s", currentObj); // needed for shark in "CACHE TABLE %s"
         }
         if (tablespaceName != null)
            {
            t = t + " IN " + tablespaceName;
            }
         doSql(conn, backend, t);

         //
         // crerate the LSTR table
         //
         t = Sqls.getSqls(backend).getSql("defaultLongStrings");
         currentObj = names.getProperty(Constants.NAME_TABLE_LONG_STRINGS);
         t = t.replaceFirst("%s", currentObj);
         if  (Backend.shark == backend) {
        	 t = t.replaceFirst("%s", currentObj); // needed for shark in "CACHE TABLE %s"
         }
         t = t.replaceFirst("%s", System.getProperty("com.ibm.rdf.longStringsLength", "2600"));
         if (tablespaceName != null)
            {
            t = t + " IN " + tablespaceName;
            }
         doSql(conn, backend, t);

         //
         // crerate the DIRECT_PREDS table
         //
         t = Sqls.getSqls(backend).getSql("defaultPredInfoTable");
         t = t.replaceFirst("%s", datasetName + "_direct");
         if  (Backend.shark == backend) {
        	 t = t.replaceFirst("%s",datasetName + "_direct"); // needed for shark in "CACHE TABLE %s"
         }
         if (tablespaceName != null)
            {
            t = t + " IN " + tablespaceName;
            }
         doSql(conn, backend, t);

         //
         // crerate the REVERSE_PRED table
         //
         t = Sqls.getSqls(backend).getSql("defaultPredInfoTable");
         t = t.replaceFirst("%s", datasetName + "_reverse");
         if  (Backend.shark==backend) {
        	 t = t.replaceFirst("%s",datasetName + "_reverse"); // needed for shark in "CACHE TABLE %s"
         }
         if (tablespaceName != null)
            {
            t = t + " IN " + tablespaceName;
            }
         doSql(conn, backend, t);

         //
         // crerate the BASIC_STATS table
         //
         t = Sqls.getSqls(backend).getSql("basicStats");
         currentObj = names.getProperty(Constants.NAME_TABLE_BASIC_STATS);
         t = t.replaceFirst("%s", currentObj);
         if  (Backend.shark == backend) {
        	 t = t.replaceFirst("%s", currentObj); // needed for shark in "CACHE TABLE %s"
         }
         if (tablespaceName != null)
            {
            t = t + " IN " + tablespaceName;
            }
         doSql(conn, backend, t);

         //
         // crerate the TOPKSTATS table
         //
         t = Sqls.getSqls(backend).getSql("topKStats");
         currentObj = names.getProperty(Constants.NAME_TABLE_TOPK_STATS);
         t = t.replaceFirst("%s", currentObj);
         if  (Backend.shark==backend) {
        	 t = t.replaceFirst("%s", currentObj); // needed for shark in "CACHE TABLE %s"
         }
         if (tablespaceName != null)
            {
            t = t + " IN " + tablespaceName;
            }
         doSql(conn, backend, t);

         //
         // crerate the DT table
         //
         t = Sqls.getSqls(backend).getSql("defaultDataTypeTable");
         currentObj = names.getProperty(Constants.NAME_TABLE_DATATYPE);
         t = t.replaceFirst("%s", currentObj);
         if  (Backend.shark==backend) {
        	 t = t.replaceFirst("%s", currentObj); // needed for shark in "CACHE TABLE %s"
         }
         if (tablespaceName != null)
            {
            t = t + " IN " + tablespaceName;
            }
         doSql(conn, backend, t);

         if (!(backend == Store.Backend.shark)) {
	         //
	         // crerate the datatype sequences
	         //
	         
	         t = Sqls.getSqls(backend).getSql("defaultDataTypeSequence");
	         currentObj = names.getProperty(Constants.NAME_TABLE_DATATYPE);
	         t = t.replaceFirst("%s", currentObj);
	         doSql(conn, backend, t);
	
	         t = Sqls.getSqls(backend).getSql("defaultDataLangSequence");
	         currentObj = names.getProperty(Constants.NAME_TABLE_DATATYPE);
	         t = t.replaceFirst("%s", currentObj);
	         doSql(conn, backend, t);
         }

         // insert into Config table
         if (backend == Store.Backend.shark) {
        	 BufferedWriter out = null;
        	 try {
				File loadFile = File.createTempFile("loadKBMetaDataRow", ".hiveql");
				 StringBuffer  insert = new StringBuffer();
				 insert.append("\\N\t")  // *entryid*
				 	   .append(datasetName.toUpperCase()).append("\t")  //storeName
				 	   .append(1).append("\t") //version
				 	   .append(names.getProperty(Constants.NAME_TABLE_DIRECT_PRIMARY_HASH)).append("\t") //directprimary
				 	   .append(Integer.toString(bucketSizeDPH)).append("\t") //dprimarysize 
				 	   .append(names.getProperty(Constants.NAME_TABLE_DIRECT_SECONDARY_HASH)).append("\t")  // directsecondary
				 	   .append(names.getProperty(Constants.NAME_TABLE_REVERSE_PRIMARY_HASH)).append("\t") //reverseprimary
				 	   .append(Integer.toString(bucketSizeRPH)).append("\t") //rprimarysize 
				 	   .append(names.getProperty(Constants.NAME_TABLE_REVERSE_SECONDARY_HASH)).append("\t") //reversesecondary
				 	   .append(names.getProperty(Constants.NAME_TABLE_LONG_STRINGS)).append("\t")  //longStrings
				 	   .append(Integer.toString(colLenght)).append("\t")  //maxStringLen
				 	   .append(Integer.toString(gidLenght)).append("\t")  //gidMaxStringLen,
				   	   .append("\\N").append("\t") //  *haslongstrings*
				 	   .append(names.getProperty(Constants.NAME_TABLE_BASIC_STATS)).append("\t")  //basicStatsTable,
				 	   .append(names.getProperty(Constants.NAME_TABLE_TOPK_STATS)).append("\t") // topKStatsTable, 
				 	   .append("\\N").append("\t") //*laststatsupdated*
				 	   .append("\\N").append("\t") //predicateMappings,
				 	   .append("\\N").append("\t") // systemPredicateTable,
				 	   .append(Integer.toString(defaultStoreStatus)).append("\t")  // |status|,
				 	   .append(names.getProperty(Constants.NAME_TABLE_DATATYPE)).append("\t") // |dataTypeTable|
				   	   .append("\\N"); // *hasspills*
				   	   //.append("\n"); 
				 out = new BufferedWriter(new FileWriter(loadFile, false));
				 out.write(insert.toString());
				 String loadHiveQl = Sqls.getSqls(backend).getSql("loadFromFile");
				 loadHiveQl = loadHiveQl.replaceFirst("%s", datasetName).replaceFirst("%c", loadFile.getAbsolutePath());
				 doSql(conn, backend, loadHiveQl);
				 
			} catch (IOException e) {
				log.error(e);
				e.printStackTrace();
				System.out.println(e.getLocalizedMessage());
			} finally {
				if (out!=null) {
					try {
						out.close();
					} catch (IOException e) {
						log.error(e);
						e.printStackTrace();
						System.out.println(e.getLocalizedMessage());
					}
				}
			}
	   	 	
         } else {
	         String insert = Sqls.getSqls(backend).getSql("defaultDataset");
	         insert = insert.replaceFirst("%s", datasetName); 
	
	         if (generateSqlOnly)
	            {
	            //
	            // We simulate here the same replacement of the ? SQL parameters with values that executeUpdate does
	            // in terms of the clob, we no longer load it with the predicate_mappings content
	            //
	            insert = insert.replaceFirst("\\?", "'" + datasetName + "'");
	            insert = insert.replaceFirst("\\?", "'" + names.getProperty(Constants.NAME_TABLE_DIRECT_PRIMARY_HASH) + "'");
	            insert = insert.replaceFirst("\\?", Integer.toString(bucketSizeDPH));
	            insert = insert.replaceFirst("\\?", "'" + names.getProperty(Constants.NAME_TABLE_DIRECT_SECONDARY_HASH) + "'");
	            insert = insert.replaceFirst("\\?", "'" + names.getProperty(Constants.NAME_TABLE_REVERSE_PRIMARY_HASH) + "'");
	            insert = insert.replaceFirst("\\?", Integer.toString(bucketSizeRPH));
	            insert = insert.replaceFirst("\\?", "'" + names.getProperty(Constants.NAME_TABLE_REVERSE_SECONDARY_HASH) + "'");
	            insert = insert.replaceFirst("\\?", "'" + names.getProperty(Constants.NAME_TABLE_LONG_STRINGS) + "'");
	            insert = insert.replaceFirst("\\?", Integer.toString(colLenght));
	            insert = insert.replaceFirst("\\?", Integer.toString(gidLenght));
	            insert = insert.replaceFirst("\\?", "'" + names.getProperty(Constants.NAME_TABLE_BASIC_STATS) + "'");
	            insert = insert.replaceFirst("\\?", "'" + names.getProperty(Constants.NAME_TABLE_TOPK_STATS) + "'");
	            insert = insert.replaceFirst("\\?", "null");
	            insert = insert.replaceFirst("\\?", "null");
	            insert = insert.replaceFirst("\\?", "'" + names.getProperty(Constants.NAME_TABLE_DATATYPE) + "'");
	            insert = insert.replaceFirst("\\?", Integer.toString(defaultStoreStatus));
	            
	            doSql(conn, backend, insert);
	            }
	         else
	            {
	            //
	            // we no longer load the predicate_mappings file to the clob
	            //
	            SQLExecutor.executeUpdate(conn, insert, datasetName, names.getProperty(Constants.NAME_TABLE_DIRECT_PRIMARY_HASH),
	                  bucketSizeDPH, names.getProperty(Constants.NAME_TABLE_DIRECT_SECONDARY_HASH),
	                  names.getProperty(Constants.NAME_TABLE_REVERSE_PRIMARY_HASH), bucketSizeRPH,
	                  names.getProperty(Constants.NAME_TABLE_REVERSE_SECONDARY_HASH),
	                  names.getProperty(Constants.NAME_TABLE_LONG_STRINGS), colLenght, gidLenght,
	                  names.getProperty(Constants.NAME_TABLE_BASIC_STATS), names.getProperty(Constants.NAME_TABLE_TOPK_STATS), "",
	                  "", names.getProperty(Constants.NAME_TABLE_DATATYPE), defaultStoreStatus);
	            }
         }
         /*
          * // Standard Datatype entries insert = Sqls.getSqls().getSql("defaultDataTypeEntry"); insert = insert.replaceFirst("%s", names
          * .getProperty(Constants.NAME_TABLE_DATATYPE));
          * 
          * stmt = null; try { stmt = conn.prepareStatement(insert); for (i = 1; i < TypeMap.getTypedIRIArray().length; i++) { stmt.setInt(1,
          * TypeMap.DATATYPE_IDS_START + i); stmt.setString(2, TypeMap.getTypedIRIArray()[i]); stmt.addBatch(); } for (i = 0; i <
          * TypeMap.getLangArray().length; i++) { stmt.setInt(1, TypeMap.getLangArray()[i].langcode); stmt.setString(2,
          * TypeMap.getLangArray()[i].langstring); stmt.addBatch(); } stmt.executeBatch(); } catch (SQLException e) { throw new
          * RdfStoreException(e.getLocalizedMessage(),e); } finally { DB2CloseObjects.close(stmt); }
          */

         if (!generateSqlOnly)
            {
            if (backend == Backend.db2)
               {
               setupRunStatsProfile(conn, backend, names.getProperty(Constants.NAME_TABLE_DIRECT_PRIMARY_HASH),
                     names.getProperty(Constants.NAME_TABLE_DIRECT_SECONDARY_HASH),
                     names.getProperty(Constants.NAME_TABLE_REVERSE_PRIMARY_HASH),
                     names.getProperty(Constants.NAME_TABLE_REVERSE_SECONDARY_HASH),
                     names.getProperty(Constants.NAME_TABLE_LONG_STRINGS), bucketSizeDPH, bucketSizeRPH,
                     getSchema(conn, schemaName));
               }

            }
         }

      if (indexCreation)
         {
         createIndexes(conn, backend, names, datasetName);
         }

      if (generateSqlOnly)
         {
         return null;
         }
      else
         {
         return StoreManager.connectStore(conn, backend, schemaName, datasetName, Context.defaultContext);
         }
      }

   private static void doSql(Connection conn, Backend backend, String t)
      {
      String generateSqlOnly = System.getProperty("com.ibm.rdf.generateStoreSQL");
      if (generateSqlOnly != null) {
    	  try {
			FileWriter fw = new FileWriter(generateSqlOnly, true);
			fw.append(t);
			fw.append(
				backend == Store.Backend.postgresql
				|| backend == Store.Backend.shark ?
					";\n":
					"\n");
			fw.close();
    	  } catch (IOException e) {
    		  e.printStackTrace();
    		  assert false;
    	  }
         }
      else
         {
         SQLExecutor.executeUpdate(conn, t);
         }
      }

   public static void createIndexes(Connection conn, Backend backend, java.util.Properties names, String datasetName )
      {
      String t;
      // create primary indexes
      t = Sqls.getSqls(backend).getSql("indexPG");
      t = t.replaceAll("%s", names.getProperty(Constants.NAME_TABLE_DIRECT_PRIMARY_HASH));
      doSql(conn, backend, t);

      t = Sqls.getSqls(backend).getSql("indexPG");
      t = t.replaceAll("%s", names.getProperty(Constants.NAME_TABLE_REVERSE_PRIMARY_HASH));
      doSql(conn, backend, t);

      t = Sqls.getSqls(backend).getSql("indexPE");
      t = t.replaceAll("%s", names.getProperty(Constants.NAME_TABLE_DIRECT_PRIMARY_HASH));
      doSql(conn, backend, t);

      t = Sqls.getSqls(backend).getSql("indexPE");
      t = t.replaceAll("%s", names.getProperty(Constants.NAME_TABLE_REVERSE_PRIMARY_HASH));
      doSql(conn, backend, t);

      // secondary indexes
      t = Sqls.getSqls(backend).getSql("indexDS");
      t = t.replaceAll("%s", names.getProperty(Constants.NAME_TABLE_DIRECT_SECONDARY_HASH));
      doSql(conn, backend, t);

      t = Sqls.getSqls(backend).getSql("indexRS");
      t = t.replaceAll("%s", names.getProperty(Constants.NAME_TABLE_REVERSE_SECONDARY_HASH));
      doSql(conn, backend, t);

      t = Sqls.getSqls(backend).getSql("indexLidElemDS");
      String sql = t.replaceAll("%s", names.getProperty(Constants.NAME_TABLE_DIRECT_SECONDARY_HASH));
      doSql(conn, backend, sql);

      t = Sqls.getSqls(backend).getSql("indexLidElemRS");
      sql = t.replaceAll("%s", names.getProperty(Constants.NAME_TABLE_REVERSE_SECONDARY_HASH));
      doSql(conn, backend, sql);

      // long string index
      t = Sqls.getSqls(backend).getSql("indexLS");
      t = t.replaceAll("%s", names.getProperty(Constants.NAME_TABLE_LONG_STRINGS));
      doSql(conn, backend, t);
      
      if (backend == Store.Backend.shark) {
    	  //index to replace primary key (pred) of defaultPredInfoTable 
    	  // primary key not supported in hive ql
    	  t = Sqls.getSqls(backend).getSql("indexPREDINFO");
          t = t.replaceAll("%s", datasetName + "_direct");
          doSql(conn, backend, t);
          
          //index to replace primary key (datatype_id, datatype_name) of defaultDataTypeTable 
    	  // primary key not supported in hive ql
          t = Sqls.getSqls(backend).getSql("indexDATATYPE");
          String currentObj = names.getProperty(Constants.NAME_TABLE_DATATYPE);
          t = t.replaceAll("%s", currentObj);
          doSql(conn, backend, t);
      }
      
      }

   public static Store connectStore(Connection conn, Backend backend, String schemaName, String datasetName, Context context)
      {

      StoreImpl store = null;
      String getter = Sqls.getSqls(backend).getSql("getDataset");

      store = new SQLExecutor().getStore(conn, context, getter.replaceFirst("%s", schemaName + "." + datasetName),
           datasetName.toUpperCase());

      if (store != null)
         {
         store.setStoreBackend(backend);
         store.setSchemaName(getSchema(conn, schemaName));
         ensureExistenceOfUserTablespaceAndBuiltInRecursiveStoreProcedure(conn, store);
         }

      return store;
      }

   public static void deleteStore(Connection conn, Backend backend, String schemaName, String datasetName, Context context)
      {
      String curTable = new String();

      Store s = connectStore(conn, backend, schemaName, datasetName, context);
      if (s == null)
         {
         System.err.println("Failed to get connection");
         return;
         }

      //
      // drop tables
      //
      curTable = s.getDirectPrimary();
      doSql(conn, backend, "drop table " + curTable);

      curTable = s.getDirectSecondary();
      doSql(conn, backend, "drop table " + curTable);

      curTable = s.getReversePrimary();
      doSql(conn, backend, "drop table " + curTable);

      curTable = s.getReverseSecondary();
      doSql(conn, backend, "drop table " + curTable);

      curTable = s.getLongStrings();
      doSql(conn, backend, "drop table " + curTable);

      curTable = s.getBasicStatsTable();
      doSql(conn, backend, "drop table " + curTable);

      curTable = s.getTopKStatsTable();
      doSql(conn, backend, "drop table " + curTable);

      doSql(conn, backend, "drop table " + datasetName + "_DIRECT_PREDS");

      doSql(conn, backend, "drop table " + datasetName + "_REVERSE_PREDS");

      if (s.getDataTypeTable() != null)
         {
         curTable = s.getDataTypeTable();
         doSql(conn, backend, "drop table " + s.getDataTypeTable());
         }
      curTable = datasetName;
      doSql(conn, backend, "drop table " + datasetName);

      if (s.getDataTypeTable() != null)
         {

         doSql(conn, backend, "drop sequence " + s.getDataTypeTable() + "_type_seq");
         doSql(conn, backend, "drop sequence " + s.getDataTypeTable() + "_lang_seq");
         }

      if (backend == Backend.postgresql)
         {
         doSql(conn, backend, "drop sequence " + datasetName + "_entry_id_seq");
         }
      }

   private static void setupRunStatsProfile(Connection conn, Backend backend, String dphName, String dsName, String rphName,
         String rsName, String lstrName, int bucketSizeDPH, int bucketSizeRPH, String schema)
      {

      if (backend == Backend.postgresql)
         {
         String dbRunStatsSql = Sqls.getSqls(backend).getSql("dphRphRunStats");
         dbRunStatsSql = dbRunStatsSql.replaceFirst("%s", schema);
         dbRunStatsSql = dbRunStatsSql.replaceFirst("%t", dphName);
         SQLExecutor.executeCall(conn, dbRunStatsSql);
         }
      if (backend == Backend.db2)
         {
         String dbRunStatsSql = Sqls.getSqls(backend).getSql("dphRphRunStats");
         dbRunStatsSql = dbRunStatsSql.replaceFirst("%s", schema);

         StringBuffer dphBuf = new StringBuffer();
         int i = 0;
         for (; (i < bucketSizeDPH - 1); i++)
            {
            dphBuf.append(" prop" + i + ",");
            }
         dphBuf.append(" prop" + i);
         // now rph
         StringBuffer rphBuf = new StringBuffer();
         for (i = 0; (i < bucketSizeRPH - 1); i++)
            {
            rphBuf.append(" prop" + i + ",");
            }
         rphBuf.append(" prop" + i);

         String table = dphName;

         // DPH
         String dph = dbRunStatsSql.replaceFirst("%t", table);
         dph = dph.replaceFirst("%c", dphBuf.toString());
         SQLExecutor.executeCall(conn, "CALL ADMIN_CMD(?)", dph);

         // RPH
         table = rphName;
         String rph = dbRunStatsSql.replaceFirst("%t", table);
         rph = rph.replaceFirst("%c", rphBuf.toString());
         SQLExecutor.executeCall(conn, "CALL ADMIN_CMD(?)", rph);

         // DS
         dbRunStatsSql = Sqls.getSqls(backend).getSql("dsRsLsRunStats");
         dbRunStatsSql = dbRunStatsSql.replaceFirst("%s", schema);
         table = dsName;
         SQLExecutor.executeCall(conn, "CALL ADMIN_CMD(?)", dbRunStatsSql.replaceFirst("%t", table));

         // RS
         table = rsName;
         SQLExecutor.executeCall(conn, "CALL ADMIN_CMD(?)", dbRunStatsSql.replaceFirst("%t", table));

         // LSTR
         table = lstrName;
         SQLExecutor.executeCall(conn, "CALL ADMIN_CMD(?)", dbRunStatsSql.replaceFirst("%t", table));

         // TURN ON AUTORUNSTATS
         SQLExecutor.executeCall(conn, "CALL ADMIN_CMD(?)", Sqls.getSqls(backend).getSql("setAutoStats"));

         }

      }

   private static Properties generateInternalNames(String datasetName)
      {

      // String ds = datasetName;
      if (datasetName.length() > TBLNAMELEN)
         datasetName = datasetName.substring(0, TBLNAMELEN);

      // not fool proof... but
      Properties tableNames = new Properties();

      tableNames.setProperty(Constants.NAME_TABLE_DIRECT_PRIMARY_HASH, datasetName + "_DPH");
      tableNames.setProperty(Constants.NAME_TABLE_DIRECT_SECONDARY_HASH, datasetName + "_DS");
      tableNames.setProperty(Constants.NAME_TABLE_REVERSE_PRIMARY_HASH, datasetName + "_RPH");
      tableNames.setProperty(Constants.NAME_TABLE_REVERSE_SECONDARY_HASH, datasetName + "_RS");
      tableNames.setProperty(Constants.NAME_TABLE_LONG_STRINGS, datasetName + "_LSTR");
      tableNames.setProperty(Constants.NAME_TABLE_BASIC_STATS, datasetName + "_BASESTATS");
      tableNames.setProperty(Constants.NAME_TABLE_TOPK_STATS, datasetName + "_TOPKSTATS");
      tableNames.setProperty(Constants.NAME_TABLE_DATATYPE, datasetName + "_DT");
      return tableNames;
      }

   public static boolean setupAutoCommit(Connection conn)
      {
      try
         {
         if (conn.getAutoCommit())
            {
            conn.setAutoCommit(false);
            return true;
            }
         }
      catch (SQLException e)
         {
         }

      return false;
      }

   public static void releaseAutoCommit(Connection conn, boolean changedAutoCommit)
      {
      if (changedAutoCommit)
         {

         try
            {
            conn.commit();
            conn.setAutoCommit(true);
            }
         catch (SQLException e)
            {
            }
         }
      }

   public static void createSchema(Connection conn, Backend backend, String schemaName)
      {
      if (backend == Store.Backend.postgresql 
    	|| backend == Store.Backend.shark)
         {
         doSql(conn, backend, "CREATE SCHEMA IF NOT EXISTS " + schemaName);
         }
      else
         {
         //
         // Check whether the schema already exists in db2
         //
         String schemaSQL = "SELECT SCHEMANAME FROM SYSCAT.SCHEMATA WHERE SCHEMANAME = UPPER('" + schemaName + "')";

         String db2SchemaName = new SQLExecutor().executeQuery(conn, schemaSQL, new SingleRowResultSetProcessor<String>()
            {
               public String processRow(Connection conn, ResultSet rs) throws SQLException
                  {
                  return rs.getString(1);
                  }
            });

         //
         // if it doesn't, create it
         //
         if (db2SchemaName == null)
            {
            doSql(conn, backend, "CREATE SCHEMA " + schemaName);
            }
         }

      }

   public static void setSchema(Connection conn, Backend backend, String schemaName)
      {
      if (schemaName != null && schemaName.length() > 0)
         {
         if (backend == Store.Backend.postgresql)
            {
            doSql(conn, backend, "SET SCHEMA '" + schemaName + "'");
            }
         else if (backend == Store.Backend.db2)
            {
            doSql(conn, backend, "SET SCHEMA " + schemaName);
            }
         else if (backend == Store.Backend.shark)
         {
        	 //Do nothing
         }
         }
      }

   public static void setPath(Connection conn, String schemaName)
      {

      if (schemaName != null && schemaName.length() > 0)
         {
         try
            {

            // set even the CURRENT PATH... this is for UDF's
            SQLExecutor.executeUpdate(conn, "set current path = " + schemaName + ",current path");
            }
         catch (SQLExceptionWrapper e)
            {
            SQLException e1 = (SQLException) e.getCause();
            if (e1.getErrorCode() == -585)
               {
               // duplicate schema, no action required
               return;
               }
            throw new RdfStoreException(e1.getLocalizedMessage(), e1);
            }

         }
      }

   public static String getSchema(Connection conn, String schemaName)
      {

      if (schemaName != null && schemaName.trim().length() > 0)
         {
         return schemaName;
         }

      return new SQLExecutor().executeQuery(conn, "select current schema from sysibm.sysdummy1",
            new SingleRowResultSetProcessor<String>()
               {
                  public String processRow(Connection conn, ResultSet rs) throws SQLException
                     {
                     return rs.getString(1).trim();

                     }

               });
      }

   public static void createFunctions(Connection conn, Backend backend)
      {
      String functionString = Sqls.getSqls(backend).getSql("typeofFuntion");

      doSql(conn, backend, functionString);
      }
   }
