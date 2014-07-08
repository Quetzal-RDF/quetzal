package com.ibm.research.proppaths;

public class DropStoreProcedure implements SQLCommand {

	protected String procedure;
	
	public DropStoreProcedure(String procedure) {
		super();
		this.procedure = procedure;
	}

	@Override
	public String toSQL() {
		//TODO: use templates
		return "DROP PROCEDURE "+procedure;
	}

}
