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
 package com.ibm.research.rdf.store.sparql11.sqlwriter;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ibm.research.rdf.store.Store.Db2Type;
import com.ibm.research.rdf.store.sparql11.model.Expression;
import com.ibm.research.rdf.store.sparql11.planner.PlanNode;
import com.ibm.wala.util.collections.Pair;

public final class FilterContext {
	private Map<String, Pair<String, String>> varMap;
	private Map<String, Db2Type> varDb2Type;
	private List<String> existsConstraints;
	private List<String> rightTarget;
	private boolean isNegated;
	private PlanNode planNode;
	private List<Expression> constants;

	public FilterContext(Map<String, Pair<String, String>> varMap,
			Map<String, Db2Type> varDb2Type, PlanNode planNode) {
		super();
		this.varMap = varMap;
		this.varDb2Type = varDb2Type;
		this.planNode = planNode;
		this.constants = new LinkedList<Expression>();
	}

	public FilterContext(Map<String, Pair<String, String>> varMap,
			Map<String, Db2Type> varDb2Type, List<String> existsConstraints,
			List<String> rightTarget, boolean isNegated, PlanNode planNode) {
		this(varMap, varDb2Type, planNode);
		this.existsConstraints = existsConstraints;
		this.rightTarget = rightTarget;
		this.isNegated = isNegated;

	}

	public Map<String, Pair<String, String>> getVarMap() {
		return varMap;
	}

	public Map<String, Db2Type> getVarDb2Type() {
		return varDb2Type;
	}

	public List<String> getExistsConstraints() {
		return existsConstraints;
	}

	public List<String> getRightTarget() {
		return rightTarget;
	}

	public boolean getIsNegated() {
		return isNegated;
	}
	
	public PlanNode getPlanNode() {
		return planNode;
	}
	
	public void addConstantNeedsTypeCheck(Expression e) {
		constants.add(e);
	}

	public boolean doesConstantNeedTypeCheck(Expression e) {
		return constants.contains(e);
	}
}
