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
 * models a describe query
 */
public class DescribeQuery {
	
	private List<BinaryUnion<Variable, IRI>> resources; // if this is null or empty, then the query is DESCRIBE *
	private List<DatasetClause> datasetClauses;
	private Pattern pattern;
	private SolutionModifiers solutionModifier;
	
	public DescribeQuery() {
		resources = new ArrayList<BinaryUnion<Variable,IRI>>();
		datasetClauses = new ArrayList<DatasetClause>();
		pattern = null;
		solutionModifier = null;
	}

	public Pattern getPattern() {
		return pattern;
	}

	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}

	public SolutionModifiers getSolutionModifier() {
		return solutionModifier;
	}

	public void setSolutionModifier(SolutionModifiers solutionModifier) {
		this.solutionModifier = solutionModifier;
	}

	public List<BinaryUnion<Variable, IRI>> getResources() {
		return resources;
	}

	public List<DatasetClause> getDatasetClauses() {
		return datasetClauses;
	}
	
	public void renamePrefixes(String base, Map<String, String> declared, Map<String, String> internal) {
		for(DatasetClause dc: datasetClauses) dc.renamePrefixes(base, declared, internal);
		if(pattern != null) pattern.renamePrefixes(base, declared, internal);
		for(BinaryUnion<Variable, IRI> bu: resources)
			if(bu.isSecondType()) bu.getSecond().rename(base, declared, internal);
		if(solutionModifier != null) solutionModifier.renamePrefixes(base, declared, internal);
	}

	public void reverseIRIs() {
		for(DatasetClause dc: datasetClauses) dc.reverseIRIs();
		if(pattern != null) pattern.reverseIRIs();
		for(BinaryUnion<Variable, IRI> bu: resources)
			if(bu.isSecondType()) bu.getSecond().reverse();
		if(solutionModifier != null) solutionModifier.reverseIRIs();
	}
	
	public Set<Variable> getPatternVariables() { return (pattern == null) ? null : pattern.gatherVariables(); }	
	
	public Set<Variable> getAllPatternVariables() { return (pattern == null) ? null : pattern.gatherVariablesWithOptional(); }	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("DESCRIBE ");
		if((resources != null) && !resources.isEmpty()) {
			for(BinaryUnion<Variable, IRI> bu: resources) {
				if (bu.isFirstType()) {
					sb.append(bu.toString() + "\n");
				} else {
					sb.append("<"+bu.toString() + ">\n");
				}
			}
			sb.append("\n");
		} else sb.append("*\n");
		for(DatasetClause dc: datasetClauses) sb.append(dc + "\n");
		if(pattern != null) sb.append(pattern.toString() + "\n");
		if(solutionModifier != null) sb.append(solutionModifier.toString());
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((datasetClauses == null) ? 0 : datasetClauses.hashCode());
		result = prime * result + ((pattern == null) ? 0 : pattern.hashCode());
		result = prime * result
				+ ((resources == null) ? 0 : resources.hashCode());
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
		DescribeQuery other = (DescribeQuery) obj;
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
		if (resources == null) {
			if (other.resources != null)
				return false;
		} else if (!resources.equals(other.resources))
			return false;
		if (solutionModifier == null) {
			if (other.solutionModifier != null)
				return false;
		} else if (!solutionModifier.equals(other.solutionModifier))
			return false;
		return true;
	}
}
