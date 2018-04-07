package com.ibm.research.rdf.store.sparql11;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import com.ibm.wala.util.collections.Pair;

import kodkod.ast.Relation;
import kodkod.instance.Bounds;

public interface UniverseFactory {

	public Bounds boundUniverse(Set<Relation> liveRelations) throws URISyntaxException;
	
	public Relation atomRelation(Object atom);
	
	public void nodesRelation(Relation r);

	public void ensureIRI(URI iri);
	
	public void ensureLiteral(Pair<String,?> lit);
	
	public void ensureBlankNode(String blankId);
	
	public void addSolution(SolutionRelation solution);
}
