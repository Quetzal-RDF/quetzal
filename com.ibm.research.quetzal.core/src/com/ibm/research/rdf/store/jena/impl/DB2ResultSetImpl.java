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
 package com.ibm.research.rdf.store.jena.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.core.ResultBinding;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.core.VarExprList;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.config.Constants;
import com.ibm.research.rdf.store.jena.RdfStoreException;
import com.ibm.research.rdf.store.jena.impl.update.InsertAndUpdateStatements;
import com.ibm.research.rdf.store.jena.impl.update.String2Node;
import com.ibm.research.rdf.store.runtime.service.types.LiteralInfoResultSet;
import com.ibm.research.rdf.store.runtime.service.types.TypeMap;
import com.ibm.research.rdf.store.runtime.service.types.TypedValue;

public class DB2ResultSetImpl implements ResultSet {

   private java.sql.ResultSet   set;
   private LiteralInfoResultSet liRs;
   private boolean              hasNext       = false;
   private int                  count         = 0;
   private List<String>         varList;
   private Store                store         = null;
   private Connection           connection;
   private boolean              hasNextCalled = false;
   private boolean              nextCalled    = true;
   private Set<String>			columnNames = new HashSet<String>();

   private static final Log     log           = LogFactory.getLog(DB2ResultSetImpl.class);

   public DB2ResultSetImpl(LiteralInfoResultSet rs, Store store, Connection c, List<String> list, VarExprList varExprList)
      {
      liRs = rs;
      set = rs.getResultSet();
      try {
	      ResultSetMetaData rsMetaData = set.getMetaData();
	      int numberOfColumns = rsMetaData.getColumnCount();
	
	      // get the column names; column indexes start from 1
	      for (int i = 1; i <= numberOfColumns; i++) {
	          columnNames.add(rsMetaData.getColumnName(i));
	         
	      }
      } catch (SQLException e) {
    	  e.printStackTrace();
    	  throw new RuntimeException("Error getting result metadata");
      }
      this.store = store;
      connection = c;

      if (varExprList.isEmpty())
         {
         varList = list;
         }
      else
         {
         List<Var> vars = varExprList.getVars();
         varList = new ArrayList<String>();
         for (int i = 0; i < vars.size(); i++)
            {
            varList.add(vars.get(i).getName());
            }
         }
      }

   public List<String> getResultVars()
      {
      return varList;
      }

   public int getRowNumber()
      {
      return count;
      }

   public boolean hasNext()
      {
      if (!nextCalled)
         {
         return hasNext;
         }
      nextCalled = false;
      hasNextCalled = true;
      if (count == 0 || hasNext)
         {
         try
            {
            hasNext = set.next();
            count++;
            }
         catch (SQLException e)
            {
            hasNext = false;
            log.error("Error while checking next solution", e);
            }
         catch (Exception e)
            {
            hasNext = false;
            }
         }
      return hasNext;
      }

   public QuerySolution next()
      {
      return new ResultBinding(getResourceModel(), nextBinding());
      }

   public void remove()
      {
      throw new RdfStoreException("Not supported");
      }

   public Model getResourceModel() {
	   // assert false;
	   return null;	
   }

   public QuerySolution nextSolution()
      {
      return next();
      }

   public Binding nextBinding() {
	  DB2Binding binding = new DB2Binding(null);
      nextCalled = true;
      if (!hasNextCalled)
         {
         hasNext();
         }
      hasNextCalled = false;
      if (hasNext)
         {
         Map<TypedValue, String> sidMap = new HashMap<TypedValue, String>();
         try
            {
            for (int i = 0; i < varList.size(); i++)
               {
               String colName = varList.get(i);
               String result = null;

              // result = set.getString(colName.toLowerCase());
               result = set.getString(colName);

               if (result != null)
                  {
                  if (result.startsWith(Constants.PREFIX_SHORT_STRING))
                     {
                     if (liRs.isLiteralVariable(colName))
                        {
                       		short type = TypeMap.SIMPLE_LITERAL_ID;
                           	if (columnNames.contains((colName + Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS).toLowerCase())) { 
                           		type = set.getShort((colName + Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS).toLowerCase());
                           	}
                            sidMap.put(new TypedValue(result, type), colName);
 
                        }
                     else
                        {
                        // this is a IRI
                        sidMap.put(new TypedValue(result, TypeMap.IRI_ID), colName);
                        }
                     }
                  else
                     {

                     RDFNode n = null;
                     if (liRs.isLiteralVariable(colName))
                        {
                   
                    	short type = TypeMap.SIMPLE_LITERAL_ID;
                    	if (columnNames.contains((colName + Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS).toLowerCase())) {
                    		type = set.getShort((colName + Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS).toLowerCase());
                    	}

                        n = new String2Node(Constants.NAME_COLUMN_OBJECT, result, type).getNode();
                        }
                     else
                        {
                        n = new String2Node(Constants.NAME_COLUMN_SUBJECT, result).getNode();

                        }

                     binding.add(Var.alloc(colName),(n != null)?n.asNode():null);
                     }
                  }
               }

            if (sidMap.size() > 0)
               {
               String sql = InsertAndUpdateStatements.getLongStringSelect(store.getLongStrings(), sidMap.size());

               PreparedStatement sStmt = null;
               java.sql.ResultSet setSID = null;
               try
                  {
                  sStmt = connection.prepareStatement(sql);

                  Iterator<TypedValue> sids = sidMap.keySet().iterator();
                  int i = 1;
                  while (sids.hasNext())
                     {
                     // mdb types
                     // sStmt.setObject(i, sids.next());
                     // i++;
                     TypedValue t = sids.next();
                     sStmt.setObject(i++, t.getValue());
                     sStmt.setObject(i++, t.getType());
                     }
                  setSID = sStmt.executeQuery();
                  while (setSID.next())
                     {
                     String sid = setSID.getString(Constants.NAME_COLUMN_SHORT_STRING.toLowerCase());
                     short type = setSID.getShort(Constants.NAME_COLUMN_PREFIX_TYPE.toLowerCase());

                     RDFNode n = new String2Node(Constants.NAME_COLUMN_OBJECT,
                           setSID.getString(Constants.NAME_COLUMN_LONG_STRING.toLowerCase())
                                 + setSID.getString(Constants.NAME_COLUMN_LONG_STRING_OVERFLOW.toLowerCase()), type).getNode();

                     binding.add(Var.alloc(sidMap.get(new TypedValue(sid, type))), n.asNode());
                     }
                  }
               finally
                  {
                  DB2CloseObjects.close(setSID, sStmt);
                  }
               }
            }
         catch (SQLException e)
            {
        	 e.printStackTrace();
            }
         }
      return binding;
      }
   }