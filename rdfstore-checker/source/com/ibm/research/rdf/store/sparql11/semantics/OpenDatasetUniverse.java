package com.ibm.research.rdf.store.sparql11.semantics;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import org.apache.jena.query.Dataset;

import kodkod.ast.Relation;
import kodkod.instance.Bounds;
import kodkod.instance.TupleFactory;

public class OpenDatasetUniverse extends BoundedUniverse {
	private final Dataset datasetModel;
	
	public OpenDatasetUniverse(URL datasetURL) throws URISyntaxException {
		super();
		datasetModel = 
				RDFDataMgr.loadDataset(
						datasetURL.toExternalForm(),
						datasetURL.getPath().endsWith(".nq")? Lang.NQUADS: Lang.NTRIPLES);
		initDataset(datasetModel);
	}
	
	@Override
	protected void boundDataSet(Set<Relation> liveRelations, final TupleFactory f,
			Bounds b, Set<Object> liveAtoms) throws URISyntaxException {
		LazyTupleSet s = dataSetCrossProduct(f);		
		bound(liveRelations, liveAtoms, b, QuadTableRelations.quads, relationTableBound(f, datasetModel.asDatasetGraph().find(), liveAtoms), s);
	}

}
