/*
 * Copyright (C) 2009-2013, Free University of Bozen Bolzano
 * This source code is available under the terms of the Affero General Public
 * License v3.
 * 
 * Please see LICENSE.txt for full license terms, including the availability of
 * proprietary exceptions.
 */
package com.ibm.research.sparql.rewriter;

import org.openrdf.query.algebra.ProjectionElem;
import org.openrdf.query.algebra.Var;
import org.openrdf.query.algebra.helpers.QueryModelVisitorBase;

public class VarRenamer extends QueryModelVisitorBase<RuntimeException> {

	private final String oldName;

	private final String newName;

	public VarRenamer(String oldName, String newName) {
		this.oldName = oldName;
		this.newName = newName;
	}

	@Override
	public void meet(Var var) {
		if (var.getName().equals(oldName)) {
			var.setName(newName);
		}
	}

	@Override
	public void meet(ProjectionElem projElem)
		throws RuntimeException
	{
		if (projElem.getSourceName().equals(oldName)) {
			projElem.setSourceName(newName);
		}
	}
}
