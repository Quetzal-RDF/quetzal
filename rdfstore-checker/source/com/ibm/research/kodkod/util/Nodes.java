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

import static kodkod.ast.Expression.NONE;
import static kodkod.ast.Expression.product;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kodkod.ast.ComparisonFormula;
import kodkod.ast.Expression;
import kodkod.ast.Formula;
import kodkod.ast.MultiplicityFormula;
import kodkod.ast.Node;
import kodkod.ast.Relation;
import kodkod.ast.Variable;
import kodkod.ast.operator.ExprCompOperator;
import kodkod.ast.visitor.AbstractReplacer;
import kodkod.instance.Bounds;
import kodkod.instance.TupleFactory;
import kodkod.instance.TupleSet;


/**
 * A collection of helper methods for constructing
 * Kodkod nodes (expression, formulas, and int expressions).
 * 
 * 
 * @author Emina Torlak
 */
public final class Nodes {
	static final Expression NONE2 = NONE.product(NONE);
	
	private Nodes() {} // singleton
	
	/**
	 * Returns a unary tupleset, created by the given factory, consisting
	 * of tuples that contain the given atoms.
	 * @requires atoms in tuples.universe.atoms[int]
	 * @return a unary tupleset, created by the given factory, consisting
	 * of tuples that contain the given atoms.
	 */
	public static TupleSet tupleset(TupleFactory tuples, Collection<?> atoms) { 
		final TupleSet ret = tuples.noneOf(1);
		for(Object atom : atoms) { 
			ret.add( tuples.tuple(atom) );
		}
		return ret;
	}
	
	/**
	 * Returns an empty expression of the given arity.
	 * @requires arity > 0
	 * @return Expression.NONE^arity
	 */
	public static Expression empty(int arity) { 
		assert arity > 0;
		switch(arity) {
		case 1 : return NONE;
		case 2 : return NONE2;
		default : return product(Collections.nCopies(arity, NONE));
		}
	}
	
	/**
	 * Simplifies the argument formula, if possible,
	 * using logic equivalence laws.
	 * @return simplified formula
	 */
	public static Formula simplify(Formula formula) { 
//		System.out.println("-----ORIGINAL-----");
//		System.out.println(Strings.prettyPrint(formula, 2, 200));
		final Set<Formula> conjuncts = kodkod.util.nodes.Nodes.roots(formula);
		final PartialCannonicalizer canonicalizer = new PartialCannonicalizer();
		final Formula simplified = Formula.and(conjuncts).accept(canonicalizer);
//		System.out.println("-----SIMPLIFIED-----");
//		System.out.println(Strings.prettyPrint(simplified, 2, 200));
		return Propagator.apply(simplified, canonicalizer);
	}
	
	/**
	 * Simplifies the given formula and bounds using logic equivalence laws.
	 * This method assumes that the given bounds object is modifiable.
	 * @effects simplifies the given bounds (if possible) by bounding some relations exactly
	 * @return simplified formula
	 */
	@SuppressWarnings("unchecked")
	public static Formula simplify(final Formula formula, final Bounds bounds) { 
		final AbstractReplacer replacer = new AbstractReplacer(Collections.EMPTY_SET) { 
			protected <N extends Node> N cache(N node, N replacement) {
				cache.put(node, replacement);
				return replacement;
			}
			public Formula visit(ComparisonFormula formula) { 
				Formula ret = lookup(formula);
				if (ret!=null) return ret;
				if (formula.left() instanceof Relation && formula.right() instanceof Relation) { 
					final Relation left = (Relation) formula.left();
					final Relation right = (Relation) formula.right();
					final TupleSet ll = bounds.lowerBound(left), lu = bounds.upperBound(left);
					final TupleSet rl = bounds.lowerBound(right), ru = bounds.upperBound(right);
					if (ll.equals(lu) && rl.equals(ru)) { 
						if (formula.op()==ExprCompOperator.EQUALS)
							return cache(formula, ll.equals(rl) ? Formula.TRUE : Formula.FALSE);
						else 
							return cache(formula, rl.containsAll(ll) ? Formula.TRUE : Formula.FALSE);
					}
				}
				return cache(formula, formula);
			}
		};
		final Set<Formula> conjuncts = kodkod.util.nodes.Nodes.roots(simplify(formula.accept(replacer)));
		for(Iterator<Formula> itr = conjuncts.iterator(); itr.hasNext(); ) { 
			final Formula next = itr.next();
			if (next instanceof MultiplicityFormula) { 
				final MultiplicityFormula multFormula = (MultiplicityFormula) next;
				if (multFormula.expression() instanceof Relation) { 
					final Relation r = (Relation) multFormula.expression();
					final TupleSet u = bounds.upperBound(r);
					final TupleSet l = bounds.lowerBound(r);
					if (u.size()==1 && l.isEmpty()) { 
						switch(multFormula.multiplicity()) { 
						case ONE : case SOME :
							bounds.boundExactly(r, u);
							break;
						case NO : 
							bounds.boundExactly(r, l);
							break;
						case LONE : 
							break; // do nothing
						default :  
							throw new AssertionError("Unknown multiplicity: " + multFormula.multiplicity());
						}
						itr.remove();
					}
				}
			}
			
		}
		return Formula.and(conjuncts);
	}
	
	/**
	 * Returns a formula that evaluates to true iff r is a total order
	 * over the set s.
	 * @requires r.arity = 2 and s.arity = 1
	 * @return a formula that evaluates to true iff r is reflexive, complete,
	 * transitive and antisymmetric over the set s.
	 */
	public static Formula totalOrder(Expression r, Expression s) { 
		assert r.arity() == 2 && s.arity() == 1;
		final List<Formula> to = new ArrayList<Formula>();
		// reflexive and complete over s
		to.add( s.product(s).in(r.union(r.transpose())) );
		// transitive
		to.add( r.join(r).in(r) );
		// antisymmetric
		to.add( r.intersection(r.transpose()).in(Expression.IDEN) );
		return Formula.and(to);
	}
	
	/**
	 * Returns an expression that evaluates to the transitive reduction of r.
	 * @requires r.arity = 2
	 * @return an expression that evaluates to the transitive reduction of r.
	 */
	public static Expression transitiveReduction(Expression r) { 
		final Variable a = Variable.unary("a");
		final Variable b = Variable.unary("b");
		final Expression dom = r.join(Expression.UNIV);
		final Expression ran = a.join(r);
		final Formula reduct = a.join(r).intersection(r.join(b)).in(a.union(b));//r.difference(a.product(b)).closure().eq(r.closure()).not();
		return reduct.comprehension(a.oneOf(dom).and(b.oneOf(ran)));
	}
	
	/**
	 * Returns a node generated by replacing the nodes in replacements.keySet() 
	 * with their corresponding values.
	 * @requires all n: replacements.keySet() | 
	 * 	n in Formula => replacements.get(n) in Formula &&
	 *  n in Decl => replacements.get(n) in Decl &&
	 *  n in Decls => replacements.get(n) in Decls &&
	 *  n in IntExpression => replacements.get(n) in IntExpression &&
	 *  n in Expression =>  replacements.get(n) in Expression && n.arity = replacements.get(n).arity
	 * @return  a node generated by replacing the nodes in replacement.keySet() 
	 * with their corresponding values.
	 */
	@SuppressWarnings("unchecked")
	public static <N extends Node> N replaceAll(N node, final Map<? extends Node, ? extends Node> replacements) { 
		final AbstractReplacer replacer = new AbstractReplacer(Collections.EMPTY_SET) {
			protected <T extends Node> T cache(T node, T replacement) { 
				if (replacements.containsKey(node)) { 
					replacement = (T) replacements.get(node);
				}
				super.cache.put(node, replacement);
				return replacement;
			}
		};
		return (N) node.accept(replacer);
	}

}
