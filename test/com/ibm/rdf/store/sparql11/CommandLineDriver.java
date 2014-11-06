package com.ibm.rdf.store.sparql11;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.ibm.rdf.store.sparql11.TestRunner.DB2Engine;
import com.ibm.rdf.store.sparql11.TestRunner.DB2TestData;
import com.ibm.rdf.store.sparql11.TestRunner.DatabaseEngine;

public class CommandLineDriver  {

	protected static com.ibm.rdf.store.sparql11.TestRunner.DB2TestData getData(String dataset) {
		return DB2TestData.getStore(
			"jdbc:db2://" + System.getenv("DB2_HOST") + ":" + System.getenv("DB2_PORT") + "/" + System.getenv("DB2_DB"), 
			dataset, 
			System.getenv("DB2_USER"),
			System.getenv("DB2_PASSWORD"),
			System.getenv("DB2_SCHEMA"),
			false);
	}

	protected static int[] getAnswers(String answers) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field f = TestConstants.class.getField(answers);
		return (int[])f.get(null);
	}
	
	protected static void run(Object x) {
		for(Method m : x.getClass().getDeclaredMethods()) {
			if (m.getAnnotation(org.junit.Test.class) != null) {
				try {
					m.invoke(x);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InstantiationException, InvocationTargetException {
		Class<TestRunner<DB2TestData>> cls = (Class<TestRunner<DB2TestData>>) Class.forName(args[0]);
		Constructor<TestRunner<DB2TestData>> ctor = cls.getDeclaredConstructor(DatabaseEngine.class, Object.class, int[].class, String.class);
		run(ctor.newInstance(new DB2Engine(), getData(args[1]), getAnswers(args[2]), args[3]));
	}
}

