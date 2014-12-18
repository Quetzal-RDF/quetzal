package com.ibm.research.proppaths;

public class StoreProcedureInvocation implements SQLCommand {

	protected String procName;
	protected int procParamSize;
	protected Object[] arguments;
	
	public StoreProcedureInvocation(StoreProcedure procedure, String... arguments) {
		super();
		this.procName = procedure.getName();
		this.procParamSize = procedure.getParamSize();
		this.arguments = arguments;
	}
	
	public StoreProcedureInvocation(String procedureName, Object... arguments) {
		super();
		this.procName= procedureName;
		this.arguments = arguments;
		this.procParamSize = arguments.length;
	}

	@Override
	public String toSQL() {
		return StoreProcedure.getSqlInvocatiionCode(procName, procParamSize, arguments);
	}

}
