package com.ibm.research.rdf.store.sparql11;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import kodkod.ast.Node;
import kodkod.ast.Relation;
import kodkod.ast.Variable;
import kodkod.ast.visitor.AbstractCollector;

import com.ibm.wala.util.collections.HashMapFactory;
import com.ibm.wala.util.collections.HashSetFactory;

public class ASTUtils {

	private static class SomethingCollector<T> extends AbstractCollector<T> {
		private final Map<Node, Set<T>> cache = HashMapFactory.make();
		
		protected SomethingCollector(Set<Node> arg0) {
			super(arg0);
		}

		@Override
		protected Set<T> cache(Node arg0, Set<T> arg1) {
			cache.put(arg0, arg1);
			return arg1;
		}

		@Override
		protected Set<T> lookup(Node arg0) {
			if (cache.containsKey(arg0)) {
				return cache.get(arg0);
			} else {
				return super.lookup(arg0);
			}
		}

		@Override
		protected Set<T> newSet() {
			return HashSetFactory.make(1);
		}

	};

	@SuppressWarnings("unchecked")
	public static Set<Variable> gatherVariables(Node astRoot) {
		class VariableCollector extends SomethingCollector<Variable> {
			private VariableCollector(Set<Node> arg0) {
				super(arg0);
			}

			@Override
			public Set<Variable> visit(Variable arg0) {
				return Collections.singleton(arg0);
			}
		};
		
		return (Set<Variable>) astRoot.accept(new VariableCollector(HashSetFactory.<Node>make()));
	}

	@SuppressWarnings("unchecked")
	public static Set<Relation> gatherRelations(Node astRoot) {
		class RelationCollector extends SomethingCollector<Relation> {
			private RelationCollector(Set<Node> arg0) {
				super(arg0);
			}

			@Override
			public Set<Relation> visit(Relation arg0) {
				return Collections.singleton(arg0);
			}
		};
		
		return (Set<Relation>) astRoot.accept(new RelationCollector(HashSetFactory.<Node>make()));
	}
}