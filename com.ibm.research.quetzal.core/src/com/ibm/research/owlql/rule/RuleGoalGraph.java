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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class RuleGoalGraph {

	
	
	
	private Map<Adornment, RuleGoalNode> adorn2Node;
	private Set<RuleGoalNode> ruleNodes;
	private Set<RuleGoalNode> goalNodes;
	private RuleGoalNode topGoal;
	
	public RuleGoalGraph(PredicateAdornment goal) {
		adorn2Node = new HashMap<Adornment, RuleGoalNode>();
		ruleNodes = new HashSet<RuleGoalNode>();
		goalNodes = new HashSet<RuleGoalNode>();
		topGoal = add(goal);
	}
	
	public RuleGoalNode get(Adornment ad) {
		RuleGoalNode ret = adorn2Node.get(ad);
		return ret;
	}
	public RuleGoalNode add(Adornment ad) {
		RuleGoalNode ret = adorn2Node.get(ad);
		if (ret==null) {
			ret= new RuleGoalNode(ad, new LinkedList<RuleGoalNode>());
			adorn2Node.put(ad,ret);
			if (ret.isGoal()) {
				goalNodes.add(ret);
			} else {
				assert ret.isRule();
				ruleNodes.add(ret);
			}
		}
		return ret;
	}
	public RuleGoalNode getTopGoalNode() {
		return topGoal;
	}

	public Set<RuleGoalNode> getGoalNodes() {
		return Collections.unmodifiableSet(goalNodes);
	}

	public Set<RuleGoalNode> getRuleNodes() {
		return Collections.unmodifiableSet(ruleNodes);
	}
	
}
