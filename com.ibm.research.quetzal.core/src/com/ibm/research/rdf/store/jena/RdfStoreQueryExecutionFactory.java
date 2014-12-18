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
 package com.ibm.research.rdf.store.jena;

import java.io.IOException;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.util.FileManager;
import com.ibm.research.owlql.ruleref.OWLQLSPARQLCompiler;
import com.ibm.research.rdf.store.jena.impl.DB2Dataset;
import com.ibm.research.rdf.store.jena.impl.DB2Graph;
import com.ibm.research.rdf.store.jena.impl.DB2QueryExecutionImpl;

public class RdfStoreQueryExecutionFactory {
	
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

	// Do we need to support overloaded create functions with
	// Syntax parameter
	// QuerySolution (yes)
	// sparqlService

	// should we take the connection as param here rather than get from Dataset
	// reason being shorter holding of connections.

	public static QueryExecution create(Query query, Model model, OWLQLSPARQLCompiler compiler) {
		checkArg(model);

		return new DB2QueryExecutionImpl( (com.ibm.research.rdf.store.jena.Query)query,
				(DB2Graph) model.getGraph(), compiler);

	}

	public static QueryExecution create(String sparql, Model model, OWLQLSPARQLCompiler compiler) {
		checkArg(model);

		return new DB2QueryExecutionImpl((com.ibm.research.rdf.store.jena.Query)
				RdfStoreQueryFactory.create(sparql),
				(DB2Graph) model.getGraph(), compiler);

	}

	public static QueryExecution create(String sparql, Dataset dataset, OWLQLSPARQLCompiler compiler) {
		checkArg(dataset);

		return new DB2QueryExecutionImpl((com.ibm.research.rdf.store.jena.Query)
				RdfStoreQueryFactory.create(sparql),
				(DB2Dataset) dataset, compiler);
	}

	public static QueryExecution create(String sparql, Dataset dataset) {
		return create(sparql, dataset, null);
	}
	public static QueryExecution create(Query query, Dataset dataset, OWLQLSPARQLCompiler compiler) {
		checkArg(dataset);
		return new DB2QueryExecutionImpl((com.ibm.research.rdf.store.jena.Query)query,
				(DB2Dataset) dataset, compiler);
	}
	public static QueryExecution create(Query query, Dataset dataset) {
		return create(query, dataset,null);
	}
	static private void checkArg(Model model) {
		checkNotNull(model, "Model is a null pointer");
	}

	private static void checkArg(Dataset dataset) {
		checkNotNull(dataset, "Dataset is a null pointer");
	}

	static private void checkNotNull(Object obj, String msg) {
		if (obj == null)
			throw new IllegalArgumentException(msg);
	}

	private RdfStoreQueryExecutionFactory() {
	}

	public static QueryExecution create(Query query,
			FileManager queryFileManager) {
		return null;
	}

}
