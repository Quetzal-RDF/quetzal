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
 package com.ibm.research.rdf.store.jena.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.ResultSetIterator;
import org.apache.commons.dbutils.RowProcessor;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.Filter;
import com.hp.hpl.jena.util.iterator.FilterIterator;
import com.hp.hpl.jena.util.iterator.Map1;
import com.hp.hpl.jena.util.iterator.MapFilter;
import com.hp.hpl.jena.util.iterator.MapFilterIterator;
import com.hp.hpl.jena.util.iterator.NiceIterator;
import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.config.Constants;
import com.ibm.research.rdf.store.jena.impl.update.InsertAndUpdateStatements;
import com.ibm.research.rdf.store.jena.impl.update.String2Node;
import com.ibm.research.rdf.store.runtime.service.types.TypeMap;
import com.ibm.research.rdf.store.runtime.service.types.TypedValue;

public class DB2ResultSetIterator   implements ExtendedIterator<Triple> {
	
	public static class RowProcessorImpl extends BasicRowProcessor implements RowProcessor {

		private Store store;
		private Connection con;

		public RowProcessorImpl(Store store, Connection con) {
			super();
			this.store = store;
			this.con = con;
		}

		@Override
		public Object[] toArray(ResultSet res) throws SQLException {
			return  new Object[]{extractRow(res)};
		}
		
		protected Triple extractRow(ResultSet res) throws SQLException {
			String sub = res.getString("entry");
			String pred = res.getString("predicate");
			String obj = res.getString("value");
			short typ = res.getShort("type");

			Resource subject;
			Property predicate;
			RDFNode object;

			if (store.isLongString()) {
				List<TypedValue> params = new ArrayList<TypedValue>();

				if (sub.startsWith(Constants.PREFIX_SHORT_STRING)) {
					params.add(new TypedValue(sub,TypeMap.IRI_ID)); // blank nodes will always fall within 118
				}
				if (pred.startsWith(Constants.PREFIX_SHORT_STRING)) {
					params.add(new TypedValue(pred, TypeMap.IRI_ID));
				}
				if (obj.startsWith(Constants.PREFIX_SHORT_STRING)) {
					params.add( new TypedValue(obj,typ));
				}

				if (params.size() > 0) {
					String sql = InsertAndUpdateStatements.getLongStringSelect(
							store.getLongStrings(), params.size());

					PreparedStatement sStmt = null;
					ResultSet set = null;
					try {
						int x = 1;
						sStmt = con.prepareStatement(sql);
						for (int i = 0; i < params.size(); i++) {
							sStmt.setObject(x++, params.get(i).getValue());
							sStmt.setObject(x++, params.get(i).getType());
						}

						set = sStmt.executeQuery();
						while (set.next()) {
							if (sub.equals(set.getObject(Constants.NAME_COLUMN_SHORT_STRING))) {
								sub = set.getString(Constants.NAME_COLUMN_LONG_STRING)
									+ set.getString(Constants.NAME_COLUMN_LONG_STRING_OVERFLOW);
							} else if (pred.equals(set.getObject(Constants.NAME_COLUMN_SHORT_STRING))) {
								pred = set.getString(Constants.NAME_COLUMN_LONG_STRING)
								+ set.getString(Constants.NAME_COLUMN_LONG_STRING_OVERFLOW);
							} else {
								obj = set.getString(Constants.NAME_COLUMN_LONG_STRING)
								+ set.getString(Constants.NAME_COLUMN_LONG_STRING_OVERFLOW);
							}
						}
					} finally {
						DB2CloseObjects.close(set, sStmt);
					}
				}
			}

			subject = (Resource) new String2Node(Constants.NAME_COLUMN_SUBJECT, sub)
					.getNode();
			predicate = (Property) new String2Node(Constants.NAME_COLUMN_PREDICATE,
					pred).getNode();
			object = new String2Node(Constants.NAME_COLUMN_OBJECT, obj,typ).getNode();

			Statement s = ResourceFactory.createStatement(subject, predicate,
					object);

			return s.asTriple();
		}
		
		
	}
	
	private ResultSet rs;
	
	private ResultSetIterator it;
	
	public DB2ResultSetIterator(ResultSet rs, Connection con, Store store) {
		it = new ResultSetIterator(rs, new RowProcessorImpl(store, con));
		this.rs = rs;
	}

	@Override
	public boolean hasNext() {
		return it.hasNext();
	}

	@Override
	public Triple next() {
		Object[] ret = it.next();
		return (Triple) ret[0];
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		close();
	}

	@Override
	public void close() {
		try {
			if (rs!=null) {
				rs.close();
				rs = null;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
	}

	@Override
	public <X extends Triple> ExtendedIterator<Triple> andThen(Iterator<X> other) {
		return NiceIterator.<Triple>andThen(this, other);
	}

	@Override
	public ExtendedIterator<Triple> filterDrop(final Filter<Triple> filter) {
		 Filter<Triple> negFilter = new Filter<Triple>() {

			@Override
			public boolean accept(Triple t) {
				return !filter.accept(t) ;
			}
			 
		 };

		 return new FilterIterator<Triple>(negFilter, this) ;
	}
	
	@Override
	public ExtendedIterator<Triple> filterKeep(Filter<Triple> filter) {
		 return new FilterIterator<Triple>(filter, this) ;
	}

	@Override
	public <U> ExtendedIterator<U> mapWith(final Map1<Triple, U> map) {
		MapFilter<Triple, U> mf = new MapFilter<Triple, U>() {

			@Override
			public U accept(Triple t) {
				return map.map1(t);
			}
			
		};
		return new MapFilterIterator<Triple, U>(mf, this);
	}

	@Override
	public Triple removeNext() {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Triple> toList() {
		return NiceIterator.asList(this);
	}

	@Override
	public Set<Triple> toSet() {
		return NiceIterator.asSet(this);
	}
}