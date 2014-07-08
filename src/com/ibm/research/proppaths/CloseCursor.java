package com.ibm.research.proppaths;

public class CloseCursor implements SQLCommand {

	protected String cursorName;
	
	
	public CloseCursor(String cursorName) {
		super();
		this.cursorName = cursorName;
	}


	@Override
	public String toSQL() {
		//TODO: use templates
		return "CLOSE "+cursorName;
	}

}
