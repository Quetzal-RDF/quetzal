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
 package com.ibm.research.rdf.store.utilities;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.ibm.research.rdf.store.sparql11.model.BuiltinFunctionExpression;
import com.ibm.research.rdf.store.sparql11.model.Expression;
import com.ibm.research.rdf.store.sparql11.model.Expression.EBuiltinType;
import com.ibm.research.rdf.store.sparql11.model.Expression.EExpressionType;
import com.ibm.research.rdf.store.sparql11.model.Expression.ERelationalOp;
import com.ibm.research.rdf.store.sparql11.model.Expression.EUnaryOp;
import com.ibm.research.rdf.store.sparql11.model.Pattern;
import com.ibm.research.rdf.store.sparql11.model.Pattern.EPatternSetType;
import com.ibm.research.rdf.store.sparql11.model.PatternSet;
import com.ibm.research.rdf.store.sparql11.model.Query;
import com.ibm.research.rdf.store.sparql11.model.RelationalExpression;
import com.ibm.research.rdf.store.sparql11.model.UnaryExpression;
import com.ibm.research.rdf.store.sparql11.model.Variable;
import com.ibm.research.rdf.store.sparql11.model.VariableExpression;
import com.ibm.wala.util.collections.HashSetFactory;

public class MinusRewriter {

	private static Set<Variable> getCommonVars(Pattern lhs, Expression rhs) {
		// gather variables on left hand side
		Set<Variable> lhsVars = lhs.gatherVariablesWithOptional();
		
		// gather variables on the right hand side
		Set<Variable> rhsVars = rhs.gatherVariables();
		
		// variables for subtract
		Set<Variable> commonVars = HashSetFactory.make(lhsVars);
		commonVars.retainAll(rhsVars);
		return commonVars;
	}

	private static boolean checkForMinusSuitability(Pattern lhs, Expression rhs) {
		Set<Variable> commonVars = getCommonVars(lhs, rhs);
		
		// special case when no common vars: becomes all-or-nothing filter
		if (commonVars.isEmpty()) {
			return false;
		}
		
		// check for bad filters in rhs
		for(Pattern rhs_pattern : ((BuiltinFunctionExpression)rhs).getPatternArguments().gatherSubPatterns(true)) {
			for(Expression e : rhs_pattern.getFilters()) {
				if (! Collections.disjoint(e.gatherVariables(), commonVars)) {
					if (e.getType() != EExpressionType.RELATIONAL ||
					    ((RelationalExpression)e).getOperator() != ERelationalOp.EQUAL) {
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	private static boolean isOptionalAsFilterNotExists(PatternSet topLevel) {
		Set<Variable> mainVars = HashSetFactory.make();
		for(Pattern p : topLevel.getPatterns()) {
			mainVars.addAll(p.gatherVariables());
		}
		
		if (topLevel.getOptionalPatterns() != null && topLevel.getOptionalPatterns().size() == 1) {
			Pattern rhs = topLevel.getOptionalPatterns().iterator().next();
		
			if (topLevel.getFilters() != null && topLevel.getFilters().size() == 1) {
				Expression e = topLevel.getFilters().iterator().next();
				
				// make sure the filter is a not bound of a var only in the optional part
				check_filter: {
					if (e.getType() == EExpressionType.UNARY) {
						UnaryExpression u = (UnaryExpression)e;
						if (u.getOperator() == EUnaryOp.NOT) {
							if (u.getExpression().getType() == EExpressionType.BUILTIN_FUNC) {
								BuiltinFunctionExpression f = (BuiltinFunctionExpression)u.getExpression();
								if (f.getBuiltinType() == EBuiltinType.BOUND) {
									assert f.getArguments().size() == 1;
									Variable v = new Variable(((VariableExpression) f.getArguments().iterator().next()).getVariable());
									if (rhs.gatherVariables().contains(v)) {
										if (! mainVars.contains(v)) {
											break check_filter;
										}
									}
								}
							}
						}
					}
					return false;
				}
				
				return true;	
			}
			 
		}
		
		return false;
	}
	
	private static Expression findExists(PatternSet topLevel) {
		for(Expression e : topLevel.getFilters()) {
			if (e.getType() == EExpressionType.BUILTIN_FUNC && ((BuiltinFunctionExpression)e).getBuiltinType() == EBuiltinType.NOT_EXISTS) {
				return e;
			}
		}
		return null;
	}
	
	private static boolean isFilterNotExistsAsMinus(PatternSet topLevel) {
		Expression exists = findExists(topLevel);

		if (exists == null) {
			return false;
		}

		return checkForMinusSuitability(topLevel, exists);
	}
	
	private final Expression rhs;
	
	protected final Query q;
	
	private MinusRewriter(Query q, PatternSet topLevel) {
		this.q = q;
		this.rhs = findExists(topLevel);
	}
	
			
	private PatternSet rewriteFilterNotExistsAsMinus(PatternSet toMinus) {	
		// remove filter not exists
		toMinus.removeFilter(rhs);
		
		// make new MINUS node
		PatternSet minus = new PatternSet(EPatternSetType.MINUS);
		minus.addPattern(((BuiltinFunctionExpression)rhs).getPatternArguments());
		
		// add minus node to replace filter
		toMinus.addPattern(minus);
		
		return toMinus;
	}
		
	private abstract static class Rewriter {
		protected final Query q;
	
		protected Rewriter(final Query q) {
			this.q = q;
		}

		protected abstract boolean shouldRewrite(PatternSet p);

		protected abstract PatternSet rewrite(PatternSet p);

		public boolean rewrite() {

			Pattern np = (new Object() {
				private boolean changed = false;

				private Pattern rewriteInternal(Pattern p) {
					if (p instanceof PatternSet) {

						// rewrite this pattern, if warranted...
						if (shouldRewrite((PatternSet)p)) {
							changed = true;
							return rewrite((PatternSet)p);

							// ...otherwise, try to rewrite child patterns
						} else { 
							List<Pattern> patterns = ((PatternSet) p).getPatterns();
							for(int i = 0 ; i < patterns.size(); i++) {
								Pattern c = patterns.get(i);
								Pattern nc = rewriteInternal(c);
								if (nc != c) {
									patterns.set(i, nc);
									nc.setParent(p);
									break;
								}
							}
							if (p.getFilters() != null) {
								for(Expression e : p.gatherFilters()) {
									if (e instanceof BuiltinFunctionExpression && ((BuiltinFunctionExpression)e).isExists()) {
										Pattern c = ((BuiltinFunctionExpression)e).getPatternArguments();
										Pattern nc = rewriteInternal(c);
										if (c != nc) {
											((BuiltinFunctionExpression)e).setPatternArgument(nc);
											break;
										}
									}
								}
							}
						}

						return p;
					} else {
						return p;
					}
				}

				private Pattern doit() {
					Pattern np = rewriteInternal(q.getMainPattern());
					if (changed) {
						return np;
					} else {
						return null;
					}
				}
			}).doit();

			if (np != null) {
				q.setMainPattern(np);
				rewrite();
				return true;
			} else {
				return false;
			}
		}
	}
	
	public static class OptionalAsFilterRewriter extends Rewriter {

		public OptionalAsFilterRewriter(Query q) {
			super(q);
		}

		@Override
		protected boolean shouldRewrite(PatternSet p) {
			return isOptionalAsFilterNotExists(p);
		}

		@Override
		protected PatternSet rewrite(PatternSet p) {
			Pattern filterPattern = p.getOptionalPatterns().iterator().next();
			
			p.getOptionalPatterns().clear();
			p.getFilters().clear();
			
	        if (filterPattern.getType() == EPatternSetType.AND &&
	        	filterPattern.getOptionalPatterns() == null &&
	        	filterPattern.getFilters() == null &&
	        	filterPattern instanceof PatternSet &&
	        	((PatternSet)filterPattern).getPatterns().size() == 1) {
	        
	        	filterPattern = ((PatternSet)filterPattern).getPatterns().iterator().next();
	        }
	        
	        filterPattern.setParent(null);
	        
	        BuiltinFunctionExpression filter = new BuiltinFunctionExpression(EBuiltinType.NOT_EXISTS, filterPattern);
	        
			p.addFilter(filter);
			
			return p;
		}
		
	}
	
	public static class FilterAsMinusRewriter extends Rewriter {

		public FilterAsMinusRewriter(Query q) {
			super(q);
		}

		@Override
		protected boolean shouldRewrite(PatternSet p) {
			return isFilterNotExistsAsMinus(p);
		}

		@Override
		protected PatternSet rewrite(PatternSet p) {
			return (new MinusRewriter(q, (PatternSet)p)).rewriteFilterNotExistsAsMinus(p);
		}
		
	}
}
