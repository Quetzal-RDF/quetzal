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
 package com.ibm.research.rdf.store.runtime.service.types;

import java.util.HashMap;
import java.util.Map;

public class TypedValue {

	private String value;
	private short type;

	public TypedValue(String value, short type) {
		super();

		this.value = value;
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public short getType() {
		return type;
	}

	public Object getNumericValue() {
		return TypeMap.getNumericValue(type, value);
	}

	public Object getTimeStampValue() {
		return TypeMap.getTimeStampValue(type, value);
	}

	@Override
	public int hashCode() {
		// This is for PrimaryGroupHashTable in RPH
		// int ret = ((value + type).toCharArray()).hashCode();
		// System.out.println((value + type) + " - " + ret);
		return (value + type).hashCode();

	}

	@Override
	public boolean equals(Object o) {

		if (o.getClass() != TypedValue.class)
			return false;

		TypedValue other = (TypedValue) o;
		if (other.type == this.type && other.value.equalsIgnoreCase(this.value))
			return true;
		return false;

	}

	public static void main(String[] args) {
		TypedValue t1 = new TypedValue("a", (short) 701);
		TypedValue t2 = new TypedValue("a", (short) 701);

		// System.out.println(t1.hashCode());
		// System.out.println(t2.hashCode());

		Map<TypedValue, String> namedGraph = new HashMap<TypedValue, String>();
		namedGraph.put(t1, "t1");
		String s = namedGraph.get(t2);
		System.out.println(s);

	}

}
