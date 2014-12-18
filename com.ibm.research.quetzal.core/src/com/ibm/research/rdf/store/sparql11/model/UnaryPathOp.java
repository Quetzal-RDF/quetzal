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

public abstract class UnaryPathOp extends Path {

	protected Path subPath;

	public UnaryPathOp(Path subPath) {
		super();
		this.subPath = subPath;
	}

	public Path getSubPath() {
		return subPath;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((subPath == null) ? 0 : subPath.hashCode());
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
		UnaryPathOp other = (UnaryPathOp) obj;
		if (subPath == null) {
			if (other.subPath != null)
				return false;
		} else if (!subPath.equals(other.subPath))
			return false;
		return true;
	}
	
}
