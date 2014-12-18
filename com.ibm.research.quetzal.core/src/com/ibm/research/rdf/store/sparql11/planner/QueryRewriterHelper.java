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
 package com.ibm.research.rdf.store.sparql11.planner;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ibm.research.rdf.store.sparql11.model.Constant;
import com.ibm.research.rdf.store.sparql11.model.IRI;
import com.ibm.research.rdf.store.sparql11.model.Pattern;
import com.ibm.research.rdf.store.sparql11.model.QueryTriple;
import com.ibm.research.rdf.store.sparql11.model.Variable;
import com.ibm.wala.util.collections.HashMapFactory;

public class QueryRewriterHelper {

	Map<IRI, List<RenamedIRIConstant>> IRIsMap = HashMapFactory.make();
	Map<Constant, List<RenamedIRIConstant>> constantsMap = HashMapFactory.make();
	Map<IRI, Variable> IRIsToVarMap = HashMapFactory.make();
	Map<Constant, Variable> ConstantToVarMap = HashMapFactory.make();

	private static final String VARIABLE_PREFIX = "intStVar";
	int variableSuffix = 0;


	public void add(IRI iri, QueryTriple querytriple, Pattern p) {
		
		// If the IRI is already in the constantsMap, add the current triple to
		// the LinkedList
		//
		if (this.IRIsMap.containsKey(iri)) {
			this.IRIsMap.get(iri).add(new RenamedIRIConstant(p, querytriple));
		}
		//
		// if this is the first time we see the constant, create a new
		// LinkedList and add it to the constantsMap
		//
		else {
			LinkedList<RenamedIRIConstant> newTripleList = new LinkedList<RenamedIRIConstant>();
			newTripleList.add(new RenamedIRIConstant(p, querytriple));
			this.IRIsMap.put(iri, newTripleList);
			Variable v = new Variable(VARIABLE_PREFIX + variableSuffix, true);
			variableSuffix++;
			this.IRIsToVarMap.put(iri, v);

		}
	}
	
	public void add(Constant c, QueryTriple querytriple, Pattern p) {
		
		// If the IRI is already in the constantsMap, add the current triple to
		// the LinkedList
		//
		if (this.constantsMap.containsKey(c)) {
			this.constantsMap.get(c).add(new RenamedIRIConstant(p, querytriple));
		}
		//
		// if this is the first time we see the constant, create a new
		// LinkedList and add it to the constantsMap
		//
		else {
			LinkedList<RenamedIRIConstant> newTripleList = new LinkedList<RenamedIRIConstant>();
			newTripleList.add(new RenamedIRIConstant(p, querytriple));
			this.constantsMap.put(c, newTripleList);
			Variable v = new Variable(VARIABLE_PREFIX + variableSuffix, true);
			variableSuffix++;
			this.ConstantToVarMap.put(c, v);

		}
	}
	
	class RenamedIRIConstant {
		Pattern p;
		QueryTriple qt;
		
		public RenamedIRIConstant(Pattern p, QueryTriple qt) {
			this.p = p;
			this.qt = qt;
		}
	}

}
