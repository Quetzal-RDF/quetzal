package com.ibm.rdf.store.sparql11.sqltemplate;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.rdf.store.Context;
import com.ibm.rdf.store.Store;
import com.ibm.rdf.store.sparql11.model.Query;
import com.ibm.rdf.store.sparql11.model.Variable;
import com.ibm.rdf.store.sparql11.stopt.STPlan;
import com.ibm.rdf.store.sparql11.stopt.STPlanNode;
import com.ibm.wala.util.collections.Pair;

public class SQLGenerator {
	Store store;
	Context ctx;
	STPlanWrapper wrapper;
	STPlan plan;
	Query q;
	private int cteStartID;

	public SQLGenerator( STPlan plan, Query q, Store store, Context ctx) {
		this(plan, q, store, ctx, 0);
	}
	public SQLGenerator( STPlan plan, Query q, Store store, Context ctx, int cteStartID) {
		this.store = store;
		this.ctx = ctx;
		this.plan = plan;
		this.q = q;
		this.cteStartID = cteStartID;
	}

	/**
	 * returns a pair of strings consisting of the CTE definitions and the main select statement, and an integer corresponding to an upper bound on the number of created CTEs (note the number
	 * of actually created CTEs may be less than it).
	 */
	public Pair<Pair<String, String>, Integer> toSQLDetailed(Map<Variable, Variable> var2NewName) throws Exception{
		
		this.wrapper = new STPlanWrapper(plan,  var2NewName, cteStartID);
		
		StringBuffer cteDefinitions =  new StringBuffer();
		if(plan != null)
			cteDefinitions.append(toSQL(plan.getPlanRoot()));
		
		SolutionModifierSqlGenerator gen = new SolutionModifierSqlGenerator(q,wrapper,store,ctx); 
		String solutionModifierCTEDefinitions = gen.toSolutionModifierSQL();
		if (solutionModifierCTEDefinitions!=null) {
			cteDefinitions.append(solutionModifierCTEDefinitions);
		}
		String solutionModifierCTEMainSelect = gen.toTopQuerySQL();
		int cteCount = wrapper.getLastCTEIdForPlanNodes() - cteStartID;
		cteStartID = wrapper.getLastCTEIdForPlanNodes();
		return Pair.make(Pair.make(cteDefinitions.toString(), solutionModifierCTEMainSelect), cteCount);

	}
	
	public String toSQL(Pair<String, String> cteDefinitionMainSelectPair) {
		if (cteDefinitionMainSelectPair.fst.trim().isEmpty()) {
			return cteDefinitionMainSelectPair.snd;
		} else {
			return "WITH " +cteDefinitionMainSelectPair.fst+"\n"+cteDefinitionMainSelectPair.snd;
		}
	}
	public String toSQL(Map<Variable, Variable> var2NewName) throws Exception{
		
		this.wrapper = new STPlanWrapper(plan,  var2NewName, cteStartID);
		
		StringBuffer cteDefinitions =  new StringBuffer();
		if(plan != null)
			cteDefinitions.append(toSQL(plan.getPlanRoot()));
		
		SolutionModifierSqlGenerator gen = new SolutionModifierSqlGenerator(q,wrapper,store,ctx); 
		String solutionModifierSQL = gen.toSQL();
		StringBuffer sqlString = new StringBuffer();
		// handle the case of empty cte definition
		if (cteDefinitions.length()>0 || wrapper.getNumberOfCTEForSolutionModifier()>1) 
		{
			sqlString.append("WITH ");
			sqlString.append(cteDefinitions);
		}
		sqlString.append(solutionModifierSQL);
		cteStartID = wrapper.getLastCTEIdForPlanNodes();
		return sqlString.toString();

	}

	public String toSQL() throws Exception{
		return toSQL((Map<Variable, Variable>)null);
	}
	/**
	 /**
	 * returns a pair of strings consisting of the CTE definitions and the main select statement, and an integer corresponding to an upper bound on the number of created CTEs (note the number
	 * of actually created CTEs may be less than it).
	 * @param n
	 * @param project
	 * @param explicitIRIBoundVariables
	 * @param includeWITH
	 * @return
	 * @throws Exception
	 */
	public Pair<Pair<String, String>, Integer> toSQLWithRootDetailed(STPlanNode n, List<Variable> project, Set<Variable> explicitIRIBoundVariables) throws Exception{
		this.wrapper = new STPlanWrapper(plan, null, cteStartID);
		StringBuffer cteDefinitions =  new StringBuffer();
		if(plan != null) {
			// make sure that all direct or indirect predecessors have been properly defined
			STPlanNode predecessor;
			STPlanNode node = n;
			List<STPlanNode> predecessorChain = new LinkedList<STPlanNode>();
			while ((predecessor = node.getPredecessor(plan))!=null) {
				predecessorChain.add(0,predecessor);
				node = predecessor;
			}
			boolean first = true;
			for (STPlanNode pred: predecessorChain) {
				String predSql = toSQL(pred);
				if (predSql!=null && predSql.trim().length()>0) {
					if (first) {
						first = false;
					} else {
						cteDefinitions.append(", ");
					}
					cteDefinitions.append(predSql);
				}
			}
			//
			String sql = toSQL(n);
			if (sql!=null && sql.trim().length()>0) {
				if (!first) {
					cteDefinitions.append(", ");
				}
				cteDefinitions.append(sql);
			}
		}
		
		SolutionModifierSqlGenerator gen = new SolutionModifierSqlGenerator(q,wrapper,store,ctx); 
		String mainSelect = gen.toSQLWithoutResultModifier(project, explicitIRIBoundVariables);
		int cteCount = wrapper.getLastCTEIdForPlanNodes() - cteStartID;
		cteStartID = wrapper.getLastCTEIdForPlanNodes();
		return Pair.make(Pair.make(cteDefinitions.toString(), mainSelect), cteCount);

	}
	
	public String  toSQLWithRoot(STPlanNode n, List<Variable> project, Set<Variable> explicitIRIBoundVariables) throws Exception {
		Pair<Pair<String, String>, Integer> res = toSQLWithRootDetailed(n, project, explicitIRIBoundVariables);
		String cteDefinitions = res.fst.fst;
		int cteCount = res.snd;
		if (cteCount == 0) {
			assert cteDefinitions.trim().isEmpty() : cteDefinitions;
		} else {
			assert cteCount > 0 :cteCount ;
			assert !cteDefinitions.trim().isEmpty() : cteDefinitions;
		}
		
		return toSQL(res.fst);
	}
	
	private String toSQL(STPlanNode n) throws Exception
	{
		StringBuffer sql = new StringBuffer();
		for(Iterator<STPlanNode> succs = plan.getPlanTree().getSuccNodes(n); succs.hasNext(); ) {
			STPlanNode child = succs.next();
			String childSQL = toSQL(child);
			if(childSQL.length()>0){
				if(sql.length()>0)sql.append(",\n");
				sql.append( childSQL );
			}
		}
		if(! wrapper.reuseSqlTemptable(n)){
			AbstractSQLTemplate thisSql = PlanNodeTemplateFactory.createSQLTemplate(n, plan, store, ctx, wrapper);
			// This check is for join nodes with left child pumping variables into the right one
			if(thisSql!=null){
				String newlyCreateSQLString = thisSql.createSQLString();
				if (newlyCreateSQLString!=null && newlyCreateSQLString.length()>0) {
					if(sql.length()>0)sql.append(",\n");
					sql.append(thisSql.createSQLString());	
				}
			}
		}
		return sql.toString();
	}


}
