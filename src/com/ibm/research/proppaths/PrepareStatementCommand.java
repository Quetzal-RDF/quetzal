package com.ibm.research.proppaths;

public class PrepareStatementCommand implements SQLCommand {

	private String variableName;
	private String sqlStatement;
	
	public PrepareStatementCommand(String variableName, String sqlStatement) {
		super();
		this.variableName = variableName;
		this.sqlStatement = sqlStatement;
	}

	@Override
	public String toSQL() {
		return "PREPARE "+ variableName+" FROM '"+sqlStatement.replace("'", "''")+"'";
	}

}
