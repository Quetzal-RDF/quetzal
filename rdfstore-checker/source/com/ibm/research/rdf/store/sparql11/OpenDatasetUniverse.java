package com.ibm.research.rdf.store.sparql11;

import java.net.URISyntaxException;
import java.net.URL;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import com.hp.hpl.jena.query.Dataset;

public class OpenDatasetUniverse extends BoundedUniverse {
	
	public OpenDatasetUniverse(URL datasetURL) throws URISyntaxException {
		super();
		Dataset datasetModel = 
				RDFDataMgr.loadDataset(
						datasetURL.toExternalForm(),
						datasetURL.getPath().endsWith(".nq")? Lang.NQUADS: Lang.NTRIPLES);
		initDataset(datasetModel);
	}
	
	
}
