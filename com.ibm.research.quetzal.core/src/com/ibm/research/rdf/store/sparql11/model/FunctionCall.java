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
 package com.ibm.research.rdf.store.sparql11.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ibm.research.rdf.store.runtime.service.types.TypeMap;

/**
 * models a function call
 */
public class FunctionCall {
	public static final String STARTS_WITH = "http://www.w3.org/2005/xpath-functions#starts-with";
	public static final String ENDS_WITH = "http://www.w3.org/2005/xpath-functions#ends-with";
	public static final String XSD_DATETIME = "http://www.w3.org/2001/XMLSchema#dateTime";
	public static final String XSD_BOOLEAN = "http://www.w3.org/2001/XMLSchema#boolean";
	public static final String XSD_DOUBLE = "http://www.w3.org/2001/XMLSchema#double";
	public static final String XSD_FLOAT = "http://www.w3.org/2001/XMLSchema#float";
	public static final String XSD_DECIMAL = "http://www.w3.org/2001/XMLSchema#decimal";
	public static final String XSD_INTEGER = "http://www.w3.org/2001/XMLSchema#integer";
	public static final String XSD_STRING = "http://www.w3.org/2001/XMLSchema#string";
	
	private IRI function;
	private List<Expression> arguments = new ArrayList<Expression>();

	public FunctionCall(IRI f) {
		function = f;
	}
	
	public FunctionCall(IRI f, Collection<? extends Expression> args) {
		this(f);
		arguments.addAll(args);
	}
	
	public FunctionCall(IRI f, Expression... args) {
		this(f);
		for(int i = 0; i < args.length; i++)
			arguments.add(args[i]);
	}

	public IRI getFunction() {
		return function;
	}

	public List<Expression> getArguments() {
		return arguments;
	}
	
	public void addArguments(List<Expression> args) {
		arguments.addAll(args);
	}
	
	public short getReturnType() {
		short returnType;
		if (function.getValue().equals(STARTS_WITH)) {
			returnType = TypeMap.BOOLEAN_ID;
		} else if (function.getValue().equals(ENDS_WITH)) {
			returnType = TypeMap.BOOLEAN_ID;
		} else if (function.getValue().equals(XSD_DATETIME) || (function.getValue().equals("xsd:dateTime"))) {
			returnType = TypeMap.DATETIME_ID;
		} else if (function.getValue().equals(XSD_BOOLEAN)|| (function.getValue().equals("xsd:boolean"))) {
			returnType = TypeMap.BOOLEAN_ID;
		} else if (function.getValue().equals(XSD_DOUBLE)|| (function.getValue().equals("xsd:double"))) {
			returnType = TypeMap.DOUBLE_ID;
		} else if (function.getValue().equals(XSD_FLOAT)|| (function.getValue().equals("xsd:float"))) {
			returnType = TypeMap.FLOAT_ID;
		} else if (function.getValue().equals(XSD_DECIMAL)|| (function.getValue().equals("xsd:decimal"))) {
			returnType = TypeMap.DECIMAL_ID;
		} else if (function.getValue().equals(XSD_INTEGER)|| (function.getValue().equals("xsd:integer"))) {
			returnType = TypeMap.INTEGER_ID;
		} else if (function.getValue().equals(XSD_STRING)|| (function.getValue().equals("xsd:string"))) {
			returnType = TypeMap.STRING_ID;
		} else {
			returnType = TypeMap.NONE_ID;
		}
		return returnType;
	}
	
	public boolean hasCast(Variable v) {
		boolean containsV=false;
		for(Expression e: arguments){
			if(e.gatherVariables().contains(v))
				containsV=true;
		}
		if(!containsV)return false;

		if (function.getValue().equals(XSD_DATETIME)) {
			return true;
		} else if (function.getValue().equals(XSD_BOOLEAN)) {
			return true;
		} else if (function.getValue().equals(XSD_DOUBLE)) {
			return true;
		} else if (function.getValue().equals(XSD_FLOAT)) {
			return true;
		} else if (function.getValue().equals(XSD_DECIMAL)) {
			return true;
		} else if (function.getValue().equals(XSD_INTEGER)) {
			return true;
		} else if (function.getValue().equals("xsd:string")) {
			return true;
		} else {
			return false;
		}
	}
	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("<"+function + ">(");
		for(int i = 0; i < arguments.size(); i++) {
			if(i > 0) sb.append(", ");
			sb.append(arguments.get(i));
		}
		sb.append(")");
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((arguments == null) ? 0 : arguments.hashCode());
		result = prime * result
				+ ((function == null) ? 0 : function.hashCode());
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
		FunctionCall other = (FunctionCall) obj;
		if (arguments == null) {
			if (other.arguments != null)
				return false;
		} else if (!arguments.equals(other.arguments))
			return false;
		if (function == null) {
			if (other.function != null)
				return false;
		} else if (!function.equals(other.function))
			return false;
		return true;
	}
	
	
}
