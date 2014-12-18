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

import static com.ibm.rdf.store.sparql11.TestRunner.DB2TestData.getStore;

import java.util.Collections;

import org.junit.Test;

import com.ibm.rdf.store.sparql11.TestRunner.DB2TestData;
import com.ibm.research.rdf.store.sparql11.SparqlParserUtilities;
import com.ibm.research.rdf.store.sparql11.model.Query;
import com.ibm.research.rdf.store.utilities.MinusRewriter.FilterAsMinusRewriter;
import com.ibm.research.rdf.store.utilities.MinusRewriter.OptionalAsFilterRewriter;

public abstract class UniprotQ9RewriteTest<D extends DB2TestData> extends TestRunner<D> {
	private final String queryDir;
	
	protected UniprotQ9RewriteTest(DatabaseEngine<D> engine, String queryDir, D data) {
		super(data, engine, null);
		this.queryDir = queryDir;
	}

	public static class XIVC6 extends UniprotQ9RewriteTest<DB2TestData> {
		private static final DB2TestData data = getStore("jdbc:db2://localhost:50000/xivdb_c6", "uniprot", "db2inst1", "ihaterc2", "db2inst1", false);

		public XIVC6() {
			super(new DB2Engine(), "../rdfstore-data/uniprot_queries_rev/", data);
		}
	}

	public static class TMSC6 extends UniprotQ9RewriteTest<DB2TestData> {
		private static final DB2TestData data = getStore("jdbc:db2://localhost:50000/tmsdb_c6", "uniprot", "db2inst1", "ihaterc2", "db2inst1", false);

		public TMSC6() {
			super(new DB2Engine(), "../rdfstore-data/uniprot_queries_rev/", data);
		}
	}

	private Query testRewriter(String file) {
		Query q = SparqlParserUtilities.parseSparqlFile(file, Collections.<String,String>emptyMap());
		(new OptionalAsFilterRewriter(q)).rewrite();
		(new FilterAsMinusRewriter(q)).rewrite();
		return q;
	}	

	@Test
	public void testQueryQ9() throws Exception {
		String file = queryDir + "query_9.sparql";
		Query q = testRewriter(file);
		System.out.println(q);
	}

}
