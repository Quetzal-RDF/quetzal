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
 package com.ibm.research.rdf.store.jena.impl.update;

import java.util.Map;

import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.config.Constants;
import com.ibm.research.rdf.store.runtime.service.types.TypeMap;
import com.ibm.research.rdf.store.sparql11.QueryInfo;
import com.ibm.research.rdf.store.sparql11.model.OrderCondition;
import com.ibm.research.rdf.store.sparql11.model.OrderCondition.EOrderType;
import com.ibm.research.rdf.store.sparql11.model.Variable;

public class InsertAndUpdateStatements {

	private static final String NEW_LINE = "\n";

	private static final String NEW_LINE_AND_TAB = "\n\t";

	private InsertAndUpdateStatements() {

	}

	public static String getGraphListStatement(String namePrimaryTable,
			String nameLongStringTable) {
		StringBuffer sql = new StringBuffer();

		sql.append("WITH Q0 AS ( SELECT DISTINCT T.");
		sql.append(Constants.NAME_COLUMN_GRAPH_ID);
		sql.append(" AS GRAPH FROM ");
		sql.append(namePrimaryTable);
		sql.append(" AS T WHERE T.GID != '").append(
				Constants.DEFAULT_GRAPH_MONIKER).append(
				"' ), Q1 AS ( SELECT COALESCE(LS.");
		sql.append(Constants.NAME_COLUMN_LONG_STRING);
		sql.append(", T.GRAPH) AS GRAPH FROM ");
		sql.append(nameLongStringTable);
		sql.append(" AS LS RIGHT OUTER JOIN Q0 AS T ON LS.");
		sql.append(Constants.NAME_COLUMN_SHORT_STRING);
		sql.append(" = T.GRAPH) SELECT GRAPH FROM Q1");

		return sql.toString();
	}

	public static String getGidByGid(String nameTable) {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append(Constants.NAME_COLUMN_GRAPH_ID);
		sql.append(" FROM ");
		sql.append(nameTable);
		sql.append(" WHERE ");
		sql.append(Constants.NAME_COLUMN_GRAPH_ID);
		sql.append(" = ? FETCH FIRST 1 ROWS ONLY");

		return sql.toString();
	}

	private static String getTripleDPH(String namePrimaryTable,
			String nameSecondaryTable, String nameLongStringTable, int size,
			boolean isGraph, boolean isLong, String whereClause) {
		StringBuffer sql = new StringBuffer();

		sql.append("WITH Q0 AS (");
		sql.append(getQuadTableSelectStatementDPH(namePrimaryTable, size,
				isLong));
		if (isGraph) {
			sql.append(" AND T.");
			sql.append(Constants.NAME_COLUMN_GRAPH_ID);
			sql.append(" = ?");
		}
		if (whereClause != null) {
			sql.append(whereClause);
		}
		sql.append("), ");
		sql.append(NEW_LINE);
		sql.append("Q1 AS (");
		sql.append("SELECT T.");
		sql.append("ENTRY AS ENTRY, T.");
		sql.append("GRAPH AS GRAPH, T.");
		sql.append("PREDICATE AS PREDICATE,");
		sql.append(" COALESCE(S.");
		sql.append(Constants.NAME_COLUMN_PREFIX_LIST_ELEMENT);
		sql.append(" ,T.VALUE) AS VALUE, ");
		sql.append(" COALESCE(S.");
		sql.append(Constants.NAME_COLUMN_PREFIX_LIST_TYPE);
		sql.append(" ,T.TYP) AS TYPE FROM ");
		sql.append(nameSecondaryTable);
		sql.append(" AS S RIGHT OUTER JOIN Q0 AS T ON S.");
		sql.append(Constants.NAME_COLUMN_LIST_ID);
		sql.append(" = T.VALUE AND S.ELEM IS NOT NULL)");

		if (isLong) {
			String[] type = new String[5];
			type[0] = "GRAPH";
			type[1] = "ENTRY";
			type[2] = "PREDICATE";
			type[3] = "VALUE";
			type[4] = "TYPE";
			for (int i = 0; i < (type.length - 1); i++) { // all except type
				sql.append(getNestedSelectOnType(nameLongStringTable, type,
						type[i], i + 2));
			}
		}

		sql.append(NEW_LINE);
		return sql.toString();
	}

	private static String getNestedSelectOnType(String nameLongStringTable,
			String[] types, String type, int index) {
		StringBuffer sql = new StringBuffer();
		sql.append(", ");
		sql.append(NEW_LINE);
		sql.append("Q");
		sql.append(index);
		sql.append(" AS ( SELECT ");
		for (int i = 0; i < types.length; i++) {
			if (types[i].equals(type)) {
				sql.append("COALESCE(LS.");
				sql.append(Constants.NAME_COLUMN_LONG_STRING);
				sql.append(" CONCAT ");
				sql.append("LS.");
				sql.append(Constants.NAME_COLUMN_LONG_STRING_OVERFLOW);
				sql.append(", T.");
				sql.append(types[i]);
				sql.append(") AS ");
				sql.append(types[i]);
			} else {
				sql.append("T.");
				sql.append(types[i]);
				sql.append(" AS ");
				sql.append(types[i]);
			}
			if (i < types.length - 1) {
				sql.append(", ");
			}
		}
		sql.append(" FROM ");
		sql.append(nameLongStringTable);
		sql.append(" AS LS RIGHT OUTER JOIN Q");
		sql.append(index - 1);
		sql.append(" AS T ON LS.");
		sql.append(Constants.NAME_COLUMN_SHORT_STRING);
		sql.append(" = T.");
		sql.append(type);
		sql.append(")");
		return sql.toString();
	}

	private static String getQuadTableSelectStatementDPH(
			String namePrimaryTable, int size, boolean isLong) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT T.");
		sql.append(Constants.NAME_COLUMN_ENTRY);
		sql.append(" AS ENTRY, T.");
		sql.append(Constants.NAME_COLUMN_GRAPH_ID);
		sql.append(" AS GRAPH, LT.");

		sql.append(Constants.NAME_COLUMN_PREFIX_PREDICATE);
		sql.append(" AS PREDICATE, LT.");
		sql.append(Constants.NAME_COLUMN_PREFIX_VALUE);
		sql.append(" AS VALUE, LT.");
		sql.append(Constants.NAME_COLUMN_PREFIX_TYPE);
		sql.append(" AS TYP FROM ");
		sql.append(namePrimaryTable);
		sql.append(" AS T, TABLE(VALUES");
		for (int i = 0; i < size; i++) {
			sql.append("(T.");
			sql.append(Constants.NAME_COLUMN_PREFIX_PREDICATE);
			sql.append(i);
			sql.append(", T.");
			sql.append(Constants.NAME_COLUMN_PREFIX_VALUE);
			sql.append(i);
			sql.append(", T.");
			sql.append(Constants.NAME_COLUMN_PREFIX_TYPE);
			sql.append(i);
			sql.append(")");
			if (i != size - 1) {
				sql.append(", ");
			}
		}
		sql.append(") AS LT(");
		sql.append(Constants.NAME_COLUMN_PREFIX_PREDICATE);
		sql.append(", ");
		sql.append(Constants.NAME_COLUMN_PREFIX_VALUE);
		sql.append(", ");
		sql.append(Constants.NAME_COLUMN_PREFIX_TYPE);
		sql.append(") WHERE LT.");
		sql.append(Constants.NAME_COLUMN_PREFIX_PREDICATE);
		sql.append(" IS NOT NULL");

		return sql.toString();
	}

	public static String getTripleInGraphDPH(String namePTable,
			String nameSTable, String nameLSTable, int size, boolean isLong,
			/* mdb to support Dataset.find(Quad q) where q.graph=ANY */
			boolean isGraph, String whereClause) {
		StringBuffer sql = new StringBuffer();
		// sql.append(getTriple(namePTable, nameSTable, nameLSTable, size, true,
		// isLong));
		sql.append(getTripleDPH(namePTable, nameSTable, nameLSTable, size,
				isGraph, isLong, whereClause));
		if (isLong) {
			sql.append("SELECT * FROM Q5");
		} else {
			sql.append("SELECT * FROM Q1");
		}

		return sql.toString();
	}
	
	private static String getQuadTableSelectStatementRPH(
			String namePrimaryTable, int size, boolean isLong) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT T.");
		sql.append(Constants.NAME_COLUMN_ENTRY);
		sql.append(" AS ENTRY, T.");
		sql.append(Constants.NAME_COLUMN_GRAPH_ID);
		sql.append(" AS GRAPH, T.");
		sql.append(Constants.NAME_COLUMN_PREFIX_TYPE);
		sql.append(" AS TYPE, LT.");

		sql.append(Constants.NAME_COLUMN_PREFIX_PREDICATE);
		sql.append(" AS PREDICATE, LT.");
		sql.append(Constants.NAME_COLUMN_PREFIX_VALUE);
		sql.append(" AS VALUE FROM ");
		sql.append(namePrimaryTable);
		sql.append(" AS T, TABLE(VALUES");
		for (int i = 0; i < size; i++) {
			sql.append("(T.");
			sql.append(Constants.NAME_COLUMN_PREFIX_PREDICATE);
			sql.append(i);
			sql.append(", T.");
			sql.append(Constants.NAME_COLUMN_PREFIX_VALUE);
			sql.append(i);
			sql.append(")");
			if (i != size - 1) {
				sql.append(", ");
			}
		}
		sql.append(") AS LT(");
		sql.append(Constants.NAME_COLUMN_PREFIX_PREDICATE);
		sql.append(", ");
		sql.append(Constants.NAME_COLUMN_PREFIX_VALUE);
		sql.append(") WHERE LT.");
		sql.append(Constants.NAME_COLUMN_PREFIX_PREDICATE);
		sql.append(" IS NOT NULL ");

		return sql.toString();
	}
	
	private static String getTripleRPH(String namePrimaryTable,
			String nameSecondaryTable, String nameLongStringTable, int size,
			boolean isGraph, boolean isLong, String whereClause) {
		StringBuffer sql = new StringBuffer();

		sql.append("WITH Q0 AS (");
		sql.append(getQuadTableSelectStatementRPH(namePrimaryTable, size,
				isLong));
		if (isGraph) {
			sql.append(" AND T.");
			sql.append(Constants.NAME_COLUMN_GRAPH_ID);
			sql.append(" = ?");
		}
		if (whereClause != null) {
			sql.append(whereClause);
		}
		sql.append("), ");
		sql.append(NEW_LINE);
		sql.append("Q1 AS (");
		sql.append("SELECT T.");
		sql.append("ENTRY AS VALUE, T.");
		sql.append("GRAPH AS GRAPH, T.");
		sql.append("TYPE AS TYPE, T.");
		sql.append("PREDICATE AS PREDICATE,");
		sql.append(" COALESCE(S.");
		sql.append(Constants.NAME_COLUMN_PREFIX_LIST_ELEMENT);
		sql.append(" ,T.VALUE) AS ENTRY FROM ");
		sql.append(nameSecondaryTable);
		sql.append(" AS S RIGHT OUTER JOIN Q0 AS T ON S.");
		sql.append(Constants.NAME_COLUMN_LIST_ID);
		sql.append(" = T.VALUE AND S.ELEM IS NOT NULL)");

		if (isLong) {
			String[] type = new String[5];
			type[0] = "GRAPH";
			type[1] = "ENTRY";
			type[2] = "PREDICATE";
			type[3] = "VALUE";
			type[4] = "TYPE";
			for (int i = 0; i < (type.length - 1); i++) { // all except type
				sql.append(getNestedSelectOnType(nameLongStringTable, type,
						type[i], i + 2));
			}
		}

		sql.append(NEW_LINE);
		return sql.toString();
	}
	
	public static String getTripleInGraphRPH(String namePTable,
			String nameSTable, String nameLSTable, int size, boolean isLong,
			boolean isGraph, String whereClause) {
		StringBuffer sql = new StringBuffer();
		// sql.append(getTriple(namePTable, nameSTable, nameLSTable, size, true,
		// isLong));
		sql.append(getTripleRPH(namePTable, nameSTable, nameLSTable, size,
				isGraph, isLong, whereClause));
		if (isLong) {
			sql.append("SELECT * FROM Q5");
		} else {
			sql.append("SELECT * FROM Q1");
		}

		return sql.toString();
	}

	public static String getLongStringSelect(String nameTable, int size) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * FROM ");
		sql.append(nameTable);
		sql.append(" WHERE (");
		sql.append(Constants.NAME_COLUMN_SHORT_STRING);
		sql.append(" = ? AND ");
		sql.append(Constants.NAME_COLUMN_PREFIX_TYPE);
		sql.append(" = ?)");
		for (int i = 1;i < size;i++) {
			sql.append(" OR (");
			sql.append(Constants.NAME_COLUMN_SHORT_STRING);
			sql.append(" = ? AND ");
			sql.append(Constants.NAME_COLUMN_PREFIX_TYPE);
			sql.append(" = ?)");
		}

		return sql.toString();
	}

	public static String getOrderByClause(String variable,
			EOrderType eOrderType, QueryInfo qInfo, boolean isModified,
			Map<String, Store.Db2Type> objectVarType) {
		boolean isBound = false;
		StringBuffer sql = new StringBuffer();
		Object[] variables = (Object[]) qInfo.getLiteralBoundVariables()
				.toArray();
		for (int i = 0; i < variables.length; i++) {
			if (((Variable) variables[i]).getName().equals(variable)) {
				isBound = true;
				break;
			}
		}

		if (isModified && isBound) {
			
			if(objectVarType.containsKey(variable)){
				Store.Db2Type tp=objectVarType.get(variable);
				if(tp!=null){
					if(tp==Store.Db2Type.DECFLOAT){
						sql.append(" CAST ("+variable + " AS DECFLOAT(34))");						
					}
					if(tp==Store.Db2Type.TIMESTAMP){
						sql.append(" XMLCAST ( XMLTEXT (" + variable+") AS TIMESTAMP)");
					}
					if(tp==Store.Db2Type.VARCHAR){
						sql.append(variable);
					}
					if (eOrderType == OrderCondition.EOrderType.ASC) {
						sql.append(" ASC ");
					} else if (eOrderType == OrderCondition.EOrderType.DESC) {
						sql.append(" DESC ");
					}
					return sql.toString();
				}
			}
			
			sql.append("CASE");
			sql.append(NEW_LINE_AND_TAB);
			sql.append("WHEN " + variable + "_typ IS NULL THEN '0'");
			sql.append(NEW_LINE_AND_TAB);
			sql.append("WHEN " + variable + "_typ BETWEEN ");
			sql.append(TypeMap.DATATYPE_NUMERICS_IDS_START + " AND ");
			sql.append(TypeMap.DATATYPE_NUMERICS_IDS_END + " THEN '");
			sql.append(TypeMap.DATATYPE_NUMERICS_IDS_START + "'");
			sql.append(NEW_LINE_AND_TAB);

			sql.append("WHEN " + variable + "_typ > ");
			sql.append(TypeMap.DATATYPE_NUMERICS_IDS_END + " THEN '");
			sql.append((TypeMap.DATATYPE_NUMERICS_IDS_END + 1) + "'");
			sql.append(NEW_LINE_AND_TAB);
			sql.append("ELSE " + variable + "_typ ");
			sql.append(NEW_LINE);
			sql.append("END");
			if (eOrderType == OrderCondition.EOrderType.ASC) {
				sql.append(" ASC ");
			} else if (eOrderType == OrderCondition.EOrderType.DESC) {
				sql.append(" DESC ");
			}
			sql.append(",");
			sql.append(NEW_LINE);
			sql.append("CASE");
			sql.append(NEW_LINE_AND_TAB);
			sql.append("WHEN " + variable + "_typ = ");
			sql.append(TypeMap.getDatatypeType(TypeMap.DATETIME_IRI));
			sql.append(" THEN XMLCAST ( XMLTEXT (" + variable);
			sql.append(") AS TIMESTAMP)");
			sql.append(NEW_LINE);
			sql.append("END");
			if (eOrderType == OrderCondition.EOrderType.ASC) {
				sql.append(" ASC ");
			} else if (eOrderType == OrderCondition.EOrderType.DESC) {
				sql.append(" DESC ");
			}
			sql.append(",");
			sql.append(NEW_LINE);
			sql.append("CASE");
			sql.append(NEW_LINE_AND_TAB);
			sql.append("WHEN " + variable + "_typ BETWEEN ");
			sql.append(TypeMap.DATATYPE_NUMERICS_IDS_START + " AND ");
			sql.append(TypeMap.DATATYPE_NUMERICS_IDS_END + " THEN CAST (");
			sql.append(variable + " AS DECFLOAT(34))");

			sql.append(NEW_LINE);
			sql.append("END");
			if (eOrderType == OrderCondition.EOrderType.ASC) {
				sql.append(" ASC ");
			} else if (eOrderType == OrderCondition.EOrderType.DESC) {
				sql.append(" DESC ");
			}
			sql.append(",");
			sql.append(NEW_LINE);
			sql.append(variable + " ");
			if (eOrderType == OrderCondition.EOrderType.ASC) {
				sql.append(" ASC ");
			} else if (eOrderType == OrderCondition.EOrderType.DESC) {
				sql.append(" DESC ");
			}
			sql.append(",");
			sql.append(NEW_LINE);
			sql.append("CASE");
			sql.append(NEW_LINE_AND_TAB);
			sql.append("WHEN " + variable + "_typ BETWEEN ");
			sql.append(TypeMap.USER_ID_START + " AND ");
			sql.append(TypeMap.DATATYPE_IDS_END + " THEN '5' CONCAT ");
			sql.append(variable + "_typ ");
			sql.append(NEW_LINE_AND_TAB);
			sql.append("WHEN " + variable + "_typ = ");
			sql.append(TypeMap.getDatatypeType(TypeMap.STRING_IRI));
			sql.append(" THEN '51499'");
			sql.append(NEW_LINE_AND_TAB);
			sql.append("ELSE " + variable + "_typ ");
			sql.append(NEW_LINE);
			sql.append("END");
			if (eOrderType == OrderCondition.EOrderType.ASC) {
				sql.append(" ASC ");
			} else if (eOrderType == OrderCondition.EOrderType.DESC) {
				sql.append(" DESC ");
			}
		} else {
			sql.append(" " + variable + " ");
			if (eOrderType == OrderCondition.EOrderType.ASC) {
				sql.append(" ASC ");
			} else if (eOrderType == OrderCondition.EOrderType.DESC) {
				sql.append(" DESC ");
			}
		}

		return sql.toString();
	}


}