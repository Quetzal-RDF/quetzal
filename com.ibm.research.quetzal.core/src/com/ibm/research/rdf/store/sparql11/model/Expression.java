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

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.antlr.stringtemplate.StringTemplate;

import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.config.Constants;
import com.ibm.research.rdf.store.hashing.HashingException;
import com.ibm.research.rdf.store.hashing.HashingHelper;
import com.ibm.research.rdf.store.runtime.service.types.TypeMap;
import com.ibm.research.rdf.store.sparql11.sqlwriter.FilterContext;
import com.ibm.wala.util.collections.Pair;

/**
 * Base class for all expressions
 */
public abstract class Expression implements ExpressionVisitor {
	public static String BETWEEN_TYPE_TEST = "BETWEEN_TYPE_TEST";
	public static final String EBV = "RDF_EBV";

	public static enum EExpressionType {
		CONSTANT, BUILTIN_FUNC, VAR, FUNC_CALL, UNARY, AND, OR, RELATIONAL, ADDITIVE, MULTIPLICATIVE, NUMERIC, AGGREGATE, ONE_OF, UNDEF
	};

	public static enum EConstantType {
		LITERAL, IRI, INTEGER, DECIMAL, DOUBLE, BOOL
	};

	public enum EBuiltinType implements Visitor {
		REGEX {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				return getSQLForLocateRegex(exp, context, store, "RDF_REGEX");
			}
		},
		STR {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				return getSQLForSingleArgFunctions(exp, context, store,
						"RDF_STR");
			}
		},
		STRSTARTS {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				return getSQLForStringOps(exp, context, store, "RDF_STRSTARTS");
			}
		},
		STRENDS {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				return getSQLForStringOps(exp, context, store, "RDF_STRENDS");
			}
		},
		STRBEFORE {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				return getSQLForStringOps(exp, context, store, "RDF_STRBEFORE");
			}
		},
		STRAFTER {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				return getSQLForStringOps(exp, context, store, "RDF_STRAFTER");
			}
		},
		LANG {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {

				Expression e = ((BuiltinFunctionExpression) exp).getArguments()
						.iterator().next();
				StringTemplate t = store.getInstanceOf("RDF_LANG");
				String fType = null;

				if (e instanceof ConstantExpression) {
					t = store.getInstanceOf("RDF_LANG_CONST");
					String langArg = ((ConstantExpression) e).getConstant()
							.toDataString();
					t.setAttribute("args", langArg);
				} else if (e instanceof VariableExpression) {
					t = store.getInstanceOf("RDF_LANG_VAR");
					t.setAttribute("dttable", store.getDataTypeTable());
					String varArg = ((VariableExpression) e).getVariable();
					String langArg = context.getVarMap().get(varArg).snd;
					t.setAttribute("langid", langArg);
					t.setAttribute("iri", TypeMap.IRI_ID);
					t.setAttribute("blankNode", TypeMap.BLANK_NODE_ID);
					t.setAttribute("startOfLangLiterals", TypeMap.LANG_START);
					t.setAttribute("endOfLangLiterals", TypeMap.LANG_END);
				}
				System.out.println(">>> " + t);
				return t.toString();
			}
		},
		LANGMATCHES {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				Iterator<Expression> iter = ((BuiltinFunctionExpression) exp)
						.getArguments().iterator();
				Expression e = iter.next();
				StringTemplate t = store.getInstanceOf("RDF_LANGMATCHES");

				if (e instanceof BuiltinFunctionExpression) {
					String fTerm = e.visit(context, store);
					t.setAttribute("ftype", fTerm);
				}
				e = iter.next();
				if (e instanceof ConstantExpression) {
					String constantData = ((ConstantExpression) e)
							.getConstant().toDataString();

					StringBuffer inClause = new StringBuffer();
					inClause.append("SELECT LOWER(");
					inClause.append(Constants.NAME_COLUMN_DATATYPE_NAME);
					inClause.append(") FROM ");
					inClause.append(store.getDataTypeTable());
					inClause.append(" WHERE ");
					inClause.append(Constants.NAME_COLUMN_DATATYPE_ID);
					inClause.append(" BETWEEN ");
					inClause.append(TypeMap.LANG_IDS_START);
					inClause.append(" AND ");
					inClause.append(TypeMap.LANG_IDS_END);
					if (!constantData.equals("*")) {
						inClause.append(" AND ");
						inClause.append(Constants.NAME_COLUMN_DATATYPE_NAME);
						inClause.append(" LIKE '");
						inClause.append(constantData.toUpperCase());
						inClause.append("%'");
					}
					t.setAttribute("rstart", inClause);
				}
				return t.toString();
			}

		},
		DATATYPE {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				Expression e = ((BuiltinFunctionExpression) exp).getArguments()
						.iterator().next();
				StringTemplate t = store.getInstanceOf("RDF_DATATYPE");
				t.setAttribute("iri", TypeMap.IRI_ID);
				t.setAttribute("blankNode", TypeMap.BLANK_NODE_ID);
				t.setAttribute("plainLiteral", TypeMap.SIMPLE_LITERAL_ID);
				t.setAttribute("startOfLangLiterals", TypeMap.LANG_START);
				t.setAttribute("endOfLangLiterals", TypeMap.LANG_END);

				if (e instanceof ConstantExpression) {
					return TypeMap.getTypedString(e.getReturnType());
				}
				if (e instanceof VariableExpression) {
					return handleDataTypeForVariable(context, e, t, store);
				} else if (e instanceof NumericExpression) {

					List<String> types = new LinkedList<String>();
					getExpressionType(e, types, context);
					assert !types.isEmpty();
					types.add(Integer.toString(TypeMap.INTEGER_ID));

					StringTemplate nt = store
							.getInstanceOf("RDF_DATATYPE_NUMERIC_EXPRESSION");
					nt.setAttribute("types", types);
					String typeCol = nt.toString();
					t.setAttribute("typeCol", typeCol);
					t.setAttribute("dtTableName", store.getDataTypeTable());

					return t.toString();
				}

				// all other expressions
				t = store.getInstanceOf("RDF_DATATYPE_EXPRESSION");
				t.setAttribute("var", e.visit(context, store));
				short retType = e.getReturnType();
				String str = TypeMap.getTypedString(retType);
				String xmlSchemaType = store.getDatatype(TypeMap
						.getCastNameForTypedString(str));
				t.setAttribute("xmlSchemaType", xmlSchemaType);
				t.setAttribute("returnType", str);

				return t.toString();
			}

			private void getExpressionType(Expression e, List<String> types,
					FilterContext context) {
				if (e instanceof VariableExpression) {
					String leftVar = ((VariableExpression) e).getVariable();
					String varType = context.getVarMap().get(leftVar).snd;
					if (varType == null) {
						varType = TypeMap.IRI_ID + "";
					}
					types.add(varType);
				} else if (e instanceof ConstantExpression) {
					types.add(Integer.toString(e.getReturnType()));
				} else if (e instanceof NumericExpression) {
					getExpressionType(((NumericExpression) e).getLHS(), types,
							context);
					getExpressionType(((NumericExpression) e).getRHS(), types,
							context);
				} else if (e instanceof LongExpression<?>) {
					for (Expression sub : ((LongExpression<?>) e)
							.getSubExpressions()) {
						getExpressionType(sub, types, context);
					}
				} else if (e.getReturnType() >= TypeMap.DATATYPE_NUMERICS_IDS_START
						&& e.getReturnType() <= TypeMap.DATATYPE_NUMERICS_IDS_END) {
					for (Variable v : e.gatherVariables()) {
						String varType = context.getVarMap().get(v).snd;
						if (varType == null) {
							varType = TypeMap.IRI_ID + "";
						}
						types.add(varType);
					}
				}
			}

			private String handleDataTypeForVariable(FilterContext context,
					Expression e, StringTemplate t, Store store) {
				String fType;
				String var = ((VariableExpression) e).getVariable();
				fType = context.getVarMap().containsKey(var) ? context
						.getVarMap().get(var).snd : null;
				if (fType == null) {
					return TypeMap.IRI_TYPE_IRI;
				}
				t.setAttribute("typeCol", fType);
				t.setAttribute("dtTableName", store.getDataTypeTable());
				return t.toString();
			}
		},
		BOUND {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				return super.visit(store, context, exp, "RDF_BOUND");
			}
		},
		SAMETERM {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				String fTerm = null;
				String fType = null;
				String sTerm = null;
				String sType = null;
				Iterator<Expression> iter = ((BuiltinFunctionExpression) exp)
						.getArguments().iterator();
				Expression e = iter.next();
				String s = e.visit(context, store);
				fTerm = s;
				Set<Variable> variables = e.gatherVariables();
				assert (variables.size() < 2);
				if (variables.size() == 0)
					fType = e.getReturnType().toString();
				else {
					for (Variable v : variables)
						fType = context.getVarMap().get(v.getName()).snd;
					if (fType == null)
						fType = TypeMap.IRI_ID + "";
				}
				e = iter.next();
				s = e.visit(context, store);
				// sTerm="'" + getSID(s.substring(1, s.length() - 1),
				// store.getMaxStringLen()) + "'";
				sTerm = s;
				variables = e.gatherVariables();
				assert (variables.size() < 2);
				if (variables.size() == 0)
					sType = e.getReturnType().toString();
				else {
					for (Variable v : variables)
						sType = context.getVarMap().get(v.getName()).snd;
					if (sType == null)
						sType = TypeMap.IRI_ID + "";
				}
				StringTemplate t = store.getInstanceOf("RDF_SAMETERM");
				t.setAttribute("fterm", fTerm);
				t.setAttribute("ftype", fType);
				t.setAttribute("sterm", sTerm);
				t.setAttribute("stype", sType);
				return t.toString();

			}
		},
		ISIRI {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				String fType = null;

				Expression e = ((BuiltinFunctionExpression) exp).getArguments()
						.iterator().next();
				for (Variable v : e.gatherVariables()) {
					fType = context.getVarMap().get(v.getName()).snd;
				}
				if (fType == null)
					fType = TypeMap.IRI_ID + "";
				StringTemplate t = store.getInstanceOf("RDF_ISIRI");
				t.setAttribute("type", fType);
				t.setAttribute("iri_type", TypeMap.IRI_ID);
				return t.toString();
			}
		},
		ISBLANK {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				return super.visit(store, context, exp, "RDF_ISBLANK");
			}
		},
		ISLITERAL {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				String fType = null;
				Expression e = ((BuiltinFunctionExpression) exp).getArguments()
						.iterator().next();
				for (Variable v : e.gatherVariables()) {
					fType = context.getVarMap().get(v.getName()).snd;
				}
				if (fType == null)
					fType = TypeMap.IRI_ID + "";
				StringTemplate t = store.getInstanceOf("RDF_ISLITERAL");
				t.setAttribute("type", fType);
				t.setAttribute("iri", TypeMap.IRI_ID);
				t.setAttribute("blankNode", TypeMap.BLANK_NODE_ID);
				return t.toString();
			}
		},
		ISNUMERIC {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				Expression e = ((BuiltinFunctionExpression) exp).getArguments()
						.iterator().next();
				StringTemplate t = store.getInstanceOf("RDF_ISNUMERIC");

				if (e instanceof VariableExpression) {
					String var = ((VariableExpression) e).getVariable();
					String funcArgs = context.getVarMap().get(var).fst;
					t.setAttribute("arg", funcArgs);
				}
				return t.toString();
			}
		},
		EXISTS {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				return getSQLForExists((BuiltinFunctionExpression) exp,
						context, store.getInstanceOf("RDF_EXISTS"));
			}
		},
		NOT_EXISTS {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				return getSQLForExists((BuiltinFunctionExpression) exp,
						context, store.getInstanceOf("RDF_EXISTS"));
			}
		},
		CONTAINS {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				return getSQLForLocateRegex(exp, context, store, "LOCATE");
			}
		},
		IRI {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				String str = null;
				Expression e = ((BuiltinFunctionExpression) exp).getArguments()
						.iterator().next();
				if (e instanceof ConstantExpression) {
					str = ((ConstantExpression) e).getConstant().toDataType()
							+ "";
				} else {
					str = e.visit(context, store);
				}
				StringTemplate t = store.getInstanceOf("RDF_IRI");
				t.setAttribute("string", str);
				return t.toString();
			}
		},
		BNODE {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				return super.visit(store, context, exp, "RDF_BOUND");

			}
		},
		RAND {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				return store.getInstanceOf("RDF_RAND").toString();
			}
		},
		ABS {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				return getSQLForSingleArgFunctions(exp, context, store,
						"RDF_ABS");
			}
		},
		CEIL {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				return getSQLForSingleArgFunctions(exp, context, store,
						"RDF_CEIL");

			}
		},
		FLOOR {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				return getSQLForSingleArgFunctions(exp, context, store,
						"RDF_FLOOR");

			}
		},
		ROUND {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				return getSQLForSingleArgFunctions(exp, context, store,
						"RDF_ROUND");
			}
		},
		CONCAT {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				Iterator<Expression> iter = ((BuiltinFunctionExpression) exp)
						.getArguments().iterator();
				Expression e = iter.next();
				String lhs = e.visit(context, store);
				
				while (iter.hasNext()) {
					StringTemplate t = store.getInstanceOf("RDF_CONCAT");
					t.setAttribute("lexpr", lhs);
					t.setAttribute("rexpr", iter.next().visit(context, store));
					lhs = t.toString();
				}
				
				return lhs;
			}
		},
		SUB_STRING_EXP {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				Iterator<Expression> iter = ((BuiltinFunctionExpression) exp)
						.getArguments().iterator();
				StringTemplate t = store.getInstanceOf("RDF_SUB_STRING_EXP");
				Expression e = iter.next();
				if (e instanceof VariableExpression) {
					String var = ((VariableExpression) e).getVariable();
					String funcArgs = context.getVarMap().get(var).fst;
					t.setAttribute("string_var", funcArgs);
				}
				e = iter.next();
				if (e instanceof ConstantExpression) {
					String startPos = ((ConstantExpression) e).getConstant()
							.toDataString();
					t.setAttribute("startPos", startPos);
				}
				if (iter.hasNext()) {
					e = iter.next();
					if (e instanceof ConstantExpression) {
						String endPos = ((ConstantExpression) e).getConstant()
								.toDataString();
						t.setAttribute("endPos", endPos);
					}
				} else {
					t.setAttribute("endPos", null);
				}
				return t.toString();
			}
		},
		STRLEN {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				return getSQLForSingleArgFunctions(exp, context, store,
						"RDF_STRLEN");
			}
		},
		UCASE {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				return getSQLForSingleArgFunctions(exp, context, store,
						"RDF_UCASE");
			}
		},
		LCASE {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				return getSQLForSingleArgFunctions(exp, context, store,
						"RDF_LCASE");

			}
		},
		ENCODE_FOR_URI {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				return super.visit(store, context, exp, "RDF_ENCODE_FOR_URI");

			}
		},
		YEAR {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				return getSQLForSingleArgFunctions(exp, context, store,
						"RDF_YEAR");

			}
		},
		MONTH {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				return getSQLForSingleArgFunctions(exp, context, store,
						"RDF_MONTH");

			}
		},
		DAY {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				return getSQLForSingleArgFunctions(exp, context, store,
						"RDF_DAY");

			}
		},
		HOURS {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				return getSQLForSingleArgFunctions(exp, context, store,
						"RDF_HOURS");

			}
		},
		STRLANG {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				//
				// first argument is the string to form
				//
				Iterator<Expression> iter = ((BuiltinFunctionExpression) exp)
						.getArguments().iterator();
				StringTemplate t = store.getInstanceOf("RDF_STRLANG");
				Expression e = iter.next();
				if (e instanceof VariableExpression) {
					String var = ((VariableExpression) e).getVariable();
					String lexform = context.getVarMap().get(var).fst;
					t.setAttribute("lexform", lexform);
				} else if (e instanceof ConstantExpression) {
					String constExpr = ((ConstantExpression) e).getConstant()
							.toDataString();
					t.setAttribute("lexform", new String("'" + constExpr + "'"));
				} else if (e instanceof BuiltinFunctionExpression) {
					String lexform = e.visit(context, store);
					t.setAttribute("lexform", lexform);
				}
				//
				// Second argument is the language tag
				//
				e = iter.next();
				if (e instanceof ConstantExpression) {
					String lang = ((ConstantExpression) e).getConstant()
							.toDataString();
					t.setAttribute("lang", lang);
				}
				return t.toString();
			}
		},
		STRDT {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				//
				// first argument is the string to form
				//
				//
				// first argument is the string to form
				//
				Iterator<Expression> iter = ((BuiltinFunctionExpression) exp)
						.getArguments().iterator();
				StringTemplate t = store.getInstanceOf("RDF_STRDT");
				Expression e = iter.next();
				if (e instanceof VariableExpression) {
					String var = ((VariableExpression) e).getVariable();
					String lexform = context.getVarMap().get(var).fst;
					t.setAttribute("lexform", lexform);
				} else if (e instanceof ConstantExpression) {
					String constExpr = ((ConstantExpression) e).getConstant()
							.toDataString();
					t.setAttribute("lexform", new String("'" + constExpr + "'"));
				} else if (e instanceof BuiltinFunctionExpression) {
					String lexform = e.visit(context, store);
					t.setAttribute("lexform", lexform);
				}
				//
				// Second argument is the type iri
				//
				e = iter.next();
				if (e instanceof ConstantExpression) {
					String typeiri = ((ConstantExpression) e).getConstant()
							.toDataString();
					t.setAttribute("typeiri", typeiri);
				}
				return t.toString();
			}
		},
		MINUTES {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				return getSQLForSingleArgFunctions(exp, context, store,
						"RDF_MINUTES");

			}
		},
		SECONDS {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				return getSQLForSingleArgFunctions(exp, context, store,
						"RDF_SECONDS");
			}
		},
		TIMEZONE {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				return getSQLForSingleArgFunctions(exp, context, store,
						"RDF_TIMEZONE");
			}
		},
		TZ {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				return getSQLForSingleArgFunctions(exp, context, store,
						"RDF_TZ");

			}
		},
		STRUUID {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				return super.visit(store, context, exp, "RDF_STRUUID");

			}
		},
		UUID {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				return super.visit(store, context, exp, "RDF_UUID");

			}
		},
		NOW {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				return store.getInstanceOf("RDF_NOW").toString();

			}
		},
		REPLACE {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				Iterator<Expression> iter = ((BuiltinFunctionExpression) exp)
						.getArguments().iterator();
				StringTemplate t = store.getInstanceOf("RDF_REPLACE");
				Expression e = iter.next();

				if (e instanceof VariableExpression) {
					String var = ((VariableExpression) e).getVariable();
					String funcArgs = context.getVarMap().get(var).fst;
					t.setAttribute("var", funcArgs);
				}
				//
				// .. or the result of a nested function
				//
				else if (e instanceof BuiltinFunctionExpression) {
					String var = e.visit(context, store);
					t.setAttribute("var", var);
				}
				//
				// the second arguments is the pattern, where we should get rid
				// of the double-quotes at the beginning/end
				//
				e = iter.next();
				if (e instanceof ConstantExpression) {
					String pat = ((ConstantExpression) e).getConstant()
							.toDataString();
					t.setAttribute("pat", pat);
				}
				//
				// the third arguments is the replacing text, where again we
				// should get rid of the double-quotes at the beginning/end
				//
				e = iter.next();
				if (e instanceof ConstantExpression) {
					String rep = ((ConstantExpression) e).getConstant()
							.toDataString();
					t.setAttribute("rep", rep);
				}
				return t.toString();

			}
		},
		MD5 {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				return getSQLForEncryptionFunctions(exp, context, store,
						"RDF_MD5", null);
			}
		},
		SHA1 {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				return getSQLForEncryptionFunctions(exp, context, store,
						"RDF_SHA", "SHA1");
			}
		},
		SHA256 {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				return getSQLForEncryptionFunctions(exp, context, store,
						"RDF_SHA", "SHA256");
			}
		},
		SHA384 {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				return getSQLForEncryptionFunctions(exp, context, store,
						"RDF_SHA", "SHA384");
			}
		},
		SHA512 {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				return getSQLForEncryptionFunctions(exp, context, store,
						"RDF_SHA", "SHA512");

			}
		},
		IF {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				StringTemplate t = store.getInstanceOf("RDF_IF");
				Set<TypeMap.TypeCategory> types = new HashSet<TypeMap.TypeCategory>();
				for (Expression arg : ((BuiltinFunctionExpression) exp)
						.getArguments()) {
					types.add(TypeMap.getTypeCategory(arg.getReturnType()));
				}
				TypeMap.TypeCategory castType = TypeMap
						.getCastTypeCategory(types);

				Iterator<Expression> iter = ((BuiltinFunctionExpression) exp)
						.getArguments().iterator();
				Expression e = iter.next();
				if (e.getReturnType() == TypeMap.NONE_ID) {
					t.setAttribute("type1", castType);
				}
				t.setAttribute("expr1", e.visit(context, store));

				e = iter.next();
				if (e.getReturnType() == TypeMap.NONE_ID) {
					t.setAttribute("type2", castType);
				}
				t.setAttribute("expr2", e.visit(context, store));

				e = iter.next();
				if (e.getReturnType() == TypeMap.NONE_ID) {
					t.setAttribute("type3", castType);
				}
				t.setAttribute("expr3", e.visit(context, store));

				return t.toString();
			}
		},
		COALESCE {
			@Override
			public String visit(Store store, FilterContext context,
					Expression exp) {
				StringTemplate t = store.getInstanceOf("RDF_COALESCE");
				Iterator<Expression> iter = ((BuiltinFunctionExpression) exp)
						.getArguments().iterator();

				Expression e = iter.next();
				t.setAttribute("expr1", e.visit(context, store));
				e = iter.next();
				t.setAttribute("expr2", e.visit(context, store));

				return t.toString();
			}
		};

		public String visit(Store store, FilterContext context, Expression exp,
				String type) {
			Iterator<Expression> iter = ((BuiltinFunctionExpression) exp)
					.getArguments().iterator();
			List<String> args = new LinkedList<String>();
			while (iter.hasNext()) {
				String s = iter.next().visit(context, store);
				if (!s.equals("")) {
					args.add(s);
				}
			}
			StringTemplate t = store.getInstanceOf(type);
			t.setAttribute("args", args);
			return t.toString();
		}

		private static String getSQLForExists(BuiltinFunctionExpression exp,
				FilterContext context, StringTemplate t) {
			t.setAttribute("right_target", context.getRightTarget());
			t.setAttribute("exists_constraints", context.getExistsConstraints());
			t.setAttribute("is_negated", context.getIsNegated());
			return t.toString();
		}

		private static String getSQLForLocateRegex(Expression exp,
				FilterContext context, Store store, String type) {
			StringTemplate t = store.getInstanceOf(type);
			boolean isFirst = true && store.getHasLongStrings() == 1;
			String sqlVarName = null;
			String pattern = null;
			String flag = "";
			int argPosition = 0;
			Iterator<Expression> iter = ((BuiltinFunctionExpression) exp)
					.getArguments().iterator();
			while (iter.hasNext()) {
				Expression e = iter.next();
				String s = e.visit(context, store);
				boolean isRegex = false;
				if (isFirst) {
					if (s.startsWith("COALESCE")) {
						s = "COALESCE(" + Constants.NAME_LONG_STRING_TABLE
								+ "." + Constants.NAME_COLUMN_LONG_STRING
								+ ", " + s + ")";
					} else if (!s
							.equals(((VariableExpression) e).getVariable())) {
						s = "COALESCE(" + Constants.NAME_LONG_STRING_TABLE
								+ "." + Constants.NAME_COLUMN_LONG_STRING
								+ ", " + ((VariableExpression) e).getVariable()
								+ ")";
					}
					isFirst = false;
				}
				if (argPosition == 0)
					sqlVarName = s;
				if (argPosition == 1)
					pattern = (s.startsWith("'")) ? s.substring(1,
							s.length() - 1) : s;
				if (argPosition == 2)
					flag = (s.startsWith("'")) ? s.substring(1, s.length() - 1)
							: s;
				argPosition++;
			}

			t = null;
			if (Pattern.matches("^[a-zA-Z0-9]+$", pattern)
					|| type.equals("LOCATE")) {
				if ("i".equals(flag)) {
					t = store.getInstanceOf("LOCATE_LCASE");
					t.setAttribute("string_var", sqlVarName);
					t.setAttribute("pattern", pattern);
				} else if (flag == null || "".equals(flag)) {
					t = store.getInstanceOf("LOCATE");
					t.setAttribute("string_var", sqlVarName);
					t.setAttribute("pattern", pattern);
				}
			} else if (Pattern.matches("^\\^[a-zA-Z0-9]+$", pattern)
					&& !flag.contains("i")) {
				t = store.getInstanceOf("RDF_STRSTARTS_LIKE");
				t.setAttribute("var", sqlVarName);
				t.setAttribute("value", pattern.substring(1));
				t.setAttribute("long_strings_table", "bogus");
			}

			if (t == null) {
				t = store.getInstanceOf(type);
				t.setAttribute("regex_var", sqlVarName);
				// KAVITHA: DB2 does not like double "\\" escape sequences in
				// the pattern!
				String p = pattern.replaceAll("\\\\\\\\", "\\\\");

				t.setAttribute("regex_pattern", p);
				t.setAttribute("regex_flag", flag);
				List<String> typeCheck = null;
				Expression e1 = ((BuiltinFunctionExpression) exp)
						.getArguments().get(0);
				if (e1.getReturnType() != TypeMap.STRING_ID
						&& e1.getReturnType() != TypeMap.SIMPLE_LITERAL_ID) {
					typeCheck = getStringTypeCheckSQL(exp, context.getVarMap());
				}
				t.setAttribute("stringTypes", typeCheck);

			}
			return t.toString();
		}

		private static List<String> getStringTypeCheckSQL(
				Expression expression, Map<String, Pair<String, String>> varMap) {
			List<String> types = new LinkedList<String>();
			// KAVITHA THIS IS TEMP -- we can use the BETWEEN TYPE TEST once
			// strings and plain lits
			// are consecutive
			for (Variable v : expression.gatherVariables()) {
				types.add(varMap.get(v.getName()).snd + "=" + TypeMap.STRING_ID);
				types.add(varMap.get(v.getName()).snd + "="
						+ TypeMap.SIMPLE_LITERAL_ID);
			}
			return types;
		}

		private static String getSQLForSingleArgFunctions(Expression exp,
				FilterContext context, Store store, String type) {
			Expression e = ((BuiltinFunctionExpression) exp).getArguments()
					.iterator().next();
			StringTemplate t = store.getInstanceOf(type);
			if (e instanceof VariableExpression) {
				String var = ((VariableExpression) e).getVariable();
				String absType = context.getVarMap().containsKey(var) ? context.getVarMap().get(var).fst : var;
				t.setAttribute("args", absType);
			}
			return t.toString();
		}

		public static String getSQLForStringOps(Expression exp,
				FilterContext context, Store store, String type) {
			//
			// first argument is the string to check, either a variable
			//
			Iterator<Expression> iter = ((BuiltinFunctionExpression) exp)
					.getArguments().iterator();
			StringTemplate t = store.getInstanceOf(type);
			Expression e = iter.next();
			if (e instanceof VariableExpression) {
				String var = ((VariableExpression) e).getVariable();
				String funcArgs = context.getVarMap().get(var).fst;
				t.setAttribute("var", funcArgs);
			}
			//
			// .. or the result of a nested function
			//
			else if (e instanceof BuiltinFunctionExpression) {
				String var = e.visit(context, store);
				t.setAttribute("var", var);
			}
			//
			// the second arguments is the length we are looking for
			//
			e = iter.next();
			if (e instanceof ConstantExpression) {
				String value = ((ConstantExpression) e).getConstant()
						.toDataString();
				t.setAttribute("value", value);
			}
			return t.toString();
		}

		private static String getSQLForEncryptionFunctions(Expression exp,
				FilterContext context, Store store, String type, String function) {
			Expression e = ((BuiltinFunctionExpression) exp).getArguments()
					.iterator().next();
			StringTemplate t = store.getInstanceOf(type);
			t.setAttribute("args", e.visit(context, store));
			if (function != null) {
				t.setAttribute("function", function);
			}
			return t.toString();
		}
	};

	public static enum EFunctionType {
		STARTS_WITH, ENDS_WITH, DATETIME
	};

	public static enum EUnaryOp {
		MINUS, NOT
	};

	public enum ERelationalOp {
		EQUAL, NOT_EQUAL, LESS, GREATER, LESS_EQUAL, GREATER_EQUAL
	};

	public static enum EAdditiveOp {
		PLUS, MINUS
	};

	public static enum EMultiplicativeOp {
		TIMES, DIVIDE
	};

	public static enum EOneOfOp {
		IN, NOT_IN
	};

	public List<Expression> getSubExpressions() {
		return Collections.emptyList();
	}

	private EExpressionType type;

	public Expression(EExpressionType type) {
		this.type = type;
	}

	public EExpressionType getType() {
		return type;
	}

	public abstract Short getReturnType();

	public abstract short getTypeEquality(Variable v);

	public abstract TypeMap.TypeCategory getTypeRestriction(Variable v);

	public abstract boolean containsCast(Variable v);

	public abstract boolean containsEBV();

	public abstract boolean containsBound();

	public abstract boolean containsNotBound();

	public abstract void renamePrefixes(String base,
			Map<String, String> declared, Map<String, String> internal);

	public abstract void reverseIRIs();

	public abstract Set<BlankNodeVariable> gatherBlankNodes();

	public abstract Set<Variable> gatherVariables();

	public abstract Set<Variable> getVariables();

	public abstract void traverse(IExpressionTraversalListener l);

	public abstract String getStringWithVarName();

	protected List<String> getNumericTypeCheckSQL(FilterContext context,
			Store store) {
		List<String> types = new LinkedList<String>();
		for (Variable v : this.gatherVariables()) {
			StringTemplate t = store.getInstanceOf(BETWEEN_TYPE_TEST);
			t.setAttribute("typeCol", context.getVarMap().get(v.getName()).snd);
			t.setAttribute("start", TypeMap.DATATYPE_NUMERICS_IDS_START);
			t.setAttribute("end", TypeMap.DATATYPE_NUMERICS_IDS_END);
			types.add(t.toString());
		}
		return types;
	}

	public static String getSID(String value, int maxLength) {
		if (value.length() > maxLength) {
			try {
				return Constants.PREFIX_SHORT_STRING
						+ HashingHelper.hashLongString(value);
			} catch (HashingException | UnsupportedEncodingException e) {
			}
		}
		return value;
	}

	public static String getTypeToCastTo(Short rexpType) {
		if (rexpType >= TypeMap.DATATYPE_NUMERICS_IDS_START
				&& rexpType <= TypeMap.DATATYPE_NUMERICS_IDS_END) {
			return "numeric";
		} else if (rexpType == TypeMap.DATE_ID
				|| rexpType == TypeMap.DATETIME_ID) {
			return "timestamp";
		}
		return null;

	}

	public static String getTypeToCastTo(TypeMap.TypeCategory rexpType) {
		if (rexpType == TypeMap.TypeCategory.NUMERIC) {
			return "numeric";
		} else if (rexpType == TypeMap.TypeCategory.DATETIME) {
			return "timestamp";
		}
		return null;

	}

	public static String getTypeTest(Short rexpType, String typeCol, Store store) {
		StringTemplate t;
		if (rexpType >= TypeMap.DATATYPE_NUMERICS_IDS_START
				&& rexpType <= TypeMap.DATATYPE_NUMERICS_IDS_END) {
			t = store.getInstanceOf(BETWEEN_TYPE_TEST);
			t.setAttribute("typeCol", typeCol);
			t.setAttribute("start", TypeMap.DATATYPE_NUMERICS_IDS_START);
			t.setAttribute("end", TypeMap.DATATYPE_NUMERICS_IDS_END);
		} else {
			t = store.getInstanceOf("TYPE_TEST");
			t.setAttribute("typeCol", typeCol);
			t.setAttribute("val", rexpType);
		}
		return t.toString();
	}

	public static String getTypeTest(TypeMap.TypeCategory type, String typeCol,
			Store store) {
		Short rexpType = null;
		if (type == TypeMap.TypeCategory.NUMERIC) {
			rexpType = TypeMap.DATATYPE_NUMERICS_IDS_START;
		} else if (type == TypeMap.TypeCategory.DATETIME) {
			rexpType = TypeMap.DATETIME_ID;
		} else if (type == TypeMap.TypeCategory.DATE) {
			rexpType = TypeMap.DATE_ID;
		}

		if (rexpType != null) {
			return getTypeTest(rexpType, typeCol, store);
		} else {
			return "";
		}
	}
}
