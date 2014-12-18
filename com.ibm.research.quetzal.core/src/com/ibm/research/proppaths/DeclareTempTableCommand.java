package com.ibm.research.proppaths;

public class DeclareTempTableCommand implements SQLCommand {

	protected String tmpSpace;
	protected String table;
	protected String tableSignature;
	
	
	public DeclareTempTableCommand(String tmpSpace, String table, String tableSignature) {
		super();
		this.tmpSpace = tmpSpace;
		this.table = table;
		this.tableSignature = tableSignature;
	}


	@Override
	public String toSQL() {
		return  "DECLARE GLOBAL TEMPORARY TABLE "+ table+ "("+tableSignature+") ON COMMIT PRESERVE ROWS  NOT LOGGED IN "+tmpSpace;
	}

}
