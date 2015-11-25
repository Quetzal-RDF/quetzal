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
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import org.junit.Test;

import com.ibm.research.rdf.store.Context;
import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.Store.Backend;
import com.ibm.research.rdf.store.StoreManager;
import com.ibm.research.rdf.store.query.QueryProcessor;
import com.ibm.research.rdf.store.query.QueryProcessorFactory;
import com.ibm.research.rdf.store.runtime.service.types.LiteralInfoResultSet;
import com.ibm.research.rdf.store.sparql11.SparqlParserUtilities;
import com.ibm.research.rdf.store.sparql11.model.Query;

public class SPARQLBaseTests {
	static Connection conn = null;
	static Store store = null;
	static String datasetName = "db2RdfStore";
	static String username="db2inst1";
	static String schemaname="";
	static String passwd="sheruser";

	//initialize the store
	static {
		try {
			System.setProperty("stripQuotesForPlainLiteral", "true");
			Class.forName("com.ibm.db2.jcc.DB2Driver");
			conn = DriverManager.getConnection(
					"jdbc:db2://min-1.watson.ibm.com:50001/RDFJAZZ", username,
					passwd);
			
			store = StoreManager.connectStore(conn, Backend.valueOf("db2"), null, datasetName, Context.defaultContext);
						

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public  void testQuery1() throws Exception {
		String file = "jazz_queries/test1.sparql";
		System.err.println("Testing:" + file);
		@SuppressWarnings("unchecked")
		Query q = SparqlParserUtilities.parseSparqlFile(file, Collections.EMPTY_MAP);
		QueryProcessor qp = QueryProcessorFactory.create(file,conn, store, new Context());
		LiteralInfoResultSet rs=null;
		if(q.isSelectQuery()) rs=qp.execSelect(); 
		if(q.isDescribeQuery()) rs=qp.execDescribe();			
		int nOR=printResult(rs.getResultSet());
		
	}
	
	@Test
	public  void testQuery2() throws Exception {
		String file = "jazz_queries/test2.sparql";
		System.err.println("Testing:" + file);
		@SuppressWarnings("unchecked")
		Query q = SparqlParserUtilities.parseSparqlFile(file, Collections.EMPTY_MAP);
		QueryProcessor qp = QueryProcessorFactory.create(file,conn, store, new Context());
		LiteralInfoResultSet rs=null;
		if(q.isSelectQuery()) rs=qp.execSelect(); 
		if(q.isDescribeQuery()) rs=qp.execDescribe();			
		int nOR=printResult(rs.getResultSet());
		
	}
	
	@Test
	public  void testQuery3() throws Exception {
		String file = "jazz_queries/test3.sparql";
		System.err.println("Testing:" + file);
		@SuppressWarnings("unchecked")
		Query q = SparqlParserUtilities.parseSparqlFile(file, Collections.EMPTY_MAP);
		QueryProcessor qp = QueryProcessorFactory.create(file,conn, store, new Context());
		LiteralInfoResultSet rs=null;
		if(q.isSelectQuery()) rs=qp.execSelect(); 
		if(q.isDescribeQuery()) rs=qp.execDescribe();		
		int nOR=printResult(rs.getResultSet());
		
	}
	
	@Test
	public  void testQuery4() throws Exception {
		String file = "jazz_queries/test4.sparql";
		System.err.println("Testing:" + file);
		@SuppressWarnings("unchecked")
		Query q = SparqlParserUtilities.parseSparqlFile(file, Collections.EMPTY_MAP);
		QueryProcessor qp = QueryProcessorFactory.create(file,conn, store, new Context());
		LiteralInfoResultSet rs=null;
		if(q.isSelectQuery()) rs=qp.execSelect(); 
		if(q.isDescribeQuery()) rs=qp.execDescribe();		
		int nOR=printResult(rs.getResultSet());
		
	}
	
	@Test
	public  void testQuery5() throws Exception {
		String file = "jazz_queries/test5.sparql";
		System.err.println("Testing:" + file);
		@SuppressWarnings("unchecked")
		Query q = SparqlParserUtilities.parseSparqlFile(file, Collections.EMPTY_MAP);
		QueryProcessor qp = QueryProcessorFactory.create(file,conn, store, new Context());
		LiteralInfoResultSet rs=null;
		if(q.isSelectQuery()) rs=qp.execSelect(); 
		if(q.isDescribeQuery()) rs=qp.execDescribe();		
		int nOR=printResult(rs.getResultSet());
		
	}
	
	@Test
	public  void testQuery6() throws Exception {
		String file = "jazz_queries/test6.sparql";
		System.err.println("Testing:" + file);
		@SuppressWarnings("unchecked")
		Query q = SparqlParserUtilities.parseSparqlFile(file, Collections.EMPTY_MAP);
		QueryProcessor qp = QueryProcessorFactory.create(file,conn, store, new Context());
		LiteralInfoResultSet rs=null;
		if(q.isSelectQuery()) rs=qp.execSelect(); 
		if(q.isDescribeQuery()) rs=qp.execDescribe();		
		int nOR=printResult(rs.getResultSet());
		
	}
	
	@Test
	public  void testQuery7() throws Exception {
		String file = "jazz_queries/test7.sparql";
		System.err.println("Testing:" + file);
		Query q = SparqlParserUtilities.parseSparqlFile(file, Collections.EMPTY_MAP);
		QueryProcessor qp = QueryProcessorFactory.create(file,conn, store, new Context());
		LiteralInfoResultSet rs=null;
		if(q.isSelectQuery()) rs=qp.execSelect(); 
		if(q.isDescribeQuery()) rs=qp.execDescribe();		
		int nOR=printResult(rs.getResultSet());
		
	}
	
	@Test
	public  void testQuery8() throws Exception {
		String file = "jazz_queries/test8.sparql";
		System.err.println("Testing:" + file);
		Query q = SparqlParserUtilities.parseSparqlFile(file, Collections.EMPTY_MAP);
		QueryProcessor qp = QueryProcessorFactory.create(file,conn, store, new Context());
		LiteralInfoResultSet rs=null;
		if(q.isSelectQuery()) rs=qp.execSelect(); 
		if(q.isDescribeQuery()) rs=qp.execDescribe();		
		int nOR=printResult(rs.getResultSet());
		
	}
	@Test
	public  void testQuery9() throws Exception {
		String file = "jazz_queries/test9.sparql";
		System.err.println("Testing:" + file);
		Query q = SparqlParserUtilities.parseSparqlFile(file, Collections.EMPTY_MAP);
		QueryProcessor qp = QueryProcessorFactory.create(file,conn, store, new Context());
		LiteralInfoResultSet rs=null;
		if(q.isSelectQuery()) rs=qp.execSelect(); 
		if(q.isDescribeQuery()) rs=qp.execDescribe();		
		int nOR=printResult(rs.getResultSet());
		
	}
	
	@Test
	public  void testQuery10() throws Exception {
		String file = "jazz_queries/test10.sparql";
		System.err.println("Testing:" + file);
		Query q = SparqlParserUtilities.parseSparqlFile(file, Collections.EMPTY_MAP);
		QueryProcessor qp = QueryProcessorFactory.create(file,conn, store, new Context());
		LiteralInfoResultSet rs=null;
		if(q.isSelectQuery()) rs=qp.execSelect(); 
		if(q.isDescribeQuery()) rs=qp.execDescribe();		
		int nOR=printResult(rs.getResultSet());
		
	}
	
	@Test
	public  void testQuery11() throws Exception {
		String file = "jazz_queries/test11.sparql";
		System.err.println("Testing:" + file);
		Query q = SparqlParserUtilities.parseSparqlFile(file, Collections.EMPTY_MAP);
		QueryProcessor qp = QueryProcessorFactory.create(file,conn, store, new Context());
		LiteralInfoResultSet rs=null;
		if(q.isSelectQuery()) rs=qp.execSelect(); 
		if(q.isDescribeQuery()) rs=qp.execDescribe();		
		int nOR=printResult(rs.getResultSet());
		
	}
	
	@Test
	public  void testQuery12() throws Exception {
		String file = "jazz_queries/test12.sparql";
		System.err.println("Testing:" + file);
		Query q = SparqlParserUtilities.parseSparqlFile(file, Collections.EMPTY_MAP);
		QueryProcessor qp = QueryProcessorFactory.create(file,conn, store, new Context());
		LiteralInfoResultSet rs=null;
		if(q.isSelectQuery()) rs=qp.execSelect(); 
		if(q.isDescribeQuery()) rs=qp.execDescribe();		
		int nOR=printResult(rs.getResultSet());
		
	}
	
		@Test
	public  void testQuery13() throws Exception {
		String file = "jazz_queries/test13.sparql";
		System.err.println("Testing:" + file);
		Query q = SparqlParserUtilities.parseSparqlFile(file, Collections.EMPTY_MAP);
		QueryProcessor qp = QueryProcessorFactory.create(file,conn, store, new Context());
		LiteralInfoResultSet rs=null;
		if(q.isSelectQuery()) rs=qp.execSelect(); 
		if(q.isDescribeQuery()) rs=qp.execDescribe();		
		int nOR=printResult(rs.getResultSet());
		
	}
	
	@Test
	public  void testQuery14() throws Exception {
		String file = "jazz_queries/test14.sparql";
		System.err.println("Testing:" + file);
		Query q = SparqlParserUtilities.parseSparqlFile(file, Collections.EMPTY_MAP);
		QueryProcessor qp = QueryProcessorFactory.create(file,conn, store, new Context());
		LiteralInfoResultSet rs=null;
		if(q.isSelectQuery()) rs=qp.execSelect(); 
		if(q.isDescribeQuery()) rs=qp.execDescribe();		
		int nOR=printResult(rs.getResultSet());
		
	}
	
	@Test
	public  void testQuery18() throws Exception {
		String file = "jazz_queries/test18.sparql";
		System.err.println("Testing:" + file);
		Query q = SparqlParserUtilities.parseSparqlFile(file, Collections.EMPTY_MAP);
		QueryProcessor qp = QueryProcessorFactory.create(file,conn, store, new Context());
		LiteralInfoResultSet rs=null;
		if(q.isSelectQuery()) rs=qp.execSelect(); 
		if(q.isDescribeQuery()) rs=qp.execDescribe();		
		int nOR=printResult(rs.getResultSet());
		
	}
	
	@Test
	public  void testQuery19() throws Exception {
		String file = "jazz_queries/test19.sparql";
		System.err.println("Testing:" + file);
		Query q = SparqlParserUtilities.parseSparqlFile(file, Collections.EMPTY_MAP);
		QueryProcessor qp = QueryProcessorFactory.create(file,conn, store, new Context());
		LiteralInfoResultSet rs=null;
		if(q.isSelectQuery()) rs=qp.execSelect(); 
		if(q.isDescribeQuery()) rs=qp.execDescribe();		
		int nOR=printResult(rs.getResultSet());
		
	}
	

	@Test
	public  void testQuery20() throws Exception {
		String file = "jazz_queries/test20.sparql";
		System.err.println("Testing:" + file);
		Query q = SparqlParserUtilities.parseSparqlFile(file, Collections.EMPTY_MAP);
		QueryProcessor qp = QueryProcessorFactory.create(file,conn, store, new Context());
		LiteralInfoResultSet rs=null;
		if(q.isSelectQuery()) rs=qp.execSelect(); 
		if(q.isDescribeQuery()) rs=qp.execDescribe();		
		int nOR=printResult(rs.getResultSet());
		
	}
	
	private static int printResult(/*String sparqlQuery,*/  ResultSet rst){
		//System.out.println("QUERY: "+sparqlQuery);
		//returns the number of rows returned

		
		
		System.out.println("RESULT: ");
		
		ArrayList<String> row;
		int i, sz;
		int numberOfRows=0;
		if (rst == null) {
			return 0;
		}
		try {
			ResultSetMetaData rsmd = rst.getMetaData();
			int numberOfColumns = rsmd.getColumnCount();
			StringBuffer resultHeader=new StringBuffer();
			for(int nCols=1;nCols<=numberOfColumns;nCols++)
				{
				resultHeader.append(rsmd.getColumnLabel(nCols));
				resultHeader.append("|||");
				}
			System.out.println("============================================================");
			System.out.println(resultHeader.toString());
			System.out.println("============================================================");
			while(rst.next()) {
				numberOfRows++;
				row = new ArrayList<String>();
				sz = rst.getMetaData().getColumnCount();
				for(i = 1; i <= sz; i++) row.add(rst.getString(i));
				System.out.println(row);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("============================================================");
		return numberOfRows;
	}
}
