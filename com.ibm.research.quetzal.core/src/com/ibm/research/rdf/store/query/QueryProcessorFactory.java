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
 package com.ibm.research.rdf.store.query;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import com.hp.hpl.jena.rdf.model.Model;
import com.ibm.research.owlql.ruleref.OWLQLSPARQLCompiler;
import com.ibm.research.rdf.store.Context;
import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.sparql11.QueryProcessorImpl;
import com.ibm.research.rdf.store.sparql11.SparqlParserUtilities;


public class QueryProcessorFactory {

	/**
	 * creates an {@link OWLQLSPARQLCompiler} given an OWL ontology
	 * @param tboxFiles
	 * @return
	 * @throws OWLOntologyCreationException
	 */
	public static OWLQLSPARQLCompiler createOWLQLCompiler(Model ont) throws IOException, OWLOntologyCreationException {
		return new OWLQLSPARQLCompiler(ont, null, null);
	}
	/**
	 * creates an {@link OWLQLSPARQLCompiler} given an OWL ontology
	 * @param tboxFiles
	 * @return
	 * @throws OWLOntologyCreationException
	 */
	public static OWLQLSPARQLCompiler createOWLQLCompiler(OWLOntology ont)  {
		return new OWLQLSPARQLCompiler(ont, null, null);
	}
	/**
	 * creates an {@link OWLQLSPARQLCompiler} given a set of OWL ontology files
	 * @param tboxFiles
	 * @return
	 * @throws OWLOntologyCreationException
	 */
	public static OWLQLSPARQLCompiler createOWLQLCompiler(String... tboxFiles) throws OWLOntologyCreationException {
		return new OWLQLSPARQLCompiler(null, null, tboxFiles);
	}
	/*
	 * creates a Query Processor for a query (SPARQL for now)
	 * 
	 * @query the query to execute
	 * 
	 * @Connection JDBC connection to the database where RDFStore exists
	 * 
	 * @Store metadata about the RDFStore (tablenames, statistics etc ) to
	 * execute the sparql against
	 * 
	 * @Context additional specific context for this query like uniondefault etc
	 */
	public static QueryProcessor create(String query, Connection conn,
			Store store, Context ctx, OWLQLSPARQLCompiler compiler) 
	{
		return new QueryProcessorImpl(query, conn, store, ctx, compiler);
	}
	/*
	 * creates a Query Processor for a query (SPARQL for now)
	 * 
	 * @query the query to execute
	 * 
	 * @Connection JDBC connection to the database where RDFStore exists
	 * 
	 * @Store metadata about the RDFStore (tablenames, statistics etc ) to
	 * execute the sparql against
	 * 
	 * @Context additional specific context for this query like uniondefault etc
	 */
	public static QueryProcessor create(String query, Connection conn,
			Store store, Context ctx) {
		return create(query, conn, store, ctx, null);
	}

	
	/*
	 * creates a Query Processor for a query file (SPARQL )
	 * 
	 * @query_file the file name of the query to execute
	 * 
	 * @Connection JDBC connection to the database where RDFStore exists
	 * 
	 * @Store metadata about the RDFStore (tablenames, statistics etc ) to
	 * execute the sparql against
	 * 
	 * @Context additional specific context for this query like uniondefault etc
	 */
	public static QueryProcessor create(File query_file, Connection conn,
			Store store, Context ctx, OWLQLSPARQLCompiler compiler)  
	{
		return new QueryProcessorImpl(query_file, conn, store, ctx, compiler);
	}
	/*
	 * creates a Query Processor for a query file (SPARQL )
	 * 
	 * @query_file the file name of the query to execute
	 * 
	 * @Connection JDBC connection to the database where RDFStore exists
	 * 
	 * @Store metadata about the RDFStore (tablenames, statistics etc ) to
	 * execute the sparql against
	 * 
	 * @Context additional specific context for this query like uniondefault etc
	 */
	public static QueryProcessor create(File query_file, Connection conn,
			Store store, Context ctx) {
		return create(query_file, conn, store, ctx, null);
	}
	
	/**
	 * Parse the query and return a query object
	 * 
	 * @param query
	 *            - the query to parse
	 * @return
	 */
	public static Query parse(String query) 
	{
		return SparqlParserUtilities.parseSparqlString(query, new HashMap<String, String>());
	}
	
	public static Query parse(File queryFile) 
	{	
		return SparqlParserUtilities.parseSparql(queryFile, new HashMap<String, String>());
	}

	/*
	 * creates a Query Processor for a query Object (SPARQL for now)
	 * 
	 * @query the query (obtained from QueryProcessorFactory.parse() )
	 * 
	 * @Connection JDBC connection to the database where RDFStore exists
	 * 
	 * @Store metadata about the RDFStore (tablenames, statistics etc ) to
	 * execute the sparql against
	 * 
	 * @Context additional specific context for this query like uniondefault etc
	 */
	public static QueryProcessor create(Query query, Connection conn,
			Store store, Context ctx, OWLQLSPARQLCompiler compiler) {

		return new QueryProcessorImpl(
				(com.ibm.research.rdf.store.sparql11.model.Query) query, conn, store,
				ctx, compiler);
	}

	/*
	 * creates a Query Processor for a query Object (SPARQL for now)
	 * 
	 * @query the query (obtained from QueryProcessorFactory.parse() )
	 * 
	 * @Connection JDBC connection to the database where RDFStore exists
	 * 
	 * @Store metadata about the RDFStore (tablenames, statistics etc ) to
	 * execute the sparql against
	 * 
	 * @Context additional specific context for this query like uniondefault etc
	 */
	public static QueryProcessor create(Query query, Connection conn,
			Store store, Context ctx) {
		return create(query, conn, store, ctx, null);
	}
	private QueryProcessorFactory() {
	}

}
