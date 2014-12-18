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

public class InsertIntoTable implements SQLCommand {

	protected String table;
	protected String sql;
	
	
	public InsertIntoTable(String table, String sql) {
		super();
		this.table = table;
		this.sql = sql;
	}

	@Override
	public String toSQL() {
		return "INSERT INTO "+table+"\n"+sql;
	}
	
	public String getSQLWithoutInsert() {
		return sql;
	}

}
