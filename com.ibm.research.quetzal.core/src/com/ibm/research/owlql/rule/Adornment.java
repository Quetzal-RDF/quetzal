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


public interface Adornment {
	boolean isAdornmentOnRule();
	boolean isAdornmentOnPredicate();
	RuleAdornment asRuleAdornment();
	PredicateAdornment asPredicateAdornment();
}
