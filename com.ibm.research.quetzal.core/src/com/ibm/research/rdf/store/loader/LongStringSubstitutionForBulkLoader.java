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
 package com.ibm.research.rdf.store.loader;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.out.SinkQuadOutput;
import org.apache.jena.riot.out.SinkTripleOutput;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.sparql.core.Quad;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.research.rdf.store.config.Constants;
import com.ibm.research.rdf.store.hashing.HashingHelper;


public class LongStringSubstitutionForBulkLoader {
	private final static Logger logger = LoggerFactory.getLogger(LongStringSubstitutionForBulkLoader.class);

	public static void main(String[] args) throws Exception {
		final int stringLength = Integer.parseInt(args[0]);
		String fileName = args[1];
		boolean quads = Boolean.parseBoolean(args[2]);
		final String outFile = args[3];
		
		final BufferedWriter stringFileWriter = new BufferedWriter(new OutputStreamWriter(System.out));
				
		if (quads) {
			RDFDataMgr.parse(new StreamRDF() {
				private final SinkQuadOutput qo = new SinkQuadOutput(new FileOutputStream(outFile), null, null);
				@Override
				public void quad(Quad arg0) {
					try {
						Node newGraph = null;
						Node newSubj = null;
						Node newObject = null;
						Node newPredicate = null;
						
						if (arg0.getGraph().toString().length() > stringLength) {
							newGraph = NodeFactory.createURI(Constants.PREFIX_SHORT_STRING + HashingHelper.hashLongString(arg0.getGraph().toString()));
							stringFileWriter.write(newGraph + " " + arg0.getGraph().toString() + "\n");
						} else {
							newGraph = arg0.getGraph();
						}
						
						if (arg0.getSubject().toString().length() > stringLength) {
							newSubj = NodeFactory.createURI(Constants.PREFIX_SHORT_STRING + HashingHelper.hashLongString(arg0.getSubject().toString()));
							stringFileWriter.write(newSubj + " " + arg0.getSubject().toString() + "\n");
						} else {
							newSubj = arg0.getSubject();
						}
						
						if (arg0.getObject().toString().length() > stringLength) {
							newObject = NodeFactory.createURI(Constants.PREFIX_SHORT_STRING + HashingHelper.hashLongString(arg0.getObject().toString()));
							stringFileWriter.write(newObject + " " + arg0.getObject().toString() + "\n");
						} else {
							newObject = arg0.getObject();
						}
						
						if (arg0.getPredicate().toString().length() > stringLength) {
							newPredicate = NodeFactory.createURI(Constants.PREFIX_SHORT_STRING + HashingHelper.hashLongString(arg0.getPredicate().toString()));
							stringFileWriter.write(newPredicate + " " + arg0.getPredicate().toString() + "\n");
						} else {
							newPredicate = arg0.getPredicate();
						}

						qo.send(new Quad(newGraph, newSubj, newPredicate, newObject));
					} catch (Exception e) {
						logger.error("Error subsituting long strings", e);
					}
				}
				@Override
				public void start() {
					// TODO Auto-generated method stub
					
				}
				@Override
				public void triple(Triple triple) {
					// TODO Auto-generated method stub
					
				}
				@Override
				public void base(String base) {
					// TODO Auto-generated method stub
					
				}
				@Override
				public void prefix(String prefix, String iri) {
					// TODO Auto-generated method stub
					
				}
				@Override
				public void finish() {
					// TODO Auto-generated method stub
					
				}
			}, fileName);
		} else {
			RDFDataMgr.parse(new StreamRDF() {
				private final SinkTripleOutput to = new SinkTripleOutput(new FileOutputStream(outFile), null, null);
				@Override
				public void triple(Triple arg0) {
					try {
						Node newSubj = null;
						Node newObject = null;
						Node newPredicate = null;
												
						if (arg0.getSubject().toString().length() > stringLength) {
							newSubj = NodeFactory.createURI(Constants.PREFIX_SHORT_STRING + HashingHelper.hashLongString(arg0.getSubject().toString()));
							stringFileWriter.write(newSubj + " " + arg0.getSubject().toString() + "\n");
						} else {
							newSubj = arg0.getSubject();
						}
						
						if (arg0.getObject().toString().length() > stringLength) {
							newObject = NodeFactory.createURI(Constants.PREFIX_SHORT_STRING + HashingHelper.hashLongString(arg0.getObject().toString()));
							stringFileWriter.write(newObject + " " + arg0.getObject().toString() + "\n");
						} else {
							newObject = arg0.getObject();
						}
						
						if (arg0.getPredicate().toString().length() > stringLength) {
							newPredicate = NodeFactory.createURI(Constants.PREFIX_SHORT_STRING + HashingHelper.hashLongString(arg0.getPredicate().toString()));
							stringFileWriter.write(newPredicate + " " + arg0.getPredicate().toString() + "\n");
						} else {
							newPredicate = arg0.getPredicate();
						}

						to.send(new Triple(newSubj, newPredicate, newObject));
						
					} catch (Exception e) {
						logger.error("Error subsituting long strings", e);
					}
				}

				@Override
				public void start() {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void quad(Quad quad) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void base(String base) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void prefix(String prefix, String iri) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void finish() {
					// TODO Auto-generated method stub
					
				}
			}, fileName);
		}
		
		stringFileWriter.close();
	}

}
