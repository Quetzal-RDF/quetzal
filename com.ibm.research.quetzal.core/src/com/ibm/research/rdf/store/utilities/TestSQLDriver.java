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
		/*
		 * WITH 	QS0_URL AS (select explode(array('http://localhost:8083/getDrugBankNames')) , url   as url ), QS0_URLS AS (select * from QS0_URL), QS0 AS (select httpGet('drug', 'url', '', 'GET', 'fn=http://localhost:8083/,x=http://www.drugbank.ca,xs=http://www.w3.org/2001/XMLSchema', '//x:row', 'drug', './x:drug', 'xs:string', url) as (drug,drug_TYP) from QS0_URLS),
	QS1_URL AS (select explode(array(concat('http://localhost:8083/getDrugTransporters','?drugName=',QS0.drug))) , url,drug,drug_TYP   from QS0 ), QS1_URLS AS (select * from QS1_URL), QS1 AS (select httpGet('transporter,drug', 'url,drug,drug_TYP', '', 'GET', 'fn=http://localhost:8083/,x=http://www.drugbank.ca,xs=http://www.w3.org/2001/XMLSchema', '//x:row', 'transporter', './x:id', 'xs:string', url,drug,drug_TYP) as (transporter,transporter_TYP,drug,drug_TYP) from QS1_URLS) 
SELECT  drug AS drug,drug_TYP AS drug_TYP,transporter AS transporter,transporter_TYP AS transporter_TYP FROM QS1  */
		 
		String sql = "WITH 	QS0_URL AS (select explode(array('http://localhost:8083/getDrugBankNames')) as url , url  ), QS0_URLS AS (select * from QS0_URL), QS0 AS (select httpGet('drug', 'url', '', 'GET', 'fn=http://localhost:8083/,x=http://www.drugbank.ca,xs=http://www.w3.org/2001/XMLSchema', '//x:row', 'drug', './x:drug', 'xs:string', url) as (drug,drug_TYP) from QS0_URLS), " +
				" QS1_POST AS (select processTable(drug,drug_TYP,'http://localhost:8083/postData','drug,drug_TYP','drug','drug,sum','fn=http://localhost:8083/,x=http://www.drugbank.ca,xs=http://www.w3.org/2001/XMLSchema', '//row', 'drug', './drug', 'xs:string','sum', './sum', 'xs:string') as p from QS0) " +
			//	" QS_RAW AS (select t.drug as drug, t.drug_typ as drug_typ, t.sum as sum, t.sum_typ as sum_typ from QS1_POST lateral view explode(p) exploded_table as t), " +
			//	" QS1_TMP AS (select `_c0` AS drug,`_c1` AS drug_TYP,`_c2` AS sum,`_c3` AS sum_TYP from QS1_RAW)" +
			//	" QS1 AS (SELECT drug,drug_TYP,sum,sum_TYP,drug,drug_TYP FROM QS1_TMP) " +
			//	" SELECT  drug AS drug,drug_TYP AS drug_TYP,sum AS sum,sum_TYP AS sum_TYP FROM QS1 " +
			// " select `_c0`, `_c1`, `_c2`,`_c3` from (SELECT inline(p) from QS1_POST) t2 ";
			" select `_c0` as drug, `_c1` as drug_typ, `_c2` as sum,`_c3` as sum_typ  from (SELECT inline(p) from QS1_POST) t2 ";
			// " select p from QS1_POST";
		//"SELECT * from QS1_POST lateral view inline(p) t1 as drug, drug_typ, sum, sum_typ";
		// " SELECT * FROM QS_TMP";
		
		ResultSet res = stmt.executeQuery(sql);

		while (res.next()) {
			System.out.println(res.getString(1) + "\t" + res.getString(2) + "\t" + res.getString(3) + "\t" + res.getString(4));
			//System.out.println(res.getString(1) + "\t" + res.getString(2));
		} 
	}
}
