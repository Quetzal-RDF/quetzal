package com.ibm.research.sparql.rewriter;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jsqlparser.expression.operators.relational.ExpressionList;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Node_Literal;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.algebra.Algebra;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.OpAsQuery;
import com.hp.hpl.jena.sparql.algebra.OpWalker;
import com.hp.hpl.jena.sparql.algebra.TransformCopy;
import com.hp.hpl.jena.sparql.algebra.Transformer;
import com.hp.hpl.jena.sparql.algebra.op.OpBGP;
import com.hp.hpl.jena.sparql.algebra.op.OpExtend;
import com.hp.hpl.jena.sparql.algebra.op.OpGroup;
import com.hp.hpl.jena.sparql.algebra.op.OpJoin;
import com.hp.hpl.jena.sparql.algebra.op.OpProject;
import com.hp.hpl.jena.sparql.algebra.op.OpTable;
import com.hp.hpl.jena.sparql.algebra.op.OpTriple;
import com.hp.hpl.jena.sparql.algebra.op.OpUnion;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.core.VarExprList;
import com.hp.hpl.jena.sparql.expr.E_Bound;
import com.hp.hpl.jena.sparql.expr.E_Conditional;
import com.hp.hpl.jena.sparql.expr.E_Str;
import com.hp.hpl.jena.sparql.expr.E_StrConcat;
import com.hp.hpl.jena.sparql.expr.ExprAggregator;
import com.hp.hpl.jena.sparql.expr.ExprList;
import com.hp.hpl.jena.sparql.expr.ExprVar;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.expr.aggregate.AggCount;
import com.hp.hpl.jena.sparql.expr.nodevalue.NodeValueNode;
import com.hp.hpl.jena.sparql.graph.NodeTransformLib;
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

/**
 * @author Kavitha Srinivas <ksrinivs@us.ibm.com>
 * @author Mariano Rodriguez <mrodrig@us.ibm.com>
 * 
 */
public class ResolutionEngineForJena {

	private List<RuleforJena> rules;
	private boolean consequentsExistInDB = false;

	public ResolutionEngineForJena(List<RuleforJena> rules, boolean consequentsExistInDB) {
		this.rules = rules;
		this.consequentsExistInDB = consequentsExistInDB;
	}
	
	public ResolutionEngineForJena(List<RuleforJena> rules) {
		this.rules = rules;
	}

	/***
	 * Takes a SELECT query and unroll it into a more complex SPARQL query under
	 * rules
	 * 
	 * @param query
	 */
	public Query unfold(Query query) {

		ResolutionVisitor visitor = new ResolutionVisitor(rules);
		Op body = Algebra.compile(query);

		OpVariableVistor<String> collector = new OpVariableVistor<String>(body,
				true) {
			@Override
			protected String processVar(Node v) {
				return v.getName();
			}
		};
		OpWalker.walk(body, collector);

		Set<String> bindingNames = new HashSet<String>(collector.getResult());

		visitor.setBindingNames(bindingNames);

		Op newQuery = body;
		newQuery = Transformer.transform(visitor, body);

		
		VarExprList expr = new VarExprList();	
	//	E_StrConcat trace = getRuleTracingConcat(newQuery);
		E_StrConcat trace = null;
		
		if (trace!=null) {
			expr.add(Var.alloc("trace"),trace);		
			newQuery = OpExtend.extend(newQuery, expr);
			newQuery = OpJoin.create(newQuery, OpTable.unit());
			
			if (SPARQLRewriterForJena.GENERATE_TRACE_SUMMARY) {
				
				VarExprList groupVars = new VarExprList();
				groupVars.add(Var.alloc("trace"));
				AggCount count = new AggCount();
				ExprAggregator aggregators = new ExprAggregator(Var.alloc("trace.sum"), count);
				List<ExprAggregator> list = new LinkedList<ExprAggregator>();
				list.add(aggregators);
				
				Op groupBy = new OpGroup(newQuery, groupVars, list);
				
				VarExprList expr2 = new VarExprList();	
				expr2.add(Var.alloc("sum"), new ExprVar("trace.sum"));			
				groupBy = OpExtend.extend(groupBy, expr2);
				List<Var> vars = new LinkedList<Var>();
				vars.add(Var.alloc("sum"));
				vars.add(Var.alloc("trace"));
				OpProject project = new OpProject(groupBy, vars);
				newQuery = project;
				
				
			}

		}
		
		
		
		
		
		Query q = OpAsQuery.asQuery(newQuery);
		q.setDistinct(true);	// KAVITHA: Not setting distinct on the consequent leaves duplicates
		return q;

	}
	
	
	/***
	 * Collects all the variables of the form RULEID_XXX to crate a new BIND that concatenates
	 * all of their values in a single string. The value of each RULEID_XXX column contains a number, indicating 
	 * the rules that were applied to create that row, mulitple per row. By concatenating we have the history
	 * trace of all the rules in a single column, which we can later use to ORDER BY and COUNT. 
	 * @param query
	 */
	private E_StrConcat getRuleTracingConcat(Op query) {
		
		OpVariableVistor<String> collector = new OpVariableVistor<String>(query,
				true) {
			@Override
			protected String processVar(Node v) {
				if (v.getName().startsWith("RULEID"))
				return v.getName();
				else return null;					
			}
		};
		OpWalker.walk(query, collector);

		// Apart from collecting, we try to sort based on the numeric suffix, numerically. 
		// This allows to have a concat that traces the application of the rules in the correct
		// order
		Set<String> bindingNames = new HashSet<String>(collector.getResult());
		bindingNames.remove(null);

		
		if (bindingNames.size() == 0) {
			return null;
		} 
		
		List<Integer> orderedList = new LinkedList<Integer>();
		for (String name: bindingNames) {
			String substring = name.substring(7, name.length());
			Integer value = Integer.valueOf(substring);
			orderedList.add(value);
		}
		Collections.sort(orderedList);

		List<E_Conditional> conditionals = new LinkedList<E_Conditional>();
		for (Integer value : orderedList) {
			
			E_Bound condition = new E_Bound(new ExprVar("RULEID_"+value));
			E_Str str = new E_Str(new ExprVar("RULEID_"+value));
			NodeValue empty = NodeValueNode.makeNodeString("");
			E_Conditional conditional = new E_Conditional(condition, str, empty);
			conditionals.add(conditional);
		}
		
		ExprList list = new ExprList();
		for (int i = 0; i < conditionals.size(); i++) {
			list.add(conditionals.get(i));

			// Appending a spearator for the rule IDs 
			if (i+1 < conditionals.size())
				list.add(NodeValueNode.makeNodeString("-"));
		}
		E_StrConcat concat = new E_StrConcat(list);
		
		
		
		
		return concat;


		
	}

	public class ResolutionVisitor extends TransformCopy {

		private Set<Op> visited;

		private Set<String> queryBindingNames;
		
		private List<RuleforJena> rules;

		public ResolutionVisitor(List<RuleforJena> rules) {
			this.rules = rules;
			this.visited = new HashSet<Op>();
		}
		
		public ResolutionVisitor(List<RuleforJena> rules, Set<String> queryBindingNames) {
			this.rules = rules;
			this.visited = new HashSet<Op>();
			this.queryBindingNames = queryBindingNames;
		}

		public void setBindingNames(Set<String> names) {
			this.queryBindingNames = names;
		}

		public void clearVisited() {
			visited.clear();
		}

		/****
		 * If the visited pattern unifies with rule, and the node has not been
		 * visited, it will replace the node (in the nodes parent) with a union
		 * consisting of the node unified with the body of all the rules that
		 * unified with the node. Jena has a visit(OpTriple t) method which
		 * never seems to get visited. visit BGP instead
		 */

		@Override
		public Op transform(OpBGP opBGP) {
			if (visited.contains(opBGP))
				return opBGP;
			visited.add(opBGP);
			Op op = opBGP;
			
			Map<Triple, Op> tripleToOp = new HashMap<Triple, Op>();
			
			for (Triple triple : opBGP.getPattern().getList()) {
				OpTriple opTriple = new OpTriple(triple);
				List<Op> alternatives = new LinkedList<Op>();
				if (consequentsExistInDB) {
					alternatives.add(opTriple);
				} 
//				else {
//					alternatives.add(OpTable.unit());
//				}
				
				for (RuleforJena rule : rules) {

					try {
						rule = rule.getFreshRule();
					} catch (Exception e) {
						throw new RuntimeException(e);
					}

					SubstitutionForJena s = StatementUnifierForJena.getMGU(
							opTriple, rule.consequent);
					if (s == null)
						continue;

					// The rule matches the node, replacing (executing a
					// resolution
					// step)

					// Preparing the body of the rule
					Op body = rule.antecedent;
					SubstitutionApplierForJena app = new SubstitutionApplierForJena(
							s);
					body = NodeTransformLib.transform(app, body);

					/*
					 * If the substitution affects any variables in the domain
					 * of the original query then we need to add a BIND(...)
					 * element, using extensions, for example. If x is in the
					 * original query, and we have the substitution
					 * x/<example.org> we need the extension BIND(<example.org>
					 * as ?x) Example given by Mariano: ?x hasName "mariano" :-
					 * ?x hasID 1 ?x hasName ?y :- ?x hasFullName ?y
					 * 
					 * Query: ?x hasName ?y
					 * 
					 * Application of rule 1's substitution for ?y has the
					 * rather nasty effect of binding ?y to "Mariano" but this
					 * is very local (i.e., it must only happen in the context
					 * of ?x hasID 1. Hence, ?y is not substituted directly but
					 * BIND is used locally to extend ?y for the context where
					 * ?x hasID 1.
					 */
					VarExprList expr = new VarExprList();
					for (Node n : s.getMap().keySet()) {
						String name = n.getName();
						if (!queryBindingNames.contains(name)) {
							continue;
						}

						Node exp = s.get(n);
						assert exp instanceof Var;
//						ExprVar v = new ExprVar(name);
						expr.add(Var.alloc(name) ,new NodeValueNode(exp));
					}

					Op bind = null;

					if (!expr.isEmpty()) {
						bind = OpExtend.extend(body, expr);
					} else {
						bind = body;
					}
					
					Op newBind = bind;
					boolean changed = true;
					while (changed) {
						newBind = Transformer.transform(new ResolutionVisitor(this.rules, this.queryBindingNames), newBind);
						if (newBind != bind) {
							bind = newBind;
							changed = true;
						} else {
							changed = false;
						}
					};
					
					alternatives.add(bind);
				}
				// done with triple
				// constructing UNION operator, a binary tree, removing 2 at a
				// time 
				if (alternatives.isEmpty()) {
					continue;
				}
				if (consequentsExistInDB && alternatives.size() == 1) {
					continue;
				}
				
				Op union = createUnionOfOps(alternatives);	
				union = OpJoin.create(union, OpTable.unit());
				tripleToOp.put(triple, union);
			}
			if (tripleToOp.isEmpty()) {
				// no real transformation occurs, return the pattern untransformed
				return opBGP;
			}
			BasicPattern p = new BasicPattern();
			Op union = null;
			if (tripleToOp.values().size() == 1) {
				union = tripleToOp.values().iterator().next();
			} else {
				union = createJoinOfOps(new LinkedList<Op>(tripleToOp.values()));	// this is now the join of all transformed triples
			}
			
			// iterate on this new expanded query until we ensure we have no more unfolding
			
			for (Triple t : opBGP.getPattern().getList()) {					// deal with untransformed triples, adding them to a basic pattern
				if (!tripleToOp.containsKey(t)) {
					p.add(t);
				}
			}
			if (p.isEmpty()) {
				return union;
			}
			OpBGP newBGP = new OpBGP(p);
			op = OpJoin.create(union, newBGP);

			return op;
		}

		private Op createJoinOfOps(List<Op> alternatives) {
			Op union = null;
			if (alternatives.size() == 1) {
				return alternatives.get(0);
			}
			while (!alternatives.isEmpty()) {
				Op newunion = null;
				if (union == null) {
					newunion = OpJoin.create(alternatives.remove(0),
							alternatives.remove(0));
				} else {
					newunion = OpJoin.create(alternatives.remove(0),
							union);
				}
				union = newunion;
			}
			return union;		
		}
		
		private Op createUnionOfOps(List<Op> alternatives) {
			Op union = null;
			if (alternatives.size() == 1) {
				return alternatives.get(0);
			}
			while (!alternatives.isEmpty()) {
				OpUnion newunion = null;
				if (union == null) {
					newunion = new OpUnion(alternatives.remove(0),
							alternatives.remove(0));
				} else {
					newunion = new OpUnion(alternatives.remove(0),
							union);
				}
				union = newunion;
			}
			return union;
		}

	
	}

}
