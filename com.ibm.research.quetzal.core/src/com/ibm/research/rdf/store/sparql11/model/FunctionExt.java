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
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.research.rdf.store.sparql11.XTree;
import com.ibm.research.rdf.store.sparql11.model.Expression.EExpressionType;
import com.ibm.wala.util.collections.HashMapFactory;
import com.ibm.wala.util.collections.HashSetFactory;

public class FunctionExt {
	
	private String name;
	private String lang;
	private FunctionBody fb;
	
	private List<Variable> inVariables;
	private List<Variable> outVariables;
	
	public FunctionExt()
	{
		name = "";
		lang = "";
		fb = null;
		inVariables = new ArrayList<Variable>();
		outVariables = new ArrayList<Variable>();
	}
	
	public void setName(String s) {
		name = s;
	}
	
	public void setName(XTree s) {
		name = s.getText();
	}
	
	public String getName() {
		return name;
	}
	
	public void setLang(String s) {
		lang = s;
	}
	
	public void setLang(XTree s) {
		lang = s.getText();
	}
	
	public String getLang() {
		return lang;
	}
	
	public void setBody(FunctionBody b) {
		fb = b;
	}
	
	public FunctionBody getBody() {
		return fb;
	}
	
	public void addInVar(Variable v) {
		inVariables.add(v);
	}

	public void addInVar(String v) {
		inVariables.add(new Variable(v));
	}
	
	public List<Variable> getInVariables() {
		return inVariables;
	}

	public void addOutVar(Variable v) {
		outVariables.add(v);
	}
	
	public void addOutVar(String v) {
		outVariables.add(new Variable(v));
	}
	
	public List<Variable> getOutVariables() {
		return outVariables;
	}
		
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("FUNCTION ");
		sb.append(name+" (");
		for (Variable v : inVariables) {
			sb.append(v).append(" ");
		}
		sb.append(") -> (");
		for (Variable v : outVariables) {
			sb.append(v).append(" ");
		}
		sb.append(") LANG "+lang+" { \n");
		sb.append(fb.toString());
		sb.append("\n}\n");
		return sb.toString();
	}
}
