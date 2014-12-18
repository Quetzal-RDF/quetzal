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
