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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.research.rdf.store.Context;
import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.config.Constants;
import com.ibm.research.rdf.store.sparql11.model.Variable;
import com.ibm.research.rdf.store.sparql11.planner.PlanNode;
import com.ibm.research.rdf.store.sparql11.sqlwriter.SQLWriterException;
import com.ibm.wala.util.collections.HashMapFactory;
import com.ibm.wala.util.collections.HashSetFactory;
import com.ibm.wala.util.collections.Pair;

public class JoinSQLTemplate extends AbstractSQLTemplate {
	PlanNode left;
	PlanNode right;
	public JoinSQLTemplate(String templateName, PlanNode planNode,
			Store store, Context ctx, STPlanWrapper wrapper, PlanNode left, PlanNode right) {
		super(templateName, store, ctx, wrapper, planNode);
		this.left = left;
		this.right = right;
		this.planNode = planNode;
		wrapper.mapPlanNode(planNode);
	}

	@Override
	Map<String, SQLMapping> populateMappings() throws SQLWriterException {
		
		varMap = new HashMap<String, Pair<String, String>>();
		
		Map<String, SQLMapping> mappings = HashMapFactory.make();
		
		List<String> qidSqlParam = new LinkedList<String>();
		qidSqlParam.add(getQIDMapping());
		SQLMapping qidMapping=new SQLMapping("sql_id", qidSqlParam, null);
		mappings.put("sql_id", qidMapping);
		
		SQLMapping pMapping=new SQLMapping("project", getProjectMapping(),null);
		mappings.put("project", pMapping);
		
		SQLMapping tMapping=new SQLMapping("target", getTargetMapping(),null);
		mappings.put("target", tMapping);
		
		List<String> filterConstraint = getFilterSQLConstraint();
		List<String> joinConstraint = getJoinConstraintMapping();

		List<String> combinedConstraints = new LinkedList<String>();
		if (filterConstraint != null) {
			combinedConstraints.addAll(filterConstraint);
		}
		if (joinConstraint != null) {
			combinedConstraints.addAll(joinConstraint);
		}
		if (!combinedConstraints.isEmpty()) {
			SQLMapping joinMapping = new SQLMapping("op_constraint", combinedConstraints, null);
			mappings.put("op_constraint", joinMapping);
		}
		
		return mappings;
	}
	
	protected void getLeftProjectMapping(List<String> projectMapping) {
		String leftSQLCte = wrapper.getPlanNodeCTE(left, false);
		Set<Variable> iriBoundVariables = wrapper.getIRIBoundVariables();
		Set<Variable> leftAvailable = left.getAvailableVariables();
		if(leftAvailable != null){
			for(Variable v : leftAvailable){
				String vPredName = wrapper.getPlanNodeVarMapping(left,v.getName());
				projectMapping.add(leftSQLCte+"."+vPredName+" AS "+v.getName());
				String vSqlType = null;
				if(!iriBoundVariables.contains(v)){
					projectMapping.add(leftSQLCte+"."+vPredName+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS+" AS "+v.getName()+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);
					vSqlType = leftSQLCte+"."+vPredName+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS;
				}
				varMap.put(v.getName(), Pair.make(leftSQLCte+"."+vPredName, vSqlType));
			}
		}
	}
	
	protected void getRightProjectMapping(List<String> projectMapping) {
		Set<Variable> operatorVariables=planNode.getOperatorsVariables();
		String rightSQLCte = wrapper.getPlanNodeCTE(right, false); 
		Set<Variable> iriBoundVariables = wrapper.getIRIBoundVariables();

		Set<Variable> rightAvailable = right.getAvailableVariables();
		if(rightAvailable != null){
			for(Variable v : rightAvailable){
				if(operatorVariables.contains(v))continue;
				String vPredName = wrapper.getPlanNodeVarMapping(right,v.getName());
				projectMapping.add(rightSQLCte+"."+vPredName+" AS "+v.getName());
				String vSqlType = null;
				if(!iriBoundVariables.contains(v)){
					projectMapping.add(rightSQLCte+"."+vPredName+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS+" AS "+v.getName()+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);
					varMap.put(v.getName(), Pair.make(rightSQLCte+"."+vPredName, vSqlType));
				}
				varMap.put(v.getName(), Pair.make(rightSQLCte+"."+vPredName, vSqlType));
			}
		}
	}
	
	List<String> getProjectMapping(){
		List<String> projectMapping = new LinkedList<String>();
		getLeftProjectMapping(projectMapping);
		getRightProjectMapping(projectMapping);
		return projectMapping;
	}
	
	List<String> getTargetMapping(){
		List<String> targetMapping = new LinkedList<String>();
		targetMapping.add(wrapper.getPlanNodeCTE(left, true));
		targetMapping.add(wrapper.getPlanNodeCTE(right, true));
		return targetMapping;
	}
	
	List<String> getJoinConstraintMapping(){
		List<String> constraintMapping = new LinkedList<String>();
		String leftSQLCte = wrapper.getPlanNodeCTE(left, false);
		String rightSQLCte = wrapper.getPlanNodeCTE(right, false); 
		Set<Variable> iriBoundVariables = wrapper.getIRIBoundVariables();

		Set<Variable> joinVariables = HashSetFactory.make();
		Set<Variable> leftAvailable = left.getAvailableVariables();
		if(leftAvailable != null){
			joinVariables.addAll(leftAvailable);
		}
		Set<Variable> rightAvailable = right.getAvailableVariables();
		if(rightAvailable != null){
			joinVariables.retainAll(rightAvailable);
		}

		for(Variable v : joinVariables){
			String vPredNameLeft = wrapper.getPlanNodeVarMapping(left,v.getName());
			String vPredNameRight = wrapper.getPlanNodeVarMapping(right,v.getName());
			String nullCheck = "(" + leftSQLCte + "." + vPredNameLeft + " IS NULL OR " + rightSQLCte + "." + vPredNameRight + " IS NULL)";
			String constraintV = "(" + leftSQLCte + "." + vPredNameLeft + " = " + rightSQLCte + "." + vPredNameRight + " OR " + nullCheck + ")";
			constraintMapping.add(constraintV);
			if(!iriBoundVariables.contains(v)){
				String constraintVTyp = "(" + leftSQLCte + "." + vPredNameLeft + Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS + " = " + rightSQLCte + "." + vPredNameRight +Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS + " OR " + nullCheck + ")";
				constraintMapping.add(constraintVTyp);
			}
		}
		return constraintMapping;
	}
	
}
