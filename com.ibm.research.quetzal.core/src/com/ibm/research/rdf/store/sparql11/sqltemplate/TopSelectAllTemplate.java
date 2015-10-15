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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.research.rdf.store.Context;
import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.config.Constants;
import com.ibm.research.rdf.store.sparql11.model.Query;
import com.ibm.research.rdf.store.sparql11.model.Variable;
import com.ibm.wala.util.collections.HashMapFactory;


public class TopSelectAllTemplate extends SolutionModifierBaseTemplate {

	protected Query q;
	protected STPlanWrapper wrapper;
	protected List<Variable> toProject;
	protected Set<Variable> explicitIRIBoundVariables;
	
	public TopSelectAllTemplate(String templateName, Query q, Store store, Context ctx,
			STPlanWrapper wrapper, List<Variable> project, Set<Variable> explicitIRIBoundVariables) {
		super(templateName, store, ctx, wrapper);
		this.wrapper = wrapper;
		this.toProject = project;
		this.q = q;
		this.explicitIRIBoundVariables = explicitIRIBoundVariables;
		wrapper.incrementCteIdForSolutionModifier();
	}

	Map<String, SQLMapping> populateMappings() throws Exception{
		Map<String, SQLMapping> mappings = HashMapFactory.make();
		
		List<String> projectList = getSelectProjectMapping(); 
		SQLMapping projectMapping=new SQLMapping("project", projectList,null);
		mappings.put("project", projectMapping);
		
		
		SQLMapping tMapping=new SQLMapping("target", getTargetSQLClause(),null);
		mappings.put("target", tMapping);
		
		SQLMapping storeNameMapping=new SQLMapping("store_name", store.getStoreName(),null);
		mappings.put("store_name", storeNameMapping);
		
		return mappings;
	}
	
	private List<String> getSelectProjectMapping() throws Exception{
		List<String> projectMapping = new LinkedList<String>();

		Set<Variable> queryPatternVariables = new HashSet<Variable>();
		// eliminate blank nodes
		// Achille : We should not eliminate blanknode
		Set<Variable> patternVariables = q.getMainPattern().gatherVariablesWithOptional();;
		for (Variable v : patternVariables)
			//if (!(v instanceof BlankNodeVariable))
				queryPatternVariables.add(v);

		Set<Variable> iriBoundVariables = explicitIRIBoundVariables!=null? 
					explicitIRIBoundVariables: q.getMainPattern().gatherIRIBoundVariables();
		if (toProject != null) {
			for (Variable pv : toProject) {
				if (queryPatternVariables != null && queryPatternVariables.contains(pv)) {
					String sqlVarName = wrapper.getLastVarMappingForQueryInfo(pv);
					Variable renamedpv = wrapper.getRenamedVariableFor(pv);
					projectMapping.add(sqlVarName+" AS "+ renamedpv.getName());
					if( !iriBoundVariables.contains(pv)){
						String sqlVarTypeName = (sqlVarName != null)?sqlVarName+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS:null; 
						projectMapping.add(sqlVarTypeName+" AS "+ renamedpv.getName()+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);
					}
				}
			}
		}
		else{
			projectMapping.add("*");
		}
		return projectMapping;
	}
}
