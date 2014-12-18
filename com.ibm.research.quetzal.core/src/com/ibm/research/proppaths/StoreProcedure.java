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
 package com.ibm.research.proppaths;

import java.util.List;

import com.ibm.wala.util.collections.Pair;



/**
 * Represent a store procedure that can be declared and invoked
 * @author fokoue
 *
 */
public class StoreProcedure {
	
	protected String name;
	protected List<Pair<String, String>> inputParameterNameTypeList;
	protected List<Pair<String, String>> outputParameterNameTypeList;
	protected List<SQLCommand> bodyStatements;
	protected int numberOfReturnedResultSets;
	public StoreProcedure(String name, List<Pair<String, String>> inputParameterNameTypeList, List<Pair<String, String>> outputParameterNameTypeList,  int numberOfReturnedResultSets, List<SQLCommand> bodyStatements) {
		super();
		this.name = name;
		this.inputParameterNameTypeList = inputParameterNameTypeList;
		this.outputParameterNameTypeList = outputParameterNameTypeList;
		this.bodyStatements = bodyStatements;
		this.numberOfReturnedResultSets = numberOfReturnedResultSets;
	}
	public String getName() {
		return name;
	}
	
	
	public List<Pair<String, String>> getInputParameterNameTypePairs() {
		return inputParameterNameTypeList;
	}
	public List<Pair<String, String>> getOutputParameterNameTypePairs() {
		return outputParameterNameTypeList;
	}
	public List<SQLCommand> getBodyStatements() {
		return bodyStatements;
	}
	public int getNumberOfReturnedResultSets() {
		return numberOfReturnedResultSets;
	}
	public int getParamSize() {
		return inputParameterNameTypeList.size()+outputParameterNameTypeList.size();
	}
	
	public static String getSqlInvocatiionCode(String procName, int procParamSize, Object... params) {
		//TODO: use templates
		if (params.length!=procParamSize) {
			throw new IllegalArgumentException("The number of parameters is different from the expected number of argument for the store procedure invocation");
		}
		StringBuffer buf = new StringBuffer();
		buf.append("call ").append(procName).append("(");
		boolean first =true;
		for (Object param: params) {
			if (first) {
				first = false;
			} else {
				buf.append(", ");
			}
			if (param instanceof String) {
				param = "'"+ ((String)param).replace("'","''")+"'";
			}
			buf.append(param);
		}
		buf.append(")");
		return buf.toString();
		
	}
	/**
	 * returns the sql code to invoke this {@link StoreProcedure}.
	 *
	 * @return
	 */
	public String getSqlInvocatiionCode(Object... params) {
		//TODO: use templates
		return getSqlInvocatiionCode(name, getParamSize(), params);
		
	}
	/**
	 * the sql code to declare this store procedure
	 * @return
	 */
	public String getSqlDeclarationCode(boolean replace) {
		//TODO: use templates
		StringBuffer buf = new StringBuffer();
		if (replace) {
			buf.append("CREATE OR REPLACE PROCEDURE ");
		} else {
			buf.append("CREATE ");
		}
		buf.append(name)
			.append("(");
				boolean first = true;
				for (Pair<String, String> nameTypePair: inputParameterNameTypeList) {
					if (first) {
						first  = false;
					} else {
						buf.append(", ");
					}
					buf.append("IN ").append(nameTypePair.fst).append(" ").append(nameTypePair.snd);
				}
				for (Pair<String, String> nameTypePair: outputParameterNameTypeList) {
					if (first) {
						first  = false;
					} else {
						buf.append(", ");
					}
					buf.append("OUT ").append(nameTypePair.fst).append(" ").append(nameTypePair.snd);
				}
			buf.append(")").append("\n");
		if (numberOfReturnedResultSets>0) {
			buf.append("DYNAMIC RESULT SETS ").append(numberOfReturnedResultSets).append("\n");
		}
		buf.append("LANGUAGE SQL\n");
		buf.append("BEGIN\n");
		for (SQLCommand cmd: bodyStatements) {
			buf.append("\t").append(cmd.toSQL()).append(";\n");
		}
		buf.append("END");
		return buf.toString();
	}
}
