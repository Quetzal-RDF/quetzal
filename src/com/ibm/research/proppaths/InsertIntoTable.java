package com.ibm.research.proppaths;

public class InsertIntoTable implements SQLCommand {

	protected String table;
	protected String sql;
	
	
	public InsertIntoTable(String table, String sql) {
		super();
		this.table = table;
		this.sql = sql;
	}

	@Override
	public String toSQL() {
		return "INSERT INTO "+table+"\n"+sql;
	}
	
	public String getSQLWithoutInsert() {
		return sql;
	}

}
