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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.ibm.wala.util.collections.HashSetFactory;
import com.ibm.wala.util.collections.Pair;

public class DefaultStoreProcedureManager implements StoreProcedureManager {

	protected String prefix;
	protected int count;
	protected Set<StoreProcedure> tempProcNotReleased;
	
	
	public DefaultStoreProcedureManager(String prefix) {
		super();
		this.prefix = prefix;
		this.count = 0;
		this.tempProcNotReleased = HashSetFactory.make();
	}

	@Override
	public List<SQLCommand> init() {
		// TODO 
		return Collections.emptyList();
	}

	@Override
	public StoreProcedure createTemporaryStoreProcedure(
			List<Pair<String, String>> inputParameterNameTypeList,
			List<Pair<String, String>> outputParameterNameTypeList,
			int numberOfReturnedResultSets, List<SQLCommand> bodyStatements) {
		String procName = prefix+(count++);
		StoreProcedure proc = new StoreProcedure(procName, inputParameterNameTypeList, outputParameterNameTypeList, numberOfReturnedResultSets, bodyStatements);
		tempProcNotReleased.add(proc);
		//SQLCommand declare = new StoreProcedureDeclaration(proc);
		return proc; //, Collections.singletonList(declare));
	}
	

	@Override
	public StoreProcedure createTemporaryStoreProcedure(
			int numberOfReturnedResultSets, List<SQLCommand> bodyStatements) {
		return createTemporaryStoreProcedure(new LinkedList<Pair<String,String>>(), new LinkedList<Pair<String,String>>(), numberOfReturnedResultSets, bodyStatements);
	}
	
	

	@Override
	public List<SQLCommand> declareAllTemporaryStoreProcedures() {
		List<SQLCommand> ret = new LinkedList<SQLCommand>();
		for (StoreProcedure proc : tempProcNotReleased) {
			SQLCommand declare = new StoreProcedureDeclaration(proc, true);
			ret.add(declare);
		}
		return ret;
	}

	@Override
	public List<SQLCommand> release(StoreProcedure proc) {
		
		if (tempProcNotReleased.remove(proc)) {
			return Collections.singletonList((SQLCommand)new DropStoreProcedure(proc.getName()));
		} else {
			return Collections.emptyList();
		}
		
	}

	@Override
	public List<SQLCommand> releaseAllTemporaryStoreProcedures() {
		List<SQLCommand> ret = new LinkedList<SQLCommand>();
		for (Iterator<StoreProcedure> it = tempProcNotReleased.iterator();it.hasNext();) {
			StoreProcedure proc = it.next();
			ret.add(new DropStoreProcedure(proc.getName()));
			it.remove();
		}
		//count = 0;
		return ret;
	}

}
