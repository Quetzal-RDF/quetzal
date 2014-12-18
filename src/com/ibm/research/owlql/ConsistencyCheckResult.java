/*******************************************************************************
* Licensed Materials - Property of IBM
* (c) Copyright IBM Corporation 2011, 2013. All Rights Reserved.
*
* Note to U.S. Government Users Restricted Rights:
* Use, duplication or disclosure restricted by GSA ADP Schedule
* Contract with IBM Corp.
*******************************************************************************/
package com.ibm.research.owlql;

import com.hp.hpl.jena.query.Query;


/**
 * result of a consistency check performed based on a QL Tbox only.
 * The result is one of the following:
 * <ul>
 * 	<li>consis</li>
 * </ul>
 * @author fokoue
 *
 */
public class ConsistencyCheckResult {

	private Boolean consistent;
	private Query inconsistencyDetectionAskQuery;
	
	ConsistencyCheckResult(boolean consistent) {
		this.consistent = consistent;
		inconsistencyDetectionAskQuery = null;
	}
	ConsistencyCheckResult(Boolean consistent,
			Query inconsistencyDetectionAskQuery) {
		super();
		this.consistent = consistent;
		this.inconsistencyDetectionAskQuery = inconsistencyDetectionAskQuery;
		assert consistent != null || inconsistencyDetectionAskQuery !=null;
	}

	/**
	 * returns the result of the consistency check based only of a QL Tbox analysis.
	 * It returns one of the following three values:
	 * <ul>
	 *  <li> {@link Boolean#TRUE} to indicate that an Abox cannot be inconsistent based on the analyzed QL Tbox</li>
	 *  <li> {@link Boolean#FALSE} to indicate that the analyzed Tbox is inconsistent (regardless of the consisdered Abox)</li>
	 *  <li> <code>null</code> to indicate that the consistency status depends of the content of the Abox. In this case,
	 *  the method {@link #getInconsistencyDetectionAskQuery()} will return the ask query that needs to be evaluated against an Abox to 
	 *  ascertain its inconsistency *  
	 *   <li>
	 * Tbox; 
	 * @return
	 */
	public Boolean isConsistent() {
		return consistent;
	}
	
	/**
	 * returns an ask query that should be evaluated against a Abox to determine whether is inconsistent.
	 * The evaluation of the returned query against an Abox A yields <code>true</code> iff. A is inconsistent.
	 * @return
	 */
	public Query getInconsistencyDetectionAskQuery() {
		return inconsistencyDetectionAskQuery;
	}
	
}
