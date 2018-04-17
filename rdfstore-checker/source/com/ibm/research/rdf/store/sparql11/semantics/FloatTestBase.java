package com.ibm.research.rdf.store.sparql11.semantics;

import static com.ibm.research.rdf.store.sparql11.semantics.ExpressionUtil.bitWidth;
import static com.ibm.research.rdf.store.sparql11.semantics.FloatingPoint.exponent;
import static com.ibm.research.rdf.store.sparql11.semantics.FloatingPoint.mantissa;
import static com.ibm.research.rdf.store.sparql11.semantics.FloatingPoint.sign;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.ibm.wala.util.collections.HashSetFactory;

import kodkod.ast.Formula;
import kodkod.ast.IntExpression;
import kodkod.ast.Relation;
import kodkod.engine.Evaluator;
import kodkod.engine.Solver;
import kodkod.engine.config.Options;
import kodkod.engine.config.Options.IntEncoding;
import kodkod.engine.satlab.SATFactory;
import kodkod.instance.Bounds;
import kodkod.instance.Instance;
import kodkod.instance.Tuple;
import kodkod.instance.TupleFactory;
import kodkod.instance.Universe;

public abstract class FloatTestBase {

	private final Options options = new Options();
	{
		options.setSolver(SATFactory.MiniSat);
		options.setIntEncoding(IntEncoding.TWOSCOMPLEMENT);
		options.setBitwidth(bitWidth);
	}
	
	public static List<Object> makeAtoms() {
		int msb = bitWidth - 1;

		List<Object> atoms = new ArrayList<Object>();

		for(int i = 0; i < msb; i++) {
			atoms.add(Integer.valueOf(1<<i));
		}
		atoms.add(Integer.valueOf(-(1<<msb)));
		
		return atoms;
	}
	
	private Instance instance(Formula f, Set<Relation> vars) {
		int msb = bitWidth - 1;

		Universe u = new Universe(atoms);
		TupleFactory tf = u.factory();
		Bounds b = new Bounds(u);

		for(int i = 0; i < msb; i++) {
			Integer v = Integer.valueOf(1<<i);
			b.boundExactly(v.intValue(), tf.setOf(v));
		}
		b.boundExactly(-(1<<msb), tf.setOf(Integer.valueOf(-(1<<msb))));

		Set<Tuple> digits = HashSetFactory.make();
		for(Object o : atoms) {
			digits.add(tf.tuple(o));
		}
		for(Relation r : vars) {
			b.bound(r, tf.setOf(digits));
		}
		
		Solver solver = new Solver(options);
		
		return solver.solve(f, b).instance();
	}
	
	protected final List<Object> atoms;

	protected final Instance instance;

	protected final Evaluator eval;
	
	protected void dumpFloat(IntExpression f, String description) {
		System.err.println(description + " sign: " + eval.evaluate(sign(f)));
		System.err.println(description + " exponent: " + eval.evaluate(exponent(f)));
		System.err.println(description + " mantissa: " + eval.evaluate(mantissa(f)));
		System.err.println(description + " bits: " + Integer.toBinaryString(eval.evaluate(f)));
	}

	protected FloatTestBase(Formula f, Set<Relation> vars) {
		atoms = makeAtoms();
		instance = instance(f, vars);
		eval = new Evaluator(instance, options);
	}
	
	protected FloatTestBase() {
		this(Formula.TRUE, Collections.<Relation>emptySet());
	}

}
