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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.ibm.research.rdf.store.Context;
import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.Store.Backend;
import com.ibm.research.rdf.store.StoreManager;
import com.ibm.research.rdf.store.jena.RdfStoreException;
import com.ibm.research.rdf.store.jena.RdfStoreFactory;
import com.ibm.research.rdf.store.jena.RdfStoreQueryExecutionFactory;
import com.ibm.research.rdf.store.jena.RdfStoreQueryFactory;

public class QueryRdfStore extends ShowPredicateColumns
   {

   private static final Log log = LogFactory.getLog(QueryRdfStore.class);

   public static void main(String[] args)
      {

      AbstractRdfCommand cmd = new QueryRdfStore();
      cmd.runCmd(args, "queryrdfstore", null);

      }

   @Override
   public boolean validateParameters()
      {
      validParams.add("-uniondefaultgraph");
      return super.validateParameters();
      }

   @Override
   public void doWork(Connection conn)
      {

      if (finalArg == null)
         {
         System.out.println("Query not specified");
         printUsage();
         return;
         }

      String query = null;
      // check if this is a file or query String
      File file = new File(finalArg);
      if (file.exists())
         {
         StringBuffer queryBuffer = new StringBuffer();
         try
            {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = null;

            while ((line = br.readLine()) != null)
               {
               queryBuffer.append(line);
               }
            br.close();
            }
         catch (IOException e)
            {
            System.out.println(e.getLocalizedMessage());
            printUsage();
            return;
            }
         query = queryBuffer.toString();
         }
      else
         {
         query = finalArg;
         }

      try
         {
         Store store = StoreManager.connectStore(conn, Backend.valueOf(params.get("-backend")), params.get("-schema"), storeName,
               Context.defaultContext);
         String unionDef = params.get("-uniondefaultgraph");
         if (unionDef != null && unionDef.equalsIgnoreCase("true"))
            {
            store.getContext().set(Context.unionDefaultGraph, true);
            }

         Dataset ds = RdfStoreFactory.connectDataset(store, conn, Backend.valueOf(params.get("-backend")));
         Query q = RdfStoreQueryFactory.create(query);
         QueryExecution qe = RdfStoreQueryExecutionFactory.create(q, ds);

         long rows = 0;
         Model m = null;
         long start = System.currentTimeMillis();

         if (q.isSelectType())
            {
            ResultSet rs = qe.execSelect();
            while (rs.hasNext())
               {
               QuerySolution qs = rs.next();
               System.out.println(qs);
               System.out.println();
               rows++;
               }
            }
         else if (q.isDescribeType())
            {
            m = qe.execDescribe();
            m.write(System.out, "N-TRIPLE");
            }
         else if (q.isAskType())
            {
            System.out.println(qe.execAsk());

            }
         else if (q.isConstructType())
            {
            m = qe.execConstruct();
            m.write(System.out, "N-TRIPLE");
            }

         long end = System.currentTimeMillis();
         qe.close();
         if (m != null)
            {
            System.out.println("Number of Rows  : " + m.size());
            m.close();
            }
         else
            {
            System.out.println("Number of Rows  : " + rows);
            }
         System.out.println("Time taken      : " + (end - start) + " ms");
         }
      catch (RdfStoreException e)
         {
         log.error(e);
         System.out.println(e.getLocalizedMessage());
         }
      catch (Exception e)
         {
         log.error(e);
         System.out.println(e.getLocalizedMessage());
         }

      }

   @Override
   public void printUsage()
      {
      }

   }
