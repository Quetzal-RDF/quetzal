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
