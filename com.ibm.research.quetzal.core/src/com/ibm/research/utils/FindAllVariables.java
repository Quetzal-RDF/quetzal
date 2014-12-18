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

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.ExprAggregator;
import com.hp.hpl.jena.sparql.expr.ExprVars;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementAssign;
import com.hp.hpl.jena.sparql.syntax.ElementBind;
import com.hp.hpl.jena.sparql.syntax.ElementData;
import com.hp.hpl.jena.sparql.syntax.ElementDataset;
import com.hp.hpl.jena.sparql.syntax.ElementExists;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementMinus;
import com.hp.hpl.jena.sparql.syntax.ElementNamedGraph;
import com.hp.hpl.jena.sparql.syntax.ElementNotExists;
import com.hp.hpl.jena.sparql.syntax.ElementOptional;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;
import com.hp.hpl.jena.sparql.syntax.ElementService;
import com.hp.hpl.jena.sparql.syntax.ElementSubQuery;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;
import com.hp.hpl.jena.sparql.syntax.ElementUnion;
import com.hp.hpl.jena.sparql.syntax.ElementVisitor;
import com.hp.hpl.jena.sparql.syntax.PatternVars;
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
		e.getPatternElement().visit(this);
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