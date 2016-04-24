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
package com.ibm.research.rdf.store.sparql11.sqltemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.ibm.research.rdf.store.Context;
import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.runtime.service.types.TypeMap;
import com.ibm.research.rdf.store.sparql11.model.BindFunctionCall;
import com.ibm.research.rdf.store.sparql11.model.BindFunctionPattern;
import com.ibm.research.rdf.store.sparql11.model.Expression;
import com.ibm.research.rdf.store.sparql11.model.IRI;
import com.ibm.research.rdf.store.sparql11.model.Pattern;
import com.ibm.research.rdf.store.sparql11.model.QueryPrologue;
import com.ibm.research.rdf.store.sparql11.model.ServiceFunction;
import com.ibm.research.rdf.store.sparql11.model.ServicePattern;
import com.ibm.research.rdf.store.sparql11.model.Variable;
import com.ibm.research.rdf.store.sparql11.planner.PlanNode;
import com.ibm.research.rdf.store.sparql11.sqlwriter.FilterContext;
import com.ibm.research.rdf.store.sparql11.sqlwriter.SQLWriterException;
import com.ibm.wala.util.collections.HashMapFactory;
import com.ibm.wala.util.collections.Pair;

public class ServiceSQLTemplate extends HttpSQLTemplate {
	public ServiceSQLTemplate(List<String> templateName, PlanNode planNode, Store store, Context ctx,
			STPlanWrapper wrapper) {
		super(templateName, store, ctx, wrapper, planNode);
	}

	@Override
	Map<String, SQLMapping> populateMappings() throws SQLWriterException {

		Map<String, SQLMapping> mappings = super.populateMappings();

		List<String> names = new ArrayList<String>();
		List<String> iris = new ArrayList<String>();
		for (Map.Entry<String, IRI> prefix : wrapper.getQuery().getPrologue().getPrefixes().entrySet()) {
			if (! "".equals(prefix.getKey())) {
				names.add(prefix.getKey());
				iris.add(prefix.getValue().getValue());
			}
		}

		Pattern sp = planNode.getPattern();
		LinkedList<String> xPathForCols = new LinkedList<String>();
		LinkedList<String> xPathForColTypes = new LinkedList<String>();

		varMap = HashMapFactory.make();

		if (! planNode.getRequiredVariables().isEmpty()) {
			BindFunctionCall bfp = ((BindFunctionPattern) sp).getFuncCall();
			Iterator<Variable> fps = ((ServiceFunction)bfp.getFunction()).getInVariables().iterator();
			PlanNode pred = planNode.getPredecessor(wrapper.plan);
			
			if (pred == null) {
				System.err.println(planNode.getPredecessor(wrapper.plan));
			}
			
			String predCte = wrapper.getPlanNodeCTE(pred, false);
			for(Variable v : planNode.getRequiredVariables()){
				String vPredName = wrapper.getPlanNodeVarMapping(pred,v.getName());
				//projectMapping.add(predCte+"."+vPredName+" AS "+v.getName());
				varMap.put(fps.next().getName(), Pair.make(predCte+"."+vPredName, null));
			}
		}
		

		{	
			for(Variable v : planNode.getProducedVariables()){
				if (store.getStoreBackend() != Store.Backend.shark) {
					varMap.put(v.getName(), Pair.make(v.getName(), "typecode(" + v.getName() + "_TYP)"));
				} else {
					varMap.put(v.getName(), Pair.make(v.getName(), v.getName() + "_TYP"));
					
				}
			}
		}
		List<String> inputCols = new LinkedList<String>();
		List<String> outputCols = new LinkedList<String>();

		
		if (sp instanceof ServicePattern) {
			names.add("s");
			iris.add("http://www.w3.org/2005/sparql-results#");
			
			names.add("xs");
			iris.add("http://www.w3.org/2001/XMLSchema");

			// this supports the regular SPARQL service call right now
			assert ((ServicePattern) sp).getService().isIRI();
			String queryText = ((ServicePattern) sp).getQueryText();
			
			String queryStr = queryText != null? "?query=" + queryText: "";
			mappings.put("queryText", new SQLMapping("queryText", queryStr, null));
			String service = ((ServicePattern) sp).getService().getIRI().toString() + queryStr;

			mappings.put("service", new SQLMapping("service", "'" + service + "'", null));
			mappings.put("xPathForRows", new SQLMapping("xPathForRows", "//s:result", null));
			inputCols.add("url");
			Set<Variable> producedVars = planNode.getProducedVariables();
			List<Variable> literals = getAllLiteralVars(producedVars);

			for (Variable v : producedVars) {
				xPathForCols.add("xs:string(./s:binding[./@name=\"" + v.getName() + "\"])");
			}
			for (Variable v : getAllLiteralVars(producedVars)) {				
				xPathForColTypes.add("xs:string(./s:binding[./@name=\"" + v.getName() + "\"]//@datatype)");
			}
			
			mappings.put(
				"htmlHeader", 
				new SQLMapping(
					"htmlHeader", 
					"<httpHeader><header name=\"Accept\" value=\"application/sparql-results+xml\"/></httpHeader>", null));
		} else {
			// this supports extensions to service which allow a GET/POST with parameters from an input table, in which case the GET
			// or POST work row by row, or a POST ALL which means the contents of the entire table get posted over.
			BindFunctionCall bfp = ((BindFunctionPattern) sp).getFuncCall();
			String service;
			
			ServiceFunction serviceFunction = (ServiceFunction)bfp.getFunction();
			if (bfp.getFunction() instanceof ServiceFunction) {
				try {
					boolean amp = false;
					Expression svc = serviceFunction.service();
			
					if (serviceFunction.tableParam() != null) {
						mappings.put("queryText", new SQLMapping("queryText", serviceFunction.tableParam().replaceAll("\"", "") + "=", ""));
					}
					
					FilterContext context = new FilterContext(varMap,  wrapper.getPropertyValueTypes(), planNode);
					service = expGenerator.getSQLExpression(svc,context, store);

					Map<String,String> ns = HashMapFactory.make();
					QueryPrologue qp = wrapper.getQuery().getPrologue();
					for(String key: qp.getPrefixes().keySet()) 
						ns.put(key, qp.getPrefixes().get(key).getValue());
					String base = (qp.getBase() == null) ? null : qp.getBase().getValue();
					svc.renamePrefixes(base, ns, Collections.<String,String>emptyMap());

					boolean useConcat = false;
					boolean concat = false;

					if (store.getStoreBackend()==Store.Backend.shark) {
						useConcat = true;
						concat = false;
					}
				
					for(Entry<String,Object> p : serviceFunction.parameters()) {
						if (p.getValue() instanceof Expression) {
							concat = true;
							FilterContext context1 = new FilterContext(varMap,  wrapper.getPropertyValueTypes(), planNode);
							String eSql = expGenerator.getSQLExpression((Expression)p.getValue(),context1, store);
							if (!amp) {
								amp = true;
								service += (useConcat? ",": "||") + "'?";
							} else {
								service += (useConcat? ",": "||") + "'&";
							}
							service += p.getKey().replaceAll("\"", "");
							service += "='" + (useConcat? ",": "||") + "DB2XML.URLENCODE(" + eSql + ", NULL)";
						}
					}
					
					if (concat && useConcat) {
						service = "concat(" + service + ")";
					}
					
				} catch (SQLWriterException e) {
					// TODO Auto-generated catch block
					service = bfp.getIri().toString();
					e.printStackTrace();
				}				
			} else {
				service = bfp.getIri().toString();
			}
			
			mappings.put("service", new SQLMapping("service", service, null));
			ServiceFunction sf = serviceFunction;
			mappings.put("xPathForRows", new SQLMapping("xPathForRows", sf.rowXPath(), null));
			Iterator<String> it = sf.columns().iterator();
			while (it.hasNext()) {
				xPathForCols.add(it.next());
	
				xPathForColTypes.add(it.next());
			}
		//	Set<Variable> vs = sf.service().gatherVariables();
			Set<Variable> vs = bfp.gatherVariables();

			if (!sf.tableFunction()) {
				inputCols.add("url");
				addPredecessorVariables(inputCols, true);
			}
					
		}
		for (Variable v: planNode.getProducedVariables()) {
			outputCols.add(v.getName());
		}
		addPredecessorVariables(outputCols, false);

		mappings.put("inputCols", new SQLMapping("inputCols", inputCols, null));
		mappings.put("outputCols", new SQLMapping("outputCols", outputCols, null));

		mappings.put("xPathForCols", new SQLMapping("xPathForCols", xPathForCols, null));
		mappings.put("xPathForColTypes", new SQLMapping("xPathForColTypes", xPathForColTypes, null));

		mappings.put("ns", new SQLMapping("ns", names, null));
		mappings.put("iris", new SQLMapping("iris", iris, null));

		if (planNode.isPost()) {
			setupPostData(mappings);
			mappings.put("httpMethod",new SQLMapping("httpMethod", "POST", null));
		} else {
			mappings.put("httpMethod",new SQLMapping("httpMethod", "GET", null));
		}

		List<String> filterConstraint = getFilterSQLConstraint();
		SQLMapping filterMapping = new SQLMapping("filter_constraint", filterConstraint,null);
		mappings.put("filter_constraint", filterMapping);
		
		if (! mappings.containsKey("htmlHeader")) {
			mappings.put("htmlHeader", new SQLMapping("htmlHeader", "", null));
		}
		
		return mappings;
	}
	

	private void setupPostData(Map<String, SQLMapping> mappings) {
		@SuppressWarnings("unchecked")
		List<String> cols = (List<String>) mappings.get("outputColumns").values;
		mappings.put("outputColumns", new SQLMapping("outputColumns", cols, null));

		List<String> indexColumns = new LinkedList<String>();
		List<String> postedColumns = new LinkedList<String>();
		List<String> postedTypes = new LinkedList<String>();
		for (Variable v : planNode.getRequiredVariables()) {
			indexColumns.add(v.getName());
			postedColumns.add(v.getName());
			if (wrapper.getIRIBoundVariables().contains(v)) {
				postedTypes.add("'xs:string'");
			} else {
				indexColumns.add(v.getName() + "_TYP");
				postedTypes.add("(case when " + v.getName() + "_TYP between " + TypeMap.DATATYPE_DECIMAL_IDS_START
						+ " and " + TypeMap.DATATYPE_NUMERICS_IDS_END + " then 'xs:decimal' " + "when " + v.getName()
						+ "_TYP between " + TypeMap.DATATYPE_NUMERICS_IDS_START + " and "
						+ TypeMap.DATATYPE_NUMERICS_IDS_END + " then 'xs:integer' " + "else 'xs:string' end)");
			}
		}
		addPredecessorVariables(indexColumns, true);
		mappings.put("indexColumns", new SQLMapping("indexColumns", indexColumns, null));
		mappings.put("postedColumns", new SQLMapping("postedColumns", postedColumns, null));
		mappings.put("postTypes", new SQLMapping("postTypes", postedTypes, null));
		
		List<String> paramNames = new ArrayList<String>();
		for(Variable p : ((ServiceFunction)((BindFunctionPattern)planNode.getPattern()).getFuncCall().getFunction()).getInVariables()) {
			paramNames.add(p.getName());
		}
		mappings.put("paramNames",  new SQLMapping("paramNames", paramNames, null));
	}
}
