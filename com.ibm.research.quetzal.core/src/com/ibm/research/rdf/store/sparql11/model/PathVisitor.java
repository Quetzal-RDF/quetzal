package com.ibm.research.rdf.store.sparql11.model;

public interface PathVisitor {
	void visit(AltPath p);
	void visit(SeqPath p);
	void visit(NegatedProperySetPath p);
	void visit(SimplePath p);
	void visit(InvPath p);
	void visit(OneOrMorePath p);
	void visit(ZeroOrMorePath p);
	void visit(ZeroOrOnePath p);
}
