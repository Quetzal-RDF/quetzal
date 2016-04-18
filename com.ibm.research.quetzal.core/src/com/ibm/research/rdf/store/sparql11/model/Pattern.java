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
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.research.rdf.store.sparql11.model.Expression.EBuiltinType;
import com.ibm.research.rdf.store.sparql11.model.Expression.EExpressionType;
import com.ibm.research.rdf.store.sparql11.model.Expression.ERelationalOp;
import com.ibm.research.rdf.store.sparql11.planner.Planner;
import com.ibm.research.rdf.store.sparql11.planner.statistics.PatternUtils;
import com.ibm.wala.util.collections.HashMapFactory;
import com.ibm.wala.util.collections.HashSetFactory;
import com.ibm.wala.util.collections.Iterator2Collection;
import com.ibm.wala.util.collections.MapIterator;
import com.ibm.wala.util.functions.Function;

/**
 *  Abstract graph pattern. Any pattern will have an optional
 *         graph restriction and a number of filters.
 */
public abstract class Pattern implements Planner.Key {

	public static enum EPatternSetType {
		UNION, AND, OPTIONAL, SIMPLE, SUBSELECT, PRODUCT, EXISTS, NOT_EXISTS, MINUS, BIND, BINDFUNC, GRAPH, VALUES, SERVICE
	};

	protected BinaryUnion<Variable, IRI> graphRestriction = null;
	protected List<Expression> filters = new ArrayList<Expression>();
	protected List<Pattern> optionalPatterns;

	protected HierarchicalIdentifier ident = null;
	protected final EPatternSetType type;
	protected Pattern parent = null;

	protected Map<Variable, Set<Constant>> filterBindings;
	protected Set<Expression> pushedFilters = new HashSet<Expression>();

	protected Pattern(EPatternSetType type) {
		this.type = type;
	}
	
	public Set<Expression> getPushedFilters() {
		return pushedFilters;
	}

	// ------------------------------ STANDARD GETTERS AND SETTERS
	// -----------------------------------
	public BinaryUnion<Variable, IRI> getGraphRestriction() {
		return graphRestriction;
	}

	public void setGraphRestriction(BinaryUnion<Variable, IRI> graphRestriction) {
		this.graphRestriction = graphRestriction;
	}

	public List<Expression> getFilters() {
		return filters;
	}

	public void addFilter(Expression e) {
		if (e.getType().equals(EExpressionType.AND)) {
			for (Expression alt : ((LogicalExpression)e).getComponents()) {
				addFilter(alt);
			}
		} else {
			filters.add(e);
		}
	}

	public HierarchicalIdentifier getHierarchicalIdentifier() {
		return ident;
	}

	public EPatternSetType getType() {
		return type;
	}

	public List<Pattern> getOptionalPatterns() {
		return optionalPatterns;
	}

	public void addOptional(Pattern optional) {
		if (optionalPatterns == null) {
			optionalPatterns = new LinkedList<Pattern>();
		}
		optional.setParent(this);
		optionalPatterns.add(optional);
	}

	public Pattern getParent() {
		return parent;
	}

	public void setParent(Pattern parent) {
		this.parent = parent;
	}

	public abstract Collection<? extends Variable> getVariables();


	// ------------------------------ END: STANDARD GETTERS AND SETTERS
	// ------------------------------

	public Pattern handleEmptyMain() {
		if (isEmpty() && optionalPatterns != null && optionalPatterns.size() > 0) {
			Pattern newMain = optionalPatterns.remove(0);
			if (optionalPatterns.isEmpty()) {
				return newMain;
			} else {
				PatternSet np = new PatternSet();
				np.addPattern(newMain);
				for(Pattern op : optionalPatterns) {
					np.addOptional(op);
				}
				return np;
			}
		} else {
			return this;
		}
	}
	
   // --------------------------------- RECURSIVE METHODS
	// --------------------------------------------
	public void renamePrefixes(String base, Map<String, String> declared,
			Map<String, String> internal) {
		if ((graphRestriction != null) && (graphRestriction.isSecondType()))
			graphRestriction.getSecond().rename(base, declared, internal);
		for (Expression e : filters)
			e.renamePrefixes(base, declared, internal);
	}

	public void reverseIRIs() {
		if ((graphRestriction != null) && (graphRestriction.isSecondType()))
			graphRestriction.getSecond().reverse();
		for (Expression e : filters) {
			e.reverseIRIs();
		}
		Set<Pattern> subPats = gatherSubPatterns(true);
		for (Pattern p : subPats) {
			p.reverse();
		}
	}
	
	public abstract void reverse();
		
	public void pushFilters() {
		List<Pattern> patterns = new LinkedList<Pattern>();
		patterns.addAll(getSubPatterns(false));
		List<Pattern> optPatterns = getOptionalPatterns();
		if(optPatterns!=null)
			patterns.addAll(optPatterns);

		
		
		
		for (Pattern p : patterns) {
			if (p == this) {
				continue;
			}			
			if(filters!=null) {
				Set<Expression> builtInExists = getFiltersWithBuiltInExistsExpressions(this.filters);
				for (Expression e : filters) {
					if (builtInExists.contains(e)) {
						continue;
					}
					Set<Variable> filterVars=e.gatherVariables();
					Set<Variable> variables = new LinkedHashSet<Variable>(p.gatherVariables());
					variables.retainAll(filterVars);
					if (!variables.isEmpty()) {		
						if(optPatterns==null || !optPatterns.contains(p) )
							p.filters.add(e);
						p.pushedFilters.add(e);						
					}
				}			
			}	
			p.pushFilters();
		}			
		
	}
	
	public void pushGraphRestrictions() {
		if (optionalPatterns != null) {
			pushGraphRestrictions(optionalPatterns);
		}
		List<Pattern> functionExpressions =  new LinkedList<Pattern>();
		for (BuiltinFunctionExpression e : getBuiltInExpressionsForPlanning()) {
			functionExpressions.add(e.getPatternArguments());
		}
		if (!functionExpressions.isEmpty()) {
			pushGraphRestrictions(functionExpressions);
		}
	}

	public void pushGraphRestrictions(List<Pattern> children) {
		if (graphRestriction != null) {
			for(Pattern p : children) {
				if (p.getGraphRestriction() == null
				&& !p.getType().equals(EPatternSetType.BIND)) {
					p.setGraphRestriction(graphRestriction);
				}
			}
		}
		
		for(Pattern p : children) {
			if (p != this) {
				p.pushGraphRestrictions();
			}
		}
	}

	public Set<BinaryUnion<Variable, IRI>> getGraphRestrictions() {
		Set<BinaryUnion<Variable, IRI>> result = HashSetFactory.make();
		for (Pattern pattern : gatherSubPatterns()) {
			if (pattern.getGraphRestriction() != null) {
				result.add(pattern.getGraphRestriction());
			}
		}

		return result;
	}
	
	public Set<Expression> gatherFilters() {
		Set<Expression> result = HashSetFactory.make();
		for (Pattern pattern : gatherSubPatterns()) {			
				result.addAll(pattern.getFilters());			
		}
		return result;
	}
	
	
	// --------------------------------- END: RECURSIVE METHODS
	// ----------------------------------------

	// ---------------------------------- PATTERN INDEX UTILITIES
	// --------------------------------------


	public static HierarchicalIdentifier getParentIdentifier(
			HierarchicalIdentifier hi) {
		if (hi == null)
			return null;
		return hi.getParent();
	}



	// ---------------------------------- END: PATTERN INDEX UTILITIES
	// ---------------------------------

	// ---------------------------------- ABSTRACT METHODS
	// ---------------------------------------------
	public abstract Set<BlankNodeVariable> gatherBlankNodes();

	public abstract Set<Variable> gatherVariables();
	
	public abstract Set<Variable> gatherOptionalVariablesWithMultipleBindings();
	
	public abstract Set<Variable> gatherVariablesWithOptional();
	
	public abstract Set<Variable> gatherIRIBoundVariables();
	
	public abstract Set<Variable> gatherVariablesInTransitiveClosure();

	public abstract void replaceFilterBindings();
	
	public abstract int getNumberTriples();

	public abstract Set<Pattern> gatherSubPatterns(boolean includeOptionals);

	public abstract Set<Pattern> gatherSubPatternsExcluding(Pattern except, boolean includeOptionals);
	
	public abstract Set<Pattern> getSubPatterns(boolean includeOptionals);
	
	public abstract boolean isEmpty();

	// ---------------------------------- END: ABSTRACT METHODS
	// -----------------------------------------

	public Set<Pattern> gatherSubPatterns() {
		return gatherSubPatterns(false);
	}
	
	public Set<BuiltinFunctionExpression> getBuiltInExpressionsForPlanning() {
		Set<BuiltinFunctionExpression> exps = HashSetFactory.make();
		// gather any complex expressions in the filters that need planning
		// e.g., graph patterns buried in the not exists or exists func calls
		for (Expression e: getFilters()) {
			for (Expression sub: e.getSubExpressions()) {
				BuiltinFunctionExpression builtIn = null;;
				if (!(sub instanceof BuiltinFunctionExpression)) {
					continue;
				}
				builtIn = (BuiltinFunctionExpression) sub;
				if (builtIn.isExists()) {
					exps.add(builtIn);
				}
			}
			if (e instanceof BuiltinFunctionExpression && ((BuiltinFunctionExpression) e).isExists()) {
				exps.add(((BuiltinFunctionExpression) e));
			}
		}
		return exps;
	}
	
	public static Set<Expression> getFiltersWithBuiltInExistsExpressions(List<Expression> filters) {
		Set<Expression> exps = HashSetFactory.make();
		// gather any complex expressions in the filters that need planning
		// e.g., graph patterns buried in the not exists or exists func calls
		for (Expression e: filters) {
			for (Expression sub: e.getSubExpressions()) {
				BuiltinFunctionExpression builtIn = null;;
				if (sub instanceof BuiltinFunctionExpression) {
					builtIn = (BuiltinFunctionExpression) sub;
					if (builtIn.isExists()) {
						exps.add(e);
					}
				}
			}
			if (e instanceof BuiltinFunctionExpression && ((BuiltinFunctionExpression) e).isExists()) {
				exps.add(e);
			}
		}
		return exps;
	}

	/*
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((filters == null) ? 0 : filters.hashCode());
		result = prime
				* result
				+ ((graphRestriction == null) ? 0 : graphRestriction.hashCode());
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
		Pattern other = (Pattern) obj;
		if (filters == null) {
			if (other.filters != null)
				return false;
		} else if (!filters.equals(other.filters))
			return false;
		if (graphRestriction == null) {
			if (other.graphRestriction != null)
				return false;
		} else if (!graphRestriction.equals(other.graphRestriction))
			return false;
		return true;
	}
	*/
	
	public Map<Variable, Set<Constant>> getFilterBindings() {
		if (filterBindings == null) {
			filterBindings = getFilterBindings(gatherVariables(), getFilters());			
		}
		return filterBindings;
	}
	
	public static Map<Variable, Set<Constant>> getFilterBindings(Set<Variable> vars, Collection<Expression> filters) {
		Map<Variable, Set<Constant>> filterBindings = HashMapFactory.make();
		for (Variable v : vars) {
			Set<Constant> consts = getVariableEqualityValues(filters, v);
			for (Constant c : consts)
				PatternUtils.addToMap(v, c, filterBindings);
		}
		return filterBindings;
	}

	public static Set<Constant> getVariableEqualityValues(Collection<Expression> e,
			Variable v) {
		Set<Constant> ret = new HashSet<Constant>();
		for (Expression ee : e) {
			Set<Constant> cr = getVariableEqualityValues(ee, v);
			if (cr != null)
				ret.addAll(cr);
		}
		// return (ret.size() > 0) ? ret : null; // changed to avoid null
		// pointer exception.
		return ret;
	}
	
	private static Set<Constant> getVariableEqualityValues(Expression e,
			Variable v) {
		class ConstantEqualityListener implements IExpressionTraversalListener {

			private Variable var;

			public ConstantEqualityListener(Variable v) {
				var = v;
			}

			private Set<Constant> constants = new HashSet<Constant>();

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * com.ibm.research.rdf.store.sparql11.model.IExpressionTraversalListener
			 * #startExpression(com.ibm.research.rdf.store.sparql11.model.Expression)
			 */
			public void startExpression(Expression e) {
				// what types of expressions are these? relational expressions
				// only
				// one side must be a constant and the other side must be a
				// variable
				if (e.getType() == EExpressionType.RELATIONAL) {
					RelationalExpression re = (RelationalExpression) e;
					if (re.getOperator() != ERelationalOp.EQUAL)
						return;
					if (isVariable(re.getLeft(), var)) {
						// is right a constant?
						if (re.getRight() == null)
							return;
						if (re.getRight().getType() == EExpressionType.CONSTANT)
							constants.add(((ConstantExpression) re.getRight())
									.getConstant());
					} else if ((re.getRight() != null)
							&& (isVariable(re.getRight(), var))) {
						if (re.getLeft().getType() == EExpressionType.CONSTANT)
							constants.add(((ConstantExpression) re.getLeft())
									.getConstant());
					}
				} else if (e.getType() == EExpressionType.BUILTIN_FUNC) {
					BuiltinFunctionExpression be = (BuiltinFunctionExpression) e;
					if (be.gatherVariables().contains(var)) {
						if (be.getBuiltinType() == EBuiltinType.REGEX) {
							for (Expression b : be.getArguments()) {
								if (b.getType() == EExpressionType.CONSTANT) {
									constants.add(((ConstantExpression) b)
										.getConstant());
								}
							}
						}
					}
					// This part of the code assumes that ANY function call that has arguments with constants in its filters 'binds' variables from the 
					// of triples in the query.
				} else if (e.getType() == EExpressionType.FUNC_CALL) {
					FunctionCallExpression be = (FunctionCallExpression) e;
					if (be.gatherVariables().contains(var)) {
						for (Expression b : be.getCall().getArguments()) {
							if (b.getType() == EExpressionType.CONSTANT) {
								constants.add(((ConstantExpression) b)
									.getConstant());
							}
						}
					}
				}
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * com.ibm.research.rdf.store.sparql11.model.IExpressionTraversalListener
			 * #endExpression(com.ibm.research.rdf.store.sparql11.model.Expression)
			 */
			public void endExpression(Expression e) {
			}

			public Set<Constant> getConstants() {
				return constants;
			}

			protected boolean isVariable(Expression e, Variable v) {
				if (e.getType() == EExpressionType.VAR) {
					return ((VariableExpression) e).getVariable().equals(
							v.getName());
				} else
					return false;
			}
		}

		ConstantEqualityListener list = new ConstantEqualityListener(v);
		e.traverse(list);
		return list.getConstants();
	}

	 @Override
	 public boolean isMandatory() {
		return true;
	 }
	
	 public Pattern getGroup() {
		 if (getType() == EPatternSetType.SUBSELECT) {
			 return getParent().getGroup();
		 } else if (this instanceof PatternSet) {
			 if (getParent() != null && getParent().getOptionalPatterns() != null && getParent().getOptionalPatterns().contains(this)) {
				 return getParent().getGroup();
			 } else {
				 return this;
			 }
		 } else {
			 return getParent() != null? getParent().getGroup() : null;
		 }
	 }
	 
	 public Set<Variable> inScopeVars() {
	   switch (type) {
		case AND:
		case PRODUCT:
		case UNION:
		case OPTIONAL: {
			Set<Variable> result = HashSetFactory.make();
			for(Pattern p : ((PatternSet)this).getPatterns()) {
				result.addAll(p.inScopeVars());
			}
			if (((PatternSet)this).getOptionalPatterns() != null) {
				for(Pattern p : ((PatternSet)this).getOptionalPatterns()) {
					result.addAll(p.inScopeVars());
				}
			}
			if(graphRestriction!=null && graphRestriction.isFirstType()){
				result.add(graphRestriction.getFirst());
			}
			return result;
		}
		
		case BIND:
			return Collections.singleton(((BindPattern)this).var);
		
		case SIMPLE:{
			if(graphRestriction!=null && graphRestriction.isFirstType()){
				Set<Variable> result = HashSetFactory.make(gatherVariables());
				result.add(graphRestriction.getFirst());
				return result;
			} else {
				return gatherVariables();
			}
		}
		
		case BINDFUNC: {
			return this.gatherVariables();
		}
			
		case SUBSELECT:
			return Iterator2Collection.toSet(
				new MapIterator<ProjectedVariable,Variable>(
					((SubSelectPattern) this).getSelectClause().getProjectedVariables().iterator(),
					new Function<ProjectedVariable,Variable>() {
						@Override
						public Variable apply(ProjectedVariable arg0) {
							return arg0.getVariable();
						}
					}));
			
		case EXISTS:
		case MINUS:
		case NOT_EXISTS:
			return Collections.emptySet();
		default:
			return Collections.emptySet();
		 }
	 }
	 
	 public Set<Expression> unscopedFilters() {
		 if (filters != null && !filters.isEmpty()) {
			 final Set<Expression> result = HashSetFactory.make();
			 final Set<Variable> scope = getGroup().inScopeVars();
			 for(Expression e : filters) {
				 e.traverse(new IExpressionTraversalListener() {
					@Override
					public void startExpression(Expression e) {
						if (!(e.getType().equals(EExpressionType.BUILTIN_FUNC) && ((BuiltinFunctionExpression)e).isExists())) {
							if (! scope.containsAll(e.getVariables())) {
								result.add(e);
							}
						}
					}
					@Override
					public void endExpression(Expression e) {
						// do nothing
					}
				 });
			 }
			 if (result != null) {
				 return result;
			 }
		 }
		 
		 return Collections.emptySet();
	 }

	 public void killUnscopedAccesses() {
		 for(Pattern p : gatherSubPatterns(true)) {
			 
			 // kill bad filters
			 Set<Expression> unscopedFilters = p.unscopedFilters();
			 if (!unscopedFilters.isEmpty()) {
				 p.filters.clear();
				 p.filters.add(new ConstantExpression(false));
			 }
		 
			 // kill bad binds
			 if (p instanceof BindPattern && ((BindPattern)p).isUnscopedBind()) {
				 BindPattern b = (BindPattern) p;
				 int i = ((PatternSet)p.getParent()).getPatterns().indexOf(p);
				 ((PatternSet)p.getParent()).getPatterns().set(i, new BindPattern(b.getVar(), new ConstantExpression()));
			 }
		 }
	 }
	 
	 public Set<Variable> optionalOnlyVars() {
		 Set<Variable> allVars = gatherVariablesWithOptional();
		 allVars.removeAll(gatherVariables());
		 return allVars;
	 }
	 
	 public Set<Variable> potentialScopeClashes() {
		 assert getGroup() == this;
		 Set<Variable> opts = optionalOnlyVars();
		 Pattern top = this;

		 while(top.getParent() != null) {
			 top = top.getParent();
		 }
		 
		 Set<Variable> others = HashSetFactory.make();
		 for(Pattern p : top.gatherSubPatternsExcluding(this, true)) {
			 if (! (p instanceof PatternSet)) {
				 others.addAll(p.getVariables());
			 }
		 }
		 
		 opts.retainAll(others);
		 
		 return opts;
	 }
}
