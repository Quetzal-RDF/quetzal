package com.ibm.rdf.store.sparql11.model;

public class ZeroOrOnePath extends UnaryPathOp {

	public ZeroOrOnePath(Path subPath) {
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
	@Override
	public boolean isDirectlyZeroOrOnePath() {
		return true;
	}
	
}
