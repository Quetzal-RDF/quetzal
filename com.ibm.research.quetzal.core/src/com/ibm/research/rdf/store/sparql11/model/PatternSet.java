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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.research.rdf.store.sparql11.model.Expression.EExpressionType;
import com.ibm.wala.util.collections.HashSetFactory;

/**
 * is a set of patterns (can be an and, a union or a pattern with multiple optionals)
 */
public class PatternSet extends Pattern {
	


	protected List<Pattern> patterns = new ArrayList<Pattern>();
	private boolean topLevel = false;
	private Set<Variable> allVariables;			//Cache all variables for pattern sets-- they may be large
	
	public PatternSet() {super(EPatternSetType.AND); }
	public PatternSet(EPatternSetType t) {
		super(t);
		assert t != null;
	}
	public PatternSet(EPatternSetType t, Collection<? extends Pattern> c) {
		this(t);
		if(c != null) patterns.addAll(c);
	}
	
	public boolean isTopLevel() { return topLevel; }
	public void setTopLevel(boolean t) { topLevel = t; }
	
	public EPatternSetType getType() {
		return type;
	}
	
	public List<Pattern> getPatterns() {
		return patterns;
	}

	public void addPattern(Pattern p) {
		patterns.add(p);
		assert p.getParent() == null;
		p.setParent(this);
	}	

	@Override
	public Set<BlankNodeVariable> gatherBlankNodes() {
		Set<BlankNodeVariable> ret = new HashSet<BlankNodeVariable>();
		for(Pattern p: patterns) ret.addAll(p.gatherBlankNodes());
		return ret;
	}
	
	@Override
	public Set<Variable> gatherVariables() {
		//if (allVariables == null)
		{
			allVariables = new HashSet<Variable>();
			for(Pattern p: patterns) allVariables.addAll(p.gatherVariables());
			if(graphRestriction!=null && graphRestriction.isFirstType()){
				allVariables.add(graphRestriction.getFirst());
			}
		}
		return allVariables;
	}

	public Set<Variable> getVariables() {
		if (filters != null) {
			final Set<Variable> x = HashSetFactory.make();
			for(Expression e : filters) {
				e.traverse(new IExpressionTraversalListener() {
					@Override
					public void startExpression(Expression se) {
						if (!(se.getType().equals(EExpressionType.BUILTIN_FUNC) && ((BuiltinFunctionExpression)se).isExists())) {
							x.addAll(se.getVariables());
						}
					}
					@Override
					public void endExpression(Expression e) {
						// do nothing
					}
				 });
			}
			return x;
		} else {
			return Collections.emptySet();
		}
	}
	
	
	@Override
	public Set<Variable>gatherVariablesWithOptional(){
		Set<Variable> ret = new HashSet<Variable>();
		for(Pattern p: patterns)
		{ 
			if(p instanceof SubSelectPattern){
				ret.addAll(((SubSelectPattern)p).gatherProjectedVariables());
			}
			else{
				ret.addAll(p.gatherVariablesWithOptional());
			}
		}
		if(optionalPatterns!=null)
			for(Pattern p: optionalPatterns) ret.addAll(p.gatherVariablesWithOptional());
		if(graphRestriction!=null && graphRestriction.isFirstType()){
			ret.add(graphRestriction.getFirst());
		}
		return ret;
	}
	
	public Set<Variable> gatherOptionalVariablesWithMultipleBindings(){
		Set<Variable> optionalVariablesWithMultipleBindings=new HashSet<Variable>();
		Set<Variable> allOptionalVars=new HashSet<Variable>();
		Set<Variable> allMainNoOptionalVars=new HashSet<Variable>();
		Set<Variable> allMainOptionalVars=new HashSet<Variable>();
		for(Pattern p : patterns){
			Set<Variable> pOptionalVars=new HashSet<Variable>();
			allMainNoOptionalVars.addAll(p.gatherVariables());
			pOptionalVars.addAll(p.gatherVariablesWithOptional());
			pOptionalVars.removeAll(p.gatherVariables());
			allMainOptionalVars.addAll(pOptionalVars);
		}
		
		if(optionalPatterns!=null){
			for(Pattern p: optionalPatterns){
				for(Variable v: p.gatherVariablesWithOptional()){
					if(allOptionalVars.contains(v)){
						// this variable appears in multiple optionals,
						// can have multiple bindings
						if(!allMainNoOptionalVars.contains(v))
							optionalVariablesWithMultipleBindings.add(v);
						
					}
					else{
						if(allMainOptionalVars.contains(v))
							optionalVariablesWithMultipleBindings.add(v);
					}					
				}
				allOptionalVars.addAll(p.gatherVariablesWithOptional());
			}
		}
		
		for(Pattern p: patterns){
			
				optionalVariablesWithMultipleBindings.addAll(p.gatherOptionalVariablesWithMultipleBindings());
		}
		if(optionalPatterns!=null){
			for(Pattern p: optionalPatterns){
				
					optionalVariablesWithMultipleBindings.addAll(p.gatherOptionalVariablesWithMultipleBindings());
			}
		}
		return optionalVariablesWithMultipleBindings;
	}
	
	
	@Override
	public Set<Variable> gatherIRIBoundVariables(){
		Set<Variable> ret = new HashSet<Variable>();
		Variable graphVar = (graphRestriction!=null && graphRestriction.isFirstType())?graphRestriction.getFirst():null;
		boolean firstPattern=true;
		Set<Variable> commonVariables = new HashSet<Variable>();
		Set<Variable> commonIRIVariables = new HashSet<Variable>();
		Set<Variable> allIRIVariables = new HashSet<Variable>();
		Set<Variable> allVariablesInTransitiveClosure = new HashSet<Variable>();
		
		for(Pattern p: patterns)
		{ 	
			Set<Variable> localVariables = p.gatherVariables();
			Set<Variable> localIRIVars=null;
			if(p instanceof SubSelectPattern){
				localIRIVars= HashSetFactory.make(((SubSelectPattern)p).gatherIRIBoundProjectedVariables());				
			}
			else{
				localIRIVars= HashSetFactory.make(p.gatherIRIBoundVariables());		
			}
			
			if(graphVar!=null && graphVar instanceof Variable)localIRIVars.add(graphVar);
			
			if(firstPattern){
				commonVariables.addAll(localVariables);
				commonIRIVariables.addAll(localIRIVars);
				firstPattern = false;
			}
			else{
				commonVariables.retainAll(localVariables);
				commonIRIVariables.retainAll(localIRIVars);
			}
			allIRIVariables.addAll(localIRIVars);
		}
		
		if(type==EPatternSetType.AND){
			ret.addAll(allIRIVariables);
		}
		else{			
			// for UNION or MINUS
			for(Pattern p: patterns)
			{ 	
				Set<Variable> localIRIVars=null;
				if(p instanceof SubSelectPattern){
					localIRIVars=((SubSelectPattern)p).gatherIRIBoundProjectedVariables();				
				}
				else{
					localIRIVars=p.gatherIRIBoundVariables();		
				}
				if(graphVar!=null)localIRIVars.add(graphVar);
				for(Variable v : localIRIVars){
					if(!commonVariables.contains(v))ret.add(v);
					if(commonIRIVariables.contains(v))ret.add(v);
				}
			}
		}
		//Optional patterns can only appear in AND patternSet
		if(optionalPatterns!=null){
			for(Pattern p: optionalPatterns) {
				Set<Variable> localIRIVars=p.gatherIRIBoundVariables();
				// Rule opt1 : if optional IRI bound var is not in the main pattern add it to ret list
				Set<Variable> mainPatternVariables = gatherVariables();
				for(Variable v : localIRIVars){
					if(!mainPatternVariables.contains(v)) ret.add(v);
				}

				/*// Rule opt2 : if v in optional pattern and not IRI bound remove v from ret
				Set<Variable> toRemove = new HashSet<Variable>();
				for(Variable v : ret){
					if(!localIRIVars.contains(v) && p.gatherVariables().contains(v))
						toRemove.add(v);
				}				
				ret.removeAll(toRemove);*/
			}			
		}	
		
		// if variables are involved in a property path with transitivity, they should always have their types projected
		// so ignore optimization even if its known that they are variable bound
		ret.removeAll(gatherVariablesInTransitiveClosure());
		return ret;
	}

	
	
		
	@Override
	public int getNumberTriples() {
		int nrTriples=0;
		for(Pattern p: patterns) nrTriples+=p.getNumberTriples();
		if (optionalPatterns != null) {
			for(Pattern p: optionalPatterns) nrTriples+=p.getNumberTriples();
		}		return nrTriples;
	}
	
	@Override
	public void renamePrefixes(String base, Map<String, String> declared,
			Map<String, String> internal) {
		super.renamePrefixes(base, declared, internal);
		for(Pattern p: patterns) p.renamePrefixes(base, declared, internal);
		if (optionalPatterns != null) {
			for(Pattern p: optionalPatterns) p.renamePrefixes(base, declared, internal);
		}
	}
	
	@Override
	public void replaceFilterBindings() {
		for(Pattern p: patterns) p.replaceFilterBindings();
		if (optionalPatterns != null) {
			for(Pattern p: optionalPatterns) p.replaceFilterBindings();
		}
	}

	
	@Override
	public Set<Pattern> gatherSubPatterns(boolean includeOptionals) {
		Set<Pattern> ret = new HashSet<Pattern>();
		for(Pattern p: patterns) {
			ret.addAll(p.gatherSubPatterns(includeOptionals));
		}
		if (includeOptionals && optionalPatterns != null) {
			for(Pattern o: optionalPatterns) { 
				ret.addAll(o.gatherSubPatterns(includeOptionals)); 
			}
		}
		ret.add(this);
		return ret;
	}

	@Override
	public Set<Pattern> gatherSubPatternsExcluding(Pattern except, boolean includeOptionals) {
		if (! except.equals(this)) {
			Set<Pattern> ret = new HashSet<Pattern>();
			for(Pattern p: patterns) {
				ret.addAll(p.gatherSubPatternsExcluding(except, includeOptionals));
			}
			if (includeOptionals && optionalPatterns != null) {
				for(Pattern o: optionalPatterns) { 
					ret.addAll(o.gatherSubPatternsExcluding(except, includeOptionals)); 
				}
			}
			ret.add(this);
			return ret;
		} else {
			return Collections.emptySet();
		}
	}

	@Override
	public Set<Pattern> getSubPatterns(boolean includeOptionals) {
		Set<Pattern> ret = new HashSet<Pattern>();
		for(Pattern p: patterns) {
			ret.add(p);
		}
		if (includeOptionals && optionalPatterns != null) {
			for(Pattern o: optionalPatterns) { 
				ret.add(o); 
			}
		}
		return ret;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String sep = "";
		switch(type) {
		case AND: sep = " . "; break;
		case MINUS: sep = " MINUS "; break;
		case OPTIONAL: sep = " OPTIONAL "; break;
		case UNION: sep = " UNION "; break;
		case EXISTS: sep = " FILTER EXISTS "; break;
		case NOT_EXISTS: sep = " FILTER NOT EXISTS "; break;
		}
		//if (!topLevel) sb.append(sep);
		if(getGraphRestriction() != null) {
			sb.append("{\n");
			if (getGraphRestriction().isFirstType()) { 
				sb.append("GRAPH "+getGraphRestriction());
			} else {
				sb.append("GRAPH <"+getGraphRestriction()+">");
			}
		}
		sb.append("\n");
		sb.append("{");
		for(int i = 0; i < patterns.size(); i++) {
			if(i > 0) sb.append("\n" + sep + " ");
			Pattern p = patterns.get(i);
			//if (p!=null) 
			if (patterns.size()>1) {
				sb.append("{\n");
			}
			sb.append(p.toString());
			if (patterns.size()>1) {
				sb.append("}\n");
			}
		}
		if((getFilters() != null) && (getFilters().size() > 0)) {
			sb.append("\n");
			for(int i = 0; i < getFilters().size(); i++)  {
				sb.append("FILTER ");
				sb.append("("+getFilters().get(i) + ")\n");
				
			}
		}
		// optionals
		if (optionalPatterns != null) {
			for (Pattern p : optionalPatterns) {
				sb.append("\nOPTIONAL {\n"+p.toString()+"}");
			}
		}
		sb.append("\n}");
		if(getGraphRestriction() != null) {
			sb.append("}\n");
		}
			
		return sb.toString();
	}
	
	/*
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((patterns == null) ? 0 : patterns.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		PatternSet other = (PatternSet) obj;
		if (patterns == null) {
			if (other.patterns != null)
				return false;
		} else if (!patterns.equals(other.patterns))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	*/
	
	public boolean isEmpty()
		{
		//
		// If the pattern has optionals, then it's not empty (no mattern its main pattern)
		//
		if (optionalPatterns != null && !optionalPatterns.isEmpty())
			{
			return false;
			}

		//
		// If the pattern has no optionals and no main pattern, then it's empty
		//
		if (patterns == null || patterns.size() == 0)
			{
			return true;
			}
		else
			{
			if (getType() == EPatternSetType.AND)
				{
				for (Pattern p : patterns)
					{
					if (!p.isEmpty())
						{
						return false;
						}
					}
				return true;
				}
			if (getType() == EPatternSetType.UNION)
				{
				for (Pattern p : patterns)
					{
					if (!p.isEmpty())
						{
						return false;
						}
					}

				return true;
				}
			}

		return false;
		}
	
	public boolean removePattern(Pattern p) {
		boolean ret = patterns.remove(p);
		if (ret) {
			p.setParent(null);
		}
		return ret;
	}
	
	public boolean removeFilter(Expression e) {
		return filters.remove(e);
	}
	
	public void pushGraphRestrictions() {
		pushGraphRestrictions(patterns);
		super.pushGraphRestrictions();
	}
	
	@Override
	public void reverse() {
		// do nothing
		
	}
	@Override
	public Set<Variable> gatherVariablesInTransitiveClosure() {
		Set<Variable> vars = HashSetFactory.make();
		for (Pattern p : patterns) {
			vars.addAll(p.gatherVariablesInTransitiveClosure());
		}
		return vars;
	}
}
