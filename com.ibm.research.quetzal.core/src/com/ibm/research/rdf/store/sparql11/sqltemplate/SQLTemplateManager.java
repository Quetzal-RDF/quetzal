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
 package com.ibm.research.rdf.store.sparql11.sqltemplate;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.SequenceInputStream;
import java.util.Set;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

import com.ibm.research.rdf.store.Store;

public class SQLTemplateManager
   {
   static StringTemplateGroup sql;

   public static void setStoreTemplate(Store store)
      {
      try
         {
         InputStream configStream = null;
         if (store.getStoreBackend().equalsIgnoreCase(Store.Backend.db2.name()))
            {
            configStream = SQLTemplateManager.class.getResourceAsStream("DB2SQLTemplates");
            }
         else if (store.getStoreBackend().equalsIgnoreCase(Store.Backend.postgresql.name()))
            {
            configStream = SQLTemplateManager.class.getResourceAsStream("PSQLSQLTemplates");
            }
         else if (store.getStoreBackend().equalsIgnoreCase(Store.Backend.shark.name())) {
        	 configStream = SQLTemplateManager.class.getResourceAsStream("SharkSQLTemplates");
         }
         
         SequenceInputStream s = new SequenceInputStream(SQLTemplateManager.class.getResourceAsStream("common.stg"), configStream);
         BufferedReader configReader = new BufferedReader(new InputStreamReader(s, "UTF-8"));
         sql = new StringTemplateGroup(configReader);
         }
      catch (Exception e)
         {
         e.printStackTrace();
         new RuntimeException(e);
         }
      }

   public static <T> String getSQLString(String templateName, Set<SQLMapping> mappings)
      {
      StringTemplate t = sql.getInstanceOf(templateName);
      if (mappings != null) {
	      for (SQLMapping m : mappings)
	      {
	    	  t.setAttribute(m.getName(), m.getValues());
	      }
      }
      return t.toString();
      }
   }
