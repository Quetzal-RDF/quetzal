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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ibm.research.rdf.store.jena.RdfStoreException;
import com.ibm.wala.util.collections.HashSetFactory;
import com.ibm.wala.util.collections.Pair;

public class AssignHashesToPredicates {
	
	private final int numTotalFunctions;
	
	private static class Functions {
		private Functions(
				Pair<Map<String, Integer>, Map<String, Integer>> colors,
				RabinHashFamily hashes) {
			this.colors = colors;
			this.hashes = hashes;
		}
		private final Pair<Map<String, Integer>, Map<String, Integer>> colors;
		private final RabinHashFamily hashes;
	}
	
	private Functions createHashingFunctions(String edgeSetCounts, boolean useColoring, Set<String> priorityPredicates) throws Exception {
		Pair<Map<String, Integer>, Map<String, Integer>> colorsPair = ColoringFunction.color(edgeSetCounts, priorityPredicates);
		int maxHashBuckets = new HashSet<Integer>(colorsPair.fst.values()).size();
		
		int numFns = numTotalFunctions;
		if (useColoring) {
			numFns = numTotalFunctions - (colorsPair.snd.isEmpty() ? 1: 2);
			numFns = numFns < 0 ? 0 : numFns;
		}
		
		RabinHashFamily family = null;
		if (numFns != 0) {
			family = new RabinHashFamily(maxHashBuckets, numFns);
			numFns = family.hashes.size();							// HACK ALERT.  If num fns is large, and max buckets is small, then we may fail to get the																	// the required num of funns
		}

		return new Functions(colorsPair, family);
	}
	
	public AssignHashesToPredicates(int numHashFunctions) {
		this.numTotalFunctions = numHashFunctions;
	}

	public void createHashingFunctionsDataset(String edgeSetCounts, String outFileForLoading, boolean useColoring, boolean isDirect, Set<String> priorityPredicates) throws Exception {
		Functions f = createHashingFunctions(edgeSetCounts, useColoring, priorityPredicates);

		// Create a dataset of default graph
		PredicateMappingsDataSet dataset;
		if (useColoring) {
			dataset = new PredicateMappingsDataSet(Math.max(numTotalFunctions-2, 1), Math.min(2, numTotalFunctions));
		} else {
			dataset = new PredicateMappingsDataSet(numTotalFunctions, 0);
		}

		// Write coloring function
		dataset.writeColoringFunctionToDataset(isDirect, f.colors);
					
		System.err.println(f.hashes.fSize);
		
		if (f.hashes != null) {
			// Write hashing function
			dataset.writeHashingFunctionToDataset(f.colors.fst.keySet(), isDirect, f.hashes);
		}
		
		// Dump the dataset to a file in n-quad format
		PrintStream ps = null;
		try {
			ps = new PrintStream(new BufferedOutputStream(new FileOutputStream(outFileForLoading)));
		}
		catch(FileNotFoundException e) {
			throw new RdfStoreException(e.getLocalizedMessage(),e);
		}
		dataset.exportToFile(ps);
		ps.close();
	}
	
	public void createHashingFunctionsLoadFile(String edgeSetCounts, String outFileForLoading, boolean useColoring, Set<String> priorityPredicates) throws Exception {
		Functions f = createHashingFunctions(edgeSetCounts, useColoring, priorityPredicates);
		BufferedWriter buf = new BufferedWriter(new FileWriter(outFileForLoading));
		for (String s : f.colors.fst.keySet()) {
			StringBuffer bf = new StringBuffer("<" + s + ">");
			bf.append(" ");
			if (useColoring) {
				bf.append(f.colors.fst.get(s)).append(" ");
				if (! f.colors.snd.isEmpty()) {
					bf.append(f.colors.snd.get(s)).append(" ");
				}
			}
			if (f.hashes != null) {
				for (int i = 0; i < f.hashes.hashes.size(); i++) {
					f.hashes.computeHash(s);
					int value = f.hashes.hash(s, i);
					bf.append(value).append(" ");
				}
			}
			bf.append("\n");
			buf.write(bf.toString());
		}
		buf.close();
	}
	
	private static Set<String> readPredicateFile(String fileName) throws IOException {
		Set<String> preds = HashSetFactory.make();
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		String line;
		while ((line = reader.readLine()) != null) {
			String str = line.trim();
			// remove the <..> of nt, if it is there
			if (str.startsWith("<")) {
				str = str.substring(1, str.length() - 1);
			}

			preds.add(str);
		}
		reader.close();
		return preds;
	}
	
	public static void main(String[] args) throws Exception {
		String inFile = args[0];
		int hashes = Integer.parseInt(args[1]);
		boolean coloring = Boolean.parseBoolean(args[2]);
		int c = coloring? 1: 0;
		boolean isDirect = Boolean.parseBoolean(args[3]);
		Set<String> priorityPredicates = (args.length > 4)? readPredicateFile(args[4]): null;
		
		AssignHashesToPredicates p = new AssignHashesToPredicates(hashes);
		String outFile = args[0] + c + "_" + hashes + ".hashes";
		p.createHashingFunctionsLoadFile(inFile, outFile, coloring, priorityPredicates);
		outFile = args[0] + c + "_" + hashes + ".load";
		p.createHashingFunctionsDataset(inFile, outFile, coloring, isDirect, priorityPredicates);
	}
}
