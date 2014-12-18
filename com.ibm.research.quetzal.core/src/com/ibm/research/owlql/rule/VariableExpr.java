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
