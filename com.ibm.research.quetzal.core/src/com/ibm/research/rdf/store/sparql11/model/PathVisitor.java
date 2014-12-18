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
 package com.ibm.research.rdf.store.sparql11.model;

public interface PathVisitor {
	void visit(AltPath p);
	void visit(SeqPath p);
	void visit(NegatedProperySetPath p);
	void visit(SimplePath p);
	void visit(InvPath p);
	void visit(OneOrMorePath p);
	void visit(ZeroOrMorePath p);
	void visit(ZeroOrOnePath p);
}
