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

import org.antlr.runtime.tree.CommonTree;

import com.ibm.research.rdf.store.sparql11.XTree;
import com.ibm.research.rdf.store.sparql11.model.Expression.EExpressionType;
import com.ibm.wala.util.collections.HashMapFactory;
import com.ibm.wala.util.collections.HashSetFactory;

/*FUNCBODY
	(
	(f=STRING_LITERAL2 {$fb.setFlag(0); $fb.setBody(f); } )
	| 
	(p=groupGraphPattern[true] {$fb.setFlag(1); $fb.setBody(p); } )
	)
*/

public class FunctionBody {
	
	private int flag;
	private String strBody;
	private Pattern gpBody;
	
	public FunctionBody()
	{
		flag = -1;
		strBody = "";
		gpBody = null;
	}
	
	public void setFlag(int f) {
		flag = f;
	}
	
	public int getFlag() {
		return flag;
	}
	
	public void setBody(String s) {
		strBody = s;
	}
	
	//modified by wensun
	public void setBody(CommonTree s) {
		strBody = s.getText();
	}
	
	public void setBody(Pattern p) {
		gpBody = p;
	}
	
	public Pattern getGraphPatternBody() {
		return gpBody;
	}
	
	public String getStringBody() {
		return strBody;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{ ");
		if(flag == 0) sb.append(strBody);
		else if(flag == 1) sb.append(gpBody.toString());
		sb.append(" }");
		return sb.toString();
	}
}
