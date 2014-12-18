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
package com.ibm.research.owlql.ruleref;



import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.ExprVars;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementAssign;
import com.hp.hpl.jena.sparql.syntax.ElementBind;
import com.hp.hpl.jena.sparql.syntax.ElementData;
import com.hp.hpl.jena.sparql.syntax.ElementDataset;
import com.hp.hpl.jena.sparql.syntax.ElementExists;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementMinus;
import com.hp.hpl.jena.sparql.syntax.ElementNamedGraph;
import com.hp.hpl.jena.sparql.syntax.ElementNotExists;
import com.hp.hpl.jena.sparql.syntax.ElementOptional;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;
import com.hp.hpl.jena.sparql.syntax.ElementService;
import com.hp.hpl.jena.sparql.syntax.ElementSubQuery;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;
import com.hp.hpl.jena.sparql.syntax.ElementUnion;
import com.hp.hpl.jena.sparql.syntax.ElementVisitor;
import com.hp.hpl.jena.sparql.syntax.ElementVisitorBase;
import com.hp.hpl.jena.sparql.syntax.ElementWalker;
import com.hp.hpl.jena.sparql.syntax.PatternVars;
import com.ibm.research.owlql.rule.RuleSystem;
import com.ibm.research.utils.FindAllVariables;
import com.ibm.research.utils.OCUtils;
/**
 * An OWL QL SPARQL Query compiler responsible for expanding a SPARQL query given an 
 * OWL QL ontology. The query expansion is performed by invoking the method {@link #compile(Query)}
 * 
 * <p>
 * Important note: According to OWL 2 Direct Semantics entailment regime, all variables in a query are distinguished variables.
 * </p>
 * @author fokoue
 *
 */
public class OWLQLSPARQLCompiler extends OWLQLToNonRecursiveDatalogCompiler {

	private static final Logger logger = LoggerFactory.getLogger(OWLQLSPARQLCompiler.class);
	/*protected class DNFTransformation implements ElementVisitor {

		protected List<? extends Element> latestConjuncts;
		
		public Element getDNF() {
			return createUnion(latestConjuncts);
		}
		protected Element createUnion(List<? extends Element> conjuncts) {
			if (conjuncts.size() == 1) {
				return conjuncts.get(0);
			} else {
				ElementUnion ret= new ElementUnion();
				for (Element e: conjuncts) {
					ret.addElement(e);
				}
				return ret;
			}
		}
		@Override
		public void visit(ElementAssign e) {
			latestConjuncts = Collections.singletonList(new ElementAssign(e.getVar(), e.getExpr()));		
		}

		
		@Override
		public void visit(ElementBind eb) {
			latestConjuncts = Collections.singletonList(new ElementBind(eb.getVar(), eb.getExpr()));	
			
		}
		@Override
		public void visit(ElementDataset e) {
		   e.getPatternElement().visit(this);
		   latestConjuncts = Collections.singletonList(
				   new ElementDataset(e.getDataset(), createUnion(latestConjuncts)));
		}

		@Override
		public void visit(ElementExists e) {
			e.getElement().visit(this);
			latestConjuncts = Collections.singletonList(
					   new ElementExists(createUnion(latestConjuncts)));
		}
		@Override
		public void visit(ElementMinus e) {
			e.getMinusElement().visit(this);
			latestConjuncts = Collections.singletonList(
					   new ElementMinus(createUnion(latestConjuncts)));
		}
		
		@Override
		public void visit(ElementFetch e) {
			latestConjuncts = Collections.singletonList(
				new ElementFetch(e.getFetchNode()));		
		}

		@Override
		public void visit(ElementFilter e) {
			latestConjuncts = Collections.singletonList(new ElementFilter(e.getExpr()));		
			
		}

		@Override
		public void visit(ElementGroup group) {
			List<Element> newConjuncts = new  LinkedList<Element>();
			List<List<? extends Element>> inputList = new LinkedList<List<? extends Element>>();
			for (Element e: group.getElements()) {
				e.visit(this);
				inputList.add(latestConjuncts);
			}
			for (List<Element> cartProdElt : computeCartesianProduct(inputList)) {
				Element newgroup = createGroup(removeDuplicates(cartProdElt));
				newConjuncts.add(newgroup);
			}
			latestConjuncts = removeDuplicates(newConjuncts);
		}
		
		
		private Element createGroup(List<Element> elts) {
			ElementGroup newgroup = new ElementGroup();
			for (Element e: elts) {
				newgroup.addElement(e);
			}
			return newgroup.getElements().size()==1?
				newgroup.getElements().get(0) :newgroup;
			
		}*/
		/**
		 * create group without nested group and with at most one triple block. 
		 * <p>NOTE: this performs only one level flattening of group, but it is called each time that a group is processed.
		 * @param elts
		 * @return
		 */
		/*private Element createGroup(List<Element> elts) {
			ElementGroup newgroup = new ElementGroup();
			ElementTriplesBlock triples = null;
			for (Element e: elts) {
				//flatten group
				if (e instanceof ElementGroup) {
					ElementGroup g = (ElementGroup) e;
					for (Element ge: g.getElements()) {
						if (ge instanceof ElementTriplesBlock) {
							ElementTriplesBlock ts = (ElementTriplesBlock) ge;
							if (triples == null) {
								triples = new ElementTriplesBlock();
								for (Triple t: ts.getPattern().getList()) {
									triples.addTriple(t);
								}
								newgroup.addElement(ts);
							} else {
								for (Triple t: ts.getPattern().getList()) {
									triples.addTriple(t);
								}
							}
						} else {
							newgroup.addElement(ge);
						}
					}
				} else  if (e instanceof ElementTriplesBlock) {
					ElementTriplesBlock ts = (ElementTriplesBlock) e;
					if (triples == null) {
						triples = new ElementTriplesBlock();
						for (Triple t: ts.getPattern().getList()) {
							triples.addTriple(t);
						}
						newgroup.addElement(ts);
					} else {
						for (Triple t: ts.getPattern().getList()) {
							triples.addTriple(t);
						}
					}
				} 
				//
				else {
					newgroup.addElement(e);
				}
			}
			return newgroup.getElements().size()==1?
				newgroup.getElements().get(0) :newgroup;
			
		}*/
		/*protected <T> List<T> removeDuplicates(List<T> l) {
			List<T> ret = new LinkedList<T>();
			Set<T> alreadySeen = new HashSet<T>();
			for (T t:l) {
				if (alreadySeen.add(t)) {
					ret.add(t);
				}
			}
			return ret;
		}*/
		/**
		 * returns a list of all the elements of the cartesian product of a given list of collections.
		 * @param <T>
		 * @param inputCollections
		 * @return
		 */
		/*public <T>  List<List<T>> computeCartesianProduct(List<List<? extends T>> inputCollections) {
			if (inputCollections.isEmpty()) {
				return new LinkedList<List<T>>();
			}
			return primComputeCartesianProduct(new LinkedList<List<? extends T>>(inputCollections));
		}
			
		private <T>  List<List<T>> primComputeCartesianProduct(List<List<? extends T>> inputCollections) {
			if (inputCollections.isEmpty()) {
				List<List<T>> ret = new LinkedList<List<T>>();
				ret.add(new LinkedList<T>());
				return ret;
			} else {
				List<List<T>> ret = new LinkedList<List<T>>();
				List<? extends T> firstCol = inputCollections.remove(0);
				List<List<T>> partialRes = primComputeCartesianProduct(inputCollections);
				for (T t: firstCol) {
					for (List<T> e: partialRes) {
						List<T> ee = new LinkedList<T>(e);
						ee.add(0,t);
						ret.add(ee);
					}
					
				}
				return ret;
			}
			
		}

		@Override
		public void visit(ElementNamedGraph ng) {
			Node n = ng.getGraphNameNode();
			Element e = ng.getElement();
			e.visit(this);
			if (n==null) {
				latestConjuncts = Collections.singletonList(
						new ElementNamedGraph(createUnion(latestConjuncts)));
			} else {
				latestConjuncts = Collections.singletonList(
						new ElementNamedGraph(n, createUnion(latestConjuncts)));
			}
		}

		@Override
		public void visit(ElementNotExists e) {
			e.getElement().visit(this);
			latestConjuncts = Collections.singletonList(
				new ElementNotExists(createUnion(latestConjuncts)));
		}

		@Override
		public void visit(ElementOptional e) {
			e.getOptionalElement().visit(this);
			latestConjuncts = Collections.singletonList(
					new ElementOptional(createUnion(latestConjuncts)));
			
		}

		@Override
		public void visit(ElementPathBlock arg0) {
			visit(OCUtils.toTriples(arg0));
		}

		@Override
		public void visit(ElementService es) {
			Node n = es.getServiceNode();
			String uri = es.getServiceURI();
			es.getElement().visit(this);
			if (n!=null) {
				latestConjuncts = Collections.singletonList(
					new ElementService(n, createUnion(latestConjuncts)));
			} else if (uri!=null) {
				latestConjuncts = Collections.singletonList(
						new ElementService(uri, createUnion(latestConjuncts)));
			} else {
				latestConjuncts = Collections.singletonList(
						new ElementService((Node) null, createUnion(latestConjuncts)));
			}
			
		}

		@Override
		public void visit(ElementSubQuery sq) {
			Element pattern = sq.getQuery().getQueryPattern();
			pattern.visit(this);
			Query newQuery = sq.getQuery().cloneQuery();
			newQuery.setQueryPattern(createUnion(latestConjuncts));
			latestConjuncts = Collections.singletonList(new ElementSubQuery(newQuery));
		}


		@Override
		public void visit(ElementTriplesBlock triples) {
			ElementTriplesBlock newTriples = new ElementTriplesBlock();
			for (Triple t: triples.getPattern().getList()) {
				newTriples.addTriple(t);
			}
			latestConjuncts = Collections.singletonList(newTriples);			
			
		}

		@Override
		public void visit(ElementUnion union) {
			List<Element> newConjuncts= new LinkedList<Element>();
			for (Element e: union.getElements()) {
				e.visit(this);
				newConjuncts.addAll(latestConjuncts);
			}
			latestConjuncts = removeDuplicates(newConjuncts);
			
			
		}
		
	}*/
	/**
	 * Perform OWL QL expansion of all basic graph patterns.
	 * Assumption: All variables are distinguished variables
	 * 
	 */
	protected class ExpandBasicGraphPatterns implements ElementVisitor {

		private Set<String> allVars;
		//private List<String> distinguishedVars;
		private Element result;
		
		
		public Element expand(Element root, /*List<String> distinguishedVars,*/  Set<String> allVars) {
			
			this.allVars = allVars;
			//this.distinguishedVars = distinguishedVars;
			root.visit(this);
			return result;
		}
		@Override
		public void visit(ElementAssign e) {
			result = e;		
		}
		@Override
		public void visit(ElementBind e) {
			result = e;		
		}
		
		@Override
		public void visit(ElementDataset e) {
		   e.getPatternElement().visit(this);
		   e.setPatternElement(result);
		   result = e;
		}

		@Override
		public void visit(ElementExists e) {
			e.getElement().visit(this);
			result = new ElementExists(result);
			
		}
		
		

		@Override
		public void visit(ElementMinus e) {
			e.getMinusElement().visit(this);
			result = new ElementMinus(result);
		}
		
		@Override
		public void visit(ElementData ed) {
			result = ed;
			
		}
		/*@Override
		public void visit(ElementFetch e) {
			result = e;
			
		}*/

		@Override
		public void visit(ElementFilter e) {
			result = e;
			
		}

		@Override
		public void visit(ElementGroup group) {
			ElementGroup newgroup = new ElementGroup();
			for (Element e: group.getElements()) {
				e.visit(this);
				newgroup.addElement(result);
			}
			result = newgroup;
			
		}

		@Override
		public void visit(ElementNamedGraph ng) {
			Node n = ng.getGraphNameNode();
			Element e = ng.getElement();
			e.visit(this);
			if (n==null) {
				result = new ElementNamedGraph(result);
			} else {
				result = new ElementNamedGraph(n, result);
			}
		}

		@Override
		public void visit(ElementNotExists e) {
			e.getElement().visit(this);
			result = new ElementNotExists(result);
			
		}

		@Override
		public void visit(ElementOptional e) {
			e.getOptionalElement().visit(this);
			result = new ElementOptional(result);
			
		}

		@Override
		public void visit(ElementPathBlock arg0) {
			visit(OCUtils.toTriples(arg0));
		}

		@Override
		public void visit(ElementService es) {
			Node n = es.getServiceNode();
			String uri = n!=null && n.isURI()? n.getURI(): null;
			es.getElement().visit(this);
			if (uri!=null) {
				result = new ElementService(uri, result, es.getSilent());
			}  else if (n!=null) {
				result = new ElementService(n, result,es.getSilent());
			} else {
				result = new ElementService((Node) null, result, es.getSilent() );
			}
		}

		@Override
		public void visit(ElementSubQuery sq) {
			Query newsq = primCompile(sq.getQuery(), allVars);
			result = new ElementSubQuery(newsq);
		}

		@Override
		public void visit(ElementTriplesBlock tb) {
			List<Triple> triples = tb.getPattern().getList();
			// convert set of triples to a rule system
			// here is where all variables in triples are assumed to be distinguished variables
			logger.debug("Triples:\n{}", triples);
			RuleSystem rs = new TriplesToRuleSystem(tbox).toRules(triples, allVars, true);
			logger.debug("Triples as rule system:\n{}", rs);
			//
			RuleSystem outrs = compileToNonRecursiveDatalog(rs);
			logger.debug("RuleSystem:\n{}", outrs );
			if (outrs!=null) {
				Query q = RuleSystemToUnionQuery.toUnionQuery(outrs);
				result = q.getQueryPattern();		
			} else {
				/*ElementTriplesBlock ts = new ElementTriplesBlock();
				Node owlNothing =  Node.createURI(OWL.Nothing.getURI());
				Node rdfType  = Node.createURI(RDF.type.getURI());
				ts.addTriple(new Triple(owlNothing, rdfType, owlNothing));
				result = ts;*/
				result = tb;
			}
		}

		@Override
		public void visit(ElementUnion union) {
			ElementUnion newunion = new ElementUnion();
			for (Element e: union.getElements()) {
				e.visit(this);
				newunion.addElement(result);
			}
			result = newunion;
			
			
		}
		
	}
	/**
	 * Map-based implementation of a multiset.
	 * @author fokoue
	 *
	 * @param <T>
	 */
	public static class MultiSet<T> implements Set<T> {
		protected Map<T, Integer>  elt2Count = new HashMap<T, Integer>();

		public int getCount(T e) {
			Integer ret = elt2Count.get(e);
			return ret == null? 0: ret;
		}
		@Override
		public boolean add(T e) {
			Integer count = elt2Count.get(e);
			boolean alreadyPresent = count!=null;
			if (count ==null) {
				count =0;
			}
			count++;
			elt2Count.put(e, count);
			return !alreadyPresent;
		}

		@Override
		public boolean addAll(Collection<? extends T> col) {
			boolean change = false;
			for (T t: col) {
				change |= add(t);
			}
			return change;
		}

		@Override
		public void clear() {
			elt2Count.clear();
			
		}

		@Override
		public boolean contains(Object t) {
			return elt2Count.containsKey(t);
		}

		@Override
		public boolean containsAll(Collection<?> col) {
			return elt2Count.keySet().containsAll(col);
		}

		@Override
		public boolean isEmpty() {
			return elt2Count.isEmpty();
		}

		@Override
		public Iterator<T> iterator() {
			return Collections.unmodifiableSet(elt2Count.keySet()).iterator();
		}

		@Override
		public boolean remove(Object t) {
			Integer count = elt2Count.get(t);
			if (count!=null) {
				assert count>0 : count;
				count--;
				if (count == 0) {
					elt2Count.remove(t);
				} else {
					elt2Count.put((T) t, count);
				}
				return true;
			} else {
				return false;
			}
 		}

		@Override
		public boolean removeAll(Collection<?> col) {
			boolean changed = false;
			for (Object t: col) {
				changed |= remove(t);
			}
			return changed;
		}

		@Override
		public boolean retainAll(Collection<?> col) {
			boolean changed = false;
			for (Object o: elt2Count.keySet()) {
				if (!col.contains(o)) {
					elt2Count.remove(o);
					changed = true;
				}
			}
			return changed;
		}

		@Override
		public int size() {
			return elt2Count.size();
		}

		@Override
		public Object[] toArray() {
			return elt2Count.keySet().toArray();
		}

		@Override
		public <T> T[] toArray(T[] arg0) {
			return elt2Count.keySet().toArray(arg0);
		}

	
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((elt2Count == null) ? 0 : elt2Count.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MultiSet other = (MultiSet) obj;
			if (elt2Count == null) {
				if (other.elt2Count != null)
					return false;
			} else if (!elt2Count.equals(other.elt2Count))
				return false;
			return true;
		}
		
	}
	
	protected static class SkipMinusWalker extends ElementWalker.Walker {

		public SkipMinusWalker(ElementVisitor arg0) {
			super(arg0, null, null);
		}

		@Override
		public void visit(ElementExists el) {
			 proc.visit(el) ;
		}

		@Override
		public void visit(ElementMinus el) {
			 proc.visit(el) ;
		}

		@Override
		public void visit(ElementNotExists el) {
			 proc.visit(el) ;
		}

		@Override
		public void visit(ElementSubQuery el) {
			 proc.visit(el) ;
		}
		
		
	}
	
	protected static MultiSet<Var> getVisibleVarsToOccurrences(Element e) {
		
		final MultiSet<Var> visibleVars = new MultiSet<Var>();
		PatternVars.vars(visibleVars, e);
		
		ElementVisitorBase visitor = new ElementVisitorBase() {
			
			// add variables in (vars intersection  visibleVars) to visibleVars
			private void add(MultiSet<Var> vars) {
				for (Var v: vars) {
					int count = visibleVars.getCount(v);
					if (count > 0) {
						count = count+ vars.getCount(v);
						visibleVars.elt2Count.put(v, count);
					} 
				}
			}
			// add variables in (vars intersection  visibleVars) to visibleVars
			private void addString(MultiSet<String> vars) {
				for (String s: vars) {
					Var v = Var.alloc(s);
					int count = visibleVars.getCount(v);
					if (count > 0) {
						count = count+ vars.getCount(s);
						visibleVars.elt2Count.put(v, count);
					} 
				}
			}
			
			@Override
			public void visit(ElementExists el) {
				MultiSet<Var> vars = new MultiSet<Var>();
				PatternVars.vars(vars, el.getElement());
				add(vars);
			}

			@Override
			public void visit(ElementMinus el) {
				MultiSet<Var> vars = new MultiSet<Var>();
				PatternVars.vars(vars, el.getMinusElement());
				add(vars);
			}

			@Override
			public void visit(ElementNotExists el) {
				MultiSet<Var> vars = new MultiSet<Var>();
				PatternVars.vars(vars, el.getElement());
				add(vars);
			}

			@Override
			public void visit(ElementSubQuery el) {
				//Do nothing
			}
			@Override 
			public void  visit(ElementFilter el) {
				MultiSet<String> vars = new MultiSet<String>();
				ExprVars.varNamesMentioned(vars, el.getExpr());
				addString(vars);
			}
		};
	
		SkipMinusWalker walker = new SkipMinusWalker(visitor);
		e.visit(walker);
		//ElementWalker.walk(e, visitor);
		return visibleVars;
		
	}
	
	protected Set<String> getMultipleOccurrenceVars(MultiSet<Var> vars) {
		Set<String> ret = new HashSet<String>();
		for (Var v: vars) {
			if (vars.getCount(v)>1) {
				ret.add(v.getName());
			}
		}
		return ret;
	}
	protected Set<String> getVars(Set<Var> vars) {
		return OCUtils.getVars(vars);
	}

	/**
	 * <p>
	 * Usage: queryFile ontologyFile (ontologyFile)* 
	 * </p>
	 * <p>
	 * where
	 *   <ul>
	 *   	<li>queryFile: indicates the location of a semicolon separated file containing the input SPARQL queries. </li>
	 *      <li>ontologyFile: indicates the location of the TBox files</li>
	 *   </ul> 
	 * </p>
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		if (args.length<2) {
			logger.error("At least two arguments expected: queryFile ontologyFile (ontologyFile)* ");
			return;
		}
		logger.debug("START");
		File queryFile = new File(args[0]);
		File[] ontFiles = new File[args.length-1];
		for (int i=1; i<args.length;i++) {
			ontFiles[i-1] = new File(args[i]);
		}
		List<Query> queries = OCUtils.loadQueries(queryFile);
		Model tboxmodel = ModelFactory.createDefaultModel();
		for (File f: ontFiles) {
			tboxmodel.read(f.toURI().toString());
		}
		
		int count = 1;
		OWLQLSPARQLCompiler compiler = new OWLQLSPARQLCompiler(tboxmodel, null, null);
		for (Query q: queries) {
			Query eq = compiler.compile(q);
			StringBuffer buf =new StringBuffer();
			buf.append(eq+"\n");
			System.out.println("# Query #"+count);
			System.out.println(buf+";");
			count++;
		}
	}
	/**
	 *  builds a compiler for a given Tbox ontology 
	 * @param originalOntTbox the input Tbox ontology
	 * @param conceptURIsInAbox the uris of all concepts appearing in the instance data if known; otherwise, null.
	 * @param propertyURIsInAbox the uris of all propertoes appearing in the instance data if known; otherwise, null.
	 */
	public OWLQLSPARQLCompiler(OWLOntology originalOntTbox, Set<String> conceptURIsInAbox, Set<String> propertyURIsInAbox) {
		super(originalOntTbox,  conceptURIsInAbox, propertyURIsInAbox);
	}
	/**
	 * builds a compiler for a given Tbox ontology
	 * @param conceptURIsInAbox the uris of all concepts appearing in the instance data if known; otherwise, null.
	 * @param propertyURIsInAbox the uris of all propertoes appearing in the instance data if known; otherwise, null.
	 * @param tboxFiles the locations of TBox files
	 * 
	 * @throws OWLOntologyCreationException
	 */
	public OWLQLSPARQLCompiler(Set<String> conceptURIsInAbox, Set<String> propertyURIsInAbox, String... tboxFiles) throws OWLOntologyCreationException {
		super(OCUtils.load(tboxFiles),  conceptURIsInAbox, propertyURIsInAbox);
		if (tboxFiles.length==0) {
			throw new RuntimeException("At least one tbox file must be specified");
		}
	}
	/**
	 * builds a compiler for a given Tbox ontology specified by its {@link Model}
	 * @param tbox
	 *  @param conceptURIsInAbox the uris of all concepts appearing in the instance data if known; otherwise, null.
	 * @param propertyURIsInAbox the uris of all propertoes appearing in the instance data if known; otherwise, null.
	 * @throws OWLOntologyCreationException
	 * @throws IOException
	 */
	public OWLQLSPARQLCompiler(Model tbox, Set<String> conceptURIsInAbox, Set<String> propertyURIsInAbox) throws OWLOntologyCreationException, IOException {
		super(OCUtils.load(tbox), conceptURIsInAbox, propertyURIsInAbox);
	}

	/*protected Query toDNF(Query q) {
		DNFTransformation visitor = new DNFTransformation();
		q.getQueryPattern().visit(visitor);
		Query ret = q.cloneQuery();
		ret.setQueryPattern(visitor.getDNF());
		return ret;
	}*/
	protected static Set<Var> getAllVariablesMentioned(Element queryPattern) {
		return FindAllVariables.getAllVariables(queryPattern);
		
	}
	protected static Set<Var> getAllVariablesMentioned(Query query) {
		return FindAllVariables.getAllVariables(query);
	}
	/**
	 * compiles a given query and returns the resulting expanded query.
	 * @param query
	 * @return
	 */
	public Query compile(Query query) {
		//query = new QueryPatternSimplification().simplify(query);
		//query =toDNF(query);
		Set<Var> vars = getAllVariablesMentioned(query);
		return primCompile(query, getVars(vars));
		
	}
	private Query primCompile(Query query, Set<String> allVars) {
		// query must already be in dnf 
		Element e = query.getQueryPattern();
		Element newelt;	
		/*if (e instanceof ElementUnion) {
			ElementUnion union= new ElementUnion();
			for (Element ge : ((ElementUnion) e).getElements()) {
				Set<String> distinguishedVars = getMultipleOccurrenceVars(getVisibleVarsToOccurrences(ge));
				distinguishedVars.addAll(query.getResultVars());
				ExpandBasicGraphPatterns ebgp = new ExpandBasicGraphPatterns();
				Element newge = ebgp.expand(query.getQueryPattern(),  new LinkedList<String>(distinguishedVars), allVars);
				union.addElement(newge);
			}
			newelt = union;
		} else {
			Set<String> distinguishedVars = getMultipleOccurrenceVars(getVisibleVarsToOccurrences(e));*/
			ExpandBasicGraphPatterns ebgp = new ExpandBasicGraphPatterns();
			//distinguishedVars.addAll(query.getResultVars());
			newelt = ebgp.expand(query.getQueryPattern(), /* new LinkedList<String>(distinguishedVars),*/ allVars);
			
		//}
		Query ret = query.cloneQuery();
		ret.setQueryPattern(newelt);
		return ret;
		
	}
	
}
