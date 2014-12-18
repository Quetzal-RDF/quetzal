package com.ibm.research.proppaths;

public class NewVariableGenerator {
	
	protected String varPrefix;
	protected int suffix;
	public NewVariableGenerator(String varPrefix, int startSuffix) {
		super();
		this.varPrefix = varPrefix;
		this.suffix = startSuffix;
	}
	
	public String createNewVariable() {
		return varPrefix+(suffix++);
	}
	
}