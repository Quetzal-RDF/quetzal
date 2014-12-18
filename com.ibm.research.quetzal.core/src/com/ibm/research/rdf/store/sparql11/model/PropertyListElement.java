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
 /*
* %Z%%W% %I%
* ===========================================================================
* IBM Confidential
* OCO Source Materials
*
* IBM Java(tm)2 SDK, Standard Edition, v 5.0
*
* (C) Copyright IBM Corp. 2011, 2011.
*
* The source code for this program is not published or otherwise divested of
* its trade secrets, irrespective of what has been deposited with the U.S.
* Copyright office.
* ===========================================================================
* Change activity:
*
* Reason 	Date 		Origin 				Description
* --------- --------- 	----------------- 	---------------------------------
* created	Mar 14, 2011		oudrea@us.ibm.com	File created
*
* ===========================================================================
* Module Information:
*
* DESCRIPTION: IBM.WRITEME
* ===========================================================================
*/
package com.ibm.research.rdf.store.sparql11.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author oudrea
 * models a property list element (a verb followed by an object list)
 */
public class PropertyListElement {
	private PropertyTerm property;
	private List<GraphNode> objects;
	
	public PropertyListElement() 
	{ 
		objects = new ArrayList<GraphNode>(); 
	}
	
	public PropertyListElement(PropertyTerm p) {
		property = p;
		objects = new ArrayList<GraphNode>(); 
	}
	public PropertyListElement(BinaryUnion<Variable, Path> p)  {
		this(new PropertyTerm(p));
	}
	
	public PropertyListElement(PropertyTerm p, List<GraphNode> objs) {
		property = p;
		objects = objs;
	}
	
	public PropertyTerm getVerb() { return property; }
	public List<GraphNode> getObjects() { return objects; }
	
	public void setVerb(PropertyTerm p)
	{
		property = p;
	}
	public void setVerb(BinaryUnion<Variable, Path> p)
	{
		setVerb(new PropertyTerm(p));
	}
	
	public void addObject(GraphNode o)
	{
		objects.add(o);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(property.toString() + " ");
		for(int i = 0; i < objects.size(); i++) {
			if(i > 0) sb.append(", ");
			sb.append(objects.get(i).toString());
		}
		return sb.toString();
	}

	public List<QueryTriple> expand(BlankNode root) {
		QueryTripleTerm subj = new QueryTripleTerm(root);
		PropertyTerm pred = new PropertyTerm(property);
		List<QueryTriple> lqt = new ArrayList<QueryTriple>();
		for(GraphNode gn: objects) {
			QueryTripleTerm obj = null;
			if(gn.isGraphTerm()) obj = new QueryTripleTerm(gn.getGraphTerm());
			else if(gn.isVariable()) obj = new QueryTripleTerm(gn.getVariable());
			else if(gn.isTriplesNode()) {
				TriplesNodeExpansion exp = gn.getTriplesNode().expand();
				lqt.addAll(exp.expandedTriples);
				obj = exp.node;
			}
			if(obj != null) lqt.add(new QueryTriple(subj, pred, obj));
		}
		return lqt;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((objects == null) ? 0 : objects.hashCode());
		result = prime * result + ((property == null) ? 0 : property.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PropertyListElement other = (PropertyListElement) obj;
		if (objects == null) {
			if (other.objects != null)
				return false;
		} else if (!objects.equals(other.objects))
			return false;
		if (property == null) {
			if (other.property != null)
				return false;
		} else if (!property.equals(other.property))
			return false;
		return true;
	}
	
}
