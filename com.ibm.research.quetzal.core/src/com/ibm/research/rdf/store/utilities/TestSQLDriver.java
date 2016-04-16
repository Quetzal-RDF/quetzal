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

		
		sql = "WITH 	QS0_URL AS (select explode(array('http://localhost:8083/getDrugBankNames')) as url , url  ), QS0_URLS AS (select * from QS0_URL), QS0_GET AS (select httpGet('drug', 'url', '', 'GET', 'fn=http://localhost:8083/,x=http://www.drugbank.ca,xs=http://www.w3.org/2001/XMLSchema', '//x:row', 'drug', './x:drug', 'xs:string', url) as (drug,drug_TYP) from QS0_URLS)," +
				" QS0 AS (select drug,drug_TYP  from QS0_GET )," +
				" QS1_POST AS (select processTable(drug,drug_TYP,'http://localhost:8083/postData','drug,drug_TYP','drug','drug,sum','fn=http://localhost:8083/,x=http://www.drugbank.ca,xs=http://www.w3.org/2001/XMLSchema', '//row', 'drug', './drug', 'xs:string','sum', './sum', 'xs:string') as p from QS0), QS1_RAW AS (select inline(p) from QS1_POST) " +
				" select drug, drug_typ, sum, sum_typ from QS1_RAW";
		
		ResultSet res = stmt.executeQuery(sql);

		while (res.next()) {
			System.out.println(res.getString(1) + "\t" + res.getString(2) + "\t" + res.getString(3) + "\t" + res.getString(4));
			// System.out.println(res.getString(1) + "\t" + res.getString(2));
			// System.out.println(res.getString(1) + "\t" + res.getString(2) + "\t" + res.getString(3) +  "\t" + res.getString(4) + "\t" + res.getString(5));
		} 
	}
}
