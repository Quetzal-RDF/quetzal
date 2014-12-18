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
 package com.ibm.research.owlql.rule;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.wala.util.collections.HashMapFactory;
import com.ibm.wala.util.collections.HashSetFactory;

/**
 * assumptions:
 *  1) Rules are safe
 *  2) Rules have been rectified
 *  3) Predicates are  properly ordered for efficient evaluation
 *  
 *  A rule system is immutable
 * @author <a href="mailto:achille@us.ibm.com">Achille Fokoue</a>
 *
 */
public class RuleSystem {
	
	public static interface RectificationAtomicFormulaFilter {
		public boolean accept(AtomicFormula af);
	}
	private static final Logger logger = LoggerFactory.getLogger(RuleSystem.class);
	
	
	public static int nextAvailableRuleId(RuleSystem rs) {
		int ret =-1;
		for (Rule r : rs.getRules()) {
			ret = Math.max(ret, r.getId());
		}
		return ret+1;
	}
	public static int nextAvailableSuffixForGeneratedPredicate(RuleSystem rs, String prefix) {
		Set<Predicate> preds = new HashSet<Predicate>(rs.getHeadPredicates());
		preds.addAll(rs.getNonHeadPredicates());
		return nextAvailableSuffixForGeneratedPredicate(preds, prefix);
	}
	public static int nextAvailableSuffixForGeneratedPredicate(Collection<Predicate> predicates, String prefix) {
		int ret = -1;
		for (Predicate p : predicates) {
			if (p.getName().length()> prefix.length()  
			&& p.getName().startsWith(prefix)) {
				String suffix = p.getName().substring(prefix.length());
				int val;
				try {
					val = Integer.parseInt(suffix);
					ret = Math.max(ret, val);
				} catch (Exception e) {
					
				}
			}
		}
		return ret+1;
	}
	/**
	 * ruleFile goal
	 * @param args
	 */
	public static void main(String[] args)  throws Exception{
		/*BufferedReader in = new BufferedReader(new FileReader(args[0]));
		try {
			StringBuffer buf = new StringBuffer();
			String line;
			while ((line=in.readLine())!=null) {
				if (!line.startsWith("#")) {
					buf.append(line+"\n");
				}
			}
			RuleSystem ruleSystem = parse(buf.toString());			
			logger.info("rule system:\n{}",ruleSystem);
			Graph<Predicate> dependencyGraph = DatalogEngine.buildDependencyGraph(ruleSystem);
			LinkedList<Set<Predicate>> sccs = DatalogEngine.topologicalSortOfSCC(dependencyGraph);
			
			
			logger.info("Number of predicates: {}", dependencyGraph.getNumberOfNodes());
			logger.info("Number of SCC in dependency graph: {}", sccs.size());
			int i=0;
			buf =new StringBuffer();
			for (Set<Predicate> ps : sccs){
				buf.append("SCC ").append(i++).append(" : ").append(ps.size()).append("\n");
				for (Predicate p : ps) {
					buf.append("\t").append(p).append("\n");
				}
			}
			logger.info(buf.toString());
			
			//Utils.getLogger().info("simplified rule system:\n"+ruleSystem.simplify());
			AtomicFormula goal = AtomicFormula.parse(args[1]);
			RuleSystem rectifiedRuleSystem=ruleSystem.rectifyRuleSystem(
					new OWLRLRectificationAtomicFormulaFilter()
				);
			logger.info("rectified equivalent rule system:\n"
					+rectifiedRuleSystem);
			
			dependencyGraph = DatalogEngine.buildDependencyGraph(rectifiedRuleSystem);
			sccs = DatalogEngine.topologicalSortOfSCC(dependencyGraph);
			logger.info("Number of predicates: {}", dependencyGraph.getNumberOfNodes());
			logger.info("Number of SCC in dependency graph: {}", sccs.size());
			i=0;
			buf =new StringBuffer();
			for (Set<Predicate> ps : sccs){
				buf.append("SCC ").append(i++).append(" : ").append(ps.size()).append("\n");
				for (Predicate p : ps) {
					buf.append("\t").append(p).append("\n");
				}
			}
			logger.info(buf.toString());
			
			//
			ruleSystem=rectifiedRuleSystem;
			ruleSystem = ruleSystem.simplify(Collections.singleton(goal.getPredicate()));
			logger.info("simplified equivalent rule system:\n"
					+ruleSystem);
			
			dependencyGraph = DatalogEngine.buildDependencyGraph(ruleSystem);
			sccs = DatalogEngine.topologicalSortOfSCC(dependencyGraph);
			logger.info("Number of predicates: {}", dependencyGraph.getNumberOfNodes());
			logger.info("Number of SCC in dependency graph: {}", sccs.size());
			i=0;
			buf =new StringBuffer();
			for (Set<Predicate> ps : sccs){
				buf.append("SCC ").append(i++).append(" : ").append(ps.size()).append("\n");
				for (Predicate p : ps) {
					buf.append("\t").append(p).append("\n");
				}
			}
			logger.info(buf.toString());
			//System.exit(0);
			
			RuleSystem uniqueBindingSys = ruleSystem.toUniqueBindingPattern(goal);
			logger.info("unique binding equivalent rule system:\n{}",uniqueBindingSys);
			
			dependencyGraph = DatalogEngine.buildDependencyGraph(uniqueBindingSys);
			sccs = DatalogEngine.topologicalSortOfSCC(dependencyGraph);
			logger.info("Number of predicates: {}", dependencyGraph.getNumberOfNodes());
			logger.info("Number of SCC in dependency graph: {}", sccs.size());
			i=0;
			buf =new StringBuffer();
			for (Set<Predicate> ps : sccs){
				buf.append("SCC ").append(i++).append(" : ").append(ps.size()).append("\n");
				for (Predicate p : ps) {
					buf.append("\t").append(p).append("\n");
				}
			}
			logger.info(buf.toString());
			//System.exit(0);
			RuleSystem magicSetSystem = ruleSystem.magicSetTransformation(goal);
			logger.info("magic-set equivalent rule system:\n{}", magicSetSystem);
			
			dependencyGraph = DatalogEngine.buildDependencyGraph(magicSetSystem);
			sccs = DatalogEngine.topologicalSortOfSCC(dependencyGraph);
			logger.info("Number of predicates: {}", dependencyGraph.getNumberOfNodes());
			logger.info("Number of SCC in dependency graph: {}", sccs.size());
			i=0;
			buf =new StringBuffer();
			for (Set<Predicate> ps : sccs){
				buf.append("SCC ").append(i++).append(" : ").append(ps.size()).append("\n");
				for (Predicate p : ps) {
					buf.append("\t").append(p).append("\n");
				}
			}
			logger.info(buf.toString());
			
			System.exit(0);
			
			logger.info("elimination of zero-arity predicate in the magic-set equivalent rule system:\n{}",
					magicSetSystem.transformPredicateOfZeroArity());
		} finally  {
			in.close();
		}
	*/	
	}
	private List<Rule> rules;
	private Map<Predicate, Set<Rule>> head2Rules;
	private Set<Predicate> nonHeadPredicates;
	
	/**
	 * the head formula to evaluate
	 */
	private AtomicFormula mainHeadFormula;
	
	private boolean distinct;
	public RuleSystem(List<Rule> rules) {
		this(rules, null, true);
	}
	public RuleSystem(List<Rule> rules, AtomicFormula mainHeadFormula, boolean distinct) {
		super();
		boolean assertionEnabled = false;
		assert assertionEnabled = true;
		if (assertionEnabled) {
			Map<Integer, Rule> id2Rule = HashMapFactory.make();
			for (Rule r: rules) {
				Rule oldr = id2Rule.put(r.getId(), r);
				assert oldr == null : oldr+"\n"+r;
			}
		}
		this.rules = new ArrayList<Rule>(rules);
		this.mainHeadFormula = mainHeadFormula;
		this.distinct = distinct;
		
		init();
	}
	public AtomicFormula getMainHeadFormula() {
		return mainHeadFormula;
	}
	public boolean areResultsForMainHeadFormulaDistinct() {
		return distinct;
	}
	public Set<String> getAllVariableNames() {
		 Set<String> ret = new HashSet<String>();
		 for (Rule r:rules) {
			 for (VariableExpr e: r.getAllRuleVariables()) {
				 ret.add(e.getName());
			 }
		 }
		 return ret;
	}
	
	public RuleSystem clone() {
		List<Rule> rs = new ArrayList<Rule>(rules.size());
		for (Iterator<Rule> it=rules.iterator();it.hasNext();) {
			Rule r = it.next();
			rs.add(r.clone());
		}
		AtomicFormula main = null;
		if (mainHeadFormula!=null) {
			main = mainHeadFormula.clone(); 
		}
		return new RuleSystem(rs, main, distinct);
	}
	
	protected void init() {
		head2Rules = new HashMap<Predicate, Set<Rule>>();
		nonHeadPredicates = new HashSet<Predicate>();
		boolean mainHeadFormulaFound  = false;
		for (Iterator<Rule> it=rules.iterator();it.hasNext();) {
			Rule r= it.next();
			Predicate headPred = r.getHead().getPredicate();
			Set<Rule> set = head2Rules.get(headPred);
			if (set == null) {
				set = new HashSet<Rule>();
				head2Rules.put(headPred,set);
			}
			set.add(r);
			for (int i=0;i<r.getBody().size();i++) {
				nonHeadPredicates.add(r.getBody().get(i).getPredicate());
			}
			if (mainHeadFormula!=null 
			&& !mainHeadFormulaFound
			&& mainHeadFormula.equals(r.getHead())) {
				mainHeadFormulaFound = true;
			}
		}
		if (mainHeadFormula!= null && !mainHeadFormulaFound) {
			throw new RuntimeException("The specified main head formula for query evaluation is not the head formula of any rule");
		}
		nonHeadPredicates.removeAll(head2Rules.keySet());
	}

	/**
	 * return a unmodifiable list of rules
	 * @return
	 */
	public List<Rule> getRules() {
		return Collections.unmodifiableList(rules);
	}
	
	/**
	 * Constructs a Rule/Goal Graph as indicated in J.D. Ullman's
	 * Principles of Database and knownledge-base systems  vol II
	 * chapter 12 section 12.8
	 * @param goal (non-negated goal)
	 * @return
	 */
	protected RuleGoalGraph build(AtomicFormula goal) {
		assert !goal.getPredicate().isNegated(): goal;
		assert !goal.getPredicate().isOptional(): goal;
		PredicateAdornment goalAdornment = buildAdormentForGoal(goal);
		RuleGoalGraph ret =  new RuleGoalGraph(goalAdornment);
		if (!isIDB(goal.getPredicate())) {
			//1. A goal node with an EDB predicate has no successors
			return ret;
		}
		Queue<RuleGoalNode> queue = new LinkedList<RuleGoalNode>();
		queue.offer(ret.getTopGoalNode());
		while (!queue.isEmpty()) {
			RuleGoalNode node = queue.poll();
			List<Adornment> ads = computeChildren(node.getAdornment());
			for (Iterator<Adornment> it=ads.iterator();it.hasNext();) {
				Adornment ad = it.next();
				RuleGoalNode childNode = ret.get(ad);
				if (childNode==null) {
					childNode=ret.add(ad);
					queue.offer(childNode);
				}
				node.addChild(childNode);
				
			}
		}
		return ret;	
		
	}
	/**
	 * Implements the algorithm for making binding pattern unique described in
	 * J.D. Ullman's  Principles of Database and knowledge-base systems  vol II
	 * chapter 12 section 12.9
	 * @param goal
	 * @return
	 */
	protected RuleSystem toUniqueBindingPattern(AtomicFormula goal) {
		RuleGoalGraph graph = build(goal);
		List<Rule> newRules= new ArrayList<Rule>();
		for (Iterator<RuleGoalNode> it=graph.getGoalNodes().iterator();it.hasNext();) {
			RuleGoalNode goalNode = it.next();
			assert goalNode.isGoal();
			for (Iterator<RuleGoalNode> ruleNodes=goalNode.getChildren().iterator();ruleNodes.hasNext();) {
				RuleGoalNode ruleNode = ruleNodes.next();
				assert ruleNode.isRule();
				Rule rule =((RuleAdornment)ruleNode.getAdornment()).getRule();
				List<PredicateAdornment> children = getAllPredicateChildren(ruleNode);
				List<AtomicFormula> newBody = transformToUniqueBindingBody(children, rule);
				PredicateAdornment predAd = (PredicateAdornment)goalNode.getAdornment();
				if (predAd.getPredicate().isOptional()) {
					predAd = new PredicateAdornment(predAd.getPredicate().switchOptionalFlag(), predAd.getBoundArguments());
				} else if (predAd.getPredicate().isNegated()) {
					predAd = new PredicateAdornment(predAd.getPredicate().negate(), predAd.getBoundArguments());
				}
				AtomicFormula newHead=transformToUniqueBinding(rule.getHead(), predAd);
				newRules.add(new Rule(newHead,newBody, newRules.size()));
			}
		}
		return new RuleSystem(newRules);
	}
	
	protected Predicate createUniqueBindingPredicate(PredicateAdornment predAd) {
		if (isIDB(predAd.getPredicate())
		|| (predAd.getPredicate().isNegated() && isIDB(predAd.getPredicate().negate()))
		|| (predAd.getPredicate().isOptional() && isIDB(predAd.getPredicate().switchOptionalFlag()))) {
			return UniqueBindingPredicate.createUniqueBindingPredicate(predAd);
		} else {
			return predAd.getPredicate();
		}
	}
	protected AtomicFormula transformToUniqueBinding(AtomicFormula oldFormula, PredicateAdornment predAdorn) {
		assert oldFormula.getPredicate().equals(predAdorn.getPredicate()): oldFormula+"\n"+predAdorn;
		Predicate newPred = createUniqueBindingPredicate(predAdorn);
		return new AtomicFormula(newPred, new ArrayList<Expr>(oldFormula.getArguments()));
	}
	protected List<AtomicFormula> transformToUniqueBindingBody(List<PredicateAdornment> predAdorn, Rule rule) {
		assert rule.getBody().size() == predAdorn.size();
		List<AtomicFormula> ret= new ArrayList<AtomicFormula>();
		List<AtomicFormula> body = rule.getBody();
		for (int i=0;i<predAdorn.size();i++) {
			PredicateAdornment ad = predAdorn.get(i);
			AtomicFormula f = body.get(i);
			assert f.getPredicate().equals(ad.getPredicate());
			ret.add(transformToUniqueBinding(f,ad));
		}
		return ret;
	}
	protected List<PredicateAdornment> getAllPredicateChildren(RuleGoalNode ruleNode) {
		assert ruleNode.isRule();
		List<PredicateAdornment>  ret = new LinkedList<PredicateAdornment>();
		Queue<RuleGoalNode> queue = new LinkedList<RuleGoalNode>();
		queue.add(ruleNode);
		while (!queue.isEmpty()) {
			RuleGoalNode node = queue.poll();
			assert node.isRule();
			assert node.getChildren().size()<3;
			//assert !node.getChildren().isEmpty();
			for (Iterator<RuleGoalNode> it=node.getChildren().iterator();it.hasNext();) {
				RuleGoalNode child = it.next();
				if (child.isGoal()) {
					ret.add((PredicateAdornment) child.getAdornment());
				} else {
					assert child.isRule();
					queue.offer(child);
				}
			}	
		}
		return  ret;
	}
	public RuleSystem magicSetTransformation(AtomicFormula goal) {
		return magicSetTransformation(goal,true, true, true);
	}
	
	protected boolean accept(Rule r, RectificationAtomicFormulaFilter filter) {
		if (filter == null) {
			return true;
		}
		if (!filter.accept(r.getHead().unrectify())) {
			return false;
		}
		for (AtomicFormula af: r.getBody()) {
			if (!filter.accept(af.unrectify())) {
				return false;
			}
		}
		return true;
	}
	public RuleSystem rectifyRuleSystem(RectificationAtomicFormulaFilter filter) {
		/* Get all atomic formulas that need rectification */
		HashMap<AtomicFormula,Predicate> toRectifyAtomicFormulas=new HashMap<AtomicFormula,Predicate>();
		for(Rule r: rules){
			List<AtomicFormula> rBody=r.getBody();
			for(AtomicFormula af : rBody ){
				if(af.needsRectification() && head2Rules.containsKey(af.getPredicate())){
					toRectifyAtomicFormulas.put(af,af.getPredicate());
				}
			}
		}
		/* If no atomic formula needs rectification, return this */
		if(toRectifyAtomicFormulas.size()==0)return this;
		List<Rule> rRules=new LinkedList<Rule>();
		Set<Rule> alreadySeen = new HashSet<Rule>();
		for(Rule r: rules){
			// rectification of the head
			r = rectifyRuleHead(r);
			//
			List<AtomicFormula>  rectifiedBody = rectifyBody(r.getBody(),toRectifyAtomicFormulas);						
			//AtomicFormula rectifiedHead = rectifyHead(r.getHead(), toRectifyAtomicFormulas);
			Rule newRule = new Rule(r.getHead(),rectifiedBody, rRules.size());
			if (alreadySeen.add(newRule)
				&& !newRule.isUnsatisfiableBasedOnEquilityBetweenDifferentConstants()			
				&& accept(newRule, filter))  {
				//rRules.add(new Rule(rectifiedHead,rectifiedBody, rRules.size()));
				rRules.add(newRule);
			}
		}
		
		// remove from toRectifyAtomicFormulas  atomicformulas whose rectification predicate 
		// is already defined in the original rule system or atomicformulas not accepted by the filter
		// 
		for (Iterator<AtomicFormula> it = toRectifyAtomicFormulas.keySet().iterator();it.hasNext();) {
			AtomicFormula f = it.next();
			AtomicFormula rf = f.rectify();
			if (head2Rules.containsKey(rf.getPredicate()) 
			|| (filter!=null && !filter.accept(f.unrectify())) ) {
				it.remove();
			}
		}
		//
		
		//logger.debug("Rect 0:\n{}", new RuleSystem(rRules));
		int origSize = rRules.size();
		for (int i=0; i< origSize; i++) {
			Rule r = rRules.get(i);
			// get rectification rules;
			List<Rule> rr=rectifyRule(r, toRectifyAtomicFormulas, rRules.size());
			//logger.debug("Rectified {}\n{}", r, new RuleSystem(rr));
			for (Rule ru: rr) {
				if (alreadySeen.add(ru)
					&& !ru.isUnsatisfiableBasedOnEquilityBetweenDifferentConstants()
					&& accept(ru, filter) ) {
					rRules.add(ru);
				}
			}
		}
		RuleSystem newRuleSystem=new RuleSystem(rRules);
		//logger.info("Rect:\n{}", newRuleSystem);
		logger.info("Rect size: {}", newRuleSystem.getRules().size());
		if (logger.isInfoEnabled()) {
			int count =0;
			for (Rule r: newRuleSystem.getRules()) {
				if (r.getHead().getPredicate().getArity() == 0) {
					count++;
				}
			}
			logger.info("Rect size: zero arity {}. non-zero arity {}", count, newRuleSystem.getRules().size()-count);
			StringBuffer buf = new StringBuffer();
			for (Map.Entry<Expr, Integer> e: RectificationPredicate.constantMaps.entrySet() ) {
				buf.append(e.getValue()+" --> "+e.getKey()+"\n");
			}
			logger.info("constantMaps:\n{}",buf);
			
		}
		return newRuleSystem.rectifyRuleSystem(filter);
	}
	
	
	List<AtomicFormula> rectifyBody(List<AtomicFormula> body, HashMap<AtomicFormula,Predicate> toRectifyAtomicFormulas){
		List<AtomicFormula>  rectifiedBody = new LinkedList<AtomicFormula>();
		for(AtomicFormula af: body){
			if(toRectifyAtomicFormulas.containsKey(af))
				rectifiedBody.add(af.rectify());
			else rectifiedBody.add(af);
		}
		return rectifiedBody;
	}
	
	protected static Rule rectifyRuleHead(Rule rule){
		// find arguments in the head that need
		// to be replaced by variables
		Set<Integer> pos = new HashSet<Integer>();
		Set<VariableExpr> varSet = new HashSet<VariableExpr>();
		for (int  i=0; i<rule.getHead().getArguments().size();i++) {
			Expr e = rule.getHead().getArguments().get(i);
			if (e.isVariable()) {
				if(varSet.contains(e)) {
					pos.add(i);
				} else {
					varSet.add((VariableExpr)e);
				}
			}
			if(e.isConstant()) {
				pos.add(i);
			}
		}
		if (pos.isEmpty()) {
			// no need for head rectification
			return rule;
		}
		List<AtomicFormula> newBody = new LinkedList<AtomicFormula>();
		List<Expr> newHeadArguments = new LinkedList<Expr>();
		Set<VariableExpr> allVarsInRule = new HashSet<VariableExpr>(rule.getAllRuleVariables());
		int count =0;
		String headVarPrefix = "HRectVar";
		for (int i=0;i<rule.getHead().getArguments().size();i++ ) {
			if (!pos.contains(i)) {
				newHeadArguments.add(rule.getHead().getArguments().get(i));
			} else {
				VariableExpr newVar;
				do {
					newVar= new VariableExpr(headVarPrefix+"_"+count++);
				}
				while ( allVarsInRule.contains(newVar));
				newHeadArguments.add(newVar);
				newBody.add(new AtomicFormula(new Predicate(Rule.BUILT_IN_EQUAL, 2),
						newVar, rule.getHead().getArguments().get(i)));
			}
			
		}
		newBody.addAll(rule.getBody());
		return new Rule(new AtomicFormula(rule.getHead().getPredicate().clone(), newHeadArguments), newBody, rule.getId());
	}
	
	List<Rule> rectifyRule(Rule r, HashMap<AtomicFormula,Predicate> toRectifyAtomicFormulas, int id){
		List<Rule> rectificationRuleList=new LinkedList<Rule>();
		AtomicFormula head=r.getHead();
		Set<Predicate> alreadyProcessed = new HashSet<Predicate>();
		//if(toRectifyAtomicFormulas.containsValue(head.getPredicate())){
		for(AtomicFormula af: toRectifyAtomicFormulas.keySet()){
			if(af.getPredicate().equals(head.getPredicate())){
				//HashMap<Expr,Expr> mappings=head.computeArgumentMapping(af);
				AtomicFormula rectificationRuleHead=af.rectify();
				if (alreadyProcessed.add(rectificationRuleHead.getPredicate())) {
					List<AtomicFormula> rectificationRuleBody = replaceByAtomicFormulaRuleDefinition(af, r, af.getAllVariables());
					//=new LinkedList<AtomicFormula>();
					//for(AtomicFormula baf : r.getBody()){
					//	rectificationRuleBody.add(baf.cloneWithArgumentMapping(mappings));
					//}
					if (rectificationRuleBody!=null) {
						rectificationRuleList.add(new Rule(rectificationRuleHead,rectificationRuleBody, id++));
					}
				}
			}
		}
		//}
		return rectificationRuleList;
	}
	
	public RuleSystem magicSetTransformation(AtomicFormula goal, boolean addInitRule) {
		return magicSetTransformation(goal,addInitRule, true, true);
	}
	
	/**
	 * For a headPredicate h of a rule r of the form h() :- B1(...) ^ ... ^ Bn(...)
	 * into h(Z) :- Z = 0 ^ B1(...) ^ ... ^ Bn(...) where Z is new variable not appearing 
	 * in any Bi(...). Furthermore, in body of any rule r', h() is replaced by h(0) 
	 * 
	 * @return
	 */
	public RuleSystem transformPredicateOfZeroArity() {
		Map<Predicate, VariableExpr> zeroArgPred2Variable = new HashMap<Predicate, VariableExpr>();
		// find valid variable names for the transformed zero argument predicates
		for (Iterator<Map.Entry<Predicate, Set<Rule>>> it=head2Rules.entrySet().iterator();it.hasNext();) {
			Map.Entry<Predicate, Set<Rule>> e = it.next();
			Predicate p = e.getKey();
			if (p.getArity()!=0) {
				continue;
			}
			Set<Rule> pRules =  e.getValue();
			Set<VariableExpr> varInPRules = new HashSet<VariableExpr>();
			for (Iterator<Rule> rs = pRules.iterator();rs.hasNext();) {
				Rule r = rs.next();
				varInPRules.addAll(r.getAllRuleVariables());
			}
			VariableExpr communVar = new VariableExpr("Z");
			int count =2;
			while (varInPRules.contains(communVar)) {
				communVar = new VariableExpr("Z_"+count++);
			}
			zeroArgPred2Variable.put(p, communVar);
		}
		
		List<Rule> newRules = new ArrayList(rules.size());
		
		if (zeroArgPred2Variable.isEmpty()) {
			// no zero arg predicate
			return this;
		}
		
		//Perform the transformation
		for (Iterator<Rule> it = rules.iterator();it.hasNext();) {
			Rule r = it.next();
			AtomicFormula newHead;
			List<AtomicFormula> newBody = new ArrayList<AtomicFormula>(r.getBody().size()+1);
			VariableExpr var = zeroArgPred2Variable.get(r.getHead().getPredicate());
			if (r.getHead().getPredicate().getArity()==0) {
				assert var!=null : r;
				newHead =  new AtomicFormula(r.getHead().getPredicate().cloneWithNonZeroArity(),
							Collections.singletonList((Expr)var.clone()));
			} else {
				assert var == null: r ;
				newHead = r.getHead().clone();
			}
			
			if (r.getHead().getPredicate().getArity()==0) {
				// we add Z= 0
				Predicate equal = new Predicate(Rule.BUILT_IN_EQUAL, 2);
				List<Expr> args = new ArrayList<Expr>(2);
				args.add(var.clone());
				args.add(new ConstantExpr(0));
				AtomicFormula equalF = new AtomicFormula(equal,args);
				newBody.add(equalF);
			}
			
			//replace any f() in the body by f(0)
			for (Iterator<AtomicFormula> fs=r.getBody().iterator();fs.hasNext();) {
				AtomicFormula f = fs.next();
				AtomicFormula newF;
				if (f.getArguments().isEmpty()) {
					newF = new AtomicFormula(f.getPredicate().cloneWithNonZeroArity(), 
							Collections.singletonList((Expr) new ConstantExpr(0) ));
				} else {
					newF = f.clone();
				}
				newBody.add(newF);
			}
			newRules.add(new Rule(newHead, newBody, r.getId()));
		}
		return new RuleSystem(newRules);
	}
	
	/**
	 * <p> Two body atoms A and B unify to the atom C  iff. </p>
	 * <p>
	 * <ol>
	 * 	<li>A and B have the same predicate p and arity n. The predicate of C is also p with arity n. </li>
	 *	<li> For each  0 &le; i < n, at least one of the following must hold
	 *		<ul>
	 *			<li> if A[i] and B[i] are constants, then A[i]=B[i]. In this case, C[i] = A[i]</li>
	 *			<li> B[i] is an unconstrained variable.  An unconstrained variable is a variable that appears at most once in the body and does not appears in the head.
	 * 		  In this case, C[i] = A[i] </li>
	 *			<li> A[i] is an unconstrained variable.	In this case C[i] = B[i] </li>
	 *			<li> if A[i] and B[i] are not unconstrained variables, then A[i]=B[i]. In this case, C[i] = A[i]</li>
	 * 		</ul>
	 *  </li>
	 * </ol>
	 * @param a the first body atom
	 * @param b the second body atom
	 * @param unconstrainedVars the set of unconstrained variables in the body of the rule
	 * @param atMostConstrainSubstitution indicates whether at most one substitution of an unconstrained variable with a constant or a constrained variable is allowed
	 * @param rule
	 * @return the result of the unification of two body atoms if they unify; otherwise it returns <code>null</code> 
	 */
	protected static AtomicFormula unify(AtomicFormula a, AtomicFormula b, Set<VariableExpr> unconstrainedVars, boolean atMostConstrainSubstitution) {
		// A and B have the same predicate p and arity n. The predicate of C is also p with arity n. 
		
		if (!a.getPredicate().equals(b.getPredicate())) {
			return null;
		}
		Predicate p = a.getPredicate();
		List<Expr> args = new ArrayList<Expr>();
		Iterator<? extends Expr> ita = a.getArguments().iterator();
		Iterator<? extends Expr> itb = b.getArguments().iterator();
		int constSubst = 0;
		for (int i=0;i<p.getArity();i++) {
			Expr ea = ita.next();
			Expr eb = itb.next();
			// if A[i] and B[i] are constants, then A[i]=B[i]. In this case, C[i] = A[i]
			if (ea.isConstant() && eb.isConstant()) {
				if (ea.equals(eb)) {
					args.add(ea);
				} else {
					// cannot unify
					return null;
				}
			} else if (unconstrainedVars.contains(eb)) {
				//  B[i] is an unconstrained variable.  In this case, C[i] = A[i] 
				if (atMostConstrainSubstitution && 
					(ea.isConstant() || !unconstrainedVars.contains(ea))){
					if (++constSubst>1) {
						return null;
					}
				}
				args.add(ea);
			} else if (unconstrainedVars.contains(ea)) {
				// A[i] is an unconstrained variable.	In this case C[i] = B[i]
				if (atMostConstrainSubstitution && 
					(eb.isConstant() || !unconstrainedVars.contains(eb))){
					if (++constSubst>1) {
						return null;
					}
				}
				args.add(eb);
			} else if (ea.equals(eb)){
				//  if A[i] and B[i] are not unconstrained variables, then A[i]=B[i]. In this case, C[i] = A[i]
				args.add(ea);
			} else {
				// cannot unify
				return null;
			}
		}
		return new AtomicFormula(p, args);
	}
	
	/**
	 * <p> The atomic formula A is subsumed by atomic formula B  iff. </p>
	 * <p>
	 * <ol>
	 * 	<li>A and B have the same predicate p and arity n. </li>
	 *	<li>For each  0 &le; i < n, one of the following must hold
	 *		<ul>
	 *			<li> if B[i] is a constant, then A[i]=B[i]. 
	 *			<li> if B[i] is not an unconstrained variable, then A[i] = B[i].  An unconstrained variable is a variable that appears at most once in the body and does not appears in the head.
	 * 			 </li>
	 * 			<li>   B[i] is  an unconstrained variable </li>
	 * 		
	 * 		</ul>
	 *  </li>
	 * </ol>
	 * @param sub the first body atom (subsumee)
	 * @param sup the second body atom (subsumer) 
	 * @param unconstrainedVars the set of unconstrained variables in the body of the rule
	 * @param rule
	 * @return whether an atomic formula (sub) is subsumed by another one (sup)</code> 
	 */
	protected static boolean isSubsumedBy(AtomicFormula sub, AtomicFormula sup, Set<VariableExpr> unconstrainedVars) {
		// A and B have the same predicate p and arity n. The predicate of C is also p with arity n. 
		
		if (!sub.getPredicate().equals(sup.getPredicate())) {
			return false;
		}
		Predicate p = sub.getPredicate();
		Iterator<? extends Expr> ita = sub.getArguments().iterator();
		Iterator<? extends Expr> itb = sup.getArguments().iterator();
		for (int i=0;i < p.getArity();i++) {
			Expr ea = ita.next();
			Expr eb = itb.next();
			// if B[i] is a constant, then A[i]=B[i]
			if (eb.isConstant()) {
				if (!eb.equals(ea)) {
					return false;
				}
			} else if (!unconstrainedVars.contains(eb)) {
				// if B[i] is not an unconstrained variable, then A[i] = B[i]
				if (!eb.equals(ea)) {
					return false;
				}
			}
		}
		return true;
	}
	/**
	 * returns the set of unconstrained variables in the body of a rule.
	 *  An unconstrained variable is a variable that appears at most once in the body and does not appears in the head
	 * @param rule
	 * @return
	 */
	protected static Set<VariableExpr> getUnconstrainedVariables(Rule rule) {
		Set<VariableExpr> ret = new HashSet<VariableExpr>();
		// all variables except head variables are candidates 
		ret.addAll(rule.getAllRuleVariables());
		ret.removeAll(rule.getHead().getAllVariables());
		//
		if (!ret.isEmpty()) {
			Set<VariableExpr> seenBodyVars = new HashSet<VariableExpr>();
			for (AtomicFormula af: rule.getBody()) {
				for (Expr e: af.getArguments()) {
					if (e.isVariable()) {
						if (!seenBodyVars.add((VariableExpr)e)) {
							// e appears twice or more ==> should be removed
							ret.remove(e);
						}
					}
				}
			}
		}		
		return ret;
		
	}
	protected static Set<VariableExpr> getUnconstrainedVariables(Collection<AtomicFormula> afs) {
		Set<VariableExpr> ret = new HashSet<VariableExpr>();
		// all variables are candidates
		for (AtomicFormula af: afs) {
			for (Expr e: af.getArguments()) {
				if (e.isVariable()) {
					ret.add((VariableExpr)e);
				}
			}
		}
		//
		if (!ret.isEmpty()) {
			Set<VariableExpr> seenBodyVars = new HashSet<VariableExpr>();
			for (AtomicFormula af: afs) {
				for (Expr e: af.getArguments()) {
					if (e.isVariable()) {
						if (!seenBodyVars.add((VariableExpr)e)) {
							// e appears twice or more ==> should be removed
							ret.remove(e);
						}
					}
				}
			}
		}		
		return ret;
		
	}
	
	
	
	
	protected static boolean isDirectlyRecursive(Rule r) {
		Predicate headp = r.getHead().getPredicate();
		for (AtomicFormula af: r.getBody()) {
			if (af.getPredicate().equals(headp)) {
				return true;
			}
		}
		return false;
	}
	/**
	 * An optional atomic formula af is useless in a rule r iff. one of the following holds
	 * <ol>
	 * 	<li> its set of variables is a subset of variables appearing in mandatory atomic formulas</li>
	 *  <li> its variables that are not in mandatory formulas do not appear in the head of the rule r </li>
	 * </ol>
	 * @param rule
	 * @return
	 */
	protected static Rule removeUselessOptionalAtomicFormulas(Rule rule)  {
		AtomicFormula newHead = rule.getHead().clone();
		List<AtomicFormula> newBody = new ArrayList<AtomicFormula>();
		Set<VariableExpr> headVars = rule.getHead().getAllVariables();
		Set<VariableExpr> mandatoryVars = new HashSet<VariableExpr>();
		for (AtomicFormula af: rule.getMandatoryBody()) {
			mandatoryVars.addAll(af.getAllVariables());
		}
		for (AtomicFormula af:rule.getBody()) {
			if (af.getPredicate().isOptional()) {
				Set<VariableExpr> afVars = af.getAllVariables();
				if (!mandatoryVars.containsAll(afVars)) {
					for (VariableExpr v: afVars) {
						if (!mandatoryVars.contains(v) && headVars.contains(v)) {
							newBody.add(af.clone());
							break;
						}
					}
				}				
			} else {
				newBody.add(af.clone());
			}
		}
		return newBody.size() == rule.getBody().size()? rule : new Rule(newHead, newBody, rule.getId());
		
	}
	protected static RuleSystem removeUselessOptionalAtomicFormulas(RuleSystem ruleSystem) {
		
		List<Rule> newRules = new ArrayList<Rule>();
		Set<Rule> seenRules = new HashSet<Rule>();
		boolean changed = false;
		for (Rule rule: ruleSystem.getRules()) {
			Rule newRule = removeUselessOptionalAtomicFormulas(rule);
			if (!newRule.equals(rule)) {
				changed = true;
			}
			if (seenRules.add(newRule)) {
				newRules.add(newRule);
			}
		}
		return changed? new RuleSystem(newRules, ruleSystem.getMainHeadFormula(), ruleSystem.distinct) : ruleSystem;
	}
	/**
	* Removes atomic formulas that subsume other atomic formula in the body of the rule
	*/
	protected static Rule removeRedundantBodyAtoms(Rule rule, RuleSystem rs) {
		Set<Integer> toRemove = new HashSet<Integer>();
		
		do {
			Set<VariableExpr>  unconstrainedVars = getUnconstrainedVariables(rule);
			toRemove.clear();
			for (int i=0; i<rule.getBody().size();i++) {
				AtomicFormula a = rule.getBody().get(i);
				if (toRemove.contains(i)) {
					// already marked for removal
					continue;
				}
				loopj : for (int j=i+1; j<rule.getBody().size(); j++) {
					AtomicFormula b = rule.getBody().get(j);
					if (toRemove.contains(j)) {
						// already marked for removal	
						continue;
					}
					if (isSubsumedBy(a, b, unconstrainedVars)) {
						// a is subsumed by b ==> we can remove b
						toRemove.add(j);
						continue loopj;
					} else if (isSubsumedBy(b, a, unconstrainedVars)) {
						// b is subsumed by a ==> we can remove a
						toRemove.add(i);
						break loopj;
					} 					
					
					{
						//if there is a rule ra defining a such that ra is made of a single subgoal subgaola
						// such that subgoala subsumes b
						// ==> we can remove a 
						// TODO: Generalize this optimization by considering indirect subsumption
						
						for (int k=0;k<2; k++) {
							AtomicFormula sup, sub;
							int removeIndex;
							if (k==0) {
								sup = a;
								sub = b;
								removeIndex = i;
							} else {
								sup = b;
								sub = a;
								removeIndex = j;
							}
							for (Rule rsup: rs.getRulesForHead(sup.getPredicate())) {
								if (rsup.getBody().size()!=1) {
									continue;
								}
								List<AtomicFormula> supreplacement = replaceByAtomicFormulaRuleDefinition(sup, rsup, 	
										new HashSet<VariableExpr>(rule.getAllRuleVariables()));
								if (supreplacement == null || supreplacement.size()!=1) {
									continue;
								}
								AtomicFormula subgoalsup = supreplacement.get(0);
								if (isSubsumedBy(sub, subgoalsup, new HashSet<VariableExpr>(Rule.getUnboundVariables(sup, supreplacement)))) {
									// b is subsumed by a ==> we can remove a
									toRemove.add(removeIndex);
									if (removeIndex == i) {
										break loopj;
									} else {
										continue loopj;
									}
								}
							}
						}
						
						
					}
					
					{
						// if  rules rbs defining the predicate pb of b are all not directly recursive
						// and for each rule rb in rbs there is a subgoal subB of rb such that
						// and a subsumes subB
						// ==> we can remove a
						// TODO: Generalize this optimization to the case where rb is not recursive (directly or indirectly)
						// by also considering whether a subsumes an atomic formula obtained from recursively unfolding  b
						
						Predicate pb = b.getPredicate();
						Set<Rule> rules = rs.getRulesForHead(pb);
						boolean directlyRecursive = false;;
						for (Rule ru: rules) {
							if (isDirectlyRecursive(ru)) {
								directlyRecursive = true;
								break;
							}
						}
						
						
						if (!directlyRecursive && !rules.isEmpty()) {
							boolean removeA = true;
							for (Rule rb : rules) {
								List<AtomicFormula> breplacement = replaceByAtomicFormulaRuleDefinition(b, rb,
										new HashSet<VariableExpr>(rule.getAllRuleVariables()));
								if (breplacement == null) {
									removeA = false;
									break;
								}
								Set<AtomicFormula> afs = new HashSet<AtomicFormula>(rule.getBody());
								afs.remove(b);
								afs.addAll(breplacement);
								Set<VariableExpr>  newUnconstrainedVars = getUnconstrainedVariables(afs);
								newUnconstrainedVars.removeAll(rule.getHead().getAllVariables());
								boolean success = false;
								for (AtomicFormula brp : breplacement) {
									if (isSubsumedBy(brp, a, newUnconstrainedVars)) {
										// b is subsumed by a ==> we can remove a
										//toRemove.add(i);
										success = true;
										break;
									} 
								}
								removeA &= success;
								if (!removeA) {
									break;
								}
							}
							if (removeA) {
								// b is subsumed by a ==> we can remove a
								toRemove.add(i);
								break loopj;
							}
						}
						//
					}
				}
			}
			if (!toRemove.isEmpty()) {
				List<AtomicFormula> newBody = new ArrayList<AtomicFormula>();
				for (int i=0; i<rule.getBody().size();i++) {
					AtomicFormula af = rule.getBody().get(i);
					if (!toRemove.contains(i)) {
						newBody.add(af);
					}
				}
				rule = new Rule(rule.getHead(), newBody, rule.getId());
			}
		}
		while (!toRemove.isEmpty());
		
		return rule;
	}
	
	protected static RuleSystem removeRedundantBodyAtoms(RuleSystem ruleSystem) {
		List<Rule> newRules = new ArrayList<Rule>();
		Set<Rule> seenRules = new HashSet<Rule>();
		boolean changed = false;
		for (Rule rule: ruleSystem.getRules()) {
			Rule newRule = removeRedundantBodyAtoms(rule, ruleSystem);
			if (!newRule.equals(rule)) {
				changed = true;
			}
			if (seenRules.add(newRule)) {
				newRules.add(newRule);
			}
		}
		return changed? new RuleSystem(newRules, ruleSystem.getMainHeadFormula(), ruleSystem.distinct) : ruleSystem;
		
		//return ruleSystem;
	}
	/**
	 * Rule r is a subsumed rule iff. there exists another rule r' such that
	 *  1) the head h of r and h' of r' have the same predicate p, 
	 *  2) there is a variable substitution s of variables in h' by corresponding value in h 
	 *  (i.e. if the ith argument of h' is a variable then it is mapped to the ith argument of h) such 
	 *  that s(h') = h 
	 *  3) body(s(r')) is contained in body(r)
	 * @param ruleSystem
	 * @return
	 */
	protected static RuleSystem removeSubsumedRules(RuleSystem ruleSystem) {
		Set<Predicate> preds = ruleSystem.getHeadPredicates();
		List<Rule> newRules = new LinkedList<Rule>();
		Set<Rule> removed = new HashSet<Rule>();
		for (Predicate p: preds) {
			Set<Rule> rulesSet = ruleSystem.getRulesForHead(p);
			List<Rule> rules = new ArrayList<Rule>(rulesSet);
			for (int i=0;i<rules.size();i++) {
				Rule subsumer = rules.get(i);
				if (removed.contains(subsumer)) {
					continue;
				}
				for (int j=0;j<rules.size();j++) {
					Rule subsumee = rules.get(j);
					if (subsumer == subsumee || removed.contains(subsumee)) {
						continue;
					}
					List<AtomicFormula> subsumeeBody = subsumee.getBody();
					List<AtomicFormula> subsumerBody =  replaceByAtomicFormulaRuleDefinition(subsumee.getHead(), subsumer,subsumee.getHead().getAllVariables());

					if (subsumerBody!=null) {
						Set<VariableExpr> unboundVar = new HashSet<VariableExpr>(Rule.getUnboundVariables(subsumee.getHead(), subsumerBody)); // unbound in subsumerBodyList - subsumee,getHead().getAllVariables()
						// variable substitution succeeded 
						//Set<AtomicFormula> subsumerBody = new HashSet<AtomicFormula>(subsumerBodyList);
						boolean allSubsumeesContained = true;
						for (AtomicFormula subsumerAf: subsumerBody) {
							boolean found = false;
							for  (AtomicFormula subsumeeAf: subsumeeBody) {
								if (isSubsumedBy(subsumeeAf,subsumerAf, unboundVar)) {
									found = true;
									break;
								}
							}
							if (!found) {
								allSubsumeesContained = false;
								break;
							}
						}
						if (allSubsumeesContained) {
							removed.add(subsumee);
						}
					}
				}
			}
			rulesSet.removeAll(removed);
			newRules.addAll(rulesSet);
		}
		return new RuleSystem(newRules, ruleSystem.getMainHeadFormula(), ruleSystem.areResultsForMainHeadFormulaDistinct());
		
	}
	
	
	protected static RuleSystem removeUnsatisfiableRulesBasedOnEqualityBetweenDifferentConstants(RuleSystem ruleSystem) {
		List<Rule> newRules = new ArrayList<Rule>();
		boolean changed = false;
		for (Rule rule: ruleSystem.getRules()) {
			if (rule.isUnsatisfiableBasedOnEquilityBetweenDifferentConstants()) {
				changed = true;
			} else {
				newRules.add(rule);
			}
		}
		return changed? new RuleSystem(newRules, ruleSystem.getMainHeadFormula(), ruleSystem.distinct) : ruleSystem;
	}
	
	public RuleSystem simplify(Set<Predicate>  nonSimplifiablePreds) {
		if (getMainHeadFormula()!=null) {
			nonSimplifiablePreds = HashSetFactory.make(nonSimplifiablePreds);
			nonSimplifiablePreds.add(getMainHeadFormula().getPredicate());
		}
		if (head2Rules.keySet().size()<=1) {
			return removeRedundantBodyAtoms(removeUselessOptionalAtomicFormulas(this));
		}
		//Utils.getLogger().info("RuleSystem before simplification:\n"+this);
	
		RuleSystem toSimplify = this;
		RuleSystem simplifiedRS  =toSimplify;
		do {
			toSimplify = removeSubsumedRules(
					removeRedundantBodyAtoms(removeUselessOptionalAtomicFormulas(
					removeUnsatisfiableRulesBasedOnEqualityBetweenDifferentConstants(simplifiedRS))));
			
			// compute all simplifiable predicates
			Set<Predicate> simplifiablePredicates = toSimplify.getSimplifiableHeadPredicates(true, nonSimplifiablePreds);
			assert simplifiablePredicates.size()<=1;
			// toSimplify.clone();
			List<Rule> newRules = new ArrayList<Rule>();
			Set<Rule> alreadyDiscoveredRules = new HashSet<Rule>();
			for (Iterator<Rule> it = toSimplify.getRules().iterator();it.hasNext();) {
				Rule r = it.next();
				if (r.getBody().size()==1 && r.getHead().equals(r.getBody().get(0))) {
					// this  vacuous rule of the form A :- A  is ignored 
				} else if (simplifiablePredicates.contains(r.getHead().getPredicate())) {
					// this rule is ignored
				} else {
					AtomicFormula newHead = r.getHead().clone();
					List<AtomicFormula> newBody = new ArrayList<AtomicFormula>();
					for (Iterator<AtomicFormula> bs = r.getBody().iterator();bs.hasNext();) {
						AtomicFormula f = bs.next();
						if (simplifiablePredicates.contains(f.getPredicate().withoutQualification()) ) {
							List<AtomicFormula> afs = toSimplify.simplifyInBody(f, new HashSet<VariableExpr>(r.getAllRuleVariables()), simplifiablePredicates);
							if (afs!=null) {
								newBody.addAll(afs);
							} else {
								newBody.add(f.clone());
							}
						} else {
							newBody.add(f.clone());
						}
					}
					Rule newRule = new Rule(newHead,newBody,r.getId());
					if (alreadyDiscoveredRules.add(newRule)) {
						newRules.add(newRule);
					}
				}
				
			}
			
			simplifiedRS = new RuleSystem(newRules, toSimplify.getMainHeadFormula(), toSimplify.distinct);
			
		} while (toSimplify.getRules().size()!=simplifiedRS.getRules().size());
		
		return simplifiedRS;		
	}
	
	/**
	 * a head predicate is simplifiable iff. 
	 * <ol>
	 *  <li> it is defined by a single rule r, and </li>
	 *  <li> it does not appear in the body of r, and </li>
	 *  <li>  it is not negated in the rulesystem </li>
	 * 	<li> one of the following must hold 
	 * 		<ul> 
	 * 			<li> it appears at most once as the predicate of an atomic formula af in the body of a rule and af is not negated or optional, or </li>
	 * 		    <li> it is equivalent to true() (i.e. has no arguments), or </li>
	 *  		<li>  the single rule defining it has a single body element </li>
	 *  	</ul>
	 *  </li>
	 *  <li>  it is not in the set of non-simplifiablePredicate	</li>
	 * 
	 * 
	 * @param r
	 * @return
	 */
	protected Set<Predicate>  getSimplifiableHeadPredicates(boolean findOnlyOne, Set<Predicate> nonSimplifiablePreds) {
		Set<Predicate> ret = new  HashSet<Predicate>();
		//<li> it appears at most once as the predicate of an atomic formula af in the body of a rule and af is not negated or optional, or </li>
		//<li> it is equivalent to true() (i.e. has no arguments), or </li>
		Set<Predicate> candidates1 = new HashSet<Predicate>(head2Rules.keySet());
		candidates1.removeAll(nonSimplifiablePreds);
		Map<Predicate, Boolean> alreadyDiscovered2NoQualification = new HashMap<Predicate, Boolean>();
		for (Iterator<Rule> it=rules.iterator();it.hasNext();) {
			Rule r = it.next();
			for (Iterator<AtomicFormula> fs = r.getBody().iterator();fs.hasNext();) {
				AtomicFormula f = fs.next();
				
				Predicate predWithoutQualification = f.getPredicate().withoutQualification();
				boolean qualification = f.getPredicate().isNegated() || f.getPredicate().isOptional(); 
				
				Boolean prevValue;
				if ((prevValue = alreadyDiscovered2NoQualification.get(predWithoutQualification))!=null
					&& predWithoutQualification.getArity() != 0) {
					// already discovered and not equivalent to true()
					candidates1.remove(predWithoutQualification);
					alreadyDiscovered2NoQualification.put(predWithoutQualification, prevValue || qualification);
				}  else {
					alreadyDiscovered2NoQualification.put(predWithoutQualification, qualification);
					if (qualification) {
						// candidate is negated or optional
						candidates1.remove(predWithoutQualification);
					}
				}
				
			}
		}
		//or the single rule defining it has a single body element
		Set<Predicate> candidates2 = new HashSet<Predicate>();
		for (Iterator<Map.Entry<Predicate, Set<Rule>>> it=head2Rules.entrySet().iterator();it.hasNext();) {
			Map.Entry<Predicate, Set<Rule>> e = it.next();
			Predicate cand = e.getKey();
			if (nonSimplifiablePreds.contains(cand)) {
				continue;
			}
			Set<Rule> rules = e.getValue();
			if (rules.size() == 1
				&& rules.iterator().next().getBody().size() == 1) {
				candidates2.add(cand);
			} 
		}
		//
		Set<Predicate> candidates = new HashSet<Predicate>(candidates1);
		candidates.addAll(candidates2);
				
		
		for (Iterator<Map.Entry<Predicate, Set<Rule>>> it=head2Rules.entrySet().iterator();it.hasNext();) {
			Map.Entry<Predicate, Set<Rule>> e = it.next();
			if (!candidates.contains(e.getKey())) {
				continue;
			}
			if (e.getValue().size()>1) {
				continue;
			} else {
				assert e.getValue().size() == 1;
				Rule r = e.getValue().iterator().next();
				Predicate p = e.getKey();
				if (!r.getBody().isEmpty()) {
					boolean pFoundInBody = false;
					for (Iterator<AtomicFormula> fs = r.getBody().iterator();fs.hasNext();) {
						AtomicFormula f = fs.next();
						if (f.getPredicate().equals(p)) {
							pFoundInBody = true;
							break;
						}
					}
					if (!pFoundInBody) {
						ret.add(p);
						if (findOnlyOne) {
							return ret;
						}
					}
				} else {
					if (r.getHead().getArguments().isEmpty() && !r.getHead().getPredicate().isNegated()) {
						// this is a fact equivalent to true();
						ret.add(p);
						if (findOnlyOne) {
							return ret;
						}
					} else {
						//  we do not get rid of non trivial facts 
					}
				}
			}
		}
		
		return ret;
	}
	// may return null
	protected  List<AtomicFormula> simplifyInBody(AtomicFormula f, Set<VariableExpr> variablesInContextOfF, Set<Predicate> simplifiablePredicates) {
		assert simplifiablePredicates.contains(f.getPredicate().withoutQualification()): f+"\n"+simplifiablePredicates;
		Set<Rule> defs = getRulesForHead(f.getPredicate().withoutQualification());
		assert defs.size()==1: f+"\n"+defs;
		Rule def =defs.iterator().next();
		return replaceByAtomicFormulaRuleDefinition(f, def, variablesInContextOfF);
	}
	
	
   // may return null if f and the head of def do not unify
	protected static List<AtomicFormula> replaceByAtomicFormulaRuleDefinition(AtomicFormula f,  Rule def, Set<VariableExpr> variablesInContextOfF) {
		if (f.getPredicate().isNegated() || f.getPredicate().isOptional()) {
			assert def.getBody().size() <= 1 : f +"\n" +def;
		}
		assert f.getPredicate().equals(def.getHead().getPredicate()) : f+"\n"+def;
		
		Map<VariableExpr, Expr> var2Expr = new HashMap<VariableExpr, Expr>();
		for (int i=0;i<f.getArguments().size();i++) {
			Expr e = f.getArguments().get(i);
			Expr var = def.getHead().getArguments().get(i);
			if (var.isVariable()) {// : def+" has not been rectified!";
				Expr old= var2Expr.put((VariableExpr)var,e);
				if (old!=null && !old.equals(e)) {
					return null;
				}
			} else {
				if (!var.equals(e)) {
					return  null;
				}
			}
		}
		List<AtomicFormula> ret = new ArrayList<AtomicFormula>();
		// = contextOfF.getAllRuleVariables();
		Map<VariableExpr, VariableExpr> oldVar2NewVar = new HashMap<VariableExpr, VariableExpr>();
		for (Iterator<AtomicFormula> it= def.getBody().iterator();it.hasNext();) {
			AtomicFormula bf = it.next();
			List<Expr> newArgs = new ArrayList<Expr>();
			for (Iterator<? extends Expr> args=bf.getArguments().iterator();args.hasNext();) {
				Expr arg = args.next();
				if (arg.isConstant()) {
					newArgs.add(arg.clone());
				} else {
					assert arg.isVariable();
					Expr value = var2Expr.get(arg);
					if ( value!=null/* && value.isConstant()*/) {
						newArgs.add(value);
					} else {
						/*if (value != null && !value.isConstant()) {
							arg = value;
						}*/
						VariableExpr var = (VariableExpr) arg;
						if (!variablesInContextOfF.contains(var)) {
							newArgs.add(var); // we can safely add the variable arg
											  // without worrying of it being capture by the rule contextOfF
							//variablesInContextOfF.add((VariableExpr) arg);
							oldVar2NewVar.put(var, var);
						} else {
							VariableExpr newVar = oldVar2NewVar.get(var);
							if (newVar == null) {
								// we need to rename variable arg to avoid 
								int count =2;
								do {
									newVar= new VariableExpr(var.getName()+"_"+count++);
								}
								while ( variablesInContextOfF.contains(newVar));
								oldVar2NewVar.put(var, newVar);
							}							
							newArgs.add(newVar);
							
						}
						
					}
				}
			}
			
			AtomicFormula newBF = new AtomicFormula(
					f.getPredicate().isOptional()? bf.getPredicate().switchOptionalFlag()
					: f.getPredicate().isNegated()? bf.getPredicate().negate(): bf.getPredicate(),
						newArgs);
			if (newBF.getPredicate().getName().equals(Rule.BUILT_IN_EQUAL)
			&& !newBF.getPredicate().isNegated()
			&& !newBF.getPredicate().isOptional()
			&& newBF.getArguments().size() == 2
			&& newBF.getArguments().get(0).equals(newBF.getArguments().get(1))) {
				// equality between two equal terms
				continue;
			}
			/*variablesInContextOfF.addAll(oldVar2NewVar.values());
			if (simplifiablePredicates.contains(bf.getPredicate())) {
				ret.addAll(simplifyInBody(newBF, variablesInContextOfF, simplifiablePredicates));
			} else*/
			{
				ret.add(newBF);
			}
		}
 		return ret;
	}
	
	
	
	
	public  RuleSystem magicSetTransformation(AtomicFormula goal, boolean addInitRule, boolean performedUniqueBindingTransfo, boolean simplify) {
		if (performedUniqueBindingTransfo) {
			RuleSystem ubRules = toUniqueBindingPattern(goal);
			logger.debug("Rules after unique binding pattern transformation:\n{}", ubRules);
			if (isIDB(goal.getPredicate())) {
				PredicateAdornment ad = buildAdormentForGoal(goal);
				UniqueBindingPredicate ubp= UniqueBindingPredicate.createUniqueBindingPredicate(ad);
				goal = new AtomicFormula(ubp, new ArrayList(goal.getArguments()));
			} 
			return ubRules.magicSetTransformation(goal,addInitRule, false, simplify);
		}
		// 
		// IMPORTANT NOTE: do not output formulas without arguments of the form "m_f()"
		// (i.e., magic-sets predicate for a formula without bound variables)
		// and "sup_r_0()" (i.e., supplementary predicate for rule r whose head predicate adornment does not
		// specify any bound variable).
		// 
		
		//Rules for the magic predicates
		List<Rule> newRules = new ArrayList<Rule>();
		for (Iterator<Rule> it=rules.iterator();it.hasNext();) {
			Rule rule= it.next();
			for (int position=0;position<rule.getBody().size();position++) {
				AtomicFormula af = rule.getBody().get(position);
				Predicate pred = af.getPredicate();
				if (pred instanceof UniqueBindingPredicate) {
					UniqueBindingPredicate ubpred= (UniqueBindingPredicate) pred;
					AtomicFormula magicF =  MagicSetPredicate.createMagicSetAtomicFormula(ubpred, af);
					if (magicF.getPredicate().getArity()>0) {
						AtomicFormula supF = SupplementaryPredicate.createSupplementatryAtomicFormula(rule,position);
						assert supF.getPredicate().getArity() > 0 : supF+"\n"+magicF;
						newRules.add(new Rule(magicF,Collections.singletonList(supF),newRules.size()));
					}
				}
			}
		}
		//Rules for the zeroth supplementary predicates
		for (Iterator<Rule> it=rules.iterator();it.hasNext();) {
			Rule rule= it.next();
			AtomicFormula supF = SupplementaryPredicate.createSupplementatryAtomicFormula(rule,0);
			if (supF.getPredicate().getArity()>0) {
				Predicate pred = rule.getHead().getPredicate();
				assert pred instanceof UniqueBindingPredicate;
				UniqueBindingPredicate ubpred = (UniqueBindingPredicate)pred;
				AtomicFormula magicF =  MagicSetPredicate.createMagicSetAtomicFormula(ubpred, rule.getHead());
				assert magicF.getPredicate().getArity()>0 : magicF;
				newRules.add(new Rule(supF,Collections.singletonList(magicF),newRules.size()));
			}
			
		}	
		// Rules for other supplementary predicates
		for (Iterator<Rule> it=rules.iterator();it.hasNext();) {
			Rule rule= it.next();
			for (int position=1;position<rule.getBody().size();position++) {
				AtomicFormula supF2 = SupplementaryPredicate.createSupplementatryAtomicFormula(rule,position);
				//if (supF2.getPredicate().getArity()>0)
				{
					AtomicFormula supF1 = SupplementaryPredicate.createSupplementatryAtomicFormula(rule,position-1);
					List<AtomicFormula> body= new ArrayList<AtomicFormula>(2);
					if (position>1 || supF1.getPredicate().getArity()>0) {
						body.add(supF1);
					}
					body.add(rule.getBody().get(position-1));
					newRules.add(new Rule(supF2,body, newRules.size()));
				}
			}
		}
		//Rule for the IDB predicates
		for (Iterator<Rule> it=rules.iterator();it.hasNext();) {
			Rule rule= it.next();
			if (!rule.getBody().isEmpty()) {
				List<AtomicFormula> optionalFormulas = rule.getOptionalBody();
				if (optionalFormulas.isEmpty()) {
					AtomicFormula lastF = rule.getBody().get(rule.getBody().size()-1);
					AtomicFormula lastSup = SupplementaryPredicate.createSupplementatryAtomicFormula(rule,rule.getBody().size()-1);
					List<AtomicFormula> body = new ArrayList<AtomicFormula>(2);
					if (rule.getBody().size()-1> 0 || lastSup.getPredicate().getArity()>0) {
						body.add(lastSup);
					}
					body.add(lastF);
					newRules.add(new Rule(rule.getHead(),body,newRules.size()));
				} else {
					int pos;
					AtomicFormula lastSup = SupplementaryPredicate.createSupplementatryAtomicFormula(rule,pos = (rule.getLastMandatoryFormulaPosition()+1));
					List<AtomicFormula> body = new ArrayList<AtomicFormula>(2);
					if (pos > 0 ||lastSup.getPredicate().getArity()>0) {
						body.add(lastSup);
					}
					body.addAll(optionalFormulas);
					newRules.add(new Rule(rule.getHead(),body,newRules.size()));
				}
			}
			
		}
		if (addInitRule) {
			//The initialization rule
			if (isIDB(goal.getPredicate())) {
				AtomicFormula magicF = MagicSetPredicate.createMagicSetAtomicFormula( 
						(UniqueBindingPredicate)goal.getPredicate(), goal,true);
				if (magicF.getPredicate().getArity()>0) {
					newRules.add(new Rule(magicF,new ArrayList<AtomicFormula>(0),newRules.size()));
				}
			} 
		}
		Predicate goalPred = goal.getPredicate();
		logger.info("Goal Predicate {}",goalPred);
		
		logger.debug("Magic-sets result before simplification:\n{}",new RuleSystem(newRules));
		RuleSystem ret = simplify? new RuleSystem(newRules).simplify(Collections.singleton(goalPred)):new RuleSystem(newRules);
		//logger.info("Magic-sets result after simplification:\n{}",ret);
		return ret;
		
	}
	
	public Predicate getMagicSetRewrittenPredicateForGoal(AtomicFormula goal) {
		return createUniqueBindingPredicate(buildAdormentForGoal(goal));
		
	}
	public Set<Predicate> getHeadPredicates() {
		return Collections.unmodifiableSet(head2Rules.keySet());
	}
	public Set<Predicate> getHeadPredicatesIn(Rule rule) {
		Set<Predicate> ret= new HashSet<Predicate>(rule.getAllPredicates());
		ret.retainAll(head2Rules.keySet());
		return ret;
	}
	
	public Set<Predicate> getNonHeadPredicates() {
		return Collections.unmodifiableSet(nonHeadPredicates);
	}
	public Set<Predicate> getNonHeadPredicatesIn(Rule rule) {
		Set<Predicate> ret= new HashSet<Predicate>(rule.getAllPredicates());
		ret.retainAll(nonHeadPredicates);
		return ret;
	}
	
	public Set<Predicate> getAllPredicates() {
		Set<Predicate> allpreds = new HashSet<Predicate>(getHeadPredicates());
		allpreds.addAll(getNonHeadPredicates());
		return Collections.unmodifiableSet(allpreds);
	}
	

	/**
	 * returns whether a given predicate corresponds to an
	 * intentional database table (i.e. it is the head of some rule)
	 * @param predicate
	 * @return
	 */
	public boolean isIDB(Predicate predicate) {
		return getHeadPredicates().contains(predicate);
	}
	/**
	 * given an atomic formula, returns its corresponding adornment, which 
	 * indicates bound arguments.
	 * @param goal
	 * @return
	 */
	protected PredicateAdornment buildAdormentForGoal(AtomicFormula goal) {
		List<Integer> boundExprs = new ArrayList<Integer>();
		int pos=0;
		for (Iterator<? extends Expr> it= goal.getArguments().iterator();it.hasNext();pos++) {
			Expr e = it.next();
			if (e.isConstant()) {
				boundExprs.add(pos);
			} else {
				assert e.isVariable();
			}
		}
		return new PredicateAdornment(goal.getPredicate(),boundExprs);
	}
	
	/**
	 * returns the list of rules whose head atomic formula has a given predicate.
	 * @param predicate
	 * @return
	 */
	public Set<Rule> getRulesForHead(Predicate predicate) {
		Set<Rule> ret = head2Rules.get(predicate);
		return ret!=null? ret:Collections.EMPTY_SET;
	}
	/**
	 * returns the list of child {@link Adornment}s of a given {@link PredicateAdornment} in the Rule/Goal Graph .
	 * @param goal
	 * @return
	 */
	protected List<Adornment> computeChildren(PredicateAdornment goal) {
		if (goal.getPredicate().isOptional() ) {
			goal = new PredicateAdornment(goal.getPredicate().switchOptionalFlag(),goal.getBoundArguments());
		} else if (goal.getPredicate().isNegated() ) {
			goal = new PredicateAdornment(goal.getPredicate().negate(),goal.getBoundArguments());
		}
		
		if (!isIDB(goal.getPredicate())){
			return Collections.EMPTY_LIST;
		}
		Set<Rule> rules = getRulesForHead(goal.getPredicate());
		assert !rules.isEmpty() : "an IDB must be the head of at least one rule";
		/*
		 * A goal node that is a IDB predicate p with 
		 * an adornment ad has children corresponding to
		 * all of the rules with head predicate p. 
		 * For such a rule r, then p^ad has child r0^[X1, ..., Xn
		 * | Y1, ..,Ym] where X1,...,Xn are all of the variables that
		 * appear in the argument of r's head that is
		 * bound according to adorment ad, and Y1, ...,Ym
		 * are the other variables of r
		 */
		List<Adornment> ret = new ArrayList<Adornment>();
		int position =0;
		for (Iterator<Rule> it = rules.iterator();it.hasNext();) {
			Rule r = it.next();
			int headArity = r.getHead().getPredicate().getArity();
			assert headArity == r.getHead().getArguments().size();
			Set<VariableExpr> boundVariables = new HashSet<VariableExpr>();
			Set<VariableExpr> freeVariables = new HashSet<VariableExpr>(r.getAllRuleVariables());
			for (int i=0;i<headArity;i++) {
				Expr arg = r.getHead().getArguments().get(i);
				if (arg.isVariable()) {
					if (goal.isBoundArgument(i)) {
						boundVariables.add((VariableExpr)arg);
					}
				} 
			}
			freeVariables.removeAll(boundVariables);
			ret.add(new RuleAdornment(r,boundVariables,freeVariables,position));
			
		}
		return ret;
	}
	/**
	 * returns the list of child {@link Adornment}s of a given {@link RuleAdornment} in the Rule/Goal Graph .
	 * @param goal
	 * @return
	 */
	protected List<Adornment> computeChildren(RuleAdornment ruleAd) {
		Rule rule = ruleAd.getRule();
		List<Adornment> ret = new ArrayList<Adornment>(2);
		Set<VariableExpr> varBoundInRule = ruleAd.getBoundVariables();
		if (ruleAd.getPosition() == 0 && rule.getBody().isEmpty()) {
			return new ArrayList<Adornment>();
		}
		AtomicFormula nextAF = rule.getBody().get(ruleAd.getPosition());
		List<Integer> boundArgs= new ArrayList<Integer>();
		for (int i=0;i<nextAF.getArguments().size();i++) {
			Expr arg = nextAF.getArguments().get(i);
			if (arg.isConstant()) {
				boundArgs.add(i);
			} else {
				assert arg.isVariable();
				VariableExpr var = (VariableExpr) arg;
				if (varBoundInRule.contains(var)) {
					boundArgs.add(i);
				}
			}
		}
		ret.add(new PredicateAdornment(nextAF.getPredicate(),boundArgs));
		RuleAdornment nextRA = ruleAd.next();
		if (nextRA!=null) {
			ret.add(nextRA);
		}
		return ret;
	}
	/**
	 * returns the list of child {@link Adornment}s of a given {@link Adornment} in the Rule/Goal Graph.
	 * @param goal
	 * @return
	 */
	protected List<Adornment> computeChildren(Adornment ad) {
		if (ad.isAdornmentOnPredicate()) {
			return computeChildren(ad.asPredicateAdornment());
		} else {
			assert ad.isAdornmentOnRule();
			return computeChildren(ad.asRuleAdornment());
		}
	}
	
	public RuleSystem add(RuleSystem rs) {
		List<Rule> newRules = new LinkedList<Rule>(rules);
		newRules.addAll(rs.getRules());
		return new RuleSystem(newRules);
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (Iterator<Rule>  it=rules.iterator();it.hasNext();) {
			Rule r = it.next();
			buf.append(r);
			if (it.hasNext()) {
				buf.append(";\n");
			}
		}
		buf.append("\n# Number of rules: "+ rules.size());
		return buf.toString();
	}
	
	public static RuleSystem parse(String s) throws ParseException {
		s=s.trim();
		List<Rule> rules = new ArrayList<Rule>();
		StringTokenizer tok = new StringTokenizer(s,";");
		while (tok.hasMoreTokens()) {
			String ruleToken = tok.nextToken();
			rules.add(Rule.parse(ruleToken,rules.size()));
		}
		return new RuleSystem(rules);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (distinct ? 1231 : 1237);
		result = prime * result
				+ ((mainHeadFormula == null) ? 0 : mainHeadFormula.hashCode());
		result = prime * result + ((rules == null) ? 0 : rules.hashCode());
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
		RuleSystem other = (RuleSystem) obj;
		if (distinct != other.distinct)
			return false;
		if (mainHeadFormula == null) {
			if (other.mainHeadFormula != null)
				return false;
		} else if (!mainHeadFormula.equals(other.mainHeadFormula))
			return false;
		if (rules == null) {
			if (other.rules != null)
				return false;
		} else if (!rules.equals(other.rules))
			return false;
		return true;
	}
	
	
}
