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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author oudrea
 * Class holding a number of common RDF store parameters. It is required for all utilities. 
 */
public class RDFStoreParameters implements Serializable {

	private static final long serialVersionUID = 676062077630635209L;

	/** This is a map of URI prefixes mapped to namespace names commonly used in this rdf store. */
	public Map<String, String> namespaces = new HashMap<String, String>();
	
	/** This is the size of the direct primary hashtable. */
	public int sizeDirectPrimaryHash;
	
	/** This is the size of the direct secondary hashtable. */
	public int sizeDirectSecondaryHash;
	
	/** This is the size of the reverse primary hashtable. */
	public int sizeReversePrimaryHash;
	
	/** This is the size of the reverse secondary hashtable. */
	public int sizeReverseSecondaryHash;
	
	/** This is the maximum length of a string that can fit into a value column. */
	public int sizeMaxString;
	
	/** This is the name of the hashing function used. */
	public String hashingFunction;
	
	/** This is the size of column for graph IDs. */
	public int sizeGraphID;
	
	/** This is the database name. */
	public String databaseName;
	
	/** This is the database name. */
	public String userName;
	
	/** Indicates true if we have any long strings in the dataset */
	public boolean hasLongString;

	
	
}
