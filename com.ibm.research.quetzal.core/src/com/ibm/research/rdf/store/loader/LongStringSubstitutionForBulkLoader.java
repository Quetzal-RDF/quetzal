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

import org.apache.jena.atlas.lib.Sink;
import org.apache.jena.riot.RiotReader;
import org.apache.jena.riot.out.SinkQuadOutput;
import org.apache.jena.riot.out.SinkTripleOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.core.Quad;
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
			RiotReader.parseQuads(fileName, new Sink<Quad>() {
				private final SinkQuadOutput qo = new SinkQuadOutput(new FileOutputStream(outFile), null, null);
				@Override
				public void send(Quad arg0) {
					try {
						Node newGraph = null;
						Node newSubj = null;
						Node newObject = null;
						Node newPredicate = null;
						
						if (arg0.getGraph().toString().length() > stringLength) {
							newGraph = Node.createURI(Constants.PREFIX_SHORT_STRING + HashingHelper.hashLongString(arg0.getGraph().toString()));
							stringFileWriter.write(newGraph + " " + arg0.getGraph().toString() + "\n");
						} else {
							newGraph = arg0.getGraph();
						}
						
						if (arg0.getSubject().toString().length() > stringLength) {
							newSubj = Node.createURI(Constants.PREFIX_SHORT_STRING + HashingHelper.hashLongString(arg0.getSubject().toString()));
							stringFileWriter.write(newSubj + " " + arg0.getSubject().toString() + "\n");
						} else {
							newSubj = arg0.getSubject();
						}
						
						if (arg0.getObject().toString().length() > stringLength) {
							newObject = Node.createURI(Constants.PREFIX_SHORT_STRING + HashingHelper.hashLongString(arg0.getObject().toString()));
							stringFileWriter.write(newObject + " " + arg0.getObject().toString() + "\n");
						} else {
							newObject = arg0.getObject();
						}
						
						if (arg0.getPredicate().toString().length() > stringLength) {
							newPredicate = Node.createURI(Constants.PREFIX_SHORT_STRING + HashingHelper.hashLongString(arg0.getPredicate().toString()));
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
				public void close() {
					qo.close();
				}
				@Override
				public void flush() {
					qo.flush();
				}
			});
		} else {
			RiotReader.parseTriples(fileName, new Sink<Triple>() {
				private final SinkTripleOutput to = new SinkTripleOutput(new FileOutputStream(outFile), null, null);
				@Override
				public void send(Triple arg0) {
					try {
						Node newSubj = null;
						Node newObject = null;
						Node newPredicate = null;
												
						if (arg0.getSubject().toString().length() > stringLength) {
							newSubj = Node.createURI(Constants.PREFIX_SHORT_STRING + HashingHelper.hashLongString(arg0.getSubject().toString()));
							stringFileWriter.write(newSubj + " " + arg0.getSubject().toString() + "\n");
						} else {
							newSubj = arg0.getSubject();
						}
						
						if (arg0.getObject().toString().length() > stringLength) {
							newObject = Node.createURI(Constants.PREFIX_SHORT_STRING + HashingHelper.hashLongString(arg0.getObject().toString()));
							stringFileWriter.write(newObject + " " + arg0.getObject().toString() + "\n");
						} else {
							newObject = arg0.getObject();
						}
						
						if (arg0.getPredicate().toString().length() > stringLength) {
							newPredicate = Node.createURI(Constants.PREFIX_SHORT_STRING + HashingHelper.hashLongString(arg0.getPredicate().toString()));
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
				public void close() {
					to.close();
				}

				@Override
				public void flush() {
					to.flush();
				}
			});
		}
		
		stringFileWriter.close();
	}

}
