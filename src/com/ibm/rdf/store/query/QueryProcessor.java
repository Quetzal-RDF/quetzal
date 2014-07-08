package com.ibm.rdf.store.query;

import com.ibm.rdf.store.runtime.service.types.LiteralInfoResultSet;


public interface QueryProcessor {

	public Query getQuery();
	
	public LiteralInfoResultSet runTestSQL(String querySQL);
	
	public LiteralInfoResultSet execSelect() ;
	
	public boolean execAsk() ;
	
	public LiteralInfoResultSet execDescribe() ;
	
	public LiteralInfoResultSet execConstruct() ;
	
	
}
