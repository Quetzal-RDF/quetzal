/******************************************************************************
 * Copyright (c) 2015 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *****************************************************************************/
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
