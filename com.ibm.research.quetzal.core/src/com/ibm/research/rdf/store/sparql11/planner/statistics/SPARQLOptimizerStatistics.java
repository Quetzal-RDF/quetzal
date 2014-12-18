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
 package com.ibm.research.rdf.store.sparql11.planner.statistics;

import java.util.Map;

import com.ibm.research.rdf.store.config.Statistics;
import com.ibm.research.rdf.store.config.StatisticsImpl;
import com.ibm.research.rdf.store.sparql11.model.BinaryUnion;
import com.ibm.research.rdf.store.sparql11.model.IRI;
import com.ibm.research.rdf.store.sparql11.model.Variable;

/**
 * class necessary as input to the SPARQL optimizer. Includes a mandatory set of global statistics and an optional map of per graph statistics (for each graph) or alternatively one 
 * per graph statistics
 */
public class SPARQLOptimizerStatistics {
	public Statistics globalStatistics;
	public Map<String, StatisticsImpl> perGraphStatistic = null;
	public Statistics perGraphSingleStatistic = null;
	
	public Statistics getGraphStatistics(BinaryUnion<Variable, IRI> graphRestriction) {
		if(graphRestriction == null) return globalStatistics;
		if (graphRestriction.getSecond() != null && perGraphStatistic != null && perGraphStatistic.containsKey(graphRestriction.getSecond().toString())) {
			return perGraphStatistic.get(graphRestriction.getSecond().toString());
		} else if (graphRestriction.getFirst() != null && perGraphSingleStatistic != null) {
			return perGraphSingleStatistic;
		} else if(perGraphSingleStatistic != null) return perGraphSingleStatistic;
		else return globalStatistics;
	}
}
