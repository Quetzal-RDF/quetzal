
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
import java.util.Map;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.ibm.wala.util.collections.HashMapFactory;

/**
 * @author Kavitha Srinivas <ksrinivs@us.ibm.com>
 * @author Mariano Rodriguez <mrodrig@us.ibm.com>
 * 
 */
public class SPARQLRewriterForJena {

	public static boolean GENERATE_TRACE = false;
	public static final boolean GENERATE_TRACE_SUMMARY = false;
	
	private File ruleFile;
	private List<RuleforJena> rules = new LinkedList<RuleforJena>();
	private static int counter = 0;

	private ResolutionEngineForJena resolutionEngine;


	/**
	 * @throws Exception
	 * 
	 */
	public SPARQLRewriterForJena(File rulesFile) throws Exception {
		this.ruleFile = rulesFile;
		if (ruleFile.getAbsolutePath().endsWith("sparql")) {
			String rulesstr = readFile(ruleFile);
			rules = parseRules(rulesstr);
		} else if (ruleFile.getAbsolutePath().endsWith(".ttl")) {
			rules = SPINRuleParser.parseRules(ruleFile);
		}

		resolutionEngine = new ResolutionEngineForJena(rules);
	}


	public Query rewrite(String selectQuery) throws Exception {

		return resolutionEngine.unfold(parseToJenaQuery(selectQuery));

	}
	
	public List<RuleforJena> getRules() {
		return rules;
	}


	public static Query parseToJenaQuery(String queryString)  {
		return QueryFactory.create(queryString);
	}
	
	public static List<RuleforJena> parseRules(String rulesString) {
		String[] rules = rulesString.split("###");
		List<RuleforJena> result = new LinkedList<RuleforJena>();
		for (String rule : rules) {

			Query query = parseToJenaQuery(rule);

			RuleforJena r = new RuleforJena(query, counter++);
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
					.rewrite("PREFIX : <http://example.org/> SELECT * WHERE { ?s :S ?o. }"));

			
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

	

}
