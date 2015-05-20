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
 package com.ibm.research.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RiotReader;
import org.apache.jena.riot.RiotWriter;
import org.apache.jena.riot.system.Checker;

import com.hp.hpl.jena.graph.Triple;
import com.ibm.wala.util.Predicate;
import com.ibm.wala.util.collections.FilterIterator;

public class FilterInvalidTriples {

	private static Iterator<Triple> filterTriples(URL url, String baseURI,
			Lang lang) throws MalformedURLException, IOException {
		Iterator<Triple> ts = RiotReader.createIteratorTriples(
				url.openStream(), lang, baseURI);
		return new FilterIterator<Triple>(ts, new Predicate<Triple>() {
			private int i = 0;
			private final Checker checker = new Checker();

			@Override
			public boolean test(Triple t) {
				return !t.getObject().isLiteral() || checker.checkLiteral(t.getObject(), i++, 0);
			}

		});
	}

	public static void filterDataFile(String dataFile, URL uri,
			FileOutputStream riotOut, Lang language)
			throws MalformedURLException, IOException {
		Iterator<Triple> ts = filterTriples(uri, dataFile, language);
		RiotWriter.writeTriples(riotOut, ts);
	}

	public static void main(String[] args) throws FileNotFoundException,
			IOException {
		String fileName = args[0];
		URL baseURI = new URL(args[1]);
		filterDataFile(fileName, baseURI, new FileOutputStream(fileName + "_filter.nt"), Lang.NT);
	}

}
