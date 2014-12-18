package com.ibm.research.proppaths;

public class CTEDefinition implements SQLCommand {

	private String name;
	private String sqlDef;
	
	public CTEDefinition(String name, String sqlDef) {
		super();
		this.name = name;
		this.sqlDef = sqlDef;
	}

	public CTEDefinition(String name, String optionalCTESignature,  String sqlDef) {
		this(name+(optionalCTESignature!=null?"("+optionalCTESignature+")":""), sqlDef);
	}
	
	@Override
	public String toSQL() {
		return name +" AS ("+sqlDef+")";
	}

}
