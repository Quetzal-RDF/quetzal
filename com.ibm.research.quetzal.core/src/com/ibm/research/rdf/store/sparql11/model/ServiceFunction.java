package com.ibm.research.rdf.store.sparql11.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ibm.wala.util.collections.HashMapFactory;

public class ServiceFunction extends FunctionBase {

	private QueryTripleTerm service;
	private Map<String,Object> parameters = HashMapFactory.make();
	private String rowXPath;
	private List<String> colXPaths = new ArrayList<String>();
	
	public void setService(QueryTripleTerm service) {
		this.service = service;
	}
	
	public QueryTripleTerm service() {
		return service;
	}
	
	public <T> void addServiceParam(String name, T value) {
		parameters.put(name, value);
	}
	
	public Iterable<Map.Entry<String,Object>> parameters() {
		return parameters.entrySet();
	}
	
	public void setServiceRowXPath(String rowPath) {
		rowXPath = rowPath;
	}

	public String rowXPath() {
		return rowXPath;
	}
	
	public void addServiceColumnXPath(String colPath) {
		colXPaths.add(colPath);
	}

	public Iterable<String> columns() {
		return colXPaths;
	}
}
