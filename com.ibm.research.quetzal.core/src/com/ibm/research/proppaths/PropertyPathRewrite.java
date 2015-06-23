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
 package com.ibm.research.proppaths;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.E_Equals;
import com.hp.hpl.jena.sparql.expr.E_NotEquals;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.ExprVar;
import com.hp.hpl.jena.sparql.expr.nodevalue.NodeValueNode;
import com.hp.hpl.jena.sparql.path.P_Alt;
import com.hp.hpl.jena.sparql.path.P_Distinct;
import com.hp.hpl.jena.sparql.path.P_FixedLength;
import com.hp.hpl.jena.sparql.path.P_Inverse;
import com.hp.hpl.jena.sparql.path.P_Link;
import com.hp.hpl.jena.sparql.path.P_Mod;
import com.hp.hpl.jena.sparql.path.P_Multi;
import com.hp.hpl.jena.sparql.path.P_NegPropSet;
import com.hp.hpl.jena.sparql.path.P_OneOrMore1;
import com.hp.hpl.jena.sparql.path.P_OneOrMoreN;
import com.hp.hpl.jena.sparql.path.P_ReverseLink;
import com.hp.hpl.jena.sparql.path.P_Seq;
import com.hp.hpl.jena.sparql.path.P_Shortest;
import com.hp.hpl.jena.sparql.path.P_ZeroOrMore1;
import com.hp.hpl.jena.sparql.path.P_ZeroOrMoreN;
import com.hp.hpl.jena.sparql.path.P_ZeroOrOne;
import com.hp.hpl.jena.sparql.path.PathVisitor;
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
import com.ibm.research.rdf.store.sparql11.SparqlParserUtilities;
import com.ibm.research.rdf.store.sparql11.model.AltPath;
import com.ibm.research.rdf.store.sparql11.model.BindPattern;
import com.ibm.research.rdf.store.sparql11.model.Constant;
import com.ibm.research.rdf.store.sparql11.model.ConstantExpression;
import com.ibm.research.rdf.store.sparql11.model.Expression;
import com.ibm.research.rdf.store.sparql11.model.Expression.ERelationalOp;
import com.ibm.research.rdf.store.sparql11.model.IRI;
import com.ibm.research.rdf.store.sparql11.model.InvPath;
import com.ibm.research.rdf.store.sparql11.model.NegatedProperySetPath;
import com.ibm.research.rdf.store.sparql11.model.OneOrMorePath;
import com.ibm.research.rdf.store.sparql11.model.Path;
import com.ibm.research.rdf.store.sparql11.model.Pattern;
import com.ibm.research.rdf.store.sparql11.model.Pattern.EPatternSetType;
import com.ibm.research.rdf.store.sparql11.model.PatternSet;
import com.ibm.research.rdf.store.sparql11.model.ProjectedVariable;
import com.ibm.research.rdf.store.sparql11.model.PropertyTerm;
import com.ibm.research.rdf.store.sparql11.model.QueryTriple;
import com.ibm.research.rdf.store.sparql11.model.QueryTripleTerm;
import com.ibm.research.rdf.store.sparql11.model.RelationalExpression;
import com.ibm.research.rdf.store.sparql11.model.SeqPath;
import com.ibm.research.rdf.store.sparql11.model.SimplePath;
import com.ibm.research.rdf.store.sparql11.model.SimplePattern;
import com.ibm.research.rdf.store.sparql11.model.SubSelectPattern;
import com.ibm.research.rdf.store.sparql11.model.Variable;
import com.ibm.research.rdf.store.sparql11.model.VariableExpression;
import com.ibm.research.rdf.store.sparql11.model.ZeroOrMorePath;
import com.ibm.research.rdf.store.sparql11.model.ZeroOrOnePath;
import com.ibm.research.rdf.store.sparql11.planner.Planner;
import com.ibm.research.utils.FindAllVariables;
import com.ibm.research.utils.OCUtils;
import com.ibm.wala.util.collections.HashSetFactory;
import com.ibm.wala.util.collections.Pair;

/**
 * Rewrite a property path expression into an equivalent expression without any property 
 * path constructs when possible
 * @author fokoue
 *
 */
public class PropertyPathRewrite {
	
	public static void main(String[] args) throws Exception{
		PropertyPathRewrite rewrite = new PropertyPathRewrite();
		String q = "prefix : <http://example/> \n"+ 
					"select * where { ?x (:p/:p)? ?t } ";
				//"select * where {?x <http://example.org/rr>+ ?y . ?x <http://example.org/r>+ ?x. ?x <http://example.org/s> <http://example.org/constIRI> . ?x <http://example.org/t>+ <http://example.org/constIRI> . } ";
				//"select * where { ?x (^<http://example.org/p>|<http://example.org/s>?)/(<http://example.org/q>| <http://example.org/r>)+/!(^<http://example.org/t>| ^<http://example.org/u>|<http://example.org/v> )?y}"; 
		//Query query = QueryFactory.create(q, Syntax.syntaxSPARQL_11);
		com.ibm.research.rdf.store.sparql11.model.Query query =  SparqlParserUtilities.parseSparqlString(q);
		System.out.println("Parsed Query:\n\t"+query);
		boolean resultQueryWithoutPropertyPath = rewrite.rewrite(query, true, null, null);
		System.out.println("Rewritten Query:\n\t"+query);
		System.out.println("Rewritten Query has no property paths: "+resultQueryWithoutPropertyPath);
 	}

	protected  class ElementRewrite implements ElementVisitor {
		private Element result;
		private boolean hasPropertyPaths;
		private boolean bestEffort;
		private NewVariableGenerator vargen;
		public ElementRewrite(boolean bestEffort, NewVariableGenerator vargen) {
			super();
			this.bestEffort = bestEffort;
			this.vargen = vargen;
		}
		
		public Element getResult() {
			return result;
		}
		public boolean isResultWithPropertyPaths() {
			return hasPropertyPaths;
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
		public void visit(ElementData e) {
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
		public void visit(ElementMinus e) {
			e.getMinusElement().visit(this);
			result = new ElementMinus(result);
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
		public void visit(ElementPathBlock e) {
			ElementPathBlock epb = null;
			ElementTriplesBlock etb = null;
			ElementGroup g = new ElementGroup();
			for (TriplePath tp : e.getPattern().getList()) {
				boolean[] hasPropPaths = new boolean[]{false};
				Element tpres =transform(tp, bestEffort, vargen, hasPropPaths);
				if (!hasPropertyPaths) {
					hasPropertyPaths = hasPropPaths[0];
				}
				if (tpres == null) {
					assert !bestEffort;
					tpres = new ElementPathBlock();
					((ElementPathBlock) tpres).addTriple(tp);
				}
				if (tpres instanceof ElementTriplesBlock) {
					for (Triple t: ((ElementTriplesBlock) tpres).getPattern().getList()) {
						if (etb == null) {
							etb =  new ElementTriplesBlock();
							g.addElement(etb);
						}
						etb.addTriple(t);
					}
				} else if  (tpres instanceof ElementPathBlock) {
					for (TriplePath t: ((ElementPathBlock) tpres).getPattern().getList()) {
						if (epb == null) {
							epb = new ElementPathBlock();
							g.addElement(epb);
						}
						epb.addTriple(t);
					}
				} else {
					g.addElement(tpres);
				}
			}
			if (g.getElements().size()>1) {
				result = g;
			} else if (g.getElements().size() ==1) {
				result = g.getElements().get(0);
			} else {
				result = g;
			}
			
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
			boolean hasNoPropPaths =  rewrite(sq.getQuery(), bestEffort);//primCompile(sq.getQuery(), allVars);
			if (!hasPropertyPaths) {
				hasPropertyPaths = !hasNoPropPaths;
			}
			result = new ElementSubQuery(sq.getQuery());
		}

		@Override
		public void visit(ElementTriplesBlock e) {
			result = e;
			
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
	
	protected class PathRewrite implements com.ibm.research.rdf.store.sparql11.model.PathVisitor {
		private Pattern result;
		private boolean resultHasPropertyPaths;
		private QueryTripleTerm subject;
		private QueryTripleTerm object;
		private Variable graphRestriction;
		private boolean bestEffort;
		private NewVariableGenerator vargen;
		private  Set<Variable> explicitIRIBoundVariables;
		private Set<Variable> explicitNotIRIBoundVariables;
		public PathRewrite(Variable graphRestriction, QueryTripleTerm  subject, QueryTripleTerm  object, boolean bestEffort,
				NewVariableGenerator vargen,  Set<Variable> explicitIRIBoundVariables, Set<Variable> explicitNotIRIBoundVariables) {
			super();
			this.bestEffort = bestEffort;
			this.vargen = vargen;
			this.subject = subject;
			this.object = object;
			this.graphRestriction = graphRestriction;
			this.explicitIRIBoundVariables = explicitIRIBoundVariables;
			this.explicitNotIRIBoundVariables = explicitNotIRIBoundVariables;
		}	
		
		
		
		
		public Pattern getResult() {
			return result;
		}
		public boolean resultWithPropertyPaths() {
			return resultHasPropertyPaths;
		}
		@Override
		public void visit(AltPath p) {
			p.getLeft().visit(this);
			Pattern left = result;
			if (left!=null) {
				p.getRight().visit(this);
				Pattern right = result;
				if (right!=null) {
					PatternSet ret = new PatternSetWithExplicitIRIBoundVariables(EPatternSetType.UNION, explicitIRIBoundVariables, explicitNotIRIBoundVariables);
					ret.addPattern(left);
					ret.addPattern(right);
					result = ret;
				} else {
					result = null;
				}
			} else {
				result = null;
			}
		}
		
		@Override
		public void visit(InvPath p) {
			QueryTriple newtp = new QueryTriple(object, new PropertyTerm(p.getSubPath()), subject);
			boolean[] resHasPropertyPaths = new boolean[]{false};
			result = transform(newtp, bestEffort, vargen, resHasPropertyPaths, explicitIRIBoundVariables, explicitNotIRIBoundVariables);
			if (!resultHasPropertyPaths) {
				resultHasPropertyPaths = resHasPropertyPaths[0];
			}
		}

		@Override
		public void visit(SimplePath p) {
			QueryTriple t = new QueryTriple(subject, new PropertyTerm(p), object);
			SimplePattern e = new SimplePatternWithExplicitIRIBoundVariables(explicitIRIBoundVariables, explicitNotIRIBoundVariables);
			e.addQueryTriple(t);
			//addVariableFilters(subject, e);
			//addVariableFilters(object, e);
			result = e;
			
		}

		
		protected Expression getExpr(QueryTripleTerm n) {
			return n.isVariable()? new VariableExpression(n.getVariable().getName()) : 
					n.isIRI()? new ConstantExpression(n.getIRI()): 
					new ConstantExpression(n.getConstant());
		}
		@Override
		public void visit(NegatedProperySetPath p) {
			List<IRI> fwd = p.getFowardProperties();
			List<IRI> bwd = p.getBackwardProperties();
			//PatternSet group = new PatternSet(EPatternSetType.AND);
			String fwdpred = null;
			QueryTriple fwdtriple= null;
			String bwdpred = null;
			QueryTriple bwdtriple = null;
			
			if (fwd!=null && !fwd.isEmpty()) {
				fwdpred = vargen.createNewVariable();
				fwdtriple = new QueryTriple(subject, new PropertyTerm(new Variable(fwdpred)), object);
			}
			
			if ( bwd!=null && !bwd.isEmpty()) {
				bwdpred = vargen.createNewVariable();
				bwdtriple = new QueryTriple(object, new PropertyTerm(new Variable(bwdpred)), subject);
			}
			if (fwdtriple!=null && bwdtriple!=null) {
				
				PatternSet union = new PatternSetWithExplicitIRIBoundVariables(EPatternSetType.UNION, explicitIRIBoundVariables, explicitNotIRIBoundVariables) ;
				
				// add forward branch: subject fwdPredVar object
				SimplePattern fwdElt = new SimplePatternWithExplicitIRIBoundVariables(explicitIRIBoundVariables, explicitNotIRIBoundVariables);
				fwdElt.addQueryTriple(fwdtriple);
				//addVariableFilters(subject, fwdElt);
				//addVariableFilters(object, fwdElt);
				Pattern fwdGroup = fwdElt;// new PatternSet(EPatternSetType.AND);
				//fwdGroup.addPattern(fwdElt);
				fwdGroup.addFilter(new RelationalExpression(getExpr(subject), getExpr(object),ERelationalOp.NOT_EQUAL));
				for (IRI n:  p.getFowardProperties()) {
					Expression exp = new ConstantExpression(n);
					Expression neq =  new RelationalExpression(new VariableExpression(fwdpred),exp, ERelationalOp.NOT_EQUAL);
					fwdGroup.addFilter(neq);
				}
				union.addPattern(fwdGroup);
				//
				
				// add backward branch: object bwdPredVar subject
				SimplePattern bwdElt = new  SimplePatternWithExplicitIRIBoundVariables(explicitIRIBoundVariables, explicitNotIRIBoundVariables);
				bwdElt.addQueryTriple(bwdtriple);
				//addVariableFilters(subject,bwdElt);
				//addVariableFilters(object, bwdElt);
				Pattern bwdGroup =  bwdElt;//new PatternSet(EPatternSetType.AND);
				//bwdGroup.addPattern(bwdElt);
				bwdGroup.addFilter(new RelationalExpression(getExpr(subject), getExpr(object), ERelationalOp.NOT_EQUAL));
				for (IRI n:  p.getBackwardProperties()) {
					Expression exp = new ConstantExpression(n);
					Expression neq = new RelationalExpression(new VariableExpression(bwdpred),exp, ERelationalOp.NOT_EQUAL);
					bwdGroup.addFilter(neq);
				}
				union.addPattern(bwdGroup);
				//
				
				// add subject = object branch
				SimplePattern eqElt = new SimplePatternWithExplicitIRIBoundVariables(explicitIRIBoundVariables, explicitNotIRIBoundVariables);
				String eqpred = vargen.createNewVariable();
				QueryTriple eqtriple = new QueryTriple(subject, new PropertyTerm(new Variable(eqpred)), object);
				eqElt.addQueryTriple(eqtriple);
				//addVariableFilters(subject,eqElt);
				//addVariableFilters(object, eqElt);
				Pattern eqGroup = eqElt;//new  PatternSet(EPatternSetType.AND);
				//eqGroup.addPattern(eqElt);
				eqGroup.addFilter(new RelationalExpression(getExpr(subject), getExpr(object),  ERelationalOp.EQUAL));
				Set<IRI> alreadySeen = HashSetFactory.make();
				for (IRI n:  p.getFowardProperties()) {
					if (alreadySeen.add(n)) {
						Expression exp = new ConstantExpression(n); 
						Expression neq = new RelationalExpression(new VariableExpression(eqpred),exp, ERelationalOp.NOT_EQUAL);
						eqGroup.addFilter(neq);
					}
				}
				for (IRI n:  p.getBackwardProperties()) {
					if (alreadySeen.add(n)) {
						Expression exp = new ConstantExpression(n); 
						Expression neq = new RelationalExpression(new VariableExpression(eqpred), exp, ERelationalOp.NOT_EQUAL);
						eqGroup.addFilter(neq);
					}
				}
				union.addPattern(eqGroup);
				//
				result = union;
				
			} else if (fwdtriple!=null) {
				SimplePattern e = new SimplePatternWithExplicitIRIBoundVariables(explicitIRIBoundVariables, explicitNotIRIBoundVariables);
				e.addQueryTriple(fwdtriple);
				//addVariableFilters(subject, e);
				//addVariableFilters(object, e);
				//group.addPattern(e);
				for (IRI n : p.getFowardProperties()) {
					Expression exp = new ConstantExpression(n);
					Expression neq = new RelationalExpression(new VariableExpression(fwdpred),exp, ERelationalOp.NOT_EQUAL);
					e.addFilter(neq);
					
				}
				result = e;
			} else if (bwdtriple!=null) {
				SimplePattern e = new SimplePatternWithExplicitIRIBoundVariables(explicitIRIBoundVariables, explicitNotIRIBoundVariables);
				e.addQueryTriple(bwdtriple);
				//addVariableFilters(subject, e);
				//addVariableFilters(object, e);
				//group.addPattern(e);
				for (IRI n:  p.getBackwardProperties()) {
					Expression exp = new ConstantExpression(n);
					Expression neq = new RelationalExpression(new VariableExpression(bwdpred),exp, ERelationalOp.NOT_EQUAL);
					e.addFilter(neq);
				}
				result = e;
			} else {
				assert false : p;
			}
			
			
		}

		@Override
		public void visit(OneOrMorePath p) {
			if (bestEffort) {
				SimplePattern e = processIrreduciblePath(p);
				result = e;
				resultHasPropertyPaths = true;
			} else {
				result = null;
			}
			
		}

		private SimplePattern processIrreduciblePath(Path p) {
			assert p.isDirectlyRecursive() || p.isDirectlyZeroOrOnePath() : p;
			SimplePattern e = new SimplePatternWithExplicitIRIBoundVariables(explicitIRIBoundVariables, explicitNotIRIBoundVariables);
			Variable subVar ; 
			Variable objVar;
			if (subject.isConstant()) {
				subVar = new Variable(vargen.createNewVariable());
				e.addFilter(new RelationalExpression(new VariableExpression(subVar.getName()), new ConstantExpression(subject.getConstant()), ERelationalOp.EQUAL));
			} else if (subject.isIRI()) {
				subVar = new Variable(vargen.createNewVariable());
				e.addFilter(new RelationalExpression(new VariableExpression(subVar.getName()), new ConstantExpression(subject.getIRI()), ERelationalOp.EQUAL));
			} else  {
				assert subject.isVariable() : subject;
				subVar = subject.getVariable();
			}
			if (object.isConstant()) {
				objVar = new Variable(vargen.createNewVariable());
				e.addFilter(new RelationalExpression(new VariableExpression(objVar.getName()), new ConstantExpression(object.getConstant()), ERelationalOp.EQUAL));
			} else if (object.isIRI()) {
				objVar = new Variable(vargen.createNewVariable());
				e.addFilter(new RelationalExpression(new VariableExpression(objVar.getName()), new ConstantExpression(object.getIRI()), ERelationalOp.EQUAL));
			
			} else {
				assert object.isVariable() : object.isVariable();
				objVar = object.getVariable();
				
			}
			if (subject.isVariable() && object.isVariable() && subject.equals(object)) {
				objVar = new Variable(vargen.createNewVariable());
				e.addFilter(new RelationalExpression(new VariableExpression(objVar.getName()), new VariableExpression(object.getVariable().getName()), ERelationalOp.EQUAL));
				if (graphRestriction!=null && graphRestriction.equals(subject.getVariable())) {
					subVar =  new Variable(vargen.createNewVariable());
					e.addFilter(new RelationalExpression(new VariableExpression(subVar.getName()), new VariableExpression(subject.getVariable().getName()), ERelationalOp.EQUAL));
				}
			} else {
				if (graphRestriction!=null && subject.isVariable() && graphRestriction.equals(subject.getVariable())) {
					subVar =  new Variable(vargen.createNewVariable());
					e.addFilter(new RelationalExpression(new VariableExpression(subVar.getName()), new VariableExpression(subject.getVariable().getName()), ERelationalOp.EQUAL));
				}
				if (graphRestriction!=null && object.isVariable() && graphRestriction.equals(object.getVariable())) {
					objVar =  new Variable(vargen.createNewVariable());
					e.addFilter(new RelationalExpression(new VariableExpression(objVar.getName()), new VariableExpression(object.getVariable().getName()), ERelationalOp.EQUAL));
				}
			}
			
			e.addQueryTriple(new QueryTriple( new QueryTripleTerm(subVar), new PropertyTerm(p), new QueryTripleTerm(objVar)));
			return e;
		}
		


		@Override
		public void visit(SeqPath p) {
			String newvar= vargen.createNewVariable();
			Variable tmpvar = new Variable(newvar);
			QueryTriple lefttp = new QueryTriple(subject, new PropertyTerm(p.getLeft()), new QueryTripleTerm(tmpvar) ); 
			boolean[] reshasPropertyPaths = new boolean[]{false};
			Pattern left = transform(lefttp, bestEffort, vargen,reshasPropertyPaths, explicitIRIBoundVariables, explicitNotIRIBoundVariables);
			if (left!=null) {
				if (!resultHasPropertyPaths) {
					resultHasPropertyPaths = reshasPropertyPaths[0];
				}
				reshasPropertyPaths = new boolean[]{false};
				QueryTriple righttp = new QueryTriple(new QueryTripleTerm(tmpvar), new PropertyTerm(p.getRight()), object);
				Pattern right = transform(righttp, bestEffort, vargen,reshasPropertyPaths, explicitIRIBoundVariables, explicitNotIRIBoundVariables);
				if (right!=null) {
					if (!resultHasPropertyPaths) {
						resultHasPropertyPaths = reshasPropertyPaths[0];
					}
					if ( (right instanceof SimplePattern)
						&& (left instanceof SimplePattern) ) {
						SimplePattern ltb = (SimplePattern) left;
						SimplePattern rtb = (SimplePattern) right;
						SimplePattern res = new SimplePatternWithExplicitIRIBoundVariables(explicitIRIBoundVariables, explicitNotIRIBoundVariables);
						for (QueryTriple t: ltb.getQueryTriples()) {
							res.addQueryTriple(t);
						}
						for (Expression exp: left.getFilters()) {
							res.addFilter(exp);
						}
						for (QueryTriple t: rtb.getQueryTriples()) {
							res.addQueryTriple(t);
						}
						for (Expression exp: right.getFilters()) {
							res.addFilter(exp);
						}
						result = res;
					} else {
						PatternSet group = new PatternSetWithExplicitIRIBoundVariables(EPatternSetType.AND, explicitIRIBoundVariables, explicitNotIRIBoundVariables);
						group.addPattern(left);
						group.addPattern(right);
						result = group;
					}
				} else {
					result =null;
				}
				
			} else {
				result = null;
			}
			
			
		}

		@Override
		public void visit(ZeroOrMorePath p) {
			if (bestEffort) {
				SimplePattern e = processIrreduciblePath(p);
				result = e;
				resultHasPropertyPaths = true;
			} else {
				result = null;
			}
			
		}

		
		private boolean sameValueAs(QueryTripleTerm nonVar1, QueryTripleTerm nonVar2) {
			assert !nonVar1.isVariable() : nonVar1;
			assert !nonVar2.isVariable() : nonVar2;
			if (nonVar1.isIRI() && nonVar2.isIRI()) {
				return nonVar1.getIRI().equals(nonVar2.getIRI());
			} else if (nonVar1.isConstant() && nonVar2.isConstant()) {
				Constant c1 = nonVar1.getConstant();
				Constant c2 = nonVar2.getConstant();
				if (c1.getConstType().equals(c2.getConstType())) {
					if (c1.getIRI()!=null && c2.getIRI()!=null) {
						return c1.getIRI().equals(c2.getIRI());
					} else if (c1.getNumber()!=null && c2.getNumber()!=null) {
						return c1.getNumber().equals(c2.getNumber());
					} else if (c1.getBoolean()!=null && c2.getBoolean()!=null) {
						return c1.getBoolean().equals(c2.getBoolean());
					} else if (c1.getLiteral().equals(c2.getLiteral())) {
						return c1.getLiteral().equals(c2.getLiteral());
					}
				}
				
			}
			return false;
		}
		
	

		@Override
		public void visit(ZeroOrOnePath p) {
			if (bestEffort) {
				SimplePattern e = processIrreduciblePath(p);
				result = e;
				resultHasPropertyPaths = true;
			} else {
				result = null;
			}
			
			
			// (subj p? obj) is semantically equivalent to a union of
			// 1) subj == obj
			// 2) subj p obj
			/*QueryTriple newtp = new QueryTriple(subject, new PropertyTerm(p.getSubPath()), object);
			boolean[] resHasPropertyPaths = new boolean[]{false};
			Pattern newElt = transform(newtp, bestEffort, vargen, resHasPropertyPaths, explicitIRIBoundVariables, explicitNotIRIBoundVariables);
			
			if (!subject.isVariable() && !object.isVariable()) {
				if (sameValueAs(subject, object)) {
					// trivially satisfied
					result = new PatternSetWithExplicitIRIBoundVariables(EPatternSetType.AND, explicitIRIBoundVariables, explicitNotIRIBoundVariables);
				} else {
					// the zero branch cannot be satified
					// so we test only the one branch
					result = newElt;
					if (!resultHasPropertyPaths) {
						resultHasPropertyPaths = resHasPropertyPaths[0];
					}
				}
			} else {
				if (!resultHasPropertyPaths) {
					resultHasPropertyPaths = resHasPropertyPaths[0];
				}
				PatternSet union = new PatternSetWithExplicitIRIBoundVariables(EPatternSetType.UNION, explicitIRIBoundVariables, explicitNotIRIBoundVariables);
				union.addPattern(newElt);
				PatternSet eqGroup = new  PatternSetWithExplicitIRIBoundVariables(EPatternSetType.AND,  explicitIRIBoundVariables, explicitNotIRIBoundVariables);
				BindPattern bind; 
				if (subject.isVariable()) {
					bind = new BindPattern(subject.getVariable().getName(), getExpr(object)); 
				} else {
					assert object.isVariable() : object;
					bind = new BindPattern(object.getVariable().getName(), getExpr(subject));
				}
				eqGroup.addPattern(bind);
				union.addPattern(eqGroup);
				result = union;
				
			}
			*/
			
		}
		
	}
	
	
	protected class JenaPathRewrite implements PathVisitor {
		
		private Element result;
		private boolean resultHasPropertyPaths;
		private Node subject;
		private Node object;
		private boolean bestEffort;
		private NewVariableGenerator vargen;
		
		
	
		public JenaPathRewrite(Node subject, Node object, boolean bestEffort,
				NewVariableGenerator vargen) {
			super();
			this.subject = subject;
			this.object = object;
			this.bestEffort = bestEffort;
			this.vargen = vargen;
		}
		public Element getResult() {
			return result;
		}
		public boolean resultWithPropertyPaths() {
			return resultHasPropertyPaths;
		}
		@Override
		public void visit(P_Alt p) {
			p.getLeft().visit(this);
			Element left = result;
			if (left!=null) {
				p.getRight().visit(this);
				Element right = result;
				if (right!=null) {
					ElementUnion ret = new ElementUnion(left);
					ret.addElement(right);
					result = ret;
				} else {
					result = null;
				}
			} else {
				result = null;
			}
		}

		@Override
		public void visit(P_Distinct p) {
			throw new UnsupportedOperationException("Unsupported property path expression: "+p);
			
		}

		@Override
		public void visit(P_FixedLength p) {
			throw new UnsupportedOperationException("Unsupported property path expression: "+p);
			
		}

		@Override
		public void visit(P_Inverse p) {
			TriplePath newtp = new TriplePath(object, p.getSubPath(), subject);
			boolean[] resHasPropertyPaths = new boolean[]{false};
			result = transform(newtp, bestEffort, vargen, resHasPropertyPaths);
			if (!resultHasPropertyPaths) {
				resultHasPropertyPaths = resHasPropertyPaths[0];
			}
		}

		@Override
		public void visit(P_Link p) {
			Triple t = new Triple(subject, p.getNode(), object);
			ElementTriplesBlock e = new ElementTriplesBlock();
			e.addTriple(t);
			result = e;
			
		}

		@Override
		public void visit(P_Mod p) {
			throw new UnsupportedOperationException("Unsupported property path expression: "+p);
			
		}

		@Override
		public void visit(P_Multi p) {
			throw new UnsupportedOperationException("Unsupported property path expression: "+p);
			
		}
		protected Expr getExpr(Node n) {
			return n.isVariable()? new ExprVar(n.getName()) : new NodeValueNode(n);
		}
		@Override
		public void visit(P_NegPropSet p) {
			List<Node> fwd = p.getFwdNodes();
			List<Node> bwd = p.getBwdNodes();
			ElementGroup group = new ElementGroup();
			String fwdpred = null;
			Triple fwdtriple= null;
			String bwdpred = null;
			Triple bwdtriple = null;
			
			if (fwd!=null && !fwd.isEmpty()) {
				fwdpred = vargen.createNewVariable();
				fwdtriple = new Triple(subject, NodeFactory.createVariable(fwdpred), object);
			}
			
			if ( bwd!=null && !bwd.isEmpty()) {
				bwdpred = vargen.createNewVariable();
				bwdtriple = new Triple(object, NodeFactory.createVariable(bwdpred), subject);
			}
			if (fwdtriple!=null && bwdtriple!=null) {
				
				ElementUnion union = new ElementUnion();
				
				// add foward branch: subject fwdPredVar object
				ElementTriplesBlock fwdElt = new ElementTriplesBlock();
				fwdElt.addTriple(fwdtriple);
				ElementGroup fwdGroup = new ElementGroup();
				fwdGroup.addElement(fwdElt);
				fwdGroup.addElementFilter(new ElementFilter(new E_NotEquals(getExpr(subject), getExpr(object))));
				for (Node n:  p.getFwdNodes()) {
					Expr exp = getExpr(n);
					E_NotEquals neq = new E_NotEquals(new ExprVar(fwdpred),exp);
					fwdGroup.addElementFilter(new ElementFilter(neq));
				}
				union.addElement(fwdGroup);
				//
				
				// add backward branch: object bwdPredVar subject
				ElementTriplesBlock bwdElt = new ElementTriplesBlock();
				bwdElt.addTriple(bwdtriple);
				ElementGroup bwdGroup = new ElementGroup();
				bwdGroup.addElement(bwdElt);
				bwdGroup.addElementFilter(new ElementFilter(new E_NotEquals(getExpr(subject), getExpr(object))));
				for (Node n:  p.getBwdNodes()) {
					Expr exp = getExpr(n);
					E_NotEquals neq = new E_NotEquals(new ExprVar(bwdpred),exp);
					bwdGroup.addElementFilter(new ElementFilter(neq));
				}
				union.addElement(bwdGroup);
				//
				
				// add subject = object branch
				ElementTriplesBlock eqElt = new ElementTriplesBlock();
				String eqpred = vargen.createNewVariable();
				Triple eqtriple = new Triple(subject, NodeFactory.createVariable(eqpred), object);
				eqElt.addTriple(eqtriple);
				ElementGroup eqGroup = new ElementGroup();
				eqGroup.addElement(eqElt);
				eqGroup.addElementFilter(new ElementFilter(new E_Equals(getExpr(subject), getExpr(object))));
				Set<Node> alreadySeen = HashSetFactory.make();
				for (Node n:  p.getFwdNodes()) {
					if (alreadySeen.add(n)) {
						Expr exp = getExpr(n); 
						E_NotEquals neq = new E_NotEquals(new ExprVar(eqpred),exp);
						eqGroup.addElementFilter(new ElementFilter(neq));
					}
				}
				for (Node n:  p.getBwdNodes()) {
					if (alreadySeen.add(n)) {
						Expr exp = getExpr(n); 
						E_NotEquals neq = new E_NotEquals(new ExprVar(eqpred),exp);
						eqGroup.addElementFilter(new ElementFilter(neq));
					}
				}
				union.addElement(eqGroup);
				//
				
				group.addElement(union);
			} else if (fwdtriple!=null) {
				ElementTriplesBlock e = new ElementTriplesBlock();
				e.addTriple(fwdtriple);
				group.addElement(e);
				for (Node n : p.getFwdNodes()) {
					Expr exp = getExpr(n);
					E_NotEquals neq = new E_NotEquals(new ExprVar(fwdpred),exp);
					group.addElementFilter(new ElementFilter(neq));
					
				}
			} else if (bwdtriple!=null) {
				ElementTriplesBlock e = new ElementTriplesBlock();
				e.addTriple(bwdtriple);
				group.addElement(e);
				for (Node n:  p.getBwdNodes()) {
					Expr exp = getExpr(n);
					E_NotEquals neq = new E_NotEquals(new ExprVar(bwdpred),exp);
					group.addElementFilter(new ElementFilter(neq));
				}
			} else {
				assert false : p;
			}
			result = group;
			
		}

		@Override
		public void visit(P_OneOrMore1 p) {
			if (bestEffort) {
				ElementPathBlock e = new ElementPathBlock();
				e.addTriple(new TriplePath(subject, p, object));
				result = e;
				resultHasPropertyPaths = true;
			} else {
				result = null;
			}
			
		}

		@Override
		public void visit(P_OneOrMoreN p) {
			if (bestEffort) {
				ElementPathBlock e = new ElementPathBlock();
				e.addTriple(new TriplePath(subject, p, object));
				result = e;
				resultHasPropertyPaths = true;
			} else {
				result = null;
			}
			
		}

		@Override
		public void visit(P_ReverseLink p) {
			Triple t = new Triple(object, p.getNode(), subject);
			ElementTriplesBlock e = new ElementTriplesBlock();
			e.addTriple(t);
			result = e;
		}

		@Override
		public void visit(P_Seq p) {
			String newvar= vargen.createNewVariable();
			Node tmpvar = NodeFactory.createVariable(newvar);
			TriplePath lefttp = new TriplePath(subject, p.getLeft(), tmpvar ); 
			boolean[] reshasPropertyPaths = new boolean[]{false};
			Element left = transform(lefttp, bestEffort, vargen,reshasPropertyPaths);
			if (left!=null) {
				if (!resultHasPropertyPaths) {
					resultHasPropertyPaths = reshasPropertyPaths[0];
				}
				reshasPropertyPaths = new boolean[]{false};
				TriplePath righttp = new TriplePath(tmpvar, p.getRight(), object);
				Element right = transform(righttp, bestEffort, vargen,reshasPropertyPaths);
				if (right!=null) {
					if (!resultHasPropertyPaths) {
						resultHasPropertyPaths = reshasPropertyPaths[0];
					}
					if ( (right instanceof ElementTriplesBlock)
						&& (left instanceof ElementTriplesBlock) ) {
						ElementTriplesBlock ltb = (ElementTriplesBlock) left;
						ElementTriplesBlock rtb = (ElementTriplesBlock) right;
						ElementTriplesBlock res = new ElementTriplesBlock();
						for (Triple t: ltb.getPattern().getList()) {
							res.addTriple(t);
						}
						for (Triple t: rtb.getPattern().getList()) {
							res.addTriple(t);
						}
						result = res;
					} else {
						ElementGroup group = new ElementGroup();
						group.addElement(left);
						group.addElement(right);
						result = group;
					}
				} else {
					result =null;
				}
				
			} else {
				result = null;
			}
			
			
		}

		@Override
		public void visit(P_Shortest p) {
			throw new UnsupportedOperationException("Unsupported property path expression: "+p);
			
		}

		@Override
		public void visit(P_ZeroOrMore1 p) {
			if (bestEffort) {
				ElementPathBlock e = new ElementPathBlock();
				e.addTriple(new TriplePath(subject, p, object));
				result = e;
				resultHasPropertyPaths = true;
			} else {
				result = null;
			}
			
		}

		@Override
		public void visit(P_ZeroOrMoreN p) {
			if (bestEffort) {
				ElementPathBlock e = new ElementPathBlock();
				e.addTriple(new TriplePath(subject, p, object));
				result = e;
				resultHasPropertyPaths = true;
			} else {
				result = null;
			}
			
		}

		@Override
		public void visit(P_ZeroOrOne p) {
			if (bestEffort) {
				ElementPathBlock e = new ElementPathBlock();
				e.addTriple(new TriplePath(subject, p, object));
				result = e;
				resultHasPropertyPaths = true;
			} else {
				result = null;
			}
			
			// (subj p? obj) is semantically equivalent to a union of
			// 1) subj == obj
			// 2) subj p obj
			/*TriplePath newtp = new TriplePath(subject, p.getSubPath(), object);
			boolean[] resHasPropertyPaths = new boolean[]{false};
			Element newElt = transform(newtp, bestEffort, vargen, resHasPropertyPaths);
			if (!subject.isVariable() && !object.isVariable()) {
				if (subject.sameValueAs(object)) {
					// trivially satisfied
					result = new ElementGroup();
				} else {
					// the zero branch cannot be satified
					// so we test only the one branch
					result = newElt;
					if (!resultHasPropertyPaths) {
						resultHasPropertyPaths = resHasPropertyPaths[0];
					}
				}
			} else {
				if (!resultHasPropertyPaths) {
					resultHasPropertyPaths = resHasPropertyPaths[0];
				}
				ElementUnion union = new ElementUnion();
				union.addElement(newElt);
				ElementGroup eqGroup = new ElementGroup();
				ElementBind bind; 
				if (subject.isVariable()) {
					bind = new ElementBind( Var.alloc(subject.getName()), getExpr(object)); 
				} else {
					assert object.isVariable() : object;
					bind = new ElementBind(Var.alloc(object.getName()), getExpr(subject));
				}
				eqGroup.addElement(bind);
				union.addElement(eqGroup);
				result = union;
				
			}
			*/
			
		}
		
	}
	/**
	 * <p>Replaces every non-recursive property path expression by its equivalent expression containing
	 * no property path constructs.  For recursive property path expressions are left unchanged if 
	 * bestEffort is set to <code>false</code>; otherwise,  only their non-recursive part is rewritten. 
	 *  </p>
	 *  <p> It returns <code>true</code> iff. the resulting rewritten query has no property path expressions.
	 * @param query
	 * @param bestEffort
	 * @return
	 */
	public boolean rewrite(Query query, boolean bestEffort) {
		Set<Var> vars = FindAllVariables.getAllVariables(query);
		Set<String> varNames = OCUtils.getVars(vars);
		String varPrefix = "intermV";
		int startSuffix = OCUtils.nextAvailableSuffixVariable(varNames, varPrefix)+1;
		NewVariableGenerator vargen = new NewVariableGenerator(varPrefix, startSuffix);
		ElementRewrite er = new ElementRewrite(bestEffort, vargen);
		query.getQueryPattern().visit(er);
		Element newElt = er.getResult();
		query.setQueryPattern(newElt);
		return !er.isResultWithPropertyPaths();
	}
	
	public Element transform(TriplePath tp,boolean bestEffort, NewVariableGenerator vargen, boolean[] resultHasPropertyPaths) {
		Triple t = tp.asTriple();
		if (t!=null) {
			// this is a simple triple
			ElementTriplesBlock ret =new ElementTriplesBlock();
			ret.addTriple(t);
			return ret;
		}
		assert !tp.isTriple();
		assert tp.getPredicate()==null: tp;
		JenaPathRewrite pr = new JenaPathRewrite(tp.getSubject(), tp.getObject(), bestEffort, vargen);
		tp.getPath().visit(pr);
		if (resultHasPropertyPaths!=null) {
			resultHasPropertyPaths[0] = pr.resultWithPropertyPaths();
		}
		return pr.getResult();
	}
	
	/**
	 * <p>Replaces every non-recursive property path expression by its equivalent expression containing
	 * no property path constructs.  For recursive property path expressions are left unchanged if 
	 * bestEffort is set to <code>false</code>; otherwise,  only their non-recursive part is rewritten. 
	 *  </p>
	 *  <p> It returns <code>true</code> iff. the resulting rewritten query has no property path expressions.
	 * @param query
	 * @param bestEffort
	 * @return
	 */
	public boolean rewrite(com.ibm.research.rdf.store.sparql11.model.Query query, boolean bestEffort, Set<Variable> explicitIRIBoundVariables, Set<Variable> explicitNotIRIBoundVariables) {
		/*if (explicitIRIBoundVariables == null) {
			explicitIRIBoundVariables = Collections.emptySet();
		}*/
		Set<String> varNames = getAllVariables(query);
		if (query.isSelectQuery() && 
			(query.getSelectQuery().getSelectClause().getProjectedVariables()==null || query.getSelectQuery().getSelectClause().getProjectedVariables().isEmpty()) ) {
			for (Variable v: Planner.gatherInScopeVariables(query.getMainPattern())) {
				query.getSelectQuery().getSelectClause().addProjectedVariable(new ProjectedVariable(v));
			}
			if (query.getMainPattern().getOptionalPatterns()!=null) {
				for (Pattern op : query.getMainPattern().getOptionalPatterns()) {
					for (Variable v: Planner.gatherInScopeVariables(op)) {
						query.getSelectQuery().getSelectClause().addProjectedVariable(new ProjectedVariable(v));
					}
				}
			}
		}
		String varPrefix = "intermV";
		int startSuffix = OCUtils.nextAvailableSuffixVariable(varNames, varPrefix)+1;
		NewVariableGenerator vargen = new NewVariableGenerator(varPrefix, startSuffix);
		boolean result; 
		if (query.getMainPattern().getType()!=EPatternSetType.SIMPLE) {
			result = rewrite(query.getMainPattern(), bestEffort, vargen, explicitIRIBoundVariables, explicitNotIRIBoundVariables);
		} else {
			SimplePattern sp = (SimplePattern) query.getMainPattern();
			boolean[] hasPropPaths = new boolean[]{false};
			Pattern newp = rewrite(sp, bestEffort, vargen, hasPropPaths, explicitIRIBoundVariables, explicitNotIRIBoundVariables);
			newp.pushFilters();
			newp.pushGraphRestrictions();
			boolean ret = !hasPropPaths[0];
			query.setMainPattern(newp);
			result = ret;
			
		}
		Pattern newp = query.getMainPattern();
		newp.pushFilters();
		newp.pushGraphRestrictions();
		return result;
		
	}
	protected boolean rewrite(Pattern p, boolean bestEffort, NewVariableGenerator vargen, Set<Variable> explicitIRIBoundVariables, Set<Variable> explicitNotIRIBoundVariables) {
		boolean ret = true;
		switch (p.getType()) {
			case AND:
			case UNION:
			case MINUS: 
			case EXISTS:
			case NOT_EXISTS:
			case OPTIONAL:{
				ret &= rewrite((PatternSet) p, bestEffort, vargen,explicitIRIBoundVariables, explicitNotIRIBoundVariables);
				break;
			}
			case BIND: {
				ret &= true; //= rewrite((BindPattern) p, bestEffort, vargen, explicitIRIBoundVariables);
				break;
			}
			case VALUES: {
				ret &= true; //= rewrite((BindPattern) p, bestEffort, vargen, explicitIRIBoundVariables);
				break;
			}
			case SERVICE: {
				ret &= true; //= rewrite((BindPattern) p, bestEffort, vargen, explicitIRIBoundVariables);
				break;
			}
			case  SIMPLE: {
				throw new RuntimeException("Unknown pattern type: "+ p); // use the method for SimplePattern
			}
			case SUBSELECT: {
				ret &= rewrite((SubSelectPattern) p, bestEffort, vargen, explicitIRIBoundVariables, explicitNotIRIBoundVariables);
				break;
			}
			case BINDFUNC: {
				ret &= true; //= rewrite((BindPattern) p, bestEffort, vargen, explicitIRIBoundVariables);
				break;
			}
		default:
			throw new RuntimeException("Unknown pattern type: "+ p.getType()+"\n"+p);
		}
		// process optional pattern
		ret &= rewriteOptionalPattern(p, bestEffort, vargen, explicitIRIBoundVariables, explicitNotIRIBoundVariables);
	    //
	    return ret;
	}
	protected boolean rewriteOptionalPattern(Pattern p ,boolean bestEffort, NewVariableGenerator vargen, Set<Variable> explicitIRIBoundVariables, Set<Variable> explicitNotIRIBoundVariables ) {
		boolean ret = true;
		// process optional pattern
		if (p.getOptionalPatterns()!=null) {
			List<Pair<Integer, Pattern>> positionNewPatternPairs = new LinkedList<Pair<Integer,Pattern>>();
		    int pos = 0;
		    for (Pattern op: p.getOptionalPatterns()) {
				if (op.getType()!=EPatternSetType.SIMPLE) {
					boolean r = rewrite(op, bestEffort, vargen, explicitIRIBoundVariables, explicitNotIRIBoundVariables);
					ret &= r;
				} else {
					SimplePattern sp = (SimplePattern) op;
					boolean[] hasPropPaths = new boolean[]{false};
					Pattern newsp = rewrite(sp, bestEffort, vargen, hasPropPaths,explicitIRIBoundVariables, explicitNotIRIBoundVariables);
					ret &= !hasPropPaths[0];
					positionNewPatternPairs.add(Pair.make(pos, newsp));
				}
				pos++;
			}
		    
			for (Pair<Integer, Pattern> pair : positionNewPatternPairs) {
				 Pattern old = p.getOptionalPatterns().remove(pair.fst.intValue());
				 assert old instanceof SimplePattern : old;
				 p.getOptionalPatterns().add(pair.fst, pair.snd);
			}
		}
	    //
		return ret;
	}
	protected Pattern rewrite(SimplePattern e, boolean bestEffort, NewVariableGenerator vargen, boolean[] hasPropertyPaths, final Set<Variable> explicitIRIBoundVariables, Set<Variable> explicitNotIRIBoundVariables) {
		SimplePattern epb = null;
		PatternSet g = new PatternSetWithExplicitIRIBoundVariables(EPatternSetType.AND, explicitIRIBoundVariables, explicitNotIRIBoundVariables) ;
		for (QueryTriple tp : e.getQueryTriples()) {
			boolean[] hasPropPaths = new boolean[]{false};
			Pattern tpres =transform(tp, bestEffort, vargen, hasPropPaths, explicitIRIBoundVariables, explicitNotIRIBoundVariables);
			if (!hasPropertyPaths[0]) {
				hasPropertyPaths[0] = hasPropPaths[0];
			}
			if (tpres == null) {
				assert !bestEffort;
				tpres = new SimplePatternWithExplicitIRIBoundVariables(explicitIRIBoundVariables, explicitNotIRIBoundVariables);
				((SimplePattern) tpres).addQueryTriple(tp);
			}
			if (tpres instanceof SimplePattern) {
				for (QueryTriple t: ((SimplePattern) tpres).getQueryTriples()) {
					if (epb == null) {
						epb =  new SimplePatternWithExplicitIRIBoundVariables(explicitIRIBoundVariables, explicitNotIRIBoundVariables);
						g.addPattern(epb);
					}
					epb.addQueryTriple(t);
				}
				if (tpres.getFilters()!=null) {
					if (epb == null) {
						epb =  new SimplePatternWithExplicitIRIBoundVariables(explicitIRIBoundVariables, explicitNotIRIBoundVariables);
						g.addPattern(epb);
					}
					for (Expression exp : tpres.getFilters()) {
						epb.addFilter(exp);
					}
				}
			} else {
				g.addPattern(tpres);
			}
		}
		Pattern result;
		if (g.getPatterns().size()>1) {
			result = g;
		} else if (g.getPatterns().size() ==1) {
			result = g.getPatterns().get(0);
		} else {
			result = g;
		}
		if (e.getGraphRestriction()!=null) {
			result.setGraphRestriction(e.getGraphRestriction());
		}
		if (e.getFilters()!=null) {
			for (Expression exp : e.getFilters()) {
				result.addFilter(exp);
			}
		}
		if (e.getOptionalPatterns()!=null) {
			for (Pattern op: e.getOptionalPatterns()) {
				result.addOptional(op);
			}
		}
		hasPropertyPaths[0] |= !rewriteOptionalPattern(result, bestEffort, vargen, explicitIRIBoundVariables, explicitNotIRIBoundVariables);
		return result;
	}
	
	protected boolean rewrite(PatternSet p, boolean bestEffort, NewVariableGenerator vargen, Set<Variable> explicitIRIBoundVariables, Set<Variable> explicitNotIRIBoundVariables) {
		boolean ret = true;
		List<Pair<Integer, Pattern>> positionNewPatternPairs = new LinkedList<Pair<Integer,Pattern>>();
	    int pos = 0;
		for (Pattern subp: p.getPatterns()) {
			if (subp.getType()!=EPatternSetType.SIMPLE) {
				boolean r = rewrite(subp, bestEffort, vargen, explicitIRIBoundVariables, explicitNotIRIBoundVariables);
				ret &= r;
			} else {
				SimplePattern e = (SimplePattern) subp;
				boolean[] hasPropPaths = new boolean[]{false};
				Pattern result = rewrite(e, bestEffort, vargen, hasPropPaths, explicitIRIBoundVariables, explicitNotIRIBoundVariables);
				ret &= !hasPropPaths[0];
				positionNewPatternPairs.add(Pair.make(pos, result));
			}
			pos++;
		}
		
		for (Pair<Integer, Pattern> pair : positionNewPatternPairs) {
			 Pattern old = p.getPatterns().remove(pair.fst.intValue());
			 assert old instanceof SimplePattern : old;
			 p.getPatterns().add(pair.fst, pair.snd);
		}
		return ret;
	}
	protected boolean rewrite(BindPattern p, boolean bestEffort, NewVariableGenerator vargen) {
		return true;
	}
	
	protected boolean rewrite(SubSelectPattern p, boolean bestEffort, NewVariableGenerator vargen, Set<Variable> explicitIRIBoundVariables, Set<Variable> explicitNotIRIBoundVariables) {
		if (p.getSelectClause().getProjectedVariables()==null || p.getSelectClause().getProjectedVariables().isEmpty()) {
			for (Variable pv: p.gatherProjectedVariables()) {
				p.getSelectClause().addProjectedVariable(new ProjectedVariable(pv));
			}
		}
		return rewrite(p.getGraphPattern(), bestEffort, vargen, explicitIRIBoundVariables, explicitNotIRIBoundVariables);
		
	}
	
	
	
	private Set<String> getAllVariables(com.ibm.research.rdf.store.sparql11.model.Query q) {
		Set<String> ret = HashSetFactory.make();
		if ( q.isSelectQuery()) {
			for (ProjectedVariable v: q.getSelectQuery().getSelectClause().getProjectedVariables()) {
				if (v.getVariable()!=null) {
					ret.add(v.getVariable().getName());
				}
			} 
		}
		for (Variable v: q.getMainPattern().gatherVariablesWithOptional()) {
			ret.add(v.getName());
		}
		if (q.getMainPattern().getGraphRestriction()!=null
			&& q.getMainPattern().getGraphRestriction().isFirstType()) {
			ret.add(q.getMainPattern().getGraphRestriction().getFirst().getName());
		}
		return ret;
	}

	public Pattern transform(QueryTriple tp,boolean bestEffort, NewVariableGenerator vargen, boolean[] resultHasPropertyPaths,  Set<Variable> explicitIRIBoundVariables, Set<Variable> explicitNotIRIBoundVariables) {
		if (explicitIRIBoundVariables == null) {
			explicitIRIBoundVariables = Collections.<Variable>emptySet();
		}
		final Set<Variable> finalExplicitIRIBoundVariables = explicitIRIBoundVariables;
		if (!tp.getPredicate().isComplexPath()) {
			SimplePattern sp = new SimplePatternWithExplicitIRIBoundVariables(finalExplicitIRIBoundVariables, explicitNotIRIBoundVariables);
			sp.addQueryTriple(tp);
			return sp;
		}
		PathRewrite pr = new PathRewrite(tp.getGraphRestriction()!=null && tp.getGraphRestriction().isFirstType()? tp.getGraphRestriction().getFirst() : null,
				tp.getSubject(), tp.getObject(), bestEffort, vargen, finalExplicitIRIBoundVariables, explicitNotIRIBoundVariables);
		tp.getPredicate().getPath().visit(pr);
		if (resultHasPropertyPaths!=null) {
			resultHasPropertyPaths[0] = pr.resultWithPropertyPaths();
		}
		return pr.getResult();
	}
	
	
}
