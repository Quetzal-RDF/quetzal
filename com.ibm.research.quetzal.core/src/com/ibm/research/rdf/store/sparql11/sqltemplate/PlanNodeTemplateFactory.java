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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.ibm.research.rdf.store.Context;
import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.Store.PredicateTable;
import com.ibm.research.rdf.store.sparql11.model.Expression;
import com.ibm.research.rdf.store.sparql11.model.PropertyTerm;
import com.ibm.research.rdf.store.sparql11.model.QueryTriple;
import com.ibm.research.rdf.store.sparql11.model.SubSelectPattern;
import com.ibm.research.rdf.store.sparql11.model.Variable;
import com.ibm.research.rdf.store.sparql11.planner.Plan;
import com.ibm.research.rdf.store.sparql11.planner.PlanNode;
import com.ibm.research.rdf.store.sparql11.planner.PlanNodeType;
import com.ibm.research.rdf.store.sparql11.sqlwriter.SPARQLToSQLExpression;
import com.ibm.research.rdf.store.sparql11.sqlwriter.SQLWriterException;

public class PlanNodeTemplateFactory {
	
	public static final boolean dynamicAccessType = true;
	public static final boolean noDuplicationOfMultiValues = false;
	
	public static AbstractSQLTemplate createSQLTemplate(PlanNode planNode, Plan plan, Store store, Context ctx, STPlanWrapper wrapper) throws SQLWriterException{
		if(planNode.getType() == PlanNodeType.TRIPLE)
			return createTripleSQLTemplate(planNode, store, ctx, plan, wrapper);
		else if(planNode.getType() == PlanNodeType.LEFT)
			return createLEFTSQLTemplate(planNode, store, ctx, plan, wrapper);
		else if(planNode.getType() == PlanNodeType.UNION)
			return createUnionSQLTemplate(planNode, store, ctx, plan, wrapper);
		else if(planNode.getType() == PlanNodeType.MINUS)
			return createMinusSQLTemplate(planNode, store, ctx, plan, wrapper);
		else if(planNode.getType() == PlanNodeType.STAR)
			return createStarSQLTemplate(planNode, store, ctx, plan, wrapper);
		else if(planNode.getType() == PlanNodeType.NOT_EXISTS || planNode.getType() == PlanNodeType.EXISTS){
			return createNotExistsSQLTemplate(planNode, store, ctx, plan, wrapper);
		} else if (planNode.getType() == PlanNodeType.JOIN) {
			return createJoinSQLTemplate(planNode, store, ctx, plan, wrapper);
		}  else if (planNode.getType() == PlanNodeType.PRODUCT) {
			return createProductSQLTemplate(planNode, store, ctx, plan, wrapper);
		} 	else if(planNode.getType() == PlanNodeType.MATERIALIZED_TABLE){
			return createMaterializedTableSQLTemplate(planNode, store, ctx, plan, wrapper);
		} else if(planNode.getType() == PlanNodeType.VALUES){
			return createValuesSQLTemplate(planNode, store, ctx, plan, wrapper);
		} else if (planNode.getType() == PlanNodeType.SUBSELECT) {
			return createSubselectSQLTemplate(planNode, store, ctx, plan, wrapper);
		} else if (planNode.getType() == PlanNodeType.SERVICE) {
			return createServiceSQLTemplate(planNode, store, ctx, plan, wrapper);
		}
		
		assert planNode.getType() == PlanNodeType.AND : "cannot find template for " + planNode.getType();
		return null;
	}
	
	private static AbstractSQLTemplate createSubselectSQLTemplate(
			PlanNode planNode, Store store, Context ctx, Plan plan,
			STPlanWrapper wrapper) {
		return new SubSelectTemplate("sub_select", planNode, (SubSelectPattern)planNode.getPattern(), store, ctx, wrapper);
	}
	
	private static AbstractSQLTemplate createServiceSQLTemplate(
			PlanNode planNode, Store store, Context ctx, Plan plan,
			STPlanWrapper wrapper) {
		if (planNode.isPost()) {
			return new ServiceSQLTemplate(Arrays.asList("xmlPostData", "serviceHttpPost", "serviceMerge"), planNode, store, ctx, wrapper);
		} else {
			return new ServiceSQLTemplate(Collections.singletonList("service"), planNode, store, ctx, wrapper);
		}
	}

	static AbstractSQLTemplate createTripleSQLTemplate(PlanNode planNode, Store store, Context ctx, Plan plan, STPlanWrapper wrapper) throws SQLWriterException{
		AccessType accessType = computeAccessType(planNode, store, plan);
		if(accessType == AccessType.PRIMARY_ONLY && !planNode.getTriple().getPredicate().isVariable())
			return new TriplePrimaryOnlySQLTemplate("simple_ph_exp1",planNode,store,ctx,wrapper);
		else if(accessType == AccessType.SECONDARY_ONLY){
			return new TripleSecondaryOnlySQLTemplate("simple_ph_exp1",planNode,store,ctx,wrapper);
		}else if(accessType == AccessType.PRIMARY_ONLY && planNode.getTriple().getPredicate().isVariable()){
			assert false; 
			//return new TripleAllPredicatesSQLTemplate("triple_all_predicates",planNode,store,ctx,wrapper);
		}
		else if(accessType == AccessType.BOTH_OUTER_JOIN && planNode.getTriple().getPredicate().isVariable()){
			return new TripleAllPredicatesBothSQLTemplate("triple_all_predicates_both",planNode,store,ctx,wrapper);
		}
		else if(extractAllPredicates(planNode)){
			return new TripleAllPredicatesBothSQLTemplate("triple_all_predicates_both",planNode,store,ctx,wrapper);
		}
		return null;
	}

	static AbstractSQLTemplate createStarSQLTemplate(PlanNode planNode, Store store, Context ctx, Plan plan, STPlanWrapper wrapper){
		AccessType accessType = computeAccessType(planNode, store, plan);
		if(accessType == AccessType.PRIMARY_ONLY)
			return new StarPrimaryOnlySQLTemplate("simple_ph_exp1",planNode,store,ctx,wrapper);
		else
			return new StarBothSQLTemplate("star_both",planNode,store,ctx,wrapper);
	}
	
	static AbstractSQLTemplate createLEFTSQLTemplate(PlanNode planNode, Store store, Context ctx, Plan plan, STPlanWrapper wrapper){
		Iterator<PlanNode> successors = plan.getPlanTree().getSuccNodes(planNode);
		PlanNode left = successors.next().getTempTableNode(plan);
		PlanNode right = successors.next().getTempTableNode(plan);
		
		return new LeftSQLTemplate("left", planNode,store, ctx,  wrapper, left, right);		
		
	}
	
	static AbstractSQLTemplate createProductSQLTemplate(PlanNode planNode, Store store, Context ctx, Plan plan, STPlanWrapper wrapper){
		Iterator<PlanNode> successors = plan.getPlanTree().getSuccNodes(planNode);
		PlanNode left = successors.next().getTempTableNode(plan);
		PlanNode right = successors.next().getTempTableNode(plan);
		return new ProductSQLTemplate("product", planNode,store, ctx,  wrapper, left, right);
	}
	
	
	@SuppressWarnings({ "rawtypes" })
	static AbstractSQLTemplate createJoinSQLTemplate(PlanNode planNode, Store store, Context ctx, Plan plan, STPlanWrapper wrapper){
		Iterator<PlanNode> successors = plan.getPlanTree().getSuccNodes(planNode);
		PlanNode left = successors.next().getTempTableNode(plan);
		PlanNode right = successors.next().getTempTableNode(plan);
		
		return new JoinSQLTemplate("join", planNode,store, ctx,  wrapper, left, right);
	}
	
	static AbstractSQLTemplate createMaterializedTableSQLTemplate(PlanNode planNode, Store store, Context ctx, Plan plan, STPlanWrapper wrapper){
		return new MaterializedTableSQLTemplate("", planNode,store, ctx,  wrapper);		
	}
	
	static AbstractSQLTemplate createValuesSQLTemplate(PlanNode planNode, Store store, Context ctx, Plan plan, STPlanWrapper wrapper){
		return new ValuesSQLTemplate(Collections.singletonList("values"), planNode,store, ctx,  wrapper);		
	}
	static AbstractSQLTemplate createUnionSQLTemplate(PlanNode planNode, Store store, Context ctx, Plan plan, STPlanWrapper wrapper){
		Iterator<PlanNode> successors = plan.getPlanTree().getSuccNodes(planNode);
		PlanNode left = successors.next().getTempTableNode(plan);
		PlanNode right = successors.next().getTempTableNode(plan);
		return new UnionSQLTemplate("union", planNode,store, ctx,  wrapper, left, right);
	}
	
	static AbstractSQLTemplate createMinusSQLTemplate(PlanNode planNode, Store store, Context ctx, Plan plan, STPlanWrapper wrapper){
		Iterator<PlanNode> successors = plan.getPlanTree().getSuccNodes(planNode);
		PlanNode left = successors.next().getTempTableNode(plan);
		PlanNode right = successors.next().getTempTableNode(plan);
		return new MinusSQLTemplate("minus", planNode,store, ctx,  wrapper, left, right);
	}
	
	static AbstractSQLTemplate createNotExistsSQLTemplate(PlanNode planNode, Store store, Context ctx, Plan plan, STPlanWrapper wrapper){
		Iterator<PlanNode> successors = plan.getPlanTree().getSuccNodes(planNode);
		Iterator<PlanNode> predecessors = plan.getPlanTree().getSuccNodes(planNode);

		PlanNode left = successors.next().getTempTableNode(plan);
		PlanNode right = successors.next().getTempTableNode(plan);
		boolean isNegated = planNode.getType() == PlanNodeType.NOT_EXISTS ? true : false;
		return new NotExistsSQLTemplate("not_exists", planNode,store, ctx,  wrapper, left, right, isNegated);
	}
	
	enum AccessType {
		PRIMARY_ONLY, SECONDARY_ONLY, BOTH_UNION, BOTH_OUTER_JOIN
	};
	
	private static AccessType computeAccessType(PlanNode tapn, Store store, Plan plan) {
		if (!dynamicAccessType) {
			return AccessType.BOTH_OUTER_JOIN;
		}
		PredicateTable oneToOnePredicates = getOneToOnePredicates(tapn, store);

		Set<QueryTriple> triples = new HashSet<QueryTriple>();
		if(tapn.getType()==PlanNodeType.TRIPLE){
			triples.add(tapn.getTriple());
		}
		if(tapn.getType()==PlanNodeType.STAR){
			triples.addAll(tapn.starTriples);
			if(tapn.starOptionalTriples != null)
				triples.addAll(tapn.starOptionalTriples);
		}
		
		AccessType accessType = null;
		for (QueryTriple qt : triples) {
			PropertyTerm t = qt.getPredicate();
			if (t.isIRI()) {
				if (oneToOnePredicates.isOneToOne(t.getIRI().getValue()) || oneToOnePredicates.getHashes(t.getIRI().getValue())==null) {
					if (accessType == null) {
						accessType = AccessType.PRIMARY_ONLY;
					} else if (accessType != AccessType.PRIMARY_ONLY) {
						accessType = AccessType.BOTH_OUTER_JOIN;
					}
				} else {
					if (accessType == null) {
						accessType = AccessType.SECONDARY_ONLY;
					} else {
						accessType = AccessType.BOTH_OUTER_JOIN;
					}
				}
			} else if (t.isVariable()) {
				Variable predicateVariable=qt.getPredicate().getVariable();
				Set<String> predicateBindings = new HashSet<String>();
				for (Expression e : tapn.getFilters()) {
					try {
						predicateBindings.addAll(SPARQLToSQLExpression
								.getVarFilterBinding(e, predicateVariable));
					} catch (SQLWriterException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				boolean singleType=false;
				boolean multiType=false;
				for(String s: predicateBindings){
					if(oneToOnePredicates.isOneToOne(s)) {
						singleType=true;
					} else {
						multiType=true;
					}
				}
				if (singleType &&!multiType)
					accessType=AccessType.PRIMARY_ONLY;
				else if(!singleType && multiType)
					accessType=AccessType.SECONDARY_ONLY;
				else 
					accessType = AccessType.BOTH_OUTER_JOIN;
			} else {
				// TODO [Property Path]: handle complex property path
				assert t.isPath() && !t.isIRI(): t;
			}
		}		
		if (noDuplicationOfMultiValues && accessType == AccessType.SECONDARY_ONLY) {
			accessType = AccessType.BOTH_UNION;
		}
		return accessType;
	}
	
	/*private static boolean isMainTableScan(STPlanNode tapn, QueryTriple t, STPlan pTree) {
		if (tapn.getMethod().getType() == STEAccessMethodType.DPH_SCAN) {
			return true;
		} else if (tapn.getMethod().getType() == STEAccessMethodType.RPH_SCAN) {
			return true;
			
		} else {
			return false;
		}
	}*/
	
	private static PredicateTable getOneToOnePredicates(PlanNode tapn, Store store) {
		PredicateTable oneToOnePredicates = null;
		switch (tapn.getMethod().getType()) {
		case DPH_INDEX_SUBJECT:
		case DPH_SCAN:
		case DPH_INDEX_GRAPH:
		case DPH_POLL_SUBJECT:
		case DPH_POLL_GRAPH:
			oneToOnePredicates = store.getDirectPredicates();
			break;
		case RPH_INDEX_OBJECT:
		case RPH_SCAN:
		case RPH_INDEX_GRAPH:
		case RPH_POLL_GRAPH:
		case RPH_POLL_OBJECT:
			oneToOnePredicates = store.getReversePredicates();
			break;
		}
		return oneToOnePredicates;
	}
	
	private static boolean extractAllPredicates(PlanNode planNode) throws SQLWriterException{
		
		if(planNode.getTriple().getPredicate().isVariable()){
			Variable predicateVariable=planNode.getTriple().getPredicate().getVariable();
			Set<String> predicateBindings = new HashSet<String>();
			for (Expression e : planNode.getFilters()) {
				predicateBindings.addAll(SPARQLToSQLExpression
						.getVarFilterBinding(e, predicateVariable));
			}
			if(predicateBindings.size() == 0)return true;
		}
		return false;
	}
}
