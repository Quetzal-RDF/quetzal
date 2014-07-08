package com.ibm.rdf.store;

import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Set;

import com.ibm.rdf.store.Store.Db2Type;
import com.ibm.rdf.store.Store.PredicateTable;
import com.ibm.rdf.store.config.Constants;
import com.ibm.rdf.store.config.Statistics;
import com.ibm.rdf.store.jena.impl.DB2CloseObjects;
import com.ibm.rdf.store.runtime.service.sql.StoreHelper;
import com.ibm.rdf.store.runtime.service.sql.StoreImpl;
import com.ibm.rdf.store.runtime.service.types.TypeMap;
import com.ibm.rdf.store.statistics.DbBasedPredicateMappingMgr;
import com.ibm.rdf.store.statistics.DbBasedStatisticsMgr;
import com.ibm.wala.util.collections.HashMapFactory;
import com.ibm.wala.util.collections.HashSetFactory;

public class StoreManager
   {

   /**
    * Creates a TripleStore/dataset in the RDFStore. This API only creates i.e. if dataset already exists will throw exception
    * 
    * @connection identifies the database and schema in which to create the tripleStore
    * @schemaName DB Schema in which to create the dataset
    * @storeName a unique identifier for the dataset in the DB/Schema
    * @TableMapper used to provide names to be used for the DB tables if required
    * @predicateMappings predicate to column mappings
    * @tableSpaceName TableSpace in which to create the tables
    * @return Store object
    */
   public static Store createStoreWithPredicateMappings(Connection conn, String backend, String schemaName, String storeName,
         String predicateMappings, String tableSpaceName)
      {
      //
      // If the schema does not exist in the database, we need to create it
      //
      StoreHelper.createSchema(conn, backend, schemaName);

      //
      // Set the database to the current schema
      //
      StoreHelper.setSchema(conn, backend, schemaName);

      boolean changedAutoCommit = false;
      if (!Store.Backend.shark.name().equalsIgnoreCase(backend)) {
    	    changedAutoCommit = StoreHelper.setupAutoCommit(conn);
      }
      //
      // There is a typeof function that is used in the DDL and must be loaded at this point
      //
      if (!Store.Backend.shark.name().equalsIgnoreCase(backend)) {
    	  StoreHelper.createFunctions(conn, backend);
      }

      if (StoreHelper.checkProperty("com.ibm.rdf.createStore"))
         {
         StoreHelper.createRdfStoreTable(conn, backend, schemaName, storeName, tableSpaceName, predicateMappings);
         }

      Store store = StoreHelper.createDefaultStore(conn, backend, schemaName, storeName, predicateMappings,
            tableSpaceName);
      if (!Store.Backend.shark.name().equalsIgnoreCase(backend)) {
    	  StoreHelper.releaseAutoCommit(conn, changedAutoCommit);
      }
      return store;
      }

   private static class PredicateTableImpl implements PredicateTable
      {
      private final Set<String>          hasSpills = HashSetFactory.make();
      private final Set<String>          oneToOne  = HashSetFactory.make();
      private final Map<String, Db2Type> types     = HashMapFactory.make();
      private final Map<String, int[]>   hashes    = HashMapFactory.make();

      @Override
      public boolean hasSpills(String predicate)
         {
         return hasSpills.contains(predicate);
         }

      @Override
      public boolean hasSpills()
         {
         return !hasSpills.isEmpty();
         }

      @Override
      public boolean isOneToOne(String predicate)
         {
         return oneToOne.contains(predicate);
         }

      @Override
      public Db2Type getType(String predicate)
         {
         return types.get(predicate);
         }

      @Override
      public int getHashCount(String predicate)
         {
         if (hashes.containsKey(predicate))
            {
            return hashes.get(predicate).length;
            }
         else
            {
            return 0;
            }
         }

      @Override
      public int[] getHashes(String predicate)
         {
         return hashes.get(predicate);
         }

      private void setPredicate(String predicate, boolean hasSpill, boolean oneToOne, String type, int[] hashes)
         {
         if (hasSpill)
            {
            this.hasSpills.add(predicate);
            }
         if (oneToOne)
            {
            this.oneToOne.add(predicate);
            }
         this.types.put(predicate, Db2Type.valueOf(type.toUpperCase()));
         this.hashes.put(predicate, hashes);
         }
      }

   /**
    * Connect to an existing RDFStore. If Store doesn't exist, raises exception
    * 
    * @connection identifies the database and schema in which the tripleStore exists
    * @datasetName a unique identifier for the dataset in the DB/Schema
    * @return Store object
    */
   public static Store connectStore(Connection conn, String backend, String schemaName, String storeName, Context context)
      {

      StoreImpl store = (StoreImpl) initialConnect(conn, backend, schemaName, storeName, context);

      PreparedStatement stmt = null;
      ResultSet rs = null;

      if (store.getDataTypeTable() != null)
         {
         String sql = "SELECT * FROM " + store.getDataTypeTable();
         try
            {
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next())
               {
               TypeMap.add(rs.getString(Constants.NAME_COLUMN_DATATYPE_NAME), rs.getShort(Constants.NAME_COLUMN_DATATYPE_ID));
               }
            }
         catch (SQLException e)
            {
            e.printStackTrace();
            }
         finally
            {
            DB2CloseObjects.close(rs, stmt);
            }
         }

      // load the stats
      Statistics[] stats = new DbBasedStatisticsMgr(conn, backend, store).loadStatsFromSchema();
      store.setPerGraphStatistics(stats[0]);
      store.setOverallStatistics(stats[1]);

      // load direct predicate data
      try
         {
         Statement s = conn.createStatement();

         PredicateTableImpl dt = readPredicateInfo(storeName, s, "_DIRECT_PREDS");
         store.setDirectPredicates(dt);

         PredicateTableImpl rt = readPredicateInfo(storeName, s, "_REVERSE_PREDS");
         store.setReversePredicates(rt);

         s.close();
         }
      catch (SQLException e)
         {
         // TODO Auto-generated catch block
         e.printStackTrace();
         }

      return store;

      /*
       * Exceptions No permissions to read Config table Dataset does not exist. Store metadata corrupted.
       */
      }

   private static PredicateTableImpl readPredicateInfo(String storeName, Statement s, String table) throws SQLException
      {
      PredicateTableImpl dt = new PredicateTableImpl();
      ResultSet dp_rs = s.executeQuery("select * from " + storeName + table);
      while (dp_rs.next())
         {
         int[] hashes = new int[dp_rs.getInt("num_hashes")];
         for (int i = 0; i < hashes.length; i++)
            {
            hashes[i] = dp_rs.getInt("hash" + i);
            }
         dt.setPredicate(dp_rs.getString("pred"), dp_rs.getBoolean("spills"), dp_rs.getBoolean("onetoone"),
               dp_rs.getString("db2type"), hashes);
         }
      dp_rs.close();
      return dt;
      }

   /**
    * Updates the required statistics to reflect current data in the store.
    * 
    * @connection identifies the database and schema in which the tripleStore exists
    * @datasetName a unique identifier for the dataset in the DB/Schema
    */
   public static void runStats(Connection conn, String backend, String schemaName, String storeName, Context context)
      {

      // update the internal statistics maintained as well as run
      // distribution stats on the required columns in the DB

      Store store = initialConnect(conn, backend, schemaName, storeName, context);

      // probably want to avoid thread race issues here
      boolean changedAutoCommit = StoreHelper.setupAutoCommit(conn);
      DbBasedStatisticsMgr stats = new DbBasedStatisticsMgr(conn, backend, store);
      stats.populateStatsSchema();
      StoreHelper.releaseAutoCommit(conn, changedAutoCommit);
      // stats.runDbEngineStatsCollection();

      /*
       * Exceptions No permissions to read Config table Dataset does not exist. Store metadata corrupted. No permissions to read tables
       */
      }

   /**
    * Calculates the Predicate and column mapping based on predicate co-occurence
    * 
    * @predicateMappingFile Location & Name of nQuad file where predicateMapping is to be stored.
    */
   public static void generatePredicateMappings(Connection conn, String backend, String schemaName, String storeName,
         OutputStream predicateMappings, Context context)
      {

      Store store = initialConnect(conn, backend, schemaName, storeName, context);
      new DbBasedPredicateMappingMgr(store, conn).createPredicateMappings(predicateMappings);
      }

   /**
    * 
    * @connection identifies the database and schema in which the tripleStore exists
    * @datasetName a unique identifier for the dataset in the DB/Schema
    */
   public static void dropRDFStore(Connection conn, String backend, String schemaName, String storeName, Context context)
      {

      StoreHelper.setSchema(conn, backend, schemaName);

      boolean changedAutoCommit = StoreHelper.setupAutoCommit(conn);

      StoreHelper.deleteStore(conn, backend, schemaName, storeName, context);

      StoreHelper.releaseAutoCommit(conn, changedAutoCommit);

      /*
       * Exceptions No delete permission Dataset does not exist.
       */
      }

   private static Store initialConnect(Connection conn, String backend, String schemaName, String storeName, Context context)
      {
      StoreHelper.setSchema(conn, backend, schemaName);
      Store store = StoreHelper.connectStore(conn, backend, schemaName, storeName, context);

      if (store == null)
         {
         return null;
         }
      System.out.println("Connection established");
      return store;
      }

   }
