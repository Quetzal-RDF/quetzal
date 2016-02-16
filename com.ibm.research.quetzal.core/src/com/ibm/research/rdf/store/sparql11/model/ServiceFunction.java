package com.ibm.research.rdf.store.sparql11.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ibm.wala.util.collections.HashMapFactory;

public class ServiceFunction extends FunctionBase {

	private boolean tableFunction;
	private Expression service;
	private Map<String,Object> parameters = HashMapFactory.make();
	private String tableParameter;
	private String rowXPath;
	private List<String> colXPaths = new ArrayList<String>();
	
	public String toString() {
		return "SERVICE " + service() + " " + parameters;
	}
		
	public String tableParam() {
		return tableParameter;
	}

	public void setTableParam(String tableParameter) {
		this.tableParameter = tableParameter;
	}

	public boolean tableFunction() {
		return tableFunction;
	}

	public void setTableFunction(boolean tableFunction) {
		this.tableFunction = tableFunction;
	}

	public void setService(Expression service) {
		this.service = service;
	}
	
	public Expression service() {
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

	private static boolean isQuoted(String s) {
		return s.startsWith("\"");
	}
	
	private static String trim(String x) {
		 return x.substring(1, x.length()-1);
	}
	
	private String maybeTrim(String x) {
		return isQuoted(x)? trim(x): x;
	}
	public String rowXPath() {
		return maybeTrim(rowXPath);
	}
	
	public void addServiceColumnXPath(String colPath) {
		colXPaths.add(colPath);
	}

	public Iterable<String> columns() {
		return colXPaths.stream().map((String x) -> maybeTrim(x)).collect(Collectors.toList());
	}
}
