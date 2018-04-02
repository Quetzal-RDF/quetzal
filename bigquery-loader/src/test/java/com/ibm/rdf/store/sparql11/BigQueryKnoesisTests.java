package com.ibm.rdf.store.sparql11;

public class BigQueryKnoesisTests extends KnoesisQueryUtilityTest<BigQueryTestData> {
	
	private static final BigQueryTestData data = new BigQueryTestData(
		System.getenv("DB_DB"), System.getenv("KB"),
		System.getenv("DB_USER"), System.getenv("DB_PASSWORD"),
		System.getenv("DB_SCHEMA"), false);

	private static final BigQueryEngine engine = new BigQueryEngine();
	
	public BigQueryKnoesisTests() {
		super(data, engine, KnoesisQueryUtilityTest.answers);
	}
}