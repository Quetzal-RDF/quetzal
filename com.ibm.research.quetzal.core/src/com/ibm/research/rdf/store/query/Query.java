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
 package com.ibm.research.rdf.store.query;

public interface Query {

	// Need to move more methods from sparql11.model.Query to this interface
	
	public boolean isSelectQuery() ;
	public boolean isAskQuery() ;
	public boolean isDescribeQuery() ;
	public boolean isConstructQuery() ;
	public boolean isDistinct();
	
}
