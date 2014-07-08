package com.ibm.rdf.store.sparql11;

import java.sql.ResultSet;

import com.ibm.rdf.store.query.QueryProcessor;
import com.ibm.rdf.store.query.QueryProcessorFactory;
import com.ibm.rdf.store.runtime.service.types.LiteralInfoResultSet;
import com.ibm.rdf.store.sparql11.TestRunner.DB2TestData;
import com.ibm.rdf.store.sparql11.model.Pattern;
import com.ibm.rdf.store.sparql11.model.Query;

import static com.ibm.rdf.store.sparql11.TestRunner.DB2TestData.*;

public class JazzNewStore {
	public DB2TestData getJazzStore() {
		return getStore("jdbc:db2://9.47.202.45:50001/DAWG", "dawg",
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
