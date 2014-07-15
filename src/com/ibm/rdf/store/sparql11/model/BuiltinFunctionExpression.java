package com.ibm.rdf.store.sparql11.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.rdf.store.runtime.service.types.TypeMap;
import com.ibm.wala.util.collections.HashSetFactory;

/**
 * Builtin function expressions.
 */
public class BuiltinFunctionExpression extends Expression
   {

   private EBuiltinType     builtinType;
   private List<Expression> arguments   = new ArrayList<Expression>();
   private Pattern          patternArgs = null;                       // used by certain built-in calls like EXITS, NOT_EXISTS etc.,
   private short            returnType;

   public BuiltinFunctionExpression(EBuiltinType builtin)
      {
      super(EExpressionType.BUILTIN_FUNC);
      builtinType = builtin;
      setReturnType(builtinType);
      }

   public BuiltinFunctionExpression(EBuiltinType builtin, Collection<? extends Expression> args)
      {
      super(EExpressionType.BUILTIN_FUNC);
      builtinType = builtin;
      arguments.addAll(args);
      setReturnType(builtinType);
      }

   public BuiltinFunctionExpression(EBuiltinType builtin, Expression... args)
      {
      super(EExpressionType.BUILTIN_FUNC);
      builtinType = builtin;
      setReturnType(builtinType);
      for (Expression e : args)
         if (e != null)
            arguments.add(e);
      }

   public BuiltinFunctionExpression(EBuiltinType builtin, Pattern args)
      {
      super(EExpressionType.BUILTIN_FUNC);
      builtinType = builtin;
      setReturnType(builtinType);
      patternArgs = args;
      }

   public EBuiltinType getBuiltinType()
      {
      return builtinType;
      }

   private void setReturnType(EBuiltinType type)
      {
      switch (builtinType)
         {
         case REGEX:
         case BOUND:
         case ISBLANK:
         case ISURI:
         case ISLITERAL:
         case ISIRI:
         case LANGMATCHES:
         case EXISTS:
         case NOT_EXISTS:
         case ISNUMERIC:
         case SAMETERM:
            returnType = TypeMap.BOOLEAN_ID;
            break;
         case COALESCE:
        	 short max = 0;
        	 for (Expression e : arguments) {
        		 if (e.getReturnType() > max) {
        			 System.out.println(e + " has: " + e.getReturnType());
        			 max = e.getReturnType();
        		 }
        	 }
        	 returnType = max;
        	 break;
         case STR:
            returnType = TypeMap.SIMPLE_LITERAL_ID;
            break;
         default:
            returnType = TypeMap.STRING_ID;
            break;
         }
      }

   public TypeMap.TypeCategory getTypeRestriction(Variable v)
      {
      if (!this.gatherVariables().contains(v))
         return TypeMap.TypeCategory.NONE;
      switch (builtinType)
         {
         case REGEX:
         case LANG:
         case STR:  
         case STRBEFORE:
         case STRAFTER:
         case LANGMATCHES:
            return TypeMap.TypeCategory.STRING;
         case IF:
         case COALESCE:
        	 for (Expression e : getArguments()) {
        		 if (e.getTypeRestriction(v) != TypeMap.TypeCategory.NONE) {
        			 return e.getTypeRestriction(v);
        		 }
        	 }

         }
      return TypeMap.TypeCategory.NONE;
      }

   public Short getReturnType()
      {
      return returnType;
      }

   public short getTypeEquality(Variable v)
      {
      return TypeMap.NONE_ID;
      }

   public String getBuiltinTypeString()
      {
      if (builtinType == EBuiltinType.REGEX)
         {
         return "RDF_REGEX";
         }
      else if (builtinType == EBuiltinType.CONTAINS)
         {
         return "LOCATE";
         }
      else if (builtinType == EBuiltinType.BOUND)
         {
         return "RDF_BOUND";
         }
      else if (builtinType == EBuiltinType.STR)
         {
         return "RDF_STR";
         }
      else if (builtinType == EBuiltinType.STRSTARTS)
         {
         return "RDF_STRSTARTS";
         }
      else if (builtinType == EBuiltinType.STRENDS)
         {
         return "RDF_STRENDS";
         }
      else if (builtinType == EBuiltinType.STRBEFORE)
         {
         return "RDF_STRBEFORE";
         }
      else if (builtinType == EBuiltinType.STRAFTER)
         {
         return "RDF_STRAFTER";
         }
      else if (builtinType == EBuiltinType.REPLACE)
         {
         return "RDF_REPLACE";
         }
      else if (builtinType == EBuiltinType.ISBLANK)
         {
         return "RDF_ISBLANK";
         }
      else if (builtinType == EBuiltinType.ISURI)
         {
         return "RDF_ISURI";
         }
      else if (builtinType == EBuiltinType.ISLITERAL)
         {
         return "RDF_ISLITERAL";
         }
      else if (builtinType == EBuiltinType.ISNUMERIC)
         {
         return "RDF_ISNUMERIC";
         }
      else if (builtinType == EBuiltinType.ISIRI)
         {
         return "RDF_ISIRI";
         }
      else if (builtinType == EBuiltinType.LANG)
         {
         return "RDF_LANG";
         }
      else if (builtinType == EBuiltinType.LANGMATCHES)
         {
         return "RDF_LANGMATCHES";
         }
      else if (builtinType == EBuiltinType.DATATYPE)
         {
         return "RDF_DATATYPE";
         }
      else if (builtinType == EBuiltinType.EXISTS)
         {
         return "RDF_EXISTS";
         }
      else if (builtinType == EBuiltinType.NOT_EXISTS)
         {
         return "RDF_NOT_EXISTS";
         }
      else if (builtinType == EBuiltinType.SAMETERM)
         {
         return "RDF_SAMETERM";
         }
      else if (builtinType == EBuiltinType.IRI)
         {
         return "RDF_IRI";
         }
      else if (builtinType == EBuiltinType.URI)
         {
         return "RDF_URI";
         }
      else if (builtinType == EBuiltinType.BNODE)
         {
         return "RDF_BNODE";
         }
      else if (builtinType == EBuiltinType.RAND)
         {
         return "RDF_RAND";
         }
      else if (builtinType == EBuiltinType.ABS)
         {
         return "RDF_ABS";
         }
      else if (builtinType == EBuiltinType.CEIL)
         {
         return "RDF_CEIL";
         }
      else if (builtinType == EBuiltinType.FLOOR)
         {
         return "RDF_FLOOR";
         }
      else if (builtinType == EBuiltinType.ROUND)
         {
         return "RDF_ROUND";
         }
      else if (builtinType == EBuiltinType.CONCAT)
         {
         return "RDF_CONCAT";
         }
      else if (builtinType == EBuiltinType.SUB_STRING_EXP)
         {
         return "RDF_SUB_STRING_EXP";
         }
      else if (builtinType == EBuiltinType.STRLEN)
         {
         return "RDF_STRLEN";
         }
      else if (builtinType == EBuiltinType.UCASE)
         {
         return "RDF_UCASE";
         }
      else if (builtinType == EBuiltinType.LCASE)
         {
         return "RDF_LCASE";
         }
      else if (builtinType == EBuiltinType.ENCODE_FOR_URI)
         {
         return "RDF_ENCODE_FOR_URI";
         }
      else if (builtinType == EBuiltinType.YEAR)
         {
         return "RDF_YEAR";
         }
      else if (builtinType == EBuiltinType.MONTH)
         {
         return "RDF_MONTH";
         }
      else if (builtinType == EBuiltinType.DAY)
         {
         return "RDF_DAY";
         }
      else if (builtinType == EBuiltinType.HOURS)
         {
         return "RDF_HOURS";
         }
      else if (builtinType == EBuiltinType.MINUTES)
         {
         return "RDF_MINUTES";
         }
      else if (builtinType == EBuiltinType.SECONDS)
         {
         return "RDF_SECONDS";
         }
      else if (builtinType == EBuiltinType.TIMEZONE)
         {
         return "RDF_TIMEZONE";
         }
      else if (builtinType == EBuiltinType.TZ)
         {
         return "RDF_TZ";
         }
      else if (builtinType == EBuiltinType.NOW)
         {
         return "RDF_NOW";
         }
      else if (builtinType == EBuiltinType.STRLANG)
         {
         return "RDF_STRLANG";
         }
      else if (builtinType == EBuiltinType.STRDT)
         {
         return "RDF_STRDT";
         }
      else if (builtinType == EBuiltinType.IF)
         {
         return "RDF_IF";
         }
      else if (builtinType == EBuiltinType.COALESCE)
         {
         return "RDF_COALESCE";
         }
      else if (builtinType == EBuiltinType.SHA1) {
    	  return "RDF_SHA";
      } else if (builtinType == EBuiltinType.SHA256) {
    	  return "RDF_SHA";
      } else if (builtinType == EBuiltinType.SHA512) {
    	  return "RDF_SHA";
      } else if (builtinType == EBuiltinType.MD5) {
    	  return "RDF_MD5";
      }
      return "";
      }

   public List<Expression> getArguments()
      {
      return arguments;
      }

   public Pattern getPatternArguments()
      {
      return patternArgs;
      }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
      {
      StringBuilder builder = new StringBuilder();
      switch (builtinType)
         {
         case EXISTS:
            builder.append("EXISTS (");
            builder.append(patternArgs.toString());
            break;
         case NOT_EXISTS:
            builder.append(" NOT EXISTS (");
            builder.append(patternArgs.toString());
            break;
         default:
            builder.append(builtinType + "(");
            for (int i = 0; i < arguments.size(); i++)
               {
               if (i > 0)
                  builder.append(", ");
               builder.append(arguments.get(i));
               }
         }
      builder.append(")");

      return builder.toString();
      }

   @Override
   public String getStringWithVarName()
      {
      StringBuilder builder = new StringBuilder();
      switch (builtinType)
         {
         case EXISTS:
            builder.append("EXISTS (");
            builder.append(patternArgs.toString());
            break;
         case NOT_EXISTS:
            builder.append(" NOT EXISTS (");
            builder.append(patternArgs.toString());
            break;
         default:
            builder.append(builtinType + "(");
            for (int i = 0; i < arguments.size(); i++)
               {
               if (i > 0)
                  builder.append(", ");
               builder.append(arguments.get(i).getStringWithVarName());
               }
         }
      builder.append(")");

      return builder.toString();
      }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode()
      {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((arguments == null) ? 0 : arguments.hashCode());
      result = prime * result + ((builtinType == null) ? 0 : builtinType.hashCode());
      result = prime * result + ((patternArgs == null) ? 0 : patternArgs.hashCode());
      return result;
      }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj)
      {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      BuiltinFunctionExpression other = (BuiltinFunctionExpression) obj;
      if (arguments == null)
         {
         if (other.arguments != null)
            return false;
         }
      else if (!arguments.equals(other.arguments))
         return false;
      if (builtinType != other.builtinType)
         return false;
      if (patternArgs == null)
         if (other.patternArgs != null)
            return false;
         else if (!patternArgs.equals(other.patternArgs))
            return false;
      return true;
      }

   /*
    * (non-Javadoc)
    * 
    * @see com.ibm.rdf.store.sparql11.model.Expression#renamePrefixes(java.lang.String, java.util.Map, java.util.Map)
    */
   @Override
   public void renamePrefixes(String base, Map<String, String> declared, Map<String, String> internal)
      {
      for (Expression e : arguments)
         e.renamePrefixes(base, declared, internal);
      if (patternArgs != null)
         patternArgs.renamePrefixes(base, declared, internal);
      }

   @Override
   public void reverseIRIs()
      {
      for (Expression e : arguments)
         e.reverseIRIs();
      if (patternArgs != null)
         patternArgs.reverseIRIs();
      }

   /*
    * (non-Javadoc)
    * 
    * @see com.ibm.rdf.store.sparql11.model.Expression#gatherBlankNodes()
    */
   @Override
   public Set<BlankNodeVariable> gatherBlankNodes()
      {
      Set<BlankNodeVariable> ret = new HashSet<BlankNodeVariable>();
      for (Expression e : arguments)
         ret.addAll(e.gatherBlankNodes());
      if (patternArgs != null)
         ret.addAll(patternArgs.gatherBlankNodes());
      return ret;
      }

   /*
    * (non-Javadoc)
    * 
    * @see com.ibm.rdf.store.sparql11.model.Expression#gatherVariables()
    */
   @Override
   public Set<Variable> gatherVariables()
      {
      Set<Variable> ret = HashSetFactory.make(getVariables());
      for (Expression e : arguments)
         ret.addAll(e.gatherVariables());
      return ret;
      }

   @Override
   public Set<Variable> getVariables()
      {
      if (patternArgs != null)
         {
         return patternArgs.gatherVariables();
         }
      else
         {
         return Collections.emptySet();
         }
      }

   public boolean isExists()
      {
      return builtinType == EBuiltinType.EXISTS || builtinType == EBuiltinType.NOT_EXISTS;
      }

   /*
    * (non-Javadoc)
    * 
    * @see com.ibm.rdf.store.sparql11.model.Expression#traverse(com.ibm.rdf.store.sparql11.model.IExpressionTraversalListener)
    */
   @Override
   public void traverse(IExpressionTraversalListener l)
      {
      l.startExpression(this);
      for (Expression e : arguments)
         e.traverse(l);
      l.endExpression(this);
      }

   public boolean containsEBV()
      {
      return false;
      }

   public boolean containsBound()
      {
      if (builtinType == EBuiltinType.BOUND)
         return true;
      else
         return false;
      }

   public boolean containsNotBound()
      {
      return false;
      }

   public boolean containsCast(Variable v)
      {
      for (Expression e : arguments)
         {
         if (e.containsCast(v))
            return true;
         }
      return false;
      }

   public void setPatternArgument(Pattern nc)
      {
      patternArgs = nc;
      }
   }
