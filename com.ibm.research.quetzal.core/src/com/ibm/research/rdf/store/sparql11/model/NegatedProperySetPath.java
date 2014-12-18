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

import java.util.List;

public class NegatedProperySetPath extends Path {
	
	protected List<IRI> fowardProperties;
	protected List<IRI> backwardProperties;
	public NegatedProperySetPath(List<IRI> fowardProperties,
			List<IRI> backwardProperties) {
		super();
		this.fowardProperties = fowardProperties;
		this.backwardProperties = backwardProperties;
	}
	public List<IRI> getFowardProperties() {
		return fowardProperties;
	}
	public List<IRI> getBackwardProperties() {
		return backwardProperties;
	}
	@Override
	public void visit(PathVisitor visitor) {
		visitor.visit(this);
	}
	@Override
	public <X> X map(PathMapper<X> visitor) {
		return visitor.visit(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((backwardProperties == null) ? 0 : backwardProperties
						.hashCode());
		result = prime
				* result
				+ ((fowardProperties == null) ? 0 : fowardProperties.hashCode());
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
		NegatedProperySetPath other = (NegatedProperySetPath) obj;
		if (backwardProperties == null) {
			if (other.backwardProperties != null)
				return false;
		} else if (!backwardProperties.equals(other.backwardProperties))
			return false;
		if (fowardProperties == null) {
			if (other.fowardProperties != null)
				return false;
		} else if (!fowardProperties.equals(other.fowardProperties))
			return false;
		return true;
	}
	
	
}
