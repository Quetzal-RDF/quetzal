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

import java.sql.Connection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.query.Dataset;
import com.ibm.research.rdf.store.Context;
import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.Store.Backend;
import com.ibm.research.rdf.store.StoreManager;
import com.ibm.research.rdf.store.jena.RdfStoreFactory;

public class LoadRdfStore  extends ShowPredicateColumns {
	
	private static final Log log = LogFactory
						.getLog(LoadRdfStore.class);
	
	public static void main(String[] args) {
		
		AbstractRdfCommand cmd = new LoadRdfStore();
		cmd.runCmd(args,"loadrdfstore",null);
	}
	
	@Override
	public void doWork(Connection conn)  {
		
		if ( finalArg == null ) {
			System.out.println("Load file not specified");
			printUsage();
			return;
		}
		
		try {
		
			Store store = StoreManager.connectStore(conn, Backend.valueOf(params.get("-backend")), params.get("-schema"), storeName, Context.defaultContext);
			Dataset db2DS = RdfStoreFactory.connectDataset(store, conn, Backend.valueOf(params.get("-backend")));
		
//			Lang lang = Lang. Lang.guess(finalArg);
//			long timeTaken;
//			long numTriples = 0;
//			if ( RDFLanguages.isQuads(lang) ) {
//				timeTaken = loadQuads(conn,db2DS);
//				numTriples =  StoreManager.getStoreSize(conn, params.get("-backend"), 
//						params.get("-schema"), storeName, Context.defaultContext);
//			}
//			else if ( RDFLanguages.isTriples(lang) ) {
//				timeTaken = loadToDefault(conn,db2DS,finalArg,lang);
//				numTriples = db2DS.getDefaultModel().size();
//			}
//			else {
//				System.out.println("Unknown RDF type : " + finalArg);
//				return;
//			}
//			
//	
//			
//			System.out.println("Loaded "  + numTriples +
//						" triples in " + (timeTaken/ 1000 ) + " secs");
         }
      catch (RuntimeException e)
         {
         log.error(e);
         System.out.println(e.getLocalizedMessage());
         }
      catch (Exception e)
         {
         log.error(e);
         System.out.println(e.getLocalizedMessage());
		}
		
	}

	@Override
	public void printUsage() {
	}

}
