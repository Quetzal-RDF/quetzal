package com.ibm.rdf.store.jena;

public class RdfStoreException extends RuntimeException {
	
	public RdfStoreException(String msg) {
		super(msg);
	}
	
	public RdfStoreException() {
		super();
	}

	public RdfStoreException(String string, Exception e) {
		super(string,e);
	}

	private static final long serialVersionUID = 1L;

}
