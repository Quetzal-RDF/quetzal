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
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLPropertyExpression;

import com.hp.hpl.jena.vocabulary.RDF;
import com.ibm.research.owlql.Taxonomy;
import com.ibm.research.owlql.rule.AtomicFormula;
import com.ibm.research.owlql.rule.BoundVariablePredicate;
import com.ibm.research.owlql.rule.ConstantExpr;
import com.ibm.research.owlql.rule.Expr;
import com.ibm.research.owlql.rule.Predicate;
import com.ibm.research.owlql.rule.Rule;
import com.ibm.research.owlql.rule.RuleSystem;
import com.ibm.research.owlql.rule.VariableExpr;

public class DefineAtomView {

	public static final String OWL_DIFFERENTFROM_URI= "http://www.w3.org/2002/07/owl#differentFrom";
	protected Taxonomy taxo;
	protected OWLDataFactory fac;
	

	public DefineAtomView(Taxonomy taxo, OWLDataFactory fac) {
		super();
		this.taxo = taxo;
		this.fac = fac;
	}
		
	protected List<Rule> defineAtomView(DLAnnotatedPredicate ap, int[] nextAvailableRuleId) {
		
		List<Rule> ret = new LinkedList<Rule>();
		OWLClassExpression classexp = ap.getClassAnnotation();
		OWLPropertyExpression propexp = ap.getPropertyAnnotation();
		
		//special case of exist(inv(R)) where R is a data property
		if (ap instanceof ExistInverseDataPropertyAnnotatedPredicate) {
			ExistInverseDataPropertyAnnotatedPredicate eap = (ExistInverseDataPropertyAnnotatedPredicate) ap;
			OWLDataProperty prop = eap.getProperty();
			Set<OWLPropertyExpression> subs = new HashSet<OWLPropertyExpression>(taxo.getAllSubproperties(prop));
			subs.add(prop);
			for (OWLPropertyExpression sub: subs) {
				VariableExpr x = new VariableExpr("x");
				VariableExpr unbound = new VariableExpr("unbound");
				VariableExpr unbound2 = new VariableExpr("unbound2");
				AtomicFormula head = ap.getArity() == 1? new AtomicFormula(ap, x): new AtomicFormula(ap, Collections.EMPTY_LIST);
				Predicate bodyPred = new Predicate(prop.getIRI().toString(), 2);
				AtomicFormula body = ap.getArity() == 1? new AtomicFormula(bodyPred, unbound, x)
								: new AtomicFormula(bodyPred, unbound, unbound2) ;
				ret.add(new Rule(head, Collections.singletonList(body), nextAvailableRuleId[0]++));
			}
			return ret;
		}
		//
		
		//
		// need to avoid duplicate rule of the form q:- R(x, y) or q:- A(x)
		Set<AtomicFormula> formulasInAllBodies = new HashSet<AtomicFormula>();
		//
		
		if (classexp!=null) {
			Set<OWLClassExpression> subs = new HashSet<OWLClassExpression>(taxo.getAllSubsumees(classexp));
			subs.add(classexp);
			for (OWLClassExpression sub: subs) {
				VariableExpr x = new VariableExpr("x");
				AtomicFormula head = ap.getArity()==1? new AtomicFormula(ap,  x): new AtomicFormula(ap);
				VariableExpr unbound = new VariableExpr("unbound");
				AtomicFormula body;
				Expr bodyVar1;
				Expr bodyVar2;
				if (!sub.isAnonymous()) {
					bodyVar1 = ap.getArity() == 1 ? x : unbound;
					body = new AtomicFormula(new Predicate(sub.asOWLClass().getIRI().toString(), 1),bodyVar1);
				} else if ( (sub instanceof OWLObjectSomeValuesFrom)
					|| (sub instanceof OWLDataSomeValuesFrom)) {
					VariableExpr unbound2 = new VariableExpr("unbound2");
					String pname;
					OWLPropertyExpression prop;
					if (sub instanceof OWLObjectSomeValuesFrom) {
						OWLObjectSomeValuesFrom some = (OWLObjectSomeValuesFrom) sub;
						OWLObjectPropertyExpression pexp = some.getProperty().getSimplified();
						prop = pexp;
						pname = pexp.getNamedProperty().getIRI().toString();
					} else {
						OWLDataSomeValuesFrom some = (OWLDataSomeValuesFrom) sub;
						OWLDataPropertyExpression pexp = some.getProperty();
						prop = pexp;
						pname = pexp.asOWLDataProperty().getIRI().toString();
					}
					if (!prop.isAnonymous()) {
						// simple prop
						bodyVar1 = ap.getArity() == 1? x : unbound; 
						bodyVar2 = ap.getArity() == 1? unbound: unbound2;
					} else {
						// inverse prop
						bodyVar1 = unbound; 
						bodyVar2 = ap.getArity() == 1? x : unbound2;
					}
					body = new AtomicFormula(new Predicate(pname, 2), bodyVar1, bodyVar2);
				} else {
					throw new RuntimeException("Unknown subclass type: "+sub);
				}
				if (ap.getArity() == 1 || formulasInAllBodies.add(body)) {
					ret.add(new Rule(head, Collections.singletonList(body), nextAvailableRuleId[0]++));
				}
			}
			
			
		}  else  {
			assert propexp!=null : ap;
			assert ap.getArity() == 2 : ap;
			String pname = getName(propexp);
			Set<OWLPropertyExpression> subs ;
			
			if (pname.equals(OWL_DIFFERENTFROM_URI)) {
				subs = new HashSet<OWLPropertyExpression>();
				subs.add(propexp);
				subs.add(fac.getOWLObjectInverseOf((OWLObjectPropertyExpression)propexp).getSimplified());
			} else {
				subs = new HashSet<OWLPropertyExpression>(taxo.getAllSubproperties(propexp));
				subs.add(propexp);
			}
			for (OWLPropertyExpression sub: subs) {
				VariableExpr x = new VariableExpr(ap.getArity() == 2? "x":"unbound");
				VariableExpr y = new VariableExpr(ap.getArity() == 2? "y":"unbound2");
				AtomicFormula head = ap.getArity()==2? new AtomicFormula(ap, x, y): new AtomicFormula(ap);
				AtomicFormula body;
				String subname = getName(sub);
				if (sub instanceof OWLObjectPropertyExpression) {
					sub = ((OWLObjectPropertyExpression) sub).getSimplified();
				}
				Predicate newPred = new Predicate(subname, 2);
				if (ap.getArity() == 2) {
					if (!sub.isAnonymous()) {
						// simple property
						body = new AtomicFormula(newPred, x, y);
					} else {
						// inverse property
						body = new AtomicFormula(newPred, y, x);
					}
				} else {
					body =  new AtomicFormula(newPred, x, y);
				}
				if (ap.getArity() == 2 || formulasInAllBodies.add(body)) {
					ret.add(new Rule(head, Collections.singletonList(body), nextAvailableRuleId[0]++));
				}
			}
			if (propexp.isObjectPropertyExpression()) {
				OWLObjectPropertyExpression op = (OWLObjectPropertyExpression) propexp;
				op = op.getSimplified();
				if (taxo.getTbox().getReflexiveProperties().contains(op.getNamedProperty())) {
					// produce the unsafe rule
					// p(x,y) :- x = y;
					/*
					VariableExpr x = new VariableExpr("x");
					VariableExpr y = new VariableExpr("y");
					AtomicFormula head =  new AtomicFormula(ap, x, y);
					AtomicFormula body = new AtomicFormula(new Predicate(Rule.BUILT_IN_EQUAL,2), x,y);
					ret.add(new Rule(head, Collections.singletonList(body), nextAvailableRuleId[0]++));
					 */
					
					//TODO: consider using the folloiwing safe rules
					// p(x,y) :- triple(x, u, v) ^ x = y
					// p(x,y) :- triple(u, v, x) ^ x = y
					//
					VariableExpr x = new VariableExpr("x");
					VariableExpr y = new VariableExpr("y");
					VariableExpr u = new VariableExpr("u");
					VariableExpr v = new VariableExpr("v");
					try {
						// add p(x, y) :- triple(x, u, v) ^ y = x
						AtomicFormula head =  new AtomicFormula(ap, x, y);
						List<AtomicFormula> body = new ArrayList<AtomicFormula>();
						body.add(new AtomicFormula(new TriplePredicate(), x, u, v));
						body.add(new AtomicFormula(new BoundVariablePredicate(2), y, x));
						ret.add(new Rule(head, body, nextAvailableRuleId[0]++));
						//
						//add p(x, y) :-triple(v, u, x) ^ u!=rdf:type ^ y = x
						head =  new AtomicFormula(ap, x, y);
						body = new ArrayList<AtomicFormula>();
						body.add(new AtomicFormula(new TriplePredicate(), v, u, x));
						body.add(new AtomicFormula(new Predicate(Rule.BUILT_IN_IRI_DIFF, 2), u, new ConstantExpr(new URI(RDF.type.getURI()))));
						body.add(new AtomicFormula(new BoundVariablePredicate(2), y, x));
						ret.add(new Rule(head, body, nextAvailableRuleId[0]++));
						//
					} catch (URISyntaxException e) {
						throw new RuntimeException(e);
					}
					
				}
				
			}
		}
		return ret;
	}
	
	protected List<Rule> defineAtomViews(RuleSystem rs) {
		List<Rule> ret = new LinkedList<Rule>();
		Set<DLAnnotatedPredicate> dlps = new HashSet<DLAnnotatedPredicate>();
		Set<Predicate> preds = new HashSet<Predicate>(rs.getHeadPredicates());
		preds.addAll(rs.getNonHeadPredicates());
		
		DLAnnotatedPredicate diffPred = new DLAnnotatedPredicate(fac.getOWLObjectProperty(IRI.create(OWL_DIFFERENTFROM_URI)));
		int[] nextAvailableRuleId = new int[]{RuleSystem.nextAvailableRuleId(rs)};
		if (preds.contains(diffPred)) {
			ret.addAll(defineAtomViewForDifferentFrom(nextAvailableRuleId[0]));
			for (Rule r: ret) {
				preds.addAll(r.getAllPredicates());
			}
		}
		for (Predicate p : preds) {
			if (p instanceof DLAnnotatedPredicate) {
				dlps.add((DLAnnotatedPredicate) p);
			}
		}
		for (DLAnnotatedPredicate dlp : dlps) {
			ret.addAll(defineAtomView(dlp, nextAvailableRuleId));
		}
		return ret;
		
	}
	
	protected List<Rule> defineAtomViewForDifferentFrom(int ruleid) {
		List<Rule> ret = new LinkedList<Rule>();
		DLAnnotatedPredicate diffPred = new DLAnnotatedPredicate(fac.getOWLObjectProperty(IRI.create(OWL_DIFFERENTFROM_URI)));
		for (OWLObjectProperty p: taxo.getTbox().getIrreflexiveProperties()) {
			// add diff(x,y) :- p(x, y)
			DLAnnotatedPredicate bodyPred = new DLAnnotatedPredicate(p.getNamedProperty());
			VariableExpr x = new VariableExpr("x");
			VariableExpr y = new VariableExpr("y");
			AtomicFormula head = new AtomicFormula(diffPred, x, y );
			AtomicFormula body =  p.getSimplified().isAnonymous()? new AtomicFormula(bodyPred, y,x) : new AtomicFormula(bodyPred, x, y);
			ret.add(new Rule(head,  ruleid++, body));
			head = new AtomicFormula(diffPred, y, x );
			//add diff(y,x) :- p(x, y)
			ret.add(new Rule(head,  ruleid++, body.clone()));
		}
		
		for (OWLDisjointObjectPropertiesAxiom disjprops: taxo.getTbox().getNegativeObjectSubPropertyAxioms()) {
			// add diff(x, y) : p(z, x) & q(z, y)  (for q and p disjoint)
			// add diff(x, y) : p(x, z) & q(y, z) (for q and p disjoint)
			Set<OWLObjectPropertyExpression> props = disjprops.getProperties();
			assert props.size()>=1 : props;
			assert props.size()<=2 : props;
			if (props.size()==2) {
				Iterator<OWLObjectPropertyExpression> it = props.iterator();
				OWLObjectPropertyExpression p1 = it.next();
				OWLObjectPropertyExpression p2 = it.next();
				DLAnnotatedPredicate bodyPred1 = new DLAnnotatedPredicate(p1.getNamedProperty());
				DLAnnotatedPredicate bodyPred2 = new DLAnnotatedPredicate(p2.getNamedProperty());
				VariableExpr x = new VariableExpr("x");
				VariableExpr y = new VariableExpr("y");
				VariableExpr z = new VariableExpr("z");
				AtomicFormula head = new AtomicFormula(diffPred, x, y );
				List<AtomicFormula> body = new LinkedList<AtomicFormula>();
				if (p1.getSimplified().isAnonymous()) {
					body.add(new AtomicFormula(bodyPred1,x,z));
				} else {
					body.add(new AtomicFormula(bodyPred1,z,x));
				}
				if (p2.getSimplified().isAnonymous()) {
					body.add(new AtomicFormula(bodyPred2,y,z));
				} else {
					body.add(new AtomicFormula(bodyPred2,z,y));
				}
				ret.add(new Rule(head, body,ruleid++));
				// add diff(y,x) : p(z, x) & q(z, y)  (for q and p disjoint)
				ret.add(new Rule( new AtomicFormula(diffPred, y, x), body,ruleid++));
				
				head = new AtomicFormula(diffPred, x, y );
				body = new LinkedList<AtomicFormula>();
				if (p1.getSimplified().isAnonymous()) {
					body.add(new AtomicFormula(bodyPred1,z,x));
				} else {
					body.add(new AtomicFormula(bodyPred1,x,z));
				}
				if (p2.getSimplified().isAnonymous()) {
					body.add(new AtomicFormula(bodyPred2,z,y));
				} else {
					body.add(new AtomicFormula(bodyPred2,y,z));
				}
				ret.add(new Rule(head, body,ruleid++));
				// add diff(y, x) : p(x, z) & q(y, z) (for q and p disjoint)
				ret.add(new Rule( new AtomicFormula(diffPred, y, x), body,ruleid++));
				
			}	
		}
		
		for (OWLDisjointDataPropertiesAxiom disjprops: taxo.getTbox().getNegativeDataSubPropertyAxioms()) {
			// add diff(x, y) : p(x, z) && q(y, z) (for q and p disjoint)
			Set<OWLDataPropertyExpression> props = disjprops.getProperties();
			assert props.size()>=1 : props;
			assert props.size()<=2 : props;
			if (props.size()==2) {
				Iterator<OWLDataPropertyExpression> it = props.iterator();
				DLAnnotatedPredicate bodyPred1 = new DLAnnotatedPredicate(it.next().asOWLDataProperty());
				DLAnnotatedPredicate bodyPred2 = new DLAnnotatedPredicate(it.next().asOWLDataProperty());
				VariableExpr x = new VariableExpr("x");
				VariableExpr y = new VariableExpr("y");
				VariableExpr z = new VariableExpr("z");
				
				AtomicFormula head = new AtomicFormula(diffPred, x, y );
				List<AtomicFormula> body = new LinkedList<AtomicFormula>();
				body.add(new AtomicFormula(bodyPred1,x,z));
				body.add(new AtomicFormula(bodyPred2,y,z));
				ret.add(new Rule(head, body,ruleid++));
				// add diff(y, x) : p(x, z) && q(y, z) (for q and p disjoint)
				ret.add(new Rule( new AtomicFormula(diffPred, y, x), body,ruleid++));
				
			}	
		}
		
		
		return ret;
	}
	
	public RuleSystem addAtomViews(RuleSystem rs) {
		List<Rule> rules = new LinkedList<Rule>(rs.getRules());
		rules.addAll(defineAtomViews(rs));
		return new RuleSystem(rules);
	}

	protected String getName(OWLPropertyExpression  propexp) {
		String pname;
		if (propexp instanceof OWLObjectPropertyExpression) {
			OWLObjectPropertyExpression pexp = (OWLObjectPropertyExpression) propexp;
			pname = pexp.getNamedProperty().getIRI().toString();
		} else {				
			OWLDataPropertyExpression pexp = (OWLDataPropertyExpression) propexp;
			pname = pexp.asOWLDataProperty().getIRI().toString();
		}
		return pname;
	}
}
