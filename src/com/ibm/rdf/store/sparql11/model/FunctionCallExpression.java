package com.ibm.rdf.store.sparql11.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ibm.rdf.store.runtime.service.types.TypeMap;

/**
 * a function call expression
 */
public class FunctionCallExpression extends Expression {

	private FunctionCall call;
	
	/**
	 * @param type
	 */
	public FunctionCallExpression(FunctionCall fc) {
		super(EExpressionType.FUNC_CALL);
		call = fc;
	}

	public FunctionCall getCall() {
		return call;
	}
	
	@Override
	public Short getReturnType() {
		return call.getReturnType();
	}

	public TypeMap.TypeCategory getTypeRestriction(Variable v){
		
		if (call.getFunction().getValue().equals(FunctionCall.STARTS_WITH) || call.getFunction().getValue().equals(FunctionCall.ENDS_WITH)) {
			return TypeMap.TypeCategory.STRING;
		} else if (call.getFunction().getValue().equals(FunctionCall.XSD_DATETIME)) {
			return TypeMap.TypeCategory.DATETIME;
		} else if (call.getFunction().getValue().equals(FunctionCall.XSD_DOUBLE) || call.getFunction().getValue().equals(FunctionCall.XSD_FLOAT)
				|| call.getFunction().getValue().equals(FunctionCall.XSD_DECIMAL) || call.getFunction().getValue().equals(FunctionCall.XSD_INTEGER)) {
			return TypeMap.TypeCategory.NUMERIC;
		}

		return TypeMap.TypeCategory.NONE;
	}
	
	
	public short getTypeEquality(Variable v) {
		return TypeMap.NONE_ID;
	}
	
	
	@Override
	public String toString() { return call.toString(); }

	@Override
	public String getStringWithVarName() { return call.toString(); }
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((call == null) ? 0 : call.hashCode());
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
		FunctionCallExpression other = (FunctionCallExpression) obj;
		if (call == null) {
			if (other.call != null)
				return false;
		} else if (!call.equals(other.call))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see com.ibm.rdf.store.sparql11.model.Expression#renamePrefixes(java.lang.String, java.util.Map, java.util.Map)
	 */
	@Override
	public void renamePrefixes(String base, Map<String, String> declared,
			Map<String, String> internal) {
		call.getFunction().rename(base, declared, internal);
		for(Expression e: call.getArguments()) e.renamePrefixes(base, declared, internal);
	}
	
	@Override
	public void reverseIRIs() {
		for(Expression e: call.getArguments()) e.reverseIRIs();
	}

	/* (non-Javadoc)
	 * @see com.ibm.rdf.store.sparql11.model.Expression#gatherBlankNodes()
	 */
	@Override
	public Set<BlankNodeVariable> gatherBlankNodes() {
		Set<BlankNodeVariable> ret = new HashSet<BlankNodeVariable>();
		for(Expression e: call.getArguments()) ret.addAll(e.gatherBlankNodes());
		return ret;
	}

	/* (non-Javadoc)
	 * @see com.ibm.rdf.store.sparql11.model.Expression#gatherVariables()
	 */
	@Override
	public Set<Variable> gatherVariables() {
		Set<Variable> ret = new HashSet<Variable>();
		for(Expression e: call.getArguments()) ret.addAll(e.gatherVariables());
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
		for(Expression e: call.getArguments()) e.traverse(l);
		l.endExpression(this);
	}
	
	public boolean containsEBV(){
		return false;
	}
	
	public boolean containsBound(){		
		return false;
	}
	
	public boolean containsNotBound(){
		return false;
	}
	
	public boolean containsCast(Variable v){
		return call.hasCast(v);		
	}
}
