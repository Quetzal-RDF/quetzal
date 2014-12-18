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
 package com.ibm.research.rdf.store.jena;

public class RdfStoreException extends RuntimeException {
	
	public RdfStoreException(String msg) {
		super(msg);
	}
	
	public RdfStoreException() {
		super();
	}

	public RdfStoreException(String string, Exception e) {
		super(string,e);
	}

	private static final long serialVersionUID = 1L;

}
