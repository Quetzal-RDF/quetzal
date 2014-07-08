package com.ibm.rdf.store.sparql11.model;

public class ZeroOrMorePath extends UnaryPathOp {

	public ZeroOrMorePath(Path subPath) {
		super(subPath);
	}
	@Override
	public void visit(PathVisitor visitor) {
		visitor.visit(this);
	}
	@Override
	public <X> X map(PathMapper<X> visitor) {
		return visitor.visit(this);
	}
	/**
	 *returns whether this path directly contains  a "*" or "+" path modifier.
	 * @return
	 */
	public boolean isDirectlyRecursive() {
		return true;
	}

}
