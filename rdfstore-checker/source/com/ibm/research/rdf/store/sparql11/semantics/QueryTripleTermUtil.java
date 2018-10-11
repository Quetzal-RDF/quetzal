package com.ibm.research.rdf.store.sparql11.semantics;

import java.net.URI;
import java.net.URISyntaxException;

import com.ibm.research.rdf.store.sparql11.model.QueryTripleTerm;
import com.ibm.research.rdf.store.sparql11.model.StringLiteral;
import com.ibm.wala.util.collections.Pair;

public class QueryTripleTermUtil {
	
	static URI toURI(QueryTripleTerm n) throws URISyntaxException {
		return new URI(n.getIRI().getValue());
	}

	public static Pair<String, Object> toLiteral(QueryTripleTerm val) throws URISyntaxException {
		StringLiteral lit = val.getConstant().getLiteral();
		
		Object snd = null;
		if (! isNull(lit.getLanguage())) {
			assert isNull(lit.getType()) || isStringType(lit.getType()): lit.getType() + " unexpected with " + lit.getLanguage();
			snd = lit.getLanguage().toLowerCase();
		}
		if (! isNull(lit.getType())) {
			assert isNull(lit.getLanguage()) || isStringType(lit.getType()): lit.getLanguage() + " unexpected with " + lit.getType();
			snd = new URI(lit.getType().getValue());
		}
		
		assert snd != null;
		
		return Pair.make(lit.getValue(), snd);
	}

	public static Object toAtom(QueryTripleTerm t) throws URISyntaxException {
		if (t == null) {
			return QuadTableRelations.NULL_atom;
		} else if (t.isIRI()) {
			return toURI(t);
		} else if (t.isBlankNode()) {
			return t.getBlankNode().getName();
		} else {
			assert t.isConstant();
			return toLiteral(t);
		}
	}
	
	public static boolean isNull(Object x) {
		return x == null || "".equals(x.toString());
	}
	
	public static boolean isStringType(Object x) {
		return x != null && x.toString().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#langString");
	}

}
