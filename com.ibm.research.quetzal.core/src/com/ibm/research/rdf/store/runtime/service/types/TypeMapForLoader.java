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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

import com.ibm.wala.util.collections.HashMapFactory;
import com.ibm.wala.util.collections.Pair;

public class TypeMapForLoader extends TypeMap {

	private static final Map<String,Short> typeTable = HashMapFactory.make();
	static {
		for(String typeIRI : TypeMap.typediriArray) { 
			if (! "".equals(typeIRI)) {
				typeTable.put(typeIRI, idForIRI(typeIRI));
			}
		}
	}
	
	private static short nextType = USER_ID_START;
	
	public static boolean ensureType(String typeIRI) {
		if (typeTable.containsKey(typeIRI)) {
			return false; 
		} else {
			typeTable.put(typeIRI, nextType++);
			return true;
		}
	}
		
	private static final Map<String,Pair<Short,Map<String,Short>>> langTable = HashMapFactory.make();
	private static short nextLanguage = LANG_IDS_START;
	static {
		for (Pair<String, String[]> langs : languages) {
			Map<String,Short> subLangs = HashMapFactory.make();
			langTable.put(langs.fst, Pair.make(nextLanguage, subLangs));
			if (langs.snd != null) {
				assert langs.snd.length < SUB_LANG_RANGE;
				for (int i = 0; i < langs.snd.length; i++) {
					subLangs.put(langs.snd[i], (short) (nextLanguage + i + 1));
				}
			}
			nextLanguage += SUB_LANG_RANGE;
		}
	}

	public static short ensureLanguage(String lang) {
		int split;
		lang = lang.toUpperCase();
		if ((split = lang.indexOf('-')) >= 0) {
			String firstPart = lang.substring(0, split);
			String secondPart = lang.substring(split+1, lang.length());
			
			if (langTable.containsKey(firstPart)) {
				Map<String,Short> subLangs = langTable.get(firstPart).snd;
				if (subLangs.containsKey(secondPart)) {
					return subLangs.get(secondPart);
				} else {
					short newLangId = (short) (langTable.get(firstPart).fst + subLangs.size() + 1);
					subLangs.put(secondPart, newLangId);
					return newLangId;
				}
			} else {
				Map<String,Short> subLangs = HashMapFactory.make();
				langTable.put(firstPart, Pair.make(nextLanguage, subLangs));
				short newLangId = (short) (nextLanguage + 1);
				subLangs.put(secondPart, newLangId);
				nextLanguage += SUB_LANG_RANGE;
				return newLangId;
			}
		} else {
			Map<String,Short> subLangs = HashMapFactory.make();
			short newLangId = nextLanguage;
			langTable.put(lang, Pair.make(newLangId, subLangs));
			nextLanguage += SUB_LANG_RANGE;
			return newLangId;			
		}
	}

	public static void main(String[] args) throws IOException {
		if (args.length > 0) {
			BufferedReader r = new BufferedReader(new FileReader(args[0]));
			String typeIRI;
			while ((typeIRI = r.readLine()) !=  null) {
				ensureType(typeIRI);
			}
			if (args.length > 1) {
				r = new BufferedReader(new FileReader(args[1]));
				String lang;
				while ((lang = r.readLine()) !=  null) {
					ensureLanguage(lang);
				}
			}
		}
		
		dump();
	}
	
	public static void dump() {
		for (Field f : TypeMap.class.getDeclaredFields()) {
			if (f.getName().endsWith("_ID")) {
				try {
					// see if _ID is really a shorthand for an IRI type, which is in the table...
					TypeMap.class.getField(f.getName().replace("_ID", "_IRI"));
				} catch (SecurityException e) {
					continue;
				} catch (NoSuchFieldException e) {
					try {
						//...no, so print it
						System.out.println(f.getName() + " " + f.getShort(null));
					} catch (IllegalArgumentException e1) {
						continue;
					} catch (IllegalAccessException e1) {
						continue;
					}
				} catch (IllegalArgumentException e) {
					continue;
				}
			}
		}

		// now print the table
		for(String typeIRI : typeTable.keySet()) {
			System.out.println(typeIRI + " " + typeTable.get(typeIRI));
		}

		for (Map.Entry<String,Pair<Short,Map<String,Short>>> elt : langTable.entrySet()) {
			System.out.println(elt.getKey() + " " + elt.getValue().fst);
			for(Map.Entry<String, Short> subElt : elt.getValue().snd.entrySet()) {
				System.out.println(elt.getKey() + "-" + subElt.getKey() + " " + subElt.getValue());
			}
		}	
	}
}
