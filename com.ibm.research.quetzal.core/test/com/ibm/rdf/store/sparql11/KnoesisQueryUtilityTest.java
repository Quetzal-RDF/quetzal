package com.ibm.rdf.store.sparql11;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.ibm.wala.util.io.Streams;

public class KnoesisQueryUtilityTest<D> extends TestRunner<D> {

	private static final int[] answers = new int[]{ 9757, 59109 };
	
	public KnoesisQueryUtilityTest(D data, DatabaseEngine<D> engine, int[] answers) {
		super(data, engine, answers);
	}

	public static class PSQLKnoesisTests extends KnoesisQueryUtilityTest<PSQLTestData> {
		
		private static final PSQLTestData data = new PSQLTestData(
			System.getenv("JDBC_URL"), System.getenv("KB"),
			System.getenv("DB_USER"), System.getenv("DB_PASSWORD"),
			System.getenv("DB_SCHEMA"), false);

		private static final PSQLEngine engine = new PSQLEngine();
		
		public PSQLKnoesisTests() {
			super(data, engine, answers);
		}
	}

	public static class BigQueryKnoesisTests extends KnoesisQueryUtilityTest<BigQueryTestData> {
		
		private static final BigQueryTestData data = new BigQueryTestData(
			System.getenv("DB_DB"), System.getenv("KB"),
			System.getenv("DB_USER"), System.getenv("DB_PASSWORD"),
			System.getenv("DB_SCHEMA"), false);

		private static final BigQueryEngine engine = new BigQueryEngine();
		
		public BigQueryKnoesisTests() {
			super(data, engine, answers);
		}
	}

	private String getQuery(String file) throws IllegalArgumentException, IOException {
		InputStream is = getClass().getClassLoader().getResourceAsStream(file);
		return new String(Streams.inputStream2ByteArray(is));
	}
	
	@Test
	public void testQuery1() throws IllegalArgumentException, IOException {
		String sparql = getQuery("queries/knoesis/q1.sparql");
		executeSparql(sparql, answers[0]);
	}

	@Test
	public void testQuery2() throws IllegalArgumentException, IOException {
		String sparql = getQuery("queries/knoesis/q2.sparql");
		executeSparql(sparql, answers[1]);
	}
}
