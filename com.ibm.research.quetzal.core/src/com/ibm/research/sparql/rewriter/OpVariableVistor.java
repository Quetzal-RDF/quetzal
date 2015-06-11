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

package com.ibm.research.sparql.rewriter;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.OpVisitorBase;
import com.hp.hpl.jena.sparql.algebra.OpWalker;
import com.hp.hpl.jena.sparql.algebra.op.OpBGP;
import com.hp.hpl.jena.sparql.algebra.op.OpExtend;
import com.hp.hpl.jena.sparql.algebra.op.OpFilter;
import com.hp.hpl.jena.sparql.algebra.op.OpGraph;
import com.hp.hpl.jena.sparql.algebra.op.OpGroup;
import com.hp.hpl.jena.sparql.algebra.op.OpLeftJoin;
import com.hp.hpl.jena.sparql.algebra.op.OpPath;
import com.hp.hpl.jena.sparql.algebra.op.OpProject;
import com.hp.hpl.jena.sparql.algebra.op.OpQuad;
import com.hp.hpl.jena.sparql.algebra.op.OpTriple;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.ExprAggregator;

/**
 * @author Mariano Rodriguez <mrodrig@us.ibm.com>
 * 
 */
public abstract class OpVariableVistor<T> extends OpVisitorBase {
	private final Op query;
	private final boolean duplicates;
	final List<T> result = new LinkedList<T>();
	
	public List<T> getResult() {
		return result;
	}

	protected abstract T processVar(Node v);
	
	public OpVariableVistor(Op query, boolean duplicates) {
		this.query = query;
		this.duplicates = duplicates;
		OpWalker.walk(query, this);
	}

	private void add(T x) {
		if (duplicates || !result.contains(x)) {
			result.add(x);
		}
	}
	
	@Override
	public void visit(OpExtend opExtend) {
		for(Entry<Var, Expr> v : opExtend.getVarExprList().getExprs().entrySet()) {
			add(processVar(v.getKey()));
			processExpr(v.getValue());
		}
	}

	private void process(Node n) {
		if (n.isVariable()) {
			add(processVar(n));
		}
	}

	@Override
	public void visit(OpQuad opQuad) {
		process(opQuad.getQuad().getSubject());
		process(opQuad.getQuad().getPredicate());
		process(opQuad.getQuad().getObject());
		process(opQuad.getQuad().getGraph());
	}

	@Override
	public void visit(OpTriple opTriple) {
		process(opTriple.getTriple().getSubject());
		process(opTriple.getTriple().getPredicate());
		process(opTriple.getTriple().getObject());
	}

	@Override
	public void visit(OpBGP opBGP) {
		for(Triple t : opBGP.getPattern().getList()) {
			process(t.getSubject());
			process(t.getPredicate());
			process(t.getObject());					
		}
	}

	@Override
	public void visit(OpGraph opGraph) {
		process(opGraph.getNode());
	}

	@Override
	public void visit(OpPath opPath) {
		process(opPath.getTriplePath().getSubject());
		process(opPath.getTriplePath().getObject());
	}

	@Override
	public void visit(OpFilter opFilter) {
		for(Expr e : opFilter.getExprs()) {
			processExpr(e);
		}
	}

	private void processExpr(Expr e) {
		for(Var v : e.getVarsMentioned()) {
			add(processVar(v));
		}
	}

	@Override
	public void visit(OpGroup opGroup) {
		for(Entry<Var, Expr> e : opGroup.getGroupVars().getExprs().entrySet()) {
			add(processVar(e.getKey()));
			for(Var v : e.getValue().getVarsMentioned()) {
				add(processVar(v));
			}
		}
		for(ExprAggregator e : opGroup.getAggregators()) {
			add(processVar(e.getAggVar().asVar()));
			if (e.getExpr() !=  null) {
				for(Var v : e.getExpr().getVarsMentioned()) {
					add(processVar(v));					
				}
			}
			if (e.getAggregator().getExpr() != null) {
				for(Var v : e.getAggregator().getExpr().getVarsMentioned()) {
					add(processVar(v));					
				}
			}
		}
	}

	@Override
	public void visit(OpLeftJoin join) {
		if (join.getExprs() != null) {
			for(Expr e : join.getExprs()) {
				processExpr(e);
			}
		}
	}

	@Override
	public void visit(OpProject opProject) {
		for(Var v : opProject.getVars()) {
			add(processVar(v));					
		}
	}
}