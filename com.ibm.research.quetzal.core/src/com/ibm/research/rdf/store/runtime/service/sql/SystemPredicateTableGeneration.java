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

import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.Store.Backend;
import com.ibm.research.rdf.store.config.Constants;

public class SystemPredicateTableGeneration
   {

   public static final int MAX_TABLE_SIZE   = 32677;

   private int             actualBucketSize = -1;

   public void setActualBucketSize(int actualBucketSize)
      {
      this.actualBucketSize = actualBucketSize;
      }

   public int getActualBucketSize()
      {
      return actualBucketSize;
      }

   public String getDirectPrimaryTableStatement(Backend backend, int gidLenght, int colLenght, int maxBucketNumber)
      {
      int length = 0;
      StringBuffer sql = new StringBuffer();
      boolean isSharkBackend= backend == Backend.shark;
      sql.append("CREATE TABLE %s(");
      sql.append(Constants.NAME_COLUMN_ENTRY + (isSharkBackend? " STRING, ": " VARCHAR(" + colLenght + ") NOT NULL, "));
      sql.append(Constants.NAME_COLUMN_GRAPH_ID +(isSharkBackend? " STRING, ": " VARCHAR(" + gidLenght + ") NOT NULL, "));
      sql.append(Constants.NAME_COLUMN_SPILL + (isSharkBackend?" INT, ":" INTEGER DEFAULT 0, "));
     
      length += colLenght * 2 + 32;
      int bucketSize = (MAX_TABLE_SIZE - length - gidLenght - colLenght) / ((colLenght * 2) + 8);

      actualBucketSize = bucketSize < (maxBucketNumber) ? bucketSize : maxBucketNumber;

      for (int i = 0; i < actualBucketSize; i++)
         {
         if (i > 0)
            sql.append(", ");
         sql.append(Constants.NAME_COLUMN_PREFIX_PREDICATE + i + (isSharkBackend? " STRING, ":" VARCHAR(" + colLenght + "), "));
         sql.append(Constants.NAME_COLUMN_PREFIX_VALUE + i +(isSharkBackend? " STRING, ": " VARCHAR(" + colLenght + "),"));
         sql.append(Constants.NAME_COLUMN_PREFIX_TYPE + i + " smallint ");
         }

      if (backend == Store.Backend.db2)
         {
         sql.append(") VALUE COMPRESSION COMPRESS YES NOT LOGGED INITIALLY");
         }
      else
         {
         sql.append(")");
         }
      if (isSharkBackend) {
    	  sql.append(" ROW FORMAT DELIMITED FIELDS TERMINATED BY '\\t'");
    	 // sql.append("CACHE TABLE %s");
      }
      return sql.toString();
      }

   public String getReversePrimaryTableStatement(Backend backend, int gidLenght, int colLenght, int maxBucketNumber)
      {
      int length = 0;
      StringBuffer sql = new StringBuffer();
      boolean isSharkBackend= (backend == Store.Backend.shark);
      
      sql.append("CREATE TABLE %s(");
      sql.append(Constants.NAME_COLUMN_ENTRY +(isSharkBackend? " STRING, ":  " VARCHAR(" + colLenght + ") NOT NULL, "));
      sql.append(Constants.NAME_COLUMN_GRAPH_ID +(isSharkBackend? " STRING, ":  " VARCHAR(" + gidLenght + ") NOT NULL, "));
      sql.append(Constants.NAME_COLUMN_SPILL +  (isSharkBackend?" INT, ":" INTEGER DEFAULT 0, "));
      if (backend == Store.Backend.db2)
         {
         sql.append(Constants.NAME_COLUMN_ENTRY_NUMERIC + " DECFLOAT(34) , ");
         }
      if (backend == Store.Backend.postgresql)
         {
         sql.append(Constants.NAME_COLUMN_ENTRY_NUMERIC + " DOUBLE PRECISION , ");
         }
      if (isSharkBackend)
      {
    	  sql.append(Constants.NAME_COLUMN_ENTRY_NUMERIC + " DOUBLE , ");
      }
      sql.append(Constants.NAME_COLUMN_ENTRY_DATETIME + " TIMESTAMP , ");
      sql.append(Constants.NAME_COLUMN_PREFIX_TYPE +(isSharkBackend?" SMALLINT , ": " SMALLINT NOT NULL, "));

      length += colLenght * 2 + 32;
      int bucketSize = (MAX_TABLE_SIZE - length - gidLenght - colLenght) / (colLenght * 2);

      actualBucketSize = bucketSize < (maxBucketNumber) ? bucketSize : maxBucketNumber;

      for (int i = 0; i < actualBucketSize; i++)
         {
         if (i > 0)
            sql.append(", ");
         sql.append(Constants.NAME_COLUMN_PREFIX_PREDICATE + i +(isSharkBackend? " STRING, ": " VARCHAR(" + colLenght + "), "));
         sql.append(Constants.NAME_COLUMN_PREFIX_VALUE + i + (isSharkBackend? " STRING ": " VARCHAR(" + colLenght + ")"));
         }

      if (backend == Store.Backend.db2)
         {
         sql.append(") VALUE COMPRESSION COMPRESS YES NOT LOGGED INITIALLY");
         }
      else
         {
         sql.append(")");
         }
      if (backend == Store.Backend.shark) {
    	  sql.append(" ROW FORMAT DELIMITED FIELDS TERMINATED BY '\\t';\n");
    	//  sql.append("CACHE TABLE %s");
      }
      return sql.toString();
      }

   }