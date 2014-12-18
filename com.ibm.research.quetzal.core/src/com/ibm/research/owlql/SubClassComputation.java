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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.research.owlql.TaxonomyBuilder.SubsumptionComputation;
import com.ibm.research.utils.OCUtils;
import com.ibm.wala.util.graph.Graph;

public class SubClassComputation implements SubsumptionComputation<OWLClassExpression> {
	private static final Logger logger = LoggerFactory.getLogger(SubClassComputation.class);

	public static void main(String[] args) throws Exception {
		OWLOntology ont = OCUtils.load(args);
		OWLDataFactory fac = ont.getOWLOntologyManager().getOWLDataFactory();
		NormalizedOWLQLTbox tbox = new NormalizedOWLQLTbox(ont);
		SubClassComputation subcomp = new SubClassComputation(tbox);
		logger.info("Classes:\n");
		for (OWLClass c : ont.getClassesInSignature()) {
			System.out.println(c);
		}
		assert tbox.getNormalizedAxioms().equals(new NormalizedOWLQLTbox(tbox.getNormalizedOntology()).getNormalizedAxioms());
		/*boolean test = subcomp.isSubumedBy(
				fac.getOWLObjectProperty(IRI.create("http://semantics.crl.ibm.com/univ-bench-dl.owl#isStudentOf")),
				fac.getOWLObjectProperty(IRI.create("http://semantics.crl.ibm.com/univ-bench-dl.owl#isMemberOf")));
		logger.info("univ#isStudentOf is subproperty of univ#isMemberOf: "+test);
		*/
		
		
		TaxonomyBuilder<OWLClassExpression> taxoBuilder = new TaxonomyBuilder<OWLClassExpression>(
				new HashSet<OWLClassExpression>(ont.getClassesInSignature()), 
				fac.getOWLThing(),
				fac.getOWLNothing(),
				subcomp);
		taxoBuilder.build();
		Graph<TaxonomyBuilder<OWLClassExpression>.TaxoNode> lattice = taxoBuilder.getLattice();

		logger.info("{}", lattice);
		logger.info("Number of direct subsumption tests performed: {}", subcomp.numOfDirectSubsumptionTests);
		long size = ont.getClassesInSignature().size();
		logger.info("Worst Case number of direct subsumption tests performed: {}^2 = {}",size, size*size);
		
		
	}
	private NormalizedOWLQLTbox tbox;
	protected int numOfDirectSubsumptionTests;
	
	Map<OWLClassExpression, Set<OWLClassExpression>> sub2ToldSubsumers;
	public SubClassComputation(NormalizedOWLQLTbox tbox) {
		super();
		numOfDirectSubsumptionTests =0;
		this.tbox = tbox;
		sub2ToldSubsumers = new HashMap<OWLClassExpression, Set<OWLClassExpression>>();
		Set<OWLSubClassOfAxiom> axioms = tbox.getNormalizedOntology().getAxioms(AxiomType.SUBCLASS_OF);
		int maxToldSubsumers =0;
		if (axioms!=null) {
			for (OWLSubClassOfAxiom ax: axioms) {
				OWLClassExpression sub = ax.getSubClass();
				OWLClassExpression sup = ax.getSuperClass();
				//if (!sub.isAnonymous()) 
				{
					Set<OWLClassExpression> s = sub2ToldSubsumers.get(sub);
					if (s == null) {
						s = new HashSet<OWLClassExpression>();
						sub2ToldSubsumers.put(sub, s);
					}
					s.add(sup);
					maxToldSubsumers = Math.max(s.size(), maxToldSubsumers);
				}
			}
		}
		logger.debug("Max told subsumers: {}", maxToldSubsumers);
		
	}

	@Override
	public boolean isSubumedBy(OWLClassExpression sub,
			OWLClassExpression sup) {
		numOfDirectSubsumptionTests++;
		return tbox.isSubClass(sub, sup);
	}

	@Override
	public Set<OWLClassExpression> getToldSubsumers(OWLClassExpression sub) {
		/*if (!sub.isAnonymous()) {
			Set<OWLClassExpression> ret = new HashSet<OWLClassExpression>();
			Set<OWLSubClassOfAxiom> axioms = tbox.getNormalizedOntology().getSubClassAxiomsForSubClass(sub.asOWLClass());
			if (axioms!=null) {
				for (OWLSubClassOfAxiom ax: axioms) {
					assert ax.getSubClass().equals(sub) : ax+"\n"+sub;
					ret.add(ax.getSuperClass());
				}
			}
			return ret;
		}*/
		Set<OWLClassExpression> ret = sub2ToldSubsumers.get(sub);
		
		return  ret!=null? ret : Collections.EMPTY_SET;
	}
	
}
