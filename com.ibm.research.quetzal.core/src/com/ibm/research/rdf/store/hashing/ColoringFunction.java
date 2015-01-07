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

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

//import com.ibm.wala.core.tests.basic.WelshPowellTest;
import com.ibm.wala.util.collections.HashMapFactory;
import com.ibm.wala.util.collections.Pair;
import com.ibm.wala.util.graph.INodeWithNumberedEdges;
import com.ibm.wala.util.graph.NumberedGraph;
import com.ibm.wala.util.graph.impl.DelegatingNumberedGraph;
import com.ibm.wala.util.graph.traverse.WelshPowell;
import com.ibm.wala.util.graph.traverse.WelshPowell.ColoredVertices;
import com.ibm.wala.util.intset.IntSet;
import com.ibm.wala.util.intset.IntSetAction;
import com.ibm.wala.util.intset.IntSetUtil;
import com.ibm.wala.util.intset.MutableIntSet;
import com.ibm.wala.util.intset.SemiSparseMutableIntSetFactory;

public class ColoringFunction {

    static {
        IntSetUtil.setDefaultIntSetFactory(new SemiSparseMutableIntSetFactory());
    }

    final class ColorNode implements INodeWithNumberedEdges {
		private final String predicate;
		private int nodeNumber = -1;
		private MutableIntSet succs = null;
		private MutableIntSet preds = null;
		private int popularity = 0;
	    
		public ColorNode(String predicate) {
			this.predicate = predicate;
		}

		public int hashCode() {
			return predicate.hashCode();
		}

		public boolean equals(Object other) {
			if (other instanceof ColorNode) {
				return ((ColorNode) other).predicate.equals(predicate);
			} else {
				return false;
			}
		}
		
		public void incrementPopularity(int pop) {
			popularity += pop;
		}

		@Override
		public int getGraphNodeId() {
			return nodeNumber;
		}

		@Override
		public void setGraphNodeId(int arg0) {
			nodeNumber = arg0;
		}

		@Override
		public void addPred(int arg0) {
			if (preds == null) {
				preds = IntSetUtil.make();
			}
			preds.add(arg0);
		}

		@Override
		public void addSucc(int arg0) {
			if (succs == null) {
				succs = IntSetUtil.make();
			}
			succs.add(arg0);
		}

		@Override
		public IntSet getPredNumbers() {
			return preds;
		}

		@Override
		public IntSet getSuccNumbers() {
			return succs;
		}

		@Override
		public void removeAllIncidentEdges() {
			removeIncomingEdges();
			removeOutgoingEdges();
		}

		@Override
		public void removeIncomingEdges() {
			if (preds != null) {
				preds.foreach(new IntSetAction() {
					@Override
					public void act(int x) {
						currentGraph.getNode(x).succs.remove(nodeNumber);
						
					}
				});
				preds.clear();
			}
		}

		@Override
		public void removeOutgoingEdges() {
			if (succs != null) {
				succs.foreach(new IntSetAction() {
					@Override
					public void act(int x) {
						currentGraph.getNode(x).preds.remove(nodeNumber);
						
					}
				});
				succs.clear();
			}
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return predicate;
		}
		
	}

	private static boolean DEBUG = true;

	private final Set<String> priorityPredicates;

	public static int MAX_COLORS = 128;

	private NumberedGraph<ColorNode> currentGraph;
	
	private int largestEdgeSetSize = 0;

	public ColoringFunction(Set<String> priorityPredicates) {
		this.priorityPredicates = priorityPredicates;
	}

	public ColoringFunction() {
		this(null);
	}

	private static Pair<Integer, Collection<String>> parseLine(String line, boolean parseAll) {
		StringTokenizer tokenizer = new StringTokenizer(line, " |");
		int sz = Integer.parseInt(tokenizer.nextToken());
		if (!parseAll && sz > MAX_COLORS) {
			return null;
		}
		int c = Integer.parseInt(tokenizer.nextToken());
		Collection<String> predicates = new ArrayList<String>(sz);
		while (tokenizer.hasMoreTokens()) {
			String str = tokenizer.nextToken();
			if (str != "") {
				// remove the <..> of nt, if it is there
				if (str.startsWith("<")) {
					str = str.substring(1, str.length() - 1);
				}
				if (!predicates.contains(str)) {
					predicates.add(str);
				}
			}
		}
		return Pair.make(c, predicates);
	}

	public void readEdgeSetsIntoGraph(String fileName) throws Exception {
		long time = System.currentTimeMillis();
		System.out.println("Reading edge sets into graph");
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		Map<String, ColorNode> currentPredicates = HashMapFactory.make();
		String line = null;
		currentGraph = new DelegatingNumberedGraph<ColorNode>();


		while ((line = reader.readLine()) != null) {
			Pair<Integer, Collection<String>> p = parseLine(line, false);
			if (p == null) {
				continue;
			}
			int count = p.fst;
			if (p.snd.size() > largestEdgeSetSize) {
				largestEdgeSetSize = p.snd.size();
			}
			// check if edge set has no priority preds at all
			if (priorityPredicates != null) {
				Set<String> edges = new HashSet<String>(p.snd);		
				edges.retainAll(priorityPredicates);
				if (edges.isEmpty()) {
					continue;
				}
			}
			Collection<String> predicates = p.snd;

			for (String p1 : predicates) {
				for (String p2 : predicates) {

					if (!currentPredicates.containsKey(p1)) {
						ColorNode c1 = new ColorNode(p1);
						c1.incrementPopularity(count);
						currentPredicates.put(p1, c1);
						currentGraph.addNode(currentPredicates.get(p1));
					}
					if (!currentPredicates.containsKey(p2)) {
						ColorNode c2 = new ColorNode(p2);
						c2.incrementPopularity(count);
						currentPredicates.put(p2, c2);
						currentGraph.addNode(currentPredicates.get(p2));
					}
					if (!p1.equals(p2)) {
						currentGraph.addEdge(currentPredicates.get(p1),
								currentPredicates.get(p2));
					}
				}
			}
		}
		reader.close();
		int numEdges = 0;
		Iterator<ColorNode> it = currentGraph.iterator();
		while (it.hasNext()) {
			numEdges += currentGraph.getSuccNodeCount(it.next());
		}
		System.out.println("Size of graph:" + currentGraph.getNumberOfNodes() + " edges: " + numEdges);
		System.out.println("Graph read in: " + (System.currentTimeMillis() - time));
	}


	private ColoredVertices<ColorNode> color(int maxColors) {
		long time = System.currentTimeMillis();
		ColoredVertices<ColorNode> colors;
		// if we are sure to have a coloring
		if (largestEdgeSetSize < maxColors) {
			if (priorityPredicates != null) {
				colors = new WelshPowell<ColorNode>().color(currentGraph,
						new Comparator<ColorNode>() {
							private final Comparator<ColorNode> usual = WelshPowell
									.defaultComparator(currentGraph);
	
							public int compare(ColorNode o1, ColorNode o2) {
								boolean p1 = priorityPredicates.contains(o1.predicate);
								if (p1 == priorityPredicates.contains(o2.predicate)) {
									return usual.compare(o1, o2);
								} else {
									return p1 ? -1
											: 1;
								}
							}
						}, maxColors);
			} else {
				colors = new WelshPowell<ColorNode>().color(currentGraph, maxColors);
			}
		} else {
			// this is a situation where we may not have a full coloring.  Try a comparator that orders by popularity of color nodes
			// rather than the usual one
			final Comparator<ColorNode> popularityComparator = new Comparator<ColorNode>() {
				
				public int compare(ColorNode o1, ColorNode o2) {
					if (o1.popularity == o2.popularity) {
						return o2.predicate.compareTo(o1.predicate);
					}
					return o2.popularity - o1.popularity;
				} 
			};
			
			if (priorityPredicates != null) {
				colors = new WelshPowell<ColorNode>().color(currentGraph,
						new Comparator<ColorNode>() {
							private final Comparator<ColorNode> usual = popularityComparator;
	
							public int compare(ColorNode o1, ColorNode o2) {
								boolean p1 = priorityPredicates.contains(o1.predicate);
								if (p1 == priorityPredicates.contains(o2.predicate)) {
									return usual.compare(o1, o2);
								} else {
									return p1 ? -1
											: 1;
								}
							}
						}, maxColors);
			} else {
				colors = new WelshPowell<ColorNode>().color(currentGraph, popularityComparator, maxColors);
			}
		}
		System.out.println("coloring took:" + (System.currentTimeMillis() - time));
		return colors;
	}

	public ColoredVertices<ColorNode> colorRemaining(ColoredVertices<ColorNode> colors) throws Exception {
		int numNodes = currentGraph.getNumberOfNodes();
		for (ColorNode n : colors.getColors().keySet()) {
			currentGraph.removeNodeAndEdges(n);
		}
		assert currentGraph.getNumberOfNodes() == numNodes - colors.getColors().size();
		long time = System.currentTimeMillis();
		ColoredVertices<ColorNode> remColors = color(MAX_COLORS);		
		System.out.println("remaining coloring took:" + (System.currentTimeMillis() - time));

		return remColors;
	}

	public static Pair<Map<String, Integer>, Map<String, Integer>> color(
			String fileName) throws Exception {
		return color(fileName, null, false);
	}
	public static Pair<Map<String, Integer>, Map<String, Integer>> color(
			String fileName, Set<String> priorityPredicates) throws Exception {
		return color(fileName, priorityPredicates, false);
	}
	
	public static Pair<Map<String, Integer>, Map<String, Integer>> color(
			String fileName, Set<String> priorityPredicates, boolean test) throws Exception {

		ColoringFunction colorFunc = new ColoringFunction(priorityPredicates);
		colorFunc.readEdgeSetsIntoGraph(fileName);
		ColoredVertices<ColorNode> colors = colorFunc.color(MAX_COLORS);
		if (test) {
			colorFunc.testColoring(colors, "First");
		}
		// KAVITHA: If we cannot color with 1 color, we will try to do the next subset
		// *eliminating* nodes we had in the graph.  We should therefore just add remaining colors
		// to the existing first coloring function
		ColoredVertices<ColorNode> remColors = null;
		if (!colors.isFullColoring()) {
			remColors = colorFunc.colorRemaining(colors);
			if (test) {
				colorFunc.testColoring(remColors, "Second");
			}
		}
		
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		Set<String> allPreds = new HashSet<String>();
		String line = null;
		while ((line = reader.readLine()) != null) {
			allPreds.addAll(parseLine(line, true).snd);
		}
		reader.close();
		
		
		return Pair.make(getColors(colors, allPreds), getColors(remColors, allPreds));
	}
	

	private void testColoring(ColoredVertices<ColorNode> colors, String numColoringFunc) {
		System.out.println("Checking " +  numColoringFunc + " coloring");
		//WelshPowellTest.assertColoring(currentGraph, colors.getColors(), colors.isFullColoring());
		if (!colors.isFullColoring()) {
			Map<ColorNode, Integer> coloring = colors.getColors();
			if (priorityPredicates != null) {
				Map<Integer, String> priorityPredsAssignment = new HashMap<Integer, String>();
				for (Map.Entry<ColorNode, Integer> e: coloring.entrySet()) {
					if (priorityPredicates.contains(e.getKey().predicate)) {
						assert e.getValue().intValue() != -1 : " priority predicates were not assigned a value";
						if (priorityPredsAssignment.containsKey(e.getValue())) {
							
							Iterator<ColorNode> neighbors = this.currentGraph.getPredNodes(e.getKey());
							
							while (neighbors.hasNext()) {
								assert !(neighbors.next().predicate.equals(priorityPredsAssignment.get(e.getValue()))) : " adjacent priority predicates assigned same integer value ";
							}
							neighbors = this.currentGraph.getSuccNodes(e.getKey());
							while (neighbors.hasNext()) {
								assert !(neighbors.next().predicate.equals(priorityPredsAssignment.get(e.getValue()))) : " adjacent priority predicates assigned same integer value ";
							}
						} else {
							priorityPredsAssignment.put(e.getValue(), e.getKey().predicate);
						}
					}
				}
				for (Integer i : priorityPredsAssignment.keySet()) {
					System.out.println("Assignment for priority predicates:" + i + " " + priorityPredsAssignment.get(i));
				}
			} else {
				int maxColoredPopularity = 0;
				int maxUncoloredPopularity = 0;
				for (Map.Entry<ColorNode, Integer> e: coloring.entrySet()) {
					int color = e.getValue().intValue();
					if (color > -1 && maxColoredPopularity < e.getKey().popularity) {
						maxColoredPopularity = e.getKey().popularity;
					} else if (color == -1 && maxUncoloredPopularity < e.getKey().popularity) {
						maxUncoloredPopularity = e.getKey().popularity;
					}
				}		
				assert maxUncoloredPopularity <= maxColoredPopularity : " predicates not colored by popularity";
				
			}
		}
		System.out.println(numColoringFunc + " Coloring checked");
	}
	private static Map<String, Integer> getColors(ColoredVertices<ColorNode> color, Set<String> allPredicates) {

		Map<String, Integer> ret = HashMapFactory.make();
		if (color != null) {
			for (Map.Entry<ColorNode, Integer> e : color.getColors().entrySet()) {
				ret.put(e.getKey().predicate, e.getValue());
			}
			for (String p : allPredicates) {
				if (!ret.containsKey(p)) {
					ret.put(p, color.getNumColors());
				}
			}
		}
		return ret;
	}

	public static void main(String[] args) throws Exception {
		Pair<Map<String, Integer>, Map<String, Integer>> colorsPair = ColoringFunction.color(args[0], null, true);
		Set<String> predicates = new HashSet<String>();
		predicates.addAll(colorsPair.fst.keySet());
		predicates.addAll(colorsPair.snd.keySet());
		System.out.println("Colored predicates:" + predicates.size());
		String line = null;
		ColoringFunction func = new ColoringFunction();
		BufferedReader reader = new BufferedReader(new FileReader(args[0]));
		Set<String> readP = new HashSet<String>();
		while ((line = reader.readLine()) != null) {
			
			StringTokenizer tokenizer = new StringTokenizer(line, " |");
			int sz = Integer.parseInt(tokenizer.nextToken());
			while (tokenizer.hasMoreTokens()) {
				String str = tokenizer.nextToken();
				if (str != "") {
					// remove the <..> of nt, if it is there
					if (str.startsWith("<")) {
						str = str.substring(1, str.length() - 1);
					}
					readP.add(str);
				}
			}
		}
		System.out.println("readp:" + readP.size());
		reader.close();
		
		
		
	}

}
