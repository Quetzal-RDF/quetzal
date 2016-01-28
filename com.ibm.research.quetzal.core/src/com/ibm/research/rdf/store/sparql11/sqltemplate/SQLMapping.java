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
 package com.ibm.research.rdf.store.sparql11.sqltemplate;


public class SQLMapping {
	String name=null;
	Object values=null;
	String separator=null;
	
	public SQLMapping(String name, Object values, String separator) {
		this.name = name;
		this.values = values;
	}

	public String getName() {
		return name;
	}

	public Object getValues() {
		return values;
	}
	
	public String toString() {
		return name + "->" + values;
	}
}
