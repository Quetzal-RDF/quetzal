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
