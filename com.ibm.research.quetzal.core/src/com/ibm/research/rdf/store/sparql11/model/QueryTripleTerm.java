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

import java.util.Map;

import com.ibm.research.rdf.store.sparql11.model.Expression.EConstantType;

/**
 * models a term in a query triple
 */
public class QueryTripleTerm {
	private FiveUnion<IRI, Constant, Variable, BlankNode, TriplesNode> value;
	
	public QueryTripleTerm(IRI i) {
		value = new FiveUnion<IRI, Constant, Variable, BlankNode, TriplesNode>();
		value.setFirst(i);
	}
	public QueryTripleTerm(Constant c) {
		value = new FiveUnion<IRI, Constant, Variable, BlankNode, TriplesNode>();
		value.setSecond(c);
	}
	public QueryTripleTerm(Variable v) {
		value = new FiveUnion<IRI, Constant, Variable, BlankNode, TriplesNode>();
		value.setThird(v);
	}
	public QueryTripleTerm(BlankNode bn) {
		value = new FiveUnion<IRI, Constant, Variable, BlankNode, TriplesNode>();
		value.setThird(new BlankNodeVariable(bn.getLabel()));
		/*
		value = new FourUnion<IRI, Constant, Variable, BlankNode>();
		value.setFourth(bn);
		*/
	}	
	public QueryTripleTerm(TriplesNode tn) {
		value = new FiveUnion<IRI, Constant, Variable, BlankNode, TriplesNode>();
		value.setFifth(tn);
	}
	
	public QueryTripleTerm(GraphTerm gt) {
		value = new FiveUnion<IRI, Constant, Variable, BlankNode, TriplesNode>();
		if(gt.isConstant()) value.setSecond(gt.getConstant());
		else if(gt.isBlankNode()) value.setThird(new BlankNodeVariable(gt.getBlankNode().getLabel()));
		/*else if(gt.isBlankNode()) value.setFourth(gt.getBlankNode());*/
		else if(gt.isIRI()) value.setFirst(gt.getIRI());
	}
	public QueryTripleTerm(GraphNode gn) {
		value = new FiveUnion<IRI, Constant, Variable, BlankNode, TriplesNode>();
		if(gn.isVariable()) value.setThird(gn.getVariable());
		else if (gn.isGraphTerm()) {
			GraphTerm gt = gn.getGraphTerm();
			if(gt.isConstant()) value.setSecond(gt.getConstant());
			else if(gt.isBlankNode()) value.setThird(new BlankNodeVariable(gt.getBlankNode().getLabel()));
			/*else if(gt.isBlankNode()) value.setFourth(gt.getBlankNode());*/
			else if(gt.isIRI()) value.setFirst(gt.getIRI());
		} else if (gn.isTriplesNode()) {
			value.setFifth(gn.getTriplesNode());
		}
	}
	public QueryTripleTerm(BinaryUnion<Variable, IRI> verb) {
		value = new FiveUnion<IRI, Constant, Variable, BlankNode, TriplesNode>();
		if(verb.isFirstType()) value.setThird(verb.getFirst());
		else if(verb.isSecondType()) value.setFirst(verb.getSecond());
	}
	
	public boolean isIRI() { return value.isFirstType(); }
	public boolean isConstant() { return value.isSecondType(); }
	public boolean isVariable() { return value.isThirdType(); }
	public boolean isBlankNode() { return value.isThirdType() && value.getThird() instanceof BlankNodeVariable; }
	public boolean isTriplesNode() { return value.isFifthType(); }
	
	public IRI getIRI() { return value.getFirst(); }
	public Constant getConstant() { return value.getSecond(); }
	public Variable getVariable() { return value.getThird(); }
	public BlankNodeVariable getBlankNode() { return (BlankNodeVariable) value.getThird(); }
	public TriplesNode getTriplesNode() { return value.getFifth(); }
	
	public  void renamePrefixes(String base, Map<String, String> declared, Map<String, String> internal) {
		if(isIRI()) {
			getIRI().rename(base, declared, internal);
		} else if (isConstant()) {
			if (value.getSecond().getConstType() == EConstantType.LITERAL) {
				IRI type = value.getSecond().getLiteral().getType();
				if (type != null) {
					type.rename(base, declared, internal);
				}	
			}
		}
	}
	
	public boolean isSimilarTo(QueryTripleTerm t) {
		// KAVITHA:  Am assuming expansion has been applied to TriplesNode
		if (t.isIRI() && this.isIRI()) {
			return t.getIRI().equals(this.getIRI());
		} else if (t.isConstant() && this.isConstant()) {
			return t.getConstant().equals(this.getConstant());
		} else if (t.isVariable() && this.isVariable()) {
			return true;
		} else if (t.isBlankNode() && this.isBlankNode()) {
			return true;
		}
		return false;
	}
	public void reverse() {
		if(isIRI()) {
			getIRI().reverse();
		} 
	}
	
	public String toSqlDataString() {
		if(isIRI()) {
			return getIRI().getSqlDataString();
		}
		if(isConstant()){
			return getConstant().toSqlDataString();
		}
		return null;
	}
	
	public String toString() { return value.toString(); }
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		QueryTripleTerm other = (QueryTripleTerm) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
}
