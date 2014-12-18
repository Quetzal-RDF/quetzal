/*******************************************************************************
* Licensed Materials - Property of IBM
* (c) Copyright IBM Corporation 2011, 2013. All Rights Reserved.
*
* Note to U.S. Government Users Restricted Rights:
* Use, duplication or disclosure restricted by GSA ADP Schedule
* Contract with IBM Corp.
*******************************************************************************/
package com.ibm.research.owlql.rule;


/**
 * Predicate whose first argument is a variable and the others arguments indicate the values
 * that this variable can take. 
 * @author fokoue
 *
 */
public class BoundVariablePredicate extends Predicate {

	public BoundVariablePredicate(int arity) {
		super(Rule.BUILT_IN_BOUND_VAR, arity);
		assert arity >= 2 : arity;
	}
	
	public BoundVariablePredicate clone() {
		return new BoundVariablePredicate(getArity());
	}
	
	Predicate cloneWithNonZeroArity() {
		return clone();
	}
	
	public boolean equals(Object o) {
		if (o instanceof BoundVariablePredicate) {
			return super.equals(o);
		} else {
			return false;
		}
	}

	
}
