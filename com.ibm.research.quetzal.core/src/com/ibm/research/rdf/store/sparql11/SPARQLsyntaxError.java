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
 package com.ibm.research.rdf.store.sparql11;

import org.antlr.runtime.RecognitionException;

public class SPARQLsyntaxError extends RuntimeException {
	private static final long serialVersionUID = 5268600753398919884L;
	
	private String sql = null;
	private RecognitionException cause;
	
	SPARQLsyntaxError(String emsg, RecognitionException cause) 
	{
		super(emsg, cause);
		this.cause = cause;
	}
	
	SPARQLsyntaxError(RecognitionException cause) 
	{
		super(cause);
		this.cause = cause;
	}
	
	SPARQLsyntaxError(String sql) 
	{
		this.sql = sql;
	}

	public void setSQL(String sql)
	{
		this.sql = sql;
	}
	
	public String getMessage()
	{
		return getMessage(sql);
	}
	
	public String getMessage(String sql)
	{
		if (cause != null) {
			String line = ""+cause.line;
			String pos = ""+cause.charPositionInLine;
			String s = "Parse error in line " + line + " position " + pos;
			return s;
		} else {
			return sql;
		}
	}
}
