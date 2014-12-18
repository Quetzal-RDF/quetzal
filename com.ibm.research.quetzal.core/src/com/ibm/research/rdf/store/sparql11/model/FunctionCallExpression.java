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
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.stringtemplate.StringTemplate;

import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.runtime.service.types.TypeMap;
import com.ibm.research.rdf.store.sparql11.sqlwriter.FilterContext;
import com.ibm.wala.util.collections.HashSetFactory;

/**
 * a function call expression
 */
public class FunctionCallExpression extends Expression {

	private FunctionCall call;
	private static final String RDF_STR = "RDF_STR";
	private static final String RDF_STRSTARTS_LIKE = "RDF_STRSTARTS_LIKE";
	private static final String RDF_STRSTARTS_LS_LIKE = "RDF_STRSTARTS_LS_LIKE";
	private static final String RDF_STRENDS_LIKE = "RDF_STRENDS_LIKE";
	private static final String RDF_STRENDS_LS_LIKE = "RDF_STRENDS_LS_LIKE";
	private static final String LS_TABLE = "long_strings_table";
	private static String TIMESTAMP_STR = "DATETIME";
	private static String DATE_STR="DATE";
	private static String FLOAT_STR = "FLOAT";
	private static String DOUBLE_STR = "DOUBLE";
	private static String INTEGER_STR = "INTEGER";
	private static String DECIMAL_STR = "DECIMAL";
	private static final String XSD_DATETIME_CAST = "XSD_DATETIME_CAST";
	private static final String XSD_DATE_CAST = "XSD_DATE_CAST";

	private static final String XSD_NUMERICS_CAST = "XSD_NUMERICS_CAST";
	private static final String XSD_BOOLEAN_CAST = "XSD_BOOLEAN_CAST";

	/**
	 * @param type
	 */
	public FunctionCallExpression(FunctionCall fc) {
		super(EExpressionType.FUNC_CALL);
		call = fc;
	}

	public FunctionCall getCall() {
		return call;
	}

	@Override
	public Short getReturnType() {
		return call.getReturnType();
	}

	public TypeMap.TypeCategory getTypeRestriction(Variable v) {

		if (call.getFunction().getValue().equals(FunctionCall.STARTS_WITH)
				|| call.getFunction().getValue().equals(FunctionCall.ENDS_WITH)) {
			return TypeMap.TypeCategory.STRING;
		} else if (call.getFunction().getValue()
				.equals(FunctionCall.XSD_DATETIME)) {
			return TypeMap.TypeCategory.DATETIME;
		} else if (call.getFunction().getValue()
				.equals(FunctionCall.XSD_DOUBLE)
				|| call.getFunction().getValue().equals(FunctionCall.XSD_FLOAT)
				|| call.getFunction().getValue()
						.equals(FunctionCall.XSD_DECIMAL)
				|| call.getFunction().getValue()
						.equals(FunctionCall.XSD_INTEGER)) {
			return TypeMap.TypeCategory.NUMERIC;
		}

		return TypeMap.TypeCategory.NONE;
	}

	public short getTypeEquality(Variable v) {
		return TypeMap.NONE_ID;
	}

	@Override
	public String toString() {
		return call.toString();
	}

	@Override
	public String getStringWithVarName() {
		return call.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((call == null) ? 0 : call.hashCode());
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
		FunctionCallExpression other = (FunctionCallExpression) obj;
		if (call == null) {
			if (other.call != null)
				return false;
		} else if (!call.equals(other.call))
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
		call.getFunction().rename(base, declared, internal);
		for (Expression e : call.getArguments())
			e.renamePrefixes(base, declared, internal);
	}

	@Override
	public void reverseIRIs() {
		for (Expression e : call.getArguments())
			e.reverseIRIs();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.research.rdf.store.sparql11.model.Expression#gatherBlankNodes()
	 */
	@Override
	public Set<BlankNodeVariable> gatherBlankNodes() {
		Set<BlankNodeVariable> ret = new HashSet<BlankNodeVariable>();
		for (Expression e : call.getArguments())
			ret.addAll(e.gatherBlankNodes());
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
		for (Expression e : call.getArguments())
			ret.addAll(e.gatherVariables());
		return ret;
	}

	@Override
	public Set<Variable> getVariables() {
		return Collections.emptySet();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.research.rdf.store.sparql11.model.Expression#traverse(com.ibm.research.rdf.store
	 * .sparql11.model.IExpressionTraversalListener)
	 */
	@Override
	public void traverse(IExpressionTraversalListener l) {
		l.startExpression(this);
		for (Expression e : call.getArguments())
			e.traverse(l);
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
		return call.hasCast(v);
	}

	@Override
	public String visit(FilterContext context, Store store) {
		FunctionCall function = getCall();
		IRI funcName = getCall().getFunction();
		List<Expression> argExps = function.getArguments();
		Iterator<Expression> iter = argExps.iterator();
		List<String> args = new ArrayList<String>();
		Expression e;
		String s = null;
		while (iter.hasNext()) {
			e = iter.next();
			if (e instanceof VariableExpression) {
				System.out.println("e= " + e + "\t e.var = "
						+ ((VariableExpression) e).getVariable());
				s = context.getVarMap().get(
						((VariableExpression) e).getVariable()).fst;
			} else if (e instanceof ConstantExpression) {
				s = ((ConstantExpression) e).toTypedString();
			} else {
				s = e.visit(context, store);
			}
			if (!s.equals(""))
				args.add(getSID(s, store.getMaxStringLen()));
		}
		StringTemplate t;
		if (funcName.getValue().compareTo(call.STARTS_WITH) == 0) {
			if (store.isLongString()) {
				t = store.getInstanceOf(RDF_STRSTARTS_LS_LIKE);
				t.setAttribute(LS_TABLE, store.getLongStrings());
			} else {
				t = store.getInstanceOf(RDF_STRSTARTS_LIKE);

			}
			t.setAttribute("var", args.get(0));
			t.setAttribute("value", args.get(1));

			return t.toString();
		} else if (funcName.getValue().compareTo(call.ENDS_WITH) == 0) {
			if (store.isLongString()) {
				t = store.getInstanceOf(RDF_STRENDS_LS_LIKE);
				t.setAttribute(LS_TABLE, store.getLongStrings());
			} else {
				t = store.getInstanceOf(RDF_STRENDS_LIKE);

			}

			t.setAttribute("var", args.get(0));
			t.setAttribute("value", args.get(1));

			return t.toString();

		} else if (funcName.getValue().compareTo(call.XSD_STRING) == 0) {
			t = store.getInstanceOf(RDF_STR);
			t.setAttribute("args", args);
			return t.toString();
		} else if (funcName.getValue().equals(call.XSD_DATETIME)) {
			Set<Short> validTypes = HashSetFactory.make();
			validTypes.add(TypeMap.DATETIME_ID);
			validTypes.add(TypeMap.SIMPLE_LITERAL_ID);
			validTypes.add(TypeMap.STRING_ID);
			return handleNumericAndDateTimeCasts(context, function, args,
					validTypes, store.getDatatype("xs_dateTime"),
					TIMESTAMP_STR, store);
		} else if (funcName.getValue().equals(call.XSD_BOOLEAN)) {
			t = store.getInstanceOf(XSD_BOOLEAN_CAST);
			String arg;
			if (args.size() > 0) {
				arg = function.getArguments().get(0).visit(context, store);
			} else {
				arg = "false";
			}

			Expression expr = function.getArguments().get(0);
			if (expr instanceof VariableExpression) {
				String v = ((VariableExpression) expr).getVariable();
				if (context.getVarMap().containsKey(v)
						&& context.getVarMap().get(v).snd != null) {
					t.setAttribute("arg", arg);
					t.setAttribute("type", context.getVarMap().get(v).snd);
				}
			} else {
				t.setAttribute("arg", expr.visit(context, store));
				t.setAttribute("type", expr.getReturnType());
			}
			t.setAttribute("nrstart", TypeMap.DATATYPE_NUMERICS_IDS_START);
			t.setAttribute("nrend", TypeMap.DATATYPE_NUMERICS_IDS_END);
			t.setAttribute("simpleLit", TypeMap.SIMPLE_LITERAL_ID);
			t.setAttribute("string", TypeMap.STRING_ID);
			t.setAttribute("boolean", TypeMap.BOOLEAN_ID);

			return t.toString();
		} else if (funcName.getValue().equals(TypeMap.INTEGER_IRI)) {			
			return handleNumericAndDateTimeCasts(context, function, args,
					null, store.getDatatype("xs_integer"), INTEGER_STR,
					store);
		} else if (funcName.getValue().equals(TypeMap.DOUBLE_IRI)) {
			return handleNumericAndDateTimeCasts(context, function, args,
					null, store.getDatatype("xs_double"), DOUBLE_STR,
					store);
		} else if (funcName.getValue().equals(TypeMap.FLOAT_IRI)) {

			return handleNumericAndDateTimeCasts(context, function, args,
					null, store.getDatatype("xs_float"), FLOAT_STR, store);
		} else if (funcName.getValue().equals(TypeMap.DECIMAL_IRI)) {
			return handleNumericAndDateTimeCasts(context, function, args,
					null, store.getDatatype("xs_decimal"), DECIMAL_STR,
					store);
		}
		return "";
	}



	private String handleNumericAndDateTimeCasts(FilterContext context,
			FunctionCall function, List<String> args, Set<Short> validTypes,
			String xmlType, String DBType, Store store) {
		
		StringTemplate t = null;
		if (DBType.equals(TIMESTAMP_STR)) {
			t = store.getInstanceOf(XSD_DATETIME_CAST);
		} else if (DBType.equals(DATE_STR)){
			t = store.getInstanceOf(XSD_DATE_CAST);
		} else {
			t = store.getInstanceOf(XSD_NUMERICS_CAST);
		}
		Expression argExp = function.getArguments().get(0);
		String arg = null;
		if (args.size() > 0) {
			arg = function.getArguments().get(0).visit(context, store);
		}

		assert arg != null;

		Set<String> typesToCheck = HashSetFactory.make();
		if (argExp instanceof VariableExpression) {
			String typ = context.getVarMap().containsKey(
					((VariableExpression) argExp).getVariable()) ? context
					.getVarMap().get(
							((VariableExpression) argExp).getVariable()).snd
					: null;
			if (typ == null) {
				typ = Integer.toString(TypeMap.IRI_ID);
			}
			if (validTypes != null) {
				for (Short type : validTypes) {
					typesToCheck.add(typ + "=" + Integer.toString(type));
				}
			} else {
				// if validTypes is null then this is a numeric, so just test a range
				typesToCheck.add("(" + typ + " >= " + TypeMap.DATATYPE_NUMERICS_IDS_START + " AND " + typ + " <= " + TypeMap.DATATYPE_NUMERICS_IDS_END + ")");
				typesToCheck.add(typ + "=" + TypeMap.SIMPLE_LITERAL_ID);
				typesToCheck.add(typ + "=" + TypeMap.STRING_ID);
				typesToCheck.add(typ + "=" + TypeMap.BOOLEAN_ID);
			}
			t.setAttribute("value", arg);
		} else {
			short ret = argExp.getReturnType();

			if (validTypes != null && !validTypes.contains(ret)) {
				typesToCheck.add(" 0 = 1 ");
			}
			t.setAttribute("value", arg);
		}
		t.setAttribute("typeMatch", typesToCheck);
		t.setAttribute("xmlType", xmlType);
		assert store.getDatatype(DBType) != null : DBType + " not found";
		t.setAttribute("DBType", store.getDatatype(DBType));
		return t.toString();
	}

}
