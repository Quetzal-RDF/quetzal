package com.ibm.research.rdf.store.sparql11.model;

public class SeqPath extends BinaryPathOp {

	public SeqPath(Path left, Path right) {
		super(left, right);
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
