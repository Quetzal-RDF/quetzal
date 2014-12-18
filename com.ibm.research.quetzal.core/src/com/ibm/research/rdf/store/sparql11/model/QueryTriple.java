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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.research.rdf.store.sparql11.model.Path.ToStringVisitor;
import com.ibm.research.rdf.store.sparql11.planner.Planner;
import com.ibm.wala.util.collections.HashMapFactory;

/**
 * models a query triple
 */
public class QueryTriple implements Planner.Key {
	private QueryTripleTerm subject;
	private PropertyTerm predicate;
	private QueryTripleTerm object;
	private BinaryUnion<Variable, IRI> graphRestriction;
	

	
	
	public QueryTriple(QueryTripleTerm s, PropertyTerm p, QueryTripleTerm o) {
		subject = s;
		predicate = p;
		object = o;
	}
	
	public BinaryUnion<Variable, IRI> getGraphRestriction() {
		return graphRestriction;
	}
	
	public void setGraphRestriction(BinaryUnion<Variable, IRI> graphRestriction) {
		this.graphRestriction = graphRestriction;
	}
	
	//
	// Added by Tasos
	//
	public void setSubject(QueryTripleTerm subject)
	   {
	   this.subject = subject;
	   }

	//
   // Added by Tasos
   //
	public void setObject(QueryTripleTerm object)
	   {
	   this.object = object;
	   }
	
	public void setPredicate(PropertyTerm predicate)
	   {
	   this.predicate = predicate;
	   }

	public QueryTripleTerm getSubject() {
		return subject;
	}

	public PropertyTerm getPredicate() {
		return predicate;
	}

	public QueryTripleTerm getObject() {
		return object;
	}
	
	public void expandAndAddTo(SimplePattern sp) {
		List<QueryTriple> subjectTriples = null;
		List<QueryTriple> objectTriples = null;
		
		if (subject.isTriplesNode()) {
			TriplesNode tn = subject.getTriplesNode();
			TriplesNodeExpansion tne = tn.expand();
			subjectTriples = tne.expandedTriples;
			this.subject = tne.node;
		}
		
		if (object.isTriplesNode()) {
			TriplesNode tn = object.getTriplesNode();
			TriplesNodeExpansion tne = tn.expand();
			objectTriples = tne.expandedTriples;
			this.object = tne.node;
		}
		
		if (subjectTriples != null) {
			sp.addQueryTriples(subjectTriples);
		}
		sp.addQueryTriple(this);
		if (objectTriples != null) {
			sp.addQueryTriples(objectTriples);
		}
	}
	
	
	public Set<BlankNodeVariable> gatherBlankNodes() {
		Set<BlankNodeVariable> r = new HashSet<BlankNodeVariable>();
		if(subject.isBlankNode()) r.add(subject.getBlankNode());
		if(predicate.isBlankNode()) r.add(predicate.getBlankNode());
		if(object.isBlankNode()) r.add(object.getBlankNode());
		return r;
	}
	
	public void renamePrefixes(String base, Map<String, String> declared, Map<String, String> internal) {
		subject.renamePrefixes(base, declared, internal);
		predicate.renamePrefixes(base, declared, internal);
		object.renamePrefixes(base, declared, internal);
	}
	
	public void reverse() {
		subject.reverse();
		predicate.reverse();
		object.reverse();
	}

	//
   // added by Tasos
   //
   public Set<IRI> gatherIRIs() {
   Set<IRI> ret = new HashSet<IRI>();
   if(subject.isIRI()) ret.add(subject.getIRI());
   if(predicate.isIRI()) ret.add(predicate.getIRI());
   if(object.isIRI()) ret.add(object.getIRI());
   return ret;
   }

	//
	// added by Tasos
	//
	public Set<Constant> gatherConstants() {
	Set<Constant> ret = new HashSet<Constant>();
	if(subject.isConstant()) ret.add(subject.getConstant());
	if(predicate.isConstant()) ret.add(predicate.getConstant());
	if(object.isConstant()) ret.add(object.getConstant());
	return ret;
	}

	public Set<Variable> gatherVariables() {
		Set<Variable> ret = new HashSet<Variable>();
		if(subject.isVariable()) ret.add(subject.getVariable());
		if(predicate.isVariable()) ret.add(predicate.getVariable());
		if(object.isVariable()) ret.add(object.getVariable());
		return ret;
	}
	
	@Override
	public String toString() { 
		return toString(subject) + " " + toString(predicate) + " " + toString(object);
	}
	
	public String toString(QueryTripleTerm qt) {
		return qt.isIRI()? "<"+qt.getIRI().getValue()+">": (qt.isConstant()? qt.getConstant().toString():qt.toString());
	}
	public String toString(PropertyTerm pt) {
		
		if (pt.isPath()) {
			ToStringVisitor toStringVisitor = new ToStringVisitor() {
				@Override
				protected String toString(IRI iri) {
					return  "<"+iri.getValue()+">";
				}
			};
			pt.getPath().visit(toStringVisitor);
			return toStringVisitor.getString();
		}  else {
			return pt.toString();
		}
			
		
	}
	 @Override
	 public boolean isMandatory() {
		return true;
	  }

	 /**
	  * Determines if 2 query triples are alike, based on any bound constants
	  * @param other
	  * @return
	  */
	 public boolean isSimilarTo(QueryTriple other) {
		 
		 if (other.getSubject().isSimilarTo(subject) && other.getPredicate().isSimilarTo(predicate) 
				 && other.getObject().isSimilarTo(object)) {
			 // check to see if we have variable re-use inside a given triple, and if we do, ensure the mappings are identical across triples	 
			 boolean varReusedCorrectly = true;
			 if (subject.isVariable() && object.isVariable() && subject.getVariable().equals(object.getVariable())) {
				 varReusedCorrectly = varReusedCorrectly && other.getSubject().getVariable().equals(other.getObject().getVariable());
			 }
			 if (subject.isVariable() && predicate.isVariable() && subject.getVariable().equals(predicate.getVariable())) {
				 varReusedCorrectly = varReusedCorrectly && other.getSubject().getVariable().equals(other.getPredicate().getVariable());
			 }

			 if (object.isVariable() && predicate.isVariable() && object.getVariable().equals(predicate.getVariable())) {
				 varReusedCorrectly = varReusedCorrectly && other.getObject().getVariable().equals(other.getPredicate().getVariable());
			 }
			 
			 return varReusedCorrectly;
			 
		 }
		 return false;
		 
	 }
	 
	 public boolean hasSimilarGraphRestrictionTo(QueryTriple other) {
		 if (graphRestriction == null || other.getGraphRestriction() == null) {
			 return graphRestriction==null? 
					 other.getGraphRestriction()==null: 
					 graphRestriction.equals(other.getGraphRestriction());
		 }
		 if (graphRestriction.isSecondType() && other.getGraphRestriction().isSecondType()) {
			 return graphRestriction.getSecond().equals(other.getGraphRestriction().getSecond());
		 }
		 if (graphRestriction.isFirstType() && other.getGraphRestriction().isFirstType()) {
			 return true;
		 }
		 return false;
	 }
	 
	 /**
	  * Determine variable mappings between two query triples.  Note a variable cannot be mapped to multiple variables.  If it is, this is an inconsistent mapping
	  * at a local level that can be dropped
	  * @param other
	  * @return
	  */
	 public Map<Variable, Variable> getVariableMappings(QueryTriple other) {
		 Map<Variable, Variable> ret = HashMapFactory.make();
		 if (subject.isVariable() && other.getSubject().isVariable()) {
			 if (ret.containsKey(subject.getVariable())) {
				 if (!ret.get(subject.getVariable()).equals(other.getSubject().getVariable())) {
					 return null;		
				 }
			 } else {
				 ret.put(subject.getVariable(), other.getSubject().getVariable());
			 }
		 }
		 if (predicate.isVariable() && other.getPredicate().isVariable()) {
			 if (ret.containsKey(predicate.getVariable())) {
				 if (!ret.get(predicate.getVariable()).equals(other.getPredicate().getVariable())) {
					 return null;		
				 }
			 } else {
				 ret.put(predicate.getVariable(), other.getPredicate().getVariable());
			 }
		 }
		 if (object.isVariable() && other.getObject().isVariable()) {
			 if (ret.containsKey(object.getVariable())) {
				 if (!ret.get(object.getVariable()).equals(other.getObject().getVariable())) {
					 return null;		
				 }
			 } else {
				 ret.put(object.getVariable(), other.getObject().getVariable());
			 }
		 }
		 
		 return ret;
	 }
	 
	 /**
	  * Determine if 2 query triples are equivalent, given a set of variable mappings they need to adhere to
	  * @param other
	  * @return
	  */
	 public boolean isEquivalentTo(QueryTriple other, Map<Variable, Variable> mappings) {

		 if (subject.isVariable() && other.getSubject().isVariable()) {
			 assert mappings.containsKey(subject.getVariable());
			 if (mappings.get(subject.getVariable()).equals(other.getSubject().getVariable())) {
				 return true;		 
			 }
		 }
		 
		 if (predicate.isVariable() && other.getPredicate().isVariable()) {
			 assert mappings.containsKey(predicate.getVariable());
			 if (mappings.get(predicate.getVariable()).equals(other.getPredicate().getVariable())) {
				 return true;		 
			 }
		 }
		 
		 if (object.isVariable() && other.getObject().isVariable()) {
			 assert mappings.containsKey(object.getVariable());
			 if (mappings.get(object.getVariable()).equals(other.getObject().getVariable())) {
				 return true;		 
			 }
		 }
		 
		 return false;
	 }
} 
