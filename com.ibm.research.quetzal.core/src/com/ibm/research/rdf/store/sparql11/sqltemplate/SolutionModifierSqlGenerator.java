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

import java.util.List;
import java.util.Set;

import com.ibm.research.rdf.store.Context;
import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.sparql11.model.Query;
import com.ibm.research.rdf.store.sparql11.model.SolutionModifiers;
import com.ibm.research.rdf.store.sparql11.model.Variable;

public class SolutionModifierSqlGenerator {
	Query q;
	STPlanWrapper wrapper;
	Store store;
	Context ctx;
	
	public SolutionModifierSqlGenerator(Query q, STPlanWrapper wrapper, Store store, Context ctx) {
		this.q = q;
		this.wrapper = wrapper;
		this.store = store;
		this.ctx=ctx;
	}
	
	/*public String toSQL() throws Exception{
		StringBuffer sql = new StringBuffer();
		if(q.isSelectQuery()){			
			sql.append(processSolutionModifierForSelect());
			sql.append("\n");
			sql.append(processTopQueryForSelectAndDescribe());
		}
		else if(q.isAskQuery()){
			sql.append(processAskQuery());
		}
		else if(q.isDescribeQuery()){
			sql.append(processSolutionModifierForDescribe());
			sql.append("\n");
			sql.append(processTopQueryForSelectAndDescribe());
		}
		return sql.toString();
	}*/
	
	
	public String toSQL() throws Exception{
		StringBuffer sql = new StringBuffer();
		String solutionModifier = toSolutionModifierSQL();
		String topSql = toTopQuerySQL(null);
		if(solutionModifier != null)sql.append(solutionModifier);
		if(sql.length()>0)sql.append("\n");
		if(topSql != null)sql.append(topSql);
		return sql.toString();
	}
	
	public String toSolutionModifierSQL() throws Exception{
		if(q.isSelectQuery() || q.isConstructQuery()){			
			return processSolutionModifierForSelectConstruct();
		}		
		else if(q.isDescribeQuery()){
			return processSolutionModifierForDescribe();
		}
		return null;
	}
	
	public String toTopQuerySQL(Set<Variable> explicitIRIBoundVariables) throws Exception{
		if(q.isSelectQuery()){
			return processTopQueryForSelectDescribeConstruct(explicitIRIBoundVariables);
		}
		else if(q.isAskQuery()){
			return processAskQuery();
		}
		else if(q.isDescribeQuery()){
			return processTopQueryForSelectDescribeConstruct(explicitIRIBoundVariables);
		}
		else if(q.isConstructQuery()){
			return processTopQueryForSelectDescribeConstruct(explicitIRIBoundVariables);
		}
		return null;
	}
	
	public String toSQLWithoutResultModifier(List<Variable> project,Set<Variable> explicitIRIBoundVariables) throws Exception{
		AbstractSQLTemplate topSQLTemplate = new TopSelectAllTemplate("top_select",q ,store, ctx, wrapper, project,explicitIRIBoundVariables);
		return topSQLTemplate.createSQLString();
	}
	
	private String processSolutionModifierForSelectConstruct() throws Exception{
		StringBuffer sql = new StringBuffer();
		SolutionModifiers solutionModifiers = null;
		if(q.isSelectQuery())solutionModifiers = q.getSelectQuery().getSolutionModifier();
		if(q.isConstructQuery())solutionModifiers = q.getConstructQuery().getSolutionModifier();
		/*
		if(solutionModifiers!= null){
			if(solutionModifiers.getLimitOffset()!=null){
				sql.append(",\n");
				boolean hasOffset = solutionModifiers.getLimitOffset().getOffset()>0;
				AbstractSQLTemplate limitTemplate = new LimitTemplate(hasOffset?"limit_for_offset":"limit",q ,store, ctx, wrapper);
				sql.append(limitTemplate.createSQLString());
				if(hasOffset){
					sql.append(",\n");
					AbstractSQLTemplate offsetTemplate = new OffsetTemplate("offset",q ,store, ctx, wrapper);
					sql.append(offsetTemplate.createSQLString());
				}
			}
			//TODO: Mihaela: We are not currently handling other solution modifiers (e.g., ORDER BY).
		} */
		return sql.toString();
	}
	
	private String processTopQueryForSelectDescribeConstruct(Set<Variable> explicitIRIBoundVariables) throws Exception{
		AbstractSQLTemplate topSQLTemplate = new TopSelectTemplate("top_select",q ,store, ctx, wrapper, explicitIRIBoundVariables);
		return topSQLTemplate.createSQLString();
	}
	
	private String processAskQuery() throws Exception{
		AbstractSQLTemplate askTemplate = new AskTemplate("ask",q ,store, ctx, wrapper);
		return askTemplate.createSQLString();
	}
	
	private String processSolutionModifierForDescribe() throws Exception{
		StringBuffer sql = new StringBuffer();
		if(q.getMainPattern()!=null)sql.append(",\n");
		AbstractSQLTemplate describeSQLTemplate = new DescribeSQLTemplate("describe",q ,store, ctx, wrapper);
		sql.append(describeSQLTemplate.createSQLString());
		/*
		if(q.getDescribeQuery().getSolutionModifier() != null){
			if(q.getDescribeQuery().getSolutionModifier().getLimitOffset()!=null){
				sql.append(",\n");
				AbstractSQLTemplate limitTemplate = new LimitTemplate("limit",q ,store, ctx, wrapper);
				sql.append(limitTemplate.createSQLString());
				if(q.getDescribeQuery().getSolutionModifier().getLimitOffset().getOffset()>0){
					sql.append(",\n");
					AbstractSQLTemplate offsetTemplate = new OffsetTemplate("offset",q ,store, ctx, wrapper);
					sql.append(offsetTemplate.createSQLString());
				}
			}
		}
		*/
		return sql.toString();
	}
}
