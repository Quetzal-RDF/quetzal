package com.ibm.rdf.store.sparql11.model;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.ibm.rdf.store.runtime.service.types.TypeMap;

/**
 * unary expression
 */
public class UnaryExpression extends Expression {

	private Expression expression;
	private EUnaryOp operator;
	
	/**
	 * @param type
	 */
	public UnaryExpression(EUnaryOp op, Expression e) {
		super(EExpressionType.UNARY);
		operator = op;
		expression = e;
	}

	public Expression getExpression() {
		return expression;
	}

	public EUnaryOp getOperator() {
		return operator;
	}
	
	@Override
	public Short getReturnType() {
		return expression.getReturnType();
	}

	public TypeMap.TypeCategory getTypeRestriction(Variable v){
		if(!this.gatherVariables().contains(v))return TypeMap.TypeCategory.NONE;
		else return expression.getTypeRestriction(v);
	}
	
	public short getTypeEquality(Variable v) {
		if(!this.gatherVariables().contains(v))return TypeMap.NONE_ID;
		else return expression.getTypeEquality(v);
	}
	
	
	@Override
	public String toString() {
		return operator.toString() + " " + expression.toString();
	}

	@Override
	public String getStringWithVarName() {
		return operator.toString() + " " + expression.getStringWithVarName();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((expression == null) ? 0 : expression.hashCode());
		result = prime * result
				+ ((operator == null) ? 0 : operator.hashCode());
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
		UnaryExpression other = (UnaryExpression) obj;
		if (expression == null) {
			if (other.expression != null)
				return false;
		} else if (!expression.equals(other.expression))
			return false;
		if (operator != other.operator)
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see com.ibm.rdf.store.sparql11.model.Expression#renamePrefixes(java.lang.String, java.util.Map, java.util.Map)
	 */
	@Override
	public void renamePrefixes(String base, Map<String, String> declared,
			Map<String, String> internal) {
		expression.renamePrefixes(base, declared, internal);
	}
	
	@Override
	public void reverseIRIs() {
		expression.reverseIRIs();
	}

	/* (non-Javadoc)
	 * @see com.ibm.rdf.store.sparql11.model.Expression#gatherBlankNodes()
	 */
	@Override
	public Set<BlankNodeVariable> gatherBlankNodes() {
		return expression.gatherBlankNodes();
	}

	/* (non-Javadoc)
	 * @see com.ibm.rdf.store.sparql11.model.Expression#gatherVariables()
	 */
	@Override
	public Set<Variable> gatherVariables() {
		return expression.gatherVariables();
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
		expression.traverse(l);
		l.endExpression(this);
	}
	
	public boolean containsEBV(){
		
		if(expression instanceof VariableExpression && 
				((VariableExpression)expression).getExpression()==null){
			return true;
		}
		else return false;
	}
	
	public boolean containsBound(){
		
		return expression.containsBound();
	}
	
	public boolean containsNotBound(){
		if(operator==EUnaryOp.NOT)
			return expression.containsBound();
		else 
			return expression.containsNotBound();
	}
	
	public boolean containsCast(Variable v) {		
		if(expression.containsCast(v))return true;
		return false;
	}
}
