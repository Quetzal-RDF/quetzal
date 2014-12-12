package com.ibm.research.rdf.store.runtime.service.sql;

import java.io.IOException;
import java.io.InputStream;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import com.ibm.research.rdf.store.Store;

public class Sqls extends Properties
   {

   /**
	 * 
	 */
   private static final long serialVersionUID = 1L;
   private static Sqls       props            = null;

   public static Sqls getSqls(String backend)
      {

      if (props == null)
         {
         props = new Sqls();
         InputStream in;

         if (backend.equalsIgnoreCase(Store.Backend.postgresql.name()))
            {
            in = props
                  .getClass()
                  .getClassLoader()
                  .getResourceAsStream(
                        "com/ibm/research/rdf/store/runtime/service/sql/PostgreSQLRdfStoreSqls");
            }
         else if (backend.equalsIgnoreCase(Store.Backend.db2.name()))
            {
            in = props
                  .getClass()
                  .getClassLoader()
                  .getResourceAsStream(
                        "com/ibm/research/rdf/store/runtime/service/sql/DB2RdfStoreSqls");
            }
         else
         { 
        	 assert backend.equalsIgnoreCase(Store.Backend.shark.name()) : "Unsupported backend: "+backend;
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
