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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibm.research.rdf.store.Store.Backend;
import com.ibm.research.rdf.store.StoreManager;
import com.ibm.research.rdf.store.jena.RdfStoreException;

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
         StoreManager.createStoreWithPredicateMappings(conn, Backend.valueOf(params.get("-backend")), params.get("-schema"), storeName,
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
