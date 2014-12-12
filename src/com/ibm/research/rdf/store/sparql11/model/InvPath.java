package com.ibm.research.rdf.store.sparql11.model;

public class InvPath extends UnaryPathOp {

	public InvPath(Path subPath) {
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
	
	
	
}
