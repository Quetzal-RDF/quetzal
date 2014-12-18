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

public interface PathMapper<X> {
	X visit(AltPath p);
	X visit(SeqPath p);
	X visit(NegatedProperySetPath p);
	X visit(SimplePath p);
	X visit(InvPath p);
	X visit(OneOrMorePath p);
	X visit(ZeroOrMorePath p);
	X visit(ZeroOrOnePath p);
}
