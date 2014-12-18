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
import java.util.Set;

import com.ibm.research.rdf.store.sparql11.model.Pattern.EPatternSetType;
import com.ibm.wala.util.collections.HashSetFactory;



public abstract class Path {
	
	public static enum PathMod {
		ZERO_OR_ONE, ZERO_OR_MORE, ONE_OR_MORE
	}
	
	public static Path createPath(Path p, PathMod mod) {
		switch (mod) {
			case ZERO_OR_ONE: return new ZeroOrOnePath(p);
			case ZERO_OR_MORE: return new ZeroOrMorePath(p);
			case ONE_OR_MORE: return new OneOrMorePath(p);
			default: 
				throw new RuntimeException("Unknow path modifier: "+ mod);
		}
	}
	/**
	 *  returns whether a given pattern contains a recursive property path or a ZeroOrOne property path
	 * @param p
	 * @return
	 */
	public static boolean isRecursiveOrHasZeroOrOnePath(Pattern p) {
		for (Pattern sp: p.gatherSubPatterns(true)) {
			if (sp.getType().equals(EPatternSetType.SIMPLE)) {
				SimplePattern simplep = (SimplePattern) sp;
				for (QueryTriple t:simplep.getQueryTriples()) {
					if (t.getPredicate().isPath() && 
						(t.getPredicate().getPath().isRecursive() ||  t.getPredicate().getPath().isDirectlyZeroOrOnePath())) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static Set<IRI> getProperties(Path path) {
		final Set<IRI> ret = HashSetFactory.make();
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
				ret.add(p.getIRI());
				
			}
			
			@Override
			public void visit(NegatedProperySetPath p) {
				ret.addAll(p.getBackwardProperties());
				ret.addAll(p.getFowardProperties());
				
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
		path.visit(visitor);
		return ret;
	}
	
	/**
	 *  returns whether a given pattern contains a recursive property path
	 * @param p
	 * @return
	 */
	/*public static boolean isRecursive(Query q) {
		return isRecursive(q.getMainPattern());
		
	}*/
	/**
	 *  returns whether a given pattern contains a oneOrMore property path (?)
	 * @param p
	 * @return
	 */
	/*public static boolean hasOneOrMorePath(Query q) {
		return  hasOneOrMorePath(q.getMainPattern());
		
	}*/
	/**
	 *  returns whether a given pattern contains a recursive property path (i.e., + or *) or an ZeroOrOne property path (i.e., ?)
	 * @param p
	 * @return
	 */
	public static boolean isRecursiveOrHasZeroOrOne(Query q) {
		return isRecursiveOrHasZeroOrOnePath(q.getMainPattern());
		
	}
	
	public static class ToStringVisitor implements PathVisitor {

		protected StringBuffer buf;
		
		public ToStringVisitor() {
			super();
			this.buf = new StringBuffer();
		}
		public String getString() {
			return buf.toString();
		}
		@Override
		public void visit(AltPath p) {
			buf.append("(");
			p.getLeft().visit(this);
			buf.append(" | ");
			p.getRight().visit(this);
			buf.append(")");
		}

		@Override
		public void visit(SeqPath p) {
			buf.append("(");
			p.getLeft().visit(this);
			buf.append("/");
			p.getRight().visit(this);
			buf.append(")");
		}

		@Override
		public void visit(NegatedProperySetPath prop) {
			buf.append("!(");
			boolean first = true;
			for (IRI p : prop.getFowardProperties()) {
				if (first) {
					first = false;
				} else {
					buf.append("|");
				}
				buf.append(toString(p));
			}
			for (IRI p : prop.getBackwardProperties()) {
				if (first) {
					first = false;
				} else {
					buf.append("|");
				}
				buf.append("^"+toString(p));
			}
			buf.append(")");
		}
		protected String toString(IRI iri) {
			return iri.toString();
		}
		@Override
		public void visit(SimplePath p) {
			buf.append(toString(p.getIRI()));
		}

		@Override
		public void visit(InvPath p) {
			buf.append("^(");
			p.getSubPath().visit(this);
			buf.append(")");
		}

		@Override
		public void visit(OneOrMorePath p) {
			buf.append("(");
			p.getSubPath().visit(this);
			buf.append(")+");
			
		}

		@Override
		public void visit(ZeroOrMorePath p) {
			buf.append("(");
			p.getSubPath().visit(this);
			buf.append(")*");
		}

		@Override
		public void visit(ZeroOrOnePath p) {
			buf.append("(");
			p.getSubPath().visit(this);
			buf.append(")?");
			
		}
		
	}
	public abstract void visit(PathVisitor visitor);

	public abstract <X> X map(PathMapper<X> visitor);

	/**
	 * returns whether the path consists of a simple URI step.
	 * The default implementation returns <code>false</code>.
	 * @return
	 */
	public boolean isSimplePath() {
		return false;
	}
	/**
	 *returns whether this path directly contains  a "?" path modifier.
	 * @return
	 */
	public boolean isDirectlyZeroOrOnePath() {
		return false;
	}
	/**
	 *returns whether this path directly contains  a "*" or "+" path modifier.
	 * @return
	 */
	public boolean isDirectlyRecursive() {
		return false;
	}
	/**
	 *returns whether this path contains directly or indirectly "*" or "+" path modifier.
	 * @return
	 */
	public boolean isRecursive() {
		final boolean[] ret = new boolean[]{false};
		PathVisitor visitor = new PathVisitor() {
			
			@Override
			public void visit(ZeroOrOnePath p) {
				if (!ret[0]) {
					p.getSubPath().visit(this);
				} 
			}
			@Override
			public void visit(ZeroOrMorePath p) {
				ret[0] = true;
			}
			
			@Override
			public void visit(OneOrMorePath p) {
				ret[0] = true;
			}
			
			@Override
			public void visit(InvPath p) {
				if (!ret[0]) {
					p.getSubPath().visit(this);
				} 
			}
			
			@Override
			public void visit(SimplePath p) {
				// Do nothing
			}
			
			@Override
			public void visit(NegatedProperySetPath p) {
				// Do nothing
				
			}
			
			@Override
			public void visit(SeqPath p) {
				if (!ret[0]) {
					p.getLeft().visit(this);
				} 
				if (!ret[0]) {
					p.getRight().visit(this);
				} 
			}
			
			@Override
			public void visit(AltPath p) {
				if (!ret[0]) {
					p.getLeft().visit(this);
				} 
				if (!ret[0]) {
					p.getRight().visit(this);
				} 
				
			}
		};
		visit(visitor);
		return ret[0];
	}
	
	/**
	 *returns whether this path contains directly or indirectly "?" path modifier.
	 * @return
	 */
	public boolean hasZeroOrOnePath() {
		final boolean[] ret = new boolean[]{false};
		PathVisitor visitor = new PathVisitor() {
			
			@Override
			public void visit(ZeroOrOnePath p) {
				ret[0] = true;
			}
			@Override
			public void visit(ZeroOrMorePath p) {
				if (!ret[0]) {
					p.getSubPath().visit(this);
				} 
			}
			
			@Override
			public void visit(OneOrMorePath p) {
				if (!ret[0]) {
					p.getSubPath().visit(this);
				} 
			}
			
			@Override
			public void visit(InvPath p) {
				if (!ret[0]) {
					p.getSubPath().visit(this);
				} 
			}
			
			@Override
			public void visit(SimplePath p) {
				// Do nothing
			}
			
			@Override
			public void visit(NegatedProperySetPath p) {
				// Do nothing
				
			}
			
			@Override
			public void visit(SeqPath p) {
				if (!ret[0]) {
					p.getLeft().visit(this);
				} 
				if (!ret[0]) {
					p.getRight().visit(this);
				} 
			}
			
			@Override
			public void visit(AltPath p) {
				if (!ret[0]) {
					p.getLeft().visit(this);
				} 
				if (!ret[0]) {
					p.getRight().visit(this);
				} 
				
			}
		};
		visit(visitor);
		return ret[0];
	}

	public final boolean isIRI() {
		return isSimplePath();
	}
	/**
	 * if this {@link Path} is a simple path (as returned by {@link #isSimplePath()}), it returns the IRI of the path; otherwise, it returns <code>null</code>.
	 * The default implementation returns <code>null</code>
	 * @return
	 */
	public IRI getIRI() {
		return null;
	}
	
	public  void renamePrefixes(final String base, final Map<String, String> declared, final Map<String, String> internal) {
		PathVisitor renamePrefixesVisitor = new PathVisitor() {
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
			public void visit(NegatedProperySetPath prop) {
				for (IRI p : prop.getFowardProperties()) {
					p.rename(base, declared, internal);
				}
				for (IRI p : prop.getBackwardProperties()) {
					p.rename(base, declared, internal);
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
		visit(renamePrefixesVisitor);
	}
	
	public void reverse() {
		
		PathVisitor reverseVisitor = new PathVisitor() {
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
			public void visit(NegatedProperySetPath prop) {
				for (IRI p : prop.getFowardProperties()) {
					p.reverse();
				}
				for (IRI p : prop.getBackwardProperties()) {
					p.reverse();
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
		visit(reverseVisitor);
	}
	
	public String toSqlDataString() {
		if(isIRI()) {
			return getIRI().getSqlDataString();
		}
		return null;
	}
	
	@Override
	public final String toString() {
		ToStringVisitor v = new ToStringVisitor();
		visit(v);
		return v.getString();
	}
	
}
