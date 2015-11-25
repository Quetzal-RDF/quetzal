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
import com.ibm.research.rdf.store.Store.PredicateTable;
import com.ibm.research.rdf.store.config.Constants;
import com.ibm.research.rdf.store.runtime.service.types.TypeMap;
import com.ibm.research.rdf.store.sparql11.model.Expression;
import com.ibm.research.rdf.store.sparql11.model.PropertyTerm;
import com.ibm.research.rdf.store.sparql11.model.QueryTriple;
import com.ibm.research.rdf.store.sparql11.model.QueryTripleTerm;
import com.ibm.research.rdf.store.sparql11.model.Variable;
import com.ibm.research.rdf.store.sparql11.planner.AccessMethod;
import com.ibm.research.rdf.store.sparql11.planner.AccessMethodType;
import com.ibm.research.rdf.store.sparql11.planner.PlanNode;
import com.ibm.research.rdf.store.sparql11.planner.PlanNodeType;
import com.ibm.research.rdf.store.sparql11.sqlwriter.FilterContext;
import com.ibm.research.rdf.store.sparql11.sqlwriter.SPARQLToSQLExpression;
import com.ibm.research.rdf.store.sparql11.sqlwriter.SQLWriterException;
import com.ibm.wala.util.collections.HashMapFactory;
import com.ibm.wala.util.collections.HashSetFactory;
import com.ibm.wala.util.collections.Pair;

/**
 * @author mbornea
 *
 */

public class TripleAllPredicatesBothSQLTemplate extends SimplePatternBothSQLTemplate {
	
	protected String ltTempTable;
	
	public TripleAllPredicatesBothSQLTemplate(String templateName, PlanNode planNode, Store store, Context ctx, STPlanWrapper wrapper) {
		super(templateName, store, ctx, wrapper, planNode);		
		ltTempTable = store.getStoreBackend() == Store.Backend.shark ? "LT.nstruct":"LT";
		
	}

	@Override
	Map<String, SQLMapping> populateMappings() throws SQLWriterException {
		
		
		projectedInPrimary = new HashSet<Variable>();
		varMap = new HashMap<String, Pair<String, String>>();
		sVarMap = new HashMap<String, Pair<String, String>>();
		
		Map<String, SQLMapping> mappings = HashMapFactory.make();
		
		List<String> qidSqlParam = new LinkedList<String>();
		qidSqlParam.add(getQIDMapping());
		SQLMapping qidSqlParams=new SQLMapping("sql_id", qidSqlParam,null);
		mappings.put("sql_id", qidSqlParams);
		//,  hasMultipleTargets)
		List<String> targets = getTargetSQLClause();
		SQLMapping tMapping=new SQLMapping("target", targets ,null);
		mappings.put("target", tMapping);
		
		Boolean multipleTargets = targets.size()>1?true: false;
		SQLMapping mtMapping=new SQLMapping("hasMultipleTargets", multipleTargets ,null);
		mappings.put("hasMultipleTargets", mtMapping);
		
		
		SQLMapping eMapping=new SQLMapping("entry_constraint", getEntrySQLConstraint(),null);
		mappings.put("entry_constraint", eMapping);
		
		SQLMapping gMapping=new SQLMapping("graph_constraint", getGraphSQLConstraint(), null);
		mappings.put("graph_constraint", gMapping);
		
		SQLMapping vMapping=new SQLMapping("val_constraint", getValueSQLConstraint(), null);
		mappings.put("val_constraint", vMapping);
	
		List<String> propSqlConstraint = getPropSQLConstraint();
		if (! propSqlConstraint.isEmpty()) {
			SQLMapping predicateMapping=new SQLMapping("predicate_constraint", propSqlConstraint, null);
			mappings.put("predicate_constraint", predicateMapping);
		}
		
		List<String> projectSqlParams = getProjectedSQLClause();
		if(projectSqlParams.size()==0)projectSqlParams.add("*");
		// Shark specific code: Shark cannot handle lateral view with join,
		// so, we first create a CTE Q<sql_id>Prime that first join T and predecessor.
		// We then need to replace references to the predecessor CTE in the project by references to the 
		// intermediate CTE Q<sql_id>Prime		
		if (targets.size()>1 && store.getStoreBackend() == Store.Backend.shark) {
			String target = null;
			PlanNode predecessor = planNode.getPredecessor(wrapper.getPlan());
			if(predecessor!=null){
				List<String> tmp = new LinkedList<String>();
				target = wrapper.getPlanNodeCTE(predecessor, false).trim();
				for (String p: projectSqlParams) {
					p = p.trim();
					if (p.startsWith(target+".") && p.length()>target.length()+1) {
						p = "Q"+getQIDMapping()+"Prime."+p.substring(target.length()+1);
					}
					if (p.startsWith(tTableColumnPrefix) && p.length()>2) {
						p = "Q"+getQIDMapping()+"Prime."+p.substring(2);
					}
					tmp.add(p);
				}
				projectSqlParams = tmp;
			}
		}
		//
		SQLMapping pMapping=new SQLMapping("project", projectSqlParams,null);
		mappings.put("project", pMapping);
		
		List<String> pfilterConstraint = getPFilterSQLConstraint();
		if (!pfilterConstraint.isEmpty()) {
			SQLMapping pfilterMapping = new SQLMapping("pfilter_constraint", pfilterConstraint,null);
			mappings.put("pfilter_constraint", pfilterMapping);
		}
		
		List<String> predicateColumns = getPredColumnsSQLParams();
		SQLMapping predicateColumnsSQLParams = new SQLMapping("columns", predicateColumns,null);
		mappings.put("columns", predicateColumnsSQLParams);
		
		List<String> projectTypeParamsList=projectTypesSQLParam();
		if(projectTypeParamsList!=null){
			SQLMapping projectTypeSQLParams = new SQLMapping("project_type", projectTypeParamsList,null);
			mappings.put("project_type", projectTypeSQLParams);
		}
		
		SQLMapping isRPHSqlParam = new SQLMapping("is_rph",isRPHSQLParam(), null);
		mappings.put("is_rph", isRPHSqlParam);
		
		SQLMapping tSMapping=new SQLMapping("s_target", getTargetSecondarySQLName(), null);
		mappings.put("s_target", tSMapping);		
	
		List<String> projectSecondarySqlParams = getProjectedSQLClauseInSecondary();
		if(projectSecondarySqlParams.size()==0)projectSecondarySqlParams.add("*");
		SQLMapping pSMapping=new SQLMapping("s_project", projectSecondarySqlParams,null);
		mappings.put("s_project", pSMapping);

		List<String> sfilterConstraint = getSFilterSQLConstraint();

		Set<String> pp = getVars(projectSqlParams);
		Set<String> sp = getVars(projectSecondarySqlParams);
		pp.retainAll(sp);
		if (! pp.isEmpty()) {
			vars: for (String v : pp) {
				String bind = " AS " + v;
				if (! projectSecondarySqlParams.contains(v + bind)) {
					for(String s : projectSecondarySqlParams) {
						if (s.endsWith(bind)) {
							String link = s.substring(0, s.length()-bind.length()) + " = " + v;
							sfilterConstraint.add(link);
							continue vars;
						}
					}
					assert false;	
				}
			}
		}
		
		SQLMapping sfilterMapping = new SQLMapping("sfilter_constraint", sfilterConstraint,null);
		mappings.put("sfilter_constraint", sfilterMapping);

				
		return mappings;
	}
	
	private Set<String> getVars(List<String> projectClause) {
		Set<String> vars = HashSetFactory.make();
		for(String var : projectClause) {
			String toks[] = var.split("[ ]");
			vars.add(toks[toks.length-1]);
		}
		return vars;
	}
	
	// @Override because base class behavior of adding bindings map to the projected clause
	//  does not work when you have both a primary access and a secondary access
	protected List<String> getProjectedSQLClause() throws SQLWriterException {
		List<String> projectSQLClause = super.getProjectedSQLClause();
		List<String> prop = mapPropForProject();
		if(prop != null)projectSQLClause.addAll(prop);
		return projectSQLClause;
	}

	List<String> mapEntryForProject(){
		QueryTriple qt = planNode.getTriple();
		QueryTripleTerm entryTerm = null;
		AccessMethod am = planNode.getMethod();
		boolean entityHasSqlType = false;
		if(AccessMethodType.isDirectAccess(am.getType())){
			entryTerm = qt.getSubject();
		}
		else{
			entryTerm = qt.getObject();
			entityHasSqlType = true;
		}
		if(entryTerm.isVariable() ){
			Variable entryVariable = entryTerm.getVariable();
			if(projectedInPrimary.contains(entryVariable))return null;
			projectedInPrimary.add(entryVariable);
			List<String> entrySqlToSparql =  new LinkedList<String>();
			entrySqlToSparql.add(Constants.NAME_COLUMN_ENTRY+" AS " + entryVariable.getName());
			String entryType = null;
			Set<Variable> iriBoundVariables = wrapper.getIRIBoundVariables();
			if(!iriBoundVariables.contains(entryVariable)){
				entryType = (entityHasSqlType)?Constants.NAME_COLUMN_PREFIX_TYPE:new Short(TypeMap.IRI_ID).toString();
				entrySqlToSparql.add(entryType+" AS "+entryVariable.getName()+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);				
			}
			return entrySqlToSparql;
		}
		else{
			return null;
		}
	}	
	
	
	/**
	 * For this schema and table access the predicate can not be variable 
	 */
	List<String> mapPropForProject(){ 
		QueryTriple qt = planNode.getTriple();
		PropertyTerm propTerm = qt.getPredicate();
		if(propTerm.isVariable()){
			Variable propVariable = propTerm.getVariable();
			if(projectedInPrimary.contains(propVariable))return null;
			projectedInPrimary.add(propVariable);
			List<String> propSqlToSparql =  new LinkedList<String>();
			propSqlToSparql.add(ltTempTable+".prop AS "+propVariable.getName());
			return propSqlToSparql;
		}
		else{
			return null;
		}
	}
	
	/**
	 * @return
	 */
	List<String> mapValForProject(){
		List<String> valSqlToSparql =  new LinkedList<String>();
		valSqlToSparql.add(ltTempTable+".val AS ltval");
		AccessMethod am = planNode.getMethod();
		boolean valHasSqlType = false;
		if(AccessMethodType.isDirectAccess(am.getType())){
			valHasSqlType = true;
		}
		valSqlToSparql.add(((valHasSqlType)?(ltTempTable+".typ"):new Short(TypeMap.IRI_ID).toString()) + " AS lttyp");

		// when there is a constraint on the value because of predecessor's available variable with the same name
		// the predecessors variable needs to be projected as well in the primary table
		// so the constraint is applied in the secondary table
		QueryTriple qt = planNode.getTriple();
		QueryTripleTerm valueTerm = (AccessMethodType.isDirectAccess(am.getType()))?qt.getObject():qt.getSubject();
		if(valueTerm.isVariable()){
			Variable valueVariable = valueTerm.getVariable();
			PlanNode predecessor = planNode.getPredecessor(wrapper.getPlan());
			boolean typConstraint = false;			
			if(valHasSqlType && !wrapper.getIRIBoundVariables().contains(valueVariable)){
				typConstraint = true;
			}
			if(predecessor!= null ){
				Set<Variable> availableVariables = predecessor.getAvailableVariables();
				String predecessorCte = wrapper.getPlanNodeCTE(predecessor, false);
				if(availableVariables != null){
					if(availableVariables.contains(valueVariable)){
						if(!projectedInPrimary.contains(valueVariable)){
							projectedInPrimary.add(valueVariable);
							String valuePredName = wrapper.getPlanNodeVarMapping(predecessor,valueVariable.getName());
							valSqlToSparql.add(predecessorCte+ "." + valuePredName+" AS "+valueVariable.getName());
							if(typConstraint){
								valSqlToSparql.add(predecessorCte+ "." + valuePredName+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS+
										" AS "+valueVariable.getName()+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);
							}
						}
					}
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
			Set<Variable> iriBoundVariables = wrapper.getIRIBoundVariables();
			if(predecessorVars != null){
				for(Variable v : predecessorVars){
					if(projectedInPrimary.contains(v))continue;
					projectedInPrimary.add(v);
					String vPredName = wrapper.getPlanNodeVarMapping(predecessor,v.getName());
					String vTypePredName = null;
					String sVTypePredName = null;
					map.add(predecessorCte+"."+vPredName+" AS "+v.getName());
					if(!iriBoundVariables.contains(v)){
						vTypePredName = wrapper.getPlanNodeVarMapping(predecessor,v.getName())+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS;
						sVTypePredName = v.getName()+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS;
						map.add(predecessorCte+"."+vTypePredName+" AS "+v.getName()+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);
					}
					varMap.put(v.getName(), Pair.make(vPredName, vTypePredName));
					sVarMap.put(v.getName(), Pair.make(v.getName(), sVTypePredName));
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
	
	List<String> getPredColumnsSQLParams(){
		List<String> predColumnsId = new LinkedList<String>();

		Variable pred = planNode.getTriple().getPredicate().getVariable();
		Set<String> predicateBindings = new HashSet<String>();
		for (Expression e : planNode.getFilters()) {
			try {
				predicateBindings.addAll(SPARQLToSQLExpression.getVarFilterBinding(e, pred));
			} catch (SQLWriterException e1) {
				e1.printStackTrace();
			}
		}
		if(predicateBindings.size()==0){
			int nrColumns=0;
			if(AccessMethodType.isDirectAccess(planNode.getMethod().getType())){
				nrColumns = store.getDPrimarySize();			
			}
			else{
				nrColumns = store.getRPrimarySize();
			}	
			for(Integer i=0;i<nrColumns;i++){
				if (!predColumnsId.contains(i.toString())) {
					predColumnsId.add(i.toString());
				}
			}		
		}
		else{
			for(String predString : predicateBindings){
				int [] hashes = getHashes(predString);
				if(hashes!=null){
					for(Integer i : hashes)	{
						if (!predColumnsId.contains(i.toString())) {
							predColumnsId.add(i.toString());
						}
					}
				}
			}
		}
		return predColumnsId;		
	}

	List<String> getEntrySQLConstraint(){
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
				entryHasConstraintWithPredecessor = super.getPredecessorConstraint(entrySQLConstraint, entryVariable, predecessor,tTableColumnPrefix+ Constants.NAME_COLUMN_ENTRY, tTableColumnPrefix+Constants.NAME_COLUMN_PREFIX_TYPE, typConstraint);
			}
			if(!entryHasConstraintWithPredecessor){
				/* This is for constraints imposed by using the same variable name on different positions of the triple node
				   If the variable was already mapped in varMap it appeared already in the triple node	
				   If there was already a constraint with the predecessor there is no need for additional constraint
				   if ?x ?x o has an external producer QS that produces ?x then QS.x = subject and QS.x = predicate
				   														subject = predicate is redundant						
				*/
				if(varMap.containsKey(entryVariable.getName())){
					entrySQLConstraint.add(tTableColumnPrefix+Constants.NAME_COLUMN_ENTRY + " = " + varMap.get(entryVariable.getName()).fst);
				}
			}
			String entryType = (typConstraint) ? tTableColumnPrefix+Constants.NAME_COLUMN_PREFIX_TYPE : null;
			String sEntryType = (typConstraint) ? entryVariable.getName()+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS : null;
			varMap.put(entryVariable.getName(), Pair.make(tTableColumnPrefix+Constants.NAME_COLUMN_ENTRY, entryType));
			// The entry is mapped in the secondary with its variable name
			sVarMap.put(entryVariable.getName(), Pair.make(entryVariable.getName(), sEntryType));
		}
		else{
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
		
		String valueSQLName = "COALESCE("+Constants.NAME_COLUMN_PREFIX_LIST_ELEMENT+",ltval)";
		String typeSQLName = "COALESCE("+Constants.NAME_COLUMN_PREFIX_TYPE+",lttyp)";
		
		if(valueTerm.isVariable()){
			Variable valueVariable = valueTerm.getVariable();
			PlanNode predecessor = planNode.getPredecessor(wrapper.getPlan());
			boolean typConstraint = false;
			if(hasSqlType && wrapper.getIRIBoundVariables().contains(valueVariable)){
				valueSQLConstraint.add(getTypeConstraintForIRIs(typeSQLName));
			}
			if(hasSqlType && !wrapper.getIRIBoundVariables().contains(valueVariable)){
				typConstraint = true;
			}
			boolean valueHasSqlConstraintWithPredecessor = false;
			if(predecessor!= null ){
				Set<Variable> availableVariables = predecessor.getAvailableVariables();
				if(availableVariables != null){
					if(availableVariables.contains(valueVariable)){
						Integer sqlId = wrapper.getPlanNodeId(planNode);
						String valuePredName = wrapper.getPlanNodeVarMapping(predecessor,valueVariable.getName());
						valueSQLConstraint.add(valueSQLName + " = Q" + sqlId+ "." + valuePredName);
						if(typConstraint){
							valueSQLConstraint.add(typeSQLName + " = Q" + sqlId + "." + valuePredName + Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);
						}
						valueHasSqlConstraintWithPredecessor = true;
					}
				}
			}
			if(!valueHasSqlConstraintWithPredecessor){
				if(sVarMap.containsKey(valueVariable.getName())){
					valueSQLConstraint.add(valueSQLName + " = " + sVarMap.get(valueVariable.getName()).fst);
				}
			}
			String valueTypeSQLName = (typConstraint) ? typeSQLName : null;
			sVarMap.put(valueVariable.getName(), Pair.make(valueSQLName, valueTypeSQLName));
		}
		else{
			//String valueTermString = (valueTerm.isConstant()) ? valueTerm.getConstant().toDataString() : valueTerm.toString();
			valueSQLConstraint.add(valueSQLName + " = '"+valueTerm.toSqlDataString()+"'");
		}
		return valueSQLConstraint;
	}
	
	List<String> getPropSQLConstraint(){
		List<String> propSQLConstraint = new LinkedList<String>();
		QueryTriple qt = planNode.getTriple();
		PropertyTerm propTerm = qt.getPredicate();
		PlanNode predecessor = planNode.getPredecessor(wrapper.getPlan());
		boolean propHasConstraintWithPredecessor = false;
		if(predecessor!=null){
			Set<Variable> predecessorAvailableVariables = predecessor.getAvailableVariables();
			if(predecessorAvailableVariables!=null && predecessorAvailableVariables.contains(propTerm.getVariable())){
				String propPredName = wrapper.getPlanNodeVarMapping(predecessor,propTerm.getVariable().getName());				
				propSQLConstraint.add(ltTempTable+".prop = "+propPredName);
				propHasConstraintWithPredecessor = true;
			}
			// the filter was already implemented
		}
		// TODO [Property Path]: Check with Mihaela: what should happen if propTerm is not a variable (e.g., if it is a IRI)?
		if(!propHasConstraintWithPredecessor){
			if(varMap.containsKey(propTerm.getVariable().getName())){
				propSQLConstraint.add(ltTempTable+".prop = "+varMap.get(propTerm.getVariable().getName()).fst);
			}
		}
		varMap.put(propTerm.getVariable().getName(), Pair.make(ltTempTable+".prop", (String)null));
		sVarMap.put(propTerm.getVariable().getName(), Pair.make(propTerm.getVariable().getName(), (String)null));
		return propSQLConstraint;
	}
	
	List<String> getSFilterSQLConstraint() throws SQLWriterException{
		List<String> filterSQLConstraint = new LinkedList<String>();
		
		Map<String,Pair<String,String>> totalMap = HashMapFactory.make();
		for(Map.Entry<String,Pair<String,String>> m : sVarMap.entrySet()) {
			totalMap.put(m.getKey(), m.getValue());
		}
		for(String m : varMap.keySet()) {
			if (! totalMap.containsKey(m)) {
				totalMap.put(m, Pair.make(m, (String)null));
			}
		}
		
		for(Expression e :planNode.getApplicableFilters(wrapper.getPlan())){
			if (applyFilterInPrimary(e)) {
				continue;
			}
			String eSql = expGenerator.getSQLForExpression(e,  new FilterContext(totalMap, wrapper.getPropertyValueTypes(), planNode), store);
			filterSQLConstraint.add(eSql);
			
		}
		return filterSQLConstraint;
	}
	
	
	List<String> getPFilterSQLConstraint() throws SQLWriterException{
		List<String> filterSQLConstraint = new LinkedList<String>();
		QueryTriple qt = planNode.getTriple();

		for(Expression e :planNode.getApplicableFilters(wrapper.getPlan())){
			if (!applyFilterInPrimary(e)) {
				continue;
			}

			String eSql = expGenerator.getSQLForExpression(e, new FilterContext(varMap,  wrapper.getPropertyValueTypes(), planNode), store);
			filterSQLConstraint.add(eSql);
		}
		return filterSQLConstraint;
	}
	
	private List<String> projectTypesSQLParam(){
		List<String> projectTypeList = null;
		AccessMethod am = planNode.getMethod();
		if(AccessMethodType.isDirectAccess(am.getType())){
			projectTypeList = new LinkedList<String>();
			projectTypeList.add("type");
		}
		return projectTypeList;
	}
	
	private Boolean  isRPHSQLParam(){
		AccessMethod am = planNode.getMethod();
		if(AccessMethodType.isDirectAccess(am.getType())){
			return false;
		}
		return true;
	}
	
	private Set<Variable> getAvailableVariablesInSecondary() {
		PlanNode predecessor = null;
		if (wrapper.getPlan().getPlanTree().getPredNodes(planNode).hasNext()) {
			predecessor = wrapper.getPlan().getPlanTree().getPredNodes(planNode).next();
		}
		if (predecessor != null && predecessor.getType() == PlanNodeType.PRODUCT) {
			PlanNode left = wrapper.getPlan().getPlanTree().getSuccNodes(predecessor).next();
			
			Set<Variable> vars = new HashSet<Variable>();
			for (Variable v : planNode.getAvailableVariables()) {
				if (left.getProducedVariables().contains(v)) {
					continue;
				}
				vars.add(v);
			}
			return vars;
		}
		return planNode.getAvailableVariables();
	}

	List<String> getProjectedSQLClauseInSecondary() throws SQLWriterException {
		List<String> projectSecondary = new LinkedList<String>();
		QueryTriple qt = planNode.getTriple();
		QueryTripleTerm valueTerm = null;
		AccessMethod am = planNode.getMethod();
		boolean valHasSqlType = false;
		if(AccessMethodType.isDirectAccess(am.getType())){
			valueTerm = qt.getObject();
			valHasSqlType = true;
		}
		else{
			valueTerm = qt.getSubject();
		}
		Set<Variable> iriBoundVariables = wrapper.getIRIBoundVariables();
		Set<Variable> availableVariables = getAvailableVariablesInSecondary();
		boolean applyBindForPrimary = applyBindForPrimary();
		if (availableVariables != null) {
			Set<Variable> bindProjectedVariables = getBindProjectedVariables();
			for(Variable v : availableVariables) {
				if(bindProjectedVariables.contains(v) && !applyBindForPrimary)continue;
				if(valueTerm.isVariable() && v.equals(valueTerm.getVariable())){
					String valueSQLName = "COALESCE(" + Constants.NAME_COLUMN_PREFIX_LIST_ELEMENT + ",ltval)";
					projectSecondary.add(valueSQLName +" AS "+v.getName());
					String valueTypeSQLName = null;
					if(!iriBoundVariables.contains(valueTerm.getVariable())){
						valueTypeSQLName = (valHasSqlType)? "COALESCE("+Constants.NAME_COLUMN_PREFIX_TYPE+",lttyp)":new Short(TypeMap.IRI_ID).toString();
						projectSecondary.add(valueTypeSQLName +" AS "+v.getName()+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);
					}
				}				
				else{
					projectSecondary.add(v.getName() +" AS "+ v.getName());
					if(!iriBoundVariables.contains(v)){
						if (qt.getPredicate().isVariable() && v.equals(qt.getPredicate().getVariable())) {
							projectSecondary.add(TypeMap.IRI_ID + " AS "+ v.getName()+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);				
						} else {
							projectSecondary.add(v.getName() + Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS+" AS "+ v.getName()+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);
						}
					}
				}
			}
		}
		if (!applyBindForPrimary) {
			projectSecondary.addAll(mapBindForProjectInSecondary());
		}		
		return projectSecondary;
	}
		
	List<String> getTargetSecondarySQLName(){
		List<String> target = new LinkedList<String>();
		if(AccessMethodType.isDirectAccess(planNode.getMethod().getType())){
			target.add(store.getDirectSecondary());
		}
		else{
			target.add(store.getReverseSecondary());
		}
		return target;
	}
	
	private int[] getHashes(String predicateString) {
		PredicateTable  hashingFamily = null;
		AccessMethod am = planNode.getMethod();
		if(AccessMethodType.isDirectAccess(am.getType())){
			hashingFamily = store.getDirectPredicates();
		}
		else{
			hashingFamily = store.getReversePredicates();
		}
		return hashingFamily.getHashes(predicateString);
	}
	
}
