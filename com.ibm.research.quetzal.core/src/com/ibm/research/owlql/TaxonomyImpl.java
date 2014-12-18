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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLQuantifiedRestriction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.wala.util.graph.Graph;

public class TaxonomyImpl implements Taxonomy {

	private static final Logger logger = LoggerFactory.getLogger(TaxonomyImpl.class);
	
	public static TaxonomyBuilder<OWLClassExpression>  buildClassHierarchy(NormalizedOWLQLTbox tbox, Collection<OWLClassExpression> additionalClassExpression) {
		OWLOntology ont = tbox.getNormalizedOntology();
		OWLDataFactory fac = ont.getOWLOntologyManager().getOWLDataFactory();
		Set<OWLClassExpression> classes;
		if (additionalClassExpression!=null) {
			classes = new HashSet<OWLClassExpression>(additionalClassExpression);
			classes.addAll(ont.getClassesInSignature());
		} else {
			classes = new HashSet<OWLClassExpression>(ont.getClassesInSignature());
		}
		SubClassComputation subcomp = new SubClassComputation(tbox);
		TaxonomyBuilder<OWLClassExpression> taxoBuilder = new TaxonomyBuilder<OWLClassExpression>(
				classes, 
				fac.getOWLThing(),
				fac.getOWLNothing(),
				subcomp);
		taxoBuilder.build();
		logger.debug("Number of direct subsumption tests performed: {}", subcomp.numOfDirectSubsumptionTests);
		long size = classes.size();
		logger.debug("Worst Case number of direct subsumption tests performed: {}^2 = {}",size, size*size);
		return taxoBuilder;
	}
	
	public static  TaxonomyBuilder<OWLPropertyExpression> buildObjectPropertyHierarchy(NormalizedOWLQLTbox tbox, Collection<? extends OWLPropertyExpression> additionalPropertyExpressions) {
		OWLOntology ont = tbox.getNormalizedOntology();
		OWLDataFactory fac = ont.getOWLOntologyManager().getOWLDataFactory();
		Set< OWLPropertyExpression> props;
		if (additionalPropertyExpressions!=null) {
			props = new HashSet<OWLPropertyExpression>(additionalPropertyExpressions);
			props.addAll(ont.getObjectPropertiesInSignature());
		} else {
			props = new HashSet<OWLPropertyExpression>( ont.getObjectPropertiesInSignature());
		}
		SubPropertyComputation subcomp = new SubPropertyComputation(tbox);
		TaxonomyBuilder<OWLPropertyExpression> taxoBuilder = new TaxonomyBuilder<OWLPropertyExpression>(
				props, 
				fac.getOWLTopObjectProperty(),
				fac.getOWLBottomObjectProperty(),
				subcomp);
		taxoBuilder.build();
		logger.debug("Number of direct subsumption tests performed: {}", subcomp.numOfDirectSubsumptionTests);
		long size = props.size(); 
		logger.debug("Worst Case number of direct subsumption tests performed: {}^2 = {}",size, size*size);
		return taxoBuilder;
	}
	
	
	public static  TaxonomyBuilder<OWLPropertyExpression> buildDataPropertyHierarchy(NormalizedOWLQLTbox tbox, Collection<? extends OWLPropertyExpression> additionalPropertyExpressions) {
		OWLOntology ont = tbox.getNormalizedOntology();
		OWLDataFactory fac = ont.getOWLOntologyManager().getOWLDataFactory();
		Set< OWLPropertyExpression> props;
		if (additionalPropertyExpressions!=null) {
			props = new HashSet<OWLPropertyExpression>(additionalPropertyExpressions);
			props.addAll(ont.getDataPropertiesInSignature());
		} else {
			props = new HashSet<OWLPropertyExpression>( ont.getDataPropertiesInSignature());
		}
		SubPropertyComputation subcomp = new SubPropertyComputation(tbox);
		TaxonomyBuilder<OWLPropertyExpression> taxoBuilder = new TaxonomyBuilder<OWLPropertyExpression>(
				props, 
				fac.getOWLTopDataProperty(),
				fac.getOWLBottomDataProperty(),
				subcomp);
		taxoBuilder.build();
		logger.debug("Number of direct subsumption tests performed: {}", subcomp.numOfDirectSubsumptionTests);
		long size = props.size(); 
		logger.debug("Worst Case number of direct subsumption tests performed: {}^2 = {}",size, size*size);
		return taxoBuilder;
	}	
	
	public static void addImpliedTaxonomy(OWLOntology ont) {
		NormalizedOWLQLTbox tbox = new NormalizedOWLQLTbox(ont);
		//build taxonomies
		logger.debug("building class taxonomy ...");
		TaxonomyBuilder<OWLClassExpression>  classTaxo = buildClassHierarchy(tbox, null);
		logger.debug("building object property taxonomy ...");
		TaxonomyBuilder<OWLPropertyExpression> opTaxo = buildObjectPropertyHierarchy(tbox, null);
		logger.debug("building data property taxonomy ...");
		TaxonomyBuilder<OWLPropertyExpression> dpTaxo = buildDataPropertyHierarchy(tbox, null);
		logger.debug("All taxonomies built!");
		//
		OWLDataFactory fac = ont.getOWLOntologyManager().getOWLDataFactory();
		for (TaxonomyBuilder<OWLClassExpression>.TaxoNode node : classTaxo.getNodes()) {
			Set<? extends OWLClassExpression> equivs =node.getEquivalentElements();
			if (equivs.size() > 1) {
				ont.getOWLOntologyManager().addAxiom(ont,fac.getOWLEquivalentClassesAxiom(equivs) );
			}
			OWLClassExpression rep = node.getRepresentative();
			assert rep!=null: node;
			for (Iterator<? extends TaxonomyBuilder<OWLClassExpression>.TaxoNode> it =  node.getSubs(); it.hasNext();) {
				TaxonomyBuilder<OWLClassExpression>.TaxoNode subNode = it.next();
				for (OWLClassExpression subclass: subNode.getEquivalentElements()) {
					ont.getOWLOntologyManager().addAxiom(ont,fac.getOWLSubClassOfAxiom(subclass, rep));
				}
			}
		}
		
		for (TaxonomyBuilder<OWLPropertyExpression>.TaxoNode node : opTaxo.getNodes()) {
			Set<? extends OWLPropertyExpression> equivs =node.getEquivalentElements();
			if (equivs.size() > 1) {
				Set<OWLObjectPropertyExpression> s =new HashSet<OWLObjectPropertyExpression>();
				for (OWLPropertyExpression p: equivs) {
					s.add((OWLObjectPropertyExpression) p);
				}
				ont.getOWLOntologyManager().addAxiom(ont,fac.getOWLEquivalentObjectPropertiesAxiom(s));
			}
			OWLObjectPropertyExpression  rep = (OWLObjectPropertyExpression) node.getRepresentative();
			
			assert rep!=null : node;
			for (Iterator<? extends TaxonomyBuilder<OWLPropertyExpression>.TaxoNode> it =  node.getSubs(); it.hasNext();) {
				TaxonomyBuilder<OWLPropertyExpression>.TaxoNode subNode = it.next();
				for (OWLPropertyExpression subprop: subNode.getEquivalentElements()) {
					ont.getOWLOntologyManager().addAxiom(ont,fac.getOWLSubObjectPropertyOfAxiom((OWLObjectPropertyExpression) subprop, rep));
				}
			}
			
		}
		
		for (TaxonomyBuilder<OWLPropertyExpression>.TaxoNode node : dpTaxo.getNodes()) {
			Set<? extends OWLPropertyExpression> equivs =node.getEquivalentElements();
			if (equivs.size() > 1) {
				Set<OWLDataPropertyExpression> s =new HashSet<OWLDataPropertyExpression>();
				for (OWLPropertyExpression p: equivs) {
					s.add((OWLDataPropertyExpression) p);
				}
				ont.getOWLOntologyManager().addAxiom(ont,fac.getOWLEquivalentDataPropertiesAxiom(s) );
			}
			OWLDataPropertyExpression  rep = (OWLDataPropertyExpression) node.getRepresentative();
			
			assert rep!=null : node;
			for (Iterator<? extends TaxonomyBuilder<OWLPropertyExpression>.TaxoNode> it =  node.getSubs(); it.hasNext();) {
				TaxonomyBuilder<OWLPropertyExpression>.TaxoNode subNode = it.next();
				for (OWLPropertyExpression subprop: subNode.getEquivalentElements()) {
						ont.getOWLOntologyManager().addAxiom(ont,fac.getOWLSubDataPropertyOfAxiom((OWLDataPropertyExpression) subprop, rep));
				}
			}
				
			
		}
		//ont.getOWLOntologyManager().addAxiom(ont, );
	}
	
	
	
	protected NormalizedOWLQLTbox tbox;
	protected TaxonomyBuilder<OWLPropertyExpression> objPropTaxo;
	protected TaxonomyBuilder<OWLPropertyExpression> dataPropTaxo;
	protected TaxonomyBuilder<OWLClassExpression> classTaxo;
	
	protected Set<OWLObjectProperty> oprops;
	protected Set<OWLObjectInverseOf> invprops;
	protected Set<OWLDataProperty> dprops;
	
	protected Set<OWLClassExpression> additionalClassExpressions;
	protected Set<OWLObjectPropertyExpression> additionalObjectPropertyExpressions;
	protected Set<OWLDataPropertyExpression> additionalDataPropertyExpressions;
	protected OWLDataFactory fac;
	//protected PelletReasoner pellet;
	
	public TaxonomyImpl(NormalizedOWLQLTbox tbox) throws OWLOntologyCreationException {
		super();
		this.tbox = tbox;
		oprops = tbox.getNormalizedOntology().getObjectPropertiesInSignature();
		dprops = tbox.getNormalizedOntology().getDataPropertiesInSignature();
		additionalClassExpressions = new HashSet<OWLClassExpression>();
		additionalObjectPropertyExpressions = new HashSet<OWLObjectPropertyExpression>();
		//Set<OWLDataPropertyExpression> additionalDataPropertyExpressions = new HashSet<OWLDataPropertyExpression>();
		invprops = new HashSet<OWLObjectInverseOf>();
		for (OWLObjectProperty p: oprops) {
			invprops.add(tbox.getFactory().getOWLObjectInverseOf(p));
		}
		
		//OWLOntology ont = tbox.getNormalizedOntology();
		Set<OWLProperty> props = new HashSet<OWLProperty>(oprops);
		props.addAll(dprops);
		//OWLQLNormalizer newNorm = tbox.getNormalizer().copy();
		fac = tbox.getFactory();
		for (OWLProperty p: props) {
			if (p.isOWLTopDataProperty()
			|| p.isOWLTopObjectProperty()
			|| p.isOWLBottomDataProperty()
			|| p.isOWLBottomObjectProperty()) {
				continue;
			}
			// add NC = some(p, top)
			OWLClassExpression existP;
			if (p.isObjectPropertyExpression()) {
				existP= tbox.getFactory().getOWLObjectSomeValuesFrom((OWLObjectProperty) p, tbox.getFactory().getOWLThing());
			} else {
				existP= tbox.getFactory().getOWLDataSomeValuesFrom((OWLDataProperty) p, tbox.getFactory().getTopDatatype());
			}
			additionalClassExpressions.add(existP);
			
			//manager.addAxiom(ont, fac.getOWLEquivalentClassesAxiom(nc, existP));
			
			//
			
			if (p.isObjectPropertyExpression()) {
				// NC' = some(inv(p), top)
				OWLObjectProperty op = (OWLObjectProperty) p;
				OWLClassExpression existInvP = tbox.getFactory().getOWLObjectSomeValuesFrom(op.getInverseProperty().getSimplified(), tbox.getFactory().getOWLThing());
				additionalClassExpressions.add(existInvP);
				
				//manager.addAxiom(ont, fac.getOWLEquivalentClassesAxiom(nc,existInvP));
				//
				
				// np = inv(p) 
				OWLObjectProperty inv = null;
				OWLObjectPropertyExpression invP = tbox.getFactory().getOWLObjectInverseOf(op).getSimplified();
				/*for (OWLObjectPropertyExpression exp: op.getInverses(ont)) {
					if (!exp.getSimplified().isAnonymous()) {
						inv = exp.getSimplified().asOWLObjectProperty();
						ATermAppl invPAterm = ATermUtils.makeTermAppl(inv.getIRI().toString());
						propExp2AtermInTaxon.put(invP,invPAterm);
						aterm2propExp.put(invPAterm, invP)
						break;
					}
				}*/
				if (inv == null) {
					additionalObjectPropertyExpressions.add(invP);
					//manager.addAxiom(ont, fac.getOWLEquivalentObjectPropertiesAxiom(np,invP));
				}
				//
			}
		}
	}
		
		
	

	

	
	@Override
	public void build() {
		//build taxonomies
		logger.debug("building data property taxonomy ...");
		dataPropTaxo = buildDataPropertyHierarchy(tbox, additionalDataPropertyExpressions);
		logger.debug("building object property taxonomy ...");
		objPropTaxo = buildObjectPropertyHierarchy(tbox, additionalObjectPropertyExpressions);
		
		logger.debug("building class taxonomy ...");
		classTaxo = buildClassHierarchy(tbox, additionalClassExpressions);
		
		logger.debug("All taxonomies built!");
		//
		Graph<TaxonomyBuilder<OWLPropertyExpression>.TaxoNode> lattice = objPropTaxo.getLattice();
		logger.debug("{}", lattice);
		
	}

	public boolean isSubClass(OWLClassExpression normalizedSub,
			OWLClassExpression normalizedSup) {
		return classTaxo.isSubsumedBy(normalizedSub, normalizedSup);
	}

	public boolean isSubProperty(OWLPropertyExpression normalizedSub,
			OWLPropertyExpression normalizedSup) {
		if (normalizedSub.isObjectPropertyExpression() ^ normalizedSup.isDataPropertyExpression()) {
			return false;
		}
		if (normalizedSub.isObjectPropertyExpression()) {
			return objPropTaxo.isSubsumedBy(normalizedSub, normalizedSup);
		} else {
			return dataPropTaxo.isSubsumedBy(normalizedSub, normalizedSup);
		}
	}

	public Set<OWLClassExpression> getMostGeneralSubsumees(
			Set<OWLClassExpression> classes,
			Set<OWLPropertyExpression> properties) {
		 Set<OWLClassExpression> existentialRestCandidates = getCommunSubsumees(classes, true);
		 Set<OWLPropertyExpression> roleCandidates = new HashSet<OWLPropertyExpression>();
		 Boolean isDataRole = null;
		 for (OWLClassExpression sub: existentialRestCandidates) {
			 assert sub.getClassExpressionType().equals(ClassExpressionType.DATA_SOME_VALUES_FROM)
			 || sub.getClassExpressionType().equals(ClassExpressionType.OBJECT_SOME_VALUES_FROM): sub;
			 OWLQuantifiedRestriction qSub = (OWLQuantifiedRestriction) sub;
			 OWLPropertyExpression subRole =  qSub.getProperty();
			 assert subRole!=null : "Unkown role : "+ qSub; 
			 roleCandidates.add(subRole);
			 isDataRole = subRole.isDataPropertyExpression();
		 }
		 if (roleCandidates.isEmpty()) {
			 return Collections.EMPTY_SET;
		 }
		 roleCandidates.retainAll(getCommunSubproperty(properties));
		 if (roleCandidates.isEmpty()) {
			 return Collections.EMPTY_SET;
		 }
		
		 // check maximality wrt inverse
		 Set<OWLClassExpression> ret = getMaximum(roleCandidates);
		 //
		return ret;
		
	
	}
	
	/**
	 * check maximality wrt inverse
	 * @param roleCandidates
	 * @return
	 */
	private Set<OWLClassExpression> getMaximum(Set<OWLPropertyExpression> roleCandidates) {
		Set<OWLClassExpression> ret = new HashSet<OWLClassExpression>();
		if (roleCandidates.isEmpty()) {
			return new HashSet<OWLClassExpression>();
		}
		boolean isDataRole = roleCandidates.iterator().next().isDataPropertyExpression();
		 // 
		 if (isDataRole) {
			  for (OWLPropertyExpression candR: roleCandidates ) {
				 assert candR.isDataPropertyExpression(): candR;
				 boolean acceptCandR = true;
				 for (OWLPropertyExpression sub: roleCandidates) {
					 if (candR == sub) {
						 continue;
					 }
					 if (dataPropTaxo.isSubsumedBy(candR, sub)
					 && !dataPropTaxo.isSubsumedBy(sub, candR)) {
						 // candR is a strict sub property of sub
						 acceptCandR  = false;
						 break;
					 }
				 }
				 if (acceptCandR) {
					// ret.add(ATermUtils.makeSomeValues(candR.getName(), ATermUtils.TOP_DATA_PROPERTY));
					 ret.add(fac.getOWLDataSomeValuesFrom(
							 (OWLDataPropertyExpression) candR, fac.getTopDatatype()));
				 }
			 }
			 
		 } else {
			 for (OWLPropertyExpression candR: roleCandidates ) {
				 boolean acceptCandR = true;
				 for (OWLPropertyExpression sub: roleCandidates) {
					 if (candR == sub) {
						 continue;
					 }
					 OWLObjectPropertyExpression candROWLProp = (OWLObjectPropertyExpression) candR;
					 OWLObjectPropertyExpression subOWLProp = (OWLObjectPropertyExpression) sub;
					 OWLClassExpression someInvCandR  = fac.getOWLObjectSomeValuesFrom(
							 candROWLProp.getInverseProperty().getSimplified(), fac.getOWLThing());
					 OWLClassExpression someInvSub = fac.getOWLObjectSomeValuesFrom(
							 subOWLProp.getInverseProperty().getSimplified(), fac.getOWLThing());
					 if (isSubClass(someInvCandR , someInvSub)
					 && !isSubClass(someInvSub, someInvCandR)) {
						 // candR is a strict sub property of sub
						 acceptCandR  = false;
						 break;
					 }
				 }
				 if (acceptCandR) {
					// ret.add(ATermUtils.makeSomeValues(candR.getName(), ATermUtils.TOP));
					 ret.add(fac.getOWLObjectSomeValuesFrom(
							 (OWLObjectPropertyExpression)  candR, fac.getOWLThing()));
				 }
			 }
		 }
		 return ret;
	}


	

	protected Set<OWLPropertyExpression> getCommunSubproperty(Set<OWLPropertyExpression> props) {
		Set<OWLPropertyExpression> communSubsumees = null;
		
		for (OWLPropertyExpression propRole: props) {
			Set<OWLPropertyExpression> subs = new HashSet<OWLPropertyExpression>
					(propRole.isDataPropertyExpression()? dataPropTaxo.getSubsumeesOrSelf(propRole)
					: objPropTaxo.getSubsumeesOrSelf(propRole));
			if (propRole.isDataPropertyExpression()) {
				subs.removeAll(dataPropTaxo.getBottomNode().getEquivalentElements());
			} else {
				assert propRole.isObjectPropertyExpression() : propRole;
				subs.removeAll(objPropTaxo.getBottomNode().getEquivalentElements());
			}
			
			if (communSubsumees == null) {
				communSubsumees = new HashSet<OWLPropertyExpression>();
				communSubsumees.addAll(subs);
			} else {
				communSubsumees.retainAll(subs);
			}
			
			if (communSubsumees.isEmpty()) {
				return Collections.EMPTY_SET;
			}
		}
		return communSubsumees == null? new HashSet<OWLPropertyExpression>(): communSubsumees;
	}
	
	protected void retainOnlyExistentialRestrictions(Set<OWLClassExpression>  cs) {
		//Set<OWLClassExpression> add = new HashSet<OWLClassExpression>();
		for (Iterator<OWLClassExpression> it = cs.iterator(); it.hasNext();) {
			OWLClassExpression c = it.next();
			if (!c.getClassExpressionType().equals(ClassExpressionType.OBJECT_SOME_VALUES_FROM)
			&& !c.getClassExpressionType().equals(ClassExpressionType.DATA_SOME_VALUES_FROM)) {
				it.remove();
				/*c = aterm2complexAtermEq.get(c);
				if (c!=null && ATermUtils.isSomeValues(c)) {
					add.add(c);
				}*/
			}
			
		}
		//cs.addAll(add);
	}
	protected Set<OWLClassExpression> getCommunSubsumees(
			Set<OWLClassExpression> classes, boolean onlyExistialRestrictions) {
		
		Set<OWLClassExpression> communSubsumees = null;
		for (OWLClassExpression c: classes) {
			Set<OWLClassExpression> subs = classTaxo.getSubsumeesOrSelf(c);
			assert subs!=null : "Not found in class taxonomy: "+c;
			subs.removeAll(classTaxo.getBottomNode().getEquivalentElements());
			if (onlyExistialRestrictions) {
				retainOnlyExistentialRestrictions(subs);
			}
			if (communSubsumees == null) {
				communSubsumees = new HashSet<OWLClassExpression>();
				communSubsumees.addAll(subs);
			} else {
				communSubsumees.retainAll(subs);
			}
			
			if (communSubsumees.isEmpty()) {
				return Collections.EMPTY_SET;
			}
		}
		return communSubsumees == null? new HashSet<OWLClassExpression>() :communSubsumees;
	}
	
	
	
	

	public Set<OWLPropertyExpression> getAllSubproperties(
			OWLPropertyExpression roleSup) {
		Set<OWLPropertyExpression> subs = new HashSet<OWLPropertyExpression>();
		for (OWLPropertyExpression sub : 
				roleSup.isDataPropertyExpression()? dataPropTaxo.getSubsumeesOrSelf(roleSup)
				: objPropTaxo.getSubsumeesOrSelf(roleSup)) {
			if (!sub.isAnonymous()) {
				subs.add(sub); 
			} else {
				//OWLObjectProperty op = ((OWLObjectPropertyExpression) c).getNamedProperty(); //fac.getOWLObjectProperty(IRI.create(sub.getInverse().getName().getAFun().getName()));
				//if (oprops.contains(op))
				{
					subs.add(sub);
				} 
				//else {
					// do nothing: inverse of a create role R, where R is defined as equivalent to the inverse of an existing role
				//}
			}
		}
		subs.remove(dataPropTaxo.getBottom());
		subs.remove(objPropTaxo.getBottom());
		subs.remove(fac.getOWLObjectInverseOf((OWLObjectPropertyExpression) objPropTaxo.getBottom()).getSimplified());
	
		return subs;
	}

	public Set<OWLClassExpression> getAllSubsumees(OWLClassExpression sup) {
		Set<OWLClassExpression> ret = new HashSet<OWLClassExpression>();
		ret =  new HashSet<OWLClassExpression>(classTaxo.getSubsumeesOrSelf(sup));
		ret.remove(classTaxo.getBottom());
		return ret;
	}
	public Set<OWLClassExpression> getAllSubsumers(OWLClassExpression sub) {
		Set<OWLClassExpression> ret = new HashSet<OWLClassExpression>();
		ret =  new HashSet<OWLClassExpression>(classTaxo.getSubsumersOrSelf(sub));
		ret.remove(classTaxo.getTop());
		return ret;
	}

	public Set<OWLClassExpression> getUnsatisfiableClassExpressions() {
		
		Set<OWLClassExpression> ret = new HashSet<OWLClassExpression>(classTaxo.getBottomNode().getEquivalentElements());
		ret.remove(classTaxo.getBottom());
		return ret;
	}

	public Set<OWLPropertyExpression> getUnsatisfiablePropertyExpressions() {
		Set<OWLPropertyExpression> ret = new HashSet<OWLPropertyExpression>();
		ret.addAll(dataPropTaxo.getBottomNode().getEquivalentElements());
		ret.remove(dataPropTaxo.getBottom());
		/*for (OWLPropertyExpression c : objPropTaxo.getBottomNode().getEquivalentElements()) {
			if (!c.isAnonymous()) {
				ret.add(c); 
			} else {
				//OWLObjectProperty op =  ((OWLObjectPropertyExpression) c).getNamedProperty();//fac.getOWLObjectProperty(IRI.create(ATermUtils.makeInv(c).getAFun().getName()));
				//if (oprops.contains(op)) 
				{
					ret.add(c);
				} 
				//else {
					// do nothing: inverse of a create role R, where R is defined as equivalent to the inverse of an existing role
				//}
			}
		}*/
		ret.addAll(objPropTaxo.getBottomNode().getEquivalentElements());
		ret.remove(objPropTaxo.getBottom());
		ret.remove(fac.getOWLObjectInverseOf((OWLObjectPropertyExpression) objPropTaxo.getBottom()).getSimplified());
		return ret;
	}
	public Set<OWLObjectInverseOf> getInverses() {
		return invprops;
	}
	public Set<OWLObjectProperty> getObjectProperties() {
		return oprops;
	}
	public Set<OWLDataProperty> getDataProperties() {
		return dprops;
	}
	public NormalizedOWLQLTbox getTbox() { 
		return tbox;
	}
	
	
	
	///
	
	public Set<OWLClassExpression> applyPropertyWildcard(Set<OWLClassExpression> mostGeneralSubmees) {
		Set<OWLClassExpression> ret = new HashSet<OWLClassExpression>();
		if (mostGeneralSubmees.isEmpty()) {
			for (OWLObjectProperty p : oprops) {
				ret.add(tbox.getFactory().getOWLObjectSomeValuesFrom(p, tbox.getFactory().getOWLThing()));
			}
			for (OWLObjectInverseOf p : invprops) {
				ret.add(tbox.getFactory().getOWLObjectSomeValuesFrom(p, tbox.getFactory().getOWLThing()));
			}
			for (OWLDataProperty p : dprops) {
				ret.add(tbox.getFactory().getOWLDataSomeValuesFrom(p, tbox.getFactory().getTopDatatype()));
			}
			
		} else {
			//get all subsumees of ret
			Set<OWLClassExpression> subsumees = new HashSet<OWLClassExpression>();
			for (OWLClassExpression c: mostGeneralSubmees) {
				assert c  instanceof OWLQuantifiedRestriction : c;
				OWLPropertyExpression p = ((OWLQuantifiedRestriction) c).getProperty();
				subsumees.add(c);
				for (OWLPropertyExpression subP : getAllSubproperties(p)) {
					if (subP.isObjectPropertyExpression()) {
						subsumees.add(tbox.getFactory().getOWLObjectSomeValuesFrom((OWLObjectPropertyExpression) subP, tbox.getFactory().getOWLThing()));
					} else {
						assert subP.isDataPropertyExpression() : subP;
						subsumees.add(tbox.getFactory().getOWLDataSomeValuesFrom((OWLDataPropertyExpression) subP, tbox.getFactory().getTopDatatype()));
						
					}
				}
			}
			ret = subsumees;
		}
		// remove unsatisfiable classes
		ret.removeAll(getUnsatisfiableClassExpressions());
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
			mandatoryPart = getMostGeneralSubsumees(classes, properties);
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
			Set<OWLClassExpression> remainingClasses = new HashSet<OWLClassExpression>(tbox.getNormalizedOntology().getClassesInSignature());
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
	
	@Override
	public Set<OWLClassExpression> getMostGeneralSubsumees(
			Set<OWLClassExpression> classes,
			Set<OWLPropertyExpression> properties,
			int conceptPropertyWildcards, int propertyWildcards,
			int conceptWildcards) {
		
		Set<OWLClassExpression> mandatoryPart = new HashSet<OWLClassExpression>();
		if (!classes.isEmpty() || !properties.isEmpty() ) {
			mandatoryPart = getMostGeneralSubsumees(classes, properties);
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
			Set<OWLClassExpression> remainingClasses = new HashSet<OWLClassExpression>(tbox.getNormalizedOntology().getClassesInSignature());
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
				for (OWLObjectProperty p : oprops) {
					ret.add(tbox.getFactory().getOWLObjectSomeValuesFrom(p, tbox.getFactory().getOWLThing()));
				}
				for (OWLObjectInverseOf p : invprops) {
					ret.add(tbox.getFactory().getOWLObjectSomeValuesFrom(p, tbox.getFactory().getOWLThing()));
				}
				for (OWLDataProperty p : dprops) {
					ret.add(tbox.getFactory().getOWLDataSomeValuesFrom(p, tbox.getFactory().getTopDatatype()));
				}
				
			} else {
				//get all subsumees of ret
				Set<OWLClassExpression> subsumees = new HashSet<OWLClassExpression>();
				for (OWLClassExpression c: ret) {
					assert c  instanceof OWLQuantifiedRestriction : c;
					OWLPropertyExpression p = ((OWLQuantifiedRestriction) c).getProperty();
					subsumees.add(c);
					for (OWLPropertyExpression subP : getAllSubproperties(p)) {
						if (subP.isObjectPropertyExpression()) {
							subsumees.add(tbox.getFactory().getOWLObjectSomeValuesFrom((OWLObjectPropertyExpression) subP, tbox.getFactory().getOWLThing()));
						} else {
							assert subP.isDataPropertyExpression() : subP;
							subsumees.add(tbox.getFactory().getOWLDataSomeValuesFrom((OWLDataPropertyExpression) subP, tbox.getFactory().getTopDatatype()));
							
						}
					}
				}
				ret = subsumees;
			}
			// remove unsatisfiable classes
			ret.removeAll(getUnsatisfiableClassExpressions());
			//
		}
		return ret;
	}
	
	
	
	

}
