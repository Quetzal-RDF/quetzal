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
import com.ibm.research.rdf.store.sparql11.model.BinaryUnion;
import com.ibm.research.rdf.store.sparql11.model.IRI;
import com.ibm.research.rdf.store.sparql11.model.PropertyTerm;
import com.ibm.research.rdf.store.sparql11.model.QueryTriple;
import com.ibm.research.rdf.store.sparql11.model.QueryTripleTerm;
import com.ibm.research.rdf.store.sparql11.model.Variable;
import com.ibm.research.rdf.store.sparql11.planner.AccessMethod;
import com.ibm.research.rdf.store.sparql11.planner.AccessMethodType;
import com.ibm.research.rdf.store.sparql11.planner.PlanNode;
import com.ibm.research.rdf.store.sparql11.sqlwriter.SQLWriterException;
import com.ibm.wala.util.collections.HashMapFactory;
import com.ibm.wala.util.collections.Pair;

/**
 * @author mbornea
 *
 */
public class TripleSecondaryOnlySQLTemplate extends SimplePatternSQLTemplate {

	Set<Variable> projectedInSecondary = null;
	public TripleSecondaryOnlySQLTemplate(String templateName, PlanNode planNode, Store store, Context ctx, STPlanWrapper wrapper) {
		super(templateName, store, ctx, wrapper, planNode);
	}

	@Override
	Map<String, SQLMapping> populateMappings() throws SQLWriterException {
		
		Map<String, SQLMapping> mappings = HashMapFactory.make();
		projectedInSecondary = new HashSet<Variable>();
		varMap = new HashMap<String, Pair<String, String>>();
		
		List<String> qidSqlParam = new LinkedList<String>();
		qidSqlParam.add(getQIDMapping());		
		SQLMapping qidMapping=new SQLMapping("sql_id", qidSqlParam,null);
		mappings.put("sql_id", qidMapping);
		
	
		List<String> projectSqlParams = getProjectedSQLClause();
		if(projectSqlParams.size()==0)projectSqlParams.add("*");
		SQLMapping pMapping=new SQLMapping("project", projectSqlParams,null);
		mappings.put("project", pMapping);
		
		SQLMapping tMapping=new SQLMapping("target", getTargetSQLClause(),null);
		mappings.put("target", tMapping);
		
		SQLMapping eMapping=new SQLMapping("entry_constraint",getEntrySQLConstraint(),null);
		mappings.put("entry_constraint", eMapping);
		
		SQLMapping gMapping=new SQLMapping("graph_constraint", getGraphSQLConstraint(),null);
		mappings.put("graph_constraint", gMapping);
	
		SQLMapping vMapping=new SQLMapping("val_constraint",getValueSQLConstraint(),null);
		mappings.put("val_constraint", vMapping);
		
		SQLMapping predicateMapping=new SQLMapping("predicate_constraint",getPropSQLConstraint(),null);
		mappings.put("predicate_constraint",predicateMapping);
		
		List<String> filterConstraint = getFilterSQLConstraint();
		SQLMapping filterMapping = new SQLMapping("filter_constraint", filterConstraint,null);
		mappings.put("filter_constraint", filterMapping);
		
		List<String> sep = new LinkedList<String>();
		sep.add(" OR ");
		SQLMapping sepMapping=new SQLMapping("sep",sep,null);
		mappings.put("sep", sepMapping);
		
		
		return mappings;
	}

	protected List<String> getProjectedSQLClause() throws SQLWriterException {
		List<String> projectSQLClause = super.getProjectedSQLClause();
		List<String> prop = mapPropForProject();
		if(prop != null)projectSQLClause.addAll(prop);
		return projectSQLClause;
	}

	protected List<String> mapEntryForProject(){
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
			if(projectedInSecondary.contains(entryVariable))return null;
			projectedInSecondary.add(entryVariable);
			List<String> entrySqlToSparql =  new LinkedList<String>();
			entrySqlToSparql.add( Constants.NAME_COLUMN_ENTITY+" AS "+entryVariable.getName());
			String entryType = null;
			Set<Variable> iriBoundVariables = wrapper.getIRIBoundVariables();
			if(!iriBoundVariables.contains(entryVariable)){
				entryType = (entryHasSqlType)?Constants.NAME_COLUMN_PREFIX_TYPE:new Short(TypeMap.IRI_ID).toString();
				entrySqlToSparql.add( entryType+" AS "+entryTerm.getVariable().getName()+"_"+Constants.NAME_COLUMN_PREFIX_TYPE);
			}
			return entrySqlToSparql;
		}
		else{
			return null;
		}
	}	
	
	
	private List<String> mapPropForProject(){ 
		QueryTriple qt = planNode.getTriple();
		// TODO [Property Path]: Double check with Mihaela that it is fine for propTerm.toSqlDataString() to return null for complex path (ie., same behavior as variable)
		PropertyTerm propTerm = qt.getPredicate();
		if(propTerm.isVariable()){
			Variable propVariable = propTerm.getVariable();
			if(projectedInSecondary.contains(propVariable))return null;
			projectedInSecondary.add(propVariable);
			List<String> propSqlToSparql =  new LinkedList<String>();
			propSqlToSparql.add(Constants.NAME_COLUMN_PREFIX_PREDICATE+" AS "+propVariable.getName());
			return propSqlToSparql;
		}
		else{
			return null;
		}
	}
	
	/**
	 * @return
	 */
	protected List<String> mapValForProject(){
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
			// if the predicate is variable it is possible that there is a binding to multiple predicate values in a filter
			// possible optimization might look at the type of all the predicates and decide if they all map to the same type
			Db2Type pType=(qt.getPredicate().isVariable())?Db2Type.MIXED:predicateTable.getType(qt.getPredicate().getIRI().getValue());
			wrapper.addProperyValueType(valueVariable.getName(), pType);
			if(projectedInSecondary.contains(valueVariable))return null;
			projectedInSecondary.add(valueVariable);
			List<String> valSqlToSparql =  new LinkedList<String>();
			valSqlToSparql.add(Constants.NAME_COLUMN_PREFIX_LIST_ELEMENT+" AS " + valueVariable.getName());
			String valType = null;
			Set<Variable> iriBoundVariables = wrapper.getIRIBoundVariables();
			if(!iriBoundVariables.contains(valueVariable)){
				valType = (valueHasSqlType)?Constants.NAME_COLUMN_PREFIX_TYPE:new Short(TypeMap.IRI_ID).toString();
				valSqlToSparql.add(valType+" AS "+valueVariable.getName()+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);
				
			}
			return valSqlToSparql;
		}
		else{
			return null;
		}
	}
	
	protected List<String> mapExternalVarForProject(){		
		List<String> varList = new LinkedList<String>();
		PlanNode predecessor = planNode.getPredecessor(wrapper.getPlan());
			
		if(predecessor!=null){
			String predecessorCTE = wrapper.getPlanNodeCTE(predecessor, false);
			Set<Variable> predecessorVars = predecessor.getAvailableVariables();
			Set<Variable> iriBoundVariables = wrapper.getIRIBoundVariables();
			if(predecessorVars!=null){
				for(Variable v : predecessorVars){
					if(projectedInSecondary.contains(v))continue;
					projectedInSecondary.add(v);
					String vPredName = wrapper.getPlanNodeVarMapping(predecessor,v.getName());
					varList.add(predecessorCTE+"."+vPredName+" AS "+v.getName());
					String vType = null;
					if(!iriBoundVariables.contains(v)){
						vType = predecessorCTE+"."+vPredName+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS;
						varList.add(vType+" AS "+v.getName()+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);							
					}
					varMap.put(v.getName(), Pair.make(predecessorCTE+"."+vPredName, vType));
				}				
			}

		}
		return varList;
	}
		
	private List<String> getTargetSQLClause(){
		List<String> targetSQLClause = new LinkedList<String>();
		if(AccessMethodType.isDirectAccess(planNode.getMethod().getType())){
			targetSQLClause.add(store.getDirectSecondary()+" AS T");
		}
		else{
			targetSQLClause.add(store.getReverseSecondary()+" AS T");
		}
		PlanNode predecessor = planNode.getPredecessor(wrapper.getPlan());
		if(predecessor!=null){
			targetSQLClause.add( wrapper.getPlanNodeCTE(predecessor, true));
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
			boolean entryHasConstraintWithPredecessor = false;
			if(predecessor!=null ){
				entryHasConstraintWithPredecessor= super.getPredecessorConstraint(entrySQLConstraint, entryVariable, predecessor, tTableColumnPrefix+Constants.NAME_COLUMN_ENTITY,tTableColumnPrefix+ Constants.NAME_COLUMN_PREFIX_TYPE, typConstraint);
			}
			/*if(!entryHasConstraintWithPredecessor){
				if(varMap.containsKey(entryVariable.getName())){
					entrySQLConstraint.add(tTableColumnPrefix+Constants.NAME_COLUMN_ENTITY + " = " + varMap.get(entryVariable.getName()).fst);
				}
			} */
			String typeSQL = (typConstraint) ? tTableColumnPrefix+Constants.NAME_COLUMN_PREFIX_TYPE : null;
			varMap.put(entryVariable.getName(), Pair.make(tTableColumnPrefix+Constants.NAME_COLUMN_ENTITY, typeSQL));
		}
		else{
			super.addConstantEntrySQLConstraint(entryTerm, entrySQLConstraint, hasSqlType, tTableColumnPrefix+Constants.NAME_COLUMN_ENTITY);
		}
		return entrySQLConstraint;
	}

	private List<String> getValueSQLConstraint(){
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
		
		String valueSQLName = tTableColumnPrefix+Constants.NAME_COLUMN_PREFIX_LIST_ELEMENT;
		
		if(valueTerm.isVariable()){
			Variable valueVariable = valueTerm.getVariable();
			PlanNode predecessor = planNode.getPredecessor(wrapper.getPlan());
			boolean typConstraint = false;
			if(hasSqlType && wrapper.getIRIBoundVariables().contains(valueVariable)){
				valueSQLConstraint.add(getTypeConstraintForIRIs(tTableColumnPrefix+Constants.NAME_COLUMN_PREFIX_TYPE));
			}
			if(hasSqlType && !wrapper.getIRIBoundVariables().contains(valueVariable)){
				typConstraint = true;
			}
			boolean hasValueConstraintWithPredecessor = false;
			if(predecessor!=null ){
				Set<Variable> availableVariables = predecessor.getAvailableVariables();
				if(availableVariables != null){
					if(availableVariables.contains(valueVariable)){
						String valPredName = wrapper.getPlanNodeVarMapping(predecessor,valueVariable.getName());
						valueSQLConstraint.add(valueSQLName+ 
								" = "+ wrapper.getPlanNodeCTE(predecessor, false)+ "." + valPredName);
						if(typConstraint){
							valueSQLConstraint.add(tTableColumnPrefix+Constants.NAME_COLUMN_PREFIX_TYPE + 
									" = " + wrapper.getPlanNodeCTE(predecessor, false) + "." + valPredName + Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);
						}
						hasValueConstraintWithPredecessor = true;
					}
				}
			}
			if(!hasValueConstraintWithPredecessor){
				if(varMap.containsKey(valueVariable.getName())){
					valueSQLConstraint.add(valueSQLName + " = "+ varMap.get(valueVariable.getName()).fst);
				}
			}
			String valType = (typConstraint) ?tTableColumnPrefix+ Constants.NAME_COLUMN_PREFIX_TYPE : null;
			varMap.put(valueVariable.getName(), Pair.make(tTableColumnPrefix+Constants.NAME_COLUMN_PREFIX_LIST_ELEMENT, valType));
		}
		else{
			valueSQLConstraint.add(valueSQLName+ " = '"+valueTerm.toSqlDataString()+"'");
		}
		return valueSQLConstraint;
	}
	
	private List<String> getPropSQLConstraint(){
		List<String> propSQLConstraint = new LinkedList<String>();
		QueryTriple qt = planNode.getTriple();
		// TODO [Property Path]: Double check with Mihaela that it is fine for propTerm.toSqlDataString() to return null for complex path (ie., same behavior as variable)
		PropertyTerm propTerm = qt.getPredicate();
		PlanNode predecessor = planNode.getPredecessor(wrapper.getPlan());
		if(propTerm.isVariable()){
			if(predecessor!=null){
				Set<Variable> availableVariables = predecessor.getAvailableVariables();
				if(availableVariables != null){
					if(availableVariables.contains(propTerm.getVariable())){
						String propPredName = wrapper.getPlanNodeVarMapping(predecessor,propTerm.getVariable().getName());	
						propSQLConstraint.add(tTableColumnPrefix+Constants.NAME_COLUMN_PREFIX_PREDICATE+
								" = " +  wrapper.getPlanNodeCTE(predecessor, false) + "." + propPredName);
					}
				}
			}
			varMap.put(propTerm.getVariable().getName(), Pair.make(tTableColumnPrefix+Constants.NAME_COLUMN_PREFIX_PREDICATE, (String)null));			
			
		}
		else{
			propSQLConstraint.add(tTableColumnPrefix+Constants.NAME_COLUMN_PREFIX_PREDICATE+ " = '"+propTerm.toSqlDataString()+"'");
		}
		
		return propSQLConstraint;
	}
	
	@Override
	protected List<String> mapGraphForProject(){
		BinaryUnion<Variable,IRI> graphRestriction = planNode.getGraphRestriction();
		if(graphRestriction !=null){	
			if(graphRestriction.isFirstType()){
				Variable graphVariable = graphRestriction.getFirst();
				if(projectedInSecondary.contains(graphVariable))return null;
				projectedInSecondary.add(graphVariable);
				List<String> graphSqlToSparql =  new LinkedList<String>();
				graphSqlToSparql.add(Constants.NAME_COLUMN_GRAPH_ID+" AS "+graphVariable.getName());			
				return graphSqlToSparql;
			}			
		}
		return null;
	}	
	
	@Override
	protected boolean applyBind() {
		return true;
	}
}
