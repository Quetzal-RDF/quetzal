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

import java.util.List;

public class RuleGoalNode {
	
	private Adornment adornment;
	private List<RuleGoalNode> children;
	
	RuleGoalNode(Adornment adornment, List<RuleGoalNode> children) {
		super();
		this.adornment = adornment;
		this.children = children;
	}
	public Adornment getAdornment() {
		return adornment;
	}
	public void setAdornment(Adornment adornment) {
		this.adornment = adornment;
	}
	public List<RuleGoalNode> getChildren() {
		return children;
	}
	public boolean equals(Object o) {
		if (o==this) {
			return true;
		} 
		if (o instanceof RuleGoalNode) {
			RuleGoalNode other =(RuleGoalNode)o;
			return adornment.equals(other.adornment)
				   && children.equals(other.children);
		}
		return false;
	}
	
	public int hashCode() {
		return adornment.hashCode()+ 31*children.hashCode();
	}
	
	public boolean isGoal() {
		return adornment.isAdornmentOnPredicate();
	}
	
	public boolean isRule() {
		return adornment.isAdornmentOnRule();
	}
	
	public void addChild(RuleGoalNode node) {
		children.add(node);
	}
	
	
}
