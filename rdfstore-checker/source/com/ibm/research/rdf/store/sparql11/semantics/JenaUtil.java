package com.ibm.research.rdf.store.sparql11.semantics;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.xsd.impl.XMLLiteralType;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.impl.LiteralLabel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.sparql.algebra.Algebra;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.OpVisitorBase;
import com.hp.hpl.jena.sparql.algebra.op.OpBGP;
import com.hp.hpl.jena.sparql.algebra.op.OpDistinct;
import com.hp.hpl.jena.sparql.algebra.op.OpExtend;
import com.hp.hpl.jena.sparql.algebra.op.OpFilter;
import com.hp.hpl.jena.sparql.algebra.op.OpGraph;
import com.hp.hpl.jena.sparql.algebra.op.OpGroup;
import com.hp.hpl.jena.sparql.algebra.op.OpJoin;
import com.hp.hpl.jena.sparql.algebra.op.OpLeftJoin;
import com.hp.hpl.jena.sparql.algebra.op.OpMinus;
import com.hp.hpl.jena.sparql.algebra.op.OpPath;
import com.hp.hpl.jena.sparql.algebra.op.OpProject;
import com.hp.hpl.jena.sparql.algebra.op.OpService;
import com.hp.hpl.jena.sparql.algebra.op.OpTable;
import com.hp.hpl.jena.sparql.algebra.op.OpUnion;
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.ExprAggregator;
import com.ibm.wala.util.collections.HashSetFactory;
import com.ibm.wala.util.collections.Pair;

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
		return NodeFactory.createAnon(new AnonId(s));
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

	static Node fromLiteral(Pair<String,Object> o, Map<Object, String> langs) {
		if (o.snd instanceof String) {
			return NodeFactory.createLiteral(o.fst, langs.containsKey(o)? langs.get(o): (String)o.snd, false);
		} else if (o.snd instanceof URI) {
			RDFDatatype dtype = XMLLiteralType.theXMLLiteralType;
			return NodeFactory.createLiteral(o.fst, dtype);
		} else {
			assert o.snd == null;
			return NodeFactory.createLiteral(o.fst);
		}
	}

	@SuppressWarnings("unchecked")
	public static Node fromAtom(Object o, Map<Object, String> langs) {
		if (o instanceof Pair<?,?>) {
			return fromLiteral((Pair<String,Object>) o, langs);
		} else {
			return fromURI(o.toString());
		}
	}
	public static Triple fromTuple(Tuple t, Map<Object, String> langs) {
		Node s = fromAtom(t.atom(1), langs);
		Node p = fromAtom(t.atom(2), langs);
		Node o = fromAtom(t.atom(3), langs);
		return Triple.create(s, p, o);
	}
	
	public static void addTupleSet(Graph G, TupleSet tt, Map<Object, String> langs) {
		for(Tuple t : tt) {
			G.add(fromTuple(t, langs));
		}
	}
	
	public static Pair<String, Object> toAtom(LiteralLabel l) {
		Object snd = null;
		if (l.getDatatypeURI() != null) {
			try {
				snd = new URI(l.getDatatypeURI());
			} catch (URISyntaxException e) {
				assert false : l.getDatatypeURI() + " is not a datatype";
			}
		}
		if (l.language() != null && !"".equals(l.language())) {
			snd = l.language().toLowerCase();
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
		});
		return result;
	}
}
