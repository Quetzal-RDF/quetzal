package com.ibm.rdf.store.sparql11.model;

public interface PathMapper<X> {
	X visit(AltPath p);
	X visit(SeqPath p);
	X visit(NegatedProperySetPath p);
	X visit(SimplePath p);
	X visit(InvPath p);
	X visit(OneOrMorePath p);
	X visit(ZeroOrMorePath p);
	X visit(ZeroOrOnePath p);
}
