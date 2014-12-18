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
 package com.ibm.research.rdf.store.schema;

import com.ibm.research.rdf.store.config.Constants;

public class ReorgSchemaCreator {
	
	public static String getCreateDirectPrimaryHash(int hashSize, int stringSize, int graphIDSize) {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE %s (");
		sb.append(Constants.NAME_COLUMN_ENTRY + " VARCHAR(" + stringSize + ") NOT NULL,");
		sb.append(Constants.NAME_COLUMN_GRAPH_ID + " VARCHAR(" + graphIDSize + ") NOT NULL,");
		sb.append(Constants.NAME_COLUMN_SPILL + " INTEGER NOT NULL,");
		for(int i = 0; i < hashSize; i++) {
			sb.append(Constants.NAME_COLUMN_PREFIX_PREDICATE + i + " VARCHAR(" + stringSize + "),");
			sb.append(Constants.NAME_COLUMN_PREFIX_VALUE + i + " VARCHAR(" + stringSize + "),");
			sb.append(Constants.NAME_COLUMN_PREFIX_TYPE + i + " smallint ");
			if(i<hashSize-1)sb.append(","); 
			sb.append(""); 
		}
		sb.append(") VALUE COMPRESSION COMPRESS YES");
		return sb.toString();
	}
	
	public static String getCreateDirectSecondaryHash(int stringSize, int graphIDSize) {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE %s (");
		sb.append(Constants.NAME_COLUMN_GRAPH_ID + " VARCHAR(" + graphIDSize + ") NOT NULL,");		
		sb.append(Constants.NAME_COLUMN_LIST_ID + " VARCHAR(" + stringSize + ") NOT NULL,");
		sb.append(Constants.NAME_COLUMN_PREFIX_LIST_ELEMENT + " VARCHAR(" + stringSize + "),");
		sb.append(Constants.NAME_COLUMN_PREFIX_LIST_TYPE + " smallint NOT NULL");
		sb.append(") VALUE COMPRESSION COMPRESS YES");
		return sb.toString();
	}
	
	public static String getCreateReversePrimaryHash(int hashSize, int stringSize, int graphIDSize) {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE %s (");
		sb.append(Constants.NAME_COLUMN_ENTRY + " VARCHAR(" + stringSize + ") NOT NULL,");
		sb.append(Constants.NAME_COLUMN_ENTRY_NUMERIC + " DECFLOAT(34) , ");
		sb.append(Constants.NAME_COLUMN_ENTRY_DATETIME + " TIMESTAMP , ");
		sb.append(Constants.NAME_COLUMN_PREFIX_TYPE + " SMALLINT NOT NULL, ");
		sb.append(Constants.NAME_COLUMN_GRAPH_ID + " VARCHAR(" + graphIDSize + ") NOT NULL,");
		sb.append(Constants.NAME_COLUMN_SPILL + " INTEGER NOT NULL,");
		for(int i = 0; i < hashSize; i++) {
			sb.append(Constants.NAME_COLUMN_PREFIX_PREDICATE + i + " VARCHAR(" + stringSize + "),");
			sb.append(Constants.NAME_COLUMN_PREFIX_VALUE + i + " VARCHAR(" + stringSize + ")");
			if(i<hashSize-1)sb.append(","); 
			sb.append(""); 
		}
		sb.append(") VALUE COMPRESSION COMPRESS YES");
		return sb.toString();
	}
	
	public static String getCreateReverseSecondaryHash(int stringSize, int graphIDSize) {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE %s (");
		sb.append(Constants.NAME_COLUMN_GRAPH_ID + " VARCHAR(" + graphIDSize + ") NOT NULL,");		
		sb.append(Constants.NAME_COLUMN_LIST_ID + " VARCHAR(" + stringSize + ") NOT NULL,");
		sb.append(Constants.NAME_COLUMN_PREFIX_LIST_ELEMENT + " VARCHAR(" + stringSize + ")");
		sb.append(") VALUE COMPRESSION COMPRESS YES");
		return sb.toString();
	}
	
	public static String getCreateLongStrings() {
		StringBuilder sb = new StringBuilder();
		int sSize = Constants.LONG_STRING_HASH_SIZES.get(Constants.LONG_STRINGS_HASHING_METHOD) + Constants.PREFIX_SHORT_STRING.length();
		sb.append("CREATE TABLE %s (");
		sb.append(Constants.NAME_COLUMN_SHORT_STRING + " VARCHAR (" + sSize + ") NOT NULL,");
		sb.append(Constants.NAME_COLUMN_LONG_STRING + " VARCHAR(" + Constants.LONG_STRING_COLUMN_SIZE + ") NOT NULL,");
		sb.append(Constants.NAME_COLUMN_PREFIX_TYPE + " SMALLINT NOT NULL, ");
		sb.append("PRIMARY KEY(" + Constants.NAME_COLUMN_SHORT_STRING + ")) VALUE COMPRESSION COMPRESS YES");
		return sb.toString();
	}
	
	
	
	public static String getLoadCommand(String tableName, String dataPath, String file) {
		StringBuilder sb = new StringBuilder();
		int delChar = (int)Constants.LOAD_COLUMN_SEPARATOR;
		sb.append("LOAD FROM " + (dataPath + "/" + file) + " OF DEL MODIFIED BY fastparse keepblanks nochardel coldel0x" + Integer.toHexString(delChar) + " INSERT INTO " + tableName + " NONRECOVERABLE INDEXING MODE REBUILD");
		return sb.toString();
	}
	
}
