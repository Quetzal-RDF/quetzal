package com.ibm.research.rdf.store.sparql11;

import static com.ibm.research.rdf.store.sparql11.ExpressionUtil.typeURI;
import static com.ibm.research.rdf.store.sparql11.ExpressionUtil.xsdBooleanType;
import static com.ibm.research.rdf.store.sparql11.ExpressionUtil.xsdDecimalType;
import static com.ibm.research.rdf.store.sparql11.ExpressionUtil.xsdDoubleType;
import static com.ibm.research.rdf.store.sparql11.ExpressionUtil.xsdFloatType;
import static com.ibm.research.rdf.store.sparql11.ExpressionUtil.xsdIntegerType;
import static com.ibm.research.rdf.store.sparql11.ExpressionUtil.xsdStringType;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;

import kodkod.ast.Relation;
import kodkod.instance.Bounds;
import kodkod.instance.TupleFactory;
import kodkod.instance.TupleSet;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import com.ibm.wala.util.collections.Pair;

public class BoundedUniverse extends BasicUniverse {

	private final int uriLimit = 5;
	private final int literalLimit = 5;
	private final int numberLimit = 5;
	
	public BoundedUniverse() throws URISyntaxException {
		initBounded();
	}

	protected void initBounded() throws URISyntaxException {
		for(int i = 0; i < uriLimit; i++) {
			ensureIRI(new URI("http://synthetic/" + i));
		}
	
		for(int i = 0; i < literalLimit; i++) {
			ensureLiteral(
				Pair.<String, Object>make(
					"lit" + i, 
					i%2==0? ANY_IRI: ANY_LANGUAGE));
		}
	
		for(int i = 0; i < numberLimit; i++) {
			ensureLiteral(
				Pair.<String, Object>make("num" + i, ANY_NUMBER));
		}
	
		iris.add(typeURI(xsdIntegerType));
		iris.add(typeURI(xsdDoubleType));
		iris.add(typeURI(xsdFloatType));
		iris.add(typeURI(xsdDecimalType));
		iris.add(typeURI(xsdStringType));
		iris.add(typeURI(xsdBooleanType));
	}

	@Override
	protected void boundDataSet(Set<Relation> liveRelations, final TupleFactory f,
			Bounds b, Set<Object> liveAtoms) throws URISyntaxException {
		LazyTupleSet s = new LazyTupleSet() {
			@Override
			public TupleSet tuples() throws URISyntaxException {
				final TupleSet x = f.noneOf(1);
				for(URI iri : iris) {
					x.add(f.tuple(iri));
				}
				
				final TupleSet y = x.clone();
				for(String blank : blankNodes) {
					y.add(f.tuple(blank));
				}

				final TupleSet z = y.clone();
				for(Pair<String, ?> lit : literals) {
					z.add(f.tuple(lit));
				}

				final TupleSet g = f.setOf(f.tuple(QuadTableRelations.defaultGraph));
				return g.product(y).product(x).product(z);
			}	
		};
		
		bound(liveRelations, liveAtoms, b, QuadTableRelations.quads, s);
	}
	
	@Override
	protected void boundLiteralTypes(Set<Relation> liveRelations,
			TupleFactory tf, Bounds b, Set<Object> liveAtoms)
			throws URISyntaxException  {
		bound(liveRelations, liveAtoms, b, QuadTableRelations.literalDatatypes, datatypeTableBound(tf));
	}


	@Override
	protected void boundNumbers(Set<Relation> liveRelations, TupleFactory tf,
			Bounds b, Set<Object> liveAtoms) throws URISyntaxException {
		bound(liveRelations, liveAtoms, b, QuadTableRelations.literalValues, numericTableBound(tf));
	}

	@Override
	public LazyTupleSet subjectsTableBound(TupleFactory tf, Set<Object> liveAtoms) {
		return nodesTableBound(tf, liveAtoms, true, true, false);
	}

	@Override
	public LazyTupleSet predicatesTableBound(TupleFactory tf, Set<Object> liveAtoms) {
		return nodesTableBound(tf, liveAtoms, true, false, false);
	}

	@Override
	public LazyTupleSet objectsTableBound(TupleFactory tf, Set<Object> liveAtoms) {
		return nodesTableBound(tf, liveAtoms, true, true, true);
	}

	
}