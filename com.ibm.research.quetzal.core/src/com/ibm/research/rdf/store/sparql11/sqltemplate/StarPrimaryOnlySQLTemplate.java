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
import com.ibm.research.rdf.store.sparql11.model.PropertyTerm;
import com.ibm.research.rdf.store.sparql11.model.QueryTriple;
import com.ibm.research.rdf.store.sparql11.model.QueryTripleTerm;
import com.ibm.research.rdf.store.sparql11.model.Variable;
import com.ibm.research.rdf.store.sparql11.planner.AccessMethod;
import com.ibm.research.rdf.store.sparql11.planner.AccessMethodType;
import com.ibm.research.rdf.store.sparql11.planner.PlanNode;
import com.ibm.research.rdf.store.sparql11.sqlwriter.MonitoredStringBuffer;
import com.ibm.research.rdf.store.sparql11.sqlwriter.SQLWriterException;
import com.ibm.wala.util.collections.HashMapFactory;
import com.ibm.wala.util.collections.Pair;

/**
 * @author mbornea
 *
 */
public class StarPrimaryOnlySQLTemplate extends SimplePatternSQLTemplate {
	
	public StarPrimaryOnlySQLTemplate(String templateName, PlanNode planNode, Store store, Context ctx, STPlanWrapper wrapper) {
		super(templateName, store, ctx, wrapper, planNode);
	}

	@Override
	Map<String, SQLMapping> populateMappings() throws SQLWriterException {
		
		projectedInPrimary = new HashSet<Variable>();
		Map<String, SQLMapping> mappings = HashMapFactory.make();
		varMap = new HashMap<String, Pair<String, String>>();
		
		List<String> qidSqlParam = new LinkedList<String>();
		qidSqlParam.add(getQIDMapping());
		SQLMapping qidSqlParams=new SQLMapping("sql_id", qidSqlParam,null);
		mappings.put("sql_id", qidSqlParams);
		
		List<String> projectSqlParams = getProjectedSQLClause();
		if(projectSqlParams.size()==0)projectSqlParams.add("*");
		SQLMapping pMapping=new SQLMapping("project", projectSqlParams,null);
		mappings.put("project", pMapping);
		
		SQLMapping tMapping=new SQLMapping("target", getTargetSQLClause(),null);
		mappings.put("target", tMapping);
		
		SQLMapping eMapping=new SQLMapping("entry_constraint",getEntrySQLConstraint() ,null);
		mappings.put("entry_constraint", eMapping);
		
		SQLMapping vMapping=new SQLMapping("val_constraint", getValueSQLConstraint(), null);
		mappings.put("val_constraint", vMapping);
		
		SQLMapping predicateMapping=new SQLMapping("predicate_constraint", getPropSQLConstraint(), null);
		mappings.put("predicate_constraint", predicateMapping);
		
		List<String> sep = new LinkedList<String>();
		sep.add(" AND ");
		SQLMapping sepMapping=new SQLMapping("sep",sep,null);
		mappings.put("sep", sepMapping);
		
		List<String> filterConstraint = getFilterSQLConstraint();
		SQLMapping filterMapping = new SQLMapping("filter_constraint", filterConstraint,null);
		mappings.put("filter_constraint", filterMapping);
		
		return mappings;
	}


	List<String> mapEntryForProject(){
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
				entryType = (entryHasSqlType)?Constants.NAME_COLUMN_PREFIX_TYPE:new Short(TypeMap.IRI_ID).toString();
				entrySqlToSparql.add(entryType+" AS "+entryTermVar.getName()+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);			
			}
		}
		return entrySqlToSparql;
	}	

		
	/**
	 * @return
	 */
	List<String> mapValForProject(){
		Set<QueryTriple> qtSet = new HashSet<QueryTriple>();
		qtSet.addAll(planNode.starTriples);
		if(planNode.starOptionalTriples != null) qtSet.addAll(planNode.starOptionalTriples);
		QueryTripleTerm valTerm = null;
		List<String> valSqlToSparql =  new LinkedList<String>();
		Set<Variable> iriBoundVariables = wrapper.getIRIBoundVariables();
		for(QueryTriple qt : qtSet){
			String predicate = qt.getPredicate().toString();
			PredicateTable predicateTable = null; 
			boolean valHasSqlType = false;
			if(AccessMethodType.isDirectAccess(planNode.getMethod().getType())){
				valTerm = qt.getObject();
				valHasSqlType = true;
				predicateTable = store.getDirectPredicates();
			}
			else{
				valTerm = qt.getSubject();
				predicateTable = store.getReversePredicates();
			}
			if(valTerm.isVariable()){
				Variable valueVariable = valTerm.getVariable();
				Db2Type pType=predicateTable.getType(qt.getPredicate().getIRI().getValue());
				wrapper.addProperyValueType(valueVariable.getName(), pType);
				if(projectedInPrimary.contains(valueVariable))continue;
				projectedInPrimary.add(valueVariable);
				valSqlToSparql.add(hashColumnExpression(Constants.NAME_COLUMN_PREFIX_VALUE,predicate)+" AS "+valueVariable.getName());
				String valType = null;
				if(!iriBoundVariables.contains(valueVariable)){
					valType = (valHasSqlType)?hashColumnExpression(Constants.NAME_COLUMN_PREFIX_TYPE,predicate):new Short(TypeMap.IRI_ID).toString();
					valSqlToSparql.add(valType+" AS "+valueVariable.getName()+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);					
				}
			}
		}
		return valSqlToSparql;
	}
	
	List<String> mapExternalVarForProject(){		
		List<String> map = new LinkedList<String>();
		PlanNode predecessor = planNode.getPredecessor(wrapper.getPlan());
		if(predecessor!=null){
			String predecessorCte = wrapper.getPlanNodeCTE(predecessor, false);
			Set<Variable> predecessorVars = predecessor.getAvailableVariables();
			if(predecessorVars != null){
				Set<Variable> iriBoundVariables = wrapper.getIRIBoundVariables();
				for(Variable v : predecessorVars){
					if(projectedInPrimary.contains(v))continue;
					projectedInPrimary.add(v);
					String vPredName = wrapper.getPlanNodeVarMapping(predecessor,v.getName());
					String vPredType = null;
					map.add(predecessorCte+"."+vPredName+" AS "+v.getName());
					if(!iriBoundVariables.contains(v)){
						vPredType = vPredName+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS;
						map.add(vPredType+" AS "+v.getName()+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);
					}
					varMap.put(v.getName(), Pair.make(predecessorCte+"."+vPredName, vPredType));
				}
			}

		}
		return map;
	}
	
	List<String> getTargetSQLClause(){
		List<String> targetSQLClause = new LinkedList<String>();
		if(AccessMethodType.isDirectAccess(planNode.getMethod().getType())){
			targetSQLClause.add(store.getDirectPrimary() + " AS T");
		}
		else{
			targetSQLClause.add(store.getReversePrimary() + " AS T");
		}
		PlanNode predecessor = planNode.getPredecessor(wrapper.getPlan());
		if(predecessor!=null){
			targetSQLClause.add(wrapper.getPlanNodeCTE(predecessor, true));
		}
		return targetSQLClause;		
	}
	
	
	List<String> getEntrySQLConstraint(){
		List<String> entrySQLConstraint = new LinkedList<String>();
		AccessMethod am = planNode.getMethod();
		boolean hasSqlType = false;
		if(!AccessMethodType.isDirectAccess(am.getType())){
			hasSqlType = true;
		}		
		if (planNode.getOperatorsVariables().iterator().hasNext()) {
			// The star entry is a variable
			// There is only one star term
			Variable entryTermVar = planNode.getOperatorsVariables().iterator().next(); 
			PlanNode predecessor = planNode.getPredecessor(wrapper.getPlan());
			boolean entryHasConstraintWithPredecestor = false;
			if(predecessor!=null ){
				entryHasConstraintWithPredecestor = super.getPredecessorConstraint(entrySQLConstraint, entryTermVar, predecessor, tTableColumnPrefix+Constants.NAME_COLUMN_ENTRY, tTableColumnPrefix+Constants.NAME_COLUMN_PREFIX_TYPE, hasSqlType);
			}
			if(!entryHasConstraintWithPredecestor){
				if(varMap.containsKey(entryTermVar.getName())){
					entrySQLConstraint.add(varMap.get(entryTermVar.getName()).fst+"="+tTableColumnPrefix+Constants.NAME_COLUMN_ENTRY);
				}
			}
			String entryType = null;
			if(hasSqlType && !wrapper.getIRIBoundVariables().contains(entryTermVar)){
				entryType = tTableColumnPrefix+Constants.NAME_COLUMN_PREFIX_TYPE;
			}
			varMap.put(entryTermVar.getName(), Pair.make(tTableColumnPrefix+Constants.NAME_COLUMN_ENTRY, entryType));
		} else {
			QueryTriple t = planNode.starTriples.iterator().next();
			QueryTripleTerm val = AccessMethodType.isDirectAccess(am.getType())? t.getSubject(): t.getObject();
			super.addConstantEntrySQLConstraint(val, entrySQLConstraint, hasSqlType, tTableColumnPrefix+Constants.NAME_COLUMN_ENTRY);
		}
		return entrySQLConstraint;
	}

	List<String> getValueSQLConstraint(){
		List<String> valueSQLConstraint = new LinkedList<String>();
		for(QueryTriple qt : planNode.starTriples){
			QueryTripleTerm valueTerm = null;
			boolean hasSqlType = false;
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
				boolean valueHasSqlConstraintWithPredecesor = false;
				if(predecessor != null ){
					Set<Variable> availableVariables = predecessor.getAvailableVariables();
					if(availableVariables != null){
						if(availableVariables.contains(valueVariable)){
							String valuePredName = wrapper.getPlanNodeVarMapping(predecessor,valueVariable.getName());
							valueSQLConstraint.add(hashColumnExpression(Constants.NAME_COLUMN_PREFIX_VALUE,predicate)+" = "+ 
									wrapper.getPlanNodeCTE(predecessor, false)+ "." + valuePredName);
							if(hasSqlType && !wrapper.getIRIBoundVariables().contains(valueVariable)){
								valueSQLConstraint.add(hashColumnExpression(Constants.NAME_COLUMN_PREFIX_TYPE,predicate)+ 
										" = " + wrapper.getPlanNodeCTE(predecessor, false) + "." + valuePredName + Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);
							}
							valueHasSqlConstraintWithPredecesor = true;
						}
					}
				}
				if(!valueHasSqlConstraintWithPredecesor){
					if(varMap.containsKey(valueVariable.getName())){
						valueSQLConstraint.add(hashColumnExpression(Constants.NAME_COLUMN_PREFIX_VALUE,predicate)+" = "+varMap.get(valueVariable.getName()).fst);
					}
				}
				String valType = null;
				if(hasSqlType && !wrapper.getIRIBoundVariables().contains(valueVariable)){
					valType = hashColumnExpression(Constants.NAME_COLUMN_PREFIX_TYPE,predicate);
				}
				varMap.put(valueVariable.getName(), Pair.make(hashColumnExpression(Constants.NAME_COLUMN_PREFIX_VALUE,predicate), valType));
			}
			else{
				//String valueTermString = (valueTerm.isConstant()) ? valueTerm.getConstant().toDataString() : valueTerm.toString();
				valueSQLConstraint.add(hashColumnExpression(Constants.NAME_COLUMN_PREFIX_VALUE,predicate)+" = '"+valueTerm.toSqlDataString()+"'");
			}
		}
		return valueSQLConstraint;
	}
	
	List<String> getPropSQLConstraint(){
		List<String> propSQLConstraint = new LinkedList<String>();
		for(QueryTriple qt : planNode.starTriples){
			// TODO [Property Path]: Double check with Mihaela that it is fine for propTerm.toSqlDataString() to return null for complex path (ie., same behavior as variable)
			PropertyTerm  propTerm = qt.getPredicate();
			propSQLConstraint.add(hashColumnExpression(Constants.NAME_COLUMN_PREFIX_PREDICATE,propTerm.toString())+" = '"+propTerm.toSqlDataString()+"'");
		}
		return propSQLConstraint;
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

	@Override
	protected boolean applyBind() {
		return true;
	}	
}
