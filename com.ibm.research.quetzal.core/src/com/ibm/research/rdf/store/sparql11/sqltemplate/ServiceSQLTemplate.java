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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ibm.research.rdf.store.Context;
import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.runtime.service.types.TypeMap;
import com.ibm.research.rdf.store.sparql11.model.BindFunctionCall;
import com.ibm.research.rdf.store.sparql11.model.BindFunctionPattern;
import com.ibm.research.rdf.store.sparql11.model.FunctionExt;
import com.ibm.research.rdf.store.sparql11.model.IRI;
import com.ibm.research.rdf.store.sparql11.model.Pattern;
import com.ibm.research.rdf.store.sparql11.model.ServicePattern;
import com.ibm.research.rdf.store.sparql11.model.Variable;
import com.ibm.research.rdf.store.sparql11.planner.PlanNode;

public class ServiceSQLTemplate extends HttpSQLTemplate {
	public ServiceSQLTemplate(List<String> templateName, PlanNode planNode,
			Store store, Context ctx, STPlanWrapper wrapper) {
		super(templateName, store, ctx, wrapper, planNode);
	}

	@Override
	Map<String, SQLMapping> populateMappings() {	
		// wrapper.getQuery().getPrologue().getPrefixes();
		Map<String, SQLMapping> mappings = super.populateMappings();

		Pattern sp = planNode.getPattern();

		if (!planNode.isPost()) {
			assert sp instanceof ServicePattern;
			assert ((ServicePattern)sp).getService().isIRI();
			String queryText = ((ServicePattern)sp).getQueryText();
			mappings.put("queryText", new SQLMapping("queryText", "&query=" + queryText, null));
			String service = ((ServicePattern)sp).getService().getIRI().toString();
			mappings.put("service", new SQLMapping("service", service, null));
		} else {
			BindFunctionCall bfp = ((BindFunctionPattern)sp).getFuncCall();
			IRI f = bfp.getIri();
			mappings.put("service", new SQLMapping("service", f.toString(), null));
			try {
				String body = ((FunctionExt)bfp.getFunction()).getBody().getStringBody();
				mappings.put("queryText", new SQLMapping("queryText", "funcName=" + URLEncoder.encode(f.getValue().substring(f.getValue().indexOf('#')+1), "UTF-8") + "&funcBody=" + URLEncoder.encode(body, "UTF-8") + "&funcData=", null));
				//mappings.add(new SQLMapping("functionBody", body, null));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}			
		}
		
		if (planNode.isPost()) {
			setupPostData(mappings);
			
			mappings.put("htmlHeader", new SQLMapping("htmlHeader", "", null));
		} 
		
		return mappings;
	}

	private void setupPostData(Map<String, SQLMapping> mappings) {
		@SuppressWarnings("unchecked")
		List<String> cols = (List<String>) mappings.get("cols").values;
		cols.add("index" );
		mappings.put("cols", new SQLMapping("cols", cols, null));

		List<String> indexColumns = new LinkedList<String>();
		List<String> postedColumns = new LinkedList<String>();
		List<String> postedTypes = new LinkedList<String>();
		for (Variable v : planNode.getRequiredVariables()) {
			indexColumns.add(v.getName());
			postedColumns.add(v.getName());
			if (wrapper.getIRIBoundVariables().contains(v) ) {
				postedTypes.add("'xs:string'");
			} else {
				indexColumns.add(v.getName() + "_TYP");
				postedTypes.add("(case when " + v.getName() + "_TYP between " + TypeMap.DATATYPE_DECIMAL_IDS_START + " and " + TypeMap.DATATYPE_NUMERICS_IDS_END + " then 'xs:decimal' " 
						      + "when " + v.getName() + "_TYP between " + TypeMap.DATATYPE_NUMERICS_IDS_START + " and " + TypeMap.DATATYPE_NUMERICS_IDS_END + " then 'xs:integer' "
						      + "else 'xs:string' end)");
			}
		}
		mappings.put("indexColumns", new SQLMapping("indexColumns", indexColumns, null));
		mappings.put("postColumns", new SQLMapping("postColumns", postedColumns, null));
		mappings.put("postTypes", new SQLMapping("postTypes", postedTypes, null));
	}
}
