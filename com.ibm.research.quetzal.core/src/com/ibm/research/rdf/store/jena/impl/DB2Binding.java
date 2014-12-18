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
package com.ibm.research.rdf.store.jena.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.engine.binding.BindingBase;

public class DB2Binding extends BindingBase {
	
	Map<Var, Node> vars = new HashMap<Var, Node>();

	protected DB2Binding(Binding parent) {
		super(parent);
	}

	
	protected void add(Var name, Node node) {
		if (getParent()!=null) {
			throw new  RuntimeException("Cannot modified a binding object with a parent");
		}
		vars.put(name, node);
	}

	/*@Override
	protected void checkAdd1(Var var, Node node) {
		
	}*/

	@Override
	protected boolean contains1(Var var) {
		return vars.containsKey(var);
	}

	@Override
	protected Node get1(Var var) {
		return vars.get(var);
	}

	@Override
	protected boolean isEmpty1() {
		return vars.isEmpty();
	}

	@Override
	protected int size1() {
		return vars.size();
	}

	@Override
	protected Iterator<Var> vars1() {
		return vars.keySet().iterator();
	}
	
}
