package com.ibm.research.rdf.store.sparql11.semantics;

import static com.ibm.research.rdf.store.sparql11.semantics.ExpressionUtil.bitWidth;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.core.Var;
import com.ibm.research.kodkod.util.Nodes;
import com.ibm.research.rdf.store.sparql11.model.Variable;
import com.ibm.research.rdf.store.utilities.io.SparqlRdfResultReader;
import com.ibm.research.rdf.store.utilities.io.SparqlSelectResult;
import com.ibm.research.rdf.store.utilities.io.SparqlSelectResult.Row;
import com.ibm.research.rdf.store.utilities.io.SparqlXmlResultReader;
import com.ibm.wala.util.collections.HashMapFactory;
import com.ibm.wala.util.collections.HashSetFactory;
import com.ibm.wala.util.collections.Pair;
import com.ibm.wala.util.functions.Function;

import kodkod.ast.Formula;
import kodkod.ast.IntConstant;
import kodkod.ast.Relation;
import kodkod.engine.Evaluator;
import kodkod.engine.Solution;
import kodkod.engine.Solution.Outcome;
import kodkod.engine.Solver;
import kodkod.engine.config.Options.IntEncoding;
import kodkod.engine.satlab.SATFactory;
import kodkod.instance.Bounds;
import kodkod.instance.Instance;
import kodkod.instance.Tuple;
import kodkod.instance.TupleSet;

public class Drivers {


	public static class DumpSolution {
		public static void main(String[] args) throws MalformedURLException, ParserConfigurationException, SAXException, IOException {
			SparqlSelectResult r = getSolution(args[0]);
			showSolution(r);
		}
	}
	
	public static class DumpUniverse {
		public static void main(String[] args) throws MalformedURLException, URISyntaxException {
			UniverseFactory ds = new DatasetUniverse(new URL(args[0]));
			Bounds b = ds.boundUniverse(null);
			System.err.println(b);
		}
	}
			
	public static SparqlSelectResult getSolution(String arg)
			throws ParserConfigurationException, SAXException, IOException,
			MalformedURLException {
		SparqlSelectResult r = null;
		if (arg.endsWith("srx")) {
			r = new SparqlXmlResultReader(new URL(arg).openStream());
		} else if (arg.endsWith("ttl")) {
			r = new SparqlRdfResultReader(arg);
		} else {
			assert false : "support " + arg;
		}
		return r;
	}

	public static void showSolution(SparqlSelectResult r) {
		for (Iterator<Row> rows = r.rows(); rows.hasNext(); ) {
			Row row = rows.next();
			for(Iterator<Variable> vars = r.variables(); vars.hasNext(); ) {
				Variable v = vars.next();
				System.out.print(v.getName() + "->" + row.get(v) + " ");
			}
			System.out.println("");
		}
	}

	public static void run(String queryFile1, String queryFile2, boolean leftNonEmpty, boolean rightNonEmpty, int bound) throws URISyntaxException, MalformedURLException, IOException {
		UniverseFactory uf = new BoundedUniverse();

		Query q1 = JenaUtil.parse(queryFile1);
		Op query1 = JenaUtil.compile(q1);

		Query q2 = JenaUtil.parse(queryFile2);
		Op query2 = JenaUtil.compile(q2);

		JenaTranslator jt = JenaTranslator.make(q1.getProjectVars(), Arrays.asList(query1, query2), uf, null);
		Pair<Formula, Pair<Formula, Formula>> answer = jt.translateMulti(leftNonEmpty, rightNonEmpty);
		
		if (bound > 0) {
			answer = Pair.make(answer.fst.and(QuadTableRelations.quads.count().lte(IntConstant.constant(bound))), answer.snd);
		}
		
		System.err.println(answer.fst);
		
		check(uf, answer, "solution");
	}
	
	public static void runVerify(URL dataSet, String queryFile, SparqlSelectResult result) throws URISyntaxException,
			MalformedURLException, ParserConfigurationException, SAXException,
			IOException {
		
		Query q = JenaUtil.parse(queryFile);
		List<Var> vars = q.getProjectVars();

		tryToCheck(dataSet, result, q, vars, Collections.<String,Object>emptyMap(), "solution", false);
	}

	private static Map<String, Object> tupleBindings(List<Var> vars, List<Object> t) {
		assert vars.size() == t.size();
		Map<String, Object> result = HashMapFactory.make();
		
		vars = JenaTranslator.sortVars(vars, new Function<Var,String>() {
			@Override
			public String apply(Var arg0) {
				return arg0.getVarName();
			}			
		});

		for(int i = 0; i < vars.size(); i++) {
			result.put(vars.get(i).getVarName(), t.get(i));
		}
		
		System.err.println(result);
		return result;
	}
	
	private static Set<List<Object>> tryToCheck(URL dataSet, SparqlSelectResult result,
			Query q, List<Var> vars, Map<String, Object> bindings, String relation, boolean expand) throws URISyntaxException,
			MalformedURLException, IOException {

		if (vars.size() <= 3) {
			BasicUniverse uf = new DatasetUniverse(dataSet);
			SolutionRelation solutionRelation;
			uf.addSolution(solutionRelation = new SolutionRelation(result, vars, bindings));

			Op query = JenaUtil.compile(q);
			System.err.println(query);
			JenaTranslator jt = JenaTranslator.make(vars, query, uf, solutionRelation);
			Set<Pair<Formula, Pair<Formula, Formula>>> answer = jt.translateSingle(bindings, expand);

			Set<List<Object>> r = HashSetFactory.make();
			
			for(Pair<Formula, Pair<Formula, Formula>> a : answer) {
				TupleSet tuples = check(uf, a, relation);
				if (q.isAskType()) {
					return null;
				} else {
					if (tuples == null) {
						continue;
					} else {
						for(Tuple t : tuples) {
							List<Object> bt = new ArrayList<Object>(vars.size());
							for (int i = 0; i < t.arity(); i++) {
								bt.add(t.atom (i));
							}
							r.add(bt);
						}
					}
				}
			}
			return r;
		} else {
			List<Var> split = new LinkedList<Var>();
			Iterator<Var> vs = vars.iterator();
			vs.next();
			while (vs.hasNext()) {
				if (vs.hasNext()) {
					split.add(vs.next());
				}
			}
			List<Var> newVars = new ArrayList<Var>(vars);
			for(Var v : split) {
				System.err.println("removing " + v + " from " + newVars);
				newVars.remove(v);
			}
			
			assert newVars.size() < vars.size();
			
			Set<List<Object>> subset = tryToCheck(dataSet, result, q, newVars, bindings, relation, expand);
			Set<List<Object>> fullset = HashSetFactory.make();
			for(List<Object> t : subset) {
				Map<String, Object> newBindings = tupleBindings(newVars, t);
				newBindings.putAll(bindings);
				for (List<Object> ext : tryToCheck(dataSet, result, q, split, newBindings, relation, expand)) {
					List<Object> bt = new ArrayList<Object>(vars.size());
					for (int i = 0; i < ext.size(); i++) {
						bt.add(ext.get(i));
					}
					for(int i = 0; i < t.size(); i++) {
						bt.add(t.get(i));
					}
					fullset.add(bt);
				}
				
			}
			System.err.println("combined solution: " + fullset);
			return fullset;
		}
	}

	public static void runFabricate(String queryFile, SparqlSelectResult result) throws URISyntaxException,
			MalformedURLException, ParserConfigurationException, SAXException,
			IOException {
		Query q = JenaUtil.parse(queryFile);
		List<Var> vars = q.getProjectVars();

		UniverseFactory uf = new BoundedUniverse();

		SolutionRelation r = null;
		if (result != null) {
			uf.addSolution(r = new SolutionRelation(result, vars, Collections.<String,Object>emptyMap()));
		}

		Op query = JenaUtil.compile(q);
		JenaTranslator jt = r==null? JenaTranslator.make(vars, query, uf): JenaTranslator.make(vars, query, uf, r);
		Pair<Formula, Pair<Formula, Formula>> answer = jt.translateSingle(Collections.<String,Object>emptyMap(), false).iterator().next();

		check(uf, answer, "solution");
	}

	public static void runRepair(URL datasetURL, String queryFile, SparqlSelectResult result) throws URISyntaxException,
			MalformedURLException, ParserConfigurationException, SAXException,
			IOException 
	{
		Query q = JenaUtil.parse(queryFile);
		List<Var> vars = q.getProjectVars();

		UniverseFactory uf = new ComparisonUniverse(datasetURL);

		SolutionRelation r = null;
		if (result != null) {
			uf.addSolution(r = new SolutionRelation(result, vars, Collections.<String,Object>emptyMap()));
		}

		Op query = JenaUtil.compile(q);
		JenaTranslator jt = r==null? JenaTranslator.make(vars, query, uf): JenaTranslator.make(vars, query, uf, r);
		Pair<Formula, Pair<Formula, Formula>> answer = jt.translateSingle(Collections.<String,Object>emptyMap(), false).iterator().next();

		Formula minDiff =
			QuadTableRelations.quads.union(QuadTableRelations.desiredQuads).count().minus(
				QuadTableRelations.quads.intersection(QuadTableRelations.desiredQuads).count()).lte(IntConstant.constant(1));
		
		answer = Pair.make(answer.fst.and(minDiff), Pair.make(answer.snd.fst.and(minDiff), answer.snd.snd.and(minDiff)));

		check(uf, answer, "solution");
	}

	public static TupleSet check(UniverseFactory uf,
			Pair<Formula, Pair<Formula, Formula>> answer,
			String relation)
			throws URISyntaxException {
		Instance rels = check(uf, answer);
		if (rels != null) {
			for(Relation r : rels.relations()) {
				if (r.toString().equals(relation)) {
					return rels.tuples(r);
				}
			}
			return null;
		} else {
			return null;
		}
	}

	public static Instance check(UniverseFactory uf,
			Pair<Formula, Pair<Formula, Formula>> answer)
			throws URISyntaxException {
		return check(uf, SATFactory.MiniSat, answer);
	}
	
	public static Instance check(UniverseFactory uf,
			SATFactory sat,
			Pair<Formula, Pair<Formula, Formula>> answer)
			throws URISyntaxException {
		Formula qf = answer.fst;		
		if (answer.snd.fst != null) {
			qf = qf.and(answer.snd.fst);
		}

		Formula cf = answer.fst;
		if (answer.snd.snd != null) {
			cf = cf.and(answer.snd.snd);
		}
		
		Set<Relation> liveRelations = ASTUtils.gatherRelations(qf.and(cf));
				
		Bounds b = uf.boundUniverse(liveRelations);
		
		Solver solver = new Solver();
		solver.options().setSolver(sat);
		solver.options().setIntEncoding(IntEncoding.TWOSCOMPLEMENT);
		solver.options().setBitwidth(bitWidth);
		//solver.options().setSkolemDepth(-1);
		solver.options().setSymmetryBreaking(0);
		solver.options().setSharing(1);
		Formula f = Nodes.simplify(qf, b);

		Solution s = solver.solve(f, b);
		
		if (s.outcome() == Outcome.SATISFIABLE || s.outcome() == Outcome.TRIVIALLY_SATISFIABLE) {
			Instance instance = s.instance();
				
			System.err.println(instance);
			return instance;
		} else {
			if (answer.snd.snd != null) {				
				Solution diff = solver.solve(Nodes.simplify(cf, b), b);

				Evaluator eval = new Evaluator(diff.instance());

				for(Relation rl : diff.instance().relations()) {
					if ("extra_solutions".equals(rl.name()) || "missing_solutions".equals(rl.name())) {
						System.err.println(rl.name() + ":\n" + eval.evaluate(rl));
					}
				}

			}
			return null;
		}
	}
}
