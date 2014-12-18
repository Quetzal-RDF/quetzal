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

import java.util.List;

public class QueryTriple2 {
	private TriplesNode triplesNode;
	private PropertyList  propertyList;
	
	public QueryTriple2()
	{
		propertyList = new PropertyList();
	}
	
	public QueryTriple2(TriplesNode tn)
	{	
		this();
		triplesNode = tn;
	}
	
	public QueryTriple2(TriplesNode tp, PropertyList pl)
	{
		triplesNode = tp;
		propertyList = pl;
	}
	
	public TriplesNode getTriplesNode()
	{
		return triplesNode;
	}
	
	public PropertyList getPropertyList()
	{
		return propertyList;
	}
	
	public void addPropertyListElement(PropertyListElement ple)
	{
		propertyList.add(ple);
	}
	
	public void expandAndAddTo(SimplePattern sp) 
	{
		QueryTripleTerm subject = null,  object = null;
		PropertyTerm predicate = null;
		List<QueryTriple> subjectTriples = null;
		List<QueryTriple> objectTriples = null;
		
		// expand the subject
		TriplesNodeExpansion tne = triplesNode.expand();
		subjectTriples = tne.expandedTriples;
		subject = tne.node;

		// add subject triples
		if (!subjectTriples.isEmpty()) {
			sp.addQueryTriples(subjectTriples);
			
		} else if (propertyList.isEmpty() && (subject != null) 
				&& (subject.isIRI()|| subject.isVariable())){ // added: (subject.isIRI()|| subject.isVariable())
			// TODO [Property Path]: Property Path implementation: check that indeed it does not make sense to do the following when !(subject.isIRI()|| subject.isVariable())
			subject = new QueryTripleTerm(new BlankNode());
			object = new QueryTripleTerm(new BlankNode());
			PropertyTerm pt = subject.isIRI()? new PropertyTerm(subject.getIRI()): new PropertyTerm(subject.getVariable());
			QueryTriple qt= new QueryTriple(subject, pt, object);
			sp.addQueryTriple(qt);
			return;
		}
		
		// iterate through property list 
		for (PropertyListElement ple : propertyList) {
			predicate = new PropertyTerm(ple.getVerb());
			List<GraphNode> objects = ple.getObjects();
			for (GraphNode o : objects) {
				object = new QueryTripleTerm(o);
				objectTriples = null;
				if (object.isTriplesNode()) {
					TriplesNode tn = object.getTriplesNode();
					TriplesNodeExpansion otne = tn.expand();
					objectTriples = otne.expandedTriples;
					object = otne.node;
				}
				QueryTriple triple = new QueryTriple(subject, predicate, object);
				sp.addQueryTriple(triple);
				if (objectTriples != null) {
					sp.addQueryTriples(objectTriples);
				}
			}
		}
	}
}
