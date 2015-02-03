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
 package com.ibm.rdf.store.sparql11;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import com.ibm.rdf.store.sparql11.TestRunner.DB2TestData;

public class DB2RDFQuery extends TestRunner<DB2TestData> {

	public DB2RDFQuery(DatabaseEngine<DB2TestData> engine, DB2TestData data) {
		super(data, engine, null);
	}

	public static void main(String[] args) {
		Options options = new Options();
		
		try {
			// create Options object
			options.addOption("jdbcurl", true, "jdbc url");
			options.addOption("schema", true, "schema name");
			options.addOption("kb", true, "knowledge base");
			options.addOption("username", true, "db user name");
			options.addOption("password", true, "db password");
			options.addOption("queryFile", true, "query file");
			options.addOption("defaultUnionGraph", false, "default Union Graph semantics");
			
			CommandLineParser parser = new GnuParser();
			CommandLine cmd = parser.parse( options, args);
			boolean defUnion = cmd.hasOption("defaultUnionGraph") ? Boolean.parseBoolean(cmd.getOptionValue("defaultUnionGraph")) : false;
			DB2TestData data = new DB2TestData(cmd.getOptionValue("jdbcurl"), 
					cmd.getOptionValue("kb"), 
					cmd.getOptionValue("username"), 
					cmd.getOptionValue("password"), 
					cmd.getOptionValue("schemaName"), defUnion);
			
			DB2RDFQuery q = new DB2RDFQuery(new DB2Engine(), data);
			q.executeQuery(cmd.getOptionValue("queryFile"));
		} catch (Exception e) {
			e.printStackTrace();
			HelpFormatter help = new HelpFormatter();
			help.printHelp( "DB2RDFQuery", options );
		}
	}
}
