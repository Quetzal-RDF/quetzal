/*******************************************************************************
* Licensed Materials - Property of IBM
* (c) Copyright IBM Corporation 2011, 2013. All Rights Reserved.
*
* Note to U.S. Government Users Restricted Rights:
* Use, duplication or disclosure restricted by GSA ADP Schedule
* Contract with IBM Corp.
*******************************************************************************/
package com.ibm.research.owlql.ruleref;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;

public class ExistInverseDataPropertyAnnotatedPredicate extends
		DLAnnotatedPredicate {

	protected OWLDataProperty prop;
	protected OWLDataFactory fac;
	public ExistInverseDataPropertyAnnotatedPredicate(
			OWLDataProperty prop, OWLDataFactory fac) {
		super(fac.getOWLObjectSomeValuesFrom( fac.getOWLObjectProperty(prop.getIRI()).getInverseProperty().getSimplified(), fac.getOWLThing()));
		this.prop = prop;
		this.fac = fac;
	}

	public DLAnnotatedPredicate clone() {
		return new ExistInverseDataPropertyAnnotatedPredicate(prop, fac);
	}

	public OWLDataProperty getProperty() {
		return prop;
	}
}
