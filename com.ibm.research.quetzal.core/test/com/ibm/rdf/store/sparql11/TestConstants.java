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
 package com.ibm.rdf.store.sparql11;

public interface TestConstants {

	public static final int[] jazzAnswers = { 5, 5, 5, 5, 5, 5, 5, 5, 5, 10, 5,
			5, -2, 1024, 1024, 1024, 1024, 1024, 1024, 1024, 100, 100, 100, -2,
			100, 2048, 2048, 2048, 2048 };

	public static final int[] lubm100kAnswers = { 4, 0, 6, 34, 719, 7790, 67,
			7790, 208, 4, 0, 0, 1, 5916 };
	public static final int[] lubm10mAnswers = { 4, 212, 6, 34, 719, 838892,
			67, 7790, 21872, 4, 0, 0, 380, 636529 };
	public static final int[] lubm100mAnswers = { 4, 1831, 6, 34, 719, 7508706,
			67, 7790, 195954, 4, 0, 0, 3409, 5695568 };
	// 8000 universities is roughly 1 billion triples
	public static final int[] lubm8000uAnswers = { 4, 2528, 6, 34, 719,
			83557706, 67, 7790, 2178420, 4, 0, 0, 37118, 64400587 };

	public final static int[] sp2b1MAnswers = new int[] { 1, 32770, 52676, 379,
			0, 2586733, 35241, 35241, 62795, 292, 400, 4, 572, 10, 1, 1, 0 };
	public final static int[] sp2b10MAnswers = new int[] { 1, 613729, 323456,
			2209, 0, -1, 404903, 404903, -1, -1, 493, 4, 656, 10, 1, 1, 0 };
	public final static int[] sp2b100MAnswers = new int[] { 1, 9050604,
			1466402, 10143, 0, -1, 2016996, 2016996, 9812030, 14645, 493, 4,
			656, 10, 1, 1, 0 };

	public static final int[] dbpedia10mAnswers = { 9, 1, 119, 3, 90, 310, 2,
			129, 3, 1, 1, 98, 2, 6, 39, 2, 2, 1, 20, 99780, 2 };
	public static final int[] dbpedia100mAnswers = { 0, 1, 32, 0, 5, 55710, 2,
			4349, 5, 1, 1, 1, 7, 2, 2, 1, 338676, 458, 0, 4 };

	public static final int[] uobm_30_answers_original = { 0, 0, 25, 533, 0,
			5112, 1, 29, 34, -2, 47, 0, 0, 376 };
	public static final int[] uobm_30_answers_proppaths = { 2343, 38135, 25,
			533, 322, 5112, 1, 29, 34, 27355, 47, 0, 0, 376, 353, 0, 9931, -2,
			28898, -2, 354, 1152768, 23, 322, 0, 0, 9, 1162699 };
	public static final int[] uobm_30_answers_proppaths_expansion = { 2343,
			38135, 25, 533, 322, 5112, 1, 29, 34, 27355, 47, 0, 0, 376, 353, 0,
			9931, -2, 28898, -2, 354, 1152768, 23, 322, 0, 0, 9, 1162699 };
	public static final int[] uobm_1_answers_proppaths_expansion = { 2352,
			37808, 22, 533, 322, 184, 4, 36, 26, -2, 90, 0, 0, -2, 350, 0, 350,
			-2, 1017, -2, 351, 1152768, 23, 322, 0, 0, 6, -2 };

	static final String dataDir = System.getProperty("workspaceRoot") + "/rdfstore-data/";
}
