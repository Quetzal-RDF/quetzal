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

public class HavingCondition {
	private List<Expression> constraints;
	
	public HavingCondition() {
		constraints = new ArrayList<Expression>();
	}

	public List<Expression> getConstraints() {
		return constraints;
	}
	
	public void addConstraint(Expression e) {
		this.constraints.add(e);
	}
	
	public void renamePrefixes(String base, Map<String, String> declared, Map<String, String> internal) {
		for(int i = 0; i < constraints.size(); i++) {
			constraints.get(i).renamePrefixes(base, declared, internal);
		}
	}
	
	@Override
	public String toString() {
		if (constraints.size() == 0) return "";
		
		StringBuilder sb = new StringBuilder();
		sb.append("HAVING ");
		for(int i = 0; i < constraints.size(); i++) {
			sb.append(constraints.get(i).toString());
		}
		sb.append("\n");
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		if (constraints.size() == 0) return prime * result;
		
		for(int i = 0; i < constraints.size(); i++) {
			result = prime * result + constraints.get(i).hashCode();
		}
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
		HavingCondition other = (HavingCondition) obj;
		if (!constraints.equals(other.constraints))
			return false;
		return true;
	}
}
