package com.ibm.rdf.store.sparql11.sqltemplate;

import java.util.Set;

import com.ibm.rdf.store.Context;
import com.ibm.rdf.store.Store;
import com.ibm.rdf.store.sparql11.stopt.STPlanNode;

public class MaterializedTableSQLTemplate extends AbstractSQLTemplate {
	public MaterializedTableSQLTemplate(String templateName, STPlanNode planNode,
			Store store, Context ctx, STPlanWrapper wrapper) {
		super(templateName, store, ctx, wrapper, planNode);
		this.planNode = planNode;
		wrapper.mapPlanNode(planNode);
	}

	@Override
	Set<SQLMapping> populateMappings() {		
		return null;
	}

	@Override
	public String createSQLString() throws Exception{
		return null;
	}
	
	
	
}
