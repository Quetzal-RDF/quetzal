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

import java.util.List;

import com.ibm.wala.util.collections.Pair;

public interface TemporaryTableMgr {

	public List<SQLCommand> init();
	/**
	 * returns the name of a new or recycled temporary table and the list of
	 * SQL command that MUST be executed before the first use of the returned temporary table
	 * @return
	 */
	public Pair<String, List<SQLCommand>> getTemporaryTable(String tableSignature);
	
	/**
	 * indicates that a temporary table that was returned by {@link #getTemporaryTable()} 
	 * can now safely be either discarded or reused.
	 * @param temptable
	 */
	public List<SQLCommand> release(String temptable);
	
	/**
	 * release all temporary tables created by this {@link TemporaryTableMgr} that have not been released yet.
	 * @return
	 */
	public List<SQLCommand> releaseAll();
	
	

}
