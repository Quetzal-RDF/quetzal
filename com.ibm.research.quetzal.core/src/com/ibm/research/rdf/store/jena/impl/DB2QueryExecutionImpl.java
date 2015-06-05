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
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sparql.util.Context;
import com.hp.hpl.jena.sparql.util.Symbol;
import com.hp.hpl.jena.util.FileManager;
import com.ibm.research.owlql.ruleref.OWLQLSPARQLCompiler;
import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.config.Constants;
import com.ibm.research.rdf.store.jena.Query;
import com.ibm.research.rdf.store.jena.RdfStoreException;
import com.ibm.research.rdf.store.jena.impl.update.String2Node;
import com.ibm.research.rdf.store.query.QueryProcessor;
import com.ibm.research.rdf.store.query.QueryProcessorFactory;
import com.ibm.research.rdf.store.runtime.service.sql.StoreImpl;
import com.ibm.research.rdf.store.runtime.service.types.LiteralInfoResultSet;
import com.ibm.research.rdf.store.sparql11.QueryProcessorImpl;
import com.ibm.research.rdf.store.sparql11.model.BlankNodeVariable;
import com.ibm.research.rdf.store.sparql11.model.ConstructQuery;
import com.ibm.research.rdf.store.sparql11.model.IRI;
import com.ibm.research.rdf.store.sparql11.model.PropertyTerm;
import com.ibm.research.rdf.store.sparql11.model.QueryPrologue;
import com.ibm.research.rdf.store.sparql11.model.QueryTriple;
import com.ibm.research.rdf.store.sparql11.model.QueryTripleTerm;

public class DB2QueryExecutionImpl implements QueryExecution
   {
   private Query                query;
   private DB2Dataset           ds;
   private DB2Graph             graph;
   private Context              jenaContext;
   private OWLQLSPARQLCompiler  compiler;
   private LiteralInfoResultSet rs  = null;
   QueryProcessor               qp  = null;
   private boolean closed = false;
   
   private static final Log     log = LogFactory.getLog(DB2QueryExecutionImpl.class);

   // query over the entire Dataset
   public DB2QueryExecutionImpl(Query query, DB2Dataset ds, OWLQLSPARQLCompiler compiler)
      {
      this.compiler = compiler;
      this.query = new Query(QueryProcessorImpl.compile(compiler, query.getDB2Query()));
      query.getDB2Query().setOrigQueryString(query.toString());
      this.ds = ds;
      }

   // querying over specific graph and not the entire dataset
   public DB2QueryExecutionImpl(Query query, DB2Graph m, OWLQLSPARQLCompiler compiler)
      {
      this.compiler = compiler;
      this.query = new Query(QueryProcessorImpl.compile(compiler, query.getDB2Query()));
      query.getDB2Query().setOrigQueryString(query.toString());
      this.graph = m;
      }

   public void abort()
      {
      if (rs != null && rs.getResultSet() != null)
         {
         try
            {
            rs.getResultSet().getStatement().cancel();
            }
         catch (SQLException e)
            {
            log.error("Cannot abort transaction", e);
            throw new RdfStoreException(e.getLocalizedMessage(), e);
            }
         finally
            {
            try
               {
               DB2CloseObjects.close(rs.getResultSet(), rs.getResultSet().getStatement());
               }
            catch (SQLException e)
               {

               }
            }
         }
      }

   public Context getContext()
      {

      if (jenaContext == null)
         {

         if (ds != null)
            { // TODO need to fix this.. always take from DS
            jenaContext = new Context();
            jenaContext.putAll(ds.getContext());
            }
         else
            {
            // TODO this else needs to go away
            // create the JENA context
            Iterator<Symbol> db2Keys = getStore().getContext().getSymbols().iterator();
            jenaContext = new Context();
            while (db2Keys.hasNext())
               {
               Symbol b = db2Keys.next();
               jenaContext.put(b, getStore().getContext().get(b));
               }
            }
         }
      return jenaContext;
      }

   public boolean execAsk()
      {
      if (query.isAskType())
         {
         qp = QueryProcessorFactory.create(query.getDB2Query(), getConnection(), getStore(), getNativeContext(), compiler);
         return qp.execAsk();
         }

      throw new IllegalArgumentException("Query was executed as ASK query, though it's not one");

      }

   public ResultSet execSelect()
      {

      if (query.isSelectType())
         {
         qp = QueryProcessorFactory.create(query.getDB2Query(), getConnection(), getStore(), getNativeContext(), compiler);
         rs = qp.execSelect();

         return new DB2ResultSetImpl(rs, getStore(), getConnection(), query.getResultVars(),
               query.getProject());
         }

      throw new IllegalArgumentException("Query was executed as SELECT query, though it's not one");

      }

   public Model execConstruct(Model model)
      {

      if (query.isConstructType())
         {
         QueryPrologue prologue = query.getDB2Query().getPrologue();
         Map<String, IRI> prefixes = prologue.getPrefixes();
         Iterator<String> iter = prefixes.keySet().iterator();
         Map<String, String> namespaces = null;
         while (iter.hasNext())
            {
            namespaces = new HashMap<String, String>();
            String namespaceprefix = (String) iter.next();
            namespaces.put(namespaceprefix, prefixes.get(namespaceprefix).toString());
            }

         if (namespaces != null)
            model.setNsPrefixes(namespaces);
         Iterator<Triple> it = execConstructTriples();
         while(it.hasNext())
            {  
            	Triple t = it.next();
            model.add(model.asStatement(t));
            }
         return model;
         }
      
      throw new IllegalArgumentException("Query was executed as CONSTRUCT query, though it's not one");

      }

   public Model execConstruct()
      {
      return execConstruct(ModelFactory.createDefaultModel());
      }

   public Model execDescribe()
      {
      return execDescribe(ModelFactory.createDefaultModel());
      }

   public Model execDescribe(Model model)
      {
	   
      if (query.isDescribeType())
         {
         qp = QueryProcessorFactory.create(query.getDB2Query(), getConnection(), getStore(), getNativeContext(), compiler);

         query.getDB2Query().getDescribeQuery().getPattern();
         rs = qp.execDescribe();

         if (rs != null)
            {
            java.sql.ResultSet sqlrs = rs.getResultSet();
            try
               {
               if (rs != null)
                  {
                  while (sqlrs.next())
                     {
                	  
                	  String S_dbValue = sqlrs.getString("described_resource");
                      Node sNode = new String2Node(Constants.NAME_COLUMN_SUBJECT, S_dbValue).getNode().asNode();
                	  
                      String P_dbValue = sqlrs.getString("property");
                      Node pNode = new String2Node(Constants.NAME_COLUMN_SUBJECT, S_dbValue).getNode().asNode();
                      
                      String O_dbValue = sqlrs.getString("object");
                      short T_dbValue = sqlrs.getShort("typ");
                      Node oNode = new String2Node(Constants.NAME_COLUMN_SUBJECT, S_dbValue,T_dbValue).getNode().asNode();
                      
                      Triple t = Triple.create(sNode, pNode, oNode);
                      model.add(model.asStatement(t));
                     }
                  }
               }
            catch (SQLException e)
               {
                throw new RuntimeException(e); 
               }
            }

        

      
         return model;
         }

      throw new IllegalArgumentException("Query was executed as DESCRIBE query, though it's not one");

      }

   public Dataset getDataset()
      {
      if (ds != null)
         return ds;

      throw new RdfStoreException("Operation Not supported");
      }

   public void setFileManager(FileManager arg0)
      {
      throw new RdfStoreException("Operation not supported");
      }

   public void setInitialBinding(QuerySolution arg0)
      {
      throw new RdfStoreException("DB2QueryExecutionImpl::setInitialBinding");
      }

   public void close()
      {

      QueryProcessorImpl qpi = (QueryProcessorImpl) qp;
      String logmsg = qpi.getSparql() + " | " + qpi.getOptimizerTime() + " | " + qpi.getTranslatorTime() + " | "
            + qpi.getDbTime() + " | " + qpi.getSql();
      ((StoreImpl) getStore()).logQueryInfo(logmsg);

      if (rs != null && rs.getResultSet() != null)
         {
         try
            {
            DB2CloseObjects.close(rs.getResultSet(), rs.getResultSet().getStatement());
            }
         catch (SQLException e)
            {

            }
         }
      closed = true;
      }

   private Connection getConnection()
      {
      if (ds != null)
         {
         return ((DB2Graph) ds.getDefaultModel().getGraph()).getConnection();
         }

      return graph.getConnection();
      }

   private Store getStore()
      {
      if (ds != null)
         {
         return ((DB2Graph) ds.getDefaultModel().getGraph()).getStore();
         }

      return graph.getStore();
      }

   private com.ibm.research.rdf.store.Context getNativeContext()
      {

      // if this context was never exercised, return the store's context
      if (jenaContext == null)
         {
         return getStore().getContext();
         }

      // convert the jenaContext back to native context
      com.ibm.research.rdf.store.Context nativeCtx = new com.ibm.research.rdf.store.Context();
      Iterator<Symbol> jkeys = jenaContext.keys().iterator();
      while (jkeys.hasNext())
         {
         Symbol b = jkeys.next();
         // jenaContext.put(b,getStore().getContext().get(b) );
         nativeCtx.set(b, jenaContext.get(b));
         }

      return nativeCtx;
      }

   @Override
   public Iterator<Triple> execConstructTriples()
      {
      if (query.isConstructType())
         {

         qp = QueryProcessorFactory.create(query.getDB2Query(), getConnection(), getStore(), getNativeContext(), compiler);
         rs = qp.execConstruct();
         if (rs == null)
            {
            return new LinkedList<Triple>().iterator();
            }

         final ConstructQuery q = query.getDB2Query().getConstructQuery();
         final java.sql.ResultSet sqlrs = rs.getResultSet();
         Iterator<Triple> ret = new Iterator<Triple>()
            {
        	   private int          blankNodeId          = 0;
        	   private boolean      incrementBlankNodeId = false;

               private List<Triple> buf                  = new LinkedList<Triple>();

               {
            	   tryFillBuffer();
               }
               
               private void tryFillBuffer() {
            	   try {
            		   while (buf.isEmpty() && sqlrs.next()) {
            			   List<QueryTriple> triples = q.getConstructPattern();
            			   for (int i = 0; i < triples.size(); i++)
            			   {
            				   QueryTriple t = triples.get(i);

            				   Node sNode = null;
            				   Node pNode = null;
            				   Node oNode = null;

            				   QueryTripleTerm sub = t.getSubject();
            				   if (sub.isVariable())
            				   {	
            	                    if (sub.getVariable() instanceof BlankNodeVariable) {
            	                    	sNode = new String2Node(Constants.NAME_COLUMN_SUBJECT, sub.getVariable().toString() + blankNodeId).getNode().asNode();
            	                    	incrementBlankNodeId = true;
                                    } else {
                                    	String dbValue = sqlrs.getString(sub.getVariable().getName().toLowerCase());
                                    	if (dbValue != null) {
                                    		sNode = new String2Node(Constants.NAME_COLUMN_SUBJECT, dbValue).getNode().asNode();
                                    	}
                                    }
            				   }
            				   else
            				   {
            					   sNode = NodeFactory.createURI(sub.getIRI().getValue());
            				   }

            				   PropertyTerm pred = t.getPredicate();
            				   if (pred.isVariable())
            				   {
            					   String varName = sqlrs.getString(pred.getVariable().getName().toLowerCase());
            					   if (varName != null) {
            						   pNode = new String2Node(Constants.NAME_COLUMN_PREDICATE, varName).getNode().asNode();
            					   }
            				   }
            				   else
            				   {
            					   assert pred.isIRI() : pred; // complex path not allow in construct template
            					   pNode = NodeFactory.createURI(pred.getIRI().getValue());
            				   }

            				   QueryTripleTerm obj = t.getObject();
            				   if (obj.isVariable())
            				   {
            					   String varName = obj.getVariable().getName();
            					   String dbvalue = sqlrs.getString(varName.toLowerCase());
            					   if (dbvalue != null) {
            						   if (rs.isLiteralVariable(varName))
            						   {
            							   short type = sqlrs.getShort((varName + Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS).toLowerCase());
            							   oNode = new String2Node(Constants.NAME_COLUMN_OBJECT, dbvalue, type).getNode().asNode();
            						   }
            						   else
            						   {
            							   oNode = new String2Node(Constants.NAME_COLUMN_SUBJECT, dbvalue).getNode().asNode();
            						   }

            					   }
            				   }
            				   else
            				   {
            					   if (obj.isIRI()) {
                   					   oNode = NodeFactory.createURI(obj.getIRI().getValue());
            					   } else {
            						   oNode = NodeFactory.createURI(obj.getConstant().toString());
            					   }
            				   }
            				   
            				   if (oNode != null && pNode != null && sNode != null) {
            					   buf.add(Triple.create(sNode, pNode, oNode));
            				   }
            				}
            				if (incrementBlankNodeId) {
                                   blankNodeId++;
            				}
            		   }
            	   } catch (SQLException e) {
            		   e.printStackTrace();
            	   }
               }
               
               public Triple next() {
            	   assert !buf.isEmpty();
            	   try {
            		   return buf.remove(0);
            	   } finally {
            		   if (buf.isEmpty()) {
            			   tryFillBuffer();
            		   }
            	   }
               }


			@Override
			public boolean hasNext() {
				return !buf.isEmpty();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();			
			}
            };
         return ret;
         }

      throw new IllegalArgumentException("Query was executed as CONSTRUCT TRIPLE query, though it's not one");

      }

   @Override
   public Iterator<Triple> execDescribeTriples()
      {
      // TODO: Implement a lazy iterator
      return new Iterator<Triple>()
         {
            StmtIterator it = execDescribe().listStatements();

            @Override
            public boolean hasNext()
               {
               return it.hasNext();
               }

            @Override
            public Triple next()
               {
               Statement st = it.next();
               return st.asTriple();
               }

            @Override
            public void remove()
               {
               it.remove();

               }

         };

      }

   @Override
   public com.hp.hpl.jena.query.Query getQuery()
      {
      return query;
      }

   @Override
   public long getTimeout1()
      {
      throw new UnsupportedOperationException();
      }

   @Override
   public long getTimeout2()
      {
      throw new UnsupportedOperationException();
      }

   @Override
   public void setTimeout(long arg0, long arg1)
      {
      throw new UnsupportedOperationException();

      }

   @Override
   public void setTimeout(long arg0, TimeUnit arg1, long arg2, TimeUnit arg3)
      {
      throw new UnsupportedOperationException();

      }

   @Override
   public void setTimeout(long arg0, TimeUnit arg1)
      {
      throw new UnsupportedOperationException();

      }

   @Override
   public void setTimeout(long arg0)
      {
      throw new UnsupportedOperationException();

      }

   }
