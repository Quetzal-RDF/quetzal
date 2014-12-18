/*******************************************************************************
 * Licensed Materials - Property of IBM
 * (c) Copyright IBM Corporation 2008. All Rights Reserved.
 * 
 * Note to U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp. 
 *******************************************************************************/


package com.ibm.research.owlql.rule;


/**
 * a variable
 * @author <a href="mailto:achille@us.ibm.com">Achille Fokoue</a>
 *
 */
public class VariableExpr extends Expr{

	
	private String varName;
	
	public VariableExpr(String varName) {
		super();
		this.varName = varName;
	}

	@Override
	public boolean isConstant() {
		return false;
	}
	@Override
	public boolean isVariable() {
		return true;
	}
	public String getName() {
		return varName;
	}

	public void setName(String varName) {
		this.varName = varName;
	}
	
	@Override
	public ConstantExpr asConstant() {
		return null;
	}

	@Override
	public VariableExpr asVariable() {
		return this;
	}

	public boolean equals(Object o) {
		if (o==this) {
			return true;
		} 
		if (o instanceof VariableExpr) {
			return varName.equals(((VariableExpr)o).getName());
		}
		return false;
	}
	
	public int hashCode() {
		return varName.hashCode();
	}

	public String toString() {
		return varName;
	}

	@Override
	public VariableExpr clone() {
		return new VariableExpr(varName);
	}
	
}
