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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLQuantifiedRestriction;

/**
 * This class implements all methods of the {@link Taxonomy} interface involving wildcard processing.
 * @author fokoue
 *
 */
public class TaxonomyWildcardHelper {
	
	private Taxonomy taxo;

	public TaxonomyWildcardHelper(Taxonomy taxo) {
		super();
		this.taxo = taxo;
	}
	public Set<OWLClassExpression> applyPropertyWildcard(Set<OWLClassExpression> mostGeneralSubmees) {
		Set<OWLClassExpression> ret = new HashSet<OWLClassExpression>();
		if (mostGeneralSubmees.isEmpty()) {
			for (OWLObjectProperty p : taxo.getObjectProperties()) {
				ret.add(taxo.getTbox().getFactory().getOWLObjectSomeValuesFrom(p, taxo.getTbox().getFactory().getOWLThing()));
			}
			for (OWLObjectInverseOf p : taxo.getInverses()) {
				ret.add(taxo.getTbox().getFactory().getOWLObjectSomeValuesFrom(p, taxo.getTbox().getFactory().getOWLThing()));
			}
			for (OWLDataProperty p : taxo.getDataProperties()) {
				ret.add(taxo.getTbox().getFactory().getOWLDataSomeValuesFrom(p, taxo.getTbox().getFactory().getTopDatatype()));
			}
			
		} else {
			//get all subsumees of ret
			Set<OWLClassExpression> subsumees = new HashSet<OWLClassExpression>();
			for (OWLClassExpression c: mostGeneralSubmees) {
				assert c  instanceof OWLQuantifiedRestriction : c;
				OWLPropertyExpression p = ((OWLQuantifiedRestriction) c).getProperty();
				subsumees.add(c);
				for (OWLPropertyExpression subP : taxo.getAllSubproperties(p)) {
					if (subP.isObjectPropertyExpression()) {
						subsumees.add(taxo.getTbox().getFactory().getOWLObjectSomeValuesFrom((OWLObjectPropertyExpression) subP, taxo.getTbox().getFactory().getOWLThing()));
					} else {
						assert subP.isDataPropertyExpression() : subP;
						subsumees.add(taxo.getTbox().getFactory().getOWLDataSomeValuesFrom((OWLDataPropertyExpression) subP, taxo.getTbox().getFactory().getTopDatatype()));
						
					}
				}
			}
			ret = subsumees;
		}
		// remove unsatisfiable classes
		ret.removeAll(taxo.getUnsatisfiableClassExpressions());
		//
		return ret;
	}

	/**
	 * returns a map associating a number of wildcards processed (>= minimalNumberOfConceptWildcardsToReport) to the most general subsumees for the corresponding number of wildcards.
	 * In other words, for k>= minimalNumberOfConceptWildcardsToReport),  map.get(k) =  getMostGeneralSubsumees(classes, properties, 0,0,k) . 
	 * NOTE: for k>= minimalNumberOfConceptWildcardsToReport, map.get(k) may be null if getMostGeneralSubsumees(classes, properties, 0,0,k).isEmpty().
	 * @param classes
	 * @param properties
	 * @param conceptWildcardsToProcess
	 * @param minimalNumberOfConceptWildcardsToReport
	 * @return
	 */
	public Map<Integer, Set<OWLClassExpression>> computeMostGeneralSubsumees(
			Set<OWLClassExpression> classes,
			Set<OWLPropertyExpression> properties,
			int conceptWildcardsToProcess, 
			int minimalNumberOfConceptWildcardsToReport) {
		if (minimalNumberOfConceptWildcardsToReport> conceptWildcardsToProcess) {
			throw new IllegalArgumentException("minimalNumberOfConceptWildcardsToReport must be less than or equals to conceptWildcardsToProcess");
		}
		Map<Integer, Set<OWLClassExpression>> ret = new HashMap<Integer, Set<OWLClassExpression>>();
		computeMostGeneralSubsumees(classes, properties, conceptWildcardsToProcess, minimalNumberOfConceptWildcardsToReport, 
				ret, 0);
		return ret;
	}

	protected void computeMostGeneralSubsumees(
			Set<OWLClassExpression> classes,
			Set<OWLPropertyExpression> properties,
			int conceptWildcardsToProcess, 
			int minimalNumberOfConceptWildcardsToReport,
			Map<Integer, Set<OWLClassExpression>> numberOfWildcards2Results, int conceptWildcardsAlreadyProccessed) {
		
		Set<OWLClassExpression> mandatoryPart = new HashSet<OWLClassExpression>();
		if (!classes.isEmpty() || !properties.isEmpty() ) {
			mandatoryPart = taxo.getMostGeneralSubsumees(classes, properties);
			if (mandatoryPart == null || mandatoryPart.isEmpty()) {
				// no common subsumees
				return;
			}
		}
		if (conceptWildcardsToProcess <= 0) {
			if (!mandatoryPart.isEmpty() && conceptWildcardsAlreadyProccessed>=minimalNumberOfConceptWildcardsToReport) {
				Set<OWLClassExpression> res = numberOfWildcards2Results.get(conceptWildcardsAlreadyProccessed);
				if (res == null) {
					res = new HashSet<OWLClassExpression>();
					numberOfWildcards2Results.put(conceptWildcardsAlreadyProccessed, res);
				}
				res.addAll(mandatoryPart);
			}
		} else {
			if (!classes.isEmpty() && !mandatoryPart.isEmpty()) {
				// classes are in the fixed part ==> the mandatory part should be added to the result
				// because it corresponds to the case where a concept wildcard matches one class in classes
				//ret.addAll(mandatoryPart);
				for (int i=1;i<=conceptWildcardsToProcess;i++) {
					if (conceptWildcardsAlreadyProccessed+i>=minimalNumberOfConceptWildcardsToReport) {
						Set<OWLClassExpression> res = numberOfWildcards2Results.get(conceptWildcardsAlreadyProccessed+i);
						if (res == null) {
							res = new HashSet<OWLClassExpression>();
							numberOfWildcards2Results.put(conceptWildcardsAlreadyProccessed+i, res);
						}
						res.addAll(mandatoryPart);
					}
				}
			}
			Set<OWLClassExpression> remainingClasses = new HashSet<OWLClassExpression>(taxo.getTbox().getNormalizedOntology().getClassesInSignature());
			remainingClasses.removeAll(classes);
			for (OWLClassExpression c: remainingClasses) {
				Set<OWLClassExpression> newClasses = new HashSet<OWLClassExpression>(classes);
				newClasses.add(c);
				computeMostGeneralSubsumees(newClasses, properties, conceptWildcardsToProcess-1, 
						minimalNumberOfConceptWildcardsToReport, numberOfWildcards2Results,conceptWildcardsAlreadyProccessed+1);
						//conceptPropertyWildcards, propertyWildcards, conceptWildcards-1);
				
			}
		}
	}
	
	public Set<OWLClassExpression> getMostGeneralSubsumees(
			Set<OWLClassExpression> classes,
			Set<OWLPropertyExpression> properties,
			int conceptPropertyWildcards, int propertyWildcards,
			int conceptWildcards) {
		
		Set<OWLClassExpression> mandatoryPart = new HashSet<OWLClassExpression>();
		if (!classes.isEmpty() || !properties.isEmpty() ) {
			mandatoryPart = taxo.getMostGeneralSubsumees(classes, properties);
			if (mandatoryPart == null || mandatoryPart.isEmpty()) {
				// no common subsumees
				return mandatoryPart;
			}
		}
		if (conceptWildcards <= 0 && conceptPropertyWildcards<=0 && propertyWildcards<=0) {
			return mandatoryPart;
		}
		Set<OWLClassExpression>  ret = new HashSet<OWLClassExpression>();
		if (conceptWildcards>0 ) {
			if (!classes.isEmpty()) {
				// classes are in the fixed part ==> the mandatory part should be added to the result
				// because it corresponds to the case where a concept wildcard matches one class in classes
				ret.addAll(mandatoryPart);
			}
			Set<OWLClassExpression> remainingClasses = new HashSet<OWLClassExpression>(taxo.getTbox().getNormalizedOntology().getClassesInSignature());
			remainingClasses.removeAll(classes);
			for (OWLClassExpression c: remainingClasses) {
				Set<OWLClassExpression> newClasses = new HashSet<OWLClassExpression>(classes);
				newClasses.add(c);
				ret.addAll( getMostGeneralSubsumees(newClasses, properties,
						conceptPropertyWildcards, propertyWildcards, conceptWildcards-1));
				
			}
			if (ret.isEmpty()) {
				return ret; //no common subsumees
			}
//			if (!mandatoryPart.isEmpty()) {
//				// i.e., classes or properties is not empty
//				assert !classes.isEmpty() || !properties.isEmpty()  : classes+"\n"+properties;
//				//
//				if (!classes.isEmpty()) {
//					// classes are in fixed part ==> the mandatory part should be added to the result
//					// because it corresponds to the case where a concept wildcard match one class in classes
//					ret.addAll(mandatoryPart);
//				}
//				
//				for (OWLClassExpression c: classesNotSeenYet) {
//					
//					// compute most general subsumees of c and previous inputs
//					Set<OWLClassExpression> newPartialSubs = new HashSet<OWLClassExpression>();
//					for (OWLClassExpression partialSub: mandatoryPart) {
//						assert partialSub instanceof OWLQuantifiedRestriction;
//						OWLPropertyExpression p = ((OWLQuantifiedRestriction) partialSub).getProperty();
//						newPartialSubs.addAll(getMostGeneralSubsumees(Collections.singleton(c), Collections.singleton(p)));
//					}
//					newPartialSubs = getMaximum(newPartialSubs);
//					//
//					
//					// 
//					for (OWLClassExpression partialSub: newPartialSubs) {
//						assert partialSub instanceof OWLQuantifiedRestriction;
//						OWLPropertyExpression p = ((OWLQuantifiedRestriction) partialSub).getProperty();
//						Set<OWLClassExpression> newClassesNotSeenYet = new HashSet<OWLClassExpression>(classesNotSeenYet);
//						newClassesNotSeenYet.remove(c);
//						ret.addAll( getMostGeneralSubsumees(Collections.EMPTY_SET, Collections.singleton(p),
//							conceptPropertyWildcards, propertyWildcards, conceptWildcards-1, newClassesNotSeenYet));
//					}
//					// 
//					 
//					
//				}
//				
//			} else {
//				for (OWLClassExpression c: classesNotSeenYet) {
//					Set<OWLClassExpression> newClassesNotSeenYet = new HashSet<OWLClassExpression>(classesNotSeenYet);
//					newClassesNotSeenYet.remove(c);
//					ret.addAll( getMostGeneralSubsumees(Collections.singleton(c), Collections.EMPTY_SET,
//							conceptPropertyWildcards, propertyWildcards, conceptWildcards-1, newClassesNotSeenYet));
//							
//				}
//		/	}
		}
		if (conceptPropertyWildcards>0 || propertyWildcards>0) {
			if (ret.isEmpty()) {
				for (OWLObjectProperty p : taxo.getObjectProperties()) {
					ret.add(taxo.getTbox().getFactory().getOWLObjectSomeValuesFrom(p, taxo.getTbox().getFactory().getOWLThing()));
				}
				for (OWLObjectInverseOf p : taxo.getInverses()) {
					ret.add(taxo.getTbox().getFactory().getOWLObjectSomeValuesFrom(p, taxo.getTbox().getFactory().getOWLThing()));
				}
				for (OWLDataProperty p : taxo.getDataProperties()) {
					ret.add(taxo.getTbox().getFactory().getOWLDataSomeValuesFrom(p, taxo.getTbox().getFactory().getTopDatatype()));
				}
				
			} else {
				//get all subsumees of ret
				Set<OWLClassExpression> subsumees = new HashSet<OWLClassExpression>();
				for (OWLClassExpression c: ret) {
					assert c  instanceof OWLQuantifiedRestriction : c;
					OWLPropertyExpression p = ((OWLQuantifiedRestriction) c).getProperty();
					subsumees.add(c);
					for (OWLPropertyExpression subP : taxo.getAllSubproperties(p)) {
						if (subP.isObjectPropertyExpression()) {
							subsumees.add(taxo.getTbox().getFactory().getOWLObjectSomeValuesFrom((OWLObjectPropertyExpression) subP, taxo.getTbox().getFactory().getOWLThing()));
						} else {
							assert subP.isDataPropertyExpression() : subP;
							subsumees.add(taxo.getTbox().getFactory().getOWLDataSomeValuesFrom((OWLDataPropertyExpression) subP, taxo.getTbox().getFactory().getTopDatatype()));
							
						}
					}
				}
				ret = subsumees;
			}
			// remove unsatisfiable classes
			ret.removeAll(taxo.getUnsatisfiableClassExpressions());
			//
		}
		return ret;
	}
	
	

}
