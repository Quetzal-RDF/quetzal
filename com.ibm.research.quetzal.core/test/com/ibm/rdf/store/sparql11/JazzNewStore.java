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
 package com.ibm.rdf.store.sparql11;


import java.sql.ResultSet;

import com.ibm.rdf.store.sparql11.TestRunner.DB2TestData;
import com.ibm.research.rdf.store.query.QueryProcessor;
import com.ibm.research.rdf.store.query.QueryProcessorFactory;
import com.ibm.research.rdf.store.runtime.service.types.LiteralInfoResultSet;
import com.ibm.research.rdf.store.sparql11.SparqlParserUtilities;
import com.ibm.research.rdf.store.sparql11.model.Pattern;
import com.ibm.research.rdf.store.sparql11.model.Query;

public class JazzNewStore {
	public DB2TestData getJazzStore() {
		return new DB2TestData("jdbc:db2://9.47.202.45:50001/DAWG", "dawg",
				"db2inst2", "db2admin", "db2inst2", false);
	}

	public int runQuery(String sparql) throws Exception {
		DB2TestData data = getJazzStore();
		String newQuery = reverseQuery(sparql);
		QueryProcessor qp = QueryProcessorFactory.create(newQuery, data.conn,
				data.store, data.ctx);
		LiteralInfoResultSet lrs = qp.execSelect();
		ResultSet rs = lrs.getResultSet();
		int i = 0;
		while (rs.next()) {
			i++;
		}
		
		return i;  
	}
	
	public String reverseQuery(String sparql) {
		Query q = SparqlParserUtilities.parseSparqlString(sparql);		
		Pattern p = q.getMainPattern();
		p.reverseIRIs();
		return q.toString();
	}

}
