package com.ibm.research.rdf.store.sparql11;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;
import junitx.framework.OrderedTestSuite;

import org.junit.Test;
import org.xml.sax.SAXException;

import com.ibm.rdf.store.dawg.queries.QueryTests.QueryTest;
import com.ibm.research.rdf.store.sparql11.DawgUtil.DawgVerification;
import com.ibm.wala.util.Predicate;
import com.ibm.wala.util.functions.VoidFunction;

public class DawgTest extends TestCase {

	private static final class DawgFabricateCase implements
			VoidFunction<DawgVerification> {
		@Override
		public void apply(DawgVerification arg0) {
			try {
				arg0.fabricate();
			} catch ( URISyntaxException
					| ParserConfigurationException 
					| SAXException
					| IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static final class DawgRepairCase implements VoidFunction<DawgVerification> {
		@Override
		public void apply(DawgVerification arg0) {
			try {
				arg0.repair();
			} catch ( URISyntaxException
					| ParserConfigurationException 
					| SAXException
					| IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static final class DawgTestCase implements
			VoidFunction<DawgVerification> {
		@Override
		public void apply(DawgVerification arg0) {
			try {
				arg0.verify();
			} catch ( URISyntaxException
					| ParserConfigurationException 
					| SAXException
					| IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static abstract class ComparisonTest {
		private final boolean leftNonEmpty;
		private final boolean rightNonEmpty;
		private final int bound;
		private final String left;
		private final String right;
		
		public ComparisonTest(boolean leftNonEmpty, boolean rightNonEmpty, int bound, final String left, final String right) {
			this.leftNonEmpty = leftNonEmpty;
			this.rightNonEmpty = rightNonEmpty;
			this.bound = bound;
			this.left = left;
			this.right = right;
		}
		
		@Test
		public void test() throws SQLException, IOException, URISyntaxException {
			Set<DawgVerification> tests = DawgUtil.dawgTests(new Predicate<QueryTest>() {
				@Override
				public boolean test(QueryTest arg0) {
					return arg0.getQuery().contains(left) || arg0.getQuery().contains(right);
				}
			});
			
			Iterator<DawgVerification> ts = tests.iterator();
			String lq = ts.next().getQueryFile();
			String rq = ts.next().getQueryFile();
			
			Drivers.run(lq, rq, leftNonEmpty, rightNonEmpty, bound);
		}
	}
	
	public static class OptionalFilterCompare1And2 extends ComparisonTest {

		public OptionalFilterCompare1And2() {
			super(true, true, 5, "optional-filter/expr-1.rq", "optional-filter/expr-2.rq");
		}
		
	}

	public static class OptionalCompareQ1AndQ3 extends ComparisonTest {

		public OptionalCompareQ1AndQ3() {
			super(false, false, 5, "optional/q-opt-1.rq", "optional/q-opt-3.rq");
		}
		
	}

	public static OrderedTestSuite suite(final VoidFunction<DawgVerification> f, Predicate<QueryTest> filter) throws SQLException, IOException {
		OrderedTestSuite suite = new OrderedTestSuite();
		for(final DawgVerification v : DawgUtil.dawgTests(filter)) {
			suite.addTest(new TestCase(v.getName()) {
				@Override
				protected void runTest() throws Throwable {
					f.apply(v);
				}
			});
		}
		return suite;
	}

	public static OrderedTestSuite suite(VoidFunction<DawgVerification> f, final String path) throws SQLException, IOException {
		return suite(f, new Predicate<QueryTest>() {
			@Override
			public boolean test(QueryTest arg0) {
				return arg0.getQuery().contains(path);
			} 
		});
	}

	public static OrderedTestSuite testSuite(final String path) throws SQLException, IOException {
		return suite(new DawgTestCase(), path);
	}

	public static OrderedTestSuite repairSuite(final String path) throws SQLException, IOException {
		return suite(new DawgRepairCase(), path);
	}

	public static OrderedTestSuite fabricateSuite(final String path) throws SQLException, IOException {
		return suite(new DawgFabricateCase(), path);
	}

	public static class CommandLineTest extends DawgTest {

		public static OrderedTestSuite suite() throws SQLException, IOException {
			return testSuite(System.getProperty("dawg.test.path"));
		}
		
	}
	
	public static class NegationTests extends DawgTest {

		public static OrderedTestSuite suite() throws SQLException, IOException {
			return testSuite("/negation/");
		}
	}

	public static class AlgebraTests extends DawgTest {

		public static OrderedTestSuite suite() throws SQLException, IOException {
			return testSuite("/algebra/");
		}
	}
	
	public static class ExistsTests extends DawgTest {

		public static OrderedTestSuite suite() throws SQLException, IOException {
			return testSuite("/exists/");
		}
	}

	public static class PropertyPathTests extends DawgTest {

		public static OrderedTestSuite suite() throws SQLException, IOException {
			return testSuite("/property-path/");
		}
	}

	public static class DatasetTests extends DawgTest {

		public static OrderedTestSuite suite() throws SQLException, IOException {
			return testSuite("/dataset/");
		}
	}

	public static class DistinctTests extends DawgTest {

		public static OrderedTestSuite suite() throws SQLException, IOException {
			return testSuite("/distinct/");
		}
	}

	public static class BindTests extends DawgTest {

		public static OrderedTestSuite suite() throws SQLException, IOException {
			return testSuite("/bind/");
		}
	}

	public static class BindingsTests extends DawgTest {

		public static OrderedTestSuite suite() throws SQLException, IOException {
			return testSuite("/bindings/");
		}
	}

	public static class AggregateTests extends DawgTest {

		public static OrderedTestSuite suite() throws SQLException, IOException {
			return testSuite("/aggregates/");
		}
	}

	public static class TripleMatchTests extends DawgTest {

		public static OrderedTestSuite suite() throws SQLException, IOException {
			return testSuite("/triple-match/");
		}
	}

	public static class OptionalTests extends DawgTest {

		public static OrderedTestSuite suite() throws SQLException, IOException {
			return testSuite("/optional");
		}
	}

	public static class ExprBuiltinRepairTests extends DawgTest {

		public static OrderedTestSuite suite() throws SQLException, IOException {
			return repairSuite("/expr-builtin/q-datatype-2");
		}
	}

	public static class OptionalFilterFabricate extends DawgTest {

		public static OrderedTestSuite suite() throws SQLException, IOException {
			return fabricateSuite("/optional-filter/");
		}
	}

	public static class GraphTests extends DawgTest {

		public static OrderedTestSuite suite() throws SQLException, IOException {
			return testSuite("/graph/");
		}
	}

	public static class AskTests extends DawgTest {

		public static OrderedTestSuite suite() throws SQLException, IOException {
			return testSuite("/ask/");
		}
	}

	public static class OpenWorldTests extends DawgTest {

		public static OrderedTestSuite suite() throws SQLException, IOException {
			return testSuite("/open-world/");
		}
	}

	public static class ExprOpTests extends DawgTest {

		public static OrderedTestSuite suite() throws SQLException, IOException {
			return testSuite("/expr-ops/");
		}
		
	}

	public static class ExprEqualsTests extends DawgTest {

		public static OrderedTestSuite suite() throws SQLException, IOException {
			return testSuite("/expr-equals/");
		}
		
	}

	public static class ExprBuiltinTests extends DawgTest {

		public static OrderedTestSuite suite() throws SQLException, IOException {
			return testSuite("/expr-builtin/");
		}
		
	}

	public static class BEVTests extends DawgTest {

		public static OrderedTestSuite suite() throws SQLException, IOException {
			return testSuite("/boolean-effective-value/");
		}
		
	}

	public static class GroupTests extends DawgTest {

		public static OrderedTestSuite suite() throws SQLException, IOException {
			return testSuite("/grouping/");
		}
		
	}

	public static class AllTests extends DawgTest {

		public static OrderedTestSuite suite() throws SQLException, IOException {
			return suite(new DawgTestCase(), new Predicate<QueryTest>() {
				@Override
				public boolean test(QueryTest arg0) {
					return true;
				} 
			});
		}
	}
}
