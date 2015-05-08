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



public class NeutralSubstitutionForJena implements SubstitutionForJena {

	@Override
	public boolean compose(Node original, Node substituion) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Node get(Node Node) {
		return null;
	}

	@Override
	public Map<Node, Node> getMap() {
		return new HashMap<Node, Node>();
	}

	public boolean isEmpty() {
		return true;
	}

}