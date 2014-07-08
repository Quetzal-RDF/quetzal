package com.ibm.research.proppaths;

public class MultipleCTEDefintion extends CTEDefinition {
	private String sql; 
	public MultipleCTEDefintion(String sql) {
		super(null, null);
		this.sql = sql;
	}
	@Override
	public String toSQL() {
		return sql;
	}
	

}
