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

import org.openrdf.query.algebra.Var;
import org.openrdf.query.algebra.helpers.QueryModelVisitorBase;

/**
 * @author Mariano Rodriguez <mrodrig@us.ibm.com>
 * 
 */
public class SubstitutionApplier extends QueryModelVisitorBase<RuntimeException> {

	Substitution s;

	/**
	 * 
	 */
	public SubstitutionApplier(Substitution s) {
		this.s = s;
	}

	@Override
	public void meet(Var var) {
		Var replacement = s.get(var);
		if (replacement == null)
			return;
		var.setName(replacement.getName());
		var.setConstant(replacement.isConstant());
		var.setValue(replacement.getValue());
	}



}
