package com.ibm.research.rdf.store.sparql11.model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * a hierarchical identifier for quick searches through a tree structure.
 */
public class HierarchicalIdentifier extends ArrayList<Integer> {

	private static final long serialVersionUID = 859498340901345584L;

	public HierarchicalIdentifier() {
		super();
		this.add(0);
	}

	public HierarchicalIdentifier(Collection<? extends Integer> collection) {
		super(collection);
	}

	public HierarchicalIdentifier(int capacity) {
		super(capacity);
	}

	public HierarchicalIdentifier getParent() { 
		if(size() == 0) return null;
		else return new HierarchicalIdentifier(this.subList(0, size() - 1));
	}
	
	public boolean isAncestorOf(HierarchicalIdentifier other) {
		return (other.size() >= size()) && other.subList(0, size()).equals(this);
	}
	
	public boolean isDescendantOf(HierarchicalIdentifier other) {
		return other.isAncestorOf(this);
	}
}
