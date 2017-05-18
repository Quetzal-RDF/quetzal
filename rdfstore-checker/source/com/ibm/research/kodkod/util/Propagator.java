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

import java.util.LinkedHashSet;
import java.util.Set;

import kodkod.ast.Expression;
import kodkod.ast.Formula;
import kodkod.ast.IntConstant;
import kodkod.ast.NotFormula;
import kodkod.util.ints.SparseSequence;

/**
 * Propagates conditions that are true in the top-level conjunction to if-then-else 
 * conditions.  PartialCannonicalizer should be applied before the propagator.
 * @author etorlak
 */
final class Propagator extends Simplifier {
	private final Set<Formula> conjuncts;
	private final Set<Formula> negs;
	/**
	 * Constructs a new propagator.
	 */
	private Propagator(Set<Formula> conjuncts, SparseSequence<Expression> empties, SparseSequence<IntConstant> constants) {
		super(empties, constants);
		this.conjuncts = conjuncts;
		this.negs = new LinkedHashSet<Formula>();
		for(Formula f: conjuncts) { 
			if (f instanceof NotFormula)
				negs.add(((NotFormula)f).formula());
		}
	}
	
	/**
	 * Applies condition propagation to the given formula, using the information from the given simplifier.
	 * @return a simplification of the given formula
	 */
	static Formula apply(Formula formula, Simplifier simplifier) {
		final Propagator p = new Propagator(kodkod.util.nodes.Nodes.roots(formula), simplifier.empties, simplifier.constants);
		final Set<Formula> out = new LinkedHashSet<Formula>();
		for(Formula f : p.conjuncts) { 
			final Formula visited = f.accept(p);
			if (visited==Formula.FALSE) { 
				return Formula.FALSE;
			} else if (visited!=Formula.TRUE) {
				out.add(visited);
			}
		}
		return Formula.and(out);
	}
	
	final boolean isTrue(Formula formula) { return formula==Formula.TRUE || conjuncts.contains(formula); }
	final boolean isFalse(Formula formula) {
		if (formula==Formula.FALSE) return true;
		else if (!conjuncts.contains(formula)) {
			if (formula.getClass()==NotFormula.class) { 
				return conjuncts.contains(((NotFormula)formula).formula());
			} else {
				return negs.contains(formula);
			}
		} 
		return false;
	}
	
}