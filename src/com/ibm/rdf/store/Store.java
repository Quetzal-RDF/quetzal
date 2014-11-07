package com.ibm.rdf.store;

import org.antlr.stringtemplate.StringTemplate;

import com.ibm.rdf.store.config.Statistics;

public interface Store {
	public static final int SHARK_REDUCERS = 200;
	public enum Backend {
		db2, postgresql, shark
	}
	public enum Db2Type {
		DECFLOAT, TIMESTAMP, VARCHAR, MIXED
	};
	
	public interface PredicateTable {
	
		boolean hasSpills(String predicate);
		
		boolean hasSpills();
		
		boolean isOneToOne(String predicate);
		
		Db2Type getType(String predicate);
		
		int getHashCount(String predicate);
		
		int[] getHashes(String predicate);
		
	}
	
	public String getStoreName();

   public String getStoreBackend();

	public String getDirectPrimary();

	public int getDPrimarySize();

	public String getDirectSecondary();
	
	public String getDatatype(String key);

	public String getReversePrimary();

	public int getRPrimarySize();

	public String getReverseSecondary();

	public String getLongStrings();

	public int getMaxStringLen();

	public int getGidMaxStringLen();

	public int getHasLongStrings();
	
	public boolean isLongString();
	
	public String getBasicStatsTable();
	
	public String getTopKStatsTable();
	
	public String getSchemaName();
	
	public Statistics getPerGraphStatistics();
	
	public Statistics getOverallStatistics();
	
	public Context getContext();
			
	public String getDataTypeTable();

	public String getRegexUdfName() ;
	
	public StringTemplate getInstanceOf(String template);
	
	public void setQueryLogFile(String logFile);
	
	public PredicateTable getDirectPredicates();

	public PredicateTable getReversePredicates();
	
	public String getUserTablespace();
	
	public void setUserTablespace(String name);
	
}
