// $ANTLR 3.3 Nov 30, 2010 12:50:56 IbmSparqlExtAstWalker.g 2015-04-14 11:35:03
 
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
import org.antlr.runtime.tree.*;import java.util.Stack;
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
            match(input,FUNCTION,FOLLOW_FUNCTION_in_functionDecl592); if (state.failed) return func;

            match(input, Token.DOWN, null); if (state.failed) return func;
            // IbmSparqlExtAstWalker.g:147:4: (fn= VARNAME )
            // IbmSparqlExtAstWalker.g:147:5: fn= VARNAME
            {
            fn=(XTree)match(input,VARNAME,FOLLOW_VARNAME_in_functionDecl600); if (state.failed) return func;
            if ( state.backtracking==0 ) {
               func.setName(fn); 
            }

            }

            match(input,OPEN_BRACE,FOLLOW_OPEN_BRACE_in_functionDecl609); if (state.failed) return func;
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
            	    pushFollow(FOLLOW_var_in_functionDecl617);
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

            match(input,ARROW,FOLLOW_ARROW_in_functionDecl627); if (state.failed) return func;
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
            	    pushFollow(FOLLOW_var_in_functionDecl635);
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

            match(input,CLOSE_BRACE,FOLLOW_CLOSE_BRACE_in_functionDecl646); if (state.failed) return func;
            // IbmSparqlExtAstWalker.g:153:4: ( ^( FUNCLANG (fl= VARNAME ) (fb= functionBody ) ) )
            // IbmSparqlExtAstWalker.g:153:5: ^( FUNCLANG (fl= VARNAME ) (fb= functionBody ) )
            {
            match(input,FUNCLANG,FOLLOW_FUNCLANG_in_functionDecl653); if (state.failed) return func;

            match(input, Token.DOWN, null); if (state.failed) return func;
            // IbmSparqlExtAstWalker.g:154:5: (fl= VARNAME )
            // IbmSparqlExtAstWalker.g:154:6: fl= VARNAME
            {
            fl=(XTree)match(input,VARNAME,FOLLOW_VARNAME_in_functionDecl663); if (state.failed) return func;
            if ( state.backtracking==0 ) {
               func.setLang(fl); 
            }

            }

            // IbmSparqlExtAstWalker.g:155:5: (fb= functionBody )
            // IbmSparqlExtAstWalker.g:155:6: fb= functionBody
            {
            pushFollow(FOLLOW_functionBody_in_functionDecl676);
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
            match(input,FUNCBODY,FOLLOW_FUNCBODY_in_functionBody722); if (state.failed) return fb;

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
                    f=(XTree)match(input,STRING_LITERAL2,FOLLOW_STRING_LITERAL2_in_functionBody737); if (state.failed) return fb;
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
                    pushFollow(FOLLOW_groupGraphPattern_in_functionBody757);
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
            match(input,DATASET,FOLLOW_DATASET_in_dataset800); if (state.failed) return dcl;

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
            	    pushFollow(FOLLOW_datasetClause_in_dataset809);
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
            match(input,SUB_SELECT,FOLLOW_SUB_SELECT_in_subSelect843); if (state.failed) return sp;

            match(input, Token.DOWN, null); if (state.failed) return sp;
            // IbmSparqlExtAstWalker.g:187:4: (s= selectClause )
            // IbmSparqlExtAstWalker.g:187:5: s= selectClause
            {
            pushFollow(FOLLOW_selectClause_in_subSelect852);
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
                    pushFollow(FOLLOW_whereClause_in_subSelect868);
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
            pushFollow(FOLLOW_solutionModifier_in_subSelect884);
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
                    pushFollow(FOLLOW_inlineData_in_subSelect910);
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
                    match(input,TYPE,FOLLOW_TYPE_in_selectClause956); if (state.failed) return sc;

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
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_selectClause960); if (state.failed) return sc;
                            if ( state.backtracking==0 ) {
                               sc.setSelectModifier(SelectClause.ESelectModifier.DISTINCT); 
                            }

                            }
                            break;
                        case 2 :
                            // IbmSparqlExtAstWalker.g:198:7: REDUCED
                            {
                            match(input,REDUCED,FOLLOW_REDUCED_in_selectClause972); if (state.failed) return sc;
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

            match(input,PVARS,FOLLOW_PVARS_in_selectClause1001); if (state.failed) return sc;

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
                        	    pushFollow(FOLLOW_var_in_selectClause1009);
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
                        	    pushFollow(FOLLOW_expVar_in_selectClause1031);
                        	    expVar(sc);

                        	    state._fsp--;
                        	    if (state.failed) return sc;

                        	    }
                        	    break;
                        	case 3 :
                        	    // IbmSparqlExtAstWalker.g:204:15: fexp[$sc]
                        	    {
                        	    pushFollow(FOLLOW_fexp_in_selectClause1054);
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
                        match(input,256,FOLLOW_256_in_selectClause1094); if (state.failed) return sc;

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
            match(input,AS,FOLLOW_AS_in_expVar1125); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            pushFollow(FOLLOW_var_in_expVar1129);
            v=var();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_expression_in_expVar1133);
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
            match(input,EXP,FOLLOW_EXP_in_fexp1151); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            pushFollow(FOLLOW_expression_in_fexp1155);
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
            match(input,CONSTRUCT,FOLLOW_CONSTRUCT_in_constructQuery1186); if (state.failed) return cq;

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
                    pushFollow(FOLLOW_constructTemplate_in_constructQuery1193);
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
                    	    pushFollow(FOLLOW_dataset_in_constructQuery1207);
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
                    pushFollow(FOLLOW_whereClause_in_constructQuery1230);
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
                    pushFollow(FOLLOW_solutionModifier_in_constructQuery1249);
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
                    	    pushFollow(FOLLOW_dataset_in_constructQuery1289);
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
                            match(input,WHERE,FOLLOW_WHERE_in_constructQuery1311); if (state.failed) return cq;

                            if ( state.backtracking==0 ) {
                                    
                                                               p = new PatternSet();
                                                             
                            }

                            match(input, Token.DOWN, null); if (state.failed) return cq;
                            pushFollow(FOLLOW_triplesTemplate_in_constructQuery1377);
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
                    pushFollow(FOLLOW_solutionModifier_in_constructQuery1457);
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
            match(input,DESCRIBE,FOLLOW_DESCRIBE_in_describeQuery1515); if (state.failed) return dq;

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
                    	    pushFollow(FOLLOW_varOrIRIref2_in_describeQuery1524);
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
                    match(input,256,FOLLOW_256_in_describeQuery1535); if (state.failed) return dq;

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
            	    pushFollow(FOLLOW_datasetClause_in_describeQuery1550);
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
                    pushFollow(FOLLOW_whereClause_in_describeQuery1571);
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
            pushFollow(FOLLOW_solutionModifier_in_describeQuery1591);
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
            match(input,ASK,FOLLOW_ASK_in_askQuery1632); if (state.failed) return aq;

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
            	    pushFollow(FOLLOW_datasetClause_in_askQuery1640);
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
            pushFollow(FOLLOW_whereClause_in_askQuery1656);
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
            match(input,FROM,FOLLOW_FROM_in_datasetClause1691); if (state.failed) return dc;

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
                    pushFollow(FOLLOW_defaultGraphClause_in_datasetClause1698);
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
                    pushFollow(FOLLOW_namedGraphClause_in_datasetClause1712);
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
            pushFollow(FOLLOW_sourceSelector_in_defaultGraphClause1745);
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
            match(input,NAMED,FOLLOW_NAMED_in_namedGraphClause1767); if (state.failed) return dc;

            match(input, Token.DOWN, null); if (state.failed) return dc;
            pushFollow(FOLLOW_sourceSelector_in_namedGraphClause1771);
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
            pushFollow(FOLLOW_iRIref_in_sourceSelector1797);
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
            match(input,WHERE,FOLLOW_WHERE_in_whereClause1820); if (state.failed) return p;

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
                        pushFollow(FOLLOW_groupGraphPattern_in_whereClause1824);
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
            match(input,MODIFIERS,FOLLOW_MODIFIERS_in_solutionModifier1855); if (state.failed) return m;

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
                        pushFollow(FOLLOW_groupClause_in_solutionModifier1864);
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
                        pushFollow(FOLLOW_havingClause_in_solutionModifier1881);
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
                        pushFollow(FOLLOW_orderClause_in_solutionModifier1898);
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
                        pushFollow(FOLLOW_limitOffsetClauses_in_solutionModifier1915);
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
            match(input,GROUP_BY,FOLLOW_GROUP_BY_in_groupClause1941); if (state.failed) return gc;

            match(input, Token.DOWN, null); if (state.failed) return gc;
            pushFollow(FOLLOW_groupCondition_in_groupClause1945);
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
            	    pushFollow(FOLLOW_builtInCall_in_groupCondition1978);
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
            	    pushFollow(FOLLOW_functionCall_in_groupCondition1997);
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
            	    match(input,CONDITION,FOLLOW_CONDITION_in_groupCondition2015); if (state.failed) return gc;

            	    match(input, Token.DOWN, null); if (state.failed) return gc;
            	    pushFollow(FOLLOW_expression_in_groupCondition2019);
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
            	            pushFollow(FOLLOW_var_in_groupCondition2024);
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
            	    pushFollow(FOLLOW_var_in_groupCondition2041);
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
            match(input,HAVING,FOLLOW_HAVING_in_havingClause2115); if (state.failed) return h;

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
            	    pushFollow(FOLLOW_havingCondition_in_havingClause2120);
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
            pushFollow(FOLLOW_constraint_in_havingCondition2148);
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
            match(input,ORDER_BY,FOLLOW_ORDER_BY_in_orderClause2177); if (state.failed) return loc;

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
            	    pushFollow(FOLLOW_orderCondition_in_orderClause2184);
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
                    match(input,ASC,FOLLOW_ASC_in_orderCondition2211); if (state.failed) return oc;

                    match(input, Token.DOWN, null); if (state.failed) return oc;
                    pushFollow(FOLLOW_brackettedExpression_in_orderCondition2215);
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
                    match(input,DESC,FOLLOW_DESC_in_orderCondition2227); if (state.failed) return oc;

                    match(input, Token.DOWN, null); if (state.failed) return oc;
                    pushFollow(FOLLOW_brackettedExpression_in_orderCondition2231);
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
                            pushFollow(FOLLOW_constraint_in_orderCondition2245);
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
                            pushFollow(FOLLOW_var_in_orderCondition2261);
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
                    pushFollow(FOLLOW_limitClause_in_limitOffsetClauses2301);
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
                            pushFollow(FOLLOW_offsetClause_in_limitOffsetClauses2309);
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
                    pushFollow(FOLLOW_offsetClause_in_limitOffsetClauses2322);
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
                            pushFollow(FOLLOW_limitClause_in_limitOffsetClauses2329);
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
            match(input,LIMIT,FOLLOW_LIMIT_in_limitClause2367); if (state.failed) return x;

            match(input, Token.DOWN, null); if (state.failed) return x;
            i=(XTree)match(input,INTEGER,FOLLOW_INTEGER_in_limitClause2372); if (state.failed) return x;

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
            match(input,OFFSET,FOLLOW_OFFSET_in_offsetClause2399); if (state.failed) return x;

            match(input, Token.DOWN, null); if (state.failed) return x;
            i=(XTree)match(input,INTEGER,FOLLOW_INTEGER_in_offsetClause2404); if (state.failed) return x;

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
            pushFollow(FOLLOW_inlineData_in_bindingsClause2428);
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
                    pushFollow(FOLLOW_iRIref_in_bindingValue2449);
                    iRIref();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:371:17: rDFLiteral
                    {
                    pushFollow(FOLLOW_rDFLiteral_in_bindingValue2453);
                    rDFLiteral();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // IbmSparqlExtAstWalker.g:371:30: numericLiteral
                    {
                    pushFollow(FOLLOW_numericLiteral_in_bindingValue2457);
                    numericLiteral();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // IbmSparqlExtAstWalker.g:371:47: booleanLiteral
                    {
                    pushFollow(FOLLOW_booleanLiteral_in_bindingValue2461);
                    booleanLiteral();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // IbmSparqlExtAstWalker.g:371:64: UNDEF
                    {
                    match(input,UNDEF,FOLLOW_UNDEF_in_bindingValue2465); if (state.failed) return ;

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
            pushFollow(FOLLOW_triplesSameSubject_in_triplesTemplate2485);
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
                    match(input,DOT,FOLLOW_DOT_in_triplesTemplate2492); if (state.failed) return ;
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
                            pushFollow(FOLLOW_triplesTemplate_in_triplesTemplate2494);
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
                    match(input,GROUP_GRAPH_PATTERN,FOLLOW_GROUP_GRAPH_PATTERN_in_groupGraphPattern2524); if (state.failed) return r;

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
                                pushFollow(FOLLOW_groupGraphPatternSub_in_groupGraphPattern2526);
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
                    pushFollow(FOLLOW_subSelect_in_groupGraphPattern2538);
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
            	    pushFollow(FOLLOW_triplesBlock_in_groupGraphPatternSub2570);
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
            	    pushFollow(FOLLOW_filter_in_groupGraphPatternSub2602);
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
            	    pushFollow(FOLLOW_graphPatternNewBGP_in_groupGraphPatternSub2640);
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
            match(input,TRIPLES_BLOCK,FOLLOW_TRIPLES_BLOCK_in_triplesBlock2699); if (state.failed) return sp;

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
            	    pushFollow(FOLLOW_triples_in_triplesBlock2732);
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
            	    pushFollow(FOLLOW_triples2_in_triplesBlock2748);
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
            match(input,TRIPLE,FOLLOW_TRIPLE_in_triples2796); if (state.failed) return qt;

            match(input, Token.DOWN, null); if (state.failed) return qt;
            match(input,SUBJECT,FOLLOW_SUBJECT_in_triples2799); if (state.failed) return qt;

            match(input, Token.DOWN, null); if (state.failed) return qt;
            // IbmSparqlExtAstWalker.g:428:27: (s1= graphNode )
            // IbmSparqlExtAstWalker.g:428:29: s1= graphNode
            {
            pushFollow(FOLLOW_graphNode_in_triples2805);
            s1=graphNode();

            state._fsp--;
            if (state.failed) return qt;
            if ( state.backtracking==0 ) {
               s = new QueryTripleTerm(s1);  
            }

            }


            match(input, Token.UP, null); if (state.failed) return qt;
            match(input,PREDICATE,FOLLOW_PREDICATE_in_triples2841); if (state.failed) return qt;

            match(input, Token.DOWN, null); if (state.failed) return qt;
            // IbmSparqlExtAstWalker.g:431:18: (v1= predicate )
            // IbmSparqlExtAstWalker.g:431:20: v1= predicate
            {
            pushFollow(FOLLOW_predicate_in_triples2847);
            v1=predicate();

            state._fsp--;
            if (state.failed) return qt;
            if ( state.backtracking==0 ) {
               p = new PropertyTerm(v1);  
            }

            }


            match(input, Token.UP, null); if (state.failed) return qt;
            match(input,VALUE,FOLLOW_VALUE_in_triples2887); if (state.failed) return qt;

            match(input, Token.DOWN, null); if (state.failed) return qt;
            pushFollow(FOLLOW_object_in_triples2892);
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
            match(input,TRIPLE2,FOLLOW_TRIPLE2_in_triples22947); if (state.failed) return qt;

            match(input, Token.DOWN, null); if (state.failed) return qt;
            match(input,SUBJECT,FOLLOW_SUBJECT_in_triples22950); if (state.failed) return qt;

            match(input, Token.DOWN, null); if (state.failed) return qt;
            // IbmSparqlExtAstWalker.g:444:26: (s= triplesNode )
            // IbmSparqlExtAstWalker.g:444:28: s= triplesNode
            {
            pushFollow(FOLLOW_triplesNode_in_triples22956);
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
            	    match(input,PROPERTY_LIST,FOLLOW_PROPERTY_LIST_in_triples22994); if (state.failed) return qt;

            	    match(input, Token.DOWN, null); if (state.failed) return qt;
            	    match(input,PREDICATE,FOLLOW_PREDICATE_in_triples23006); if (state.failed) return qt;

            	    match(input, Token.DOWN, null); if (state.failed) return qt;
            	    // IbmSparqlExtAstWalker.g:448:22: (p= predicate )
            	    // IbmSparqlExtAstWalker.g:448:24: p= predicate
            	    {
            	    pushFollow(FOLLOW_predicate_in_triples23013);
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
            	    	    match(input,VALUE,FOLLOW_VALUE_in_triples23063); if (state.failed) return qt;

            	    	    match(input, Token.DOWN, null); if (state.failed) return qt;
            	    	    pushFollow(FOLLOW_object_in_triples23068);
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
                    pushFollow(FOLLOW_groupMinusOrUnionGraphPattern_in_graphPatternNewBGP3128);
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
                    pushFollow(FOLLOW_optionalGraphPattern_in_graphPatternNewBGP3148);
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
                    pushFollow(FOLLOW_graphGraphPattern_in_graphPatternNewBGP3169);
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
                    pushFollow(FOLLOW_serviceGraphPattern_in_graphPatternNewBGP3188);
                    serviceGraphPattern();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // IbmSparqlExtAstWalker.g:474:5: b= bind
                    {
                    pushFollow(FOLLOW_bind_in_graphPatternNewBGP3197);
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
                    pushFollow(FOLLOW_inlineData_in_graphPatternNewBGP3217);
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
            match(input,VALUES,FOLLOW_VALUES_in_inlineData3250); if (state.failed) return v;

            match(input, Token.DOWN, null); if (state.failed) return v;
            pushFollow(FOLLOW_dataBlock_in_inlineData3254);
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
            match(input,INLINE_DATA,FOLLOW_INLINE_DATA_in_dataBlock3286); if (state.failed) return values;

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
                        match(input,NIL,FOLLOW_NIL_in_dataBlock3296); if (state.failed) return values;

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
                        	    pushFollow(FOLLOW_var_in_dataBlock3303);
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
                        match(input,NIL,FOLLOW_NIL_in_dataBlock3318); if (state.failed) return values;

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
                        	    pushFollow(FOLLOW_dataBlockValue_in_dataBlock3325);
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
                    pushFollow(FOLLOW_iRIref_in_dataBlockValue3361);
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
                    pushFollow(FOLLOW_rDFLiteral_in_dataBlockValue3377);
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
                    pushFollow(FOLLOW_numericLiteral_in_dataBlockValue3392);
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
                    pushFollow(FOLLOW_booleanLiteral_in_dataBlockValue3407);
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
                    u=(XTree)match(input,UNDEF,FOLLOW_UNDEF_in_dataBlockValue3423); if (state.failed) return e;
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
            match(input,OPTIONAL,FOLLOW_OPTIONAL_in_optionalGraphPattern3453); if (state.failed) return p;

            match(input, Token.DOWN, null); if (state.failed) return p;
            pushFollow(FOLLOW_groupGraphPattern_in_optionalGraphPattern3457);
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
            match(input,GRAPH,FOLLOW_GRAPH_in_graphGraphPattern3487); if (state.failed) return p;

            match(input, Token.DOWN, null); if (state.failed) return p;
            pushFollow(FOLLOW_varOrIRIref2_in_graphGraphPattern3491);
            r=varOrIRIref2();

            state._fsp--;
            if (state.failed) return p;
            pushFollow(FOLLOW_groupGraphPattern_in_graphGraphPattern3495);
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
            top=(XTree)match(input,SERVICE,FOLLOW_SERVICE_in_serviceGraphPattern3531); if (state.failed) return p;

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
                    match(input,SILENT,FOLLOW_SILENT_in_serviceGraphPattern3534); if (state.failed) return p;
                    if ( state.backtracking==0 ) {
                       silent=true; 
                    }

                    }
                    break;

            }

            pushFollow(FOLLOW_varOrIRIref_in_serviceGraphPattern3542);
            s=varOrIRIref();

            state._fsp--;
            if (state.failed) return p;
            pushFollow(FOLLOW_groupGraphPattern_in_serviceGraphPattern3546);
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
    // IbmSparqlExtAstWalker.g:530:1: bind returns [Pattern p] : ^( BIND v= var e= expression ) ;
    public final Pattern bind() throws RecognitionException {
        Pattern p = null;

        String v = null;

        Expression e = null;


        try {
            // IbmSparqlExtAstWalker.g:531:2: ( ^( BIND v= var e= expression ) )
            // IbmSparqlExtAstWalker.g:531:7: ^( BIND v= var e= expression )
            {
            match(input,BIND,FOLLOW_BIND_in_bind3579); if (state.failed) return p;

            match(input, Token.DOWN, null); if (state.failed) return p;
            pushFollow(FOLLOW_var_in_bind3583);
            v=var();

            state._fsp--;
            if (state.failed) return p;
            pushFollow(FOLLOW_expression_in_bind3589);
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
    // IbmSparqlExtAstWalker.g:534:1: groupMinusOrUnionGraphPattern returns [Pattern r] : ( ^( UNION g1= groupGraphPattern[false] (g2= groupGraphPattern[false] )+ ) | ^( MINUS g1= groupGraphPattern[false] ) | g3= groupGraphPattern[false] );
    public final Pattern groupMinusOrUnionGraphPattern() throws RecognitionException {
        Pattern r = null;

        Pattern g1 = null;

        Pattern g2 = null;

        Pattern g3 = null;



        	  PatternSet p = null;
        	
        try {
            // IbmSparqlExtAstWalker.g:538:2: ( ^( UNION g1= groupGraphPattern[false] (g2= groupGraphPattern[false] )+ ) | ^( MINUS g1= groupGraphPattern[false] ) | g3= groupGraphPattern[false] )
            int alt60=3;
            switch ( input.LA(1) ) {
            case UNION:
                {
                alt60=1;
                }
                break;
            case MINUS:
                {
                alt60=2;
                }
                break;
            case GROUP_GRAPH_PATTERN:
            case SUB_SELECT:
                {
                alt60=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return r;}
                NoViableAltException nvae =
                    new NoViableAltException("", 60, 0, input);

                throw nvae;
            }

            switch (alt60) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:538:6: ^( UNION g1= groupGraphPattern[false] (g2= groupGraphPattern[false] )+ )
                    {
                    match(input,UNION,FOLLOW_UNION_in_groupMinusOrUnionGraphPattern3621); if (state.failed) return r;

                    match(input, Token.DOWN, null); if (state.failed) return r;
                    pushFollow(FOLLOW_groupGraphPattern_in_groupMinusOrUnionGraphPattern3642);
                    g1=groupGraphPattern(false);

                    state._fsp--;
                    if (state.failed) return r;
                    if ( state.backtracking==0 ) {
                       p = new PatternSet(PatternSet.EPatternSetType.UNION); 
                      			      r = p;
                                        p.addPattern(g1);	
                    }
                    // IbmSparqlExtAstWalker.g:543:5: (g2= groupGraphPattern[false] )+
                    int cnt59=0;
                    loop59:
                    do {
                        int alt59=2;
                        int LA59_0 = input.LA(1);

                        if ( (LA59_0==GROUP_GRAPH_PATTERN||LA59_0==SUB_SELECT) ) {
                            alt59=1;
                        }


                        switch (alt59) {
                    	case 1 :
                    	    // IbmSparqlExtAstWalker.g:543:6: g2= groupGraphPattern[false]
                    	    {
                    	    pushFollow(FOLLOW_groupGraphPattern_in_groupMinusOrUnionGraphPattern3673);
                    	    g2=groupGraphPattern(false);

                    	    state._fsp--;
                    	    if (state.failed) return r;
                    	    if ( state.backtracking==0 ) {
                    	       
                    	                         p.addPattern(g2); 
                    	                       
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt59 >= 1 ) break loop59;
                    	    if (state.backtracking>0) {state.failed=true; return r;}
                                EarlyExitException eee =
                                    new EarlyExitException(59, input);
                                throw eee;
                        }
                        cnt59++;
                    } while (true);


                    match(input, Token.UP, null); if (state.failed) return r;

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:547:9: ^( MINUS g1= groupGraphPattern[false] )
                    {
                    match(input,MINUS,FOLLOW_MINUS_in_groupMinusOrUnionGraphPattern3709); if (state.failed) return r;

                    match(input, Token.DOWN, null); if (state.failed) return r;
                    pushFollow(FOLLOW_groupGraphPattern_in_groupMinusOrUnionGraphPattern3724);
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
                    // IbmSparqlExtAstWalker.g:552:7: g3= groupGraphPattern[false]
                    {
                    pushFollow(FOLLOW_groupGraphPattern_in_groupMinusOrUnionGraphPattern3751);
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
    // IbmSparqlExtAstWalker.g:558:1: filter returns [Expression e] : ^( FILTER c= constraint ) ;
    public final Expression filter() throws RecognitionException {
        Expression e = null;

        Expression c = null;


        try {
            // IbmSparqlExtAstWalker.g:559:2: ( ^( FILTER c= constraint ) )
            // IbmSparqlExtAstWalker.g:559:6: ^( FILTER c= constraint )
            {
            match(input,FILTER,FOLLOW_FILTER_in_filter3784); if (state.failed) return e;

            match(input, Token.DOWN, null); if (state.failed) return e;
            pushFollow(FOLLOW_constraint_in_filter3788);
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
    // IbmSparqlExtAstWalker.g:562:1: constraint returns [Expression e] : (be= brackettedExpression | bc= builtInCall | fc= functionCall );
    public final Expression constraint() throws RecognitionException {
        Expression e = null;

        Expression be = null;

        Expression bc = null;

        FunctionCall fc = null;


        try {
            // IbmSparqlExtAstWalker.g:563:2: (be= brackettedExpression | bc= builtInCall | fc= functionCall )
            int alt61=3;
            switch ( input.LA(1) ) {
            case EXPRESSION:
                {
                alt61=1;
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
                alt61=2;
                }
                break;
            case FUNCTION:
                {
                alt61=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return e;}
                NoViableAltException nvae =
                    new NoViableAltException("", 61, 0, input);

                throw nvae;
            }

            switch (alt61) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:563:6: be= brackettedExpression
                    {
                    pushFollow(FOLLOW_brackettedExpression_in_constraint3812);
                    be=brackettedExpression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = be; 
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:564:5: bc= builtInCall
                    {
                    pushFollow(FOLLOW_builtInCall_in_constraint3822);
                    bc=builtInCall();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = bc; 
                    }

                    }
                    break;
                case 3 :
                    // IbmSparqlExtAstWalker.g:565:5: fc= functionCall
                    {
                    pushFollow(FOLLOW_functionCall_in_constraint3835);
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
    // IbmSparqlExtAstWalker.g:568:1: functionCall returns [FunctionCall fc] : ^( FUNCTION i= iRIref a= argList ) ;
    public final FunctionCall functionCall() throws RecognitionException {
        FunctionCall fc = null;

        IRI i = null;

        List<Expression> a = null;


        try {
            // IbmSparqlExtAstWalker.g:569:2: ( ^( FUNCTION i= iRIref a= argList ) )
            // IbmSparqlExtAstWalker.g:569:6: ^( FUNCTION i= iRIref a= argList )
            {
            match(input,FUNCTION,FOLLOW_FUNCTION_in_functionCall3857); if (state.failed) return fc;

            match(input, Token.DOWN, null); if (state.failed) return fc;
            pushFollow(FOLLOW_iRIref_in_functionCall3861);
            i=iRIref();

            state._fsp--;
            if (state.failed) return fc;
            pushFollow(FOLLOW_argList_in_functionCall3865);
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
    // IbmSparqlExtAstWalker.g:572:1: argList returns [List<Expression> args] : ( NIL | DISTINCT (e1= expression )+ | (e2= expression )+ );
    public final List<Expression> argList() throws RecognitionException {
        List<Expression> args = null;

        Expression e1 = null;

        Expression e2 = null;


         args = new ArrayList<Expression>(); 
        try {
            // IbmSparqlExtAstWalker.g:574:5: ( NIL | DISTINCT (e1= expression )+ | (e2= expression )+ )
            int alt64=3;
            switch ( input.LA(1) ) {
            case NIL:
                {
                alt64=1;
                }
                break;
            case DISTINCT:
                {
                alt64=2;
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
                alt64=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return args;}
                NoViableAltException nvae =
                    new NoViableAltException("", 64, 0, input);

                throw nvae;
            }

            switch (alt64) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:574:9: NIL
                    {
                    match(input,NIL,FOLLOW_NIL_in_argList3894); if (state.failed) return args;

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:575:6: DISTINCT (e1= expression )+
                    {
                    match(input,DISTINCT,FOLLOW_DISTINCT_in_argList3907); if (state.failed) return args;
                    // IbmSparqlExtAstWalker.g:575:15: (e1= expression )+
                    int cnt62=0;
                    loop62:
                    do {
                        int alt62=2;
                        int LA62_0 = input.LA(1);

                        if ( ((LA62_0>=BROKEN_PLUS && LA62_0<=BROKEN_MINUS)||(LA62_0>=VAR && LA62_0<=NOT_EXISTS)||(LA62_0>=STRING && LA62_0<=BOOLEAN)||LA62_0==LTE||(LA62_0>=BIG_INTEGER && LA62_0<=BIG_DECIMAL)||(LA62_0>=LOGICAL_OR && LA62_0<=SHA1)||(LA62_0>=SHA256 && LA62_0<=GROUP_CONCAT)||LA62_0==DOUBLE||LA62_0==256||LA62_0==259||(LA62_0>=262 && LA62_0<=268)) ) {
                            alt62=1;
                        }


                        switch (alt62) {
                    	case 1 :
                    	    // IbmSparqlExtAstWalker.g:575:16: e1= expression
                    	    {
                    	    pushFollow(FOLLOW_expression_in_argList3912);
                    	    e1=expression();

                    	    state._fsp--;
                    	    if (state.failed) return args;
                    	    if ( state.backtracking==0 ) {
                    	       args.add(e1); 
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt62 >= 1 ) break loop62;
                    	    if (state.backtracking>0) {state.failed=true; return args;}
                                EarlyExitException eee =
                                    new EarlyExitException(62, input);
                                throw eee;
                        }
                        cnt62++;
                    } while (true);


                    }
                    break;
                case 3 :
                    // IbmSparqlExtAstWalker.g:576:4: (e2= expression )+
                    {
                    // IbmSparqlExtAstWalker.g:576:4: (e2= expression )+
                    int cnt63=0;
                    loop63:
                    do {
                        int alt63=2;
                        int LA63_0 = input.LA(1);

                        if ( ((LA63_0>=BROKEN_PLUS && LA63_0<=BROKEN_MINUS)||(LA63_0>=VAR && LA63_0<=NOT_EXISTS)||(LA63_0>=STRING && LA63_0<=BOOLEAN)||LA63_0==LTE||(LA63_0>=BIG_INTEGER && LA63_0<=BIG_DECIMAL)||(LA63_0>=LOGICAL_OR && LA63_0<=SHA1)||(LA63_0>=SHA256 && LA63_0<=GROUP_CONCAT)||LA63_0==DOUBLE||LA63_0==256||LA63_0==259||(LA63_0>=262 && LA63_0<=268)) ) {
                            alt63=1;
                        }


                        switch (alt63) {
                    	case 1 :
                    	    // IbmSparqlExtAstWalker.g:576:5: e2= expression
                    	    {
                    	    pushFollow(FOLLOW_expression_in_argList3925);
                    	    e2=expression();

                    	    state._fsp--;
                    	    if (state.failed) return args;
                    	    if ( state.backtracking==0 ) {
                    	       args.add(e2); 
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt63 >= 1 ) break loop63;
                    	    if (state.backtracking>0) {state.failed=true; return args;}
                                EarlyExitException eee =
                                    new EarlyExitException(63, input);
                                throw eee;
                        }
                        cnt63++;
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
    // IbmSparqlExtAstWalker.g:579:1: expressionList returns [List<Expression> exprs] : ( NIL | (e= expression )+ );
    public final List<Expression> expressionList() throws RecognitionException {
        List<Expression> exprs = null;

        Expression e = null;


         exprs = new ArrayList<Expression>(); 
        try {
            // IbmSparqlExtAstWalker.g:581:2: ( NIL | (e= expression )+ )
            int alt66=2;
            int LA66_0 = input.LA(1);

            if ( (LA66_0==NIL) ) {
                alt66=1;
            }
            else if ( ((LA66_0>=BROKEN_PLUS && LA66_0<=BROKEN_MINUS)||(LA66_0>=VAR && LA66_0<=NOT_EXISTS)||(LA66_0>=STRING && LA66_0<=BOOLEAN)||LA66_0==LTE||(LA66_0>=BIG_INTEGER && LA66_0<=BIG_DECIMAL)||(LA66_0>=LOGICAL_OR && LA66_0<=SHA1)||(LA66_0>=SHA256 && LA66_0<=GROUP_CONCAT)||LA66_0==DOUBLE||LA66_0==256||LA66_0==259||(LA66_0>=262 && LA66_0<=268)) ) {
                alt66=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return exprs;}
                NoViableAltException nvae =
                    new NoViableAltException("", 66, 0, input);

                throw nvae;
            }
            switch (alt66) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:581:6: NIL
                    {
                    match(input,NIL,FOLLOW_NIL_in_expressionList3960); if (state.failed) return exprs;

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:582:6: (e= expression )+
                    {
                    // IbmSparqlExtAstWalker.g:582:6: (e= expression )+
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
                    	    // IbmSparqlExtAstWalker.g:582:8: e= expression
                    	    {
                    	    pushFollow(FOLLOW_expression_in_expressionList3983);
                    	    e=expression();

                    	    state._fsp--;
                    	    if (state.failed) return exprs;
                    	    if ( state.backtracking==0 ) {
                    	       exprs.add(e); 
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt65 >= 1 ) break loop65;
                    	    if (state.backtracking>0) {state.failed=true; return exprs;}
                                EarlyExitException eee =
                                    new EarlyExitException(65, input);
                                throw eee;
                        }
                        cnt65++;
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
    // IbmSparqlExtAstWalker.g:586:1: constructTemplate[ConstructQuery cq] : ( constructTriples[$cq] )? ;
    public final void constructTemplate(ConstructQuery cq) throws RecognitionException {
        try {
            // IbmSparqlExtAstWalker.g:587:2: ( ( constructTriples[$cq] )? )
            // IbmSparqlExtAstWalker.g:587:6: ( constructTriples[$cq] )?
            {
            // IbmSparqlExtAstWalker.g:587:6: ( constructTriples[$cq] )?
            int alt67=2;
            int LA67_0 = input.LA(1);

            if ( ((LA67_0>=TRIPLE && LA67_0<=TRIPLE2)) ) {
                alt67=1;
            }
            switch (alt67) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:587:6: constructTriples[$cq]
                    {
                    pushFollow(FOLLOW_constructTriples_in_constructTemplate4003);
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
    // IbmSparqlExtAstWalker.g:590:1: constructTriples[ConstructQuery cq] : (t= triples | t2= triples2 )+ ;
    public final void constructTriples(ConstructQuery cq) throws RecognitionException {
        QueryTriple t = null;

        QueryTriple2 t2 = null;


        try {
            // IbmSparqlExtAstWalker.g:591:2: ( (t= triples | t2= triples2 )+ )
            // IbmSparqlExtAstWalker.g:591:6: (t= triples | t2= triples2 )+
            {
            // IbmSparqlExtAstWalker.g:591:6: (t= triples | t2= triples2 )+
            int cnt68=0;
            loop68:
            do {
                int alt68=3;
                int LA68_0 = input.LA(1);

                if ( (LA68_0==TRIPLE) ) {
                    alt68=1;
                }
                else if ( (LA68_0==TRIPLE2) ) {
                    alt68=2;
                }


                switch (alt68) {
            	case 1 :
            	    // IbmSparqlExtAstWalker.g:591:8: t= triples
            	    {
            	    pushFollow(FOLLOW_triples_in_constructTriples4024);
            	    t=triples();

            	    state._fsp--;
            	    if (state.failed) return ;
            	    if ( state.backtracking==0 ) {
            	      cq.addConstructPattern(t);
            	    }

            	    }
            	    break;
            	case 2 :
            	    // IbmSparqlExtAstWalker.g:592:5: t2= triples2
            	    {
            	    pushFollow(FOLLOW_triples2_in_constructTriples4035);
            	    t2=triples2();

            	    state._fsp--;
            	    if (state.failed) return ;
            	    if ( state.backtracking==0 ) {
            	      cq.addConstructPattern(t2);
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt68 >= 1 ) break loop68;
            	    if (state.backtracking>0) {state.failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(68, input);
                        throw eee;
                }
                cnt68++;
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
    // IbmSparqlExtAstWalker.g:596:1: triplesSameSubject[ConstructQuery cq] returns [SimplePattern sp] : ( (t1= triples )+ | t12= triples2 );
    public final SimplePattern triplesSameSubject(ConstructQuery cq) throws RecognitionException {
        SimplePattern sp = null;

        QueryTriple t1 = null;

        QueryTriple2 t12 = null;


         sp = new SimplePattern(); 
        try {
            // IbmSparqlExtAstWalker.g:598:2: ( (t1= triples )+ | t12= triples2 )
            int alt70=2;
            int LA70_0 = input.LA(1);

            if ( (LA70_0==TRIPLE) ) {
                alt70=1;
            }
            else if ( (LA70_0==TRIPLE2) ) {
                alt70=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return sp;}
                NoViableAltException nvae =
                    new NoViableAltException("", 70, 0, input);

                throw nvae;
            }
            switch (alt70) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:598:4: (t1= triples )+
                    {
                    // IbmSparqlExtAstWalker.g:598:4: (t1= triples )+
                    int cnt69=0;
                    loop69:
                    do {
                        int alt69=2;
                        int LA69_0 = input.LA(1);

                        if ( (LA69_0==TRIPLE) ) {
                            alt69=1;
                        }


                        switch (alt69) {
                    	case 1 :
                    	    // IbmSparqlExtAstWalker.g:598:5: t1= triples
                    	    {
                    	    pushFollow(FOLLOW_triples_in_triplesSameSubject4072);
                    	    t1=triples();

                    	    state._fsp--;
                    	    if (state.failed) return sp;
                    	    if ( state.backtracking==0 ) {
                    	       cq.addConstructPattern(t1); t1.expandAndAddTo(sp); 
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt69 >= 1 ) break loop69;
                    	    if (state.backtracking>0) {state.failed=true; return sp;}
                                EarlyExitException eee =
                                    new EarlyExitException(69, input);
                                throw eee;
                        }
                        cnt69++;
                    } while (true);


                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:599:4: t12= triples2
                    {
                    pushFollow(FOLLOW_triples2_in_triplesSameSubject4084);
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
    // IbmSparqlExtAstWalker.g:602:1: object returns [GraphNode r] : g= graphNode ;
    public final GraphNode object() throws RecognitionException {
        GraphNode r = null;

        GraphNode g = null;


        try {
            // IbmSparqlExtAstWalker.g:603:2: (g= graphNode )
            // IbmSparqlExtAstWalker.g:603:6: g= graphNode
            {
            pushFollow(FOLLOW_graphNode_in_object4106);
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
    // IbmSparqlExtAstWalker.g:606:1: verb returns [QueryTripleTerm t] : (v= varOrIRIref | 'a' );
    public final QueryTripleTerm verb() throws RecognitionException {
        QueryTripleTerm t = null;

        QueryTripleTerm v = null;


        try {
            // IbmSparqlExtAstWalker.g:607:2: (v= varOrIRIref | 'a' )
            int alt71=2;
            int LA71_0 = input.LA(1);

            if ( ((LA71_0>=VAR && LA71_0<=PREFIXED_NS)||LA71_0==IRI) ) {
                alt71=1;
            }
            else if ( (LA71_0==257) ) {
                alt71=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return t;}
                NoViableAltException nvae =
                    new NoViableAltException("", 71, 0, input);

                throw nvae;
            }
            switch (alt71) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:607:6: v= varOrIRIref
                    {
                    pushFollow(FOLLOW_varOrIRIref_in_verb4128);
                    v=varOrIRIref();

                    state._fsp--;
                    if (state.failed) return t;
                    if ( state.backtracking==0 ) {
                       t = v; 
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:608:5: 'a'
                    {
                    match(input,257,FOLLOW_257_in_verb4138); if (state.failed) return t;
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
    // IbmSparqlExtAstWalker.g:611:1: verbPath : path ;
    public final void verbPath() throws RecognitionException {
        try {
            // IbmSparqlExtAstWalker.g:612:2: ( path )
            // IbmSparqlExtAstWalker.g:612:6: path
            {
            pushFollow(FOLLOW_path_in_verbPath4156);
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
    // IbmSparqlExtAstWalker.g:615:1: verbSimple returns [Variable v] : x= var ;
    public final Variable verbSimple() throws RecognitionException {
        Variable v = null;

        String x = null;


        try {
            // IbmSparqlExtAstWalker.g:616:2: (x= var )
            // IbmSparqlExtAstWalker.g:616:6: x= var
            {
            pushFollow(FOLLOW_var_in_verbSimple4175);
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
    // IbmSparqlExtAstWalker.g:619:1: path : pathAlternative ;
    public final void path() throws RecognitionException {
        try {
            // IbmSparqlExtAstWalker.g:620:2: ( pathAlternative )
            // IbmSparqlExtAstWalker.g:620:6: pathAlternative
            {
            pushFollow(FOLLOW_pathAlternative_in_path4192);
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
    // IbmSparqlExtAstWalker.g:623:1: pathAlternative : pathSequence ( '|' pathSequence )* ;
    public final void pathAlternative() throws RecognitionException {
        try {
            // IbmSparqlExtAstWalker.g:624:2: ( pathSequence ( '|' pathSequence )* )
            // IbmSparqlExtAstWalker.g:624:6: pathSequence ( '|' pathSequence )*
            {
            pushFollow(FOLLOW_pathSequence_in_pathAlternative4205);
            pathSequence();

            state._fsp--;
            if (state.failed) return ;
            // IbmSparqlExtAstWalker.g:624:19: ( '|' pathSequence )*
            loop72:
            do {
                int alt72=2;
                int LA72_0 = input.LA(1);

                if ( (LA72_0==258) ) {
                    alt72=1;
                }


                switch (alt72) {
            	case 1 :
            	    // IbmSparqlExtAstWalker.g:624:21: '|' pathSequence
            	    {
            	    match(input,258,FOLLOW_258_in_pathAlternative4209); if (state.failed) return ;
            	    pushFollow(FOLLOW_pathSequence_in_pathAlternative4211);
            	    pathSequence();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop72;
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
    // IbmSparqlExtAstWalker.g:627:1: pathSequence : pathEltOrInverse ( '/' pathEltOrInverse )* ;
    public final void pathSequence() throws RecognitionException {
        try {
            // IbmSparqlExtAstWalker.g:628:2: ( pathEltOrInverse ( '/' pathEltOrInverse )* )
            // IbmSparqlExtAstWalker.g:628:7: pathEltOrInverse ( '/' pathEltOrInverse )*
            {
            pushFollow(FOLLOW_pathEltOrInverse_in_pathSequence4228);
            pathEltOrInverse();

            state._fsp--;
            if (state.failed) return ;
            // IbmSparqlExtAstWalker.g:628:24: ( '/' pathEltOrInverse )*
            loop73:
            do {
                int alt73=2;
                int LA73_0 = input.LA(1);

                if ( (LA73_0==259) ) {
                    alt73=1;
                }


                switch (alt73) {
            	case 1 :
            	    // IbmSparqlExtAstWalker.g:628:26: '/' pathEltOrInverse
            	    {
            	    match(input,259,FOLLOW_259_in_pathSequence4232); if (state.failed) return ;
            	    pushFollow(FOLLOW_pathEltOrInverse_in_pathSequence4234);
            	    pathEltOrInverse();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop73;
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
    // IbmSparqlExtAstWalker.g:631:1: pathElt : pathPrimary ( pathMod )? ;
    public final void pathElt() throws RecognitionException {
        try {
            // IbmSparqlExtAstWalker.g:632:2: ( pathPrimary ( pathMod )? )
            // IbmSparqlExtAstWalker.g:632:7: pathPrimary ( pathMod )?
            {
            pushFollow(FOLLOW_pathPrimary_in_pathElt4251);
            pathPrimary();

            state._fsp--;
            if (state.failed) return ;
            // IbmSparqlExtAstWalker.g:632:19: ( pathMod )?
            int alt74=2;
            int LA74_0 = input.LA(1);

            if ( (LA74_0==256||(LA74_0>=261 && LA74_0<=262)) ) {
                alt74=1;
            }
            switch (alt74) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:632:19: pathMod
                    {
                    pushFollow(FOLLOW_pathMod_in_pathElt4253);
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
    // IbmSparqlExtAstWalker.g:635:1: pathEltOrInverse : ( pathElt | '^' pathElt );
    public final void pathEltOrInverse() throws RecognitionException {
        try {
            // IbmSparqlExtAstWalker.g:636:2: ( pathElt | '^' pathElt )
            int alt75=2;
            int LA75_0 = input.LA(1);

            if ( ((LA75_0>=PREFIXED_NAME && LA75_0<=PREFIXED_NS)||LA75_0==OPEN_BRACE||LA75_0==IRI||LA75_0==257||LA75_0==263) ) {
                alt75=1;
            }
            else if ( (LA75_0==260) ) {
                alt75=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 75, 0, input);

                throw nvae;
            }
            switch (alt75) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:636:7: pathElt
                    {
                    pushFollow(FOLLOW_pathElt_in_pathEltOrInverse4268);
                    pathElt();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:636:17: '^' pathElt
                    {
                    match(input,260,FOLLOW_260_in_pathEltOrInverse4272); if (state.failed) return ;
                    pushFollow(FOLLOW_pathElt_in_pathEltOrInverse4274);
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
    // IbmSparqlExtAstWalker.g:639:1: pathMod returns [Path.PathMod v] : ( '*' | '?' | '+' );
    public final Path.PathMod pathMod() throws RecognitionException {
        Path.PathMod v = null;

        try {
            // IbmSparqlExtAstWalker.g:640:2: ( '*' | '?' | '+' )
            int alt76=3;
            switch ( input.LA(1) ) {
            case 256:
                {
                alt76=1;
                }
                break;
            case 261:
                {
                alt76=2;
                }
                break;
            case 262:
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
                    // IbmSparqlExtAstWalker.g:640:4: '*'
                    {
                    match(input,256,FOLLOW_256_in_pathMod4289); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = Path.PathMod.ZERO_OR_MORE; 
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:641:4: '?'
                    {
                    match(input,261,FOLLOW_261_in_pathMod4298); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = Path.PathMod.ZERO_OR_ONE; 
                    }

                    }
                    break;
                case 3 :
                    // IbmSparqlExtAstWalker.g:642:4: '+'
                    {
                    match(input,262,FOLLOW_262_in_pathMod4307); if (state.failed) return v;
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
    // IbmSparqlExtAstWalker.g:645:1: pathPrimary : ( iRIref | 'a' | '!' pathNegatedPropertySet | OPEN_BRACE path CLOSE_BRACE );
    public final void pathPrimary() throws RecognitionException {
        try {
            // IbmSparqlExtAstWalker.g:646:2: ( iRIref | 'a' | '!' pathNegatedPropertySet | OPEN_BRACE path CLOSE_BRACE )
            int alt77=4;
            switch ( input.LA(1) ) {
            case PREFIXED_NAME:
            case PREFIXED_NS:
            case IRI:
                {
                alt77=1;
                }
                break;
            case 257:
                {
                alt77=2;
                }
                break;
            case 263:
                {
                alt77=3;
                }
                break;
            case OPEN_BRACE:
                {
                alt77=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 77, 0, input);

                throw nvae;
            }

            switch (alt77) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:646:7: iRIref
                    {
                    pushFollow(FOLLOW_iRIref_in_pathPrimary4325);
                    iRIref();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:646:16: 'a'
                    {
                    match(input,257,FOLLOW_257_in_pathPrimary4329); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // IbmSparqlExtAstWalker.g:646:22: '!' pathNegatedPropertySet
                    {
                    match(input,263,FOLLOW_263_in_pathPrimary4333); if (state.failed) return ;
                    pushFollow(FOLLOW_pathNegatedPropertySet_in_pathPrimary4335);
                    pathNegatedPropertySet();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // IbmSparqlExtAstWalker.g:646:51: OPEN_BRACE path CLOSE_BRACE
                    {
                    match(input,OPEN_BRACE,FOLLOW_OPEN_BRACE_in_pathPrimary4339); if (state.failed) return ;
                    pushFollow(FOLLOW_path_in_pathPrimary4341);
                    path();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,CLOSE_BRACE,FOLLOW_CLOSE_BRACE_in_pathPrimary4343); if (state.failed) return ;

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
    // IbmSparqlExtAstWalker.g:649:1: pathNegatedPropertySet returns [Pair<? extends List<IRI>, ? extends List<IRI>> pair] : (v= pathOneInPropertySet | OPEN_BRACE (v1= pathOneInPropertySet ( '|' v2= pathOneInPropertySet )* )? CLOSE_BRACE );
    public final Pair<? extends List<IRI>, ? extends List<IRI>> pathNegatedPropertySet() throws RecognitionException {
        Pair<? extends List<IRI>, ? extends List<IRI>> pair = null;

        Pair<IRI, Boolean> v = null;

        Pair<IRI, Boolean> v1 = null;

        Pair<IRI, Boolean> v2 = null;


         pair = Pair.make(new LinkedList<IRI>(), new LinkedList<IRI>()); 
        try {
            // IbmSparqlExtAstWalker.g:651:2: (v= pathOneInPropertySet | OPEN_BRACE (v1= pathOneInPropertySet ( '|' v2= pathOneInPropertySet )* )? CLOSE_BRACE )
            int alt80=2;
            int LA80_0 = input.LA(1);

            if ( (LA80_0==INV||(LA80_0>=PREFIXED_NAME && LA80_0<=PREFIXED_NS)||LA80_0==IRI||LA80_0==257) ) {
                alt80=1;
            }
            else if ( (LA80_0==OPEN_BRACE) ) {
                alt80=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return pair;}
                NoViableAltException nvae =
                    new NoViableAltException("", 80, 0, input);

                throw nvae;
            }
            switch (alt80) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:651:7: v= pathOneInPropertySet
                    {
                    pushFollow(FOLLOW_pathOneInPropertySet_in_pathNegatedPropertySet4373);
                    v=pathOneInPropertySet();

                    state._fsp--;
                    if (state.failed) return pair;
                    if ( state.backtracking==0 ) {
                       if (v.snd ) { pair.fst.add(v.fst); } else {pair.snd.add(v.fst);} 
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:652:4: OPEN_BRACE (v1= pathOneInPropertySet ( '|' v2= pathOneInPropertySet )* )? CLOSE_BRACE
                    {
                    match(input,OPEN_BRACE,FOLLOW_OPEN_BRACE_in_pathNegatedPropertySet4381); if (state.failed) return pair;
                    // IbmSparqlExtAstWalker.g:652:15: (v1= pathOneInPropertySet ( '|' v2= pathOneInPropertySet )* )?
                    int alt79=2;
                    int LA79_0 = input.LA(1);

                    if ( (LA79_0==INV||(LA79_0>=PREFIXED_NAME && LA79_0<=PREFIXED_NS)||LA79_0==IRI||LA79_0==257) ) {
                        alt79=1;
                    }
                    switch (alt79) {
                        case 1 :
                            // IbmSparqlExtAstWalker.g:652:17: v1= pathOneInPropertySet ( '|' v2= pathOneInPropertySet )*
                            {
                            pushFollow(FOLLOW_pathOneInPropertySet_in_pathNegatedPropertySet4389);
                            v1=pathOneInPropertySet();

                            state._fsp--;
                            if (state.failed) return pair;
                            // IbmSparqlExtAstWalker.g:652:43: ( '|' v2= pathOneInPropertySet )*
                            loop78:
                            do {
                                int alt78=2;
                                int LA78_0 = input.LA(1);

                                if ( (LA78_0==258) ) {
                                    alt78=1;
                                }


                                switch (alt78) {
                            	case 1 :
                            	    // IbmSparqlExtAstWalker.g:652:45: '|' v2= pathOneInPropertySet
                            	    {
                            	    match(input,258,FOLLOW_258_in_pathNegatedPropertySet4393); if (state.failed) return pair;
                            	    pushFollow(FOLLOW_pathOneInPropertySet_in_pathNegatedPropertySet4399);
                            	    v2=pathOneInPropertySet();

                            	    state._fsp--;
                            	    if (state.failed) return pair;
                            	    if ( state.backtracking==0 ) {
                            	       if (v2.snd ) { pair.fst.add(v2.fst); } else {pair.snd.add(v2.fst);} 
                            	    }

                            	    }
                            	    break;

                            	default :
                            	    break loop78;
                                }
                            } while (true);


                            }
                            break;

                    }

                    match(input,CLOSE_BRACE,FOLLOW_CLOSE_BRACE_in_pathNegatedPropertySet4410); if (state.failed) return pair;
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
    // IbmSparqlExtAstWalker.g:656:1: pathOneInPropertySet returns [Pair<IRI, Boolean> v ] : (i= iRIref | 'a' | ^( INV invi= iRIrefOrRDFType ) );
    public final Pair<IRI, Boolean> pathOneInPropertySet() throws RecognitionException {
        Pair<IRI, Boolean> v = null;

        IRI i = null;

        IRI invi = null;


        try {
            // IbmSparqlExtAstWalker.g:657:4: (i= iRIref | 'a' | ^( INV invi= iRIrefOrRDFType ) )
            int alt81=3;
            switch ( input.LA(1) ) {
            case PREFIXED_NAME:
            case PREFIXED_NS:
            case IRI:
                {
                alt81=1;
                }
                break;
            case 257:
                {
                alt81=2;
                }
                break;
            case INV:
                {
                alt81=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 81, 0, input);

                throw nvae;
            }

            switch (alt81) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:657:7: i= iRIref
                    {
                    pushFollow(FOLLOW_iRIref_in_pathOneInPropertySet4438);
                    i=iRIref();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = Pair.make(i, true); 
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:658:4: 'a'
                    {
                    match(input,257,FOLLOW_257_in_pathOneInPropertySet4445); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = Pair.make(new IRI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"), true); 
                    }

                    }
                    break;
                case 3 :
                    // IbmSparqlExtAstWalker.g:659:4: ^( INV invi= iRIrefOrRDFType )
                    {
                    match(input,INV,FOLLOW_INV_in_pathOneInPropertySet4454); if (state.failed) return v;

                    match(input, Token.DOWN, null); if (state.failed) return v;
                    pushFollow(FOLLOW_iRIrefOrRDFType_in_pathOneInPropertySet4460);
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
    // IbmSparqlExtAstWalker.g:662:1: iRIrefOrRDFType returns [ IRI v ] : (i= iRIref | 'a' );
    public final IRI iRIrefOrRDFType() throws RecognitionException {
        IRI v = null;

        IRI i = null;


        try {
            // IbmSparqlExtAstWalker.g:663:4: (i= iRIref | 'a' )
            int alt82=2;
            int LA82_0 = input.LA(1);

            if ( ((LA82_0>=PREFIXED_NAME && LA82_0<=PREFIXED_NS)||LA82_0==IRI) ) {
                alt82=1;
            }
            else if ( (LA82_0==257) ) {
                alt82=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 82, 0, input);

                throw nvae;
            }
            switch (alt82) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:663:6: i= iRIref
                    {
                    pushFollow(FOLLOW_iRIref_in_iRIrefOrRDFType4487);
                    i=iRIref();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = i;
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:664:6: 'a'
                    {
                    match(input,257,FOLLOW_257_in_iRIrefOrRDFType4496); if (state.failed) return v;
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
    // IbmSparqlExtAstWalker.g:667:1: integer : INTEGER ;
    public final void integer() throws RecognitionException {
        try {
            // IbmSparqlExtAstWalker.g:668:2: ( INTEGER )
            // IbmSparqlExtAstWalker.g:668:7: INTEGER
            {
            match(input,INTEGER,FOLLOW_INTEGER_in_integer4517); if (state.failed) return ;

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
    // IbmSparqlExtAstWalker.g:671:1: triplesNode returns [TriplesNode tn] : ^( TRIPLES_NODE (c= collection | b= blankNodePropertyList ) ) ;
    public final TriplesNode triplesNode() throws RecognitionException {
        TriplesNode tn = null;

        RDFCollection c = null;

        PropertyList b = null;


        try {
            // IbmSparqlExtAstWalker.g:672:2: ( ^( TRIPLES_NODE (c= collection | b= blankNodePropertyList ) ) )
            // IbmSparqlExtAstWalker.g:672:6: ^( TRIPLES_NODE (c= collection | b= blankNodePropertyList ) )
            {
            match(input,TRIPLES_NODE,FOLLOW_TRIPLES_NODE_in_triplesNode4536); if (state.failed) return tn;

            match(input, Token.DOWN, null); if (state.failed) return tn;
            // IbmSparqlExtAstWalker.g:673:5: (c= collection | b= blankNodePropertyList )
            int alt83=2;
            int LA83_0 = input.LA(1);

            if ( (LA83_0==COLLECTION) ) {
                alt83=1;
            }
            else if ( (LA83_0==PROPERTY_LIST) ) {
                alt83=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return tn;}
                NoViableAltException nvae =
                    new NoViableAltException("", 83, 0, input);

                throw nvae;
            }
            switch (alt83) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:673:7: c= collection
                    {
                    pushFollow(FOLLOW_collection_in_triplesNode4547);
                    c=collection();

                    state._fsp--;
                    if (state.failed) return tn;
                    if ( state.backtracking==0 ) {
                       tn = new TriplesNode(c);
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:674:8: b= blankNodePropertyList
                    {
                    pushFollow(FOLLOW_blankNodePropertyList_in_triplesNode4564);
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
    // IbmSparqlExtAstWalker.g:679:1: blankNodePropertyList returns [PropertyList pl] : ^( PROPERTY_LIST ( ^( PREDICATE v= predicate ) ^( VALUE (o= graphNode )+ ) )+ ) ;
    public final PropertyList blankNodePropertyList() throws RecognitionException {
        PropertyList pl = null;

        BinaryUnion<Variable, Path> v = null;

        GraphNode o = null;



        			pl = new PropertyList(); 
        			PropertyListElement e = null;
        		
        try {
            // IbmSparqlExtAstWalker.g:684:2: ( ^( PROPERTY_LIST ( ^( PREDICATE v= predicate ) ^( VALUE (o= graphNode )+ ) )+ ) )
            // IbmSparqlExtAstWalker.g:684:4: ^( PROPERTY_LIST ( ^( PREDICATE v= predicate ) ^( VALUE (o= graphNode )+ ) )+ )
            {
            match(input,PROPERTY_LIST,FOLLOW_PROPERTY_LIST_in_blankNodePropertyList4603); if (state.failed) return pl;

            match(input, Token.DOWN, null); if (state.failed) return pl;
            // IbmSparqlExtAstWalker.g:685:6: ( ^( PREDICATE v= predicate ) ^( VALUE (o= graphNode )+ ) )+
            int cnt85=0;
            loop85:
            do {
                int alt85=2;
                int LA85_0 = input.LA(1);

                if ( (LA85_0==PREDICATE) ) {
                    alt85=1;
                }


                switch (alt85) {
            	case 1 :
            	    // IbmSparqlExtAstWalker.g:685:8: ^( PREDICATE v= predicate ) ^( VALUE (o= graphNode )+ )
            	    {
            	    if ( state.backtracking==0 ) {
            	       e = new PropertyListElement(); 
            	    }
            	    match(input,PREDICATE,FOLLOW_PREDICATE_in_blankNodePropertyList4624); if (state.failed) return pl;

            	    match(input, Token.DOWN, null); if (state.failed) return pl;
            	    pushFollow(FOLLOW_predicate_in_blankNodePropertyList4628);
            	    v=predicate();

            	    state._fsp--;
            	    if (state.failed) return pl;

            	    match(input, Token.UP, null); if (state.failed) return pl;
            	    if ( state.backtracking==0 ) {
            	      e.setVerb(v);
            	    }
            	    match(input,VALUE,FOLLOW_VALUE_in_blankNodePropertyList4643); if (state.failed) return pl;

            	    match(input, Token.DOWN, null); if (state.failed) return pl;
            	    // IbmSparqlExtAstWalker.g:687:17: (o= graphNode )+
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
            	    	    // IbmSparqlExtAstWalker.g:687:18: o= graphNode
            	    	    {
            	    	    pushFollow(FOLLOW_graphNode_in_blankNodePropertyList4648);
            	    	    o=graphNode();

            	    	    state._fsp--;
            	    	    if (state.failed) return pl;
            	    	    if ( state.backtracking==0 ) {
            	    	      e.addObject(o);
            	    	    }

            	    	    }
            	    	    break;

            	    	default :
            	    	    if ( cnt84 >= 1 ) break loop84;
            	    	    if (state.backtracking>0) {state.failed=true; return pl;}
            	                EarlyExitException eee =
            	                    new EarlyExitException(84, input);
            	                throw eee;
            	        }
            	        cnt84++;
            	    } while (true);


            	    match(input, Token.UP, null); if (state.failed) return pl;
            	    if ( state.backtracking==0 ) {
            	       pl.add(e); 
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt85 >= 1 ) break loop85;
            	    if (state.backtracking>0) {state.failed=true; return pl;}
                        EarlyExitException eee =
                            new EarlyExitException(85, input);
                        throw eee;
                }
                cnt85++;
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
    // IbmSparqlExtAstWalker.g:693:1: predicate returns [BinaryUnion<Variable, Path> p] : (v= var | i= iRIref | 'a' | ^( ALT (alt= predicate )* ) | ^( SEQ (seq= predicate )* ) | ^( ELT pred= predicate mod= pathMod ) | ^( INV pred= predicate ) | '!' neg= pathNegatedPropertySet );
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
            // IbmSparqlExtAstWalker.g:695:2: (v= var | i= iRIref | 'a' | ^( ALT (alt= predicate )* ) | ^( SEQ (seq= predicate )* ) | ^( ELT pred= predicate mod= pathMod ) | ^( INV pred= predicate ) | '!' neg= pathNegatedPropertySet )
            int alt88=8;
            switch ( input.LA(1) ) {
            case VAR:
                {
                alt88=1;
                }
                break;
            case PREFIXED_NAME:
            case PREFIXED_NS:
            case IRI:
                {
                alt88=2;
                }
                break;
            case 257:
                {
                alt88=3;
                }
                break;
            case ALT:
                {
                alt88=4;
                }
                break;
            case SEQ:
                {
                alt88=5;
                }
                break;
            case ELT:
                {
                alt88=6;
                }
                break;
            case INV:
                {
                alt88=7;
                }
                break;
            case 263:
                {
                alt88=8;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return p;}
                NoViableAltException nvae =
                    new NoViableAltException("", 88, 0, input);

                throw nvae;
            }

            switch (alt88) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:695:4: v= var
                    {
                    pushFollow(FOLLOW_var_in_predicate4704);
                    v=var();

                    state._fsp--;
                    if (state.failed) return p;
                    if ( state.backtracking==0 ) {
                       p.setFirst(new Variable(v));  
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:696:4: i= iRIref
                    {
                    pushFollow(FOLLOW_iRIref_in_predicate4719);
                    i=iRIref();

                    state._fsp--;
                    if (state.failed) return p;
                    if ( state.backtracking==0 ) {
                       p.setSecond(new SimplePath(i)); 
                    }

                    }
                    break;
                case 3 :
                    // IbmSparqlExtAstWalker.g:697:5: 'a'
                    {
                    match(input,257,FOLLOW_257_in_predicate4730); if (state.failed) return p;
                    if ( state.backtracking==0 ) {
                       p.setSecond(new SimplePath(new IRI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"))); 
                    }

                    }
                    break;
                case 4 :
                    // IbmSparqlExtAstWalker.g:698:9: ^( ALT (alt= predicate )* )
                    {
                    match(input,ALT,FOLLOW_ALT_in_predicate4745); if (state.failed) return p;

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); if (state.failed) return p;
                        // IbmSparqlExtAstWalker.g:698:15: (alt= predicate )*
                        loop86:
                        do {
                            int alt86=2;
                            int LA86_0 = input.LA(1);

                            if ( ((LA86_0>=ALT && LA86_0<=INV)||(LA86_0>=VAR && LA86_0<=PREFIXED_NS)||LA86_0==IRI||LA86_0==257||LA86_0==263) ) {
                                alt86=1;
                            }


                            switch (alt86) {
                        	case 1 :
                        	    // IbmSparqlExtAstWalker.g:698:16: alt= predicate
                        	    {
                        	    pushFollow(FOLLOW_predicate_in_predicate4750);
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
                        	    break loop86;
                            }
                        } while (true);


                        match(input, Token.UP, null); if (state.failed) return p;
                    }

                    }
                    break;
                case 5 :
                    // IbmSparqlExtAstWalker.g:706:9: ^( SEQ (seq= predicate )* )
                    {
                    match(input,SEQ,FOLLOW_SEQ_in_predicate4766); if (state.failed) return p;

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); if (state.failed) return p;
                        // IbmSparqlExtAstWalker.g:706:15: (seq= predicate )*
                        loop87:
                        do {
                            int alt87=2;
                            int LA87_0 = input.LA(1);

                            if ( ((LA87_0>=ALT && LA87_0<=INV)||(LA87_0>=VAR && LA87_0<=PREFIXED_NS)||LA87_0==IRI||LA87_0==257||LA87_0==263) ) {
                                alt87=1;
                            }


                            switch (alt87) {
                        	case 1 :
                        	    // IbmSparqlExtAstWalker.g:706:16: seq= predicate
                        	    {
                        	    pushFollow(FOLLOW_predicate_in_predicate4771);
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
                        	    break loop87;
                            }
                        } while (true);


                        match(input, Token.UP, null); if (state.failed) return p;
                    }

                    }
                    break;
                case 6 :
                    // IbmSparqlExtAstWalker.g:714:9: ^( ELT pred= predicate mod= pathMod )
                    {
                    match(input,ELT,FOLLOW_ELT_in_predicate4787); if (state.failed) return p;

                    match(input, Token.DOWN, null); if (state.failed) return p;
                    pushFollow(FOLLOW_predicate_in_predicate4791);
                    pred=predicate();

                    state._fsp--;
                    if (state.failed) return p;
                    pushFollow(FOLLOW_pathMod_in_predicate4797);
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
                    // IbmSparqlExtAstWalker.g:715:9: ^( INV pred= predicate )
                    {
                    match(input,INV,FOLLOW_INV_in_predicate4812); if (state.failed) return p;

                    match(input, Token.DOWN, null); if (state.failed) return p;
                    pushFollow(FOLLOW_predicate_in_predicate4816);
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
                    // IbmSparqlExtAstWalker.g:716:7: '!' neg= pathNegatedPropertySet
                    {
                    match(input,263,FOLLOW_263_in_predicate4827); if (state.failed) return p;
                    pushFollow(FOLLOW_pathNegatedPropertySet_in_predicate4833);
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
    // IbmSparqlExtAstWalker.g:719:1: collection returns [RDFCollection c] : ^( COLLECTION (g= graphNode )+ ) ;
    public final RDFCollection collection() throws RecognitionException {
        RDFCollection c = null;

        GraphNode g = null;


         c = new RDFCollection(); 
        try {
            // IbmSparqlExtAstWalker.g:721:2: ( ^( COLLECTION (g= graphNode )+ ) )
            // IbmSparqlExtAstWalker.g:721:7: ^( COLLECTION (g= graphNode )+ )
            {
            match(input,COLLECTION,FOLLOW_COLLECTION_in_collection4861); if (state.failed) return c;

            match(input, Token.DOWN, null); if (state.failed) return c;
            // IbmSparqlExtAstWalker.g:721:21: (g= graphNode )+
            int cnt89=0;
            loop89:
            do {
                int alt89=2;
                int LA89_0 = input.LA(1);

                if ( ((LA89_0>=NIL && LA89_0<=ANNON)||(LA89_0>=VAR && LA89_0<=PREFIXED_NS)||(LA89_0>=STRING && LA89_0<=BOOLEAN)||LA89_0==TRIPLES_NODE||(LA89_0>=BIG_INTEGER && LA89_0<=BIG_DECIMAL)||LA89_0==IRI||LA89_0==DOUBLE||LA89_0==BLANK_NODE_LABEL) ) {
                    alt89=1;
                }


                switch (alt89) {
            	case 1 :
            	    // IbmSparqlExtAstWalker.g:721:22: g= graphNode
            	    {
            	    pushFollow(FOLLOW_graphNode_in_collection4866);
            	    g=graphNode();

            	    state._fsp--;
            	    if (state.failed) return c;
            	    if ( state.backtracking==0 ) {
            	       c.add(g); 
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt89 >= 1 ) break loop89;
            	    if (state.backtracking>0) {state.failed=true; return c;}
                        EarlyExitException eee =
                            new EarlyExitException(89, input);
                        throw eee;
                }
                cnt89++;
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
    // IbmSparqlExtAstWalker.g:724:1: graphNode returns [GraphNode gn] : (v= var | g= graphTerm | t= triplesNode );
    public final GraphNode graphNode() throws RecognitionException {
        GraphNode gn = null;

        String v = null;

        GraphTerm g = null;

        TriplesNode t = null;


        try {
            // IbmSparqlExtAstWalker.g:725:2: (v= var | g= graphTerm | t= triplesNode )
            int alt90=3;
            switch ( input.LA(1) ) {
            case VAR:
                {
                alt90=1;
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
                alt90=2;
                }
                break;
            case TRIPLES_NODE:
                {
                alt90=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return gn;}
                NoViableAltException nvae =
                    new NoViableAltException("", 90, 0, input);

                throw nvae;
            }

            switch (alt90) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:725:6: v= var
                    {
                    pushFollow(FOLLOW_var_in_graphNode4892);
                    v=var();

                    state._fsp--;
                    if (state.failed) return gn;
                    if ( state.backtracking==0 ) {
                       gn = new GraphNode(new Variable(v));			
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:726:5: g= graphTerm
                    {
                    pushFollow(FOLLOW_graphTerm_in_graphNode4905);
                    g=graphTerm();

                    state._fsp--;
                    if (state.failed) return gn;
                    if ( state.backtracking==0 ) {
                       gn = new GraphNode(g);						
                    }

                    }
                    break;
                case 3 :
                    // IbmSparqlExtAstWalker.g:727:5: t= triplesNode
                    {
                    pushFollow(FOLLOW_triplesNode_in_graphNode4917);
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
    // IbmSparqlExtAstWalker.g:730:1: varOrTerm returns [QueryTripleTerm qt] : (v= var | g= graphTerm );
    public final QueryTripleTerm varOrTerm() throws RecognitionException {
        QueryTripleTerm qt = null;

        String v = null;

        GraphTerm g = null;


        try {
            // IbmSparqlExtAstWalker.g:731:2: (v= var | g= graphTerm )
            int alt91=2;
            int LA91_0 = input.LA(1);

            if ( (LA91_0==VAR) ) {
                alt91=1;
            }
            else if ( ((LA91_0>=NIL && LA91_0<=ANNON)||(LA91_0>=PREFIXED_NAME && LA91_0<=PREFIXED_NS)||(LA91_0>=STRING && LA91_0<=BOOLEAN)||(LA91_0>=BIG_INTEGER && LA91_0<=BIG_DECIMAL)||LA91_0==IRI||LA91_0==DOUBLE||LA91_0==BLANK_NODE_LABEL) ) {
                alt91=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return qt;}
                NoViableAltException nvae =
                    new NoViableAltException("", 91, 0, input);

                throw nvae;
            }
            switch (alt91) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:731:7: v= var
                    {
                    pushFollow(FOLLOW_var_in_varOrTerm4940);
                    v=var();

                    state._fsp--;
                    if (state.failed) return qt;
                    if ( state.backtracking==0 ) {
                       qt = new QueryTripleTerm(new Variable(v)); 	
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:732:6: g= graphTerm
                    {
                    pushFollow(FOLLOW_graphTerm_in_varOrTerm4955);
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
    // IbmSparqlExtAstWalker.g:735:1: varOrIRIref returns [QueryTripleTerm qt] : (v= var | i= iRIref );
    public final QueryTripleTerm varOrIRIref() throws RecognitionException {
        QueryTripleTerm qt = null;

        String v = null;

        IRI i = null;


        try {
            // IbmSparqlExtAstWalker.g:736:2: (v= var | i= iRIref )
            int alt92=2;
            int LA92_0 = input.LA(1);

            if ( (LA92_0==VAR) ) {
                alt92=1;
            }
            else if ( ((LA92_0>=PREFIXED_NAME && LA92_0<=PREFIXED_NS)||LA92_0==IRI) ) {
                alt92=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return qt;}
                NoViableAltException nvae =
                    new NoViableAltException("", 92, 0, input);

                throw nvae;
            }
            switch (alt92) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:736:6: v= var
                    {
                    pushFollow(FOLLOW_var_in_varOrIRIref4977);
                    v=var();

                    state._fsp--;
                    if (state.failed) return qt;
                    if ( state.backtracking==0 ) {
                       qt = new QueryTripleTerm(new Variable(v)); 	
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:737:5: i= iRIref
                    {
                    pushFollow(FOLLOW_iRIref_in_varOrIRIref4992);
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
    // IbmSparqlExtAstWalker.g:740:1: varOrIRIref2 returns [BinaryUnion<Variable, IRI> bu] : (v= var | i= iRIref );
    public final BinaryUnion<Variable, IRI> varOrIRIref2() throws RecognitionException {
        BinaryUnion<Variable, IRI> bu = null;

        String v = null;

        IRI i = null;


         bu = new BinaryUnion<Variable, IRI>(); 
        try {
            // IbmSparqlExtAstWalker.g:742:5: (v= var | i= iRIref )
            int alt93=2;
            int LA93_0 = input.LA(1);

            if ( (LA93_0==VAR) ) {
                alt93=1;
            }
            else if ( ((LA93_0>=PREFIXED_NAME && LA93_0<=PREFIXED_NS)||LA93_0==IRI) ) {
                alt93=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return bu;}
                NoViableAltException nvae =
                    new NoViableAltException("", 93, 0, input);

                throw nvae;
            }
            switch (alt93) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:742:8: v= var
                    {
                    pushFollow(FOLLOW_var_in_varOrIRIref25027);
                    v=var();

                    state._fsp--;
                    if (state.failed) return bu;
                    if ( state.backtracking==0 ) {
                       bu.setFirst(new Variable(v)); 
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:743:8: i= iRIref
                    {
                    pushFollow(FOLLOW_iRIref_in_varOrIRIref25043);
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
    // IbmSparqlExtAstWalker.g:746:1: var returns [String v] : ^( VAR (x1= VAR1 | x2= VAR2 ) ) ;
    public final String var() throws RecognitionException {
        String v = null;

        XTree x1=null;
        XTree x2=null;

        try {
            // IbmSparqlExtAstWalker.g:747:2: ( ^( VAR (x1= VAR1 | x2= VAR2 ) ) )
            // IbmSparqlExtAstWalker.g:747:6: ^( VAR (x1= VAR1 | x2= VAR2 ) )
            {
            match(input,VAR,FOLLOW_VAR_in_var5070); if (state.failed) return v;

            match(input, Token.DOWN, null); if (state.failed) return v;
            // IbmSparqlExtAstWalker.g:747:13: (x1= VAR1 | x2= VAR2 )
            int alt94=2;
            int LA94_0 = input.LA(1);

            if ( (LA94_0==VAR1) ) {
                alt94=1;
            }
            else if ( (LA94_0==VAR2) ) {
                alt94=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 94, 0, input);

                throw nvae;
            }
            switch (alt94) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:747:15: x1= VAR1
                    {
                    x1=(XTree)match(input,VAR1,FOLLOW_VAR1_in_var5077); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = new String(x1.getText().substring(1)); 
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:748:7: x2= VAR2
                    {
                    x2=(XTree)match(input,VAR2,FOLLOW_VAR2_in_var5092); if (state.failed) return v;
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
    // IbmSparqlExtAstWalker.g:753:1: graphTerm returns [GraphTerm gt] : (i= iRIref | r= rDFLiteral | d= numericLiteral | b= booleanLiteral | l= blankNode | NIL );
    public final GraphTerm graphTerm() throws RecognitionException {
        GraphTerm gt = null;

        IRI i = null;

        StringLiteral r = null;

        Constant d = null;

        Boolean b = null;

        BlankNode l = null;


        try {
            // IbmSparqlExtAstWalker.g:754:2: (i= iRIref | r= rDFLiteral | d= numericLiteral | b= booleanLiteral | l= blankNode | NIL )
            int alt95=6;
            switch ( input.LA(1) ) {
            case PREFIXED_NAME:
            case PREFIXED_NS:
            case IRI:
                {
                alt95=1;
                }
                break;
            case STRING:
                {
                alt95=2;
                }
                break;
            case BIG_INTEGER:
            case BIG_DECIMAL:
            case DOUBLE:
                {
                alt95=3;
                }
                break;
            case BOOLEAN:
                {
                alt95=4;
                }
                break;
            case ANNON:
            case BLANK_NODE_LABEL:
                {
                alt95=5;
                }
                break;
            case NIL:
                {
                alt95=6;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return gt;}
                NoViableAltException nvae =
                    new NoViableAltException("", 95, 0, input);

                throw nvae;
            }

            switch (alt95) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:754:6: i= iRIref
                    {
                    pushFollow(FOLLOW_iRIref_in_graphTerm5126);
                    i=iRIref();

                    state._fsp--;
                    if (state.failed) return gt;
                    if ( state.backtracking==0 ) {
                       gt = new GraphTerm(i); 
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:755:5: r= rDFLiteral
                    {
                    pushFollow(FOLLOW_rDFLiteral_in_graphTerm5140);
                    r=rDFLiteral();

                    state._fsp--;
                    if (state.failed) return gt;
                    if ( state.backtracking==0 ) {
                       gt = new GraphTerm(new Constant(r)); 
                    }

                    }
                    break;
                case 3 :
                    // IbmSparqlExtAstWalker.g:756:4: d= numericLiteral
                    {
                    pushFollow(FOLLOW_numericLiteral_in_graphTerm5152);
                    d=numericLiteral();

                    state._fsp--;
                    if (state.failed) return gt;
                    if ( state.backtracking==0 ) {
                       gt = new GraphTerm(d); 				
                    }

                    }
                    break;
                case 4 :
                    // IbmSparqlExtAstWalker.g:757:5: b= booleanLiteral
                    {
                    pushFollow(FOLLOW_booleanLiteral_in_graphTerm5165);
                    b=booleanLiteral();

                    state._fsp--;
                    if (state.failed) return gt;
                    if ( state.backtracking==0 ) {
                       gt = new GraphTerm(new Constant(b)); 
                    }

                    }
                    break;
                case 5 :
                    // IbmSparqlExtAstWalker.g:758:5: l= blankNode
                    {
                    pushFollow(FOLLOW_blankNode_in_graphTerm5178);
                    l=blankNode();

                    state._fsp--;
                    if (state.failed) return gt;
                    if ( state.backtracking==0 ) {
                       gt = new GraphTerm(l); 
                    }

                    }
                    break;
                case 6 :
                    // IbmSparqlExtAstWalker.g:759:5: NIL
                    {
                    match(input,NIL,FOLLOW_NIL_in_graphTerm5191); if (state.failed) return gt;
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
    // IbmSparqlExtAstWalker.g:762:1: expression returns [Expression e] : ( ^( LOGICAL_OR e1= expression (e2= expression )+ ) | ^( LOGICAL_AND e3= expression (e4= expression )+ ) | ^( '=' e5= expression e6= expression ) | ^( '!=' e7= expression e8= expression ) | ^( LT e9= expression e10= expression ) | ^( '>' e11= expression e12= expression ) | ^( LTE e13= expression e14= expression ) | ^( '>=' e15= expression e16= expression ) | ^( IN e17= expression e18= expressionList ) | ^( NOT e19= expression e20= expressionList ) | ^( '+' e21= expression (e22= expression )? ) | ^( BROKEN_PLUS e21= expression e22= expression ) | ^( '-' e23= expression (e24= expression )? ) | ^( BROKEN_MINUS e23= expression e24= expression ) | ^( '*' e25= expression e26= expression ) | ^( '/' e27= expression e28= expression ) | ^( '!' e29= expression ) | e30= brackettedExpression | e31= primaryExpression );
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
            // IbmSparqlExtAstWalker.g:768:2: ( ^( LOGICAL_OR e1= expression (e2= expression )+ ) | ^( LOGICAL_AND e3= expression (e4= expression )+ ) | ^( '=' e5= expression e6= expression ) | ^( '!=' e7= expression e8= expression ) | ^( LT e9= expression e10= expression ) | ^( '>' e11= expression e12= expression ) | ^( LTE e13= expression e14= expression ) | ^( '>=' e15= expression e16= expression ) | ^( IN e17= expression e18= expressionList ) | ^( NOT e19= expression e20= expressionList ) | ^( '+' e21= expression (e22= expression )? ) | ^( BROKEN_PLUS e21= expression e22= expression ) | ^( '-' e23= expression (e24= expression )? ) | ^( BROKEN_MINUS e23= expression e24= expression ) | ^( '*' e25= expression e26= expression ) | ^( '/' e27= expression e28= expression ) | ^( '!' e29= expression ) | e30= brackettedExpression | e31= primaryExpression )
            int alt100=19;
            switch ( input.LA(1) ) {
            case LOGICAL_OR:
                {
                alt100=1;
                }
                break;
            case LOGICAL_AND:
                {
                alt100=2;
                }
                break;
            case 264:
                {
                alt100=3;
                }
                break;
            case 265:
                {
                alt100=4;
                }
                break;
            case LT:
                {
                alt100=5;
                }
                break;
            case 266:
                {
                alt100=6;
                }
                break;
            case LTE:
                {
                alt100=7;
                }
                break;
            case 267:
                {
                alt100=8;
                }
                break;
            case IN:
                {
                alt100=9;
                }
                break;
            case NOT:
                {
                alt100=10;
                }
                break;
            case 262:
                {
                alt100=11;
                }
                break;
            case BROKEN_PLUS:
                {
                alt100=12;
                }
                break;
            case 268:
                {
                alt100=13;
                }
                break;
            case BROKEN_MINUS:
                {
                alt100=14;
                }
                break;
            case 256:
                {
                alt100=15;
                }
                break;
            case 259:
                {
                alt100=16;
                }
                break;
            case 263:
                {
                alt100=17;
                }
                break;
            case EXPRESSION:
                {
                alt100=18;
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
                alt100=19;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return e;}
                NoViableAltException nvae =
                    new NoViableAltException("", 100, 0, input);

                throw nvae;
            }

            switch (alt100) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:768:6: ^( LOGICAL_OR e1= expression (e2= expression )+ )
                    {
                    match(input,LOGICAL_OR,FOLLOW_LOGICAL_OR_in_expression5222); if (state.failed) return e;

                    if ( state.backtracking==0 ) {
                       le = new LogicalExpression(LogicalExpression.ELogicalType.OR);  
                    }

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression5234);
                    e1=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       le.addComponent(e1);   
                    }
                    // IbmSparqlExtAstWalker.g:770:4: (e2= expression )+
                    int cnt96=0;
                    loop96:
                    do {
                        int alt96=2;
                        int LA96_0 = input.LA(1);

                        if ( ((LA96_0>=BROKEN_PLUS && LA96_0<=BROKEN_MINUS)||(LA96_0>=VAR && LA96_0<=NOT_EXISTS)||(LA96_0>=STRING && LA96_0<=BOOLEAN)||LA96_0==LTE||(LA96_0>=BIG_INTEGER && LA96_0<=BIG_DECIMAL)||(LA96_0>=LOGICAL_OR && LA96_0<=SHA1)||(LA96_0>=SHA256 && LA96_0<=GROUP_CONCAT)||LA96_0==DOUBLE||LA96_0==256||LA96_0==259||(LA96_0>=262 && LA96_0<=268)) ) {
                            alt96=1;
                        }


                        switch (alt96) {
                    	case 1 :
                    	    // IbmSparqlExtAstWalker.g:770:5: e2= expression
                    	    {
                    	    pushFollow(FOLLOW_expression_in_expression5255);
                    	    e2=expression();

                    	    state._fsp--;
                    	    if (state.failed) return e;
                    	    if ( state.backtracking==0 ) {
                    	       le.addComponent(e2); 	
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt96 >= 1 ) break loop96;
                    	    if (state.backtracking>0) {state.failed=true; return e;}
                                EarlyExitException eee =
                                    new EarlyExitException(96, input);
                                throw eee;
                        }
                        cnt96++;
                    } while (true);


                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = le; 
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:772:6: ^( LOGICAL_AND e3= expression (e4= expression )+ )
                    {
                    match(input,LOGICAL_AND,FOLLOW_LOGICAL_AND_in_expression5280); if (state.failed) return e;

                    if ( state.backtracking==0 ) {
                       le = new LogicalExpression(LogicalExpression.ELogicalType.AND); 
                    }

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression5292);
                    e3=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       le.addComponent(e3);   
                    }
                    // IbmSparqlExtAstWalker.g:774:4: (e4= expression )+
                    int cnt97=0;
                    loop97:
                    do {
                        int alt97=2;
                        int LA97_0 = input.LA(1);

                        if ( ((LA97_0>=BROKEN_PLUS && LA97_0<=BROKEN_MINUS)||(LA97_0>=VAR && LA97_0<=NOT_EXISTS)||(LA97_0>=STRING && LA97_0<=BOOLEAN)||LA97_0==LTE||(LA97_0>=BIG_INTEGER && LA97_0<=BIG_DECIMAL)||(LA97_0>=LOGICAL_OR && LA97_0<=SHA1)||(LA97_0>=SHA256 && LA97_0<=GROUP_CONCAT)||LA97_0==DOUBLE||LA97_0==256||LA97_0==259||(LA97_0>=262 && LA97_0<=268)) ) {
                            alt97=1;
                        }


                        switch (alt97) {
                    	case 1 :
                    	    // IbmSparqlExtAstWalker.g:774:5: e4= expression
                    	    {
                    	    pushFollow(FOLLOW_expression_in_expression5313);
                    	    e4=expression();

                    	    state._fsp--;
                    	    if (state.failed) return e;
                    	    if ( state.backtracking==0 ) {
                    	       le.addComponent(e4); 	
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt97 >= 1 ) break loop97;
                    	    if (state.backtracking>0) {state.failed=true; return e;}
                                EarlyExitException eee =
                                    new EarlyExitException(97, input);
                                throw eee;
                        }
                        cnt97++;
                    } while (true);


                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = le; 
                    }

                    }
                    break;
                case 3 :
                    // IbmSparqlExtAstWalker.g:776:6: ^( '=' e5= expression e6= expression )
                    {
                    match(input,264,FOLLOW_264_in_expression5340); if (state.failed) return e;

                    if ( state.backtracking==0 ) {
                       rl = new RelationalExpression(); 								  
                    }

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression5354);
                    e5=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       rl.setLeft(e5); 				
                    }
                    pushFollow(FOLLOW_expression_in_expression5365);
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
                    // IbmSparqlExtAstWalker.g:780:4: ^( '!=' e7= expression e8= expression )
                    {
                    match(input,265,FOLLOW_265_in_expression5385); if (state.failed) return e;

                    if ( state.backtracking==0 ) {
                       rl = new RelationalExpression(); 								  
                    }

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression5399);
                    e7=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       rl.setLeft(e7); 				
                    }
                    pushFollow(FOLLOW_expression_in_expression5410);
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
                    // IbmSparqlExtAstWalker.g:784:4: ^( LT e9= expression e10= expression )
                    {
                    match(input,LT,FOLLOW_LT_in_expression5430); if (state.failed) return e;

                    if ( state.backtracking==0 ) {
                       rl = new RelationalExpression(); 								  
                    }

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression5445);
                    e9=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       rl.setLeft(e9); 				
                    }
                    pushFollow(FOLLOW_expression_in_expression5460);
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
                    // IbmSparqlExtAstWalker.g:788:4: ^( '>' e11= expression e12= expression )
                    {
                    match(input,266,FOLLOW_266_in_expression5480); if (state.failed) return e;

                    if ( state.backtracking==0 ) {
                       rl = new RelationalExpression(); 								  
                    }

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression5495);
                    e11=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       rl.setLeft(e11); 				
                    }
                    pushFollow(FOLLOW_expression_in_expression5506);
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
                    // IbmSparqlExtAstWalker.g:792:4: ^( LTE e13= expression e14= expression )
                    {
                    match(input,LTE,FOLLOW_LTE_in_expression5526); if (state.failed) return e;

                    if ( state.backtracking==0 ) {
                       rl = new RelationalExpression(); 								  
                    }

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression5540);
                    e13=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       rl.setLeft(e13); 				
                    }
                    pushFollow(FOLLOW_expression_in_expression5551);
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
                    // IbmSparqlExtAstWalker.g:796:4: ^( '>=' e15= expression e16= expression )
                    {
                    match(input,267,FOLLOW_267_in_expression5571); if (state.failed) return e;

                    if ( state.backtracking==0 ) {
                       rl = new RelationalExpression(); 								  
                    }

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression5585);
                    e15=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       rl.setLeft(e15); 				
                    }
                    pushFollow(FOLLOW_expression_in_expression5596);
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
                    // IbmSparqlExtAstWalker.g:800:4: ^( IN e17= expression e18= expressionList )
                    {
                    match(input,IN,FOLLOW_IN_in_expression5616); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression5643);
                    e17=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expressionList_in_expression5656);
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
                    // IbmSparqlExtAstWalker.g:805:4: ^( NOT e19= expression e20= expressionList )
                    {
                    match(input,NOT,FOLLOW_NOT_in_expression5702); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression5709);
                    e19=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expressionList_in_expression5717);
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
                    // IbmSparqlExtAstWalker.g:809:4: ^( '+' e21= expression (e22= expression )? )
                    {
                    match(input,262,FOLLOW_262_in_expression5751); if (state.failed) return e;

                    if ( state.backtracking==0 ) {
                       ne = new NumericExpression(); 								
                    }

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression5778);
                    e21=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       ne.setLHS(e21); 				
                    }
                    // IbmSparqlExtAstWalker.g:811:4: (e22= expression )?
                    int alt98=2;
                    int LA98_0 = input.LA(1);

                    if ( ((LA98_0>=BROKEN_PLUS && LA98_0<=BROKEN_MINUS)||(LA98_0>=VAR && LA98_0<=NOT_EXISTS)||(LA98_0>=STRING && LA98_0<=BOOLEAN)||LA98_0==LTE||(LA98_0>=BIG_INTEGER && LA98_0<=BIG_DECIMAL)||(LA98_0>=LOGICAL_OR && LA98_0<=SHA1)||(LA98_0>=SHA256 && LA98_0<=GROUP_CONCAT)||LA98_0==DOUBLE||LA98_0==256||LA98_0==259||(LA98_0>=262 && LA98_0<=268)) ) {
                        alt98=1;
                    }
                    switch (alt98) {
                        case 1 :
                            // IbmSparqlExtAstWalker.g:811:5: e22= expression
                            {
                            pushFollow(FOLLOW_expression_in_expression5790);
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
                    // IbmSparqlExtAstWalker.g:813:4: ^( BROKEN_PLUS e21= expression e22= expression )
                    {
                    match(input,BROKEN_PLUS,FOLLOW_BROKEN_PLUS_in_expression5813); if (state.failed) return e;

                    if ( state.backtracking==0 ) {
                       ne = new NumericExpression(); 								
                    }

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression5832);
                    e21=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       ne.setLHS(e21); 				
                    }
                    pushFollow(FOLLOW_expression_in_expression5843);
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
                    // IbmSparqlExtAstWalker.g:817:4: ^( '-' e23= expression (e24= expression )? )
                    {
                    match(input,268,FOLLOW_268_in_expression5863); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression5889);
                    e23=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new UnaryExpression(Expression.EUnaryOp.MINUS, e23); 
                    }
                    // IbmSparqlExtAstWalker.g:819:4: (e24= expression )?
                    int alt99=2;
                    int LA99_0 = input.LA(1);

                    if ( ((LA99_0>=BROKEN_PLUS && LA99_0<=BROKEN_MINUS)||(LA99_0>=VAR && LA99_0<=NOT_EXISTS)||(LA99_0>=STRING && LA99_0<=BOOLEAN)||LA99_0==LTE||(LA99_0>=BIG_INTEGER && LA99_0<=BIG_DECIMAL)||(LA99_0>=LOGICAL_OR && LA99_0<=SHA1)||(LA99_0>=SHA256 && LA99_0<=GROUP_CONCAT)||LA99_0==DOUBLE||LA99_0==256||LA99_0==259||(LA99_0>=262 && LA99_0<=268)) ) {
                        alt99=1;
                    }
                    switch (alt99) {
                        case 1 :
                            // IbmSparqlExtAstWalker.g:819:5: e24= expression
                            {
                            pushFollow(FOLLOW_expression_in_expression5901);
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
                    // IbmSparqlExtAstWalker.g:821:4: ^( BROKEN_MINUS e23= expression e24= expression )
                    {
                    match(input,BROKEN_MINUS,FOLLOW_BROKEN_MINUS_in_expression5918); if (state.failed) return e;

                    if ( state.backtracking==0 ) {
                       ne = new NumericExpression(); 								
                    }

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression5936);
                    e23=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       ne.setLHS(e23); 				
                    }
                    pushFollow(FOLLOW_expression_in_expression5947);
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
                    // IbmSparqlExtAstWalker.g:825:4: ^( '*' e25= expression e26= expression )
                    {
                    match(input,256,FOLLOW_256_in_expression5967); if (state.failed) return e;

                    if ( state.backtracking==0 ) {
                       ne = new NumericExpression(); 								
                    }

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression5994);
                    e25=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       ne.setLHS(e25); 				
                    }
                    pushFollow(FOLLOW_expression_in_expression6005);
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
                    // IbmSparqlExtAstWalker.g:829:4: ^( '/' e27= expression e28= expression )
                    {
                    match(input,259,FOLLOW_259_in_expression6025); if (state.failed) return e;

                    if ( state.backtracking==0 ) {
                       ne = new NumericExpression(); 								
                    }

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression6052);
                    e27=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       ne.setLHS(e27); 				
                    }
                    pushFollow(FOLLOW_expression_in_expression6062);
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
                    // IbmSparqlExtAstWalker.g:833:4: ^( '!' e29= expression )
                    {
                    match(input,263,FOLLOW_263_in_expression6082); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_expression6086);
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
                    // IbmSparqlExtAstWalker.g:834:4: e30= brackettedExpression
                    {
                    pushFollow(FOLLOW_brackettedExpression_in_expression6097);
                    e30=brackettedExpression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = e30; 
                    }

                    }
                    break;
                case 19 :
                    // IbmSparqlExtAstWalker.g:835:5: e31= primaryExpression
                    {
                    pushFollow(FOLLOW_primaryExpression_in_expression6107);
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
    // IbmSparqlExtAstWalker.g:839:1: primaryExpression returns [Expression e] : (e1= builtInCall | i= iRIref | f= iRIFunction | r= rDFLiteral | n= numericLiteral | b= booleanLiteral | v= var | a= aggregate );
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
            // IbmSparqlExtAstWalker.g:840:2: (e1= builtInCall | i= iRIref | f= iRIFunction | r= rDFLiteral | n= numericLiteral | b= booleanLiteral | v= var | a= aggregate )
            int alt101=8;
            alt101 = dfa101.predict(input);
            switch (alt101) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:840:6: e1= builtInCall
                    {
                    pushFollow(FOLLOW_builtInCall_in_primaryExpression6132);
                    e1=builtInCall();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = e1; 
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:841:5: i= iRIref
                    {
                    pushFollow(FOLLOW_iRIref_in_primaryExpression6144);
                    i=iRIref();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new ConstantExpression(i); 
                    }

                    }
                    break;
                case 3 :
                    // IbmSparqlExtAstWalker.g:842:4: f= iRIFunction
                    {
                    pushFollow(FOLLOW_iRIFunction_in_primaryExpression6156);
                    f=iRIFunction();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new FunctionCallExpression(f); 
                    }

                    }
                    break;
                case 4 :
                    // IbmSparqlExtAstWalker.g:843:5: r= rDFLiteral
                    {
                    pushFollow(FOLLOW_rDFLiteral_in_primaryExpression6168);
                    r=rDFLiteral();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new ConstantExpression(r); 
                    }

                    }
                    break;
                case 5 :
                    // IbmSparqlExtAstWalker.g:844:5: n= numericLiteral
                    {
                    pushFollow(FOLLOW_numericLiteral_in_primaryExpression6180);
                    n=numericLiteral();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new ConstantExpression(n); 
                    }

                    }
                    break;
                case 6 :
                    // IbmSparqlExtAstWalker.g:845:5: b= booleanLiteral
                    {
                    pushFollow(FOLLOW_booleanLiteral_in_primaryExpression6191);
                    b=booleanLiteral();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new ConstantExpression(b); 
                    }

                    }
                    break;
                case 7 :
                    // IbmSparqlExtAstWalker.g:846:5: v= var
                    {
                    pushFollow(FOLLOW_var_in_primaryExpression6202);
                    v=var();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new VariableExpression(v); 
                    }

                    }
                    break;
                case 8 :
                    // IbmSparqlExtAstWalker.g:847:5: a= aggregate
                    {
                    pushFollow(FOLLOW_aggregate_in_primaryExpression6216);
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
    // IbmSparqlExtAstWalker.g:850:1: brackettedExpression returns [Expression e] : ^( EXPRESSION e1= expression ) ;
    public final Expression brackettedExpression() throws RecognitionException {
        Expression e = null;

        Expression e1 = null;


        try {
            // IbmSparqlExtAstWalker.g:851:2: ( ^( EXPRESSION e1= expression ) )
            // IbmSparqlExtAstWalker.g:851:4: ^( EXPRESSION e1= expression )
            {
            match(input,EXPRESSION,FOLLOW_EXPRESSION_in_brackettedExpression6239); if (state.failed) return e;

            match(input, Token.DOWN, null); if (state.failed) return e;
            pushFollow(FOLLOW_expression_in_brackettedExpression6244);
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
    // IbmSparqlExtAstWalker.g:854:1: builtInCall returns [Expression e] : ( ^( STR st= expression ) | ^( LANG lg= expression ) | ^( LANGMATCHES lm1= expression lm2= expression ) | ^( DATATYPE dt= expression ) | ^( BOUND v= var ) | ^( IRI e6= expression ) | ^( URI e7= expression ) | ^( BNODE e8= expression ) | BNODE | ^( RAND NIL ) | ^( ABS e9= expression ) | ^( CEIL e10= expression ) | ^( FLOOR e11= expression ) | ^( ROUND e12= expression ) | ^( CONCAT e13= expressionList ) | ^( SUBSTR e14= expression e15= expression (e16= expression )? ) | ^( STRLEN e15= expression ) | ^( UCASE e16= expression ) | ^( REPLACE e1= expression e2= expression e3= expression (e4= expression )? ) | ^( LCASE e17= expression ) | ^( ENCODE_FOR_URI e18= expression ) | ^( CONTAINS e19= expression e20= expression ) | ^( STRSTARTS e21= expression e22= expression ) | ^( STRENDS e23= expression e24= expression ) | ^( STRBEFORE e241= expression e242= expression ) | ^( STRAFTER e243= expression e244= expression ) | ^( YEAR e25= expression ) | ^( MONTH e26= expression ) | ^( DAY e27= expression ) | ^( HOURS e28= expression ) | ^( MINUTES e29= expression ) | ^( SECONDS e30= expression ) | ^( TIMEZONE e31= expression ) | ^( TZ e32= expression ) | NOW | UUID | STRUUID | ^( MD5 e33= expression ) | ^( SHA1 e34= expression ) | ^( SHA256 e36= expression ) | ^( SHA384 e37= expression ) | ^( SHA512 e38= expression ) | ^( COALESCE e39= expressionList ) | ^( IF e40= expression e41= expression e42= expression ) | ^( STRLANG e45= expression e46= expression ) | ^( STRDT e47= expression e48= expression ) | ^( SAMETERM sam1= expression sam2= expression ) | ^( ISIRI isi= expression ) | ^( ISURI isu= expression ) | ^( ISBLANK isb= expression ) | ^( ISLITERAL isl= expression ) | ^( ISNUMERIC e55= expression ) | r= regexExpression | p= existsFunc | p= notExistsFunc );
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
            // IbmSparqlExtAstWalker.g:858:2: ( ^( STR st= expression ) | ^( LANG lg= expression ) | ^( LANGMATCHES lm1= expression lm2= expression ) | ^( DATATYPE dt= expression ) | ^( BOUND v= var ) | ^( IRI e6= expression ) | ^( URI e7= expression ) | ^( BNODE e8= expression ) | BNODE | ^( RAND NIL ) | ^( ABS e9= expression ) | ^( CEIL e10= expression ) | ^( FLOOR e11= expression ) | ^( ROUND e12= expression ) | ^( CONCAT e13= expressionList ) | ^( SUBSTR e14= expression e15= expression (e16= expression )? ) | ^( STRLEN e15= expression ) | ^( UCASE e16= expression ) | ^( REPLACE e1= expression e2= expression e3= expression (e4= expression )? ) | ^( LCASE e17= expression ) | ^( ENCODE_FOR_URI e18= expression ) | ^( CONTAINS e19= expression e20= expression ) | ^( STRSTARTS e21= expression e22= expression ) | ^( STRENDS e23= expression e24= expression ) | ^( STRBEFORE e241= expression e242= expression ) | ^( STRAFTER e243= expression e244= expression ) | ^( YEAR e25= expression ) | ^( MONTH e26= expression ) | ^( DAY e27= expression ) | ^( HOURS e28= expression ) | ^( MINUTES e29= expression ) | ^( SECONDS e30= expression ) | ^( TIMEZONE e31= expression ) | ^( TZ e32= expression ) | NOW | UUID | STRUUID | ^( MD5 e33= expression ) | ^( SHA1 e34= expression ) | ^( SHA256 e36= expression ) | ^( SHA384 e37= expression ) | ^( SHA512 e38= expression ) | ^( COALESCE e39= expressionList ) | ^( IF e40= expression e41= expression e42= expression ) | ^( STRLANG e45= expression e46= expression ) | ^( STRDT e47= expression e48= expression ) | ^( SAMETERM sam1= expression sam2= expression ) | ^( ISIRI isi= expression ) | ^( ISURI isu= expression ) | ^( ISBLANK isb= expression ) | ^( ISLITERAL isl= expression ) | ^( ISNUMERIC e55= expression ) | r= regexExpression | p= existsFunc | p= notExistsFunc )
            int alt104=55;
            alt104 = dfa104.predict(input);
            switch (alt104) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:858:5: ^( STR st= expression )
                    {
                    match(input,STR,FOLLOW_STR_in_builtInCall6274); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6278);
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
                    // IbmSparqlExtAstWalker.g:860:5: ^( LANG lg= expression )
                    {
                    match(input,LANG,FOLLOW_LANG_in_builtInCall6291); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6295);
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
                    // IbmSparqlExtAstWalker.g:862:5: ^( LANGMATCHES lm1= expression lm2= expression )
                    {
                    match(input,LANGMATCHES,FOLLOW_LANGMATCHES_in_builtInCall6308); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6312);
                    lm1=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6316);
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
                    // IbmSparqlExtAstWalker.g:864:5: ^( DATATYPE dt= expression )
                    {
                    match(input,DATATYPE,FOLLOW_DATATYPE_in_builtInCall6329); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6333);
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
                    // IbmSparqlExtAstWalker.g:866:4: ^( BOUND v= var )
                    {
                    match(input,BOUND,FOLLOW_BOUND_in_builtInCall6345); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_var_in_builtInCall6349);
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
                    // IbmSparqlExtAstWalker.g:868:4: ^( IRI e6= expression )
                    {
                    match(input,IRI,FOLLOW_IRI_in_builtInCall6367); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6371);
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
                    // IbmSparqlExtAstWalker.g:870:5: ^( URI e7= expression )
                    {
                    match(input,URI,FOLLOW_URI_in_builtInCall6384); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6388);
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
                    // IbmSparqlExtAstWalker.g:872:4: ^( BNODE e8= expression )
                    {
                    match(input,BNODE,FOLLOW_BNODE_in_builtInCall6400); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6404);
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
                    // IbmSparqlExtAstWalker.g:874:4: BNODE
                    {
                    match(input,BNODE,FOLLOW_BNODE_in_builtInCall6415); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new BuiltinFunctionExpression(Expression.EBuiltinType.BNODE); 							
                    }

                    }
                    break;
                case 10 :
                    // IbmSparqlExtAstWalker.g:876:4: ^( RAND NIL )
                    {
                    match(input,RAND,FOLLOW_RAND_in_builtInCall6426); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    match(input,NIL,FOLLOW_NIL_in_builtInCall6428); if (state.failed) return e;

                    match(input, Token.UP, null); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new BuiltinFunctionExpression(Expression.EBuiltinType.RAND); 							
                    }

                    }
                    break;
                case 11 :
                    // IbmSparqlExtAstWalker.g:878:4: ^( ABS e9= expression )
                    {
                    match(input,ABS,FOLLOW_ABS_in_builtInCall6441); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6445);
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
                    // IbmSparqlExtAstWalker.g:880:4: ^( CEIL e10= expression )
                    {
                    match(input,CEIL,FOLLOW_CEIL_in_builtInCall6457); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6461);
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
                    // IbmSparqlExtAstWalker.g:882:4: ^( FLOOR e11= expression )
                    {
                    match(input,FLOOR,FOLLOW_FLOOR_in_builtInCall6473); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6477);
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
                    // IbmSparqlExtAstWalker.g:884:4: ^( ROUND e12= expression )
                    {
                    match(input,ROUND,FOLLOW_ROUND_in_builtInCall6489); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6493);
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
                    // IbmSparqlExtAstWalker.g:886:4: ^( CONCAT e13= expressionList )
                    {
                    match(input,CONCAT,FOLLOW_CONCAT_in_builtInCall6505); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expressionList_in_builtInCall6509);
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
                    // IbmSparqlExtAstWalker.g:888:5: ^( SUBSTR e14= expression e15= expression (e16= expression )? )
                    {
                    match(input,SUBSTR,FOLLOW_SUBSTR_in_builtInCall6522); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6526);
                    e14=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6530);
                    e15=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       args.add(e14); args.add(e15); 
                    }
                    // IbmSparqlExtAstWalker.g:890:13: (e16= expression )?
                    int alt102=2;
                    int LA102_0 = input.LA(1);

                    if ( ((LA102_0>=BROKEN_PLUS && LA102_0<=BROKEN_MINUS)||(LA102_0>=VAR && LA102_0<=NOT_EXISTS)||(LA102_0>=STRING && LA102_0<=BOOLEAN)||LA102_0==LTE||(LA102_0>=BIG_INTEGER && LA102_0<=BIG_DECIMAL)||(LA102_0>=LOGICAL_OR && LA102_0<=SHA1)||(LA102_0>=SHA256 && LA102_0<=GROUP_CONCAT)||LA102_0==DOUBLE||LA102_0==256||LA102_0==259||(LA102_0>=262 && LA102_0<=268)) ) {
                        alt102=1;
                    }
                    switch (alt102) {
                        case 1 :
                            // IbmSparqlExtAstWalker.g:890:15: e16= expression
                            {
                            pushFollow(FOLLOW_expression_in_builtInCall6554);
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
                    // IbmSparqlExtAstWalker.g:894:4: ^( STRLEN e15= expression )
                    {
                    match(input,STRLEN,FOLLOW_STRLEN_in_builtInCall6577); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6581);
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
                    // IbmSparqlExtAstWalker.g:896:4: ^( UCASE e16= expression )
                    {
                    match(input,UCASE,FOLLOW_UCASE_in_builtInCall6593); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6597);
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
                    // IbmSparqlExtAstWalker.g:898:4: ^( REPLACE e1= expression e2= expression e3= expression (e4= expression )? )
                    {
                    match(input,REPLACE,FOLLOW_REPLACE_in_builtInCall6609); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6613);
                    e1=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6617);
                    e2=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6621);
                    e3=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       args.add(e1); args.add(e2); args.add(e3); 
                    }
                    // IbmSparqlExtAstWalker.g:900:13: (e4= expression )?
                    int alt103=2;
                    int LA103_0 = input.LA(1);

                    if ( ((LA103_0>=BROKEN_PLUS && LA103_0<=BROKEN_MINUS)||(LA103_0>=VAR && LA103_0<=NOT_EXISTS)||(LA103_0>=STRING && LA103_0<=BOOLEAN)||LA103_0==LTE||(LA103_0>=BIG_INTEGER && LA103_0<=BIG_DECIMAL)||(LA103_0>=LOGICAL_OR && LA103_0<=SHA1)||(LA103_0>=SHA256 && LA103_0<=GROUP_CONCAT)||LA103_0==DOUBLE||LA103_0==256||LA103_0==259||(LA103_0>=262 && LA103_0<=268)) ) {
                        alt103=1;
                    }
                    switch (alt103) {
                        case 1 :
                            // IbmSparqlExtAstWalker.g:900:15: e4= expression
                            {
                            pushFollow(FOLLOW_expression_in_builtInCall6645);
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
                    // IbmSparqlExtAstWalker.g:902:4: ^( LCASE e17= expression )
                    {
                    match(input,LCASE,FOLLOW_LCASE_in_builtInCall6663); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6667);
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
                    // IbmSparqlExtAstWalker.g:904:4: ^( ENCODE_FOR_URI e18= expression )
                    {
                    match(input,ENCODE_FOR_URI,FOLLOW_ENCODE_FOR_URI_in_builtInCall6679); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6683);
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
                    // IbmSparqlExtAstWalker.g:906:4: ^( CONTAINS e19= expression e20= expression )
                    {
                    match(input,CONTAINS,FOLLOW_CONTAINS_in_builtInCall6695); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6699);
                    e19=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6703);
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
                    // IbmSparqlExtAstWalker.g:908:4: ^( STRSTARTS e21= expression e22= expression )
                    {
                    match(input,STRSTARTS,FOLLOW_STRSTARTS_in_builtInCall6715); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6719);
                    e21=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6723);
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
                    // IbmSparqlExtAstWalker.g:910:4: ^( STRENDS e23= expression e24= expression )
                    {
                    match(input,STRENDS,FOLLOW_STRENDS_in_builtInCall6735); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6739);
                    e23=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6743);
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
                    // IbmSparqlExtAstWalker.g:912:4: ^( STRBEFORE e241= expression e242= expression )
                    {
                    match(input,STRBEFORE,FOLLOW_STRBEFORE_in_builtInCall6755); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6759);
                    e241=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6763);
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
                    // IbmSparqlExtAstWalker.g:914:4: ^( STRAFTER e243= expression e244= expression )
                    {
                    match(input,STRAFTER,FOLLOW_STRAFTER_in_builtInCall6775); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6779);
                    e243=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6783);
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
                    // IbmSparqlExtAstWalker.g:916:4: ^( YEAR e25= expression )
                    {
                    match(input,YEAR,FOLLOW_YEAR_in_builtInCall6795); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6799);
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
                    // IbmSparqlExtAstWalker.g:918:4: ^( MONTH e26= expression )
                    {
                    match(input,MONTH,FOLLOW_MONTH_in_builtInCall6811); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6815);
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
                    // IbmSparqlExtAstWalker.g:920:4: ^( DAY e27= expression )
                    {
                    match(input,DAY,FOLLOW_DAY_in_builtInCall6827); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6831);
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
                    // IbmSparqlExtAstWalker.g:922:4: ^( HOURS e28= expression )
                    {
                    match(input,HOURS,FOLLOW_HOURS_in_builtInCall6843); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6847);
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
                    // IbmSparqlExtAstWalker.g:924:4: ^( MINUTES e29= expression )
                    {
                    match(input,MINUTES,FOLLOW_MINUTES_in_builtInCall6859); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6863);
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
                    // IbmSparqlExtAstWalker.g:926:4: ^( SECONDS e30= expression )
                    {
                    match(input,SECONDS,FOLLOW_SECONDS_in_builtInCall6875); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6879);
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
                    // IbmSparqlExtAstWalker.g:928:4: ^( TIMEZONE e31= expression )
                    {
                    match(input,TIMEZONE,FOLLOW_TIMEZONE_in_builtInCall6891); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6895);
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
                    // IbmSparqlExtAstWalker.g:930:4: ^( TZ e32= expression )
                    {
                    match(input,TZ,FOLLOW_TZ_in_builtInCall6907); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6911);
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
                    // IbmSparqlExtAstWalker.g:932:4: NOW
                    {
                    match(input,NOW,FOLLOW_NOW_in_builtInCall6922); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new BuiltinFunctionExpression(Expression.EBuiltinType.NOW); 
                    }

                    }
                    break;
                case 36 :
                    // IbmSparqlExtAstWalker.g:933:9: UUID
                    {
                    match(input,UUID,FOLLOW_UUID_in_builtInCall6934); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new BuiltinFunctionExpression(Expression.EBuiltinType.UUID); 
                    }

                    }
                    break;
                case 37 :
                    // IbmSparqlExtAstWalker.g:934:9: STRUUID
                    {
                    match(input,STRUUID,FOLLOW_STRUUID_in_builtInCall6946); if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new BuiltinFunctionExpression(Expression.EBuiltinType.STRUUID); 
                    }

                    }
                    break;
                case 38 :
                    // IbmSparqlExtAstWalker.g:935:4: ^( MD5 e33= expression )
                    {
                    match(input,MD5,FOLLOW_MD5_in_builtInCall6955); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6959);
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
                    // IbmSparqlExtAstWalker.g:937:4: ^( SHA1 e34= expression )
                    {
                    match(input,SHA1,FOLLOW_SHA1_in_builtInCall6971); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6975);
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
                    // IbmSparqlExtAstWalker.g:939:4: ^( SHA256 e36= expression )
                    {
                    match(input,SHA256,FOLLOW_SHA256_in_builtInCall6987); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall6991);
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
                    // IbmSparqlExtAstWalker.g:941:4: ^( SHA384 e37= expression )
                    {
                    match(input,SHA384,FOLLOW_SHA384_in_builtInCall7003); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall7007);
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
                    // IbmSparqlExtAstWalker.g:943:4: ^( SHA512 e38= expression )
                    {
                    match(input,SHA512,FOLLOW_SHA512_in_builtInCall7019); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall7023);
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
                    // IbmSparqlExtAstWalker.g:945:4: ^( COALESCE e39= expressionList )
                    {
                    match(input,COALESCE,FOLLOW_COALESCE_in_builtInCall7035); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expressionList_in_builtInCall7039);
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
                    // IbmSparqlExtAstWalker.g:947:4: ^( IF e40= expression e41= expression e42= expression )
                    {
                    match(input,IF,FOLLOW_IF_in_builtInCall7051); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall7055);
                    e40=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall7059);
                    e41=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall7063);
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
                    // IbmSparqlExtAstWalker.g:949:4: ^( STRLANG e45= expression e46= expression )
                    {
                    match(input,STRLANG,FOLLOW_STRLANG_in_builtInCall7075); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall7079);
                    e45=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall7083);
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
                    // IbmSparqlExtAstWalker.g:951:4: ^( STRDT e47= expression e48= expression )
                    {
                    match(input,STRDT,FOLLOW_STRDT_in_builtInCall7095); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall7099);
                    e47=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall7103);
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
                    // IbmSparqlExtAstWalker.g:953:4: ^( SAMETERM sam1= expression sam2= expression )
                    {
                    match(input,SAMETERM,FOLLOW_SAMETERM_in_builtInCall7115); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall7119);
                    sam1=expression();

                    state._fsp--;
                    if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall7123);
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
                    // IbmSparqlExtAstWalker.g:955:4: ^( ISIRI isi= expression )
                    {
                    match(input,ISIRI,FOLLOW_ISIRI_in_builtInCall7135); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall7139);
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
                    // IbmSparqlExtAstWalker.g:957:4: ^( ISURI isu= expression )
                    {
                    match(input,ISURI,FOLLOW_ISURI_in_builtInCall7151); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall7155);
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
                    // IbmSparqlExtAstWalker.g:959:4: ^( ISBLANK isb= expression )
                    {
                    match(input,ISBLANK,FOLLOW_ISBLANK_in_builtInCall7167); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall7171);
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
                    // IbmSparqlExtAstWalker.g:961:4: ^( ISLITERAL isl= expression )
                    {
                    match(input,ISLITERAL,FOLLOW_ISLITERAL_in_builtInCall7183); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall7187);
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
                    // IbmSparqlExtAstWalker.g:963:4: ^( ISNUMERIC e55= expression )
                    {
                    match(input,ISNUMERIC,FOLLOW_ISNUMERIC_in_builtInCall7199); if (state.failed) return e;

                    match(input, Token.DOWN, null); if (state.failed) return e;
                    pushFollow(FOLLOW_expression_in_builtInCall7203);
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
                    // IbmSparqlExtAstWalker.g:965:4: r= regexExpression
                    {
                    pushFollow(FOLLOW_regexExpression_in_builtInCall7216);
                    r=regexExpression();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = r; 
                    }

                    }
                    break;
                case 54 :
                    // IbmSparqlExtAstWalker.g:966:4: p= existsFunc
                    {
                    pushFollow(FOLLOW_existsFunc_in_builtInCall7225);
                    p=existsFunc();

                    state._fsp--;
                    if (state.failed) return e;
                    if ( state.backtracking==0 ) {
                       e = new BuiltinFunctionExpression(Expression.EBuiltinType.EXISTS, p); 	
                    }

                    }
                    break;
                case 55 :
                    // IbmSparqlExtAstWalker.g:967:4: p= notExistsFunc
                    {
                    pushFollow(FOLLOW_notExistsFunc_in_builtInCall7234);
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
    // IbmSparqlExtAstWalker.g:970:1: regexExpression returns [Expression e] : ^( REGEX e1= expression e2= expression (e3= expression )? ) ;
    public final Expression regexExpression() throws RecognitionException {
        Expression e = null;

        Expression e1 = null;

        Expression e2 = null;

        Expression e3 = null;


        try {
            // IbmSparqlExtAstWalker.g:971:2: ( ^( REGEX e1= expression e2= expression (e3= expression )? ) )
            // IbmSparqlExtAstWalker.g:971:6: ^( REGEX e1= expression e2= expression (e3= expression )? )
            {
            match(input,REGEX,FOLLOW_REGEX_in_regexExpression7257); if (state.failed) return e;

            match(input, Token.DOWN, null); if (state.failed) return e;
            pushFollow(FOLLOW_expression_in_regexExpression7261);
            e1=expression();

            state._fsp--;
            if (state.failed) return e;
            pushFollow(FOLLOW_expression_in_regexExpression7265);
            e2=expression();

            state._fsp--;
            if (state.failed) return e;
            // IbmSparqlExtAstWalker.g:971:44: (e3= expression )?
            int alt105=2;
            int LA105_0 = input.LA(1);

            if ( ((LA105_0>=BROKEN_PLUS && LA105_0<=BROKEN_MINUS)||(LA105_0>=VAR && LA105_0<=NOT_EXISTS)||(LA105_0>=STRING && LA105_0<=BOOLEAN)||LA105_0==LTE||(LA105_0>=BIG_INTEGER && LA105_0<=BIG_DECIMAL)||(LA105_0>=LOGICAL_OR && LA105_0<=SHA1)||(LA105_0>=SHA256 && LA105_0<=GROUP_CONCAT)||LA105_0==DOUBLE||LA105_0==256||LA105_0==259||(LA105_0>=262 && LA105_0<=268)) ) {
                alt105=1;
            }
            switch (alt105) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:971:44: e3= expression
                    {
                    pushFollow(FOLLOW_expression_in_regexExpression7269);
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
    // IbmSparqlExtAstWalker.g:975:1: existsFunc returns [Pattern p] : ^( EXISTS g= groupGraphPattern[false] ) ;
    public final Pattern existsFunc() throws RecognitionException {
        Pattern p = null;

        Pattern g = null;


        try {
            // IbmSparqlExtAstWalker.g:976:2: ( ^( EXISTS g= groupGraphPattern[false] ) )
            // IbmSparqlExtAstWalker.g:976:6: ^( EXISTS g= groupGraphPattern[false] )
            {
            match(input,EXISTS,FOLLOW_EXISTS_in_existsFunc7301); if (state.failed) return p;

            match(input, Token.DOWN, null); if (state.failed) return p;
            pushFollow(FOLLOW_groupGraphPattern_in_existsFunc7305);
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
    // IbmSparqlExtAstWalker.g:980:1: notExistsFunc returns [Pattern p] : ^( NOT_EXISTS g= groupGraphPattern[false] ) ;
    public final Pattern notExistsFunc() throws RecognitionException {
        Pattern p = null;

        Pattern g = null;


        try {
            // IbmSparqlExtAstWalker.g:981:2: ( ^( NOT_EXISTS g= groupGraphPattern[false] ) )
            // IbmSparqlExtAstWalker.g:981:6: ^( NOT_EXISTS g= groupGraphPattern[false] )
            {
            match(input,NOT_EXISTS,FOLLOW_NOT_EXISTS_in_notExistsFunc7336); if (state.failed) return p;

            match(input, Token.DOWN, null); if (state.failed) return p;
            pushFollow(FOLLOW_groupGraphPattern_in_notExistsFunc7340);
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
    // IbmSparqlExtAstWalker.g:985:1: aggregate returns [AggregateExpression a] : ( ^( COUNT ( DISTINCT )? (e1= expression | '*' ) ) | ^( SUM ( DISTINCT )? e2= expression ) | ^( MIN ( DISTINCT )? e3= expression ) | ^( MAX ( DISTINCT )? e4= expression ) | ^( AVG ( DISTINCT )? e5= expression ) | ^( SAMPLE ( DISTINCT )? e6= expression ) | ^( GROUP_CONCAT ( DISTINCT )? e7= expression ( ^( SEPARATOR s= string ) )? ) );
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
            // IbmSparqlExtAstWalker.g:989:2: ( ^( COUNT ( DISTINCT )? (e1= expression | '*' ) ) | ^( SUM ( DISTINCT )? e2= expression ) | ^( MIN ( DISTINCT )? e3= expression ) | ^( MAX ( DISTINCT )? e4= expression ) | ^( AVG ( DISTINCT )? e5= expression ) | ^( SAMPLE ( DISTINCT )? e6= expression ) | ^( GROUP_CONCAT ( DISTINCT )? e7= expression ( ^( SEPARATOR s= string ) )? ) )
            int alt115=7;
            switch ( input.LA(1) ) {
            case COUNT:
                {
                alt115=1;
                }
                break;
            case SUM:
                {
                alt115=2;
                }
                break;
            case MIN:
                {
                alt115=3;
                }
                break;
            case MAX:
                {
                alt115=4;
                }
                break;
            case AVG:
                {
                alt115=5;
                }
                break;
            case SAMPLE:
                {
                alt115=6;
                }
                break;
            case GROUP_CONCAT:
                {
                alt115=7;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return a;}
                NoViableAltException nvae =
                    new NoViableAltException("", 115, 0, input);

                throw nvae;
            }

            switch (alt115) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:990:3: ^( COUNT ( DISTINCT )? (e1= expression | '*' ) )
                    {
                    match(input,COUNT,FOLLOW_COUNT_in_aggregate7375); if (state.failed) return a;

                    if ( state.backtracking==0 ) {
                       a.setAggregationType(AggregateExpression.EType.COUNT);  	
                    }

                    match(input, Token.DOWN, null); if (state.failed) return a;
                    // IbmSparqlExtAstWalker.g:991:4: ( DISTINCT )?
                    int alt106=2;
                    int LA106_0 = input.LA(1);

                    if ( (LA106_0==DISTINCT) ) {
                        alt106=1;
                    }
                    switch (alt106) {
                        case 1 :
                            // IbmSparqlExtAstWalker.g:991:5: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregate7387); if (state.failed) return a;
                            if ( state.backtracking==0 ) {
                               a.isDistinct(true);	
                            }

                            }
                            break;

                    }

                    // IbmSparqlExtAstWalker.g:992:4: (e1= expression | '*' )
                    int alt107=2;
                    int LA107_0 = input.LA(1);

                    if ( ((LA107_0>=BROKEN_PLUS && LA107_0<=BROKEN_MINUS)||(LA107_0>=VAR && LA107_0<=NOT_EXISTS)||(LA107_0>=STRING && LA107_0<=BOOLEAN)||LA107_0==LTE||(LA107_0>=BIG_INTEGER && LA107_0<=BIG_DECIMAL)||(LA107_0>=LOGICAL_OR && LA107_0<=SHA1)||(LA107_0>=SHA256 && LA107_0<=GROUP_CONCAT)||LA107_0==DOUBLE||LA107_0==259||(LA107_0>=262 && LA107_0<=268)) ) {
                        alt107=1;
                    }
                    else if ( (LA107_0==256) ) {
                        int LA107_2 = input.LA(2);

                        if ( (LA107_2==DOWN) ) {
                            alt107=1;
                        }
                        else if ( (LA107_2==UP) ) {
                            alt107=2;
                        }
                        else {
                            if (state.backtracking>0) {state.failed=true; return a;}
                            NoViableAltException nvae =
                                new NoViableAltException("", 107, 2, input);

                            throw nvae;
                        }
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return a;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 107, 0, input);

                        throw nvae;
                    }
                    switch (alt107) {
                        case 1 :
                            // IbmSparqlExtAstWalker.g:992:6: e1= expression
                            {
                            pushFollow(FOLLOW_expression_in_aggregate7444);
                            e1=expression();

                            state._fsp--;
                            if (state.failed) return a;
                            if ( state.backtracking==0 ) {
                               a.setArgs(e1);		
                            }

                            }
                            break;
                        case 2 :
                            // IbmSparqlExtAstWalker.g:993:6: '*'
                            {
                            match(input,256,FOLLOW_256_in_aggregate7454); if (state.failed) return a;

                            }
                            break;

                    }


                    match(input, Token.UP, null); if (state.failed) return a;

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:996:5: ^( SUM ( DISTINCT )? e2= expression )
                    {
                    match(input,SUM,FOLLOW_SUM_in_aggregate7474); if (state.failed) return a;

                    if ( state.backtracking==0 ) {
                       a.setAggregationType(AggregateExpression.EType.SUM);  	
                    }

                    match(input, Token.DOWN, null); if (state.failed) return a;
                    // IbmSparqlExtAstWalker.g:997:4: ( DISTINCT )?
                    int alt108=2;
                    int LA108_0 = input.LA(1);

                    if ( (LA108_0==DISTINCT) ) {
                        alt108=1;
                    }
                    switch (alt108) {
                        case 1 :
                            // IbmSparqlExtAstWalker.g:997:5: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregate7486); if (state.failed) return a;
                            if ( state.backtracking==0 ) {
                               a.isDistinct(true);	
                            }

                            }
                            break;

                    }

                    pushFollow(FOLLOW_expression_in_aggregate7503);
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
                    // IbmSparqlExtAstWalker.g:1000:4: ^( MIN ( DISTINCT )? e3= expression )
                    {
                    match(input,MIN,FOLLOW_MIN_in_aggregate7516); if (state.failed) return a;

                    if ( state.backtracking==0 ) {
                       a.setAggregationType(AggregateExpression.EType.MIN);  	
                    }

                    match(input, Token.DOWN, null); if (state.failed) return a;
                    // IbmSparqlExtAstWalker.g:1001:4: ( DISTINCT )?
                    int alt109=2;
                    int LA109_0 = input.LA(1);

                    if ( (LA109_0==DISTINCT) ) {
                        alt109=1;
                    }
                    switch (alt109) {
                        case 1 :
                            // IbmSparqlExtAstWalker.g:1001:5: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregate7529); if (state.failed) return a;
                            if ( state.backtracking==0 ) {
                               a.isDistinct(true);	
                            }

                            }
                            break;

                    }

                    pushFollow(FOLLOW_expression_in_aggregate7545);
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
                    // IbmSparqlExtAstWalker.g:1004:4: ^( MAX ( DISTINCT )? e4= expression )
                    {
                    match(input,MAX,FOLLOW_MAX_in_aggregate7558); if (state.failed) return a;

                    if ( state.backtracking==0 ) {
                       a.setAggregationType(AggregateExpression.EType.MAX);  	
                    }

                    match(input, Token.DOWN, null); if (state.failed) return a;
                    // IbmSparqlExtAstWalker.g:1005:4: ( DISTINCT )?
                    int alt110=2;
                    int LA110_0 = input.LA(1);

                    if ( (LA110_0==DISTINCT) ) {
                        alt110=1;
                    }
                    switch (alt110) {
                        case 1 :
                            // IbmSparqlExtAstWalker.g:1005:5: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregate7571); if (state.failed) return a;
                            if ( state.backtracking==0 ) {
                               a.isDistinct(true);	
                            }

                            }
                            break;

                    }

                    pushFollow(FOLLOW_expression_in_aggregate7588);
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
                    // IbmSparqlExtAstWalker.g:1008:4: ^( AVG ( DISTINCT )? e5= expression )
                    {
                    match(input,AVG,FOLLOW_AVG_in_aggregate7601); if (state.failed) return a;

                    if ( state.backtracking==0 ) {
                       a.setAggregationType(AggregateExpression.EType.AVG);  	
                    }

                    match(input, Token.DOWN, null); if (state.failed) return a;
                    // IbmSparqlExtAstWalker.g:1009:4: ( DISTINCT )?
                    int alt111=2;
                    int LA111_0 = input.LA(1);

                    if ( (LA111_0==DISTINCT) ) {
                        alt111=1;
                    }
                    switch (alt111) {
                        case 1 :
                            // IbmSparqlExtAstWalker.g:1009:5: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregate7614); if (state.failed) return a;
                            if ( state.backtracking==0 ) {
                               a.isDistinct(true);	
                            }

                            }
                            break;

                    }

                    pushFollow(FOLLOW_expression_in_aggregate7631);
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
                    // IbmSparqlExtAstWalker.g:1012:4: ^( SAMPLE ( DISTINCT )? e6= expression )
                    {
                    match(input,SAMPLE,FOLLOW_SAMPLE_in_aggregate7644); if (state.failed) return a;

                    if ( state.backtracking==0 ) {
                       a.setAggregationType(AggregateExpression.EType.SAMPLE);  
                    }

                    match(input, Token.DOWN, null); if (state.failed) return a;
                    // IbmSparqlExtAstWalker.g:1013:4: ( DISTINCT )?
                    int alt112=2;
                    int LA112_0 = input.LA(1);

                    if ( (LA112_0==DISTINCT) ) {
                        alt112=1;
                    }
                    switch (alt112) {
                        case 1 :
                            // IbmSparqlExtAstWalker.g:1013:5: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregate7656); if (state.failed) return a;
                            if ( state.backtracking==0 ) {
                               a.isDistinct(true);	
                            }

                            }
                            break;

                    }

                    pushFollow(FOLLOW_expression_in_aggregate7673);
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
                    // IbmSparqlExtAstWalker.g:1016:5: ^( GROUP_CONCAT ( DISTINCT )? e7= expression ( ^( SEPARATOR s= string ) )? )
                    {
                    match(input,GROUP_CONCAT,FOLLOW_GROUP_CONCAT_in_aggregate7687); if (state.failed) return a;

                    if ( state.backtracking==0 ) {
                       a.setAggregationType(AggregateExpression.EType.GROUP_CONCAT); 
                    }

                    match(input, Token.DOWN, null); if (state.failed) return a;
                    // IbmSparqlExtAstWalker.g:1017:4: ( DISTINCT )?
                    int alt113=2;
                    int LA113_0 = input.LA(1);

                    if ( (LA113_0==DISTINCT) ) {
                        alt113=1;
                    }
                    switch (alt113) {
                        case 1 :
                            // IbmSparqlExtAstWalker.g:1017:5: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregate7699); if (state.failed) return a;
                            if ( state.backtracking==0 ) {
                               a.isDistinct(true);	
                            }

                            }
                            break;

                    }

                    pushFollow(FOLLOW_expression_in_aggregate7714);
                    e7=expression();

                    state._fsp--;
                    if (state.failed) return a;
                    if ( state.backtracking==0 ) {
                       a.setArgs(e7);		
                    }
                    // IbmSparqlExtAstWalker.g:1019:4: ( ^( SEPARATOR s= string ) )?
                    int alt114=2;
                    int LA114_0 = input.LA(1);

                    if ( (LA114_0==SEPARATOR) ) {
                        alt114=1;
                    }
                    switch (alt114) {
                        case 1 :
                            // IbmSparqlExtAstWalker.g:1019:5: ^( SEPARATOR s= string )
                            {
                            match(input,SEPARATOR,FOLLOW_SEPARATOR_in_aggregate7725); if (state.failed) return a;

                            match(input, Token.DOWN, null); if (state.failed) return a;
                            pushFollow(FOLLOW_string_in_aggregate7729);
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
    // IbmSparqlExtAstWalker.g:1023:1: iRIFunction returns [FunctionCall f] : ^( FUNCTION i= iRIref (a= argList )? ) ;
    public final FunctionCall iRIFunction() throws RecognitionException {
        FunctionCall f = null;

        IRI i = null;

        List<Expression> a = null;


        try {
            // IbmSparqlExtAstWalker.g:1024:2: ( ^( FUNCTION i= iRIref (a= argList )? ) )
            // IbmSparqlExtAstWalker.g:1024:6: ^( FUNCTION i= iRIref (a= argList )? )
            {
            match(input,FUNCTION,FOLLOW_FUNCTION_in_iRIFunction7758); if (state.failed) return f;

            match(input, Token.DOWN, null); if (state.failed) return f;
            pushFollow(FOLLOW_iRIref_in_iRIFunction7768);
            i=iRIref();

            state._fsp--;
            if (state.failed) return f;
            if ( state.backtracking==0 ) {
               f = new FunctionCall(i); 
            }
            // IbmSparqlExtAstWalker.g:1026:5: (a= argList )?
            int alt116=2;
            int LA116_0 = input.LA(1);

            if ( ((LA116_0>=BROKEN_PLUS && LA116_0<=NIL)||(LA116_0>=VAR && LA116_0<=NOT_EXISTS)||(LA116_0>=STRING && LA116_0<=BOOLEAN)||LA116_0==LTE||(LA116_0>=BIG_INTEGER && LA116_0<=BIG_DECIMAL)||LA116_0==DISTINCT||(LA116_0>=LOGICAL_OR && LA116_0<=SHA1)||(LA116_0>=SHA256 && LA116_0<=GROUP_CONCAT)||LA116_0==DOUBLE||LA116_0==256||LA116_0==259||(LA116_0>=262 && LA116_0<=268)) ) {
                alt116=1;
            }
            switch (alt116) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:1026:6: a= argList
                    {
                    pushFollow(FOLLOW_argList_in_iRIFunction7780);
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
    // IbmSparqlExtAstWalker.g:1030:1: rDFLiteral returns [StringLiteral sl] : s= string (l= LANGTAG | ( '^^' i= iRIref ) )? ;
    public final StringLiteral rDFLiteral() throws RecognitionException {
        StringLiteral sl = null;

        XTree l=null;
        String s = null;

        IRI i = null;


        try {
            // IbmSparqlExtAstWalker.g:1031:2: (s= string (l= LANGTAG | ( '^^' i= iRIref ) )? )
            // IbmSparqlExtAstWalker.g:1031:6: s= string (l= LANGTAG | ( '^^' i= iRIref ) )?
            {
            pushFollow(FOLLOW_string_in_rDFLiteral7812);
            s=string();

            state._fsp--;
            if (state.failed) return sl;
            if ( state.backtracking==0 ) {
               sl = new StringLiteral(s);     
            }
            // IbmSparqlExtAstWalker.g:1032:3: (l= LANGTAG | ( '^^' i= iRIref ) )?
            int alt117=3;
            int LA117_0 = input.LA(1);

            if ( (LA117_0==LANGTAG) ) {
                alt117=1;
            }
            else if ( (LA117_0==269) ) {
                alt117=2;
            }
            switch (alt117) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:1032:5: l= LANGTAG
                    {
                    l=(XTree)match(input,LANGTAG,FOLLOW_LANGTAG_in_rDFLiteral7827); if (state.failed) return sl;
                    if ( state.backtracking==0 ) {
                       sl.setLanguage((l!=null?l.getText():null));        
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:1033:5: ( '^^' i= iRIref )
                    {
                    // IbmSparqlExtAstWalker.g:1033:5: ( '^^' i= iRIref )
                    // IbmSparqlExtAstWalker.g:1033:7: '^^' i= iRIref
                    {
                    match(input,269,FOLLOW_269_in_rDFLiteral7841); if (state.failed) return sl;
                    pushFollow(FOLLOW_iRIref_in_rDFLiteral7845);
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
    // IbmSparqlExtAstWalker.g:1037:1: numericLiteral returns [Constant n] : (n1= numericLiteralUnsigned | n2= numericLiteralPositive | n3= numericLiteralNegative );
    public final Constant numericLiteral() throws RecognitionException {
        Constant n = null;

        Constant n1 = null;

        Constant n2 = null;

        Constant n3 = null;


        try {
            // IbmSparqlExtAstWalker.g:1038:2: (n1= numericLiteralUnsigned | n2= numericLiteralPositive | n3= numericLiteralNegative )
            int alt118=3;
            alt118 = dfa118.predict(input);
            switch (alt118) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:1038:6: n1= numericLiteralUnsigned
                    {
                    pushFollow(FOLLOW_numericLiteralUnsigned_in_numericLiteral7876);
                    n1=numericLiteralUnsigned();

                    state._fsp--;
                    if (state.failed) return n;
                    if ( state.backtracking==0 ) {
                       n = n1; 
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:1039:5: n2= numericLiteralPositive
                    {
                    pushFollow(FOLLOW_numericLiteralPositive_in_numericLiteral7886);
                    n2=numericLiteralPositive();

                    state._fsp--;
                    if (state.failed) return n;
                    if ( state.backtracking==0 ) {
                       n = n2; 
                    }

                    }
                    break;
                case 3 :
                    // IbmSparqlExtAstWalker.g:1040:5: n3= numericLiteralNegative
                    {
                    pushFollow(FOLLOW_numericLiteralNegative_in_numericLiteral7896);
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
    // IbmSparqlExtAstWalker.g:1043:1: numericLiteralUnsigned returns [Constant c] : ( ^( BIG_INTEGER i= INTEGER ) | ^( BIG_DECIMAL d1= DECIMAL ) | ^( DOUBLE d2= DOUBLE ) );
    public final Constant numericLiteralUnsigned() throws RecognitionException {
        Constant c = null;

        XTree i=null;
        XTree d1=null;
        XTree d2=null;

        try {
            // IbmSparqlExtAstWalker.g:1044:2: ( ^( BIG_INTEGER i= INTEGER ) | ^( BIG_DECIMAL d1= DECIMAL ) | ^( DOUBLE d2= DOUBLE ) )
            int alt119=3;
            switch ( input.LA(1) ) {
            case BIG_INTEGER:
                {
                alt119=1;
                }
                break;
            case BIG_DECIMAL:
                {
                alt119=2;
                }
                break;
            case DOUBLE:
                {
                alt119=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return c;}
                NoViableAltException nvae =
                    new NoViableAltException("", 119, 0, input);

                throw nvae;
            }

            switch (alt119) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:1044:6: ^( BIG_INTEGER i= INTEGER )
                    {
                    match(input,BIG_INTEGER,FOLLOW_BIG_INTEGER_in_numericLiteralUnsigned7918); if (state.failed) return c;

                    match(input, Token.DOWN, null); if (state.failed) return c;
                    i=(XTree)match(input,INTEGER,FOLLOW_INTEGER_in_numericLiteralUnsigned7923); if (state.failed) return c;

                    match(input, Token.UP, null); if (state.failed) return c;
                    if ( state.backtracking==0 ) {
                       c = new Constant((i!=null?i.getText():null), new BigInteger((i!=null?i.getText():null)));		
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:1045:5: ^( BIG_DECIMAL d1= DECIMAL )
                    {
                    match(input,BIG_DECIMAL,FOLLOW_BIG_DECIMAL_in_numericLiteralUnsigned7939); if (state.failed) return c;

                    match(input, Token.DOWN, null); if (state.failed) return c;
                    d1=(XTree)match(input,DECIMAL,FOLLOW_DECIMAL_in_numericLiteralUnsigned7943); if (state.failed) return c;

                    match(input, Token.UP, null); if (state.failed) return c;
                    if ( state.backtracking==0 ) {
                       c = new Constant((d1!=null?d1.getText():null), new BigDecimal((d1!=null?d1.getText():null)));	
                    }

                    }
                    break;
                case 3 :
                    // IbmSparqlExtAstWalker.g:1046:5: ^( DOUBLE d2= DOUBLE )
                    {
                    match(input,DOUBLE,FOLLOW_DOUBLE_in_numericLiteralUnsigned7958); if (state.failed) return c;

                    match(input, Token.DOWN, null); if (state.failed) return c;
                    d2=(XTree)match(input,DOUBLE,FOLLOW_DOUBLE_in_numericLiteralUnsigned7963); if (state.failed) return c;

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
    // IbmSparqlExtAstWalker.g:1049:1: numericLiteralPositive returns [Constant c] : ( ^( BIG_INTEGER i= INTEGER_POSITIVE ) | ^( BIG_DECIMAL d1= DECIMAL_POSITIVE ) | ^( DOUBLE d2= DOUBLE_POSITIVE ) );
    public final Constant numericLiteralPositive() throws RecognitionException {
        Constant c = null;

        XTree i=null;
        XTree d1=null;
        XTree d2=null;

        try {
            // IbmSparqlExtAstWalker.g:1050:2: ( ^( BIG_INTEGER i= INTEGER_POSITIVE ) | ^( BIG_DECIMAL d1= DECIMAL_POSITIVE ) | ^( DOUBLE d2= DOUBLE_POSITIVE ) )
            int alt120=3;
            switch ( input.LA(1) ) {
            case BIG_INTEGER:
                {
                alt120=1;
                }
                break;
            case BIG_DECIMAL:
                {
                alt120=2;
                }
                break;
            case DOUBLE:
                {
                alt120=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return c;}
                NoViableAltException nvae =
                    new NoViableAltException("", 120, 0, input);

                throw nvae;
            }

            switch (alt120) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:1050:6: ^( BIG_INTEGER i= INTEGER_POSITIVE )
                    {
                    match(input,BIG_INTEGER,FOLLOW_BIG_INTEGER_in_numericLiteralPositive7993); if (state.failed) return c;

                    match(input, Token.DOWN, null); if (state.failed) return c;
                    i=(XTree)match(input,INTEGER_POSITIVE,FOLLOW_INTEGER_POSITIVE_in_numericLiteralPositive7998); if (state.failed) return c;

                    match(input, Token.UP, null); if (state.failed) return c;
                    if ( state.backtracking==0 ) {
                       c = new Constant((i!=null?i.getText():null), new BigInteger((i!=null?i.getText():null).substring(1)));		
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:1051:5: ^( BIG_DECIMAL d1= DECIMAL_POSITIVE )
                    {
                    match(input,BIG_DECIMAL,FOLLOW_BIG_DECIMAL_in_numericLiteralPositive8011); if (state.failed) return c;

                    match(input, Token.DOWN, null); if (state.failed) return c;
                    d1=(XTree)match(input,DECIMAL_POSITIVE,FOLLOW_DECIMAL_POSITIVE_in_numericLiteralPositive8015); if (state.failed) return c;

                    match(input, Token.UP, null); if (state.failed) return c;
                    if ( state.backtracking==0 ) {
                       c = new Constant((d1!=null?d1.getText():null), new BigDecimal((d1!=null?d1.getText():null).substring(1)));	
                    }

                    }
                    break;
                case 3 :
                    // IbmSparqlExtAstWalker.g:1052:5: ^( DOUBLE d2= DOUBLE_POSITIVE )
                    {
                    match(input,DOUBLE,FOLLOW_DOUBLE_in_numericLiteralPositive8028); if (state.failed) return c;

                    match(input, Token.DOWN, null); if (state.failed) return c;
                    d2=(XTree)match(input,DOUBLE_POSITIVE,FOLLOW_DOUBLE_POSITIVE_in_numericLiteralPositive8033); if (state.failed) return c;

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
    // IbmSparqlExtAstWalker.g:1055:1: numericLiteralNegative returns [Constant c] : ( ^( BIG_INTEGER i= INTEGER_NEGATIVE ) | ^( BIG_DECIMAL d1= DECIMAL_NEGATIVE ) | ^( DOUBLE d2= DOUBLE_NEGATIVE ) );
    public final Constant numericLiteralNegative() throws RecognitionException {
        Constant c = null;

        XTree i=null;
        XTree d1=null;
        XTree d2=null;

        try {
            // IbmSparqlExtAstWalker.g:1056:2: ( ^( BIG_INTEGER i= INTEGER_NEGATIVE ) | ^( BIG_DECIMAL d1= DECIMAL_NEGATIVE ) | ^( DOUBLE d2= DOUBLE_NEGATIVE ) )
            int alt121=3;
            switch ( input.LA(1) ) {
            case BIG_INTEGER:
                {
                alt121=1;
                }
                break;
            case BIG_DECIMAL:
                {
                alt121=2;
                }
                break;
            case DOUBLE:
                {
                alt121=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return c;}
                NoViableAltException nvae =
                    new NoViableAltException("", 121, 0, input);

                throw nvae;
            }

            switch (alt121) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:1056:6: ^( BIG_INTEGER i= INTEGER_NEGATIVE )
                    {
                    match(input,BIG_INTEGER,FOLLOW_BIG_INTEGER_in_numericLiteralNegative8058); if (state.failed) return c;

                    match(input, Token.DOWN, null); if (state.failed) return c;
                    i=(XTree)match(input,INTEGER_NEGATIVE,FOLLOW_INTEGER_NEGATIVE_in_numericLiteralNegative8063); if (state.failed) return c;

                    match(input, Token.UP, null); if (state.failed) return c;
                    if ( state.backtracking==0 ) {
                       c = new Constant((i!=null?i.getText():null), new BigInteger((i!=null?i.getText():null)));		
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:1057:5: ^( BIG_DECIMAL d1= DECIMAL_NEGATIVE )
                    {
                    match(input,BIG_DECIMAL,FOLLOW_BIG_DECIMAL_in_numericLiteralNegative8076); if (state.failed) return c;

                    match(input, Token.DOWN, null); if (state.failed) return c;
                    d1=(XTree)match(input,DECIMAL_NEGATIVE,FOLLOW_DECIMAL_NEGATIVE_in_numericLiteralNegative8080); if (state.failed) return c;

                    match(input, Token.UP, null); if (state.failed) return c;
                    if ( state.backtracking==0 ) {
                       c = new Constant((d1!=null?d1.getText():null), new BigDecimal((d1!=null?d1.getText():null)));	
                    }

                    }
                    break;
                case 3 :
                    // IbmSparqlExtAstWalker.g:1058:5: ^( DOUBLE d2= DOUBLE_NEGATIVE )
                    {
                    match(input,DOUBLE,FOLLOW_DOUBLE_in_numericLiteralNegative8093); if (state.failed) return c;

                    match(input, Token.DOWN, null); if (state.failed) return c;
                    d2=(XTree)match(input,DOUBLE_NEGATIVE,FOLLOW_DOUBLE_NEGATIVE_in_numericLiteralNegative8098); if (state.failed) return c;

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
    // IbmSparqlExtAstWalker.g:1061:1: booleanLiteral returns [Boolean b] : ( ^( BOOLEAN TRUE ) | ^( BOOLEAN FALSE ) );
    public final Boolean booleanLiteral() throws RecognitionException {
        Boolean b = null;

        try {
            // IbmSparqlExtAstWalker.g:1062:2: ( ^( BOOLEAN TRUE ) | ^( BOOLEAN FALSE ) )
            int alt122=2;
            int LA122_0 = input.LA(1);

            if ( (LA122_0==BOOLEAN) ) {
                int LA122_1 = input.LA(2);

                if ( (LA122_1==DOWN) ) {
                    int LA122_2 = input.LA(3);

                    if ( (LA122_2==TRUE) ) {
                        alt122=1;
                    }
                    else if ( (LA122_2==FALSE) ) {
                        alt122=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return b;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 122, 2, input);

                        throw nvae;
                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return b;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 122, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return b;}
                NoViableAltException nvae =
                    new NoViableAltException("", 122, 0, input);

                throw nvae;
            }
            switch (alt122) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:1062:6: ^( BOOLEAN TRUE )
                    {
                    match(input,BOOLEAN,FOLLOW_BOOLEAN_in_booleanLiteral8122); if (state.failed) return b;

                    match(input, Token.DOWN, null); if (state.failed) return b;
                    match(input,TRUE,FOLLOW_TRUE_in_booleanLiteral8124); if (state.failed) return b;

                    match(input, Token.UP, null); if (state.failed) return b;
                    if ( state.backtracking==0 ) {
                       b = Boolean.TRUE;  
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:1063:6: ^( BOOLEAN FALSE )
                    {
                    match(input,BOOLEAN,FOLLOW_BOOLEAN_in_booleanLiteral8139); if (state.failed) return b;

                    match(input, Token.DOWN, null); if (state.failed) return b;
                    match(input,FALSE,FOLLOW_FALSE_in_booleanLiteral8141); if (state.failed) return b;

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
    // IbmSparqlExtAstWalker.g:1066:1: string returns [String s] : ( ^( STRING s1= STRING_LITERAL1 ) | ^( STRING s2= STRING_LITERAL2 ) | ^( STRING s3= STRING_LITERAL_LONG1 ) | ^( STRING s4= STRING_LITERAL_LONG2 ) );
    public final String string() throws RecognitionException {
        String s = null;

        XTree s1=null;
        XTree s2=null;
        XTree s3=null;
        XTree s4=null;

        try {
            // IbmSparqlExtAstWalker.g:1067:2: ( ^( STRING s1= STRING_LITERAL1 ) | ^( STRING s2= STRING_LITERAL2 ) | ^( STRING s3= STRING_LITERAL_LONG1 ) | ^( STRING s4= STRING_LITERAL_LONG2 ) )
            int alt123=4;
            int LA123_0 = input.LA(1);

            if ( (LA123_0==STRING) ) {
                int LA123_1 = input.LA(2);

                if ( (LA123_1==DOWN) ) {
                    switch ( input.LA(3) ) {
                    case STRING_LITERAL1:
                        {
                        alt123=1;
                        }
                        break;
                    case STRING_LITERAL2:
                        {
                        alt123=2;
                        }
                        break;
                    case STRING_LITERAL_LONG1:
                        {
                        alt123=3;
                        }
                        break;
                    case STRING_LITERAL_LONG2:
                        {
                        alt123=4;
                        }
                        break;
                    default:
                        if (state.backtracking>0) {state.failed=true; return s;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 123, 2, input);

                        throw nvae;
                    }

                }
                else {
                    if (state.backtracking>0) {state.failed=true; return s;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 123, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return s;}
                NoViableAltException nvae =
                    new NoViableAltException("", 123, 0, input);

                throw nvae;
            }
            switch (alt123) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:1067:4: ^( STRING s1= STRING_LITERAL1 )
                    {
                    match(input,STRING,FOLLOW_STRING_in_string8164); if (state.failed) return s;

                    match(input, Token.DOWN, null); if (state.failed) return s;
                    s1=(XTree)match(input,STRING_LITERAL1,FOLLOW_STRING_LITERAL1_in_string8168); if (state.failed) return s;

                    match(input, Token.UP, null); if (state.failed) return s;
                    if ( state.backtracking==0 ) {
                       s = new String((s1!=null?s1.getText():null)); 
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:1068:4: ^( STRING s2= STRING_LITERAL2 )
                    {
                    match(input,STRING,FOLLOW_STRING_in_string8185); if (state.failed) return s;

                    match(input, Token.DOWN, null); if (state.failed) return s;
                    s2=(XTree)match(input,STRING_LITERAL2,FOLLOW_STRING_LITERAL2_in_string8189); if (state.failed) return s;

                    match(input, Token.UP, null); if (state.failed) return s;
                    if ( state.backtracking==0 ) {
                       s = new String((s2!=null?s2.getText():null)); 
                    }

                    }
                    break;
                case 3 :
                    // IbmSparqlExtAstWalker.g:1069:4: ^( STRING s3= STRING_LITERAL_LONG1 )
                    {
                    match(input,STRING,FOLLOW_STRING_in_string8206); if (state.failed) return s;

                    match(input, Token.DOWN, null); if (state.failed) return s;
                    s3=(XTree)match(input,STRING_LITERAL_LONG1,FOLLOW_STRING_LITERAL_LONG1_in_string8210); if (state.failed) return s;

                    match(input, Token.UP, null); if (state.failed) return s;
                    if ( state.backtracking==0 ) {
                       s = new String((s3!=null?s3.getText():null)); 
                    }

                    }
                    break;
                case 4 :
                    // IbmSparqlExtAstWalker.g:1070:4: ^( STRING s4= STRING_LITERAL_LONG2 )
                    {
                    match(input,STRING,FOLLOW_STRING_in_string8222); if (state.failed) return s;

                    match(input, Token.DOWN, null); if (state.failed) return s;
                    s4=(XTree)match(input,STRING_LITERAL_LONG2,FOLLOW_STRING_LITERAL_LONG2_in_string8226); if (state.failed) return s;

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
    // IbmSparqlExtAstWalker.g:1073:1: iRIref returns [IRI r] : ( ^( IRI i= IRI_REF ) | p= prefixedName );
    public final IRI iRIref() throws RecognitionException {
        IRI r = null;

        XTree i=null;
        String p = null;


        try {
            // IbmSparqlExtAstWalker.g:1074:2: ( ^( IRI i= IRI_REF ) | p= prefixedName )
            int alt124=2;
            int LA124_0 = input.LA(1);

            if ( (LA124_0==IRI) ) {
                alt124=1;
            }
            else if ( ((LA124_0>=PREFIXED_NAME && LA124_0<=PREFIXED_NS)) ) {
                alt124=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return r;}
                NoViableAltException nvae =
                    new NoViableAltException("", 124, 0, input);

                throw nvae;
            }
            switch (alt124) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:1074:4: ^( IRI i= IRI_REF )
                    {
                    match(input,IRI,FOLLOW_IRI_in_iRIref8250); if (state.failed) return r;

                    match(input, Token.DOWN, null); if (state.failed) return r;
                    i=(XTree)match(input,IRI_REF,FOLLOW_IRI_REF_in_iRIref8254); if (state.failed) return r;

                    match(input, Token.UP, null); if (state.failed) return r;
                    if ( state.backtracking==0 ) {
                       r = new IRI((i!=null?i.getText():null).substring(1, (i!=null?i.getText():null).length()-1)); 	
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:1075:6: p= prefixedName
                    {
                    pushFollow(FOLLOW_prefixedName_in_iRIref8266);
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
    // IbmSparqlExtAstWalker.g:1078:1: prefixedName returns [String s] : ( ^( PREFIXED_NAME n1= PNAME_LN ) | ^( PREFIXED_NS n2= PNAME_NS ) );
    public final String prefixedName() throws RecognitionException {
        String s = null;

        XTree n1=null;
        XTree n2=null;

        try {
            // IbmSparqlExtAstWalker.g:1079:2: ( ^( PREFIXED_NAME n1= PNAME_LN ) | ^( PREFIXED_NS n2= PNAME_NS ) )
            int alt125=2;
            int LA125_0 = input.LA(1);

            if ( (LA125_0==PREFIXED_NAME) ) {
                alt125=1;
            }
            else if ( (LA125_0==PREFIXED_NS) ) {
                alt125=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return s;}
                NoViableAltException nvae =
                    new NoViableAltException("", 125, 0, input);

                throw nvae;
            }
            switch (alt125) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:1079:4: ^( PREFIXED_NAME n1= PNAME_LN )
                    {
                    match(input,PREFIXED_NAME,FOLLOW_PREFIXED_NAME_in_prefixedName8288); if (state.failed) return s;

                    match(input, Token.DOWN, null); if (state.failed) return s;
                    n1=(XTree)match(input,PNAME_LN,FOLLOW_PNAME_LN_in_prefixedName8292); if (state.failed) return s;

                    match(input, Token.UP, null); if (state.failed) return s;
                    if ( state.backtracking==0 ) {
                       s = new String(n1.getText()); 
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:1080:4: ^( PREFIXED_NS n2= PNAME_NS )
                    {
                    match(input,PREFIXED_NS,FOLLOW_PREFIXED_NS_in_prefixedName8301); if (state.failed) return s;

                    match(input, Token.DOWN, null); if (state.failed) return s;
                    n2=(XTree)match(input,PNAME_NS,FOLLOW_PNAME_NS_in_prefixedName8305); if (state.failed) return s;

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
    // IbmSparqlExtAstWalker.g:1083:1: blankNode returns [BlankNode bn] : (b= BLANK_NODE_LABEL | ^( ANNON t= OPEN_SQ_BRACKET ) );
    public final BlankNode blankNode() throws RecognitionException {
        BlankNode bn = null;

        XTree b=null;
        XTree t=null;

        try {
            // IbmSparqlExtAstWalker.g:1084:2: (b= BLANK_NODE_LABEL | ^( ANNON t= OPEN_SQ_BRACKET ) )
            int alt126=2;
            int LA126_0 = input.LA(1);

            if ( (LA126_0==BLANK_NODE_LABEL) ) {
                alt126=1;
            }
            else if ( (LA126_0==ANNON) ) {
                alt126=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return bn;}
                NoViableAltException nvae =
                    new NoViableAltException("", 126, 0, input);

                throw nvae;
            }
            switch (alt126) {
                case 1 :
                    // IbmSparqlExtAstWalker.g:1084:6: b= BLANK_NODE_LABEL
                    {
                    b=(XTree)match(input,BLANK_NODE_LABEL,FOLLOW_BLANK_NODE_LABEL_in_blankNode8329); if (state.failed) return bn;
                    if ( state.backtracking==0 ) {
                       bn = new BlankNode(b.getText()); 
                    }

                    }
                    break;
                case 2 :
                    // IbmSparqlExtAstWalker.g:1085:6: ^( ANNON t= OPEN_SQ_BRACKET )
                    {
                    match(input,ANNON,FOLLOW_ANNON_in_blankNode8340); if (state.failed) return bn;

                    match(input, Token.DOWN, null); if (state.failed) return bn;
                    t=(XTree)match(input,OPEN_SQ_BRACKET,FOLLOW_OPEN_SQ_BRACKET_in_blankNode8344); if (state.failed) return bn;

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
        match(input,NIL,FOLLOW_NIL_in_synpred1_IbmSparqlExtAstWalker3292); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred1_IbmSparqlExtAstWalker

    // $ANTLR start synpred2_IbmSparqlExtAstWalker
    public final void synpred2_IbmSparqlExtAstWalker_fragment() throws RecognitionException {   
        // IbmSparqlExtAstWalker.g:495:81: ( NIL )
        // IbmSparqlExtAstWalker.g:495:83: NIL
        {
        match(input,NIL,FOLLOW_NIL_in_synpred2_IbmSparqlExtAstWalker3314); if (state.failed) return ;

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
    protected DFA101 dfa101 = new DFA101(this);
    protected DFA104 dfa104 = new DFA104(this);
    protected DFA118 dfa118 = new DFA118(this);
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
    static final String DFA101_eotS =
        "\13\uffff";
    static final String DFA101_eofS =
        "\13\uffff";
    static final String DFA101_minS =
        "\1\42\1\uffff\1\2\7\uffff\1\11";
    static final String DFA101_maxS =
        "\1\u00c7\1\uffff\1\2\7\uffff\1\u010c";
    static final String DFA101_acceptS =
        "\1\uffff\1\1\1\uffff\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\uffff";
    static final String DFA101_specialS =
        "\13\uffff}>";
    static final String[] DFA101_transitionS = {
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

    static final short[] DFA101_eot = DFA.unpackEncodedString(DFA101_eotS);
    static final short[] DFA101_eof = DFA.unpackEncodedString(DFA101_eofS);
    static final char[] DFA101_min = DFA.unpackEncodedStringToUnsignedChars(DFA101_minS);
    static final char[] DFA101_max = DFA.unpackEncodedStringToUnsignedChars(DFA101_maxS);
    static final short[] DFA101_accept = DFA.unpackEncodedString(DFA101_acceptS);
    static final short[] DFA101_special = DFA.unpackEncodedString(DFA101_specialS);
    static final short[][] DFA101_transition;

    static {
        int numStates = DFA101_transitionS.length;
        DFA101_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA101_transition[i] = DFA.unpackEncodedString(DFA101_transitionS[i]);
        }
    }

    class DFA101 extends DFA {

        public DFA101(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 101;
            this.eot = DFA101_eot;
            this.eof = DFA101_eof;
            this.min = DFA101_min;
            this.max = DFA101_max;
            this.accept = DFA101_accept;
            this.special = DFA101_special;
            this.transition = DFA101_transition;
        }
        public String getDescription() {
            return "839:1: primaryExpression returns [Expression e] : (e1= builtInCall | i= iRIref | f= iRIFunction | r= rDFLiteral | n= numericLiteral | b= booleanLiteral | v= var | a= aggregate );";
        }
    }
    static final String DFA104_eotS =
        "\71\uffff";
    static final String DFA104_eofS =
        "\71\uffff";
    static final String DFA104_minS =
        "\1\47\7\uffff\1\2\60\uffff";
    static final String DFA104_maxS =
        "\1\u00bc\7\uffff\1\u010c\60\uffff";
    static final String DFA104_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\uffff\1\12\1\13\1\14\1\15"+
        "\1\16\1\17\1\20\1\21\1\22\1\23\1\24\1\25\1\26\1\27\1\30\1\31\1\32"+
        "\1\33\1\34\1\35\1\36\1\37\1\40\1\41\1\42\1\43\1\44\1\45\1\46\1\47"+
        "\1\50\1\51\1\52\1\53\1\54\1\55\1\56\1\57\1\60\1\61\1\62\1\63\1\64"+
        "\1\65\1\66\1\67\1\10\1\11";
    static final String DFA104_specialS =
        "\71\uffff}>";
    static final String[] DFA104_transitionS = {
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
            return "854:1: builtInCall returns [Expression e] : ( ^( STR st= expression ) | ^( LANG lg= expression ) | ^( LANGMATCHES lm1= expression lm2= expression ) | ^( DATATYPE dt= expression ) | ^( BOUND v= var ) | ^( IRI e6= expression ) | ^( URI e7= expression ) | ^( BNODE e8= expression ) | BNODE | ^( RAND NIL ) | ^( ABS e9= expression ) | ^( CEIL e10= expression ) | ^( FLOOR e11= expression ) | ^( ROUND e12= expression ) | ^( CONCAT e13= expressionList ) | ^( SUBSTR e14= expression e15= expression (e16= expression )? ) | ^( STRLEN e15= expression ) | ^( UCASE e16= expression ) | ^( REPLACE e1= expression e2= expression e3= expression (e4= expression )? ) | ^( LCASE e17= expression ) | ^( ENCODE_FOR_URI e18= expression ) | ^( CONTAINS e19= expression e20= expression ) | ^( STRSTARTS e21= expression e22= expression ) | ^( STRENDS e23= expression e24= expression ) | ^( STRBEFORE e241= expression e242= expression ) | ^( STRAFTER e243= expression e244= expression ) | ^( YEAR e25= expression ) | ^( MONTH e26= expression ) | ^( DAY e27= expression ) | ^( HOURS e28= expression ) | ^( MINUTES e29= expression ) | ^( SECONDS e30= expression ) | ^( TIMEZONE e31= expression ) | ^( TZ e32= expression ) | NOW | UUID | STRUUID | ^( MD5 e33= expression ) | ^( SHA1 e34= expression ) | ^( SHA256 e36= expression ) | ^( SHA384 e37= expression ) | ^( SHA512 e38= expression ) | ^( COALESCE e39= expressionList ) | ^( IF e40= expression e41= expression e42= expression ) | ^( STRLANG e45= expression e46= expression ) | ^( STRDT e47= expression e48= expression ) | ^( SAMETERM sam1= expression sam2= expression ) | ^( ISIRI isi= expression ) | ^( ISURI isu= expression ) | ^( ISBLANK isb= expression ) | ^( ISLITERAL isl= expression ) | ^( ISNUMERIC e55= expression ) | r= regexExpression | p= existsFunc | p= notExistsFunc );";
        }
    }
    static final String DFA118_eotS =
        "\12\uffff";
    static final String DFA118_eofS =
        "\12\uffff";
    static final String DFA118_minS =
        "\1\75\3\2\1\136\1\u00c6\1\u00c7\3\uffff";
    static final String DFA118_maxS =
        "\1\u00c7\3\2\1\u00cb\1\u00cc\1\u00cd\3\uffff";
    static final String DFA118_acceptS =
        "\7\uffff\1\1\1\2\1\3";
    static final String DFA118_specialS =
        "\12\uffff}>";
    static final String[] DFA118_transitionS = {
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

    static final short[] DFA118_eot = DFA.unpackEncodedString(DFA118_eotS);
    static final short[] DFA118_eof = DFA.unpackEncodedString(DFA118_eofS);
    static final char[] DFA118_min = DFA.unpackEncodedStringToUnsignedChars(DFA118_minS);
    static final char[] DFA118_max = DFA.unpackEncodedStringToUnsignedChars(DFA118_maxS);
    static final short[] DFA118_accept = DFA.unpackEncodedString(DFA118_acceptS);
    static final short[] DFA118_special = DFA.unpackEncodedString(DFA118_specialS);
    static final short[][] DFA118_transition;

    static {
        int numStates = DFA118_transitionS.length;
        DFA118_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA118_transition[i] = DFA.unpackEncodedString(DFA118_transitionS[i]);
        }
    }

    class DFA118 extends DFA {

        public DFA118(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 118;
            this.eot = DFA118_eot;
            this.eof = DFA118_eof;
            this.min = DFA118_min;
            this.max = DFA118_max;
            this.accept = DFA118_accept;
            this.special = DFA118_special;
            this.transition = DFA118_transition;
        }
        public String getDescription() {
            return "1037:1: numericLiteral returns [Constant n] : (n1= numericLiteralUnsigned | n2= numericLiteralPositive | n3= numericLiteralNegative );";
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
    public static final BitSet FOLLOW_FUNCTION_in_functionDecl592 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VARNAME_in_functionDecl600 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_OPEN_BRACE_in_functionDecl609 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_var_in_functionDecl617 = new BitSet(new long[]{0x0000000400000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_ARROW_in_functionDecl627 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_var_in_functionDecl635 = new BitSet(new long[]{0x0000000400000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_CLOSE_BRACE_in_functionDecl646 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_FUNCLANG_in_functionDecl653 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VARNAME_in_functionDecl663 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_functionBody_in_functionDecl676 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FUNCBODY_in_functionBody722 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_LITERAL2_in_functionBody737 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_groupGraphPattern_in_functionBody757 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DATASET_in_dataset800 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_datasetClause_in_dataset809 = new BitSet(new long[]{0x0000000000000008L,0x0000000000200000L});
    public static final BitSet FOLLOW_SUB_SELECT_in_subSelect843 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_selectClause_in_subSelect852 = new BitSet(new long[]{0x1200020000000000L});
    public static final BitSet FOLLOW_whereClause_in_subSelect868 = new BitSet(new long[]{0x1200020000000000L});
    public static final BitSet FOLLOW_solutionModifier_in_subSelect884 = new BitSet(new long[]{0x0000000000000008L,0x0040000000000000L});
    public static final BitSet FOLLOW_inlineData_in_subSelect910 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TYPE_in_selectClause956 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DISTINCT_in_selectClause960 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_REDUCED_in_selectClause972 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PVARS_in_selectClause1001 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_var_in_selectClause1009 = new BitSet(new long[]{0x0000000400400008L,0x0000000000010000L});
    public static final BitSet FOLLOW_expVar_in_selectClause1031 = new BitSet(new long[]{0x0000000400400008L,0x0000000000010000L});
    public static final BitSet FOLLOW_fexp_in_selectClause1054 = new BitSet(new long[]{0x0000000400400008L,0x0000000000010000L});
    public static final BitSet FOLLOW_256_in_selectClause1094 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_AS_in_expVar1125 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_var_in_expVar1129 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_expVar1133 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EXP_in_fexp1151 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_fexp1155 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CONSTRUCT_in_constructQuery1186 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_constructTemplate_in_constructQuery1193 = new BitSet(new long[]{0x0200020000000000L});
    public static final BitSet FOLLOW_dataset_in_constructQuery1207 = new BitSet(new long[]{0x0200020000000000L});
    public static final BitSet FOLLOW_whereClause_in_constructQuery1230 = new BitSet(new long[]{0x1200020000000000L});
    public static final BitSet FOLLOW_solutionModifier_in_constructQuery1249 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_dataset_in_constructQuery1289 = new BitSet(new long[]{0x1200020000000000L});
    public static final BitSet FOLLOW_WHERE_in_constructQuery1311 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_triplesTemplate_in_constructQuery1377 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_solutionModifier_in_constructQuery1457 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DESCRIBE_in_describeQuery1515 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_varOrIRIref2_in_describeQuery1524 = new BitSet(new long[]{0x1200021C00000008L,0x0000000000200000L,0x0000000000001000L});
    public static final BitSet FOLLOW_256_in_describeQuery1535 = new BitSet(new long[]{0x1200020000000008L,0x0000000000200000L});
    public static final BitSet FOLLOW_datasetClause_in_describeQuery1550 = new BitSet(new long[]{0x1200020000000008L,0x0000000000200000L});
    public static final BitSet FOLLOW_whereClause_in_describeQuery1571 = new BitSet(new long[]{0x1200020000000000L});
    public static final BitSet FOLLOW_solutionModifier_in_describeQuery1591 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ASK_in_askQuery1632 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_datasetClause_in_askQuery1640 = new BitSet(new long[]{0x0200000000000008L,0x0000000000200000L});
    public static final BitSet FOLLOW_whereClause_in_askQuery1656 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FROM_in_datasetClause1691 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_defaultGraphClause_in_datasetClause1698 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_namedGraphClause_in_datasetClause1712 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_sourceSelector_in_defaultGraphClause1745 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NAMED_in_namedGraphClause1767 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_sourceSelector_in_namedGraphClause1771 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_iRIref_in_sourceSelector1797 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHERE_in_whereClause1820 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_groupGraphPattern_in_whereClause1824 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MODIFIERS_in_solutionModifier1855 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_groupClause_in_solutionModifier1864 = new BitSet(new long[]{0x0000080000000008L,0x00000000A2000000L});
    public static final BitSet FOLLOW_havingClause_in_solutionModifier1881 = new BitSet(new long[]{0x0000080000000008L,0x00000000A0000000L});
    public static final BitSet FOLLOW_orderClause_in_solutionModifier1898 = new BitSet(new long[]{0x0000000000000008L,0x00000000A0000000L});
    public static final BitSet FOLLOW_limitOffsetClauses_in_solutionModifier1915 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_GROUP_BY_in_groupClause1941 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_groupCondition_in_groupClause1945 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_builtInCall_in_groupCondition1978 = new BitSet(new long[]{0x000010A400000002L,0x0000000000000000L,0x1FFFF7FFFFFFFF80L});
    public static final BitSet FOLLOW_functionCall_in_groupCondition1997 = new BitSet(new long[]{0x000010A400000002L,0x0000000000000000L,0x1FFFF7FFFFFFFF80L});
    public static final BitSet FOLLOW_CONDITION_in_groupCondition2015 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_groupCondition2019 = new BitSet(new long[]{0x0000000400000008L});
    public static final BitSet FOLLOW_var_in_groupCondition2024 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_var_in_groupCondition2041 = new BitSet(new long[]{0x000010A400000002L,0x0000000000000000L,0x1FFFF7FFFFFFFF80L});
    public static final BitSet FOLLOW_HAVING_in_havingClause2115 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_havingCondition_in_havingClause2120 = new BitSet(new long[]{0x000000E000000008L,0x0000000000000000L,0x1FFFF7FFFFFFFF80L});
    public static final BitSet FOLLOW_constraint_in_havingCondition2148 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ORDER_BY_in_orderClause2177 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_orderCondition_in_orderClause2184 = new BitSet(new long[]{0x000000E400000008L,0x0000000018000000L,0x1FFFF7FFFFFFFF80L});
    public static final BitSet FOLLOW_ASC_in_orderCondition2211 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_brackettedExpression_in_orderCondition2215 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DESC_in_orderCondition2227 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_brackettedExpression_in_orderCondition2231 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_constraint_in_orderCondition2245 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_in_orderCondition2261 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_limitClause_in_limitOffsetClauses2301 = new BitSet(new long[]{0x0000000000000002L,0x00000000A0000000L});
    public static final BitSet FOLLOW_offsetClause_in_limitOffsetClauses2309 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_offsetClause_in_limitOffsetClauses2322 = new BitSet(new long[]{0x0000000000000002L,0x0000000020000000L});
    public static final BitSet FOLLOW_limitClause_in_limitOffsetClauses2329 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LIMIT_in_limitClause2367 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_INTEGER_in_limitClause2372 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_OFFSET_in_offsetClause2399 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_INTEGER_in_offsetClause2404 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_inlineData_in_bindingsClause2428 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_iRIref_in_bindingValue2449 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rDFLiteral_in_bindingValue2453 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numericLiteral_in_bindingValue2457 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_booleanLiteral_in_bindingValue2461 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UNDEF_in_bindingValue2465 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_triplesSameSubject_in_triplesTemplate2485 = new BitSet(new long[]{0x0000000000000002L,0x0020000000000000L});
    public static final BitSet FOLLOW_DOT_in_triplesTemplate2492 = new BitSet(new long[]{0x00000000C0000002L});
    public static final BitSet FOLLOW_triplesTemplate_in_triplesTemplate2494 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GROUP_GRAPH_PATTERN_in_groupGraphPattern2524 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_groupGraphPatternSub_in_groupGraphPattern2526 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_subSelect_in_groupGraphPattern2538 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_triplesBlock_in_groupGraphPatternSub2570 = new BitSet(new long[]{0x0000000019000002L,0x1FC8000000000000L});
    public static final BitSet FOLLOW_filter_in_groupGraphPatternSub2602 = new BitSet(new long[]{0x0000000019000002L,0x1FC8000000000000L});
    public static final BitSet FOLLOW_graphPatternNewBGP_in_groupGraphPatternSub2640 = new BitSet(new long[]{0x0000000019000002L,0x1FC8000000000000L});
    public static final BitSet FOLLOW_TRIPLES_BLOCK_in_triplesBlock2699 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_triples_in_triplesBlock2732 = new BitSet(new long[]{0x00000000C0000008L});
    public static final BitSet FOLLOW_triples2_in_triplesBlock2748 = new BitSet(new long[]{0x00000000C0000008L});
    public static final BitSet FOLLOW_TRIPLE_in_triples2796 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_SUBJECT_in_triples2799 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_graphNode_in_triples2805 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PREDICATE_in_triples2841 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_predicate_in_triples2847 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VALUE_in_triples2887 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_object_in_triples2892 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TRIPLE2_in_triples22947 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_SUBJECT_in_triples22950 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_triplesNode_in_triples22956 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PROPERTY_LIST_in_triples22994 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_PREDICATE_in_triples23006 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_predicate_in_triples23013 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VALUE_in_triples23063 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_object_in_triples23068 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_groupMinusOrUnionGraphPattern_in_graphPatternNewBGP3128 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_optionalGraphPattern_in_graphPatternNewBGP3148 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_graphGraphPattern_in_graphPatternNewBGP3169 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_serviceGraphPattern_in_graphPatternNewBGP3188 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_bind_in_graphPatternNewBGP3197 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inlineData_in_graphPatternNewBGP3217 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VALUES_in_inlineData3250 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_dataBlock_in_inlineData3254 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_INLINE_DATA_in_dataBlock3286 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NIL_in_dataBlock3296 = new BitSet(new long[]{0x6000C01800000808L,0x0000000200000000L,0x0000000000001000L,0x0000000000000080L});
    public static final BitSet FOLLOW_var_in_dataBlock3303 = new BitSet(new long[]{0x6000C01C00000808L,0x0000000200000000L,0x0000000000001000L,0x0000000000000080L});
    public static final BitSet FOLLOW_NIL_in_dataBlock3318 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_dataBlockValue_in_dataBlock3325 = new BitSet(new long[]{0x6000C01800000008L,0x0000000200000000L,0x0000000000001000L,0x0000000000000080L});
    public static final BitSet FOLLOW_iRIref_in_dataBlockValue3361 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rDFLiteral_in_dataBlockValue3377 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numericLiteral_in_dataBlockValue3392 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_booleanLiteral_in_dataBlockValue3407 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UNDEF_in_dataBlockValue3423 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPTIONAL_in_optionalGraphPattern3453 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_groupGraphPattern_in_optionalGraphPattern3457 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_GRAPH_in_graphGraphPattern3487 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_varOrIRIref2_in_graphGraphPattern3491 = new BitSet(new long[]{0x0000000009000000L,0x0C00000000000000L});
    public static final BitSet FOLLOW_groupGraphPattern_in_graphGraphPattern3495 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SERVICE_in_serviceGraphPattern3531 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_SILENT_in_serviceGraphPattern3534 = new BitSet(new long[]{0x0000001C00000000L,0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_varOrIRIref_in_serviceGraphPattern3542 = new BitSet(new long[]{0x0000000009000000L,0x0C00000000000000L});
    public static final BitSet FOLLOW_groupGraphPattern_in_serviceGraphPattern3546 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BIND_in_bind3579 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_var_in_bind3583 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_bind3589 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_UNION_in_groupMinusOrUnionGraphPattern3621 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_groupGraphPattern_in_groupMinusOrUnionGraphPattern3642 = new BitSet(new long[]{0x0000000009000000L,0x0C00000000000000L});
    public static final BitSet FOLLOW_groupGraphPattern_in_groupMinusOrUnionGraphPattern3673 = new BitSet(new long[]{0x0000000009000008L,0x0C00000000000000L});
    public static final BitSet FOLLOW_MINUS_in_groupMinusOrUnionGraphPattern3709 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_groupGraphPattern_in_groupMinusOrUnionGraphPattern3724 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_groupGraphPattern_in_groupMinusOrUnionGraphPattern3751 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FILTER_in_filter3784 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_constraint_in_filter3788 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_brackettedExpression_in_constraint3812 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_builtInCall_in_constraint3822 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionCall_in_constraint3835 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_functionCall3857 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_iRIref_in_functionCall3861 = new BitSet(new long[]{0x6800C0FC00000E00L,0x0000000000004000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_argList_in_functionCall3865 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NIL_in_argList3894 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DISTINCT_in_argList3907 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_argList3912 = new BitSet(new long[]{0x6800C0FC00000602L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_argList3925 = new BitSet(new long[]{0x6800C0FC00000602L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_NIL_in_expressionList3960 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_expressionList3983 = new BitSet(new long[]{0x6800C0FC00000602L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_constructTriples_in_constructTemplate4003 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_triples_in_constructTriples4024 = new BitSet(new long[]{0x00000000C0000002L});
    public static final BitSet FOLLOW_triples2_in_constructTriples4035 = new BitSet(new long[]{0x00000000C0000002L});
    public static final BitSet FOLLOW_triples_in_triplesSameSubject4072 = new BitSet(new long[]{0x0000000040000002L});
    public static final BitSet FOLLOW_triples2_in_triplesSameSubject4084 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_graphNode_in_object4106 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_varOrIRIref_in_verb4128 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_257_in_verb4138 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_path_in_verbPath4156 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_in_verbSimple4175 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathAlternative_in_path4192 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathSequence_in_pathAlternative4205 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_258_in_pathAlternative4209 = new BitSet(new long[]{0x0000001800000000L,0x0000000000000040L,0x0000000000001000L,0x0000000000000000L,0x0000000000000092L});
    public static final BitSet FOLLOW_pathSequence_in_pathAlternative4211 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_pathEltOrInverse_in_pathSequence4228 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_259_in_pathSequence4232 = new BitSet(new long[]{0x0000001800000000L,0x0000000000000040L,0x0000000000001000L,0x0000000000000000L,0x0000000000000092L});
    public static final BitSet FOLLOW_pathEltOrInverse_in_pathSequence4234 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_pathPrimary_in_pathElt4251 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000061L});
    public static final BitSet FOLLOW_pathMod_in_pathElt4253 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathElt_in_pathEltOrInverse4268 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_260_in_pathEltOrInverse4272 = new BitSet(new long[]{0x0000001800000000L,0x0000000000000040L,0x0000000000001000L,0x0000000000000000L,0x0000000000000082L});
    public static final BitSet FOLLOW_pathElt_in_pathEltOrInverse4274 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_256_in_pathMod4289 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_261_in_pathMod4298 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_262_in_pathMod4307 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_iRIref_in_pathPrimary4325 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_257_in_pathPrimary4329 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_263_in_pathPrimary4333 = new BitSet(new long[]{0x0000001800000100L,0x0000000000000040L,0x0000000000001000L,0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_pathNegatedPropertySet_in_pathPrimary4335 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_BRACE_in_pathPrimary4339 = new BitSet(new long[]{0x0000001800000000L,0x0000000000000040L,0x0000000000001000L,0x0000000000000000L,0x0000000000000092L});
    public static final BitSet FOLLOW_path_in_pathPrimary4341 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_CLOSE_BRACE_in_pathPrimary4343 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathOneInPropertySet_in_pathNegatedPropertySet4373 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_BRACE_in_pathNegatedPropertySet4381 = new BitSet(new long[]{0x0000001800000100L,0x0000000000000100L,0x0000000000001000L,0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_pathOneInPropertySet_in_pathNegatedPropertySet4389 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L,0x0000000000000000L,0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_258_in_pathNegatedPropertySet4393 = new BitSet(new long[]{0x0000001800000100L,0x0000000000000000L,0x0000000000001000L,0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_pathOneInPropertySet_in_pathNegatedPropertySet4399 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L,0x0000000000000000L,0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_CLOSE_BRACE_in_pathNegatedPropertySet4410 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_iRIref_in_pathOneInPropertySet4438 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_257_in_pathOneInPropertySet4445 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INV_in_pathOneInPropertySet4454 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_iRIrefOrRDFType_in_pathOneInPropertySet4460 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_iRIref_in_iRIrefOrRDFType4487 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_257_in_iRIrefOrRDFType4496 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INTEGER_in_integer4517 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRIPLES_NODE_in_triplesNode4536 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_collection_in_triplesNode4547 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_blankNodePropertyList_in_triplesNode4564 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PROPERTY_LIST_in_blankNodePropertyList4603 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_PREDICATE_in_blankNodePropertyList4624 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_predicate_in_blankNodePropertyList4628 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VALUE_in_blankNodePropertyList4643 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_graphNode_in_blankNodePropertyList4648 = new BitSet(new long[]{0x6020C01C00001808L,0x0000000000000000L,0x0000000000001000L,0x0000000000100080L});
    public static final BitSet FOLLOW_var_in_predicate4704 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_iRIref_in_predicate4719 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_257_in_predicate4730 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALT_in_predicate4745 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_predicate_in_predicate4750 = new BitSet(new long[]{0x0000001C000001E8L,0x0000000000000000L,0x0000000000001000L,0x0000000000000000L,0x0000000000000082L});
    public static final BitSet FOLLOW_SEQ_in_predicate4766 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_predicate_in_predicate4771 = new BitSet(new long[]{0x0000001C000001E8L,0x0000000000000000L,0x0000000000001000L,0x0000000000000000L,0x0000000000000082L});
    public static final BitSet FOLLOW_ELT_in_predicate4787 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_predicate_in_predicate4791 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000061L});
    public static final BitSet FOLLOW_pathMod_in_predicate4797 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_INV_in_predicate4812 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_predicate_in_predicate4816 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_263_in_predicate4827 = new BitSet(new long[]{0x0000001800000100L,0x0000000000000040L,0x0000000000001000L,0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_pathNegatedPropertySet_in_predicate4833 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLLECTION_in_collection4861 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_graphNode_in_collection4866 = new BitSet(new long[]{0x6020C01C00001808L,0x0000000000000000L,0x0000000000001000L,0x0000000000100080L});
    public static final BitSet FOLLOW_var_in_graphNode4892 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_graphTerm_in_graphNode4905 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_triplesNode_in_graphNode4917 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_in_varOrTerm4940 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_graphTerm_in_varOrTerm4955 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_in_varOrIRIref4977 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_iRIref_in_varOrIRIref4992 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_in_varOrIRIref25027 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_iRIref_in_varOrIRIref25043 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VAR_in_var5070 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VAR1_in_var5077 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VAR2_in_var5092 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_iRIref_in_graphTerm5126 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rDFLiteral_in_graphTerm5140 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numericLiteral_in_graphTerm5152 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_booleanLiteral_in_graphTerm5165 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_blankNode_in_graphTerm5178 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NIL_in_graphTerm5191 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LOGICAL_OR_in_expression5222 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression5234 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_expression5255 = new BitSet(new long[]{0x6800C0FC00000608L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_LOGICAL_AND_in_expression5280 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression5292 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_expression5313 = new BitSet(new long[]{0x6800C0FC00000608L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_264_in_expression5340 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression5354 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_expression5365 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_265_in_expression5385 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression5399 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_expression5410 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LT_in_expression5430 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression5445 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_expression5460 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_266_in_expression5480 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression5495 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_expression5506 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LTE_in_expression5526 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression5540 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_expression5551 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_267_in_expression5571 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression5585 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_expression5596 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IN_in_expression5616 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression5643 = new BitSet(new long[]{0x6800C0FC00000E00L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expressionList_in_expression5656 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NOT_in_expression5702 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression5709 = new BitSet(new long[]{0x6800C0FC00000E00L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expressionList_in_expression5717 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_262_in_expression5751 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression5778 = new BitSet(new long[]{0x6800C0FC00000608L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_expression5790 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BROKEN_PLUS_in_expression5813 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression5832 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_expression5843 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_268_in_expression5863 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression5889 = new BitSet(new long[]{0x6800C0FC00000608L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_expression5901 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BROKEN_MINUS_in_expression5918 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression5936 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_expression5947 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_256_in_expression5967 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression5994 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_expression6005 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_259_in_expression6025 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression6052 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_expression6062 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_263_in_expression6082 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression6086 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_brackettedExpression_in_expression6097 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primaryExpression_in_expression6107 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_builtInCall_in_primaryExpression6132 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_iRIref_in_primaryExpression6144 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_iRIFunction_in_primaryExpression6156 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rDFLiteral_in_primaryExpression6168 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numericLiteral_in_primaryExpression6180 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_booleanLiteral_in_primaryExpression6191 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_in_primaryExpression6202 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aggregate_in_primaryExpression6216 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXPRESSION_in_brackettedExpression6239 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_brackettedExpression6244 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STR_in_builtInCall6274 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6278 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LANG_in_builtInCall6291 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6295 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LANGMATCHES_in_builtInCall6308 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6312 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_builtInCall6316 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DATATYPE_in_builtInCall6329 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6333 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BOUND_in_builtInCall6345 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_var_in_builtInCall6349 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IRI_in_builtInCall6367 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6371 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_URI_in_builtInCall6384 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6388 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BNODE_in_builtInCall6400 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6404 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BNODE_in_builtInCall6415 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RAND_in_builtInCall6426 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NIL_in_builtInCall6428 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ABS_in_builtInCall6441 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6445 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CEIL_in_builtInCall6457 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6461 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FLOOR_in_builtInCall6473 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6477 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ROUND_in_builtInCall6489 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6493 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CONCAT_in_builtInCall6505 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expressionList_in_builtInCall6509 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SUBSTR_in_builtInCall6522 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6526 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_builtInCall6530 = new BitSet(new long[]{0x6800C0FC00000608L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_builtInCall6554 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRLEN_in_builtInCall6577 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6581 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_UCASE_in_builtInCall6593 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6597 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_REPLACE_in_builtInCall6609 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6613 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_builtInCall6617 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_builtInCall6621 = new BitSet(new long[]{0x6800C0FC00000608L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_builtInCall6645 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LCASE_in_builtInCall6663 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6667 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ENCODE_FOR_URI_in_builtInCall6679 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6683 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CONTAINS_in_builtInCall6695 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6699 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_builtInCall6703 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRSTARTS_in_builtInCall6715 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6719 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_builtInCall6723 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRENDS_in_builtInCall6735 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6739 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_builtInCall6743 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRBEFORE_in_builtInCall6755 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6759 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_builtInCall6763 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRAFTER_in_builtInCall6775 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6779 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_builtInCall6783 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_YEAR_in_builtInCall6795 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6799 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MONTH_in_builtInCall6811 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6815 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DAY_in_builtInCall6827 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6831 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_HOURS_in_builtInCall6843 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6847 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MINUTES_in_builtInCall6859 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6863 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SECONDS_in_builtInCall6875 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6879 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TIMEZONE_in_builtInCall6891 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6895 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TZ_in_builtInCall6907 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6911 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NOW_in_builtInCall6922 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UUID_in_builtInCall6934 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRUUID_in_builtInCall6946 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MD5_in_builtInCall6955 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6959 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SHA1_in_builtInCall6971 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6975 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SHA256_in_builtInCall6987 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall6991 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SHA384_in_builtInCall7003 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall7007 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SHA512_in_builtInCall7019 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall7023 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_COALESCE_in_builtInCall7035 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expressionList_in_builtInCall7039 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IF_in_builtInCall7051 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall7055 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_builtInCall7059 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_builtInCall7063 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRLANG_in_builtInCall7075 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall7079 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_builtInCall7083 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRDT_in_builtInCall7095 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall7099 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_builtInCall7103 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SAMETERM_in_builtInCall7115 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall7119 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_builtInCall7123 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ISIRI_in_builtInCall7135 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall7139 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ISURI_in_builtInCall7151 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall7155 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ISBLANK_in_builtInCall7167 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall7171 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ISLITERAL_in_builtInCall7183 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall7187 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ISNUMERIC_in_builtInCall7199 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall7203 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_regexExpression_in_builtInCall7216 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_existsFunc_in_builtInCall7225 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_notExistsFunc_in_builtInCall7234 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_REGEX_in_regexExpression7257 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_regexExpression7261 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_regexExpression7265 = new BitSet(new long[]{0x6800C0FC00000608L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_regexExpression7269 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EXISTS_in_existsFunc7301 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_groupGraphPattern_in_existsFunc7305 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NOT_EXISTS_in_notExistsFunc7336 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_groupGraphPattern_in_notExistsFunc7340 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_COUNT_in_aggregate7375 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregate7387 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_aggregate7444 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_256_in_aggregate7454 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SUM_in_aggregate7474 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregate7486 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_aggregate7503 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MIN_in_aggregate7516 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregate7529 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_aggregate7545 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MAX_in_aggregate7558 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregate7571 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_aggregate7588 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_AVG_in_aggregate7601 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregate7614 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_aggregate7631 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SAMPLE_in_aggregate7644 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregate7656 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_aggregate7673 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_GROUP_CONCAT_in_aggregate7687 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregate7699 = new BitSet(new long[]{0x6800C0FC00000600L,0x0000000000000000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_expression_in_aggregate7714 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_SEPARATOR_in_aggregate7725 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_string_in_aggregate7729 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FUNCTION_in_iRIFunction7758 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_iRIref_in_iRIFunction7768 = new BitSet(new long[]{0x6800C0FC00000E08L,0x0000000000004000L,0xFFFFF7FFFFFFFFFCL,0x000000000000008FL,0x0000000000001FC9L});
    public static final BitSet FOLLOW_argList_in_iRIFunction7780 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_string_in_rDFLiteral7812 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000000020L,0x0000000000002000L});
    public static final BitSet FOLLOW_LANGTAG_in_rDFLiteral7827 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_269_in_rDFLiteral7841 = new BitSet(new long[]{0x0000001800000000L,0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_iRIref_in_rDFLiteral7845 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numericLiteralUnsigned_in_numericLiteral7876 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numericLiteralPositive_in_numericLiteral7886 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numericLiteralNegative_in_numericLiteral7896 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BIG_INTEGER_in_numericLiteralUnsigned7918 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_INTEGER_in_numericLiteralUnsigned7923 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BIG_DECIMAL_in_numericLiteralUnsigned7939 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DECIMAL_in_numericLiteralUnsigned7943 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DOUBLE_in_numericLiteralUnsigned7958 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DOUBLE_in_numericLiteralUnsigned7963 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BIG_INTEGER_in_numericLiteralPositive7993 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_INTEGER_POSITIVE_in_numericLiteralPositive7998 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BIG_DECIMAL_in_numericLiteralPositive8011 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DECIMAL_POSITIVE_in_numericLiteralPositive8015 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DOUBLE_in_numericLiteralPositive8028 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DOUBLE_POSITIVE_in_numericLiteralPositive8033 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BIG_INTEGER_in_numericLiteralNegative8058 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_INTEGER_NEGATIVE_in_numericLiteralNegative8063 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BIG_DECIMAL_in_numericLiteralNegative8076 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DECIMAL_NEGATIVE_in_numericLiteralNegative8080 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DOUBLE_in_numericLiteralNegative8093 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DOUBLE_NEGATIVE_in_numericLiteralNegative8098 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BOOLEAN_in_booleanLiteral8122 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_TRUE_in_booleanLiteral8124 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BOOLEAN_in_booleanLiteral8139 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_FALSE_in_booleanLiteral8141 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_in_string8164 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_LITERAL1_in_string8168 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_in_string8185 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_LITERAL2_in_string8189 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_in_string8206 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_LITERAL_LONG1_in_string8210 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_in_string8222 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_LITERAL_LONG2_in_string8226 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IRI_in_iRIref8250 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IRI_REF_in_iRIref8254 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_prefixedName_in_iRIref8266 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PREFIXED_NAME_in_prefixedName8288 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_PNAME_LN_in_prefixedName8292 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PREFIXED_NS_in_prefixedName8301 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_PNAME_NS_in_prefixedName8305 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BLANK_NODE_LABEL_in_blankNode8329 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANNON_in_blankNode8340 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_OPEN_SQ_BRACKET_in_blankNode8344 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NIL_in_synpred1_IbmSparqlExtAstWalker3292 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NIL_in_synpred2_IbmSparqlExtAstWalker3314 = new BitSet(new long[]{0x0000000000000002L});

}