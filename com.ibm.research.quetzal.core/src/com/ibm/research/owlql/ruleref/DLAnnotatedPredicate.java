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

import java.io.IOException;
import java.io.StringWriter;

import org.coode.owlapi.functionalrenderer.OWLObjectRenderer;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectVisitor;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLPropertyExpression;

import com.ibm.research.owlql.rule.Predicate;

/**
 * A predicate annotated with a DL role or concept expression.
 * @author achille
 *
 */
public class DLAnnotatedPredicate extends Predicate {

	public static final String NAME_PREFIX ="P";
	protected OWLClassExpression classAnnotation;
	protected OWLPropertyExpression propAnnotation;
	protected static OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	
	private static String getName(int arity, OWLObject annotation)  {
		//OWLFunctionalSyntaxRenderer render = new OWLFunctionalSyntaxRenderer( )
		try {
			StringWriter w = new StringWriter();
			OWLObjectVisitor ren = new OWLObjectRenderer(manager, manager.createOntology(), w);
			annotation.accept(ren);
			w.flush();
			String ret = NAME_PREFIX +"["+arity+", " +w.toString()+"]";
			w.close();
			return ret;
		} catch (OWLOntologyCreationException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	public DLAnnotatedPredicate(OWLClassExpression classAnnotation) {
		this(classAnnotation, false);
	}
	
	public DLAnnotatedPredicate(OWLClassExpression classAnnotation, boolean zeroPredicate) {
		super(getName(zeroPredicate? 0 :1, classAnnotation), zeroPredicate? 0 :1);
		this.classAnnotation = classAnnotation;
	}
	public DLAnnotatedPredicate( OWLPropertyExpression propAnnotation) {
		this(propAnnotation, false);
	}
	public DLAnnotatedPredicate( OWLPropertyExpression propAnnotation, boolean zeroPredicate) {
		super(getName(zeroPredicate? 0 :2,
				propAnnotation = (propAnnotation instanceof OWLObjectPropertyExpression? ((OWLObjectPropertyExpression) propAnnotation).getSimplified(): propAnnotation)),
				zeroPredicate? 0 :2);
		this.propAnnotation = propAnnotation;
	}
	
	public boolean hasClassAnnotation() {
		return classAnnotation!=null;
	}
	public OWLClassExpression getClassAnnotation() {
		return classAnnotation;
	}
	
	public boolean hasPropertyAnnotation() {
		return propAnnotation!=null;
	}
	public OWLPropertyExpression getPropertyAnnotation() {
		return propAnnotation;
	}
	public DLAnnotatedPredicate clone() {
		return classAnnotation!=null?new DLAnnotatedPredicate(classAnnotation, getArity()==0)
				: new DLAnnotatedPredicate( propAnnotation);
	}
	public DLAnnotatedPredicate decreaseArity() {
		if (getArity()==0) {
			throw new RuntimeException("Arity already at 0, and cannot be decreased further.");
		}
		DLAnnotatedPredicate ret = clone();
		ret.arity--;
		return ret;
		
	}
	public OWLObject getAnnotation() {
		return classAnnotation!=null? classAnnotation: propAnnotation;
	}
	Predicate cloneWithNonZeroArity() {
		return clone();
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((classAnnotation == null) ? 0 : classAnnotation.hashCode());
		result = prime * result
				+ ((propAnnotation == null) ? 0 : propAnnotation.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		DLAnnotatedPredicate other = (DLAnnotatedPredicate) obj;
		if (classAnnotation == null) {
			if (other.classAnnotation != null)
				return false;
		} else if (!classAnnotation.equals(other.classAnnotation))
			return false;
		if (propAnnotation == null) {
			if (other.propAnnotation != null)
				return false;
		} else if (!propAnnotation.equals(other.propAnnotation))
			return false;
		return true;
	}
	
}
