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
 package com.ibm.research.rdf.store.sparql11.planner.statistics;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.research.rdf.store.sparql11.model.BinaryUnion;
import com.ibm.research.rdf.store.sparql11.model.BuiltinFunctionExpression;
import com.ibm.research.rdf.store.sparql11.model.Constant;
import com.ibm.research.rdf.store.sparql11.model.ConstantExpression;
import com.ibm.research.rdf.store.sparql11.model.Expression;
import com.ibm.research.rdf.store.sparql11.model.Expression.EBuiltinType;
import com.ibm.research.rdf.store.sparql11.model.Expression.EExpressionType;
import com.ibm.research.rdf.store.sparql11.model.Expression.ERelationalOp;
import com.ibm.research.rdf.store.sparql11.model.FunctionCallExpression;
import com.ibm.research.rdf.store.sparql11.model.IExpressionTraversalListener;
import com.ibm.research.rdf.store.sparql11.model.IRI;
import com.ibm.research.rdf.store.sparql11.model.Pattern;
import com.ibm.research.rdf.store.sparql11.model.ProjectedVariable;
import com.ibm.research.rdf.store.sparql11.model.Query;
import com.ibm.research.rdf.store.sparql11.model.RelationalExpression;
import com.ibm.research.rdf.store.sparql11.model.Variable;
import com.ibm.research.rdf.store.sparql11.model.VariableExpression;
import com.ibm.wala.util.collections.HashSetFactory;

/**
 */
public class PatternUtils {

	public static Set<Constant> getVariableEqualityValues(Expression e,
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

	public static <V, N> void addToMap(V v, N n, Map<V, Set<N>> map) {
		Set<N> s = map.get(v);
		if (s == null) {
			s = new HashSet<N>();
			map.put(v, s);
		}
		s.add(n);
	}

	public static <V, N> void removeFromMap(V v, N n, Map<V, Set<N>> map) {
		Set<N> s = map.get(v);
		if (s == null)
			return;
		s.remove(n);
		if (s.size() == 0)
			map.remove(v);
	}



	public static <T, U> Map<T, Set<U>> unionMaps(Map<T, Set<U>> m1,
			Map<T, Set<U>> m2) {
		Map<T, Set<U>> ret = new LinkedHashMap<T, Set<U>>(m1);
		for (T key : m2.keySet()) {
			Set<U> val = m2.get(key);
			Set<U> crs = ret.get(key);
			if (crs == null) {
				crs = new HashSet<U>();
				ret.put(key, crs);
			}
			crs.addAll(val);
		}
		return ret;
	}

	public static Set<Variable> getReferredVariables(Query q) {
		Set<Variable> referredVariables = null;

		if (q.isSelectQuery()) {
			List<ProjectedVariable> pvars = q.getSelectQuery()
					.getSelectClause().getProjectedVariables();
			referredVariables = new LinkedHashSet<Variable>();
			for (ProjectedVariable pvar : pvars) {
				referredVariables.add(pvar.getVariable());
			}
			// referredVariables = new
			// LinkedHashSet<Variable>(q.getSelectQuery().getProjectedVariables());
		} else if (q.isDescribeQuery()) {
			List<BinaryUnion<Variable, IRI>> resources = q.getDescribeQuery()
					.getResources();
			for (BinaryUnion<Variable, IRI> b : resources) {
				referredVariables = new LinkedHashSet<Variable>();
				referredVariables.add(b.getFirst());
			}
		} else {
			throw new UnsupportedOperationException(
					"ASK OR CONSTRUCT QUERIES NOT YET IMPLEMENTED");
		}

		return referredVariables;

	}

	public static Pattern findLeastCommonAncestor(Pattern p, Pattern q) {
		Set<Pattern> pAncestors = HashSetFactory.make();
		do {
			pAncestors.add(p);
			p = p.getParent();
		} while (p != null);

		do {
			if (pAncestors.contains(q)) {
				return q;
			}
			q = q.getParent();
		} while (q != null);

		assert false : "disconnected patterns; using patterns from different queries?";
		return null;
	}

	public static boolean isInOptional(Pattern p) {
		if (p.getParent() != null && p.getParent().getOptionalPatterns() != null && p.getParent().getOptionalPatterns().contains(p)) {
			return true;
		}
		if (p.getParent() != null) {
			return isInOptional(p.getParent());
		}
		return false;
	}
}
