package com.ibm.research.sparql.rewriter;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.topbraid.spin.system.SPINModuleRegistry;
import org.topbraid.spin.util.CommandWrapper;
import org.topbraid.spin.util.SPINQueryFinder;
import org.topbraid.spin.vocabulary.SP;
import org.topbraid.spin.vocabulary.SPIN;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDFS;

public class SPINRuleParser {
		
	public static List<RuleforJena> parseRules(File ruleFile) {
		// Initialize system functions and templates
		SPINModuleRegistry.get().init();
		List<RuleforJena> l = new LinkedList<RuleforJena>();
		
		// Load main file
		Model baseModel = ModelFactory.createDefaultModel();
		baseModel.read(ruleFile.getAbsolutePath());
        Map<Resource,List<CommandWrapper>> cls2Query = SPINQueryFinder.getClass2QueryMap(baseModel, baseModel, SPIN.rule, true, false);
        l.addAll(extractRules(baseModel, cls2Query));
        
        cls2Query = SPINQueryFinder.getClass2QueryMap(baseModel, baseModel, SPIN.body, true, false);
        l.addAll(extractRules(baseModel, cls2Query));
        return l;
	}

	private static List<RuleforJena> extractRules(Model baseModel,
			Map<Resource, List<CommandWrapper>> cls2Query) {
		List<RuleforJena> l = new LinkedList<RuleforJena>();
	
		for (Resource r : cls2Query.keySet()) {
        	RuleforJena rule;
        	
        	for (CommandWrapper q : cls2Query.get(r)) {
          		StmtIterator stmt = q.getStatement().getObject().asResource().listProperties(baseModel.getProperty(SP.text.toString()));
          		assert stmt.hasNext();
          	
           		while (stmt.hasNext()) {
        			Query query = SPARQLRewriterForJena.parseToJenaQuery(stmt.next().getObject().toString());
        			StmtIterator stmt2 = q.getStatement().getObject().asResource().listProperties(baseModel.getProperty("http://example.org/id"));
        			int id = -1;
            		while (stmt2.hasNext()) {		
            			id = stmt2.next().getObject().asLiteral().getInt();
            		}
        			rule = new RuleforJena(query, id);		
               		stmt2 = q.getStatement().getObject().asResource().listProperties(baseModel.getProperty(RDFS.label.toString()));
            		while (stmt2.hasNext()) {		
            			rule.setDescription(stmt2.next().getObject().toString());
            		}
            		
              		stmt2 = q.getStatement().getObject().asResource().listProperties(baseModel.getProperty("http://example.org/displayText"));
              		
             		while (stmt2.hasNext()) {		
            			rule.setDisplayText(stmt2.next().getObject().toString());
            		}
            		l.add(rule);
            		rule.setLabel(r.getLocalName());
        		}    		
 
        	}
        }
		return l;
	}
}
