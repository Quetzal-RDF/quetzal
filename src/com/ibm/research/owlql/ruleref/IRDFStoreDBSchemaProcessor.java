/*******************************************************************************
* Licensed Materials - Property of IBM
* (c) Copyright IBM Corporation 2011, 2013. All Rights Reserved.
*
* Note to U.S. Government Users Restricted Rights:
* Use, duplication or disclosure restricted by GSA ADP Schedule
* Contract with IBM Corp.
*******************************************************************************/
package com.ibm.research.owlql.ruleref;

import com.ibm.iodt.sor.db.sql.CreatableTable;
import com.ibm.research.owlql.rule.Predicate;
import com.ibm.research.owlql.rule.RuleSystem;

public interface IRDFStoreDBSchemaProcessor {

	
	public void setRuleSystem(RuleSystem rs);
	public RuleSystem convertDLPredicateToDBTablePredicate(); 
	
	/**
	 * definition of a table with a single column and a single row.
	 *  We use that table to add in sql
	 * query select statement that return a single constant value 
	 * (e.g select 3 from singletonTable) 
	 */
	public CreatableTable getSingletonTable(); 
	
	public CreatableTable getDBTable(Predicate p);
	
}
