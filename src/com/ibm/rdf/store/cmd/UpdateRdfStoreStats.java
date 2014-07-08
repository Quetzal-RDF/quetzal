package com.ibm.rdf.store.cmd;

import java.sql.Connection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibm.rdf.store.Context;
import com.ibm.rdf.store.StoreManager;
import com.ibm.rdf.store.jena.RdfStoreException;

public class UpdateRdfStoreStats extends AbstractRdfCommand {

	private static final Log log = LogFactory
			.getLog(UpdateRdfStoreStats.class);
	
	public static void main(String[] args) {
		
		AbstractRdfCommand cmd = new UpdateRdfStoreStats();
		cmd.runCmd(args,"updaterdfstorestats",null);
	}
	
	@Override
	public void doWork(Connection conn) {
		try {
			StoreManager.runStats(conn, params.get("-backend"), params.get("-schema"), storeName, Context.defaultContext);
		}
		catch(RdfStoreException e) {
			log.error(e);
			e.printStackTrace();
			System.out.println(e.getLocalizedMessage());
		}
		catch(Exception e) {
			log.error(e);
			e.printStackTrace();
			System.out.println(e.getLocalizedMessage());
		}
	}
		

	
	

	
}
