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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.runtime.service.types.TypeMap;
import com.ibm.research.rdf.store.runtime.service.types.TypeMap.TypeCategory;
import com.ibm.research.rdf.store.sparql11.QueryInfo;
import com.ibm.research.rdf.store.sparql11.model.Expression;
import com.ibm.research.rdf.store.sparql11.model.ProjectedVariable;
import com.ibm.research.rdf.store.sparql11.model.Query;
import com.ibm.research.rdf.store.sparql11.model.RelationalExpression;
import com.ibm.research.rdf.store.sparql11.model.Variable;
import com.ibm.wala.util.collections.HashMapFactory;
import com.ibm.wala.util.collections.HashSetFactory;
import com.ibm.wala.util.collections.Pair;
import com.ibm.wala.util.functions.Function;

public class TypeInference {
	private List<Expression> overallExpressions = new LinkedList<Expression>();
	
	public TypeInference(Plan plan, Store store, Query q) {
		QueryInfo queryInfo = new QueryInfo(q);
		if (queryInfo.getProjectedVariables() != null) {
			for (ProjectedVariable v : queryInfo.getProjectedVariables()) {
				if (v.getExpression() != null) {
					overallExpressions.add(v.getExpression());
				}
			}
		}
		inferTypes(plan);
	}
	
	private void inferTypes(Plan plan) {	
		final Map<Variable, Set<PlanNode>> castsForVariables = findCasts(plan, new Function<Pair<Variable, Expression>, Pair<Set<Variable>, TypeMap.TypeCategory>>() {
			@Override
			public Pair<Set<Variable>, TypeCategory> apply(Pair<Variable, Expression> p) {
				TypeMap.TypeCategory cat = p.snd.getTypeRestriction(p.fst);
				return Pair.make(Collections.singleton(p.fst), cat);
			}
		});	
		
		findCasts(plan, new Function<Pair<Variable, Expression>, Pair<Set<Variable>, TypeMap.TypeCategory>>() {
			@Override
			public Pair<Set<Variable>, TypeCategory> apply(Pair<Variable, Expression> p) {
				if (p.snd instanceof RelationalExpression) {
					RelationalExpression rexp = (RelationalExpression) p.snd;
					Set<Variable> relationalVars = HashSetFactory.make(rexp.gatherVariables());
					boolean isCastable = false;
					for (Variable v : relationalVars) {
						if (castsForVariables.containsKey(v)) {
							isCastable = true;
							break;
						}
					}
					if (isCastable) {
						relationalVars.remove(p.fst);
						return Pair.make(relationalVars, TypeMap.TypeCategory.NUMERIC);
					}
				} 
				return Pair.make(Collections.<Variable>emptySet(), TypeMap.TypeCategory.NONE);
			}
		});	
		

	}

	private Map<Variable, Set<PlanNode>> findCasts(Plan plan, Function<Pair<Variable, Expression>, Pair<Set<Variable>, TypeMap.TypeCategory>> function) {
		Iterator<PlanNode> it = plan.getPlanTree().getNodeManager().iterator();
		Map<Variable, Set<PlanNode>> castsForVariables = HashMapFactory.make();

		while (it.hasNext()) {
			PlanNode n = it.next();

			HashMap<Variable, List<Expression>> expVariables = HashMapFactory.make();
			Set<Expression> expressionsInScope = HashSetFactory.make(overallExpressions);
			expressionsInScope.addAll(n.gatherAllExpressions());

			for (Expression e : expressionsInScope) {
				for (Variable v : e.gatherVariables()) {
					if (!expVariables.containsKey(v)) {
						expVariables.put(v, new LinkedList<Expression>());
					}
					expVariables.get(v).add(e);
				}
			}
			
			for (Variable v : n.getAvailableVariables()) {
				if (!expVariables.containsKey(v)) {
					continue;
				}
				for (Expression e : expVariables.get(v)) {
					Pair<Set<Variable>, TypeMap.TypeCategory> cat = function.apply(Pair.make(v, e));
					if (cat.snd == TypeMap.TypeCategory.NUMERIC || cat.snd == TypeMap.TypeCategory.DATE || cat.snd == TypeMap.TypeCategory.DATETIME) {
						for (Variable k : cat.fst) {
							PlanNode pred = findEarliestProducerOfVariable(plan, n, k);
							if (pred == null) {
								continue;
							}
								
							// save for a second pass to determine if these types get propagated to other variables that are involved in relational
							// expressions with these variables
							if (!castsForVariables.containsKey(k)) {
								castsForVariables.put(k, HashSetFactory.<PlanNode>make());
							} else {
								castsForVariables.get(k).add(pred);
							}
								
							pred.needsTypeCheck(k, e);		
							
						}
					}
					
				}
			}
		}
		return castsForVariables;
	}

	private PlanNode findEarliestProducerOfVariable(Plan plan, PlanNode n,
			Variable v) {
		if (n.getProducedVariables().contains(v)) {
			return n;
		}
		if (n.getPredecessor(plan) == null) {
			return null;
		}
		PlanNode pred = n.getPredecessor(plan);
		
		if (pred.getProducedVariables() != null && pred.getProducedVariables().contains(v)) {
			findEarliestProducerOfVariable(plan, pred, v);
		}
		return null;
	}

}
