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

public class DatasetUniverse extends BasicUniverse {

	private final URL datasetURL;
	protected final Dataset datasetModel;

	public DatasetUniverse(URL datasetURL) throws URISyntaxException {
		this.datasetURL = datasetURL;
		this.datasetModel = 
				RDFDataMgr.loadDataset(
						datasetURL.toExternalForm(),
						datasetURL.getPath().endsWith(".nq")? Lang.NQUADS: Lang.NTRIPLES);
		initDataset(datasetModel);
	}

	@Override
	public String toString() {
		return datasetURL.getPath();
	}
	
	@Override
	protected void boundNumbers(Set<Relation> liveRelations, TupleFactory tf,
			Bounds b, Set<Object> liveAtoms) throws URISyntaxException {
		boundExactly(liveRelations, liveAtoms, b, QuadTableRelations.literalValues, numericTableBound(tf));
	}

	
	@Override
	protected void boundLiteralTypes(Set<Relation> liveRelations,
			TupleFactory tf, Bounds b, Set<Object> liveAtoms)
			throws URISyntaxException  {
		boundExactly(liveRelations, liveAtoms, b, QuadTableRelations.literalDatatypes, datatypeTableBound(tf));
	}

	@Override
	protected void boundDataSet(Set<Relation> liveRelations, TupleFactory tf,
			Bounds b, Set<Object> liveAtoms) throws URISyntaxException {
		boundExactly(liveRelations, liveAtoms, b, QuadTableRelations.quads, relationTableBound(tf, datasetModel.asDatasetGraph().find(), liveAtoms));
	}
}
