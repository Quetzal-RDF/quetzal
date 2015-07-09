package com.ibm.rdf.store.testSuite;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.ibm.research.sparql.rewriter.ResolutionEngineForJena;
import com.ibm.research.sparql.rewriter.RuleforJena;
import com.ibm.research.sparql.rewriter.SPARQLRewriterForJena;

public class SPINRulesTest {
	private File ruleFile;
	private List<RuleforJena> rules = new LinkedList<RuleforJena>();
	private ResolutionEngineForJena resolutionEngine;

	public SPINRulesTest() throws Exception {
		this.ruleFile = new File(System.getenv("rulesFile"));
		String rulesstr = readFile(ruleFile);
		rules = parseRules(rulesstr);
		resolutionEngine = new ResolutionEngineForJena(rules);
	}
	
	public Query rewrite(String selectQuery) throws Exception {
		return resolutionEngine.unfold(parseToJenaQuery(selectQuery));
	}
	
	public static Query parseToJenaQuery(String queryString)  {
		return QueryFactory.create(queryString);
	}
	
	public static List<RuleforJena> parseRules(String rulesString) {
		String[] rules = rulesString.split("###");
		List<RuleforJena> result = new LinkedList<RuleforJena>();
		int counter = 0;
		for (String rule : rules) {

			Query query = parseToJenaQuery(rule);

			RuleforJena r = new RuleforJena(query, counter++);
			result.add(r);
		}
		return result;

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


	@Test
	public void testOilDeclineVulnerableIndustry() throws Exception {
		rewrite("PREFIX : <http://example.org/> SELECT ?x WHERE { ?x a :OilDeclineVulnerableIndustry . }");
	}
	
	@Test
	public void testOilDeclineVulnerableCompany() throws Exception {
		rewrite("PREFIX : <http://example.org/> SELECT ?x WHERE { ?x a :OilDeclineVulnerableCompany . }");
	}
	
	@Test
	public void testOilDeclineVulnerableLoan() throws Exception {
		rewrite("PREFIX : <http://example.org/> SELECT ?x WHERE { ?x a :OilDeclineVulnerableLoan . }");
	}

}
