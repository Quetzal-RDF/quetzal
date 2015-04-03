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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.ibm.research.rdf.store.Context;
import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.config.Constants;
import com.ibm.research.rdf.store.sparql11.model.Pattern;
import com.ibm.research.rdf.store.sparql11.model.ProjectedVariable;
import com.ibm.research.rdf.store.sparql11.model.Query;
import com.ibm.research.rdf.store.sparql11.model.SelectClause;
import com.ibm.research.rdf.store.sparql11.model.SolutionModifiers;
import com.ibm.research.rdf.store.sparql11.model.Variable;


public class TopSelectTemplate extends AbstractSelectTemplate {

	Query q;
	public TopSelectTemplate(String templateName, Query q, Store store, Context ctx,
			STPlanWrapper wrapper, Set<Variable> explicitIRIBoundVariables) {
		super(templateName, store, ctx, wrapper, explicitIRIBoundVariables);
		wrapper.incrementCteIdForSolutionModifier();
		this.q = q;
		}
	
	@Override
	protected Set<Variable> getAllPatternVariables() {
		if(q.isSelectQuery())return q.getSelectQuery().getAllPatternVariables();
		if(q.isConstructQuery())return q.getConstructQuery().getAllPatternVariables();
		if(q.isDescribeQuery())return q.getDescribeQuery().getAllPatternVariables();
		else return null;
	}

	@Override
	protected SelectClause getSelectClause() {
		if(q.isSelectQuery())return q.getSelectQuery().getSelectClause();
		else return null;		
	}
	@Override
	protected Pattern getPattern() {
		return q.getMainPattern();
	}
	@Override
	protected SolutionModifiers getSolutionModifiers() {
		if(q.isSelectQuery()) return q.getSelectQuery().getSolutionModifier();
		else if (q.isDescribeQuery())return q.getDescribeQuery().getSolutionModifier();
		else if(q.isConstructQuery())return q.getConstructQuery().getSolutionModifier();
		else return null;
	}
	
	@Override
	protected List<ProjectedVariable> getProjectedVariables() {
		if(q.isSelectQuery())return q.getSelectQuery().getSelectClause().getProjectedVariables();
		if(q.isSelectQuery())return q.getSelectQuery().getSelectClause().getProjectedVariables();
		else return Collections.emptyList();
	}
	
	/*@Override
	protected List<String> getProjectMapping(List<String> projectAliasNames) throws Exception{
		if(q.isSelectQuery())return super.getProjectMapping(projectAliasNames);
		if(q.isDescribeQuery())return getDescribeProjectMapping();
		if(q.isConstructQuery())return getConstructProjectMapping();
		return null;
	}*/
	
	private List<String> getConstructProjectMapping() throws Exception{
		List<String> projectMapping = new LinkedList<String>();
		Set<Variable> queryPatternVariables = q.getConstructQuery().getAllPatternVariables();
		Set<Variable> projectedVariables = q.getConstructQuery().getConstructPatternVariables();
		Set<Variable> iriBoundVariables = q.getConstructQuery().getPattern().gatherIRIBoundVariables();
		if (projectedVariables != null) {
			for (Variable pv : projectedVariables) {
				Variable renamedpv = wrapper.getRenamedVariableFor(pv);
				if (queryPatternVariables != null && queryPatternVariables.contains(pv)) {
					String sqlVarName = wrapper.getLastVarMappingForQueryInfo(pv);
					projectMapping.add(sqlVarName+" AS "+ renamedpv.getName());
					if( !iriBoundVariables.contains(pv)){
						String sqlVarTypeName = (sqlVarName != null)?sqlVarName+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS:null; 
						projectMapping.add(sqlVarTypeName+" AS "+ renamedpv.getName()+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);
					}
				} 
			}
		}
		return projectMapping;
	}

}
