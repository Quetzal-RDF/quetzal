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
 package com.ibm.research.owlql.rule;

import java.util.Arrays;
import java.util.List;

public class RuleFormatter {

	public static final String formatRule( String head, String... subgoals ) {
		return formatRule( head, Arrays.asList( subgoals ) );
	}
	public static final String formatRule( String head, List<String> subgoals ) { 
		return formatRule(head, subgoals, true);
	}
	public static final String formatRule( String head, List<String> subgoals, boolean forRuleSystemParsing) {
		if (subgoals.size() == 0) return head + (forRuleSystemParsing ? ";" : "");
		String rule = head + " :- " + subgoals.get(0);
		for(String g : subgoals.subList( 1, subgoals.size() ))
			rule += " ^ " + g;
		return rule + (forRuleSystemParsing ? ";" : "");
	}
	public static final String formatAtomicFormula( String pred, String ... args ) {
		return formatAtomicFormula(pred, Arrays.asList( args ));
	}
	public static final String formatAtomicFormula( String pred, List<String> args ) {
		return pred + formatArgs(args);
	}
	
	public static final String formatArgs(String ... vars) { return formatArgs(Arrays.asList( vars )); }
	public static final String formatArgs(List<String> vars) {
		if (vars.size() == 0) return "()";
		String s = "(" + vars.get(0);
		for (String var : vars.subList( 1, vars.size() )) s += "," + var;
		return s + ")";
	}
}
