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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.wala.util.collections.HashMapFactory;
import com.ibm.wala.util.collections.HashSetFactory;
import com.ibm.wala.util.collections.Pair;

public class DefaultTemporaryTableMgr implements TemporaryTableMgr {
	private static Map<Pair<String, String>, DefaultTemporaryTableMgr>  tmpSpacePrefixPair2TmpTableMgr = HashMapFactory.make();;
	
	
	public static synchronized DefaultTemporaryTableMgr get(String tmpSpace, String prefix) {
		Pair<String, String> key = Pair.make(tmpSpace, prefix);
		DefaultTemporaryTableMgr ret =  tmpSpacePrefixPair2TmpTableMgr.get(key);
		if (ret == null) {
			ret = new DefaultTemporaryTableMgr(tmpSpace, prefix);
			tmpSpacePrefixPair2TmpTableMgr.put(key,ret);
		}
		return ret;
	}
	public static synchronized DefaultTemporaryTableMgr get(String tmpSpace) {
		return get(tmpSpace, "session.tempt");
	}
	protected String prefix;
	protected int count = 0;
	protected String tmpSpace;
	protected Set<Integer> tablesNotReleased;
	
	
	private DefaultTemporaryTableMgr(String tmpSpace) {
		this(tmpSpace, "session.tempt");
	}
	private DefaultTemporaryTableMgr(String tmpSpace, String prefix) {
		super();
		this.prefix = prefix;
		this.tmpSpace = tmpSpace;
		tablesNotReleased = HashSetFactory.make();
		
	}

	@Override
	public synchronized List<SQLCommand> init() {
		// TODO Auto-generated method stub
		return new LinkedList<SQLCommand>();
	}
	@Override
	public synchronized Pair<String, List<SQLCommand>> getTemporaryTable(String tableSignature) {
		tablesNotReleased.add(count);
		String table = prefix+(count++);
		DeclareTempTableCommand cmd = new DeclareTempTableCommand(tmpSpace, table, tableSignature);
		
		return Pair.make(table, Collections.singletonList((SQLCommand)cmd));
	}
	
	
	@Override
	public  synchronized  List<SQLCommand> release(String temptable) {
		assert temptable.length() > prefix.length() && temptable.startsWith(prefix)
			: temptable+" was not created by this "+this;
		String suffix = temptable.substring(prefix.length());
		try {
			int pos = Integer.valueOf(suffix);
			if (tablesNotReleased.remove(pos)) {
				return Collections.singletonList((SQLCommand)new DropTableCommand(temptable));
			} else {
				return Collections.emptyList();
			}
		} catch (NumberFormatException e) {
			throw new RuntimeException(temptable+" was not created by this "+this, e);
		}
	}
	@Override
	public  synchronized  List<SQLCommand> releaseAll() {
		List<SQLCommand> ret = new LinkedList<SQLCommand>();
		for (Integer pos : tablesNotReleased) {
			String temptable = prefix+pos;
			ret.add(new DropTableCommand(temptable));
		}
		tablesNotReleased.clear();
		//count = 0;
		return ret;
	}

	
}
