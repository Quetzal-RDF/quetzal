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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLQuantifiedRestriction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.vocabulary.RDF;
import com.ibm.research.owlql.NormalizedOWLQLTbox;
import com.ibm.research.owlql.Taxonomy;
import com.ibm.research.owlql.rule.AtomicFormula;
import com.ibm.research.owlql.rule.BoundVariablePredicate;
import com.ibm.research.owlql.rule.ConstantExpr;
import com.ibm.research.owlql.rule.Expr;
import com.ibm.research.owlql.rule.Predicate;
import com.ibm.research.owlql.rule.Rule;
import com.ibm.research.owlql.rule.RuleSystem;
import com.ibm.research.owlql.rule.VariableExpr;
import com.ibm.wala.util.collections.Pair;

public class EliminateEJVar {

	private static final Logger logger = LoggerFactory.getLogger(EliminateEJVar.class);
	
	protected static class Signature {
		private boolean propertyWildcardXFirst;
		private boolean propertyWildcardXSecond;
		private int conceptWildcards;
		private Set<AtomicFormula> conceptWildcardsWithBoundVars;
		private Set<AtomicFormula> propertyWildcardsWithBoundVars;
		public Signature(List<AtomicFormula> conceptWildcards,
				List<AtomicFormula> propertyWildcardsXisInFirstPos, List<AtomicFormula> propertyWildcardsXisInSecondPos,
				Rule r) {
			super();
			this.propertyWildcardXFirst = propertyWildcardsXisInFirstPos.size()>0;
			this.propertyWildcardXSecond = propertyWildcardsXisInSecondPos.size()>0;
			this.conceptWildcards = conceptWildcards.size();
			this.conceptWildcardsWithBoundVars = new HashSet<AtomicFormula>();
			for (AtomicFormula cw: conceptWildcards) {
				assert cw.getArguments().get(2).isVariable();
				VariableExpr conceptVar = (VariableExpr)  cw.getArguments().get(2);
				if (!r.getUnboundVariables().contains(conceptVar)) {
					// if conceptVar is not a unbound variable
					conceptWildcardsWithBoundVars.add(cw);
				}
				Expr e1 = cw.getArguments().get(1);
				if (e1.isVariable() && !r.getUnboundVariables().contains(e1)) {
					// predicate variable is not an unbound variable
					conceptWildcardsWithBoundVars.add(cw);
				}
			}
			
			this.propertyWildcardsWithBoundVars = new HashSet<AtomicFormula>();
			for (int i=0;i<2;i++) {
				for (AtomicFormula pw: i==0? propertyWildcardsXisInFirstPos: propertyWildcardsXisInSecondPos) {
					VariableExpr propertyVar = (VariableExpr) pw.getArguments().get(1);
					if (!r.getUnboundVariables().contains(propertyVar)) {
						// propertyVar is not an unbound variable
						propertyWildcardsWithBoundVars.add(pw);
					}
				}
			}
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + conceptWildcards;
			result = prime
					* result
					+ ((conceptWildcardsWithBoundVars == null) ? 0
							: conceptWildcardsWithBoundVars.hashCode());
			result = prime * result + (propertyWildcardXFirst ? 1231 : 1237);
			result = prime * result + (propertyWildcardXSecond ? 1231 : 1237);
			result = prime
					* result
					+ ((propertyWildcardsWithBoundVars == null) ? 0
							: propertyWildcardsWithBoundVars.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Signature other = (Signature) obj;
			if (conceptWildcards != other.conceptWildcards)
				return false;
			if (conceptWildcardsWithBoundVars == null) {
				if (other.conceptWildcardsWithBoundVars != null)
					return false;
			} else if (!conceptWildcardsWithBoundVars
					.equals(other.conceptWildcardsWithBoundVars))
				return false;
			if (propertyWildcardXFirst != other.propertyWildcardXFirst)
				return false;
			if (propertyWildcardXSecond != other.propertyWildcardXSecond)
				return false;
			if (propertyWildcardsWithBoundVars == null) {
				if (other.propertyWildcardsWithBoundVars != null)
					return false;
			} else if (!propertyWildcardsWithBoundVars
					.equals(other.propertyWildcardsWithBoundVars))
				return false;
			return true;
		}
		@Override
		public String toString() {
			return "Signature [propertyWildcardXFirst="
					+ propertyWildcardXFirst + ", propertyWildcardXSecond="
					+ propertyWildcardXSecond + ", conceptWildcards="
					+ conceptWildcards + ", conceptWildcardsWithHeadVars="
					+ conceptWildcardsWithBoundVars
					+ ", propertyWildcardsWithHeadVars="
					+ propertyWildcardsWithBoundVars + "]";
		}
		
		
		
	}
	protected NormalizedOWLQLTbox tbox;
	protected Taxonomy taxo;
	protected OWLDataFactory fac;
	public EliminateEJVar(NormalizedOWLQLTbox tbox, Taxonomy taxo, OWLDataFactory fac) {
		super();
		this.taxo = taxo;
		this.fac = fac;
		this.tbox = tbox;
	}

	Set<VariableExpr> getJoinVariables(Rule r) {
		//
		Set<VariableExpr> ret = new HashSet<VariableExpr>(r.getAllRuleVariables());
		ret.removeAll(r.getHead().getAllVariables());
		ret.removeAll( r.getUnboundVariables());
		ret.removeAll(TriplePredicate.getPredicateVariables(r)); 
		
		return ret;
		//
	}
	RuleSystem  eliminate(Rule r, VariableExpr x, int[] nextAvailableRuleId) {
		
		// we assume that unbound vars have already been eliminated
		//assert r.getUnboundVariables().isEmpty() : r;
		//
		// we assume that x is a join variable 
		assert !r.getHead().getAllVariables().contains(x)  : x+"\n"+r;
		assert !r.getUnboundVariables().contains(x) : x+"\n"+r;
		//
		
		// assume that the split operation has
		
		// indicates whether x is a rdf resource (uri or blank node) or 
		// a rdf literal
		Boolean xIsAResource =null ;
		
		List<AtomicFormula> unary= new LinkedList<AtomicFormula>();
		
		List<AtomicFormula> binaryInFirstPos = new LinkedList<AtomicFormula>();
		List<AtomicFormula> binaryInSndPos = new LinkedList<AtomicFormula>();
		List<AtomicFormula> conceptPropertyWildcards = new LinkedList<AtomicFormula>();
		List<AtomicFormula> conceptWildcards = new LinkedList<AtomicFormula>();
		List<AtomicFormula> propertyWildcards = new LinkedList<AtomicFormula>();
		
		List<AtomicFormula> formulasWithoutEJVarX = new LinkedList<AtomicFormula>();
		
		Set<Expr> constants = new HashSet<Expr>();
		
		for (AtomicFormula af: r.getBody()) {
			//check if x is in af
			
			// handle the case of wildcard concept or role
			if (af.getPredicate() instanceof TriplePredicate) {
				// the predicate variable cannot be a join var
				assert !af.getArguments().get(1).equals(x) : af+"\n"+x;
				Expr arg1 = af.getArguments().get(0);
				Expr arg2 = af.getArguments().get(2);
				
				//
				boolean xfound = false;
				if (arg1.equals(x)
				&& arg2.equals(x)) {
					// x cannot appear twice in the same formula
					return null;
				} else if (arg1.equals(x)) {
					if (xIsAResource!=null && !xIsAResource) {
						// x stand for a resource and a literal
						return null;
					}
					xIsAResource = Boolean.TRUE;
					xfound = true;
					if (arg2.isConstant()) {
						constants.add(arg2);
						if (constants.size()>1) {
							// multiple constants make unification impossible
							return null;
						}
					}
				} else if (arg2.equals(x)) {
					xfound = true;
					if (arg1.isConstant()) {
						constants.add(arg1);
						if (constants.size()>1) {
							// multiple constants make unification impossible
							return null;
						}
					}
				}
				if (xfound) {
					if (isConceptPropertyWildcard(af)) {
						if (af.getArguments().get(2).equals(x)) {
							// the join variable only stands for individuals
							// so this must actually be a propertyWildcard
							propertyWildcards.add(af);
						} else {
							
							conceptPropertyWildcards.add(af);
						}
					} else if (isConceptWildcard(af)) {
						conceptWildcards.add(af);
					} else {
						assert isPropertyWildcard(af);
						propertyWildcards.add(af);
					}
				} else {
					formulasWithoutEJVarX.add(af);
				}
				
			}
			//
			
			if ( !(af.getPredicate() instanceof DLAnnotatedPredicate)
			|| af.getArity() == 0) {
				formulasWithoutEJVarX.add(af);
				continue;
			}
			DLAnnotatedPredicate dlp = (DLAnnotatedPredicate) af.getPredicate();
			// x is in af
			if (af.getArity() == 1
			&& x.equals(af.getArguments().get(0))) {
				
				if (dlp instanceof ExistInverseDataPropertyAnnotatedPredicate) {
					if (xIsAResource!=null && xIsAResource) {
						// x stand for a resource and a literal
						return null;
					}
					xIsAResource = Boolean.FALSE;
					unary.add(af);
				} else {
					if (xIsAResource!=null && !xIsAResource) {
						// x stand for a resource and a literal
						return null;
					}
					xIsAResource = Boolean.TRUE;
					unary.add(af);
				}
			} else if (af.getArity() == 2) {
				Expr arg1 = af.getArguments().get(0);
				Expr arg2 = af.getArguments().get(1);
				if (x.equals(arg1) && x.equals(arg2)) {
					// x cannot appear twice in the same formula
					return null;
				} else if (x.equals(arg1)) {
					if (xIsAResource!=null && !xIsAResource) {
						// x stand for a resource and a literal
						return null;
					}
					xIsAResource = Boolean.TRUE;
					if (arg2.isConstant()) {
						constants.add(arg2);
						if (constants.size()>1) {
							// multiple constants make unification impossible
							return null;
						}
					}
					binaryInFirstPos.add(af);
				} else if (x.equals(arg2)) {
					OWLPropertyExpression prop = dlp.getPropertyAnnotation();
					if (xIsAResource!=null && xIsAResource.booleanValue()!=prop.isObjectPropertyExpression()) {
						// x stand for a resource and a literal
						return null;
					}
					xIsAResource = prop.isObjectPropertyExpression()? Boolean.TRUE: Boolean.FALSE;
					if (arg1.isConstant()) {
						constants.add(arg1);
						if (constants.size()>1) {
							// multiple constants make unification impossible
							return null;
						}
					}
					binaryInSndPos.add(af);
				} else {
					formulasWithoutEJVarX.add(af);
				}
			} else {
				formulasWithoutEJVarX.add(af);
			}
		}
		Set<OWLClassExpression> mostGeneralSubsumees;
		Set<OWLClassExpression> classexprs = new HashSet<OWLClassExpression>();
		Set<OWLPropertyExpression> propexprs = new HashSet<OWLPropertyExpression>();
		if (xIsAResource != null) {
			if (xIsAResource) {
				 for (AtomicFormula af: unary) {
					 DLAnnotatedPredicate dlp =(DLAnnotatedPredicate) af.getPredicate();
					 classexprs.add(dlp.getClassAnnotation());
				 }
				 for (AtomicFormula af: binaryInFirstPos) {
					 DLAnnotatedPredicate dlp =(DLAnnotatedPredicate) af.getPredicate();
					 propexprs.add(dlp.getPropertyAnnotation());
				 }
				 for (AtomicFormula af: binaryInSndPos) {
					 DLAnnotatedPredicate dlp =(DLAnnotatedPredicate) af.getPredicate();
					 OWLObjectPropertyExpression oprop =  (OWLObjectPropertyExpression) dlp.getPropertyAnnotation();
					 propexprs.add(oprop.getInverseProperty().getSimplified());
				 }
			} else {
				 for (AtomicFormula af: unary) {
					 ExistInverseDataPropertyAnnotatedPredicate dlp =(ExistInverseDataPropertyAnnotatedPredicate) af.getPredicate();
					 OWLDataProperty prop = dlp.getProperty();
					 propexprs.add(prop);
				 }
				 assert binaryInFirstPos.isEmpty() : binaryInFirstPos;
				 for (AtomicFormula af: binaryInSndPos) {
					 DLAnnotatedPredicate dlp =(DLAnnotatedPredicate) af.getPredicate();
					 OWLDataPropertyExpression prop =  (OWLDataPropertyExpression) dlp.getPropertyAnnotation();
					 assert !prop.isAnonymous() : prop;
					 propexprs.add(prop);
				 }
				 assert !propexprs.isEmpty(); //since at least one of the following sets is non-empty: unary, binaryInFirstPos, binaryInSndPos.
			}
		} else {
			assert unary.isEmpty() && binaryInFirstPos.isEmpty() && binaryInSndPos.isEmpty();
		}
//		if (!classexprs.isEmpty() || !propexprs.isEmpty()) {
//			mostGeneralSubsumees = taxo.getMostGeneralSubsumees(classexprs, propexprs);
//			if (mostGeneralSubsumees == null || mostGeneralSubsumees.isEmpty()) {
//				return null; // the fixed part has no common subsumees
//			}
//		}
		Map<Integer, Set<OWLClassExpression>> conceptWildcard2MGS = taxo.computeMostGeneralSubsumees(classexprs, propexprs,
				conceptWildcards.size()+conceptPropertyWildcards.size(), conceptWildcards.size());
		if (conceptWildcard2MGS.isEmpty()
		&& (!classexprs.isEmpty() || !classexprs.isEmpty() || conceptWildcards.size()+conceptPropertyWildcards.size()>0)) {
			return null;
		}
		
		Set<Rule> ret = new HashSet<Rule>();
		Set<Signature> signs = new HashSet<Signature>();
		int maxSignSize = 100000;// when the signature size is about this limit we disable the signature optimization
		for (Iterator<Pair<List<AtomicFormula>, List<AtomicFormula>>> it = getAllCombinationsOfConceptWildcardPropertyWildcard(conceptPropertyWildcards);it.hasNext();) {
			Pair<List<AtomicFormula>, List<AtomicFormula>> p =it.next();
			List<AtomicFormula> conceptWs = new LinkedList<AtomicFormula>(p.fst);
			conceptWs.addAll(conceptWildcards);
			List<AtomicFormula> propertyWs = new LinkedList<AtomicFormula>(p.snd);
			propertyWs.addAll(propertyWildcards);
			List<AtomicFormula> propertyWXisInFirstPos = new LinkedList<AtomicFormula>();
			List<AtomicFormula> propertyWXisInSecondPos = new LinkedList<AtomicFormula>();
			for (AtomicFormula af : propertyWs) {
				if (af.getArguments().get(0).equals(x)) {
					propertyWXisInFirstPos.add(af);
				} else {
					propertyWXisInSecondPos.add(af);
				}
			}
			if (signs!=null && !signs.add(new Signature(conceptWs, propertyWXisInFirstPos, propertyWXisInSecondPos, r))) {
				// signature has already been seen
				// it will not generate any new rule
				continue;
			}
			if (signs.size()>maxSignSize) {
				signs = null;
			}
			//mostGeneralSubsumees = taxo.getMostGeneralSubsumees(classexprs, propexprs,0,propertyWs.size(),conceptWs.size());
			// more efficient approach than "mostGeneralSubsumees = taxo.getMostGeneralSubsumees(classexprs, propexprs,0,propertyWs.size(),conceptWs.size());"
			mostGeneralSubsumees = conceptWildcard2MGS.get(conceptWs.size());
			if (propertyWs.size()>0) {
				// property wildcards exist
				if (mostGeneralSubsumees == null || mostGeneralSubsumees.isEmpty()) { 
					if ( conceptWs.isEmpty() && classexprs.isEmpty() && propexprs.isEmpty()) {
						//only property wildcards,
						mostGeneralSubsumees = taxo.applyPropertyWildcard(Collections.EMPTY_SET);
					}
				} else {
					mostGeneralSubsumees = taxo.applyPropertyWildcard(mostGeneralSubsumees);
				}
			}
			//
			
			if (mostGeneralSubsumees==null ||  mostGeneralSubsumees.isEmpty()) {
				continue;
			}
			
			
			if (binaryInFirstPos.isEmpty() && binaryInSndPos.isEmpty() && propertyWs.isEmpty()) {
				for (OWLClassExpression ce: mostGeneralSubsumees) {
					Predicate newBodyPred = new DLAnnotatedPredicate(ce, true);
					List<AtomicFormula> newBody = new LinkedList<AtomicFormula>(formulasWithoutEJVarX);
					if (!conceptWs.isEmpty()) {
						Set<URI> classNames = getNamedSuperclassOrSelf(ce);
						if (classNames.isEmpty()) {
							continue; // there is no named classes that can be used as the concept
						}
						for (AtomicFormula cw: conceptWs)  {
							assert cw.getArguments().get(2).isVariable();
							VariableExpr conceptVar = (VariableExpr)  cw.getArguments().get(2);
							if (!r.getUnboundVariables().contains(conceptVar)) {
								// if conceptVar is not an unbound variable
								AtomicFormula boundAf = createBoundFormula(conceptVar, classNames);
								newBody.add(0,boundAf);
							}
							Expr e1 = cw.getArguments().get(1);
							if (e1.isVariable() && !r.getUnboundVariables().contains(e1)) {
								// predicate variable is not an unbound variable
								VariableExpr predicateVar = (VariableExpr) e1;
								AtomicFormula boundAf = createBoundFormula(predicateVar, Collections.singleton(getRDFType()));
								newBody.add(0,boundAf);
							}
							// if the predicate is a constant, it must be rdf:type
							assert !e1.isConstant() || ((ConstantExpr) e1).getValue().equals(getRDFType()): e1; 
							
						}
					}
					AtomicFormula af = new AtomicFormula(newBodyPred, new ArrayList<Expr>());
					newBody.add(0,af);
					ret.add(new Rule(r.getHead().clone(), newBody, nextAvailableRuleId[0]++));
					
				}
			} else {
				Set<VariableExpr> allEqVars = new  HashSet<VariableExpr>();
				for (AtomicFormula af: binaryInFirstPos) {
					Expr e = af.getArguments().get(1);
					if (e.isVariable()) {
						allEqVars.add((VariableExpr)e);
					}
				}
				for (AtomicFormula af: binaryInSndPos) {
					Expr e = af.getArguments().get(0);
					if (e.isVariable()) {
						allEqVars.add((VariableExpr)e);
					}
				}
				for (AtomicFormula af: propertyWs) {
					Expr e;
					if (af.getArguments().get(0).equals(x)) {
						e = af.getArguments().get(2);
					} else {
						assert af.getArguments().get(2).equals(x) : af;
						e = af.getArguments().get(0);
					}
					if (e.isVariable()) {
						allEqVars.add((VariableExpr)e);
					}
				}
				Expr newTerm;
				if (constants.isEmpty()) {
					assert !allEqVars.isEmpty() ;
					newTerm = allEqVars.iterator().next();
				} else {
					newTerm = constants.iterator().next();
				}
				
				for (OWLClassExpression ce: mostGeneralSubsumees) {
					Predicate newBodyPred; //= new DLAnnotatedPredicate(ce, true);
					List<AtomicFormula> newBody = new LinkedList<AtomicFormula>(replaceVariables(formulasWithoutEJVarX, allEqVars, newTerm));
					if (xIsAResource!=null && !xIsAResource) {
						// assumption about construction of classexprs and propexprs
						assert classexprs.isEmpty(); // by construction of classexprs for !isResource
						assert !propexprs.isEmpty(); // by construction of propsexprs for !isResource
						boolean assertionEnabled = false;
						assert assertionEnabled = true;
						if (assertionEnabled) {
							for (OWLPropertyExpression pe: propexprs) {
								assert pe instanceof OWLDataProperty: pe;
							}
						}
						assert ce instanceof OWLDataSomeValuesFrom: ce; // since propexprs contains at least one data property
						// 
						newBodyPred = new DLAnnotatedPredicate(ce);
						
						AtomicFormula af = new AtomicFormula(newBodyPred, newTerm);
						newBody.add(0,af);
					} else {
						if (ce instanceof OWLObjectSomeValuesFrom) {
							OWLObjectSomeValuesFrom c = (OWLObjectSomeValuesFrom) ce;
							OWLObjectSomeValuesFrom invC = fac.getOWLObjectSomeValuesFrom(c.getProperty().getInverseProperty().getSimplified(),
									c.getFiller());
							newBodyPred = new DLAnnotatedPredicate(invC);
						} else {
							assert ce instanceof OWLDataSomeValuesFrom : ce;
							OWLDataSomeValuesFrom dsv = (OWLDataSomeValuesFrom) ce;
							newBodyPred = new ExistInverseDataPropertyAnnotatedPredicate((OWLDataProperty)dsv.getProperty(), fac);
						}
						AtomicFormula af = new AtomicFormula(newBodyPred, newTerm);
						newBody.add(0,af);
					}
					
					if (!propertyWs.isEmpty()) {
						
						if (!propertyWXisInFirstPos.isEmpty()) {
							Set<URI> inFirstProps = getNamedSuperPropertiesOrSelf(ce);
							if (inFirstProps.isEmpty()) {
								continue; // there is no named property that can be used 
							}
							for (AtomicFormula af: propertyWXisInFirstPos)  {
								assert af.getArguments().get(1).isVariable(): af; // wildcard
								assert af.getArguments().get(0).equals(x): af; // definition of inFirst
								
								VariableExpr propertyVar = (VariableExpr) af.getArguments().get(1);
								if (!r.getUnboundVariables().contains(propertyVar)) {
									// propertyVar is not an unbound  variable
									AtomicFormula boundAf = createBoundFormula(propertyVar, inFirstProps);
									newBody.add(0,boundAf);
								}
							}
						}
						if (!propertyWXisInSecondPos.isEmpty()) {
							Set<URI> inSecondProps = getNamedInverseSuperPropertiesOrSelf(ce);
							if (inSecondProps.isEmpty()) {
								continue; // there is no named property that can be used 
							}
							for (AtomicFormula af: propertyWXisInSecondPos)  {
								assert af.getArguments().get(1).isVariable(): af; // wildcard
								assert af.getArguments().get(2).equals(x): af; // definition of inFirst
								
								VariableExpr propertyVar = (VariableExpr) af.getArguments().get(1);
								if (!r.getUnboundVariables().contains(propertyVar)) {
									// propertyVar is not an unbound variable
									AtomicFormula boundAf = createBoundFormula(propertyVar, inSecondProps);
									newBody.add(0,boundAf);
								}
							}
						}
						
					}
					
									
					ret.add(new Rule(r.getHead().clone(), newBody, nextAvailableRuleId[0]++));
				}
			}
		}
		
 		
		return ret.isEmpty()? null: new RuleSystem(new LinkedList<Rule>(ret));
	}
	
	protected static URI getRDFType() {
		try {
			return new URI(RDF.type.getURI());
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * in afs, replace occurrence of variables in varsToReplace by replacementExpression
	 * @param afs
	 * @param varsToReplace
	 * @param replacementExpression
	 * @return
	 */
	protected List<AtomicFormula> replaceVariables(List<AtomicFormula> afs, Set<VariableExpr> varsToReplace, Expr replacementExpression) {
		 List<AtomicFormula> ret = new LinkedList<AtomicFormula>();
		 for (AtomicFormula af: afs) {
			 ret.add(replaceVariables(af, varsToReplace, replacementExpression));
		 }
		 return ret;
	}
	/**
	 * replace occurrence of variables in varsToReplace by replacementExpression
	 * @param afs
	 * @param varsToReplace
	 * @param replacementExpression
	 * @return
	 */
	protected AtomicFormula replaceVariables(AtomicFormula af, Set<VariableExpr> varsToReplace, Expr replacementExpression) {
		List<Expr> args = new LinkedList<Expr>();
		for (Expr e : af.getArguments()) {
			if (varsToReplace.contains(e)) {
				args.add(replacementExpression);
			} else {
				args.add(e);
			}
		}
		return new AtomicFormula(af.getPredicate(), args);
	}
	/**
	 * NOTE: if conceptPropertyWildcars.isEmpty() returns {Pair<EmptyList, EmptyList>}.
	 * @param conceptPropertyWildcards
	 * @return
	 */
	protected Iterator<Pair<List<AtomicFormula>, List<AtomicFormula>>> getAllCombinationsOfConceptWildcardPropertyWildcard(List<AtomicFormula> conceptPropertyWildcards) {
		List<Pair<List<AtomicFormula>, List<AtomicFormula>>> results = new LinkedList<Pair<List<AtomicFormula>,List<AtomicFormula>>>();
		computeAllCombinationsOfConceptWildcardPropertyWildcard(conceptPropertyWildcards, results);
		return results.iterator();
	}
	
	private void computeAllCombinationsOfConceptWildcardPropertyWildcard(List<AtomicFormula> conceptPropertyWildcards, List<Pair<List<AtomicFormula>, List<AtomicFormula>>> results) {
		assert results!=null;
		if (!conceptPropertyWildcards.isEmpty()) {
			AtomicFormula af = conceptPropertyWildcards.remove(0);
			List<Pair<List<AtomicFormula>, List<AtomicFormula>>> newresults = new LinkedList<Pair<List<AtomicFormula>,List<AtomicFormula>>>();
			computeAllCombinationsOfConceptWildcardPropertyWildcard(conceptPropertyWildcards, newresults);
			for (Pair<List<AtomicFormula>, List<AtomicFormula>> r : newresults) {
				Pair<List<AtomicFormula>, List<AtomicFormula>> r1 = r;
				Pair<List<AtomicFormula>, List<AtomicFormula>> r2 = 
					Pair.make( (List<AtomicFormula>)  new LinkedList<AtomicFormula>(r.fst), 
							 (List<AtomicFormula>) new LinkedList<AtomicFormula>(r.snd));
				r1.fst.add(af);
				r2.snd.add(af);
				results.add(r1);
				results.add(r2);
				
			}
		} else {
			results.add(Pair.make( (List<AtomicFormula>) new LinkedList<AtomicFormula>(),  (List<AtomicFormula>) new LinkedList<AtomicFormula>()));
		}
	}
	
	/**
	 * return named classes nc such that c \subseteq nc
	 * @param c
	 * @return
	 */
	protected Set<URI> getNamedSuperclassOrSelf(OWLClassExpression c) {
		Set<OWLClassExpression> subs = taxo.getAllSubsumers(c);
		Set<URI> ret = new HashSet<URI>();
		for (OWLClassExpression ex: subs) {
			if (!ex.isAnonymous()) {
				OWLClass cl = ex.asOWLClass();
				ret.add(cl.getIRI().toURI());
			}
		}
		return ret;
	}
	/**
	 * return named properties p such that c \subseteq \some p T
	 * @param c
	 * @return
	 */
	protected Set<URI> getNamedSuperPropertiesOrSelf(OWLClassExpression c) {
		Set<OWLClassExpression> subs = taxo.getAllSubsumers(c);
		Set<URI> ret = new HashSet<URI>();
		for (OWLClassExpression ex: subs) {
			if (ex.getClassExpressionType().equals(ClassExpressionType.OBJECT_SOME_VALUES_FROM)
			|| ex.getClassExpressionType().equals(ClassExpressionType.DATA_SOME_VALUES_FROM)) {
				OWLQuantifiedRestriction rest = (OWLQuantifiedRestriction) ex;
				OWLPropertyExpression prop = rest.getProperty();
				if (!prop.isAnonymous()) {
					if (prop.isDataPropertyExpression()) {
						OWLDataProperty namedProp = (OWLDataProperty) prop;
						ret.add(namedProp.getIRI().toURI());
					} else {
						assert prop.isObjectPropertyExpression() : prop;
						OWLObjectProperty namedProp = (OWLObjectProperty) prop;
						ret.add(namedProp.getIRI().toURI());
					}
				}
				
			}
		}
		return ret;
	}
	/**
	 * return named properties p such that c \subseteq \some inv(p) T
	 * @param c
	 * @return
	 */
	protected Set<URI> getNamedInverseSuperPropertiesOrSelf(OWLClassExpression c) {
		Set<OWLClassExpression> subs = taxo.getAllSubsumers(c);
		Set<URI> ret = new HashSet<URI>();
		for (OWLClassExpression ex: subs) {
			if (ex.getClassExpressionType().equals(ClassExpressionType.OBJECT_SOME_VALUES_FROM)) {
				OWLObjectSomeValuesFrom rest = (OWLObjectSomeValuesFrom) ex;
				OWLObjectPropertyExpression prop = rest.getProperty().getSimplified();
				if (prop.isAnonymous()) {
					OWLObjectProperty namedProp = prop.getNamedProperty();
					ret.add(namedProp.getIRI().toURI());
				}
				
			}
		}
		return ret;
		
	}
	/**
	 * returns BOUND(var, val1, ..., valN);
	 * @param var
	 * @param values
	 * @return
	 */
	
	protected AtomicFormula createBoundFormula(VariableExpr var, Set<? extends Object> values ) {
		assert values.size()> 0;
		BoundVariablePredicate p = new BoundVariablePredicate(1+values.size());
		List<Expr> args = new LinkedList<Expr>();
		args.add(var);
		for (Object o :values) {
			args.add(new ConstantExpr(o));
		}
		return new AtomicFormula(p, args);
		
	}
	protected  boolean isConceptPropertyWildcard(AtomicFormula af) {
		return af.getPredicate().getArity() == 3
			&& af.getArguments().get(1).isVariable()
			&& af.getArguments().get(2).isVariable();
	}
	protected boolean isConceptWildcard(AtomicFormula af) {
		return af.getPredicate().getArity() == 3
			&& af.getArguments().get(1).isConstant() 
			&& RDF.type.getURI().equals((((ConstantExpr)af.getArguments().get(1)).getValue().toString()))
			&& af.getArguments().get(2).isVariable();
	}
	protected  boolean isPropertyWildcard(AtomicFormula af) {
		Expr e;
		if( af.getPredicate().getArity() == 3
			&& af.getArguments().get(1).isVariable()
			&& (e=af.getArguments().get(2)).isConstant()){
				ConstantExpr ce =(ConstantExpr) e;
				if (ce.getValue() instanceof URI) {
					if (tbox.getNormalizedOntology().getClassesInSignature().contains(tbox.getFactory().getOWLClass(IRI.create(ce.getValue().toString())))) {
						assert false : af ; // assuming renaming of af to C(x) has already been done in the renaming step
						return false;
					} else {
						return true;
					}
				} else {
					// not a uri so cannot be a type
					return true;
				}
		}
		return false;
	}
}
