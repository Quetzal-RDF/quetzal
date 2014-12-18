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
package com.ibm.research.owlql.rule;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




public class Rule {

	private static final Logger logger = LoggerFactory.getLogger(Rule.class);
	public final static  String BUILT_IN_DIFF ="~=";
	public final static  String BUILT_IN_EQUAL ="=";
	public final static  String BUILT_IN_IRI_DIFF ="IRI!=";
	public final static  String BUILT_IN_IRI_EQUAL ="IRI=";
	
	public final static  String BUILT_IN_LESSTHAN ="<";
	public final static  String BUILT_IN_LESSTHAN_OR_EQUAL ="<=";
	public final static  String BUILT_IN_GREATERTHAN =">";
	public final static  String BUILT_IN_GREATERTHAN_OR_EQUAL =">=";
	public final static  String BUILT_IN_UNBOUNDVAR ="NILLVAR";
	/**Predicate whose first argument is a variable and the others arguments indicate the values
	 * that this variable can take. 
	 * @see BoundVariablePredicate
	 */
	public final static String BUILT_IN_BOUND_VAR ="boundVar";
	public final static Set<String> BUILT_IN_PREDICATES;
	static {
		Set temp = new HashSet<String>();
		temp.add(BUILT_IN_IRI_DIFF);
		temp.add(BUILT_IN_IRI_EQUAL);
		temp.add(BUILT_IN_EQUAL);
		temp.add(BUILT_IN_LESSTHAN);
		temp.add(BUILT_IN_LESSTHAN_OR_EQUAL);
		temp.add(BUILT_IN_GREATERTHAN);
		temp.add(BUILT_IN_GREATERTHAN_OR_EQUAL);
		temp.add(BUILT_IN_DIFF);
		temp.add(BUILT_IN_UNBOUNDVAR);
		temp.add(BUILT_IN_BOUND_VAR);
		BUILT_IN_PREDICATES = Collections.unmodifiableSet(temp);
	}
	public static boolean isBuiltInPredicate(Predicate p) {
		return BUILT_IN_PREDICATES.contains(p.getName());
	}
	/**
	 * returns all unbound variables (ie. variables that are not in the head and appear at most once).
	 * Variables appear in the same order as in the body
	 * @param q
	 * @return
	 */
	public  static List<VariableExpr> getUnboundVariables(AtomicFormula head, List<AtomicFormula> body) {
		Map<VariableExpr, Integer> var2Occurrence = new HashMap<VariableExpr, Integer>();
		List<VariableExpr> ret = new ArrayList<VariableExpr>();
		for (AtomicFormula af: body) {
			for (VariableExpr var: af.getAllVariables()) {
				Integer prevOcc = var2Occurrence.get(var);
				if (prevOcc ==null) {
					prevOcc = 0;
					ret.add(var);
				}
				prevOcc++;
				var2Occurrence.put(var, prevOcc);
			}
		}
		Set<VariableExpr> headVars =head.getAllVariables();
		
		//var2Occurrence.keySet().removeAll(q.getProjectedVariables());
		for (VariableExpr pv : headVars) {
			ret.remove(pv);
		}
		for (Map.Entry<VariableExpr, Integer> e: var2Occurrence.entrySet()) {
			if (e.getValue() != 1) {
				ret.remove(e.getKey());
			}
		}
		return ret;
	}
	public static List<VariableExpr> getVariablesOccurringMoreThanOnce(List<AtomicFormula> body) {
		List<VariableExpr> ret = new ArrayList<VariableExpr>();
		Set<VariableExpr> tmpset = new HashSet<VariableExpr>();
		for (AtomicFormula af : body) {
			for (Expr e : af.getArguments()) {
				if (e.isVariable() && !tmpset.add(e.asVariable())) ret.add(e.asVariable()); 
			}
		}
		return ret;
	}
	private AtomicFormula head;
	private List<AtomicFormula> body;
	private int id;
	public Rule(AtomicFormula head,int ruleid, AtomicFormula... bodyFormulas) {
		this(head, Arrays.asList(bodyFormulas), ruleid);
	}
	public Rule(AtomicFormula head, List<AtomicFormula> body, int ruleid) {
		super();
		this.head = head;
		this.body = body;
		this.id = ruleid;
		//TODO: check that mandatory formulas do not appear after optional ones.
	}
	public Rule clone() {
		List<AtomicFormula> bd = new ArrayList<AtomicFormula>(body.size());
		for (Iterator<AtomicFormula> it=body.iterator();it.hasNext();) {
			AtomicFormula f = it.next();
			bd.add(f.clone());
		}
		return new Rule(head.clone(), bd,id);
	}

	public AtomicFormula getHead() {
		return head;
	}

	/*public void setHead(AtomicFormula head) {
		this.head = head;
	}*/

	public List<AtomicFormula> getBody() {
		return body;
	}
	
	/**
	 * returns non-optional atomic formula in the body of the rule
	 * @return
	 */
	public List<AtomicFormula> getMandatoryBody() {
		List<AtomicFormula> ret = new ArrayList<AtomicFormula>();
		for (AtomicFormula af: body) {
			if (!af.getPredicate().isOptional()) {
				ret.add(af);
			}
		}
		return ret;
	}
	
	public int getLastMandatoryFormulaPosition() {
		int ret =0;
		for (int i=0;i<body.size();i++) {
			AtomicFormula af= body.get(i);
			if (!af.getPredicate().isOptional())  {
				ret = i;
			}
		}
		return ret;
	}
	/**
	 * returns optional atomic formula in the body of the rule
	 * @return
	 */
	public List<AtomicFormula> getOptionalBody() {
		List<AtomicFormula> ret = new ArrayList<AtomicFormula>();
		for (AtomicFormula af: body) {
			if (af.getPredicate().isOptional()) {
				ret.add(af);
			}
		}
		return ret;
	}
	/**
	 * each variable in the returned list appears only once. Furthermore, variables 
	 * appears in the same order as in the rule. 
	 * 
	 * @return
	 */
	public List<VariableExpr> getAllRuleVariables() {
		Set<VariableExpr> ruleVars = new HashSet<VariableExpr>();
		List<AtomicFormula> ruleAFs = new  ArrayList<AtomicFormula>();
		List<VariableExpr>  ret = new ArrayList<VariableExpr> ();
		ruleAFs.add(head);
		for (Iterator<AtomicFormula> fs= body.iterator(); fs.hasNext();) {
			ruleAFs.add(fs.next());
		}
		for (Iterator<AtomicFormula> fs= ruleAFs.iterator();fs.hasNext(); ) {
			for (Iterator<? extends Expr> exps= fs.next().getArguments().iterator();exps.hasNext();) {
				Expr e = exps.next();
				if (e.isVariable() && !ruleVars.contains(e)) {
					ruleVars.add((VariableExpr)e);
					ret.add((VariableExpr)e);
				} else if (!e.isVariable()) {
					assert e.isConstant(): "Only variable and constant expressions are supported!";
				}
			}
		}
		return ret;
	}
	/**
	 * returns all unbound variables (ie. variables that are not in the head and appear at most once).
	 * Variables appear in the same order as in the body
	 * @param q
	 * @return
	 */
	public   List<VariableExpr> getUnboundVariables() {
		return getUnboundVariables(getHead(), getBody());
	}
	public List<VariableExpr> getJoinVariables() {
		return getVariablesOccurringMoreThanOnce(getBody());
	}
	public boolean equals(Object o) {
		if (o==this) {
			return true;
		} 
		if (o.getClass().equals(getClass())) {
			Rule other = (Rule) o;
			return head.equals(other.head)
				   && body.equals(other.body);
		}
		return false;
	}
	
	public int hashCode() {
		return head.hashCode()+31*body.hashCode();
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		//buf.append("# Rule id = "+ id+"\n" );
		buf.append(head);
		if (!body.isEmpty()) {
			buf.append(" :- ");
			for (Iterator<AtomicFormula> it=body.iterator();it.hasNext();) {
				AtomicFormula f = it.next();
				buf.append(f);
				if (it.hasNext()) {
					buf.append(" ^ ");
				}
			}
		}
		
		return buf.toString();
	}
	
	public static Rule parse(String s, int id) throws ParseException {
		s=s.trim();
		// logger.info("Rule: {}",s);
		int entailSymb = s.indexOf(":-");
		
		if (entailSymb==0) {
			throw new ParseException("':-' is not allowed at the beginning of a rule: "+s,0);
		}
		AtomicFormula headF;
		if (entailSymb<0) {
			headF = AtomicFormula.parse(s);
			
			//throw new ParseException("Missing ':-' in rule: "+s,0);
			return new Rule(headF, new ArrayList<AtomicFormula>(0),id);
		}
		String head = s.substring(0,entailSymb);
		headF = AtomicFormula.parse(head);
		
		if (entailSymb+3>= s.length()) {
			throw new ParseException("':-' is not allowed at the end of a rule: "+s,0);
			
		}
		String body = s.substring(entailSymb+3);
		StringTokenizer tok = new StringTokenizer(body, "^");
		List<AtomicFormula> bodyAsAF = new ArrayList<AtomicFormula>();
		while (tok.hasMoreTokens()) {
			String f = tok.nextToken();
			bodyAsAF.add(AtomicFormula.parse(f));
		}
		return new Rule(headF, bodyAsAF,id);
	}

	public int getId() {
		return id;
	}
	public Set<Predicate> getAllPredicates() {
		Set<Predicate> ret = new HashSet<Predicate>(body.size()+1);
		ret.add(head.getPredicate());
		for (Iterator<AtomicFormula> it = body.iterator();it.hasNext();) {
			AtomicFormula f =it.next();
			ret.add(f.getPredicate());
		}
		return ret;
	}
	

	public Set<Predicate> getAllBodyPredicates() {
		Set<Predicate> ret = new HashSet<Predicate>(body.size());
		for (Iterator<AtomicFormula> it = body.iterator();it.hasNext();) {
			AtomicFormula f =it.next();
			ret.add(f.getPredicate());
		}
		return ret;
	}
	public List<Predicate> getAllBodyPredicatesAllowMultiple() {
		List<Predicate> ret = new ArrayList<Predicate>();
		for (Iterator<AtomicFormula> it = body.iterator();it.hasNext();) {
			AtomicFormula f =it.next();
			ret.add(f.getPredicate());
		}
		return ret;
	}
	
	public Set<VariableExpr> getVariablesInOrdinaryPredicates() {
		Set<VariableExpr> ret = new HashSet<VariableExpr>();
		for (Iterator<AtomicFormula> it=body.iterator();it.hasNext();) {
			AtomicFormula f=it.next();
			if (!BUILT_IN_PREDICATES.contains(f.getPredicate().getName())) {
				ret.addAll(f.getAllVariables());
			}
		}
		return ret;
	}
	
	public Set<VariableExpr> getVariablesInBuiltInPredicates() {
		Set<VariableExpr> ret = new HashSet<VariableExpr>();
		for (Iterator<AtomicFormula> it=body.iterator();it.hasNext();) {
			AtomicFormula f=it.next();
			if (BUILT_IN_PREDICATES.contains(f.getPredicate().getName())) {
				ret.addAll(f.getAllVariables());
			}
		}
		return ret;
	}
	
	public boolean isUnsatisfiableBasedOnEquilityBetweenDifferentConstants() {
		return !computeVariableEquivalenceClass(new HashMap<VariableExpr, Set<VariableExpr>>(), new HashMap<VariableExpr, ConstantExpr>());
	}
	/** 
	 * This methods iterates over formulas of the form  X=Y and X = const, where X and Y are variables and const is
	 * a constant, and computes the equivalence class of each variable and the mapping from variable to constant.
	 * It returns false if a variable is found to value two distinct values or if a formula of the form const1 = const2 
	 * is found (where const1 and const2 are two distinct constants); otherwise it returns true. Note: the update to the 
	 * two maps is only meaningful when the return value is true.
	* */
	protected boolean computeVariableEquivalenceClass(	Map<VariableExpr, Set<VariableExpr>> var2IdenticalVars,
			Map<VariableExpr, ConstantExpr> var2Constant) {
		// Iterate over formulas of the form f(X,Y), where f is a built-in predicate
		// and X and Y are variables, to compute the equivalence class of each variable
		for (Iterator<AtomicFormula> it=getBody().iterator();it.hasNext();) {
			AtomicFormula f = it.next();
			if (!Rule.BUILT_IN_EQUAL.equals(f.getPredicate().getName())) {
				//throw new RuntimeException("= is the only supported built in predicate");
				continue;
			}
			assert f.getPredicate().getArity() == 2;
			Expr arg1 = f.getArguments().get(0);
			Expr arg2 = f.getArguments().get(1);
			VariableExpr var1 = null ;
			VariableExpr var2 = null;
			Set<VariableExpr> equiv1 = null;
			Set<VariableExpr> equiv2 = null;
			if (arg1.isVariable()) {
				var1 = (VariableExpr) arg1;
				equiv1= var2IdenticalVars.get(var1); 
				if (equiv1==null) {
					equiv1= new  HashSet<VariableExpr>();
					var2IdenticalVars.put(var1, equiv1);
				}
				equiv1.add(var1);
			}
			if (arg2.isVariable()) {
				var2 = (VariableExpr) arg2;
				equiv2= var2IdenticalVars.get(var2); 
				if (equiv2==null) {
					equiv2= new  HashSet<VariableExpr>();
					var2IdenticalVars.put(var2, equiv2);
				}
				equiv2.add(var2);
			}
 			if (arg1.isConstant() || arg2.isConstant()) {
 				
				if (arg1.isVariable()) {
					assert var1!=null: arg1;
					ConstantExpr old = var2Constant.put(var1,(ConstantExpr)arg2 );
					if (old!=null && !old.equals(arg2)) {
						// two different values for the same variable
						return false;
					}
				} else if (arg2.isVariable()) {
					assert var2!=null: arg2;
					ConstantExpr old = var2Constant.put(var2, (ConstantExpr)arg1);
					if (old!=null && !old.equals(arg1)) {
						// two different values for the same variable
						return false;
					}
				} else if (!arg1.equals(arg2)) {
					// equality between two different constants
					return false;
				}
				
			} else {
				equiv1.add(var2);
				equiv2.add(var1);
				
			}
		}
		closureEquivalentClass(var2IdenticalVars);
		for (Map.Entry<VariableExpr, Set<VariableExpr>> e:var2IdenticalVars.entrySet()) {
			VariableExpr var = e.getKey();
			// find equivalent variables with constant values
			for (VariableExpr equivVar : e.getValue()) {
				ConstantExpr val = var2Constant.get(equivVar);
				if (val!=null) {
					ConstantExpr old = var2Constant.put(var, val);
					if (old!=null && !old.equals(val)) {
						// two different values for the two equivalent variables
						return false;
					}
				}
			}
				
		}
		//
		return true;
	}
	
	protected  static void closureEquivalentClass(Map<VariableExpr, Set<VariableExpr>> var2IdenticalVars) {
		Set<VariableExpr> updated = new HashSet<VariableExpr>(var2IdenticalVars.keySet());
		while (!updated.isEmpty()) {
			VariableExpr var =updated.iterator().next();
			updated.remove(var);
			Set<VariableExpr> equiv = var2IdenticalVars.get(var);
			equiv.add(var);
			for (Iterator<VariableExpr>  it= equiv.iterator();it.hasNext();) {
				VariableExpr equivVar = it.next();
				Set<VariableExpr> setToUpdate = var2IdenticalVars.get(equivVar);
				if (setToUpdate == null) {
					setToUpdate = new HashSet<VariableExpr>();
					var2IdenticalVars.put(equivVar, setToUpdate);
				}
				if (setToUpdate.addAll(equiv)) {
					updated.add(equivVar);
				}
			}
		}
	}


}