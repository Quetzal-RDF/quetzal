package com.ibm.rdf.store.sparql11;

import org.junit.Test;

import com.ibm.rdf.store.sparql11.TestRunner.DB2TestData;

public class SinatraTests extends TestRunner<DB2TestData> {
	private static final DB2TestData data = TestRunner.DB2TestData.getStore("jdbc:db2://localhost:8997/sinatra", "lewtan", "db2inst1", "db2admin", "db2inst1", false);

	public SinatraTests() {
		super(data, new DB2Engine(), null);
	}

	//@Test
	public void testZipToFips() {
		executeSparql("select ?z where { _:x <http://tables.sinatra.ibm.com/ZipCode> 10463 ; <http://tables.sinatra.ibm.com/FIPS> ?z . }", 1);
	}

	//@Test
	public void testCountLoans() {
		executeSparql("select ?z where { ?z a <http://lewtandata.sinatra.ibm.com/type#ABSNetLoan>. }", 36215);
	}

	//@Test
	public void testCountTranches() {
		executeSparql("select ?z where { ?z a <http://lewtandata.sinatra.ibm.com/type#ABSNetLoanTranche> . }", 250);
	}
	
	//@Test
	public void testHighUnemploymentRate() {
		executeSparql("select ?z where { _:y <http://tables.sinatra.ibm.com/FIPS> ?f ; <http://tables.sinatra.ibm.com/UnemploymentRate> ?u ; <http://tables.sinatra.ibm.com/Period> ?d . _:x <http://tables.sinatra.ibm.com/ZipCode> ?z ; <http://tables.sinatra.ibm.com/FIPS> ?f . FILTER (?u > 10.0 && ?d = \"2014-10\"^^<http://www.w3.org/2001/XMLSchema#gMonthYear>) }", 1038);
	}

	//@Test
	public void testLoansInHighUnemploymentRateAreas() {
		executeSparql("select ?l where { ?l <http://lewtandata.sinatra.ibm.com/PropertyZip> ?z . _:y <http://tables.sinatra.ibm.com/FIPS> ?f ; <http://tables.sinatra.ibm.com/UnemploymentRate> ?u ; <http://tables.sinatra.ibm.com/Period> ?d . _:x <http://tables.sinatra.ibm.com/ZipCode> ?z ; <http://tables.sinatra.ibm.com/FIPS> ?f . FILTER (?u > 10.0 && ?d = \"2014-10\"^^<http://www.w3.org/2001/XMLSchema#gMonthYear>) }", 496);
	}

	//@Test
	public void testRisingUnemploymentRate() {
		executeSparql(
			"select ?z where {" + 
		    " _:y1 <http://tables.sinatra.ibm.com/FIPS> ?f ; <http://tables.sinatra.ibm.com/UnemploymentRate> ?u1 ; <http://tables.sinatra.ibm.com/Period> ?d1 . " + 
		    " _:y2 <http://tables.sinatra.ibm.com/FIPS> ?f ; <http://tables.sinatra.ibm.com/UnemploymentRate> ?u2 ; <http://tables.sinatra.ibm.com/Period> ?d2 . " + 
			" _:x <http://tables.sinatra.ibm.com/ZipCode> ?z ; <http://tables.sinatra.ibm.com/FIPS> ?f . " + 
			"FILTER (?u1 > ?u2 &&" + 
			" ?d2 = \"2014-09\"^^<http://www.w3.org/2001/XMLSchema#gMonthYear> &&" +
			" ?d1 = \"2014-10\"^^<http://www.w3.org/2001/XMLSchema#gMonthYear>) }", 
			1038);
	}

	//@Test
	public void testLoansInAreasOfRisingUnemploymentRate() {
		executeSparql(
			"select ?l where {" + 
		    " ?l a <http://lewtandata.sinatra.ibm.com/type#ABSNetLoan> ; " + 
			"    <http://lewtandata.sinatra.ibm.com/PropertyZip> ?z ." +
		    " _:y1 <http://tables.sinatra.ibm.com/FIPS> ?f ; <http://tables.sinatra.ibm.com/UnemploymentRate> ?u1 ; <http://tables.sinatra.ibm.com/Period> ?d1 . " + 
		    " _:y2 <http://tables.sinatra.ibm.com/FIPS> ?f ; <http://tables.sinatra.ibm.com/UnemploymentRate> ?u2 ; <http://tables.sinatra.ibm.com/Period> ?d2 . " + 
			" _:x <http://tables.sinatra.ibm.com/ZipCode> ?z ; <http://tables.sinatra.ibm.com/FIPS> ?f . " + 
			"FILTER (?u1 > ?u2 &&" + 
			" ?d2 = \"2014-09\"^^<http://www.w3.org/2001/XMLSchema#gMonthYear> &&" +
			" ?d1 = \"2014-10\"^^<http://www.w3.org/2001/XMLSchema#gMonthYear>) }", 
			1038);
	}

	//@Test
	public void testPoolBalancesInAreasWithHighUnemploymentRate() {
		executeSparql(
			"select ?p (SUM(?b) AS ?balance) where {" + 
			" ?p a <http://lewtandata.sinatra.ibm.com/type#ABSNetLoanTranche> . " +
			" ?l a <http://lewtandata.sinatra.ibm.com/type#ABSNetLoan> ; " + 
			"    <http://lewtandata.sinatra.ibm.com/ABSNetLoanPool> ?p ; " +
			"    <http://lewtandata.sinatra.ibm.com/OriginalLoanBalance> ?b ; " +
		    "    <http://lewtandata.sinatra.ibm.com/PropertyZip> ?z ." +
		    " _:y <http://tables.sinatra.ibm.com/FIPS> ?f ; " +
		    "     <http://tables.sinatra.ibm.com/UnemploymentRate> ?u ; " + 
		    "     <http://tables.sinatra.ibm.com/Period> ?d . " + 
		    " _:x <http://tables.sinatra.ibm.com/ZipCode> ?z ; " +
		    "     <http://tables.sinatra.ibm.com/FIPS> ?f . " + 
		    " FILTER (?u > 10.0 && ?d = \"2014-10\"^^<http://www.w3.org/2001/XMLSchema#gMonthYear>) }" +
			" GROUP BY ?p", 
			1038);
	}

	@Test
	public void testPoolBalancesInAreasOfRisingUnemploymentRate() {
		executeSparql(
			"select ?p (SUM(?b) AS ?balance) where {" + 
			" ?p a <http://lewtandata.sinatra.ibm.com/type#ABSNetLoanTranche> . " +
			" ?l a <http://lewtandata.sinatra.ibm.com/type#ABSNetLoan> ; " + 
			"    <http://lewtandata.sinatra.ibm.com/ABSNetLoanPool> ?p ; " +
			"    <http://lewtandata.sinatra.ibm.com/OriginalLoanBalance> ?b ; " +
		    "    <http://lewtandata.sinatra.ibm.com/PropertyZip> ?z ." +
		    " _:y1 <http://tables.sinatra.ibm.com/FIPS> ?f ; <http://tables.sinatra.ibm.com/UnemploymentRate> ?u1 ; <http://tables.sinatra.ibm.com/Period> ?d1 . " + 
		    " _:y2 <http://tables.sinatra.ibm.com/FIPS> ?f ; <http://tables.sinatra.ibm.com/UnemploymentRate> ?u2 ; <http://tables.sinatra.ibm.com/Period> ?d2 . " + 
			" _:x <http://tables.sinatra.ibm.com/ZipCode> ?z ; <http://tables.sinatra.ibm.com/FIPS> ?f . " + 
			"FILTER (?u1 > ?u2 &&" + 
			" ?d2 = \"2014-09\"^^<http://www.w3.org/2001/XMLSchema#gMonthYear> &&" +
			" ?d1 = \"2014-10\"^^<http://www.w3.org/2001/XMLSchema#gMonthYear>) }" +
			" GROUP BY ?p", 
			1038);
	}

	@Test
	public void testPoolFractionInAreasOfRisingUnemploymentRate() {
		executeSparql(
			"select ?p (SUM(?b)/SUM(?m) AS ?balance) where {" + 
			" ?p a <http://lewtandata.sinatra.ibm.com/type#ABSNetLoanTranche> ; " +
			"    <http://lewtandata.sinatra.ibm.com/OriginalBalance> ?m . " +
			" ?l a <http://lewtandata.sinatra.ibm.com/type#ABSNetLoan> ; " + 
			"    <http://lewtandata.sinatra.ibm.com/ABSNetLoanPool> ?p ; " +
			"    <http://lewtandata.sinatra.ibm.com/OriginalLoanBalance> ?b ; " +
		    "    <http://lewtandata.sinatra.ibm.com/PropertyZip> ?z ." +
		    " _:y1 <http://tables.sinatra.ibm.com/FIPS> ?f ; <http://tables.sinatra.ibm.com/UnemploymentRate> ?u1 ; <http://tables.sinatra.ibm.com/Period> ?d1 . " + 
		    " _:y2 <http://tables.sinatra.ibm.com/FIPS> ?f ; <http://tables.sinatra.ibm.com/UnemploymentRate> ?u2 ; <http://tables.sinatra.ibm.com/Period> ?d2 . " + 
			" _:x <http://tables.sinatra.ibm.com/ZipCode> ?z ; <http://tables.sinatra.ibm.com/FIPS> ?f . " + 
			"FILTER (?u1 > ?u2 &&" + 
			" ?d2 = \"2014-09\"^^<http://www.w3.org/2001/XMLSchema#gMonthYear> &&" +
			" ?d1 = \"2014-10\"^^<http://www.w3.org/2001/XMLSchema#gMonthYear>) }" +
			" GROUP BY ?p", 
			1038);
	}

}
