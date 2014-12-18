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
 package com.ibm.rdf.store.sparql11;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import com.ibm.research.rdf.store.Store;


public class SharkJDBCClientTest {

	public static void main(String[] args) throws Exception {
		for (Store.Backend be: Store.Backend.values()) {
			System.out.println("name = "+ be.name()+" ordinal ="+be.ordinal());
		}
		 try {
		      Class.forName("org.apache.hive.jdbc.HiveDriver");
		    } catch (ClassNotFoundException e) {
		      // TODO Auto-generated catch block
		      e.printStackTrace();
		      System.exit(1);
		    }
		    Connection con = DriverManager.getConnection("jdbc:hive2://9.12.196.243:10000/default", "root", "nkoutche");
		    Statement stmt = con.createStatement();
		   /* stmt.executeQuery("drop table src");
		    stmt.executeQuery("drop table src_cached");
			   */
		    String tableName = "kbt_TOPKSTATS";//"src_cached2";
		   /* stmt.executeQuery("drop table " + tableName);
		    ResultSet res = stmt.executeQuery("create table " + tableName + " (key int, value string)");*/
		    //show databases
		    String sql = "show databases ";//'" + tableName + "'";
		    System.out.println("Running: " + sql);
		    ResultSet res = stmt.executeQuery(sql);
		    while (res.next()) {
		      System.out.println(res.getString(1));
		    }
		    // show schemas
		    sql = "show schemas ";//'" + tableName + "'";
		    System.out.println("Running: " + sql);
		    res = stmt.executeQuery(sql);
		    while (res.next()) {
		      System.out.println(res.getString(1));
		    }
		    // show tables
		    sql = "show tables ";//'" + tableName + "'";
		    System.out.println("Running: " + sql);
		    res = stmt.executeQuery(sql);
		    while (res.next()) {
		      System.out.println(res.getString(1));
		    }
		    // describe table
		   /* sql = "describe " + tableName;
		    System.out.println("Running: " + sql);
		    res = stmt.executeQuery(sql);
		    while (res.next()) {
		      System.out.println(res.getString(1) + "\t" + res.getString(2));
		    }
		 */
		    // load data into table
		    // NOTE: filepath has to be local to the hive server
		    // NOTE: /tmp/a.txt is a ctrl-A separated file with two fields per line
		  /*  String filepath = "/tmp/a.txt";
		    sql = "load data local inpath '" + filepath + "' into table " + tableName;
		    System.out.println("Running: " + sql);
		    res = stmt.executeQuery(sql);*/
		 
		    // select * query
		    //sql = "select * from " + tableName;
		   /* sql = "select  entity, count FROM ("+
		    		 "SELECT entity, count FROM (SELECT entity, count(*) AS count FROM default.kbt_DS GROUP BY entity) Q0 "+
		    		 " UNION ALL "+
		    		 "SELECT entity, count FROM (SELECT entity, count(*) AS count FROM default.kbt_DS GROUP BY entity) Q1 "+ 
		    		 ")	 Q2 WHERE count > 100000 ";*/
		    sql = "select  entity, sum(count) AS count FROM ( "+
				  "SELECT entity, count FROM (SELECT entity, count(*) AS count FROM default.kbt_DS GROUP BY entity) Q0 "+
		    		"UNION ALL  "+
					"SELECT entity, count FROM (SELECT entry AS entity, count(*) AS count FROM  "+
					"		( "+
					"		SELECT entry, gid, prop0 AS prop, val0 AS val FROM default.kbt_DPH WHERE prop0 IS NOT NULL "+
					"		UNION ALL SELECT entry, gid, prop1 AS prop, val1 AS val FROM default.kbt_DPH WHERE prop1 IS NOT NULL "+
					"		UNION ALL SELECT entry, gid, prop2 AS prop, val2 AS val FROM default.kbt_DPH WHERE prop2 IS NOT NULL  "+
					"		UNION ALL SELECT entry, gid, prop3 AS prop, val3 AS val FROM default.kbt_DPH WHERE prop3 IS NOT NULL  "+
					"		UNION ALL SELECT entry, gid, prop4 AS prop, val4 AS val FROM default.kbt_DPH WHERE prop4 IS NOT NULL  "+
					"		UNION ALL SELECT entry, gid, prop5 AS prop, val5 AS val FROM default.kbt_DPH WHERE prop5 IS NOT NULL  "+
					"		UNION ALL SELECT entry, gid, prop6 AS prop, val6 AS val FROM default.kbt_DPH WHERE prop6 IS NOT NULL "+
					"		UNION ALL SELECT entry, gid, prop7 AS prop, val7 AS val FROM default.kbt_DPH WHERE prop7 IS NOT NULL  "+
					"		UNION ALL SELECT entry, gid, prop8 AS prop, val8 AS val FROM default.kbt_DPH WHERE prop8 IS NOT NULL  "+
					"		UNION ALL SELECT entry, gid, prop9 AS prop, val9 AS val FROM default.kbt_DPH WHERE prop9 IS NOT NULL) LT  "+	
					"			 WHERE LT.VAL NOT LIKE 'lid:%' GROUP BY entry)  Q1)  "+
					"			 Q2 WHERE count > 1 GROUP BY entity ORDER BY count";// 100000 ";
		    sql = "SELECT  'subj', 'null', entity, 'null', 'null', sum(count)  FROM (SELECT entity, count FROM (SELECT entity, count(*) AS count FROM default.kbt_DS GROUP BY entity) Q0 UNION ALL SELECT entity, count FROM (SELECT LT.entry AS entity, count(*) AS count FROM (SELECT entry, gid, prop0 AS prop, val0 AS val FROM default.kbt_DPH WHERE prop0 IS NOT NULL UNION ALL SELECT entry, gid, prop1 AS prop, val1 AS val FROM default.kbt_DPH WHERE prop1 IS NOT NULL UNION ALL SELECT entry, gid, prop2 AS prop, val2 AS val FROM default.kbt_DPH WHERE prop2 IS NOT NULL UNION ALL SELECT entry, gid, prop3 AS prop, val3 AS val FROM default.kbt_DPH WHERE prop3 IS NOT NULL UNION ALL SELECT entry, gid, prop4 AS prop, val4 AS val FROM default.kbt_DPH WHERE prop4 IS NOT NULL UNION ALL SELECT entry, gid, prop5 AS prop, val5 AS val FROM default.kbt_DPH WHERE prop5 IS NOT NULL UNION ALL SELECT entry, gid, prop6 AS prop, val6 AS val FROM default.kbt_DPH WHERE prop6 IS NOT NULL UNION ALL SELECT entry, gid, prop7 AS prop, val7 AS val FROM default.kbt_DPH WHERE prop7 IS NOT NULL UNION ALL SELECT entry, gid, prop8 AS prop, val8 AS val FROM default.kbt_DPH WHERE prop8 IS NOT NULL UNION ALL SELECT entry, gid, prop9 AS prop, val9 AS val FROM default.kbt_DPH WHERE prop9 IS NOT NULL) LT WHERE LT.VAL NOT LIKE 'lid:%' GROUP BY LT.entry) Q1) Q2 WHERE count > 1 GROUP BY entity";
		    sql = "select * from kb_dph lateral view expode(array())  C AS pp limit 10 ";
		    sql = "select entry, prop, val from kb_dph lateral view explode(map(prop1, val1, prop2, val2, prop3, val3)) m AS prop, val";
		    sql = "select entry, prop  from kb_dph lateral view explode(array(prop1, prop2, prop3)) a AS prop";
			sql = "describe extended  uobm1a_rph_inmem";
			sql ="SELECT  s AS s FROM (SELECT entry AS v,typ AS v_TYP,T.val0 AS s FROM default.d_cast_data_nt_RPH  T  WHERE   (T.prop0 = 'http://example.org/p')  AND    ( (CASE  WHEN CAST( ( CASE   WHEN T.entry = 'true' THEN 'true'    WHEN T.typ BETWEEN 5007 AND 5022 AND CAST(T.entry AS FLOAT) <> 0 THEN 'true' 	else null end ) AS boolean) IS NOT NULL THEN 'boolean'		else	  'string'	 end)  =  'boolean' )     AND  T.gid = 'DEF'  )  QS0";
			//sql = "select * from kbt_dph  limit 10 ";
		   // sql ="select * from kb_dph";
		    //sql =" select 'graph', gid, 'null', 'null', 'null', count(*) as COUNT  from default.kbt_DS group by GID having count(*) > 10000 order by  count  limit 5000";
		    System.out.println("Running: " + sql);
		    res = stmt.executeQuery(sql);
		    while (res.next()) {
		    	System.out.println(res.getString(1));//+ "\t" + res.getString(2) + "\t"+res.getString(3));
		      //System.out.println(String.valueOf(res.getString(1)) + "\t" + res.getString(2)+"\t"+String.valueOf(res.getString(3)) + "\t" + res.getString(4)+ "\t" + res.getString(5)+ "\t" + res.getString(6)+"\n");
		    }
		 
		    // regular hive query
		    sql = "select count(1) from " + tableName;
		    System.out.println("Running: " + sql);
		    res = stmt.executeQuery(sql);
		    while (res.next()) {
		      System.out.println(res.getString(1));
		    }

	}

}
