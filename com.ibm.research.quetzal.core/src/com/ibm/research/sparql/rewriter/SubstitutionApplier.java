package com.ibm.research.sparql.rewriter;

import org.openrdf.query.algebra.Var;
import org.openrdf.query.algebra.helpers.QueryModelVisitorBase;

public class SubstitutionApplier extends QueryModelVisitorBase<RuntimeException> {

	Substitution s;

	/**
	 * 
	 */
	public SubstitutionApplier(Substitution s) {
		this.s = s;
	}

	@Override
	public void meet(Var var) {
		Var replacement = s.get(var);
		if (replacement == null)
			return;
		var.setName(replacement.getName());
		var.setConstant(replacement.isConstant());
		var.setValue(replacement.getValue());
	}

	// @Override
	// public void meet(ProjectionElem projElem)
	// throws RuntimeException
	// {
	// // if (projElem.getSourceName().equals(oldName)) {
	// // projElem.setSourceName(newName);
	// // }
	// }

}
