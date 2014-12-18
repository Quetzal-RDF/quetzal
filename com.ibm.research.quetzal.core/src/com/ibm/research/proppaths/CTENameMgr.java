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
 package com.ibm.research.proppaths;

import java.util.Collections;
import java.util.List;

import com.ibm.wala.util.collections.Pair;

public class CTENameMgr implements TemporaryTableMgr {

	protected String prefix;
	protected int count = 0;
	public CTENameMgr() {
		this("cte");
	}
	public CTENameMgr(String prefix) {
		super();
		this.prefix = prefix;
	}
	@Override
	public List<SQLCommand> init() {
		return Collections.emptyList();
	}
	@Override
	public Pair<String, List<SQLCommand>> getTemporaryTable(
			String tableSignature) {
		return Pair.make(prefix+(count++),Collections.<SQLCommand>emptyList());
	}
	@Override
	public List<SQLCommand> release(String temptable) {
		return Collections.emptyList();
	}
	@Override
	public List<SQLCommand> releaseAll() {
		return Collections.emptyList();
	}
	
	
	
}
