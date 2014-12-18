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
