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
 package com.ibm.research.rdf.store;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.hp.hpl.jena.sparql.util.Symbol;
/**
 * 
 **
 */
public class Context {

	public final static String rdfStoreSpace = "http://rdfstore.ibm.com/IM/symbol#";

	public final static Symbol unionDefaultGraph = Symbol.create(rdfStoreSpace
			+ "unionDefaultGraph");
	public final static Symbol systemPredicatesFilter = Symbol
			.create(rdfStoreSpace + "sysPredicatesContext");

	private Map<Symbol, Object> context = new HashMap<Symbol, Object>();

	public Object get(Symbol uri) {
		return context.get(uri);
	}

	public void set(Symbol uri, Object object) {
		context.put(uri, object);
	}

	public void putAll(Map<Symbol, Object> all) {
		context.putAll(all);
	}

	public Set<Symbol> getSymbols() {
		return context.keySet();
	}

	public Context copy() {
		Context copy = new Context();
		copy.putAll(context);
		return copy;
	}
	
	public static final Context defaultContext = new Context();
	
}
