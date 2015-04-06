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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.research.rdf.store.sparql11.model.Expression.ERelationalOp;
import com.ibm.wala.util.collections.HashSetFactory;

/**
 * @author oudrea
 * represents a pattern consisting of a list of triples
 */
public class SimplePattern extends Pattern {
	
	private List<QueryTriple> queryTriples = new ArrayList<QueryTriple>();
	//private List<SelectQuery> subSelects = new ArrayList<SelectQuery>();



	public SimplePattern() { super(EPatternSetType.SIMPLE); }
	
	public List<QueryTriple> getQueryTriples() {
		return queryTriples;
	}
	
	public void addQueryTriple(QueryTriple q) {
		queryTriples.add(q);
	}
	
	public void addQueryTriples(List<QueryTriple> lt) {
		queryTriples.addAll(lt);
	}
	
	/*public List<SelectQuery> getSubSelect() {
		return subSelects;
	}
	
	public void addSubSelect(SelectQuery sq) {
		subSelects.add(sq);
	}*/
	
	@Override
	public void pushGraphRestrictions() {
		// TODO Auto-generated method stub
		super.pushGraphRestrictions();
		for (QueryTriple q : queryTriples) {
			q.setGraphRestriction(graphRestriction);
		}
	}
	//
   // added by Tasos
   //
   public Set<IRI> gatherIRIs() {
   Set<IRI> ret = new HashSet<IRI>();
   for(QueryTriple qt: queryTriples) ret.addAll(qt.gatherIRIs());
   return ret;
   }

	//
   // added by Tasos
   //
	public Set<Constant> gatherConstants() {
	Set<Constant> ret = new HashSet<Constant>();
	for(QueryTriple qt: queryTriples) ret.addAll(qt.gatherConstants());
	return ret;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		//if(getGraphRestriction()!= null) sb.append(getGraphRestriction());
		if(getGraphRestriction() != null 
		&& (parent == null || parent.getGraphRestriction() ==null)) {
			sb.append("{\n");
			if (getGraphRestriction().isFirstType()) { 
				sb.append("GRAPH "+getGraphRestriction());
			} else {
				sb.append("GRAPH <"+getGraphRestriction()+">");
			}
			sb.append("\n");
			sb.append("{");
		}
		sb.append("\n");
		for(int i = 0; i < queryTriples.size(); i++) {
			if(i > 0) sb.append(" .\n");
			sb.append(queryTriples.get(i).toString());
		}
		//sb.append(" .\n");
		if((getFilters() != null) && (getFilters().size() > 0)) {
			sb.append("\n");
			for(int i = 0; i < getFilters().size(); i++) {
				sb.append("FILTER ");
				sb.append("("+getFilters().get(i) + ")\n");
			}
		}
		/*for(int i = 0; i < subSelects.size(); i++) {
			if(i > 0) sb.append(" .\n");
			sb.append(subSelects.get(i).toString());
		}*/
		sb.append("\n");
		// optionals
		if (optionalPatterns != null) {
			for (Pattern p : optionalPatterns) {
				sb.append("\nOPTIONAL {\n"+p.toString()+"}");
			}
		}
		if(getGraphRestriction() != null 
		&& (parent == null || parent.getGraphRestriction() ==null)) {
			sb.append("\n}}");
			
		}
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see com.ibm.research.rdf.store.sparql11.model.Pattern#gatherBlankNodes()
	 */
	@Override
	public Set<BlankNodeVariable> gatherBlankNodes() {
		Set<BlankNodeVariable> bn = new HashSet<BlankNodeVariable>();
		for(QueryTriple qt: queryTriples) bn.addAll(qt.gatherBlankNodes());
		return bn;
	}

	@Override
	public Set<Variable> getVariables() {
		return gatherVariables();
	}
	
	/* (non-Javadoc)
	 * @see com.ibm.research.rdf.store.sparql11.model.Pattern#gatherVariables()
	 */
	@Override
	public Set<Variable> gatherVariables() {
		Set<Variable> ret = new HashSet<Variable>();
		for(QueryTriple qt: queryTriples) ret.addAll(qt.gatherVariables());
//		if(graphRestriction!=null && graphRestriction.isFirstType()){
//			ret.add(graphRestriction.getFirst());
//		}
		return ret;
	}
	
	@Override
	public Set<Variable>gatherVariablesWithOptional(){
		Set<Variable> ret = new HashSet<Variable>();
		for(QueryTriple qt: queryTriples) ret.addAll(qt.gatherVariables());

		if(graphRestriction!=null && graphRestriction.isFirstType()){
			ret.add(graphRestriction.getFirst());
		}
		return ret;
		//return gatherVariables();
	}
	
	@Override
	public Set<Variable> gatherIRIBoundVariables(){
		Set<Variable> ret = new HashSet<Variable>();
		for(QueryTriple qt: queryTriples) {
			if (qt.getPredicate().isIRI() || qt.getPredicate().isVariable()) {
				if(qt.getSubject().isVariable())ret.add(qt.getSubject().getVariable());
				if(qt.getPredicate().isVariable())ret.add(qt.getPredicate().getVariable());
				if(graphRestriction!=null && graphRestriction.isFirstType())ret.add(graphRestriction.getFirst());
			} else  {
				assert qt.getPredicate().isPath() : qt.getPredicate();
				Path path = qt.getPredicate().getPath();
				if (path.isDirectlyRecursive() || path.isDirectlyZeroOrOnePath()) {
					//do nothing
					//TODO: This is a very conservative analysis.
					// We can do better by analysis the content of the sub-path
				} else {
					throw new RuntimeException(qt+" has not been properly rewritten to get rid of the property path");
				}
				
			}
		}
		return ret;
	}  
	
	@Override
	public int getNumberTriples() {
		int nrTriples = queryTriples.size();
		if (optionalPatterns != null) {
			for(Pattern p: optionalPatterns) nrTriples+=p.getNumberTriples();
		}
		return nrTriples;

	}
	
	@Override
	public void renamePrefixes(String base, Map<String, String> declared,
			Map<String, String> internal) {
		super.renamePrefixes(base, declared, internal);
		for(QueryTriple qt: queryTriples) qt.renamePrefixes(base, declared, internal);
	}
	
	@Override
	public void replaceFilterBindings() {
		for(QueryTriple qt: queryTriples) {
			if(qt.getPredicate().isVariable()){
				for(Expression e: filters){
					if(e instanceof RelationalExpression){
						RelationalExpression rel = (RelationalExpression) e;
						if (rel.getOperator().equals(ERelationalOp.EQUAL)) {
							if( ( ((RelationalExpression) e).getLeft() instanceof VariableExpression &&
								((RelationalExpression) e).getRight() instanceof ConstantExpression) ){
								VariableExpression ve=(VariableExpression)((RelationalExpression) e).getLeft();
								Variable v=new Variable(ve.getVariable());	
								ConstantExpression ce=(ConstantExpression)((RelationalExpression) e).getRight();
								if(v.equals(qt.getPredicate().getVariable())){
									qt.setPredicate(new PropertyTerm(ce.getConstant().getIRI()));
									filters.remove(e);
									break;
								}
									
							}
						}
										
					}		
				}
			}
		}//end for
	}
	


	@Override
	public void reverse() {
		for(QueryTriple qt: queryTriples) qt.reverse();
	}

	
	@Override
	public Set<Pattern> gatherSubPatterns(boolean includeOptionals) {
		Set<Pattern> ret = new HashSet<Pattern>();
		ret.add(this);
		if (includeOptionals && optionalPatterns != null) {
			for(Pattern o: optionalPatterns) { 
				ret.addAll(o.gatherSubPatterns(includeOptionals)); 
			}
		}
		return ret;
	}

	@Override
	public Set<Pattern> gatherSubPatternsExcluding(Pattern except, boolean includeOptionals) {
		Set<Pattern> ret = new HashSet<Pattern>();
		if (!except.equals(this)) {
			ret.add(this);
		}
		if (includeOptionals && optionalPatterns != null) {
			for(Pattern o: optionalPatterns) { 
				ret.addAll(o.gatherSubPatternsExcluding(except, includeOptionals)); 
			}
		}
		return ret;
	}

	@Override
	public Set<Pattern> getSubPatterns(boolean includeOptionals) {
		Set<Pattern> ret = new HashSet<Pattern>();
		ret.add(this);
		if (includeOptionals && optionalPatterns != null) {
			for(Pattern o: optionalPatterns) { 
				ret.add(o); 
			}
		}
		return ret;
	}
	
	
	/*
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((queryTriples == null) ? 0 : queryTriples.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimplePattern other = (SimplePattern) obj;
		if (queryTriples == null) {
			if (other.queryTriples != null)
				return false;
		} else if (!queryTriples.equals(other.queryTriples))
			return false;
		return true;
	}
	*/
	
	public boolean isEmpty(){
		if(queryTriples==null ||queryTriples.size()==0)return true;
		else return false;
	
	}

	public Set<Variable> gatherOptionalVariablesWithMultipleBindings(){
		return new HashSet<Variable>();
	}

	@Override
	public Set<Variable> gatherVariablesInTransitiveClosure() {
		Set<Variable> vars = HashSetFactory.make();
		for (QueryTriple q: queryTriples) {
			if (q.getPredicate().isComplexPath() && q.getPredicate().getPath().isRecursive()) {
				if (q.getSubject().isVariable()) {
					vars.add(q.getSubject().getVariable());
				}
				if (q.getObject().isVariable()) {
					vars.add(q.getObject().getVariable());
				}
			}
		}
		return vars;
	}
	
}
