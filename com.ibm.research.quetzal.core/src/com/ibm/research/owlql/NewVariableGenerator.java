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
 package com.ibm.research.owlql;

/**
 * A generator of new fresh variables 
 * @author achille
 *
 */
public class NewVariableGenerator {
	private String prefix;
	private int suffixCount;
	public NewVariableGenerator(String prefix) {
		this(prefix, 0);
	}
	public NewVariableGenerator(String prefix, int suffixCount) {
		super();
		this.prefix = prefix;
		this.suffixCount = suffixCount;
	}
	
	public String createNewVariable(){
		return prefix+(suffixCount++);
	}
	
}
