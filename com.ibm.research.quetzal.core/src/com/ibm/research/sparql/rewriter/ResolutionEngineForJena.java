package com.ibm.research.sparql.rewriter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hp.hpl.jena.graph.Node;
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
import com.hp.hpl.jena.sparql.algebra.op.OpJoin;
import com.hp.hpl.jena.sparql.algebra.op.OpTriple;
import com.hp.hpl.jena.sparql.algebra.op.OpUnion;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.core.VarExprList;
import com.hp.hpl.jena.sparql.expr.ExprVar;
import com.hp.hpl.jena.sparql.graph.NodeTransformLib;
import com.ibm.research.sparql.rewriter.ResolutionEngine.ResolutionVisitor;
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

public class ResolutionEngineForJena {

	private List<RuleforJena> rules;

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
		
		System.out.println(OpAsQuery.asQuery(newQuery));
		return OpAsQuery.asQuery(newQuery);

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
				alternatives.add(opTriple);
				
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

						expr.add((Var) exp, new ExprVar(name));
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
				if (alternatives.size() == 1) {
					continue;
				}
				OpUnion union = createUnionOfOps(alternatives);	
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
				union = createUnionOfOps(new LinkedList<Op>(tripleToOp.values()));	// this is now the union of all transformed triples
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

		private OpUnion createUnionOfOps(List<Op> alternatives) {
			OpUnion union = null;
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
