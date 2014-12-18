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
 package com.ibm.research.rdf.store.hashing;

import java.util.HashSet;
import java.util.Set;

/**
 * Edge represents and edge in the edgeset.
 * 
 * It encapsulate the count of predicates in the the edge, the count
 * of occurrence of the predicates and the list of the predicates in the edge.
 * 
 * @author prsahoo
 *
 */
public class Edge implements Comparable<Edge>{
	
	//The count of occurrence of the edge in the dataset
	private long occurrence;
	
	//The list of predicates
	private Set<String> predicates;

	/**
	 * Constructor 
	 * 
	 * @param occurrence
	 */
	public Edge()
	{
		this(new HashSet<String>(),1);
	}
	
	/**
	 * Constructor
	 * 
	 * @param predicateCount
	 * @param predicateOccurence
	 * @param predicates
	 */
	public Edge(Set<String> predicates,long predicateOccurence)
	{
		this.occurrence = predicateOccurence;
		this.predicates = predicates;
	}
	
	/**
	 * @return the predicateCount
	 */
	public long getPredicateCount() {
		return predicates.size();
	}

	/**
	 * @return the occurrence
	 */
	public long getOccurrence() {
		return occurrence;
	}

	/**
	 * @param occurence the occurrence to set
	 */
	public void setOccurrence(long occurence) {
		this.occurrence = occurence;
	}

	/**
	 * @return the predicates
	 */
	public Set<String> getPredicates() {
		return predicates;
	}

	/**
	 * @param predicates the predicates to set
	 */
	public void setPredicates(Set<String> predicates) {
		this.predicates = predicates;
	}

	/**
	 * Adds predicates to the list of predicates and increments the count
	 * 
	 * @param predicate
	 */
	public void addPredicate(String predicate)
	{
		predicates.add(predicate);
	}

	@Override
	public boolean equals(Object edge) {
		
		if(edge == null || ! (edge instanceof Edge))
		{
			return false;
		}
		
		Edge param = (Edge)edge;
		return (this.predicates.containsAll(param.getPredicates()) && param.predicates.containsAll(this.getPredicates()));
	}

	@Override
	public String toString() {
		return getPredicateCount() + "\t" + occurrence + "\t" + predicates ;
	}

	public int compareTo(Edge edge) {
		
		if (edge == null) {
			throw new IllegalArgumentException(
					"Can not compare with null object");
		}

		if (this.getPredicateCount() == edge.getPredicateCount()) {
			
			if(this.getOccurrence() == edge.getOccurrence())
			{
				return this.predicates.toString().compareTo(edge.predicates.toString());
			}
			else
			{
				return (int) Math.signum(this.occurrence
						- edge.getOccurrence());
			}
		} else {
			return (int) Math.signum(this.getPredicateCount()
					- edge.getPredicateCount());
		}
	}

}
