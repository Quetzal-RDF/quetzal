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
 package com.ibm.research.rdf.store.runtime.service.types;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import com.ibm.research.rdf.store.jena.RdfStoreException;
import com.ibm.wala.util.collections.Pair;

public class TypeMap {

	// Following below is essentially ascending order of values followed by
	// ORDER BY clause.
	// All the assigned integer constants are temporary, we can change based on
	// the need till the release.
	// After the release we have to stick to the values decided.
	// Always refer to the type IDs using the constants defined here.

	// lid columns and unbound variables
	public static final short LANG_START = 1000;
	public static final short LANG_END = 4999;
	
	public static final short NONE_ID = 10000;

	// Blank Nodes
	public static final short BLANK_NODE_ID = 10001;

	// IRI
	public static final short IRI_ID = 10002;
	
	// Simple Literals
	public static final short SIMPLE_LITERAL_ID = 5001;

	public static final short DATATYPE_IDS_START = 5002;
	public static final short DATATYPE_IDS_END = 9999;

	public static final String STRING_IRI = "http://www.w3.org/2001/XMLSchema#string";
	public static final String BOOLEAN_IRI = "http://www.w3.org/2001/XMLSchema#boolean";
	public static final String DATETIME_IRI = "http://www.w3.org/2001/XMLSchema#dateTime";
	public static final String DATE_IRI = "http://www.w3.org/2001/XMLSchema#date";

	// numeric types
	public static final String INTEGER_IRI = "http://www.w3.org/2001/XMLSchema#integer";
	public static final String DECIMAL_IRI = "http://www.w3.org/2001/XMLSchema#decimal";
	public static final String FLOAT_IRI = "http://www.w3.org/2001/XMLSchema#float";
	public static final String DOUBLE_IRI = "http://www.w3.org/2001/XMLSchema#double";
	public static final String NPI_IRI = "http://www.w3.org/2001/XMLSchema#nonPositiveInteger";
	public static final String NINTEGER_IRI = "http://www.w3.org/2001/XMLSchema#negativeInteger";
	public static final String LONG_IRI = "http://www.w3.org/2001/XMLSchema#long";
	public static final String INT_IRI = "http://www.w3.org/2001/XMLSchema#int";
	public static final String SHORT_IRI = "http://www.w3.org/2001/XMLSchema#short";
	public static final String BYTE_IRI = "http://www.w3.org/2001/XMLSchema#byte";
	public static final String NNI_IRI = "http://www.w3.org/2001/XMLSchema#nonNegativeInteger";
	public static final String ULONG_IRI = "http://www.w3.org/2001/XMLSchema#unsignedLong";
	public static final String UINT_IRI = "http://www.w3.org/2001/XMLSchema#unsignedInt";
	public static final String USHORT_IRI = "http://www.w3.org/2001/XMLSchema#unsignedShort";
	public static final String UBYTE_IRI = "http://www.w3.org/2001/XMLSchema#unsignedByte";
	public static final String PINTEGER_IRI = "http://www.w3.org/2001/XMLSchema#positiveInteger";

	private static final String FIRST_NUMERIC_IRI;
	private static final String FIRST_DECIMAL_IRI;
	private static final String LAST_NUMERIC_IRI;

	private static Map<String, Short> dataTypeIDMap = new HashMap<String, Short>();
	private static Map<Short, String> IDDataTypeMap = new HashMap<Short, String>();
	
	private static Map<String, Short> langIDMap = new HashMap<String, Short>();
	private static Map<Short, String> IDLangMap = new HashMap<Short, String>();

	protected static final String[] typediriArray = { "",
		STRING_IRI,
		BOOLEAN_IRI ,
		DATE_IRI , 
		DATETIME_IRI ,
		FIRST_NUMERIC_IRI = PINTEGER_IRI,				
		NPI_IRI ,
		NINTEGER_IRI ,
		LONG_IRI ,
		INT_IRI ,
		SHORT_IRI ,
		BYTE_IRI ,
		NNI_IRI ,
		ULONG_IRI ,
		UINT_IRI ,
		USHORT_IRI ,
		UBYTE_IRI ,
		INTEGER_IRI,
		FIRST_DECIMAL_IRI = DECIMAL_IRI,	
		FLOAT_IRI ,
		LAST_NUMERIC_IRI = DOUBLE_IRI
	};

	public static String[] getTypedIRIArray() {
		return typediriArray;
	}
	
	public static LangMap[] getLangArray() {
		return langArray;
	}

	public enum TypeCategory {
		NUMERIC, STRING, NONE, IRI, DATETIME, DATE
	}

	protected static short idForIRI(String iri) {
		for (short i = 0; i < typediriArray.length; i++) {
			if (typediriArray[i].equals(iri)) {
				return (short) (i + DATATYPE_IDS_START);
			}
		}

		assert false;
		return -1;
	}
	
	public static TypeMap.TypeCategory getCastTypeCategory(Set<TypeMap.TypeCategory> types) {
		TypeMap.TypeCategory castType = null;
		 if (types.contains(TypeMap.TypeCategory.DATE)) {
			 castType = TypeMap.TypeCategory.DATE;
		 } else if (types.contains(TypeMap.TypeCategory.DATETIME)) {
			 castType = TypeMap.TypeCategory.DATETIME;
		 } else if (types.contains(TypeMap.TypeCategory.NUMERIC)) {
			 castType = TypeMap.TypeCategory.NUMERIC;
		 }
		return castType;
	}

	public static final short DATATYPE_NUMERICS_IDS_START = idForIRI(FIRST_NUMERIC_IRI);
	public static final short DATATYPE_DECIMAL_IDS_START = idForIRI(FIRST_DECIMAL_IRI);
	public static final short DATATYPE_NUMERICS_IDS_END = idForIRI(LAST_NUMERIC_IRI);

	public static final short STRING_ID = idForIRI("http://www.w3.org/2001/XMLSchema#string");
	public static final short BOOLEAN_ID = idForIRI("http://www.w3.org/2001/XMLSchema#boolean");
	public static final short DATETIME_ID = idForIRI("http://www.w3.org/2001/XMLSchema#dateTime");
	public static final short DATE_ID = idForIRI("http://www.w3.org/2001/XMLSchema#date");
	public static final short INTEGER_ID = idForIRI("http://www.w3.org/2001/XMLSchema#integer");
	public static final short DECIMAL_ID = idForIRI("http://www.w3.org/2001/XMLSchema#decimal");
	public static final short FLOAT_ID = idForIRI("http://www.w3.org/2001/XMLSchema#float");
	public static final short DOUBLE_ID = idForIRI("http://www.w3.org/2001/XMLSchema#double");
	public static final short NPI_ID = idForIRI("http://www.w3.org/2001/XMLSchema#nonPositiveInteger");
	public static final short NINTEGER_ID = idForIRI("http://www.w3.org/2001/XMLSchema#negativeInteger");
	public static final short LONG_ID = idForIRI("http://www.w3.org/2001/XMLSchema#long");
	public static final short INT_ID = idForIRI("http://www.w3.org/2001/XMLSchema#int");
	public static final short SHORT_ID = idForIRI("http://www.w3.org/2001/XMLSchema#short");
	public static final short BYTE_ID = idForIRI("http://www.w3.org/2001/XMLSchema#byte");
	public static final short NNI_ID = idForIRI("http://www.w3.org/2001/XMLSchema#nonNegativeInteger");
	public static final short ULONG_ID = idForIRI("http://www.w3.org/2001/XMLSchema#unsignedLong");
	public static final short UINT_ID = idForIRI("http://www.w3.org/2001/XMLSchema#unsignedInt");
	public static final short USHORT_ID = idForIRI("http://www.w3.org/2001/XMLSchema#unsignedShort");
	public static final short UBYTE_ID = idForIRI("http://www.w3.org/2001/XMLSchema#unsignedByte");
	public static final short PINTEGER_ID = idForIRI("http://www.w3.org/2001/XMLSchema#positiveInteger");

	private static final LangMap[] langArray;
	public static final short LANG_IDS_START = 1000;
	public static final short LANG_IDS_END = 5000;
	public static final short SUB_LANG_RANGE = 10;
	@SuppressWarnings("unchecked")
	public static final Pair<String, String[]>[] languages = new Pair[] {
			Pair.make("BG", new String[] { "BG" }),
			Pair.make("CS", new String[] { "CZ" }),
			Pair.make("DA", new String[] { "DK" }),
			Pair.make("DE", new String[] { "DE" }),
			Pair.make("EL", new String[] { "GR" }),
			Pair.make("EN", new String[] { "US", "AU", "GB" }),
			Pair.make("ES", new String[] { "ES" }),
			Pair.make("FI", new String[] { "FI" }),
			Pair.make("FR", new String[] { "FR", "CA" }),
			Pair.make("HR", new String[] { "HR" }),
			Pair.make("HU", new String[] { "HU" }),
			Pair.make("IT", new String[] { "IT" }),
			Pair.make("JA", new String[] { "JP" }),
			Pair.make("KO", new String[] { "KR" }),
			Pair.make("NL", new String[] { "NL" }),
			Pair.make("NO", new String[] { "NO" }),
			Pair.make("PL", new String[] { "PL" }),
			Pair.make("PT", new String[] { "PT", "BR" }),
			Pair.make("RO", new String[] { "RO" }),
			Pair.make("RU", new String[] { "RU" }),
			Pair.make("SK", new String[] { "SK" }),
			Pair.make("SL", new String[] { "SL" }),
			Pair.make("SV", new String[] { "SE" }),
			Pair.make("TR", new String[] { "TR" }),
			Pair.make("ZH", new String[] { "CN", "TW" }),
			Pair.make("ZZ", null), 
			Pair.make("XX", new String[] { "EN" }) };
	static {
		List<LangMap> map = new ArrayList<LangMap>();
		short id = LANG_IDS_START;
		for (Pair<String, String[]> langs : languages) {
			map.add(new LangMap(langs.fst, id));
			if (langs.snd != null) {
				assert langs.snd.length < SUB_LANG_RANGE;
				for (int i = 0; i < langs.snd.length; i++) {
					map.add(new LangMap(langs.fst + "-" + langs.snd[i],
							(short) (id + i + 1)));
				}
			}
			id += SUB_LANG_RANGE;
		}
		langArray = map.toArray(new LangMap[map.size()]);
	}

	// etc., add other types...

	public static final short USER_ID_START = 5500;

	/**
	 * Load type map table in database. One time task. Return number of rows
	 * added.
	 * 
	 */
	public static int load() {
		return 0; // for now
	}

	/**
	 * When we add any new maps after a release. Do a diff with what we have in
	 * database and add any new maps we have in addition to it. Return number of
	 * rows added.
	 * 
	 */
	public static int addNewMaps() {
		return 0;
	}

	/**
	 * Check the integrity of the type ID map values with the database.
	 * 
	 */
	public static boolean checkIntegrity() {
		return true; // for now
	}

	/**
	 * Fix type map integrity issues in database. Return success or failure.
	 * 
	 */
	public static boolean fixIntegrityIssues() {
		return true; // for now
	}

	public static short getDatatypeType(String literalDatatypeURI) {
		Short id = dataTypeIDMap.get(literalDatatypeURI);

		if (id == null) {
			return NEW_TYPE;
		}
		return id;
	}
	
	public static boolean isDateTime(short type) {
		if (type == DATETIME_ID || type == getDatatypeType("http://www.w3.org/2001/XMLSchema#date")) {
			return true;
		}
		return false;
	}
	
	public static TypeMap.TypeCategory getTypeCategory(short type) {
		if (TypeMap.isDateTime(type)) {
			return TypeMap.TypeCategory.DATETIME;
		} else if (type>=TypeMap.DATATYPE_NUMERICS_IDS_START &&
							type<=TypeMap.DATATYPE_NUMERICS_IDS_END) {
			return TypeMap.TypeCategory.NUMERIC;
		} else if (type == STRING_ID || type == SIMPLE_LITERAL_ID) {
			return TypeMap.TypeCategory.STRING;
		} else if (type == IRI_ID) {
			return TypeMap.TypeCategory.IRI;
		} else {
			return TypeMap.TypeCategory.NONE;
		}
	}

	public static short getLanguageType(String lang) {
		/*for (int i = 0; i < langArray.length; i++) {
			if (lang.equalsIgnoreCase(langArray[i].langstring)) {
				return langArray[i].langcode;
			}
		}*/
		
		Short id = langIDMap.get(lang.toUpperCase());

		if (id == null) {
			return NEW_LANG;
		}
		return id;
	}

	public static String getLanguageString(short code) {
		/*// this can be better optimized to just a index lookup
		for (int i = 0; i < langArray.length; i++) {
			if (code == langArray[i].langcode) {
				return langArray[i].langstring;
			}
		}*/
		String lang = IDLangMap.get(code);
		
		if (lang != null) {
			return lang;
		}

		return "UNKNOWN";
	}

	public static Object getNumericValue(short type, String value) {

		if (type >= DATATYPE_NUMERICS_IDS_START
				&& type <= DATATYPE_NUMERICS_IDS_END) {

			try {
				new BigDecimal(value);
			} catch (NumberFormatException e) {
				throw new RdfStoreException(e.getLocalizedMessage(), e);
			}
			return value;

		}

		// Numeric column should be null in database for any other type
		return null;
	}

	public static Object getTimeStampValue(short type, String value) {

		if (type == DATETIME_ID)
			return formatXsdDtToDate(value);

		// TS column value should be null in database for any other type
		return null;
	}

	public static String getTypedString(short datatype) {
		return IDDataTypeMap.get(datatype);
	}

	private static Object formatXsdDtToDate(String value) {

		String baseformat = "yyyy-MM-dd'T'HH:mm:ss";
		int baseformatlen = "yyyy-MM-dd'T'HH:mm:ss".length();

		String mSecsTZ = value.substring(baseformatlen - 2);

		// check if there is millisecs and TX
		int numMillisecs = 0;
		boolean hastz = false;
		if (!mSecsTZ.isEmpty()) {

			if (mSecsTZ.charAt(0) == '.') {

				// contains millisecs
				int i = 1;
				for (; i < mSecsTZ.length(); i++) {

					if (mSecsTZ.charAt(i) == '+' || mSecsTZ.charAt(i) == '-'
							|| mSecsTZ.charAt(i) == 'Z') {
						numMillisecs = i - 1;
						if (mSecsTZ.charAt(i) == 'Z') {
							mSecsTZ = mSecsTZ.replaceFirst("Z", ""); // strip Z
						} else {
							hastz = true;
							mSecsTZ = mSecsTZ.replaceFirst(":", ""); // strip :
						}
						break;
					}
				}
				if (!hastz) {
					numMillisecs = i - 1;
				}

				// Add the . and the number of millisecs
				if (numMillisecs > 0)
					baseformat = baseformat + ".";
				for (i = 0; i < numMillisecs; i++) {
					baseformat = baseformat + "S";
				}
			} else {

				// no msecs, check if TZ
				if (mSecsTZ.indexOf("+") != -1 || mSecsTZ.indexOf("-") != -1) {
					hastz = true;
					mSecsTZ = mSecsTZ.replaceFirst(":", ""); // strip the :
				} else if (mSecsTZ.indexOf("Z") != -1) {
					// hastz = true;
					mSecsTZ = mSecsTZ.replaceFirst("Z", ""); // strip Z
				}
			}
		}

		if (hastz) {
			baseformat = baseformat + "Z";
		}

		value = value.substring(0, baseformatlen - 2);
		value += mSecsTZ;

		SimpleDateFormat sd = new SimpleDateFormat(baseformat);
		java.util.Date d;
		try {
			sd.setTimeZone(TimeZone.getTimeZone("GMT"));
			d = sd.parse(value);

		} catch (ParseException e) {
			throw new RdfStoreException(e.getLocalizedMessage(), e);
		}

		return new java.sql.Date(d.getTime());

	}

	public static void add(String string, short id) {
		if (id > TypeMap.LANG_IDS_END) {
			dataTypeIDMap.put(string, id);
			IDDataTypeMap.put(id, string);
		} else {
			langIDMap.put(string, id);
			IDLangMap.put(id, string);
		}
	}
	
	// Unknown Type
	private static final short NEW_TYPE = Short.MIN_VALUE;
	
	// Unknown Type
	private static final short NEW_LANG = Short.MIN_VALUE + 1;

	public static final String IRI_TYPE_IRI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#resource";

	public static boolean isNewType(short type) {
		return type == NEW_TYPE;
	}

	public static boolean isNewLang(short lang) {
		return lang == NEW_LANG;
	}
	
	public static String getCastNameForTypedString(String str) {
		String ret = str.substring(str.indexOf("#") + 1);
		return "xs_" + ret;
	}
}
