package com.ibm.research.proppaths;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.ibm.rdf.store.sparql11.model.Expression;
import com.ibm.rdf.store.sparql11.model.Pattern;
import com.ibm.rdf.store.sparql11.model.Query;
import com.ibm.rdf.store.sparql11.model.Variable;
import com.ibm.rdf.store.sparql11.stopt.Planner;
import com.ibm.rdf.store.sparql11.stopt.STEPlanNodeType;
import com.ibm.rdf.store.sparql11.stopt.STPlan;
import com.ibm.rdf.store.sparql11.stopt.STPlanNode;
import com.ibm.wala.util.collections.HashSetFactory;
import com.ibm.wala.util.collections.Pair;
import com.ibm.wala.util.graph.NumberedGraph;

public class PlannerWithMaterializedTableStartingPoint extends Planner {

	public static class MaterializedTableKey implements Planner.Key {

		protected STPlanNode materializedTable;
		
		
		public MaterializedTableKey(STPlanNode materializedTable) {
			super();
			this.materializedTable = materializedTable;
			assert materializedTable.getType().equals(STEPlanNodeType.MATERIALIZED_TABLE) : materializedTable;
		}
		
		public STPlanNode getMaterializedTable() {
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
		public STPlanNode augmentPlan(Query q, STPlanNode currentHead,
				NumberedGraph<STPlanNode> g, List<Expression> filters,
				Set<Variable> availableVars, Set<Variable> liveVars) {
			STPlanNode node = materializedTableKey.getMaterializedTable().clone();
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
		public Pair<Double, Double> getCost(NumberedGraph<STPlanNode> g) {
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
	
	public PlannerWithMaterializedTableStartingPoint(STPlanNode materializedTable) {
		super();
		materializedTableKey = materializedTable!=null? new MaterializedTableKey(materializedTable): null;
	}

	public PlannerWithMaterializedTableStartingPoint(PlanNodeCreator planFactory, STPlanNode materializedTable) {
		super(planFactory);
		materializedTableKey =  materializedTable!=null? new MaterializedTableKey(materializedTable): null;
	}

	public STPlan plan(Query q, ApplicableNodes applicableNodes) {
		return new RecursiveWalker(
				new GreedyPlannerWithMaterializedTableStartingPoint(q, applicableNodes, materializedTableKey)).plan(q);
	}
}
