package com.ibm.research.rdf.store.query;

public interface Query {

	// Need to move more methods from sparql11.model.Query to this interface
	
	public boolean isSelectQuery() ;
	public boolean isAskQuery() ;
	public boolean isDescribeQuery() ;
	public boolean isConstructQuery() ;
	public boolean isDistinct();
	
}
