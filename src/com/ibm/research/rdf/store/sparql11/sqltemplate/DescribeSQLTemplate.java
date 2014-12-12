package com.ibm.research.rdf.store.sparql11.sqltemplate;


import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.ibm.research.rdf.store.Context;
import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.config.Constants;
import com.ibm.research.rdf.store.sparql11.model.BinaryUnion;
import com.ibm.research.rdf.store.sparql11.model.IRI;
import com.ibm.research.rdf.store.sparql11.model.Query;
import com.ibm.research.rdf.store.sparql11.model.Variable;
import com.ibm.research.rdf.store.sparql11.sqlwriter.SQLWriterException;

/**
 * @author mbornea
 *
 */
public class DescribeSQLTemplate extends SolutionModifierBaseTemplate {
	
	Query q;

	public DescribeSQLTemplate(String templateName, Query q, Store store, Context ctx, STPlanWrapper wrapper) {
		super(templateName, store, ctx, wrapper);
		this.q = q;
		wrapper.incrementCteIdForSolutionModifier();
	}

	@Override
	Set<SQLMapping> populateMappings() throws SQLWriterException {
		
		HashSet<SQLMapping> mappings = new HashSet<SQLMapping>();
		
		List<String> qidSqlParam = getSQLIDMapping();
		SQLMapping qidSqlParams=new SQLMapping("sql_id", qidSqlParam,null);
		mappings.add(qidSqlParams);
		
				
		SQLMapping tMapping=new SQLMapping("target", getTargetSQLClause(),null);
		mappings.add(tMapping);
			
		SQLMapping eMapping=new SQLMapping("entry_constraint", getEntrySQLConstraint(),null);
		mappings.add(eMapping);	
		
		SQLMapping predicateColumnsSQLParams = new SQLMapping("columns", getPredColumnsSQLParams(),null);
		mappings.add(predicateColumnsSQLParams);
			
		SQLMapping tSMapping=new SQLMapping("s_target", getTargetSecondarySQLName(),null);
		mappings.add(tSMapping);
		
		return mappings;
	}


	
	private LinkedList<String> getSQLIDMapping(){
		LinkedList<String> qIdMapping = new LinkedList<String>();
		Integer planId = wrapper.getCteIdForSolutionModifier();
		qIdMapping.add(planId.toString());
		return qIdMapping;
	}
	
	@Override
	protected LinkedList<String> getTargetSQLClause(){
		LinkedList<String> targetSQLClause = new LinkedList<String>();
		targetSQLClause.add(store.getDirectPrimary());
		if(requiresExternalVariables()){
			targetSQLClause.add(wrapper.getCtePredecessorForSolutionModifier());
		}
		return targetSQLClause;		
	}
	
	List<String> getPredColumnsSQLParams(){
		List<String> predColumnsId = new LinkedList<String>();
		
		for(Integer i=0;i<store.getDPrimarySize();i++){
			predColumnsId.add(i.toString());
		}		

		return predColumnsId;		
	}

	List<String> getEntrySQLConstraint(){
		List<String> entrySQLConstraint = new LinkedList<String>();
		if(q.getDescribeQuery().getResources()!=null){
			for (BinaryUnion<Variable, IRI> resource : q.getDescribeQuery().getResources()){
				if(resource.isFirstType()){
					String predecesorCteId = wrapper.getCtePredecessorForSolutionModifier();
					String entryPredName = wrapper.getLastVarMappingForQueryInfo(resource.getFirst());	
					entrySQLConstraint.add(Constants.NAME_COLUMN_ENTRY+" = "+predecesorCteId + "." +entryPredName);
				}
				else{
					entrySQLConstraint.add(Constants.NAME_COLUMN_ENTRY+" = '"+resource.getSecond().getValue()+"'");
				}
			}	
		}
		else{
			Set<Variable> patternVars = q.getDescribeQuery().getAllPatternVariables();
			for(Variable v : patternVars){
				String predecesorCteId = wrapper.getCtePredecessorForSolutionModifier();
				String entryPredName = wrapper.getLastVarMappingForQueryInfo(v);	
				entrySQLConstraint.add(Constants.NAME_COLUMN_ENTRY+" = "+predecesorCteId + "." +entryPredName);
			}
		}		
		return entrySQLConstraint;
	}


	
	LinkedList<String> getTargetSecondarySQLName(){
		LinkedList<String> targetSQLClause = new LinkedList<String>();
		targetSQLClause.add(store.getDirectSecondary());
		return targetSQLClause;	
	}
	
	boolean  requiresExternalVariables(){
		if(q.getDescribeQuery().getResources()!=null){
			for (BinaryUnion<Variable, IRI> resource : q.getDescribeQuery().getResources()){
				if(resource.isFirstType()){
					return true;
				}
			}	
		}
		return false;
	}
}
