package com.ibm.rdf.store.sparql11.model;

import com.ibm.rdf.store.Store;
import com.ibm.rdf.store.sparql11.sqlwriter.FilterContext;

public interface Visitor {
	public String visit(Store store, FilterContext context, Expression exp);
}
