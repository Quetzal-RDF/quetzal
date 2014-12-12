package com.ibm.research.rdf.store.sparql11.sqltemplate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.ibm.research.rdf.store.Context;
import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.config.Constants;
import com.ibm.research.rdf.store.sparql11.model.Variable;
import com.ibm.research.rdf.store.sparql11.planner.PlanNode;
import com.ibm.research.rdf.store.sparql11.sqlwriter.SQLWriterException;
import com.ibm.wala.util.collections.Pair;

public class LeftSQLTemplate extends AbstractSQLTemplate {
	PlanNode left;
	PlanNode right;
	
	public LeftSQLTemplate(String templateName, PlanNode planNode,
			Store store, Context ctx, STPlanWrapper wrapper, PlanNode left, PlanNode right) {
		super(templateName, store, ctx, wrapper, planNode);
		this.left = left;
		this.right = right;
		wrapper.mapPlanNode(planNode);
	}

	@Override
	Set<SQLMapping> populateMappings() throws SQLWriterException {
		varMap = new HashMap<String, Pair<String, String>>();
		HashSet<SQLMapping> mappings = new HashSet<SQLMapping>();
		
		List<String> qidSqlParam = new LinkedList<String>();
		qidSqlParam.add(getQIDMapping());
		SQLMapping qidMapping=new SQLMapping("sql_id", qidSqlParam,null);
		mappings.add(qidMapping);
		
		SQLMapping pMapping=new SQLMapping("project", getProjectMapping(),null);
		mappings.add(pMapping);
		
		List<String> target = getTargetMapping();
		SQLMapping tMapping=new SQLMapping("target", target,null);
		mappings.add(tMapping);
		
		SQLMapping oMapping=new SQLMapping("op_constraint", getOperatorConstraintMapping() ,null);
		mappings.add(oMapping);
		
		List<String> filterConstraint = getFilterSQLConstraint();
		SQLMapping filterMapping = new SQLMapping("filter_constraint", filterConstraint,null);
		mappings.add(filterMapping);

		return mappings;
	}
	
	List<String> getProjectMapping(){
		List<String> projectMapping = new LinkedList<String>();
		Set<Variable> operatorVariables=planNode.getOperatorsVariables();
		String leftSQLCte = wrapper.getPlanNodeCTE(left);
		String rightSQLCte = wrapper.getPlanNodeCTE(right); 
		Set<Variable> leftAvailable = left.getAvailableVariables();
		Set<Variable> iriBoundVariables = wrapper.getIRIBoundVariables();
		if(leftAvailable != null){
			for(Variable v : leftAvailable){
				String vPredName = wrapper.getPlanNodeVarMapping(left,v.getName());
				String valName=leftSQLCte+"."+vPredName;
				projectMapping.add(valName+" AS "+v.getName());
				String valType = null;
				if(!iriBoundVariables.contains(v)){
					valType = leftSQLCte+"."+vPredName+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS;
					projectMapping.add(valType+" AS "+v.getName()+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);
				}
				varMap.put(v.getName(), Pair.make(valName, valType));
			}
		}
		Set<Variable> rightAvailable = right.getAvailableVariables();
		if(rightAvailable != null){
			for(Variable v : rightAvailable){
				String vPredName = wrapper.getPlanNodeVarMapping(right,v.getName());
				if(leftAvailable != null && leftAvailable.contains(v))continue;
				if(operatorVariables.contains(v))continue;
				String valName = rightSQLCte+"."+vPredName;
				projectMapping.add(valName+" AS "+v.getName());
				String valType = null;
				if(!iriBoundVariables.contains(v)){
					valType = rightSQLCte+"."+vPredName+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS;
					projectMapping.add(valType+" AS "+v.getName()+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);
				}
				varMap.put(v.getName(), Pair.make(valName, valType));
			}
		}
		return projectMapping;
	}
	
	LinkedList<String> getTargetMapping(){
		LinkedList<String> targetMapping = new LinkedList<String>();
		targetMapping.add(wrapper.getPlanNodeCTE(left));
		targetMapping.add(wrapper.getPlanNodeCTE(right));
		return targetMapping;
	}
	
	List<String> getOperatorConstraintMapping(){
		List<String> constraintMapping = new LinkedList<String>();
		Set<Variable> operatorVariables=left.getAvailableVariables();
		for(Variable v : operatorVariables){
			if (store.getStoreBackend().equalsIgnoreCase(Store.Backend.shark.name())) {
				constraintMapping.add("(" + wrapper.getPlanNodeCTE(right)+"."+v.getName()+" <=> "+wrapper.getPlanNodeCTE(left)+"."+v.getName()+")");
			} else {
				constraintMapping.add("(" + wrapper.getPlanNodeCTE(right)+"."+v.getName()+" = "+wrapper.getPlanNodeCTE(left)+"."+v.getName() + " OR (" + wrapper.getPlanNodeCTE(right)+"."+v.getName()+" is null and "+wrapper.getPlanNodeCTE(left)+"."+v.getName() + " is null))");
			}
		}
		return constraintMapping;
	}
	
	
}
