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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.ibm.research.rdf.store.Context;
import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.sparql11.model.ServicePattern;
import com.ibm.research.rdf.store.sparql11.model.Variable;
import com.ibm.research.rdf.store.sparql11.planner.PlanNode;
import com.ibm.wala.util.collections.HashSetFactory;

public class ServiceSQLTemplate extends JoinNonSchemaTablesSQLTemplate {
	public ServiceSQLTemplate(String templateName, PlanNode planNode,
			Store store, Context ctx, STPlanWrapper wrapper) {
		super(templateName, store, ctx, wrapper, planNode);
		this.planNode = planNode;
		wrapper.mapPlanNode(planNode);
	}

	@Override
	Set<SQLMapping> populateMappings() {		
		HashSet<SQLMapping> mappings = new HashSet<SQLMapping>();

		ServicePattern sp = (ServicePattern) planNode.getPattern();
		
		mappings.add(new SQLMapping("sql_id",wrapper.getPlanNodeId(planNode), null));
		String queryText = sp.getQueryText();
		mappings.add(new SQLMapping("queryText", "&query=" + queryText, null));
		assert sp.getService().isIRI();
		String service = sp.getService().getIRI().toString();
		mappings.add(new SQLMapping("service", service, null));

		Set<Variable> vars = sp.getPattern().gatherVariables();
		Set<String> cols = HashSetFactory.make();
		Set<String> firstProjectCols = HashSetFactory.make();
		
		for (Variable v : vars) {
			firstProjectCols.add(v.getName());
			cols.add(v.getName() + " VARCHAR (128) PATH 'declare namespace x=\"http://www.w3.org/2005/sparql-results#\"; "
					+ "xs:string(./x:binding[./@name=\"" + v.getName() + "\"])'" );
		}
		
		List<Variable> literalVars = getAllLiteralVars(vars);
		Set<String> dtCols = HashSetFactory.make();
		List<String> dtConstraints = new LinkedList<String>();

		for (Variable v : literalVars) {
			String vt = v.getName() + "_TYP";
			firstProjectCols.add(vt);
			cols.add(v.getName() + "_TYP" + " VARCHAR (128) PATH 'declare namespace x=\"http://www.w3.org/2005/sparql-results#\"; "
					+ "./x:binding[./@name=\"" + v.getName() + "\"]/x:literal/@datatype'");
			dtConstraints.add("(" + vt + " IS NOT NULL AND " + vt + " = " + "DATATYPE_NAME) OR (" + vt + " IS NULL AND DATATYPE_NAME='SIMPLE_LITERAL_ID')");
		}
		
		Set<String> secondProjectCols = HashSetFactory.make();
		for (String s : firstProjectCols) {
			secondProjectCols.add(wrapper.getPlanNodeCTE(planNode, false) + "_TMP." + s);
		}
		
		secondProjectCols.addAll(getProjectedVariablesFromPredecessor());
		
		mappings.add(new SQLMapping("dtCols", dtCols, null));
		mappings.add(new SQLMapping("dtConstraints", dtConstraints, null));
		mappings.add(new SQLMapping("dtTable", this.store.getDataTypeTable(), null));
		
		mappings.add(new SQLMapping("cols", cols, null));
		mappings.add(new SQLMapping("firstProjectCols", firstProjectCols, null));
		mappings.add(new SQLMapping("secondProjectCols", secondProjectCols, null));


		List<String> targetList = getTargetMapping();
		SQLMapping tMapping = new SQLMapping("target", targetList, null);
		mappings.add(tMapping);
		
		if (pred != null) {
			String leftSQLCte = wrapper.getPlanNodeCTE(pred, false);
			String rightSQLCte = wrapper.getPlanNodeCTE(planNode, false) + "_TMP"; 
			List<String> joinConstraints = getJoinConstraintMapping(leftSQLCte, rightSQLCte);
			SQLMapping joinMapping = new SQLMapping("join_constraint", joinConstraints, null);
			mappings.add(joinMapping);
		}
		
		return mappings;
	}	
	
	public List<String> getProjectedVariablesFromPredecessor() {
		if (pred == null) {
			return Collections.emptyList();
		}
		List<String> projected = new LinkedList<String>();
		Set<Variable> vars = pred.getAvailableVariables();
		String leftSQLCte = wrapper.getPlanNodeCTE(pred, false);
		Set<Variable> joinVars = getJoinVariables();
		
		for(Variable v : vars) {
			if (joinVars.contains(v)) {
				continue;
			}
			String vPredNameLeft = wrapper.getPlanNodeVarMapping(pred,v.getName());
			projected.add(leftSQLCte + "." + vPredNameLeft);
			if (!wrapper.iriBoundVariablesInQuery.contains(v)) {
				projected.add(leftSQLCte + "." + vPredNameLeft + "_TYP");
			}

		}
		return projected;
	}
	private List<Variable> getAllLiteralVars(Set<Variable> vars) {
		List<Variable> l = new LinkedList<Variable>();
		for (Variable v : vars) {
			if (!wrapper.iriBoundVariablesInQuery.contains(v)) {
				l.add(v);
			}
		}
		return l;
	}
}
