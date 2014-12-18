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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class DB2Closure {
	
	/**
	 * Calculate the bNode closure from a resource . The Statement itself does
	 * not automatically get included.
	 * 
	 * @param resource
	 * @param testThisNode
	 *            Indicate whether to apply the closure test to the Resource
	 *            argument.
	 * @param results
	 *            Add the statements to this model
	 * @return A model containing statements
	 */
	
	public static Model closure(Resource resource, boolean testThisNode,
			Model results, Collection<String> resources) {
		return closure(resource, new ClosureBNode(), testThisNode, results, resources);
	}

	/**
	 * Calculate the bNode closure from a resource . The Statement itself does
	 * not automatically get included.
	 * 
	 * @param resource
	 * @param test
	 *            The test object to be applied
	 * @param testThisNode
	 *            Indicate whether to apply the closure test to the Resource
	 *            argument.
	 * @param results
	 *            Add the statements to this model
	 * @return A model containing statements
	 */

	public static Model closure(Resource resource, ClosureTest test,
			boolean testThisNode, Model results, Collection<String> resources) {
		List<Resource> visited = new ArrayList<Resource>();

		if (!testThisNode)
			closureNoTest(resource, results, visited, test, resources);
		else
			closure(resource, results, visited, test, resources);
		return results;
	}

	// --------------------------------------------------------------------------------

	private static void closure(Statement stmt, Model closureBlob,
			Collection<Resource> visited, ClosureTest test, Collection<String> resources) {
		if (test.includeStmt(stmt))
			closureBlob.add(stmt);
		closure(stmt.getSubject(), closureBlob, visited, test, resources);
		closure(stmt.getObject(), closureBlob, visited, test, resources);
	}

	private static void closure(RDFNode n, Model closureBlob,
			Collection<Resource> visited, ClosureTest test, Collection<String> resources) {
		if (!(n instanceof Resource))
			return;

		Resource r = (Resource) n;

		if (visited.contains(r))
			return;

		if (!test.traverse(r))
			return;

		closureNoTest(r, closureBlob, visited, test, resources);
	}

	private static void closureNoTest(Resource r, Model closureBlob,
			Collection<Resource> visited, ClosureTest test, Collection<String> resources) {
		visited.add(r);
		String gid = ((DB2Graph) r.getModel().getGraph()).getGraphID();
		String key = null;
		if (r.isAnon()) {
			key = gid + ":" + r.getId();
		} else {
			key = gid + ":" + r.getURI();
		}
		if (resources.contains(key)) {
			return;
		}
		resources.add(key);

		StmtIterator sIter = r.listProperties();

		for (; sIter.hasNext();) {
			Statement stmt = sIter.nextStatement();
			closure(stmt, closureBlob, visited, test, resources);
		}
	}

	// Defines the bNode closure

	static class ClosureBNode implements ClosureTest {
		public boolean traverse(Resource r) {
			return r.isAnon();
		}

		public boolean includeStmt(Statement s) {
			return true;
		}
	}

	static class ClosureReachable implements ClosureTest {
		public boolean traverse(Resource r) {
			return true;
		}

		public boolean includeStmt(Statement s) {
			return true;
		}
	}

	interface ClosureTest {
		/**
		 * Return true if the closure algorithm should continue with statements
		 * with this resource as subject. Applied to subject and object iof each
		 * statement traversed
		 * 
		 * @param r
		 */
		public boolean traverse(Resource r);

		/**
		 * Return true if the statement should be included in the closure. The
		 * algorithm still recurses on the subject and object - this test is
		 * just about whether it is included in the result collection.
		 * 
		 * @param s
		 *            Statement to test
		 */
		public boolean includeStmt(Statement s);

	}

}