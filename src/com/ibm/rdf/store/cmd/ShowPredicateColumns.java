package com.ibm.rdf.store.cmd;

import java.sql.Connection;

import com.ibm.rdf.store.Context;
import com.ibm.rdf.store.Store;
import com.ibm.rdf.store.StoreManager;
import com.ibm.rdf.store.jena.RdfStoreException;

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

      Store store = StoreManager.connectStore(conn, params.get("-backend"), params.get("-schema"), storeName,
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
