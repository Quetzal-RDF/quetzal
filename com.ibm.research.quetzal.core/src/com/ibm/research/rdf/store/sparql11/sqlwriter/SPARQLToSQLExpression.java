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
 package com.ibm.research.rdf.store.sparql11.sqlwriter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.stringtemplate.StringTemplate;

import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.runtime.service.types.TypeMap;
import com.ibm.research.rdf.store.sparql11.model.AggregateExpression;
import com.ibm.research.rdf.store.sparql11.model.BindPattern;
import com.ibm.research.rdf.store.sparql11.model.BuiltinFunctionExpression;
import com.ibm.research.rdf.store.sparql11.model.ConstantExpression;
import com.ibm.research.rdf.store.sparql11.model.Expression;
import com.ibm.research.rdf.store.sparql11.model.Expression.ERelationalOp;
import com.ibm.research.rdf.store.sparql11.model.FunctionCallExpression;
import com.ibm.research.rdf.store.sparql11.model.IRI;
import com.ibm.research.rdf.store.sparql11.model.LogicalExpression;
import com.ibm.research.rdf.store.sparql11.model.LongExpression;
import com.ibm.research.rdf.store.sparql11.model.NumericExpression;
import com.ibm.research.rdf.store.sparql11.model.RelationalExpression;
import com.ibm.research.rdf.store.sparql11.model.Variable;
import com.ibm.research.rdf.store.sparql11.model.VariableExpression;
import com.ibm.wala.util.collections.HashSetFactory;
import com.ibm.wala.util.collections.Pair;

public class SPARQLToSQLExpression
   {
 
   private static final String BOOLEAN_CASE = "BOOLEAN_CASE";


   private static Store               store;
   protected static String            CAST_LEFT                     = "CAST_LEFT";
   protected static String              CAST_RIGHT                    = "CAST_RIGHT";
   protected static String              CAST_ALL                      = "CAST_ALL";
  
   protected static String              RDF_OPERATOR                  = "RDF_OPERATOR";
   protected static String              RDF_OPERATOR_NUM              = "RDF_OPERATOR_NUM";
   protected static String              RDF_OPERATOR_DATE             = "RDF_OPERATOR_DATE";
   protected static String				NUMERIC_STR = "NUMERIC";
   protected static String NUMERICS_CASE = "NUMERICS_CASE";

   public static StringTemplate getInstanceOf(String name)
   {
      return store.getInstanceOf(name);
      }


   public void setStore(Store theStore)
      {
      store = theStore;
      }

   public String getColumnName(String columnName, TypeMap.TypeCategory type) {
	   assert type != null;
	   String str = store.getDatatype("STRING");
	   StringTemplate t = getInstanceOf("castsForColumns");
	   t.setAttribute("column", columnName);
	   t.setAttribute("type", str);
	   return t.toString();
   }
   public String getSQLBind(BindPattern bp, FilterContext context) throws SQLWriterException
      {
      String expression = getSQLForExpression(bp.getExpression(), context, store);
      StringBuffer buf = new StringBuffer();
      buf.append(expression).append(" AS ").append(bp.getVar().getName());
      return buf.toString();
      }

   public String getSQLForExpression(Expression exp, FilterContext context, Store theStore) throws SQLWriterException
      {
      store = theStore;
      StringTemplate t = null;
      // String sqlVar;
      String sql;

      String s = getSQLExpression(exp, context, store);
      if (exp instanceof ConstantExpression)
         {
         return s;
         }
      if ((exp instanceof ConstantExpression)
            || (exp instanceof VariableExpression && ((VariableExpression) exp).getExpression() == null))
         {

         String fTerm = null;
         String fType = null;

         s = getSQLExpression(exp, context, store);
         fTerm = s;
         Set<Variable> variables = exp.gatherVariables();
         assert (variables.size() < 2);
         if (variables.size() == 0)
            {
            short constType = exp.getReturnType();
            String constValue = ((ConstantExpression) exp).getConstant().toDataString();
            fTerm = "'" + ConstantExpression.getSID(constValue, store.getMaxStringLen()) + "'";
            if (constType >= TypeMap.DATATYPE_NUMERICS_IDS_START && constType <= TypeMap.DATATYPE_NUMERICS_IDS_END)
               {
               // cast((value as decfloat) <> 0)
               fTerm = "CAST('" + constValue + "' AS " + store.getDatatype(NUMERIC_STR) + ")";
               t = getInstanceOf("RDF_NE");
               t.setAttribute("left", fTerm);
               t.setAttribute("right", 0);
               }
            else if (constType == TypeMap.BOOLEAN_ID)
               {
               // value=true
               t = getInstanceOf("RDF_EQ");
               t.setAttribute("left", fTerm);
               t.setAttribute("right", "'true'");
               }
            else
               {
               // value <> "";
               t = getInstanceOf("RDF_NE");
               t.setAttribute("left", fTerm);
               t.setAttribute("right", "''");
               }
            }
         else
            {
            t = getInstanceOf(Expression.EBV);
            for (Variable v : variables)
               {
               fType = context.getVarMap().get(v.getName()).snd;
               fTerm = context.getVarMap().get(v.getName()).fst;
               }
            if (fType == null)
               fType = TypeMap.IRI_ID + "";
            t.setAttribute("fterm", fTerm);
            t.setAttribute("ftype", fType);
            t.setAttribute("nrstart", TypeMap.DATATYPE_NUMERICS_IDS_START);
            t.setAttribute("nrend", TypeMap.DATATYPE_NUMERICS_IDS_END);
            t.setAttribute("tstring", TypeMap.STRING_ID);
            t.setAttribute("pstring", TypeMap.SIMPLE_LITERAL_ID);
            t.setAttribute("tboolean", TypeMap.BOOLEAN_ID);
            }
         sql = t.toString();
         }
      else
         {
         sql = s;
         }
      return sql;
      }

   public String getSQLExpression(Expression exp, FilterContext context, Store store) throws SQLWriterException
      {
	  String ret = null;
	  Set<AggregateExpression> aggregateExpressions = HashSetFactory.make();
	  gatherAggregates(exp, aggregateExpressions);
	  if (!aggregateExpressions.isEmpty()) {
		  for (Expression e: aggregateExpressions) {
			  context.addConstantNeedsTypeCheck(e);
		  }
	  }
      if (exp == null)
         return "";

      ret = exp.visit(context, store);
      
      if (aggregateExpressions.isEmpty() && context.doesConstantNeedTypeCheck(exp) && exp.getReturnType() == TypeMap.DOUBLE_ID) {
    	  ret = wrapNumericsInCase(exp, ret, context);
      }
      return ret;
      }

   public static void gatherAggregates(Expression e, Set<AggregateExpression> expressions) {
	   if (e instanceof AggregateExpression) {
		   expressions.add((AggregateExpression) e);
		   return;
	   }
	   if (e instanceof NumericExpression) {
		   gatherAggregates(((NumericExpression) e).getLHS(), expressions);
		   gatherAggregates(((NumericExpression) e).getRHS(), expressions);
	   }
	   if (e instanceof LongExpression<?>) {
		   for (Expression exp : e.getSubExpressions()) {
			   gatherAggregates(exp, expressions);
		   }
	   }
	   if (e instanceof BuiltinFunctionExpression) {
		   if (((BuiltinFunctionExpression) e).getArguments() != null) {
			   for (Expression exp : ((BuiltinFunctionExpression) e).getArguments()) {
				   gatherAggregates(exp, expressions);
			   }
		   }
	   }
   }
   

   public static Set<Variable> gatherVariables(Expression e)
      {
      return e.gatherVariables();
      }

   public static List<String> getVarFilterBinding(Expression e, Variable var) throws SQLWriterException
      {
      List<String> varFilterBinding = new ArrayList<String>();
      if (e instanceof LogicalExpression)
         {
         List<Expression> components = ((LogicalExpression) e).getComponents();
         for (int i = 0; i < components.size(); i++)
            {
            Expression ex = components.get(i);
            varFilterBinding.addAll(getVarFilterBinding(ex, var));
            }
         }
      else if (e instanceof RelationalExpression && ((RelationalExpression) e).getOperator().equals(ERelationalOp.EQUAL))
         {
         if ((((RelationalExpression) e).getLeft() instanceof VariableExpression && ((RelationalExpression) e).getRight() instanceof ConstantExpression))
            {
            VariableExpression ve = (VariableExpression) ((RelationalExpression) e).getLeft();
            Variable v = new Variable(ve.getVariable());
            ConstantExpression ce = (ConstantExpression) ((RelationalExpression) e).getRight();
            if (v.equals(var))
               varFilterBinding.add(ce.toDataString());
            }
         if ((((RelationalExpression) e).getRight() instanceof VariableExpression && ((RelationalExpression) e).getLeft() instanceof ConstantExpression))
            {
            VariableExpression ve = (VariableExpression) ((RelationalExpression) e).getRight();
            Variable v = new Variable(ve.getVariable());
            ConstantExpression ce = (ConstantExpression) ((RelationalExpression) e).getLeft();
            if (v.equals(var))
               varFilterBinding.add(ce.toDataString());
            }
         }
      else if (e instanceof FunctionCallExpression)
         {
         IRI function = ((FunctionCallExpression) e).getCall().getFunction();
         List<Expression> arguments = ((FunctionCallExpression) e).getCall().getArguments();
         if (function.getValue() == "http://www.w3.org/2005/xpath-functions#starts-with")
            {
            Set<Variable> vars = arguments.get(0).gatherVariables();
            if ((arguments.get(0) instanceof ConstantExpression) && vars.contains(var))
               {
               varFilterBinding.add(((ConstantExpression) arguments.get(0)).toDataString());
               }
            }
         if (function.getValue() == "http://www.w3.org/2005/xpath-functions#string")
            {
            varFilterBinding.addAll(getVarFilterBinding(arguments.get(0), var));
            }
         }
      else if (e instanceof BuiltinFunctionExpression)
         {
         return new ArrayList<String>();
         }
      else if (e instanceof VariableExpression)
         {
         return new ArrayList<String>();
         }
      else if (e instanceof ConstantExpression)
         {
         return new ArrayList<String>();
         }
      else
         {
         // System.out.println("This filter is not implemented");
         // throw new SQLWriterException("Filter type not implemented");
         return new ArrayList<String>();
         }
      return varFilterBinding;
      }
	
	public String wrapBooleanExpressionInCase(String expression) {
		StringTemplate t = getInstanceOf(BOOLEAN_CASE);
		t.setAttribute("condition", expression);
		return t.toString();
	}
	
	public String wrapNumericsInCase(Expression expression, String condition, FilterContext context) {
		Map<String, Pair<String, String>> varMap = context.getVarMap();
		
		List<String> types = getNumericTypeCheckSQL(expression, varMap);
		StringTemplate t = getInstanceOf(NUMERICS_CASE);
		t.setAttribute("types", types);
		t.setAttribute("condition", condition);
		return t.toString();
	}

	private List<String> getNumericTypeCheckSQL(Expression expression,
			Map<String, Pair<String, String>> varMap) {
		List<String> types = new LinkedList<String>();
		for (Variable v : expression.gatherVariables()) {
			StringTemplate t = getInstanceOf(Expression.BETWEEN_TYPE_TEST);
			t.setAttribute("typeCol", varMap.get(v.getName()).snd);
			t.setAttribute("start", TypeMap.DATATYPE_NUMERICS_IDS_START);
			t.setAttribute("end", TypeMap.DATATYPE_NUMERICS_IDS_END);
			types.add(t.toString());
		}
		return types;
	}
   }
