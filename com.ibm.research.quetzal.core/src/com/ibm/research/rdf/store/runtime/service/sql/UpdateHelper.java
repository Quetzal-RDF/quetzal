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
 package com.ibm.research.rdf.store.runtime.service.sql;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibm.research.rdf.store.Store;

public class UpdateHelper
{

   private static final Log log = LogFactory
			.getLog(UpdateHelper.class);

   private static final String db2_sp_addTriple = "CALL DB2INST1.ADDTRIPLE(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
   private static final String db2_sp_deleteTriple = "CALL DB2INST1.DELETETRIPLE(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
   
   public static boolean addTriple(Connection conn, String backend, String schemaName, String datasetName, String sub, String pred, String obj, int obj_datatype, int dft_dcol, int dft_rcol, String gid, int lock_mode )
   {
	   if (backend.equalsIgnoreCase(Store.Backend.db2.name()))
	   {
		   String ret = executeCall(conn, db2_sp_addTriple, 11, schemaName, datasetName, sub, obj, pred, obj_datatype, dft_dcol, dft_rcol, gid, lock_mode);
		   System.out.println("---Add Triple: "+ret);
		   return ret == "FINISHED";
	   }
	   else
	   {
		   return false;
	   }
   }
   
   public static boolean deleteTriple(Connection conn, String backend, String schemaName, String datasetName, String sub, String pred, String obj, int dph_size, int rph_size, int dft_dcol, int dft_rcol, String gid, int lock_mode )
   {
	   if (backend.equalsIgnoreCase(Store.Backend.db2.name()))
	   {
		   String ret = executeCall(conn, db2_sp_deleteTriple, 12, schemaName, datasetName, sub, obj, pred, dph_size, rph_size, dft_dcol, dft_rcol, gid, lock_mode);
		   System.out.println("---Delete Triple: "+ret);
		   return ret == "FINISHED";
	   }
	   else
	   {
		   return false;
	   }
   }

   //copy & modify SQLExecutor.executeCall here to return the result codes
   private static String executeCall(Connection conn, String sql, int retPid,
			Object... params)
   {

	   
		CallableStatement stmt = null;
		String ret = null;
		
		
		try {
			conn.setAutoCommit(false);
		} catch (SQLException ex) {
			log.error(ex);
			ex.printStackTrace();
			System.out.println(ex.getLocalizedMessage());
			return ret;
		}
		
		try {
			
			stmt = conn.prepareCall(sql);
			int i = 1;
			for (Object o : params) {
				stmt.setObject(i, o);
				i++;
			}
			
			stmt.registerOutParameter(retPid, Types.VARCHAR);
			
			stmt.execute();
			ret = stmt.getString(retPid);
			
			conn.commit();
			
		} catch (SQLException e) {
//			log.error(e);
//			e.printStackTrace();
//			System.out.println(e.getLocalizedMessage());
			ret = null;
			
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				log.error(e1);
				e1.printStackTrace();
				System.out.println(e1.getLocalizedMessage());
				ret = null;
			}
			
		} finally {
			closeSQLObjects(stmt, null);
		}
		
		try {
			conn.setAutoCommit(true);
		} catch (SQLException ex) {
			log.error(ex);
			ex.printStackTrace();
			System.out.println(ex.getLocalizedMessage());
			ret = null;
		}
		
		return ret;
	}
   
   private static void closeSQLObjects(PreparedStatement stmt, ResultSet rs) {
		try {
			if (rs != null)
				rs.close();
		} catch (SQLException e) {
		}

		try {
			if (stmt != null)
				stmt.close();
		} catch (SQLException e) {
		}
	}
   
   
   public static void main(String[] args)
   {
	   String JDBC_URL = "jdbc:db2://localhost:10997/lubm";
	   String user = "db2inst1";
	   String password = "db2admin";
	   
	   String backend = "db2";
	   String schema = "db2inst1";
	   String store = "lubm_100m_r";
	   int lock = 1;
	   int dph_size = 11;
	   int rph_size = 4;
	   
	   String[][] triples = {
			   {"http://usersub1/", "http://testpred1/", "http://userobj1/"},
			   {"http://usersub1/", "http://testpred2/", "http://userobj2/"},
			   {"http://usersub1/", "http://testpred1/", "userobj3"},
			   {"http://usersub2/", "http://testpred2/", "http://userobj2/"},
			   {"http://usersub1/", "http://testpred3/", "userobj4"},
			   {"http://usersub1/", "http://testpred4/", "userobj4"},
			   {"http://usersub2/", "http://testpred8/", "http://userobj2/"},
			   {"http://usersub2/", "http://testpred6/", "http://userobj2/"},
			   {"http://usersub2/", "http://testpred7/", "http://userobj2/"},
			   {"http://usersub1/", "http://testpred5/", "userobj5"},
			   {"http://usersub2/", "http://testpred9/", "http://userobj2/"},
			   {"http://usersub3/", "http://testpred2/", "http://userobj2/"},
			   {"http://usersub1/", "http://testpred1/", "userobj4"},
			   {"http://usersub1/", "http://testpred1/", "userobj5"},
			   {"http://usersub1/", "http://testpred3/", "userobj5"},
			   {"http://usersub4/", "http://testpred2/", "http://userobj2/"},
			   {"http://usersub3/", "http://testpred1/", "userobj3"}
	   };
	   
	   try {
		   
		    String classNameString = new String("com.ibm.db2.jcc.DB2Driver");
			Class.forName(classNameString);
			
			Connection conn = DriverManager.getConnection(JDBC_URL, user, password);
			  
			String gid = "DEF";
			
			//add triples to graph DEF
			for(int i = 0; i < triples.length; i++)
			{
				String sub = triples[i][0];
				String pred = triples[i][1];
				String obj = triples[i][2];
				int obj_dt = obj.startsWith("http://") ? 10002 : 5001;
				int dft_col = i % 2;
				
				addTriple(conn, backend, schema, store, sub, pred, obj, obj_dt, dft_col, dft_col, gid, lock );
			}
			
			
			gid = "G1";
			
			//add triples to graph G1
			//some operations will fail due to UNIQUE indexes (on DS, RS) without GID
			for(int i = 0; i < triples.length; i++)
			{
				String sub = triples[i][0];
				String pred = triples[i][1];
				String obj = triples[i][2];
				int obj_dt = obj.startsWith("http://") ? 10002 : 5001;
				int dft_col = i % 2;
				
				addTriple(conn, backend, schema, store, sub, pred, obj, obj_dt, dft_col, dft_col, gid, lock );
			}
			
			
			gid = "DEF";
			
			//delete triples to graph DEF
			for(int i = triples.length - 1; i >= 0; i--)
			{
				String sub = triples[i][0];
				String pred = triples[i][1];
				String obj = triples[i][2];
				int dft_col = i % 2;
				
				deleteTriple(conn, backend, schema, store, sub, pred, obj, dph_size, rph_size, dft_col, dft_col, gid, lock );
			}
			
			
			gid = "G1";
			
			//delete triples to graph G1
			for(int i = triples.length - 1; i >= 0; i--)
			{
				String sub = triples[i][0];
				String pred = triples[i][1];
				String obj = triples[i][2];
				int dft_col = i % 2;
				
				deleteTriple(conn, backend, schema, store, sub, pred, obj, dph_size, rph_size, dft_col, dft_col, gid, lock );
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} 
	   
   }
}
