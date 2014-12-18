/*******************************************************************************
* Licensed Materials - Property of IBM
* (c) Copyright IBM Corporation 2011, 2013. All Rights Reserved.
*
* Note to U.S. Government Users Restricted Rights:
* Use, duplication or disclosure restricted by GSA ADP Schedule
* Contract with IBM Corp.
*******************************************************************************/
/*******************************************************************************
 * Licensed Materials - Property of IBM
 * (c) Copyright IBM Corporation 2008. All Rights Reserved.
 * 
 * Note to U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp. 
 *******************************************************************************/


package com.ibm.research.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;



/**
 * Utitlies methods to read a file or an inputstream consisting of semicolon separated SPARQL queries.
 * @author achille
 *
 */
public class SPARQLFileParser {

	/**
	 * reads an input stream consisting of semicolon separated SPARQL queries, and returns the list of SPARQL 
	 * queries as strings.
	 * @param ins
	 * @return
	 * @throws IOException
	 */
	public static String[] readQueries(Reader ins) throws IOException {
	
		BufferedReader in = new BufferedReader(ins);
		StringBuffer buf = new StringBuffer();
		String line = null;
		while ((line=in.readLine())!=null) {
			if (!line.startsWith("#")) {
				buf.append(line+"\n");
			}
		}
		StringTokenizer t = new StringTokenizer(buf.toString(),";");
		List<String> ret =  new ArrayList<String>();
		while (t.hasMoreTokens()) {
			String query = t.nextToken().trim();
			if (!"".equals(query)) {
				ret.add(query);
			}
		}
		return ret.toArray(new String[ret.size()]);
		
	}
}
