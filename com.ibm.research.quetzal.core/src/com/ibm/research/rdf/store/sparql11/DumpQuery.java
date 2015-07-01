package com.ibm.research.rdf.store.sparql11;

import java.io.IOException;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.BufferedTreeNodeStream;
import org.antlr.runtime.tree.CommonTree;
import org.json.JSONException;

public class DumpQuery {

	public static void main(String[] args) throws RecognitionException, IOException, JSONException {
		CommonTree ast = SparqlParserUtilities.getParseTree(new ANTLRFileStream(args[0], "UTF8"));
		JSONWriter writer = new JSONWriter(new BufferedTreeNodeStream(ast));
		System.err.println(writer.queryUnit());
	}

}
