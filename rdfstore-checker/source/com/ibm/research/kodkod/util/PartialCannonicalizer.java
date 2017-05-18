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

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import kodkod.ast.BinaryExpression;
import kodkod.ast.BinaryFormula;
import kodkod.ast.BinaryIntExpression;
import kodkod.ast.ComparisonFormula;
import kodkod.ast.ExprToIntCast;
import kodkod.ast.Expression;
import kodkod.ast.Formula;
import kodkod.ast.IfExpression;
import kodkod.ast.IfIntExpression;
import kodkod.ast.IntComparisonFormula;
import kodkod.ast.IntExpression;
import kodkod.ast.IntToExprCast;
import kodkod.ast.NaryExpression;
import kodkod.ast.NaryFormula;
import kodkod.ast.NaryIntExpression;
import kodkod.ast.Node;
import kodkod.ast.NotFormula;
import kodkod.ast.UnaryExpression;
import kodkod.ast.UnaryIntExpression;
import kodkod.ast.operator.ExprCastOperator;
import kodkod.ast.operator.ExprCompOperator;
import kodkod.ast.operator.ExprOperator;
import kodkod.ast.operator.FormulaOperator;
import kodkod.ast.operator.IntCastOperator;
import kodkod.ast.operator.IntCompOperator;
import kodkod.ast.operator.IntOperator;
import kodkod.util.collections.CacheSet;
import kodkod.util.ints.Ints;


/**
 * Partially canonicalizes a translation.
 * @author Emina Torlak
 */
final class PartialCannonicalizer extends Simplifier {
	private final CacheSet<PartialCannonicalizer.Holder<Formula>> formulas;
	private final CacheSet<PartialCannonicalizer.Holder<Expression>> exprs;
	private final CacheSet<PartialCannonicalizer.Holder<IntExpression>> intExprs;
	
	PartialCannonicalizer() { 
		exprs = new CacheSet<PartialCannonicalizer.Holder<Expression>>();
		formulas = new CacheSet<PartialCannonicalizer.Holder<Formula>>();
		intExprs = new CacheSet<PartialCannonicalizer.Holder<IntExpression>>();
	}
	
	private static final class Holder<T> {
		final T obj; final int hash;
		Holder(T obj, int hash) { 
			this.obj = obj;
			this.hash = hash;
		}
		public int hashCode() { return hash; }
	}
	
	/**
	 * Caches the given replacement for the specified node, if this is 
	 * a caching visitor.  Otherwise does nothing.  The method returns
	 * the replacement node.  
	 * @effects this.cache' = this.cache ++ node->replacement,
	 * @return replacement
	 */
	protected <N extends Node> N cache(N node, N replacement) {
		cache.put(node, replacement);
		return replacement;
	}
	
	/**
	 * Returns the super-fast hash of the identity hashcodes of the given objects.
	 * @return super-fast hash of the identity hashcodes of the given objects.
	 */
	private static int hash(Object init, Object...rest) {
		int hash = Ints.superFastHashIncremental(System.identityHashCode(init), 17);
		for(Object obj : rest) {
			hash = Ints.superFastHashIncremental(System.identityHashCode(obj), hash);
		}
		return Ints.superFastHashAvalanche(hash);
	}
	
	public Expression visit(IfExpression expr) { 
		Expression ret = lookup(expr);
		if (ret!=null) return ret;
		
		final Formula cond = expr.condition().accept(this);
		final Expression thenExpr = expr.thenExpr().accept(this);
		final Expression elseExpr = expr.elseExpr().accept(this);
		
		ret = simplify(cond,thenExpr,elseExpr);
		
		if (ret==null) { 
			final int hash = hash(IfExpression.class, cond, thenExpr, elseExpr);
			for(Iterator<PartialCannonicalizer.Holder<Expression>> itr = exprs.get(hash); itr.hasNext(); ) {
				final Expression next = itr.next().obj;
				if (next.getClass()==IfExpression.class) { 
					final IfExpression hit = (IfExpression) next;
					if (hit.condition()==cond && hit.thenExpr()==thenExpr && hit.elseExpr()==elseExpr) { 
						return cache(expr, hit);
					}
				}
			}
			ret = cond==expr.condition()&&thenExpr==expr.thenExpr()&&elseExpr==expr.elseExpr() ? expr : cond.thenElse(thenExpr, elseExpr);
			exprs.add(new PartialCannonicalizer.Holder<Expression>(ret, hash));
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
			final int hash = hash(IfIntExpression.class, cond, thenExpr, elseExpr);
			for(Iterator<PartialCannonicalizer.Holder<IntExpression>> itr = intExprs.get(hash); itr.hasNext(); ) {
				final IntExpression next = itr.next().obj;
				if (next.getClass()==IfIntExpression.class) { 
					final IfIntExpression hit = (IfIntExpression) next;
					if (hit.condition()==cond && hit.thenExpr()==thenExpr && hit.elseExpr()==elseExpr) { 
						return cache(expr, hit);
					}
				}
			}
			ret = cond==expr.condition()&&thenExpr==expr.thenExpr()&&elseExpr==expr.elseExpr() ? expr : cond.thenElse(thenExpr, elseExpr);
			intExprs.add(new PartialCannonicalizer.Holder<IntExpression>(ret, hash));
		}
		return cache(expr,ret);
	}
	
	public Formula visit(NotFormula not) { 
		Formula ret = lookup(not);
		if (ret!=null) return ret;
		
		final Formula child = not.formula().accept(this);
		
		ret = simplify(child);
		if (ret==null) {
			final int hash = hash(NotFormula.class, child);
			for(Iterator<PartialCannonicalizer.Holder<Formula>> itr = formulas.get(hash); itr.hasNext(); ) {
				final Formula next = itr.next().obj;
				if (next.getClass()==NotFormula.class) { 
					if (((NotFormula)next).formula()==child)
						return cache(not, next);
				}
			}
			ret = child==not.formula() ? not : child.not();
			formulas.add(new PartialCannonicalizer.Holder<Formula>(ret, hash));
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
		
		final int hash = hash(op, left, right);
		for(Iterator<PartialCannonicalizer.Holder<Formula>> itr = formulas.get(hash); itr.hasNext(); ) {
			final Formula next = itr.next().obj;
			if (next instanceof ComparisonFormula) { 
				final ComparisonFormula hit = (ComparisonFormula) next;
				if (hit.op()==op && hit.left()==left && hit.right()==right) { 
					return cache(formula, hit);
				}
			}
		}

		ret = left==formula.left()&&right==formula.right() ? formula : left.compare(op, right);
		formulas.add(new PartialCannonicalizer.Holder<Formula>(ret, hash));
		return cache(formula,ret);
	}
	
	public Formula visit(IntComparisonFormula formula) { 
		Formula ret = lookup(formula);
		if (ret!=null) return ret;
		
		final IntCompOperator op = formula.op();
		final IntExpression left = formula.left().accept(this);
		final IntExpression right = formula.right().accept(this);
		
		if (left==right) return cache(formula,Formula.TRUE);
		
		final int hash = hash(op, left, right);
		for(Iterator<PartialCannonicalizer.Holder<Formula>> itr = formulas.get(hash); itr.hasNext(); ) {
			final Formula next = itr.next().obj;
			if (next instanceof IntComparisonFormula) { 
				final IntComparisonFormula hit = (IntComparisonFormula) next;
				if (hit.op()==op && hit.left()==left && hit.right()==right) { 
					return cache(formula, hit);
				}
			}
		}

		ret = left==formula.left()&&right==formula.right() ? formula : left.compare(op, right);
		formulas.add(new PartialCannonicalizer.Holder<Formula>(ret, hash));
		return cache(formula,ret);
	}
	
	public Formula visit(NaryFormula formula) { 
		Formula ret = lookup(formula);
		if (ret!=null) return ret;
		
		final FormulaOperator op = formula.op();
		
		final List<Formula> children = simplify(op, visitAll(op==FormulaOperator.AND ? kodkod.util.nodes.Nodes.roots(formula) : formula));
		final int size = children.size();
		if (size<2) { 
			return cache(formula, Formula.compose(op, children));
		} else {
			final int hash = hash(op, children.toArray());
			if (size==2) { 
				final Formula left = children.get(0), right = children.get(1);
				for(Iterator<PartialCannonicalizer.Holder<Formula>> itr = formulas.get(hash); itr.hasNext(); ) {
					final Formula next = itr.next().obj;
					if (next.getClass() == BinaryFormula.class) { 
						final BinaryFormula hit = (BinaryFormula) next;
						if (hit.op()==op && hit.left()==left && hit.right()==right)
							return cache(formula, hit);
					}
				}
			} else {
				for(Iterator<PartialCannonicalizer.Holder<Formula>> itr = formulas.get(hash); itr.hasNext(); ) {
					final Formula next = itr.next().obj;
					if (next.getClass() == NaryFormula.class) { 
						final NaryFormula hit = (NaryFormula) next;
						if (hit.op()==op && hit.size()==size && allSame(hit, children)) {
							return cache(formula, hit);
						}
					}
				}	
			}
			ret = formula.size()==size && allSame(formula,children) ? formula : Formula.compose(op, children);
			formulas.add(new PartialCannonicalizer.Holder<Formula>(ret, hash));
	
			return cache(formula,ret);
		}	
	}
	
	public Formula visit(BinaryFormula formula) { 
		Formula ret = lookup(formula);
		if (ret!=null) return ret;
		final FormulaOperator op = formula.op();
		if (op==FormulaOperator.AND) {
			final Set<Formula> conjuncts = kodkod.util.nodes.Nodes.roots(formula);
			if (conjuncts.size()>2) { 
				return cache(formula, Formula.and(conjuncts).accept(this));
			}
		}
		
		final Formula left = formula.left().accept(this);
		final Formula right = formula.right().accept(this);
		
		ret = simplify(op, left, right);
		
		if (ret==null) {
		
			final int hash = hash(op, left, right);
			for(Iterator<PartialCannonicalizer.Holder<Formula>> itr = formulas.get(hash); itr.hasNext(); ) {
				final Formula next = itr.next().obj;
				if (next instanceof BinaryFormula) { 
					final BinaryFormula hit = (BinaryFormula) next;
					if (hit.op()==op && hit.left()==left && hit.right()==right) { 
						return cache(formula, hit);
					}
				}
			}

			ret = left==formula.left()&&right==formula.right() ? formula : left.compose(op, right);
			formulas.add(new PartialCannonicalizer.Holder<Formula>(ret, hash));
		}
		
		return cache(formula,ret);
	}
	
	public Expression visit(BinaryExpression expr) { 
		Expression ret = lookup(expr);
		if (ret!=null) return ret;
		final ExprOperator op = expr.op();
		final Expression left = expr.left().accept(this);
		final Expression right = expr.right().accept(this);
		
		ret = simplify(op, left, right);
		
		if (ret==null) {
		
			final int hash = hash(op, left, right);
			for(Iterator<PartialCannonicalizer.Holder<Expression>> itr = exprs.get(hash); itr.hasNext(); ) {
				final Expression next = itr.next().obj;
				if (next instanceof BinaryExpression) { 
					final BinaryExpression hit = (BinaryExpression) next;
					if (hit.op()==op && hit.left()==left && hit.right()==right) { 
						return cache(expr, hit);
					}
				}
			}

			ret = left==expr.left()&&right==expr.right() ? expr : left.compose(op, right);
			exprs.add(new PartialCannonicalizer.Holder<Expression>(ret, hash));
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
			final int hash = hash(op, children.toArray());
			if (size==2) { 
				final Expression left = children.get(0), right = children.get(1);
				for(Iterator<PartialCannonicalizer.Holder<Expression>> itr = exprs.get(hash); itr.hasNext(); ) {
					final Expression next = itr.next().obj;
					if (next.getClass() == BinaryExpression.class) { 
						final BinaryExpression hit = (BinaryExpression) next;
						if (hit.op()==op && hit.left()==left && hit.right()==right) {
							return cache(expr, hit);
						}
					}
				}
			} else {
				for(Iterator<PartialCannonicalizer.Holder<Expression>> itr = exprs.get(hash); itr.hasNext(); ) {
					final Expression next = itr.next().obj;
					if (next.getClass() == NaryExpression.class) { 
						final NaryExpression hit = (NaryExpression) next;
						if (hit.op()==op && hit.size()==size && allSame(hit,children)) {
							return cache(expr, hit);
						}
					}
				}	
			}
			ret = expr.size()==size && allSame(expr,children) ? expr : Expression.compose(op, children);
			exprs.add(new PartialCannonicalizer.Holder<Expression>(ret, hash));
			return cache(expr,ret);
		}	
	}
	
	public Expression visit(UnaryExpression expr) { 
		Expression ret = lookup(expr);
		if (ret!=null) return ret;
		
		final ExprOperator op = expr.op();
		final Expression child = expr.expression().accept(this);
		
		if (isEmpty(child)) return cache(expr, child);
		final int hash = hash(op, child);
		for(Iterator<PartialCannonicalizer.Holder<Expression>> itr = exprs.get(hash); itr.hasNext(); ) {
			final Expression next = itr.next().obj;
			if (next.getClass()==UnaryExpression.class) { 
				if (((UnaryExpression)next).expression()==child)
					return cache(expr, next);
			}
		}
		ret = child==expr.expression() ? expr : child.apply(op);
		exprs.add(new PartialCannonicalizer.Holder<Expression>(ret, hash));
		return cache(expr,ret);
	}
	
	public IntExpression visit(UnaryIntExpression expr) { 
		IntExpression ret = lookup(expr);
		if (ret!=null) return ret;
		
		final IntOperator op = expr.op();
		final IntExpression child = expr.intExpr().accept(this);
		
		ret = simplify(op, child);
		if (ret==null) {
			final int hash = hash(op, child);
			for(Iterator<PartialCannonicalizer.Holder<IntExpression>> itr = intExprs.get(hash); itr.hasNext(); ) {
				final IntExpression next = itr.next().obj;
				if (next.getClass()==UnaryIntExpression.class) { 
					if (((UnaryIntExpression)next).intExpr()==child)
						return cache(expr, next);
				}
			}
			ret = child==expr.intExpr() ? expr : child.apply(op);
			intExprs.add(new PartialCannonicalizer.Holder<IntExpression>(ret, hash));
		}
		return cache(expr,ret);
	}
	
	public IntExpression visit(ExprToIntCast expr) { 
		IntExpression ret = lookup(expr);
		if (ret!=null) return ret;
		
		final ExprCastOperator op = expr.op();
		final Expression child = expr.expression().accept(this);
		
		final int hash = hash(op, child);
		for(Iterator<PartialCannonicalizer.Holder<IntExpression>> itr = intExprs.get(hash); itr.hasNext(); ) {
			final IntExpression next = itr.next().obj;
			if (next.getClass()==ExprToIntCast.class) { 
				if (((ExprToIntCast)next).expression()==child)
					return cache(expr, next);
			}
		}
		ret = child==expr.expression() ? expr : child.apply(op);
		intExprs.add(new PartialCannonicalizer.Holder<IntExpression>(ret, hash));
		return cache(expr,ret);
	}
	
	public Expression visit(IntToExprCast expr) { 
		Expression ret = lookup(expr);
		if (ret!=null) return ret;
		
		final IntCastOperator op = expr.op();
		final IntExpression child = expr.intExpr().accept(this);
		
		final int hash = hash(op, child);
		for(Iterator<PartialCannonicalizer.Holder<Expression>> itr = exprs.get(hash); itr.hasNext(); ) {
			final Expression next = itr.next().obj;
			if (next.getClass()==IntToExprCast.class) { 
				if (((IntToExprCast)next).intExpr()==child)
					return cache(expr, next);
			}
		}
		ret = child==expr.intExpr() ? expr : child.cast(op);
		exprs.add(new PartialCannonicalizer.Holder<Expression>(ret, hash));
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
		
			final int hash = hash(op, left, right);
			for(Iterator<PartialCannonicalizer.Holder<IntExpression>> itr = intExprs.get(hash); itr.hasNext(); ) {
				final IntExpression next = itr.next().obj;
				if (next instanceof BinaryIntExpression) { 
					final BinaryIntExpression hit = (BinaryIntExpression) next;
					if (hit.op()==op && hit.left()==left && hit.right()==right) { 
						return cache(expr, hit);
					}
				}
			}

			ret = left==expr.left()&&right==expr.right() ? expr : left.compose(op, right);
			intExprs.add(new PartialCannonicalizer.Holder<IntExpression>(ret, hash));
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
			final int hash = hash(op, children.toArray());
			if (size==2) { 
				final IntExpression left = children.get(0), right = children.get(1);
				for(Iterator<PartialCannonicalizer.Holder<IntExpression>> itr = intExprs.get(hash); itr.hasNext(); ) {
					final IntExpression next = itr.next().obj;
					if (next.getClass() == BinaryIntExpression.class) { 
						final BinaryIntExpression hit = (BinaryIntExpression) next;
						if (hit.op()==op && hit.left()==left && hit.right()==right)
							return cache(expr, hit);
					}
				}
			} else {
				for(Iterator<PartialCannonicalizer.Holder<IntExpression>> itr = intExprs.get(hash); itr.hasNext(); ) {
					final IntExpression next = itr.next().obj;
					if (next.getClass() == NaryIntExpression.class) { 
						final NaryIntExpression hit = (NaryIntExpression) next;
						if (hit.op()==op && hit.size()==size && allSame(hit,children)) {
							return cache(expr, hit);
						}
					}
				}	
			}
			ret = expr.size()==size && allSame(expr,children) ? expr : IntExpression.compose(op, children);
			intExprs.add(new PartialCannonicalizer.Holder<IntExpression>(ret, hash));
	
			return cache(expr,ret);
		}	
	}
}