package com.ibm.research.quetzal.loader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.Enumeration;
import java.util.Properties;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


public class JSONSchemaWriter {
	
	public static void main(String[] args) throws Exception {
		String schemaFile = args[0];
		String predicatesToMultiValues = args[1];
		
		// have a schema already, re-use it
		File out = new File(args[2]);
		Properties schema = new Properties();
		Properties preds = new Properties();
        JSONWriter.readProperties(schema, schemaFile);
        JSONWriter.readProperties(preds, predicatesToMultiValues);
        Enumeration<String> e = (Enumeration<String>) schema.propertyNames();
		JSONArray fields = new JSONArray();
        JSONObject f = new JSONObject();
        f.put("name", "subject");
        f.put("type", "string");
        fields.add(f);
        while (e.hasMoreElements()) {
        	String key = e.nextElement();
            String value = schema.getProperty(key);
            f = new JSONObject();
            f.put("name", value);
            f.put("type", "string");
            if (preds.containsKey(key) && preds.getProperty(key).equals("repeated")) {
            	f.put("mode", "repeated");
            }
            fields.add(f);
        }
		
        JSONObject obj = new JSONObject();
        obj.put("fields", fields);
        JSONObject sch = new JSONObject();
        sch.put("schema", obj);
        
        BufferedWriter buf = new BufferedWriter(new FileWriter(out));
        buf.write(sch + "\n");
		buf.close();
	}

}
