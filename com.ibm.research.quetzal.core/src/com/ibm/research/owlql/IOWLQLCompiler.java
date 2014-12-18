/*******************************************************************************
* Licensed Materials - Property of IBM
* (c) Copyright IBM Corporation 2011, 2013. All Rights Reserved.
*
* Note to U.S. Government Users Restricted Rights:
* Use, duplication or disclosure restricted by GSA ADP Schedule
* Contract with IBM Corp.
*******************************************************************************/
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
