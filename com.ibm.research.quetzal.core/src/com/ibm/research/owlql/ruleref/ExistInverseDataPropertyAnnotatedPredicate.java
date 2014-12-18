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
