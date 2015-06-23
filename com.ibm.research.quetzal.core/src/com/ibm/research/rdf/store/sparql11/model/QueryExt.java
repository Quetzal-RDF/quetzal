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

import java.util.HashMap;
import java.util.Map;

import com.ibm.research.rdf.store.sparql11.model.SelectClause.ESelectModifier;

/**
 * models a query
 */
public class QueryExt extends Query {
	
	private String origQueryString = null;
	
	public String getOrigQueryString() {
		return origQueryString;
	}
	public void setOrigQueryString(String origQueryString) {
		this.origQueryString = origQueryString;
	}

	private QueryPrologue prologue;
	private FourUnion<SelectQueryExt, AskQuery, DescribeQuery, ConstructQuery> query;
	
	public QueryExt(QueryPrologue p, SelectQueryExt s) {
		super(p, s);
		prologue = p;
		query = new FourUnion<SelectQueryExt, AskQuery, DescribeQuery, ConstructQuery>();
		query.setFirst(s);
	}
	public QueryExt(QueryPrologue p, AskQuery q) {
		super(p, q);
		prologue = p;
		query = new FourUnion<SelectQueryExt, AskQuery, DescribeQuery, ConstructQuery>();
		query.setSecond(q);
	}
	public QueryExt(QueryPrologue p, DescribeQuery q) {
		super (p, q);
		prologue = p;
		query = new FourUnion<SelectQueryExt, AskQuery, DescribeQuery, ConstructQuery>();
		query.setThird(q);
	}
	public QueryExt(QueryPrologue p, ConstructQuery q) {
		super(p, q);
		prologue = p;
		query = new FourUnion<SelectQueryExt, AskQuery, DescribeQuery, ConstructQuery>();
		query.setFourth(q);
	}

	public QueryPrologue getPrologue() {
		return prologue;
	}
	
	public Pattern getMainPattern() {
		if(isSelectQuery()) return getSelectQuery().getGraphPattern();
		else if(isAskQuery()) return getAskQuery().getPattern();
		else if(isDescribeQuery()) return getDescribeQuery().getPattern();
		else if(isConstructQuery()) return getConstructQuery().getPattern();
		else return null;
	}

	public void setMainPattern(Pattern p) {
		if(isSelectQuery()) getSelectQuery().setGraphPattern(p);
		else if(isAskQuery()) getAskQuery().setPattern(p);
		else if(isDescribeQuery()) getDescribeQuery().setPattern(p);
		else if(isConstructQuery()) getConstructQuery().setPattern(p);
	}

	public boolean isSelectQuery() { return query.isFirstType(); }
	public boolean isAskQuery() { return query.isSecondType(); }
	public boolean isDescribeQuery() { return query.isThirdType(); }
	public boolean isConstructQuery() { return query.isFourthType(); }
	
	public SelectQueryExt getSelectQuery() { return query.getFirst(); }
	public AskQuery getAskQuery() { return query.getSecond(); }
	public DescribeQuery getDescribeQuery() { return query.getThird(); }
	public ConstructQuery getConstructQuery() { return query.getFourth(); }
	
	public void expandPrefixes(Map<String, String> externalNamespaces) {
		Map<String, String> internalNamespaces = new HashMap<String, String>();
		for(String key: prologue.getPrefixes().keySet()) 
			internalNamespaces.put(key, prologue.getPrefixes().get(key).getValue());
		String base = (prologue.getBase() == null) ? null : prologue.getBase().getValue();
		if(isSelectQuery()) getSelectQuery().renamePrefixes(base, internalNamespaces, externalNamespaces);
		else if(isAskQuery()) getAskQuery().renamePrefixes(base, internalNamespaces, externalNamespaces);
		else if(isDescribeQuery()) getDescribeQuery().renamePrefixes(base, internalNamespaces, externalNamespaces);
		else if(isConstructQuery()) getConstructQuery().renamePrefixes(base, internalNamespaces, externalNamespaces);
	}
	
	public void reverseIRIs() {
		if(isSelectQuery()) getSelectQuery().reverseIRIs();
		else if(isAskQuery()) getAskQuery().reverseIRIs();
		else if(isDescribeQuery()) getDescribeQuery().reverseIRIs();
		else if(isConstructQuery()) getConstructQuery().reverseIRIs();
	}
	
	public String toString() {
		return ("\n\n"+prologue.toString() + "\n" + query.toString());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((prologue == null) ? 0 : prologue.hashCode());
		result = prime * result + ((query == null) ? 0 : query.hashCode());
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
		QueryExt other = (QueryExt) obj;
		if (prologue == null) {
			if (other.prologue != null)
				return false;
		} else if (!prologue.equals(other.prologue))
			return false;
		if (query == null) {
			if (other.query != null)
				return false;
		} else if (!query.equals(other.query))
			return false;
		return true;
	}

	public boolean isDistinct() {
		if ( !isSelectQuery() )
			return false;
		
		if ( getSelectQuery().getSelectClause().getSelectModifier().compareTo(
				ESelectModifier.DISTINCT) == 0 )
			return true;
		else
			return false;
		
	}
	
}
