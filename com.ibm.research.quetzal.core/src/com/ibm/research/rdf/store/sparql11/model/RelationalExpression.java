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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.stringtemplate.StringTemplate;

import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.Store.Db2Type;
import com.ibm.research.rdf.store.runtime.service.types.TypeMap;
import com.ibm.research.rdf.store.sparql11.sqlwriter.FilterContext;
import com.ibm.wala.util.collections.Pair;

public class RelationalExpression extends Expression {
	private Expression left = null;
	private Expression right = null;
	private ERelationalOp operator;
	private static String RDF_OPERATOR = "RDF_OPERATOR";
	private static String RDF_OPERATOR_NUM = "RDF_OPERATOR_NUM";
	private static String RDF_OPERATOR_DATE = "RDF_OPERATOR_DATE";
	private static String NEQ_CAST_ALL = "NEQ_CAST_ALL";
	private static String EQ_CAST_ALL = "EQ_CAST_ALL";
	private static String GT_CAST_ALL = "GT_CAST_ALL";
	private static final String IS_TRUE = "RDF_IS_TRUE";
	private static final String IS_FALSE = "RDF_IS_FALSE";
	private static final String IS_NOT_TRUE = "RDF_IS_NOT_TRUE";
	private static final String IS_NOT_FALSE = "RDF_IS_NOT_TRUE";
	private static String TIMESTAMP_STR = "TIMESTAMP";
	private static String DATE_STR = "DATE";
	private static String TYPE_CHECK_WITH_CAST = "TYPE_CHECK_WITH_CAST";
	private static String TYPE_CHECK_WITH_CAST_TIMESTAMP = "TYPE_CHECK_WITH_CAST_TIMESTAMP";
	private static String TYPE_CHECK_WITH_CAST_DATE = "TYPE_CHECK_WITH_CAST_DATE";

	private static String TYPE_CHECK_CASE = "TYPE_CHECK_CASE";
	private static String TYPE_CHECK = "TYPE_CHECK";

	public RelationalExpression() {
		super(EExpressionType.RELATIONAL);
	}

	public RelationalExpression(Expression e) {
		super(EExpressionType.RELATIONAL);
		left = e;
	}

	public RelationalExpression(Expression l, Expression r, ERelationalOp op) {
		this(l);
		right = r;
		operator = op;
	}

	public Expression getLeft() {
		return this.left;
	}

	public void setLeft(Expression e) {
		this.left = e;
	}

	public Expression getRight() {
		return this.right;
	}

	public boolean containsCast(Variable v) {
		if (left != null && left.containsCast(v))
			return true;
		if (right != null && right.containsCast(v))
			return true;
		return false;
	}

	public void setRight(ERelationalOp t, Expression e) {
		this.operator = t;
		this.right = e;
	}

	public ERelationalOp getOperator() {
		return this.operator;
	}

	@Override
	public Short getReturnType() {
		return TypeMap.BOOLEAN_ID;
	}

	public TypeMap.TypeCategory getTypeRestriction(Variable v) {
		TypeMap.TypeCategory returnType = TypeMap.TypeCategory.NONE;
		if (left.gatherVariables().contains(v)) {
			returnType = left.getTypeRestriction(v);
			if (returnType == TypeMap.TypeCategory.NONE
					&& !left.containsCast(v)) {
				// if(right instanceof ConstantExpression) {
				short rightReturnType = right.getReturnType();
				if ((rightReturnType >= TypeMap.DATATYPE_NUMERICS_IDS_START && rightReturnType <= TypeMap.DATATYPE_NUMERICS_IDS_END)
						|| TypeMap.isDateTime(rightReturnType)) {
					returnType = TypeMap.getTypeCategory(rightReturnType);
				}
				// }
			}
		} else if (right.gatherVariables().contains(v)) {
			returnType = right.getTypeRestriction(v);
			if (returnType == TypeMap.TypeCategory.NONE
					&& !right.containsCast(v)) {
				// if(left instanceof ConstantExpression){
				short leftReturnType = left.getReturnType();
				if ((leftReturnType >= TypeMap.DATATYPE_NUMERICS_IDS_START && leftReturnType <= TypeMap.DATATYPE_NUMERICS_IDS_END)
						|| (TypeMap.isDateTime(leftReturnType))) {
					returnType = TypeMap.getTypeCategory(leftReturnType);

				}
				// }
			}
		}
		return returnType;
	}

	public short getTypeEquality(Variable v) {
		short returnType = TypeMap.NONE_ID;
		if (left.gatherVariables().contains(v)) {
			if (left.getTypeRestriction(v) == TypeMap.TypeCategory.NONE
					&& !left.containsCast(v))
				if (right instanceof ConstantExpression) {
					short rightReturnType = right.getReturnType();
					if (rightReturnType >= TypeMap.STRING_ID)
						returnType = rightReturnType;
					if (rightReturnType == TypeMap.BOOLEAN_ID)
						returnType = rightReturnType;
					if (rightReturnType == TypeMap.DATETIME_ID)
						returnType = rightReturnType;
					if (rightReturnType >= TypeMap.USER_ID_START
							&& rightReturnType <= TypeMap.DATATYPE_IDS_END)
						returnType = rightReturnType;
				}
		} else if (right.gatherVariables().contains(v)) {
			if (right.getTypeRestriction(v) == TypeMap.TypeCategory.NONE
					&& !right.containsCast(v))
				if (left instanceof ConstantExpression) {
					short leftReturnType = left.getReturnType();
					if (leftReturnType >= TypeMap.STRING_ID)
						returnType = leftReturnType;
					if (leftReturnType >= TypeMap.BOOLEAN_ID)
						returnType = leftReturnType;
					if (leftReturnType == TypeMap.DATETIME_ID)
						returnType = leftReturnType;
					if (leftReturnType >= TypeMap.USER_ID_START
							&& leftReturnType <= TypeMap.DATATYPE_IDS_END)
						returnType = leftReturnType;
				}
		}
		return returnType;
	}

	public String getOperatorString() {
		if (this.operator == ERelationalOp.EQUAL) {
			return "RDF_EQ";
		} else if (this.operator == ERelationalOp.NOT_EQUAL) {
			return "RDF_NE";
		} else if (this.operator == ERelationalOp.LESS) {
			return "RDF_LT";
		} else if (this.operator == ERelationalOp.GREATER) {
			return "RDF_GT";
		} else if (this.operator == ERelationalOp.LESS_EQUAL) {
			return "RDF_LE";
		} else if (this.operator == ERelationalOp.GREATER_EQUAL) {
			return "RDF_GE";
		}
		return "";
	}

	@Override
	public List<Expression> getSubExpressions() {
		LinkedList<Expression> exps = new LinkedList<Expression>();
		exps.add(left);
		exps.add(right);
		return exps;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();

		if (this.right == null) {
			sb.append(this.left.toString());
			return sb.toString();
		}

		sb.append("(");
		sb.append(this.left.toString());
		if (this.operator == ERelationalOp.EQUAL) {
			sb.append(" = ");
		} else if (this.operator == ERelationalOp.NOT_EQUAL) {
			sb.append(" != ");
		} else if (this.operator == ERelationalOp.LESS) {
			sb.append(" < ");
		} else if (this.operator == ERelationalOp.GREATER) {
			sb.append(" > ");
		} else if (this.operator == ERelationalOp.LESS_EQUAL) {
			sb.append(" <= ");
		} else if (this.operator == ERelationalOp.GREATER_EQUAL) {
			sb.append(" >= ");
		}
		sb.append(right.toString());
		sb.append(")");

		return sb.toString();
	}

	@Override
	public String getStringWithVarName() {
		StringBuilder sb = new StringBuilder();

		if (this.right == null) {
			sb.append(this.left.getStringWithVarName());
			return sb.toString();
		}

		sb.append("(");
		sb.append(this.left.getStringWithVarName());
		if (this.operator == ERelationalOp.EQUAL) {
			sb.append(" = ");
		} else if (this.operator == ERelationalOp.NOT_EQUAL) {
			sb.append(" != ");
		} else if (this.operator == ERelationalOp.LESS) {
			sb.append(" < ");
		} else if (this.operator == ERelationalOp.GREATER) {
			sb.append(" > ");
		} else if (this.operator == ERelationalOp.LESS_EQUAL) {
			sb.append(" <= ");
		} else if (this.operator == ERelationalOp.GREATER_EQUAL) {
			sb.append(" >= ");
		}
		sb.append(right.getStringWithVarName());
		sb.append(")");

		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.left.hashCode();
		if (this.right != null) {
			result = prime * result + this.right.hashCode();
		}
		if (this.operator != null) {
			result = prime * result + this.operator.hashCode();
		}
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
		RelationalExpression other = (RelationalExpression) obj;
		if (!this.left.equals(other.left))
			return false;
		if (this.operator != other.operator)
			return false;
		if (this.right != null) {
			if (!this.right.equals(other.right))
				return false;
		}
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
		left.renamePrefixes(base, declared, internal);
		right.renamePrefixes(base, declared, internal);
	}

	@Override
	public void reverseIRIs() {
		left.reverseIRIs();
		right.reverseIRIs();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.research.rdf.store.sparql11.model.Expression#gatherBlankNodes()
	 */
	@Override
	public Set<BlankNodeVariable> gatherBlankNodes() {
		Set<BlankNodeVariable> ret = new HashSet<BlankNodeVariable>();
		ret.addAll(left.gatherBlankNodes());
		ret.addAll(right.gatherBlankNodes());
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.research.rdf.store.sparql11.model.Expression#gatherVariables()
	 */
	@Override
	public Set<Variable> gatherVariables() {
		Set<Variable> ret = new HashSet<Variable>();
		ret.addAll(left.gatherVariables());
		ret.addAll(right.gatherVariables());
		return ret;
	}

	@Override
	public Set<Variable> getVariables() {
		return Collections.emptySet();
	}

	public void traverse(IExpressionTraversalListener l) {
		l.startExpression(this);
		left.traverse(l);
		right.traverse(l);
		l.endExpression(this);
	}

	public String getOperatorSymbol() {
		return getOperatorSymbol(this.operator);
	}

	public static String getOperatorSymbol(ERelationalOp op) {
		StringBuffer sb = new StringBuffer();
		if (op == ERelationalOp.EQUAL) {
			sb.append(" = ");
		} else if (op == ERelationalOp.NOT_EQUAL) {
			sb.append(" <> ");
		} else if (op == ERelationalOp.LESS) {
			sb.append(" < ");
		} else if (op == ERelationalOp.GREATER) {
			sb.append(" > ");
		} else if (op == ERelationalOp.LESS_EQUAL) {
			sb.append(" <= ");
		} else if (op == ERelationalOp.GREATER_EQUAL) {
			sb.append(" >= ");
		}
		return sb.toString();
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

	@Override
	public String visit(FilterContext context, Store store) {
		String left = getLeft().visit(context, store);
		String right = getRight().visit(context, store);
		if ((left.equals("")) || (right.equals("")))
			return "";

		String symbol = getOperatorSymbol();
		if (symbol.equals(""))
			return "";
		StringTemplate t = null;

		Expression rightExpression = getRight();
		Expression leftExpression = getLeft();

		if (rightExpression instanceof ConstantExpression) {
			return handleRelationalsWithConstants(
					((ConstantExpression) rightExpression), leftExpression,
					context, getOperator(), store);
		} else if (leftExpression instanceof ConstantExpression) {
			return handleRelationalsWithConstants(
					((ConstantExpression) leftExpression), rightExpression,
					context, getOperator(), store);
		} else if (leftExpression instanceof VariableExpression
				&& rightExpression instanceof VariableExpression) {
			String lVarString = ((VariableExpression) leftExpression)
					.getVariable();
			String rVarString = ((VariableExpression) rightExpression)
					.getVariable();
			if (context.getVarMap().get(lVarString).snd == null
					|| context.getVarMap().get(rVarString).snd == null
					|| context.getVarMap().get(lVarString).snd == context
							.getVarMap().get(rVarString).snd) { // comparison
																// with IRI, or
																// types that
																// are the same
				t = store.getInstanceOf(RDF_OPERATOR);

			} else {
				if (context.getVarDb2Type().get(lVarString) == Db2Type.DECFLOAT
						|| context.getVarDb2Type().get(rVarString) == Db2Type.DECFLOAT) {
					t = store.getInstanceOf(RDF_OPERATOR_NUM);
				} else if (context.getVarDb2Type().get(lVarString) == Db2Type.VARCHAR
						|| context.getVarDb2Type().get(rVarString) == Db2Type.VARCHAR) {
					t = store.getInstanceOf(RDF_OPERATOR);
					if (context.getVarMap().get(lVarString).snd != null) {
						t.setAttribute("left_type",
								context.getVarMap().get(lVarString).snd);
						t.setAttribute("right_type",
								context.getVarMap().get(rVarString).snd);
					}

				} else if (context.getVarDb2Type().get(lVarString) == Db2Type.TIMESTAMP
						|| context.getVarDb2Type().get(rVarString) == Db2Type.TIMESTAMP) {
					t = store.getInstanceOf(RDF_OPERATOR_DATE);
				} else {
					String simpleLitMatch = getSimpleLiteralMatch(context,
							lVarString, rVarString, store);
					if (getOperator() == ERelationalOp.NOT_EQUAL) {
						t = store.getInstanceOf(NEQ_CAST_ALL);
					} else if (getOperator() == ERelationalOp.EQUAL) {
						t = store.getInstanceOf(EQ_CAST_ALL);
					} else {
						t = store.getInstanceOf(GT_CAST_ALL);
					}

					t.setAttribute("tiri", TypeMap.IRI_ID);
					t.setAttribute("tbn", TypeMap.BLANK_NODE_ID);
					t.setAttribute("ltype", (context.getVarMap()
							.get(lVarString).snd != null) ? context.getVarMap()
							.get(lVarString).snd : TypeMap.IRI_ID);
					t.setAttribute("rtype", (context.getVarMap()
							.get(rVarString).snd != null) ? context.getVarMap()
							.get(rVarString).snd : TypeMap.IRI_ID);
					t.setAttribute("nrstart",
							TypeMap.DATATYPE_NUMERICS_IDS_START);
					t.setAttribute("nrend", TypeMap.DATATYPE_NUMERICS_IDS_END);
					t.setAttribute("tstring", TypeMap.STRING_ID);
					t.setAttribute("tdatetime", TypeMap.DATETIME_ID);
					t.setAttribute("simpleLitMatch", simpleLitMatch);
				}
			}
		} else { // KAVITHA: -- Constant-constant expressions?
			t = store.getInstanceOf(RDF_OPERATOR);
		}
		assert t != null;

		t.setAttribute("left", left);
		t.setAttribute("right", right);
		t.setAttribute("operator", symbol);
		String lstr = getTypeToCastTo(leftExpression.getReturnType());
		String rstr = getTypeToCastTo(rightExpression.getReturnType());
		rstr = rstr == null ? lstr : rstr;
		lstr = lstr == null ? rstr : lstr;
		t.setAttribute("ltype", lstr);
		t.setAttribute("rtype", rstr);

		return t.toString();
	}

	private String handleRelationalsWithConstants(
			ConstantExpression constantExpression, Expression otherExpression,
			FilterContext context, ERelationalOp relationalOperator, Store store) {

		StringTemplate t = null;

		Short rexpType = constantExpression.getReturnType();
		String constantTypeString = constantExpression.visit(context, store);
		/*
		 * if (otherExpression instanceof BuiltinFunctionExpression) { if
		 * (((BuiltinFunctionExpression) otherExpression).getBuiltinType() ==
		 * EBuiltinType.LANG) constantTypeString = rexpType.toString(); }
		 */
		// special case booleans
		if (rexpType == TypeMap.BOOLEAN_ID) {
			return handleBooleanConstant(relationalOperator, otherExpression,
					constantTypeString, context, store).toString();

		} else if (needsCaseAndTypeCheck(otherExpression, constantExpression,
				context).fst == true) {
			return handleConstantsNeedingCase(
					needsCaseAndTypeCheck(otherExpression, constantExpression,
							context), rexpType, relationalOperator,
					otherExpression, constantExpression, context, store);
		}

		t = store.getInstanceOf(RDF_OPERATOR);
		String left = otherExpression.visit(context, store);
		String right = null;
		if (constantTypeString != null) {
			right = constantTypeString;
		} else {
			right = constantExpression.visit(context, store);
		}

		setRelationalAttributes(relationalOperator, left, right, t);
		return t.toString();

	}

	private void setRelationalAttributes(ERelationalOp relationalOperator,
			String left, String right, StringTemplate t) {
		t.setAttribute("left", left);
		t.setAttribute("right", right);
		t.setAttribute("operator",
				RelationalExpression.getOperatorSymbol(relationalOperator));
	}

	private Pair<Boolean, Boolean> needsCaseAndTypeCheck(
			Expression leftExpression, ConstantExpression rightExpression,
			FilterContext context) {
		boolean typeCheck = false; // if the right is a constant, we always need
									// a type check
		boolean caseNeeded = false;
		if (context.getPlanNode() == null) {
			return Pair.make(typeCheck, caseNeeded);
		}

		for (Variable v : leftExpression.getVariables()) {
			typeCheck = context.getPlanNode().isTypeCheckVariables(v).fst
					|| typeCheck;
			caseNeeded = context.getPlanNode().isTypeCheckVariables(v).snd
					|| caseNeeded;
		}
		// if the right expression is a constant and appears in a where clause,
		// we will need to add type as a constraint

		typeCheck = context.doesConstantNeedTypeCheck(rightExpression);

		return Pair.make(typeCheck, caseNeeded);
	}

	private String getSimpleLiteralMatch(FilterContext context,
			String lVarString, String rVarString, Store store) {
		StringTemplate t;
		t = store.getInstanceOf("STRING_SIMPLE_LIT_MATCH");
		t.setAttribute("simple_lit_type", TypeMap.SIMPLE_LITERAL_ID);
		t.setAttribute("string_type", TypeMap.STRING_ID);

		t.setAttribute("ltype",
				(context.getVarMap().get(lVarString).snd != null) ? context
						.getVarMap().get(lVarString).snd : TypeMap.IRI_ID);
		t.setAttribute("rtype",
				(context.getVarMap().get(rVarString).snd != null) ? context
						.getVarMap().get(rVarString).snd : TypeMap.IRI_ID);

		String simpleLitMatch = t.toString();
		return simpleLitMatch;
	}

	private String handleConstantsNeedingCase(
			Pair<Boolean, Boolean> needsCaseAndTypeCheck, Short rexpType,
			ERelationalOp relationalOperator, Expression otherExpression,
			ConstantExpression constantExpression, FilterContext context,
			Store store) {
		assert needsCaseAndTypeCheck.fst == true; // needing a type check is a
													// requirement if case is
													// turned on

		String left = otherExpression.visit(context, store);

		String right = null;

		String castType = null;
		if (rexpType == TypeMap.DATETIME_ID) {
			castType = TIMESTAMP_STR;
		} else if (rexpType == TypeMap.DATE_ID) {
			castType = DATE_STR;
		}
		StringTemplate t = getTemplate(needsCaseAndTypeCheck, castType,
				getTypeToCastTo(rexpType), store);

		if (castType != null && castType == TIMESTAMP_STR) {
			t.setAttribute("rhsNeedsCast", "true");
		}
		// KAVITHA: user defined types are not comparable, and only the equals
		// operator applies to them. If you see these, set the type constraint
		// to NONE
		// because nothing should match.
		if (relationalOperator != ERelationalOp.EQUAL
				&& rexpType >= TypeMap.USER_ID_START
				&& rexpType < TypeMap.NONE_ID) {
			rexpType = TypeMap.NONE_ID;
		}

		assert otherExpression instanceof VariableExpression;

		String typeCol = context.getVarMap().get(
				((VariableExpression) otherExpression).getVariable()).snd;
		t.setAttribute("typeTest", getTypeTest(rexpType, typeCol, store));

		right = constantExpression.getSQLConstantExpressionAsType(context,
				store);

		setRelationalAttributes(relationalOperator, left, right, t);

		return t.toString();
	}

	private String handleBooleanConstant(
			ERelationalOp relationalOperator, Expression otherExpression,
			String constantTypeString, FilterContext context, Store store) {
		String left = otherExpression.visit(context, store);
		boolean isBooleanExpr = otherExpression.getReturnType() == TypeMap.BOOLEAN_ID;
		boolean sense = constantTypeString.equalsIgnoreCase("'true'")
				|| constantTypeString.equals("'1'");
		if (isBooleanExpr) {
			if (sense == (relationalOperator == ERelationalOp.EQUAL)) {
				return left;
			} else {
				return "not( " + left + ")";
			}
		} else {
			StringTemplate t;
			if (sense) {
				if (relationalOperator == ERelationalOp.EQUAL)
					t = store.getInstanceOf(IS_TRUE);
				else
					t = store.getInstanceOf(IS_NOT_TRUE);
			} else {
				if (relationalOperator == ERelationalOp.EQUAL)
					t = store.getInstanceOf(IS_FALSE);
				else
					t = store.getInstanceOf(IS_NOT_FALSE);
			}
			t.setAttribute("value", left);
			return t.toString();
		}
	}

	private StringTemplate getTemplate(
			Pair<Boolean, Boolean> needsCaseAndTypeCheck, String castType,
			String typeToCastTo, Store store) {
		if (needsCaseAndTypeCheck.snd == true) {
			if (castType != null) {
				StringTemplate t = null;
				if (castType.equals(TIMESTAMP_STR)) {
					t = store.getInstanceOf(TYPE_CHECK_WITH_CAST_TIMESTAMP);
				} else if (castType.equals(DATE_STR)) {
					t = store.getInstanceOf(TYPE_CHECK_WITH_CAST_DATE);
				} else {
					t = store.getInstanceOf(TYPE_CHECK_WITH_CAST);
				}
				t.setAttribute("cast", castType);
				return t;
			} else {
				StringTemplate t = store.getInstanceOf(TYPE_CHECK_CASE);
				t.setAttribute("typeToCastTo", typeToCastTo);
				return t;
			}
		}
		StringTemplate t = store.getInstanceOf(TYPE_CHECK);
		t.setAttribute("typeToCastTo", typeToCastTo);
		return t;
	}
}
