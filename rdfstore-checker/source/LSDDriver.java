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
import com.ibm.research.rdf.store.sparql11.ASTUtils;
import com.ibm.research.rdf.store.sparql11.BasicUniverse;
import com.ibm.research.rdf.store.sparql11.Drivers;
import com.ibm.research.rdf.store.sparql11.JenaTranslator;
import com.ibm.research.rdf.store.sparql11.JenaUtil;
import com.ibm.research.rdf.store.sparql11.OpenDatasetUniverse;
import com.ibm.wala.util.collections.Pair;

import kodkod.ast.Formula;
import kodkod.ast.Relation;
import kodkod.instance.TupleSet;

public class LSDDriver {

	public static void main(String[] args) throws URISyntaxException, IOException {
		BasicUniverse U = new OpenDatasetUniverse(new URL(args[0]));
//		BasicUniverse U = new BoundedUniverse();
		Query ast = JenaUtil.parse(args[1]);
		Op query = JenaUtil.compile(ast);
		
		System.err.println(query);
		
		JenaTranslator xlator = JenaTranslator.make(ast.getProjectVars(), Collections.singleton(query), U, null);

		if (args.length > 2 && "expand".equals(args[2])) {
			Dataset dataset = DatasetFactory.createMem();
			Graph G = dataset.asDatasetGraph().getDefaultGraph();
			Set<Pair<Formula, Pair<Formula, Formula>>> fs = xlator.translateSingle(Collections.<String,Object>emptyMap(), true);
			for(Pair<Formula, Pair<Formula, Formula>> f : fs) {
				System.err.println(f);
				
				for(Relation r : ASTUtils.gatherRelations(f.fst)) {
					if (r.name().equals("solution")) {
						f = Pair.make(f.fst.and(r.some()), f.snd);
						break;
					}
				}
				
				TupleSet t = Drivers.check(U, f, "quads");
				if (t != null) {
					JenaUtil.addTupleSet(G, t);
				}
			}
			RDFDataMgr.write(System.out, dataset, Lang.NQ);

		} else {
			Pair<Formula, Pair<Formula, Formula>> xlation = xlator.translateSingle(Collections.<String,Object>emptyMap(), false).iterator().next();
		
			System.err.println(xlation.fst);
			Drivers.check(U, xlation, "solution");
		}
	}

}
