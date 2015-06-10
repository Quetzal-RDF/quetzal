
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

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;

/**
 * @author Kavitha Srinivas <ksrinivs@us.ibm.com>
 * 
 */
public class SPARQLRewriterForJena {

	private File ruleFile;
	private List<RuleforJena> rules = new LinkedList<RuleforJena>();
	private ResolutionEngineForJena resolutionEngine;


	/**
	 * @throws Exception
	 * 
	 */
	public SPARQLRewriterForJena(File rulesFile) throws Exception {
		this.ruleFile = rulesFile;
		String rulesstr = readFile(ruleFile);
		rules = parseRules(rulesstr);
		resolutionEngine = new ResolutionEngineForJena(rules);
	}


	public String rewrite(String selectQuery) throws Exception {

		return resolutionEngine.unfold(parseToJenaQuery(selectQuery)).toString();

	}
	
	public static Query parseToJenaQuery(String queryString)  {
		return QueryFactory.create(queryString);
	}
	
	public static List<RuleforJena> parseRules(String rulesString) {
		String[] rules = rulesString.split("###");
		List<RuleforJena> result = new LinkedList<RuleforJena>();
		for (String rule : rules) {

			Query query = parseToJenaQuery(rule);

			RuleforJena r = new RuleforJena(query);
			result.add(r);
		}
		return result;

	}
	

	public static void main(String args[]) {
		File rulesFile = new File(args[0]);

		try {

			SPARQLRewriterForJena rewriter = new SPARQLRewriterForJena(rulesFile);

//			System.out
//					.println(rewriter
//							.rewrite("PREFIX : <http://example.org/> SELECT * WHERE { ?x a :OilDeclineVulnerableIndustry . }"));
			

			System.out
			.println(rewriter
					.rewrite("PREFIX : <http://example.org/> SELECT * WHERE { ?s ?p ?o. }"));

			
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
