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

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.TripleMatch;
import com.hp.hpl.jena.graph.impl.GraphBase;
import com.hp.hpl.jena.graph.impl.GraphWithPerform;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.config.Constants;
import com.ibm.research.rdf.store.hashing.HashingException;
import com.ibm.research.rdf.store.hashing.HashingHelper;
import com.ibm.research.rdf.store.jena.impl.update.InsertAndUpdateStatements;
import com.ibm.research.rdf.store.jena.impl.update.Node2String;

public class DB2Graph extends GraphBase implements GraphWithPerform
   {

   protected Connection     con;
   private Store            store   = null;
   private String           graphID = null;

   private static final Log log     = LogFactory.getLog(DB2Graph.class);

   public String getGraphID()
      {
      return graphID;
      }

   public void setGraphID(String graphID)
      {
      if (graphID == null)
         {
         this.graphID = Constants.DEFAULT_GRAPH_MONIKER;
         }
      else
         {
         this.graphID = graphID;
         }
      }

   public DB2Graph(Store store, Connection con, String graphID)
      {
      super();
      this.con = con;
      this.store = store;
      setGraphID(graphID);
      }

   public Connection getConnection()
      {
      return con;
      }

   public Store getStore()
      {
      return store;
      }

   // Construction of input as per the store specified limit.
   private static String getCustomString(String string, int limit/* , int index */)
      {
      if (string.length() > Constants.LONG_STRING_COLUMN_SIZE)
         {
         return null;
         }

      if (string.length() > limit)
         {
         try
            {
            string = Constants.PREFIX_SHORT_STRING + HashingHelper.hashLongString(string);
            }
         catch (HashingException | UnsupportedEncodingException e)
            {
            log.error("Hashing Exception");
            }
         }

      return string;
      }

   // mdb support for find in any graph.. from Dataset.find(Quad)
   public static ExtendedIterator<Triple> find(Store store, TripleMatch m, String gid, Connection con, boolean isReified,
         boolean searchAllGraphs)
      {

      Node subNode = m.getMatchSubject();
      Node preNode = m.getMatchPredicate();
      Node objNode = m.getMatchObject();
      boolean exception = false;
      boolean directAccess = !((subNode == null || subNode.equals(Node.ANY)) && !(objNode == null || objNode.equals(Node.ANY)));

      boolean isGraph = (!(gid == null));
      List<String> params = new ArrayList<String>();

      if (searchAllGraphs)
         {
         // this means the call is coming from Dataset.find(Quad)
         // do nothing
         }
      else
         {
         // this is the old code for simple Graph.find(Triple)
         if (gid == null)
            {
            exception = true;
            }
         params.add(gid);
         }

      StringBuffer whereClause = new StringBuffer();

      if (subNode != null && !subNode.equals(Node.ANY))
         {
         whereClause.append(" AND ");
         if (directAccess)
            {
            whereClause.append("T.");
            whereClause.append(Constants.NAME_COLUMN_ENTRY);
            whereClause.append(" = ?");
            }
         else
            {
            whereClause.append("LT.");
            whereClause.append(Constants.NAME_COLUMN_PREFIX_VALUE);
            whereClause.append(" = ?");
            }
         String value = Node2String.getString(subNode);
         value = getCustomString(value, store.getMaxStringLen());

         if (value == null)
            {
            exception = true;
            }
         params.add(value);
         }

      if (preNode != null && !preNode.equals(Node.ANY))
         {
         whereClause.append(" AND ");
         whereClause.append("LT.");
         whereClause.append(Constants.NAME_COLUMN_PREFIX_PREDICATE);
         whereClause.append(" = ?");
         String value = Node2String.getString(preNode);

         value = getCustomString(value, store.getMaxStringLen());

         if (value == null)
            {
            exception = true;
            }
         params.add(value);
         }

      StringBuffer sql = new StringBuffer();

      if (directAccess)
         {
         sql.append(InsertAndUpdateStatements.getTripleInGraphDPH(store.getDirectPrimary(), store.getDirectSecondary(),
               store.getLongStrings(), store.getDPrimarySize(), store.isLongString(), isGraph, whereClause.toString()));
         }
      else
         {
         sql.append(InsertAndUpdateStatements.getTripleInGraphRPH(store.getReversePrimary(), store.getReverseSecondary(),
               store.getLongStrings(), store.getRPrimarySize(), store.isLongString(), isGraph, whereClause.toString()));
         }

      if (objNode != null && !objNode.equals(Node.ANY))
         {
         sql.append(" WHERE ");
         sql.append(" VALUE = ?");
         sql.append(" AND TYPE = ?");

         String literalValue = Node2String.getString(objNode);
         literalValue = getCustomString(literalValue, store.getMaxStringLen());
         if (literalValue == null)
            {
            exception = true;
            }
         params.add(literalValue);
         params.add(Short.toString(Node2String.getType(objNode)));
         }

      PreparedStatement stmt = null;
      ResultSet rs = null;
      try
         {
         stmt = con.prepareStatement(sql.toString());
         rs = getRS(stmt, params, exception);
         return new DB2ResultSetIterator(rs, con, store);
         }
      catch (SQLException e)
         {
         DB2CloseObjects.close(rs, stmt);
         return null;
         }
      }

   /*
    * public Reifier constructReifier() { return new DB2Reifier(this, style); }
    */

   private static ResultSet getRS(PreparedStatement stmt, List<String> params, boolean isException) throws SQLException
      {
      for (int i = 0; i < params.size(); i++)
         {
         if (isException)
            {
            stmt.setObject(i + 1, null);
            }
         else
            {
            stmt.setObject(i + 1, params.get(i));
            }
         }
      return stmt.executeQuery();
      }

   @Override
   protected ExtendedIterator<Triple> graphBaseFind(TripleMatch arg0)
      {
      // TODO Auto-generated method stub
      return null;
      }
   }