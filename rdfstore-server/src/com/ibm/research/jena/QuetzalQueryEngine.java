/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ibm.research.jena;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Iterator;

import org.apache.jena.atlas.io.IndentedWriter;

import com.hp.hpl.jena.assembler.Assembler;
import com.hp.hpl.jena.assembler.Mode;
import com.hp.hpl.jena.assembler.assemblers.AssemblerBase;
import com.hp.hpl.jena.query.ARQ;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.sparql.ARQInternalErrorException;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.sparql.core.assembler.AssemblerUtils;
import com.hp.hpl.jena.sparql.engine.Plan;
import com.hp.hpl.jena.sparql.engine.QueryEngineFactory;
import com.hp.hpl.jena.sparql.engine.QueryEngineRegistry;
import com.hp.hpl.jena.sparql.engine.QueryIterator;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.engine.iterator.QueryIteratorResultSet;
import com.hp.hpl.jena.sparql.engine.main.QueryEngineMain;
import com.hp.hpl.jena.sparql.resultset.RDFInput;
import com.hp.hpl.jena.sparql.serializer.SerializationContext;
import com.hp.hpl.jena.sparql.util.Context;
import com.ibm.rdf.store.sparql11.TestRunner.DB2TestData;
import com.ibm.rdf.store.sparql11.TestRunner.PSQLTestData;
import com.ibm.rdf.store.sparql11.TestRunner.SharkTestData;
import com.ibm.rdf.store.sparql11.TestRunner.TestData;
import com.ibm.research.rdf.store.jena.impl.DB2Dataset;
import com.ibm.research.rdf.store.jena.impl.DB2QueryExecutionImpl;


/**
 * QuetzalQueryEngine replaces the engine that Jena uses with our own query execution.
 * @author ksrinivs
 *
 */
public class QuetzalQueryEngine extends QueryEngineMain {
	private Query query;
	static QueryEngineFactory factory;

	private static TestData testData = null;

	/**
	 * Assembler is called by Fuseki, this is used to set up the dataset from the 
	 * assembler description file from turtle file passed in as an argument to Fuseki's --config
	 * parameter 
	 */
	public static QuetzalAssembler assembler = new QuetzalAssembler();
	private static final String NS = "http://quetzal/";
	private static final Resource buildRsrc = ResourceFactory.createResource(NS
			+ "build");
	private static final Property jdbcRsrc = ResourceFactory.createProperty(NS
			+ "jdbc");
	private static final Property dbRsrc = ResourceFactory.createProperty(NS
			+ "db");
	private static final Property userRsrc = ResourceFactory.createProperty(NS
			+ "user");
	private static final Property passwdRsrc = ResourceFactory.createProperty(NS
			+ "passwd");
	private static final Property schemaRsrc = ResourceFactory.createProperty(NS
			+ "schema");
	private static final Property defaultUnionRsrc = ResourceFactory.createProperty(NS
			+ "defaultUnion");

	static {
		AssemblerUtils.init();
		Assembler.general.implementWith(buildRsrc, assembler);
		factory = new QuetzalQueryEngineFactory();
		register();
	}
	
	public static class QuetzalAssembler extends AssemblerBase implements
			Assembler {
		
		@Override
		public Object open(Assembler a, Resource root, Mode mode) {
			// read the configuration 
			String jdbcurl = extractProperty(root, jdbcRsrc);
			String db = extractProperty(root, dbRsrc);
			String user = extractProperty(root, userRsrc);
			String passwd = extractProperty(root, passwdRsrc);
			String schema = extractProperty(root, schemaRsrc);
			boolean defaultUnion = Boolean.parseBoolean(extractProperty(root, defaultUnionRsrc));
			
			if (jdbcurl.startsWith("jdbc:postgresql")) {
				QuetzalQueryEngine.testData = new PSQLTestData(
					jdbcurl, db,
					user, passwd, schema, defaultUnion);
			} else if (jdbcurl.startsWith("jdbc:db2")) {
				QuetzalQueryEngine.testData = new DB2TestData(
						jdbcurl, db,
						user, passwd, schema, defaultUnion);
			} else {
				QuetzalQueryEngine.testData = new SharkTestData(
						jdbcurl, db,
						user, passwd, schema, defaultUnion);

			}
			Dataset ds = QuetzalQueryEngine.testData.getDataset();
			return ds;
		}
		
		private String extractProperty(Resource root, Property prop) {
			StmtIterator iter = root.listProperties(prop);
			assert iter.hasNext();	
			Statement stmt = iter.nextStatement();
			if (!stmt.getObject().isLiteral())
				throw new RuntimeException("Not a literal " + stmt);
			String lit = stmt.getString();
			iter.close();
			return lit;
		}
	}


	public QuetzalQueryEngine(Query query, DatasetGraph dataset, Binding initial,
			Context context) {
		super(query, testData.getDataset().asDatasetGraph(), initial, context);
		this.query = query;
	}

	@Override
	protected Plan createPlan() {
		// TODO Auto-generated method stub
		return super.createPlan();
	}

	/**
	 * This class registers itself for generation of our own custom query engine
	 * @return
	 */
	static public QueryEngineFactory getFactory() {
		return factory;
	}

	static public void register() {
		QueryEngineRegistry.addFactory(factory);
	}

	static public void unregister() {
		QueryEngineRegistry.removeFactory(factory);
	}

	static class QuetzalQueryEngineFactory implements QueryEngineFactory {
		// Accept only queries that the assembler registers -- other datasets we get queried about are Jena specific
		// which we know nothing about, we have to punt on
		@Override
		public boolean accept(Query query, DatasetGraph dataset, Context context) {
			if (QuetzalQueryEngine.testData != null
					&& dataset == QuetzalQueryEngine.testData.getDataset()
							.asDatasetGraph()) {
				return true;
			}
			return false;
		}

		@Override
		public Plan create(Query query, DatasetGraph dataset, Binding initial,
				Context context) {
			// return a plan from our custom query engine
			QuetzalQueryEngine engine = new QuetzalQueryEngine(query, dataset,
					initial, context);
			return engine.getPlan();
		}

		@Override
		public boolean accept(Op op, DatasetGraph dataset, Context context) { 
			return false;		// don't accept any intermediate op requests -- only handle a full query request 
		}

		@Override
		public Plan create(Op op, DatasetGraph dataset, Binding inputBinding,
				Context context) { 
			throw new ARQInternalErrorException(
					"QuetzalQueryEngine: factory calleddirectly with an algebra expression");
		}
	}

	@Override
	public Plan getPlan() {
		// TODO Auto-generated method stub
		QuetzalPlan plan = new QuetzalPlan(query, testData.getDataset());
		return plan;
	}

	public static final class QuetzalPlan implements Plan {

		Query query;
		Dataset dataset;
		QueryExecution qe;

		public QuetzalPlan(Query query, Dataset dataset) {
			this.query = query;
			this.dataset = dataset;
		}

		@Override
		public void output(IndentedWriter arg0, SerializationContext arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public String toString(PrefixMapping arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void output(IndentedWriter arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void close() {
			if (qe != null) {
				qe.close();
			}

		}

		@Override
		public Op getOp() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public QueryIterator iterator() {
			QueryIterator qi = null;
			
			qe = new DB2QueryExecutionImpl(
					new com.ibm.research.rdf.store.jena.Query(query.toString()),
					(DB2Dataset) dataset, null);
			
			if (query.isSelectType()) {
				com.hp.hpl.jena.query.ResultSet rs = qe.execSelect();
				qi = new QueryIteratorResultSet(rs);
			} else if (query.isDescribeType()) {
				Model m = qe.execDescribe();
				qi = new QueryIteratorResultSet(RDFInput.fromRDF(m));
			} else if (query.isConstructType()) {
				Model m = qe.execConstruct();
				qi = new QueryIteratorResultSet(RDFInput.fromRDF(m));
			} else if (query.isAskType()) {
				boolean ask = qe.execAsk();
				if (ask) {
					// do nothing for now
				}
			}

			return qi;

		}

	}

	public static void main(String[] args) throws Exception {

		BufferedReader reader = new BufferedReader(new FileReader(args[0]));
		StringBuffer buf = new StringBuffer();
		String line = null;
		while ((line = reader.readLine()) != null) {
			buf.append(line).append("\n");
		}
		reader.close();

		Query query = QueryFactory.create(buf.toString(),
				Syntax.syntaxSPARQL_11);

		// Execute the query and obtain results
		ARQ.getContext().setTrue(ARQ.useSAX);
		QuetzalQueryEngine.testData = new PSQLTestData(
				"jdbc:postgresql://helix1.pok.ibm.com:5432/lubm100m", "lubm100m",
				"akement", "passw0rd", "db2inst2", false);

		QueryExecution qe = QueryExecutionFactory.create(query, testData.getDataset());

		ResultSet results = null;
		if (query.isSelectType()) {
			results = qe.execSelect();
			// Output query results
			while (results.hasNext()) {
				QuerySolution soln = results.nextSolution();
				Iterator<String> varNames = soln.varNames();
				while (varNames.hasNext()) {
					RDFNode x = soln.get(varNames.next()); // Get a result
															// variable by
					// name.
					System.out.println(x.toString());
				}
			}
		}
	}
}
