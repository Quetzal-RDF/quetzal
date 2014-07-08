package com.ibm.research.proppaths;

public class InsertCopyOfATableIntoTable extends InsertIntoTable implements SQLCommand {

	public InsertCopyOfATableIntoTable(String targetTable, String sourceTable) {
		super(targetTable, "SELECT * FROM "+sourceTable);
	}

	

}
