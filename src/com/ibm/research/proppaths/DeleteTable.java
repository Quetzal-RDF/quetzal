package com.ibm.research.proppaths;

public class DeleteTable implements SQLCommand {
	protected String table;
	
	public DeleteTable(String table) {
		super();
		this.table = table;
	}

	@Override
	public String toSQL() {
		return "DELETE FROM "+ table;
	}

}
