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
 package com.ibm.research.rdf.store.config;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import com.ibm.research.rdf.store.schema.Pair;
import com.ibm.research.rdf.store.sparql11.planner.statistics.AverageStatistic;


public class StatisticsImpl implements Serializable, Statistics {
	private static final long serialVersionUID = -8203091125770008140L;

	/* ---------------------------------------------- STATISTICS ----------------------------------------------- */
	private boolean perGraphStatistics = false;
	
	private BigInteger tripleCount = BigInteger.ZERO;
	
	private AverageStatistic subjectStatistic = new AverageStatistic();
	private AverageStatistic predicateStatistic = new AverageStatistic();
	private AverageStatistic objectStatistic = new AverageStatistic();
	private AverageStatistic objectPredicatePairs = new AverageStatistic();
	private AverageStatistic subjectPredicatePairs = new AverageStatistic();
	private AverageStatistic graphStatistic = new AverageStatistic();
	
	private Map<String, BigInteger> topSubjects = new HashMap<String, BigInteger>();
	private Map<String, BigInteger> topPredicates = new HashMap<String, BigInteger>();
	private Map<String, BigInteger> topObjects = new HashMap<String, BigInteger>();
	private Map<Pair<String>, BigInteger> topObject_Predicate_Pairs = new HashMap<Pair<String>, BigInteger>();
	private Map<Pair<String>, BigInteger> topSubject_Predicate_Pairs = new HashMap<Pair<String>, BigInteger>();
	private Map<String, BigInteger> topGraphs = new HashMap<String, BigInteger>();
	
	public String toString() {
		StringBuffer buf = new StringBuffer().append("Subject statistic:").append(getSubjectStatistic()).append("\n");
		buf.append("Object statistic:").append(getObjectStatistic()).append("\n");
		buf.append("Predicate statistic:").append(getPredicateStatistic()).append("\n");
		buf.append("ObjectPredicatePairs:").append(getObjectPredicatePairs()).append("\n");
		buf.append("SubjectPredicatePairs:").append(getSubjectPredicatePairs()).append("\n");
		buf.append("Triplecount:").append(getTripleCount()).append("\n");
		
		buf.append(getMap("topSubjects:", getTopSubjects()));
		buf.append(getMap("topPredicates:", getTopPredicates()));
		buf.append(getMap("topObjects:", getTopObjects()));
		buf.append(getMapPair("topSubjectPredicate:", getTopSubject_Predicate_Pairs()));
		buf.append(getMapPair("topObjectPredicates:", getTopObject_Predicate_Pairs()));
	
		
		return buf.toString();
		
	}
	
	private String getMap(String s, Map<String, BigInteger> m) {
		StringBuffer buf = new StringBuffer(s);
		for (Map.Entry<String, BigInteger> e : m.entrySet()) {
			buf.append(e.getKey()).append(" ").append(e.getValue().toString()).append("\n");
		}
		return buf.toString();
	}

	private String getMapPair(String s, Map<Pair<String>, BigInteger> m) {
		StringBuffer buf = new StringBuffer(s);
		for (Map.Entry<Pair<String>, BigInteger> e : m.entrySet()) {
			buf.append(e.getKey()).append(" ").append(e.getValue().toString()).append("\n");
		}
		return buf.toString();
	}

	/* (non-Javadoc)
	 * @see com.ibm.research.rdf.store.config.Statistics#getTopSubjects()
	 */
	@Override
	public Map<String, BigInteger> getTopSubjects() {
		return topSubjects;
	}

	/* (non-Javadoc)
	 * @see com.ibm.research.rdf.store.config.Statistics#setTopSubjects(java.util.Map)
	 */
	@Override
	public void setTopSubjects(Map<String, BigInteger> topSubjects) {
		this.topSubjects = topSubjects;
	}

	/* (non-Javadoc)
	 * @see com.ibm.research.rdf.store.config.Statistics#getTopPredicates()
	 */
	@Override
	public Map<String, BigInteger> getTopPredicates() {
		return topPredicates;
	}

	/* (non-Javadoc)
	 * @see com.ibm.research.rdf.store.config.Statistics#setTopPredicates(java.util.Map)
	 */
	@Override
	public void setTopPredicates(Map<String, BigInteger> topPredicates) {
		this.topPredicates = topPredicates;
	}

	/* (non-Javadoc)
	 * @see com.ibm.research.rdf.store.config.Statistics#getTopObjects()
	 */
	@Override
	public Map<String, BigInteger> getTopObjects() {
		return topObjects;
	}

	/* (non-Javadoc)
	 * @see com.ibm.research.rdf.store.config.Statistics#setTopObjects(java.util.Map)
	 */
	@Override
	public void setTopObjects(Map<String, BigInteger> topObjects) {
		this.topObjects = topObjects;
	}

	/* (non-Javadoc)
	 * @see com.ibm.research.rdf.store.config.Statistics#getTopObject_Predicate_Pairs()
	 */
	@Override
	public Map<Pair<String>, BigInteger> getTopObject_Predicate_Pairs() {
		return topObject_Predicate_Pairs;
	}

	/* (non-Javadoc)
	 * @see com.ibm.research.rdf.store.config.Statistics#setTopObject_Predicate_Pairs(java.util.Map)
	 */
	@Override
	public void setTopObject_Predicate_Pairs(
			Map<Pair<String>, BigInteger> topObject_Predicate_Pairs) {
		this.topObject_Predicate_Pairs = topObject_Predicate_Pairs;
	}

	/* (non-Javadoc)
	 * @see com.ibm.research.rdf.store.config.Statistics#getTopSubject_Predicate_Pairs()
	 */
	@Override
	public Map<Pair<String>, BigInteger> getTopSubject_Predicate_Pairs() {
		return topSubject_Predicate_Pairs;
	}

	/* (non-Javadoc)
	 * @see com.ibm.research.rdf.store.config.Statistics#setTopSubject_Predicate_Pairs(java.util.Map)
	 */
	@Override
	public void setTopSubject_Predicate_Pairs(
			Map<Pair<String>, BigInteger> topSubject_Predicate_Pairs) {
		this.topSubject_Predicate_Pairs = topSubject_Predicate_Pairs;
	}

	/* (non-Javadoc)
	 * @see com.ibm.research.rdf.store.config.Statistics#getTopGraphs()
	 */
	@Override
	public Map<String, BigInteger> getTopGraphs() {
		return topGraphs;
	}

	/* (non-Javadoc)
	 * @see com.ibm.research.rdf.store.config.Statistics#setTopGraphs(java.util.Map)
	 */
	@Override
	public void setTopGraphs(Map<String, BigInteger> topGraphs) {
		this.topGraphs = topGraphs;
	}

	/* (non-Javadoc)
	 * @see com.ibm.research.rdf.store.config.Statistics#isPerGraphStatistics()
	 */
	@Override
	public boolean isPerGraphStatistics() {
		return perGraphStatistics;
	}

	/* (non-Javadoc)
	 * @see com.ibm.research.rdf.store.config.Statistics#setPerGraphStatistics(boolean)
	 */
	@Override
	public void setPerGraphStatistics(boolean perGraphStatistics) {
		this.perGraphStatistics = perGraphStatistics;
	}

	/* (non-Javadoc)
	 * @see com.ibm.research.rdf.store.config.Statistics#getTripleCount()
	 */
	@Override
	public BigInteger getTripleCount() {
		return tripleCount;
	}

	/* (non-Javadoc)
	 * @see com.ibm.research.rdf.store.config.Statistics#setTripleCount(java.math.BigInteger)
	 */
	@Override
	public void setTripleCount(BigInteger tripleCount) {
		this.tripleCount = tripleCount;
	}

	/* (non-Javadoc)
	 * @see com.ibm.research.rdf.store.config.Statistics#getSubjectStatistic()
	 */
	@Override
	public AverageStatistic getSubjectStatistic() {
		return subjectStatistic;
	}

	/* (non-Javadoc)
	 * @see com.ibm.research.rdf.store.config.Statistics#setSubjectStatistic(com.ibm.research.rdf.store.statistics.AverageStatistic)
	 */
	@Override
	public void setSubjectStatistic(AverageStatistic subjectStatistic) {
		this.subjectStatistic = subjectStatistic;
	}

	/* (non-Javadoc)
	 * @see com.ibm.research.rdf.store.config.Statistics#getPredicateStatistic()
	 */
	@Override
	public AverageStatistic getPredicateStatistic() {
		return predicateStatistic;
	}

	/* (non-Javadoc)
	 * @see com.ibm.research.rdf.store.config.Statistics#setPredicateStatistic(com.ibm.research.rdf.store.statistics.AverageStatistic)
	 */
	@Override
	public void setPredicateStatistic(AverageStatistic predicateStatistic) {
		this.predicateStatistic = predicateStatistic;
	}

	/* (non-Javadoc)
	 * @see com.ibm.research.rdf.store.config.Statistics#getObjectStatistic()
	 */
	@Override
	public AverageStatistic getObjectStatistic() {
		return objectStatistic;
	}

	/* (non-Javadoc)
	 * @see com.ibm.research.rdf.store.config.Statistics#setObjectStatistic(com.ibm.research.rdf.store.statistics.AverageStatistic)
	 */
	@Override
	public void setObjectStatistic(AverageStatistic objectStatistic) {
		this.objectStatistic = objectStatistic;
	}

	/* (non-Javadoc)
	 * @see com.ibm.research.rdf.store.config.Statistics#getObjectPredicatePairs()
	 */
	@Override
	public AverageStatistic getObjectPredicatePairs() {
		return objectPredicatePairs;
	}

	/* (non-Javadoc)
	 * @see com.ibm.research.rdf.store.config.Statistics#setObjectPredicatePairs(com.ibm.research.rdf.store.statistics.AverageStatistic)
	 */
	@Override
	public void setObjectPredicatePairs(AverageStatistic objectPredicatePairs) {
		this.objectPredicatePairs = objectPredicatePairs;
	}

	/* (non-Javadoc)
	 * @see com.ibm.research.rdf.store.config.Statistics#getSubjectPredicatePairs()
	 */
	@Override
	public AverageStatistic getSubjectPredicatePairs() {
		return subjectPredicatePairs;
	}

	/* (non-Javadoc)
	 * @see com.ibm.research.rdf.store.config.Statistics#setSubjectPredicatePairs(com.ibm.research.rdf.store.statistics.AverageStatistic)
	 */
	@Override
	public void setSubjectPredicatePairs(AverageStatistic subjectPredicatePairs) {
		this.subjectPredicatePairs = subjectPredicatePairs;
	}

	/* (non-Javadoc)
	 * @see com.ibm.research.rdf.store.config.Statistics#getGraphStatistic()
	 */
	@Override
	public AverageStatistic getGraphStatistic() {
		return graphStatistic;
	}

	/* (non-Javadoc)
	 * @see com.ibm.research.rdf.store.config.Statistics#setGraphStatistic(com.ibm.research.rdf.store.statistics.AverageStatistic)
	 */
	@Override
	public void setGraphStatistic(AverageStatistic graphStatistic) {
		this.graphStatistic = graphStatistic;
	}
	
}
