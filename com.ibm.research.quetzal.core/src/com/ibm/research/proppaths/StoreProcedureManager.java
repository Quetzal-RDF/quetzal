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

public interface StoreProcedureManager {
	

	public List<SQLCommand> init();
	public StoreProcedure createTemporaryStoreProcedure( 
			List<Pair<String, String>> inputParameterNameTypeList, List<Pair<String, String>> outputParameterNameTypeList, 
			int numberOfReturnedResultSets, List<SQLCommand> bodyStatements);
	public StoreProcedure createTemporaryStoreProcedure(int numberOfReturnedResultSets, List<SQLCommand> bodyStatements);
	
	public List<SQLCommand> declareAllTemporaryStoreProcedures();
	public List<SQLCommand> release(StoreProcedure proc);
	
	public List<SQLCommand> releaseAllTemporaryStoreProcedures();
	
}
