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
 package com.ibm.research.rdf.store.runtime.service.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface SingleRowResultSetProcessor<E> {

	E processRow( Connection conn, ResultSet rs) throws SQLException;
	
}

