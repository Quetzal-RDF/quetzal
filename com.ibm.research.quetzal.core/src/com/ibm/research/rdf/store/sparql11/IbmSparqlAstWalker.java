// $ANTLR 3.3 Nov 30, 2010 12:50:56 IbmSparqlAstWalker.g 2014-07-17 09:08:09
 
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
public class IbmSparqlAstWalker extends TreeParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "PATH", "ALT", "SEQ", "ELT", "INV", "BROKEN_PLUS", "BROKEN_MINUS", "NIL", "ANNON", "ROOT", "PROLOGUE", "DEFAULT_NAMESPACE", "NAMESPACE_PREFIX_MAP", "KEY", "QUERY", "UPDATE", "TYPE", "PVARS", "EXP", "NOT_IN", "GROUP_GRAPH_PATTERN", "GROUP_GRAPH_PATTERN_SUB", "GRAPH_GRAPH_PATTERN", "SUB_SELECT", "TRIPLES_BLOCK", "NON_TRIPLES", "TRIPLE", "TRIPLE2", "TRIPLES_SAME_SUBJECT", "GRAPH_NODE", "VAR", "PREFIXED_NAME", "PREFIXED_NS", "FUNCTION", "EXPRESSION", "NOT_EXISTS", "IRI_OR_FUNCTION", "DATASET", "GROUP_BY", "ORDER_BY", "CONDITION", "BIND_VALUES", "STRING", "BOOLEAN", "NUMERIC", "SUBJECT", "PREDICATE", "VALUE", "TRIPLES_NODE_PROPERTY_LIST", "TRIPLES_NODE", "COLLECTION", "PROPERTY_LIST", "PREDICATE_VALUE", "WHERE", "IRI_REF", "LTE", "MODIFIERS", "BIG_INTEGER", "BIG_DECIMAL", "INLINE_DATA", "BASE", "PREFIX", "PNAME_NS", "SELECT", "DISTINCT", "REDUCED", "OPEN_BRACE", "AS", "CLOSE_BRACE", "CONSTRUCT", "WHERE_TOKEN", "OPEN_CURLY_BRACE", "CLOSE_CURLY_BRACE", "DESCRIBE", "ASK", "FROM", "NAMED", "GROUP", "BY", "HAVING", "ORDER", "ASC", "DESC", "LIMIT", "INTEGER", "OFFSET", "BINDINGS", "UNDEF", "SEMICOLON", "LOAD", "SILENT", "INTO", "CLEAR", "DROP", "CREATE", "ADD", "TO", "MOVE", "COPY", "INSERT", "DATA", "DELETE", "WITH", "USING", "DEFAULT", "GRAPH", "ALL", "DOT", "VALUES", "OPTIONAL", "SERVICE", "BIND", "UNION", "MINUS", "FILTER", "COMMA", "OPEN_SQ_BRACKET", "CLOSE_SQ_BRACKET", "VAR1", "VAR2", "LOGICAL_OR", "LOGICAL_AND", "LT", "IN", "NOT", "STR", "LANG", "LANGMATCHES", "DATATYPE", "BOUND", "IRI", "URI", "BNODE", "RAND", "ABS", "CEIL", "FLOOR", "ROUND", "CONCAT", "STRLEN", "UCASE", "LCASE", "ENCODE_FOR_URI", "CONTAINS", "STRSTARTS", "STRENDS", "STRBEFORE", "STRAFTER", "YEAR", "MONTH", "DAY", "HOURS", "MINUTES", "SECONDS", "TIMEZONE", "TZ", "NOW", "UUID", "STRUUID", "MD5", "SHA1", "SHA224", "SHA256", "SHA384", "SHA512", "COALESCE", "IF", "STRLANG", "STRDT", "SAMETERM", "ISIRI", "ISURI", "ISBLANK", "ISLITERAL", "ISNUMERIC", "REGEX", "SUBSTR", "REPLACE", "EXISTS", "COUNT", "SUM", "MIN", "MAX", "AVG", "SAMPLE", "GROUP_CONCAT", "SEPARATOR", "LANGTAG", "DECIMAL", "DOUBLE", "INTEGER_POSITIVE", "DECIMAL_POSITIVE", "DOUBLE_POSITIVE", "INTEGER_NEGATIVE", "DECIMAL_NEGATIVE", "DOUBLE_NEGATIVE", "TRUE", "FALSE", "STRING_LITERAL1", "STRING_LITERAL2", "STRING_LITERAL_LONG1", "STRING_LITERAL_LONG2", "PNAME_LN", "BLANK_NODE_LABEL", "B", "A", "S", "E", "P", "R", "F", "I", "X", "L", "C", "T", "D", "N", "U", "O", "W", "H", "K", "M", "G", "Y", "V", "Z", "UNICODE_ESCAPE", "PN_PREFIX", "PN_LOCAL", "VARNAME", "DIGIT", "HEXDIGIT", "EXPONENT", "ECHAR", "WS", "EOL", "COMMENT", "PN_CHARS_BASE", "PN_CHARS_U", "PN_CHARS", "PLX", "PERCENT", "PN_LOCAL_ESC", "J", "Q", "'*'", "'a'", "'|'", "'/'", "'^'", "'?'", "'+'", "'!'", "'='", "'!='", "'>'", "'>='", "'-'", "'^^'"
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

    // delegates
    // delegators


        public IbmSparqlAstWalker(TreeNodeStream input) {
            this(input, new RecognizerSharedState());
        }
        public IbmSparqlAstWalker(TreeNodeStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return IbmSparqlAstWalker.tokenNames; }
    public String getGrammarFileName() { return "IbmSparqlAstWalker.g"; }


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
    // IbmSparqlAstWalker.g:62:1: queryUnit returns [Query q] : ^( ROOT x= query ) ;
    public final Query queryUnit() throws RecognitionException {
        Query q = null;

        Query x = null;


        try {
            // IbmSparqlAstWalker.g:63:2: ( ^( ROOT x= query ) )
            // IbmSparqlAstWalker.g:63:8: ^( ROOT x= query )
            {
            match(input,ROOT,FOLLOW_ROOT_in_queryUnit83); if (state.failed) return q;

            match(input, Token.DOWN, null); if (state.failed) return q;
            pushFollow(FOLLOW_query_in_queryUnit87);
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
    // IbmSparqlAstWalker.g:66:1: query returns [Query q] : ^( QUERY (p= prologue ) ( (s= selectQuery (b= bindingsClause )? ) | (c= constructQuery ) | (d= describeQuery ) | (a= askQuery ) ) ) ;
    public final Query query() throws RecognitionException {
        Query q = null;

        QueryPrologue p = null;

        SelectQuery s = null;

        Pattern b = null;

        ConstructQuery c = null;

        DescribeQuery d = null;

        AskQuery a = null;



        		
        	
        try {
            // IbmSparqlAstWalker.g:70:2: ( ^( QUERY (p= prologue ) ( (s= selectQuery (b= bindingsClause )? ) | (c= constructQuery ) | (d= describeQuery ) | (a= askQuery ) ) ) )
            // IbmSparqlAstWalker.g:70:4: ^( QUERY (p= prologue ) ( (s= selectQuery (b= bindingsClause )? ) | (c= constructQuery ) | (d= describeQuery ) | (a= askQuery ) ) )
            {
            match(input,QUERY,FOLLOW_QUERY_in_query115); if (state.failed) return q;

            match(input, Token.DOWN, null); if (state.failed) return q;
            // IbmSparqlAstWalker.g:71:4: (p= prologue )
            // IbmSparqlAstWalker.g:71:6: p= prologue
            {
            pushFollow(FOLLOW_prologue_in_query125);
            p=prologue();

            state._fsp--;
            if (state.failed) return q;

            }

            // IbmSparqlAstWalker.g:72:4: ( (s= selectQuery (b= bindingsClause )? ) | (c= constructQuery ) | (d= describeQuery ) | (a= askQuery ) )
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
                    // IbmSparqlAstWalker.g:72:6: (s= selectQuery (b= bindingsClause )? )
                    {
                    // IbmSparqlAstWalker.g:72:6: (s= selectQuery (b= bindingsClause )? )
                    // IbmSparqlAstWalker.g:72:8: s= selectQuery (b= bindingsClause )?
                    {
                    pushFollow(FOLLOW_selectQuery_in_query144);
                    s=selectQuery();

                    state._fsp--;
                    if (state.failed) return q;
                    if ( state.backtracking==0 ) {
                       q = new Query(p,s); 
                    }
                    // IbmSparqlAstWalker.g:73:8: (b= bindingsClause )?
                    int alt1=2;
                    int LA1_0 = input.LA(1);

                    if ( (LA1_0==VALUES) ) {
                        alt1=1;
                    }
                    switch (alt1) {
                        case 1 :
                            // IbmSparqlAstWalker.g:73:10: b= bindingsClause
                            {
                            pushFollow(FOLLOW_bindingsClause_in_query162);
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
                    // IbmSparqlAstWalker.g:85:6: (c= constructQuery )
                    {
                    // IbmSparqlAstWalker.g:85:6: (c= constructQuery )
                    // IbmSparqlAstWalker.g:85:8: c= constructQuery
                    {
                    pushFollow(FOLLOW_constructQuery_in_query219);
                    c=constructQuery();

                    state._fsp--;
                    if (state.failed) return q;
                    if ( state.backtracking==0 ) {
                       q = new Query(p,c); 
                    }

                    }


                    }
                    break;
                case 3 :
                    // IbmSparqlAstWalker.g:86:6: (d= describeQuery )
                    {
                    // IbmSparqlAstWalker.g:86:6: (d= describeQuery )
                    // IbmSparqlAstWalker.g:86:8: d= describeQuery
                    {
                    pushFollow(FOLLOW_describeQuery_in_query235);
                    d=describeQuery();

                    state._fsp--;
                    if (state.failed) return q;
                    if ( state.backtracking==0 ) {
                       q = new Query(p,d); 
                    }

                    }


                    }
                    break;
                case 4 :
                    // IbmSparqlAstWalker.g:87:6: (a= askQuery )
                    {
                    // IbmSparqlAstWalker.g:87:6: (a= askQuery )
                    // IbmSparqlAstWalker.g:87:8: a= askQuery
                    {
                    pushFollow(FOLLOW_askQuery_in_query251);
                    a=askQuery();

                    state._fsp--;
                    if (state.failed) return q;
                    if ( state.backtracking==0 ) {
                       q = new Query(p,a); 
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
    // IbmSparqlAstWalker.g:100:1: prologue returns [QueryPrologue qp] : ^( PROLOGUE ( baseDecl[$qp] )* ( prefixDecl[$qp] )* ) ;
    public final QueryPrologue prologue() throws RecognitionException {
        QueryPrologue qp = null;

         qp = new QueryPrologue(); 
        try {
            // IbmSparqlAstWalker.g:102:2: ( ^( PROLOGUE ( baseDecl[$qp] )* ( prefixDecl[$qp] )* ) )
            // IbmSparqlAstWalker.g:103:3: ^( PROLOGUE ( baseDecl[$qp] )* ( prefixDecl[$qp] )* )
            {
            match(input,PROLOGUE,FOLLOW_PROLOGUE_in_prologue307); if (state.failed) return qp;

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); if (state.failed) return qp;
                // IbmSparqlAstWalker.g:103:14: ( baseDecl[$qp] )*
                loop3:
                do {
                    int alt3=2;
                    int LA3_0 = input.LA(1);

                    if ( (LA3_0==BASE) ) {
                        alt3=1;
                    }


                    switch (alt3) {
                	case 1 :
                	    // IbmSparqlAstWalker.g:103:14: baseDecl[$qp]
                	    {
                	    pushFollow(FOLLOW_baseDecl_in_prologue309);
                	    baseDecl(qp);

                	    state._fsp--;
                	    if (state.failed) return qp;

                	    }
                	    break;

                	default :
                	    break loop3;
                    }
                } while (true);

                // IbmSparqlAstWalker.g:103:30: ( prefixDecl[$qp] )*
                loop4:
                do {
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==PREFIX) ) {
                        alt4=1;
                    }


                    switch (alt4) {
                	case 1 :
                	    // IbmSparqlAstWalker.g:103:30: prefixDecl[$qp]
                	    {
                	    pushFollow(FOLLOW_prefixDecl_in_prologue314);
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
    // IbmSparqlAstWalker.g:107:1: baseDecl[QueryPrologue qp] : ^( BASE i= iRIref ) ;
    public final void baseDecl(QueryPrologue qp) throws RecognitionException {
        IRI i = null;


        try {
            // IbmSparqlAstWalker.g:108:2: ( ^( BASE i= iRIref ) )
            // IbmSparqlAstWalker.g:109:4: ^( BASE i= iRIref )
            {
            match(input,BASE,FOLLOW_BASE_in_baseDecl345); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            pushFollow(FOLLOW_iRIref_in_baseDecl349);
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
    // IbmSparqlAstWalker.g:113:1: prefixDecl[QueryPrologue qp] : ^( PREFIX n= prefixedName v= iRIref ) ;
    public final void prefixDecl(QueryPrologue qp) throws RecognitionException {
        String n = null;

        IRI v = null;


        try {
            // IbmSparqlAstWalker.g:114:2: ( ^( PREFIX n= prefixedName v= iRIref ) )
            // IbmSparqlAstWalker.g:115:3: ^( PREFIX n= prefixedName v= iRIref )
            {
            match(input,PREFIX,FOLLOW_PREFIX_in_prefixDecl377); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            pushFollow(FOLLOW_prefixedName_in_prefixDecl381);
            n=prefixedName();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_iRIref_in_prefixDecl386);
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
    // IbmSparqlAstWalker.g:119:1: selectQuery returns [SelectQuery sq] : ^( SELECT (s= selectClause ) (d= dataset )* (w= whereClause )? (m= solutionModifier ) ) ;
    public final SelectQuery selectQuery() throws RecognitionException {
        SelectQuery sq = null;

        SelectClause s = null;

        List<DatasetClause> d = null;

        Pattern w = null;

        SolutionModifiers m = null;


         sq = new SelectQuery(); 
        try {
            // IbmSparqlAstWalker.g:121:2: ( ^( SELECT (s= selectClause ) (d= dataset )* (w= whereClause )? (m= solutionModifier ) ) )
            // IbmSparqlAstWalker.g:122:3: ^( SELECT (s= selectClause ) (d= dataset )* (w= whereClause )? (m= solutionModifier ) )
            {
            match(input,SELECT,FOLLOW_SELECT_in_selectQuery424); if (state.failed) return sq;

            match(input, Token.DOWN, null); if (state.failed) return sq;
            // IbmSparqlAstWalker.g:123:4: (s= selectClause )
            // IbmSparqlAstWalker.g:123:5: s= selectClause
            {
            pushFollow(FOLLOW_selectClause_in_selectQuery434);
            s=selectClause();

            state._fsp--;
            if (state.failed) return sq;
            if ( state.backtracking==0 ) {
               sq.setSelectClause(s);      
            }

            }

            // IbmSparqlAstWalker.g:124:4: (d= dataset )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==DATASET) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // IbmSparqlAstWalker.g:124:5: d= dataset
            	    {
            	    pushFollow(FOLLOW_dataset_in_selectQuery450);
            	    d=dataset();

            	    state._fsp--;
            	    if (state.failed) return sq;
            	    if ( state.backtracking==0 ) {
            	       sq.setDatasetClauses(d);    
            	    }

            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);

            // IbmSparqlAstWalker.g:125:4: (w= whereClause )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==WHERE) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // IbmSparqlAstWalker.g:125:5: w= whereClause
                    {
                    pushFollow(FOLLOW_whereClause_in_selectQuery469);
                    w=whereClause();

                    state._fsp--;
                    if (state.failed) return sq;
                    if ( state.backtracking==0 ) {
                       sq.setGraphPattern(w);      
                    }

                    }
                    break;

            }

            // IbmSparqlAstWalker.g:126:4: (m= solutionModifier )
            // IbmSparqlAstWalker.g:126:5: m= solutionModifier
            {
            pushFollow(FOLLOW_solutionModifier_in_selectQuery485);
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


    // $ANTLR start "dataset"
    // IbmSparqlAstWalker.g:130:1: dataset returns [List<DatasetClause> dcl] : ^( DATASET (dc= datasetClause )+ ) ;
    public final List<DatasetClause> dataset() throws RecognitionException {
        List<DatasetClause> dcl = null;

        DatasetClause dc = null;


         dcl = new ArrayList<DatasetClause>(); 
        try {
            // IbmSparqlAstWalker.g:132:2: ( ^( DATASET (dc= datasetClause )+ ) )
            // IbmSparqlAstWalker.g:133:3: ^( DATASET (dc= datasetClause )+ )
            {
            match(input,DATASET,FOLLOW_DATASET_in_dataset524); if (state.failed) return dcl;

            match(input, Token.DOWN, null); if (state.failed) return dcl;
            // IbmSparqlAstWalker.g:134:4: (dc= datasetClause )+
            int cnt7=0;
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( (LA7_0==FROM) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // IbmSparqlAstWalker.g:134:5: dc= datasetClause
            	    {
            	    pushFollow(FOLLOW_datasetClause_in_dataset533);
            	    dc=datasetClause();

            	    state._fsp--;
            	    if (state.failed) return dcl;
            	    if ( state.backtracking==0 ) {
            	      dcl.add(dc);
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt7 >= 1 ) break loop7;
            	    if (state.backtracking>0) {state.failed=true; return dcl;}
                        EarlyExitException eee =
                            new EarlyExitException(7, input);
                        throw eee;
                }
                cnt7++;
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
    // IbmSparqlAstWalker.g:138:1: subSelect returns [SubSelectPattern sp] : ^( SUB_SELECT (s= selectClause ) (w= whereClause )? (m= solutionModifier ) (d= inlineData )? ) ;
    public final SubSelectPattern subSelect() throws RecognitionException {
        SubSelectPattern sp = null;

        SelectClause s = null;

        Pattern w = null;

        SolutionModifiers m = null;

        ValuesPattern d = null;


         
        		sp = new SubSelectPattern();
        	
        try {
            // IbmSparqlAstWalker.g:142:2: ( ^( SUB_SELECT (s= selectClause ) (w= whereClause )? (m= solutionModifier ) (d= inlineData )? ) )
            // IbmSparqlAstWalker.g:142:6: ^( SUB_SELECT (s= selectClause ) (w= whereClause )? (m= solutionModifier ) (d= inlineData )? )
            {
            match(input,SUB_SELECT,FOLLOW_SUB_SELECT_in_subSelect567); if (state.failed) return sp;

            match(input, Token.DOWN, null); if (state.failed) return sp;
            // IbmSparqlAstWalker.g:143:4: (s= selectClause )
            // IbmSparqlAstWalker.g:143:5: s= selectClause
            {
            pushFollow(FOLLOW_selectClause_in_subSelect576);
            s=selectClause();

            state._fsp--;
            if (state.failed) return sp;
            if ( state.backtracking==0 ) {
               sp.setSelectClause(s);      
            }

            }

            // IbmSparqlAstWalker.g:144:4: (w= whereClause )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==WHERE) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // IbmSparqlAstWalker.g:144:5: w= whereClause
                    {
                    pushFollow(FOLLOW_whereClause_in_subSelect592);
                    w=whereClause();

                    state._fsp--;
                    if (state.failed) return sp;
                    if ( state.backtracking==0 ) {
                       sp.setGraphPattern(w);      
                    }

                    }
                    break;

            }

            // IbmSparqlAstWalker.g:145:4: (m= solutionModifier )
            // IbmSparqlAstWalker.g:145:5: m= solutionModifier
            {
            pushFollow(FOLLOW_solutionModifier_in_subSelect608);
            m=solutionModifier();

            state._fsp--;
            if (state.failed) return sp;
            if ( state.backtracking==0 ) {
               sp.setSolutionModifier(m);  
            }

            }

            // IbmSparqlAstWalker.g:146:13: (d= inlineData )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==VALUES) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // IbmSparqlAstWalker.g:146:14: d= inlineData
                    {
                    pushFollow(FOLLOW_inlineData_in_subSelect634);
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
    // IbmSparqlAstWalker.g:150:1: selectClause returns [SelectClause sc] : ( ^( TYPE ( DISTINCT | REDUCED ) ) )? ^( PVARS ( (v= var | expVar[$sc] | fexp[$sc] )* | '*' ) ) ;
    public final SelectClause selectClause() throws RecognitionException {
        SelectClause sc = null;

        String v = null;


         sc = new SelectClause(); 
        try {
            // IbmSparqlAstWalker.g:152:2: ( ( ^( TYPE ( DISTINCT | REDUCED ) ) )? ^( PVARS ( (v= var | expVar[$sc] | fexp[$sc] )* | '*' ) ) )
            // IbmSparqlAstWalker.g:153:3: ( ^( TYPE ( DISTINCT | REDUCED ) ) )? ^( PVARS ( (v= var | expVar[$sc] | fexp[$sc] )* | '*' ) )
            {
            // IbmSparqlAstWalker.g:153:3: ( ^( TYPE ( DISTINCT | REDUCED ) ) )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==TYPE) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // IbmSparqlAstWalker.g:153:4: ^( TYPE ( DISTINCT | REDUCED ) )
                    {
                    match(input,TYPE,FOLLOW_TYPE_in_selectClause680); if (state.failed) return sc;

                    match(input, Token.DOWN, null); if (state.failed) return sc;
                    // IbmSparqlAstWalker.g:153:11: ( DISTINCT | REDUCED )
                    int alt10=2;
                    int LA10_0 = input.LA(1);

                    if ( (LA10_0==DISTINCT) ) {
                        alt10=1;
                    }
                    else if ( (LA10_0==REDUCED) ) {
                        alt10=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return sc;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 10, 0, input);

                        throw nvae;
                    }
                    switch (alt10) {
                        case 1 :
                            // IbmSparqlAstWalker.g:153:13: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_selectClause684); if (state.failed) return sc;
                            if ( state.backtracking==0 ) {
                               sc.setSelectModifier(SelectClause.ESelectModifier.DISTINCT); 
                            }

                            }
                            break;
                        case 2 :
                            // IbmSparqlAstWalker.g:154:7: REDUCED
                            {
                            match(input,REDUCED,FOLLOW_REDUCED_in_selectClause696); if (state.failed) return sc;
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

            match(input,PVARS,FOLLOW_PVARS_in_selectClause725); if (state.failed) return sc;

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); if (state.failed) return sc;
                // IbmSparqlAstWalker.g:158:11: ( (v= var | expVar[$sc] | fexp[$sc] )* | '*' )
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( (LA13_0==UP||LA13_0==EXP||LA13_0==VAR||LA13_0==AS) ) {
                    alt13=1;
                }
                else if ( (LA13_0==251) ) {
                    alt13=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return sc;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 13, 0, input);

                    throw nvae;
                }
                switch (alt13) {
                    case 1 :
                        // IbmSparqlAstWalker.g:158:13: (v= var | expVar[$sc] | fexp[$sc] )*
                        {
                        // IbmSparqlAstWalker.g:158:13: (v= var | expVar[$sc] | fexp[$sc] )*
                        loop12:
                        do {
                            int alt12=4;
                            switch ( input.LA(1) ) {
                            case VAR:
                                {
                                alt12=1;
                                }
                                break;
                            case AS:
                                {
                                alt12=2;
                                }
                                break;
                            case EXP:
                                {
                                alt12=3;
                                }
                                break;

                            }

                            switch (alt12) {
                        	case 1 :
                        	    // IbmSparqlAstWalker.g:158:15: v= var
                        	    {
                        	    pushFollow(FOLLOW_var_in_selectClause733);
                        	    v=var();

                        	    state._fsp--;
                        	    if (state.failed) return sc;
                        	    if ( state.backtracking==0 ) {
                        	       sc.addProjectedVariable(new ProjectedVariable(v)); 
                        	    }

                        	    }
                        	    break;
                        	case 2 :
                        	    // IbmSparqlAstWalker.g:159:15: expVar[$sc]
                        	    {
                        	    pushFollow(FOLLOW_expVar_in_selectClause755);
                        	    expVar(sc);

                        	    state._fsp--;
                        	    if (state.failed) return sc;

                        	    }
                        	    break;
                        	case 3 :
                        	    // IbmSparqlAstWalker.g:160:15: fexp[$sc]
                        	    {
                        	    pushFollow(FOLLOW_fexp_in_selectClause778);
                        	    fexp(sc);

                        	    state._fsp--;
                        	    if (state.failed) return sc;

                        	    }
                        	    break;

                        	default :
                        	    break loop12;
                            }
                        } while (true);


                        }
                        break;
                    case 2 :
                        // IbmSparqlAstWalker.g:162:13: '*'
                        {
                        match(input,251,FOLLOW_251_in_selectClause818); if (state.failed) return sc;

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
    // IbmSparqlAstWalker.g:167:1: expVar[SelectClause sc] : ^( AS v= var e= expression ) ;
    public final void expVar(SelectClause sc) throws RecognitionException {
        String v = null;

        Expression e = null;


        try {
            // IbmSparqlAstWalker.g:168:2: ( ^( AS v= var e= expression ) )
            // IbmSparqlAstWalker.g:168:4: ^( AS v= var e= expression )
            {
            match(input,AS,FOLLOW_AS_in_expVar849); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            pushFollow(FOLLOW_var_in_expVar853);
            v=var();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_expression_in_expVar857);
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
    // IbmSparqlAstWalker.g:171:1: fexp[SelectClause sc] : ^( EXP e= expression ) ;
    public final void fexp(SelectClause sc) throws RecognitionException {
        Expression e = null;


        try {
            // IbmSparqlAstWalker.g:172:2: ( ^( EXP e= expression ) )
            // IbmSparqlAstWalker.g:172:4: ^( EXP e= expression )
            {
            match(input,EXP,FOLLOW_EXP_in_fexp875); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            pushFollow(FOLLOW_expression_in_fexp879);
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
    // IbmSparqlAstWalker.g:175:1: constructQuery returns [ConstructQuery cq] : ^( CONSTRUCT ( ( constructTemplate[$cq] (d= dataset )* (w= whereClause ) (m= solutionModifier ) ) | ( (d= dataset )* ( ^( WHERE triplesTemplate[$cq,p] ) )? (m= solutionModifier ) ) ) ) ;
    public final ConstructQuery constructQuery() throws RecognitionException {
        ConstructQuery cq = null;

        List<DatasetClause> d = null;

        Pattern w = null;

        SolutionModifiers m = null;


         
                PatternSet p = null;
        		cq = new ConstructQuery();
        	
        try {
            // IbmSparqlAstWalker.g:180:2: ( ^( CONSTRUCT ( ( constructTemplate[$cq] (d= dataset )* (w= whereClause ) (m= solutionModifier ) ) | ( (d= dataset )* ( ^( WHERE triplesTemplate[$cq,p] ) )? (m= solutionModifier ) ) ) ) )
            // IbmSparqlAstWalker.g:180:6: ^( CONSTRUCT ( ( constructTemplate[$cq] (d= dataset )* (w= whereClause ) (m= solutionModifier ) ) | ( (d= dataset )* ( ^( WHERE triplesTemplate[$cq,p] ) )? (m= solutionModifier ) ) ) )
            {
            match(input,CONSTRUCT,FOLLOW_CONSTRUCT_in_constructQuery910); if (state.failed) return cq;

            match(input, Token.DOWN, null); if (state.failed) return cq;
            // IbmSparqlAstWalker.g:180:20: ( ( constructTemplate[$cq] (d= dataset )* (w= whereClause ) (m= solutionModifier ) ) | ( (d= dataset )* ( ^( WHERE triplesTemplate[$cq,p] ) )? (m= solutionModifier ) ) )
            int alt17=2;
            alt17 = dfa17.predict(input);
            switch (alt17) {
                case 1 :
                    // IbmSparqlAstWalker.g:180:22: ( constructTemplate[$cq] (d= dataset )* (w= whereClause ) (m= solutionModifier ) )
                    {
                    // IbmSparqlAstWalker.g:180:22: ( constructTemplate[$cq] (d= dataset )* (w= whereClause ) (m= solutionModifier ) )
                    // IbmSparqlAstWalker.g:180:24: constructTemplate[$cq] (d= dataset )* (w= whereClause ) (m= solutionModifier )
                    {
                    pushFollow(FOLLOW_constructTemplate_in_constructQuery917);
                    constructTemplate(cq);

                    state._fsp--;
                    if (state.failed) return cq;
                    // IbmSparqlAstWalker.g:181:8: (d= dataset )*
                    loop14:
                    do {
                        int alt14=2;
                        int LA14_0 = input.LA(1);

                        if ( (LA14_0==DATASET) ) {
                            alt14=1;
                        }


                        switch (alt14) {
                    	case 1 :
                    	    // IbmSparqlAstWalker.g:181:9: d= dataset
                    	    {
                    	    pushFollow(FOLLOW_dataset_in_constructQuery931);
                    	    d=dataset();

                    	    state._fsp--;
                    	    if (state.failed) return cq;
                    	    if ( state.backtracking==0 ) {
                    	       cq.setDatasetClauses(d);    
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop14;
                        }
                    } while (true);

                    // IbmSparqlAstWalker.g:182:8: (w= whereClause )
                    // IbmSparqlAstWalker.g:182:9: w= whereClause
                    {
                    pushFollow(FOLLOW_whereClause_in_constructQuery954);
                    w=whereClause();

                    state._fsp--;
                    if (state.failed) return cq;
                    if ( state.backtracking==0 ) {
                       cq.setPattern(w);           
                    }

                    }

                    // IbmSparqlAstWalker.g:183:8: (m= solutionModifier )
                    // IbmSparqlAstWalker.g:183:9: m= solutionModifier
                    {
                    pushFollow(FOLLOW_solutionModifier_in_constructQuery973);
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
                    // IbmSparqlAstWalker.g:186:7: ( (d= dataset )* ( ^( WHERE triplesTemplate[$cq,p] ) )? (m= solutionModifier ) )
                    {
                    // IbmSparqlAstWalker.g:186:7: ( (d= dataset )* ( ^( WHERE triplesTemplate[$cq,p] ) )? (m= solutionModifier ) )
                    // IbmSparqlAstWalker.g:186:9: (d= dataset )* ( ^( WHERE triplesTemplate[$cq,p] ) )? (m= solutionModifier )
                    {
                    // IbmSparqlAstWalker.g:186:9: (d= dataset )*
                    loop15:
                    do {
                        int alt15=2;
                        int LA15_0 = input.LA(1);

                        if ( (LA15_0==DATASET) ) {
                            alt15=1;
                        }


                        switch (alt15) {
                    	case 1 :
                    	    // IbmSparqlAstWalker.g:186:10: d= dataset
                    	    {
                    	    pushFollow(FOLLOW_dataset_in_constructQuery1013);
                    	    d=dataset();

                    	    state._fsp--;
                    	    if (state.failed) return cq;
                    	    if ( state.backtracking==0 ) {
                    	       cq.setDatasetClauses(d);    
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop15;
                        }
                    } while (true);

                    // IbmSparqlAstWalker.g:187:8: ( ^( WHERE triplesTemplate[$cq,p] ) )?
                    int alt16=2;
                    int LA16_0 = input.LA(1);

                    if ( (LA16_0==WHERE) ) {
                        alt16=1;
                    }
                    switch (alt16) {
                        case 1 :
                            // IbmSparqlAstWalker.g:187:9: ^( WHERE triplesTemplate[$cq,p] )
                            {
                            match(input,WHERE,FOLLOW_WHERE_in_constructQuery1035); if (state.failed) return cq;

                            if ( state.backtracking==0 ) {
                                    
                                                               p = new PatternSet();
                                                             
                            }

                            match(input, Token.DOWN, null); if (state.failed) return cq;
                            pushFollow(FOLLOW_triplesTemplate_in_constructQuery1101);
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

                    // IbmSparqlAstWalker.g:197:8: (m= solutionModifier )
                    // IbmSparqlAstWalker.g:197:9: m= solutionModifier
                    {
                    pushFollow(FOLLOW_solutionModifier_in_constructQuery1181);
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
    // IbmSparqlAstWalker.g:203:1: describeQuery returns [DescribeQuery dq] : ^( DESCRIBE ( ( (v= varOrIRIref2 )+ | '*' ) (d= datasetClause )* (w= whereClause )? (s= solutionModifier ) ) ) ;
    public final DescribeQuery describeQuery() throws RecognitionException {
        DescribeQuery dq = null;

        BinaryUnion<Variable, IRI> v = null;

        DatasetClause d = null;

        Pattern w = null;

        SolutionModifiers s = null;


         
        		dq = new DescribeQuery();
        	
        try {
            // IbmSparqlAstWalker.g:207:2: ( ^( DESCRIBE ( ( (v= varOrIRIref2 )+ | '*' ) (d= datasetClause )* (w= whereClause )? (s= solutionModifier ) ) ) )
            // IbmSparqlAstWalker.g:207:6: ^( DESCRIBE ( ( (v= varOrIRIref2 )+ | '*' ) (d= datasetClause )* (w= whereClause )? (s= solutionModifier ) ) )
            {
            match(input,DESCRIBE,FOLLOW_DESCRIBE_in_describeQuery1239); if (state.failed) return dq;

            match(input, Token.DOWN, null); if (state.failed) return dq;
            // IbmSparqlAstWalker.g:207:18: ( ( (v= varOrIRIref2 )+ | '*' ) (d= datasetClause )* (w= whereClause )? (s= solutionModifier ) )
            // IbmSparqlAstWalker.g:207:20: ( (v= varOrIRIref2 )+ | '*' ) (d= datasetClause )* (w= whereClause )? (s= solutionModifier )
            {
            // IbmSparqlAstWalker.g:207:20: ( (v= varOrIRIref2 )+ | '*' )
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( ((LA19_0>=VAR && LA19_0<=PREFIXED_NS)||LA19_0==IRI) ) {
                alt19=1;
            }
            else if ( (LA19_0==251) ) {
                alt19=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return dq;}
                NoViableAltException nvae =
                    new NoViableAltException("", 19, 0, input);

                throw nvae;
            }
            switch (alt19) {
                case 1 :
                    // IbmSparqlAstWalker.g:207:22: (v= varOrIRIref2 )+
                    {
                    // IbmSparqlAstWalker.g:207:22: (v= varOrIRIref2 )+
                    int cnt18=0;
                    loop18:
                    do {
                        int alt18=2;
                        int LA18_0 = input.LA(1);

                        if ( ((LA18_0>=VAR && LA18_0<=PREFIXED_NS)||LA18_0==IRI) ) {
                            alt18=1;
                        }


                        switch (alt18) {
                    	case 1 :
                    	    // IbmSparqlAstWalker.g:207:23: v= varOrIRIref2
                    	    {
                    	    pushFollow(FOLLOW_varOrIRIref2_in_describeQuery1248);
                    	    v=varOrIRIref2();

                    	    state._fsp--;
                    	    if (state.failed) return dq;
                    	    if ( state.backtracking==0 ) {
                    	      dq.getResources().add(v);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt18 >= 1 ) break loop18;
                    	    if (state.backtracking>0) {state.failed=true; return dq;}
                                EarlyExitException eee =
                                    new EarlyExitException(18, input);
                                throw eee;
                        }
                        cnt18++;
                    } while (true);


                    }
                    break;
                case 2 :
                    // IbmSparqlAstWalker.g:207:74: '*'
                    {
                    match(input,251,FOLLOW_251_in_describeQuery1259); if (state.failed) return dq;

                    }
                    break;

            }

            // IbmSparqlAstWalker.g:208:8: (d= datasetClause )*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0==FROM) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // IbmSparqlAstWalker.g:208:10: d= datasetClause
            	    {
            	    pushFollow(FOLLOW_datasetClause_in_describeQuery1274);
            	    d=datasetClause();

            	    state._fsp--;
            	    if (state.failed) return dq;
            	    if ( state.backtracking==0 ) {
            	      dq.getDatasetClauses().add(d);
            	    }

            	    }
            	    break;

            	default :
            	    break loop20;
                }
            } while (true);

            // IbmSparqlAstWalker.g:209:8: (w= whereClause )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==WHERE) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // IbmSparqlAstWalker.g:209:10: w= whereClause
                    {
                    pushFollow(FOLLOW_whereClause_in_describeQuery1295);
                    w=whereClause();

                    state._fsp--;
                    if (state.failed) return dq;
                    if ( state.backtracking==0 ) {
                      dq.setPattern(w);	
                    }

                    }
                    break;

            }

            // IbmSparqlAstWalker.g:210:8: (s= solutionModifier )
            // IbmSparqlAstWalker.g:210:10: s= solutionModifier
            {
            pushFollow(FOLLOW_solutionModifier_in_describeQuery1315);
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
    // IbmSparqlAstWalker.g:215:1: askQuery returns [AskQuery aq] : ^( ASK ( (d= datasetClause )* (w= whereClause ) ) ) ;
    public final AskQuery askQuery() throws RecognitionException {
        AskQuery aq = null;

        DatasetClause d = null;

        Pattern w = null;



        		ArrayList<DatasetClause> dsl = new ArrayList<DatasetClause>();
        	
        try {
            // IbmSparqlAstWalker.g:219:2: ( ^( ASK ( (d= datasetClause )* (w= whereClause ) ) ) )
            // IbmSparqlAstWalker.g:219:6: ^( ASK ( (d= datasetClause )* (w= whereClause ) ) )
            {
            match(input,ASK,FOLLOW_ASK_in_askQuery1356); if (state.failed) return aq;

            match(input, Token.DOWN, null); if (state.failed) return aq;
            // IbmSparqlAstWalker.g:219:13: ( (d= datasetClause )* (w= whereClause ) )
            // IbmSparqlAstWalker.g:219:15: (d= datasetClause )* (w= whereClause )
            {
            // IbmSparqlAstWalker.g:219:15: (d= datasetClause )*
            loop22:
            do {
                int alt22=2;
                int LA22_0 = input.LA(1);

                if ( (LA22_0==FROM) ) {
                    alt22=1;
                }


                switch (alt22) {
            	case 1 :
            	    // IbmSparqlAstWalker.g:219:16: d= datasetClause
            	    {
            	    pushFollow(FOLLOW_datasetClause_in_askQuery1364);
            	    d=datasetClause();

            	    state._fsp--;
            	    if (state.failed) return aq;
            	    if ( state.backtracking==0 ) {
            	       dsl.add(d); 
            	    }

            	    }
            	    break;

            	default :
            	    break loop22;
                }
            } while (true);

            // IbmSparqlAstWalker.g:220:6: (w= whereClause )
            // IbmSparqlAstWalker.g:220:7: w= whereClause
            {
            pushFollow(FOLLOW_whereClause_in_askQuery1380);
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
    // IbmSparqlAstWalker.g:225:1: datasetClause returns [DatasetClause dc] : ^( FROM (d= defaultGraphClause | n= namedGraphClause ) ) ;
    public final DatasetClause datasetClause() throws RecognitionException {
        DatasetClause dc = null;

        DatasetClause d = null;

        DatasetClause n = null;


        try {
            // IbmSparqlAstWalker.g:226:2: ( ^( FROM (d= defaultGraphClause | n= namedGraphClause ) ) )
            // IbmSparqlAstWalker.g:226:4: ^( FROM (d= defaultGraphClause | n= namedGraphClause ) )
            {
            match(input,FROM,FOLLOW_FROM_in_datasetClause1415); if (state.failed) return dc;

            match(input, Token.DOWN, null); if (state.failed) return dc;
            // IbmSparqlAstWalker.g:226:12: (d= defaultGraphClause | n= namedGraphClause )
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( ((LA23_0>=PREFIXED_NAME && LA23_0<=PREFIXED_NS)||LA23_0==IRI) ) {
                alt23=1;
            }
            else if ( (LA23_0==NAMED) ) {
                alt23=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return dc;}
                NoViableAltException nvae =
                    new NoViableAltException("", 23, 0, input);

                throw nvae;
            }
            switch (alt23) {
                case 1 :
                    // IbmSparqlAstWalker.g:226:14: d= defaultGraphClause
                    {
                    pushFollow(FOLLOW_defaultGraphClause_in_datasetClause1422);
                    d=defaultGraphClause();

                    state._fsp--;
                    if (state.failed) return dc;
                    if ( state.backtracking==0 ) {
                       dc = d; 
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlAstWalker.g:227:7: n= namedGraphClause
                    {
                    pushFollow(FOLLOW_namedGraphClause_in_datasetClause1436);
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
    // IbmSparqlAstWalker.g:232:1: defaultGraphClause returns [DatasetClause dc] : d= sourceSelector ;
    public final DatasetClause defaultGraphClause() throws RecognitionException {
        DatasetClause dc = null;

        IRI d = null;


        try {
            // IbmSparqlAstWalker.g:233:2: (d= sourceSelector )
            // IbmSparqlAstWalker.g:233:6: d= sourceSelector
            {
            pushFollow(FOLLOW_sourceSelector_in_defaultGraphClause1469);
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
    // IbmSparqlAstWalker.g:236:1: namedGraphClause returns [DatasetClause dc] : ^( NAMED d= sourceSelector ) ;
    public final DatasetClause namedGraphClause() throws RecognitionException {
        DatasetClause dc = null;

        IRI d = null;


        try {
            // IbmSparqlAstWalker.g:237:2: ( ^( NAMED d= sourceSelector ) )
            // IbmSparqlAstWalker.g:237:6: ^( NAMED d= sourceSelector )
            {
            match(input,NAMED,FOLLOW_NAMED_in_namedGraphClause1491); if (state.failed) return dc;

            match(input, Token.DOWN, null); if (state.failed) return dc;
            pushFollow(FOLLOW_sourceSelector_in_namedGraphClause1495);
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
    // IbmSparqlAstWalker.g:240:1: sourceSelector returns [IRI r] : i= iRIref ;
    public final IRI sourceSelector() throws RecognitionException {
        IRI r = null;

        IRI i = null;


        try {
            // IbmSparqlAstWalker.g:241:2: (i= iRIref )
            // IbmSparqlAstWalker.g:241:7: i= iRIref
            {
            pushFollow(FOLLOW_iRIref_in_sourceSelector1521);
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
    // IbmSparqlAstWalker.g:244:1: whereClause returns [Pattern p] : ^( WHERE (g= groupGraphPattern[true] )? ) ;
    public final Pattern whereClause() throws RecognitionException {
        Pattern p = null;

        Pattern g = null;


        try {
            // IbmSparqlAstWalker.g:245:2: ( ^( WHERE (g= groupGraphPattern[true] )? ) )
            // IbmSparqlAstWalker.g:246:3: ^( WHERE (g= groupGraphPattern[true] )? )
            {
            match(input,WHERE,FOLLOW_WHERE_in_whereClause1544); if (state.failed) return p;

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); if (state.failed) return p;
                // IbmSparqlAstWalker.g:246:12: (g= groupGraphPattern[true] )?
                int alt24=2;
                int LA24_0 = input.LA(1);

                if ( (LA24_0==GROUP_GRAPH_PATTERN||LA24_0==SUB_SELECT) ) {
                    alt24=1;
                }
                switch (alt24) {
                    case 1 :
                        // IbmSparqlAstWalker.g:246:12: g= groupGraphPattern[true]
                        {
                        pushFollow(FOLLOW_groupGraphPattern_in_whereClause1548);
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
    // IbmSparqlAstWalker.g:249:1: solutionModifier returns [SolutionModifiers m] : ^( MODIFIERS (g= groupClause )? (h= havingClause )? (o= orderClause )? (l= limitOffsetClauses )? ) ;
    public final SolutionModifiers solutionModifier() throws RecognitionException {
        SolutionModifiers m = null;

        GroupCondition g = null;

        HavingCondition h = null;

        List<OrderCondition> o = null;

        LimitOffsetClauses l = null;



        		m = new SolutionModifiers();
        	
        try {
            // IbmSparqlAstWalker.g:253:2: ( ^( MODIFIERS (g= groupClause )? (h= havingClause )? (o= orderClause )? (l= limitOffsetClauses )? ) )
            // IbmSparqlAstWalker.g:253:6: ^( MODIFIERS (g= groupClause )? (h= havingClause )? (o= orderClause )? (l= limitOffsetClauses )? )
            {
            match(input,MODIFIERS,FOLLOW_MODIFIERS_in_solutionModifier1579); if (state.failed) return m;

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); if (state.failed) return m;
                // IbmSparqlAstWalker.g:254:4: (g= groupClause )?
                int alt25=2;
                int LA25_0 = input.LA(1);

                if ( (LA25_0==GROUP_BY) ) {
                    alt25=1;
                }
                switch (alt25) {
                    case 1 :
                        // IbmSparqlAstWalker.g:254:6: g= groupClause
                        {
                        pushFollow(FOLLOW_groupClause_in_solutionModifier1588);
                        g=groupClause();

                        state._fsp--;
                        if (state.failed) return m;
                        if ( state.backtracking==0 ) {
                          m.setGroupClause(g); 
                        }

                        }
                        break;

                }

                // IbmSparqlAstWalker.g:255:4: (h= havingClause )?
                int alt26=2;
                int LA26_0 = input.LA(1);

                if ( (LA26_0==HAVING) ) {
                    alt26=1;
                }
                switch (alt26) {
                    case 1 :
                        // IbmSparqlAstWalker.g:255:6: h= havingClause
                        {
                        pushFollow(FOLLOW_havingClause_in_solutionModifier1605);
                        h=havingClause();

                        state._fsp--;
                        if (state.failed) return m;
                        if ( state.backtracking==0 ) {
                          m.setHavingClause(h);
                        }

                        }
                        break;

                }

                // IbmSparqlAstWalker.g:256:4: (o= orderClause )?
                int alt27=2;
                int LA27_0 = input.LA(1);

                if ( (LA27_0==ORDER_BY) ) {
                    alt27=1;
                }
                switch (alt27) {
                    case 1 :
                        // IbmSparqlAstWalker.g:256:6: o= orderClause
                        {
                        pushFollow(FOLLOW_orderClause_in_solutionModifier1622);
                        o=orderClause();

                        state._fsp--;
                        if (state.failed) return m;
                        if ( state.backtracking==0 ) {
                          m.setOrderClause(o); 
                        }

                        }
                        break;

                }

                // IbmSparqlAstWalker.g:257:4: (l= limitOffsetClauses )?
                int alt28=2;
                int LA28_0 = input.LA(1);

                if ( (LA28_0==LIMIT||LA28_0==OFFSET) ) {
                    alt28=1;
                }
                switch (alt28) {
                    case 1 :
                        // IbmSparqlAstWalker.g:257:6: l= limitOffsetClauses
                        {
                        pushFollow(FOLLOW_limitOffsetClauses_in_solutionModifier1639);
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
    // IbmSparqlAstWalker.g:260:1: groupClause returns [GroupCondition gc] : ^( GROUP_BY c= groupCondition ) ;
    public final GroupCondition groupClause() throws RecognitionException {
        GroupCondition gc = null;

        GroupCondition c = null;


        try {
            // IbmSparqlAstWalker.g:261:2: ( ^( GROUP_BY c= groupCondition ) )
            // IbmSparqlAstWalker.g:261:6: ^( GROUP_BY c= groupCondition )
            {
            match(input,GROUP_BY,FOLLOW_GROUP_BY_in_groupClause1665); if (state.failed) return gc;

            match(input, Token.DOWN, null); if (state.failed) return gc;
            pushFollow(FOLLOW_groupCondition_in_groupClause1669);
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
    // IbmSparqlAstWalker.g:264:1: groupCondition returns [GroupCondition gc] : (b= builtInCall | f= functionCall | ^( CONDITION e2= expression (v= var )? ) | v2= var )+ ;
    public final GroupCondition groupCondition() throws RecognitionException {
        GroupCondition gc = null;

        Expression b = null;

        FunctionCall f = null;

        Expression e2 = null;

        String v = null;

        String v2 = null;



        		gc = new GroupCondition();
        	
        try {
            // IbmSparqlAstWalker.g:268:2: ( (b= builtInCall | f= functionCall | ^( CONDITION e2= expression (v= var )? ) | v2= var )+ )
            // IbmSparqlAstWalker.g:269:2: (b= builtInCall | f= functionCall | ^( CONDITION e2= expression (v= var )? ) | v2= var )+
            {
            // IbmSparqlAstWalker.g:269:2: (b= builtInCall | f= functionCall | ^( CONDITION e2= expression (v= var )? ) | v2= var )+
            int cnt30=0;
            loop30:
            do {
                int alt30=5;
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
                    alt30=1;
                    }
                    break;
                case FUNCTION:
                    {
                    alt30=2;
                    }
                    break;
                case CONDITION:
                    {
                    alt30=3;
                    }
                    break;
                case VAR:
                    {
                    alt30=4;
                    }
                    break;

                }

                switch (alt30) {
            	case 1 :
            	    // IbmSparqlAstWalker.g:269:4: b= builtInCall
            	    {
            	    pushFollow(FOLLOW_builtInCall_in_groupCondition1702);
            	    b=builtInCall();

            	    state._fsp--;
            	    if (state.failed) return gc;
            	    if ( state.backtracking==0 ) {
            	       gc.addCondition(b);                               
            	    }

            	    }
            	    break;
            	case 2 :
            	    // IbmSparqlAstWalker.g:270:4: f= functionCall
            	    {
            	    pushFollow(FOLLOW_functionCall_in_groupCondition1721);
            	    f=functionCall();

            	    state._fsp--;
            	    if (state.failed) return gc;
            	    if ( state.backtracking==0 ) {
            	       gc.addCondition(new FunctionCallExpression(f));   
            	    }

            	    }
            	    break;
            	case 3 :
            	    // IbmSparqlAstWalker.g:271:4: ^( CONDITION e2= expression (v= var )? )
            	    {
            	    match(input,CONDITION,FOLLOW_CONDITION_in_groupCondition1739); if (state.failed) return gc;

            	    match(input, Token.DOWN, null); if (state.failed) return gc;
            	    pushFollow(FOLLOW_expression_in_groupCondition1743);
            	    e2=expression();

            	    state._fsp--;
            	    if (state.failed) return gc;
            	    // IbmSparqlAstWalker.g:271:30: (v= var )?
            	    int alt29=2;
            	    int LA29_0 = input.LA(1);

            	    if ( (LA29_0==VAR) ) {
            	        alt29=1;
            	    }
            	    switch (alt29) {
            	        case 1 :
            	            // IbmSparqlAstWalker.g:271:31: v= var
            	            {
            	            pushFollow(FOLLOW_var_in_groupCondition1748);
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
            	    // IbmSparqlAstWalker.g:272:4: v2= var
            	    {
            	    pushFollow(FOLLOW_var_in_groupCondition1765);
            	    v2=var();

            	    state._fsp--;
            	    if (state.failed) return gc;
            	    if ( state.backtracking==0 ) {
            	       gc.addCondition(new VariableExpression(v2));      
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt30 >= 1 ) break loop30;
            	    if (state.backtracking>0) {state.failed=true; return gc;}
                        EarlyExitException eee =
                            new EarlyExitException(30, input);
                        throw eee;
                }
                cnt30++;
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
    // IbmSparqlAstWalker.g:276:1: havingClause returns [HavingCondition h] : ^( HAVING (c= havingCondition )+ ) ;
    public final HavingCondition havingClause() throws RecognitionException {
        HavingCondition h = null;

        Expression c = null;



        		h = new HavingCondition();
        	
        try {
            // IbmSparqlAstWalker.g:280:2: ( ^( HAVING (c= havingCondition )+ ) )
            // IbmSparqlAstWalker.g:280:6: ^( HAVING (c= havingCondition )+ )
            {
            match(input,HAVING,FOLLOW_HAVING_in_havingClause1839); if (state.failed) return h;

            match(input, Token.DOWN, null); if (state.failed) return h;
            // IbmSparqlAstWalker.g:280:15: (c= havingCondition )+
            int cnt31=0;
            loop31:
            do {
                int alt31=2;
                int LA31_0 = input.LA(1);

                if ( ((LA31_0>=FUNCTION && LA31_0<=NOT_EXISTS)||(LA31_0>=STR && LA31_0<=SHA1)||(LA31_0>=SHA256 && LA31_0<=EXISTS)) ) {
                    alt31=1;
                }


                switch (alt31) {
            	case 1 :
            	    // IbmSparqlAstWalker.g:280:16: c= havingCondition
            	    {
            	    pushFollow(FOLLOW_havingCondition_in_havingClause1844);
            	    c=havingCondition();

            	    state._fsp--;
            	    if (state.failed) return h;
            	    if ( state.backtracking==0 ) {
            	      h.addConstraint(c); 
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt31 >= 1 ) break loop31;
            	    if (state.backtracking>0) {state.failed=true; return h;}
                        EarlyExitException eee =
                            new EarlyExitException(31, input);
                        throw eee;
                }
                cnt31++;
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
    // IbmSparqlAstWalker.g:283:1: havingCondition returns [Expression e] : c= constraint ;
    public final Expression havingCondition() throws RecognitionException {
        Expression e = null;

        Expression c = null;


        try {
            // IbmSparqlAstWalker.g:284:2: (c= constraint )
            // IbmSparqlAstWalker.g:284:6: c= constraint
            {
            pushFollow(FOLLOW_constraint_in_havingCondition1872);
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
    // IbmSparqlAstWalker.g:287:1: orderClause returns [List<OrderCondition> loc] : ^( ORDER_BY (oc= orderCondition )+ ) ;
    public final List<OrderCondition> orderClause() throws RecognitionException {
        List<OrderCondition> loc = null;

        OrderCondition oc = null;


         
        		loc = new ArrayList<OrderCondition>(); 
        	
        try {
            // IbmSparqlAstWalker.g:291:2: ( ^( ORDER_BY (oc= orderCondition )+ ) )
            // IbmSparqlAstWalker.g:291:6: ^( ORDER_BY (oc= orderCondition )+ )
            {
            match(input,ORDER_BY,FOLLOW_ORDER_BY_in_orderClause1901); if (state.failed) return loc;

            match(input, Token.DOWN, null); if (state.failed) return loc;
            // IbmSparqlAstWalker.g:291:18: (oc= orderCondition )+
            int cnt32=0;
            loop32:
            do {
                int alt32=2;
                int LA32_0 = input.LA(1);

                if ( (LA32_0==VAR||(LA32_0>=FUNCTION && LA32_0<=NOT_EXISTS)||(LA32_0>=ASC && LA32_0<=DESC)||(LA32_0>=STR && LA32_0<=SHA1)||(LA32_0>=SHA256 && LA32_0<=EXISTS)) ) {
                    alt32=1;
                }


                switch (alt32) {
            	case 1 :
            	    // IbmSparqlAstWalker.g:291:20: oc= orderCondition
            	    {
            	    pushFollow(FOLLOW_orderCondition_in_orderClause1908);
            	    oc=orderCondition();

            	    state._fsp--;
            	    if (state.failed) return loc;
            	    if ( state.backtracking==0 ) {
            	       loc.add(oc); 
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt32 >= 1 ) break loop32;
            	    if (state.backtracking>0) {state.failed=true; return loc;}
                        EarlyExitException eee =
                            new EarlyExitException(32, input);
                        throw eee;
                }
                cnt32++;
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
    // IbmSparqlAstWalker.g:294:1: orderCondition returns [OrderCondition oc] : ( ( ^( ASC e1= brackettedExpression ) ) | ( ^( DESC e2= brackettedExpression ) ) | (c= constraint | v= var ) );
    public final OrderCondition orderCondition() throws RecognitionException {
        OrderCondition oc = null;

        Expression e1 = null;

        Expression e2 = null;

        Expression c = null;

        String v = null;


        try {
            // IbmSparqlAstWalker.g:295:2: ( ( ^( ASC e1= brackettedExpression ) ) | ( ^( DESC e2= brackettedExpression ) ) | (c= constraint | v= var ) )
            int alt34=3;
            switch ( input.LA(1) ) {
            case ASC:
                {
                alt34=1;
                }
                break;
            case DESC:
                {
                alt34=2;
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
                alt34=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return oc;}
                NoViableAltException nvae =
                    new NoViableAltException("", 34, 0, input);

                throw nvae;
            }

            switch (alt34) {
                case 1 :
                    // IbmSparqlAstWalker.g:295:5: ( ^( ASC e1= brackettedExpression ) )
                    {
                    // IbmSparqlAstWalker.g:295:5: ( ^( ASC e1= brackettedExpression ) )
                    // IbmSparqlAstWalker.g:295:6: ^( ASC e1= brackettedExpression )
                    {
                    match(input,ASC,FOLLOW_ASC_in_orderCondition1935); if (state.failed) return oc;

                    match(input, Token.DOWN, null); if (state.failed) return oc;
                    pushFollow(FOLLOW_brackettedExpression_in_orderCondition1939);
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
                    // IbmSparqlAstWalker.g:296:4: ( ^( DESC e2= brackettedExpression ) )
                    {
                    // IbmSparqlAstWalker.g:296:4: ( ^( DESC e2= brackettedExpression ) )
                    // IbmSparqlAstWalker.g:296:5: ^( DESC e2= brackettedExpression )
                    {
                    match(input,DESC,FOLLOW_DESC_in_orderCondition1951); if (state.failed) return oc;

                    match(input, Token.DOWN, null); if (state.failed) return oc;
                    pushFollow(FOLLOW_brackettedExpression_in_orderCondition1955);
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
                    // IbmSparqlAstWalker.g:297:5: (c= constraint | v= var )
                    {
                    // IbmSparqlAstWalker.g:297:5: (c= constraint | v= var )
                    int alt33=2;
                    int LA33_0 = input.LA(1);

                    if ( ((LA33_0>=FUNCTION && LA33_0<=NOT_EXISTS)||(LA33_0>=STR && LA33_0<=SHA1)||(LA33_0>=SHA256 && LA33_0<=EXISTS)) ) {
                        alt33=1;
                    }
                    else if ( (LA33_0==VAR) ) {
                        alt33=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return oc;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 33, 0, input);

                        throw nvae;
                    }
                    switch (alt33) {
                        case 1 :
                            // IbmSparqlAstWalker.g:297:7: c= constraint
                            {
                            pushFollow(FOLLOW_constraint_in_orderCondition1969);
                            c=constraint();

                            state._fsp--;
                            if (state.failed) return oc;
                            if ( state.backtracking==0 ) {
                               oc = new OrderCondition(c); 
                            }

                            }
                            break;
                        case 2 :
                            // IbmSparqlAstWalker.g:298:5: v= var
                            {
                            pushFollow(FOLLOW_var_in_orderCondition1985);
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
    // IbmSparqlAstWalker.g:302:1: limitOffsetClauses returns [LimitOffsetClauses loc] : (lc1= limitClause (oc1= offsetClause )? | oc2= offsetClause (lc2= limitClause )? ) ;
    public final LimitOffsetClauses limitOffsetClauses() throws RecognitionException {
        LimitOffsetClauses loc = null;

        int lc1 = 0;

        int oc1 = 0;

        int oc2 = 0;

        int lc2 = 0;


         
        		int lc = -1, oc = -1; 
        	
        try {
            // IbmSparqlAstWalker.g:306:2: ( (lc1= limitClause (oc1= offsetClause )? | oc2= offsetClause (lc2= limitClause )? ) )
            // IbmSparqlAstWalker.g:306:6: (lc1= limitClause (oc1= offsetClause )? | oc2= offsetClause (lc2= limitClause )? )
            {
            // IbmSparqlAstWalker.g:306:6: (lc1= limitClause (oc1= offsetClause )? | oc2= offsetClause (lc2= limitClause )? )
            int alt37=2;
            int LA37_0 = input.LA(1);

            if ( (LA37_0==LIMIT) ) {
                alt37=1;
            }
            else if ( (LA37_0==OFFSET) ) {
                alt37=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return loc;}
                NoViableAltException nvae =
                    new NoViableAltException("", 37, 0, input);

                throw nvae;
            }
            switch (alt37) {
                case 1 :
                    // IbmSparqlAstWalker.g:306:8: lc1= limitClause (oc1= offsetClause )?
                    {
                    pushFollow(FOLLOW_limitClause_in_limitOffsetClauses2025);
                    lc1=limitClause();

                    state._fsp--;
                    if (state.failed) return loc;
                    if ( state.backtracking==0 ) {
                      lc = lc1;
                    }
                    // IbmSparqlAstWalker.g:306:37: (oc1= offsetClause )?
                    int alt35=2;
                    int LA35_0 = input.LA(1);

                    if ( (LA35_0==OFFSET) ) {
                        alt35=1;
                    }
                    switch (alt35) {
                        case 1 :
                            // IbmSparqlAstWalker.g:306:38: oc1= offsetClause
                            {
                            pushFollow(FOLLOW_offsetClause_in_limitOffsetClauses2033);
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
                    // IbmSparqlAstWalker.g:307:5: oc2= offsetClause (lc2= limitClause )?
                    {
                    pushFollow(FOLLOW_offsetClause_in_limitOffsetClauses2046);
                    oc2=offsetClause();

                    state._fsp--;
                    if (state.failed) return loc;
                    if ( state.backtracking==0 ) {
                      oc = oc2;
                    }
                    // IbmSparqlAstWalker.g:307:34: (lc2= limitClause )?
                    int alt36=2;
                    int LA36_0 = input.LA(1);

                    if ( (LA36_0==LIMIT) ) {
                        alt36=1;
                    }
                    switch (alt36) {
                        case 1 :
                            // IbmSparqlAstWalker.g:307:35: lc2= limitClause
                            {
                            pushFollow(FOLLOW_limitClause_in_limitOffsetClauses2053);
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
    // IbmSparqlAstWalker.g:312:1: limitClause returns [int x] : ^( LIMIT i= INTEGER ) ;
    public final int limitClause() throws RecognitionException {
        int x = 0;

        XTree i=null;

        try {
            // IbmSparqlAstWalker.g:313:2: ( ^( LIMIT i= INTEGER ) )
            // IbmSparqlAstWalker.g:314:3: ^( LIMIT i= INTEGER )
            {
            match(input,LIMIT,FOLLOW_LIMIT_in_limitClause2091); if (state.failed) return x;

            match(input, Token.DOWN, null); if (state.failed) return x;
            i=(XTree)match(input,INTEGER,FOLLOW_INTEGER_in_limitClause2096); if (state.failed) return x;

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
    // IbmSparqlAstWalker.g:317:1: offsetClause returns [int x] : ^( OFFSET i= INTEGER ) ;
    public final int offsetClause() throws RecognitionException {
        int x = 0;

        XTree i=null;

        try {
            // IbmSparqlAstWalker.g:318:2: ( ^( OFFSET i= INTEGER ) )
            // IbmSparqlAstWalker.g:319:3: ^( OFFSET i= INTEGER )
            {
            match(input,OFFSET,FOLLOW_OFFSET_in_offsetClause2123); if (state.failed) return x;

            match(input, Token.DOWN, null); if (state.failed) return x;
            i=(XTree)match(input,INTEGER,FOLLOW_INTEGER_in_offsetClause2128); if (state.failed) return x;

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
    // IbmSparqlAstWalker.g:322:1: bindingsClause returns [Pattern v] : d= inlineData ;
    public final Pattern bindingsClause() throws RecognitionException {
        Pattern v = null;

        ValuesPattern d = null;


        try {
            // IbmSparqlAstWalker.g:323:2: (d= inlineData )
            // IbmSparqlAstWalker.g:323:6: d= inlineData
            {
            pushFollow(FOLLOW_inlineData_in_bindingsClause2152);
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
    // IbmSparqlAstWalker.g:326:1: bindingValue : ( iRIref | rDFLiteral | numericLiteral | booleanLiteral | UNDEF ) ;
    public final void bindingValue() throws RecognitionException {
        try {
            // IbmSparqlAstWalker.g:327:2: ( ( iRIref | rDFLiteral | numericLiteral | booleanLiteral | UNDEF ) )
            // IbmSparqlAstWalker.g:327:6: ( iRIref | rDFLiteral | numericLiteral | booleanLiteral | UNDEF )
            {
            // IbmSparqlAstWalker.g:327:6: ( iRIref | rDFLiteral | numericLiteral | booleanLiteral | UNDEF )
            int alt38=5;
            switch ( input.LA(1) ) {
            case PREFIXED_NAME:
            case PREFIXED_NS:
            case IRI:
                {
                alt38=1;
                }
                break;
            case STRING:
                {
                alt38=2;
                }
                break;
            case BIG_INTEGER:
            case BIG_DECIMAL:
            case DOUBLE:
                {
                alt38=3;
                }
                break;
            case BOOLEAN:
                {
                alt38=4;
                }
                break;
            case UNDEF:
                {
                alt38=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 38, 0, input);

                throw nvae;
            }

            switch (alt38) {
                case 1 :
                    // IbmSparqlAstWalker.g:327:8: iRIref
                    {
                    pushFollow(FOLLOW_iRIref_in_bindingValue2173);
                    iRIref();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // IbmSparqlAstWalker.g:327:17: rDFLiteral
                    {
                    pushFollow(FOLLOW_rDFLiteral_in_bindingValue2177);
                    rDFLiteral();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // IbmSparqlAstWalker.g:327:30: numericLiteral
                    {
                    pushFollow(FOLLOW_numericLiteral_in_bindingValue2181);
                    numericLiteral();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // IbmSparqlAstWalker.g:327:47: booleanLiteral
                    {
                    pushFollow(FOLLOW_booleanLiteral_in_bindingValue2185);
                    booleanLiteral();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // IbmSparqlAstWalker.g:327:64: UNDEF
                    {
                    match(input,UNDEF,FOLLOW_UNDEF_in_bindingValue2189); if (state.failed) return ;

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
    // IbmSparqlAstWalker.g:330:1: triplesTemplate[ConstructQuery cq, PatternSet p] : sp= triplesSameSubject[$cq] ( DOT ( triplesTemplate[$cq, $p] )? )? ;
    public final void triplesTemplate(ConstructQuery cq, PatternSet p) throws RecognitionException {
        SimplePattern sp = null;


        try {
            // IbmSparqlAstWalker.g:331:2: (sp= triplesSameSubject[$cq] ( DOT ( triplesTemplate[$cq, $p] )? )? )
            // IbmSparqlAstWalker.g:331:7: sp= triplesSameSubject[$cq] ( DOT ( triplesTemplate[$cq, $p] )? )?
            {
            pushFollow(FOLLOW_triplesSameSubject_in_triplesTemplate2209);
            sp=triplesSameSubject(cq);

            state._fsp--;
            if (state.failed) return ;
            if ( state.backtracking==0 ) {
               p.addPattern(sp); 
            }
            // IbmSparqlAstWalker.g:331:57: ( DOT ( triplesTemplate[$cq, $p] )? )?
            int alt40=2;
            int LA40_0 = input.LA(1);

            if ( (LA40_0==DOT) ) {
                alt40=1;
            }
            switch (alt40) {
                case 1 :
                    // IbmSparqlAstWalker.g:331:59: DOT ( triplesTemplate[$cq, $p] )?
                    {
                    match(input,DOT,FOLLOW_DOT_in_triplesTemplate2216); if (state.failed) return ;
                    // IbmSparqlAstWalker.g:331:63: ( triplesTemplate[$cq, $p] )?
                    int alt39=2;
                    int LA39_0 = input.LA(1);

                    if ( ((LA39_0>=TRIPLE && LA39_0<=TRIPLE2)) ) {
                        alt39=1;
                    }
                    switch (alt39) {
                        case 1 :
                            // IbmSparqlAstWalker.g:331:63: triplesTemplate[$cq, $p]
                            {
                            pushFollow(FOLLOW_triplesTemplate_in_triplesTemplate2218);
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
    // IbmSparqlAstWalker.g:334:1: groupGraphPattern[boolean l] returns [Pattern r] : ( ^( GROUP_GRAPH_PATTERN ( groupGraphPatternSub[p] )? ) | s= subSelect );
    public final Pattern groupGraphPattern(boolean l) throws RecognitionException {
        Pattern r = null;

        SubSelectPattern s = null;



        		PatternSet p = new PatternSet();  p.setTopLevel(l);
        		r = p;
        	
        try {
            // IbmSparqlAstWalker.g:339:2: ( ^( GROUP_GRAPH_PATTERN ( groupGraphPatternSub[p] )? ) | s= subSelect )
            int alt42=2;
            int LA42_0 = input.LA(1);

            if ( (LA42_0==GROUP_GRAPH_PATTERN) ) {
                alt42=1;
            }
            else if ( (LA42_0==SUB_SELECT) ) {
                alt42=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return r;}
                NoViableAltException nvae =
                    new NoViableAltException("", 42, 0, input);

                throw nvae;
            }
            switch (alt42) {
                case 1 :
                    // IbmSparqlAstWalker.g:339:6: ^( GROUP_GRAPH_PATTERN ( groupGraphPatternSub[p] )? )
                    {
                    match(input,GROUP_GRAPH_PATTERN,FOLLOW_GROUP_GRAPH_PATTERN_in_groupGraphPattern2248); if (state.failed) return r;

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); if (state.failed) return r;
                        // IbmSparqlAstWalker.g:339:28: ( groupGraphPatternSub[p] )?
                        int alt41=2;
                        int LA41_0 = input.LA(1);

                        if ( (LA41_0==GROUP_GRAPH_PATTERN||(LA41_0>=SUB_SELECT && LA41_0<=TRIPLES_BLOCK)||LA41_0==GRAPH||(LA41_0>=VALUES && LA41_0<=FILTER)) ) {
                            alt41=1;
                        }
                        switch (alt41) {
                            case 1 :
                                // IbmSparqlAstWalker.g:339:28: groupGraphPatternSub[p]
                                {
                                pushFollow(FOLLOW_groupGraphPatternSub_in_groupGraphPattern2250);
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
                    // IbmSparqlAstWalker.g:340:4: s= subSelect
                    {
                    pushFollow(FOLLOW_subSelect_in_groupGraphPattern2262);
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
    // IbmSparqlAstWalker.g:343:1: groupGraphPatternSub[PatternSet p] : (sp= triplesBlock[$p] | f= filter | graphPatternNewBGP[$p,blankNodes] )+ ;
    public final void groupGraphPatternSub(PatternSet p) throws RecognitionException {
        SimplePattern sp = null;

        Expression f = null;



              Set<BlankNodeVariable> blankNodes = HashSetFactory.make();  
            
        try {
            // IbmSparqlAstWalker.g:347:2: ( (sp= triplesBlock[$p] | f= filter | graphPatternNewBGP[$p,blankNodes] )+ )
            // IbmSparqlAstWalker.g:347:4: (sp= triplesBlock[$p] | f= filter | graphPatternNewBGP[$p,blankNodes] )+
            {
            // IbmSparqlAstWalker.g:347:4: (sp= triplesBlock[$p] | f= filter | graphPatternNewBGP[$p,blankNodes] )+
            int cnt43=0;
            loop43:
            do {
                int alt43=4;
                switch ( input.LA(1) ) {
                case TRIPLES_BLOCK:
                    {
                    alt43=1;
                    }
                    break;
                case FILTER:
                    {
                    alt43=2;
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
                    alt43=3;
                    }
                    break;

                }

                switch (alt43) {
            	case 1 :
            	    // IbmSparqlAstWalker.g:347:10: sp= triplesBlock[$p]
            	    {
            	    pushFollow(FOLLOW_triplesBlock_in_groupGraphPatternSub2294);
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
            	    // IbmSparqlAstWalker.g:352:12: f= filter
            	    {
            	    pushFollow(FOLLOW_filter_in_groupGraphPatternSub2326);
            	    f=filter();

            	    state._fsp--;
            	    if (state.failed) return ;
            	    if ( state.backtracking==0 ) {
            	       
            	                      if (f != null) { p.addFilter(f); }
            	                    
            	    }

            	    }
            	    break;
            	case 3 :
            	    // IbmSparqlAstWalker.g:356:15: graphPatternNewBGP[$p,blankNodes]
            	    {
            	    pushFollow(FOLLOW_graphPatternNewBGP_in_groupGraphPatternSub2364);
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
            	    if ( cnt43 >= 1 ) break loop43;
            	    if (state.backtracking>0) {state.failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(43, input);
                        throw eee;
                }
                cnt43++;
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
    // IbmSparqlAstWalker.g:368:1: triplesBlock[PatternSet p] returns [SimplePattern sp] : ^( TRIPLES_BLOCK (s= triples | s2= triples2 )+ ) ;
    public final SimplePattern triplesBlock(PatternSet p) throws RecognitionException {
        SimplePattern sp = null;

        QueryTriple s = null;

        QueryTriple2 s2 = null;


        try {
            // IbmSparqlAstWalker.g:369:5: ( ^( TRIPLES_BLOCK (s= triples | s2= triples2 )+ ) )
            // IbmSparqlAstWalker.g:369:9: ^( TRIPLES_BLOCK (s= triples | s2= triples2 )+ )
            {
            match(input,TRIPLES_BLOCK,FOLLOW_TRIPLES_BLOCK_in_triplesBlock2423); if (state.failed) return sp;

            if ( state.backtracking==0 ) {
               sp = new SimplePattern(); 
            }

            match(input, Token.DOWN, null); if (state.failed) return sp;
            // IbmSparqlAstWalker.g:371:6: (s= triples | s2= triples2 )+
            int cnt44=0;
            loop44:
            do {
                int alt44=3;
                int LA44_0 = input.LA(1);

                if ( (LA44_0==TRIPLE) ) {
                    alt44=1;
                }
                else if ( (LA44_0==TRIPLE2) ) {
                    alt44=2;
                }


                switch (alt44) {
            	case 1 :
            	    // IbmSparqlAstWalker.g:372:10: s= triples
            	    {
            	    pushFollow(FOLLOW_triples_in_triplesBlock2456);
            	    s=triples();

            	    state._fsp--;
            	    if (state.failed) return sp;
            	    if ( state.backtracking==0 ) {
            	       s.expandAndAddTo(sp); 
            	    }

            	    }
            	    break;
            	case 2 :
            	    // IbmSparqlAstWalker.g:373:7: s2= triples2
            	    {
            	    pushFollow(FOLLOW_triples2_in_triplesBlock2472);
            	    s2=triples2();

            	    state._fsp--;
            	    if (state.failed) return sp;
            	    if ( state.backtracking==0 ) {
            	       s2.expandAndAddTo(sp); 
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt44 >= 1 ) break loop44;
            	    if (state.backtracking>0) {state.failed=true; return sp;}
                        EarlyExitException eee =
                            new EarlyExitException(44, input);
                        throw eee;
                }
                cnt44++;
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
    // IbmSparqlAstWalker.g:378:1: triples returns [QueryTriple qt] : ( ^( TRIPLE ^( SUBJECT (s1= graphNode ) ) ^( PREDICATE (v1= predicate ) ) ^( VALUE o= object ) ) ) ;
    public final QueryTriple triples() throws RecognitionException {
        QueryTriple qt = null;

        GraphNode s1 = null;

        BinaryUnion<Variable, Path> v1 = null;

        GraphNode o = null;


         
        			QueryTripleTerm s = null;
        			PropertyTerm p = null;
        			QueryTripleTerm v = null; 
        		  
        try {
            // IbmSparqlAstWalker.g:384:2: ( ( ^( TRIPLE ^( SUBJECT (s1= graphNode ) ) ^( PREDICATE (v1= predicate ) ) ^( VALUE o= object ) ) ) )
            // IbmSparqlAstWalker.g:384:6: ( ^( TRIPLE ^( SUBJECT (s1= graphNode ) ) ^( PREDICATE (v1= predicate ) ) ^( VALUE o= object ) ) )
            {
            // IbmSparqlAstWalker.g:384:6: ( ^( TRIPLE ^( SUBJECT (s1= graphNode ) ) ^( PREDICATE (v1= predicate ) ) ^( VALUE o= object ) ) )
            // IbmSparqlAstWalker.g:384:8: ^( TRIPLE ^( SUBJECT (s1= graphNode ) ) ^( PREDICATE (v1= predicate ) ) ^( VALUE o= object ) )
            {
            match(input,TRIPLE,FOLLOW_TRIPLE_in_triples2520); if (state.failed) return qt;

            match(input, Token.DOWN, null); if (state.failed) return qt;
            match(input,SUBJECT,FOLLOW_SUBJECT_in_triples2523); if (state.failed) return qt;

            match(input, Token.DOWN, null); if (state.failed) return qt;
            // IbmSparqlAstWalker.g:384:27: (s1= graphNode )
            // IbmSparqlAstWalker.g:384:29: s1= graphNode
            {
            pushFollow(FOLLOW_graphNode_in_triples2529);
            s1=graphNode();

            state._fsp--;
            if (state.failed) return qt;
            if ( state.backtracking==0 ) {
               s = new QueryTripleTerm(s1);  
            }

            }


            match(input, Token.UP, null); if (state.failed) return qt;
            match(input,PREDICATE,FOLLOW_PREDICATE_in_triples2565); if (state.failed) return qt;

            match(input, Token.DOWN, null); if (state.failed) return qt;
            // IbmSparqlAstWalker.g:387:18: (v1= predicate )
            // IbmSparqlAstWalker.g:387:20: v1= predicate
            {
            pushFollow(FOLLOW_predicate_in_triples2571);
            v1=predicate();

            state._fsp--;
            if (state.failed) return qt;
            if ( state.backtracking==0 ) {
               p = new PropertyTerm(v1);  
            }

            }


            match(input, Token.UP, null); if (state.failed) return qt;
            match(input,VALUE,FOLLOW_VALUE_in_triples2611); if (state.failed) return qt;

            match(input, Token.DOWN, null); if (state.failed) return qt;
            pushFollow(FOLLOW_object_in_triples2616);
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
    // IbmSparqlAstWalker.g:396:1: triples2 returns [QueryTriple2 qt] : ^( TRIPLE2 ^( SUBJECT (s= triplesNode ) ) ( ^( PROPERTY_LIST ^( PREDICATE (p= predicate ) ) ( ^( VALUE o= object ) )+ ) )* ) ;
    public final QueryTriple2 triples2() throws RecognitionException {
        QueryTriple2 qt = null;

        TriplesNode s = null;

        BinaryUnion<Variable, Path> p = null;

        GraphNode o = null;


         
        				PropertyListElement ple = null;
        		  
        try {
            // IbmSparqlAstWalker.g:400:2: ( ^( TRIPLE2 ^( SUBJECT (s= triplesNode ) ) ( ^( PROPERTY_LIST ^( PREDICATE (p= predicate ) ) ( ^( VALUE o= object ) )+ ) )* ) )
            // IbmSparqlAstWalker.g:400:6: ^( TRIPLE2 ^( SUBJECT (s= triplesNode ) ) ( ^( PROPERTY_LIST ^( PREDICATE (p= predicate ) ) ( ^( VALUE o= object ) )+ ) )* )
            {
            match(input,TRIPLE2,FOLLOW_TRIPLE2_in_triples22671); if (state.failed) return qt;

            match(input, Token.DOWN, null); if (state.failed) return qt;
            match(input,SUBJECT,FOLLOW_SUBJECT_in_triples22674); if (state.failed) return qt;

            match(input, Token.DOWN, null); if (state.failed) return qt;
            // IbmSparqlAstWalker.g:400:26: (s= triplesNode )
            // IbmSparqlAstWalker.g:400:28: s= triplesNode
            {
            pushFollow(FOLLOW_triplesNode_in_triples22680);
            s=triplesNode();

            state._fsp--;
            if (state.failed) return qt;
            if ( state.backtracking==0 ) {
               qt = new QueryTriple2(s);  
            }

            }


            match(input, Token.UP, null); if (state.failed) return qt;
            // IbmSparqlAstWalker.g:403:6: ( ^( PROPERTY_LIST ^( PREDICATE (p= predicate ) ) ( ^( VALUE o= object ) )+ ) )*
            loop46:
            do {
                int alt46=2;
                int LA46_0 = input.LA(1);

                if ( (LA46_0==PROPERTY_LIST) ) {
                    alt46=1;
                }


                switch (alt46) {
            	case 1 :
            	    // IbmSparqlAstWalker.g:403:8: ^( PROPERTY_LIST ^( PREDICATE (p= predicate ) ) ( ^( VALUE o= object ) )+ )
            	    {
            	    match(input,PROPERTY_LIST,FOLLOW_PROPERTY_LIST_in_triples22718); if (state.failed) return qt;

            	    match(input, Token.DOWN, null); if (state.failed) return qt;
            	    match(input,PREDICATE,FOLLOW_PREDICATE_in_triples22730); if (state.failed) return qt;

            	    match(input, Token.DOWN, null); if (state.failed) return qt;
            	    // IbmSparqlAstWalker.g:404:22: (p= predicate )
            	    // IbmSparqlAstWalker.g:404:24: p= predicate
            	    {
            	    pushFollow(FOLLOW_predicate_in_triples22737);
            	    p=predicate();

            	    state._fsp--;
            	    if (state.failed) return qt;
            	    if ( state.backtracking==0 ) {
            	       ple = new PropertyListElement(p);  
            	    }

            	    }


            	    match(input, Token.UP, null); if (state.failed) return qt;
            	    // IbmSparqlAstWalker.g:407:9: ( ^( VALUE o= object ) )+
            	    int cnt45=0;
            	    loop45:
            	    do {
            	        int alt45=2;
            	        int LA45_0 = input.LA(1);

            	        if ( (LA45_0==VALUE) ) {
            	            alt45=1;
            	        }


            	        switch (alt45) {
            	    	case 1 :
            	    	    // IbmSparqlAstWalker.g:407:11: ^( VALUE o= object )
            	    	    {
            	    	    match(input,VALUE,FOLLOW_VALUE_in_triples22787); if (state.failed) return qt;

            	    	    match(input, Token.DOWN, null); if (state.failed) return qt;
            	    	    pushFollow(FOLLOW_object_in_triples22792);
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
            	    	    if ( cnt45 >= 1 ) break loop45;
            	    	    if (state.backtracking>0) {state.failed=true; return qt;}
            	                EarlyExitException eee =
            	                    new EarlyExitException(45, input);
            	                throw eee;
            	        }
            	        cnt45++;
            	    } while (true);

            	    if ( state.backtracking==0 ) {
            	       qt.addPropertyListElement(ple); 
            	    }

            	    match(input, Token.UP, null); if (state.failed) return qt;

            	    }
            	    break;

            	default :
            	    break loop46;
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
    // IbmSparqlAstWalker.g:415:1: graphPatternNewBGP[PatternSet p, Set<BlankNodeVariable> vars] : (u= groupMinusOrUnionGraphPattern | o= optionalGraphPattern | g= graphGraphPattern | serviceGraphPattern | b= bind | v= inlineData );
    public final void graphPatternNewBGP(PatternSet p, Set<BlankNodeVariable> vars) throws RecognitionException {
        Pattern u = null;

        Pattern o = null;

        Pattern g = null;

        Pattern b = null;

        ValuesPattern v = null;


        try {
            // IbmSparqlAstWalker.g:416:5: (u= groupMinusOrUnionGraphPattern | o= optionalGraphPattern | g= graphGraphPattern | serviceGraphPattern | b= bind | v= inlineData )
            int alt47=6;
            switch ( input.LA(1) ) {
            case GROUP_GRAPH_PATTERN:
            case SUB_SELECT:
            case UNION:
            case MINUS:
                {
                alt47=1;
                }
                break;
            case OPTIONAL:
                {
                alt47=2;
                }
                break;
            case GRAPH:
                {
                alt47=3;
                }
                break;
            case SERVICE:
                {
                alt47=4;
                }
                break;
            case BIND:
                {
                alt47=5;
                }
                break;
            case VALUES:
                {
                alt47=6;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 47, 0, input);

                throw nvae;
            }

            switch (alt47) {
                case 1 :
                    // IbmSparqlAstWalker.g:416:9: u= groupMinusOrUnionGraphPattern
                    {
                    pushFollow(FOLLOW_groupMinusOrUnionGraphPattern_in_graphPatternNewBGP2852);
                    u=groupMinusOrUnionGraphPattern();

                    state._fsp--;
                    if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       p.addPattern(u); 
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlAstWalker.g:418:5: o= optionalGraphPattern
                    {
                    pushFollow(FOLLOW_optionalGraphPattern_in_graphPatternNewBGP2872);
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
                    // IbmSparqlAstWalker.g:425:5: g= graphGraphPattern
                    {
                    pushFollow(FOLLOW_graphGraphPattern_in_graphPatternNewBGP2893);
                    g=graphGraphPattern();

                    state._fsp--;
                    if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       
                                  p.addPattern(g);
                              
                    }

                    }
                    break;
                case 4 :
                    // IbmSparqlAstWalker.g:429:5: serviceGraphPattern
                    {
                    pushFollow(FOLLOW_serviceGraphPattern_in_graphPatternNewBGP2912);
                    serviceGraphPattern();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // IbmSparqlAstWalker.g:430:5: b= bind
                    {
                    pushFollow(FOLLOW_bind_in_graphPatternNewBGP2921);
                    b=bind();

                    state._fsp--;
                    if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                      					
                      			p.addPattern(b);
                      	    
                    }

                    }
                    break;
                case 6 :
                    // IbmSparqlAstWalker.g:434:9: v= inlineData
                    {
                    pushFollow(FOLLOW_inlineData_in_graphPatternNewBGP2941);
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
    // IbmSparqlAstWalker.g:440:1: inlineData returns [ValuesPattern v] : ^( VALUES d= dataBlock ) ;
    public final ValuesPattern inlineData() throws RecognitionException {
        ValuesPattern v = null;

        Values d = null;


        try {
            // IbmSparqlAstWalker.g:441:5: ( ^( VALUES d= dataBlock ) )
            // IbmSparqlAstWalker.g:441:9: ^( VALUES d= dataBlock )
            {
            match(input,VALUES,FOLLOW_VALUES_in_inlineData2974); if (state.failed) return v;

            match(input, Token.DOWN, null); if (state.failed) return v;
            pushFollow(FOLLOW_dataBlock_in_inlineData2978);
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
    // IbmSparqlAstWalker.g:444:1: dataBlock returns [Values values] : ^( INLINE_DATA ( ( NIL )=> NIL | (v= var )* ) ( ( NIL )=> NIL | (d= dataBlockValue )* ) ) ;
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
            // IbmSparqlAstWalker.g:451:5: ( ^( INLINE_DATA ( ( NIL )=> NIL | (v= var )* ) ( ( NIL )=> NIL | (d= dataBlockValue )* ) ) )
            // IbmSparqlAstWalker.g:451:9: ^( INLINE_DATA ( ( NIL )=> NIL | (v= var )* ) ( ( NIL )=> NIL | (d= dataBlockValue )* ) )
            {
            match(input,INLINE_DATA,FOLLOW_INLINE_DATA_in_dataBlock3010); if (state.failed) return values;

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); if (state.failed) return values;
                // IbmSparqlAstWalker.g:451:23: ( ( NIL )=> NIL | (v= var )* )
                int alt49=2;
                int LA49_0 = input.LA(1);

                if ( (LA49_0==NIL) ) {
                    int LA49_1 = input.LA(2);

                    if ( (synpred1_IbmSparqlAstWalker()) ) {
                        alt49=1;
                    }
                    else if ( (true) ) {
                        alt49=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return values;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 49, 1, input);

                        throw nvae;
                    }
                }
                else if ( (LA49_0==UP||(LA49_0>=VAR && LA49_0<=PREFIXED_NS)||(LA49_0>=STRING && LA49_0<=BOOLEAN)||(LA49_0>=BIG_INTEGER && LA49_0<=BIG_DECIMAL)||LA49_0==UNDEF||LA49_0==IRI||LA49_0==DOUBLE) ) {
                    alt49=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return values;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 49, 0, input);

                    throw nvae;
                }
                switch (alt49) {
                    case 1 :
                        // IbmSparqlAstWalker.g:451:25: ( NIL )=> NIL
                        {
                        match(input,NIL,FOLLOW_NIL_in_dataBlock3020); if (state.failed) return values;

                        }
                        break;
                    case 2 :
                        // IbmSparqlAstWalker.g:451:40: (v= var )*
                        {
                        // IbmSparqlAstWalker.g:451:40: (v= var )*
                        loop48:
                        do {
                            int alt48=2;
                            int LA48_0 = input.LA(1);

                            if ( (LA48_0==VAR) ) {
                                alt48=1;
                            }


                            switch (alt48) {
                        	case 1 :
                        	    // IbmSparqlAstWalker.g:451:41: v= var
                        	    {
                        	    pushFollow(FOLLOW_var_in_dataBlock3027);
                        	    v=var();

                        	    state._fsp--;
                        	    if (state.failed) return values;
                        	    if ( state.backtracking==0 ) {
                        	      vars.add(new Variable(v));
                        	    }

                        	    }
                        	    break;

                        	default :
                        	    break loop48;
                            }
                        } while (true);


                        }
                        break;

                }

                // IbmSparqlAstWalker.g:451:79: ( ( NIL )=> NIL | (d= dataBlockValue )* )
                int alt51=2;
                int LA51_0 = input.LA(1);

                if ( (LA51_0==NIL) && (synpred2_IbmSparqlAstWalker())) {
                    alt51=1;
                }
                else if ( (LA51_0==UP||(LA51_0>=PREFIXED_NAME && LA51_0<=PREFIXED_NS)||(LA51_0>=STRING && LA51_0<=BOOLEAN)||(LA51_0>=BIG_INTEGER && LA51_0<=BIG_DECIMAL)||LA51_0==UNDEF||LA51_0==IRI||LA51_0==DOUBLE) ) {
                    alt51=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return values;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 51, 0, input);

                    throw nvae;
                }
                switch (alt51) {
                    case 1 :
                        // IbmSparqlAstWalker.g:451:81: ( NIL )=> NIL
                        {
                        match(input,NIL,FOLLOW_NIL_in_dataBlock3042); if (state.failed) return values;

                        }
                        break;
                    case 2 :
                        // IbmSparqlAstWalker.g:451:96: (d= dataBlockValue )*
                        {
                        // IbmSparqlAstWalker.g:451:96: (d= dataBlockValue )*
                        loop50:
                        do {
                            int alt50=2;
                            int LA50_0 = input.LA(1);

                            if ( ((LA50_0>=PREFIXED_NAME && LA50_0<=PREFIXED_NS)||(LA50_0>=STRING && LA50_0<=BOOLEAN)||(LA50_0>=BIG_INTEGER && LA50_0<=BIG_DECIMAL)||LA50_0==UNDEF||LA50_0==IRI||LA50_0==DOUBLE) ) {
                                alt50=1;
                            }


                            switch (alt50) {
                        	case 1 :
                        	    // IbmSparqlAstWalker.g:451:97: d= dataBlockValue
                        	    {
                        	    pushFollow(FOLLOW_dataBlockValue_in_dataBlock3049);
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
                        	    break loop50;
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
    // IbmSparqlAstWalker.g:462:1: dataBlockValue returns [Expression e] : (i= iRIref | r= rDFLiteral | d= numericLiteral | b= booleanLiteral | u= UNDEF );
    public final Expression dataBlockValue() throws RecognitionException {
        Expression e = null;

        XTree u=null;
        IRI i = null;

        StringLiteral r = null;

        Constant d = null;

        Boolean b = null;


        try {
            // IbmSparqlAstWalker.g:463:2: (i= iRIref | r= rDFLiteral | d= numericLiteral | b= booleanLiteral | u= UNDEF )
            int alt52=5;
            switch ( input.LA(1) ) {
            case PREFIXED_NAME:
            case PREFIXED_NS:
            case IRI:
                {
                alt52=1;
                }
                break;
            case STRING:
                {
                alt52=2;
                }
                break;
            case BIG_INTEGER:
            case BIG_DECIMAL:
            case DOUBLE:
                {
                alt52=3;
                }
                break;
            case BOOLEAN:
                {
                alt52=4;
                }
                break;
            case UNDEF:
                {
                alt52=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return e;}
                NoViableAltException nvae =
                    new NoViableAltException("", 52, 0, input);

                throw nvae;
            }

            switch (alt52) {
                case 1 :
                    // IbmSparqlAstWalker.g:463:7: i= iRIref
                    {
                    pushFollow(FOLLOW_iRIref_in_dataBlockValue3085);
                    i=iRIref();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                      e = new ConstantExpression(i);
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlAstWalker.g:464:7: r= rDFLiteral
                    {
                    pushFollow(FOLLOW_rDFLiteral_in_dataBlockValue3101);
                    r=rDFLiteral();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                      e = new ConstantExpression(r);
                    }

                    }
                    break;
                case 3 :
                    // IbmSparqlAstWalker.g:465:7: d= numericLiteral
                    {
                    pushFollow(FOLLOW_numericLiteral_in_dataBlockValue3116);
                    d=numericLiteral();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                      e = new ConstantExpression(d);
                    }

                    }
                    break;
                case 4 :
                    // IbmSparqlAstWalker.g:466:7: b= booleanLiteral
                    {
                    pushFollow(FOLLOW_booleanLiteral_in_dataBlockValue3131);
                    b=booleanLiteral();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                      e = new ConstantExpression(b);
                    }

                    }
                    break;
                case 5 :
                    // IbmSparqlAstWalker.g:467:7: u= UNDEF
                    {
                    u=(XTree)match(input,UNDEF,FOLLOW_UNDEF_in_dataBlockValue3147); if (state.failed) return e;
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
    // IbmSparqlAstWalker.g:470:1: optionalGraphPattern returns [Pattern p] : ^( OPTIONAL g= groupGraphPattern[false] ) ;
    public final Pattern optionalGraphPattern() throws RecognitionException {
        Pattern p = null;

        Pattern g = null;


        try {
            // IbmSparqlAstWalker.g:471:2: ( ^( OPTIONAL g= groupGraphPattern[false] ) )
            // IbmSparqlAstWalker.g:471:7: ^( OPTIONAL g= groupGraphPattern[false] )
            {
            match(input,OPTIONAL,FOLLOW_OPTIONAL_in_optionalGraphPattern3177); if (state.failed) return p;

            match(input, Token.DOWN, null); if (state.failed) return p;
            pushFollow(FOLLOW_groupGraphPattern_in_optionalGraphPattern3181);
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
    // IbmSparqlAstWalker.g:474:1: graphGraphPattern returns [Pattern p] : ^( GRAPH r= varOrIRIref2 g= groupGraphPattern[false] ) ;
    public final Pattern graphGraphPattern() throws RecognitionException {
        Pattern p = null;

        BinaryUnion<Variable, IRI> r = null;

        Pattern g = null;


        try {
            // IbmSparqlAstWalker.g:475:2: ( ^( GRAPH r= varOrIRIref2 g= groupGraphPattern[false] ) )
            // IbmSparqlAstWalker.g:475:7: ^( GRAPH r= varOrIRIref2 g= groupGraphPattern[false] )
            {
            match(input,GRAPH,FOLLOW_GRAPH_in_graphGraphPattern3211); if (state.failed) return p;

            match(input, Token.DOWN, null); if (state.failed) return p;
            pushFollow(FOLLOW_varOrIRIref2_in_graphGraphPattern3215);
            r=varOrIRIref2();

            state._fsp--;
            if (state.failed) return p;
            pushFollow(FOLLOW_groupGraphPattern_in_graphGraphPattern3219);
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
    // IbmSparqlAstWalker.g:478:1: serviceGraphPattern returns [Pattern p] : ^(top= SERVICE ( SILENT )? s= varOrIRIref g= groupGraphPattern[false] ) ;
    public final Pattern serviceGraphPattern() throws RecognitionException {
        Pattern p = null;

        XTree top=null;
        QueryTripleTerm s = null;

        Pattern g = null;


         boolean silent=false; 
        try {
            // IbmSparqlAstWalker.g:480:2: ( ^(top= SERVICE ( SILENT )? s= varOrIRIref g= groupGraphPattern[false] ) )
            // IbmSparqlAstWalker.g:480:7: ^(top= SERVICE ( SILENT )? s= varOrIRIref g= groupGraphPattern[false] )
            {
            top=(XTree)match(input,SERVICE,FOLLOW_SERVICE_in_serviceGraphPattern3255); if (state.failed) return p;

            match(input, Token.DOWN, null); if (state.failed) return p;
            // IbmSparqlAstWalker.g:480:21: ( SILENT )?
            int alt53=2;
            int LA53_0 = input.LA(1);

            if ( (LA53_0==SILENT) ) {
                alt53=1;
            }
            switch (alt53) {
                case 1 :
                    // IbmSparqlAstWalker.g:480:22: SILENT
                    {
                    match(input,SILENT,FOLLOW_SILENT_in_serviceGraphPattern3258); if (state.failed) return p;
                    if ( state.backtracking==0 ) {
                       silent=true; 
                    }

                    }
                    break;

            }

            pushFollow(FOLLOW_varOrIRIref_in_serviceGraphPattern3266);
            s=varOrIRIref();

            state._fsp--;
            if (state.failed) return p;
            pushFollow(FOLLOW_groupGraphPattern_in_serviceGraphPattern3270);
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
    // IbmSparqlAstWalker.g:486:1: bind returns [Pattern p] : ^( BIND v= var e= expression ) ;
    public final Pattern bind() throws RecognitionException {
        Pattern p = null;

        String v = null;

        Expression e = null;


        try {
            // IbmSparqlAstWalker.g:487:2: ( ^( BIND v= var e= expression ) )
            // IbmSparqlAstWalker.g:487:7: ^( BIND v= var e= expression )
            {
            match(input,BIND,FOLLOW_BIND_in_bind3303); if (state.failed) return p;

            match(input, Token.DOWN, null); if (state.failed) return p;
            pushFollow(FOLLOW_var_in_bind3307);
            v=var();

            state._fsp--;
            if (state.failed) return p;
            pushFollow(FOLLOW_expression_in_bind3313);
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
    // $ANTLR end "bind"


    // $ANTLR start "groupMinusOrUnionGraphPattern"
    // IbmSparqlAstWalker.g:490:1: groupMinusOrUnionGraphPattern returns [Pattern r] : ( ^( UNION g1= groupGraphPattern[false] (g2= groupGraphPattern[false] )+ ) | ^( MINUS g1= groupGraphPattern[false] ) | g3= groupGraphPattern[false] );
    public final Pattern groupMinusOrUnionGraphPattern() throws RecognitionException {
        Pattern r = null;

        Pattern g1 = null;

        Pattern g2 = null;

        Pattern g3 = null;



        	  PatternSet p = null;
        	
        try {
            // IbmSparqlAstWalker.g:494:2: ( ^( UNION g1= groupGraphPattern[false] (g2= groupGraphPattern[false] )+ ) | ^( MINUS g1= groupGraphPattern[false] ) | g3= groupGraphPattern[false] )
            int alt55=3;
            switch ( input.LA(1) ) {
            case UNION:
                {
                alt55=1;
                }
                break;
            case MINUS:
                {
                alt55=2;
                }
                break;
            case GROUP_GRAPH_PATTERN:
            case SUB_SELECT:
                {
                alt55=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return r;}
                NoViableAltException nvae =
                    new NoViableAltException("", 55, 0, input);

                throw nvae;
            }

            switch (alt55) {
                case 1 :
                    // IbmSparqlAstWalker.g:494:6: ^( UNION g1= groupGraphPattern[false] (g2= groupGraphPattern[false] )+ )
                    {
                    match(input,UNION,FOLLOW_UNION_in_groupMinusOrUnionGraphPattern3345); if (state.failed) return r;

                    match(input, Token.DOWN, null); if (state.failed) return r;
                    pushFollow(FOLLOW_groupGraphPattern_in_groupMinusOrUnionGraphPattern3366);
                    g1=groupGraphPattern(false);

                    state._fsp--;
                    if (state.failed) return r;
                    if ( state.backtracking==0 ) {
                       p = new PatternSet(PatternSet.EPatternSetType.UNION); 
                      			      r = p;
                                        p.addPattern(g1);	
                    }
                    // IbmSparqlAstWalker.g:499:5: (g2= groupGraphPattern[false] )+
                    int cnt54=0;
                    loop54:
                    do {
                        int alt54=2;
                        int LA54_0 = input.LA(1);

                        if ( (LA54_0==GROUP_GRAPH_PATTERN||LA54_0==SUB_SELECT) ) {
                            alt54=1;
                        }


                        switch (alt54) {
                    	case 1 :
                    	    // IbmSparqlAstWalker.g:499:6: g2= groupGraphPattern[false]
                    	    {
                    	    pushFollow(FOLLOW_groupGraphPattern_in_groupMinusOrUnionGraphPattern3397);
                    	    g2=groupGraphPattern(false);

                    	    state._fsp--;
                    	    if (state.failed) return r;
                    	    if ( state.backtracking==0 ) {
                    	       
                    	                         p.addPattern(g2); 
                    	                       
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt54 >= 1 ) break loop54;
                    	    if (state.backtracking>0) {state.failed=true; return r;}
                                EarlyExitException eee =
                                    new EarlyExitException(54, input);
                                throw eee;
                        }
                        cnt54++;
                    } while (true);


                    match(input, Token.UP, null); if (state.failed) return r;

                    }
                    break;
                case 2 :
                    // IbmSparqlAstWalker.g:503:9: ^( MINUS g1= groupGraphPattern[false] )
                    {
                    match(input,MINUS,FOLLOW_MINUS_in_groupMinusOrUnionGraphPattern3433); if (state.failed) return r;

                    match(input, Token.DOWN, null); if (state.failed) return r;
                    pushFollow(FOLLOW_groupGraphPattern_in_groupMinusOrUnionGraphPattern3448);
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
                    // IbmSparqlAstWalker.g:508:7: g3= groupGraphPattern[false]
                    {
                    pushFollow(FOLLOW_groupGraphPattern_in_groupMinusOrUnionGraphPattern3475);
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
    // IbmSparqlAstWalker.g:514:1: filter returns [Expression e] : ^( FILTER c= constraint ) ;
    public final Expression filter() throws RecognitionException {
        Expression e = null;

        Expression c = null;


        try {
            // IbmSparqlAstWalker.g:515:2: ( ^( FILTER c= constraint ) )
            // IbmSparqlAstWalker.g:515:6: ^( FILTER c= constraint )
            {
            match(input,FILTER,FOLLOW_FILTER_in_filter3508); if (state.failed) return e;

            match(input, Token.DOWN, null); if (state.failed) return e;
            pushFollow(FOLLOW_constraint_in_filter3512);
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
    // IbmSparqlAstWalker.g:518:1: constraint returns [Expression e] : (be= brackettedExpression | bc= builtInCall | fc= functionCall );
    public final Expression constraint() throws RecognitionException {
        Expression e = null;

        Expression be = null;

        Expression bc = null;

        FunctionCall fc = null;


        try {
            // IbmSparqlAstWalker.g:519:2: (be= brackettedExpression | bc= builtInCall | fc= functionCall )
            int alt56=3;
            switch ( input.LA(1) ) {
            case EXPRESSION:
                {
                alt56=1;
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
                alt56=2;
                }
                break;
            case FUNCTION:
                {
                alt56=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return e;}
                NoViableAltException nvae =
                    new NoViableAltException("", 56, 0, input);

                throw nvae;
            }

            switch (alt56) {
                case 1 :
                    // IbmSparqlAstWalker.g:519:6: be= brackettedExpression
                    {
                    pushFollow(FOLLOW_brackettedExpression_in_constraint3536);
                    be=brackettedExpression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = be; 
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlAstWalker.g:520:5: bc= builtInCall
                    {
                    pushFollow(FOLLOW_builtInCall_in_constraint3546);
                    bc=builtInCall();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = bc; 
                    }

                    }
                    break;
                case 3 :
                    // IbmSparqlAstWalker.g:521:5: fc= functionCall
                    {
                    pushFollow(FOLLOW_functionCall_in_constraint3559);
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
    // IbmSparqlAstWalker.g:524:1: functionCall returns [FunctionCall fc] : ^( FUNCTION i= iRIref a= argList ) ;
    public final FunctionCall functionCall() throws RecognitionException {
        FunctionCall fc = null;

        IRI i = null;

        List<Expression> a = null;


        try {
            // IbmSparqlAstWalker.g:525:2: ( ^( FUNCTION i= iRIref a= argList ) )
            // IbmSparqlAstWalker.g:525:6: ^( FUNCTION i= iRIref a= argList )
            {
            match(input,FUNCTION,FOLLOW_FUNCTION_in_functionCall3581); if (state.failed) return fc;

            match(input, Token.DOWN, null); if (state.failed) return fc;
            pushFollow(FOLLOW_iRIref_in_functionCall3585);
            i=iRIref();

            state._fsp--;
            if (state.failed) return fc;
            pushFollow(FOLLOW_argList_in_functionCall3589);
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
    // IbmSparqlAstWalker.g:528:1: argList returns [List<Expression> args] : ( NIL | DISTINCT (e1= expression )+ | (e2= expression )+ );
    public final List<Expression> argList() throws RecognitionException {
        List<Expression> args = null;

        Expression e1 = null;

        Expression e2 = null;


         args = new ArrayList<Expression>(); 
        try {
            // IbmSparqlAstWalker.g:530:5: ( NIL | DISTINCT (e1= expression )+ | (e2= expression )+ )
            int alt59=3;
            switch ( input.LA(1) ) {
            case NIL:
                {
                alt59=1;
                }
                break;
            case DISTINCT:
                {
                alt59=2;
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
            case 251:
            case 254:
            case 257:
            case 258:
            case 259:
            case 260:
            case 261:
            case 262:
            case 263:
                {
                alt59=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return args;}
                NoViableAltException nvae =
                    new NoViableAltException("", 59, 0, input);

                throw nvae;
            }

            switch (alt59) {
                case 1 :
                    // IbmSparqlAstWalker.g:530:9: NIL
                    {
                    match(input,NIL,FOLLOW_NIL_in_argList3618); if (state.failed) return args;

                    }
                    break;
                case 2 :
                    // IbmSparqlAstWalker.g:531:6: DISTINCT (e1= expression )+
                    {
                    match(input,DISTINCT,FOLLOW_DISTINCT_in_argList3631); if (state.failed) return args;
                    // IbmSparqlAstWalker.g:531:15: (e1= expression )+
                    int cnt57=0;
                    loop57:
                    do {
                        int alt57=2;
                        int LA57_0 = input.LA(1);

                        if ( ((LA57_0>=BROKEN_PLUS && LA57_0<=BROKEN_MINUS)||(LA57_0>=VAR && LA57_0<=NOT_EXISTS)||(LA57_0>=STRING && LA57_0<=BOOLEAN)||LA57_0==LTE||(LA57_0>=BIG_INTEGER && LA57_0<=BIG_DECIMAL)||(LA57_0>=LOGICAL_OR && LA57_0<=SHA1)||(LA57_0>=SHA256 && LA57_0<=GROUP_CONCAT)||LA57_0==DOUBLE||LA57_0==251||LA57_0==254||(LA57_0>=257 && LA57_0<=263)) ) {
                            alt57=1;
                        }


                        switch (alt57) {
                    	case 1 :
                    	    // IbmSparqlAstWalker.g:531:16: e1= expression
                    	    {
                    	    pushFollow(FOLLOW_expression_in_argList3636);
                    	    e1=expression();

                    	    state._fsp--;
                    	    if (state.failed) return args;
                    	    if ( state.backtracking==0 ) {
                    	       args.add(e1); 
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt57 >= 1 ) break loop57;
                    	    if (state.backtracking>0) {state.failed=true; return args;}
                                EarlyExitException eee =
                                    new EarlyExitException(57, input);
                                throw eee;
                        }
                        cnt57++;
                    } while (true);


                    }
                    break;
                case 3 :
                    // IbmSparqlAstWalker.g:532:4: (e2= expression )+
                    {
                    // IbmSparqlAstWalker.g:532:4: (e2= expression )+
                    int cnt58=0;
                    loop58:
                    do {
                        int alt58=2;
                        int LA58_0 = input.LA(1);

                        if ( ((LA58_0>=BROKEN_PLUS && LA58_0<=BROKEN_MINUS)||(LA58_0>=VAR && LA58_0<=NOT_EXISTS)||(LA58_0>=STRING && LA58_0<=BOOLEAN)||LA58_0==LTE||(LA58_0>=BIG_INTEGER && LA58_0<=BIG_DECIMAL)||(LA58_0>=LOGICAL_OR && LA58_0<=SHA1)||(LA58_0>=SHA256 && LA58_0<=GROUP_CONCAT)||LA58_0==DOUBLE||LA58_0==251||LA58_0==254||(LA58_0>=257 && LA58_0<=263)) ) {
                            alt58=1;
                        }


                        switch (alt58) {
                    	case 1 :
                    	    // IbmSparqlAstWalker.g:532:5: e2= expression
                    	    {
                    	    pushFollow(FOLLOW_expression_in_argList3649);
                    	    e2=expression();

                    	    state._fsp--;
                    	    if (state.failed) return args;
                    	    if ( state.backtracking==0 ) {
                    	       args.add(e2); 
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt58 >= 1 ) break loop58;
                    	    if (state.backtracking>0) {state.failed=true; return args;}
                                EarlyExitException eee =
                                    new EarlyExitException(58, input);
                                throw eee;
                        }
                        cnt58++;
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
    // IbmSparqlAstWalker.g:535:1: expressionList returns [List<Expression> exprs] : ( NIL | (e= expression )+ );
    public final List<Expression> expressionList() throws RecognitionException {
        List<Expression> exprs = null;

        Expression e = null;


         exprs = new ArrayList<Expression>(); 
        try {
            // IbmSparqlAstWalker.g:537:2: ( NIL | (e= expression )+ )
            int alt61=2;
            int LA61_0 = input.LA(1);

            if ( (LA61_0==NIL) ) {
                alt61=1;
            }
            else if ( ((LA61_0>=BROKEN_PLUS && LA61_0<=BROKEN_MINUS)||(LA61_0>=VAR && LA61_0<=NOT_EXISTS)||(LA61_0>=STRING && LA61_0<=BOOLEAN)||LA61_0==LTE||(LA61_0>=BIG_INTEGER && LA61_0<=BIG_DECIMAL)||(LA61_0>=LOGICAL_OR && LA61_0<=SHA1)||(LA61_0>=SHA256 && LA61_0<=GROUP_CONCAT)||LA61_0==DOUBLE||LA61_0==251||LA61_0==254||(LA61_0>=257 && LA61_0<=263)) ) {
                alt61=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return exprs;}
                NoViableAltException nvae =
                    new NoViableAltException("", 61, 0, input);

                throw nvae;
            }
            switch (alt61) {
                case 1 :
                    // IbmSparqlAstWalker.g:537:6: NIL
                    {
                    match(input,NIL,FOLLOW_NIL_in_expressionList3684); if (state.failed) return exprs;

                    }
                    break;
                case 2 :
                    // IbmSparqlAstWalker.g:538:6: (e= expression )+
                    {
                    // IbmSparqlAstWalker.g:538:6: (e= expression )+
                    int cnt60=0;
                    loop60:
                    do {
                        int alt60=2;
                        int LA60_0 = input.LA(1);

                        if ( ((LA60_0>=BROKEN_PLUS && LA60_0<=BROKEN_MINUS)||(LA60_0>=VAR && LA60_0<=NOT_EXISTS)||(LA60_0>=STRING && LA60_0<=BOOLEAN)||LA60_0==LTE||(LA60_0>=BIG_INTEGER && LA60_0<=BIG_DECIMAL)||(LA60_0>=LOGICAL_OR && LA60_0<=SHA1)||(LA60_0>=SHA256 && LA60_0<=GROUP_CONCAT)||LA60_0==DOUBLE||LA60_0==251||LA60_0==254||(LA60_0>=257 && LA60_0<=263)) ) {
                            alt60=1;
                        }


                        switch (alt60) {
                    	case 1 :
                    	    // IbmSparqlAstWalker.g:538:8: e= expression
                    	    {
                    	    pushFollow(FOLLOW_expression_in_expressionList3707);
                    	    e=expression();

                    	    state._fsp--;
                    	    if (state.failed) return exprs;
                    	    if ( state.backtracking==0 ) {
                    	       exprs.add(e); 
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt60 >= 1 ) break loop60;
                    	    if (state.backtracking>0) {state.failed=true; return exprs;}
                                EarlyExitException eee =
                                    new EarlyExitException(60, input);
                                throw eee;
                        }
                        cnt60++;
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
    // IbmSparqlAstWalker.g:542:1: constructTemplate[ConstructQuery cq] : ( constructTriples[$cq] )? ;
    public final void constructTemplate(ConstructQuery cq) throws RecognitionException {
        try {
            // IbmSparqlAstWalker.g:543:2: ( ( constructTriples[$cq] )? )
            // IbmSparqlAstWalker.g:543:6: ( constructTriples[$cq] )?
            {
            // IbmSparqlAstWalker.g:543:6: ( constructTriples[$cq] )?
            int alt62=2;
            int LA62_0 = input.LA(1);

            if ( ((LA62_0>=TRIPLE && LA62_0<=TRIPLE2)) ) {
                alt62=1;
            }
            switch (alt62) {
                case 1 :
                    // IbmSparqlAstWalker.g:543:6: constructTriples[$cq]
                    {
                    pushFollow(FOLLOW_constructTriples_in_constructTemplate3727);
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
    // IbmSparqlAstWalker.g:546:1: constructTriples[ConstructQuery cq] : (t= triples | t2= triples2 )+ ;
    public final void constructTriples(ConstructQuery cq) throws RecognitionException {
        QueryTriple t = null;

        QueryTriple2 t2 = null;


        try {
            // IbmSparqlAstWalker.g:547:2: ( (t= triples | t2= triples2 )+ )
            // IbmSparqlAstWalker.g:547:6: (t= triples | t2= triples2 )+
            {
            // IbmSparqlAstWalker.g:547:6: (t= triples | t2= triples2 )+
            int cnt63=0;
            loop63:
            do {
                int alt63=3;
                int LA63_0 = input.LA(1);

                if ( (LA63_0==TRIPLE) ) {
                    alt63=1;
                }
                else if ( (LA63_0==TRIPLE2) ) {
                    alt63=2;
                }


                switch (alt63) {
            	case 1 :
            	    // IbmSparqlAstWalker.g:547:8: t= triples
            	    {
            	    pushFollow(FOLLOW_triples_in_constructTriples3748);
            	    t=triples();

            	    state._fsp--;
            	    if (state.failed) return ;
            	    if ( state.backtracking==0 ) {
            	      cq.addConstructPattern(t);
            	    }

            	    }
            	    break;
            	case 2 :
            	    // IbmSparqlAstWalker.g:548:5: t2= triples2
            	    {
            	    pushFollow(FOLLOW_triples2_in_constructTriples3759);
            	    t2=triples2();

            	    state._fsp--;
            	    if (state.failed) return ;
            	    if ( state.backtracking==0 ) {
            	      cq.addConstructPattern(t2);
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt63 >= 1 ) break loop63;
            	    if (state.backtracking>0) {state.failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(63, input);
                        throw eee;
                }
                cnt63++;
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
    // IbmSparqlAstWalker.g:552:1: triplesSameSubject[ConstructQuery cq] returns [SimplePattern sp] : ( (t1= triples )+ | t12= triples2 );
    public final SimplePattern triplesSameSubject(ConstructQuery cq) throws RecognitionException {
        SimplePattern sp = null;

        QueryTriple t1 = null;

        QueryTriple2 t12 = null;


         sp = new SimplePattern(); 
        try {
            // IbmSparqlAstWalker.g:554:2: ( (t1= triples )+ | t12= triples2 )
            int alt65=2;
            int LA65_0 = input.LA(1);

            if ( (LA65_0==TRIPLE) ) {
                alt65=1;
            }
            else if ( (LA65_0==TRIPLE2) ) {
                alt65=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return sp;}
                NoViableAltException nvae =
                    new NoViableAltException("", 65, 0, input);

                throw nvae;
            }
            switch (alt65) {
                case 1 :
                    // IbmSparqlAstWalker.g:554:4: (t1= triples )+
                    {
                    // IbmSparqlAstWalker.g:554:4: (t1= triples )+
                    int cnt64=0;
                    loop64:
                    do {
                        int alt64=2;
                        int LA64_0 = input.LA(1);

                        if ( (LA64_0==TRIPLE) ) {
                            alt64=1;
                        }


                        switch (alt64) {
                    	case 1 :
                    	    // IbmSparqlAstWalker.g:554:5: t1= triples
                    	    {
                    	    pushFollow(FOLLOW_triples_in_triplesSameSubject3796);
                    	    t1=triples();

                    	    state._fsp--;
                    	    if (state.failed) return sp;
                    	    if ( state.backtracking==0 ) {
                    	       cq.addConstructPattern(t1); t1.expandAndAddTo(sp); 
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt64 >= 1 ) break loop64;
                    	    if (state.backtracking>0) {state.failed=true; return sp;}
                                EarlyExitException eee =
                                    new EarlyExitException(64, input);
                                throw eee;
                        }
                        cnt64++;
                    } while (true);


                    }
                    break;
                case 2 :
                    // IbmSparqlAstWalker.g:555:4: t12= triples2
                    {
                    pushFollow(FOLLOW_triples2_in_triplesSameSubject3808);
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
    // IbmSparqlAstWalker.g:558:1: object returns [GraphNode r] : g= graphNode ;
    public final GraphNode object() throws RecognitionException {
        GraphNode r = null;

        GraphNode g = null;


        try {
            // IbmSparqlAstWalker.g:559:2: (g= graphNode )
            // IbmSparqlAstWalker.g:559:6: g= graphNode
            {
            pushFollow(FOLLOW_graphNode_in_object3830);
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
    // IbmSparqlAstWalker.g:562:1: verb returns [QueryTripleTerm t] : (v= varOrIRIref | 'a' );
    public final QueryTripleTerm verb() throws RecognitionException {
        QueryTripleTerm t = null;

        QueryTripleTerm v = null;


        try {
            // IbmSparqlAstWalker.g:563:2: (v= varOrIRIref | 'a' )
            int alt66=2;
            int LA66_0 = input.LA(1);

            if ( ((LA66_0>=VAR && LA66_0<=PREFIXED_NS)||LA66_0==IRI) ) {
                alt66=1;
            }
            else if ( (LA66_0==252) ) {
                alt66=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return t;}
                NoViableAltException nvae =
                    new NoViableAltException("", 66, 0, input);

                throw nvae;
            }
            switch (alt66) {
                case 1 :
                    // IbmSparqlAstWalker.g:563:6: v= varOrIRIref
                    {
                    pushFollow(FOLLOW_varOrIRIref_in_verb3852);
                    v=varOrIRIref();

                    state._fsp--;
                    if (state.failed) return t;
                    if ( state.backtracking==0 ) {
                       t = v; 
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlAstWalker.g:564:5: 'a'
                    {
                    match(input,252,FOLLOW_252_in_verb3862); if (state.failed) return t;
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
    // IbmSparqlAstWalker.g:567:1: verbPath : path ;
    public final void verbPath() throws RecognitionException {
        try {
            // IbmSparqlAstWalker.g:568:2: ( path )
            // IbmSparqlAstWalker.g:568:6: path
            {
            pushFollow(FOLLOW_path_in_verbPath3880);
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
    // IbmSparqlAstWalker.g:571:1: verbSimple returns [Variable v] : x= var ;
    public final Variable verbSimple() throws RecognitionException {
        Variable v = null;

        String x = null;


        try {
            // IbmSparqlAstWalker.g:572:2: (x= var )
            // IbmSparqlAstWalker.g:572:6: x= var
            {
            pushFollow(FOLLOW_var_in_verbSimple3899);
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
    // IbmSparqlAstWalker.g:575:1: path : pathAlternative ;
    public final void path() throws RecognitionException {
        try {
            // IbmSparqlAstWalker.g:576:2: ( pathAlternative )
            // IbmSparqlAstWalker.g:576:6: pathAlternative
            {
            pushFollow(FOLLOW_pathAlternative_in_path3916);
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
    // IbmSparqlAstWalker.g:579:1: pathAlternative : pathSequence ( '|' pathSequence )* ;
    public final void pathAlternative() throws RecognitionException {
        try {
            // IbmSparqlAstWalker.g:580:2: ( pathSequence ( '|' pathSequence )* )
            // IbmSparqlAstWalker.g:580:6: pathSequence ( '|' pathSequence )*
            {
            pushFollow(FOLLOW_pathSequence_in_pathAlternative3929);
            pathSequence();

            state._fsp--;
            if (state.failed) return ;
            // IbmSparqlAstWalker.g:580:19: ( '|' pathSequence )*
            loop67:
            do {
                int alt67=2;
                int LA67_0 = input.LA(1);

                if ( (LA67_0==253) ) {
                    alt67=1;
                }


                switch (alt67) {
            	case 1 :
            	    // IbmSparqlAstWalker.g:580:21: '|' pathSequence
            	    {
            	    match(input,253,FOLLOW_253_in_pathAlternative3933); if (state.failed) return ;
            	    pushFollow(FOLLOW_pathSequence_in_pathAlternative3935);
            	    pathSequence();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop67;
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
    // IbmSparqlAstWalker.g:583:1: pathSequence : pathEltOrInverse ( '/' pathEltOrInverse )* ;
    public final void pathSequence() throws RecognitionException {
        try {
            // IbmSparqlAstWalker.g:584:2: ( pathEltOrInverse ( '/' pathEltOrInverse )* )
            // IbmSparqlAstWalker.g:584:7: pathEltOrInverse ( '/' pathEltOrInverse )*
            {
            pushFollow(FOLLOW_pathEltOrInverse_in_pathSequence3952);
            pathEltOrInverse();

            state._fsp--;
            if (state.failed) return ;
            // IbmSparqlAstWalker.g:584:24: ( '/' pathEltOrInverse )*
            loop68:
            do {
                int alt68=2;
                int LA68_0 = input.LA(1);

                if ( (LA68_0==254) ) {
                    alt68=1;
                }


                switch (alt68) {
            	case 1 :
            	    // IbmSparqlAstWalker.g:584:26: '/' pathEltOrInverse
            	    {
            	    match(input,254,FOLLOW_254_in_pathSequence3956); if (state.failed) return ;
            	    pushFollow(FOLLOW_pathEltOrInverse_in_pathSequence3958);
            	    pathEltOrInverse();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop68;
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
    // IbmSparqlAstWalker.g:587:1: pathElt : pathPrimary ( pathMod )? ;
    public final void pathElt() throws RecognitionException {
        try {
            // IbmSparqlAstWalker.g:588:2: ( pathPrimary ( pathMod )? )
            // IbmSparqlAstWalker.g:588:7: pathPrimary ( pathMod )?
            {
            pushFollow(FOLLOW_pathPrimary_in_pathElt3975);
            pathPrimary();

            state._fsp--;
            if (state.failed) return ;
            // IbmSparqlAstWalker.g:588:19: ( pathMod )?
            int alt69=2;
            int LA69_0 = input.LA(1);

            if ( (LA69_0==251||(LA69_0>=256 && LA69_0<=257)) ) {
                alt69=1;
            }
            switch (alt69) {
                case 1 :
                    // IbmSparqlAstWalker.g:588:19: pathMod
                    {
                    pushFollow(FOLLOW_pathMod_in_pathElt3977);
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
    // IbmSparqlAstWalker.g:591:1: pathEltOrInverse : ( pathElt | '^' pathElt );
    public final void pathEltOrInverse() throws RecognitionException {
        try {
            // IbmSparqlAstWalker.g:592:2: ( pathElt | '^' pathElt )
            int alt70=2;
            int LA70_0 = input.LA(1);

            if ( ((LA70_0>=PREFIXED_NAME && LA70_0<=PREFIXED_NS)||LA70_0==OPEN_BRACE||LA70_0==IRI||LA70_0==252||LA70_0==258) ) {
                alt70=1;
            }
            else if ( (LA70_0==255) ) {
                alt70=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 70, 0, input);

                throw nvae;
            }
            switch (alt70) {
                case 1 :
                    // IbmSparqlAstWalker.g:592:7: pathElt
                    {
                    pushFollow(FOLLOW_pathElt_in_pathEltOrInverse3992);
                    pathElt();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // IbmSparqlAstWalker.g:592:17: '^' pathElt
                    {
                    match(input,255,FOLLOW_255_in_pathEltOrInverse3996); if (state.failed) return ;
                    pushFollow(FOLLOW_pathElt_in_pathEltOrInverse3998);
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
    // IbmSparqlAstWalker.g:595:1: pathMod returns [Path.PathMod v] : ( '*' | '?' | '+' );
    public final Path.PathMod pathMod() throws RecognitionException {
        Path.PathMod v = null;

        try {
            // IbmSparqlAstWalker.g:596:2: ( '*' | '?' | '+' )
            int alt71=3;
            switch ( input.LA(1) ) {
            case 251:
                {
                alt71=1;
                }
                break;
            case 256:
                {
                alt71=2;
                }
                break;
            case 257:
                {
                alt71=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 71, 0, input);

                throw nvae;
            }

            switch (alt71) {
                case 1 :
                    // IbmSparqlAstWalker.g:596:4: '*'
                    {
                    match(input,251,FOLLOW_251_in_pathMod4013); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = Path.PathMod.ZERO_OR_MORE; 
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlAstWalker.g:597:4: '?'
                    {
                    match(input,256,FOLLOW_256_in_pathMod4022); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = Path.PathMod.ZERO_OR_ONE; 
                    }

                    }
                    break;
                case 3 :
                    // IbmSparqlAstWalker.g:598:4: '+'
                    {
                    match(input,257,FOLLOW_257_in_pathMod4031); if (state.failed) return v;
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
    // IbmSparqlAstWalker.g:601:1: pathPrimary : ( iRIref | 'a' | '!' pathNegatedPropertySet | OPEN_BRACE path CLOSE_BRACE );
    public final void pathPrimary() throws RecognitionException {
        try {
            // IbmSparqlAstWalker.g:602:2: ( iRIref | 'a' | '!' pathNegatedPropertySet | OPEN_BRACE path CLOSE_BRACE )
            int alt72=4;
            switch ( input.LA(1) ) {
            case PREFIXED_NAME:
            case PREFIXED_NS:
            case IRI:
                {
                alt72=1;
                }
                break;
            case 252:
                {
                alt72=2;
                }
                break;
            case 258:
                {
                alt72=3;
                }
                break;
            case OPEN_BRACE:
                {
                alt72=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 72, 0, input);

                throw nvae;
            }

            switch (alt72) {
                case 1 :
                    // IbmSparqlAstWalker.g:602:7: iRIref
                    {
                    pushFollow(FOLLOW_iRIref_in_pathPrimary4049);
                    iRIref();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // IbmSparqlAstWalker.g:602:16: 'a'
                    {
                    match(input,252,FOLLOW_252_in_pathPrimary4053); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // IbmSparqlAstWalker.g:602:22: '!' pathNegatedPropertySet
                    {
                    match(input,258,FOLLOW_258_in_pathPrimary4057); if (state.failed) return ;
                    pushFollow(FOLLOW_pathNegatedPropertySet_in_pathPrimary4059);
                    pathNegatedPropertySet();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // IbmSparqlAstWalker.g:602:51: OPEN_BRACE path CLOSE_BRACE
                    {
                    match(input,OPEN_BRACE,FOLLOW_OPEN_BRACE_in_pathPrimary4063); if (state.failed) return ;
                    pushFollow(FOLLOW_path_in_pathPrimary4065);
                    path();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,CLOSE_BRACE,FOLLOW_CLOSE_BRACE_in_pathPrimary4067); if (state.failed) return ;

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
    // IbmSparqlAstWalker.g:605:1: pathNegatedPropertySet returns [Pair<? extends List<IRI>, ? extends List<IRI>> pair] : (v= pathOneInPropertySet | OPEN_BRACE (v1= pathOneInPropertySet ( '|' v2= pathOneInPropertySet )* )? CLOSE_BRACE );
    public final Pair<? extends List<IRI>, ? extends List<IRI>> pathNegatedPropertySet() throws RecognitionException {
        Pair<? extends List<IRI>, ? extends List<IRI>> pair = null;

        Pair<IRI, Boolean> v = null;

        Pair<IRI, Boolean> v1 = null;

        Pair<IRI, Boolean> v2 = null;


         pair = Pair.make(new LinkedList<IRI>(), new LinkedList<IRI>()); 
        try {
            // IbmSparqlAstWalker.g:607:2: (v= pathOneInPropertySet | OPEN_BRACE (v1= pathOneInPropertySet ( '|' v2= pathOneInPropertySet )* )? CLOSE_BRACE )
            int alt75=2;
            int LA75_0 = input.LA(1);

            if ( (LA75_0==INV||(LA75_0>=PREFIXED_NAME && LA75_0<=PREFIXED_NS)||LA75_0==IRI||LA75_0==252) ) {
                alt75=1;
            }
            else if ( (LA75_0==OPEN_BRACE) ) {
                alt75=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return pair;}
                NoViableAltException nvae =
                    new NoViableAltException("", 75, 0, input);

                throw nvae;
            }
            switch (alt75) {
                case 1 :
                    // IbmSparqlAstWalker.g:607:7: v= pathOneInPropertySet
                    {
                    pushFollow(FOLLOW_pathOneInPropertySet_in_pathNegatedPropertySet4097);
                    v=pathOneInPropertySet();

                    state._fsp--;
                    if (state.failed) return pair;
                    if ( state.backtracking==0 ) {
                       if (v.snd ) { pair.fst.add(v.fst); } else {pair.snd.add(v.fst);} 
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlAstWalker.g:608:4: OPEN_BRACE (v1= pathOneInPropertySet ( '|' v2= pathOneInPropertySet )* )? CLOSE_BRACE
                    {
                    match(input,OPEN_BRACE,FOLLOW_OPEN_BRACE_in_pathNegatedPropertySet4105); if (state.failed) return pair;
                    // IbmSparqlAstWalker.g:608:15: (v1= pathOneInPropertySet ( '|' v2= pathOneInPropertySet )* )?
                    int alt74=2;
                    int LA74_0 = input.LA(1);

                    if ( (LA74_0==INV||(LA74_0>=PREFIXED_NAME && LA74_0<=PREFIXED_NS)||LA74_0==IRI||LA74_0==252) ) {
                        alt74=1;
                    }
                    switch (alt74) {
                        case 1 :
                            // IbmSparqlAstWalker.g:608:17: v1= pathOneInPropertySet ( '|' v2= pathOneInPropertySet )*
                            {
                            pushFollow(FOLLOW_pathOneInPropertySet_in_pathNegatedPropertySet4113);
                            v1=pathOneInPropertySet();

                            state._fsp--;
                            if (state.failed) return pair;
                            // IbmSparqlAstWalker.g:608:43: ( '|' v2= pathOneInPropertySet )*
                            loop73:
                            do {
                                int alt73=2;
                                int LA73_0 = input.LA(1);

                                if ( (LA73_0==253) ) {
                                    alt73=1;
                                }


                                switch (alt73) {
                            	case 1 :
                            	    // IbmSparqlAstWalker.g:608:45: '|' v2= pathOneInPropertySet
                            	    {
                            	    match(input,253,FOLLOW_253_in_pathNegatedPropertySet4117); if (state.failed) return pair;
                            	    pushFollow(FOLLOW_pathOneInPropertySet_in_pathNegatedPropertySet4123);
                            	    v2=pathOneInPropertySet();

                            	    state._fsp--;
                            	    if (state.failed) return pair;
                            	    if ( state.backtracking==0 ) {
                            	       if (v2.snd ) { pair.fst.add(v2.fst); } else {pair.snd.add(v2.fst);} 
                            	    }

                            	    }
                            	    break;

                            	default :
                            	    break loop73;
                                }
                            } while (true);


                            }
                            break;

                    }

                    match(input,CLOSE_BRACE,FOLLOW_CLOSE_BRACE_in_pathNegatedPropertySet4134); if (state.failed) return pair;
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
    // IbmSparqlAstWalker.g:612:1: pathOneInPropertySet returns [Pair<IRI, Boolean> v ] : (i= iRIref | 'a' | ^( INV invi= iRIrefOrRDFType ) );
    public final Pair<IRI, Boolean> pathOneInPropertySet() throws RecognitionException {
        Pair<IRI, Boolean> v = null;

        IRI i = null;

        IRI invi = null;


        try {
            // IbmSparqlAstWalker.g:613:4: (i= iRIref | 'a' | ^( INV invi= iRIrefOrRDFType ) )
            int alt76=3;
            switch ( input.LA(1) ) {
            case PREFIXED_NAME:
            case PREFIXED_NS:
            case IRI:
                {
                alt76=1;
                }
                break;
            case 252:
                {
                alt76=2;
                }
                break;
            case INV:
                {
                alt76=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 76, 0, input);

                throw nvae;
            }

            switch (alt76) {
                case 1 :
                    // IbmSparqlAstWalker.g:613:7: i= iRIref
                    {
                    pushFollow(FOLLOW_iRIref_in_pathOneInPropertySet4162);
                    i=iRIref();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = Pair.make(i, true); 
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlAstWalker.g:614:4: 'a'
                    {
                    match(input,252,FOLLOW_252_in_pathOneInPropertySet4169); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = Pair.make(new IRI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"), true); 
                    }

                    }
                    break;
                case 3 :
                    // IbmSparqlAstWalker.g:615:4: ^( INV invi= iRIrefOrRDFType )
                    {
                    match(input,INV,FOLLOW_INV_in_pathOneInPropertySet4178); if (state.failed) return v;

                    match(input, Token.DOWN, null); if (state.failed) return v;
                    pushFollow(FOLLOW_iRIrefOrRDFType_in_pathOneInPropertySet4184);
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
    // IbmSparqlAstWalker.g:618:1: iRIrefOrRDFType returns [ IRI v ] : (i= iRIref | 'a' );
    public final IRI iRIrefOrRDFType() throws RecognitionException {
        IRI v = null;

        IRI i = null;


        try {
            // IbmSparqlAstWalker.g:619:4: (i= iRIref | 'a' )
            int alt77=2;
            int LA77_0 = input.LA(1);

            if ( ((LA77_0>=PREFIXED_NAME && LA77_0<=PREFIXED_NS)||LA77_0==IRI) ) {
                alt77=1;
            }
            else if ( (LA77_0==252) ) {
                alt77=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 77, 0, input);

                throw nvae;
            }
            switch (alt77) {
                case 1 :
                    // IbmSparqlAstWalker.g:619:6: i= iRIref
                    {
                    pushFollow(FOLLOW_iRIref_in_iRIrefOrRDFType4211);
                    i=iRIref();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = i;
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlAstWalker.g:620:6: 'a'
                    {
                    match(input,252,FOLLOW_252_in_iRIrefOrRDFType4220); if (state.failed) return v;
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
    // IbmSparqlAstWalker.g:623:1: integer : INTEGER ;
    public final void integer() throws RecognitionException {
        try {
            // IbmSparqlAstWalker.g:624:2: ( INTEGER )
            // IbmSparqlAstWalker.g:624:7: INTEGER
            {
            match(input,INTEGER,FOLLOW_INTEGER_in_integer4241); if (state.failed) return ;

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
    // IbmSparqlAstWalker.g:627:1: triplesNode returns [TriplesNode tn] : ^( TRIPLES_NODE (c= collection | b= blankNodePropertyList ) ) ;
    public final TriplesNode triplesNode() throws RecognitionException {
        TriplesNode tn = null;

        RDFCollection c = null;

        PropertyList b = null;


        try {
            // IbmSparqlAstWalker.g:628:2: ( ^( TRIPLES_NODE (c= collection | b= blankNodePropertyList ) ) )
            // IbmSparqlAstWalker.g:628:6: ^( TRIPLES_NODE (c= collection | b= blankNodePropertyList ) )
            {
            match(input,TRIPLES_NODE,FOLLOW_TRIPLES_NODE_in_triplesNode4260); if (state.failed) return tn;

            match(input, Token.DOWN, null); if (state.failed) return tn;
            // IbmSparqlAstWalker.g:629:5: (c= collection | b= blankNodePropertyList )
            int alt78=2;
            int LA78_0 = input.LA(1);

            if ( (LA78_0==COLLECTION) ) {
                alt78=1;
            }
            else if ( (LA78_0==PROPERTY_LIST) ) {
                alt78=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return tn;}
                NoViableAltException nvae =
                    new NoViableAltException("", 78, 0, input);

                throw nvae;
            }
            switch (alt78) {
                case 1 :
                    // IbmSparqlAstWalker.g:629:7: c= collection
                    {
                    pushFollow(FOLLOW_collection_in_triplesNode4271);
                    c=collection();

                    state._fsp--;
                    if (state.failed) return tn;
                    if ( state.backtracking==0 ) {
                       tn = new TriplesNode(c);
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlAstWalker.g:630:8: b= blankNodePropertyList
                    {
                    pushFollow(FOLLOW_blankNodePropertyList_in_triplesNode4288);
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
    // IbmSparqlAstWalker.g:635:1: blankNodePropertyList returns [PropertyList pl] : ^( PROPERTY_LIST ( ^( PREDICATE v= predicate ) ^( VALUE (o= graphNode )+ ) )+ ) ;
    public final PropertyList blankNodePropertyList() throws RecognitionException {
        PropertyList pl = null;

        BinaryUnion<Variable, Path> v = null;

        GraphNode o = null;



        			pl = new PropertyList(); 
        			PropertyListElement e = null;
        		
        try {
            // IbmSparqlAstWalker.g:640:2: ( ^( PROPERTY_LIST ( ^( PREDICATE v= predicate ) ^( VALUE (o= graphNode )+ ) )+ ) )
            // IbmSparqlAstWalker.g:640:4: ^( PROPERTY_LIST ( ^( PREDICATE v= predicate ) ^( VALUE (o= graphNode )+ ) )+ )
            {
            match(input,PROPERTY_LIST,FOLLOW_PROPERTY_LIST_in_blankNodePropertyList4327); if (state.failed) return pl;

            match(input, Token.DOWN, null); if (state.failed) return pl;
            // IbmSparqlAstWalker.g:641:6: ( ^( PREDICATE v= predicate ) ^( VALUE (o= graphNode )+ ) )+
            int cnt80=0;
            loop80:
            do {
                int alt80=2;
                int LA80_0 = input.LA(1);

                if ( (LA80_0==PREDICATE) ) {
                    alt80=1;
                }


                switch (alt80) {
            	case 1 :
            	    // IbmSparqlAstWalker.g:641:8: ^( PREDICATE v= predicate ) ^( VALUE (o= graphNode )+ )
            	    {
            	    if ( state.backtracking==0 ) {
            	       e = new PropertyListElement(); 
            	    }
            	    match(input,PREDICATE,FOLLOW_PREDICATE_in_blankNodePropertyList4348); if (state.failed) return pl;

            	    match(input, Token.DOWN, null); if (state.failed) return pl;
            	    pushFollow(FOLLOW_predicate_in_blankNodePropertyList4352);
            	    v=predicate();

            	    state._fsp--;
            	    if (state.failed) return pl;

            	    match(input, Token.UP, null); if (state.failed) return pl;
            	    if ( state.backtracking==0 ) {
            	      e.setVerb(v);
            	    }
            	    match(input,VALUE,FOLLOW_VALUE_in_blankNodePropertyList4367); if (state.failed) return pl;

            	    match(input, Token.DOWN, null); if (state.failed) return pl;
            	    // IbmSparqlAstWalker.g:643:17: (o= graphNode )+
            	    int cnt79=0;
            	    loop79:
            	    do {
            	        int alt79=2;
            	        int LA79_0 = input.LA(1);

            	        if ( ((LA79_0>=NIL && LA79_0<=ANNON)||(LA79_0>=VAR && LA79_0<=PREFIXED_NS)||(LA79_0>=STRING && LA79_0<=BOOLEAN)||LA79_0==TRIPLES_NODE||(LA79_0>=BIG_INTEGER && LA79_0<=BIG_DECIMAL)||LA79_0==IRI||LA79_0==DOUBLE||LA79_0==BLANK_NODE_LABEL) ) {
            	            alt79=1;
            	        }


            	        switch (alt79) {
            	    	case 1 :
            	    	    // IbmSparqlAstWalker.g:643:18: o= graphNode
            	    	    {
            	    	    pushFollow(FOLLOW_graphNode_in_blankNodePropertyList4372);
            	    	    o=graphNode();

            	    	    state._fsp--;
            	    	    if (state.failed) return pl;
            	    	    if ( state.backtracking==0 ) {
            	    	      e.addObject(o);
            	    	    }

            	    	    }
            	    	    break;

            	    	default :
            	    	    if ( cnt79 >= 1 ) break loop79;
            	    	    if (state.backtracking>0) {state.failed=true; return pl;}
            	                EarlyExitException eee =
            	                    new EarlyExitException(79, input);
            	                throw eee;
            	        }
            	        cnt79++;
            	    } while (true);


            	    match(input, Token.UP, null); if (state.failed) return pl;
            	    if ( state.backtracking==0 ) {
            	       pl.add(e); 
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt80 >= 1 ) break loop80;
            	    if (state.backtracking>0) {state.failed=true; return pl;}
                        EarlyExitException eee =
                            new EarlyExitException(80, input);
                        throw eee;
                }
                cnt80++;
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
    // IbmSparqlAstWalker.g:649:1: predicate returns [BinaryUnion<Variable, Path> p] : (v= var | i= iRIref | 'a' | ^( ALT (alt= predicate )* ) | ^( SEQ (seq= predicate )* ) | ^( ELT pred= predicate mod= pathMod ) | ^( INV pred= predicate ) | '!' neg= pathNegatedPropertySet );
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
            // IbmSparqlAstWalker.g:651:2: (v= var | i= iRIref | 'a' | ^( ALT (alt= predicate )* ) | ^( SEQ (seq= predicate )* ) | ^( ELT pred= predicate mod= pathMod ) | ^( INV pred= predicate ) | '!' neg= pathNegatedPropertySet )
            int alt83=8;
            switch ( input.LA(1) ) {
            case VAR:
                {
                alt83=1;
                }
                break;
            case PREFIXED_NAME:
            case PREFIXED_NS:
            case IRI:
                {
                alt83=2;
                }
                break;
            case 252:
                {
                alt83=3;
                }
                break;
            case ALT:
                {
                alt83=4;
                }
                break;
            case SEQ:
                {
                alt83=5;
                }
                break;
            case ELT:
                {
                alt83=6;
                }
                break;
            case INV:
                {
                alt83=7;
                }
                break;
            case 258:
                {
                alt83=8;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return p;}
                NoViableAltException nvae =
                    new NoViableAltException("", 83, 0, input);

                throw nvae;
            }

            switch (alt83) {
                case 1 :
                    // IbmSparqlAstWalker.g:651:4: v= var
                    {
                    pushFollow(FOLLOW_var_in_predicate4428);
                    v=var();

                    state._fsp--;
                    if (state.failed) return p;
                    if ( state.backtracking==0 ) {
                       p.setFirst(new Variable(v));  
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlAstWalker.g:652:4: i= iRIref
                    {
                    pushFollow(FOLLOW_iRIref_in_predicate4443);
                    i=iRIref();

                    state._fsp--;
                    if (state.failed) return p;
                    if ( state.backtracking==0 ) {
                       p.setSecond(new SimplePath(i)); 
                    }

                    }
                    break;
                case 3 :
                    // IbmSparqlAstWalker.g:653:5: 'a'
                    {
                    match(input,252,FOLLOW_252_in_predicate4454); if (state.failed) return p;
                    if ( state.backtracking==0 ) {
                       p.setSecond(new SimplePath(new IRI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"))); 
                    }

                    }
                    break;
                case 4 :
                    // IbmSparqlAstWalker.g:654:9: ^( ALT (alt= predicate )* )
                    {
                    match(input,ALT,FOLLOW_ALT_in_predicate4469); if (state.failed) return p;

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); if (state.failed) return p;
                        // IbmSparqlAstWalker.g:654:15: (alt= predicate )*
                        loop81:
                        do {
                            int alt81=2;
                            int LA81_0 = input.LA(1);

                            if ( ((LA81_0>=ALT && LA81_0<=INV)||(LA81_0>=VAR && LA81_0<=PREFIXED_NS)||LA81_0==IRI||LA81_0==252||LA81_0==258) ) {
                                alt81=1;
                            }


                            switch (alt81) {
                        	case 1 :
                        	    // IbmSparqlAstWalker.g:654:16: alt= predicate
                        	    {
                        	    pushFollow(FOLLOW_predicate_in_predicate4474);
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
                        	    break loop81;
                            }
                        } while (true);


                        match(input, Token.UP, null); if (state.failed) return p;
                    }

                    }
                    break;
                case 5 :
                    // IbmSparqlAstWalker.g:662:9: ^( SEQ (seq= predicate )* )
                    {
                    match(input,SEQ,FOLLOW_SEQ_in_predicate4490); if (state.failed) return p;

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); if (state.failed) return p;
                        // IbmSparqlAstWalker.g:662:15: (seq= predicate )*
                        loop82:
                        do {
                            int alt82=2;
                            int LA82_0 = input.LA(1);

                            if ( ((LA82_0>=ALT && LA82_0<=INV)||(LA82_0>=VAR && LA82_0<=PREFIXED_NS)||LA82_0==IRI||LA82_0==252||LA82_0==258) ) {
                                alt82=1;
                            }


                            switch (alt82) {
                        	case 1 :
                        	    // IbmSparqlAstWalker.g:662:16: seq= predicate
                        	    {
                        	    pushFollow(FOLLOW_predicate_in_predicate4495);
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
                        	    break loop82;
                            }
                        } while (true);


                        match(input, Token.UP, null); if (state.failed) return p;
                    }

                    }
                    break;
                case 6 :
                    // IbmSparqlAstWalker.g:670:9: ^( ELT pred= predicate mod= pathMod )
                    {
                    match(input,ELT,FOLLOW_ELT_in_predicate4511); if (state.failed) return p;

                    match(input, Token.DOWN, null); if (state.failed) return p;
                    pushFollow(FOLLOW_predicate_in_predicate4515);
                    pred=predicate();

                    state._fsp--;
                    if (state.failed) return p;
                    pushFollow(FOLLOW_pathMod_in_predicate4521);
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
                    // IbmSparqlAstWalker.g:671:9: ^( INV pred= predicate )
                    {
                    match(input,INV,FOLLOW_INV_in_predicate4536); if (state.failed) return p;

                    match(input, Token.DOWN, null); if (state.failed) return p;
                    pushFollow(FOLLOW_predicate_in_predicate4540);
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
                    // IbmSparqlAstWalker.g:672:7: '!' neg= pathNegatedPropertySet
                    {
                    match(input,258,FOLLOW_258_in_predicate4551); if (state.failed) return p;
                    pushFollow(FOLLOW_pathNegatedPropertySet_in_predicate4557);
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
    // IbmSparqlAstWalker.g:675:1: collection returns [RDFCollection c] : ^( COLLECTION (g= graphNode )+ ) ;
    public final RDFCollection collection() throws RecognitionException {
        RDFCollection c = null;

        GraphNode g = null;


         c = new RDFCollection(); 
        try {
            // IbmSparqlAstWalker.g:677:2: ( ^( COLLECTION (g= graphNode )+ ) )
            // IbmSparqlAstWalker.g:677:7: ^( COLLECTION (g= graphNode )+ )
            {
            match(input,COLLECTION,FOLLOW_COLLECTION_in_collection4585); if (state.failed) return c;

            match(input, Token.DOWN, null); if (state.failed) return c;
            // IbmSparqlAstWalker.g:677:21: (g= graphNode )+
            int cnt84=0;
            loop84:
            do {
                int alt84=2;
                int LA84_0 = input.LA(1);

                if ( ((LA84_0>=NIL && LA84_0<=ANNON)||(LA84_0>=VAR && LA84_0<=PREFIXED_NS)||(LA84_0>=STRING && LA84_0<=BOOLEAN)||LA84_0==TRIPLES_NODE||(LA84_0>=BIG_INTEGER && LA84_0<=BIG_DECIMAL)||LA84_0==IRI||LA84_0==DOUBLE||LA84_0==BLANK_NODE_LABEL) ) {
                    alt84=1;
                }


                switch (alt84) {
            	case 1 :
            	    // IbmSparqlAstWalker.g:677:22: g= graphNode
            	    {
            	    pushFollow(FOLLOW_graphNode_in_collection4590);
            	    g=graphNode();

            	    state._fsp--;
            	    if (state.failed) return c;
            	    if ( state.backtracking==0 ) {
            	       c.add(g); 
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt84 >= 1 ) break loop84;
            	    if (state.backtracking>0) {state.failed=true; return c;}
                        EarlyExitException eee =
                            new EarlyExitException(84, input);
                        throw eee;
                }
                cnt84++;
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
    // IbmSparqlAstWalker.g:680:1: graphNode returns [GraphNode gn] : (v= var | g= graphTerm | t= triplesNode );
    public final GraphNode graphNode() throws RecognitionException {
        GraphNode gn = null;

        String v = null;

        GraphTerm g = null;

        TriplesNode t = null;


        try {
            // IbmSparqlAstWalker.g:681:2: (v= var | g= graphTerm | t= triplesNode )
            int alt85=3;
            switch ( input.LA(1) ) {
            case VAR:
                {
                alt85=1;
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
                alt85=2;
                }
                break;
            case TRIPLES_NODE:
                {
                alt85=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return gn;}
                NoViableAltException nvae =
                    new NoViableAltException("", 85, 0, input);

                throw nvae;
            }

            switch (alt85) {
                case 1 :
                    // IbmSparqlAstWalker.g:681:6: v= var
                    {
                    pushFollow(FOLLOW_var_in_graphNode4616);
                    v=var();

                    state._fsp--;
                    if (state.failed) return gn;
                    if ( state.backtracking==0 ) {
                       gn = new GraphNode(new Variable(v));			
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlAstWalker.g:682:5: g= graphTerm
                    {
                    pushFollow(FOLLOW_graphTerm_in_graphNode4629);
                    g=graphTerm();

                    state._fsp--;
                    if (state.failed) return gn;
                    if ( state.backtracking==0 ) {
                       gn = new GraphNode(g);						
                    }

                    }
                    break;
                case 3 :
                    // IbmSparqlAstWalker.g:683:5: t= triplesNode
                    {
                    pushFollow(FOLLOW_triplesNode_in_graphNode4641);
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
    // IbmSparqlAstWalker.g:686:1: varOrTerm returns [QueryTripleTerm qt] : (v= var | g= graphTerm );
    public final QueryTripleTerm varOrTerm() throws RecognitionException {
        QueryTripleTerm qt = null;

        String v = null;

        GraphTerm g = null;


        try {
            // IbmSparqlAstWalker.g:687:2: (v= var | g= graphTerm )
            int alt86=2;
            int LA86_0 = input.LA(1);

            if ( (LA86_0==VAR) ) {
                alt86=1;
            }
            else if ( ((LA86_0>=NIL && LA86_0<=ANNON)||(LA86_0>=PREFIXED_NAME && LA86_0<=PREFIXED_NS)||(LA86_0>=STRING && LA86_0<=BOOLEAN)||(LA86_0>=BIG_INTEGER && LA86_0<=BIG_DECIMAL)||LA86_0==IRI||LA86_0==DOUBLE||LA86_0==BLANK_NODE_LABEL) ) {
                alt86=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return qt;}
                NoViableAltException nvae =
                    new NoViableAltException("", 86, 0, input);

                throw nvae;
            }
            switch (alt86) {
                case 1 :
                    // IbmSparqlAstWalker.g:687:7: v= var
                    {
                    pushFollow(FOLLOW_var_in_varOrTerm4664);
                    v=var();

                    state._fsp--;
                    if (state.failed) return qt;
                    if ( state.backtracking==0 ) {
                       qt = new QueryTripleTerm(new Variable(v)); 	
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlAstWalker.g:688:6: g= graphTerm
                    {
                    pushFollow(FOLLOW_graphTerm_in_varOrTerm4679);
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
    // IbmSparqlAstWalker.g:691:1: varOrIRIref returns [QueryTripleTerm qt] : (v= var | i= iRIref );
    public final QueryTripleTerm varOrIRIref() throws RecognitionException {
        QueryTripleTerm qt = null;

        String v = null;

        IRI i = null;


        try {
            // IbmSparqlAstWalker.g:692:2: (v= var | i= iRIref )
            int alt87=2;
            int LA87_0 = input.LA(1);

            if ( (LA87_0==VAR) ) {
                alt87=1;
            }
            else if ( ((LA87_0>=PREFIXED_NAME && LA87_0<=PREFIXED_NS)||LA87_0==IRI) ) {
                alt87=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return qt;}
                NoViableAltException nvae =
                    new NoViableAltException("", 87, 0, input);

                throw nvae;
            }
            switch (alt87) {
                case 1 :
                    // IbmSparqlAstWalker.g:692:6: v= var
                    {
                    pushFollow(FOLLOW_var_in_varOrIRIref4701);
                    v=var();

                    state._fsp--;
                    if (state.failed) return qt;
                    if ( state.backtracking==0 ) {
                       qt = new QueryTripleTerm(new Variable(v)); 	
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlAstWalker.g:693:5: i= iRIref
                    {
                    pushFollow(FOLLOW_iRIref_in_varOrIRIref4716);
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
    // IbmSparqlAstWalker.g:696:1: varOrIRIref2 returns [BinaryUnion<Variable, IRI> bu] : (v= var | i= iRIref );
    public final BinaryUnion<Variable, IRI> varOrIRIref2() throws RecognitionException {
        BinaryUnion<Variable, IRI> bu = null;

        String v = null;

        IRI i = null;


         bu = new BinaryUnion<Variable, IRI>(); 
        try {
            // IbmSparqlAstWalker.g:698:5: (v= var | i= iRIref )
            int alt88=2;
            int LA88_0 = input.LA(1);

            if ( (LA88_0==VAR) ) {
                alt88=1;
            }
            else if ( ((LA88_0>=PREFIXED_NAME && LA88_0<=PREFIXED_NS)||LA88_0==IRI) ) {
                alt88=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return bu;}
                NoViableAltException nvae =
                    new NoViableAltException("", 88, 0, input);

                throw nvae;
            }
            switch (alt88) {
                case 1 :
                    // IbmSparqlAstWalker.g:698:8: v= var
                    {
                    pushFollow(FOLLOW_var_in_varOrIRIref24751);
                    v=var();

                    state._fsp--;
                    if (state.failed) return bu;
                    if ( state.backtracking==0 ) {
                       bu.setFirst(new Variable(v)); 
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlAstWalker.g:699:8: i= iRIref
                    {
                    pushFollow(FOLLOW_iRIref_in_varOrIRIref24767);
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
    // IbmSparqlAstWalker.g:702:1: var returns [String v] : ^( VAR (x1= VAR1 | x2= VAR2 ) ) ;
    public final String var() throws RecognitionException {
        String v = null;

        XTree x1=null;
        XTree x2=null;

        try {
            // IbmSparqlAstWalker.g:703:2: ( ^( VAR (x1= VAR1 | x2= VAR2 ) ) )
            // IbmSparqlAstWalker.g:703:6: ^( VAR (x1= VAR1 | x2= VAR2 ) )
            {
            match(input,VAR,FOLLOW_VAR_in_var4794); if (state.failed) return v;

            match(input, Token.DOWN, null); if (state.failed) return v;
            // IbmSparqlAstWalker.g:703:13: (x1= VAR1 | x2= VAR2 )
            int alt89=2;
            int LA89_0 = input.LA(1);

            if ( (LA89_0==VAR1) ) {
                alt89=1;
            }
            else if ( (LA89_0==VAR2) ) {
                alt89=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 89, 0, input);

                throw nvae;
            }
            switch (alt89) {
                case 1 :
                    // IbmSparqlAstWalker.g:703:15: x1= VAR1
                    {
                    x1=(XTree)match(input,VAR1,FOLLOW_VAR1_in_var4801); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = new String(x1.getText().substring(1)); 
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlAstWalker.g:704:7: x2= VAR2
                    {
                    x2=(XTree)match(input,VAR2,FOLLOW_VAR2_in_var4816); if (state.failed) return v;
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
    // IbmSparqlAstWalker.g:709:1: graphTerm returns [GraphTerm gt] : (i= iRIref | r= rDFLiteral | d= numericLiteral | b= booleanLiteral | l= blankNode | NIL );
    public final GraphTerm graphTerm() throws RecognitionException {
        GraphTerm gt = null;

        IRI i = null;

        StringLiteral r = null;

        Constant d = null;

        Boolean b = null;

        BlankNode l = null;


        try {
            // IbmSparqlAstWalker.g:710:2: (i= iRIref | r= rDFLiteral | d= numericLiteral | b= booleanLiteral | l= blankNode | NIL )
            int alt90=6;
            switch ( input.LA(1) ) {
            case PREFIXED_NAME:
            case PREFIXED_NS:
            case IRI:
                {
                alt90=1;
                }
                break;
            case STRING:
                {
                alt90=2;
                }
                break;
            case BIG_INTEGER:
            case BIG_DECIMAL:
            case DOUBLE:
                {
                alt90=3;
                }
                break;
            case BOOLEAN:
                {
                alt90=4;
                }
                break;
            case ANNON:
            case BLANK_NODE_LABEL:
                {
                alt90=5;
                }
                break;
            case NIL:
                {
                alt90=6;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return gt;}
                NoViableAltException nvae =
                    new NoViableAltException("", 90, 0, input);

                throw nvae;
            }

            switch (alt90) {
                case 1 :
                    // IbmSparqlAstWalker.g:710:6: i= iRIref
                    {
                    pushFollow(FOLLOW_iRIref_in_graphTerm4850);
                    i=iRIref();

                    state._fsp--;
                    if (state.failed) return gt;
                    if ( state.backtracking==0 ) {
                       gt = new GraphTerm(i); 
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlAstWalker.g:711:5: r= rDFLiteral
                    {
                    pushFollow(FOLLOW_rDFLiteral_in_graphTerm4864);
                    r=rDFLiteral();

                    state._fsp--;
                    if (state.failed) return gt;
                    if ( state.backtracking==0 ) {
                       gt = new GraphTerm(new Constant(r)); 
                    }

                    }
                    break;
                case 3 :
                    // IbmSparqlAstWalker.g:712:4: d= numericLiteral
                    {
                    pushFollow(FOLLOW_numericLiteral_in_graphTerm4876);
                    d=numericLiteral();

                    state._fsp--;
                    if (state.failed) return gt;
                    if ( state.backtracking==0 ) {
                       gt = new GraphTerm(d); 				
                    }

                    }
                    break;
                case 4 :
                    // IbmSparqlAstWalker.g:713:5: b= booleanLiteral
                    {
                    pushFollow(FOLLOW_booleanLiteral_in_graphTerm4889);
                    b=booleanLiteral();

                    state._fsp--;
                    if (state.failed) return gt;
                    if ( state.backtracking==0 ) {
                       gt = new GraphTerm(new Constant(b)); 
                    }

                    }
                    break;
                case 5 :
                    // IbmSparqlAstWalker.g:714:5: l= blankNode
                    {
                    pushFollow(FOLLOW_blankNode_in_graphTerm4902);
                    l=blankNode();

                    state._fsp--;
                    if (state.failed) return gt;
                    if ( state.backtracking==0 ) {
                       gt = new GraphTerm(l); 
                    }

                    }
                    break;
                case 6 :
                    // IbmSparqlAstWalker.g:715:5: NIL
                    {
                    match(input,NIL,FOLLOW_NIL_in_graphTerm4915); if (state.failed) return gt;
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
    // IbmSparqlAstWalker.g:718:1: expression returns [Expression e] : ( ^( LOGICAL_OR e1= expression (e2= expression )+ ) | ^( LOGICAL_AND e3= expression (e4= expression )+ ) | ^( '=' e5= expression e6= expression ) | ^( '!=' e7= expression e8= expression ) | ^( LT e9= expression e10= expression ) | ^( '>' e11= expression e12= expression ) | ^( LTE e13= expression e14= expression ) | ^( '>=' e15= expression e16= expression ) | ^( IN e17= expression e18= expressionList ) | ^( NOT e19= expression e20= expressionList ) | ^( '+' e21= expression (e22= expression )? ) | ^( BROKEN_PLUS e21= expression e22= expression ) | ^( '-' e23= expression (e24= expression )? ) | ^( BROKEN_MINUS e23= expression e24= expression ) | ^( '*' e25= expression e26= expression ) | ^( '/' e27= expression e28= expression ) | ^( '!' e29= expression ) | e30= brackettedExpression | e31= primaryExpression );
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
            // IbmSparqlAstWalker.g:724:2: ( ^( LOGICAL_OR e1= expression (e2= expression )+ ) | ^( LOGICAL_AND e3= expression (e4= expression )+ ) | ^( '=' e5= expression e6= expression ) | ^( '!=' e7= expression e8= expression ) | ^( LT e9= expression e10= expression ) | ^( '>' e11= expression e12= expression ) | ^( LTE e13= expression e14= expression ) | ^( '>=' e15= expression e16= expression ) | ^( IN e17= expression e18= expressionList ) | ^( NOT e19= expression e20= expressionList ) | ^( '+' e21= expression (e22= expression )? ) | ^( BROKEN_PLUS e21= expression e22= expression ) | ^( '-' e23= expression (e24= expression )? ) | ^( BROKEN_MINUS e23= expression e24= expression ) | ^( '*' e25= expression e26= expression ) | ^( '/' e27= expression e28= expression ) | ^( '!' e29= expression ) | e30= brackettedExpression | e31= primaryExpression )
            int alt95=19;
            switch ( input.LA(1) ) {
            case LOGICAL_OR:
                {
                alt95=1;
                }
                break;
            case LOGICAL_AND:
                {
                alt95=2;
                }
                break;
            case 259:
                {
                alt95=3;
                }
                break;
            case 260:
                {
                alt95=4;
                }
                break;
            case LT:
                {
                alt95=5;
                }
                break;
            case 261:
                {
                alt95=6;
                }
                break;
            case LTE:
                {
                alt95=7;
                }
                break;
            case 262:
                {
                alt95=8;
                }
                break;
            case IN:
                {
                alt95=9;
                }
                break;
            case NOT:
                {
                alt95=10;
                }
                break;
            case 257:
                {
                alt95=11;
                }
                break;
            case BROKEN_PLUS:
                {
                alt95=12;
                }
                break;
            case 263:
                {
                alt95=13;
                }
                break;
            case BROKEN_MINUS:
                {
                alt95=14;
                }
                break;
            case 251:
                {
                alt95=15;
                }
                break;
            case 254:
                {
                alt95=16;
                }
                break;
            case 258:
                {
                alt95=17;
                }
                break;
            case EXPRESSION:
                {
                alt95=18;
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
                alt95=19;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return e;}
                NoViableAltException nvae =
                    new NoViableAltException("", 95, 0, input);

                throw nvae;
            }

            switch (alt95) {
                case 1 :
                    // IbmSparqlAstWalker.g:724:6: ^( LOGICAL_OR e1= expression (e2= expression )+ )
                    {
                    match(input,LOGICAL_OR,FOLLOW_LOGICAL_OR_in_expression4946); if (state.failed) return e;

                    if ( state.backtracking==0 ) {
                       le = new LogicalExpression(LogicalExpression.ELogicalType.OR);  
                    }

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression4958);
                    e1=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       le.addComponent(e1);   
                    }
                    // IbmSparqlAstWalker.g:726:4: (e2= expression )+
                    int cnt91=0;
                    loop91:
                    do {
                        int alt91=2;
                        int LA91_0 = input.LA(1);

                        if ( ((LA91_0>=BROKEN_PLUS && LA91_0<=BROKEN_MINUS)||(LA91_0>=VAR && LA91_0<=NOT_EXISTS)||(LA91_0>=STRING && LA91_0<=BOOLEAN)||LA91_0==LTE||(LA91_0>=BIG_INTEGER && LA91_0<=BIG_DECIMAL)||(LA91_0>=LOGICAL_OR && LA91_0<=SHA1)||(LA91_0>=SHA256 && LA91_0<=GROUP_CONCAT)||LA91_0==DOUBLE||LA91_0==251||LA91_0==254||(LA91_0>=257 && LA91_0<=263)) ) {
                            alt91=1;
                        }


                        switch (alt91) {
                    	case 1 :
                    	    // IbmSparqlAstWalker.g:726:5: e2= expression
                    	    {
                    	    pushFollow(FOLLOW_expression_in_expression4979);
                    	    e2=expression();

                    	    state._fsp--;
                    	    if (state.failed) return e;
                    	    if ( state.backtracking==0 ) {
                    	       le.addComponent(e2); 	
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt91 >= 1 ) break loop91;
                    	    if (state.backtracking>0) {state.failed=true; return e;}
                                EarlyExitException eee =
                                    new EarlyExitException(91, input);
                                throw eee;
                        }
                        cnt91++;
                    } while (true);


                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = le; 
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlAstWalker.g:728:6: ^( LOGICAL_AND e3= expression (e4= expression )+ )
                    {
                    match(input,LOGICAL_AND,FOLLOW_LOGICAL_AND_in_expression5004); if (state.failed) return e;

                    if ( state.backtracking==0 ) {
                       le = new LogicalExpression(LogicalExpression.ELogicalType.AND); 
                    }

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression5016);
                    e3=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       le.addComponent(e3);   
                    }
                    // IbmSparqlAstWalker.g:730:4: (e4= expression )+
                    int cnt92=0;
                    loop92:
                    do {
                        int alt92=2;
                        int LA92_0 = input.LA(1);

                        if ( ((LA92_0>=BROKEN_PLUS && LA92_0<=BROKEN_MINUS)||(LA92_0>=VAR && LA92_0<=NOT_EXISTS)||(LA92_0>=STRING && LA92_0<=BOOLEAN)||LA92_0==LTE||(LA92_0>=BIG_INTEGER && LA92_0<=BIG_DECIMAL)||(LA92_0>=LOGICAL_OR && LA92_0<=SHA1)||(LA92_0>=SHA256 && LA92_0<=GROUP_CONCAT)||LA92_0==DOUBLE||LA92_0==251||LA92_0==254||(LA92_0>=257 && LA92_0<=263)) ) {
                            alt92=1;
                        }


                        switch (alt92) {
                    	case 1 :
                    	    // IbmSparqlAstWalker.g:730:5: e4= expression
                    	    {
                    	    pushFollow(FOLLOW_expression_in_expression5037);
                    	    e4=expression();

                    	    state._fsp--;
                    	    if (state.failed) return e;
                    	    if ( state.backtracking==0 ) {
                    	       le.addComponent(e4); 	
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt92 >= 1 ) break loop92;
                    	    if (state.backtracking>0) {state.failed=true; return e;}
                                EarlyExitException eee =
                                    new EarlyExitException(92, input);
                                throw eee;
                        }
                        cnt92++;
                    } while (true);


                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = le; 
                    }

                    }
                    break;
                case 3 :
                    // IbmSparqlAstWalker.g:732:6: ^( '=' e5= expression e6= expression )
                    {
                    match(input,259,FOLLOW_259_in_expression5064); if (state.failed) return e;

                    if ( state.backtracking==0 ) {
                       rl = new RelationalExpression(); 								  
                    }

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression5078);
                    e5=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       rl.setLeft(e5); 				
                    }
                    pushFollow(FOLLOW_expression_in_expression5089);
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
                    // IbmSparqlAstWalker.g:736:4: ^( '!=' e7= expression e8= expression )
                    {
                    match(input,260,FOLLOW_260_in_expression5109); if (state.failed) return e;

                    if ( state.backtracking==0 ) {
                       rl = new RelationalExpression(); 								  
                    }

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression5123);
                    e7=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       rl.setLeft(e7); 				
                    }
                    pushFollow(FOLLOW_expression_in_expression5134);
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
                    // IbmSparqlAstWalker.g:740:4: ^( LT e9= expression e10= expression )
                    {
                    match(input,LT,FOLLOW_LT_in_expression5154); if (state.failed) return e;

                    if ( state.backtracking==0 ) {
                       rl = new RelationalExpression(); 								  
                    }

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression5169);
                    e9=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       rl.setLeft(e9); 				
                    }
                    pushFollow(FOLLOW_expression_in_expression5184);
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
                    // IbmSparqlAstWalker.g:744:4: ^( '>' e11= expression e12= expression )
                    {
                    match(input,261,FOLLOW_261_in_expression5204); if (state.failed) return e;

                    if ( state.backtracking==0 ) {
                       rl = new RelationalExpression(); 								  
                    }

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression5219);
                    e11=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       rl.setLeft(e11); 				
                    }
                    pushFollow(FOLLOW_expression_in_expression5230);
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
                    // IbmSparqlAstWalker.g:748:4: ^( LTE e13= expression e14= expression )
                    {
                    match(input,LTE,FOLLOW_LTE_in_expression5250); if (state.failed) return e;

                    if ( state.backtracking==0 ) {
                       rl = new RelationalExpression(); 								  
                    }

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression5264);
                    e13=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       rl.setLeft(e13); 				
                    }
                    pushFollow(FOLLOW_expression_in_expression5275);
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
                    // IbmSparqlAstWalker.g:752:4: ^( '>=' e15= expression e16= expression )
                    {
                    match(input,262,FOLLOW_262_in_expression5295); if (state.failed) return e;

                    if ( state.backtracking==0 ) {
                       rl = new RelationalExpression(); 								  
                    }

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression5309);
                    e15=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       rl.setLeft(e15); 				
                    }
                    pushFollow(FOLLOW_expression_in_expression5320);
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
                    // IbmSparqlAstWalker.g:756:4: ^( IN e17= expression e18= expressionList )
                    {
                    match(input,IN,FOLLOW_IN_in_expression5340); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression5367);
                    e17=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expressionList_in_expression5380);
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
                    // IbmSparqlAstWalker.g:761:4: ^( NOT e19= expression e20= expressionList )
                    {
                    match(input,NOT,FOLLOW_NOT_in_expression5426); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression5433);
                    e19=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expressionList_in_expression5441);
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
                    // IbmSparqlAstWalker.g:765:4: ^( '+' e21= expression (e22= expression )? )
                    {
                    match(input,257,FOLLOW_257_in_expression5475); if (state.failed) return e;

                    if ( state.backtracking==0 ) {
                       ne = new NumericExpression(); 								
                    }

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression5502);
                    e21=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       ne.setLHS(e21); 				
                    }
                    // IbmSparqlAstWalker.g:767:4: (e22= expression )?
                    int alt93=2;
                    int LA93_0 = input.LA(1);

                    if ( ((LA93_0>=BROKEN_PLUS && LA93_0<=BROKEN_MINUS)||(LA93_0>=VAR && LA93_0<=NOT_EXISTS)||(LA93_0>=STRING && LA93_0<=BOOLEAN)||LA93_0==LTE||(LA93_0>=BIG_INTEGER && LA93_0<=BIG_DECIMAL)||(LA93_0>=LOGICAL_OR && LA93_0<=SHA1)||(LA93_0>=SHA256 && LA93_0<=GROUP_CONCAT)||LA93_0==DOUBLE||LA93_0==251||LA93_0==254||(LA93_0>=257 && LA93_0<=263)) ) {
                        alt93=1;
                    }
                    switch (alt93) {
                        case 1 :
                            // IbmSparqlAstWalker.g:767:5: e22= expression
                            {
                            pushFollow(FOLLOW_expression_in_expression5514);
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
                    // IbmSparqlAstWalker.g:769:4: ^( BROKEN_PLUS e21= expression e22= expression )
                    {
                    match(input,BROKEN_PLUS,FOLLOW_BROKEN_PLUS_in_expression5537); if (state.failed) return e;

                    if ( state.backtracking==0 ) {
                       ne = new NumericExpression(); 								
                    }

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression5556);
                    e21=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       ne.setLHS(e21); 				
                    }
                    pushFollow(FOLLOW_expression_in_expression5567);
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
                    // IbmSparqlAstWalker.g:773:4: ^( '-' e23= expression (e24= expression )? )
                    {
                    match(input,263,FOLLOW_263_in_expression5587); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression5613);
                    e23=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new UnaryExpression(Expression.EUnaryOp.MINUS, e23); 
                    }
                    // IbmSparqlAstWalker.g:775:4: (e24= expression )?
                    int alt94=2;
                    int LA94_0 = input.LA(1);

                    if ( ((LA94_0>=BROKEN_PLUS && LA94_0<=BROKEN_MINUS)||(LA94_0>=VAR && LA94_0<=NOT_EXISTS)||(LA94_0>=STRING && LA94_0<=BOOLEAN)||LA94_0==LTE||(LA94_0>=BIG_INTEGER && LA94_0<=BIG_DECIMAL)||(LA94_0>=LOGICAL_OR && LA94_0<=SHA1)||(LA94_0>=SHA256 && LA94_0<=GROUP_CONCAT)||LA94_0==DOUBLE||LA94_0==251||LA94_0==254||(LA94_0>=257 && LA94_0<=263)) ) {
                        alt94=1;
                    }
                    switch (alt94) {
                        case 1 :
                            // IbmSparqlAstWalker.g:775:5: e24= expression
                            {
                            pushFollow(FOLLOW_expression_in_expression5625);
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
                    // IbmSparqlAstWalker.g:777:4: ^( BROKEN_MINUS e23= expression e24= expression )
                    {
                    match(input,BROKEN_MINUS,FOLLOW_BROKEN_MINUS_in_expression5642); if (state.failed) return e;

                    if ( state.backtracking==0 ) {
                       ne = new NumericExpression(); 								
                    }

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression5660);
                    e23=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       ne.setLHS(e23); 				
                    }
                    pushFollow(FOLLOW_expression_in_expression5671);
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
                    // IbmSparqlAstWalker.g:781:4: ^( '*' e25= expression e26= expression )
                    {
                    match(input,251,FOLLOW_251_in_expression5691); if (state.failed) return e;

                    if ( state.backtracking==0 ) {
                       ne = new NumericExpression(); 								
                    }

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression5718);
                    e25=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       ne.setLHS(e25); 				
                    }
                    pushFollow(FOLLOW_expression_in_expression5729);
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
                    // IbmSparqlAstWalker.g:785:4: ^( '/' e27= expression e28= expression )
                    {
                    match(input,254,FOLLOW_254_in_expression5749); if (state.failed) return e;

                    if ( state.backtracking==0 ) {
                       ne = new NumericExpression(); 								
                    }

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression5776);
                    e27=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       ne.setLHS(e27); 				
                    }
                    pushFollow(FOLLOW_expression_in_expression5786);
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
                    // IbmSparqlAstWalker.g:789:4: ^( '!' e29= expression )
                    {
                    match(input,258,FOLLOW_258_in_expression5806); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression5810);
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
                    // IbmSparqlAstWalker.g:790:4: e30= brackettedExpression
                    {
                    pushFollow(FOLLOW_brackettedExpression_in_expression5821);
                    e30=brackettedExpression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = e30; 
                    }

                    }
                    break;
                case 19 :
                    // IbmSparqlAstWalker.g:791:5: e31= primaryExpression
                    {
                    pushFollow(FOLLOW_primaryExpression_in_expression5831);
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
    // IbmSparqlAstWalker.g:795:1: primaryExpression returns [Expression e] : (e1= builtInCall | i= iRIref | f= iRIFunction | r= rDFLiteral | n= numericLiteral | b= booleanLiteral | v= var | a= aggregate );
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
            // IbmSparqlAstWalker.g:796:2: (e1= builtInCall | i= iRIref | f= iRIFunction | r= rDFLiteral | n= numericLiteral | b= booleanLiteral | v= var | a= aggregate )
            int alt96=8;
            alt96 = dfa96.predict(input);
            switch (alt96) {
                case 1 :
                    // IbmSparqlAstWalker.g:796:6: e1= builtInCall
                    {
                    pushFollow(FOLLOW_builtInCall_in_primaryExpression5856);
                    e1=builtInCall();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = e1; 
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlAstWalker.g:797:5: i= iRIref
                    {
                    pushFollow(FOLLOW_iRIref_in_primaryExpression5868);
                    i=iRIref();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new ConstantExpression(i); 
                    }

                    }
                    break;
                case 3 :
                    // IbmSparqlAstWalker.g:798:4: f= iRIFunction
                    {
                    pushFollow(FOLLOW_iRIFunction_in_primaryExpression5880);
                    f=iRIFunction();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new FunctionCallExpression(f); 
                    }

                    }
                    break;
                case 4 :
                    // IbmSparqlAstWalker.g:799:5: r= rDFLiteral
                    {
                    pushFollow(FOLLOW_rDFLiteral_in_primaryExpression5892);
                    r=rDFLiteral();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new ConstantExpression(r); 
                    }

                    }
                    break;
                case 5 :
                    // IbmSparqlAstWalker.g:800:5: n= numericLiteral
                    {
                    pushFollow(FOLLOW_numericLiteral_in_primaryExpression5904);
                    n=numericLiteral();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new ConstantExpression(n); 
                    }

                    }
                    break;
                case 6 :
                    // IbmSparqlAstWalker.g:801:5: b= booleanLiteral
                    {
                    pushFollow(FOLLOW_booleanLiteral_in_primaryExpression5915);
                    b=booleanLiteral();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new ConstantExpression(b); 
                    }

                    }
                    break;
                case 7 :
                    // IbmSparqlAstWalker.g:802:5: v= var
                    {
                    pushFollow(FOLLOW_var_in_primaryExpression5926);
                    v=var();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new VariableExpression(v); 
                    }

                    }
                    break;
                case 8 :
                    // IbmSparqlAstWalker.g:803:5: a= aggregate
                    {
                    pushFollow(FOLLOW_aggregate_in_primaryExpression5940);
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
    // IbmSparqlAstWalker.g:806:1: brackettedExpression returns [Expression e] : ^( EXPRESSION e1= expression ) ;
    public final Expression brackettedExpression() throws RecognitionException {
        Expression e = null;

        Expression e1 = null;


        try {
            // IbmSparqlAstWalker.g:807:2: ( ^( EXPRESSION e1= expression ) )
            // IbmSparqlAstWalker.g:807:4: ^( EXPRESSION e1= expression )
            {
            match(input,EXPRESSION,FOLLOW_EXPRESSION_in_brackettedExpression5963); if (state.failed) return e;

            match(input, Token.DOWN, null); if (state.failed) return e;
            pushFollow(FOLLOW_expression_in_brackettedExpression5968);
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
    // IbmSparqlAstWalker.g:810:1: builtInCall returns [Expression e] : ( ^( STR st= expression ) | ^( LANG lg= expression ) | ^( LANGMATCHES lm1= expression lm2= expression ) | ^( DATATYPE dt= expression ) | ^( BOUND v= var ) | ^( IRI e6= expression ) | ^( URI e7= expression ) | ^( BNODE e8= expression ) | BNODE | ^( RAND NIL ) | ^( ABS e9= expression ) | ^( CEIL e10= expression ) | ^( FLOOR e11= expression ) | ^( ROUND e12= expression ) | ^( CONCAT e13= expressionList ) | ^( SUBSTR e14= expression e15= expression (e16= expression )? ) | ^( STRLEN e15= expression ) | ^( UCASE e16= expression ) | ^( REPLACE e1= expression e2= expression e3= expression (e4= expression )? ) | ^( LCASE e17= expression ) | ^( ENCODE_FOR_URI e18= expression ) | ^( CONTAINS e19= expression e20= expression ) | ^( STRSTARTS e21= expression e22= expression ) | ^( STRENDS e23= expression e24= expression ) | ^( STRBEFORE e241= expression e242= expression ) | ^( STRAFTER e243= expression e244= expression ) | ^( YEAR e25= expression ) | ^( MONTH e26= expression ) | ^( DAY e27= expression ) | ^( HOURS e28= expression ) | ^( MINUTES e29= expression ) | ^( SECONDS e30= expression ) | ^( TIMEZONE e31= expression ) | ^( TZ e32= expression ) | NOW | UUID | STRUUID | ^( MD5 e33= expression ) | ^( SHA1 e34= expression ) | ^( SHA256 e36= expression ) | ^( SHA384 e37= expression ) | ^( SHA512 e38= expression ) | ^( COALESCE e39= expressionList ) | ^( IF e40= expression e41= expression e42= expression ) | ^( STRLANG e45= expression e46= expression ) | ^( STRDT e47= expression e48= expression ) | ^( SAMETERM sam1= expression sam2= expression ) | ^( ISIRI isi= expression ) | ^( ISURI isu= expression ) | ^( ISBLANK isb= expression ) | ^( ISLITERAL isl= expression ) | ^( ISNUMERIC e55= expression ) | r= regexExpression | p= existsFunc | p= notExistsFunc );
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
            // IbmSparqlAstWalker.g:814:2: ( ^( STR st= expression ) | ^( LANG lg= expression ) | ^( LANGMATCHES lm1= expression lm2= expression ) | ^( DATATYPE dt= expression ) | ^( BOUND v= var ) | ^( IRI e6= expression ) | ^( URI e7= expression ) | ^( BNODE e8= expression ) | BNODE | ^( RAND NIL ) | ^( ABS e9= expression ) | ^( CEIL e10= expression ) | ^( FLOOR e11= expression ) | ^( ROUND e12= expression ) | ^( CONCAT e13= expressionList ) | ^( SUBSTR e14= expression e15= expression (e16= expression )? ) | ^( STRLEN e15= expression ) | ^( UCASE e16= expression ) | ^( REPLACE e1= expression e2= expression e3= expression (e4= expression )? ) | ^( LCASE e17= expression ) | ^( ENCODE_FOR_URI e18= expression ) | ^( CONTAINS e19= expression e20= expression ) | ^( STRSTARTS e21= expression e22= expression ) | ^( STRENDS e23= expression e24= expression ) | ^( STRBEFORE e241= expression e242= expression ) | ^( STRAFTER e243= expression e244= expression ) | ^( YEAR e25= expression ) | ^( MONTH e26= expression ) | ^( DAY e27= expression ) | ^( HOURS e28= expression ) | ^( MINUTES e29= expression ) | ^( SECONDS e30= expression ) | ^( TIMEZONE e31= expression ) | ^( TZ e32= expression ) | NOW | UUID | STRUUID | ^( MD5 e33= expression ) | ^( SHA1 e34= expression ) | ^( SHA256 e36= expression ) | ^( SHA384 e37= expression ) | ^( SHA512 e38= expression ) | ^( COALESCE e39= expressionList ) | ^( IF e40= expression e41= expression e42= expression ) | ^( STRLANG e45= expression e46= expression ) | ^( STRDT e47= expression e48= expression ) | ^( SAMETERM sam1= expression sam2= expression ) | ^( ISIRI isi= expression ) | ^( ISURI isu= expression ) | ^( ISBLANK isb= expression ) | ^( ISLITERAL isl= expression ) | ^( ISNUMERIC e55= expression ) | r= regexExpression | p= existsFunc | p= notExistsFunc )
            int alt99=55;
            alt99 = dfa99.predict(input);
            switch (alt99) {
                case 1 :
                    // IbmSparqlAstWalker.g:814:5: ^( STR st= expression )
                    {
                    match(input,STR,FOLLOW_STR_in_builtInCall5998); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6002);
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
                    // IbmSparqlAstWalker.g:816:5: ^( LANG lg= expression )
                    {
                    match(input,LANG,FOLLOW_LANG_in_builtInCall6015); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6019);
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
                    // IbmSparqlAstWalker.g:818:5: ^( LANGMATCHES lm1= expression lm2= expression )
                    {
                    match(input,LANGMATCHES,FOLLOW_LANGMATCHES_in_builtInCall6032); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6036);
                    lm1=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6040);
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
                    // IbmSparqlAstWalker.g:820:5: ^( DATATYPE dt= expression )
                    {
                    match(input,DATATYPE,FOLLOW_DATATYPE_in_builtInCall6053); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6057);
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
                    // IbmSparqlAstWalker.g:822:4: ^( BOUND v= var )
                    {
                    match(input,BOUND,FOLLOW_BOUND_in_builtInCall6069); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_var_in_builtInCall6073);
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
                    // IbmSparqlAstWalker.g:824:4: ^( IRI e6= expression )
                    {
                    match(input,IRI,FOLLOW_IRI_in_builtInCall6091); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6095);
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
                    // IbmSparqlAstWalker.g:826:5: ^( URI e7= expression )
                    {
                    match(input,URI,FOLLOW_URI_in_builtInCall6108); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6112);
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
                    // IbmSparqlAstWalker.g:828:4: ^( BNODE e8= expression )
                    {
                    match(input,BNODE,FOLLOW_BNODE_in_builtInCall6124); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6128);
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
                    // IbmSparqlAstWalker.g:830:4: BNODE
                    {
                    match(input,BNODE,FOLLOW_BNODE_in_builtInCall6139); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new BuiltinFunctionExpression(Expression.EBuiltinType.BNODE); 							
                    }

                    }
                    break;
                case 10 :
                    // IbmSparqlAstWalker.g:832:4: ^( RAND NIL )
                    {
                    match(input,RAND,FOLLOW_RAND_in_builtInCall6150); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    match(input,NIL,FOLLOW_NIL_in_builtInCall6152); if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new BuiltinFunctionExpression(Expression.EBuiltinType.RAND); 							
                    }

                    }
                    break;
                case 11 :
                    // IbmSparqlAstWalker.g:834:4: ^( ABS e9= expression )
                    {
                    match(input,ABS,FOLLOW_ABS_in_builtInCall6165); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6169);
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
                    // IbmSparqlAstWalker.g:836:4: ^( CEIL e10= expression )
                    {
                    match(input,CEIL,FOLLOW_CEIL_in_builtInCall6181); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6185);
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
                    // IbmSparqlAstWalker.g:838:4: ^( FLOOR e11= expression )
                    {
                    match(input,FLOOR,FOLLOW_FLOOR_in_builtInCall6197); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6201);
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
                    // IbmSparqlAstWalker.g:840:4: ^( ROUND e12= expression )
                    {
                    match(input,ROUND,FOLLOW_ROUND_in_builtInCall6213); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6217);
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
                    // IbmSparqlAstWalker.g:842:4: ^( CONCAT e13= expressionList )
                    {
                    match(input,CONCAT,FOLLOW_CONCAT_in_builtInCall6229); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expressionList_in_builtInCall6233);
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
                    // IbmSparqlAstWalker.g:844:5: ^( SUBSTR e14= expression e15= expression (e16= expression )? )
                    {
                    match(input,SUBSTR,FOLLOW_SUBSTR_in_builtInCall6246); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6250);
                    e14=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6254);
                    e15=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       args.add(e14); args.add(e15); 
                    }
                    // IbmSparqlAstWalker.g:846:13: (e16= expression )?
                    int alt97=2;
                    int LA97_0 = input.LA(1);

                    if ( ((LA97_0>=BROKEN_PLUS && LA97_0<=BROKEN_MINUS)||(LA97_0>=VAR && LA97_0<=NOT_EXISTS)||(LA97_0>=STRING && LA97_0<=BOOLEAN)||LA97_0==LTE||(LA97_0>=BIG_INTEGER && LA97_0<=BIG_DECIMAL)||(LA97_0>=LOGICAL_OR && LA97_0<=SHA1)||(LA97_0>=SHA256 && LA97_0<=GROUP_CONCAT)||LA97_0==DOUBLE||LA97_0==251||LA97_0==254||(LA97_0>=257 && LA97_0<=263)) ) {
                        alt97=1;
                    }
                    switch (alt97) {
                        case 1 :
                            // IbmSparqlAstWalker.g:846:15: e16= expression
                            {
                            pushFollow(FOLLOW_expression_in_builtInCall6278);
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
                    // IbmSparqlAstWalker.g:850:4: ^( STRLEN e15= expression )
                    {
                    match(input,STRLEN,FOLLOW_STRLEN_in_builtInCall6301); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6305);
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
                    // IbmSparqlAstWalker.g:852:4: ^( UCASE e16= expression )
                    {
                    match(input,UCASE,FOLLOW_UCASE_in_builtInCall6317); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6321);
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
                    // IbmSparqlAstWalker.g:854:4: ^( REPLACE e1= expression e2= expression e3= expression (e4= expression )? )
                    {
                    match(input,REPLACE,FOLLOW_REPLACE_in_builtInCall6333); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6337);
                    e1=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6341);
                    e2=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6345);
                    e3=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       args.add(e1); args.add(e2); args.add(e3); 
                    }
                    // IbmSparqlAstWalker.g:856:13: (e4= expression )?
                    int alt98=2;
                    int LA98_0 = input.LA(1);

                    if ( ((LA98_0>=BROKEN_PLUS && LA98_0<=BROKEN_MINUS)||(LA98_0>=VAR && LA98_0<=NOT_EXISTS)||(LA98_0>=STRING && LA98_0<=BOOLEAN)||LA98_0==LTE||(LA98_0>=BIG_INTEGER && LA98_0<=BIG_DECIMAL)||(LA98_0>=LOGICAL_OR && LA98_0<=SHA1)||(LA98_0>=SHA256 && LA98_0<=GROUP_CONCAT)||LA98_0==DOUBLE||LA98_0==251||LA98_0==254||(LA98_0>=257 && LA98_0<=263)) ) {
                        alt98=1;
                    }
                    switch (alt98) {
                        case 1 :
                            // IbmSparqlAstWalker.g:856:15: e4= expression
                            {
                            pushFollow(FOLLOW_expression_in_builtInCall6369);
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
                    // IbmSparqlAstWalker.g:858:4: ^( LCASE e17= expression )
                    {
                    match(input,LCASE,FOLLOW_LCASE_in_builtInCall6387); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6391);
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
                    // IbmSparqlAstWalker.g:860:4: ^( ENCODE_FOR_URI e18= expression )
                    {
                    match(input,ENCODE_FOR_URI,FOLLOW_ENCODE_FOR_URI_in_builtInCall6403); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6407);
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
                    // IbmSparqlAstWalker.g:862:4: ^( CONTAINS e19= expression e20= expression )
                    {
                    match(input,CONTAINS,FOLLOW_CONTAINS_in_builtInCall6419); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6423);
                    e19=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6427);
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
                    // IbmSparqlAstWalker.g:864:4: ^( STRSTARTS e21= expression e22= expression )
                    {
                    match(input,STRSTARTS,FOLLOW_STRSTARTS_in_builtInCall6439); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6443);
                    e21=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6447);
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
                    // IbmSparqlAstWalker.g:866:4: ^( STRENDS e23= expression e24= expression )
                    {
                    match(input,STRENDS,FOLLOW_STRENDS_in_builtInCall6459); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6463);
                    e23=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6467);
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
                    // IbmSparqlAstWalker.g:868:4: ^( STRBEFORE e241= expression e242= expression )
                    {
                    match(input,STRBEFORE,FOLLOW_STRBEFORE_in_builtInCall6479); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6483);
                    e241=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6487);
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
                    // IbmSparqlAstWalker.g:870:4: ^( STRAFTER e243= expression e244= expression )
                    {
                    match(input,STRAFTER,FOLLOW_STRAFTER_in_builtInCall6499); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6503);
                    e243=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6507);
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
                    // IbmSparqlAstWalker.g:872:4: ^( YEAR e25= expression )
                    {
                    match(input,YEAR,FOLLOW_YEAR_in_builtInCall6519); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6523);
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
                    // IbmSparqlAstWalker.g:874:4: ^( MONTH e26= expression )
                    {
                    match(input,MONTH,FOLLOW_MONTH_in_builtInCall6535); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6539);
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
                    // IbmSparqlAstWalker.g:876:4: ^( DAY e27= expression )
                    {
                    match(input,DAY,FOLLOW_DAY_in_builtInCall6551); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6555);
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
                    // IbmSparqlAstWalker.g:878:4: ^( HOURS e28= expression )
                    {
                    match(input,HOURS,FOLLOW_HOURS_in_builtInCall6567); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6571);
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
                    // IbmSparqlAstWalker.g:880:4: ^( MINUTES e29= expression )
                    {
                    match(input,MINUTES,FOLLOW_MINUTES_in_builtInCall6583); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6587);
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
                    // IbmSparqlAstWalker.g:882:4: ^( SECONDS e30= expression )
                    {
                    match(input,SECONDS,FOLLOW_SECONDS_in_builtInCall6599); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6603);
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
                    // IbmSparqlAstWalker.g:884:4: ^( TIMEZONE e31= expression )
                    {
                    match(input,TIMEZONE,FOLLOW_TIMEZONE_in_builtInCall6615); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6619);
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
                    // IbmSparqlAstWalker.g:886:4: ^( TZ e32= expression )
                    {
                    match(input,TZ,FOLLOW_TZ_in_builtInCall6631); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6635);
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
                    // IbmSparqlAstWalker.g:888:4: NOW
                    {
                    match(input,NOW,FOLLOW_NOW_in_builtInCall6646); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new BuiltinFunctionExpression(Expression.EBuiltinType.NOW); 
                    }

                    }
                    break;
                case 36 :
                    // IbmSparqlAstWalker.g:889:9: UUID
                    {
                    match(input,UUID,FOLLOW_UUID_in_builtInCall6658); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new BuiltinFunctionExpression(Expression.EBuiltinType.UUID); 
                    }

                    }
                    break;
                case 37 :
                    // IbmSparqlAstWalker.g:890:9: STRUUID
                    {
                    match(input,STRUUID,FOLLOW_STRUUID_in_builtInCall6670); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new BuiltinFunctionExpression(Expression.EBuiltinType.STRUUID); 
                    }

                    }
                    break;
                case 38 :
                    // IbmSparqlAstWalker.g:891:4: ^( MD5 e33= expression )
                    {
                    match(input,MD5,FOLLOW_MD5_in_builtInCall6679); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6683);
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
                    // IbmSparqlAstWalker.g:893:4: ^( SHA1 e34= expression )
                    {
                    match(input,SHA1,FOLLOW_SHA1_in_builtInCall6695); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6699);
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
                    // IbmSparqlAstWalker.g:895:4: ^( SHA256 e36= expression )
                    {
                    match(input,SHA256,FOLLOW_SHA256_in_builtInCall6711); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6715);
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
                    // IbmSparqlAstWalker.g:897:4: ^( SHA384 e37= expression )
                    {
                    match(input,SHA384,FOLLOW_SHA384_in_builtInCall6727); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6731);
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
                    // IbmSparqlAstWalker.g:899:4: ^( SHA512 e38= expression )
                    {
                    match(input,SHA512,FOLLOW_SHA512_in_builtInCall6743); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6747);
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
                    // IbmSparqlAstWalker.g:901:4: ^( COALESCE e39= expressionList )
                    {
                    match(input,COALESCE,FOLLOW_COALESCE_in_builtInCall6759); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expressionList_in_builtInCall6763);
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
                    // IbmSparqlAstWalker.g:903:4: ^( IF e40= expression e41= expression e42= expression )
                    {
                    match(input,IF,FOLLOW_IF_in_builtInCall6775); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6779);
                    e40=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6783);
                    e41=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6787);
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
                    // IbmSparqlAstWalker.g:905:4: ^( STRLANG e45= expression e46= expression )
                    {
                    match(input,STRLANG,FOLLOW_STRLANG_in_builtInCall6799); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6803);
                    e45=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6807);
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
                    // IbmSparqlAstWalker.g:907:4: ^( STRDT e47= expression e48= expression )
                    {
                    match(input,STRDT,FOLLOW_STRDT_in_builtInCall6819); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6823);
                    e47=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6827);
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
                    // IbmSparqlAstWalker.g:909:4: ^( SAMETERM sam1= expression sam2= expression )
                    {
                    match(input,SAMETERM,FOLLOW_SAMETERM_in_builtInCall6839); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6843);
                    sam1=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6847);
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
                    // IbmSparqlAstWalker.g:911:4: ^( ISIRI isi= expression )
                    {
                    match(input,ISIRI,FOLLOW_ISIRI_in_builtInCall6859); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6863);
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
                    // IbmSparqlAstWalker.g:913:4: ^( ISURI isu= expression )
                    {
                    match(input,ISURI,FOLLOW_ISURI_in_builtInCall6875); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6879);
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
                    // IbmSparqlAstWalker.g:915:4: ^( ISBLANK isb= expression )
                    {
                    match(input,ISBLANK,FOLLOW_ISBLANK_in_builtInCall6891); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6895);
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
                    // IbmSparqlAstWalker.g:917:4: ^( ISLITERAL isl= expression )
                    {
                    match(input,ISLITERAL,FOLLOW_ISLITERAL_in_builtInCall6907); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6911);
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
                    // IbmSparqlAstWalker.g:919:4: ^( ISNUMERIC e55= expression )
                    {
                    match(input,ISNUMERIC,FOLLOW_ISNUMERIC_in_builtInCall6923); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6927);
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
                    // IbmSparqlAstWalker.g:921:4: r= regexExpression
                    {
                    pushFollow(FOLLOW_regexExpression_in_builtInCall6940);
                    r=regexExpression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = r; 
                    }

                    }
                    break;
                case 54 :
                    // IbmSparqlAstWalker.g:922:4: p= existsFunc
                    {
                    pushFollow(FOLLOW_existsFunc_in_builtInCall6949);
                    p=existsFunc();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new BuiltinFunctionExpression(Expression.EBuiltinType.EXISTS, p); 	
                    }

                    }
                    break;
                case 55 :
                    // IbmSparqlAstWalker.g:923:4: p= notExistsFunc
                    {
                    pushFollow(FOLLOW_notExistsFunc_in_builtInCall6958);
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
    // IbmSparqlAstWalker.g:926:1: regexExpression returns [Expression e] : ^( REGEX e1= expression e2= expression (e3= expression )? ) ;
    public final Expression regexExpression() throws RecognitionException {
        Expression e = null;

        Expression e1 = null;

        Expression e2 = null;

        Expression e3 = null;


        try {
            // IbmSparqlAstWalker.g:927:2: ( ^( REGEX e1= expression e2= expression (e3= expression )? ) )
            // IbmSparqlAstWalker.g:927:6: ^( REGEX e1= expression e2= expression (e3= expression )? )
            {
            match(input,REGEX,FOLLOW_REGEX_in_regexExpression6981); if (state.failed) return e;

            match(input, Token.DOWN, null); if (state.failed) return e;
            pushFollow(FOLLOW_expression_in_regexExpression6985);
            e1=expression();

            state._fsp--;
            if (state.failed) return e;
            pushFollow(FOLLOW_expression_in_regexExpression6989);
            e2=expression();

            state._fsp--;
            if (state.failed) return e;
            // IbmSparqlAstWalker.g:927:44: (e3= expression )?
            int alt100=2;
            int LA100_0 = input.LA(1);

            if ( ((LA100_0>=BROKEN_PLUS && LA100_0<=BROKEN_MINUS)||(LA100_0>=VAR && LA100_0<=NOT_EXISTS)||(LA100_0>=STRING && LA100_0<=BOOLEAN)||LA100_0==LTE||(LA100_0>=BIG_INTEGER && LA100_0<=BIG_DECIMAL)||(LA100_0>=LOGICAL_OR && LA100_0<=SHA1)||(LA100_0>=SHA256 && LA100_0<=GROUP_CONCAT)||LA100_0==DOUBLE||LA100_0==251||LA100_0==254||(LA100_0>=257 && LA100_0<=263)) ) {
                alt100=1;
            }
            switch (alt100) {
                case 1 :
                    // IbmSparqlAstWalker.g:927:44: e3= expression
                    {
                    pushFollow(FOLLOW_expression_in_regexExpression6993);
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
    // IbmSparqlAstWalker.g:931:1: existsFunc returns [Pattern p] : ^( EXISTS g= groupGraphPattern[false] ) ;
    public final Pattern existsFunc() throws RecognitionException {
        Pattern p = null;

        Pattern g = null;


        try {
            // IbmSparqlAstWalker.g:932:2: ( ^( EXISTS g= groupGraphPattern[false] ) )
            // IbmSparqlAstWalker.g:932:6: ^( EXISTS g= groupGraphPattern[false] )
            {
            match(input,EXISTS,FOLLOW_EXISTS_in_existsFunc7025); if (state.failed) return p;

            match(input, Token.DOWN, null); if (state.failed) return p;
            pushFollow(FOLLOW_groupGraphPattern_in_existsFunc7029);
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
    // IbmSparqlAstWalker.g:936:1: notExistsFunc returns [Pattern p] : ^( NOT_EXISTS g= groupGraphPattern[false] ) ;
    public final Pattern notExistsFunc() throws RecognitionException {
        Pattern p = null;

        Pattern g = null;


        try {
            // IbmSparqlAstWalker.g:937:2: ( ^( NOT_EXISTS g= groupGraphPattern[false] ) )
            // IbmSparqlAstWalker.g:937:6: ^( NOT_EXISTS g= groupGraphPattern[false] )
            {
            match(input,NOT_EXISTS,FOLLOW_NOT_EXISTS_in_notExistsFunc7060); if (state.failed) return p;

            match(input, Token.DOWN, null); if (state.failed) return p;
            pushFollow(FOLLOW_groupGraphPattern_in_notExistsFunc7064);
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
    // IbmSparqlAstWalker.g:941:1: aggregate returns [AggregateExpression a] : ( ^( COUNT ( DISTINCT )? (e1= expression | '*' ) ) | ^( SUM ( DISTINCT )? e2= expression ) | ^( MIN ( DISTINCT )? e3= expression ) | ^( MAX ( DISTINCT )? e4= expression ) | ^( AVG ( DISTINCT )? e5= expression ) | ^( SAMPLE ( DISTINCT )? e6= expression ) | ^( GROUP_CONCAT ( DISTINCT )? e7= expression ( ^( SEPARATOR s= string ) )? ) );
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
            // IbmSparqlAstWalker.g:945:2: ( ^( COUNT ( DISTINCT )? (e1= expression | '*' ) ) | ^( SUM ( DISTINCT )? e2= expression ) | ^( MIN ( DISTINCT )? e3= expression ) | ^( MAX ( DISTINCT )? e4= expression ) | ^( AVG ( DISTINCT )? e5= expression ) | ^( SAMPLE ( DISTINCT )? e6= expression ) | ^( GROUP_CONCAT ( DISTINCT )? e7= expression ( ^( SEPARATOR s= string ) )? ) )
            int alt110=7;
            switch ( input.LA(1) ) {
            case COUNT:
                {
                alt110=1;
                }
                break;
            case SUM:
                {
                alt110=2;
                }
                break;
            case MIN:
                {
                alt110=3;
                }
                break;
            case MAX:
                {
                alt110=4;
                }
                break;
            case AVG:
                {
                alt110=5;
                }
                break;
            case SAMPLE:
                {
                alt110=6;
                }
                break;
            case GROUP_CONCAT:
                {
                alt110=7;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return a;}
                NoViableAltException nvae =
                    new NoViableAltException("", 110, 0, input);

                throw nvae;
            }

            switch (alt110) {
                case 1 :
                    // IbmSparqlAstWalker.g:946:3: ^( COUNT ( DISTINCT )? (e1= expression | '*' ) )
                    {
                    match(input,COUNT,FOLLOW_COUNT_in_aggregate7099); if (state.failed) return a;

                    if ( state.backtracking==0 ) {
                       a.setAggregationType(AggregateExpression.EType.COUNT);  	
                    }

                    match(input, Token.DOWN, null); if (state.failed) return a;
                    // IbmSparqlAstWalker.g:947:4: ( DISTINCT )?
                    int alt101=2;
                    int LA101_0 = input.LA(1);

                    if ( (LA101_0==DISTINCT) ) {
                        alt101=1;
                    }
                    switch (alt101) {
                        case 1 :
                            // IbmSparqlAstWalker.g:947:5: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregate7111); if (state.failed) return a;
                            if ( state.backtracking==0 ) {
                               a.isDistinct(true);	
                            }

                            }
                            break;

                    }

                    // IbmSparqlAstWalker.g:948:4: (e1= expression | '*' )
                    int alt102=2;
                    int LA102_0 = input.LA(1);

                    if ( ((LA102_0>=BROKEN_PLUS && LA102_0<=BROKEN_MINUS)||(LA102_0>=VAR && LA102_0<=NOT_EXISTS)||(LA102_0>=STRING && LA102_0<=BOOLEAN)||LA102_0==LTE||(LA102_0>=BIG_INTEGER && LA102_0<=BIG_DECIMAL)||(LA102_0>=LOGICAL_OR && LA102_0<=SHA1)||(LA102_0>=SHA256 && LA102_0<=GROUP_CONCAT)||LA102_0==DOUBLE||LA102_0==254||(LA102_0>=257 && LA102_0<=263)) ) {
                        alt102=1;
                    }
                    else if ( (LA102_0==251) ) {
                        int LA102_2 = input.LA(2);

                        if ( (LA102_2==DOWN) ) {
                            alt102=1;
                        }
                        else if ( (LA102_2==UP) ) {
                            alt102=2;
                        }
                        else {
                            if (state.backtracking>0) {state.failed=true; return a;}
                            NoViableAltException nvae =
                                new NoViableAltException("", 102, 2, input);

                            throw nvae;
                        }
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return a;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 102, 0, input);

                        throw nvae;
                    }
                    switch (alt102) {
                        case 1 :
                            // IbmSparqlAstWalker.g:948:6: e1= expression
                            {
                            pushFollow(FOLLOW_expression_in_aggregate7168);
                            e1=expression();

                            state._fsp--;
                            if (state.failed) return a;
                            if ( state.backtracking==0 ) {
                               a.setArgs(e1);		
                            }

                            }
                            break;
                        case 2 :
                            // IbmSparqlAstWalker.g:949:6: '*'
                            {
                            match(input,251,FOLLOW_251_in_aggregate7178); if (state.failed) return a;

                            }
                            break;

                    }


                    match(input, Token.UP, null); if (state.failed) return a;

                    }
                    break;
                case 2 :
                    // IbmSparqlAstWalker.g:952:5: ^( SUM ( DISTINCT )? e2= expression )
                    {
                    match(input,SUM,FOLLOW_SUM_in_aggregate7198); if (state.failed) return a;

                    if ( state.backtracking==0 ) {
                       a.setAggregationType(AggregateExpression.EType.SUM);  	
                    }

                    match(input, Token.DOWN, null); if (state.failed) return a;
                    // IbmSparqlAstWalker.g:953:4: ( DISTINCT )?
                    int alt103=2;
                    int LA103_0 = input.LA(1);

                    if ( (LA103_0==DISTINCT) ) {
                        alt103=1;
                    }
                    switch (alt103) {
                        case 1 :
                            // IbmSparqlAstWalker.g:953:5: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregate7210); if (state.failed) return a;
                            if ( state.backtracking==0 ) {
                               a.isDistinct(true);	
                            }

                            }
                            break;

                    }

                    pushFollow(FOLLOW_expression_in_aggregate7227);
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
                    // IbmSparqlAstWalker.g:956:4: ^( MIN ( DISTINCT )? e3= expression )
                    {
                    match(input,MIN,FOLLOW_MIN_in_aggregate7240); if (state.failed) return a;

                    if ( state.backtracking==0 ) {
                       a.setAggregationType(AggregateExpression.EType.MIN);  	
                    }

                    match(input, Token.DOWN, null); if (state.failed) return a;
                    // IbmSparqlAstWalker.g:957:4: ( DISTINCT )?
                    int alt104=2;
                    int LA104_0 = input.LA(1);

                    if ( (LA104_0==DISTINCT) ) {
                        alt104=1;
                    }
                    switch (alt104) {
                        case 1 :
                            // IbmSparqlAstWalker.g:957:5: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregate7253); if (state.failed) return a;
                            if ( state.backtracking==0 ) {
                               a.isDistinct(true);	
                            }

                            }
                            break;

                    }

                    pushFollow(FOLLOW_expression_in_aggregate7269);
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
                    // IbmSparqlAstWalker.g:960:4: ^( MAX ( DISTINCT )? e4= expression )
                    {
                    match(input,MAX,FOLLOW_MAX_in_aggregate7282); if (state.failed) return a;

                    if ( state.backtracking==0 ) {
                       a.setAggregationType(AggregateExpression.EType.MAX);  	
                    }

                    match(input, Token.DOWN, null); if (state.failed) return a;
                    // IbmSparqlAstWalker.g:961:4: ( DISTINCT )?
                    int alt105=2;
                    int LA105_0 = input.LA(1);

                    if ( (LA105_0==DISTINCT) ) {
                        alt105=1;
                    }
                    switch (alt105) {
                        case 1 :
                            // IbmSparqlAstWalker.g:961:5: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregate7295); if (state.failed) return a;
                            if ( state.backtracking==0 ) {
                               a.isDistinct(true);	
                            }

                            }
                            break;

                    }

                    pushFollow(FOLLOW_expression_in_aggregate7312);
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
                    // IbmSparqlAstWalker.g:964:4: ^( AVG ( DISTINCT )? e5= expression )
                    {
                    match(input,AVG,FOLLOW_AVG_in_aggregate7325); if (state.failed) return a;

                    if ( state.backtracking==0 ) {
                       a.setAggregationType(AggregateExpression.EType.AVG);  	
                    }

                    match(input, Token.DOWN, null); if (state.failed) return a;
                    // IbmSparqlAstWalker.g:965:4: ( DISTINCT )?
                    int alt106=2;
                    int LA106_0 = input.LA(1);

                    if ( (LA106_0==DISTINCT) ) {
                        alt106=1;
                    }
                    switch (alt106) {
                        case 1 :
                            // IbmSparqlAstWalker.g:965:5: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregate7338); if (state.failed) return a;
                            if ( state.backtracking==0 ) {
                               a.isDistinct(true);	
                            }

                            }
                            break;

                    }

                    pushFollow(FOLLOW_expression_in_aggregate7355);
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
                    // IbmSparqlAstWalker.g:968:4: ^( SAMPLE ( DISTINCT )? e6= expression )
                    {
                    match(input,SAMPLE,FOLLOW_SAMPLE_in_aggregate7368); if (state.failed) return a;

                    if ( state.backtracking==0 ) {
                       a.setAggregationType(AggregateExpression.EType.SAMPLE);  
                    }

                    match(input, Token.DOWN, null); if (state.failed) return a;
                    // IbmSparqlAstWalker.g:969:4: ( DISTINCT )?
                    int alt107=2;
                    int LA107_0 = input.LA(1);

                    if ( (LA107_0==DISTINCT) ) {
                        alt107=1;
                    }
                    switch (alt107) {
                        case 1 :
                            // IbmSparqlAstWalker.g:969:5: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregate7380); if (state.failed) return a;
                            if ( state.backtracking==0 ) {
                               a.isDistinct(true);	
                            }

                            }
                            break;

                    }

                    pushFollow(FOLLOW_expression_in_aggregate7397);
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
                    // IbmSparqlAstWalker.g:972:5: ^( GROUP_CONCAT ( DISTINCT )? e7= expression ( ^( SEPARATOR s= string ) )? )
                    {
                    match(input,GROUP_CONCAT,FOLLOW_GROUP_CONCAT_in_aggregate7411); if (state.failed) return a;

                    if ( state.backtracking==0 ) {
                       a.setAggregationType(AggregateExpression.EType.GROUP_CONCAT); 
                    }

                    match(input, Token.DOWN, null); if (state.failed) return a;
                    // IbmSparqlAstWalker.g:973:4: ( DISTINCT )?
                    int alt108=2;
                    int LA108_0 = input.LA(1);

                    if ( (LA108_0==DISTINCT) ) {
                        alt108=1;
                    }
                    switch (alt108) {
                        case 1 :
                            // IbmSparqlAstWalker.g:973:5: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregate7423); if (state.failed) return a;
                            if ( state.backtracking==0 ) {
                               a.isDistinct(true);	
                            }

                            }
                            break;

                    }

                    pushFollow(FOLLOW_expression_in_aggregate7438);
                    e7=expression();

                    state._fsp--;
                    if (state.failed) return a;
                    if ( state.backtracking==0 ) {
                       a.setArgs(e7);		
                    }
                    // IbmSparqlAstWalker.g:975:4: ( ^( SEPARATOR s= string ) )?
                    int alt109=2;
                    int LA109_0 = input.LA(1);

                    if ( (LA109_0==SEPARATOR) ) {
                        alt109=1;
                    }
                    switch (alt109) {
                        case 1 :
                            // IbmSparqlAstWalker.g:975:5: ^( SEPARATOR s= string )
                            {
                            match(input,SEPARATOR,FOLLOW_SEPARATOR_in_aggregate7449); if (state.failed) return a;

                            match(input, Token.DOWN, null); if (state.failed) return a;
                            pushFollow(FOLLOW_string_in_aggregate7453);
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
    // IbmSparqlAstWalker.g:979:1: iRIFunction returns [FunctionCall f] : ^( FUNCTION i= iRIref (a= argList )? ) ;
    public final FunctionCall iRIFunction() throws RecognitionException {
        FunctionCall f = null;

        IRI i = null;

        List<Expression> a = null;


        try {
            // IbmSparqlAstWalker.g:980:2: ( ^( FUNCTION i= iRIref (a= argList )? ) )
            // IbmSparqlAstWalker.g:980:6: ^( FUNCTION i= iRIref (a= argList )? )
            {
            match(input,FUNCTION,FOLLOW_FUNCTION_in_iRIFunction7482); if (state.failed) return f;

            match(input, Token.DOWN, null); if (state.failed) return f;
            pushFollow(FOLLOW_iRIref_in_iRIFunction7492);
            i=iRIref();

            state._fsp--;
            if (state.failed) return f;
            if ( state.backtracking==0 ) {
               f = new FunctionCall(i); 
            }
            // IbmSparqlAstWalker.g:982:5: (a= argList )?
            int alt111=2;
            int LA111_0 = input.LA(1);

            if ( ((LA111_0>=BROKEN_PLUS && LA111_0<=NIL)||(LA111_0>=VAR && LA111_0<=NOT_EXISTS)||(LA111_0>=STRING && LA111_0<=BOOLEAN)||LA111_0==LTE||(LA111_0>=BIG_INTEGER && LA111_0<=BIG_DECIMAL)||LA111_0==DISTINCT||(LA111_0>=LOGICAL_OR && LA111_0<=SHA1)||(LA111_0>=SHA256 && LA111_0<=GROUP_CONCAT)||LA111_0==DOUBLE||LA111_0==251||LA111_0==254||(LA111_0>=257 && LA111_0<=263)) ) {
                alt111=1;
            }
            switch (alt111) {
                case 1 :
                    // IbmSparqlAstWalker.g:982:6: a= argList
                    {
                    pushFollow(FOLLOW_argList_in_iRIFunction7504);
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
    // IbmSparqlAstWalker.g:986:1: rDFLiteral returns [StringLiteral sl] : s= string (l= LANGTAG | ( '^^' i= iRIref ) )? ;
    public final StringLiteral rDFLiteral() throws RecognitionException {
        StringLiteral sl = null;

        XTree l=null;
        String s = null;

        IRI i = null;


        try {
            // IbmSparqlAstWalker.g:987:2: (s= string (l= LANGTAG | ( '^^' i= iRIref ) )? )
            // IbmSparqlAstWalker.g:987:6: s= string (l= LANGTAG | ( '^^' i= iRIref ) )?
            {
            pushFollow(FOLLOW_string_in_rDFLiteral7536);
            s=string();

            state._fsp--;
            if (state.failed) return sl;
            if ( state.backtracking==0 ) {
               sl = new StringLiteral(s);     
            }
            // IbmSparqlAstWalker.g:988:3: (l= LANGTAG | ( '^^' i= iRIref ) )?
            int alt112=3;
            int LA112_0 = input.LA(1);

            if ( (LA112_0==LANGTAG) ) {
                alt112=1;
            }
            else if ( (LA112_0==264) ) {
                alt112=2;
            }
            switch (alt112) {
                case 1 :
                    // IbmSparqlAstWalker.g:988:5: l= LANGTAG
                    {
                    l=(XTree)match(input,LANGTAG,FOLLOW_LANGTAG_in_rDFLiteral7551); if (state.failed) return sl;
                    if ( state.backtracking==0 ) {
                       sl.setLanguage((l!=null?l.getText():null));        
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlAstWalker.g:989:5: ( '^^' i= iRIref )
                    {
                    // IbmSparqlAstWalker.g:989:5: ( '^^' i= iRIref )
                    // IbmSparqlAstWalker.g:989:7: '^^' i= iRIref
                    {
                    match(input,264,FOLLOW_264_in_rDFLiteral7565); if (state.failed) return sl;
                    pushFollow(FOLLOW_iRIref_in_rDFLiteral7569);
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
    // IbmSparqlAstWalker.g:993:1: numericLiteral returns [Constant n] : (n1= numericLiteralUnsigned | n2= numericLiteralPositive | n3= numericLiteralNegative );
    public final Constant numericLiteral() throws RecognitionException {
        Constant n = null;

        Constant n1 = null;

        Constant n2 = null;

        Constant n3 = null;


        try {
            // IbmSparqlAstWalker.g:994:2: (n1= numericLiteralUnsigned | n2= numericLiteralPositive | n3= numericLiteralNegative )
            int alt113=3;
            alt113 = dfa113.predict(input);
            switch (alt113) {
                case 1 :
                    // IbmSparqlAstWalker.g:994:6: n1= numericLiteralUnsigned
                    {
                    pushFollow(FOLLOW_numericLiteralUnsigned_in_numericLiteral7600);
                    n1=numericLiteralUnsigned();

                    state._fsp--;
                    if (state.failed) return n;
                    if ( state.backtracking==0 ) {
                       n = n1; 
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlAstWalker.g:995:5: n2= numericLiteralPositive
                    {
                    pushFollow(FOLLOW_numericLiteralPositive_in_numericLiteral7610);
                    n2=numericLiteralPositive();

                    state._fsp--;
                    if (state.failed) return n;
                    if ( state.backtracking==0 ) {
                       n = n2; 
                    }

                    }
                    break;
                case 3 :
                    // IbmSparqlAstWalker.g:996:5: n3= numericLiteralNegative
                    {
                    pushFollow(FOLLOW_numericLiteralNegative_in_numericLiteral7620);
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
    // IbmSparqlAstWalker.g:999:1: numericLiteralUnsigned returns [Constant c] : ( ^( BIG_INTEGER i= INTEGER ) | ^( BIG_DECIMAL d1= DECIMAL ) | ^( DOUBLE d2= DOUBLE ) );
    public final Constant numericLiteralUnsigned() throws RecognitionException {
        Constant c = null;

        XTree i=null;
        XTree d1=null;
        XTree d2=null;

        try {
            // IbmSparqlAstWalker.g:1000:2: ( ^( BIG_INTEGER i= INTEGER ) | ^( BIG_DECIMAL d1= DECIMAL ) | ^( DOUBLE d2= DOUBLE ) )
            int alt114=3;
            switch ( input.LA(1) ) {
            case BIG_INTEGER:
                {
                alt114=1;
                }
                break;
            case BIG_DECIMAL:
                {
                alt114=2;
                }
                break;
            case DOUBLE:
                {
                alt114=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return c;}
                NoViableAltException nvae =
                    new NoViableAltException("", 114, 0, input);

                throw nvae;
            }

            switch (alt114) {
                case 1 :
                    // IbmSparqlAstWalker.g:1000:6: ^( BIG_INTEGER i= INTEGER )
                    {
                    match(input,BIG_INTEGER,FOLLOW_BIG_INTEGER_in_numericLiteralUnsigned7642); if (state.failed) return c;

                    match(input, Token.DOWN, null); if (state.failed) return c;
                    i=(XTree)match(input,INTEGER,FOLLOW_INTEGER_in_numericLiteralUnsigned7647); if (state.failed) return c;

                    match(input, Token.UP, null); if (state.failed) return c;
                    if ( state.backtracking==0 ) {
                       c = new Constant((i!=null?i.getText():null), new BigInteger((i!=null?i.getText():null)));		
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlAstWalker.g:1001:5: ^( BIG_DECIMAL d1= DECIMAL )
                    {
                    match(input,BIG_DECIMAL,FOLLOW_BIG_DECIMAL_in_numericLiteralUnsigned7663); if (state.failed) return c;

                    match(input, Token.DOWN, null); if (state.failed) return c;
                    d1=(XTree)match(input,DECIMAL,FOLLOW_DECIMAL_in_numericLiteralUnsigned7667); if (state.failed) return c;

                    match(input, Token.UP, null); if (state.failed) return c;
                    if ( state.backtracking==0 ) {
                       c = new Constant((d1!=null?d1.getText():null), new BigDecimal((d1!=null?d1.getText():null)));	
                    }

                    }
                    break;
                case 3 :
                    // IbmSparqlAstWalker.g:1002:5: ^( DOUBLE d2= DOUBLE )
                    {
                    match(input,DOUBLE,FOLLOW_DOUBLE_in_numericLiteralUnsigned7682); if (state.failed) return c;

                    match(input, Token.DOWN, null); if (state.failed) return c;
                    d2=(XTree)match(input,DOUBLE,FOLLOW_DOUBLE_in_numericLiteralUnsigned7687); if (state.failed) return c;

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
    // IbmSparqlAstWalker.g:1005:1: numericLiteralPositive returns [Constant c] : ( ^( BIG_INTEGER i= INTEGER_POSITIVE ) | ^( BIG_DECIMAL d1= DECIMAL_POSITIVE ) | ^( DOUBLE d2= DOUBLE_POSITIVE ) );
    public final Constant numericLiteralPositive() throws RecognitionException {
        Constant c = null;

        XTree i=null;
        XTree d1=null;
        XTree d2=null;

        try {
            // IbmSparqlAstWalker.g:1006:2: ( ^( BIG_INTEGER i= INTEGER_POSITIVE ) | ^( BIG_DECIMAL d1= DECIMAL_POSITIVE ) | ^( DOUBLE d2= DOUBLE_POSITIVE ) )
            int alt115=3;
            switch ( input.LA(1) ) {
            case BIG_INTEGER:
                {
                alt115=1;
                }
                break;
            case BIG_DECIMAL:
                {
                alt115=2;
                }
                break;
            case DOUBLE:
                {
                alt115=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return c;}
                NoViableAltException nvae =
                    new NoViableAltException("", 115, 0, input);

                throw nvae;
            }

            switch (alt115) {
                case 1 :
                    // IbmSparqlAstWalker.g:1006:6: ^( BIG_INTEGER i= INTEGER_POSITIVE )
                    {
                    match(input,BIG_INTEGER,FOLLOW_BIG_INTEGER_in_numericLiteralPositive7717); if (state.failed) return c;

                    match(input, Token.DOWN, null); if (state.failed) return c;
                    i=(XTree)match(input,INTEGER_POSITIVE,FOLLOW_INTEGER_POSITIVE_in_numericLiteralPositive7722); if (state.failed) return c;

                    match(input, Token.UP, null); if (state.failed) return c;
                    if ( state.backtracking==0 ) {
                       c = new Constant((i!=null?i.getText():null), new BigInteger((i!=null?i.getText():null).substring(1)));		
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlAstWalker.g:1007:5: ^( BIG_DECIMAL d1= DECIMAL_POSITIVE )
                    {
                    match(input,BIG_DECIMAL,FOLLOW_BIG_DECIMAL_in_numericLiteralPositive7735); if (state.failed) return c;

                    match(input, Token.DOWN, null); if (state.failed) return c;
                    d1=(XTree)match(input,DECIMAL_POSITIVE,FOLLOW_DECIMAL_POSITIVE_in_numericLiteralPositive7739); if (state.failed) return c;

                    match(input, Token.UP, null); if (state.failed) return c;
                    if ( state.backtracking==0 ) {
                       c = new Constant((d1!=null?d1.getText():null), new BigDecimal((d1!=null?d1.getText():null).substring(1)));	
                    }

                    }
                    break;
                case 3 :
                    // IbmSparqlAstWalker.g:1008:5: ^( DOUBLE d2= DOUBLE_POSITIVE )
                    {
                    match(input,DOUBLE,FOLLOW_DOUBLE_in_numericLiteralPositive7752); if (state.failed) return c;

                    match(input, Token.DOWN, null); if (state.failed) return c;
                    d2=(XTree)match(input,DOUBLE_POSITIVE,FOLLOW_DOUBLE_POSITIVE_in_numericLiteralPositive7757); if (state.failed) return c;

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
    // IbmSparqlAstWalker.g:1011:1: numericLiteralNegative returns [Constant c] : ( ^( BIG_INTEGER i= INTEGER_NEGATIVE ) | ^( BIG_DECIMAL d1= DECIMAL_NEGATIVE ) | ^( DOUBLE d2= DOUBLE_NEGATIVE ) );
    public final Constant numericLiteralNegative() throws RecognitionException {
        Constant c = null;

        XTree i=null;
        XTree d1=null;
        XTree d2=null;

        try {
            // IbmSparqlAstWalker.g:1012:2: ( ^( BIG_INTEGER i= INTEGER_NEGATIVE ) | ^( BIG_DECIMAL d1= DECIMAL_NEGATIVE ) | ^( DOUBLE d2= DOUBLE_NEGATIVE ) )
            int alt116=3;
            switch ( input.LA(1) ) {
            case BIG_INTEGER:
                {
                alt116=1;
                }
                break;
            case BIG_DECIMAL:
                {
                alt116=2;
                }
                break;
            case DOUBLE:
                {
                alt116=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return c;}
                NoViableAltException nvae =
                    new NoViableAltException("", 116, 0, input);

                throw nvae;
            }

            switch (alt116) {
                case 1 :
                    // IbmSparqlAstWalker.g:1012:6: ^( BIG_INTEGER i= INTEGER_NEGATIVE )
                    {
                    match(input,BIG_INTEGER,FOLLOW_BIG_INTEGER_in_numericLiteralNegative7782); if (state.failed) return c;

                    match(input, Token.DOWN, null); if (state.failed) return c;
                    i=(XTree)match(input,INTEGER_NEGATIVE,FOLLOW_INTEGER_NEGATIVE_in_numericLiteralNegative7787); if (state.failed) return c;

                    match(input, Token.UP, null); if (state.failed) return c;
                    if ( state.backtracking==0 ) {
                       c = new Constant((i!=null?i.getText():null), new BigInteger((i!=null?i.getText():null)));		
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlAstWalker.g:1013:5: ^( BIG_DECIMAL d1= DECIMAL_NEGATIVE )
                    {
                    match(input,BIG_DECIMAL,FOLLOW_BIG_DECIMAL_in_numericLiteralNegative7800); if (state.failed) return c;

                    match(input, Token.DOWN, null); if (state.failed) return c;
                    d1=(XTree)match(input,DECIMAL_NEGATIVE,FOLLOW_DECIMAL_NEGATIVE_in_numericLiteralNegative7804); if (state.failed) return c;

                    match(input, Token.UP, null); if (state.failed) return c;
                    if ( state.backtracking==0 ) {
                       c = new Constant((d1!=null?d1.getText():null), new BigDecimal((d1!=null?d1.getText():null)));	
                    }

                    }
                    break;
                case 3 :
                    // IbmSparqlAstWalker.g:1014:5: ^( DOUBLE d2= DOUBLE_NEGATIVE )
                    {
                    match(input,DOUBLE,FOLLOW_DOUBLE_in_numericLiteralNegative7817); if (state.failed) return c;

                    match(input, Token.DOWN, null); if (state.failed) return c;
                    d2=(XTree)match(input,DOUBLE_NEGATIVE,FOLLOW_DOUBLE_NEGATIVE_in_numericLiteralNegative7822); if (state.failed) return c;

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
    // IbmSparqlAstWalker.g:1017:1: booleanLiteral returns [Boolean b] : ( ^( BOOLEAN TRUE ) | ^( BOOLEAN FALSE ) );
    public final Boolean booleanLiteral() throws RecognitionException {
        Boolean b = null;

        try {
            // IbmSparqlAstWalker.g:1018:2: ( ^( BOOLEAN TRUE ) | ^( BOOLEAN FALSE ) )
            int alt117=2;
            int LA117_0 = input.LA(1);

            if ( (LA117_0==BOOLEAN) ) {
                int LA117_1 = input.LA(2);

                if ( (LA117_1==DOWN) ) {
                    int LA117_2 = input.LA(3);

                    if ( (LA117_2==TRUE) ) {
                        alt117=1;
                    }
                    else if ( (LA117_2==FALSE) ) {
                        alt117=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return b;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 117, 2, input);

                        throw nvae;
                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return b;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 117, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return b;}
                NoViableAltException nvae =
                    new NoViableAltException("", 117, 0, input);

                throw nvae;
            }
            switch (alt117) {
                case 1 :
                    // IbmSparqlAstWalker.g:1018:6: ^( BOOLEAN TRUE )
                    {
                    match(input,BOOLEAN,FOLLOW_BOOLEAN_in_booleanLiteral7846); if (state.failed) return b;

                    match(input, Token.DOWN, null); if (state.failed) return b;
                    match(input,TRUE,FOLLOW_TRUE_in_booleanLiteral7848); if (state.failed) return b;

                    match(input, Token.UP, null); if (state.failed) return b;
                    if ( state.backtracking==0 ) {
                       b = Boolean.TRUE;  
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlAstWalker.g:1019:6: ^( BOOLEAN FALSE )
                    {
                    match(input,BOOLEAN,FOLLOW_BOOLEAN_in_booleanLiteral7863); if (state.failed) return b;

                    match(input, Token.DOWN, null); if (state.failed) return b;
                    match(input,FALSE,FOLLOW_FALSE_in_booleanLiteral7865); if (state.failed) return b;

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
    // IbmSparqlAstWalker.g:1022:1: string returns [String s] : ( ^( STRING s1= STRING_LITERAL1 ) | ^( STRING s2= STRING_LITERAL2 ) | ^( STRING s3= STRING_LITERAL_LONG1 ) | ^( STRING s4= STRING_LITERAL_LONG2 ) );
    public final String string() throws RecognitionException {
        String s = null;

        XTree s1=null;
        XTree s2=null;
        XTree s3=null;
        XTree s4=null;

        try {
            // IbmSparqlAstWalker.g:1023:2: ( ^( STRING s1= STRING_LITERAL1 ) | ^( STRING s2= STRING_LITERAL2 ) | ^( STRING s3= STRING_LITERAL_LONG1 ) | ^( STRING s4= STRING_LITERAL_LONG2 ) )
            int alt118=4;
            int LA118_0 = input.LA(1);

            if ( (LA118_0==STRING) ) {
                int LA118_1 = input.LA(2);

                if ( (LA118_1==DOWN) ) {
                    switch ( input.LA(3) ) {
                    case STRING_LITERAL1:
                        {
                        alt118=1;
                        }
                        break;
                    case STRING_LITERAL2:
                        {
                        alt118=2;
                        }
                        break;
                    case STRING_LITERAL_LONG1:
                        {
                        alt118=3;
                        }
                        break;
                    case STRING_LITERAL_LONG2:
                        {
                        alt118=4;
                        }
                        break;
                    default:
                        if (state.backtracking>0) {state.failed=true; return s;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 118, 2, input);

                        throw nvae;
                    }

                }
                else {
                    if (state.backtracking>0) {state.failed=true; return s;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 118, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return s;}
                NoViableAltException nvae =
                    new NoViableAltException("", 118, 0, input);

                throw nvae;
            }
            switch (alt118) {
                case 1 :
                    // IbmSparqlAstWalker.g:1023:4: ^( STRING s1= STRING_LITERAL1 )
                    {
                    match(input,STRING,FOLLOW_STRING_in_string7888); if (state.failed) return s;

                    match(input, Token.DOWN, null); if (state.failed) return s;
                    s1=(XTree)match(input,STRING_LITERAL1,FOLLOW_STRING_LITERAL1_in_string7892); if (state.failed) return s;

                    match(input, Token.UP, null); if (state.failed) return s;
                    if ( state.backtracking==0 ) {
                       s = new String((s1!=null?s1.getText():null)); 
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlAstWalker.g:1024:4: ^( STRING s2= STRING_LITERAL2 )
                    {
                    match(input,STRING,FOLLOW_STRING_in_string7909); if (state.failed) return s;

                    match(input, Token.DOWN, null); if (state.failed) return s;
                    s2=(XTree)match(input,STRING_LITERAL2,FOLLOW_STRING_LITERAL2_in_string7913); if (state.failed) return s;

                    match(input, Token.UP, null); if (state.failed) return s;
                    if ( state.backtracking==0 ) {
                       s = new String((s2!=null?s2.getText():null)); 
                    }

                    }
                    break;
                case 3 :
                    // IbmSparqlAstWalker.g:1025:4: ^( STRING s3= STRING_LITERAL_LONG1 )
                    {
                    match(input,STRING,FOLLOW_STRING_in_string7930); if (state.failed) return s;

                    match(input, Token.DOWN, null); if (state.failed) return s;
                    s3=(XTree)match(input,STRING_LITERAL_LONG1,FOLLOW_STRING_LITERAL_LONG1_in_string7934); if (state.failed) return s;

                    match(input, Token.UP, null); if (state.failed) return s;
                    if ( state.backtracking==0 ) {
                       s = new String((s3!=null?s3.getText():null)); 
                    }

                    }
                    break;
                case 4 :
                    // IbmSparqlAstWalker.g:1026:4: ^( STRING s4= STRING_LITERAL_LONG2 )
                    {
                    match(input,STRING,FOLLOW_STRING_in_string7946); if (state.failed) return s;

                    match(input, Token.DOWN, null); if (state.failed) return s;
                    s4=(XTree)match(input,STRING_LITERAL_LONG2,FOLLOW_STRING_LITERAL_LONG2_in_string7950); if (state.failed) return s;

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
    // IbmSparqlAstWalker.g:1029:1: iRIref returns [IRI r] : ( ^( IRI i= IRI_REF ) | p= prefixedName );
    public final IRI iRIref() throws RecognitionException {
        IRI r = null;

        XTree i=null;
        String p = null;


        try {
            // IbmSparqlAstWalker.g:1030:2: ( ^( IRI i= IRI_REF ) | p= prefixedName )
            int alt119=2;
            int LA119_0 = input.LA(1);

            if ( (LA119_0==IRI) ) {
                alt119=1;
            }
            else if ( ((LA119_0>=PREFIXED_NAME && LA119_0<=PREFIXED_NS)) ) {
                alt119=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return r;}
                NoViableAltException nvae =
                    new NoViableAltException("", 119, 0, input);

                throw nvae;
            }
            switch (alt119) {
                case 1 :
                    // IbmSparqlAstWalker.g:1030:4: ^( IRI i= IRI_REF )
                    {
                    match(input,IRI,FOLLOW_IRI_in_iRIref7974); if (state.failed) return r;

                    match(input, Token.DOWN, null); if (state.failed) return r;
                    i=(XTree)match(input,IRI_REF,FOLLOW_IRI_REF_in_iRIref7978); if (state.failed) return r;

                    match(input, Token.UP, null); if (state.failed) return r;
                    if ( state.backtracking==0 ) {
                       r = new IRI((i!=null?i.getText():null).substring(1, (i!=null?i.getText():null).length()-1)); 	
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlAstWalker.g:1031:6: p= prefixedName
                    {
                    pushFollow(FOLLOW_prefixedName_in_iRIref7990);
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
    // IbmSparqlAstWalker.g:1034:1: prefixedName returns [String s] : ( ^( PREFIXED_NAME n1= PNAME_LN ) | ^( PREFIXED_NS n2= PNAME_NS ) );
    public final String prefixedName() throws RecognitionException {
        String s = null;

        XTree n1=null;
        XTree n2=null;

        try {
            // IbmSparqlAstWalker.g:1035:2: ( ^( PREFIXED_NAME n1= PNAME_LN ) | ^( PREFIXED_NS n2= PNAME_NS ) )
            int alt120=2;
            int LA120_0 = input.LA(1);

            if ( (LA120_0==PREFIXED_NAME) ) {
                alt120=1;
            }
            else if ( (LA120_0==PREFIXED_NS) ) {
                alt120=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return s;}
                NoViableAltException nvae =
                    new NoViableAltException("", 120, 0, input);

                throw nvae;
            }
            switch (alt120) {
                case 1 :
                    // IbmSparqlAstWalker.g:1035:4: ^( PREFIXED_NAME n1= PNAME_LN )
                    {
                    match(input,PREFIXED_NAME,FOLLOW_PREFIXED_NAME_in_prefixedName8012); if (state.failed) return s;

                    match(input, Token.DOWN, null); if (state.failed) return s;
                    n1=(XTree)match(input,PNAME_LN,FOLLOW_PNAME_LN_in_prefixedName8016); if (state.failed) return s;

                    match(input, Token.UP, null); if (state.failed) return s;
                    if ( state.backtracking==0 ) {
                       s = new String(n1.getText()); 
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlAstWalker.g:1036:4: ^( PREFIXED_NS n2= PNAME_NS )
                    {
                    match(input,PREFIXED_NS,FOLLOW_PREFIXED_NS_in_prefixedName8025); if (state.failed) return s;

                    match(input, Token.DOWN, null); if (state.failed) return s;
                    n2=(XTree)match(input,PNAME_NS,FOLLOW_PNAME_NS_in_prefixedName8029); if (state.failed) return s;

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
    // IbmSparqlAstWalker.g:1039:1: blankNode returns [BlankNode bn] : (b= BLANK_NODE_LABEL | ^( ANNON t= OPEN_SQ_BRACKET ) );
    public final BlankNode blankNode() throws RecognitionException {
        BlankNode bn = null;

        XTree b=null;
        XTree t=null;

        try {
            // IbmSparqlAstWalker.g:1040:2: (b= BLANK_NODE_LABEL | ^( ANNON t= OPEN_SQ_BRACKET ) )
            int alt121=2;
            int LA121_0 = input.LA(1);

            if ( (LA121_0==BLANK_NODE_LABEL) ) {
                alt121=1;
            }
            else if ( (LA121_0==ANNON) ) {
                alt121=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return bn;}
                NoViableAltException nvae =
                    new NoViableAltException("", 121, 0, input);

                throw nvae;
            }
            switch (alt121) {
                case 1 :
                    // IbmSparqlAstWalker.g:1040:6: b= BLANK_NODE_LABEL
                    {
                    b=(XTree)match(input,BLANK_NODE_LABEL,FOLLOW_BLANK_NODE_LABEL_in_blankNode8053); if (state.failed) return bn;
                    if ( state.backtracking==0 ) {
                       bn = new BlankNode(b.getText()); 
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlAstWalker.g:1041:6: ^( ANNON t= OPEN_SQ_BRACKET )
                    {
                    match(input,ANNON,FOLLOW_ANNON_in_blankNode8064); if (state.failed) return bn;

                    match(input, Token.DOWN, null); if (state.failed) return bn;
                    t=(XTree)match(input,OPEN_SQ_BRACKET,FOLLOW_OPEN_SQ_BRACKET_in_blankNode8068); if (state.failed) return bn;

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

    // $ANTLR start synpred1_IbmSparqlAstWalker
    public final void synpred1_IbmSparqlAstWalker_fragment() throws RecognitionException {   
        // IbmSparqlAstWalker.g:451:25: ( NIL )
        // IbmSparqlAstWalker.g:451:27: NIL
        {
        match(input,NIL,FOLLOW_NIL_in_synpred1_IbmSparqlAstWalker3016); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred1_IbmSparqlAstWalker

    // $ANTLR start synpred2_IbmSparqlAstWalker
    public final void synpred2_IbmSparqlAstWalker_fragment() throws RecognitionException {   
        // IbmSparqlAstWalker.g:451:81: ( NIL )
        // IbmSparqlAstWalker.g:451:83: NIL
        {
        match(input,NIL,FOLLOW_NIL_in_synpred2_IbmSparqlAstWalker3038); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred2_IbmSparqlAstWalker

    // Delegated rules

    public final boolean synpred1_IbmSparqlAstWalker() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred1_IbmSparqlAstWalker_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred2_IbmSparqlAstWalker() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred2_IbmSparqlAstWalker_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA17 dfa17 = new DFA17(this);
    protected DFA96 dfa96 = new DFA96(this);
    protected DFA99 dfa99 = new DFA99(this);
    protected DFA113 dfa113 = new DFA113(this);
    static final String DFA17_eotS =
        "\46\uffff";
    static final String DFA17_eofS =
        "\46\uffff";
    static final String DFA17_minS =
        "\1\36\1\uffff\2\2\1\uffff\1\117\1\3\1\2\1\43\4\2\1\72\1\u00ce\1"+
        "\102\1\43\3\3\3\2\3\3\1\72\1\u00ce\1\102\4\3\1\51\4\3";
    static final String DFA17_maxS =
        "\1\74\1\uffff\2\2\1\uffff\1\117\1\37\1\2\1\u0086\4\2\1\72\1\u00ce"+
        "\1\102\1\u0086\3\3\3\2\3\3\1\72\1\u00ce\1\102\1\117\3\3\1\74\4\3";
    static final String DFA17_acceptS =
        "\1\uffff\1\1\2\uffff\1\2\41\uffff";
    static final String DFA17_specialS =
        "\46\uffff}>";
    static final String[] DFA17_transitionS = {
            "\2\1\11\uffff\1\2\17\uffff\1\3\2\uffff\1\4",
            "",
            "\1\5",
            "\1\6",
            "",
            "\1\7",
            "\1\1\24\uffff\1\1\2\uffff\1\1\2\uffff\2\4",
            "\1\10",
            "\1\12\1\13\53\uffff\1\14\65\uffff\1\11",
            "\1\15",
            "\1\16",
            "\1\17",
            "\1\20",
            "\1\21",
            "\1\22",
            "\1\23",
            "\1\25\1\26\141\uffff\1\24",
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
            "\1\41\113\uffff\1\7",
            "\1\42",
            "\1\43",
            "\1\44",
            "\1\2\17\uffff\1\3\2\uffff\1\4",
            "\1\45",
            "\1\45",
            "\1\45",
            "\1\35"
    };

    static final short[] DFA17_eot = DFA.unpackEncodedString(DFA17_eotS);
    static final short[] DFA17_eof = DFA.unpackEncodedString(DFA17_eofS);
    static final char[] DFA17_min = DFA.unpackEncodedStringToUnsignedChars(DFA17_minS);
    static final char[] DFA17_max = DFA.unpackEncodedStringToUnsignedChars(DFA17_maxS);
    static final short[] DFA17_accept = DFA.unpackEncodedString(DFA17_acceptS);
    static final short[] DFA17_special = DFA.unpackEncodedString(DFA17_specialS);
    static final short[][] DFA17_transition;

    static {
        int numStates = DFA17_transitionS.length;
        DFA17_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA17_transition[i] = DFA.unpackEncodedString(DFA17_transitionS[i]);
        }
    }

    class DFA17 extends DFA {

        public DFA17(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 17;
            this.eot = DFA17_eot;
            this.eof = DFA17_eof;
            this.min = DFA17_min;
            this.max = DFA17_max;
            this.accept = DFA17_accept;
            this.special = DFA17_special;
            this.transition = DFA17_transition;
        }
        public String getDescription() {
            return "180:20: ( ( constructTemplate[$cq] (d= dataset )* (w= whereClause ) (m= solutionModifier ) ) | ( (d= dataset )* ( ^( WHERE triplesTemplate[$cq,p] ) )? (m= solutionModifier ) ) )";
        }
    }
    static final String DFA96_eotS =
        "\13\uffff";
    static final String DFA96_eofS =
        "\13\uffff";
    static final String DFA96_minS =
        "\1\42\1\uffff\1\2\7\uffff\1\11";
    static final String DFA96_maxS =
        "\1\u00c1\1\uffff\1\2\7\uffff\1\u0107";
    static final String DFA96_acceptS =
        "\1\uffff\1\1\1\uffff\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\uffff";
    static final String DFA96_specialS =
        "\13\uffff}>";
    static final String[] DFA96_transitionS = {
            "\1\10\2\3\1\4\1\uffff\1\1\6\uffff\1\5\1\7\15\uffff\2\6\102\uffff"+
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
            "\75\uffff\51\1\1\uffff\30\1\3\uffff\1\1\71\uffff\1\1\2\uffff"+
            "\1\1\2\uffff\7\1"
    };

    static final short[] DFA96_eot = DFA.unpackEncodedString(DFA96_eotS);
    static final short[] DFA96_eof = DFA.unpackEncodedString(DFA96_eofS);
    static final char[] DFA96_min = DFA.unpackEncodedStringToUnsignedChars(DFA96_minS);
    static final char[] DFA96_max = DFA.unpackEncodedStringToUnsignedChars(DFA96_maxS);
    static final short[] DFA96_accept = DFA.unpackEncodedString(DFA96_acceptS);
    static final short[] DFA96_special = DFA.unpackEncodedString(DFA96_specialS);
    static final short[][] DFA96_transition;

    static {
        int numStates = DFA96_transitionS.length;
        DFA96_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA96_transition[i] = DFA.unpackEncodedString(DFA96_transitionS[i]);
        }
    }

    class DFA96 extends DFA {

        public DFA96(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 96;
            this.eot = DFA96_eot;
            this.eof = DFA96_eof;
            this.min = DFA96_min;
            this.max = DFA96_max;
            this.accept = DFA96_accept;
            this.special = DFA96_special;
            this.transition = DFA96_transition;
        }
        public String getDescription() {
            return "795:1: primaryExpression returns [Expression e] : (e1= builtInCall | i= iRIref | f= iRIFunction | r= rDFLiteral | n= numericLiteral | b= booleanLiteral | v= var | a= aggregate );";
        }
    }
    static final String DFA99_eotS =
        "\71\uffff";
    static final String DFA99_eofS =
        "\71\uffff";
    static final String DFA99_minS =
        "\1\47\7\uffff\1\2\60\uffff";
    static final String DFA99_maxS =
        "\1\u00b6\7\uffff\1\u0107\60\uffff";
    static final String DFA99_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\uffff\1\12\1\13\1\14\1\15"+
        "\1\16\1\17\1\20\1\21\1\22\1\23\1\24\1\25\1\26\1\27\1\30\1\31\1\32"+
        "\1\33\1\34\1\35\1\36\1\37\1\40\1\41\1\42\1\43\1\44\1\45\1\46\1\47"+
        "\1\50\1\51\1\52\1\53\1\54\1\55\1\56\1\57\1\60\1\61\1\62\1\63\1\64"+
        "\1\65\1\66\1\67\1\10\1\11";
    static final String DFA99_specialS =
        "\71\uffff}>";
    static final String[] DFA99_transitionS = {
            "\1\66\131\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1"+
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
            "\2\70\13\uffff\1\70\1\uffff\2\70\26\uffff\2\70\45\uffff\51\70"+
            "\1\uffff\31\70\2\uffff\1\70\71\uffff\1\70\2\uffff\1\70\2\uffff"+
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

    static final short[] DFA99_eot = DFA.unpackEncodedString(DFA99_eotS);
    static final short[] DFA99_eof = DFA.unpackEncodedString(DFA99_eofS);
    static final char[] DFA99_min = DFA.unpackEncodedStringToUnsignedChars(DFA99_minS);
    static final char[] DFA99_max = DFA.unpackEncodedStringToUnsignedChars(DFA99_maxS);
    static final short[] DFA99_accept = DFA.unpackEncodedString(DFA99_acceptS);
    static final short[] DFA99_special = DFA.unpackEncodedString(DFA99_specialS);
    static final short[][] DFA99_transition;

    static {
        int numStates = DFA99_transitionS.length;
        DFA99_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA99_transition[i] = DFA.unpackEncodedString(DFA99_transitionS[i]);
        }
    }

    class DFA99 extends DFA {

        public DFA99(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 99;
            this.eot = DFA99_eot;
            this.eof = DFA99_eof;
            this.min = DFA99_min;
            this.max = DFA99_max;
            this.accept = DFA99_accept;
            this.special = DFA99_special;
            this.transition = DFA99_transition;
        }
        public String getDescription() {
            return "810:1: builtInCall returns [Expression e] : ( ^( STR st= expression ) | ^( LANG lg= expression ) | ^( LANGMATCHES lm1= expression lm2= expression ) | ^( DATATYPE dt= expression ) | ^( BOUND v= var ) | ^( IRI e6= expression ) | ^( URI e7= expression ) | ^( BNODE e8= expression ) | BNODE | ^( RAND NIL ) | ^( ABS e9= expression ) | ^( CEIL e10= expression ) | ^( FLOOR e11= expression ) | ^( ROUND e12= expression ) | ^( CONCAT e13= expressionList ) | ^( SUBSTR e14= expression e15= expression (e16= expression )? ) | ^( STRLEN e15= expression ) | ^( UCASE e16= expression ) | ^( REPLACE e1= expression e2= expression e3= expression (e4= expression )? ) | ^( LCASE e17= expression ) | ^( ENCODE_FOR_URI e18= expression ) | ^( CONTAINS e19= expression e20= expression ) | ^( STRSTARTS e21= expression e22= expression ) | ^( STRENDS e23= expression e24= expression ) | ^( STRBEFORE e241= expression e242= expression ) | ^( STRAFTER e243= expression e244= expression ) | ^( YEAR e25= expression ) | ^( MONTH e26= expression ) | ^( DAY e27= expression ) | ^( HOURS e28= expression ) | ^( MINUTES e29= expression ) | ^( SECONDS e30= expression ) | ^( TIMEZONE e31= expression ) | ^( TZ e32= expression ) | NOW | UUID | STRUUID | ^( MD5 e33= expression ) | ^( SHA1 e34= expression ) | ^( SHA256 e36= expression ) | ^( SHA384 e37= expression ) | ^( SHA512 e38= expression ) | ^( COALESCE e39= expressionList ) | ^( IF e40= expression e41= expression e42= expression ) | ^( STRLANG e45= expression e46= expression ) | ^( STRDT e47= expression e48= expression ) | ^( SAMETERM sam1= expression sam2= expression ) | ^( ISIRI isi= expression ) | ^( ISURI isu= expression ) | ^( ISBLANK isb= expression ) | ^( ISLITERAL isl= expression ) | ^( ISNUMERIC e55= expression ) | r= regexExpression | p= existsFunc | p= notExistsFunc );";
        }
    }
    static final String DFA113_eotS =
        "\12\uffff";
    static final String DFA113_eofS =
        "\12\uffff";
    static final String DFA113_minS =
        "\1\75\3\2\1\130\1\u00c0\1\u00c1\3\uffff";
    static final String DFA113_maxS =
        "\1\u00c1\3\2\1\u00c5\1\u00c6\1\u00c7\3\uffff";
    static final String DFA113_acceptS =
        "\7\uffff\1\1\1\2\1\3";
    static final String DFA113_specialS =
        "\12\uffff}>";
    static final String[] DFA113_transitionS = {
            "\1\1\1\2\u0082\uffff\1\3",
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

    static final short[] DFA113_eot = DFA.unpackEncodedString(DFA113_eotS);
    static final short[] DFA113_eof = DFA.unpackEncodedString(DFA113_eofS);
    static final char[] DFA113_min = DFA.unpackEncodedStringToUnsignedChars(DFA113_minS);
    static final char[] DFA113_max = DFA.unpackEncodedStringToUnsignedChars(DFA113_maxS);
    static final short[] DFA113_accept = DFA.unpackEncodedString(DFA113_acceptS);
    static final short[] DFA113_special = DFA.unpackEncodedString(DFA113_specialS);
    static final short[][] DFA113_transition;

    static {
        int numStates = DFA113_transitionS.length;
        DFA113_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA113_transition[i] = DFA.unpackEncodedString(DFA113_transitionS[i]);
        }
    }

    class DFA113 extends DFA {

        public DFA113(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 113;
            this.eot = DFA113_eot;
            this.eof = DFA113_eof;
            this.min = DFA113_min;
            this.max = DFA113_max;
            this.accept = DFA113_accept;
            this.special = DFA113_special;
            this.transition = DFA113_transition;
        }
        public String getDescription() {
            return "993:1: numericLiteral returns [Constant n] : (n1= numericLiteralUnsigned | n2= numericLiteralPositive | n3= numericLiteralNegative );";
        }
    }
 

    public static final BitSet FOLLOW_ROOT_in_queryUnit83 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_query_in_queryUnit87 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_QUERY_in_query115 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_prologue_in_query125 = new BitSet(new long[]{0x0000000000000000L,0x0000000000006208L});
    public static final BitSet FOLLOW_selectQuery_in_query144 = new BitSet(new long[]{0x0000000000000008L,0x0001000000000000L});
    public static final BitSet FOLLOW_bindingsClause_in_query162 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_constructQuery_in_query219 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_describeQuery_in_query235 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_askQuery_in_query251 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PROLOGUE_in_prologue307 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_baseDecl_in_prologue309 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000003L});
    public static final BitSet FOLLOW_prefixDecl_in_prologue314 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000002L});
    public static final BitSet FOLLOW_BASE_in_baseDecl345 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_iRIref_in_baseDecl349 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PREFIX_in_prefixDecl377 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_prefixedName_in_prefixDecl381 = new BitSet(new long[]{0x0000001800000000L,0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_iRIref_in_prefixDecl386 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SELECT_in_selectQuery424 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_selectClause_in_selectQuery434 = new BitSet(new long[]{0x1200020000000000L});
    public static final BitSet FOLLOW_dataset_in_selectQuery450 = new BitSet(new long[]{0x1200020000000000L});
    public static final BitSet FOLLOW_whereClause_in_selectQuery469 = new BitSet(new long[]{0x1200020000000000L});
    public static final BitSet FOLLOW_solutionModifier_in_selectQuery485 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DATASET_in_dataset524 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_datasetClause_in_dataset533 = new BitSet(new long[]{0x0000000000000008L,0x0000000000008000L});
    public static final BitSet FOLLOW_SUB_SELECT_in_subSelect567 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_selectClause_in_subSelect576 = new BitSet(new long[]{0x1200020000000000L});
    public static final BitSet FOLLOW_whereClause_in_subSelect592 = new BitSet(new long[]{0x1200020000000000L});
    public static final BitSet FOLLOW_solutionModifier_in_subSelect608 = new BitSet(new long[]{0x0000000000000008L,0x0001000000000000L});
    public static final BitSet FOLLOW_inlineData_in_subSelect634 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TYPE_in_selectClause680 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DISTINCT_in_selectClause684 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_REDUCED_in_selectClause696 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PVARS_in_selectClause725 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_var_in_selectClause733 = new BitSet(new long[]{0x0000000400400008L,0x0000000000000080L});
    public static final BitSet FOLLOW_expVar_in_selectClause755 = new BitSet(new long[]{0x0000000400400008L,0x0000000000000080L});
    public static final BitSet FOLLOW_fexp_in_selectClause778 = new BitSet(new long[]{0x0000000400400008L,0x0000000000000080L});
    public static final BitSet FOLLOW_251_in_selectClause818 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_AS_in_expVar849 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_var_in_expVar853 = new BitSet(new long[]{0x6800C0FC00000600L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_expression_in_expVar857 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EXP_in_fexp875 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_fexp879 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CONSTRUCT_in_constructQuery910 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_constructTemplate_in_constructQuery917 = new BitSet(new long[]{0x0200020000000000L});
    public static final BitSet FOLLOW_dataset_in_constructQuery931 = new BitSet(new long[]{0x0200020000000000L});
    public static final BitSet FOLLOW_whereClause_in_constructQuery954 = new BitSet(new long[]{0x1200020000000000L});
    public static final BitSet FOLLOW_solutionModifier_in_constructQuery973 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_dataset_in_constructQuery1013 = new BitSet(new long[]{0x1200020000000000L});
    public static final BitSet FOLLOW_WHERE_in_constructQuery1035 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_triplesTemplate_in_constructQuery1101 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_solutionModifier_in_constructQuery1181 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DESCRIBE_in_describeQuery1239 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_varOrIRIref2_in_describeQuery1248 = new BitSet(new long[]{0x1200021C00000008L,0x0000000000008000L,0x0000000000000040L});
    public static final BitSet FOLLOW_251_in_describeQuery1259 = new BitSet(new long[]{0x1200020000000008L,0x0000000000008000L});
    public static final BitSet FOLLOW_datasetClause_in_describeQuery1274 = new BitSet(new long[]{0x1200020000000008L,0x0000000000008000L});
    public static final BitSet FOLLOW_whereClause_in_describeQuery1295 = new BitSet(new long[]{0x1200020000000000L});
    public static final BitSet FOLLOW_solutionModifier_in_describeQuery1315 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ASK_in_askQuery1356 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_datasetClause_in_askQuery1364 = new BitSet(new long[]{0x0200000000000008L,0x0000000000008000L});
    public static final BitSet FOLLOW_whereClause_in_askQuery1380 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FROM_in_datasetClause1415 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_defaultGraphClause_in_datasetClause1422 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_namedGraphClause_in_datasetClause1436 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_sourceSelector_in_defaultGraphClause1469 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NAMED_in_namedGraphClause1491 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_sourceSelector_in_namedGraphClause1495 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_iRIref_in_sourceSelector1521 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHERE_in_whereClause1544 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_groupGraphPattern_in_whereClause1548 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MODIFIERS_in_solutionModifier1579 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_groupClause_in_solutionModifier1588 = new BitSet(new long[]{0x0000080000000008L,0x0000000002880000L});
    public static final BitSet FOLLOW_havingClause_in_solutionModifier1605 = new BitSet(new long[]{0x0000080000000008L,0x0000000002800000L});
    public static final BitSet FOLLOW_orderClause_in_solutionModifier1622 = new BitSet(new long[]{0x0000000000000008L,0x0000000002800000L});
    public static final BitSet FOLLOW_limitOffsetClauses_in_solutionModifier1639 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_GROUP_BY_in_groupClause1665 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_groupCondition_in_groupClause1669 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_builtInCall_in_groupCondition1702 = new BitSet(new long[]{0x000010A400000002L,0x0000000000000000L,0x007FFFDFFFFFFFFEL});
    public static final BitSet FOLLOW_functionCall_in_groupCondition1721 = new BitSet(new long[]{0x000010A400000002L,0x0000000000000000L,0x007FFFDFFFFFFFFEL});
    public static final BitSet FOLLOW_CONDITION_in_groupCondition1739 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_groupCondition1743 = new BitSet(new long[]{0x0000000400000008L});
    public static final BitSet FOLLOW_var_in_groupCondition1748 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_var_in_groupCondition1765 = new BitSet(new long[]{0x000010A400000002L,0x0000000000000000L,0x007FFFDFFFFFFFFEL});
    public static final BitSet FOLLOW_HAVING_in_havingClause1839 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_havingCondition_in_havingClause1844 = new BitSet(new long[]{0x000000E000000008L,0x0000000000000000L,0x007FFFDFFFFFFFFEL});
    public static final BitSet FOLLOW_constraint_in_havingCondition1872 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ORDER_BY_in_orderClause1901 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_orderCondition_in_orderClause1908 = new BitSet(new long[]{0x000000E400000008L,0x0000000000600000L,0x007FFFDFFFFFFFFEL});
    public static final BitSet FOLLOW_ASC_in_orderCondition1935 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_brackettedExpression_in_orderCondition1939 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DESC_in_orderCondition1951 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_brackettedExpression_in_orderCondition1955 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_constraint_in_orderCondition1969 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_in_orderCondition1985 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_limitClause_in_limitOffsetClauses2025 = new BitSet(new long[]{0x0000000000000002L,0x0000000002800000L});
    public static final BitSet FOLLOW_offsetClause_in_limitOffsetClauses2033 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_offsetClause_in_limitOffsetClauses2046 = new BitSet(new long[]{0x0000000000000002L,0x0000000000800000L});
    public static final BitSet FOLLOW_limitClause_in_limitOffsetClauses2053 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LIMIT_in_limitClause2091 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_INTEGER_in_limitClause2096 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_OFFSET_in_offsetClause2123 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_INTEGER_in_offsetClause2128 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_inlineData_in_bindingsClause2152 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_iRIref_in_bindingValue2173 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rDFLiteral_in_bindingValue2177 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numericLiteral_in_bindingValue2181 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_booleanLiteral_in_bindingValue2185 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UNDEF_in_bindingValue2189 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_triplesSameSubject_in_triplesTemplate2209 = new BitSet(new long[]{0x0000000000000002L,0x0000800000000000L});
    public static final BitSet FOLLOW_DOT_in_triplesTemplate2216 = new BitSet(new long[]{0x00000000C0000002L});
    public static final BitSet FOLLOW_triplesTemplate_in_triplesTemplate2218 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GROUP_GRAPH_PATTERN_in_groupGraphPattern2248 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_groupGraphPatternSub_in_groupGraphPattern2250 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_subSelect_in_groupGraphPattern2262 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_triplesBlock_in_groupGraphPatternSub2294 = new BitSet(new long[]{0x0000000019000002L,0x007F200000000000L});
    public static final BitSet FOLLOW_filter_in_groupGraphPatternSub2326 = new BitSet(new long[]{0x0000000019000002L,0x007F200000000000L});
    public static final BitSet FOLLOW_graphPatternNewBGP_in_groupGraphPatternSub2364 = new BitSet(new long[]{0x0000000019000002L,0x007F200000000000L});
    public static final BitSet FOLLOW_TRIPLES_BLOCK_in_triplesBlock2423 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_triples_in_triplesBlock2456 = new BitSet(new long[]{0x00000000C0000008L});
    public static final BitSet FOLLOW_triples2_in_triplesBlock2472 = new BitSet(new long[]{0x00000000C0000008L});
    public static final BitSet FOLLOW_TRIPLE_in_triples2520 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_SUBJECT_in_triples2523 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_graphNode_in_triples2529 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PREDICATE_in_triples2565 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_predicate_in_triples2571 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VALUE_in_triples2611 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_object_in_triples2616 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TRIPLE2_in_triples22671 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_SUBJECT_in_triples22674 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_triplesNode_in_triples22680 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PROPERTY_LIST_in_triples22718 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_PREDICATE_in_triples22730 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_predicate_in_triples22737 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VALUE_in_triples22787 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_object_in_triples22792 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_groupMinusOrUnionGraphPattern_in_graphPatternNewBGP2852 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_optionalGraphPattern_in_graphPatternNewBGP2872 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_graphGraphPattern_in_graphPatternNewBGP2893 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_serviceGraphPattern_in_graphPatternNewBGP2912 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_bind_in_graphPatternNewBGP2921 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inlineData_in_graphPatternNewBGP2941 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VALUES_in_inlineData2974 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_dataBlock_in_inlineData2978 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_INLINE_DATA_in_dataBlock3010 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NIL_in_dataBlock3020 = new BitSet(new long[]{0x6000C01800000808L,0x0000000008000000L,0x0000000000000040L,0x0000000000000002L});
    public static final BitSet FOLLOW_var_in_dataBlock3027 = new BitSet(new long[]{0x6000C01C00000808L,0x0000000008000000L,0x0000000000000040L,0x0000000000000002L});
    public static final BitSet FOLLOW_NIL_in_dataBlock3042 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_dataBlockValue_in_dataBlock3049 = new BitSet(new long[]{0x6000C01800000008L,0x0000000008000000L,0x0000000000000040L,0x0000000000000002L});
    public static final BitSet FOLLOW_iRIref_in_dataBlockValue3085 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rDFLiteral_in_dataBlockValue3101 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numericLiteral_in_dataBlockValue3116 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_booleanLiteral_in_dataBlockValue3131 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UNDEF_in_dataBlockValue3147 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPTIONAL_in_optionalGraphPattern3177 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_groupGraphPattern_in_optionalGraphPattern3181 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_GRAPH_in_graphGraphPattern3211 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_varOrIRIref2_in_graphGraphPattern3215 = new BitSet(new long[]{0x0000000009000000L,0x0030000000000000L});
    public static final BitSet FOLLOW_groupGraphPattern_in_graphGraphPattern3219 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SERVICE_in_serviceGraphPattern3255 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_SILENT_in_serviceGraphPattern3258 = new BitSet(new long[]{0x0000001C00000000L,0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_varOrIRIref_in_serviceGraphPattern3266 = new BitSet(new long[]{0x0000000009000000L,0x0030000000000000L});
    public static final BitSet FOLLOW_groupGraphPattern_in_serviceGraphPattern3270 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BIND_in_bind3303 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_var_in_bind3307 = new BitSet(new long[]{0x6800C0FC00000600L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_expression_in_bind3313 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_UNION_in_groupMinusOrUnionGraphPattern3345 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_groupGraphPattern_in_groupMinusOrUnionGraphPattern3366 = new BitSet(new long[]{0x0000000009000000L,0x0030000000000000L});
    public static final BitSet FOLLOW_groupGraphPattern_in_groupMinusOrUnionGraphPattern3397 = new BitSet(new long[]{0x0000000009000008L,0x0030000000000000L});
    public static final BitSet FOLLOW_MINUS_in_groupMinusOrUnionGraphPattern3433 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_groupGraphPattern_in_groupMinusOrUnionGraphPattern3448 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_groupGraphPattern_in_groupMinusOrUnionGraphPattern3475 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FILTER_in_filter3508 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_constraint_in_filter3512 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_brackettedExpression_in_constraint3536 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_builtInCall_in_constraint3546 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionCall_in_constraint3559 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_functionCall3581 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_iRIref_in_functionCall3585 = new BitSet(new long[]{0x6800C0FC00000E00L,0xF000000000000010L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_argList_in_functionCall3589 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NIL_in_argList3618 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DISTINCT_in_argList3631 = new BitSet(new long[]{0x6800C0FC00000600L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_expression_in_argList3636 = new BitSet(new long[]{0x6800C0FC00000602L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_expression_in_argList3649 = new BitSet(new long[]{0x6800C0FC00000602L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_NIL_in_expressionList3684 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_expressionList3707 = new BitSet(new long[]{0x6800C0FC00000602L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_constructTriples_in_constructTemplate3727 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_triples_in_constructTriples3748 = new BitSet(new long[]{0x00000000C0000002L});
    public static final BitSet FOLLOW_triples2_in_constructTriples3759 = new BitSet(new long[]{0x00000000C0000002L});
    public static final BitSet FOLLOW_triples_in_triplesSameSubject3796 = new BitSet(new long[]{0x0000000040000002L});
    public static final BitSet FOLLOW_triples2_in_triplesSameSubject3808 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_graphNode_in_object3830 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_varOrIRIref_in_verb3852 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_252_in_verb3862 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_path_in_verbPath3880 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_in_verbSimple3899 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathAlternative_in_path3916 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathSequence_in_pathAlternative3929 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x2000000000000000L});
    public static final BitSet FOLLOW_253_in_pathAlternative3933 = new BitSet(new long[]{0x0000001800000000L,0x0000000000000040L,0x0000000000000040L,0x9000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_pathSequence_in_pathAlternative3935 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x2000000000000000L});
    public static final BitSet FOLLOW_pathEltOrInverse_in_pathSequence3952 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x4000000000000000L});
    public static final BitSet FOLLOW_254_in_pathSequence3956 = new BitSet(new long[]{0x0000001800000000L,0x0000000000000040L,0x0000000000000040L,0x9000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_pathEltOrInverse_in_pathSequence3958 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x4000000000000000L});
    public static final BitSet FOLLOW_pathPrimary_in_pathElt3975 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0800000000000000L,0x0000000000000003L});
    public static final BitSet FOLLOW_pathMod_in_pathElt3977 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathElt_in_pathEltOrInverse3992 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_255_in_pathEltOrInverse3996 = new BitSet(new long[]{0x0000001800000000L,0x0000000000000040L,0x0000000000000040L,0x1000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_pathElt_in_pathEltOrInverse3998 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_251_in_pathMod4013 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_256_in_pathMod4022 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_257_in_pathMod4031 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_iRIref_in_pathPrimary4049 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_252_in_pathPrimary4053 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_258_in_pathPrimary4057 = new BitSet(new long[]{0x0000001800000100L,0x0000000000000040L,0x0000000000000040L,0x1000000000000000L});
    public static final BitSet FOLLOW_pathNegatedPropertySet_in_pathPrimary4059 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_BRACE_in_pathPrimary4063 = new BitSet(new long[]{0x0000001800000000L,0x0000000000000040L,0x0000000000000040L,0x9000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_path_in_pathPrimary4065 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_CLOSE_BRACE_in_pathPrimary4067 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathOneInPropertySet_in_pathNegatedPropertySet4097 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_BRACE_in_pathNegatedPropertySet4105 = new BitSet(new long[]{0x0000001800000100L,0x0000000000000100L,0x0000000000000040L,0x1000000000000000L});
    public static final BitSet FOLLOW_pathOneInPropertySet_in_pathNegatedPropertySet4113 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L,0x0000000000000000L,0x2000000000000000L});
    public static final BitSet FOLLOW_253_in_pathNegatedPropertySet4117 = new BitSet(new long[]{0x0000001800000100L,0x0000000000000000L,0x0000000000000040L,0x1000000000000000L});
    public static final BitSet FOLLOW_pathOneInPropertySet_in_pathNegatedPropertySet4123 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L,0x0000000000000000L,0x2000000000000000L});
    public static final BitSet FOLLOW_CLOSE_BRACE_in_pathNegatedPropertySet4134 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_iRIref_in_pathOneInPropertySet4162 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_252_in_pathOneInPropertySet4169 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INV_in_pathOneInPropertySet4178 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_iRIrefOrRDFType_in_pathOneInPropertySet4184 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_iRIref_in_iRIrefOrRDFType4211 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_252_in_iRIrefOrRDFType4220 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INTEGER_in_integer4241 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRIPLES_NODE_in_triplesNode4260 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_collection_in_triplesNode4271 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_blankNodePropertyList_in_triplesNode4288 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PROPERTY_LIST_in_blankNodePropertyList4327 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_PREDICATE_in_blankNodePropertyList4348 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_predicate_in_blankNodePropertyList4352 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VALUE_in_blankNodePropertyList4367 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_graphNode_in_blankNodePropertyList4372 = new BitSet(new long[]{0x6020C01C00001808L,0x0000000000000000L,0x0000000000000040L,0x0000000000008002L});
    public static final BitSet FOLLOW_var_in_predicate4428 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_iRIref_in_predicate4443 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_252_in_predicate4454 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALT_in_predicate4469 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_predicate_in_predicate4474 = new BitSet(new long[]{0x0000001C000001E8L,0x0000000000000000L,0x0000000000000040L,0x1000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_SEQ_in_predicate4490 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_predicate_in_predicate4495 = new BitSet(new long[]{0x0000001C000001E8L,0x0000000000000000L,0x0000000000000040L,0x1000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_ELT_in_predicate4511 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_predicate_in_predicate4515 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0800000000000000L,0x0000000000000003L});
    public static final BitSet FOLLOW_pathMod_in_predicate4521 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_INV_in_predicate4536 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_predicate_in_predicate4540 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_258_in_predicate4551 = new BitSet(new long[]{0x0000001800000100L,0x0000000000000040L,0x0000000000000040L,0x1000000000000000L});
    public static final BitSet FOLLOW_pathNegatedPropertySet_in_predicate4557 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLLECTION_in_collection4585 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_graphNode_in_collection4590 = new BitSet(new long[]{0x6020C01C00001808L,0x0000000000000000L,0x0000000000000040L,0x0000000000008002L});
    public static final BitSet FOLLOW_var_in_graphNode4616 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_graphTerm_in_graphNode4629 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_triplesNode_in_graphNode4641 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_in_varOrTerm4664 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_graphTerm_in_varOrTerm4679 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_in_varOrIRIref4701 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_iRIref_in_varOrIRIref4716 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_in_varOrIRIref24751 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_iRIref_in_varOrIRIref24767 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VAR_in_var4794 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VAR1_in_var4801 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VAR2_in_var4816 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_iRIref_in_graphTerm4850 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rDFLiteral_in_graphTerm4864 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numericLiteral_in_graphTerm4876 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_booleanLiteral_in_graphTerm4889 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_blankNode_in_graphTerm4902 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NIL_in_graphTerm4915 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LOGICAL_OR_in_expression4946 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression4958 = new BitSet(new long[]{0x6800C0FC00000600L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_expression_in_expression4979 = new BitSet(new long[]{0x6800C0FC00000608L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_LOGICAL_AND_in_expression5004 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression5016 = new BitSet(new long[]{0x6800C0FC00000600L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_expression_in_expression5037 = new BitSet(new long[]{0x6800C0FC00000608L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_259_in_expression5064 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression5078 = new BitSet(new long[]{0x6800C0FC00000600L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_expression_in_expression5089 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_260_in_expression5109 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression5123 = new BitSet(new long[]{0x6800C0FC00000600L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_expression_in_expression5134 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LT_in_expression5154 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression5169 = new BitSet(new long[]{0x6800C0FC00000600L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_expression_in_expression5184 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_261_in_expression5204 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression5219 = new BitSet(new long[]{0x6800C0FC00000600L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_expression_in_expression5230 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LTE_in_expression5250 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression5264 = new BitSet(new long[]{0x6800C0FC00000600L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_expression_in_expression5275 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_262_in_expression5295 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression5309 = new BitSet(new long[]{0x6800C0FC00000600L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_expression_in_expression5320 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IN_in_expression5340 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression5367 = new BitSet(new long[]{0x6800C0FC00000E00L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_expressionList_in_expression5380 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NOT_in_expression5426 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression5433 = new BitSet(new long[]{0x6800C0FC00000E00L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_expressionList_in_expression5441 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_257_in_expression5475 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression5502 = new BitSet(new long[]{0x6800C0FC00000608L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_expression_in_expression5514 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BROKEN_PLUS_in_expression5537 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression5556 = new BitSet(new long[]{0x6800C0FC00000600L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_expression_in_expression5567 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_263_in_expression5587 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression5613 = new BitSet(new long[]{0x6800C0FC00000608L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_expression_in_expression5625 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BROKEN_MINUS_in_expression5642 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression5660 = new BitSet(new long[]{0x6800C0FC00000600L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_expression_in_expression5671 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_251_in_expression5691 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression5718 = new BitSet(new long[]{0x6800C0FC00000600L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_expression_in_expression5729 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_254_in_expression5749 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression5776 = new BitSet(new long[]{0x6800C0FC00000600L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_expression_in_expression5786 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_258_in_expression5806 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression5810 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_brackettedExpression_in_expression5821 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primaryExpression_in_expression5831 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_builtInCall_in_primaryExpression5856 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_iRIref_in_primaryExpression5868 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_iRIFunction_in_primaryExpression5880 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rDFLiteral_in_primaryExpression5892 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numericLiteral_in_primaryExpression5904 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_booleanLiteral_in_primaryExpression5915 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_in_primaryExpression5926 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aggregate_in_primaryExpression5940 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXPRESSION_in_brackettedExpression5963 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_brackettedExpression5968 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STR_in_builtInCall5998 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6002 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LANG_in_builtInCall6015 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6019 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LANGMATCHES_in_builtInCall6032 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6036 = new BitSet(new long[]{0x6800C0FC00000600L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_expression_in_builtInCall6040 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DATATYPE_in_builtInCall6053 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6057 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BOUND_in_builtInCall6069 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_var_in_builtInCall6073 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IRI_in_builtInCall6091 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6095 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_URI_in_builtInCall6108 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6112 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BNODE_in_builtInCall6124 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6128 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BNODE_in_builtInCall6139 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RAND_in_builtInCall6150 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NIL_in_builtInCall6152 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ABS_in_builtInCall6165 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6169 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CEIL_in_builtInCall6181 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6185 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FLOOR_in_builtInCall6197 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6201 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ROUND_in_builtInCall6213 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6217 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CONCAT_in_builtInCall6229 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expressionList_in_builtInCall6233 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SUBSTR_in_builtInCall6246 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6250 = new BitSet(new long[]{0x6800C0FC00000600L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_expression_in_builtInCall6254 = new BitSet(new long[]{0x6800C0FC00000608L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_expression_in_builtInCall6278 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRLEN_in_builtInCall6301 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6305 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_UCASE_in_builtInCall6317 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6321 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_REPLACE_in_builtInCall6333 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6337 = new BitSet(new long[]{0x6800C0FC00000600L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_expression_in_builtInCall6341 = new BitSet(new long[]{0x6800C0FC00000600L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_expression_in_builtInCall6345 = new BitSet(new long[]{0x6800C0FC00000608L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_expression_in_builtInCall6369 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LCASE_in_builtInCall6387 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6391 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ENCODE_FOR_URI_in_builtInCall6403 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6407 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CONTAINS_in_builtInCall6419 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6423 = new BitSet(new long[]{0x6800C0FC00000600L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_expression_in_builtInCall6427 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRSTARTS_in_builtInCall6439 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6443 = new BitSet(new long[]{0x6800C0FC00000600L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_expression_in_builtInCall6447 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRENDS_in_builtInCall6459 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6463 = new BitSet(new long[]{0x6800C0FC00000600L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_expression_in_builtInCall6467 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRBEFORE_in_builtInCall6479 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6483 = new BitSet(new long[]{0x6800C0FC00000600L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_expression_in_builtInCall6487 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRAFTER_in_builtInCall6499 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6503 = new BitSet(new long[]{0x6800C0FC00000600L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_expression_in_builtInCall6507 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_YEAR_in_builtInCall6519 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6523 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MONTH_in_builtInCall6535 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6539 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DAY_in_builtInCall6551 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6555 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_HOURS_in_builtInCall6567 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6571 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MINUTES_in_builtInCall6583 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6587 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SECONDS_in_builtInCall6599 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6603 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TIMEZONE_in_builtInCall6615 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6619 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TZ_in_builtInCall6631 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6635 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NOW_in_builtInCall6646 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UUID_in_builtInCall6658 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRUUID_in_builtInCall6670 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MD5_in_builtInCall6679 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6683 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SHA1_in_builtInCall6695 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6699 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SHA256_in_builtInCall6711 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6715 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SHA384_in_builtInCall6727 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6731 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SHA512_in_builtInCall6743 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6747 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_COALESCE_in_builtInCall6759 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expressionList_in_builtInCall6763 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IF_in_builtInCall6775 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6779 = new BitSet(new long[]{0x6800C0FC00000600L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_expression_in_builtInCall6783 = new BitSet(new long[]{0x6800C0FC00000600L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_expression_in_builtInCall6787 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRLANG_in_builtInCall6799 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6803 = new BitSet(new long[]{0x6800C0FC00000600L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_expression_in_builtInCall6807 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRDT_in_builtInCall6819 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6823 = new BitSet(new long[]{0x6800C0FC00000600L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_expression_in_builtInCall6827 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SAMETERM_in_builtInCall6839 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6843 = new BitSet(new long[]{0x6800C0FC00000600L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_expression_in_builtInCall6847 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ISIRI_in_builtInCall6859 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6863 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ISURI_in_builtInCall6875 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6879 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ISBLANK_in_builtInCall6891 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6895 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ISLITERAL_in_builtInCall6907 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6911 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ISNUMERIC_in_builtInCall6923 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6927 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_regexExpression_in_builtInCall6940 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_existsFunc_in_builtInCall6949 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_notExistsFunc_in_builtInCall6958 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_REGEX_in_regexExpression6981 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_regexExpression6985 = new BitSet(new long[]{0x6800C0FC00000600L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_expression_in_regexExpression6989 = new BitSet(new long[]{0x6800C0FC00000608L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_expression_in_regexExpression6993 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EXISTS_in_existsFunc7025 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_groupGraphPattern_in_existsFunc7029 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NOT_EXISTS_in_notExistsFunc7060 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_groupGraphPattern_in_notExistsFunc7064 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_COUNT_in_aggregate7099 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregate7111 = new BitSet(new long[]{0x6800C0FC00000600L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_expression_in_aggregate7168 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_251_in_aggregate7178 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SUM_in_aggregate7198 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregate7210 = new BitSet(new long[]{0x6800C0FC00000600L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_expression_in_aggregate7227 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MIN_in_aggregate7240 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregate7253 = new BitSet(new long[]{0x6800C0FC00000600L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_expression_in_aggregate7269 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MAX_in_aggregate7282 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregate7295 = new BitSet(new long[]{0x6800C0FC00000600L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_expression_in_aggregate7312 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_AVG_in_aggregate7325 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregate7338 = new BitSet(new long[]{0x6800C0FC00000600L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_expression_in_aggregate7355 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SAMPLE_in_aggregate7368 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregate7380 = new BitSet(new long[]{0x6800C0FC00000600L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_expression_in_aggregate7397 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_GROUP_CONCAT_in_aggregate7411 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregate7423 = new BitSet(new long[]{0x6800C0FC00000600L,0xF000000000000000L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_expression_in_aggregate7438 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x4000000000000000L});
    public static final BitSet FOLLOW_SEPARATOR_in_aggregate7449 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_string_in_aggregate7453 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FUNCTION_in_iRIFunction7482 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_iRIref_in_iRIFunction7492 = new BitSet(new long[]{0x6800C0FC00000E08L,0xF000000000000010L,0x3FFFFFDFFFFFFFFFL,0x4800000000000002L,0x00000000000000FEL});
    public static final BitSet FOLLOW_argList_in_iRIFunction7504 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_string_in_rDFLiteral7536 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x8000000000000000L,0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_LANGTAG_in_rDFLiteral7551 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_264_in_rDFLiteral7565 = new BitSet(new long[]{0x0000001800000000L,0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_iRIref_in_rDFLiteral7569 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numericLiteralUnsigned_in_numericLiteral7600 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numericLiteralPositive_in_numericLiteral7610 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numericLiteralNegative_in_numericLiteral7620 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BIG_INTEGER_in_numericLiteralUnsigned7642 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_INTEGER_in_numericLiteralUnsigned7647 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BIG_DECIMAL_in_numericLiteralUnsigned7663 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DECIMAL_in_numericLiteralUnsigned7667 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DOUBLE_in_numericLiteralUnsigned7682 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DOUBLE_in_numericLiteralUnsigned7687 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BIG_INTEGER_in_numericLiteralPositive7717 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_INTEGER_POSITIVE_in_numericLiteralPositive7722 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BIG_DECIMAL_in_numericLiteralPositive7735 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DECIMAL_POSITIVE_in_numericLiteralPositive7739 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DOUBLE_in_numericLiteralPositive7752 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DOUBLE_POSITIVE_in_numericLiteralPositive7757 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BIG_INTEGER_in_numericLiteralNegative7782 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_INTEGER_NEGATIVE_in_numericLiteralNegative7787 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BIG_DECIMAL_in_numericLiteralNegative7800 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DECIMAL_NEGATIVE_in_numericLiteralNegative7804 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DOUBLE_in_numericLiteralNegative7817 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DOUBLE_NEGATIVE_in_numericLiteralNegative7822 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BOOLEAN_in_booleanLiteral7846 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_TRUE_in_booleanLiteral7848 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BOOLEAN_in_booleanLiteral7863 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_FALSE_in_booleanLiteral7865 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_in_string7888 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_LITERAL1_in_string7892 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_in_string7909 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_LITERAL2_in_string7913 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_in_string7930 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_LITERAL_LONG1_in_string7934 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_in_string7946 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_LITERAL_LONG2_in_string7950 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IRI_in_iRIref7974 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IRI_REF_in_iRIref7978 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_prefixedName_in_iRIref7990 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PREFIXED_NAME_in_prefixedName8012 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_PNAME_LN_in_prefixedName8016 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PREFIXED_NS_in_prefixedName8025 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_PNAME_NS_in_prefixedName8029 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BLANK_NODE_LABEL_in_blankNode8053 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANNON_in_blankNode8064 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_OPEN_SQ_BRACKET_in_blankNode8068 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NIL_in_synpred1_IbmSparqlAstWalker3016 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NIL_in_synpred2_IbmSparqlAstWalker3038 = new BitSet(new long[]{0x0000000000000002L});

}