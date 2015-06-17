package com.ibm.research.rdf.store.sparql11.model;

public interface Service {
	public QueryTripleTerm getService();
	public EServiceType getServiceType();
}

enum EServiceType {
	POST, GET
}