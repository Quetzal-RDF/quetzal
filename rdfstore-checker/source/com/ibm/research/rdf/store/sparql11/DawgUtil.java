package com.ibm.research.rdf.store.sparql11;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.ibm.rdf.store.dawg.queries.BaseUriSparqlResultReader;
import com.ibm.rdf.store.dawg.queries.DawgStore;
import com.ibm.rdf.store.dawg.queries.QueryTests;
import com.ibm.rdf.store.dawg.queries.QueryTests.DataSet;
import com.ibm.rdf.store.dawg.queries.QueryTests.QueryTest;
import com.ibm.rdf.store.dawg.queries.SparqlSelectResult;
import com.ibm.wala.util.Predicate;
import com.ibm.wala.util.collections.HashSetFactory;

import static com.ibm.research.rdf.store.sparql11.Drivers.*;

public class DawgUtil {

	public static abstract class DawgVerification {
		abstract String getName();
		
		abstract URL getDataset();
		
		abstract String getQueryFile() throws IOException;
		
		abstract SparqlSelectResult getResult() throws MalformedURLException, ParserConfigurationException, SAXException, IOException, URISyntaxException;

		public void verify() throws MalformedURLException, URISyntaxException, ParserConfigurationException, SAXException, IOException {
			System.err.println("*** test *** " + getName());
			runVerify(getDataset(), getQueryFile(), getResult());
		}

		public void fabricate() throws MalformedURLException, URISyntaxException, ParserConfigurationException, SAXException, IOException {
			System.err.println("*** test *** " + getName());
			runFabricate(getQueryFile(), getResult());
		}

		public void repair() throws MalformedURLException, URISyntaxException, ParserConfigurationException, SAXException, IOException {
			System.err.println("*** test *** " + getName());
			runRepair(getDataset(), getQueryFile(), getResult());
		}
}
	
	public static Set<DawgVerification> dawgTests(Predicate<QueryTest> f) throws SQLException, IOException {
		Set<DawgVerification> result = HashSetFactory.make();
		Map<DataSet, Set<QueryTest>> tests = QueryTests.getDataSets();
		for(final Map.Entry<DataSet,Set<QueryTest>> e : tests.entrySet()) {
			for(final QueryTest q : e.getValue()) {
				if (f.test(q)) {
					result.add(new DawgVerification() {
						@Override
						public URL getDataset() {
							String ds = e.getKey().computeDatabaseName();
							return DawgStore.class.getClassLoader().getResource("queryTests/" + ds + (ds.endsWith("nq")? "/data.nq": "/data.nt"));
						}

						@Override
						public String getQueryFile() throws IOException {
							return "file://" + q.getQueryFile().getAbsolutePath();
						}

						@Override
						public SparqlSelectResult getResult() throws MalformedURLException, ParserConfigurationException, SAXException, IOException, URISyntaxException {
							return getDawgSolution(q.getResult());
						}

						@Override
						String getName() {
							return q.makeTestName();
						}
					});
				}
			}
		}
		return result;
	}
	
	public static SparqlSelectResult getDawgSolution(String testFileName)
			throws ParserConfigurationException, SAXException, IOException,
			MalformedURLException, URISyntaxException {
		URL url = DawgStore.getTestFile(testFileName);
		String file = url.getPath();
		return new BaseUriSparqlResultReader(new URI(testFileName), file, Drivers.getSolution("file://" + file));
	}

}
