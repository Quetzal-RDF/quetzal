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


import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RectificationPredicate extends Predicate {

	static Map<Expr, Integer> constantMaps=new HashMap<Expr,Integer>();
	
	protected Map<Integer,Expr> constRectificationTerms;
	protected List<List<Integer>> variableRectificationTerms;
	protected Predicate originalPredicate;
	protected static final String separator = "_";//","
	public RectificationPredicate(Predicate originalPredicate, int arity, boolean isNegated,
			boolean isOptional, 
			Map<Integer,Expr> constRectification, 
			List<List<Integer>> variableRectification) {
		super(getRectifiedName(constRectification,variableRectification,originalPredicate.getName()), 
				arity, isNegated, isOptional);
		this.originalPredicate=originalPredicate;
		constRectificationTerms=new LinkedHashMap<Integer, Expr>();
		constRectificationTerms.putAll(constRectification);
		variableRectificationTerms=new LinkedList<List<Integer>>(variableRectification);
		
	}
	@SuppressWarnings("unchecked")
	private static  String getRectifiedName(Map<Integer,Expr> constRectificationTerms,
			List<List<Integer>> variableRectificationTerms,
			String originalName){	
		StringBuffer pname=new StringBuffer();
		pname.append(originalName);
		for(Integer pos : constRectificationTerms.keySet()){
			Expr e=constRectificationTerms.get(pos);
			pname.append("_c_"); // _c:"
			if(constantMaps.containsKey(e)){
				pname.append(constantMaps.get(e));				
			}
			else{
				pname.append(constantMaps.size());
				constantMaps.put(e,constantMaps.size());				
			}
			pname.append(separator);
			pname.append(pos);
		}
		for(Object o: variableRectificationTerms){
			pname.append("_");
			boolean useComa=false;
			for(Integer p:(List<Integer>)o){
				if(useComa)pname.append(separator);
				pname.append(p);
				useComa=true;
			}			
		}
		return pname.toString();
	}
	
	
	
	public Map<Integer, Expr> getConstRectificationTerms() {
		return Collections.unmodifiableMap(constRectificationTerms);
	}
	
	public List<List<Integer>> getVariableRectificationTerms() {
		return Collections.unmodifiableList(variableRectificationTerms);
	}
	
	public Predicate getOriginalPredicate() {
		return originalPredicate;
	}
	
	public Predicate negate() {
		return  new RectificationPredicate(getOriginalPredicate(),getArity(),  !isNegated(), isOptional(),constRectificationTerms, variableRectificationTerms);
	}
	public Predicate switchOptionalFlag() {
		return  new RectificationPredicate(getOriginalPredicate(),getArity(),  isNegated(), !isOptional(),constRectificationTerms, variableRectificationTerms);
	}
	public Predicate withoutQualification() {
		return  new RectificationPredicate(getOriginalPredicate(),getArity(),  false, false,constRectificationTerms, variableRectificationTerms);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((constRectificationTerms == null) ? 0
						: constRectificationTerms.hashCode());
		result = prime
				* result
				+ ((originalPredicate == null) ? 0 : originalPredicate
						.hashCode());
		result = prime
				* result
				+ ((variableRectificationTerms == null) ? 0
						: variableRectificationTerms.hashCode());
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
		RectificationPredicate other = (RectificationPredicate) obj;
		if (constRectificationTerms == null) {
			if (other.constRectificationTerms != null)
				return false;
		} else if (!constRectificationTerms
				.equals(other.constRectificationTerms))
			return false;
		if (originalPredicate == null) {
			if (other.originalPredicate != null)
				return false;
		} else if (!originalPredicate.equals(other.originalPredicate))
			return false;
		if (variableRectificationTerms == null) {
			if (other.variableRectificationTerms != null)
				return false;
		} else if (!variableRectificationTerms
				.equals(other.variableRectificationTerms))
			return false;
		return true;
	}

	
	
	
}
