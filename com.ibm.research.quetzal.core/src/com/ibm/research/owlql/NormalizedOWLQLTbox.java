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
 package com.ibm.research.owlql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLNamedObject;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLPropertyRange;
import org.semanticweb.owlapi.model.OWLQuantifiedRestriction;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyAxiom;
import org.semanticweb.owlapi.rdf.util.RDFConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;
import com.hp.hpl.jena.sparql.syntax.ElementUnion;

public class NormalizedOWLQLTbox {

	private static final Logger logger = LoggerFactory.getLogger(NormalizedOWLQLTbox.class);
	/**
	 * the original ontology
	 */
	//protected OWLOntology originalOnt;
	/**
	 * OWL factory
	 */
	protected OWLDataFactory fac;
	/**
	 * normalizer
	 */
	protected OWLQLNormalizer normalizer;
	/**
	 * normalized OWLQL axioms
	 */
	protected OWLOntology normalizedOntology;
	/**
	 * non OWL QL axioms
	 */
	protected Set<OWLAxiom> nonOWLQLAxioms;
	/**
	 * positive concept inclusion axioms in the tbox
	 */
	protected Set<OWLSubClassOfAxiom> posIncAxInTbox;
	/**
	 * positve role inclusion axioms in the tbox
	 */
	protected Set<OWLSubPropertyAxiom> posSubPropAxInTbox;
	/**
	 * negative concept inclusion in the tbox
	 */
	protected Set<OWLSubClassOfAxiom> negIncAxInTbox;
	
	protected Set<OWLObjectProperty> reflexiveProps;
	
	protected Set<OWLObjectProperty> irreflexiveProps;
	
	
	/**
	 * negative object property inclusion axioms in the tbox
	 */
	protected Set<OWLDisjointObjectPropertiesAxiom> negObjectSubPropAxInTbox;
	/**
	 * negative data property inclusion axioms in the tbox
	 */
	protected Set<OWLDisjointDataPropertiesAxiom> negDataSubPropAxInTbox;
	
	/**
	 * negative concept inclusion in the the negative closure
	 */
	protected Set<OWLSubClassOfAxiom> negIncAxInNegClos;
	/**
	 * negative object property inclusion axioms in the negative closure
	 */
	protected Set<OWLDisjointObjectPropertiesAxiom> negObjectSubPropAxInNegClos;
	/**
	 * negative data property inclusion axioms in the negative closure
	 */
	protected Set<OWLDisjointDataPropertiesAxiom> negDataSubPropAxInNegClos;
	/**
	 *  negative inclusion axiom closure
	 */
	protected Set<OWLAxiom> negativeInclClosure;
	/**mapping from key (ie, named property or concept) positive concept inclusion axioms.
	* used as an index to speed up the search for axioms to consider during rewrite.
	*/
	protected Map<String, Set<OWLSubClassOfAxiom>> iri2PosIncAxInTbox;
	/**mapping from key (ie, named property or concept) positive role inclusion axioms.
	* used as an index to speed up the search for axioms to consider during rewrite.
	*/
	protected Map<String, Set<OWLSubPropertyAxiom>> iri2PosSubPropAxInTbox;

	protected Set<OWLClass> unsatisfiableClasses;
	protected Set<OWLProperty> unsatisfiableProperties;
	
	
	public NormalizedOWLQLTbox(OWLOntology originalOntTbox) {
		super();
		fac = originalOntTbox.getOWLOntologyManager().getOWLDataFactory();
		init(originalOntTbox.getAxioms());
	}
	
	public NormalizedOWLQLTbox(Set<OWLAxiom> axioms) {
		super();
		fac = OWLDataFactoryImpl.getInstance();
		init(axioms);
	}
	
	
	
	public NormalizedOWLQLTbox(NormalizedOWLQLTbox tbox) {
		super();
		try {
			this.fac = tbox.fac;
			this.normalizer = tbox.normalizer.copy();
			this.normalizedOntology = OWLManager.createOWLOntologyManager().createOntology();
			this.normalizedOntology.getOWLOntologyManager().addAxioms(this.normalizedOntology, tbox.normalizedOntology.getAxioms());
			this.nonOWLQLAxioms = new HashSet<OWLAxiom>(tbox.nonOWLQLAxioms);
			this.posIncAxInTbox = new HashSet<OWLSubClassOfAxiom>(tbox.posIncAxInTbox);
			this.posSubPropAxInTbox = new HashSet<OWLSubPropertyAxiom>(tbox.posSubPropAxInTbox);
			this.negIncAxInTbox =  new HashSet<OWLSubClassOfAxiom>(tbox.negIncAxInTbox);
			this.negObjectSubPropAxInTbox =  new HashSet<OWLDisjointObjectPropertiesAxiom>(tbox.negObjectSubPropAxInTbox);
			this.negDataSubPropAxInTbox = new HashSet<OWLDisjointDataPropertiesAxiom>(tbox.negDataSubPropAxInTbox);
			this.negativeInclClosure = new HashSet<OWLAxiom>(tbox.negativeInclClosure);
			this.iri2PosIncAxInTbox = new HashMap<String, Set<OWLSubClassOfAxiom>>();
			this.negIncAxInNegClos = tbox.negIncAxInNegClos!=null?
						new HashSet<OWLSubClassOfAxiom>(tbox.negIncAxInNegClos) : null;
			this.negObjectSubPropAxInNegClos = tbox.negObjectSubPropAxInNegClos!=null?
						new HashSet<OWLDisjointObjectPropertiesAxiom>(tbox.negObjectSubPropAxInNegClos) : null;
			this.negDataSubPropAxInNegClos = tbox.negDataSubPropAxInNegClos!=null ? 
						new HashSet<OWLDisjointDataPropertiesAxiom>(tbox.negDataSubPropAxInNegClos) : null;
			this.unsatisfiableClasses = null;
			this.unsatisfiableProperties = null;
			for (Map.Entry<String, Set<OWLSubClassOfAxiom>> e: tbox.iri2PosIncAxInTbox.entrySet() ) {
				this.iri2PosIncAxInTbox.put(e.getKey(), new HashSet<OWLSubClassOfAxiom>(e.getValue()));
			}
			this.iri2PosSubPropAxInTbox = new HashMap<String, Set<OWLSubPropertyAxiom>>();
			for (Map.Entry<String, Set<OWLSubPropertyAxiom>> e:tbox.iri2PosSubPropAxInTbox.entrySet()) {
				this.iri2PosSubPropAxInTbox.put(e.getKey(), new HashSet<OWLSubPropertyAxiom>(e.getValue()));
			}
		} catch (OWLOntologyCreationException e) {
			throw new RuntimeException(e);
		}
	}

	public Set<OWLAxiom> getNegativeInclClosure() {
		return negativeInclClosure;
	}

	/**
	 * negative object property inclusion axioms in the tbox
	 */
	public Set<OWLDisjointObjectPropertiesAxiom> getNegativeObjectSubPropertyAxioms() {
		return negObjectSubPropAxInTbox;
	}
	/**
	 * negative data property inclusion axioms in the tbox
	 */
	public Set<OWLDisjointDataPropertiesAxiom>  getNegativeDataSubPropertyAxioms() {
		return negDataSubPropAxInTbox;
	}
	/**
	 * negative concept inclusion in the negative closure
	 */
	public Set<OWLSubClassOfAxiom> getNegativeInclInNegClosure() {
		if (negIncAxInNegClos ==null) {
			negIncAxInNegClos = new HashSet<OWLSubClassOfAxiom>();
			negObjectSubPropAxInNegClos = new HashSet<OWLDisjointObjectPropertiesAxiom>();
			negDataSubPropAxInNegClos = new HashSet<OWLDisjointDataPropertiesAxiom>();
			organizeNegativeClosure(negativeInclClosure, negIncAxInNegClos, negObjectSubPropAxInNegClos, negDataSubPropAxInNegClos);
		}
		return negIncAxInNegClos;
	}
	/**
	 * negative object property inclusion axioms in the negative closure
	 */
	protected Set<OWLDisjointObjectPropertiesAxiom> getNegativeObjectSubPropInNegClosure() {
		if (negObjectSubPropAxInNegClos ==null) {
			negIncAxInNegClos = new HashSet<OWLSubClassOfAxiom>();
			negObjectSubPropAxInNegClos = new HashSet<OWLDisjointObjectPropertiesAxiom>();
			negDataSubPropAxInNegClos = new HashSet<OWLDisjointDataPropertiesAxiom>();
			organizeNegativeClosure(negativeInclClosure, negIncAxInNegClos, negObjectSubPropAxInNegClos, negDataSubPropAxInNegClos);
		}
		return negObjectSubPropAxInNegClos;
	}
	/**
	 * negative data property inclusion axioms in the negative closure
	 */
	protected Set<OWLDisjointDataPropertiesAxiom> getNegativeDataSubPropAxInClosure() {
		if (negDataSubPropAxInNegClos == null) {
			negIncAxInNegClos = new HashSet<OWLSubClassOfAxiom>();
			negObjectSubPropAxInNegClos = new HashSet<OWLDisjointObjectPropertiesAxiom>();
			negDataSubPropAxInNegClos = new HashSet<OWLDisjointDataPropertiesAxiom>();
			organizeNegativeClosure(negativeInclClosure, negIncAxInNegClos, negObjectSubPropAxInNegClos, negDataSubPropAxInNegClos);
		}
		return negDataSubPropAxInNegClos ;
	}
	public OWLDataFactory getFactory() {
		return fac;
	}

	public OWLQLNormalizer getNormalizer() {
		return normalizer;
	}

	protected  Set<OWLAxiom> computeNegativeInclusionClosure(Set<OWLSubClassOfAxiom> deltaNegIncAx,
			Set<OWLDisjointObjectPropertiesAxiom> deltaNegObjSubPropAx,Set<OWLDisjointDataPropertiesAxiom> deltaNegDataSubPropAx,
			Set<OWLSubClassOfAxiom> deltaPosIncAx, Set<OWLSubPropertyAxiom> deltaPosSubPropAx) {
		
		return computeNegativeInclusionClosure(deltaNegIncAx, deltaNegObjSubPropAx, deltaNegDataSubPropAx, deltaPosIncAx, deltaPosSubPropAx, null);
	}

	
	protected  Set<OWLAxiom> computeNegativeInclusionClosure(Collection<OWLSubClassOfAxiom> deltaNegIncAx,
			Collection<OWLDisjointObjectPropertiesAxiom> deltaNegObjSubPropAx, Collection<OWLDisjointDataPropertiesAxiom> deltaNegDataSubPropAx,
			Collection<OWLSubClassOfAxiom> deltaPosIncAx, Collection<OWLSubPropertyAxiom> deltaPosSubPropAx, OWLAxiom inconsistencyWitness) {
		Set<OWLAxiom> ret = new HashSet<OWLAxiom>();
		/*if (negativeInclClosure!=null) {
			deltaNegIncAx.removeAll(negativeInclClosure);
			deltaNegDataSubPropAx.removeAll(negativeInclClosure);
			deltaNegObjSubPropAx.removeAll(negativeInclClosure);
			ret.addAll(negativeInclClosure);
			deltaPosIncAx.removeAll(posIncAxInTbox);
			deltaPosSubPropAx.removeAll(posSubPropAxInTbox);
		}*/
		
		// 1. add all negative inclusions
		ret.addAll(deltaNegIncAx);
		ret.addAll(deltaNegObjSubPropAx);
		ret.addAll(deltaNegDataSubPropAx);
		//
		
		List<OWLSubClassOfAxiom> negIncAx;
		List<OWLDisjointObjectPropertiesAxiom> negObjSubPropAx;
		List<OWLDisjointDataPropertiesAxiom> negDataSubPropAx;
		List<OWLSubClassOfAxiom>  posIncAx; 
		List<OWLSubPropertyAxiom> posSubPropAx;
		
		for (int i = 0; i <2; i++) {
			if (i==0) {
				// positive closure set to all pos axioms
				Set<OWLSubClassOfAxiom> temp = new HashSet<OWLSubClassOfAxiom>(posIncAxInTbox);
				temp.addAll(deltaPosIncAx);
				posIncAx = new LinkedList<OWLSubClassOfAxiom>(temp);
				Set<OWLSubPropertyAxiom> temp2 = new HashSet<OWLSubPropertyAxiom>(posSubPropAxInTbox);	
				temp2.addAll(deltaPosSubPropAx);
				posSubPropAx = new LinkedList<OWLSubPropertyAxiom>(temp2);
				//
				
				// negative closure set to delta
				negIncAx = new LinkedList<OWLSubClassOfAxiom>( deltaNegIncAx);
				negObjSubPropAx = new LinkedList<OWLDisjointObjectPropertiesAxiom>(deltaNegObjSubPropAx);
				negDataSubPropAx = new LinkedList<OWLDisjointDataPropertiesAxiom>(deltaNegDataSubPropAx);
				//
			} else {
				if (negativeInclClosure == null) {
					continue;
				}
				// negative closure fixed
				negIncAx = /*Collections.unmodifiableSet*/new LinkedList<OWLSubClassOfAxiom>(getNegativeInclInNegClosure());// new HashSet<OWLSubClassOfAxiom>(getNegativeInclInNegClosure());
				//negIncAx.addAll(deltaNegIncAx);
				negObjSubPropAx =/*Collections.unmodifiableSet*/new LinkedList<OWLDisjointObjectPropertiesAxiom>(getNegativeObjectSubPropInNegClosure());// new HashSet<OWLDisjointObjectPropertiesAxiom>(getNegativeObjectSubPropInNegClosure());
				//negObjSubPropAx.addAll(deltaNegObjSubPropAx);
				negDataSubPropAx = /*Collections.unmodifiableSet*/new LinkedList<OWLDisjointDataPropertiesAxiom>(getNegativeDataSubPropAxInClosure());// new HashSet<OWLDisjointDataPropertiesAxiom>(getNegativeDataSubPropAxInClosure());
				//negDataSubPropAx.addAll(deltaNegDataSubPropAx);				
				//			
				
				//
				posIncAx = new LinkedList<OWLSubClassOfAxiom>(deltaPosIncAx);
				posSubPropAx = new LinkedList<OWLSubPropertyAxiom>(deltaPosSubPropAx);
				//
			}
			boolean firstTimeInWhileLoop = true;
			int numOfIterations = 0;
			Set<OWLSubClassOfAxiom> newNegIncAx = new HashSet<OWLSubClassOfAxiom>();
			Set<OWLDisjointObjectPropertiesAxiom> newNegObjSubPropAx = new HashSet<OWLDisjointObjectPropertiesAxiom>();
			Set<OWLDisjointDataPropertiesAxiom> newNegDataSubPropAx = new HashSet<OWLDisjointDataPropertiesAxiom>();

			while (!negIncAx.isEmpty() || !negObjSubPropAx.isEmpty()
					|| !negDataSubPropAx.isEmpty()) {
				if (logger.isDebugEnabled()) {
					logger.debug("Number of iterations "+numOfIterations);
					//if (numOfIterations>50)
					{
						logger.debug("posIncAc: {}\n{}", posIncAx.size(), posIncAx);
						logger.debug("negIncAx: {}\n{}", negIncAx.size(), negIncAx) ;
						logger.debug("negObjSubPropAx: {}\n{}", negObjSubPropAx.size(), negObjSubPropAx);
						logger.debug("negDataSubPropAx: {}\n{}", negDataSubPropAx.size(), negDataSubPropAx);
					}
				}
				numOfIterations++;
				newNegIncAx.clear();
				newNegObjSubPropAx.clear();
				newNegDataSubPropAx.clear();

				// if sub(B1, B2) in Tbox and sub(B2, not(B3)) or sub(B3, not(B2)) in negClos, then
				// add sub(B1, not(B3) to negClos;
				if (!negIncAx.isEmpty()) {
					for (OWLSubClassOfAxiom posSubAx : posIncAx) {
						OWLClassExpression b1 = posSubAx.getSubClass();
						OWLClassExpression b2 = posSubAx.getSuperClass();
						assert !isNegatedOWLQLRHSConcept(b2) : b2 +"\n"+b1;
						for (OWLSubClassOfAxiom negSubAx : negIncAx) {
							OWLClassExpression subclass = negSubAx.getSubClass();
							OWLClassExpression supclass = negSubAx.getSuperClass();
							if (isNegatedOWLQLRHSConcept(supclass)) {
								if (b2.equals(subclass)) {
									newNegIncAx.add(fac.getOWLSubClassOfAxiom(b1,
											supclass));
								} else {
									OWLClassExpression notSupclass = fac
											.getOWLObjectComplementOf(supclass)
											.getNNF();
									if (b2.equals(notSupclass)) {
										OWLClassExpression notSubclass = fac
												.getOWLObjectComplementOf(subclass)
												.getNNF();
										newNegIncAx.add(fac.getOWLSubClassOfAxiom(
												b1, notSubclass));
									}
								}
							}
						}
								
					}
				}

				//
				for (OWLSubPropertyAxiom posSubAx : posSubPropAx) {
					OWLPropertyExpression r1 = posSubAx.getSubProperty();
					OWLPropertyExpression r2 = posSubAx.getSuperProperty();
					OWLQuantifiedRestriction b1;
					OWLQuantifiedRestriction b1Inv;
					if (r1.isObjectPropertyExpression()) {
						b1 = fac.getOWLObjectSomeValuesFrom(
								(OWLObjectPropertyExpression) r1, fac
										.getOWLThing());
						OWLObjectPropertyExpression r1Inv = fac
								.getOWLObjectInverseOf((OWLObjectPropertyExpression) r1).getSimplified();
						b1Inv = fac.getOWLObjectSomeValuesFrom(r1Inv, fac
								.getOWLThing());
					} else {
						assert r1.isDataPropertyExpression() : r1;
						b1 = fac.getOWLDataSomeValuesFrom(
								(OWLDataPropertyExpression) r1, fac
										.getTopDatatype());
						b1Inv = null;
					}
					//if sub(R1, R2) in Tbox and sub(some(R2, top), not(B)) or sub(B, not(some(R2, top))) is in negClos, then
					// add sub(some(R1, top), not(B))  in negClos;

					// if sub(R1, R2) in Tbox and sub(some(inv(R2), top), not(B)) or sub(B, not(some(inv(R2), top))) is in negClos, then
					// add sub(some(inv(R1), top), not(B))  in negClos;
					for (OWLSubClassOfAxiom negSubAx : negIncAx) {
						OWLClassExpression subclass = negSubAx.getSubClass();
						OWLClassExpression supclass = negSubAx.getSuperClass();
						if (isNegatedOWLQLRHSConcept(supclass)) {
							if (subclass
									.getClassExpressionType()
									.equals(
											ClassExpressionType.OBJECT_SOME_VALUES_FROM)
									|| subclass
											.getClassExpressionType()
											.equals(
													ClassExpressionType.DATA_SOME_VALUES_FROM)) {
								OWLQuantifiedRestriction some = (OWLQuantifiedRestriction) subclass;
								OWLPropertyExpression prop = some.getProperty();
								if (r2.equals(prop)) {
									newNegIncAx.add(fac.getOWLSubClassOfAxiom(
											b1, supclass));
								} else if (prop.isObjectPropertyExpression()) {
									OWLObjectPropertyExpression oprop = (OWLObjectPropertyExpression) prop;
									OWLObjectPropertyExpression propInv = fac
											.getOWLObjectInverseOf(oprop).getSimplified();
									if (r2.equals(propInv)) {
										assert b1Inv != null : r2 + "\n" + prop
												+ "\n" + propInv;
										newNegIncAx.add(fac
												.getOWLSubClassOfAxiom(b1Inv,
														supclass));
									}
								}
							}
							OWLClassExpression notSupclass = fac
									.getOWLObjectComplementOf(supclass)
									.getNNF();
							if (notSupclass
									.getClassExpressionType()
									.equals(
											ClassExpressionType.OBJECT_SOME_VALUES_FROM)
									|| notSupclass
											.getClassExpressionType()
											.equals(
													ClassExpressionType.DATA_SOME_VALUES_FROM)) {
								OWLQuantifiedRestriction some = (OWLQuantifiedRestriction) notSupclass;
								OWLPropertyExpression prop = some.getProperty();
								if (r2.equals(prop)) {
									OWLClassExpression notSubclass = fac
											.getOWLObjectComplementOf(subclass)
											.getNNF();
									newNegIncAx.add(fac.getOWLSubClassOfAxiom(
											b1, notSubclass));
								} else if (prop.isObjectPropertyExpression()) {
									OWLObjectPropertyExpression oprop = (OWLObjectPropertyExpression) prop;
									OWLObjectPropertyExpression propInv = fac
											.getOWLObjectInverseOf(oprop).getSimplified();
									if (r2.equals(propInv)) {
										assert b1Inv != null : r2 + "\n" + prop
												+ "\n" + propInv;
										OWLClassExpression notSubclass = fac
												.getOWLObjectComplementOf(
														subclass).getNNF();
										newNegIncAx.add(fac
												.getOWLSubClassOfAxiom(b1Inv,
														notSubclass));
									}
								}
							}
						}

					}
					//

					//if sub(R1,R2) in TBox and sub(R2, not(R3)) or sub(R3, not(R2) in negClos, then
					//add sub(R1, not(R3)) to negClos
					Set<Set<? extends OWLPropertyExpression>> disjointProperties = new HashSet<Set<? extends OWLPropertyExpression>>();
					for (OWLDisjointObjectPropertiesAxiom dpax : negObjSubPropAx) {
						disjointProperties.add(dpax.getProperties());
					}
					for (OWLDisjointDataPropertiesAxiom dpax : negDataSubPropAx) {
						disjointProperties.add(dpax.getProperties());
					}
					for (Set<? extends OWLPropertyExpression> props : disjointProperties) {
						assert props.size() == 2 || props.size() == 1 : props
								+ "\n normalization was not performed properly";
						if (props.contains(r2)) {
							if (r2.isObjectPropertyExpression()) {
								Set<OWLObjectPropertyExpression> disjProps = new HashSet<OWLObjectPropertyExpression>();
								disjProps.add((OWLObjectPropertyExpression) r1);
								disjProps
										.addAll((Set<OWLObjectPropertyExpression>) props);
								if (props.size() > 1) {
									disjProps.remove(r2);
								}
								newNegObjSubPropAx
										.add(fac
												.getOWLDisjointObjectPropertiesAxiom(disjProps));
							} else {
								assert r2.isDataPropertyExpression() : r2;
								Set<OWLDataPropertyExpression> disjProps = new HashSet<OWLDataPropertyExpression>();
								disjProps.add((OWLDataPropertyExpression) r1);
								disjProps
										.addAll((Set<OWLDataPropertyExpression>) props);
								if (props.size() > 1) {
									disjProps.remove(r2);
								}
								newNegDataSubPropAx
										.add(fac
												.getOWLDisjointDataPropertiesAxiom(disjProps));
							}
						}
						//Case not covered in the original DL-Lite techreport:
						// if sub(R1,inv(R2)) in TBox and sub(R2, not(R3)) or sub(R3, not(R2) in negClos, then
						//add sub(inv(R1), not(R3)) to negClos
						if (r2.isObjectPropertyExpression()) {
							OWLObjectPropertyExpression or2 = (OWLObjectPropertyExpression) r2;
							OWLObjectPropertyExpression invR2 =fac.getOWLObjectInverseOf(or2).getSimplified();
							if (props.contains(invR2)) {
								Set<OWLObjectPropertyExpression> disjProps = new HashSet<OWLObjectPropertyExpression>();
								disjProps.add((OWLObjectPropertyExpression) fac.getOWLObjectInverseOf((OWLObjectPropertyExpression)r1).getSimplified());
								disjProps
										.addAll((Set<OWLObjectPropertyExpression>) props);
								if (props.size() > 1) {
									disjProps.remove(invR2);
								}
								newNegObjSubPropAx
										.add(fac
												.getOWLDisjointObjectPropertiesAxiom(disjProps));
							}
								
						}
						//
					}
					//
				}
				//

				// if sub(some(R), not(some(R)) or sub(some(inv(R)), not(some(inv(R))) or sub(R, not(R)) in negClos, then
				// add all three axioms in negClos
				for (OWLSubClassOfAxiom ax : negIncAx) {
					OWLClassExpression subclass = ax.getSubClass();
					OWLClassExpression supclass = ax.getSuperClass();
					OWLClassExpression negSupclass = fac
							.getOWLObjectComplementOf(supclass).getNNF();
					if (negSupclass.equals(subclass)) {
						if (subclass.getClassExpressionType().equals(
								ClassExpressionType.OBJECT_SOME_VALUES_FROM)) {
							OWLObjectSomeValuesFrom some = (OWLObjectSomeValuesFrom) subclass;
							OWLObjectPropertyExpression r = some.getProperty();
							OWLObjectPropertyExpression rInv = fac
									.getOWLObjectInverseOf(r).getSimplified();
							OWLObjectSomeValuesFrom someInv = fac
									.getOWLObjectSomeValuesFrom(rInv, fac
											.getOWLThing());
							OWLClassExpression negSomeInv = fac
									.getOWLObjectComplementOf(someInv).getNNF();
							newNegIncAx.add(fac.getOWLSubClassOfAxiom(someInv,
									negSomeInv));
							Set<OWLObjectPropertyExpression> disjProps = new HashSet<OWLObjectPropertyExpression>();
							disjProps.add(r);
							newNegObjSubPropAx
									.add(fac
											.getOWLDisjointObjectPropertiesAxiom(disjProps));
						} else if (subclass.getClassExpressionType().equals(
								ClassExpressionType.DATA_SOME_VALUES_FROM)) {
							OWLDataSomeValuesFrom some = (OWLDataSomeValuesFrom) subclass;
							OWLDataPropertyExpression r = some.getProperty();
							Set<OWLDataPropertyExpression> disjProps = new HashSet<OWLDataPropertyExpression>();
							disjProps.add(r);
							newNegDataSubPropAx
									.add(fac
											.getOWLDisjointDataPropertiesAxiom(disjProps));
						}
					}

				}
				for (OWLDisjointObjectPropertiesAxiom ax : negObjSubPropAx) {
					Set<OWLObjectPropertyExpression> props = ax.getProperties();
					assert props.size() == 2 || props.size() == 1 : props
							+ "\n normalization was not performed properly";
					if (props.size() == 1) {
						OWLObjectPropertyExpression r = props.iterator().next();
						OWLObjectSomeValuesFrom some = fac
								.getOWLObjectSomeValuesFrom(r, fac
										.getOWLThing());
						OWLClassExpression negSome = fac
								.getOWLObjectComplementOf(some).getNNF();
						newNegIncAx.add(fac
								.getOWLSubClassOfAxiom(some, negSome));

						OWLObjectPropertyExpression rInv = fac
								.getOWLObjectInverseOf(r).getSimplified();
						OWLObjectSomeValuesFrom someInv = fac
								.getOWLObjectSomeValuesFrom(rInv, fac
										.getOWLThing());
						OWLClassExpression negSomeInv = fac
								.getOWLObjectComplementOf(someInv).getNNF();
						newNegIncAx.add(fac.getOWLSubClassOfAxiom(someInv,
								negSomeInv));
					}
				}
				for (OWLDisjointDataPropertiesAxiom ax : negDataSubPropAx) {
					Set<OWLDataPropertyExpression> props = ax.getProperties();
					assert props.size() == 2 || props.size() == 1 : props
							+ "\n normalization was not performed properly";
					if (props.size() == 1) {
						OWLDataPropertyExpression r = props.iterator().next();
						OWLDataSomeValuesFrom some = fac
								.getOWLDataSomeValuesFrom(r, fac
										.getTopDatatype());
						OWLClassExpression negSome = fac
								.getOWLObjectComplementOf(some).getNNF();
						newNegIncAx.add(fac
								.getOWLSubClassOfAxiom(some, negSome));
					}
				}
				//
				newNegDataSubPropAx.removeAll(ret);
				newNegObjSubPropAx.removeAll(ret);
				newNegIncAx.removeAll(ret);
				ret.addAll(newNegIncAx);
				ret.addAll(newNegObjSubPropAx);
				ret.addAll(newNegDataSubPropAx);
				// check for inconsistencies
				if (inconsistencyWitness!=null) {
					if (newNegDataSubPropAx.contains(inconsistencyWitness)
						|| newNegObjSubPropAx.contains(inconsistencyWitness)
						|| newNegIncAx.contains(inconsistencyWitness)) {
						return null;
					}
						
				}
				//
				negIncAx = new LinkedList<OWLSubClassOfAxiom>(newNegIncAx);
				negObjSubPropAx = new LinkedList<OWLDisjointObjectPropertiesAxiom>(newNegObjSubPropAx);
				negDataSubPropAx = new LinkedList<OWLDisjointDataPropertiesAxiom>(newNegDataSubPropAx);
				if (firstTimeInWhileLoop && i==1) {
					//
					Set<OWLSubClassOfAxiom> temp = new HashSet<OWLSubClassOfAxiom>(posIncAx);
					temp.addAll(posIncAxInTbox);
					posIncAx = new LinkedList<OWLSubClassOfAxiom>(temp);
					Set<OWLSubPropertyAxiom> temp2 = new HashSet<OWLSubPropertyAxiom>(posSubPropAx);
					temp2.addAll(posSubPropAxInTbox);
					posSubPropAx = new LinkedList<OWLSubPropertyAxiom>(temp2);
					//
				}
				firstTimeInWhileLoop = false;
			}
		}
		return ret;
			
	}
	
	

	/**
	 *  returns an unmodifiable set of OWL QL normalized axioms
	 * @return
	 */
	public Set<OWLAxiom> getNormalizedAxioms() {
		return Collections.unmodifiableSet(normalizedOntology.getAxioms());
	}
	
	public static Set<OWLAxiom> getTboxRboxAxioms( Set<OWLAxiom> axs) {
		 Set<OWLAxiom> ret = new HashSet<OWLAxiom>();
		 for (OWLAxiom ax: axs) {
			 if (!isAboxAxiom(ax)) {
				 ret.add(ax);
			 }
		 }
		 return ret;
	}

	public OWLOntology getNormalizedOntology() {
		return normalizedOntology;
	}
	/**
	 *  returns an unmodifiable set of non OWL QL axioms
	 * @return
	 */
	public Set<OWLAxiom> getNonQLAxioms() {
		return Collections.unmodifiableSet(nonOWLQLAxioms);
	}

	/**
	 * initialization which includes normalization of the Tbox axioms
	 */
	protected void init(Set<OWLAxiom> axioms) {
		init(axioms, true, true);
	}
	/**
	 * initialization which includes normalization of the Tbox axioms
	 */
	protected void init(Set<OWLAxiom> axioms, boolean normalize, boolean computeNegClos) {
		try {
			if (nonOWLQLAxioms == null) {
				nonOWLQLAxioms = new HashSet<OWLAxiom>();
			}
			if (normalizer == null) {
				normalizer = new OWLQLNormalizer(fac);
			}
			OWLOntology originalOnt = OWLManager.createOWLOntologyManager().createOntology();
			originalOnt.getOWLOntologyManager().addAxioms(originalOnt, axioms);
			
			Set<OWLAxiom> normalizedAxioms = normalize? normalizer.toQLNormalForm(axioms, nonOWLQLAxioms): axioms;
			normalizedOntology = normalizedOntology!=null? normalizedOntology: OWLManager.createOWLOntologyManager().createOntology();
			normalizedOntology.getOWLOntologyManager().addAxioms(normalizedOntology, normalizedAxioms);
			// add properties and types
			for (OWLObjectProperty p: originalOnt.getObjectPropertiesInSignature()) {
				if (!normalizedOntology.getObjectPropertiesInSignature().contains(p)) {
					normalizedOntology.getOWLOntologyManager().addAxiom(normalizedOntology,
							normalizedOntology.getOWLOntologyManager().getOWLDataFactory().getOWLDeclarationAxiom(p));
				}
			}
			for (OWLDataProperty p: originalOnt.getDataPropertiesInSignature()) {
				if (!normalizedOntology.getDataPropertiesInSignature().contains(p)) {
					normalizedOntology.getOWLOntologyManager().addAxiom(normalizedOntology,
							normalizedOntology.getOWLOntologyManager().getOWLDataFactory().getOWLDeclarationAxiom(p));
				}
			}
			for (OWLClass c: originalOnt.getClassesInSignature()) {
				if (!normalizedOntology.getClassesInSignature().contains(c)) {
					normalizedOntology.getOWLOntologyManager().addAxiom(normalizedOntology,
							normalizedOntology.getOWLOntologyManager().getOWLDataFactory().getOWLDeclarationAxiom(c));
				}
			}
			//
			if (logger.isDebugEnabled()) {
				logger.debug("Normalized axioms: {}", normalizedAxioms.size());
				for (OWLAxiom ax : normalizedAxioms) {
					logger.debug("\t{}",ax);
				}
				logger.debug("Non OWL QL axioms: {}", nonOWLQLAxioms.size());
				for (OWLAxiom ax : nonOWLQLAxioms) {
					logger.debug("\t{}",ax);
				}
			}
			if (posIncAxInTbox==null) {
				posIncAxInTbox = new HashSet<OWLSubClassOfAxiom>();
			} 
			if (negIncAxInTbox == null) {
				negIncAxInTbox = new HashSet<OWLSubClassOfAxiom>();
			}
			if (posSubPropAxInTbox == null) {
				posSubPropAxInTbox = new HashSet<OWLSubPropertyAxiom>();
			}
			if (negObjectSubPropAxInTbox == null) {
				negObjectSubPropAxInTbox = new HashSet<OWLDisjointObjectPropertiesAxiom>();
			}
			if (negDataSubPropAxInTbox == null) {
				negDataSubPropAxInTbox = new HashSet<OWLDisjointDataPropertiesAxiom>();
			}
			if (iri2PosIncAxInTbox == null) {
				iri2PosIncAxInTbox = new HashMap<String, Set<OWLSubClassOfAxiom>>();
			} 
			if (iri2PosSubPropAxInTbox == null) {
				iri2PosSubPropAxInTbox = new HashMap<String, Set<OWLSubPropertyAxiom>>();
			}
			if (reflexiveProps == null) {
				reflexiveProps = new HashSet<OWLObjectProperty>();
			}
			if (irreflexiveProps == null) {
				irreflexiveProps = new HashSet<OWLObjectProperty>();
			}
			// organize Tbox axioms per type
			organizeTboxAxioms(normalizedAxioms, negIncAxInTbox, negObjectSubPropAxInTbox, negDataSubPropAxInTbox, posIncAxInTbox, posSubPropAxInTbox, 
					reflexiveProps, irreflexiveProps, iri2PosIncAxInTbox, iri2PosSubPropAxInTbox);
			//
			if (computeNegClos) {
				negativeInclClosure = computeNegativeInclusionClosure(negIncAxInTbox, negObjectSubPropAxInTbox,
					negDataSubPropAxInTbox, posIncAxInTbox, posSubPropAxInTbox);
			}
		} catch (OWLOntologyCreationException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean isNegatedOWLQLRHSConcept(OWLClassExpression cl) {
		ClassExpressionType type = cl.getNNF().getClassExpressionType();
		return  type.equals(ClassExpressionType.OBJECT_COMPLEMENT_OF)
			|| type.equals(ClassExpressionType.OBJECT_ALL_VALUES_FROM)
			|| type.equals(ClassExpressionType.DATA_ALL_VALUES_FROM);
			
	}

	/**
	 * returns the key associated with a class expression
	 * @param lhsConcept
	 * @return
	 */
	public static String getKey(OWLClassExpression lhsConcept) {
		if (!lhsConcept.isAnonymous()) {
			return lhsConcept.asOWLClass().getIRI().toString();
		} else if (lhsConcept.getClassExpressionType().equals(ClassExpressionType.OBJECT_SOME_VALUES_FROM)
				|| lhsConcept.getClassExpressionType().equals(ClassExpressionType.DATA_SOME_VALUES_FROM)) {
			OWLQuantifiedRestriction rest =  (OWLQuantifiedRestriction) lhsConcept;
			OWLPropertyExpression pe = rest.getProperty();
			return getKey(pe);
		} else {
			assert false : "Invalid left hand side concept: "+lhsConcept;
			return null;
		}
	}

	/**
	 * returns the key associated with a property expression
	 * @param pe
	 * @return
	 */
	public static String getKey(OWLPropertyExpression pe) {
		if (pe.isObjectPropertyExpression()) {
			pe = ((OWLObjectPropertyExpression) pe).getSimplified();
		}
		if (!pe.isAnonymous()) {
			return ((OWLNamedObject) pe).getIRI().toString();
		} else {
			OWLObjectInverseOf ope = (OWLObjectInverseOf) pe;
			OWLObjectProperty op = (OWLObjectProperty) ope.getInverse();
			return op.getIRI().toString();
			
		}
	}

	public static boolean isAboxAxiom(OWLAxiom ax) {
		return OWLQLNormalizer.isAboxAxiom(ax);
	}
	private static void organizeNegativeClosure(Collection<OWLAxiom> normalizedNegClosureAxioms, Collection<OWLSubClassOfAxiom> negIncAxInNegClos,
			Collection<OWLDisjointObjectPropertiesAxiom> negObjectSubPropAxInNegClos, Collection<OWLDisjointDataPropertiesAxiom> negDataSubPropAxInNegClos) {
			organizeTboxAxioms(normalizedNegClosureAxioms, negIncAxInNegClos, negObjectSubPropAxInNegClos, negDataSubPropAxInNegClos,
					new HashSet<OWLSubClassOfAxiom>(), new HashSet<OWLSubPropertyAxiom>(), 
					new HashSet<OWLObjectProperty>(), new HashSet<OWLObjectProperty>(),
					new HashMap<String, Set<OWLSubClassOfAxiom>>(), new HashMap<String, Set<OWLSubPropertyAxiom>>());
	}
	private static void organizeTboxAxioms(Collection<OWLAxiom> normalizedAxioms, Collection<OWLSubClassOfAxiom> negIncAxInTbox,
			Collection<OWLDisjointObjectPropertiesAxiom> negObjectSubPropAxInTbox,Collection<OWLDisjointDataPropertiesAxiom> negDataSubPropAxInTbox,
			Collection<OWLSubClassOfAxiom> posIncAxInTbox, Collection<OWLSubPropertyAxiom> posSubPropAxInTbox,
			Collection<OWLObjectProperty> reflexivePropsInTbox, Collection<OWLObjectProperty> irreflexivePropsInTbox,
			 Map<String, Set<OWLSubClassOfAxiom>> iri2PosIncAxInTbox, Map<String, Set<OWLSubPropertyAxiom>> iri2PosSubPropAxInTbox) {
		// organize Tbox axioms per type
		for (OWLAxiom ax: normalizedAxioms ) {
			if (ax.getAxiomType().equals(AxiomType.SUBCLASS_OF)) {
				OWLSubClassOfAxiom subax = (OWLSubClassOfAxiom) ax;
				OWLClassExpression sub =  subax.getSubClass();
				OWLClassExpression superclass = subax.getSuperClass();
				if (!superclass.isAnonymous()
				|| superclass.getClassExpressionType().equals(ClassExpressionType.OBJECT_SOME_VALUES_FROM)
				|| superclass.getClassExpressionType().equals(ClassExpressionType.DATA_SOME_VALUES_FROM)) {
					posIncAxInTbox.add(subax);
					String key = getKey(superclass);
					Set<OWLSubClassOfAxiom> axs = iri2PosIncAxInTbox.get(key);
					if (axs==null) {
						axs = new HashSet<OWLSubClassOfAxiom>();
						iri2PosIncAxInTbox.put(key, axs);
					}
					axs.add(subax);
				} else {
					assert superclass.getClassExpressionType().equals(ClassExpressionType.OBJECT_ALL_VALUES_FROM)
					|| superclass.getClassExpressionType().equals(ClassExpressionType.DATA_ALL_VALUES_FROM)
					|| superclass.getClassExpressionType().equals(ClassExpressionType.OBJECT_COMPLEMENT_OF)
					: ax;
					negIncAxInTbox.add(subax);
				}
			} else if (ax.getAxiomType().equals(AxiomType.SUB_DATA_PROPERTY) 
					|| ax.getAxiomType().equals(AxiomType.SUB_OBJECT_PROPERTY)) {
				OWLSubPropertyAxiom subax = (OWLSubPropertyAxiom) ax;
				OWLPropertyExpression superprop = subax.getSuperProperty();
				posSubPropAxInTbox.add(subax);
				String key = getKey(superprop);
				Set<OWLSubPropertyAxiom> axs = iri2PosSubPropAxInTbox.get(key);
				if (axs==null) {
					axs = new HashSet<OWLSubPropertyAxiom>();
					iri2PosSubPropAxInTbox.put(key, axs);
				}
				axs.add(subax);				
			} else if (ax.getAxiomType().equals(AxiomType.DISJOINT_OBJECT_PROPERTIES)) {
				OWLDisjointObjectPropertiesAxiom dax = (OWLDisjointObjectPropertiesAxiom) ax;
				negObjectSubPropAxInTbox.add(dax);
			} else if (ax.getAxiomType().equals(AxiomType.DISJOINT_DATA_PROPERTIES)) {
				OWLDisjointDataPropertiesAxiom dax = (OWLDisjointDataPropertiesAxiom) ax;
				negDataSubPropAxInTbox.add(dax);
			} else if (ax.getAxiomType().equals(AxiomType.REFLEXIVE_OBJECT_PROPERTY)) {
				OWLReflexiveObjectPropertyAxiom rax = (OWLReflexiveObjectPropertyAxiom) ax;
				reflexivePropsInTbox.add(rax.getProperty().getNamedProperty());
			} else if (ax.getAxiomType().equals(AxiomType.IRREFLEXIVE_OBJECT_PROPERTY)) {
				OWLIrreflexiveObjectPropertyAxiom irrax = (OWLIrreflexiveObjectPropertyAxiom) ax;
				irreflexivePropsInTbox.add(irrax.getProperty().getNamedProperty());
			}
			else if (isAboxAxiom(ax)) {
				// abox assertions
				
			}  	else {
				assert false : "Unknown Normalized Axiom Type: " + ax;
			}
		}
	}
	
	public Set<OWLSubClassOfAxiom> getPositiveInclusions(String key) {
		return iri2PosIncAxInTbox.get(key);
	}
	public Set<OWLSubPropertyAxiom> getPositivePropertyInclusions(String key) {
		return iri2PosSubPropAxInTbox.get(key);
	}
	
	public Set<OWLObjectProperty> getReflexiveProperties() {
		if (reflexiveProps == null) {
			reflexiveProps = new HashSet<OWLObjectProperty>();
			for (OWLReflexiveObjectPropertyAxiom ax: normalizedOntology.getAxioms(AxiomType.REFLEXIVE_OBJECT_PROPERTY)) {
				reflexiveProps.add(ax.getProperty().getNamedProperty());
			}
		}
		return reflexiveProps;
	}
	
	public Set<OWLObjectProperty> getIrreflexiveProperties() {
		if (irreflexiveProps == null) {
			irreflexiveProps = new HashSet<OWLObjectProperty>();
			for (OWLIrreflexiveObjectPropertyAxiom ax: normalizedOntology.getAxioms(AxiomType.IRREFLEXIVE_OBJECT_PROPERTY)) {
				irreflexiveProps.add(ax.getProperty().getNamedProperty());
			}
		}
		return irreflexiveProps;
	}
	
	/*public NormalizedOWLQLTbox add(Set<OWLAxiom> axioms) {
		NormalizedOWLQLTbox ret = copy();
		Set<OWLAxiom> normalizedAxioms = ret.getNormalizer().toQLNormalForm(axioms, ret.nonOWLQLAxioms);
		ret.init(normalizedAxioms, false, false);
		Set<OWLSubClassOfAxiom> deltaNegIncAx = new HashSet<OWLSubClassOfAxiom>();
		Set<OWLDisjointObjectPropertiesAxiom> deltaNegObjSubPropAx = new HashSet<OWLDisjointObjectPropertiesAxiom>();
		Set<OWLDisjointDataPropertiesAxiom> deltaNegDataSubPropAx = new HashSet<OWLDisjointDataPropertiesAxiom>();
		Set<OWLSubClassOfAxiom> deltaPosIncAx = new HashSet<OWLSubClassOfAxiom>();
		Set<OWLSubPropertyAxiom> deltaPosSubPropAx = new HashSet<OWLSubPropertyAxiom>();
		organizeTboxAxioms(normalizedAxioms, deltaNegIncAx, deltaNegObjSubPropAx, deltaNegDataSubPropAx,deltaPosIncAx, deltaPosSubPropAx,
				new HashMap<String, Set<OWLSubClassOfAxiom>>(), new HashMap<String, Set<OWLSubPropertyAxiom>>());
		Set<OWLAxiom> newNegClos = ret.computeNegativeInclusionClosure(deltaNegIncAx, deltaNegObjSubPropAx, deltaNegDataSubPropAx, 
				deltaPosIncAx, deltaPosSubPropAx);
		// clear 
		ret.negIncAxInNegClos = null;
		ret.negObjectSubPropAxInNegClos = null;
		ret.negDataSubPropAxInNegClos = null;
		//
		ret.negativeInclClosure = newNegClos;
		return ret;
	
	}*/
	
	public Set<OWLClass> getUnsatisfiableNamedClasses() {
		if (unsatisfiableClasses == null) {
			unsatisfiableClasses = new HashSet<OWLClass>();
			// unsatisfiabe named classes C are in the negative closure in the form
			// subclass( C, not(C)) 
			for (OWLSubClassOfAxiom ax: getNegativeInclInNegClosure()) {
				OWLClassExpression sub = ax.getSubClass();
				OWLClassExpression sup = ax.getSuperClass();
				if (!sub.isAnonymous()
				&& sup.getClassExpressionType().equals(ClassExpressionType.OBJECT_COMPLEMENT_OF)
				&& ((OWLObjectComplementOf) sup).getOperand().equals(sub)) {
					unsatisfiableClasses.add(sub.asOWLClass());
				}
			}
		}
		return Collections.unmodifiableSet(unsatisfiableClasses);
		//return new HashSet<OWLClass>();
	}
	public Set<OWLProperty> getUnsatisfiableProperties() {
		if (unsatisfiableProperties == null) {
			unsatisfiableProperties = new HashSet<OWLProperty>();
			for (OWLDisjointDataPropertiesAxiom ax: getNegativeDataSubPropAxInClosure()) {
				if (ax.getProperties().size() == 1) {
					unsatisfiableProperties.add(ax.getProperties().iterator().next().asOWLDataProperty());
				}
			}
			for (OWLDisjointObjectPropertiesAxiom ax: getNegativeObjectSubPropInNegClosure()) {
				if (ax.getProperties().size() == 1) {
					unsatisfiableProperties.add(ax.getProperties().iterator().next().getNamedProperty().asOWLObjectProperty());
				}
			}
		}
		return Collections.unmodifiableSet(unsatisfiableProperties);
	}
	
	/*public boolean isUnsatisfiable(OWLClassExpression normalizedConcept) {
		if (!normalizedConcept.isAnonymous()) {
			if (normalizedConcept.isOWLNothing()) {
				return  true;
			} 
			if (getUnsatisfiableNamedClasses().contains(normalizedConcept)) {
				return true;
			}
		} else if (normalizedConcept.getClassExpressionType().equals(ClassExpressionType.OBJECT_COMPLEMENT_OF)) {
			OWLObjectComplementOf c = (OWLObjectComplementOf) normalizedConcept;
			OWLClassExpression 
		}
		
	}*/
	
	public boolean isSubClass(OWLClassExpression sub, OWLClassExpression sup) {
		// if sup is equivalent to Top return true
		if (sup.isOWLThing()) {
			return true;
		}
		if (sup.getClassExpressionType().equals(ClassExpressionType.OBJECT_COMPLEMENT_OF)
		&& 
		(
			((OWLObjectComplementOf) sup).getOperand().isOWLNothing()
			|| getUnsatisfiableNamedClasses().contains(((OWLObjectComplementOf) sup).getOperand()) 
		)  ) {
			return true;
		}
		//
		
		// if sub is equivalent to Bottom return true
		if (sub.isOWLNothing() || getUnsatisfiableNamedClasses().contains(sub)) {
			return true;
		}
		if (sub.getClassExpressionType().equals(ClassExpressionType.OBJECT_COMPLEMENT_OF)
		&& ((OWLObjectComplementOf) sub).getOperand().isOWLThing()) {
			return true;
		}
		if ( (sub.getClassExpressionType().equals(ClassExpressionType.OBJECT_SOME_VALUES_FROM)
		|| sub.getClassExpressionType().equals(ClassExpressionType.DATA_SOME_VALUES_FROM))
			&&
		(((OWLQuantifiedRestriction) sub).getProperty().isOWLBottomObjectProperty()
			||((OWLQuantifiedRestriction) sub).getProperty().isOWLBottomDataProperty()
			|| getUnsatisfiableProperties().contains(((OWLQuantifiedRestriction) sub).getProperty()))) {
			return true;
		}
		//
	
		// check that tbox union {subclass(NC, sub), sublcass(NC, not(sup)), NC(Nind)}
		// is inconsistent. NC: new concept, Nind: new individual
		
		List<OWLAxiom> newAxioms = new LinkedList<OWLAxiom>();
		NormalizedOWLQLTbox ret = this;//copy();
		OWLClass newConcept = ret.getNormalizer().createNewNamedClass();
		newAxioms.add(fac.getOWLSubClassOfAxiom(newConcept, sub.getNNF()));
		newAxioms.add(fac.getOWLSubClassOfAxiom(newConcept, sup.getComplementNNF()));
		List<OWLAxiom> newNonOWLQLAxioms = new LinkedList<OWLAxiom>();
		
		Set<OWLAxiom> normalizedAxioms = ret.getNormalizer().toQLNormalForm(newAxioms, newNonOWLQLAxioms);
		if (!newNonOWLQLAxioms.isEmpty()) {
			throw new RuntimeException("Both concepts  must be OWL QL valid concepts");
		}
		List<OWLSubClassOfAxiom> deltaNegIncAx = new LinkedList<OWLSubClassOfAxiom>();
		List<OWLDisjointObjectPropertiesAxiom> deltaNegObjSubPropAx = new LinkedList<OWLDisjointObjectPropertiesAxiom>();
		List<OWLDisjointDataPropertiesAxiom> deltaNegDataSubPropAx = new LinkedList<OWLDisjointDataPropertiesAxiom>();
		List<OWLSubClassOfAxiom> deltaPosIncAx = new LinkedList<OWLSubClassOfAxiom>();
		List<OWLSubPropertyAxiom> deltaPosSubPropAx = new LinkedList<OWLSubPropertyAxiom>();
		organizeTboxAxioms(normalizedAxioms, deltaNegIncAx, deltaNegObjSubPropAx, deltaNegDataSubPropAx,deltaPosIncAx, deltaPosSubPropAx,
				new HashSet<OWLObjectProperty>(), new HashSet<OWLObjectProperty>(),
				new HashMap<String, Set<OWLSubClassOfAxiom>>(), new HashMap<String, Set<OWLSubPropertyAxiom>>());
		// inconsistent iff subclass(NC, not(NC)) is in the negative closure 
		OWLAxiom inconsistencyWitness = fac.getOWLSubClassOfAxiom(newConcept, newConcept.getComplementNNF());
		Set<OWLAxiom> newNegClos = ret.computeNegativeInclusionClosure(deltaNegIncAx, deltaNegObjSubPropAx, deltaNegDataSubPropAx, 
				deltaPosIncAx, deltaPosSubPropAx, inconsistencyWitness);
		//assert newNegClos == null || !newNegClos.contains(inconsistencyWitness) : newNegClos;
		// 
		
		return newNegClos == null;
		
	}
	public boolean isSubProperty(OWLPropertyExpression sub, OWLPropertyExpression sup) {
		// false if one is data property and the other is object property
		if (sub.isDataPropertyExpression() ^ sup.isDataPropertyExpression()) {
			return false;
		}
		//
		
		// true if sub is the bottom property
		if (sub.isOWLBottomDataProperty() || sub.isOWLBottomObjectProperty()
			|| getUnsatisfiableProperties().contains(sub)) {
			return true;
		}
		// true if sup is the top property
		if (sup.isOWLTopDataProperty() || sup.isOWLTopObjectProperty()) {
			return true;
		}
		// 
		if (sub.isObjectPropertyExpression()) {
			OWLObjectPropertyExpression objSub = (OWLObjectPropertyExpression) sub;
			OWLObjectPropertyExpression objSup = (OWLObjectPropertyExpression) sup;
			// true if sub is equal or equivalent to the inverse of the bottom property
			// true if sup is the inverse of the top property
			if (objSub.getNamedProperty().isOWLBottomObjectProperty()
			|| getUnsatisfiableProperties().contains(objSub.getNamedProperty())		
			|| objSup.getNamedProperty().isOWLTopObjectProperty()
			) {
				return true;
			}
		}
		
		
		
		// check that tbox union {subprop(NP, sub), subprop(NP, not(sup), NP(Nind1, Nind2)}
		// is inconsistent. NP: new property, Nind1, Nind2: new individuals
		
		NormalizedOWLQLTbox ret = this;//copy();
		
		Set<OWLAxiom> newAxioms = new HashSet<OWLAxiom>();
		OWLAxiom inconsistencyWitness;
		if (sub.isDataPropertyExpression()) {
			OWLDataProperty newProp = ret.getNormalizer().createNewDataProperty();
			newAxioms.add(fac.getOWLSubDataPropertyOfAxiom(newProp,(OWLDataPropertyExpression) sub));
			newAxioms.add(fac.getOWLDisjointDataPropertiesAxiom(newProp, (OWLDataPropertyExpression) sup));
			// inconsistent iff subprop(NP, not(NP)) is in the negative closure 
			inconsistencyWitness = fac.getOWLDisjointDataPropertiesAxiom(newProp, newProp);
		} else {
			OWLObjectProperty newProp = ret.getNormalizer().createNewObjectProperty();
			newAxioms.add(fac.getOWLSubObjectPropertyOfAxiom(newProp,(OWLObjectPropertyExpression) sub));
			newAxioms.add(fac.getOWLDisjointObjectPropertiesAxiom(newProp, (OWLObjectPropertyExpression) sup));
			// inconsistent iff subprop(NP, not(NP)) is in the negative closure 
			inconsistencyWitness = fac.getOWLDisjointObjectPropertiesAxiom(newProp, newProp);
		}
		Set<OWLAxiom> newNonOWLQLAxioms = new HashSet<OWLAxiom>();
		Set<OWLAxiom> normalizedAxioms = ret.getNormalizer().toQLNormalForm(newAxioms, newNonOWLQLAxioms);
		if (!newNonOWLQLAxioms.isEmpty()) {
			throw new RuntimeException("Both concepts  must be OWL QL valid concepts");
		}
		Set<OWLSubClassOfAxiom> deltaNegIncAx = new HashSet<OWLSubClassOfAxiom>();
		Set<OWLDisjointObjectPropertiesAxiom> deltaNegObjSubPropAx = new HashSet<OWLDisjointObjectPropertiesAxiom>();
		Set<OWLDisjointDataPropertiesAxiom> deltaNegDataSubPropAx = new HashSet<OWLDisjointDataPropertiesAxiom>();
		Set<OWLSubClassOfAxiom> deltaPosIncAx = new HashSet<OWLSubClassOfAxiom>();
		Set<OWLSubPropertyAxiom> deltaPosSubPropAx = new HashSet<OWLSubPropertyAxiom>();
		organizeTboxAxioms(normalizedAxioms, deltaNegIncAx, deltaNegObjSubPropAx, deltaNegDataSubPropAx,deltaPosIncAx, deltaPosSubPropAx,
				new HashSet<OWLObjectProperty>(), new HashSet<OWLObjectProperty>(),
				new HashMap<String, Set<OWLSubClassOfAxiom>>(), new HashMap<String, Set<OWLSubPropertyAxiom>>());
		
		Set<OWLAxiom> newNegClos = ret.computeNegativeInclusionClosure(deltaNegIncAx, deltaNegObjSubPropAx, deltaNegDataSubPropAx, 
				deltaPosIncAx, deltaPosSubPropAx, inconsistencyWitness);
		
		return newNegClos == null;
		
	}
	
	protected boolean isGeneratedRole(String uri) {
		return getNormalizer().isGeneratedRole(uri);
	}

	protected boolean isGeneratedClass(String uri) {
		return getNormalizer().isGeneratedClass(uri);
	}

	protected boolean isTripleAbsentFromAbox(Triple qt) {
		Node pred = qt.getPredicate();
		if (pred.isURI() && isGeneratedRole(pred.getURI())) {
			return true;
		} else if (pred.isURI() && pred.getURI().equals(RDFConstants.RDF_TYPE)) {
			Node obj = qt.getObject();
			if (obj.isURI() && isGeneratedClass(obj.getURI())) {
				return true;
			}
		}
		return false;
	}
	
	protected void addOnlyAboxTriples(ElementUnion patterns, List<Triple> triples){
		boolean isAbsentFromAbox = false;
		ElementTriplesBlock sp = new ElementTriplesBlock();
		for (Triple t : triples) {
			if (isTripleAbsentFromAbox(t)) {
				isAbsentFromAbox = true;
				break;
			}
			sp.addTriple(t);
		}
		if (!isAbsentFromAbox)
			patterns.addElement(sp);
	}
	
	/**
	 * returns <code> null</code> if the Tbox entails an inconsistent KB;otherwise, returns a boolean query which, when evaluated against the Abox, returns <code>true</code> to indicate 
	 * that the KB is inconsistent.
	 * @param taxo
	 * @return
	 */
	public ConsistencyCheckResult computeConsistencyCheck(Taxonomy taxo) {
		ElementUnion patterns  = new ElementUnion();
		NewVariableGenerator varGen = new NewVariableGenerator("var_");
		for (OWLAxiom ax: getNegativeInclClosure()) {
			List<Triple> triples = new  ArrayList<Triple>();
			if (ax.getAxiomType().equals(AxiomType.SUBCLASS_OF)) {
				OWLSubClassOfAxiom subax = (OWLSubClassOfAxiom) ax;
				OWLClassExpression subclass = subax.getSubClass();
				OWLClassExpression superclass = subax.getSuperClass();
				Node var = Node.createVariable(varGen.createNewVariable());
				assert NormalizedOWLQLTbox.isNegatedOWLQLRHSConcept(superclass) : "Invalid negative subclass axiom: "+ ax;
				Triple t1 = toTriple(subclass, var, varGen, getFactory());
				if (!getReflexiveProperties().contains(getFactory().getOWLObjectProperty(IRI.create(t1.getPredicate().getURI())))) {
					triples.add(t1);
				}				
				OWLClassExpression negSuperclass = getFactory().getOWLObjectComplementOf(superclass).getNNF();
				Triple t2 = toTriple(negSuperclass, var, varGen, getFactory());
				if (!getReflexiveProperties().contains(getFactory().getOWLObjectProperty(IRI.create(t2.getPredicate().getURI())))) {
					triples.add(t2);
				}	
				if (triples.isEmpty()) {
					// t1 and t2 are satisfied based on the reflexivity of their predicate
					return new ConsistencyCheckResult(false);	
				}
			} else if (ax.getAxiomType().equals(AxiomType.DISJOINT_OBJECT_PROPERTIES))  {
				OWLDisjointObjectPropertiesAxiom disjAx = (OWLDisjointObjectPropertiesAxiom) ax;
				Set<OWLObjectPropertyExpression> props = disjAx.getProperties();
				assert props.size() == 1 || props.size() ==2: ax;
				Node x = Node.createVariable(varGen.createNewVariable());
				Node y = Node.createVariable(varGen.createNewVariable());
				int numberOfReflexiveProps =  0;
				for (OWLObjectPropertyExpression prop: props) {
					triples.add(toTriple(prop, x, y, varGen));
					if (getReflexiveProperties().contains(prop.getNamedProperty())) {
						numberOfReflexiveProps++;
					}
				}				
				if (numberOfReflexiveProps==props.size()) {
					// two disjoint properties (or bottom property) that are reflexive
					return new ConsistencyCheckResult(false);	
				} else if (numberOfReflexiveProps>0) {
					assert numberOfReflexiveProps == 1 : numberOfReflexiveProps+"\n"+props;
					// we add a new disjoint of the form: R(x, x), where R is the property which is not reflexive
					{
						List<Triple> newtriples = new  ArrayList<Triple>();
						for (OWLObjectPropertyExpression prop: props) {
							if (!getReflexiveProperties().contains(prop.getNamedProperty())) {
								newtriples.add(toTriple(prop, x, x, varGen));
							}
						}	
						
						addOnlyAboxTriples(patterns, newtriples);
					}
				}
				
			} else if (ax.getAxiomType().equals(AxiomType.DISJOINT_DATA_PROPERTIES))  {
				OWLDisjointDataPropertiesAxiom disjAx = (OWLDisjointDataPropertiesAxiom) ax;
				Set<OWLDataPropertyExpression> props = disjAx.getProperties();
				assert props.size() == 1 || props.size() ==2: ax;
				Node x = Node.createVariable(varGen.createNewVariable());
				Node y = Node.createVariable(varGen.createNewVariable());
				for (OWLDataPropertyExpression prop: props) {
					triples.add(toTriple(prop, x, y, varGen));
				}			
			} else {
				assert false : "Invalid axiom type in negative closure: "+ ax;
			}
			
			addOnlyAboxTriples(patterns, triples);
		}
		
		// handle irreflexive properties
		// check p(x, x) does not exist for an irreflexive property p
		if (!getIrreflexiveProperties().isEmpty()) {
			try {
				if (taxo == null) {
					taxo = new TaxonomyImpl(this);
				}
			} catch (OWLOntologyCreationException e) {
				throw new RuntimeException(e);
			}
			for (OWLObjectProperty p: getIrreflexiveProperties()) {
				for (OWLPropertyExpression subp: taxo.getAllSubproperties(p)) {
					OWLObjectPropertyExpression osubp = (OWLObjectPropertyExpression) subp;
					
					if (getReflexiveProperties().contains(osubp.getNamedProperty())) {
						// an irreflexive property has a reflexive subproperty
						return new ConsistencyCheckResult(false);	
					} else {
						ElementTriplesBlock sp = new ElementTriplesBlock();
						Node x = Node.createVariable(varGen.createNewVariable());
						Triple triple = toTriple(subp, x, x, varGen);
						if (!isTripleAbsentFromAbox(triple)) {
							sp.addTriple(toTriple(subp, x, x, varGen));
							patterns.addElement(sp);
						}
					}
				}
			}		
		}
		//
		
		Query ret = new Query();
		ret.setQueryAskType();
		if (patterns.getElements().size()>1) {
			ret.setQueryPattern(patterns);
		} else if (patterns.getElements().size() ==1) {
			ret.setQueryPattern(patterns.getElements().get(0));
		} else {
			return new ConsistencyCheckResult(true);
		}
		return new ConsistencyCheckResult((Boolean)null, ret);
	}
	/**
	 *  returns a query triple corresponding to a left hand side concept.
	 *  <ul>
	 *   <li> B --> x rdf:type B </li>
	 *   <li> some(R, top) --> x R ?y if  </li>
	 *   <li> some(inv(R), top) --> ?y R x </li>
	 *   </ul>
	 * @param lhsConcept
	 * @param var
	 * @return
	 */
	protected static Triple toTriple(OWLClassExpression lhsConcept, Node var, NewVariableGenerator varGen, OWLDataFactory fac) {
		if (!lhsConcept.isAnonymous()) {
			return new Triple(
						var,
						Node.createURI(RDFConstants.RDF_TYPE),
						Node.createURI(((OWLClass)lhsConcept).getIRI().toString()));
		 } else if (lhsConcept.getClassExpressionType().equals(ClassExpressionType.OBJECT_SOME_VALUES_FROM)
				 || lhsConcept.getClassExpressionType().equals(ClassExpressionType.DATA_SOME_VALUES_FROM)) {
			 OWLQuantifiedRestriction rest = (OWLQuantifiedRestriction) lhsConcept;
			 OWLPropertyExpression prop = rest.getProperty();
			 OWLPropertyRange propRange = rest.getFiller();
			 if (prop.isObjectPropertyExpression()) {
				 prop = ((OWLObjectPropertyExpression)prop).getSimplified();
			 }
			 assert propRange.equals(fac.getOWLThing()) || propRange.equals(fac.getTopDatatype()) : propRange;
			 if (!prop.isAnonymous()) {
				 OWLProperty p = (OWLProperty) prop;
				 return new Triple(
							var,
							Node.createURI(p.getIRI().toString()),
							Node.createVariable(varGen.createNewVariable()));
			 } else {
				assert prop instanceof OWLObjectInverseOf : prop;
			 	OWLObjectInverseOf inv = (OWLObjectInverseOf) prop;
			 	assert !inv.getInverse().isAnonymous() : inv+"\n"+ "Property expression simplification failed!";
			 	OWLObjectProperty op = (OWLObjectProperty) inv.getInverse();
			 	return new Triple(
			 			Node.createVariable(varGen.createNewVariable()),
						Node.createURI(op.getIRI().toString()),
						var);
			 	
			 }
		 } else {
			 throw new RuntimeException(lhsConcept+" is not a valid concept expression in the left hand side of a subclass axiom in OWL QL");
		 }
		
	}
	/**
	 * returns a query triple corresponding to a property expression
	 * 	<ul>
	 * 	<li> R  --> x R y </li>
	 *  <li> inv(R) --> y R x </li>
	 * </ul>
	 * 
	 */
	protected static Triple toTriple(OWLPropertyExpression pe, Node x, Node y, NewVariableGenerator varGen) {
		pe = getSimplified(pe);
		if (!pe.isAnonymous()) {
			OWLProperty p = (OWLProperty) pe;
			 return new Triple(
						x,
						Node.createURI(p.getIRI().toString()),
						y); 
		} else {
			assert pe instanceof OWLObjectInverseOf: pe;
			OWLObjectInverseOf inv = (OWLObjectInverseOf) pe;
			OWLObjectProperty p = (OWLObjectProperty) inv.getInverse();
			 return new Triple(
						y,
						Node.createURI(p.getIRI().toString()),
						x); 
		}
	}
	protected static OWLPropertyExpression getSimplified(OWLPropertyExpression pe) {
		if (pe.isObjectPropertyExpression()) {
			pe = ((OWLObjectPropertyExpression) pe).getSimplified();
		}
		return pe;
	}
	
	public NormalizedOWLQLTbox copy() {
		return new NormalizedOWLQLTbox(this);
	}
	public Object clone() {
		return copy();
	}
	
}
