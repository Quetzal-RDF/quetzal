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

import java.util.Map;

/**
 * Models a property, which is represented as variable or a path expression.
 * @author fokoue
 *
 */
public class PropertyTerm {
	private BinaryUnion<Variable, Path> value;
	
	
	public PropertyTerm(Variable var) {
		value = new BinaryUnion<Variable, Path>();
		value.setFirst(var);
	}
	public PropertyTerm(Path path) {
		value = new  BinaryUnion<Variable, Path>();
		value.setSecond(path);
	}
	
	public PropertyTerm(BinaryUnion<Variable, Path> value) {
		super();
		this.value = new BinaryUnion<Variable, Path>();
		if (value.isFirstType()) {
			this.value.setFirst(value.getFirst());
		} else {
			this.value.setSecond(value.getSecond());
		}
	}
	public PropertyTerm(IRI iri) {
		this(new SimplePath(iri));
	}
	public PropertyTerm(PropertyTerm pt) {
		value = new BinaryUnion<Variable, Path>();
		if (pt.isVariable()) {
			value.setFirst(pt.getVariable());
		} else {
			assert pt.isPath(): pt;
			value.setSecond(pt.getPath());
			
		}
	}
	public boolean isVariable() {
		return value.isFirstType();
	}
	public boolean isPath() {
		return value.isSecondType();
	}
	
	public Variable getVariable() {
		return value.getFirst();
	}
	public Path getPath() {
		return value.getSecond();
	}
	public QueryTripleTerm toQueryTripleTerm() {
		if (isVariable()) {
			return new QueryTripleTerm(getVariable());
		} else if (isIRI()) {
			return new QueryTripleTerm(getIRI());
		} else {
			return null;
		}
	}
	public boolean isSimilarTo(PropertyTerm other) {
		if (isVariable() && other.isVariable()) {
			return true;
		} else if (isPath() && other.isPath()){
			return this.getPath().equals(other.getPath());
		}
		return false;
	}
	
	
	
	public  void renamePrefixes(final String base, final Map<String, String> declared, final Map<String, String> internal) {
		/*if(isPath() && getPath().isIRI()) {
			getPath().getIRI().rename(base, declared, internal);
		}*/
		if (isPath()) {
			PathVisitor visitor = new PathVisitor() {
				@Override
				public void visit(ZeroOrOnePath p) {
					p.getSubPath().visit(this);
					
				}
				
				@Override
				public void visit(ZeroOrMorePath p) {
					p.getSubPath().visit(this);
					
				}
				
				@Override
				public void visit(OneOrMorePath p) {
					p.getSubPath().visit(this);
					
				}
				
				@Override
				public void visit(InvPath p) {
					p.getSubPath().visit(this);
					
				}
				
				@Override
				public void visit(SimplePath p) {
					p.getIRI().rename(base, declared, internal);
					
				}
				
				@Override
				public void visit(NegatedProperySetPath p) {
					for (IRI iri: p.getBackwardProperties()) {
						iri.rename(base, declared, internal);
					}
					for (IRI iri: p.getFowardProperties()) {
						iri.rename(base, declared, internal);
					}
				}
				
				@Override
				public void visit(SeqPath p) {
					p.getLeft().visit(this);
					p.getRight().visit(this);
				}
				
				@Override
				public void visit(AltPath p) {
					p.getLeft().visit(this);
					p.getRight().visit(this);
				}
			};
			getPath().visit(visitor);
		}
	}
	
	public void reverse() {
		/*if(isPath() && getPath().isIRI()) {
			getPath().getIRI().reverse();
		} */
		if (isPath()) {
			PathVisitor visitor = new PathVisitor() {
				@Override
				public void visit(ZeroOrOnePath p) {
					p.getSubPath().visit(this);
					
				}
				
				@Override
				public void visit(ZeroOrMorePath p) {
					p.getSubPath().visit(this);
					
				}
				
				@Override
				public void visit(OneOrMorePath p) {
					p.getSubPath().visit(this);
					
				}
				
				@Override
				public void visit(InvPath p) {
					p.getSubPath().visit(this);
					
				}
				
				@Override
				public void visit(SimplePath p) {
					p.getIRI().reverse();
					
				}
				
				@Override
				public void visit(NegatedProperySetPath p) {
					for (IRI iri: p.getBackwardProperties()) {
						iri.reverse();
					}
					for (IRI iri: p.getFowardProperties()) {
						iri.reverse();
					}
				}
				
				@Override
				public void visit(SeqPath p) {
					p.getLeft().visit(this);
					p.getRight().visit(this);
				}
				
				@Override
				public void visit(AltPath p) {
					p.getLeft().visit(this);
					p.getRight().visit(this);
				}
			};
			getPath().visit(visitor);
		}
	}
	
	public String toSqlDataString() {
		if(isPath() && getPath().isIRI()) {
			return getPath().getIRI().getSqlDataString();
		}
		/*if(isConstant()){
			return getConstant().toSqlDataString();
		}*/
		return null;
	}
	
	public boolean isComplexPath() {
		return isPath() && !getPath().isSimplePath();
	}
	//Legacy methods
	public boolean isIRI() { return isPath() && getPath().isIRI(); }
	public boolean isConstant() { return false; }
	public boolean isBlankNode() { return  false; }
	public boolean isTriplesNode() { return false; }
	
	public IRI getIRI() { return isIRI()? getPath().getIRI(): null; }
	public Constant getConstant() { return null; }
	public BlankNodeVariable getBlankNode() { return null; }
	public TriplesNode getTriplesNode() { return null; }
	
	//
	
	@Override
	public String toString() {
		return value.toString();
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		PropertyTerm other = (PropertyTerm) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
	
}
