package com.ibm.research.rdf.store.utilities.io;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;

import com.ibm.research.rdf.store.sparql11.model.IRI;
import com.ibm.research.rdf.store.sparql11.model.QueryTripleTerm;
import com.ibm.research.rdf.store.sparql11.model.Variable;
import com.ibm.wala.util.collections.MapIterator;
import com.ibm.wala.util.functions.Function;

public class BaseUriSparqlResultReader implements SparqlSelectResult {
	private final URI baseURI;
	private final String fileBase;
	private final SparqlSelectResult base;
		
	public BaseUriSparqlResultReader(URI baseURI, String filePath, SparqlSelectResult base) {
		this.fileBase = "file://" + filePath.substring(0, filePath.lastIndexOf('/')+1);
		this.baseURI = baseURI;
		this.base = base;
	}

	@Override
	public Iterator<Row> rows() {
		return new MapIterator<Row,Row>(base.rows(), new Function<Row,Row>() {
			@Override
			public Row apply(final Row arg0) {
				return new Row() {
					@Override
					public QueryTripleTerm get(Variable v) {
						QueryTripleTerm x = arg0.get(v);
						try {
							if (x != null && x.isIRI()) {
								URI u = new URI(x.getIRI().getValue());
								if (! u.isAbsolute()) {
									return new QueryTripleTerm(new IRI(new URL(baseURI.toURL(), u.toURL().toString()).toString()));
								} else if (x.getIRI().getValue().startsWith(fileBase)) {
									return new QueryTripleTerm(new IRI(new URL(baseURI.toURL(), u.toString().substring(fileBase.length())).toString()));
								} 
							}
						} catch (URISyntaxException e) {
							assert false : e.toString();
						} catch (MalformedURLException e) {
							assert false : e.toString();
						}
						return x;
					}

					@Override
					public Iterator<Variable> variables() {
						return arg0.variables();
					}
				};
			}
		});
	}

	@Override
	public Iterator<Variable> variables() {
		return base.variables();
	}

}
