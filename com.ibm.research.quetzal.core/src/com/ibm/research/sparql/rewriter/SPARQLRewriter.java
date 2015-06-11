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

package com.ibm.research.sparql.rewriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.ParsedTupleQuery;
import org.openrdf.query.parser.QueryParser;
import org.openrdf.query.parser.sparql.SPARQLParserFactory;
import org.openrdf.queryrender.sparql.SPARQLQueryRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mariano Rodriguez <mrodrig@us.ibm.com>
 * 
 */
public class SPARQLRewriter {

	// private OWLOntology ontology;
	private File ruleFile;
	private List<Rule> rules = new LinkedList<Rule>();
	private ResolutionEngine resolutionEngine;

	private static final Logger log = LoggerFactory.getLogger(SPARQLRewriter.class);

	/**
	 * @throws Exception
	 * 
	 */
	public SPARQLRewriter(File rulesFile) throws Exception {
		this.ruleFile = rulesFile;
		String rulesstr = readFile(ruleFile);
		rules = parse(rulesstr);
		resolutionEngine = new ResolutionEngine(rules);
	}

	private List<Rule> parse(String rulesString) throws Exception {
		String[] rules = rulesString.split("###");
		List<Rule> result = new LinkedList<Rule>();
		for (String rule : rules) {

			SPARQLParserFactory fac = new SPARQLParserFactory();
			QueryParser parser = fac.getParser();
			ParsedQuery query = parser.parseQuery(rule, null);

			Rule r = new Rule(query);
			result.add(r);


		}
		return result;
	}

	public String rewrite(String selectQuery) throws Exception {
		SPARQLParserFactory fac = new SPARQLParserFactory();
		QueryParser parser = fac.getParser();
		ParsedTupleQuery query = (ParsedTupleQuery) parser.parseQuery(selectQuery, null);

		resolutionEngine.unfold(query);

		SPARQLQueryRenderer renderer = new SPARQLQueryRenderer();
		String renderedQuery = renderer.render(query);

		return renderedQuery;
	}

	public static void main(String args[]) {
		File rulesFile = new File(args[0]);

		try {

			SPARQLRewriter rewriter = new SPARQLRewriter(rulesFile);

//			System.out
//					.println(rewriter
//							.rewrite("PREFIX : <http://example.org/> SELECT ?x WHERE { ?x a :OilDeclineBenefittingIndustry . }"));
//			
			
			System.out
			.println(rewriter
					.rewrite("PREFIX : <http://example.org/> SELECT * WHERE {  ?x :S ?y  }"));
			
			
//			
//			SPARQLParserFactory fac = new SPARQLParserFactory();
//			QueryParser parser = fac.getParser();
//			ParsedTupleQuery query = (ParsedTupleQuery) parser.parseQuery("PREFIX : <http://example.org/> SELECT (COUNT(*) as ?c) WHERE {  ?x :S ?y . BIND(:testc as ?yoyo) . FILTER(?x = 2) OPTIONAL {?x :P ?t} . { SELECT ?momo { ?momo :waka ?toto}  } } GROUP BY ?x", null);
//			SPARQLQueryRenderer renderer = new SPARQLQueryRenderer();
//			String renderedQuery = renderer.render(query);
//			System.out.println(renderedQuery);
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private String readFile(File file) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(file));
		StringBuilder response = new StringBuilder();
		String line = in.readLine();
		while (line != null) {
			response.append(line);
			response.append("\n");
			line = in.readLine();
		}
		in.close();
		return response.toString();
	}

	// class StatementPatternCollector extends QueryModelVisitorBase<Exception>
	// {
	// private List<StatementPattern> statementPatterns = new
	// LinkedList<StatementPattern>();
	// boolean done = false;
	//
	// @Override
	// public void meet(StatementPattern node) throws Exception {
	// ValueFactoryImpl fac = ValueFactoryImpl.getInstance();
	//
	// URI subject = fac.createURI("http://mariano1.com");
	// URI pred = fac.createURI("http://mariano2.com");
	// URI object = fac.createURI("http://mariano3.com");
	//
	//
	// Union union = new Union();
	// Var s = new Var("xxxxxx", subject);
	// Var p = new Var("yyyyyy", pred);
	// Var o = new Var("zzzzzz", object);
	//
	// StatementPattern st1 = new StatementPattern(s , p, o);
	// StatementPattern st2 = new StatementPattern(s , s, s);
	//
	// union.setLeftArg(st1);
	// union.setRightArg(st2);
	//
	// node.replaceWith(union);
	//
	// super.meet(node);
	// }
	//
	// public List<StatementPattern> getPatterns() {
	// return this.statementPatterns;
	// }
	// }

}
