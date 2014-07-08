package com.ibm.rdf.store.cmd;

import java.sql.Connection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibm.rdf.store.StoreManager;
import com.ibm.rdf.store.jena.RdfStoreException;

public class CreateRdfStore extends AbstractRdfCommand
   {

   private static final Log log = LogFactory.getLog(CreateRdfStore.class);

   public static void main(String[] args)
      {
      AbstractRdfCommand cmd = new CreateRdfStore();
      cmd.runCmd(args, "createrdfstore", "CREATERDFSTORE");
      }

   @Override
   public boolean validateParameters()
      {
      validParams.add("-predicateMappings");

      return super.validateParameters();
      }

   @Override
   public void doWork(Connection conn)
      {
      try
         {
         //
         // check whether predicate mappings are passed as parameters
         //
         String predicateMappings = null;
         if (params.get("-predicateMappings") != null)
            {
            predicateMappings = params.get("-predicateMappings");
            }

         //
         // create the store.
         //
         StoreManager.createStoreWithPredicateMappings(conn, params.get("-backend"), params.get("-schema"), storeName,
               predicateMappings, params.get("-tablespace"));
         }
      catch (RdfStoreException e)
         {
         log.error(e);
         System.out.println(e.getLocalizedMessage());
         }
      catch (Exception e)
         {
         log.error(e);
         e.printStackTrace(System.err);
         System.out.println(e.getLocalizedMessage());
         }
      }

   }
