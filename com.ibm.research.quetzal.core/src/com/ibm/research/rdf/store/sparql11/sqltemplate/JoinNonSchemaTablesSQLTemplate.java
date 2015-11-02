package com.ibm.research.rdf.store.sparql11.sqltemplate;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.ibm.research.rdf.store.Context;
import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.config.Constants;
import com.ibm.research.rdf.store.sparql11.model.Variable;
import com.ibm.research.rdf.store.sparql11.planner.PlanNode;
import com.ibm.research.rdf.store.sparql11.planner.PlanNodeType;
import com.ibm.wala.util.collections.HashSetFactory;

public abstract class JoinNonSchemaTablesSQLTemplate extends AbstractSQLTemplate {

	protected PlanNode pred;

	public JoinNonSchemaTablesSQLTemplate(List<String> templateName, Store store,
			Context ctx, STPlanWrapper wrapper, PlanNode planNode) {
		super(templateName, store, ctx, wrapper, planNode);
		pred = planNode.getPredecessor(wrapper.plan);
	}

	public List<String> getTargetMapping() {
		if (pred == null) {
			return null;
		}
		return Collections.<String>singletonList(wrapper.getPlanNodeCTE(pred, true));
	}
	
		
	public List<String> getJoinConstraintMapping(String leftSQLCte, String rightSQLCte){
		if (pred == null) {
			return null;
		}
		List<String> constraintMapping = new LinkedList<String>();
		Set<Variable> iriBoundVariables = wrapper.getIRIBoundVariables();

		Set<Variable> joinVariables = getJoinVariables();



		for(Variable v : joinVariables){
			String vPredNameLeft = wrapper.getPlanNodeVarMapping(pred,v.getName());
			String vPredNameRight = wrapper.getPlanNodeVarMapping(planNode,v.getName());
			String constraintV = leftSQLCte + "." + vPredNameLeft + " = " + rightSQLCte +  "." + vPredNameRight;
			String constraintVTyp = leftSQLCte + "." + vPredNameLeft + Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS + " = " + rightSQLCte +  "." + vPredNameRight +Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS;
			String undefCostraint = null;
			assert planNode.getType() == PlanNodeType.VALUES || planNode.getType() == PlanNodeType.SERVICE;
			
			if(planNode.getUndefVariables() != null && planNode.getUndefVariables().contains(v)){
				undefCostraint = rightSQLCte + "." + vPredNameRight + "='NULL'";
			}
			if(undefCostraint == null){
				constraintMapping.add(constraintV);
				if(!iriBoundVariables.contains(v)){
					constraintMapping.add(constraintVTyp);
				}
			}
			else {
				String constraint = null; 
				if(iriBoundVariables.contains(v)){
					constraint = "(" + constraintV + " OR " + undefCostraint + ")";
				}
				else{
					constraint = "( (" + constraintV + " AND " + constraintVTyp+") OR " + undefCostraint + ")";
				}
				constraintMapping.add(constraint);
			}
		}
		return constraintMapping;
	}

	protected Set<Variable> getJoinVariables() {
		Set<Variable> joinVariables = HashSetFactory.make();
		if (pred == null) {
			return Collections.emptySet();
		}
		Set<Variable> leftAvailable = pred.getAvailableVariables();
		if(leftAvailable != null){
			joinVariables.addAll(leftAvailable);
		}
		Set<Variable> rightAvailable = planNode.getRequiredVariables();
		if(rightAvailable != null && !rightAvailable.isEmpty()) {
			joinVariables.retainAll(rightAvailable);
		}
		// KAVITHA: In a values node, nothing might be required but if the variable that is 
		// being produced has been produced before by the predecessor, it needs to be 
		// joined
		Set<Variable> rightProduced = planNode.getProducedVariables();
		if(rightProduced != null && !rightProduced.isEmpty()) {
			joinVariables.retainAll(rightProduced);
		}
		return joinVariables;
	}
	

}
