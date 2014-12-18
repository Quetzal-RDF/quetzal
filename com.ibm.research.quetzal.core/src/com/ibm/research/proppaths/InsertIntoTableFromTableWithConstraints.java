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
 package com.ibm.research.proppaths;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.Store.Db2Type;
import com.ibm.research.rdf.store.config.Constants;
import com.ibm.research.rdf.store.runtime.service.types.TypeMap;
import com.ibm.research.rdf.store.sparql11.model.Constant;
import com.ibm.research.rdf.store.sparql11.model.Expression;
import com.ibm.research.rdf.store.sparql11.model.Variable;
import com.ibm.research.rdf.store.sparql11.sqlwriter.FilterContext;
import com.ibm.research.rdf.store.sparql11.sqlwriter.SPARQLToSQLExpression;
import com.ibm.research.rdf.store.sparql11.sqlwriter.SQLWriterException;
import com.ibm.wala.util.collections.Pair;

public class InsertIntoTableFromTableWithConstraints extends InsertIntoTable
		implements SQLCommand {
	// planNode.get{rpcessedFilters(plan)
	private static String toSQL( String sourceTable,
			List<Pair<Variable, Set<Constant>>> variableConstantConstraints,
			List<Pair<Variable, Variable>> variableEqualityConstraints, 
			Set<Variable> explicitIRIBoundVariables, List<Expression> exps,
			Map<String, Pair<String, String>> varMap, Store store) {
		StringBuffer sql = new StringBuffer();
		boolean isPostGres = CodeGenerator.isPostGresBackend(store);
		sql.append("SELECT * ").append(" FROM ").append(sourceTable);
		boolean first = true;
		if ( (variableConstantConstraints!=null && variableConstantConstraints.size()>0)
		|| (variableEqualityConstraints!=null && variableEqualityConstraints.size()>0)
		|| (exps!=null && exps.size()>0 )) {
			sql.append(" WHERE ");
		}
		if (variableConstantConstraints!=null && variableConstantConstraints.size()>0) {
			for (Pair<Variable, Set<Constant>> p: variableConstantConstraints) {
				Set<Constant> cs = p.snd;
				if (explicitIRIBoundVariables.contains(p.fst)) {
					// use IN operator 
					if (!cs.isEmpty()) {
						
						if (first) {
							first = false;
						} else {
							sql.append("\n AND ");
						}
						sql.append(p.fst.getName()).append(" IN (");
						boolean firstConst = true;
						for (Constant c: cs) {
							if (firstConst) {
								firstConst = false;
							} else {
								sql.append(", ");
							}
							sql.append("'"+c.toSqlDataString()+"'").append((isPostGres?"::text":""));
						}
						sql.append(")");						
					}
				} else {
					// use OR operator
					if (!cs.isEmpty()) {
						if (first) {
							first = false;
						} else {
							sql.append("\n AND ");
						}
						sql.append("(");
						boolean firstConst = true;
						for (Constant c: cs) {
							if (firstConst) {
								firstConst = false;
							} else {
								sql.append(" OR ");
							}
							sql.append("(");
							sql.append(p.fst.getName()).append("='").append(c.toSqlDataString()).append("'").append((isPostGres?"::text":""))
							   .append(" AND ")
							   .append(p.fst.getName()).append(Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS)
							   .append("=").append(c.toDataType());
							sql.append(")");
						}
						sql.append(")");
					}
				}
				
			}
		}
		if (variableEqualityConstraints!=null) {
			for (Pair<Variable, Variable> p : variableEqualityConstraints) {
				if (first) {
					first = false;
				} else {
					sql.append("\n AND ");
				}
				
				if (explicitIRIBoundVariables.contains(p.fst) && explicitIRIBoundVariables.contains(p.snd)) {
					sql.append(p.fst.getName()).append("=").append(p.snd.getName());
				} else {
					sql.append("(");
					sql.append(p.fst.getName()).append("=").append(p.snd.getName());
					sql.append(" AND ");
					if (explicitIRIBoundVariables.contains(p.fst)) {
						sql.append(p.snd.getName()).append(Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS)
						   .append("=")
						   .append(TypeMap.IRI_ID);
					} else if (explicitIRIBoundVariables.contains(p.snd)) {
						sql.append(p.fst.getName()).append(Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS)
						   .append("=")
						   .append(TypeMap.IRI_ID);
					} else {
						sql.append(p.fst.getName()).append(Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS)
						   .append("=")
						   .append(p.snd.getName()).append(Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);
					}
					sql.append(")");
				}
			}
		}
		if (exps!=null) {
			try {
				SPARQLToSQLExpression gen = new SPARQLToSQLExpression();
				for (Expression exp: exps) {
					if (first) {
						first = false;
					} else {
						sql.append("\n AND ");
					}
					String sqlExp =  gen.getSQLForExpression(exp, new FilterContext(varMap, new HashMap<String, Db2Type>(), null), store);
					sql.append("(").append(sqlExp).append(")");
				}
			} catch (SQLWriterException e) {
				throw new RuntimeException(e);
			}
		}
		
		return sql.toString();
	}
	protected List<Pair<Variable, Set<Constant>>> variableConstantConstraints;
	protected List<Pair<Variable, Variable>> variableEqualityConstraints;
	protected String targetTable;
	protected String sourceTable;
	public InsertIntoTableFromTableWithConstraints(String targetTable, String sourceTable,
			List<Pair<Variable, Set<Constant>>> variableConstantConstraints,
			List<Pair<Variable, Variable>> variableEqualityConstraints,Set<Variable> explicitIRIBoundVariables,
			List<Expression> exps, Map<String, Pair<String, String>> varMap, Store store) {
		super(targetTable, toSQL(sourceTable, variableConstantConstraints, variableEqualityConstraints, explicitIRIBoundVariables, exps, varMap, store));
		this.variableConstantConstraints = variableConstantConstraints;
		this.variableEqualityConstraints = variableEqualityConstraints;
		this.targetTable = targetTable;
		this.sourceTable = sourceTable;
	}
	
	
	
	

}
