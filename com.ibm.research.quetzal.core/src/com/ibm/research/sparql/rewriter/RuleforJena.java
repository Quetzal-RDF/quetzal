
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
import java.util.Map;
import java.util.UUID;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.algebra.Algebra;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.op.OpTriple;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.graph.NodeTransform;
import com.hp.hpl.jena.sparql.graph.NodeTransformLib;

/**
 * @author Kavitha Srinivas <ksrinivs@us.ibm.com>
 * 
 */
public class RuleforJena {
	OpTriple consequent;
	Op antecedent;
	
	public RuleforJena(Query constructQuery) {
		antecedent = Algebra.compile(constructQuery);
		// KAVITHA: the consequent should be a single triple
		consequent = new OpTriple(constructQuery.getConstructTemplate().getBGP().getList().get(0));
	}
	
	public RuleforJena(Op antecedent, OpTriple consequent) {
		this.antecedent = antecedent;
		this.consequent = consequent;
	}
	
	public RuleforJena getFreshRule() throws Exception {
		RenameAllVars r = new RenameAllVars();
		Op newAnt = NodeTransformLib.transform(r, antecedent);
		OpTriple newCons = (OpTriple) NodeTransformLib.transform(r, consequent);
		
		RuleforJena result = new RuleforJena(newAnt, newCons);
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

		RenameAllVars() {
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
			var2 = Var.alloc(var.getName() + "_" + UUID.randomUUID());
			aliases.put(var, var2);
			return var2;
		}
	}

}
