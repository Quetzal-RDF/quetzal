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
 package com.ibm.research.rdf.store.sparql11.planner.statistics;

import java.util.Comparator;

import com.ibm.research.rdf.store.schema.Pair;

/**
 * This is the central way of comparing statistics. In other words, how do we decide whether a cardinality estimate with average a1 and stdev s1 is "lower" (better) 
 * than one with average a2 and standard deviation s2?
 * For now, we simply compare the averages.
 */
public class StatisticComparator implements Comparator<Pair<Double>> {

	public static StatisticComparator instance = new StatisticComparator();
	
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Pair<Double> object1, Pair<Double> object2) {
		if((object1 == null) && (object2 != null)) return -1;
		else if((object1 != null) && (object2 == null)) return 1;
		else if((object1 == null) && (object2 == null)) return 0;
		return (int)Math.signum(object1.getFirst() - object2.getFirst());
	}

	public static boolean lessThan(Pair<Double> s1, Pair<Double> s2) {
		return instance.compare(s1, s2) < 0;
	}

	public static boolean lessThanEquals(Pair<Double> s1, Pair<Double> s2) {
		return instance.compare(s1, s2) <= 0;
	}

	public static boolean greaterThan(Pair<Double> s1, Pair<Double> s2) {
		return instance.compare(s1, s2) > 0;
	}
	
	public static boolean greaterThanEquals(Pair<Double> s1, Pair<Double> s2) {
		return instance.compare(s1, s2) >= 0;
	}

	public static boolean equals(Pair<Double> s1, Pair<Double> s2) {
		return instance.compare(s1, s2) == 0;
	}
	
	public static Pair<Double> add(Pair<Double> s1, Pair<Double> s2) {
		return new Pair<Double>(s1.getFirst() + s2.getFirst(), Math.max(s1.getSecond(), s2.getSecond()));
	}
}
