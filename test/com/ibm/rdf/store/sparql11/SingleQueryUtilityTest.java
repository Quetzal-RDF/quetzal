package com.ibm.rdf.store.sparql11;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.ibm.rdf.store.Context;
import com.ibm.rdf.store.Store;
import com.ibm.rdf.store.StoreManager;
import com.ibm.rdf.store.query.QueryProcessor;
import com.ibm.rdf.store.query.QueryProcessorFactory;
import com.ibm.rdf.store.runtime.service.types.LiteralInfoResultSet;
import com.ibm.rdf.store.sparql11.model.Query;

public abstract class SingleQueryUtilityTest {
	Connection conn = null;
	Store store = null;
	Context ctx=null;

	private final String datasetName;
	private final String username;
	private final String passwd;
	private final String jdbcUrl;
	private final String queryDir;
	private final String schemaName;
	private int[] answers;
	
	static final int[] lubm10mAnswers = {4, 212, 6, 34, 719, 838892, 67, 7790, 21872, 4, 0, 0, 380, 636529};
	static final int[] lubm100mAnswers = {4, 1831, 6, 34, 719, 7508706, 67, 7790, 195954, 4, 0, 0, 3409, 5695568};
	
	protected SingleQueryUtilityTest(String datasetName, String username,
			String passwd, String jdbcUrl, String queryDir, String schemaName, int[] ans) {
		this.datasetName = datasetName;
		this.username = username;
		this.passwd = passwd;
		this.jdbcUrl = jdbcUrl;
		this.queryDir = queryDir;
		this.schemaName = schemaName;
		this.answers = ans;
	}

//	@RunWith(com.ibm.rdf.store.testing.RandomizedRepeatRunner.class)
//	@RandomizedRepeat(8)
	public static class Reversed10M extends SingleQueryUtilityTest {
		public Reversed10M() {
			super("lubm10m_r", "db2inst1", "db2inst1", "jdbc:db2://min-1.watson.ibm.com:50001/testrev", "../rdfstore-data/tasos_queries/", "db2inst4", lubm10mAnswers);
		}
	}

	//initialize the store
	@Before
	public void init() {
		try {
			Class.forName("com.ibm.db2.jcc.DB2Driver");
			conn = DriverManager.getConnection(jdbcUrl, username, passwd);
			ctx = new Context();
//			ctx.set(Context.unionDefaultGraph,new Boolean(false));

			store = StoreManager.connectStore(conn, "db2", schemaName, datasetName, Context.defaultContext);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private int executeQuery(String file, int queryNum) {
		return execute(file,false, queryNum);
	}
	
	private int execute(String file, boolean isDescribeWithoutRows, int queryNum) {
		int nOR = -1;
		@SuppressWarnings("unchecked")
		Query q = SparqlParserUtilities.parseSparqlFile(file, Collections.EMPTY_MAP);
		QueryProcessor qp = QueryProcessorFactory.create(file,conn, store, ctx);
		LiteralInfoResultSet rs=null;
		long time = System.currentTimeMillis();
		if(q.isSelectQuery()) {
			rs=qp.execSelect();
			nOR=printResult(rs.getResultSet());
			
		}
		
		if(q.isDescribeQuery()) { 
			rs=qp.execDescribe();
			if ( isDescribeWithoutRows ) {
				 nOR = 0;
			}
			else {
				nOR=printResult(rs.getResultSet());
				
			}
			
		}
		System.out.println("Query: " + file + " time:" + (System.currentTimeMillis() - time) + " rows: " + nOR);
		
		Assert.assertTrue(nOR > 0);
		return nOR;
		
		
	}
	
	  @Test
   public void testQuery0() throws Exception
      {
      String file = queryDir + "q0.sparql";
      System.err.println("Testing:" + file);
      int result = executeQuery(file, 0);
      System.out.println(file + " has : " + result + " rows");
      }

   @Test
   public void testQuery1() throws Exception
      {
      String file = queryDir + "q1.sparql";
      System.err.println("Testing:" + file);
      int result = executeQuery(file, 0);
      System.out.println(file + " has : " + result + " rows");
      }

   @Test
   public void testQuery2() throws Exception
      {
      String file = queryDir + "q2.sparql";
      System.err.println("Testing:" + file);
      int result = executeQuery(file, 0);
      System.out.println(file + " has : " + result + " rows");
      }

   @Test
   public void testQuery3() throws Exception
      {
      String file = queryDir + "q3.sparql";
      System.err.println("Testing:" + file);
      int result = executeQuery(file, 0);
      System.out.println(file + " has : " + result + " rows");
      }
	
	private static int printResult(/*String sparqlQuery,*/  ResultSet rst){

		System.out.println("RESULT: ");
		
		ArrayList<String> row;
		int i, sz;
		int numberOfRows=0;
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
			} 
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("============================================================");
		System.out.println("Returned " + numberOfRows + " rows");
		return numberOfRows;
	}
}
