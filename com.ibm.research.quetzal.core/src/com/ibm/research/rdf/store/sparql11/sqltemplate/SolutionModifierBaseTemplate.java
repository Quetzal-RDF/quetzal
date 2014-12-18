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
 package com.ibm.research.rdf.store.sparql11.sqltemplate;

import java.util.LinkedList;
import java.util.List;

import com.ibm.research.rdf.store.Context;
import com.ibm.research.rdf.store.Store;

public abstract class SolutionModifierBaseTemplate extends AbstractSQLTemplate{

	public SolutionModifierBaseTemplate(String templateName, Store store,
			Context ctx, STPlanWrapper wrapper) {
		super(templateName, store, ctx, wrapper);
	}

	protected List<String> getTargetSQLClause(){
		List<String> targetSQLClause = new LinkedList<String>();
		String predecesorCteId = wrapper.getCtePredecessorForSolutionModifier();
		if(predecesorCteId != null)targetSQLClause.add(predecesorCteId);
		return targetSQLClause;		
	}
	
}
