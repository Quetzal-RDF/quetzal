package com.ibm.research.rdf.store.sparql11.semantics;

import static com.ibm.research.rdf.store.sparql11.semantics.ExpressionUtil.bitWidth;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.graph.impl.LiteralLabel;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.AnonId;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.sparql.algebra.Algebra;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.OpVisitorBase;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.algebra.op.OpDistinct;
import org.apache.jena.sparql.algebra.op.OpExtend;
import org.apache.jena.sparql.algebra.op.OpFilter;
import org.apache.jena.sparql.algebra.op.OpGraph;
import org.apache.jena.sparql.algebra.op.OpGroup;
import org.apache.jena.sparql.algebra.op.OpJoin;
import org.apache.jena.sparql.algebra.op.OpLeftJoin;
import org.apache.jena.sparql.algebra.op.OpMinus;
import org.apache.jena.sparql.algebra.op.OpOrder;
import org.apache.jena.sparql.algebra.op.OpPath;
import org.apache.jena.sparql.algebra.op.OpProject;
import org.apache.jena.sparql.algebra.op.OpService;
import org.apache.jena.sparql.algebra.op.OpSlice;
import org.apache.jena.sparql.algebra.op.OpTable;
import org.apache.jena.sparql.algebra.op.OpUnion;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.expr.ExprAggregator;

import com.ibm.wala.util.collections.HashSetFactory;
import com.ibm.wala.util.collections.Pair;

import kodkod.ast.Relation;
import kodkod.engine.Evaluator;
import kodkod.engine.config.Options;
import kodkod.engine.config.Options.IntEncoding;
import kodkod.engine.fol2sat.UnboundLeafException;
import kodkod.instance.Instance;
import kodkod.instance.Tuple;
import kodkod.instance.TupleSet;

public class JenaUtil {

	static URI toURI(Node n) throws URISyntaxException {
		return new URI(n.getURI());
	}

	static Node fromURI(String uri) {
		return NodeFactory.createURI(uri);
	}
	
	static String toBlank(Node n) {
		return n.getBlankNodeLabel();
	}

	static Node fromBlank(String s) {
		return NodeFactory.createAnon(s);
	}
		
	public static Object toAtom(Node n) throws URISyntaxException {
		if (n.isURI()) {
			return toURI(n);
		} else if (n.isBlank()) {
			return toBlank(n);
		} else {
			assert n.isLiteral();
			return toAtom(n.getLiteral());
		}
	}

	public static Query parse(String queryString) throws MalformedURLException, IOException {
		if (new URL(queryString).openConnection().getInputStream() != null) {
			return QueryFactory.read(queryString);
		} else {
			return QueryFactory.create(queryString);
		}
	}

	public static Op compile(String queryString) throws MalformedURLException, IOException {
		return Algebra.compile(parse(queryString));
	}

	public static Op compile(Query query) throws MalformedURLException, IOException {
		return Algebra.compile(query);
	}
	
	@SuppressWarnings("unchecked")
	static RDFNode fromLiteral(Model m, Pair<String,Object> o, BasicUniverse u, Instance t2) {
		try {
			Relation me = Relation.unary("me");
			t2.add(me, t2.universe().factory().setOf(t2.universe().factory().tuple(o)));
			Options opt = new Options();
			opt.setIntEncoding(IntEncoding.TWOSCOMPLEMENT);
			opt.setBitwidth(bitWidth);
			Evaluator e = new Evaluator(t2, opt);
			if (o.snd instanceof String) {
				if (t2.contains(QuadTableRelations.literalLanguages)) {
					TupleSet langs = e.evaluate(me.join(QuadTableRelations.literalLanguages));
					if (! langs.isEmpty()) {
						return m.createLiteral(o.fst, ((Pair<String,?>)langs.iterator().next().atom(0)).fst);
					}
				}
				return m.createLiteral(o.fst, (String)o.snd);				
			} else if (o.snd == null) {
				return m.createLiteral(o.fst);			
			} else {
				if (t2.contains(QuadTableRelations.literalDatatypes)) {
					TupleSet tt = e.evaluate(me.join(QuadTableRelations.literalDatatypes));
					Iterator<Tuple> types = tt.iterator();
					if (types.hasNext()) {
						String type = String.valueOf(types.next().atom(0));
						if (ExpressionUtil.numericTypeNames.contains(type)) {
							int v = e.evaluate(me.join(QuadTableRelations.literalValues).sum());
							return m.createTypedLiteral(v, type);
						} else {
							Object v = e.evaluate(me.join(QuadTableRelations.literalValues)).iterator().next().atom(0);
							return m.createTypedLiteral(v, type);			
						}	
					}
				}
				if (t2.contains(QuadTableRelations.literalValues) && e.evaluate(me.join(QuadTableRelations.literalValues)).iterator().hasNext()) {
					Object v = e.evaluate(me.join(QuadTableRelations.literalValues)).iterator().next().atom(0);
					return m.createTypedLiteral(v, o.snd.toString());			
				}
				return m.createTypedLiteral(o.fst, o.snd.toString());			
			}
		} catch (UnboundLeafException | NoSuchElementException ee) {
			return m.createTypedLiteral(o.fst, String.valueOf(o.snd));
		}
	}

	@SuppressWarnings("unchecked")
	public static RDFNode fromAtom(Model m, Object o, BasicUniverse u, Instance t2) {
		if (o instanceof Pair<?,?>) {
			return fromLiteral(m, (Pair<String,Object>) o, u, t2);
		} else if (o.toString().startsWith("_:")) {
			return m.createResource(new AnonId(o.toString()));
		} else {
			return m.createResource(o.toString());
		}
	}
	
	public static Statement fromTuple(Model m, Tuple t, BasicUniverse u, Instance t2) {
		Resource s = (Resource) fromAtom(m, t.atom(1), u, t2);
		Property p = m.createProperty(String.valueOf(t.atom(2)));
		RDFNode o = fromAtom(m, t.atom(3), u, t2);
		return m.createStatement(s, p, o);
	}
	
	public static void addTupleSet(Dataset dataset, TupleSet tt, BasicUniverse u, Instance t2) {
		if (tt != null) {
			for(Tuple t : tt) {
				Object graph = t.atom(0);
				Model m = QuadTableRelations.defaultGraph.equals(graph)? dataset.getDefaultModel(): dataset.getNamedModel(graph.toString());
				m.add(fromTuple(m, t, u, t2));
			}
		}
	}
	
	public static Pair<String, Object> toAtom(LiteralLabel l) {
		Object snd = null;
		if (l.language() != null && !"".equals(l.language())) {
			snd = l.language().toLowerCase();
		} else if (l.getDatatypeURI() != null) {
			try {
				snd = new URI(l.getDatatypeURI());
			} catch (URISyntaxException e) {
				assert false : l.getDatatypeURI() + " is not a datatype";
			}
		}
		return Pair.make(l.getLexicalForm(), snd);
	}

	public static Set<String> scope(Op top) {
		final Set<String> result = HashSetFactory.make();
		top.visit(new OpVisitorBase() {

			@Override
			public void visit(OpMinus opMinus) {
				opMinus.getLeft().visit(this);
			}

			@Override
			public void visit(OpFilter opFilter) {
				opFilter.getSubOp().visit(this);
			}

			@Override
			public void visit(OpBGP opBGP) {
				for(Triple t : opBGP.getPattern().getList()) {
					if (t.getSubject().isVariable()) {
						result.add(t.getSubject().getName());
					}
					if (t.getPredicate().isVariable()) {
						result.add(t.getPredicate().getName());
					}
					if (t.getObject().isVariable()) {
						result.add(t.getObject().getName());
					}
				}
			}

			@Override
			public void visit(OpPath opPath) {
				TriplePath t = opPath.getTriplePath();
				if (t.getSubject().isVariable()) {
					result.add(t.getSubject().getName());
				}
				if (t.getObject().isVariable()) {
					result.add(t.getObject().getName());
				}
			}

			@Override
			public void visit(OpJoin opJoin) {
				opJoin.getLeft().visit(this);
				opJoin.getRight().visit(this);
			}

			@Override
			public void visit(OpLeftJoin opLeftJoin) {
				opLeftJoin.getLeft().visit(this);
				opLeftJoin.getRight().visit(this);
			}

			@Override
			public void visit(OpUnion opUnion) {
				opUnion.getLeft().visit(this);
				opUnion.getRight().visit(this);
			}

			@Override
			public void visit(OpGraph opGraph) {
				opGraph.getSubOp().visit(this);
				if (opGraph.getNode().isVariable()) {
					result.add(opGraph.getNode().getName());
				}
			}

			@Override
			public void visit(OpService opService) {
				opService.getSubOp().visit(this);
				if (opService.getService().isVariable()) {
					result.add(opService.getService().getName());
				}
			}

			@Override
			public void visit(OpTable opTable) {
				result.addAll(opTable.getTable().getVarNames());
			}

			@Override
			public void visit(OpExtend opExtend) {
				opExtend.getSubOp().visit(this);
				for(Var v : opExtend.getVarExprList().getVars()) {
					result.add(v.getName());
				}
			}

			@Override
			public void visit(OpProject opProject) {
				opProject.getSubOp().visit(this);
				for (Var v : opProject.getVars()) {
					result.add(v.getName());
				}
			}

			@Override
			public void visit(OpGroup opGroup) {
				opGroup.getSubOp().visit(this);
				for (Var v : opGroup.getGroupVars().getVars()) {
					result.add(v.getName());
				}
				for(ExprAggregator agg : opGroup.getAggregators()) {
					result.add(agg.getAggVar().getVarName());
				}
			}

			@Override
			public void visit(OpDistinct opDistinct) {
				opDistinct.getSubOp().visit(this);
			}

			@Override
			public void visit(OpOrder opOrder) {
				opOrder.getSubOp().visit(this);
			}

			@Override
			public void visit(OpSlice opSlice) {
				opSlice.getSubOp().visit(this);
			}
		});
		return result;
	}
}
