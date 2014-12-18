package com.ibm.research.proppaths;

public class DropTableCommand implements SQLCommand {

	protected String table;
	
	public DropTableCommand(String table) {
		super();
		this.table = table;
	}

	@Override
	public String toSQL() {
		return "DROP TABLE "+table;
	}

}
