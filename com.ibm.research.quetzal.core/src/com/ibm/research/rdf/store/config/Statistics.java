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

import java.math.BigInteger;
import java.util.Map;

import com.ibm.research.rdf.store.schema.Pair;
import com.ibm.research.rdf.store.sparql11.planner.statistics.AverageStatistic;

public interface Statistics {

	public abstract Map<String, BigInteger> getTopSubjects();

	public abstract void setTopSubjects(Map<String, BigInteger> topSubjects);

	public abstract Map<String, BigInteger> getTopPredicates();

	public abstract void setTopPredicates(Map<String, BigInteger> topPredicates);

	public abstract Map<String, BigInteger> getTopObjects();

	public abstract void setTopObjects(Map<String, BigInteger> topObjects);

	public abstract Map<Pair<String>, BigInteger> getTopObject_Predicate_Pairs();

	public abstract void setTopObject_Predicate_Pairs(
			Map<Pair<String>, BigInteger> topObject_Predicate_Pairs);

	public abstract Map<Pair<String>, BigInteger> getTopSubject_Predicate_Pairs();

	public abstract void setTopSubject_Predicate_Pairs(
			Map<Pair<String>, BigInteger> topSubject_Predicate_Pairs);

	public abstract Map<String, BigInteger> getTopGraphs();

	public abstract void setTopGraphs(Map<String, BigInteger> topGraphs);

	public abstract boolean isPerGraphStatistics();

	public abstract void setPerGraphStatistics(boolean perGraphStatistics);

	public abstract BigInteger getTripleCount();

	public abstract void setTripleCount(BigInteger tripleCount);

	public abstract AverageStatistic getSubjectStatistic();

	public abstract void setSubjectStatistic(AverageStatistic subjectStatistic);

	public abstract AverageStatistic getPredicateStatistic();

	public abstract void setPredicateStatistic(
			AverageStatistic predicateStatistic);

	public abstract AverageStatistic getObjectStatistic();

	public abstract void setObjectStatistic(AverageStatistic objectStatistic);

	public abstract AverageStatistic getObjectPredicatePairs();

	public abstract void setObjectPredicatePairs(
			AverageStatistic objectPredicatePairs);

	public abstract AverageStatistic getSubjectPredicatePairs();

	public abstract void setSubjectPredicatePairs(
			AverageStatistic subjectPredicatePairs);

	public abstract AverageStatistic getGraphStatistic();

	public abstract void setGraphStatistic(AverageStatistic graphStatistic);

}