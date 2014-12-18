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

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.vocabulary.RDF;
import com.ibm.iodt.sor.db.sql.CreatableTable;
import com.ibm.iodt.sor.db.sql.DatatypeFactory;
import com.ibm.iodt.sor.db.sql.Table;
import com.ibm.research.owlql.rule.AtomicFormula;
import com.ibm.research.owlql.rule.ConstantExpr;
import com.ibm.research.owlql.rule.Expr;
import com.ibm.research.owlql.rule.Predicate;
import com.ibm.research.owlql.rule.Rule;
import com.ibm.research.owlql.rule.RuleSystem;

public abstract class AbstractRDFStoreDBSchemaProcessor implements  IRDFStoreDBSchemaProcessor {

	static final Logger logger = LoggerFactory.getLogger(AbstractRDFStoreDBSchemaProcessor.class);
	protected Table triplesTable;
	protected Predicate triplePredicate;
	protected RuleSystem originalRules;
	protected RuleSystem newRules;
	protected Map<Predicate, CreatableTable> predicate2Table;
	protected Table singletonTable;
	
	

	public AbstractRDFStoreDBSchemaProcessor() {
		super();
	}
	public RuleSystem convertDLPredicateToDBTablePredicate() {
		return newRules;
	}

	public CreatableTable getDBTable(Predicate p) {
		return predicate2Table.get(p);
	}

	public CreatableTable getSingletonTable() {
		return singletonTable;
	}

	public void setRuleSystem(RuleSystem rs) {
		try {
			init(rs);
		} catch (Exception ex) {
			throw new  RuntimeException(ex);
		}
	}
	protected void init(RuleSystem rs) throws SQLException {
		predicate2Table = new HashMap<Predicate, CreatableTable>();
		originalRules = rs;
		createTriplesTable();
		//createNodesTable();
		createSingletonTable();
		//nonExistingID = getMinID().add(MINUS_ONE);
		//logger.debug("ID for non existing query uris: {}", nonExistingID);
		transform();
		populateNonHeadPredicateToTableMap();
		populateHeadPredicateToTableMap();
		
	}
	protected void createSingletonTable() {
		if (singletonTable==null) {
			Table t = new Table("singleton");
			t.addColumn("c1", DatatypeFactory.createInt(), false);
			singletonTable = t;
		}
	}
	protected void createTriplesTable() {
		triplesTable = new Table("triples");
		triplesTable.addColumn("s", DatatypeFactory.createBigInt(), false);
		triplesTable.addColumn("p", DatatypeFactory.createBigInt(), false);
		triplesTable.addColumn("o", DatatypeFactory.createBigInt(), false);
	}
	protected void populateHeadPredicateToTableMap() {
		Set<Predicate> allPredicates =new HashSet<Predicate>();
		allPredicates.add(new Predicate("triples", 3));
		String prefix = "dlpred_";
		int suffix = RuleSystem.nextAvailableSuffixForGeneratedPredicate(
				allPredicates, prefix);
		for (Predicate pred: newRules.getHeadPredicates()) {
			Table t;
			if (pred instanceof DLAnnotatedPredicate) {
				String name = prefix+(suffix++);
				t = new Table(name);
				allPredicates.add(new Predicate(name, pred.getArity()));
			} else {
				t = new Table(pred.getName());
			}
			for (int i=0;i<pred.getArity();i++) {
				t.addColumn("hash_"+(i+1), DatatypeFactory.createBigInt(), false);
			}
			predicate2Table.put(pred, t);
		}
		
	}
	protected void populateNonHeadPredicateToTableMap() {
		predicate2Table.put(triplePredicate, triplesTable);
	}
	
	protected abstract Object getID(ConstantExpr uri);
	protected abstract Object getID(Predicate p);

	

	protected void transform() {
		String prefix ="triples";
		RuleSystem rs = originalRules;
		int suffix = RuleSystem.nextAvailableSuffixForGeneratedPredicate(
						rs.getAllPredicates(), prefix);
		String triplePredicateName = suffix == 0? prefix: prefix+suffix;
		triplePredicate = new Predicate(triplePredicateName, 3);
		List<Rule> rules = new LinkedList<Rule>();
		Set<Predicate> headPreds = rs.getHeadPredicates();
		for (Rule r: rs.getRules()) {
			List<AtomicFormula> newBody = new LinkedList<AtomicFormula>();
			for (AtomicFormula af: r.getBody()) {
				if (headPreds.contains(af.getPredicate())
				|| Rule.isBuiltInPredicate(af.getPredicate())) {
					List<Expr> arguments = new LinkedList<Expr>();
					for (Expr e: af.getArguments()) {
						arguments.add(transform(e));
					}
					newBody.add(new AtomicFormula(af.getPredicate(), arguments));
				} else if (af.getPredicate().getArity()==2) {
					List<Expr> arguments = new LinkedList<Expr>();
					arguments.add(transform(af.getArguments().get(0)));
					arguments.add(transform(af.getPredicate()));
					arguments.add(transform(af.getArguments().get(1)));
					newBody.add(new AtomicFormula(triplePredicate, arguments));
				} else if (af.getPredicate().getArity()==1) {
					List<Expr> arguments = new LinkedList<Expr>();
					arguments.add(transform(af.getArguments().get(0)));
					arguments.add(transform(new Predicate(RDF.type.getURI(), 0)));
					arguments.add(transform(af.getPredicate()));
					newBody.add(new AtomicFormula(triplePredicate, arguments));
				} else if (af.getPredicate() instanceof TriplePredicate) {
					List<Expr> arguments = new LinkedList<Expr>();
					arguments.add(transform(af.getArguments().get(0)));
					arguments.add(transform(af.getArguments().get(1)));
					arguments.add(transform(af.getArguments().get(2)));
					newBody.add(new AtomicFormula(triplePredicate, arguments));
				} else {
					throw new RuntimeException("Predicate arity is restricted to 1 or 2: "+af);
				}
			}
			rules.add(new Rule(r.getHead().clone(), newBody, r.getId()));
		}
		newRules = new RuleSystem(rules, rs.getMainHeadFormula(), rs.areResultsForMainHeadFormulaDistinct());
		logger.debug("Transform : \n{}\n==>\n{})",originalRules,newRules);
	}
	protected Expr transform(Expr exp) {
		if (exp.isVariable()) {
			return exp;
		} else {
			assert exp.isConstant();
			ConstantExpr c = (ConstantExpr) exp;
			Object id = getID(c);
			return new ConstantExpr(id);
		}
	}
	
	protected Expr transform(Predicate p) {
		Object id = getID(p);
		return new ConstantExpr(id);
	}

	
}
