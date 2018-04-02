package com.ibm.rdf.store.sparql11;

import com.ibm.rdf.store.sparql11.TestRunner.AbstractEngine;
import com.ibm.research.owlql.ruleref.OWLQLSPARQLCompiler;
import com.ibm.research.rdf.store.query.QueryProcessor;
import com.ibm.research.rdf.store.query.QueryProcessorFactory;
import com.ibm.research.rdf.store.sparql11.model.Query;

public class BigQueryEngine extends AbstractEngine<BigQueryTestData> {

	public BigQueryEngine() {
		super();
	}

	public BigQueryEngine(OWLQLSPARQLCompiler compiler) {
		super(compiler);
	}

	protected QueryProcessor createQueryProcessor(BigQueryTestData data, Query q) {
		return QueryProcessorFactory.create(q, data.conn, data.store,
				data.ctx, compiler);
	}

}