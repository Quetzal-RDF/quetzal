package com.ibm.research.rdf.store.sparql11;

import static com.ibm.research.rdf.store.sparql11.FloatingPoint.floatMultiply;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import junit.framework.Assert;
import kodkod.ast.Formula;
import kodkod.ast.IntConstant;
import kodkod.ast.IntExpression;
import kodkod.ast.Relation;

@RunWith(Parameterized.class)
public class FloatDivideTests extends FloatTestBase {
	private static final Relation quotient = Relation.unary("quotient");

	private static Formula makeTest(float l, float r) {
		IntExpression qe = quotient.sum();
		IntConstant re = IntConstant.constant(Float.floatToIntBits(r));
		IntExpression mult = floatMultiply(qe, re);
		IntConstant le = IntConstant.constant(Float.floatToIntBits(l));
		return le.eq(mult);
	}
		
	private final float l;
	private final float r;
		
	public FloatDivideTests(final float l, final float r) {
		super(makeTest(l, r), Collections.singleton(quotient));
		this.l = l;
		this.r = r;
	}
		
	@Test
	public void testDivide() {
		float v = Float.intBitsToFloat(eval.evaluate(quotient.sum()));
		float qv = l/r;
		System.err.println(l + " / " + r + " = " + v);
		Assert.assertTrue("expected " + qv + " but got " + v, Float.compare(qv*r, v*r) == 0);
		Assert.assertTrue("expected " + qv + " but got " + v, Float.compare(l, v*r) == 0);
	}

	@Parameters
	public static Collection<Object[]> generateData() {
		 return Arrays.asList(new Object[][] {
			new Float[]{1f, 5f},
			new Float[]{21.2f, 8.8f},
			new Float[]{21.2f, -2f},
			new Float[]{121.1999f, -23.5f},
			new Float[]{1221.2f, -2331.5f},
			new Float[]{1221.2f, -331.5f},
			new Float[]{1221.2f, -23.5f},
			new Float[]{1221.1999f, -23.5f},
			new Float[]{.0021f, .8f},
			new Float[]{.0021f, -2.5f},
			new Float[]{21.2f, 1.5f},
			new Float[]{21.2f, .8f}
		 });
	}

}
