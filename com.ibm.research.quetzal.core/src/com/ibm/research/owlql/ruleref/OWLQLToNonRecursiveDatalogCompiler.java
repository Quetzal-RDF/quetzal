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
import java.sql.Statement;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.query.Query;
import com.ibm.research.owlql.ConjunctiveQuery;
import com.ibm.research.owlql.ConsistencyCheckResult;
import com.ibm.research.owlql.IOWLQLCompiler;
import com.ibm.research.owlql.NormalizedOWLQLTbox;
import com.ibm.research.owlql.Taxonomy;
import com.ibm.research.owlql.TaxonomyImpl;
import com.ibm.research.owlql.rule.Rule;
import com.ibm.research.owlql.rule.RuleSystem;
import com.ibm.research.owlql.rule.VariableExpr;
import com.ibm.research.utils.OCUtils;

public class OWLQLToNonRecursiveDatalogCompiler implements IOWLQLCompiler {

	
	//private static final String GENERATED_VAR_PRFIX ="genVar_";
	private static final Logger logger = LoggerFactory.getLogger(OWLQLToNonRecursiveDatalogCompiler.class);
	/**
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
		List<ConjunctiveQuery> conjQueries = OCUtils.loadConjunctiveQueries(queryFile);
		OWLOntology ont = OCUtils.load(ontFiles);
		/*Set<ConjunctiveQuery> memQueries = Utils.getMembershipQuery(ont);
		logger.info("Number of membership queries: {}",memQueries.size());
		for (ConjunctiveQuery cq: memQueries) {
			logger.info(cq);
		}
		conjQueries.addAll(memQueries);*/
		int count = 1;
		OWLQLToNonRecursiveDatalogCompiler compiler = new OWLQLToNonRecursiveDatalogCompiler(ont, null, null);
		for (ConjunctiveQuery q: conjQueries) {
			//logger.info("Building non-recursive datalog for query: {}",q);
			long time = System.currentTimeMillis();
			RuleSystem rs = compiler.compileToNonRecursiveDatalog(q);
			//logger.info("Non-recursive datalog built in {} ms", (System.currentTimeMillis()-time));
			//logger.info("Result:\n", rs);
			Query cq =RuleSystemToUnionQuery.toUnionQuery(rs);
			StringBuffer buf =new StringBuffer();
			buf.append(cq+"\n");
			//logger.info("Generated Query:\n{}", buf);
			System.out.println("# Query #"+count);
			System.out.println(buf+";");
			count++;
		}
	}
	
	protected NormalizedOWLQLTbox tbox;
	protected RuleRenaming rename;
	protected Split splitter;
	protected EliminateEJVar ejVarEliminator;
	protected DeleteRuleWithUnsatifiableAtoms zeroArityAtomsEliminator;
	protected DeleteRuleWithConceptOrPropertyNotInAbox conceptsAndPropertiesNotInAboxEliminator;
	protected DeleteRedundantAtoms redundantAtomsEliminator;
	protected DeleteUnboundVariables unboundVarsEliminator;
	protected DefineAtomView atomViewDef;
	protected Taxonomy taxo;
	public OWLQLToNonRecursiveDatalogCompiler(OWLOntology originalOntTbox) {
		this(originalOntTbox, null, null);
	}
	public OWLQLToNonRecursiveDatalogCompiler(OWLOntology originalOntTbox, Statement st) {
		this(originalOntTbox,  null, null);
	}
	public OWLQLToNonRecursiveDatalogCompiler(OWLOntology originalOntTbox, Set<String> conceptURIsInAbox, Set<String> propertyURIsInAbox) {
		super();
		try {
			Set<OWLAxiom> tboxAxioms = NormalizedOWLQLTbox.getTboxRboxAxioms(originalOntTbox.getAxioms());
			/*if (completeTbox) {
				OWLOntology ontTbox = OWLManager.createOWLOntologyManager().createOntology();
				ontTbox.getOWLOntologyManager().addAxioms(ontTbox, tboxAxioms);
				PelletBasedTaxonomy.addImpliedTaxonomy(ontTbox);
				tboxAxioms = NormalizedOWLQLTbox.getTboxRboxAxioms(ontTbox.getAxioms());
			}*/
			tbox = new NormalizedOWLQLTbox(tboxAxioms);
			OWLDataFactory fac = tbox.getFactory();
			rename = new RuleRenaming(tbox);
			splitter = new Split(fac);
			taxo = new TaxonomyImpl(tbox);
			taxo.build();
			ejVarEliminator = new EliminateEJVar(tbox, taxo, fac);
			zeroArityAtomsEliminator = new DeleteRuleWithUnsatifiableAtoms(taxo,tbox);
			conceptsAndPropertiesNotInAboxEliminator = new DeleteRuleWithConceptOrPropertyNotInAbox(taxo, tbox, conceptURIsInAbox, propertyURIsInAbox);
			redundantAtomsEliminator = new DeleteRedundantAtoms(taxo, fac);
			unboundVarsEliminator = new DeleteUnboundVariables(fac);
			atomViewDef = new DefineAtomView(taxo, fac);
		} catch (OWLOntologyCreationException e) {
			throw new RuntimeException(e);
		}
	}
	  
	
	public RuleSystem compileToNonRecursiveDatalog(ConjunctiveQuery query) {
		return compileToNonRecursiveDatalog(toRules(query));
	}
	
	public RuleSystem compileToNonRecursiveDatalog(RuleSystem inputRs) {
		boolean distinct = inputRs.areResultsForMainHeadFormulaDistinct();
		RuleSystem rs = rename.rename(inputRs);
		logger.debug("Rule renaming: {}\n==>\n {}",inputRs, rs);
		RuleSystem rs2 = unboundVarsEliminator.deleteUnboundVariables(rs);
		logger.debug("Unbound variable elimination: {}\n==>\n {}",rs, rs2);
		RuleSystem rs3 =  redundantAtomsEliminator.deleteRedundantAtoms(rs2);
		logger.debug("Redundant atoms elimination: {}\n==>\n {}",rs2, rs3);
		RuleSystem rs4 = splitter.split(rs3);
		logger.debug("Rule splitting: {}\n==>\n {}",rs3, rs4);
		rs2 = zeroArityAtomsEliminator.deleteRuleWithUnsatifiableAtoms(rs4);
		logger.debug("Remove unsatisfiable rules: {}\n==>\n {}",rs4, rs2);
		if (rs2 == null) {
			return null;
		}
		if (inputRs.getMainHeadFormula()!=null 
		&& rs2.getRulesForHead(inputRs.getMainHeadFormula().getPredicate()).isEmpty()) {
			logger.debug("Main head formula elimitated: {}", inputRs.getMainHeadFormula());
			return null;
		}
		
		RuleSystem rulesystem = rs2;
		List<Rule> newRules= rulesystem.getRules();
		boolean hasChanged = true;
		while (hasChanged) {
			hasChanged = false;
			List<Rule> rulesToProcess = new LinkedList<Rule>();
			for (Rule r : newRules) {
				for (VariableExpr jv : ejVarEliminator.getJoinVariables(r)) {
					RuleSystem unificationRes = ejVarEliminator.eliminate(r, jv,
								new int[]{RuleSystem.nextAvailableRuleId(rulesystem)});
					logger.debug("Unification:\n\trule: {}\n\t variable: {}\n\tnew rules:{}",
								new Object[]{r, jv,unificationRes});
					if (unificationRes!=null) {
						RuleSystem rs5 = unboundVarsEliminator.deleteUnboundVariables(unificationRes);
						logger.debug("Unbound variable elimination: {}\n==>\n {}",unificationRes, rs5);
						RuleSystem rs6 =  redundantAtomsEliminator.deleteRedundantAtoms(rs5);
						logger.debug("Redundant atoms elimination: {}\n==>\n {}",rs5, rs6);
						RuleSystem rs7 = splitter.split(rs6);
						logger.debug("Rule splitting: {}\n==>\n {}",rs6, rs7);
						rulesToProcess.addAll(rs7.getRules());
						rulesystem = rulesystem.add(rs7);
						hasChanged = true;
					}
				}
			}
			newRules = rulesToProcess;
		}
		rulesystem = atomViewDef.addAtomViews(rulesystem);
		rs2 = zeroArityAtomsEliminator.deleteRuleWithUnsatifiableAtoms(rulesystem);
		logger.debug("Remove unsatisfiable rules: {}\n==>\n {}",rulesystem, rs2);
		if (rs2 == null) {
			return null;
		}
		rulesystem = conceptsAndPropertiesNotInAboxEliminator.deleteRuleWithConceptOrPropertyNotInAbox(rs2);
		logger.debug("Remove rules with concepts and properties not mentioned in the instance data: {}\n==>\n {}",rs2, rulesystem);
		if (rulesystem == null) {
			return null;
		}
		if (inputRs.getMainHeadFormula()!=null 
		&& rulesystem.getRulesForHead(inputRs.getMainHeadFormula().getPredicate()).isEmpty()) {
			logger.info("Main head formula elimitated: {}", inputRs.getMainHeadFormula());
			return null;
		}
		//logger.info("Compiled rule system:\n{}", rulesystem);
		return new RuleSystem(rulesystem.getRules(), inputRs.getMainHeadFormula(), distinct);
	}
	
	
	public RuleSystem toRules(ConjunctiveQuery query) {
		return new TriplesToRuleSystem(tbox).toRules(query);
	} 
	
	
	public ConsistencyCheckResult computeConsistencyCheck() {
		return tbox.computeConsistencyCheck(taxo);
	}

	public boolean canCompileToRules() {
		return true;
	}
	public boolean canCompileToSPARQL() {
		return true;
	}
	public boolean canCompileToSQL(boolean returnIDs) {
		return false; 
	}
	public RuleSystem compileToRules(ConjunctiveQuery q) {
		return compileToNonRecursiveDatalog(q);
	}
	public String compileToSQL(ConjunctiveQuery q, boolean returnIDs) {
		return null;
	}
	public Set<ConjunctiveQuery> compile(ConjunctiveQuery q) {
		RuleSystem rules = compileToRules(q);
		if (rules!=null) {
			return RuleSystemToQueries.toConjunctiveQueries(rules);
		} else {
			return Collections.emptySet();
		}
	}
	
	@Override
	public Query compileToUnionQuery(ConjunctiveQuery q) {
		RuleSystem rules = compileToRules(q);
		if (rules!=null) {
			return RuleSystemToUnionQuery.toUnionQuery(rules);
		} else {
			return null;
		}
	}
	@Override
	public boolean canCompileToUnionSPARQL() {
		return true;
	}
	public NormalizedOWLQLTbox getTBox() {
		return tbox;
	}

	
	
	
}
