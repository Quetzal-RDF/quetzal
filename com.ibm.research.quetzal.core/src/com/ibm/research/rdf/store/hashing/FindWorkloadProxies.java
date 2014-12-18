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
 package com.ibm.research.rdf.store.hashing;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.ibm.research.rdf.store.sparql11.SparqlParserUtilities;
import com.ibm.research.rdf.store.sparql11.model.Query;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.collections.HashMapFactory;
import com.ibm.wala.util.collections.HashSetFactory;
import com.ibm.wala.util.graph.labeled.LabeledGraph;

public class FindWorkloadProxies {

	public static Set<String> getPredicates(File[] queryFiles) {
		Set<String> predicates = HashSetFactory.make();
		for(File query : queryFiles) {
			Query q = SparqlParserUtilities.parseSparqlFile(query.getAbsolutePath(), Collections.<String,String>emptyMap());
			assert q != null;
			predicates.addAll(SparqlParserUtilities.gatherQueryPredicates(q));
		}
		return predicates;
	}

	public static Set<String> getPredicates(File queryDir) {
		assert queryDir.isDirectory();
		return getPredicates(queryDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith("sparql") || name.endsWith("rq");
			}			
		}));
	}
	
	private static FindPredicateProxies getProxies(File graphFile) throws CancelException, IOException { 
		LabeledGraph<String,Double> proxyGraph = FindPredicateProxies.readGraph(graphFile);	
		FindPredicateProxies x = new FindPredicateProxies(proxyGraph);	
		x.solve();
		return x;
	}
	
	public static Map<String,String> getWorkloadProxies(File queryDir, File correlationGraphFile) throws CancelException, IOException {
		Set<String> predicates = getPredicates(queryDir);
		FindPredicateProxies proxies = getProxies(correlationGraphFile);
		
		Map<String,String> result = HashMapFactory.make();
		for(String predicate : predicates) {
			String proxy = "<" + predicate + ">"; 
			while (proxies.getProxy(proxy) != null && !proxies.getProxy(proxy).getProxyNode().equals(proxy)) {
				proxy = proxies.getProxy(proxy).getProxyNode();
			}
			result.put(predicate, proxy);
		}
		
		return result;
	}
	
	public static void main(String[] args) throws CancelException, IOException {
		Map<String,String> x = getWorkloadProxies(new File(args[0]), new File(args[1]));
		for(Map.Entry<String, String> p : x.entrySet()) {
			System.out.println(p.getKey() + " " + p.getValue());
		}
	}
}
