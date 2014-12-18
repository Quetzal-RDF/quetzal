package com.ibm.research.proppaths;

public class OpenCursor implements SQLCommand {

	protected String cursorName;
	
	
	public OpenCursor(String cursorName) {
		super();
		this.cursorName = cursorName;
	}


	@Override
	public String toSQL() {
		//TODO: use templates
		return "OPEN "+cursorName;
	}

}
