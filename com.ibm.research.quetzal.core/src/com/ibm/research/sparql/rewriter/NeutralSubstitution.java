package com.ibm.research.sparql.rewriter;

import java.util.HashMap;
import java.util.Map;

import org.openrdf.query.algebra.Var;

public class NeutralSubstitution implements Substitution {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.research.sparql.rewriter.StatementUnifier.Substitution#compose
	 * (org.openrdf.query.algebra.Var, org.openrdf.query.algebra.Var)
	 */
	@Override
	public boolean compose(Var original, Var substituion) {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.research.sparql.rewriter.StatementUnifier.Substitution#get
	 * (org.openrdf.query.algebra.Var)
	 */
	@Override
	public Var get(Var var) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.research.sparql.rewriter.StatementUnifier.Substitution#getMap
	 * ()
	 */
	@Override
	public Map<Var, Var> getMap() {
		return new HashMap<Var, Var>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.research.sparql.rewriter.StatementUnifier.Substitution#isEmpty
	 * ()
	 */
	@Override
	public boolean isEmpty() {
		return true;
	}

}