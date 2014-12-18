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
 package com.ibm.rdf.store.customer;

import java.util.Collections;
import java.util.Map;
import java.util.Random;

import com.ibm.research.rdf.store.sparql11.SparqlParserUtilities;
import com.ibm.research.rdf.store.sparql11.model.Pattern;
import com.ibm.research.rdf.store.sparql11.model.Pattern.EPatternSetType;
import com.ibm.research.rdf.store.sparql11.model.PatternSet;
import com.ibm.research.rdf.store.sparql11.model.PropertyTerm;
import com.ibm.research.rdf.store.sparql11.model.Query;
import com.ibm.research.rdf.store.sparql11.model.QueryTriple;
import com.ibm.research.rdf.store.sparql11.model.QueryTripleTerm;
import com.ibm.research.rdf.store.sparql11.model.SimplePattern;
import com.ibm.research.rdf.store.sparql11.model.Variable;
import com.ibm.wala.util.collections.HashMapFactory;

public class QueryKBCreator {
	static int counter = 0;
	static Random r = new Random();
	static int numPatternsToCreate = 0;
	static float optionalsAttenuationRate = 0;

	
	public static void main(String[] args) {
		String queryFile = args[0];
		numPatternsToCreate = Integer.parseInt(args[1]);
		optionalsAttenuationRate = Float.parseFloat(args[2]);
		
		Query query = SparqlParserUtilities.parseSparqlFile(queryFile,
				Collections.EMPTY_MAP);
		Map<Variable, String> variablesToConstants = HashMapFactory.make();

		Pattern p = query.getMainPattern();
		for (int i = 0; i < numPatternsToCreate; i++) {
			variablesToConstants.clear();
			createKB(p, false, variablesToConstants);
		}

	}
	
	private static void createKB(Pattern p, boolean isOptional, Map<Variable, String> variablesToConstants) {
		if (p instanceof PatternSet) {
			PatternSet ps = (PatternSet) p;
			createKB(ps, isOptional, variablesToConstants);
		} else if (p instanceof SimplePattern) {
			createKB((SimplePattern) p, isOptional, variablesToConstants);
		}
	}
	
	private static void createKB(PatternSet ps, boolean isOptional, Map<Variable, String> varToConstants) {
		Map<Variable, String> variablesToConstants = varToConstants;
		
		for (Pattern p : ps.getPatterns()) {
			// every branch of a union gets its own copy of variableToConstants since union branches don't share variables
			if (ps.getType().equals(EPatternSetType.UNION)) {
				variablesToConstants = HashMapFactory.make();
				variablesToConstants.putAll(varToConstants);
			}
			createKB(p, isOptional, variablesToConstants);
		}
		if (ps.getOptionalPatterns() != null) {
			for (Pattern op: ps.getOptionalPatterns()) {
				if (r.nextDouble() < optionalsAttenuationRate) {
					continue;			// skip this and all other nested patterns
				}
				createKB(op, true, variablesToConstants);
			}
		}
	}
	
	private static void createKB(SimplePattern sp, boolean isOptional, Map<Variable, String> variablesToConstants) {

		for (QueryTriple t : sp.getQueryTriples()) {
			String subject, predicate, object = null;	
			subject = handleTerm(t.getSubject(), variablesToConstants);
			predicate = handleTerm(t.getPredicate(), variablesToConstants);
			object = handleTerm(t.getObject(), variablesToConstants);
			System.out.println(subject + " " + predicate + " " + object + " . ");

		}
	}

	private static String handleTerm(QueryTripleTerm t, Map<Variable, String> variablesToConstants) {
		String term;
		if (t.isVariable()) {
			if (variablesToConstants.containsKey(t.getVariable())) {
				term = variablesToConstants.get(t.getVariable());
			} else {
				term = t.toString();
				term = "<urn://" + term.substring(1) + counter + ">";
				variablesToConstants.put(t.getVariable(), term);
				counter++;
			}
		} else if (t.isIRI()) {
			term = t.getIRI().toString();
		} else if (t.isBlankNode()) {
			term = t.getBlankNode().toString();
		} else if (t.isConstant()) {
			term = t.getConstant().toTypedString();
		} else {
			throw new UnsupportedOperationException("don't handle this term yet");
		}
		
		return term;
	}
	private static String handleTerm(PropertyTerm t, Map<Variable, String> variablesToConstants) {
		String term;
		if (t.isVariable()) {
			if (variablesToConstants.containsKey(t.getVariable())) {
				term = variablesToConstants.get(t.getVariable());
			} else {
				term = t.toString();
				term = "<urn://" + term.substring(1) + counter + ">";
				variablesToConstants.put(t.getVariable(), term);
				counter++;
			}
		} else if (t.isIRI()) {
			term = t.getIRI().toString();
		} else if (t.isBlankNode()) {
			term = t.getBlankNode().toString();
		} else if (t.isConstant()) {
			term = t.getConstant().toTypedString();
		} else if (t.isPath()) {
			term = t.getPath().toString();
		} else {
			throw new UnsupportedOperationException("don't handle this term yet");
		}
		
		return term;
	}


}
