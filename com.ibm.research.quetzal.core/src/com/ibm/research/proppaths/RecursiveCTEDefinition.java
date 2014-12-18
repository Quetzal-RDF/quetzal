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

public class RecursiveCTEDefinition extends CTEDefinition {

	public static String toSQLDefinition(String initializationSQL, String iterationSQL, boolean removeDuplicate) {
		StringBuffer buf = new StringBuffer();
		buf.append(initializationSQL)
		   .append(removeDuplicate?" UNION ": " UNION ALL ")
		   .append(iterationSQL);
		return buf.toString();
	}
	public RecursiveCTEDefinition(String name, String optionalCTESignature,  String initializationSQL, String iterationSQL, boolean removeDuplicate) {
		super(name, optionalCTESignature, toSQLDefinition(initializationSQL, iterationSQL, removeDuplicate));
	}
	
}
