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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * models an ask query
 */
public class AskQuery {
	private List<DatasetClause> datasetClauses;
	private Pattern pattern;
	
	public AskQuery(Pattern pat) {
		pattern = pat;
	}
	
	public AskQuery(Collection<? extends DatasetClause> ds, Pattern pat) {
		this(pat);
		datasetClauses = new ArrayList<DatasetClause>();
		if(ds != null) datasetClauses.addAll(ds);
	}

	public List<DatasetClause> getDatasetClauses() {
		return datasetClauses;
	}

	public Pattern getPattern() {
		return pattern;
	}
	
	public void renamePrefixes(String base, Map<String, String> declared, Map<String, String> internal) {
		for(DatasetClause dc: datasetClauses) dc.renamePrefixes(base, declared, internal);
		pattern.renamePrefixes(base, declared, internal);
	}
	
	public void reverseIRIs() {
		for(DatasetClause dc: datasetClauses) dc.reverseIRIs();
		pattern.reverseIRIs();
	}
	
	public Set<Variable> getPatternVariables() { return (pattern == null) ? null : pattern.gatherVariables(); }	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ASK\n");
		for(DatasetClause dc: datasetClauses) sb.append(dc.toString() + "\n");
		sb.append(pattern.toString());
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((datasetClauses == null) ? 0 : datasetClauses.hashCode());
		result = prime * result + ((pattern == null) ? 0 : pattern.hashCode());
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
		AskQuery other = (AskQuery) obj;
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
		return true;
	}

	public void setPattern(Pattern p) {
		pattern = p;
	}
}
