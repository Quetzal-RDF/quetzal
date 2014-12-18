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

public class StoreProcedureDeclaration implements SQLCommand {

	protected StoreProcedure procedure;
	protected boolean replace;
	
	public StoreProcedureDeclaration(StoreProcedure procedure) {
		this(procedure, true);
	}
	
	public StoreProcedureDeclaration(StoreProcedure procedure, boolean replace) {
		super();
		this.procedure = procedure;
		this.replace = replace;
	}


	@Override
	public String toSQL() {
		 return procedure.getSqlDeclarationCode(replace);
	}

}
