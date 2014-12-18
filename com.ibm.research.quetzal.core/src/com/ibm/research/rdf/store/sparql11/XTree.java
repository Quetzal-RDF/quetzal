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
 package com.ibm.research.rdf.store.sparql11;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;

public class XTree extends CommonTree {

  protected String matched;

  public XTree(Token t) {
    super(t);
    matched = null;
  }
}