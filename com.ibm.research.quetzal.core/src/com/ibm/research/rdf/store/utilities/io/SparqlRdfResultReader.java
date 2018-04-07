package com.ibm.research.rdf.store.utilities.io;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.jena.riot.RDFDataMgr;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.ibm.research.rdf.store.sparql11.model.BlankNode;
import com.ibm.research.rdf.store.sparql11.model.Constant;
import com.ibm.research.rdf.store.sparql11.model.IRI;
import com.ibm.research.rdf.store.sparql11.model.QueryTripleTerm;
import com.ibm.research.rdf.store.sparql11.model.StringLiteral;
import com.ibm.research.rdf.store.sparql11.model.Variable;
import com.ibm.wala.util.collections.HashMapFactory;
import com.ibm.wala.util.collections.HashSetFactory;

public class SparqlRdfResultReader implements SparqlSelectResult {

	private final Model ds;

	private final Set<Variable> vs;

	public SparqlRdfResultReader(String uri) {
		this.ds = RDFDataMgr.loadModel(uri);

		Query query = QueryFactory.create("ask { ?x a <http://www.w3.org/2001/sw/DataAccess/tests/result-set#ResultSet> . }");
		QueryExecution qexec = QueryExecutionFactory.create(query, ds);
		boolean isSelectOrAsk = qexec.execAsk();
		assert isSelectOrAsk;

		query = QueryFactory.create("ask { ?x <http://www.w3.org/2001/sw/DataAccess/tests/result-set#boolean> ?a . }");
		qexec = QueryExecutionFactory.create(query, ds);
		boolean isAskResult = qexec.execAsk();
		assert !isAskResult;

		query = QueryFactory.create("select distinct * where { ?x <http://www.w3.org/2001/sw/DataAccess/tests/result-set#resultVariable> ?v . }");
		qexec = QueryExecutionFactory.create(query, ds);
		com.hp.hpl.jena.query.ResultSet results = qexec.execSelect();

		this.vs = HashSetFactory.make();
		for (; results.hasNext(); ) {
			Binding solution = results.nextBinding();
			for(Iterator<Var> vs = solution.vars(); vs.hasNext(); ) {
				Var v = vs.next();
				if ("v".equals(v.getName())) {
					String varName = solution.get(v).getLiteralLexicalForm();
					this.vs.add(new Variable(varName));
				}
			}
		}
	}

	@Override
	public Iterator<Row> rows() {		
		Query query = QueryFactory.create("select ?g1 ?var ?val where { " + 
				" ?g0 <http://www.w3.org/2001/sw/DataAccess/tests/result-set#solution> ?g1 . " + 
				" ?g1 <http://www.w3.org/2001/sw/DataAccess/tests/result-set#binding> ?g2 . " +
				" ?g2 <http://www.w3.org/2001/sw/DataAccess/tests/result-set#variable> ?var ; " +
				" <http://www.w3.org/2001/sw/DataAccess/tests/result-set#value> ?val . }");
		QueryExecution qexec = QueryExecutionFactory.create(query, ds);
		final Map<Object,Map<Variable,QueryTripleTerm>> rows = HashMapFactory.make();
		for(ResultSet bindings = qexec.execSelect(); bindings.hasNext(); ) {
			Object solution = null;
			Variable v = null;
			QueryTripleTerm n = null;
			Binding b = bindings.nextBinding();
			for(Iterator<Var> vars = b.vars(); vars.hasNext(); ) {
				Var var = vars.next();
				if ("g1".equals(var.getName())) {
					solution =  b.get(var).getBlankNodeLabel();
				} else if ("var".equals(var.getName())) {
					v = new Variable(b.get(var).getLiteralLexicalForm());
				} else if ("val".equals(var.getName())) {
					Node x = b.get(var);
					if (x.isURI()) {
						n = new QueryTripleTerm(new IRI(x.getURI()));
					} else if (x.isLiteral()) {
						StringLiteral l = new StringLiteral(x.getLiteralLexicalForm());
						if (x.getLiteralDatatypeURI() != null) {
							l.setType(new IRI(x.getLiteralDatatypeURI()));
						}
						if (x.getLiteralLanguage() != null) {
							l.setLanguage(x.getLiteralLanguage());
						}
						n = new QueryTripleTerm(new Constant(l));
					} else if (x.isBlank()) {
						n = new QueryTripleTerm(new BlankNode(x.getBlankNodeLabel()));
					} else {
						assert false : x.toString();
					}
				}
			}
			assert v != null;
			assert n != null;
			assert solution != null;
			if (! rows.containsKey(solution)) {
				rows.put(solution, HashMapFactory.<Variable,QueryTripleTerm>make());
			}
			rows.get(solution).put(v, n);
		}

		return new Iterator<Row>() {
			private final Iterator<?> rs = rows.keySet().iterator();

			@Override
			public boolean hasNext() {
				return rs.hasNext();
			}

			@Override
			public Row next() {
				final Map<Variable,QueryTripleTerm> r = rows.get(rs.next());
				return new Row() {
					@Override
					public QueryTripleTerm get(Variable v) {
						return r.get(v);
					}

					@Override
					public Iterator<Variable> variables() {
						return r.keySet().iterator();
					}
				};
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

		};
	}

	@Override
	public Iterator<Variable> variables() {
		return vs.iterator();
	}

}
