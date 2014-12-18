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

public class SelectClause {
	public static enum ESelectModifier { DISTINCT, REDUCED };
	
	private ESelectModifier selectModifier = null;
	private List<ProjectedVariable> projectedVariables;	// if this list is null or empty, that means SELECT * 
	
	public SelectClause() {
		projectedVariables = new ArrayList<ProjectedVariable>();
	}

	public ESelectModifier getSelectModifier() {
		return selectModifier;
	}

	public void setSelectModifier(ESelectModifier selectModifier) {
		this.selectModifier = selectModifier;
	}

	public List<ProjectedVariable> getProjectedVariables() {
		return projectedVariables;
	}
	
	public void addProjectedVariable(ProjectedVariable var)
	{
		this.projectedVariables.add(var);
	}
		
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ");
		if(selectModifier != null) {
			switch(selectModifier) {
			case DISTINCT: sb.append("DISTINCT "); break;
			case REDUCED: sb.append("REDUCED "); break;
			}
		}
		if((projectedVariables != null) && (!projectedVariables.isEmpty())) {
			for(int i = 0; i < projectedVariables.size(); i++) {
				if(i > 0) sb.append(" ");//", ");
				sb.append("?"+projectedVariables.get(i).toString());
			}
			sb.append(" ");
		} else sb.append("* ");
		sb.append("\n");
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((projectedVariables == null) ? 0 : projectedVariables
						.hashCode());
		result = prime * result
				+ ((selectModifier == null) ? 0 : selectModifier.hashCode());
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
		SelectClause other = (SelectClause) obj;
		if (projectedVariables == null) {
			if (other.projectedVariables != null)
				return false;
		} else if (!projectedVariables.equals(other.projectedVariables))
			return false;
		return true;
	}
	
	public void renamePrefixes(String base, Map<String, String> declared, Map<String, String> internal) {
		for (ProjectedVariable pv : projectedVariables) {
			if (pv.getExpression() != null) {
				pv.getExpression().renamePrefixes(base, declared, internal);
			}
		}
	}
}
