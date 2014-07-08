package com.ibm.rdf.store.sparql11.sqltemplate;


import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.ibm.rdf.store.Context;
import com.ibm.rdf.store.Store;
import com.ibm.rdf.store.sparql11.model.Query;
import com.ibm.rdf.store.sparql11.stopt.STPlanNode;

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
