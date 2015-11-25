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
 package com.ibm.rdf.store.sparql11;

import java.sql.Connection;
import java.sql.DriverManager;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.ResultSet;
import com.ibm.research.rdf.store.Context;
import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.Store.Backend;
import com.ibm.research.rdf.store.StoreManager;
import com.ibm.research.rdf.store.jena.RdfStoreFactory;
import com.ibm.research.rdf.store.jena.RdfStoreQueryExecutionFactory;
import com.ibm.research.rdf.store.jena.RdfStoreQueryFactory;

public class SPRQLParser {
	private static Connection conn = null;
	static int count = 1;
	private static String query ="";
	private static Store store = null ;
	private static int queryNum = 0 ;
	
	private static Connection getConnection(){
		Connection conn = null ;
		try {

			Class.forName("com.ibm.db2.jcc.DB2Driver");

		//	LUBM100M -db test -user db2inst1 -password db2admin -host 9.47.205.206 -port 50000
			conn = DriverManager.getConnection("jdbc:db2://9.47.202.45:50001/jazz1000", "db2inst2","db2admin");
		//	conn = DriverManager.getConnection("jdbc:db2://falcon.in.ibm.com:60080/rdf100k", "abhigyan","welcome123");
		//	conn = DriverManager.getConnection("jdbc:db2://9.47.205.206:50000/test", "db2inst1","db2admin");
			 String storeName = "jazz";
		//	 String storeName = "rrc";
	//		 String storeName = "LUBM100M";
		//		String schema = "db2inst1";
				String schema = "db2inst2";
		//	 String schema = "";
				store = null;
				store = StoreManager.connectStore(conn, Backend.valueOf("db2"), schema, storeName, Context.defaultContext);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return conn ;
	}
	
	public static  RS queryData(String query) throws Exception{
			
		if ( conn == null ) {
		 conn = getConnection();
		}
		
		
		try {
		
			Query q = RdfStoreQueryFactory.create(query);

			
			Dataset ds = RdfStoreFactory.connectDataset(store, conn, Backend.valueOf("db2"));
		
			QueryExecution qe = RdfStoreQueryExecutionFactory.create(q, ds);
			
//			qe.setTimeout(30000);
			
			qe.getContext().set(Context.unionDefaultGraph, true);

			long rows = 0;

			ResultSet rs = qe.execSelect();
			
			count++;

			return new RS(qe,rs);

		}catch (Exception e) {
			e.printStackTrace();
			System.out.println("Query failed");
			return null;
		}
	}
	static final class RS {
		
		private QueryExecution qe;
		private ResultSet rs;
		
		
		public RS(QueryExecution qe, ResultSet rs) {
			super();
			this.qe = qe;
			this.rs = rs;
		}


		public QueryExecution getQe() {
			return qe;
		}


		public ResultSet getRs() {
			return rs;
		}
		
	}	

	
}

