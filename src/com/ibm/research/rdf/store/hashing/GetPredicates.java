package com.ibm.research.rdf.store.hashing;

import java.io.File;
import java.util.Set;

public class GetPredicates {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String queryDir = args[0];
		
		Set<String> predicates = FindWorkloadProxies.getPredicates(new File(queryDir));
		for(String s : predicates) {
			System.out.println(s);
		}
	}

}
