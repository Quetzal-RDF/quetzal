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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLNaryPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLPropertyRange;
import org.semanticweb.owlapi.model.OWLQuantifiedRestriction;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;
/**
 * 
 * @author achille
 *
 */
public class OWLQLNormalizer {

	private static final Logger logger = LoggerFactory.getLogger(OWLQLNormalizer.class);
	public static void main(String[] args) {
		OWLDataFactory fac = new OWLDataFactoryImpl();
		OWLQLNormalizer comp = new OWLQLNormalizer(fac);
		//some(R, A)
		OWLObjectSomeValuesFrom someRA = fac.getOWLObjectSomeValuesFrom(fac.getOWLObjectProperty(IRI.create("http://R")),
					fac.getOWLClass(IRI.create("http://A")));
		//some(S, some(R,A))
		OWLObjectSomeValuesFrom someSsomeRA = fac.getOWLObjectSomeValuesFrom(fac.getOWLObjectProperty(IRI.create("http://S")),
					someRA);
		//subclass(B, someRA)
		OWLSubClassOfAxiom sub1 = fac.getOWLSubClassOfAxiom(fac.getOWLClass(IRI.create("http://B")), someRA);
		Set<OWLAxiom> normalizedSub1 = comp.toQLNormalForm(sub1,null);
		logger.info("axiom: {}", sub1);
		logger.info("\tnormalized axioms:");
		for (OWLAxiom ax: normalizedSub1) {
			logger.info("\t\t{}", ax);
		}
		
		//subclass(B, someSsomeRA)
		OWLSubClassOfAxiom sub2 = fac.getOWLSubClassOfAxiom(fac.getOWLClass(IRI.create("http://B")), someSsomeRA);
		Set<OWLAxiom> normalizedSub2= comp.toQLNormalForm(sub2,null);
		logger.info("axiom: {}",sub2);
		logger.info("\tnormalized axioms:");
		for (OWLAxiom ax: normalizedSub2) {
			logger.info("\t\t{}", ax);
		}
		//
		//subclass(someRA, someSsomeRA)
		OWLSubClassOfAxiom sub3 = fac.getOWLSubClassOfAxiom(someRA, someSsomeRA);
		Set<OWLAxiom> normalizedSub3= comp.toQLNormalForm(sub3,null);
		logger.info("axiom: {}",sub3);
		logger.info("\tnormalized axioms:");
		if (normalizedSub3!=null) {
			for (OWLAxiom ax: normalizedSub3) {
				logger.info("\t\t{}", ax);
			}
		}
	}
	public static boolean isAboxAxiom(OWLAxiom ax) {
		return ax.getAxiomType().equals(AxiomType.DATA_PROPERTY_ASSERTION)
		|| ax.getAxiomType().equals(AxiomType.OBJECT_PROPERTY_ASSERTION)
		|| ax.getAxiomType().equals(AxiomType.CLASS_ASSERTION)
		|| ax.getAxiomType().equals(AxiomType.SAME_INDIVIDUAL)
		|| ax.getAxiomType().equals(AxiomType.DIFFERENT_INDIVIDUALS);
	}
	public static final String NEW_ENTITY_URI_PREFIX ="http://www.research.ibm.com/";
	protected OWLDataFactory fac;
	protected String newClassURIPrefix;
	protected String newRoleURIPrefix;
	protected int newClasses;
	protected int newRoles;
	/**
	 * whether for composite axioms (e.g. equivalence class axioms), sub axioms that can be converted in QL should be converted.
	 * 
	 */
	protected boolean bestEffort;
	
	public OWLQLNormalizer(OWLDataFactory fac) {
		this(fac, NEW_ENTITY_URI_PREFIX+"/newClass_",NEW_ENTITY_URI_PREFIX+"/newRole_");
	}
	public OWLQLNormalizer(OWLDataFactory fac, String newClassURIPrefix,
			String newRoleURIPrefix) {
		this(fac,newClassURIPrefix, newRoleURIPrefix, true);
	}
	public OWLQLNormalizer(OWLDataFactory fac, String newClassURIPrefix,
			String newRoleURIPrefix, boolean bestEffort) {
		super();
		this.fac = fac;
		this.newClassURIPrefix = newClassURIPrefix;
		this.newRoleURIPrefix = newRoleURIPrefix;
		newClasses = newRoles = 0;
		this.bestEffort = bestEffort;
	}
	
	public boolean isGeneratedRole(String iri) {
		return iri.startsWith(newRoleURIPrefix);
	}
	
	public boolean isGeneratedClass(String iri) {
		return iri.startsWith(newClassURIPrefix);
	}
	/**
	 * returns axioms in QL normal form (DL-LiteR):
	 * <ul>
	 * 	<li> subclass(B, C), where 
	 * 		<ul>
	 * 			<li>B --> A | someValuesFrom(R, Top), A is an atomic concept </li>
	 * 			<li>C --> B | not(B) </li>
	 * 		</ul>	
	 *   </li>
	 *  <li> subproperty(R, S), where R and S are atomic properties or inverse of atomic properties</li>
	 *  <li> disjoint(R, S), where R and S are atomic properties or inverse of atomic properties. NOTE: disjoint(R, R) is allowed</li>
	 *  <li> reflexive(R), where R is a reflexive atomic property</li>
	 *  <li> irreflexive(R), where R is an irreflexive atomic property</li>
	 *  </ul>
	 * 
	 * @param axioms list of axioms to normalize in QL form
	 * @param nonQLAxioms nonQLAxioms will be added to this set if it is non-null
	 * @return list of axioms in QL normal form
	 */
	public Set<OWLAxiom> toQLNormalForm(Collection<? extends OWLAxiom> axioms, Collection<OWLAxiom> nonQLAxioms) {
		return toQLNormalForm(axioms, nonQLAxioms, false);
	}
	
	/**
	 * returns axioms in QL normal form (DL-LiteR):
	 * <ul>
	 * 	<li> subclass(B, C), where 
	 * 		<ul>
	 * 			<li>B --> A | someValuesFrom(R, Top), A is an atomic concept </li>
	 * 			<li>C --> B | not(B) </li>
	 * 		</ul>	
	 *   </li>
	 *  <li> subproperty(R, S), where R and S are atomic properties or inverse of atomic properties</li>
	 *  <li> disjoint(R, S), where R and S are atomic properties or inverse of atomic properties. NOTE: disjoint(R, R) is allowed</li>
	 *  <li> reflexive(R), where R is a reflexive atomic property</li>
	 *  <li> irreflexive(R), where R is an irreflexive atomic property</li>
	 *  </ul>
	 * 
	 * @param axioms list of axioms to normalize in QL form
	 * @param nonQLAxioms nonQLAxioms will be added to this set if it is non-null
	 * @return list of axioms in QL normal form
	 */
	protected Set<OWLAxiom> toQLNormalForm(Collection<? extends OWLAxiom> axioms, Collection<OWLAxiom> nonQLAxioms, boolean exitWithNullAtFirstFailure) {
		 Set<OWLAxiom> ret = new HashSet<OWLAxiom>();
		 for (OWLAxiom ax: axioms) {
			Set<OWLAxiom> tp;
			Set<OWLAxiom> tmpNonQLAxioms = new HashSet<OWLAxiom>();
			tp = toQLNormalForm(ax, tmpNonQLAxioms);
			if (!tmpNonQLAxioms.isEmpty()) {
				if (bestEffort) {
					if (tp!= null && nonQLAxioms!=null) {
						nonQLAxioms.addAll(tmpNonQLAxioms);
					}
				} 
			}
			
			if (tp!=null) {
				ret.addAll(tp); 
			} else {
				if (nonQLAxioms!=null) {
					nonQLAxioms.add(ax);
				} 
				if (exitWithNullAtFirstFailure) {
					return null;
				}
			}
		 }
		 return ret;
	}
	
	protected boolean isValidOWLQLRightConcept(OWLClassExpression owlclass) {
		if (isValidOWLQLLeftConcept(owlclass)) {
			return true;
		}
		owlclass = fac.getOWLObjectComplementOf(owlclass).getNNF();
		return isValidOWLQLLeftConcept(owlclass);
	}
	
	protected boolean isValidOWLQLLeftConcept(OWLClassExpression owlclass) {
		// check that subclass is either atomic or someValuesFrom(R, Top)
		if (!owlclass.isAnonymous()) {
			return true;
		}
		if (!owlclass.getClassExpressionType().equals(ClassExpressionType.DATA_SOME_VALUES_FROM)
		&& !owlclass.getClassExpressionType().equals(ClassExpressionType.OBJECT_SOME_VALUES_FROM)) {
			return false;
		}
		OWLQuantifiedRestriction quant = (OWLQuantifiedRestriction) owlclass;
		OWLPropertyRange cl= quant.getFiller();
		if (cl instanceof OWLClassExpression) {
			OWLClassExpression clexp = (OWLClassExpression) cl;
			return clexp.isOWLThing();
		} else if (cl instanceof OWLDataRange) {
			OWLDataRange dr = (OWLDataRange) cl;
			//TODO handle qualified data existential restriction
			return dr.isTopDatatype();
		} else {
			assert false: "Unknown OWLPropertyRange: " +cl;
			return false;
		}
	}
	
	protected OWLClass createNewNamedClass() {
		return fac.getOWLClass(IRI.create(newClassURIPrefix+(newClasses++)));
	}
	protected OWLObjectProperty createNewObjectProperty() {
		return  fac.getOWLObjectProperty(IRI.create(newRoleURIPrefix+(newRoles++)));
	}
	protected OWLDataProperty createNewDataProperty() {
		return  fac.getOWLDataProperty(IRI.create(newRoleURIPrefix+(newRoles++)));
	}
	/** normalize quantified existential to the right
	 * <p> normalize(subclass(B, some(R, C)))  -->
	 * <ul>
	 * 	<li> subclass(B, some(R', Top) </li>
	 *  <li> subprop(R', R)</li>
	 *  <li> normalize(subclass(some(inv(R'), Top), C)) </li>
	 * </ul>
	 * @param ax
	 * @return
	 */
	private Set<OWLAxiom> toQLNormalFormRightExistential(OWLSubClassOfAxiom ax) {
		Set<OWLAxiom> ret = new HashSet<OWLAxiom>();
		OWLClassExpression subclass = ax.getSubClass();
		OWLClassExpression superclass = ax.getSuperClass();
		assert isValidOWLQLLeftConcept(subclass): subclass;
		assert superclass.getClassExpressionType().equals(ClassExpressionType.OBJECT_SOME_VALUES_FROM) : superclass;
		// 
		OWLObjectSomeValuesFrom some = (OWLObjectSomeValuesFrom) superclass;
		OWLObjectPropertyExpression r = some.getProperty();
		OWLClassExpression c = some.getFiller();
		
		OWLObjectProperty rPrime = createNewObjectProperty();
		//subclass(B, some(R', Top) 
		ret.add(fac.getOWLSubClassOfAxiom(subclass, fac.getOWLObjectSomeValuesFrom(rPrime, fac.getOWLThing())));
		//subprop(R', R)
		ret.add(fac.getOWLSubObjectPropertyOfAxiom(rPrime, r));
		//normalize(subclass(some(inv(R'), Top), C))
		OWLSubClassOfAxiom axToNormalize= fac.getOWLSubClassOfAxiom(fac.getOWLObjectSomeValuesFrom(fac.getOWLObjectInverseOf(rPrime).getSimplified(), fac.getOWLThing()), c);
		if (!c.isAnonymous()) {
			ret.add(axToNormalize);
		} else if (c.getClassExpressionType().equals(ClassExpressionType.OBJECT_SOME_VALUES_FROM)) {
			Set<OWLAxiom> tpAxioms = toQLNormalFormRightExistential(axToNormalize);
			if (tpAxioms!=null) {
				ret.addAll(tpAxioms);
			} else {
				return null;
			}
		}  else {
			return null;
		}
		return ret;
		
	}
	public Set<OWLSubObjectPropertyOfAxiom> asSubObjectPropertyOfAxioms(OWLEquivalentObjectPropertiesAxiom eop ) {
		List<OWLObjectPropertyExpression> props = new ArrayList<OWLObjectPropertyExpression>(eop.getProperties());
	    Set<OWLSubObjectPropertyOfAxiom> axs = new HashSet<OWLSubObjectPropertyOfAxiom>();
	    for(int i = 0; i < props.size() - 1; i++) {
	    	for(int j = i + 1; j < props.size(); j++) {
	    		axs.add(fac.getOWLSubObjectPropertyOfAxiom(props.get(i), props.get(j)));
	    		axs.add(fac.getOWLSubObjectPropertyOfAxiom(props.get(j), props.get(i)));
	    	}
	    }
	    return axs;
	}
	protected Set<OWLAxiom> toQLNormalForm(OWLAxiom ax, Set<OWLAxiom> nonQLAxioms) {
		ax = ax.getNNF();
		Set<OWLAxiom> tmpNonQLAxioms = bestEffort? new HashSet<OWLAxiom>(): null;
		Set<OWLAxiom> ret = new HashSet<OWLAxiom>();
		if (!ax.getAxiomType().isLogical()) {
			//ret =  new HashSet<OWLAxiom>();
		} else if (isAboxAxiom(ax)){
			ret.add(ax);
		} else if (ax.getAxiomType().equals(AxiomType.EQUIVALENT_CLASSES)) {
		
			// handle equivalence through reduction to two subclass axioms
			OWLEquivalentClassesAxiom eqAx = (OWLEquivalentClassesAxiom) ax;
			Set<OWLSubClassOfAxiom> subclAxioms = (Set<OWLSubClassOfAxiom>)eqAx.asOWLSubClassOfAxioms();
			ret =  toQLNormalForm(subclAxioms,tmpNonQLAxioms,!bestEffort);
			/*if (bestEffort) {
				return toQLNormalForm(subclAxioms,nonQLAxioms);
			} else {
				Set<OWLAxiom> tmpNonQLAxioms = new HashSet<OWLAxiom>();
				Set<OWLAxiom> tmpRet = toQLNormalForm(subclAxioms, tmpNonQLAxioms);
				return !tmpNonQLAxioms.isEmpty()? null: tmpRet;
			}*/
		} else if (ax.getAxiomType().equals(AxiomType.SUBCLASS_OF)) {
			OWLSubClassOfAxiom subAx = (OWLSubClassOfAxiom) ax;
			OWLClassExpression subclass= subAx.getSubClass();
			OWLClassExpression superclass = subAx.getSuperClass();
			// check that subclass is either atomic or someValuesFrom(R, Top)
			if (isValidOWLQLLeftConcept(subclass)) {
				if (isValidOWLQLRightConcept(superclass)) {
					ret.add(subAx);
				} else if (superclass.getClassExpressionType().equals(ClassExpressionType.OBJECT_SOME_VALUES_FROM)) {
					ret.addAll(toQLNormalFormRightExistential(subAx));
				} else if (superclass.getClassExpressionType().equals(ClassExpressionType.OBJECT_INTERSECTION_OF)) {
					OWLObjectIntersectionOf inter = (OWLObjectIntersectionOf) superclass;
					Set<OWLClassExpression> superclasses = inter.getOperands();
					for (OWLClassExpression sc: superclasses) {
						OWLSubClassOfAxiom newAx = fac.getOWLSubClassOfAxiom(subclass, sc);
						Set<OWLAxiom> tpAxioms = toQLNormalForm(newAx,tmpNonQLAxioms);
						if (tpAxioms!=null) {
							ret.addAll(tpAxioms);
						} else if (!bestEffort) {
							ret = null;
							break;
						}
					}
				} 
				else {
					//TODO handle qualified data existential restriction
					ret = null;
				}
			} else {
				ret = null;
			}
		} else if (ax.getAxiomType().equals(AxiomType.DISJOINT_CLASSES)) {
			OWLDisjointClassesAxiom dax = (OWLDisjointClassesAxiom) ax;
			for (OWLDisjointClassesAxiom sdax: dax.asPairwiseAxioms()) {
				Set<OWLClassExpression> classes = sdax.getClassExpressions();
				assert classes.size() == 2 : classes;
				Iterator<OWLClassExpression> it = classes.iterator();
				OWLClassExpression class1 = it.next();
				OWLClassExpression class2 = it.next();
				OWLSubClassOfAxiom sca = fac.getOWLSubClassOfAxiom(class1,
						fac.getOWLObjectComplementOf(class2).getNNF());
				Set<OWLAxiom> tpAxioms = toQLNormalForm(sca,tmpNonQLAxioms);
				if (tpAxioms!=null) {
					ret.addAll(tpAxioms);
				} else {
					sca =  fac.getOWLSubClassOfAxiom(class2,
							fac.getOWLObjectComplementOf(class1).getNNF());
					tpAxioms = toQLNormalForm(sca, tmpNonQLAxioms);
					if (tpAxioms!=null) {
						ret.addAll(tpAxioms);
					} else {
						if (!bestEffort) {
							ret = null;
							break;
						}
					}
				}
			}
		} else if (ax.getAxiomType().equals(AxiomType.EQUIVALENT_OBJECT_PROPERTIES)) {
			OWLEquivalentObjectPropertiesAxiom eop = (OWLEquivalentObjectPropertiesAxiom) ax;
			ret = toQLNormalForm(asSubObjectPropertyOfAxioms(eop), tmpNonQLAxioms, !bestEffort);
		} else if (ax.getAxiomType().equals(AxiomType.EQUIVALENT_DATA_PROPERTIES)) {
			OWLEquivalentDataPropertiesAxiom eop = (OWLEquivalentDataPropertiesAxiom) ax;
			List<OWLDataPropertyExpression> props = new ArrayList<OWLDataPropertyExpression>(eop.getProperties());
			outerLoop: for (int i=0;i<props.size();i++) {
				for (int j=i+1; j<props.size();j++) {
					Set<OWLSubDataPropertyOfAxiom> subprops = new HashSet<OWLSubDataPropertyOfAxiom>();
					subprops.add(fac.getOWLSubDataPropertyOfAxiom(props.get(i),props.get(j)));
					subprops.add(fac.getOWLSubDataPropertyOfAxiom(props.get(j),props.get(i)));
					Set<OWLAxiom> tp = toQLNormalForm(subprops, tmpNonQLAxioms, !bestEffort);
					if (tp==null) {
						if (!bestEffort) {
							ret = null;
							break outerLoop;
						}
					} else {
						ret.addAll(tp);
					}
				}
			}
			
		}
		else if (ax.getAxiomType().equals(AxiomType.INVERSE_OBJECT_PROPERTIES)) {
			OWLInverseObjectPropertiesAxiom iopa = (OWLInverseObjectPropertiesAxiom) ax;
			return toQLNormalForm(iopa.asSubObjectPropertyOfAxioms(), tmpNonQLAxioms, !bestEffort);
			
		} else if (ax.getAxiomType().equals(AxiomType.SYMMETRIC_OBJECT_PROPERTY)) {
			OWLSymmetricObjectPropertyAxiom sopa = (OWLSymmetricObjectPropertyAxiom) ax;
			return toQLNormalForm(sopa.asSubPropertyAxioms(), tmpNonQLAxioms, !bestEffort);
			
		} else if (ax.getAxiomType().equals(AxiomType.OBJECT_PROPERTY_DOMAIN)) {
	            OWLPropertyDomainAxiom<OWLObjectPropertyExpression> pda = (OWLPropertyDomainAxiom<OWLObjectPropertyExpression>) ax;
	            OWLObjectPropertyExpression prop = pda.getProperty();
	            OWLClassExpression sub;
	            sub = fac.getOWLObjectSomeValuesFrom((OWLObjectPropertyExpression) prop, fac.getOWLThing());
	            OWLSubClassOfAxiom subclassAx = fac.getOWLSubClassOfAxiom(sub, pda.getDomain());
	            ret = toQLNormalForm(subclassAx, tmpNonQLAxioms);
	        } else if (ax.getAxiomType().equals(AxiomType.DATA_PROPERTY_DOMAIN)) {
	            OWLPropertyDomainAxiom<OWLDataPropertyExpression> pda = (OWLPropertyDomainAxiom<OWLDataPropertyExpression>) ax;
	            OWLDataPropertyExpression prop = pda.getProperty();
	            OWLClassExpression sub;
	            sub = fac.getOWLDataSomeValuesFrom((OWLDataPropertyExpression) prop, fac.getTopDatatype());
	            OWLSubClassOfAxiom subclassAx = fac.getOWLSubClassOfAxiom(sub, pda.getDomain());
	            ret = toQLNormalForm(subclassAx, tmpNonQLAxioms);
		} else if ( ax.getAxiomType().equals(AxiomType.OBJECT_PROPERTY_RANGE)) {
			OWLObjectPropertyRangeAxiom prax = (OWLObjectPropertyRangeAxiom) ax;
			OWLObjectPropertyExpression prop = prax.getProperty();
			OWLClassExpression sub = fac.getOWLObjectSomeValuesFrom(fac.getOWLObjectInverseOf(prop).getSimplified(),fac.getOWLThing());
			OWLSubClassOfAxiom subclassAx = fac.getOWLSubClassOfAxiom(sub, prax.getRange());
			ret = toQLNormalForm(subclassAx, tmpNonQLAxioms);
		}  else if (ax.getAxiomType().equals(AxiomType.DISJOINT_OBJECT_PROPERTIES)
			|| ax.getAxiomType().equals(AxiomType.DISJOINT_DATA_PROPERTIES)){
			OWLNaryPropertyAxiom nax = (OWLNaryPropertyAxiom) ax;
			List<OWLPropertyExpression>  props = new ArrayList<OWLPropertyExpression>(nax.getProperties());
			for (int i=0;i<props.size();i++) {
				for (int j=i+1; j<props.size();j++) {
					if (ax.getAxiomType().equals(AxiomType.DISJOINT_OBJECT_PROPERTIES)) {
						Set<OWLObjectPropertyExpression> oprops =  new HashSet<OWLObjectPropertyExpression>();
						oprops.add((OWLObjectPropertyExpression) props.get(i));
						oprops.add((OWLObjectPropertyExpression) props.get(j));
						ret.add(fac.getOWLDisjointObjectPropertiesAxiom(oprops));
					} else {
						Set<OWLDataPropertyExpression> oprops =  new HashSet<OWLDataPropertyExpression>();
						oprops.add((OWLDataPropertyExpression) props.get(i));
						oprops.add((OWLDataPropertyExpression) props.get(j));
						ret.add(fac.getOWLDisjointDataPropertiesAxiom(oprops));
					}
				}
			}
		} else if (ax.getAxiomType().equals(AxiomType.SUB_OBJECT_PROPERTY)
				|| ax.getAxiomType().equals(AxiomType.SUB_DATA_PROPERTY)) {
			ret.add(ax);
		} else if ( ax.getAxiomType().equals(AxiomType.ASYMMETRIC_OBJECT_PROPERTY)) {
			OWLAsymmetricObjectPropertyAxiom asymax = (OWLAsymmetricObjectPropertyAxiom) ax;
			OWLObjectProperty prop = (OWLObjectProperty) asymax.getProperty();
			ret.add(fac.getOWLDisjointObjectPropertiesAxiom(prop, prop.getInverseProperty().getSimplified()));
		} else if ( ax.getAxiomType().equals(AxiomType.REFLEXIVE_OBJECT_PROPERTY)) {
			OWLReflexiveObjectPropertyAxiom rax = (OWLReflexiveObjectPropertyAxiom) ax;
			ret.add(fac.getOWLReflexiveObjectPropertyAxiom(rax.getProperty().getNamedProperty()));
		} else if (ax.getAxiomType().equals(AxiomType.IRREFLEXIVE_OBJECT_PROPERTY)) {
			OWLIrreflexiveObjectPropertyAxiom iax = (OWLIrreflexiveObjectPropertyAxiom) ax;
			ret.add(fac.getOWLIrreflexiveObjectPropertyAxiom(iax.getProperty().getNamedProperty()));
		} else if (ax.getAxiomType().equals(AxiomType.DATA_PROPERTY_RANGE)) {
			//TODO: 
			ret = null;
		} else if (ax.getAxiomType().equals(AxiomType.FUNCTIONAL_OBJECT_PROPERTY)
				|| ax.getAxiomType().equals(AxiomType.INVERSE_FUNCTIONAL_OBJECT_PROPERTY)
				|| ax.getAxiomType().equals(AxiomType.TRANSITIVE_OBJECT_PROPERTY)
				|| ax.getAxiomType().equals(AxiomType.SUB_PROPERTY_CHAIN_OF)
				|| ax.getAxiomType().equals(AxiomType.FUNCTIONAL_DATA_PROPERTY)
				|| ax.getAxiomType().equals(AxiomType.HAS_KEY)
				|| ax.getAxiomType().equals(AxiomType.DISJOINT_UNION)
				|| ax.getAxiomType().equals(AxiomType.DATATYPE_DEFINITION)
				|| ax.getAxiomType().equals(AxiomType.SWRL_RULE)) {
			//Non OWL QL constructs.
			ret = null;
		} 
		if (tmpNonQLAxioms!=null) {
			// bestEffort
			if (!tmpNonQLAxioms.isEmpty()) {
				if (nonQLAxioms!=null) {
					if (ret!= null) {
						nonQLAxioms.addAll(tmpNonQLAxioms);
					} else {
						nonQLAxioms.add(ax);
					}
				}
			} 
		} else {
			// !bestEffort
			if (ret==null && nonQLAxioms!=null) {
				nonQLAxioms.add(ax);
			}
		}
		return ret;
	}
	
	public OWLQLNormalizer copy() {
		OWLQLNormalizer ret = new OWLQLNormalizer(fac, newClassURIPrefix,
				newRoleURIPrefix, bestEffort);
		ret.newClasses =  newClasses;
		ret.newRoles = newRoles;
		return ret;
	}
	public Object clone() {
		return copy();
	}
}
