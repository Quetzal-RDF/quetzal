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

import java.util.Collection;
import java.util.HashSet;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.ARQConstants;
import com.hp.hpl.jena.sparql.core.describe.DescribeHandler;
import com.hp.hpl.jena.sparql.util.Context;
import com.ibm.research.rdf.store.jena.RdfStoreQueryExecutionFactory;

public class DB2DescribeHandler implements DescribeHandler {
	private Model acc;
	private Dataset dataset;
	private Collection<String> resources;

	public void start(Model accumulateResultModel, Context cxt) {
		acc = accumulateResultModel;
		this.dataset = (Dataset) cxt.get(ARQConstants.sysCurrentDataset);
		resources = new HashSet<String>();
	}

	public void finish() {
		resources.clear();
	}

	// Check all named graphs
	public void describe(Resource r) {
		// Default model.
		DB2Closure.closure(otherModel(r, dataset.getDefaultModel()), false, acc, resources);

		String query = "SELECT ?g { GRAPH ?g { <" + r.getURI() + "> ?p ?o } }";
		QueryExecution qExec = RdfStoreQueryExecutionFactory.create(query,
				dataset);

		ResultSet rs = qExec.execSelect();
		for (; rs.hasNext();) {
			QuerySolution qs = rs.next();
			String gName = qs.getResource("g").getURI(); // mdb for DB2
			Model model = dataset.getNamedModel(gName);
			Resource r2 = otherModel(r, model);
			DB2Closure.closure(r2, false, acc, resources);
		}

		qExec.close();
		
		DB2Closure.closure(r, false, acc, resources);
	}
	
	/*public void describe(Set<Resource> resources) {
		StringBuffer query = new StringBuffer();
		selectClaues(query, resources.size());
		int i = 0;
		query.append("WHERE { \n");
		Iterator<Resource> iterator = resources.iterator();
		while (iterator.hasNext()) {
			Resource r = iterator.next();
			if (i > 0) query.append(" UNION \n");
			sparqlQueryN(query, i, r);
			i++;
			// Default model.
			Closure.closure(otherModel(r, dataset.getDefaultModel()), false, acc);
			Closure.closure(r, false, acc);
		}
		
		query.append("}");
		
		QueryExecution qExec = RdfStoreQueryExecutionFactory.create(query.toString(),
				dataset);

		ResultSet rs = qExec.execSelect();
		Set<String> graphs = new HashSet<String>();

		while (rs.hasNext()) {
			QuerySolution qs = rs.next();
			Iterator<String> variables = qs.varNames();
			while (variables.hasNext()) {
				String variable = variables.next();
				String gName = qs.getResource(variable).getURI();
				graphs.add(gName);
			}
		}
		
		Iterator<String> graphIT = graphs.iterator();
		while (graphIT.hasNext()) {
			String gName = graphIT.next();
			Model model = dataset.getNamedModel(gName);
			iterator = resources.iterator();
			while (iterator.hasNext()) {
				Resource r = iterator.next();
				Resource r2 = otherModel(r, model);
				Closure.closure(r2, false, acc);
			}
		}
	}

	private void sparqlQueryN(StringBuffer query, int i, Resource resource) {
		query.append("  { GRAPH ?g" + i + " { \n");
		query.append("    <");
		query.append(resource.getURI());
		query.append("> ?p" + i + " ?o" + i);
		query.append("  } }"); 
	}

	private void selectClaues(StringBuffer query, int numResources) {
		query.append("SELECT ");
		for (int i = 0;i < numResources;i++) {
			query.append("?g" + i + " ");
		}
		query.append("\n");
	}*/

	private static Resource otherModel(Resource r, Model model) {
		if (r.isURIResource())
			return model.createResource(r.getURI());
		if (r.isAnon())
			return model.createResource(r.getId());
		return r;
	}
}
