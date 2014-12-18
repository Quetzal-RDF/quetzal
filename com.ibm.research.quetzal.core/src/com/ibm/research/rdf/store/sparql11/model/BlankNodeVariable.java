package com.ibm.research.rdf.store.sparql11.model;

public class BlankNodeVariable extends Variable {

	public BlankNodeVariable(String name) {
		super(name);
	}
	
	public String toString() {
		return "_:" + super.getName();
	}

}
