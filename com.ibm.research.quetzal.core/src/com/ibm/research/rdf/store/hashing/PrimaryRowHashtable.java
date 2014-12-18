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
 package com.ibm.research.rdf.store.hashing;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.ibm.research.rdf.store.config.Constants;

/**
 * Implements a read-only hashtable based off a result set for the primary tables.
 */
public class PrimaryRowHashtable implements Map<String, String> {
	
	protected ResultSet resultSet;
	protected IHashingFunction func = null;
	protected int size;
	
	public PrimaryRowHashtable(String hashMethod, ResultSet rst) throws HashingException, SQLException {
		resultSet = rst;
		size = computeSize(resultSet);
		func = HashingHelper.getHashingFunction(hashMethod, size);
	}

	protected int computeSize(ResultSet rst) throws SQLException {
		return (resultSet.getMetaData().getColumnCount() - 3) / 2;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Map#clear()
	 */
	public void clear() {
		throw new UnsupportedOperationException("Clear is not supported.");
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	public boolean containsKey(Object key) {
		try {
			int x = find_qp((String)key);
			return x != -1;
		} catch(SQLException e) { e.printStackTrace(System.err); return false; }
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value) {
		for(int i = 0; i < size; i++) {
			try {
				if(getKey(i) != null) {
					String val = getValue(i);
					if(val == null) 
						return (value == null);
					else if(val.equals(value)) return true;
				}
			} catch(SQLException e) { e.printStackTrace(System.err); }
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#entrySet()
	 */
	public Set<Map.Entry<String, String>> entrySet() {
		class MyEntry implements Map.Entry<String, String> {
			private String key;
			private String value;
			public MyEntry(String k, String v) { key = k; value = v; }
			
			/* (non-Javadoc)
			 * @see java.util.Map.Entry#getKey()
			 */
			public String getKey() {
				return key;
			}

			/* (non-Javadoc)
			 * @see java.util.Map.Entry#getValue()
			 */
			public String getValue() {
				return value;
			}

			/* (non-Javadoc)
			 * @see java.util.Map.Entry#setValue(java.lang.Object)
			 */
			public String setValue(String value) {
				this.value = value;
				return this.value;
			}
		}
		Set<Map.Entry<String, String>> ret = new HashSet<Map.Entry<String, String>>();
		for(int i = 0; i < size; i++) {
			try {
				String key = getKey(i);
				if(key != null) {
					MyEntry e = new MyEntry(key, getValue(i));
					ret.add(e);
				}
			} catch(SQLException e) { e.printStackTrace(System.err); }
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public String get(Object key) {
		try {
			int x = find_qp((String)key);
			if(x == -1) return null;
			else return getValue(x);
		} catch(SQLException e) { e.printStackTrace(System.err); return null; }
	}

	/* (non-Javadoc)
	 * @see java.util.Map#isEmpty()
	 */
	public boolean isEmpty() {
		return size() == 0;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#keySet()
	 */
	public Set<String> keySet() {
		TreeSet<String> ret = new TreeSet<String>();
		for(int i = 0; i < size; i++) {
			try {
				String key = getKey(i);
				if(key != null) ret.add(key);
			} catch(SQLException e) { e.printStackTrace(System.err); }
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	public String put(String key, String value) {
		throw new UnsupportedOperationException("Put is not supported.");
	}

	/* (non-Javadoc)
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	public void putAll(Map<? extends String, ? extends String> t) {
		throw new UnsupportedOperationException("PutAll is not supported.");
	}

	/* (non-Javadoc)
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	public String remove(Object key) {
		throw new UnsupportedOperationException("Remove is not supported.");
	}

	/* (non-Javadoc)
	 * @see java.util.Map#size()
	 */
	public int size() {
		int cnt = 0;
		for(int i = 0; i < size; i++) {
			try {
				if(getKey(i) != null) cnt++;
			} catch(SQLException e) { e.printStackTrace(System.err); }
		}
		return cnt;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#values()
	 */
	public Collection<String> values() {
		List<String> ret = new ArrayList<String>();
		for(int i = 0; i < size; i++) {
			try {
				if(getKey(i) != null) ret.add(getValue(i));
			} catch(SQLException e) { e.printStackTrace(System.err); }
		}
		return ret;
	}
	
	private int find_lp(String key) throws SQLException {
		int h = func.hash(key);
		int x = h;
		String s;
		do {
			s = getKey(x);
			if(s == null) return -1;
			else if(s.equals(key)) return x;
			x = (x + 1) % size;
		} while(x != h);
		return -1;
	}
	
	private int find_qp(String key) throws SQLException {
		int h = func.hash(key);
		int x = h, d = 1;
		String s;
		
		do {
			s = getKey(x);
			if(s == null) return -1;
			else if(s.equals(key)) return x;
			x = (x + d * d) % size;
			d++;
		} while(x != h);
		return -1;		
	}
	
	private int findFree_lp(String key) throws SQLException {
		int h = func.hash(key);
		int x = h;
		String s;
		do {
			s = getKey(x);
			if(s == null) return x;
			x = (x + 1) % size;
		} while(x != h);
		return -1;		
	}
	
	private int findFree_qp(String key) throws SQLException {
		int h = func.hash(key);
		int x = h, d = 1;
		String s;
		
		do {
			s = getKey(x);
			if(s == null) return x;
			x = (x + d * d) % size;
			d++;
		} while(x != h);
		return -1;				
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < size; i++) {
			try {
				String key = getKey(i);
				if(key != null) {
					if(sb.length() > 0) sb.append("; ");
					sb.append(key + "=" + getValue(i));
				}
			} catch(SQLException e) { e.printStackTrace(System.err); }
		}
		return "{" + sb.toString() + "}";
	}
	
	public String toRecordLine() throws SQLException {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < size; i++) {
			String key = getKey(i);
			String value = getValue(i);
			sb.append(((key == null) ? "" : ("\"" + key + "\"")) + Constants.LOAD_COLUMN_SEPARATOR + 
					  ((value == null)? "" : ("\"" + value + "\"")) + Constants.LOAD_COLUMN_SEPARATOR);
		}
		return sb.toString();
	}
	
	public String toSecondaryRecordLine() throws SQLException {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < size; i++) {
			String key = getKey(i);
			sb.append(((key == null) ? "" : ("\"" + key + "\"")) + Constants.LOAD_COLUMN_SEPARATOR);
		}
		return sb.toString();
	}
	
	protected String getKey(int i) throws SQLException {
		return resultSet.getString(Constants.NAME_COLUMN_PREFIX_PREDICATE.toLowerCase() + i);
	}
	
	protected String getValue(int i) throws SQLException {
		return resultSet.getString(Constants.NAME_COLUMN_PREFIX_VALUE.toLowerCase() + i);
	}

	public Map<String, String> getAsMap() throws SQLException {
		Map<String, String> ret = new HashMap<String, String>();
		for(int i = 0; i < size; i++) {
			String key = getKey(i);
			if(key != null) ret.put(key, getValue(i));
		}
		return ret;
	}
}
