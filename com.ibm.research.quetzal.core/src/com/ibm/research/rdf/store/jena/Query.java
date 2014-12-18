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
 package com.ibm.research.rdf.store.jena;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.QueryVisitor;
import com.hp.hpl.jena.query.SortCondition;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.core.VarExprList;
import com.hp.hpl.jena.sparql.core.describe.DescribeHandler;
import com.hp.hpl.jena.sparql.core.describe.DescribeHandlerFactory;
import com.hp.hpl.jena.sparql.core.describe.DescribeHandlerRegistry;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.ExprAggregator;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.Template;
import com.ibm.research.rdf.store.config.Constants;
import com.ibm.research.rdf.store.jena.impl.DB2DescribeHandler;
import com.ibm.research.rdf.store.query.QueryProcessorFactory;
import com.ibm.research.rdf.store.sparql11.model.AskQuery;
import com.ibm.research.rdf.store.sparql11.model.BinaryUnion;
import com.ibm.research.rdf.store.sparql11.model.ConstructQuery;
import com.ibm.research.rdf.store.sparql11.model.DatasetClause;
import com.ibm.research.rdf.store.sparql11.model.DescribeQuery;
import com.ibm.research.rdf.store.sparql11.model.Expression;
import com.ibm.research.rdf.store.sparql11.model.Expression.EExpressionType;
import com.ibm.research.rdf.store.sparql11.model.GroupCondition;
import com.ibm.research.rdf.store.sparql11.model.HavingCondition;
import com.ibm.research.rdf.store.sparql11.model.IRI;
import com.ibm.research.rdf.store.sparql11.model.LimitOffsetClauses;
import com.ibm.research.rdf.store.sparql11.model.OrderCondition;
import com.ibm.research.rdf.store.sparql11.model.ProjectedVariable;
import com.ibm.research.rdf.store.sparql11.model.SelectClause;
import com.ibm.research.rdf.store.sparql11.model.SelectClause.ESelectModifier;
import com.ibm.research.rdf.store.sparql11.model.SelectQuery;
import com.ibm.research.rdf.store.sparql11.model.SolutionModifiers;
import com.ibm.research.rdf.store.sparql11.model.Variable;
import com.ibm.research.rdf.store.sparql11.model.VariableExpression;

public class Query extends com.hp.hpl.jena.query.Query {



	private String sparql;
	private SelectQuery selectQuery;
	private ConstructQuery constructQuery;
	private DescribeQuery describeQuery;
	private AskQuery askQuery;

	private com.ibm.research.rdf.store.sparql11.model.Query internalQ;

	public Query(String sparql) {
		super();
		this.sparql = sparql;

		doOperations(null);
	}

	public Query(File f) {
		sparql = f.getPath();
		doOperations(f);
	}
	
	public Query(com.ibm.research.rdf.store.sparql11.model.Query db2query) {
		sparql = db2query.getOrigQueryString()!=null? db2query.getOrigQueryString(): db2query.toString();
		internalQ = db2query;
		doOperations(null);
	}

	private void doOperations(File f) {
		if (f == null) {
			if (internalQ == null) {
				internalQ =  (com.ibm.research.rdf.store.sparql11.model.Query) QueryProcessorFactory
					.parse(sparql);
			}
		} else {
			internalQ = (com.ibm.research.rdf.store.sparql11.model.Query) QueryProcessorFactory
					.parse(f);
		}

		if (isSelectType()) {
			selectQuery = internalQ.getSelectQuery();
		} else if (isConstructType()) {
			constructQuery = internalQ.getConstructQuery();
		} else if (isDescribeType()) {
			describeQuery = internalQ.getDescribeQuery();
			if (isDefaultDescribeHandlerSet) {
				setDescribeHandler();
			}
		} else if (isAskType()) {
			askQuery = internalQ.getAskQuery();
		}
	}

	// To remove ARQ's DescribeHandler and adding DB2DescribeHandler.
	// static field, because it is required only once per JVM.
	private static boolean isDefaultDescribeHandlerSet = true;

	synchronized private void setDescribeHandler() {
		DescribeHandlerRegistry.get().add(new DescribeHandlerFactory() {
			public DescribeHandler create() {
				return new DB2DescribeHandler();
			}
		});

		isDefaultDescribeHandlerSet = false;
	}

	public String toString() {
		return serialize();
	}

	public boolean isAskType() {
		return internalQ.isAskQuery();
	}

	public boolean isSelectType() {
		return internalQ.isSelectQuery();
	}

	public boolean isConstructType() {
		return internalQ.isConstructQuery();
	}

	public boolean isDescribeType() {
		return internalQ.isDescribeQuery();
	}

	public boolean isDistinct() {
		return internalQ.isDistinct();
	}

	@Override
	public List<ExprAggregator> getAggregators() {
		throw new RdfStoreException("Not implemented");
	}

	@Override
	public Template getConstructTemplate() {
		throw new RdfStoreException("Not implemented");
	}

	@Override
	public List<String> getGraphURIs() {
		List<String> graphURIs = new ArrayList<String>();
		if (isSelectType()) {
			List<DatasetClause> datasetClause = selectQuery.getDatasetClauses();
			for (int i = 0; i < datasetClause.size(); i++) {
				DatasetClause clause = datasetClause.get(i);
				if (!clause.isNamed()) {
					graphURIs.add(clause.getIri().toString());
				}
			}
		}
		return graphURIs;
	}

	@Override
	public VarExprList getGroupBy() {
		VarExprList list = new VarExprList();
		GroupCondition condition = selectQuery.getSolutionModifier()
				.getGroupClause();
		if (condition != null) {
			List<Expression> expressions = condition.getConditions();
			for (Expression expression : expressions) {
				// Set<Variable> variables = expression.gatherVariables();
				// System.out.println(expression.getType());
			}
		}
		return list;
	}

	@Override
	public List<Expr> getHavingExprs() {
		List<Expr> exprs = new ArrayList<Expr>();
		HavingCondition condition = selectQuery.getSolutionModifier()
				.getHavingClause();
		if (condition != null) {
			List<Expression> expressions = condition.getConstraints();
			for (Expression expression : expressions) {
				expression.getType();
			}
		}
		return exprs;
	}

	@Override
	public long getLimit() {
		SolutionModifiers modifiers = selectQuery.getSolutionModifier();
		LimitOffsetClauses clauses = modifiers.getLimitOffset();
		if (clauses == null) {
			return Long.MIN_VALUE;
		}
		return clauses.getLimit();
	}

	@Override
	public List<String> getNamedGraphURIs() {
		List<String> graphURIs = new ArrayList<String>();
		if (isSelectType()) {
			List<DatasetClause> datasetClause = selectQuery.getDatasetClauses();
			for (int i = 0; i < datasetClause.size(); i++) {
				DatasetClause clause = datasetClause.get(i);
				if (clause.isNamed()) {
					graphURIs.add(clause.getIri().toString());
				}
			}
		}
		return graphURIs;
	}

	@Override
	public long getOffset() {
		SolutionModifiers modifiers = selectQuery.getSolutionModifier();
		LimitOffsetClauses clauses = modifiers.getLimitOffset();
		if (clauses == null) {
			return Long.MIN_VALUE;
		}
		return clauses.getOffset();
	}

	@Override
	public List<SortCondition> getOrderBy() {
		List<SortCondition> conditions = new ArrayList<SortCondition>();
		List<OrderCondition> orderConditions = selectQuery
				.getSolutionModifier().getOrderClause();
		for (OrderCondition condition : orderConditions) {
			Var expr = null;
			int dir = ORDER_UNKNOW;
			if (condition.getType() == OrderCondition.EOrderType.ASC) {
				dir = ORDER_ASCENDING;
			} else if (condition.getType() == OrderCondition.EOrderType.DESC) {
				dir = ORDER_DESCENDING;
			}

			SortCondition sortCondition = new SortCondition(expr, dir);
			conditions.add(sortCondition);
		}
		return conditions.size() > 0 ? conditions : null;
	}

	@Override
	public VarExprList getProject() {
		List<ProjectedVariable> variables = selectQuery.getSelectClause()
				.getProjectedVariables();
		VarExprList list = new VarExprList();
		for (ProjectedVariable variable : variables) {
			Var var = Var.alloc(variable.getVariable().getName());
			list.add(var);
		}

		return list;
	}

	@Override
	public Element getQueryPattern() {
		throw new RdfStoreException("Not implemented");
	}

	@Override
	public int getQueryType() {
		if (isSelectType()) {
			return QueryTypeSelect;
		} else if (isConstructType()) {
			return QueryTypeConstruct;
		} else if (isDescribeType()) {
			return QueryTypeDescribe;
		} else if (isAskType()) {
			return QueryTypeAsk;
		}
		return QueryTypeUnknown;
	}

	public String getQueryTypeString() {
		if (isSelectType()) {
			return "SELECT";
		} else if (isConstructType()) {
			return "CONSTRUCT";
		} else if (isDescribeType()) {
			return "DESCRIBE";
		} else if (isAskType()) {
			return "ASK";
		}
		return "UnKnown";
	}

	@Override
	public List<Node> getResultURIs() {
		List<Node> list = new ArrayList<Node>();
		if (describeQuery != null) {
			List<BinaryUnion<Variable, IRI>> binaryUnions = describeQuery
					.getResources();
			if (binaryUnions != null) {
				for (BinaryUnion<Variable, IRI> binaryUnion : binaryUnions) {
					if (binaryUnion.getSecond() != null) {
						String v = binaryUnion.getSecond().toString();
						Node e = Node.createURI(v);
						list.add(e);
					}
				}
			}
		}
		return list;
	}

	@Override
	public List<String> getResultVars() {

		// If project variables in not empty , return the set of projected
		// variables
		// Else check if group by clause is there , return the set of variables
		// in group by clause
		// Else return all query pattern variables
		Set<Variable> variables = new HashSet<Variable>();
		if (selectQuery.getSelectClause().getProjectedVariables() != null
				&& !selectQuery.getSelectClause().getProjectedVariables()
						.isEmpty()) {
			List<ProjectedVariable> pvars = selectQuery.getSelectClause()
					.getProjectedVariables();
			for (ProjectedVariable var : pvars) {
				variables.add(var.getVariable());
			}
		} else if (selectQuery.getSolutionModifier().getGroupClause() != null) {
			GroupCondition groupCondition = selectQuery.getSolutionModifier()
					.getGroupClause();
			for (Expression expr : groupCondition.getConditions()) {
				if (EExpressionType.VAR == expr.getType()) {
					VariableExpression varExpr = (VariableExpression) expr;
					if (varExpr.getExpression() != null) {
						variables.add(new Variable(varExpr.getVariable()));
					} else {
						variables.addAll(expr.gatherVariables());
					}
				}
			}
		} else {
			variables = selectQuery.getAllPatternVariables();
		}

		List<String> list = new ArrayList<String>();
		if (variables != null) {
			for (Variable variable : variables) {
				if (!(variable.isSystemVariable() || variable.toString()
						.startsWith(Constants.PREFIX_BLANK_NODE))) {
					list.add(variable.getName());
				}
			}
		}
		return list;
	}

	@Override
	public Syntax getSyntax() {
		return Syntax.syntaxSPARQL_10;
	}

	@Override
	public boolean hasAggregators() {
		throw new RdfStoreException("Not implemented");
	}

	@Override
	public boolean hasDatasetDescription() {
		return false;
		// throw new RdfStoreException("Not implemented");
	}

	@Override
	public boolean hasGroupBy() {
		return !getGroupBy().isEmpty();
	}

	@Override
	public boolean hasHaving() {
		return !getHavingExprs().isEmpty();
	}

	@Override
	public boolean hasLimit() {
		SolutionModifiers modifiers = selectQuery.getSolutionModifier();
		LimitOffsetClauses clauses = modifiers.getLimitOffset();
		if (clauses == null || clauses.getLimit() <= 0) {
			return false;
		}
		return true;
	}

	@Override
	public boolean hasOffset() {
		SolutionModifiers modifiers = selectQuery.getSolutionModifier();
		LimitOffsetClauses clauses = modifiers.getLimitOffset();
		if (clauses == null || clauses.getOffset() <= 0) {
			return false;
		}
		return true;
	}

	@Override
	public boolean hasOrderBy() {
		return getOrderBy() == null ? false : true;
	}

	@Override
	public boolean isQueryResultStar() {
		throw new RdfStoreException("Not implemented");
	}

	@Override
	public boolean isReduced() {
		if (!isSelectType()) {
			return false;
		}
		SelectClause clause = selectQuery.getSelectClause();
		if (clause != null) {
			ESelectModifier selectModifier = clause.getSelectModifier();
			if (selectModifier != null) {
				if (selectModifier.compareTo(ESelectModifier.REDUCED) == 0) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean isStrict() {
		throw new RdfStoreException("Not implemented");
	}

	@Override
	public boolean isUnknownType() {
		return getQueryType() == QueryTypeUnknown;
	}

	@Override
	public boolean usesGraphURI(String uri) {
		return getGraphURIs().contains(uri);
	}

	@Override
	public boolean usesNamedGraphURI(String uri) {
		return getNamedGraphURIs().contains(uri);
	}

	/*@Override
	public void validate() {
		// do nothing.. if parsed successfully then validated.
	}*/

	@Override
	public void visit(QueryVisitor visitor) {
		throw new RdfStoreException("Not supported");
	}

	public com.ibm.research.rdf.store.sparql11.model.Query getDB2Query() {
		return internalQ;
	}

	public String serialize() {
		return sparql;
	}
}