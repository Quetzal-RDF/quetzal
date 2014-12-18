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
 package com.ibm.research.rdf.store.sparql11;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.ibm.research.rdf.store.query.EQueryType;
import com.ibm.research.rdf.store.sparql11.model.BinaryUnion;
import com.ibm.research.rdf.store.sparql11.model.DatasetClause;
import com.ibm.research.rdf.store.sparql11.model.Expression;
import com.ibm.research.rdf.store.sparql11.model.GroupCondition;
import com.ibm.research.rdf.store.sparql11.model.HavingCondition;
import com.ibm.research.rdf.store.sparql11.model.IRI;
import com.ibm.research.rdf.store.sparql11.model.OrderCondition;
import com.ibm.research.rdf.store.sparql11.model.Pattern;
import com.ibm.research.rdf.store.sparql11.model.ProjectedVariable;
import com.ibm.research.rdf.store.sparql11.model.Query;
import com.ibm.research.rdf.store.sparql11.model.QueryTriple;
import com.ibm.research.rdf.store.sparql11.model.SelectClause.ESelectModifier;
import com.ibm.research.rdf.store.sparql11.model.SolutionModifiers;
import com.ibm.research.rdf.store.sparql11.model.SubSelectPattern;
import com.ibm.research.rdf.store.sparql11.model.Variable;

public class QueryInfo {

	private EQueryType queryType;
	private ESelectModifier selectModifier = null;
	private List<ProjectedVariable> projectedVariables=null;	// if this list is null or empty, that means SELECT * 
	private List<BinaryUnion<Variable,IRI>> describedResources=null;
	private SolutionModifiers solutionModifier=null;
	private Set<Variable> queryPatternVariables=null;
	private Set<Variable> optionalVarsWithMultipleBindings=null;
	private Set<String> defaultGraphIRI=null;
	private Set<String> namedGraphIRI=null;
	private Set<BinaryUnion<Variable, IRI>> emptyPatternGraphRestrictions=null;
	private Set<Expression> emptyPatternFilters=null;
	private Set<Expression> constantFilters=null;
	private boolean hasEmptyGraphPattern=false;
	private List<QueryTriple> constructPattern=null;
	private Set<Variable> literalBoundVariables=new HashSet<Variable>();
	private Set<Variable> optionallyExtendedVars=null;
	
	public QueryInfo(Query q){	
		
		if(q.isSelectQuery()){
			queryType=EQueryType.SELECT_QUERY;
			selectModifier=q.getSelectQuery().getSelectClause().getSelectModifier();
			projectedVariables=q.getSelectQuery().getSelectClause().getProjectedVariables();
			solutionModifier=q.getSelectQuery().getSolutionModifier();
			queryPatternVariables=q.getSelectQuery().getAllPatternVariables();
			optionalVarsWithMultipleBindings=q.getSelectQuery().getPattern().gatherOptionalVariablesWithMultipleBindings();
			
			List<DatasetClause> datasetClauses=q.getSelectQuery().getDatasetClauses();
			defaultGraphIRI=new HashSet<String>();
			namedGraphIRI=new HashSet<String>(); 
			if(datasetClauses!=null){
				for(DatasetClause d: datasetClauses){
					if(d.isNamed())namedGraphIRI.add(d.getIri().toString());
					else defaultGraphIRI.add(d.getIri().toString());
				}
			}
			if(q.getMainPattern().isEmpty()){
				hasEmptyGraphPattern=true;
				emptyPatternGraphRestrictions=q.getMainPattern().getGraphRestrictions();
				emptyPatternFilters=q.getMainPattern().gatherFilters();
			}			
		}
		if(q.isDescribeQuery()){
			queryType=EQueryType.DESCRIBE_QUERY;
			describedResources=q.getDescribeQuery().getResources();
			solutionModifier=q.getDescribeQuery().getSolutionModifier();	
			queryPatternVariables=q.getDescribeQuery().getPatternVariables();
			if(q.getDescribeQuery().getPattern()!=null){
				optionalVarsWithMultipleBindings=q.getDescribeQuery().getPattern().gatherOptionalVariablesWithMultipleBindings();
			}
			List<DatasetClause> datasetClauses=q.getDescribeQuery().getDatasetClauses();
			defaultGraphIRI=new HashSet<String>();
			namedGraphIRI=new HashSet<String>(); 
			if(datasetClauses!=null){
				for(DatasetClause d: datasetClauses){
					if(d.isNamed())namedGraphIRI.add(d.getIri().toString());
					else defaultGraphIRI.add(d.getIri().toString());
				}
			}
		}

		if(q.isAskQuery())
		{
			queryType=EQueryType.ASK_QUERY;
			List<DatasetClause> datasetClauses=q.getAskQuery().getDatasetClauses();
			defaultGraphIRI=new HashSet<String>();
			namedGraphIRI=new HashSet<String>(); 
			if(datasetClauses!=null){
				for(DatasetClause d: datasetClauses){
					if(d.isNamed())namedGraphIRI.add(d.getIri().toString());
					else defaultGraphIRI.add(d.getIri().toString());
				}
			}		
			optionalVarsWithMultipleBindings=q.getAskQuery().getPattern().gatherOptionalVariablesWithMultipleBindings();
		}
		if(q.isConstructQuery())
		{
			queryType=EQueryType.CONSTRUCT_QUERY;
			solutionModifier = q.getConstructQuery().getSolutionModifier();
			queryPatternVariables = q.getConstructQuery().getConstructPatternVariables();
			constructPattern = q.getConstructQuery().getConstructPattern();
			projectedVariables= new ArrayList<ProjectedVariable>();
			List<DatasetClause> datasetClauses=q.getConstructQuery().getDatasetClauses();
			defaultGraphIRI=new HashSet<String>();
			namedGraphIRI=new HashSet<String>(); 
			optionalVarsWithMultipleBindings=q.getConstructQuery().getPattern().gatherOptionalVariablesWithMultipleBindings();
			if(datasetClauses!=null){
				for(DatasetClause d: datasetClauses){
					if(d.isNamed())namedGraphIRI.add(d.getIri().toString());
					else defaultGraphIRI.add(d.getIri().toString());
				}
			}
			if(q.getMainPattern().isEmpty()){
				hasEmptyGraphPattern=true;
				emptyPatternGraphRestrictions=q.getMainPattern().getGraphRestrictions();
				emptyPatternFilters=q.getMainPattern().gatherFilters();
			}
			
		}
		Pattern qPattern=q.getMainPattern();
		if(qPattern!=null){
			literalBoundVariables.addAll(qPattern.gatherVariablesWithOptional());
			literalBoundVariables.removeAll(qPattern.gatherIRIBoundVariables());
			if(projectedVariables!=null){
				for(ProjectedVariable pv: projectedVariables){
					if(pv.getExpression()!=null)literalBoundVariables.add(pv.getVariable());
				}
			}
			
			constantFilters=new HashSet<Expression>();
			for(Expression e: q.getMainPattern().gatherFilters()){
				if(e.gatherVariables().size()==0)constantFilters.add(e);
			}
			
		}
		
	}
	
	// This is used for empty patterns
	public QueryInfo(Pattern p, Set<Variable> pVars){		
		queryType=EQueryType.SELECT_QUERY;
		queryPatternVariables=p.gatherVariablesWithOptional();
		optionalVarsWithMultipleBindings=new HashSet<Variable>();
		defaultGraphIRI=new HashSet<String>();
		namedGraphIRI=new HashSet<String>();
		projectedVariables=new LinkedList<ProjectedVariable>();
		if(pVars!=null){
			for(Variable v: pVars)projectedVariables.add(new ProjectedVariable(v.getName()));
		}
		if(p.isEmpty()){
			hasEmptyGraphPattern=true;
			emptyPatternGraphRestrictions=p.getGraphRestrictions();
			emptyPatternFilters=p.gatherFilters();
		}
		
		if(p!=null){
			literalBoundVariables.addAll(p.gatherVariablesWithOptional());
			literalBoundVariables.removeAll(p.gatherIRIBoundVariables());
			if(projectedVariables!=null){
				for(ProjectedVariable pv: projectedVariables){
					if(pv.getExpression()!=null)literalBoundVariables.add(pv.getVariable());
				}
			}
			
			constantFilters=new HashSet<Expression>();
			for(Expression e: p.gatherFilters()){
				if(e.gatherVariables().size()==0)constantFilters.add(e);
			}
			
		}
	}
	
	public QueryInfo(SubSelectPattern p){
		
			queryType=EQueryType.SELECT_QUERY;
			selectModifier=p.getSelectClause().getSelectModifier();
			projectedVariables=p.getSelectClause().getProjectedVariables();
			solutionModifier=p.getSolutionModifier();
			queryPatternVariables=p.getAllPatternVariables();
			optionalVarsWithMultipleBindings=p.getGraphPattern().gatherOptionalVariablesWithMultipleBindings();
			
			defaultGraphIRI=new HashSet<String>();
			namedGraphIRI=new HashSet<String>();
			if(p.isEmpty()){
				hasEmptyGraphPattern=true;
				emptyPatternGraphRestrictions=p.getGraphRestrictions();
				emptyPatternFilters=p.gatherFilters();
			}
			if(p!=null){
				literalBoundVariables.addAll(p.gatherVariablesWithOptional());
				literalBoundVariables.removeAll(p.gatherIRIBoundVariables());
				if(projectedVariables!=null){
					for(ProjectedVariable pv: projectedVariables){
						if(pv.getExpression()!=null)literalBoundVariables.add(pv.getVariable());
					}
				}
				constantFilters=new HashSet<Expression>();
				for(Expression e: p.gatherFilters()){
					if(e.gatherVariables().size()==0)constantFilters.add(e);
				}
			}			
	}


	public QueryInfo(QueryInfo qi){
		
		queryType=qi.queryType;
		selectModifier=qi.selectModifier;
		projectedVariables=qi.projectedVariables;
		solutionModifier=qi.solutionModifier;
		queryPatternVariables=qi.queryPatternVariables;
		optionalVarsWithMultipleBindings=new HashSet<Variable>();
		optionalVarsWithMultipleBindings.addAll(qi.optionalVarsWithMultipleBindings);
		
		defaultGraphIRI=new HashSet<String>();
		if(qi.defaultGraphIRI!=null)defaultGraphIRI.addAll(qi.defaultGraphIRI);
		namedGraphIRI=new HashSet<String>();
		if(qi.namedGraphIRI!=null)namedGraphIRI.addAll(qi.namedGraphIRI);
		
		hasEmptyGraphPattern=qi.hasEmptyGraphPattern;
		emptyPatternGraphRestrictions=qi.emptyPatternGraphRestrictions;
			
		emptyPatternFilters=new HashSet<Expression>();
		if(qi.emptyPatternFilters!=null)emptyPatternFilters.addAll(qi.emptyPatternFilters);
		
		literalBoundVariables=new HashSet<Variable>();
		if(qi.literalBoundVariables!=null)literalBoundVariables.addAll(qi.literalBoundVariables);
			
			
		constantFilters=new HashSet<Expression>();
		if(qi.constantFilters!=null)constantFilters.addAll(qi.constantFilters);	
					
	}
	

	public boolean modifiesResult(){
		if(selectModifier!=null || 
				solutionModifier.getGroupClause()!=null ||
				solutionModifier.getHavingClause()!=null ||
				solutionModifier.getLimitOffset()!=null ||
				solutionModifier.getOrderClause().size()>0)return true;
		return false;
	}
	
	
	public Set<Variable> getProjectedVariableNames(){
		Set<Variable> pVN=new HashSet();
		if(queryType==EQueryType.SELECT_QUERY){
			if(projectedVariables==null)return null;
			if(projectedVariables.size()==0)return null;
			for(ProjectedVariable pv:projectedVariables){
				pVN.add(pv.getVariable());
			}
			return pVN;
		}
		if(queryType==EQueryType.DESCRIBE_QUERY){
			if(describedResources!=null)
				for(BinaryUnion<Variable,IRI> dR: describedResources){
					if(dR.isFirstType())pVN.add(dR.getFirst());
					if(pVN.size()==0)return null;
					else return pVN;
				}
		}
		return null;
	}
	
	public Set<String> getProjectedVariableStrings(){
		Set<String> pVN=new HashSet();
		if(queryType==EQueryType.SELECT_QUERY){
			if(projectedVariables==null)return null;
			if(projectedVariables.size()==0)return null;
			for(ProjectedVariable pv:projectedVariables){
				pVN.add(pv.getVariable().getName());
			}
			return pVN;
		}
		if(queryType==EQueryType.DESCRIBE_QUERY){
			for(BinaryUnion<Variable,IRI> dR: describedResources){
				if(dR.isFirstType())pVN.add(dR.getFirst().getName());
				if(pVN.size()==0)return null;
				else return pVN;
			}
		}
		return null;
	}
	
	public List<ProjectedVariable> getProjectedVariables(){
		if(projectedVariables!=null && projectedVariables.size()==0)return null;
		return projectedVariables;
	}
	
	public EQueryType getQueryType(){
		return queryType;
	}
	
	public ESelectModifier getSelectModifier(){
		return selectModifier;
	}
	
	public List<BinaryUnion<Variable,IRI>> getDescribedResources(){
		return describedResources;
	}
	
	public int  getResultLimit(){
		if(solutionModifier != null && solutionModifier.getLimitOffset()!=null)
			if(solutionModifier.getLimitOffset().getLimit()>0){
				return (solutionModifier.getLimitOffset().getLimit()+
						solutionModifier.getLimitOffset().getOffset());
			}
			else return 0;
		else return 0;
	}
	
	public int  getLimit(){
		if(solutionModifier != null && solutionModifier.getLimitOffset()!=null)
			return (solutionModifier.getLimitOffset().getLimit());
		else return 0;
	}

	public boolean  isLimit(){
		if(solutionModifier != null && solutionModifier.getLimitOffset()!=null)
			return (solutionModifier.getLimitOffset().getLimit() > 0);
		else return false;
	}
	
	public int  getOffset(){
		if(solutionModifier != null && solutionModifier.getLimitOffset()!=null)
			return (solutionModifier.getLimitOffset().getOffset());
		else return 0;
	}
	
	public boolean  isOffset(){
		if(solutionModifier != null && solutionModifier.getLimitOffset()!=null)
			return (solutionModifier.getLimitOffset().getOffset() > 0);
		else return false;
	}
	
	public List<OrderCondition> getOrderClause(){
		return solutionModifier.getOrderClause();
	}
	
	public Set<Variable> getQueryPatternVariables(){
		return queryPatternVariables;
	}
	
	public Set<Variable> getOptionalPatternVariables(){
		return optionalVarsWithMultipleBindings;
	}
	
	public boolean extractExposedVarsOnly(){
		if(queryType==EQueryType.DESCRIBE_QUERY)return true;
		if(queryType==EQueryType.SELECT_QUERY && selectModifier==ESelectModifier.DISTINCT)return true;
		return false;
	}
	
	
	public Set<Variable>  getAllExposedVariables(){
		Set<Variable> expVars=new HashSet<Variable>();
		if(solutionModifier!=null){
			if(solutionModifier.getHavingClause()!=null)
				for(Expression e : solutionModifier.getHavingClause().getConstraints())
					expVars.addAll(e.gatherVariables());
			if(solutionModifier.getGroupClause() !=null)
				for(Expression e : solutionModifier.getGroupClause().getConditions())
					expVars.addAll(e.gatherVariables());
			if(solutionModifier.getOrderClause()!=null)
				for(OrderCondition oc : solutionModifier.getOrderClause()){
					expVars.addAll(oc.getExpression().gatherVariables());
				}	
		}
		if(projectedVariables!=null && projectedVariables.size()>0) {
			for(ProjectedVariable pv: projectedVariables){
				expVars.add(pv.getVariable());
				if(pv.getExpression()!=null)expVars.addAll(pv.getExpression().gatherVariables());
			}
		}
		else{
			return null;
		}
		return expVars;
	}
	
	public Set<String>  getAllExposedVariableStrings(){
		Set<Variable> expVars=getAllExposedVariables();
		if(expVars==null)return null;
		Set<String> expVarStrings=new HashSet<String>();
		for(Variable v:expVars)expVarStrings.add(v.getName());
		return expVarStrings;
	}

	public Set<String> getDefaultGraphIRIString(){
		return defaultGraphIRI;
	}
	
	public void addDefaultGraphIRIString( Set<String> dGIRI){
		defaultGraphIRI.addAll(dGIRI);
	}
	
	public Set<String> getNamedGraphIRIString(){
		return namedGraphIRI;
	}
	

	public void addNamedGraphIRIString(Set<String> nGIRI){
		namedGraphIRI.addAll(nGIRI);
	}
	
	public Boolean isDefaultGraphUnion(){
		if(defaultGraphIRI.size()>1)return true;
		return false;
	}
	
	public Boolean isNamedGraphUnion(){
		if(namedGraphIRI.size()>1)return true;
		return false;
	}
	
	public GroupCondition getGroupByClause()
	{
		return solutionModifier.getGroupClause();
	}
	
	public HavingCondition getHavingClause()
	{
		return solutionModifier.getHavingClause();
	}
	
	public boolean  hasOrderClause()
	{
		if(solutionModifier!=null && solutionModifier.getOrderClause().size()>0)return true;
		else return false;
	}

	public Set<BinaryUnion<Variable, IRI>> getEmptyPatternGraphRestrictions(){
		return emptyPatternGraphRestrictions;
	}
	
	public Set<Expression> getEmptyPatternFilters(){
		return emptyPatternFilters;
	}
	
	public Set<Expression> getConstantFilters(){
		return constantFilters;
	}
	
	public boolean hasEmptyGraphPattern(){
		return hasEmptyGraphPattern;
	}
	
	public void setGraphRestrictions(Set<BinaryUnion<Variable, IRI>> gR){
		emptyPatternGraphRestrictions=gR;
	}
	
	public Set<BinaryUnion<Variable, IRI>> getGraphRestrictions(){
		return emptyPatternGraphRestrictions;
	}
	
	public Set<Variable> getLiteralBoundVariables(){
		return literalBoundVariables;
	}
	
	public void setLiteralBoundVariables(Set<Variable> lBV){
		literalBoundVariables=lBV;
	}
	
	public Set<Variable> getOptionallyExtendedVariables(){
		return optionallyExtendedVars;
	}
	
	public void setOptionallyExtendedVariables(Set<Variable> oEV){
		optionallyExtendedVars=oEV;
	}
}
