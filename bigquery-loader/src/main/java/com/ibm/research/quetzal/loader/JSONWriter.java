package com.ibm.research.quetzal.loader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JSONWriter  {
	static int counter = 0;
	
	static String schemaFile = "schema.properties";
	static String predToMultiValues = "predsToMultiValues.properties";
	
	private Map<String, Set<String>> predicateObjects = new HashMap<String, Set<String>>();
	private String lastSubject;
	private Writer out;
	private Properties schema;
	private Properties predicatesToMultiValues;
	private boolean givenSchema;

	public JSONWriter(Writer out, Properties schema, Properties predicatesToMultiValues, boolean givenSchema) {
		this.out = out;
		this.schema = schema;
		this.givenSchema = givenSchema;
		this.predicatesToMultiValues = predicatesToMultiValues;
	}
	
	public void endFile() throws Exception {
		endPredicateObjectList();
		out.close();
	}

	protected void addTriple(String currSubject, String property, String object) throws Exception {
		if (property.equals("<http://www.w3.org/2002/07/owl#imports>")) {
			return;
		}
		if (lastSubject != null) {
			if (!lastSubject.equals(currSubject)) {
				// Start new predicate object list
				endPredicateObjectList();
				startPredicateObjectList(currSubject);
			}
		} else {
			startPredicateObjectList(currSubject);
		}
		if (!predicateObjects.containsKey(property)) {
			predicateObjects.put(property, new HashSet<String>());
		}
		Set<String> values = predicateObjects.get(property);
		values.add(object);
	}
	
	protected String getPredicateToColumnMapping(String key, int size) {
		assert size >= 1;
		if (!schema.containsKey(key) && givenSchema) {
			throw new RuntimeException("Schema does not contain key:" + key);
		}
		if (!schema.containsKey(key)) {
			schema.setProperty(key, "col_" + counter);
			counter++;
		}
		
		String value = null; 
		if (size == 1) {
			value = "single";
		} else {
			value = "repeated";
		}
		
		if (!predicatesToMultiValues.containsKey(key) && givenSchema) {
			throw new RuntimeException("Predicates to multi values map does not contain key:" + key);
		}
		if (predicatesToMultiValues.containsKey(key) && givenSchema) {
			if (size > 1 && !predicatesToMultiValues.getProperty(key, "").equals(value)) {
				throw new RuntimeException("Predicates to multi values map has wrong values for key:" + key);
			}
		}
		if (!predicatesToMultiValues.containsKey(key)) {
			predicatesToMultiValues.setProperty(key, value);
		} else {
			if (size > 1) {		// clobber existing value if its a multi valued
				predicatesToMultiValues.setProperty(key, value);
			}
		}
		
		return schema.getProperty(key);
	}

	protected void endPredicateObjectList() throws Exception {
		JSONObject obj = new JSONObject();
		for (Map.Entry<String, Set<String>> entry : predicateObjects.entrySet()) {
			obj.put("subject", lastSubject);
			Set<String> l = entry.getValue();
			if (predicatesToMultiValues.containsKey(entry.getKey()) && predicatesToMultiValues.get(entry.getKey()).equals("repeated")) {
				JSONArray arr = new JSONArray();
				for (String str : l) {
					arr.add(str);
				}
				obj.put(getPredicateToColumnMapping(entry.getKey(), l.size()), arr);
			} else {
				obj.put(getPredicateToColumnMapping(entry.getKey(), l.size()),l.iterator().next());
			}
		}
		// System.out.print(obj);
		out.write(obj + "\n");
	}

	protected void startPredicateObjectList(String currSubject) {
		predicateObjects.clear();
		lastSubject = currSubject;
	}
	
	public static void main(String[] args) throws Exception {
		GZIPOutputStream zip = new GZIPOutputStream(
	            new FileOutputStream(new File(args[1])));
		
		Properties schema = new Properties();
		Properties predToMultiValues = new Properties();
		boolean givenSchema = false;
		if (args.length == 4) {
			givenSchema = true;
	        readProperties(schema, args[2]);
	        readProperties(predToMultiValues, args[3]);
			// System.out.println(schema);
		}
		
	    BufferedWriter writer = new BufferedWriter(
	            new OutputStreamWriter(zip, "UTF-8"));	
		JSONWriter json = new JSONWriter(writer, schema, predToMultiValues, givenSchema);
		InputStream gzipStream = new GZIPInputStream(new FileInputStream(args[0]));
		Reader decoder = new InputStreamReader(gzipStream);
		BufferedReader reader = new BufferedReader(decoder);
		
		String line = null;
		while ((line = reader.readLine()) != null) {
			int index = line.indexOf(' ');
			String subject = line.substring(0, index).trim();
			int nindex = line.indexOf(' ', index + 1);
			String predicate = line.substring(index, nindex).trim();
			int pindex = line.lastIndexOf('.');
			String object = line.substring(nindex + 1, pindex).trim();
			json.addTriple(subject, predicate, object);
		}
		json.endFile();
		if (args.length < 3) {
	        OutputStream out = new FileOutputStream( new File(schemaFile) );
	        schema.store(out,"Predicate to column mappings");
	        out.close();
	        out = new FileOutputStream( new File(JSONWriter.predToMultiValues) );
	        predToMultiValues.store(out,"Predicate to column multivalues");
	        out.close();
		}
		reader.close();
	}

	public static void readProperties(Properties schema, String str) throws FileNotFoundException, IOException {
		FileInputStream stream = new FileInputStream(new File(str));
		schema.load(stream);
	}

}
