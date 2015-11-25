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

import com.ibm.research.rdf.store.Context;
import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.StoreManager;
import com.ibm.research.rdf.store.Store.Backend;

public class ShowPredicateColumns extends AbstractRdfCommand
   {

   protected String finalArg = null;

   public static void main(String[] args)
      {

      AbstractRdfCommand cmd = new ShowPredicateColumns();
      cmd.runCmd(args, "displaypredicatecolumns", "DISPLAYPREDCOL");

      }

   public void parseArguments(String[] args)
      {

      if (args.length < 2)
         {
         return;
         }

      for (int i = 1; i < args.length; i += 2)
         {
         /*
          * if ((i + 1) >= args.length) { break; }
          */

         if (i + 1 >= args.length)
            {
            // last param which is the predicate
            finalArg = args[i];
            return;
            }

         if (!(args[i].startsWith("-")))
            {
            throw new IllegalArgumentException("argument doesn't start with a '-'");
            }
         params.put(args[i], args[i + 1]);
         }

      }

   @Override
   public void doWork(Connection conn)
      {

      if (finalArg == null)
         {
         System.out.println("Predicate not specified");
         printUsage();
         return;
         }

      Store store = StoreManager.connectStore(conn, Backend.valueOf(params.get("-backend")), params.get("-schema"), storeName,
            Context.defaultContext);

      int columns[] = store.getDirectPredicates().getHashes(finalArg);
      System.out.println("Columns for predicate in SPO table is :");
      for (int i = 0; i < 3; i++)
         {
         System.out.println(columns[i]);
         }

      columns = store.getReversePredicates().getHashes(finalArg);
      System.out.println("Columns for predicate in OPS table is :");
      for (int i = 0; i < 3; i++)
         {
         System.out.println(columns[i]);
         }

      }
   }
