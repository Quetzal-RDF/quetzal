package com.ibm.rdf.store.sparql11.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GroupCondition {
	private List<Expression> expressions;
	
	public GroupCondition() {
		expressions = new ArrayList<Expression>();
	}

	public List<Expression> getConditions() {
		return expressions;
	}
	
	public void addCondition(Expression e) {
		expressions.add(e);
	}
	
	public void renamePrefixes(String base, Map<String, String> declared, Map<String, String> internal) {
		for(int i = 0; i < expressions.size(); i++) {
			expressions.get(i).renamePrefixes(base, declared, internal);
		}
	}
	
	@Override
	public String toString() {
		if (expressions.size() == 0) return "";
		
		StringBuilder sb = new StringBuilder();
		sb.append("GROUP BY ");
		for(int i = 0; i < expressions.size(); i++) {
			sb.append(expressions.get(i).toString());
		}
		sb.append("\n");
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		if (expressions.size() == 0) return prime * result;
		
		for(int i = 0; i < expressions.size(); i++) {
			result = prime * result + expressions.get(i).hashCode();
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
		GroupCondition other = (GroupCondition) obj;
		if (!expressions.equals(other.expressions))
			return false;
		return true;
	}
}
