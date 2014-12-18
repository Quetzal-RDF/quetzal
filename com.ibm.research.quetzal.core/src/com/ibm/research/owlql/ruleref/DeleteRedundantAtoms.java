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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.vocabulary.RDF;
import com.ibm.research.owlql.Taxonomy;
import com.ibm.research.owlql.rule.AtomicFormula;
import com.ibm.research.owlql.rule.ConstantExpr;
import com.ibm.research.owlql.rule.Expr;
import com.ibm.research.owlql.rule.Predicate;
import com.ibm.research.owlql.rule.Rule;
import com.ibm.research.owlql.rule.RuleSystem;

public class DeleteRedundantAtoms {

	private static final Logger logger = LoggerFactory.getLogger(DeleteRedundantAtoms.class);
	
	protected Taxonomy taxo;
	protected OWLDataFactory fac;
	protected DeleteUnboundVariables delUnboundVars;
	public DeleteRedundantAtoms(Taxonomy taxo, OWLDataFactory fac) {
		super();
		this.taxo = taxo;
		this.fac = fac;
		this.delUnboundVars = new DeleteUnboundVariables(fac);
	}

	public RuleSystem deleteRedundantAtoms(RuleSystem rs) {
		List<Rule> newRules = new ArrayList<Rule>(rs.getRules().size());
		for (Rule r:rs.getRules()) {
			newRules.add(deleteRedundantAtoms(r));
		}
		return new RuleSystem(newRules);
	}
	public Rule deleteRedundantAtoms(Rule r) {
		/*if (true) {
			return r;
		}*/
		//TODO: 
		// REVISIT: implement the simplification rules involving zero arity atoms
		//
		Set<AtomicFormula> newBody = new HashSet<AtomicFormula>(r.getBody());
		for (int i=0;i<r.getBody().size();i++) {
			AtomicFormula af1 = r.getBody().get(i);
			Predicate p1 = af1.getPredicate();
			if (!newBody.contains(af1)) {
				continue;
			}
			for (int j=0;j<r.getBody().size();j++) {
				
				AtomicFormula af2 = r.getBody().get(j);
				Predicate p2 = af2.getPredicate();
				if (af1 == af2
				|| !newBody.contains(af2)) {
					continue;
				}
				
				if (af1.equals(af2)) {
					newBody.remove(af2);
					
				} else if ( (p1 instanceof TriplePredicate)
				&& (p2 instanceof TriplePredicate) ) {
					Expr predVar1 = af1.getArguments().get(1);
					Expr predVar2 =  af2.getArguments().get(1);
					Expr obj2 = af2.getArguments().get(2);
					Expr obj1 = af1.getArguments().get(2);
					Expr subj2 = af2.getArguments().get(0);
					Expr subj1 = af1.getArguments().get(0);
					if ( (subj1.equals(subj2) || r.getUnboundVariables().contains(subj2))  
					&& (predVar1.equals(predVar2)  || r.getUnboundVariables().contains(predVar2)) 
					&& (obj1.equals(obj2) || r.getUnboundVariables().contains(obj2)) ) {
						// (u1, v1, w1) and (u2, v2, w2) and (u1 = u2 or u2 is an unbound variable) and 
						//(v1 = v2 or v2 is an unbound variable) and (w1=w2 or w2 is an unbound variable) ==> get rid of (u2, v2, w2)
						newBody.remove(af2);
						logger.debug("Redundant atom {} removed from rule : {}", af2, r);
					} 
				} else if ( (p1 instanceof DLAnnotatedPredicate)
				&& p1.getArity()>0
				&& (p2 instanceof TriplePredicate) ) {
					DLAnnotatedPredicate dlp1 = (DLAnnotatedPredicate) p1;
					OWLPropertyExpression prop1 = dlp1.getPropertyAnnotation();
					Expr predVar2 =  af2.getArguments().get(1);
					Expr obj2 = af2.getArguments().get(2);
					Expr subj2 = af2.getArguments().get(0);
					Expr subj1;
					Expr predVar1;
					Expr obj1;
					// compute subj1, prevVar1, obj1
					if (p1.getArity()==1) {
						subj1 = af1.getArguments().get(0);
						obj1 = dlp1.getClassAnnotation().isAnonymous()? null : new ConstantExpr(dlp1.getClassAnnotation().asOWLClass().getIRI().toURI());
						predVar1 = new ConstantExpr(getRDFType());
					} else {
						assert p1.getArity() == 2 : p1+"\n"+af1;
						if (prop1.isDataPropertyExpression()
						|| !((OWLObjectPropertyExpression)prop1).getSimplified().isAnonymous()) {
							subj1 = af1.getArguments().get(0);
							obj1 = af1.getArguments().get(1);
							predVar1 = new ConstantExpr( prop1.isDataPropertyExpression()? 
									((OWLProperty) prop1).getIRI().toURI(): ((OWLObjectPropertyExpression)prop1).getSimplified().getNamedProperty().getIRI().toURI());
						} else {
							// inverse
							subj1 = af1.getArguments().get(1);
							obj1 = af1.getArguments().get(0);
							predVar1 = new ConstantExpr(((OWLObjectPropertyExpression)prop1).getSimplified().getNamedProperty().getIRI().toURI());
						}
					}
					//
					
					if ( (subj1.equals(subj2) || r.getUnboundVariables().contains(subj2)) 					
					&& (predVar1.equals(predVar2)  || r.getUnboundVariables().contains(predVar2)) 
					&& obj1!=null && (obj1.equals(obj2) || r.getUnboundVariables().contains(obj2)) ) { // obj1 ==null for C(u) with C a complex class
						// (u1, v1, w1) and (u2, v2, w2) and (u1=u2 or u2 is an unbound variable)
						// and (v1 = v2 or v2 is an unbound variable) and (w1=w2 or w2 is an unbound variable) ==> get rid of (u2, v2, w2)
						newBody.remove(af2);
						logger.debug("Redundant atom {} removed from rule : {}", af2, r);
						
					}
					
				}
				
				if (!(p1 instanceof DLAnnotatedPredicate)
				|| !(p2 instanceof DLAnnotatedPredicate)) {
					continue;
				}
				DLAnnotatedPredicate dlp1 = (DLAnnotatedPredicate) p1;
				DLAnnotatedPredicate dlp2 = (DLAnnotatedPredicate) p2;
				OWLPropertyExpression prop1 = dlp1.getPropertyAnnotation();
				OWLPropertyExpression prop2 = dlp2.getPropertyAnnotation();
				
				if (af1.getArguments().equals(af2.getArguments())) {
					if (af1.getPredicate().getArity() == 2) {
						// p1_R(a, b) and p2_S(a, b) and R <= S  ==> get rid of p2_S(a,b)
						
						if (taxo.isSubProperty(prop1, prop2)) {
							newBody.remove(af2);
							logger.debug("Redundant atom {} removed from rule : {}", af2, r);
						}
						//
					} else if (af1.getPredicate().getArity() == 1) {
						// p1_C(a) and p2_D(a) and C <= D  ==> get rid of p2_D(a)
						if (allInstancesOfExistInvDataPropAnnotedPredicate(dlp1, dlp2)) {
							// C = some(inv(R), top)  and  D = some(inv(S), top) where R and S are data properties
							ExistInverseDataPropertyAnnotatedPredicate e1 = (ExistInverseDataPropertyAnnotatedPredicate) dlp1;
							ExistInverseDataPropertyAnnotatedPredicate e2 = (ExistInverseDataPropertyAnnotatedPredicate) dlp2;
							if (taxo.isSubProperty(e1.getProperty(), e2.getProperty())) {
								newBody.remove(af2);
								logger.debug("Redundant atom {} removed from rule : {}", af2, r);
							}
							
						} else if (!onlyOneInstanceOfExistInvDataPropAnnotedPredicate(dlp1, dlp2)) {
							// normal case
							if (taxo.isSubClass(dlp1.getClassAnnotation(),dlp2.getClassAnnotation())) {
								newBody.remove(af2);
								logger.debug("Redundant atom {} removed from rule : {}", af2, r);
							}
							//
						}
						//
					}
					
				} else {
					if (af1.getArity() == 2 && af2.getArity() ==2 ) {
						if (prop1.isObjectPropertyExpression() && prop2.isObjectPropertyExpression()
						&& af1.getArguments().get(0).equals(af2.getArguments().get(1))
						&& af1.getArguments().get(1).equals(af2.getArguments().get(0))) {
							// p1_R(a, b) and p2_S(b, a) and R <= S  ==> get rid of p2_S(a,b)
							if (taxo.isSubProperty(prop1,
							((OWLObjectPropertyExpression) prop2).getInverseProperty().getSimplified())) {
								newBody.remove(af2);
								logger.debug("Redundant atom {} removed from rule : {}", af2, r);
							}
							//
						}
					} else if (af1.getArity() == 2 && af2.getArity() ==1) {
						if (af1.getArguments().get(0).equals(af2.getArguments().get(0)) ){
							// p1_R(a, b) and p2_C(a) and some(R, top) <= C  ==> get rid of p2_C(a)
							if (!(dlp2 instanceof ExistInverseDataPropertyAnnotatedPredicate)) {
								OWLClassExpression some;
								if (prop1.isObjectPropertyExpression()) {
									some = fac.getOWLObjectSomeValuesFrom((OWLObjectPropertyExpression) prop1,
											fac.getOWLThing());
								} else {
									some = fac.getOWLDataSomeValuesFrom((OWLDataPropertyExpression) prop1,
											fac.getTopDatatype());
								}
								if (taxo.isSubClass(some, dlp2.getClassAnnotation())) {
									newBody.remove(af2);
									logger.debug("Redundant atom {} removed from rule : {}", af2, r);
								}
							}
							//
						} else if (af1.getArguments().get(1).equals(af2.getArguments().get(0))) {
							// p1_R(a, b) and p2_C(b) and some(inv(R), top) <= C  ==> get rid of p2_C(b)
							if (dlp2 instanceof ExistInverseDataPropertyAnnotatedPredicate){
								//  C = some(inv(S), top) where S is a data property. Test whether R <= S
								if (taxo.isSubProperty(prop1, ((ExistInverseDataPropertyAnnotatedPredicate) dlp2).getProperty())) {
									newBody.remove(af2);
									logger.debug("Redundant atom {} removed from rule : {}", af2, r);
								}
								//
							} else {
								// general case 
								if (prop1.isObjectPropertyExpression()) {
									OWLObjectSomeValuesFrom some = fac.getOWLObjectSomeValuesFrom(
											((OWLObjectPropertyExpression) prop1).getInverseProperty().getSimplified(),
												fac.getOWLThing());
									if (taxo.isSubClass(some, dlp2.getClassAnnotation())) {
										newBody.remove(af2);
										logger.debug("Redundant atom {} removed from rule : {}", af2, r);
									}
								}
							}
						}
					}
				}
				
			}
		}
		Rule newRule = new Rule(r.getHead(), new LinkedList<AtomicFormula>(newBody), r.getId());
		if (newBody.size()!=r.getBody().size()) {
			//potential for new unbound variables
			newRule = delUnboundVars.deleteUnboundVariables(newRule);
		}
		return newRule;
	}
	protected boolean allInstancesOfExistInvDataPropAnnotedPredicate(
			DLAnnotatedPredicate dlp1, DLAnnotatedPredicate dlp2) {
			return (dlp1 instanceof ExistInverseDataPropertyAnnotatedPredicate) &&
			(dlp2 instanceof ExistInverseDataPropertyAnnotatedPredicate);
	}
	protected boolean onlyOneInstanceOfExistInvDataPropAnnotedPredicate(
		DLAnnotatedPredicate dlp1, DLAnnotatedPredicate dlp2) {
		if (!(dlp1 instanceof ExistInverseDataPropertyAnnotatedPredicate) &&
		(dlp2 instanceof ExistInverseDataPropertyAnnotatedPredicate)) {
			return true;
		}
		if ((dlp1 instanceof ExistInverseDataPropertyAnnotatedPredicate) &&
			!(dlp2 instanceof ExistInverseDataPropertyAnnotatedPredicate)) {
			return true;
		}
		return  false;
	}
	protected static URI getRDFType() {
		try {
			return new URI(RDF.type.getURI());
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
}
