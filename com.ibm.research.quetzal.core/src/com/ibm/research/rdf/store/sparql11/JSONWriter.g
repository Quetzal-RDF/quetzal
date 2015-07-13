tree grammar JSONWriter;

options {
	language = Java;
	tokenVocab = IbmSparql;
	ASTLabelType = XTree;
}						
				
@header { 
package com.ibm.research.rdf.store.sparql11;
	
import org.antlr.runtime.BitSet;
import java.util.*;
import java.math.BigInteger;
import java.math.BigDecimal;
import com.ibm.research.rdf.store.sparql11.model.*;
import com.ibm.wala.util.collections.HashSetFactory;
import com.ibm.wala.util.collections.Pair;
import org.json.*;
}	

@members {
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

    private void putAll(List c, JSONArray y) throws JSONException {
        for(int i = 0; i < y.length(); i++) {
            c.add(y.get(i));
        }
    }
}

@rulecatch {
	catch(RecognitionException eee) {
        throw new RuntimeException(eee);
    }
}

		
queryUnit returns [JSONObject o] throws JSONException
	:     ^(q=ROOT x=query)  	{ $o = x; }
	;
	
query returns [JSONObject o] throws JSONException
	: ^(q=QUERY 
			( p=prologue )      
			(	( s=selectQuery  	{ $o = s; $o.put("source", q.matched); } )
			    ( b=bindingsClause  
                  { 
                    $o.put("bindings", b);
                  }
                )?
			)
		)
	;
	

prologue  throws JSONException
	:  	 ^(PROLOGUE baseDecl?  prefixDecl*)
	;

				
baseDecl  throws JSONException
	:  	^(BASE iRIref)
    ;

prefixDecl throws JSONException
	:  	^(PREFIX prefixedName  iRIref)
	;
			
selectQuery	returns [JSONObject o] throws JSONException
	@init { $o = new JSONObject(); }
	:  	
		^(SELECT  
			(s=selectClause[$o]  )
			(d=dataset				{ $o.put("datasets", d);    }  )?
			( whereClause[$o] )?
			( solutionModifier[$o] )  
		)
	;
	
dataset returns [JSONArray dcl] throws JSONException
	:	
		^(DATASET 
            { dcl = new JSONArray(); }
            (dc=datasetClause { dcl.put(dc); } )+
		)
	;
	
subSelect returns [JSONObject sp] throws JSONException
	:  	^(SUB_SELECT 
	      { 
		    $sp = new JSONObject();
	      }
			s=selectClause[$sp]
			(w=whereClause[$sp] )?
			( solutionModifier[$sp] )
            (d=inlineData           { $sp.put("bindings", d); })?
		)
	;

selectClause [JSONObject o] throws JSONException
    @init { JSONArray args = null; }
	:  	
		(^(TYPE ( DISTINCT   { $o.put("distinct", true); }
				| REDUCED    { $o.put("reduced", true); }
				)
		   )
		)? 
		^(PVARS
          (
           '*' { $o.put("star", true); }
           | 
           { args = new JSONArray(); }
           (   v=(var | expVar | fexp)+ { args.put(v); } )
          )
		) 
	;

expVar returns [JSONObject o] throws JSONException
	:	^(AS v=var e=expression)   { $o = new JSONObject(); $o.put("var", v); $o.put("expr", e); } 
	;

fexp returns [JSONObject o] throws JSONException
	:	^(EXP e=expression)   { $o = e; }
	;
	
datasetClause returns [JSONObject o] throws JSONException
	:	^(FROM 	( d=defaultGraphClause	 	{ $o = d; }
				| n=namedGraphClause		{ $o = n; }
				)
		)
	;
	
defaultGraphClause returns [JSONObject o] throws JSONException
	:  	d=sourceSelector  { $o = new JSONObject(); $o.put("dataset", d); }
	;
	
namedGraphClause returns [JSONObject o] throws JSONException
	:  	^(NAMED d=sourceSelector)  { $o = new JSONObject(); $o.put("dataset", d); } 
	;
	
sourceSelector returns [IRI  r] throws JSONException
	:  	 i=iRIref	{ $r = i; }
	;

whereClause[JSONObject o] throws JSONException
	:  	
		^(WHERE g=groupGraphPattern?)  { $o.put("where", g); }
	;
	
solutionModifier[JSONObject o] throws JSONException
	:  	^(MODIFIERS
			( g=groupClause 		{$o.put("group", g); } )? 
			( h=havingClause 		{$o.put("having", h);} )? 
			( r=orderClause 		{$o.put("order", r); } )? 
			( limitOffsetClauses[$o] )? )
	;

groupClause	returns [JSONArray gc] throws JSONException
	:  	^(GROUP_BY c=groupCondition)	{ $gc = c; }
	;
	
groupCondition returns [JSONArray gc] throws JSONException
    @init { $gc = new JSONArray(); }
	:  	
	( b=builtInCall									 { $gc.put(b); } 
	| f=functionCall 								 { $gc.put(f); }
	| ^(CONDITION e2=expression (v=var)?)			 { JSONObject x = new JSONObject(); x.put("expr", e2); if (v != null) { x.put("var", v); } $gc.put(x); }  
	| v2=var                                         { $gc.put(v2); }
	)+  
	;

havingClause returns [JSONArray h] throws JSONException 
	@init {
		$h = new JSONArray();
	} 
	:  	^(HAVING (c=havingCondition	{ $h.put(c); } )+ )
	;
	
havingCondition	 returns [Object e] throws JSONException
	:  	c=constraint  {$e = c;}
	;
	 
orderClause	returns [JSONArray loc] throws JSONException
	@init { 
		$loc = new JSONArray(); 
	}
	:  	^(ORDER_BY 	( oc=orderCondition { $loc.put(oc); } )+ )
	;
		
orderCondition returns [Object oc] throws JSONException
	: 	(^(ASC e1=brackettedExpression)  { JSONObject x = new JSONObject(); x.put("expr", e1); x.put("direction", "asc"); $oc = x; })
	|	(^(DESC e2=brackettedExpression)  { JSONObject x = new JSONObject(); x.put("expr", e2); x.put("direction", "desc"); $oc = x; })
	| 	( c=constraint 					{ $oc = c; } 
		| v=var 						{ $oc = v; }
		)
	;
	
limitOffsetClauses [JSONObject loc] throws JSONException
	:  	( limitClause[$loc] (offsetClause[$loc])? 
		| offsetClause[$loc] (limitClause[$loc])?  
		)	
	;
	
limitClause	[JSONObject loc] throws JSONException
	:  	
		^(LIMIT i=INTEGER) 	{ $loc.put("limit", Integer.parseInt($i.text)); }
	;
	
offsetClause [JSONObject loc] throws JSONException
	:  	
		^(OFFSET i=INTEGER)	{ $loc.put("offset", Integer.parseInt($i.text)); }
	;
	
bindingsClause returns [JSONObject v] throws JSONException 
	:  	d=inlineData { $v = d; }
	;
	
bindingValue  throws JSONException
	:  	( iRIref | rDFLiteral | numericLiteral | booleanLiteral | UNDEF )
	;
	
groupGraphPattern returns [JSONObject r] throws JSONException
	:   ^(GROUP_GRAPH_PATTERN
          (g=groupGraphPatternSub
            { $r = new JSONObject();
              $r.put("group", g); } )?)  
	|	s=subSelect	{ $r = s; }
    ;
	
groupGraphPatternSub returns [JSONArray p] throws JSONException
    @init {
      $p = new JSONArray();
    }
	:	(     triplesBlock[$p]
	    |     f=filter 					
              { 
                if (f != null) { $p.put(f); }
              }
        |     graphPatternNewBGP[$p]
        )+
    ;

triplesBlock [JSONArray sp] throws JSONException
    :   ^( TRIPLES_BLOCK
		   (
		       s=triples    { $sp.put(s); } 
			 | s2=triples2[$sp]
		   )+ 
		 ) 
   ;

triples returns [JSONObject qt] throws JSONException
	:   ( ^(TRIPLE ^(SUBJECT s1=graphNode )
				   ^(PREDICATE v1=predicate )  
				   ^(VALUE o=object)	
				 
		   ) 	
         { $qt = new JSONObject();
           $qt.put("subject", s1);
           $qt.put("predicate", v1);
           $qt.put("object", o);
         }
		)
	;

triples2[JSONArray qt] throws JSONException
	:   ^(TRIPLE2 ^(SUBJECT	s=triplesNode )
			( ^(PROPERTY_LIST 
			    ^(PREDICATE	p=predicate)  
				   ( ^(VALUE  o=object)		
			         { JSONObject t = new JSONObject(); 
                       t.put("subject", s);
                       t.put("predicate", p);
                       t.put("object", o);
                       $qt.put(t);
                     }	)+
			       )
				 )*
		)
	;


graphPatternNewBGP[JSONArray p] throws JSONException
    :   u=groupMinusOrUnionGraphPattern 	
        { $p.put(u); }
	| 	o=optionalGraphPattern 		
        { $p.put(o); }
	| 	g=graphGraphPattern 		
        { $p.put(g); }
	| 	s=serviceGraphPattern 
		{ $p.put(s); }
	| 	b=bind
        { $p.put(b); } 
    |   v=inlineData
        { $p.put(v); }
    ;

inlineData returns [JSONObject v] throws JSONException
    :   ^(VALUES d=dataBlock { $v=d; })
    ;

dataBlock returns [JSONObject values] throws JSONException
@init {JSONArray vars = new JSONArray(); 
       JSONArray expressions = new JSONArray(); 
       JSONArray rowExp = null;
       int i = 0; 

       $values = new JSONObject();
       values.put("vars", vars);
       values.put("exprs", expressions);
       }
    :   ^(INLINE_DATA ( ( NIL )=>NIL | (v=var {vars.put(v);})*) ( ( NIL )=>NIL | (d=dataBlockValue 
    	{ int row = i \% vars.length(); 
    	  if (row == 0) {
    	  	rowExp = new JSONArray();
    	  	expressions.put(rowExp);
    	  }
    	  rowExp.put(d);
    	  i++;
		})* ))
    ;

dataBlockValue returns [Object e] throws JSONException
	:    i=iRIref 			{e = i;} 
	|    r=rDFLiteral 		{e = r;} 
	|    d=numericLiteral 	{e = d;} 
	|    b= booleanLiteral 	{e = b;} 
	|    u = UNDEF          {e = "UNDEF";}
	;

optionalGraphPattern returns [JSONObject p] throws JSONException
	:    ^(OPTIONAL g=groupGraphPattern)    				
         { 
           $p = new JSONObject();
           $p.put("optional", g); 
         }
	;

graphGraphPattern returns [JSONObject p] throws JSONException
	:    ^(GRAPH r=varOrIRIref2 g=groupGraphPattern)		
         { 
           $p = new JSONObject();
           $p.put("graph", r); 
           $p.put("pattern", g); 
         }
	;

serviceGraphPattern  returns [JSONObject p] throws JSONException
    @init { 
            $p = new JSONObject();
          }
	:    ^(SERVICE 
           (SILENT { $p.put("silent", true); })? 
           s=varOrIRIref 
           g=groupGraphPattern)
         {
           $p.put("service", s); 
           $p.put("pattern", g); 
         }
	;

bind  returns [JSONObject p] throws JSONException
	:    ^(BIND v=var  e= expression) 
         { 
           $p = new JSONObject();
           $p.put("var", v);
           $p.put("expr", e);
         }
	    ;

groupMinusOrUnionGraphPattern returns [JSONObject p] throws JSONException
    @init { JSONArray x = null; }
	:   ^(UNION 
                g1=groupGraphPattern
                { 
            	  $p = new JSONObject();
                  x = new JSONArray();
                  $p.put("union", x);
                  x.put(g1);
              	} 
				(g2=groupGraphPattern
                 { 
                   x.put(g2); 
                 })+ )
    |   ^(MINUS
           g1=groupGraphPattern
           { $p = new JSONObject();
             $p.put("minus", g1); } 
         )
    |	g3=groupGraphPattern
        { 
          $p = g3;
        }
	;

filter returns [JSONObject e] throws JSONException
	:   ^(FILTER c=constraint)			{ $e = new JSONObject(); $e.put("filter", c); }
	;

constraint returns [Object e] throws JSONException
	:   be=brackettedExpression { $e = be; }
	| 	bc=builtInCall 			{ $e = bc; }
	| 	fc=functionCall			{ $e = fc; }
	;

functionCall returns [JSONObject fc] throws JSONException
	:   ^(FUNCTION i=iRIref a=argList)	
        { 
          $fc = new JSONObject();
          $fc.put("function", i);
          $fc.put("args", a);
        }
	;

argList returns [JSONArray args] throws JSONException
	@init { $args = new JSONArray(); }
    :  	NIL						
	|   DISTINCT (e1=expression	{ $args.put(e1); }	)+
	|	(e2=expression			{ $args.put(e2); }	)+ 
	;

expressionList returns [JSONArray exprs] throws JSONException
    @init { $exprs = new JSONArray(); } 
	:   NIL            
	|   ( e=expression { $exprs.put(e); } )+
	;


object returns [Object r] throws JSONException
	:   g=graphNode		{ $r = g; }
	;

verb returns [Object t] throws JSONException
	:   v=varOrIRIref  	{ $t = v; }
	| 	'a'				{ $t = new IRI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"); }
	;

verbPath throws JSONException
	:   path
	;

verbSimple throws JSONException
	:   var
	;

path returns [Object o] throws JSONException
	:   p=pathAlternative { $o = p; }
	;

pathAlternative returns [Object o] throws JSONException
    @init { JSONArray x = new JSONArray(); }
	:   l=pathSequence ( '|' r=pathSequence { if (x.length()==0) { x.put(l); } x.put(r); } )*
    {
      if (x.length() > 0) {
        $o = new JSONObject();
        ((JSONObject)$o).put("alts", x);
      } else {
        $o = l;
      }
    }
	;

pathSequence returns [Object o] throws JSONException
    @init {
      JSONArray x = new JSONArray();
    }
	:    l=pathEltOrInverse ( '/' r=pathEltOrInverse { if (x.length()==0) { x.put(l); } x.put(r); } )*
    {
      if (x.length() > 0) {
        $o = new JSONObject();
        ((JSONObject)$o).put("seq", x);
      } else {
        $o = l;
      }
    }
	;

pathEltOrInverse returns [Object o] throws JSONException
	:    p=pathElt { $o=p; } | 
         '^' x=pathElt 
         {
           $o = new JSONObject();
           ((JSONObject)$o).put("inv", x);
         }
	;

pathElt returns [Object o] throws JSONException
    @init { Object x = null; }
	:    (pp=pathPrimary { x = pp; }
          (m=pathMod { JSONObject mod = new JSONObject(); mod.put("path", x); mod.put("modifider", m); $o = mod; } | { $o=x; } ))
	;

pathMod returns [String v] throws JSONException
	: '*'  { $v = "*"; } 
	| '?'  { $v = "?"; } 
	| '+'  { $v = "+"; } 
	;

pathPrimary returns [Object o] throws JSONException
	:    i=iRIref { $o = i; } |
        'a' { $o = new IRI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"); } |
        '!' p=pathNegatedPropertySet { $o = p; } |
        OPEN_BRACE p=path { $o = p; } CLOSE_BRACE
	;

pathNegatedPropertySet returns [JSONObject s] throws JSONException
	@init { $s = new JSONObject();
            JSONArray pos = new JSONArray();
            JSONArray neg = new JSONArray(); }
	: ( v = pathOneInPropertySet  { if (v.snd ) { pos.put(v.fst); } else {neg.put(v.fst);} }
	    | OPEN_BRACE ( v1 = pathOneInPropertySet {if (v1.snd ) { pos.put(v1.fst); } else {neg.put(v1.fst);} }
          ( '|' v2 = pathOneInPropertySet  { if (v2.snd ) { pos.put(v2.fst); } else {neg.put(v2.fst);} } )* )? 
          CLOSE_BRACE)
    {
    	if (pos.length() > 0) { $s.put("pathElts", pos); }
    	if (neg.length() > 0) { $s.put("invPathElts", neg); }
    }
	;

pathOneInPropertySet returns [Pair<IRI, Boolean> v ] throws JSONException
  	:  i = iRIref { $v = Pair.make(i, true); }
	| 'a'  { $v = Pair.make(new IRI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"), true); }
	| ^(INV invi = iRIrefOrRDFType ) { $v = Pair.make(invi, false); }
	;

iRIrefOrRDFType returns [ IRI v ] throws JSONException 
   : i =  iRIref { $v = i;}
   | 'a' { $v = new IRI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"); }
   ;
   
integer
	:    INTEGER
	;

triplesNode returns [JSONObject tn] throws JSONException
    @init { $tn = new JSONObject(); }
	:   ^( TRIPLES_NODE 
				(	c=collection 				{ $tn.put("collection", c);}
				| 	b=blankNodePropertyList     { $tn.put("propertyList", b);}
				)
		)
	;

blankNodePropertyList returns [JSONArray pl] throws JSONException
	@init {
			$pl = new JSONArray();
		}
	:	^( PROPERTY_LIST  	
					(	^(PREDICATE v=predicate)
					  	^(VALUE (o=graphNode 
                                 { JSONObject e = new JSONObject();  
                                   e.put("predicate", v);
                                   e.put("value", o);
                                   $pl.put(e);
                                 })+) 
					)+ 
		 )   
	;

predicate returns [Object p] throws JSONException
    @init { JSONObject o = null; JSONArray elts = null; }
	:	v=var       { $p = new Variable(v);  }
	|	i=iRIref    { $p = i; }
	| 	'a'			{ $p = new IRI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"); }
    |   ^(ALT { o = new JSONObject(); 
                elts = new JSONArray(); 
                o.put("alt", elts); }
              (alt=predicate { elts.put(alt); })*)
   	|   ^(SEQ { o = new JSONObject(); 
                elts = new JSONArray(); 
                o.put("seq", elts); }
              (seq=predicate { elts.put(seq); })*)
   	|   ^(ELT pred=predicate mod = pathMod) 
         { o = new JSONObject(); o.put("pred", pred); o.put("mod", mod); $p = o; }
    |   ^(INV pred=predicate)
         { o = new JSONObject(); o.put("inv", pred); $p = o; }
    | '!' neg = pathNegatedPropertySet 
         { o = new JSONObject(); o.put("neg", neg); $p = o; }
	;

collection returns [JSONArray c] throws JSONException
	@init { $c = new JSONArray(); }
	:    ^( COLLECTION (g=graphNode { $c.put(g); })+ ) 
	;

graphNode returns [Object gn] throws JSONException
	:   v=var				{ $gn = new Variable(v);			}
	| 	g=graphTerm 		{ $gn = g;						}
	| 	t=triplesNode		{ $gn = t;						}
	;

varOrTerm returns [Object qt] throws JSONException
	:    v=var 				{ $qt = new Variable(v); 	}
	| 	 g=graphTerm		{ $qt = g;					}
	;

varOrIRIref returns [Object qt] throws JSONException
	:   v=var  				{ $qt = new Variable(v); 	}
	| 	i=iRIref 			{ $qt = i; 	    }
	;

varOrIRIref2 returns [Object bu] throws JSONException
    : 	v=var 		{ $bu = new Variable(v); } 
    | 	i=iRIref   	{ $bu = i; }
    ;
	
var returns [String v] throws JSONException
	:   ^(VAR 	( x1=VAR1    { $v = new String(x1.getText().substring(1)); }
				| x2=VAR2	 { $v = new String(x2.getText().substring(1)); }
				)
		)		
	;

graphTerm returns [Object gt] throws JSONException
	:   i=iRIref 				{ $gt = i; }
	| 	r=rDFLiteral			{ $gt = r; } 
	|	d=numericLiteral 		{ $gt = d; } 
	| 	b=booleanLiteral		{ $gt = b; }  
	| 	l=blankNode				{ $gt = l; }  
	| 	NIL						{ $gt = new IRI("http://www.w3.org/1999/02/22-rdf-syntax-ns#nil"); }
	;

expression returns [JSONObject e] throws JSONException
	:   ^(LOGICAL_OR 			{ $e = new JSONObject(); $e.put("type", "or"); }
			e1=expression 		{ $e.put("left", e1);   }         
			e2=expression		{ $e.put("right", e2); 	}
			(e3=expression		{ JSONObject x = $e;
                                  $e = new JSONObject();
                                  $e.put("left", x);
                                  $e.put("right", e3); 	}	)*
		)						
	|   ^(LOGICAL_AND 			{ $e = new JSONObject(); $e.put("type", "and"); }
			e1=expression 		{ $e.put("left", e1);   }         
			e2=expression		{ $e.put("right", e2); 	}
			(e3=expression		{ JSONObject x = $e;
                                  $e = new JSONObject();
                                  $e.put("left", x);
                                  $e.put("right", e3); 	}	)*
		)						
	|   ^('=' 					{ $e = new JSONObject(); $e.put("type", "="); }
			e5=expression 		{ $e.put("left", e5); 				}
			e6=expression		{ $e.put("right", e6); }
		)
	|	^('!=' 					{ $e = new JSONObject(); $e.put("type", "!="); }
			e7=expression 		{ $e.put("left",e7); 				}
			e8=expression		{ $e.put("right", e8);    }
		)						
	|	^(LT  					{ $e = new JSONObject(); $e.put("type", "<"); }
			e9=expression       { $e.put("left",e9); 				}
			e10=expression		{ $e.put("right", e10);		  }
		)						
	|	^('>'  					{ $e = new JSONObject(); $e.put("type", ">"); }
			e11=expression  	{ $e.put("left",e11); 				}
			e12=expression		{ $e.put("right", e12);	  }
		)						
	|	^(LTE  				    { $e = new JSONObject(); $e.put("type", "<="); }
			e13=expression  	{ $e.put("left",e13); 				}
			e14=expression		{ $e.put("right", e14); }
		)					
	|	^('>='  				{ $e = new JSONObject(); $e.put("type", ">="); }
			e15=expression  	{ $e.put("left",e15); 				}
			e16=expression		{ $e.put("right", e16); }
		)						
	|	^(IN                    { $e = new JSONObject(); $e.put("type", "in"); }
			e17=expression      { $e.put("expr", e17); }
			e18=expressionList  { $e.put("values", e18); }
                                
		)
	|	^(NOT                   { $e = new JSONObject(); $e.put("type", "notin"); }
			e19=expression      { $e.put("expr", e19); }
			e20=expressionList  { $e.put("values", e20); }
		)                      
	|	^('+'                   { $e = new JSONObject(); $e.put("type", "+"); }
			e21=expression 		{ $e.put("left", e21); 				}
			(e22=expression		{ $e.put("right", e22);      			}	)?
		)						
	|	^(BROKEN_PLUS           { $e = new JSONObject(); $e.put("type", "+"); }
			e21=expression 		{ $e.put("left",e21); 				}
			e22=expression		{ $e.put("right", e22);      			}
		)						
	|	^('-'                   { $e = new JSONObject(); $e.put("type", "-"); }
			e23=expression 		{ $e.put("left", e23); }
			(e24=expression		{ $e.put("right", e24); })?
    )
	|	^(BROKEN_MINUS          { $e = new JSONObject(); $e.put("type", "-"); }
			e23=expression 		{ $e.put("left",e23); 				}
			e24=expression		{ $e.put("right",e24); 				}
		)						
	|	^('*'                   { $e = new JSONObject(); $e.put("type", "*"); }
			e25=expression 		{ $e.put("left", e25); 				}
			e26=expression		{ $e.put("right", e26);   				}
		)						
	|	^('/'                   { $e = new JSONObject(); $e.put("type", "/"); }
			e27=expression		{ $e.put("left", e27); 				}
			e28=expression		{ $e.put("right", e28);   			}
		)					
	|	^('!' e29=expression) 	{ $e = new JSONObject(); 
                                  $e.put("type", "!");
                                  $e.put("expr", e29); }
	|	e30=brackettedExpression { $e = new JSONObject(); $e.put("expr", e30); }
	| 	e31=primaryExpression 	{ $e = new JSONObject(); $e.put("expr", e31); }
	;


primaryExpression returns [Object e] throws JSONException  
	:  	e1=builtInCall 		{ $e = e1; }
	| 	i=iRIref		 	{ $e = i; }
	|	f=iRIFunction 		{ $e = f; }
	| 	r=rDFLiteral 		{ $e = r; }
	| 	n=numericLiteral 	{ $e = n; }
	| 	b=booleanLiteral 	{ $e = b; }
	| 	v=var 				{ $e = v; }
	| 	a=aggregate			{ $e = a; }
	;
  	
brackettedExpression returns [Object e] throws JSONException
	:	^(EXPRESSION  e1=expression)  { $e = e1; }
    ;

builtInCall	returns [JSONObject e] throws JSONException
	@init {
        String op = null;
		ArrayList args = new ArrayList();
	}
	: 	(^(STR st=expression)
			{ args.add(st); op = "str"; }
	| 	^(LANG lg=expression)
			{ args.add(lg); op = "lang"; }
	| 	^(LANGMATCHES lm1=expression lm2=expression)
			{ args.add(lm1); args.add(lm2); op = "langmatches"; }
	| 	^(DATATYPE dt=expression)
			{ args.add(dt); op = "datatype"; }
	|	^(BOUND v=var)    		
			{ args.add(v); op = "bound"; }
	|	^(IRI e6=expression)
			{ args.add(e6); op = "iri"; }
	| 	^(URI e7=expression)
			{ args.add(e7); op = "uri"; }
	|	^(BNODE e8=expression)
			{ args.add(e8); op = "bnode"; }
	|	BNODE
			{ op = "bnode"; }
	|	^(RAND NIL )
			{ op = "rand"; }
	|	^(ABS e9=expression)
			{ args.add(e9); op = "abs"; }
	|	^(CEIL e10=expression)
			{ args.add(e10); op = "ceil"; }
	|	^(FLOOR e11=expression)
			{ args.add(e11); op = "floor"; }
	|	^(ROUND e12=expression)
			{ args.add(e12); op = "round"; }
	|	^(CONCAT e13=expressionList)
			{ putAll(args, e13); op = "concat"; }
	| 	^(SUBSTR e14=expression e15=expression
			{ args.add(e14); args.add(e15); } 
            ( e16=expression { args.add(e16); } )? )
        {
          op = "substr";
        }
	|	^(STRLEN e15=expression)
			{ args.add(e15); op = "strlen"; }
	|	^(UCASE e16=expression)
			{ args.add(e16); op = "ucase"; }
	|	^(REPLACE e1=expression e2=expression e3=expression 
			{ args.add(e1); args.add(e2); args.add(e3); }
            ( e4=expression { args.add(e4); } )? )
			{ op = "replace"; }
	|	^(LCASE e17=expression)
			{ args.add(e17); op="lcase"; }
	|	^(ENCODE_FOR_URI e18=expression)
			{ args.add(e18); op = "encode_for_uri"; }
	|	^(CONTAINS e19=expression e20=expression)
			{ args.add(e19); args.add(e20); op = "contains"; }
	|	^(STRSTARTS e21=expression e22=expression)
			{ args.add(e21); args.add(e22); op = "strstarts"; }
	|	^(STRENDS e23=expression e24=expression)
			{ args.add(e23); args.add(e24); op = "strends"; }
	|	^(STRBEFORE e241=expression e242=expression)
			{ args.add(e241); args.add(e242); op = "strbefore"; }
	|	^(STRAFTER e243=expression e244=expression)
			{ args.add(e243); args.add(e244); op = "strafter"; }
	|	^(YEAR e25=expression)
			{ args.add(e25); op = "year"; }
	|	^(MONTH e26=expression)
			{ args.add(e26); op = "month"; }
	|	^(DAY e27=expression)
			{ args.add(e27); op = "day"; }
	|	^(HOURS e28=expression)
			{ args.add(e28); op = "hours"; }
	|	^(MINUTES e29=expression)
			{ args.add(e29); op = "minutes"; }
	|	^(SECONDS e30=expression)
			{ args.add(e30); op = "seconds"; }
	|	^(TIMEZONE e31=expression)
			{ args.add(e31); op = "timezone"; }
	|	^(TZ e32=expression)
			{ args.add(e32); op = "tz"; }
	|	NOW { op = "now"; }
    |   UUID { op = "uuid"; }
    |   STRUUID  { op = "struuid"; }
	|	^(MD5 e33=expression)
			{ args.add(e33); op = "md5"; }
	|	^(SHA1 e34=expression)
			{ args.add(e34); op = "sha1"; }
	|	^(SHA256 e36=expression)
			{ args.add(e36); op = "sha256"; }
	|	^(SHA384 e37=expression)
			{ args.add(e37); op = "sha384"; }
	|	^(SHA512 e38=expression)
			{ args.add(e37); op = "sha512"; }
	|	^(COALESCE e39=expressionList)
			{ putAll(args, e39); op = "coalesce"; }
	|	^(IF e40=expression e41=expression e42=expression)
			{ args.add(e40); args.add(e41); args.add(e42); op = "if"; }
	|	^(STRLANG e45=expression e46=expression)
			{ args.add(e45); args.add(e46); op = "strlang"; }
	|	^(STRDT e47=expression e48=expression)
			{ args.add(e34); op = "strdt"; }
	|	^(SAMETERM sam1=expression sam2=expression)
			{ args.add(sam1);  args.add(sam2); op = "sha1"; }
	|	^(ISIRI isi=expression)
			{ args.add(isi); op = "isiri"; }
	|	^(ISURI isu=expression)
			{ args.add(isu); op = "isuri"; }
	|	^(ISBLANK isb=expression)
			{ args.add(isb); op = "isblank"; }
	|	^(ISLITERAL isl=expression)
			{ args.add(isl); op = "isliteral"; }
	|	^(ISNUMERIC e55=expression)
			{ args.add(e55); op = "isnumeric"; }
	|   ^(REGEX e1=expression e2=expression e3=expression?)
			{ args.add(e1); args.add(e2); args.add(e3); op = "regex"; }
	|   p=existsFunc 
            { args.add(p); op = "exists"; }
	|   p=notExistsFunc 
            { args.add(p); op = "notexists"; })
    {
      $e = new JSONObject();
      $e.put("op", op);
      $e.put("args", new JSONArray(args));
    }
	;
  	
existsFunc returns [Object p] throws JSONException	  
	:  	^(EXISTS g=groupGraphPattern)
	   	{ $p = g; }
	;
	
notExistsFunc returns [Object p] throws JSONException	  
	:  	^(NOT_EXISTS g=groupGraphPattern)
	   	{ $p = g; }
	;

aggregate returns [JSONObject a] throws JSONException
	@init { 
		$a = new JSONObject(); 
	}
	: 	
		^(COUNT 				{ $a.put("aggregation", "count");  	}
			(DISTINCT 			{ $a.put("distinct", true);	}	)?  	                                     
			( e1=expression 	{ $a.put("expr", e1);		}
			| '*' 			
			)
		)
	| 	^(SUM					{ $a.put("aggregation", "sum");  	}
			(DISTINCT 			{ $a.put("distinct", true);	}	)?  
			e2=expression		{ $a.put("expr", e2);		}
		)
	|	^(MIN 					{ $a.put("aggregation", "min");  	}
			(DISTINCT 			{ $a.put("distinct", true);	}	)? 
			e3=expression		{ $a.put("expr", e3);		}
		)
	|	^(MAX 					{ $a.put("aggregation", "max");  	}
			(DISTINCT 			{ $a.put("distinct", true);	}	)?  
			e4=expression		{ $a.put("expr", e4);		}
		)
	|	^(AVG 					{ $a.put("aggregation", "avg");  	}
			(DISTINCT 			{ $a.put("distinct", true);	}	)?  
			e5=expression		{ $a.put("expr", e5);		}
		)
	|	^(SAMPLE 				{ $a.put("aggregation", "sample");  }
			(DISTINCT 			{ $a.put("distinct", true);	}	)?  
			e6=expression		{ $a.put("expr", e6);		}
		)
	| 	^(GROUP_CONCAT 			{ $a.put("aggregation", "group_concat"); } 
			(DISTINCT 			{ $a.put("distinct", true);	}	)?
			e7=expression 		{ $a.put("expr", e7);		}
			(^(SEPARATOR s=string	{ $a.put("separator", s);		})	)?
		)
	;
	
iRIFunction returns [JSONObject f] throws JSONException
	:  	^(FUNCTION  
				i=iRIref 	{ $f = new JSONObject(); $f.put("function", i); }
				(a=argList 	{ $f.put("arguments", a); 		} )?
		) 
	;
	
rDFLiteral returns [StringLiteral sl] throws JSONException 
	:  	s=string 					{ $sl = new StringLiteral(s);     }
		( l=LANGTAG 				{ $sl.setLanguage($l.text);        }
		| ( '^^' i=iRIref ) 		{ $sl.setType(i);       }
		)?
	;
	
numericLiteral returns [Constant n] throws JSONException
	:  	n1=numericLiteralUnsigned	{ $n = n1; }
	| 	n2=numericLiteralPositive	{ $n = n2; }
	| 	n3=numericLiteralNegative	{ $n = n3; }
	;
	
numericLiteralUnsigned returns [Constant c] throws JSONException
	:  	^( BIG_INTEGER  i=INTEGER )					{ $c = new Constant($i.text, new BigInteger($i.text));		}
	| 	^( BIG_DECIMAL	d1=DECIMAL )				{ $c = new Constant($d1.text, new BigDecimal($d1.text));	}
	| 	^( DOUBLE		d2=DOUBLE )					{ $c = new Constant($d2.text, new Double($d2.text));		}
	;
  	
numericLiteralPositive returns [Constant c] throws JSONException
	:  	^( BIG_INTEGER  i=INTEGER_POSITIVE )		{ $c = new Constant($i.text, new BigInteger($i.text.substring(1)));		}
	| 	^( BIG_DECIMAL	d1=DECIMAL_POSITIVE )		{ $c = new Constant($d1.text, new BigDecimal($d1.text.substring(1)));	}
	| 	^( DOUBLE		d2=DOUBLE_POSITIVE )		{ $c = new Constant($d2.text, new Double($d2.text.substring(1)));		}
	;
	
numericLiteralNegative returns [Constant c] throws JSONException
	:  	^( BIG_INTEGER  i=INTEGER_NEGATIVE )		{ $c = new Constant($i.text, new BigInteger($i.text));		}
	| 	^( BIG_DECIMAL	d1=DECIMAL_NEGATIVE )		{ $c = new Constant($d1.text, new BigDecimal($d1.text));	}
	| 	^( DOUBLE		d2=DOUBLE_NEGATIVE )		{ $c = new Constant($d2.text, new Double($d2.text));		}
	;
	
booleanLiteral returns [Boolean b] throws JSONException
	:  	^(BOOLEAN TRUE)     { $b = Boolean.TRUE;  }
	|   ^(BOOLEAN FALSE)    { $b = Boolean.FALSE; }
	;
	
string returns [String s] throws JSONException
	:	^(STRING s1=STRING_LITERAL1)         { $s = new String($s1.text); }
	|	^(STRING s2=STRING_LITERAL2)         { $s = new String($s2.text); }
	|	^(STRING s3=STRING_LITERAL_LONG1)    { $s = new String($s3.text); }
	|	^(STRING s4=STRING_LITERAL_LONG2)    { $s = new String($s4.text); }
	;
	
iRIref  returns [IRI r] throws JSONException
	:	^(IRI i=IRI_REF)	{ $r = new IRI($i.text.substring(1, $i.text.length()-1)); 	}
	|   p=prefixedName		{ $r = new IRI(p); 				}
	;
	
prefixedName returns [String s] throws JSONException  
	:	^(PREFIXED_NAME n1=PNAME_LN)	{ $s = new String($n1.getText()); }
	|	^(PREFIXED_NS n2=PNAME_NS)		{ $s = new String($n2.getText()); }
	;
	
blankNode returns [BlankNode bn] throws JSONException
	:  	b=BLANK_NODE_LABEL  { $bn = new BlankNode($b.getText()); }
	|   ^(ANNON t=OPEN_SQ_BRACKET)				{ $bn = new BlankNode("b" + t.getTokenStartIndex()); }
	;

