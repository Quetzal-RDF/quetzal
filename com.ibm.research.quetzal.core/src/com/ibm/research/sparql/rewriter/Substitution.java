package com.ibm.research.sparql.rewriter;

import java.util.Map;

import org.openrdf.query.algebra.Var;

public interface Substitution {

	boolean compose(Var original, Var substituion);

	Var get(Var var);

	Map<Var, Var> getMap();

	public boolean isEmpty();
}