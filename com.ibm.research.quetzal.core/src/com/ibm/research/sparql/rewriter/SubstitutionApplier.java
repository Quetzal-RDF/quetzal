/*
 * Copyright (C) 2009-2013, Free University of Bozen Bolzano
 * This source code is available under the terms of the Affero General Public
 * License v3.
 * 
 * Please see LICENSE.txt for full license terms, including the availability of
 * proprietary exceptions.
 */
package com.ibm.research.sparql.rewriter;

import org.openrdf.query.algebra.Var;
import org.openrdf.query.algebra.helpers.QueryModelVisitorBase;

/**
 * @author Mariano Rodriguez Muro <mariano.muro@gmail.com>
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

	// @Override
	// public void meet(ProjectionElem projElem)
	// throws RuntimeException
	// {
	// // if (projElem.getSourceName().equals(oldName)) {
	// // projElem.setSourceName(newName);
	// // }
	// }

}
