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
 package com.ibm.research.rdf.store.config;

import java.util.HashMap;
import java.util.Map;

/**
 * @author oudrea
 * Contains various constants used by RDFStore classes.
 */
public class Constants {

	/** Table containing the various hash methods for mapping long strings to shorter strings and the respective short string size. */
	public static final Map<String, Integer> LONG_STRING_HASH_SIZES;
	
	static {
		LONG_STRING_HASH_SIZES = new HashMap<String, Integer>();
		LONG_STRING_HASH_SIZES.put("MD5", 128 / 8 * 2);
		LONG_STRING_HASH_SIZES.put("MD2", 128 / 8 * 2);
		LONG_STRING_HASH_SIZES.put("SHA-1", 160 / 8 * 2);
		LONG_STRING_HASH_SIZES.put("SHA-256", 256 / 8 * 2);
		LONG_STRING_HASH_SIZES.put("SHA-384", 384 / 8 * 2);
		LONG_STRING_HASH_SIZES.put("SHA-512", 512 / 8 * 2);
	}
	
	/** This is the default method to hash long strings into shorter strings. */
	public static final String LONG_STRINGS_HASHING_METHOD = "MD5";
	
	/** Column separator character for files generated for LOAD. */
	public static final char LOAD_COLUMN_SEPARATOR = '|';
	
	/** Load file name for direct primary hash. */
	public static final String LOADFILE_DIRECT_PRIMARY_HASH = "direct-primary.load";

	/** Load file name for direct secondary hash. */
	public static final String LOADFILE_DIRECT_SECONDARY_HASH = "direct-secondary.load";

	/** Load file name for reverse primary hash. */
	public static final String LOADFILE_REVERSE_PRIMARY_HASH = "reverse-primary.load";

	/** Load file name for reverse secondary hash. */
	public static final String LOADFILE_REVERSE_SECONDARY_HASH = "reverse-secondary.load";

	/** Load file name for spillover table. */
	public static final String LOADFILE_SPILLOVER = "spillover.load";

	/** Load file name for long strings table. */
	public static final String LOADFILE_LONG_STRINGS = "long-strings.load";
	
	/** Load file name for direct primary hash. */
	public static final String DUMPFILE_DIRECT_PRIMARY_HASH = "direct-primary.dmp";

	/** Load file name for direct secondary hash. */
	public static final String DUMPFILE_DIRECT_SECONDARY_HASH = "direct-secondary.dmp";

	/** Load file name for reverse primary hash. */
	public static final String DUMPFILE_REVERSE_PRIMARY_HASH = "reverse-primary.dmp";

	/** Load file name for reverse secondary hash. */
	public static final String DUMPFILE_REVERSE_SECONDARY_HASH = "reverse-secondary.dmp";

	/** Load file name for spillover table. */
	public static final String DUMPFILE_SPILLOVER = "spillover.dmp";

	/** Load file name for long strings table. */
	public static final String DUMPFILE_LONG_STRINGS = "long-strings.dmp";
	
	/** Number of decimals to use for rounding. */
	public static final int ROUNDING_DECIMALS = 10;
	
	/** The "fudge" factor in determining schema sizes, i.e., how much more space we should allow for future dataset updates. Should be greater than 1. */
	public static double SCHEMA_FUTURE_ALLOCATION = 1.3;
	
	/** The standard prefix for SHORT STRINGS. */
	public static String PREFIX_SHORT_STRING = "s:";
	
	/** The standard prefix for BLANK NODE. */
	public static String PREFIX_BLANK_NODE = "_:";
	
	/** The standard prefix for LIST IDs. */
	public static String PREFIX_LIST_ID = "lid:";
	
	/** The standard prefix for Typed Literal */
	public static String TYPED_LITERAL_DELIMITER = "^^";
	
	/** The standard prefix for Language Literal */
	public static String LITERAL_LANGUAGE = "@";
	
	/** The maximum page size. */
	public static final int MAX_PAGE_SIZE = 30 * 1024;
	
	/** The number of bytes for long ID columns. */
	public static final int ID_COLUMN_SIZE = 8;
	
	/** The penalty weight for triples. */
	public static final double PENALTY_WEIGHT_TRIPLES = 0.7;
	
	/** The penalty weight for strings. */
	public static final double PENALTY_WEIGHT_STRINGS = 0.3;
	
	// --------------------------------------- TABLE AND COLUMN NAME CONSTANTS --------------------------------------------
	public static final String NAME_TABLE_DIRECT_PRIMARY_HASH = "direct_primary_hash";
	public static final String NAME_TABLE_DIRECT_SECONDARY_HASH = "direct_secondary";
	public static final String NAME_TABLE_REVERSE_PRIMARY_HASH = "reverse_primary_hash";
	public static final String NAME_TABLE_REVERSE_SECONDARY_HASH = "reverse_secondary";
	public static final String NAME_TABLE_LONG_STRINGS = "long_strings";
	public static final String NAME_TABLE_BASIC_STATS = "basic_stats";
	public static final String NAME_TABLE_TOPK_STATS = "topk_stats";
	public static final String NAME_TABLE_DATATYPE = "data_type";
	public static final String NAME_TABLE_SPILLOVER = "spillover";
	public static final String NAME_COLUMN_RECORD_ID = "record_id";
	public static final String NAME_COLUMN_SPILL = "spill";
	//public static final String NAME_COLUMN_PRED_SIGN = "pred_sign";
	public static final String NAME_COLUMN_ENTRY = "entry";
	public static final String NAME_COLUMN_ENTITY = "entity";
	public static final String NAME_COLUMN_GRAPH_ID = "gid";
	public static final String NAME_COLUMN_PREFIX_PREDICATE = "prop";
	public static final String NAME_COLUMN_PREFIX_VALUE = "val";
	public static final String NAME_COLUMN_LIST_ID = "list_id";
	public static final String NAME_COLUMN_PREFIX_LIST_ELEMENT = "elem";
	public static final String NAME_COLUMN_SHORT_STRING = "short_string";
	public static final String NAME_COLUMN_LONG_STRING = "long_string";
	public static final String NAME_COLUMN_LONG_STRING_OVERFLOW = "overflow";
	public static final String NAME_COLUMN_SUBJECT = "subject";
	public static final String NAME_COLUMN_PREDICATE = "predicate";
	public static final String NAME_COLUMN_OBJECT = "object";
	public static final String NAME_COLUMN_GRAPH = "graph";
	public static final String NAME_COLUMN_PROVENANCE = "prov";
	public static final String NAME_COLUMN_COLNAME = "colName";
	public static final String NAME_COLUMN_MAPNAME = "mapName";
	public static final String NAME_COLUMN_PREFIX_LIST_TYPE = "typ";
	public static final String NAME_COLUMN_PREFIX_TYPE = "typ";
	public static final String NAME_COLUMN_ENTRY_NUMERIC = "numentry";
	public static final String NAME_COLUMN_ENTRY_DATETIME = "dtentry";
	public static final String TYP_COLUMN_SUFFIX_IN_SPARQL_RS = "_TYP";
	public static final String NAME_COLUMN_DATATYPE_ID = "datatype_id";
	public static final String NAME_COLUMN_DATATYPE_NAME = "datatype_name";
	
	public static final String NAME_UDF_REGEX = "RDF_REGEX";
	public static final String NAME_UDF_REGEXJAR_NS = "DB2RDFJAR";
	public static final String NAME_UDF_REGEXJAR_CLASS = "db2rdf.RDFUtil";
	
	public static final String NAME_LONG_STRING_TABLE = "LSTR";
	
	// -----------------------------------META DATA table info ------------------------------------------
	public static final String DPH_BUCKET_SIZE = "dph_bucket_size";
	public static final String RPH_BUCKET_SIZE = "rph_bucket_size";
	public static final String MAX_STRING_LEN = "max_String_length";
	public static final String MAX_GID_LEN = "max_gid_length";
	public static final String PREFIX_SYSTEM_PREDICATE = "SYSPRED_";
	public static final String STORE_STATUS = "status";
	
	/** The terminator char for SQL statements. */
	public static final String SQL_TERMINATOR = "@";
	
	/** The column size for long strings. */
	public static final int LONG_STRING_COLUMN_SIZE = 2000;
	public static final int LONG_STRING_OVERFLOW_COLUMN_SIZE = 10000;
	
	/** The default graph moniker. */
	public static final String DEFAULT_GRAPH_MONIKER = "DEF";
	
	/** The extension for the configuration file. */
	public static final String CONFIG_FILE_EXTENSION = "rconfig";
	
	/** Maximum size of sameSubject, sameObject and subjectChain, objectChain stored UDFs. */
	public static final int MAXIMUM_COMPLEX_UDF = 5;
	
	/** Flags for provenance of spillover tuples. */
	public static final int SPILLOVER_DIRECT = 0;
	public static final int SPILLOVER_REVERSE = 1;
	
	/** Limits for how many elements can be inside the IN clause of a query. */
	public static final int MAX_IN_LIST_SECONDARY_HASH = 10;
	public static final int MAX_IN_LIST_SHORT_STRING = 10;
	
	/** Constants about the stored procedures generated jar. */
	public static final String JAR_NAME = "rdfstore-proc.jar";
	public static final String JAR_URI = "rdfstore-proc";
	
	
	public static int getIterationCount(int limitPerIteration, int limit) {
		int x = limit/limitPerIteration;
		if((limit % limitPerIteration) != 0) x++;
		return x;
	}
	
	public static final String RDFSTORE_HOME_PROP = "RDFSTORE_HOME";
	
	/* -------------------------- statistics constants ----------------------- */
	/** Constant indicating how many top entities do we gather statistics for. */
	public static final int STATS_TOP_K = 20;
	
	/** Constant indicating how many entities to keep when computing approximate top k statistics (for instance, for predicates) */
	public static final int STATS_INEXACT_TOPK_LIMIT = 20 * STATS_TOP_K;
	
	/** The file SUBJECT moniker. */
	public static final String SUBJECT_MONIKER = "http://SUBJECT";
	
	/** The file OBJECT moniker. */
	public static final String OBJECT_MONIKER = "http://OBJECT";
	
	/** Constant indicating the threshold number of lids beyond which we consider the lid as frequently occurring*/
	public static final int LID_COUNT_THRESHOLD = 100000;
	
	/*---------------------- Predicate mapping RDF Constants -----------------------*/
	
	public static final String ibmns = "http://com.ibm.rdf/";
	public static final String HASHING_FUNCTION = "function/hash";
	public static final String COLORING_FUNCTION = "function/color";
	public static final String COLOR_FUNCTION_URI = ibmns + COLORING_FUNCTION;
	public static final String FUNCTION_TYPE_PREDICATE = "functiontype";
	public static final String COLORING_FUNCTION_TUPE_PREDICATE = "function/color/type";
	public static final String FUNCTION_PRIORITY = "function/priority";
	public static final String COLUMN_PREDICATE = "column";
	public static final String NUM_COL_FUNCTION = "number/coloring";
	public static final String NUM_HASH_FUNCTION = "number/hashing";
	public static final String VALUE_PREDICATE = "value";
	public static final String DIRECT_TYPE = "direct";
	public static final String REVERSE_TYPE = "reverse";

	// ------------------------ DEFAULT values for store --------------------------
	public static final int DEFAULT_GID_LENGTH = 118;
	public static final int DEFAULT_COL_LENGTH = 118;
	public static final int DEFAULT_DPH_BUCKET_SIZE = 128;
	public static final int DEFAULT_RPH_BUCKET_SIZE = 33;
	
	// ----------------------   STORE STATUSES --------------------------
	public static final int DEFAULT_STORE_STATUS = 0;
	public static final int REORG_STORE_STATUS = 1;
	public static final int ROLLBACK_STORE_STATUS = -1;
	
	//-----------------------  REORG FLAG ---------------------------------
	public static final String IS_REORG_SCHEMA = "isReorgSchema";
	
	//-----------------------  DATA Type sequence -------------------------
	public static final String POST_FIX_TYPE_SEQUENCE_NAME = "_type_seq";
	public static final String POST_FIX_LANG_SEQUENCE_NAME = "_lang_seq";
}
