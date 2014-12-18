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

public class DeclareReturnCursor implements SQLCommand {

	/**
	 * cursor index starting from 0
	 */
	//protected int cursorIndex;
	
	protected String sqlQuery;
	
	protected String returnCursorName;
	
	public DeclareReturnCursor(int cursorIndex, String sqlQuery) {
		this("C", cursorIndex, sqlQuery);
	}
		
	public DeclareReturnCursor(String varName, String sqlQuery) {
		super();
		this.returnCursorName = varName;
		this.sqlQuery = sqlQuery;
		
	}
	public DeclareReturnCursor(String returnCursorPrefix, int cursorIndex, String sqlQuery) {
		this(returnCursorPrefix+(cursorIndex+1), sqlQuery);
	}
	
	public String getCursorName() {
		return returnCursorName ;
	}

	@Override
	public String toSQL() {
		//TODO: use templates
		StringBuffer buf = new StringBuffer();
		buf.append("DECLARE ").append(getCursorName()).append(" CURSOR WITH RETURN FOR ");
		buf.append(sqlQuery);
		return buf.toString();
	}

}
