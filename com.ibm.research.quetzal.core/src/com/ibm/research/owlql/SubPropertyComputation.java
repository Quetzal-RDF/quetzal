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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubPropertyAxiom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.research.owlql.TaxonomyBuilder.SubsumptionComputation;
import com.ibm.research.utils.OCUtils;
import com.ibm.wala.util.graph.Graph;

public class SubPropertyComputation implements SubsumptionComputation<OWLPropertyExpression> {
	private static final Logger logger = LoggerFactory.getLogger(SubPropertyComputation.class);

	

	public static void main(String[] args) throws Exception {
		OWLOntology ont = OCUtils.load(args);
		OWLDataFactory fac = ont.getOWLOntologyManager().getOWLDataFactory();
		NormalizedOWLQLTbox tbox = new NormalizedOWLQLTbox(ont);
		SubPropertyComputation subcomp = new SubPropertyComputation(tbox);
		logger.info("Properties:\n");
		for (OWLObjectProperty op : ont.getObjectPropertiesInSignature()) {
			System.out.println(op);
		}
		assert tbox.getNormalizedAxioms().equals(new NormalizedOWLQLTbox(tbox.getNormalizedOntology()).getNormalizedAxioms());
		/*boolean test = subcomp.isSubumedBy(
				fac.getOWLObjectProperty(IRI.create("http://semantics.crl.ibm.com/univ-bench-dl.owl#isStudentOf")),
				fac.getOWLObjectProperty(IRI.create("http://semantics.crl.ibm.com/univ-bench-dl.owl#isMemberOf")));
		logger.info("univ#isStudentOf is subproperty of univ#isMemberOf: "+test);
		*/
		
		TaxonomyBuilder<OWLPropertyExpression> taxoBuilder = new TaxonomyBuilder<OWLPropertyExpression>(
				ont.getObjectPropertiesInSignature(), 
				fac.getOWLTopObjectProperty(),
				fac.getOWLBottomObjectProperty(),
				subcomp);
		taxoBuilder.build();
		Graph<TaxonomyBuilder<OWLPropertyExpression>.TaxoNode> lattice = taxoBuilder.getLattice();

		logger.info("{}", lattice);
		logger.info("Number of direct subsumption tests performed: {}", subcomp.numOfDirectSubsumptionTests);
		long size = ont.getObjectPropertiesInSignature().size(); 
		logger.info("Worst Case number of direct subsumption tests performed: {}^2 = {}",size, size*size);
	
		
	}
	private NormalizedOWLQLTbox tbox;
	protected int numOfDirectSubsumptionTests;
	public SubPropertyComputation(NormalizedOWLQLTbox tbox) {
		super();
		this.tbox = tbox;
		numOfDirectSubsumptionTests =0;
	}

	@Override
	public boolean isSubumedBy(OWLPropertyExpression sub,
			OWLPropertyExpression sup) {
		numOfDirectSubsumptionTests++;
		return tbox.isSubProperty(sub, sup);
	}

	@Override
	public Set<OWLPropertyExpression> getToldSubsumers(
			OWLPropertyExpression sub) {
		if (!sub.isAnonymous()) {
			Set<OWLPropertyExpression> ret = new HashSet<OWLPropertyExpression>();
			Set<? extends OWLSubPropertyAxiom> axioms;// = new HashSet<? extends OWLSubPropertyAxiom>();
			if (sub.isObjectPropertyExpression()) {
				axioms = tbox.getNormalizedOntology().getObjectSubPropertyAxiomsForSubProperty(
						(OWLObjectPropertyExpression) sub);
			} else if (!sub.isAnonymous()) {
				axioms = tbox.getNormalizedOntology().getDataSubPropertyAxiomsForSubProperty(
						((OWLDataPropertyExpression) sub).asOWLDataProperty());
			} else {
				axioms = Collections.EMPTY_SET;
			}
			if (axioms!=null) {
				for (OWLSubPropertyAxiom ax: axioms) {
					assert ax.getSubProperty().equals(sub) : ax+"\n"+sub;
					ret.add(ax.getSuperProperty());
				}
			}
			return ret;
		}
		return Collections.EMPTY_SET;
	}
	
	
}
