grammar IbmSparqlExt;

options {
	language = Java;
	output = AST;
    ASTLabelType=XTree;
}			
	
tokens {
    PATH;
    ALT;
    SEQ;
    ELT;
    INV;
    BROKEN_PLUS;
    BROKEN_MINUS;
    NIL;
    ANNON;
	ROOT;
	PROLOGUE;
	DEFAULT_NAMESPACE;
	NAMESPACE_PREFIX_MAP;
	KEY;
	QUERY;
	UPDATE;
	TYPE;
	PVARS;
	EXP;
	NOT_IN;
	GROUP_GRAPH_PATTERN;
	GROUP_GRAPH_PATTERN_SUB;
	GRAPH_GRAPH_PATTERN;
	SUB_SELECT;
	TRIPLES_BLOCK;
	NON_TRIPLES;
	TRIPLE;
	TRIPLE2;
	TRIPLES_SAME_SUBJECT;
	GRAPH_NODE;
	VAR;
	PREFIXED_NAME;
	PREFIXED_NS;
	FUNCTION;
	EXPRESSION;
	NOT_EXISTS;
	IRI_OR_FUNCTION;
	DATASET;
	GROUP_BY;
	ORDER_BY;
	CONDITION;
	BIND_VALUES;
	STRING;
	BOOLEAN;
	NUMERIC;
	SUBJECT;
	PREDICATE;
	VALUE;
	TRIPLES_NODE_PROPERTY_LIST;
	TRIPLES_NODE;
	COLLECTION;
	PROPERTY_LIST;
	PREDICATE_VALUE;
    WHERE;
    IRI_REF;
    LTE;
    MODIFIERS;
    BIG_INTEGER;
    BIG_DECIMAL;
    INLINE_DATA;
    
    //added by wensun
    OUTV;
    INV;
    FUNCBODY;
    FUNCNAME;
    FUNCLG;

    PARAM;
    PARAMS;
    KIND;
}

@header { 
package com.ibm.research.rdf.store.sparql11;
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
}

@rulecatch {
	catch(RecognitionException re) {
		throw new SPARQLsyntaxError(re);
	}
}

@lexer::header { 
package com.ibm.research.rdf.store.sparql11; 
}


@lexer::members {
	private static boolean stripStringQuotes = false;
	static { 
		String prop = System.getProperty("stripQuotesForPlainLiteral");
		if ((prop != null) && (prop.equalsIgnoreCase("true"))) {
			stripStringQuotes = true;
		}
	}
}

queryUnit
	:	query  //{System.out.println("Done.\n");}
	
		->  ^( ROOT query )
	
	;
			
query 
	:	p=prologue
		( s=selectQuery | c=constructQuery | d=describeQuery | a=askQuery )
		b=bindingsClause?
		
		->  ^( QUERY $p $s? $c? $d? $a? $b? )
	;
					
updateUnit	  
	:  	update
	
		->  ^( UPDATE update )
	;
  	
prologue	  
	:  	( b+=baseDecl | p+=prefixDecl )*
	
		->  ^( PROLOGUE $b*  $p* )
	;
	
baseDecl	  
	:  	BASE i=iRIref 
	
		->  ^( BASE $i )
	;

prefixDecl	  
	:  	PREFIX p=PNAME_NS i=iRIref
        
		->  ^( PREFIX ^(PREFIXED_NS $p) $i )
	;

//modified by wensun
selectQuery	  
	:  	f+=functionDecl* s=selectClause d+=datasetClause* w=whereClause m=solutionModifier
		->  ^(FUNCTION $f*)? ^(SELECT $s ^(DATASET $d*)? $w? $m? )
	;
	
functionParam
    :  param=string ARROW ( valueE=expression | valueP=groupGraphPattern | POST DATA )
       -> ^(PARAM $param $valueE? $valueP?) 
;

//added by wensun
functionDecl
	:	FUNCTION fn=iRIref kind=(POST ALL? | GET)? OPEN_BRACE inv+=var* ARROW outv+=var* CLOSE_BRACE
        ( ( FUNCLANG fl=VAR0 fb=functionBody
    	  -> ^( FUNCNAME $fn ^(KIND $kind?) ^(INV $inv*) ^(OUTV $outv*) ^(FUNCLG $fl) $fb ) )
        |
        ( ( x=SERVICE | x=TABLE ) s=expression OPEN_SQ_BRACKET params+=functionParam* CLOSE_SQ_BRACKET ARROW rowdef=string '::' ( col+=string )+
          -> ^( FUNCNAME ^($x $s $fn) ^(KIND $kind?) ^(INV $inv*) ^(OUTV $outv*) ^(PARAMS $params*) $rowdef $col* )
        ) )
	;

//added by wensun
functionBody
	: OPEN_CURLY_BRACE f=STRING_LINE_NONEOP CLOSE_CURLY_BRACE
	  ->  ^( FUNCBODY $f* )
	| OPEN_CURLY_BRACE p=groupGraphPattern CLOSE_CURLY_BRACE
	  ->  ^( FUNCBODY $p )
	;
	
subSelect	  
	:  	s=selectClause w=whereClause m=solutionModifier (d=inlineData)?
		->  ^( SUB_SELECT $s $w $m $d? )
	;
	
selectClause	  
	:  	SELECT ( t1=DISTINCT | t2=REDUCED )? ( ( v1+=var | e+=expAsVar | f+=fexp )+ | '*' )

		->   ^(TYPE $t1? $t2?)? ^(PVARS $v1* $e* $f* '*'?) 
	;
	
expAsVar
	:	OPEN_BRACE e=expression AS v=var CLOSE_BRACE
	
		->	^( AS  $v  $e )
	;

fexp :	( a=aggregate | b=builtInCall )

		->  ^( EXP $a? $b? )
		
	;
	
constructQuery	  
	:  	CONSTRUCT 
	    (
	      (c=constructTemplate d1+=datasetClause* w1=whereClause s1=solutionModifier)
		  -> ^( CONSTRUCT $c? ^(DATASET $d1*)? $w1 $s1) |
	      (d2+=datasetClause* WHERE_TOKEN OPEN_CURLY_BRACE t=triplesTemplate? CLOSE_CURLY_BRACE s2=solutionModifier)
		  -> ^( CONSTRUCT ^(DATASET $d2*)? (^(WHERE $t))? $s2 ) 
	    )
	;
			
describeQuery	  
	:  	DESCRIBE ( v+=varOrIRIref+ | r='*' ) d=datasetClause* w=whereClause? s=solutionModifier
	
		->  ^( DESCRIBE  $v*  $r?  $d*  $w?  $s )
	;
	
askQuery	  
	:  	ASK d=datasetClause* w=whereClause
	
		->  ^( ASK $d* $w )
	;
	
datasetClause	  
	:  	FROM ( d=defaultGraphClause | n=namedGraphClause )
	
		->  ^( FROM $d? ^(NAMED $n)? )
	;
	
defaultGraphClause	  
	:  	sourceSelector
	;
	
namedGraphClause	  
	:  	NAMED s=sourceSelector  -> $s
	;
	
sourceSelector	  
	:  	iRIref
	;
	
whereClause	  
	:  	WHERE_TOKEN? groupGraphPattern
	
		->  ^( WHERE groupGraphPattern )
	;
	
solutionModifier	  
	:  	g=groupClause? h=havingClause? o=orderClause? l=limitOffsetClauses?
		-> ^(MODIFIERS $g? $h? $o? $l?)
	;
	
groupClause	  
	:  	GROUP BY g+=groupCondition+
	
		->  ^( GROUP_BY $g+ )
	;
	
groupCondition	  
	:  	builtInCall 
	|   functionCall 
	|   OPEN_BRACE e=expression ( AS v=var )? CLOSE_BRACE   ->  ^( CONDITION  $e  $v? )
	|   var
	;
	
havingClause	  
	:  	HAVING h+=havingCondition+  // { System.out.println("==> Found Having Clause <=="); }
	
		->  ^( HAVING $h+ )
	;
	
havingCondition	  
	:  	constraint
	;
	
orderClause	  
	:  	ORDER BY o+=orderCondition+
	
		->  ^( ORDER_BY $o+ )
	;
		
orderCondition	
	: ( ( ASC^ | DESC^ ) brackettedExpression )
	| constraint 
    | var
	;
	
limitOffsetClauses	  
	:  	limitClause offsetClause? | offsetClause limitClause?
	;
	
limitClause	  
	:  	LIMIT i=INTEGER   ->  ^( LIMIT  $i )
	;
	
offsetClause	  
	:  	OFFSET i=INTEGER  ->  ^( OFFSET  $i )
	;
	
bindingsClause	  
	:  	BINDINGS v+=var* OPEN_CURLY_BRACE ( OPEN_BRACE bv+=bindingValue+ CLOSE_BRACE | nil )* CLOSE_CURLY_BRACE
	
		->  ^( BINDINGS  $v*  ^(BIND_VALUES $bv* nil?) )
    |   inlineData
	;
	
bindingValue	  
	:  	( iRIref | rDFLiteral | numericLiteral | booleanLiteral | UNDEF )
	;
	
update	  
	:  	prologue ( update1 ( SEMICOLON update )? )?
	;
	
update1	  
	:  	load | clear | drop | add | move | copy | create | insertData | deleteData | deleteWhere | modify
	;
	
load	  
	:  	LOAD SILENT? iRIref ( INTO graphRef )?
	;
	
clear	  
	:  	CLEAR SILENT? graphRefAll
	;
	
drop	  
	:  	DROP SILENT? graphRefAll
	;
	
create	  
	:  	CREATE SILENT? graphRef
	;
	
add	  
	:  	ADD SILENT? graphOrDefault TO graphOrDefault
	;
	
move	  
	:  	MOVE SILENT? graphOrDefault TO graphOrDefault
	;
	
copy	  
	:  	COPY SILENT? graphOrDefault TO graphOrDefault
	;
	
insertData	  
	:  	INSERT DATA quadData
	;

deleteData	  
	:  	DELETE DATA quadData
	;
	
deleteWhere	  
	:  	DELETE WHERE_TOKEN quadPattern
	;
	
modify	  
	:  	( WITH iRIref )? ( deleteClause insertClause? | insertClause ) usingClause* WHERE_TOKEN groupGraphPattern
	;

deleteClause	  
	:  	DELETE quadPattern
	;
	
insertClause	  
	:  	INSERT quadPattern
	;
	
usingClause	  
	:  	USING ( iRIref | NAMED iRIref )
	;
	
graphOrDefault	  
	:  	DEFAULT | GRAPH? iRIref
	;
	
graphRef	  
	:  	GRAPH iRIref
	;

graphRefAll
	:    graphRef | DEFAULT | NAMED | ALL
	;

quadPattern
	:    OPEN_CURLY_BRACE quads CLOSE_CURLY_BRACE
	;

quadData
	:    OPEN_CURLY_BRACE quads CLOSE_CURLY_BRACE
	;

quads
	:    triplesTemplate? ( quadsNotTriples DOT? triplesTemplate? )*
	;

quadsNotTriples
	:    GRAPH varOrIRIref OPEN_CURLY_BRACE triplesTemplate? CLOSE_CURLY_BRACE
	;

triplesTemplate
	:    triplesSameSubject ( DOT triplesTemplate? )?
	;

groupGraphPattern
	:    OPEN_CURLY_BRACE ( s=subSelect | g=groupGraphPatternSub ) CLOSE_CURLY_BRACE
	
		->  ^( GROUP_GRAPH_PATTERN subSelect? groupGraphPatternSub? )
	;
	
groupGraphPatternSub
	:    triplesBlock? groupGraphPatternSubInt*
		->  ^(TRIPLES_BLOCK triplesBlock)? groupGraphPatternSubInt*
	;

groupGraphPatternSubInt
	:    graphPatternNotTriples DOT? triplesBlock?
		->  graphPatternNotTriples ^(TRIPLES_BLOCK triplesBlock)?
	;

triplesBlock
	:    t1=triplesSameSubjectPath ( DOT t2=triplesBlock? )? 	->  triplesSameSubjectPath (triplesBlock+)?
	;
 
graphPatternNotTriples
	:    groupOrUnionGraphPattern | 
         optionalGraphPattern |
         minusGraphPattern | 
         graphGraphPattern | 
         serviceGraphPattern | 
         filter | 
         bind |
         inlineData
    ;

inlineData
    :    VALUES dataBlock -> ^(VALUES dataBlock)
    ;

dataBlock
    :    inlineDataOneVar | inlineDataFull
    ;

inlineDataOneVar
    :    v=var OPEN_CURLY_BRACE b+=dataBlockValue* CLOSE_CURLY_BRACE -> ^(INLINE_DATA $v $b*)
    ;

inlineDataFull
    :    OPEN_BRACE v+=var* CLOSE_BRACE OPEN_CURLY_BRACE ( OPEN_BRACE b+=dataBlockValue* CLOSE_BRACE )* CLOSE_CURLY_BRACE  -> ^(INLINE_DATA $v* $b*)
    ;

dataBlockValue  
	:    iRIref | rDFLiteral | numericLiteral | booleanLiteral | UNDEF ;

optionalGraphPattern
	:    OPTIONAL g=groupGraphPattern
	
		->  ^( OPTIONAL $g )
	;

graphGraphPattern
	:    GRAPH v=varOrIRIref g=groupGraphPattern
	
		->  ^( GRAPH $v $g )
	;

serviceGraphPattern
    @after { $serviceGraphPattern.tree.matched = $serviceGraphPattern.text; }
	:    SERVICE s=SILENT? v=varOrIRIref g=groupGraphPattern
	
		->  ^( SERVICE  $s?  $v  $g )
	;

//added by wensun
bind	
	:	bind1
	|	bind2
	;
	
//modified by wensun
bind1
	:    BIND OPEN_BRACE e=expression AS v=var CLOSE_BRACE
	
		->  ^( BIND  $v  $e )
	;

//added by wensun
bind2
	:    BIND OPEN_BRACE f=funcCall AS OPEN_BRACE v+=var+ CLOSE_BRACE CLOSE_BRACE
	
		->  ^( BIND  $f  $v* )
	;
	
funcCall
	:    fn=iRIref OPEN_BRACE v+=var* CLOSE_BRACE
		->  ^( FUNCCALL  $fn  $v* )
	;

groupOrUnionGraphPattern
	:    g1=groupGraphPattern ( ( UNION g2+=groupGraphPattern )+ -> ^( UNION  $g1  $g2+ ) | -> $g1 )
	;

minusGraphPattern 
    :    MINUS n=groupGraphPattern -> ^(MINUS $n)
    ;

filter
	:    FILTER c=constraint
	
		->  ^( FILTER  $c )
	;

constraint
	:    brackettedExpression | builtInCall | functionCall
	;

functionCall
	:    i=iRIref a=argList
	
		->  ^( FUNCTION $i $a )
	;

argList
	:    nil   ->  NIL
	|    OPEN_BRACE d=DISTINCT? e1=expression ( COMMA e2+=expression )* CLOSE_BRACE
		       ->  $d? $e1  $e2*
	;

expressionList
	:    nil   ->  NIL
	|    OPEN_BRACE e1=expression ( COMMA e2+=expression )* CLOSE_BRACE
	           ->  $e1  $e2*
	;

constructTemplate
	:    OPEN_CURLY_BRACE! constructTriples? CLOSE_CURLY_BRACE!
	;

constructTriples
	:    triplesSameSubject ( DOT! constructTriples? )?
	;

triplesSameSubject
	:   (s1=varOrTerm p1=propertyListNotEmpty[(CommonTree)$s1.tree, 1])  	->   propertyListNotEmpty
	|   (s2=triplesNode p2=propertyList[(CommonTree)$s2.tree, 2])        	->	 ^( TRIPLE2 ^( SUBJECT $s2 ) $p2? ) 
	;

propertyListNotEmpty[CommonTree s, int t]
	:   p1=verb objectList[$s, (CommonTree)$p1.tree, $t]
		( SEMICOLON (p2=verb objectList[$s, (CommonTree)$p2.tree, $t] )? )*
		
		-> objectList+
	;

propertyList[CommonTree s, int t]
	:    propertyListNotEmpty[$s, $t]?
	;

objectList[CommonTree s, CommonTree p, int t]
	:    
		object (COMMA object)*

		-> {t==3}?  ( ^(PREDICATE {$p}) ^(VALUE object) )+
		-> {t==2}?  ^( PROPERTY_LIST ^(PREDICATE {$p}) (^(VALUE object))+ )
		-> 	 		^( TRIPLE ^(SUBJECT {$s}) ^(PREDICATE {$p}) ^(VALUE object) )+
	; 
  
objectListPath[CommonTree s, CommonTree p, int t]
	:    
		objectPath (COMMA objectPath)*

		-> {t==3}?  ( ^(PREDICATE {$p}) ^(VALUE objectPath) )+
		-> {t==2}?  ^( PROPERTY_LIST ^(PREDICATE {$p}) (^(VALUE objectPath))+ )
		-> 	 		^( TRIPLE ^(SUBJECT {$s}) ^(PREDICATE {$p}) ^(VALUE objectPath) )+
	; 
  
object
	:    graphNode
	;

objectPath
	:    graphNodePath
	;


verb 
	:   v=varOrIRIref  	
	| 	'a'
	;
		
triplesSameSubjectPath
	:   (s1=varOrTerm p1+=propertyListNotEmptyPath[(CommonTree)$s1.tree, 1])   	->  propertyListNotEmptyPath
	|   (s2=triplesNodePath p2+=propertyListPath[(CommonTree)$s2.tree, 2])     		->	 ^( TRIPLE2 ^( SUBJECT $s2 ) $p2? ) 
	;

propertyListNotEmptyPath[CommonTree s, int t]
	:    ( p1=verbPath v1=objectListPath[$s,(CommonTree)$p1.tree, $t]        	
		 | p2=verbSimple v2=objectListPath[$s,(CommonTree)$p2.tree, $t] 		
		 )
		 ( SEMICOLON ( ( p3=verbPath v3=objectList[$s,(CommonTree)$p3.tree, $t]		 
		                | p4=verbSimple v4=objectList[$s,(CommonTree)$p4.tree, $t] 	 
		                ) 
		              )? 
		 )*
		 
		 ->  objectListPath objectList*
	;
	
propertyListPath[CommonTree s, int t]
	:    propertyListNotEmptyPath[$s, $t]?		 ->  propertyListNotEmptyPath?
	;

verbPath
	:    path
	;

verbSimple
	:    var
	;

path
	:    pathAlternative
	;

pathAlternative
	:    s1=pathSequence 
         ( ('|' s2+=pathSequence)+ -> ^(ALT $s1 $s2+)
           | -> $s1 )
	;

pathSequence
	:    s1=pathEltOrInverse 
        ( ('/' s2+=pathEltOrInverse)+ -> ^(SEQ $s1 $s2+)
          | -> $s1 )
	;

pathElt
	:   pathPrimary 
        ( (pathMod -> ^(ELT pathPrimary pathMod))
	      | -> pathPrimary)
    ;

pathEltOrInverse
	:    pathElt | ('^' pathElt -> ^(INV pathElt))
	;

pathMod
	:    '*' | '?' | '+' 
	;

pathPrimary
	:    iRIref | 'a' | '!' pathNegatedPropertySet | OPEN_BRACE! path CLOSE_BRACE!
	;

pathNegatedPropertySet
	:    pathOneInPropertySet | OPEN_BRACE ( pathOneInPropertySet ( '|' pathOneInPropertySet )* )? CLOSE_BRACE
	;

pathOneInPropertySet
	:    iRIref | 'a' | ('^' iRIref -> ^(INV iRIref)) |  ('^' 'a' -> ^(INV 'a'))
	;

integer
	:    INTEGER
	;

triplesNode
	:    ( c=collection
		 | p=blankNodePropertyList
		 )
			-> ^(TRIPLES_NODE $c? $p?)
	;

triplesNodePath
	:    ( c=collectionPath   
		 | p=blankNodePropertyListPath 
		 )
			-> ^(TRIPLES_NODE $c? $p?)
	;

blankNodePropertyList
	:    OPEN_SQ_BRACKET p=propertyListNotEmpty[null, 3] CLOSE_SQ_BRACKET
	
		-> ^( PROPERTY_LIST $p )
	;

blankNodePropertyListPath
	:    OPEN_SQ_BRACKET p=propertyListNotEmptyPath[null, 3] CLOSE_SQ_BRACKET
	
		-> ^( PROPERTY_LIST $p )
	;

collection
	:   (OPEN_BRACE g+=graphNode+ CLOSE_BRACE)
	
		->  ^( COLLECTION $g+ )
	;

collectionPath
	:   (OPEN_BRACE g+=graphNodePath+ CLOSE_BRACE)
	
		->  ^( COLLECTION $g+ )
	;

graphNode
	:    varOrTerm | triplesNode
	;

graphNodePath
	:    varOrTerm | triplesNodePath
	;


varOrTerm
	:    var | graphTerm
	;

varOrIRIref
	:    var | iRIref
	;

var
	:    v1=VAR1    ->  ^(VAR $v1)
	|    v2=VAR2    ->  ^(VAR $v2)
	;

graphTerm
	:    iRIref | rDFLiteral | numericLiteral | booleanLiteral | blankNode | nil
	;

expression
	:    conditionalOrExpression
	;

conditionalOrExpression
	@init {boolean isOR=false;}
	:    c1=conditionalAndExpression ( LOGICAL_OR c2+=conditionalAndExpression {isOR=true;})*
		
		->  {isOR==true}?  ^( LOGICAL_OR  $c1  $c2+ )
		->  $c1
	;

conditionalAndExpression
	@init {boolean isAnd=false;}
	:    v1=valueLogical ( LOGICAL_AND v2+=valueLogical {isAnd=true;} )*
	
		->  {isAnd==true}?  ^(LOGICAL_AND  $v1  $v2+ )
		->  $v1
	;

valueLogical
	:    relationalExpression
	;

relationalExpression
	:    n1=numericExpression ( '='^   n2=numericExpression
						      | '!='^  n3=numericExpression   
						      | LT^   n4=numericExpression   
						      | '>'^   n5=numericExpression   
						      | LTE^  n6=numericExpression   
						      | '>='^  n7=numericExpression   
						      | IN^  e1=expressionList        
						      | NOT^ IN!  e2=expressionList     
						      )?                             
	;
	
numericExpression	  
	:  	additiveExpression
	;
	
additiveExpression	  
	:  	(lhs=multiplicativeExpression -> $lhs)
         ( '+' rhs1=multiplicativeExpression -> ^('+' $additiveExpression $rhs1)
         | '-' rhs2=multiplicativeExpression -> ^('-' $additiveExpression $rhs2)
         |	litp=numericLiteralPositive 
            ( op1=('*'|'/') rhs3=multiplicativeExpression
              -> ^(BROKEN_PLUS $additiveExpression ^($op1 $litp $rhs3))
            |
              -> ^(BROKEN_PLUS $additiveExpression $litp)
            )
         |	litn=numericLiteralNegative 
            ( op2=('*'|'/') rhs4=multiplicativeExpression
              -> ^(BROKEN_MINUS $additiveExpression ^($op2 $litn $rhs4))
            | 
              -> ^(BROKEN_MINUS $additiveExpression $litn)
            )
         )*
	;

multiplicativeExpression	  
	:  	unaryExpression  ( '*'^ unaryExpression 
						 | '/'^ unaryExpression 
						 )*
	;
	
unaryExpression	  
	:	'!'^ primaryExpression
	| 	'+'^ primaryExpression
	| 	'-'^ primaryExpression
	| 	primaryExpression
	;
	
primaryExpression	  
	:  	brackettedExpression 
	| 	builtInCall 
	| 	iRIrefOrFunction 
	| 	rDFLiteral 
	| 	numericLiteral 
	| 	booleanLiteral 
	| 	var 
	| 	aggregate
	;
  	
brackettedExpression	  
	:  	( options {backtrack=true;} : OPEN_BRACE e=expression CLOSE_BRACE )
		
		->  ^( EXPRESSION  $e )
	;
	
builtInCall	  
	: STR OPEN_BRACE e1=expression CLOSE_BRACE                ->  ^( STR $e1 )
	| LANG OPEN_BRACE e2=expression CLOSE_BRACE               ->  ^( LANG $e2 )
	| LANGMATCHES OPEN_BRACE e3=expression COMMA e4=expression CLOSE_BRACE      ->  ^( LANGMATCHES $e3 $e4 )
	| DATATYPE OPEN_BRACE e5=expression CLOSE_BRACE           ->  ^( DATATYPE $e5 )
	| BOUND OPEN_BRACE v=var CLOSE_BRACE                      ->  ^( BOUND $v )
	| IRI OPEN_BRACE e51=expression CLOSE_BRACE               ->  ^( IRI $e51 )
	| URI OPEN_BRACE e6=expression CLOSE_BRACE                ->  ^( URI $e6 )
	| BNODE ( OPEN_BRACE e7=expression CLOSE_BRACE | nil )    ->  ^( BNODE $e7? )
	| RAND nil                                                ->  ^( RAND NIL )
	| ABS OPEN_BRACE e8=expression CLOSE_BRACE                ->  ^( ABS $e8 )
	| CEIL OPEN_BRACE e9=expression CLOSE_BRACE               ->  ^( CEIL $e9 )
	| FLOOR OPEN_BRACE e10=expression CLOSE_BRACE             ->  ^( FLOOR $e10 )
	| ROUND OPEN_BRACE e11=expression CLOSE_BRACE             ->  ^( ROUND $e11 )
	| CONCAT e12=expressionList                               ->  ^( CONCAT $e12 )
	| s=substringExpression                                   ->  $s
	| STRLEN OPEN_BRACE e13=expression CLOSE_BRACE            ->  ^( STRLEN $e13 )
    | strReplaceExpression
	| UCASE OPEN_BRACE e14=expression CLOSE_BRACE             ->  ^( UCASE $e14 )
	| LCASE OPEN_BRACE e15=expression CLOSE_BRACE             ->  ^( LCASE $e15 )
	| ENCODE_FOR_URI OPEN_BRACE e16=expression CLOSE_BRACE    ->  ^( ENCODE_FOR_URI $e16 )
	| CONTAINS OPEN_BRACE e17=expression COMMA e18=expression CLOSE_BRACE       ->  ^( CONTAINS $e17 $e18 )
	| STRSTARTS OPEN_BRACE e19=expression COMMA e20=expression CLOSE_BRACE      ->  ^( STRSTARTS $e19 $e20 )
	| STRENDS OPEN_BRACE e21=expression COMMA e22=expression CLOSE_BRACE        ->  ^( STRENDS $e21 $e22 )
	| STRBEFORE OPEN_BRACE e21=expression COMMA e22=expression CLOSE_BRACE        ->  ^( STRBEFORE $e21 $e22 )
	| STRAFTER OPEN_BRACE e21=expression COMMA e22=expression CLOSE_BRACE        ->  ^( STRAFTER $e21 $e22 )
	| YEAR OPEN_BRACE e23=expression CLOSE_BRACE              ->  ^( YEAR $e23 )
	| MONTH OPEN_BRACE e24=expression CLOSE_BRACE             ->  ^( MONTH $e24 )
	| DAY OPEN_BRACE e25=expression CLOSE_BRACE               ->  ^( DAY $e25 )
	| HOURS OPEN_BRACE e26=expression CLOSE_BRACE             ->  ^( HOURS $e26 )
	| MINUTES OPEN_BRACE e27=expression CLOSE_BRACE           ->  ^( MINUTES $e27 )
	| SECONDS OPEN_BRACE e28=expression CLOSE_BRACE           ->  ^( SECONDS $e28 )
	| TIMEZONE OPEN_BRACE e29=expression CLOSE_BRACE          ->  ^( TIMEZONE $e29 )
	| TZ OPEN_BRACE e30=expression CLOSE_BRACE                ->  ^( TZ $e30 )
	| NOW nil -> NOW
	| UUID nil -> UUID
	| STRUUID nil -> STRUUID
	| MD5 OPEN_BRACE e31=expression CLOSE_BRACE               ->  ^( MD5 $e31 )
	| SHA1 OPEN_BRACE e32=expression CLOSE_BRACE              ->  ^( SHA1 $e32 )
	| SHA224 OPEN_BRACE e33=expression CLOSE_BRACE            ->  ^( SHA224 $e33 )
	| SHA256 OPEN_BRACE e34=expression CLOSE_BRACE            ->  ^( SHA256 $e34 )
	| SHA384 OPEN_BRACE e35=expression CLOSE_BRACE            ->  ^( SHA384 $e35 )
	| SHA512 OPEN_BRACE e36=expression CLOSE_BRACE            ->  ^( SHA512 $e36 )
	| COALESCE e37=expressionList                             ->  ^( COALESCE $e37 )
	| IF OPEN_BRACE e38=expression COMMA e39=expression COMMA e40=expression CLOSE_BRACE    ->  ^( IF $e38 $e39 $e40 )
	| STRLANG OPEN_BRACE e41=expression COMMA e42=expression CLOSE_BRACE                    ->  ^( STRLANG $e41 $e42 )
	| STRDT OPEN_BRACE e43=expression COMMA e44=expression CLOSE_BRACE                      ->  ^( STRDT $e43 $e44 )
	| SAMETERM OPEN_BRACE e45=expression COMMA e46=expression CLOSE_BRACE                   ->  ^( SAMETERM $e45 $e46 )
	| ISIRI OPEN_BRACE e47=expression CLOSE_BRACE             ->  ^( ISIRI $e47 )
	| ISURI OPEN_BRACE e48=expression CLOSE_BRACE             ->  ^( ISURI $e48 )
	| ISBLANK OPEN_BRACE e52=expression CLOSE_BRACE           ->  ^( ISBLANK $e52 )
	| ISLITERAL OPEN_BRACE e49=expression CLOSE_BRACE         ->  ^( ISLITERAL $e49 )
	| ISNUMERIC OPEN_BRACE e50=expression CLOSE_BRACE         ->  ^( ISNUMERIC $e50 )
	| regexExpression
	| existsFunc
	| notExistsFunc
	;
  	
regexExpression	  
	:	REGEX OPEN_BRACE e1=expression COMMA e2=expression ( COMMA e3=expression )? CLOSE_BRACE
	
		->  ^( REGEX  $e1  $e2  $e3? )
	;
	
substringExpression	  
	:  	SUBSTR OPEN_BRACE e1=expression COMMA e2=expression ( COMMA e3=expression )? CLOSE_BRACE
	
		->  ^( SUBSTR  $e1  $e2  $e3? )
	;

strReplaceExpression
    :   REPLACE OPEN_BRACE e1=expression COMMA e2=expression COMMA e3=expression (COMMA e4=expression)? CLOSE_BRACE
        -> ^(REPLACE $e1  $e2  $e3 $e4? )
    ;

existsFunc	  
	:  	EXISTS g=groupGraphPattern
	
		->  ^( EXISTS  $g )
	;
	
notExistsFunc	  
	:  	NOT EXISTS g=groupGraphPattern
	
		->  ^( NOT_EXISTS  $g )
	;
	
aggregate	  
	: COUNT OPEN_BRACE d1=DISTINCT? ( '*' | e1=expression ) CLOSE_BRACE     ->  ^( COUNT  $d1?  '*'?  $e1? )
	| SUM OPEN_BRACE d2=DISTINCT? e2=expression CLOSE_BRACE    ->  ^( SUM $d2? $e2 )
	| MIN OPEN_BRACE d3=DISTINCT? e3=expression CLOSE_BRACE    ->  ^( MIN $d3? $e3 )
	| MAX OPEN_BRACE d4=DISTINCT? e4=expression CLOSE_BRACE    ->  ^( MAX $d4? $e4 )
	| AVG OPEN_BRACE d5=DISTINCT? e5=expression CLOSE_BRACE    ->  ^( AVG $d5? $e5 )
	| SAMPLE OPEN_BRACE d6=DISTINCT? e6=expression CLOSE_BRACE ->  ^( SAMPLE $d6? $e6 )
	| GROUP_CONCAT OPEN_BRACE d7=DISTINCT? e7=expression ( SEMICOLON SEPARATOR '=' s=string )? CLOSE_BRACE
		->  ^( GROUP_CONCAT $d7? $e7  ^(SEPARATOR  $s)? )
	;
	
iRIrefOrFunction
	@init { 
		boolean f=false;
	}	  
	:  	i=iRIref (a=argList {f=true;})?
	
		->  {f==false}?  $i
		->  ^( FUNCTION  $i  $a? ) 
	;
	
rDFLiteral	  
	:  	string ( LANGTAG | ( '^^' iRIref ) )?
	;
	
numericLiteral	  
	:  	numericLiteralUnsigned 	
	| 	numericLiteralPositive 	
	| 	numericLiteralNegative	
	;
	
numericLiteralUnsigned	  
	:  	INTEGER 				->	^( BIG_INTEGER  INTEGER )
	| 	DECIMAL 				->	^( BIG_DECIMAL	DECIMAL )
	| 	DOUBLE					->	^( DOUBLE		DOUBLE )
	;
  	
numericLiteralPositive	  
	:  	INTEGER_POSITIVE 		->	^( BIG_INTEGER  INTEGER_POSITIVE )
	| 	DECIMAL_POSITIVE 		->	^( BIG_DECIMAL	DECIMAL_POSITIVE )
	| 	DOUBLE_POSITIVE			->	^( DOUBLE		DOUBLE_POSITIVE )
	;
	
numericLiteralNegative	  
	:  	INTEGER_NEGATIVE 		->	^( BIG_INTEGER  INTEGER_NEGATIVE )
	| 	DECIMAL_NEGATIVE 		->	^( BIG_DECIMAL	DECIMAL_NEGATIVE )
	| 	DOUBLE_NEGATIVE			->	^( DOUBLE		DOUBLE_NEGATIVE )
	;
	
booleanLiteral	  
	:  	t=TRUE 		->  ^( BOOLEAN $t )
	|   f=FALSE		->  ^( BOOLEAN $f )
	;
	
string	  
	:  	s1=STRING_LITERAL1         ->  ^( STRING $s1 )
	| 	s2=STRING_LITERAL2         ->  ^( STRING $s2 )
	| 	s3=STRING_LITERAL_LONG1    ->  ^( STRING $s3 )
	|	s4=STRING_LITERAL_LONG2    ->  ^( STRING $s4 )
	;
		
iRIref	  
	:  	i=IRI_REF           ->  ^(IRI $i)
	|   prefixedName
	;
	
prefixedName	  
	:  	n1=PNAME_LN         ->  ^(PREFIXED_NAME $n1)
	|   n2=PNAME_NS         ->  ^(PREFIXED_NS $n2)
	;
	
blankNode	  
	:  	BLANK_NODE_LABEL | annon
    ;

annon
    :   OPEN_SQ_BRACKET CLOSE_SQ_BRACKET -> ^(ANNON OPEN_SQ_BRACKET)
	;

nil	:  	OPEN_BRACE  CLOSE_BRACE	-> NIL
	;



// $>

// $<Lexer

//added by wensun
ARROW 	:  '-'  '>'  ;
FUNCLANG	:  L  A  N  G  U  A  G  E  ;
FUNCCALL	:  F  U  N  C  C  A  L  L  ;
FUNCTION	:  F  U  N  C  T  I  O  N  ;
//IRI_REF		:  I  R  I  R  E  F  ;
//LTE		:  L  T  E  ;

BASE	:  B  A  S  E   ; 

PREFIX	:  P  R  E  F  I  X   ; 

SELECT	:  S  E  L  E  C  T   ; 

DISTINCT	:  D  I  S  T  I  N  C  T   ; 

REDUCED	:  R  E  D  U  C  E  D   ; 

AS	:  A  S   ; 

CONSTRUCT	:  C  O  N  S  T  R  U  C  T   ; 

WHERE_TOKEN	:  W  H  E  R  E   ; 

DESCRIBE	:  D  E  S  C  R  I  B  E   ; 

ASK	:  A  S  K   ; 

FROM	:  F  R  O  M   ; 

NAMED	:  N  A  M  E  D   ; 

GROUP	:  G  R  O  U  P   ; 

BY	:  B  Y   ; 

HAVING	:  H  A  V  I  N  G   ; 

ORDER	:  O  R  D  E  R   ; 

ASC	:  A  S  C   ; 

DESC	:  D  E  S  C   ; 

LIMIT	:  L  I  M  I  T   ; 

OFFSET	:  O  F  F  S  E  T   ; 

BINDINGS	:  B  I  N  D  I  N  G  S   ; 

UNDEF	:  U  N  D  E  F   ; 

LOAD	:  L  O  A  D   ; 

SILENT	:  S  I  L  E  N  T   ; 

INTO	:  I  N  T  O   ; 

CLEAR	:  C  L  E  A  R   ; 

DROP	:  D  R  O  P   ; 

CREATE	:  C  R  E  A  T  E   ; 

TO	:  T  O   ; 

MOVE	:  M  O  V  E   ; 

COPY	:  C  O  P  Y   ; 

INSERT	:  I  N  S  E  R  T   ; 

DATA	:  D  A  T  A   ; 

DELETE	:  D  E  L  E  T  E   ; 

WITH	:  W  I  T  H   ; 

USING	:  U  S  I  N  G   ; 

DEFAULT	:  D  E  F  A  U  L  T   ; 

GRAPH	:  G  R  A  P  H   ; 

ALL	:  A  L  L   ; 

OPTIONAL	:  O  P  T  I  O  N  A  L   ; 

SERVICE	:  S  E  R  V  I  C  E   ; 

BIND	:  B  I  N  D   ; 

MINUS	:  M  I  N  U  S   ; 

UNION	:  U  N  I  O  N   ; 

FILTER	:  F  I  L  T  E  R   ; 

STR	:  S  T  R   ; 

LANG	:  L  A  N  G   ; 

LANGMATCHES	:  L  A  N  G  M  A  T  C  H  E  S   ; 

DATATYPE	:  D  A  T  A  T  Y  P  E   ; 

BOUND	:  B  O  U  N  D   ; 

IRI	:  I  R  I   ; 

URI	:  U  R  I   ; 

BNODE	:  B  N  O  D  E   ; 

RAND	:  R  A  N  D   ; 

ABS	:  A  B  S   ; 

CEIL	:  C  E  I  L   ; 

FLOOR	:  F  L  O  O  R   ; 

ROUND	:  R  O  U  N  D   ; 

CONCAT	:  C  O  N  C  A  T   ; 

STRLEN	:  S  T  R  L  E  N   ; 

UCASE	:  U  C  A  S  E   ; 

LCASE	:  L  C  A  S  E   ; 

ENCODE_FOR_URI	:  E  N  C  O  D  E  '_'  F  O  R  '_'  U  R  I   ; 

CONTAINS	:  C  O  N  T  A  I  N  S   ; 

STRSTARTS	:  S  T  R  S  T  A  R  T  S   ; 

STRENDS	:  S  T  R  E  N  D  S   ; 

STRBEFORE	:  S  T  R  B E F O R E  ; 

STRAFTER	:  S  T  R  A F T E R   ; 

YEAR	:  Y  E  A  R   ; 

MONTH	:  M  O  N  T  H   ; 

DAY	:  D  A  Y   ; 

HOURS	:  H  O  U  R  S   ; 

MINUTES	:  M  I  N  U  T  E  S   ; 

SECONDS	:  S  E  C  O  N  D  S   ; 

TIMEZONE	:  T  I  M  E  Z  O  N  E   ; 

TZ	:  T  Z   ; 

NOW	:  N  O  W   ; 

MD5	:  M  D  '5'   ; 

SHA1	:  S  H  A  '1'   ; 

SHA224	:  S  H  A  '2'  '2'  '4'   ; 

SHA256	:  S  H  A  '2'  '5'  '6'   ; 

SHA384	:  S  H  A  '3'  '8'  '4'   ; 

SHA512	:  S  H  A  '5'  '1'  '2'   ; 

COALESCE	:  C  O  A  L  E  S  C  E   ; 

IF	:  I  F   ; 

STRLANG	:  S  T  R  L  A  N  G   ; 

STRDT	:  S  T  R  D  T   ; 

SAMETERM	:  S  A  M  E  T  E  R  M   ; 

ISIRI	:  I  S  I  R  I   ; 

ISURI	:  I  S  U  R  I   ; 

ISBLANK	:  I  S  B  L  A  N  K   ; 

ISLITERAL	:  I  S  L  I  T  E  R  A  L   ; 

ISNUMERIC	:  I  S  N  U  M  E  R  I  C   ; 

REGEX	:  R  E  G  E  X   ; 

TRUE	:  T  R  U  E   ; 

FALSE	:  F  A  L  S  E   ; 

ADD	:  A  D  D   ; 

IN	:  I  N   ; 

NOT	:  N  O  T   ; 

SUBSTR	:  S  U  B  S  T  R   ; 

EXISTS	:  E  X  I  S  T  S   ; 

COUNT	:  C  O  U  N  T   ; 

SUM	:  S  U  M   ; 

MIN	:  M  I  N   ; 

MAX	:  M  A  X   ; 

AVG	:  A  V  G   ; 

SAMPLE	:  S  A  M  P  L  E   ; 

GROUP_CONCAT	:  G  R  O  U  P  '_'  C  O  N  C  A  T   ; 

SEPARATOR	:  S  E  P  A  R  A  T  O  R   ; 

VALUES	:	V A L U E S ;

REPLACE : R E P L A C E;

UUID : U U I D ;

STRUUID : S T R U U I D ;

POST : P O S T;

TABLE : T A B L E ;

GET : G E T ;

SOP  : '\\' S O P ;

EOP  : '\\' E O P ;

OPEN_CURLY_BRACE
	:	'{'
	;

CLOSE_CURLY_BRACE
	:	'}'
	;

DOT
	:	'.'
	;

OPEN_BRACE
	:	'('
	;

CLOSE_BRACE
	:	')'
	;

COMMA
	:	','
	;

SEMICOLON
	:	';'
	;

LOGICAL_OR
	:	'||'
	;

LOGICAL_AND
	:	'&&'
	;

OPEN_SQ_BRACKET
	:	'['
	;

CLOSE_SQ_BRACKET
	:	']'
	;

LT
	: 	'<'
        (
          ( ( ('\\' UNICODE_ESCAPE) | ~( '<' | '>' | '"' | OPEN_CURLY_BRACE | CLOSE_CURLY_BRACE | '|' | '^' | '`' | '\\' | ('\u0000'..'\u0020') ) )*  '>' ) => ( ( ('\\' UNICODE_ESCAPE) | ~( '<' | '>' | '"' | OPEN_CURLY_BRACE | CLOSE_CURLY_BRACE | '|' | '^' | '`' | '\\' | ('\u0000'..'\u0020') ) )*  '>' )
            { $type = IRI_REF; }
        |   '='
            { $type = LTE; }
        |
    );
	
PNAME_NS	  
	:  	PN_PREFIX? ':'      
	;
	
PNAME_LN	  
	:  	PNAME_NS PN_LOCAL
	;
	
BLANK_NODE_LABEL	  
	:  	'_:' l=PN_LOCAL			{ setText($l.text); }
	;
	
VAR1	  
	:  	'?' VARNAME
	;
	
VAR2	  
	:  	'$' VARNAME
	;
	
	
VAR0	  
	:  	('a'..'z'|'A'..'Z')  ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*
	;
		
LANGTAG	  
	:  	'@' ('a'..'z'|'A'..'Z')+ ( '-' ('a'..'z'|'A'..'Z'|'0'..'9')+  )*		{ setText($text.substring(1, $text.length())); }
	;

fragment
DIGIT
	: 	('0'..'9')
	;
	
fragment
HEXDIGIT
    :   DIGIT | 'A'..'F' | 'a'..'f'
    ;

fragment
UNICODE_ESCAPE
    :   'u' HEXDIGIT HEXDIGIT HEXDIGIT HEXDIGIT
    ;

INTEGER	  
	:  	DIGIT+
	;
	
DECIMAL	  
	:  	DIGIT* DOT DIGIT*
	;
	
DOUBLE	  
	:  	DIGIT+ DOT DIGIT* EXPONENT 
	| 	DOT DIGIT+ EXPONENT 
	| 	DIGIT+ EXPONENT
	;
	
INTEGER_POSITIVE	  
	:  	'+' INTEGER
	;
	
DECIMAL_POSITIVE	  
	:  	'+' DECIMAL
	;
	
DOUBLE_POSITIVE	  
	:  	'+' DOUBLE
	;
	
INTEGER_NEGATIVE	  
	:  	'-' INTEGER
	;
	
DECIMAL_NEGATIVE	  
	:  	'-' DECIMAL
	;
	
DOUBLE_NEGATIVE	  
	:  	'-' DOUBLE
	;
	
fragment
EXPONENT	  
	:  	('e'|'E') ( ('+'|'-')? ('0'..'9') )+
	;
	
STRING_LINE_NONEOP	  
:  SOP	( options {greedy=false;} : ~('\u007C'|'\u005C') | ECHAR )*  EOP
	{ setText($text.substring(4, $text.length()-4)); 		}
;

STRING_LITERAL1	  
	:  	'\'' ( options {greedy=false;} : ~('\u0027'|'\u005C'|'\u000A'|'\u000D') | ECHAR )*  '\''	

			{ if (stripStringQuotes) setText($text.substring(1, $text.length()-1));
			  else setText("\""+$text.substring(1, $text.length()-1)+"\""); 		}
	;
	
STRING_LITERAL2	  
	:  	'"' ( options {greedy=false;} : ~('\u0022'|'\u005C'|'\u000A'|'\u000D') | ECHAR )*  '"'		
		
			{ if (stripStringQuotes) setText($text.substring(1, $text.length()-1)); }
	;
	
STRING_LITERAL_LONG1	  
	:  	'\'\'\'' ( options {greedy=false;} : ( '\'' | '\'\'' )? ( ~('\''|'\\') | ECHAR ) )*  '\'\'\'' 	
	
			{ if (stripStringQuotes) setText($text.substring(3, $text.length()-3)); 
			  else setText("\"\"\""+$text.substring(3, $text.length()-3)+"\"\"\"");	}
	;
	
STRING_LITERAL_LONG2	  
	:  	'"""' ( options {greedy=false;} : ( '"' | '""' )? ( ~('"'|'\\') | ECHAR ) )* '"""'		
	
			{ if (stripStringQuotes) setText($text.substring(3, $text.length()-3)); }
	;
	
ECHAR	  
	:  	'\\' ('t'|'b'|'n'|'r'|'f'|'\"'|'\\'|'\''|UNICODE_ESCAPE)
	;
	
WS	:  	( ' ' | '\t' | '\r' | '\n' )+      { $channel=HIDDEN; }
	;

COMMENT
	:	'#' ( options{greedy=false;} : . )*   EOL  		{ $channel=HIDDEN; }
	;
	
fragment 
EOL	:	('\n' | '\r')+		;

fragment	
PN_CHARS_BASE	  
	:  	('A'..'Z') | ('a'..'z')  | ('\\' UNICODE_ESCAPE)
	| 	('\u00C0'..'\u00D6') | ('\u00D8'..'\u00F6') | ('\u00F8'..'\u02FF')
	|   ('\u0370'..'\u037D') | ('\u037F'..'\u1FFF') | ('\u200C'..'\u200D') 
	|   ('\u2070'..'\u218F') | ('\u2C00'..'\u2FEF') | ('\u3001'..'\uD7FF') 
	|   ('\uF900'..'\uFDCF') | ('\uFDF0'..'\uFFFD') 
	;

fragment	
PN_CHARS_U	  
	:  	( PN_CHARS_BASE | '_' )
	;

fragment
VARNAME	  
	:  	( PN_CHARS_U | ('0'..'9') )
	    ( PN_CHARS_U | ('0'..'9') | '\u00B7' | ('\u0300'..'\u036F') | ('\u203F'..'\u2040') )* 
	;
	
fragment
PN_CHARS	  
	:  	PN_CHARS_U | '-' | ('0'..'9') | '\u00B7' | ('\u0300'..'\u036F') | ('\u203F'..'\u2040')
	;
	
fragment 
PN_PREFIX	  
	:  	PN_CHARS_BASE ((PN_CHARS|DOT)* PN_CHARS)?
	;

fragment	
PN_LOCAL	  
	:  	( PN_CHARS_U | ':' |  ('0'..'9') | PLX ) ( (PN_CHARS | DOT | ':' | PLX)* (PN_CHARS | ':' | PLX) )?
	;
	
fragment
PLX
    :   PERCENT | PN_LOCAL_ESC
    ;

fragment
PERCENT
    :   '%' HEXDIGIT HEXDIGIT
    ;

fragment
PN_LOCAL_ESC
    :   '\\' ('_' | '-' | '.' | '~' | '\'' | '!' | '$' | '&' | '(' | ')' | '*' | '+' | ',' | ';' | '=' | '/' | '?' | '#' | '@' | '%')
    ;

fragment A :  'A' | 'a'  ;	
fragment B :  'B' | 'b'  ;	
fragment C :  'C' | 'c'  ;	
fragment D :  'D' | 'd'  ;	
fragment E :  'E' | 'e'  ;	
fragment F :  'F' | 'f'  ;
fragment G :  'G' | 'g'  ;	
fragment H :  'H' | 'h'  ;	
fragment I :  'I' | 'i'  ;	
fragment J :  'J' | 'j'  ;	
fragment K :  'K' | 'k'  ;	
fragment L :  'L' | 'l'  ;	
fragment M :  'M' | 'm'  ;	
fragment N :  'N' | 'n'  ;	
fragment O :  'O' | 'o'  ;	
fragment P :  'P' | 'p'  ;	
fragment Q :  'Q' | 'q'  ;	
fragment R :  'R' | 'r'  ;	
fragment S :  'S' | 's'  ;	
fragment T :  'T' | 't'  ;	
fragment U :  'U' | 'u'  ;	
fragment V :  'V' | 'v'  ;	
fragment W :  'W' | 'w'  ;	
fragment X :  'X' | 'x'  ;	
fragment Y :  'Y' | 'y'  ;	
fragment Z :  'Z' | 'z'  ;	

// $>

/* end of file */	

