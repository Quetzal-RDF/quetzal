package com.ibm.rdf.store.sparql11.sqltemplate;


import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.ibm.rdf.store.Context;
import com.ibm.rdf.store.Store;
import com.ibm.rdf.store.config.Constants;
import com.ibm.rdf.store.sparql11.model.BlankNodeVariable;
import com.ibm.rdf.store.sparql11.model.ProjectedVariable;
import com.ibm.rdf.store.sparql11.model.Query;
import com.ibm.rdf.store.sparql11.model.Variable;

public class OffsetTemplate extends SolutionModifierBaseTemplate {

	Query q;
	
	public OffsetTemplate(String templateName, Query q, Store store, Context ctx,
			STPlanWrapper wrapper) {
		super(templateName, store, ctx, wrapper);
		this.q = q;
		wrapper.incrementCteIdForSolutionModifier();
	}

	Set<SQLMapping> populateMappings() throws Exception{
		HashSet<SQLMapping> mappings = new HashSet<SQLMapping>();
		
		SQLMapping qidSqlParams=new SQLMapping("sql_id", getSQLIDMapping(),null);
		mappings.add(qidSqlParams);
			
		SQLMapping limitSqlParams=new SQLMapping("limit", getLimitSqlParams() ,null);
		mappings.add(limitSqlParams);
		
		SQLMapping offsetSqlParams=new SQLMapping("offset", getOffsetSqlParams() ,null);
		mappings.add(offsetSqlParams);
		
		List<String> projectList = getProjectMapping();
		if(projectList.size() == 0) projectList.add("*");	
		SQLMapping projectMapping=new SQLMapping("project", projectList,null);
		mappings.add(projectMapping);
			
		
		SQLMapping tMapping=new SQLMapping("target", getTargetSQLClause(),null);
		mappings.add(tMapping);
		
		return mappings;
	}
	
	private List<String> getProjectMapping() throws Exception{
		if(q.isSelectQuery())return getSelectProjectMapping();
		if(q.isDescribeQuery())return getDescribeProjectMapping();
		return null;
	}
	
	private List<String> getSelectProjectMapping() throws Exception{
		List<String> projectMapping =new LinkedList<String>();

		Set<Variable> queryPatternVariables = new HashSet<Variable>();
		// eliminate blank nodes
		Set<Variable> patternVariables = q.getSelectQuery().getAllPatternVariables();
		for (Variable v : patternVariables)
			if (!(v instanceof BlankNodeVariable))
				queryPatternVariables.add(v);

		List<ProjectedVariable> projectedVariables = q.getSelectQuery().getSelectClause().getProjectedVariables();
		Set<Variable> iriBoundVariables = q.getSelectQuery().getPattern().gatherIRIBoundVariables();
		if (projectedVariables != null) {
			for (ProjectedVariable pv : projectedVariables) {
				Variable renamedpv = wrapper.getRenamedVariableFor(pv.getVariable());
				if (queryPatternVariables != null
						&& queryPatternVariables.contains(pv.getVariable())) {
					projectMapping.add(pv.getVariable().getName()+ " AS "+renamedpv.getName());
					if(!iriBoundVariables.contains(pv.getVariable())){
						projectMapping.add(pv.getVariable().getName()+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS+" AS "+ renamedpv.getName()+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);
					}		
				}
				else if (pv.getExpression() != null) {
					for(Variable ev : pv.getExpression().gatherVariables()){
						Variable renamedev = wrapper.getRenamedVariableFor(ev);
						projectMapping.add(ev.getName()+" AS "+renamedev.getName());
					}
				} else {
					projectMapping.add("NULL AS "+pv.getVariable().getName());
					if(!iriBoundVariables.contains(pv.getVariable())){
						projectMapping.add("null AS "+ renamedpv.getName()+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);
					}
				}
			}
		} 
		
		return projectMapping;
	}

	
	private List<String> getSQLIDMapping(){
		List<String> qIdMapping = new LinkedList<String>();
		Integer planId = wrapper.getCteIdForSolutionModifier();
		qIdMapping.add(planId.toString());
		return qIdMapping;
	}
	
	private List<String> getLimitSqlParams(){
		List<String> limitSqlParams = new LinkedList<String>();
		Integer limit = (q.isSelectQuery())?q.getSelectQuery().getSolutionModifier().getLimitOffset().getLimit():
			q.getDescribeQuery().getSolutionModifier().getLimitOffset().getLimit();
		Integer offset = (q.isSelectQuery())?q.getSelectQuery().getSolutionModifier().getLimitOffset().getOffset():
			q.getDescribeQuery().getSolutionModifier().getLimitOffset().getOffset();
		limit += offset;
		limitSqlParams.add(limit.toString());
		return limitSqlParams;
	}
	
	private List<String> getOffsetSqlParams(){
		List<String> offsetSqlParams = new LinkedList<String>();
		Integer offset = (q.isSelectQuery())?q.getSelectQuery().getSolutionModifier().getLimitOffset().getOffset():
			q.getDescribeQuery().getSolutionModifier().getLimitOffset().getOffset();
		offsetSqlParams.add(offset.toString());
		return offsetSqlParams;
	}
}
