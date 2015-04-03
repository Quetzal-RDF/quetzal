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

public class ProjectedVariable {
	private Variable name;
	private Expression exp;
	private static int id = 0;
	
	public static void resetId() 
	{
		id = 0;
	}
	
	public ProjectedVariable(String s) 
	{
		this(s, null);
	}
	
	public ProjectedVariable(String s, Expression e)
	{
		this( new Variable(s), e);
	}
	
	public ProjectedVariable(Expression e)
	{
		this("v_"+(++id), e);
		/*this.exp = e;
		id++;
		this.name = new Variable("v_"+id);*/
	}
	public ProjectedVariable(Variable v, Expression e) {
		this.exp = e;
		this.name = v;
	}
	public ProjectedVariable(Variable v) {
		this(v, null);
	}
	
	public Variable getVariable() {return this.name;}
	
	public Expression getExpression() {return this.exp;}
	
	public void setExpression(Expression e) {this.exp = e;}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((exp == null) ? 0 : exp.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		ProjectedVariable other = (ProjectedVariable) obj;
		if (exp == null) {
			if (other.exp != null)
				return false;
		} else if (!exp.equals(other.exp))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public String toString()
	{
		if ((this.exp != null) && (this.name != null)) {
			return "(" + exp.toString() + " AS " + name.getName() + ")";
		} 
		else if (this.exp != null) {
			return exp.toString();
		} 
		else if (this.name != null) {
			return name.getName();
		}
		else {
			return "";
		}
	}
}