package com.ibm.rdf.store.sparql11.model;

/**
 * interface for classes that are to be used to traverse the expression tree. Methods of this class are called during a traversal
 */
public interface IExpressionTraversalListener {
	public void startExpression(Expression e);
	public void endExpression(Expression e);
}
