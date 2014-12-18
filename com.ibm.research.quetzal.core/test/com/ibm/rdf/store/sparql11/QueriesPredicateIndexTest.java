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

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

import com.ibm.research.rdf.store.hashing.ColoringFunction;
import com.ibm.research.rdf.store.hashing.FindPredicateProxies;
import com.ibm.research.rdf.store.sparql11.SparqlParserUtilities;
import com.ibm.research.rdf.store.sparql11.model.Query;
import com.ibm.wala.util.collections.Pair;
import com.ibm.wala.util.graph.labeled.LabeledGraph;
import com.ibm.wala.util.intset.IntSetUtil;
import com.ibm.wala.util.intset.MutableIntSet;

public class QueriesPredicateIndexTest {

	@Test
	public void testPredicatesToIndex() throws Exception {		
		Set<String> predicates = new HashSet<String>();
		Collection<File[]> queries = ParseSparqlTest.data();
		for(File[] files : queries) {
			for(File query : files) {
				System.err.println("parsing " + query);
				Query q = SparqlParserUtilities.parseSparqlFile(query.getAbsolutePath(), Collections.<String,String>emptyMap());
				Assert.assertNotNull(q);
				predicates.addAll(SparqlParserUtilities.gatherQueryPredicates(q));
			}
		}
		
		System.err.println("all predicates: " + predicates);
		
		Set<String> usedPredicates = new HashSet<String>();
		File graphFile = new File(System.getProperty("proxies.graph.file"));
		LabeledGraph<String,Double> proxyGraph = FindPredicateProxies.readGraph(graphFile);
		for(String predicate : predicates) {
			String nt = "<" + predicate + ">";
			if (proxyGraph.containsNode(nt)) {
				usedPredicates.add(nt);
			}
		}
		
		FindPredicateProxies x = new FindPredicateProxies(proxyGraph);
		x.solve();
		
		Set<String> index = new HashSet<String>();
		for(String nt : usedPredicates) {
			index.add(x.getProxy(nt) != null? x.getProxy(nt).getProxyNode(): nt);
			System.err.println(nt + ": " + x.getProxy(nt));
		}
		
		System.err.println(index.size() + " for " + usedPredicates.size());
		System.err.println(index);
		
		// Assert.assertEquals(Integer.parseInt(System.getProperty("indexes.count")), index.size());
	
		Pair<Map<String, Integer>, Map<String, Integer>> colors = ColoringFunction.color(System.getProperty("edge.set.file"), index);
		
		MutableIntSet columns = IntSetUtil.make();
		for(String pred : index) {
			Assert.assertTrue(colors.fst.containsKey(pred));
			columns.add(colors.fst.get(pred));
		}
		
		System.err.println("using " + columns.size() + " colors : " + columns);
		
		Assert.assertEquals(Integer.parseInt(System.getProperty("columns.count")), columns.size());
	}
}
