package com.ibm.research.rdf.store.sparql11;

import java.io.File;
import java.io.IOException;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.BufferedTreeNodeStream;
import org.antlr.runtime.tree.CommonTree;
import org.json.JSONException;

public class DumpQuery {

	public static void main(String[] args) throws RecognitionException, IOException, JSONException {
		ANTLRStringStream sparql;
		if (new File(args[0]).exists()) {
			sparql = new ANTLRFileStream(args[0], "UTF8");
		} else {
			sparql = new ANTLRStringStream(args[0]);			
		}
		
		CommonTree ast = SparqlParserUtilities.getParseTree(sparql);
		JSONWriter writer = new JSONWriter(new BufferedTreeNodeStream(ast));
		System.err.println(writer.queryUnit());
	}

}
