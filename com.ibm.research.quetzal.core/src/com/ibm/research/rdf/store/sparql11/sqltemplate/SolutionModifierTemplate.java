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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ibm.research.rdf.store.Context;
import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.sparql11.model.Expression;
import com.ibm.research.rdf.store.sparql11.model.OrderCondition;
import com.ibm.research.rdf.store.sparql11.model.SolutionModifiers;
import com.ibm.research.rdf.store.sparql11.model.VariableExpression;
import com.ibm.research.rdf.store.sparql11.sqlwriter.FilterContext;
import com.ibm.research.rdf.store.sparql11.sqlwriter.SQLWriterException;
import com.ibm.wala.util.collections.HashMapFactory;
import com.ibm.wala.util.collections.Pair;

/**
 * This is really a helper class, and reflects a rather awkward class structure.
 * Should be moved to a base class in the future, but trying to move it to the
 * base right now causes a lot of different problems
 * @author ksrinivs
 *
 */
public class SolutionModifierTemplate extends AbstractSQLTemplate {

	private STPlanWrapper wrapper;
	private SolutionModifiers modifiers;
	private AbstractSelectTemplate mainTemplate;

	public SolutionModifierTemplate(String templateName, SolutionModifiers modifiers, Store store,
			Context ctx, STPlanWrapper wrapper, AbstractSelectTemplate mainTemplate) {
		super(templateName, store, ctx, wrapper);
		this.modifiers = modifiers;
		this.wrapper = wrapper;
		this.mainTemplate = mainTemplate;
		this.varMap = mainTemplate.varMap;
	}	

	Map<String, SQLMapping> populateMappings() throws Exception {
		Map<String, SQLMapping> mappings = HashMapFactory.make();

		SQLMapping qidSqlParams = new SQLMapping("sql_id", getSQLIDMapping(),
				null);
		mappings.put("sql_id", qidSqlParams);
		
		addSolutionModifiersMappings(mappings);

		return mappings;
	}


	private List<String> getSQLIDMapping() {
		List<String> qIdMapping = new LinkedList<String>();
		Integer planId = wrapper.getCteIdForSolutionModifier();
		qIdMapping.add(planId.toString());
		return qIdMapping;
	}



	protected String addSolutionModifiersMappings(Map<String, SQLMapping> mappings) {
		StringBuffer solnModifiers = new StringBuffer();
		if (modifiers == null) {
			return null;
		}
		try {
			FilterContext context = new FilterContext(varMap,
					wrapper.getPropertyValueTypes(), planNode);

			if (modifiers.getGroupClause() != null) {
				List<Expression> expressions = modifiers.getGroupClause()
						.getConditions();
				handleExpressionsInModifiers("groupBy", mappings, context,
						expressions);
				
			}
			if (modifiers.getHavingClause() != null) {
				List<Expression> expressions = modifiers.getHavingClause()
						.getConstraints();
				handleExpressionsInModifiers("having", mappings, context,
						expressions);
			}
			if (modifiers.getOrderClause() != null
					&& !modifiers.getOrderClause().isEmpty()) {
				Iterator<OrderCondition> orderConditions = modifiers
						.getOrderClause().iterator();
				
				List<String> orders = new LinkedList<String>();
				while (orderConditions.hasNext()) {
					OrderCondition o = orderConditions.next();
					mainTemplate.populateVarMap(o.getExpression());
					String str = expGenerator.getSQLExpression(o.getExpression(), context, store);
					orders.add(str + " " + o.getType());
				}
				mappings.put("orderBy", new SQLMapping("orderBy", orders, null));
			}
			if (modifiers.getLimitOffset() != null) {
				int limit = modifiers.getLimitOffset().getLimit();
				int offset = modifiers.getLimitOffset().getOffset();

				if (limit > -1) {
					mappings.put("limit", new SQLMapping("limit", limit, null));
				}
				if (limit > -1 && offset > -1) {
					mappings.put("offset", new SQLMapping("offset", offset, null));
				} else if (limit == -1 && offset > -1) {
					// KAVITHA DB2 is super annoying about needing limits to be
					// specified if offsets are
					// this is an egregious hack to give it a 'limit
					mappings.put("limit", new SQLMapping("limit", Integer.MAX_VALUE, null));
					mappings.put("offset", new SQLMapping("offset", offset, null));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return solnModifiers.toString();
	}

	private void handleExpressionsInModifiers(String mappingVal, Map<String, SQLMapping> mappings, FilterContext context, List<Expression> expressions)
			throws SQLWriterException {
		Iterator<Expression> it = expressions.iterator();
		List<String> ret = new LinkedList<String>();
		while (it.hasNext()) {
			Expression e = it.next();
			if (e instanceof VariableExpression && ((VariableExpression) e).getExpression() != null) { 
				e = ((VariableExpression) e).getExpression();
			}
			mainTemplate.populateVarMap(e);
			// this is dreadfully hackish, but SQL expects types as well for the group by, so we need to add the expression as well
			// as its type
			if (mappingVal.equals("groupBy") && e instanceof VariableExpression) {
				Pair<String,String> varTyp = mainTemplate.varMap.get(((VariableExpression) e).getVariable()); 
				ret.add(varTyp.snd);
			}
			ret.add(expGenerator.getSQLExpression(e, context, store));
			
		}
		mappings.put(mappingVal, new SQLMapping(mappingVal, ret, null));
	}

}
