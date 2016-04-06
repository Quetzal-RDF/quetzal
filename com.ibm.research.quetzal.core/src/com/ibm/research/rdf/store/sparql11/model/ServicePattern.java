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
 package com.ibm.research.rdf.store.sparql11.model;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ServicePattern extends Pattern implements Service {
	private final QueryTripleTerm service;
	private final String queryText;
	private final boolean silent;
	private final Pattern pattern;
	private final String originalText;
	
	
	public ServicePattern(QueryTripleTerm service, String queryText, boolean silent, Pattern pattern) {
		super(EPatternSetType.SERVICE);
		String x = null;
		
		try {
			String str = "SELECT * WHERE " + queryText.substring(queryText.indexOf('{'));
			x = URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			assert false : e.toString();
		}
		this.originalText = queryText;
		this.queryText = x;
		this.service = service;
		this.silent = silent;
		this.pattern = pattern;
	}

	@Override
	public String toString() {
		return service.toString() + " " + queryText;
	}

	@Override
	public Collection<? extends Variable> getVariables() {
		return new HashSet<Variable>(pattern.getVariables());
	}

	@Override
	public void reverse() {
		
	}

	@Override
	public Set<BlankNodeVariable> gatherBlankNodes() {
		return new HashSet<BlankNodeVariable>(pattern.gatherBlankNodes());
	}

	@Override
	public Set<Variable> gatherVariables() {
		Set<Variable> var = pattern.gatherVariables();
		return new HashSet<Variable>(pattern.gatherVariables());
	}

	@Override
	public Set<Variable> gatherOptionalVariablesWithMultipleBindings() {
		return new HashSet<Variable>(pattern.gatherOptionalVariablesWithMultipleBindings());
	}

	@Override
	public Set<Variable> gatherVariablesWithOptional() {
		return new HashSet<Variable>(pattern.gatherVariablesWithOptional());
	}

	@Override
	public Set<Variable> gatherIRIBoundVariables() {
		return new HashSet<Variable>(pattern.gatherIRIBoundVariables());
	}

	@Override
	public Set<Variable> gatherVariablesInTransitiveClosure() {
		return new HashSet<Variable>(pattern.gatherVariablesInTransitiveClosure());
	}

	@Override
	public void replaceFilterBindings() {
		pattern.replaceFilterBindings();
	}

	@Override
	public int getNumberTriples() {
		return pattern.getNumberTriples();
	}

	@Override
	public Set<Pattern> gatherSubPatterns(boolean includeOptionals) {
		Set<Pattern> p = new HashSet<Pattern>(pattern.gatherSubPatterns(includeOptionals));
		p.add(pattern);
		return p;
	}

	@Override
	public Set<Pattern> gatherSubPatternsExcluding(Pattern except,
			boolean includeOptionals) {
		Set<Pattern> p = new HashSet<Pattern>(pattern.gatherSubPatternsExcluding(except, includeOptionals));
		return p;
	}

	@Override
	public Set<Pattern> getSubPatterns(boolean includeOptionals) {
		Set<Pattern> p = new HashSet<Pattern>(pattern.getSubPatterns(includeOptionals));
		return p;
	}

	@Override
	public boolean isEmpty() {
		return pattern.isEmpty();
	}

	public QueryTripleTerm getService() {
		return service;
	}

	public String getQueryText() {
		return queryText;
	}

	public boolean isSilent() {
		return silent;
	}

	public Pattern getPattern() {
		return pattern;
	}

	@Override
	public EServiceType getServiceType() {
		return EServiceType.GET;
	}
}
