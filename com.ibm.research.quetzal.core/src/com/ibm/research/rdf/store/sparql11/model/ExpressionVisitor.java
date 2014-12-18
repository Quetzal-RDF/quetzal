package com.ibm.research.rdf.store.sparql11.model;

import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.sparql11.sqlwriter.FilterContext;

public interface ExpressionVisitor {

	public String visit(FilterContext context, Store store);
}
