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
 package com.ibm.research.rdf.store.sparql11.planner;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.research.rdf.store.sparql11.model.Constant;
import com.ibm.research.rdf.store.sparql11.model.ConstantExpression;
import com.ibm.research.rdf.store.sparql11.model.Expression.ERelationalOp;
import com.ibm.research.rdf.store.sparql11.model.IRI;
import com.ibm.research.rdf.store.sparql11.model.Pattern;
import com.ibm.research.rdf.store.sparql11.model.PatternSet;
import com.ibm.research.rdf.store.sparql11.model.Query;
import com.ibm.research.rdf.store.sparql11.model.QueryTriple;
import com.ibm.research.rdf.store.sparql11.model.QueryTripleTerm;
import com.ibm.research.rdf.store.sparql11.model.RelationalExpression;
import com.ibm.research.rdf.store.sparql11.model.SimplePattern;
import com.ibm.research.rdf.store.sparql11.model.SubSelectPattern;
import com.ibm.research.rdf.store.sparql11.model.VariableExpression;
import com.ibm.research.rdf.store.sparql11.planner.QueryRewriterHelper.RenamedIRIConstant;
import com.ibm.wala.util.collections.HashSetFactory;

public class QueryRewriter {
	private Query rewrQuery;


	public QueryRewriter(Query origQuery) {
		this.rewrQuery = origQuery;
		QueryRewriterHelper helper = new QueryRewriterHelper();
		findIRIConstants(this.rewrQuery
				.getMainPattern(), helper);

		replaceIRIConstants(helper);
	}

	private void findIRIConstants(Pattern pattern, QueryRewriterHelper helper) {

		if (pattern instanceof PatternSet) {
			for (Pattern subPattern : ((PatternSet) pattern).getPatterns()) {
				findIRIConstants(subPattern, helper);
			}
		} else if (pattern instanceof SimplePattern) {
			//
			for (QueryTriple triple : ((SimplePattern) pattern)
					.getQueryTriples()) {
				//
				// Check whether the triple has IRIs (in its subject or object).
				// If it does, then for each such IRI...
				//
				for (IRI tripleIRI : triple.gatherIRIs()) {
					if ((triple.getPredicate().isIRI())
							&& (triple.getPredicate().getIRI()
									.equals(tripleIRI))) {
						continue;
					}
					helper.add(tripleIRI, triple, pattern);
				}

				//
				// Check whether the triple has any constants. If there are,
				// then for each constant...
				//
				for (Constant tripleConstant : triple.gatherConstants()) {
					helper.add(tripleConstant, triple, pattern);
				}
			}
		} else if (pattern instanceof SubSelectPattern) {
			findIRIConstants(((SubSelectPattern) pattern).getGraphPattern(), helper);
		}
		
		if (pattern.getOptionalPatterns() != null) {
			for (Pattern p : pattern.getOptionalPatterns()) {
				findIRIConstants(p, helper);
			}
		}

	}

	private void replaceIRIConstants(QueryRewriterHelper helper) {

		Set<Pattern> patternsChanged = HashSetFactory.make();
		
		for (Map.Entry<IRI, List<RenamedIRIConstant>> entry : helper.IRIsMap.entrySet()) {
			//
			// skip IRIs that only appear in one place
			//
			if (entry.getValue().size() == 1) {
				continue;
			}
			IRI iri = entry.getKey();
			
			QueryTripleTerm newVariableTerm = new QueryTripleTerm(helper.IRIsToVarMap.get(iri));
			boolean isVariableNameUsed = false;
			for (RenamedIRIConstant r : entry.getValue()) {
				QueryTriple triple = r.qt;
				if ((triple.getSubject().isIRI())
						&& (triple.getSubject().getIRI().equals(iri))) {
					isVariableNameUsed = true;
					triple.setSubject(newVariableTerm);
				}
				if ((triple.getObject().isIRI())
						&& (triple.getObject().getIRI().equals(iri))) {
					isVariableNameUsed = true;
					triple.setObject(newVariableTerm);
				}
				if (isVariableNameUsed) {
					RelationalExpression newFilter = new RelationalExpression(
							new VariableExpression(helper.IRIsToVarMap.get(iri).getName()),
							new ConstantExpression(iri), ERelationalOp.EQUAL);
					r.p.addFilter(newFilter);
					patternsChanged.add(r.p);
				}
			}
		}
		
		for (Map.Entry<Constant, List<RenamedIRIConstant>> entry : helper.constantsMap.entrySet()) {
			//
			// skip IRIs that only appear in one place
			//
			if (entry.getValue().size() == 1) {
				continue;
			}
			Constant c = entry.getKey();
			
			QueryTripleTerm newVariableTerm = new QueryTripleTerm(helper.ConstantToVarMap.get(c));
			boolean isVariableNameUsed = false;
			for (RenamedIRIConstant r : entry.getValue()) {
				QueryTriple triple = r.qt;
				if ((triple.getSubject().isIRI())
						&& (triple.getSubject().getIRI().equals(c))) {
					isVariableNameUsed = true;
					triple.setSubject(newVariableTerm);
				}
				if ((triple.getObject().isIRI())
						&& (triple.getObject().getIRI().equals(c))) {
					isVariableNameUsed = true;
					triple.setObject(newVariableTerm);
				}
				if (isVariableNameUsed) {
					RelationalExpression newFilter = new RelationalExpression(
							new VariableExpression(helper.ConstantToVarMap.get(c).getName()),
							new ConstantExpression(c), ERelationalOp.EQUAL);
					r.p.addFilter(newFilter);
					patternsChanged.add(r.p);
				}
			}
		}
		
		for (Pattern p : patternsChanged) {
			p.pushFilters();
		}
	}


	public Query getRewrQuery() {
		return rewrQuery;
	}

	//
	// Output the contents of the object as a string
	//
	public String toString() {
		StringBuffer returnString = new StringBuffer();

		returnString.append("<STQueryRewriter>Rewritten Query:\n"
				+ this.rewrQuery);

		return returnString.toString();
	}

}
