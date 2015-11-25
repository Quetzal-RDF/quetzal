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
 package com.ibm.research.rdf.store.sparql11.sqltemplate;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.research.rdf.store.Context;
import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.config.Constants;
import com.ibm.research.rdf.store.runtime.service.types.TypeMap;
import com.ibm.research.rdf.store.sparql11.model.BinaryUnion;
import com.ibm.research.rdf.store.sparql11.model.BindPattern;
import com.ibm.research.rdf.store.sparql11.model.Expression;
import com.ibm.research.rdf.store.sparql11.model.IRI;
import com.ibm.research.rdf.store.sparql11.model.QueryTripleTerm;
import com.ibm.research.rdf.store.sparql11.model.Variable;
import com.ibm.research.rdf.store.sparql11.planner.PlanNode;
import com.ibm.research.rdf.store.sparql11.sqlwriter.FilterContext;
import com.ibm.research.rdf.store.sparql11.sqlwriter.SQLWriterException;
import com.ibm.wala.util.collections.Pair;

public abstract class SimplePatternSQLTemplate extends AbstractSQLTemplate {
	protected Set<Variable> projectedInPrimary = null;
	//
	protected String tTableColumnPrefix;

	public SimplePatternSQLTemplate(String templateName, Store store,
			Context ctx, STPlanWrapper wrapper, PlanNode planNode) {
		super(templateName, store, ctx, wrapper, planNode);
		this.wrapper.mapPlanNode(planNode);
		if (store.getStoreBackend() == Store.Backend.shark) {
			tTableColumnPrefix = "T.";
		} else {
			tTableColumnPrefix = "";
		}
	}

	protected void addConstantEntrySQLConstraint(QueryTripleTerm entryTerm,
			List<String> entrySQLConstraint, boolean hasSqlType,
			String entryColumn) {
		entrySQLConstraint.add(entryColumn + " = '"
				+ entryTerm.toSqlDataString() + "'");
		if (hasSqlType) {
			if (entryTerm.isConstant() && !entryTerm.isIRI()) {
				if (entryTerm.getConstant().getLiteral() != null
						&& entryTerm.getConstant().getLiteral().getType() != null) {
					String str = entryTerm.getConstant().getLiteral().getType()
							.getValue();
					short s = TypeMap.getDatatypeType(str);
					entrySQLConstraint
							.add(Expression.getTypeTest(s, tTableColumnPrefix
									+ Constants.NAME_COLUMN_PREFIX_TYPE, store));
				} else {
					// KAVITHA: Turns out that when a constant appears in a
					// graph we don't need to look for its value, but rather
					// match its exact type
					// entrySQLConstraint.add(SPARQLToSQLExpression.getTypeTest(entryTerm.getConstant().toDataType(),
					// Constants.NAME_COLUMN_PREFIX_TYPE));
					entrySQLConstraint.add(tTableColumnPrefix
							+ Constants.NAME_COLUMN_PREFIX_TYPE + " = "
							+ entryTerm.getConstant().toDataType());

				}
			}
		}
	}

	protected List<String> getProjectedSQLClause() throws SQLWriterException {
		List<String> projectSQLClause = new LinkedList<String>();
		List<String> entryMap = mapEntryForProject();
		List<String> valMap = mapValForProject();
		List<String> eVarMap = mapExternalVarForProject();
		List<String> graphMap = mapGraphForProject();
		List<String> bindMap = mapBindForProject();

		if (entryMap != null)
			projectSQLClause.addAll(entryMap);
		if (valMap != null)
			projectSQLClause.addAll(valMap);
		if (eVarMap != null)
			projectSQLClause.addAll(eVarMap);
		if (graphMap != null)
			projectSQLClause.addAll(graphMap);
		if (applyBind() && bindMap != null)
			projectSQLClause.addAll(bindMap);

		return projectSQLClause;

	}

	protected abstract boolean applyBind();

	protected List<String> mapBindForProject() throws SQLWriterException {
		return mapBindForProject(varMap);
	}



	abstract List<String> mapEntryForProject();

	abstract List<String> mapValForProject();

	abstract List<String> mapExternalVarForProject();

	protected List<String> mapGraphForProject() {
		BinaryUnion<Variable, IRI> graphRestriction = planNode
				.getGraphRestriction();
		if (graphRestriction != null) {
			if (graphRestriction.isFirstType()) {
				Variable graphVariable = graphRestriction.getFirst();
				if (projectedInPrimary.contains(graphVariable))
					return null;
				projectedInPrimary.add(graphVariable);
				List<String> graphSqlToSparql = new LinkedList<String>();
				graphSqlToSparql.add(Constants.NAME_COLUMN_GRAPH_ID + " AS "
						+ graphVariable.getName());
				return graphSqlToSparql;
			}
		}
		return null;
	}

	protected List<String> getGraphSQLConstraint() {
		List<String> graphSQLConstraint = new LinkedList<String>();
		
		if (planNode.getGraphRestriction() != null) {
			if (planNode.getGraphRestriction().isFirstType()) {
				Variable graphVar = planNode.getGraphRestriction().getFirst();
				PlanNode predecessor = planNode.getPredecessor(wrapper
						.getPlan());
				boolean graphHasConstraintWithPredecessor = false;
				if (predecessor != null) {
					Set<Variable> availableVariables = predecessor
							.getAvailableVariables();
					if (availableVariables != null) {
						if (availableVariables.contains(graphVar)) {
							String graphPredName = wrapper
									.getPlanNodeVarMapping(predecessor,
											graphVar.getName());
							graphSQLConstraint.add(tTableColumnPrefix
									+ Constants.NAME_COLUMN_GRAPH_ID + " = "
									+ wrapper.getPlanNodeCTE(predecessor, false) + "."
									+ graphPredName);
							graphHasConstraintWithPredecessor = true;
						}
					}
				}
				if (!graphHasConstraintWithPredecessor) {
					if (varMap.containsKey(graphVar.getName())) {
						graphSQLConstraint.add(tTableColumnPrefix
								+ Constants.NAME_COLUMN_GRAPH_ID + " = "
								+ varMap.get(graphVar.getName()).fst);
					} else if (!Boolean.TRUE.equals(store.getContext().get(
							Context.unionDefaultGraph))) {
						graphSQLConstraint.add(tTableColumnPrefix
								+ Constants.NAME_COLUMN_GRAPH_ID + " <> 'DEF'");
					}
				}
				// filters go on secondary table
				varMap.put(graphVar.getName(), Pair.make(tTableColumnPrefix
						+ Constants.NAME_COLUMN_GRAPH_ID, (String) null));
			} else {
				String graphTermString = planNode.getGraphRestriction()
						.getSecond().getSqlDataString();
				graphSQLConstraint.add(tTableColumnPrefix
						+ Constants.NAME_COLUMN_GRAPH_ID + " = '"
						+ graphTermString + "'");
			}
		} else if (!Boolean.TRUE.equals(store.getContext().get(
				Context.unionDefaultGraph)) && store.hasGraphs()) {
			graphSQLConstraint.add(tTableColumnPrefix
					+ Constants.NAME_COLUMN_GRAPH_ID + " = 'DEF'");
		}
		if (graphSQLConstraint.isEmpty()) {
			return null;
		}
		return graphSQLConstraint;
	}
	
	protected String getTypeConstraintForIRIs(String typeCol) {
		return "(" + typeCol + " = " + TypeMap.IRI_ID + " OR " + typeCol + " = " + TypeMap.BLANK_NODE_ID + ")";
	}
}
