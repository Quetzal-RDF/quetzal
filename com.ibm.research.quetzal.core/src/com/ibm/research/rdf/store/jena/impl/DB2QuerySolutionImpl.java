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
 package com.ibm.research.rdf.store.jena.impl;

import java.util.Iterator;
import java.util.Map;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.QuerySolutionMap;
import org.apache.jena.rdf.model.RDFNode;

public class DB2QuerySolutionImpl extends QuerySolutionMap implements QuerySolution {
	public DB2QuerySolutionImpl(Map<String, RDFNode> resultMap) {
		Iterator<String> variables = resultMap.keySet().iterator();
		
		while (variables.hasNext()) {
			String variable = variables.next();
			
			add (variable, resultMap.get(variable));
		}		
	}
}