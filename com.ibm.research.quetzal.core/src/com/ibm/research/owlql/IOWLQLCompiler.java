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

import java.util.Set;

import com.hp.hpl.jena.query.Query;
import com.ibm.research.owlql.rule.RuleSystem;

public interface IOWLQLCompiler {

	public abstract NormalizedOWLQLTbox getTBox();

	
	/**
	 * performs query reformulation
	 * @param q
	 * @return
	 */
	public Set<ConjunctiveQuery> compile(ConjunctiveQuery q);
	
	/**
	 * performs query reformulation
	 * @param q
	 * @return
	 */
	public String compileToSQL(ConjunctiveQuery q, boolean returnIDs);
	
	public Query compileToUnionQuery(ConjunctiveQuery q);
	
	public RuleSystem compileToRules(ConjunctiveQuery q);
	
	public boolean canCompileToSPARQL();
	
	public boolean canCompileToSQL(boolean returnIDs);

	public boolean canCompileToRules();
	
	/**
	 * returns whether the compiler can produce a non-flattened union query.
	 * If <code> true</code> is returned, then  {@link #compileToUnionQuery(ConjunctiveQuery)} will return
	 * a non-null value.
	 * @return
	 */
	public boolean canCompileToUnionSPARQL();
	
	/**
	 *
	  * @return
	 */
	public ConsistencyCheckResult computeConsistencyCheck();
}
