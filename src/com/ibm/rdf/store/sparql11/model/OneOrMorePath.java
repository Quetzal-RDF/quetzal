package com.ibm.rdf.store.sparql11.model;

public  class OneOrMorePath extends UnaryPathOp {

	public OneOrMorePath(Path subPath) {
		super(subPath);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void visit(PathVisitor visitor) {
		visitor.visit(this);
	}
	
	/**
	 *returns whether this path directly contains  a "*" or "+" path modifier.
	 * @return
	 */
	public boolean isDirectlyRecursive() {
		return true;
	}
	@Override
	public <X> X map(PathMapper<X> visitor) {
		return visitor.visit(this);
	}
	

}
