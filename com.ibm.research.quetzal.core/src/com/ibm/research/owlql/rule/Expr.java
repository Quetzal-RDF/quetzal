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

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.StringTokenizer;





/**
 * An expression used as an argument of an {@link AtomicFormula}
 *  Note: In this simple implementation, there are only 
 *  two expressions: variables and constants. 
	 
 * @author <a href="mailto:achille@us.ibm.com">Achille Fokoue</a>
 * 
 */
public abstract class Expr {

	/**
	 * whether this argument is a constant. 
	 ** 
	 * @return
	 */
	public abstract boolean isConstant();
	
	public abstract ConstantExpr asConstant();
	/**
	 * whether this argument is a variable. 
	 ** 
	 * @return
	 */
	public abstract boolean isVariable();
	public abstract VariableExpr asVariable();
	
	
	public static Expr parse(String s ) throws ParseException {
		s=s.trim();
		if (s.length()==0) {
			throw new ParseException(" the empty string is not a valid expression", 0);
		}
		int firstCh = s.charAt(0);
		//
		if (Character.isLetter(firstCh) ) {
					
			StringTokenizer tok= new StringTokenizer(s);
			int numOfTokens = 0;
			while(tok.hasMoreTokens()) {
				numOfTokens++;
				tok.nextToken();
				if (numOfTokens>1) {
					throw new ParseException("Invalid variable: "+s,0);
				}
			}
			return new VariableExpr(s);		
		} else {
			if (s.length()>1) {
				if (s.startsWith("'") && s.endsWith("'")) {
					return new ConstantExpr(s.substring(1,s.length()-1));
				} 
				if (s.startsWith("\"") && s.endsWith("\"")) {
					return new ConstantExpr(s.substring(1,s.length()-1));
				} 
				if (s.startsWith("<") && s.endsWith(">")) {
					try {
						return new ConstantExpr(new URI(s.substring(1,s.length()-1)));
					} catch (URISyntaxException e) {
						throw new ParseException("Invalid URI: "+ s+". Error message: "+e.getMessage(), 0);
					}
				}
			}
			try {
				long value = Long.parseLong(s);
				if (value<=Integer.MAX_VALUE && value>=Integer.MIN_VALUE) {
					return new ConstantExpr(new Integer((int)value));
				}
				return new ConstantExpr(new Long(value));
			} catch (NumberFormatException e) {
				try {
					double value = Double.parseDouble(s);
					return new ConstantExpr(new Double(value));
				} catch (NumberFormatException ex) {
					throw new ParseException("Invalid constant " + s, 0);
				}
				
				
			}
				
		}
	}
	
	public abstract Expr clone();
	
}
