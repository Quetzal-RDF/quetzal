package com.ibm.research.rdf.store.sparql11;

import static com.ibm.research.rdf.store.sparql11.ExpressionUtil.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import kodkod.ast.Relation;
import kodkod.instance.Bounds;
import kodkod.instance.Tuple;
import kodkod.instance.TupleFactory;
import kodkod.instance.TupleSet;
import kodkod.instance.Universe;

import com.hp.hpl.jena.graph.impl.LiteralLabel;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.sparql.core.Quad;
import com.ibm.wala.util.collections.HashMapFactory;
import com.ibm.wala.util.collections.HashSetFactory;
import com.ibm.wala.util.collections.Pair;

public abstract class BasicUniverse implements UniverseFactory {
	
	protected final String ANY_LANGUAGE;
	protected final URI ANY_IRI;
	protected final URI ANY_NUMBER;
	
	protected BasicUniverse() throws URISyntaxException {
		ANY_LANGUAGE = "ANY_LANGUAGE";
		ANY_IRI = new URI("http://any/iri");
		ANY_NUMBER = new URI("http://any/number");
	}
	
	interface LazyTupleSet {
		TupleSet tuples() throws URISyntaxException;
	}
		
	protected final Set<URI> graphs = HashSetFactory.make();
	protected final Set<URI> iris = HashSetFactory.make();
	protected final Set<URI> predicates = HashSetFactory.make();
	protected final Set<Object> subjects = HashSetFactory.make();
	protected final Set<Object> objects = HashSetFactory.make();
	protected final Set<String> blankNodes = HashSetFactory.make();
	protected final Set<Pair<String,?>> literals = HashSetFactory.make();
	private final Set<URI> datatypes = HashSetFactory.make();
	private final Set<String> languages = HashSetFactory.make();
	private final Map<Object,Relation> atomRelations = HashMapFactory.make();
		
	@Override
	public void ensureIRI(URI iri) {
		iris.add(iri);
	}

	private void addLanguage(String language) {
		languages.add(language);
		ensureLiteral(Pair.make(language, null));
	}
	
	@Override
	public void ensureLiteral(Pair<String, ?> lit) {
		if (lit.snd instanceof URI) {
			datatypes.add((URI)lit.snd);
		} else if (lit.snd instanceof String) {
			addLanguage((String)lit.snd);
			addLanguage(((String)lit.snd).toLowerCase());
		} else {
			assert lit.snd == null;
		}
		literals.add(lit);
	}

	@Override
	public void ensureBlankNode(String blankId) {
		blankNodes.add(blankId);
	}
	
	private Collection<Object> universe() throws URISyntaxException {
		Collection<Object> atoms = HashSetFactory.make();
		atoms.add(QuadTableRelations.NULL_atom);
		atoms.add(QuadTableRelations.defaultGraph);

		atoms.addAll(graphs);

		atoms.addAll(iris);

		atoms.addAll(blankNodes);

		atoms.addAll(datatypes);

		atoms.addAll(languages);

		for(Pair<String,?> l : literals) {
			atoms.add(l);
			atoms.add(l.fst);
		}

		int msb = bitWidth - 1;
		atoms.add(-(1<<msb));
		for(int i = 0; i < msb; i++) {
			int v = (int)Math.pow(2, i);
			atoms.add(v);
		}

		for(Object atom : atomRelations.keySet()) {
			atoms.add(atom);
		}

		return atoms;
	}
	
	private void collectAtoms(Set<Object> liveAtoms, TupleSet bound) {
		for(Tuple t : bound) {
			for(int i = 0; i < t.arity(); i++) {
				Object atom = t.atom(i);
				liveAtoms.add(atom);
			}
		}
	}
	
	protected void boundExactly(Set<Relation> liveRelations, Set<Object> liveAtoms, Bounds b, Relation r, LazyTupleSet bound) throws URISyntaxException {
		if (liveRelations == null || liveRelations.contains(r)) {
			TupleSet ts = bound.tuples();
			b.boundExactly(r, ts);
			collectAtoms(liveAtoms, ts);
		}
	}
	
	protected void bound(Set<Relation> liveRelations, Set<Object> liveAtoms, Bounds b, Relation r, LazyTupleSet bound) throws URISyntaxException {
		if (liveRelations == null || liveRelations.contains(r)) {
			TupleSet ts = bound.tuples();
			b.bound(r, ts);
			collectAtoms(liveAtoms, ts);
		}
	}

	protected void bound(Set<Relation> liveRelations, Set<Object> liveAtoms, Bounds b, Relation r, LazyTupleSet lower, LazyTupleSet upper) throws URISyntaxException {
		if (liveRelations == null || liveRelations.contains(r)) {
			TupleSet ls = lower.tuples();
			TupleSet us = upper.tuples();
			b.bound(r, ls, us);
			collectAtoms(liveAtoms, ls);
			collectAtoms(liveAtoms, us);
		}
	}

	@Override
	public Bounds boundUniverse(Set<Relation> liveRelations) throws URISyntaxException {
		Universe u = new Universe(universe());
		TupleFactory tf = u.factory();
		Bounds b = new Bounds(u);
	
		Set<Object> liveAtoms = basicBounds(liveRelations, u, tf, b);

		u = new Universe(liveAtoms);
		tf = u.factory();
		b = new Bounds(u);
		basicBounds(liveRelations, u, tf, b);
				
		b.boundExactly(QuadTableRelations.NULL, tf.setOf(tf.tuple(QuadTableRelations.NULL_atom)));
		
		for(Relation r : nodeRelations) {
			int arity = r.arity();
			TupleSet rb = nodesTableBound(tf, liveAtoms, true, true, true).tuples();
			rb.add(tf.tuple(QuadTableRelations.NULL_atom));
			for(int i = 1; i < arity; i++) {
				TupleSet rbi = nodesTableBound(tf, liveAtoms, true, true, true).tuples();
				rbi.add(tf.tuple(QuadTableRelations.NULL_atom));
				rb = rb.product(rbi);
			}
			b.bound(r, rb);
		}
		
		return b;
	}

	public Set<Object> basicBounds(Set<Relation> liveRelations, final Universe u,
			final TupleFactory tf, Bounds b) throws URISyntaxException {
		Set<Object> liveAtoms = HashSetFactory.make();
		liveAtoms.add(QuadTableRelations.NULL_atom);
		
		for(Object atom : atomRelations.keySet()) {
			System.err.println("adding literal to liveAtoms: " + atom);
			liveAtoms.add(atom);
		}
		
		boundDataSet(liveRelations, tf, b, liveAtoms);
		
		boundLiteralTypes(liveRelations, tf, b, liveAtoms);
		
		boundExactly(liveRelations, liveAtoms, b, QuadTableRelations.literalLanguages, languageTableBound(tf));

		boundExactly(liveRelations, liveAtoms, b, QuadTableRelations.languageCaseMatch, languageCaseMatchBound(tf));
	
		boundExactly(liveRelations, liveAtoms, b, QuadTableRelations.graphs, nodesTableBound(tf, liveAtoms, false, false, false));
		
		boundExactly(liveRelations, liveAtoms, b, QuadTableRelations.subjects, subjectsTableBound(tf, liveAtoms));

		boundExactly(liveRelations, liveAtoms, b, QuadTableRelations.predicates, predicatesTableBound(tf, liveAtoms));

		boundExactly(liveRelations, liveAtoms, b, QuadTableRelations.objects, objectsTableBound(tf, liveAtoms));

		boundExactly(liveRelations, liveAtoms, b, QuadTableRelations.nodes, nodesTableBound(tf, liveAtoms, true, true, true));

		boundNumbers(liveRelations, tf, b, liveAtoms);
		
		boundExactly(liveRelations, liveAtoms, b, QuadTableRelations.numericTypes, numericTypeTableBound(u, tf));

		boundExactly(liveRelations, liveAtoms, b, QuadTableRelations.floatTypes, floatTypeTableBound(u, tf));

		boundExactly(liveRelations, liveAtoms, b, QuadTableRelations.booleanTypes, booleanTypeTableBound(u, tf));

		boundExactly(liveRelations, liveAtoms, b, QuadTableRelations.dateTypes, dateTypeTableBound(u, tf));

		boundExactly(liveRelations, liveAtoms, b, QuadTableRelations.choice, choiceBound(tf));

		boundExactly(liveRelations, liveAtoms, b, QuadTableRelations.stringOrder, stringOrderBound(tf));

		final URI stringTypeAtom = new URI(ExpressionUtil.xsdStringType);
		boundExactly(liveRelations, liveAtoms, b, QuadTableRelations.stringTypes, new LazyTupleSet() {
			@Override
			public TupleSet tuples() throws URISyntaxException {
				return u.contains(stringTypeAtom)? tf.setOf(tf.tuple(stringTypeAtom)): tf.noneOf(1);
			}
		});
		
		final LazyTupleSet bn = blankNodeBound(tf);
		boundExactly(liveRelations, liveAtoms, b, QuadTableRelations.blankNodes, bn);
		bound(liveRelations, liveAtoms, b, QuadTableRelations.blankNodeRenaming, new LazyTupleSet() {
			@Override
			public TupleSet tuples() throws URISyntaxException {
				TupleSet x = bn.tuples();
				return x.product(x);
			}			
		});
		
		for(Iterator<?> atoms = u.iterator(); atoms.hasNext(); ) {
			final Object atom = atoms.next();
			Relation ar = atomRelation(atom);
			boundExactly(liveRelations, liveAtoms, b, ar, new LazyTupleSet() {
				@Override
				public TupleSet tuples() throws URISyntaxException {
					return tf.setOf(atom);
				}				
			});
		}
				
		int msb = bitWidth - 1;
		for(int i = 0; i < msb; i++) {
			Integer v = Integer.valueOf(1<<i);
			liveAtoms.add(v);
			b.boundExactly(v.intValue(), tf.setOf(v));
		}
		liveAtoms.add(Integer.valueOf(-(1<<msb)));
		b.boundExactly(-(1<<msb), tf.setOf(Integer.valueOf(-(1<<msb))));
		
		if (solution != null && !solution.variables().isEmpty()) {
			solution.bound(b, b.universe().factory());
		}

		return liveAtoms;
	}

	protected abstract void boundLiteralTypes(Set<Relation> liveRelations,
			final TupleFactory tf, Bounds b, Set<Object> liveAtoms)
			throws URISyntaxException;

	protected abstract void boundNumbers(Set<Relation> liveRelations,
			final TupleFactory tf, Bounds b, Set<Object> liveAtoms)
			throws URISyntaxException;

	protected abstract void boundDataSet(Set<Relation> liveRelations,
			final TupleFactory tf, Bounds b, Set<Object> liveAtoms)
			throws URISyntaxException;
	
	private LazyTupleSet typeNameTableBound(final Collection<String> typeNames, final Universe u, final TupleFactory f) {
		return new LazyTupleSet() {
			public TupleSet tuples() throws URISyntaxException {
				Collection<Tuple> tuples = HashSetFactory.make();
				for(String typeName : typeNames) {
					URI atom = new URI(typeName);
					if (u.contains(atom)) {
						tuples.add(f.tuple(atom));
					}
				}
				if (tuples.isEmpty()) {
					return f.noneOf(1);
				} else {
					return f.setOf(tuples);
				}
			}
		};
	}
			
	public LazyTupleSet dateTypeTableBound(final Universe u, final TupleFactory f) throws URISyntaxException {
		return typeNameTableBound(ExpressionUtil.dateTypeNames, u, f);				
	}

	public LazyTupleSet numericTypeTableBound(final Universe u, final TupleFactory f) throws URISyntaxException {
		return typeNameTableBound(ExpressionUtil.numericTypeNames, u, f);				
	}

	public LazyTupleSet floatTypeTableBound(final Universe u, final TupleFactory f) throws URISyntaxException {
		return typeNameTableBound(ExpressionUtil.floatTypeNames, u, f);				
	}

	public LazyTupleSet booleanTypeTableBound(final Universe u, final TupleFactory tf) {
		return typeNameTableBound(Collections.singleton(ExpressionUtil.xsdBooleanType), u, tf);
	}

	public LazyTupleSet choiceBound(final TupleFactory tf) {
		return new LazyTupleSet() {
			public TupleSet tuples() {
				Collection<Tuple> tuples = HashSetFactory.make();
				tuples.add(tf.tuple(0));
				tuples.add(tf.tuple(1));
				return tf.setOf(tuples);
			}
		};
	}

	public LazyTupleSet numericTableBound(final TupleFactory f) {
		return new LazyTupleSet() {
			private void encode(final TupleFactory f, Collection<Tuple> tuples, Pair<String, ?> l, int v) {
				int msb = bitWidth - 1;
				if (v < 0) {
					tuples.add(f.tuple(l, -(1<<msb)));
					v = v + (1<<msb);
				}
				
				for(int i = msb; v > 0 && i >= 0; i--) {
					int x = (int) Math.pow(2, i);
					if (v >= x) {
						tuples.add(f.tuple(l, x));
						v -= x;
					}
				}
			}

			public TupleSet tuples() {
				Collection<Tuple> tuples = HashSetFactory.make();
				for(Pair<String,?> l : literals) {
					String type = String.valueOf(l.snd);
					try {
						if (type == ANY_NUMBER.toString()) {
							encode(f, tuples, l, Integer.MAX_VALUE/2 - 1);
						} else if (type.equals(xsdFloatType) || type.equals(xsdDoubleType) || type.equals(xsdDecimalType)) {
							encode(f, tuples, l, Float.floatToIntBits(Float.parseFloat(String.valueOf(l.fst))));		
						} else if (type.equals(xsdIntegerType)) {
							encode(f, tuples, l, Integer.parseInt(String.valueOf(l.fst)));
						} else {
							tuples.add(f.tuple(l, l.fst));							
						}
					} catch (NumberFormatException e) {
						tuples.add(f.tuple(l, l.fst));							
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
	
	public LazyTupleSet datatypeTableBound(final TupleFactory f) {
		return new LazyTupleSet() {
			public TupleSet tuples() {
				Collection<URI> numericTypes = Arrays.asList(typeURI(xsdIntegerType),typeURI(xsdFloatType),typeURI(xsdDecimalType),typeURI(xsdDoubleType));
				Collection<Tuple> tuples = HashSetFactory.make();
				for(Pair<String,?> l : literals) {
					if (l.snd instanceof URI) {
						if (ANY_IRI.equals(l.snd)) {
							for(URI type : iris) {
								if (! numericTypes.contains(type)) {
									tuples.add(f.tuple(l, type));		
								}
							}
						} else if (ANY_NUMBER.equals(l.snd)) {
							for(URI type : numericTypes) {
								tuples.add(f.tuple(l, type));
							}
						} else {
							tuples.add(f.tuple(l, l.snd));
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

	public LazyTupleSet blankNodeBound(final TupleFactory tf) {
		return new LazyTupleSet() {
			public TupleSet tuples() {
				TupleSet tuples = tf.noneOf(1);
				for(String bn : blankNodes) {
					tuples.add(tf.tuple(bn));
				}

				return tuples;
			}
		};
	}

	public TupleSet subjectsTableTuples(TupleFactory tf) {
		TupleSet tuples = tf.noneOf(1);
		for(Object bn : subjects) {
			tuples.add(tf.tuple(bn));
		}

		return tuples;
	}

	public LazyTupleSet subjectsTableBound(final TupleFactory tf, Set<Object> liveAtoms) {
		return new LazyTupleSet() {
			public TupleSet tuples() {
				return subjectsTableTuples(tf);
			}
		};
	}

	public TupleSet predicatesTableTuples(final TupleFactory tf) {
		TupleSet tuples = tf.noneOf(1);
		for(URI bn : predicates) {
			tuples.add(tf.tuple(bn));
		}

		return tuples;
	}

	public LazyTupleSet predicatesTableBound(final TupleFactory tf, Set<Object> liveAtoms) {
		return new LazyTupleSet() {
			public TupleSet tuples() {
				return predicatesTableTuples(tf);
			}
		};
	}

	public TupleSet objectsTableTuples(TupleFactory tf) {
		TupleSet tuples = tf.noneOf(1);
		for(Object bn : objects) {
			tuples.add(tf.tuple(bn));
		}

		return tuples;
	}

	public LazyTupleSet objectsTableBound(final TupleFactory tf, Set<Object> liveAtoms) {
		return new LazyTupleSet() {
			public TupleSet tuples() {
				return objectsTableTuples(tf);
			}
		};
	}

	public LazyTupleSet nodesTableBound(final TupleFactory tf, final Set<Object> liveAtoms, final boolean uris, final boolean blanks, final boolean all) {
		return new LazyTupleSet() {
			public TupleSet tuples() throws URISyntaxException {
				Set<Tuple> tuples = HashSetFactory.make();

				for(URI iri : graphs) {
					if (liveAtoms.contains(iri)) {
						tuples.add(tf.tuple(iri));
					}
				}

				if (uris) {
					for(URI iri : iris) {
						if (liveAtoms.contains(iri)) {
							tuples.add(tf.tuple(iri));
						}
					}

					if (blanks) {
						for(String bn : blankNodes) {
							if (liveAtoms.contains(bn)) {
								tuples.add(tf.tuple(bn));
							}
						}

						if (all) {
							for(Object lit : literals) {
								if (liveAtoms.contains(lit)) {
									tuples.add(tf.tuple(lit));
								}
							}
						}
					}
				}
				if (tuples.isEmpty()) {
					return tf.noneOf(1);
				} else {
					return tf.setOf(tuples);
				}
			}
		};
	}
	
	public LazyTupleSet languageTableBound(final TupleFactory f) {
		return new LazyTupleSet() {
			public TupleSet tuples() throws URISyntaxException {
				Collection<Tuple> tuples = HashSetFactory.make();
				for(Pair<String,?> l : literals) {
					if (l.snd instanceof String) {
						if (ANY_IRI.equals(l.snd)) {
							for(Pair<String,?> lang : literals) {
								tuples.add(f.tuple(l, lang));
							}
						} else {
							tuples.add(f.tuple(l, Pair.make(l.snd, null)));
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

	public LazyTupleSet languageCaseMatchBound(final TupleFactory f) {
		return new LazyTupleSet() {
			public TupleSet tuples() throws URISyntaxException {
				Collection<Tuple> tuples = HashSetFactory.make();
				for(Pair<String,?> l : literals) {
					if (l.snd instanceof String) {
						tuples.add(f.tuple(l.snd, ((String)l.snd).toLowerCase()));
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

	public LazyTupleSet valueTableBound(final TupleFactory f) {
		return new LazyTupleSet() {
			public TupleSet tuples() throws URISyntaxException {
				Collection<Tuple> tuples = HashSetFactory.make();
				for(Pair<String,?> l : literals) {
					tuples.add(f.tuple(l, l.fst));
				}
				if (tuples.isEmpty()) {
					return f.noneOf(2);
				} else {
					return f.setOf(tuples);
				}
			}
		};
	}

	public LazyTupleSet stringOrderBound(final TupleFactory f) {
		return new LazyTupleSet() {
			public TupleSet tuples() throws URISyntaxException {
				Collection<Tuple> tuples = HashSetFactory.make();
				for(Pair<String,?> l : literals) {
					if (l.snd == null || l.snd.toString().equals(ExpressionUtil.xsdStringType)) {
						tuples.add(f.tuple(l.fst));
					}
				}
				if (tuples.isEmpty()) {
					return f.noneOf(2);
				} else {
					SortedSet<Tuple> order = new TreeSet<Tuple>(new Comparator<Tuple>() {
						@Override
						public int compare(Tuple o1, Tuple o2) {
							return o1.atom(0).toString().compareTo(o2.atom(0).toString());
						}	
					});
					order.addAll(tuples);
					TupleSet t = f.noneOf(2);
					Iterator<Tuple> orderedTuples = order.iterator();
					Tuple l = orderedTuples.next();
					while (orderedTuples.hasNext()) {
						Tuple r = orderedTuples.next();
						t.add(l.product(r));
						l = r;
					}
					return t;
				}
			}
		};
	}

	@Override
	public Relation atomRelation(Object atom) {
		if (!atomRelations.containsKey(atom)) {
			atomRelations.put(atom, Relation.unary(String.valueOf(atom)));
		}
		return atomRelations.get(atom);
	}

	private final Set<Relation> nodeRelations = HashSetFactory.make();
	
	@Override
	public void nodesRelation(Relation r) {
		nodeRelations.add(r);
	}

	private SolutionRelation solution = null;

	public void addSolution(SolutionRelation solution) {
		this.solution = solution;
		solution.init(this);
	}

	protected void initDataset(Dataset datasetModel) throws URISyntaxException {
		for(Iterator<Quad> quads = datasetModel.asDatasetGraph().find(); quads.hasNext(); ) {
			Quad q = quads.next();
			
			if (! q.isDefaultGraph()) {
				assert q.getGraph().isURI();
				URI g = JenaUtil.toURI(q.getGraph());
				ensureIRI(g);
				graphs.add(g);
			}
			
			assert q.getSubject().isURI() || q.getSubject().isBlank();
			if (q.getSubject().isBlank()) {
				String blank = JenaUtil.toBlank(q.getSubject());
				ensureBlankNode(blank);
				subjects.add(blank);
			} else {
				URI uri = JenaUtil.toURI(q.getSubject());
				ensureIRI(uri);
				subjects.add(uri);
			}
			
			assert q.getPredicate().isURI();
			URI uri = JenaUtil.toURI(q.getPredicate());
			ensureIRI(uri);
			predicates.add(uri);
			
			if (q.getObject().isURI()) {
				URI uri2 = JenaUtil.toURI(q.getObject());
				ensureIRI(uri2);
				objects.add(uri2);
			} else if (q.getObject().isBlank()) {
				String blank = JenaUtil.toBlank(q.getObject());
				ensureBlankNode(blank);
				objects.add(blank);
			} else {
				assert q.getObject().isLiteral();
				LiteralLabel l = q.getObject().getLiteral();
				Pair<String, Object> atom = JenaUtil.toAtom(l);
				ensureLiteral(atom);
				objects.add(atom);
			}
		}
	}

	protected LazyTupleSet relationTableBound(final TupleFactory f, final Iterator<Quad> quads, final Set<Object> liveAtoms)
			throws URISyntaxException {
				return new LazyTupleSet() {
					public TupleSet tuples() throws URISyntaxException {
						Collection<Tuple> tuples = HashSetFactory.make();
						while(quads.hasNext()) {
							Quad q = quads.next();
							Object subject = JenaUtil.toAtom(q.getSubject());
							Object predicate = JenaUtil.toAtom(q.getPredicate());
							Object object = JenaUtil.toAtom(q.getObject());
							if (q.isDefaultGraph()) {
								tuples.add(f.tuple(QuadTableRelations.defaultGraph, subject, predicate, object));
							} else {
								Object graph = JenaUtil.toAtom(q.getGraph());
								tuples.add(f.tuple(graph, subject, predicate, object));				
							}
						}
						if (tuples.isEmpty()) {
							return f.noneOf(4);
						} else {
							return f.setOf(tuples);
						}
					}
				};
			}

	protected LazyTupleSet dataSetCrossProduct(final TupleFactory f) {
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
		return s;
	}

}
