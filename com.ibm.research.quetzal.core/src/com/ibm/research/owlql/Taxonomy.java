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

import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpression;

public interface Taxonomy {

	public void build();
	/**
	 * returns wether sub is a subclass of sup or is equivalent to sup
	 * @param normalizedSub
	 * @param normalizedSup
	 * @return
	 */
	public boolean isSubClass(OWLClassExpression normalizedSub, 
				OWLClassExpression normalizedSup);
	/**
	 *  returns wether sub is a sub property of sup or is equivalent to sup
	 * @param normalizedSub
	 * @param normalizedSup
	 * @return
	 */
	
	public boolean isSubProperty(OWLPropertyExpression normalizedSub, 
			OWLPropertyExpression normalizedSup);
	/**
	 * returns subsumees and equivalent class expressions
	 * @param sup
	 * @return
	 */
	public Set<OWLClassExpression> getAllSubsumees(OWLClassExpression sup);
	/**
	 * returns subsumers and equivalent class expressions
	 * @param sup
	 * @return
	 */
	public Set<OWLClassExpression> getAllSubsumers(OWLClassExpression sub);
	/**
	 * returns subsumees and equivalent class expressions
	 * @param sup
	 * @return
	 */
	public Set<OWLPropertyExpression> getAllSubproperties(OWLPropertyExpression sup);
		/**
		 * computes the most general subsumees 
		 * @param classes
		 * @param properties
		 * @return
		 */
	public Set<OWLClassExpression> getMostGeneralSubsumees(Set<OWLClassExpression> classes, Set<OWLPropertyExpression> properties);
	/**
	 * computes the most general subsumees. 
	 * This is a generalization of the concept of most general subsumees.
	 * First some definitions:
	 * <ol>
	 *   <li> A ConceptPropertyWildcard represents either an atomic concept, an atomic property or the inverse of an atomic property. </li>
	 *   <li> A PropertyWildcard represents either an atomic property or the inverse of an atomic property.
	 *   <li> A ConceptWildcard represents an atomic concept </li>
	 * </ol>
	 * Let C be the set of atomic concept in the Tbox, and P be the set of atomic properties and inverses of atomic properties.
	 * Let n = conceptPropertyWildcards, m = propertyWildcards, l = conceptWildcards, 
	 *  Q = (C union P)^n * P^m * C^l    ( * : cartesian product)
	 *   
	 *  The returned value is UNION{ q in Q} getMostGeneralSubsumees(elements(q) union classes union properties)
	 * 
	 * @param classes atomic classes
	 * @param properties properties or inverse properties
	 * @param conceptPropertyWildcards number of ConceptRoleWildCards.
	 * @param propertyWildcards. 
	 * @param conceptWildcards
	 * @return
	 */
	public Set<OWLClassExpression> getMostGeneralSubsumees(Set<OWLClassExpression> classes, Set<OWLPropertyExpression> properties, 
			int conceptPropertyWildcards, int propertyWildcards, int conceptWildcards);
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
			int minimalNumberOfConceptWildcardsToReport);
	
	public Set<OWLClassExpression> applyPropertyWildcard(Set<OWLClassExpression> mostGeneralSubmees) ;
	
	public Set<OWLClassExpression> getUnsatisfiableClassExpressions();
	public Set<OWLPropertyExpression> getUnsatisfiablePropertyExpressions();
	
	
	public Set<OWLObjectInverseOf> getInverses();
	public Set<OWLObjectProperty> getObjectProperties();
	public Set<OWLDataProperty> getDataProperties();
	public NormalizedOWLQLTbox getTbox();
	
	
}
