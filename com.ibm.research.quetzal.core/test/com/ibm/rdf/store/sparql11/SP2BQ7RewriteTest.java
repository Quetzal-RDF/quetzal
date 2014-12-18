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

import java.util.Collections;

import org.junit.Test;

import com.ibm.rdf.store.sparql11.TestRunner.DB2TestData;
import com.ibm.research.rdf.store.sparql11.SparqlParserUtilities;
import com.ibm.research.rdf.store.sparql11.model.Query;
import com.ibm.research.rdf.store.utilities.MinusRewriter;

import static com.ibm.rdf.store.sparql11.TestRunner.DB2TestData.*;

public class SP2BQ7RewriteTest extends TestRunner<DB2TestData> {
	private final String queryDir;
	
	public static class SP2B100M_R_RC2 extends SP2BQ7RewriteTest {
		private static final DB2TestData data = getStore("jdbc:db2://9.47.202.45:50001/sp2b",
				"sp2b100m", "db2inst2","db2admin","db2inst2",false);
		
		public SP2B100M_R_RC2() {
			super(data, "../rdfstore-data/rewriter-test-queries/");
		}
	}

	public SP2BQ7RewriteTest(DB2TestData data, String queryDir) {
		super(data, new DB2Engine(), null);
		this.queryDir = queryDir;
	}

	private Query testRewriter(String file) {
		Query q = SparqlParserUtilities.parseSparqlFile(file, Collections.<String,String>emptyMap());
		while (new MinusRewriter.OptionalAsFilterRewriter(q).rewrite()) ;
		while (new MinusRewriter.FilterAsMinusRewriter(q).rewrite()) ;
		return q;
	}	

	@Test
	public void testQueryQ7() throws Exception {
		String file = queryDir + "q7.sparql";
		Query q = testRewriter(file);
		System.out.println(q);
	}

	@Test
	public void testQueryQ7Orig() throws Exception {
		String file = queryDir + "q7Orig.sparql";
		Query q = testRewriter(file);
		System.out.println(q);
	}

	@Test
	public void testQueryQ7Reverse() throws Exception {
		String file = queryDir + "q7Negation.sparql";
		Query q = testRewriter(file);
		System.out.println(q);
	}

	@Test
	public void testQueryQ7WithUse() throws Exception {
		String file = queryDir + "q7WithUse.sparql";
		Query q = testRewriter(file);
		System.out.println(q);
	}	
}
