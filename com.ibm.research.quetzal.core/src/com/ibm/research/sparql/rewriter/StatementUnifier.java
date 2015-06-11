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

package com.ibm.research.sparql.rewriter;

import org.openrdf.query.algebra.QueryModelNode;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.Var;

/**
 * @author Mariano Rodriguez <mrodrig@us.ibm.com>
 * 
 */
public class StatementUnifier {

	/**
	 * 
	 */
	public StatementUnifier() {
	}

	

	/**
	 * Computes the Most General Unifier (MGU) for two n-ary atoms.
	 * 
	 * @param first
	 * @param second
	 * @return the substitution corresponding to this unification.
	 */
	public static Substitution getMGU(StatementPattern st1, StatementPattern st2) {

		Var term1;
		Var term2;

		Substitution mgu = new SubstitutionImpl();

		for (int i = 0; i < 3; i++) {
			term1 = st1.getVarList().get(i);
			term2 = st2.getVarList().get(i);
			Substitution s = getUnifier(term1, term2);
			if (s == null)
				return null;
			if (s instanceof NeutralSubstitution)
				continue;

			SingletonSubstituion ss = (SingletonSubstituion) s;
			mgu.compose(ss.original, ss.substition);

			st1 = st1.clone();
			st2 = st2.clone();

			applySubstitution(mgu, st1);
			applySubstitution(mgu, st2);
		}
		if (mgu.isEmpty())
			return new NeutralSubstitution();
		
		return mgu;

	}

	private static void applySubstitution(Substitution s, QueryModelNode node) {
		
			SubstitutionApplier visitor = new SubstitutionApplier(s);
			node.visit(visitor);
		
	}

	/***
	 * Returns null if both terms cant be unified, a neutralsubstition if they
	 * are already equal, or a singleton subtitution otherwise.
	 * 
	 * @param term1
	 * @param term2
	 * @return
	 */
	public static Substitution getUnifier(Var term1, Var term2) {
		Var left = !term1.isConstant() ? term1 : term2;
		Var right = !term1.isConstant() ? term2 : term1;

		if (term1.equals(term2))
			return new NeutralSubstitution();

		if (left.isConstant() && right.isConstant()) {
			if (left.getValue().equals(right.getValue())) {
				return new NeutralSubstitution();
			}
			return null;
		}
		else if (right.isConstant())// left is always a variable
			return new SingletonSubstituion(left, right);
		else
			// both not equal variables, substition right for left (priority for
			// original names).
			return new SingletonSubstituion(right, left);
	}
}
