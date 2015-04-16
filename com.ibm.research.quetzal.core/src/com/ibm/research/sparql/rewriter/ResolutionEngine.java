package com.ibm.research.sparql.rewriter;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.openrdf.query.algebra.QueryModelNode;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.algebra.Union;
import org.openrdf.query.algebra.helpers.QueryModelVisitorBase;
import org.openrdf.query.parser.ParsedTupleQuery;


public class ResolutionEngine {

	private List<Rule> rules;

	public ResolutionEngine(List<Rule> rules) {
		this.rules = rules;
	}

	/***
	 * Takes a SELECT query and 
	 * @param query
	 */
	public void unfold(ParsedTupleQuery query) {
		
		ResolutionVisitor visitor = new ResolutionVisitor(rules);
		TupleExpr body = query.getTupleExpr();
		
		body.visit(visitor);
		while (visitor.producedChange) {
			visitor.producedChange = false;
			body.visit(visitor);
		}
		
	}

	public void unfoldSinglePass(QueryModelNode query) {

	}

	/***
	 * traverses the algebra tree looking for PatternStatements that can be
	 * resolved with the rules in the engine. Every pattern that can be resolved
	 * with one or more rules will be replaced by a union consisting of the
	 * pattern union the body of the rules that can be resolved with it, as
	 * defined in a resolution step in an SLD-tree derivation.
	 * 
	 * @author mariano
	 *
	 */
	public class ResolutionVisitor extends QueryModelVisitorBase<RuntimeException> {

		private Set<QueryModelNode> visited;
		
		private boolean producedChange = false;

		// Each rules is a head (key) and body (value)
		private List<Rule> rules = new LinkedList<Rule>();

		public ResolutionVisitor(List<Rule> rules) {
			this.rules = rules;
			this.visited = new HashSet<QueryModelNode>();
		}

		public void clearVisited() {
			visited.clear();
		}


		
		/****
		 * If the visited pattern unifies with rule, and the node has not been
		 * visited, it will replace the node (in the nodes parent) with a union
		 * consisting of the node unified with the body of all the rules that
		 * unified with the node.
		 */
		@Override
		public void meet(StatementPattern node) {

			if (visited.contains(node))
				return;

			visited.add(node);
			QueryModelNode parent = node.getParentNode();

			List<TupleExpr> nodeAlternatives = new LinkedList<TupleExpr>();
			nodeAlternatives.add(node);

			for (Rule rule : rules) {
				
				try {
					rule = rule.getFreshRule();
				} catch (Exception e) {
					throw new RuntimeException(e);
				} 
				
				Substitution s = StatementUnifier.getMGU(node, rule.consequent);
				if (s == null)
					continue;

				// The rule matches the node, replacing (executing a resolution
				// step)

				TupleExpr body = rule.antecedent;
				SubstitutionApplier app = new SubstitutionApplier(s);
				body.visit(app);
				nodeAlternatives.add(body);
			}

			if (nodeAlternatives.size() == 1) {
				// There was no resolution, no change
				return;
			}

			// constructing UNION operator, a binary tree, removing 2 at a time
			Union union = null;
			while (!nodeAlternatives.isEmpty()) {
				Union newunion = null;
				if (union == null) {
					newunion = new Union(nodeAlternatives.remove(0), nodeAlternatives.remove(0));

				} else {
					newunion = new Union(nodeAlternatives.remove(0), union);
				}
				union = newunion;
			}

			// replacing the node with the constructed union
			parent.replaceChildNode(node, union);
			
			
			producedChange = true;

		}

	}

}
