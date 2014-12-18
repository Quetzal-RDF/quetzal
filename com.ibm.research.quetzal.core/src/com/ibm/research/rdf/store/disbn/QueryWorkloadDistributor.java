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
 package com.ibm.research.rdf.store.disbn;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.ibm.research.rdf.store.sparql11.SparqlParserUtilities;
import com.ibm.research.rdf.store.sparql11.model.BuiltinFunctionExpression;
import com.ibm.research.rdf.store.sparql11.model.Expression;
import com.ibm.research.rdf.store.sparql11.model.Pattern;
import com.ibm.research.rdf.store.sparql11.model.Pattern.EPatternSetType;
import com.ibm.research.rdf.store.sparql11.model.PatternSet;
import com.ibm.research.rdf.store.sparql11.model.Query;
import com.ibm.research.rdf.store.sparql11.model.QueryTriple;
import com.ibm.research.rdf.store.sparql11.model.SimplePattern;
import com.ibm.wala.util.collections.HashMapFactory;
import com.ibm.wala.util.collections.Pair;
import com.ibm.wala.util.graph.impl.SlowSparseNumberedGraph;
import com.ibm.wala.util.graph.labeled.SlowSparseNumberedLabeledGraph;
import com.ibm.wala.util.graph.traverse.FloydWarshall;

public class QueryWorkloadDistributor {
	
	private HashMap<String, Set<Pair<String, String>>> labelsToEdges = HashMapFactory.make();
	private SlowSparseNumberedGraph<String> joinGraph = SlowSparseNumberedGraph.make();
	

	public static void main(String[] args) {
		File folder = new File(args[0]);
		File[] listOfFiles = folder.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				// TODO Auto-generated method stub
				return true;
			}
		});

		QueryWorkloadDistributor d = new QueryWorkloadDistributor();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				System.out.println("Parsing file:" + listOfFiles[i]);
				Query q = SparqlParserUtilities.parseSparql(listOfFiles[i],
						new HashMap());
				List<List<QueryTriple>> all = d.getAllJoinedTriples(q
						.getMainPattern());
				System.out.println(listOfFiles[i] + " has: " + all.size());
				for (List<QueryTriple> ql : all) {
					for (QueryTriple qt : ql) {
						System.out.println(qt);
					}
					System.out.println("---------------------");
				}
				d.buildGraph(all);
				
				System.out.println("***********************");
			}
		}
		
		System.out.println("Join graph");
		System.out.println(d.joinGraph);
	}
	
	private void buildGraph(List<List<QueryTriple>> queryTriples) {
		SlowSparseNumberedLabeledGraph<String, String> graph = new SlowSparseNumberedLabeledGraph<String, String>("");
		int max = 0;
		
		for (List<QueryTriple> qtl : queryTriples) {

			for (QueryTriple qt : qtl) {
				String s = qt.getSubject().toString();
				String p = qt.getPredicate().toString();
				String o = qt.getObject().toString();
				
				if (qt.getSubject().isVariable() && qt.getObject().isVariable()) {
					if (labelsToEdges.containsKey(p)) {
						labelsToEdges.put(p, new HashSet<Pair<String, String>>());
					}
				}
				if (!graph.containsNode(s)) {
					graph.addNode(s);
				}
				if (!graph.containsNode(o)) {
					graph.addNode(o);
				}
				
				if (!graph.hasEdge(s, o, p)) {
					graph.addEdge(s, o, p);				
				}
			}
			int[][] pathlens = FloydWarshall.shortestPathLengths(graph);

			for (int i = 0; i < pathlens.length; i++) {
				for (int j = 0; j < pathlens[i].length; j++) {
					if (pathlens[i][j] > max && pathlens[i][j] != Integer.MAX_VALUE) {
						max = pathlens[i][j];
					}
				}
			}
		}
		
		Iterator<String> it = graph.iterator();
		while (it.hasNext()) {
			String n = it.next();
			Iterator<String> preds = (Iterator<String>) graph.getPredLabels(n);
			Iterator<String> succs = (Iterator<String>) graph.getSuccLabels(n);
			// This object->subject joins
			List<String> labels = new LinkedList<String>();

			while (preds.hasNext()) {
				String pred = preds.next();
				labels.add(pred);
				while (succs.hasNext()) {
					String succ = succs.next();
					addEdgeToJoin(pred, succ);
				}
			}
			
			// This captures object->object joins
			for (int i = 0 ; i < labels.size(); i++) {
				for (int j = i + 1; j < labels.size(); j++) {
					addEdgeToJoin(labels.get(i), labels.get(j));
				}
			}
		}
		
		System.out.println("Max diameter:" + max);

		
	}

	private void addEdgeToJoin(String pred, String succ) {
		if (!joinGraph.containsNode(pred)) {
			joinGraph.addNode(pred);
		}
		if (!joinGraph.containsNode(succ)) {
			joinGraph.addNode(succ);
		}
		joinGraph.addEdge(pred, succ);
	}

	private List<List<QueryTriple>> getAllJoinedTriples(Pattern p) {
		List<List<QueryTriple>> l = new LinkedList<List<QueryTriple>>();
		List<QueryTriple> qt = new LinkedList<QueryTriple>();
		l.add(qt);
		getAllJoinedTriples(p, qt, l);
		return l;
	}

	private void getAllJoinedTriples(Pattern p, List<QueryTriple> currentList,
			List<List<QueryTriple>> all) {
		switch (p.getType()) {
		case SIMPLE:
			currentList.addAll(((SimplePattern) p).getQueryTriples());
			break;
		case MINUS:
		case UNION:
			for (Pattern ps : ((PatternSet) p).getPatterns()) {
				LinkedList<QueryTriple> l = new LinkedList<QueryTriple>();
				all.add(l);
				l.addAll(currentList);
				getAllJoinedTriples(ps, l, all);
			}
			break;
		case AND:
			List<Pattern> ps = new LinkedList<Pattern>();
			ps.addAll( ((PatternSet) p).getPatterns() );
			Collections.sort(ps, new Comparator<Pattern>() {
				public int compare(Pattern o1, Pattern o2) {
					if (o1.equals(o2)) {
						return 0;
					}
					if (isJoinedPattern(o1) && !isJoinedPattern(o2)) {
						return -1;
					} else if (isJoinedPattern(o2) && !isJoinedPattern(o1)) {
						return 1;
					}
					return 1;
				}
			});
			for (Pattern psa : ps) {
				getAllJoinedTriples(psa, currentList, all);
			}
		}
		List<Expression> exps = p.getFilters();
		for (Expression e : exps) {
			if (e instanceof BuiltinFunctionExpression) {
				if (((BuiltinFunctionExpression) e).isExists()) {
					Pattern ePat = ((BuiltinFunctionExpression) e).getPatternArguments();
					getAllJoinedTriples(ePat, currentList, all);
				}
			}
		}

	}
	
	private boolean isJoinedPattern(Pattern p) {
		return p.getType() == EPatternSetType.AND || p.getType() == EPatternSetType.SIMPLE;
	}

}
