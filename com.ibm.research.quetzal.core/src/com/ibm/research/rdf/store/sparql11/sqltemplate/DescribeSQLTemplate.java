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
import java.util.Map;
import java.util.Set;

import com.ibm.research.rdf.store.Context;
import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.config.Constants;
import com.ibm.research.rdf.store.sparql11.model.BinaryUnion;
import com.ibm.research.rdf.store.sparql11.model.IRI;
import com.ibm.research.rdf.store.sparql11.model.Query;
import com.ibm.research.rdf.store.sparql11.model.Variable;
import com.ibm.research.rdf.store.sparql11.sqlwriter.SQLWriterException;
import com.ibm.wala.util.collections.HashMapFactory;

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
	Map<String, SQLMapping> populateMappings() throws SQLWriterException {
		
		Map<String,SQLMapping> mappings = HashMapFactory.make();
		
		List<String> qidSqlParam = getSQLIDMapping();
		SQLMapping qidSqlParams=new SQLMapping("sql_id", qidSqlParam,null);
		mappings.put("sql_id", qidSqlParams);
		
				
		SQLMapping tMapping=new SQLMapping("target", getTargetSQLClause(),null);
		mappings.put("target", tMapping);
			
		SQLMapping eMapping=new SQLMapping("entry_constraint", getEntrySQLConstraint(),null);
		mappings.put("entry_constraint", eMapping);	
		
		SQLMapping predicateColumnsSQLParams = new SQLMapping("columns", getPredColumnsSQLParams(),null);
		mappings.put("columns", predicateColumnsSQLParams);
			
		SQLMapping tSMapping=new SQLMapping("s_target", getTargetSecondarySQLName(),null);
		mappings.put("s_target", tSMapping);
		
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
