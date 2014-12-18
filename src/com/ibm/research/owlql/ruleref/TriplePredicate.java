/*******************************************************************************
* Licensed Materials - Property of IBM
* (c) Copyright IBM Corporation 2011, 2013. All Rights Reserved.
*
* Note to U.S. Government Users Restricted Rights:
* Use, duplication or disclosure restricted by GSA ADP Schedule
* Contract with IBM Corp.
*******************************************************************************/
package com.ibm.research.owlql.ruleref;

import java.util.HashSet;
import java.util.Set;

import com.ibm.research.owlql.rule.AtomicFormula;
import com.ibm.research.owlql.rule.Expr;
import com.ibm.research.owlql.rule.Predicate;
import com.ibm.research.owlql.rule.Rule;
import com.ibm.research.owlql.rule.VariableExpr;
/**
 * A predicate representing a rdf triple
 * @author fokoue
 *
 */
public class TriplePredicate extends Predicate {

	public static final String TRIPLE_PREDICATE = "triple";
	public static Set<VariableExpr> getPredicateVariables(Rule r) {
		Set<VariableExpr> ret = new HashSet<VariableExpr>();
		for (AtomicFormula af: r.getBody()) {
			if (af.getPredicate() instanceof TriplePredicate) {
				assert af.getPredicate().getArity() == 3;
				Expr e;
				if ((e=af.getArguments().get(1)).isVariable()) {
					ret.add((VariableExpr)e);
				}
			}
		}
		if (r.getHead().getPredicate() instanceof TriplePredicate) {
			assert r.getHead().getPredicate().getArity() == 3;
			Expr e;
			if ((e= r.getHead().getArguments().get(1)).isVariable()) {
				ret.add((VariableExpr)e);
			}
		}
		return ret;
	}
	
	
	
	public TriplePredicate() {
		super(TRIPLE_PREDICATE, 3);
	}

	@Override
	public Predicate clone() {
		return super.clone();
	}

	Predicate cloneWithNonZeroArity() {
		return clone();
	}
}
