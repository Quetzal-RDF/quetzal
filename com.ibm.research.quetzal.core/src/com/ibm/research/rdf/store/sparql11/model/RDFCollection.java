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
 package com.ibm.research.rdf.store.sparql11.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author oudrea
 * models an RDF collection.
 */
public class RDFCollection extends ArrayList<GraphNode>{

	public static IRI IRI_FIRST = new IRI("http://www.w3.org/1999/02/22-rdf-syntax-ns#first");
	public static IRI IRI_REST = new IRI("http://www.w3.org/1999/02/22-rdf-syntax-ns#rest");
	
	private static final long serialVersionUID = 5835285815021792094L;

	public RDFCollection() {
		super();
	}

	public RDFCollection(Collection<? extends GraphNode> collection) {
		super(collection);
	}

	public RDFCollection(int capacity) {
		super(capacity);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < size(); i++) {
			if(i > 0) sb.append(" ");
			sb.append(get(i).toString());
		}
		return sb.toString();
	}
	
	public TriplesNodeExpansion expand() {
		return expand(this);
	}
	
	protected TriplesNodeExpansion expand(List<GraphNode> l) {
		if(l.size() == 0) return null;
		QueryTripleTerm firstTerm = null;
		List<QueryTriple> addition = null;
		GraphNode gn = l.get(0);
		if(gn.isGraphTerm()) firstTerm = new QueryTripleTerm(gn.getGraphTerm());
		else if(gn.isVariable()) firstTerm = new QueryTripleTerm(gn.getVariable());
		else if(gn.isTriplesNode()) {
			TriplesNodeExpansion tne = gn.getTriplesNode().expand();
			firstTerm = tne.node;
			addition = tne.expandedTriples;
		}
		if(l.size() > 1) {
			TriplesNodeExpansion exp = expand(l.subList(1, l.size()));
			BlankNode bn = new BlankNode();
			QueryTripleTerm qt = new QueryTripleTerm(bn);
			exp.expandedTriples.add(new QueryTriple(qt, new PropertyTerm(IRI_FIRST), firstTerm));
			exp.expandedTriples.add(new QueryTriple(qt, new PropertyTerm(IRI_REST), exp.node));
			exp.node = qt;
			return exp;
		} else {
			TriplesNodeExpansion ret = new TriplesNodeExpansion();
			BlankNode bn = new BlankNode();
			QueryTripleTerm qt = new QueryTripleTerm(bn);
			ret.node = qt;
			ret.expandedTriples = new ArrayList<QueryTriple>();
			ret.expandedTriples.add(new QueryTriple(qt, new PropertyTerm(IRI_FIRST), firstTerm));
			ret.expandedTriples.add(new QueryTriple(qt, new PropertyTerm(IRI_REST), new QueryTripleTerm(new IRI("http://www.w3.org/1999/02/22-rdf-syntax-ns#nil"))));
			if  (addition != null) {
				ret.expandedTriples.addAll(addition);
			}
			return ret;
		}
	}

}
