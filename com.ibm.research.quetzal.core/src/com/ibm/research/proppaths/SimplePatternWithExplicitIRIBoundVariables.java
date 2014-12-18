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
 package com.ibm.research.proppaths;

import java.util.Collections;
import java.util.Set;

import com.ibm.research.rdf.store.sparql11.model.SimplePattern;
import com.ibm.research.rdf.store.sparql11.model.Variable;
import com.ibm.wala.util.collections.HashSetFactory;

public class SimplePatternWithExplicitIRIBoundVariables extends SimplePattern {
	
	protected Set<Variable> explicitIRIBoundVariables; 
	protected Set<Variable> explicitNotIRIBoundVariables;
	/*public SimplePatternWithExplicitIRIBoundVariables() {
		this(null, null);
	}*/
	public SimplePatternWithExplicitIRIBoundVariables(
			Set<Variable> explicitIRIBoundVariables, Set<Variable> explicitNotIRIBoundVariables) {
		super();
		this.explicitIRIBoundVariables = explicitIRIBoundVariables;
		this.explicitNotIRIBoundVariables = explicitNotIRIBoundVariables;
		if (explicitIRIBoundVariables!=null && explicitNotIRIBoundVariables!=null) {
			assert Collections.disjoint(explicitIRIBoundVariables, explicitNotIRIBoundVariables);
		}
	}
	
	@Override
	public Set<Variable> gatherIRIBoundVariables() {
		//TODO: Fix it everywhere where SimplePattern.gatherIRIBoundVariables is overriden.
		if (explicitIRIBoundVariables == null && explicitNotIRIBoundVariables == null) {
			return super.gatherIRIBoundVariables();
		} else {
			Set<Variable> ret = HashSetFactory.make(super.gatherIRIBoundVariables());
			if (explicitNotIRIBoundVariables!=null) { 
				ret.removeAll(explicitNotIRIBoundVariables);
			} 
			if(explicitIRIBoundVariables!=null) {
				ret.addAll(explicitIRIBoundVariables);
			}
			return ret;
		}
	}
}
