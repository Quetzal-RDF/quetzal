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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.StringTokenizer;

import com.ibm.research.rdf.store.config.Constants;

public class BasicStatistics implements Serializable {

	private static final long serialVersionUID = 4976744477424738397L;

	//
	// The name for which basic statistics are computed
	// e.g. "Resource Outdegree", "Resource Indegree", "Resource per Property",
	// "Number of triples"
	//
	private String statName;

	//
	// The type of values counted
	// e.g. "resources", "triples", "properties", "subjects"
	//
	private String valueType;
	private long valueCounter;

	private double minValue;
	private double maxValue;
	private String minValueLiteral;
	private String maxValueLiteral;

	private BigDecimal valueSum;
	private BigDecimal valueLogSum;
	private BigDecimal valueSquareSum;
	private BigDecimal arithmeticMean;
	private BigDecimal geometricMean;
	private BigDecimal standardDeviation;
	
	private CollapsibleHistogram histogram;

	public BasicStatistics(String statName, String valueType) {
		this.statName = new String(statName);
		this.valueType = new String(valueType);

		this.valueCounter = 0;
		this.minValue = Integer.MAX_VALUE;
		this.maxValue = Integer.MIN_VALUE;
		this.valueSum = BigDecimal.ZERO;
		this.valueLogSum = BigDecimal.ZERO;
		this.valueSquareSum = BigDecimal.ZERO;
		this.arithmeticMean = BigDecimal.ZERO;

		histogram = new CollapsibleHistogram();
	}

	public void updateStatistics(String currValueLiteral, double currValue) {
		BigDecimal currValueDec = new BigDecimal(currValue);

		histogram.increment((int)currValue);
		
		if (currValue < this.minValue) {
			this.minValue = currValue;
			this.minValueLiteral = new String(currValueLiteral);
		}
		if (currValue > this.maxValue) {
			this.maxValue = currValue;
			this.maxValueLiteral = new String(currValueLiteral);
		}

		this.valueCounter++;
		this.valueSum = this.valueSum.add(currValueDec);
		this.valueLogSum = this.valueLogSum.add(new BigDecimal(Math
				.log10(currValue)));
		this.valueSquareSum = this.valueSquareSum.add(currValueDec.pow(2));
		this.arithmeticMean = this.valueSum.divide(new BigDecimal(this.valueCounter), 10, RoundingMode.HALF_DOWN);

		BigDecimal tmpResult = (this.valueSquareSum.divide(new BigDecimal(this.valueCounter), 10, RoundingMode.HALF_DOWN))
								.subtract(this.arithmeticMean.pow(2));
		if (tmpResult.compareTo(BigDecimal.ZERO) < 0)
			tmpResult = BigDecimal.ZERO;
		this.standardDeviation = (new BigDecimal(Math.sqrt(tmpResult.doubleValue()))).setScale(10, RoundingMode.HALF_DOWN);

		this.geometricMean = (new BigDecimal(Math.pow(10, (this.valueLogSum.divide(new BigDecimal(this.valueCounter), 10,
									RoundingMode.HALF_DOWN)).doubleValue()))).setScale(3,RoundingMode.HALF_DOWN);
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer returnString = new StringBuffer();

		returnString.append(this.valueCounter + " " + this.valueType + " analyzed\n");
		returnString.append("Min " + this.statName + " is " + this.minValue + " in " + this.valueType + " " + this.minValueLiteral + "\n");
		returnString.append("Max " + this.statName + " is " + this.maxValue + " in " + this.valueType + " " + this.maxValueLiteral + "\n");
		returnString.append("The Arithmetic mean is    " + this.arithmeticMean + "\n");
		returnString.append("The Standard deviation is " + this.standardDeviation + "\n");
		returnString.append("The Geometric  mean is    " + this.geometricMean + "\n");
		
		returnString.append(this.histogram + "\n");

		return returnString.toString();
	}

	public String toOneLiner() {
		return "min: " + minValue + "(" + minValueLiteral + "); max: "
				+ maxValue + "(" + maxValueLiteral + ");" + "avg: "
				+ arithmeticMean + "; stdev: " + standardDeviation
				+ "; geometric mean: " + geometricMean;
	}

	public static BasicStatistics fromFile(File f) throws Exception {
		BasicStatistics ret = null;
		BufferedReader reader = new BufferedReader(new FileReader(f));
		String line = null;
		while ( (line = reader.readLine()) != null) {
			StringTokenizer tokenizer = new StringTokenizer(line, ": ");
			String name = tokenizer.nextToken();
			String value = tokenizer.nextToken();
			
			if (name.equals("Name")) {
				ret = new BasicStatistics(name, value);
			} else if (name.equals("Average")) {
				ret.arithmeticMean = new BigDecimal(Double.parseDouble(value));
			} else if (name.equals("Min")) {
				ret.minValue = Double.parseDouble(value);
			} else if (name.equals("Max")) {
				ret.maxValue = Double.parseDouble(value);
			} else if (name.equals("Std")) {
				ret.standardDeviation = new BigDecimal(Double.parseDouble(value));				
			} else if (name.equals("Histogram")) {
				int count = Integer.parseInt(tokenizer.nextToken());		
				ret.histogram.increment(Integer.parseInt(value), count);
			}
		}
		return ret;
	}
	public static BasicStatistics fromString(String s) {
		try {
			BasicStatistics ret;
			if (s == null)
				return null;
			if (s.length() == 0)
				return null;

			String[] lines = s.split("\r\n|\r|\n");
			if (lines.length < 6)
				return null;

			// first line, count and resource type
			String[] words = lines[0].split("\\s+");
			if (words.length < 2)
				return null;
			ret = new BasicStatistics("Parsed", words[1]);
			ret.valueCounter = Long.valueOf(words[0]);

			// second line, min
			words = lines[1].split("\\s+");
			// we just go through and parse the first thing starting with a
			// digit.
			// We're not interested in the actual literal
			double x = findFirstNumber(words);
			ret.minValue = x;

			// third line, max
			words = lines[2].split("\\s+");
			x = findFirstNumber(words);
			ret.maxValue = x;

			// fourth line, avg
			words = lines[3].split("\\s+");
			x = findFirstNumber(words);
			if (x != -1)
				ret.arithmeticMean = new BigDecimal(x);

			// fifth line, stdev
			words = lines[4].split("\\s+");
			x = findFirstNumber(words);
			if (x != -1)
				ret.standardDeviation = new BigDecimal(x);

			// sixth line, geometric mean
			words = lines[5].split("\\s+");
			x = findFirstNumber(words);
			if (x != -1)
				ret.geometricMean = new BigDecimal(x);

			return ret;
		} catch (Exception e) {
			e.printStackTrace(System.err);
			return null;
		}
	}

	public static double findFirstNumber(String[] words) {
		for (int i = 0; i < words.length; i++) {
			if (words[i].length() == 0)
				continue;
			if (Character.isDigit(words[i].charAt(0))) {
				try {
					return Double.parseDouble(words[i]);
				} catch (Exception e) {
				}
			}
		}
		return -1;
	}

	public String getStatName() { return statName; }
	public String getValueType() { return valueType; }
	public long getValueCounter() { return valueCounter; }
	public double getMinValue() { return minValue; }
	public double getMaxValue() { return maxValue; }
	public String getMinValueLiteral() { return minValueLiteral; }
	public String getMaxValueLiteral() { return maxValueLiteral; }
	public BigDecimal getArithmeticMean() { return arithmeticMean; }
	public BigDecimal getGeometricMean() { return geometricMean; }
	public BigDecimal getStandardDeviation() { return standardDeviation; }
	public CollapsibleHistogram getHistogram() { return histogram; }

	public BigDecimal getStandardError() {
		BigDecimal bd = new BigDecimal(Math.sqrt(valueCounter));
		bd = standardDeviation.divide(bd, Constants.ROUNDING_DECIMALS,
				BigDecimal.ROUND_HALF_DOWN);
		return bd;
	}
	
	

}
