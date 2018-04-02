package com.ibm.research.rdf.store.sparql11;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;

import kodkod.ast.Relation;
import kodkod.instance.Bounds;
import kodkod.instance.TupleFactory;

public class OpenDatasetUniverse extends DatasetUniverse {

	public OpenDatasetUniverse(URL datasetURL) throws URISyntaxException {
		super(datasetURL);
	}

	@Override
	protected void boundDataSet(Set<Relation> liveRelations, TupleFactory tf, Bounds b, Set<Object> liveAtoms)
			throws URISyntaxException {
		LazyTupleSet s = dataSetCrossProduct(tf);		
		bound(liveRelations, liveAtoms, b, QuadTableRelations.quads, relationTableBound(tf, datasetModel.asDatasetGraph().find(), liveAtoms), s);
	}

	
}
