package com.ibm.research.rdf.store.utilities;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.api.java.UDF1;
import org.apache.spark.sql.hive.HiveContext;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.SparkConf;

public class TestWebServiceGet 
{
    public static void main( String[] args )
    {   	
 //   	SparkConf conf = new SparkConf().setAppName("App-mt").setMaster("local[2]");
 //      	SparkConf conf = new SparkConf().setAppName("App-mt").setMaster("spark://Kavithas-MBP.home:7077");
		SparkConf conf = new SparkConf().setAppName("App-mt").setMaster("spark://kavithas-mbp.watson.ibm.com:7077");
    
    	JavaSparkContext sc = new JavaSparkContext(conf);
    	
    	HiveContext sqlContext = new HiveContext(sc.sc());
    	DataFrame urls = sqlContext.read().json("/tmp/urls.json");

    	urls.registerTempTable("urls");
    	DataFrame temp = sqlContext.sql("select * from urls");
    	temp.show();
    	
 	   	sqlContext.sql("add jar /tmp/quetzal.jar");
		sqlContext.sql("create temporary function webservice as 'com.ibm.research.rdf.store.utilities.WebServiceGetUDTF'");
		DataFrame drugs = sqlContext.sql("select webservice(\"drug,id,action\", \"url\", \"\", \"GET\", \"xs=http://www.w3.org/2001/XMLSchema\", \"//row\",\"drug\",\"./drug\","
				+ " \"<string>\", \"id\", \"./id\",\"<string>\", \"action\", \"./action\", \"<string>\", url) as (drug, drug_typ, id, id_typ, action, action_typ) from urls");
		drugs.show();
		System.out.println("Num rows:" + drugs.count());
    }
 
}
