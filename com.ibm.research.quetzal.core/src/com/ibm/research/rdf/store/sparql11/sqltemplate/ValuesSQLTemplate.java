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

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

import com.ibm.research.rdf.store.Context;
import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.config.Constants;
import com.ibm.research.rdf.store.sparql11.model.Expression;
import com.ibm.research.rdf.store.sparql11.model.Variable;
import com.ibm.research.rdf.store.sparql11.planner.PlanNode;
import com.ibm.research.rdf.store.sparql11.sqlwriter.FilterContext;
import com.ibm.research.rdf.store.sparql11.sqlwriter.SQLWriterException;
import com.ibm.wala.util.collections.HashMapFactory;

public class ValuesSQLTemplate extends JoinNonSchemaTablesSQLTemplate {
	public static void main(String[] args) {
		 String st = "group sql;\n"
		 		+ "values(values, values_project,  store_name) ::= << \n"
				+ "( select <values_project:{ v |  struct_col.col<i> AS <v>} ; separator=\",\"> from (select storename from <store_name> limit 1) dummy lateral view explode(array(<values:{struct(<it; separator=\",\">)};separator=\",\">)) LATTEMP_<store_name> AS struct_col  ) AS TEMP"
			 	//+" (select VALUES <values:{(<it; separator=\",\">)};separator=\",\">) AS TEMP(<values_project; separator=\",\">) \n"
				+">>";
		 System.out.println("template text: "+st);
		 BufferedReader configReader = new BufferedReader(new StringReader(st));
         StringTemplateGroup group = new StringTemplateGroup(configReader);
		 StringTemplate template =  group.getInstanceOf("values");//new StringTemplate(st);
		 System.out.println("template: "+template.toString());
		 template.setAttribute("store_name", "uobm1a");
		 List<String> projects = new LinkedList<String>();
		 projects.add("column1");
		 projects.add("column2");
		 template.setAttribute("values_project",projects);
		 List< List<String>> values = new LinkedList<List<String>>();
		 List<String> value = new LinkedList<String>();
		 value.add("'test1'");
		 value.add("'test2'");
		 values.add(value);
		 value = new LinkedList<String>();
		 value.add("'test3'");
		 value.add("'test4'");
		 values.add(value);
		 
		 template.setAttribute("values", values);
		 System.out.println("string: "+template.toString());
	}
	
	
	
	public ValuesSQLTemplate(List<String> templateName, PlanNode planNode,
			Store store, Context ctx, STPlanWrapper wrapper) {
		super(templateName, store, ctx, wrapper, planNode);
		this.planNode = planNode;
		wrapper.mapPlanNode(planNode);
	}

	@Override
	Map<String, SQLMapping> populateMappings() {
		Map<String, SQLMapping> mappings = HashMapFactory.make();
		
		List<String> qidSqlParam = new LinkedList<String>();
		qidSqlParam.add(getQIDMapping());
		SQLMapping qidMapping=new SQLMapping("sql_id", qidSqlParam,null);
		mappings.put("sql_id", qidMapping);
	
		List<String> valuesProjectList = getValuesProjectList();			
		SQLMapping vpMapping=new SQLMapping("values_project", valuesProjectList, null);
		mappings.put("values_project", vpMapping);

		List<List<String>> valuesList = getValuesList();
		SQLMapping vMapping=new SQLMapping("values", valuesList, null);
		mappings.put("values", vMapping);
		
		List<String> projectList = getProjectList();			
		SQLMapping pMapping=new SQLMapping("project", projectList, null);
		mappings.put("project", pMapping);	
		
		List<String> targetList = getTargetMapping();
		SQLMapping tMapping = new SQLMapping("target", targetList, null);
		mappings.put("target", tMapping);
		
		if (pred != null) {
			String leftSQLCte = wrapper.getPlanNodeCTE(pred, false);
			String rightSQLCte = "TEMP"; 
	
			List<String> joinConstraints = getJoinConstraintMapping(leftSQLCte, rightSQLCte);
			SQLMapping joinMapping = new SQLMapping("join_constraint", joinConstraints, null);
			mappings.put("join_constraint", joinMapping);
		}
		String store_name = store.getStoreName();
		SQLMapping storeNameMapping = new SQLMapping("store_name",store_name, null);
		mappings.put("store_name", storeNameMapping);
	
		return mappings;
	}

	List<String> getProjectList(){
		List<String> projectList = new LinkedList<String>();
		

		for (String s: getValuesProjectList()) {
			projectList.add("TEMP." + s + " AS " + s);
		}
		
		List<String> bindMap;
		try {
			bindMap = mapBindForProject(varMap);
			projectList.addAll(bindMap);

		} catch (SQLWriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		if (pred != null) {
			String leftSQLCte = wrapper.getPlanNodeCTE(pred, false);
			Set<Variable> valueVars = new HashSet(planNode.getValues().getVariables());;
	
			for (Variable v : pred.getAvailableVariables()) {
				if (planNode.getRequiredVariables().contains(v)) {
					continue;
				}
				if (valueVars.contains(v)) {
					continue;
				}
				String vPredName = wrapper.getPlanNodeVarMapping(pred,v.getName());
				projectList.add(leftSQLCte+"."+vPredName+" AS "+v.getName());
				if(!wrapper.getIRIBoundVariables().contains(v)){
					projectList.add(leftSQLCte+"."+vPredName+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS+" AS "+v.getName()+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);
				}
			}		
		}
		return projectList;
	}

	private List<String> getValuesProjectList() {
		List<String> projectList = new LinkedList<String>();

		for (Variable v : planNode.getValues().getVariables()){
			projectList.add(v.getName());
			if (!wrapper.getIRIBoundVariables().contains(v)) {
				projectList.add(v.getName() + Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);
			}
		}

		return projectList;
	}
	
	List<List<String>> getValuesList()  {
		List<List<String>> ret = new LinkedList<List<String>>();
		try {
			varMap = HashMapFactory.make();
			
			int k = 0;
			List<Integer> variableIndexesNotIRIBound = new LinkedList<Integer>();
			for (Variable v : planNode.getValues().getVariables()) {
				if (!wrapper.getIRIBoundVariables().contains(v)) {
					variableIndexesNotIRIBound.add(k);
				}
				k++;
			}
			

			for (int i = 0; i < planNode.getValues().getExpressions().size(); i++) {
				List<String> row = new LinkedList<String>();
				List<Expression> listOfValues = planNode.getValues().getExpressions().get(i);
				
				for (int j = 0; j < listOfValues.size(); j++) {
					Expression e = listOfValues.get(j);
					String eSql = expGenerator.getSQLForExpression(e, new FilterContext(varMap, wrapper.getPropertyValueTypes(), planNode), store);
					row.add(eSql);
					if (variableIndexesNotIRIBound.contains(j)) {
						row.add(e.getReturnType().toString());
					}
 				}
				ret.add(row);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	

}
