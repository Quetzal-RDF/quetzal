package com.ibm.research.rdf.store.sparql11.semantics;

import static com.ibm.research.rdf.store.sparql11.semantics.ExpressionUtil.typeURI;
import static com.ibm.research.rdf.store.sparql11.semantics.ExpressionUtil.xsdBooleanType;
import static com.ibm.research.rdf.store.sparql11.semantics.ExpressionUtil.xsdDecimalType;
import static com.ibm.research.rdf.store.sparql11.semantics.ExpressionUtil.xsdDoubleType;
import static com.ibm.research.rdf.store.sparql11.semantics.ExpressionUtil.xsdFloatType;
import static com.ibm.research.rdf.store.sparql11.semantics.ExpressionUtil.xsdIntegerType;
import static com.ibm.research.rdf.store.sparql11.semantics.ExpressionUtil.xsdStringType;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Set;

import com.ibm.research.rdf.store.runtime.service.types.TypeMap;
import com.ibm.wala.util.collections.HashSetFactory;
import com.ibm.wala.util.collections.Pair;

import kodkod.ast.Relation;
import kodkod.instance.Bounds;
import kodkod.instance.Tuple;
import kodkod.instance.TupleFactory;
import kodkod.instance.TupleSet;

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
	
		ensureLiteral(Pair.<String, Object>make(ANY_LANGUAGE, null));
		
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
		
		for(Pair<String, ?> l : TypeMap.languages) {
			addLanguage(l.fst.toLowerCase());
		}
	}

	public LazyTupleSet anyLanguageTableBound(final TupleFactory f) {
		return new LazyTupleSet() {
			public TupleSet tuples() throws URISyntaxException {
				Collection<Tuple> tuples = HashSetFactory.make();
				for(Pair<String,?> l : literals) {
					if (l.snd instanceof String && ANY_LANGUAGE.equals(l.snd)) {
						for(String ll : languages) {
							tuples.add(f.tuple(l, Pair.make(ll, null)));
						}
					} 
				}
				if (tuples.isEmpty()) {
					return f.noneOf(2);
				} else {
					return f.setOf(tuples);
				}
			}
		};
	}

	@Override
	protected void boundLanguages(Set<Relation> liveRelations, TupleFactory tf, Bounds b, Set<Object> liveAtoms)
			throws URISyntaxException {
		LazyTupleSet base = super.languageTableBound(tf);
		bound(liveRelations, liveAtoms, b, QuadTableRelations.literalLanguages, 
			base, 
			new LazyTupleSet() {
				@Override
				public TupleSet tuples() throws URISyntaxException {
					TupleSet ts = tf.noneOf(2);
					ts.addAll(base.tuples());
					ts.addAll(anyLanguageTableBound(tf).tuples());
					return ts;
				}			
			});
	}

	@Override
	protected void boundDataSet(Set<Relation> liveRelations, final TupleFactory f,
			Bounds b, Set<Object> liveAtoms) throws URISyntaxException {
		LazyTupleSet s = dataSetCrossProduct(f);		
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
