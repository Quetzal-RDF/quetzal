/*******************************************************************************
 * Licensed Materials - Property of IBM
 * (c) Copyright IBM Corporation 2008. All Rights Reserved.
 * 
 * Note to U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp. 
 *******************************************************************************/


package com.ibm.research.owlql.rule;


public interface Adornment {
	boolean isAdornmentOnRule();
	boolean isAdornmentOnPredicate();
	RuleAdornment asRuleAdornment();
	PredicateAdornment asPredicateAdornment();
}
