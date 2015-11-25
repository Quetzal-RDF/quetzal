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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.research.rdf.store.Context;
import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.Store.Backend;
import com.ibm.research.rdf.store.config.Constants;
import com.ibm.research.rdf.store.runtime.service.types.TypeMap;
import com.ibm.research.rdf.store.sparql11.model.BinaryUnion;
import com.ibm.research.rdf.store.sparql11.model.BindPattern;
import com.ibm.research.rdf.store.sparql11.model.Constant;
import com.ibm.research.rdf.store.sparql11.model.ConstantExpression;
import com.ibm.research.rdf.store.sparql11.model.Expression;
import com.ibm.research.rdf.store.sparql11.model.Expression.ERelationalOp;
import com.ibm.research.rdf.store.sparql11.model.IRI;
import com.ibm.research.rdf.store.sparql11.model.OneOrMorePath;
import com.ibm.research.rdf.store.sparql11.model.Path;
import com.ibm.research.rdf.store.sparql11.model.Pattern.EPatternSetType;
import com.ibm.research.rdf.store.sparql11.model.PatternSet;
import com.ibm.research.rdf.store.sparql11.model.ProjectedVariable;
import com.ibm.research.rdf.store.sparql11.model.PropertyTerm;
import com.ibm.research.rdf.store.sparql11.model.Query;
import com.ibm.research.rdf.store.sparql11.model.QueryPrologue;
import com.ibm.research.rdf.store.sparql11.model.QueryTriple;
import com.ibm.research.rdf.store.sparql11.model.QueryTripleTerm;
import com.ibm.research.rdf.store.sparql11.model.RelationalExpression;
import com.ibm.research.rdf.store.sparql11.model.SelectClause;
import com.ibm.research.rdf.store.sparql11.model.SelectClause.ESelectModifier;
import com.ibm.research.rdf.store.sparql11.model.SelectQuery;
import com.ibm.research.rdf.store.sparql11.model.SimplePath;
import com.ibm.research.rdf.store.sparql11.model.SimplePattern;
import com.ibm.research.rdf.store.sparql11.model.UnaryPathOp;
import com.ibm.research.rdf.store.sparql11.model.Variable;
import com.ibm.research.rdf.store.sparql11.model.VariableExpression;
import com.ibm.research.rdf.store.sparql11.model.ZeroOrMorePath;
import com.ibm.research.rdf.store.sparql11.model.ZeroOrOnePath;
import com.ibm.research.rdf.store.sparql11.planner.AccessMethodType;
import com.ibm.research.rdf.store.sparql11.planner.Plan;
import com.ibm.research.rdf.store.sparql11.planner.PlanNode;
import com.ibm.research.rdf.store.sparql11.planner.PlanNodeType;
import com.ibm.research.rdf.store.sparql11.planner.QueryTripleNode;
import com.ibm.research.rdf.store.sparql11.planner.statistics.SPARQLOptimizerStatistics;
import com.ibm.research.rdf.store.sparql11.sqltemplate.SQLGenerator;
import com.ibm.research.rdf.store.sparql11.sqlwriter.SPARQLToSQLExpression;
import com.ibm.research.utils.OCUtils;
import com.ibm.wala.util.collections.HashMapFactory;
import com.ibm.wala.util.collections.HashSetFactory;
import com.ibm.wala.util.collections.Pair;
//TODO: change the computation of explicitIRIBoundVariables in the data model so that 
// in { select ?x ?y where {?x <p>* ?y} ?x is not added to  explicitIRIBoundVariables

//TODO: Perf improvement, remove the last join when access method is a poll
// by storing variables from predecessor in temp tables
public class CodeGenerator {
	 final static Logger   logger         = LoggerFactory.getLogger(CodeGenerator.class);

	//TODO : move to Constants
	public static final String PROC_REACHABLENODES = "REACHABLENODES";
	 
	protected Store store;
	protected SPARQLOptimizerStatistics stats;
	protected Context context;
	protected Plan plan;
	protected Query query;
	protected TemporaryTableMgr tmptableMgr;
	protected StoreProcedureManager procMgr;
	protected NewVariableGenerator newvargen;
	protected Set<Variable> explicitIRIBoundVariables;
	protected Map<Variable, Variable> variable2RenamedVariable;
	
	protected Set<PlanNode> liveTempTables;
	protected Set<PlanNode> nodesWithAllChildrenMaterialized;
	protected SPARQLToSQLExpression expGenerator;// = new SPARQLToSQLExpression();
	protected int cteCount = 0;
	protected CTEToNestedQueryConverter converter;
	/*public CodeGenerator( Store store, SPARQLOptimizerStatistics stats, Context context, Query query, TemporaryTableMgr tmptableMgr, StoreProcedureManager procMgr) {
		this(store,  stats, context, query, new Planner().plan(query, store, stats), tmptableMgr, procMgr, new NewVariableGenerator("var", 0));
	}*/
	public CodeGenerator( Store store, SPARQLOptimizerStatistics stats, Context context, Query query,Plan plan, TemporaryTableMgr tmptableMgr, StoreProcedureManager procMgr, int cteCount) {
		this( store,  stats, context, query, plan, tmptableMgr, procMgr, new NewVariableGenerator("var", 0), null, null, cteCount);
	}
	public CodeGenerator( Store store, SPARQLOptimizerStatistics stats, Context context, Query query, PlanNode materializedTableStartingPoint,
			TemporaryTableMgr tmptableMgr, StoreProcedureManager procMgr,  NewVariableGenerator newvargen, Set<Variable> explicitIRIBoundVariables, 
			Map<Variable, Variable> variable2RenamedVariable, int cteCount) {
		this( store,  stats, context, query, new PlannerWithMaterializedTableStartingPoint(materializedTableStartingPoint).plan(query, store, stats),
				tmptableMgr, procMgr, newvargen, explicitIRIBoundVariables, variable2RenamedVariable, cteCount);
		
	}
	public CodeGenerator( Store store, SPARQLOptimizerStatistics stats, Context context, Query query,Plan plan, TemporaryTableMgr tmptableMgr, 
			StoreProcedureManager procMgr,  NewVariableGenerator newvargen, Set<Variable> explicitIRIBoundVariables, Map<Variable, Variable> variable2RenamedVariable, int cteCount) {
		super();
		this.store = store;
		this.stats = stats;
		this.context = context;
		this.plan = plan;
		this.query = query;
		this.tmptableMgr = tmptableMgr;
		this.procMgr = procMgr;
		this.newvargen = newvargen;
		liveTempTables = HashSetFactory.make();
		nodesWithAllChildrenMaterialized= HashSetFactory.make();
		if (explicitIRIBoundVariables!=null) {
			this.explicitIRIBoundVariables = explicitIRIBoundVariables;
		} else {
			this.explicitIRIBoundVariables = HashSetFactory.make(query.getMainPattern().gatherIRIBoundVariables());
		}
		this.variable2RenamedVariable = variable2RenamedVariable;
		this.expGenerator = new SPARQLToSQLExpression();
		this.expGenerator.setStore(this.store);
		this.cteCount = cteCount;
		this.converter = new CTEToNestedQueryConverter(store.getStoreBackend());
		//planNode2ReferenceTempTables = HashMapFactory.make();
	}
	public static boolean isDB2Backend(Store store) {
		return store.getStoreBackend() == Backend.db2;
	}
	public boolean isDB2Backend() {
		return isDB2Backend(store);
	}
	public  static boolean isPostGresBackend(Store store) {
		return store.getStoreBackend() == Backend.postgresql;
	}
	public  boolean isPostGresBackend() {
		return isPostGresBackend(store);
	}
	/**
	 * returns a store procedure whose evaluation yields the result of the query.
	 * 
	 * @return
	 * @param dependentProcedures list of pairs consisting of a dependent store procedure and commands that must be executed before the declaration of the store procedure
	 * @param optionalResultTable optional result table. If it is not null, then the result of the store procedure will be store in it and no result sets are returned; 
	 * otherwise, the store procedure returns a single result set  
	 */
	public StoreProcedure toStoreProcedureSQL(List<Pair<StoreProcedure, List<SQLCommand>>> dependentProcedures, String optionalResultTable) {
		try {
			List<SQLCommand> cmds = new LinkedList<SQLCommand>();
			cmds.addAll(computeAllSQLCommand(dependentProcedures));
			SQLGenerator gen = new SQLGenerator(plan, query, store, context, cteCount);
			Pair<Pair<String, String>, Integer> pair = gen.toSQLDetailed(variable2RenamedVariable, explicitIRIBoundVariables);
			cteCount += pair.snd+1;
			//return gen.toSQL(variable2RenamedVariable);
			String sql =  gen.toSQL(pair.fst);
			if (optionalResultTable ==null) {
				String stmtVar;
				DeclareReturnCursor returnCursor = new DeclareReturnCursor(newvargen.createNewVariable(), stmtVar = newvargen.createNewVariable());
				cmds.add(0,returnCursor);
				cmds.add(new PrepareStatementCommand(stmtVar,sql));
				cmds.add(new OpenCursor(returnCursor.getCursorName()));
			} else {
				cmds.add(new InsertIntoTable(optionalResultTable, sql));
				
			}
			StoreProcedure ret = procMgr.createTemporaryStoreProcedure(optionalResultTable==null?1:0, cmds);	
			return ret;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public String toRecursiveSQL() {
		if (!isPostGresBackend()) {
			return null;
		}
		StringBuffer buf = new StringBuffer();
		Pair<List<CTEDefinition>, String>  pair =  toCTEDefinitionsMainSelectPair();
		List<CTEDefinition> cteDefinitions = pair.fst;
		String mainSelect = pair.snd;
		if (cteDefinitions.isEmpty()) {
			if (isRecursiveQueryOrHasZeroOrOne()) {
				Pair<String, List<SQLCommand>> p = tmptableMgr.getTemporaryTable(getTableSignature(new Variable("X")));
				String cteName = p.fst;
				CTEDefinition def = new CTEDefinition(cteName, mainSelect);
				buf.append("WITH RECURSIVE ");
				buf.append(def.toSQL());
				buf.append("SELECT * FROM ").append(cteName);
				return buf.toString();
			} else {
				return mainSelect;
			}
		} else {
			buf.append("WITH ");
			if (isRecursiveQueryOrHasZeroOrOne()) {
				buf.append("RECURSIVE ");
			}
			boolean first = true;
			for (CTEDefinition def : cteDefinitions) {
				if (first) {
					first = false;
				} else {
					buf.append(",\n");
				}
				buf.append(def.toSQL());
			}
			buf.append("\n");
			buf.append(mainSelect);
			return buf.toString();
		}
		
	}
	
	protected  List<CTEDefinition>  toCTEDefinitions(String mainCTEName) {
		if (!isPostGresBackend()) {
			return null;
		}
		Pair<List<CTEDefinition>, String>  pair =  toCTEDefinitionsMainSelectPair();
		List<CTEDefinition> ret = new LinkedList<CTEDefinition>();
		ret.addAll(pair.fst);
		ret.add(new CTEDefinition(mainCTEName, pair.snd));
		return ret;
	}
	
	protected  Pair<List<CTEDefinition>, String>  toCTEDefinitionsMainSelectPair() {
		if (!isPostGresBackend()) {
			return null;
		}
		try {
			List<SQLCommand> cmds = new LinkedList<SQLCommand>();
			cmds.addAll(computeAllSQLCommand(new LinkedList<Pair<StoreProcedure,List<SQLCommand>>>()));
			List<CTEDefinition> ret = new LinkedList<CTEDefinition>();
			for (SQLCommand cmd: cmds ) {
				assert cmd instanceof CTEDefinition: cmd;
				ret.add((CTEDefinition) cmd);
			}
			SQLGenerator gen = new SQLGenerator(plan, query, store, context, cteCount);
			Pair<Pair<String, String>, Integer> pair = gen.toSQLDetailed(variable2RenamedVariable, explicitIRIBoundVariables);
			String ctedefs = pair.fst.fst;
			String mainSelect = pair.fst.snd;
			cteCount += pair.snd+1;
			if (!ctedefs.trim().isEmpty()) {
				ret.add(new MultipleCTEDefintion(ctedefs));
			}
			return Pair.make(ret, mainSelect);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
	/**
	 * for a non recursive query, it returns the translation to SQL without using any store procedure. For recursive query, it returns  <code>null</code>.
	 * @return
	 */
	public String toSQLWithoutStoreProcedureSQL() {
		try {
			if (isRecursiveQueryOrHasZeroOrOne()) {
				return null;
			} else {
				SQLGenerator gen = new SQLGenerator(plan, query, store, context, cteCount);
				Pair<Pair<String, String>, Integer> pair = gen.toSQLDetailed(variable2RenamedVariable, explicitIRIBoundVariables);
				cteCount += pair.snd+1;
				//return gen.toSQL(variable2RenamedVariable);
				return gen.toSQL(pair.fst);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public boolean isRecursiveQueryOrHasZeroOrOne() {
		return Path.isRecursiveOrHasZeroOrOne(query);
	}
	public List<SQLCommand> releaseAllCreatedTemporaryStructures() {
		List<SQLCommand> ret = new LinkedList<SQLCommand>();
		ret.addAll(tmptableMgr.releaseAll());
		ret.addAll(procMgr.releaseAllTemporaryStoreProcedures());
		return ret;
	}
	/**
	 * compute all  sql commands that need to evaluate in the process of materializing some
	 * subset of the plan tree. This method also marks some nodes as being materialized.
	 * @return
	 */
	protected List<SQLCommand> computeAllSQLCommand(List<Pair<StoreProcedure, List<SQLCommand>>> dependentProcedures) {
		List<SQLCommand> cmds = new LinkedList<SQLCommand>();
		computeAllSQLCommand(plan.getPlanRoot(), cmds, dependentProcedures);
		return cmds;
	}
	
	protected void computeAllSQLCommand(PlanNode node, List<SQLCommand> cmds, List<Pair<StoreProcedure, List<SQLCommand>>> dependentProcedures) {
		switch (node.getType()) {
			case TRIPLE: {
				if (node.getTriple().getPredicate().isComplexPath()) {
					assert node.getTriple().getPredicate().getPath().isDirectlyRecursive() 
					|| node.getTriple().getPredicate().getPath().isDirectlyZeroOrOnePath()
					: "The following triple has not been properly normalized\n\t"+node.getTriple();
					//STPlanNode predecessorUp = node.getPredecessorUp(plan);
					PlanNode predecessor = null;
					if (!node.getRequiredVariables().isEmpty()) {
						predecessor = node.getPredecessor(plan);
						if (predecessor!=null) {
							if (!predecessor.getType().equals(PlanNodeType.MATERIALIZED_TABLE)) {
								// Materialized the predecessor
								Pair<String, List<SQLCommand>>  pair = tmptableMgr.getTemporaryTable(getTableSignature(predecessor));
								String predTempTable = pair.fst;
								cmds.addAll(pair.snd); // add the commands from the tmp table mgr before the use of the new tmp table
								
								if (isDB2Backend()) {
									// get the predecessor query
									String predSql = getSQL(predecessor); 
									cmds.add(new InsertIntoTable(predTempTable, predSql));
								} else {
									assert isPostGresBackend() : store.getStoreBackend();
									Pair<String, String> predSql = getSQLDetailed(predecessor);
									if (predSql.fst.trim().length()>0) {
										cmds.add(new MultipleCTEDefintion(predSql.fst));
									}
									cmds.add(new CTEDefinition(predTempTable, predSql.snd));
								}
								cmds.addAll(materialize(predecessor, predTempTable));
								cmds.addAll(releaseLeftSiblingsForANDOrPRODUCTParentNode(predecessor));
								//
							} 
						}
					}
					Pair<String, List<SQLCommand>>  pair  = evaluateTriplePathNode(predecessor, node, dependentProcedures);
					String outputTable = pair.fst;
					cmds.addAll(pair.snd); // add the commands from the tmp table mgr before the use of the new tmp table
					cmds.addAll(materialize(node, outputTable));
					cmds.addAll(releaseLeftSiblingsForANDOrPRODUCTParentNode(node));
				}
				break;
			}
			default : {
				boolean allChildrenMaterialized = true;
				boolean hasChildren = false;
				for (Iterator<PlanNode> ss= plan.getPlanTree().getSuccNodes(node); ss.hasNext(); ) {
					hasChildren = true;
					PlanNode child = ss.next();
					computeAllSQLCommand(child, cmds, dependentProcedures);
					if (allChildrenMaterialized &&
						(!child.getPredecessorDown(plan).getType().equals(PlanNodeType.MATERIALIZED_TABLE)
						 || nodesWithAllChildrenMaterialized.contains(child))) {
						allChildrenMaterialized = false;
					}
				}
				allChildrenMaterialized &= hasChildren;
				// all the children have been materialized, so we can release the left sibling temp tables for a AND or PRODUCT parent.
				if (allChildrenMaterialized ) {
					nodesWithAllChildrenMaterialized.add(node);
					cmds.addAll(releaseLeftSiblingsForANDOrPRODUCTParentNode(node));
				}
			}
			
		}
		
	}
	protected String getTableSignature( Variable... vars) {
		return getTableSignature(true, vars);
	}
	protected String getTableSignature(boolean withTypeInfo, Variable... vars) {
		assert vars!=null && vars.length>0;
		List<Variable> l = new LinkedList<Variable>();
		for (Variable v: vars) {
			l.add(v);
		}
		return getTableSignature(withTypeInfo, l);
	}
	protected String getTableSignature(PlanNode node) {
		return getTableSignature(node.getAvailableVariables());
	}
	protected String getTableSignature(Variable v) {
		return getTableSignature(Collections.singletonList(v));
	}
	protected String getTableSignature(Collection<Variable> vars) {
		return getTableSignature(true, vars);
	}
	protected String getTableSignature(boolean withTypeInfo, Collection<Variable> vars) {
		Set<Variable> iriVars= explicitIRIBoundVariables;
		StringBuffer buf = new StringBuffer();
		boolean first = true;
		for (Variable v : vars) {
			if (first) {
				first = false;
			} else {
				buf.append(", ");
			}
			buf.append(v.getName());
			if (withTypeInfo) {
				buf.append(" VARCHAR(100)");
			}
			if (!iriVars.contains(v)) {
				buf.append(", ").append(v.getName()).append(Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);
				if (withTypeInfo) {
					buf.append(" SMALLINT");
				}
			}
		}
		return buf.toString();
	}
	
	
	protected List<SQLCommand> materialize(PlanNode node, String tempTable) {
		assert !node.getType().equals(PlanNodeType.MATERIALIZED_TABLE): node;
		List<SQLCommand> cmds = releaseAllDescendantsOrSelf(node);
		node.materialize(tempTable);
		liveTempTables.add(node);
		return cmds;
	}
	/**
	 * release all temp tables in root or all descendants 
	 * @param root
	 * 
	 */
	protected List<SQLCommand> releaseAllDescendantsOrSelf(PlanNode root) {
		List<SQLCommand> ret = new LinkedList<SQLCommand>();
		if (root.getType().equals(PlanNodeType.MATERIALIZED_TABLE) ) {
			List<SQLCommand> cmds = release(root);
			if (cmds!=null) {
				ret.addAll(cmds);
			}
		} else {
			List<PlanNode> children = new LinkedList<PlanNode>();
			for (Iterator<PlanNode> ss= plan.getPlanTree().getSuccNodes(root); ss.hasNext(); ) {
				PlanNode child = ss.next();
				children.add(child);
				ret.addAll(releaseAllDescendantsOrSelf(child));
			}
			// remove children
			for (PlanNode child: children) {
				plan.getPlanTree().removeEdge(root, child);
			}
			//
		}
		return ret;
	}
	
	/**
	 * For a node n child of an AND or PRODUCT node, release all temp tables for left siblings of n.
	 * @param root
	 */
	protected List<SQLCommand> releaseLeftSiblingsForANDOrPRODUCTParentNode(PlanNode node) {
		List<SQLCommand> ret = new LinkedList<SQLCommand>();
		PlanNode parent = getParent(node);
		
		if (parent != null) {
			PlanNodeType parentType = parent.getType();
			if (parentType.equals(PlanNodeType.AND)) {
				do  {
					List<PlanNode> childrenToRemove = new LinkedList<PlanNode>();
					forLoop: for (Iterator<PlanNode> ss= plan.getPlanTree().getSuccNodes(parent); ss.hasNext(); ) {
						PlanNode child = ss.next();
						if (child!=node) {
							// left sibling of node
							ret.addAll(releaseAllDescendantsOrSelf(child));
							childrenToRemove.add(child);
						} else {
							break forLoop;
						}
					}
					// remove children
					for (PlanNode child: childrenToRemove) {
						plan.getPlanTree().removeEdge(parent, child);
					}
					node = parent;
					parent = getParent(node);
					
				} while (parent!=null && parent.getType().equals(parentType));
			}
	
		}
		return ret;
	}
	
	protected PlanNode getParent(PlanNode node) {
		Iterator<PlanNode> it = plan.getPlanTree().getPredNodes(node);
		if (it.hasNext()) {
			return it.next();
		} else {
			return null;
		}
	}
	/**
	 * releases the temp table for a given materialized node. It returns true if the materialized node was live, otherwise returns false
	 * @param materializedNode
	 * @return
	 */
	protected List<SQLCommand> release(PlanNode materializedNode) {
		List<SQLCommand> ret = new LinkedList<SQLCommand>();
		assert materializedNode.getType().equals(PlanNodeType.MATERIALIZED_TABLE): materializedNode;
		boolean removed = liveTempTables.remove(materializedNode);
		if (removed) {
			ret.addAll(tmptableMgr.release(materializedNode.getMaterialzedTable()));
		}
		return ret;
	}
	
	
	public static boolean isNotBoundVariableOrIsBoundVariableToMultipleConstants(QueryTripleTerm v,
			Map<Variable, Set<Constant>> filterBindings) {
		if (!v.isVariable()) {
			return false;
		}
		return isNotBoundVariableOrIsBoundVariableToMultipleConstants(v.getVariable(), filterBindings);
		
	}
	public static boolean isNotBoundVariableOrIsBoundVariableToMultipleConstants(Variable v,
			Map<Variable, Set<Constant>> filterBindings) {

		Set<Constant> consts = filterBindings.get(v);
		return consts == null || consts.size() >1;
		
	}
	
	protected boolean trackSubjectObjectPairs(PlanNode node, PlanNode predecessor) {
		boolean trackSubjectObjectPairs = true;
		QueryTriple t = node.getTriple();
		assert !AccessMethodType.isGraphAccess(node.getMethod().getType()) : node;
		//Set<Variable> intersectionAvailVarsInNodeAndPredecessor = HashSetFactory.make(node.getAvailableVariables());
		//intersectionAvailVarsInNodeAndPredecessor.retainAll(predecessor.getAvailableVariables());
		/*if (STEAccessMethodType.isGraphAccess(node.getMethod().getType())) {
			Set<Variable> availMinusGraphVar = HashSetFactory.make(node.getAvailableVariables());
			if (node.getTriple().getGraphRestriction()!=null
				&& node.getTriple().getGraphRestriction().isFirstType()) {
				availMinusGraphVar.remove(node.getTriple().getGraphRestriction().getFirst());
			}
			// the only available variable other than the graph restriction variable 
			// is either the subject or the object.
			if (availMinusGraphVar.size()<=1
			&& (availMinusGraphVar.contains(node.getTriple().getSubject().getVariable()) 
				|| availMinusGraphVar.contains(node.getTriple().getObject().getVariable()))) {
				trackSubjectObjectPairs = false;
			}
			//
		} else */
		{
			Set<Variable> availMinusGraphVar = HashSetFactory.make(node.getAvailableVariables());
			if (node.getTriple().getGraphRestriction()!=null
				&& node.getTriple().getGraphRestriction().isFirstType()) {
				availMinusGraphVar.remove(node.getTriple().getGraphRestriction().getFirst());
			}
			
			Variable resultVar = 				
				AccessMethodType.isDirectAccess(node.getMethod().getType())?
						t.getObject().getVariable(): t.getSubject().getVariable();
			Variable startingPoint = 				
							AccessMethodType.isDirectAccess(node.getMethod().getType())?
									t.getSubject().getVariable(): t.getObject().getVariable();
			if (!isNotBoundVariableOrIsBoundVariableToMultipleConstants(startingPoint, node.getPattern().getFilterBindings())) {
				trackSubjectObjectPairs = false;
			} else if (availMinusGraphVar.size()<=1
			&& availMinusGraphVar.contains(resultVar)){ 
				// the only available variable other than the graph restriction variable  is the result variable
				trackSubjectObjectPairs = false;
			} /*else if (availMinusGraphVar.size() == 2
			&& availMinusGraphVar.contains(node.getTriple().getSubject().getVariable())
			&& availMinusGraphVar.contains(node.getTriple().getObject().getVariable())
			&& !isNotBoundVariableOrIsBoundVariableToMultipleConstants(startingPoint, node.getPattern().getFilterBindings())) {
				// the only available variables other than the graph restriction variable  
				// are the subject and object variables and the starting point variable is bind to a constant
				trackSubjectObjectPairs = false;
			}*/
			
		}
		return trackSubjectObjectPairs;
	}
	protected Pair<String, List<SQLCommand>> evaluateTriplePathNode(PlanNode predecessor, PlanNode node, List<Pair<StoreProcedure, List<SQLCommand>>>  dependentProcedures){
		assert node.getType().equals(PlanNodeType.TRIPLE) && node.getTriple().getPredicate().isComplexPath(): node;
		assert node.getTriple().getPredicate().getPath().isDirectlyRecursive() ||
			node.getTriple().getPredicate().getPath().isDirectlyZeroOrOnePath()
			: "The following triple has not been properly normalized\n\t"+node.getTriple();
		assert predecessor==null || predecessor.getType().equals(PlanNodeType.MATERIALIZED_TABLE): predecessor.getType() + ": " + predecessor;
		
		// assume that constants have been replaced by variable with filter expressions
		assert node.getTriple().getSubject().isVariable() : "The following triple has not been properly normalized\n\t"+node.getTriple(); 
		assert node.getTriple().getObject().isVariable() : "The following triple has not been properly normalized\n\t"+node.getTriple(); 
		//
		assert !AccessMethodType.isGraphAccess(node.getMethod().getType()) : "Graph access not supported for property paths";
		// KAVITHA: If a triple has a variable in the object position, and it defines a recursive path, then the intermediate variables
		// that get bound to ?x do not need to be URIs.  
		//if (node.getTriple().getObject().isVariable()) {
		//	explicitIRIBoundVariables.remove(node.getTriple().getObject().getVariable());
		//}
		
		
		List<SQLCommand> ret = new LinkedList<SQLCommand>();
		boolean trackSubjectObjectPairs = trackSubjectObjectPairs(node, predecessor);
		QueryTriple t = node.getTriple();
		Variable graphRestrictionVariable = t.getGraphRestriction()!=null
					&& t.getGraphRestriction().isFirstType() 
					&& node.getAvailableVariables().contains(t.getGraphRestriction().getFirst())
					&& isNotBoundVariableOrIsBoundVariableToMultipleConstants(t.getGraphRestriction().getFirst(), node.getPattern().getFilterBindings())?
							t.getGraphRestriction().getFirst() : null;
		
		Path subPath = ((UnaryPathOp) node.getTriple().getPredicate().getPath()).getSubPath();
		
		
		
		if (!trackSubjectObjectPairs && !(subPath.isRecursive() || subPath.hasZeroOrOnePath()) ) {
			Pair<Pair<String, Variable>,  List<SQLCommand>> pair = computeReachableNodes(node, predecessor, t.getPredicate().getPath().isDirectlyZeroOrOnePath()?1:-1, trackSubjectObjectPairs);
			String resultTable = pair.fst.fst;
			ret.addAll(pair.snd);
			// additional constraints
			/*if ( (node.getAvailableVariables().contains(node.getTriple().getSubject().getVariable()) 
					^ node.getAvailableVariables().contains(node.getTriple().getObject().getVariable()) 
					&& !node.getAvailableVariables().contains(resultTable))) */
			//{
				/*Pair<String, List<SQLCommand>> p = addConstraints(resultTable, node, graphRestrictionVariable, pair.fst.snd, null, false);
				resultTable = p.fst;
				ret.addAll(p.snd);*/
			//}
			//
			
			// join with predecessor if needed
			Map<Variable, Constant> var2Const = null;
			assert !AccessMethodType.isGraphAccess(node.getMethod().getType()): "Graph access not allowed for property paths: "+node;
			Variable startingPoint = 				 
				AccessMethodType.isDirectAccess(node.getMethod().getType())?
						t.getSubject().getVariable(): t.getObject().getVariable();
			var2Const =  HashMapFactory.make();
			if (node.getAvailableVariables().contains(startingPoint) &&  !trackSubjectObjectPairs) {
				assert   node.getPattern().getFilterBindings().get(startingPoint)!=null && node.getPattern().getFilterBindings().get(startingPoint).size() ==1:
					node.getPattern().getFilterBindings().get(startingPoint)+"\n" +startingPoint+"\n"+node;
				var2Const.put(startingPoint, node.getPattern().getFilterBindings().get(startingPoint).iterator().next());
			}		
			
			if ( t.getGraphRestriction()!=null
				&& t.getGraphRestriction().isFirstType()
				&& node.getAvailableVariables().contains(t.getGraphRestriction().getFirst())
				&& !isNotBoundVariableOrIsBoundVariableToMultipleConstants(t.getGraphRestriction().getFirst(), node.getPattern().getFilterBindings())) {
				Set<Constant> constants = node.getPattern().getFilterBindings().get(t.getGraphRestriction().getFirst());
				assert constants!=null && constants.size() == 1 : node+"\n"+constants;
				Constant val = constants.iterator().next();
				var2Const.put(t.getGraphRestriction().getFirst(), val);				
			}
			Pair<String, List<SQLCommand>> p= joinWithPredecessorAndProcessFilters(node, resultTable, predecessor, var2Const);
			String finalResultTable = p.fst;
			ret.addAll(p.snd);
			//		
			return Pair.make(finalResultTable, ret);
		} else /*if (trackSubjectObjectPairs && !subPath.isRecursive())*/ {
			Variable[] tableVariables = graphRestrictionVariable ==null?
										new Variable[] {node.getTriple().getSubject().getVariable(), node.getTriple().getObject().getVariable()}:
										new Variable[] {graphRestrictionVariable, node.getTriple().getSubject().getVariable(), node.getTriple().getObject().getVariable() };
			// compute (startingPoint, resultPoint) 
			// A) If it is a simple recursion  (i.e., subPath is not recursive and not a ZeroOrOne)
			// 		1) start by computing all the nodes reachable from startingPoint
			// 		2) then evaluate the query "select ?X ?Y where {?X subPath ?Y. ?X in reachableFromStartingPoint}" or "select ?X ?Y where {?X subPath ?Y. ?Y in reachableFromStartingPoint}"
			// 		and store the result in the table tab(X, Y)
			// B) If it is a more complex recursion (i.e., subPath is recursive or a ZeroOrOne) or the property path of the node is a ZeroOrOne (?)
			//	    1)  evaluate the query "select ?X ?Y where {?X subPath ?Y}" and store the result in the table tab(X, Y)
			// 3) finally,  compute the transitive closure of the relation tab
			// 4) if '*' modifier, add (X, X) where X is a starting point
			// 5) Finally, perform natural join with predecessor table
			String tabTable;
			PlanNode newNode = node;
			if (node.getTriple().getPredicate().getPath() instanceof OneOrMorePath) {
				// replace '+' by '*' to include ancestor (or descendant) and self
				QueryTriple triple = new QueryTriple(node.getTriple().getSubject(),
						new PropertyTerm(new ZeroOrMorePath(subPath)), node.getTriple().getObject());
				triple.setGraphRestriction(node.getTriple().getGraphRestriction());
				
				newNode = new PlanNode(triple, 
							node.getMethod(), new com.ibm.research.rdf.store.schema.Pair<Set<Variable>>(node.getProducedVariables(), node.getRequiredVariables()), node.getPattern());
				if (node.getAvailableVariables()!=null) {
					newNode.setAvailableVariables(node.getAvailableVariables());
				}
			}
			if (!(subPath.isRecursive() || subPath.hasZeroOrOnePath())) {
				if ((predecessor!=null
				|| !AccessMethodType.isScanAccess(node.getMethod().getType())) 
				&&  !t.getPredicate().getPath().isDirectlyZeroOrOnePath() ) {
				//|| !QueryTripleNode.isNotBoundVariable(node.getTriple().getSubject(), node.getPattern().getFilterBindings())
				//|| !QueryTripleNode.isNotBoundVariable(node.getTriple().getObject(), node.getPattern().getFilterBindings())) {
					// Step 1: 
					Variable resultVariable, startingPoint;
					if (AccessMethodType.isDirectAccess(node.getMethod().getType())) {
						resultVariable = t.getObject().getVariable();
						startingPoint = t.getSubject().getVariable();
					} else {
						resultVariable = t.getSubject().getVariable();
						startingPoint = t.getObject().getVariable();
					}
					Set<Variable> oldExplicitIRIBoundVariables = explicitIRIBoundVariables;
					if (explicitIRIBoundVariables.contains(resultVariable) && !explicitIRIBoundVariables.contains(startingPoint)) {
						explicitIRIBoundVariables = HashSetFactory.make(explicitIRIBoundVariables);
						explicitIRIBoundVariables.remove(resultVariable);
					}
					Pair<Pair<String, Variable>, List<SQLCommand>> p = computeReachableNodes(newNode, predecessor, t.getPredicate().getPath().isDirectlyZeroOrOnePath()?1:-1, false);
					explicitIRIBoundVariables = oldExplicitIRIBoundVariables;
					String reachableTable = p.fst.fst;
					Variable reachableVariable = p.fst.snd;
					Variable startingPointVariable;
					if (node.getTriple().getSubject().getVariable().equals(reachableVariable)) {
						startingPointVariable = node.getTriple().getObject().getVariable();
					} else {
						assert node.getTriple().getObject().getVariable().equals(reachableVariable) : node+"\n" +reachableVariable;
						startingPointVariable = node.getTriple().getSubject().getVariable();
					}
					ret.addAll(p.snd);
					//
					//Step 2:
					
					//swap startingPointVariable and reachableVariable
					QueryTriple triple = new QueryTriple(
							new QueryTripleTerm(node.getTriple().getSubject().getVariable().equals(startingPointVariable)? reachableVariable : startingPointVariable),
								node.getTriple().getPredicate(),
							new QueryTripleTerm(node.getTriple().getObject().getVariable().equals(startingPointVariable)? reachableVariable : startingPointVariable));
					Set<Variable> newExplicitIRIBoundVariables = HashSetFactory.make(explicitIRIBoundVariables);
					if (explicitIRIBoundVariables.contains(startingPointVariable)  && explicitIRIBoundVariables.contains(reachableVariable)) {
						//newExplicitIRIBoundVariables.add(newResultVar);
						// do nothing
					} else if (explicitIRIBoundVariables.contains(startingPointVariable)) {
						newExplicitIRIBoundVariables.add(reachableVariable);
						newExplicitIRIBoundVariables.remove(startingPointVariable);
						
					} else if (explicitIRIBoundVariables.contains(reachableVariable) ) {
						newExplicitIRIBoundVariables.add(startingPointVariable);
						newExplicitIRIBoundVariables.remove(reachableVariable);
					}
					Variable[] reverseTableVariables = graphRestrictionVariable ==null?
							new Variable[] { node.getTriple().getObject().getVariable(), node.getTriple().getSubject().getVariable()}:
							new Variable[] {graphRestrictionVariable,  node.getTriple().getObject().getVariable(), node.getTriple().getSubject().getVariable() };
					Query subQuery =getSubQuery(triple, node.getTriple().getGraphRestriction(), node, false, newExplicitIRIBoundVariables, reverseTableVariables);
								
					//newExplicitIRIBoundVariables.addAll(subQuery.getMainPattern().gatherIRIBoundVariables());
					Map<Variable, Variable> newVariable2RenamedVariable = variable2RenamedVariable==null? HashMapFactory.<Variable, Variable>make() : HashMapFactory.<Variable, Variable>make(variable2RenamedVariable);
					newVariable2RenamedVariable.put(startingPointVariable, reachableVariable);
					newVariable2RenamedVariable.put(reachableVariable, startingPointVariable);
					//
					
					PlanNode reachablePlanNode = new PlanNode(reachableTable, Collections.singleton(reachableVariable));
				
					/*Query subQuery = getSubQuery(node, false, tableVariables);
					Set<Variable> newExplicitIRIBoundVariables = HashSetFactory.make(explicitIRIBoundVariables);*/
					//newExplicitIRIBoundVariables.addAll(subQuery.getMainPattern().gatherIRIBoundVariables());
					CodeGenerator newcodegen = new CodeGenerator( store, stats, context, subQuery, reachablePlanNode, tmptableMgr, procMgr, newvargen, newExplicitIRIBoundVariables,newVariable2RenamedVariable, cteCount);
					assert !newcodegen.isRecursiveQueryOrHasZeroOrOne();
					
					Pair<String, List<SQLCommand>> pair = tmptableMgr.getTemporaryTable(getTableSignature(tableVariables));
					tabTable= pair.fst;
					ret.addAll(pair.snd);
					if (isDB2Backend()) {
						String tabQuery = newcodegen.toSQLWithoutStoreProcedureSQL();
						InsertIntoTable ins = new InsertIntoTable(tabTable, tabQuery);	
						ret.add(ins);
					} else {
						assert isPostGresBackend(): store.getStoreBackend();
						Pair<List<CTEDefinition>, String> res = newcodegen.toCTEDefinitionsMainSelectPair();
						ret.addAll(res.fst);
						ret.add(new CTEDefinition(tabTable, res.snd));
					}
					ret.addAll(tmptableMgr.release(reachableTable));
					cteCount = newcodegen.cteCount;
				} else {
					Query subQuery = getSubQuery(node, false, tableVariables);
					Set<Variable> newExplicitIRIBoundVariables = HashSetFactory.make(explicitIRIBoundVariables);
					//newExplicitIRIBoundVariables.addAll(subQuery.getMainPattern().gatherIRIBoundVariables());
					CodeGenerator newcodegen = new CodeGenerator( store, stats, context, subQuery, (PlanNode) predecessor, tmptableMgr, procMgr, newvargen, newExplicitIRIBoundVariables,variable2RenamedVariable, cteCount);
					assert !newcodegen.isRecursiveQueryOrHasZeroOrOne();
					Pair<String, List<SQLCommand>> pair = tmptableMgr.getTemporaryTable(getTableSignature( tableVariables));
					tabTable= pair.fst;
					ret.addAll(pair.snd);
					if (isDB2Backend()) {
						String tabQuery = newcodegen.toSQLWithoutStoreProcedureSQL();
						InsertIntoTable ins = new InsertIntoTable(tabTable, tabQuery);
						ret.add(ins);
					} else {
						assert isPostGresBackend(): store.getStoreBackend();
						Pair<List<CTEDefinition>, String> res = newcodegen.toCTEDefinitionsMainSelectPair();
						ret.addAll(res.fst);
						ret.add(new CTEDefinition(tabTable, res.snd));
					}
					cteCount = newcodegen.cteCount;
				}
			} else {
				//TODO: more efficient implementation based on magic set transformation
				Query subQuery = getSubQuery(node, false, tableVariables);
				Set<Variable> newExplicitIRIBoundVariables = HashSetFactory.make(explicitIRIBoundVariables);
				//newExplicitIRIBoundVariables.addAll(subQuery.getMainPattern().gatherIRIBoundVariables());
				CodeGenerator newcodegen = new CodeGenerator( store, stats, context, subQuery, (PlanNode) null, tmptableMgr, procMgr, newvargen, newExplicitIRIBoundVariables,variable2RenamedVariable, cteCount);
				assert newcodegen.isRecursiveQueryOrHasZeroOrOne();
				Pair<String, List<SQLCommand>> pair = tmptableMgr.getTemporaryTable(getTableSignature(tableVariables));
				tabTable= pair.fst;
				//ret.addAll(pair.snd);
				if (isDB2Backend()) {
					StoreProcedure proc = newcodegen.toStoreProcedureSQL(dependentProcedures, tabTable);
					dependentProcedures.add(Pair.make(proc, pair.snd));
					ret.add(new StoreProcedureInvocation(proc));
				} else {
					assert isPostGresBackend(): store.getStoreBackend();
					List<CTEDefinition> cteDefs = newcodegen.toCTEDefinitions(tabTable);
					ret.addAll(cteDefs);
				}
				cteCount = newcodegen.cteCount;
				/*String tabQuery = proc.getSqlInvocatiionCode();
				InsertIntoTable ins = new InsertIntoTable(tabTable, tabQuery);
				ret.add(ins);*/
				//ret.add(new DropStoreProcedure(proc.getName()));
				//ret.addAll(tmptableMgr.release(temptable))
				
				//
			}
				//
			//Step 3:
			//boolean rightRecursion = STEAccessMethodType.isDirectAccess(node.getMethod().getType());
			String transitiveClosureTable;
			Pair<String, List<SQLCommand>> pair;
			if (t.getPredicate().getPath().isDirectlyZeroOrOnePath()) {
				transitiveClosureTable = tabTable;
			} else {
			
				pair = computeTransitiveClosure(tabTable, graphRestrictionVariable,  node.getTriple().getSubject().getVariable(), node.getTriple().getObject().getVariable());
				transitiveClosureTable = pair.fst;
				ret.addAll(pair.snd);
			}
			//
			
			Variable x = node.getTriple().getSubject().getVariable();
			Variable y = node.getTriple().getObject().getVariable();
			//Step 4: 
			if (node.getTriple().getPredicate().getPath() instanceof ZeroOrMorePath || node.getTriple().getPredicate().getPath().isDirectlyZeroOrOnePath() ) {
				String startingPointTable;
				Variable resultVariable ;
				
				if (!( subPath.isRecursive() || subPath.hasZeroOrOnePath())) {
					Pair<Pair<String, Variable>, List<SQLCommand>> p = computeReachableNodes(newNode, predecessor,0,false);
					startingPointTable = p.fst.fst;
					resultVariable = p.fst.snd;
					ret.addAll(p.snd);
				} else {
					// create sub rdf:type* obj  and get only the initialization part
					Set<IRI> props = Path.getProperties(node.getTriple().getPredicate().getPath());
					assert props.size()>0;
					IRI prop = props.iterator().next();
					QueryTriple triple = new QueryTriple(node.getTriple().getSubject(),
							new PropertyTerm(new ZeroOrMorePath(new SimplePath(prop))), node.getTriple().getObject());
					triple.setGraphRestriction(node.getTriple().getGraphRestriction());
					PlanNode newNode2 = new PlanNode(triple, 
								node.getMethod(), new com.ibm.research.rdf.store.schema.Pair<Set<Variable>>(node.getProducedVariables(), node.getRequiredVariables()), node.getPattern());
					if (node.getAvailableVariables()!=null) {
						newNode2.setAvailableVariables(node.getAvailableVariables());
					}
					//
					Pair<Pair<String, Variable>, List<SQLCommand>> p = computeReachableNodes(newNode2, predecessor,0, false);
					startingPointTable = p.fst.fst;
					resultVariable = p.fst.snd;
					ret.addAll(p.snd);
				}
				
				//  "INSERT INTO transitiveClosure SELECT X, X FROM "+startingPointTable+ " EXCEPT (SELECT * FROM "+transitiveClosureTable+" )"; 
				//project the right variables including type vars sqlIdenticalSubjectObject
				StringBuffer sqlIdenticalSubjectObject= new StringBuffer();
				boolean xType = !explicitIRIBoundVariables.contains(x);
				boolean yType = !explicitIRIBoundVariables.contains(y);
				boolean resultVarType = !explicitIRIBoundVariables.contains(resultVariable);
				sqlIdenticalSubjectObject.append("SELECT ");
				if (graphRestrictionVariable!=null) {
					sqlIdenticalSubjectObject.append(graphRestrictionVariable.getName()).append(" AS ").append(graphRestrictionVariable.getName()).append(", ");
				}
				sqlIdenticalSubjectObject.append(resultVariable.getName()).append(" AS ").append(x.getName());
				if (xType) {
					// add x type
					if (resultVarType) {
						sqlIdenticalSubjectObject.append(", ").append(resultVariable.getName()).append(Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS)
							.append(" AS ").append(x.getName()).append(Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);
					} else {
						sqlIdenticalSubjectObject.append(", ").append(TypeMap.IRI_ID)
							.append(" AS ").append(x.getName()).append(Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);
					}
				}
				sqlIdenticalSubjectObject.append(", ").append(resultVariable.getName()).append(" AS ").append(y.getName()).append(" ");
				if (yType) {
					// add y type
					if (resultVarType) {
						sqlIdenticalSubjectObject.append(", ").append(resultVariable.getName()).append(Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS)
							.append(" AS ").append(y.getName()).append(Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);
					} else {
						sqlIdenticalSubjectObject.append(", ").append(TypeMap.IRI_ID)
						.append(" AS ").append(y.getName()).append(Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);
					}
				}
				sqlIdenticalSubjectObject.append(" FROM ").append(startingPointTable).append("\n");								
				if (isDB2Backend()) {
					//
					sqlIdenticalSubjectObject.append("EXCEPT (SELECT * FROM "+transitiveClosureTable+" )"); 
					ret.add(new InsertIntoTable(transitiveClosureTable, sqlIdenticalSubjectObject.toString()));
				} else {
					assert isPostGresBackend(): store.getStoreBackend();
					pair = tmptableMgr.getTemporaryTable(getTableSignature(tableVariables));
					String newTransitiveClosureTable = pair.fst;
					ret.addAll(pair.snd);
					String sql = "SELECT * FROM "+transitiveClosureTable
								+"\n UNION \n"
								+sqlIdenticalSubjectObject.toString(); 
					ret.add(new CTEDefinition(newTransitiveClosureTable, sql));
					ret.addAll(tmptableMgr.release(transitiveClosureTable));
					transitiveClosureTable = newTransitiveClosureTable;				
				}
				if (!startingPointTable.equals(transitiveClosureTable)) {
					ret.addAll(tmptableMgr.release(startingPointTable));
					if (startingPointTable.equals(tabTable)) {
						tabTable = null;
					}
				}
			}
			//
			if (tabTable!=null && !tabTable.equals(transitiveClosureTable)) {
				ret.addAll(tmptableMgr.release(tabTable));
				tabTable = null;
			}
			
			
			// additional constraints
			/*pair = addConstraints(transitiveClosureTable, node, graphRestrictionVariable, x, y, subPath.isRecursive());
			transitiveClosureTable = pair.fst;
			ret.addAll(pair.snd);*/
			//
			
			//Step 5
			Map<Variable, Constant> var2Const = HashMapFactory.make();
			if ( t.getGraphRestriction()!=null
					&& t.getGraphRestriction().isFirstType()
					&& node.getAvailableVariables().contains(t.getGraphRestriction().getFirst())
					&& !isNotBoundVariableOrIsBoundVariableToMultipleConstants(t.getGraphRestriction().getFirst(), node.getPattern().getFilterBindings())) {
					Set<Constant> constants = node.getPattern().getFilterBindings().get(t.getGraphRestriction().getFirst());
					assert constants!=null && constants.size() == 1 : node+"\n"+constants;
					Constant val = constants.iterator().next();
					var2Const.put(t.getGraphRestriction().getFirst(), val);				
			}
			pair = joinWithPredecessorAndProcessFilters(node, transitiveClosureTable, predecessor, var2Const);
			String finalResultTable = pair.fst;
			ret.addAll(pair.snd);
			//
			return Pair.make(finalResultTable, ret);
			
		} /*else {
			assert subPath.isRecursive();
			// more complex recursion: nested recursive operator
		}*/
			
					
	}
	
	
	// this also produces graph restriction  variable if needed
	// 
	private Pair<String, List<SQLCommand>> joinWithPredecessorAndProcessFilters(PlanNode node, String table,  PlanNode predecessor, Map<Variable, Constant> var2Constant) {
		List<SQLCommand> cmds = new LinkedList<SQLCommand>();
		List<Expression> exps = node.getApplicableFilters(plan);
		if (predecessor == null && (var2Constant==null || var2Constant.isEmpty()) && (exps ==null || exps.isEmpty())
		&& (node.getTriple().getGraphRestriction() ==null || !node.getTriple().getGraphRestriction().isFirstType())) {
			return Pair.make(table, Collections.<SQLCommand>emptyList());
		}  
		Set<Variable> livePredAvailVars = null;
		if (predecessor!=null ) {
			livePredAvailVars = HashSetFactory.make(predecessor.getAvailableVariables());
			livePredAvailVars.retainAll(node.getAvailableVariables());
		}
		if ( (node.getTriple().getGraphRestriction() ==null || !node.getTriple().getGraphRestriction().isFirstType())
			&& livePredAvailVars!=null
			&& livePredAvailVars.size() <=0
			//&& STEAccessMethodType.isPollAccess(node.getMethod().getType()) 
			//&& (var2Constant==null || var2Constant.isEmpty())
			)  {
			Map<String, Pair<String, String>> varMap = HashMapFactory.make();
			for (Variable v: node.getAvailableVariables()) {
				if (explicitIRIBoundVariables.contains(v)) {
					varMap.put(v.getName(), Pair.make(table+"."+v.getName(), (String)null));
				}  else {
					varMap.put(v.getName(), Pair.make(table+"."+v.getName(),table+"."+v.getName()+Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS));
				}
			}
			
			Variable startingPoint = AccessMethodType.isReverseAccess(node.getMethod().getType())? 
					node.getTriple().getObject().getVariable(): node.getTriple().getSubject().getVariable();
			//Variable endingPoint = STEAccessMethodType.isReverseAccess(node.getMethod().getType())? 
			//		 node.getTriple().getSubject().getVariable() : node.getTriple().getObject().getVariable();
			/*if (livePredAvailVars.size()  == 1) {
				if (livePredAvailVars.iterator().next().equals(startingPoint)) {
					//: livePredAvailVars+"\n"+node;
					Pair<String, List<SQLCommand>> ret = (exps ==null || exps.isEmpty())? 
								Pair.make(table, Collections.<SQLCommand>emptyList()) :
								addConstraints(table, node, null, null, null, false, exps, varMap);
					if (var2Constant==null || var2Constant.isEmpty()) {
						return  ret;
					} else {
						table = ret.fst;
						cmds.addAll(ret.snd);
						predecessor = null; // no need to join with predecessor. Just project variables bound to single constant
					}
								
				}
			} else  */ 
			{
				Pair<String, List<SQLCommand>> ret =  (exps ==null || exps.isEmpty())? 
						Pair.make(table, Collections.<SQLCommand>emptyList()) :
						addConstraints(table, node, null, null, null, false, exps, varMap);
				if (var2Constant==null || var2Constant.isEmpty()) {
					return  ret;
				} else {
					table = ret.fst;
					cmds.addAll(ret.snd);
					predecessor = null; // No need to join with predecessor. Just project variables bound to single constant
				}
			} 
		}
		//  there are variables corresponding to constants or/and there is a need to join with the predecessor
		Pair<String, List<SQLCommand>> pair =  tmptableMgr.getTemporaryTable(getTableSignature(node));
		String finalResultTable = pair.fst;
		cmds.addAll(pair.snd);
		InsertIntoTableFromJoinWithPredecessorAndProcessFilters cmd = new InsertIntoTableFromJoinWithPredecessorAndProcessFilters(finalResultTable, node, table, predecessor, var2Constant, explicitIRIBoundVariables, exps, store);
		if (isDB2Backend()) {
			cmds.add(cmd);
		} else {
			assert isPostGresBackend() : store.getStoreBackend();
			cmds.add(new CTEDefinition(finalResultTable, cmd.getSQLWithoutInsert()));
		}
		cmds.addAll(tmptableMgr.release(table));
		return Pair.make(finalResultTable, cmds);
		
	}
	private static boolean isNotBoundVariable(Variable v,
			Map<Variable, Set<Constant>> filterBindings) {
		return !filterBindings.containsKey(v);
	}
	
	private Pair<String, List<SQLCommand>> addConstraints(String table,  PlanNode node, Variable graphRestriction,  Variable x, Variable y, 
			boolean addAllConstraintsRegardlessOfAccessMethod) {
		return addConstraints(table, node, graphRestriction, x, y, addAllConstraintsRegardlessOfAccessMethod, null, null);
	}
	// add constant constraints on subject or object and subject = object constraint 
	private Pair<String, List<SQLCommand>> addConstraints(String table,  PlanNode node, Variable graphRestriction,  Variable x, Variable y, 
			boolean addAllConstraintsRegardlessOfAccessMethod,
			List<Expression> exps, Map<String, Pair<String, String>> varMap) {
		assert node.getType().equals(PlanNodeType.TRIPLE) : node;
		QueryTriple t = node.getTriple();
		// additional constraints
		Pair<Variable, Set<Constant>> xConstants = null;
		Pair<Variable, Set<Constant>> yConstants = null;
		Pair<Variable, Set<Constant>> graphRestrictionConstants = null;
		boolean xEqualsY = false;
		if (!QueryTripleNode.isNotBoundVariable(t.getSubject(), node.getPattern().getFilterBindings())
		&& ( addAllConstraintsRegardlessOfAccessMethod || !node.getMethod().getType().equals(AccessMethodType.DPH_INDEX_SUBJECT)) ) {
		//&& (STEAccessMethodType.isReverseAccess(node.getMethod().getType()) ||  STEAccessMethodType.isScanAccess(node.getMethod().getType()) ) ) {
			// subject has some constant bindings
			// and was not the starting point of the exploration or the access method was a scan
			// Restrict subjectVariable to its constant bindings.
			Set<Constant> constants = node.getPattern().getFilterBindings().get(t.getSubject().getVariable());
			assert constants != null && constants.size()>0 : node+"\n"+ node.getPattern().getFilterBindings();
			//Variable sub = t.getSubject().getVariable().equals(x)? x: t.getSubject().getVariable().equals(y)? y : null;
			if ( x!=null && t.getSubject().getVariable().equals(x) ) {
				xConstants = Pair.make(x, constants);
			} else if (y!=null &&t.getSubject().getVariable().equals(y) ) {
				yConstants = Pair.make(y,constants);
			} 
		} 
		
		if (x!=null && y!=null && !x.equals(y)) {
			if (!QueryTripleNode.isNotBoundVariable(t.getObject(), node.getPattern().getFilterBindings())
			&&  ( addAllConstraintsRegardlessOfAccessMethod || !node.getMethod().getType().equals(AccessMethodType.RPH_INDEX_OBJECT))) {
			//&& (STEAccessMethodType.isDirectAccess(node.getMethod().getType()) ||  STEAccessMethodType.isScanAccess(node.getMethod().getType()) ) ) {
				// object has some constant bindings
				// and was not the starting point of the exploration or the access method was a scan
				// Restrict objectVariable to its constant bindings.
				Set<Constant> constants = node.getPattern().getFilterBindings().get(t.getObject().getVariable());
				assert constants != null && constants.size()>0 : node+"\n"+ node.getPattern().getFilterBindings();
				if (x!=null && t.getObject().getVariable().equals(x)) {
					assert xConstants ==null : x+"\n"+xConstants;
					xConstants = Pair.make(x, constants);
					
				} else if (y!=null & t.getObject().getVariable().equals(y)) {
					assert yConstants == null : y+"\n"+yConstants;
					yConstants = Pair.make(y, constants);
				}
			}
		} else if (x!=null && y!=null) {
			xEqualsY = true;
		}
		
		if (graphRestriction!=null) {
			if (!isNotBoundVariable(graphRestriction, node.getPattern().getFilterBindings())
			/*&& !node.getMethod().getType().equals(STEAccessMethodType.DPH_INDEX_GRAPH)
			&& !node.getMethod().getType().equals(STEAccessMethodType.RPH_INDEX_GRAPH)*/) {
				Set<Constant> constants = node.getPattern().getFilterBindings().get(graphRestriction);
				assert constants != null && constants.size()>0 : node+"\n"+ node.getPattern().getFilterBindings();
				graphRestrictionConstants = Pair.make(graphRestriction, constants);
			}
		}
		
		if (xConstants==null && yConstants==null && graphRestrictionConstants ==null && !xEqualsY ) {
			return Pair.make(table, Collections.<SQLCommand>emptyList());
		} else {
			String resultTable;
			List<SQLCommand> cmds = new LinkedList<SQLCommand>();
			List<Variable> tableVariables = new LinkedList<Variable>();
			if (graphRestriction!=null) {
				tableVariables.add(graphRestriction);
			}
			if (x!=null) {
				tableVariables.add(x);
			}
			if (y!=null) {
				tableVariables.add(y);
			}
			if (x==null && y == null) {
				throw new RuntimeException("At least one of x or y must be non-null");
			}
			
				
			Pair<String, List<SQLCommand>> p = tmptableMgr.getTemporaryTable(getTableSignature(tableVariables));
			resultTable = p.fst;
			cmds.addAll(p.snd);
			
			List<Pair<Variable, Set<Constant>>> variableConstantConstraints = new LinkedList<Pair<Variable,Set<Constant>>>();
			if (xConstants!=null) {
				variableConstantConstraints.add(xConstants);
			}
			if (yConstants!=null) {
				variableConstantConstraints.add(yConstants);
			}
			if (graphRestrictionConstants!=null) {
				variableConstantConstraints.add(graphRestrictionConstants);
			}
			List<Pair<Variable, Variable>> variableEqualityConstraints = new LinkedList<Pair<Variable,Variable>>();
			if (xEqualsY) {
				variableEqualityConstraints.add(Pair.make(x, y));
			}
			InsertIntoTableFromTableWithConstraints cmd = new InsertIntoTableFromTableWithConstraints(resultTable, table, variableConstantConstraints, variableEqualityConstraints, 
					explicitIRIBoundVariables, exps, varMap, store);
			if (isDB2Backend()) {
				cmds.add(cmd);
			} else {
				assert isPostGresBackend() : store.getStoreBackend();
				cmds.add(new CTEDefinition(resultTable, cmd.getSQLWithoutInsert()));
			}
			cmds.addAll(tmptableMgr.release(table));
			return Pair.make(resultTable,cmds);
		}
		//
	}
	// relationTable(X, Y)
	//      result(X, Y) := relation(X, Y)
	//	    result(X, Y) :- relation(X, Z) & result(Z, Y)
	
	private Pair<String, List<SQLCommand>> computeTransitiveClosure(String relationTable, Variable graphRestriction, Variable x, Variable y) {
		List<SQLCommand> cmds = new LinkedList<SQLCommand>();
		Variable[] tableVariables = graphRestriction == null? 
									new Variable[] {x, y} : new Variable[] {graphRestriction, x, y};
		// create result, delta and old delta table
		Pair<String, List<SQLCommand>> pair = tmptableMgr.getTemporaryTable(getTableSignature(tableVariables));
		String resultTable = pair.fst;
		cmds.addAll(pair.snd);
		String deltaTable;
		if (isDB2Backend()) {
			pair = tmptableMgr.getTemporaryTable(getTableSignature(tableVariables));
			deltaTable = pair.fst;
			cmds.addAll(pair.snd);
		} else {
			assert isPostGresBackend(): store.getStoreBackend();
			deltaTable = resultTable;
		}
		String oldDeltaTable;
		if (isDB2Backend()) {
			pair = tmptableMgr.getTemporaryTable(getTableSignature(tableVariables));
			oldDeltaTable = pair.fst;
			cmds.addAll(pair.snd);
		} else {
			assert isPostGresBackend(): store.getStoreBackend();
			oldDeltaTable = resultTable;
		}
		
		//
		
		//initialization
		InsertCopyOfATableIntoTable initializationCMD = new InsertCopyOfATableIntoTable(deltaTable, relationTable);
		// select  [rel.graphRest as graphRest, ] rel.x as x [, rel.x_typ as x_typ], res.y as y [, res.y_typ as y_typ] from relationTable as rel, oldDeltaTable as res
		// where [rel.graphRest = res.graphRest  AND ] rel.y = res.x [AND rel.y_typ = res.x_typ] 
		StringBuffer loopFromOldDeltaToDeltaSQL = new StringBuffer();
		
		
		boolean xType = !explicitIRIBoundVariables.contains(x);
		boolean yType = !explicitIRIBoundVariables.contains(y);
		loopFromOldDeltaToDeltaSQL.append("SELECT ");
		if (graphRestriction!=null) {
			loopFromOldDeltaToDeltaSQL.append("REL.").append(graphRestriction.getName()).append(" AS ").append(graphRestriction.getName()).append(", ");
		}
		loopFromOldDeltaToDeltaSQL.append("REL.").append(x.getName()).append(" AS ").append(x.getName());
		if (xType) {
			// add x type
			loopFromOldDeltaToDeltaSQL.append(", REL.").append(x.getName()).append(Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS)
				.append(" AS ").append(x.getName()).append(Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);
		}
		loopFromOldDeltaToDeltaSQL.append(", RES.").append(y.getName()).append(" AS ").append(y.getName()).append(" ");
		if (yType) {
			// add y type
			loopFromOldDeltaToDeltaSQL.append(", RES.").append(y.getName()).append(Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS)
				.append(" AS ").append(y.getName()).append(Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);
		}
		loopFromOldDeltaToDeltaSQL.append(" FROM ").append(relationTable).append(" AS REL, ").append(oldDeltaTable).append(" AS RES\n");
		loopFromOldDeltaToDeltaSQL.append(" WHERE ");
		if (graphRestriction!=null) {
			loopFromOldDeltaToDeltaSQL.append("REL.").append(graphRestriction.getName()).append("=").append("RES.").append(graphRestriction.getName()).append(" AND ");
		}
		loopFromOldDeltaToDeltaSQL.append("REL.").append(y.getName()).append("=").append("RES.").append(x.getName()).append(" "); // rel.y = res.x
		if (xType && yType) {
			loopFromOldDeltaToDeltaSQL.append(" AND REL.").append(y.getName()).append(Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS).append("=").append("RES.").append(x.getName()).append(Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);
					//rel.y_typ = res.x_type
		} else if (yType) {
			loopFromOldDeltaToDeltaSQL.append(" AND REL.").append(y.getName()).append(Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS).append("=").append(TypeMap.IRI_ID);
					//rel.y_typ = IRI
		} else if (xType) {
			loopFromOldDeltaToDeltaSQL.append(" AND RES.").append(x.getName()).append(Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS).append("=").append(TypeMap.IRI_ID);
					//res.x_type = IRI
		} 
		
		loopFromOldDeltaToDeltaSQL.append("\n");
		if (isDB2Backend()) {
			//
			String loopSTMTFromOldDetaToDelta = new InsertIntoTable(deltaTable,
											 loopFromOldDeltaToDeltaSQL.toString()+
											"\n EXCEPT (SELECT * FROM "+ resultTable).toSQL()+")";
			String loopSTMTFromDetaToOldDelta =  new InsertIntoTable(oldDeltaTable,
										 loopFromOldDeltaToDeltaSQL.toString().replace(oldDeltaTable, deltaTable)
										+"\n EXCEPT (SELECT * FROM "+ resultTable).toSQL()+")";
			StoreProcedureInvocation procCall = new StoreProcedureInvocation(PROC_REACHABLENODES, resultTable, deltaTable, oldDeltaTable,
					initializationCMD.toSQL(), loopSTMTFromOldDetaToDelta,loopSTMTFromDetaToOldDelta, -1);
			cmds.add(procCall);
			cmds.addAll(tmptableMgr.release(deltaTable));
			cmds.addAll(tmptableMgr.release(oldDeltaTable));
		} else {
			assert isPostGresBackend(): store.getStoreBackend();
			RecursiveCTEDefinition cteDef = new RecursiveCTEDefinition(resultTable, getTableSignature(false, tableVariables),
													converter.transform(initializationCMD.getSQLWithoutInsert()),
													converter.transform(loopFromOldDeltaToDeltaSQL.toString()), true);
			cmds.add(cteDef);
		}
		return Pair.make(resultTable, cmds);
		
	}
	
	// return result table, result variable,  and list of commands
	private Pair<Pair<String, Variable>, List<SQLCommand>> computeReachableNodes(PlanNode node, PlanNode predecessor, int level, boolean trackStartingPoint) {
		assert level == 0 || level <0 || level ==1 : level; //
		QueryTriple t = node.getTriple();

		assert level >=0 || !t.getPredicate().getPath().isDirectlyZeroOrOnePath(): level+"\n"+t.getPredicate().getPath();
		List<SQLCommand> ret = new LinkedList<SQLCommand>();
		// invoke reachableNode store procedure
		String resultTable;
		String deltaTable;
		String oldDeltaTable;
		String initializationSTMT = null;
		String initializationCTE = null;
		String loopSTMTFromOldDetaToDelta;
		String  loopSTMTFromDetaToOldDelta;
		Variable resultVariable;
		Variable startingPoint;
		assert !AccessMethodType.isGraphAccess(node.getMethod().getType()) : node;
		// This should be be done through query rewrite
		/*if (STEAccessMethodType.isGraphAccess(node.getMethod().getType()) 
		&& QueryTripleNode.isNotBoundVariable(t.getSubject(), node.getPattern().getFilterBindings())
		&& QueryTripleNode.isNotBoundVariable(t.getObject(), node.getPattern().getFilterBindings())) {
			// graph access with subject and object unbound variable
			if (node.getAvailableVariables().contains(node.getTriple().getSubject().getVariable())) {
				resultVariable = node.getTriple().getSubject().getVariable();
			} else {
				resultVariable = node.getTriple().getObject().getVariable();
			}
			Query subQuery = getSubQuery(node, false,  resultVariable);
			Set<Variable> newExplicitIRIBoundVariables = HashSetFactory.make(explicitIRIBoundVariables);
			newExplicitIRIBoundVariables.addAll(subQuery.getMainPattern().gatherIRIBoundVariables());
			CodeGenerator newcodegen = new CodeGenerator(store, stats, context, subQuery, predecessor, tmptableMgr, procMgr, newvargen, newExplicitIRIBoundVariables, variable2RenamedVariable);
			assert !newcodegen.isRecursiveQuery();
			String sql = newcodegen.toSQLWithoutStoreProcedureSQL();
			Pair<String, List<SQLCommand>> pair = tmptableMgr.getTemporaryTable(getTableSignature(resultVariable));
			resultTable = pair.fst;
			ret.addAll(pair.snd);
			ret.add(new InsertIntoTable(resultTable, sql));
			return Pair.make(Pair.make(resultTable, resultVariable), ret);	
		}*/
		//

		if (AccessMethodType.isDirectAccess(node.getMethod().getType())) {
			resultVariable = t.getObject().getVariable();
			startingPoint = t.getSubject().getVariable();
		} else {
			resultVariable = t.getSubject().getVariable();
			startingPoint = t.getObject().getVariable();
		}
		Variable graphRestrictionVariable = t.getGraphRestriction()!=null
											&& t.getGraphRestriction().isFirstType() 
											&& node.getAvailableVariables().contains(t.getGraphRestriction().getFirst())
											&& isNotBoundVariableOrIsBoundVariableToMultipleConstants(t.getGraphRestriction().getFirst(), node.getPattern().getFilterBindings())?
													t.getGraphRestriction().getFirst() : null;
		//assert !QueryTripleNode.isNotBoundVariable(t.getSubject(), node.getPattern().getFilterBindings()) : t;
		// result table contains object values
		Variable[] tableVariables = graphRestrictionVariable == null? 
										(trackStartingPoint? new Variable[]{startingPoint, resultVariable} : new Variable[] {resultVariable} )
										: 
										(trackStartingPoint? new Variable[]{startingPoint,graphRestrictionVariable, resultVariable}:	new Variable[] { graphRestrictionVariable, resultVariable});
		Pair<String, List<SQLCommand>> pair = tmptableMgr.getTemporaryTable(getTableSignature(tableVariables));
		resultTable = pair.fst;
		ret.addAll(pair.snd);
		if (isDB2Backend()) {
			pair = tmptableMgr.getTemporaryTable(getTableSignature(tableVariables));
			deltaTable = pair.fst;
			ret.addAll(pair.snd);
		} else {
			assert isPostGresBackend() : store.getStoreBackend();
			deltaTable = resultTable;
		}
		if (isDB2Backend()) {
			pair = tmptableMgr.getTemporaryTable(getTableSignature(tableVariables));
			oldDeltaTable = pair.fst;
			ret.addAll(pair.snd);
		} else {
			assert isPostGresBackend(): store.getStoreBackend();
			oldDeltaTable = resultTable;
		}
		//
		
		//PropertyPathRewrite rw = new PropertyPathRewrite();
		Query qq; //boolean hasNoPropertyPathExpression = rw.rewrite(subQuery, true);
		// assert no nested recursion
		assert new PropertyPathRewrite().rewrite(qq= getSubQuery(node, false, tableVariables), true, null, null) : qq+"\n"+node; 
		
		// starting point 
		if (t.getPredicate().getPath() instanceof ZeroOrMorePath || (t.getPredicate().getPath() instanceof ZeroOrOnePath)) {
			boolean includeType = !explicitIRIBoundVariables.contains(resultVariable);
			boolean includeTypeStartingVariable = trackStartingPoint && !explicitIRIBoundVariables.contains(startingPoint);
			if (predecessor==null || !AccessMethodType.isPollAccess(node.getMethod().getType()) /*|| STEAccessMethodType.isGraphAccess(node.getMethod().getType())*/) {
				assert AccessMethodType.isIndexAccess(node.getMethod().getType()) || AccessMethodType.isScanAccess(node.getMethod().getType())
				/*||  STEAccessMethodType.isGraphAccess(node.getMethod().getType())*/ : node;
				// starting point is the subject only
				Set<Constant> constants = node.getPattern().getFilterBindings().get(startingPoint);
				if (constants != null && constants.size()>0 && graphRestrictionVariable == null  && !trackStartingPoint &&  !AccessMethodType.isScanAccess(node.getMethod().getType())) {
					//assert STEAccessMethodType.isIndexAccess(node.getMethod().getType()) : node;
					StringBuffer buf = new StringBuffer();
					InsertValuesIntoTable ins = new InsertValuesIntoTable(deltaTable, constants, includeType, store);
					buf.append(ins.toSQL());
					initializationSTMT = buf.toString();
					String initializationSQL = ins.getSQLWithoutInsert();
					if (isPostGresBackend()) {
						initializationCTE = tmptableMgr.getTemporaryTable(getTableSignature(tableVariables)).fst;
						ret.add(new CTEDefinition(initializationCTE,getTableSignature(false, tableVariables),  initializationSQL));
					}
				} else {
					
					//assert STEAccessMethodType.isScanAccess(node.getMethod().getType()): node;
					if ( AccessMethodType.isScanAccess(node.getMethod().getType())) {
						constants = null;
						// very expensive scan!!!
					}
					Set<Constant> graphRestrictionConstants = t.getGraphRestriction()!=null 
							&& t.getGraphRestriction().isFirstType()? node.getPattern().getFilterBindings().get(t.getGraphRestriction().getFirst()) : null;
					Query q = trackStartingPoint?
							getSelectAllResources(t.getGraphRestriction(), graphRestrictionConstants, startingPoint,  includeTypeStartingVariable, constants,
									trackStartingPoint, resultVariable , includeType)
							:getSelectAllResources(t.getGraphRestriction(), graphRestrictionConstants, startingPoint,  includeType, constants,
							trackStartingPoint, null , false);
					logger.info("Select all resources query: {}", q);
					Set<Variable> newExplicitIRIBoundVariables = HashSetFactory.make(explicitIRIBoundVariables);
					//newExplicitIRIBoundVariables.addAll(q.getMainPattern().gatherIRIBoundVariables());
					CodeGenerator newcodegen = new CodeGenerator( store, stats, context, q, predecessor, tmptableMgr, procMgr, newvargen, newExplicitIRIBoundVariables,variable2RenamedVariable,cteCount);
					assert !newcodegen.isRecursiveQueryOrHasZeroOrOne();
					if (isDB2Backend()) {
						String startingPointQuery = newcodegen.toSQLWithoutStoreProcedureSQL();
						initializationSTMT = new InsertIntoTable(deltaTable, startingPointQuery).toSQL();
					} else {
						assert isPostGresBackend() :store;
						initializationCTE = tmptableMgr.getTemporaryTable(getTableSignature(tableVariables)).fst;
						ret.addAll(newcodegen.toCTEDefinitions(initializationCTE));
					}
					cteCount = newcodegen.cteCount;
					
				}
			}  else {
				assert AccessMethodType.isPollAccess(node.getMethod().getType()) : node;
				if (graphRestrictionVariable == null || predecessor.getAvailableVariables().contains(graphRestrictionVariable)) {
					StringBuffer sql = new StringBuffer();
					sql.append("SELECT DISTINCT ");
					if (trackStartingPoint) {
						sql.append(startingPoint.getName()).append(" AS ")
							.append(startingPoint.getName()).append(", ");
						if (includeTypeStartingVariable) {
							sql.append(startingPoint.getName()).append(Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS).append(" AS ")
							.append(startingPoint.getName()).append(Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS).append(", ");
						}
					}
					if (graphRestrictionVariable!=null) {
						sql.append(graphRestrictionVariable.getName()).append(" AS ")
						.append(graphRestrictionVariable.getName()).append(", ");
						if (explicitIRIBoundVariables.contains(graphRestrictionVariable)) {
							sql.append(TypeMap.IRI_ID).append(" AS ")
							.append(graphRestrictionVariable.getName()).append(Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS).append(", ");
						} else {
							sql.append(graphRestrictionVariable.getName()).append(Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS).append(" AS ")
							.append(graphRestrictionVariable.getName()).append(Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS).append(", ");
						}
					}
					sql.append(startingPoint.getName()).append(" AS ")
						.append(resultVariable.getName()).append(" ");
					if (includeType) {
						sql.append(", ");
						if (explicitIRIBoundVariables.contains(startingPoint) ) {
							sql.append(TypeMap.IRI_ID).append(" AS ")
							.append(resultVariable.getName()).append(Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS).append(" ");
						}  else {
							sql.append(startingPoint.getName()).append(Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS).append(" AS ")
								.append(resultVariable.getName()).append(Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS).append(" ");
						}
					}
					sql.append("FROM ").append(predecessor.getMaterialzedTable());
					InsertIntoTable ins = new InsertIntoTable(deltaTable, sql.toString());
					initializationSTMT = ins.toSQL();
					String initializationSQL = sql.toString();
					if (isPostGresBackend()) {
						initializationCTE = tmptableMgr.getTemporaryTable(getTableSignature(tableVariables)).fst;
						ret.add(new CTEDefinition(initializationCTE, getTableSignature(false, tableVariables), initializationSQL));
					}
				} else {
					Set<Constant> graphRestrictionConstants = t.getGraphRestriction()!=null 
					&& t.getGraphRestriction().isFirstType()? node.getPattern().getFilterBindings().get(t.getGraphRestriction().getFirst()) : null;
					Query q = trackStartingPoint?
							getSelectAllResources(t.getGraphRestriction(), graphRestrictionConstants, startingPoint,  includeTypeStartingVariable, null,
									trackStartingPoint, resultVariable, includeType)
							:getSelectAllResources(t.getGraphRestriction(), graphRestrictionConstants, startingPoint,  includeType, null,
							trackStartingPoint, null, false);
					logger.info("Select all resources query: {}", q);
					Set<Variable> newExplicitIRIBoundVariables = HashSetFactory.make(explicitIRIBoundVariables);
					//newExplicitIRIBoundVariables.addAll(q.getMainPattern().gatherIRIBoundVariables());
					CodeGenerator newcodegen = new CodeGenerator(store, stats, context, q, predecessor, tmptableMgr, procMgr, newvargen, newExplicitIRIBoundVariables,variable2RenamedVariable, cteCount);
					assert !newcodegen.isRecursiveQueryOrHasZeroOrOne();
					if (isDB2Backend()) {
						String startingPointQuery = newcodegen.toSQLWithoutStoreProcedureSQL();
						initializationSTMT = new InsertIntoTable(deltaTable, startingPointQuery).toSQL();
					} else {
						assert isPostGresBackend() :store;
						initializationCTE = tmptableMgr.getTemporaryTable(getTableSignature(tableVariables)).fst;
						ret.addAll(newcodegen.toCTEDefinitions(initializationCTE));
					}
					cteCount = newcodegen.cteCount;
				}
			}
		} else {
			// starting point = result of the query select ?resultVariable where { ?startingPoint subPath ?resultVariable . FILTER (?startingPoint = XYZ) } 
			assert (t.getPredicate().getPath() instanceof OneOrMorePath) : t.getPredicate().getPath();
			Query subQuery = getSubQuery(node,  true, tableVariables);
			logger.info("Subquery = {}", subQuery);	
			Set<Variable> newExplicitIRIBoundVariables = HashSetFactory.make(explicitIRIBoundVariables);
			//newExplicitIRIBoundVariables.addAll(subQuery.getMainPattern().gatherIRIBoundVariables());
			CodeGenerator newcodegen = new CodeGenerator( store, stats, context, subQuery, 
					AccessMethodType.isPollAccess(node.getMethod().getType())?predecessor:null,
						tmptableMgr, procMgr, newvargen, newExplicitIRIBoundVariables,variable2RenamedVariable, cteCount);
			assert !newcodegen.isRecursiveQueryOrHasZeroOrOne();
			if (isDB2Backend()) {
				String startingPointQuery = newcodegen.toSQLWithoutStoreProcedureSQL();
				initializationSTMT = new InsertIntoTable(deltaTable, startingPointQuery).toSQL();
			} else {
				assert isPostGresBackend() :store;
				initializationCTE = tmptableMgr.getTemporaryTable(getTableSignature(tableVariables)).fst;
				ret.addAll(newcodegen.toCTEDefinitions(initializationCTE));
			}
			cteCount = newcodegen.cteCount;
		}
		// iteration step = select ?resultVariable where  { oldDeltaTable subPath ?resultVariable . FILTER (?startingPoint = XYZ) }
		Set<Variable> prodVars;
		if (trackStartingPoint) {
			prodVars = HashSetFactory.make();
			prodVars.add(resultVariable);
			prodVars.add(startingPoint);
		} else {
			prodVars =  Collections.singleton(resultVariable);
		}
		PlanNode oldDeltaTableNode = new PlanNode(oldDeltaTable,prodVars);
		
									
		QueryTriple triple;
		Variable newResultVar =null;
		String varPrefix="res";
		int startSuffix = OCUtils.nextAvailableSuffixVariable(getAllVariables(node.getTriple()), varPrefix)+1;
		NewVariableGenerator vargen = new NewVariableGenerator(varPrefix, startSuffix);
		triple = new QueryTriple(
				new QueryTripleTerm(node.getTriple().getSubject().getVariable().equals(resultVariable)? newResultVar = new Variable(vargen.createNewVariable()) : resultVariable),
					node.getTriple().getPredicate(),
				new QueryTripleTerm(node.getTriple().getObject().getVariable().equals(resultVariable)? newResultVar = new Variable(vargen.createNewVariable()) : resultVariable));
		assert newResultVar!=null;
		Set<Variable> newExplicitIRIBoundVariables = HashSetFactory.make(explicitIRIBoundVariables);
		if (explicitIRIBoundVariables.contains(resultVariable)) {
			newExplicitIRIBoundVariables.add(newResultVar);
		}
		Variable newStartingPoint= trackStartingPoint? startingPoint:null;
		Variable[] newTableVariables = graphRestrictionVariable == null? 
				(trackStartingPoint? new Variable[]{newStartingPoint, newResultVar} : new Variable[] {newResultVar} )
				: 
				(trackStartingPoint? new Variable[]{newStartingPoint,graphRestrictionVariable, newResultVar}:	new Variable[] { graphRestrictionVariable, newResultVar});

		Query subQuery = getSubQuery(triple, node.getTriple().getGraphRestriction(), node, false, newExplicitIRIBoundVariables, newTableVariables);//  // newResultVar)// resultVariable);
		//newExplicitIRIBoundVariables.addAll(subQuery.getMainPattern().gatherIRIBoundVariables());
		Map<Variable, Variable> newVariable2RenamedVariable = variable2RenamedVariable==null? HashMapFactory.<Variable, Variable>make() : HashMapFactory.<Variable, Variable>make(variable2RenamedVariable);
		newVariable2RenamedVariable.put(newResultVar,  resultVariable);
		if (trackStartingPoint) {
			newVariable2RenamedVariable.put(newStartingPoint, startingPoint);
		}
		CodeGenerator newcodegen = new CodeGenerator( store, stats, context, subQuery, oldDeltaTableNode, tmptableMgr, procMgr, newvargen, newExplicitIRIBoundVariables,newVariable2RenamedVariable, cteCount);
		assert !newcodegen.isRecursiveQueryOrHasZeroOrOne();
		String loopFromOldDeltaToDeltaSQL = newcodegen.toSQLWithoutStoreProcedureSQL();
		logger.info("Subquery = {}", subQuery);			
		logger.info("Recursive step SQL:\n{}", loopFromOldDeltaToDeltaSQL);
		logger.info("InitializationSTMT:\n{}", initializationSTMT);
		logger.info("InitializationCTE:\n{}", initializationCTE);
		if (isDB2Backend()) {
			assert initializationSTMT!=null;
			loopSTMTFromOldDetaToDelta = new InsertIntoTable(deltaTable,
											 loopFromOldDeltaToDeltaSQL+
											"\n EXCEPT (SELECT * FROM "+ resultTable).toSQL()+")";
			loopSTMTFromDetaToOldDelta =  new InsertIntoTable(oldDeltaTable,
										 loopFromOldDeltaToDeltaSQL.replace(oldDeltaTable, deltaTable)
										+"\n EXCEPT (SELECT * FROM "+ resultTable).toSQL()+")";
			StoreProcedureInvocation procCall = new StoreProcedureInvocation(PROC_REACHABLENODES, resultTable, deltaTable, oldDeltaTable,
					initializationSTMT, loopSTMTFromOldDetaToDelta,loopSTMTFromDetaToOldDelta,
					level);
			ret.add(procCall);
			ret.addAll(tmptableMgr.release(deltaTable));
			ret.addAll(tmptableMgr.release(oldDeltaTable));
		} else {
			assert isPostGresBackend() : store.getStoreBackend();
			assert initializationCTE!=null;
			if (level == 0) {
				String sql = "SELECT * FROM "+initializationCTE;
				ret.add(new CTEDefinition(resultTable,getTableSignature(false, tableVariables), sql));
			}
			else if (level == 1) {
				String sql = "SELECT * FROM "+initializationCTE+
							"\n UNION \n"
							+converter.transform(loopFromOldDeltaToDeltaSQL.replace(oldDeltaTable,initializationCTE));
				ret.add(new CTEDefinition(resultTable,getTableSignature(false, tableVariables), sql));
			} else {
				assert level < 0 : level;
				ret.add(new RecursiveCTEDefinition(resultTable, getTableSignature(false, tableVariables),
						"SELECT * FROM "+initializationCTE+" ",
						//converter.transform(initializationSQL)
						converter.transform(loopFromOldDeltaToDeltaSQL),
						true));	
			}
		}
		cteCount = newcodegen.cteCount;
		return Pair.make(Pair.make(resultTable, resultVariable), ret);
	}
	public static Set<String> getAllVariables(QueryTriple t) {
		Set<String> ret = HashSetFactory.make();
		if (t.getObject().isVariable()) {
			ret.add(t.getObject().getVariable().getName());
		}
		if (t.getSubject().isVariable()) {
			ret.add(t.getSubject().getVariable().getName());
		}
		if (t.getGraphRestriction()!=null && t.getGraphRestriction().isFirstType()) {
			ret.add(t.getGraphRestriction().getFirst().getName());
		}
		return ret;
	}
	/**
	 * return a query that returns all the resources in a store (i.e., "select distinct ?variable where { {?variable ?p1  ?x1} union { ?x2 ?p2 ?variable} }"
	 * or "select ?graphRestriction ?variable where { GRAPH graphRestriction { {?variable ?p1  ?x1} union { ?x2 ?p2 ?variable} } }"
	 * or "select distinct ?startingVar ?variable where { {?variable ?p1  ?x1} union { ?x2 ?p2 ?variable}  bind (?variable AS ?startingVar)}"
	 * or "select ?startingVar ?graphRestriction ?variable where { GRAPH graphRestriction { {?variable ?p1  ?x1} union { ?x2 ?p2 ?variable} } bind (?variable AS ?startingVar) } ). 
	 * @return
	 */
	private Query getSelectAllResources( BinaryUnion<Variable, IRI> graphRestriction, Set<Constant> graphRestrictionConstBindings,  final Variable variable, final boolean projectVariableType,Set<Constant> variableConstBindings,
					final boolean trackStartingPoint, final Variable startingVariable,final  boolean projectStartingVariableType ) {
		PatternSet union = new PatternSet(EPatternSetType.UNION) {
			@Override
			public Set<Variable> gatherIRIBoundVariables() {
				Set<Variable> ret = HashSetFactory.make(super.gatherIRIBoundVariables());
				if (projectVariableType) {
					ret.remove(variable);
				} else {
					ret.add(variable);
				}
				if (trackStartingPoint) {
					if (projectStartingVariableType) {
						ret.remove(startingVariable); 
					}else {
						ret.add(startingVariable);
					}
				}
				return ret;
			}
			
		}; 
		SimplePattern sp = new SimplePattern();
		String varPrefix="var";
		int startSuffix = OCUtils.nextAvailableSuffixVariable(Collections.singleton(variable.getName()), varPrefix)+1;
		NewVariableGenerator vargen = new NewVariableGenerator(varPrefix, startSuffix);
		QueryTriple triple = new QueryTriple(new QueryTripleTerm(variable),
				new PropertyTerm(new Variable(vargen.createNewVariable())),
				new QueryTripleTerm(new Variable(vargen.createNewVariable())));
		sp.addQueryTriple(triple);
		
		if (variableConstBindings!=null) {
			for (Constant val :variableConstBindings) {
				// TODO: Use Values instead of Filter (especially for multiple constants)
				sp.addFilter(new RelationalExpression(new VariableExpression(variable.getName()), 
						new ConstantExpression(val), ERelationalOp.EQUAL));
			}
		}
	
		union.addPattern(sp);
		
		sp = new SimplePattern();
		triple = new QueryTriple(new QueryTripleTerm(new Variable(vargen.createNewVariable())),
				new PropertyTerm(new Variable(vargen.createNewVariable())), 
				new QueryTripleTerm(variable));
		sp.addQueryTriple(triple);
		if (variableConstBindings!=null) {
			for (Constant val :variableConstBindings) {
				// TODO: Use Values instead of Filter (especially for multiple constants)
				sp.addFilter(new RelationalExpression(new VariableExpression(variable.getName()), 
						new ConstantExpression(val), ERelationalOp.EQUAL));
			}
		}
		union.addPattern(sp);
		if (graphRestriction!=null) {
			union.setGraphRestriction(graphRestriction);
			union.pushGraphRestrictions();
			if (graphRestrictionConstBindings!=null && graphRestriction.isFirstType()) {
				for (Constant val: graphRestrictionConstBindings) {
					// TODO: Use Values instead of Filter (especially for multiple constants)
					union.addFilter(new RelationalExpression(new VariableExpression(graphRestriction.getFirst().getName()), 
							new ConstantExpression(val), ERelationalOp.EQUAL));
				}
			}
		}
		PatternSet top;
		if (trackStartingPoint) {
			top=new PatternSet(EPatternSetType.AND);
			top.addPattern(union);
			top.addPattern(new BindPattern(startingVariable, new VariableExpression(variable))); 
			
		} else {
			top = union;
		}
		top.pushFilters();
		SelectQuery sq = new SelectQuery();
		sq.setGraphPattern(top);
	
		SelectClause sc = new SelectClause();
		if (trackStartingPoint) {
			sc.addProjectedVariable(new ProjectedVariable(startingVariable));//, new VariableExpression(variable.getName())));
		}
 		if (graphRestriction!=null && graphRestriction.isFirstType()) {
			sc.addProjectedVariable(new ProjectedVariable(graphRestriction.getFirst()));
		}
		sc.addProjectedVariable(new ProjectedVariable(variable));
		sq.setSelectClause(sc);
		Query ret = new Query(new QueryPrologue(), sq);
		ret.getSelectQuery().getSelectClause().setSelectModifier(ESelectModifier.DISTINCT);
		return ret;
		
	}
	private Query getSubQuery(PlanNode node,   boolean includeVariableConstantFilters,  Variable... projectedVariables) {
		return getSubQuery(node, includeVariableConstantFilters, explicitIRIBoundVariables,  projectedVariables);
	}
	private Query getSubQuery(PlanNode node,   boolean includeVariableConstantFilters, Set<Variable> explicitIRIBoundVariables, Variable... projectedVariables) {
		return getSubQuery(null, null, node, includeVariableConstantFilters, explicitIRIBoundVariables, projectedVariables);
	}
	private Query getSubQuery(QueryTriple triple, BinaryUnion<Variable, IRI> graphRestriction,  PlanNode node,   boolean includeVariableConstantFilters, Set<Variable> explicitIRIBoundVariables,  Variable... projectedVariables) {
		if (projectedVariables == null || projectedVariables.length==0) {
			throw new IllegalArgumentException();
		}
		QueryTriple t = triple!=null? triple: node.getTriple();
		BinaryUnion<Variable, IRI> gr = graphRestriction!=null ? graphRestriction : node.getGraphRestriction();
		Path subPath =((UnaryPathOp) node.getTriple().getPredicate().getPath()).getSubPath();
		QueryTriple st = new QueryTriple(t.getSubject(), new PropertyTerm(subPath), t.getObject());
		Set<Variable> explicitNotIRIBoundVariables = HashSetFactory.make(Arrays.asList(projectedVariables));
		explicitNotIRIBoundVariables.addAll(t.gatherVariables());
		if(gr!=null && gr.isFirstType()) {
			explicitNotIRIBoundVariables.add(gr.getFirst());
		}
		explicitNotIRIBoundVariables.removeAll(explicitIRIBoundVariables);

		
		SimplePattern sp = new SimplePatternWithExplicitIRIBoundVariables(explicitIRIBoundVariables, explicitNotIRIBoundVariables);
		sp.addQueryTriple(st);
		if (gr!=null) {
			sp.setGraphRestriction(gr);
			sp.pushGraphRestrictions();
		}
		
		if (includeVariableConstantFilters) {
			Set<Constant> consts = node.getPattern().getFilterBindings().get(t.getSubject().getVariable());
			if (consts!=null) {
				for (Constant val :consts) {
					// TODO: Use Values instead of Filter (especially for multiple constants)
					sp.addFilter(new RelationalExpression(new VariableExpression(t.getSubject().getVariable().getName()), 
							new ConstantExpression(val), ERelationalOp.EQUAL));
				}
			}
			consts = node.getPattern().getFilterBindings().get(t.getObject().getVariable());
			if (consts!=null) {
				for (Constant val :consts) {
					// TODO: Use Values instead of Filter (especially for multiple constants)
					sp.addFilter(new RelationalExpression(new VariableExpression(t.getObject().getVariable().getName()), 
							new ConstantExpression(val), ERelationalOp.EQUAL));
				}
			}
			sp.pushFilters();
		}
		
		SelectQuery sq = new SelectQuery();
		sq.setGraphPattern(sp);
		SelectClause sc = new SelectClause();
		for (Variable v : projectedVariables) {
			sc.addProjectedVariable(new ProjectedVariable(v));
		}
		sq.setSelectClause(sc);
		Query subQuery = new Query(new QueryPrologue(), sq);
		subQuery.getSelectQuery().getSelectClause().setSelectModifier(ESelectModifier.DISTINCT);
		
		// query rewrite to transform property paths
		PropertyPathRewrite rewrite = new PropertyPathRewrite();
		rewrite.rewrite(subQuery, true, explicitIRIBoundVariables, explicitNotIRIBoundVariables);
		logger.debug("transformed subquery: {}", subQuery);
		//
		return subQuery;
	}
	
	protected synchronized String getSQL(PlanNode node) {
		return getSQL(node, new LinkedList<Variable>(node.getAvailableVariables()));
	}
	protected synchronized String getSQL(PlanNode node, List<Variable> projectedVariables) {
		assert projectedVariables == null || projectedVariables.isEmpty() || node.getAvailableVariables().containsAll(projectedVariables) : projectedVariables+"\n"+node;

		try {
			SQLGenerator gen = new SQLGenerator(plan, query, store, context, cteCount);
			Pair<Pair<String, String>, Integer> pair =  gen.toSQLWithRootDetailed(node, projectedVariables, explicitIRIBoundVariables);
			cteCount += pair.snd+1;
			return gen.toSQL(pair.fst);
		}  catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		
	}
	/**
	 * returns a pair of strings consisting of the CTE definitions and the main select statement
	 * @param node
	 * @param projectedVariables
	 * @return
	 */
	protected synchronized Pair<String, String> getSQLDetailed(PlanNode node) {
		return getSQLDetailed(node, new LinkedList<Variable>(node.getAvailableVariables()));
	}
	/**
	 * returns a pair of strings consisting of the CTE definitions and the main select statement
	 * @param node
	 * @param projectedVariables
	 * @return
	 */
	protected synchronized Pair<String, String> getSQLDetailed(PlanNode node, List<Variable> projectedVariables) {
		assert projectedVariables == null || projectedVariables.isEmpty() || node.getAvailableVariables().containsAll(projectedVariables) : projectedVariables+"\n"+node;

		try {
			SQLGenerator gen = new SQLGenerator(plan, query, store, context, cteCount);
			Pair<Pair<String, String>, Integer> pair =  gen.toSQLWithRootDetailed(node, projectedVariables, explicitIRIBoundVariables);
			cteCount += pair.snd+1;
			return pair.fst;
		}  catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
