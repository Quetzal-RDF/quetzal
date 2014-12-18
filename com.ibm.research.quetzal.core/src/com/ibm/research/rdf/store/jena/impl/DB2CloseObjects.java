package com.ibm.research.rdf.store.jena.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DB2CloseObjects {
	private static final Log log = LogFactory.getLog(DB2CloseObjects.class);

	// Closing ResultSet and PreparedStatement.
	public static void close(ResultSet rs, Statement statement) {
		close(rs);
		close(statement);
	}

	// Closing ResultSet
	public static void close(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				log.error("Unexpected SQLException while closing resultset..",
						e);
			}
		}
	}

	// Closing PreparedStatement.
	public static void close(Statement statement) {
		if (statement != null) {
			try {
				//if (!statement.isClosed()) {
					statement.close();
				//}
			} catch (SQLException e) {
				log.error("Unexpected SQLException while closing statement..",
						e);
			}
		}
	}
}
