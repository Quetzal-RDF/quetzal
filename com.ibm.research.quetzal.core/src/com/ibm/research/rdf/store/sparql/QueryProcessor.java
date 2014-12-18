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
 package com.ibm.research.rdf.store.sparql;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ibm.research.rdf.store.config.RDFStoreParameters;

public class QueryProcessor {
	
	private RDFStoreParameters params;
	
	public QueryProcessor(RDFStoreParameters p){
		params=p;
		Parser.ConfigureParser(p);
	}
	
	public ResultSet query(Connection c, String q){
		PreparedStatement pstmt;
		ResultSet rst=null;
		String sqlQuery=Parser.toSQLQuery(q);
		try {
			pstmt = c.prepareStatement(sqlQuery);
			rst=pstmt.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rst;
	}
	
	public ResultSet query(String q){
		Connection c=null;
		try {
			c = ConnectionFactory.getConnection();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return query(c,q);
	}
	
	public ResultSet query (String u, String p, String q){
		Connection c=null;
		try {
			c=ConnectionFactory.getConnection(params.databaseName,u,p);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return query(c,q);
	}
}
