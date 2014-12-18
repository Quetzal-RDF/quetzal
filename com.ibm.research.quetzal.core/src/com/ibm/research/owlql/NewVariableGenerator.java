/*******************************************************************************
* Licensed Materials - Property of IBM
* (c) Copyright IBM Corporation 2011, 2013. All Rights Reserved.
*
* Note to U.S. Government Users Restricted Rights:
* Use, duplication or disclosure restricted by GSA ADP Schedule
* Contract with IBM Corp.
*******************************************************************************/
package com.ibm.research.owlql;

/**
 * A generator of new fresh variables 
 * @author achille
 *
 */
public class NewVariableGenerator {
	private String prefix;
	private int suffixCount;
	public NewVariableGenerator(String prefix) {
		this(prefix, 0);
	}
	public NewVariableGenerator(String prefix, int suffixCount) {
		super();
		this.prefix = prefix;
		this.suffixCount = suffixCount;
	}
	
	public String createNewVariable(){
		return prefix+(suffixCount++);
	}
	
}
