/******************************************************************************
 * Copyright (c) 2009 - 2015 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *****************************************************************************/
/**
 * 
 */
package com.ibm.research.kodkod.util;



import static kodkod.ast.operator.FormulaOperator.AND;
import static kodkod.ast.operator.FormulaOperator.IFF;
import static kodkod.ast.operator.FormulaOperator.IMPLIES;
import static kodkod.ast.operator.FormulaOperator.OR;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import kodkod.ast.BinaryExpression;
import kodkod.ast.BinaryFormula;
import kodkod.ast.BinaryIntExpression;
import kodkod.ast.ComparisonFormula;
import kodkod.ast.Comprehension;
import kodkod.ast.ConstantExpression;
import kodkod.ast.ConstantFormula;
import kodkod.ast.Decl;
import kodkod.ast.Decls;
import kodkod.ast.ExprToIntCast;
import kodkod.ast.Expression;
import kodkod.ast.Formula;
import kodkod.ast.IfExpression;
import kodkod.ast.IfIntExpression;
import kodkod.ast.IntComparisonFormula;
import kodkod.ast.IntConstant;
import kodkod.ast.IntExpression;
import kodkod.ast.IntToExprCast;
import kodkod.ast.MultiplicityFormula;
import kodkod.ast.NaryExpression;
import kodkod.ast.NaryFormula;
import kodkod.ast.NaryIntExpression;
import kodkod.ast.Node;
import kodkod.ast.NotFormula;
import kodkod.ast.ProjectExpression;
import kodkod.ast.QuantifiedFormula;
import kodkod.ast.Relation;
import kodkod.ast.RelationPredicate;
import kodkod.ast.SumExpression;
import kodkod.ast.UnaryExpression;
import kodkod.ast.UnaryIntExpression;
import kodkod.ast.Variable;
import kodkod.ast.operator.ExprOperator;
import kodkod.ast.operator.FormulaOperator;
import kodkod.ast.operator.IntCastOperator;
import kodkod.ast.operator.IntOperator;
import kodkod.ast.operator.Multiplicity;
import kodkod.ast.visitor.VoidVisitor;

/**
 * Pretty-prints Kodkod nodes.
 * 
 * @author Emina Torlak
 */
public final class PrettyPrinter {
	/**
	 * Returns a pretty-printed string representation of the 
	 * given nodes, with each line offset by at least the given
	 * number of whitespaces.  The line parameter determines the
	 * length of each pretty-printed line, including the offset.
	 * The display parameter determines how 
	 * the mapped nodes are displayed; that is, a descendant d of the
	 * given node is displayed as display.get(d.toString()) if 
	 * display.containsKey(d.toString()) is true.
	 * @requires 0 <= offset < line
	 * @return a pretty-printed string representation of the 
	 * given nodes
	 */
	@SuppressWarnings("unchecked")
	public static void print(Collection<Node> nodes, int offset, int line, Map<String, String> display) { 
		final Formatter formatter = new Formatter(offset,line,display);
		
		if (nodes.size()==1)
			nodes.iterator().next().accept(formatter);
		else {
			for(Node node : nodes) { 
				node.accept(formatter);
				formatter.newline();
				formatter.newline();
			}
		}
		if (!formatter.displayed.isEmpty()) {
			final Formatter dispFormatter = new Formatter(offset,line,Collections.EMPTY_MAP);
			dispFormatter.append("LET ");
			dispFormatter.indent++;
			final Iterator<Node> itr = formatter.displayed.iterator();
			Node n = itr.next();
			dispFormatter.append(display.get(n.toString()));
			dispFormatter.infix(":=");
			n.accept(dispFormatter);
			while(itr.hasNext()) { 
				n = itr.next();
				dispFormatter.append(", ");
				dispFormatter.newline();
				dispFormatter.append(display.get(n.toString()));
				dispFormatter.infix(":=");
				n.accept(dispFormatter);
			}
			dispFormatter.infix("|");
			dispFormatter.indent--;
			dispFormatter.append("\n\n");
			dispFormatter.tokens.print(formatter.tokens);
		} 
	}

	
	
	/**
	 * Generates a buffer of tokens comprising the string representation
	 * of a given node.  The buffer contains at least the parentheses 
	 * needed to correctly represent the node's tree structure.
	 * 
	 * @specfield tokens: seq<Object> 
	 * @author Emina Torlak
	 */
	private static class Formatter implements VoidVisitor {
		final PrintWriter tokens ;
		private final int lineLength;
		private int indent, lineStart;
		private boolean negated, top;
		private final Map<String, String> display;
		final Set<Node> displayed;
		
		/**
		 * Constructs a new tokenizer.
		 * @effects no this.tokens
		 */
		Formatter(int offset, int line, Map<String, String> display) {
			assert offset >= 0 && offset < line;
			this.tokens = new PrintWriter(System.out);
			this.lineLength = line;
			this.lineStart = 0;
			this.indent = offset;
			this.negated = false;
			this.top = true;
			this.display = display;
			this.displayed = new LinkedHashSet<Node>();
			indent();
		}
		
		/*--------------FORMATTERS---------------*/
		
			
		/** @effects this.tokens' = concat [ this.tokens, " ", token, " " ]*/
		private void infix(Object token) { 
			space();
			append(token);
			space();
		}
		
		/** @effects this.tokens' = concat [ this.tokens, token, " " ]*/
		private void keyword(Object token) { 
			append(token);
			space();
		}
		
		/** @effects this.tokens' = concat [ this.tokens, ", " ]*/
		private void comma() { 
			append(","); 
			space(); 
		}
		
		/** @effects this.tokens' = concat [ this.tokens, ": " ]*/
		private void colon() { 
			append(":"); 
			space(); 
		}
		
		/** @effects adds this.indent spaces to this.tokens */
		private void indent() { for(int i = 0; i < indent; i++) { space(); } }
		
		/** @effects adds newline plus this.indent spaces to this.tokens */
		private void newline() { 
			tokens.append("\n");
			lineStart = 0;
			indent();
		}
		
		/** @effects this.tokens' = concat[ this.tokens,  " " ] **/
		private void space() { append(" "); }
	
		/** @effects this.tokens' = concat [ this.tokens, token ]*/
		private void append(Object token) { 
			final String str = String.valueOf(token);
			if ((lineStart + str.length()) > lineLength) {
				newline();
			}
			lineStart += str.length();
			tokens.append(str);
		}
		
		/*--------------LEAVES---------------*/
		/** @effects this.tokens' = concat[ this.tokens, node ] */
		public void visit(Relation node) { append(node); }

		/** @effects this.tokens' = concat[ this.tokens, node ] */
		public void visit(Variable node) { append(node); }

		/** @effects this.tokens' = concat[ this.tokens, node ] */
		public void visit(ConstantExpression node) { append(node); }
		
		/** @effects this.tokens' = concat[ this.tokens, node ] */
		public void visit(IntConstant node) { append(node); }
		
		/** @effects this.tokens' = concat[ this.tokens, node ] */
		public void visit(ConstantFormula node) { append(node); }
		
		private boolean notTop() { 
			final boolean old = top;
			top = false;
			return old;
		}
		
		private boolean displayed(Node node) {
			if (display.isEmpty()) {
				return false;
			}
			final String key = node.toString();
			if (display.containsKey(key)) {
				append(display.get(key));
				displayed.add(node);
				return true;
			}
			return false;
		}
		
		/*--------------DECLARATIONS---------------*/
		/** 
		 * @effects this.tokens' = 
		 *   concat[ this.tokens, tokenize[ node.variable ], ":", tokenize[ node.expression ] 
		 **/
		public void visit(Decl node) {
			if (displayed(node)) return;
			final boolean oldTop = notTop();
			node.variable().accept(this);
			colon();
			if (node.multiplicity()!=Multiplicity.ONE) {
				append(node.multiplicity());
				space();
			}
			node.expression().accept(this);
			top = oldTop;
		}
		
		/** 
		 * @effects this.tokens' = 
		 *   concat[ this.tokens, tokenize[ node.declarations[0] ], ",", 
		 *    ..., tokenize[ node.declarations[ node.size() - 1 ] ] ] 
		 **/
		public void visit(Decls node) {
			if (displayed(node)) return;
			final boolean oldTop = notTop();
			Iterator<Decl> decls = node.iterator();
			decls.next().accept(this);
			while(decls.hasNext()) { 
				comma();
				decls.next().accept(this);
			}
			top = oldTop;
		}
		
		/*--------------UNARY NODES---------------*/
		
		/** @effects this.tokenize' = 
		 *   (parenthesize => concat [ this.tokens, "(", tokenize[child], ")" ] else 
		 *                    concat [ this.tokens, tokenize[child] ]*/
		private void visitChild(Node child, boolean parenthesize) { 
			if (parenthesize) { append("("); }
			child.accept(this);
			if (parenthesize) { append(")"); }
		}
		
		/** @return true if the given expression should be parenthesized when a 
		 * child of a compound parent */
		private boolean parenthesize(Expression child) { 
			return (display.isEmpty() || !display.containsKey(child.toString())) &&
			       (child instanceof BinaryExpression || child instanceof NaryExpression || child instanceof IfExpression); 
		}
		
		/** @return true if the given expression should be parenthesized when a 
		 * child of a compound parent */
		private boolean parenthesize(IntExpression child) { 
			return !(child instanceof UnaryIntExpression || 
					 child instanceof IntConstant || 
					 child instanceof ExprToIntCast); 
		}
		
		/** @return true if the given formula should be parenthesized when a 
		 * child of a compound parent */
		private boolean parenthesize(Formula child) { 
			return !(child instanceof NotFormula || child instanceof ConstantFormula || 
					 child instanceof RelationPredicate);
		}
		
		/** @effects appends the given op and child to this.tokens; the child is 
		 * parenthesized if it's an instance of binary expression or an if expression. **/
		public void visit(UnaryExpression node) { 
			if (displayed(node)) return;
			final boolean oldTop = notTop();
			append(node.op());
			visitChild(node.expression(), parenthesize(node.expression()));
			top = oldTop;
		}
		
		
		/** @effects appends the given op and child to this.tokens; the child is 
		 * parenthesized if it's not an instance of unary int expression or int constant. **/
		public void visit(UnaryIntExpression node)  { 
			if (displayed(node)) return;
			final boolean oldTop = notTop();
			final IntExpression child = node.intExpr();
			final IntOperator op = node.op();
			final boolean parens = 
				(op==IntOperator.ABS) || (op==IntOperator.SGN) || 
				parenthesize(child);
			append(node.op());
			visitChild(child, parens);
			top = oldTop;
		}
		
		/** @effects appends the given op and child to this.tokens; the child is 
		 * parenthesized if it's not an instance of not formula, constant formula, or 
		 * relation predicate. **/
		public void visit(NotFormula node) {
			if (displayed(node)) return;
			negated = !negated;
			append("!");
			final boolean pchild = parenthesize(node.formula());
			indent += pchild ? 2 : 1;
			visitChild(node.formula(), parenthesize(node.formula()));
			indent -= pchild ? 2 : 1;
			negated = !negated;
		}
		
		/** @effects appends the given op and child to this.tokens; the child is 
		 * parenthesized if it's an instance of binary expression or an if expression. **/
		public void visit(MultiplicityFormula node) {
			if (displayed(node)) return;
			final boolean oldTop = notTop();
			keyword(node.multiplicity());
			visitChild(node.expression(), parenthesize(node.expression()));
			top = oldTop;
		}
		
		/*--------------BINARY NODES---------------*/
		
		/** @return true if the given  expression needs to be parenthesized if a 
		 * child of a binary  expression with the given operator */
		private boolean parenthesize(ExprOperator op, Expression child) { 
			return display.containsKey(child.toString()) ? false :
				   child instanceof IfExpression ||
				   (child instanceof NaryExpression && ((NaryExpression)child).op()!=op) ||
			       (child instanceof BinaryExpression && 
			        (/*op!=ExprOperator.JOIN && */
			         ((BinaryExpression)child).op()!=op));
		}
		
		/** @effects appends the tokenization of the given node to this.tokens */
		public void visit(BinaryExpression node) {
			if (displayed(node)) return;
			final boolean oldTop = notTop();
			final ExprOperator op = node.op();
			final Expression left = node.left(), right = node.right();
			if (op==ExprOperator.JOIN && left.arity()==1 && right.arity()==2 && right instanceof Relation) { 
				append(right);
				append("[");
				visitChild(left, false);
				append("]");
			} else {
				visitChild(left, parenthesize(op, left));
				infix(op);
				visitChild(right, parenthesize(op, right));
			}
			top = oldTop;
		}
		
		

		/** @return true if the given operator is assocative */
		private boolean associative(IntOperator op) { 
			switch(op) { 
			case DIVIDE : case MODULO : case SHA : case SHL : case SHR : return false;
			default : return true;
			}
		}
		
		/** @return true if the given int expression needs to be parenthesized if a 
		 * child of a binary int expression with the given operator */
		private boolean parenthesize(IntOperator op, IntExpression child) { 
			return child instanceof SumExpression ||
				   child instanceof IfIntExpression || 
				   child instanceof NaryIntExpression ||
			       (child instanceof BinaryIntExpression && 
			        (!associative(op) || ((BinaryIntExpression)child).op()!=op));
		}
		
		/** @effects appends the tokenization of the given node to this.tokens */
		public void visit(BinaryIntExpression node) {
			if (displayed(node)) return;
			final boolean oldTop = notTop();
			final IntOperator op = node.op();
			visitChild(node.left(), parenthesize(op, node.left()));
			infix(op);
			visitChild(node.right(), parenthesize(op, node.right()));
			top = oldTop;
		}
		
		/** @return true if the given formula needs to be parenthesized if a 
		 * child of a binary formula with the given operator */
		private boolean parenthesize(FormulaOperator op, Formula child) { 
			return child instanceof QuantifiedFormula || 
			       (child instanceof BinaryFormula && 
			        (op==FormulaOperator.IMPLIES || 
			       ((BinaryFormula)child).op()!=op)) || 
			        ((child instanceof NaryFormula) && ((NaryFormula)child).op()!=op);
		}
	
		/** @effects appends the tokenization of the given node to this.tokens */
		public void visit(BinaryFormula node) {
			if (displayed(node)) return;
			final boolean oldTop = top;
			final FormulaOperator op = node.op();
			if (op==IFF || (negated && op==AND) || (!negated && (op==OR || op==IMPLIES))) { // not top in these cases
				top = false;
			}
			final boolean pleft = parenthesize(op, node.left());
			final boolean flip = (negated && op==IMPLIES);
			
			if (pleft) indent++;
			negated = negated ^ flip;
			visitChild(node.left(), pleft);
			if (pleft) indent--;
			infix(op);
			if (top) newline();
			final boolean pright =  parenthesize(op, node.right());
			if (pright) indent++;
			negated = negated ^ flip;
			visitChild(node.right(), pright);
			if (pright) indent--;
			top = oldTop;
		}
		
		/** @effects this.tokens' = concat[ this.tokens, tokenize[node.left], node.op, tokenize[node.right] */
		public void visit(ComparisonFormula node) {
			if (displayed(node)) return;
			final boolean oldTop = notTop();
			visitChild(node.left(), parenthesize(node.left()));
			infix(node.op());
			visitChild(node.right(), parenthesize(node.right()));
			top = oldTop;
		}
		
		/** @effects this.tokens' = concat[ this.tokens, tokenize[node.left], node.op, tokenize[node.right] */
		public void visit(IntComparisonFormula node) {
			if (displayed(node)) return;
			final boolean oldTop = notTop();
			visitChild(node.left(), parenthesize(node.left()));
			infix(node.op());
			visitChild(node.right(), parenthesize(node.right()));
			top = oldTop;
		}
		
		/*--------------TERNARY NODES---------------*/
		/** @return true if e is (a product of) Expression.NONE*/
		private boolean isEmpty(Expression e) { 
			if (e==Expression.NONE) return true;
			else if (e instanceof BinaryExpression) { 
				final BinaryExpression b = (BinaryExpression) e;
				return b.op()==ExprOperator.PRODUCT && isEmpty(b.left()) && isEmpty(b.right());
			} else if (e instanceof NaryExpression) { 
				final NaryExpression n = (NaryExpression) e;
				if (n.op()==ExprOperator.PRODUCT) { 
					for(int i = 0, max = n.size(); i < max; i++) { 
						if (!isEmpty(n.child(i))) return false;
					}
					return true;
				}
			}
			return false;
		}
		
		/** @effects appends the tokenization of the given node to this.tokens */
		public void visit(IfExpression node) {
			if (displayed(node)) return;
			final boolean oldTop = notTop();
			if (isEmpty(node.elseExpr())) {
				append("guard(");
				visitChild(node.condition(), false);
				append(",");
				space();
				visitChild(node.thenExpr(), false);
				append(")");
			} else {
				append("if");
				space();
				visitChild(node.condition(), parenthesize(node.condition()));
				infix("then");
	//			indent++;
	//			newline();
				visitChild(node.thenExpr(), parenthesize(node.thenExpr()));
				infix("else");
	//			newline();
				visitChild(node.elseExpr(), parenthesize(node.elseExpr()));
	//			indent--;
			}
			top = oldTop;
		}
		
		/** @effects appends the tokenization of the given node to this.tokens */
		public void visit(IfIntExpression node) {
			if (displayed(node)) return;
			final boolean oldTop = notTop();
			append("if");
			space();
			visitChild(node.condition(), parenthesize(node.condition()));
			infix("then");
//			indent++;
//			newline();
			visitChild(node.thenExpr(), parenthesize(node.thenExpr()));
			infix("else");
//			newline();
			visitChild(node.elseExpr(), parenthesize(node.elseExpr()));
//			indent--;
			top = oldTop;
		}
		
		/*--------------VARIABLE CREATOR NODES---------------*/
		/** @effects this.tokens' = concat[ this.tokens, 
		 *   "{", tokenize[node.decls], "|", tokenize[ node.formula ], "}" ]*/
		public void visit(Comprehension node) {
			if (displayed(node)) return;
			final boolean oldTop = notTop();
			append("{");
			node.decls().accept(this);
			infix("|");
			node.formula().accept(this);
			append("}");	
			top = oldTop;
		}
		
		/** @effects this.tokens' = concat[ this.tokens,  "sum",
		 *   tokenize[node.decls], "|", tokenize[ node.intExpr ],  ]*/
		public void visit(SumExpression node) {
			if (displayed(node)) return;
			final boolean oldTop = notTop();
			keyword("sum");
			node.decls().accept(this);
			infix("|");
			node.intExpr().accept(this);
			top = oldTop;
		}
		
		/** @effects this.tokens' = concat[ this.tokens,  node.quantifier,
		 *   tokenize[node.decls], "|", tokenize[ node.formula ] ]*/
		public void visit(QuantifiedFormula node) {
			if (displayed(node)) return;
			keyword(node.quantifier());
			node.decls().accept(this);
			infix("|");
			indent++;
			if (top) newline();
			node.formula().accept(this);
			indent--;
		}
		
		/*--------------NARY NODES---------------*/
		
		/** @effects appends the tokenization of the given node to this.tokens */
		public void visit(NaryExpression node) {
			if (displayed(node)) return;
			final boolean oldTop = notTop();
			final ExprOperator op = node.op();
			visitChild(node.child(0), parenthesize(op, node.child(0)));
			for(int i = 1, size = node.size(); i < size; i++) {
				infix(op);
				visitChild(node.child(i), parenthesize(op, node.child(i)));
			}
			top = oldTop;
		}
		/** @effects appends the tokenization of the given node to this.tokens */
		public void visit(NaryIntExpression node) {
			if (displayed(node)) return;
			final boolean oldTop = notTop();
			final IntOperator op = node.op();
			visitChild(node.child(0), parenthesize(op, node.child(0)));
			for(int i = 1, size = node.size(); i < size; i++) {
				infix(op);
				visitChild(node.child(i), parenthesize(op, node.child(i)));
			}
			top = oldTop;
		}
		/** @effects appends the tokenization of the given node to this.tokens */
		public void visit(NaryFormula node) {
			if (displayed(node)) return;
			final boolean oldTop = top;
			final FormulaOperator op = node.op();
			if ((negated && op==AND) || (!negated && op==OR)) { // not top in these cases
				top = false;
			}
			boolean parens = parenthesize(op, node.child(0));
			negated = negated ^ (op==OR);
			if (parens) indent++;
			visitChild(node.child(0), parens);
			if (parens) indent--;
			for(int i = 1, size = node.size(); i < size; i++) { 
				infix(op);
				if (top) newline();
				parens = parenthesize(op, node.child(i));
				if (parens) indent++;
				visitChild(node.child(i), parens);
				if (parens) indent--;
			}
			negated = negated ^ (op==OR);
			top = oldTop;
		}
		/*--------------OTHER NODES---------------*/
		
		/** @effects appends the tokenization of the given node to this.tokens */
		public void visit(ProjectExpression node) {
			if (displayed(node)) return;
			final boolean oldTop = notTop();
			append("project");
			append("[");
			node.expression().accept(this);
			comma();
			append("<");
			final Iterator<IntExpression> cols = node.columns();
			cols.next().accept(this);
			while(cols.hasNext()) { 
				comma();
				cols.next().accept(this);
			}
			append(">");
			append("]");
			top = oldTop;
		}
		
		/** @effects this.tokens' = concat[ this.tokens, "Int","[",
		 *   tokenize[node.intExpr], "]" ] **/
		public void visit(IntToExprCast node) {
			if (displayed(node)) return;
			final boolean oldTop = notTop();
			append(node.op()==IntCastOperator.INTCAST ? "Int" : "Bits");
			append("[");
			node.intExpr().accept(this);
			append("]");
			top = oldTop;
		}
		
		/** @effects this.tokens' = concat[ this.tokens, "int","[",
		 *   tokenize[node.expression], "]" ] **/
		public void visit(ExprToIntCast node) {
			if (displayed(node)) return;
			final boolean oldTop = notTop();
			switch(node.op()) { 
			case SUM:
				append("int");
				append("[");
				node.expression().accept(this);
				append("]");
				break;
			case CARDINALITY : 
				append("#");
				append("(");
				node.expression().accept(this);
				append(")");
				break;
			default : throw new IllegalArgumentException("unknown operator: " + node.op());
			}
			top = oldTop;
		}

		/** @effects appends the tokenization of the given node to this.tokens */
		public void visit(RelationPredicate node) {
			if (displayed(node)) return;
			final boolean oldTop = notTop();
			switch(node.name()) { 
			case ACYCLIC : 
				append("acyclic");
				append("[");
				node.relation().accept(this);
				append("]");
				break;
			case FUNCTION : 
				RelationPredicate.Function func = (RelationPredicate.Function)node;
				append("function");
				append("[");
				func.relation().accept(this);
				colon();
				func.domain().accept(this);
				infix("->");
				keyword(func.targetMult());
				func.range().accept(this);
				append("]");
				break;
			case TOTAL_ORDERING : 
				RelationPredicate.TotalOrdering ord = (RelationPredicate.TotalOrdering)node;
				append("ord");
				append("[");
				ord.relation().accept(this);
				comma();
				ord.ordered().accept(this);
				comma();
				ord.first().accept(this);
				comma();
				ord.last().accept(this);
				append("]");
				break;
			default:
				throw new AssertionError("unreachable");
			}
			top = oldTop;
		}
		
	}
	

}
