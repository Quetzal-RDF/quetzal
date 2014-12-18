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
 package com.ibm.research.rdf.store.hashing;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.sparql.core.DatasetGraphFactory;
import com.ibm.research.rdf.store.config.Constants;

public class GraphColoringHashingFamily implements IHashingFamily {
	HashMap<String, ArrayList<Integer>> predHashes;
	Set<String> coloredPreds = new HashSet<String>();
	int maxColors = 0;
	
	int size;

	Property priority = new PropertyImpl(Constants.ibmns
			+ Constants.FUNCTION_PRIORITY);

	Property colortype = new PropertyImpl(Constants.ibmns
			+ Constants.COLORING_FUNCTION_TUPE_PREDICATE);

	Property column = new PropertyImpl(Constants.ibmns
			+ Constants.COLUMN_PREDICATE);

	Property value = new PropertyImpl(Constants.ibmns
			+ Constants.VALUE_PREDICATE);

	int numOfHashingFunctions;

	int numOfColoringFunctions;
	
	IHashingFamily rabinHash = null;
	
	public GraphColoringHashingFamily(String fileName) {
		try {
			predHashes = new HashMap<String, ArrayList<Integer>>();
			// Open the file that is the first
			// command line parameter
			FileInputStream fstream = new FileInputStream(fileName);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(strLine);
				size = st.countTokens() - 1;
				String predicate = st.nextToken();
				ArrayList<Integer> hashes = new ArrayList<Integer>();
				while (st.hasMoreTokens()) {
					hashes.add(Integer.parseInt(st.nextToken()));
				}
				predHashes.put(predicate, hashes);
			}
			// Close the input stream
			in.close();
			
			//Initialize the rabin hash
			this.numOfColoringFunctions=2;
			this.numOfHashingFunctions=1;
			rabinHash = new RabinHashFamily(getMaxColor(), numOfHashingFunctions);
			
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	/**
	 * Initialize hashing family from predicate mappings in supplied file
	 * 
	 * @param predicateMappingFileName
	 * @param isDirect
	 * @param familySize
	 */
	public GraphColoringHashingFamily(File nquadFile, boolean isDirect,
			int familySize) {
		predHashes = new HashMap<String, ArrayList<Integer>>();
		this.size = familySize;
		DatasetGraph dsg = loadDataset(nquadFile);
		initializeFamily(dsg, isDirect);
	}

	/**
	 * Initialize hashing family from predicate mappings in database.
	 * 
	 * @param store
	 * @param schema
	 * @param isDirect
	 * @param familySize
	 */
	public GraphColoringHashingFamily(DatasetGraph dsg,
			boolean isDirect, int familySize) {
		predHashes = new HashMap<String, ArrayList<Integer>>();
		this.size = familySize;
		//DatasetGraph dsg = loadDataset(inputStream);
		initializeFamily(dsg, isDirect);
	}

	public int hash(String s, int HashId) {
		if (HashId > size) {
			//System.out.println("The family contains " + size + " hash functions ");
		}
		ArrayList<Integer> hashes = predHashes.get(s);
		
		if(hashes != null)
		{
			return (Integer) (hashes.toArray())[HashId];
		}
		else
		{
			if(HashId > numOfColoringFunctions-1)
			{
				return rabinHash.hash(s, HashId-numOfColoringFunctions);
			}
			else
			{
				return -1;
			}
			
		}
		
	}

	public int getFamilySize(String predicate) {
		int[] hashes = getHash(predicate);
		if (hashes[0] == -1)	{		// dataset does not contain this predicate
			return 1;
		}
		if (coloredPreds.contains(predicate) && numOfColoringFunctions == 2) {
			if (hashes[1] == maxColors) {
				return 1;
			}
		}
		return size;
	}
	

	public void computeHash(String s) {
		// TODO Auto-generated method stub

	}

	public int[] getHash(String s) {
		int[] hashVals = new int[size];
		for (int i = 0; i < hashVals.length; i++) {
			hashVals[i] = hash(s, i);
		}
		if (hashVals[0] == -1) {
			hashVals = new int[1];
			hashVals[0] = 0;
		}
		return hashVals;
	}

	/**
	 * Initialize family
	 * 
	 * @param isDirect
	 */
	private void initializeFamily(DatasetGraph dsg, boolean isDirect) {

		Graph defGraph = dsg.getDefaultGraph();
		Model model = ModelFactory.createModelForGraph(defGraph);

		StmtIterator iter = model.listStatements((Resource) null, value,
				(RDFNode) null);
		while (iter.hasNext()) {

			Statement stmt = iter.next();
			if (stmt.getSubject().toString().contains(
					Constants.NUM_COL_FUNCTION)) {
				numOfColoringFunctions = stmt.getInt();
			} else {
				numOfHashingFunctions = stmt.getInt();
			}
		}

		// Validate the input family size
		if (size < 1 || size > numOfColoringFunctions + numOfHashingFunctions) {
			throw new IllegalArgumentException("Incorrect family size");
		}

		Model[] models = new Model[size];
		model.listStatements();
		StmtIterator stmtIterator = model.listStatements((Resource) null,
				colortype, (RDFNode) null);

		String type = isDirect ? Constants.DIRECT_TYPE : Constants.REVERSE_TYPE;
		
		String[] modelNames = new String[size];

		while (stmtIterator.hasNext()) {

			Statement s = stmtIterator.next();

			if (type.equals(s.getString())) {
				StmtIterator niterator = model.listStatements(s.getSubject(),
						priority, (RDFNode) null);
				if (niterator.hasNext()) {
					Statement stmt = niterator.next();

					int funpriority = stmt.getInt();
					modelNames[funpriority -1] = stmt.getSubject().toString();
					models[funpriority - 1] = ModelFactory
							.createModelForGraph(dsg.getGraph(stmt.getSubject()
									.asNode()));
				}

			}
		}
		
		

		for (int index = 0; index < models.length; index++) {
			
			if(models[index] == null)
				break;
			
			StmtIterator stmtIter = models[index].listStatements(null, column,
					(RDFNode) null);

			while (stmtIter.hasNext()) {
				Statement curentStatment = stmtIter.next();

				String currPredicate = curentStatment.getSubject().toString();
				if (modelNames[index].startsWith(Constants.COLOR_FUNCTION_URI)) {
					coloredPreds.add(currPredicate);
				}
				int idx = curentStatment.getInt();

				if (maxColors < idx) {
					maxColors = idx;
				}
				
				if (predHashes.containsKey(currPredicate)) {
					predHashes.get(currPredicate).add(idx);
					
				} else {
					ArrayList hashes = new ArrayList<Integer>();
					hashes.add(idx);
					predHashes.put(curentStatment.getSubject().toString(),
							hashes);
					
				}
			}
		}
		
		//If predicate hashes are empty , then create a rabin hash for the entire family size.
		if(predHashes.isEmpty())
		{
			numOfColoringFunctions = 0;
			numOfHashingFunctions = size;
		}
		
		//Initialize the rabin hash
		int rabinHashSize = maxColors > 0 ? maxColors : (isDirect ? Constants.DEFAULT_DPH_BUCKET_SIZE : Constants.DEFAULT_RPH_BUCKET_SIZE);
		rabinHash = new RabinHashFamily(rabinHashSize, numOfHashingFunctions);
	}

	/**
	 * Load dataset from N-quad file
	 * 
	 * @param predicateMappingFileName
	 * @return
	 */
	public static DatasetGraph loadDataset(String nquadFile) {
		return loadDataset(new File(nquadFile));
	}
	
	private static DatasetGraph loadDataset(File nquadFile) {

		DatasetGraph dsg =  RDFDataMgr.loadDatasetGraph(nquadFile.toURI().toString(), Lang.NQUADS);
		return dsg;
	}

	/**
	 * Load dataset from CLOB field in database
	 * 
	 * @param store
	 * @param schema
	 * @return
	 */
	public static DatasetGraph loadDataset(InputStream inputStream) {

		// Read the dataset from CLOB
		DatasetGraph dsg = DatasetGraphFactory.createMem();
		RDFDataMgr.read(dsg,inputStream,  Lang.NQUADS);
		
		
		return dsg;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getNumberOfPredicates()
	{
		return predHashes.size();
	}
	
	/**
	 * 
	 * @return
	 */
	public int getMaxColor()
	{
		return maxColors;
	}
	
	public static void main(String[] args) {
	}
	
}
