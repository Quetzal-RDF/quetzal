
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Node_Literal;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.algebra.Algebra;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.op.OpExtend;
import com.hp.hpl.jena.sparql.algebra.op.OpTriple;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.core.VarExprList;
import com.hp.hpl.jena.sparql.expr.nodevalue.NodeValueNode;
import com.hp.hpl.jena.sparql.graph.NodeTransform;
import com.hp.hpl.jena.sparql.graph.NodeTransformLib;

/**
 * @author Kavitha Srinivas <ksrinivs@us.ibm.com>
 * 
 */
public class RuleforJena {
	OpTriple consequent;
	Op antecedent;
	static int count = 0;
	int id = 0;
	
	private static int ID_COUNTER = 0;
	
	public RuleforJena(Query constructQuery) {
		this.id = ID_COUNTER++;
		
		antecedent = Algebra.compile(constructQuery);
		// KAVITHA: the consequent should be a single triple
		List<Triple> list = constructQuery.getConstructTemplate().getBGP().getList();
		consequent = new OpTriple(list.get(0));
		
		
		Op bind = null;
		VarExprList expr = new VarExprList();
		expr.add(Var.alloc("RULEID"+id) ,new NodeValueNode(Node_Literal.createLiteral("true")));		
		bind = OpExtend.extend(antecedent, expr);
		antecedent = bind;
		
		
		
	}
	
//	public RuleforJena(Op antecedent, OpTriple consequent) {
//		this.antecedent = antecedent;
//		this.consequent = consequent;
//		this.id = ID_COUNTER++;
//	}
	
	private RuleforJena(Op antecedent, OpTriple consequent, int id) {
		this.antecedent = antecedent;
		this.consequent = consequent;
		this.id = id;
	}
	
	
	
	public RuleforJena getFreshRule() throws Exception {
		RenameAllVars r = new RenameAllVars(this.id);
		Op newAnt = NodeTransformLib.transform(r, antecedent);
		OpTriple newCons = (OpTriple) NodeTransformLib.transform(r, consequent);
		
		RuleforJena result = new RuleforJena(newAnt, newCons, this.id);
		return result;
	}

	public String toString() {
		StringBuilder response = new StringBuilder();
		response.append("Consequent:\n");
		response.append(consequent);
		response.append("Antecedent:\n");
		response.append(antecedent);
		return response.toString();
	}
	
	static final class RenameAllVars implements NodeTransform {
		private Map<Var, Var> aliases = new HashMap<Var, Var>();
		private int id = -1;

		RenameAllVars() {
		}
		
		RenameAllVars(int id) {
			this.id = id;
		}
		
		RenameAllVars(Map<Var, Var> aliases) {
			this.aliases = aliases;
		}
		@Override
		public Node convert(Node node) {
			if (!Var.isVar(node))
				return node;

			Var var = (Var) node;
			Var var2 = aliases.get(var);
			if (var2 != null)
				return var2;
			
//			if (id == -1 )
				var2 = Var.alloc(var.getName() + "_" + count);
//			else 
//				var2 = Var.alloc(var.getName() + "_RULE" + this.id + "_" + count);
			count++;
			aliases.put(var, var2);
			return var2;
		}
	}

}
