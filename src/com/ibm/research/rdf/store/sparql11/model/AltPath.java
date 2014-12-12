package com.ibm.research.rdf.store.sparql11.model;
/**
 * Represent the property path expression: (path1 | path2)
 * @author fokoue
 *
 */
public class AltPath extends BinaryPathOp {

	public AltPath(Path left, Path right) {
		super(left, right);
	}
	
	@Override
	public <X> X map(PathMapper<X> visitor) {
		return visitor.visit(this);
	}

	@Override
	public void visit(PathVisitor visitor) {
		visitor.visit(this);
	}
	
}
