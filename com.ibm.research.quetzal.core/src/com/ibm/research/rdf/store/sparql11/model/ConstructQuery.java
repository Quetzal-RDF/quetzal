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
 * models a construct query
 */
public class ConstructQuery {
	
	private SimplePattern constructPattern;
	private List<DatasetClause> datasetClauses;
	private Pattern pattern;
	private SolutionModifiers solutionModifier;
	
	public ConstructQuery() {
		constructPattern = new SimplePattern();
		datasetClauses = new ArrayList<DatasetClause>();
		pattern = null;
		solutionModifier = null;
	}

	public void addConstructPattern(QueryTriple qt) {
		qt.expandAndAddTo(constructPattern);
	}

	public void addConstructPattern(QueryTriple2 qt) {
		qt.expandAndAddTo(constructPattern);
	}
	
	public List<QueryTriple> getConstructPattern() {
		return constructPattern.getQueryTriples();
	}

	public void setDatasetClauses(List<DatasetClause> ds) {
		datasetClauses = ds;
	}
	
	public void addDatasetClause(DatasetClause d) {
		datasetClauses.add(d);
	}
	
	public List<DatasetClause> getDatasetClauses() {
		return datasetClauses;
	}

	public Pattern getPattern() {
		return pattern;
	}

	public SolutionModifiers getSolutionModifier() {
		return solutionModifier;
	}

	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}

	public void setSolutionModifier(SolutionModifiers solutionModifier) {
		this.solutionModifier = solutionModifier;
	}

	public void renamePrefixes(String base, Map<String, String> declared, Map<String, String> internal) {
		for(DatasetClause dc: datasetClauses) dc.renamePrefixes(base, declared, internal);
		if(pattern != null) pattern.renamePrefixes(base, declared, internal);
		if(solutionModifier != null) solutionModifier.renamePrefixes(base, declared, internal);
		constructPattern.renamePrefixes(base, declared, internal);
	}

	public void reverseIRIs() {
		for(DatasetClause dc: datasetClauses) dc.reverseIRIs();
		if(pattern != null) pattern.reverseIRIs();
		if(solutionModifier != null) solutionModifier.reverseIRIs();
		constructPattern.reverseIRIs();
	}
	
	public Set<Variable> getPatternVariables() { return (pattern == null) ? null : pattern.gatherVariables(); }
	public Set<Variable> getAllPatternVariables() { return (pattern == null) ? null : pattern.gatherVariablesWithOptional(); }
	public Set<Variable> getConstructPatternVariables() { return constructPattern.gatherVariables(); }
		
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("CONSTRUCT { ");
		//for(int i = 0; i < constructPattern.size(); i++) {
		//	if(i > 0) sb.append(" . ");
			sb.append(constructPattern.toString());
		//}
		sb.append(" } \n");
		for(DatasetClause dc: datasetClauses) sb.append(dc.toString() + "\n");
		if (pattern != null) {
			sb.append("WHERE ");
			sb.append(pattern.toString());
		}
		if(solutionModifier != null) sb.append("\n" + solutionModifier.toString());
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((constructPattern == null) ? 0 : constructPattern.hashCode());
		result = prime * result
				+ ((datasetClauses == null) ? 0 : datasetClauses.hashCode());
		result = prime * result + ((pattern == null) ? 0 : pattern.hashCode());
		result = prime
				* result
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
		ConstructQuery other = (ConstructQuery) obj;
		if (constructPattern == null) {
			if (other.constructPattern != null)
				return false;
		} else if (!constructPattern.equals(other.constructPattern))
			return false;
		if (datasetClauses == null) {
			if (other.datasetClauses != null)
				return false;
		} else if (!datasetClauses.equals(other.datasetClauses))
			return false;
		if (pattern == null) {
			if (other.pattern != null)
				return false;
		} else if (!pattern.equals(other.pattern))
			return false;
		if (solutionModifier == null) {
			if (other.solutionModifier != null)
				return false;
		} else if (!solutionModifier.equals(other.solutionModifier))
			return false;
		return true;
	}
}
