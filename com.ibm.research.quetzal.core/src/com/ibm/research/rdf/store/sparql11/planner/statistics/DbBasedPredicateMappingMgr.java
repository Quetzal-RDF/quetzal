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
 package com.ibm.research.rdf.store.sparql11.planner.statistics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.hashing.ColoringFunction;
import com.ibm.research.rdf.store.hashing.Edge;
import com.ibm.research.rdf.store.hashing.PredicateMappingsDataSet;
import com.ibm.research.rdf.store.jena.RdfStoreException;
import com.ibm.research.rdf.store.jena.impl.DB2CloseObjects;

/**
 * DbBasedPredicateMappingMgr computes the mapping between predicates and column
 * numbers for the predicates available in the database.
 *  
 * @author prsahoo
 * 
 */
public class DbBasedPredicateMappingMgr {

	private Store store = null;
	private Connection con = null;
	private String edgeSetTempFile = "edgeset.tmp";
	private boolean useColoring = false;
	private int numOfFunctions;
	private int numOfColorFunctions;
	private int numOfHashFunctions;

	/**
	 * Constructor
	 * 
	 * @param store
	 *            Store RDFStore
	 * @param con
	 *            Connection
	 * @param filename
	 *            String Output file name to write predicate mapping RDF
	 * @param useColoring
	 *            boolean whether to use coloring or not
	 * @param numOfFunctions
	 *            int total number of functions
	 */
	public DbBasedPredicateMappingMgr(Store store, Connection con) 
	{
		this.store = store;
		this.con = con;
		this.useColoring = true;
		this.numOfFunctions = 3;
	}

	/**
	 * Creates Predicate mappings and write it as a RDF to the given output stream.
	 */
	public void createPredicateMappings(OutputStream predicateMappings) {

		// Compute number of coloring and hash functions required.
		numOfHashFunctions = numOfFunctions;
		numOfColorFunctions = 0;
		if (useColoring) {
			numOfHashFunctions = numOfFunctions - 2;
			numOfHashFunctions = numOfHashFunctions < 1 ? 1
					: numOfHashFunctions;
			numOfColorFunctions = numOfFunctions - numOfHashFunctions;
		}

		// Create a dataset of default graph
		PredicateMappingsDataSet dataset = new PredicateMappingsDataSet(numOfHashFunctions, numOfColorFunctions);

		// Calculate the predicate mappings for subject and write to dataset
		addMappingsToDataset(dataset, true);
		
		// Calculate the predicate mappings for object and write to dataset
		addMappingsToDataset(dataset, false);

		// Dump the dataset to a file in n-quad format
		dataset.exportToFile(predicateMappings);

	}

	/**
	 * Add coloring and hashing functions to the dataset
	 * 
	 * @param dataset
	 * @param isDirect
	 */
	private void addMappingsToDataset(PredicateMappingsDataSet dataset, boolean isDirect) {
		com.ibm.wala.util.collections.Pair<Map<String, Integer>, Map<String, Integer>> colorsPair = null;
			
		try {
			colorsPair = calculateColoringFunctionFromDB(isDirect);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (colorsPair != null) {
			
			// Calculate the ideal bucket size for the hash function
			int maxColors = new HashSet(colorsPair.fst.values()).size();
			int hashBuckeSize = (int) (maxColors * (100 + 25)/100);
			
			if(isDirect)
			{
				hashBuckeSize = (hashBuckeSize <= store.getDPrimarySize()) ? hashBuckeSize : store.getDPrimarySize();
			}
			else
			{
				hashBuckeSize = (hashBuckeSize <= store.getRPrimarySize()) ? hashBuckeSize : store.getDPrimarySize();
			}
			
			// Write coloring function
			dataset.writeColoringFunctionToDataset(isDirect, colorsPair);
			
			// Write hashing function
			dataset.writeHashingFunctionToDataset(colorsPair.fst.keySet(), isDirect, hashBuckeSize);
		}
	}

	/**
	 * Calculate the coloring function from the edge sets depending on the
	 * supplied type
	 * 
	 * @param type
	 * @return
	 */
	public com.ibm.wala.util.collections.Pair<Map<String, Integer>, Map<String, Integer>> calculateColoringFunctionFromDB(
			boolean isDirect) {

		com.ibm.wala.util.collections.Pair<Map<String, Integer>, Map<String, Integer>> colorsPair = null;
		Set<Edge> edgeSet = null;

		// Calculate Edge Set
		edgeSet = calculateEdgeSet(isDirect);
		
		// If empty edge set, do not proceed
		if (edgeSet == null || edgeSet.isEmpty()) {
			return null;
		}

		// Delete and create new file
		File file = new File(edgeSetTempFile);
		try {
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// Write the edge set to file
		writeEdgeSetToFile(file, edgeSet);

		// Compute coloring function.
		try {
			colorsPair = ColoringFunction.color(file.getAbsolutePath());
		} catch (Exception e) {
			throw new RdfStoreException("Operation:GeneratePredicateMappings Error: "
					+ e.getMessage(), e);
		}

		return colorsPair;
	}

	/**
	 * Calculate edge set for the supplied type and write it to file
	 * 
	 * @param type
	 * @return
	 * @throws SQLException
	 */
	private Set<Edge> calculateEdgeSet(boolean isDirect){
		Set<Edge> edgeSet = new TreeSet<Edge>();
		Edge currentEdge = null;
		String currPred;
		String currSubject;
		String prevSubject = null;
		
		Statement stmt = null;
		ResultSet rst = null;
		
		try{
			// Create statement
			stmt = con.createStatement();
	
			// Get the predicates and count
			rst = stmt.executeQuery(getQueryForPredicates(isDirect));
			
			// Calculate edge set
			while (rst.next()) {
	
				currSubject = rst.getString("subject");
				currPred = rst.getString("predicate");
				
				if(prevSubject != null && prevSubject.equals(currSubject))
				{
					currentEdge.addPredicate(currPred);
				}
				else
				{
					if (currentEdge != null) {
						Iterator<Edge> iter = edgeSet.iterator();
						boolean found = false;
						while(iter.hasNext())
						{
							Edge nextEdge = (Edge) iter.next();
							if(nextEdge.equals(currentEdge))
							{
								found = true;
								nextEdge.setOccurrence(nextEdge.getOccurrence()+1);
								break;
							}
						}
						if(!found)
						{
							edgeSet.add(currentEdge);
						}
					}
	
					currentEdge = new Edge();
					currentEdge.addPredicate(currPred);
					prevSubject = currSubject;
				}
			}
		}catch(SQLException sqle){
			throw new RdfStoreException("Operation:GeneratePredicateMappings Error: "
					+ sqle.getMessage(), sqle);
		}finally {
			DB2CloseObjects.close(rst, stmt);
		}
		
		if (currentEdge != null) {
			Iterator<Edge> iter = edgeSet.iterator();
			boolean found = false;
			while(iter.hasNext())
			{
				Edge nextEdge = (Edge) iter.next();
				if(nextEdge.equals(currentEdge))
				{
					found = true;
					nextEdge.setOccurrence(nextEdge.getOccurrence()+1);
					break;
				}
			}
			if(!found)
			{
				edgeSet.add(currentEdge);
			}
		}

		return edgeSet;
	}

	/**
	 * Write the supplied edgeset to the supplied file
	 * 
	 * @param file
	 * @param edgeSet
	 */

	private void writeEdgeSetToFile(File file, Set<Edge> edgeSet) {
		if (edgeSet != null) {
			BufferedWriter buf;
			try {
				buf = new BufferedWriter(new FileWriter(file));
				Iterator<Edge> edges = edgeSet.iterator();
				while (edges.hasNext()) {
					StringBuffer bf = new StringBuffer();
					Edge edge = (Edge) edges.next();
					bf.append(edge.getPredicateCount());
					bf.append(" ");
					bf.append(edge.getOccurrence());
					bf.append(" ");

					Set<String> predicates = edge.getPredicates();
					for (String pred : predicates) {
						bf.append("|");
						bf.append(pred);
					}

					buf.append(bf.toString());
					buf.newLine();
				}
				buf.close();

			} catch (IOException e) {
				throw new RdfStoreException("Operation:GeneratePredicateMappings Error: "
						+ e.getMessage(), e);
			}
		}
	}

	/**
	 * Create the query for fetching predicates
	 * 
	 * @param direct
	 * @return
	 */
	private String getQueryForPredicates(boolean direct) {

		StringBuffer query = new StringBuffer();
		int maxPreds;
		query.append(" WITH Q0 AS (SELECT ENTRY AS SUBJECT, LT.PROP AS PREDICATE FROM \n");
		if (direct) {
			query.append(store.getDirectPrimary());
			maxPreds = store.getDPrimarySize();
		} else {
			query.append(store.getReversePrimary());
			maxPreds = store.getRPrimarySize();
		}
		query.append(" AS T,");
		query.append("TABLE(VALUES ");
		int nrPreds = 0;
		for (; nrPreds < maxPreds; nrPreds++) {
			if (nrPreds > 0)
				query.append(",");
			query.append("(T.PROP");
			query.append(nrPreds);
			query.append(")");
		}
		query.append(") AS LT(PROP) ");
		query.append(" WHERE LT.PROP IS NOT NULL),");
		query.append(" Q1 AS ( SELECT COALESCE(LS.long_string, SUBJECT) AS SUBJECT, T.PREDICATE AS PREDICATE FROM ");
		query.append(store.getLongStrings());
		query.append(" AS LS RIGHT OUTER JOIN Q0 AS T ON LS.short_string =T.SUBJECT),");
		query.append(" Q2 AS ( SELECT T.SUBJECT AS SUBJECT, COALESCE(LS.long_string, T.PREDICATE) AS PREDICATE FROM ");
		query.append(store.getLongStrings());
		query.append(" AS LS RIGHT OUTER JOIN Q1 AS T ON LS.short_string = T.PREDICATE)");
		query.append(" SELECT SUBJECT,PREDICATE from Q2 ORDER BY SUBJECT");

		return query.toString();
	}

	

}
