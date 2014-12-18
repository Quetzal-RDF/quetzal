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

public class SimplePath extends Path {

	protected IRI iri;

	public SimplePath(IRI iri) {
		super();
		this.iri = iri;
		assert iri!=null;
	}

	@Override
	public IRI getIRI() {
		return iri;
	}
	

	@Override
	public boolean isSimplePath() {
		return true;
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
		result = prime * result + ((iri == null) ? 0 : iri.hashCode());
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
		SimplePath other = (SimplePath) obj;
		if (iri == null) {
			if (other.iri != null)
				return false;
		} else if (!iri.equals(other.iri))
			return false;
		return true;
	}
	
	
}
