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
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.openrdf.query.algebra.Extension;
import org.openrdf.query.algebra.ExtensionElem;
import org.openrdf.query.algebra.Projection;
import org.openrdf.query.algebra.ProjectionElem;
import org.openrdf.query.algebra.Reduced;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.algebra.ValueConstant;
import org.openrdf.query.algebra.Var;
import org.openrdf.query.algebra.helpers.QueryModelVisitorBase;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.sparql.ast.VisitorException;

/**
 * @author Mariano Rodriguez <mrodrig@us.ibm.com>
 * 
 */
public class Rule {

	StatementPattern consequent;
	TupleExpr antecedent;

	private Rule() {

	}

	/**
	 * 
	 */
	public Rule(ParsedQuery constructQuery) {

		/* Getting elements of the construct part, they are always implemented 
		*  as a pair of projection and extension before the actual algebra of the query
		   e.g.,
		   Projection
		      ProjectionElemList
		         ProjectionElem "x" AS "subject"
		         ProjectionElem "_const-f5e5585a-uri" AS "predicate"
		         ProjectionElem "_const-a31c101d-uri" AS "object"
		      Extension
		         ExtensionElem (_const-a31c101d-uri)
		            ValueConstant (value=http://example.org/Company)
		         ExtensionElem (_const-f5e5585a-uri)
		            ValueConstant (value=http://www.w3.org/1999/02/22-rdf-syntax-ns#type)
		         Join...
		 */

		TupleExpr expr = constructQuery.getTupleExpr();
		Projection projection = (Projection)((Reduced) expr).getArg();
		Extension extension = (Extension) projection.getArg();
		
		antecedent = extension.getArg();

		extractConsequent(expr, projection, extension);
	}

	private void extractConsequent(TupleExpr constructQuery, Projection projection, Extension extension) {
		StatementPattern consequent = new StatementPattern();
		Var sub = new Var();
		Var pred = new Var();
		Var obj = new Var();
		
		HashMap<String, ValueConstant> index = new HashMap<String, ValueConstant>();
		for (ExtensionElem extElem: extension.getElements()) {
			try {
				index.put(extElem.getName(), (ValueConstant) extElem.getExpr());
			} catch (ClassCastException e) {
				e.printStackTrace();
				throw new RuntimeException("Unsupported construct query rule: \n" + constructQuery.toString());
			}
		}
		
		for (ProjectionElem elem: projection.getProjectionElemList().getElements()) {
			Var currentComponent = null;
			if (elem.getTargetName().equals("subject")) {
				currentComponent = sub;
			} else if (elem.getTargetName().equals("predicate")) {
				currentComponent = pred;
			} else if (elem.getTargetName().equals("object")) {
				currentComponent = obj;
			} else {
				throw new RuntimeException("Unsupported construct query rule: " + constructQuery.toString());
			}
			
			ValueConstant valueConstant = index.get(elem.getSourceName());
			if (valueConstant != null) {
				currentComponent.setConstant(true);
				currentComponent.setValue(valueConstant.getValue());
				currentComponent.setName(elem.getSourceName());
			} else {
				currentComponent.setConstant(false);
				currentComponent.setName(elem.getSourceName());
			}
		}
		
		consequent.setSubjectVar(sub);
		consequent.setPredicateVar(pred);
		consequent.setObjectVar(obj);

		this.consequent = consequent;
	}

	public Rule getFreshRule() throws Exception {
		StatementPattern consequentClone = consequent.clone();
		TupleExpr antecedentClone = antecedent.clone();

		Set<Var> vars = new HashSet<Var>();
		// vars.addAll(consequentClone.getVarList());
		VarCollector vis = new VarCollector();

		antecedentClone.visit(vis);
		consequentClone.visit(vis);

		vars.addAll(vis.getCollectedVars());

		for (Var var : vars) {
			if (var.isConstant())
				continue;

//			Var newVar = new Var();
			UUID id = UUID.randomUUID();
			
//			String uniqueStringForValue = Integer.toString(id.toString());
			String uniqueStringForValue = Integer.toHexString(id.toString().hashCode());
			
//			newVar.setName(var.getName() + "_" + uniqueStringForValue.toString());
//			newVar.setAnonymous(var.isAnonymous());

			VarRenamer renamer = new VarRenamer(var.getName(), var.getName() + "_" + uniqueStringForValue);
			antecedentClone.visit(renamer);
			consequentClone.visit(renamer);
		}

		Rule result = new Rule();
		result.antecedent = antecedentClone;
		result.consequent = consequentClone;
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

	protected class VarCollector extends QueryModelVisitorBase<VisitorException> {

		private final Set<Var> collectedVars = new HashSet<Var>();

		@Override
		public void meet(Var var) {
			collectedVars.add(var);
		}

		/**
		 * @return Returns the collectedVars.
		 */
		public Set<Var> getCollectedVars() {
			return collectedVars;
		}

	}
}
