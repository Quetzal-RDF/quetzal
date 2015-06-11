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

import com.hp.hpl.jena.graph.Node;




/**
 * @author Kavitha Srinivas <ksrinivs@us.ibm.com>
 * @author Mariano Rodriguez <mrodrig@us.ibm.com>
 * 
 */public class SingletonSubstitutionForJena implements SubstitutionForJena {
	public final Node original;
	public final Node substitution;

	public SingletonSubstitutionForJena(Node original, Node substitution) {
		this.original = original;
		this.substitution = substitution;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.research.sparql.rewriter.StatementUnifier.Substitution#compose
	 * (org.openrdf.query.algebra.Node, org.openrdf.query.algebra.Node)
	 */
	@Override
	public boolean compose(Node original, Node substituion) {
		throw new UnsupportedOperationException();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.research.sparql.rewriter.StatementUnifier.Substitution#get
	 * (org.openrdf.query.algebra.Node)
	 */
	@Override
	public Node get(Node Node) {
		if (original.equals(Node))
			return substitution;
		return null;
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
		HashMap<Node, Node> map = new HashMap<Node, Node>();
		map.put(original, substitution);
		return null;
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
		return false;
	}

	public Node getOriginal() {
		return original;
	}

	public Node getSubstitution() {
		return substitution;
	}
}