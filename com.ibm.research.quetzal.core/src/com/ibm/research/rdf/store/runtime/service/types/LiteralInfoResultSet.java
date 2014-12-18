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
 package com.ibm.research.rdf.store.runtime.service.types;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.ibm.research.rdf.store.sparql11.model.Variable;

public class LiteralInfoResultSet {

	private ResultSet resultSet;
	private Statement st;
	private Set<String> literalColumns = new HashSet<String>();
	
	public LiteralInfoResultSet(Statement st, ResultSet resultSet,
			Set<Variable> literalColumns) {
		
		super();
		this.resultSet = resultSet;
		this.st = st;
		Iterator<Variable> it = literalColumns.iterator();
		while ( it.hasNext() ) {
			this.literalColumns.add(it.next().getName());
		}
	}

	public ResultSet getResultSet() {
		return resultSet;
	}

	public Statement getStatement() {
		if (st!=null) {
			return st;
		} else {
			try {
				return resultSet.getStatement();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}
	public boolean isLiteralVariable(String variable) {
		return literalColumns.contains(variable);
	}
	
	
}
