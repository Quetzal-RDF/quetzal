package com.ibm.research.rdf.store.sparql11;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;

public class XTree extends CommonTree {

  protected String matched;

  public XTree(Token t) {
    super(t);
    matched = null;
  }
}