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


import java.util.HashSet;
import java.util.Set;

import com.ibm.research.rdf.store.Context;
import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.sparql11.model.Query;

public class AskTemplate extends SolutionModifierBaseTemplate {

	Query q;
	
	public AskTemplate(String templateName, Query q, Store store, Context ctx,
			STPlanWrapper wrapper) {
		super(templateName, store, ctx, wrapper);
		this.q = q;
		wrapper.incrementCteIdForSolutionModifier();
	}

	Set<SQLMapping> populateMappings() throws Exception{
		
		HashSet<SQLMapping> mappings = new HashSet<SQLMapping>();
		
		SQLMapping tMapping=new SQLMapping("target", getTargetSQLClause(),null);
		mappings.add(tMapping);
		
		return mappings;
	}
}
