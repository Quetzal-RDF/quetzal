package com.ibm.research.rdf.store.hashing;

public class HashingException extends Exception {
	private static final long serialVersionUID = -2107064965806219851L;
	public HashingException() {}
	public HashingException(String message) { super(message); }
	public HashingException(Throwable cause) { super(cause); }
	public HashingException(String message, Throwable cause) { super(message, cause); }
}
