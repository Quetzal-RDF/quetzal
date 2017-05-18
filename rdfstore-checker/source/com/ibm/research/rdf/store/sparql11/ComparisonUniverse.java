package com.ibm.research.rdf.store.sparql11;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;

import kodkod.ast.Relation;
import kodkod.instance.Bounds;
import kodkod.instance.TupleFactory;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import com.hp.hpl.jena.query.Dataset;

public class ComparisonUniverse extends BoundedUniverse {
	private final Dataset datasetModel;

	public ComparisonUniverse(URL datasetURL) throws URISyntaxException {
		initDataset(datasetModel = RDFDataMgr.loadDataset(
			datasetURL.toExternalForm(),
			datasetURL.getPath().endsWith(".nq")? Lang.NQUADS: Lang.NTRIPLES));
	}

	@Override
	protected void boundDataSet(Set<Relation> liveRelations, TupleFactory tf,
			Bounds b, Set<Object> liveAtoms) throws URISyntaxException {
		boundExactly(liveRelations, liveAtoms, b, QuadTableRelations.desiredQuads, relationTableBound(tf, datasetModel.asDatasetGraph().find(), liveAtoms));
		super.boundDataSet(liveRelations, tf, b, liveAtoms);
	}
	
}
