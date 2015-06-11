
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

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.sparql.algebra.op.OpTriple;
import com.hp.hpl.jena.sparql.graph.NodeTransformLib;


/**
 * @author Kavitha Srinivas <ksrinivs@us.ibm.com>
 * @author Mariano Rodriguez <mrodrig@us.ibm.com>
 * 
 */public class StatementUnifierForJena {


	/**
	 * Computes the Most General Unifier (MGU) for two n-ary atoms.
	 * 
	 * @param first
	 * @param second
	 * @return the substitution corresponding to this unification.
	 */
	public static SubstitutionForJena getMGU(OpTriple st1, OpTriple st2) {
		SubstitutionForJena mgu = new SubstitutionImplForJena();
		SubstitutionForJena temp = process(st1.getTriple().getSubject(), st2.getTriple().getSubject(), mgu, st1, st2);
		if (temp == null) {
			return null;			// if we have a null substitution returned at any time, give up entirely
		}
		temp = process(st1.getTriple().getPredicate(), st2.getTriple().getPredicate(), mgu, st1, st2);
		if (temp == null) {
			return null;
		}
		temp = process(st1.getTriple().getObject(), st2.getTriple().getObject(), mgu, st1, st2);
		if (temp == null) {
			return null;
		}
		if (mgu.isEmpty()) {
			return new NeutralSubstitutionForJena();
		}
		return mgu;
	}
	
	private static SubstitutionForJena process(Node st1, Node st2, SubstitutionForJena mgu, OpTriple t1, OpTriple t2) {		
		SubstitutionForJena s = getUnifier(st1, st2);
		if (s == null)
			return null;
		if (s instanceof NeutralSubstitutionForJena)
			return s;

		SingletonSubstitutionForJena ss = (SingletonSubstitutionForJena) s;
		mgu.compose(ss.original, ss.substitution);

		applySubstitution(mgu, st1, t1);
		applySubstitution(mgu, st2, t2);
		return ss;

	}
	

	private static void applySubstitution(SubstitutionForJena s, Node node, OpTriple t) {		
		SubstitutionApplierForJena visitor = new SubstitutionApplierForJena(s);
		NodeTransformLib.transform(visitor, t);	
	}

	/***
	 * Returns null if both terms cant be unified, a neutralsubstitution if they
	 * are already equal, or a singleton substitution otherwise.
	 * 
	 * @param term1
	 * @param term2
	 * @return
	 */
	public static SubstitutionForJena getUnifier(Node term1, Node term2) {

		if (term1.equals(term2))
			return new NeutralSubstitutionForJena();

		if (!term1.isVariable() && !term2.isVariable()) {
			if (term1.matches(term2)) {
				return new NeutralSubstitutionForJena();
			}
			return null;
		}
		else if (term1.isVariable() && !term2.isVariable())// left is always a variable
			return new SingletonSubstitutionForJena(term1, term2);
		else {
			// both not equal variables, substitution right for left (priority for
			// original names).
			return new SingletonSubstitutionForJena(term2, term1);  // n, x
		}
	}
}
