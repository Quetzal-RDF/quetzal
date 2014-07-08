package com.ibm.rdf.store.coloring;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Set;

import org.junit.Test;

import com.ibm.rdf.store.hashing.ColoringFunction;
import com.ibm.wala.util.collections.HashSetFactory;

public class DbpediaSimulationColoring {

	@Test
	public void testDbpediaSimCorr() {
		try {
			ColoringFunction.color("../rdfstore-data/dbpedia/dbpediaSimCorr", null, true);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	@Test
	public void testDbpediaObj() {
		try {
			// test the dbpedia 3.7 datasets for coloring
			ColoringFunction.color("../rdfstore-data/dbpedia/dbpedia-rev.nt.sorted_obj.edge_sets", null, true);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	@Test
	public void testDbpediaSubj() {
		try {
			// test the dbpedia 3.7 datasets for coloring
			ColoringFunction.color("../rdfstore-data/dbpedia/dbpedia-rev.nt.sorted_subj.edge_sets", null, true);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	@Test
	public void testDbpediaSubjWithPriorityPredicates() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader("../rdfstore-data/dbpedia/priorityPreds3.7"));
			Set<String> priorityPreds = HashSetFactory.make();
			String line = null;
			while ((line = reader.readLine()) != null) {
				priorityPreds.add(line);
			}
			reader.close();
			// test the dbpedia 3.7 datasets for coloring
			ColoringFunction.color("../rdfstore-data/dbpedia/dbpedia-rev.nt.sorted_subj.edge_sets", priorityPreds, true);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
