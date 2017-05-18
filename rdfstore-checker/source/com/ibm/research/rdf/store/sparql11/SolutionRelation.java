package com.ibm.research.rdf.store.sparql11;

import java.net.URISyntaxException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import kodkod.ast.Expression;
import kodkod.ast.Relation;
import kodkod.instance.Bounds;
import kodkod.instance.TupleFactory;
import kodkod.instance.TupleSet;

import com.hp.hpl.jena.sparql.core.Var;
import com.ibm.rdf.store.dawg.queries.SparqlSelectResult;
import com.ibm.rdf.store.dawg.queries.SparqlSelectResult.Row;
import com.ibm.research.rdf.store.sparql11.model.QueryTripleTerm;
import com.ibm.research.rdf.store.sparql11.model.Variable;
import com.ibm.wala.util.collections.HashSetFactory;
import com.ibm.wala.util.collections.Iterator2Set;

public class SolutionRelation {
	private final SparqlSelectResult solution;

	private boolean hasBlankNodes = false;
	
	private Relation solutionRelation;
	
	private Set<String> usedVarNames;
	
	private Map<String, Object> bindings;
	
	private SolutionRelation(SparqlSelectResult solution, Set<String> vars, Map<String, Object> bindings) {
		this.solution = solution;
		this.usedVarNames = vars;
		this.bindings = bindings;
		System.err.println(vars);
	}

	private static Set<String> toNames(List<Var> vars) {
		Set<String> usedVarNames = HashSetFactory.make();
		for(Var v : vars) {
			usedVarNames.add(v.getVarName());
		}
		return usedVarNames;
	}
	
	public SolutionRelation(SparqlSelectResult solution, List<Var> vars, Map<String, Object> bindings) {
		this(solution, toNames(vars), bindings);
	}

	private static Set<String> toNames(Iterator2Set<com.ibm.research.rdf.store.sparql11.model.Variable> vars) {
		Set<String> usedVarNames = HashSetFactory.make();
		for(Variable v : vars) {
			usedVarNames.add(v.getName());
		}
		return usedVarNames;
	}

	public SolutionRelation(SparqlSelectResult solution, Iterator2Set<com.ibm.research.rdf.store.sparql11.model.Variable> vars, Map<String, Object> bindings) {
		this(solution, toNames(vars), bindings);
	}

	public SortedSet<Variable> variables() {
		TreeSet<Variable> names = new TreeSet<Variable>(new Comparator<Variable>() {
			@Override
			public int compare(Variable o1, Variable o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		for(Iterator<Variable> vs = solution.variables(); vs.hasNext(); ) {
			Variable v = vs.next();
			if (usedVarNames.contains(v.getName())) {
				names.add(v);
			}
		}
		return names;
	}
	
	public Relation solutionRelation() {
		return solutionRelation;
	}
	
	public void init(UniverseFactory universe) {
		try {
			SortedSet<Variable> vars = variables();
			
			if (! variables().isEmpty()) {
				solutionRelation = Relation.nary("expected_solution", vars.size());
			}
			
			for(Iterator<Row> rows = solution.rows(); rows.hasNext(); ) {
				Row row = rows.next();
				for(Iterator<Variable> vs = solution.variables(); vs.hasNext(); ) {
					Variable v = vs.next();
										
					QueryTripleTerm val = row.get(v);
					if (val == null) {
						continue;
					}
					
					if (val.isIRI()) {
						universe.ensureIRI(QueryTripleTermUtil.toURI(val));
					} else if (val.isBlankNode()) {
						hasBlankNodes = true;
						universe.ensureBlankNode(val.getBlankNode().getName());
					} else if (val.isConstant()) {
						universe.ensureLiteral(QueryTripleTermUtil.toLiteral(val));
					}
				}
			}
		} catch (URISyntaxException e) {
			assert false : "bad element in solution: " + e;
		}
	}

	public boolean hasBlankNodes() {
		return hasBlankNodes;
	}
	
	public void bound(Bounds b, TupleFactory tf) {
		try {
			SortedSet<Variable> vars = variables();
			TupleSet bound = tf.noneOf(vars.size());
			rows: for(Iterator<Row> rows = solution.rows(); rows.hasNext(); ) {
				Row row = rows.next();
				
				for(Iterator<Variable> vs = solution.variables(); vs.hasNext(); ) {
					Variable v = vs.next();
					Object o = QueryTripleTermUtil.toAtom(row.get(v));
					if (bindings.containsKey(v.getName())) {
						if (! bindings.get(v.getName()).equals(o)) {
							continue rows;
						}
					}
				}
				
				int i = 0;
				Object[] atoms = new Object[ usedVarNames.size()];
				for(Iterator<Variable> vs = vars.iterator(); vs.hasNext(); ) {
					Variable v = vs.next();
					Object o = QueryTripleTermUtil.toAtom(row.get(v));
					if (usedVarNames.contains(v.getName())) {
						atoms[i++] = o;
					}
				}
				bound.add(tf.tuple(atoms));
			}

			System.err.println("bound " + bound);
			
			b.boundExactly(solutionRelation, bound);
			
		} catch (URISyntaxException e) {
			assert false : "bad solution element: " + e;
		}
	}
	
	public Expression mappedSolution(UniverseFactory uf) {
		try {
			SortedSet<Variable> vars = variables();
			Expression bound = null;
			rows: for(Iterator<Row> rows = solution.rows(); rows.hasNext(); ) {
				Row row = rows.next();

				for(Iterator<Variable> vs = solution.variables(); vs.hasNext(); ) {
					Variable v = vs.next();
					Object o = QueryTripleTermUtil.toAtom(row.get(v));
					if (bindings.containsKey(v.getName())) {
						if (! bindings.get(v.getName()).equals(o)) {
							continue rows;
						}
					}
				}

				Expression r = null;
				for(Iterator<Variable> vs = vars.iterator(); vs.hasNext(); ) {
					Variable v = vs.next();
					if (usedVarNames.contains(v.getName())) {
						QueryTripleTerm d = row.get(v);
						Expression x = uf.atomRelation(QueryTripleTermUtil.toAtom(d));
						if (d != null && d.isBlankNode()) {
							x = x.in(QuadTableRelations.blankNodes).thenElse(QuadTableRelations.blankNodeRenaming.join(x), x);
						}
						r = r==null? x: r.product(x);
					}
				}
				bound = bound==null? r: bound.union(r);
			}

			return bound;			
		} catch (URISyntaxException e) {
			assert false : "bad solution element: " + e;
			return null;
		}
	}

	public boolean hasSolutions() {
		return solution.rows().hasNext();
	}
}
