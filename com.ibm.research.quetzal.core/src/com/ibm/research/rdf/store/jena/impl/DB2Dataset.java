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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.TripleMatch;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.LabelExistsException;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.sparql.core.Quad;
import com.hp.hpl.jena.sparql.core.Transactional;
import com.hp.hpl.jena.sparql.util.Context;
import com.hp.hpl.jena.sparql.util.Symbol;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.config.Constants;
import com.ibm.research.rdf.store.hashing.HashingException;
import com.ibm.research.rdf.store.hashing.HashingHelper;
import com.ibm.research.rdf.store.jena.RdfStoreException;
import com.ibm.research.rdf.store.jena.impl.update.InsertAndUpdateStatements;

public class DB2Dataset implements /* DataSource, */Dataset, DatasetGraph, Transactional
   {

   private Connection       connection;
   private Store            store;
   private static final Log log         = LogFactory.getLog(DB2Dataset.class);

   private Model            defaultModel;

   private boolean          closed      = false;
   private Context          jenaContext = null;

   public DB2Dataset(Model model) 
      {
      defaultModel = model;
      DB2Graph g = (DB2Graph) model.getGraph();
      store = g.getStore();
      connection = g.getConnection();
      }

   public void commit()
      {
      try
         {
         connection.commit();
         }
      catch (SQLException e)
         {
         throw new RdfStoreException(e.getLocalizedMessage(), e);
         }
      }

   @Override
   public void abort()
      {
	   // do nothing

      }

   @Override
   public void begin(ReadWrite arg0)
      {
      // do nothing

      }

   @Override
   public void end()
      {
      // do nothing
      }

   @Override
   public boolean isInTransaction()
      {
      return false;
      }

   @Override
   public boolean supportsTransactions()
      {
      return true;
      }

   // Checking if Model already exist in the store.
   public boolean containsNamedModel(String nameModel)
      {
      boolean returnFlag = false;
      if (nameModel.length() > Constants.LONG_STRING_COLUMN_SIZE)
         {
         return returnFlag;
         }
      if (nameModel.length() > store.getGidMaxStringLen())
         {
         try
            {
            nameModel = Constants.PREFIX_SHORT_STRING + HashingHelper.hashLongString(nameModel);
            }
         catch (HashingException | UnsupportedEncodingException e)
            {
            log.error("Hashing Exception");
            }
         }
      String sql = InsertAndUpdateStatements.getGidByGid(store.getDirectPrimary());
      PreparedStatement stmt = null;
      ResultSet rs = null;
      try
         {
         stmt = connection.prepareStatement(sql);
         stmt.setString(1, nameModel);
         rs = stmt.executeQuery();
         if (rs.next())
            {
            returnFlag = true;
            }

         }
      catch (SQLException e)
         {
         }
      finally
         {
         DB2CloseObjects.close(rs, stmt);
         }

      return returnFlag;
      }

   //
   public boolean containsGraph(Node graphNode)
      {
      return containsNamedModel(graphNode.getURI());
      }

   //
   public Graph getGraph(Node graphNode)
      {
      return new DB2Graph(store, connection, graphNode.getURI());
      }

   // Creating/connecting to the given Model name.
   public Model getNamedModel(String namedModel)
      {
      return ModelFactory.createModelForGraph(getGraph(Node.createURI(namedModel)));
      }

   // Getting list of all the graph name present in the store.
   public Iterator<String> listNames()
      {
      String sql = InsertAndUpdateStatements.getGraphListStatement(store.getDirectPrimary(), store.getLongStrings());

      List<String> graphList = new ArrayList<String>();
      PreparedStatement stmt = null;
      ResultSet rs = null;
      try
         {
         stmt = connection.prepareStatement(sql);
         rs = stmt.executeQuery();
         while (rs.next())
            {
            graphList.add(rs.getString(1));
            }
         }
      catch (SQLException e)
         {
         log.error("Unexpected SQLException..", e);
         }
      finally
         {
         DB2CloseObjects.close(rs, stmt);
         }
      return graphList.iterator();
      }

   public Iterator<Node> listGraphNodes()
      {
      Iterator<String> names = listNames();

      Set<Node> nodes = new HashSet<Node>();
      while (names.hasNext())
         {
         nodes.add(Node.createURI(names.next()));
         }
      return nodes.iterator();

      }

   public DatasetGraph asDatasetGraph()
      {
      return this;
      }

   public Model getDefaultModel()
      {
      return defaultModel;
      }

   public Graph getDefaultGraph()
      {
      return defaultModel.getGraph();
      }

   public void addNamedModel(String uri, Model model) throws LabelExistsException
      {

      if (containsNamedModel(uri))
         {
         throw new LabelExistsException(uri + " already exists.");
         }

      getNamedModel(uri).add(model);
      }

   public void replaceNamedModel(String uri, Model model)
      {
      Model m = getNamedModel(uri);
      m.removeAll();
      m.add(model);
      }

   public void addGraph(Node graphName, Graph graph)
      {
      replaceNamedModel(graphName.getURI(), ModelFactory.createModelForGraph(graph));
      }

   public void removeNamedModel(String uri)
      {
      getNamedModel(uri).removeAll();
      }

   public void removeGraph(Node graphName)
      {
      removeNamedModel(graphName.getURI());
      }

   public void add(Quad quad)
      {
      getGraph(quad.getGraph()).add(quad.asTriple());
      }

   public boolean contains(Quad quad)
      {
      Iterator<Quad> it = find(quad);
      if (it != null && it.hasNext())
         {
         return true;
         }
      return false;
      }

   public boolean contains(Node g, Node s, Node p, Node o)
      {
      return contains(new Quad(g, s, p, o));
      }

   public void delete(Quad quad)
      {

      if (quad.getGraph().getURI().equalsIgnoreCase(Constants.DEFAULT_GRAPH_MONIKER))
         {
         defaultModel.getGraph().delete(quad.asTriple());
         }

      getGraph(quad.getGraph()).delete(quad.asTriple());
      }

   @Override
   public void add(Node g, Node s, Node p, Node o)
      {
      add(new Quad(g, s, p, o));

      }

   @Override
   public void delete(Node g, Node s, Node p, Node o)
      {
      delete(new Quad(g, s, p, o));

      }

   @Override
   public Iterator<Quad> findNG(Node g, Node s, Node p, Node o)
      {
      if ((g == null) || g.equals(Node.ANY))
         {
         // TODO: REVISIT: very inefficient way to get all the matching quads from named graphs only
         LinkedList<Quad> l = new LinkedList<Quad>();
         Iterator<Quad> res = find(g, s, p, o);
         while (res.hasNext())
            {
            Quad q = res.next();
            if (q.getGraph() != null
                  && (!q.getGraph().isURI() || !q.getGraph().getURI().equalsIgnoreCase(Constants.DEFAULT_GRAPH_MONIKER)))
               {
               l.add(q);
               }
            }
         return l.iterator();

         }
      else if (g.getURI().equalsIgnoreCase(Constants.DEFAULT_GRAPH_MONIKER))
         {
         return new LinkedList<Quad>().iterator();
         }
      else
         {
         return find(g, s, p, o);
         }
      }

   public Iterator<Quad> find(Quad quad)
      {
      return find(quad.getGraph(), quad.getSubject(), quad.getPredicate(), quad.getObject());

      }

   @Override
   public Iterator<Quad> find()
      {
      return find(Node.ANY, Node.ANY, Node.ANY, Node.ANY);
      }

   public Iterator<Quad> find(Node g, final Node s, final Node p, final Node o)
      {

      ExtendedIterator<Triple> it;

      if ((g == null) || g.equals(Node.ANY))
         {
         it = DB2Graph.find(store, new TripleMatch()
            {
               public Triple asTriple()
                  {
                  return new Triple(s, p, o);
                  }

               public Node getMatchObject()
                  {
                  return o;
                  }

               public Node getMatchPredicate()
                  {
                  return p;
                  }

               public Node getMatchSubject()
                  {
                  return s;
                  }

            }, null, connection, false, /* not reification */
               true /* search all graphs */);
         }
      else
         {

         if (g.getURI().equalsIgnoreCase(Constants.DEFAULT_GRAPH_MONIKER))
            {
            it = defaultModel.getGraph().find(s, p, o);
            }
         else
            {
            it = getGraph(g).find(s, p, o);
            }

         }

      Set<Quad> sq = new HashSet<Quad>();
      while (it.hasNext())
         {
         sq.add(new Quad(g, it.next()));
         }
      it.close();

      return sq.iterator();

      }

   public Context getContext()
      {
      if (jenaContext == null)
         {
         jenaContext = new Context();
         Iterator<Symbol> db2Keys = store.getContext().getSymbols().iterator();
         while (db2Keys.hasNext())
            {
            Symbol b = db2Keys.next();
            jenaContext.put(b, store.getContext().get(b));
            }
         }
      return jenaContext;
      }

   public long size()
      {
      Iterator<String> it = listNames();
      long size = 0;
      while (it.hasNext())
         {
         it.next();
         size++;
         }
      return size;
      }

   public boolean isEmpty()
      {
      StringBuffer sql = new StringBuffer();
      sql.append("SELECT ");
      sql.append(Constants.NAME_COLUMN_GRAPH_ID);
      sql.append(" FROM ");
      sql.append(store.getDirectPrimary());
      sql.append(" FETCH FIRST 1 ROWS ONLY");

      PreparedStatement stmt = null;
      ResultSet rs = null;

      try
         {
         stmt = connection.prepareStatement(sql.toString());
         rs = stmt.executeQuery();
         if (rs.next())
            {
            return false;
            }
         }
      catch (SQLException e)
         {
         return true;
         }
      finally
         {
         DB2CloseObjects.close(rs, stmt);
         }
      return true;
      }

   public void deleteAny(Node g, Node s, Node p, Node o)
      {
      // TODO dont know how to implement using Model/Graph
      throw new RdfStoreException("Operation Not supported");
      }

   public void setDefaultGraph(Graph g)
      {
      throw new RdfStoreException("Operation not supported");

      }

   public void setDefaultModel(Model model)
      {
      throw new RdfStoreException("Operation not supported");
      }

   public Lock getLock()
      {
      return null;
      }

   public void close()
      {
      closed = true;
      }

   }