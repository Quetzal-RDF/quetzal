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
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.vocabulary.RDF;
import com.ibm.research.owlql.NormalizedOWLQLTbox;
import com.ibm.research.owlql.rule.AtomicFormula;
import com.ibm.research.owlql.rule.BoundVariablePredicate;
import com.ibm.research.owlql.rule.ConstantExpr;
import com.ibm.research.owlql.rule.Expr;
import com.ibm.research.owlql.rule.Predicate;
import com.ibm.research.owlql.rule.Rule;
import com.ibm.research.owlql.rule.RuleSystem;

/**
 * implement renaming to introduce DL annotated formulas
 * @author achille
 *
 */
public class RuleRenaming {
	
	private static final Logger logger = LoggerFactory.getLogger(RuleRenaming.class);
	protected OWLDataFactory fac;
	protected NormalizedOWLQLTbox tbox;
	protected Set<OWLClass> classes;
	protected Set<OWLObjectProperty> oprops;
	protected Set<OWLDataProperty> dprops;
	
	public RuleRenaming(NormalizedOWLQLTbox tbox) {
		super();
		this.tbox = tbox;
		this.fac = tbox.getFactory();
		oprops = tbox.getNormalizedOntology().getObjectPropertiesInSignature();
		dprops = tbox.getNormalizedOntology().getDataPropertiesInSignature();
		classes =  tbox.getNormalizedOntology().getClassesInSignature();
	}

	public Rule rename(Rule r) {
		AtomicFormula newHead = r.getHead();//.clone();
		List<AtomicFormula> newBody = new ArrayList<AtomicFormula>(r.getBody().size()); 
		for (AtomicFormula af: r.getBody()) {
			Predicate newPred ;
			
			
			if (af.getPredicate().getArity()==1) {
				IRI predName = IRI.create(af.getPredicate().getName());
				// arity 1 ==> it must be a class
				OWLClass owlclass= fac.getOWLClass(predName);
				if (classes.contains(owlclass)) {
					// the class must be in the tbox
					newPred = new DLAnnotatedPredicate(owlclass);
				} else {
					logger.warn("Unknown class in query: {}", owlclass);
					newPred = new Predicate(predName.toString(), 1);
				}
				
			} else if (af.getPredicate().getArity() ==2) {	
				IRI predName = IRI.create(af.getPredicate().getName());
				//assert af.getPredicate().getArity() ==2 : af.getPredicate().getArity();
				// data or object property
				OWLObjectProperty oprop = fac.getOWLObjectProperty(predName);
				if (af.getPredicate().getName().equals("http://www.w3.org/2002/07/owl#differentFrom")) {
					newPred = new DLAnnotatedPredicate(oprop);
				} else if (oprops.contains(oprop)) {
					newPred = new DLAnnotatedPredicate(oprop);
				} else  {
					OWLDataProperty dprop = fac.getOWLDataProperty(predName);
					if (dprops.contains(dprop)) {
						newPred = new DLAnnotatedPredicate(dprop);//OWLObjectProperty(predName));
					} else {
						logger.warn("Unnknown property in query: {}", dprop);
						newPred = new Predicate(predName.toString(), 2);
					}
				}
			} else if (af.getPredicate() instanceof TriplePredicate) {
				Expr e;
				OWLClass c ; 
				// triple( *, ?X, conceptURI)  ---> conceptURI(*) && ?X = rdf:type
				if( af.getArguments().get(1).isVariable()
				&& (e=af.getArguments().get(2)).isConstant()){
					ConstantExpr ce =(ConstantExpr) e;
					if ((ce.getValue() instanceof URI)
					&& tbox.getNormalizedOntology().getClassesInSignature().contains(c =tbox.getFactory().getOWLClass(IRI.create(ce.getValue().toString())))) {
						// af to C(x) 
						try {
							newPred = new DLAnnotatedPredicate(c);
							newBody.add(new AtomicFormula(newPred,af.getArguments().get(0)));
							ConstantExpr rdftype = new ConstantExpr(new URI(RDF.type.getURI()));
							newBody.add(new AtomicFormula(new BoundVariablePredicate(2), af.getArguments().get(1), rdftype));
							continue;
						} catch (URISyntaxException e1) {
							throw new RuntimeException(e1);
						}
					}
				}
				newPred = af.getPredicate();
				assert af.getArguments().get(1).isVariable() 
				|| (((ConstantExpr) af.getArguments().get(1)).getValue().equals(getRDFType()) && af.getArguments().get(2).isVariable()) : "Wildcard must either have a variable in predicate position or have rdf:type in predicate position and a variable as object\n"+af;
				
				/*if (af.getArguments().get(1).isVariable()) {
					newPred = new TriplePredicate(af.getPredicate().getName());
				} else if (((ConstantExpr) af.getArguments().get(1)).getValue().equals(getRDFType()) && af.getArguments().get(2).isVariable()) {
					newPred = new TriplePredicate(af.getPredicate().getName());
				} else 	{
					newPred = new Predicate(af.getPredicate().getName(), af.getPredicate().getArity());
					logger.warn("Unknown predicate: {}", af);
				}*/
			} else {
				newPred = new Predicate(af.getPredicate().getName(), af.getPredicate().getArity());
				logger.warn("Unknown predicate: {}", af);
				
			}
			//assert newPred!=null : af.getPredicate().getName() +" unknown DL atom";
			newBody.add(new AtomicFormula(newPred, af.getArguments()));
		}
		return new Rule(newHead, newBody,r.getId());
	
	}
	
	public RuleSystem rename(RuleSystem rs) {
		List<Rule> newRules = new ArrayList<Rule>(rs.getRules().size());
		for (Rule r:rs.getRules()) {
			newRules.add(rename(r));
		}
		return new RuleSystem(newRules);
	}
	protected static URI getRDFType() {
		try {
			return new URI(RDF.type.getURI());
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
}
