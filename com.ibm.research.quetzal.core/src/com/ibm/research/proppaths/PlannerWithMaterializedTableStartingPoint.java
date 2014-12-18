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

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.ibm.research.rdf.store.sparql11.model.Expression;
import com.ibm.research.rdf.store.sparql11.model.Pattern;
import com.ibm.research.rdf.store.sparql11.model.Query;
import com.ibm.research.rdf.store.sparql11.model.Variable;
import com.ibm.research.rdf.store.sparql11.planner.Plan;
import com.ibm.research.rdf.store.sparql11.planner.PlanNode;
import com.ibm.research.rdf.store.sparql11.planner.PlanNodeType;
import com.ibm.research.rdf.store.sparql11.planner.Planner;
import com.ibm.wala.util.collections.HashSetFactory;
import com.ibm.wala.util.collections.Pair;
import com.ibm.wala.util.graph.NumberedGraph;

public class PlannerWithMaterializedTableStartingPoint extends Planner {

	public static class MaterializedTableKey implements Planner.Key {

		protected PlanNode materializedTable;
		
		
		public MaterializedTableKey(PlanNode materializedTable) {
			super();
			this.materializedTable = materializedTable;
			assert materializedTable.getType().equals(PlanNodeType.MATERIALIZED_TABLE) : materializedTable;
		}
		
		public PlanNode getMaterializedTable() {
			return materializedTable;
		}

		@Override
		public Set<Variable> gatherVariables() {
			Set<Variable> ret = HashSetFactory.make(materializedTable.getRequiredVariables());
			ret.addAll(materializedTable.getProducedVariables());
			return ret;
		}
		
		@Override
		public boolean isMandatory() {
			return true;
		}
		
	}
	public  class MaterializedTableNode implements Planner.Node {

		protected MaterializedTableKey materializedTableKey;
		protected int id;
		
		public MaterializedTableNode(MaterializedTableKey materializedTableKey, int id) {
			super();
			this.materializedTableKey = materializedTableKey;
			this.id = id;
		}

		@Override
		public PlanNode augmentPlan(Query q, PlanNode currentHead,
				NumberedGraph<PlanNode> g, List<Expression> filters,
				Set<Variable> availableVars, Set<Variable> liveVars) {
			PlanNode node = materializedTableKey.getMaterializedTable().clone();
			node.cost = getCost(g);
			//assert node.cost.fst > 0.0;
			node.setFilters(filters);
			Set<Variable> vars = HashSetFactory.make(availableVars);
			vars.addAll(getProducedVariables());
			vars.retainAll(liveVars);
			node.setAvailableVariables(vars);
			return join(JoinTypes.AND, q, g, currentHead, node, liveVars);
		}

		@Override
		public Set<? extends Key> getKeys() {
			return Collections.singleton(materializedTableKey);
		}

		@Override
		public int getId() {
			return id;
		}

		@Override
		public Kind kind() {
			return Kind.MATERIALIZED_TABLE;
		}

		@Override
		public Pair<Double, Double> getCost(NumberedGraph<PlanNode> g) {
			return Pair.make(0.0, 0.0);
		}

		@Override
		public Set<Variable> getRequiredVariables() {
			return Collections.emptySet();
		}

		@Override
		public Set<Variable> getProducedVariables() {
			return materializedTableKey.gatherVariables();
		}
	}
	public class  GreedyPlannerWithMaterializedTableStartingPoint extends GreedyPlanner {

		protected MaterializedTableKey materializedKey;
		public GreedyPlannerWithMaterializedTableStartingPoint(Query q,
				ApplicableNodes applicableNodes,  MaterializedTableKey materializedKey) {
			super(q, applicableNodes);
			this.materializedKey = materializedKey;
		}
		@Override
		public Set<Key> computeNeededKeys(List<Pattern> region) {
			Set<Key> ret = super.computeNeededKeys(region);
			if (materializedKey!=null) {
				ret.add(materializedKey);
			}
			return ret;
		}
		@Override
		public Set<Node> getApplicableNodes(List<Pattern> region,
				Set<Variable> availableVars, Set<Variable> liveVars,
				Set<Key> neededKeys, Walker walker) {
			Set<Node> ret =  super.getApplicableNodes(region, availableVars, liveVars, neededKeys,
					walker);
			if (materializedKey!=null && neededKeys.contains(materializedKey)) {
				ret.add(new MaterializedTableNode(materializedKey,  ++graphCounterId));
			}
			return ret;
		}
		
		
	}
	
	
	protected MaterializedTableKey materializedTableKey;
	
	public PlannerWithMaterializedTableStartingPoint(PlanNode materializedTable) {
		super();
		materializedTableKey = materializedTable!=null? new MaterializedTableKey(materializedTable): null;
	}

	public PlannerWithMaterializedTableStartingPoint(PlanNodeCreator planFactory, PlanNode materializedTable) {
		super(planFactory, true);
		materializedTableKey =  materializedTable!=null? new MaterializedTableKey(materializedTable): null;
	}

	public Plan plan(Query q, ApplicableNodes applicableNodes) {
		return new RecursiveWalker(
				new GreedyPlannerWithMaterializedTableStartingPoint(q, applicableNodes, materializedTableKey)).plan(q);
	}
}
