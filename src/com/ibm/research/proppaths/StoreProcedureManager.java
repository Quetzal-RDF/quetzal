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
