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
import java.util.Collection;
import java.util.List;

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
         if (store.getStoreBackend() == Store.Backend.db2)
            {
            configStream = SQLTemplateManager.class.getResourceAsStream("DB2SQLTemplates");
            }
         else if (store.getStoreBackend() == Store.Backend.postgresql)
            {
            configStream = SQLTemplateManager.class.getResourceAsStream("PSQLSQLTemplates");
            }
         else if (store.getStoreBackend() == Store.Backend.shark) {
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

   public static String getSQLString(List<String> templateNames, Collection<SQLMapping> collection)
      {
	   StringBuffer result = new StringBuffer();
	   for (String templateName : templateNames) {
		   StringTemplate t = sql.getInstanceOf(templateName);
		   if (collection != null) {
			   for (SQLMapping m : collection) {
				   if (t.getFormalArgument(m.getName()) != null) {
					   t.setAttribute(m.getName(), m.getValues());
				   }
			   }
		   }
		   result.append(t.toString());
	   }
	   return result.toString();
      }
   }
