package com.ibm.rdf.store.sparql11.sqlwriter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.antlr.stringtemplate.StringTemplate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibm.rdf.store.Store;
import com.ibm.rdf.store.Store.Db2Type;
import com.ibm.rdf.store.config.Constants;
import com.ibm.rdf.store.hashing.HashingException;
import com.ibm.rdf.store.hashing.HashingHelper;
import com.ibm.rdf.store.runtime.service.types.TypeMap;
import com.ibm.rdf.store.sparql11.model.AggregateExpression;
import com.ibm.rdf.store.sparql11.model.AggregateExpression.EType;
import com.ibm.rdf.store.sparql11.model.BindPattern;
import com.ibm.rdf.store.sparql11.model.BuiltinFunctionExpression;
import com.ibm.rdf.store.sparql11.model.ConstantExpression;
import com.ibm.rdf.store.sparql11.model.Expression;
import com.ibm.rdf.store.sparql11.model.Expression.EBuiltinType;
import com.ibm.rdf.store.sparql11.model.Expression.ERelationalOp;
import com.ibm.rdf.store.sparql11.model.Expression.EUnaryOp;
import com.ibm.rdf.store.sparql11.model.FunctionCall;
import com.ibm.rdf.store.sparql11.model.FunctionCallExpression;
import com.ibm.rdf.store.sparql11.model.IRI;
import com.ibm.rdf.store.sparql11.model.LogicalExpression;
import com.ibm.rdf.store.sparql11.model.LongExpression;
import com.ibm.rdf.store.sparql11.model.NumericExpression;
import com.ibm.rdf.store.sparql11.model.RelationalExpression;
import com.ibm.rdf.store.sparql11.model.UNDEFExpression;
import com.ibm.rdf.store.sparql11.model.UnaryExpression;
import com.ibm.rdf.store.sparql11.model.Variable;
import com.ibm.rdf.store.sparql11.model.VariableExpression;
import com.ibm.wala.util.collections.HashSetFactory;
import com.ibm.wala.util.collections.Pair;

public class SPARQLToSQLExpression
   {
   private static final Log           log                           = LogFactory.getLog(SPARQLToSQLExpression.class);

  /* public static String               templatesFile                 = null;
   public static final String         DB2templatesFile              = "com/ibm/rdf/store/sparql11/sqlwriter/DB2SQLTemplates.stg";
   public static final String         PSQLtemplatesFile             = "com/ibm/rdf/store/sparql11/sqlwriter/PSQLSQLTemplates.stg";
*/
   // string template names
   public static final String         LOGICAL_OR                    = "logical_or_expressions";
   public static final String         LOGICAL_AND                   = "logical_and_expressions";
   public static final String         RELATIONAL                    = "relational_expression";
   public static final String         NUMERIC                       = "numeric_expression";
   public static final String         UNARY                         = "unary_expression";
   public static final String         UNARY_NOT                     = "unary_not";
   public static final String         UNARY_MINUS                   = "unary_minus";
   public static final String         BUILTIN_FUNCTION              = "builtin_function";
   public static final String         BOOLEAN_FUNCTION              = "boolean_function";
   public static final String         COALESCE                      = "coalesce";
   public static final String         REGEX_VAR                     = "regex_var";
   public static final String         REGEX_PATTERN                 = "regex_pattern";
   public static final String         REGEX_FLAG                    = "regex_flag";
   public static final String         ARGS                          = "args";
   public static final String         ARG                           = "arg";
   public static final String         FUNCTION                      = "function";
   public static final String         EXPS                          = "expressions";
   public static final String         EXP                           = "expression";
   public static final String         LEFT                          = "left";
   public static final String         RIGHT                         = "right";
   public static final String         CAST                          = "cast";
   public static final String         CAST_FUNCTION_START           = "cast_function_start";
   public static final String         CAST_FUNCTION_END             = "cast_function_end";
   public static final String         OP                            = "operator";
   public static final String         UDF                           = "udf";
   public static final String         EBV                           = "RDF_EBV";
   public static final String         NOT_EBV                       = "NOT_RDF_EBV";
   public static final String         IS_TRUE                       = "RDF_IS_TRUE";
   public static final String         IS_FALSE                      = "RDF_IS_FALSE";
   public static final String         IS_NOT_TRUE                   = "RDF_IS_NOT_TRUE";
   public static final String         IS_NOT_FALSE                  = "RDF_IS_NOT_TRUE";
   public static final String         VAR                           = "var";
   public static final String         TYPE                          = "type";
   public static final String         VALUE                         = "value";
   public static final String         LEN                           = "len";
   public static final String         LS_TABLE                      = "long_strings_table";
   public static final String         STRSTARTS                     = "http://www.w3.org/2005/xpath-functions#starts-with";
   public static final String         STRENDS                       = "http://www.w3.org/2005/xpath-functions#ends-with";
   // public static final String XFN_DATETIME = "http://www.w3.org/2005/xpath-functions#dateTime";
   public static final String         XSD_DATETIME                  = "http://www.w3.org/2001/XMLSchema#dateTime";
   public static final String         XSD_INTEGER                   = "http://www.w3.org/2001/XMLSchema#integer";
   public static final String 		  XSD_BOOLEAN					= "http://www.w3.org/2001/XMLSchema#boolean";
   public static final String         XSD_DATETIME_NUMERICS_CAST              = "XSD_DATETIME_NUMERICS_CAST";
   public static final String         RDF_XFN_INTEGER               = "RDF_XSD_INTEGER";
   public static final String         RDF_XFN_INTEGER_DS            = "RDF_XSD_INTEGER_DS";
   public static final String         STR                           = "http://www.w3.org/2001/XMLSchema#string";
   public static final String         RDF_STR                       = "RDF_STR";
   public static final String         RDF_STRSTARTS                 = "RDF_STRSTARTS";
   public static final String         RDF_STRSTARTS_LS              = "RDF_STRSTARTS_LS";
   public static final String         RDF_STRENDS                   = "RDF_STRENDS";
   public static final String         RDF_STRENDS_LS                = "RDF_STRENDS_LS";
   public static final String         RDF_STRSTARTS_LIKE            = "RDF_STRSTARTS_LIKE";
   public static final String         RDF_STRSTARTS_LS_LIKE         = "RDF_STRSTARTS_LS_LIKE";
   public static final String         RDF_STRENDS_LIKE              = "RDF_STRENDS_LIKE";
   public static final String         RDF_STRENDS_LS_LIKE           = "RDF_STRENDS_LS_LIKE";
   public static final String         AGGREGATE_COUNT               = "aggregate_count";
   public static final String         AGGREGATE_DISTINCT_COUNT      = "aggregate_distinct_count";
   public static final String         AGGREGATE_FUNCTION            = "aggregate_function";
   public static final String         LONG_STRING_ORDER_BY_VARIABLE = "LONG_STRING_ORDER_BY_VARIABLE";
   public static final String         LONG_STRING_ORDER_BY_JOIN     = "LONG_STRING_ORDER_BY_JOIN";
   public static final String         TABLE_NAME                    = "tab_name";
   public static final String         TABLE_ALIAS                   = "tab_alias";
   public static final String         VARIABLE_NAME                 = "var";
   public static final String         VARIABLE_TYPE                 = "var_typ";

   public static final String         EMPTY_STRING                  = "";

private static final String STRING_SIMPLE_LIT_MATCH = "STRING_SIMPLE_LIT_MATCH";

private static final String BOOLEAN_CASE = "BOOLEAN_CASE";

private static final String NUMERICS_TYP_CHECK = "NUMERICS_TYP_CHECK";

private static final String XSD_BOOLEAN_CAST = "XSD_BOOLEAN_CAST";

private static final String RDF_DATATYPE_EXPRESSION = "RDF_DATATYPE_EXPRESSION";

private static final String GROUP_CONCAT = "GROUP_CONCAT";

private static final String AGGREGATE_FUNCTION_WITH_TYP_CHECK = "aggregate_function_with_type_check";



   private static Store               store;
   private static boolean             useLike                       = true;
   protected static String            CAST_LEFT                     = "CAST_LEFT";
   protected static String              CAST_RIGHT                    = "CAST_RIGHT";
   protected static String              CAST_ALL                      = "CAST_ALL";
   protected static String              NEQ_CAST_ALL               = "NEQ_CAST_ALL";
   protected static String              EQ_CAST_ALL               = "EQ_CAST_ALL";
   protected static String              GT_CAST_ALL               = "GT_CAST_ALL";

   protected static String              TIMESTAMP_STR                 = "DATETIME";
   protected static String              DATE_STR                 =      "DATE";
   protected static String              FLOAT_STR                  = "FLOAT";
   protected static String				STRING_STR   = "STRING";
   protected static String 				DOUBLE_STR = "DOUBLE";
   protected static String				INTEGER_STR = "INTEGER";
   protected static String				DECIMAL_STR = "DECIMAL";
   protected static String				NUMERIC_STR = "NUMERIC";
   
   protected static String              RDF_OPERATOR                  = "RDF_OPERATOR";
   protected static String              RDF_OPERATOR_NUM              = "RDF_OPERATOR_NUM";
   protected static String              RDF_OPERATOR_DATE             = "RDF_OPERATOR_DATE";
   protected static String 	TYPE_CHECK_WITH_CAST = "TYPE_CHECK_WITH_CAST";
   protected static String  TYPE_CHECK_CASE = "TYPE_CHECK_CASE";
   protected static String TYPE_CHECK = "TYPE_CHECK";
   protected static String BETWEEN_TYPE_TEST = "BETWEEN_TYPE_TEST";
   protected static String TYPE_TEST = "TYPE_TEST";
   protected static String NUMERICS_CASE = "NUMERICS_CASE";
   protected static String CAST_PROJECTED_VAR = "cast_numeric_projected_variable";
   
   private boolean                    isRegex                       = false;

   protected String CONSTANT_EXP_TYPE_NAME = "CONSTANT_EXP_TYPE_NAME";

   protected String CONST_EXP_TYPE_VALUE = "CONSTANT_EXP_TYPE_VALUE";

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
      String expression = getSQLExpression(bp.getExpression(), context, store);
      StringBuffer buf = new StringBuffer();
      buf.append(expression).append(" AS ").append(bp.getVar().getName());
      return buf.toString();
      }

   public String getSQLExpression(Expression exp, FilterContext context, Store theStore) throws SQLWriterException
      {
      store = theStore;
      StringTemplate t = null;
      // String sqlVar;
      String sql;

      String s = getSQLExpression(exp, context);
      if (exp instanceof ConstantExpression)
         {
         return s;
         }
      if ((exp instanceof ConstantExpression)
            || (exp instanceof VariableExpression && ((VariableExpression) exp).getExpression() == null))
         {

         String fTerm = null;
         String fType = null;

         s = getSQLExpression(exp, context);
         fTerm = s;
         Set<Variable> variables = exp.gatherVariables();
         assert (variables.size() < 2);
         if (variables.size() == 0)
            {
            short constType = exp.getReturnType();
            String constValue = ((ConstantExpression) exp).getConstant().toDataString();
            fTerm = "'" + getSID(constValue, store.getMaxStringLen()) + "'";
            if (constType >= TypeMap.DATATYPE_NUMERICS_IDS_START && constType <= TypeMap.DATATYPE_NUMERICS_IDS_END)
               {
               // cast((value as decfloat) <> 0)
               fTerm = "CAST('" + constValue + "' AS " + store.getDatatype(NUMERIC_STR) + ")";
               t = getInstanceOf("RDF_NE");
               t.setAttribute(LEFT, fTerm);
               t.setAttribute(RIGHT, 0);
               }
            else if (constType == TypeMap.BOOLEAN_ID)
               {
               // value=true
               t = getInstanceOf("RDF_EQ");
               t.setAttribute(LEFT, fTerm);
               t.setAttribute(RIGHT, "'true'");
               }
            else
               {
               // value <> "";
               t = getInstanceOf("RDF_NE");
               t.setAttribute(LEFT, fTerm);
               t.setAttribute(RIGHT, "''");
               }
            }
         else
            {
            t = getInstanceOf(EBV);
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

   public String getSQLExpression(Expression exp, FilterContext context) throws SQLWriterException
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
         return EMPTY_STRING;

      if (exp instanceof LogicalExpression)
         {
         ret = getSQLLogicalExpression((LogicalExpression) exp, context);
         }
      else if (exp instanceof RelationalExpression)
         {
         ret = getSQLRelationalExpression((RelationalExpression) exp, context);
         }
      else if (exp instanceof NumericExpression)
         {
         ret = getSQLNumericExpression((NumericExpression) exp, context);
         }
      else if (exp instanceof UnaryExpression)
         {
         ret = getSQLUnaryExpression((UnaryExpression) exp, context);
         }
      else if (exp instanceof BuiltinFunctionExpression)
         {
         ret = getSQLBuiltinFunctionExpression((BuiltinFunctionExpression) exp, context);
         }
      else if (exp instanceof AggregateExpression)
         {
         ret = getSQLAggregateExpression((AggregateExpression) exp, context);
         }
      else if (exp instanceof FunctionCallExpression)
         {
         ret =  getSQLFunctionCallExpression((FunctionCallExpression) exp, context);
         }
      else if (exp instanceof ConstantExpression)
         {
         ret = getSQLConstantExpression((ConstantExpression) exp, context);
         }
      else if (exp instanceof VariableExpression)
         {
         ret = getSQLVariableExpression((VariableExpression) exp, context);
         }
      else if (exp instanceof UNDEFExpression)
         {
         ret =  getSQLUNDEFExpression(((UNDEFExpression) exp));
         }

      
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
   
   private String getSQLUNDEFExpression(UNDEFExpression exp)
      {
      return "'" + getSID(exp.toDataString(), store.getMaxStringLen()) + "'";

      }

   private String getSQLLogicalExpression(LogicalExpression lexp, FilterContext context) throws SQLWriterException
      {
      StringTemplate t;
      String s;
      List<String> l = new ArrayList<String>();

      for (Expression e : lexp.getComponents())
         {
         s = getSQLExpression(e, context);
         if (s.equals(EMPTY_STRING))
            continue;
         // Mihaela: ToAsk why only var exp where the exp is null become EBV?
         if ((e instanceof ConstantExpression)
               || (e instanceof VariableExpression && ((VariableExpression) e).getExpression() == null))
            {
            t = getInstanceOf(EBV);

            String fTerm = null;
            String fType = null;

            s = getSQLExpression(e, context);
            fTerm = s;
            Set<Variable> variables = e.gatherVariables();
            assert (variables.size() < 2);
            if (variables.size() == 0)
               {
               short constType = e.getReturnType();
               String constValue = ((ConstantExpression) e).getConstant().toDataString();
               fTerm = "'" + getSID(constValue, store.getMaxStringLen()) + "'";
               if (constType >= TypeMap.DATATYPE_NUMERICS_IDS_START && constType <= TypeMap.DATATYPE_NUMERICS_IDS_END)
                  {
                  // cast((value as decfloat) <> 0)
                  fTerm = "CAST('" + constValue + "' AS " + store.getDatatype(NUMERIC_STR) + ")";
                  t = getInstanceOf("RDF_NE");
                  t.setAttribute(LEFT, fTerm);
                  t.setAttribute(RIGHT, 0);
                  }
               else if (constType == TypeMap.BOOLEAN_ID)
                  {
                  // value=true
                  t = getInstanceOf("RDF_EQ");
                  t.setAttribute(LEFT, fTerm);
                  t.setAttribute(RIGHT, "'true'");
                  }
               else
                  {
                  // value <> "";
                  t = getInstanceOf("RDF_NE");
                  t.setAttribute(LEFT, fTerm);
                  t.setAttribute(RIGHT, "''");
                  }
               }
            else
               {
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

            s = t.toString();
            }
         l.add(s);
         }

      if (lexp.getType() == Expression.EExpressionType.OR)
         {
         t = getInstanceOf(LOGICAL_OR);
         }
      else
         {
         t = getInstanceOf(LOGICAL_AND);
         }
      t.setAttribute(EXPS, l);

      return t.toString();
      }

 
   private String getSQLVariableExpression(VariableExpression exp, FilterContext context)
      {
      String v = exp.getVariable();
      if (context.getVarMap().keySet().contains(v)) {
    	  String e = null;
    	  if (exp.getExpression() != null) {
    		  try {
    			  // KAVITHA: Cant have a variable with an expression in a solution modifier
    			  e = getSQLExpression(exp.getExpression(), context);
    		  } catch (Exception ex) {
    			  ex.printStackTrace();
    		  }
    	  }
    	  if (e != null) {
    		  return "(" + e + ") AS " + context.getVarMap().get(v).fst;
    	  }
         return (context.getVarMap().get(v)).fst;
      } else {
         return EMPTY_STRING;
      }
      }

   private String getSQLConstantExpression(ConstantExpression exp, FilterContext context)
      {
      if (isRegex)
         {
         return "'" + exp.toDataString() + "'";
         }

      return "'" + getSID(exp.toDataString(), store.getMaxStringLen()) + "'";
      }

   private String getSQLConstantExpressionAsType(ConstantExpression exp, FilterContext context)
   {	   
	   if (exp.getReturnType() >= TypeMap.DATATYPE_NUMERICS_IDS_START && exp.getReturnType() <= TypeMap.DATATYPE_NUMERICS_IDS_END) {
		   return getSID(exp.toDataString(), store.getMaxStringLen());
	   } 
	   if (exp.getReturnType() == TypeMap.DATE_ID) {
		   return " DATE '" + exp.toDataString() + "'";
	   }
	   return getSQLConstantExpression(exp, context);
   }
   
   private String getSQLNumericExpression(NumericExpression exp, FilterContext context) throws SQLWriterException
      {
	   String left = null;
	   if (exp.getLHS() instanceof ConstantExpression) {
		   left = getSQLConstantExpressionAsType((ConstantExpression) exp.getLHS(), context);
	   } else {
		   left = getSQLExpression(exp.getLHS(), context);
	   }
      String right = null;
      if (exp.getRHS() instanceof ConstantExpression) {
    	  right = getSQLConstantExpressionAsType((ConstantExpression) exp.getRHS(), context);
      } else {
    	  right = getSQLExpression(exp.getRHS(), context);
      }
      if ((left.equals(EMPTY_STRING)) || (right.equals(EMPTY_STRING)))
         return EMPTY_STRING;

      String operator = exp.getOperatorString();
      if (operator.equals(EMPTY_STRING))
         return EMPTY_STRING;

      StringTemplate t = getInstanceOf(NUMERIC);
      t.setAttribute(LEFT, left);
      t.setAttribute(RIGHT, right);
      t.setAttribute(OP, operator);

      return t.toString();
      }

   private String getSQLUnaryExpression(UnaryExpression exp, FilterContext context) throws SQLWriterException
      {
      String s = getSQLExpression(exp.getExpression(), context);
      if (s.equals(EMPTY_STRING))
         return EMPTY_STRING;

      StringTemplate t = null;
      if (exp.getOperator() == EUnaryOp.MINUS)
      {
      t = getInstanceOf(UNARY_MINUS);
      } else if ((exp.getExpression() instanceof VariableExpression && ((VariableExpression) exp.getExpression()).getExpression() == null))
         {
    	  t = getInstanceOf(EBV);
          String fType = null;
          String fTerm = null;
          for (Variable v : exp.getExpression().gatherVariables())
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
          s = t.toString();
          
    	  if (exp.getOperator() == EUnaryOp.NOT) {
    		  t = getInstanceOf(NOT_EBV);
    		  t.setAttribute("ebv", s);
              t.setAttribute("type", fType); 
    		  t.setAttribute("unknownTypesStart", TypeMap.USER_ID_START);
              t.setAttribute("unknownTypesEnd", TypeMap.NONE_ID);
    		  return t.toString();
    	  } 
         }

      if (exp.getOperator() == EUnaryOp.NOT)
         {
         t = getInstanceOf(UNARY_NOT);
         }
/*      else
         {
         return s;
         } */

      t.setAttribute(EXP, s);

      return t.toString();
      }

   private String getSQLEffectiveBooleanValue(String var, FilterContext context) throws SQLWriterException
      {
      StringTemplate t = getInstanceOf(EBV);

      String fType = context.getVarMap().get(var).snd;
      String fTerm = context.getVarMap().get(var).fst;

      if (fType == null)
         fType = TypeMap.IRI_ID + "";

      t.setAttribute("fterm", fTerm);
      t.setAttribute("ftype", fType);
      t.setAttribute("nrstart", TypeMap.DATATYPE_NUMERICS_IDS_START);
      t.setAttribute("nrend", TypeMap.DATATYPE_NUMERICS_IDS_END);
      t.setAttribute("tstring", TypeMap.STRING_ID);
      t.setAttribute("pstring", TypeMap.SIMPLE_LITERAL_ID);
      t.setAttribute("tboolean", TypeMap.BOOLEAN_ID);

      return t.toString();
      }

   private String getSQLForExists(BuiltinFunctionExpression exp, FilterContext context, StringTemplate t)
      {
      t.setAttribute("right_target", context.getRightTarget());
      t.setAttribute("exists_constraints", context.getExistsConstraints());
      t.setAttribute("is_negated", context.getIsNegated());
      return t.toString();
      }
   
   private String getSQLBuiltinFunctionExpression(BuiltinFunctionExpression exp, FilterContext context) throws SQLWriterException
      {
      List<String> args = new ArrayList<String>();
      List<Expression> exps = exp.getArguments();
      Iterator<Expression> iter = exps.iterator();
      Expression e = null;
      String s;

      System.out.println("exp: " + exp);
      String type = exp.getBuiltinTypeString();

      if (type.equals("RDF_EXISTS") || type.equals("RDF_NOT_EXISTS"))
         {
         return getSQLForExists(exp, context, getInstanceOf("RDF_EXISTS"));

         }
      StringTemplate t = getInstanceOf(type);
      System.out.println("type: " + type + ", template: " + t);
      System.out.println("varmap is " + context.getVarMap());
      System.out.println("varDB2Type is " + context.getVarDb2Type());

      if (type.equals("RDF_SAMETERM"))
         {
         String fTerm = null;
         String fType = null;
         String sTerm = null;
         String sType = null;
         e = iter.next();
         s = getSQLExpression(e, context);
         fTerm = s;
         Set<Variable> variables = e.gatherVariables();
         assert (variables.size() < 2);
         if (variables.size() == 0)
            fType = e.getReturnType().toString();
         else
            {
            for (Variable v : variables)
               fType = context.getVarMap().get(v.getName()).snd;
            if (fType == null)
               fType = TypeMap.IRI_ID + "";
            }
         e = iter.next();
         s = getSQLExpression(e, context);
         // sTerm="'" + getSID(s.substring(1, s.length() - 1), store.getMaxStringLen()) + "'";
         sTerm = s;
         variables = e.gatherVariables();
         assert (variables.size() < 2);
         if (variables.size() == 0)
            sType = e.getReturnType().toString();
         else
            {
            for (Variable v : variables)
               sType = context.getVarMap().get(v.getName()).snd;
            if (sType == null)
               sType = TypeMap.IRI_ID + "";
            }
         t.setAttribute("fterm", fTerm);
         t.setAttribute("ftype", fType);
         t.setAttribute("sterm", sTerm);
         t.setAttribute("stype", sType);
         return t.toString();
         }
      else if (type == "RDF_ISIRI" || type == "RDF_ISURI")
         {
         String fType = null;
         e = iter.next();
         for (Variable v : e.gatherVariables())
            {
            fType = context.getVarMap().get(v.getName()).snd;
            }
         if (fType == null)
            fType = TypeMap.IRI_ID + "";
         t.setAttribute("type", fType);
         t.setAttribute("iri_type", TypeMap.IRI_ID);
         return t.toString();
         }
      else if (type.equals("RDF_ISLITERAL"))
         {
         String fType = null;
         e = iter.next();
         for (Variable v : e.gatherVariables())
            {
            fType = context.getVarMap().get(v.getName()).snd;
            }
         if (fType == null)
            fType = TypeMap.IRI_ID + "";
         t.setAttribute("type", fType);
         t.setAttribute("iri", TypeMap.IRI_ID);
         t.setAttribute("blankNode", TypeMap.BLANK_NODE_ID);
         return t.toString();
         }
      else if (type.equals("RDF_ISNUMERIC"))
         {
         e = iter.next();
         if (e instanceof VariableExpression)
            {
            String var = ((VariableExpression) e).getVariable();
            String funcArgs = context.getVarMap().get(var).fst;
            t.setAttribute("arg", funcArgs);
            }
         return t.toString();
         }
      else if (type.equals("RDF_DATATYPE"))
         {
          e = iter.next();
          return handleExpressionForDatatype(context, t, e);
 
         }
      else if (type.equals("RDF_LANG"))
         {
         String fType = null;
         e = iter.next();
         if (e instanceof ConstantExpression)
            {
            t = getInstanceOf("RDF_LANG_CONST");
            String langArg = ((ConstantExpression) e).getConstant().toDataString();
            t.setAttribute("args", langArg);
            }
         else if (e instanceof VariableExpression)
            {
            t = getInstanceOf("RDF_LANG_VAR");
            t.setAttribute("dttable", store.getDataTypeTable());
            String varArg = ((VariableExpression) e).getVariable();
            String langArg = context.getVarMap().get(varArg).snd;
            t.setAttribute("langid", langArg);
            t.setAttribute("iri", TypeMap.IRI_ID);
            t.setAttribute("blankNode", TypeMap.BLANK_NODE_ID);
            t.setAttribute("startOfLangLiterals", TypeMap.LANG_START);
            t.setAttribute("endOfLangLiterals", TypeMap.LANG_END);
            }
         // for (Variable v : e.gatherVariables())
         // {
         // fType = context.getVarMap().get(v.getName()).snd;
         // }
         // if (fType == null)
         // fType = TypeMap.IRI_ID + "";
         // t.setAttribute(ARGS, fType);
         System.out.println(">>> " + t);
         return t.toString();
         }
      else if (type.equals("RDF_LANGMATCHES"))
         {
         e = iter.next();
         if (e instanceof BuiltinFunctionExpression)
            {
            String fTerm = getSQLBuiltinFunctionExpression((BuiltinFunctionExpression) e, context);
            t.setAttribute("ftype", fTerm);
            }
         e = iter.next();
         if (e instanceof ConstantExpression)
            {
            String constantData = ((ConstantExpression) e).getConstant().toDataString();
            /*
             * if(constantData.equals("*")){ t = getInstanceOf("RDF_LANGMATCHES_ALL"); t.setAttribute("ftype", fTerm); t.setAttribute("rstart",
             * TypeMap.LANG_IDS_START); } else{ Short requestedLang=TypeMap.getLanguageType(constantData); if(requestedLang%100==0){ //this is a
             * language family Short rStart=requestedLang; Short rEnd=(short) (rStart+TypeMap.SUB_LANG_RANGE-1); t =
             * getInstanceOf("RDF_LANGMATCHES_FAMILY"); t.setAttribute("ftype", fTerm); t.setAttribute("rstart", rStart); t.setAttribute("rend",
             * rEnd); } else{ sTerm=requestedLang.toString(); t = getInstanceOf("RDF_LANGMATCHES"); t.setAttribute("ftype", fTerm);
             * t.setAttribute("stype", sTerm); } }
             */
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
            if (!constantData.equals("*"))
               {
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
      else if (type.equals("RDF_EBV"))
         {
         String fTerm = null;
         String fType = null;
         e = iter.next();
         s = getSQLExpression(e, context);
         fTerm = s;
         Set<Variable> variables = e.gatherVariables();
         assert (variables.size() < 2);
         if (variables.size() == 0)
            {
            short constType = e.getReturnType();
            String constValue = ((ConstantExpression) e).getConstant().toDataString();
            if (constType >= TypeMap.DATATYPE_NUMERICS_IDS_START && constType <= TypeMap.DATATYPE_NUMERICS_IDS_END)
               {
               // cast((value as decfloat) <> 0)
               fTerm = "CAST('" + constValue + "' AS " + store.getDatatype(NUMERIC_STR) + ")";
               t = getInstanceOf("RDF_NE");
               t.setAttribute(LEFT, fTerm);
               t.setAttribute("ftype", 0);
               }
            else if (constType == TypeMap.BOOLEAN_ID)
               {
               // value=true
               t = getInstanceOf("RDF_EQ");
               t.setAttribute(LEFT, constValue);
               t.setAttribute("ftype", "'true'");
               }
            else
               {
               // value <> "";
               t = getInstanceOf("RDF_NE");
               t.setAttribute(LEFT, constValue);
               t.setAttribute("ftype", "''");
               }
            }
         else
            {
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
         return t.toString();
         }
      else if (type.equals("RDF_REGEX") || type.equals("LOCATE"))
         {
         boolean isFirst = true && store.getHasLongStrings() == 1;
         String sqlVarName = null;
         String pattern = null;
         String flag = "";
         int argPosition = 0;
         while (iter.hasNext())
            {
            e = iter.next();
            isRegex = true;
            s = getSQLExpression(e, context);
            isRegex = false;
            if (isFirst)
               {
               if (s.startsWith("COALESCE"))
                  {
                  s = "COALESCE(" + Constants.NAME_LONG_STRING_TABLE + "." + Constants.NAME_COLUMN_LONG_STRING + ", " + s + ")";
                  }
               else if (!s.equals(((VariableExpression) e).getVariable()))
                  {
                  s = "COALESCE(" + Constants.NAME_LONG_STRING_TABLE + "." + Constants.NAME_COLUMN_LONG_STRING + ", "
                        + ((VariableExpression) e).getVariable() + ")";
                  }
               isFirst = false;
               }
            if (argPosition == 0)
               sqlVarName = s;
            if (argPosition == 1)
               pattern = (s.startsWith("'")) ? s.substring(1, s.length() - 1) : s;
            if (argPosition == 2)
               flag = (s.startsWith("'")) ? s.substring(1, s.length() - 1) : s;
            argPosition++;
            }

         t = null;
         if (Pattern.matches("^[a-zA-Z0-9]+$", pattern) || type.equals("LOCATE"))
            {
            if ("i".equals(flag))
               {
               t = getInstanceOf("LOCATE_LCASE");
               t.setAttribute("string_var", sqlVarName);
               t.setAttribute("pattern", pattern);
               }
            else if (flag == null || "".equals(flag))
               {
               t = getInstanceOf("LOCATE");
               t.setAttribute("string_var", sqlVarName);
               t.setAttribute("pattern", pattern);
               }
            }
         else if (Pattern.matches("^\\^[a-zA-Z0-9]+$", pattern) && !flag.contains("i"))
            {
            t = getInstanceOf("RDF_STRSTARTS_LIKE");
            t.setAttribute("var", sqlVarName);
            t.setAttribute("value", pattern.substring(1));
            t.setAttribute("long_strings_table", "bogus");
            }

         if (t == null)
            {
            t = getInstanceOf(type);
            t.setAttribute(REGEX_VAR, sqlVarName);
            // KAVITHA: DB2 does not like double "\\" escape sequences in the pattern!
            String p = pattern.replaceAll("\\\\\\\\", "\\\\");
            
            t.setAttribute(REGEX_PATTERN, p);
            t.setAttribute(REGEX_FLAG, flag);
            List<String> typeCheck = null;
            Expression e1 = exp.getArguments().get(0);
            if (e1.getReturnType() != TypeMap.STRING_ID && e1.getReturnType() != TypeMap.SIMPLE_LITERAL_ID) {
            	typeCheck = getStringTypeCheckSQL(exp, context.getVarMap());
            }
            t.setAttribute("stringTypes", typeCheck);
            
            }
         return t.toString();
         }
      else if (type.equals("RDF_IRI") || type.equals("RDF_URI"))
         {
         String str = null;
         e = iter.next();
         if (e instanceof ConstantExpression)
            {
            str = ((ConstantExpression) e).getConstant().toDataType() + "";
            }
         else
            {
            str = getSQLExpression(e, context);
            }
         t = getInstanceOf("RDF_IRI");
         t.setAttribute("string", str);
         return t.toString();
         }
      else if (type.equals("RDF_RAND"))
         {
         return t.toString();
         }
      else if (type.equals("RDF_ABS"))
         {
         e = iter.next();
         if (e instanceof VariableExpression)
            {
            String var = ((VariableExpression) e).getVariable();
            String absType = context.getVarMap().get(var).fst;
            t.setAttribute("args", absType);
            }
         return t.toString();
         }
      else if (type.equals("RDF_CEIL") || type.equals("RDF_FLOOR") || type.equals("RDF_ROUND") || type.equals("RDF_STRLEN")
            || type.equals("RDF_UCASE") || type.equals("RDF_LCASE") || type.equals("RDF_YEAR") || type.equals("RDF_MONTH")
            || type.equals("RDF_DAY") || type.equals("RDF_HOURS") || type.equals("RDF_MINUTES") || type.equals("RDF_SECONDS")
            || type.equals("RDF_TIMEZONE") || type.equals("RDF_TZ"))
         {
         e = iter.next();
         if (e instanceof VariableExpression)
            {
            String var = ((VariableExpression) e).getVariable();
            String funcArgs = context.getVarMap().get(var).fst;
            t.setAttribute("args", funcArgs);
            }
         return t.toString();
         }
      else if (type.equals("RDF_NOW"))
         {
         return t.toString();
         }
      else if (type.equals("RDF_CONCAT"))
         {
         e = iter.next();
         
         t.setAttribute("lexpr", getSQLExpression(e, context));
         
         e = iter.next();
         if (iter.hasNext()) {
        	 List<Expression> argsE = new LinkedList<Expression>();
        	 argsE.add(e);
        	 while (iter.hasNext()) {
        		 argsE.add(iter.next());
        	 }
        	 Expression rexp = new BuiltinFunctionExpression(EBuiltinType.CONCAT, argsE);
        	 t.setAttribute("rexpr", getSQLExpression(rexp, context));
         } else {
        	 t.setAttribute("rexpr", getSQLExpression(e, context));
         }
         return t.toString();
         }
      else if (type.equals("RDF_SUB_STRING_EXP"))
         {
         e = iter.next();
         if (e instanceof VariableExpression)
            {
            String var = ((VariableExpression) e).getVariable();
            String funcArgs = context.getVarMap().get(var).fst;
            t.setAttribute("string_var", funcArgs);
            }
         e = iter.next();
         if (e instanceof ConstantExpression)
            {
            String startPos = ((ConstantExpression) e).getConstant().toDataString();
            t.setAttribute("startPos", startPos);
            }
         if (iter.hasNext())
            {
            e = iter.next();
            if (e instanceof ConstantExpression)
               {
               String endPos = ((ConstantExpression) e).getConstant().toDataString();
               t.setAttribute("endPos", endPos);
               }
            }
         else
            {
            t.setAttribute("endPos", null);
            }
         return t.toString();
         }
      else if (type.equals("RDF_STR"))
         {
         e = iter.next();
         if (e instanceof VariableExpression)
            {
            String var = ((VariableExpression) e).getVariable();
            String funcArgs = context.getVarMap().get(var).fst;
            t.setAttribute("args", funcArgs);
            }
         return t.toString();
         }
      else if (type.equals("RDF_STRSTARTS") || type.equals("RDF_STRENDS") || type.equals("RDF_STRBEFORE")
            || type.equals("RDF_STRAFTER"))
         {
         //
         // first argument is the string to check, either a variable
         //
         e = iter.next();
         if (e instanceof VariableExpression)
            {
            String var = ((VariableExpression) e).getVariable();
            String funcArgs = context.getVarMap().get(var).fst;
            t.setAttribute("var", funcArgs);
            }
         //
         // .. or the result of a nested function
         //
         else if (e instanceof BuiltinFunctionExpression)
            {
            String var = getSQLBuiltinFunctionExpression((BuiltinFunctionExpression) e, context);
            t.setAttribute("var", var);
            }
         //
         // the second arguments is the length we are looking for
         //
         e = iter.next();
         if (e instanceof ConstantExpression)
            {
            String value = ((ConstantExpression) e).getConstant().toDataString();
            t.setAttribute("value", value);
            }
         return t.toString();
         }
      else if (type.equals("RDF_REPLACE"))
         {
         //
         // first argument is the string to check, either a variable
         //
         e = iter.next();
         if (e instanceof VariableExpression)
            {
            String var = ((VariableExpression) e).getVariable();
            String funcArgs = context.getVarMap().get(var).fst;
            t.setAttribute("var", funcArgs);
            }
         //
         // .. or the result of a nested function
         //
         else if (e instanceof BuiltinFunctionExpression)
            {
            String var = getSQLBuiltinFunctionExpression((BuiltinFunctionExpression) e, context);
            t.setAttribute("var", var);
            }
         //
         // the second arguments is the pattern, where we should get rid of the double-quotes at the beginning/end
         //
         e = iter.next();
         if (e instanceof ConstantExpression)
            {
            String pat = ((ConstantExpression) e).getConstant().toDataString();
            t.setAttribute("pat", pat);
            }
         //
         // the third arguments is the replacing text, where again we should get rid of the double-quotes at the beginning/end
         //
         e = iter.next();
         if (e instanceof ConstantExpression)
            {
            String rep = ((ConstantExpression) e).getConstant().toDataString();
            t.setAttribute("rep", rep);
            }
         return t.toString();
         }
      else if (type.equals("RDF_STRLANG"))
         {
         //
         // first argument is the string to form
         //
         e = iter.next();
         if (e instanceof VariableExpression)
            {
            String var = ((VariableExpression) e).getVariable();
            String lexform = context.getVarMap().get(var).fst;
            t.setAttribute("lexform", lexform);
            }
         else if (e instanceof ConstantExpression)
            {
            String constExpr = ((ConstantExpression) e).getConstant().toDataString();
            t.setAttribute("lexform", new String("'" + constExpr + "'"));
            }
         else if (e instanceof BuiltinFunctionExpression)
            {
            String lexform = getSQLBuiltinFunctionExpression((BuiltinFunctionExpression) e, context);
            t.setAttribute("lexform", lexform);
            }
         //
         // Second argument is the language tag
         //
         e = iter.next();
         if (e instanceof ConstantExpression)
            {
            String lang = ((ConstantExpression) e).getConstant().toDataString();
            t.setAttribute("lang", lang);
            }
         return t.toString();
         }
      else if (type.equals("RDF_STRDT"))
         {
         //
         // first argument is the string to form
         //
         e = iter.next();
         if (e instanceof VariableExpression)
            {
            String var = ((VariableExpression) e).getVariable();
            String lexform = context.getVarMap().get(var).fst;
            t.setAttribute("lexform", lexform);
            }
         else if (e instanceof ConstantExpression)
            {
            String constExpr = ((ConstantExpression) e).getConstant().toDataString();
            t.setAttribute("lexform", new String("'" + constExpr + "'"));
            }
         else if (e instanceof BuiltinFunctionExpression)
            {
            String lexform = getSQLBuiltinFunctionExpression((BuiltinFunctionExpression) e, context);
            t.setAttribute("lexform", lexform);
            }
         //
         // Second argument is the type iri
         //
         e = iter.next();
         if (e instanceof ConstantExpression)
            {
            String typeiri = ((ConstantExpression) e).getConstant().toDataString();
            t.setAttribute("typeiri", typeiri);
            }
         return t.toString();
         }
      else if (type.equals("RDF_IF"))
         {
     	 Set<TypeMap.TypeCategory> types = new HashSet<TypeMap.TypeCategory>();
     	 for (Expression arg : exp.getArguments()) {
     		types.add(TypeMap.getTypeCategory(arg.getReturnType()));
     	 }
     	 TypeMap.TypeCategory castType = TypeMap.getCastTypeCategory(types);
     	 
         e = iter.next();
         if (e.getReturnType() == TypeMap.NONE_ID) {
        	 t.setAttribute("type1", castType);
         }
         t.setAttribute("expr1", getSQLExpression(e, context));

         e = iter.next(); 
         if (e.getReturnType() == TypeMap.NONE_ID) {
        	 t.setAttribute("type2", castType);
         }
         t.setAttribute("expr2", getSQLExpression(e, context));
         
         e = iter.next();
         if (e.getReturnType() == TypeMap.NONE_ID) {
        	 t.setAttribute("type3", castType);
         }
         t.setAttribute("expr3", getSQLExpression(e, context));

         return t.toString();
         }
      else if (type.equals("RDF_COALESCE"))
         {
          e = iter.next();
          t.setAttribute("expr1", getSQLExpression(e, context));
          e = iter.next(); 
          t.setAttribute("expr2", getSQLExpression(e, context));
  
         return t.toString();
         }
      else if (type.equals("RDF_SHA")) {
    	  exp.getBuiltinType();
    	  e = iter.next();
    	  t.setAttribute("args", getSQLExpression(e, context));
    	  String function = null;
    	  if (exp.getBuiltinType() == EBuiltinType.SHA1) {
    		  function = "SHA1";
    	  } else if (exp.getBuiltinType() == EBuiltinType.SHA256) {
    		  function = "SHA256";
    	  } else {
    		  function = "SHA512";
    	  }
    	  t.setAttribute("function", function);
    	  return t.toString();
      } else if (type.equals("RDF_MD5")) {
    	  e = iter.next();
    	  t.setAttribute("args", getSQLExpression(e, context));
    	  return t.toString();
      }
      else
         {
         while (iter.hasNext())
            {
            e = iter.next();
            s = getSQLExpression(e, context);
            if (!s.equals(EMPTY_STRING))
               args.add(s);
            }
         t = getInstanceOf(type);
         t.setAttribute(ARGS, args);
         return t.toString();
         }
      }





	private String handleExpressionForDatatype(FilterContext context,
			StringTemplate t, Expression e) throws SQLWriterException {
		t.setAttribute("iri", TypeMap.IRI_ID);
		t.setAttribute("blankNode", TypeMap.BLANK_NODE_ID);
		t.setAttribute("plainLiteral", TypeMap.SIMPLE_LITERAL_ID);
        t.setAttribute("startOfLangLiterals", TypeMap.LANG_START);
        t.setAttribute("endOfLangLiterals", TypeMap.LANG_END);


		if (e instanceof ConstantExpression) {
			return TypeMap.getTypedString(e.getReturnType());
		} if (e instanceof VariableExpression) {
			return handleDataTypeForVariable(context, e, t);
		} else if (e instanceof NumericExpression) {
	         
			List<String> types = new LinkedList<String>();
			getExpressionType(e, types, context);
			assert !types.isEmpty();
			types.add(Integer.toString(TypeMap.INTEGER_ID));
			
			StringTemplate nt = getInstanceOf("RDF_DATATYPE_NUMERIC_EXPRESSION");
			nt.setAttribute("types", types);
			String typeCol = nt.toString();
			t.setAttribute("typeCol", typeCol);
			t.setAttribute("dtTableName", store.getDataTypeTable());

			return t.toString();
		}

		// all other expressions
		t = getInstanceOf(RDF_DATATYPE_EXPRESSION);
		t.setAttribute("var", getSQLExpression(e, context));
		short retType = e.getReturnType();
		String str = TypeMap.getTypedString(retType);
		String xmlSchemaType = store.getDatatype(TypeMap.getCastNameForTypedString(str));
		t.setAttribute("xmlSchemaType", xmlSchemaType);
		t.setAttribute("returnType", str);
		
		return t.toString();
	}

	private void getExpressionType(Expression e, List<String> types, FilterContext context) {
		if (e instanceof VariableExpression)
		{
			String leftVar = ((VariableExpression) e).getVariable();
			String varType = context.getVarMap().get(leftVar).snd;
			if (varType == null) {
				varType = TypeMap.IRI_ID + "";
			} 
			types.add(varType);
		} else if (e instanceof ConstantExpression) {
			types.add(Integer.toString(e.getReturnType()));
		} else if (e instanceof NumericExpression) {
			getExpressionType(((NumericExpression) e).getLHS(), types, context);
			getExpressionType(((NumericExpression) e).getRHS(), types, context);
		} else if (e instanceof LongExpression<?>) {
			for (Expression sub : ((LongExpression<?>)e).getSubExpressions()) {
				getExpressionType(sub, types, context);
			}
		} else if (e.getReturnType() >= TypeMap.DATATYPE_NUMERICS_IDS_START && e.getReturnType() <= TypeMap.DATATYPE_NUMERICS_IDS_END) {
			for (Variable v : e.gatherVariables()) {
				String varType = context.getVarMap().get(v).snd;
				if (varType == null) {
					varType = TypeMap.IRI_ID + "";
				} 
				types.add(varType);
			}
		}
	}
	
	
private String handleDataTypeForVariable(FilterContext context, Expression e,
		StringTemplate t) {
	String fType;
	String var = ((VariableExpression) e).getVariable();
	 fType = context.getVarMap().containsKey(var) ? context.getVarMap().get(var).snd : null;
	 if (fType == null) {
		 return TypeMap.IRI_TYPE_IRI;
	 }
	 t.setAttribute("typeCol", fType);
	 t.setAttribute("dtTableName", store.getDataTypeTable());
	 return t.toString();
}

   private String getSQLFunctionCallExpression(FunctionCallExpression exp, FilterContext context) throws SQLWriterException
      {
      FunctionCall function = exp.getCall();
      IRI funcName = exp.getCall().getFunction();
      List<Expression> argExps = function.getArguments();
      Iterator<Expression> iter = argExps.iterator();
      List<String> args = new ArrayList<String>();
      Expression e;
      String s = null;
      while (iter.hasNext())
         {
         e = iter.next();
         if (e instanceof VariableExpression)
            {
        	 System.out.println("e= "+e+"\t e.var = "+((VariableExpression) e).getVariable());
            s = context.getVarMap().get(((VariableExpression) e).getVariable()).fst;
            }
         else if (e instanceof ConstantExpression)
            {
            s = ((ConstantExpression) e).toTypedString();
            }
         else
            {
            s = getSQLExpression(e, context);
            }
         if (!s.equals(EMPTY_STRING))
            args.add(getSID(s, store.getMaxStringLen()));
         }
      StringTemplate t;
      if (funcName.getValue().compareTo(STRSTARTS) == 0)
         {
         if (store.isLongString())
            {
            if (useLike)
               {
               t = getInstanceOf(RDF_STRSTARTS_LS_LIKE);

               }
            else
               {
               t = getInstanceOf(RDF_STRSTARTS_LS);
               t.setAttribute(LEN, args.get(1).length());
               }
            t.setAttribute(LS_TABLE, store.getLongStrings());
            }
         else
            {
            if (useLike)
               {
               t = getInstanceOf(RDF_STRSTARTS_LIKE);
               }
            else
               {
               t = getInstanceOf(RDF_STRSTARTS);
               t.setAttribute(LEN, args.get(1).length());
               }
            }
         t.setAttribute(VAR, args.get(0));
         t.setAttribute(VALUE, args.get(1));

         return t.toString();
         }
      else if (funcName.getValue().compareTo(STRENDS) == 0)
         {
         if (store.isLongString())
            {
            if (useLike)
               {
               t = getInstanceOf(RDF_STRENDS_LS_LIKE);

               }
            else
               {
               t = getInstanceOf(RDF_STRENDS_LS);
               t.setAttribute(LEN, args.get(1).length());
               }
            t.setAttribute(LS_TABLE, store.getLongStrings());
            }
         else
            {
            if (useLike)
               {
               t = getInstanceOf(RDF_STRENDS_LIKE);
               }
            else
               {
               t = getInstanceOf(RDF_STRENDS);
               t.setAttribute(LEN, args.get(1).length());
               }
            }

         t.setAttribute(VAR, args.get(0));
         t.setAttribute(VALUE, args.get(1));

         return t.toString();

         }
      else if (funcName.getValue().compareTo(STR) == 0)
         {
         t = getInstanceOf(RDF_STR);
         t.setAttribute(ARGS, args);
         return t.toString();
         }
      else if (funcName.getValue().equals(XSD_DATETIME))
         {
    	  Set<Short> validTypes = HashSetFactory.make();
    	  validTypes.add(TypeMap.DATETIME_ID);
    	  validTypes.add(TypeMap.SIMPLE_LITERAL_ID);
    	  validTypes.add(TypeMap.STRING_ID);  
          return handleNumericAndDateTimeCasts(context, function, args, validTypes, store.getDatatype("xs_dateTime"), TIMESTAMP_STR);
         }
      else if (funcName.getValue().equals(XSD_BOOLEAN))
      {
      t = getInstanceOf(XSD_BOOLEAN_CAST);
      String arg;
      if (args.size() > 0)
         {
         arg = getSQLExpression(function.getArguments().get(0), context);
         }
      else
         {
         arg = "false"; 
         }

      Expression expr = function.getArguments().get(0);
      if (expr instanceof VariableExpression) {
    	  String v = ((VariableExpression) expr).getVariable();
     	  if (context.getVarMap().containsKey(v) && context.getVarMap().get(v).snd != null) {
              t.setAttribute("arg", arg);
    		  t.setAttribute("type", context.getVarMap().get(v).snd);
    	  }
      } else {
    	  t.setAttribute("arg", getSQLExpression(expr, context));
    	  t.setAttribute("type", expr.getReturnType());
      }
      t.setAttribute("nrstart", TypeMap.DATATYPE_NUMERICS_IDS_START);
      t.setAttribute("nrend", TypeMap.DATATYPE_NUMERICS_IDS_END);
      t.setAttribute("simpleLit", TypeMap.SIMPLE_LITERAL_ID);
      t.setAttribute("string", TypeMap.STRING_ID);
      t.setAttribute("boolean", TypeMap.BOOLEAN_ID);
      
      return t.toString();
      } 
      else if (funcName.getValue().equals(TypeMap.INTEGER_IRI))
         {
       	  Set<Short> validTypes = HashSetFactory.make();
    	  getValidNumericTypes(validTypes);
     	      	       	    	  
          return handleNumericAndDateTimeCasts(context, function, args, validTypes, store.getDatatype("xs_integer"), INTEGER_STR);
         }
      else if (funcName.getValue().equals(TypeMap.DOUBLE_IRI))
      {
    	  Set<Short> validTypes = HashSetFactory.make();
    	  getValidNumericTypes(validTypes);
     	      	       	    	  
          return handleNumericAndDateTimeCasts(context, function, args, validTypes, store.getDatatype("xs_double"), DOUBLE_STR);
      }
      else if (funcName.getValue().equals(TypeMap.FLOAT_IRI))
      {
    	  Set<Short> validTypes = HashSetFactory.make();
    	  getValidNumericTypes(validTypes);
     	      	       	    	  
          return handleNumericAndDateTimeCasts(context, function, args, validTypes, store.getDatatype("xs_float"), FLOAT_STR);
      }
      else if (funcName.getValue().equals(TypeMap.DECIMAL_IRI))
      {
    	  Set<Short> validTypes = HashSetFactory.make();
    	  getValidNumericTypes(validTypes);
     	      	       	    	  
          return handleNumericAndDateTimeCasts(context, function, args, validTypes, store.getDatatype("xs_decimal"), DECIMAL_STR);
      }
      return "";
      }


private void getValidNumericTypes(Set<Short> validTypes) {
	validTypes.add(TypeMap.FLOAT_ID);
	  validTypes.add(TypeMap.SIMPLE_LITERAL_ID);
	  validTypes.add(TypeMap.STRING_ID);  
	  validTypes.add(TypeMap.DECIMAL_ID);
	  validTypes.add(TypeMap.INTEGER_ID);
	  validTypes.add(TypeMap.DOUBLE_ID);
	  validTypes.add(TypeMap.BOOLEAN_ID);
}


	private String handleNumericAndDateTimeCasts(FilterContext context,
		FunctionCall function, List<String> args, Set<Short> validTypes, String xmlType, String DBType) throws SQLWriterException {
		StringTemplate t = getInstanceOf(XSD_DATETIME_NUMERICS_CAST);
		Expression argExp = function.getArguments().get(0);
		String arg = null;
		if (args.size() > 0) {
			  arg = getSQLExpression(function.getArguments().get(0), context);
		}
		  
		assert arg != null;
		  
		Set<String> typesToCheck = HashSetFactory.make();
		if (argExp instanceof VariableExpression) {
			String typ = context.getVarMap().containsKey(((VariableExpression) argExp).getVariable()) ? context.getVarMap().get(((VariableExpression) argExp).getVariable()).snd : null;
			if (typ == null) {
				typ = Integer.toString(TypeMap.IRI_ID);
			}	
			for (Short type : validTypes) {
				typesToCheck.add(typ + "=" + Integer.toString(type));
				typesToCheck.add(typ + "=" + Integer.toString(type));
				typesToCheck.add(typ + "=" + Integer.toString(type)); 
			} 
			t.setAttribute(VALUE, arg); 
		} else {
			short ret = argExp.getReturnType();
			 
			if (!validTypes.contains(ret)) {
				typesToCheck.add(" 0 = 1 ");
			}
			t.setAttribute(VALUE, arg);
		}
		t.setAttribute("typeMatch", typesToCheck);
		t.setAttribute("xmlType", xmlType);
		assert store.getDatatype(DBType) != null : DBType + " not found";
		t.setAttribute("DBType", store.getDatatype(DBType));
		return t.toString();
	}

   private String getSQLAggregateExpression(AggregateExpression aggexp, FilterContext context) throws SQLWriterException
      {
      Set<Variable> vars = null;
      if (aggexp.getArgs() != null)
         {
         vars = aggexp.gatherVariables();
         }

      StringBuffer sql = new StringBuffer();
      StringTemplate t = null;

      if (AggregateExpression.EType.COUNT == aggexp.getAggregationType())
         {
         if (vars != null)
            {
            Iterator<Variable> varIterator = vars.iterator();
            boolean isFirstVar = true;
            while (varIterator.hasNext())
               {
               if (!isFirstVar)
                  {
                  sql.append("||");
                  }
               else
                  {
                  isFirstVar = false;
                  }
               sql.append(varIterator.next().getName());
               }
            }
         else
            {
            sql.append("*");
            }

         if (aggexp.isDistinct())
            {
            t = getInstanceOf(AGGREGATE_DISTINCT_COUNT);
            }
         else
            {
            t = getInstanceOf(AGGREGATE_COUNT);
            }
         t.setAttribute(VAR, sql.toString());

         return t.toString();
         }
      else if (aggexp.getAggregationType() == EType.GROUP_CONCAT) {
          t = getInstanceOf(GROUP_CONCAT);
          t.setAttribute(VAR, vars.iterator().next().getName());

          return t.toString();
      }
      else
         {
    	 if (context.doesConstantNeedTypeCheck(aggexp)) {
    		 t = getInstanceOf(AGGREGATE_FUNCTION_WITH_TYP_CHECK);
             t.setAttribute("typecheck", getNumericTypeCheckSQL(aggexp, context.getVarMap()));

    	 } else {
    		 t = getInstanceOf(AGGREGATE_FUNCTION);
    	 }
    	 
         EType func = aggexp.getAggregationType();
    	 if (AggregateExpression.EType.SAMPLE == aggexp.getAggregationType()) {
    		 func = EType.MIN;		// Kavitha: If this is a sample type, we need to basically return 1 item from the set
    	 }
         t.setAttribute(FUNCTION, func.toString().toLowerCase());	// XPath/XQuery functions are all lower case!
         if (aggexp.getArgs() instanceof VariableExpression) {
        	 t.setAttribute(VAR, vars.iterator().next().getName());
         } else {
        	 t.setAttribute(VAR, getSQLExpression(aggexp.getArgs(), context));
         }

         return t.toString();
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

   public static List<String> getVarFilterBinding(Set<Expression> filters, Variable var)
      {
      LinkedList<String> bindings = new LinkedList<String>();
      for (Expression e : filters)
         try
            {
            bindings.addAll(getVarFilterBinding(e, var));
            }
         catch (SQLWriterException e1)
            {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            }
      return bindings;
      }

   public static String
         getSQLFilter(Expression e, Variable entryVar, List<String> sqlVars, Store store) throws SQLWriterException
      {
      StringBuffer sqlFilter = new StringBuffer();
      if (e instanceof LogicalExpression)
         {
         List<Expression> components = ((LogicalExpression) e).getComponents();
         String separator = (((LogicalExpression) e).getType() == LogicalExpression.EExpressionType.OR) ? "OR" : "AND";

         for (int i = 0; i < components.size(); i++)
            {
            if (i > 0)
               sqlFilter.append(" " + separator + " ");
            sqlFilter.append(getSQLFilter(components.get(i), entryVar, sqlVars, store));
            }
         }
      else if (e instanceof RelationalExpression)
         {
         // List<String> leftPossibleTerms=new ArrayList<String>();
         // List<String> rightPossibleTerms=new ArrayList<String>();
         String operator = null;
         switch (((RelationalExpression) e).getOperator())
            {
            case EQUAL:
               operator = " = ";
               break;
            case NOT_EQUAL:
               operator = " != ";
               break;
            case GREATER:
               operator = " > ";
               break;
            case LESS:
               operator = " < ";
               break;
            case GREATER_EQUAL:
               operator = " >= ";
               break;
            case LESS_EQUAL:
               operator = " <= ";
               break;
            }

         if (((RelationalExpression) e).getLeft() instanceof VariableExpression)
            {
            VariableExpression ex = (VariableExpression) ((RelationalExpression) e).getLeft();
            Variable leftVar = new Variable(ex.getVariable());
            String rightVar = null;
            if (((RelationalExpression) e).getRight() instanceof ConstantExpression)
               {
               rightVar = ((RelationalExpression) e).getRight().toString();
               }
            else
               {
               // System.out.println("This filter is not supported");
               throw new SQLWriterException("Filter type not implemented");
               }

            if (entryVar.equals(leftVar))
               {
               sqlFilter.append("(");
               StringBuffer partSQLFilter = new StringBuffer();
               for (String sqlVar : sqlVars)
                  {
                  if (partSQLFilter.length() > 0)
                     partSQLFilter.append(" OR ");
                  partSQLFilter.append(sqlVar);
                  partSQLFilter.append(operator);
                  partSQLFilter.append("'");
                  partSQLFilter.append(getSID(rightVar, store.getMaxStringLen()));
                  partSQLFilter.append("' ");
                  }
               sqlFilter.append(partSQLFilter);
               sqlFilter.append(")");
               }
            else
               {
               // System.out.println("Possible exception: Filter is pushed at the wrong level");
               throw new SQLWriterException("Filter type not implemented");
               }
            }
         if (((RelationalExpression) e).getRight() instanceof VariableExpression)
            {
            VariableExpression ex = (VariableExpression) ((RelationalExpression) e).getRight();
            Variable rightVar = new Variable(ex.getVariable());
            String leftVar = null;
            if (((RelationalExpression) e).getLeft() instanceof ConstantExpression)
               {
               leftVar = ((RelationalExpression) e).getLeft().toString();
               }
            else
               {
               // System.out.println("This filter is not supported");
               throw new SQLWriterException("Filter type not implemented");
               }
            if (entryVar.equals(rightVar))
               {
               sqlFilter.append("(");
               StringBuffer partSQLFilter = new StringBuffer();
               for (String sqlVar : sqlVars)
                  {
                  if (partSQLFilter.length() > 0)
                     partSQLFilter.append(" OR ");
                  partSQLFilter.append(sqlVar);
                  partSQLFilter.append(operator);
                  partSQLFilter.append("'");
                  partSQLFilter.append(getSID(leftVar, store.getMaxStringLen()));
                  partSQLFilter.append("' ");
                  }
               sqlFilter.append(partSQLFilter);
               sqlFilter.append(")");
               }
            else
               {
               // System.out.println("Possible exception: Filter is pushed at the wrong level");
               }
            }
         }
      else if (e instanceof FunctionCallExpression)
         {
         IRI function = ((FunctionCallExpression) e).getCall().getFunction();
         List<Expression> arguments = ((FunctionCallExpression) e).getCall().getArguments();
         ArrayList<String> SQLArguments = new ArrayList<String>();
         for (Expression exp : arguments)
            SQLArguments.add(getSQLFilter(exp, entryVar, sqlVars, store));
         /*
          * if(function.getValue().compareTo("http://www.w3.org/2005/xpath-functions#starts-with")==0){ if(SQLArguments.get(0)!=null &&
          * SQLArguments.get(1)!=null){ String repl = SQLArguments.get(1); if(repl.startsWith("\"")&& repl.endsWith("\"")) SQLArguments.set(1,
          * repl.substring(1, repl.length()-1)); sqlFilter.append("("); StringBuffer partSQLFilter=new StringBuffer(); for(String sqlVar:sqlVars){
          * if(partSQLFilter.length()>0)partSQLFilter.append(" OR \n"); partSQLFilter.append(sqlVar); partSQLFilter.append(" LIKE '");
          * partSQLFilter.append(SQLArguments.get(1)); partSQLFilter.append("%' OR "); partSQLFilter.append(sqlVar);
          * partSQLFilter.append(" LIKE '\""); partSQLFilter.append(SQLArguments.get(1)); partSQLFilter.append("%'"); if(store.isLongString()){
          * partSQLFilter.append(" OR \n"); partSQLFilter.append(sqlVar); partSQLFilter.append(" IN ( SELECT ");
          * partSQLFilter.append(Constants.NAME_COLUMN_SHORT_STRING); partSQLFilter.append(" FROM "); partSQLFilter.append(store.getLongStrings());
          * partSQLFilter.append(" WHERE "); partSQLFilter.append(Constants.NAME_COLUMN_LONG_STRING); partSQLFilter.append(" LIKE '");
          * partSQLFilter.append(SQLArguments.get(1)); partSQLFilter.append("%' OR "); partSQLFilter.append(Constants.NAME_COLUMN_LONG_STRING);
          * partSQLFilter.append(" LIKE '\""); partSQLFilter.append(SQLArguments.get(1)); partSQLFilter.append("%' )"); } }
          * sqlFilter.append(partSQLFilter); sqlFilter.append(")"); } }
          */
         if (function.getValue().compareTo("http://www.w3.org/2005/xpath-functions#starts-with") == 0)
            {
            if (SQLArguments.get(0) != null && SQLArguments.get(1) != null)
               {
               String repl = SQLArguments.get(1);
               if (repl.startsWith("\"") && repl.endsWith("\""))
                  SQLArguments.set(1, repl.substring(1, repl.length() - 1));
               sqlFilter.append("(");
               StringBuffer partSQLFilter = new StringBuffer();
               for (String sqlVar : sqlVars)
                  {
                  if (partSQLFilter.length() > 0)
                     partSQLFilter.append(" OR \n");
                  partSQLFilter.append("SUBSTR(");
                  partSQLFilter.append(sqlVar);
                  partSQLFilter.append(",1,");
                  partSQLFilter.append(SQLArguments.get(1).length());
                  partSQLFilter.append(")='");
                  partSQLFilter.append(getSID(SQLArguments.get(1), store.getMaxStringLen()));
                  partSQLFilter.append("' OR ");
                  partSQLFilter.append("SUBSTR(");
                  partSQLFilter.append(sqlVar);
                  partSQLFilter.append(",1,");
                  partSQLFilter.append(SQLArguments.get(1).length() + 1);
                  partSQLFilter.append(")='\"");
                  partSQLFilter.append(getSID(SQLArguments.get(1), store.getMaxStringLen()));
                  partSQLFilter.append("'");
                  if (store.isLongString())
                     {
                     partSQLFilter.append(" OR \n");
                     partSQLFilter.append(sqlVar);
                     partSQLFilter.append(" IN ( SELECT ");
                     partSQLFilter.append(Constants.NAME_COLUMN_SHORT_STRING);
                     partSQLFilter.append(" FROM ");
                     partSQLFilter.append(store.getLongStrings());
                     partSQLFilter.append(" WHERE ");

                     partSQLFilter.append("SUBSTR(");
                     partSQLFilter.append(Constants.NAME_COLUMN_LONG_STRING);
                     partSQLFilter.append(",1,");
                     partSQLFilter.append(SQLArguments.get(1).length());
                     partSQLFilter.append(")='");
                     partSQLFilter.append(getSID(SQLArguments.get(1), store.getMaxStringLen()));
                     partSQLFilter.append("' OR ");
                     partSQLFilter.append("SUBSTR(");
                     partSQLFilter.append(Constants.NAME_COLUMN_LONG_STRING);
                     partSQLFilter.append(",1,");
                     partSQLFilter.append(SQLArguments.get(1).length() + 1);
                     partSQLFilter.append(")='\"");
                     partSQLFilter.append(getSID(SQLArguments.get(1), store.getMaxStringLen()));
                     partSQLFilter.append("')");

                     }
                  }
               sqlFilter.append(partSQLFilter);
               sqlFilter.append(")");
               }
            }
         if (function.getValue().compareTo("http://www.w3.org/2005/xpath-functions#ends-with") == 0)
            {
            if (SQLArguments.get(0) != null && SQLArguments.get(1) != null)
               {
               String repl = SQLArguments.get(1);
               if (repl.startsWith("\"") && repl.endsWith("\""))
                  SQLArguments.set(1, repl.substring(1, repl.length() - 1));
               sqlFilter.append("(");
               StringBuffer partSQLFilter = new StringBuffer();
               for (String sqlVar : sqlVars)
                  {
                  if (partSQLFilter.length() > 0)
                     partSQLFilter.append(" OR \n");
                  partSQLFilter.append(sqlVar);
                  partSQLFilter.append(" LIKE '%");
                  partSQLFilter.append(SQLArguments.get(1));
                  partSQLFilter.append("' OR ");
                  partSQLFilter.append(sqlVar);
                  partSQLFilter.append(" LIKE '%");
                  partSQLFilter.append(SQLArguments.get(1));
                  partSQLFilter.append("\"' ");
                  if (store.isLongString())
                     {
                     partSQLFilter.append(" OR \n");
                     partSQLFilter.append(sqlVar);
                     partSQLFilter.append(" IN ( SELECT ");
                     partSQLFilter.append(Constants.NAME_COLUMN_SHORT_STRING);
                     partSQLFilter.append(" FROM ");
                     partSQLFilter.append(store.getLongStrings());
                     partSQLFilter.append(" WHERE ");
                     partSQLFilter.append(Constants.NAME_COLUMN_LONG_STRING);
                     partSQLFilter.append(" LIKE '%");
                     partSQLFilter.append(SQLArguments.get(1));
                     partSQLFilter.append("' OR ");
                     partSQLFilter.append(Constants.NAME_COLUMN_LONG_STRING);
                     partSQLFilter.append(" LIKE '%");
                     partSQLFilter.append(SQLArguments.get(1));
                     partSQLFilter.append("\"' )");
                     }
                  }
               sqlFilter.append(partSQLFilter);
               sqlFilter.append(")");
               }
            }
         if (function.getValue().compareTo("http://www.w3.org/2001/XMLSchema#string") == 0)
            {
            return SQLArguments.get(0);
            }
         }
      else if (e instanceof BuiltinFunctionExpression)
         {
         BuiltinFunctionExpression b = (BuiltinFunctionExpression) e;
         EBuiltinType type = b.getBuiltinType();
         if (type == EBuiltinType.ISBLANK)
            {
            sqlFilter.append("(");
            StringBuffer partSQLFilter = new StringBuffer();
            for (String sqlVar : sqlVars)
               {
               if (partSQLFilter.length() > 0)
                  partSQLFilter.append(" OR ");
               partSQLFilter.append(sqlVar);
               partSQLFilter.append(" LIKE '");
               partSQLFilter.append("!_:");
               partSQLFilter.append("%' ESCAPE '!'");
               }
            sqlFilter.append(partSQLFilter);
            sqlFilter.append(")");
            }
         if (type == EBuiltinType.BOUND)
            {
            sqlFilter.append("(");
            StringBuffer partSQLFilter = new StringBuffer();
            for (String sqlVar : sqlVars)
               {
               if (partSQLFilter.length() > 0)
                  partSQLFilter.append(" OR ");
               partSQLFilter.append(sqlVar);
               partSQLFilter.append(" IS NOT NULL");
               }
            sqlFilter.append(partSQLFilter);
            sqlFilter.append(")");
            }
         else
            {
            throw new SQLWriterException("Filter type not implemented");
            }
         }
      else if (e instanceof VariableExpression)
         {
         if (((VariableExpression) e).getVariable().compareTo(entryVar.getName()) == 0)
            return ((VariableExpression) e).getVariable();
         else
            return null;
         }
      else if (e instanceof ConstantExpression)
         {
         return ((ConstantExpression) e).getConstant().toString();
         }
      else if (e instanceof UnaryExpression)
         {
         if (((UnaryExpression) e).getOperator() == EUnaryOp.NOT)
            {
            sqlFilter.append(" NOT ");
            sqlFilter.append(getSQLFilter(((UnaryExpression) e).getExpression(), entryVar, sqlVars, store));
            }
         else
            {
            throw new SQLWriterException("Filter type not implemented");
            }
         }
      else
         {
         // System.out.println("This filter is not implemented");
         }

      return sqlFilter.toString();
      }

   public String getLongStringOrderBY(String var, String tabName, String tabAlias, String varType, boolean isJoin)
      {
      StringTemplate t = getInstanceOf(LONG_STRING_ORDER_BY_VARIABLE);
      ;
      if (isJoin)
         {
         t = getInstanceOf(LONG_STRING_ORDER_BY_JOIN);
         }
      t.setAttribute(VARIABLE_NAME, var);
      t.setAttribute(TABLE_ALIAS, tabAlias);
      if (isJoin)
         {
         t.setAttribute(TABLE_NAME, tabName);
         if (varType != null)
            {
            t.setAttribute(VARIABLE_TYPE, varType);
            }
         else
            {
            t.setAttribute(VARIABLE_TYPE, TypeMap.IRI_ID);
            }
         }
      return t.toString();
      }
   
	private String handleRelationalsWithConstants(
			ConstantExpression constantExpression, Expression otherExpression,
			FilterContext context, ERelationalOp relationalOperator)
			throws SQLWriterException {

		StringTemplate t = null;

		Short rexpType = constantExpression.getReturnType();
		String constantTypeString = null;
/*
		if (otherExpression instanceof BuiltinFunctionExpression) {
			if (((BuiltinFunctionExpression) otherExpression).getBuiltinType() == EBuiltinType.LANG)
				constantTypeString = rexpType.toString();
		}
*/		
		// special case booleans
		if (rexpType == TypeMap.BOOLEAN_ID) {
			return handleBooleanConstant(relationalOperator, otherExpression,
					constantTypeString, context).toString();

		} else if (needsCaseAndTypeCheck(otherExpression, constantExpression, context).fst == true) {
			return handleConstantsNeedingCase(needsCaseAndTypeCheck(otherExpression, constantExpression, context), rexpType, relationalOperator, otherExpression, constantExpression, context);
		}
		
		t = getInstanceOf(RDF_OPERATOR);
		String left = getSQLExpression(otherExpression, context);
		String right = null;
		if (constantTypeString != null) {
			right = constantTypeString;
		} else {
			right = getSQLExpression(constantExpression, context);
		}

		setRelationalAttributes(relationalOperator, left, right, t);
		return t.toString();

	}

	private void setRelationalAttributes(ERelationalOp relationalOperator,
			String left, String right, StringTemplate t) {
		t.setAttribute(LEFT, left);
		t.setAttribute(RIGHT, right);
		t.setAttribute(OP, RelationalExpression.getOperatorSymbol(relationalOperator));
	}

	private Pair<Boolean, Boolean> needsCaseAndTypeCheck(Expression leftExpression, ConstantExpression rightExpression, FilterContext context) {
		boolean typeCheck = false;		// if the right is a constant, we always need a type check
		boolean caseNeeded = false;
		if (context.getPlanNode() == null) {
			return Pair.make(typeCheck, caseNeeded);
		}
		
		for (Variable v : leftExpression.getVariables()) {
			typeCheck = context.getPlanNode().isTypeCheckVariables(v).fst || typeCheck;
			caseNeeded = context.getPlanNode().isTypeCheckVariables(v).snd || caseNeeded;
		}
		// if the right expression is a constant and appears in a where clause, we will need to add type as a constraint
		
		typeCheck = context.doesConstantNeedTypeCheck(rightExpression);
		

 		return Pair.make(typeCheck, caseNeeded);
	}
	
   private StringTemplate getTemplate(Pair<Boolean, Boolean> needsCaseAndTypeCheck, String castType, String typeToCastTo)
      {
      if (needsCaseAndTypeCheck.snd == true)
         {
         if (castType != null)
            {
            StringTemplate t = getInstanceOf(TYPE_CHECK_WITH_CAST);
            t.setAttribute(CAST, castType);
            return t;
            }
         else
            {
        	StringTemplate t = getInstanceOf(TYPE_CHECK_CASE);
            t.setAttribute("typeToCastTo", typeToCastTo);
            return t;
            }
         }
      StringTemplate t = getInstanceOf(TYPE_CHECK);
      t.setAttribute("typeToCastTo", typeToCastTo);
      return t;
      }
   
   public static String getTypeToCastTo(Short rexpType) {
	   if (rexpType >= TypeMap.DATATYPE_NUMERICS_IDS_START && rexpType <= TypeMap.DATATYPE_NUMERICS_IDS_END) {
		   return "numeric";
	   } else if (rexpType == TypeMap.DATE_ID || rexpType == TypeMap.DATETIME_ID) {
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
	
   public static String getTypeTest(Short rexpType, String typeCol)
      {
      StringTemplate t;
      if (rexpType >= TypeMap.DATATYPE_NUMERICS_IDS_START && rexpType <= TypeMap.DATATYPE_NUMERICS_IDS_END)
         {
         t = getInstanceOf(BETWEEN_TYPE_TEST);
         t.setAttribute("typeCol", typeCol);
         t.setAttribute("start", TypeMap.DATATYPE_NUMERICS_IDS_START);
         t.setAttribute("end", TypeMap.DATATYPE_NUMERICS_IDS_END);
         }
      else
         {
         t = getInstanceOf(TYPE_TEST);
         t.setAttribute("typeCol", typeCol);
         t.setAttribute("val", rexpType);
         }
      return t.toString();
      }
   
   public static String getTypeTest(TypeMap.TypeCategory type, String typeCol)
   {
   Short rexpType = null;
   if (type == TypeMap.TypeCategory.NUMERIC) {
	   rexpType = TypeMap.DATATYPE_NUMERICS_IDS_START;
   } else if (type == TypeMap.TypeCategory.DATETIME) {
	   rexpType = TypeMap.DATETIME_ID;
   } else if (type == TypeMap.TypeCategory.DATE) {
	   rexpType = TypeMap.DATE_ID;
   }
   
   if (rexpType != null) {
	   return getTypeTest(rexpType, typeCol);
   } else {
	   return "";
   }
   }
   
	
	private String handleConstantsNeedingCase(Pair<Boolean, Boolean> needsCaseAndTypeCheck, Short rexpType, ERelationalOp relationalOperator, Expression otherExpression, ConstantExpression constantExpression, FilterContext context) throws SQLWriterException {
		assert needsCaseAndTypeCheck.fst == true;			// needing a type check is a requirement if case is turned on
		
		String left = getSQLExpression(otherExpression, context);
		
		String right = null;
		
		String castType = null;
		if (rexpType == TypeMap.DATETIME_ID) {
			castType = TIMESTAMP_STR; 
		} else if (rexpType == TypeMap.DATE_ID) {
			castType = DATE_STR;
		}
		StringTemplate t = getTemplate(needsCaseAndTypeCheck, castType, getTypeToCastTo(rexpType));
		
		if (castType != null) {
			t.setAttribute(CAST_FUNCTION_START, "XMLCAST ( XMLTEXT ");
			t.setAttribute(CAST_FUNCTION_END, ")"); 
		} 
		// KAVITHA: user defined types are not comparable, and only the equals operator applies to them.  If you see these, set the type constraint to NONE
		// because nothing should match.
		if (relationalOperator != ERelationalOp.EQUAL && rexpType >=TypeMap.USER_ID_START && rexpType < TypeMap.NONE_ID) {
			rexpType = TypeMap.NONE_ID;
		}
		
		assert otherExpression instanceof VariableExpression;
		
		String typeCol = context.getVarMap().get(((VariableExpression) otherExpression).getVariable()).snd;
		t.setAttribute("typeTest", getTypeTest(rexpType, typeCol));
		
		
		right = getSQLConstantExpressionAsType(constantExpression, context);

		setRelationalAttributes(relationalOperator, left, right, t);
		
		return t.toString();
	}

   private StringTemplate handleBooleanConstant(ERelationalOp relationalOperator, Expression otherExpression,
         String constantTypeString, FilterContext context) throws SQLWriterException
      {
      String left = getSQLExpression(otherExpression, context);
      StringTemplate t;
      if (constantTypeString.equalsIgnoreCase("'true'") || constantTypeString.equals("'1'"))
         {
         if (relationalOperator == ERelationalOp.EQUAL)
            t = getInstanceOf(IS_TRUE);
         else
            t = getInstanceOf(IS_NOT_TRUE);
         }
      else
         {
         if (relationalOperator == ERelationalOp.EQUAL)
            t = getInstanceOf(IS_FALSE);
         else
            t = getInstanceOf(IS_NOT_FALSE);
         }
      t.setAttribute(VALUE, left);
      return t;
      }

	private boolean isBooleanValue(String constantTypeString) {
		return constantTypeString.equalsIgnoreCase("'true'")
				|| constantTypeString.equals("'1'")
				|| constantTypeString.equalsIgnoreCase("'false'")
				|| constantTypeString.equals("'0'");
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


	private List<String> getStringTypeCheckSQL(Expression expression,
			Map<String, Pair<String, String>> varMap) {
		List<String> types = new LinkedList<String>();
		// KAVITHA THIS IS TEMP -- we can use the BETWEEN TYPE TEST once strings and plain lits
		// are consecutive
		for (Variable v : expression.gatherVariables()) {
			types.add(varMap.get(v.getName()).snd + "=" + TypeMap.STRING_ID);
			types.add(varMap.get(v.getName()).snd + "=" + TypeMap.SIMPLE_LITERAL_ID);
		}
		return types;
	}
	private List<String> getNumericTypeCheckSQL(Expression expression,
			Map<String, Pair<String, String>> varMap) {
		List<String> types = new LinkedList<String>();
		for (Variable v : expression.gatherVariables()) {
			StringTemplate t = getInstanceOf(BETWEEN_TYPE_TEST);
			t.setAttribute("typeCol", varMap.get(v.getName()).snd);
			t.setAttribute("start", TypeMap.DATATYPE_NUMERICS_IDS_START);
			t.setAttribute("end", TypeMap.DATATYPE_NUMERICS_IDS_END);
			types.add(t.toString());
		}
		return types;
	}

	private String getSQLRelationalExpression(RelationalExpression rexp,
			FilterContext context) throws SQLWriterException {
		String left = getSQLExpression(rexp.getLeft(), context);
		String right = getSQLExpression(rexp.getRight(), context);
		if ((left.equals(EMPTY_STRING)) || (right.equals(EMPTY_STRING)))
			return EMPTY_STRING;

		String symbol = rexp.getOperatorSymbol();
		if (symbol.equals(EMPTY_STRING))
			return EMPTY_STRING;
		StringTemplate t = null;

		Expression rightExpression = rexp.getRight();
		Expression leftExpression = rexp.getLeft();

		if (rightExpression instanceof ConstantExpression) {
			return handleRelationalsWithConstants(
					((ConstantExpression) rightExpression), leftExpression,
					context, rexp.getOperator());
		} else if (leftExpression instanceof ConstantExpression) {
			return handleRelationalsWithConstants(
					((ConstantExpression) leftExpression), rightExpression,
					context, rexp.getOperator());
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
				t = getInstanceOf(RDF_OPERATOR);
				

			} else {
				if (context.getVarDb2Type().get(lVarString) == Db2Type.DECFLOAT
						|| context.getVarDb2Type().get(rVarString) == Db2Type.DECFLOAT) {
					t = getInstanceOf(RDF_OPERATOR_NUM);
				} else if (context.getVarDb2Type().get(lVarString) == Db2Type.VARCHAR
						|| context.getVarDb2Type().get(rVarString) == Db2Type.VARCHAR) {
					t = getInstanceOf(RDF_OPERATOR);
					if (context.getVarMap().get(lVarString).snd != null) {
						t.setAttribute("left_type",
								context.getVarMap().get(lVarString).snd);
						t.setAttribute("right_type",
								context.getVarMap().get(rVarString).snd);
					}

				} else if (context.getVarDb2Type().get(lVarString) == Db2Type.TIMESTAMP
						|| context.getVarDb2Type().get(rVarString) == Db2Type.TIMESTAMP) {
					t = getInstanceOf(RDF_OPERATOR_DATE);
				} else {
					String simpleLitMatch = getSimpleLiteralMatch(context,
							lVarString, rVarString);
					if (rexp.getOperator() == ERelationalOp.NOT_EQUAL) {
						t = getInstanceOf(NEQ_CAST_ALL);
					} else if (rexp.getOperator() == ERelationalOp.EQUAL) {
						t = getInstanceOf(EQ_CAST_ALL);
					} else {
						t = getInstanceOf(GT_CAST_ALL);
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
		} else {	// KAVITHA: -- Constant-constant expressions?
			t = getInstanceOf(RDF_OPERATOR);
		}
		assert t != null;
		
		t.setAttribute(LEFT, left);
		t.setAttribute(RIGHT, right);
		t.setAttribute(OP, symbol);
		String lstr = getTypeToCastTo(leftExpression.getReturnType());
		String rstr = getTypeToCastTo(rightExpression.getReturnType());
		rstr = rstr == null? lstr : rstr;
		lstr = lstr == null? rstr : lstr;
		t.setAttribute("ltype", lstr);
		t.setAttribute("rtype", rstr);

		return t.toString();
	}


	private String getSimpleLiteralMatch(FilterContext context,
			String lVarString, String rVarString) {
		StringTemplate t;
		t = getInstanceOf(STRING_SIMPLE_LIT_MATCH);
		t.setAttribute("simple_lit_type", TypeMap.SIMPLE_LITERAL_ID);
		t.setAttribute("string_type", TypeMap.STRING_ID);

		t.setAttribute("ltype", (context.getVarMap()
				.get(lVarString).snd != null) ? context.getVarMap()
				.get(lVarString).snd : TypeMap.IRI_ID);
		t.setAttribute("rtype", (context.getVarMap()
				.get(rVarString).snd != null) ? context.getVarMap()
				.get(rVarString).snd : TypeMap.IRI_ID);
		
		String simpleLitMatch = t.toString();
		return simpleLitMatch;
	}

   public static String getSID(String value, int maxLength)
      {
      if (value.length() > maxLength)
         {
         try
            {
            return Constants.PREFIX_SHORT_STRING + HashingHelper.hashLongString(value);
            }
         catch (HashingException e)
            {
            }
         }
      return value;
      }

   }

