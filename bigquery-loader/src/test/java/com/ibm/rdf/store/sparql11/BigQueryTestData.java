package com.ibm.rdf.store.sparql11;

import java.sql.SQLException;

import com.ibm.rdf.store.sparql11.TestRunner.TestData;
import com.ibm.research.rdf.store.Context;
import com.ibm.research.rdf.store.Store.Backend;
import com.simba.googlebigquery.googlebigquery.core.BQConnectionOptions.QueryDialect;
import com.simba.googlebigquery.jdbc42.DataSource;

public class BigQueryTestData extends TestData {

	public BigQueryTestData(String db, String dataset, String username,
			String password, String schemaName, boolean defUnionGraph) {
		super(db, dataset, username, password, schemaName,
				defUnionGraph);
	}
	
	private BigQueryTestData(BigQueryTestData toCopy, String dataset) {
		super(toCopy, dataset);
		setStore();
	}

	@Override
	protected void setConnectionDetails() {
		String jdbcUrl = "jdbc:bigquery://https://www.googleapis.com/bigquery/v2:443;ProjectId=" 
				+ jdbcurl 
				+ ";OAuthType=0;OAuthServiceAcctEmail=" 
				+ username
				+ ";OAuthPvtKeyPath="
				+ password;

		   DataSource ds = new DataSource();
		   ds.setSQLDialect(QueryDialect.SQL);
		   ds.setURL(jdbcUrl);
		   ds.setTimeout(120);
		   try {
			   conn = ds.getConnection();
		   } catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException(e);				   
		   }
		   ctx = new Context();
		   setStore();
	}

	@Override
	protected Backend getBackend() {
		return Backend.bigquery;
	}

	@Override
	public TestData cloneAndResetStore(String dataset) {
		return new BigQueryTestData(this, dataset);
	}
}
