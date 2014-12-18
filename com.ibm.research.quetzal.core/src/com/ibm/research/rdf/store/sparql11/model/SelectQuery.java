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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * models a select query
 */
public class SelectQuery {
	private SelectClause selectClause;
	private List<DatasetClause> datasetClauses;
	private Pattern graphPattern;
	private SolutionModifiers solutionModifier;
	
	public SelectQuery() {
		selectClause = new SelectClause();
		datasetClauses = new ArrayList<DatasetClause>();
		graphPattern = null;
		solutionModifier = null;
		ProjectedVariable.resetId();
	}

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

	public SolutionModifiers getSolutionModifier() {
		return solutionModifier;
	}

	public void setSolutionModifier(SolutionModifiers solutionModifier) {
		this.solutionModifier = solutionModifier;
	}

	public List<DatasetClause> getDatasetClauses() {
		return datasetClauses;
	}
	
	public void setDatasetClauses(List<DatasetClause> dcl) {
		datasetClauses = dcl;
	}
	
	public void renamePrefixes(String base, Map<String, String> declared, Map<String, String> internal) {
		for(DatasetClause dc: datasetClauses) dc.renamePrefixes(base, declared, internal);
		if(graphPattern != null) graphPattern.renamePrefixes(base, declared, internal);
		if(solutionModifier != null) solutionModifier.renamePrefixes(base, declared, internal);
		selectClause.renamePrefixes(base, declared, internal);
		
	}
	
	public void reverseIRIs() {
		for(DatasetClause dc: datasetClauses) dc.reverseIRIs();
		if(graphPattern != null) graphPattern.reverseIRIs();
		if(solutionModifier != null) solutionModifier.reverseIRIs();
	}
	
	public Set<Variable> getPatternVariables() { return (graphPattern == null) ? null : graphPattern.gatherVariables(); }
	
	public Set<Variable> getAllPatternVariables() { return (graphPattern == null) ? null : graphPattern.gatherVariablesWithOptional(); }
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(selectClause.toString());
		sb.append("\n");
		for(DatasetClause dc: datasetClauses) 
			sb.append(dc.toString() + "\n");
		if (graphPattern != null)
			sb.append("WHERE {\n" + graphPattern.toString() + "}\n");
		if(solutionModifier != null)
			sb.append(solutionModifier.toString());
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (selectClause.hashCode());
		result = prime * result
				+ ((datasetClauses == null) ? 0 : datasetClauses.hashCode());
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
		SelectQuery other = (SelectQuery) obj;
		if (datasetClauses == null) {
			if (other.datasetClauses != null)
				return false;
		} else if (!datasetClauses.equals(other.datasetClauses))
			return false;
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
}
