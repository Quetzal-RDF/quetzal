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
import java.util.Map;
import java.util.Set;

import com.hp.hpl.jena.graph.Node;




/**
 * @author Kavitha Srinivas <ksrinivs@us.ibm.com>
 * @author Mariano Rodriguez <mrodrig@us.ibm.com>
 * 
 */public class SubstitutionImplForJena implements SubstitutionForJena {

	Map<Node, Node> map = new HashMap<Node, Node>();

	/**
	 * Accumulates the different subsititions for a triple and applies them intelligently
		if we have a substitution 
		x/y
		and in a next step we compute a new substitution
		y/z
		then the composition should be
		x/z, y/z
		and not
		x/y, y/z **/
	@Override
	public boolean compose(Node original, Node substitution) {
		SubstitutionForJena unifier = StatementUnifierForJena.getUnifier(original, substitution);
		Set<Node> trivialRemove = new HashSet<Node>();

		if (unifier == null)
			return false;
		if (unifier instanceof NeutralSubstitution)
			return true;
		for (Node key : map.keySet()) {
			Node value = map.get(key);
			if (value.equals(original))
				if (key.equals(substitution)) {
					// existing substitition now trivial, remove it.
					trivialRemove.add(key);
				} else {
					map.put(key, substitution);
				}
		}
		map.put(original, substitution);
		map.keySet().removeAll(trivialRemove);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.research.sparql.rewriter.StatementUnifier.Substitution#get
	 * (org.openrdf.query.algebra.Node)
	 */
	@Override
	public Node get(Node node) {
		// Jena is idiotic and doesnt realize when two nodes are the same (e.g. have the same variable)
		for (Map.Entry<Node, Node> e : map.entrySet()) {
			if (e.getKey().getName().equals(node)) {
				return e.getValue();
			}
		}
		return map.get(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.research.sparql.rewriter.StatementUnifier.Substitution#getMap
	 * ()
	 */
	@Override
	public Map<Node, Node> getMap() {
		return map;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.research.sparql.rewriter.StatementUnifier.Substitution#isEmpty
	 * ()
	 */
	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}
	
	public String toString() {
		return map.toString();
	}

}