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
 package com.ibm.research.rdf.store.sparql11.model;

import static com.ibm.research.rdf.store.sparql11.model.Pattern.EPatternSetType.SUBSELECT;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.research.rdf.store.sparql11.planner.Planner;
import com.ibm.research.utils.OCUtils;
import com.ibm.wala.util.collections.HashSetFactory;

public class SubSelectPattern extends Pattern 
{
	private SelectClause selectClause;
	private Pattern graphPattern;
	private SolutionModifiers solutionModifier;

	public SubSelectPattern() {
		super(EPatternSetType.SUBSELECT);
		selectClause = new SelectClause();
		graphPattern = null;
		solutionModifier = null;
	}


	@Override
	public Set<BlankNodeVariable> gatherBlankNodes() {
		return graphPattern.gatherBlankNodes();
	}

	@Override
	public Set<Pattern> gatherSubPatterns(
			boolean includeOptionals) {
		Set<Pattern> ps = HashSetFactory.make(graphPattern.gatherSubPatterns());
		ps.add(this);
		return ps;
		
	}
	
	@Override
	public Set<Pattern> gatherSubPatternsExcluding(Pattern except, boolean includeOptionals) {
		if (except.equals(this)) {
			return Collections.emptySet();
		} else {
			return graphPattern.gatherSubPatternsExcluding(except, includeOptionals);
		}
	}

	@Override
	public Set<Pattern> getSubPatterns(
			boolean includeOptionals) {
		return graphPattern.getSubPatterns(includeOptionals);
	}

	@Override
	public Set<Variable> gatherVariables() {
		Set<Variable> ret = graphPattern.gatherVariables();
		ret.addAll(getVariables());
		return ret;
	}

	@Override
	public Set<Variable> gatherVariablesWithOptional() {
		return graphPattern.gatherVariablesWithOptional();
	}

	public Set<Variable> getVariables() {
		List<ProjectedVariable> pvList=gatherRealProjectedVariables();
		if(pvList!=null && pvList.size()>0){
			Set<Variable> allVars=new LinkedHashSet<Variable>();
			for(ProjectedVariable pv :pvList){
				allVars.add(pv.getVariable());
				if (pv.getExpression() != null) {
					allVars.addAll(pv.getExpression().gatherVariables());
				}
			}
			return allVars;
		} else {
			return Collections.emptySet();
		}
	}
	
	public Set<Variable> gatherProjectedVariables() {
		Set<Variable> allVars=new LinkedHashSet<Variable>();
		List<ProjectedVariable> pvList=selectClause.getProjectedVariables();
		if(pvList!=null && pvList.size()>0){
			for(ProjectedVariable pv :pvList){
				allVars.add(pv.getVariable());
			}
			return allVars;
		}
		else{
			allVars.addAll(Planner.gatherInScopeVariables(graphPattern));
			if (graphPattern.getOptionalPatterns()!=null) {
				for (Pattern op : graphPattern.getOptionalPatterns()) {
					allVars.addAll(Planner.gatherInScopeVariables(op));
				}
			}
			return allVars;
		}
		
	}
	
	public List<ProjectedVariable> gatherRealProjectedVariables() {
		List<ProjectedVariable> pvList=selectClause.getProjectedVariables();
		if(pvList!=null && pvList.size()>0){
			return pvList;
		}
		else{
			Set<Variable> vars= gatherProjectedVariables();
			pvList = new LinkedList<ProjectedVariable>();
			for (Variable var : vars) {
				pvList.add(new ProjectedVariable(var.getName()));
			}
			return pvList;
		}
	}

	public Set<Variable> gatherIRIBoundProjectedVariables() {
		Set<Variable> allVars=new LinkedHashSet<Variable>();
		Set<Variable> IRIBoundVars=this.gatherIRIBoundVariables();
		List<ProjectedVariable> pvList=selectClause.getProjectedVariables();
		if(pvList!=null && pvList.size()>0){
			for(ProjectedVariable pv :pvList){
				if(pv.getExpression()==null && IRIBoundVars.contains(pv.getVariable()))
					allVars.add(pv.getVariable());
			}
			return allVars;
		}
		else{
			return graphPattern.gatherIRIBoundVariables();
		}
		
	}
	
	
	@Override
	public int getNumberTriples() {
		return graphPattern.getNumberTriples();
	}

	@Override
	public boolean isEmpty() {
		return graphPattern.isEmpty();
	}
	/* -- end of pattern methods -- */

	public SelectClause getSelectClause() {
		return this.selectClause;
	}
	
	public Pattern getPattern() {
		return graphPattern;
	}
	
	public void setSelectClause(SelectClause sc)  {
		this.selectClause = sc;
	}
	
	public Pattern getGraphPattern() {
		return graphPattern;
	}

	public void setGraphPattern(Pattern graphPattern) {
		this.graphPattern = graphPattern;
	}
	
	private Set<Variable> getProjectedVariables() {
		Set<Variable> ret = HashSetFactory.make();
		for (ProjectedVariable pv: gatherRealProjectedVariables()) {
			ret.add(pv.getVariable());
		}
		return ret;
	}
	private Pattern getTopAncestor() {
		Pattern parent = getParent();
		if (parent ==null) {
			return null;
		}
		Pattern previousParent = parent;
		while ((parent=previousParent.getParent())!=null) {
			previousParent = parent;
		}
		return previousParent;
	}
	public void setGraphRestriction(BinaryUnion<Variable, IRI> graphRestriction) {
		//if (this.graphRestriction == null) 
		{
			if (graphRestriction!=null && graphRestriction.isFirstType() 
			&& gatherVariables().contains(graphRestriction.getFirst())
			&& !getProjectedVariables().contains(graphRestriction.getFirst())) {
				// graph restriction variable already used in the local graph pattern
				// and is not visible outside
				// we need to
				 // 1) use a new variable v (not used in this pattern or its ancestors)  for the graph restriction
				 // 2) project v
				 // 3) set the graph restriction to v
				 // 4) add in this subselect pattern a filter ?v = ?graphRestriction
				 Set<Variable> vars = HashSetFactory.make();
				 Pattern top = getTopAncestor();
				 if (top!=null) {
					 vars.addAll(Planner.gatherInScopeVariables(top));
					 if (top.getOptionalPatterns()!=null) {
						 for (Pattern op: top.getOptionalPatterns()) {
							 vars.addAll(Planner.gatherInScopeVariables(op));
						 }
					 }
				 }
				 vars.addAll(gatherVariables());
				 String prefix = "graphRestrictionGeneratedVar_";
				 int id = OCUtils.nextAvailableSuffixVariable(OCUtils.getVariables(vars), prefix);
				 Variable v = new Variable(prefix+id);
				 this.graphRestriction = new BinaryUnion<Variable, IRI>();
				 this.graphRestriction.setFirst(v);
				 
				 if (selectClause.getProjectedVariables() == null || selectClause.getProjectedVariables().isEmpty()) {
					 for (ProjectedVariable pv: gatherRealProjectedVariables()) {
						 selectClause.addProjectedVariable(pv);
					 }
				 }
				 selectClause.addProjectedVariable(new ProjectedVariable(v.getName()));
				 //getParent().addFilter(new RelationalExpression(new VariableExpression(graphRestriction.getFirst().getName()),
				 //		 new VariableExpression(v.getName()), ERelationalOp.EQUAL));
				
			} else {
				this.graphRestriction = graphRestriction;
			}
		}
		
	}
	public void pushGraphRestrictions() {
		if (graphPattern!=null) {
			if (graphRestriction != null) {
				graphPattern.setGraphRestriction(graphRestriction);
			}
			pushGraphRestrictions(Collections.singletonList(graphPattern));
		}
		super.pushGraphRestrictions();
	}
	
	
	public SolutionModifiers getSolutionModifier() {
		return solutionModifier;
	}

	public void setSolutionModifier(SolutionModifiers solutionModifier) {
		this.solutionModifier = solutionModifier;
	}
	
	public void renamePrefixes(String base, Map<String, String> declared, Map<String, String> internal) {
		if(graphPattern != null) graphPattern.renamePrefixes(base, declared, internal);
		if(solutionModifier != null) solutionModifier.renamePrefixes(base, declared, internal);
	}
	
	public void replaceFilterBindings() {
		if(graphPattern != null) graphPattern.replaceFilterBindings();
	}
	
	
	public Set<Variable> getPatternVariables() { return (graphPattern == null) ? null : graphPattern.gatherVariables(); }
	
	public Set<Variable> getAllPatternVariables() { return (graphPattern == null) ? null : graphPattern.gatherVariablesWithOptional(); }
	
	public Set<Variable> gatherIRIBoundVariables() { return (graphPattern == null) ? null : graphPattern.gatherIRIBoundVariables(); }
	
	public Set<Variable> gatherVariablesInTransitiveClosure() { return (graphPattern == null) ? null : graphPattern.gatherVariablesInTransitiveClosure(); }

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(selectClause.toString());
		sb.append("\n");
		if (graphPattern != null)
			sb.append("WHERE " + graphPattern.toString() + "\n");
		if(solutionModifier != null)
			sb.append(solutionModifier.toString());
		return sb.toString();
	}

	@Override
	public EPatternSetType getType() {
		return SUBSELECT;
	}

	
	/*
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (selectClause.hashCode());
		result = prime * result
				+ ((graphPattern == null) ? 0 : graphPattern.hashCode());	
		result = prime * result
				+ ((solutionModifier == null) ? 0 : solutionModifier.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SubSelectPattern other = (SubSelectPattern) obj;
		if (graphPattern == null) {
			if (other.graphPattern != null)
				return false;
		} else if (!graphPattern.equals(other.graphPattern))
			return false;
		if (selectClause == null) {
			if (other.selectClause != null)
				return false;
		} else if (!selectClause.equals(other.selectClause))
			return false;
		if (solutionModifier == null) {
			if (other.solutionModifier != null)
				return false;
		} else if (!solutionModifier.equals(other.solutionModifier))
			return false;
		return true;
	}
	*/
	
	public Set<Variable> gatherOptionalVariablesWithMultipleBindings(){
		return new HashSet<Variable>();
	}

	@Override
	public void reverse() {
	}
}
