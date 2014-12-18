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
 package com.ibm.research.proppaths;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.sparql11.model.Constant;
import com.ibm.wala.util.collections.HashSetFactory;

public class InsertValuesIntoTable extends InsertIntoTable {

	protected Set<List<Constant>> values;
	protected ArrayList<Boolean> includeTypes;
	
	
	public InsertValuesIntoTable(String table, Set<List<Constant>> values, ArrayList<Boolean> includeTypes, Store store) {
		super(table, toSQLValues(values, includeTypes, store));
		this.values = values;
		this.includeTypes = includeTypes;
	}
	public InsertValuesIntoTable(String table, Set<Constant> values, boolean includeType, Store store) {
		super(table, toSQLValues(values, includeType, store));
		this.values = HashSetFactory.make();
		for (Constant v: values) {
			this.values.add(Collections.singletonList(v));
		}
		this.includeTypes = new ArrayList<Boolean>(1);
		this.includeTypes.add(includeType);
	}
	public static String toSQLValues(Set<Constant> values, boolean includeType, Store store) {
		Set<List<Constant>> vals = HashSetFactory.make();
		for (Constant v: values) {
			vals.add(Collections.singletonList(v));
		}
		ArrayList<Boolean> includeTypes = new ArrayList<Boolean>(1);
		includeTypes.add(includeType);
		return toSQLValues(vals, includeTypes, store);
	}
		
	public static String toSQLValues(Set<List<Constant>> values, ArrayList<Boolean> includeTypes, Store store) {
		
		boolean isPostGres = CodeGenerator.isPostGresBackend(store);
		StringBuffer buf = new StringBuffer();
		buf.append("VALUES \n");
		boolean first = true;
		for (List<Constant> vals : values) {
			if (first) {
				first  = false;
				} else {
					buf.append(",\n ");
				}
			buf.append("(");
			boolean firstVal = true;
			int count = 0;
			for (Constant val: vals) {
				if (firstVal) {
					firstVal = false;
				} else {
					buf.append(", ");
				}
				buf.append("'").append(val.toSqlDataString()).append("'").append((isPostGres?"::text":""));
				if (includeTypes.get(count)) {
					buf.append(", ");
					buf.append(val.toDataType());
				}
				count++;
			}
			buf.append(")");
		}
		return buf.toString();
	}



}
