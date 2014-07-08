package com.ibm.rdf.store.cmd;

import java.io.BufferedOutputStream;
import java.io.PrintStream;
import java.sql.Connection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibm.rdf.store.Context;
import com.ibm.rdf.store.StoreManager;
import com.ibm.rdf.store.jena.RdfStoreException;

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
			
			StoreManager.generatePredicateMappings(conn, params.get("-backend"), params.get("-schema"),
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
