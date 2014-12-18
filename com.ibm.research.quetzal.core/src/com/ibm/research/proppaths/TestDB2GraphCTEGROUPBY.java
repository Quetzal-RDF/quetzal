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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


// test for qNum = 0
public class TestDB2GraphCTEGROUPBY {
	
	private static final String SQL_FILE_DIR = "../rdfstore-data/dbpediaLongQueriesSQL/";
	// queryIndex?
	public static void main(String[] args)  throws Exception {
		 {
			int queryIndex = -1;
			if (args.length>0) {
				queryIndex = Integer.valueOf(args[0]);
				System.out.println("queryIndex ="+queryIndex);
			}
			
			Connection conn = getConnection("jdbc:db2://9.47.204.38:50001/dbpedia","db2inst2", "db2admin");
			int start= queryIndex<0? 0: queryIndex;
			int maxExclusive = queryIndex<0? 11: queryIndex+1;
			for (queryIndex = start; queryIndex<maxExclusive; queryIndex++) {
				long time = System.currentTimeMillis();
				int count = evaluate(conn,queryIndex);
				System.out.println("Result: "+ count);
				System.out.println("First evaluation done in "+(System.currentTimeMillis() - time)+ " ms");
				// precise perf eval
				
				int numOfRuns = 7;
				time = System.currentTimeMillis();
				for (int i=0; i<numOfRuns;i++) {
					int val = evaluate(conn, queryIndex);
					assert val == count : val;
					long avgTime = (System.currentTimeMillis() - time)/(i+1);
					System.out.println("Query "+(queryIndex+1)+" average evaluation time over "+ (i+1)+" runs: "+avgTime+ " ms");
				}
				
				
			}
			conn.close();
			
		}
	}
	
	protected static String readSQLFile(File file) {
		try {
			StringBuffer sql = new StringBuffer();
			BufferedReader in = new BufferedReader(new FileReader(file));
			String line;
			while ((line=in.readLine())!=null) {
				sql.append(line+"\n");
			}
			in.close();
			return sql.toString();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	public static int evaluate(Connection conn, int queryIndex) throws SQLException {
		File file = new File(SQL_FILE_DIR+"lq"+(queryIndex+1)+".sql");
		String sql = readSQLFile(file);
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		
		int ret = 0;
		int rows = 0;
		while (rs.next()) {
			ret = rs.getInt(1);
			assert rows == 0 : rows;
			rows++;
		}
		rs.close();
		stmt.close();
		return ret;
		
	}
	
	
	 public static Connection getConnection(String jdbcurl,  String username, String password ) throws SQLException, ClassNotFoundException
	         {
	         
	          return TestDB2Graph.getConnection(jdbcurl, username, password);
	         
	      }

}
