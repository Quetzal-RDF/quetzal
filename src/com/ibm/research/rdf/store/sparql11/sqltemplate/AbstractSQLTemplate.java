package com.ibm.research.rdf.store.sparql11.sqltemplate;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.research.rdf.store.Context;
import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.config.Constants;
import com.ibm.research.rdf.store.hashing.HashingException;
import com.ibm.research.rdf.store.hashing.HashingHelper;
import com.ibm.research.rdf.store.runtime.service.types.TypeMap;
import com.ibm.research.rdf.store.sparql11.model.ConstantExpression;
import com.ibm.research.rdf.store.sparql11.model.Expression;
import com.ibm.research.rdf.store.sparql11.model.RelationalExpression;
import com.ibm.research.rdf.store.sparql11.model.Variable;
import com.ibm.research.rdf.store.sparql11.model.VariableExpression;
import com.ibm.research.rdf.store.sparql11.planner.PlanNode;
import com.ibm.research.rdf.store.sparql11.sqlwriter.FilterContext;
import com.ibm.research.rdf.store.sparql11.sqlwriter.SPARQLToSQLExpression;
import com.ibm.research.rdf.store.sparql11.sqlwriter.SQLWriterException;
import com.ibm.wala.util.collections.Pair;

public abstract class AbstractSQLTemplate {
	
	String templateName;	
	Store store;
	Context ctx;
	STPlanWrapper wrapper;
	protected PlanNode planNode;
	protected Map<String, Pair<String, String>> varMap;
	protected final SPARQLToSQLExpression expGenerator = new SPARQLToSQLExpression();
	
	public AbstractSQLTemplate(String templateName, Store store, Context ctx, STPlanWrapper wrapper, PlanNode planNode) {
		this.templateName = templateName;
		this.store = store;
		this.ctx = ctx;
		this.wrapper = wrapper;
		this.planNode = planNode;
	}

	public AbstractSQLTemplate(String templateName, Store store, Context ctx, STPlanWrapper wrapper) {
		this(templateName, store, ctx, wrapper, null);
	}
	
	public String createSQLString() throws Exception{
		Set<SQLMapping> mappings = populateMappings();
		SQLTemplateManager.setStoreTemplate(this.store);
		return SQLTemplateManager.getSQLString(templateName, mappings);
	}
	

	abstract Set<SQLMapping> populateMappings() throws Exception;

	protected List<String> getFilterSQLConstraint() throws SQLWriterException {
		List<String> filterSQLConstraint = new LinkedList<String>();
		for(Expression e :planNode.getApplicableFilters(wrapper.getPlan())){
			if( ! planNode.getAvailableVariables().containsAll(e.gatherVariables()))continue ;	
			FilterContext context = new FilterContext(varMap,  wrapper.getPropertyValueTypes(), planNode);
			List<Expression> constants = getConstantsThatNeedTypeCheck(e);
			for (Expression c : constants) {
				context.addConstantNeedsTypeCheck(c);
			}
			// KAVITHA: This is bad, but if you enter a boolean constant expression as an filter, DB2 will not like it because booleans aren't supported in WHERE clauses
			if (e instanceof ConstantExpression && e.getReturnType() == TypeMap.BOOLEAN_ID) {
				if (((ConstantExpression) e).getConstant().toDataString().equals("true")) {
					filterSQLConstraint.add("0=0");
				} else {
					filterSQLConstraint.add("1=0");
				}
			} else {
				String eSql = expGenerator.getSQLForExpression(e,context, store);
				filterSQLConstraint.add(eSql);
			}
			
		}
		return filterSQLConstraint;
	}
	
	private List<Expression> getConstantsThatNeedTypeCheck(Expression e) {
		if (!(e instanceof RelationalExpression) || !( ((RelationalExpression) e).getLeft() instanceof VariableExpression)) {
			return Collections.emptyList();
		}
		List<Expression> constants = new LinkedList<Expression>();
		for (Expression exp : e.getSubExpressions()) {
			if (exp instanceof ConstantExpression && ((ConstantExpression) exp).getConstant().getIRI() == null) {
				constants.add(exp);
			}
		}
		return constants;
	}

	protected String getQIDMapping() {
		Integer planId = wrapper.getPlanNodeId(planNode);
		return planId.toString();
	}
	
	protected List<String> getDescribeProjectMapping() throws Exception{
		List<String> projectMapping = new LinkedList<String>();
		projectMapping.add("DESCRIBED_RESOURCE");
		projectMapping.add("PROPERTY");
		projectMapping.add("OBJECT");
		projectMapping.add("TYP");
		return projectMapping;
	}
	
	public boolean getPredecessorConstraint(List<String> sqlConstraint,
			Variable variable, PlanNode predecessor, String column, String typeColumn,
			boolean needsTypeConstraint) {
		boolean entryConstraintWithPredecessor = false;
		if(predecessor!=null ){
			Set<Variable> availableVariables = predecessor.getAvailableVariables();
			if(availableVariables != null){
				if(availableVariables.contains(variable)){
					boolean needsTypeCast = predecessor.isTypeCheckVariables(variable).fst;
					String entryPredName = wrapper.getPlanNodeVarMapping(predecessor,variable.getName());
					if (needsTypeCast) {
						SPARQLToSQLExpression exp = new SPARQLToSQLExpression();
						String entryPredNameMod = exp.getColumnName(entryPredName, predecessor.getTypeOfTypeCheckVariable(variable));
						sqlConstraint.add(column + " = " +wrapper.getPlanNodeCTE(predecessor) + "." +entryPredNameMod);
					} else { 
						sqlConstraint.add(column + " = " +wrapper.getPlanNodeCTE(predecessor) + "." +entryPredName);
					}
					if(needsTypeConstraint){
						sqlConstraint.add(typeColumn + " = " +wrapper.getPlanNodeCTE(predecessor) + "." +entryPredName + Constants.TYP_COLUMN_SUFFIX_IN_SPARQL_RS);
					}
					entryConstraintWithPredecessor = true;
				}
			}
		}
		return entryConstraintWithPredecessor;
	}

	public static String getSID(String value, int maxLength) {
		if (value.length() > maxLength) {
			try {
				return Constants.PREFIX_SHORT_STRING
						+ HashingHelper.hashLongString(value);
			} catch (HashingException | UnsupportedEncodingException e) {
			}
		}
		return value;
	}
	
}
