package com.ibm.research.rdf.store.sparql11.semantics;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;

import com.ibm.wala.util.collections.Pair;

import kodkod.ast.Expression;
import kodkod.ast.Formula;
import kodkod.ast.IntConstant;
import kodkod.ast.IntExpression;
import kodkod.ast.operator.ExprCastOperator;
import kodkod.ast.operator.IntCompOperator;

public class ExpressionUtil {

	public static final int bitWidth = 32;
	
	public static final String xsdBooleanType = "http://www.w3.org/2001/XMLSchema#boolean";

	public static final String xsdIntegerType = "http://www.w3.org/2001/XMLSchema#integer";

	public static final String xsdDecimalType = "http://www.w3.org/2001/XMLSchema#decimal";

	public static final String xsdFloatType = "http://www.w3.org/2001/XMLSchema#float";

	public static final String xsdDoubleType = "http://www.w3.org/2001/XMLSchema#double";

	public static final String xsdStringType = "http://www.w3.org/2001/XMLSchema#string";

	public static final String rdfLangStringType = "http://www.w3.org/1999/02/22-rdf-syntax-ns#langString";
	
	public static final String xsdDateType = "http://www.w3.org/2001/XMLSchema#date";

	public static final String xsdDateTimeType = "http://www.w3.org/2001/XMLSchema#dateTime";

	public static final Collection<String> numericTypeNames = Arrays.asList(xsdIntegerType, xsdDecimalType, xsdFloatType, xsdDoubleType);

	public static final Collection<String> floatTypeNames = Arrays.asList(xsdIntegerType, xsdDecimalType, xsdFloatType, xsdDoubleType);

	public static final Collection<String> dateTypeNames = Arrays.asList(xsdDateType, xsdDateTimeType);

	public static Formula isNumeric(Expression e) {
		return (e.join(QuadTableRelations.literalDatatypes).intersection(QuadTableRelations.numericTypes)).one();
	}

	public static Formula isNumeric(Pair<Expression,IntExpression> e) {
		return e.snd != null? Formula.TRUE: isNumeric(e.fst);
	}
	
	public static Formula isDate(Expression e) {
		return (e.join(QuadTableRelations.literalDatatypes).intersection(QuadTableRelations.dateTypes)).one();
	}

	public static Formula isBoolean(Expression e) {
		return (e.join(QuadTableRelations.literalDatatypes).intersection(QuadTableRelations.booleanTypes)).one();
	}

	public static Formula isString(Expression e) {
		return (e.join(QuadTableRelations.literalDatatypes).intersection(QuadTableRelations.stringTypes)).one();
	}

	public static Formula isLiteral(Expression e) {
		return (e.join(QuadTableRelations.literalValues)).some();
	}

	public static Formula isBlank(Expression e) {
		return e.in(QuadTableRelations.blankNodes);
	}

	public static Formula isSimple(Expression e) {
		return isLiteral(e).and(e.join(QuadTableRelations.literalDatatypes).no()).and(e.join(QuadTableRelations.literalLanguages).no());
	}

	public static Expression language(Expression e) {
		return e.join(QuadTableRelations.literalLanguages)/*.join(QuadTableRelations.languageCaseMatch)*/;
	}
	
	public static Formula isLanguage(Expression e) {
		return e.join(QuadTableRelations.literalLanguages).one();
	}

	public static Expression value(Expression e) {
		return e.join(QuadTableRelations.literalValues);
	}

	public static Expression type(Expression e) {
		return e.join(QuadTableRelations.literalDatatypes);
	}

	public static IntExpression intValue(Pair<Expression,IntExpression> e) {
		if (e.snd != null) {
			return e.snd;
		} else {
			return intValue(e.fst);
		}
	}
	
	public static IntExpression intValue(Expression e) {
		return value(e).apply(ExprCastOperator.SUM);
	}
	
	public static Formula isStringOrSimple(Expression e) {
		return isString(e).or(isSimple(e));
	}
	
	public static Formula string_equal(Expression l, Expression r, boolean negated) {
		Formula test = value(l).eq(value(r));
		if (negated) {
			test = test.not();
		}
		return isStringOrSimple(l).and(isStringOrSimple(r)).and(test);
	}

	public static Formula string_less(Expression l, Expression r) {
		return isStringOrSimple(l).and(isStringOrSimple(r)).and(value(l).product(value(r)).intersection(QuadTableRelations.stringOrder.closure()).one());
	}

	public static Formula string_greater(Expression l, Expression r) {
		return isStringOrSimple(l).and(isStringOrSimple(r)).and(value(r).product(value(l)).intersection(QuadTableRelations.stringOrder.closure()).one());
	}

	public static Formula language_equal(Expression l, Expression r, boolean negated) {
		Formula test = value(l).eq(value(r));
		if (negated) {
			test = test.not();
		}
		return isLanguage(l).and(isLanguage(r)).and(test).and(language(l).eq(language(r)));
	}

	public static Formula numeric_test(Pair<Expression,IntExpression> l, Pair<Expression,IntExpression> r, IntCompOperator op, boolean negated) {
		Formula test = intValue(l).compare(op, intValue(r));
		if (negated) {
			test = test.not();
		}
		return isNumeric(l).and(isNumeric(r)).and(test);
	}

	public static Formula numeric_test(Expression l, Expression r, IntCompOperator op, boolean negated) {
		Formula test = intValue(l).compare(op, intValue(r));
		if (negated) {
			test = test.not();
		}
		return isNumeric(l).and(isNumeric(r)).and(test);
	}

	public static Formula isUnknown(Expression e) {
		return e.join(QuadTableRelations.literalDatatypes).some().and(e.join(QuadTableRelations.literalDatatypes).intersection(QuadTableRelations.knownTypes).no());
	}
	
	public static Formula unknown_equal(Expression l, Expression r, boolean negated) {
		Formula test = type(l).eq(type(r));
		if (negated) {
			test = test.not();
		}
		return isUnknown(l).and(isUnknown(r)).and(value(l).eq(value(r))).and(test);
	}
	
	public static Formula equal_test(Pair<Expression,IntExpression> l, Pair<Expression,IntExpression> r) {
		if (l.snd != null || r.snd != null) {
			Formula f = intValue(l).eq(intValue(r));
			if (r.fst != null) {
				f = isNumeric(r.fst).and(f);
			}
			if (l.fst != null) {
				f = isNumeric(l.fst).and(f);
			}			
 			return f;
		} else {
			return equal_test(l.fst, r.fst);
		}
	}
	
	public static Formula equal_test(Expression l, Expression r) {
		return string_equal(l, r, false)
				.or(numeric_test(l, r, IntCompOperator.EQ, false))
				.or(language_equal(l, r, false))
				.or(unknown_equal(l, r, false))
				.or(isLiteral(l).not().and(isLiteral(r).not()).and(l.eq(r)));
	}

	public static Formula not_equal_test(Pair<Expression,IntExpression> l, Pair<Expression,IntExpression> r) {
		if (l.snd != null || r.snd != null) {
			return isNumeric(l.fst).and(isNumeric(r.fst)).and(intValue(l).eq(intValue(r)).not()); 
		} else {
			return not_equal_test(l.fst, r.fst);
		}
	}

	public static Formula not_equal_test(Expression l, Expression r) {
		return string_equal(l, r, true)
				.or(numeric_test(l, r, IntCompOperator.EQ, true))
				.or(language_equal(l, r, true))
				.or(unknown_equal(l, r, true))
				.or(isLiteral(l).not().and(isLiteral(r).not()).and(l.eq(r).not()));
	}
	
	public static Formula less_test(Expression l, Expression r) {
		return numeric_test(l, r, IntCompOperator.LT, false).or(string_less(l, r));
	}

	public static Formula greater_test(Expression l, Expression r) {
		return numeric_test(l, r, IntCompOperator.GT, false).or(string_greater(l, r));
	}

	public static Formula less_test(Pair<Expression,IntExpression> l, Pair<Expression,IntExpression> r) {
		return numeric_test(l, r, IntCompOperator.LT, false).or(string_less(l.fst, r.fst));
	}

	public static Formula greater_test(Pair<Expression,IntExpression> l, Pair<Expression,IntExpression> r) {
		return numeric_test(l, r, IntCompOperator.GT, false).or(string_greater(l.fst, r.fst));
	}

	public static IntExpression plus(Pair<Expression,IntExpression> l, Pair<Expression,IntExpression> r) {
		return plus(intValue(l), intValue(r));
	}
	
	public static IntExpression plus(IntExpression l, IntExpression r) {
		return l.plus(r);
	}

	public static IntExpression minus(Pair<Expression,IntExpression> l, Pair<Expression,IntExpression> r) {
		return minus(intValue(l), intValue(r));
	}
	
	public static IntExpression minus(IntExpression l, IntExpression r) {
		return l.minus(r);
	}

	public static IntExpression minus(Pair<Expression,IntExpression> r) {
		return minus(intValue(r));
	}
	
	public static IntExpression minus(IntExpression r) {
		return IntConstant.constant(0).minus(r);
	}

	public static IntExpression times(Pair<Expression,IntExpression> l, Pair<Expression,IntExpression> r) {
		return times(intValue(l), intValue(r));
	}
	
	public static IntExpression times(IntExpression l, IntExpression r) {
		return l.multiply(r);
	}
	
	public static IntExpression divide(IntExpression l, IntExpression r) {
		return l.divide(r);
	}
	
	public static IntExpression maxSetBit(IntExpression intValue) {
		IntExpression bit = IntConstant.constant(-1);
		for(int i = 0; i < bitWidth-1; i++) {
			bit = IntConstant.constant(0).eq(IntConstant.constant(1<<i).and(intValue)).not()
				.thenElse(IntConstant.constant(i), bit);
		}
		return intValue.lt(IntConstant.constant(0)).thenElse(IntConstant.constant(bitWidth-1), bit);
	}
	
	public static final IntConstant zero = IntConstant.constant(0);
	public static final IntConstant one = IntConstant.constant(1);

	private static Formula lsb(IntExpression l) {
		return l.and(one).eq(zero).not();
	}
	
	private static Formula unsignedOverflow(IntExpression l, IntExpression r) {
		Formula carryFromLSB = lsb(l).and(lsb(r));
		IntExpression shiftedSum = l.shr(one).plus(r.shr(one));
		shiftedSum = carryFromLSB.thenElse(shiftedSum.plus(one), shiftedSum);
		return shiftedSum.and(one.shl(IntConstant.constant(31))).eq(zero).not();
	}

	public static Pair<IntExpression,IntExpression> fullUnsignedIntegerMultiply(IntExpression ml, IntExpression mr) {
		IntExpression low = zero;
		IntExpression high = zero;
		
		for(int i = 0; i < bitWidth; i++) {
			// add with carry
			high = 
				mr.and(IntConstant.constant(1<<i)).eq(zero)
					.thenElse(high, 
						unsignedOverflow(low, ml.shl(IntConstant.constant(i)))
							.thenElse(high.plus(one), high));
			low = mr.and(IntConstant.constant(1<<i)).eq(zero).thenElse(low, low.plus(ml.shl(IntConstant.constant(i))));
			if (i != 0) {
				high = mr.and(IntConstant.constant(1<<i)).eq(zero).thenElse(high, high.plus(ml.shr(IntConstant.constant(bitWidth-i))));			
			}
		}
		
		return Pair.make(low, high);
	}

	public static URI typeURI(String typeName) {
		try {
			return new URI(typeName);
		} catch (URISyntaxException e) {
			assert false : e.toString();
			throw new RuntimeException(e);
		}
	}

}
