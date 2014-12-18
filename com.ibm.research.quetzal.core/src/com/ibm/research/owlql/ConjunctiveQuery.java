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
package com.ibm.research.owlql;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;

/**
 * A conjunctive query
 * @author achille
 *
 */
public class ConjunctiveQuery {
	
	private Query query;
	public ConjunctiveQuery() {
		query = new Query();
		query.setQuerySelectType();
	}
	public ConjunctiveQuery(Query q) {
		if (!q.isSelectType()) {
			throw new RuntimeException("A conjunctive query can only be created from a select query: "+q.serialize(Syntax.syntaxSPARQL_11));
		}
		query = q;
		setQueryPattern(q.getQueryPattern());
	}
	
	public boolean isDistinct() {
		return query.isDistinct();
	}
	public void setDistinct(boolean distinct) {
		 query.setDistinct(distinct);
	}
	
	public Query getQuery() {
		return query;
	}
	
	
	protected List<Element> flatten(Element g) {
		List<Element> ret = new ArrayList<Element>();
		Stack<Element> stack = new Stack<Element>();
		stack.push(g);
		while (!stack.isEmpty()) {
			Element e = stack.pop();
			if (e instanceof ElementGroup) {
				ElementGroup eg = (ElementGroup) e;
				for (Element ne:eg.getElements()) {
					stack.push(ne);
				}
			} else {
				ret.add(e);
			}
		}
		return ret;
		
	}
	
	public ConjunctiveQuery cloneConjQuery(boolean copyResultVars) {
		//return new ConjunctiveQuery(query.cloneQuery());
		
		ConjunctiveQuery ret = new ConjunctiveQuery();
		ElementTriplesBlock pattern = new ElementTriplesBlock();
		for (Triple t: getTriples()) {
			pattern.addTriple(new Triple(t.getSubject(), t.getPredicate(), t.getObject()));
		}
		ret.setQueryPattern(pattern);
		for (ElementFilter f: getFilters()) {
			ret.addFilter(f.getExpr());
		}
		if (copyResultVars) {
			for (String v: getResultVars()) {
				ret.query.addResultVar(v);
			}
		}
		return ret;
	}
	public Set<String> getAllVariables() {
		Set<String> ret = new HashSet<String>();
		for (Triple t: getTriples()) {
			if (t.getSubject().isVariable()) {
				ret.add(t.getSubject().getName());
			}
			if (t.getPredicate().isVariable()) {
				ret.add(t.getPredicate().getName());
			}
			if (t.getObject().isVariable()) {
				ret.add(t.getObject().getName());
			}
		}
		ret.addAll(getResultVars());
		return ret;
	}
	public ConjunctiveQuery cloneConjQuery() {
		return cloneConjQuery(true);
	}
	public void setQueryPattern(Element graphPattern) {
		if (graphPattern instanceof ElementTriplesBlock) {
			query.setQueryPattern((ElementTriplesBlock)graphPattern);
		} else if (graphPattern instanceof ElementGroup) {
			ElementGroup group = (ElementGroup) graphPattern;
			for (Element e: flatten(group)) {
				if (e instanceof ElementPathBlock) {
					ElementPathBlock epb = (ElementPathBlock) e;
					for (TriplePath p:epb.getPattern().getList()) {
						if (!p.isTriple()) {
							throw new IllegalArgumentException("Conjunctive query only accepts a basic graph patterns. No triple path:  "+ p+"\n"+graphPattern );	
						}
					}
				} else 	if (!(e instanceof ElementTriplesBlock)
				&&  !(e instanceof ElementFilter)) {
					throw new IllegalArgumentException("Conjunctive query only accepts a basic graph patterns: "+ e+"\n"+graphPattern );	
				}
			}
			query.setQueryPattern(graphPattern);
		}
		else {
			throw new IllegalArgumentException("Conjunctive query only accepts a basic graph patterns: "+ graphPattern);				
		}
		
	}
	public void addTriple(Triple t) {
		if (query.getQueryPattern() instanceof ElementTriplesBlock) {
			((ElementTriplesBlock)query.getQueryPattern()).addTriple(t);
		} else { 
			assert query.getQueryPattern() instanceof ElementGroup: query.getQueryPattern();
			ElementGroup group = (ElementGroup) query.getQueryPattern();
			group.addTriplePattern(t);
		}
			
	}
	
	public List<String> getResultVars() {
		return query.getResultVars();
	}
	public void addResultVar(String var) {
		query.addResultVar(var);
	}
	
	public List<Triple> getTriples() {
		if (query.getQueryPattern() instanceof ElementTriplesBlock) {
			ElementTriplesBlock  sp = (ElementTriplesBlock) query.getQueryPattern();
			return sp.getPattern().getList();
		} else {
			assert query.getQueryPattern() instanceof ElementGroup : query.getQueryPattern() ;
			List<Element> es = flatten(query.getQueryPattern());
			List<Triple> ret = new ArrayList<Triple>();
			for (Element e: es) {
				if (e instanceof ElementTriplesBlock) {
					ret.addAll(((ElementTriplesBlock) e).getPattern().getList());
				} else if (e instanceof ElementPathBlock ) {
					ElementPathBlock epb = (ElementPathBlock) e;
					for (TriplePath tp: epb.getPattern().getList()) {
						assert tp.isTriple() : tp;
						ret.add(tp.asTriple());
					}
				}
			}
			return ret;
		}
	}
	
	public List<ElementFilter>  getFilters() {
		if (query.getQueryPattern() instanceof ElementTriplesBlock) {
			return new ArrayList<ElementFilter>();
		} else {
			assert query.getQueryPattern() instanceof ElementGroup;
			List<Element> es = flatten(query.getQueryPattern());
			List<ElementFilter> ret = new ArrayList<ElementFilter>();
			for (Element e: es) {
				if (e instanceof ElementFilter) {
					ret.add((ElementFilter) e);
				}
			}
			return ret;
		}
	}
	
	public void addFilter(Expr expr) {
		addFilter(new ElementFilter(expr));
	}
	
	
	
	public void addFilter(ElementFilter filter) {
		if (query.getQueryPattern() instanceof ElementTriplesBlock) {
			ElementGroup group = new ElementGroup();
			group.addElement(query.getQueryPattern());
			group.addElementFilter(filter);
			query.setQueryPattern(group);
		} else {
			assert query.getQueryPattern() instanceof ElementGroup;
			ElementGroup group = (ElementGroup) query.getQueryPattern();
			group.addElementFilter(filter);
		}
	}
	public void add(Element elt) {
		if (query.getQueryPattern() instanceof ElementTriplesBlock) {
			ElementGroup group = new ElementGroup();
			group.addElement(query.getQueryPattern());
			group.addElement(elt);
			query.setQueryPattern(group);
		} else {
			assert query.getQueryPattern() instanceof ElementGroup;
			ElementGroup group = (ElementGroup) query.getQueryPattern();
			group.addElement(elt);
		}
	}
	
	public String toString() {
		return query.serialize(Syntax.syntaxSPARQL_11);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getResultVars().hashCode();
		result = prime * result + new HashSet<Triple>(getTriples()).hashCode();
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
		ConjunctiveQuery other = (ConjunctiveQuery) obj;
		if (!getResultVars().equals(other.getResultVars())) {
			return false;
		}
		if (!new HashSet<Triple>(getTriples()).equals(new HashSet<Triple>(other.getTriples()))) {
			return false;
		}
		if (!new HashSet<ElementFilter>(getFilters()).equals(new HashSet<ElementFilter>(other.getFilters()))) {
			return false;
		}	
		return true;
	}
	// delegate
	
	
	
}
