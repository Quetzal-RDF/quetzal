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
// $ANTLR 3.3 Nov 30, 2010 12:50:56 IbmSparqlExtAstWalker.g 2015-04-14 15:37:38
 
package com.ibm.research.rdf.store.sparql11;
	
import org.antlr.runtime.BitSet;

import java.util.*;
import java.math.BigInteger;
import java.math.BigDecimal;

import com.ibm.research.rdf.store.sparql11.SPARQLsyntaxError;
import com.ibm.research.rdf.store.sparql11.model.*;
import com.ibm.wala.util.collections.HashSetFactory;
import com.ibm.wala.util.collections.Pair;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;

import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
public class IbmSparqlExtAstWalker extends TreeParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "PATH", "ALT", "SEQ", "ELT", "INV", "BROKEN_PLUS", "BROKEN_MINUS", "NIL", "ANNON", "ROOT", "PROLOGUE", "DEFAULT_NAMESPACE", "NAMESPACE_PREFIX_MAP", "KEY", "QUERY", "UPDATE", "TYPE", "PVARS", "EXP", "NOT_IN", "GROUP_GRAPH_PATTERN", "GROUP_GRAPH_PATTERN_SUB", "GRAPH_GRAPH_PATTERN", "SUB_SELECT", "TRIPLES_BLOCK", "NON_TRIPLES", "TRIPLE", "TRIPLE2", "TRIPLES_SAME_SUBJECT", "GRAPH_NODE", "VAR", "PREFIXED_NAME", "PREFIXED_NS", "FUNCTION", "EXPRESSION", "NOT_EXISTS", "IRI_OR_FUNCTION", "DATASET", "GROUP_BY", "ORDER_BY", "CONDITION", "BIND_VALUES", "STRING", "BOOLEAN", "NUMERIC", "SUBJECT", "PREDICATE", "VALUE", "TRIPLES_NODE_PROPERTY_LIST", "TRIPLES_NODE", "COLLECTION", "PROPERTY_LIST", "PREDICATE_VALUE", "WHERE", "IRI_REF", "LTE", "MODIFIERS", "BIG_INTEGER", "BIG_DECIMAL", "INLINE_DATA", "OUTV", "FUNCBODY", "BASE", "PREFIX", "PNAME_NS", "VARNAME", "OPEN_BRACE", "ARROW", "CLOSE_BRACE", "FUNCLANG", "OPEN_CURLY_BRACE", "STRING_LITERAL2", "CLOSE_CURLY_BRACE", "SELECT", "DISTINCT", "REDUCED", "AS", "CONSTRUCT", "WHERE_TOKEN", "DESCRIBE", "ASK", "FROM", "NAMED", "GROUP", "BY", "HAVING", "ORDER", "ASC", "DESC", "LIMIT", "INTEGER", "OFFSET", "BINDINGS", "UNDEF", "SEMICOLON", "LOAD", "SILENT", "INTO", "CLEAR", "DROP", "CREATE", "ADD", "TO", "MOVE", "COPY", "INSERT", "DATA", "DELETE", "WITH", "USING", "DEFAULT", "GRAPH", "ALL", "DOT", "VALUES", "OPTIONAL", "SERVICE", "BIND", "UNION", "MINUS", "FILTER", "COMMA", "OPEN_SQ_BRACKET", "CLOSE_SQ_BRACKET", "VAR1", "VAR2", "LOGICAL_OR", "LOGICAL_AND", "LT", "IN", "NOT", "STR", "LANG", "LANGMATCHES", "DATATYPE", "BOUND", "IRI", "URI", "BNODE", "RAND", "ABS", "CEIL", "FLOOR", "ROUND", "CONCAT", "STRLEN", "UCASE", "LCASE", "ENCODE_FOR_URI", "CONTAINS", "STRSTARTS", "STRENDS", "STRBEFORE", "STRAFTER", "YEAR", "MONTH", "DAY", "HOURS", "MINUTES", "SECONDS", "TIMEZONE", "TZ", "NOW", "UUID", "STRUUID", "MD5", "SHA1", "SHA224", "SHA256", "SHA384", "SHA512", "COALESCE", "IF", "STRLANG", "STRDT", "SAMETERM", "ISIRI", "ISURI", "ISBLANK", "ISLITERAL", "ISNUMERIC", "REGEX", "SUBSTR", "REPLACE", "EXISTS", "COUNT", "SUM", "MIN", "MAX", "AVG", "SAMPLE", "GROUP_CONCAT", "SEPARATOR", "LANGTAG", "DECIMAL", "DOUBLE", "INTEGER_POSITIVE", "DECIMAL_POSITIVE", "DOUBLE_POSITIVE", "INTEGER_NEGATIVE", "DECIMAL_NEGATIVE", "DOUBLE_NEGATIVE", "TRUE", "FALSE", "STRING_LITERAL1", "STRING_LITERAL_LONG1", "STRING_LITERAL_LONG2", "PNAME_LN", "BLANK_NODE_LABEL", "L", "A", "N", "G", "U", "E", "F", "C", "FUNCCALL", "T", "I", "O", "B", "S", "P", "R", "X", "D", "W", "H", "K", "M", "Y", "V", "Z", "UNICODE_ESCAPE", "PN_PREFIX", "PN_LOCAL", "DIGIT", "HEXDIGIT", "EXPONENT", "ECHAR", "WS", "EOL", "COMMENT", "PN_CHARS_BASE", "PN_CHARS_U", "PN_CHARS", "PLX", "PERCENT", "PN_LOCAL_ESC", "J", "Q", "'*'", "'a'", "'|'", "'/'", "'^'", "'?'", "'+'", "'!'", "'='", "'!='", "'>'", "'>='", "'-'", "'^^'"
    };
    public static final int EOF=-1;
    public static final int T__256=256;
    public static final int T__257=257;
    public static final int T__258=258;
    public static final int T__259=259;
    public static final int T__260=260;
    public static final int T__261=261;
    public static final int T__262=262;
    public static final int T__263=263;
    public static final int T__264=264;
    public static final int T__265=265;
    public static final int T__266=266;
    public static final int T__267=267;
    public static final int T__268=268;
    public static final int T__269=269;
    public static final int PATH=4;
    public static final int ALT=5;
    public static final int SEQ=6;
    public static final int ELT=7;
    public static final int INV=8;
    public static final int BROKEN_PLUS=9;
    public static final int BROKEN_MINUS=10;
    public static final int NIL=11;
    public static final int ANNON=12;
    public static final int ROOT=13;
    public static final int PROLOGUE=14;
    public static final int DEFAULT_NAMESPACE=15;
    public static final int NAMESPACE_PREFIX_MAP=16;
    public static final int KEY=17;
    public static final int QUERY=18;
    public static final int UPDATE=19;
    public static final int TYPE=20;
    public static final int PVARS=21;
    public static final int EXP=22;
    public static final int NOT_IN=23;
    public static final int GROUP_GRAPH_PATTERN=24;
    public static final int GROUP_GRAPH_PATTERN_SUB=25;
    public static final int GRAPH_GRAPH_PATTERN=26;
    public static final int SUB_SELECT=27;
    public static final int TRIPLES_BLOCK=28;
    public static final int NON_TRIPLES=29;
    public static final int TRIPLE=30;
    public static final int TRIPLE2=31;
    public static final int TRIPLES_SAME_SUBJECT=32;
    public static final int GRAPH_NODE=33;
    public static final int VAR=34;
    public static final int PREFIXED_NAME=35;
    public static final int PREFIXED_NS=36;
    public static final int FUNCTION=37;
    public static final int EXPRESSION=38;
    public static final int NOT_EXISTS=39;
    public static final int IRI_OR_FUNCTION=40;
    public static final int DATASET=41;
    public static final int GROUP_BY=42;
    public static final int ORDER_BY=43;
    public static final int CONDITION=44;
    public static final int BIND_VALUES=45;
    public static final int STRING=46;
    public static final int BOOLEAN=47;
    public static final int NUMERIC=48;
    public static final int SUBJECT=49;
    public static final int PREDICATE=50;
    public static final int VALUE=51;
    public static final int TRIPLES_NODE_PROPERTY_LIST=52;
    public static final int TRIPLES_NODE=53;
    public static final int COLLECTION=54;
    public static final int PROPERTY_LIST=55;
    public static final int PREDICATE_VALUE=56;
    public static final int WHERE=57;
    public static final int IRI_REF=58;
    public static final int LTE=59;
    public static final int MODIFIERS=60;
    public static final int BIG_INTEGER=61;
    public static final int BIG_DECIMAL=62;
    public static final int INLINE_DATA=63;
    public static final int OUTV=64;
    public static final int FUNCBODY=65;
    public static final int BASE=66;
    public static final int PREFIX=67;
    public static final int PNAME_NS=68;
    public static final int VARNAME=69;
    public static final int OPEN_BRACE=70;
    public static final int ARROW=71;
    public static final int CLOSE_BRACE=72;
    public static final int FUNCLANG=73;
    public static final int OPEN_CURLY_BRACE=74;
    public static final int STRING_LITERAL2=75;
    public static final int CLOSE_CURLY_BRACE=76;
    public static final int SELECT=77;
    public static final int DISTINCT=78;
    public static final int REDUCED=79;
    public static final int AS=80;
    public static final int CONSTRUCT=81;
    public static final int WHERE_TOKEN=82;
    public static final int DESCRIBE=83;
    public static final int ASK=84;
    public static final int FROM=85;
    public static final int NAMED=86;
    public static final int GROUP=87;
    public static final int BY=88;
    public static final int HAVING=89;
    public static final int ORDER=90;
    public static final int ASC=91;
    public static final int DESC=92;
    public static final int LIMIT=93;
    public static final int INTEGER=94;
    public static final int OFFSET=95;
    public static final int BINDINGS=96;
    public static final int UNDEF=97;
    public static final int SEMICOLON=98;
    public static final int LOAD=99;
    public static final int SILENT=100;
    public static final int INTO=101;
    public static final int CLEAR=102;
    public static final int DROP=103;
    public static final int CREATE=104;
    public static final int ADD=105;
    public static final int TO=106;
    public static final int MOVE=107;
    public static final int COPY=108;
    public static final int INSERT=109;
    public static final int DATA=110;
    public static final int DELETE=111;
    public static final int WITH=112;
    public static final int USING=113;
    public static final int DEFAULT=114;
    public static final int GRAPH=115;
    public static final int ALL=116;
    public static final int DOT=117;
    public static final int VALUES=118;
    public static final int OPTIONAL=119;
    public static final int SERVICE=120;
    public static final int BIND=121;
    public static final int UNION=122;
    public static final int MINUS=123;
    public static final int FILTER=124;
    public static final int COMMA=125;
    public static final int OPEN_SQ_BRACKET=126;
    public static final int CLOSE_SQ_BRACKET=127;
    public static final int VAR1=128;
    public static final int VAR2=129;
    public static final int LOGICAL_OR=130;
    public static final int LOGICAL_AND=131;
    public static final int LT=132;
    public static final int IN=133;
    public static final int NOT=134;
    public static final int STR=135;
    public static final int LANG=136;
    public static final int LANGMATCHES=137;
    public static final int DATATYPE=138;
    public static final int BOUND=139;
    public static final int IRI=140;
    public static final int URI=141;
    public static final int BNODE=142;
    public static final int RAND=143;
    public static final int ABS=144;
    public static final int CEIL=145;
    public static final int FLOOR=146;
    public static final int ROUND=147;
    public static final int CONCAT=148;
    public static final int STRLEN=149;
    public static final int UCASE=150;
    public static final int LCASE=151;
    public static final int ENCODE_FOR_URI=152;
    public static final int CONTAINS=153;
    public static final int STRSTARTS=154;
    public static final int STRENDS=155;
    public static final int STRBEFORE=156;
    public static final int STRAFTER=157;
    public static final int YEAR=158;
    public static final int MONTH=159;
    public static final int DAY=160;
    public static final int HOURS=161;
    public static final int MINUTES=162;
    public static final int SECONDS=163;
    public static final int TIMEZONE=164;
    public static final int TZ=165;
    public static final int NOW=166;
    public static final int UUID=167;
    public static final int STRUUID=168;
    public static final int MD5=169;
    public static final int SHA1=170;
    public static final int SHA224=171;
    public static final int SHA256=172;
    public static final int SHA384=173;
    public static final int SHA512=174;
    public static final int COALESCE=175;
    public static final int IF=176;
    public static final int STRLANG=177;
    public static final int STRDT=178;
    public static final int SAMETERM=179;
    public static final int ISIRI=180;
    public static final int ISURI=181;
    public static final int ISBLANK=182;
    public static final int ISLITERAL=183;
    public static final int ISNUMERIC=184;
    public static final int REGEX=185;
    public static final int SUBSTR=186;
    public static final int REPLACE=187;
    public static final int EXISTS=188;
    public static final int COUNT=189;
    public static final int SUM=190;
    public static final int MIN=191;
    public static final int MAX=192;
    public static final int AVG=193;
    public static final int SAMPLE=194;
    public static final int GROUP_CONCAT=195;
    public static final int SEPARATOR=196;
    public static final int LANGTAG=197;
    public static final int DECIMAL=198;
    public static final int DOUBLE=199;
    public static final int INTEGER_POSITIVE=200;
    public static final int DECIMAL_POSITIVE=201;
    public static final int DOUBLE_POSITIVE=202;
    public static final int INTEGER_NEGATIVE=203;
    public static final int DECIMAL_NEGATIVE=204;
    public static final int DOUBLE_NEGATIVE=205;
    public static final int TRUE=206;
    public static final int FALSE=207;
    public static final int STRING_LITERAL1=208;
    public static final int STRING_LITERAL_LONG1=209;
    public static final int STRING_LITERAL_LONG2=210;
    public static final int PNAME_LN=211;
    public static final int BLANK_NODE_LABEL=212;
    public static final int L=213;
    public static final int A=214;
    public static final int N=215;
    public static final int G=216;
    public static final int U=217;
    public static final int E=218;
    public static final int F=219;
    public static final int C=220;
    public static final int FUNCCALL=221;
    public static final int T=222;
    public static final int I=223;
    public static final int O=224;
    public static final int B=225;
    public static final int S=226;
    public static final int P=227;
    public static final int R=228;
    public static final int X=229;
    public static final int D=230;
    public static final int W=231;
    public static final int H=232;
    public static final int K=233;
    public static final int M=234;
    public static final int Y=235;
    public static final int V=236;
    public static final int Z=237;
    public static final int UNICODE_ESCAPE=238;
    public static final int PN_PREFIX=239;
    public static final int PN_LOCAL=240;
    public static final int DIGIT=241;
    public static final int HEXDIGIT=242;
    public static final int EXPONENT=243;
    public static final int ECHAR=244;
    public static final int WS=245;
    public static final int EOL=246;
    public static final int COMMENT=247;
    public static final int PN_CHARS_BASE=248;
    public static final int PN_CHARS_U=249;
    public static final int PN_CHARS=250;
    public static final int PLX=251;
    public static final int PERCENT=252;
    public static final int PN_LOCAL_ESC=253;
    public static final int J=254;
    public static final int Q=255;

    // delegates
    // delegators


        public IbmSparqlExtAstWalker(TreeNodeStream input) {
            this(input, new RecognizerSharedState());
        }
        public IbmSparqlExtAstWalker(TreeNodeStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return IbmSparqlExtAstWalker.tokenNames; }
    public String getGrammarFileName() { return "IbmSparqlExtAstWalker.g"; }


    	protected void mismatch(IntStream input, int ttype, BitSet follow)
    		throws RecognitionException
    	{
    		throw new MismatchedTokenException(ttype, input);
    	}
    	
    	public Object recoverFromMismatchedSet(IntStream input, RecognitionException e, BitSet follow)
    		throws RecognitionException	
    	{
    		throw e;
    	}
    	
    	protected Object recoverFromMismatchedToken(IntStream input, int ttype, BitSet follow)
        	throws RecognitionException
    	{   
        	throw new MismatchedTokenException(ttype, input);
    	}  

        private final Set<BlankNodeVariable> allBlankNodes = HashSetFactory.make();

        private void checkBlankNodes(Set<BlankNodeVariable> blankNodes) {
          // blank node scope is a single BGP
          if (! Collections.disjoint(blankNodes, allBlankNodes)) {
            throw new SPARQLsyntaxError("blank node scope");
          }

          allBlankNodes.addAll(blankNodes);
        }

        private int blankNodeCount = 0;



    // $ANTLR start "queryUnit"
    // IbmSparqlExtAstWalker.g:62:1: queryUnit returns [QueryExt q] : ^( ROOT x= query ) ;
    public final QueryExt queryUnit() throws RecognitionException {
        QueryExt q = null;

        QueryExt x = null;


        try {
            // IbmSparqlExtAstWalker.g:63:2: ( ^( ROOT x= query ) )
            // IbmSparqlExtAstWalker.g:63:8: ^( ROOT x= query )
            {
            match(input,ROOT,FOLLOW_ROOT_in_queryUnit82); if (state.failed) return q;

            match(input, Token.DOWN, null); if (state.failed) return q;
            pushFollow(FOLLOW_query_in_queryUnit86);
            x=query();

            state._fsp--;
            if (state.failed) return q;

            match(input, Token.UP, null); if (state.failed) return q;
            if ( state.backtracking==0 ) {
               q = x; /*System.out.println(q.toString());*/ 
            }

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return q;
    }
    // $ANTLR end "queryUnit"


    // $ANTLR start "query"
    // IbmSparqlExtAstWalker.g:67:1: query returns [QueryExt q] : ^( QUERY (p= prologue ) ( (s= selectQuery (b= bindingsClause )? ) | (c= constructQuery ) | (d= describeQuery ) | (a= askQuery ) ) ) ;
    public final QueryExt query() throws RecognitionException {
        QueryExt q = null;

        QueryPrologue p = null;

        SelectQueryExt s = null;

        Pattern b = null;

        ConstructQuery c = null;

        DescribeQuery d = null;

        AskQuery a = null;



        		
        	
        try {
            // IbmSparqlExtAstWalker.g:71:2: ( ^( QUERY (p= prologue ) ( (s= selectQuery (b= bindingsClause )? ) | (c= constructQuery ) | (d= describeQuery ) | (a= askQuery ) ) ) )
            // IbmSparqlExtAstWalker.g:71:4: ^( QUERY (p= prologue ) ( (s= selectQuery (b= bindingsClause )? ) | (c= constructQuery ) | (d= describeQuery ) | (a= askQuery ) ) )
            {
            match(input,QUERY,FOLLOW_QUERY_in_query116); if (state.failed) return q;

            match(input, Token.DOWN, null); if (state.failed) return q;
            // IbmSparqlExtAstWalker.g:72:4: (p= prologue )
            // IbmSparqlExtAstWalker.g:72:6: p= prologue
            {
            pushFollow(FOLLOW_prologue_in_query126);
            p=prologue();

            state._fsp--;
            if (state.failed) return q;

            }

            // IbmSparqlExtAstWalker.g:73:4: ( (s= selectQuery (b= bindingsClause )? ) | (c= constructQuery ) | (d= describeQuery ) | (a= askQuery ) )
            int alt2=4;
            switch ( input.LA(1) ) {
            case SELECT:
                {
                alt2=1;
                }
                break;
            case CONSTRUCT:
                {
                alt2=2;
                }
                break;
            case DESCRIBE:
                {
                alt2=3;
                }
                break;
            case ASK:
                {
                alt2=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return q;}
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;
            }

            switch (alt2) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:73:6: (s= selectQuery (b= bindingsClause )? )
                    {
                    // IbmSparqlExtAstWalker.g:73:6: (s= selectQuery (b= bindingsClause )? )
                    // IbmSparqlExtAstWalker.g:73:8: s= selectQuery (b= bindingsClause )?
                    {
                    pushFollow(FOLLOW_selectQuery_in_query145);
                    s=selectQuery();

                    state._fsp--;
                    if (state.failed) return q;
                    if ( state.backtracking==0 ) {
                       q = new QueryExt(p,s); 
                    }
                    // IbmSparqlExtAstWalker.g:74:8: (b= bindingsClause )?
                    int alt1=2;
                    int LA1_0 = input.LA(1);

                    if ( (LA1_0==VALUES) ) {
                        alt1=1;
                    }
                    switch (alt1) {
                        case 1 :
                            // IbmSparqlExtAstWalker.g:74:10: b= bindingsClause
                            {
                            pushFollow(FOLLOW_bindingsClause_in_query163);
                            b=bindingsClause();

                            state._fsp--;
                            if (state.failed) return q;
                            if ( state.backtracking==0 ) {
                               
                                                  PatternSet top = new PatternSet();
                                                  top.setTopLevel(true);
                                                  PatternSet oldMain = (PatternSet)q.getMainPattern();
                                                  oldMain.setTopLevel(false);
                                                  top.addPattern(oldMain);
                                                  top.addPattern(b);
                                                  q.setMainPattern(top);
                                                
                            }

                            }
                            break;

                    }


                    }


                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:86:6: (c= constructQuery )
                    {
                    // IbmSparqlExtAstWalker.g:86:6: (c= constructQuery )
                    // IbmSparqlExtAstWalker.g:86:8: c= constructQuery
                    {
                    pushFollow(FOLLOW_constructQuery_in_query220);
                    c=constructQuery();

                    state._fsp--;
                    if (state.failed) return q;
                    if ( state.backtracking==0 ) {
                       q = new QueryExt(p,c); 
                    }

                    }


                    }
                    break;
                case 3 :
                    // IbmSparqlExtAstWalker.g:87:6: (d= describeQuery )
                    {
                    // IbmSparqlExtAstWalker.g:87:6: (d= describeQuery )
                    // IbmSparqlExtAstWalker.g:87:8: d= describeQuery
                    {
                    pushFollow(FOLLOW_describeQuery_in_query236);
                    d=describeQuery();

                    state._fsp--;
                    if (state.failed) return q;
                    if ( state.backtracking==0 ) {
                       q = new QueryExt(p,d); 
                    }

                    }


                    }
                    break;
                case 4 :
                    // IbmSparqlExtAstWalker.g:88:6: (a= askQuery )
                    {
                    // IbmSparqlExtAstWalker.g:88:6: (a= askQuery )
                    // IbmSparqlExtAstWalker.g:88:8: a= askQuery
                    {
                    pushFollow(FOLLOW_askQuery_in_query252);
                    a=askQuery();

                    state._fsp--;
                    if (state.failed) return q;
                    if ( state.backtracking==0 ) {
                       q = new QueryExt(p,a); 
                    }

                    }


                    }
                    break;

            }


            match(input, Token.UP, null); if (state.failed) return q;

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return q;
    }
    // $ANTLR end "query"


    // $ANTLR start "prologue"
    // IbmSparqlExtAstWalker.g:101:1: prologue returns [QueryPrologue qp] : ^( PROLOGUE ( baseDecl[$qp] )* ( prefixDecl[$qp] )* ) ;
    public final QueryPrologue prologue() throws RecognitionException {
        QueryPrologue qp = null;

         qp = new QueryPrologue(); 
        try {
            // IbmSparqlExtAstWalker.g:103:2: ( ^( PROLOGUE ( baseDecl[$qp] )* ( prefixDecl[$qp] )* ) )
            // IbmSparqlExtAstWalker.g:104:3: ^( PROLOGUE ( baseDecl[$qp] )* ( prefixDecl[$qp] )* )
            {
            match(input,PROLOGUE,FOLLOW_PROLOGUE_in_prologue308); if (state.failed) return qp;

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); if (state.failed) return qp;
                // IbmSparqlExtAstWalker.g:104:14: ( baseDecl[$qp] )*
                loop3:
                do {
                    int alt3=2;
                    int LA3_0 = input.LA(1);

                    if ( (LA3_0==BASE) ) {
                        alt3=1;
                    }


                    switch (alt3) {
                	case 1 :
                	    // IbmSparqlExtAstWalker.g:104:14: baseDecl[$qp]
                	    {
                	    pushFollow(FOLLOW_baseDecl_in_prologue310);
                	    baseDecl(qp);

                	    state._fsp--;
                	    if (state.failed) return qp;

                	    }
                	    break;

                	default :
                	    break loop3;
                    }
                } while (true);

                // IbmSparqlExtAstWalker.g:104:30: ( prefixDecl[$qp] )*
                loop4:
                do {
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==PREFIX) ) {
                        alt4=1;
                    }


                    switch (alt4) {
                	case 1 :
                	    // IbmSparqlExtAstWalker.g:104:30: prefixDecl[$qp]
                	    {
                	    pushFollow(FOLLOW_prefixDecl_in_prologue315);
                	    prefixDecl(qp);

                	    state._fsp--;
                	    if (state.failed) return qp;

                	    }
                	    break;

                	default :
                	    break loop4;
                    }
                } while (true);


                match(input, Token.UP, null); if (state.failed) return qp;
            }

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return qp;
    }
    // $ANTLR end "prologue"


    // $ANTLR start "baseDecl"
    // IbmSparqlExtAstWalker.g:108:1: baseDecl[QueryPrologue qp] : ^( BASE i= iRIref ) ;
    public final void baseDecl(QueryPrologue qp) throws RecognitionException {
        IRI i = null;


        try {
            // IbmSparqlExtAstWalker.g:109:2: ( ^( BASE i= iRIref ) )
            // IbmSparqlExtAstWalker.g:110:4: ^( BASE i= iRIref )
            {
            match(input,BASE,FOLLOW_BASE_in_baseDecl346); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            pushFollow(FOLLOW_iRIref_in_baseDecl350);
            i=iRIref();

            state._fsp--;
            if (state.failed) return ;

            match(input, Token.UP, null); if (state.failed) return ;
            if ( state.backtracking==0 ) {
               qp.setBase(i);	
            }

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return ;
    }
    // $ANTLR end "baseDecl"


    // $ANTLR start "prefixDecl"
    // IbmSparqlExtAstWalker.g:114:1: prefixDecl[QueryPrologue qp] : ^( PREFIX n= prefixedName v= iRIref ) ;
    public final void prefixDecl(QueryPrologue qp) throws RecognitionException {
        String n = null;

        IRI v = null;


        try {
            // IbmSparqlExtAstWalker.g:115:2: ( ^( PREFIX n= prefixedName v= iRIref ) )
            // IbmSparqlExtAstWalker.g:116:3: ^( PREFIX n= prefixedName v= iRIref )
            {
            match(input,PREFIX,FOLLOW_PREFIX_in_prefixDecl378); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            pushFollow(FOLLOW_prefixedName_in_prefixDecl382);
            n=prefixedName();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_iRIref_in_prefixDecl387);
            v=iRIref();

            state._fsp--;
            if (state.failed) return ;

            match(input, Token.UP, null); if (state.failed) return ;
            if ( state.backtracking==0 ) {
               qp.getPrefixes().put(n.substring(0,n.length()-1), v);	
            }

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return ;
    }
    // $ANTLR end "prefixDecl"


    // $ANTLR start "selectQuery"
    // IbmSparqlExtAstWalker.g:121:1: selectQuery returns [SelectQueryExt sq] : ^( SELECT (f= functionSet )* (s= selectClause ) (d= dataset )* (w= whereClause )? (m= solutionModifier ) ) ;
    public final SelectQueryExt selectQuery() throws RecognitionException {
        SelectQueryExt sq = null;

        List<FunctionExt> f = null;

        SelectClause s = null;

        List<DatasetClause> d = null;

        Pattern w = null;

        SolutionModifiers m = null;


         sq = new SelectQueryExt(); 
        try {
            // IbmSparqlExtAstWalker.g:123:2: ( ^( SELECT (f= functionSet )* (s= selectClause ) (d= dataset )* (w= whereClause )? (m= solutionModifier ) ) )
            // IbmSparqlExtAstWalker.g:124:3: ^( SELECT (f= functionSet )* (s= selectClause ) (d= dataset )* (w= whereClause )? (m= solutionModifier ) )
            {
            match(input,SELECT,FOLLOW_SELECT_in_selectQuery425); if (state.failed) return sq;

            match(input, Token.DOWN, null); if (state.failed) return sq;
            // IbmSparqlExtAstWalker.g:125:4: (f= functionSet )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==FUNCTION) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // IbmSparqlExtAstWalker.g:125:5: f= functionSet
            	    {
            	    pushFollow(FOLLOW_functionSet_in_selectQuery435);
            	    f=functionSet();

            	    state._fsp--;
            	    if (state.failed) return sq;
            	    if ( state.backtracking==0 ) {
            	       sq.setFunctions(f);    
            	    }

            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);

            // IbmSparqlExtAstWalker.g:126:4: (s= selectClause )
            // IbmSparqlExtAstWalker.g:126:5: s= selectClause
            {
            pushFollow(FOLLOW_selectClause_in_selectQuery453);
            s=selectClause();

            state._fsp--;
            if (state.failed) return sq;
            if ( state.backtracking==0 ) {
               sq.setSelectClause(s);      
            }

            }

            // IbmSparqlExtAstWalker.g:127:4: (d= dataset )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( (LA6_0==DATASET) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // IbmSparqlExtAstWalker.g:127:5: d= dataset
            	    {
            	    pushFollow(FOLLOW_dataset_in_selectQuery469);
            	    d=dataset();

            	    state._fsp--;
            	    if (state.failed) return sq;
            	    if ( state.backtracking==0 ) {
            	       sq.setDatasetClauses(d);    
            	    }

            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);

            // IbmSparqlExtAstWalker.g:128:4: (w= whereClause )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==WHERE) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:128:5: w= whereClause
                    {
                    pushFollow(FOLLOW_whereClause_in_selectQuery488);
                    w=whereClause();

                    state._fsp--;
                    if (state.failed) return sq;
                    if ( state.backtracking==0 ) {
                       sq.setGraphPattern(w);      
                    }

                    }
                    break;

            }

            // IbmSparqlExtAstWalker.g:129:4: (m= solutionModifier )
            // IbmSparqlExtAstWalker.g:129:5: m= solutionModifier
            {
            pushFollow(FOLLOW_solutionModifier_in_selectQuery504);
            m=solutionModifier();

            state._fsp--;
            if (state.failed) return sq;
            if ( state.backtracking==0 ) {
               sq.setSolutionModifier(m);  
            }

            }


            match(input, Token.UP, null); if (state.failed) return sq;

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return sq;
    }
    // $ANTLR end "selectQuery"


    // $ANTLR start "functionSet"
    // IbmSparqlExtAstWalker.g:134:1: functionSet returns [List<FunctionExt> funcs] : ^( FUNCTION (f= functionDecl )+ ) ;
    public final List<FunctionExt> functionSet() throws RecognitionException {
        List<FunctionExt> funcs = null;

        FunctionExt f = null;


         funcs = new ArrayList<FunctionExt>(); 
        try {
            // IbmSparqlExtAstWalker.g:136:2: ( ^( FUNCTION (f= functionDecl )+ ) )
            // IbmSparqlExtAstWalker.g:137:3: ^( FUNCTION (f= functionDecl )+ )
            {
            match(input,FUNCTION,FOLLOW_FUNCTION_in_functionSet545); if (state.failed) return funcs;

            match(input, Token.DOWN, null); if (state.failed) return funcs;
            // IbmSparqlExtAstWalker.g:138:4: (f= functionDecl )+
            int cnt8=0;
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==FUNCTION) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // IbmSparqlExtAstWalker.g:138:5: f= functionDecl
            	    {
            	    pushFollow(FOLLOW_functionDecl_in_functionSet554);
            	    f=functionDecl();

            	    state._fsp--;
            	    if (state.failed) return funcs;
            	    if ( state.backtracking==0 ) {
            	      funcs.add(f);
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt8 >= 1 ) break loop8;
            	    if (state.backtracking>0) {state.failed=true; return funcs;}
                        EarlyExitException eee =
                            new EarlyExitException(8, input);
                        throw eee;
                }
                cnt8++;
            } while (true);


            match(input, Token.UP, null); if (state.failed) return funcs;

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return funcs;
    }
    // $ANTLR end "functionSet"


    // $ANTLR start "functionDecl"
    // IbmSparqlExtAstWalker.g:143:1: functionDecl returns [FunctionExt func] : ^( FUNCTION (fn= VARNAME ) OPEN_BRACE (inv= var )+ ARROW (outv= var )+ CLOSE_BRACE ( ^( FUNCLANG (fl= VARNAME ) (fb= functionBody ) ) ) ) ;
    public final FunctionExt functionDecl() throws RecognitionException {
        FunctionExt func = null;

        XTree fn=null;
        XTree fl=null;
        String inv = null;

        String outv = null;

        FunctionBody fb = null;


         func = new FunctionExt(); 
        try {
            // IbmSparqlExtAstWalker.g:145:2: ( ^( FUNCTION (fn= VARNAME ) OPEN_BRACE (inv= var )+ ARROW (outv= var )+ CLOSE_BRACE ( ^( FUNCLANG (fl= VARNAME ) (fb= functionBody ) ) ) ) )
            // IbmSparqlExtAstWalker.g:146:3: ^( FUNCTION (fn= VARNAME ) OPEN_BRACE (inv= var )+ ARROW (outv= var )+ CLOSE_BRACE ( ^( FUNCLANG (fl= VARNAME ) (fb= functionBody ) ) ) )
            {
            match(input,FUNCTION,FOLLOW_FUNCTION_in_functionDecl590); if (state.failed) return func;

            match(input, Token.DOWN, null); if (state.failed) return func;
            // IbmSparqlExtAstWalker.g:147:4: (fn= VARNAME )
            // IbmSparqlExtAstWalker.g:147:5: fn= VARNAME
            {
            fn=(XTree)match(input,VARNAME,FOLLOW_VARNAME_in_functionDecl598); if (state.failed) return func;
            if ( state.backtracking==0 ) {
               func.setName(fn); 
            }

            }

            match(input,OPEN_BRACE,FOLLOW_OPEN_BRACE_in_functionDecl607); if (state.failed) return func;
            // IbmSparqlExtAstWalker.g:149:4: (inv= var )+
            int cnt9=0;
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==VAR) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // IbmSparqlExtAstWalker.g:149:5: inv= var
            	    {
            	    pushFollow(FOLLOW_var_in_functionDecl615);
            	    inv=var();

            	    state._fsp--;
            	    if (state.failed) return func;
            	    if ( state.backtracking==0 ) {
            	       func.addInVar(inv); 
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt9 >= 1 ) break loop9;
            	    if (state.backtracking>0) {state.failed=true; return func;}
                        EarlyExitException eee =
                            new EarlyExitException(9, input);
                        throw eee;
                }
                cnt9++;
            } while (true);

            match(input,ARROW,FOLLOW_ARROW_in_functionDecl625); if (state.failed) return func;
            // IbmSparqlExtAstWalker.g:151:4: (outv= var )+
            int cnt10=0;
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==VAR) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // IbmSparqlExtAstWalker.g:151:5: outv= var
            	    {
            	    pushFollow(FOLLOW_var_in_functionDecl633);
            	    outv=var();

            	    state._fsp--;
            	    if (state.failed) return func;
            	    if ( state.backtracking==0 ) {
            	       func.addOutVar(outv); 
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt10 >= 1 ) break loop10;
            	    if (state.backtracking>0) {state.failed=true; return func;}
                        EarlyExitException eee =
                            new EarlyExitException(10, input);
                        throw eee;
                }
                cnt10++;
            } while (true);

            match(input,CLOSE_BRACE,FOLLOW_CLOSE_BRACE_in_functionDecl644); if (state.failed) return func;
            // IbmSparqlExtAstWalker.g:153:4: ( ^( FUNCLANG (fl= VARNAME ) (fb= functionBody ) ) )
            // IbmSparqlExtAstWalker.g:153:5: ^( FUNCLANG (fl= VARNAME ) (fb= functionBody ) )
            {
            match(input,FUNCLANG,FOLLOW_FUNCLANG_in_functionDecl651); if (state.failed) return func;

            match(input, Token.DOWN, null); if (state.failed) return func;
            // IbmSparqlExtAstWalker.g:154:5: (fl= VARNAME )
            // IbmSparqlExtAstWalker.g:154:6: fl= VARNAME
            {
            fl=(XTree)match(input,VARNAME,FOLLOW_VARNAME_in_functionDecl661); if (state.failed) return func;
            if ( state.backtracking==0 ) {
               func.setLang(fl); 
            }

            }

            // IbmSparqlExtAstWalker.g:155:5: (fb= functionBody )
            // IbmSparqlExtAstWalker.g:155:6: fb= functionBody
            {
            pushFollow(FOLLOW_functionBody_in_functionDecl674);
            fb=functionBody();

            state._fsp--;
            if (state.failed) return func;
            if ( state.backtracking==0 ) {
               func.setBody(fb); 
            }

            }


            match(input, Token.UP, null); if (state.failed) return func;

            }


            match(input, Token.UP, null); if (state.failed) return func;

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return func;
    }
    // $ANTLR end "functionDecl"


    // $ANTLR start "functionBody"
    // IbmSparqlExtAstWalker.g:162:1: functionBody returns [FunctionBody fb] : ^( FUNCBODY ( (f= STRING_LITERAL2 ) | (p= groupGraphPattern[true] ) ) ) ;
    public final FunctionBody functionBody() throws RecognitionException {
        FunctionBody fb = null;

        XTree f=null;
        Pattern p = null;


         fb = new FunctionBody(); 
        try {
            // IbmSparqlExtAstWalker.g:164:2: ( ^( FUNCBODY ( (f= STRING_LITERAL2 ) | (p= groupGraphPattern[true] ) ) ) )
            // IbmSparqlExtAstWalker.g:165:4: ^( FUNCBODY ( (f= STRING_LITERAL2 ) | (p= groupGraphPattern[true] ) ) )
            {
            match(input,FUNCBODY,FOLLOW_FUNCBODY_in_functionBody720); if (state.failed) return fb;

            match(input, Token.DOWN, null); if (state.failed) return fb;
            // IbmSparqlExtAstWalker.g:166:5: ( (f= STRING_LITERAL2 ) | (p= groupGraphPattern[true] ) )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==STRING_LITERAL2) ) {
                alt11=1;
            }
            else if ( (LA11_0==GROUP_GRAPH_PATTERN||LA11_0==SUB_SELECT) ) {
                alt11=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return fb;}
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:167:5: (f= STRING_LITERAL2 )
                    {
                    // IbmSparqlExtAstWalker.g:167:5: (f= STRING_LITERAL2 )
                    // IbmSparqlExtAstWalker.g:167:6: f= STRING_LITERAL2
                    {
                    f=(XTree)match(input,STRING_LITERAL2,FOLLOW_STRING_LITERAL2_in_functionBody735); if (state.failed) return fb;
                    if ( state.backtracking==0 ) {
                      fb.setFlag(0); fb.setBody(f); 
                    }

                    }


                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:169:5: (p= groupGraphPattern[true] )
                    {
                    // IbmSparqlExtAstWalker.g:169:5: (p= groupGraphPattern[true] )
                    // IbmSparqlExtAstWalker.g:169:6: p= groupGraphPattern[true]
                    {
                    pushFollow(FOLLOW_groupGraphPattern_in_functionBody755);
                    p=groupGraphPattern(true);

                    state._fsp--;
                    if (state.failed) return fb;
                    if ( state.backtracking==0 ) {
                      fb.setFlag(1); fb.setBody(p); 
                    }

                    }


                    }
                    break;

            }


            match(input, Token.UP, null); if (state.failed) return fb;

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return fb;
    }
    // $ANTLR end "functionBody"


    // $ANTLR start "dataset"
    // IbmSparqlExtAstWalker.g:174:1: dataset returns [List<DatasetClause> dcl] : ^( DATASET (dc= datasetClause )+ ) ;
    public final List<DatasetClause> dataset() throws RecognitionException {
        List<DatasetClause> dcl = null;

        DatasetClause dc = null;


         dcl = new ArrayList<DatasetClause>(); 
        try {
            // IbmSparqlExtAstWalker.g:176:2: ( ^( DATASET (dc= datasetClause )+ ) )
            // IbmSparqlExtAstWalker.g:177:3: ^( DATASET (dc= datasetClause )+ )
            {
            match(input,DATASET,FOLLOW_DATASET_in_dataset798); if (state.failed) return dcl;

            match(input, Token.DOWN, null); if (state.failed) return dcl;
            // IbmSparqlExtAstWalker.g:178:4: (dc= datasetClause )+
            int cnt12=0;
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( (LA12_0==FROM) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // IbmSparqlExtAstWalker.g:178:5: dc= datasetClause
            	    {
            	    pushFollow(FOLLOW_datasetClause_in_dataset807);
            	    dc=datasetClause();

            	    state._fsp--;
            	    if (state.failed) return dcl;
            	    if ( state.backtracking==0 ) {
            	      dcl.add(dc);
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt12 >= 1 ) break loop12;
            	    if (state.backtracking>0) {state.failed=true; return dcl;}
                        EarlyExitException eee =
                            new EarlyExitException(12, input);
                        throw eee;
                }
                cnt12++;
            } while (true);


            match(input, Token.UP, null); if (state.failed) return dcl;

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return dcl;
    }
    // $ANTLR end "dataset"


    // $ANTLR start "subSelect"
    // IbmSparqlExtAstWalker.g:182:1: subSelect returns [SubSelectPattern sp] : ^( SUB_SELECT (s= selectClause ) (w= whereClause )? (m= solutionModifier ) (d= inlineData )? ) ;
    public final SubSelectPattern subSelect() throws RecognitionException {
        SubSelectPattern sp = null;

        SelectClause s = null;

        Pattern w = null;

        SolutionModifiers m = null;

        ValuesPattern d = null;


         
        		sp = new SubSelectPattern();
        	
        try {
            // IbmSparqlExtAstWalker.g:186:2: ( ^( SUB_SELECT (s= selectClause ) (w= whereClause )? (m= solutionModifier ) (d= inlineData )? ) )
            // IbmSparqlExtAstWalker.g:186:6: ^( SUB_SELECT (s= selectClause ) (w= whereClause )? (m= solutionModifier ) (d= inlineData )? )
            {
            match(input,SUB_SELECT,FOLLOW_SUB_SELECT_in_subSelect841); if (state.failed) return sp;

            match(input, Token.DOWN, null); if (state.failed) return sp;
            // IbmSparqlExtAstWalker.g:187:4: (s= selectClause )
            // IbmSparqlExtAstWalker.g:187:5: s= selectClause
            {
            pushFollow(FOLLOW_selectClause_in_subSelect850);
            s=selectClause();

            state._fsp--;
            if (state.failed) return sp;
            if ( state.backtracking==0 ) {
               sp.setSelectClause(s);      
            }

            }

            // IbmSparqlExtAstWalker.g:188:4: (w= whereClause )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==WHERE) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:188:5: w= whereClause
                    {
                    pushFollow(FOLLOW_whereClause_in_subSelect866);
                    w=whereClause();

                    state._fsp--;
                    if (state.failed) return sp;
                    if ( state.backtracking==0 ) {
                       sp.setGraphPattern(w);      
                    }

                    }
                    break;

            }

            // IbmSparqlExtAstWalker.g:189:4: (m= solutionModifier )
            // IbmSparqlExtAstWalker.g:189:5: m= solutionModifier
            {
            pushFollow(FOLLOW_solutionModifier_in_subSelect882);
            m=solutionModifier();

            state._fsp--;
            if (state.failed) return sp;
            if ( state.backtracking==0 ) {
               sp.setSolutionModifier(m);  
            }

            }

            // IbmSparqlExtAstWalker.g:190:13: (d= inlineData )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==VALUES) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:190:14: d= inlineData
                    {
                    pushFollow(FOLLOW_inlineData_in_subSelect908);
                    d=inlineData();

                    state._fsp--;
                    if (state.failed) return sp;
                    if ( state.backtracking==0 ) {
                       ((PatternSet)sp.getPattern()).addPattern(d); 
                    }

                    }
                    break;

            }


            match(input, Token.UP, null); if (state.failed) return sp;

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return sp;
    }
    // $ANTLR end "subSelect"


    // $ANTLR start "selectClause"
    // IbmSparqlExtAstWalker.g:194:1: selectClause returns [SelectClause sc] : ( ^( TYPE ( DISTINCT | REDUCED ) ) )? ^( PVARS ( (v= var | expVar[$sc] | fexp[$sc] )* | '*' ) ) ;
    public final SelectClause selectClause() throws RecognitionException {
        SelectClause sc = null;

        String v = null;


         sc = new SelectClause(); 
        try {
            // IbmSparqlExtAstWalker.g:196:2: ( ( ^( TYPE ( DISTINCT | REDUCED ) ) )? ^( PVARS ( (v= var | expVar[$sc] | fexp[$sc] )* | '*' ) ) )
            // IbmSparqlExtAstWalker.g:197:3: ( ^( TYPE ( DISTINCT | REDUCED ) ) )? ^( PVARS ( (v= var | expVar[$sc] | fexp[$sc] )* | '*' ) )
            {
            // IbmSparqlExtAstWalker.g:197:3: ( ^( TYPE ( DISTINCT | REDUCED ) ) )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==TYPE) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:197:4: ^( TYPE ( DISTINCT | REDUCED ) )
                    {
                    match(input,TYPE,FOLLOW_TYPE_in_selectClause954); if (state.failed) return sc;

                    match(input, Token.DOWN, null); if (state.failed) return sc;
                    // IbmSparqlExtAstWalker.g:197:11: ( DISTINCT | REDUCED )
                    int alt15=2;
                    int LA15_0 = input.LA(1);

                    if ( (LA15_0==DISTINCT) ) {
                        alt15=1;
                    }
                    else if ( (LA15_0==REDUCED) ) {
                        alt15=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return sc;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 15, 0, input);

                        throw nvae;
                    }
                    switch (alt15) {
                        case 1 :
                            // IbmSparqlExtAstWalker.g:197:13: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_selectClause958); if (state.failed) return sc;
                            if ( state.backtracking==0 ) {
                               sc.setSelectModifier(SelectClause.ESelectModifier.DISTINCT); 
                            }

                            }
                            break;
                        case 2 :
                            // IbmSparqlExtAstWalker.g:198:7: REDUCED
                            {
                            match(input,REDUCED,FOLLOW_REDUCED_in_selectClause970); if (state.failed) return sc;
                            if ( state.backtracking==0 ) {
                               sc.setSelectModifier(SelectClause.ESelectModifier.REDUCED);  
                            }

                            }
                            break;

                    }


                    match(input, Token.UP, null); if (state.failed) return sc;

                    }
                    break;

            }

            match(input,PVARS,FOLLOW_PVARS_in_selectClause999); if (state.failed) return sc;

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); if (state.failed) return sc;
                // IbmSparqlExtAstWalker.g:202:11: ( (v= var | expVar[$sc] | fexp[$sc] )* | '*' )
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( (LA18_0==UP||LA18_0==EXP||LA18_0==VAR||LA18_0==AS) ) {
                    alt18=1;
                }
                else if ( (LA18_0==256) ) {
                    alt18=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return sc;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 18, 0, input);

                    throw nvae;
                }
                switch (alt18) {
                    case 1 :
                        // IbmSparqlExtAstWalker.g:202:13: (v= var | expVar[$sc] | fexp[$sc] )*
                        {
                        // IbmSparqlExtAstWalker.g:202:13: (v= var | expVar[$sc] | fexp[$sc] )*
                        loop17:
                        do {
                            int alt17=4;
                            switch ( input.LA(1) ) {
                            case VAR:
                                {
                                alt17=1;
                                }
                                break;
                            case AS:
                                {
                                alt17=2;
                                }
                                break;
                            case EXP:
                                {
                                alt17=3;
                                }
                                break;

                            }

                            switch (alt17) {
                        	case 1 :
                        	    // IbmSparqlExtAstWalker.g:202:15: v= var
                        	    {
                        	    pushFollow(FOLLOW_var_in_selectClause1007);
                        	    v=var();

                        	    state._fsp--;
                        	    if (state.failed) return sc;
                        	    if ( state.backtracking==0 ) {
                        	       sc.addProjectedVariable(new ProjectedVariable(v)); 
                        	    }

                        	    }
                        	    break;
                        	case 2 :
                        	    // IbmSparqlExtAstWalker.g:203:15: expVar[$sc]
                        	    {
                        	    pushFollow(FOLLOW_expVar_in_selectClause1029);
                        	    expVar(sc);

                        	    state._fsp--;
                        	    if (state.failed) return sc;

                        	    }
                        	    break;
                        	case 3 :
                        	    // IbmSparqlExtAstWalker.g:204:15: fexp[$sc]
                        	    {
                        	    pushFollow(FOLLOW_fexp_in_selectClause1052);
                        	    fexp(sc);

                        	    state._fsp--;
                        	    if (state.failed) return sc;

                        	    }
                        	    break;

                        	default :
                        	    break loop17;
                            }
                        } while (true);


                        }
                        break;
                    case 2 :
                        // IbmSparqlExtAstWalker.g:206:13: '*'
                        {
                        match(input,256,FOLLOW_256_in_selectClause1092); if (state.failed) return sc;

                        }
                        break;

                }


                match(input, Token.UP, null); if (state.failed) return sc;
            }

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return sc;
    }
    // $ANTLR end "selectClause"


    // $ANTLR start "expVar"
    // IbmSparqlExtAstWalker.g:211:1: expVar[SelectClause sc] : ^( AS v= var e= expression ) ;
    public final void expVar(SelectClause sc) throws RecognitionException {
        String v = null;

        Expression e = null;


        try {
            // IbmSparqlExtAstWalker.g:212:2: ( ^( AS v= var e= expression ) )
            // IbmSparqlExtAstWalker.g:212:4: ^( AS v= var e= expression )
            {
            match(input,AS,FOLLOW_AS_in_expVar1123); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            pushFollow(FOLLOW_var_in_expVar1127);
            v=var();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_expression_in_expVar1131);
            e=expression();

            state._fsp--;
            if (state.failed) return ;

            match(input, Token.UP, null); if (state.failed) return ;
            if ( state.backtracking==0 ) {
               sc.addProjectedVariable(new ProjectedVariable(v, e)); 
            }

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return ;
    }
    // $ANTLR end "expVar"


    // $ANTLR start "fexp"
    // IbmSparqlExtAstWalker.g:215:1: fexp[SelectClause sc] : ^( EXP e= expression ) ;
    public final void fexp(SelectClause sc) throws RecognitionException {
        Expression e = null;


        try {
            // IbmSparqlExtAstWalker.g:216:2: ( ^( EXP e= expression ) )
            // IbmSparqlExtAstWalker.g:216:4: ^( EXP e= expression )
            {
            match(input,EXP,FOLLOW_EXP_in_fexp1149); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            pushFollow(FOLLOW_expression_in_fexp1153);
            e=expression();

            state._fsp--;
            if (state.failed) return ;

            match(input, Token.UP, null); if (state.failed) return ;
            if ( state.backtracking==0 ) {
               sc.addProjectedVariable(new ProjectedVariable(e)); 
            }

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return ;
    }
    // $ANTLR end "fexp"


    // $ANTLR start "constructQuery"
    // IbmSparqlExtAstWalker.g:219:1: constructQuery returns [ConstructQuery cq] : ^( CONSTRUCT ( ( constructTemplate[$cq] (d= dataset )* (w= whereClause ) (m= solutionModifier ) ) | ( (d= dataset )* ( ^( WHERE triplesTemplate[$cq,p] ) )? (m= solutionModifier ) ) ) ) ;
    public final ConstructQuery constructQuery() throws RecognitionException {
        ConstructQuery cq = null;

        List<DatasetClause> d = null;

        Pattern w = null;

        SolutionModifiers m = null;


         
                PatternSet p = null;
        		cq = new ConstructQuery();
        	
        try {
            // IbmSparqlExtAstWalker.g:224:2: ( ^( CONSTRUCT ( ( constructTemplate[$cq] (d= dataset )* (w= whereClause ) (m= solutionModifier ) ) | ( (d= dataset )* ( ^( WHERE triplesTemplate[$cq,p] ) )? (m= solutionModifier ) ) ) ) )
            // IbmSparqlExtAstWalker.g:224:6: ^( CONSTRUCT ( ( constructTemplate[$cq] (d= dataset )* (w= whereClause ) (m= solutionModifier ) ) | ( (d= dataset )* ( ^( WHERE triplesTemplate[$cq,p] ) )? (m= solutionModifier ) ) ) )
            {
            match(input,CONSTRUCT,FOLLOW_CONSTRUCT_in_constructQuery1184); if (state.failed) return cq;

            match(input, Token.DOWN, null); if (state.failed) return cq;
            // IbmSparqlExtAstWalker.g:224:20: ( ( constructTemplate[$cq] (d= dataset )* (w= whereClause ) (m= solutionModifier ) ) | ( (d= dataset )* ( ^( WHERE triplesTemplate[$cq,p] ) )? (m= solutionModifier ) ) )
            int alt22=2;
            alt22 = dfa22.predict(input);
            switch (alt22) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:224:22: ( constructTemplate[$cq] (d= dataset )* (w= whereClause ) (m= solutionModifier ) )
                    {
                    // IbmSparqlExtAstWalker.g:224:22: ( constructTemplate[$cq] (d= dataset )* (w= whereClause ) (m= solutionModifier ) )
                    // IbmSparqlExtAstWalker.g:224:24: constructTemplate[$cq] (d= dataset )* (w= whereClause ) (m= solutionModifier )
                    {
                    pushFollow(FOLLOW_constructTemplate_in_constructQuery1191);
                    constructTemplate(cq);

                    state._fsp--;
                    if (state.failed) return cq;
                    // IbmSparqlExtAstWalker.g:225:8: (d= dataset )*
                    loop19:
                    do {
                        int alt19=2;
                        int LA19_0 = input.LA(1);

                        if ( (LA19_0==DATASET) ) {
                            alt19=1;
                        }


                        switch (alt19) {
                    	case 1 :
                    	    // IbmSparqlExtAstWalker.g:225:9: d= dataset
                    	    {
                    	    pushFollow(FOLLOW_dataset_in_constructQuery1205);
                    	    d=dataset();

                    	    state._fsp--;
                    	    if (state.failed) return cq;
                    	    if ( state.backtracking==0 ) {
                    	       cq.setDatasetClauses(d);    
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop19;
                        }
                    } while (true);

                    // IbmSparqlExtAstWalker.g:226:8: (w= whereClause )
                    // IbmSparqlExtAstWalker.g:226:9: w= whereClause
                    {
                    pushFollow(FOLLOW_whereClause_in_constructQuery1228);
                    w=whereClause();

                    state._fsp--;
                    if (state.failed) return cq;
                    if ( state.backtracking==0 ) {
                       cq.setPattern(w);           
                    }

                    }

                    // IbmSparqlExtAstWalker.g:227:8: (m= solutionModifier )
                    // IbmSparqlExtAstWalker.g:227:9: m= solutionModifier
                    {
                    pushFollow(FOLLOW_solutionModifier_in_constructQuery1247);
                    m=solutionModifier();

                    state._fsp--;
                    if (state.failed) return cq;
                    if ( state.backtracking==0 ) {
                       cq.setSolutionModifier(m);  
                    }

                    }


                    }


                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:230:7: ( (d= dataset )* ( ^( WHERE triplesTemplate[$cq,p] ) )? (m= solutionModifier ) )
                    {
                    // IbmSparqlExtAstWalker.g:230:7: ( (d= dataset )* ( ^( WHERE triplesTemplate[$cq,p] ) )? (m= solutionModifier ) )
                    // IbmSparqlExtAstWalker.g:230:9: (d= dataset )* ( ^( WHERE triplesTemplate[$cq,p] ) )? (m= solutionModifier )
                    {
                    // IbmSparqlExtAstWalker.g:230:9: (d= dataset )*
                    loop20:
                    do {
                        int alt20=2;
                        int LA20_0 = input.LA(1);

                        if ( (LA20_0==DATASET) ) {
                            alt20=1;
                        }


                        switch (alt20) {
                    	case 1 :
                    	    // IbmSparqlExtAstWalker.g:230:10: d= dataset
                    	    {
                    	    pushFollow(FOLLOW_dataset_in_constructQuery1287);
                    	    d=dataset();

                    	    state._fsp--;
                    	    if (state.failed) return cq;
                    	    if ( state.backtracking==0 ) {
                    	       cq.setDatasetClauses(d);    
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop20;
                        }
                    } while (true);

                    // IbmSparqlExtAstWalker.g:231:8: ( ^( WHERE triplesTemplate[$cq,p] ) )?
                    int alt21=2;
                    int LA21_0 = input.LA(1);

                    if ( (LA21_0==WHERE) ) {
                        alt21=1;
                    }
                    switch (alt21) {
                        case 1 :
                            // IbmSparqlExtAstWalker.g:231:9: ^( WHERE triplesTemplate[$cq,p] )
                            {
                            match(input,WHERE,FOLLOW_WHERE_in_constructQuery1309); if (state.failed) return cq;

                            if ( state.backtracking==0 ) {
                                    
                                                               p = new PatternSet();
                                                             
                            }

                            match(input, Token.DOWN, null); if (state.failed) return cq;
                            pushFollow(FOLLOW_triplesTemplate_in_constructQuery1375);
                            triplesTemplate(cq, p);

                            state._fsp--;
                            if (state.failed) return cq;
                            if ( state.backtracking==0 ) {

                                                               
                                                               cq.setPattern(p);
                                                             
                            }

                            match(input, Token.UP, null); if (state.failed) return cq;

                            }
                            break;

                    }

                    // IbmSparqlExtAstWalker.g:241:8: (m= solutionModifier )
                    // IbmSparqlExtAstWalker.g:241:9: m= solutionModifier
                    {
                    pushFollow(FOLLOW_solutionModifier_in_constructQuery1455);
                    m=solutionModifier();

                    state._fsp--;
                    if (state.failed) return cq;
                    if ( state.backtracking==0 ) {
                       cq.setSolutionModifier(m);  
                    }

                    }


                    }


                    }
                    break;

            }


            match(input, Token.UP, null); if (state.failed) return cq;

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return cq;
    }
    // $ANTLR end "constructQuery"


    // $ANTLR start "describeQuery"
    // IbmSparqlExtAstWalker.g:247:1: describeQuery returns [DescribeQuery dq] : ^( DESCRIBE ( ( (v= varOrIRIref2 )+ | '*' ) (d= datasetClause )* (w= whereClause )? (s= solutionModifier ) ) ) ;
    public final DescribeQuery describeQuery() throws RecognitionException {
        DescribeQuery dq = null;

        BinaryUnion<Variable, IRI> v = null;

        DatasetClause d = null;

        Pattern w = null;

        SolutionModifiers s = null;


         
        		dq = new DescribeQuery();
        	
        try {
            // IbmSparqlExtAstWalker.g:251:2: ( ^( DESCRIBE ( ( (v= varOrIRIref2 )+ | '*' ) (d= datasetClause )* (w= whereClause )? (s= solutionModifier ) ) ) )
            // IbmSparqlExtAstWalker.g:251:6: ^( DESCRIBE ( ( (v= varOrIRIref2 )+ | '*' ) (d= datasetClause )* (w= whereClause )? (s= solutionModifier ) ) )
            {
            match(input,DESCRIBE,FOLLOW_DESCRIBE_in_describeQuery1513); if (state.failed) return dq;

            match(input, Token.DOWN, null); if (state.failed) return dq;
            // IbmSparqlExtAstWalker.g:251:18: ( ( (v= varOrIRIref2 )+ | '*' ) (d= datasetClause )* (w= whereClause )? (s= solutionModifier ) )
            // IbmSparqlExtAstWalker.g:251:20: ( (v= varOrIRIref2 )+ | '*' ) (d= datasetClause )* (w= whereClause )? (s= solutionModifier )
            {
            // IbmSparqlExtAstWalker.g:251:20: ( (v= varOrIRIref2 )+ | '*' )
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( ((LA24_0>=VAR && LA24_0<=PREFIXED_NS)||LA24_0==IRI) ) {
                alt24=1;
            }
            else if ( (LA24_0==256) ) {
                alt24=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return dq;}
                NoViableAltException nvae =
                    new NoViableAltException("", 24, 0, input);

                throw nvae;
            }
            switch (alt24) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:251:22: (v= varOrIRIref2 )+
                    {
                    // IbmSparqlExtAstWalker.g:251:22: (v= varOrIRIref2 )+
                    int cnt23=0;
                    loop23:
                    do {
                        int alt23=2;
                        int LA23_0 = input.LA(1);

                        if ( ((LA23_0>=VAR && LA23_0<=PREFIXED_NS)||LA23_0==IRI) ) {
                            alt23=1;
                        }


                        switch (alt23) {
                    	case 1 :
                    	    // IbmSparqlExtAstWalker.g:251:23: v= varOrIRIref2
                    	    {
                    	    pushFollow(FOLLOW_varOrIRIref2_in_describeQuery1522);
                    	    v=varOrIRIref2();

                    	    state._fsp--;
                    	    if (state.failed) return dq;
                    	    if ( state.backtracking==0 ) {
                    	      dq.getResources().add(v);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt23 >= 1 ) break loop23;
                    	    if (state.backtracking>0) {state.failed=true; return dq;}
                                EarlyExitException eee =
                                    new EarlyExitException(23, input);
                                throw eee;
                        }
                        cnt23++;
                    } while (true);


                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:251:74: '*'
                    {
                    match(input,256,FOLLOW_256_in_describeQuery1533); if (state.failed) return dq;

                    }
                    break;

            }

            // IbmSparqlExtAstWalker.g:252:8: (d= datasetClause )*
            loop25:
            do {
                int alt25=2;
                int LA25_0 = input.LA(1);

                if ( (LA25_0==FROM) ) {
                    alt25=1;
                }


                switch (alt25) {
            	case 1 :
            	    // IbmSparqlExtAstWalker.g:252:10: d= datasetClause
            	    {
            	    pushFollow(FOLLOW_datasetClause_in_describeQuery1548);
            	    d=datasetClause();

            	    state._fsp--;
            	    if (state.failed) return dq;
            	    if ( state.backtracking==0 ) {
            	      dq.getDatasetClauses().add(d);
            	    }

            	    }
            	    break;

            	default :
            	    break loop25;
                }
            } while (true);

            // IbmSparqlExtAstWalker.g:253:8: (w= whereClause )?
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==WHERE) ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:253:10: w= whereClause
                    {
                    pushFollow(FOLLOW_whereClause_in_describeQuery1569);
                    w=whereClause();

                    state._fsp--;
                    if (state.failed) return dq;
                    if ( state.backtracking==0 ) {
                      dq.setPattern(w);	
                    }

                    }
                    break;

            }

            // IbmSparqlExtAstWalker.g:254:8: (s= solutionModifier )
            // IbmSparqlExtAstWalker.g:254:10: s= solutionModifier
            {
            pushFollow(FOLLOW_solutionModifier_in_describeQuery1589);
            s=solutionModifier();

            state._fsp--;
            if (state.failed) return dq;
            if ( state.backtracking==0 ) {
              dq.setSolutionModifier(s); 
            }

            }


            }


            match(input, Token.UP, null); if (state.failed) return dq;

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return dq;
    }
    // $ANTLR end "describeQuery"


    // $ANTLR start "askQuery"
    // IbmSparqlExtAstWalker.g:259:1: askQuery returns [AskQuery aq] : ^( ASK ( (d= datasetClause )* (w= whereClause ) ) ) ;
    public final AskQuery askQuery() throws RecognitionException {
        AskQuery aq = null;

        DatasetClause d = null;

        Pattern w = null;



        		ArrayList<DatasetClause> dsl = new ArrayList<DatasetClause>();
        	
        try {
            // IbmSparqlExtAstWalker.g:263:2: ( ^( ASK ( (d= datasetClause )* (w= whereClause ) ) ) )
            // IbmSparqlExtAstWalker.g:263:6: ^( ASK ( (d= datasetClause )* (w= whereClause ) ) )
            {
            match(input,ASK,FOLLOW_ASK_in_askQuery1630); if (state.failed) return aq;

            match(input, Token.DOWN, null); if (state.failed) return aq;
            // IbmSparqlExtAstWalker.g:263:13: ( (d= datasetClause )* (w= whereClause ) )
            // IbmSparqlExtAstWalker.g:263:15: (d= datasetClause )* (w= whereClause )
            {
            // IbmSparqlExtAstWalker.g:263:15: (d= datasetClause )*
            loop27:
            do {
                int alt27=2;
                int LA27_0 = input.LA(1);

                if ( (LA27_0==FROM) ) {
                    alt27=1;
                }


                switch (alt27) {
            	case 1 :
            	    // IbmSparqlExtAstWalker.g:263:16: d= datasetClause
            	    {
            	    pushFollow(FOLLOW_datasetClause_in_askQuery1638);
            	    d=datasetClause();

            	    state._fsp--;
            	    if (state.failed) return aq;
            	    if ( state.backtracking==0 ) {
            	       dsl.add(d); 
            	    }

            	    }
            	    break;

            	default :
            	    break loop27;
                }
            } while (true);

            // IbmSparqlExtAstWalker.g:264:6: (w= whereClause )
            // IbmSparqlExtAstWalker.g:264:7: w= whereClause
            {
            pushFollow(FOLLOW_whereClause_in_askQuery1654);
            w=whereClause();

            state._fsp--;
            if (state.failed) return aq;

            }

            if ( state.backtracking==0 ) {
               aq = new AskQuery(dsl, w); 
            }

            }


            match(input, Token.UP, null); if (state.failed) return aq;

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return aq;
    }
    // $ANTLR end "askQuery"


    // $ANTLR start "datasetClause"
    // IbmSparqlExtAstWalker.g:269:1: datasetClause returns [DatasetClause dc] : ^( FROM (d= defaultGraphClause | n= namedGraphClause ) ) ;
    public final DatasetClause datasetClause() throws RecognitionException {
        DatasetClause dc = null;

        DatasetClause d = null;

        DatasetClause n = null;


        try {
            // IbmSparqlExtAstWalker.g:270:2: ( ^( FROM (d= defaultGraphClause | n= namedGraphClause ) ) )
            // IbmSparqlExtAstWalker.g:270:4: ^( FROM (d= defaultGraphClause | n= namedGraphClause ) )
            {
            match(input,FROM,FOLLOW_FROM_in_datasetClause1689); if (state.failed) return dc;

            match(input, Token.DOWN, null); if (state.failed) return dc;
            // IbmSparqlExtAstWalker.g:270:12: (d= defaultGraphClause | n= namedGraphClause )
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( ((LA28_0>=PREFIXED_NAME && LA28_0<=PREFIXED_NS)||LA28_0==IRI) ) {
                alt28=1;
            }
            else if ( (LA28_0==NAMED) ) {
                alt28=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return dc;}
                NoViableAltException nvae =
                    new NoViableAltException("", 28, 0, input);

                throw nvae;
            }
            switch (alt28) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:270:14: d= defaultGraphClause
                    {
                    pushFollow(FOLLOW_defaultGraphClause_in_datasetClause1696);
                    d=defaultGraphClause();

                    state._fsp--;
                    if (state.failed) return dc;
                    if ( state.backtracking==0 ) {
                       dc = d; 
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:271:7: n= namedGraphClause
                    {
                    pushFollow(FOLLOW_namedGraphClause_in_datasetClause1710);
                    n=namedGraphClause();

                    state._fsp--;
                    if (state.failed) return dc;
                    if ( state.backtracking==0 ) {
                       dc = n; 
                    }

                    }
                    break;

            }


            match(input, Token.UP, null); if (state.failed) return dc;

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return dc;
    }
    // $ANTLR end "datasetClause"


    // $ANTLR start "defaultGraphClause"
    // IbmSparqlExtAstWalker.g:276:1: defaultGraphClause returns [DatasetClause dc] : d= sourceSelector ;
    public final DatasetClause defaultGraphClause() throws RecognitionException {
        DatasetClause dc = null;

        IRI d = null;


        try {
            // IbmSparqlExtAstWalker.g:277:2: (d= sourceSelector )
            // IbmSparqlExtAstWalker.g:277:6: d= sourceSelector
            {
            pushFollow(FOLLOW_sourceSelector_in_defaultGraphClause1743);
            d=sourceSelector();

            state._fsp--;
            if (state.failed) return dc;
            if ( state.backtracking==0 ) {
               dc = new DatasetClause(d); 
            }

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return dc;
    }
    // $ANTLR end "defaultGraphClause"


    // $ANTLR start "namedGraphClause"
    // IbmSparqlExtAstWalker.g:280:1: namedGraphClause returns [DatasetClause dc] : ^( NAMED d= sourceSelector ) ;
    public final DatasetClause namedGraphClause() throws RecognitionException {
        DatasetClause dc = null;

        IRI d = null;


        try {
            // IbmSparqlExtAstWalker.g:281:2: ( ^( NAMED d= sourceSelector ) )
            // IbmSparqlExtAstWalker.g:281:6: ^( NAMED d= sourceSelector )
            {
            match(input,NAMED,FOLLOW_NAMED_in_namedGraphClause1765); if (state.failed) return dc;

            match(input, Token.DOWN, null); if (state.failed) return dc;
            pushFollow(FOLLOW_sourceSelector_in_namedGraphClause1769);
            d=sourceSelector();

            state._fsp--;
            if (state.failed) return dc;

            match(input, Token.UP, null); if (state.failed) return dc;
            if ( state.backtracking==0 ) {
               dc = new DatasetClause(d, true); 
            }

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return dc;
    }
    // $ANTLR end "namedGraphClause"


    // $ANTLR start "sourceSelector"
    // IbmSparqlExtAstWalker.g:284:1: sourceSelector returns [IRI r] : i= iRIref ;
    public final IRI sourceSelector() throws RecognitionException {
        IRI r = null;

        IRI i = null;


        try {
            // IbmSparqlExtAstWalker.g:285:2: (i= iRIref )
            // IbmSparqlExtAstWalker.g:285:7: i= iRIref
            {
            pushFollow(FOLLOW_iRIref_in_sourceSelector1795);
            i=iRIref();

            state._fsp--;
            if (state.failed) return r;
            if ( state.backtracking==0 ) {
               r = i; 
            }

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return r;
    }
    // $ANTLR end "sourceSelector"


    // $ANTLR start "whereClause"
    // IbmSparqlExtAstWalker.g:288:1: whereClause returns [Pattern p] : ^( WHERE (g= groupGraphPattern[true] )? ) ;
    public final Pattern whereClause() throws RecognitionException {
        Pattern p = null;

        Pattern g = null;


        try {
            // IbmSparqlExtAstWalker.g:289:2: ( ^( WHERE (g= groupGraphPattern[true] )? ) )
            // IbmSparqlExtAstWalker.g:290:3: ^( WHERE (g= groupGraphPattern[true] )? )
            {
            match(input,WHERE,FOLLOW_WHERE_in_whereClause1818); if (state.failed) return p;

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); if (state.failed) return p;
                // IbmSparqlExtAstWalker.g:290:12: (g= groupGraphPattern[true] )?
                int alt29=2;
                int LA29_0 = input.LA(1);

                if ( (LA29_0==GROUP_GRAPH_PATTERN||LA29_0==SUB_SELECT) ) {
                    alt29=1;
                }
                switch (alt29) {
                    case 1 :
                        // IbmSparqlExtAstWalker.g:290:12: g= groupGraphPattern[true]
                        {
                        pushFollow(FOLLOW_groupGraphPattern_in_whereClause1822);
                        g=groupGraphPattern(true);

                        state._fsp--;
                        if (state.failed) return p;

                        }
                        break;

                }


                match(input, Token.UP, null); if (state.failed) return p;
            }
            if ( state.backtracking==0 ) {
              p = g;
            }

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return p;
    }
    // $ANTLR end "whereClause"


    // $ANTLR start "solutionModifier"
    // IbmSparqlExtAstWalker.g:293:1: solutionModifier returns [SolutionModifiers m] : ^( MODIFIERS (g= groupClause )? (h= havingClause )? (o= orderClause )? (l= limitOffsetClauses )? ) ;
    public final SolutionModifiers solutionModifier() throws RecognitionException {
        SolutionModifiers m = null;

        GroupCondition g = null;

        HavingCondition h = null;

        List<OrderCondition> o = null;

        LimitOffsetClauses l = null;



        		m = new SolutionModifiers();
        	
        try {
            // IbmSparqlExtAstWalker.g:297:2: ( ^( MODIFIERS (g= groupClause )? (h= havingClause )? (o= orderClause )? (l= limitOffsetClauses )? ) )
            // IbmSparqlExtAstWalker.g:297:6: ^( MODIFIERS (g= groupClause )? (h= havingClause )? (o= orderClause )? (l= limitOffsetClauses )? )
            {
            match(input,MODIFIERS,FOLLOW_MODIFIERS_in_solutionModifier1853); if (state.failed) return m;

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); if (state.failed) return m;
                // IbmSparqlExtAstWalker.g:298:4: (g= groupClause )?
                int alt30=2;
                int LA30_0 = input.LA(1);

                if ( (LA30_0==GROUP_BY) ) {
                    alt30=1;
                }
                switch (alt30) {
                    case 1 :
                        // IbmSparqlExtAstWalker.g:298:6: g= groupClause
                        {
                        pushFollow(FOLLOW_groupClause_in_solutionModifier1862);
                        g=groupClause();

                        state._fsp--;
                        if (state.failed) return m;
                        if ( state.backtracking==0 ) {
                          m.setGroupClause(g); 
                        }

                        }
                        break;

                }

                // IbmSparqlExtAstWalker.g:299:4: (h= havingClause )?
                int alt31=2;
                int LA31_0 = input.LA(1);

                if ( (LA31_0==HAVING) ) {
                    alt31=1;
                }
                switch (alt31) {
                    case 1 :
                        // IbmSparqlExtAstWalker.g:299:6: h= havingClause
                        {
                        pushFollow(FOLLOW_havingClause_in_solutionModifier1879);
                        h=havingClause();

                        state._fsp--;
                        if (state.failed) return m;
                        if ( state.backtracking==0 ) {
                          m.setHavingClause(h);
                        }

                        }
                        break;

                }

                // IbmSparqlExtAstWalker.g:300:4: (o= orderClause )?
                int alt32=2;
                int LA32_0 = input.LA(1);

                if ( (LA32_0==ORDER_BY) ) {
                    alt32=1;
                }
                switch (alt32) {
                    case 1 :
                        // IbmSparqlExtAstWalker.g:300:6: o= orderClause
                        {
                        pushFollow(FOLLOW_orderClause_in_solutionModifier1896);
                        o=orderClause();

                        state._fsp--;
                        if (state.failed) return m;
                        if ( state.backtracking==0 ) {
                          m.setOrderClause(o); 
                        }

                        }
                        break;

                }

                // IbmSparqlExtAstWalker.g:301:4: (l= limitOffsetClauses )?
                int alt33=2;
                int LA33_0 = input.LA(1);

                if ( (LA33_0==LIMIT||LA33_0==OFFSET) ) {
                    alt33=1;
                }
                switch (alt33) {
                    case 1 :
                        // IbmSparqlExtAstWalker.g:301:6: l= limitOffsetClauses
                        {
                        pushFollow(FOLLOW_limitOffsetClauses_in_solutionModifier1913);
                        l=limitOffsetClauses();

                        state._fsp--;
                        if (state.failed) return m;
                        if ( state.backtracking==0 ) {
                          m.setLimitOffset(l); 
                        }

                        }
                        break;

                }


                match(input, Token.UP, null); if (state.failed) return m;
            }

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return m;
    }
    // $ANTLR end "solutionModifier"


    // $ANTLR start "groupClause"
    // IbmSparqlExtAstWalker.g:304:1: groupClause returns [GroupCondition gc] : ^( GROUP_BY c= groupCondition ) ;
    public final GroupCondition groupClause() throws RecognitionException {
        GroupCondition gc = null;

        GroupCondition c = null;


        try {
            // IbmSparqlExtAstWalker.g:305:2: ( ^( GROUP_BY c= groupCondition ) )
            // IbmSparqlExtAstWalker.g:305:6: ^( GROUP_BY c= groupCondition )
            {
            match(input,GROUP_BY,FOLLOW_GROUP_BY_in_groupClause1939); if (state.failed) return gc;

            match(input, Token.DOWN, null); if (state.failed) return gc;
            pushFollow(FOLLOW_groupCondition_in_groupClause1943);
            c=groupCondition();

            state._fsp--;
            if (state.failed) return gc;

            match(input, Token.UP, null); if (state.failed) return gc;
            if ( state.backtracking==0 ) {
               gc = c; 
            }

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return gc;
    }
    // $ANTLR end "groupClause"


    // $ANTLR start "groupCondition"
    // IbmSparqlExtAstWalker.g:308:1: groupCondition returns [GroupCondition gc] : (b= builtInCall | f= functionCall | ^( CONDITION e2= expression (v= var )? ) | v2= var )+ ;
    public final GroupCondition groupCondition() throws RecognitionException {
        GroupCondition gc = null;

        Expression b = null;

        FunctionCall f = null;

        Expression e2 = null;

        String v = null;

        String v2 = null;



        		gc = new GroupCondition();
        	
        try {
            // IbmSparqlExtAstWalker.g:312:2: ( (b= builtInCall | f= functionCall | ^( CONDITION e2= expression (v= var )? ) | v2= var )+ )
            // IbmSparqlExtAstWalker.g:313:2: (b= builtInCall | f= functionCall | ^( CONDITION e2= expression (v= var )? ) | v2= var )+
            {
            // IbmSparqlExtAstWalker.g:313:2: (b= builtInCall | f= functionCall | ^( CONDITION e2= expression (v= var )? ) | v2= var )+
            int cnt35=0;
            loop35:
            do {
                int alt35=5;
                switch ( input.LA(1) ) {
                case NOT_EXISTS:
                case STR:
                case LANG:
                case LANGMATCHES:
                case DATATYPE:
                case BOUND:
                case IRI:
                case URI:
                case BNODE:
                case RAND:
                case ABS:
                case CEIL:
                case FLOOR:
                case ROUND:
                case CONCAT:
                case STRLEN:
                case UCASE:
                case LCASE:
                case ENCODE_FOR_URI:
                case CONTAINS:
                case STRSTARTS:
                case STRENDS:
                case STRBEFORE:
                case STRAFTER:
                case YEAR:
                case MONTH:
                case DAY:
                case HOURS:
                case MINUTES:
                case SECONDS:
                case TIMEZONE:
                case TZ:
                case NOW:
                case UUID:
                case STRUUID:
                case MD5:
                case SHA1:
                case SHA256:
                case SHA384:
                case SHA512:
                case COALESCE:
                case IF:
                case STRLANG:
                case STRDT:
                case SAMETERM:
                case ISIRI:
                case ISURI:
                case ISBLANK:
                case ISLITERAL:
                case ISNUMERIC:
                case REGEX:
                case SUBSTR:
                case REPLACE:
                case EXISTS:
                    {
                    alt35=1;
                    }
                    break;
                case FUNCTION:
                    {
                    alt35=2;
                    }
                    break;
                case CONDITION:
                    {
                    alt35=3;
                    }
                    break;
                case VAR:
                    {
                    alt35=4;
                    }
                    break;

                }

                switch (alt35) {
            	case 1 :
            	    // IbmSparqlExtAstWalker.g:313:4: b= builtInCall
            	    {
            	    pushFollow(FOLLOW_builtInCall_in_groupCondition1976);
            	    b=builtInCall();

            	    state._fsp--;
            	    if (state.failed) return gc;
            	    if ( state.backtracking==0 ) {
            	       gc.addCondition(b);                               
            	    }

            	    }
            	    break;
            	case 2 :
            	    // IbmSparqlExtAstWalker.g:314:4: f= functionCall
            	    {
            	    pushFollow(FOLLOW_functionCall_in_groupCondition1995);
            	    f=functionCall();

            	    state._fsp--;
            	    if (state.failed) return gc;
            	    if ( state.backtracking==0 ) {
            	       gc.addCondition(new FunctionCallExpression(f));   
            	    }

            	    }
            	    break;
            	case 3 :
            	    // IbmSparqlExtAstWalker.g:315:4: ^( CONDITION e2= expression (v= var )? )
            	    {
            	    match(input,CONDITION,FOLLOW_CONDITION_in_groupCondition2013); if (state.failed) return gc;

            	    match(input, Token.DOWN, null); if (state.failed) return gc;
            	    pushFollow(FOLLOW_expression_in_groupCondition2017);
            	    e2=expression();

            	    state._fsp--;
            	    if (state.failed) return gc;
            	    // IbmSparqlExtAstWalker.g:315:30: (v= var )?
            	    int alt34=2;
            	    int LA34_0 = input.LA(1);

            	    if ( (LA34_0==VAR) ) {
            	        alt34=1;
            	    }
            	    switch (alt34) {
            	        case 1 :
            	            // IbmSparqlExtAstWalker.g:315:31: v= var
            	            {
            	            pushFollow(FOLLOW_var_in_groupCondition2022);
            	            v=var();

            	            state._fsp--;
            	            if (state.failed) return gc;

            	            }
            	            break;

            	    }


            	    match(input, Token.UP, null); if (state.failed) return gc;
            	    if ( state.backtracking==0 ) {
            	       gc.addCondition(new VariableExpression(e2, v));   
            	    }

            	    }
            	    break;
            	case 4 :
            	    // IbmSparqlExtAstWalker.g:316:4: v2= var
            	    {
            	    pushFollow(FOLLOW_var_in_groupCondition2039);
            	    v2=var();

            	    state._fsp--;
            	    if (state.failed) return gc;
            	    if ( state.backtracking==0 ) {
            	       gc.addCondition(new VariableExpression(v2));      
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt35 >= 1 ) break loop35;
            	    if (state.backtracking>0) {state.failed=true; return gc;}
                        EarlyExitException eee =
                            new EarlyExitException(35, input);
                        throw eee;
                }
                cnt35++;
            } while (true);


            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return gc;
    }
    // $ANTLR end "groupCondition"


    // $ANTLR start "havingClause"
    // IbmSparqlExtAstWalker.g:320:1: havingClause returns [HavingCondition h] : ^( HAVING (c= havingCondition )+ ) ;
    public final HavingCondition havingClause() throws RecognitionException {
        HavingCondition h = null;

        Expression c = null;



        		h = new HavingCondition();
        	
        try {
            // IbmSparqlExtAstWalker.g:324:2: ( ^( HAVING (c= havingCondition )+ ) )
            // IbmSparqlExtAstWalker.g:324:6: ^( HAVING (c= havingCondition )+ )
            {
            match(input,HAVING,FOLLOW_HAVING_in_havingClause2113); if (state.failed) return h;

            match(input, Token.DOWN, null); if (state.failed) return h;
            // IbmSparqlExtAstWalker.g:324:15: (c= havingCondition )+
            int cnt36=0;
            loop36:
            do {
                int alt36=2;
                int LA36_0 = input.LA(1);

                if ( ((LA36_0>=FUNCTION && LA36_0<=NOT_EXISTS)||(LA36_0>=STR && LA36_0<=SHA1)||(LA36_0>=SHA256 && LA36_0<=EXISTS)) ) {
                    alt36=1;
                }


                switch (alt36) {
            	case 1 :
            	    // IbmSparqlExtAstWalker.g:324:16: c= havingCondition
            	    {
            	    pushFollow(FOLLOW_havingCondition_in_havingClause2118);
            	    c=havingCondition();

            	    state._fsp--;
            	    if (state.failed) return h;
            	    if ( state.backtracking==0 ) {
            	      h.addConstraint(c); 
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt36 >= 1 ) break loop36;
            	    if (state.backtracking>0) {state.failed=true; return h;}
                        EarlyExitException eee =
                            new EarlyExitException(36, input);
                        throw eee;
                }
                cnt36++;
            } while (true);


            match(input, Token.UP, null); if (state.failed) return h;

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return h;
    }
    // $ANTLR end "havingClause"


    // $ANTLR start "havingCondition"
    // IbmSparqlExtAstWalker.g:327:1: havingCondition returns [Expression e] : c= constraint ;
    public final Expression havingCondition() throws RecognitionException {
        Expression e = null;

        Expression c = null;


        try {
            // IbmSparqlExtAstWalker.g:328:2: (c= constraint )
            // IbmSparqlExtAstWalker.g:328:6: c= constraint
            {
            pushFollow(FOLLOW_constraint_in_havingCondition2146);
            c=constraint();

            state._fsp--;
            if (state.failed) return e;
            if ( state.backtracking==0 ) {
              e = c;
            }

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return e;
    }
    // $ANTLR end "havingCondition"


    // $ANTLR start "orderClause"
    // IbmSparqlExtAstWalker.g:331:1: orderClause returns [List<OrderCondition> loc] : ^( ORDER_BY (oc= orderCondition )+ ) ;
    public final List<OrderCondition> orderClause() throws RecognitionException {
        List<OrderCondition> loc = null;

        OrderCondition oc = null;


         
        		loc = new ArrayList<OrderCondition>(); 
        	
        try {
            // IbmSparqlExtAstWalker.g:335:2: ( ^( ORDER_BY (oc= orderCondition )+ ) )
            // IbmSparqlExtAstWalker.g:335:6: ^( ORDER_BY (oc= orderCondition )+ )
            {
            match(input,ORDER_BY,FOLLOW_ORDER_BY_in_orderClause2175); if (state.failed) return loc;

            match(input, Token.DOWN, null); if (state.failed) return loc;
            // IbmSparqlExtAstWalker.g:335:18: (oc= orderCondition )+
            int cnt37=0;
            loop37:
            do {
                int alt37=2;
                int LA37_0 = input.LA(1);

                if ( (LA37_0==VAR||(LA37_0>=FUNCTION && LA37_0<=NOT_EXISTS)||(LA37_0>=ASC && LA37_0<=DESC)||(LA37_0>=STR && LA37_0<=SHA1)||(LA37_0>=SHA256 && LA37_0<=EXISTS)) ) {
                    alt37=1;
                }


                switch (alt37) {
            	case 1 :
            	    // IbmSparqlExtAstWalker.g:335:20: oc= orderCondition
            	    {
            	    pushFollow(FOLLOW_orderCondition_in_orderClause2182);
            	    oc=orderCondition();

            	    state._fsp--;
            	    if (state.failed) return loc;
            	    if ( state.backtracking==0 ) {
            	       loc.add(oc); 
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt37 >= 1 ) break loop37;
            	    if (state.backtracking>0) {state.failed=true; return loc;}
                        EarlyExitException eee =
                            new EarlyExitException(37, input);
                        throw eee;
                }
                cnt37++;
            } while (true);


            match(input, Token.UP, null); if (state.failed) return loc;

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return loc;
    }
    // $ANTLR end "orderClause"


    // $ANTLR start "orderCondition"
    // IbmSparqlExtAstWalker.g:338:1: orderCondition returns [OrderCondition oc] : ( ( ^( ASC e1= brackettedExpression ) ) | ( ^( DESC e2= brackettedExpression ) ) | (c= constraint | v= var ) );
    public final OrderCondition orderCondition() throws RecognitionException {
        OrderCondition oc = null;

        Expression e1 = null;

        Expression e2 = null;

        Expression c = null;

        String v = null;


        try {
            // IbmSparqlExtAstWalker.g:339:2: ( ( ^( ASC e1= brackettedExpression ) ) | ( ^( DESC e2= brackettedExpression ) ) | (c= constraint | v= var ) )
            int alt39=3;
            switch ( input.LA(1) ) {
            case ASC:
                {
                alt39=1;
                }
                break;
            case DESC:
                {
                alt39=2;
                }
                break;
            case VAR:
            case FUNCTION:
            case EXPRESSION:
            case NOT_EXISTS:
            case STR:
            case LANG:
            case LANGMATCHES:
            case DATATYPE:
            case BOUND:
            case IRI:
            case URI:
            case BNODE:
            case RAND:
            case ABS:
            case CEIL:
            case FLOOR:
            case ROUND:
            case CONCAT:
            case STRLEN:
            case UCASE:
            case LCASE:
            case ENCODE_FOR_URI:
            case CONTAINS:
            case STRSTARTS:
            case STRENDS:
            case STRBEFORE:
            case STRAFTER:
            case YEAR:
            case MONTH:
            case DAY:
            case HOURS:
            case MINUTES:
            case SECONDS:
            case TIMEZONE:
            case TZ:
            case NOW:
            case UUID:
            case STRUUID:
            case MD5:
            case SHA1:
            case SHA256:
            case SHA384:
            case SHA512:
            case COALESCE:
            case IF:
            case STRLANG:
            case STRDT:
            case SAMETERM:
            case ISIRI:
            case ISURI:
            case ISBLANK:
            case ISLITERAL:
            case ISNUMERIC:
            case REGEX:
            case SUBSTR:
            case REPLACE:
            case EXISTS:
                {
                alt39=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return oc;}
                NoViableAltException nvae =
                    new NoViableAltException("", 39, 0, input);

                throw nvae;
            }

            switch (alt39) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:339:5: ( ^( ASC e1= brackettedExpression ) )
                    {
                    // IbmSparqlExtAstWalker.g:339:5: ( ^( ASC e1= brackettedExpression ) )
                    // IbmSparqlExtAstWalker.g:339:6: ^( ASC e1= brackettedExpression )
                    {
                    match(input,ASC,FOLLOW_ASC_in_orderCondition2209); if (state.failed) return oc;

                    match(input, Token.DOWN, null); if (state.failed) return oc;
                    pushFollow(FOLLOW_brackettedExpression_in_orderCondition2213);
                    e1=brackettedExpression();

                    state._fsp--;
                    if (state.failed) return oc;

                    match(input, Token.UP, null); if (state.failed) return oc;
                    if ( state.backtracking==0 ) {
                       oc = new OrderCondition(e1, OrderCondition.EOrderType.ASC); 
                    }

                    }


                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:340:4: ( ^( DESC e2= brackettedExpression ) )
                    {
                    // IbmSparqlExtAstWalker.g:340:4: ( ^( DESC e2= brackettedExpression ) )
                    // IbmSparqlExtAstWalker.g:340:5: ^( DESC e2= brackettedExpression )
                    {
                    match(input,DESC,FOLLOW_DESC_in_orderCondition2225); if (state.failed) return oc;

                    match(input, Token.DOWN, null); if (state.failed) return oc;
                    pushFollow(FOLLOW_brackettedExpression_in_orderCondition2229);
                    e2=brackettedExpression();

                    state._fsp--;
                    if (state.failed) return oc;

                    match(input, Token.UP, null); if (state.failed) return oc;
                    if ( state.backtracking==0 ) {
                       oc = new OrderCondition(e2, OrderCondition.EOrderType.DESC);
                    }

                    }


                    }
                    break;
                case 3 :
                    // IbmSparqlExtAstWalker.g:341:5: (c= constraint | v= var )
                    {
                    // IbmSparqlExtAstWalker.g:341:5: (c= constraint | v= var )
                    int alt38=2;
                    int LA38_0 = input.LA(1);

                    if ( ((LA38_0>=FUNCTION && LA38_0<=NOT_EXISTS)||(LA38_0>=STR && LA38_0<=SHA1)||(LA38_0>=SHA256 && LA38_0<=EXISTS)) ) {
                        alt38=1;
                    }
                    else if ( (LA38_0==VAR) ) {
                        alt38=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return oc;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 38, 0, input);

                        throw nvae;
                    }
                    switch (alt38) {
                        case 1 :
                            // IbmSparqlExtAstWalker.g:341:7: c= constraint
                            {
                            pushFollow(FOLLOW_constraint_in_orderCondition2243);
                            c=constraint();

                            state._fsp--;
                            if (state.failed) return oc;
                            if ( state.backtracking==0 ) {
                               oc = new OrderCondition(c); 
                            }

                            }
                            break;
                        case 2 :
                            // IbmSparqlExtAstWalker.g:342:5: v= var
                            {
                            pushFollow(FOLLOW_var_in_orderCondition2259);
                            v=var();

                            state._fsp--;
                            if (state.failed) return oc;
                            if ( state.backtracking==0 ) {
                               oc = new OrderCondition(new VariableExpression(v)); 
                            }

                            }
                            break;

                    }


                    }
                    break;

            }
        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return oc;
    }
    // $ANTLR end "orderCondition"


    // $ANTLR start "limitOffsetClauses"
    // IbmSparqlExtAstWalker.g:346:1: limitOffsetClauses returns [LimitOffsetClauses loc] : (lc1= limitClause (oc1= offsetClause )? | oc2= offsetClause (lc2= limitClause )? ) ;
    public final LimitOffsetClauses limitOffsetClauses() throws RecognitionException {
        LimitOffsetClauses loc = null;

        int lc1 = 0;

        int oc1 = 0;

        int oc2 = 0;

        int lc2 = 0;


         
        		int lc = -1, oc = -1; 
        	
        try {
            // IbmSparqlExtAstWalker.g:350:2: ( (lc1= limitClause (oc1= offsetClause )? | oc2= offsetClause (lc2= limitClause )? ) )
            // IbmSparqlExtAstWalker.g:350:6: (lc1= limitClause (oc1= offsetClause )? | oc2= offsetClause (lc2= limitClause )? )
            {
            // IbmSparqlExtAstWalker.g:350:6: (lc1= limitClause (oc1= offsetClause )? | oc2= offsetClause (lc2= limitClause )? )
            int alt42=2;
            int LA42_0 = input.LA(1);

            if ( (LA42_0==LIMIT) ) {
                alt42=1;
            }
            else if ( (LA42_0==OFFSET) ) {
                alt42=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return loc;}
                NoViableAltException nvae =
                    new NoViableAltException("", 42, 0, input);

                throw nvae;
            }
            switch (alt42) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:350:8: lc1= limitClause (oc1= offsetClause )?
                    {
                    pushFollow(FOLLOW_limitClause_in_limitOffsetClauses2299);
                    lc1=limitClause();

                    state._fsp--;
                    if (state.failed) return loc;
                    if ( state.backtracking==0 ) {
                      lc = lc1;
                    }
                    // IbmSparqlExtAstWalker.g:350:37: (oc1= offsetClause )?
                    int alt40=2;
                    int LA40_0 = input.LA(1);

                    if ( (LA40_0==OFFSET) ) {
                        alt40=1;
                    }
                    switch (alt40) {
                        case 1 :
                            // IbmSparqlExtAstWalker.g:350:38: oc1= offsetClause
                            {
                            pushFollow(FOLLOW_offsetClause_in_limitOffsetClauses2307);
                            oc1=offsetClause();

                            state._fsp--;
                            if (state.failed) return loc;
                            if ( state.backtracking==0 ) {
                              oc = oc1;
                            }

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:351:5: oc2= offsetClause (lc2= limitClause )?
                    {
                    pushFollow(FOLLOW_offsetClause_in_limitOffsetClauses2320);
                    oc2=offsetClause();

                    state._fsp--;
                    if (state.failed) return loc;
                    if ( state.backtracking==0 ) {
                      oc = oc2;
                    }
                    // IbmSparqlExtAstWalker.g:351:34: (lc2= limitClause )?
                    int alt41=2;
                    int LA41_0 = input.LA(1);

                    if ( (LA41_0==LIMIT) ) {
                        alt41=1;
                    }
                    switch (alt41) {
                        case 1 :
                            // IbmSparqlExtAstWalker.g:351:35: lc2= limitClause
                            {
                            pushFollow(FOLLOW_limitClause_in_limitOffsetClauses2327);
                            lc2=limitClause();

                            state._fsp--;
                            if (state.failed) return loc;
                            if ( state.backtracking==0 ) {
                              lc = lc2;
                            }

                            }
                            break;

                    }


                    }
                    break;

            }

            if ( state.backtracking==0 ) {
               loc = new LimitOffsetClauses(lc, oc); 
            }

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return loc;
    }
    // $ANTLR end "limitOffsetClauses"


    // $ANTLR start "limitClause"
    // IbmSparqlExtAstWalker.g:356:1: limitClause returns [int x] : ^( LIMIT i= INTEGER ) ;
    public final int limitClause() throws RecognitionException {
        int x = 0;

        XTree i=null;

        try {
            // IbmSparqlExtAstWalker.g:357:2: ( ^( LIMIT i= INTEGER ) )
            // IbmSparqlExtAstWalker.g:358:3: ^( LIMIT i= INTEGER )
            {
            match(input,LIMIT,FOLLOW_LIMIT_in_limitClause2365); if (state.failed) return x;

            match(input, Token.DOWN, null); if (state.failed) return x;
            i=(XTree)match(input,INTEGER,FOLLOW_INTEGER_in_limitClause2370); if (state.failed) return x;

            match(input, Token.UP, null); if (state.failed) return x;
            if ( state.backtracking==0 ) {
               x = Integer.parseInt((i!=null?i.getText():null)); 
            }

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return x;
    }
    // $ANTLR end "limitClause"


    // $ANTLR start "offsetClause"
    // IbmSparqlExtAstWalker.g:361:1: offsetClause returns [int x] : ^( OFFSET i= INTEGER ) ;
    public final int offsetClause() throws RecognitionException {
        int x = 0;

        XTree i=null;

        try {
            // IbmSparqlExtAstWalker.g:362:2: ( ^( OFFSET i= INTEGER ) )
            // IbmSparqlExtAstWalker.g:363:3: ^( OFFSET i= INTEGER )
            {
            match(input,OFFSET,FOLLOW_OFFSET_in_offsetClause2397); if (state.failed) return x;

            match(input, Token.DOWN, null); if (state.failed) return x;
            i=(XTree)match(input,INTEGER,FOLLOW_INTEGER_in_offsetClause2402); if (state.failed) return x;

            match(input, Token.UP, null); if (state.failed) return x;
            if ( state.backtracking==0 ) {
               x = Integer.parseInt((i!=null?i.getText():null)); 
            }

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return x;
    }
    // $ANTLR end "offsetClause"


    // $ANTLR start "bindingsClause"
    // IbmSparqlExtAstWalker.g:366:1: bindingsClause returns [Pattern v] : d= inlineData ;
    public final Pattern bindingsClause() throws RecognitionException {
        Pattern v = null;

        ValuesPattern d = null;


        try {
            // IbmSparqlExtAstWalker.g:367:2: (d= inlineData )
            // IbmSparqlExtAstWalker.g:367:6: d= inlineData
            {
            pushFollow(FOLLOW_inlineData_in_bindingsClause2426);
            d=inlineData();

            state._fsp--;
            if (state.failed) return v;
            if ( state.backtracking==0 ) {
              v = d; 
            }

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return v;
    }
    // $ANTLR end "bindingsClause"


    // $ANTLR start "bindingValue"
    // IbmSparqlExtAstWalker.g:370:1: bindingValue : ( iRIref | rDFLiteral | numericLiteral | booleanLiteral | UNDEF ) ;
    public final void bindingValue() throws RecognitionException {
        try {
            // IbmSparqlExtAstWalker.g:371:2: ( ( iRIref | rDFLiteral | numericLiteral | booleanLiteral | UNDEF ) )
            // IbmSparqlExtAstWalker.g:371:6: ( iRIref | rDFLiteral | numericLiteral | booleanLiteral | UNDEF )
            {
            // IbmSparqlExtAstWalker.g:371:6: ( iRIref | rDFLiteral | numericLiteral | booleanLiteral | UNDEF )
            int alt43=5;
            switch ( input.LA(1) ) {
            case PREFIXED_NAME:
            case PREFIXED_NS:
            case IRI:
                {
                alt43=1;
                }
                break;
            case STRING:
                {
                alt43=2;
                }
                break;
            case BIG_INTEGER:
            case BIG_DECIMAL:
            case DOUBLE:
                {
                alt43=3;
                }
                break;
            case BOOLEAN:
                {
                alt43=4;
                }
                break;
            case UNDEF:
                {
                alt43=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 43, 0, input);

                throw nvae;
            }

            switch (alt43) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:371:8: iRIref
                    {
                    pushFollow(FOLLOW_iRIref_in_bindingValue2447);
                    iRIref();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:371:17: rDFLiteral
                    {
                    pushFollow(FOLLOW_rDFLiteral_in_bindingValue2451);
                    rDFLiteral();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // IbmSparqlExtAstWalker.g:371:30: numericLiteral
                    {
                    pushFollow(FOLLOW_numericLiteral_in_bindingValue2455);
                    numericLiteral();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // IbmSparqlExtAstWalker.g:371:47: booleanLiteral
                    {
                    pushFollow(FOLLOW_booleanLiteral_in_bindingValue2459);
                    booleanLiteral();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // IbmSparqlExtAstWalker.g:371:64: UNDEF
                    {
                    match(input,UNDEF,FOLLOW_UNDEF_in_bindingValue2463); if (state.failed) return ;

                    }
                    break;

            }


            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return ;
    }
    // $ANTLR end "bindingValue"


    // $ANTLR start "triplesTemplate"
    // IbmSparqlExtAstWalker.g:374:1: triplesTemplate[ConstructQuery cq, PatternSet p] : sp= triplesSameSubject[$cq] ( DOT ( triplesTemplate[$cq, $p] )? )? ;
    public final void triplesTemplate(ConstructQuery cq, PatternSet p) throws RecognitionException {
        SimplePattern sp = null;


        try {
            // IbmSparqlExtAstWalker.g:375:2: (sp= triplesSameSubject[$cq] ( DOT ( triplesTemplate[$cq, $p] )? )? )
            // IbmSparqlExtAstWalker.g:375:7: sp= triplesSameSubject[$cq] ( DOT ( triplesTemplate[$cq, $p] )? )?
            {
            pushFollow(FOLLOW_triplesSameSubject_in_triplesTemplate2483);
            sp=triplesSameSubject(cq);

            state._fsp--;
            if (state.failed) return ;
            if ( state.backtracking==0 ) {
               p.addPattern(sp); 
            }
            // IbmSparqlExtAstWalker.g:375:57: ( DOT ( triplesTemplate[$cq, $p] )? )?
            int alt45=2;
            int LA45_0 = input.LA(1);

            if ( (LA45_0==DOT) ) {
                alt45=1;
            }
            switch (alt45) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:375:59: DOT ( triplesTemplate[$cq, $p] )?
                    {
                    match(input,DOT,FOLLOW_DOT_in_triplesTemplate2490); if (state.failed) return ;
                    // IbmSparqlExtAstWalker.g:375:63: ( triplesTemplate[$cq, $p] )?
                    int alt44=2;
                    int LA44_0 = input.LA(1);

                    if ( ((LA44_0>=TRIPLE && LA44_0<=TRIPLE2)) ) {
                        alt44=1;
                    }
                    switch (alt44) {
                        case 1 :
                            // IbmSparqlExtAstWalker.g:375:63: triplesTemplate[$cq, $p]
                            {
                            pushFollow(FOLLOW_triplesTemplate_in_triplesTemplate2492);
                            triplesTemplate(cq, p);

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;

            }


            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return ;
    }
    // $ANTLR end "triplesTemplate"


    // $ANTLR start "groupGraphPattern"
    // IbmSparqlExtAstWalker.g:378:1: groupGraphPattern[boolean l] returns [Pattern r] : ( ^( GROUP_GRAPH_PATTERN ( groupGraphPatternSub[p] )? ) | s= subSelect );
    public final Pattern groupGraphPattern(boolean l) throws RecognitionException {
        Pattern r = null;

        SubSelectPattern s = null;



        		PatternSet p = new PatternSet();  p.setTopLevel(l);
        		r = p;
        	
        try {
            // IbmSparqlExtAstWalker.g:383:2: ( ^( GROUP_GRAPH_PATTERN ( groupGraphPatternSub[p] )? ) | s= subSelect )
            int alt47=2;
            int LA47_0 = input.LA(1);

            if ( (LA47_0==GROUP_GRAPH_PATTERN) ) {
                alt47=1;
            }
            else if ( (LA47_0==SUB_SELECT) ) {
                alt47=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return r;}
                NoViableAltException nvae =
                    new NoViableAltException("", 47, 0, input);

                throw nvae;
            }
            switch (alt47) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:383:6: ^( GROUP_GRAPH_PATTERN ( groupGraphPatternSub[p] )? )
                    {
                    match(input,GROUP_GRAPH_PATTERN,FOLLOW_GROUP_GRAPH_PATTERN_in_groupGraphPattern2522); if (state.failed) return r;

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); if (state.failed) return r;
                        // IbmSparqlExtAstWalker.g:383:28: ( groupGraphPatternSub[p] )?
                        int alt46=2;
                        int LA46_0 = input.LA(1);

                        if ( (LA46_0==GROUP_GRAPH_PATTERN||(LA46_0>=SUB_SELECT && LA46_0<=TRIPLES_BLOCK)||LA46_0==GRAPH||(LA46_0>=VALUES && LA46_0<=FILTER)) ) {
                            alt46=1;
                        }
                        switch (alt46) {
                            case 1 :
                                // IbmSparqlExtAstWalker.g:383:28: groupGraphPatternSub[p]
                                {
                                pushFollow(FOLLOW_groupGraphPatternSub_in_groupGraphPattern2524);
                                groupGraphPatternSub(p);

                                state._fsp--;
                                if (state.failed) return r;

                                }
                                break;

                        }


                        match(input, Token.UP, null); if (state.failed) return r;
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:384:4: s= subSelect
                    {
                    pushFollow(FOLLOW_subSelect_in_groupGraphPattern2536);
                    s=subSelect();

                    state._fsp--;
                    if (state.failed) return r;
                    if ( state.backtracking==0 ) {
                       r = s; 
                    }

                    }
                    break;

            }
        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return r;
    }
    // $ANTLR end "groupGraphPattern"


    // $ANTLR start "groupGraphPatternSub"
    // IbmSparqlExtAstWalker.g:387:1: groupGraphPatternSub[PatternSet p] : (sp= triplesBlock[$p] | f= filter | graphPatternNewBGP[$p,blankNodes] )+ ;
    public final void groupGraphPatternSub(PatternSet p) throws RecognitionException {
        SimplePattern sp = null;

        Expression f = null;



              Set<BlankNodeVariable> blankNodes = HashSetFactory.make();  
            
        try {
            // IbmSparqlExtAstWalker.g:391:2: ( (sp= triplesBlock[$p] | f= filter | graphPatternNewBGP[$p,blankNodes] )+ )
            // IbmSparqlExtAstWalker.g:391:4: (sp= triplesBlock[$p] | f= filter | graphPatternNewBGP[$p,blankNodes] )+
            {
            // IbmSparqlExtAstWalker.g:391:4: (sp= triplesBlock[$p] | f= filter | graphPatternNewBGP[$p,blankNodes] )+
            int cnt48=0;
            loop48:
            do {
                int alt48=4;
                switch ( input.LA(1) ) {
                case TRIPLES_BLOCK:
                    {
                    alt48=1;
                    }
                    break;
                case FILTER:
                    {
                    alt48=2;
                    }
                    break;
                case GROUP_GRAPH_PATTERN:
                case SUB_SELECT:
                case GRAPH:
                case VALUES:
                case OPTIONAL:
                case SERVICE:
                case BIND:
                case UNION:
                case MINUS:
                    {
                    alt48=3;
                    }
                    break;

                }

                switch (alt48) {
            	case 1 :
            	    // IbmSparqlExtAstWalker.g:391:10: sp= triplesBlock[$p]
            	    {
            	    pushFollow(FOLLOW_triplesBlock_in_groupGraphPatternSub2568);
            	    sp=triplesBlock(p);

            	    state._fsp--;
            	    if (state.failed) return ;
            	    if ( state.backtracking==0 ) {
            	       
            	                      blankNodes.addAll(sp.gatherBlankNodes());
            	                      p.addPattern(sp);
            	                    
            	    }

            	    }
            	    break;
            	case 2 :
            	    // IbmSparqlExtAstWalker.g:396:12: f= filter
            	    {
            	    pushFollow(FOLLOW_filter_in_groupGraphPatternSub2600);
            	    f=filter();

            	    state._fsp--;
            	    if (state.failed) return ;
            	    if ( state.backtracking==0 ) {
            	       
            	                      if (f != null) { p.addFilter(f); }
            	                    
            	    }

            	    }
            	    break;
            	case 3 :
            	    // IbmSparqlExtAstWalker.g:400:15: graphPatternNewBGP[$p,blankNodes]
            	    {
            	    pushFollow(FOLLOW_graphPatternNewBGP_in_groupGraphPatternSub2638);
            	    graphPatternNewBGP(p, blankNodes);

            	    state._fsp--;
            	    if (state.failed) return ;
            	    if ( state.backtracking==0 ) {

            	                checkBlankNodes(blankNodes);
            	                blankNodes.clear();
            	              
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt48 >= 1 ) break loop48;
            	    if (state.backtracking>0) {state.failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(48, input);
                        throw eee;
                }
                cnt48++;
            } while (true);

            if ( state.backtracking==0 ) {

                        checkBlankNodes(blankNodes);
                      
            }

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return ;
    }
    // $ANTLR end "groupGraphPatternSub"


    // $ANTLR start "triplesBlock"
    // IbmSparqlExtAstWalker.g:412:1: triplesBlock[PatternSet p] returns [SimplePattern sp] : ^( TRIPLES_BLOCK (s= triples | s2= triples2 )+ ) ;
    public final SimplePattern triplesBlock(PatternSet p) throws RecognitionException {
        SimplePattern sp = null;

        QueryTriple s = null;

        QueryTriple2 s2 = null;


        try {
            // IbmSparqlExtAstWalker.g:413:5: ( ^( TRIPLES_BLOCK (s= triples | s2= triples2 )+ ) )
            // IbmSparqlExtAstWalker.g:413:9: ^( TRIPLES_BLOCK (s= triples | s2= triples2 )+ )
            {
            match(input,TRIPLES_BLOCK,FOLLOW_TRIPLES_BLOCK_in_triplesBlock2697); if (state.failed) return sp;

            if ( state.backtracking==0 ) {
               sp = new SimplePattern(); 
            }

            match(input, Token.DOWN, null); if (state.failed) return sp;
            // IbmSparqlExtAstWalker.g:415:6: (s= triples | s2= triples2 )+
            int cnt49=0;
            loop49:
            do {
                int alt49=3;
                int LA49_0 = input.LA(1);

                if ( (LA49_0==TRIPLE) ) {
                    alt49=1;
                }
                else if ( (LA49_0==TRIPLE2) ) {
                    alt49=2;
                }


                switch (alt49) {
            	case 1 :
            	    // IbmSparqlExtAstWalker.g:416:10: s= triples
            	    {
            	    pushFollow(FOLLOW_triples_in_triplesBlock2730);
            	    s=triples();

            	    state._fsp--;
            	    if (state.failed) return sp;
            	    if ( state.backtracking==0 ) {
            	       s.expandAndAddTo(sp); 
            	    }

            	    }
            	    break;
            	case 2 :
            	    // IbmSparqlExtAstWalker.g:417:7: s2= triples2
            	    {
            	    pushFollow(FOLLOW_triples2_in_triplesBlock2746);
            	    s2=triples2();

            	    state._fsp--;
            	    if (state.failed) return sp;
            	    if ( state.backtracking==0 ) {
            	       s2.expandAndAddTo(sp); 
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt49 >= 1 ) break loop49;
            	    if (state.backtracking>0) {state.failed=true; return sp;}
                        EarlyExitException eee =
                            new EarlyExitException(49, input);
                        throw eee;
                }
                cnt49++;
            } while (true);


            match(input, Token.UP, null); if (state.failed) return sp;

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return sp;
    }
    // $ANTLR end "triplesBlock"


    // $ANTLR start "triples"
    // IbmSparqlExtAstWalker.g:422:1: triples returns [QueryTriple qt] : ( ^( TRIPLE ^( SUBJECT (s1= graphNode ) ) ^( PREDICATE (v1= predicate ) ) ^( VALUE o= object ) ) ) ;
    public final QueryTriple triples() throws RecognitionException {
        QueryTriple qt = null;

        GraphNode s1 = null;

        BinaryUnion<Variable, Path> v1 = null;

        GraphNode o = null;


         
        			QueryTripleTerm s = null;
        			PropertyTerm p = null;
        			QueryTripleTerm v = null; 
        		  
        try {
            // IbmSparqlExtAstWalker.g:428:2: ( ( ^( TRIPLE ^( SUBJECT (s1= graphNode ) ) ^( PREDICATE (v1= predicate ) ) ^( VALUE o= object ) ) ) )
            // IbmSparqlExtAstWalker.g:428:6: ( ^( TRIPLE ^( SUBJECT (s1= graphNode ) ) ^( PREDICATE (v1= predicate ) ) ^( VALUE o= object ) ) )
            {
            // IbmSparqlExtAstWalker.g:428:6: ( ^( TRIPLE ^( SUBJECT (s1= graphNode ) ) ^( PREDICATE (v1= predicate ) ) ^( VALUE o= object ) ) )
            // IbmSparqlExtAstWalker.g:428:8: ^( TRIPLE ^( SUBJECT (s1= graphNode ) ) ^( PREDICATE (v1= predicate ) ) ^( VALUE o= object ) )
            {
            match(input,TRIPLE,FOLLOW_TRIPLE_in_triples2794); if (state.failed) return qt;

            match(input, Token.DOWN, null); if (state.failed) return qt;
            match(input,SUBJECT,FOLLOW_SUBJECT_in_triples2797); if (state.failed) return qt;

            match(input, Token.DOWN, null); if (state.failed) return qt;
            // IbmSparqlExtAstWalker.g:428:27: (s1= graphNode )
            // IbmSparqlExtAstWalker.g:428:29: s1= graphNode
            {
            pushFollow(FOLLOW_graphNode_in_triples2803);
            s1=graphNode();

            state._fsp--;
            if (state.failed) return qt;
            if ( state.backtracking==0 ) {
               s = new QueryTripleTerm(s1);  
            }

            }


            match(input, Token.UP, null); if (state.failed) return qt;
            match(input,PREDICATE,FOLLOW_PREDICATE_in_triples2839); if (state.failed) return qt;

            match(input, Token.DOWN, null); if (state.failed) return qt;
            // IbmSparqlExtAstWalker.g:431:18: (v1= predicate )
            // IbmSparqlExtAstWalker.g:431:20: v1= predicate
            {
            pushFollow(FOLLOW_predicate_in_triples2845);
            v1=predicate();

            state._fsp--;
            if (state.failed) return qt;
            if ( state.backtracking==0 ) {
               p = new PropertyTerm(v1);  
            }

            }


            match(input, Token.UP, null); if (state.failed) return qt;
            match(input,VALUE,FOLLOW_VALUE_in_triples2885); if (state.failed) return qt;

            match(input, Token.DOWN, null); if (state.failed) return qt;
            pushFollow(FOLLOW_object_in_triples2890);
            o=object();

            state._fsp--;
            if (state.failed) return qt;

            match(input, Token.UP, null); if (state.failed) return qt;
            if ( state.backtracking==0 ) {
               v = new QueryTripleTerm(o);   
            }

            match(input, Token.UP, null); if (state.failed) return qt;
            if ( state.backtracking==0 ) {
               qt = new QueryTriple(s, p, v); 
            }

            }


            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return qt;
    }
    // $ANTLR end "triples"


    // $ANTLR start "triples2"
    // IbmSparqlExtAstWalker.g:440:1: triples2 returns [QueryTriple2 qt] : ^( TRIPLE2 ^( SUBJECT (s= triplesNode ) ) ( ^( PROPERTY_LIST ^( PREDICATE (p= predicate ) ) ( ^( VALUE o= object ) )+ ) )* ) ;
    public final QueryTriple2 triples2() throws RecognitionException {
        QueryTriple2 qt = null;

        TriplesNode s = null;

        BinaryUnion<Variable, Path> p = null;

        GraphNode o = null;


         
        				PropertyListElement ple = null;
        		  
        try {
            // IbmSparqlExtAstWalker.g:444:2: ( ^( TRIPLE2 ^( SUBJECT (s= triplesNode ) ) ( ^( PROPERTY_LIST ^( PREDICATE (p= predicate ) ) ( ^( VALUE o= object ) )+ ) )* ) )
            // IbmSparqlExtAstWalker.g:444:6: ^( TRIPLE2 ^( SUBJECT (s= triplesNode ) ) ( ^( PROPERTY_LIST ^( PREDICATE (p= predicate ) ) ( ^( VALUE o= object ) )+ ) )* )
            {
            match(input,TRIPLE2,FOLLOW_TRIPLE2_in_triples22945); if (state.failed) return qt;

            match(input, Token.DOWN, null); if (state.failed) return qt;
            match(input,SUBJECT,FOLLOW_SUBJECT_in_triples22948); if (state.failed) return qt;

            match(input, Token.DOWN, null); if (state.failed) return qt;
            // IbmSparqlExtAstWalker.g:444:26: (s= triplesNode )
            // IbmSparqlExtAstWalker.g:444:28: s= triplesNode
            {
            pushFollow(FOLLOW_triplesNode_in_triples22954);
            s=triplesNode();

            state._fsp--;
            if (state.failed) return qt;
            if ( state.backtracking==0 ) {
               qt = new QueryTriple2(s);  
            }

            }


            match(input, Token.UP, null); if (state.failed) return qt;
            // IbmSparqlExtAstWalker.g:447:6: ( ^( PROPERTY_LIST ^( PREDICATE (p= predicate ) ) ( ^( VALUE o= object ) )+ ) )*
            loop51:
            do {
                int alt51=2;
                int LA51_0 = input.LA(1);

                if ( (LA51_0==PROPERTY_LIST) ) {
                    alt51=1;
                }


                switch (alt51) {
            	case 1 :
            	    // IbmSparqlExtAstWalker.g:447:8: ^( PROPERTY_LIST ^( PREDICATE (p= predicate ) ) ( ^( VALUE o= object ) )+ )
            	    {
            	    match(input,PROPERTY_LIST,FOLLOW_PROPERTY_LIST_in_triples22992); if (state.failed) return qt;

            	    match(input, Token.DOWN, null); if (state.failed) return qt;
            	    match(input,PREDICATE,FOLLOW_PREDICATE_in_triples23004); if (state.failed) return qt;

            	    match(input, Token.DOWN, null); if (state.failed) return qt;
            	    // IbmSparqlExtAstWalker.g:448:22: (p= predicate )
            	    // IbmSparqlExtAstWalker.g:448:24: p= predicate
            	    {
            	    pushFollow(FOLLOW_predicate_in_triples23011);
            	    p=predicate();

            	    state._fsp--;
            	    if (state.failed) return qt;
            	    if ( state.backtracking==0 ) {
            	       ple = new PropertyListElement(p);  
            	    }

            	    }


            	    match(input, Token.UP, null); if (state.failed) return qt;
            	    // IbmSparqlExtAstWalker.g:451:9: ( ^( VALUE o= object ) )+
            	    int cnt50=0;
            	    loop50:
            	    do {
            	        int alt50=2;
            	        int LA50_0 = input.LA(1);

            	        if ( (LA50_0==VALUE) ) {
            	            alt50=1;
            	        }


            	        switch (alt50) {
            	    	case 1 :
            	    	    // IbmSparqlExtAstWalker.g:451:11: ^( VALUE o= object )
            	    	    {
            	    	    match(input,VALUE,FOLLOW_VALUE_in_triples23061); if (state.failed) return qt;

            	    	    match(input, Token.DOWN, null); if (state.failed) return qt;
            	    	    pushFollow(FOLLOW_object_in_triples23066);
            	    	    o=object();

            	    	    state._fsp--;
            	    	    if (state.failed) return qt;

            	    	    match(input, Token.UP, null); if (state.failed) return qt;
            	    	    if ( state.backtracking==0 ) {
            	    	       ple.addObject(o); 
            	    	    }

            	    	    }
            	    	    break;

            	    	default :
            	    	    if ( cnt50 >= 1 ) break loop50;
            	    	    if (state.backtracking>0) {state.failed=true; return qt;}
            	                EarlyExitException eee =
            	                    new EarlyExitException(50, input);
            	                throw eee;
            	        }
            	        cnt50++;
            	    } while (true);

            	    if ( state.backtracking==0 ) {
            	       qt.addPropertyListElement(ple); 
            	    }

            	    match(input, Token.UP, null); if (state.failed) return qt;

            	    }
            	    break;

            	default :
            	    break loop51;
                }
            } while (true);


            match(input, Token.UP, null); if (state.failed) return qt;

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return qt;
    }
    // $ANTLR end "triples2"


    // $ANTLR start "graphPatternNewBGP"
    // IbmSparqlExtAstWalker.g:459:1: graphPatternNewBGP[PatternSet p, Set<BlankNodeVariable> vars] : (u= groupMinusOrUnionGraphPattern | o= optionalGraphPattern | g= graphGraphPattern | serviceGraphPattern | b= bind | v= inlineData );
    public final void graphPatternNewBGP(PatternSet p, Set<BlankNodeVariable> vars) throws RecognitionException {
        Pattern u = null;

        Pattern o = null;

        Pattern g = null;

        Pattern b = null;

        ValuesPattern v = null;


        try {
            // IbmSparqlExtAstWalker.g:460:5: (u= groupMinusOrUnionGraphPattern | o= optionalGraphPattern | g= graphGraphPattern | serviceGraphPattern | b= bind | v= inlineData )
            int alt52=6;
            switch ( input.LA(1) ) {
            case GROUP_GRAPH_PATTERN:
            case SUB_SELECT:
            case UNION:
            case MINUS:
                {
                alt52=1;
                }
                break;
            case OPTIONAL:
                {
                alt52=2;
                }
                break;
            case GRAPH:
                {
                alt52=3;
                }
                break;
            case SERVICE:
                {
                alt52=4;
                }
                break;
            case BIND:
                {
                alt52=5;
                }
                break;
            case VALUES:
                {
                alt52=6;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 52, 0, input);

                throw nvae;
            }

            switch (alt52) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:460:9: u= groupMinusOrUnionGraphPattern
                    {
                    pushFollow(FOLLOW_groupMinusOrUnionGraphPattern_in_graphPatternNewBGP3126);
                    u=groupMinusOrUnionGraphPattern();

                    state._fsp--;
                    if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       p.addPattern(u); 
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:462:5: o= optionalGraphPattern
                    {
                    pushFollow(FOLLOW_optionalGraphPattern_in_graphPatternNewBGP3146);
                    o=optionalGraphPattern();

                    state._fsp--;
                    if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       if (p.isEmpty()) {
                                      p.addPattern(o);
                                } else { 
                                      p.addOptional(o); 
                                } 
                              
                    }

                    }
                    break;
                case 3 :
                    // IbmSparqlExtAstWalker.g:469:5: g= graphGraphPattern
                    {
                    pushFollow(FOLLOW_graphGraphPattern_in_graphPatternNewBGP3167);
                    g=graphGraphPattern();

                    state._fsp--;
                    if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       
                                  p.addPattern(g);
                              
                    }

                    }
                    break;
                case 4 :
                    // IbmSparqlExtAstWalker.g:473:5: serviceGraphPattern
                    {
                    pushFollow(FOLLOW_serviceGraphPattern_in_graphPatternNewBGP3186);
                    serviceGraphPattern();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // IbmSparqlExtAstWalker.g:474:5: b= bind
                    {
                    pushFollow(FOLLOW_bind_in_graphPatternNewBGP3195);
                    b=bind();

                    state._fsp--;
                    if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                      					
                      			p.addPattern(b);
                      	    
                    }

                    }
                    break;
                case 6 :
                    // IbmSparqlExtAstWalker.g:478:9: v= inlineData
                    {
                    pushFollow(FOLLOW_inlineData_in_graphPatternNewBGP3215);
                    v=inlineData();

                    state._fsp--;
                    if (state.failed) return ;
                    if ( state.backtracking==0 ) {

                             		p.addPattern(v);
                             
                    }

                    }
                    break;

            }
        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return ;
    }
    // $ANTLR end "graphPatternNewBGP"


    // $ANTLR start "inlineData"
    // IbmSparqlExtAstWalker.g:484:1: inlineData returns [ValuesPattern v] : ^( VALUES d= dataBlock ) ;
    public final ValuesPattern inlineData() throws RecognitionException {
        ValuesPattern v = null;

        Values d = null;


        try {
            // IbmSparqlExtAstWalker.g:485:5: ( ^( VALUES d= dataBlock ) )
            // IbmSparqlExtAstWalker.g:485:9: ^( VALUES d= dataBlock )
            {
            match(input,VALUES,FOLLOW_VALUES_in_inlineData3248); if (state.failed) return v;

            match(input, Token.DOWN, null); if (state.failed) return v;
            pushFollow(FOLLOW_dataBlock_in_inlineData3252);
            d=dataBlock();

            state._fsp--;
            if (state.failed) return v;
            if ( state.backtracking==0 ) {
              v =new ValuesPattern(d);
            }

            match(input, Token.UP, null); if (state.failed) return v;

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return v;
    }
    // $ANTLR end "inlineData"


    // $ANTLR start "dataBlock"
    // IbmSparqlExtAstWalker.g:488:1: dataBlock returns [Values values] : ^( INLINE_DATA ( ( NIL )=> NIL | (v= var )* ) ( ( NIL )=> NIL | (d= dataBlockValue )* ) ) ;
    public final Values dataBlock() throws RecognitionException {
        Values values = null;

        String v = null;

        Expression d = null;


        List<Variable> vars = new ArrayList<Variable>(); 
               List<List<Expression>> expressions = new LinkedList<List<Expression>>(); 
               values = new Values(vars, expressions);
               List<Expression> rowExp = new LinkedList<Expression>();
               int i = 0; 
               
        try {
            // IbmSparqlExtAstWalker.g:495:5: ( ^( INLINE_DATA ( ( NIL )=> NIL | (v= var )* ) ( ( NIL )=> NIL | (d= dataBlockValue )* ) ) )
            // IbmSparqlExtAstWalker.g:495:9: ^( INLINE_DATA ( ( NIL )=> NIL | (v= var )* ) ( ( NIL )=> NIL | (d= dataBlockValue )* ) )
            {
            match(input,INLINE_DATA,FOLLOW_INLINE_DATA_in_dataBlock3284); if (state.failed) return values;

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); if (state.failed) return values;
                // IbmSparqlExtAstWalker.g:495:23: ( ( NIL )=> NIL | (v= var )* )
                int alt54=2;
                int LA54_0 = input.LA(1);

                if ( (LA54_0==NIL) ) {
                    int LA54_1 = input.LA(2);

                    if ( (synpred1_IbmSparqlExtAstWalker()) ) {
                        alt54=1;
                    }
                    else if ( (true) ) {
                        alt54=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return values;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 54, 1, input);

                        throw nvae;
                    }
                }
                else if ( (LA54_0==UP||(LA54_0>=VAR && LA54_0<=PREFIXED_NS)||(LA54_0>=STRING && LA54_0<=BOOLEAN)||(LA54_0>=BIG_INTEGER && LA54_0<=BIG_DECIMAL)||LA54_0==UNDEF||LA54_0==IRI||LA54_0==DOUBLE) ) {
                    alt54=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return values;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 54, 0, input);

                    throw nvae;
                }
                switch (alt54) {
                    case 1 :
                        // IbmSparqlExtAstWalker.g:495:25: ( NIL )=> NIL
                        {
                        match(input,NIL,FOLLOW_NIL_in_dataBlock3294); if (state.failed) return values;

                        }
                        break;
                    case 2 :
                        // IbmSparqlExtAstWalker.g:495:40: (v= var )*
                        {
                        // IbmSparqlExtAstWalker.g:495:40: (v= var )*
                        loop53:
                        do {
                            int alt53=2;
                            int LA53_0 = input.LA(1);

                            if ( (LA53_0==VAR) ) {
                                alt53=1;
                            }


                            switch (alt53) {
                        	case 1 :
                        	    // IbmSparqlExtAstWalker.g:495:41: v= var
                        	    {
                        	    pushFollow(FOLLOW_var_in_dataBlock3301);
                        	    v=var();

                        	    state._fsp--;
                        	    if (state.failed) return values;
                        	    if ( state.backtracking==0 ) {
                        	      vars.add(new Variable(v));
                        	    }

                        	    }
                        	    break;

                        	default :
                        	    break loop53;
                            }
                        } while (true);


                        }
                        break;

                }

                // IbmSparqlExtAstWalker.g:495:79: ( ( NIL )=> NIL | (d= dataBlockValue )* )
                int alt56=2;
                int LA56_0 = input.LA(1);

                if ( (LA56_0==NIL) && (synpred2_IbmSparqlExtAstWalker())) {
                    alt56=1;
                }
                else if ( (LA56_0==UP||(LA56_0>=PREFIXED_NAME && LA56_0<=PREFIXED_NS)||(LA56_0>=STRING && LA56_0<=BOOLEAN)||(LA56_0>=BIG_INTEGER && LA56_0<=BIG_DECIMAL)||LA56_0==UNDEF||LA56_0==IRI||LA56_0==DOUBLE) ) {
                    alt56=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return values;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 56, 0, input);

                    throw nvae;
                }
                switch (alt56) {
                    case 1 :
                        // IbmSparqlExtAstWalker.g:495:81: ( NIL )=> NIL
                        {
                        match(input,NIL,FOLLOW_NIL_in_dataBlock3316); if (state.failed) return values;

                        }
                        break;
                    case 2 :
                        // IbmSparqlExtAstWalker.g:495:96: (d= dataBlockValue )*
                        {
                        // IbmSparqlExtAstWalker.g:495:96: (d= dataBlockValue )*
                        loop55:
                        do {
                            int alt55=2;
                            int LA55_0 = input.LA(1);

                            if ( ((LA55_0>=PREFIXED_NAME && LA55_0<=PREFIXED_NS)||(LA55_0>=STRING && LA55_0<=BOOLEAN)||(LA55_0>=BIG_INTEGER && LA55_0<=BIG_DECIMAL)||LA55_0==UNDEF||LA55_0==IRI||LA55_0==DOUBLE) ) {
                                alt55=1;
                            }


                            switch (alt55) {
                        	case 1 :
                        	    // IbmSparqlExtAstWalker.g:495:97: d= dataBlockValue
                        	    {
                        	    pushFollow(FOLLOW_dataBlockValue_in_dataBlock3323);
                        	    d=dataBlockValue();

                        	    state._fsp--;
                        	    if (state.failed) return values;
                        	    if ( state.backtracking==0 ) {
                        	       int row = i % vars.size(); 
                        	          	  if (row == 0) {
                        	          	  	rowExp = new LinkedList<Expression>();
                        	          	  	expressions.add(rowExp);
                        	          	  }
                        	          	  rowExp.add(d);
                        	          	  i++;
                        	      		
                        	    }

                        	    }
                        	    break;

                        	default :
                        	    break loop55;
                            }
                        } while (true);


                        }
                        break;

                }


                match(input, Token.UP, null); if (state.failed) return values;
            }

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return values;
    }
    // $ANTLR end "dataBlock"


    // $ANTLR start "dataBlockValue"
    // IbmSparqlExtAstWalker.g:506:1: dataBlockValue returns [Expression e] : (i= iRIref | r= rDFLiteral | d= numericLiteral | b= booleanLiteral | u= UNDEF );
    public final Expression dataBlockValue() throws RecognitionException {
        Expression e = null;

        XTree u=null;
        IRI i = null;

        StringLiteral r = null;

        Constant d = null;

        Boolean b = null;


        try {
            // IbmSparqlExtAstWalker.g:507:2: (i= iRIref | r= rDFLiteral | d= numericLiteral | b= booleanLiteral | u= UNDEF )
            int alt57=5;
            switch ( input.LA(1) ) {
            case PREFIXED_NAME:
            case PREFIXED_NS:
            case IRI:
                {
                alt57=1;
                }
                break;
            case STRING:
                {
                alt57=2;
                }
                break;
            case BIG_INTEGER:
            case BIG_DECIMAL:
            case DOUBLE:
                {
                alt57=3;
                }
                break;
            case BOOLEAN:
                {
                alt57=4;
                }
                break;
            case UNDEF:
                {
                alt57=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return e;}
                NoViableAltException nvae =
                    new NoViableAltException("", 57, 0, input);

                throw nvae;
            }

            switch (alt57) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:507:7: i= iRIref
                    {
                    pushFollow(FOLLOW_iRIref_in_dataBlockValue3359);
                    i=iRIref();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                      e = new ConstantExpression(i);
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:508:7: r= rDFLiteral
                    {
                    pushFollow(FOLLOW_rDFLiteral_in_dataBlockValue3375);
                    r=rDFLiteral();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                      e = new ConstantExpression(r);
                    }

                    }
                    break;
                case 3 :
                    // IbmSparqlExtAstWalker.g:509:7: d= numericLiteral
                    {
                    pushFollow(FOLLOW_numericLiteral_in_dataBlockValue3390);
                    d=numericLiteral();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                      e = new ConstantExpression(d);
                    }

                    }
                    break;
                case 4 :
                    // IbmSparqlExtAstWalker.g:510:7: b= booleanLiteral
                    {
                    pushFollow(FOLLOW_booleanLiteral_in_dataBlockValue3405);
                    b=booleanLiteral();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                      e = new ConstantExpression(b);
                    }

                    }
                    break;
                case 5 :
                    // IbmSparqlExtAstWalker.g:511:7: u= UNDEF
                    {
                    u=(XTree)match(input,UNDEF,FOLLOW_UNDEF_in_dataBlockValue3421); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                      e = new UNDEFExpression();
                    }

                    }
                    break;

            }
        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return e;
    }
    // $ANTLR end "dataBlockValue"


    // $ANTLR start "optionalGraphPattern"
    // IbmSparqlExtAstWalker.g:514:1: optionalGraphPattern returns [Pattern p] : ^( OPTIONAL g= groupGraphPattern[false] ) ;
    public final Pattern optionalGraphPattern() throws RecognitionException {
        Pattern p = null;

        Pattern g = null;


        try {
            // IbmSparqlExtAstWalker.g:515:2: ( ^( OPTIONAL g= groupGraphPattern[false] ) )
            // IbmSparqlExtAstWalker.g:515:7: ^( OPTIONAL g= groupGraphPattern[false] )
            {
            match(input,OPTIONAL,FOLLOW_OPTIONAL_in_optionalGraphPattern3451); if (state.failed) return p;

            match(input, Token.DOWN, null); if (state.failed) return p;
            pushFollow(FOLLOW_groupGraphPattern_in_optionalGraphPattern3455);
            g=groupGraphPattern(false);

            state._fsp--;
            if (state.failed) return p;

            match(input, Token.UP, null); if (state.failed) return p;
            if ( state.backtracking==0 ) {
               p = g; 
            }

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return p;
    }
    // $ANTLR end "optionalGraphPattern"


    // $ANTLR start "graphGraphPattern"
    // IbmSparqlExtAstWalker.g:518:1: graphGraphPattern returns [Pattern p] : ^( GRAPH r= varOrIRIref2 g= groupGraphPattern[false] ) ;
    public final Pattern graphGraphPattern() throws RecognitionException {
        Pattern p = null;

        BinaryUnion<Variable, IRI> r = null;

        Pattern g = null;


        try {
            // IbmSparqlExtAstWalker.g:519:2: ( ^( GRAPH r= varOrIRIref2 g= groupGraphPattern[false] ) )
            // IbmSparqlExtAstWalker.g:519:7: ^( GRAPH r= varOrIRIref2 g= groupGraphPattern[false] )
            {
            match(input,GRAPH,FOLLOW_GRAPH_in_graphGraphPattern3485); if (state.failed) return p;

            match(input, Token.DOWN, null); if (state.failed) return p;
            pushFollow(FOLLOW_varOrIRIref2_in_graphGraphPattern3489);
            r=varOrIRIref2();

            state._fsp--;
            if (state.failed) return p;
            pushFollow(FOLLOW_groupGraphPattern_in_graphGraphPattern3493);
            g=groupGraphPattern(false);

            state._fsp--;
            if (state.failed) return p;

            match(input, Token.UP, null); if (state.failed) return p;
            if ( state.backtracking==0 ) {
               g.setGraphRestriction(r);  p = g; 
            }

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return p;
    }
    // $ANTLR end "graphGraphPattern"


    // $ANTLR start "serviceGraphPattern"
    // IbmSparqlExtAstWalker.g:522:1: serviceGraphPattern returns [Pattern p] : ^(top= SERVICE ( SILENT )? s= varOrIRIref g= groupGraphPattern[false] ) ;
    public final Pattern serviceGraphPattern() throws RecognitionException {
        Pattern p = null;

        XTree top=null;
        QueryTripleTerm s = null;

        Pattern g = null;


         boolean silent=false; 
        try {
            // IbmSparqlExtAstWalker.g:524:2: ( ^(top= SERVICE ( SILENT )? s= varOrIRIref g= groupGraphPattern[false] ) )
            // IbmSparqlExtAstWalker.g:524:7: ^(top= SERVICE ( SILENT )? s= varOrIRIref g= groupGraphPattern[false] )
            {
            top=(XTree)match(input,SERVICE,FOLLOW_SERVICE_in_serviceGraphPattern3529); if (state.failed) return p;

            match(input, Token.DOWN, null); if (state.failed) return p;
            // IbmSparqlExtAstWalker.g:524:21: ( SILENT )?
            int alt58=2;
            int LA58_0 = input.LA(1);

            if ( (LA58_0==SILENT) ) {
                alt58=1;
            }
            switch (alt58) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:524:22: SILENT
                    {
                    match(input,SILENT,FOLLOW_SILENT_in_serviceGraphPattern3532); if (state.failed) return p;
                    if ( state.backtracking==0 ) {
                       silent=true; 
                    }

                    }
                    break;

            }

            pushFollow(FOLLOW_varOrIRIref_in_serviceGraphPattern3540);
            s=varOrIRIref();

            state._fsp--;
            if (state.failed) return p;
            pushFollow(FOLLOW_groupGraphPattern_in_serviceGraphPattern3544);
            g=groupGraphPattern(false);

            state._fsp--;
            if (state.failed) return p;

            match(input, Token.UP, null); if (state.failed) return p;
            if ( state.backtracking==0 ) {

                         p = new ServicePattern(s, top.matched, silent, g);
                       
            }

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return p;
    }
    // $ANTLR end "serviceGraphPattern"


    // $ANTLR start "bind"
    // IbmSparqlExtAstWalker.g:531:1: bind returns [Pattern p] : (bp= bind1 | bf= bind2 );
    public final Pattern bind() throws RecognitionException {
        Pattern p = null;

        Pattern bp = null;

        Pattern bf = null;


        try {
            // IbmSparqlExtAstWalker.g:532:2: (bp= bind1 | bf= bind2 )
            int alt59=2;
            int LA59_0 = input.LA(1);

            if ( (LA59_0==BIND) ) {
                int LA59_1 = input.LA(2);

                if ( (LA59_1==DOWN) ) {
                    int LA59_2 = input.LA(3);

                    if ( (LA59_2==VAR) ) {
                        alt59=1;
                    }
                    else if ( (LA59_2==FUNCCALL) ) {
                        alt59=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return p;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 59, 2, input);

                        throw nvae;
                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return p;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 59, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return p;}
                NoViableAltException nvae =
                    new NoViableAltException("", 59, 0, input);

                throw nvae;
            }
            switch (alt59) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:532:4: bp= bind1
                    {
                    pushFollow(FOLLOW_bind1_in_bind3575);
                    bp=bind1();

                    state._fsp--;
                    if (state.failed) return p;
                    if ( state.backtracking==0 ) {
                      p =bp;
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:533:4: bf= bind2
                    {
                    pushFollow(FOLLOW_bind2_in_bind3584);
                    bf=bind2();

                    state._fsp--;
                    if (state.failed) return p;
                    if ( state.backtracking==0 ) {
                      p =bf;
                    }

                    }
                    break;

            }
        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return p;
    }
    // $ANTLR end "bind"


    // $ANTLR start "bind1"
    // IbmSparqlExtAstWalker.g:536:1: bind1 returns [Pattern p] : ^( BIND v= var e= expression ) ;
    public final Pattern bind1() throws RecognitionException {
        Pattern p = null;

        String v = null;

        Expression e = null;


        try {
            // IbmSparqlExtAstWalker.g:537:2: ( ^( BIND v= var e= expression ) )
            // IbmSparqlExtAstWalker.g:537:7: ^( BIND v= var e= expression )
            {
            match(input,BIND,FOLLOW_BIND_in_bind13606); if (state.failed) return p;

            match(input, Token.DOWN, null); if (state.failed) return p;
            pushFollow(FOLLOW_var_in_bind13610);
            v=var();

            state._fsp--;
            if (state.failed) return p;
            pushFollow(FOLLOW_expression_in_bind13616);
            e=expression();

            state._fsp--;
            if (state.failed) return p;

            match(input, Token.UP, null); if (state.failed) return p;
            if ( state.backtracking==0 ) {
               p = new BindPattern(v, e); 
            }

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return p;
    }
    // $ANTLR end "bind1"


    // $ANTLR start "bind2"
    // IbmSparqlExtAstWalker.g:540:1: bind2 returns [Pattern p] : ^( BIND (f= funcCall ) (v= var )+ ) ;
    public final Pattern bind2() throws RecognitionException {
        Pattern p = null;

        BindFunctionCall f = null;

        String v = null;


         p = new BindFunctionPattern(); 
        try {
            // IbmSparqlExtAstWalker.g:542:2: ( ^( BIND (f= funcCall ) (v= var )+ ) )
            // IbmSparqlExtAstWalker.g:542:7: ^( BIND (f= funcCall ) (v= var )+ )
            {
            match(input,BIND,FOLLOW_BIND_in_bind23649); if (state.failed) return p;

            match(input, Token.DOWN, null); if (state.failed) return p;
            // IbmSparqlExtAstWalker.g:543:4: (f= funcCall )
            // IbmSparqlExtAstWalker.g:543:5: f= funcCall
            {
            pushFollow(FOLLOW_funcCall_in_bind23658);
            f=funcCall();

            state._fsp--;
            if (state.failed) return p;
            if ( state.backtracking==0 ) {
               ((BindFunctionPattern) p).setFuncCall(f); 
            }

            }

            // IbmSparqlExtAstWalker.g:544:4: (v= var )+
            int cnt60=0;
            loop60:
            do {
                int alt60=2;
                int LA60_0 = input.LA(1);

                if ( (LA60_0==VAR) ) {
                    alt60=1;
                }


                switch (alt60) {
            	case 1 :
            	    // IbmSparqlExtAstWalker.g:544:5: v= var
            	    {
            	    pushFollow(FOLLOW_var_in_bind23671);
            	    v=var();

            	    state._fsp--;
            	    if (state.failed) return p;
            	    if ( state.backtracking==0 ) {
            	       ((BindFunctionPattern) p).addVar(v); 
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt60 >= 1 ) break loop60;
            	    if (state.backtracking>0) {state.failed=true; return p;}
                        EarlyExitException eee =
                            new EarlyExitException(60, input);
                        throw eee;
                }
                cnt60++;
            } while (true);


            match(input, Token.UP, null); if (state.failed) return p;

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return p;
    }
    // $ANTLR end "bind2"


    // $ANTLR start "funcCall"
    // IbmSparqlExtAstWalker.g:548:1: funcCall returns [BindFunctionCall f] : ^( FUNCCALL (fn= VARNAME ) (v= var )+ ) ;
    public final BindFunctionCall funcCall() throws RecognitionException {
        BindFunctionCall f = null;

        XTree fn=null;
        String v = null;


         f = new BindFunctionCall(); 
        try {
            // IbmSparqlExtAstWalker.g:550:2: ( ^( FUNCCALL (fn= VARNAME ) (v= var )+ ) )
            // IbmSparqlExtAstWalker.g:550:7: ^( FUNCCALL (fn= VARNAME ) (v= var )+ )
            {
            match(input,FUNCCALL,FOLLOW_FUNCCALL_in_funcCall3711); if (state.failed) return f;

            match(input, Token.DOWN, null); if (state.failed) return f;
            // IbmSparqlExtAstWalker.g:551:4: (fn= VARNAME )
            // IbmSparqlExtAstWalker.g:551:5: fn= VARNAME
            {
            fn=(XTree)match(input,VARNAME,FOLLOW_VARNAME_in_funcCall3720); if (state.failed) return f;
            if ( state.backtracking==0 ) {
              f.setName(fn); 
            }

            }

            // IbmSparqlExtAstWalker.g:552:4: (v= var )+
            int cnt61=0;
            loop61:
            do {
                int alt61=2;
                int LA61_0 = input.LA(1);

                if ( (LA61_0==VAR) ) {
                    alt61=1;
                }


                switch (alt61) {
            	case 1 :
            	    // IbmSparqlExtAstWalker.g:552:5: v= var
            	    {
            	    pushFollow(FOLLOW_var_in_funcCall3732);
            	    v=var();

            	    state._fsp--;
            	    if (state.failed) return f;
            	    if ( state.backtracking==0 ) {
            	      f.addVar(v);
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt61 >= 1 ) break loop61;
            	    if (state.backtracking>0) {state.failed=true; return f;}
                        EarlyExitException eee =
                            new EarlyExitException(61, input);
                        throw eee;
                }
                cnt61++;
            } while (true);


            match(input, Token.UP, null); if (state.failed) return f;

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return f;
    }
    // $ANTLR end "funcCall"


    // $ANTLR start "groupMinusOrUnionGraphPattern"
    // IbmSparqlExtAstWalker.g:556:1: groupMinusOrUnionGraphPattern returns [Pattern r] : ( ^( UNION g1= groupGraphPattern[false] (g2= groupGraphPattern[false] )+ ) | ^( MINUS g1= groupGraphPattern[false] ) | g3= groupGraphPattern[false] );
    public final Pattern groupMinusOrUnionGraphPattern() throws RecognitionException {
        Pattern r = null;

        Pattern g1 = null;

        Pattern g2 = null;

        Pattern g3 = null;



        	  PatternSet p = null;
        	
        try {
            // IbmSparqlExtAstWalker.g:560:2: ( ^( UNION g1= groupGraphPattern[false] (g2= groupGraphPattern[false] )+ ) | ^( MINUS g1= groupGraphPattern[false] ) | g3= groupGraphPattern[false] )
            int alt63=3;
            switch ( input.LA(1) ) {
            case UNION:
                {
                alt63=1;
                }
                break;
            case MINUS:
                {
                alt63=2;
                }
                break;
            case GROUP_GRAPH_PATTERN:
            case SUB_SELECT:
                {
                alt63=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return r;}
                NoViableAltException nvae =
                    new NoViableAltException("", 63, 0, input);

                throw nvae;
            }

            switch (alt63) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:560:6: ^( UNION g1= groupGraphPattern[false] (g2= groupGraphPattern[false] )+ )
                    {
                    match(input,UNION,FOLLOW_UNION_in_groupMinusOrUnionGraphPattern3767); if (state.failed) return r;

                    match(input, Token.DOWN, null); if (state.failed) return r;
                    pushFollow(FOLLOW_groupGraphPattern_in_groupMinusOrUnionGraphPattern3788);
                    g1=groupGraphPattern(false);

                    state._fsp--;
                    if (state.failed) return r;
                    if ( state.backtracking==0 ) {
                       p = new PatternSet(PatternSet.EPatternSetType.UNION); 
                      			      r = p;
                                        p.addPattern(g1);	
                    }
                    // IbmSparqlExtAstWalker.g:565:5: (g2= groupGraphPattern[false] )+
                    int cnt62=0;
                    loop62:
                    do {
                        int alt62=2;
                        int LA62_0 = input.LA(1);

                        if ( (LA62_0==GROUP_GRAPH_PATTERN||LA62_0==SUB_SELECT) ) {
                            alt62=1;
                        }


                        switch (alt62) {
                    	case 1 :
                    	    // IbmSparqlExtAstWalker.g:565:6: g2= groupGraphPattern[false]
                    	    {
                    	    pushFollow(FOLLOW_groupGraphPattern_in_groupMinusOrUnionGraphPattern3819);
                    	    g2=groupGraphPattern(false);

                    	    state._fsp--;
                    	    if (state.failed) return r;
                    	    if ( state.backtracking==0 ) {
                    	       
                    	                         p.addPattern(g2); 
                    	                       
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt62 >= 1 ) break loop62;
                    	    if (state.backtracking>0) {state.failed=true; return r;}
                                EarlyExitException eee =
                                    new EarlyExitException(62, input);
                                throw eee;
                        }
                        cnt62++;
                    } while (true);


                    match(input, Token.UP, null); if (state.failed) return r;

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:569:9: ^( MINUS g1= groupGraphPattern[false] )
                    {
                    match(input,MINUS,FOLLOW_MINUS_in_groupMinusOrUnionGraphPattern3855); if (state.failed) return r;

                    match(input, Token.DOWN, null); if (state.failed) return r;
                    pushFollow(FOLLOW_groupGraphPattern_in_groupMinusOrUnionGraphPattern3870);
                    g1=groupGraphPattern(false);

                    state._fsp--;
                    if (state.failed) return r;
                    if ( state.backtracking==0 ) {
                       p = new PatternSet(PatternSet.EPatternSetType.MINUS); 
                      			 r = p;
                                   p.addPattern(g1); 
                    }

                    match(input, Token.UP, null); if (state.failed) return r;

                    }
                    break;
                case 3 :
                    // IbmSparqlExtAstWalker.g:574:7: g3= groupGraphPattern[false]
                    {
                    pushFollow(FOLLOW_groupGraphPattern_in_groupMinusOrUnionGraphPattern3897);
                    g3=groupGraphPattern(false);

                    state._fsp--;
                    if (state.failed) return r;
                    if ( state.backtracking==0 ) {
                       
                                r = g3;
                              
                    }

                    }
                    break;

            }
        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return r;
    }
    // $ANTLR end "groupMinusOrUnionGraphPattern"


    // $ANTLR start "filter"
    // IbmSparqlExtAstWalker.g:580:1: filter returns [Expression e] : ^( FILTER c= constraint ) ;
    public final Expression filter() throws RecognitionException {
        Expression e = null;

        Expression c = null;


        try {
            // IbmSparqlExtAstWalker.g:581:2: ( ^( FILTER c= constraint ) )
            // IbmSparqlExtAstWalker.g:581:6: ^( FILTER c= constraint )
            {
            match(input,FILTER,FOLLOW_FILTER_in_filter3930); if (state.failed) return e;

            match(input, Token.DOWN, null); if (state.failed) return e;
            pushFollow(FOLLOW_constraint_in_filter3934);
            c=constraint();

            state._fsp--;
            if (state.failed) return e;

            match(input, Token.UP, null); if (state.failed) return e;
            if ( state.backtracking==0 ) {
               e = c; 
            }

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return e;
    }
    // $ANTLR end "filter"


    // $ANTLR start "constraint"
    // IbmSparqlExtAstWalker.g:584:1: constraint returns [Expression e] : (be= brackettedExpression | bc= builtInCall | fc= functionCall );
    public final Expression constraint() throws RecognitionException {
        Expression e = null;

        Expression be = null;

        Expression bc = null;

        FunctionCall fc = null;


        try {
            // IbmSparqlExtAstWalker.g:585:2: (be= brackettedExpression | bc= builtInCall | fc= functionCall )
            int alt64=3;
            switch ( input.LA(1) ) {
            case EXPRESSION:
                {
                alt64=1;
                }
                break;
            case NOT_EXISTS:
            case STR:
            case LANG:
            case LANGMATCHES:
            case DATATYPE:
            case BOUND:
            case IRI:
            case URI:
            case BNODE:
            case RAND:
            case ABS:
            case CEIL:
            case FLOOR:
            case ROUND:
            case CONCAT:
            case STRLEN:
            case UCASE:
            case LCASE:
            case ENCODE_FOR_URI:
            case CONTAINS:
            case STRSTARTS:
            case STRENDS:
            case STRBEFORE:
            case STRAFTER:
            case YEAR:
            case MONTH:
            case DAY:
            case HOURS:
            case MINUTES:
            case SECONDS:
            case TIMEZONE:
            case TZ:
            case NOW:
            case UUID:
            case STRUUID:
            case MD5:
            case SHA1:
            case SHA256:
            case SHA384:
            case SHA512:
            case COALESCE:
            case IF:
            case STRLANG:
            case STRDT:
            case SAMETERM:
            case ISIRI:
            case ISURI:
            case ISBLANK:
            case ISLITERAL:
            case ISNUMERIC:
            case REGEX:
            case SUBSTR:
            case REPLACE:
            case EXISTS:
                {
                alt64=2;
                }
                break;
            case FUNCTION:
                {
                alt64=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return e;}
                NoViableAltException nvae =
                    new NoViableAltException("", 64, 0, input);

                throw nvae;
            }

            switch (alt64) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:585:6: be= brackettedExpression
                    {
                    pushFollow(FOLLOW_brackettedExpression_in_constraint3958);
                    be=brackettedExpression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = be; 
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:586:5: bc= builtInCall
                    {
                    pushFollow(FOLLOW_builtInCall_in_constraint3968);
                    bc=builtInCall();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = bc; 
                    }

                    }
                    break;
                case 3 :
                    // IbmSparqlExtAstWalker.g:587:5: fc= functionCall
                    {
                    pushFollow(FOLLOW_functionCall_in_constraint3981);
                    fc=functionCall();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new FunctionCallExpression(fc); 
                    }

                    }
                    break;

            }
        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return e;
    }
    // $ANTLR end "constraint"


    // $ANTLR start "functionCall"
    // IbmSparqlExtAstWalker.g:590:1: functionCall returns [FunctionCall fc] : ^( FUNCTION i= iRIref a= argList ) ;
    public final FunctionCall functionCall() throws RecognitionException {
        FunctionCall fc = null;

        IRI i = null;

        List<Expression> a = null;


        try {
            // IbmSparqlExtAstWalker.g:591:2: ( ^( FUNCTION i= iRIref a= argList ) )
            // IbmSparqlExtAstWalker.g:591:6: ^( FUNCTION i= iRIref a= argList )
            {
            match(input,FUNCTION,FOLLOW_FUNCTION_in_functionCall4003); if (state.failed) return fc;

            match(input, Token.DOWN, null); if (state.failed) return fc;
            pushFollow(FOLLOW_iRIref_in_functionCall4007);
            i=iRIref();

            state._fsp--;
            if (state.failed) return fc;
            pushFollow(FOLLOW_argList_in_functionCall4011);
            a=argList();

            state._fsp--;
            if (state.failed) return fc;

            match(input, Token.UP, null); if (state.failed) return fc;
            if ( state.backtracking==0 ) {
               fc = new FunctionCall(i, a); 
            }

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return fc;
    }
    // $ANTLR end "functionCall"


    // $ANTLR start "argList"
    // IbmSparqlExtAstWalker.g:594:1: argList returns [List<Expression> args] : ( NIL | DISTINCT (e1= expression )+ | (e2= expression )+ );
    public final List<Expression> argList() throws RecognitionException {
        List<Expression> args = null;

        Expression e1 = null;

        Expression e2 = null;


         args = new ArrayList<Expression>(); 
        try {
            // IbmSparqlExtAstWalker.g:596:5: ( NIL | DISTINCT (e1= expression )+ | (e2= expression )+ )
            int alt67=3;
            switch ( input.LA(1) ) {
            case NIL:
                {
                alt67=1;
                }
                break;
            case DISTINCT:
                {
                alt67=2;
                }
                break;
            case BROKEN_PLUS:
            case BROKEN_MINUS:
            case VAR:
            case PREFIXED_NAME:
            case PREFIXED_NS:
            case FUNCTION:
            case EXPRESSION:
            case NOT_EXISTS:
            case STRING:
            case BOOLEAN:
            case LTE:
            case BIG_INTEGER:
            case BIG_DECIMAL:
            case LOGICAL_OR:
            case LOGICAL_AND:
            case LT:
            case IN:
            case NOT:
            case STR:
            case LANG:
            case LANGMATCHES:
            case DATATYPE:
            case BOUND:
            case IRI:
            case URI:
            case BNODE:
            case RAND:
            case ABS:
            case CEIL:
            case FLOOR:
            case ROUND:
            case CONCAT:
            case STRLEN:
            case UCASE:
            case LCASE:
            case ENCODE_FOR_URI:
            case CONTAINS:
            case STRSTARTS:
            case STRENDS:
            case STRBEFORE:
            case STRAFTER:
            case YEAR:
            case MONTH:
            case DAY:
            case HOURS:
            case MINUTES:
            case SECONDS:
            case TIMEZONE:
            case TZ:
            case NOW:
            case UUID:
            case STRUUID:
            case MD5:
            case SHA1:
            case SHA256:
            case SHA384:
            case SHA512:
            case COALESCE:
            case IF:
            case STRLANG:
            case STRDT:
            case SAMETERM:
            case ISIRI:
            case ISURI:
            case ISBLANK:
            case ISLITERAL:
            case ISNUMERIC:
            case REGEX:
            case SUBSTR:
            case REPLACE:
            case EXISTS:
            case COUNT:
            case SUM:
            case MIN:
            case MAX:
            case AVG:
            case SAMPLE:
            case GROUP_CONCAT:
            case DOUBLE:
            case 256:
            case 259:
            case 262:
            case 263:
            case 264:
            case 265:
            case 266:
            case 267:
            case 268:
                {
                alt67=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return args;}
                NoViableAltException nvae =
                    new NoViableAltException("", 67, 0, input);

                throw nvae;
            }

            switch (alt67) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:596:9: NIL
                    {
                    match(input,NIL,FOLLOW_NIL_in_argList4040); if (state.failed) return args;

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:597:6: DISTINCT (e1= expression )+
                    {
                    match(input,DISTINCT,FOLLOW_DISTINCT_in_argList4053); if (state.failed) return args;
                    // IbmSparqlExtAstWalker.g:597:15: (e1= expression )+
                    int cnt65=0;
                    loop65:
                    do {
                        int alt65=2;
                        int LA65_0 = input.LA(1);

                        if ( ((LA65_0>=BROKEN_PLUS && LA65_0<=BROKEN_MINUS)||(LA65_0>=VAR && LA65_0<=NOT_EXISTS)||(LA65_0>=STRING && LA65_0<=BOOLEAN)||LA65_0==LTE||(LA65_0>=BIG_INTEGER && LA65_0<=BIG_DECIMAL)||(LA65_0>=LOGICAL_OR && LA65_0<=SHA1)||(LA65_0>=SHA256 && LA65_0<=GROUP_CONCAT)||LA65_0==DOUBLE||LA65_0==256||LA65_0==259||(LA65_0>=262 && LA65_0<=268)) ) {
                            alt65=1;
                        }


                        switch (alt65) {
                    	case 1 :
                    	    // IbmSparqlExtAstWalker.g:597:16: e1= expression
                    	    {
                    	    pushFollow(FOLLOW_expression_in_argList4058);
                    	    e1=expression();

                    	    state._fsp--;
                    	    if (state.failed) return args;
                    	    if ( state.backtracking==0 ) {
                    	       args.add(e1); 
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt65 >= 1 ) break loop65;
                    	    if (state.backtracking>0) {state.failed=true; return args;}
                                EarlyExitException eee =
                                    new EarlyExitException(65, input);
                                throw eee;
                        }
                        cnt65++;
                    } while (true);


                    }
                    break;
                case 3 :
                    // IbmSparqlExtAstWalker.g:598:4: (e2= expression )+
                    {
                    // IbmSparqlExtAstWalker.g:598:4: (e2= expression )+
                    int cnt66=0;
                    loop66:
                    do {
                        int alt66=2;
                        int LA66_0 = input.LA(1);

                        if ( ((LA66_0>=BROKEN_PLUS && LA66_0<=BROKEN_MINUS)||(LA66_0>=VAR && LA66_0<=NOT_EXISTS)||(LA66_0>=STRING && LA66_0<=BOOLEAN)||LA66_0==LTE||(LA66_0>=BIG_INTEGER && LA66_0<=BIG_DECIMAL)||(LA66_0>=LOGICAL_OR && LA66_0<=SHA1)||(LA66_0>=SHA256 && LA66_0<=GROUP_CONCAT)||LA66_0==DOUBLE||LA66_0==256||LA66_0==259||(LA66_0>=262 && LA66_0<=268)) ) {
                            alt66=1;
                        }


                        switch (alt66) {
                    	case 1 :
                    	    // IbmSparqlExtAstWalker.g:598:5: e2= expression
                    	    {
                    	    pushFollow(FOLLOW_expression_in_argList4071);
                    	    e2=expression();

                    	    state._fsp--;
                    	    if (state.failed) return args;
                    	    if ( state.backtracking==0 ) {
                    	       args.add(e2); 
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt66 >= 1 ) break loop66;
                    	    if (state.backtracking>0) {state.failed=true; return args;}
                                EarlyExitException eee =
                                    new EarlyExitException(66, input);
                                throw eee;
                        }
                        cnt66++;
                    } while (true);


                    }
                    break;

            }
        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return args;
    }
    // $ANTLR end "argList"


    // $ANTLR start "expressionList"
    // IbmSparqlExtAstWalker.g:601:1: expressionList returns [List<Expression> exprs] : ( NIL | (e= expression )+ );
    public final List<Expression> expressionList() throws RecognitionException {
        List<Expression> exprs = null;

        Expression e = null;


         exprs = new ArrayList<Expression>(); 
        try {
            // IbmSparqlExtAstWalker.g:603:2: ( NIL | (e= expression )+ )
            int alt69=2;
            int LA69_0 = input.LA(1);

            if ( (LA69_0==NIL) ) {
                alt69=1;
            }
            else if ( ((LA69_0>=BROKEN_PLUS && LA69_0<=BROKEN_MINUS)||(LA69_0>=VAR && LA69_0<=NOT_EXISTS)||(LA69_0>=STRING && LA69_0<=BOOLEAN)||LA69_0==LTE||(LA69_0>=BIG_INTEGER && LA69_0<=BIG_DECIMAL)||(LA69_0>=LOGICAL_OR && LA69_0<=SHA1)||(LA69_0>=SHA256 && LA69_0<=GROUP_CONCAT)||LA69_0==DOUBLE||LA69_0==256||LA69_0==259||(LA69_0>=262 && LA69_0<=268)) ) {
                alt69=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return exprs;}
                NoViableAltException nvae =
                    new NoViableAltException("", 69, 0, input);

                throw nvae;
            }
            switch (alt69) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:603:6: NIL
                    {
                    match(input,NIL,FOLLOW_NIL_in_expressionList4106); if (state.failed) return exprs;

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:604:6: (e= expression )+
                    {
                    // IbmSparqlExtAstWalker.g:604:6: (e= expression )+
                    int cnt68=0;
                    loop68:
                    do {
                        int alt68=2;
                        int LA68_0 = input.LA(1);

                        if ( ((LA68_0>=BROKEN_PLUS && LA68_0<=BROKEN_MINUS)||(LA68_0>=VAR && LA68_0<=NOT_EXISTS)||(LA68_0>=STRING && LA68_0<=BOOLEAN)||LA68_0==LTE||(LA68_0>=BIG_INTEGER && LA68_0<=BIG_DECIMAL)||(LA68_0>=LOGICAL_OR && LA68_0<=SHA1)||(LA68_0>=SHA256 && LA68_0<=GROUP_CONCAT)||LA68_0==DOUBLE||LA68_0==256||LA68_0==259||(LA68_0>=262 && LA68_0<=268)) ) {
                            alt68=1;
                        }


                        switch (alt68) {
                    	case 1 :
                    	    // IbmSparqlExtAstWalker.g:604:8: e= expression
                    	    {
                    	    pushFollow(FOLLOW_expression_in_expressionList4129);
                    	    e=expression();

                    	    state._fsp--;
                    	    if (state.failed) return exprs;
                    	    if ( state.backtracking==0 ) {
                    	       exprs.add(e); 
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt68 >= 1 ) break loop68;
                    	    if (state.backtracking>0) {state.failed=true; return exprs;}
                                EarlyExitException eee =
                                    new EarlyExitException(68, input);
                                throw eee;
                        }
                        cnt68++;
                    } while (true);


                    }
                    break;

            }
        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return exprs;
    }
    // $ANTLR end "expressionList"


    // $ANTLR start "constructTemplate"
    // IbmSparqlExtAstWalker.g:608:1: constructTemplate[ConstructQuery cq] : ( constructTriples[$cq] )? ;
    public final void constructTemplate(ConstructQuery cq) throws RecognitionException {
        try {
            // IbmSparqlExtAstWalker.g:609:2: ( ( constructTriples[$cq] )? )
            // IbmSparqlExtAstWalker.g:609:6: ( constructTriples[$cq] )?
            {
            // IbmSparqlExtAstWalker.g:609:6: ( constructTriples[$cq] )?
            int alt70=2;
            int LA70_0 = input.LA(1);

            if ( ((LA70_0>=TRIPLE && LA70_0<=TRIPLE2)) ) {
                alt70=1;
            }
            switch (alt70) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:609:6: constructTriples[$cq]
                    {
                    pushFollow(FOLLOW_constructTriples_in_constructTemplate4149);
                    constructTriples(cq);

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }


            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return ;
    }
    // $ANTLR end "constructTemplate"


    // $ANTLR start "constructTriples"
    // IbmSparqlExtAstWalker.g:612:1: constructTriples[ConstructQuery cq] : (t= triples | t2= triples2 )+ ;
    public final void constructTriples(ConstructQuery cq) throws RecognitionException {
        QueryTriple t = null;

        QueryTriple2 t2 = null;


        try {
            // IbmSparqlExtAstWalker.g:613:2: ( (t= triples | t2= triples2 )+ )
            // IbmSparqlExtAstWalker.g:613:6: (t= triples | t2= triples2 )+
            {
            // IbmSparqlExtAstWalker.g:613:6: (t= triples | t2= triples2 )+
            int cnt71=0;
            loop71:
            do {
                int alt71=3;
                int LA71_0 = input.LA(1);

                if ( (LA71_0==TRIPLE) ) {
                    alt71=1;
                }
                else if ( (LA71_0==TRIPLE2) ) {
                    alt71=2;
                }


                switch (alt71) {
            	case 1 :
            	    // IbmSparqlExtAstWalker.g:613:8: t= triples
            	    {
            	    pushFollow(FOLLOW_triples_in_constructTriples4170);
            	    t=triples();

            	    state._fsp--;
            	    if (state.failed) return ;
            	    if ( state.backtracking==0 ) {
            	      cq.addConstructPattern(t);
            	    }

            	    }
            	    break;
            	case 2 :
            	    // IbmSparqlExtAstWalker.g:614:5: t2= triples2
            	    {
            	    pushFollow(FOLLOW_triples2_in_constructTriples4181);
            	    t2=triples2();

            	    state._fsp--;
            	    if (state.failed) return ;
            	    if ( state.backtracking==0 ) {
            	      cq.addConstructPattern(t2);
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt71 >= 1 ) break loop71;
            	    if (state.backtracking>0) {state.failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(71, input);
                        throw eee;
                }
                cnt71++;
            } while (true);


            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return ;
    }
    // $ANTLR end "constructTriples"


    // $ANTLR start "triplesSameSubject"
    // IbmSparqlExtAstWalker.g:618:1: triplesSameSubject[ConstructQuery cq] returns [SimplePattern sp] : ( (t1= triples )+ | t12= triples2 );
    public final SimplePattern triplesSameSubject(ConstructQuery cq) throws RecognitionException {
        SimplePattern sp = null;

        QueryTriple t1 = null;

        QueryTriple2 t12 = null;


         sp = new SimplePattern(); 
        try {
            // IbmSparqlExtAstWalker.g:620:2: ( (t1= triples )+ | t12= triples2 )
            int alt73=2;
            int LA73_0 = input.LA(1);

            if ( (LA73_0==TRIPLE) ) {
                alt73=1;
            }
            else if ( (LA73_0==TRIPLE2) ) {
                alt73=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return sp;}
                NoViableAltException nvae =
                    new NoViableAltException("", 73, 0, input);

                throw nvae;
            }
            switch (alt73) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:620:4: (t1= triples )+
                    {
                    // IbmSparqlExtAstWalker.g:620:4: (t1= triples )+
                    int cnt72=0;
                    loop72:
                    do {
                        int alt72=2;
                        int LA72_0 = input.LA(1);

                        if ( (LA72_0==TRIPLE) ) {
                            alt72=1;
                        }


                        switch (alt72) {
                    	case 1 :
                    	    // IbmSparqlExtAstWalker.g:620:5: t1= triples
                    	    {
                    	    pushFollow(FOLLOW_triples_in_triplesSameSubject4218);
                    	    t1=triples();

                    	    state._fsp--;
                    	    if (state.failed) return sp;
                    	    if ( state.backtracking==0 ) {
                    	       cq.addConstructPattern(t1); t1.expandAndAddTo(sp); 
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt72 >= 1 ) break loop72;
                    	    if (state.backtracking>0) {state.failed=true; return sp;}
                                EarlyExitException eee =
                                    new EarlyExitException(72, input);
                                throw eee;
                        }
                        cnt72++;
                    } while (true);


                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:621:4: t12= triples2
                    {
                    pushFollow(FOLLOW_triples2_in_triplesSameSubject4230);
                    t12=triples2();

                    state._fsp--;
                    if (state.failed) return sp;
                    if ( state.backtracking==0 ) {
                       cq.addConstructPattern(t12); t12.expandAndAddTo(sp); 
                    }

                    }
                    break;

            }
        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return sp;
    }
    // $ANTLR end "triplesSameSubject"


    // $ANTLR start "object"
    // IbmSparqlExtAstWalker.g:624:1: object returns [GraphNode r] : g= graphNode ;
    public final GraphNode object() throws RecognitionException {
        GraphNode r = null;

        GraphNode g = null;


        try {
            // IbmSparqlExtAstWalker.g:625:2: (g= graphNode )
            // IbmSparqlExtAstWalker.g:625:6: g= graphNode
            {
            pushFollow(FOLLOW_graphNode_in_object4252);
            g=graphNode();

            state._fsp--;
            if (state.failed) return r;
            if ( state.backtracking==0 ) {
               r = g; 
            }

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return r;
    }
    // $ANTLR end "object"


    // $ANTLR start "verb"
    // IbmSparqlExtAstWalker.g:628:1: verb returns [QueryTripleTerm t] : (v= varOrIRIref | 'a' );
    public final QueryTripleTerm verb() throws RecognitionException {
        QueryTripleTerm t = null;

        QueryTripleTerm v = null;


        try {
            // IbmSparqlExtAstWalker.g:629:2: (v= varOrIRIref | 'a' )
            int alt74=2;
            int LA74_0 = input.LA(1);

            if ( ((LA74_0>=VAR && LA74_0<=PREFIXED_NS)||LA74_0==IRI) ) {
                alt74=1;
            }
            else if ( (LA74_0==257) ) {
                alt74=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return t;}
                NoViableAltException nvae =
                    new NoViableAltException("", 74, 0, input);

                throw nvae;
            }
            switch (alt74) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:629:6: v= varOrIRIref
                    {
                    pushFollow(FOLLOW_varOrIRIref_in_verb4274);
                    v=varOrIRIref();

                    state._fsp--;
                    if (state.failed) return t;
                    if ( state.backtracking==0 ) {
                       t = v; 
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:630:5: 'a'
                    {
                    match(input,257,FOLLOW_257_in_verb4284); if (state.failed) return t;
                    if ( state.backtracking==0 ) {
                       t = new QueryTripleTerm(new IRI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")); 
                    }

                    }
                    break;

            }
        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return t;
    }
    // $ANTLR end "verb"


    // $ANTLR start "verbPath"
    // IbmSparqlExtAstWalker.g:633:1: verbPath : path ;
    public final void verbPath() throws RecognitionException {
        try {
            // IbmSparqlExtAstWalker.g:634:2: ( path )
            // IbmSparqlExtAstWalker.g:634:6: path
            {
            pushFollow(FOLLOW_path_in_verbPath4302);
            path();

            state._fsp--;
            if (state.failed) return ;

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return ;
    }
    // $ANTLR end "verbPath"


    // $ANTLR start "verbSimple"
    // IbmSparqlExtAstWalker.g:637:1: verbSimple returns [Variable v] : x= var ;
    public final Variable verbSimple() throws RecognitionException {
        Variable v = null;

        String x = null;


        try {
            // IbmSparqlExtAstWalker.g:638:2: (x= var )
            // IbmSparqlExtAstWalker.g:638:6: x= var
            {
            pushFollow(FOLLOW_var_in_verbSimple4321);
            x=var();

            state._fsp--;
            if (state.failed) return v;
            if ( state.backtracking==0 ) {
               v = new Variable(x);
            }

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return v;
    }
    // $ANTLR end "verbSimple"


    // $ANTLR start "path"
    // IbmSparqlExtAstWalker.g:641:1: path : pathAlternative ;
    public final void path() throws RecognitionException {
        try {
            // IbmSparqlExtAstWalker.g:642:2: ( pathAlternative )
            // IbmSparqlExtAstWalker.g:642:6: pathAlternative
            {
            pushFollow(FOLLOW_pathAlternative_in_path4338);
            pathAlternative();

            state._fsp--;
            if (state.failed) return ;

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return ;
    }
    // $ANTLR end "path"


    // $ANTLR start "pathAlternative"
    // IbmSparqlExtAstWalker.g:645:1: pathAlternative : pathSequence ( '|' pathSequence )* ;
    public final void pathAlternative() throws RecognitionException {
        try {
            // IbmSparqlExtAstWalker.g:646:2: ( pathSequence ( '|' pathSequence )* )
            // IbmSparqlExtAstWalker.g:646:6: pathSequence ( '|' pathSequence )*
            {
            pushFollow(FOLLOW_pathSequence_in_pathAlternative4351);
            pathSequence();

            state._fsp--;
            if (state.failed) return ;
            // IbmSparqlExtAstWalker.g:646:19: ( '|' pathSequence )*
            loop75:
            do {
                int alt75=2;
                int LA75_0 = input.LA(1);

                if ( (LA75_0==258) ) {
                    alt75=1;
                }


                switch (alt75) {
            	case 1 :
            	    // IbmSparqlExtAstWalker.g:646:21: '|' pathSequence
            	    {
            	    match(input,258,FOLLOW_258_in_pathAlternative4355); if (state.failed) return ;
            	    pushFollow(FOLLOW_pathSequence_in_pathAlternative4357);
            	    pathSequence();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop75;
                }
            } while (true);


            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return ;
    }
    // $ANTLR end "pathAlternative"


    // $ANTLR start "pathSequence"
    // IbmSparqlExtAstWalker.g:649:1: pathSequence : pathEltOrInverse ( '/' pathEltOrInverse )* ;
    public final void pathSequence() throws RecognitionException {
        try {
            // IbmSparqlExtAstWalker.g:650:2: ( pathEltOrInverse ( '/' pathEltOrInverse )* )
            // IbmSparqlExtAstWalker.g:650:7: pathEltOrInverse ( '/' pathEltOrInverse )*
            {
            pushFollow(FOLLOW_pathEltOrInverse_in_pathSequence4374);
            pathEltOrInverse();

            state._fsp--;
            if (state.failed) return ;
            // IbmSparqlExtAstWalker.g:650:24: ( '/' pathEltOrInverse )*
            loop76:
            do {
                int alt76=2;
                int LA76_0 = input.LA(1);

                if ( (LA76_0==259) ) {
                    alt76=1;
                }


                switch (alt76) {
            	case 1 :
            	    // IbmSparqlExtAstWalker.g:650:26: '/' pathEltOrInverse
            	    {
            	    match(input,259,FOLLOW_259_in_pathSequence4378); if (state.failed) return ;
            	    pushFollow(FOLLOW_pathEltOrInverse_in_pathSequence4380);
            	    pathEltOrInverse();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop76;
                }
            } while (true);


            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return ;
    }
    // $ANTLR end "pathSequence"


    // $ANTLR start "pathElt"
    // IbmSparqlExtAstWalker.g:653:1: pathElt : pathPrimary ( pathMod )? ;
    public final void pathElt() throws RecognitionException {
        try {
            // IbmSparqlExtAstWalker.g:654:2: ( pathPrimary ( pathMod )? )
            // IbmSparqlExtAstWalker.g:654:7: pathPrimary ( pathMod )?
            {
            pushFollow(FOLLOW_pathPrimary_in_pathElt4397);
            pathPrimary();

            state._fsp--;
            if (state.failed) return ;
            // IbmSparqlExtAstWalker.g:654:19: ( pathMod )?
            int alt77=2;
            int LA77_0 = input.LA(1);

            if ( (LA77_0==256||(LA77_0>=261 && LA77_0<=262)) ) {
                alt77=1;
            }
            switch (alt77) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:654:19: pathMod
                    {
                    pushFollow(FOLLOW_pathMod_in_pathElt4399);
                    pathMod();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }


            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return ;
    }
    // $ANTLR end "pathElt"


    // $ANTLR start "pathEltOrInverse"
    // IbmSparqlExtAstWalker.g:657:1: pathEltOrInverse : ( pathElt | '^' pathElt );
    public final void pathEltOrInverse() throws RecognitionException {
        try {
            // IbmSparqlExtAstWalker.g:658:2: ( pathElt | '^' pathElt )
            int alt78=2;
            int LA78_0 = input.LA(1);

            if ( ((LA78_0>=PREFIXED_NAME && LA78_0<=PREFIXED_NS)||LA78_0==OPEN_BRACE||LA78_0==IRI||LA78_0==257||LA78_0==263) ) {
                alt78=1;
            }
            else if ( (LA78_0==260) ) {
                alt78=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 78, 0, input);

                throw nvae;
            }
            switch (alt78) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:658:7: pathElt
                    {
                    pushFollow(FOLLOW_pathElt_in_pathEltOrInverse4414);
                    pathElt();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:658:17: '^' pathElt
                    {
                    match(input,260,FOLLOW_260_in_pathEltOrInverse4418); if (state.failed) return ;
                    pushFollow(FOLLOW_pathElt_in_pathEltOrInverse4420);
                    pathElt();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return ;
    }
    // $ANTLR end "pathEltOrInverse"


    // $ANTLR start "pathMod"
    // IbmSparqlExtAstWalker.g:661:1: pathMod returns [Path.PathMod v] : ( '*' | '?' | '+' );
    public final Path.PathMod pathMod() throws RecognitionException {
        Path.PathMod v = null;

        try {
            // IbmSparqlExtAstWalker.g:662:2: ( '*' | '?' | '+' )
            int alt79=3;
            switch ( input.LA(1) ) {
            case 256:
                {
                alt79=1;
                }
                break;
            case 261:
                {
                alt79=2;
                }
                break;
            case 262:
                {
                alt79=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 79, 0, input);

                throw nvae;
            }

            switch (alt79) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:662:4: '*'
                    {
                    match(input,256,FOLLOW_256_in_pathMod4435); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = Path.PathMod.ZERO_OR_MORE; 
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:663:4: '?'
                    {
                    match(input,261,FOLLOW_261_in_pathMod4444); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = Path.PathMod.ZERO_OR_ONE; 
                    }

                    }
                    break;
                case 3 :
                    // IbmSparqlExtAstWalker.g:664:4: '+'
                    {
                    match(input,262,FOLLOW_262_in_pathMod4453); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = Path.PathMod.ONE_OR_MORE; 
                    }

                    }
                    break;

            }
        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return v;
    }
    // $ANTLR end "pathMod"


    // $ANTLR start "pathPrimary"
    // IbmSparqlExtAstWalker.g:667:1: pathPrimary : ( iRIref | 'a' | '!' pathNegatedPropertySet | OPEN_BRACE path CLOSE_BRACE );
    public final void pathPrimary() throws RecognitionException {
        try {
            // IbmSparqlExtAstWalker.g:668:2: ( iRIref | 'a' | '!' pathNegatedPropertySet | OPEN_BRACE path CLOSE_BRACE )
            int alt80=4;
            switch ( input.LA(1) ) {
            case PREFIXED_NAME:
            case PREFIXED_NS:
            case IRI:
                {
                alt80=1;
                }
                break;
            case 257:
                {
                alt80=2;
                }
                break;
            case 263:
                {
                alt80=3;
                }
                break;
            case OPEN_BRACE:
                {
                alt80=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 80, 0, input);

                throw nvae;
            }

            switch (alt80) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:668:7: iRIref
                    {
                    pushFollow(FOLLOW_iRIref_in_pathPrimary4471);
                    iRIref();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:668:16: 'a'
                    {
                    match(input,257,FOLLOW_257_in_pathPrimary4475); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // IbmSparqlExtAstWalker.g:668:22: '!' pathNegatedPropertySet
                    {
                    match(input,263,FOLLOW_263_in_pathPrimary4479); if (state.failed) return ;
                    pushFollow(FOLLOW_pathNegatedPropertySet_in_pathPrimary4481);
                    pathNegatedPropertySet();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // IbmSparqlExtAstWalker.g:668:51: OPEN_BRACE path CLOSE_BRACE
                    {
                    match(input,OPEN_BRACE,FOLLOW_OPEN_BRACE_in_pathPrimary4485); if (state.failed) return ;
                    pushFollow(FOLLOW_path_in_pathPrimary4487);
                    path();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,CLOSE_BRACE,FOLLOW_CLOSE_BRACE_in_pathPrimary4489); if (state.failed) return ;

                    }
                    break;

            }
        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return ;
    }
    // $ANTLR end "pathPrimary"


    // $ANTLR start "pathNegatedPropertySet"
    // IbmSparqlExtAstWalker.g:671:1: pathNegatedPropertySet returns [Pair<? extends List<IRI>, ? extends List<IRI>> pair] : (v= pathOneInPropertySet | OPEN_BRACE (v1= pathOneInPropertySet ( '|' v2= pathOneInPropertySet )* )? CLOSE_BRACE );
    public final Pair<? extends List<IRI>, ? extends List<IRI>> pathNegatedPropertySet() throws RecognitionException {
        Pair<? extends List<IRI>, ? extends List<IRI>> pair = null;

        Pair<IRI, Boolean> v = null;

        Pair<IRI, Boolean> v1 = null;

        Pair<IRI, Boolean> v2 = null;


         pair = Pair.make(new LinkedList<IRI>(), new LinkedList<IRI>()); 
        try {
            // IbmSparqlExtAstWalker.g:673:2: (v= pathOneInPropertySet | OPEN_BRACE (v1= pathOneInPropertySet ( '|' v2= pathOneInPropertySet )* )? CLOSE_BRACE )
            int alt83=2;
            int LA83_0 = input.LA(1);

            if ( (LA83_0==INV||(LA83_0>=PREFIXED_NAME && LA83_0<=PREFIXED_NS)||LA83_0==IRI||LA83_0==257) ) {
                alt83=1;
            }
            else if ( (LA83_0==OPEN_BRACE) ) {
                alt83=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return pair;}
                NoViableAltException nvae =
                    new NoViableAltException("", 83, 0, input);

                throw nvae;
            }
            switch (alt83) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:673:7: v= pathOneInPropertySet
                    {
                    pushFollow(FOLLOW_pathOneInPropertySet_in_pathNegatedPropertySet4519);
                    v=pathOneInPropertySet();

                    state._fsp--;
                    if (state.failed) return pair;
                    if ( state.backtracking==0 ) {
                       if (v.snd ) { pair.fst.add(v.fst); } else {pair.snd.add(v.fst);} 
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:674:4: OPEN_BRACE (v1= pathOneInPropertySet ( '|' v2= pathOneInPropertySet )* )? CLOSE_BRACE
                    {
                    match(input,OPEN_BRACE,FOLLOW_OPEN_BRACE_in_pathNegatedPropertySet4527); if (state.failed) return pair;
                    // IbmSparqlExtAstWalker.g:674:15: (v1= pathOneInPropertySet ( '|' v2= pathOneInPropertySet )* )?
                    int alt82=2;
                    int LA82_0 = input.LA(1);

                    if ( (LA82_0==INV||(LA82_0>=PREFIXED_NAME && LA82_0<=PREFIXED_NS)||LA82_0==IRI||LA82_0==257) ) {
                        alt82=1;
                    }
                    switch (alt82) {
                        case 1 :
                            // IbmSparqlExtAstWalker.g:674:17: v1= pathOneInPropertySet ( '|' v2= pathOneInPropertySet )*
                            {
                            pushFollow(FOLLOW_pathOneInPropertySet_in_pathNegatedPropertySet4535);
                            v1=pathOneInPropertySet();

                            state._fsp--;
                            if (state.failed) return pair;
                            // IbmSparqlExtAstWalker.g:674:43: ( '|' v2= pathOneInPropertySet )*
                            loop81:
                            do {
                                int alt81=2;
                                int LA81_0 = input.LA(1);

                                if ( (LA81_0==258) ) {
                                    alt81=1;
                                }


                                switch (alt81) {
                            	case 1 :
                            	    // IbmSparqlExtAstWalker.g:674:45: '|' v2= pathOneInPropertySet
                            	    {
                            	    match(input,258,FOLLOW_258_in_pathNegatedPropertySet4539); if (state.failed) return pair;
                            	    pushFollow(FOLLOW_pathOneInPropertySet_in_pathNegatedPropertySet4545);
                            	    v2=pathOneInPropertySet();

                            	    state._fsp--;
                            	    if (state.failed) return pair;
                            	    if ( state.backtracking==0 ) {
                            	       if (v2.snd ) { pair.fst.add(v2.fst); } else {pair.snd.add(v2.fst);} 
                            	    }

                            	    }
                            	    break;

                            	default :
                            	    break loop81;
                                }
                            } while (true);


                            }
                            break;

                    }

                    match(input,CLOSE_BRACE,FOLLOW_CLOSE_BRACE_in_pathNegatedPropertySet4556); if (state.failed) return pair;
                    if ( state.backtracking==0 ) {
                      if (v1.snd ) { pair.fst.add(0,v1.fst); } else {pair.snd.add(0, v1.fst);} 
                    }

                    }
                    break;

            }
        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return pair;
    }
    // $ANTLR end "pathNegatedPropertySet"


    // $ANTLR start "pathOneInPropertySet"
    // IbmSparqlExtAstWalker.g:678:1: pathOneInPropertySet returns [Pair<IRI, Boolean> v ] : (i= iRIref | 'a' | ^( INV invi= iRIrefOrRDFType ) );
    public final Pair<IRI, Boolean> pathOneInPropertySet() throws RecognitionException {
        Pair<IRI, Boolean> v = null;

        IRI i = null;

        IRI invi = null;


        try {
            // IbmSparqlExtAstWalker.g:679:4: (i= iRIref | 'a' | ^( INV invi= iRIrefOrRDFType ) )
            int alt84=3;
            switch ( input.LA(1) ) {
            case PREFIXED_NAME:
            case PREFIXED_NS:
            case IRI:
                {
                alt84=1;
                }
                break;
            case 257:
                {
                alt84=2;
                }
                break;
            case INV:
                {
                alt84=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 84, 0, input);

                throw nvae;
            }

            switch (alt84) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:679:7: i= iRIref
                    {
                    pushFollow(FOLLOW_iRIref_in_pathOneInPropertySet4584);
                    i=iRIref();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = Pair.make(i, true); 
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:680:4: 'a'
                    {
                    match(input,257,FOLLOW_257_in_pathOneInPropertySet4591); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = Pair.make(new IRI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"), true); 
                    }

                    }
                    break;
                case 3 :
                    // IbmSparqlExtAstWalker.g:681:4: ^( INV invi= iRIrefOrRDFType )
                    {
                    match(input,INV,FOLLOW_INV_in_pathOneInPropertySet4600); if (state.failed) return v;

                    match(input, Token.DOWN, null); if (state.failed) return v;
                    pushFollow(FOLLOW_iRIrefOrRDFType_in_pathOneInPropertySet4606);
                    invi=iRIrefOrRDFType();

                    state._fsp--;
                    if (state.failed) return v;

                    match(input, Token.UP, null); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = Pair.make(invi, false); 
                    }

                    }
                    break;

            }
        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return v;
    }
    // $ANTLR end "pathOneInPropertySet"


    // $ANTLR start "iRIrefOrRDFType"
    // IbmSparqlExtAstWalker.g:684:1: iRIrefOrRDFType returns [ IRI v ] : (i= iRIref | 'a' );
    public final IRI iRIrefOrRDFType() throws RecognitionException {
        IRI v = null;

        IRI i = null;


        try {
            // IbmSparqlExtAstWalker.g:685:4: (i= iRIref | 'a' )
            int alt85=2;
            int LA85_0 = input.LA(1);

            if ( ((LA85_0>=PREFIXED_NAME && LA85_0<=PREFIXED_NS)||LA85_0==IRI) ) {
                alt85=1;
            }
            else if ( (LA85_0==257) ) {
                alt85=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 85, 0, input);

                throw nvae;
            }
            switch (alt85) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:685:6: i= iRIref
                    {
                    pushFollow(FOLLOW_iRIref_in_iRIrefOrRDFType4633);
                    i=iRIref();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = i;
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:686:6: 'a'
                    {
                    match(input,257,FOLLOW_257_in_iRIrefOrRDFType4642); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = new IRI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"); 
                    }

                    }
                    break;

            }
        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return v;
    }
    // $ANTLR end "iRIrefOrRDFType"


    // $ANTLR start "integer"
    // IbmSparqlExtAstWalker.g:689:1: integer : INTEGER ;
    public final void integer() throws RecognitionException {
        try {
            // IbmSparqlExtAstWalker.g:690:2: ( INTEGER )
            // IbmSparqlExtAstWalker.g:690:7: INTEGER
            {
            match(input,INTEGER,FOLLOW_INTEGER_in_integer4663); if (state.failed) return ;

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return ;
    }
    // $ANTLR end "integer"


    // $ANTLR start "triplesNode"
    // IbmSparqlExtAstWalker.g:693:1: triplesNode returns [TriplesNode tn] : ^( TRIPLES_NODE (c= collection | b= blankNodePropertyList ) ) ;
    public final TriplesNode triplesNode() throws RecognitionException {
        TriplesNode tn = null;

        RDFCollection c = null;

        PropertyList b = null;


        try {
            // IbmSparqlExtAstWalker.g:694:2: ( ^( TRIPLES_NODE (c= collection | b= blankNodePropertyList ) ) )
            // IbmSparqlExtAstWalker.g:694:6: ^( TRIPLES_NODE (c= collection | b= blankNodePropertyList ) )
            {
            match(input,TRIPLES_NODE,FOLLOW_TRIPLES_NODE_in_triplesNode4682); if (state.failed) return tn;

            match(input, Token.DOWN, null); if (state.failed) return tn;
            // IbmSparqlExtAstWalker.g:695:5: (c= collection | b= blankNodePropertyList )
            int alt86=2;
            int LA86_0 = input.LA(1);

            if ( (LA86_0==COLLECTION) ) {
                alt86=1;
            }
            else if ( (LA86_0==PROPERTY_LIST) ) {
                alt86=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return tn;}
                NoViableAltException nvae =
                    new NoViableAltException("", 86, 0, input);

                throw nvae;
            }
            switch (alt86) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:695:7: c= collection
                    {
                    pushFollow(FOLLOW_collection_in_triplesNode4693);
                    c=collection();

                    state._fsp--;
                    if (state.failed) return tn;
                    if ( state.backtracking==0 ) {
                       tn = new TriplesNode(c);
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:696:8: b= blankNodePropertyList
                    {
                    pushFollow(FOLLOW_blankNodePropertyList_in_triplesNode4710);
                    b=blankNodePropertyList();

                    state._fsp--;
                    if (state.failed) return tn;
                    if ( state.backtracking==0 ) {
                       tn = new TriplesNode(b);
                    }

                    }
                    break;

            }


            match(input, Token.UP, null); if (state.failed) return tn;

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return tn;
    }
    // $ANTLR end "triplesNode"


    // $ANTLR start "blankNodePropertyList"
    // IbmSparqlExtAstWalker.g:701:1: blankNodePropertyList returns [PropertyList pl] : ^( PROPERTY_LIST ( ^( PREDICATE v= predicate ) ^( VALUE (o= graphNode )+ ) )+ ) ;
    public final PropertyList blankNodePropertyList() throws RecognitionException {
        PropertyList pl = null;

        BinaryUnion<Variable, Path> v = null;

        GraphNode o = null;



        			pl = new PropertyList(); 
        			PropertyListElement e = null;
        		
        try {
            // IbmSparqlExtAstWalker.g:706:2: ( ^( PROPERTY_LIST ( ^( PREDICATE v= predicate ) ^( VALUE (o= graphNode )+ ) )+ ) )
            // IbmSparqlExtAstWalker.g:706:4: ^( PROPERTY_LIST ( ^( PREDICATE v= predicate ) ^( VALUE (o= graphNode )+ ) )+ )
            {
            match(input,PROPERTY_LIST,FOLLOW_PROPERTY_LIST_in_blankNodePropertyList4749); if (state.failed) return pl;

            match(input, Token.DOWN, null); if (state.failed) return pl;
            // IbmSparqlExtAstWalker.g:707:6: ( ^( PREDICATE v= predicate ) ^( VALUE (o= graphNode )+ ) )+
            int cnt88=0;
            loop88:
            do {
                int alt88=2;
                int LA88_0 = input.LA(1);

                if ( (LA88_0==PREDICATE) ) {
                    alt88=1;
                }


                switch (alt88) {
            	case 1 :
            	    // IbmSparqlExtAstWalker.g:707:8: ^( PREDICATE v= predicate ) ^( VALUE (o= graphNode )+ )
            	    {
            	    if ( state.backtracking==0 ) {
            	       e = new PropertyListElement(); 
            	    }
            	    match(input,PREDICATE,FOLLOW_PREDICATE_in_blankNodePropertyList4770); if (state.failed) return pl;

            	    match(input, Token.DOWN, null); if (state.failed) return pl;
            	    pushFollow(FOLLOW_predicate_in_blankNodePropertyList4774);
            	    v=predicate();

            	    state._fsp--;
            	    if (state.failed) return pl;

            	    match(input, Token.UP, null); if (state.failed) return pl;
            	    if ( state.backtracking==0 ) {
            	      e.setVerb(v);
            	    }
            	    match(input,VALUE,FOLLOW_VALUE_in_blankNodePropertyList4789); if (state.failed) return pl;

            	    match(input, Token.DOWN, null); if (state.failed) return pl;
            	    // IbmSparqlExtAstWalker.g:709:17: (o= graphNode )+
            	    int cnt87=0;
            	    loop87:
            	    do {
            	        int alt87=2;
            	        int LA87_0 = input.LA(1);

            	        if ( ((LA87_0>=NIL && LA87_0<=ANNON)||(LA87_0>=VAR && LA87_0<=PREFIXED_NS)||(LA87_0>=STRING && LA87_0<=BOOLEAN)||LA87_0==TRIPLES_NODE||(LA87_0>=BIG_INTEGER && LA87_0<=BIG_DECIMAL)||LA87_0==IRI||LA87_0==DOUBLE||LA87_0==BLANK_NODE_LABEL) ) {
            	            alt87=1;
            	        }


            	        switch (alt87) {
            	    	case 1 :
            	    	    // IbmSparqlExtAstWalker.g:709:18: o= graphNode
            	    	    {
            	    	    pushFollow(FOLLOW_graphNode_in_blankNodePropertyList4794);
            	    	    o=graphNode();

            	    	    state._fsp--;
            	    	    if (state.failed) return pl;
            	    	    if ( state.backtracking==0 ) {
            	    	      e.addObject(o);
            	    	    }

            	    	    }
            	    	    break;

            	    	default :
            	    	    if ( cnt87 >= 1 ) break loop87;
            	    	    if (state.backtracking>0) {state.failed=true; return pl;}
            	                EarlyExitException eee =
            	                    new EarlyExitException(87, input);
            	                throw eee;
            	        }
            	        cnt87++;
            	    } while (true);


            	    match(input, Token.UP, null); if (state.failed) return pl;
            	    if ( state.backtracking==0 ) {
            	       pl.add(e); 
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt88 >= 1 ) break loop88;
            	    if (state.backtracking>0) {state.failed=true; return pl;}
                        EarlyExitException eee =
                            new EarlyExitException(88, input);
                        throw eee;
                }
                cnt88++;
            } while (true);


            match(input, Token.UP, null); if (state.failed) return pl;

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return pl;
    }
    // $ANTLR end "blankNodePropertyList"


    // $ANTLR start "predicate"
    // IbmSparqlExtAstWalker.g:715:1: predicate returns [BinaryUnion<Variable, Path> p] : (v= var | i= iRIref | 'a' | ^( ALT (alt= predicate )* ) | ^( SEQ (seq= predicate )* ) | ^( ELT pred= predicate mod= pathMod ) | ^( INV pred= predicate ) | '!' neg= pathNegatedPropertySet );
    public final BinaryUnion<Variable, Path> predicate() throws RecognitionException {
        BinaryUnion<Variable, Path> p = null;

        String v = null;

        IRI i = null;

        BinaryUnion<Variable, Path> alt = null;

        BinaryUnion<Variable, Path> seq = null;

        BinaryUnion<Variable, Path> pred = null;

        Path.PathMod mod = null;

        Pair<? extends List<IRI>, ? extends List<IRI>> neg = null;


         p = new BinaryUnion<Variable, Path>(); 
        try {
            // IbmSparqlExtAstWalker.g:717:2: (v= var | i= iRIref | 'a' | ^( ALT (alt= predicate )* ) | ^( SEQ (seq= predicate )* ) | ^( ELT pred= predicate mod= pathMod ) | ^( INV pred= predicate ) | '!' neg= pathNegatedPropertySet )
            int alt91=8;
            switch ( input.LA(1) ) {
            case VAR:
                {
                alt91=1;
                }
                break;
            case PREFIXED_NAME:
            case PREFIXED_NS:
            case IRI:
                {
                alt91=2;
                }
                break;
            case 257:
                {
                alt91=3;
                }
                break;
            case ALT:
                {
                alt91=4;
                }
                break;
            case SEQ:
                {
                alt91=5;
                }
                break;
            case ELT:
                {
                alt91=6;
                }
                break;
            case INV:
                {
                alt91=7;
                }
                break;
            case 263:
                {
                alt91=8;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return p;}
                NoViableAltException nvae =
                    new NoViableAltException("", 91, 0, input);

                throw nvae;
            }

            switch (alt91) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:717:4: v= var
                    {
                    pushFollow(FOLLOW_var_in_predicate4850);
                    v=var();

                    state._fsp--;
                    if (state.failed) return p;
                    if ( state.backtracking==0 ) {
                       p.setFirst(new Variable(v));  
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:718:4: i= iRIref
                    {
                    pushFollow(FOLLOW_iRIref_in_predicate4865);
                    i=iRIref();

                    state._fsp--;
                    if (state.failed) return p;
                    if ( state.backtracking==0 ) {
                       p.setSecond(new SimplePath(i)); 
                    }

                    }
                    break;
                case 3 :
                    // IbmSparqlExtAstWalker.g:719:5: 'a'
                    {
                    match(input,257,FOLLOW_257_in_predicate4876); if (state.failed) return p;
                    if ( state.backtracking==0 ) {
                       p.setSecond(new SimplePath(new IRI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"))); 
                    }

                    }
                    break;
                case 4 :
                    // IbmSparqlExtAstWalker.g:720:9: ^( ALT (alt= predicate )* )
                    {
                    match(input,ALT,FOLLOW_ALT_in_predicate4891); if (state.failed) return p;

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); if (state.failed) return p;
                        // IbmSparqlExtAstWalker.g:720:15: (alt= predicate )*
                        loop89:
                        do {
                            int alt89=2;
                            int LA89_0 = input.LA(1);

                            if ( ((LA89_0>=ALT && LA89_0<=INV)||(LA89_0>=VAR && LA89_0<=PREFIXED_NS)||LA89_0==IRI||LA89_0==257||LA89_0==263) ) {
                                alt89=1;
                            }


                            switch (alt89) {
                        	case 1 :
                        	    // IbmSparqlExtAstWalker.g:720:16: alt= predicate
                        	    {
                        	    pushFollow(FOLLOW_predicate_in_predicate4896);
                        	    alt=predicate();

                        	    state._fsp--;
                        	    if (state.failed) return p;
                        	    if ( state.backtracking==0 ) {
                        	       
                        	          		if (!p.isFirstType() && !p.isSecondType()) { 
                        	          			assert alt.isSecondType(): alt; 
                        	          			p = alt;
                        	          		} else { 
                        	          			p.setSecond(new AltPath(p.getSecond(), alt.getSecond())); 
                        	          		} 
                        	          	
                        	    }

                        	    }
                        	    break;

                        	default :
                        	    break loop89;
                            }
                        } while (true);


                        match(input, Token.UP, null); if (state.failed) return p;
                    }

                    }
                    break;
                case 5 :
                    // IbmSparqlExtAstWalker.g:728:9: ^( SEQ (seq= predicate )* )
                    {
                    match(input,SEQ,FOLLOW_SEQ_in_predicate4912); if (state.failed) return p;

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); if (state.failed) return p;
                        // IbmSparqlExtAstWalker.g:728:15: (seq= predicate )*
                        loop90:
                        do {
                            int alt90=2;
                            int LA90_0 = input.LA(1);

                            if ( ((LA90_0>=ALT && LA90_0<=INV)||(LA90_0>=VAR && LA90_0<=PREFIXED_NS)||LA90_0==IRI||LA90_0==257||LA90_0==263) ) {
                                alt90=1;
                            }


                            switch (alt90) {
                        	case 1 :
                        	    // IbmSparqlExtAstWalker.g:728:16: seq= predicate
                        	    {
                        	    pushFollow(FOLLOW_predicate_in_predicate4917);
                        	    seq=predicate();

                        	    state._fsp--;
                        	    if (state.failed) return p;
                        	    if ( state.backtracking==0 ) {
                        	       
                        	          		if (!p.isFirstType() && !p.isSecondType()) { 
                        	          			assert seq.isSecondType(): seq; 
                        	          			p = seq;
                        	          		} else { 
                        	          			p.setSecond(new SeqPath(p.getSecond(), seq.getSecond())); 
                        	          		} 
                        	          	
                        	    }

                        	    }
                        	    break;

                        	default :
                        	    break loop90;
                            }
                        } while (true);


                        match(input, Token.UP, null); if (state.failed) return p;
                    }

                    }
                    break;
                case 6 :
                    // IbmSparqlExtAstWalker.g:736:9: ^( ELT pred= predicate mod= pathMod )
                    {
                    match(input,ELT,FOLLOW_ELT_in_predicate4933); if (state.failed) return p;

                    match(input, Token.DOWN, null); if (state.failed) return p;
                    pushFollow(FOLLOW_predicate_in_predicate4937);
                    pred=predicate();

                    state._fsp--;
                    if (state.failed) return p;
                    pushFollow(FOLLOW_pathMod_in_predicate4943);
                    mod=pathMod();

                    state._fsp--;
                    if (state.failed) return p;

                    match(input, Token.UP, null); if (state.failed) return p;
                    if ( state.backtracking==0 ) {
                       p.setSecond(Path.createPath(pred.getSecond(), mod)) ; 
                    }

                    }
                    break;
                case 7 :
                    // IbmSparqlExtAstWalker.g:737:9: ^( INV pred= predicate )
                    {
                    match(input,INV,FOLLOW_INV_in_predicate4958); if (state.failed) return p;

                    match(input, Token.DOWN, null); if (state.failed) return p;
                    pushFollow(FOLLOW_predicate_in_predicate4962);
                    pred=predicate();

                    state._fsp--;
                    if (state.failed) return p;

                    match(input, Token.UP, null); if (state.failed) return p;
                    if ( state.backtracking==0 ) {
                       p.setSecond(new InvPath(pred.getSecond())) ; 
                    }

                    }
                    break;
                case 8 :
                    // IbmSparqlExtAstWalker.g:738:7: '!' neg= pathNegatedPropertySet
                    {
                    match(input,263,FOLLOW_263_in_predicate4973); if (state.failed) return p;
                    pushFollow(FOLLOW_pathNegatedPropertySet_in_predicate4979);
                    neg=pathNegatedPropertySet();

                    state._fsp--;
                    if (state.failed) return p;
                    if ( state.backtracking==0 ) {
                       p.setSecond(new NegatedProperySetPath(neg.fst, neg.snd)); 
                    }

                    }
                    break;

            }
        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return p;
    }
    // $ANTLR end "predicate"


    // $ANTLR start "collection"
    // IbmSparqlExtAstWalker.g:741:1: collection returns [RDFCollection c] : ^( COLLECTION (g= graphNode )+ ) ;
    public final RDFCollection collection() throws RecognitionException {
        RDFCollection c = null;

        GraphNode g = null;


         c = new RDFCollection(); 
        try {
            // IbmSparqlExtAstWalker.g:743:2: ( ^( COLLECTION (g= graphNode )+ ) )
            // IbmSparqlExtAstWalker.g:743:7: ^( COLLECTION (g= graphNode )+ )
            {
            match(input,COLLECTION,FOLLOW_COLLECTION_in_collection5007); if (state.failed) return c;

            match(input, Token.DOWN, null); if (state.failed) return c;
            // IbmSparqlExtAstWalker.g:743:21: (g= graphNode )+
            int cnt92=0;
            loop92:
            do {
                int alt92=2;
                int LA92_0 = input.LA(1);

                if ( ((LA92_0>=NIL && LA92_0<=ANNON)||(LA92_0>=VAR && LA92_0<=PREFIXED_NS)||(LA92_0>=STRING && LA92_0<=BOOLEAN)||LA92_0==TRIPLES_NODE||(LA92_0>=BIG_INTEGER && LA92_0<=BIG_DECIMAL)||LA92_0==IRI||LA92_0==DOUBLE||LA92_0==BLANK_NODE_LABEL) ) {
                    alt92=1;
                }


                switch (alt92) {
            	case 1 :
            	    // IbmSparqlExtAstWalker.g:743:22: g= graphNode
            	    {
            	    pushFollow(FOLLOW_graphNode_in_collection5012);
            	    g=graphNode();

            	    state._fsp--;
            	    if (state.failed) return c;
            	    if ( state.backtracking==0 ) {
            	       c.add(g); 
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt92 >= 1 ) break loop92;
            	    if (state.backtracking>0) {state.failed=true; return c;}
                        EarlyExitException eee =
                            new EarlyExitException(92, input);
                        throw eee;
                }
                cnt92++;
            } while (true);


            match(input, Token.UP, null); if (state.failed) return c;

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return c;
    }
    // $ANTLR end "collection"


    // $ANTLR start "graphNode"
    // IbmSparqlExtAstWalker.g:746:1: graphNode returns [GraphNode gn] : (v= var | g= graphTerm | t= triplesNode );
    public final GraphNode graphNode() throws RecognitionException {
        GraphNode gn = null;

        String v = null;

        GraphTerm g = null;

        TriplesNode t = null;


        try {
            // IbmSparqlExtAstWalker.g:747:2: (v= var | g= graphTerm | t= triplesNode )
            int alt93=3;
            switch ( input.LA(1) ) {
            case VAR:
                {
                alt93=1;
                }
                break;
            case NIL:
            case ANNON:
            case PREFIXED_NAME:
            case PREFIXED_NS:
            case STRING:
            case BOOLEAN:
            case BIG_INTEGER:
            case BIG_DECIMAL:
            case IRI:
            case DOUBLE:
            case BLANK_NODE_LABEL:
                {
                alt93=2;
                }
                break;
            case TRIPLES_NODE:
                {
                alt93=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return gn;}
                NoViableAltException nvae =
                    new NoViableAltException("", 93, 0, input);

                throw nvae;
            }

            switch (alt93) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:747:6: v= var
                    {
                    pushFollow(FOLLOW_var_in_graphNode5038);
                    v=var();

                    state._fsp--;
                    if (state.failed) return gn;
                    if ( state.backtracking==0 ) {
                       gn = new GraphNode(new Variable(v));			
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:748:5: g= graphTerm
                    {
                    pushFollow(FOLLOW_graphTerm_in_graphNode5051);
                    g=graphTerm();

                    state._fsp--;
                    if (state.failed) return gn;
                    if ( state.backtracking==0 ) {
                       gn = new GraphNode(g);						
                    }

                    }
                    break;
                case 3 :
                    // IbmSparqlExtAstWalker.g:749:5: t= triplesNode
                    {
                    pushFollow(FOLLOW_triplesNode_in_graphNode5063);
                    t=triplesNode();

                    state._fsp--;
                    if (state.failed) return gn;
                    if ( state.backtracking==0 ) {
                       gn = new GraphNode(t);						
                    }

                    }
                    break;

            }
        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return gn;
    }
    // $ANTLR end "graphNode"


    // $ANTLR start "varOrTerm"
    // IbmSparqlExtAstWalker.g:752:1: varOrTerm returns [QueryTripleTerm qt] : (v= var | g= graphTerm );
    public final QueryTripleTerm varOrTerm() throws RecognitionException {
        QueryTripleTerm qt = null;

        String v = null;

        GraphTerm g = null;


        try {
            // IbmSparqlExtAstWalker.g:753:2: (v= var | g= graphTerm )
            int alt94=2;
            int LA94_0 = input.LA(1);

            if ( (LA94_0==VAR) ) {
                alt94=1;
            }
            else if ( ((LA94_0>=NIL && LA94_0<=ANNON)||(LA94_0>=PREFIXED_NAME && LA94_0<=PREFIXED_NS)||(LA94_0>=STRING && LA94_0<=BOOLEAN)||(LA94_0>=BIG_INTEGER && LA94_0<=BIG_DECIMAL)||LA94_0==IRI||LA94_0==DOUBLE||LA94_0==BLANK_NODE_LABEL) ) {
                alt94=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return qt;}
                NoViableAltException nvae =
                    new NoViableAltException("", 94, 0, input);

                throw nvae;
            }
            switch (alt94) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:753:7: v= var
                    {
                    pushFollow(FOLLOW_var_in_varOrTerm5086);
                    v=var();

                    state._fsp--;
                    if (state.failed) return qt;
                    if ( state.backtracking==0 ) {
                       qt = new QueryTripleTerm(new Variable(v)); 	
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:754:6: g= graphTerm
                    {
                    pushFollow(FOLLOW_graphTerm_in_varOrTerm5101);
                    g=graphTerm();

                    state._fsp--;
                    if (state.failed) return qt;
                    if ( state.backtracking==0 ) {
                       qt = new QueryTripleTerm(g);					
                    }

                    }
                    break;

            }
        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return qt;
    }
    // $ANTLR end "varOrTerm"


    // $ANTLR start "varOrIRIref"
    // IbmSparqlExtAstWalker.g:757:1: varOrIRIref returns [QueryTripleTerm qt] : (v= var | i= iRIref );
    public final QueryTripleTerm varOrIRIref() throws RecognitionException {
        QueryTripleTerm qt = null;

        String v = null;

        IRI i = null;


        try {
            // IbmSparqlExtAstWalker.g:758:2: (v= var | i= iRIref )
            int alt95=2;
            int LA95_0 = input.LA(1);

            if ( (LA95_0==VAR) ) {
                alt95=1;
            }
            else if ( ((LA95_0>=PREFIXED_NAME && LA95_0<=PREFIXED_NS)||LA95_0==IRI) ) {
                alt95=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return qt;}
                NoViableAltException nvae =
                    new NoViableAltException("", 95, 0, input);

                throw nvae;
            }
            switch (alt95) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:758:6: v= var
                    {
                    pushFollow(FOLLOW_var_in_varOrIRIref5123);
                    v=var();

                    state._fsp--;
                    if (state.failed) return qt;
                    if ( state.backtracking==0 ) {
                       qt = new QueryTripleTerm(new Variable(v)); 	
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:759:5: i= iRIref
                    {
                    pushFollow(FOLLOW_iRIref_in_varOrIRIref5138);
                    i=iRIref();

                    state._fsp--;
                    if (state.failed) return qt;
                    if ( state.backtracking==0 ) {
                       qt = new QueryTripleTerm(i); 	    
                    }

                    }
                    break;

            }
        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return qt;
    }
    // $ANTLR end "varOrIRIref"


    // $ANTLR start "varOrIRIref2"
    // IbmSparqlExtAstWalker.g:762:1: varOrIRIref2 returns [BinaryUnion<Variable, IRI> bu] : (v= var | i= iRIref );
    public final BinaryUnion<Variable, IRI> varOrIRIref2() throws RecognitionException {
        BinaryUnion<Variable, IRI> bu = null;

        String v = null;

        IRI i = null;


         bu = new BinaryUnion<Variable, IRI>(); 
        try {
            // IbmSparqlExtAstWalker.g:764:5: (v= var | i= iRIref )
            int alt96=2;
            int LA96_0 = input.LA(1);

            if ( (LA96_0==VAR) ) {
                alt96=1;
            }
            else if ( ((LA96_0>=PREFIXED_NAME && LA96_0<=PREFIXED_NS)||LA96_0==IRI) ) {
                alt96=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return bu;}
                NoViableAltException nvae =
                    new NoViableAltException("", 96, 0, input);

                throw nvae;
            }
            switch (alt96) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:764:8: v= var
                    {
                    pushFollow(FOLLOW_var_in_varOrIRIref25173);
                    v=var();

                    state._fsp--;
                    if (state.failed) return bu;
                    if ( state.backtracking==0 ) {
                       bu.setFirst(new Variable(v)); 
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:765:8: i= iRIref
                    {
                    pushFollow(FOLLOW_iRIref_in_varOrIRIref25189);
                    i=iRIref();

                    state._fsp--;
                    if (state.failed) return bu;
                    if ( state.backtracking==0 ) {
                       bu.setSecond(i); 
                    }

                    }
                    break;

            }
        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return bu;
    }
    // $ANTLR end "varOrIRIref2"


    // $ANTLR start "var"
    // IbmSparqlExtAstWalker.g:768:1: var returns [String v] : ^( VAR (x1= VAR1 | x2= VAR2 ) ) ;
    public final String var() throws RecognitionException {
        String v = null;

        XTree x1=null;
        XTree x2=null;

        try {
            // IbmSparqlExtAstWalker.g:769:2: ( ^( VAR (x1= VAR1 | x2= VAR2 ) ) )
            // IbmSparqlExtAstWalker.g:769:6: ^( VAR (x1= VAR1 | x2= VAR2 ) )
            {
            match(input,VAR,FOLLOW_VAR_in_var5216); if (state.failed) return v;

            match(input, Token.DOWN, null); if (state.failed) return v;
            // IbmSparqlExtAstWalker.g:769:13: (x1= VAR1 | x2= VAR2 )
            int alt97=2;
            int LA97_0 = input.LA(1);

            if ( (LA97_0==VAR1) ) {
                alt97=1;
            }
            else if ( (LA97_0==VAR2) ) {
                alt97=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 97, 0, input);

                throw nvae;
            }
            switch (alt97) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:769:15: x1= VAR1
                    {
                    x1=(XTree)match(input,VAR1,FOLLOW_VAR1_in_var5223); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = new String(x1.getText().substring(1)); 
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:770:7: x2= VAR2
                    {
                    x2=(XTree)match(input,VAR2,FOLLOW_VAR2_in_var5238); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = new String(x2.getText().substring(1)); 
                    }

                    }
                    break;

            }


            match(input, Token.UP, null); if (state.failed) return v;

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return v;
    }
    // $ANTLR end "var"


    // $ANTLR start "graphTerm"
    // IbmSparqlExtAstWalker.g:775:1: graphTerm returns [GraphTerm gt] : (i= iRIref | r= rDFLiteral | d= numericLiteral | b= booleanLiteral | l= blankNode | NIL );
    public final GraphTerm graphTerm() throws RecognitionException {
        GraphTerm gt = null;

        IRI i = null;

        StringLiteral r = null;

        Constant d = null;

        Boolean b = null;

        BlankNode l = null;


        try {
            // IbmSparqlExtAstWalker.g:776:2: (i= iRIref | r= rDFLiteral | d= numericLiteral | b= booleanLiteral | l= blankNode | NIL )
            int alt98=6;
            switch ( input.LA(1) ) {
            case PREFIXED_NAME:
            case PREFIXED_NS:
            case IRI:
                {
                alt98=1;
                }
                break;
            case STRING:
                {
                alt98=2;
                }
                break;
            case BIG_INTEGER:
            case BIG_DECIMAL:
            case DOUBLE:
                {
                alt98=3;
                }
                break;
            case BOOLEAN:
                {
                alt98=4;
                }
                break;
            case ANNON:
            case BLANK_NODE_LABEL:
                {
                alt98=5;
                }
                break;
            case NIL:
                {
                alt98=6;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return gt;}
                NoViableAltException nvae =
                    new NoViableAltException("", 98, 0, input);

                throw nvae;
            }

            switch (alt98) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:776:6: i= iRIref
                    {
                    pushFollow(FOLLOW_iRIref_in_graphTerm5272);
                    i=iRIref();

                    state._fsp--;
                    if (state.failed) return gt;
                    if ( state.backtracking==0 ) {
                       gt = new GraphTerm(i); 
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:777:5: r= rDFLiteral
                    {
                    pushFollow(FOLLOW_rDFLiteral_in_graphTerm5286);
                    r=rDFLiteral();

                    state._fsp--;
                    if (state.failed) return gt;
                    if ( state.backtracking==0 ) {
                       gt = new GraphTerm(new Constant(r)); 
                    }

                    }
                    break;
                case 3 :
                    // IbmSparqlExtAstWalker.g:778:4: d= numericLiteral
                    {
                    pushFollow(FOLLOW_numericLiteral_in_graphTerm5298);
                    d=numericLiteral();

                    state._fsp--;
                    if (state.failed) return gt;
                    if ( state.backtracking==0 ) {
                       gt = new GraphTerm(d); 				
                    }

                    }
                    break;
                case 4 :
                    // IbmSparqlExtAstWalker.g:779:5: b= booleanLiteral
                    {
                    pushFollow(FOLLOW_booleanLiteral_in_graphTerm5311);
                    b=booleanLiteral();

                    state._fsp--;
                    if (state.failed) return gt;
                    if ( state.backtracking==0 ) {
                       gt = new GraphTerm(new Constant(b)); 
                    }

                    }
                    break;
                case 5 :
                    // IbmSparqlExtAstWalker.g:780:5: l= blankNode
                    {
                    pushFollow(FOLLOW_blankNode_in_graphTerm5324);
                    l=blankNode();

                    state._fsp--;
                    if (state.failed) return gt;
                    if ( state.backtracking==0 ) {
                       gt = new GraphTerm(l); 
                    }

                    }
                    break;
                case 6 :
                    // IbmSparqlExtAstWalker.g:781:5: NIL
                    {
                    match(input,NIL,FOLLOW_NIL_in_graphTerm5337); if (state.failed) return gt;
                    if ( state.backtracking==0 ) {
                       gt = new GraphTerm(new IRI("http://www.w3.org/1999/02/22-rdf-syntax-ns#nil")); 
                    }

                    }
                    break;

            }
        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return gt;
    }
    // $ANTLR end "graphTerm"


    // $ANTLR start "expression"
    // IbmSparqlExtAstWalker.g:784:1: expression returns [Expression e] : ( ^( LOGICAL_OR e1= expression (e2= expression )+ ) | ^( LOGICAL_AND e3= expression (e4= expression )+ ) | ^( '=' e5= expression e6= expression ) | ^( '!=' e7= expression e8= expression ) | ^( LT e9= expression e10= expression ) | ^( '>' e11= expression e12= expression ) | ^( LTE e13= expression e14= expression ) | ^( '>=' e15= expression e16= expression ) | ^( IN e17= expression e18= expressionList ) | ^( NOT e19= expression e20= expressionList ) | ^( '+' e21= expression (e22= expression )? ) | ^( BROKEN_PLUS e21= expression e22= expression ) | ^( '-' e23= expression (e24= expression )? ) | ^( BROKEN_MINUS e23= expression e24= expression ) | ^( '*' e25= expression e26= expression ) | ^( '/' e27= expression e28= expression ) | ^( '!' e29= expression ) | e30= brackettedExpression | e31= primaryExpression );
    public final Expression expression() throws RecognitionException {
        Expression e = null;

        Expression e1 = null;

        Expression e2 = null;

        Expression e3 = null;

        Expression e4 = null;

        Expression e5 = null;

        Expression e6 = null;

        Expression e7 = null;

        Expression e8 = null;

        Expression e9 = null;

        Expression e10 = null;

        Expression e11 = null;

        Expression e12 = null;

        Expression e13 = null;

        Expression e14 = null;

        Expression e15 = null;

        Expression e16 = null;

        Expression e17 = null;

        List<Expression> e18 = null;

        Expression e19 = null;

        List<Expression> e20 = null;

        Expression e21 = null;

        Expression e22 = null;

        Expression e23 = null;

        Expression e24 = null;

        Expression e25 = null;

        Expression e26 = null;

        Expression e27 = null;

        Expression e28 = null;

        Expression e29 = null;

        Expression e30 = null;

        Expression e31 = null;


         
        		LogicalExpression le = null;
        		RelationalExpression rl = null;
        		NumericExpression ne = null;
        	
        try {
            // IbmSparqlExtAstWalker.g:790:2: ( ^( LOGICAL_OR e1= expression (e2= expression )+ ) | ^( LOGICAL_AND e3= expression (e4= expression )+ ) | ^( '=' e5= expression e6= expression ) | ^( '!=' e7= expression e8= expression ) | ^( LT e9= expression e10= expression ) | ^( '>' e11= expression e12= expression ) | ^( LTE e13= expression e14= expression ) | ^( '>=' e15= expression e16= expression ) | ^( IN e17= expression e18= expressionList ) | ^( NOT e19= expression e20= expressionList ) | ^( '+' e21= expression (e22= expression )? ) | ^( BROKEN_PLUS e21= expression e22= expression ) | ^( '-' e23= expression (e24= expression )? ) | ^( BROKEN_MINUS e23= expression e24= expression ) | ^( '*' e25= expression e26= expression ) | ^( '/' e27= expression e28= expression ) | ^( '!' e29= expression ) | e30= brackettedExpression | e31= primaryExpression )
            int alt103=19;
            switch ( input.LA(1) ) {
            case LOGICAL_OR:
                {
                alt103=1;
                }
                break;
            case LOGICAL_AND:
                {
                alt103=2;
                }
                break;
            case 264:
                {
                alt103=3;
                }
                break;
            case 265:
                {
                alt103=4;
                }
                break;
            case LT:
                {
                alt103=5;
                }
                break;
            case 266:
                {
                alt103=6;
                }
                break;
            case LTE:
                {
                alt103=7;
                }
                break;
            case 267:
                {
                alt103=8;
                }
                break;
            case IN:
                {
                alt103=9;
                }
                break;
            case NOT:
                {
                alt103=10;
                }
                break;
            case 262:
                {
                alt103=11;
                }
                break;
            case BROKEN_PLUS:
                {
                alt103=12;
                }
                break;
            case 268:
                {
                alt103=13;
                }
                break;
            case BROKEN_MINUS:
                {
                alt103=14;
                }
                break;
            case 256:
                {
                alt103=15;
                }
                break;
            case 259:
                {
                alt103=16;
                }
                break;
            case 263:
                {
                alt103=17;
                }
                break;
            case EXPRESSION:
                {
                alt103=18;
                }
                break;
            case VAR:
            case PREFIXED_NAME:
            case PREFIXED_NS:
            case FUNCTION:
            case NOT_EXISTS:
            case STRING:
            case BOOLEAN:
            case BIG_INTEGER:
            case BIG_DECIMAL:
            case STR:
            case LANG:
            case LANGMATCHES:
            case DATATYPE:
            case BOUND:
            case IRI:
            case URI:
            case BNODE:
            case RAND:
            case ABS:
            case CEIL:
            case FLOOR:
            case ROUND:
            case CONCAT:
            case STRLEN:
            case UCASE:
            case LCASE:
            case ENCODE_FOR_URI:
            case CONTAINS:
            case STRSTARTS:
            case STRENDS:
            case STRBEFORE:
            case STRAFTER:
            case YEAR:
            case MONTH:
            case DAY:
            case HOURS:
            case MINUTES:
            case SECONDS:
            case TIMEZONE:
            case TZ:
            case NOW:
            case UUID:
            case STRUUID:
            case MD5:
            case SHA1:
            case SHA256:
            case SHA384:
            case SHA512:
            case COALESCE:
            case IF:
            case STRLANG:
            case STRDT:
            case SAMETERM:
            case ISIRI:
            case ISURI:
            case ISBLANK:
            case ISLITERAL:
            case ISNUMERIC:
            case REGEX:
            case SUBSTR:
            case REPLACE:
            case EXISTS:
            case COUNT:
            case SUM:
            case MIN:
            case MAX:
            case AVG:
            case SAMPLE:
            case GROUP_CONCAT:
            case DOUBLE:
                {
                alt103=19;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return e;}
                NoViableAltException nvae =
                    new NoViableAltException("", 103, 0, input);

                throw nvae;
            }

            switch (alt103) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:790:6: ^( LOGICAL_OR e1= expression (e2= expression )+ )
                    {
                    match(input,LOGICAL_OR,FOLLOW_LOGICAL_OR_in_expression5368); if (state.failed) return e;

                    if ( state.backtracking==0 ) {
                       le = new LogicalExpression(LogicalExpression.ELogicalType.OR);  
                    }

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression5380);
                    e1=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       le.addComponent(e1);   
                    }
                    // IbmSparqlExtAstWalker.g:792:4: (e2= expression )+
                    int cnt99=0;
                    loop99:
                    do {
                        int alt99=2;
                        int LA99_0 = input.LA(1);

                        if ( ((LA99_0>=BROKEN_PLUS && LA99_0<=BROKEN_MINUS)||(LA99_0>=VAR && LA99_0<=NOT_EXISTS)||(LA99_0>=STRING && LA99_0<=BOOLEAN)||LA99_0==LTE||(LA99_0>=BIG_INTEGER && LA99_0<=BIG_DECIMAL)||(LA99_0>=LOGICAL_OR && LA99_0<=SHA1)||(LA99_0>=SHA256 && LA99_0<=GROUP_CONCAT)||LA99_0==DOUBLE||LA99_0==256||LA99_0==259||(LA99_0>=262 && LA99_0<=268)) ) {
                            alt99=1;
                        }


                        switch (alt99) {
                    	case 1 :
                    	    // IbmSparqlExtAstWalker.g:792:5: e2= expression
                    	    {
                    	    pushFollow(FOLLOW_expression_in_expression5401);
                    	    e2=expression();

                    	    state._fsp--;
                    	    if (state.failed) return e;
                    	    if ( state.backtracking==0 ) {
                    	       le.addComponent(e2); 	
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt99 >= 1 ) break loop99;
                    	    if (state.backtracking>0) {state.failed=true; return e;}
                                EarlyExitException eee =
                                    new EarlyExitException(99, input);
                                throw eee;
                        }
                        cnt99++;
                    } while (true);


                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = le; 
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:794:6: ^( LOGICAL_AND e3= expression (e4= expression )+ )
                    {
                    match(input,LOGICAL_AND,FOLLOW_LOGICAL_AND_in_expression5426); if (state.failed) return e;

                    if ( state.backtracking==0 ) {
                       le = new LogicalExpression(LogicalExpression.ELogicalType.AND); 
                    }

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression5438);
                    e3=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       le.addComponent(e3);   
                    }
                    // IbmSparqlExtAstWalker.g:796:4: (e4= expression )+
                    int cnt100=0;
                    loop100:
                    do {
                        int alt100=2;
                        int LA100_0 = input.LA(1);

                        if ( ((LA100_0>=BROKEN_PLUS && LA100_0<=BROKEN_MINUS)||(LA100_0>=VAR && LA100_0<=NOT_EXISTS)||(LA100_0>=STRING && LA100_0<=BOOLEAN)||LA100_0==LTE||(LA100_0>=BIG_INTEGER && LA100_0<=BIG_DECIMAL)||(LA100_0>=LOGICAL_OR && LA100_0<=SHA1)||(LA100_0>=SHA256 && LA100_0<=GROUP_CONCAT)||LA100_0==DOUBLE||LA100_0==256||LA100_0==259||(LA100_0>=262 && LA100_0<=268)) ) {
                            alt100=1;
                        }


                        switch (alt100) {
                    	case 1 :
                    	    // IbmSparqlExtAstWalker.g:796:5: e4= expression
                    	    {
                    	    pushFollow(FOLLOW_expression_in_expression5459);
                    	    e4=expression();

                    	    state._fsp--;
                    	    if (state.failed) return e;
                    	    if ( state.backtracking==0 ) {
                    	       le.addComponent(e4); 	
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt100 >= 1 ) break loop100;
                    	    if (state.backtracking>0) {state.failed=true; return e;}
                                EarlyExitException eee =
                                    new EarlyExitException(100, input);
                                throw eee;
                        }
                        cnt100++;
                    } while (true);


                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = le; 
                    }

                    }
                    break;
                case 3 :
                    // IbmSparqlExtAstWalker.g:798:6: ^( '=' e5= expression e6= expression )
                    {
                    match(input,264,FOLLOW_264_in_expression5486); if (state.failed) return e;

                    if ( state.backtracking==0 ) {
                       rl = new RelationalExpression(); 								  
                    }

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression5500);
                    e5=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       rl.setLeft(e5); 				
                    }
                    pushFollow(FOLLOW_expression_in_expression5511);
                    e6=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       rl.setRight(RelationalExpression.ERelationalOp.EQUAL, e6); 		  
                    }

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = rl; 
                    }

                    }
                    break;
                case 4 :
                    // IbmSparqlExtAstWalker.g:802:4: ^( '!=' e7= expression e8= expression )
                    {
                    match(input,265,FOLLOW_265_in_expression5531); if (state.failed) return e;

                    if ( state.backtracking==0 ) {
                       rl = new RelationalExpression(); 								  
                    }

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression5545);
                    e7=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       rl.setLeft(e7); 				
                    }
                    pushFollow(FOLLOW_expression_in_expression5556);
                    e8=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       rl.setRight(RelationalExpression.ERelationalOp.NOT_EQUAL, e8);    
                    }

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = rl; 
                    }

                    }
                    break;
                case 5 :
                    // IbmSparqlExtAstWalker.g:806:4: ^( LT e9= expression e10= expression )
                    {
                    match(input,LT,FOLLOW_LT_in_expression5576); if (state.failed) return e;

                    if ( state.backtracking==0 ) {
                       rl = new RelationalExpression(); 								  
                    }

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression5591);
                    e9=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       rl.setLeft(e9); 				
                    }
                    pushFollow(FOLLOW_expression_in_expression5606);
                    e10=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       rl.setRight(RelationalExpression.ERelationalOp.LESS, e10);		  
                    }

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = rl; 
                    }

                    }
                    break;
                case 6 :
                    // IbmSparqlExtAstWalker.g:810:4: ^( '>' e11= expression e12= expression )
                    {
                    match(input,266,FOLLOW_266_in_expression5626); if (state.failed) return e;

                    if ( state.backtracking==0 ) {
                       rl = new RelationalExpression(); 								  
                    }

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression5641);
                    e11=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       rl.setLeft(e11); 				
                    }
                    pushFollow(FOLLOW_expression_in_expression5652);
                    e12=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       rl.setRight(RelationalExpression.ERelationalOp.GREATER, e12);	  
                    }

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = rl; 
                    }

                    }
                    break;
                case 7 :
                    // IbmSparqlExtAstWalker.g:814:4: ^( LTE e13= expression e14= expression )
                    {
                    match(input,LTE,FOLLOW_LTE_in_expression5672); if (state.failed) return e;

                    if ( state.backtracking==0 ) {
                       rl = new RelationalExpression(); 								  
                    }

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression5686);
                    e13=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       rl.setLeft(e13); 				
                    }
                    pushFollow(FOLLOW_expression_in_expression5697);
                    e14=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       rl.setRight(RelationalExpression.ERelationalOp.LESS_EQUAL, e14); 
                    }

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = rl; 
                    }

                    }
                    break;
                case 8 :
                    // IbmSparqlExtAstWalker.g:818:4: ^( '>=' e15= expression e16= expression )
                    {
                    match(input,267,FOLLOW_267_in_expression5717); if (state.failed) return e;

                    if ( state.backtracking==0 ) {
                       rl = new RelationalExpression(); 								  
                    }

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression5731);
                    e15=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       rl.setLeft(e15); 				
                    }
                    pushFollow(FOLLOW_expression_in_expression5742);
                    e16=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       rl.setRight(RelationalExpression.ERelationalOp.GREATER_EQUAL, e16); 
                    }

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = rl; 
                    }

                    }
                    break;
                case 9 :
                    // IbmSparqlExtAstWalker.g:822:4: ^( IN e17= expression e18= expressionList )
                    {
                    match(input,IN,FOLLOW_IN_in_expression5762); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression5789);
                    e17=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expressionList_in_expression5802);
                    e18=expressionList();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new OneOfExpression(Expression.EOneOfOp.IN ,e17, e18); 
                    }

                    match(input, Token.UP, null); if (state.failed) return e;

                    }
                    break;
                case 10 :
                    // IbmSparqlExtAstWalker.g:827:4: ^( NOT e19= expression e20= expressionList )
                    {
                    match(input,NOT,FOLLOW_NOT_in_expression5848); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression5855);
                    e19=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expressionList_in_expression5863);
                    e20=expressionList();

                    state._fsp--;
                    if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new OneOfExpression(Expression.EOneOfOp.NOT_IN ,e19, e20); 
                    }

                    }
                    break;
                case 11 :
                    // IbmSparqlExtAstWalker.g:831:4: ^( '+' e21= expression (e22= expression )? )
                    {
                    match(input,262,FOLLOW_262_in_expression5897); if (state.failed) return e;

                    if ( state.backtracking==0 ) {
                       ne = new NumericExpression(); 								
                    }

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression5924);
                    e21=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       ne.setLHS(e21); 				
                    }
                    // IbmSparqlExtAstWalker.g:833:4: (e22= expression )?
                    int alt101=2;
                    int LA101_0 = input.LA(1);

                    if ( ((LA101_0>=BROKEN_PLUS && LA101_0<=BROKEN_MINUS)||(LA101_0>=VAR && LA101_0<=NOT_EXISTS)||(LA101_0>=STRING && LA101_0<=BOOLEAN)||LA101_0==LTE||(LA101_0>=BIG_INTEGER && LA101_0<=BIG_DECIMAL)||(LA101_0>=LOGICAL_OR && LA101_0<=SHA1)||(LA101_0>=SHA256 && LA101_0<=GROUP_CONCAT)||LA101_0==DOUBLE||LA101_0==256||LA101_0==259||(LA101_0>=262 && LA101_0<=268)) ) {
                        alt101=1;
                    }
                    switch (alt101) {
                        case 1 :
                            // IbmSparqlExtAstWalker.g:833:5: e22= expression
                            {
                            pushFollow(FOLLOW_expression_in_expression5936);
                            e22=expression();

                            state._fsp--;
                            if (state.failed) return e;
                            if ( state.backtracking==0 ) {
                               ne.setRHS(NumericExpression.ENEType.PLUS, e22);      			
                            }

                            }
                            break;

                    }


                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = ne; 
                    }

                    }
                    break;
                case 12 :
                    // IbmSparqlExtAstWalker.g:835:4: ^( BROKEN_PLUS e21= expression e22= expression )
                    {
                    match(input,BROKEN_PLUS,FOLLOW_BROKEN_PLUS_in_expression5959); if (state.failed) return e;

                    if ( state.backtracking==0 ) {
                       ne = new NumericExpression(); 								
                    }

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression5978);
                    e21=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       ne.setLHS(e21); 				
                    }
                    pushFollow(FOLLOW_expression_in_expression5989);
                    e22=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       ne.setRHS(NumericExpression.ENEType.PLUS, e22);      			
                    }

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = ne; 
                    }

                    }
                    break;
                case 13 :
                    // IbmSparqlExtAstWalker.g:839:4: ^( '-' e23= expression (e24= expression )? )
                    {
                    match(input,268,FOLLOW_268_in_expression6009); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression6035);
                    e23=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new UnaryExpression(Expression.EUnaryOp.MINUS, e23); 
                    }
                    // IbmSparqlExtAstWalker.g:841:4: (e24= expression )?
                    int alt102=2;
                    int LA102_0 = input.LA(1);

                    if ( ((LA102_0>=BROKEN_PLUS && LA102_0<=BROKEN_MINUS)||(LA102_0>=VAR && LA102_0<=NOT_EXISTS)||(LA102_0>=STRING && LA102_0<=BOOLEAN)||LA102_0==LTE||(LA102_0>=BIG_INTEGER && LA102_0<=BIG_DECIMAL)||(LA102_0>=LOGICAL_OR && LA102_0<=SHA1)||(LA102_0>=SHA256 && LA102_0<=GROUP_CONCAT)||LA102_0==DOUBLE||LA102_0==256||LA102_0==259||(LA102_0>=262 && LA102_0<=268)) ) {
                        alt102=1;
                    }
                    switch (alt102) {
                        case 1 :
                            // IbmSparqlExtAstWalker.g:841:5: e24= expression
                            {
                            pushFollow(FOLLOW_expression_in_expression6047);
                            e24=expression();

                            state._fsp--;
                            if (state.failed) return e;
                            if ( state.backtracking==0 ) {
                               e = new NumericExpression(NumericExpression.ENEType.MINUS, e23, e24); 
                            }

                            }
                            break;

                    }


                    match(input, Token.UP, null); if (state.failed) return e;

                    }
                    break;
                case 14 :
                    // IbmSparqlExtAstWalker.g:843:4: ^( BROKEN_MINUS e23= expression e24= expression )
                    {
                    match(input,BROKEN_MINUS,FOLLOW_BROKEN_MINUS_in_expression6064); if (state.failed) return e;

                    if ( state.backtracking==0 ) {
                       ne = new NumericExpression(); 								
                    }

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression6082);
                    e23=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       ne.setLHS(e23); 				
                    }
                    pushFollow(FOLLOW_expression_in_expression6093);
                    e24=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       ne.setRHS(NumericExpression.ENEType.MINUS, e24);   			
                    }

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = ne; 
                    }

                    }
                    break;
                case 15 :
                    // IbmSparqlExtAstWalker.g:847:4: ^( '*' e25= expression e26= expression )
                    {
                    match(input,256,FOLLOW_256_in_expression6113); if (state.failed) return e;

                    if ( state.backtracking==0 ) {
                       ne = new NumericExpression(); 								
                    }

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression6140);
                    e25=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       ne.setLHS(e25); 				
                    }
                    pushFollow(FOLLOW_expression_in_expression6151);
                    e26=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       ne.setRHS(NumericExpression.ENEType.MUL, e26);   				
                    }

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = ne; 
                    }

                    }
                    break;
                case 16 :
                    // IbmSparqlExtAstWalker.g:851:4: ^( '/' e27= expression e28= expression )
                    {
                    match(input,259,FOLLOW_259_in_expression6171); if (state.failed) return e;

                    if ( state.backtracking==0 ) {
                       ne = new NumericExpression(); 								
                    }

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression6198);
                    e27=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       ne.setLHS(e27); 				
                    }
                    pushFollow(FOLLOW_expression_in_expression6208);
                    e28=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       ne.setRHS(NumericExpression.ENEType.DIV, e28);   			
                    }

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = ne; 
                    }

                    }
                    break;
                case 17 :
                    // IbmSparqlExtAstWalker.g:855:4: ^( '!' e29= expression )
                    {
                    match(input,263,FOLLOW_263_in_expression6228); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression6232);
                    e29=expression();

                    state._fsp--;
                    if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new UnaryExpression(Expression.EUnaryOp.NOT, e29); 		
                    }

                    }
                    break;
                case 18 :
                    // IbmSparqlExtAstWalker.g:856:4: e30= brackettedExpression
                    {
                    pushFollow(FOLLOW_brackettedExpression_in_expression6243);
                    e30=brackettedExpression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = e30; 
                    }

                    }
                    break;
                case 19 :
                    // IbmSparqlExtAstWalker.g:857:5: e31= primaryExpression
                    {
                    pushFollow(FOLLOW_primaryExpression_in_expression6253);
                    e31=primaryExpression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = e31; 
                    }

                    }
                    break;

            }
        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return e;
    }
    // $ANTLR end "expression"


    // $ANTLR start "primaryExpression"
    // IbmSparqlExtAstWalker.g:861:1: primaryExpression returns [Expression e] : (e1= builtInCall | i= iRIref | f= iRIFunction | r= rDFLiteral | n= numericLiteral | b= booleanLiteral | v= var | a= aggregate );
    public final Expression primaryExpression() throws RecognitionException {
        Expression e = null;

        Expression e1 = null;

        IRI i = null;

        FunctionCall f = null;

        StringLiteral r = null;

        Constant n = null;

        Boolean b = null;

        String v = null;

        AggregateExpression a = null;


        try {
            // IbmSparqlExtAstWalker.g:862:2: (e1= builtInCall | i= iRIref | f= iRIFunction | r= rDFLiteral | n= numericLiteral | b= booleanLiteral | v= var | a= aggregate )
            int alt104=8;
            alt104 = dfa104.predict(input);
            switch (alt104) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:862:6: e1= builtInCall
                    {
                    pushFollow(FOLLOW_builtInCall_in_primaryExpression6278);
                    e1=builtInCall();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = e1; 
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:863:5: i= iRIref
                    {
                    pushFollow(FOLLOW_iRIref_in_primaryExpression6290);
                    i=iRIref();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new ConstantExpression(i); 
                    }

                    }
                    break;
                case 3 :
                    // IbmSparqlExtAstWalker.g:864:4: f= iRIFunction
                    {
                    pushFollow(FOLLOW_iRIFunction_in_primaryExpression6302);
                    f=iRIFunction();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new FunctionCallExpression(f); 
                    }

                    }
                    break;
                case 4 :
                    // IbmSparqlExtAstWalker.g:865:5: r= rDFLiteral
                    {
                    pushFollow(FOLLOW_rDFLiteral_in_primaryExpression6314);
                    r=rDFLiteral();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new ConstantExpression(r); 
                    }

                    }
                    break;
                case 5 :
                    // IbmSparqlExtAstWalker.g:866:5: n= numericLiteral
                    {
                    pushFollow(FOLLOW_numericLiteral_in_primaryExpression6326);
                    n=numericLiteral();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new ConstantExpression(n); 
                    }

                    }
                    break;
                case 6 :
                    // IbmSparqlExtAstWalker.g:867:5: b= booleanLiteral
                    {
                    pushFollow(FOLLOW_booleanLiteral_in_primaryExpression6337);
                    b=booleanLiteral();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new ConstantExpression(b); 
                    }

                    }
                    break;
                case 7 :
                    // IbmSparqlExtAstWalker.g:868:5: v= var
                    {
                    pushFollow(FOLLOW_var_in_primaryExpression6348);
                    v=var();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new VariableExpression(v); 
                    }

                    }
                    break;
                case 8 :
                    // IbmSparqlExtAstWalker.g:869:5: a= aggregate
                    {
                    pushFollow(FOLLOW_aggregate_in_primaryExpression6362);
                    a=aggregate();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = a; 
                    }

                    }
                    break;

            }
        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return e;
    }
    // $ANTLR end "primaryExpression"


    // $ANTLR start "brackettedExpression"
    // IbmSparqlExtAstWalker.g:872:1: brackettedExpression returns [Expression e] : ^( EXPRESSION e1= expression ) ;
    public final Expression brackettedExpression() throws RecognitionException {
        Expression e = null;

        Expression e1 = null;


        try {
            // IbmSparqlExtAstWalker.g:873:2: ( ^( EXPRESSION e1= expression ) )
            // IbmSparqlExtAstWalker.g:873:4: ^( EXPRESSION e1= expression )
            {
            match(input,EXPRESSION,FOLLOW_EXPRESSION_in_brackettedExpression6385); if (state.failed) return e;

            match(input, Token.DOWN, null); if (state.failed) return e;
            pushFollow(FOLLOW_expression_in_brackettedExpression6390);
            e1=expression();

            state._fsp--;
            if (state.failed) return e;

            match(input, Token.UP, null); if (state.failed) return e;
            if ( state.backtracking==0 ) {
               e = e1; 
            }

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return e;
    }
    // $ANTLR end "brackettedExpression"


    // $ANTLR start "builtInCall"
    // IbmSparqlExtAstWalker.g:876:1: builtInCall returns [Expression e] : ( ^( STR st= expression ) | ^( LANG lg= expression ) | ^( LANGMATCHES lm1= expression lm2= expression ) | ^( DATATYPE dt= expression ) | ^( BOUND v= var ) | ^( IRI e6= expression ) | ^( URI e7= expression ) | ^( BNODE e8= expression ) | BNODE | ^( RAND NIL ) | ^( ABS e9= expression ) | ^( CEIL e10= expression ) | ^( FLOOR e11= expression ) | ^( ROUND e12= expression ) | ^( CONCAT e13= expressionList ) | ^( SUBSTR e14= expression e15= expression (e16= expression )? ) | ^( STRLEN e15= expression ) | ^( UCASE e16= expression ) | ^( REPLACE e1= expression e2= expression e3= expression (e4= expression )? ) | ^( LCASE e17= expression ) | ^( ENCODE_FOR_URI e18= expression ) | ^( CONTAINS e19= expression e20= expression ) | ^( STRSTARTS e21= expression e22= expression ) | ^( STRENDS e23= expression e24= expression ) | ^( STRBEFORE e241= expression e242= expression ) | ^( STRAFTER e243= expression e244= expression ) | ^( YEAR e25= expression ) | ^( MONTH e26= expression ) | ^( DAY e27= expression ) | ^( HOURS e28= expression ) | ^( MINUTES e29= expression ) | ^( SECONDS e30= expression ) | ^( TIMEZONE e31= expression ) | ^( TZ e32= expression ) | NOW | UUID | STRUUID | ^( MD5 e33= expression ) | ^( SHA1 e34= expression ) | ^( SHA256 e36= expression ) | ^( SHA384 e37= expression ) | ^( SHA512 e38= expression ) | ^( COALESCE e39= expressionList ) | ^( IF e40= expression e41= expression e42= expression ) | ^( STRLANG e45= expression e46= expression ) | ^( STRDT e47= expression e48= expression ) | ^( SAMETERM sam1= expression sam2= expression ) | ^( ISIRI isi= expression ) | ^( ISURI isu= expression ) | ^( ISBLANK isb= expression ) | ^( ISLITERAL isl= expression ) | ^( ISNUMERIC e55= expression ) | r= regexExpression | p= existsFunc | p= notExistsFunc );
    public final Expression builtInCall() throws RecognitionException {
        Expression e = null;

        Expression st = null;

        Expression lg = null;

        Expression lm1 = null;

        Expression lm2 = null;

        Expression dt = null;

        String v = null;

        Expression e6 = null;

        Expression e7 = null;

        Expression e8 = null;

        Expression e9 = null;

        Expression e10 = null;

        Expression e11 = null;

        Expression e12 = null;

        List<Expression> e13 = null;

        Expression e14 = null;

        Expression e15 = null;

        Expression e16 = null;

        Expression e1 = null;

        Expression e2 = null;

        Expression e3 = null;

        Expression e4 = null;

        Expression e17 = null;

        Expression e18 = null;

        Expression e19 = null;

        Expression e20 = null;

        Expression e21 = null;

        Expression e22 = null;

        Expression e23 = null;

        Expression e24 = null;

        Expression e241 = null;

        Expression e242 = null;

        Expression e243 = null;

        Expression e244 = null;

        Expression e25 = null;

        Expression e26 = null;

        Expression e27 = null;

        Expression e28 = null;

        Expression e29 = null;

        Expression e30 = null;

        Expression e31 = null;

        Expression e32 = null;

        Expression e33 = null;

        Expression e34 = null;

        Expression e36 = null;

        Expression e37 = null;

        Expression e38 = null;

        List<Expression> e39 = null;

        Expression e40 = null;

        Expression e41 = null;

        Expression e42 = null;

        Expression e45 = null;

        Expression e46 = null;

        Expression e47 = null;

        Expression e48 = null;

        Expression sam1 = null;

        Expression sam2 = null;

        Expression isi = null;

        Expression isu = null;

        Expression isb = null;

        Expression isl = null;

        Expression e55 = null;

        Expression r = null;

        Pattern p = null;



        		ArrayList args = new ArrayList();
        	
        try {
            // IbmSparqlExtAstWalker.g:880:2: ( ^( STR st= expression ) | ^( LANG lg= expression ) | ^( LANGMATCHES lm1= expression lm2= expression ) | ^( DATATYPE dt= expression ) | ^( BOUND v= var ) | ^( IRI e6= expression ) | ^( URI e7= expression ) | ^( BNODE e8= expression ) | BNODE | ^( RAND NIL ) | ^( ABS e9= expression ) | ^( CEIL e10= expression ) | ^( FLOOR e11= expression ) | ^( ROUND e12= expression ) | ^( CONCAT e13= expressionList ) | ^( SUBSTR e14= expression e15= expression (e16= expression )? ) | ^( STRLEN e15= expression ) | ^( UCASE e16= expression ) | ^( REPLACE e1= expression e2= expression e3= expression (e4= expression )? ) | ^( LCASE e17= expression ) | ^( ENCODE_FOR_URI e18= expression ) | ^( CONTAINS e19= expression e20= expression ) | ^( STRSTARTS e21= expression e22= expression ) | ^( STRENDS e23= expression e24= expression ) | ^( STRBEFORE e241= expression e242= expression ) | ^( STRAFTER e243= expression e244= expression ) | ^( YEAR e25= expression ) | ^( MONTH e26= expression ) | ^( DAY e27= expression ) | ^( HOURS e28= expression ) | ^( MINUTES e29= expression ) | ^( SECONDS e30= expression ) | ^( TIMEZONE e31= expression ) | ^( TZ e32= expression ) | NOW | UUID | STRUUID | ^( MD5 e33= expression ) | ^( SHA1 e34= expression ) | ^( SHA256 e36= expression ) | ^( SHA384 e37= expression ) | ^( SHA512 e38= expression ) | ^( COALESCE e39= expressionList ) | ^( IF e40= expression e41= expression e42= expression ) | ^( STRLANG e45= expression e46= expression ) | ^( STRDT e47= expression e48= expression ) | ^( SAMETERM sam1= expression sam2= expression ) | ^( ISIRI isi= expression ) | ^( ISURI isu= expression ) | ^( ISBLANK isb= expression ) | ^( ISLITERAL isl= expression ) | ^( ISNUMERIC e55= expression ) | r= regexExpression | p= existsFunc | p= notExistsFunc )
            int alt107=55;
            alt107 = dfa107.predict(input);
            switch (alt107) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:880:5: ^( STR st= expression )
                    {
                    match(input,STR,FOLLOW_STR_in_builtInCall6420); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6424);
                    st=expression();

                    state._fsp--;
                    if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       args.add(st); e = new BuiltinFunctionExpression(Expression.EBuiltinType.STR, args); 			
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:882:5: ^( LANG lg= expression )
                    {
                    match(input,LANG,FOLLOW_LANG_in_builtInCall6437); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6441);
                    lg=expression();

                    state._fsp--;
                    if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       args.add(lg); e = new BuiltinFunctionExpression(Expression.EBuiltinType.LANG, args); 		
                    }

                    }
                    break;
                case 3 :
                    // IbmSparqlExtAstWalker.g:884:5: ^( LANGMATCHES lm1= expression lm2= expression )
                    {
                    match(input,LANGMATCHES,FOLLOW_LANGMATCHES_in_builtInCall6454); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6458);
                    lm1=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6462);
                    lm2=expression();

                    state._fsp--;
                    if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new BuiltinFunctionExpression(Expression.EBuiltinType.LANGMATCHES, lm1, lm2); 			
                    }

                    }
                    break;
                case 4 :
                    // IbmSparqlExtAstWalker.g:886:5: ^( DATATYPE dt= expression )
                    {
                    match(input,DATATYPE,FOLLOW_DATATYPE_in_builtInCall6475); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6479);
                    dt=expression();

                    state._fsp--;
                    if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       args.add(dt); e = new BuiltinFunctionExpression(Expression.EBuiltinType.DATATYPE, args); 	
                    }

                    }
                    break;
                case 5 :
                    // IbmSparqlExtAstWalker.g:888:4: ^( BOUND v= var )
                    {
                    match(input,BOUND,FOLLOW_BOUND_in_builtInCall6491); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_var_in_builtInCall6495);
                    v=var();

                    state._fsp--;
                    if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new BuiltinFunctionExpression(Expression.EBuiltinType.BOUND, new VariableExpression(v)); 
                    }

                    }
                    break;
                case 6 :
                    // IbmSparqlExtAstWalker.g:890:4: ^( IRI e6= expression )
                    {
                    match(input,IRI,FOLLOW_IRI_in_builtInCall6513); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6517);
                    e6=expression();

                    state._fsp--;
                    if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       args.add(e6); e = new BuiltinFunctionExpression(Expression.EBuiltinType.IRI, args);			
                    }

                    }
                    break;
                case 7 :
                    // IbmSparqlExtAstWalker.g:892:5: ^( URI e7= expression )
                    {
                    match(input,URI,FOLLOW_URI_in_builtInCall6530); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6534);
                    e7=expression();

                    state._fsp--;
                    if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       args.add(e7); e = new BuiltinFunctionExpression(Expression.EBuiltinType.IRI, args);			
                    }

                    }
                    break;
                case 8 :
                    // IbmSparqlExtAstWalker.g:894:4: ^( BNODE e8= expression )
                    {
                    match(input,BNODE,FOLLOW_BNODE_in_builtInCall6546); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6550);
                    e8=expression();

                    state._fsp--;
                    if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       args.add(e8); e = new BuiltinFunctionExpression(Expression.EBuiltinType.BNODE, args);		
                    }

                    }
                    break;
                case 9 :
                    // IbmSparqlExtAstWalker.g:896:4: BNODE
                    {
                    match(input,BNODE,FOLLOW_BNODE_in_builtInCall6561); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new BuiltinFunctionExpression(Expression.EBuiltinType.BNODE); 							
                    }

                    }
                    break;
                case 10 :
                    // IbmSparqlExtAstWalker.g:898:4: ^( RAND NIL )
                    {
                    match(input,RAND,FOLLOW_RAND_in_builtInCall6572); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    match(input,NIL,FOLLOW_NIL_in_builtInCall6574); if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new BuiltinFunctionExpression(Expression.EBuiltinType.RAND); 							
                    }

                    }
                    break;
                case 11 :
                    // IbmSparqlExtAstWalker.g:900:4: ^( ABS e9= expression )
                    {
                    match(input,ABS,FOLLOW_ABS_in_builtInCall6587); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6591);
                    e9=expression();

                    state._fsp--;
                    if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       args.add(e9); e = new BuiltinFunctionExpression(Expression.EBuiltinType.ABS, args);			
                    }

                    }
                    break;
                case 12 :
                    // IbmSparqlExtAstWalker.g:902:4: ^( CEIL e10= expression )
                    {
                    match(input,CEIL,FOLLOW_CEIL_in_builtInCall6603); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6607);
                    e10=expression();

                    state._fsp--;
                    if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       args.add(e10); e = new BuiltinFunctionExpression(Expression.EBuiltinType.CEIL, args);		
                    }

                    }
                    break;
                case 13 :
                    // IbmSparqlExtAstWalker.g:904:4: ^( FLOOR e11= expression )
                    {
                    match(input,FLOOR,FOLLOW_FLOOR_in_builtInCall6619); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6623);
                    e11=expression();

                    state._fsp--;
                    if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       args.add(e11); e = new BuiltinFunctionExpression(Expression.EBuiltinType.FLOOR, args);		
                    }

                    }
                    break;
                case 14 :
                    // IbmSparqlExtAstWalker.g:906:4: ^( ROUND e12= expression )
                    {
                    match(input,ROUND,FOLLOW_ROUND_in_builtInCall6635); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6639);
                    e12=expression();

                    state._fsp--;
                    if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       args.add(e12); e = new BuiltinFunctionExpression(Expression.EBuiltinType.ROUND, args);		
                    }

                    }
                    break;
                case 15 :
                    // IbmSparqlExtAstWalker.g:908:4: ^( CONCAT e13= expressionList )
                    {
                    match(input,CONCAT,FOLLOW_CONCAT_in_builtInCall6651); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expressionList_in_builtInCall6655);
                    e13=expressionList();

                    state._fsp--;
                    if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       args.addAll(e13); e = new BuiltinFunctionExpression(Expression.EBuiltinType.CONCAT, args);		
                    }

                    }
                    break;
                case 16 :
                    // IbmSparqlExtAstWalker.g:910:5: ^( SUBSTR e14= expression e15= expression (e16= expression )? )
                    {
                    match(input,SUBSTR,FOLLOW_SUBSTR_in_builtInCall6668); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6672);
                    e14=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6676);
                    e15=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       args.add(e14); args.add(e15); 
                    }
                    // IbmSparqlExtAstWalker.g:912:13: (e16= expression )?
                    int alt105=2;
                    int LA105_0 = input.LA(1);

                    if ( ((LA105_0>=BROKEN_PLUS && LA105_0<=BROKEN_MINUS)||(LA105_0>=VAR && LA105_0<=NOT_EXISTS)||(LA105_0>=STRING && LA105_0<=BOOLEAN)||LA105_0==LTE||(LA105_0>=BIG_INTEGER && LA105_0<=BIG_DECIMAL)||(LA105_0>=LOGICAL_OR && LA105_0<=SHA1)||(LA105_0>=SHA256 && LA105_0<=GROUP_CONCAT)||LA105_0==DOUBLE||LA105_0==256||LA105_0==259||(LA105_0>=262 && LA105_0<=268)) ) {
                        alt105=1;
                    }
                    switch (alt105) {
                        case 1 :
                            // IbmSparqlExtAstWalker.g:912:15: e16= expression
                            {
                            pushFollow(FOLLOW_expression_in_builtInCall6700);
                            e16=expression();

                            state._fsp--;
                            if (state.failed) return e;
                            if ( state.backtracking==0 ) {
                               args.add(e16); 
                            }

                            }
                            break;

                    }


                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {

                                e = new BuiltinFunctionExpression(Expression.EBuiltinType.SUB_STRING_EXP, args);
                              
                    }

                    }
                    break;
                case 17 :
                    // IbmSparqlExtAstWalker.g:916:4: ^( STRLEN e15= expression )
                    {
                    match(input,STRLEN,FOLLOW_STRLEN_in_builtInCall6723); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6727);
                    e15=expression();

                    state._fsp--;
                    if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       args.add(e15); e = new BuiltinFunctionExpression(Expression.EBuiltinType.STRLEN, args);		
                    }

                    }
                    break;
                case 18 :
                    // IbmSparqlExtAstWalker.g:918:4: ^( UCASE e16= expression )
                    {
                    match(input,UCASE,FOLLOW_UCASE_in_builtInCall6739); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6743);
                    e16=expression();

                    state._fsp--;
                    if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       args.add(e16); e = new BuiltinFunctionExpression(Expression.EBuiltinType.UCASE, args);		
                    }

                    }
                    break;
                case 19 :
                    // IbmSparqlExtAstWalker.g:920:4: ^( REPLACE e1= expression e2= expression e3= expression (e4= expression )? )
                    {
                    match(input,REPLACE,FOLLOW_REPLACE_in_builtInCall6755); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6759);
                    e1=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6763);
                    e2=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6767);
                    e3=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       args.add(e1); args.add(e2); args.add(e3); 
                    }
                    // IbmSparqlExtAstWalker.g:922:13: (e4= expression )?
                    int alt106=2;
                    int LA106_0 = input.LA(1);

                    if ( ((LA106_0>=BROKEN_PLUS && LA106_0<=BROKEN_MINUS)||(LA106_0>=VAR && LA106_0<=NOT_EXISTS)||(LA106_0>=STRING && LA106_0<=BOOLEAN)||LA106_0==LTE||(LA106_0>=BIG_INTEGER && LA106_0<=BIG_DECIMAL)||(LA106_0>=LOGICAL_OR && LA106_0<=SHA1)||(LA106_0>=SHA256 && LA106_0<=GROUP_CONCAT)||LA106_0==DOUBLE||LA106_0==256||LA106_0==259||(LA106_0>=262 && LA106_0<=268)) ) {
                        alt106=1;
                    }
                    switch (alt106) {
                        case 1 :
                            // IbmSparqlExtAstWalker.g:922:15: e4= expression
                            {
                            pushFollow(FOLLOW_expression_in_builtInCall6791);
                            e4=expression();

                            state._fsp--;
                            if (state.failed) return e;
                            if ( state.backtracking==0 ) {
                               args.add(e4); 
                            }

                            }
                            break;

                    }


                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new BuiltinFunctionExpression(Expression.EBuiltinType.REPLACE, args);		
                    }

                    }
                    break;
                case 20 :
                    // IbmSparqlExtAstWalker.g:924:4: ^( LCASE e17= expression )
                    {
                    match(input,LCASE,FOLLOW_LCASE_in_builtInCall6809); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6813);
                    e17=expression();

                    state._fsp--;
                    if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       args.add(e17); e = new BuiltinFunctionExpression(Expression.EBuiltinType.LCASE, args);		
                    }

                    }
                    break;
                case 21 :
                    // IbmSparqlExtAstWalker.g:926:4: ^( ENCODE_FOR_URI e18= expression )
                    {
                    match(input,ENCODE_FOR_URI,FOLLOW_ENCODE_FOR_URI_in_builtInCall6825); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6829);
                    e18=expression();

                    state._fsp--;
                    if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       args.add(e18); e = new BuiltinFunctionExpression(Expression.EBuiltinType.ENCODE_FOR_URI, args);		
                    }

                    }
                    break;
                case 22 :
                    // IbmSparqlExtAstWalker.g:928:4: ^( CONTAINS e19= expression e20= expression )
                    {
                    match(input,CONTAINS,FOLLOW_CONTAINS_in_builtInCall6841); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6845);
                    e19=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6849);
                    e20=expression();

                    state._fsp--;
                    if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new BuiltinFunctionExpression(Expression.EBuiltinType.CONTAINS, e19, e20);				 
                    }

                    }
                    break;
                case 23 :
                    // IbmSparqlExtAstWalker.g:930:4: ^( STRSTARTS e21= expression e22= expression )
                    {
                    match(input,STRSTARTS,FOLLOW_STRSTARTS_in_builtInCall6861); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6865);
                    e21=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6869);
                    e22=expression();

                    state._fsp--;
                    if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new BuiltinFunctionExpression(Expression.EBuiltinType.STRSTARTS, e21, e22);				 
                    }

                    }
                    break;
                case 24 :
                    // IbmSparqlExtAstWalker.g:932:4: ^( STRENDS e23= expression e24= expression )
                    {
                    match(input,STRENDS,FOLLOW_STRENDS_in_builtInCall6881); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6885);
                    e23=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6889);
                    e24=expression();

                    state._fsp--;
                    if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new BuiltinFunctionExpression(Expression.EBuiltinType.STRENDS, e23, e24);				 
                    }

                    }
                    break;
                case 25 :
                    // IbmSparqlExtAstWalker.g:934:4: ^( STRBEFORE e241= expression e242= expression )
                    {
                    match(input,STRBEFORE,FOLLOW_STRBEFORE_in_builtInCall6901); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6905);
                    e241=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6909);
                    e242=expression();

                    state._fsp--;
                    if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new BuiltinFunctionExpression(Expression.EBuiltinType.STRBEFORE, e241, e242);				 
                    }

                    }
                    break;
                case 26 :
                    // IbmSparqlExtAstWalker.g:936:4: ^( STRAFTER e243= expression e244= expression )
                    {
                    match(input,STRAFTER,FOLLOW_STRAFTER_in_builtInCall6921); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6925);
                    e243=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6929);
                    e244=expression();

                    state._fsp--;
                    if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new BuiltinFunctionExpression(Expression.EBuiltinType.STRAFTER, e243, e244);				 
                    }

                    }
                    break;
                case 27 :
                    // IbmSparqlExtAstWalker.g:938:4: ^( YEAR e25= expression )
                    {
                    match(input,YEAR,FOLLOW_YEAR_in_builtInCall6941); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6945);
                    e25=expression();

                    state._fsp--;
                    if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       args.add(e25); e = new BuiltinFunctionExpression(Expression.EBuiltinType.YEAR, args);		
                    }

                    }
                    break;
                case 28 :
                    // IbmSparqlExtAstWalker.g:940:4: ^( MONTH e26= expression )
                    {
                    match(input,MONTH,FOLLOW_MONTH_in_builtInCall6957); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6961);
                    e26=expression();

                    state._fsp--;
                    if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       args.add(e26); e = new BuiltinFunctionExpression(Expression.EBuiltinType.MONTH, args);		
                    }

                    }
                    break;
                case 29 :
                    // IbmSparqlExtAstWalker.g:942:4: ^( DAY e27= expression )
                    {
                    match(input,DAY,FOLLOW_DAY_in_builtInCall6973); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6977);
                    e27=expression();

                    state._fsp--;
                    if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       args.add(e27); e = new BuiltinFunctionExpression(Expression.EBuiltinType.DAY, args);		
                    }

                    }
                    break;
                case 30 :
                    // IbmSparqlExtAstWalker.g:944:4: ^( HOURS e28= expression )
                    {
                    match(input,HOURS,FOLLOW_HOURS_in_builtInCall6989); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6993);
                    e28=expression();

                    state._fsp--;
                    if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       args.add(e28); e = new BuiltinFunctionExpression(Expression.EBuiltinType.HOURS, args);		
                    }

                    }
                    break;
                case 31 :
                    // IbmSparqlExtAstWalker.g:946:4: ^( MINUTES e29= expression )
                    {
                    match(input,MINUTES,FOLLOW_MINUTES_in_builtInCall7005); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall7009);
                    e29=expression();

                    state._fsp--;
                    if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       args.add(e29); e = new BuiltinFunctionExpression(Expression.EBuiltinType.MINUTES, args);		
                    }

                    }
                    break;
                case 32 :
                    // IbmSparqlExtAstWalker.g:948:4: ^( SECONDS e30= expression )
                    {
                    match(input,SECONDS,FOLLOW_SECONDS_in_builtInCall7021); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall7025);
                    e30=expression();

                    state._fsp--;
                    if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       args.add(e30); e = new BuiltinFunctionExpression(Expression.EBuiltinType.SECONDS, args);		
                    }

                    }
                    break;
                case 33 :
                    // IbmSparqlExtAstWalker.g:950:4: ^( TIMEZONE e31= expression )
                    {
                    match(input,TIMEZONE,FOLLOW_TIMEZONE_in_builtInCall7037); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall7041);
                    e31=expression();

                    state._fsp--;
                    if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       args.add(e31); e = new BuiltinFunctionExpression(Expression.EBuiltinType.TIMEZONE, args);		
                    }

                    }
                    break;
                case 34 :
                    // IbmSparqlExtAstWalker.g:952:4: ^( TZ e32= expression )
                    {
                    match(input,TZ,FOLLOW_TZ_in_builtInCall7053); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall7057);
                    e32=expression();

                    state._fsp--;
                    if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       args.add(e32); e = new BuiltinFunctionExpression(Expression.EBuiltinType.TZ, args);		
                    }

                    }
                    break;
                case 35 :
                    // IbmSparqlExtAstWalker.g:954:4: NOW
                    {
                    match(input,NOW,FOLLOW_NOW_in_builtInCall7068); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new BuiltinFunctionExpression(Expression.EBuiltinType.NOW); 
                    }

                    }
                    break;
                case 36 :
                    // IbmSparqlExtAstWalker.g:955:9: UUID
                    {
                    match(input,UUID,FOLLOW_UUID_in_builtInCall7080); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new BuiltinFunctionExpression(Expression.EBuiltinType.UUID); 
                    }

                    }
                    break;
                case 37 :
                    // IbmSparqlExtAstWalker.g:956:9: STRUUID
                    {
                    match(input,STRUUID,FOLLOW_STRUUID_in_builtInCall7092); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new BuiltinFunctionExpression(Expression.EBuiltinType.STRUUID); 
                    }

                    }
                    break;
                case 38 :
                    // IbmSparqlExtAstWalker.g:957:4: ^( MD5 e33= expression )
                    {
                    match(input,MD5,FOLLOW_MD5_in_builtInCall7101); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall7105);
                    e33=expression();

                    state._fsp--;
                    if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new BuiltinFunctionExpression(Expression.EBuiltinType.MD5, e33); 			
                    }

                    }
                    break;
                case 39 :
                    // IbmSparqlExtAstWalker.g:959:4: ^( SHA1 e34= expression )
                    {
                    match(input,SHA1,FOLLOW_SHA1_in_builtInCall7117); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall7121);
                    e34=expression();

                    state._fsp--;
                    if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new BuiltinFunctionExpression(Expression.EBuiltinType.SHA1, e34); 			
                    }

                    }
                    break;
                case 40 :
                    // IbmSparqlExtAstWalker.g:961:4: ^( SHA256 e36= expression )
                    {
                    match(input,SHA256,FOLLOW_SHA256_in_builtInCall7133); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall7137);
                    e36=expression();

                    state._fsp--;
                    if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new BuiltinFunctionExpression(Expression.EBuiltinType.SHA256, e36); 			
                    }

                    }
                    break;
                case 41 :
                    // IbmSparqlExtAstWalker.g:963:4: ^( SHA384 e37= expression )
                    {
                    match(input,SHA384,FOLLOW_SHA384_in_builtInCall7149); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall7153);
                    e37=expression();

                    state._fsp--;
                    if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new BuiltinFunctionExpression(Expression.EBuiltinType.SHA384, e37); 			
                    }

                    }
                    break;
                case 42 :
                    // IbmSparqlExtAstWalker.g:965:4: ^( SHA512 e38= expression )
                    {
                    match(input,SHA512,FOLLOW_SHA512_in_builtInCall7165); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall7169);
                    e38=expression();

                    state._fsp--;
                    if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new BuiltinFunctionExpression(Expression.EBuiltinType.SHA512, e38); 			
                    }

                    }
                    break;
                case 43 :
                    // IbmSparqlExtAstWalker.g:967:4: ^( COALESCE e39= expressionList )
                    {
                    match(input,COALESCE,FOLLOW_COALESCE_in_builtInCall7181); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expressionList_in_builtInCall7185);
                    e39=expressionList();

                    state._fsp--;
                    if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new BuiltinFunctionExpression(Expression.EBuiltinType.COALESCE, e39); 			
                    }

                    }
                    break;
                case 44 :
                    // IbmSparqlExtAstWalker.g:969:4: ^( IF e40= expression e41= expression e42= expression )
                    {
                    match(input,IF,FOLLOW_IF_in_builtInCall7197); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall7201);
                    e40=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall7205);
                    e41=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall7209);
                    e42=expression();

                    state._fsp--;
                    if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new BuiltinFunctionExpression(Expression.EBuiltinType.IF, e40, e41, e42); 			
                    }

                    }
                    break;
                case 45 :
                    // IbmSparqlExtAstWalker.g:971:4: ^( STRLANG e45= expression e46= expression )
                    {
                    match(input,STRLANG,FOLLOW_STRLANG_in_builtInCall7221); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall7225);
                    e45=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall7229);
                    e46=expression();

                    state._fsp--;
                    if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new BuiltinFunctionExpression(Expression.EBuiltinType.STRLANG, e45, e46); 			
                    }

                    }
                    break;
                case 46 :
                    // IbmSparqlExtAstWalker.g:973:4: ^( STRDT e47= expression e48= expression )
                    {
                    match(input,STRDT,FOLLOW_STRDT_in_builtInCall7241); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall7245);
                    e47=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall7249);
                    e48=expression();

                    state._fsp--;
                    if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new BuiltinFunctionExpression(Expression.EBuiltinType.STRDT, e47, e48); 			
                    }

                    }
                    break;
                case 47 :
                    // IbmSparqlExtAstWalker.g:975:4: ^( SAMETERM sam1= expression sam2= expression )
                    {
                    match(input,SAMETERM,FOLLOW_SAMETERM_in_builtInCall7261); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall7265);
                    sam1=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall7269);
                    sam2=expression();

                    state._fsp--;
                    if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new BuiltinFunctionExpression(Expression.EBuiltinType.SAMETERM, sam1, sam2); 			
                    }

                    }
                    break;
                case 48 :
                    // IbmSparqlExtAstWalker.g:977:4: ^( ISIRI isi= expression )
                    {
                    match(input,ISIRI,FOLLOW_ISIRI_in_builtInCall7281); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall7285);
                    isi=expression();

                    state._fsp--;
                    if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       args.add(isi); e = new BuiltinFunctionExpression(Expression.EBuiltinType.ISIRI, args); 		
                    }

                    }
                    break;
                case 49 :
                    // IbmSparqlExtAstWalker.g:979:4: ^( ISURI isu= expression )
                    {
                    match(input,ISURI,FOLLOW_ISURI_in_builtInCall7297); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall7301);
                    isu=expression();

                    state._fsp--;
                    if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       args.add(isu); e = new BuiltinFunctionExpression(Expression.EBuiltinType.ISIRI, args); 		
                    }

                    }
                    break;
                case 50 :
                    // IbmSparqlExtAstWalker.g:981:4: ^( ISBLANK isb= expression )
                    {
                    match(input,ISBLANK,FOLLOW_ISBLANK_in_builtInCall7313); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall7317);
                    isb=expression();

                    state._fsp--;
                    if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       args.add(isb); e = new BuiltinFunctionExpression(Expression.EBuiltinType.ISBLANK, args); 	
                    }

                    }
                    break;
                case 51 :
                    // IbmSparqlExtAstWalker.g:983:4: ^( ISLITERAL isl= expression )
                    {
                    match(input,ISLITERAL,FOLLOW_ISLITERAL_in_builtInCall7329); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall7333);
                    isl=expression();

                    state._fsp--;
                    if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       args.add(isl); e = new BuiltinFunctionExpression(Expression.EBuiltinType.ISLITERAL, args); 	
                    }

                    }
                    break;
                case 52 :
                    // IbmSparqlExtAstWalker.g:985:4: ^( ISNUMERIC e55= expression )
                    {
                    match(input,ISNUMERIC,FOLLOW_ISNUMERIC_in_builtInCall7345); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall7349);
                    e55=expression();

                    state._fsp--;
                    if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       args.add(e55); e = new BuiltinFunctionExpression(Expression.EBuiltinType.ISNUMERIC, args); 	
                    }

                    }
                    break;
                case 53 :
                    // IbmSparqlExtAstWalker.g:987:4: r= regexExpression
                    {
                    pushFollow(FOLLOW_regexExpression_in_builtInCall7362);
                    r=regexExpression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = r; 
                    }

                    }
                    break;
                case 54 :
                    // IbmSparqlExtAstWalker.g:988:4: p= existsFunc
                    {
                    pushFollow(FOLLOW_existsFunc_in_builtInCall7371);
                    p=existsFunc();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new BuiltinFunctionExpression(Expression.EBuiltinType.EXISTS, p); 	
                    }

                    }
                    break;
                case 55 :
                    // IbmSparqlExtAstWalker.g:989:4: p= notExistsFunc
                    {
                    pushFollow(FOLLOW_notExistsFunc_in_builtInCall7380);
                    p=notExistsFunc();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new BuiltinFunctionExpression(Expression.EBuiltinType.NOT_EXISTS, p); 	
                    }

                    }
                    break;

            }
        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return e;
    }
    // $ANTLR end "builtInCall"


    // $ANTLR start "regexExpression"
    // IbmSparqlExtAstWalker.g:992:1: regexExpression returns [Expression e] : ^( REGEX e1= expression e2= expression (e3= expression )? ) ;
    public final Expression regexExpression() throws RecognitionException {
        Expression e = null;

        Expression e1 = null;

        Expression e2 = null;

        Expression e3 = null;


        try {
            // IbmSparqlExtAstWalker.g:993:2: ( ^( REGEX e1= expression e2= expression (e3= expression )? ) )
            // IbmSparqlExtAstWalker.g:993:6: ^( REGEX e1= expression e2= expression (e3= expression )? )
            {
            match(input,REGEX,FOLLOW_REGEX_in_regexExpression7403); if (state.failed) return e;

            match(input, Token.DOWN, null); if (state.failed) return e;
            pushFollow(FOLLOW_expression_in_regexExpression7407);
            e1=expression();

            state._fsp--;
            if (state.failed) return e;
            pushFollow(FOLLOW_expression_in_regexExpression7411);
            e2=expression();

            state._fsp--;
            if (state.failed) return e;
            // IbmSparqlExtAstWalker.g:993:44: (e3= expression )?
            int alt108=2;
            int LA108_0 = input.LA(1);

            if ( ((LA108_0>=BROKEN_PLUS && LA108_0<=BROKEN_MINUS)||(LA108_0>=VAR && LA108_0<=NOT_EXISTS)||(LA108_0>=STRING && LA108_0<=BOOLEAN)||LA108_0==LTE||(LA108_0>=BIG_INTEGER && LA108_0<=BIG_DECIMAL)||(LA108_0>=LOGICAL_OR && LA108_0<=SHA1)||(LA108_0>=SHA256 && LA108_0<=GROUP_CONCAT)||LA108_0==DOUBLE||LA108_0==256||LA108_0==259||(LA108_0>=262 && LA108_0<=268)) ) {
                alt108=1;
            }
            switch (alt108) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:993:44: e3= expression
                    {
                    pushFollow(FOLLOW_expression_in_regexExpression7415);
                    e3=expression();

                    state._fsp--;
                    if (state.failed) return e;

                    }
                    break;

            }


            match(input, Token.UP, null); if (state.failed) return e;
            if ( state.backtracking==0 ) {
               e = new BuiltinFunctionExpression(Expression.EBuiltinType.REGEX, e1, e2, e3); 
            }

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return e;
    }
    // $ANTLR end "regexExpression"


    // $ANTLR start "existsFunc"
    // IbmSparqlExtAstWalker.g:997:1: existsFunc returns [Pattern p] : ^( EXISTS g= groupGraphPattern[false] ) ;
    public final Pattern existsFunc() throws RecognitionException {
        Pattern p = null;

        Pattern g = null;


        try {
            // IbmSparqlExtAstWalker.g:998:2: ( ^( EXISTS g= groupGraphPattern[false] ) )
            // IbmSparqlExtAstWalker.g:998:6: ^( EXISTS g= groupGraphPattern[false] )
            {
            match(input,EXISTS,FOLLOW_EXISTS_in_existsFunc7447); if (state.failed) return p;

            match(input, Token.DOWN, null); if (state.failed) return p;
            pushFollow(FOLLOW_groupGraphPattern_in_existsFunc7451);
            g=groupGraphPattern(false);

            state._fsp--;
            if (state.failed) return p;

            match(input, Token.UP, null); if (state.failed) return p;
            if ( state.backtracking==0 ) {
               p = g; 
            }

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return p;
    }
    // $ANTLR end "existsFunc"


    // $ANTLR start "notExistsFunc"
    // IbmSparqlExtAstWalker.g:1002:1: notExistsFunc returns [Pattern p] : ^( NOT_EXISTS g= groupGraphPattern[false] ) ;
    public final Pattern notExistsFunc() throws RecognitionException {
        Pattern p = null;

        Pattern g = null;


        try {
            // IbmSparqlExtAstWalker.g:1003:2: ( ^( NOT_EXISTS g= groupGraphPattern[false] ) )
            // IbmSparqlExtAstWalker.g:1003:6: ^( NOT_EXISTS g= groupGraphPattern[false] )
            {
            match(input,NOT_EXISTS,FOLLOW_NOT_EXISTS_in_notExistsFunc7482); if (state.failed) return p;

            match(input, Token.DOWN, null); if (state.failed) return p;
            pushFollow(FOLLOW_groupGraphPattern_in_notExistsFunc7486);
            g=groupGraphPattern(false);

            state._fsp--;
            if (state.failed) return p;

            match(input, Token.UP, null); if (state.failed) return p;
            if ( state.backtracking==0 ) {
               p = g; 
            }

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return p;
    }
    // $ANTLR end "notExistsFunc"


    // $ANTLR start "aggregate"
    // IbmSparqlExtAstWalker.g:1007:1: aggregate returns [AggregateExpression a] : ( ^( COUNT ( DISTINCT )? (e1= expression | '*' ) ) | ^( SUM ( DISTINCT )? e2= expression ) | ^( MIN ( DISTINCT )? e3= expression ) | ^( MAX ( DISTINCT )? e4= expression ) | ^( AVG ( DISTINCT )? e5= expression ) | ^( SAMPLE ( DISTINCT )? e6= expression ) | ^( GROUP_CONCAT ( DISTINCT )? e7= expression ( ^( SEPARATOR s= string ) )? ) );
    public final AggregateExpression aggregate() throws RecognitionException {
        AggregateExpression a = null;

        Expression e1 = null;

        Expression e2 = null;

        Expression e3 = null;

        Expression e4 = null;

        Expression e5 = null;

        Expression e6 = null;

        Expression e7 = null;

        String s = null;


         
        		a = new AggregateExpression(); 
        	
        try {
            // IbmSparqlExtAstWalker.g:1011:2: ( ^( COUNT ( DISTINCT )? (e1= expression | '*' ) ) | ^( SUM ( DISTINCT )? e2= expression ) | ^( MIN ( DISTINCT )? e3= expression ) | ^( MAX ( DISTINCT )? e4= expression ) | ^( AVG ( DISTINCT )? e5= expression ) | ^( SAMPLE ( DISTINCT )? e6= expression ) | ^( GROUP_CONCAT ( DISTINCT )? e7= expression ( ^( SEPARATOR s= string ) )? ) )
            int alt118=7;
            switch ( input.LA(1) ) {
            case COUNT:
                {
                alt118=1;
                }
                break;
            case SUM:
                {
                alt118=2;
                }
                break;
            case MIN:
                {
                alt118=3;
                }
                break;
            case MAX:
                {
                alt118=4;
                }
                break;
            case AVG:
                {
                alt118=5;
                }
                break;
            case SAMPLE:
                {
                alt118=6;
                }
                break;
            case GROUP_CONCAT:
                {
                alt118=7;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return a;}
                NoViableAltException nvae =
                    new NoViableAltException("", 118, 0, input);

                throw nvae;
            }

            switch (alt118) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:1012:3: ^( COUNT ( DISTINCT )? (e1= expression | '*' ) )
                    {
                    match(input,COUNT,FOLLOW_COUNT_in_aggregate7521); if (state.failed) return a;

                    if ( state.backtracking==0 ) {
                       a.setAggregationType(AggregateExpression.EType.COUNT);  	
                    }

                    match(input, Token.DOWN, null); if (state.failed) return a;
                    // IbmSparqlExtAstWalker.g:1013:4: ( DISTINCT )?
                    int alt109=2;
                    int LA109_0 = input.LA(1);

                    if ( (LA109_0==DISTINCT) ) {
                        alt109=1;
                    }
                    switch (alt109) {
                        case 1 :
                            // IbmSparqlExtAstWalker.g:1013:5: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregate7533); if (state.failed) return a;
                            if ( state.backtracking==0 ) {
                               a.isDistinct(true);	
                            }

                            }
                            break;

                    }

                    // IbmSparqlExtAstWalker.g:1014:4: (e1= expression | '*' )
                    int alt110=2;
                    int LA110_0 = input.LA(1);

                    if ( ((LA110_0>=BROKEN_PLUS && LA110_0<=BROKEN_MINUS)||(LA110_0>=VAR && LA110_0<=NOT_EXISTS)||(LA110_0>=STRING && LA110_0<=BOOLEAN)||LA110_0==LTE||(LA110_0>=BIG_INTEGER && LA110_0<=BIG_DECIMAL)||(LA110_0>=LOGICAL_OR && LA110_0<=SHA1)||(LA110_0>=SHA256 && LA110_0<=GROUP_CONCAT)||LA110_0==DOUBLE||LA110_0==259||(LA110_0>=262 && LA110_0<=268)) ) {
                        alt110=1;
                    }
                    else if ( (LA110_0==256) ) {
                        int LA110_2 = input.LA(2);

                        if ( (LA110_2==DOWN) ) {
                            alt110=1;
                        }
                        else if ( (LA110_2==UP) ) {
                            alt110=2;
                        }
                        else {
                            if (state.backtracking>0) {state.failed=true; return a;}
                            NoViableAltException nvae =
                                new NoViableAltException("", 110, 2, input);

                            throw nvae;
                        }
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return a;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 110, 0, input);

                        throw nvae;
                    }
                    switch (alt110) {
                        case 1 :
                            // IbmSparqlExtAstWalker.g:1014:6: e1= expression
                            {
                            pushFollow(FOLLOW_expression_in_aggregate7590);
                            e1=expression();

                            state._fsp--;
                            if (state.failed) return a;
                            if ( state.backtracking==0 ) {
                               a.setArgs(e1);		
                            }

                            }
                            break;
                        case 2 :
                            // IbmSparqlExtAstWalker.g:1015:6: '*'
                            {
                            match(input,256,FOLLOW_256_in_aggregate7600); if (state.failed) return a;

                            }
                            break;

                    }


                    match(input, Token.UP, null); if (state.failed) return a;

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:1018:5: ^( SUM ( DISTINCT )? e2= expression )
                    {
                    match(input,SUM,FOLLOW_SUM_in_aggregate7620); if (state.failed) return a;

                    if ( state.backtracking==0 ) {
                       a.setAggregationType(AggregateExpression.EType.SUM);  	
                    }

                    match(input, Token.DOWN, null); if (state.failed) return a;
                    // IbmSparqlExtAstWalker.g:1019:4: ( DISTINCT )?
                    int alt111=2;
                    int LA111_0 = input.LA(1);

                    if ( (LA111_0==DISTINCT) ) {
                        alt111=1;
                    }
                    switch (alt111) {
                        case 1 :
                            // IbmSparqlExtAstWalker.g:1019:5: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregate7632); if (state.failed) return a;
                            if ( state.backtracking==0 ) {
                               a.isDistinct(true);	
                            }

                            }
                            break;

                    }

                    pushFollow(FOLLOW_expression_in_aggregate7649);
                    e2=expression();

                    state._fsp--;
                    if (state.failed) return a;
                    if ( state.backtracking==0 ) {
                       a.setArgs(e2);		
                    }

                    match(input, Token.UP, null); if (state.failed) return a;

                    }
                    break;
                case 3 :
                    // IbmSparqlExtAstWalker.g:1022:4: ^( MIN ( DISTINCT )? e3= expression )
                    {
                    match(input,MIN,FOLLOW_MIN_in_aggregate7662); if (state.failed) return a;

                    if ( state.backtracking==0 ) {
                       a.setAggregationType(AggregateExpression.EType.MIN);  	
                    }

                    match(input, Token.DOWN, null); if (state.failed) return a;
                    // IbmSparqlExtAstWalker.g:1023:4: ( DISTINCT )?
                    int alt112=2;
                    int LA112_0 = input.LA(1);

                    if ( (LA112_0==DISTINCT) ) {
                        alt112=1;
                    }
                    switch (alt112) {
                        case 1 :
                            // IbmSparqlExtAstWalker.g:1023:5: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregate7675); if (state.failed) return a;
                            if ( state.backtracking==0 ) {
                               a.isDistinct(true);	
                            }

                            }
                            break;

                    }

                    pushFollow(FOLLOW_expression_in_aggregate7691);
                    e3=expression();

                    state._fsp--;
                    if (state.failed) return a;
                    if ( state.backtracking==0 ) {
                       a.setArgs(e3);		
                    }

                    match(input, Token.UP, null); if (state.failed) return a;

                    }
                    break;
                case 4 :
                    // IbmSparqlExtAstWalker.g:1026:4: ^( MAX ( DISTINCT )? e4= expression )
                    {
                    match(input,MAX,FOLLOW_MAX_in_aggregate7704); if (state.failed) return a;

                    if ( state.backtracking==0 ) {
                       a.setAggregationType(AggregateExpression.EType.MAX);  	
                    }

                    match(input, Token.DOWN, null); if (state.failed) return a;
                    // IbmSparqlExtAstWalker.g:1027:4: ( DISTINCT )?
                    int alt113=2;
                    int LA113_0 = input.LA(1);

                    if ( (LA113_0==DISTINCT) ) {
                        alt113=1;
                    }
                    switch (alt113) {
                        case 1 :
                            // IbmSparqlExtAstWalker.g:1027:5: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregate7717); if (state.failed) return a;
                            if ( state.backtracking==0 ) {
                               a.isDistinct(true);	
                            }

                            }
                            break;

                    }

                    pushFollow(FOLLOW_expression_in_aggregate7734);
                    e4=expression();

                    state._fsp--;
                    if (state.failed) return a;
                    if ( state.backtracking==0 ) {
                       a.setArgs(e4);		
                    }

                    match(input, Token.UP, null); if (state.failed) return a;

                    }
                    break;
                case 5 :
                    // IbmSparqlExtAstWalker.g:1030:4: ^( AVG ( DISTINCT )? e5= expression )
                    {
                    match(input,AVG,FOLLOW_AVG_in_aggregate7747); if (state.failed) return a;

                    if ( state.backtracking==0 ) {
                       a.setAggregationType(AggregateExpression.EType.AVG);  	
                    }

                    match(input, Token.DOWN, null); if (state.failed) return a;
                    // IbmSparqlExtAstWalker.g:1031:4: ( DISTINCT )?
                    int alt114=2;
                    int LA114_0 = input.LA(1);

                    if ( (LA114_0==DISTINCT) ) {
                        alt114=1;
                    }
                    switch (alt114) {
                        case 1 :
                            // IbmSparqlExtAstWalker.g:1031:5: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregate7760); if (state.failed) return a;
                            if ( state.backtracking==0 ) {
                               a.isDistinct(true);	
                            }

                            }
                            break;

                    }

                    pushFollow(FOLLOW_expression_in_aggregate7777);
                    e5=expression();

                    state._fsp--;
                    if (state.failed) return a;
                    if ( state.backtracking==0 ) {
                       a.setArgs(e5);		
                    }

                    match(input, Token.UP, null); if (state.failed) return a;

                    }
                    break;
                case 6 :
                    // IbmSparqlExtAstWalker.g:1034:4: ^( SAMPLE ( DISTINCT )? e6= expression )
                    {
                    match(input,SAMPLE,FOLLOW_SAMPLE_in_aggregate7790); if (state.failed) return a;

                    if ( state.backtracking==0 ) {
                       a.setAggregationType(AggregateExpression.EType.SAMPLE);  
                    }

                    match(input, Token.DOWN, null); if (state.failed) return a;
                    // IbmSparqlExtAstWalker.g:1035:4: ( DISTINCT )?
                    int alt115=2;
                    int LA115_0 = input.LA(1);

                    if ( (LA115_0==DISTINCT) ) {
                        alt115=1;
                    }
                    switch (alt115) {
                        case 1 :
                            // IbmSparqlExtAstWalker.g:1035:5: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregate7802); if (state.failed) return a;
                            if ( state.backtracking==0 ) {
                               a.isDistinct(true);	
                            }

                            }
                            break;

                    }

                    pushFollow(FOLLOW_expression_in_aggregate7819);
                    e6=expression();

                    state._fsp--;
                    if (state.failed) return a;
                    if ( state.backtracking==0 ) {
                       a.setArgs(e6);		
                    }

                    match(input, Token.UP, null); if (state.failed) return a;

                    }
                    break;
                case 7 :
                    // IbmSparqlExtAstWalker.g:1038:5: ^( GROUP_CONCAT ( DISTINCT )? e7= expression ( ^( SEPARATOR s= string ) )? )
                    {
                    match(input,GROUP_CONCAT,FOLLOW_GROUP_CONCAT_in_aggregate7833); if (state.failed) return a;

                    if ( state.backtracking==0 ) {
                       a.setAggregationType(AggregateExpression.EType.GROUP_CONCAT); 
                    }

                    match(input, Token.DOWN, null); if (state.failed) return a;
                    // IbmSparqlExtAstWalker.g:1039:4: ( DISTINCT )?
                    int alt116=2;
                    int LA116_0 = input.LA(1);

                    if ( (LA116_0==DISTINCT) ) {
                        alt116=1;
                    }
                    switch (alt116) {
                        case 1 :
                            // IbmSparqlExtAstWalker.g:1039:5: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregate7845); if (state.failed) return a;
                            if ( state.backtracking==0 ) {
                               a.isDistinct(true);	
                            }

                            }
                            break;

                    }

                    pushFollow(FOLLOW_expression_in_aggregate7860);
                    e7=expression();

                    state._fsp--;
                    if (state.failed) return a;
                    if ( state.backtracking==0 ) {
                       a.setArgs(e7);		
                    }
                    // IbmSparqlExtAstWalker.g:1041:4: ( ^( SEPARATOR s= string ) )?
                    int alt117=2;
                    int LA117_0 = input.LA(1);

                    if ( (LA117_0==SEPARATOR) ) {
                        alt117=1;
                    }
                    switch (alt117) {
                        case 1 :
                            // IbmSparqlExtAstWalker.g:1041:5: ^( SEPARATOR s= string )
                            {
                            match(input,SEPARATOR,FOLLOW_SEPARATOR_in_aggregate7871); if (state.failed) return a;

                            match(input, Token.DOWN, null); if (state.failed) return a;
                            pushFollow(FOLLOW_string_in_aggregate7875);
                            s=string();

                            state._fsp--;
                            if (state.failed) return a;
                            if ( state.backtracking==0 ) {
                               a.setSeparator(s);		
                            }

                            match(input, Token.UP, null); if (state.failed) return a;

                            }
                            break;

                    }


                    match(input, Token.UP, null); if (state.failed) return a;

                    }
                    break;

            }
        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return a;
    }
    // $ANTLR end "aggregate"


    // $ANTLR start "iRIFunction"
    // IbmSparqlExtAstWalker.g:1045:1: iRIFunction returns [FunctionCall f] : ^( FUNCTION i= iRIref (a= argList )? ) ;
    public final FunctionCall iRIFunction() throws RecognitionException {
        FunctionCall f = null;

        IRI i = null;

        List<Expression> a = null;


        try {
            // IbmSparqlExtAstWalker.g:1046:2: ( ^( FUNCTION i= iRIref (a= argList )? ) )
            // IbmSparqlExtAstWalker.g:1046:6: ^( FUNCTION i= iRIref (a= argList )? )
            {
            match(input,FUNCTION,FOLLOW_FUNCTION_in_iRIFunction7904); if (state.failed) return f;

            match(input, Token.DOWN, null); if (state.failed) return f;
            pushFollow(FOLLOW_iRIref_in_iRIFunction7914);
            i=iRIref();

            state._fsp--;
            if (state.failed) return f;
            if ( state.backtracking==0 ) {
               f = new FunctionCall(i); 
            }
            // IbmSparqlExtAstWalker.g:1048:5: (a= argList )?
            int alt119=2;
            int LA119_0 = input.LA(1);

            if ( ((LA119_0>=BROKEN_PLUS && LA119_0<=NIL)||(LA119_0>=VAR && LA119_0<=NOT_EXISTS)||(LA119_0>=STRING && LA119_0<=BOOLEAN)||LA119_0==LTE||(LA119_0>=BIG_INTEGER && LA119_0<=BIG_DECIMAL)||LA119_0==DISTINCT||(LA119_0>=LOGICAL_OR && LA119_0<=SHA1)||(LA119_0>=SHA256 && LA119_0<=GROUP_CONCAT)||LA119_0==DOUBLE||LA119_0==256||LA119_0==259||(LA119_0>=262 && LA119_0<=268)) ) {
                alt119=1;
            }
            switch (alt119) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:1048:6: a= argList
                    {
                    pushFollow(FOLLOW_argList_in_iRIFunction7926);
                    a=argList();

                    state._fsp--;
                    if (state.failed) return f;
                    if ( state.backtracking==0 ) {
                       f.addArguments(a); 		
                    }

                    }
                    break;

            }


            match(input, Token.UP, null); if (state.failed) return f;

            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return f;
    }
    // $ANTLR end "iRIFunction"


    // $ANTLR start "rDFLiteral"
    // IbmSparqlExtAstWalker.g:1052:1: rDFLiteral returns [StringLiteral sl] : s= string (l= LANGTAG | ( '^^' i= iRIref ) )? ;
    public final StringLiteral rDFLiteral() throws RecognitionException {
        StringLiteral sl = null;

        XTree l=null;
        String s = null;

        IRI i = null;


        try {
            // IbmSparqlExtAstWalker.g:1053:2: (s= string (l= LANGTAG | ( '^^' i= iRIref ) )? )
            // IbmSparqlExtAstWalker.g:1053:6: s= string (l= LANGTAG | ( '^^' i= iRIref ) )?
            {
            pushFollow(FOLLOW_string_in_rDFLiteral7958);
            s=string();

            state._fsp--;
            if (state.failed) return sl;
            if ( state.backtracking==0 ) {
               sl = new StringLiteral(s);     
            }
            // IbmSparqlExtAstWalker.g:1054:3: (l= LANGTAG | ( '^^' i= iRIref ) )?
            int alt120=3;
            int LA120_0 = input.LA(1);

            if ( (LA120_0==LANGTAG) ) {
                alt120=1;
            }
            else if ( (LA120_0==269) ) {
                alt120=2;
            }
            switch (alt120) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:1054:5: l= LANGTAG
                    {
                    l=(XTree)match(input,LANGTAG,FOLLOW_LANGTAG_in_rDFLiteral7973); if (state.failed) return sl;
                    if ( state.backtracking==0 ) {
                       sl.setLanguage((l!=null?l.getText():null));        
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:1055:5: ( '^^' i= iRIref )
                    {
                    // IbmSparqlExtAstWalker.g:1055:5: ( '^^' i= iRIref )
                    // IbmSparqlExtAstWalker.g:1055:7: '^^' i= iRIref
                    {
                    match(input,269,FOLLOW_269_in_rDFLiteral7987); if (state.failed) return sl;
                    pushFollow(FOLLOW_iRIref_in_rDFLiteral7991);
                    i=iRIref();

                    state._fsp--;
                    if (state.failed) return sl;

                    }

                    if ( state.backtracking==0 ) {
                       sl.setType(i);       
                    }

                    }
                    break;

            }


            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return sl;
    }
    // $ANTLR end "rDFLiteral"


    // $ANTLR start "numericLiteral"
    // IbmSparqlExtAstWalker.g:1059:1: numericLiteral returns [Constant n] : (n1= numericLiteralUnsigned | n2= numericLiteralPositive | n3= numericLiteralNegative );
    public final Constant numericLiteral() throws RecognitionException {
        Constant n = null;

        Constant n1 = null;

        Constant n2 = null;

        Constant n3 = null;


        try {
            // IbmSparqlExtAstWalker.g:1060:2: (n1= numericLiteralUnsigned | n2= numericLiteralPositive | n3= numericLiteralNegative )
            int alt121=3;
            alt121 = dfa121.predict(input);
            switch (alt121) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:1060:6: n1= numericLiteralUnsigned
                    {
                    pushFollow(FOLLOW_numericLiteralUnsigned_in_numericLiteral8022);
                    n1=numericLiteralUnsigned();

                    state._fsp--;
                    if (state.failed) return n;
                    if ( state.backtracking==0 ) {
                       n = n1; 
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:1061:5: n2= numericLiteralPositive
                    {
                    pushFollow(FOLLOW_numericLiteralPositive_in_numericLiteral8032);
                    n2=numericLiteralPositive();

                    state._fsp--;
                    if (state.failed) return n;
                    if ( state.backtracking==0 ) {
                       n = n2; 
                    }

                    }
                    break;
                case 3 :
                    // IbmSparqlExtAstWalker.g:1062:5: n3= numericLiteralNegative
                    {
                    pushFollow(FOLLOW_numericLiteralNegative_in_numericLiteral8042);
                    n3=numericLiteralNegative();

                    state._fsp--;
                    if (state.failed) return n;
                    if ( state.backtracking==0 ) {
                       n = n3; 
                    }

                    }
                    break;

            }
        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return n;
    }
    // $ANTLR end "numericLiteral"


    // $ANTLR start "numericLiteralUnsigned"
    // IbmSparqlExtAstWalker.g:1065:1: numericLiteralUnsigned returns [Constant c] : ( ^( BIG_INTEGER i= INTEGER ) | ^( BIG_DECIMAL d1= DECIMAL ) | ^( DOUBLE d2= DOUBLE ) );
    public final Constant numericLiteralUnsigned() throws RecognitionException {
        Constant c = null;

        XTree i=null;
        XTree d1=null;
        XTree d2=null;

        try {
            // IbmSparqlExtAstWalker.g:1066:2: ( ^( BIG_INTEGER i= INTEGER ) | ^( BIG_DECIMAL d1= DECIMAL ) | ^( DOUBLE d2= DOUBLE ) )
            int alt122=3;
            switch ( input.LA(1) ) {
            case BIG_INTEGER:
                {
                alt122=1;
                }
                break;
            case BIG_DECIMAL:
                {
                alt122=2;
                }
                break;
            case DOUBLE:
                {
                alt122=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return c;}
                NoViableAltException nvae =
                    new NoViableAltException("", 122, 0, input);

                throw nvae;
            }

            switch (alt122) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:1066:6: ^( BIG_INTEGER i= INTEGER )
                    {
                    match(input,BIG_INTEGER,FOLLOW_BIG_INTEGER_in_numericLiteralUnsigned8064); if (state.failed) return c;

                    match(input, Token.DOWN, null); if (state.failed) return c;
                    i=(XTree)match(input,INTEGER,FOLLOW_INTEGER_in_numericLiteralUnsigned8069); if (state.failed) return c;

                    match(input, Token.UP, null); if (state.failed) return c;
                    if ( state.backtracking==0 ) {
                       c = new Constant((i!=null?i.getText():null), new BigInteger((i!=null?i.getText():null)));		
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:1067:5: ^( BIG_DECIMAL d1= DECIMAL )
                    {
                    match(input,BIG_DECIMAL,FOLLOW_BIG_DECIMAL_in_numericLiteralUnsigned8085); if (state.failed) return c;

                    match(input, Token.DOWN, null); if (state.failed) return c;
                    d1=(XTree)match(input,DECIMAL,FOLLOW_DECIMAL_in_numericLiteralUnsigned8089); if (state.failed) return c;

                    match(input, Token.UP, null); if (state.failed) return c;
                    if ( state.backtracking==0 ) {
                       c = new Constant((d1!=null?d1.getText():null), new BigDecimal((d1!=null?d1.getText():null)));	
                    }

                    }
                    break;
                case 3 :
                    // IbmSparqlExtAstWalker.g:1068:5: ^( DOUBLE d2= DOUBLE )
                    {
                    match(input,DOUBLE,FOLLOW_DOUBLE_in_numericLiteralUnsigned8104); if (state.failed) return c;

                    match(input, Token.DOWN, null); if (state.failed) return c;
                    d2=(XTree)match(input,DOUBLE,FOLLOW_DOUBLE_in_numericLiteralUnsigned8109); if (state.failed) return c;

                    match(input, Token.UP, null); if (state.failed) return c;
                    if ( state.backtracking==0 ) {
                       c = new Constant((d2!=null?d2.getText():null), new Double((d2!=null?d2.getText():null)));		
                    }

                    }
                    break;

            }
        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return c;
    }
    // $ANTLR end "numericLiteralUnsigned"


    // $ANTLR start "numericLiteralPositive"
    // IbmSparqlExtAstWalker.g:1071:1: numericLiteralPositive returns [Constant c] : ( ^( BIG_INTEGER i= INTEGER_POSITIVE ) | ^( BIG_DECIMAL d1= DECIMAL_POSITIVE ) | ^( DOUBLE d2= DOUBLE_POSITIVE ) );
    public final Constant numericLiteralPositive() throws RecognitionException {
        Constant c = null;

        XTree i=null;
        XTree d1=null;
        XTree d2=null;

        try {
            // IbmSparqlExtAstWalker.g:1072:2: ( ^( BIG_INTEGER i= INTEGER_POSITIVE ) | ^( BIG_DECIMAL d1= DECIMAL_POSITIVE ) | ^( DOUBLE d2= DOUBLE_POSITIVE ) )
            int alt123=3;
            switch ( input.LA(1) ) {
            case BIG_INTEGER:
                {
                alt123=1;
                }
                break;
            case BIG_DECIMAL:
                {
                alt123=2;
                }
                break;
            case DOUBLE:
                {
                alt123=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return c;}
                NoViableAltException nvae =
                    new NoViableAltException("", 123, 0, input);

                throw nvae;
            }

            switch (alt123) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:1072:6: ^( BIG_INTEGER i= INTEGER_POSITIVE )
                    {
                    match(input,BIG_INTEGER,FOLLOW_BIG_INTEGER_in_numericLiteralPositive8139); if (state.failed) return c;

                    match(input, Token.DOWN, null); if (state.failed) return c;
                    i=(XTree)match(input,INTEGER_POSITIVE,FOLLOW_INTEGER_POSITIVE_in_numericLiteralPositive8144); if (state.failed) return c;

                    match(input, Token.UP, null); if (state.failed) return c;
                    if ( state.backtracking==0 ) {
                       c = new Constant((i!=null?i.getText():null), new BigInteger((i!=null?i.getText():null).substring(1)));		
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:1073:5: ^( BIG_DECIMAL d1= DECIMAL_POSITIVE )
                    {
                    match(input,BIG_DECIMAL,FOLLOW_BIG_DECIMAL_in_numericLiteralPositive8157); if (state.failed) return c;

                    match(input, Token.DOWN, null); if (state.failed) return c;
                    d1=(XTree)match(input,DECIMAL_POSITIVE,FOLLOW_DECIMAL_POSITIVE_in_numericLiteralPositive8161); if (state.failed) return c;

                    match(input, Token.UP, null); if (state.failed) return c;
                    if ( state.backtracking==0 ) {
                       c = new Constant((d1!=null?d1.getText():null), new BigDecimal((d1!=null?d1.getText():null).substring(1)));	
                    }

                    }
                    break;
                case 3 :
                    // IbmSparqlExtAstWalker.g:1074:5: ^( DOUBLE d2= DOUBLE_POSITIVE )
                    {
                    match(input,DOUBLE,FOLLOW_DOUBLE_in_numericLiteralPositive8174); if (state.failed) return c;

                    match(input, Token.DOWN, null); if (state.failed) return c;
                    d2=(XTree)match(input,DOUBLE_POSITIVE,FOLLOW_DOUBLE_POSITIVE_in_numericLiteralPositive8179); if (state.failed) return c;

                    match(input, Token.UP, null); if (state.failed) return c;
                    if ( state.backtracking==0 ) {
                       c = new Constant((d2!=null?d2.getText():null), new Double((d2!=null?d2.getText():null).substring(1)));		
                    }

                    }
                    break;

            }
        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return c;
    }
    // $ANTLR end "numericLiteralPositive"


    // $ANTLR start "numericLiteralNegative"
    // IbmSparqlExtAstWalker.g:1077:1: numericLiteralNegative returns [Constant c] : ( ^( BIG_INTEGER i= INTEGER_NEGATIVE ) | ^( BIG_DECIMAL d1= DECIMAL_NEGATIVE ) | ^( DOUBLE d2= DOUBLE_NEGATIVE ) );
    public final Constant numericLiteralNegative() throws RecognitionException {
        Constant c = null;

        XTree i=null;
        XTree d1=null;
        XTree d2=null;

        try {
            // IbmSparqlExtAstWalker.g:1078:2: ( ^( BIG_INTEGER i= INTEGER_NEGATIVE ) | ^( BIG_DECIMAL d1= DECIMAL_NEGATIVE ) | ^( DOUBLE d2= DOUBLE_NEGATIVE ) )
            int alt124=3;
            switch ( input.LA(1) ) {
            case BIG_INTEGER:
                {
                alt124=1;
                }
                break;
            case BIG_DECIMAL:
                {
                alt124=2;
                }
                break;
            case DOUBLE:
                {
                alt124=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return c;}
                NoViableAltException nvae =
                    new NoViableAltException("", 124, 0, input);

                throw nvae;
            }

            switch (alt124) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:1078:6: ^( BIG_INTEGER i= INTEGER_NEGATIVE )
                    {
                    match(input,BIG_INTEGER,FOLLOW_BIG_INTEGER_in_numericLiteralNegative8204); if (state.failed) return c;

                    match(input, Token.DOWN, null); if (state.failed) return c;
                    i=(XTree)match(input,INTEGER_NEGATIVE,FOLLOW_INTEGER_NEGATIVE_in_numericLiteralNegative8209); if (state.failed) return c;

                    match(input, Token.UP, null); if (state.failed) return c;
                    if ( state.backtracking==0 ) {
                       c = new Constant((i!=null?i.getText():null), new BigInteger((i!=null?i.getText():null)));		
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:1079:5: ^( BIG_DECIMAL d1= DECIMAL_NEGATIVE )
                    {
                    match(input,BIG_DECIMAL,FOLLOW_BIG_DECIMAL_in_numericLiteralNegative8222); if (state.failed) return c;

                    match(input, Token.DOWN, null); if (state.failed) return c;
                    d1=(XTree)match(input,DECIMAL_NEGATIVE,FOLLOW_DECIMAL_NEGATIVE_in_numericLiteralNegative8226); if (state.failed) return c;

                    match(input, Token.UP, null); if (state.failed) return c;
                    if ( state.backtracking==0 ) {
                       c = new Constant((d1!=null?d1.getText():null), new BigDecimal((d1!=null?d1.getText():null)));	
                    }

                    }
                    break;
                case 3 :
                    // IbmSparqlExtAstWalker.g:1080:5: ^( DOUBLE d2= DOUBLE_NEGATIVE )
                    {
                    match(input,DOUBLE,FOLLOW_DOUBLE_in_numericLiteralNegative8239); if (state.failed) return c;

                    match(input, Token.DOWN, null); if (state.failed) return c;
                    d2=(XTree)match(input,DOUBLE_NEGATIVE,FOLLOW_DOUBLE_NEGATIVE_in_numericLiteralNegative8244); if (state.failed) return c;

                    match(input, Token.UP, null); if (state.failed) return c;
                    if ( state.backtracking==0 ) {
                       c = new Constant((d2!=null?d2.getText():null), new Double((d2!=null?d2.getText():null)));		
                    }

                    }
                    break;

            }
        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return c;
    }
    // $ANTLR end "numericLiteralNegative"


    // $ANTLR start "booleanLiteral"
    // IbmSparqlExtAstWalker.g:1083:1: booleanLiteral returns [Boolean b] : ( ^( BOOLEAN TRUE ) | ^( BOOLEAN FALSE ) );
    public final Boolean booleanLiteral() throws RecognitionException {
        Boolean b = null;

        try {
            // IbmSparqlExtAstWalker.g:1084:2: ( ^( BOOLEAN TRUE ) | ^( BOOLEAN FALSE ) )
            int alt125=2;
            int LA125_0 = input.LA(1);

            if ( (LA125_0==BOOLEAN) ) {
                int LA125_1 = input.LA(2);

                if ( (LA125_1==DOWN) ) {
                    int LA125_2 = input.LA(3);

                    if ( (LA125_2==TRUE) ) {
                        alt125=1;
                    }
                    else if ( (LA125_2==FALSE) ) {
                        alt125=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return b;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 125, 2, input);

                        throw nvae;
                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return b;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 125, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return b;}
                NoViableAltException nvae =
                    new NoViableAltException("", 125, 0, input);

                throw nvae;
            }
            switch (alt125) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:1084:6: ^( BOOLEAN TRUE )
                    {
                    match(input,BOOLEAN,FOLLOW_BOOLEAN_in_booleanLiteral8268); if (state.failed) return b;

                    match(input, Token.DOWN, null); if (state.failed) return b;
                    match(input,TRUE,FOLLOW_TRUE_in_booleanLiteral8270); if (state.failed) return b;

                    match(input, Token.UP, null); if (state.failed) return b;
                    if ( state.backtracking==0 ) {
                       b = Boolean.TRUE;  
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:1085:6: ^( BOOLEAN FALSE )
                    {
                    match(input,BOOLEAN,FOLLOW_BOOLEAN_in_booleanLiteral8285); if (state.failed) return b;

                    match(input, Token.DOWN, null); if (state.failed) return b;
                    match(input,FALSE,FOLLOW_FALSE_in_booleanLiteral8287); if (state.failed) return b;

                    match(input, Token.UP, null); if (state.failed) return b;
                    if ( state.backtracking==0 ) {
                       b = Boolean.FALSE; 
                    }

                    }
                    break;

            }
        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return b;
    }
    // $ANTLR end "booleanLiteral"


    // $ANTLR start "string"
    // IbmSparqlExtAstWalker.g:1088:1: string returns [String s] : ( ^( STRING s1= STRING_LITERAL1 ) | ^( STRING s2= STRING_LITERAL2 ) | ^( STRING s3= STRING_LITERAL_LONG1 ) | ^( STRING s4= STRING_LITERAL_LONG2 ) );
    public final String string() throws RecognitionException {
        String s = null;

        XTree s1=null;
        XTree s2=null;
        XTree s3=null;
        XTree s4=null;

        try {
            // IbmSparqlExtAstWalker.g:1089:2: ( ^( STRING s1= STRING_LITERAL1 ) | ^( STRING s2= STRING_LITERAL2 ) | ^( STRING s3= STRING_LITERAL_LONG1 ) | ^( STRING s4= STRING_LITERAL_LONG2 ) )
            int alt126=4;
            int LA126_0 = input.LA(1);

            if ( (LA126_0==STRING) ) {
                int LA126_1 = input.LA(2);

                if ( (LA126_1==DOWN) ) {
                    switch ( input.LA(3) ) {
                    case STRING_LITERAL1:
                        {
                        alt126=1;
                        }
                        break;
                    case STRING_LITERAL2:
                        {
                        alt126=2;
                        }
                        break;
                    case STRING_LITERAL_LONG1:
                        {
                        alt126=3;
                        }
                        break;
                    case STRING_LITERAL_LONG2:
                        {
                        alt126=4;
                        }
                        break;
                    default:
                        if (state.backtracking>0) {state.failed=true; return s;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 126, 2, input);

                        throw nvae;
                    }

                }
                else {
                    if (state.backtracking>0) {state.failed=true; return s;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 126, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return s;}
                NoViableAltException nvae =
                    new NoViableAltException("", 126, 0, input);

                throw nvae;
            }
            switch (alt126) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:1089:4: ^( STRING s1= STRING_LITERAL1 )
                    {
                    match(input,STRING,FOLLOW_STRING_in_string8310); if (state.failed) return s;

                    match(input, Token.DOWN, null); if (state.failed) return s;
                    s1=(XTree)match(input,STRING_LITERAL1,FOLLOW_STRING_LITERAL1_in_string8314); if (state.failed) return s;

                    match(input, Token.UP, null); if (state.failed) return s;
                    if ( state.backtracking==0 ) {
                       s = new String((s1!=null?s1.getText():null)); 
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:1090:4: ^( STRING s2= STRING_LITERAL2 )
                    {
                    match(input,STRING,FOLLOW_STRING_in_string8331); if (state.failed) return s;

                    match(input, Token.DOWN, null); if (state.failed) return s;
                    s2=(XTree)match(input,STRING_LITERAL2,FOLLOW_STRING_LITERAL2_in_string8335); if (state.failed) return s;

                    match(input, Token.UP, null); if (state.failed) return s;
                    if ( state.backtracking==0 ) {
                       s = new String((s2!=null?s2.getText():null)); 
                    }

                    }
                    break;
                case 3 :
                    // IbmSparqlExtAstWalker.g:1091:4: ^( STRING s3= STRING_LITERAL_LONG1 )
                    {
                    match(input,STRING,FOLLOW_STRING_in_string8352); if (state.failed) return s;

                    match(input, Token.DOWN, null); if (state.failed) return s;
                    s3=(XTree)match(input,STRING_LITERAL_LONG1,FOLLOW_STRING_LITERAL_LONG1_in_string8356); if (state.failed) return s;

                    match(input, Token.UP, null); if (state.failed) return s;
                    if ( state.backtracking==0 ) {
                       s = new String((s3!=null?s3.getText():null)); 
                    }

                    }
                    break;
                case 4 :
                    // IbmSparqlExtAstWalker.g:1092:4: ^( STRING s4= STRING_LITERAL_LONG2 )
                    {
                    match(input,STRING,FOLLOW_STRING_in_string8368); if (state.failed) return s;

                    match(input, Token.DOWN, null); if (state.failed) return s;
                    s4=(XTree)match(input,STRING_LITERAL_LONG2,FOLLOW_STRING_LITERAL_LONG2_in_string8372); if (state.failed) return s;

                    match(input, Token.UP, null); if (state.failed) return s;
                    if ( state.backtracking==0 ) {
                       s = new String((s4!=null?s4.getText():null)); 
                    }

                    }
                    break;

            }
        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return s;
    }
    // $ANTLR end "string"


    // $ANTLR start "iRIref"
    // IbmSparqlExtAstWalker.g:1095:1: iRIref returns [IRI r] : ( ^( IRI i= IRI_REF ) | p= prefixedName );
    public final IRI iRIref() throws RecognitionException {
        IRI r = null;

        XTree i=null;
        String p = null;


        try {
            // IbmSparqlExtAstWalker.g:1096:2: ( ^( IRI i= IRI_REF ) | p= prefixedName )
            int alt127=2;
            int LA127_0 = input.LA(1);

            if ( (LA127_0==IRI) ) {
                alt127=1;
            }
            else if ( ((LA127_0>=PREFIXED_NAME && LA127_0<=PREFIXED_NS)) ) {
                alt127=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return r;}
                NoViableAltException nvae =
                    new NoViableAltException("", 127, 0, input);

                throw nvae;
            }
            switch (alt127) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:1096:4: ^( IRI i= IRI_REF )
                    {
                    match(input,IRI,FOLLOW_IRI_in_iRIref8396); if (state.failed) return r;

                    match(input, Token.DOWN, null); if (state.failed) return r;
                    i=(XTree)match(input,IRI_REF,FOLLOW_IRI_REF_in_iRIref8400); if (state.failed) return r;

                    match(input, Token.UP, null); if (state.failed) return r;
                    if ( state.backtracking==0 ) {
                       r = new IRI((i!=null?i.getText():null).substring(1, (i!=null?i.getText():null).length()-1)); 	
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:1097:6: p= prefixedName
                    {
                    pushFollow(FOLLOW_prefixedName_in_iRIref8412);
                    p=prefixedName();

                    state._fsp--;
                    if (state.failed) return r;
                    if ( state.backtracking==0 ) {
                       r = new IRI(p); 				
                    }

                    }
                    break;

            }
        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return r;
    }
    // $ANTLR end "iRIref"


    // $ANTLR start "prefixedName"
    // IbmSparqlExtAstWalker.g:1100:1: prefixedName returns [String s] : ( ^( PREFIXED_NAME n1= PNAME_LN ) | ^( PREFIXED_NS n2= PNAME_NS ) );
    public final String prefixedName() throws RecognitionException {
        String s = null;

        XTree n1=null;
        XTree n2=null;

        try {
            // IbmSparqlExtAstWalker.g:1101:2: ( ^( PREFIXED_NAME n1= PNAME_LN ) | ^( PREFIXED_NS n2= PNAME_NS ) )
            int alt128=2;
            int LA128_0 = input.LA(1);

            if ( (LA128_0==PREFIXED_NAME) ) {
                alt128=1;
            }
            else if ( (LA128_0==PREFIXED_NS) ) {
                alt128=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return s;}
                NoViableAltException nvae =
                    new NoViableAltException("", 128, 0, input);

                throw nvae;
            }
            switch (alt128) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:1101:4: ^( PREFIXED_NAME n1= PNAME_LN )
                    {
                    match(input,PREFIXED_NAME,FOLLOW_PREFIXED_NAME_in_prefixedName8434); if (state.failed) return s;

                    match(input, Token.DOWN, null); if (state.failed) return s;
                    n1=(XTree)match(input,PNAME_LN,FOLLOW_PNAME_LN_in_prefixedName8438); if (state.failed) return s;

                    match(input, Token.UP, null); if (state.failed) return s;
                    if ( state.backtracking==0 ) {
                       s = new String(n1.getText()); 
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:1102:4: ^( PREFIXED_NS n2= PNAME_NS )
                    {
                    match(input,PREFIXED_NS,FOLLOW_PREFIXED_NS_in_prefixedName8447); if (state.failed) return s;

                    match(input, Token.DOWN, null); if (state.failed) return s;
                    n2=(XTree)match(input,PNAME_NS,FOLLOW_PNAME_NS_in_prefixedName8451); if (state.failed) return s;

                    match(input, Token.UP, null); if (state.failed) return s;
                    if ( state.backtracking==0 ) {
                       s = new String(n2.getText()); 
                    }

                    }
                    break;

            }
        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return s;
    }
    // $ANTLR end "prefixedName"


    // $ANTLR start "blankNode"
    // IbmSparqlExtAstWalker.g:1105:1: blankNode returns [BlankNode bn] : (b= BLANK_NODE_LABEL | ^( ANNON t= OPEN_SQ_BRACKET ) );
    public final BlankNode blankNode() throws RecognitionException {
        BlankNode bn = null;

        XTree b=null;
        XTree t=null;

        try {
            // IbmSparqlExtAstWalker.g:1106:2: (b= BLANK_NODE_LABEL | ^( ANNON t= OPEN_SQ_BRACKET ) )
            int alt129=2;
            int LA129_0 = input.LA(1);

            if ( (LA129_0==BLANK_NODE_LABEL) ) {
                alt129=1;
            }
            else if ( (LA129_0==ANNON) ) {
                alt129=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return bn;}
                NoViableAltException nvae =
                    new NoViableAltException("", 129, 0, input);

                throw nvae;
            }
            switch (alt129) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:1106:6: b= BLANK_NODE_LABEL
                    {
                    b=(XTree)match(input,BLANK_NODE_LABEL,FOLLOW_BLANK_NODE_LABEL_in_blankNode8475); if (state.failed) return bn;
                    if ( state.backtracking==0 ) {
                       bn = new BlankNode(b.getText()); 
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:1107:6: ^( ANNON t= OPEN_SQ_BRACKET )
                    {
                    match(input,ANNON,FOLLOW_ANNON_in_blankNode8486); if (state.failed) return bn;

                    match(input, Token.DOWN, null); if (state.failed) return bn;
                    t=(XTree)match(input,OPEN_SQ_BRACKET,FOLLOW_OPEN_SQ_BRACKET_in_blankNode8490); if (state.failed) return bn;

                    match(input, Token.UP, null); if (state.failed) return bn;
                    if ( state.backtracking==0 ) {
                       bn = new BlankNode("b" + t.getTokenStartIndex()); 
                    }

                    }
                    break;

            }
        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return bn;
    }
    // $ANTLR end "blankNode"

    // $ANTLR start synpred1_IbmSparqlExtAstWalker
    public final void synpred1_IbmSparqlExtAstWalker_fragment() throws RecognitionException {   
        // IbmSparqlExtAstWalker.g:495:25: ( NIL )
        // IbmSparqlExtAstWalker.g:495:27: NIL
        {
        match(input,NIL,FOLLOW_NIL_in_synpred1_IbmSparqlExtAstWalker3290); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred1_IbmSparqlExtAstWalker

    // $ANTLR start synpred2_IbmSparqlExtAstWalker
    public final void synpred2_IbmSparqlExtAstWalker_fragment() throws RecognitionException {   
        // IbmSparqlExtAstWalker.g:495:81: ( NIL )
        // IbmSparqlExtAstWalker.g:495:83: NIL
        {
        match(input,NIL,FOLLOW_NIL_in_synpred2_IbmSparqlExtAstWalker3312); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred2_IbmSparqlExtAstWalker

    // Delegated rules

    public final boolean synpred1_IbmSparqlExtAstWalker() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred1_IbmSparqlExtAstWalker_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred2_IbmSparqlExtAstWalker() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred2_IbmSparqlExtAstWalker_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA22 dfa22 = new DFA22(this);
    protected DFA104 dfa104 = new DFA104(this);
    protected DFA107 dfa107 = new DFA107(this);
    protected DFA121 dfa121 = new DFA121(this);
    static final String DFA22_eotS =
        "\46\uffff";
    static final String DFA22_eofS =
        "\46\uffff";
    static final String DFA22_minS =
        "\1\36\1\uffff\2\2\1\uffff\1\125\1\3\1\2\1\43\4\2\1\72\1\u00d3\1"+
        "\104\1\43\3\3\3\2\3\3\1\72\1\u00d3\1\104\4\3\1\51\4\3";
    static final String DFA22_maxS =
        "\1\74\1\uffff\2\2\1\uffff\1\125\1\37\1\2\1\u008c\4\2\1\72\1\u00d3"+
        "\1\104\1\u008c\3\3\3\2\3\3\1\72\1\u00d3\1\104\1\125\3\3\1\74\4\3";
    static final String DFA22_acceptS =
        "\1\uffff\1\1\2\uffff\1\2\41\uffff";
    static final String DFA22_specialS =
        "\46\uffff}>";
    static final String[] DFA22_transitionS = {
            "\2\1\11\uffff\1\2\17\uffff\1\3\2\uffff\1\4",
            "",
            "\1\5",
            "\1\6",
            "",
            "\1\7",
            "\1\1\24\uffff\1\1\2\uffff\1\1\2\uffff\2\4",
            "\1\10",
            "\1\12\1\13\61\uffff\1\14\65\uffff\1\11",
            "\1\15",
            "\1\16",
            "\1\17",
            "\1\20",
            "\1\21",
            "\1\22",
            "\1\23",
            "\1\25\1\26\147\uffff\1\24",
            "\1\27",
            "\1\30",
            "\1\31",
            "\1\32",
            "\1\33",
            "\1\34",
            "\1\35",
            "\1\35",
            "\1\35",
            "\1\36",
            "\1\37",
            "\1\40",
            "\1\41\121\uffff\1\7",
            "\1\42",
            "\1\43",
            "\1\44",
            "\1\2\17\uffff\1\3\2\uffff\1\4",
            "\1\45",
            "\1\45",
            "\1\45",
            "\1\35"
    };

    static final short[] DFA22_eot = DFA.unpackEncodedString(DFA22_eotS);
    static final short[] DFA22_eof = DFA.unpackEncodedString(DFA22_eofS);
    static final char[] DFA22_min = DFA.unpackEncodedStringToUnsignedChars(DFA22_minS);
    static final char[] DFA22_max = DFA.unpackEncodedStringToUnsignedChars(DFA22_maxS);
    static final short[] DFA22_accept = DFA.unpackEncodedString(DFA22_acceptS);
    static final short[] DFA22_special = DFA.unpackEncodedString(DFA22_specialS);
    static final short[][] DFA22_transition;

    static {
        int numStates = DFA22_transitionS.length;
        DFA22_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA22_transition[i] = DFA.unpackEncodedString(DFA22_transitionS[i]);
        }
    }

    class DFA22 extends DFA {

        public DFA22(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 22;
            this.eot = DFA22_eot;
            this.eof = DFA22_eof;
            this.min = DFA22_min;
            this.max = DFA22_max;
            this.accept = DFA22_accept;
            this.special = DFA22_special;
            this.transition = DFA22_transition;
        }
        public String getDescription() {
            return "224:20: ( ( constructTemplate[$cq] (d= dataset )* (w= whereClause ) (m= solutionModifier ) ) | ( (d= dataset )* ( ^( WHERE triplesTemplate[$cq,p] ) )? (m= solutionModifier ) ) )";
        }
    }
    static final String DFA104_eotS =
        "\13\uffff";
    static final String DFA104_eofS =
        "\13\uffff";
    static final String DFA104_minS =
        "\1\42\1\uffff\1\2\7\uffff\1\11";
    static final String DFA104_maxS =
        "\1\u00c7\1\uffff\1\2\7\uffff\1\u010c";
    static final String DFA104_acceptS =
        "\1\uffff\1\1\1\uffff\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\uffff";
    static final String DFA104_specialS =
        "\13\uffff}>";
    static final String[] DFA104_transitionS = {
            "\1\10\2\3\1\4\1\uffff\1\1\6\uffff\1\5\1\7\15\uffff\2\6\110\uffff"+
            "\5\1\1\2\36\1\1\uffff\21\1\7\11\3\uffff\1\6",
            "",
            "\1\12",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\2\1\27\uffff\6\1\6\uffff\2\1\12\uffff\1\3\1\1\1\uffff\2\1"+
            "\103\uffff\51\1\1\uffff\30\1\3\uffff\1\1\70\uffff\1\1\2\uffff"+
            "\1\1\2\uffff\7\1"
    };

    static final short[] DFA104_eot = DFA.unpackEncodedString(DFA104_eotS);
    static final short[] DFA104_eof = DFA.unpackEncodedString(DFA104_eofS);
    static final char[] DFA104_min = DFA.unpackEncodedStringToUnsignedChars(DFA104_minS);
    static final char[] DFA104_max = DFA.unpackEncodedStringToUnsignedChars(DFA104_maxS);
    static final short[] DFA104_accept = DFA.unpackEncodedString(DFA104_acceptS);
    static final short[] DFA104_special = DFA.unpackEncodedString(DFA104_specialS);
    static final short[][] DFA104_transition;

    static {
        int numStates = DFA104_transitionS.length;
        DFA104_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA104_transition[i] = DFA.unpackEncodedString(DFA104_transitionS[i]);
        }
    }

    class DFA104 extends DFA {

        public DFA104(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 104;
            this.eot = DFA104_eot;
            this.eof = DFA104_eof;
            this.min = DFA104_min;
            this.max = DFA104_max;
            this.accept = DFA104_accept;
            this.special = DFA104_special;
            this.transition = DFA104_transition;
        }
        public String getDescription() {
            return "861:1: primaryExpression returns [Expression e] : (e1= builtInCall | i= iRIref | f= iRIFunction | r= rDFLiteral | n= numericLiteral | b= booleanLiteral | v= var | a= aggregate );";
        }
    }
    static final String DFA107_eotS =
        "\71\uffff";
    static final String DFA107_eofS =
        "\71\uffff";
    static final String DFA107_minS =
        "\1\47\7\uffff\1\2\60\uffff";
    static final String DFA107_maxS =
        "\1\u00bc\7\uffff\1\u010c\60\uffff";
    static final String DFA107_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\uffff\1\12\1\13\1\14\1\15"+
        "\1\16\1\17\1\20\1\21\1\22\1\23\1\24\1\25\1\26\1\27\1\30\1\31\1\32"+
        "\1\33\1\34\1\35\1\36\1\37\1\40\1\41\1\42\1\43\1\44\1\45\1\46\1\47"+
        "\1\50\1\51\1\52\1\53\1\54\1\55\1\56\1\57\1\60\1\61\1\62\1\63\1\64"+
        "\1\65\1\66\1\67\1\10\1\11";
    static final String DFA107_specialS =
        "\71\uffff}>";
    static final String[] DFA107_transitionS = {
            "\1\66\137\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1"+
            "\13\1\14\1\15\1\16\1\20\1\21\1\23\1\24\1\25\1\26\1\27\1\30\1"+
            "\31\1\32\1\33\1\34\1\35\1\36\1\37\1\40\1\41\1\42\1\43\1\44\1"+
            "\45\1\46\1\uffff\1\47\1\50\1\51\1\52\1\53\1\54\1\55\1\56\1\57"+
            "\1\60\1\61\1\62\1\63\1\64\1\17\1\22\1\65",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\67\1\70\5\uffff\3\70\26\uffff\6\70\4\uffff\1\70\1\uffff"+
            "\2\70\13\uffff\1\70\1\uffff\2\70\34\uffff\2\70\45\uffff\51\70"+
            "\1\uffff\31\70\2\uffff\1\70\70\uffff\1\70\2\uffff\1\70\2\uffff"+
            "\7\70",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA107_eot = DFA.unpackEncodedString(DFA107_eotS);
    static final short[] DFA107_eof = DFA.unpackEncodedString(DFA107_eofS);
    static final char[] DFA107_min = DFA.unpackEncodedStringToUnsignedChars(DFA107_minS);
    static final char[] DFA107_max = DFA.unpackEncodedStringToUnsignedChars(DFA107_maxS);
    static final short[] DFA107_accept = DFA.unpackEncodedString(DFA107_acceptS);
    static final short[] DFA107_special = DFA.unpackEncodedString(DFA107_specialS);
    static final short[][] DFA107_transition;

    static {
        int numStates = DFA107_transitionS.length;
        DFA107_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA107_transition[i] = DFA.unpackEncodedString(DFA107_transitionS[i]);
        }
    }

    class DFA107 extends DFA {

        public DFA107(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 107;
            this.eot = DFA107_eot;
            this.eof = DFA107_eof;
            this.min = DFA107_min;
            this.max = DFA107_max;
            this.accept = DFA107_accept;
            this.special = DFA107_special;
            this.transition = DFA107_transition;
        }
        public String getDescription() {
            return "876:1: builtInCall returns [Expression e] : ( ^( STR st= expression ) | ^( LANG lg= expression ) | ^( LANGMATCHES lm1= expression lm2= expression ) | ^( DATATYPE dt= expression ) | ^( BOUND v= var ) | ^( IRI e6= expression ) | ^( URI e7= expression ) | ^( BNODE e8= expression ) | BNODE | ^( RAND NIL ) | ^( ABS e9= expression ) | ^( CEIL e10= expression ) | ^( FLOOR e11= expression ) | ^( ROUND e12= expression ) | ^( CONCAT e13= expressionList ) | ^( SUBSTR e14= expression e15= expression (e16= expression )? ) | ^( STRLEN e15= expression ) | ^( UCASE e16= expression ) | ^( REPLACE e1= expression e2= expression e3= expression (e4= expression )? ) | ^( LCASE e17= expression ) | ^( ENCODE_FOR_URI e18= expression ) | ^( CONTAINS e19= expression e20= expression ) | ^( STRSTARTS e21= expression e22= expression ) | ^( STRENDS e23= expression e24= expression ) | ^( STRBEFORE e241= expression e242= expression ) | ^( STRAFTER e243= expression e244= expression ) | ^( YEAR e25= expression ) | ^( MONTH e26= expression ) | ^( DAY e27= expression ) | ^( HOURS e28= expression ) | ^( MINUTES e29= expression ) | ^( SECONDS e30= expression ) | ^( TIMEZONE e31= expression ) | ^( TZ e32= expression ) | NOW | UUID | STRUUID | ^( MD5 e33= expression ) | ^( SHA1 e34= expression ) | ^( SHA256 e36= expression ) | ^( SHA384 e37= expression ) | ^( SHA512 e38= expression ) | ^( COALESCE e39= expressionList ) | ^( IF e40= expression e41= expression e42= expression ) | ^( STRLANG e45= expression e46= expression ) | ^( STRDT e47= expression e48= expression ) | ^( SAMETERM sam1= expression sam2= expression ) | ^( ISIRI isi= expression ) | ^( ISURI isu= expression ) | ^( ISBLANK isb= expression ) | ^( ISLITERAL isl= expression ) | ^( ISNUMERIC e55= expression ) | r= regexExpression | p= existsFunc | p= notExistsFunc );";
        }
    }
    static final String DFA121_eotS =
        "\12\uffff";
    static final String DFA121_eofS =
        "\12\uffff";
    static final String DFA121_minS =
        "\1\75\3\2\1\136\1\u00c6\1\u00c7\3\uffff";
    static final String DFA121_maxS =
        "\1\u00c7\3\2\1\u00cb\1\u00cc\1\u00cd\3\uffff";
    static final String DFA121_acceptS =
        "\7\uffff\1\1\1\2\1\3";
    static final String DFA121_specialS =
        "\12\uffff}>";
    static final String[] DFA121_transitionS = {
            "\1\1\1\2\u0088\uffff\1\3",
            "\1\4",
            "\1\5",
            "\1\6",
            "\1\7\151\uffff\1\10\2\uffff\1\11",
            "\1\7\2\uffff\1\10\2\uffff\1\11",
            "\1\7\2\uffff\1\10\2\uffff\1\11",
            "",
            "",
            ""
    };

    static final short[] DFA121_eot = DFA.unpackEncodedString(DFA121_eotS);
    static final short[] DFA121_eof = DFA.unpackEncodedString(DFA121_eofS);
    static final char[] DFA121_min = DFA.unpackEncodedStringToUnsignedChars(DFA121_minS);
    static final char[] DFA121_max = DFA.unpackEncodedStringToUnsignedChars(DFA121_maxS);
    static final short[] DFA121_accept = DFA.unpackEncodedString(DFA121_acceptS);
    static final short[] DFA121_special = DFA.unpackEncodedString(DFA121_specialS);
    static final short[][] DFA121_transition;

    static {
        int numStates = DFA121_transitionS.length;
        DFA121_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA121_transition[i] = DFA.unpackEncodedString(DFA121_transitionS[i]);
        }
    }

    class DFA121 extends DFA {

        public DFA121(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 121;
            this.eot = DFA121_eot;
            this.eof = DFA121_eof;
            this.min = DFA121_min;
            this.max = DFA121_max;
            this.accept = DFA121_accept;
            this.special = DFA121_special;
            this.transition = DFA121_transition;
        }
        public String getDescription() {
            return "1059:1: numericLiteral returns [Constant n] : (n1= numericLiteralUnsigned | n2= numericLiteralPositive | n3= numericLiteralNegative );";
        }
    }
 

    public static final BitSet FOLLOW_ROOT_in_queryUnit82 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_query_in_queryUnit86 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_QUERY_in_query116 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_prologue_in_query126 = new BitSet(new long[]{0x0000000000000000L,0x00000000001A2000L});
    public static final BitSet FOLLOW_selectQuery_in_query145 = new BitSet(new long[]{0x0000000000000008L,0x0040000000000000L});
    public static final BitSet FOLLOW_bindingsClause_in_query163 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_constructQuery_in_query220 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_describeQuery_in_query236 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_askQuery_in_query252 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PROLOGUE_in_prologue308 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_baseDecl_in_prologue310 = new BitSet(new long[]{0x0000000000000008L,0x000000000000000CL});
    public static final BitSet FOLLOW_prefixDecl_in_prologue315 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000008L});
    public static final BitSet FOLLOW_BASE_in_baseDecl346 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_iRIref_in_baseDecl350 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PREFIX_in_prefixDecl378 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_prefixedName_in_prefixDecl382 = new BitSet(new long[]{0x0000001800000000L,0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_iRIref_in_prefixDecl387 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SELECT_in_selectQuery425 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_functionSet_in_selectQuery435 = new BitSet(new long[]{0x0000002000300000L});
    public static final BitSet FOLLOW_selectClause_in_selectQuery453 = new BitSet(new long[]{0x1200020000000000L});
    public static final BitSet FOLLOW_dataset_in_selectQuery469 = new BitSet(new long[]{0x1200020000000000L});
    public static final BitSet FOLLOW_whereClause_in_selectQuery488 = new BitSet(new long[]{0x1200020000000000L});
    public static final BitSet FOLLOW_solutionModifier_in_selectQuery504 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FUNCTION_in_functionSet545 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_functionDecl_in_functionSet554 = new BitSet(new long[]{0x0000002000000008L});
    public static final BitSet FOLLOW_FUNCTION_in_functionDecl590 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VARNAME_in_functionDecl598 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_OPEN_BRACE_in_functionDecl607 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_var_in_functionDecl615 = new BitSet(new long[]{0x0000000400000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_ARROW_in_functionDecl625 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_var_in_functionDecl633 = new BitSet(new long[]{0x0000000400000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_CLOSE_BRACE_in_functionDecl644 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_FUNCLANG_in_functionDecl651 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VARNAME_in_functionDecl661 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_functionBody_in_functionDecl674 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FUNCBODY_in_functionBody720 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_LITERAL2_in_functionBody735 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_groupGraphPattern_in_functionBody755 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DATASET_in_dataset798 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_datasetClause_in_dataset807 = new BitSet(new long[]{0x0000000000000008L,0x0000000000200000L});
    public static final BitSet FOLLOW_SUB_SELECT_in_subSelect841 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_selectClause_in_subSelect850 = new BitSet(new long[]{0x1200020000000000L});
    public static final BitSet FOLLOW_whereClause_in_subSelect866 = new BitSet(new long[]{0x1200020000000000L});
    public static final BitSet FOLLOW_solutionModifier_in_subSelect882 = new BitSet(new long[]{0x0000000000000008L,0x0040000000000000L});
    public static final BitSet FOLLOW_inlineData_in_subSelect908 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TYPE_in_selectClause954 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DISTINCT_in_selectClause958 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_REDUCED_in_selectClause970 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PVARS_in_selectClause999 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_var_in_selectClause1007 = new BitSet(new long[]{0x0000000400400008L,0x0000000000010000L});
    public static final BitSet FOLLOW_expVar_in_selectClause1029 = new BitSet(new long[]{0x0000000400400008L,0x0000000000010000L});
    public static final BitSet FOLLOW_fexp_in_selectClause1052 = new BitSet(new long[]{0x0000000400400008L,0x0000000000010000L});
    public static final BitSet FOLLOW_256_in_selectClause1092 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_AS_in_expVar1123 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_var_in_expVar1127 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_expVar1131 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EXP_in_fexp1149 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_fexp1153 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CONSTRUCT_in_constructQuery1184 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_constructTemplate_in_constructQuery1191 = new BitSet(new long[]{0x0200020000000000L});
    public static final BitSet FOLLOW_dataset_in_constructQuery1205 = new BitSet(new long[]{0x0200020000000000L});
    public static final BitSet FOLLOW_whereClause_in_constructQuery1228 = new BitSet(new long[]{0x1200020000000000L});
    public static final BitSet FOLLOW_solutionModifier_in_constructQuery1247 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_dataset_in_constructQuery1287 = new BitSet(new long[]{0x1200020000000000L});
    public static final BitSet FOLLOW_WHERE_in_constructQuery1309 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_triplesTemplate_in_constructQuery1375 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_solutionModifier_in_constructQuery1455 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DESCRIBE_in_describeQuery1513 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_varOrIRIref2_in_describeQuery1522 = new BitSet(new long[]{0x1200021C00000008L,0x0000000000200000L,0x0000000000001000L});
    public static final BitSet FOLLOW_256_in_describeQuery1533 = new BitSet(new long[]{0x1200020000000008L,0x0000000000200000L});
    public static final BitSet FOLLOW_datasetClause_in_describeQuery1548 = new BitSet(new long[]{0x1200020000000008L,0x0000000000200000L});
    public static final BitSet FOLLOW_whereClause_in_describeQuery1569 = new BitSet(new long[]{0x1200020000000000L});
    public static final BitSet FOLLOW_solutionModifier_in_describeQuery1589 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ASK_in_askQuery1630 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_datasetClause_in_askQuery1638 = new BitSet(new long[]{0x0200000000000008L,0x0000000000200000L});
    public static final BitSet FOLLOW_whereClause_in_askQuery1654 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FROM_in_datasetClause1689 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_defaultGraphClause_in_datasetClause1696 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_namedGraphClause_in_datasetClause1710 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_sourceSelector_in_defaultGraphClause1743 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NAMED_in_namedGraphClause1765 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_sourceSelector_in_namedGraphClause1769 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_iRIref_in_sourceSelector1795 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHERE_in_whereClause1818 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_groupGraphPattern_in_whereClause1822 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MODIFIERS_in_solutionModifier1853 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_groupClause_in_solutionModifier1862 = new BitSet(new long[]{0x0000080000000008L,0x00000000A2000000L});
    public static final BitSet FOLLOW_havingClause_in_solutionModifier1879 = new BitSet(new long[]{0x0000080000000008L,0x00000000A0000000L});
    public static final BitSet FOLLOW_orderClause_in_solutionModifier1896 = new BitSet(new long[]{0x0000000000000008L,0x00000000A0000000L});
    public static final BitSet FOLLOW_limitOffsetClauses_in_solutionModifier1913 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_GROUP_BY_in_groupClause1939 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_groupCondition_in_groupClause1943 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_builtInCall_in_groupCondition1976 = new BitSet(new long[]{0x000010A400000002L,0x0000000000000000L,0x1FFFF7FFFFFFFF80L});
    public static final BitSet FOLLOW_functionCall_in_groupCondition1995 = new BitSet(new long[]{0x000010A400000002L,0x0000000000000000L,0x1FFFF7FFFFFFFF80L});
    public static final BitSet FOLLOW_CONDITION_in_groupCondition2013 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_groupCondition2017 = new BitSet(new long[]{0x0000000400000008L});
    public static final BitSet FOLLOW_var_in_groupCondition2022 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_var_in_groupCondition2039 = new BitSet(new long[]{0x000010A400000002L,0x0000000000000000L,0x1FFFF7FFFFFFFF80L});
    public static final BitSet FOLLOW_HAVING_in_havingClause2113 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_havingCondition_in_havingClause2118 = new BitSet(new long[]{0x000000E000000008L,0x0000000000000000L,0x1FFFF7FFFFFFFF80L});
    public static final BitSet FOLLOW_constraint_in_havingCondition2146 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ORDER_BY_in_orderClause2175 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_orderCondition_in_orderClause2182 = new BitSet(new long[]{0x000000E400000008L,0x0000000018000000L,0x1FFFF7FFFFFFFF80L});
    public static final BitSet FOLLOW_ASC_in_orderCondition2209 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_brackettedExpression_in_orderCondition2213 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DESC_in_orderCondition2225 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_brackettedExpression_in_orderCondition2229 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_constraint_in_orderCondition2243 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_in_orderCondition2259 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_limitClause_in_limitOffsetClauses2299 = new BitSet(new long[]{0x0000000000000002L,0x00000000A0000000L});
    public static final BitSet FOLLOW_offsetClause_in_limitOffsetClauses2307 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_offsetClause_in_limitOffsetClauses2320 = new BitSet(new long[]{0x0000000000000002L,0x0000000020000000L});
    public static final BitSet FOLLOW_limitClause_in_limitOffsetClauses2327 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LIMIT_in_limitClause2365 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_INTEGER_in_limitClause2370 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_OFFSET_in_offsetClause2397 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_INTEGER_in_offsetClause2402 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_inlineData_in_bindingsClause2426 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_iRIref_in_bindingValue2447 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rDFLiteral_in_bindingValue2451 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numericLiteral_in_bindingValue2455 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_booleanLiteral_in_bindingValue2459 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UNDEF_in_bindingValue2463 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_triplesSameSubject_in_triplesTemplate2483 = new BitSet(new long[]{0x0000000000000002L,0x0020000000000000L});
    public static final BitSet FOLLOW_DOT_in_triplesTemplate2490 = new BitSet(new long[]{0x00000000C0000002L});
    public static final BitSet FOLLOW_triplesTemplate_in_triplesTemplate2492 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GROUP_GRAPH_PATTERN_in_groupGraphPattern2522 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_groupGraphPatternSub_in_groupGraphPattern2524 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_subSelect_in_groupGraphPattern2536 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_triplesBlock_in_groupGraphPatternSub2568 = new BitSet(new long[]{0x0000000019000002L,0x1FC8000000000000L});
    public static final BitSet FOLLOW_filter_in_groupGraphPatternSub2600 = new BitSet(new long[]{0x0000000019000002L,0x1FC8000000000000L});
    public static final BitSet FOLLOW_graphPatternNewBGP_in_groupGraphPatternSub2638 = new BitSet(new long[]{0x0000000019000002L,0x1FC8000000000000L});
    public static final BitSet FOLLOW_TRIPLES_BLOCK_in_triplesBlock2697 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_triples_in_triplesBlock2730 = new BitSet(new long[]{0x00000000C0000008L});
    public static final BitSet FOLLOW_triples2_in_triplesBlock2746 = new BitSet(new long[]{0x00000000C0000008L});
    public static final BitSet FOLLOW_TRIPLE_in_triples2794 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_SUBJECT_in_triples2797 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_graphNode_in_triples2803 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PREDICATE_in_triples2839 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_predicate_in_triples2845 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VALUE_in_triples2885 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_object_in_triples2890 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TRIPLE2_in_triples22945 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_SUBJECT_in_triples22948 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_triplesNode_in_triples22954 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PROPERTY_LIST_in_triples22992 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_PREDICATE_in_triples23004 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_predicate_in_triples23011 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VALUE_in_triples23061 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_object_in_triples23066 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_groupMinusOrUnionGraphPattern_in_graphPatternNewBGP3126 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_optionalGraphPattern_in_graphPatternNewBGP3146 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_graphGraphPattern_in_graphPatternNewBGP3167 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_serviceGraphPattern_in_graphPatternNewBGP3186 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_bind_in_graphPatternNewBGP3195 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inlineData_in_graphPatternNewBGP3215 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VALUES_in_inlineData3248 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_dataBlock_in_inlineData3252 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_INLINE_DATA_in_dataBlock3284 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NIL_in_dataBlock3294 = new BitSet(new long[]{0x6000C01800000808L,0x0000000200000000L,0x0000000000001000L,0x0000000000000080L});
    public static final BitSet FOLLOW_var_in_dataBlock3301 = new BitSet(new long[]{0x6000C01C00000808L,0x0000000200000000L,0x0000000000001000L,0x0000000000000080L});
    public static final BitSet FOLLOW_NIL_in_dataBlock3316 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_dataBlockValue_in_dataBlock3323 = new BitSet(new long[]{0x6000C01800000008L,0x0000000200000000L,0x0000000000001000L,0x0000000000000080L});
    public static final BitSet FOLLOW_iRIref_in_dataBlockValue3359 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rDFLiteral_in_dataBlockValue3375 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numericLiteral_in_dataBlockValue3390 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_booleanLiteral_in_dataBlockValue3405 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UNDEF_in_dataBlockValue3421 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPTIONAL_in_optionalGraphPattern3451 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_groupGraphPattern_in_optionalGraphPattern3455 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_GRAPH_in_graphGraphPattern3485 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_varOrIRIref2_in_graphGraphPattern3489 = new BitSet(new long[]{0x0000000009000000L,0x0C00000000000000L});
    public static final BitSet FOLLOW_groupGraphPattern_in_graphGraphPattern3493 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SERVICE_in_serviceGraphPattern3529 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_SILENT_in_serviceGraphPattern3532 = new BitSet(new long[]{0x0000001C00000000L,0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_varOrIRIref_in_serviceGraphPattern3540 = new BitSet(new long[]{0x0000000009000000L,0x0C00000000000000L});
    public static final BitSet FOLLOW_groupGraphPattern_in_serviceGraphPattern3544 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_bind1_in_bind3575 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_bind2_in_bind3584 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BIND_in_bind13606 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_var_in_bind13610 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_bind13616 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BIND_in_bind23649 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_funcCall_in_bind23658 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_var_in_bind23671 = new BitSet(new long[]{0x0000000400000008L});
    public static final BitSet FOLLOW_FUNCCALL_in_funcCall3711 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VARNAME_in_funcCall3720 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_var_in_funcCall3732 = new BitSet(new long[]{0x0000000400000008L});
    public static final BitSet FOLLOW_UNION_in_groupMinusOrUnionGraphPattern3767 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_groupGraphPattern_in_groupMinusOrUnionGraphPattern3788 = new BitSet(new long[]{0x0000000009000000L,0x0C00000000000000L});
    public static final BitSet FOLLOW_groupGraphPattern_in_groupMinusOrUnionGraphPattern3819 = new BitSet(new long[]{0x0000000009000008L,0x0C00000000000000L});
    public static final BitSet FOLLOW_MINUS_in_groupMinusOrUnionGraphPattern3855 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_groupGraphPattern_in_groupMinusOrUnionGraphPattern3870 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_groupGraphPattern_in_groupMinusOrUnionGraphPattern3897 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FILTER_in_filter3930 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_constraint_in_filter3934 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_brackettedExpression_in_constraint3958 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_builtInCall_in_constraint3968 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionCall_in_constraint3981 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_functionCall4003 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_iRIref_in_functionCall4007 = new BitSet(new long[]{0x6800C0FC00000E00L,0x0000000000004000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_argList_in_functionCall4011 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NIL_in_argList4040 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DISTINCT_in_argList4053 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_argList4058 = new BitSet(new long[]{0x6800C0FC00000602L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_argList4071 = new BitSet(new long[]{0x6800C0FC00000602L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_NIL_in_expressionList4106 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_expressionList4129 = new BitSet(new long[]{0x6800C0FC00000602L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_constructTriples_in_constructTemplate4149 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_triples_in_constructTriples4170 = new BitSet(new long[]{0x00000000C0000002L});
    public static final BitSet FOLLOW_triples2_in_constructTriples4181 = new BitSet(new long[]{0x00000000C0000002L});
    public static final BitSet FOLLOW_triples_in_triplesSameSubject4218 = new BitSet(new long[]{0x0000000040000002L});
    public static final BitSet FOLLOW_triples2_in_triplesSameSubject4230 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_graphNode_in_object4252 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_varOrIRIref_in_verb4274 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_257_in_verb4284 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_path_in_verbPath4302 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_in_verbSimple4321 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathAlternative_in_path4338 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathSequence_in_pathAlternative4351 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_258_in_pathAlternative4355 = new BitSet(new long[]{0x0000001800000000L,0x0000000000000040L,0x0000000000001000L,0x0000000000000000L,0x0000000000000092L});
    public static final BitSet FOLLOW_pathSequence_in_pathAlternative4357 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_pathEltOrInverse_in_pathSequence4374 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_259_in_pathSequence4378 = new BitSet(new long[]{0x0000001800000000L,0x0000000000000040L,0x0000000000001000L,0x0000000000000000L,0x0000000000000092L});
    public static final BitSet FOLLOW_pathEltOrInverse_in_pathSequence4380 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_pathPrimary_in_pathElt4397 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000061L});
    public static final BitSet FOLLOW_pathMod_in_pathElt4399 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathElt_in_pathEltOrInverse4414 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_260_in_pathEltOrInverse4418 = new BitSet(new long[]{0x0000001800000000L,0x0000000000000040L,0x0000000000001000L,0x0000000000000000L,0x0000000000000082L});
    public static final BitSet FOLLOW_pathElt_in_pathEltOrInverse4420 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_256_in_pathMod4435 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_261_in_pathMod4444 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_262_in_pathMod4453 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_iRIref_in_pathPrimary4471 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_257_in_pathPrimary4475 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_263_in_pathPrimary4479 = new BitSet(new long[]{0x0000001800000100L,0x0000000000000040L,0x0000000000001000L,0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_pathNegatedPropertySet_in_pathPrimary4481 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_BRACE_in_pathPrimary4485 = new BitSet(new long[]{0x0000001800000000L,0x0000000000000040L,0x0000000000001000L,0x0000000000000000L,0x0000000000000092L});
    public static final BitSet FOLLOW_path_in_pathPrimary4487 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_CLOSE_BRACE_in_pathPrimary4489 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathOneInPropertySet_in_pathNegatedPropertySet4519 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_BRACE_in_pathNegatedPropertySet4527 = new BitSet(new long[]{0x0000001800000100L,0x0000000000000100L,0x0000000000001000L,0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_pathOneInPropertySet_in_pathNegatedPropertySet4535 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L,0x0000000000000000L,0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_258_in_pathNegatedPropertySet4539 = new BitSet(new long[]{0x0000001800000100L,0x0000000000000000L,0x0000000000001000L,0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_pathOneInPropertySet_in_pathNegatedPropertySet4545 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L,0x0000000000000000L,0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_CLOSE_BRACE_in_pathNegatedPropertySet4556 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_iRIref_in_pathOneInPropertySet4584 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_257_in_pathOneInPropertySet4591 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INV_in_pathOneInPropertySet4600 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_iRIrefOrRDFType_in_pathOneInPropertySet4606 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_iRIref_in_iRIrefOrRDFType4633 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_257_in_iRIrefOrRDFType4642 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INTEGER_in_integer4663 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRIPLES_NODE_in_triplesNode4682 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_collection_in_triplesNode4693 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_blankNodePropertyList_in_triplesNode4710 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PROPERTY_LIST_in_blankNodePropertyList4749 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_PREDICATE_in_blankNodePropertyList4770 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_predicate_in_blankNodePropertyList4774 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VALUE_in_blankNodePropertyList4789 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_graphNode_in_blankNodePropertyList4794 = new BitSet(new long[]{0x6020C01C00001808L,0x0000000000000000L,0x0000000000001000L,0x0000000000100080L});
    public static final BitSet FOLLOW_var_in_predicate4850 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_iRIref_in_predicate4865 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_257_in_predicate4876 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALT_in_predicate4891 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_predicate_in_predicate4896 = new BitSet(new long[]{0x0000001C000001E8L,0x0000000000000000L,0x0000000000001000L,0x0000000000000000L,0x0000000000000082L});
    public static final BitSet FOLLOW_SEQ_in_predicate4912 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_predicate_in_predicate4917 = new BitSet(new long[]{0x0000001C000001E8L,0x0000000000000000L,0x0000000000001000L,0x0000000000000000L,0x0000000000000082L});
    public static final BitSet FOLLOW_ELT_in_predicate4933 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_predicate_in_predicate4937 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000061L});
    public static final BitSet FOLLOW_pathMod_in_predicate4943 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_INV_in_predicate4958 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_predicate_in_predicate4962 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_263_in_predicate4973 = new BitSet(new long[]{0x0000001800000100L,0x0000000000000040L,0x0000000000001000L,0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_pathNegatedPropertySet_in_predicate4979 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLLECTION_in_collection5007 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_graphNode_in_collection5012 = new BitSet(new long[]{0x6020C01C00001808L,0x0000000000000000L,0x0000000000001000L,0x0000000000100080L});
    public static final BitSet FOLLOW_var_in_graphNode5038 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_graphTerm_in_graphNode5051 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_triplesNode_in_graphNode5063 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_in_varOrTerm5086 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_graphTerm_in_varOrTerm5101 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_in_varOrIRIref5123 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_iRIref_in_varOrIRIref5138 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_in_varOrIRIref25173 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_iRIref_in_varOrIRIref25189 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VAR_in_var5216 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VAR1_in_var5223 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VAR2_in_var5238 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_iRIref_in_graphTerm5272 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rDFLiteral_in_graphTerm5286 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numericLiteral_in_graphTerm5298 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_booleanLiteral_in_graphTerm5311 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_blankNode_in_graphTerm5324 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NIL_in_graphTerm5337 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LOGICAL_OR_in_expression5368 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression5380 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_expression5401 = new BitSet(new long[]{0x6800C0FC00000608L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_LOGICAL_AND_in_expression5426 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression5438 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_expression5459 = new BitSet(new long[]{0x6800C0FC00000608L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_264_in_expression5486 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression5500 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_expression5511 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_265_in_expression5531 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression5545 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_expression5556 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LT_in_expression5576 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression5591 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_expression5606 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_266_in_expression5626 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression5641 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_expression5652 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LTE_in_expression5672 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression5686 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_expression5697 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_267_in_expression5717 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression5731 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_expression5742 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IN_in_expression5762 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression5789 = new BitSet(new long[]{0x6800C0FC00000E00L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expressionList_in_expression5802 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NOT_in_expression5848 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression5855 = new BitSet(new long[]{0x6800C0FC00000E00L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expressionList_in_expression5863 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_262_in_expression5897 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression5924 = new BitSet(new long[]{0x6800C0FC00000608L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_expression5936 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BROKEN_PLUS_in_expression5959 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression5978 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_expression5989 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_268_in_expression6009 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression6035 = new BitSet(new long[]{0x6800C0FC00000608L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_expression6047 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BROKEN_MINUS_in_expression6064 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression6082 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_expression6093 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_256_in_expression6113 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression6140 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_expression6151 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_259_in_expression6171 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression6198 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_expression6208 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_263_in_expression6228 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression6232 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_brackettedExpression_in_expression6243 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primaryExpression_in_expression6253 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_builtInCall_in_primaryExpression6278 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_iRIref_in_primaryExpression6290 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_iRIFunction_in_primaryExpression6302 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rDFLiteral_in_primaryExpression6314 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numericLiteral_in_primaryExpression6326 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_booleanLiteral_in_primaryExpression6337 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_in_primaryExpression6348 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aggregate_in_primaryExpression6362 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXPRESSION_in_brackettedExpression6385 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_brackettedExpression6390 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STR_in_builtInCall6420 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6424 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LANG_in_builtInCall6437 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6441 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LANGMATCHES_in_builtInCall6454 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6458 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_builtInCall6462 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DATATYPE_in_builtInCall6475 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6479 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BOUND_in_builtInCall6491 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_var_in_builtInCall6495 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IRI_in_builtInCall6513 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6517 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_URI_in_builtInCall6530 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6534 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BNODE_in_builtInCall6546 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6550 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BNODE_in_builtInCall6561 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RAND_in_builtInCall6572 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NIL_in_builtInCall6574 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ABS_in_builtInCall6587 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6591 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CEIL_in_builtInCall6603 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6607 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FLOOR_in_builtInCall6619 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6623 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ROUND_in_builtInCall6635 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6639 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CONCAT_in_builtInCall6651 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expressionList_in_builtInCall6655 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SUBSTR_in_builtInCall6668 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6672 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_builtInCall6676 = new BitSet(new long[]{0x6800C0FC00000608L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_builtInCall6700 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRLEN_in_builtInCall6723 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6727 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_UCASE_in_builtInCall6739 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6743 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_REPLACE_in_builtInCall6755 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6759 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_builtInCall6763 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_builtInCall6767 = new BitSet(new long[]{0x6800C0FC00000608L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_builtInCall6791 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LCASE_in_builtInCall6809 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6813 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ENCODE_FOR_URI_in_builtInCall6825 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6829 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CONTAINS_in_builtInCall6841 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6845 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_builtInCall6849 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRSTARTS_in_builtInCall6861 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6865 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_builtInCall6869 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRENDS_in_builtInCall6881 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6885 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_builtInCall6889 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRBEFORE_in_builtInCall6901 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6905 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_builtInCall6909 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRAFTER_in_builtInCall6921 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6925 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_builtInCall6929 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_YEAR_in_builtInCall6941 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6945 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MONTH_in_builtInCall6957 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6961 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DAY_in_builtInCall6973 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6977 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_HOURS_in_builtInCall6989 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6993 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MINUTES_in_builtInCall7005 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall7009 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SECONDS_in_builtInCall7021 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall7025 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TIMEZONE_in_builtInCall7037 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall7041 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TZ_in_builtInCall7053 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall7057 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NOW_in_builtInCall7068 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UUID_in_builtInCall7080 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRUUID_in_builtInCall7092 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MD5_in_builtInCall7101 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall7105 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SHA1_in_builtInCall7117 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall7121 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SHA256_in_builtInCall7133 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall7137 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SHA384_in_builtInCall7149 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall7153 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SHA512_in_builtInCall7165 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall7169 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_COALESCE_in_builtInCall7181 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expressionList_in_builtInCall7185 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IF_in_builtInCall7197 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall7201 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_builtInCall7205 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_builtInCall7209 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRLANG_in_builtInCall7221 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall7225 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_builtInCall7229 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRDT_in_builtInCall7241 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall7245 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_builtInCall7249 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SAMETERM_in_builtInCall7261 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall7265 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_builtInCall7269 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ISIRI_in_builtInCall7281 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall7285 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ISURI_in_builtInCall7297 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall7301 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ISBLANK_in_builtInCall7313 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall7317 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ISLITERAL_in_builtInCall7329 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall7333 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ISNUMERIC_in_builtInCall7345 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall7349 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_regexExpression_in_builtInCall7362 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_existsFunc_in_builtInCall7371 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_notExistsFunc_in_builtInCall7380 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_REGEX_in_regexExpression7403 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_regexExpression7407 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_regexExpression7411 = new BitSet(new long[]{0x6800C0FC00000608L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_regexExpression7415 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EXISTS_in_existsFunc7447 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_groupGraphPattern_in_existsFunc7451 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NOT_EXISTS_in_notExistsFunc7482 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_groupGraphPattern_in_notExistsFunc7486 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_COUNT_in_aggregate7521 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregate7533 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_aggregate7590 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_256_in_aggregate7600 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SUM_in_aggregate7620 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregate7632 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_aggregate7649 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MIN_in_aggregate7662 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregate7675 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_aggregate7691 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MAX_in_aggregate7704 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregate7717 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_aggregate7734 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_AVG_in_aggregate7747 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregate7760 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_aggregate7777 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SAMPLE_in_aggregate7790 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregate7802 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_aggregate7819 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_GROUP_CONCAT_in_aggregate7833 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregate7845 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_aggregate7860 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_SEPARATOR_in_aggregate7871 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_string_in_aggregate7875 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FUNCTION_in_iRIFunction7904 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_iRIref_in_iRIFunction7914 = new BitSet(new long[]{0x6800C0FC00000E08L,0x0000000000004000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_argList_in_iRIFunction7926 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_string_in_rDFLiteral7958 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000000020L,0x0000000000002000L});
    public static final BitSet FOLLOW_LANGTAG_in_rDFLiteral7973 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_269_in_rDFLiteral7987 = new BitSet(new long[]{0x0000001800000000L,0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_iRIref_in_rDFLiteral7991 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numericLiteralUnsigned_in_numericLiteral8022 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numericLiteralPositive_in_numericLiteral8032 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numericLiteralNegative_in_numericLiteral8042 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BIG_INTEGER_in_numericLiteralUnsigned8064 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_INTEGER_in_numericLiteralUnsigned8069 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BIG_DECIMAL_in_numericLiteralUnsigned8085 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DECIMAL_in_numericLiteralUnsigned8089 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DOUBLE_in_numericLiteralUnsigned8104 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DOUBLE_in_numericLiteralUnsigned8109 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BIG_INTEGER_in_numericLiteralPositive8139 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_INTEGER_POSITIVE_in_numericLiteralPositive8144 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BIG_DECIMAL_in_numericLiteralPositive8157 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DECIMAL_POSITIVE_in_numericLiteralPositive8161 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DOUBLE_in_numericLiteralPositive8174 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DOUBLE_POSITIVE_in_numericLiteralPositive8179 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BIG_INTEGER_in_numericLiteralNegative8204 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_INTEGER_NEGATIVE_in_numericLiteralNegative8209 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BIG_DECIMAL_in_numericLiteralNegative8222 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DECIMAL_NEGATIVE_in_numericLiteralNegative8226 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DOUBLE_in_numericLiteralNegative8239 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DOUBLE_NEGATIVE_in_numericLiteralNegative8244 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BOOLEAN_in_booleanLiteral8268 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_TRUE_in_booleanLiteral8270 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BOOLEAN_in_booleanLiteral8285 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_FALSE_in_booleanLiteral8287 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_in_string8310 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_LITERAL1_in_string8314 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_in_string8331 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_LITERAL2_in_string8335 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_in_string8352 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_LITERAL_LONG1_in_string8356 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_in_string8368 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_LITERAL_LONG2_in_string8372 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IRI_in_iRIref8396 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IRI_REF_in_iRIref8400 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_prefixedName_in_iRIref8412 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PREFIXED_NAME_in_prefixedName8434 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_PNAME_LN_in_prefixedName8438 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PREFIXED_NS_in_prefixedName8447 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_PNAME_NS_in_prefixedName8451 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BLANK_NODE_LABEL_in_blankNode8475 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANNON_in_blankNode8486 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_OPEN_SQ_BRACKET_in_blankNode8490 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NIL_in_synpred1_IbmSparqlExtAstWalker3290 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NIL_in_synpred2_IbmSparqlExtAstWalker3312 = new BitSet(new long[]{0x0000000000000002L});

}