package com.ibm.rdf.store.runtime.service.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface SingleRowResultSetProcessor<E> {

	E processRow( Connection conn, ResultSet rs) throws SQLException;
	
}

