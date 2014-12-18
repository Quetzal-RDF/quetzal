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
 package com.ibm.research.proppaths;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.path.P_Alt;
import com.hp.hpl.jena.sparql.path.P_Distinct;
import com.hp.hpl.jena.sparql.path.P_FixedLength;
import com.hp.hpl.jena.sparql.path.P_Inverse;
import com.hp.hpl.jena.sparql.path.P_Link;
import com.hp.hpl.jena.sparql.path.P_Mod;
import com.hp.hpl.jena.sparql.path.P_Multi;
import com.hp.hpl.jena.sparql.path.P_NegPropSet;
import com.hp.hpl.jena.sparql.path.P_OneOrMore1;
import com.hp.hpl.jena.sparql.path.P_OneOrMoreN;
import com.hp.hpl.jena.sparql.path.P_ReverseLink;
import com.hp.hpl.jena.sparql.path.P_Seq;
import com.hp.hpl.jena.sparql.path.P_Shortest;
import com.hp.hpl.jena.sparql.path.P_ZeroOrMore1;
import com.hp.hpl.jena.sparql.path.P_ZeroOrMoreN;
import com.hp.hpl.jena.sparql.path.P_ZeroOrOne;
import com.hp.hpl.jena.sparql.path.PathVisitor;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;
import com.ibm.research.owlql.rule.AtomicFormula;
import com.ibm.research.owlql.rule.ConstantExpr;
import com.ibm.research.owlql.rule.Expr;
import com.ibm.research.owlql.rule.MagicSetPredicate;
import com.ibm.research.owlql.rule.Predicate;
import com.ibm.research.owlql.rule.Rule;
import com.ibm.research.owlql.rule.RuleSystem;
import com.ibm.research.owlql.rule.VariableExpr;
import com.ibm.research.rdf.store.sparql11.SparqlParserUtilities;
import com.ibm.research.utils.OCUtils;
import com.ibm.wala.util.collections.HashSetFactory;

/**
 * Convert a property path expression into an equivalent datalog rule set
 * path constructs when possible
 * @author fokoue
 *
 */
public class PropertyPathToRules {
	
	public static void main(String[] args) throws Exception{
		PropertyPathToRules toRules = new PropertyPathToRules();
		String q = "select * where { " +
				"?x (^<http://example.org/p>|<http://example.org/s>?)/(<http://example.org/q>| <http://example.org/r>)+/!(^<http://example.org/t>| ^<http://example.org/u>|<http://example.org/v> )?y ." +
				"?x <http://example.org/s>/<http://example.org/r>/^<http://example.org/t> ?y ." +
				"?x <http://example.org/s>/<http://example.org/r>+/^<http://example.org/t> ?y ." +
				"?x (<http://example.org/s>/<http://example.org/r>+/^<http://example.org/t>)+ ?y ." +
				"?x (<http://example.org/s>/(^(<http://example.org/r>+|<http://example.org/p>))+/(^<http://example.org/t>)+)+ ?y ." +
			"?x <http://example.org/s>/<http://example.org/r>/<http://example.org/t> ?y ." +
			"?x !(a|<http://example.org/s>|<http://example.org/r>| <http://example.org/t> ) ?y ." +
			"?x !(<http://example.org/r>|^<http://example.org/t>| ^a|<http://example.org/s> ) ?y ." +
				"}"; 
		// ( iRIref | 'a' )
		com.ibm.research.rdf.store.sparql11.model.Query db2rdfQuery =  SparqlParserUtilities.parseSparqlString(q);
		System.out.println("Query:\n"+db2rdfQuery.toString());
		
		//System.exit(0);
		
		Query query = QueryFactory.create(q, Syntax.syntaxSPARQL_11);
		System.out.println("Parsed Query:\n\t"+query);
		ElementGroup group = (ElementGroup)  query.getQueryPattern();
		ElementPathBlock p = (ElementPathBlock) group.getElements().get(0);
		for (TriplePath path : p.getPattern().getList()) {
			System.out.println("Path: "+path);
			RuleSystem rs = toRules.toRules(path, true, "Triple", "NegatedPropertySetTriple");
			System.out.println("Equivalent Rule System (Main formula: "+rs.getMainHeadFormula()+"):\n"+rs);
			rs = rs.simplify(Collections.singleton(rs.getMainHeadFormula().getPredicate()));
			System.out.println("Equivalent Rule System after simplification (Main formula: "+rs.getMainHeadFormula()+"):\n"
					+rs);
			/*Graph<Predicate> graph = DatalogEngine.buildDependencyGraph(rs);
			LinkedList<Set<Predicate>> sccs =DatalogEngine.topologicalSortOfSCC(graph);
			System.out.println("Predicate dependency graph:\n"+graph);
			System.out.println("Topological sort of predicates:");
			for (Set<Predicate> scc: sccs) {
				System.out.println("\t"+scc);
			}*/
			AtomicFormula goal = new AtomicFormula(rs.getMainHeadFormula().getPredicate(), new ConstantExpr(0), new VariableExpr("Y"));
			RuleSystem mgrs = rs.magicSetTransformation(goal, true, true, false);
			Set<Predicate> predicatesToKeep = HashSetFactory.make();
			predicatesToKeep.add(goal.getPredicate());
			for (Predicate pred: mgrs.getHeadPredicates()) {
				if (pred instanceof MagicSetPredicate) {
					//predicatesToKeep.add(pred);
					/*Set<Predicate> preds = HashSetFactory.make();
					preds.add(goal.getPredicate());
					preds.add(pred);
					RuleSystem rules = mgrs.simplify(preds);
					System.out.println("Rule System For Magic set predicate: "+pred+"\n"+rules);
					*/
				}
			}
			mgrs = mgrs.simplify(predicatesToKeep);
			System.out.println("Rule System after magic set transformation (goal: "+goal+"):\n"+mgrs);
			/*graph = DatalogEngine.buildDependencyGraph(mgrs);
			sccs =DatalogEngine.topologicalSortOfSCC(graph);
			System.out.println("Predicate dependency graph:\n"+graph);
			System.out.println("Topological sort of predicates:");
			for (Set<Predicate> scc: sccs) {
				System.out.println("\t"+scc);
			}*/
		}
	
		
 	}

	
	protected class PathToRules implements PathVisitor {
		
		private List<Rule> result;
		private Predicate resultMainPredicate;
		private int ruleid = 0;
		private boolean rightRecursion;
		private NewVariableGenerator idpPredicateGen;
		private Predicate triplePredicate;
		// negPropPred(subject, object, numberOfFowardProperties, prop1, ..., propN)
		private String negatedPropertySetPredName;
		
		
	
		
		public PathToRules(NewVariableGenerator idpPredicateGen,String triplePredicateName,String negatedPropertySetPredName,
				boolean rightRecursion) {
			super();
			this.idpPredicateGen = idpPredicateGen;
			this.rightRecursion = rightRecursion;
			this.triplePredicate = new Predicate(triplePredicateName, 3);
			this.negatedPropertySetPredName = negatedPropertySetPredName;
		}
		public RuleSystem getResult() {
			return new RuleSystem(result, new AtomicFormula(resultMainPredicate, new VariableExpr("X"), new VariableExpr("Y")), true);
		}
		
		@Override
		public void visit(P_Alt p) {
			p.getLeft().visit(this);
			List<Rule> left = result;
			Predicate leftPred =  resultMainPredicate;
			p.getRight().visit(this);
			List<Rule> right = result;
			Predicate rightPred = resultMainPredicate;
			List<Rule> rules = new LinkedList<Rule>();
			rules.addAll(left);
			rules.addAll(right);
			
			Predicate newpred = new Predicate(idpPredicateGen.createNewVariable(), 2);
			VariableExpr x = new VariableExpr("X");
			VariableExpr y = new VariableExpr("Y");
			// add : newpred(x, y) :- leftpred(x, y)
			rules.add( new Rule(new AtomicFormula(newpred, x, y),  ruleid++,
							new AtomicFormula(leftPred, x, y)));
			// add : newpred(x, y) :- leftpred(x, y)
			rules.add( new Rule(new AtomicFormula(newpred, x, y),  ruleid++,
					new AtomicFormula(rightPred, x, y)));
			result = rules;
			resultMainPredicate = newpred;
			
			
		}

		@Override
		public void visit(P_Distinct p) {
			throw new UnsupportedOperationException("Unsupported property path expression: "+p);
			
		}

		@Override
		public void visit(P_FixedLength p) {
			throw new UnsupportedOperationException("Unsupported property path expression: "+p);
			
		}

		@Override
		public void visit(P_Inverse p) {
			boolean prevRightRecursion = rightRecursion;
			rightRecursion = !rightRecursion;
			p.getSubPath().visit(this);
			rightRecursion = prevRightRecursion;
			List<Rule> rules = result;
			Predicate pred = resultMainPredicate;
			Predicate newpred = new Predicate(idpPredicateGen.createNewVariable(), 2);
			VariableExpr x = new VariableExpr("X");
			VariableExpr y = new VariableExpr("Y");
			// add : newpred(x, y) :- pred(y, z)
			rules.add( new Rule(new AtomicFormula(newpred, x, y),  ruleid++,
							new AtomicFormula(pred, y, x)));
			result = rules;
			resultMainPredicate = newpred;
			
		}

		@Override
		public void visit(P_Link p) {
			try {
				List<Rule> rules = new LinkedList<Rule>();
				Predicate newpred = new Predicate(idpPredicateGen.createNewVariable(), 2);
				if (p.getNode().isVariable() || p.getNode().isBlank()) {
					throw new UnsupportedOperationException("Unsupported property path expression: "+p);
				} 
				assert p.getNode().isURI() : p.getNode();
				VariableExpr x = new VariableExpr("X");
				VariableExpr y = new VariableExpr("Y");
				ConstantExpr propURI = new ConstantExpr(new URI(p.getNode().getURI())); 
				rules.add(new Rule(new AtomicFormula(newpred, x, y), ruleid++, new AtomicFormula(triplePredicate, x, propURI, y)));
				result = rules;
				resultMainPredicate = newpred;
				
			} catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}
			
		}

		@Override
		public void visit(P_Mod p) {
			throw new UnsupportedOperationException("Unsupported property path expression: "+p);
			
		}

		@Override
		public void visit(P_Multi p) {
			throw new UnsupportedOperationException("Unsupported property path expression: "+p);
			
		}
		@Override
		public void visit(P_NegPropSet p) {
			try {
				List<Node> fwd = p.getFwdNodes();
				List<Node> bwd = p.getBwdNodes();
				int numberOfForwardProps = fwd.size();
				List<Rule> rules = new LinkedList<Rule>();
				Predicate newpred = new Predicate(idpPredicateGen.createNewVariable(), 2);
				VariableExpr x = new VariableExpr("X");
				VariableExpr y = new VariableExpr("Y");
				AtomicFormula head = new AtomicFormula(newpred, x, y);
				List<Expr> args = new LinkedList<Expr>();
				args.add(x);
				args.add(y);
				args.add(new ConstantExpr(numberOfForwardProps));
				for (Node n : fwd) {
					if (!n.isURI()) {
						throw new UnsupportedOperationException("Unsupported property path expression: "+p+"\n\t"+n);
					}
					args.add(new ConstantExpr(new URI(n.getURI())));
				}
				for (Node n : bwd) {
					if (!n.isURI()) {
						throw new UnsupportedOperationException("Unsupported property path expression: "+p+"\n\t"+n);
					}
					args.add(new ConstantExpr(new URI(n.getURI())));
				}
				AtomicFormula body = new AtomicFormula(new Predicate(negatedPropertySetPredName, 3+fwd.size()+bwd.size()), args);
				rules.add(new Rule(head,ruleid++,body));
				result = rules;
				resultMainPredicate = newpred;
				
			} catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}
			
			
		}

		@Override
		public void visit(P_OneOrMore1 p) {
			p.getSubPath().visit(this);
			List<Rule> rules = result;
			Predicate basePred = resultMainPredicate;
			Predicate newpred = new Predicate(idpPredicateGen.createNewVariable(), 2);
			VariableExpr x = new VariableExpr("X");
			VariableExpr y = new VariableExpr("Y");
			VariableExpr z = new VariableExpr("Z");
			rules.add(new Rule(new AtomicFormula(newpred, x, y), ruleid++, new AtomicFormula(basePred, x, y)));
			if (rightRecursion) {
				rules.add(new Rule(new  AtomicFormula(newpred, x, y), ruleid++, new AtomicFormula(basePred, x, z), new AtomicFormula(newpred, z, y) ));
			} else {
				rules.add(new Rule(new  AtomicFormula(newpred, x, y), ruleid++, new AtomicFormula(newpred, z, y), new AtomicFormula(basePred, x, z)));
			}
			result = rules;
			resultMainPredicate = newpred;
			
		}

		@Override
		public void visit(P_OneOrMoreN p) {
			p.getSubPath().visit(this);
			List<Rule> rules = result;
			Predicate basePred = resultMainPredicate;
			Predicate newpred = new Predicate(idpPredicateGen.createNewVariable(), 2);
			VariableExpr x = new VariableExpr("X");
			VariableExpr y = new VariableExpr("Y");
			VariableExpr z = new VariableExpr("Z");
			rules.add(new Rule(new AtomicFormula(newpred, x, y), ruleid++, new AtomicFormula(basePred, x, y)));
			if (rightRecursion) {
				rules.add(new Rule(new  AtomicFormula(newpred, x, y), ruleid++, new AtomicFormula(basePred, x, z), new AtomicFormula(newpred, z, y) ));
			} else {
				rules.add(new Rule(new  AtomicFormula(newpred, x, y), ruleid++, new AtomicFormula(newpred, z, y), new AtomicFormula(basePred, x, z)));
			}
			result = rules;
			resultMainPredicate = newpred;
			
		}

		@Override
		public void visit(P_ReverseLink p) {
			try {
				List<Rule> rules = new LinkedList<Rule>();
				Predicate newpred = new Predicate(idpPredicateGen.createNewVariable(), 2);
				if (p.getNode().isVariable() || p.getNode().isBlank()) {
					throw new UnsupportedOperationException("Unsupported property path expression: "+p);
				} 
				assert p.getNode().isURI() : p.getNode();
				VariableExpr x = new VariableExpr("X");
				VariableExpr y = new VariableExpr("Y");
				ConstantExpr propURI = new ConstantExpr(new URI(p.getNode().getURI())); 
				rules.add(new Rule(new AtomicFormula(newpred, x, y), ruleid++, new AtomicFormula(triplePredicate, y, propURI, x)));
				result = rules;
				resultMainPredicate = newpred;
				
			} catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}
			
		}

		@Override
		public void visit(P_Seq p) {
			p.getLeft().visit(this);
			List<Rule> left = result;
			Predicate leftPred = resultMainPredicate;
			
			p.getRight().visit(this);
			List<Rule> right = result;
			Predicate rightPred = resultMainPredicate;
			
			List<Rule> rules = new LinkedList<Rule>();
			rules.addAll(left);
			rules.addAll(right);
			
			Predicate newpred = new Predicate(idpPredicateGen.createNewVariable(), 2);
			VariableExpr x = new VariableExpr("X");
			VariableExpr y = new VariableExpr("Y");
			VariableExpr z = new VariableExpr("Z");
			rules.add(new Rule(new AtomicFormula(newpred, x, y), ruleid++, new AtomicFormula(leftPred, x, z), new AtomicFormula(rightPred, z, y)));
			
			result = rules;
			resultMainPredicate = newpred;
			
		}

		@Override
		public void visit(P_Shortest p) {
			throw new UnsupportedOperationException("Unsupported property path expression: "+p);
			
		}

		@Override
		public void visit(P_ZeroOrMore1 p) {
			new P_OneOrMore1(p.getSubPath()).visit(this);
			List<Rule> rules = result;
			Predicate oneOrMorePred = resultMainPredicate;
			Predicate newpred = new Predicate(idpPredicateGen.createNewVariable(), 2);
			VariableExpr x = new VariableExpr("X");
			VariableExpr y = new VariableExpr("Y");
			rules.add(new Rule(new AtomicFormula(newpred, x, y), ruleid++, new AtomicFormula(new Predicate(Rule.BUILT_IN_EQUAL, 2), x, y)));
			rules.add(new Rule(new AtomicFormula(newpred, x, y), ruleid++, new AtomicFormula(oneOrMorePred, x, y)));
			result = rules;
			resultMainPredicate = newpred;
			
		}

		@Override
		public void visit(P_ZeroOrMoreN p) {
			new P_OneOrMoreN(p.getSubPath()).visit(this);
			List<Rule> rules = result;
			Predicate oneOrMorePred = resultMainPredicate;
			Predicate newpred = new Predicate(idpPredicateGen.createNewVariable(), 2);
			VariableExpr x = new VariableExpr("X");
			VariableExpr y = new VariableExpr("Y");
			rules.add(new Rule(new AtomicFormula(newpred, x, y), ruleid++, new AtomicFormula(new Predicate(Rule.BUILT_IN_EQUAL, 2), x, y)));
			rules.add(new Rule(new AtomicFormula(newpred, x, y), ruleid++, new AtomicFormula(oneOrMorePred, x, y)));
			result = rules;
			resultMainPredicate = newpred;
			
			
		}

		@Override
		public void visit(P_ZeroOrOne p) {
			p.getSubPath().visit(this);
			List<Rule> rules = result;
			Predicate subpred = resultMainPredicate;
			Predicate newpred = new Predicate(idpPredicateGen.createNewVariable(), 2);
			VariableExpr x = new VariableExpr("X");
			VariableExpr y = new VariableExpr("Y");
			rules.add(new Rule(new AtomicFormula(newpred, x, y), ruleid++, new AtomicFormula(new Predicate(Rule.BUILT_IN_EQUAL, 2), x, y)));
			rules.add(new Rule(new AtomicFormula(newpred, x, y), ruleid++, new AtomicFormula(subpred, x, y)));
			result = rules;
			resultMainPredicate = newpred;
			
			
		}
		
	}
	
	public RuleSystem toRules(TriplePath tp,  String triplePredicateName, String negatedPropertySetPredName) {
		return toRules(tp, true, triplePredicateName, negatedPropertySetPredName);
	}

	public RuleSystem toRules(TriplePath tp, boolean rightRecursion, String triplePredicateName, String negatedPropertySetPredName) {
		String predPrefix = "P";
		Set<String> predNames = HashSetFactory.make();
		predNames.add(triplePredicateName);
		predNames.add(negatedPropertySetPredName);
		int startSuffix = OCUtils.nextAvailableSuffixVariable(predNames, predPrefix)+1;
		NewVariableGenerator predgen = new NewVariableGenerator(predPrefix, startSuffix);
		PathToRules visitor = new PathToRules(predgen, triplePredicateName, negatedPropertySetPredName, rightRecursion);;
		tp.getPath().visit(visitor);
		return visitor.getResult();
	}
	
	
}
