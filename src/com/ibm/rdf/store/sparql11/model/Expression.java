package com.ibm.rdf.store.sparql11.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.rdf.store.runtime.service.types.TypeMap;

/**
 * Base class for all expressions
 */
public abstract class Expression {
	public static enum EExpressionType { CONSTANT, BUILTIN_FUNC, VAR, FUNC_CALL, UNARY, AND, OR, RELATIONAL, ADDITIVE, MULTIPLICATIVE, NUMERIC, AGGREGATE, ONE_OF, UNDEF };
	public static enum EConstantType { LITERAL, IRI, INTEGER, DECIMAL, DOUBLE, BOOL };
	public static enum EBuiltinType { REGEX, STR, STRSTARTS, STRENDS, STRBEFORE, STRAFTER, LANG, LANGMATCHES, DATATYPE, BOUND, SAMETERM, ISIRI, ISURI, ISBLANK, ISLITERAL, ISNUMERIC, EXISTS, 
		NOT_EXISTS, CONTAINS, IRI, URI, BNODE, RAND, ABS, CEIL, FLOOR, ROUND, CONCAT, SUB_STRING_EXP, STRLEN, UCASE, LCASE, ENCODE_FOR_URI, YEAR, MONTH, DAY, HOURS, STRLANG, STRDT,
		MINUTES, SECONDS, TIMEZONE, TZ, STRUUID, UUID, NOW, REPLACE, MD5, SHA1, SHA224, SHA256, SHA512, IF, COALESCE, SHA384 };
	public static enum EFunctionType { STARTS_WITH, ENDS_WITH, DATETIME };
	public static enum EUnaryOp { MINUS, NOT };
	public static enum ERelationalOp { 
		EQUAL, 
		NOT_EQUAL, 
		LESS, 
		GREATER, 
		LESS_EQUAL, 
		GREATER_EQUAL } ;
	public static enum EAdditiveOp { PLUS, MINUS };
	public static enum EMultiplicativeOp { TIMES, DIVIDE };
	public static enum EOneOfOp { IN, NOT_IN };
	
	
	public List<Expression> getSubExpressions() {
		return Collections.emptyList();
	}
	
	private EExpressionType type;
	
	public Expression(EExpressionType type) {
		this.type = type;
	}
	
	public EExpressionType getType() { return type; }
	
	public abstract Short getReturnType();
	public abstract short getTypeEquality(Variable v);
	public abstract TypeMap.TypeCategory getTypeRestriction(Variable v);
	public abstract boolean containsCast(Variable v);
	
	public abstract boolean containsEBV();
	public abstract boolean containsBound();
	public abstract boolean containsNotBound();
	
	public abstract void renamePrefixes(String base, Map<String, String> declared, Map<String, String> internal);
	public abstract void reverseIRIs();
	public abstract Set<BlankNodeVariable> gatherBlankNodes();
	public abstract Set<Variable> gatherVariables();
	public abstract Set<Variable> getVariables();
	public abstract void traverse(IExpressionTraversalListener l);
	public abstract String getStringWithVarName();
	
	
}
