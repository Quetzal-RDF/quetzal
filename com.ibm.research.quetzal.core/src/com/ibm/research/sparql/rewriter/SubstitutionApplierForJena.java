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

import java.util.UUID;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.graph.NodeTransform;



/**
 * @author Kavitha Srinivas <ksrinivs@us.ibm.com>
 * @author Mariano Rodriguez <mrodrig@us.ibm.com>
 * 
 */public class SubstitutionApplierForJena implements NodeTransform {

	SubstitutionForJena s;

	public SubstitutionApplierForJena(SubstitutionForJena s) {
		this.s = s;
	}

	@Override
	public Node convert(Node node) {		
		Node node2 = s.get(node);
		if (node2 != null)
			return node2;
		return node;
	}
	

}
