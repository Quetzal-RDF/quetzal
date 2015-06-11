package com.ibm.research.sparql.rewriter;

import java.util.Map;

import com.hp.hpl.jena.graph.Node;



/**
 * @author Kavitha Srinivas <ksrinivs@us.ibm.com>
 * @author Mariano Rodriguez <mrodrig@us.ibm.com>
 * 
 */public interface SubstitutionForJena {

	boolean compose(Node original, Node substituion);

	Node get(Node Node);

	Map<Node, Node> getMap();

	public boolean isEmpty();
}