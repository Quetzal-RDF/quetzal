/******************************************************************************
 * Copyright (c) 2015 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *****************************************************************************/
 package com.ibm.research.rdf.store.sparql11;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.BufferedTreeNodeStream;
import org.antlr.runtime.tree.CommonTree;

import com.ibm.research.rdf.store.sparql11.model.Expression;
import com.ibm.research.rdf.store.sparql11.model.Query;


public class Test {

	public static void runParser(String sparqlFile) throws IOException {
    	//System.out.println("Parsing: "+sparqlFile+"\n");
    	IbmSparqlLexer lex = new IbmSparqlLexer(new ANTLRFileStream(sparqlFile,	"UTF8"));
		CommonTokenStream tokens = new CommonTokenStream(lex);
		IbmSparqlParser parser = new IbmSparqlParser(tokens);
		try {
			IbmSparqlParser.queryUnit_return ret = parser.queryUnit();
			CommonTree ast = (CommonTree) ret.getTree();
			//System.out.println(ast.toStringTree());
			//SparqlParserUtilities.dump_tree(ast, tokens, 0);
			BufferedTreeNodeStream nodes = new BufferedTreeNodeStream(ast);
			nodes.setTokenStream(tokens);
			IbmSparqlAstWalker walker = new IbmSparqlAstWalker(nodes);
			Query query = walker.queryUnit();
			//System.out.println(query.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    public static void main1(String args[]) throws Exception {
    	for(String arg : args) {
    		File f = new File(arg);
    		if (f.exists()) {
    			if (f.isDirectory()) {
    				File[] queries = f.listFiles(new FileFilter() {
    					public boolean accept(File pathname) {
    						return pathname.getName().endsWith(".sparql");
    					}
    				});
    				for(File x : queries) {
    					runParser(x.getAbsolutePath());
    				}
    			} else {
    				runParser(arg);
    			}
    		}
    	}
	}
    
    public static void main2(String args[]) {
    	try {
    		File f = new File("C:\\Raj\\Workspace\\ARQTests\\testing_Full\\ARQ\\ExprDatatypes\\dt-query-3.rq");
    		//File f = new File("jazz_queries\\a1.sparql");
    		System.out.println("Parsing "+f.getAbsolutePath());
    		//SparqlParserUtilities.parseSparqlString("select * fro abc where {:x :y :z};");
    		Query q = SparqlParserUtilities.parseSparql(f, Collections.EMPTY_MAP);
    		System.out.println("==Parsed-Sparql==>>>>>>>>\n"+q.toString()+"\n<<<<<<<<<<<<<<<<\n");
    		List<Expression> a = new ArrayList<Expression>();
    		//a = q.getSelectQuery().getSolutionModifier().getGroupClause().getConditions();
    		for (Expression x: a) {
    			System.out.println(x+" : "+x.getReturnType()+" : "+x.getType());
    		}
    	}
    	catch (SPARQLsyntaxError se) {
    		se.printStackTrace();
    	}
    }
    
    public static void main(String args[])  {
    	testReturnType(args);
    }
    
    public static void testReturnType(String args[]) {
    	try {
    		File f = new File("individual_test_queries\\returnType.sparql");
    		System.out.println("Parsing "+f.getAbsolutePath());
    		Query q = SparqlParserUtilities.parseSparql(f, Collections.EMPTY_MAP);
    		System.out.println("==Parsed-Sparql==>>>>>>>>\n"+q.toString()+"\n<<<<<<<<<<<<<<<<\n");
    		List<Expression> a = new ArrayList<Expression>();
    		a = q.getSelectQuery().getSolutionModifier().getGroupClause().getConditions();
    		for (Expression x: a) {
    			System.out.println(x+" : "+x.getReturnType()+" : "+x.getType());
    		}
    	}
    	catch (SPARQLsyntaxError se) {
    		se.printStackTrace();
    	}
    }
}