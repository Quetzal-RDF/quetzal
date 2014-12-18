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

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;


// test for qNum = 0
public class TestDB2Graph {
	
	// qNum startNum hopNum
	public static void main(String[] args)  throws Exception {
		if (args.length!=3) {
			System.out.println("Usage: qNum startNum hopNum");
			System.exit(1);
		} else {
			int qNum = Integer.valueOf(args[0]);
			assert qNum == 0 : qNum;
			int startNum = Integer.valueOf(args[1]);
			int hopNum = Integer.valueOf(args[2]);
			System.out.println("qNum ="+qNum+" startNum ="+startNum+" hopNum="+hopNum);
			
			Connection conn = getConnection("jdbc:db2://9.47.204.38:50001/dbpedia","db2inst2", "db2admin");
			long time = System.currentTimeMillis();
			int count = evaluate(conn, qNum, startNum, hopNum, false, true, false, false);
			System.out.println("Result: "+ count);
			System.out.println("First evaluation done in "+(System.currentTimeMillis() - time)+ " ms");
			// precise perf eval
			
			int numOfRuns = 7;
			time = System.currentTimeMillis();
			for (int i=0; i<numOfRuns;i++) {
				int val = evaluate(conn, qNum, startNum, hopNum, false, true, false, false);
				assert val == count : val;
			}
			long avgTime = (System.currentTimeMillis() - time)/numOfRuns;
			System.out.println("Average evaluation time over "+ numOfRuns+" runs: "+avgTime+ " ms");
			conn.close();
			
		}
	}
	
	public static int evaluate(Connection conn, int qNum, int startNum, int hopNum, boolean runStats, boolean distinct, boolean indices, boolean runStatsSampledIndexAll) throws SQLException {
		assert qNum == 0: qNum;
		CallableStatement stmt = getStoreProcedureInvocation(conn, qNum, startNum, hopNum, runStats, distinct, indices, runStatsSampledIndexAll);
		ResultSet rs = stmt.executeQuery();
		
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
	
	public  static String getStartingPointStmt(int qNum, int startNum, int hopNum) {
		String Q1_STMT1 = ""
             + "INSERT INTO SESSION.TEMP(VAL, LEVEL)\n"
             + "SELECT ID, 0\n" + "FROM DB2ADMIN.DBP38EN_ID WHERE QNO="+qNum+" AND SNO <="+startNum+"\n";
		 return Q1_STMT1;
	}
	
	public static String getRecursionStmt(boolean distinct) {
		  String Q1_STMT2 = ""
              + "INSERT INTO SESSION.TEMP(VAL, LEVEL)\n"
              + "WITH\n"
              + "  TEMP_1_0 AS\n"
              + "     (SELECT T.VAL AS VAL\n"
              + "        FROM SESSION.TEMP P, DB2ADMIN.DBP38EN_RPH AS R,\n"
              + "             TABLE(VALUES (R.VAL2, R.LBL2)) AS T (VAL, LBL)\n"
              + "        WHERE P.VAL = R.ENTRY AND P.LEVEL  = ? AND T.LBL IN ('http://dbpedia.org/ontology/isPartOf')),\n"
              + "  TEMP_1_1 AS\n"
              + "     (SELECT COALESCE(RS.VAL, P.VAL) AS VAL\n"
              + "        FROM\n"
              + "             TEMP_1_0 AS P LEFT OUTER JOIN DB2ADMIN.DBP38EN_RS AS RS ON P.VAL = RS.ID)\n"
              + (distinct? "SELECT DISTINCT T1.VAL, ?\n": "SELECT T1.VAL, ?\n")
              + "FROM TEMP_1_1 T1\n" + "LEFT OUTER JOIN SESSION.TEMP T2\n"
              + "ON T1.VAL = T2.VAL\n" + "WHERE T2.VAL IS NULL\n"
              + (distinct? "": "GROUP BY T1.VAL\n");
		 return  Q1_STMT2;
	}
	
	public static String getFinalStmt() {
		 String Q1_STMT3 = "SELECT COUNT(*) FROM SESSION.TEMP\n";
		 return Q1_STMT3;
	}
	
	public static CallableStatement getStoreProcedureInvocation(Connection conn,int qNum, int startNum, int hopNum, boolean  runStats, boolean distinct, boolean indices, boolean runStatsSampledIndexAll) throws SQLException {
		String procName; // = runStats? "DB2INST2.GENPROC2":"DB2INST2.GENPROC0";
		if (runStats) {
			procName =  "DB2INST2.GENPROC2";
		} else {
			procName = "DB2INST2.GENPROC0";
		}
		CallableStatement cstmt = conn.prepareCall("CALL "+procName+"(?, ?, ?, ?)");
        cstmt.setString(1, getStartingPointStmt(qNum, startNum, hopNum));
        cstmt.setString(2, getRecursionStmt(distinct));
        cstmt.setString(3, getFinalStmt());
        cstmt.setInt(4, hopNum);
        cstmt.execute();
        return cstmt;
	}
	 public static Connection getConnection(String jdbcurl,  String username, String password ) throws SQLException, ClassNotFoundException
	         {
	         
	            Class.forName("com.ibm.db2.jcc.DB2Driver");
	            Properties info = new Properties();
	            info.setProperty("user", username);
	            info.setProperty("password", password);
	            info.setProperty("prompt", "false");
	            info.setProperty("access", "read only");
	            info.setProperty("big decimal", "false");
	            info.setProperty("lazy close", "true");
	            info.setProperty("block criteria", "2");
	            info.setProperty("block size", "1024");
	            info.setProperty("data compression", "true");
	            Connection conn = DriverManager.getConnection(jdbcurl, info);
	            return conn;
	         
	      }

}
