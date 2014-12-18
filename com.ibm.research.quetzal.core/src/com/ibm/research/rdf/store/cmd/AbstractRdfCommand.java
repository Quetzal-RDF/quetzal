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
 package com.ibm.research.rdf.store.cmd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.jena.RdfStoreException;

public abstract class AbstractRdfCommand
   {

   public static final Log       log         = LogFactory.getLog(AbstractRdfCommand.class);

   protected Set<String>         validParams = new HashSet<String>();
   protected Map<String, String> params      = new HashMap<String, String>();
   protected Set<String>         reqParams   = new HashSet<String>();

   protected String              storeName   = null;

   protected String              cmdUsageKey = null;
   protected String              cmdString   = null;

   public void parseArguments(String[] args)
      {

      if (args.length < 2)
         {
         return;
         }

      for (int i = 1; i < args.length; i += 2)
         {
         if ((i + 1) >= args.length)
            {
            break;
            }
         if (!(args[i].startsWith("-")))
            {
            throw new IllegalArgumentException("argument doesn't start with a '-'" + args[i]);
            }
         params.put(args[i], args[i + 1]);
         }

      }

   public boolean validateParameters()
      {

      validParams.add("-backend");
      validParams.add("-host");
      validParams.add("-port");
      validParams.add("-schema");
      validParams.add("-db");
      validParams.add("-user");
      validParams.add("-password");

      reqParams.add("-db");
      reqParams.add("-user");
      reqParams.add("-password");

      for (String s : reqParams)
         {
         if (!params.containsKey(s))
            {
            return false;
            }
         }

      for (String s : params.keySet())
         {
         if (!validParams.contains(s))
            {
            return false;
            }
         }

      return true;
      }

   private Connection getConnection()
      {

      String backend = params.get("-backend");
      String db = params.get("-db");
      String port = params.get("-port");
      String host = params.get("-host");
      String user = params.get("-user");
      String password = params.get("-password");
      if (backend == null)
         backend = Store.Backend.db2.name();
      if (host == null)
         host = "localhost";
      if (port == null)
         port = "50000";

      String JDBC_URL;
      String classNameString;
      if (backend.equalsIgnoreCase(Store.Backend.postgresql.name()))
         {
         JDBC_URL = new String("jdbc:postgresql://" + host + ":" + port + "/" + db);
         classNameString = new String("org.postgresql.Driver");
         }
      else if  (backend.equalsIgnoreCase(Store.Backend.shark.name())) {
    	  JDBC_URL = new String("jdbc:hive2://" + host + ":" + port + "/" + db);
          classNameString = new String("org.apache.hive.jdbc.HiveDriver");
      }
      else
         {
         JDBC_URL = new String("jdbc:db2://" + host + ":" + port + "/" + db);
         classNameString = new String("com.ibm.db2.jcc.DB2Driver");
         }

      try
         {
    	 Class.forName(classNameString); 
         Connection conn = DriverManager.getConnection(JDBC_URL, user, password);
         if (conn != null )
            {
        	 if (!backend.equalsIgnoreCase(Store.Backend.shark.name()))
        	 {
	            // this will cause StoreManager to start a txn
	            // and commit on success, which is good for us
	            conn.setAutoCommit(true);
        	 }
            }
         else
            {
            System.err.println("Failed to establish connection to the database");
            }
         return conn;
         }
      catch (ClassNotFoundException e)
         {
         System.err.println(e.getLocalizedMessage());
         return null;
         }
      catch (SQLException e)
         {
         System.err.println(e.getLocalizedMessage());
         return null;
         }

      // + ":traceFile=c:/jcc.log;traceLevel=-1;traceFileAppend=false;" ;

      }

   private void closeConnection(Connection conn)
      {
      try
         {
         if (conn != null)
            {
            // safe to rollback since storeManager commits on success
            // when connection is autoCommit, which is what the commandline
            // utils do
            conn.rollback();
            conn.close();
            }
         }
      catch (SQLException e)
         {
         }
      }

   public void runCmd(String[] args, String cmdString, String cmdUsageKey)
      {
	   System.err.println("In runCmd ...");
      this.cmdUsageKey = cmdUsageKey;
      this.cmdString = cmdString;
      try
         {
    	 System.err.println("Args: "+Arrays.toString(args));
         parseArguments(args);
         System.err.println("Param: "+params);
         
         }
      catch (RdfStoreException e)
         {
         System.out.println(e.getLocalizedMessage());
         printUsage();
         return;
         }

      if (args.length == 0 || args[0] == null)
         {
         printUsage();
         return;
         }
      storeName = args[0];

      if (storeName.startsWith("\""))
         {
         System.out.println("StoreName does not support special characters");
         printUsage();
         return;
         }

      if (!validateParameters())
         {
         printUsage();
         return;
         }
      System.err.println("Establishing connection ...");
      Connection conn = getConnection();
      if (conn == null)
         return;
      System.err.println("Connection established!");
      System.err.println("Doing work ...");
      doWork(conn);
      closeConnection(conn);

      }

   public abstract void doWork(Connection conn);

   public void printUsage()
      {
	   System.err.println("Error: PRINT USAGE!");
      }

   }
