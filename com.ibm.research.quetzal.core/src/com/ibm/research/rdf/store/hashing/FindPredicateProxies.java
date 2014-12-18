/******************************************************************************
 * Copyright (c) 2015 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *****************************************************************************/
 package com.ibm.research.rdf.store.hashing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import com.ibm.wala.dataflow.graph.AbstractMeetOperator;
import com.ibm.wala.dataflow.graph.DataflowSolver;
import com.ibm.wala.dataflow.graph.IKilldallFramework;
import com.ibm.wala.dataflow.graph.ITransferFunctionProvider;
import com.ibm.wala.fixpoint.IVariable;
import com.ibm.wala.fixpoint.UnaryOperator;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.graph.Graph;
import com.ibm.wala.util.graph.labeled.LabeledGraph;
import com.ibm.wala.util.graph.labeled.SlowSparseNumberedLabeledGraph;
import com.ibm.wala.util.graph.traverse.SCCIterator;

public class FindPredicateProxies {

	private final LabeledGraph<String, Double> predicates;

	private DataflowSolver<String, ProxyImpl> solver;

	public interface Proxy {
		String getProxyNode();

		double getProxyWeight();
	}

	private static class ProxyImpl implements Proxy, IVariable<ProxyImpl> {
		private int graphNodeId = -1;

		private int orderNumber = -1;

		private String proxyNode;

		private double proxyWeight;

		private ProxyImpl(String proxyNode, double proxyWeight) {
			this.proxyNode = proxyNode;
			this.proxyWeight = proxyWeight;
		}

		public int getGraphNodeId() {
			return graphNodeId;
		}

		public void setGraphNodeId(int number) {
			graphNodeId = number;
		}

		public int getOrderNumber() {
			return orderNumber;
		}

		public void setOrderNumber(int i) {
			orderNumber = i;
		}

		public void copyState(ProxyImpl v) {
			proxyNode = v.proxyNode;
			proxyWeight = v.proxyWeight;
		}

		public String toString() {
			return proxyNode + "(" + proxyWeight + ")";
		}

		public String getProxyNode() {
			return proxyNode;
		}

		public double getProxyWeight() {
			return proxyWeight;
		}
	}

	private IKilldallFramework<String, ProxyImpl> getProblem() {
		return new IKilldallFramework<String, ProxyImpl>() {

			public Graph<String> getFlowGraph() {
				return predicates;
			}

			public ITransferFunctionProvider<String, ProxyImpl> getTransferFunctionProvider() {
				return new ITransferFunctionProvider<String, ProxyImpl>() {

					public UnaryOperator<ProxyImpl> getNodeTransferFunction(
							String node) {
						throw new UnsupportedOperationException();
					}

					public boolean hasNodeTransferFunctions() {
						return false;
					}

					public UnaryOperator<ProxyImpl> getEdgeTransferFunction(
							final String src, final String dst) {
						class TransferProxyOperator extends
								UnaryOperator<ProxyImpl> {
							final double weight = predicates.getEdgeLabels(src,
									dst).iterator().next().doubleValue();

							public byte evaluate(ProxyImpl lhs, ProxyImpl rhs) {
								if (lhs.proxyWeight < weight * rhs.proxyWeight) {
									assert rhs.proxyNode != null;
									lhs.proxyNode = rhs.proxyNode;
									lhs.proxyWeight = weight * rhs.proxyWeight;
									assert lhs.proxyWeight > 0.0;
									return CHANGED;
								} else {
									return NOT_CHANGED;
								}
							}

							public int hashCode() {
								return (int) (21771 * weight);
							}

							public boolean equals(Object o) {
								return o instanceof TransferProxyOperator
										&& weight == ((TransferProxyOperator) o).weight;
							}

							public String toString() {
								return "transferProxyOperator(" + weight + ")";
							}
						}
						;
						return new TransferProxyOperator();
					}

					public boolean hasEdgeTransferFunctions() {
						return true;
					}

					private final AbstractMeetOperator<ProxyImpl> chooseBestProxy = new AbstractMeetOperator<ProxyImpl>() {

						public boolean isUnaryNoOp() {
							return false;
						}

						public byte evaluate(ProxyImpl lhs,
								ProxyImpl[] rhsProxies) {
							byte changed = NOT_CHANGED;
							for (ProxyImpl rhs : rhsProxies) {
								if (lhs.proxyWeight < rhs.proxyWeight) {
									assert rhs.proxyNode != null;
									lhs.proxyNode = rhs.proxyNode;
									lhs.proxyWeight = rhs.proxyWeight;
									assert lhs.proxyWeight > 0.0;
									changed = CHANGED;
								}
							}
							return changed;
						}

						public int hashCode() {
							return System.identityHashCode(this);
						}

						public boolean equals(Object o) {
							return o == this;
						}

						public String toString() {
							return "chooseBestProxy";
						}

					};

					public AbstractMeetOperator<ProxyImpl> getMeetOperator() {
						return chooseBestProxy;
					}

				};
			}
		};
	}

	private DataflowSolver<String, ProxyImpl> getSolver() {
		return new DataflowSolver<String, ProxyImpl>(getProblem()) {

			private final Set<String> roots;

			{
				roots = new HashSet<String>();
				SCCIterator<String> sccs = new SCCIterator<String>(predicates);
				each_scc: while (sccs.hasNext()) {
					Set<String> sccNodes = sccs.next();
					for (String sccNode : sccNodes) {
						Iterator<String> preds = predicates
								.getPredNodes(sccNode);
						while (preds.hasNext()) {
							if (!sccNodes.contains(preds.next())) {
								continue each_scc;
							}
						}
					}
					roots.add(sccNodes.iterator().next());
				}
			}

			protected ProxyImpl makeNodeVariable(String n, boolean IN) {
				assert IN;
				return roots.contains(n) ? new ProxyImpl(n, 1.0)
						: new ProxyImpl(null, 0.0);
			}

			protected ProxyImpl makeEdgeVariable(String src, String dst) {
				return new ProxyImpl(null, 0.0);
			}

			protected ProxyImpl[] makeStmtRHS(int size) {
				return new ProxyImpl[size];
			}

		};
	}

	public void solve() throws CancelException, IOException {
		solver = getSolver();
		solver.solve(null);
	}

	public Proxy getProxy(String predicate) {
		return solver.getIn(predicate);
	}

	public FindPredicateProxies(LabeledGraph<String, Double> predicates) {
		this.predicates = predicates;
	}

	public static LabeledGraph<String, Double> readGraph(File fileName)
			throws IOException {
		LabeledGraph<String, Double> predicates = new SlowSparseNumberedLabeledGraph<String, Double>(
				-1.0);
		BufferedReader in = new BufferedReader(new FileReader(fileName));
		String line;
		while ((line = in.readLine()) != null) {
			if (line.startsWith("#")) {
				continue;
			} else {
				StringTokenizer words = new StringTokenizer(line);
				String from = words.nextToken();
				if (!predicates.containsNode(from)) {
					predicates.addNode(from);
				}
				if (words.hasMoreTokens()) {
					Double scale = Double.parseDouble(words.nextToken());
					String to = words.nextToken();
					if (!predicates.containsNode(to)) {
						predicates.addNode(to);
					}
					predicates.addEdge(from, to, scale);
				}
			}
		}
		return predicates;
	}

	public FindPredicateProxies(String graphFileName) throws IOException {
		this(readGraph(new File(graphFileName)));
	}

	public static void main(String[] args) throws CancelException, IOException {
		(new FindPredicateProxies(args[0])).solve();
	}
}
