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

import org.openrdf.query.algebra.ProjectionElem;
import org.openrdf.query.algebra.Var;
import org.openrdf.query.algebra.helpers.QueryModelVisitorBase;

/**
 * @author Mariano Rodriguez <mrodrig@us.ibm.com>
 * 
 */
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
