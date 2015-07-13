package com.ibm.research.rdf.store.sparql11;

import java.io.File;
import java.io.IOException;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.BufferedTreeNodeStream;
import org.antlr.runtime.tree.CommonTree;
import org.json.JSONException;
import org.json.JSONObject;

public class DumpQuery {

	public static void main(String[] args) throws RecognitionException, IOException, JSONException {
		System.err.println(queryToJSON(args[0]));
	}

	public static JSONObject queryToJSON(String query) throws IOException,
			RecognitionException, JSONException {
		ANTLRStringStream sparql;
		if (new File(query).exists()) {
			sparql = new ANTLRFileStream(query, "UTF8");
		} else {
			System.err.println(query);
			sparql = new ANTLRStringStream(query);			
		}
		
		XTree ast = SparqlParserUtilities.getParseTree(sparql);
		JSONWriter writer = new JSONWriter(new BufferedTreeNodeStream(ast));
		JSONObject jsonOutput = writer.queryUnit();
		return jsonOutput;
	}

}
