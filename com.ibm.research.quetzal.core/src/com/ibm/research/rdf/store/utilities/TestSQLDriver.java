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

			
		sql = "WITH 	QS0_URL AS (select explode(array('http://10.0.2.2:8083/getDrugBankNames')) as url , url  ), QS0_URLS AS (select * from QS0_URL), QS0_GET AS (select httpGet('drug', 'url', '', 'GET', 'fn=http://10.0.2.2:8083/,x=http://www.drugbank.ca,xs=http://www.w3.org/2001/XMLSchema,up=http://uniprot.org/uniprot,drug=http://www.drugbank.ca', '//x:row', 'drug', './x:drug', 'xs:string', url) as (drug,drug_TYP) from QS0_URLS), " +
				" QS0 AS (select drug,drug_TYP  from QS0_GET ), " +
				" QS1_POST AS (select processTable(drug,drug_TYP,'http://10.0.2.2:8083/getTransporters','drug,drug_TYP','drug','drug,transporter','fn=http://10.0.2.2:8083/,x=http://www.drugbank.ca,xs=http://www.w3.org/2001/XMLSchema,up=http://uniprot.org/uniprot,drug=http://www.drugbank.ca', '//x:row', 'drug', './x:drug', 'xs:string','transporter', './x:transporter', 'xs:string') as p from QS0), QS1_RAW AS (select inline(p) from QS1_POST)," +
				" QS1_TMP AS (select drug,drug_TYP,transporter,transporter_TYP from QS1_RAW),QS1 AS (SELECT drug,drug_TYP,transporter,transporter_TYP FROM QS1_TMP ) ," +
				" QS2 AS  (SELECT  DISTINCT  transporter AS transporter,transporter_TYP AS transporter_TYP FROM QS1), " +
				" QS3_URL AS (select explode(array('http://10.0.2.2:8083/getDrugBankNames')) as url , url  ), QS3_URLS AS (select * from QS3_URL), QS3_GET AS (select httpGet('drug', 'url', '', 'GET', 'fn=http://10.0.2.2:8083/,x=http://www.drugbank.ca,xs=http://www.w3.org/2001/XMLSchema,up=http://uniprot.org/uniprot,drug=http://www.drugbank.ca', '//x:row', 'drug', './x:drug', 'xs:string', url) as (drug,drug_TYP) from QS3_URLS), " +
				" QS3 AS (select drug,drug_TYP  from QS3_GET ), " +
				" QS4_POST AS (select processTable(drug,drug_TYP,'http://10.0.2.2:8083/getTransporters','drug,drug_TYP','drug','drug,transporter','fn=http://10.0.2.2:8083/,x=http://www.drugbank.ca,xs=http://www.w3.org/2001/XMLSchema,up=http://uniprot.org/uniprot,drug=http://www.drugbank.ca', '//x:row', 'drug', './x:drug', 'xs:string','transporter', './x:transporter', 'xs:string') as p from QS3), QS4_RAW AS (select inline(p) from QS4_POST), " +
				" QS4_TMP AS (select drug,drug_TYP,transporter,transporter_TYP from QS4_RAW),QS4 AS (SELECT drug,drug_TYP,transporter,transporter_TYP FROM QS4_TMP ) , " + 
				" QS5 AS  (SELECT  DISTINCT  transporter AS transporter,transporter_TYP AS transporter_TYP FROM QS4), " +
	//			" select * from QS5 ";
				" QS5A AS (select transporter, transporter_TYP, round((row_number() over (order by transporter))/10) as index from QS5), " +
				" QS6 AS  (SELECT CONCAT_WS('+', collect_set(transporter))  AS ids,5003 AS ids_TYP FROM QS5A GROUP BY INDEX), "+
	//			" select ids, ids_typ from QS6";
				" QS7_URL AS (select explode(array(  CONCAT('http://www.ebi.ac.uk/Tools/dbfetch/dbfetch?db=uniprot&format=xml&id=', QS6.ids) )) as url , url,ids,ids_TYP   from QS6 ), QS7_URLS AS (select * from QS7_URL), QS7_GET AS (select httpGet('geneFunction,t2,type,ids', 'url,ids,ids_TYP', '', 'GET', 'fn=http://10.0.2.2:8083/,x=http://www.drugbank.ca,xs=http://www.w3.org/2001/XMLSchema,up=http://uniprot.org/uniprot,drug=http://www.drugbank.ca', '/up:uniprot/up:entry/up:dbReference', 'geneFunction', './@id', 'xs:string','t2', '../up:accession[1]', 'xs:string','type', './@type', 'xs:string', url,ids,ids_TYP) as (geneFunction,geneFunction_TYP,t2,t2_TYP,type,type_TYP,ids,ids_TYP) from QS7_URLS), " +
				" QS7 AS (select geneFunction,geneFunction_TYP,t2,t2_TYP,type,type_TYP,ids,ids_TYP  from QS7_GET  WHERE (  type_TYP = 5001  AND type  =  'GO')  )," + 
				" QS8 AS  (SELECT  geneFunction AS geneFunction,geneFunction_TYP AS geneFunction_TYP,t2 AS t2,t2_TYP AS t2_TYP,type AS type,type_TYP AS type_TYP FROM QS7), " +
	//			" select distinct geneFunction, t2 from QS8";
				" QS9 AS ( SELECT QS1.transporter AS transporter,QS1.transporter_TYP AS transporter_TYP,QS1.drug as drug, QS1.drug_TYP as drug_TYP, QS8.geneFunction AS geneFunction,QS8.geneFunction_TYP AS geneFunction_TYP,QS8.t2 AS t2,QS8.t2_TYP AS t2_TYP,QS8.type AS type,QS8.type_TYP AS type_TYP FROM QS1,QS8 where QS1.transporter=QS8.t2 AND QS1.transporter_TYP=QS8.t2_TYP), " +
			//	" SELECT  drug as drug, drug_TYP as drug_TYP, t2 AS t2,t2_TYP AS t2_TYP,geneFunction AS geneFunction,geneFunction_TYP AS geneFunction_TYP FROM QS9 ";
				" QS10 AS (SELECT drug as drug, drug_TYP as drug_TYP, geneFunction AS geneFunction,geneFunction_TYP AS geneFunction_TYP FROM QS9), " +
				" QS11_POST AS (select processTable(drug,drug_TYP,geneFunction,geneFunction_TYP,'http://10.0.2.2:8083/computeGOSimilarity','drug,drug_TYP,GO,GO_TYP','drug,GO','drug,drug2,sim','fn=http://10.0.2.2:8083/,x=http://www.drugbank.ca,xs=http://www.w3.org/2001/XMLSchema,up=http://uniprot.org/uniprot,drug=http://www.drugbank.ca', '//row', 'drug', './drug1', 'xs:string','drug2', './drug2', 'xs:string','sim', './sim', 'xs:string') as p from QS10), QS11_RAW AS (select inline(p) from QS11_POST)," +
				" QS11_TMP AS (select drug,drug_TYP,drug2,drug2_TYP,sim,sim_TYP from QS11_RAW),QS11 AS (SELECT drug,drug_TYP,drug2,drug2_TYP,sim,sim_TYP FROM QS11_TMP ) " +
				" select * from QS11";
		//				" select ids, ids_typ from QS6";
		
		long time = System.currentTimeMillis();
		ResultSet res = stmt.executeQuery(sql);
		int rows = 0;
		while (res.next()) {
			int colCount = res.getMetaData().getColumnCount();
			for (int i = 1; i <= colCount; i++) {
				System.out.print(res.getString(i) + "\t");
			}
			System.out.println("\n");
			rows++;
			// System.out.println(res.getString(1) + "\t" + res.getString(2));
			// System.out.println(res.getString(1) + "\t" + res.getString(2) + "\t" + res.getString(3) +  "\t" + res.getString(4) + "\t" + res.getString(5));
		} 
		
		System.out.println("Query time:" + (System.currentTimeMillis() - time));
		System.out.println("Num of rows:" + rows);
	}
}
