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

import java.math.BigDecimal;

import com.ibm.research.rdf.store.runtime.service.types.TypeMap;
import com.ibm.research.rdf.store.sparql11.model.Expression.EConstantType;

/**
 * A single literal.
 */
public class Constant {
	private final FourUnion<StringLiteral, IRI, Number, Boolean> value = new FourUnion<StringLiteral, IRI, Number, Boolean>();
	private final EConstantType constType;
	private String strValue;  // user provided string for Number
	
	public Constant(StringLiteral literal) {
		constType = EConstantType.LITERAL;
		value.setFirst(literal);
	}
	
	public Constant(IRI i) {
		constType = EConstantType.IRI;
		value.setSecond(i);		
	}
	
	public Constant(String s, Number d) {
		strValue = s;
		if (d instanceof BigDecimal) {
			constType = EConstantType.DECIMAL;
		}
		else if (d instanceof Double) {
			constType = EConstantType.DOUBLE;
		} else {
			constType = EConstantType.INTEGER;
		}
		value.setThird(d);
	}

	public Constant(Boolean b) {
		constType = EConstantType.BOOL;
		value.setFourth(b);
	}
	
	public Constant(Number d) {
		if (d instanceof BigDecimal) {
			constType = EConstantType.DECIMAL;
		}
		else if (d instanceof Double) {
			constType = EConstantType.DOUBLE;
		}
		else {
			constType = EConstantType.INTEGER;
		}
		value.setThird(d);
	}
	
	public void reverse() {
	}

	public EConstantType getConstType() { return constType; }
	
	public StringLiteral getLiteral() { return value.getFirst(); }
	public IRI getIRI() { return value.getSecond(); }
	public Number getNumber() { return value.getThird(); }
	public Boolean getBoolean() { return value.getFourth(); }

	public String toString() { 
		switch(constType) {
		case BOOL: return getBoolean().toString();
		case INTEGER: return strValue; //getNumber().toString();
		case DECIMAL: return strValue; //getNumber().toString();
		case DOUBLE: return strValue; //getNumber().toString();
		case IRI: return "<"+getIRI().toString() +">";
		case LITERAL: return getLiteral().toString(); 
		default: return "";
		}
	}
	
	public String toTypedString() { 
		
		switch(constType) {
		case BOOL: 
			return "\"" + getBoolean().toString()+"\"^^"+
					"http://www.w3.org/2001/XMLSchema#boolean";
		case INTEGER: 
			return "\"" + getNumber().toString()+"\"^^"+
					"http://www.w3.org/2001/XMLSchema#integer";
		case DECIMAL: 
			return "\"" + getNumber().toString()+"\"^^"+
					"http://www.w3.org/2001/XMLSchema#decimal";
		case DOUBLE: 
			return "\"" + getNumber().toString()+"\"^^"+
					"http://www.w3.org/2001/XMLSchema#double";
		case IRI: 
			return getIRI().toString();
		case LITERAL: 
			return getLiteral().toTypedString(); 
		default: return "";
		}
	}
	
	public short toDataType() { 
		
		switch(constType) {
		case BOOL: 
			return TypeMap.BOOLEAN_ID;
		case INTEGER: 
			return TypeMap.INTEGER_ID;
		case DECIMAL: 
			return TypeMap.DECIMAL_ID;
		case DOUBLE: 
			return TypeMap.DOUBLE_ID;
		case IRI: 
			return TypeMap.IRI_ID;
		case LITERAL: 
			IRI literalType=getLiteral().getType();
			if(literalType==null){
				String language=getLiteral().getLanguage();				
				if(language==null){
					short lang=TypeMap.getLanguageType(getLiteral().getValue());
					if(TypeMap.isNewLang(lang))
						return TypeMap.SIMPLE_LITERAL_ID;
					else return lang;
				}
				else
					return TypeMap.getLanguageType(language);
			}
			else return TypeMap.getDatatypeType(literalType.getValue());
		default: return TypeMap.NONE_ID;
		}
	}
	
	public String toDataString() { 
		switch(constType) {
		case BOOL: 
			return getBoolean().toString();
		case INTEGER: 
			return strValue != null? strValue: getNumber().toString();
		case DECIMAL: 
			return strValue != null? strValue: ""+((BigDecimal)getNumber()).doubleValue();
		case DOUBLE: 
			return strValue != null? strValue: getNumber().toString();
		case IRI: 
			return getIRI().toString();
		case LITERAL: 
			return getLiteral().toDataString(); 
		default: return "";
		}
	}
	
	public String toSqlDataString() { 
		switch(constType) {
		case BOOL: 
			return getBoolean().toString();
		case INTEGER: 
			return strValue.replace("'", "''"); //getNumber().toString();
		case DECIMAL: 
			return strValue.replace("'", "''"); //""+((BigDecimal)getNumber()).doubleValue();
		case DOUBLE: 
			return strValue.replace("'", "''"); //getNumber().toString();
		case IRI: 
			return getIRI().toString().replace("'", "''");
		case LITERAL: 
			return getLiteral().toDataString().replace("'", "''"); 
		default: return "";
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((constType == null) ? 0 : constType.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Constant other = (Constant) obj;
		if (constType != other.constType)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
}
