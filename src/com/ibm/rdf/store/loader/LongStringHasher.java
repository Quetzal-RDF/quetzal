package com.ibm.rdf.store.loader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.hp.hpl.jena.graph.Node;
import com.ibm.rdf.store.config.Constants;
import com.ibm.rdf.store.hashing.HashingException;
import com.ibm.rdf.store.hashing.HashingHelper;

public class LongStringHasher {

	/**
	 * @param args
	 * @throws HashingException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws HashingException, IOException {
		int cutoff = Integer.parseInt(args[0]);
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		String line;
		while ((line = input.readLine()) != null) {
			if (line.length() > cutoff) {
				System.out.println(Node.createURI(Constants.PREFIX_SHORT_STRING + HashingHelper.hashLongString(line)));
			} else {
				System.out.println(line);
			}
		}
	}

}
