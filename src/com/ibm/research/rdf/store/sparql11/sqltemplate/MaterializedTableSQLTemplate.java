package com.ibm.research.rdf.store.sparql11.sqltemplate;

import java.util.Set;

import com.ibm.research.rdf.store.Context;
import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.sparql11.planner.PlanNode;

public class MaterializedTableSQLTemplate extends AbstractSQLTemplate {
	public MaterializedTableSQLTemplate(String templateName, PlanNode planNode,
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
