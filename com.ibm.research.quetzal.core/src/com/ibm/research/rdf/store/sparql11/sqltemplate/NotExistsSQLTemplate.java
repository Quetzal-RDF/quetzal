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
import com.ibm.research.rdf.store.sparql11.model.Expression;
import com.ibm.research.rdf.store.sparql11.model.Variable;
import com.ibm.research.rdf.store.sparql11.planner.PlanNode;
import com.ibm.research.rdf.store.sparql11.sqlwriter.FilterContext;
import com.ibm.wala.util.collections.HashMapFactory;
import com.ibm.wala.util.collections.Pair;

public class NotExistsSQLTemplate extends AbstractSQLTemplate {
	protected PlanNode left;
	protected PlanNode right;
	protected boolean isNegated;
	
	public NotExistsSQLTemplate(String templateName, PlanNode planNode,
			Store store, Context ctx, STPlanWrapper wrapper, PlanNode left, PlanNode right, boolean isNegated) {
		super(templateName, store, ctx, wrapper, planNode);
		this.left = left;
		this.right = right;
		this.planNode = planNode;
		wrapper.mapPlanNode(planNode);
		this.isNegated = isNegated;
	}

	@Override
	Map<String, SQLMapping> populateMappings() {
		Map<String, SQLMapping> mappings = HashMapFactory.make();
		varMap = HashMapFactory.make();
		List<String> qidSqlParam = new LinkedList<String>();
		qidSqlParam.add(getQIDMapping());
		SQLMapping qidMapping=new SQLMapping("sql_id", qidSqlParam,null);
		mappings.put("sql_id", qidMapping);
		
		
		SQLMapping pMapping=new SQLMapping("left_project", getLeftProjectMapping(),null);
		mappings.put("left_project", pMapping);
		
		SQLMapping tMapping=new SQLMapping("left_target", getLeftTargetMapping(),null);
		mappings.put("left_target", tMapping);		
			
			
		SQLMapping filterConstraintsMapping=new SQLMapping("filter_constraints", getFilterConstraints(), null);
		mappings.put("filter_constraints", filterConstraintsMapping);					
		
		return mappings;
	}
	
	List<String> getLeftProjectMapping(){
		List<String> projectMapping = new LinkedList<String>();
		Set<Variable> notExistAvailableVariables=planNode.getAvailableVariables();
		String leftSQLCte = wrapper.getPlanNodeCTE(left, false); 
		Set<Variable> leftAvailable = left.getAvailableVariables();
		Set<Variable> iriBoundVariables = wrapper.getIRIBoundVariables();
		for(Variable v : notExistAvailableVariables){
			if(leftAvailable != null){
				if(leftAvailable.contains(v)){
					String vPredName = wrapper.getPlanNodeVarMapping(left,v.getName());
					projectMapping.add(leftSQLCte+"."+ vPredName +" AS "+v.getName());
					if(!iriBoundVariables.contains(v)){
						projectMapping.add(leftSQLCte+"."+vPredName+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS+" AS "+v.getName()+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);
					}
				}
			}
		}
		return projectMapping;
	}
	
	List<String> getFilterConstraints() {
		List<String> filterConstraints = new LinkedList<String>();
		List<String> notExistsConstraint = new LinkedList<String>();
		// Not exists constraints can either be in terms of shared variables from the left hand side to the right, or in terms of actual explicit filters
		// that specify the relationship between the constraints
		Set<Variable> notExistVariables = planNode.getOperatorsVariables();
		for(Variable v : notExistVariables){
			notExistsConstraint.add(wrapper.getPlanNodeCTE(left, false)+"."+v.getName()+" = "+wrapper.getPlanNodeCTE(right, false)+"."+wrapper.getPlanNodeVarMapping(right,v.getName()));
		}
		
		// construct a variable map to map filter names
		Set<Variable> rightVariables = right.getAvailableVariables();
		
		Set<Variable> leftVariables = left.getAvailableVariables();
		
		Map<String, Pair<String, String>> varMap = HashMapFactory.make();
		Set<Variable> iriBoundVariables =  wrapper.getIRIBoundVariables();
		List<String> rightTarget = getRightTargetMapping();

		for (Variable v : rightVariables) {
			String varName = wrapper.getPlanNodeCTE(right, false) + "." + wrapper.getPlanNodeVarMapping(right,v.getName());
			String varType = null;
			if(!iriBoundVariables.contains(v)){
				varType = varName+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS;
			}
			varMap.put(v.getName(), Pair.make(varName, varType));
		}

		for (Variable v : leftVariables) {
			String varName = wrapper.getPlanNodeCTE(left, false) + "." + wrapper.getPlanNodeVarMapping(left,v.getName());
			String varType = null;
			if(!iriBoundVariables.contains(v)){
				varType = varName+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS;
			}
			varMap.put(v.getName(), Pair.make(varName, varType));
		}

		try {		
			FilterContext context = new FilterContext(varMap, wrapper.getPropertyValueTypes(), notExistsConstraint, rightTarget, isNegated, planNode);
	
			Set<Variable> available = new HashSet<Variable>(right.getAvailableVariables());
			available.addAll(left.getAvailableVariables());
			if (right.getApplicableFilters(wrapper.getPlan(), available) != null) {
				for (Expression e : right.getApplicableFilters(wrapper.getPlan(), available)) {
					assert available.containsAll(e.gatherVariables());
					String eSql = expGenerator.getSQLForExpression(e, context, store);
					notExistsConstraint.add(eSql);
				}
			}
			
		
			if (planNode.getFilters() != null) {
				for (Expression e : planNode.getFilters()) {
					String eSql = expGenerator.getSQLForExpression(e, context, store);
					filterConstraints.add(eSql);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		
		assert !filterConstraints.isEmpty() : planNode.getFilters();
		return filterConstraints;
	}
	
	
	List<String> getLeftTargetMapping(){
		List<String> targetMapping = new LinkedList<String>();
		targetMapping.add(wrapper.getPlanNodeCTE(left, true));
		return targetMapping;
	}
	
	List<String> getRightTargetMapping(){
		List<String> targetMapping = new LinkedList<String>(); 
		targetMapping.add(wrapper.getPlanNodeCTE(right, true));
		return targetMapping;
	}
}
