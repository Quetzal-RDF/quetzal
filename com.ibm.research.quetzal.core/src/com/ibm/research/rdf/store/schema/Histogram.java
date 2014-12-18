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
import java.util.HashMap;
import java.util.Map;

/**
 * @author oudrea<br/>
 * This object can be used to record a histogram. It is parameterized by the base category type. Each category will be
 * mapped to a count.
 */
public class Histogram <T> extends HashMap<T, BigInteger>{
	
	private static final long serialVersionUID = 3073883211841994282L;

	public Histogram () {
		super();
	}
	
	public void increment(T category) {
		increment(category, 1);
	}
	
	public void increment(T category, int step) {
		BigInteger bistep = BigInteger.valueOf(step);
		if(containsKey(category)) {
			put(category, get(category).add(bistep));
		} else put(category, bistep);		
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (Map.Entry<T, BigInteger> e : this.entrySet()) {
			buf.append("Histogram:" + e.getKey() + "-" + e.getValue());
		}
		return buf.toString();
	}
	
	
}
