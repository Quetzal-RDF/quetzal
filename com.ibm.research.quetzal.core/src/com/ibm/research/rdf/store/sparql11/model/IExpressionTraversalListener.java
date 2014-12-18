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

/**
 * interface for classes that are to be used to traverse the expression tree. Methods of this class are called during a traversal
 */
public interface IExpressionTraversalListener {
	public void startExpression(Expression e);
	public void endExpression(Expression e);
}
