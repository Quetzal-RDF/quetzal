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

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementAssign;
import org.apache.jena.sparql.syntax.ElementBind;
import org.apache.jena.sparql.syntax.ElementData;
import org.apache.jena.sparql.syntax.ElementDataset;
import org.apache.jena.sparql.syntax.ElementExists;
import org.apache.jena.sparql.syntax.ElementFilter;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementMinus;
import org.apache.jena.sparql.syntax.ElementNamedGraph;
import org.apache.jena.sparql.syntax.ElementNotExists;
import org.apache.jena.sparql.syntax.ElementOptional;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementService;
import org.apache.jena.sparql.syntax.ElementSubQuery;
import org.apache.jena.sparql.syntax.ElementTriplesBlock;
import org.apache.jena.sparql.syntax.ElementUnion;
import org.apache.jena.sparql.syntax.ElementVisitor;

import com.ibm.research.utils.OCUtils;
/**
 * simplifies {@link ElementGroup} and {@link ElementUnion} by removing useless nesting 
 * and goup or union of with a single element.
 * @author fokoue
 *
 */
public class QueryPatternSimplification  implements ElementVisitor {
	private Element result;
	
	
	public QueryPatternSimplification() {
		super();
	}

	public Element getResult() {
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
		e.getElement().visit(this);
		result = new ElementDataset(e.getDataset(), result);
	}
	@Override
	public void visit(ElementExists exists) {
		exists.getElement().visit(this);
		result = new ElementExists(result);			
	}
	

	@Override
	public void visit(ElementMinus e) {
		e.getMinusElement().visit(this);
		result = new ElementMinus(result);		
	}

	
	/*@Override
	public void visit(ElementFetch e) {
		result = e;
	}*/

	@Override
	public void visit(ElementData e) {
		result = e;
		
	}

	@Override
	public void visit(ElementFilter e) {
		result = e;
		
	}


	@Override
	public void visit(ElementGroup group) {
		//remove nested groups
		ElementGroup ret = new ElementGroup();
		ElementPathBlock pathBlock = new ElementPathBlock();
		for (Element elt: group.getElements()) {
			elt.visit(this);
			if (result instanceof ElementGroup) {
				ElementGroup subgroup = (ElementGroup) result;
				// add the subgroup content directly
				add(ret, pathBlock, subgroup);
			} else if (result instanceof ElementPathBlock) {
				if (pathBlock.isEmpty()) {
					ret.addElement(pathBlock);
				}
				add(pathBlock, (ElementPathBlock) result);
			} else if (result instanceof ElementTriplesBlock) {
				if (pathBlock.isEmpty()) {
					ret.addElement(pathBlock);
				}
				add(pathBlock, (ElementTriplesBlock) result);
				
			} else {
				ret.addElement(result);
			}
			
		}
		if (ret.getElements().size() == 1) {
			result = ret.getElements().get(0);
		} else {
			result = ret;
		}
	}

	@Override
	public void visit(ElementNamedGraph ng) {
		Element elt = ng.getElement();
		elt.visit(this);
		elt = result;
		Node n = ng.getGraphNameNode();
		if ( n == null) {
			result = new ElementNamedGraph(elt);
		} else {
			result = new ElementNamedGraph(n, elt);
		}
		
	}

	@Override
	public void visit(ElementNotExists notExists) {
		notExists.getElement().visit(this);
		result = new ElementNotExists(result);		
		
	}

	@Override
	public void visit(ElementOptional opt) {
		opt.getOptionalElement().visit(this);
		result = new ElementOptional(result);
		
	}

	@Override
	public void visit(ElementPathBlock e) {
		visit(OCUtils.toTriples(e));
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
	public void visit(ElementSubQuery e) {
		Query sq = e.getQuery();
		QueryPatternSimplification qps = new QueryPatternSimplification();
		sq.getQueryPattern().visit(qps);
		Element newelt = qps.getResult();
		Query newsq = sq.cloneQuery();
		newsq.setQueryPattern(newelt);
		result = new ElementSubQuery(newsq);
	}

	@Override
	public void visit(ElementTriplesBlock e) {
		result = e;
		
	}

	@Override
	public void visit(ElementUnion union) {
		// remove nested unions
		ElementUnion ret = new ElementUnion();
		for (Element elt : union.getElements()) {
			elt.visit(this);
			if (result instanceof ElementUnion) {
				ElementUnion nestedUnion = (ElementUnion) result;
				for (Element subelt: nestedUnion.getElements()) {
					assert !(subelt instanceof ElementUnion) : subelt +"\n"+ nestedUnion;
					ret.addElement(subelt);
				}
			} else {
				ret.addElement(result);
			}
		}
		
		
		if (ret.getElements().size() == 1) {
			result = ret.getElements().get(0);
		} else {
			result = ret;
		}
		
	}
	
	protected void add(ElementGroup targetGroup,ElementPathBlock targetGroupPathBlock, ElementGroup newGroup) {
		// add the subgroup content directly
		for (Element subelt: newGroup.getElements()) {
			if (subelt instanceof ElementPathBlock) {
				add(targetGroupPathBlock, (ElementPathBlock) subelt);
			} else if (subelt instanceof ElementTriplesBlock) {
				add(targetGroupPathBlock, (ElementTriplesBlock) subelt);
			} else if (subelt instanceof ElementGroup) {
				assert false : subelt+"\n"+newGroup ;
				//add(targetGroup, targetGroupPathBlock, (ElementGroup) subelt);
			} else {
				targetGroup.addElement(subelt);
			}
		}
	}
	protected void add(ElementPathBlock targetPathBlock, ElementPathBlock newPaths) {
		for (TriplePath tp:newPaths.getPattern().getList()) {
			targetPathBlock.addTriple(tp);
		}
	}
	protected void add(ElementPathBlock targetPathBlock, ElementTriplesBlock newTriples) {
		for (Triple t:newTriples.getPattern().getList()) {
			targetPathBlock.addTriple(t);
		}
	}
	
	public Query simplify(Query q) {
		QueryPatternSimplification qps = new QueryPatternSimplification();
		q.getQueryPattern().visit(qps);
		Element newelt = qps.getResult();
		Query ret = q.cloneQuery();
		ret.setQueryPattern(newelt);
		return ret;
	}
	
}
