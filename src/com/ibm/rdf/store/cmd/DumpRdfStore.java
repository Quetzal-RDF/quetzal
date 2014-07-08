package com.ibm.rdf.store.cmd;

import java.io.BufferedOutputStream;
import java.io.PrintStream;
import java.sql.Connection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openjena.riot.RiotWriter;

import com.hp.hpl.jena.query.Dataset;
import com.ibm.rdf.store.Context;
import com.ibm.rdf.store.Store;
import com.ibm.rdf.store.StoreManager;
import com.ibm.rdf.store.jena.RdfStoreException;
import com.ibm.rdf.store.jena.RdfStoreFactory;

public class DumpRdfStore extends AbstractRdfCommand {

	private static final Log log = LogFactory
				.getLog(DumpRdfStore.class);
	
	public static void main(String[] args) {
		
		AbstractRdfCommand cmd = new DumpRdfStore();
		cmd.runCmd(args,"dumprdfstore",null);
	}
	
	
	
	@Override
	public void doWork(Connection conn) {
		
		// create the store.
		try {
			
			Store store = StoreManager.connectStore(conn, params.get("-backend"), params.get("-schema"), storeName, Context.defaultContext);
			Dataset ds = RdfStoreFactory.connectDataset(store, conn, params.get("-backend"));
			
			PrintStream ps = new PrintStream(new BufferedOutputStream(System.out,1000000));
			RiotWriter.writeNQuads(ps, ds.asDatasetGraph());
			ps.close();
		}
		catch(RdfStoreException e) {
			log.error(e);
			System.out.println(e.getLocalizedMessage());
		}
		catch(Exception e) {
			log.error(e);
			System.out.println(e.getLocalizedMessage());
		}
	}
		

		
	
}
