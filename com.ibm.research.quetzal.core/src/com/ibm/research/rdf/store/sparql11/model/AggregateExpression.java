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

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.antlr.stringtemplate.StringTemplate;

import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.runtime.service.types.TypeMap;
import com.ibm.research.rdf.store.sparql11.sqlwriter.FilterContext;

public class AggregateExpression extends Expression {
	public static enum EType {
		UNKNOWN, COUNT, SUM, MIN, MAX, AVG, SAMPLE, GROUP_CONCAT
	}

	private static final String AGGREGATE_COUNT = "aggregate_count";
	private static final String AGGREGATE_DISTINCT_COUNT = "aggregate_distinct_count";
	private static final String AGGREGATE_FUNCTION = "aggregate_function";
	private static final String GROUP_CONCAT = "GROUP_CONCAT";
	private static final String AGGREGATE_FUNCTION_WITH_TYP_CHECK = "aggregate_function_with_type_check";

	private EType aggrType = EType.UNKNOWN;
	private short returnType;
	private boolean distinct = false;
	private Expression args = null;
	private String separator = "";

	public AggregateExpression() {
		super(Expression.EExpressionType.AGGREGATE);
	}

	public EType getAggregationType() {
		return aggrType;
	}

	public void setAggregationType(EType t) {
		this.aggrType = t;
	}

	public boolean isDistinct() {
		return this.distinct;
	}

	public void isDistinct(boolean b) {
		this.distinct = b;
	}

	public Expression getArgs() {
		return this.args;
	}

	public void setArgs(Expression a) {

		this.args = a;
	}

	public String getSeparator() {
		return this.separator;
	}

	public void setSeparator(String s) {
		this.separator = s;
	}

	public TypeMap.TypeCategory getTypeRestriction(Variable v) {
		if (!this.gatherVariables().contains(v))
			return TypeMap.TypeCategory.NONE;
		// Kavitha The only functions whose arguments have to be numeric are sum
		// and average
		switch (aggrType) {
		case SUM:
		case AVG:
			return TypeMap.TypeCategory.NUMERIC;
		case MIN:
		case MAX:
			return TypeMap.TypeCategory.NUMERIC;
		}
		if (this.getArgs() != null) {
			return args.getTypeRestriction(v);
		}
		return TypeMap.TypeCategory.NONE;
	}

	@Override
	public Short getReturnType() {
		switch (aggrType) {
		case COUNT:
			return TypeMap.INTEGER_ID;
		case SUM:
		case MIN:
		case MAX:
		case AVG:
		case SAMPLE:
			return TypeMap.DOUBLE_ID;
		case GROUP_CONCAT:
			return TypeMap.STRING_ID;
		}
		return TypeMap.DOUBLE_ID;
	}

	@Override
	public short getTypeEquality(Variable v) {
		return TypeMap.NONE_ID;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		switch (aggrType) {
		case COUNT:
			sb.append("COUNT(");
			break;
		case SUM:
			sb.append("SUM(");
			break;
		case MIN:
			sb.append("MIN(");
			break;
		case MAX:
			sb.append("MAX(");
			break;
		case AVG:
			sb.append("AVG(");
			break;
		case SAMPLE:
			sb.append("SAMPLE(");
			break;
		case GROUP_CONCAT:
			sb.append("GROUP_CONCAT(");
			break;
		}
		if (distinct)
			sb.append("DISTINCT ");
		if (args != null)
			sb.append(args.toString());
		else
			sb.append(" * ");
		if (!separator.equals(""))
			sb.append("; SEPARATOR=" + separator);
		sb.append(")");
		return sb.toString();
	}

	@Override
	public String getStringWithVarName() {
		StringBuilder sb = new StringBuilder();
		switch (aggrType) {
		case COUNT:
			sb.append("COUNT(");
			break;
		case SUM:
			sb.append("SUM(");
			break;
		case MIN:
			sb.append("MIN(");
			break;
		case MAX:
			sb.append("MAX(");
			break;
		case AVG:
			sb.append("AVG(");
			break;
		case SAMPLE:
			sb.append("SAMPLE(");
			break;
		case GROUP_CONCAT:
			sb.append("GROUP_CONCAT(");
			break;
		}
		if (distinct)
			sb.append("DISTINCT ");
		if (args != null)
			sb.append(args.getStringWithVarName());
		else
			sb.append(" * ");
		if (!separator.equals(""))
			sb.append("; SEPARATOR=" + separator);
		sb.append(")");
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((args == null) ? 0 : args.hashCode());
		result = prime * result + aggrType.hashCode();
		result = prime * result + ((distinct == true) ? 1 : 0);
		result = prime * result + separator.hashCode();
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
		AggregateExpression other = (AggregateExpression) obj;
		if (args == null) {
			if (other.args != null)
				return false;
		} else if (!args.equals(other.args))
			return false;
		if (aggrType != other.aggrType)
			return false;
		if (distinct != other.distinct)
			return false;
		if (!separator.equals(other.separator))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.research.rdf.store.sparql11.model.Expression#renamePrefixes(java.lang.
	 * String, java.util.Map, java.util.Map)
	 */
	@Override
	public void renamePrefixes(String base, Map<String, String> declared,
			Map<String, String> internal) {
		if (args != null)
			args.renamePrefixes(base, declared, internal);
	}

	@Override
	public void reverseIRIs() {
		args.reverseIRIs();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.research.rdf.store.sparql11.model.Expression#gatherBlankNodes()
	 */
	@Override
	public Set<BlankNodeVariable> gatherBlankNodes() {
		if (args != null)
			return args.gatherBlankNodes();
		else
			return new HashSet<BlankNodeVariable>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.research.rdf.store.sparql11.model.Expression#gatherVariables()
	 */
	@Override
	public Set<Variable> gatherVariables() {
		if (args != null)
			return args.gatherVariables();
		else
			return new HashSet<Variable>();
	}

	@Override
	public Set<Variable> getVariables() {
		return Collections.emptySet();
	}

	public void traverse(IExpressionTraversalListener l) {
		l.startExpression(this);
		args.traverse(l);
		l.endExpression(this);
	}

	public boolean containsEBV() {
		return false;
	}

	public boolean containsBound() {
		return false;
	}

	public boolean containsNotBound() {
		return false;
	}

	public boolean containsCast(Variable v) {
		if (args != null)
			return args.containsCast(v);
		else
			return false;
	}

	@Override
	public String visit(FilterContext context, Store store) {
		Set<Variable> vars = null;
		if (getArgs() != null) {
			vars = gatherVariables();
		}

		StringBuffer sql = new StringBuffer();
		StringTemplate t = null;

		if (AggregateExpression.EType.COUNT == getAggregationType()) {
			if (vars != null) {
				Iterator<Variable> varIterator = vars.iterator();
				boolean isFirstVar = true;
				while (varIterator.hasNext()) {
					if (!isFirstVar) {
						sql.append("||");
					} else {
						isFirstVar = false;
					}
					sql.append(varIterator.next().getName());
				}
			} else {
				sql.append("*");
			}

			if (isDistinct()) {
				t = store.getInstanceOf(AGGREGATE_DISTINCT_COUNT);
			} else {
				t = store.getInstanceOf(AGGREGATE_COUNT);
			}
			t.setAttribute("var", sql.toString());

			return t.toString();
		} else if (getAggregationType() == EType.GROUP_CONCAT) {
			t = store.getInstanceOf(GROUP_CONCAT);
			t.setAttribute("var", vars.iterator().next().getName());
			t.setAttribute("s", getSeparator().substring(1, 2));
			
			return t.toString();
		} else {
			if (context.doesConstantNeedTypeCheck(this)) {
				t = store.getInstanceOf(AGGREGATE_FUNCTION_WITH_TYP_CHECK);
				t.setAttribute("typecheck",
						getNumericTypeCheckSQL(context, store));

			} else {
				t = store.getInstanceOf(AGGREGATE_FUNCTION);
			}

			EType func = getAggregationType();
			if (AggregateExpression.EType.SAMPLE == getAggregationType()) {
				func = EType.MIN; // Kavitha: If this is a sample type, we need
									// to basically return 1 item from the set
			}
			t.setAttribute("function", func.toString().toLowerCase()); // XPath/XQuery
																		// functions
																		// are
																		// all
																		// lower
																		// case!
			if (getArgs() instanceof VariableExpression) {
				t.setAttribute("var", vars.iterator().next().getName());
			} else {
				t.setAttribute("var", getArgs().visit(context, store));
			}

			return t.toString();
		}
	}

}
