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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * an atomic formula  used in a {@link Rule}.
 * @author <a href="mailto:achille@us.ibm.com">Achille Fokoue</a>
 *
 */
public class AtomicFormula {

	private Predicate predicate;	
	private List<? extends Expr> args;
	
	
	/*
	 * @param arguments
	 */
	public AtomicFormula(Predicate predicate, List<? extends Expr> arguments) {
		super();
		this.args = arguments;
		this.predicate =predicate;
		assert predicate.getArity()== arguments.size()
			: "Incorrect number of arguments for predicate "+predicate.getName()+" with arity "+predicate.getArity();
	}
	
	public AtomicFormula(Predicate predicate, Expr... arg) {
		this(predicate, Arrays.asList(arg));
	}
	/*public AtomicFormula(Predicate predicate, Expr arg1, Expr arg2) {
		this(predicate, Arrays.asList(new Expr[]{arg1, arg2}));
	}
	public AtomicFormula(Predicate predicate, Expr arg1, Expr arg2, Expr arg3) {
		this(predicate, Arrays.asList(new Expr[]{arg1, arg2,arg3}));
	}*/
	public AtomicFormula clone() {
		List<Expr> as = new ArrayList<Expr>(args.size());
		for (Iterator<? extends Expr> it= args.iterator();it.hasNext();) {
			Expr e = it.next();
			as.add(e.clone());
		}
		return new AtomicFormula(predicate.clone(), as);
	}

	/**
	 * returns the list of  arguments.
	 * @return
	 */
	public List<? extends Expr> getArguments() {
		return args;
	}
	public Set<VariableExpr> getAllVariables() {
		Set<VariableExpr> ret = new HashSet<VariableExpr>();
		for (Iterator<? extends Expr> it=getArguments().iterator();it.hasNext();) {
			Expr e = it.next();
			if (e.isVariable()) {
				ret.add((VariableExpr)e);
			}
		}
		return ret;
	}
	/*public boolean equals(Object o) {
		if (o==this) {
			return true;
		} 
		if (o instanceof AtomicFormula) {
			AtomicFormula other =(AtomicFormula)o;
			return predicate.equals(other.predicate)
				   && args.equals(other.getArguments());
		}
		return false;
	}
	
	public int hashCode() {
		return predicate.hashCode()+ 31*args.hashCode();
	}*/
	

	public Predicate getPredicate() {
		return predicate;
	}
	
	
	
	public AtomicFormula negate() {
		return new AtomicFormula(predicate.negate(), args);
	}
	
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((args == null) ? 0 : args.hashCode());
		result = prime * result
				+ ((predicate == null) ? 0 : predicate.hashCode());
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
		AtomicFormula other = (AtomicFormula) obj;
		
		if (predicate == null) {
			if (other.predicate != null)
				return false;
		} else if (!predicate.equals(other.predicate))
			return false;
		if (args == null) {
			if (other.args != null)
				return false;
		} else if (!args.equals(other.args))
			return false;
		
		return true;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(predicate.toString());
		buf.append("(");
		for (Iterator<? extends Expr> it= args.iterator();it.hasNext();) {
			Expr e = it.next();
			buf.append(e.toString());
			if (it.hasNext()) {
				buf.append(", ");
			}
		}
		buf.append(")");
		return buf.toString();
	}
	/**
	 * 
	 * @param s
	 * @return
	 * @throws ParseException
	 */
	public static AtomicFormula parse(String s) throws ParseException {
		s=s.trim();
		int firstPara = s.indexOf("(");
		if (firstPara<=0 ) {
			throw new ParseException("Missing '(' in the atomic formula: "+s,0);
		} else if (firstPara ==0) {
			throw new ParseException("Missing predicate name before the first '(' in the atomic formula: "+ s, 0);
		}
		String lastChar = s.substring(s.length()-1);
		if (!lastChar.equals(")")) {
			throw new ParseException("Missing ')' at the end of the atomic formula: "+s,s.length()-1);
		}
		if (firstPara!=s.lastIndexOf("(")) {
			throw new ParseException("'(' appears more than once in the atomic formula: "+s,0);
		}
		if (s.indexOf(")")!=s.length()-1) {
			throw new ParseException("')' appears more than once in the atomic formula: "+s,0);
			
		}
		String name=s.substring(0,firstPara);
		boolean isNegated;
		boolean isOptional;
		if (name.charAt(0) == Predicate.NEGATION_SYMBOL) {
			isNegated = true;
			isOptional = false;
			if (name.length()==1) {
				throw new ParseException("Missing predicate name after negation symbol: "+s, 0);
			}
			name = name.substring(1);
			if (name.charAt(0) == Predicate.OPTIONAL_SYMBOL) {
				throw new ParseException("Optional symbol not allowed after negation symbol: "+s, 0);
			} else if (name.charAt(0) == Predicate.NEGATION_SYMBOL) {
				throw new ParseException("Two consecutive negation symbols not allowed: "+s, 0);
			} 
		} else  if (name.charAt(0) == Predicate.OPTIONAL_SYMBOL) {
			isOptional = true;
			isNegated = false;
			if (name.length()==1) {
				throw new ParseException("Missing predicate name after optional symbol: "+s, 0);
			}
			name = name.substring(1);
			if (name.charAt(0) == Predicate.NEGATION_SYMBOL) {
				throw new ParseException("Negation symbol not allowed after optional symbol: "+s, 0);
			} else if (name.charAt(0) == Predicate.NEGATION_SYMBOL) {
				throw new ParseException("Two consecutive optional symbols not allowed: "+s, 0);
			} 
		} else 	{
			isNegated = false;
			isOptional = false;
		}
		
		List<Expr> args = new ArrayList<Expr>(); 
		if (firstPara==s.length()-2) {
			//no args
		} else {
			String argsAsString=s.substring(firstPara+1,s.length()-1); 
			StringTokenizer tokzer = new StringTokenizer(argsAsString,",");
			while (tokzer.hasMoreTokens()) {
				String arg = tokzer.nextToken();
				args.add(Expr.parse(arg));
			}
		}
		return new AtomicFormula(new Predicate(name,args.size(),isNegated, isOptional), args);
	}
	/**
	 * returns positions where a given variable appears
	 * @return
	 */
	public List<Integer> getPositions(VariableExpr var) {
		List<Integer> ret = new LinkedList<Integer>();
		for (int i=0;i<args.size();i++) {
			if (var.equals(args.get(i))) {
				ret.add(i);
			}
		}
		return ret;
		
	}
	/**
	 *Checks if the atomic formula contains constant arguments or 
	 *same variable argument multiple times 
	 */
	public boolean needsRectification(){
		Set<VariableExpr> varSet = new HashSet<VariableExpr>();
		for (Iterator<? extends Expr> it=getArguments().iterator();it.hasNext();) {
			Expr e = it.next();
			if (e.isVariable()) {
				if(varSet.contains(e))return true;
				varSet.add((VariableExpr)e);
			}
			if(e.isConstant())return true;
		}
		return false;
	}
	
	/* changes the name of the predicate
	 * eliminates constants and redundant 
	 * variables from the arguments
	 * */
	@SuppressWarnings("unchecked")
	public AtomicFormula rectify(){
		if (needsRectification()) {
			//get the variableArguments for the rectified predicate
			List<Expr> varArgs = getVarArguments4RectifiedPredicate();
			
			//get the constant terms and positions that cause rectification 
			Map<Integer, Expr> constantRectificationTerms = getConstanstRectificationTerms();
			
			List<List<Integer>> variableRectificationTerms=getVariableRectificationTerms();
			
			Predicate p = new RectificationPredicate(predicate,varArgs.size(),
					predicate.isNegated(), predicate.isOptional(), 
					constantRectificationTerms, variableRectificationTerms);
			return new AtomicFormula(p,varArgs);
		} else {
			return this;
		}
	}
	public AtomicFormula unrectify() {
		if (getPredicate() instanceof RectificationPredicate) {
			RectificationPredicate rp = (RectificationPredicate) getPredicate();
			Predicate originalPredicate = rp.getOriginalPredicate();
			Expr[] newArgs = new Expr[originalPredicate.getArity()];
			Map<Integer, Expr> pos2Val = rp.getConstRectificationTerms();
			List<List<Integer>> listOfListOfSameVarPos = new LinkedList<List<Integer>>(
					rp.getVariableRectificationTerms()) ;
			
			// add constants
			for (Map.Entry<Integer, Expr> e : pos2Val.entrySet()) {
				newArgs[e.getKey()] = e.getValue();
			}
			//
			
			int nextNull = 0;
			// add variables and non-rectified constants
			for (Expr exp: args) {
				// look for the next non set position
				while ( newArgs[nextNull]!=null) {
					nextNull++;
				}
				//
				
				List<Integer> sameVarPos;
				if (!listOfListOfSameVarPos.isEmpty()
				&& nextNull == (sameVarPos= listOfListOfSameVarPos.get(0)).get(0)) {
					//position corresponds to a variable appearing in
					// multiple positions
					for (Integer pos: sameVarPos) {
						 newArgs[pos] = exp;
					 }
					listOfListOfSameVarPos.remove(0);
				} else {
					assert listOfListOfSameVarPos.isEmpty() || (sameVarPos= listOfListOfSameVarPos.get(0)).contains(nextNull) :nextNull;//+"\n"+sameVarPos;
					newArgs[nextNull] = exp;
				}
				
			}
			return new AtomicFormula(originalPredicate, Arrays.asList(newArgs)).unrectify();
			
		} else {
			return this;
		}
	}
	
	private List<List<Integer>> getVariableRectificationTerms() {
		Map<Expr,List<Integer>> varToPositionsMap=new LinkedHashMap<Expr,List<Integer>>();	
		List<Expr> orderedExprs = new LinkedList<Expr>();
		int argPosition=0;
		for(Expr e: args){
			if(e.isVariable()){
				if(varToPositionsMap.containsKey(e)){
					varToPositionsMap.get(e).add(argPosition);
				}
				else{
					List<Integer> newSameVarList=new LinkedList<Integer>();
					newSameVarList.add(argPosition);
					varToPositionsMap.put(e, newSameVarList );
					orderedExprs.add(e);
				}				
			}			
			argPosition++;	
		}
		List<List<Integer>> variableRectificationTerms = new LinkedList<List<Integer>>();
		for(Expr e : orderedExprs){
			List<Integer> positions = varToPositionsMap.get(e);
			if(positions.size()>1)variableRectificationTerms.add(positions);
		}
		return variableRectificationTerms;
	}

	private Map<Integer, Expr> getConstanstRectificationTerms() {
		Map<Integer, Expr> constantRectificationTerms=new LinkedHashMap<Integer, Expr>();
		Integer argPosition=0;
		for(Expr e: args){
			if(e.isConstant()){
				constantRectificationTerms.put(argPosition, e);
			}
			argPosition++;
		}		
		return constantRectificationTerms;
	}

	private List<Expr> getVarArguments4RectifiedPredicate() {
		List<Expr> varArgs=new LinkedList<Expr>();
		for(Expr e: args){
			if(e.isVariable()){
				if( !varArgs.contains(e)){
					varArgs.add(e);
				}		
			}
		}
		return varArgs;
	}
	
	/*Assumes the predicates are equal, including the same arity*/
	public HashMap<Expr,Expr> computeArgumentMapping(AtomicFormula af){
		HashMap<Expr,Expr> mappings=new HashMap<Expr,Expr>();
		Iterator<? extends Expr> itThis = getArguments().iterator();
		Iterator< ? extends Expr> itAf = af.getArguments().iterator();
		while(itThis.hasNext() && itAf.hasNext()){
			Expr exprThis=itThis.next();
			Expr exprAf=itAf.next();
			if(!exprThis.equals(exprAf)){
				mappings.put(exprThis, exprAf);
			}
		}
		return mappings;
	}
	
	public AtomicFormula cloneWithArgumentMapping(HashMap<Expr,Expr> mappings){
		List<Expr> clonedExprList=new LinkedList<Expr>();
		for(Expr e : args){
			if(mappings.containsKey(e))
				clonedExprList.add(mappings.get(e));
			else
				clonedExprList.add(e);
		}
		return new AtomicFormula(predicate.clone(),clonedExprList);
	}
	
	public boolean hasSameArgumentType(AtomicFormula af){
		if(args.size()!=af.getArguments().size())return false;
		Iterator<? extends Expr> itThis = getArguments().iterator();
		Iterator<? extends Expr> itAf = af.getArguments().iterator();
		while(itThis.hasNext() && itAf.hasNext()){
			Expr exprThis=itThis.next();
			Expr exprAf=itAf.next();
			if((exprThis.isConstant()&& exprAf.isVariable())||
					(exprAf.isConstant()&& exprThis.isVariable())){
				return false;
			}
		}
		return true;
	}
	
	public int getArity() {
		return predicate.getArity();
	}
}
