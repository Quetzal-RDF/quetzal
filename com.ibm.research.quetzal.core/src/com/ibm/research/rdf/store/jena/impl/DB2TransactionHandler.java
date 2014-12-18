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
 package com.ibm.research.rdf.store.jena.impl;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.impl.SimpleTransactionHandler;
import com.ibm.research.rdf.store.jena.RdfStoreException;

public class DB2TransactionHandler extends SimpleTransactionHandler {

	private Connection conn;
	private static final Log log = LogFactory
			.getLog(DB2TransactionHandler.class);

	public DB2TransactionHandler(Graph graph) {
		super();
		conn = ((DB2Graph) graph).getConnection();
	}

	public void abort() {
		try {
			conn.rollback();
		} catch (SQLException e) {
			log.error("Cannot abort transaction", e);
			throw new RdfStoreException(e.getLocalizedMessage(), e);
		}
	}

	public void begin() {
		try {
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			log.error("Cannot begin transaction", e);
			throw new RdfStoreException(e.getLocalizedMessage(), e);
		}
	}

	public void commit() {
		try {
			conn.commit();
		} catch (SQLException e) {
			log.error("Cannot commit transaction", e);
			throw new RdfStoreException(e.getLocalizedMessage(), e);
		}
	}

	public boolean transactionsSupported() {
		return true;
	}
}
