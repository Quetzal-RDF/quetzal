package com.ibm.research.rdf.store.sparql11.model;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Set;

public class ServicePattern extends Pattern {
	private final QueryTripleTerm service;
	private final String queryText;
	private final boolean silent;
	private final Pattern pattern;
	
	public ServicePattern(QueryTripleTerm service, String queryText, boolean silent, Pattern pattern) {
		super(EPatternSetType.SERVICE);
		String x = null;
		try {
			x = URLEncoder.encode(queryText.substring(queryText.indexOf('{')), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			assert false : e.toString();
		}
		this.queryText = x;
		this.service = service;
		this.silent = silent;
		this.pattern = pattern;
	}

	@Override
	public Collection<? extends Variable> getVariables() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reverse() {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<BlankNodeVariable> gatherBlankNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Variable> gatherVariables() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Variable> gatherOptionalVariablesWithMultipleBindings() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Variable> gatherVariablesWithOptional() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Variable> gatherIRIBoundVariables() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void replaceFilterBindings() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getNumberTriples() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Set<Pattern> gatherSubPatterns(boolean includeOptionals) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Pattern> gatherSubPatternsExcluding(Pattern except,
			boolean includeOptionals) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Pattern> getSubPatterns(boolean includeOptionals) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

}
