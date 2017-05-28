package com.ibm.research.quetzal.loader;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableSchema;
import com.ibm.research.rdf.store.config.Constants;
import com.simba.googlebigquery.core.BQConnectionOptions.QueryDialect;
import com.simba.googlebigquery.jdbc42.DataSource;

public class DphSchemaJDBC {

	public static void main(String[] args) throws SQLException {
		String project = args[0];
		String email = args[1];
		String key = args[2];
		String table = args[3];
		System.out.println(readSchema(project, email, key, table));
	}

	public static TableSchema readSchema(String project, String email, String key, String table) throws SQLException {
		TableSchema schema = new TableSchema();
		
		String jdbcUrl = "jdbc:bigquery://https://www.googleapis.com/bigquery/v2:443;ProjectId=" 
				+ project 
				+ ";OAuthType=0;OAuthServiceAcctEmail=" 
				+ email
				+ ";OAuthPvtKeyPath="
				+ key;
		
		DataSource ds = new DataSource();
		ds.setURL(jdbcUrl);
		ds.setSQLDialect(QueryDialect.SQL);
		Connection connection = ds.getConnection();
		
		List<TableFieldSchema> fields = new LinkedList<TableFieldSchema>();
		
		TableFieldSchema subject = new TableFieldSchema();
		subject.setName(Constants.NAME_COLUMN_ENTRY);
		subject.setType("STRING");
		fields.add(subject);
		
		Statement s = connection.createStatement();
		ResultSet rs = s.executeQuery("select onetoone, hash0 from quetzal.knoesis_PREDICATES order by hash0");
		while (rs.next()) {
			TableFieldSchema x = new TableFieldSchema();
			fields.add(x);
			x.setName(Constants.NAME_COLUMN_PREFIX_VALUE + rs.getInt("hash0"));
			x.setType("STRING");
			if (! rs.getBoolean("onetoone")) {
				x.setMode("REPEATED");
			}

			x = new TableFieldSchema();
			fields.add(x);
			x.setName(Constants.NAME_COLUMN_PREFIX_TYPE + rs.getInt("hash0"));
			x.setType("INTEGER");
			if (! rs.getBoolean("onetoone")) {
				x.setMode("REPEATED");
			}
		}
		connection.close();

		schema.setFields(fields);
		
		return schema;
	}

}
