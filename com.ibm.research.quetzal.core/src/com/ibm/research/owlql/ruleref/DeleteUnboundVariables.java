/******************************************************************************
 * Copyright (c) 2015 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *****************************************************************************/
package com.ibm.research.owlql.ruleref;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLPropertyExpression;

import com.ibm.research.owlql.rule.AtomicFormula;
import com.ibm.research.owlql.rule.Expr;
import com.ibm.research.owlql.rule.Rule;
import com.ibm.research.owlql.rule.RuleSystem;
import com.ibm.research.owlql.rule.VariableExpr;

/**
 * Delete unbound variable
 * @author achille
 *
 */
public class DeleteUnboundVariables {

	protected OWLDataFactory fac;

	public DeleteUnboundVariables(OWLDataFactory fac) {
		super();
		this.fac = fac;
	}
	
	public Rule deleteUnboundVariables(Rule r) {
		Set<VariableExpr> uvars = new HashSet<VariableExpr>(r.getUnboundVariables()); 
		if (uvars.isEmpty()) {
			return r;
		}
		AtomicFormula newHead = r.getHead();
		List<AtomicFormula> newBody = new LinkedList<AtomicFormula>(); 
		for (AtomicFormula af: r.getBody()) {
			if (!(af.getPredicate() instanceof DLAnnotatedPredicate)) {
				newBody.add(af);
				continue;
			}
			DLAnnotatedPredicate p = (DLAnnotatedPredicate) af.getPredicate();
			if (p.getArity()==0) {
				newBody.add(af);
			} else if (p.getArity() == 1) {
				assert p.getClassAnnotation()!= null : p;
				Expr e = af.getArguments().get(0);
				if (uvars.contains(e)) {
					// p_A(unbound) --> p_A()
					newBody.add(new AtomicFormula(
							p.decreaseArity(), new ArrayList<Expr>(0)));
				} else {
					newBody.add(af);
				}
			} else if (p.getArity() == 2) {
				assert p.getPropertyAnnotation()!= null : p;
				Expr e1 = af.getArguments().get(0);
				Expr e2 = af.getArguments().get(1);
				
				if (!uvars.contains(e1) && !uvars.contains(e2)) {
					newBody.add(af);
				} else if (!uvars.contains(e1) && uvars.contains(e2)) {
					// p_R(bound, unbound) --> p_some(R)(bound)
					OWLPropertyExpression prop = p.getPropertyAnnotation();
					OWLClassExpression some ;
					if (prop.isObjectPropertyExpression()) {
						some =fac.getOWLObjectSomeValuesFrom( (OWLObjectPropertyExpression) prop, fac.getOWLThing());
					} else {
						some =fac.getOWLDataSomeValuesFrom( (OWLDataPropertyExpression) prop, fac.getTopDatatype());
						
					}
					DLAnnotatedPredicate newPred = new DLAnnotatedPredicate(some, false);
					newBody.add(new AtomicFormula(newPred, e1));
					
				} else if (uvars.contains(e1) && !uvars.contains(e2)) {
					//p_R(unbound, bound) --> p_some(inv(R))(bound)
					OWLPropertyExpression prop = p.getPropertyAnnotation();
					OWLClassExpression some ; 
					if (prop.isObjectPropertyExpression()) {
						some =fac.getOWLObjectSomeValuesFrom( ((OWLObjectPropertyExpression) prop).getInverseProperty().getSimplified(), fac.getOWLThing());
						DLAnnotatedPredicate newPred = new DLAnnotatedPredicate(some, false);
						newBody.add(new AtomicFormula(newPred, e2));
					} else {
						OWLDataPropertyExpression dprop = (OWLDataPropertyExpression) prop;
						ExistInverseDataPropertyAnnotatedPredicate newPred = new ExistInverseDataPropertyAnnotatedPredicate(dprop.asOWLDataProperty(), fac);
						newBody.add(new AtomicFormula(newPred, e2));
					}
				} else {
					// p_R(unbound, unbound) --> p_some(R)()
					OWLPropertyExpression prop = p.getPropertyAnnotation();
					DLAnnotatedPredicate newPred = new DLAnnotatedPredicate(prop, true);
					newBody.add(new AtomicFormula(newPred, new ArrayList<Expr>(0)));
				}
			} else {
				throw new RuntimeException("Maximum arity for DL predicates is 2: "+ af);
			}
		}
		return new Rule(newHead, newBody,r.getId());
	}
	
	public RuleSystem deleteUnboundVariables(RuleSystem rs) {
		List<Rule> newRules = new ArrayList<Rule>(rs.getRules().size());
		for (Rule r:rs.getRules()) {
			newRules.add(deleteUnboundVariables(r));
		}
		return new RuleSystem(newRules);
	}
}
