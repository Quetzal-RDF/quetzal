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
 package com.ibm.research.rdf.store.cmd;

import java.io.BufferedOutputStream;
import java.io.PrintStream;
import java.sql.Connection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibm.research.rdf.store.Context;
import com.ibm.research.rdf.store.StoreManager;
import com.ibm.research.rdf.store.Store.Backend;
import com.ibm.research.rdf.store.jena.RdfStoreException;

/**
 * Command for Generating predicate mappings
 * 
 * @author prsahoo
 * 
 */
public class GeneratePredicateMappings extends AbstractRdfCommand {

	private static final Log log = LogFactory
			.getLog(GeneratePredicateMappings.class);
	
	@Override
	public void doWork(Connection conn) {
		// TODO Auto-generated method stub
		try {

			
			PrintStream ps = new PrintStream(new BufferedOutputStream(System.out,1000000));
			
			StoreManager.generatePredicateMappings(conn, Backend.valueOf(params.get("-backend")), params.get("-schema"),
					storeName, ps, Context.defaultContext);
			ps.close();
			
		} catch (RdfStoreException e) {
			log.error(e);
			System.out.println(e.getLocalizedMessage());
		} catch (Exception e) {
			log.error(e);
			System.out.println(e.getLocalizedMessage());
		}
	}

	
	public static void main(String[] args) {

		AbstractRdfCommand cmd = new GeneratePredicateMappings();
		cmd.runCmd(args,"genpredicatemapping",null);
	}

}
