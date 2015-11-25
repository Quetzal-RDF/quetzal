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

import com.ibm.research.rdf.store.Context;
import com.ibm.research.rdf.store.Store.Backend;
import com.ibm.research.rdf.store.StoreManager;
import com.ibm.research.rdf.store.jena.RdfStoreException;

public class DropRdfStore extends AbstractRdfCommand
   {

   private static final Log log = LogFactory.getLog(DropRdfStore.class);

   public static void main(String[] args)
      {
      AbstractRdfCommand cmd = new DropRdfStore();
      cmd.runCmd(args, "droprdfstore", null);
      }

   @Override
   public void doWork(Connection conn)
      {
      try
         {
         StoreManager.dropRDFStore(conn, Backend.valueOf(params.get("-backend")), params.get("-schema"), storeName, Context.defaultContext);
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

   }
