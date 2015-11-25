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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.stringtemplate.StringTemplate;

import com.ibm.research.rdf.store.Context;
import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.config.Constants;
import com.ibm.research.rdf.store.runtime.service.types.TypeMap;
import com.ibm.research.rdf.store.sparql11.model.AggregateExpression;
import com.ibm.research.rdf.store.sparql11.model.AggregateExpression.EType;
import com.ibm.research.rdf.store.sparql11.model.BlankNodeVariable;
import com.ibm.research.rdf.store.sparql11.model.ConstantExpression;
import com.ibm.research.rdf.store.sparql11.model.Expression;
import com.ibm.research.rdf.store.sparql11.model.OrderCondition;
import com.ibm.research.rdf.store.sparql11.model.Pattern;
import com.ibm.research.rdf.store.sparql11.model.ProjectedVariable;
import com.ibm.research.rdf.store.sparql11.model.SelectClause;
import com.ibm.research.rdf.store.sparql11.model.SelectClause.ESelectModifier;
import com.ibm.research.rdf.store.sparql11.model.SolutionModifiers;
import com.ibm.research.rdf.store.sparql11.model.Variable;
import com.ibm.research.rdf.store.sparql11.model.VariableExpression;
import com.ibm.research.rdf.store.sparql11.planner.PlanNode;
import com.ibm.research.rdf.store.sparql11.sqlwriter.FilterContext;
import com.ibm.research.rdf.store.sparql11.sqlwriter.SPARQLToSQLExpression;
import com.ibm.research.rdf.store.sparql11.sqlwriter.SQLWriterException;
import com.ibm.wala.util.collections.HashMapFactory;
import com.ibm.wala.util.collections.HashSetFactory;
import com.ibm.wala.util.collections.Pair;


public abstract class AbstractSelectTemplate extends SolutionModifierBaseTemplate {
	protected Set<Variable> explicitIRIBoundVariables;
	public AbstractSelectTemplate(String templateName, Store store, Context ctx, STPlanWrapper wrapper, Set<Variable> explicitIRIBoundVariables) {
		super(templateName, store, ctx, wrapper);
		this.explicitIRIBoundVariables = explicitIRIBoundVariables;
	}
	public AbstractSelectTemplate(String templateName, Store store, Context ctx, STPlanWrapper wrapper) {
		this(templateName, store, ctx, wrapper, null);
		
	}

	protected abstract Set<Variable> getAllPatternVariables();
	
	protected abstract SelectClause getSelectClause();
	
	protected abstract Pattern getPattern();
	
	protected abstract SolutionModifiers getSolutionModifiers();
	
	protected abstract List<ProjectedVariable> getProjectedVariables();
	
	
	protected HashMap<String, Pair<String, String>> varMap = new HashMap<String, Pair<String, String>>();

	
	Map<String, SQLMapping> populateMappings() throws Exception{
		Map<String,SQLMapping> mappings = HashMapFactory.make();
		
		
		List<String> selectDistinctMap = getSelectDistinctMapping();
		if(selectDistinctMap != null){			
			SQLMapping selectDistinctMapping=new SQLMapping("distinct", selectDistinctMap, null);
			mappings.put("distinct", selectDistinctMapping);
		}
		List<String> projectAliasNames = new LinkedList<String>();
		Pair<List<String>, Set<String>> selectProjectMapping = getSelectProjectMapping(projectAliasNames) ;
		List<String> projectList = selectProjectMapping != null ? selectProjectMapping.fst : null;
		boolean projectAll = false;
		if(projectList == null || projectList.isEmpty()) {
			projectList = new LinkedList<String>();
			projectList.add("*");
			assert projectAliasNames == null || projectAliasNames.isEmpty() : projectAliasNames;
			projectAliasNames.add("*");
			projectAll = true;
		} 
		SQLMapping projectAliasNameMapping=new SQLMapping("project_alias_name", projectAliasNames,null);
		mappings.put("project_alias_name", projectAliasNameMapping);
		
		SQLMapping projectMapping=new SQLMapping("project", projectList,null);
		mappings.put("project", projectMapping);
		
		SolutionModifiers solMod = getSolutionModifiers();
		if (!projectAll && (store.getStoreBackend() == Store.Backend.shark) 
			&& solMod!=null && solMod.getOrderClause()!=null && solMod.getOrderClause().size()>0) {
			Set<Variable> varsInOrderBy = HashSetFactory.make();
			for (OrderCondition order : solMod.getOrderClause()) {
				varsInOrderBy.addAll(order.getExpression().gatherVariables());
			}
			Set<Variable> projectedVars = HashSetFactory.make();
			for (ProjectedVariable pv : getProjectedVariables()) {
				projectedVars.add(pv.getVariable());
			}
			Set<Variable> varsInOrderByNotInProjectedVars = HashSetFactory.make(varsInOrderBy);
			varsInOrderByNotInProjectedVars.removeAll(projectedVars);
			List<String> vars = new LinkedList<String>();
			for (Variable v: varsInOrderByNotInProjectedVars) {
				vars.add(wrapper.getRenamedVariableFor(v).getName());
				if (!(v instanceof BlankNodeVariable)) {
					if ( !getPattern().gatherIRIBoundVariables().contains(v)) {
						vars.add(wrapper.getRenamedVariableFor(v).getName()+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS );
					}
				}
			}
			SQLMapping project_orderByVarsMapping =  new SQLMapping("project_orderby_vars", 
					vars, null);
			mappings.put("project_orderby_vars", project_orderByVarsMapping);
		} else {
			SQLMapping project_orderByVarsMapping =  new SQLMapping("project_orderby_vars", 
					null, null);
			mappings.put("project_orderby_vars", project_orderByVarsMapping);
		}
		
		SQLMapping tMapping=new SQLMapping("target", getTargetSQLClause(),null);
		mappings.put("target", tMapping);
	
		String solnModifiers = getSolutionModifiersMappings();
		if (solnModifiers != null && solnModifiers.length() != 0) {
			mappings.put("endModifiers", new SQLMapping("endModifiers", solnModifiers, null));
		}
		
		Set<String> outerProject = selectProjectMapping != null ? selectProjectMapping.snd : null;
		if (outerProject != null) {
			mappings.put("outerProject", new SQLMapping("outerProject", outerProject, null));
		}

		
		return mappings;
	}
	
	
	
	private Map<String, Expression> getExpressionsInSolutionModifiers() {
		Map<String, Expression> ret = HashMapFactory.make();
		SolutionModifiers mods = getSolutionModifiers();
		if (mods == null) {
			return Collections.emptyMap();
		}
		if (mods.getGroupClause() != null && mods.getGroupClause().getConditions() != null) {
			for (Expression e : mods.getGroupClause().getConditions()) {
				extractExpressionFromSolutionsClause(ret, e);
			}
		}
		if (mods.getHavingClause() != null && mods.getHavingClause().getConstraints() != null) {
			for (Expression e : mods.getHavingClause().getConstraints()) {
				extractExpressionFromSolutionsClause(ret, e);
			}
		}
		
		return ret;
	}

	private void extractExpressionFromSolutionsClause(
			Map<String, Expression> ret, Expression e) {
		if (e instanceof VariableExpression) {
			if (((VariableExpression) e).getExpression() != null) {
				ret.put(((VariableExpression) e).getVariable(), ((VariableExpression) e).getExpression());
			}
		}
	}


	
	private Pair<List<String>, Set<String>> getSelectProjectMapping(List<String> projectAliasNames) throws Exception{
		List<ProjectedVariable> projectedVariables = getProjectedVariables();
		if (projectedVariables == null || projectedVariables.isEmpty()) {
			return null;
		}

		Pair<List<String>, Set<AggregateExpression>> exps = getProjectionMapping(projectedVariables, projectAliasNames);
		List<String> projectMapping = exps.fst; 
		Set<AggregateExpression> aggregateExpressions = exps.snd;
		
		Set<String> aggs = HashSetFactory.make();
		if (aggregateExpressions != null && !aggregateExpressions.isEmpty()) {
			for (Expression e: aggregateExpressions) {
				for (Variable v : e.gatherVariables()) {
					assert varMap.containsKey(v.getName());
					aggs.add("MIN(" + varMap.get(v.getName()).snd + ") AS " + varMap.get(v.getName()).snd + "_MIN");
					projectAliasNames.add(varMap.get(v.getName()).snd + "_MIN");
					aggs.add("MAX(" + varMap.get(v.getName()).snd + ") AS "+ varMap.get(v.getName()).snd + "_MAX");	
					projectAliasNames.add( varMap.get(v.getName()).snd + "_MAX");	
				}
			}
		}
		
		for (String s: aggs) {
			projectMapping.add(s);
		}
		
		Set<String> outerProjections = null;
		if (aggregateExpressions != null && !aggregateExpressions.isEmpty()) {
			outerProjections = getOuterProjectionMapping(projectedVariables, aggregateExpressions);
		}
		
		return Pair.make(projectMapping, outerProjections);
	}
	
	private Set<String> getOuterProjectionMapping(List<ProjectedVariable> projectedVariables, Set<AggregateExpression> aggregateExpressions) {
		Set<String> ret = HashSetFactory.make();
		Map<String, Expression> expressionsInSolutions = getExpressionsInSolutionModifiers();

		for (ProjectedVariable v : projectedVariables) {
			Variable renamedpv = wrapper.getRenamedVariableFor(v.getVariable());

			if (v.getExpression() != null && aggregateExpressions.contains(v.getExpression())) {
				ret.add(getOuterAggregate(renamedpv, v.getExpression()));
			} else if (expressionsInSolutions.containsKey(v.getVariable().getName())) {
				Expression e = expressionsInSolutions.get(v.getVariable().getName());
				if (aggregateExpressions.contains(e)) {
					getOuterAggregate(renamedpv, e);
				}
			} else {
				ret.add(renamedpv.getName());
			} 
		}
		return ret;
	}
		
	private String getOuterAggregate(Variable renamedpv, Expression e) {
		// KAVITHA: we need to wrap an aggregate in a case if its a numeric and there has been an 'error' in processing it meaning we have different types
		StringTemplate t = SPARQLToSQLExpression.getInstanceOf("outer_aggregate_function_type_check");
		List<String> typeCheck = new LinkedList<String>();
		for (Variable v :e.gatherVariables()) {
			typeCheck.add(varMap.get(v.getName()).snd + "_MIN >= " + TypeMap.DATATYPE_NUMERICS_IDS_START + " AND " + varMap.get(v.getName()).snd + "_MAX <= " + TypeMap.DATATYPE_NUMERICS_IDS_END);
		}
		t.setAttribute("typecheck", typeCheck);
		t.setAttribute("var", renamedpv.getName());
		return t.toString();
	}

	private Pair<List<String>, Set<AggregateExpression>> getProjectionMapping(List<ProjectedVariable> projectedVariables, List<String> projectAliasNames) throws SQLWriterException {
		if (projectedVariables == null || projectedVariables.isEmpty()) {
			return null;
		}
		Set<Variable> queryPatternVariables = HashSetFactory.make();
		Set<AggregateExpression> aggregateExpressions = HashSetFactory.make();
		// eliminate blank nodes
		//Achille: We should not eliminate blank nodes
		Set<Variable> patternVariables = getAllPatternVariables();
		if (patternVariables != null) {
			for (Variable v : patternVariables) {
				//if (!(v instanceof BlankNodeVariable))
				//Achille: We should not eliminate blank nodes
				queryPatternVariables.add(v);
			}
		}

		List<String> projectMapping = new LinkedList<String>();
		Set<Variable> iriBoundVariables = 
				explicitIRIBoundVariables!=null?explicitIRIBoundVariables: getPattern().gatherIRIBoundVariables();
		
		
		Map<String, Expression> expressionsInSolutions = getExpressionsInSolutionModifiers();
		
		if (projectedVariables != null) {
			for (ProjectedVariable pv : projectedVariables) {
				Variable renamedpv = wrapper.getRenamedVariableFor(pv.getVariable());
				
				if (queryPatternVariables != null && queryPatternVariables.contains(pv.getVariable())) {
					String sqlVarName = wrapper.getLastVarMappingForQueryInfo(pv.getVariable());
					projectMapping.add(sqlVarName+" AS "+ renamedpv.getName());
					projectAliasNames.add(renamedpv.getName());
					if( !iriBoundVariables.contains(pv.getVariable())){
						String sqlVarTypeName = (sqlVarName != null)?sqlVarName+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS:null; 
						projectMapping.add(sqlVarTypeName+" AS "+ renamedpv.getName()+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);
						projectAliasNames.add(renamedpv.getName()+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);
					}
				}
				else if (pv.getExpression() != null) {
					handleProjectedExpression(pv.getExpression(), renamedpv, projectMapping, projectAliasNames, aggregateExpressions);
				} else if (expressionsInSolutions.containsKey(pv.getVariable().getName())) {
					Expression e = expressionsInSolutions.get(pv.getVariable().getName());
					handleProjectedExpression(e, renamedpv, projectMapping, projectAliasNames, aggregateExpressions);
				} else {
					projectMapping.add(renamedpv.getName());
					projectAliasNames.add(renamedpv.getName());
					if( explicitIRIBoundVariables!=null && !iriBoundVariables.contains(pv.getVariable())){
						projectMapping.add( renamedpv.getName()+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);
						projectAliasNames.add(renamedpv.getName()+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);
					}
				}
			}
		}
		return Pair.make(projectMapping, aggregateExpressions);
	}
	
	/*protected List<String> getProjectMapping(List<String> projectAliasNames) throws Exception{
		return getSelectProjectMapping(projectAliasNames).fst;
	}*/
	
	private void handleProjectedExpression(Expression e, Variable renamedpv, List<String> projectMapping,List<String> projectAliasNames, Set<AggregateExpression> aggregateExpressions) throws SQLWriterException {

		FilterContext context = new FilterContext(varMap,  wrapper.getPropertyValueTypes(), planNode);
		if (wrapper.getPlan() != null) {
			PlanNode pn = planNode != null ? planNode : wrapper.getPlan().planTreeRoot;
			if (!hasAvailableVariables(pn, e)) {
				return;															// SPARQL has this annoying habit of asking about variables that are never bound
			}
		}
		populateVarMap(e);

		if (needCaseForNumeric(e, context)) {
			context.addConstantNeedsTypeCheck(e);
			SPARQLToSQLExpression.gatherAggregates(e, aggregateExpressions);
		}
		
		String str = expGenerator.getSQLExpression(e, context, store);

		
		if (e.getReturnType() == TypeMap.BOOLEAN_ID) {
			// boolean expressions cannot be projected directly  They need to be wrapped in a case statement
			projectMapping.add(expGenerator.wrapBooleanExpressionInCase(str) +" AS "+renamedpv.getName());
		}  else {
			projectMapping.add(str + " AS " + renamedpv.getName());
		}
		projectAliasNames.add(renamedpv.getName());
		
		String vtyp = null;
		if (e instanceof AggregateExpression) {
			// if the aggregation is a max or min, and has a variable as an argument, then the 
			// type of whatever gets returned is the max of variable
			if (((AggregateExpression) e).getAggregationType() == EType.MAX || ((AggregateExpression) e).getAggregationType() == EType.MIN
					|| ((AggregateExpression) e).getAggregationType() == EType.SUM || ((AggregateExpression) e).getAggregationType() == EType.SAMPLE) {
				Set<Variable> vars = e.gatherVariables();
				assert vars.size() == 1;
				Variable v = vars.iterator().next();
				vtyp = varMap.get(v.getName()).snd;
				projectMapping.add("MAX(" + vtyp + ") AS " + renamedpv.getName() + "_TYP");
				projectAliasNames.add( renamedpv.getName() + "_TYP"); 
			} else {
				projectMapping.add(e.getReturnType() + " AS " + renamedpv.getName() + "_TYP");
				projectAliasNames.add( renamedpv.getName() + "_TYP");
			}
		} else if (!(e instanceof VariableExpression) && !(e instanceof ConstantExpression)){
			projectMapping.add(e.getReturnType() + " AS " + renamedpv.getName() + "_TYP");
			projectAliasNames.add( renamedpv.getName() + "_TYP");
		}
		// done with this expression.  Add the projected variable in to varmap in case it gets referenced later in another expression
		if (!varMap.containsKey(renamedpv.getName())) {
			varMap.put(renamedpv.getName(), Pair.make(str, vtyp));
		}	

	}


	private boolean hasAvailableVariables(PlanNode planNode, Expression e) {
		for (Variable v : e.gatherVariables()) {
			if (!(planNode.getAvailableVariables().contains(v) ||
				  (planNode.getOperatorVariables() != null && planNode.getOperatorVariables().contains(v)) 
			     ) && !varMap.containsKey(v.getName())) {
				return false;
			}
		}
		return true;		
	}
	
	
	private boolean needCaseForNumeric(Expression e, FilterContext context) {

		// KAVITHA: We need something special for aggregate handling. DAWG has the 'open world semantics' of XML and unfortunately
		// doing XML here will cause ridiculous performance problems
		boolean hasSingleType = true;

		for (Variable v : e.gatherVariables()) {
			if (e.getTypeRestriction(v) == TypeMap.TypeCategory.NONE) {
				return false;
			}
			if (!context.getVarMap().containsKey(v.getName())) {
				return false;			// never encountered this variable before, its a new var in project
			}
			if (context.getVarMap().containsKey(v.getName()) && context.getVarMap().get(v.getName()).snd == null) {
				return false;			// we don't even have a type, so unclear we can make any sort of case check
			}
			if (wrapper.getPlan() != null && wrapper.getPlan().isVariableOfSingleType(store, v)) {
				hasSingleType = hasSingleType && true;
			} else {
				return true;			// of mixed type, needs case
			}
		}
		if (hasSingleType) {
			return false;			// all variables in this numeric have a single type, we should be fine with just a type check
		}
		return true;
	}

	
	private List<String> getSelectDistinctMapping(){
		SelectClause sc = getSelectClause();
		if(sc != null){
			if (getSelectClause().getSelectModifier() == ESelectModifier.DISTINCT) {
				List<String> selectDistinctMapping = new LinkedList<String>();		
				selectDistinctMapping.add("DISTINCT");
				return selectDistinctMapping;
			}
		}
		return null;
	}

	
	protected String getSolutionModifiersMappings() throws Exception {
		SolutionModifiers modifiers = getSolutionModifiers();
		if (modifiers == null) {
			return null;
		}
		
		AbstractSQLTemplate solnModiferTemplate = new SolutionModifierTemplate("solnModifier", modifiers ,store, ctx, wrapper, this);


		return solnModiferTemplate.createSQLString();
	}

		
	protected void populateVarMap(Expression e) {
		for (Variable v : e.gatherVariables()){

			String vSqlName = wrapper.getLastVarMappingForQueryInfo(v);
			if (vSqlName == null) {
				vSqlName = v.getName();
			}
			
			if (varMap.containsKey(vSqlName)) {
				continue;
			}

			if(wrapper.getIRIBoundVariables().contains(v)) {
				varMap.put(vSqlName, Pair.make(v.getName(),(String) null));
			} else {
				varMap.put(vSqlName, Pair.make(v.getName(),v.getName()+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS));
			} 
		}
		
	}
}
