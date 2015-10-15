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
import java.util.Map;
import java.util.Set;

import com.ibm.research.rdf.store.Context;
import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.config.Constants;
import com.ibm.research.rdf.store.sparql11.model.Pattern;
import com.ibm.research.rdf.store.sparql11.model.ProjectedVariable;
import com.ibm.research.rdf.store.sparql11.model.SelectClause;
import com.ibm.research.rdf.store.sparql11.model.SolutionModifiers;
import com.ibm.research.rdf.store.sparql11.model.SubSelectPattern;
import com.ibm.research.rdf.store.sparql11.model.Variable;
import com.ibm.research.rdf.store.sparql11.planner.PlanNode;
import com.ibm.wala.util.collections.Pair;

public class SubSelectTemplate extends AbstractSelectTemplate {

	private final SubSelectPattern select;
	
	public SubSelectTemplate(String templateName, PlanNode planNode, SubSelectPattern select, Store store, Context ctx,
			STPlanWrapper wrapper) {
		super(templateName, store, ctx, wrapper);
		this.planNode = planNode;
		this.select = select;
		wrapper.mapPlanNode(planNode);
	}

	@Override
	protected Set<Variable> getAllPatternVariables() {
		return select.getAllPatternVariables();
	}

	@Override
	protected SelectClause getSelectClause() {
		return select.getSelectClause();
	}

	@Override
	protected Pattern getPattern() {
		return select.getGraphPattern();
	}

	@Override
	protected SolutionModifiers getSolutionModifiers() {
		return select.getSolutionModifier();
	}


	@Override
	Map<String, SQLMapping> populateMappings() throws Exception {
		Map<String, SQLMapping> mappings = super.populateMappings();
		
		List<String> qidSqlParam = new LinkedList<String>();
		qidSqlParam.add(getQIDMapping());
		SQLMapping qidMapping=new SQLMapping("sql_id", qidSqlParam,null);
		mappings.put("sql_id", qidMapping);
		
		return mappings;
	}

	@Override
	protected List<String> getTargetSQLClause() {
		PlanNode body = wrapper.plan.getPlanTree().getSuccNodes(planNode).next();
		body = body.getPredecessorDown(wrapper.plan);
		String cte = wrapper.getPlanNodeCTE(body, true);
		return Collections.singletonList(cte);
	}

	@Override
	protected List<ProjectedVariable> getProjectedVariables() {
		List<ProjectedVariable> vs = new LinkedList<ProjectedVariable>();
		for(ProjectedVariable v : getSelectClause().getProjectedVariables()) {
			vs.add(v);
		}	
		return vs;
	}
	
	List<String> mapExternalVarForProject(){		
		List<String> map = new LinkedList<String>();
		PlanNode predecessor = planNode.getPredecessor(wrapper.getPlan());

		List<ProjectedVariable> projectedVars = getProjectedVariables();
		List<Variable> vars = new LinkedList<Variable>();
		for (ProjectedVariable v : projectedVars) {
			vars.add(v.getVariable());
		}
		if(predecessor!=null){
			String predecessorCTE = wrapper.getPlanNodeCTE(predecessor, false);
			Set<Variable> predecessorVars = predecessor.getAvailableVariables();
			if(predecessorVars != null){
				Set<Variable> iriBoundVariables = wrapper.getIRIBoundVariables();
				for(Variable v : predecessorVars){
					if (vars.contains(v)) continue;
					String vPredName = wrapper.getPlanNodeVarMapping(predecessor,v.getName());
					map.add(predecessorCTE+"."+v.getName()+" AS "+v.getName());
					String vType = null;						
					if(!iriBoundVariables.contains(v)){
						String vPredType = predecessorCTE+"."+v.getName()+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS;
						map.add(vPredType+" AS "+v.getName()+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);
						vType = vPredType;
					}
					varMap.put(v.getName(), Pair.make(predecessorCTE+"."+vPredName, vType));
				}
			}
		}
		return map;
	}
	/*@Override
	protected List<String> getProjectMapping(List<String> projectAliasNames) throws Exception {
		List<String> projectMapping = new LinkedList<String>(super.getProjectMapping(projectAliasNames));
		projectMapping.addAll(mapExternalVarForProject());
		return projectMapping;
	}*/


}
