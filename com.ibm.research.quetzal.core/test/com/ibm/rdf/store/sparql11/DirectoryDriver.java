package com.ibm.rdf.store.sparql11;

import static com.ibm.rdf.store.sparql11.CommandLineDriver.getData;
import static com.ibm.rdf.store.sparql11.CommandLineDriver.getEngine;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.InvocationTargetException;

import junit.framework.TestCase;
import junitx.framework.OrderedTestSuite;

import com.ibm.rdf.store.sparql11.TestRunner.DatabaseEngine;
import com.ibm.rdf.store.sparql11.TestRunner.TestData;
import com.ibm.research.rdf.store.sparql11.SparqlParserUtilities;
import com.ibm.wala.util.Predicate;
import com.ibm.wala.util.functions.VoidFunction;

public class DirectoryDriver extends TestCase {

	public static void recurseFiles(VoidFunction<File> action, final Predicate<File> filter, File top) {
		  	if (top.isDirectory()) {
		  		for(File f : top.listFiles(new FileFilter() {
		  			@Override
		  			public boolean accept(File file) {
		  				return filter.test(file) || file.isDirectory();
		  			}	
		  		})) {
		  			recurseFiles(action, filter, f);
		  		}
		  	} else {
		  		action.apply(top);
		  	}
		  }

	public static OrderedTestSuite suite() throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		CommandLineDriver.checkEnvironment();
		CommandLineDriver.checkExtensions();
		final TestData data = getData();
		@SuppressWarnings("rawtypes")
		final DatabaseEngine engine = getEngine(data, false);
		final OrderedTestSuite x = new OrderedTestSuite();
		recurseFiles(new VoidFunction<File>() {
			@Override
			public void apply(final File arg0) {
				x.addTest(new TestCase(arg0.getName()) {
					@Override
					@SuppressWarnings("unchecked")
					public void runTest() {
						engine.executeQuery(data, arg0.getAbsolutePath());
					} 
				});
			} }, new Predicate<File>() {
				@Override
				public boolean test(File arg0) {
					return arg0.getName().endsWith(".sparql") || arg0.getName().endsWith(".rq");
				} }, new File(System.getenv("TEST_DIR")));
		return x;
	}

}
