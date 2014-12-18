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

public class StoreProcedureInvocation implements SQLCommand {

	protected String procName;
	protected int procParamSize;
	protected Object[] arguments;
	
	public StoreProcedureInvocation(StoreProcedure procedure, String... arguments) {
		super();
		this.procName = procedure.getName();
		this.procParamSize = procedure.getParamSize();
		this.arguments = arguments;
	}
	
	public StoreProcedureInvocation(String procedureName, Object... arguments) {
		super();
		this.procName= procedureName;
		this.arguments = arguments;
		this.procParamSize = arguments.length;
	}

	@Override
	public String toSQL() {
		return StoreProcedure.getSqlInvocatiionCode(procName, procParamSize, arguments);
	}

}
