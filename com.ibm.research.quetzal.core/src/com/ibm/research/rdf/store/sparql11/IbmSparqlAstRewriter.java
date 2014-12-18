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
 // $ANTLR 3.3 Nov 30, 2010 12:50:56 IbmSparqlAstRewriter.g 2014-07-17 09:08:07
 
package com.ibm.research.rdf.store.sparql11;
	
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.AbstractMap;
import java.util.HashMap;




import com.ibm.research.rdf.store.sparql11.SPARQLsyntaxError;
import com.ibm.research.rdf.store.sparql11.model.*;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class IbmSparqlAstRewriter extends TreeRewriter {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "PATH", "ALT", "SEQ", "ELT", "INV", "BROKEN_PLUS", "BROKEN_MINUS", "NIL", "ANNON", "ROOT", "PROLOGUE", "DEFAULT_NAMESPACE", "NAMESPACE_PREFIX_MAP", "KEY", "QUERY", "UPDATE", "TYPE", "PVARS", "EXP", "NOT_IN", "GROUP_GRAPH_PATTERN", "GROUP_GRAPH_PATTERN_SUB", "GRAPH_GRAPH_PATTERN", "SUB_SELECT", "TRIPLES_BLOCK", "NON_TRIPLES", "TRIPLE", "TRIPLE2", "TRIPLES_SAME_SUBJECT", "GRAPH_NODE", "VAR", "PREFIXED_NAME", "PREFIXED_NS", "FUNCTION", "EXPRESSION", "NOT_EXISTS", "IRI_OR_FUNCTION", "DATASET", "GROUP_BY", "ORDER_BY", "CONDITION", "BIND_VALUES", "STRING", "BOOLEAN", "NUMERIC", "SUBJECT", "PREDICATE", "VALUE", "TRIPLES_NODE_PROPERTY_LIST", "TRIPLES_NODE", "COLLECTION", "PROPERTY_LIST", "PREDICATE_VALUE", "WHERE", "IRI_REF", "LTE", "MODIFIERS", "BIG_INTEGER", "BIG_DECIMAL", "INLINE_DATA", "BASE", "PREFIX", "PNAME_NS", "SELECT", "DISTINCT", "REDUCED", "OPEN_BRACE", "AS", "CLOSE_BRACE", "CONSTRUCT", "WHERE_TOKEN", "OPEN_CURLY_BRACE", "CLOSE_CURLY_BRACE", "DESCRIBE", "ASK", "FROM", "NAMED", "GROUP", "BY", "HAVING", "ORDER", "ASC", "DESC", "LIMIT", "INTEGER", "OFFSET", "BINDINGS", "UNDEF", "SEMICOLON", "LOAD", "SILENT", "INTO", "CLEAR", "DROP", "CREATE", "ADD", "TO", "MOVE", "COPY", "INSERT", "DATA", "DELETE", "WITH", "USING", "DEFAULT", "GRAPH", "ALL", "DOT", "VALUES", "OPTIONAL", "SERVICE", "BIND", "UNION", "MINUS", "FILTER", "COMMA", "OPEN_SQ_BRACKET", "CLOSE_SQ_BRACKET", "VAR1", "VAR2", "LOGICAL_OR", "LOGICAL_AND", "LT", "IN", "NOT", "STR", "LANG", "LANGMATCHES", "DATATYPE", "BOUND", "IRI", "URI", "BNODE", "RAND", "ABS", "CEIL", "FLOOR", "ROUND", "CONCAT", "STRLEN", "UCASE", "LCASE", "ENCODE_FOR_URI", "CONTAINS", "STRSTARTS", "STRENDS", "STRBEFORE", "STRAFTER", "YEAR", "MONTH", "DAY", "HOURS", "MINUTES", "SECONDS", "TIMEZONE", "TZ", "NOW", "UUID", "STRUUID", "MD5", "SHA1", "SHA224", "SHA256", "SHA384", "SHA512", "COALESCE", "IF", "STRLANG", "STRDT", "SAMETERM", "ISIRI", "ISURI", "ISBLANK", "ISLITERAL", "ISNUMERIC", "REGEX", "SUBSTR", "REPLACE", "EXISTS", "COUNT", "SUM", "MIN", "MAX", "AVG", "SAMPLE", "GROUP_CONCAT", "SEPARATOR", "LANGTAG", "DECIMAL", "DOUBLE", "INTEGER_POSITIVE", "DECIMAL_POSITIVE", "DOUBLE_POSITIVE", "INTEGER_NEGATIVE", "DECIMAL_NEGATIVE", "DOUBLE_NEGATIVE", "TRUE", "FALSE", "STRING_LITERAL1", "STRING_LITERAL2", "STRING_LITERAL_LONG1", "STRING_LITERAL_LONG2", "PNAME_LN", "BLANK_NODE_LABEL", "B", "A", "S", "E", "P", "R", "F", "I", "X", "L", "C", "T", "D", "N", "U", "O", "W", "H", "K", "M", "G", "Y", "V", "Z", "UNICODE_ESCAPE", "PN_PREFIX", "PN_LOCAL", "VARNAME", "DIGIT", "HEXDIGIT", "EXPONENT", "ECHAR", "WS", "EOL", "COMMENT", "PN_CHARS_BASE", "PN_CHARS_U", "PN_CHARS", "PLX", "PERCENT", "PN_LOCAL_ESC", "J", "Q", "'*'", "'a'", "'|'", "'/'", "'^'", "'?'", "'+'", "'!'", "'='", "'!='", "'>'", "'>='", "'-'", "'^^'", "N_GROUP_GRAPH_PATTERN", "N_UNION"
    };
    public static final int EOF=-1;
    public static final int T__251=251;
    public static final int T__252=252;
    public static final int T__253=253;
    public static final int T__254=254;
    public static final int T__255=255;
    public static final int T__256=256;
    public static final int T__257=257;
    public static final int T__258=258;
    public static final int T__259=259;
    public static final int T__260=260;
    public static final int T__261=261;
    public static final int T__262=262;
    public static final int T__263=263;
    public static final int T__264=264;
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
    public static final int BASE=64;
    public static final int PREFIX=65;
    public static final int PNAME_NS=66;
    public static final int SELECT=67;
    public static final int DISTINCT=68;
    public static final int REDUCED=69;
    public static final int OPEN_BRACE=70;
    public static final int AS=71;
    public static final int CLOSE_BRACE=72;
    public static final int CONSTRUCT=73;
    public static final int WHERE_TOKEN=74;
    public static final int OPEN_CURLY_BRACE=75;
    public static final int CLOSE_CURLY_BRACE=76;
    public static final int DESCRIBE=77;
    public static final int ASK=78;
    public static final int FROM=79;
    public static final int NAMED=80;
    public static final int GROUP=81;
    public static final int BY=82;
    public static final int HAVING=83;
    public static final int ORDER=84;
    public static final int ASC=85;
    public static final int DESC=86;
    public static final int LIMIT=87;
    public static final int INTEGER=88;
    public static final int OFFSET=89;
    public static final int BINDINGS=90;
    public static final int UNDEF=91;
    public static final int SEMICOLON=92;
    public static final int LOAD=93;
    public static final int SILENT=94;
    public static final int INTO=95;
    public static final int CLEAR=96;
    public static final int DROP=97;
    public static final int CREATE=98;
    public static final int ADD=99;
    public static final int TO=100;
    public static final int MOVE=101;
    public static final int COPY=102;
    public static final int INSERT=103;
    public static final int DATA=104;
    public static final int DELETE=105;
    public static final int WITH=106;
    public static final int USING=107;
    public static final int DEFAULT=108;
    public static final int GRAPH=109;
    public static final int ALL=110;
    public static final int DOT=111;
    public static final int VALUES=112;
    public static final int OPTIONAL=113;
    public static final int SERVICE=114;
    public static final int BIND=115;
    public static final int UNION=116;
    public static final int MINUS=117;
    public static final int FILTER=118;
    public static final int COMMA=119;
    public static final int OPEN_SQ_BRACKET=120;
    public static final int CLOSE_SQ_BRACKET=121;
    public static final int VAR1=122;
    public static final int VAR2=123;
    public static final int LOGICAL_OR=124;
    public static final int LOGICAL_AND=125;
    public static final int LT=126;
    public static final int IN=127;
    public static final int NOT=128;
    public static final int STR=129;
    public static final int LANG=130;
    public static final int LANGMATCHES=131;
    public static final int DATATYPE=132;
    public static final int BOUND=133;
    public static final int IRI=134;
    public static final int URI=135;
    public static final int BNODE=136;
    public static final int RAND=137;
    public static final int ABS=138;
    public static final int CEIL=139;
    public static final int FLOOR=140;
    public static final int ROUND=141;
    public static final int CONCAT=142;
    public static final int STRLEN=143;
    public static final int UCASE=144;
    public static final int LCASE=145;
    public static final int ENCODE_FOR_URI=146;
    public static final int CONTAINS=147;
    public static final int STRSTARTS=148;
    public static final int STRENDS=149;
    public static final int STRBEFORE=150;
    public static final int STRAFTER=151;
    public static final int YEAR=152;
    public static final int MONTH=153;
    public static final int DAY=154;
    public static final int HOURS=155;
    public static final int MINUTES=156;
    public static final int SECONDS=157;
    public static final int TIMEZONE=158;
    public static final int TZ=159;
    public static final int NOW=160;
    public static final int UUID=161;
    public static final int STRUUID=162;
    public static final int MD5=163;
    public static final int SHA1=164;
    public static final int SHA224=165;
    public static final int SHA256=166;
    public static final int SHA384=167;
    public static final int SHA512=168;
    public static final int COALESCE=169;
    public static final int IF=170;
    public static final int STRLANG=171;
    public static final int STRDT=172;
    public static final int SAMETERM=173;
    public static final int ISIRI=174;
    public static final int ISURI=175;
    public static final int ISBLANK=176;
    public static final int ISLITERAL=177;
    public static final int ISNUMERIC=178;
    public static final int REGEX=179;
    public static final int SUBSTR=180;
    public static final int REPLACE=181;
    public static final int EXISTS=182;
    public static final int COUNT=183;
    public static final int SUM=184;
    public static final int MIN=185;
    public static final int MAX=186;
    public static final int AVG=187;
    public static final int SAMPLE=188;
    public static final int GROUP_CONCAT=189;
    public static final int SEPARATOR=190;
    public static final int LANGTAG=191;
    public static final int DECIMAL=192;
    public static final int DOUBLE=193;
    public static final int INTEGER_POSITIVE=194;
    public static final int DECIMAL_POSITIVE=195;
    public static final int DOUBLE_POSITIVE=196;
    public static final int INTEGER_NEGATIVE=197;
    public static final int DECIMAL_NEGATIVE=198;
    public static final int DOUBLE_NEGATIVE=199;
    public static final int TRUE=200;
    public static final int FALSE=201;
    public static final int STRING_LITERAL1=202;
    public static final int STRING_LITERAL2=203;
    public static final int STRING_LITERAL_LONG1=204;
    public static final int STRING_LITERAL_LONG2=205;
    public static final int PNAME_LN=206;
    public static final int BLANK_NODE_LABEL=207;
    public static final int B=208;
    public static final int A=209;
    public static final int S=210;
    public static final int E=211;
    public static final int P=212;
    public static final int R=213;
    public static final int F=214;
    public static final int I=215;
    public static final int X=216;
    public static final int L=217;
    public static final int C=218;
    public static final int T=219;
    public static final int D=220;
    public static final int N=221;
    public static final int U=222;
    public static final int O=223;
    public static final int W=224;
    public static final int H=225;
    public static final int K=226;
    public static final int M=227;
    public static final int G=228;
    public static final int Y=229;
    public static final int V=230;
    public static final int Z=231;
    public static final int UNICODE_ESCAPE=232;
    public static final int PN_PREFIX=233;
    public static final int PN_LOCAL=234;
    public static final int VARNAME=235;
    public static final int DIGIT=236;
    public static final int HEXDIGIT=237;
    public static final int EXPONENT=238;
    public static final int ECHAR=239;
    public static final int WS=240;
    public static final int EOL=241;
    public static final int COMMENT=242;
    public static final int PN_CHARS_BASE=243;
    public static final int PN_CHARS_U=244;
    public static final int PN_CHARS=245;
    public static final int PLX=246;
    public static final int PERCENT=247;
    public static final int PN_LOCAL_ESC=248;
    public static final int J=249;
    public static final int Q=250;
    public static final int N_GROUP_GRAPH_PATTERN=265;
    public static final int N_UNION=266;

    // delegates
    // delegators


        public IbmSparqlAstRewriter(TreeNodeStream input) {
            this(input, new RecognizerSharedState());
        }
        public IbmSparqlAstRewriter(TreeNodeStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return IbmSparqlAstRewriter.tokenNames; }
    public String getGrammarFileName() { return "IbmSparqlAstRewriter.g"; }


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


    public static class bottomup_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "bottomup"
    // IbmSparqlAstRewriter.g:56:1: bottomup : ( groupGraphPatternNull | unionNull | whereNull );
    public final IbmSparqlAstRewriter.bottomup_return bottomup() throws RecognitionException {
        IbmSparqlAstRewriter.bottomup_return retval = new IbmSparqlAstRewriter.bottomup_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        IbmSparqlAstRewriter.groupGraphPatternNull_return groupGraphPatternNull1 = null;

        IbmSparqlAstRewriter.unionNull_return unionNull2 = null;

        IbmSparqlAstRewriter.whereNull_return whereNull3 = null;



        try {
            // IbmSparqlAstRewriter.g:57:2: ( groupGraphPatternNull | unionNull | whereNull )
            int alt1=3;
            switch ( input.LA(1) ) {
            case GROUP_GRAPH_PATTERN:
                {
                alt1=1;
                }
                break;
            case UNION:
                {
                alt1=2;
                }
                break;
            case WHERE:
                {
                alt1=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                throw nvae;
            }

            switch (alt1) {
                case 1 :
                    // IbmSparqlAstRewriter.g:57:4: groupGraphPatternNull
                    {
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_groupGraphPatternNull_in_bottomup97);
                    groupGraphPatternNull1=groupGraphPatternNull();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==1 ) 
                     
                    if ( _first_0==null ) _first_0 = groupGraphPatternNull1.tree;

                    if ( state.backtracking==1 ) {
                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);}
                    }
                    break;
                case 2 :
                    // IbmSparqlAstRewriter.g:58:4: unionNull
                    {
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_unionNull_in_bottomup102);
                    unionNull2=unionNull();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==1 ) 
                     
                    if ( _first_0==null ) _first_0 = unionNull2.tree;

                    if ( state.backtracking==1 ) {
                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);}
                    }
                    break;
                case 3 :
                    // IbmSparqlAstRewriter.g:59:4: whereNull
                    {
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_whereNull_in_bottomup107);
                    whereNull3=whereNull();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==1 ) 
                     
                    if ( _first_0==null ) _first_0 = whereNull3.tree;

                    if ( state.backtracking==1 ) {
                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);}
                    }
                    break;

            }
        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "bottomup"

    public static class whereNull_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "whereNull"
    // IbmSparqlAstRewriter.g:62:1: whereNull : ^( WHERE GROUP_GRAPH_PATTERN ) -> WHERE ;
    public final IbmSparqlAstRewriter.whereNull_return whereNull() throws RecognitionException {
        IbmSparqlAstRewriter.whereNull_return retval = new IbmSparqlAstRewriter.whereNull_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree WHERE4=null;
        CommonTree GROUP_GRAPH_PATTERN5=null;

        CommonTree WHERE4_tree=null;
        CommonTree GROUP_GRAPH_PATTERN5_tree=null;
        RewriteRuleNodeStream stream_WHERE=new RewriteRuleNodeStream(adaptor,"token WHERE");
        RewriteRuleNodeStream stream_GROUP_GRAPH_PATTERN=new RewriteRuleNodeStream(adaptor,"token GROUP_GRAPH_PATTERN");

        try {
            // IbmSparqlAstRewriter.g:63:2: ( ^( WHERE GROUP_GRAPH_PATTERN ) -> WHERE )
            // IbmSparqlAstRewriter.g:63:4: ^( WHERE GROUP_GRAPH_PATTERN )
            {
            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_1 = _last;
            CommonTree _first_1 = null;
            _last = (CommonTree)input.LT(1);
            WHERE4=(CommonTree)match(input,WHERE,FOLLOW_WHERE_in_whereNull120); if (state.failed) return retval; 
            if ( state.backtracking==1 ) stream_WHERE.add(WHERE4);


            if ( state.backtracking==1 )
            if ( _first_0==null ) _first_0 = WHERE4;
            match(input, Token.DOWN, null); if (state.failed) return retval;
            _last = (CommonTree)input.LT(1);
            GROUP_GRAPH_PATTERN5=(CommonTree)match(input,GROUP_GRAPH_PATTERN,FOLLOW_GROUP_GRAPH_PATTERN_in_whereNull122); if (state.failed) return retval; 
            if ( state.backtracking==1 ) stream_GROUP_GRAPH_PATTERN.add(GROUP_GRAPH_PATTERN5);


            match(input, Token.UP, null); if (state.failed) return retval;_last = _save_last_1;
            }



            // AST REWRITE
            // elements: WHERE
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==1 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 63:36: -> WHERE
            {
                adaptor.addChild(root_0, stream_WHERE.nextNode());

            }

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            input.replaceChildren(adaptor.getParent(retval.start),
                                  adaptor.getChildIndex(retval.start),
                                  adaptor.getChildIndex(_last),
                                  retval.tree);}
            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "whereNull"

    public static class groupGraphPatternNull_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "groupGraphPatternNull"
    // IbmSparqlAstRewriter.g:66:1: groupGraphPatternNull : ^( GROUP_GRAPH_PATTERN ( ( GROUP_GRAPH_PATTERN )=> GROUP_GRAPH_PATTERN | ( ^( TRIPLES_BLOCK ( . )+ ) )=> (x+= . ) | ( ^( N_GROUP_GRAPH_PATTERN ( . )+ ) )=> (x+= . ) | (x+= . ) )+ ) -> {(c == 0)&&(t == 0)&&(g == 0)}? GROUP_GRAPH_PATTERN -> {(c == 0)&&(t == 0)&&(g > 0)}? ( $x)+ -> ^( N_GROUP_GRAPH_PATTERN ( $x)* ) ;
    public final IbmSparqlAstRewriter.groupGraphPatternNull_return groupGraphPatternNull() throws RecognitionException {
        IbmSparqlAstRewriter.groupGraphPatternNull_return retval = new IbmSparqlAstRewriter.groupGraphPatternNull_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree GROUP_GRAPH_PATTERN6=null;
        CommonTree GROUP_GRAPH_PATTERN7=null;
        CommonTree x=null;
        List list_x=null;

        CommonTree GROUP_GRAPH_PATTERN6_tree=null;
        CommonTree GROUP_GRAPH_PATTERN7_tree=null;
        CommonTree x_tree=null;
        RewriteRuleNodeStream stream_GROUP_GRAPH_PATTERN=new RewriteRuleNodeStream(adaptor,"token GROUP_GRAPH_PATTERN");


        		int t = 0, c = 0, g = 0;
        	
        try {
            // IbmSparqlAstRewriter.g:70:2: ( ^( GROUP_GRAPH_PATTERN ( ( GROUP_GRAPH_PATTERN )=> GROUP_GRAPH_PATTERN | ( ^( TRIPLES_BLOCK ( . )+ ) )=> (x+= . ) | ( ^( N_GROUP_GRAPH_PATTERN ( . )+ ) )=> (x+= . ) | (x+= . ) )+ ) -> {(c == 0)&&(t == 0)&&(g == 0)}? GROUP_GRAPH_PATTERN -> {(c == 0)&&(t == 0)&&(g > 0)}? ( $x)+ -> ^( N_GROUP_GRAPH_PATTERN ( $x)* ) )
            // IbmSparqlAstRewriter.g:70:4: ^( GROUP_GRAPH_PATTERN ( ( GROUP_GRAPH_PATTERN )=> GROUP_GRAPH_PATTERN | ( ^( TRIPLES_BLOCK ( . )+ ) )=> (x+= . ) | ( ^( N_GROUP_GRAPH_PATTERN ( . )+ ) )=> (x+= . ) | (x+= . ) )+ )
            {
            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_1 = _last;
            CommonTree _first_1 = null;
            _last = (CommonTree)input.LT(1);
            GROUP_GRAPH_PATTERN6=(CommonTree)match(input,GROUP_GRAPH_PATTERN,FOLLOW_GROUP_GRAPH_PATTERN_in_groupGraphPatternNull149); if (state.failed) return retval; 
            if ( state.backtracking==1 ) stream_GROUP_GRAPH_PATTERN.add(GROUP_GRAPH_PATTERN6);


            if ( state.backtracking==1 )
            if ( _first_0==null ) _first_0 = GROUP_GRAPH_PATTERN6;
            match(input, Token.DOWN, null); if (state.failed) return retval;
            // IbmSparqlAstRewriter.g:70:27: ( ( GROUP_GRAPH_PATTERN )=> GROUP_GRAPH_PATTERN | ( ^( TRIPLES_BLOCK ( . )+ ) )=> (x+= . ) | ( ^( N_GROUP_GRAPH_PATTERN ( . )+ ) )=> (x+= . ) | (x+= . ) )+
            int cnt2=0;
            loop2:
            do {
                int alt2=5;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==GROUP_GRAPH_PATTERN) ) {
                    int LA2_2 = input.LA(2);

                    if ( (synpred1_IbmSparqlAstRewriter()) ) {
                        alt2=1;
                    }
                    else if ( (synpred2_IbmSparqlAstRewriter()) ) {
                        alt2=2;
                    }
                    else if ( (synpred3_IbmSparqlAstRewriter()) ) {
                        alt2=3;
                    }
                    else if ( (true) ) {
                        alt2=4;
                    }


                }
                else if ( ((LA2_0>=PATH && LA2_0<=NOT_IN)||(LA2_0>=GROUP_GRAPH_PATTERN_SUB && LA2_0<=N_UNION)) ) {
                    int LA2_3 = input.LA(2);

                    if ( (synpred2_IbmSparqlAstRewriter()) ) {
                        alt2=2;
                    }
                    else if ( (synpred3_IbmSparqlAstRewriter()) ) {
                        alt2=3;
                    }
                    else if ( (true) ) {
                        alt2=4;
                    }


                }


                switch (alt2) {
            	case 1 :
            	    // IbmSparqlAstRewriter.g:70:30: ( GROUP_GRAPH_PATTERN )=> GROUP_GRAPH_PATTERN
            	    {
            	    _last = (CommonTree)input.LT(1);
            	    GROUP_GRAPH_PATTERN7=(CommonTree)match(input,GROUP_GRAPH_PATTERN,FOLLOW_GROUP_GRAPH_PATTERN_in_groupGraphPatternNull160); if (state.failed) return retval; 
            	    if ( state.backtracking==1 ) stream_GROUP_GRAPH_PATTERN.add(GROUP_GRAPH_PATTERN7);


            	    if ( state.backtracking==1 ) {
            	    retval.tree = (CommonTree)_first_0;
            	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
            	        retval.tree = (CommonTree)adaptor.getParent(retval.tree);}
            	    }
            	    break;
            	case 2 :
            	    // IbmSparqlAstRewriter.g:71:11: ( ^( TRIPLES_BLOCK ( . )+ ) )=> (x+= . )
            	    {
            	    // IbmSparqlAstRewriter.g:71:35: (x+= . )
            	    // IbmSparqlAstRewriter.g:71:36: x+= .
            	    {
            	    _last = (CommonTree)input.LT(1);
            	    x=(CommonTree)input.LT(1);
            	    matchAny(input); if (state.failed) return retval;
            	     
            	    if ( state.backtracking==1 )
            	    if ( _first_1==null ) _first_1 = x;
            	    if (list_x==null) list_x=new ArrayList();
            	    list_x.add(x);

            	    if ( state.backtracking==1 ) {
            	      t++;
            	    }

            	    if ( state.backtracking==1 ) {
            	    retval.tree = (CommonTree)_first_0;
            	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
            	        retval.tree = (CommonTree)adaptor.getParent(retval.tree);}
            	    }


            	    if ( state.backtracking==1 ) {
            	    retval.tree = (CommonTree)_first_0;
            	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
            	        retval.tree = (CommonTree)adaptor.getParent(retval.tree);}
            	    }
            	    break;
            	case 3 :
            	    // IbmSparqlAstRewriter.g:72:11: ( ^( N_GROUP_GRAPH_PATTERN ( . )+ ) )=> (x+= . )
            	    {
            	    // IbmSparqlAstRewriter.g:72:43: (x+= . )
            	    // IbmSparqlAstRewriter.g:72:44: x+= .
            	    {
            	    _last = (CommonTree)input.LT(1);
            	    x=(CommonTree)input.LT(1);
            	    matchAny(input); if (state.failed) return retval;
            	     
            	    if ( state.backtracking==1 )
            	    if ( _first_1==null ) _first_1 = x;
            	    if (list_x==null) list_x=new ArrayList();
            	    list_x.add(x);

            	    if ( state.backtracking==1 ) {
            	      g++;
            	    }

            	    if ( state.backtracking==1 ) {
            	    retval.tree = (CommonTree)_first_0;
            	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
            	        retval.tree = (CommonTree)adaptor.getParent(retval.tree);}
            	    }


            	    if ( state.backtracking==1 ) {
            	    retval.tree = (CommonTree)_first_0;
            	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
            	        retval.tree = (CommonTree)adaptor.getParent(retval.tree);}
            	    }
            	    break;
            	case 4 :
            	    // IbmSparqlAstRewriter.g:73:11: (x+= . )
            	    {
            	    // IbmSparqlAstRewriter.g:73:11: (x+= . )
            	    // IbmSparqlAstRewriter.g:73:13: x+= .
            	    {
            	    _last = (CommonTree)input.LT(1);
            	    x=(CommonTree)input.LT(1);
            	    matchAny(input); if (state.failed) return retval;
            	     
            	    if ( state.backtracking==1 )
            	    if ( _first_1==null ) _first_1 = x;
            	    if (list_x==null) list_x=new ArrayList();
            	    list_x.add(x);

            	    if ( state.backtracking==1 ) {
            	      c++;
            	    }

            	    if ( state.backtracking==1 ) {
            	    retval.tree = (CommonTree)_first_0;
            	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
            	        retval.tree = (CommonTree)adaptor.getParent(retval.tree);}
            	    }


            	    if ( state.backtracking==1 ) {
            	    retval.tree = (CommonTree)_first_0;
            	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
            	        retval.tree = (CommonTree)adaptor.getParent(retval.tree);}
            	    }
            	    break;

            	default :
            	    if ( cnt2 >= 1 ) break loop2;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(2, input);
                        throw eee;
                }
                cnt2++;
            } while (true);


            match(input, Token.UP, null); if (state.failed) return retval;_last = _save_last_1;
            }



            // AST REWRITE
            // elements: x, x, GROUP_GRAPH_PATTERN
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: x
            if ( state.backtracking==1 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_x=new RewriteRuleSubtreeStream(adaptor,"wildcard x",list_x);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 77:3: -> {(c == 0)&&(t == 0)&&(g == 0)}? GROUP_GRAPH_PATTERN
            if ((c == 0)&&(t == 0)&&(g == 0)) {
                adaptor.addChild(root_0, stream_GROUP_GRAPH_PATTERN.nextNode());

            }
            else // 78:3: -> {(c == 0)&&(t == 0)&&(g > 0)}? ( $x)+
            if ((c == 0)&&(t == 0)&&(g > 0)) {
                if ( !(stream_x.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_x.hasNext() ) {
                    adaptor.addChild(root_0, stream_x.nextTree());

                }
                stream_x.reset();

            }
            else // 79:3: -> ^( N_GROUP_GRAPH_PATTERN ( $x)* )
            {
                // IbmSparqlAstRewriter.g:79:15: ^( N_GROUP_GRAPH_PATTERN ( $x)* )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(N_GROUP_GRAPH_PATTERN, "N_GROUP_GRAPH_PATTERN"), root_1);

                // IbmSparqlAstRewriter.g:79:40: ( $x)*
                while ( stream_x.hasNext() ) {
                    adaptor.addChild(root_1, stream_x.nextTree());

                }
                stream_x.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            input.replaceChildren(adaptor.getParent(retval.start),
                                  adaptor.getChildIndex(retval.start),
                                  adaptor.getChildIndex(_last),
                                  retval.tree);}
            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "groupGraphPatternNull"

    public static class unionNull_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "unionNull"
    // IbmSparqlAstRewriter.g:82:1: unionNull : ^( UNION ( ( GROUP_GRAPH_PATTERN )=> GROUP_GRAPH_PATTERN | ( (x+= . ) ) )+ ) -> {c == 0}? GROUP_GRAPH_PATTERN -> {c == 1}? ( $x)+ -> ^( N_UNION ( $x)+ ) ;
    public final IbmSparqlAstRewriter.unionNull_return unionNull() throws RecognitionException {
        IbmSparqlAstRewriter.unionNull_return retval = new IbmSparqlAstRewriter.unionNull_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree UNION8=null;
        CommonTree GROUP_GRAPH_PATTERN9=null;
        CommonTree x=null;
        List list_x=null;

        CommonTree UNION8_tree=null;
        CommonTree GROUP_GRAPH_PATTERN9_tree=null;
        CommonTree x_tree=null;
        RewriteRuleNodeStream stream_GROUP_GRAPH_PATTERN=new RewriteRuleNodeStream(adaptor,"token GROUP_GRAPH_PATTERN");
        RewriteRuleNodeStream stream_UNION=new RewriteRuleNodeStream(adaptor,"token UNION");


        		int c = 0;
        	
        try {
            // IbmSparqlAstRewriter.g:86:2: ( ^( UNION ( ( GROUP_GRAPH_PATTERN )=> GROUP_GRAPH_PATTERN | ( (x+= . ) ) )+ ) -> {c == 0}? GROUP_GRAPH_PATTERN -> {c == 1}? ( $x)+ -> ^( N_UNION ( $x)+ ) )
            // IbmSparqlAstRewriter.g:86:4: ^( UNION ( ( GROUP_GRAPH_PATTERN )=> GROUP_GRAPH_PATTERN | ( (x+= . ) ) )+ )
            {
            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_1 = _last;
            CommonTree _first_1 = null;
            _last = (CommonTree)input.LT(1);
            UNION8=(CommonTree)match(input,UNION,FOLLOW_UNION_in_unionNull318); if (state.failed) return retval; 
            if ( state.backtracking==1 ) stream_UNION.add(UNION8);


            if ( state.backtracking==1 )
            if ( _first_0==null ) _first_0 = UNION8;
            match(input, Token.DOWN, null); if (state.failed) return retval;
            // IbmSparqlAstRewriter.g:86:12: ( ( GROUP_GRAPH_PATTERN )=> GROUP_GRAPH_PATTERN | ( (x+= . ) ) )+
            int cnt3=0;
            loop3:
            do {
                int alt3=3;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==GROUP_GRAPH_PATTERN) ) {
                    int LA3_2 = input.LA(2);

                    if ( (synpred4_IbmSparqlAstRewriter()) ) {
                        alt3=1;
                    }
                    else if ( (true) ) {
                        alt3=2;
                    }


                }
                else if ( ((LA3_0>=PATH && LA3_0<=NOT_IN)||(LA3_0>=GROUP_GRAPH_PATTERN_SUB && LA3_0<=N_UNION)) ) {
                    alt3=2;
                }


                switch (alt3) {
            	case 1 :
            	    // IbmSparqlAstRewriter.g:86:16: ( GROUP_GRAPH_PATTERN )=> GROUP_GRAPH_PATTERN
            	    {
            	    _last = (CommonTree)input.LT(1);
            	    GROUP_GRAPH_PATTERN9=(CommonTree)match(input,GROUP_GRAPH_PATTERN,FOLLOW_GROUP_GRAPH_PATTERN_in_unionNull329); if (state.failed) return retval; 
            	    if ( state.backtracking==1 ) stream_GROUP_GRAPH_PATTERN.add(GROUP_GRAPH_PATTERN9);


            	    if ( state.backtracking==1 ) {
            	    retval.tree = (CommonTree)_first_0;
            	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
            	        retval.tree = (CommonTree)adaptor.getParent(retval.tree);}
            	    }
            	    break;
            	case 2 :
            	    // IbmSparqlAstRewriter.g:87:7: ( (x+= . ) )
            	    {
            	    // IbmSparqlAstRewriter.g:87:7: ( (x+= . ) )
            	    // IbmSparqlAstRewriter.g:87:9: (x+= . )
            	    {
            	    // IbmSparqlAstRewriter.g:87:9: (x+= . )
            	    // IbmSparqlAstRewriter.g:87:11: x+= .
            	    {
            	    _last = (CommonTree)input.LT(1);
            	    x=(CommonTree)input.LT(1);
            	    matchAny(input); if (state.failed) return retval;
            	     
            	    if ( state.backtracking==1 )
            	    if ( _first_1==null ) _first_1 = x;
            	    if (list_x==null) list_x=new ArrayList();
            	    list_x.add(x);


            	    if ( state.backtracking==1 ) {
            	    retval.tree = (CommonTree)_first_0;
            	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
            	        retval.tree = (CommonTree)adaptor.getParent(retval.tree);}
            	    }

            	    if ( state.backtracking==1 ) {
            	      c++;
            	    }

            	    if ( state.backtracking==1 ) {
            	    retval.tree = (CommonTree)_first_0;
            	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
            	        retval.tree = (CommonTree)adaptor.getParent(retval.tree);}
            	    }


            	    if ( state.backtracking==1 ) {
            	    retval.tree = (CommonTree)_first_0;
            	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
            	        retval.tree = (CommonTree)adaptor.getParent(retval.tree);}
            	    }
            	    break;

            	default :
            	    if ( cnt3 >= 1 ) break loop3;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(3, input);
                        throw eee;
                }
                cnt3++;
            } while (true);


            match(input, Token.UP, null); if (state.failed) return retval;_last = _save_last_1;
            }



            // AST REWRITE
            // elements: x, GROUP_GRAPH_PATTERN, x
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: x
            if ( state.backtracking==1 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_x=new RewriteRuleSubtreeStream(adaptor,"wildcard x",list_x);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 91:3: -> {c == 0}? GROUP_GRAPH_PATTERN
            if (c == 0) {
                adaptor.addChild(root_0, stream_GROUP_GRAPH_PATTERN.nextNode());

            }
            else // 92:3: -> {c == 1}? ( $x)+
            if (c == 1) {
                if ( !(stream_x.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_x.hasNext() ) {
                    adaptor.addChild(root_0, stream_x.nextTree());

                }
                stream_x.reset();

            }
            else // 93:3: -> ^( N_UNION ( $x)+ )
            {
                // IbmSparqlAstRewriter.g:93:9: ^( N_UNION ( $x)+ )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(N_UNION, "N_UNION"), root_1);

                if ( !(stream_x.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_x.hasNext() ) {
                    adaptor.addChild(root_1, stream_x.nextTree());

                }
                stream_x.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            input.replaceChildren(adaptor.getParent(retval.start),
                                  adaptor.getChildIndex(retval.start),
                                  adaptor.getChildIndex(_last),
                                  retval.tree);}
            }

        }

        	catch(RecognitionException re) {
        		throw new SPARQLsyntaxError(re);
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "unionNull"

    // $ANTLR start synpred1_IbmSparqlAstRewriter
    public final void synpred1_IbmSparqlAstRewriter_fragment() throws RecognitionException {   
        // IbmSparqlAstRewriter.g:70:30: ( GROUP_GRAPH_PATTERN )
        // IbmSparqlAstRewriter.g:70:31: GROUP_GRAPH_PATTERN
        {
        match(input,GROUP_GRAPH_PATTERN,FOLLOW_GROUP_GRAPH_PATTERN_in_synpred1_IbmSparqlAstRewriter156); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred1_IbmSparqlAstRewriter

    // $ANTLR start synpred2_IbmSparqlAstRewriter
    public final void synpred2_IbmSparqlAstRewriter_fragment() throws RecognitionException {   
        // IbmSparqlAstRewriter.g:71:11: ( ^( TRIPLES_BLOCK ( . )+ ) )
        // IbmSparqlAstRewriter.g:71:12: ^( TRIPLES_BLOCK ( . )+ )
        {
        match(input,TRIPLES_BLOCK,FOLLOW_TRIPLES_BLOCK_in_synpred2_IbmSparqlAstRewriter175); if (state.failed) return ;

        match(input, Token.DOWN, null); if (state.failed) return ;
        // IbmSparqlAstRewriter.g:71:28: ( . )+
        int cnt4=0;
        loop4:
        do {
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( ((LA4_0>=PATH && LA4_0<=N_UNION)) ) {
                alt4=1;
            }
            else if ( (LA4_0==UP) ) {
                alt4=2;
            }


            switch (alt4) {
        	case 1 :
        	    // IbmSparqlAstRewriter.g:71:28: .
        	    {
        	    matchAny(input); if (state.failed) return ;

        	    }
        	    break;

        	default :
        	    if ( cnt4 >= 1 ) break loop4;
        	    if (state.backtracking>0) {state.failed=true; return ;}
                    EarlyExitException eee =
                        new EarlyExitException(4, input);
                    throw eee;
            }
            cnt4++;
        } while (true);


        match(input, Token.UP, null); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred2_IbmSparqlAstRewriter

    // $ANTLR start synpred3_IbmSparqlAstRewriter
    public final void synpred3_IbmSparqlAstRewriter_fragment() throws RecognitionException {   
        // IbmSparqlAstRewriter.g:72:11: ( ^( N_GROUP_GRAPH_PATTERN ( . )+ ) )
        // IbmSparqlAstRewriter.g:72:12: ^( N_GROUP_GRAPH_PATTERN ( . )+ )
        {
        match(input,N_GROUP_GRAPH_PATTERN,FOLLOW_N_GROUP_GRAPH_PATTERN_in_synpred3_IbmSparqlAstRewriter203); if (state.failed) return ;

        match(input, Token.DOWN, null); if (state.failed) return ;
        // IbmSparqlAstRewriter.g:72:36: ( . )+
        int cnt5=0;
        loop5:
        do {
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( ((LA5_0>=PATH && LA5_0<=N_UNION)) ) {
                alt5=1;
            }
            else if ( (LA5_0==UP) ) {
                alt5=2;
            }


            switch (alt5) {
        	case 1 :
        	    // IbmSparqlAstRewriter.g:72:36: .
        	    {
        	    matchAny(input); if (state.failed) return ;

        	    }
        	    break;

        	default :
        	    if ( cnt5 >= 1 ) break loop5;
        	    if (state.backtracking>0) {state.failed=true; return ;}
                    EarlyExitException eee =
                        new EarlyExitException(5, input);
                    throw eee;
            }
            cnt5++;
        } while (true);


        match(input, Token.UP, null); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred3_IbmSparqlAstRewriter

    // $ANTLR start synpred4_IbmSparqlAstRewriter
    public final void synpred4_IbmSparqlAstRewriter_fragment() throws RecognitionException {   
        // IbmSparqlAstRewriter.g:86:16: ( GROUP_GRAPH_PATTERN )
        // IbmSparqlAstRewriter.g:86:17: GROUP_GRAPH_PATTERN
        {
        match(input,GROUP_GRAPH_PATTERN,FOLLOW_GROUP_GRAPH_PATTERN_in_synpred4_IbmSparqlAstRewriter325); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred4_IbmSparqlAstRewriter

    // Delegated rules

    public final boolean synpred4_IbmSparqlAstRewriter() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred4_IbmSparqlAstRewriter_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred3_IbmSparqlAstRewriter() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred3_IbmSparqlAstRewriter_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred1_IbmSparqlAstRewriter() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred1_IbmSparqlAstRewriter_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred2_IbmSparqlAstRewriter() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred2_IbmSparqlAstRewriter_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


 

    public static final BitSet FOLLOW_groupGraphPatternNull_in_bottomup97 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unionNull_in_bottomup102 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_whereNull_in_bottomup107 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHERE_in_whereNull120 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_GROUP_GRAPH_PATTERN_in_whereNull122 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_GROUP_GRAPH_PATTERN_in_groupGraphPatternNull149 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_GROUP_GRAPH_PATTERN_in_groupGraphPatternNull160 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF8L,0xFFFFFFFFFFFFFFFFL,0xFFFFFFFFFFFFFFFFL,0xFFFFFFFFFFFFFFFFL,0x00000000000007FFL});
    public static final BitSet FOLLOW_UNION_in_unionNull318 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_GROUP_GRAPH_PATTERN_in_unionNull329 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF8L,0xFFFFFFFFFFFFFFFFL,0xFFFFFFFFFFFFFFFFL,0xFFFFFFFFFFFFFFFFL,0x00000000000007FFL});
    public static final BitSet FOLLOW_GROUP_GRAPH_PATTERN_in_synpred1_IbmSparqlAstRewriter156 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRIPLES_BLOCK_in_synpred2_IbmSparqlAstRewriter175 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_N_GROUP_GRAPH_PATTERN_in_synpred3_IbmSparqlAstRewriter203 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_GROUP_GRAPH_PATTERN_in_synpred4_IbmSparqlAstRewriter325 = new BitSet(new long[]{0x0000000000000002L});

}