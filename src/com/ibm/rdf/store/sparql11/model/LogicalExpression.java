package com.ibm.rdf.store.sparql11.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.rdf.store.runtime.service.types.TypeMap;

/**
 * models an AND or OR between expressions
 */
public class LogicalExpression extends Expression {

	public static enum ELogicalType { AND, OR };
	private List<Expression> components = new ArrayList<Expression>();
	public LogicalExpression(ELogicalType ltype) {
		super((ltype == ELogicalType.AND) ? EExpressionType.AND : EExpressionType.OR);
	}
	public LogicalExpression(Collection<? extends Expression> ex, ELogicalType ltype) {
		this(ltype);
		if(ex != null) components.addAll(ex);
	}
	public List<Expression> getComponents() {
		return components;
	}
	
	public List<Expression> getSubExpressions() {
		return components;
	}
	
	public void addComponent(Expression e) {
		components.add(e);
	}
	@Override
	public Short getReturnType() {
		return TypeMap.BOOLEAN_ID;
	}
	
	public TypeMap.TypeCategory getTypeRestriction(Variable v){
		
		TypeMap.TypeCategory returnType=TypeMap.TypeCategory.NONE;
		for(Expression e : components){
			if(!e.gatherVariables().contains(v)) continue;
			returnType=e.getTypeRestriction(v);
		}
		return returnType;
	}
	
	
	public short getTypeEquality(Variable v) {
		short returnType=TypeMap.NONE_ID;
		for(Expression e : components){
			if(!e.gatherVariables().contains(v)) continue;
			returnType=e.getTypeEquality(v);
		}
		return returnType;
	}
	
	
	
	public String toString() {
		String separator = (getType() == EExpressionType.OR) ? "||" : "&&";
		StringBuilder sb = new StringBuilder();
		
		sb.append("(");
		for(int i = 0; i < components.size(); i++) {
			if(i > 0) sb.append(" " + separator + " ");
			Expression e = components.get(i);
			sb.append(e.toString());
		}
		sb.append(")");
		
		return sb.toString();
	}
	
	@Override
	public String getStringWithVarName() {
		String separator = (getType() == EExpressionType.OR) ? "OR" : "AND";
		StringBuilder sb = new StringBuilder();
		
		sb.append("(");
		for(int i = 0; i < components.size(); i++) {
			if(i > 0) sb.append(" " + separator + " ");
			Expression e = components.get(i);
			sb.append(e.getStringWithVarName());
		}
		sb.append(")");
		
		return sb.toString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((components == null) ? 0 : components.hashCode());
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
		LogicalExpression other = (LogicalExpression) obj;
		if (components == null) {
			if (other.components != null)
				return false;
		} else if (!components.equals(other.components))
			return false;
		return true;
	}
	/* (non-Javadoc)
	 * @see com.ibm.rdf.store.sparql11.model.Expression#renamePrefixes(java.lang.String, java.util.Map, java.util.Map)
	 */
	@Override
	public void renamePrefixes(String base, Map<String, String> declared,
			Map<String, String> internal) {
		for(Expression e: components) e.renamePrefixes(base, declared, internal);
	}
	
	@Override
	public void reverseIRIs() {
		for(Expression e: components) e.reverseIRIs();
	}
	
	/* (non-Javadoc)
	 * @see com.ibm.rdf.store.sparql11.model.Expression#gatherBlankNodes()
	 */
	@Override
	public Set<BlankNodeVariable> gatherBlankNodes() {
		Set<BlankNodeVariable> ret = new HashSet<BlankNodeVariable>();
		for(Expression e: components) ret.addAll(e.gatherBlankNodes());
		return ret;
	}
	/* (non-Javadoc)
	 * @see com.ibm.rdf.store.sparql11.model.Expression#gatherVariables()
	 */
	@Override
	public Set<Variable> gatherVariables() {
		Set<Variable> ret = new HashSet<Variable>();
		for(Expression e: components) ret.addAll(e.gatherVariables());
		return ret;
	}
	@Override
	public Set<Variable> getVariables() {
		return Collections.emptySet();
	}
	/* (non-Javadoc)
	 * @see com.ibm.rdf.store.sparql11.model.Expression#traverse(com.ibm.rdf.store.sparql11.model.IExpressionTraversalListener)
	 */
	@Override
	public void traverse(IExpressionTraversalListener l) {
		l.startExpression(this);
		for(Expression e: components) e.traverse(l);
		l.endExpression(this);
	}
	
	public boolean containsEBV(){
		boolean retType=false;
		for(Expression e: components){
			if(e.containsEBV())retType=true;
		}
		return retType;
	}

	public boolean containsBound(){
		boolean retType=false;
		for(Expression e: components){
			if(e.containsBound())retType=true;
		}
		return retType;
	}
	public boolean containsNotBound(){
		boolean retType=false;
		for(Expression e: components){
			if(e.containsNotBound())retType=true;
		}
		return retType;
	}
	
	public boolean containsCast(Variable v){
		boolean retType=false;
		for(Expression e: components){
			if(e.containsCast(v))retType=true;
		}
		return retType;
	}
}
