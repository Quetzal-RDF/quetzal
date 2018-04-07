package com.ibm.research.rdf.store.utilities.io;

import java.util.Iterator;

import com.ibm.research.rdf.store.sparql11.model.QueryTripleTerm;
import com.ibm.research.rdf.store.sparql11.model.Variable;

public interface SparqlSelectResult {

	public interface Row {
	
		QueryTripleTerm get(Variable v);
		
		Iterator<Variable> variables();
		
	}
	
	Iterator<Row> rows();
	
	Iterator<Variable> variables();
	
}
