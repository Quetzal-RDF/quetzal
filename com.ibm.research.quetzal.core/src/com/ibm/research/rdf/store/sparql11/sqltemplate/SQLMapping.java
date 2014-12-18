package com.ibm.research.rdf.store.sparql11.sqltemplate;


public class SQLMapping {
	String name=null;
	Object values=null;
	String separator=null;
	
	public SQLMapping(String name, Object values, String separator) {
		this.name = name;
		this.values = values;
	}

	public String getName() {
		return name;
	}

	public Object getValues() {
		return values;
	}
		
}
