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
 package com.ibm.research.rdf.store.sparql11.sqlwriter;

public class MonitoredStringBuffer {
	private final StringBuffer buf;
	
	public MonitoredStringBuffer(String string) {
		buf = new StringBuffer(string);
	}

	public MonitoredStringBuffer() {
		buf = new StringBuffer();
	}

	public MonitoredStringBuffer append(String string) {
		if (string.contains("QS5.class")) {
			System.err.println("string");
		}
		buf.append(string);
		return this;
	}

	public MonitoredStringBuffer append(char c) {
		buf.append(c);
		return this;
	}

	public MonitoredStringBuffer append(int hash) {
		buf.append(hash);
		return this;
	}

	public <T> MonitoredStringBuffer append(T obj) {
		buf.append(obj);
		return this;
	}

	public int length() {
		return buf.length();
	}

	public int indexOf(String x) {
		return buf.indexOf(x);
	}

	public String toString() {
		return buf.toString();
	}
}
