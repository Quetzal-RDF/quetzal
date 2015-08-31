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
package com.ibm.research.rdf.store.sparql11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.BaseTree;
import org.antlr.runtime.tree.BufferedTreeNodeStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibm.research.rdf.store.sparql11.model.AltPath;
import com.ibm.research.rdf.store.sparql11.model.Constant;
import com.ibm.research.rdf.store.sparql11.model.ConstantExpression;
import com.ibm.research.rdf.store.sparql11.model.Expression;
import com.ibm.research.rdf.store.sparql11.model.Expression.EExpressionType;
import com.ibm.research.rdf.store.sparql11.model.IExpressionTraversalListener;
import com.ibm.research.rdf.store.sparql11.model.IRI;
import com.ibm.research.rdf.store.sparql11.model.InvPath;
import com.ibm.research.rdf.store.sparql11.model.NegatedProperySetPath;
import com.ibm.research.rdf.store.sparql11.model.OneOrMorePath;
import com.ibm.research.rdf.store.sparql11.model.PathVisitor;
import com.ibm.research.rdf.store.sparql11.model.Pattern;
import com.ibm.research.rdf.store.sparql11.model.PropertyTerm;
import com.ibm.research.rdf.store.sparql11.model.Query;
import com.ibm.research.rdf.store.sparql11.model.QueryExt;
import com.ibm.research.rdf.store.sparql11.model.QueryTriple;
import com.ibm.research.rdf.store.sparql11.model.SeqPath;
import com.ibm.research.rdf.store.sparql11.model.SimplePath;
import com.ibm.research.rdf.store.sparql11.model.SimplePattern;
import com.ibm.research.rdf.store.sparql11.model.ZeroOrMorePath;
import com.ibm.research.rdf.store.sparql11.model.ZeroOrOnePath;

/**
 * utilities for parsing sparql queries
 */
public class SparqlParserUtilities {

	public static final Log log = LogFactory
			.getLog(SparqlParserUtilities.class);
	public static boolean USE_EXTENSIONS = true;

	public static Query parseSparqlFile(String sparqlFile,
			Map<String, String> rdfStorePrefixes) {
		Query q;
		CharStream stream;
		try {
			stream = new ANTLRFileStream(sparqlFile, "UTF8");
		} catch (IOException e) {
			log.error("Error opening file " + sparqlFile);
			throw new RuntimeException("Error opening file " + sparqlFile, e);
		}
		q = parseSparql(stream, rdfStorePrefixes);
		return q;
	}

	public static Query parseSparqlFile(URL sparqlFile,
			Map<String, String> rdfStorePrefixes) {
		Query q;
		CharStream stream;
		try {
			stream = new ANTLRInputStream(sparqlFile.openStream(), "UTF8");
		} catch (IOException e) {
			log.error("Error opening file " + sparqlFile);
			throw new RuntimeException("Error opening file " + sparqlFile, e);
		}
		q = parseSparql(stream, rdfStorePrefixes);
		return q;
	}

	public static Query parseSparql(File sparqlFile,
			Map<String, String> rdfStorePrefixes) {
		Query q;
		CharStream stream;
		try {
			// uncomment for mac
			// String str = "/" + sparqlFile.toString();
			// stream = new ANTLRFileStream(str , "UTF8");
			stream = new ANTLRFileStream(sparqlFile.getAbsolutePath(), "UTF8");
		} catch (IOException e) {
			log.error("Error opening file " + sparqlFile.getPath());
			throw new RuntimeException("Error reading file " + sparqlFile, e);
		}
		q = parseSparql(stream, rdfStorePrefixes);
		return q;
	}

	public static Query parseSparql(CharStream sparqlFile,
			Map<String, String> rdfStorePrefixes) {
		try {
			Query q;
			if (USE_EXTENSIONS) {
				q = getQueryExt(sparqlFile);
			} else {
				q = getQuery(sparqlFile);
			}
			// System.out.println(q);
			q.expandPrefixes(rdfStorePrefixes);
			// System.out.println(q);
			// q.reverseIRIs();
			// System.out.println("After replacement \n");
			// System.out.println(q);
			Pattern p = q.getMainPattern();
			if (p != null) {
				// p.computePatternIndex();
				p.killUnscopedAccesses();
				p.pushFilters();
				p.pushGraphRestrictions();
				p.replaceFilterBindings();
			}
			return q;
		} catch (SPARQLsyntaxError se) {
			throw se;
		} catch (RecognitionException e) {
			throw new SPARQLsyntaxError(e);
		}
	}

	private static Query getQueryExt(CharStream sparqlFile)
			throws RecognitionException {
		// System.out.println("Parsing: "+sparqlFile+"\n");
		IbmSparqlExtLexer lex = new IbmSparqlExtLexer(sparqlFile);
		CommonTokenStream tokens = new CommonTokenStream(lex);
		IbmSparqlExtParser parser = new IbmSparqlExtParser(tokens);

		parser.setTreeAdaptor(new CommonTreeAdaptor() {
			@Override
			public Object create(Token t) {
				return new XTree(t);
			}
		});

		IbmSparqlExtParser.queryUnit_return ret = parser.queryUnit();
		CommonTree ast = (CommonTree) ret.getTree();

		//
		System.out.println(ast.toStringTree());
		// SparqlParserUtilities.dump_tree(ast, tokens, 0);

		BufferedTreeNodeStream nodes = new BufferedTreeNodeStream(ast);
		nodes.setTokenStream(tokens);
		IbmSparqlExtAstWalker walker = new IbmSparqlExtAstWalker(nodes);
		QueryExt query = walker.queryUnit();
		return query;
	}

	static Query getQuery(CharStream sparqlFile)
			throws RecognitionException {
		CommonTree ast = getParseTree(sparqlFile);
		// System.out.println(ast.toStringTree());
		BufferedTreeNodeStream nodes = new BufferedTreeNodeStream(ast);
		// nodes.setTokenStream(tokens);
		// IbmSparqlAstRewriter astRewriter = new IbmSparqlAstRewriter(nodes);
		// CommonTree ast2 = (CommonTree)astRewriter.downup(ast, false);
		// System.out.println(ast2.toStringTree());
		// BufferedTreeNodeStream nodes2 = new BufferedTreeNodeStream(ast2);
		// nodes2.setTokenStream(tokens);
		IbmSparqlAstWalker walker = new IbmSparqlAstWalker(nodes);
		Query q = walker.queryUnit();
		return q;
	}

	static XTree getParseTree(CharStream sparqlFile)
			throws RecognitionException {
		IbmSparqlLexer lex = new IbmSparqlLexer(sparqlFile);
		CommonTokenStream tokens = new CommonTokenStream(lex);
		IbmSparqlParser parser = new IbmSparqlParser(tokens);
		parser.setTreeAdaptor(new CommonTreeAdaptor() {
			@Override
			public Object create(Token t) {
				return new XTree(t);
			}
		});
		IbmSparqlParser.queryUnit_return ret = parser.queryUnit();
		XTree ast = (XTree) ret.getTree();
		return ast;
	}

	public static Query parseSparqlString(String sparql,
			Map<String, String> rdfStorePrefixes) {
		CharStream input = new ANTLRStringStream(sparql);
		Query q = null;
		try {
			q = parseSparql(input, rdfStorePrefixes);
		} catch (SPARQLsyntaxError se) {
			se.setSQL(sparql);
			throw se;
		}

		return q;
	}

	public static Query parseSparqlString(String sparql) {
		return parseSparqlString(sparql,
				Collections.<String, String> emptyMap());
	}

	private static void dump_tree(CommonTree tree, TokenStream s, int depth) {
		int i;
		int n;
		CommonTree t;

		for (i = 0; i < depth; i++) {
			System.out.print("  ");
		}
		if ((tree != null) && (!tree.isNil())) {
			Location loc = get_tree_position(s, tree);
			if (loc != null) {
				ParsePosition start_position = loc.start_position;
				ParsePosition stop_position = loc.end_position;
				// System.out.println(tree.toString() + " ["
				// + start_position.getLine() + "," + start_position.getCol()
				// + "] -> [" + stop_position.getLine() + ","
				// + stop_position.getCol() + "]");
			} else {
				// System.out.println(tree.toString());
			}
		} else {
			// System.out.println("<no text>\n");
		}

		if ((tree != null) && (!tree.isNil()) && (tree.getChildCount() != 0)) {
			n = tree.getChildCount();

			for (i = 0; i < n; i++) {
				t = (CommonTree) tree.getChild(i);
				dump_tree(t, s, depth + 1);
			}
		}
	}

	/**
	 * Computes line and col
	 * 
	 * @param line
	 * @param col
	 * @param text
	 * @return
	 */
	private static ParsePosition adjust_position(ParsePosition position,
			char[] text) {
		int i;
		int line = position.getLine();
		int col = position.getCol();

		for (i = 1; i < text.length; i++) {
			if (text[i] == '\n') {
				line++;
				col = 1;
			} else {
				col++;
			}
		}

		position.setLine(line);
		position.setCol(col);
		return position;
	}

	private static ParsePosition get_token_start_position(
			TokenStream tokenStream, int tokenIndex) {
		CommonToken token = (CommonToken) tokenStream.get(tokenIndex);
		int line = token.getLine();
		int column = token.getCharPositionInLine();
		return new ParsePosition(line, column);
	}

	private static ParsePosition get_token_end_position(
			TokenStream tokenStream, int tokenIndex) {
		CommonToken token;

		ParsePosition pos = get_token_start_position(tokenStream, tokenIndex);

		token = (CommonToken) tokenStream.get(tokenIndex);
		return adjust_position(pos, token.getText().toCharArray());
	}

	private static Location get_tree_position(TokenStream tokenStream,
			BaseTree tree) {
		int startIndex;
		int stopIndex;
		CommonToken stopToken;
		int stopColumn;

		ParsePosition end_position = null;
		startIndex = tree.getTokenStartIndex();
		ParsePosition start_position = null;
		try {
			start_position = get_token_start_position(tokenStream, startIndex);
		} catch (RuntimeException e) {
			return null;
		}

		stopIndex = tree.getTokenStopIndex();
		stopToken = (CommonToken) tokenStream.get(stopIndex);
		stopColumn = stopToken.getCharPositionInLine();
		if (stopColumn == -1) {
			int end_line = tree.getLine();
			int end_column = tree.getCharPositionInLine();
			end_position = adjust_position(new ParsePosition(end_line,
					end_column), tree.getText().toCharArray());
		} else {
			end_position = get_token_end_position(tokenStream, stopIndex);
		}

		return new Location(start_position, end_position);
	}

	static class Location {

		public ParsePosition start_position;
		public ParsePosition end_position;

		public Location(ParsePosition x, ParsePosition y) {
			start_position = x;
			end_position = y;
		}
	}

	static class ParsePosition {

		int line;
		int col;

		public ParsePosition(int x, int y) {
			line = x;
			col = y;
		}

		public String toString() {
			return "[" + line + "," + col + "]";
		}

		public int getLine() {
			return line;
		}

		public void setLine(int x) {
			line = x;
		}

		public int getCol() {
			return col;
		}

		public void setCol(int y) {
			col = y;
		}
	}

	public static Set<String> gatherQueryPredicates(Query q) {
		if (q.getMainPattern() == null) {
			return Collections.emptySet();
		}
		final Set<String> preds = new HashSet<String>();
		for (Pattern p : q.getMainPattern().gatherSubPatterns(true)) {
			if (p instanceof SimplePattern) {
				for (QueryTriple t : ((SimplePattern) p).getQueryTriples()) {
					PropertyTerm pred = t.getPredicate();
					if (pred.isIRI()) {
						preds.add(pred.getIRI().getValue());
					} else if (pred.isPath()) {
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
								preds.add(p.getIRI().getValue());
							}

							@Override
							public void visit(NegatedProperySetPath prop) {
								for (IRI p : prop.getFowardProperties()) {
									preds.add(p.getValue());
								}
								for (IRI p : prop.getBackwardProperties()) {
									preds.add(p.getValue());
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
						pred.getPath().visit(visitor);
					} else {
						assert pred.isVariable();
						for (Expression expr : p.getFilters()) {
							if (expr.gatherVariables().contains(
									pred.getVariable())) {
								expr.traverse(new IExpressionTraversalListener() {
									public void startExpression(Expression e) {
										if (e.getType() == EExpressionType.CONSTANT) {
											Constant c = ((ConstantExpression) e)
													.getConstant();
											if (c.getIRI() != null) {
												preds.add(c.getIRI().getValue());
											}
										}
									}

									public void endExpression(Expression e) {

									}
								});
							}
						}
					}
				}
			}
		}
		return preds;
	}
}
