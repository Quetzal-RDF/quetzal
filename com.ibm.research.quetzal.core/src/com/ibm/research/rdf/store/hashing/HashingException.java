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
 package com.ibm.research.rdf.store.hashing;

public class HashingException extends Exception {
	private static final long serialVersionUID = -2107064965806219851L;
	public HashingException() {}
	public HashingException(String message) { super(message); }
	public HashingException(Throwable cause) { super(cause); }
	public HashingException(String message, Throwable cause) { super(message, cause); }
}
