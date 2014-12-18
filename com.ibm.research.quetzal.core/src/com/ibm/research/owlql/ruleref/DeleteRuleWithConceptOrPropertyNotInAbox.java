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

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.research.owlql.NormalizedOWLQLTbox;
import com.ibm.research.owlql.Taxonomy;
import com.ibm.research.owlql.rule.AtomicFormula;
import com.ibm.research.owlql.rule.Predicate;
import com.ibm.research.owlql.rule.Rule;
import com.ibm.research.owlql.rule.RuleSystem;
import com.ibm.wala.util.collections.HashSetFactory;

/**
 * Delete rules with properties or concepts not mentioned in the ABox when the set of properties and concepts in the ABox is known.
 * @author fokoue
 *
 */
public class DeleteRuleWithConceptOrPropertyNotInAbox {
	private static final Logger logger = LoggerFactory.getLogger(DeleteRuleWithConceptOrPropertyNotInAbox.class);
	protected Taxonomy taxo;
	NormalizedOWLQLTbox tbox;
	protected Set<String> conceptsAbsentFromAbox;
	protected Set<String> propertiesAbsentFromAbox;
	
	
	public DeleteRuleWithConceptOrPropertyNotInAbox(Taxonomy taxo, NormalizedOWLQLTbox tbox,  Set<String> conceptURIsInAbox, Set<String> propertyURIsInAbox) {
		super();
		this.taxo = taxo;
		this.tbox = tbox;
		conceptsAbsentFromAbox = HashSetFactory.make();
		propertiesAbsentFromAbox = HashSetFactory.make();
		if (conceptURIsInAbox!=null) {
			for (OWLClass c: tbox.getNormalizedOntology().getClassesInSignature()) {
				if (!conceptURIsInAbox.contains(c.getIRI().toString())) {
					conceptsAbsentFromAbox.add(c.getIRI().toString());
				}
			}
		}
		
		if (propertyURIsInAbox!=null) {
			for (OWLObjectProperty prop : tbox.getNormalizedOntology().getObjectPropertiesInSignature()) {
				if (!propertyURIsInAbox.contains(prop.getIRI().toString())) {
					propertiesAbsentFromAbox.add(prop.getIRI().toString());
				}
			}
			for (OWLDataProperty prop : tbox.getNormalizedOntology().getDataPropertiesInSignature()) {
				if (!propertyURIsInAbox.contains(prop.getIRI().toString())) {
					propertiesAbsentFromAbox.add(prop.getIRI().toString());
				}
			}
		}
	}

	/**
	 * It returns <code>null</code> to indicate that the rule is unsatisfiable
	 * @param r
	 * @return
	 */
	public Rule deleteRuleWithConceptOrPropertyNotInAbox(Rule r) {
		return deleteRuleWithConceptOrPropertyNotInAbox(r,  Collections.EMPTY_SET);
	}

	/**
	 * It returns <code>null</code> to indicate that the rule is unsatisfiable
	 * @param r
	 * @return
	 */
	protected Rule deleteRuleWithConceptOrPropertyNotInAbox(Rule r, Set<Predicate> unsatisfiablePredicates) {
		List<AtomicFormula> newBody = new LinkedList<AtomicFormula>();
		
		for (AtomicFormula af : r.getBody()) {
			if (unsatisfiablePredicates.contains(af.getPredicate())) {
				return null;
			}
			if (!(af.getPredicate() instanceof ExistInverseDataPropertyAnnotatedPredicate) 
				&& !(af.getPredicate() instanceof DLAnnotatedPredicate)) {
				String name = af.getPredicate().getName();
				if (conceptsAbsentFromAbox.contains(name) || propertiesAbsentFromAbox.contains(name) ) {
					logger.debug("Predicate not present in the instance data: {}", af);
					return null;
				} else if (tbox.getNormalizer().isGeneratedRole(af.getPredicate().getName())) {
					return null;
				}

			} 
			newBody.add(af);
		}
		return new Rule(r.getHead(), newBody, r.getId());
	}
	
	public String getName(OWLPropertyExpression propexp) {
		String name;
		if (!propexp.isAnonymous()) {
			if (propexp.isDataPropertyExpression()) {
				name = ((OWLDataPropertyExpression) propexp).asOWLDataProperty().getIRI().toString();
			} else {
				name = ((OWLObjectPropertyExpression) propexp).asOWLObjectProperty().getIRI().toString();
			}
		} else {
			assert propexp.isObjectPropertyExpression();
			name = ((OWLObjectPropertyExpression) propexp).getNamedProperty().getIRI().toString();
		}
		return name;
	}
	
	public OWLProperty getNamedProperty(OWLPropertyExpression propexp) {
		OWLProperty ret;
		if (!propexp.isAnonymous()) {
			if (propexp.isDataPropertyExpression()) {
				ret = ((OWLDataPropertyExpression) propexp).asOWLDataProperty();;
			} else {
				ret = ((OWLObjectPropertyExpression) propexp).asOWLObjectProperty();
			}
		} else {
			assert propexp.isObjectPropertyExpression();
			ret = ((OWLObjectPropertyExpression) propexp).getNamedProperty();
		}
		return ret;
	}
	
	
	
	/**
	 * returns the rule system after removing rules with unsatisfiable atoms. It returns <code>null</code> to indicate that all rules were eliminated 
	 *
	 * @param rs
	 *  @return
	 */
	public RuleSystem deleteRuleWithConceptOrPropertyNotInAbox(RuleSystem rs) {
		return deleteRuleWithConceptOrPropertyNotInAbox(rs,  null);
	}
	/**
	 * returns the rule system after removing rules with concepts or properties not memtioned in the instance data. It returns <code>null</code> to indicate that all rules were eliminated 
	 * @param rs
	 * @param outputUnsatisfiableHeadPredicates where unsatisfiable head predicates will be stored
	 * @return
	 */
	public RuleSystem deleteRuleWithConceptOrPropertyNotInAbox(RuleSystem rs,Set<Predicate> outputUnsatisfiableHeadPredicates) {
		
		Set<Predicate> satisfiableZeroArityPredicates = new HashSet<Predicate>();
		Set<Predicate> unsatisfiablePredicates = new HashSet<Predicate>();
		List<Rule> rules = rs.getRules();
		do {
			List<Rule> newRules = new  LinkedList<Rule>();
			Set<Predicate> newHeads = new HashSet<Predicate>();
			Set<Predicate> removedHeadPred = new HashSet<Predicate>();
			Set<Predicate> newSatisfiableZeroArityPredicates = new HashSet<Predicate>();
			for (Rule r : rules) {
				Rule newR = deleteRuleWithConceptOrPropertyNotInAbox(r, unsatisfiablePredicates);
				if (newR == null) {
					logger.debug("Unsatisfiable rule: {}", r);
					removedHeadPred.add(r.getHead().getPredicate());
				} else {
					if (newR.getBody().isEmpty()) {
						// we assume that the original system was such that
						// each head variable appears at least once in the body
						assert newR.getHead().getPredicate().getArity() == 0 : r
								+ "\n" + newR;
						logger.debug("Satisfieable head: {}", newR.getHead());
						newSatisfiableZeroArityPredicates.add(newR.getHead()
								.getPredicate());
					} else {
						newRules.add(newR);
						newHeads.add(newR.getHead().getPredicate());
					}

				}
			}
			rules = newRules;
			//compute new unsatisfiable predicates:
			// head of removed rules that are no longer head of any new rules
			// 
			removedHeadPred.removeAll(newHeads);
			unsatisfiablePredicates = removedHeadPred;
			unsatisfiablePredicates.removeAll(newSatisfiableZeroArityPredicates);
			if (!unsatisfiablePredicates.isEmpty()) {
				logger.debug("Unsatisfiable predicates : {}", unsatisfiablePredicates);
			}
			if (!newSatisfiableZeroArityPredicates.isEmpty()) {
				logger.debug("Satisfiable predicates : {}", newSatisfiableZeroArityPredicates);
			}
			//
			satisfiableZeroArityPredicates = newSatisfiableZeroArityPredicates;
			if (outputUnsatisfiableHeadPredicates!=null) {
				outputUnsatisfiableHeadPredicates.addAll(unsatisfiablePredicates);
			}
		} while ((!satisfiableZeroArityPredicates.isEmpty() || !unsatisfiablePredicates.isEmpty())
				&& !rules.isEmpty());
		
		return rules.isEmpty()? null : new RuleSystem(rules);
	}
}
