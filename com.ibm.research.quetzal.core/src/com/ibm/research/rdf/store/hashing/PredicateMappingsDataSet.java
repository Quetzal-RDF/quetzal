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

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.openjena.riot.RiotWriter;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.graph.GraphFactory;
import com.ibm.research.rdf.store.config.Constants;

public class PredicateMappingsDataSet {

	private int numberOfHashFunctions;
	private int numberOfColorFunctions;
	private Dataset dataset;

	public PredicateMappingsDataSet(int numberOfHashFunctions,
			int numberOfColorFunctions) {
		this.numberOfColorFunctions = numberOfColorFunctions;
		this.numberOfHashFunctions = numberOfHashFunctions;

		Model defModel = ModelFactory.createDefaultModel();
		dataset = DatasetFactory.create(defModel);
		dataset.getDefaultModel()
				.getGraph()
				.add(new Triple(NodeFactory.createURI(Constants.ibmns
						+ Constants.NUM_COL_FUNCTION),
						NodeFactory.createURI(Constants.ibmns
								+ Constants.VALUE_PREDICATE), intToNode(numberOfColorFunctions)));

		dataset.getDefaultModel()
				.getGraph()
				.add(new Triple(NodeFactory.createURI(Constants.ibmns
						+ Constants.NUM_HASH_FUNCTION),
						NodeFactory.createURI(Constants.ibmns
								+ Constants.VALUE_PREDICATE), intToNode(numberOfHashFunctions)));
	}

	protected Node intToNode(int val) {
		return NodeFactory.createLiteral(Long.toString(val), "", XSDDatatype.XSDinteger) ;
	}
	/**
	 * Write the supplied colors to the dataset
	 * 
	 * @param numColoringFunctions
	 * @param type
	 * @param colorsPair
	 * @param dataset
	 */
	public void writeColoringFunctionToDataset(
			boolean isDirect,
			com.ibm.wala.util.collections.Pair<Map<String, Integer>, Map<String, Integer>> colorsPair) {

		String type = isDirect ? Constants.DIRECT_TYPE : Constants.REVERSE_TYPE;

		Node predicateNode = NodeFactory.createURI(Constants.ibmns
				+ Constants.COLUMN_PREDICATE);

		if (numberOfColorFunctions > 0) {
			// First coloring function
			Map<String, Integer> colors = colorsPair.fst;
			Graph graph = GraphFactory.createPlainGraph();
			Iterator iterator = colors.entrySet().iterator();

			while (iterator.hasNext()) {
				Entry<String, Integer> entry = (Entry<String, Integer>) iterator
						.next();
				Triple triple = new Triple(NodeFactory.createURI(entry.getKey()),
						predicateNode, intToNode(entry.getValue()));
				graph.add(triple);
			}

			Node colorFunction = NodeFactory.createURI(Constants.ibmns
					+ Constants.COLORING_FUNCTION + type + 1);

			dataset.addNamedModel(Constants.ibmns + Constants.COLORING_FUNCTION
					+ type + 1, ModelFactory.createModelForGraph(graph));

			Triple deftriple = new Triple(colorFunction,
					NodeFactory.createURI(Constants.ibmns
							+ Constants.FUNCTION_TYPE_PREDICATE),
					NodeFactory.createLiteral("color"));//, "", ""));
			dataset.getDefaultModel().getGraph().add(deftriple);

			dataset.getDefaultModel()
					.getGraph()
					.add(new Triple(
							colorFunction,
							NodeFactory.createURI(Constants.ibmns
									+ Constants.COLORING_FUNCTION_TUPE_PREDICATE),
							NodeFactory.createLiteral(type)));//, "", "")));

			dataset.getDefaultModel()
					.getGraph()
					.add(new Triple(colorFunction, NodeFactory.createURI(Constants.ibmns
									+ Constants.FUNCTION_PRIORITY), intToNode(1)));

		}

		// Second coloring function
		if (numberOfColorFunctions > 1) {
			Map<String, Integer> colors2 = colorsPair.snd;

			Graph graph2 = GraphFactory.createPlainGraph();
			Iterator iterator2 = colors2.entrySet().iterator();
			while (iterator2.hasNext()) {
				Entry<String, Integer> entry = (Entry<String, Integer>) iterator2
						.next();
				Triple triple = new Triple(NodeFactory.createURI(entry.getKey()),
						predicateNode, intToNode(entry.getValue()));
				graph2.add(triple);
			}

			dataset.addNamedModel(Constants.ibmns + Constants.COLORING_FUNCTION
					+ type + 2, ModelFactory.createModelForGraph(graph2));

			Node colorFunction = NodeFactory.createURI(Constants.ibmns
					+ Constants.COLORING_FUNCTION + type + 2);

			dataset.getDefaultModel()
					.getGraph()
					.add(new Triple(colorFunction, NodeFactory.createURI(Constants.ibmns
									+ Constants.FUNCTION_TYPE_PREDICATE),
							NodeFactory.createLiteral("color")));//, "", "")));

			dataset.getDefaultModel()
					.getGraph()
					.add(new Triple(
							colorFunction,
							NodeFactory.createURI(Constants.ibmns
									+ Constants.COLORING_FUNCTION_TUPE_PREDICATE),
							NodeFactory.createLiteral(type)));//, "", "")));

			dataset.getDefaultModel()
					.getGraph()
					.add(new Triple(
							colorFunction,
							NodeFactory.createURI(Constants.ibmns
									+ Constants.COLORING_FUNCTION_TUPE_PREDICATE),
							NodeFactory.createLiteral(type)));//, "", "")));

			dataset.getDefaultModel()
					.getGraph()
					.add(new Triple(colorFunction, NodeFactory.createURI(Constants.ibmns
									+ Constants.FUNCTION_PRIORITY), intToNode(2)));

		}
	}

	/**
	 * Write hashing functions to the dataset
	 * 
	 * @param predicates
	 * @param dataset
	 * @param type
	 */
	public void writeHashingFunctionToDataset(Set<String> predicates,
			boolean isDirect, int bucketSize) {
		if (numberOfHashFunctions != 0) {
			writeHashingFunctionToDataset(predicates, isDirect, new RabinHashFamily(bucketSize, numberOfHashFunctions));
		}
	}
	
	public void writeHashingFunctionToDataset(Set<String> predicates,
			boolean isDirect, IHashingFamily family) {

		String type = isDirect ? "direct" : "reverse";
		Node predicateNode = NodeFactory.createURI(Constants.ibmns
				+ Constants.COLUMN_PREDICATE);

			Graph[] hashingGraphs = new Graph[numberOfHashFunctions];
			for (int i = 0; i < numberOfHashFunctions; i++) {
				hashingGraphs[i] = GraphFactory.createPlainGraph();
			}

			Iterator<String> predicateIterator = predicates.iterator();
			while (predicateIterator.hasNext()) {
				String entry = predicateIterator.next();
				family.computeHash(entry);
				int[] hashes = family.getHash(entry);
				for (int i = 0; i < numberOfHashFunctions; i++) {
					Triple triple = new Triple(NodeFactory.createURI(entry),
							predicateNode, intToNode(hashes[i]));
					hashingGraphs[i].add(triple);
				}
			}

			for (int i = 0; i < numberOfHashFunctions; i++) {
				dataset.addNamedModel(Constants.ibmns
						+ Constants.HASHING_FUNCTION + type + (i + 1),
						ModelFactory.createModelForGraph(hashingGraphs[i]));

				Node hashFunction = NodeFactory.createURI(Constants.ibmns
						+ Constants.HASHING_FUNCTION + type + (i + 1));

				Triple deftriple3 = new Triple(hashFunction,
						NodeFactory.createURI(Constants.ibmns
								+ Constants.FUNCTION_TYPE_PREDICATE),
						NodeFactory.createLiteral("hashing"));//, "", ""));
				dataset.getDefaultModel().getGraph().add(deftriple3);

				dataset.getDefaultModel()
						.getGraph()
						.add(new Triple(hashFunction, NodeFactory.createURI(Constants.ibmns
										+ Constants.FUNCTION_PRIORITY),
										intToNode(numberOfColorFunctions
										+ i + 1)));

				dataset.getDefaultModel()
						.getGraph()
						.add(new Triple(
								hashFunction,
								NodeFactory.createURI(Constants.ibmns
										+ Constants.COLORING_FUNCTION_TUPE_PREDICATE),
								NodeFactory.createLiteral(type)));//, "", "")));

			}

	
	}
	

	/**
	 * Export the dataset in N-Quad format
	 * 
	 * @param outputFile
	 */
	public void exportToFile(OutputStream outputStream) {
				
		// Dump the dataset to a file in n-quad format
		PrintStream ps = new PrintStream(outputStream); 
		RiotWriter.writeNQuads(ps, dataset.asDatasetGraph());
		ps.close();
	}

}
