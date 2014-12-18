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

public class DropStoreProcedure implements SQLCommand {

	protected String procedure;
	
	public DropStoreProcedure(String procedure) {
		super();
		this.procedure = procedure;
	}

	@Override
	public String toSQL() {
		//TODO: use templates
		return "DROP PROCEDURE "+procedure;
	}

}
