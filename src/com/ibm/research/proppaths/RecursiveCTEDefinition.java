package com.ibm.research.proppaths;

public class RecursiveCTEDefinition extends CTEDefinition {

	public static String toSQLDefinition(String initializationSQL, String iterationSQL, boolean removeDuplicate) {
		StringBuffer buf = new StringBuffer();
		buf.append(initializationSQL)
		   .append(removeDuplicate?" UNION ": " UNION ALL ")
		   .append(iterationSQL);
		return buf.toString();
	}
	public RecursiveCTEDefinition(String name, String optionalCTESignature,  String initializationSQL, String iterationSQL, boolean removeDuplicate) {
		super(name, optionalCTESignature, toSQLDefinition(initializationSQL, iterationSQL, removeDuplicate));
	}
	
}
