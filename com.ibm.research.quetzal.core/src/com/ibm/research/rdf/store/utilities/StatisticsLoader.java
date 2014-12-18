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
 package com.ibm.research.rdf.store.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import com.ibm.research.rdf.store.config.Statistics;
import com.ibm.research.rdf.store.config.StatisticsImpl;
import com.ibm.research.rdf.store.sparql11.planner.statistics.AverageStatistic;

/**
 * This class is used to read the values of Statistics gathered from SQL into a
 * Java serialized object. The Statistics objects will be used in query
 * planning. All statistics are expected to follow the following patterns
 * (examples are provided in rdf_data/graphmetrics)
 * 
 * Per graph statistics prefixed by "graph" Subject statistics: subj Predicate
 * statistics: pred Object statistics: obj Top K: cnt Average: avg
 * 
 * @author ksrinivs
 * 
 */
public class StatisticsLoader {

	public static final String graph = "graph";
	public static final String subject = "subj";
	public static final String predicate = "pred";
	public static final String triplesCount = "nr_triples";
	public static final String object = "obj";
	public static final String topK = "cnt";
	public static final String average = "avg";

	public static final String SUFFIX = ".out";
	public static final String DELIMITER = "_";

	private static String directory = null;

	public static void main(String[] args) throws Exception {
		directory = args[0];
		loadStatistics(args[1]);
	}

	public static void loadStatistics(String out) throws Exception {
		Statistics perGraphStatistics = new StatisticsImpl();
		populateStatistics(perGraphStatistics, true);
		System.out.println("Per Graph Statistics");
		System.out.println(perGraphStatistics);
		ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(out
				+ "_perGraphStatistics"));
		os.writeObject(perGraphStatistics);
		os.close();
		Statistics overallStatistics = new StatisticsImpl();
		populateStatistics(overallStatistics, false);
		System.out.println("Overall Statistics");
		System.out.println(overallStatistics);
		os = new ObjectOutputStream(new FileOutputStream(out
				+ "_graphStatistics"));
		os.writeObject(overallStatistics);
		os.close();
	}

	private static void populateStatistics(Statistics stats, boolean addGraph)
			throws Exception {
		// subject top K
		StringBuffer fName = createFileName(addGraph, subject, topK);
		stats.setTopSubjects(readTopK(fName.toString()));
		// predicate topK
		fName = createFileName(addGraph, predicate, topK);
		stats.setTopPredicates(readTopK(fName.toString()));
		// object topK
		fName = createFileName(addGraph, object, topK);
		stats.setTopObjects(readTopK(fName.toString()));
		// subject predicate topK
		/*fName = createFileName(addGraph, subject + DELIMITER + predicate, topK);
		stats.topSubject_Predicate_Pairs = readTopK(fName.toString());
		// object predicate pairs
		fName = createFileName(addGraph, object + DELIMITER + predicate, topK);
		stats.topObject_Predicate_Pairs = readTopK(fName.toString());*/

		// averages
		fName = createFileName(addGraph, subject, average);
		stats.setSubjectStatistic(readAverage(fName.toString()));
		// predicate
		fName = createFileName(addGraph, predicate, average);
		stats.setPredicateStatistic(readAverage(fName.toString()));
		// object
		fName = createFileName(addGraph, object, average);
		stats.setObjectStatistic(readAverage(fName.toString()));
		// subject predicate
		fName = createFileName(addGraph, subject + DELIMITER + predicate,
				average);
		stats.setSubjectPredicatePairs(readAverage(fName.toString()));
		// object predicate
		fName = createFileName(addGraph, object + DELIMITER + predicate,
				average);
		stats.setObjectPredicatePairs(readAverage(fName.toString()));

		fName = createFileName(false, triplesCount, null);
		stats.setTripleCount(readCount(fName.toString()));

	}

	private static StringBuffer createFileName(boolean isGraph, String pos1,
			String pos2) {
		StringBuffer fName = new StringBuffer(directory).append(File.separator);
		if (isGraph) {
			fName.append(graph).append(DELIMITER);
		}
		if (pos1 != null) {
			fName.append(pos1);
			if (pos2 != null) {
				fName.append(DELIMITER);
			}
		}
		if (pos2 != null) {
			fName.append(pos2);
		}
		fName.append(SUFFIX);
		return fName;
	}

	private static ArrayList<String> getValidLines(String file) throws Exception {
		ArrayList<String> linesToReturn = new ArrayList<String>();
		int counter = 0;
		int startIndex = -1;
		int endIndex = -1;
		ArrayList<String> readLines = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		while ((line = reader.readLine()) != null) {
			readLines.add(line);
			if (line.matches("^[\\-]+.*")) {
				if (counter == 1) {
					startIndex = readLines.size() - 1;
				} 
				counter++;
			}
			if (line.matches(".* row[(]s[)] fetched.*")) {
				endIndex = readLines.size() - 1;
			}
		}
		assert startIndex != -1;
		assert endIndex != -1;
		for (int j = startIndex + 1; j < endIndex; j++) {
			if (!readLines.get(j).trim().equals("")) {
				linesToReturn.add(readLines.get(j));
				System.out.println("line:" + readLines.get(j));
			}
		}
		reader.close();
		return linesToReturn;
	}

	private static AverageStatistic readAverage(String file) throws Exception {
		ArrayList<String> lines = getValidLines(file);
		assert lines.size() == 1;
		String line = lines.get(0);
		StringTokenizer tokenizer = new StringTokenizer(line);
		assert tokenizer.countTokens() == 2;
		double avg = Double.parseDouble(tokenizer.nextToken());
		double std = Double.parseDouble(tokenizer.nextToken());
		return new AverageStatistic(avg, std);
	}
	
	private static BigInteger readCount(String file) throws Exception {
		ArrayList<String> lines = getValidLines(file);
		assert lines.size() == 1;
		String line = lines.get(0);
		StringTokenizer tokenizer = new StringTokenizer(line);
		assert tokenizer.countTokens() == 1;
		BigInteger count = new BigInteger(tokenizer.nextToken());
		assert !count.equals(BigInteger.ZERO);
		return count;
	}

	private static Map<String, BigInteger> readTopK(String file)
			throws Exception {
		Map<String, BigInteger> ret = new HashMap<String, BigInteger>();
		ArrayList<String> lines = getValidLines(file);

		for (String line: lines) {
			if (line.trim().equals("")) {
				continue;
			}
			StringTokenizer tokenizer = new StringTokenizer(line);
			int count = tokenizer.countTokens();
			StringBuffer buf = new StringBuffer(); // this is idiotic but we do
													// need the string which is
													// the last token parsed
													// to handle literals properly
			BigInteger num = null;
			while (tokenizer.hasMoreTokens()) {
				if (count > 1) {
					buf.append(tokenizer.nextToken()).append(" ");
				} else {
					num = new BigInteger(tokenizer.nextToken());
				}
				count--;
			}
			assert !buf.equals("");
			assert num != null : " line: " + line;
			System.out.println("buf:" + buf.toString() + " num:" + num);

			ret.put(buf.toString(), num);

		}
		return ret;
	}

}
