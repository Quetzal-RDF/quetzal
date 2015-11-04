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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLQuantifiedRestriction;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyAxiom;
import org.semanticweb.owlapi.rdf.util.RDFConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.expr.E_Equals;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.ExprVar;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;
import com.ibm.research.owlql.rule.RuleSystem;


/**
 * Given a SPARQL conjunctive query, this OWLQL compiler reformulates it into a set of new conjunctive queries
 * to take into account relevant Tbox axioms
 * @author achille
 *
 */
public class OWLQLCompiler implements IOWLQLCompiler {

	private static final Logger logger = LoggerFactory.getLogger(OWLQLCompiler.class);
	public static final String UNBOUND_VARIABLE_PREFIX="unbound_";
	
	
	protected NormalizedOWLQLTbox tbox;
	protected Taxonomy taxo;
	public OWLQLCompiler(OWLOntology originalOntTbox) {
		super();
		tbox = new NormalizedOWLQLTbox(NormalizedOWLQLTbox.getTboxRboxAxioms(originalOntTbox.getAxioms()));
	}
	/* (non-Javadoc)
	 * @see com.ibm.research.owlql.IOWLQLComipler#getTBox()
	 */
	public NormalizedOWLQLTbox getTBox() {
		return tbox;
	}
	protected String getKey(Triple t) {
		Node subj = t.getSubject();
		Node obj = t.getObject();
		Node pred = t.getPredicate();
		if (!pred.isURI()) {
			return null;
		}
		if (!subj.isVariable() && !obj.isVariable()) {
			return null;
		}
		if (pred.getURI().equals(RDFConstants.RDF_TYPE)) {
			return obj.isURI()? obj.getURI() : null;
		} else {
			return pred.getURI();
		}
		
	}
	
	protected Triple basicRewrite(Triple t, OWLSubClassOfAxiom ax, String key, NewVariableGenerator varGen) {
		assert key.equals(tbox.getKey(ax.getSuperClass())) : key +"\n"+ ax;
		assert key.equals(getKey(t)) : key+"\n"+t;
		Node subj = t.getSubject();
		Node obj = t.getObject();
		Node pred = t.getPredicate();
		assert pred.isURI() : t;
		//assert subj.isVariable() || obj.isVariable() : t;
		OWLClassExpression sub = ax.getSubClass();
		OWLClassExpression sup = ax.getSuperClass();
		if (pred.getURI().equals(RDFConstants.RDF_TYPE)) {
			assert obj.isURI() : t;
			Triple newTriple = toTriple(sub, subj, varGen);
			return newTriple;
		} else {
			OWLQuantifiedRestriction rest = (OWLQuantifiedRestriction) sup;
			OWLPropertyExpression pe = rest.getProperty();
			if (pe.isObjectPropertyExpression()) {
				pe = ((OWLObjectPropertyExpression) pe).getSimplified();
			}
			if (isUnbound(obj)) {
				// pred(x, _)
				//check that sup = some(pred, top)
				if (!pe.isAnonymous()) {
					OWLProperty p = (OWLProperty) pe;
					assert pred.getURI().equals(p.getIRI().toString()) : t+"\n"+ax+"\n"+key;
					Triple newTriple = toTriple(sub, subj, varGen);
					return newTriple;
				} 
			} else if (	isUnbound(subj)) {
				//pred(_,x)
				// check that sup = some(inv(pred), top)
				if (pe.isAnonymous()) {
					OWLObjectInverseOf oi = (OWLObjectInverseOf) pe;
					OWLObjectProperty p = (OWLObjectProperty) oi.getInverse();
					assert pred.getURI().equals(p.getIRI().toString()) : t+"\n"+ax+"\n"+key;
					Triple newTriple = toTriple(sub, obj, varGen);
					return newTriple;
				}
			}
		}
		return null;
	}
	protected OWLPropertyExpression getSimplified(OWLPropertyExpression pe) {
		return NormalizedOWLQLTbox.getSimplified(pe);
	}
	protected Triple basicRewrite(Triple t, OWLSubPropertyAxiom ax, String key, NewVariableGenerator varGen) {
		assert key.equals(tbox.getKey(ax.getSuperProperty())) : key +"\n"+ ax;
		assert key.equals(getKey(t)) : key+"\n"+t;
		Node subj = t.getSubject();
		Node obj = t.getObject();
		Node pred = t.getPredicate();
		assert pred.isURI() : t;
		assert subj.isVariable() || obj.isVariable() : t;
		OWLPropertyExpression sub = getSimplified(ax.getSubProperty());
		OWLPropertyExpression sup = getSimplified(ax.getSuperProperty());
		if (!pred.getURI().equals(RDFConstants.RDF_TYPE)) {
				OWLProperty subp;
				if (sub.isAnonymous()) {
					OWLObjectInverseOf inv =(OWLObjectInverseOf) sub;
					subp = (OWLProperty) inv.getInverse();
				} else {
					subp = (OWLProperty)  sub;
				}
				// same polarity?
				if ((sub.isAnonymous() && sup.isAnonymous())
				|| (!sub.isAnonymous() && !sup.isAnonymous())) {
					return toTriple(subp, subj, obj, varGen);
				} else {
					return toTriple(subp, obj, subj, varGen);
				}
		}
		return null;		
	}
	
	protected ConjunctiveQuery replace(ConjunctiveQuery q, Triple oldT, Triple newT) {
		ElementTriplesBlock newPattern = new ElementTriplesBlock();
		for (Triple t:q.getTriples()) {
			if (t.equals(oldT)) {
				newPattern.addTriple(newT);
			} else {
				newPattern.addTriple(t);
			}
		}
		ConjunctiveQuery ret = q.cloneConjQuery();
		ret.setQueryPattern(newPattern);
		for (ElementFilter ef: q.getFilters()) {
			ret.addFilter(ef.getExpr());
		}
		return ret;
	}
	
	
	
	/**
	 * returns a query triple corresponding to a property expression
	 * 	<ul>
	 * 	<li> R  --> x R y </li>
	 *  <li> inv(R) --> y R x </li>
	 * </ul>
	 * 
	 */
	protected Triple toTriple(OWLPropertyExpression pe, Node x, Node y, NewVariableGenerator varGen) {
		return NormalizedOWLQLTbox.toTriple(pe, x, y, varGen);
	}
	/**
	 *  returns a query triple corresponding to a left hand side concept.
	 *  <ul>
	 *   <li> B --> x rdf:type B </li>
	 *   <li> some(R, top) --> x R ?y </li>
	 *   <li> some(inv(R), top) --> ?y R x </li>
	 *   </ul>
	 * @param lhsConcept
	 * @param var
	 * @return
	 */
	protected Triple toTriple(OWLClassExpression lhsConcept, Node var, NewVariableGenerator varGen) {
		return NormalizedOWLQLTbox.toTriple(lhsConcept, var, varGen, tbox.getFactory());
		
	}
	protected boolean hasTripleAbsentFromAbox(ConjunctiveQuery cq) {
		for (Triple qt: cq.getTriples()) {
			if (tbox.isTripleAbsentFromAbox(qt)) {
				return true;
			}
		}
		return false;
	}
	protected  void removeQueryWithGeneratedTerms(Set<ConjunctiveQuery> q) {
		Set<ConjunctiveQuery> toRemove = new HashSet<ConjunctiveQuery>();
		for (ConjunctiveQuery cq : q) {
			if (hasTripleAbsentFromAbox(cq)) {
				toRemove.add(cq);
			}
		}
		int removed = toRemove.size();
		logger.debug("Number of removed rewritten queries: "+removed+" ("+(((float)removed)/q.size())+")%");
		q.removeAll(toRemove);
	}
	
	protected Set<Triple> singleStepTriplesSubstitution(Triple t, NewVariableGenerator varGen ) {
		Set<Triple> ret = new HashSet<Triple>();
		String key = getKey(t);
		if (key!=null) {
			Set<OWLSubClassOfAxiom> candAxs = tbox.getPositiveInclusions(key);
			if (candAxs!=null) {
				for (OWLSubClassOfAxiom ca: candAxs) {
					Triple newT = basicRewrite(t, ca, key,varGen);
					if (newT!=null) {
						ret.add(newT);
					}
				}
			}
			Set<OWLSubPropertyAxiom> candAxs2 = tbox.getPositivePropertyInclusions(key);
			if (candAxs2!=null) {
				for (OWLSubPropertyAxiom pa: candAxs2) {
					Triple newT = basicRewrite(t, pa, key,varGen);
					if (newT!=null) {
						ret.add(newT);
					}
				}
			}
		}
		return ret;
	}
	
	protected Set<Triple> getRecursiveTriplesSubstitution(Triple t, NewVariableGenerator varGen, 
			boolean allowTriplesAbsentFromAbox, Map<Triple,	Set<Triple>> triple2Results  ) {
		return computeRecursiveTriplesSubstitution(t, varGen, allowTriplesAbsentFromAbox,  new HashSet<Triple>(), triple2Results);
		
	}
	
	protected Set<Triple> computeRecursiveTriplesSubstitution(Triple t, NewVariableGenerator varGen, boolean allowTriplesAbsentFromAbox,
				 Set<Triple> alreadyFound, Map<Triple,	Set<Triple>> triple2Results ) {
		alreadyFound.add(t);
		
		Set<Triple> results = triple2Results.get(t);
		if (results!=null) {
			return results;
		}
		results = new HashSet<Triple>();
		Set<Triple> ts = singleStepTriplesSubstitution(t, varGen);
		for (Triple nt : ts) {
			if (allowTriplesAbsentFromAbox || !tbox.isTripleAbsentFromAbox(nt)) {
				results.add(nt);
			}
		}
		for (Triple nt:ts) {
			if (!alreadyFound.contains(nt)) {
				results.addAll(computeRecursiveTriplesSubstitution(nt, varGen, allowTriplesAbsentFromAbox, alreadyFound,triple2Results));
			}
		}
		if (allowTriplesAbsentFromAbox || !tbox.isTripleAbsentFromAbox(t)) {
			triple2Results.put(t,results);
		}
		return results;
	}
	/* (non-Javadoc)
	 * @see com.ibm.research.owlql.IOWLQLComipler#compile(com.ibm.research.owlql.ConjunctiveQuery)
	 */
	public Set<ConjunctiveQuery> compile(ConjunctiveQuery q) {
		q = renameUnboundVariables(q);
		int suffixStart = nextAvailableNumSuffixForUnboundVariable(q);
		NewVariableGenerator varGen = new NewVariableGenerator(UNBOUND_VARIABLE_PREFIX, suffixStart);
		Set<ConjunctiveQuery> res = new HashSet<ConjunctiveQuery>();
		Set<ConjunctiveQuery> newCqs = new HashSet<ConjunctiveQuery>();
		newCqs.add(q);
		res.addAll(newCqs);		
		Map<Triple, Set<Triple>> triple2TripleSubstitutions = new HashMap<Triple, Set<Triple>>();
		while (!newCqs.isEmpty()) {
			Set<ConjunctiveQuery> prevNewCqs = new HashSet<ConjunctiveQuery>(newCqs);
			newCqs.clear();
			for (ConjunctiveQuery cq: prevNewCqs) {
				for (Triple t: new HashSet<Triple>(cq.getTriples())) {
					//
					Set<Triple> cqTriples = new HashSet<Triple>(cq.getTriples());
					cqTriples.remove(t);
					Set<String> unboundVars = new HashSet<String>(getValidNameForUnboundVariable(getAllVariables(cqTriples)));
					//
					Set<Triple> newTs = triple2TripleSubstitutions.get(t);
					if (newTs ==null) {
						newTs = getRecursiveTriplesSubstitution(t, varGen, false,triple2TripleSubstitutions);//singleStepTriplesSubstitution(t, varGen);//
						triple2TripleSubstitutions.put(t, newTs);
					}
					for (Triple newT: newTs) {
						// rename to avoid unbound variable capture
						newT = renameTripleToAvoidUnboundVarCapture(newT, unboundVars, varGen);
						//
						ConjunctiveQuery newCq = replace(cq, t, newT);
						if (!res.contains(newCq)) {
							newCqs.add(newCq);
						}
					}
				}
				//unification of terms
				List<Triple> triples = cq.getTriples();
				for (int i=0;i<triples.size();i++) {
					for (int j=i+1;j<triples.size();j++) {
						ConjunctiveQuery newCq = unify(triples.get(i), triples.get(j), cq);
						if (newCq!=null) {
							newCq = renameUnboundVariables(newCq, varGen);
							if (!res.contains(newCq)) {
								newCqs.add(newCq);
							}
						}
					}
				}
				//
			}
			res.addAll(newCqs);			
		}
		removeQueryWithGeneratedTerms(res);
 		return res;
	}
	
	public boolean canCompileToRules() {
		return false;
	}
	public boolean canCompileToSPARQL() {
		return true;
	}
	public boolean canCompileToSQL(boolean returnIDs) {
		return false;
	}
	public RuleSystem compileToRules(ConjunctiveQuery q) {
		return null;
	}
	public String compileToSQL(ConjunctiveQuery q, boolean returnIDs) {
		return null;
	}
	
			
	@Override
	public Query compileToUnionQuery(ConjunctiveQuery q) {
		return null;
	}
	@Override
	public boolean canCompileToUnionSPARQL() {
		return false;
	}
	/**
	 * Returns the result of the unification of two triples if they unify; otherwise, returns <code>null</code>.
	 * This assumes unbounded query are already marked unbounded. 
	 * 
	 * @param t1 a triple in the conjunctive query q s.t t1 != t2
	 * @param t2 a triple in the conjunctive query q s.t t1 != t2
	 * @param q a conjunctive query 
	 * @return
	 */
	protected ConjunctiveQuery unify(Triple t1, Triple t2, ConjunctiveQuery q) {
		assert q.getTriples().contains(t1) : t1+"\n"+q;
		assert q.getTriples().contains(t2) : t2+"\n"+q;
		
		Node pred1 = t1.getPredicate();
		Node pred2 = t2.getPredicate();
		
		assert pred1.isURI() : pred1;
		assert pred2.isURI() : pred2;
		if (!pred1.equals(pred2)) {
			return null;
		}
		assert pred1.isURI() : pred1;
		Map<Node, Node> old2New = new HashMap<Node, Node>();
		Node obj1 = t1.getObject();
		Node obj2 = t2.getObject();
		Node sub1 = t1.getSubject();
		Node sub2 = t2.getSubject();
		
		if (RDFConstants.RDF_TYPE.equals(pred1.getURI())) {
			// typeOf assertion
			if (!obj1.equals(obj2)) {
				return null;
			}
			assert obj1.isURI() : t1;
		} else {
			if (!unify(obj1, obj2, old2New)) {
				return null;
			}
		}
		if (!unify(sub1, sub2, old2New)) {
			return null;
		}
		
		// unification successful. 
		// now perform substitution based on old2New map
		
		ElementTriplesBlock newPattern = new ElementTriplesBlock();
		for (Triple t:q.getTriples()) {
			if (t==t2) {
				//t2 is removed as it unifies with t1
				continue;
			} 
			Node sub = t.getSubject();
			Node pred = t.getPredicate();
			Node obj = t.getObject();
			assert pred.isURI() : t;
			
			Node newSub = old2New.get(sub);
			newSub = (newSub == null? sub : newSub);
			Node newObj = old2New.get(obj);
			newObj = (newObj == null? obj : newObj);
			
			Triple newT = new Triple(newSub, pred, newObj);
			newPattern.addTriple(newT);
		}
		
		ConjunctiveQuery ret = q.cloneConjQuery();
		ret.setQueryPattern(newPattern);
		
		//update projected variables
		for (String var: q.getResultVars()) {
			Node tvar = Node.createVariable(var);
			Node newTvar = old2New.get(tvar);
			if (newTvar == null ) {
				continue;
			}
			if (newTvar.isVariable() && newTvar.getName().equals(var)) {
				continue;
			}
			
			newTvar= (newTvar == null ? tvar: newTvar);
			
			ExprVar left = new ExprVar(var);
			Expr right;
			if (newTvar.isVariable()) {
				right = new ExprVar(newTvar.getName());
			} else 	if (newTvar.isLiteral()) {
				right = NodeValue.makeNode(newTvar);
			} else {
				assert  newTvar.isURI() : newTvar;
				right = NodeValue.makeNode(newTvar);
			}
			ret.addFilter(
					new E_Equals(left, right));
			
		}
		return ret;
	}
	
	private  boolean unifyT1Variable(Node t1,Node  t2, Map<Node, Node> old2New) {
		assert t1.isVariable() : t1;
		if (!t2.isVariable()) {
			 old2New.put(t1, t2);
		 } else if (!isValidNameForUnboundVariable(t1.getName())) {
			 // t1  bound
			 old2New.put(t2, t1);
		 } else {
			 // t1 is unbound
			 old2New.put(t1, t2);
		 }
		return true;
	}
	protected boolean unify(Node t1, Node t2, Map<Node, Node> old2New) {
		if (t1.isVariable()) {
			return unifyT1Variable(t1, t2, old2New);
		} else if (t2.isVariable()) {
			return unifyT1Variable(t2,t1, old2New);
		} else {
			// i.e., t1 and t2 are not variables
			return t1.equals(t2);
		}
	}
	public static ConjunctiveQuery renameUnboundVariables(ConjunctiveQuery conjQuery, NewVariableGenerator varGen) {
		List<String> unboundVars = getUnboundVariables(conjQuery);
		List<Triple> newTriples = new ArrayList<Triple>();
		Map<String, String> old2New = new HashMap<String, String>();
		for (Triple triple: conjQuery.getTriples()) {
			Node subj = triple.getSubject();
			Node obj = triple.getObject();
			Node[] subjObj = new Node[]{subj, obj};
			Node pred = triple.getPredicate();
			assert pred.isURI() : "predicate must be an IRI: "+ triple;
			Node newSubj = subj;
			Node newObj = obj;
			for (int i=0;i<2;i++) {
				Node t = subjObj[i];
				if (t.isVariable()) {
					String var = t.getName();
					if (unboundVars.contains(var)
					&& !isValidNameForUnboundVariable(var)) {
						String newVar = varGen.createNewVariable();
						if (i==0) {
							newSubj = Node.createVariable(newVar);
						} else {
							newObj = Node.createVariable(newVar);
						}
					}
				}
			}
			Triple newTriple = new Triple(newSubj, pred, newObj);
			if (newSubj!=subj && subj.isVariable()) {
				old2New.put(subj.getName(), newSubj.getName());
			}
			if (newObj!=obj && obj.isVariable()) {
				old2New.put(obj.getName(), newObj.getName());
			}
			newTriples.add(newTriple);
		}
		ConjunctiveQuery ret =conjQuery.cloneConjQuery(false);
		ElementTriplesBlock newPattern = new ElementTriplesBlock();
		for (Triple t: newTriples) {
			newPattern.getPattern().add(t);
		}
		
		
		ret.setQueryPattern(newPattern);
		//Perform renaming on filters
		for (ElementFilter ef: conjQuery.getFilters()) {
			assert  (ef.getExpr() instanceof E_Equals ): ef;
			E_Equals e = (E_Equals) ef.getExpr();
			Expr left = e.getArg1();
			Expr right = e.getArg2();
			Expr newLeft = left;
			Expr newRight = right;
			for (int i=0;i<2;i++) {
				Expr expr = i==0? left: right;
				Expr newExpr = null;
				if (expr instanceof ExprVar) {
					ExprVar evar = (ExprVar) expr;
					String newVar = old2New.get(evar.getVarName());
					if (newVar != null && !newVar.equals(evar.getVarName())) {
						newExpr = new ExprVar(newVar);
					}
				}
				if (newExpr != null) {
					if (i==0) {
						newLeft = newExpr;
					} else {
						newRight = newExpr;
					}
				}
			}
			ret.addFilter(new E_Equals(newLeft, newRight));
		}
		//
		//ret.getDatasetClauses().addAll(conjQuery.getDatasetClauses());
		for (String oldV: conjQuery.getResultVars()) {
			String newV = old2New.get(oldV);
			newV = (newV==null? oldV:newV);
			ret.addResultVar(newV);
		}
		return ret;
	}
	public static ConjunctiveQuery renameUnboundVariables(ConjunctiveQuery conjQuery) {
		int suffixStart = nextAvailableNumSuffixForUnboundVariable(conjQuery);
		NewVariableGenerator varGen = new NewVariableGenerator(UNBOUND_VARIABLE_PREFIX, suffixStart);
		return renameUnboundVariables(conjQuery, varGen);
		
	}
	
	protected boolean isUnbound(Node t) {
		return t.isVariable() && isValidNameForUnboundVariable(t.getName());
	}
	
	public static boolean isValidNameForUnboundVariable(String st) {
		if (st.startsWith(UNBOUND_VARIABLE_PREFIX)
		&& st.length()>UNBOUND_VARIABLE_PREFIX.length()) {
			String suffix = st.substring(UNBOUND_VARIABLE_PREFIX.length());
			try {
				int suffixInt = Integer.parseInt(suffix);
				return true;
			} catch (NumberFormatException e) {
				
			}
		}
		return false;
	}
	protected static Set<String> getAllVariables(Collection<Triple> triples) {
		Set<String> ret = new HashSet<String>();
		for (Triple t: triples) {
			Node s = t.getSubject();
			Node o = t.getObject();
			if (s.isVariable()) {
				ret.add(s.getName());
			}
			if (o.isVariable()) {
				ret.add(o.getName());
			}
		}
		return ret;
	}
	protected Set<String> getAllVariables(Triple t) {
		Set<String> ret = new HashSet<String>();
		Node s = t.getSubject();
		Node o = t.getObject();
		if (s.isVariable()) {
			ret.add(s.getName());
		}
		if (o.isVariable()) {
			ret.add(o.getName());
		}
		return ret;
	}
	
	protected Set<String> getValidNameForUnboundVariable(Set<String> vars) {
		Set<String> ret = new HashSet<String>();
		for (String var: vars) {
			if(isValidNameForUnboundVariable(var)) {
				ret.add(var);
			}
		}
		return ret;
	}
	protected Triple renameTripleToAvoidUnboundVarCapture(Triple triple, Set<String> unboundVarsInConjQ, NewVariableGenerator varGen) {
		//Set<String> unboundVarsInQ = new HashSet<String>(getUnboundVariables(q));
		Set<String> clashingVars = getValidNameForUnboundVariable(getAllVariables(triple));
		clashingVars.retainAll(unboundVarsInConjQ);
		if (clashingVars.isEmpty()) {
			return triple;
		}
		Node subj = triple.getSubject();
		Node obj = triple.getObject();
		Node[] subjObj = new Node[]{subj, obj};
		Node pred = triple.getPredicate();
		assert pred.isURI() : "predicate must be an IRI: "+ triple;
		Node newSubj = subj;
		Node newObj = obj;
		for (int i=0;i<2;i++) {
			Node t = subjObj[i];
			if (t.isVariable()) {
				String var = t.getName();
				if (clashingVars.contains(var)) {
					assert isValidNameForUnboundVariable(var): var;
					String newVar = varGen.createNewVariable();
					if (i==0) {
						newSubj = Node.createVariable(newVar);
					} else {
						newObj = Node.createVariable(newVar);
					}
				}
			}
		}
		Triple newTriple = new Triple(newSubj, pred, newObj);
		return newTriple;
	}
	protected static int nextAvailableNumSuffixForUnboundVariable(ConjunctiveQuery q) {
		Set<String> vars = getAllVariables(q.getTriples());
		for (String pv: q.getResultVars()) {
			vars.add(pv);
		}
		int ret=-1;
		for (String var: vars) {
			String st = var;
			if (st.startsWith(UNBOUND_VARIABLE_PREFIX)
			&& st.length()>UNBOUND_VARIABLE_PREFIX.length()) {
				String suffix = st.substring(UNBOUND_VARIABLE_PREFIX.length());
				try {
					int suffixInt = Integer.parseInt(suffix);
					ret = Math.max(ret, suffixInt);
				} catch (NumberFormatException e) {
					
				}
			}
		}
		ret++;
		return ret;
	}
	/**
	 * returns all unbound variables (ie. variables that are not in the select clause and appear at most once).
	 * Variables appear in the same order as in the query
	 * @param q
	 * @return
	 */
	public static List<String> getUnboundVariables(ConjunctiveQuery q) {
		Map<String, Integer> var2Occurrence = new HashMap<String, Integer>();
		List<String> ret = new ArrayList<String>();
		for (Triple triple: q.getTriples()) {
			Node subj = triple.getSubject();
			Node obj = triple.getObject();
			Node[] subjObj = new Node[]{subj, obj};
			Node pred = triple.getPredicate();
			assert pred.isURI() : "predicate must be an IRI: "+ triple;
			for (Node t: subjObj) {
				if (t.isVariable()) {
					String var = t.getName();
					Integer prevOcc = var2Occurrence.get(var);
					if (prevOcc ==null) {
						prevOcc = 0;
						ret.add(var);
					}
					prevOcc++;
					var2Occurrence.put(var, prevOcc);
				}
			}
		}
		List<String> projectedVars = q.getResultVars();
		if (projectedVars==null || projectedVars.isEmpty()) {
			// this is "select * "
			// all variables are bound.
			return new ArrayList<String>();
		} else {
			//var2Occurrence.keySet().removeAll(q.getProjectedVariables());
			for (String pv : q.getResultVars()) {
				ret.remove(pv);
			}
			for (Map.Entry<String, Integer> e: var2Occurrence.entrySet()) {
				if (e.getValue() != 1) {
					ret.remove(e.getKey());
				}
			}
			return ret;
		}
		
		
	}
	@Override
	public ConsistencyCheckResult computeConsistencyCheck() {
		try {
			if (taxo == null) {
				taxo = new TaxonomyImpl(tbox);
			}
			return tbox.computeConsistencyCheck(taxo);
		} catch (OWLOntologyCreationException e) {
			throw new RuntimeException(e);
		}
	}
	
	
}
