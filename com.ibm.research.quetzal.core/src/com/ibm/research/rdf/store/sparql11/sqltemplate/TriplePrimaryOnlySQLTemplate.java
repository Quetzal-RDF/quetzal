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
public class TriplePrimaryOnlySQLTemplate extends SimplePatternSQLTemplate {
	

	public TriplePrimaryOnlySQLTemplate(String templateName, PlanNode planNode, Store store, Context ctx, STPlanWrapper wrapper) {
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
		
		SQLMapping eMapping=new SQLMapping("entry_constraint", getEntrySQLConstraint(),null);
		mappings.put("entry_constraint", eMapping);
		
		SQLMapping gMapping=new SQLMapping("graph_constraint", getGraphSQLConstraint(), null);
		mappings.put("graph_constraint", gMapping);
		
		SQLMapping vMapping=new SQLMapping("val_constraint", getValueSQLConstraint(), null);
		mappings.put("val_constraint", vMapping);
		
		SQLMapping predicateMapping=new SQLMapping("predicate_constraint", getPropSQLConstraint(),null);
		mappings.put("predicate_constraint", predicateMapping);
		
		List<String> filterConstraint = getFilterSQLConstraint();
		SQLMapping filterMapping = new SQLMapping("filter_constraint", filterConstraint,null);
		mappings.put("filter_constraint", filterMapping);
				
		return mappings;
	}



	List<String> mapEntryForProject(){
		QueryTriple qt = planNode.getTriple();
		QueryTripleTerm entryTerm = null;
		AccessMethod am = planNode.getMethod();
		boolean entryHasSqlType = false;
		if(AccessMethodType.isDirectAccess(am.getType())){
			entryTerm = qt.getSubject();
		}
		else{
			entryTerm = qt.getObject();
			entryHasSqlType = true;
		}
		if(entryTerm.isVariable()){
			Variable entryVariable = entryTerm.getVariable();
			if(projectedInPrimary.contains(entryVariable))return null;
			projectedInPrimary.add(entryVariable);
			List<String> entrySqlToSparql =  new LinkedList<String>();
			entrySqlToSparql.add(Constants.NAME_COLUMN_ENTRY+" AS "+ entryVariable.getName());
			String entryTypeSql = null;
			Set<Variable> iriBoundVariables = wrapper.getIRIBoundVariables();
			if(!iriBoundVariables.contains(entryVariable)){
				entryTypeSql = (entryHasSqlType)?Constants.NAME_COLUMN_PREFIX_TYPE: new Short(TypeMap.IRI_ID).toString();
				entrySqlToSparql.add(entryTypeSql+" AS "+entryVariable.getName()+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);				
			}			
			return entrySqlToSparql;
		}
		else{
			return null;
		}
	}	
	
	
	/**
	 * @return
	 */
	List<String> mapValForProject(){
		QueryTriple qt = planNode.getTriple();
		QueryTripleTerm valTerm = null;
		AccessMethod am = planNode.getMethod();
		PredicateTable predicateTable = null;
		boolean valueHasSqlType = false;
		if(AccessMethodType.isDirectAccess(am.getType())){
			valTerm = qt.getObject();
			valueHasSqlType = true;
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
			if(projectedInPrimary.contains(valueVariable))return null;
			projectedInPrimary.add(valueVariable);
			List<String> valSqlToSparql =  new LinkedList<String>();
			valSqlToSparql.add(hashColumnExpression(Constants.NAME_COLUMN_PREFIX_VALUE)+" AS "+valueVariable.getName());
			String valSqlType = null;
			Set<Variable> iriBoundVariables = wrapper.getIRIBoundVariables();
			if(!iriBoundVariables.contains(valueVariable)){
				valSqlType = (valueHasSqlType)?hashColumnExpression(Constants.NAME_COLUMN_PREFIX_TYPE):new Short(TypeMap.IRI_ID).toString();
				valSqlToSparql.add(valSqlType+" AS "+valueVariable.getName()+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);
				
			}
			return valSqlToSparql;
		}
		else{
			return null;
		}
	}
	
	List<String> mapExternalVarForProject(){		
		List<String> map = new LinkedList<String>();
		PlanNode predecessor = planNode.getPredecessor(wrapper.getPlan());
		if(predecessor!=null){
			String predecessorCTE = wrapper.getPlanNodeCTE(predecessor, false);
			Set<Variable> predecessorVars = predecessor.getAvailableVariables();
			if(predecessorVars != null){
				Set<Variable> iriBoundVariables = wrapper.getIRIBoundVariables();
				for(Variable v : predecessorVars){
					if(projectedInPrimary.contains(v))continue;
					projectedInPrimary.add(v);
					String vPredName = wrapper.getPlanNodeVarMapping(predecessor,v.getName());
					map.add(predecessorCTE+"."+vPredName+" AS "+v.getName());
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
	
	List<String> getTargetSQLClause(){
		List<String> targetSQLClause = new LinkedList<String>();
		if(AccessMethodType.isDirectAccess(planNode.getMethod().getType())){
			targetSQLClause.add(store.getDirectPrimary() +" AS T");
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
	
	
	private List<String> getEntrySQLConstraint(){
		List<String> entrySQLConstraint = new LinkedList<String>();
		QueryTriple qt = planNode.getTriple();
		QueryTripleTerm entryTerm = null;
		AccessMethod am = planNode.getMethod();
		boolean hasSqlType = false;
		if(AccessMethodType.isDirectAccess(am.getType())){
			entryTerm = qt.getSubject();
		}
		else{
			entryTerm = qt.getObject();
			hasSqlType = true;
		}
		if(entryTerm.isVariable()){
			Variable entryVariable = entryTerm.getVariable();
			PlanNode predecessor = planNode.getPredecessor(wrapper.getPlan());
			boolean typConstraint = false;
			if(hasSqlType && wrapper.getIRIBoundVariables().contains(entryVariable)){
				entrySQLConstraint.add(getTypeConstraintForIRIs(tTableColumnPrefix+Constants.NAME_COLUMN_PREFIX_TYPE));
			}
			else if(hasSqlType && !wrapper.getIRIBoundVariables().contains(entryVariable)){
				typConstraint = true;
			}
			boolean entryConstraintWithPredecessor = getPredecessorConstraint(
					entrySQLConstraint, entryVariable, predecessor,
					tTableColumnPrefix+Constants.NAME_COLUMN_ENTRY, tTableColumnPrefix+Constants.NAME_COLUMN_PREFIX_TYPE, typConstraint);
			if(!entryConstraintWithPredecessor){
				//Check for entry constraint for variable with the same name on different positions
				if(varMap.containsKey(entryVariable.getName())){
					entrySQLConstraint.add(tTableColumnPrefix+Constants.NAME_COLUMN_ENTRY + " = " +varMap.get(entryVariable.getName()).fst);
				}
			}
			String entryType = (typConstraint) ?tTableColumnPrefix+ Constants.NAME_COLUMN_PREFIX_TYPE : null;
			varMap.put(entryVariable.getName(), Pair.make(tTableColumnPrefix+Constants.NAME_COLUMN_ENTRY, entryType));
		}
		else {
			super.addConstantEntrySQLConstraint(entryTerm, entrySQLConstraint, hasSqlType, tTableColumnPrefix+Constants.NAME_COLUMN_ENTRY);
		}
		return entrySQLConstraint;
	}

		
	List<String> getValueSQLConstraint(){
		List<String> valueSQLConstraint = new LinkedList<String>();
		QueryTriple qt = planNode.getTriple();
		QueryTripleTerm valueTerm = null;
		AccessMethod am = planNode.getMethod();
		boolean hasSqlType = false;
		if(AccessMethodType.isDirectAccess(am.getType())){
			valueTerm = qt.getObject();
			hasSqlType = true;
		}
		else{
			valueTerm = qt.getSubject();
		}
		if(valueTerm.isVariable()){
			Variable valueVariable = valueTerm.getVariable();
			PlanNode predecessor = planNode.getPredecessor(wrapper.getPlan());
			boolean typConstraint = false;
			if(hasSqlType && wrapper.getIRIBoundVariables().contains(valueVariable)){
				valueSQLConstraint.add(getTypeConstraintForIRIs(hashColumnExpression(Constants.NAME_COLUMN_PREFIX_TYPE)));
			}
			if(hasSqlType && !wrapper.getIRIBoundVariables().contains(valueVariable)){
				typConstraint = true;
			}
			boolean hasValueConstrintWithPredecessor = 
					getPredecessorConstraint(valueSQLConstraint, 
							valueVariable, 
							predecessor, 
							hashColumnExpression(Constants.NAME_COLUMN_PREFIX_VALUE), 
							hashColumnExpression(Constants.NAME_COLUMN_PREFIX_TYPE),
							typConstraint);
			if(!hasValueConstrintWithPredecessor){
				if(varMap.containsKey(valueVariable.getName())){
					//Check for value constraint for variable with the same name on different positions
					valueSQLConstraint.add(hashColumnExpression(Constants.NAME_COLUMN_PREFIX_VALUE)+ 
							" = " + varMap.get(valueVariable.getName()).fst);
				}
			}
			String valueType = (typConstraint) ? hashColumnExpression(Constants.NAME_COLUMN_PREFIX_TYPE) : null;
			varMap.put(valueVariable.getName(), Pair.make(hashColumnExpression(Constants.NAME_COLUMN_PREFIX_VALUE),valueType));
		}
		else{
			valueSQLConstraint.add(hashColumnExpression(Constants.NAME_COLUMN_PREFIX_VALUE)+ " = '"+valueTerm.toSqlDataString()+"'");
		}
		return valueSQLConstraint;
	}
	
	List<String> getPropSQLConstraint(){
		List<String> propSQLConstraint = new LinkedList<String>();
		QueryTriple qt = planNode.getTriple();
		// TODO [Property Path]: Double check with Mihaela that it is fine for propTerm.toSqlDataString() to return null for complex path (ie., same behavior as variable)
		PropertyTerm propTerm = qt.getPredicate();
		propSQLConstraint.add(hashColumnExpression(Constants.NAME_COLUMN_PREFIX_PREDICATE) + " = '"+propTerm.toSqlDataString()+"'");
		return propSQLConstraint;
	}
	
	/**
	 * This is used to compute the predicate or value columns in the PH tables
	 * Deals with multiple hash functions
	 * @param columnName
	 * @return
	 */
	private String hashColumnExpression(String columnName) {
		String predicateString = planNode.getTriple().getPredicate().toString();
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
