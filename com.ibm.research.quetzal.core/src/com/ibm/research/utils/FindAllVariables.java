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
 package com.ibm.research.utils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprAggregator;
import org.apache.jena.sparql.expr.ExprVars;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementAssign;
import org.apache.jena.sparql.syntax.ElementBind;
import org.apache.jena.sparql.syntax.ElementData;
import org.apache.jena.sparql.syntax.ElementDataset;
import org.apache.jena.sparql.syntax.ElementExists;
import org.apache.jena.sparql.syntax.ElementFilter;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementMinus;
import org.apache.jena.sparql.syntax.ElementNamedGraph;
import org.apache.jena.sparql.syntax.ElementNotExists;
import org.apache.jena.sparql.syntax.ElementOptional;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementService;
import org.apache.jena.sparql.syntax.ElementSubQuery;
import org.apache.jena.sparql.syntax.ElementTriplesBlock;
import org.apache.jena.sparql.syntax.ElementUnion;
import org.apache.jena.sparql.syntax.ElementVisitor;
import org.apache.jena.sparql.syntax.PatternVars;

import com.ibm.wala.util.collections.HashSetFactory;

/**
 * Collect all variables appearing in a {@link Element}
 * @author fokoue
 *
 */
public class FindAllVariables implements ElementVisitor {

	
	/**
	 * returns all variables mentioned in a query
	 * @param query
	 * @return
	 */
	public static Set<Var> getAllVariables(Query query) {
			Set<Var> vars=  getAllVariables(query.getQueryPattern());
			if (query.hasGroupBy() && query.getGroupBy()!=null && query.getGroupBy().getExprs()!=null) {
				for (Map.Entry<Var, Expr> e: query.getGroupBy().getExprs().entrySet()) {
					vars.add(e.getKey());
					ExprVars.varsMentioned(vars, e.getValue());
				}
			}
			if (query.hasHaving() && query.getHavingExprs()!=null) {
				for (Expr e: query.getHavingExprs()) {
					ExprVars.varsMentioned(vars, e);
				}
			}
			
			if (query.hasAggregators() && query.getAggregators()!=null) {
				for (ExprAggregator e: query.getAggregators()) {
					ExprVars.varsMentioned(vars, e);
				}
			}
			for (String v: query.getResultVars()) {
				vars.add(Var.alloc(v));
			}
			return vars;
	}
	
	public static Set<Var> getAllVariables(Element e) {
			Set<Var> vars = HashSetFactory.make();
			FindAllVariables visitor = new FindAllVariables(vars);
			e.visit(visitor);
			return vars;
	}
	
	
	protected Collection<Var> vars;
	
	
	public FindAllVariables(Collection<Var> vars) {
		super();
		this.vars = vars;
	}

	protected void addVar(Node n) {
		if (n!=null && n.isVariable()) {
			vars.add(Var.alloc(n));
		}
	}
	protected void addVars(Triple t) {
		addVar(t.getSubject());
		addVar(t.getPredicate());
		addVar(t.getObject());
	}
	protected void addVars(TriplePath t) {
		addVar(t.getSubject());
		if (t.getPredicate()!=null) {
			addVar(t.getPredicate());
		}
		addVar(t.getObject());
	}
	
	@Override
	public void visit(ElementAssign e) {
		addVar(e.getVar());
		ExprVars.varsMentioned(vars, e.getExpr());
	}

	
	@Override
	public void visit(ElementBind e) {
		addVar(e.getVar());
		ExprVars.varsMentioned(vars, e.getExpr());
	}

	@Override
	public void visit(ElementDataset e) {
		e.getElement().visit(this);
	}

	@Override
	public void visit(ElementExists e) {
		e.getElement().visit(this);
	}
	
	/*@Override
	public void visit(ElementFetch e) {
		addVar(e.getFetchNode());
	}*/

	@Override
	public void visit(ElementData ed) {
		vars.addAll(ed.getVars());
		
	}

	@Override
	public void visit(ElementFilter e) {
		ExprVars.varsMentioned(vars, e.getExpr());
	}

	@Override
	public void visit(ElementGroup e) {
		for (Element ge : e.getElements()) {
			ge.visit(this);
		}
		
	}

	@Override
	public void visit(ElementMinus e) {
	
		e.getMinusElement().visit(this);
	}

	@Override
	public void visit(ElementNamedGraph e) {
		e.getElement().visit(this);
		addVar(e.getGraphNameNode());
	}

	@Override
	public void visit(ElementNotExists e) {
		e.getElement().visit(this);
	}

	@Override
	public void visit(ElementOptional e) {
		e.getOptionalElement().visit(this);
	}

	

	@Override
	public void visit(ElementService e) {
		e.getElement().visit(this);
		addVar(e.getServiceNode());
	}

	@Override
	public void visit(ElementSubQuery e) {
		vars.addAll(PatternVars.vars(e));
		e.getQuery().getQueryPattern().visit(this);
		
	}

	@Override
	public void visit(ElementTriplesBlock e) {
		for (Triple t: e.getPattern().getList()) {
			addVars(t);
		}
		
	}
	@Override
	public void visit(ElementPathBlock e) {
		for (TriplePath t:e.getPattern().getList()) {
			addVars(t);
		}
	}
	@Override
	public void visit(ElementUnion e) {
		for (Element ge : e.getElements()) {
			ge.visit(this);
		}
	}
	
	
}