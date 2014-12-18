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
import com.ibm.research.rdf.store.sparql11.model.Constant;
import com.ibm.research.rdf.store.sparql11.model.Expression;
import com.ibm.research.rdf.store.sparql11.model.Variable;
import com.ibm.research.rdf.store.sparql11.planner.PlanNode;
import com.ibm.research.rdf.store.sparql11.planner.PlanNodeType;
import com.ibm.research.rdf.store.sparql11.sqlwriter.FilterContext;
import com.ibm.research.rdf.store.sparql11.sqlwriter.SPARQLToSQLExpression;
import com.ibm.research.rdf.store.sparql11.sqlwriter.SQLWriterException;
import com.ibm.wala.util.collections.HashMapFactory;
import com.ibm.wala.util.collections.HashSetFactory;
import com.ibm.wala.util.collections.Pair;

public class InsertIntoTableFromJoinWithPredecessorAndProcessFilters extends InsertIntoTable {
	private static String toSQL(PlanNode node, String sourceTable, 
			PlanNode predecessor, Map<Variable, Constant> var2Constant, Set<Variable> explicitIRIBoundVariables,
			List<Expression> exps, Store store, boolean isPostGres) {
		assert node.getType().equals(PlanNodeType.TRIPLE) : node;
		StringBuffer sql = new StringBuffer();
		String nodeTable = "node";
		String predecessorTable = "pred";
		Map<String, Pair<String, String>> varMap = HashMapFactory.make();
		sql.append("SELECT ");
		boolean firstVar = true;
		for (Variable v: node.getAvailableVariables()) {
			if (firstVar) {
				firstVar = false;
			} else {
				sql.append(", ");
			}
			String table;
			if (node.getProducedVariables().contains(v)
			|| node.getRequiredVariables().contains(v)) {
				table = nodeTable;
			} else {
				assert predecessor != null;
				assert predecessor.getAvailableVariables().contains(v);
				table = predecessorTable;
			}
			Constant c = var2Constant!=null? var2Constant.get(v): null;
			String sqlVarName;
			String sqlVarType;
			if (c == null) {
				sqlVarName = table+"."+v.getName();
				sql.append(sqlVarName);
			} else {
				sqlVarName = "'"+c.toSqlDataString()+"'"+(isPostGres?"::text":"");
				sql.append(sqlVarName);
			}
			sql.append(" AS ").append(v.getName());
			if (!explicitIRIBoundVariables.contains(v)) {
				sql.append(", ");
				if (c == null) {
					sqlVarType = table+"."+v.getName()+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS;
					sql.append(sqlVarType);
				} else {
					sqlVarType = ""+c.toDataType();
					sql.append(sqlVarType);
					
				}
				sql.append(" AS ").append(v.getName()).append(Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);
			} else {
				sqlVarType = null;
			}
			varMap.put(v.getName(), Pair.make(sqlVarName, sqlVarType));
		}
		sql.append(" FROM ");
		sql.append(sourceTable).append(" AS ").append(nodeTable);
		boolean whereAdded = false;
		if (predecessor!=null) {
			assert predecessor.getType().equals(PlanNodeType.MATERIALIZED_TABLE) : predecessor;
			Set<Variable> livePredecessorVariables = HashSetFactory.make(predecessor.getAvailableVariables());
			livePredecessorVariables.retainAll(node.getAvailableVariables());
			/*if (node.getRequiredVariables().containsAll(livePredecessorVariables)) {
				// no need to join. 
				// all live  predecessor available variables are required variables
				// and thus have already been joined to produce the results in sourceTable
				
			} else*/ {
				sql.append(", ").append(predecessor.getMaterialzedTable()).append(" AS ").append(predecessorTable);
				sql.append("\n");
				// join  on common variables
				Set<Variable> commonVariables = HashSetFactory.make();
				// add triple variables
				commonVariables.addAll(node.getProducedVariables());
				commonVariables.addAll(node.getRequiredVariables());
				//
				
				commonVariables.retainAll(predecessor.getAvailableVariables());
				if (!commonVariables.isEmpty()) {
					sql.append(" WHERE ");
					whereAdded = true;
					boolean firstConstraint = true;
					for (Variable jv : commonVariables) {
						if (firstConstraint) {
							firstConstraint = false;
						} else {
							sql.append("\nAND ");
						}
						Constant c = var2Constant!=null? var2Constant.get(jv):null;
						if (c == null) {
							sql.append(predecessorTable).append(".").append(jv.getName()).append("=").append(nodeTable).append(".").append(jv.getName());
							if (!explicitIRIBoundVariables.contains(jv)) {
								sql.append("\nAND ");
								sql.append(predecessorTable).append(".").append(jv.getName()).append(Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS)
								.append("=")
								.append(nodeTable).append(".").append(jv.getName()).append(Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);
							}
						} else {
							sql.append(predecessorTable).append(".").append(jv.getName()).append("=").append("'").append(c.toSqlDataString()).append("'").append((isPostGres?"::text":""));
							if (!explicitIRIBoundVariables.contains(jv)) {
								sql.append("\nAND ");
								sql.append(predecessorTable).append(".").append(jv.getName()).append(Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS)
								.append("=").append(c.toDataType());
							}
						}
					}
				} else {
					//expensive cartesian product
					// do nothing no join condition
				}
			}
			//
		}
		
		if (exps!=null) {
			boolean first = !whereAdded;
			if (!whereAdded) {
				sql.append(" WHERE ");
				whereAdded = true;
			}
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
	
	protected PlanNode node;
	protected String targetTable;
	protected String sourceTable;
	protected PlanNode predecessor;
	protected Map<Variable, Constant> var2Constant;
	public InsertIntoTableFromJoinWithPredecessorAndProcessFilters(String targetTable,
			PlanNode node, String sourceTable, 
			PlanNode predecessor, Map<Variable, Constant> var2Constant,Set<Variable> explicitIRIBoundVariables,
			List<Expression> exps, Store store) {
		super(targetTable, toSQL(node, sourceTable, predecessor, var2Constant, explicitIRIBoundVariables, exps, store, CodeGenerator.isPostGresBackend(store) ));
		this.node = node;
		this.targetTable = targetTable;
		this.sourceTable = sourceTable;
		this.predecessor = predecessor;
		this.var2Constant = var2Constant;
	}
	
	/*protected static List<String> getFilterSQLConstraint(SPARQLToSQLExpression expGenerator, STPlanNode planNode, STPlan plan, Store store) throws SQLWriterException{
		List<String> filterSQLConstraint = new LinkedList<String>();
		for(Expression e :planNode.getProcessedFilters(plan)){
			if( ! planNode.getAvailableVariables().containsAll(e.gatherVariables()))
				continue ;			
			String eSql = expGenerator.getSQLExpression(e, varMap, store);
			filterSQLConstraint.add(eSql);
		}
		return filterSQLConstraint;
	}*/
	
	
}
