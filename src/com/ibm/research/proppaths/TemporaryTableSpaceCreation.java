package com.ibm.research.proppaths;

public class TemporaryTableSpaceCreation implements SQLCommand {

	protected String name;
	protected int numberOfPages;
	protected int prefetchSizeInM;
	protected String file;
	
	public TemporaryTableSpaceCreation(String name, String file, int numberOfPages,
			int prefetchSizeInM) {
		super();
		this.name = name;
		this.numberOfPages = numberOfPages;
		this.prefetchSizeInM = prefetchSizeInM;
		this.file  = file;
	}
	public TemporaryTableSpaceCreation(String name, String file) {
		this(name, file, 100000,50);
	}
	
	@Override
	public String toSQL() {
		return "CREATE USER TEMPORARY TABLESPACE "+name
				+" MANAGED BY DATABASE USING (FILE '"+file+"' "+numberOfPages
				+") PREFETCHSIZE "+prefetchSizeInM+"M";
	}

}
