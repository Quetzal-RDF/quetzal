package com.ibm.research.rdf.store.sparql11.semantics;

import static com.ibm.research.rdf.store.sparql11.semantics.ExpressionUtil.fullUnsignedIntegerMultiply;
import static com.ibm.research.rdf.store.sparql11.semantics.FloatingPoint.exponentBias;
import static com.ibm.research.rdf.store.sparql11.semantics.FloatingPoint.exponentMask;
import static com.ibm.research.rdf.store.sparql11.semantics.FloatingPoint.floatAbs;
import static com.ibm.research.rdf.store.sparql11.semantics.FloatingPoint.floatAdd;
import static com.ibm.research.rdf.store.sparql11.semantics.FloatingPoint.floatCompare;
import static com.ibm.research.rdf.store.sparql11.semantics.FloatingPoint.floatDivide;
import static com.ibm.research.rdf.store.sparql11.semantics.FloatingPoint.floatMinus;
import static com.ibm.research.rdf.store.sparql11.semantics.FloatingPoint.floatMultiply;
import static com.ibm.research.rdf.store.sparql11.semantics.FloatingPoint.intToFloat;
import static com.ibm.research.rdf.store.sparql11.semantics.FloatingPoint.mantissa;
import static com.ibm.research.rdf.store.sparql11.semantics.FloatingPoint.mantissaBits;
import static com.ibm.research.rdf.store.sparql11.semantics.FloatingPoint.minusOne;

import org.junit.Test;

import com.ibm.wala.util.collections.Pair;

import junit.framework.Assert;
import kodkod.ast.IntConstant;
import kodkod.ast.IntExpression;

public class FloatExpressionTests extends FloatTestBase {
	
	private void testCastToFloat(int i) {		
		IntExpression cast = intToFloat(IntConstant.constant(i));
		int floatBits = Float.floatToIntBits((float)i);
		IntExpression raw = IntConstant.constant(floatBits);
		
		Assert.assertTrue("expected " + eval.evaluate(raw) + " but got " + eval.evaluate(cast), eval.evaluate(cast.eq(raw)));
	}

	private void testMaxSetBit(int i) {
		IntExpression cast = ExpressionUtil.maxSetBit(IntConstant.constant(i));

		Assert.assertEquals(Integer.numberOfLeadingZeros(i), 31 - eval.evaluate(cast));
	}
	
	private void testAdd(float l, float r) {
		IntExpression lv = IntConstant.constant(Float.floatToIntBits(l));
		IntExpression rv = IntConstant.constant(Float.floatToIntBits(r));
		
		IntExpression computedSum = floatAdd(lv, rv);
		IntExpression expectedSum = IntConstant.constant(Float.floatToIntBits(l+r));
		
		Assert.assertEquals("expected " + eval.evaluate(expectedSum) + " but got " + eval.evaluate(computedSum), eval.evaluate(expectedSum), eval.evaluate(computedSum));
	}

	private void testMultiply(float l, float r) {
		IntExpression lv = IntConstant.constant(Float.floatToIntBits(l));
		IntExpression rv = IntConstant.constant(Float.floatToIntBits(r));
		
		IntExpression computedSum = floatMultiply(lv, rv);
		IntExpression expectedSum = IntConstant.constant(Float.floatToIntBits(((float)l)*((float)r)));

		System.err.println(eval.evaluate(computedSum));
		dumpFloat(computedSum, "computed");
		dumpFloat(expectedSum, "expected");
		
		dumpFloat(lv, "left");
		dumpFloat(rv, "right");

		Assert.assertEquals("expected " + eval.evaluate(expectedSum) + " but got " + eval.evaluate(computedSum), eval.evaluate(expectedSum), eval.evaluate(computedSum));
	}

	private void testDivide(float l, float r) {
		IntExpression lv = IntConstant.constant(Float.floatToIntBits(l));
		IntExpression rv = IntConstant.constant(Float.floatToIntBits(r));
		
		IntExpression computedSum = floatDivide(lv, rv);
		float computedQuotient = Float.intBitsToFloat(eval.evaluate(computedSum));
		float expectedQuotient = ((float)l)/((float)r);

		System.err.println(eval.evaluate(computedSum));
		dumpFloat(computedSum, "computed");
		dumpFloat(IntConstant.constant(Float.floatToIntBits(expectedQuotient)), "expected");
		
		dumpFloat(lv, "left");
		dumpFloat(rv, "right");

		Assert.assertTrue("expected " + expectedQuotient + " but got " + computedQuotient, Float.compare(computedQuotient*r, expectedQuotient*r) == 0);
		Assert.assertTrue("expected " + expectedQuotient + " but got " + computedQuotient, Float.compare(computedQuotient*r, l) == 0);
	}

	private void testCompare(float l, float r) {
		IntExpression expected = IntConstant.constant(l > r? 1: l < r? -1: 0);
		IntExpression actual = floatCompare(
			IntConstant.constant(Float.floatToIntBits(l)),
			IntConstant.constant(Float.floatToIntBits(r)));

		Assert.assertEquals("expected " + eval.evaluate(actual) + " but got " + eval.evaluate(actual), eval.evaluate(expected), eval.evaluate(actual));
	}
	
	private void testFullIntegerMultiply(int l, int r) {
		IntExpression le = IntConstant.constant(l); 
		IntExpression re = IntConstant.constant(r);
		
		long product = (long)l * (long)r;	
		IntConstant lower = IntConstant.constant((int) (product & 0x00000000FFFFFFFFL));
		IntConstant upper = IntConstant.constant((int) ((product & 0xFFFFFFFF00000000L)>>32));
		
		Pair<IntExpression,IntExpression> computed = fullUnsignedIntegerMultiply(le, re);
		
		System.err.println(Integer.toBinaryString(l));
		System.err.println(Integer.toBinaryString(r));
		System.err.println(product);
		System.err.println(eval.evaluate(upper));
		System.err.println(eval.evaluate(lower));
		System.err.println(eval.evaluate(computed.snd));
		System.err.println(eval.evaluate(computed.fst));
		System.err.println(Integer.toBinaryString(eval.evaluate(upper)));
		System.err.println(Integer.toBinaryString(eval.evaluate(lower)));
		System.err.println(Integer.toBinaryString(eval.evaluate(computed.snd)));
		System.err.println(Integer.toBinaryString(eval.evaluate(computed.fst)));
		for(int i = 0; i < 32; i++) {
			System.err.println(Integer.toBinaryString(eval.evaluate(re.shr(IntConstant.constant(32-i-1)))));			
		}
		
		Assert.assertEquals(eval.evaluate(lower), eval.evaluate(computed.fst));
		Assert.assertEquals(eval.evaluate(upper), eval.evaluate(computed.snd));
	}
		
	@Test
	public void testSetBitZero() {
		testMaxSetBit(0);
	}

	@Test
	public void testSetBitOne() {
		testMaxSetBit(1);
	}

	@Test
	public void testSetBitTwo() {
		testMaxSetBit(2);
	}

	@Test
	public void testSetBitThree() {
		testMaxSetBit(3);
	}

	@Test
	public void testSetBitFour() {
		testMaxSetBit(4);
	}

	@Test
	public void testSetBitFive() {
		testMaxSetBit(5);
	}

	@Test
	public void testSetBitSix() {
		testMaxSetBit(6);
	}

	@Test
	public void testSetBitSeven() {
		testMaxSetBit(7);
	}

	@Test
	public void testSetBitEight() {
		testMaxSetBit(8);
	}

	@Test
	public void testSetBitNine() {
		testMaxSetBit(9);
	}

	@Test
	public void testSetBitTen() {
		testMaxSetBit(10);
	}

	@Test
	public void testSetBit13() {
		testMaxSetBit(13);
	}

	@Test
	public void testSetBit38() {
		testMaxSetBit(38);
	}

	@Test
	public void testSetBit107() {
		testMaxSetBit(107);
	}

	@Test
	public void testSetBit1355() {
		testMaxSetBit(1355);
	}

	@Test
	public void testSetBit54331() {
		testMaxSetBit(54331);
	}

	@Test
	public void testSetBitMinusOne() {
		testMaxSetBit(-1);
	}

	@Test
	public void testCastZero() {
		testCastToFloat(0);
	}

	@Test
	public void testCastOne() {
		testCastToFloat(1);
	}

	@Test
	public void testCastTwo() {
		testCastToFloat(2);
	}

	@Test
	public void testCastThree() {
		testCastToFloat(3);
	}
	
	@Test
	public void testCastFour() {
		testCastToFloat(4);
	}
	
	@Test
	public void testCastFive() {
		testCastToFloat(5);
	}
	
	@Test
	public void testCastSix() {
		testCastToFloat(6);
	}
	
	@Test
	public void testCastSeven() {
		testCastToFloat(7);
	}

	@Test
	public void testCastEight() {
		testCastToFloat(8);
	}

	@Test
	public void testCastNine() {
		testCastToFloat(9);
	}

	@Test
	public void testCastTen() {
		testCastToFloat(10);
	}

	@Test
	public void testCast20() {
		testCastToFloat(20);
	}
	
	@Test
	public void testCast30() {
		testCastToFloat(30);
	}

	@Test
	public void testCast31() {
		testCastToFloat(31);
	}

	@Test
	public void testCast107() {
		testCastToFloat(107);
	}

	@Test
	public void testCast54330() {
		testCastToFloat(54330);
	}

	@Test
	public void testCast54331() {
		testCastToFloat(54331);
	}

	@Test
	public void testCast1543311() {
		testCastToFloat(1543311);
	}

	@Test
	public void testCast23543310() {
		testCastToFloat(23543310);
	}

	@Test
	public void testCast23543311() {
		testCastToFloat(23543311);
	}

	@Test
	public void testCast23543312() {
		testCastToFloat(23543312);
	}

	@Test
	public void testCast23543313() {
		testCastToFloat(23543313);
	}

	@Test
	public void testCast33543310() {
		testCastToFloat(33543310);
	}

	@Test
	public void testCast33543311() {
		testCastToFloat(33543311);
	}

	@Test
	public void testCast231543311() {
		testCastToFloat(231543311);
	}

	@Test
	public void testCast1231543310() {
		testCastToFloat(1231543310);
	}

	@Test
	public void testCast1231543311() {
		testCastToFloat(1231543311);
	}

	@Test
	public void testCast1231543312() {
		testCastToFloat(1231543312);
	}

	@Test
	public void testCastMinusOne() {
		testCastToFloat(-1);
	}

	@Test
	public void testCastMinusTwo() {
		testCastToFloat(-2);
	}
	
	@Test
	public void testCastMinusTen() {
		testCastToFloat(-10);
	}
	
	@Test
	public void testCastMinus100() {
		testCastToFloat(-100);
	}

	@Test
	public void testCastMinus54331() {
		testCastToFloat(-54331);
	}

	@Test
	public void testCastMinus1543311() {
		testCastToFloat(-1543311);
	}

	@Test
	public void testCastMinus23543310() {
		testCastToFloat(-23543310);
	}

	@Test
	public void testCastMinus23543311() {
		testCastToFloat(-23543311);
	}

	@Test
	public void testCastMinus1231543311() {
		testCastToFloat(-1231543311);
	}

	@Test
	public void testAddOneOne() {
		testAdd(1f, 1f);
	}

	@Test
	public void testAdd10101One() {
		testAdd(10101f, 1f);
	}

	@Test
	public void testAdd10101Zero() {
		testAdd(10101f, 0f);
	}

	@Test
	public void testAdd10101p11One() {
		testAdd(10101.11f, 1f);
	}
	
	@Test
	public void testAdd10101p11x110p11() {
		testAdd(10101.11f, 110.11f);
	}

	@Test
	public void testAdd33101p11x133p11() {
		testAdd(33101.11f, 133.11f);
	}

	@Test
	public void testCompare2v1() {
		testCompare(2f, 1f);
	}

	@Test
	public void testMultiply4p1v2() {
		testMultiply(4.2f, 2f);
	}

	@Test
	public void testMultiply2p4p1() {
		testMultiply(2f, 4.2f);
	}
	@Test
	public void testMultiply11441p1v2p3() {
		testMultiply(11441.2f, 2.3f);
	}

	@Test
	public void testMultiply11441p1v2114p3() {
		testMultiply(11441.2f, 2114.3f);
	}

	@Test
	public void testMultiply211441p1v12114p3() {
		testMultiply(211441.2f, 12114.3f);
	}

	@Test
	public void testMultiply11441p1vMinus2114p3() {
		testMultiply(11441.2f, -2114.3f);
	}

	@Test
	public void testMultiplyp0021v2() {
		testMultiply(.0021f, 2f);
	}

	@Test
	public void testMultiplyp0021vMinus2() {
		testMultiply(.0021f, -2f);
	}

	@Test
	public void testMultiply1p8823529vp8() {
		testMultiply(1.8823529f, .8f);
	}

	@Test
	public void testMultiply51p965958v23p5() {
		testMultiply(51.965958f, 23.5f);
	}

	@Test
	public void testMultiply51p965954v23p5() {
		testMultiply(51.965954f, 23.5f);
	}

	@Test
	public void testFullIntegerMultiply141414t212121() {
		testFullIntegerMultiply(141414, 212121);
	}

	@Test
	public void testFullIntegerMultiply141614t213121() {
		testFullIntegerMultiply(141614, 213121);
	}

	@Test
	public void testFullIntegerMultiplyMantissa1p8823529p8() {
		testFullIntegerMultiply(eval.evaluate(mantissa(IntConstant.constant(Float.floatToIntBits(1.8823529f)))), eval.evaluate(mantissa(IntConstant.constant(Float.floatToIntBits(.8f)))));
	}
	
	@Test
	public void testFullIntegerMultiply5141614t213121() {
		testFullIntegerMultiply(5141614, 213121);
	}

	@Test
	public void testReciprocalInitialp8() {
		testReciprocalInitial(.8f);
	}

	@Test
	public void testReciprocalInitial2() {
		testReciprocalInitial(2f);
	}

	private void testReciprocalInitial(float v) {
		IntExpression ve = IntConstant.constant(Float.floatToIntBits(v));

		ve = floatAbs(ve);
		ve = ve.and(IntConstant.constant(exponentMask).not()).or(minusOne.plus(IntConstant.constant(exponentBias)).shl(IntConstant.constant(mantissaBits)));
		v = Float.intBitsToFloat(eval.evaluate(ve));
		
		IntExpression pe = floatMultiply(
			IntConstant.constant(Float.floatToIntBits(((float)32)/((float)17))), 
			ve);
		IntExpression xi = 
				floatMinus(
					IntConstant.constant(Float.floatToIntBits(((float)48)/((float)17))), 
					pe);

		float p = (((float)32)/((float)17))*v;
		float x0 = (float)48/(float)17 - p;		
		IntExpression x0e = IntConstant.constant(Float.floatToIntBits(x0));
	
		Assert.assertEquals(eval.evaluate(IntConstant.constant(Float.floatToIntBits(p))), eval.evaluate(pe));		
		Assert.assertEquals(eval.evaluate(x0e), eval.evaluate(xi));
	}
	
	@Test
	public void testDividep0021vp8() {
		testDivide(.0021f, .8f);
	}

	@Test
	public void testDivide21p2v1p5() {
		testDivide(21.2f, 1.5f);
	}

	@Test
	public void testDivide1221p2v1p5() {
		testDivide(1221.2f, 1.5f);
	}

	@Test
	public void testDivide1221p2v23p5() {
		testDivide(1221.2f, 23.5f);
	}

	@Test
	public void testDivide21p2vp8() {
		testDivide(21.2f, .8f);
	}

}
