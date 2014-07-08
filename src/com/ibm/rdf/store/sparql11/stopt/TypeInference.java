package com.ibm.rdf.store.sparql11.stopt;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.rdf.store.Store;
import com.ibm.rdf.store.runtime.service.types.TypeMap;
import com.ibm.rdf.store.runtime.service.types.TypeMap.TypeCategory;
import com.ibm.rdf.store.sparql11.merger.QueryInfo;
import com.ibm.rdf.store.sparql11.model.AggregateExpression;
import com.ibm.rdf.store.sparql11.model.Expression;
import com.ibm.rdf.store.sparql11.model.ProjectedVariable;
import com.ibm.rdf.store.sparql11.model.Query;
import com.ibm.rdf.store.sparql11.model.RelationalExpression;
import com.ibm.rdf.store.sparql11.model.Variable;
import com.ibm.wala.util.collections.HashMapFactory;
import com.ibm.wala.util.collections.HashSetFactory;
import com.ibm.wala.util.collections.Pair;
import com.ibm.wala.util.functions.Function;

public class TypeInference {
	private List<Expression> overallExpressions = new LinkedList<Expression>();
	
	public TypeInference(STPlan plan, Store store, Query q) {
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
	
	private void inferTypes(STPlan plan) {	
		final Map<Variable, Set<STPlanNode>> castsForVariables = findCasts(plan, new Function<Pair<Variable, Expression>, Pair<Set<Variable>, TypeMap.TypeCategory>>() {
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

	private Map<Variable, Set<STPlanNode>> findCasts(STPlan plan, Function<Pair<Variable, Expression>, Pair<Set<Variable>, TypeMap.TypeCategory>> function) {
		Iterator<STPlanNode> it = plan.getPlanTree().getNodeManager().iterator();
		Map<Variable, Set<STPlanNode>> castsForVariables = HashMapFactory.make();

		while (it.hasNext()) {
			STPlanNode n = it.next();

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
							STPlanNode pred = findEarliestProducerOfVariable(plan, n, k);
							if (pred == null) {
								continue;
							}
								
							// save for a second pass to determine if these types get propagated to other variables that are involved in relational
							// expressions with these variables
							if (!castsForVariables.containsKey(k)) {
								castsForVariables.put(k, HashSetFactory.<STPlanNode>make());
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

	private STPlanNode findEarliestProducerOfVariable(STPlan plan, STPlanNode n,
			Variable v) {
		if (n.getProducedVariables().contains(v)) {
			return n;
		}
		if (n.getPredecessor(plan) == null) {
			return null;
		}
		STPlanNode pred = n.getPredecessor(plan);
		
		if (pred.getProducedVariables() != null && pred.getProducedVariables().contains(v)) {
			findEarliestProducerOfVariable(plan, pred, v);
		}
		return null;
	}

}
