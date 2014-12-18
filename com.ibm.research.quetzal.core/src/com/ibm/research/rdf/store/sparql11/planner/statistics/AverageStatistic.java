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

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

import com.ibm.research.rdf.store.config.Constants;

/**
 * Includes data on an average and standard deviation statistic.
 */
public class AverageStatistic implements Serializable {
	
	private static final long serialVersionUID = -2570240192017990207L;
	private double average = -1;
	private double stdev = -1;
	private BigInteger sum = BigInteger.ZERO;
	private BigInteger count = BigInteger.ZERO;
	private BigInteger sumOfSquares = BigInteger.ZERO;
	boolean compute = true;

	public AverageStatistic() {
	}

	public AverageStatistic(double average, double stdev) {
		this.average = average;
		this.stdev = stdev;
		this.compute = false;
	}
	public void add(long value) {
		sum = sum.add(BigInteger.valueOf(value));
		count = count.add(BigInteger.ONE);
		sumOfSquares = sumOfSquares.add(BigInteger.valueOf(value).multiply(BigInteger.valueOf(value)));
	}
	
	public BigInteger getCount() {
		return count;
	}

	public double average() {
		if(compute) {
			if( !count.equals(BigInteger.ZERO)) {
				average = (new BigDecimal(sum)).divide(new BigDecimal(count), Constants.ROUNDING_DECIMALS, BigDecimal.ROUND_HALF_DOWN).doubleValue();
			}
		}
		return average;
	}
	
	public double stdev() {

		if(compute) {
			if( !count.equals(BigInteger.ZERO)) {
				double avg = average();
				stdev = Math.sqrt((new BigDecimal(sumOfSquares)).divide(new BigDecimal(count), Constants.ROUNDING_DECIMALS, BigDecimal.ROUND_HALF_DOWN).subtract((new BigDecimal(avg)).multiply(new BigDecimal(avg))).doubleValue());
			}
		}
		return stdev;
	}
	
	public String toString() {
		return "(Average: " + average() + "; stdev: " + stdev() + ")";
	}
}
