package com.ibm.rdf.store.sparql11.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.rdf.store.runtime.service.types.TypeMap;

public class RelationalExpression extends Expression {
	private Expression left = null;
	private Expression right = null;
	private ERelationalOp operator;
	
	public RelationalExpression() 
	{
		super(EExpressionType.RELATIONAL);
	}
	
	public RelationalExpression(Expression e) {
		super(EExpressionType.RELATIONAL);
		left = e;
	}
	
	public RelationalExpression(Expression l, Expression r, ERelationalOp op) {
		this(l);
		right = r;
		operator = op;
	}
	
	public Expression getLeft()
	{
		return this.left;
	}
	
	public void setLeft(Expression e)
	{
		this.left = e;
	}
	
	public Expression getRight()
	{
		return this.right;
	}
	
	public boolean containsCast(Variable v) {		
		if(left!=null && left.containsCast(v))return true;
		if(right!=null && right.containsCast(v))return true;
		return false;
	}
	
	public void setRight(ERelationalOp t, Expression e)
	{
		this.operator = t;
		this.right = e;
	}
	
	public ERelationalOp getOperator() 
	{
		return this.operator;
	}
	
	@Override
	public Short getReturnType() {
		return TypeMap.BOOLEAN_ID;
	}
	
	public TypeMap.TypeCategory getTypeRestriction(Variable v){
		TypeMap.TypeCategory returnType=TypeMap.TypeCategory.NONE;
		if(left.gatherVariables().contains(v)){
			returnType=left.getTypeRestriction(v);
			if(returnType==TypeMap.TypeCategory.NONE && !left.containsCast(v)) {
//				if(right instanceof ConstantExpression) {
					short rightReturnType=right.getReturnType();
					if((rightReturnType>=TypeMap.DATATYPE_NUMERICS_IDS_START &&
							rightReturnType<=TypeMap.DATATYPE_NUMERICS_IDS_END) || TypeMap.isDateTime(rightReturnType) ) {
						returnType = TypeMap.getTypeCategory(rightReturnType);
					}
//				}
			}
		}
		else if(right.gatherVariables().contains(v) ){
			returnType=right.getTypeRestriction(v);
			if(returnType==TypeMap.TypeCategory.NONE && !right.containsCast(v)) {
		//		if(left instanceof ConstantExpression){
					short leftReturnType=left.getReturnType();
					if ((leftReturnType>=TypeMap.DATATYPE_NUMERICS_IDS_START &&
							leftReturnType<=TypeMap.DATATYPE_NUMERICS_IDS_END) || (TypeMap.isDateTime(leftReturnType))) {
								returnType = TypeMap.getTypeCategory(leftReturnType);
								
							}
		//		}
			}
		}
		return returnType;
	}
	
	public short getTypeEquality(Variable v) {
		short returnType=TypeMap.NONE_ID;
		if(left.gatherVariables().contains(v)){
			if(left.getTypeRestriction(v)==TypeMap.TypeCategory.NONE && !left.containsCast(v))
				if(right instanceof ConstantExpression){
					short rightReturnType=right.getReturnType();
					if(rightReturnType>=TypeMap.STRING_ID)returnType=rightReturnType;
					if(rightReturnType==TypeMap.BOOLEAN_ID)returnType=rightReturnType;
					if(rightReturnType==TypeMap.DATETIME_ID)returnType=rightReturnType;
					if(rightReturnType>=TypeMap.USER_ID_START && rightReturnType<=TypeMap.DATATYPE_IDS_END)returnType=rightReturnType;
				}
		}
		else if(right.gatherVariables().contains(v)){
			if(right.getTypeRestriction(v)==TypeMap.TypeCategory.NONE && !right.containsCast(v))
				if(left instanceof ConstantExpression){
					short leftReturnType=left.getReturnType();
					if(leftReturnType>=TypeMap.STRING_ID)returnType=leftReturnType;
					if(leftReturnType>=TypeMap.BOOLEAN_ID)returnType=leftReturnType;
					if(leftReturnType==TypeMap.DATETIME_ID)returnType=leftReturnType;
					if(leftReturnType>=TypeMap.USER_ID_START && leftReturnType<=TypeMap.DATATYPE_IDS_END)returnType=leftReturnType;
				}
		}
		return returnType;
	}
	
	
	public String getOperatorString()
	{
		if (this.operator == ERelationalOp.EQUAL) {
			return "RDF_EQ";
		} else if (this.operator == ERelationalOp.NOT_EQUAL) {
			return "RDF_NE";
		} else if (this.operator == ERelationalOp.LESS) {
			return "RDF_LT";
		} else if (this.operator == ERelationalOp.GREATER) {
			return "RDF_GT";
		} else if (this.operator == ERelationalOp.LESS_EQUAL) {
			return "RDF_LE";
		} else if (this.operator == ERelationalOp.GREATER_EQUAL) {
			return "RDF_GE";
		}
		return "";
	}
	
	
	
	@Override
	public List<Expression> getSubExpressions() {
		LinkedList<Expression> exps = new LinkedList<Expression>();
		exps.add(left);
		exps.add(right);
		return exps;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		if (this.right == null) {
			sb.append(this.left.toString());
			return sb.toString();
		}
		
		sb.append("(");
		sb.append(this.left.toString());
		if (this.operator == ERelationalOp.EQUAL) {
			sb.append(" = ");
		} else if (this.operator == ERelationalOp.NOT_EQUAL) {
			sb.append(" != ");
		} else if (this.operator == ERelationalOp.LESS) {
			sb.append(" < ");
		} else if (this.operator == ERelationalOp.GREATER) {
			sb.append(" > ");
		} else if (this.operator == ERelationalOp.LESS_EQUAL) {
			sb.append(" <= ");
		} else if (this.operator == ERelationalOp.GREATER_EQUAL) {
			sb.append(" >= ");
		}
		sb.append(right.toString());
		sb.append(")");
		
		return sb.toString();
	}
	
	@Override
	public String getStringWithVarName() {
		StringBuilder sb = new StringBuilder();
		
		if (this.right == null) {
			sb.append(this.left.getStringWithVarName());
			return sb.toString();
		}
		
		sb.append("(");
		sb.append(this.left.getStringWithVarName());
		if (this.operator == ERelationalOp.EQUAL) {
			sb.append(" = ");
		} else if (this.operator == ERelationalOp.NOT_EQUAL) {
			sb.append(" != ");
		} else if (this.operator == ERelationalOp.LESS) {
			sb.append(" < ");
		} else if (this.operator == ERelationalOp.GREATER) {
			sb.append(" > ");
		} else if (this.operator == ERelationalOp.LESS_EQUAL) {
			sb.append(" <= ");
		} else if (this.operator == ERelationalOp.GREATER_EQUAL) {
			sb.append(" >= ");
		}
		sb.append(right.getStringWithVarName());
		sb.append(")");
		
		return sb.toString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.left.hashCode();
		if (this.right != null) {
			result = prime * result + this.right.hashCode();
		} 
		if (this.operator != null) {
			result = prime * result + this.operator.hashCode();
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
		RelationalExpression other = (RelationalExpression) obj;
		if (!this.left.equals(other.left))
			return false;
		if (this.operator != other.operator)  
			return false;
		if (this.right != null) {
			if (!this.right.equals(other.right))
				return false;
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see com.ibm.rdf.store.sparql11.model.Expression#renamePrefixes(java.lang.String, java.util.Map, java.util.Map)
	 */
	@Override
	public void renamePrefixes(String base, Map<String, String> declared,
			Map<String, String> internal) 
	{
		left.renamePrefixes(base, declared, internal);
		right.renamePrefixes(base, declared, internal);
	}
	
	@Override
	public void reverseIRIs() 
	{
		left.reverseIRIs();
		right.reverseIRIs();
	}
	
	/* (non-Javadoc)
	 * @see com.ibm.rdf.store.sparql11.model.Expression#gatherBlankNodes()
	 */
	@Override
	public Set<BlankNodeVariable> gatherBlankNodes() 
	{
		Set<BlankNodeVariable> ret = new HashSet<BlankNodeVariable>();
		ret.addAll(left.gatherBlankNodes());
		ret.addAll(right.gatherBlankNodes());
		return ret;
	}
	
	/* (non-Javadoc)
	 * @see com.ibm.rdf.store.sparql11.model.Expression#gatherVariables()
	 */
	@Override
	public Set<Variable> gatherVariables() 
	{
		Set<Variable> ret = new HashSet<Variable>();
		ret.addAll(left.gatherVariables());
		ret.addAll(right.gatherVariables());
		return ret;
	}
	
	@Override
	public Set<Variable> getVariables() {
		return Collections.emptySet();
	}

	public void traverse(IExpressionTraversalListener l) {
		l.startExpression(this);
		left.traverse(l);
		right.traverse(l);
		l.endExpression(this);
	}
	
	public String getOperatorSymbol() {
		return getOperatorSymbol(this.operator);
	}
	public static String getOperatorSymbol(ERelationalOp op)
	{
		StringBuffer sb = new StringBuffer();
		if (op == ERelationalOp.EQUAL) {
			sb.append(" = ");
		} else if (op == ERelationalOp.NOT_EQUAL) {
			sb.append(" <> ");
		} else if (op == ERelationalOp.LESS) {
			sb.append(" < ");
		} else if (op == ERelationalOp.GREATER) {
			sb.append(" > ");
		} else if (op == ERelationalOp.LESS_EQUAL) {
			sb.append(" <= ");
		} else if (op == ERelationalOp.GREATER_EQUAL) {
			sb.append(" >= ");
		}
		return sb.toString();
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
}
