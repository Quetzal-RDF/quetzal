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
 package com.ibm.research.rdf.store.sparql;


import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import antlr.RecognitionException;
import antlr.TokenStreamException;

import com.ibm.iodt.sor.query.AndPattern;
import com.ibm.iodt.sor.query.GraphGraphPattern;
import com.ibm.iodt.sor.query.Pattern;
import com.ibm.iodt.sor.query.Query;
import com.ibm.iodt.sor.query.SPARQLLexer;
import com.ibm.iodt.sor.query.SPARQLParser;
import com.ibm.iodt.sor.query.TriplePattern;
import com.ibm.iodt.sor.query.VarOrTerm;
import com.ibm.iodt.sor.query.Variable;
import com.ibm.research.rdf.store.config.Constants;
import com.ibm.research.rdf.store.config.RDFStoreParameters;
import com.ibm.research.rdf.store.hashing.HashingException;
import com.ibm.research.rdf.store.hashing.HashingHelper;

public class Parser {
	
	private static RDFStoreParameters rdfParams;
	
	public static String toSQLQuery(String sparql)
	{
		SPARQLLexer lexer = new SPARQLLexer(new StringReader(sparql));
		SPARQLParser p = new SPARQLParser(lexer);
		Query q=null;
		try {
			q = p.query();
		} catch (RecognitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TokenStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return toSQL(q);
	}
	
	private static String toSQL(Query q)
	{
		StringBuffer queryStringBuffer=new StringBuffer();
		
		if(q.getType()==Query.SELECT) {
			List<Variable> projectionList=q.getResultList();
			int projectionListSize=projectionList.size();
			//use a null graph Term
			q.getPattern().setPatternVariables(null,Constants.MAXIMUM_COMPLEX_UDF);
			try {
				NormalizePattern(q.getPattern());
			} catch (HashingException | UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(1);
			}
			String patternToSQL=q.getPattern().toSQLQuery(rdfParams.hashingFunction, null,Constants.DEFAULT_GRAPH_MONIKER);
			queryStringBuffer.append("SELECT ");
			for(int i=0;i<projectionListSize;i++){
				Variable projection=projectionList.get(i);				
				queryStringBuffer.append(mapProjectionToSQLAlias(projection,q.getPattern())+" AS" + projection.toString().replace('?',' '));
				if(i<projectionListSize-1){
					queryStringBuffer.append(",");
				}
			}
			queryStringBuffer.append(" FROM ");
			queryStringBuffer.append(patternToSQL);

		}
		return queryStringBuffer.toString();
	}
	
	
	private static String mapProjectionToSQLAlias(VarOrTerm proj,Pattern p){
		// if mapProjectionToSQLAlias returns null the term is inside an optional 
		String projStr=proj.toString(); 
		if(p.getPatternVariables().containsKey(projStr))
			return p.getPatternVariables().get(projStr);
		if(p.getOptPatternVariables().containsKey(projStr))
			return p.getPatternVariables().get(projStr);
		return null;
	}
	
	private static void NormalizePattern(Pattern p) throws HashingException, UnsupportedEncodingException{
		if(p instanceof GraphGraphPattern){
			String normGraphID;
			VarOrTerm graphID=((GraphGraphPattern) p).getTerm();
			if(graphID instanceof Variable){
				normGraphID=graphID.toString();
			}
			else{
				normGraphID=replacePrefix(graphID.toString());
				if(normGraphID.length()>rdfParams.sizeMaxString) 
					normGraphID=HashingHelper.hashLongString(normGraphID);
			}
			((GraphGraphPattern) p).setNormTerm(normGraphID);
			NormalizePattern(((GraphGraphPattern)p).getPattern());
		}
		if(p instanceof AndPattern){
			List<Pattern> patternList=((AndPattern) p).getPatternList();
			int patternListSize=patternList.size();			
			for(int i=0;i<patternListSize;i++){
				NormalizePattern(patternList.get(i));
			}			
		}
		if(p instanceof TriplePattern){
			VarOrTerm tripleSubject=((TriplePattern) p).getSubject();
			VarOrTerm triplePredicate=((TriplePattern) p).getPredicate();
			VarOrTerm tripleObject=((TriplePattern) p).getObject();
			String normSubj, normPred, normObj;
			if(tripleSubject instanceof Variable){
				normSubj=tripleSubject.toString();
			}
			else{
				normSubj=replacePrefix(tripleSubject.toString());
				if(normSubj.length()>rdfParams.sizeMaxString) 
					normSubj=HashingHelper.hashLongString(normSubj);
			}
			if(triplePredicate instanceof Variable){
				normPred=triplePredicate.toString();
			}
			else{
				normPred=replacePrefix(triplePredicate.toString());
				if(normPred.length()>rdfParams.sizeMaxString) 
					normPred=HashingHelper.hashLongString(normPred);
			}
			if(tripleObject instanceof Variable){
				normObj=tripleObject.toString();
			}
			else{
				normObj=replacePrefix(tripleObject.toString());
				if(normObj.length()>rdfParams.sizeMaxString) 
					normObj=HashingHelper.hashLongString(normObj);
			}
			((TriplePattern) p).setNormTerms(normSubj,normPred,normObj);
		}
	}
	
	public static void ConfigureParser(RDFStoreParameters params){
		rdfParams=params;
	}
	
	private static String replacePrefix(String s)
	{
		String result=s;
		for(String prefix: rdfParams.namespaces.keySet()) {
			if(s.startsWith(prefix)){
				result=rdfParams.namespaces.get(prefix)+":"+s.substring(prefix.length());				
				break;
			}			
		}
		return result;
	}
}