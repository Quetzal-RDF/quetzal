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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ibm.research.rdf.store.Context;
import com.ibm.research.rdf.store.jena.RdfStoreException;

public class SQLExecutor {

	public static void main(String[] args) {
		String in = "SELECT * from %s WHERE storeName = ? and status = ? and entryid = 0";
		
		System.out.println(getSql(in, "kbt", 1));
	}
	protected static String getSql(String sql, Object... params)  {
		Pattern pattern =  Pattern.compile("\\?");
		Matcher m = pattern.matcher(sql);
		StringBuffer sb = new StringBuffer();
		for (Object o : params) {
			if (m.find()) {
			   String s = o instanceof String? "'"+o.toString()+"'": o.toString();
			   String repl = Matcher.quoteReplacement(s);
			   m.appendReplacement(sb, repl);
			}
		}
		m.appendTail(sb);
		return sb.toString();
	}
	public static PreparedStatement prepare(Connection conn, String sql) {
		try {
			return conn.prepareStatement(sql);
		} catch (SQLException e) {
			throw new RdfStoreException(e.getLocalizedMessage(), e);
		}

	}

	public static int executeUpdate(Connection conn, String sql) {
		PreparedStatement stmt = null;
		System.err.println(sql);
		try {
			stmt = conn.prepareStatement(sql);
			return stmt.executeUpdate();
		} catch (SQLException e) {
			throw new SQLExceptionWrapper(e);
		} finally {
			closeSQLObjects(stmt, null);
		}
	}
	
	public static int executeUpdate(Connection conn, String sql,
			Object... params) {
		PreparedStatement stmt = null;
		System.err.println(sql);
		try {
			stmt = conn.prepareStatement(sql);
			try {
				int i = 1;
				for (Object o : params) {
					stmt.setObject(i, o);
					i++;
				}
			} catch (SQLException e) {
				// handle hive/shark (no support for setObject)
				sql = getSql(sql, params);
				stmt = conn.prepareStatement(sql);	
			}
			return stmt.executeUpdate();
		} catch (SQLException e) {
			throw new SQLExceptionWrapper(e);
		} finally {
			closeSQLObjects(stmt, null);
		}
	}

	private PreparedStatement stmt;
	private ResultSet rs;

	public <E> E executeQuery(Connection conn, String sql,
			SingleRowResultSetProcessor<E> p, Object... params) {
		SQLExecutor.closeSQLObjects(stmt, rs);

		try {
			stmt = conn.prepareStatement(sql);
			
			try {
				int i = 1;
				for (Object o : params) {
					stmt.setObject(i, o);
					i++;
				}
			} catch (SQLException e) {
				// handle hive/shark (no support for setObject)
				sql = getSql(sql, params);
				stmt = conn.prepareStatement(sql);	
			}
			//System.out.println("SQL:\n"+ sql);
			rs = stmt.executeQuery();
			boolean hasResults = false;
			hasResults = rs.next();
			if (hasResults == false)
				return null;

			return p.processRow(conn, rs);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new SQLExceptionWrapper(e);
		} finally {
			SQLExecutor.closeSQLObjects(stmt, rs);
		}
	}

	public StoreImpl getStore(Connection conn, final Context context, String sql, Object... params) {
	   StoreImpl store= executeQuery(conn, sql,
				new SingleRowResultSetProcessor<StoreImpl>() {
					public StoreImpl processRow(Connection conn, ResultSet rs)
							throws SQLException {
						StoreImpl store = new StoreImpl(context);
						store.setBasicStatsTable(rs.getString("basicStatsTable".toLowerCase()));
						store.setDirectPrimary(rs.getString("directPrimary".toLowerCase()));
						store.setDirectSecondary(rs.getString("directSecondary".toLowerCase()));
						store.setDPrimarySize(rs.getInt("dPrimarySize".toLowerCase()));
						store.setEntry_ID(rs.getInt("entry_ID".toLowerCase()));
						store.setGidMaxStringLen(rs.getInt("gidMaxStringLen".toLowerCase()));
						store.setHasLongStrings(rs.getInt("hasLongStrings".toLowerCase()));
						store.setLongStrings(rs.getString("longStrings".toLowerCase()));
						store.setMaxStringLen(rs.getInt("maxStringLen".toLowerCase()));
						store.setReversePrimary(rs.getString("reversePrimary".toLowerCase()));
						store.setReverseSecondary(rs.getString("reverseSecondary".toLowerCase()));
						store.setRPrimarySize(rs.getInt("rPrimarySize".toLowerCase()));
						store.setStoreName(rs.getString("storeName".toLowerCase()));
						store.setTopKStatsTable(rs.getString("topKStatsTable".toLowerCase()));
						store.setDatatypeTable(rs.getString("datatypetable".toLowerCase()));

						return store;
					}
				}, params);
		return  store;
	}

	public static boolean executeCall(Connection conn, String sql,
							Object... params) {
		
		CallableStatement stmt = null;
		
		try {
			stmt = conn.prepareCall(sql);
			int i = 1;
			for (Object o : params) {
				stmt.setObject(i, o);
				i++;
			}
			
			return stmt.execute();
		} catch (SQLException e) {
			throw new SQLExceptionWrapper(e);
		} finally {
			closeSQLObjects(stmt, null);
		}
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
}
