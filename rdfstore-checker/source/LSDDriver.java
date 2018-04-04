import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.Set;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.ibm.rdf.store.dawg.queries.SparqlRdfResultReader;
import com.ibm.rdf.store.dawg.queries.SparqlSelectResult;
import com.ibm.research.rdf.store.sparql11.ASTUtils;
import com.ibm.research.rdf.store.sparql11.BasicUniverse;
import com.ibm.research.rdf.store.sparql11.BoundedUniverse;
import com.ibm.research.rdf.store.sparql11.DatasetUniverse;
import com.ibm.research.rdf.store.sparql11.Drivers;
import com.ibm.research.rdf.store.sparql11.JenaTranslator;
import com.ibm.research.rdf.store.sparql11.JenaUtil;
import com.ibm.research.rdf.store.sparql11.SolutionRelation;
import com.ibm.wala.util.collections.Pair;

import kodkod.ast.Formula;
import kodkod.ast.Relation;
import kodkod.instance.TupleSet;

public class LSDDriver {

	public static void main(String[] args) throws URISyntaxException, IOException {
		Query ast = JenaUtil.parse(args[1]);
		Op query = JenaUtil.compile(ast);

		System.err.println(query);

		BasicUniverse U = new DatasetUniverse(new URL(args[0]));
		SparqlSelectResult answer = new SparqlRdfResultReader(args[2]);
		SolutionRelation s = new SolutionRelation(answer, ast.getProjectVars(), Collections.<String,Object>emptyMap());
		s.init(U);
		JenaTranslator xlator = JenaTranslator.make(ast.getProjectVars(), Collections.singleton(query), U, s);
		Pair<Formula, Pair<Formula, Formula>> xlation = xlator.translateSingle(Collections.<String,Object>emptyMap(), false).iterator().next();
		System.err.println(xlation.fst);
		Drivers.check(U, xlation, "solution");
		
		U = new BoundedUniverse();
		xlator = JenaTranslator.make(ast.getProjectVars(), Collections.singleton(query), U, null);
		Formula f = Formula.TRUE;
		Formula s1 = null;
		Formula s2 = null;
		Dataset dataset = DatasetFactory.createMem();
		Graph G = dataset.asDatasetGraph().getDefaultGraph();
		Set<Pair<Formula, Pair<Formula, Formula>>> fs = xlator.translateSingle(Collections.<String,Object>emptyMap(), true);
		for(Pair<Formula, Pair<Formula, Formula>> p : fs) {
			for(Relation r : ASTUtils.gatherRelations(p.fst)) {
				if (r.name().equals("solution")) {
					f = f.and(p.fst.and(r.some()));
					s1 = s1==null? p.snd.fst: p.snd.fst==null? s1: p.snd.fst.and(s1);
					s2 = s2==null? p.snd.snd: p.snd.snd==null? s2: p.snd.snd.and(s2);
					break;
				}
			}
		}

		System.err.println(f);
		TupleSet t = Drivers.check(U, Pair.make(f,  Pair.make(s1, s2)), "quads");
		if (t != null) {
			JenaUtil.addTupleSet(G, t);
		}

		RDFDataMgr.write(System.out, dataset, Lang.NQ);
	}
}
