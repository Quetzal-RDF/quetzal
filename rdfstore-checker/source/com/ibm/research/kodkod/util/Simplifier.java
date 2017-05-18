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
package com.ibm.research.kodkod.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import kodkod.ast.BinaryExpression;
import kodkod.ast.BinaryFormula;
import kodkod.ast.BinaryIntExpression;
import kodkod.ast.ComparisonFormula;
import kodkod.ast.ConstantFormula;
import kodkod.ast.Expression;
import kodkod.ast.Formula;
import kodkod.ast.IfExpression;
import kodkod.ast.IfIntExpression;
import kodkod.ast.IntComparisonFormula;
import kodkod.ast.IntConstant;
import kodkod.ast.IntExpression;
import kodkod.ast.NaryExpression;
import kodkod.ast.NaryFormula;
import kodkod.ast.NaryIntExpression;
import kodkod.ast.Node;
import kodkod.ast.NotFormula;
import kodkod.ast.UnaryExpression;
import kodkod.ast.UnaryIntExpression;
import kodkod.ast.operator.ExprCompOperator;
import kodkod.ast.operator.ExprOperator;
import kodkod.ast.operator.FormulaOperator;
import kodkod.ast.operator.IntCompOperator;
import kodkod.ast.operator.IntOperator;
import kodkod.ast.visitor.AbstractReplacer;
import kodkod.util.ints.SparseSequence;
import kodkod.util.ints.TreeSequence;

import com.ibm.wala.util.debug.Assertions;

/**
 * Provides helper methods for performing common simplifications on Kodkod nodes.
 * @author etorlak
 */
abstract class Simplifier extends AbstractReplacer {
	final SparseSequence<Expression> empties;
	final SparseSequence<IntConstant> constants;
	/**
	 * Constructs a new simplifier that will use the given collection 
	 * of empty expressions and integer constants.  The empties sequence must map each of its indices
	 * to an empty expression of the same arity as the index.  The constants sequence must map each of
	 * its indices to a Kodkod constant with the same value as the index.
	 */
	@SuppressWarnings("unchecked")
	Simplifier(SparseSequence<Expression> empties, SparseSequence<IntConstant> constants) {
		super(Collections.EMPTY_SET);
		this.empties = empties;
		this.constants = constants;
	}
	
	/**
	 * Constructs a new simplifier with the empties sequence initializes with 
	 * Expression.NONE and Nodes.NONE2.
	 */
	Simplifier() {	
		this(new TreeSequence<Expression>(), new TreeSequence<IntConstant>()); 
		empties.put(1, Expression.NONE);
		empties.put(2, Nodes.NONE2);
	}
	
	/**
	 * Caches the given replacement for the specified node, if this is 
	 * a caching visitor.  Otherwise does nothing.  The method returns
	 * the replacement node.  
	 * @effects this.cache' = this.cache ++ node->replacement,
	 * @return replacement
	 */
	protected <N extends Node> N cache(N node, N replacement) {
		assert replacement != null;
		cache.put(node, replacement);
		return replacement;
	}
	
	/** @return true if expr is equal to a canonical empty expression */
	final boolean isEmpty(Expression expr) { return empties.get(expr.arity())==expr; }
	
	/** @return canonical empty expression of the given arity */
	final Expression empty(int arity) { 
		Expression empty = empties.get(arity);
		if (empty==null) { 
			empty = Nodes.empty(arity);
			empties.put(arity, empty);
		}
		return empty;
	}

	/** @return canonical constant expression for the given int expression, if it is a constant, or null otherwise */
	final IntConstant constant(IntExpression expr) { 
		if (expr.getClass()==IntConstant.class) { 
			final IntConstant constant = (IntConstant) expr;
			final int val = constant.value();
			final IntConstant ret = constants.get(val);
			if (ret!=null) return ret;
			constants.put(val, constant);
			return constant;
		}
		return null;
	}
	
	/** @return canonical integer constant with the given value. */
	final IntConstant constant(int val) { 
		IntConstant ret = constants.get(val);
		if (ret==null) {
			ret = IntConstant.constant(val);
			constants.put(val, ret);
		}
		return ret;
	}
	
	/** @return visits all nodes returned by the given iterator and returns the result in a list. */
	@SuppressWarnings("unchecked")
	final <N extends Node> List<N> visitAll(Iterable<N> iterable) {
		final List<N> result = new ArrayList<N>();
		for(Iterator<N> itr = iterable.iterator(); itr.hasNext(); ) { 
			result.add((N)itr.next().accept(this)); 
		}
		return result;
	}
	
	/** @return true if the two iterators return identical elements; otherwise returns false */
	final boolean allSame(Iterable<?> first, Iterable<?> second) {
		final Iterator<?> itr1 = first.iterator(), itr2 = second.iterator();
		while(itr1.hasNext() && itr2.hasNext()) 
			if (itr1.next()!=itr2.next()) return false;
		return !itr1.hasNext() && !itr2.hasNext();
	}
	
	/** @return a simplification of left op right, if possible, or null otherwise. */
	final Expression simplify(ExprOperator op, Expression left, Expression right) { 
		switch(op) { 
		case UNION : case OVERRIDE : 
			if (left==right) 			{ return left; }
			else if (isEmpty(left))		{ return right; }
			else if (isEmpty(right))	{ return left; }
			break;
		case JOIN :
			if (isEmpty(left) || isEmpty(right)) 	{ return empty(left.arity()+right.arity()-2); }
			break;
		case PRODUCT : 
			if (isEmpty(left) || isEmpty(right)) 	{ return empty(left.arity()+right.arity()); }
			break;
		case DIFFERENCE :
			if (left==right)						{ return empty(left.arity()); }
			else if (isEmpty(left)||isEmpty(right)) { return left; }
			break;
		case INTERSECTION :
			if (left==right)			{ return left; }
			else if (isEmpty(left))		{ return left; }
			else if (isEmpty(right))	{ return right; }
			break;
		default :
			Assertions.UNREACHABLE();
		}
		return null;
	}
	
	/**
	 * @requires op.nary() 
	 * @return simplification of the given operand list 
	 **/
	final List<Expression> simplify(ExprOperator op, List<Expression> children) { 
		switch(op) { 
		case UNION : case OVERRIDE : 
			for(Iterator<Expression> itr = children.iterator(); itr.hasNext(); ) { 
				final Expression child = itr.next();
				if (isEmpty(child)) itr.remove();
			}
			break;
		case PRODUCT :
			for(Expression child : children) { 
				if (isEmpty(child))
					return Collections.singletonList(empty(children.size()));
			}
			break;
		case INTERSECTION :
				for(Expression child : children) { 
					if (isEmpty(child)) 
						return Collections.singletonList(child);
				}
				break;
		default : 
				Assertions.UNREACHABLE();
		}
		return children;
	}
	
	boolean isTrue(Formula formula) { return formula==Formula.TRUE; }
	boolean isFalse(Formula formula) { return formula==Formula.FALSE; }
	
	/** @return a simplification of !child, if possible, or null otherwise. */
	final Formula simplify(Formula child) { 
		// can only simplify in the case of not with constants for the Propagator to work correctly
		if (child==Formula.TRUE) 
			return Formula.FALSE;
		else if (child==Formula.FALSE) 
			return Formula.TRUE;
		else if (child.getClass()==NotFormula.class) 
			return ((NotFormula)child).formula();
		else return null;
	}
	
	/** @return true if left is a NotFormula with right as its child or vice versa */
	final boolean areInverses(Formula left, Formula right) { 
		return 	((left.getClass() == NotFormula.class) && ((NotFormula)left).formula()==right) ||
				((right.getClass() == NotFormula.class) && ((NotFormula)right).formula()==left);
	}
	
	/** @return a simplification of left op right, if possible, or null otherwise. */
	final Formula simplify(FormulaOperator op, Formula left, Formula right) { 
		switch(op) { 
		case AND : 
			if (left==right)					{ return left; }
			else if (isTrue(left))				{ return right; }
			else if (isTrue(right))				{ return left; } 	
			else if (isFalse(left) || 
					 isFalse(right) || 
					 areInverses(left, right)) 	{ return Formula.FALSE; }
			break;
		case OR : 
			if (left==right)					{ return left; }
			else if (isFalse(left))				{ return right; }
			else if (isFalse(right))			{ return left; } 
			else if (isTrue(left) || 
					 isTrue(right) || 
					 areInverses(left, right)) 	{ return Formula.TRUE; }
			break;
		case IMPLIES : // !left or right
			if (left==right)					{ return Formula.TRUE; }
			else if (isTrue(left))				{ return right; }
			else if (isFalse(right)) 			{ return left; }
			else if (isFalse(left) || 
					 isTrue(right))				{ return Formula.TRUE; }
			break;
		case IFF : // (left and right) or (!left and !right)
			if (left==right)					{ return Formula.TRUE; }
			else if (isTrue(left))				{ return right; }
			else if (isFalse(left))				{ return right.not().accept(this); } 
			else if (isTrue(right))				{ return left; }
			else if (isFalse(right))			{ return left.not().accept(this); }
			else if (areInverses(left, right))	{ return Formula.FALSE; }
			break;
		default :
			Assertions.UNREACHABLE();
		}
		return null;
	}
	
	/**
	 * @requires op.nary
	 * @return simplification of the given operand set 
	 **/
	final List<Formula> simplify(FormulaOperator op, List<Formula> children) { 
		final boolean sc = op!=FormulaOperator.AND;
		final Formula scFormula = ConstantFormula.constant(sc);
		final Set<Formula> pos = new LinkedHashSet<Formula>(children);
		final Set<Formula> negs = new LinkedHashSet<Formula>();
		children.retainAll(pos);
		if (sc) { 
			for(Iterator<Formula> itr = children.iterator(); itr.hasNext();) {
				final Formula child = itr.next();
				if (isTrue(child)) return Collections.singletonList(scFormula);
				else if (isFalse(child)) itr.remove();
			}
		} else {
			for(Iterator<Formula> itr = children.iterator(); itr.hasNext();) {
				final Formula child = itr.next();
				if (isFalse(child)) return Collections.singletonList(scFormula);
				else if (isTrue(child)) itr.remove();
			}
		}
		for(Iterator<Formula> itr = children.iterator(); itr.hasNext();) {
			final Formula child = itr.next();
			if (negs.contains(child)) 	{ 
				return Collections.singletonList(scFormula);
			} else if (child.getClass() == NotFormula.class) { 
				final NotFormula notChild = (NotFormula)child;
				if (pos.contains(notChild.formula())) 
					return Collections.singletonList(scFormula);
				negs.add(notChild.formula()); 
			}
		}
		return children;
	}
	
	/** @return a simplification of op child, if possible, or null otherwise. */
	final IntExpression simplify(IntOperator op, IntExpression child) { 
		if (child==constant(0)) {
			switch(op) {
			case ABS : case SGN : case NEG : return child;
			case NOT : return constant(-1);
			default : Assertions.UNREACHABLE();
			}
		}
		return null;
	}
	
	/** @return a simplification of left op right, if possible, or null otherwise. */
	final IntExpression simplify(IntOperator op, IntExpression left, IntExpression right) { 
		final boolean lzero = left==constant(0), rzero = right==constant(0);
		
		switch(op) { 
		case PLUS : case OR : case XOR : 
			if (lzero)		{ return right; }
			else if (rzero)	{ return left; }
			break;
		case MULTIPLY : case AND : 
			if (lzero)		{ return left; }
			else if (rzero) { return right; }
			break;
		case MINUS : 
			if (lzero)		{ return right.negate().accept(this); }
			else if (rzero) { return left; }
			break;
		case SHA : case SHL : case SHR : 
			if (lzero || rzero)	{ return left; }
			break;
		case DIVIDE : case MODULO : 
			if (lzero) 		{ return left; }
			break;
		default :
			Assertions.UNREACHABLE();
		}
		
		return null;
	}
	
	/**
	 * @requires op.nary
	 * @return simplification of the given operand list
	 **/
	final List<IntExpression> simplify(IntOperator op, List<IntExpression> children) { 
		final IntExpression zero = constant(0);
		switch(op) { 
		case PLUS : case OR : 
			for(Iterator<IntExpression> itr = children.iterator(); itr.hasNext(); ) { 
				if (itr.next()==zero)
					itr.remove();
			}
			break;
		case MULTIPLY : case AND :
			for(IntExpression child : children) { 
				if (child==zero)
					return Collections.singletonList(zero);
			}
			break;
		default : 
			Assertions.UNREACHABLE();
		}
		return children;
	}
	
	/**
	 * @return simplification of the given ITE application or null if none is possible
	 */
	final <T> T simplify(Formula cond, T thenExpr, T elseExpr) { 
		if (isTrue(cond))		 		return thenExpr;
		else if (isFalse(cond))		 	return elseExpr;
		else if (thenExpr==elseExpr) 	return thenExpr;
		else 							return null;
	}
	
	public Expression visit(IfExpression expr) { 
		Expression ret = lookup(expr);
		if (ret!=null) return ret;
		
		final Formula cond = expr.condition().accept(this);
		final Expression thenExpr = expr.thenExpr().accept(this);
		final Expression elseExpr = expr.elseExpr().accept(this);
		
		ret = simplify(cond,thenExpr,elseExpr);
		
		if (ret==null) { 
			ret = cond==expr.condition()&&thenExpr==expr.thenExpr()&&elseExpr==expr.elseExpr() ? expr : cond.thenElse(thenExpr, elseExpr);
		}
		return cache(expr,ret);
	}
	
	public IntExpression visit(IfIntExpression expr) { 
		IntExpression ret = lookup(expr);
		if (ret!=null) return ret;
		
		final Formula cond = expr.condition().accept(this);
		final IntExpression thenExpr = expr.thenExpr().accept(this);
		final IntExpression elseExpr = expr.elseExpr().accept(this);
		
		ret = simplify(cond,thenExpr,elseExpr);
		
		if (ret==null) { 
			ret = cond==expr.condition()&&thenExpr==expr.thenExpr()&&elseExpr==expr.elseExpr() ? expr : cond.thenElse(thenExpr, elseExpr);
		}
		return cache(expr,ret);
	}
	
	public Formula visit(NotFormula not) { 
		Formula ret = lookup(not);
		if (ret!=null) return ret;
		
		final Formula child = not.formula().accept(this);
		
		ret = simplify(child);
		if (ret==null) {
			ret = child==not.formula() ? not : child.not();
		}
		return cache(not,ret);
	}
	
	public Formula visit(ComparisonFormula formula) { 
		Formula ret = lookup(formula);
		if (ret!=null) return ret;
		
		final ExprCompOperator op = formula.op();
		final Expression left = formula.left().accept(this);
		final Expression right = formula.right().accept(this);
		
		if (left==right) return cache(formula,Formula.TRUE);
		
		ret = left==formula.left()&&right==formula.right() ? formula : left.compare(op, right);
		return cache(formula,ret);
	}
	
	public Formula visit(IntComparisonFormula formula) { 
		Formula ret = lookup(formula);
		if (ret!=null) return ret;
		
		final IntCompOperator op = formula.op();
		final IntExpression left = formula.left().accept(this);
		final IntExpression right = formula.right().accept(this);
		
		if (left==right) return cache(formula,Formula.TRUE);
		
		ret = left==formula.left()&&right==formula.right() ? formula : left.compare(op, right);
		return cache(formula,ret);
	}
	
	public Formula visit(BinaryFormula expr) { 
		Formula ret = lookup(expr);
		if (ret!=null) return ret;
		final FormulaOperator op = expr.op();
		final Formula left = expr.left().accept(this);
		final Formula right = expr.right().accept(this);
		
		ret = simplify(op, left, right);
		
		if (ret==null) {
			ret = left==expr.left()&&right==expr.right() ? expr : left.compose(op, right);
		}
		
		return cache(expr,ret);
	}
	
	public Formula visit(NaryFormula formula) { 
		Formula ret = lookup(formula);
		if (ret!=null) return ret;
		
		final FormulaOperator op = formula.op();
		
		final List<Formula> children = simplify(op, visitAll(formula));
		final int size = children.size();
		if (size<2) { 
			return cache(formula, Formula.compose(op, children));
		} else {
			ret = formula.size()==size && allSame(formula,children) ? formula : Formula.compose(op, children);	
			return cache(formula,ret);
		}	
	}
	
	public Expression visit(BinaryExpression expr) { 
		Expression ret = lookup(expr);
		if (ret!=null) return ret;
		final ExprOperator op = expr.op();
		final Expression left = expr.left().accept(this);
		final Expression right = expr.right().accept(this);
		
		ret = simplify(op, left, right);
		
		if (ret==null) {
			ret = left==expr.left()&&right==expr.right() ? expr : left.compose(op, right);
		}
		
		return cache(expr,ret);
	}
	

	
	public Expression visit(NaryExpression expr) { 
		Expression ret = lookup(expr);
		if (ret!=null) return ret;
		
		final ExprOperator op = expr.op();
		
		final List<Expression> children = simplify(op, visitAll(expr));
		final int size = children.size();
		switch(size) { 
		case 0 : return cache(expr, empty(expr.arity()));
		case 1 : return cache(expr, children.get(0));
		default :
			ret = expr.size()==size && allSame(expr,children) ? expr : Expression.compose(op, children);
			return cache(expr,ret);
		}	
	}

	public Expression visit(UnaryExpression expr) { 
		Expression ret = lookup(expr);
		if (ret!=null) return ret;
		
		final ExprOperator op = expr.op();
		final Expression child = expr.expression().accept(this);
		
		if (isEmpty(child)) return cache(expr, child);
		ret = child==expr.expression() ? expr : child.apply(op);
		return cache(expr,ret);
	}
	
	public IntExpression visit(UnaryIntExpression expr) { 
		IntExpression ret = lookup(expr);
		if (ret!=null) return ret;
		
		final IntOperator op = expr.op();
		final IntExpression child = expr.intExpr().accept(this);
		
		ret = simplify(op, child);
		if (ret==null) {
			ret = child==expr.intExpr() ? expr : child.apply(op);
		}
		return cache(expr,ret);
	}
			
	public IntExpression visit(BinaryIntExpression expr) { 
		IntExpression ret = lookup(expr);
		if (ret!=null) return ret;
		final IntOperator op = expr.op();
		final IntExpression left = expr.left().accept(this);
		final IntExpression right = expr.right().accept(this);
		
		ret = simplify(op, left, right);
		
		if (ret==null) {
			ret = left==expr.left()&&right==expr.right() ? expr : left.compose(op, right);
		}
		
		return cache(expr,ret);
	}
	
	public IntExpression visit(NaryIntExpression expr) { 
		IntExpression ret = lookup(expr);
		if (ret!=null) return ret;
		
		final IntOperator op = expr.op();
		
		final List<IntExpression> children = simplify(op, visitAll(expr));
		final int size = children.size();
		switch(size) { 
		case 0 : return cache(expr, constant(0));
		case 1 : return cache(expr, children.get(0));
		default :
			ret = expr.size()==size && allSame(expr,children) ? expr : IntExpression.compose(op, children);
			return cache(expr,ret);
		}	
	}
}