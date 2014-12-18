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
 package com.ibm.research.rdf.store.sparql11.planner;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.Store.Db2Type;
import com.ibm.research.rdf.store.Store.PredicateTable;
import com.ibm.research.rdf.store.schema.Pair;
import com.ibm.research.rdf.store.sparql11.model.Pattern;
import com.ibm.research.rdf.store.sparql11.model.Pattern.EPatternSetType;
import com.ibm.research.rdf.store.sparql11.model.ProjectedVariable;
import com.ibm.research.rdf.store.sparql11.model.PropertyTerm;
import com.ibm.research.rdf.store.sparql11.model.Query;
import com.ibm.research.rdf.store.sparql11.model.QueryTriple;
import com.ibm.research.rdf.store.sparql11.model.SimplePattern;
import com.ibm.research.rdf.store.sparql11.model.SubSelectPattern;
import com.ibm.research.rdf.store.sparql11.model.Variable;
import com.ibm.research.rdf.store.sparql11.planner.statistics.SPARQLOptimizerStatistics;
import com.ibm.research.rdf.store.sparql11.planner.statistics.StatisticComparator;
import com.ibm.wala.util.collections.HashMapFactory;
import com.ibm.wala.util.collections.HashSetFactory;
import com.ibm.wala.util.graph.impl.SlowSparseNumberedGraph;

//
// A plan is a tree of STPlanNodes.
//
public class Plan implements Comparable<Plan>
   {
   //
   // The pattern for which the plan is built
   //
   private Pattern pattern;

   //
   // A plan is a tree of STPlanNodes whose root is pointed to by planRoot
   //
   public PlanNode planTreeRoot;
   private SlowSparseNumberedGraph<PlanNode> planTree;
   
   //
   // For convenience, we also keep a map of where each variable is required
   // Note that a variable can be required in any number of STPlanNodes
   // We do not need to keep this information for produced variables since
   // all the produced variables in the plan are those that can be produced by
   // the STPlanNode plan root node (planTreeRoot)
   //
   private HashSet<Variable> requiredVariables; 
   
   //
   // The cost of the plan
   //
   private Pair<Double> cost;
   
  
   //
   // Create an empty STPlan
   //
   public Plan()
      {
      this.pattern = null;
      this.planTreeRoot = null;
      this.planTree = SlowSparseNumberedGraph.make();
      }
   
   public void findProductsInPlan() {
	   Iterator<PlanNode> nodes = planTree.getNodeManager().iterator();
	   while (nodes.hasNext()) {
		   nodes.next().setProductFlag(this);
	   }
   }
   
   //
   // Get the pattern of the current STPlan
   //
   public Pattern getPattern()
      {
      return pattern;
      }

   //
   // Get the plan root of the current STPlan
   //
   public PlanNode getPlanRoot()
      {
      return planTreeRoot;
      }

   // 
   // Get the plan tree of the current STPlan
   //
   public SlowSparseNumberedGraph<PlanNode> getPlanTree()
      {
      return planTree;
      }
   
   // 
   // Get the required variables of the current STPlan
   //
   public HashSet<Variable> getRequiredVariables()
      {
      return this.requiredVariables;
      }

   //
   // The cost of a plan is the cost of the root node, or zero if no root node exists
   //
   public Pair<Double> getCost()
      {
      return this.cost;
      }
   
   //
   // Set the pattern for the current STPlan
   //
   public void setPattern(Pattern pattern)
      {
      this.pattern = pattern;
      }
    
   //
   // There are three cases to check for plan compatibility:
   // What is consumed has already been produced:
   //    Case 1: The current and the mergedPlan have the same produced variable and the right part has no required
   //    Case 2: The current plan produces all variables that are required by the otherPlan, 
   //	   Case 3: The current plan and the other plan have the same required variable
   // If any of these cases is true, the plans are compatible and the method returns the intersecting variables
   // IMPORTANT: The cases are not mutually exclusive
   //
   public HashSet<Variable> isCompatible(Plan otherPlan)
      {
	   HashSet<Variable> thisProducedVars = new HashSet<Variable>(this.planTreeRoot.getProducedVariables());
	   HashSet<Variable> variableIntersectionSet = new HashSet<Variable>(); 
      HashSet<Variable> commonProducedVars= new HashSet<Variable>(); 
      commonProducedVars.addAll(thisProducedVars);
      commonProducedVars.retainAll(otherPlan.getPlanRoot().getProducedVariables());  
      HashSet<Variable> commonRequiredVars= new HashSet<Variable>(); 
      commonRequiredVars.addAll(this.getRequiredVariables());
      commonRequiredVars.retainAll(otherPlan.getRequiredVariables());  
	   
	   //
	   // Case 1: Check whether the current plan produces all variables that are required by the otherPlan
	   //       
      	thisProducedVars.retainAll(otherPlan.getRequiredVariables());
		if (!thisProducedVars.isEmpty()) {
			variableIntersectionSet.addAll(thisProducedVars);
			variableIntersectionSet.addAll(commonProducedVars);
		}
      // 
      // Case 2: We are looking for one produced variable that is common between the two
      // And the other plan has no required vars
	   //
      if (variableIntersectionSet.isEmpty() && commonRequiredVars.isEmpty())
         {
         variableIntersectionSet.addAll(commonProducedVars);             
         }    
      
      //
      // Case 3: The two sets of required variables are overlapping
      //
      if (!commonRequiredVars.isEmpty())
         {
         variableIntersectionSet.addAll(commonRequiredVars);
         }
      
      return variableIntersectionSet;
      }
   
   //
   // Returns the union of all variables in this and the otherPlan
   //
   public HashSet<Variable> makeUnionCompatible(Plan otherPlan)
      {
      HashSet<Variable> unionVariableSet = new HashSet<Variable>(this.planTreeRoot.getProducedVariables());
      unionVariableSet.addAll(this.requiredVariables);
      unionVariableSet.addAll(otherPlan.getPlanRoot().getProducedVariables());
      unionVariableSet.addAll(otherPlan.getRequiredVariables());

      return unionVariableSet;
      }
   
   //
   // Returns the union of all variables in this and the otherPlan
   //
   public HashSet<Variable> makeMinusCompatible(Plan otherPlan)
      {
      HashSet<Variable> unionVariableSet = new HashSet<Variable>(this.planTreeRoot.getProducedVariables());
      unionVariableSet.addAll(this.requiredVariables);
      HashSet<Variable> otherVariableSet = new HashSet<Variable>(otherPlan.getPlanRoot().getProducedVariables());
      otherVariableSet.addAll(otherPlan.getRequiredVariables());
      unionVariableSet.retainAll(otherVariableSet);
      return unionVariableSet;
      }
   
   //
   // Returns true if the plans have the same required variables 
   //
   public boolean sameRequirement(Plan otherPlan)
      {
	   HashSet<Variable> thisRequiredVars=this.requiredVariables;
	   HashSet<Variable> otherRequiredVars=otherPlan.getRequiredVariables();
	   if(thisRequiredVars.containsAll(otherRequiredVars) &&
			   otherRequiredVars.containsAll(thisRequiredVars) )return true;
	   return false;
      }
   
   //
   // this method attaches an optional root on top of the plan
   // the purpose is to indicate that the whole graph is optional
   //
   public void makePlanOptional(Pattern optionalPattern)
      {
      PlanNodeType newRootType = PlanNodeType.LEFT;
      PlanNode newRoot = new PlanNode(newRootType, new HashSet<Variable>(), optionalPattern);
      newRoot.addProducedVariables(this.planTreeRoot.getProducedVariables());
      newRoot.setRequiredVariables(this.planTreeRoot.getRequiredVariables());
      this.requiredVariables.addAll(newRoot.getRequiredVariables());
      
      //
      // add the newRoot to the graph and connect it to the old root
      //
      this.planTree.addNode(newRoot);
      this.planTree.addEdge(newRoot, this.planTreeRoot);

      //
      // set the root of this graph to newRoot
      //
      this.planTreeRoot = newRoot;
      // the cost of the STPlan is not changed
      }
   
   //
   // This method attached a SUBSELECT root node on top of the plan 
   //
   public void makePlanSubselect(Pattern subselectPattern)
      {
      HashSet<Variable> operatorVariables = new HashSet<Variable>();
      
      //
      // Create the new node and add as operator variables each selected variable in the SUBSELECT
      //
      for (ProjectedVariable projectedVariable: ((SubSelectPattern)(subselectPattern)).getSelectClause().getProjectedVariables())
         {
         operatorVariables.add(projectedVariable.getVariable());
         }
      PlanNodeType newRootType=PlanNodeType.SUBSELECT;
      PlanNode newRoot = new PlanNode(newRootType, operatorVariables, subselectPattern);
      newRoot.addProducedVariables(operatorVariables);

      //
      // add the newRoot to the graph and connect it to the old root
      //
      this.planTree.addNode(newRoot);
      this.planTree.addEdge(newRoot, this.planTreeRoot);
     
      //
      // set the root of this graph to newRoot
      //
      this.planTreeRoot = newRoot;
      //the cost of the STPlan is not changed
      }
   
   //
   // Method to compare the cost of plans
   //
   public int compareTo(Plan arg0)
      {
      if (StatisticComparator.lessThan(this.getCost(), arg0.getCost()))
         {
         return -1;
         }
      else if (StatisticComparator.equals(this.getCost(), arg0.getCost()))
         {
         return 0;
         }
      else 
         {
         return 1;
         }       
      }

   //
   // Output the contents of the object as a string
   //
   public String toString()
      {
      StringBuffer returnString = new StringBuffer();

      int counter = 0;
      Map<PlanNode,Integer> nodeIds = HashMapFactory.make();
      for(PlanNode n : planTree) {
    	  nodeIds.put(n, counter++);
      }
      
      for(PlanNode n : planTree) {
    	  returnString.append("node: " + nodeIds.get(n) + "\n");
    	  PlanNode p = n.getPredecessor(this);
    	  if (p != null) {
    		  returnString.append("logical predecessor: " + nodeIds.get(p) + "\n");
    	  }
    	  returnString.append(n.toString() + "\n");
    	  for(Iterator<PlanNode> succs = planTree.getSuccNodes(n); succs.hasNext(); ) {
    		  returnString.append("  --> " + nodeIds.get(succs.next()) + "\n");
    	  }
      }

      return returnString.toString();
      }
   
   public boolean isVariableOfSingleType(Store store, Variable v) {
	   PredicateTable predTable = store.getDirectPredicates();
	   Iterator<PlanNode> it = planTree.getNodeManager().iterator();
	   Set<Db2Type> types = HashSetFactory.make();
	   
	   while (it.hasNext()) {
		   PlanNode n = it.next();
		   if (n.getProducedVariables().contains(v) && n.getType() == PlanNodeType.TRIPLE) {
			   PropertyTerm prop = n.getTriple().getPredicate();
			   if (prop.isIRI()) {
				   types.add(predTable.getType(prop.getIRI().getValue()));
			   }
		   }
	   }
	   
	   if (types.size() == 1 && types.iterator().next() != Db2Type.MIXED) {
		   return true;
	   }
	   return false;
   }

public static AccessMethod getSTAccessMethod(QueryTriple triple,
		SPARQLOptimizerStatistics stats, QueryTripleNode n, Query q, PredicateTable revPreds) {
	AccessMethod am = null;
	switch (n.getAccessMethod()) {
	case QueryTripleNode.FILTER_DPH:
	case QueryTripleNode.DPH: {
		if (QueryTripleNode.isNotBoundVariable(n.getQueryTriple().getSubject(), n.getFilterBindings())) { // subject
			// is a
			// variable
			am = new AccessMethod(
					AccessMethodType.DPH_POLL_SUBJECT,
					new com.ibm.research.rdf.store.schema.Pair<Double>(
							stats.globalStatistics.getSubjectStatistic()
									.average()
									* stats.globalStatistics.getObjectStatistic()
											.average(),
							stats.globalStatistics.getSubjectStatistic().stdev()));
		} else if (n.getFilterBindings().containsKey(
				n.getQueryTriple().getSubject().getVariable())) {
			am = new AccessMethod(
					AccessMethodType.DPH_INDEX_SUBJECT,
					new com.ibm.research.rdf.store.schema.Pair<Double>(
							stats.globalStatistics.getSubjectStatistic()
									.average(),
							stats.globalStatistics.getSubjectStatistic().stdev()));
		} else {
			assert (!n.getQueryTriple().getSubject().isVariable());
			am = new AccessMethod(
					AccessMethodType.DPH_INDEX_SUBJECT,
					new com.ibm.research.rdf.store.schema.Pair<Double>(
							stats.globalStatistics.getSubjectStatistic()
									.average(),
							stats.globalStatistics.getSubjectStatistic().stdev()));
		}
		break;
	}
	case QueryTripleNode.FILTER_RPH:
	case QueryTripleNode.RPH: {
		if (QueryTripleNode.isNotBoundVariable(n.getQueryTriple().getObject(), n.getFilterBindings())) { // object
																	// is
			// a
			// variable
			am = new AccessMethod(
					AccessMethodType.RPH_POLL_OBJECT,
					new com.ibm.research.rdf.store.schema.Pair<Double>(
							stats.globalStatistics.getSubjectStatistic()
									.average()
									* stats.globalStatistics.getObjectStatistic()
											.average(),
							stats.globalStatistics.getObjectStatistic().stdev()));
		} else if (n.getFilterBindings().containsKey(
				n.getQueryTriple().getObject().getVariable())) {
			am = new AccessMethod(AccessMethodType.RPH_INDEX_OBJECT,
					new com.ibm.research.rdf.store.schema.Pair<Double>(
							stats.globalStatistics.getObjectStatistic()
									.average(),
							stats.globalStatistics.getObjectStatistic().stdev()));
		} else {
			assert (!n.getQueryTriple().getObject().isVariable());
			am = new AccessMethod(AccessMethodType.RPH_INDEX_OBJECT,
					new com.ibm.research.rdf.store.schema.Pair<Double>(
							stats.globalStatistics.getObjectStatistic()
									.average(),
							stats.globalStatistics.getObjectStatistic().stdev()));
		}
		break;
	}
	case QueryTripleNode.GDPH: {
		if (n.getPattern().getGraphRestriction() != null
				&& n.getPattern().getGraphRestriction().getFirst() != null) { // graph
			// is
			// a
			// variable
			am = new AccessMethod(
					AccessMethodType.DPH_POLL_GRAPH,
					new com.ibm.research.rdf.store.schema.Pair<Double>(
							2 * stats.perGraphSingleStatistic.getGraphStatistic()
									.average(),
							stats.perGraphSingleStatistic.getGraphStatistic()
									.stdev()));
		} else if (n.getPattern().getGraphRestriction() != null
				&& n.getPattern().getGraphRestriction().getFirst() != null
				&& n.getFilterBindings().containsKey(
						n.getPattern().getGraphRestriction().getFirst())) {
			am = new AccessMethod(AccessMethodType.DPH_INDEX_GRAPH,
					new com.ibm.research.rdf.store.schema.Pair<Double>(2
							* stats.perGraphSingleStatistic.getGraphStatistic()
									.average()
							* n.getFilterBindings().get(
									n.getPattern().getGraphRestriction()
											.getFirst()).size(),
							stats.globalStatistics.getObjectStatistic().stdev()));
		} else {
			assert (n.getPattern().getGraphRestriction().getSecond() != null);
			am = new AccessMethod(
					AccessMethodType.DPH_INDEX_GRAPH,
					new com.ibm.research.rdf.store.schema.Pair<Double>(
							2 * stats.perGraphSingleStatistic.getGraphStatistic()
									.average(),
							stats.perGraphSingleStatistic.getGraphStatistic()
									.stdev()));
		}
		break;
	}
	case QueryTripleNode.SCAN:
		Variable subject = n.getQueryTriple().getSubject().getVariable();
		Variable object = n.getQueryTriple().getObject().getVariable();
		boolean needSubject = false;
		boolean needObject = false;
		if (q.isSelectQuery()) {
			for(ProjectedVariable pv : q.getSelectQuery().getSelectClause().getProjectedVariables()) {
				if (pv.getVariable().equals(subject)) {
					needSubject = true;
				}
				if (pv.getVariable().equals(object)) {
					needObject = true;
				}
			}
		}
		for(Pattern p : q.getMainPattern().gatherSubPatterns(true)) {
			if (p.getType() == EPatternSetType.SIMPLE) {
				for(QueryTriple t : ((SimplePattern)p).getQueryTriples()) {
					if (! t.equals(n.getQueryTriple())) {
						Set<Variable> vs = t.gatherVariables();
						if (vs.contains(subject)) {
							needSubject = true;
						}
						if (vs.contains(object)) {
							needObject = true;
						}
					}
				}
			}
		}
		
		boolean revOneToOne =n.getQueryTriple().getPredicate().isIRI()? revPreds.isOneToOne(n.getQueryTriple().getPredicate().getIRI().getValue()): false;
		
		am = new AccessMethod(revOneToOne || (needObject&&!needSubject)? AccessMethodType.RPH_SCAN: AccessMethodType.DPH_SCAN,
				new com.ibm.research.rdf.store.schema.Pair<Double>(Double.MAX_VALUE,
						Double.MAX_VALUE));
		break;
	}
	return am;
}
   }
