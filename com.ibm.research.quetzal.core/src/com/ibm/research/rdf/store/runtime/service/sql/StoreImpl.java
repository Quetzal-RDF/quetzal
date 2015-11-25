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
 package com.ibm.research.rdf.store.runtime.service.sql;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.SequenceInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

import com.hp.hpl.jena.rdf.model.Property;
import com.ibm.research.rdf.store.Context;
import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.config.Constants;
import com.ibm.research.rdf.store.config.Statistics;
import com.ibm.research.rdf.store.jena.RdfStoreException;
import com.ibm.research.rdf.store.sparql11.sqlwriter.SPARQLToSQLExpression;

public class StoreImpl implements Store {

	private String storeName;
	private Backend storeBackend;
	private String directPrimary;
	private String directSecondary;
	private String reversePrimary;
	private String reverseSecondary;
	private String longStrings;
	private int dPrimarySize;
	private int gidMaxStringLen;
	private int maxStringLen;
	private int rPrimarySize;
	private String schemaName;
	private String basicStatsTable;
	private String topKStatsTable;
	private Statistics perGraphStatistics;
	private Statistics overallStatistics;
	private final Context context;
	private boolean hasGraphs;

	private int longString;
	private int entry_ID;
	private Map<Property, String> systemPredicates = new HashMap<Property, String>();

	private String datatype;
	private String regexUdf;
	private String logFile = null;
	PrintWriter pw = null;
	private String userTablespace;

	private StringTemplateGroup group;
	
	private Properties dataTypes;
	
	public static final String DB2templatesFile = "com/ibm/research/rdf/store/sparql11/sqlwriter/DB2SQLTemplates.stg";
	public static final String PSQLtemplatesFile = "com/ibm/research/rdf/store/sparql11/sqlwriter/PSQLSQLTemplates.stg";
	public static final String SharktemplatesFile = "com/ibm/research/rdf/store/sparql11/sqlwriter/SharkSQLTemplates.stg";
	
	public static final String DB2DatatypesFile = "com/ibm/research/rdf/store/runtime/service/sql/DB2.datatypes";
	public static final String PostgresqlDatatypesFile = "com/ibm/research/rdf/store/runtime/service/sql/Postgresql.datatypes";
	public static final String SharkDatatypesFile = "com/ibm/research/rdf/store/runtime/service/sql/Shark.datatypes";
	
	public StoreImpl(Context context) {
		super();
		this.context = context;
	}

	public int getEntry_ID() {
		return entry_ID;
	}

	public void setEntry_ID(int entry_ID) {
		this.entry_ID = entry_ID;
	}

	public String getStoreName() {
		return storeName;
	}

	public Backend getStoreBackend() {
		return storeBackend;
	}

	public String getDirectPrimary() {
		return schemaName + "." + directPrimary;
	}

	public String getDirectSecondary() {
		return schemaName + "." + directSecondary;
	}

	public String getReversePrimary() {
		return schemaName + "." + reversePrimary;
	}

	public String getReverseSecondary() {
		return schemaName + "." + reverseSecondary;
	}

	public String getLongStrings() {
		return longStrings;
	}

	public int getGidMaxStringLen() {
		return gidMaxStringLen;
	}

	public int getMaxStringLen() {
		return maxStringLen;
	}

	public int getDPrimarySize() {
		return dPrimarySize;
	}

	public int getRPrimarySize() {
		return rPrimarySize;
	}

	public int getHasLongStrings() {
		return longString;
	}

	public boolean isLongString() {
		return longString == 1;
	}

	public void setLongString(boolean val) {
		throw new RdfStoreException("Not supported.");
	}

	public Statistics getPerGraphStatistics() {
		return perGraphStatistics;
	}

	public Statistics getOverallStatistics() {
		return overallStatistics;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;

		String name = storeName;
		if (name.length() > StoreHelper.TBLNAMELEN) {
			name = name.substring(0, StoreHelper.TBLNAMELEN);
		}
		name = name + "_";

		this.regexUdf = name + Constants.NAME_UDF_REGEX;
	}

	public void setStoreBackend(Backend storeBackend) {
		this.storeBackend = storeBackend;
		String templatesFile = null;
		String dataTypesFile = null;
		try {
			if (storeBackend==Store.Backend.postgresql)
			{
				templatesFile = PSQLtemplatesFile;
				dataTypesFile = PostgresqlDatatypesFile;
			
			}
			else if (storeBackend==Store.Backend.shark)
			{
				templatesFile = SharktemplatesFile;
				dataTypesFile = SharkDatatypesFile;
			}
			else {
				templatesFile = DB2templatesFile;
				dataTypesFile = DB2DatatypesFile;
			}
			
			InputStream istream = SPARQLToSQLExpression.class.getClassLoader().getResourceAsStream(templatesFile);
			InputStream common = SPARQLToSQLExpression.class.getClassLoader().getResourceAsStream("com/ibm/research/rdf/store/sparql11/sqlwriter/common.stg");
			InputStreamReader reader = new InputStreamReader(new SequenceInputStream(common, istream));
			group = new StringTemplateGroup(reader);
			reader.close();
			
			dataTypes = new Properties();
			dataTypes.load(SPARQLToSQLExpression.class.getClassLoader().getResourceAsStream(dataTypesFile));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getDatatype(String key) {
		assert dataTypes.containsKey(key) : "key not found in store:" + key;
		return dataTypes.getProperty(key);
	}

	public String getRegexUdfName() {
		return regexUdf;
	}

	public void setDirectPrimary(String directPrimary) {
		this.directPrimary = directPrimary;
	}

	public void setDirectSecondary(String directSecondary) {
		this.directSecondary = directSecondary;
	}

	public void setReversePrimary(String reversePrimary) {
		this.reversePrimary = reversePrimary;
	}

	public void setReverseSecondary(String reverseSecondary) {
		this.reverseSecondary = reverseSecondary;
	}

	public void setLongStrings(String longStrings) {
		this.longStrings = longStrings;
	}

	public void setDPrimarySize(int directPrimarySize) {
		this.dPrimarySize = directPrimarySize;
	}

	public void setGidMaxStringLen(int maxGIDStringLength) {
		this.gidMaxStringLen = maxGIDStringLength;
	}

	public void setMaxStringLen(int maxStringLength) {
		this.maxStringLen = maxStringLength;
	}

	public void setRPrimarySize(int reversePrimarySize) {
		this.rPrimarySize = reversePrimarySize;
	}

	public void setHasLongStrings(int flag) {
		longString = flag;
	}

	public String getBasicStatsTable() {
		return basicStatsTable;
	}

	public void setBasicStatsTable(String basicStatsTable) {
		this.basicStatsTable = basicStatsTable;
	}

	public String getTopKStatsTable() {
		return topKStatsTable;
	}

	public void setTopKStatsTable(String topKStatsTable) {
		this.topKStatsTable = topKStatsTable;
	}

	public void setPerGraphStatistics(Statistics perGraphStatistics) {
		this.perGraphStatistics = perGraphStatistics;
	}

	public void setOverallStatistics(Statistics overallStatistics) {
		this.overallStatistics = overallStatistics;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context ctx) {

	}

	public Map<Property, String> getSystemPredicates() {
		return systemPredicates;
	}

	public void setSystemPredicates(Map<Property, String> systemPredicates) {
		this.systemPredicates = systemPredicates;
	}

	public String getDataTypeTable() {
		return datatype;
	}

	public void setDatatypeTable(String datatype) {
		this.datatype = datatype;
	}

	public void setQueryLogFile(String logFile) {
		this.logFile = logFile;
		if (pw != null) {
			pw.close();
		}
		if (this.logFile != null) {
			try {
				pw = new PrintWriter(
						new BufferedWriter(new FileWriter(logFile)));
			} catch (IOException e) {
				// swallow
			}
		}

	}

	public void logQueryInfo(String queryInfo) {

		if (pw != null) {
			pw.println(queryInfo);
			pw.flush();
		}

	}

	private PredicateTable directPredicates;

	@Override
	public PredicateTable getDirectPredicates() {
		return directPredicates;
	}

	public void setDirectPredicates(PredicateTable table) {
		directPredicates = table;
	}

	private PredicateTable reversePredicates;

	@Override
	public PredicateTable getReversePredicates() {
		return reversePredicates;
	}

	public void setReversePredicates(PredicateTable table) {
		reversePredicates = table;
	}

	@Override
	public String getUserTablespace() {
		return userTablespace;
	}

	@Override
	public void setUserTablespace(String name) {
		userTablespace = name;
	}

	@Override
	public StringTemplate getInstanceOf(String template) {
		return group.getInstanceOf(template);
	}

	public boolean hasGraphs() {
		return hasGraphs;
	}

	public void setHasGraphs(boolean hasGraphs) {
		this.hasGraphs = hasGraphs;
	}

}