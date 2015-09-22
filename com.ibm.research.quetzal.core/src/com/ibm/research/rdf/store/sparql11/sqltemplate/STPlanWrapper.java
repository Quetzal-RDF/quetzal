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

import static com.ibm.research.rdf.store.sparql11.planner.AccessMethodType.isDirectAccess;
import static com.ibm.research.rdf.store.sparql11.planner.AccessMethodType.isGraphAccess;
import static com.ibm.research.rdf.store.sparql11.planner.AccessMethodType.isReverseAccess;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.ibm.research.rdf.store.Store.Db2Type;
import com.ibm.research.rdf.store.sparql11.model.BinaryUnion;
import com.ibm.research.rdf.store.sparql11.model.IRI;
import com.ibm.research.rdf.store.sparql11.model.PropertyTerm;
import com.ibm.research.rdf.store.sparql11.model.Query;
import com.ibm.research.rdf.store.sparql11.model.QueryTriple;
import com.ibm.research.rdf.store.sparql11.model.QueryTripleTerm;
import com.ibm.research.rdf.store.sparql11.model.Variable;
import com.ibm.research.rdf.store.sparql11.planner.AccessMethodType;
import com.ibm.research.rdf.store.sparql11.planner.Plan;
import com.ibm.research.rdf.store.sparql11.planner.PlanNode;
import com.ibm.research.rdf.store.sparql11.planner.PlanNodeType;
import com.ibm.wala.util.collections.HashMapFactory;
import com.ibm.wala.util.collections.HashSetFactory;

/**
 * @author mabornea
 *
 */
public class STPlanWrapper {
	
	HashMap<PlanNode,Integer> aliasMap=null;
	HashMap<PlanNode,Integer> nodeMap=null;
	int lastId;
	int queryInfoId=-1;
	Plan plan;
	PlanNode lastMappedNode =null;
	Map<Variable, Variable> variableToRenamedVariable;
	Set<Variable> iriBoundVariablesInQuery = null;
	Map<String,Db2Type> propertyValueTypes = null;
	private final Query q;
	
	public STPlanWrapper(Query q, Plan plan, Map<Variable, Variable> variableToRenamedVariable){
		this(q, plan, variableToRenamedVariable, 1);
	}
		
	public STPlanWrapper(Query q, Plan plan, Map<Variable, Variable> variableToRenamedVariable, int startCTEId ) {
		lastId = startCTEId;
		nodeMap=new HashMap<PlanNode,Integer>();
		aliasMap=new HashMap<PlanNode,Integer>();
		this.q = q;
		this.plan = plan;
		this.variableToRenamedVariable = variableToRenamedVariable;
		if(plan != null)
			iriBoundVariablesInQuery = plan.getPattern().gatherIRIBoundVariables();
		propertyValueTypes = new HashMap<String,Db2Type>();
	}
	
	public Map<String, Db2Type> getPropertyValueTypes() {
		return propertyValueTypes;
	}
	
	public void addProperyValueType(String v, Db2Type t){
		propertyValueTypes.put(v, t);
	}

	/**
	 * returns the renamed variable for a given variable
	 * @param var
	 * @return
	 */
	public Variable getRenamedVariableFor(Variable var) {
		if (variableToRenamedVariable == null) {
			return var;
		}
		Variable ret = variableToRenamedVariable.get(var);
		return ret == null? var: ret;
	}
	
	public Integer mapPlanNode(PlanNode p){
		int pId=lastId;
		nodeMap.put(p, pId);
		lastId++;
		lastMappedNode = p;	
		return pId;
	}
	
	public void mapPlanNodesAs(PlanNode p, int id){
		int pId=lastId;
		aliasMap.put(p, pId);
		lastId++;
		
		nodeMap.put(p, id);
		lastMappedNode = p;
	}

	public Plan getPlan(){
		return plan;
	}
	
	public Query getQuery() {
		return q;
	}
	
	public Integer getPlanNodeId(PlanNode p){
		return nodeMap.get(p);
	}
	
	public String getPlanNodeCTE(PlanNode p, boolean asTarget){
		if(p.getType() == PlanNodeType.MATERIALIZED_TABLE) {
			return p.getMaterialzedTable();
		} else {
			if (aliasMap.containsKey(p)) {
				if (asTarget) {
					assert nodeMap.containsKey(p) : p;
					return "QS"+nodeMap.get(p) + " AS " + "QS"+aliasMap.get(p);
				} else {
					return "QS"+aliasMap.get(p);			
				}
			} else {
				assert nodeMap.containsKey(p) : p;
				return "QS"+nodeMap.get(p);
			}
		}
	}
	
	public Integer getCteIdForSolutionModifier(){
		return queryInfoId;
	}
	
	public void incrementCteIdForSolutionModifier(){
		queryInfoId++;
	}
	
	public String getCtePredecessorForSolutionModifier(){
		if(queryInfoId == 0 && lastMappedNode == null)return null;
		if(queryInfoId == 0 ){
			if(lastMappedNode.getType() == PlanNodeType.MATERIALIZED_TABLE)
				return lastMappedNode.getMaterialzedTable();
			else 
				return "QS"+getPlanNodeId(lastMappedNode);
		}
		else {
			return "QR"+(queryInfoId-1);
		}
	}
		
	private final Map<PlanNode,Map<String,String>> renamedVariables = HashMapFactory.make();
	//private final Map<STPlanNode,HashSet<Expression>> processedFilters = HashMapFactory.make();
	
	
	public boolean reuseSqlTemptable(PlanNode node){
		if (node.getType() == PlanNodeType.REUSE) {
			PlanNode rn = node.getReUseNode();
			if (rn.getType() == PlanNodeType.AND) {
				Iterator<PlanNode> it = plan.getPlanTree().getSuccNodes(rn);
				while (it.hasNext()) {
					PlanNode succ = it.next();
					if (reusableNode(succ)) {
						reuseNode(node, succ);
					}
				}		
				
			} else if (reusableNode(rn)) {
				reuseNode(node, rn);
			}
			return true;
		}
		return false;
	}

	private static boolean reusableNode(PlanNode node) {
		return node.getType() == PlanNodeType.TRIPLE || 
			node.getType() == PlanNodeType.STAR || 
			node.getType() == PlanNodeType.PRODUCT;
	}

	private void reuseNode(PlanNode node, PlanNode rn) {
		mapPlanNodesAs(node, getPlanNodeId(rn));
		Map<String, String> varMap = HashMapFactory.make();
		for (Map.Entry<Variable, Variable> entry : node.getReuseVarMappings().entrySet()) {
			// reverse the mapping so its re-used variable name can be found
			varMap.put(entry.getValue().getName(), entry.getKey().getName());
		}
		renamedVariables.put(node, varMap);
	}
	
	public String getPlanNodeVarMapping(PlanNode predecessor,String varName) {
		String mappedVarName=varName;
		Map<String,String> predRenamedVariables = renamedVariables.get(predecessor);
		if(predRenamedVariables != null && predRenamedVariables.containsKey(varName))
			mappedVarName = predRenamedVariables.get(varName);
		return mappedVarName;
	}
	
	public String getLastVarMappingForQueryInfo(Variable var) {
		if(queryInfoId == 0 ){
			if(lastMappedNode.getAvailableVariables().contains(var))
				return getPlanNodeVarMapping(lastMappedNode,var.getName());			
			else
				return null;
		}
		else{
			return var.getName();
		}
	}
	// TODO [Property Path]: Question for Mihaela: Why two classes named TAPNKey (one in STPlanWrapper and the other in AccessPlantoSQL)
	class TAPNKey {
		class TripleKey {
			private final QueryTripleTerm subject;
			private final PropertyTerm predicate;
			private final QueryTripleTerm object;

			private TripleKey(QueryTripleTerm subject,
					PropertyTerm predicate, QueryTripleTerm object) {
				this.subject = subject;
				this.predicate = predicate;
				this.object = object;
			}

			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result
						+ ((predicate == null) ? 0 : predicate.hashCode());
				return result;
			}

			private String aliasHack(String var) {
				return alias(var);
			}
			
			private boolean compareTerms(QueryTripleTerm mine, TripleKey otherKey, QueryTripleTerm other, boolean produced) {
				if (mine.isVariable() && other.isVariable()) {
					if (produced) {
						return true;
					} else {
						String myName = aliasHack(mine.getVariable().getName());
						return myName.equals(otherKey.aliasHack(other.getVariable().getName()));
					}
				} else {
					return mine.equals(other);
				}
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				TripleKey other = (TripleKey) obj;
				if (object == null) {
					if (other.object != null)
						return false;
				} else if (!compareTerms(object, other, other.object, isDirectAccess(accessMethod)))
					return false;
				if (predicate == null) {
					if (other.predicate != null)
						return false;
				} else if (!predicate.equals(other.predicate))
					return false;
				if (subject == null) {
					if (other.subject != null)
						return false;
				} else if (!compareTerms(subject, other, other.subject, isReverseAccess(accessMethod)))
					return false;
				return true;
			}	
			
			@Override
			public String toString() {
				return subject.toString() + " " + predicate.toString() + " " + object.toString();
			}
		}
		
		private final Set<TripleKey> triples;
		private final AccessMethodType accessMethod;
		private final PlanNode externalProducer;
		private final BinaryUnion<Variable,IRI> graphRestriction;
		
		private Set<TripleKey> set(QueryTriple triple) {
			Set<TripleKey> result = HashSetFactory.make();
				result.add(new TripleKey(triple.getSubject(), triple.getPredicate(), triple.getObject()));			
			return result;
		}
		
		private String alias(String var) {
			if (externalProducer != null && 
				renamedVariables.containsKey(externalProducer) &&
				renamedVariables.get(externalProducer).containsKey(var)) {
				return renamedVariables.get(externalProducer).get(var);
			} else {
				return var;
			}
		}
		
		private boolean compareTerms(BinaryUnion<Variable,IRI> mine, BinaryUnion<Variable,IRI> other, boolean produced) {
			if (mine.isFirstType() && other.isFirstType()) {
				if (produced) {
					return true;
				} else {
					String myName = alias(mine.getFirst().getName());
					return myName.equals(other.getFirst().getName());
				}
			} else {
				return mine.equals(other);
			}
		}

		private TAPNKey(PlanNode planNode) {
			this.accessMethod = planNode.getMethod().getType();
			this.triples = set(planNode.getTriple());
			this.externalProducer = planNode.getPredecessor(plan);
			this.graphRestriction = planNode.getGraphRestriction();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
				+ ((graphRestriction == null || isGraphAccess(accessMethod)) ? 0 : graphRestriction.hashCode());
			result = prime * result
					+ ((accessMethod == null) ? 0 : accessMethod.hashCode());
			result = prime * result
					+ ((triples == null) ? 0 : triples.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TAPNKey other = (TAPNKey) obj;
			if (accessMethod != other.accessMethod)
				return false;
			if (graphRestriction == null) {
				if (other.graphRestriction != null)
					return false;
			} else if (!compareTerms(graphRestriction, other.graphRestriction, isGraphAccess(accessMethod)))
				return false;
			if (externalProducer == null) {
				if (other.externalProducer != null)
					return false;
			} else if (!getPlanNodeId(externalProducer).equals(getPlanNodeId(other.externalProducer))) {				
					return false;				
			}
			if (triples == null) {
				if (other.triples != null)
					return false;
			} else if (!triples.equals(other.triples))
				return false;
			return true;
		}
		
		@Override
		public String toString() {
			return triples.toString() + ":" + 
				accessMethod.toString() + 
				((externalProducer!=null)? "[" + getPlanNodeId(externalProducer) + "]": "");
		}

		public Map<String, String> getAliases(TAPNKey oldKey) {
			Map<String,String> result = HashMapFactory.make();
			for(TripleKey triple : triples) {
				for(TripleKey otherTriple : oldKey.triples) {
					if (triple.equals(otherTriple)) {
			
						if (isDirectAccess(accessMethod)) {
							QueryTripleTerm obj = triple.object;
							QueryTripleTerm otherObj = otherTriple.object;
							if (obj.isVariable() && otherObj.isVariable()) {
								if (! obj.getVariable().getName().equals(otherObj.getVariable().getName())) {
									result.put(obj.getVariable().getName(), otherObj.getVariable().getName());
								}
							}
						}

						if (isReverseAccess(accessMethod)) {
							QueryTripleTerm subj = triple.subject;
							QueryTripleTerm otherSubj = otherTriple.subject;
							if (subj.isVariable() && otherSubj.isVariable()) {
								if (! subj.getVariable().getName().equals(otherSubj.getVariable().getName())) {
									result.put(subj.getVariable().getName(), otherSubj.getVariable().getName());
								}
							}
						}
					}
				}
			}
			return result;
		}
	}
	
	public Set<Variable> getIRIBoundVariables(){
		return iriBoundVariablesInQuery;
	}
		
	public int getNumberOfCTEForSolutionModifier() {
		return  queryInfoId+1;
	}
	
	//Used in the property path component
	public int getLastCTEIdForPlanNodes(){
		return lastId;
	}
}
