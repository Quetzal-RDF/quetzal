tree grammar IbmSparqlAstRewriter;

options {
	language=Java;
	tokenVocab=IbmSparql;
	ASTLabelType=CommonTree;
	output=AST;
	rewrite=true;
	filter=true;
}					
		
tokens {
	N_GROUP_GRAPH_PATTERN;
	N_UNION;
}
		
@header { 
package com.ibm.research.rdf.store.sparql11;
	
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.AbstractMap;
import java.util.HashMap;
import com.ibm.research.rdf.store.sparql11.model.*;

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
	
bottomup
	:	groupGraphPatternNull
	|	unionNull
	|	whereNull
	;
	
whereNull
	:	^(WHERE GROUP_GRAPH_PATTERN) 			->	 WHERE	
	;

groupGraphPatternNull
	@init{
		int t = 0, c = 0, g = 0;
	}
	:	^(GROUP_GRAPH_PATTERN 	( 	(GROUP_GRAPH_PATTERN)=> GROUP_GRAPH_PATTERN 
								|	(^(TRIPLES_BLOCK .+))=> (x+=. {t++;})
								|	(^(N_GROUP_GRAPH_PATTERN .+))=> (x+=. {g++;})
								|	( x+=.	{c++;} 	)
								)+	
		)
		
		->	{(c == 0)&&(t == 0)&&(g == 0)}?		GROUP_GRAPH_PATTERN
		->	{(c == 0)&&(t == 0)&&(g > 0)}?		$x+
		->										^( N_GROUP_GRAPH_PATTERN $x* )
	;

unionNull
	@init {
		int c = 0;
	}
	:	^(UNION (  	(GROUP_GRAPH_PATTERN)=> GROUP_GRAPH_PATTERN 
				|	( ( x+=. )		{c++;} )
				)+ 
		)		
		
		->	{c == 0}? 	GROUP_GRAPH_PATTERN
		->	{c == 1}? 	$x+ 
		->				^( N_UNION $x+ )
	;

/* end of file */
