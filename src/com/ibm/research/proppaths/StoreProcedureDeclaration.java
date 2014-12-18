package com.ibm.research.proppaths;

public class StoreProcedureDeclaration implements SQLCommand {

	protected StoreProcedure procedure;
	protected boolean replace;
	
	public StoreProcedureDeclaration(StoreProcedure procedure) {
		this(procedure, true);
	}
	
	public StoreProcedureDeclaration(StoreProcedure procedure, boolean replace) {
		super();
		this.procedure = procedure;
		this.replace = replace;
	}


	@Override
	public String toSQL() {
		 return procedure.getSqlDeclarationCode(replace);
	}

}
