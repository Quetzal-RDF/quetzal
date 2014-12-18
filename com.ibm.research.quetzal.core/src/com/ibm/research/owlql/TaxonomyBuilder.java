/******************************************************************************
 * Copyright (c) 2015 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *****************************************************************************/
 package com.ibm.research.owlql;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.wala.util.collections.Pair;
import com.ibm.wala.util.graph.Graph;
import com.ibm.wala.util.graph.impl.SlowSparseNumberedGraph;
import com.ibm.wala.util.graph.traverse.SCCIterator;

public class TaxonomyBuilder<T> {
	private static final Logger logger = LoggerFactory.getLogger(TaxonomyBuilder.class);

	public interface SubsumptionComputation<T> {
		public boolean isSubumedBy(T sub, T sup);
		/***
		 */
		public Set<T> getToldSubsumers(T sub);
	}
	
	public class TaxoNode {
		private T representative;
		private int hashcode;
		private Set<T> equivElts;
		private TaxoNode(T elt) {
			representative = elt;
			equivElts = new HashSet<T>();
			addEquivalentElt(elt);
			hashcode = computeHashCode();
		}
		
		private void addEquivalentElt(T elt) {
			equivElts.add(elt);
			elt2Node.put(elt, this);
		}
		public T getRepresentative() {
			return representative;
		}
		public Set<? extends T> getEquivalentElements() {
			return Collections.unmodifiableSet(equivElts);
		}
		public Iterator<? extends TaxoNode> getSubs() {
			return lattice.getPredNodes(this);
		}
		
		public Iterator<? extends TaxoNode> getSupers() {
			return lattice.getSuccNodes(this);
		}
		@Override
		public int hashCode() {
			return hashcode;
		}
		private int computeHashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime
					* result
					+ ((representative == null) ? 0 : representative.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TaxoNode other = (TaxoNode) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (representative == null) {
				if (other.representative != null)
					return false;
			} else if (!representative.equals(other.representative))
				return false;
			return true;
		}
		
		private TaxonomyBuilder getOuterType() {
			return TaxonomyBuilder.this;
		}
		@Override
		public String toString() {
			return "TaxoNode [representative=" + representative
					+ ", equivElts=" + equivElts + "]";
		}
		
		
	}
	
	protected Graph<TaxoNode> lattice;
	protected Map<T, TaxoNode> elt2Node;
	protected Collection<? extends T> elements;
	protected T top;
	protected TaxoNode topNode;
	protected T bottom;
	protected TaxoNode bottomNode;	
	protected Map<T, Set<T>> elt2finalSubs;
	protected Map<T, Set<T>> elt2finalSupers;
	protected boolean built;
	protected SubsumptionComputation<T> subcomputation;
	
	
	public TaxonomyBuilder(Collection<? extends T> elements, T top, T bottom,
			SubsumptionComputation<T> subcomputation) {
		super();
		this.elements = elements;
		this.top = top;
		this.bottom = bottom;
		this.subcomputation = subcomputation;
		this.elt2finalSubs = new HashMap<T, Set<T>>();
		this.elt2finalSupers = new HashMap<T, Set<T>>();
		built =false;
		init();
	}

	protected void init() {
		elt2Node = new HashMap<T, TaxoNode>();
		//TODO: Talk to Julian about using a better graph implementation
		lattice =  new SlowSparseNumberedGraph<TaxoNode>(3);
		// add top and bottom
		Pair<TaxoNode, TaxoNode> p =addEdge(bottom, top);		
		topNode = p.snd;
		bottomNode = p.fst;
	}	
	
	
	protected Graph<TaxoNode> getLattice() {
		return lattice;
	}
	/**
	 * 
	 */
	public Set<TaxoNode> getNodes() {
		if (!built) {
			throw new RuntimeException("build() method should be invoked first!");
		}
		Set<TaxoNode> ret = new HashSet<TaxoNode>(elt2Node.values());
		return ret;
	}
	/**
	 *
	 * @param t
	 * @return
	 */
	public TaxoNode get(T t) {
		if (!built) {
			throw new RuntimeException("build() method should be invoked first!");
		}
		return elt2Node.get(t);
	}
	
	public void build() {
		//TODO: Talk to Julian about using a better graph implementation
		Graph<T> toldSubsumerGraph =  new SlowSparseNumberedGraph<T>(3);
		for (T c: elements) {
			if (!toldSubsumerGraph.containsNode(c)) {
				toldSubsumerGraph.addNode(c);
			}
			Set<T> toldSubsumers = new HashSet<T>(subcomputation.getToldSubsumers(c));
			toldSubsumers.retainAll(elements);
			for (T s: toldSubsumers) {
				if (!toldSubsumerGraph.containsNode(s)) {
					toldSubsumerGraph.addNode(s);
				}
				toldSubsumerGraph.addEdge(s, c);
			}
		}
		SCCIterator<T> sortedIt = new SCCIterator<T>(toldSubsumerGraph);
	/*	List<T> sortedElements = new ArrayList<T>(elements);
		Collections.sort(sortedElements, new Comparator<T>() {
			@Override
			public int compare(T t0, T t1) {
				int s0 = subcomputation.getToldSubsumers(t0).size();
				int s1 = subcomputation.getToldSubsumers(t1).size();
				return s0<s1? 1: s0==s1? 0: -1;
			}
			
		});*/
		//for (T c: elements) {
		for (;sortedIt.hasNext();) {
			for (T c : sortedIt.next())
			{
				if (logger.isDebugEnabled()) {
					logger.debug("Inserting element {} in taxonomy", c);
					logger.debug("Taxonomy:\n{}", lattice);
				}
				Set<TaxoNode> subsumers = topSearch(c);
				Set<TaxoNode> subsumees = bottomSearch(c, subsumers);
				TaxoNode nc = null;
				// check if c is equivalent to one of its subsumers 
				// or subsumes
				Set<TaxoNode> subsumeAndSubsumer = new HashSet<TaxonomyBuilder<T>.TaxoNode>(subsumees);
				subsumeAndSubsumer.retainAll(subsumers);
				assert subsumeAndSubsumer.size()<=1 : subsumeAndSubsumer;
				if (!subsumeAndSubsumer.isEmpty()) {
					TaxoNode equiv = subsumeAndSubsumer.iterator().next();
					// equivalence
					equiv.addEquivalentElt(c);
					nc = equiv;
				}	 
				//
				
				if (nc ==null) {
					nc = addNode(c);
				} else {
					// c is equivalent to an existing node.
					continue;
				}
				for (TaxoNode sup: subsumers) {
					assert !nc.equals(sup) /*|| nc == sup*/ : nc+"\n"+sup+"\n"+(nc==sup);
					/*if (nc==sup) {
						assert nc.getRepresentative().equals(top) 
							|| nc.getRepresentative().equals(bottom); 
					}*/
					lattice.addEdge(nc, sup);
					// remove edge (sub, sup) where sub is in the set of subsumees of nc
					List<TaxoNode> subToRemove = new LinkedList<TaxoNode>();
					for (Iterator<? extends TaxoNode> it = lattice.getPredNodes(sup);it.hasNext();) {
						TaxoNode sub = it.next();
						if (subsumees.contains(sub)) {
							subToRemove.add(sub);
						}
					}
					for (TaxoNode sub: subToRemove) {
						lattice.removeEdge(sub, sup);
					}
					//
				}
				for (TaxoNode sub: subsumees) {
					assert !nc.equals(sub) : nc+"\n"+sub;
					lattice.addEdge(sub, nc);
					// Remove edge (sub, sup) where sup is in the set of subsumers of nc.
					// This is not needded bc it (sub, sup) must have already been removed
					// when processing subsumers
					/*for (Iterator<? extends TaxoNode> it = lattice.getSuccNodes(sub);it.hasNext();) {
						TaxoNode sup = it.next();
						if (subsumers.contains(sup)) {
							lattice.removeEdge(sub, sup);
						}
					}*/
					//
								
				}
			}		
		}
		built = true;
	}
	protected TaxoNode addNode(T elt) {
		TaxoNode ret =elt2Node.get(elt);
		if (ret==null) {
			ret = new TaxoNode(elt);
			elt2Node.put(elt, ret);
			lattice.addNode(ret);
		}
		return ret;
	}
	
	protected Pair<TaxoNode, TaxoNode> addEdge(T sub, T sup) {
		assert !sub.equals(sup) : sub+"\n"+sup;
		TaxoNode subN = addNode(sub);
		TaxoNode supN = addNode(sup);
		if (!lattice.hasEdge(subN, supN)) {
			lattice.addEdge(subN, supN);
		}
		return Pair.make(subN, supN);
	}
	
	/**
	 * performs the top search to find all the most specific subsumers of elt already in the 
	 * lattice
	 * @param elt
	 * @return
	 */
	protected Set<TaxoNode> topSearch(T elt) {
		Set<TaxoNode> ret = new HashSet<TaxoNode>();
		// check that two nodes are not compared more than once
		boolean assertionsEnable = false;
		assert assertionsEnable = true;
		Set<Pair<T, T>> comparisons = null;
		if (assertionsEnable) {
			comparisons = new HashSet<Pair<T,T>>();
		}
		//
		List<TaxoNode> queue = new LinkedList<TaxoNode>();
		Set<TaxoNode> toldSubsumers = new HashSet<TaxoNode>();
		for (T sup : subcomputation.getToldSubsumers(elt)) {
			TaxoNode n = elt2Node.get(sup);
			if (n!=null) {
				getSubsumersOrSelf(n, toldSubsumers);
			}
		}
		Set<TaxoNode> nonSubsumers = new HashSet<TaxoNode>();
		Set<TaxoNode> discovered = new HashSet<TaxoNode>();
		// when a node is discovered for the first time it is compared to elt
		// so nodes in discovered are known to be either subsumer of elt or not
		queue.add(topNode);
		while (!queue.isEmpty()) {
			TaxoNode t = queue.remove(0);
			boolean supFound = false;
			for (Iterator<? extends TaxoNode> it = lattice.getPredNodes(t);it.hasNext();) {
				TaxoNode sub = it.next();
				boolean subIsKnownNonSubsumers = nonSubsumers.contains(sub);
				if (discovered.contains(sub) && !subIsKnownNonSubsumers) {
					// sub is known to be a subsumer of elt 
					supFound = true;
				} else	if (!subIsKnownNonSubsumers) {
					assert comparisons.add(Pair.make(elt, sub.getRepresentative())) 
					: comparisons+"\n"+elt+"\n"+sub.getRepresentative();
					// not visited, status unknown
					if (toldSubsumers.contains(sub) 
					|| subcomputation.isSubumedBy(elt, sub.getRepresentative())) {
						queue.add(sub);
						supFound = true;					
					} else {
						getSubsumeesOrSelf(sub, nonSubsumers);
					}
					discovered.add(sub);
				}
			}
			if (!supFound) {
				ret.add(t);
			}
			
		}
		return ret;
	}
	
	/**
	 * performs the bottom search to find all the most general subsumees of elt already in the 
	 * lattice
	 * @param elt
	 * @return
	 */
	protected Set<TaxoNode> bottomSearch(T elt, Set<TaxoNode> topSearchSubsumers) {
		
		
		Set<TaxoNode> ret = new HashSet<TaxoNode>();
		Set<TaxoNode> nonSubsumees = new HashSet<TaxoNode>();
		
		for (TaxoNode n: topSearchSubsumers) {
			// strict subsumers of topSearchSubsumers 
			// not allowed
			boolean self = nonSubsumees.contains(n);
			getSubsumersOrSelf(n, nonSubsumees);
			if (!self) {
				nonSubsumees.remove(n);
			}
		}
	
		// check that two nodes are not compared more than once
		boolean assertionsEnable = false;
		assert assertionsEnable = true;
		Set<Pair<T, T>> comparisons = null;
		if (assertionsEnable) {
			comparisons = new HashSet<Pair<T,T>>();
		}
		//
		List<TaxoNode> queue = new LinkedList<TaxoNode>();
		Set<TaxoNode> discovered = new HashSet<TaxoNode>();
		// when a node is discovered for the first time it is compared to elt
		// so nodes in discovered are known to be either subsumee of elt or not
		queue.add(bottomNode);
		while (!queue.isEmpty()) {
			TaxoNode t =queue.remove(0);
			boolean subFound = false;
			for (Iterator<? extends TaxoNode> it = lattice.getSuccNodes(t);it.hasNext();) {
				TaxoNode sup = it.next();
				boolean supIsKnownNonSubsumees = nonSubsumees.contains(sup);
				if (discovered.contains(sup) && !supIsKnownNonSubsumees) {
					// known to be a subsumee
					subFound = true; 
				} else 	if (!supIsKnownNonSubsumees) {
					assert comparisons.add(Pair.make(sup.getRepresentative(), elt)) 
						: comparisons+"\n"+sup.getRepresentative()+"\n"+ elt ;
					if (subcomputation.isSubumedBy(sup.getRepresentative(), elt)) {
						queue.add(sup);
						subFound = true;
					} else {
						getSubsumersOrSelf(sup, nonSubsumees);
					}
					discovered.add(sup);
				}
			}
			if (!subFound) {
				ret.add(t);
			}
			
		}
		return ret;
	}
	protected void getSubsumeesOrSelf(TaxoNode node, Set<TaxoNode> alreadyVisited) {
		traverseLattice(node, alreadyVisited, true);
	}
	protected void getSubsumersOrSelf(TaxoNode node, Set<TaxoNode> alreadyVisited) {
		traverseLattice(node, alreadyVisited, false);
	}
	
	protected void traverseLattice(TaxoNode node, Set<TaxoNode> alreadyVisited, boolean fromSuperToSub) {
		if (alreadyVisited == null) {
			alreadyVisited = new HashSet<TaxonomyBuilder<T>.TaxoNode>();
		}
		//Set<TaxoNode> ret = new HashSet<TaxonomyBuilder<T>.TaxoNode>();
		List<TaxoNode> queue = new LinkedList<TaxonomyBuilder<T>.TaxoNode>();
		queue.add(node);
		while (!queue.isEmpty()) {
			TaxoNode t = queue.remove(0);
			//ret.add(t);
			alreadyVisited.add(t);
			for (Iterator<? extends TaxoNode> it = fromSuperToSub? lattice.getPredNodes(t): lattice.getSuccNodes(t);it.hasNext();) {
				TaxoNode next = it.next();
				if (!alreadyVisited.contains(next)) {
					queue.add(next);
				}
			}
		}
		//return ret;
	}
	/**
	 * returns the direct and indirect subsumees of a given element. 
	 * Returns null if the element is not in the built taxonomy.
	 * 
	 * NOTE: Should only be invoked after calling {@link #build()}.
	 * @param sup
	 * @return
	 */
	public Set<T> getSubsumeesOrSelf(T sup) {
		if (!built) {
			throw new RuntimeException("build() method should be invoked first!");
		}
		TaxoNode node = get(sup);
		if (node == null ) {
			return null;
		}	
		Set<T> ret = elt2finalSubs.get(sup);
		if (ret == null) {
			ret = new HashSet<T>();
			Set<TaxoNode> subNodes = new HashSet<TaxoNode>();
			getSubsumeesOrSelf(node, subNodes);
			for (TaxoNode n: subNodes) {
				ret.addAll(n.getEquivalentElements());
			}
			elt2finalSubs.put(sup, ret);
		}
		return ret;
	}
	
	/**
	 * returns the direct and indirect subsumers of a given element. 
	 * Returns null if the element is not in the built taxonomy.
	 * NOTE: Should only be invoked after calling {@link #build()}.
	 *
	 * @param sub
	 * @return
	 */
	public Set<T> getSubsumersOrSelf(T sub) {
		if (!built) {
			throw new RuntimeException("build() method should be invoked first!");
		}
		TaxoNode node = get(sub);
		if (node == null ) {
			return null;
		}	
		Set<T> ret = elt2finalSupers.get(sub);
		if (ret == null) {
			ret = new HashSet<T>();
			Set<TaxoNode> superNodes = new HashSet<TaxoNode>();
			getSubsumersOrSelf(node, superNodes);
			for (TaxoNode n: superNodes) {
				ret.addAll(n.getEquivalentElements());
			}
			elt2finalSupers.put(sub, ret);
		}
		return ret;
	}
	/**
	 * returns whether sub is subsumes by sup. If sub or sup is not in the 
	 * built taxonomy an RuntimeException is thrown
	 * NOTE: Should only be invoked after calling {@link #build()}.
	 *
	 * @param sub
	 * @param sup
	 * @return
	 */
	public boolean isSubsumedBy(T sub, T sup) {
		if (!built) {
			throw new RuntimeException("build() method should be invoked first!");
		}
		if (get(sub) == null) {
			throw new RuntimeException(sub +" is not in the taxonomy");
		}
		if (get(sup) == null) {
			throw new RuntimeException(sup +"is not in the taxonomy");
		}
		return getSubsumeesOrSelf(sup).contains(sub);
	}
	
	public T getTop() {
		return  top;
	}
	public T getBottom() {
		return bottom;
	}
	public TaxoNode getTopNode() {
		return topNode;
	}
	public TaxoNode getBottomNode() {
		return bottomNode;
	}
	
	

	


	

}
