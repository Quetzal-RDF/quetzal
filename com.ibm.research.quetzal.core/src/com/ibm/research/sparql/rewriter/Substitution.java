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

import java.util.Map;

import org.openrdf.query.algebra.Var;

/**
 * @author Mariano Rodriguez <mrodrig@us.ibm.com>
 * 
 */
public interface Substitution {

	boolean compose(Var original, Var substituion);

	Var get(Var var);

	Map<Var, Var> getMap();

	public boolean isEmpty();
}