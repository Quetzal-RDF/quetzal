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
import com.ibm.research.rdf.store.sparql11.XTree;

public class FunctionExt extends FunctionBase {
	
	private String lang;
	private FunctionBody fb;
	
	public FunctionExt()
	{
		name = null;
		lang = "";
		fb = null;
		inVariables = new ArrayList<Variable>();
		outVariables = new ArrayList<Variable>();
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
