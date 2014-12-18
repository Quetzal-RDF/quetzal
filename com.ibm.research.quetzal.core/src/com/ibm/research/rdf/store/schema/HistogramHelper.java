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
 package com.ibm.research.rdf.store.schema;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author oudrea
 * Provides various utility methods for histograms.
 */
public class HistogramHelper {
	
	/**
	 * Prints the histogram to a string according to the given format
	 * @param h - the histogram
	 * @param format - the format as a string. %C is the category, %V is the value
	 * @return The String representation.
	 */
	public static String toString(Histogram<Integer> h, String format) {
		List<Integer> categs = new ArrayList<Integer>(h.keySet());
		Collections.sort(categs);
		String res;
		StringBuilder sb = new StringBuilder();
		for(Integer i: categs) {
			res = format.replaceAll("%C", i.toString());
			res = res.replaceAll("%V", h.get(i).toString());
			sb.append(res);
		}
		return sb.toString();
	}

	/**
	 * Prints the histogram to a string according to the given format
	 * @param h - the histogram
	 * @param format - the format as a string. %C is the category, %V is the value
	 * @return The String representation.
	 */
	public static String collapsedToString(Histogram<String> h, String format) {
		String res;
		StringBuilder sb = new StringBuilder();
		for(String s: h.keySet()) {
			res = format.replaceAll("%C", s);
			res = res.replaceAll("%V", h.get(s).toString());
			sb.append(res);
		}
		return sb.toString();
	}
	
	public static class OrderedHistogram<T> {
		public List<T> keys;
		public List<BigInteger> values;
		
		public String toString() {
			StringBuffer sbuf = new StringBuffer();
			if((keys == null) || (values == null)) return null;
			for(int i = 0; i < Math.min(keys.size(), values.size()); i++) {
				sbuf.append(keys.get(i).toString() + "<" + values.get(i).toString() + "> ");
			}
			return sbuf.toString();
		}
	}
	
	public static class IntegerComparator implements Comparator<Integer> {
		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Integer o1, Integer o2) {
			return o1 - o2;
		}
	}
	
	public static class StringIntervalComparator implements Comparator<String> {
		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(String o1, String o2) {
			if((o1 == null) || (o2 == null)) return -1;
			String s1 = o1.trim(), s2 = o2.trim();
			if(!s1.startsWith("[") || !s2.startsWith("[") || !s1.endsWith("]") || !s2.endsWith("]"))
				return -1;
			String s1nos = s1.substring(1, s1.length() - 1);
			String s2nos = s2.substring(1, s2.length() - 1);
			String[] s1pieces = s1nos.split("\\,");
			String[] s2pieces = s2nos.split("\\,");
			
			if((s1pieces.length != 2) || (s2pieces.length != 2)) return -1;
			try {
				return Integer.valueOf(s1pieces[0]) - Integer.valueOf(s2pieces[0]);
			} catch(Exception e) { return -1; }
		}
	}
	
	// orders a histogram ascending by the order of its keys.
	public static <T> OrderedHistogram<T> orderHistogram(Histogram<T> h, Comparator<T> comp) {
		if(h == null) return null;
		OrderedHistogram<T> ret = new OrderedHistogram<T>();
		ArrayList<T> keys = new ArrayList<T>();
		ArrayList<BigInteger> values = new ArrayList<BigInteger>();
		
		keys.addAll(h.keySet());
		Collections.sort(keys, comp);
		for(T x: keys) {
			values.add(h.get(x));
		}
		
		ret.keys = keys;
		ret.values = values;
		return ret;
	}
}
