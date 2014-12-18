package com.ibm.research.rdf.store.sparql11.model;

import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.sparql11.sqlwriter.FilterContext;

public interface Visitor {
	public String visit(Store store, FilterContext context, Expression exp);
}
