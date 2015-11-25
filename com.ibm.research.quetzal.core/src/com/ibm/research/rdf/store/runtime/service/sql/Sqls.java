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

import java.io.IOException;
import java.io.InputStream;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.Store.Backend;

public class Sqls extends Properties
   {

   /**
	 * 
	 */
   private static final long serialVersionUID = 1L;
   private static Sqls       props            = null;

   public static Sqls getSqls(Backend backend)
      {

      if (props == null)
         {
         props = new Sqls();
         InputStream in;

         if (backend == Store.Backend.postgresql)
            {
            in = props
                  .getClass()
                  .getClassLoader()
                  .getResourceAsStream(
                        "com/ibm/research/rdf/store/runtime/service/sql/PostgreSQLRdfStoreSqls");
            }
         else if (backend == Store.Backend.db2)
            {
            in = props
                  .getClass()
                  .getClassLoader()
                  .getResourceAsStream(
                        "com/ibm/research/rdf/store/runtime/service/sql/DB2RdfStoreSqls");
            }
         else
         { 
        	 assert backend == Store.Backend.shark : "Unsupported backend: "+backend;
        	 in = props
                     .getClass()
                     .getClassLoader()
                     .getResourceAsStream(
                           "com/ibm/research/rdf/store/runtime/service/sql/Hive2RdfStoreSqls");
         }
         try
            {
            props.load(in);
            }
         catch (InvalidPropertiesFormatException e)
            {
            e.printStackTrace();
            }
         catch (IOException e)
            {
            e.printStackTrace();
            }
         }

      return props;
      }

   private Sqls()
      {

      }

   public String getSql(String name)
      {
      return super.getProperty(name);
      }
   }
