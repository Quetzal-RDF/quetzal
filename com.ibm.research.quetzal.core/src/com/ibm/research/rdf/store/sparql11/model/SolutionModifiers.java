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

import com.ibm.wala.util.collections.HashSetFactory;

/**
 * models order by, offset and lmit clauses
 */
public class SolutionModifiers {
	private GroupCondition groupClause = null;
	private HavingCondition havingClause = null;
	private List<OrderCondition> orderClause = new ArrayList<OrderCondition>();
	private LimitOffsetClauses limitOffset = null;
	
	public SolutionModifiers() {}
	
	public SolutionModifiers(Collection<? extends OrderCondition> o) {		
		if(o != null) orderClause.addAll(o);
	}
	
	public SolutionModifiers(Collection<? extends OrderCondition> o, LimitOffsetClauses loc) {
		this(o);
		limitOffset = loc;
	}

	public GroupCondition getGroupClause() {
		return groupClause;
	}
	
	public void setGroupClause(GroupCondition gc) {
		this.groupClause = gc;
	}
	
	public HavingCondition getHavingClause() {
		return havingClause;
	}
	
	public void setHavingClause(HavingCondition gc) {
		this.havingClause = gc;
	}
	
	public List<OrderCondition> getOrderClause() {
		return orderClause;
	}
	
	public void setOrderClause(List<OrderCondition> oc) {
		this.orderClause = oc;
	}

	public LimitOffsetClauses getLimitOffset() {
		return limitOffset;
	}
	
	public void setLimitOffset(LimitOffsetClauses lc) {
		this.limitOffset = lc;
	}

	public void renamePrefixes(String base, Map<String, String> declared, Map<String, String> internal) {
		for(OrderCondition oc: orderClause) oc.renamePrefixes(base, declared, internal);
		if (groupClause != null) groupClause.renamePrefixes(base, declared, internal);
		if (havingClause != null) havingClause.renamePrefixes(base, declared, internal);
		
	}
	
	public void reverseIRIs() {
		for(OrderCondition oc: orderClause) oc.reverseIRIs();
	}
	
	public Set<Variable> gatherVariables() {
		Set<Variable> result = HashSetFactory.make();
		if (groupClause != null) {
			for(Expression e : groupClause.getConditions()) {
				result.addAll(e.gatherVariables());
			}
		}
		if (havingClause != null) {
			for(Expression e : havingClause.getConstraints()) {
				result.addAll(e.gatherVariables());
			}
		}
		if (orderClause != null) {
			for(OrderCondition c : orderClause) {
				result.addAll(c.getExpression().gatherVariables());
			}
		}
		return result;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (groupClause != null)  sb.append(groupClause.toString());
		if (havingClause != null) sb.append(havingClause.toString());
		if(orderClause != null && !orderClause.isEmpty()) {
			sb.append("ORDER BY ");
			for(OrderCondition oc: orderClause) 
				sb.append(oc.toString() + " ");
			sb.append("\n");
		}
		if (limitOffset != null) {
			sb.append(limitOffset.toString());
			sb.append("\n");
		}
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((limitOffset == null) ? 0 : limitOffset.hashCode());
		result = prime * result
				+ ((orderClause == null) ? 0 : orderClause.hashCode());
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
		SolutionModifiers other = (SolutionModifiers) obj;
		if (limitOffset == null) {
			if (other.limitOffset != null)
				return false;
		} else if (!limitOffset.equals(other.limitOffset))
			return false;
		if (orderClause == null) {
			if (other.orderClause != null)
				return false;
		} else if (!orderClause.equals(other.orderClause))
			return false;
		return true;
	}
}
