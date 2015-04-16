package com.ibm.research.sparql.rewriter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openrdf.query.algebra.Var;

public class SubstitutionImpl implements Substitution {

	Map<Var, Var> map = new HashMap<Var, Var>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.research.sparql.rewriter.StatementUnifier.Substitution#compose
	 * (org.openrdf.query.algebra.Var, org.openrdf.query.algebra.Var)
	 */
	@Override
	public boolean compose(Var original, Var substitution) {
		Substitution unifier = StatementUnifier.getUnifier(original, substitution);
		Set<Var> trivialRemove = new HashSet<Var>();

		if (unifier == null)
			return false;
		if (unifier instanceof NeutralSubstitution)
			return true;
		for (Var key : map.keySet()) {
			Var value = map.get(key);
			if (value.equals(original))
				if (key.equals(substitution)) {
					// existing substitition now trivial, remove it.
					trivialRemove.add(key);
				} else {
					map.put(key, substitution);
				}
		}
		map.put(original, substitution);
		map.keySet().removeAll(trivialRemove);
		return true;
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
		return map.get(var);
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
		return map;
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
		return map.isEmpty();
	}

}