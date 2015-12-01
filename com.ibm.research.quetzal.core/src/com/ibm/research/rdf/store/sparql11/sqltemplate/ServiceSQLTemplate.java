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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.research.rdf.store.Context;
import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.runtime.service.types.TypeMap;
import com.ibm.research.rdf.store.sparql11.model.BindFunctionCall;
import com.ibm.research.rdf.store.sparql11.model.BindFunctionPattern;
import com.ibm.research.rdf.store.sparql11.model.IRI;
import com.ibm.research.rdf.store.sparql11.model.Pattern;
import com.ibm.research.rdf.store.sparql11.model.ServiceFunction;
import com.ibm.research.rdf.store.sparql11.model.ServicePattern;
import com.ibm.research.rdf.store.sparql11.model.Variable;
import com.ibm.research.rdf.store.sparql11.planner.PlanNode;

public class ServiceSQLTemplate extends HttpSQLTemplate {
	public ServiceSQLTemplate(List<String> templateName, PlanNode planNode, Store store, Context ctx,
			STPlanWrapper wrapper) {
		super(templateName, store, ctx, wrapper, planNode);
	}

	@Override
	Map<String, SQLMapping> populateMappings() {
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

		if (sp instanceof ServicePattern) {
			names.add("sparql");
			iris.add("http://www.w3.org/2005/sparql-results#");

			// this supports the regular SPARQL service call right now
			assert ((ServicePattern) sp).getService().isIRI();
			String queryText = ((ServicePattern) sp).getQueryText();
			mappings.put("queryText", new SQLMapping("queryText", "&query=" + queryText, null));
			String service = ((ServicePattern) sp).getService().getIRI().toString();
			mappings.put("service", new SQLMapping("service", service, null));
			mappings.put("xPathForRows", new SQLMapping("xPathForRows", "//sparql:result", null));
			Set<Variable> producedVars = planNode.getProducedVariables();
			for (Variable v : producedVars) {
				xPathForCols.add("xs:string(./sparql:binding[./@name=\"" + v.getName() + "\"])");
			}
			for (Variable v : getAllLiteralVars(producedVars)) {				
				xPathForColTypes.add("xs:string(./sparql:binding[./@name=\"" + v.getName() + "\"]//@datatype)");
			}
		} else {
			// this supports extensions to service which allow a GET/POST with parameters from an input table, in which case the GET
			// or POST work row by row, or a POST ALL which means the contents of the entire table get posted over.
			BindFunctionCall bfp = ((BindFunctionPattern) sp).getFuncCall();
			IRI f = bfp.getIri();
			mappings.put("service", new SQLMapping("service", f.toString(), null));
			ServiceFunction sf = ((ServiceFunction) bfp.getFunction());
			mappings.put("xPathForRows", new SQLMapping("xPathForRows", sf.rowXPath(), null));
			Iterator<String> it = sf.columns().iterator();
			while (it.hasNext()) {
				xPathForCols.add(it.next());
	
				xPathForColTypes.add(it.next());
			}
			List<String> inputCols = new LinkedList<String>();
			if (sf.service().isVariable()) {
				inputCols.add(sf.service().getVariable().getName());
			} else {
				inputCols.add("url");
			}
			for (Variable v : sf.getInVariables()) {
				inputCols.add(v.getName());
			}
			mappings.put("inputCols", new SQLMapping("inputCols", inputCols, null));
		}

		mappings.put("xPathForCols", new SQLMapping("xPathForCols", xPathForCols, null));
		mappings.put("xPathForColTypes", new SQLMapping("xPathForColTypes", xPathForColTypes, null));

		mappings.put("ns", new SQLMapping("ns", names, null));
		mappings.put("iris", new SQLMapping("iris", iris, null));

		if (planNode.isPost()) {
			setupPostData(mappings);
			mappings.put("htmlHeader", new SQLMapping("htmlHeader", "", null));
			mappings.put("httpMethod",new SQLMapping("httpMethod", "POST", null));
		} else {
			mappings.put("httpMethod",new SQLMapping("httpMethod", "GET", null));
		}
		return mappings;
	}
	

	private void setupPostData(Map<String, SQLMapping> mappings) {
		@SuppressWarnings("unchecked")
		List<String> cols = (List<String>) mappings.get("outputColumns").values;
		cols.add("index");
		mappings.put("outputColumns", new SQLMapping("outputColumns", cols, null));

		List<String> indexColumns = new LinkedList<String>();
		List<String> postedTypes = new LinkedList<String>();
		for (Variable v : planNode.getRequiredVariables()) {
			indexColumns.add(v.getName());
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
		mappings.put("indexColumns", new SQLMapping("indexColumns", indexColumns, null));
		mappings.put("postTypes", new SQLMapping("postTypes", postedTypes, null));
	}
}
