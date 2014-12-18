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
 package com.ibm.research.rdf.store.utilities;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.ibm.research.rdf.store.sparql11.SparqlParserUtilities;
import com.ibm.research.rdf.store.sparql11.model.OrderCondition;
import com.ibm.research.rdf.store.sparql11.model.Pattern;
import com.ibm.research.rdf.store.sparql11.model.PatternSet;
import com.ibm.research.rdf.store.sparql11.model.ProjectedVariable;
import com.ibm.research.rdf.store.sparql11.model.Query;
import com.ibm.research.rdf.store.sparql11.model.SelectClause;
import com.ibm.research.rdf.store.sparql11.model.SelectClause.ESelectModifier;
import com.ibm.research.rdf.store.sparql11.model.SelectQuery;
import com.ibm.research.rdf.store.sparql11.model.SolutionModifiers;
import com.ibm.research.rdf.store.sparql11.model.SubSelectPattern;
import com.ibm.research.rdf.store.sparql11.model.VariableExpression;
import com.ibm.wala.util.collections.HashSetFactory;

/**
 * Fixes the mismatch between relational databases (DB2/Postgresql) and SPARQL semantics.  
 * In SPARQL its ok to have a DISTINCT in the select, with an ORDER BY on any expression.  Neither Postgresql or DB2
 * allow it. The fix here rewrites the query as a subselect with an outer query that contains the distinct and an inner
 * subquery that contains the ORDER BY on an expression with no distinct on the inner select.  TODO: handle subselects that may have 
 * a similar pattern?
 * @author ksrinivs
 *
 */
public class DistinctOrderByRewriter {

	public static boolean shouldRewrite(Query query) {
		if (checkQuery(query)) {
			return true;
		}
		if (checkSubQuery(query)) {
			return true;
		}
		return false;
	}
	
	private static boolean checkSubQuery(Query query) {
		for (SubSelectPattern p : findSubSelectPattern(query)) {
			if (checkSelectClauseAndSolutionMod(p.getSelectClause(), p.getSolutionModifier())) {
				return true;
			}
		}
		return false;
	}

	private static boolean checkQuery(Query query) {
		if (query.isSelectQuery()) {
			SelectQuery sq = query.getSelectQuery();
			if (checkSelectClauseAndSolutionMod(sq.getSelectClause(), sq.getSolutionModifier())) {
				return true;
			}
		}
		return false;
	}
	
	private static Set<SubSelectPattern> findSubSelectPattern(Query q) {
		Set<SubSelectPattern> subselects = HashSetFactory.make();
		for (Pattern p : q.getMainPattern().getSubPatterns(false)) {
			if (p instanceof SubSelectPattern) {
				subselects.add((SubSelectPattern) p);
			}
		}
		return subselects;
	}

	private static boolean checkSelectClauseAndSolutionMod(SelectClause selectClause, SolutionModifiers solnModifiers) {
		if (selectClause.getSelectModifier() != null
				&& selectClause.getSelectModifier() == ESelectModifier.DISTINCT) {
			if (solnModifiers == null) {
				return false;
			}
			List<OrderCondition> oc = solnModifiers.getOrderClause();
			if (oc == null || oc.isEmpty()) {
				return false;
			}
			for (OrderCondition o : oc) {
				if (!(o.getExpression() instanceof VariableExpression)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static Query rewrite(Query query) {
		Query newQuery = null;
		if (checkQuery(query)) {
			SelectQuery newSelect = new SelectQuery();
			newSelect.setSelectClause(query.getSelectQuery().getSelectClause());
			
			SubSelectPattern subselectPattern = createNewSubSelect(query.getSelectQuery().getGraphPattern(), query.getSelectQuery().getSolutionModifier(), query.getSelectQuery().getSelectClause());
			
			newSelect.setGraphPattern(subselectPattern);
			newQuery = new Query(query.getPrologue(), newSelect);
		}
		
		if (checkSubQuery(query)) {
			Set<SubSelectPattern> subselects = findSubSelectPattern(query);
			if (newQuery == null) {
				newQuery = query;
			}
			for (SubSelectPattern sp : subselects) {
				if (checkSelectClauseAndSolutionMod(sp.getSelectClause(), sp.getSolutionModifier())) {
					SubSelectPattern newSubSelect = createNewSubSelect(sp.getGraphPattern(), sp.getSolutionModifier(), sp.getSelectClause());
					SubSelectPattern newSelect = new SubSelectPattern();
					newSelect.setSelectClause(sp.getSelectClause());
					newSelect.setGraphPattern(newSubSelect);
					newSelect.setParent(sp.getParent());
					Pattern parent = sp.getParent();
					if (parent instanceof PatternSet) {
						PatternSet ps = (PatternSet) parent;
						List<Pattern> children = ps.getPatterns();
						int i = children.indexOf(sp);
						children.remove(sp);
						children.add(i, newSelect);
					}
				}
			}
		}
		return newQuery;
	}

	private static SubSelectPattern createNewSubSelect(Pattern graphPattern, SolutionModifiers solnModifer, SelectClause selectClause) {
		SubSelectPattern subselectPattern = new SubSelectPattern();
		subselectPattern.setGraphPattern(graphPattern);
		subselectPattern.setSolutionModifier(solnModifer);
		
		SelectClause selClause = new SelectClause();
		for (ProjectedVariable pv : selectClause.getProjectedVariables()) {
			selClause.addProjectedVariable(pv);
		}
		subselectPattern.setSelectClause(selClause);
		return subselectPattern;
	}
	
	public static void main(String[] args) {
		Query q = SparqlParserUtilities.parseSparqlFile(args[0], new HashMap());
		System.out.println("Should rewrite:" + shouldRewrite(q));
		if (shouldRewrite(q)) {
			System.out.println("Rewrite:" + rewrite(q));
		}
	}
}
