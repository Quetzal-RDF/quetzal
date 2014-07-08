package com.ibm.rdf.store.jena.impl;

import java.util.Iterator;
import java.util.Map;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class DB2QuerySolutionImpl extends QuerySolutionMap implements QuerySolution {
	public DB2QuerySolutionImpl(Map<String, RDFNode> resultMap) {
		Iterator<String> variables = resultMap.keySet().iterator();
		
		while (variables.hasNext()) {
			String variable = variables.next();
			
			add (variable, resultMap.get(variable));
		}		
	}
}