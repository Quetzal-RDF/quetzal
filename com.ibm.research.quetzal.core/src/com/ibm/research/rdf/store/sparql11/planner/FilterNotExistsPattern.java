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
 package com.ibm.research.rdf.store.sparql11.planner;

import com.ibm.research.rdf.store.sparql11.model.Pattern;
import com.ibm.research.rdf.store.sparql11.model.PatternSet;

/**
 * This pattern object is not part of the query model but is used as part of the pattern
 * process.  
 * @author ksrinivs
 *
 */
public class FilterNotExistsPattern extends PatternSet {

	public void addPattern(Pattern p) {
		patterns.add(p);
	}

	public FilterNotExistsPattern() {
		super();
	}

	public FilterNotExistsPattern(EPatternSetType t) {
		super(t);
	}	
	public String toString() {
		return "FilterNotExistsPattern";
	}
}
