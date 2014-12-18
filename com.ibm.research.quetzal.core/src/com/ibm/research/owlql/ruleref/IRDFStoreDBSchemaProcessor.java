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
