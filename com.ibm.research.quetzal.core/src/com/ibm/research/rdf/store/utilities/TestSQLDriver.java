package com.ibm.research.rdf.store.utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestSQLDriver {
	private static String driverName = "org.apache.hive.jdbc.HiveDriver";

	public static void main(String[] args) throws Exception  {
		Class.forName(driverName);

		Connection con = DriverManager.getConnection("jdbc:hive2://localhost:10000", "root",
				"foo2bar");
		Statement stmt = con.createStatement();
	
		String sql = "create temporary function httpGet as 'com.ibm.research.rdf.store.utilities.WebServiceGetUDTF'";
		stmt.executeQuery(sql);
		sql = "create temporary function processTable as 'com.ibm.research.rdf.store.utilities.WebServicePostUDAF'";
		stmt.executeQuery(sql);

		
		sql = "WITH QS5_URL AS (select explode(array('http://localhost:8083/getDrugBankNames')) as url , url  ), " + 
		" QS5_URLS AS (select * from QS5_URL), QS5_GET AS (select httpGet('d1', 'url', '', 'GET', 'fn=http://localhost:8083/,x=http://www.drugbank.ca,xs=http://www.w3.org/2001/XMLSchema,up=http://uniprot.org/uniprot,drug=http://www.drugbank.ca', '//x:row', 'd1', './x:drug', 'xs:string', url) as (d1,d1_TYP) from QS5_URLS)," 
		+ " QS5 AS (select d1,d1_TYP  from QS5_GET ),"
		+ " QS6_URL AS (select explode(array(concat('http://localhost:8083/getDrugTransporters','?drugName=',QS5.d1))) as url , url,transporter,transporter_TYP,d1,d1_TYP   from QS5 ), QS6_URLS AS (select * from QS6_URL), QS6_GET AS (select " +
		" httpGet('transporter,d1', 'url,transporter,transporter_TYP,d1,d1_TYP', '', 'GET', 'fn=http://localhost:8083/,x=http://www.drugbank.ca,xs=http://www.w3.org/2001/XMLSchema,up=http://uniprot.org/uniprot,drug=http://www.drugbank.ca', '//x:row', 'transporter', './x:drug', 'xs:string','d1', './x:id', 'xs:string', url,transporter,transporter_TYP,d1,d1_TYP) as (transporter,transporter_TYP,d1,d1_TYP) from QS6_URLS)," +
		" QS6 AS (select transporter,transporter_TYP,d1,d1_TYP  from QS6_GET ) "
		+ " select * from QS6";
		
		ResultSet res = stmt.executeQuery(sql);

		while (res.next()) {
			System.out.println(res.getString(1) + "\t" + res.getString(2) + "\t" + res.getString(3) + "\t" + res.getString(4));
			// System.out.println(res.getString(1) + "\t" + res.getString(2));
			// System.out.println(res.getString(1) + "\t" + res.getString(2) + "\t" + res.getString(3) +  "\t" + res.getString(4) + "\t" + res.getString(5));
		} 
	}
}
