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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;

import com.ibm.research.rdf.store.config.Constants;

/**
 * @author oudrea<br/>
 * This class is a collapsible histogram that maps integers to counts
 * (for instance, the number of properties per subject to the number of subjects with that
 * property count). It can be collapsed into user-specified ranges.
 */
public class CollapsibleHistogram extends Histogram<Integer> {

	private static final long serialVersionUID = -3984174935043963397L;
	
	public CollapsibleHistogram() {
		super();
	}
	
	/** 
	 * Collapses this histogram into a new one with the given number of categories.
	 * This is done by creating intervals that cover the min to max of key values and 
	 * aggregating the values for each interval.
	 */
	public Histogram<String> collapse(int no_categories) {
		if(size() == 0) return null;
		if(no_categories == 0) return null;
		Histogram<String> ret = new Histogram<String>();
		
		int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
		for(Integer x: keySet()) {
			if(x < min) min = x;
			if(x > max) max = x;
		}
		
		// we found the interval, now to split it up.
		int crMin, crMax, intsize = (max - min) / no_categories;
		for(int i = 0; i < no_categories; i++) {
			crMin = min + i * intsize;
			crMax = crMin + intsize - 1;
			if(i == (no_categories - 1)) crMax = max;
			String intName = "[" + crMin + "," + crMax + "]";
			BigInteger aggVal = BigInteger.ZERO;
			for(int x = crMin; x <= crMax; x++) {
				if(!containsKey(x)) continue;
				aggVal = aggVal.add(get(x));
			}
			ret.put(intName, aggVal);
		}
		
		return ret;
	}
	
	/** 
	 * Collapses the histogram into a new one where it tries to keep the size of the bars
	 * approximately the same, but vary the size of the intervals.
	 */
	public Histogram<String> collapseEqualBars(int no_categories) {
		if(size() == 0) return null;
		if(no_categories == 0) return null;
		Histogram<String> ret = new Histogram<String>();
		
		BigInteger sum = BigInteger.ZERO;
		int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
		for(Integer x: keySet()) {
			if(x < min) min = x;
			if(x > max) max = x;
			sum = sum.add(get(x));
		}
		
		// now that we computed the sum, we have to compute the approximate bar size
		BigDecimal barSize = new BigDecimal(sum);
		barSize = barSize.divide(new BigDecimal(no_categories), Constants.ROUNDING_DECIMALS, BigDecimal.ROUND_HALF_DOWN);
		
		// now we go from min to max. Every time the current bar size is equal or greater
		// than the estimated bar size, we stop right there.
		int crStart = min, crEnd = -1;
		BigDecimal crSum = BigDecimal.ZERO;
		for(int x = min; x <= max; x++) {
			if(!containsKey(x)) continue;
			crSum = crSum.add(new BigDecimal(get(x)));
			if(crSum.compareTo(barSize) >= 0) {
				crEnd = x;
				ret.put("[" + crStart + "," + crEnd + "]", crSum.toBigInteger());
				crSum = BigDecimal.ZERO;
				
				crStart = x + 1;
				crEnd = -1;
			}
		}
		
		if(crStart <= max) {
			crEnd = max;
			ret.put("[" + crStart + "," + crEnd + "]", crSum.toBigInteger());
		}
		
		return ret;
		
	}
	
	// parses a histogram from a string. this assumes each element does NOT contain spaces.
	// by default, it creates a String / Integer mapping
	public static Histogram<String> parseHistogram(String s) {
		if((s == null) || (s.length() == 0)) return null;
		Histogram<String> ret = new Histogram<String>();
		String categ, value;
		String elems[] = s.split("<|>|\\s+");
		for(int i = 0; i < elems.length; i++) {
			if(elems[i].length() == 0) continue;
			categ = elems[i];
			if(i == (elems.length - 1)) continue;
			value = elems[++i];
			ret.put(categ, new BigInteger(value));
		}

		return ret;
	}
	
	// converts a Histogram<String> into a Histogram<Int>. Be very careful when you 
	// do this
	public static Histogram<Integer> convert(Histogram<String> h) {
		try {
			if(h == null) return null;
			Histogram<Integer> ret = new Histogram<Integer>();
			for(String s: h.keySet()) {
				ret.put(Integer.valueOf(s), h.get(s));
			}
			return ret;
		} catch(Exception e) {
			e.printStackTrace(System.err);
			return null; 
		}
	}
	
	/** Returns the maximum key value in this histogram. */
	public int getMaxKey() {
		return Collections.max(keySet());
	}
	
	/** Returns the minimum key value in this histogram. */
	public int getMinKey() {
		return Collections.min(keySet());
	}
	
	/** Returns the sum of all bars with a key value above a given threshold. */
	public BigInteger getTotalAbove(int threshold) {
		BigInteger ret = BigInteger.ZERO;
		for(int x: keySet()) {
			if(x > threshold) ret = ret.add(get(x));
		}
		return ret;
	}

	/** Returns the sum of all bars with a key value above or equal to a given threshold. */
	public BigInteger getTotalBelow(int threshold) {
		BigInteger ret = BigInteger.ZERO;
		for(int x: keySet()) {
			if(x <= threshold) ret = ret.add(get(x));
		}
		return ret;
	}
}
