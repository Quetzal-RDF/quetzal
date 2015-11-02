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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.research.rdf.store.Context;
import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.Store.Db2Type;
import com.ibm.research.rdf.store.Store.PredicateTable;
import com.ibm.research.rdf.store.config.Constants;
import com.ibm.research.rdf.store.runtime.service.types.TypeMap;
import com.ibm.research.rdf.store.sparql11.model.Expression;
import com.ibm.research.rdf.store.sparql11.model.IRI;
import com.ibm.research.rdf.store.sparql11.model.PropertyTerm;
import com.ibm.research.rdf.store.sparql11.model.QueryTriple;
import com.ibm.research.rdf.store.sparql11.model.QueryTripleTerm;
import com.ibm.research.rdf.store.sparql11.model.Variable;
import com.ibm.research.rdf.store.sparql11.planner.AccessMethod;
import com.ibm.research.rdf.store.sparql11.planner.AccessMethodType;
import com.ibm.research.rdf.store.sparql11.planner.PlanNode;
import com.ibm.research.rdf.store.sparql11.sqlwriter.FilterContext;
import com.ibm.research.rdf.store.sparql11.sqlwriter.MonitoredStringBuffer;
import com.ibm.research.rdf.store.sparql11.sqlwriter.SQLWriterException;
import com.ibm.wala.util.collections.HashMapFactory;
import com.ibm.wala.util.collections.Pair;

/**
 * @author mbornea
 *
 */
public class StarBothSQLTemplate extends SimplePatternBothSQLTemplate {
	
	HashMap<QueryTriple,Integer> tripleToIdMap;
	Set<Variable> projectedInSecondary = null;

	public StarBothSQLTemplate(String templateName, PlanNode planNode, Store store, Context ctx, STPlanWrapper wrapper) {
		super(templateName, store, ctx, wrapper, planNode);
		tripleToIdMap = mapPredicatesToUniqueIds();
	}

	@Override
	Map<String, SQLMapping> populateMappings() throws SQLWriterException {
		
		projectedInPrimary = new HashSet<Variable>();
		projectedInSecondary = new HashSet<Variable>();
		varMap = new HashMap<String, Pair<String, String>>();
		sVarMap = new HashMap<String, Pair<String, String>>();
		
		Map<String, SQLMapping> mappings = HashMapFactory.make();
		
		List<String> qidSqlParam = new LinkedList<String>();
		qidSqlParam.add(getQIDMapping());
		SQLMapping qidSqlParams=new SQLMapping("sql_id", qidSqlParam,null);
		mappings.put("sql_id", qidSqlParams);
		
		SQLMapping tMapping=new SQLMapping("target", getTargetSQLClause(),null);
		mappings.put("target", tMapping);
		
		SQLMapping eMapping=new SQLMapping("entry_constraint", getEntrySQLConstraint(),null);
		mappings.put("entry_constraint", eMapping);
		
		SQLMapping vMapping=new SQLMapping("val_constraint", getValueSQLConstraint(),null);
		mappings.put("val_constraint", vMapping);
		
		SQLMapping predicateMapping=new SQLMapping("predicate_constraint", getPropSQLConstraint(),null);
		mappings.put("predicate_constraint", predicateMapping);
		
		List<String> sep = new LinkedList<String>();
		sep.add(" AND ");
		SQLMapping sepMapping=new SQLMapping("sep",sep,null);
		mappings.put("sep", sepMapping);
		
		List<String> projectSqlParams = getProjectedSQLClause();
		if(projectSqlParams.size()==0)projectSqlParams.add("*");
		SQLMapping pMapping=new SQLMapping("project", projectSqlParams,null);
		mappings.put("project", pMapping);
		
		List<String> filterConstraint = getFilterSQLConstraint();
		SQLMapping filterMapping = new SQLMapping("filter_constraint", filterConstraint,null);
		mappings.put("filter_constraint", filterMapping);
	
		List<String> projectSqlParamsForSecondary = getProjectedSQLClauseForSecondary();
		if(projectSqlParamsForSecondary.size()==0)projectSqlParamsForSecondary.add("*");
		SQLMapping pMappingForSecondary=new SQLMapping("s_project", projectSqlParamsForSecondary,null);
		mappings.put("s_project", pMappingForSecondary);
	
		SQLMapping tMappingForSecondary=new SQLMapping("s_target", getTargetSQLClauseForSecondary(),null);
		mappings.put("s_target", tMappingForSecondary);

		SQLMapping vMappingForSecondary=new SQLMapping("s_val_constraint",getValueSQLConstraintForSecondary(),null);
		mappings.put("s_val_constraint", vMappingForSecondary);

		List<String> filterConstraintForSecondary = getFilterSQLConstraintForSecondary();
		SQLMapping filterMappingForSecondary = new SQLMapping("s_filter_constraint", filterConstraintForSecondary,null);
		mappings.put("s_filter_constraint", filterMappingForSecondary);
		
		List<String> multivaluedIds = getMultivaluedIds();
		SQLMapping multivaluedIdsMapping = new SQLMapping("multivalued_ids", multivaluedIds,null);
		mappings.put("multivalued_ids",multivaluedIdsMapping);
				
		return mappings;
	}


	protected List<String> mapEntryForProject(){
		List<String> entrySqlToSparql =  new LinkedList<String>();
		if (planNode.getOperatorsVariables().iterator().hasNext()) {
			//The star entry is always a variable
			//There is only one star term
			Variable entryTermVar = planNode.getOperatorsVariables().iterator().next(); 
			if(projectedInPrimary.contains(entryTermVar))return null;
			projectedInPrimary.add(entryTermVar);
			boolean entryHasSqlType = false;
			if(!AccessMethodType.isDirectAccess(planNode.getMethod().getType())){
				entryHasSqlType = true;
			}
			entrySqlToSparql.add(Constants.NAME_COLUMN_ENTRY+" AS "+entryTermVar.getName());
			String entryType = null;
			Set<Variable> iriBoundVariables = wrapper.getIRIBoundVariables();
			if(!iriBoundVariables.contains(entryTermVar)){
				entryType =(entryHasSqlType)?Constants.NAME_COLUMN_PREFIX_TYPE:new Short(TypeMap.IRI_ID).toString();
				entrySqlToSparql.add(entryType+" AS "+entryTermVar.getName()+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);			
			}
		}
		return entrySqlToSparql;
	}	
		
	protected List<String> mapValForProject(){
		Set<QueryTriple> qtSet = new HashSet<QueryTriple>();
		qtSet.addAll(planNode.starTriples);
		if (planNode.starOptionalTriples != null) qtSet.addAll(planNode.starOptionalTriples);
		QueryTripleTerm valTerm = null;
		List<String> valSqlToSparql =  new LinkedList<String>();
		PredicateTable predicateTable = (AccessMethodType.isDirectAccess(planNode.getMethod().getType()))?
				store.getDirectPredicates() : store.getReversePredicates();
		Set<Variable> iriBoundVariables = wrapper.getIRIBoundVariables();
		for(QueryTriple qt : qtSet){
			String predicate = qt.getPredicate().toString();
			boolean valHasSqlType = false;
			if(AccessMethodType.isDirectAccess(planNode.getMethod().getType())){
				valTerm = qt.getObject();
				valHasSqlType = true;
			}
			else{
				valTerm = qt.getSubject();
			}
			if(predicateTable.isOneToOne(predicate)){
				if(valTerm.isVariable()){
					Variable valueVariable = valTerm.getVariable();
					Db2Type pType=predicateTable.getType(qt.getPredicate().getIRI().getValue());
					wrapper.addProperyValueType(valueVariable.getName(), pType);
					if(projectedInPrimary.contains(valueVariable))continue;
					projectedInPrimary.add(valueVariable);
					String pValue = null;
					String pValueType = null;
					if (planNode.starOptionalTriples != null && planNode.starOptionalTriples.contains(qt) ){
						pValue = "CASE WHEN "+hashColumnExpression(Constants.NAME_COLUMN_PREFIX_PREDICATE,predicate)+"='"+predicate+
								"' THEN " + hashColumnExpression(Constants.NAME_COLUMN_PREFIX_VALUE,predicate)+" ELSE null END";
						if(!iriBoundVariables.contains(valueVariable)){
							pValueType = (valHasSqlType) ? "CASE WHEN "+hashColumnExpression(Constants.NAME_COLUMN_PREFIX_PREDICATE,predicate)+"='"+predicate+
									"' THEN " + hashColumnExpression(Constants.NAME_COLUMN_PREFIX_TYPE,predicate)+" ELSE null END" : new Short(TypeMap.IRI_ID).toString();
						}
					}
					else{
						pValue =hashColumnExpression(Constants.NAME_COLUMN_PREFIX_VALUE,predicate);
						if(!iriBoundVariables.contains(valueVariable)){
							pValueType = (valHasSqlType) ? hashColumnExpression(Constants.NAME_COLUMN_PREFIX_TYPE,predicate) : new Short(TypeMap.IRI_ID).toString();
						}
					}
					valSqlToSparql.add(pValue+" AS "+valueVariable.getName());
					if(pValueType!=null)
						valSqlToSparql.add(pValueType+" AS "+valueVariable.getName()+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);
				}				
			}
			else{
				valSqlToSparql.add(hashColumnExpression(Constants.NAME_COLUMN_PREFIX_VALUE,predicate)+" AS "+"VAL"+tripleToIdMap.get(qt));
				if(valTerm.isVariable() && !iriBoundVariables.contains(valTerm.getVariable())){
					valSqlToSparql.add(hashColumnExpression(Constants.NAME_COLUMN_PREFIX_TYPE,predicate)+" AS "+"TYP"+tripleToIdMap.get(qt));
				}
			}
		}
		
		return valSqlToSparql;
	}
	
	protected List<String> mapExternalVarForProject(){		
		List<String> map = new LinkedList<String>();
		PlanNode predecessor = planNode.getPredecessor(wrapper.getPlan());
		Set<QueryTriple> qtSet = new HashSet<QueryTriple>();
		qtSet.addAll(planNode.starTriples);
		if (planNode.starOptionalTriples != null) qtSet.addAll(planNode.starOptionalTriples);
		if(predecessor!=null){
			Set<Variable> predecessorVars = predecessor.getAvailableVariables();
			if(predecessorVars != null){

				Set<Variable> iriBoundVariables = wrapper.getIRIBoundVariables();
				for(Variable v : predecessorVars){
					if(projectedInPrimary.contains(v))continue;
					projectedInPrimary.add(v);
					String vPredName = wrapper.getPlanNodeVarMapping(predecessor,v.getName());
					String vTypePredName = null;
					map.add(vPredName+" AS "+v.getName());
					if(!iriBoundVariables.contains(new Variable(vPredName))){
						vTypePredName = wrapper.getPlanNodeVarMapping(predecessor,v.getName()) + Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS;
						map.add(vTypePredName+" AS "+v.getName() + Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);
					}
					varMap.put(v.getName(), Pair.make(vPredName, vTypePredName));
				}
			}
		}
		return map;
	}
	
	// In the secondary, we don't need to look at renamed variables in the predecessor
	// The renaming was already considered in the Primary table 
	private List<String> mapExternalVarForProjectInSecondary(){		
		List<String> map = new LinkedList<String>();
		PlanNode predecessor = planNode.getPredecessor(wrapper.getPlan());
		Set<QueryTriple> qtSet = new HashSet<QueryTriple>();
		qtSet.addAll(planNode.starTriples);
		if (planNode.starOptionalTriples != null) qtSet.addAll(planNode.starOptionalTriples);
		if(predecessor!=null){
			Set<Variable> predecessorVars = predecessor.getAvailableVariables();
			if(predecessorVars != null){
				Set<Variable> iriBoundVariables = wrapper.getIRIBoundVariables();
				for(Variable v : predecessorVars){
					if(projectedInSecondary.contains(v))continue;
					projectedInSecondary.add(v);
					String vPredName = wrapper.getPlanNodeVarMapping(predecessor,v.getName());
					String vType = null;
					map.add(v.getName()+" AS "+v.getName());
					if(!iriBoundVariables.contains(new Variable(vPredName))){
						vType = v.getName() + Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS;
						map.add(vType);
					}
					sVarMap.put(v.getName(), Pair.make(v.getName(), vType));
				}
			}
		}
		return map;
	}
		
	private List<String> getTargetSQLClause(){
		List<String> targetSQLClause = new LinkedList<String>();
		if(AccessMethodType.isDirectAccess(planNode.getMethod().getType())){
			targetSQLClause.add(store.getDirectPrimary()+ " AS T");
		}
		else{
			targetSQLClause.add(store.getReversePrimary()+" AS T");
		}
		PlanNode predecessor = planNode.getPredecessor(wrapper.getPlan());
		if(predecessor!=null){
			targetSQLClause.add(wrapper.getPlanNodeCTE(predecessor, false));
		}
		return targetSQLClause;		
	}
	
	private List<String> getEntrySQLConstraint(){
		List<String> entrySQLConstraint = new LinkedList<String>();
		AccessMethod am = planNode.getMethod();

		boolean hasSqlType = false;
		if(!AccessMethodType.isDirectAccess(am.getType())){
			hasSqlType = true;
		}

		if (planNode.getOperatorsVariables().iterator().hasNext()) {
			//The star entry is always a variable
			//There is only one star term
			Variable entryTermVar = planNode.getOperatorsVariables().iterator().next(); 

			PlanNode predecessor = planNode.getPredecessor(wrapper.getPlan());
			boolean entryHasConstraintWithPredecessor = false;
			if(predecessor!=null ){
				entryHasConstraintWithPredecessor = super.getPredecessorConstraint(entrySQLConstraint, entryTermVar, predecessor, tTableColumnPrefix+Constants.NAME_COLUMN_ENTRY, tTableColumnPrefix+Constants.NAME_COLUMN_PREFIX_TYPE, hasSqlType);
			}
			if(!entryHasConstraintWithPredecessor){
				if(varMap.containsKey(entryTermVar.getName())){
					entrySQLConstraint.add(tTableColumnPrefix+Constants.NAME_COLUMN_ENTRY+ " = "+ varMap.get(entryTermVar.getName()).fst);
				}
			}
			String entryType = null;
			String sEntryType = null;
			if(hasSqlType && !wrapper.getIRIBoundVariables().contains(entryTermVar)){
				entryType = tTableColumnPrefix+Constants.NAME_COLUMN_PREFIX_TYPE;
				sEntryType = entryTermVar.getName()+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS;
			}
			varMap.put(entryTermVar.getName(), Pair.make(tTableColumnPrefix+Constants.NAME_COLUMN_ENTRY, entryType));
			// The entry is mapped in the secondary with its variable name
			sVarMap.put(entryTermVar.getName(), Pair.make(entryTermVar.getName(), sEntryType));
		} else {
			QueryTriple t = planNode.starTriples.iterator().next();
			QueryTripleTerm val = AccessMethodType.isDirectAccess(am.getType())? t.getSubject(): t.getObject();
			super.addConstantEntrySQLConstraint(val, entrySQLConstraint, hasSqlType, tTableColumnPrefix+Constants.NAME_COLUMN_ENTRY);
		}

		return entrySQLConstraint;
	}

	private List<String> getValueSQLConstraint(){
		List<String> valueSQLConstraint = new LinkedList<String>();
		PredicateTable predicateTable = (AccessMethodType.isDirectAccess(planNode.getMethod().getType()))?
				store.getDirectPredicates() : store.getReversePredicates();
		for(QueryTriple qt : planNode.starTriples){
		
			if(!predicateTable.isOneToOne(qt.getPredicate().toString()))continue;
			boolean hasSqlType = false;
			QueryTripleTerm valueTerm = null;
			if(AccessMethodType.isDirectAccess(planNode.getMethod().getType())){
				valueTerm = qt.getObject();
				hasSqlType = true;
			}
			else{
				valueTerm = qt.getSubject();
			}
			String predicate = qt.getPredicate().getIRI().getValue();
			if(valueTerm.isVariable()){
				Variable valueVariable = valueTerm.getVariable();
				PlanNode predecessor = planNode.getPredecessor(wrapper.getPlan());
				boolean valueHasConstraintWitPredecessor = false;
				if(predecessor != null ){
					Set<Variable> availableVariables = predecessor.getAvailableVariables();
					if(availableVariables != null){

						if(availableVariables.contains(valueVariable)){
							String valuePredName = wrapper.getPlanNodeVarMapping(predecessor,valueVariable.getName());
							valueSQLConstraint.add(hashColumnExpression(Constants.NAME_COLUMN_PREFIX_VALUE,predicate)+ 
									" = " +wrapper.getPlanNodeCTE(predecessor, false)+ "." + valuePredName);
							if(hasSqlType && !wrapper.getIRIBoundVariables().contains(valueVariable)){
								valueSQLConstraint.add(hashColumnExpression(Constants.NAME_COLUMN_PREFIX_TYPE,predicate)+ 
										" = " + wrapper.getPlanNodeCTE(predecessor, false) + "." + valuePredName + Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);
							}
							valueHasConstraintWitPredecessor = true;
						}
					}
				}
				if(!valueHasConstraintWitPredecessor){
					if(varMap.containsKey(valueVariable.getName())){
						valueSQLConstraint.add(hashColumnExpression(Constants.NAME_COLUMN_PREFIX_VALUE,predicate)+ "=" + varMap.get(valueVariable.getName()).fst);
					}
				}
				String pValueType = null;
				if(hasSqlType && !wrapper.getIRIBoundVariables().contains(valueVariable)){
					pValueType = hashColumnExpression(Constants.NAME_COLUMN_PREFIX_TYPE,predicate);
				}
				varMap.put(valueVariable.getName(), Pair.make(hashColumnExpression(Constants.NAME_COLUMN_PREFIX_VALUE,predicate), pValueType));
			}
			else{
				valueSQLConstraint.add(hashColumnExpression(Constants.NAME_COLUMN_PREFIX_VALUE,predicate)+" = '"+valueTerm.toSqlDataString()+"'");
			}
		}
		return valueSQLConstraint;
	}
	
	private List<String> getPropSQLConstraint(){
		List<String> propSQLConstraint = new LinkedList<String>();
		for(QueryTriple qt : planNode.starTriples){
			// TODO [Property Path]: Double check with Mihaela that it is fine for propTerm.toSqlDataString() to return null for complex path (ie., same behavior as variable)
			PropertyTerm propTerm = qt.getPredicate();
			propSQLConstraint.add(hashColumnExpression(Constants.NAME_COLUMN_PREFIX_PREDICATE,propTerm.toString())+" = '"+propTerm.toSqlDataString()+"'");
		}
		return propSQLConstraint;
	}
	
	protected boolean isApplicableInSecondaryOnly(Variable v) {
		Set<Variable> multivaluedVariables = getMultivaluedVariables();

		return multivaluedVariables.contains(v);
	}

	protected List<String> getFilterSQLConstraint() throws SQLWriterException{
		List<String> filterSQLConstraint = new LinkedList<String>();
		for(Expression e :planNode.getApplicableFilters(wrapper.getPlan())){
			if( ! planNode.getAvailableVariables().containsAll(e.gatherVariables())) continue ;			
			if(applyFilterInPrimary(e)){
				String eSql = expGenerator.getSQLForExpression(e, new FilterContext(varMap, wrapper.getPropertyValueTypes(), planNode), store);
				filterSQLConstraint.add(eSql);
			}
		}
		return filterSQLConstraint;
	}
	
	private List<String> getFilterSQLConstraintForSecondary() throws SQLWriterException{
		List<String> filterSQLConstraint = new LinkedList<String>();
		for(Expression e :planNode.getApplicableFilters(wrapper.getPlan())){
			if( ! planNode.getAvailableVariables().containsAll(e.gatherVariables()))continue ;			
			
			if(!applyFilterInPrimary(e)){
				String eSql = expGenerator.getSQLForExpression(e, new FilterContext(sVarMap, wrapper.getPropertyValueTypes(), planNode), store);
				filterSQLConstraint.add(eSql);
			}
		}
		return filterSQLConstraint;
	}
	
	
	private List<String> getProjectedSQLClauseForSecondary() throws SQLWriterException {
		List<String> projectSQLClause = new LinkedList<String>();
		List<String> entryMap = mapEntityForProjectInSecondary();
		List<String> valMap = mapValForProjectInSecondary();
		List<String> eVarMap = mapExternalVarForProjectInSecondary();
		if(entryMap!=null)projectSQLClause.addAll(entryMap);
		if(valMap!=null)projectSQLClause.addAll(valMap);
		if(eVarMap!=null)projectSQLClause.addAll(eVarMap);
		if (!applyBindForPrimary()) {
			List<String> bindMap = mapBindForProjectInSecondary();
			if (bindMap != null) {
				projectSQLClause.addAll(bindMap);
			}
		}
		return projectSQLClause;
	}

	private List<String> mapEntityForProjectInSecondary(){
		List<String> entrySqlToSparql =  new LinkedList<String>();

		if (planNode.getOperatorsVariables().iterator().hasNext()) {
			//The star entry is always a variable
			//There is only one star term
			Variable entryTermVar = planNode.getOperatorsVariables().iterator().next(); 
			if(projectedInSecondary.contains(entryTermVar)) return null;
			projectedInSecondary.add(entryTermVar);
			if (sVarMap.containsKey(entryTermVar.getName())) {
				Pair<String, String> pair = sVarMap.get(entryTermVar.getName());
				if (pair.snd != null) {			// KAVITHA: snd is a type is sometimes null if the variable is uri bound
					entrySqlToSparql.add(pair.snd+" AS "+pair.snd);
				}
			}
			entrySqlToSparql.add(entryTermVar.getName()+" AS "+entryTermVar.getName());
		}

		return entrySqlToSparql;
	}
	
	private List<String> mapValForProjectInSecondary(){
		Set<QueryTriple> qtSet = new HashSet<QueryTriple>();
		qtSet.addAll(planNode.starTriples);
		if (planNode.starOptionalTriples != null) qtSet.addAll(planNode.starOptionalTriples);
		QueryTripleTerm valTerm = null;
		List<String> valSqlToSparql =  new LinkedList<String>();
		PredicateTable predicateTable = (AccessMethodType.isDirectAccess(planNode.getMethod().getType()))?store.getDirectPredicates() : store.getReversePredicates();
		Set<Variable> iriBoundVariables = wrapper.getIRIBoundVariables();
		for(QueryTriple qt : qtSet){
			String predicate = qt.getPredicate().toString();
			boolean valHasSqlType = false;
			if(AccessMethodType.isDirectAccess(planNode.getMethod().getType())){
				valTerm = qt.getObject();
				valHasSqlType = true;
			}
			else{
				valTerm = qt.getSubject();
			}
			if(!predicateTable.isOneToOne(predicate)){
				if(valTerm.isVariable()){
					Variable valueVariable = valTerm.getVariable();
					Db2Type pType=predicateTable.getType(qt.getPredicate().getIRI().getValue());
					wrapper.addProperyValueType(valueVariable.getName(), pType);
					if(projectedInSecondary.contains(valueVariable))continue;
					projectedInSecondary.add(valueVariable);
					String valSqlName = coalesceColumnExpression(tripleToIdMap.get(qt), Constants.NAME_COLUMN_PREFIX_LIST_ELEMENT,Constants.NAME_COLUMN_PREFIX_VALUE);
					String valTypeSqlName = null;
					valSqlToSparql.add(valSqlName+" AS "+valueVariable.getName());
					if(!iriBoundVariables.contains(valueVariable)){
						valTypeSqlName = (valHasSqlType)?
								coalesceColumnExpression(tripleToIdMap.get(qt), Constants.NAME_COLUMN_PREFIX_TYPE, Constants.NAME_COLUMN_PREFIX_TYPE)
								: new Short(TypeMap.IRI_ID).toString();
						valSqlToSparql.add(valTypeSqlName+" AS "+valueVariable.getName()+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);
					}
				}
			}else{
				if(valTerm.isVariable()){
					Variable valueVariable = valTerm.getVariable();			
					if(projectedInSecondary.contains(valueVariable))continue;
					projectedInSecondary.add(valueVariable);
					valSqlToSparql.add(valueVariable.getName()+" AS "+valueVariable.getName());
					if(!iriBoundVariables.contains(valueVariable)){
						valSqlToSparql.add(valueVariable.getName()+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS+" AS "+valueVariable.getName()+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);
					}
				}
			}			
		}
		return valSqlToSparql;
	}

	
	
	private List<String> getTargetSQLClauseForSecondary(){
		List<String> targetSQLClause = new LinkedList<String>();
		if(AccessMethodType.isDirectAccess(planNode.getMethod().getType())){
			targetSQLClause.add(store.getDirectSecondary());
		}
		else{
			targetSQLClause.add(store.getReverseSecondary());
		}
		return targetSQLClause;		
	}
	
	
	private List<String> getValueSQLConstraintForSecondary(){
		List<String> valueSQLConstraint = new LinkedList<String>();
		PredicateTable predicateTable = (AccessMethodType.isDirectAccess(planNode.getMethod().getType()))?
				store.getDirectPredicates() : store.getReversePredicates();
		for(QueryTriple qt : planNode.starTriples){
			
			if(predicateTable.isOneToOne(qt.getPredicate().toString()))continue;
			boolean hasSqlType = false;
			QueryTripleTerm valueTerm = null;
			if(AccessMethodType.isDirectAccess(planNode.getMethod().getType())){
				valueTerm = qt.getObject();
				hasSqlType = true;
			}
			else{
				valueTerm = qt.getSubject();
			}
			String valSqlName = coalesceColumnExpression(tripleToIdMap.get(qt),Constants.NAME_COLUMN_PREFIX_LIST_ELEMENT, Constants.NAME_COLUMN_PREFIX_VALUE);
			String valTypeSqlName = null;
			
			if(valueTerm.isVariable()){		
				Variable valueVariable = valueTerm.getVariable();
				PlanNode predecessor = planNode.getPredecessor(wrapper.getPlan());
				boolean valueHasConstraintWithPredecessor = false;
				if(predecessor != null ){
					Set<Variable> availableVariables = predecessor.getAvailableVariables();
					if(availableVariables != null){
						if(availableVariables.contains(valueTerm.getVariable())){
							String valPredName = wrapper.getPlanNodeVarMapping(predecessor,valueTerm.getVariable().getName());
							valueSQLConstraint.add(valSqlName+" = "+ valPredName);
							if (!wrapper.getIRIBoundVariables().contains(valueTerm.getVariable())) {
								valTypeSqlName = coalesceColumnExpression(tripleToIdMap.get(qt),Constants.NAME_COLUMN_PREFIX_TYPE, Constants.NAME_COLUMN_PREFIX_TYPE);
							}
							if(hasSqlType && !wrapper.getIRIBoundVariables().contains(valueTerm.getVariable())){
								valueSQLConstraint.add(valTypeSqlName + " = " + valPredName + Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);
							} 
							valueHasConstraintWithPredecessor = true;
						}
					}
				}
				if(!valueHasConstraintWithPredecessor){
					if(sVarMap.containsKey(valueVariable.getName())){
						valueSQLConstraint.add(valSqlName+" = "+ sVarMap.get(valueVariable.getName()).fst);
					}
				}
				sVarMap.put(valueVariable.getName(), Pair.make(valSqlName, valTypeSqlName));
			}
			else{
				String valueTermString = (valueTerm.isConstant()) ? valueTerm.getConstant().toDataString() : valueTerm.toString();
				valueSQLConstraint.add(valSqlName+" = '"+valueTermString+"'");
			}
		}
		return valueSQLConstraint;
	}
	
	private List<String> getMultivaluedIds(){
		List<String> multivaluedIds = new ArrayList<String>();
		PredicateTable predicateTable = (AccessMethodType.isDirectAccess(planNode.getMethod().getType()))?
				store.getDirectPredicates() : store.getReversePredicates();
		for(QueryTriple qt : tripleToIdMap.keySet()){
			IRI p = qt.getPredicate().getIRI();
			if(!predicateTable.isOneToOne(p.getValue()))	
				multivaluedIds.add(tripleToIdMap.get(qt).toString());
		}
		return multivaluedIds;
	}	
	
	private Set<Variable> getMultivaluedVariables(){
		HashSet<Variable> multivaluedVariables =  new HashSet<Variable>();
		PredicateTable predicateTable = (AccessMethodType.isDirectAccess(planNode.getMethod().getType()))?store.getDirectPredicates() : store.getReversePredicates();
		Set<QueryTriple> qtSet = new HashSet<QueryTriple>();
		qtSet.addAll(planNode.starTriples);
		if (planNode.starOptionalTriples != null) qtSet.addAll(planNode.starOptionalTriples);
		for(QueryTriple qt : qtSet){
			QueryTripleTerm valTerm = (AccessMethodType.isDirectAccess(planNode.getMethod().getType()))?qt.getObject() : qt.getSubject();
			if(valTerm.isVariable()){
				String predicate = qt.getPredicate().getIRI().getValue();
				if(! predicateTable.isOneToOne(predicate))multivaluedVariables.add(valTerm.getVariable());
			}
		}
		return multivaluedVariables;
	}

	
		
	private String coalesceColumnExpression(int i, String secondaryColumn, String primaryColumn){
		String expression = "COALESCE(S"+i+"."+secondaryColumn+","+primaryColumn+i+")";
		return expression;
	}
	
	
	
	/**
	 * This is used to compute the predicate or value columns in the PH tables
	 * Deals with multiple hash functions
	 * @param columnName
	 * @return
	 */
	private String hashColumnExpression(String columnName, String predicateString) {
		PredicateTable  hashingFamily = null;
		AccessMethod am = planNode.getMethod();
		if(AccessMethodType.isDirectAccess(am.getType())){
			hashingFamily = store.getDirectPredicates();
		}
		else{
			hashingFamily = store.getReversePredicates();
		}		
		int numberOfPredHashes = hashingFamily.getHashCount(predicateString);
		if (numberOfPredHashes == 0) {
			return null;			
		} else if (numberOfPredHashes == 1 /*&& (mergeLogicType != SQLLogicNodeType.AND || !tapn.hasPossibleSpills(store))*/) {
			String pExpression = "T." + columnName + hashingFamily.getHashes(predicateString)[0];
			return pExpression;
		} else {
			MonitoredStringBuffer tmpProjectString = new MonitoredStringBuffer(" CASE ");
			for (int h = 0; h < numberOfPredHashes; h++) {
				int hash = hashingFamily.getHashes(predicateString)[h];
				if (hash == -1) {
					hash = 0;
				}			
				readPredicateColumn(predicateString, tmpProjectString, hash, columnName);			
				if (hashingFamily.getHashes(predicateString)[0] == -1) {
					break;
				}
			}		
			tmpProjectString.append(" ELSE NULL END");		
			return tmpProjectString.toString();
		}
	}	
	
	private void readPredicateColumn(String predicateString, MonitoredStringBuffer projectString, int hash, String columnName) {
		projectString.append(" WHEN T.");
		projectString.append(Constants.NAME_COLUMN_PREFIX_PREDICATE);
		projectString.append(hash);
		projectString.append(" = '");
		projectString.append(printPredicateString(predicateString));
		projectString.append("' ");
		projectString.append(" THEN T.");
		projectString.append(columnName);
		projectString.append(hash);
		projectString.append("\n");
	}
	
	private String printPredicateString(String predicateString) {
		return getSID(predicateString, store
				.getMaxStringLen());
	}
	
	private HashMap<QueryTriple,Integer> mapPredicatesToUniqueIds(){
		HashMap<QueryTriple,Integer> predicateMap = new HashMap<QueryTriple,Integer>();
		int globalPredicateId=1;
		for(QueryTriple qt : planNode.starTriples){
			predicateMap.put(qt, globalPredicateId++);
		}
		if(planNode.starOptionalTriples != null){
			for(QueryTriple qt : planNode.starOptionalTriples){
				predicateMap.put(qt, globalPredicateId++);
			}
		}
		return predicateMap;
	}
	
}
