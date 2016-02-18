tree grammar IbmSparqlExtAstWalker;

options {
	language = Java;
	tokenVocab = IbmSparqlExt;
	ASTLabelType = XTree;
}						
				
@header { 
package com.ibm.research.rdf.store.sparql11;
	
import org.antlr.runtime.BitSet;
import java.util.*;
import java.math.BigInteger;
import java.math.BigDecimal;
import com.ibm.research.rdf.store.sparql11.model.*;
import com.ibm.wala.util.collections.HashMapFactory;
import com.ibm.wala.util.collections.HashSetFactory;
import com.ibm.wala.util.collections.Pair;
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

    private final Set<BlankNodeVariable> allBlankNodes = HashSetFactory.make();

    private void checkBlankNodes(Set<BlankNodeVariable> blankNodes) {
      // blank node scope is a single BGP
      if (! Collections.disjoint(blankNodes, allBlankNodes)) {
        throw new SPARQLsyntaxError("blank node scope");
      }

      allBlankNodes.addAll(blankNodes);
    }

    private int blankNodeCount = 0;

    private final Map<IRI, FunctionBase> functions = HashMapFactory.make();
}

@rulecatch {
	catch(RecognitionException re) {
		throw new SPARQLsyntaxError(re);
	}
}

	//modified by wensun
queryUnit returns [QueryExt q]
	:     ^(ROOT x=query)  	{ $q = x; /*System.out.println($q.toString());*/ }
	;
	
	//modified by wensun
query returns [QueryExt q]
	@init {
		
	}
	: ^(QUERY 
			( p=prologue )      
			(	( s=selectQuery  	{ $q = new QueryExt(p,s); } 
			    ( b=bindingsClause  
                  { 
                    PatternSet top = new PatternSet();
                    top.setTopLevel(true);
                    PatternSet oldMain = (PatternSet)$q.getMainPattern();
                    oldMain.setTopLevel(false);
                    top.addPattern(oldMain);
                    top.addPattern(b);
                    $q.setMainPattern(top);
                  }
                )?
			)
			|	( c=constructQuery 	{ $q = new QueryExt(p,c); } )
			|	( d=describeQuery 	{ $q = new QueryExt(p,d); } )
			|	( a=askQuery 		{ $q = new QueryExt(p,a); } )
			)
				// ???TBD: add this clause to Query
			
		)
	;
	
/*
updateUnit	  
	:  	update
	;
*/

prologue returns [QueryPrologue qp]
	@init { $qp = new QueryPrologue(); }
	:  	 
		^(PROLOGUE baseDecl[$qp]*  prefixDecl[$qp]*)
	;

				
baseDecl [QueryPrologue qp]	  
	:  	
	 	^(BASE i=iRIref)		{ $qp.setBase(i);	}
		//s=IRI_REF				{ $qp.setBase(new IRI($s.getToken().getText()));	}
	;

prefixDecl [QueryPrologue qp]	 
	:  	
		^(PREFIX n=prefixedName  v=iRIref)	  { $qp.getPrefixes().put(n.substring(0,n.length()-1), v);	}
		//^(KEY n=PNAME_NS) ^(VALUE v=IRI_REF)	{ $qp.getPrefixes().put($n.getText(), new IRI($v.getText()));	}
	;
	
	//modified by wensun		
selectQuery	returns [SelectQueryExt sq]
	@init { $sq = new SelectQueryExt(); }
	:  	
		(^(f=functionSet			{ $sq.setFunctions(f);    }  )
		)?
		^(SELECT  
			(s=selectClause  		{ $sq.setSelectClause(s);      }  )
			(d=dataset				{ $sq.setDatasetClauses(d);    }  )*  
			(w=whereClause			{ $sq.setGraphPattern(w);      }  )?
			(m=solutionModifier     { $sq.setSolutionModifier(m);  }  )
		)
	;
	
	//added by wensun
functionSet returns [List<FunctionBase> funcs]
	@init { funcs = new ArrayList<FunctionBase>(); }
	:	
		^(FUNCTION
			(f=functionDecl {funcs.add(f);} )+
		)
	;

	//added by wensun
functionDecl returns [FunctionBase func]
	@init { FunctionExt ext = null; ServiceFunction svc = null; }
	:	
		^(FUNCNAME
			(
                (fn=iRIref { $func = ext = new FunctionExt(); ext.setName(fn);  } )
            |
                ^(SERVICE s=expression fn=iRIref { $func = svc = new ServiceFunction(); svc.setService(s); } )
            |
                ^(TABLE s=expression fn=iRIref { $func = svc = new ServiceFunction(); svc.setService(s); svc.setTableFunction(true); } )
            )
            { functions.put(fn, $func); }
            ^(KIND ( ( POST { $func.setPost(); } ) |
                     ( GET { $func.setGet(); } ) )? )
			^(INV ( inv=var { $func.addInVar(inv); } )*)
			^(OUTV ( outv=var { $func.addOutVar(outv); } )*)
            (
                (
                    ^(FUNCLG (fl=VAR0 { ext.setLang($fl.getText()); } ) )
                    (fb=functionBody { ext.setBody(fb); } )
                )
            |
                (
                    ^(PARAMS
                        (
                            ^(PARAM
                                param=string
                                (
                                    value=expression
                                    { System.err.println("adding " + param + " and " + value + " to " + svc); svc.addServiceParam(param, value); } 
                                |
                                    pattern=groupGraphPattern[true]
                                    { svc.addServiceParam(param, pattern); }
                                |
                                    
                                    { svc.setTableParam(param); }
                                ) 
                            )
                        )*
                    )
                    rowdef=string { svc.setServiceRowXPath(rowdef); }
                    ( coldef=string { svc.addServiceColumnXPath(coldef); } )+
                )
            )
		)
	;

	//added by wensun
functionBody returns [FunctionBody fb]
	@init { $fb = new FunctionBody(); }
	:
	 	^(FUNCBODY
	 		(
	 		(f=STRING_LINE_NONEOP {$fb.setFlag(0); $fb.setBody(f); } )
	 		| 
	 		(p=groupGraphPattern[true] {$fb.setFlag(1); $fb.setBody(p); } )
	 		)
	 	)
	 ;
	
dataset returns [List<DatasetClause> dcl]
	@init { dcl = new ArrayList<DatasetClause>(); }
	:	
		^(DATASET 
			(dc=datasetClause {dcl.add(dc);} )+
		)
	;
	
subSelect returns [SubSelectPattern sp]
	@init { 
		$sp = new SubSelectPattern();
	}
	:  	^(SUB_SELECT 
			(s=selectClause  		{ $sp.setSelectClause(s);      }  )
			(w=whereClause			{ $sp.setGraphPattern(w);      }  )?
			(m=solutionModifier     { $sp.setSolutionModifier(m);  }  )
            (d=inlineData           { ((PatternSet)$sp.getPattern()).addPattern(d); })?
		)
	;

selectClause returns [SelectClause sc]
	@init { sc = new SelectClause(); }
	:  	
		(^(TYPE ( DISTINCT   { $sc.setSelectModifier(SelectClause.ESelectModifier.DISTINCT); }
				| REDUCED    { $sc.setSelectModifier(SelectClause.ESelectModifier.REDUCED);  }
				)
		   )
		)? 
		^(PVARS ( ( v=var    { $sc.addProjectedVariable(new ProjectedVariable(v)); } 
		          | expVar[$sc]						
		          | fexp[$sc]							   
		          )*
		        | '*' 
		        )
		) 
	;

expVar[SelectClause sc]
	:	^(AS v=var e=expression)   { $sc.addProjectedVariable(new ProjectedVariable(v, e)); }
	;

fexp[SelectClause sc]
	:	^(EXP e=expression)   { $sc.addProjectedVariable(new ProjectedVariable(e)); }
	;
	
constructQuery returns [ConstructQuery cq]
	@init { 
        PatternSet p = null;
		$cq = new ConstructQuery();
	}
	:  	^( CONSTRUCT  (	(	constructTemplate[$cq] 
							(d=dataset				{ $cq.setDatasetClauses(d);    }  )*  
							(w=whereClause			{ $cq.setPattern(w);           }  )
							(m=solutionModifier     { $cq.setSolutionModifier(m);  }  )
						) 
						| 
						(	(d=dataset				{ $cq.setDatasetClauses(d);    }  )*  
							(^(WHERE
                               {      
                                 p = new PatternSet();
                               }
                               triplesTemplate[$cq,p]
                               {
                                 
                                 $cq.setPattern(p);
                               }
                            ) )? 
							(m=solutionModifier     { $cq.setSolutionModifier(m);  }  )
						)
					  )
		 ) 
	;
	
describeQuery returns [DescribeQuery dq]
	@init { 
		$dq = new DescribeQuery();
	}
	:  	^( DESCRIBE	(	( (v=varOrIRIref2    	{$dq.getResources().add(v);} )+|'*') 
					 	( d=datasetClause 		{$dq.getDatasetClauses().add(d);} )*	
					 	( w=whereClause 		{$dq.setPattern(w);	}	)?
					 	( s=solutionModifier	{$dq.setSolutionModifier(s); } )
					)
		 )
	;
	
askQuery returns [AskQuery aq]
	@init {
		ArrayList<DatasetClause> dsl = new ArrayList<DatasetClause>();
	}
	:  	^(ASK 	(	(d=datasetClause	{ dsl.add(d); } )* 
					(w=whereClause)		{ $aq = new AskQuery(dsl, w); }
				)	
		 )		
	;
	
datasetClause returns [DatasetClause dc]
	:	^(FROM 	( d=defaultGraphClause	 	{ $dc = d; }
				| n=namedGraphClause		{ $dc = n; }
				)
		)
	;
	
defaultGraphClause returns [DatasetClause dc]
	:  	d=sourceSelector  { $dc = new DatasetClause(d); }
	;
	
namedGraphClause returns [DatasetClause dc]
	:  	^(NAMED d=sourceSelector)  { $dc = new DatasetClause(d, true); } 
	;
	
sourceSelector returns [IRI  r]
	:  	 i=iRIref	{ $r = i; }
	;

whereClause	returns [Pattern p]
	:  	
		^(WHERE g=groupGraphPattern[true]?)  {$p = g;}
	;
	
solutionModifier returns [SolutionModifiers m]
	@init {
		$m = new SolutionModifiers();
	}
	:  	^(MODIFIERS
			( g=groupClause 		{$m.setGroupClause(g); } )? 
			( h=havingClause 		{$m.setHavingClause(h);} )? 
			( o=orderClause 		{$m.setOrderClause(o); } )? 
			( l=limitOffsetClauses  {$m.setLimitOffset(l); } )? )
	;

groupClause	returns [GroupCondition gc]
	:  	^(GROUP_BY c=groupCondition)	{ $gc = c; }
	;
	
groupCondition returns [GroupCondition gc]
	@init {
		$gc = new GroupCondition();
	}
	:  	
	( b=builtInCall									 { $gc.addCondition(b);                               } 
	| f=functionCall 								 { $gc.addCondition(new FunctionCallExpression(f));   } 
	| ^(CONDITION e2=expression (v=var)?)			 { $gc.addCondition(new VariableExpression(e2, v));   }  
	| v2=var                                         { $gc.addCondition(new VariableExpression(v2));      }
	)+  
	;

havingClause returns [HavingCondition h] 
	@init {
		$h = new HavingCondition();
	} 
	:  	^(HAVING (c=havingCondition	{$h.addConstraint(c); } )+ )
	;
	
havingCondition	 returns [Expression e]
	:  	c=constraint  {$e = c;}
	;
	 
orderClause	returns [List<OrderCondition> loc]
	@init { 
		$loc = new ArrayList<OrderCondition>(); 
	}
	:  	^(ORDER_BY 	( oc=orderCondition { $loc.add(oc); } )+ )
	;
		
orderCondition returns [OrderCondition oc]
	: 	(^(ASC e1=brackettedExpression)  { $oc = new OrderCondition(e1, OrderCondition.EOrderType.ASC); })
	|	(^(DESC e2=brackettedExpression) { $oc = new OrderCondition(e2, OrderCondition.EOrderType.DESC);})
	| 	( c=constraint 					{ $oc = new OrderCondition(c); } 
		| v=var 						{ $oc = new OrderCondition(new VariableExpression(v)); }
		)
	;
	
limitOffsetClauses returns [LimitOffsetClauses loc]
	@init { 
		int lc = -1, oc = -1; 
	}
	:  	( lc1=limitClause {lc = lc1;}  (oc1=offsetClause {oc = oc1;})? 
		| oc2=offsetClause {oc = oc2;} (lc2=limitClause {lc = lc2;})?  
		)	
		{ $loc = new LimitOffsetClauses(lc, oc); } 
	;
	
limitClause	returns [int x]
	:  	
		^(LIMIT  i=INTEGER) 	{ $x = Integer.parseInt($i.text); }
	;
	
offsetClause returns [int x] 
	:  	
		^(OFFSET  i=INTEGER)	{ $x = Integer.parseInt($i.text); }
	;
	
bindingsClause returns [Pattern v] 
	:  	d=inlineData {$v = d; }
	;
	
bindingValue	  
	:  	( iRIref | rDFLiteral | numericLiteral | booleanLiteral | UNDEF )
	;
	
triplesTemplate[ConstructQuery cq, PatternSet p]
	:    sp=triplesSameSubject[$cq] { $p.addPattern(sp); } ( DOT triplesTemplate[$cq, $p]? )?
	;

groupGraphPattern[boolean l] returns [Pattern r]
	@init {
		PatternSet p = new PatternSet();  p.setTopLevel(l);
		$r = p;
	}
	:   ^(GROUP_GRAPH_PATTERN groupGraphPatternSub[p]?)  
	|	s=subSelect	{ $r = s; }
	;
	
groupGraphPatternSub[PatternSet p]
    @init {
      Set<BlankNodeVariable> blankNodes = HashSetFactory.make();  
    }
	:	(     sp=triplesBlock[$p]
              { 
                blankNodes.addAll(sp.gatherBlankNodes());
                $p.addPattern(sp);
              }
	    |     f=filter 					
              { 
                if (f != null) { $p.addFilter(f); }
              }
        |     graphPatternNewBGP[$p,blankNodes]
        {
          checkBlankNodes(blankNodes);
          blankNodes.clear();
        }

        )+
        {
          checkBlankNodes(blankNodes);
        }
    ;

triplesBlock[PatternSet p] returns [SimplePattern sp]
    :   ^( TRIPLES_BLOCK
           { $sp = new SimplePattern(); }
		   (
		       s=triples    { s.expandAndAddTo($sp); } 
			 | s2=triples2  { s2.expandAndAddTo($sp); } 
		   )+ 
		 ) 
    ;

triples returns [QueryTriple qt]
	@init { 
			QueryTripleTerm s = null;
			PropertyTerm p = null;
			QueryTripleTerm v = null; 
		  }
	:   ( ^(TRIPLE ^(SUBJECT	( s1=graphNode  		{ s = new QueryTripleTerm(s1);  }
							    )
				    )
				 ^(PREDICATE	( v1=predicate          { p = new PropertyTerm(v1);  } 
				 				)
				 )  
				 ^(VALUE  o=object)						{ v = new QueryTripleTerm(o);   }		 
				 
		   ) 	{ $qt = new QueryTriple(s, p, v); }
		)
	;

triples2 returns [QueryTriple2 qt]
	@init { 
				PropertyListElement ple = null;
		  }
	:   ^(TRIPLE2 ^(SUBJECT	( s=triplesNode  		{ $qt = new QueryTriple2(s);  }
							    )
				    )
				 (	^(PROPERTY_LIST 
				 			^(PREDICATE		( p=predicate          { ple = new PropertyListElement(p);  } 
				 						)
				 			)  
				 			( ^(VALUE  o=object)					{ ple.addObject(o); }	)+
				 			{ qt.addPropertyListElement(ple); }
				 	)
				 )*
		)
	;


graphPatternNewBGP[PatternSet p, Set<BlankNodeVariable> vars]
    :   u=groupMinusOrUnionGraphPattern 	
        { $p.addPattern(u); }
	| 	o=optionalGraphPattern 		
        { if ($p.isEmpty()) {
                $p.addPattern(o);
          } else { 
                $p.addOptional(o); 
          } 
        }
	| 	g=graphGraphPattern 		
        { 
            $p.addPattern(g);
        }
	| 	s=serviceGraphPattern
		{
			$p.addPattern(s);
		} 
	| 	b=bind
	    {					
			$p.addPattern(b);
	    } 
    |   v=inlineData
       {
       		$p.addPattern(v);
       }
    ;

inlineData returns [ValuesPattern v]
    :   ^(VALUES d=dataBlock {$v=new ValuesPattern(d);})
    ;

dataBlock returns [Values values]
@init {List<Variable> vars = new ArrayList<Variable>(); 
       List<List<Expression>> expressions = new LinkedList<List<Expression>>(); 
       values = new Values(vars, expressions);
       List<Expression> rowExp = new LinkedList<Expression>();
       int i = 0; 
       }
    :   ^(INLINE_DATA ( ( NIL )=>NIL | (v=var {vars.add(new Variable(v));})*) ( ( NIL )=>NIL | (d=dataBlockValue 
    	{ int row = i \% vars.size(); 
    	  if (row == 0) {
    	  	rowExp = new LinkedList<Expression>();
    	  	expressions.add(rowExp);
    	  }
    	  rowExp.add(d);
    	  i++;
		})* ))
    ;

dataBlockValue returns[Expression e] 
	:    i=iRIref 			{e = new ConstantExpression(i);} 
	|    r=rDFLiteral 		{e = new ConstantExpression(r);} 
	|    d=numericLiteral 	{e = new ConstantExpression(d);} 
	|    b= booleanLiteral 	{e = new ConstantExpression(b);} 
	|    u = UNDEF          {e = new UNDEFExpression();}
	;

optionalGraphPattern returns [Pattern p]
	:    ^(OPTIONAL g=groupGraphPattern[false])    				{ $p = g; }
	;

graphGraphPattern returns [Pattern p]
	:    ^(GRAPH r=varOrIRIref2 g=groupGraphPattern[false])		{ g.setGraphRestriction(r);  $p = g; }
	;

serviceGraphPattern  returns [Pattern p]
    @init { boolean silent=false; }
	:    ^(top=SERVICE (SILENT { silent=true; })? s=varOrIRIref g=groupGraphPattern[false])
         {
           $p = new ServicePattern(s, top.matched, silent, g);
         }
	;

//added & modified by wensun
bind	returns [Pattern p]
	:	bp=bind1 {$p=bp;}
	|	bf=bind2 {$p=bf;}
	;

bind1  returns [Pattern p]
	:    ^(BIND v=var  e= expression) { $p = new BindPattern(v, e); }
	    ;

bind2  returns [Pattern p]
	@init { $p = new BindFunctionPattern(); }
	:    ^(BIND 
			(f=funcCall { ((BindFunctionPattern)$p).setFuncCall(f); } )
			(v= var { ((BindFunctionPattern)$p).addVar(v); } )*
		)
	    ;

funcCall  returns [BindFunctionCall f]
	@init { $f = new BindFunctionCall(); }
	:    ^( FUNCCALL 
			(fn=iRIref {$f.setIri(fn); $f.setFunction(functions.get(fn)); } )
			(v=var {$f.addVar(v);} )*
		)
	;
	
groupMinusOrUnionGraphPattern  returns [Pattern r]
	@init {
	  PatternSet p = null;
	}
	:   ^(UNION 
                g1=groupGraphPattern[false] 	
                { p = new PatternSet(PatternSet.EPatternSetType.UNION); 
			      $r = p;
                  p.addPattern(g1);	} 
				(g2=groupGraphPattern[false] 
                 { 
                   p.addPattern(g2); 
                 })+ )
    |   ^(MINUS
           g1=groupGraphPattern[false] 
           { p = new PatternSet(PatternSet.EPatternSetType.MINUS); 
			 $r = p;
             p.addPattern(g1); } )
    |	g3=groupGraphPattern[false]   	
        { 
          $r = g3;
        }
	;

filter returns [Expression e]
	:   ^(FILTER c=constraint)			{ $e = c; }
	;

constraint returns [Expression e]
	:   be=brackettedExpression { $e = be; }
	| 	bc=builtInCall 			{ $e = bc; }
	| 	fc=functionCall			{ $e = new FunctionCallExpression(fc); }
	;

functionCall returns [FunctionCall fc]
	:   ^(FUNCTION i=iRIref a=argList)	{ $fc = new FunctionCall(i, a); }
	;

argList returns [List<Expression> args]
	@init { $args = new ArrayList<Expression>(); }
    :  	NIL						
	|   DISTINCT (e1=expression	{ $args.add(e1); }	)+
	|	(e2=expression			{ $args.add(e2); }	)+ 
	;

expressionList returns [List<Expression> exprs]
    @init { $exprs = new ArrayList<Expression>(); } 
	:   NIL            
	|   ( e=expression { $exprs.add(e); } )+
	;


constructTemplate[ConstructQuery cq]
	:   constructTriples[$cq]? 
	;

constructTriples[ConstructQuery cq]
	:   ( t=triples {$cq.addConstructPattern(t);} 
		| t2=triples2 {$cq.addConstructPattern(t2);} 
		)+ 
	;

triplesSameSubject[ConstructQuery cq] returns [SimplePattern sp]
    @init { $sp = new SimplePattern(); }
	:	(t1=triples  { $cq.addConstructPattern(t1); t1.expandAndAddTo($sp); })+
	|	t12=triples2 { $cq.addConstructPattern(t12); t12.expandAndAddTo($sp); } 
	;

object returns [GraphNode r]
	:   g=graphNode		{ $r = g; }
	;

verb returns [QueryTripleTerm t]
	:   v=varOrIRIref  	{ $t = v; }
	| 	'a'				{ $t = new QueryTripleTerm(new IRI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")); }
	;

verbPath
	:   path
	;

verbSimple returns [Variable v]
	:   x=var   { $v = new Variable(x);}
	;

path
	:   pathAlternative
	;

pathAlternative
	:   pathSequence ( '|' pathSequence )*
	;

pathSequence
	:    pathEltOrInverse ( '/' pathEltOrInverse )*
	;

pathElt
	:    pathPrimary pathMod?
	;

pathEltOrInverse
	:    pathElt | '^' pathElt
	;

pathMod returns [Path.PathMod v]
	: '*'  { $v = Path.PathMod.ZERO_OR_MORE; } 
	| '?'  { $v = Path.PathMod.ZERO_OR_ONE; } 
	| '+'  { $v = Path.PathMod.ONE_OR_MORE; } 
	;

pathPrimary
	:    iRIref | 'a' | '!' pathNegatedPropertySet | OPEN_BRACE path CLOSE_BRACE
	;

pathNegatedPropertySet returns [Pair<? extends List<IRI>, ? extends List<IRI>> pair] 
	@init { $pair = Pair.make(new LinkedList<IRI>(), new LinkedList<IRI>()); } 
	:    v = pathOneInPropertySet  { if (v.snd ) { $pair.fst.add(v.fst); } else {$pair.snd.add(v.fst);} }
	| OPEN_BRACE ( v1 = pathOneInPropertySet ( '|' v2 = pathOneInPropertySet  { if (v2.snd ) { $pair.fst.add(v2.fst); } else {$pair.snd.add(v2.fst);} } )* )? CLOSE_BRACE  
		{if (v1.snd ) { $pair.fst.add(0,v1.fst); } else {$pair.snd.add(0, v1.fst);} }
	;

pathOneInPropertySet returns [Pair<IRI, Boolean> v ]
  	:  i = iRIref { $v = Pair.make(i, true); }
	| 'a'  { $v = Pair.make(new IRI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"), true); }
	| ^(INV invi = iRIrefOrRDFType ) { $v = Pair.make(invi, false); }
	;

iRIrefOrRDFType returns [ IRI v ] 
   : i =  iRIref { $v = i;}
   | 'a' { $v = new IRI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"); }
   ;
   
integer
	:    INTEGER
	;

triplesNode returns [TriplesNode tn]
	:   ^( TRIPLES_NODE 
				(	c=collection 				{ $tn = new TriplesNode(c);}
				| 	b=blankNodePropertyList     { $tn = new TriplesNode(b);}
				)
		)
	;

blankNodePropertyList returns [PropertyList pl]
	@init {
			$pl = new PropertyList(); 
			PropertyListElement e = null;
		}
	:	^( PROPERTY_LIST  	
					(	{ e = new PropertyListElement(); }
						^(PREDICATE v=predicate)  {e.setVerb(v);}
					  	^(VALUE (o=graphNode {e.addObject(o);})+) 
					  	{ $pl.add(e); }
					)+ 
		 )   
	;

predicate returns [BinaryUnion<Variable, Path> p]
	@init { $p = new BinaryUnion<Variable, Path>(); }
	:	v=var       { $p.setFirst(new Variable(v));  }
	|	i=iRIref    { $p.setSecond(new SimplePath(i)); }
	| 	'a'			{ $p.setSecond(new SimplePath(new IRI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"))); }
    |   ^(ALT (alt=predicate { 
    		if (!$p.isFirstType() && !$p.isSecondType()) { 
    			assert alt.isSecondType(): alt; 
    			$p = alt;
    		} else { 
    			$p.setSecond(new AltPath($p.getSecond(), alt.getSecond())); 
    		} 
    	})*)
   	|   ^(SEQ (seq=predicate { 
    		if (!$p.isFirstType() && !$p.isSecondType()) { 
    			assert seq.isSecondType(): seq; 
    			$p = seq;
    		} else { 
    			$p.setSecond(new SeqPath($p.getSecond(), seq.getSecond())); 
    		} 
    	})*)
   	|   ^(ELT pred=predicate mod = pathMod) { $p.setSecond(Path.createPath(pred.getSecond(), mod)) ; } 
    |   ^(INV pred=predicate) { $p.setSecond(new InvPath(pred.getSecond())) ; }
    | '!' neg = pathNegatedPropertySet { $p.setSecond(new NegatedProperySetPath(neg.fst, neg.snd)); }
	;

collection returns [RDFCollection c]
	@init { $c = new RDFCollection(); }
	:    ^( COLLECTION (g=graphNode { $c.add(g); })+ ) 
	;

graphNode returns [GraphNode gn]
	:   v=var				{ $gn = new GraphNode(new Variable(v));			}
	| 	g=graphTerm 		{ $gn = new GraphNode(g);						}
	| 	t=triplesNode		{ $gn = new GraphNode(t);						}
	;

varOrTerm returns [QueryTripleTerm qt]
	:    v=var 				{ $qt = new QueryTripleTerm(new Variable(v)); 	}
	| 	 g=graphTerm		{ $qt = new QueryTripleTerm(g);					}
	;

varOrIRIref returns [QueryTripleTerm qt]
	:   v=var  				{ $qt = new QueryTripleTerm(new Variable(v)); 	}
	| 	i=iRIref 			{ $qt = new QueryTripleTerm(i); 	    }
	;

varOrIRIref2 returns [BinaryUnion<Variable, IRI> bu]
	@init    { $bu = new BinaryUnion<Variable, IRI>(); }
    : 	v=var 		{ $bu.setFirst(new Variable(v)); } 
    | 	i=iRIref   	{ $bu.setSecond(i); }
    ;
	
var returns [String v]
	:   ^(VAR 	( x1=VAR1    { $v = new String($x1.getText().substring(1)); }
				| x2=VAR2	 { $v = new String($x2.getText().substring(1)); }
				)
		)		
	;

graphTerm returns [GraphTerm gt]
	:   i=iRIref 				{ $gt = new GraphTerm(i); }
	| 	r=rDFLiteral			{ $gt = new GraphTerm(new Constant(r)); } 
	|	d=numericLiteral 		{ $gt = new GraphTerm(d); 				} 
	| 	b=booleanLiteral		{ $gt = new GraphTerm(new Constant(b)); }  
	| 	l=blankNode				{ $gt = new GraphTerm(l); }  
	| 	NIL						{ $gt = new GraphTerm(new IRI("http://www.w3.org/1999/02/22-rdf-syntax-ns#nil")); }
	;

expression returns [Expression e]
	@init { 
		LogicalExpression le = null;
		RelationalExpression rl = null;
		NumericExpression ne = null;
	}
	:   ^(LOGICAL_OR 			{ le = new LogicalExpression(LogicalExpression.ELogicalType.OR);  }
			e1=expression 		{ le.addComponent(e1);   }         
			(e2=expression		{ le.addComponent(e2); 	}	)+
		)						{ $e = le; }
	|   ^(LOGICAL_AND 			{ le = new LogicalExpression(LogicalExpression.ELogicalType.AND); }
			e3=expression 		{ le.addComponent(e3);   }         
			(e4=expression  	{ le.addComponent(e4); 	}	)+
		) 						{ $e = le; }
	|   ^('=' 					{ rl = new RelationalExpression(); 								  }
			e5=expression 		{ rl.setLeft(e5); 				}
			e6=expression		{ rl.setRight(RelationalExpression.ERelationalOp.EQUAL, e6); 		  }
		)						{ $e = rl; }
	|	^('!=' 					{ rl = new RelationalExpression(); 								  }
			e7=expression 		{ rl.setLeft(e7); 				}
			e8=expression		{ rl.setRight(RelationalExpression.ERelationalOp.NOT_EQUAL, e8);    }
		)						{ $e = rl; }
	|	^(LT  					{ rl = new RelationalExpression(); 								  }
			e9=expression       { rl.setLeft(e9); 				}
			e10=expression		{ rl.setRight(RelationalExpression.ERelationalOp.LESS, e10);		  }
		)						{ $e = rl; }
	|	^('>'  					{ rl = new RelationalExpression(); 								  }
			e11=expression  	{ rl.setLeft(e11); 				}
			e12=expression		{ rl.setRight(RelationalExpression.ERelationalOp.GREATER, e12);	  }
		)						{ $e = rl; }
	|	^(LTE  				{ rl = new RelationalExpression(); 								  }
			e13=expression  	{ rl.setLeft(e13); 				}
			e14=expression		{ rl.setRight(RelationalExpression.ERelationalOp.LESS_EQUAL, e14); }
		)						{ $e = rl; }
	|	^('>='  				{ rl = new RelationalExpression(); 								  }
			e15=expression  	{ rl.setLeft(e15); 				}
			e16=expression		{ rl.setRight(RelationalExpression.ERelationalOp.GREATER_EQUAL, e16); }
		)						{ $e = rl; }
	|	^(IN                    
			e17=expression      
			e18=expressionList  
                                { $e = new OneOfExpression(Expression.EOneOfOp.IN ,e17, e18); }
		)
	|	^(NOT
			e19=expression 
			e20=expressionList
		)                       { $e = new OneOfExpression(Expression.EOneOfOp.NOT_IN ,e19, e20); }
	|	^('+'                   { ne = new NumericExpression(); 								}
			e21=expression 		{ ne.setLHS(e21); 				}
			(e22=expression		{ ne.setRHS(NumericExpression.ENEType.PLUS, e22);      			}	)?
		)						{ $e = ne; }
	|	^(BROKEN_PLUS           { ne = new NumericExpression(); 								}
			e21=expression 		{ ne.setLHS(e21); 				}
			e22=expression		{ ne.setRHS(NumericExpression.ENEType.PLUS, e22);      			}
		)						{ $e = ne; }
	|	^('-'                   
			e23=expression 		{ $e = new UnaryExpression(Expression.EUnaryOp.MINUS, e23); }
			(e24=expression		{ $e = new NumericExpression(NumericExpression.ENEType.MINUS, e23, e24); })?
    )
	|	^(BROKEN_MINUS          { ne = new NumericExpression(); 								}
			e23=expression 		{ ne.setLHS(e23); 				}
			e24=expression		{ ne.setRHS(NumericExpression.ENEType.MINUS, e24);   			}
		)						{ $e = ne; }
	|	^('*'                   { ne = new NumericExpression(); 								}
			e25=expression 		{ ne.setLHS(e25); 				}
			e26=expression		{ ne.setRHS(NumericExpression.ENEType.MUL, e26);   				}
		)						{ $e = ne; }
	|	^('/'                   { ne = new NumericExpression(); 								}
			e27=expression		{ ne.setLHS(e27); 				}
			e28=expression		{ ne.setRHS(NumericExpression.ENEType.DIV, e28);   			}
		)						{ $e = ne; }
	|	^('!' e29=expression) 	{ $e = new UnaryExpression(Expression.EUnaryOp.NOT, e29); 		}
	|	e30=brackettedExpression { $e = e30; }
	| 	e31=primaryExpression 	{ $e = e31; }
	;


primaryExpression returns [Expression e]  
	:  	e1=builtInCall 		{ $e = e1; }
	| 	i=iRIref		 	{ $e = new ConstantExpression(i); }
	|	f=iRIFunction 		{ $e = new FunctionCallExpression(f); }
	| 	r=rDFLiteral 		{ $e = new ConstantExpression(r); }
	| 	n=numericLiteral 	{ $e = new ConstantExpression(n); }
	| 	b=booleanLiteral 	{ $e = new ConstantExpression(b); }
	| 	v=var 				{ $e = new VariableExpression(v); }
	| 	a=aggregate			{ $e = a; }
	;
  	
brackettedExpression returns [Expression e]
	:	^(EXPRESSION  e1=expression)  { $e = e1; }
    ;

builtInCall	returns [Expression e]
	@init {
		ArrayList args = new ArrayList();
	}
	: 	^(STR st=expression)
			{ args.add(st); $e = new BuiltinFunctionExpression(Expression.EBuiltinType.STR, args); 			}
	| 	^(LANG lg=expression)
			{ args.add(lg); $e = new BuiltinFunctionExpression(Expression.EBuiltinType.LANG, args); 		}
	| 	^(LANGMATCHES lm1=expression lm2=expression)
			{ $e = new BuiltinFunctionExpression(Expression.EBuiltinType.LANGMATCHES, lm1, lm2); 			}
	| 	^(DATATYPE dt=expression)
			{ args.add(dt); $e = new BuiltinFunctionExpression(Expression.EBuiltinType.DATATYPE, args); 	}
	|	^(BOUND v=var)    		
			{ $e = new BuiltinFunctionExpression(Expression.EBuiltinType.BOUND, new VariableExpression(v)); }
	|	^(IRI e6=expression)
			{ args.add(e6); $e = new BuiltinFunctionExpression(Expression.EBuiltinType.IRI, args);			}
	| 	^(URI e7=expression)
			{ args.add(e7); $e = new BuiltinFunctionExpression(Expression.EBuiltinType.IRI, args);			}
	|	^(BNODE e8=expression)
			{ args.add(e8); $e = new BuiltinFunctionExpression(Expression.EBuiltinType.BNODE, args);		}
	|	BNODE
			{ $e = new BuiltinFunctionExpression(Expression.EBuiltinType.BNODE); 							}
	|	^(RAND NIL )
			{ $e = new BuiltinFunctionExpression(Expression.EBuiltinType.RAND); 							}
	|	^(ABS e9=expression)
			{ args.add(e9); $e = new BuiltinFunctionExpression(Expression.EBuiltinType.ABS, args);			}
	|	^(CEIL e10=expression)
			{ args.add(e10); $e = new BuiltinFunctionExpression(Expression.EBuiltinType.CEIL, args);		}
	|	^(FLOOR e11=expression)
			{ args.add(e11); $e = new BuiltinFunctionExpression(Expression.EBuiltinType.FLOOR, args);		}
	|	^(ROUND e12=expression)
			{ args.add(e12); $e = new BuiltinFunctionExpression(Expression.EBuiltinType.ROUND, args);		}
	|	^(CONCAT e13=expressionList)
			{ args.addAll(e13); $e = new BuiltinFunctionExpression(Expression.EBuiltinType.CONCAT, args);		}
	| 	^(SUBSTR e14=expression e15=expression
			{ args.add(e14); args.add(e15); } 
            ( e16=expression { args.add(e16); } )? )
        {
          $e = new BuiltinFunctionExpression(Expression.EBuiltinType.SUB_STRING_EXP, args);
        }
	|	^(STRLEN e15=expression)
			{ args.add(e15); $e = new BuiltinFunctionExpression(Expression.EBuiltinType.STRLEN, args);		}
	|	^(UCASE e16=expression)
			{ args.add(e16); $e = new BuiltinFunctionExpression(Expression.EBuiltinType.UCASE, args);		}
	|	^(REPLACE e1=expression e2=expression e3=expression 
			{ args.add(e1); args.add(e2); args.add(e3); }
            ( e4=expression { args.add(e4); } )? )
			{ $e = new BuiltinFunctionExpression(Expression.EBuiltinType.REPLACE, args);		}
	|	^(LCASE e17=expression)
			{ args.add(e17); $e = new BuiltinFunctionExpression(Expression.EBuiltinType.LCASE, args);		}
	|	^(ENCODE_FOR_URI e18=expression)
			{ args.add(e18); $e = new BuiltinFunctionExpression(Expression.EBuiltinType.ENCODE_FOR_URI, args);		}
	|	^(CONTAINS e19=expression e20=expression)
			{ $e = new BuiltinFunctionExpression(Expression.EBuiltinType.CONTAINS, e19, e20);				 }
	|	^(STRSTARTS e21=expression e22=expression)
			{ $e = new BuiltinFunctionExpression(Expression.EBuiltinType.STRSTARTS, e21, e22);				 }
	|	^(STRENDS e23=expression e24=expression)
			{ $e = new BuiltinFunctionExpression(Expression.EBuiltinType.STRENDS, e23, e24);				 }
	|	^(STRBEFORE e241=expression e242=expression)
			{ $e = new BuiltinFunctionExpression(Expression.EBuiltinType.STRBEFORE, e241, e242);				 }
	|	^(STRAFTER e243=expression e244=expression)
			{ $e = new BuiltinFunctionExpression(Expression.EBuiltinType.STRAFTER, e243, e244);				 }
	|	^(YEAR e25=expression)
			{ args.add(e25); $e = new BuiltinFunctionExpression(Expression.EBuiltinType.YEAR, args);		}
	|	^(MONTH e26=expression)
			{ args.add(e26); $e = new BuiltinFunctionExpression(Expression.EBuiltinType.MONTH, args);		}
	|	^(DAY e27=expression)
			{ args.add(e27); $e = new BuiltinFunctionExpression(Expression.EBuiltinType.DAY, args);		}
	|	^(HOURS e28=expression)
			{ args.add(e28); $e = new BuiltinFunctionExpression(Expression.EBuiltinType.HOURS, args);		}
	|	^(MINUTES e29=expression)
			{ args.add(e29); $e = new BuiltinFunctionExpression(Expression.EBuiltinType.MINUTES, args);		}
	|	^(SECONDS e30=expression)
			{ args.add(e30); $e = new BuiltinFunctionExpression(Expression.EBuiltinType.SECONDS, args);		}
	|	^(TIMEZONE e31=expression)
			{ args.add(e31); $e = new BuiltinFunctionExpression(Expression.EBuiltinType.TIMEZONE, args);		}
	|	^(TZ e32=expression)
			{ args.add(e32); $e = new BuiltinFunctionExpression(Expression.EBuiltinType.TZ, args);		}
	|	NOW { $e = new BuiltinFunctionExpression(Expression.EBuiltinType.NOW); }
    |   UUID { $e = new BuiltinFunctionExpression(Expression.EBuiltinType.UUID); }
    |   STRUUID  { $e = new BuiltinFunctionExpression(Expression.EBuiltinType.STRUUID); }
	|	^(MD5 e33=expression)
			{ $e = new BuiltinFunctionExpression(Expression.EBuiltinType.MD5, e33); 			}
	|	^(SHA1 e34=expression)
			{ $e = new BuiltinFunctionExpression(Expression.EBuiltinType.SHA1, e34); 			}
	|	^(SHA256 e36=expression)
			{ $e = new BuiltinFunctionExpression(Expression.EBuiltinType.SHA256, e36); 			}
	|	^(SHA384 e37=expression)
			{ $e = new BuiltinFunctionExpression(Expression.EBuiltinType.SHA384, e37); 			}
	|	^(SHA512 e38=expression)
			{ $e = new BuiltinFunctionExpression(Expression.EBuiltinType.SHA512, e38); 			}
	|	^(COALESCE e39=expressionList)
			{ $e = new BuiltinFunctionExpression(Expression.EBuiltinType.COALESCE, e39); 			}
	|	^(IF e40=expression e41=expression e42=expression)
			{ $e = new BuiltinFunctionExpression(Expression.EBuiltinType.IF, e40, e41, e42); 			}
	|	^(STRLANG e45=expression e46=expression)
			{ $e = new BuiltinFunctionExpression(Expression.EBuiltinType.STRLANG, e45, e46); 			}
	|	^(STRDT e47=expression e48=expression)
			{ $e = new BuiltinFunctionExpression(Expression.EBuiltinType.STRDT, e47, e48); 			}
	|	^(SAMETERM sam1=expression sam2=expression)
			{ $e = new BuiltinFunctionExpression(Expression.EBuiltinType.SAMETERM, sam1, sam2); 			}
	|	^(ISIRI isi=expression)
			{ args.add(isi); $e = new BuiltinFunctionExpression(Expression.EBuiltinType.ISIRI, args); 		}
	|	^(ISURI isu=expression)
			{ args.add(isu); $e = new BuiltinFunctionExpression(Expression.EBuiltinType.ISIRI, args); 		}
	|	^(ISBLANK isb=expression)
			{ args.add(isb); $e = new BuiltinFunctionExpression(Expression.EBuiltinType.ISBLANK, args); 	}
	|	^(ISLITERAL isl=expression)
			{ args.add(isl); $e = new BuiltinFunctionExpression(Expression.EBuiltinType.ISLITERAL, args); 	}
	|	^(ISNUMERIC e55=expression)
			{ args.add(e55); $e = new BuiltinFunctionExpression(Expression.EBuiltinType.ISNUMERIC, args); 	}
	| r=regexExpression { $e = r; }
	| p=existsFunc { $e = new BuiltinFunctionExpression(Expression.EBuiltinType.EXISTS, p); 	}
	| p=notExistsFunc { $e = new BuiltinFunctionExpression(Expression.EBuiltinType.NOT_EXISTS, p); 	}
	;
  	
regexExpression	returns [Expression e]
	:   ^(REGEX e1=expression e2=expression e3=expression?)
    	{ $e = new BuiltinFunctionExpression(Expression.EBuiltinType.REGEX, e1, e2, e3); }
	 ;
	
existsFunc returns [Pattern p]	  
	:  	^(EXISTS g=groupGraphPattern[false])
	   	{ $p = g; }
	;
	
notExistsFunc returns [Pattern p]	  
	:  	^(NOT_EXISTS g=groupGraphPattern[false])
	   	{ $p = g; }
	;

aggregate returns [AggregateExpression a]
	@init { 
		$a = new AggregateExpression(); 
	}
	: 	
		^(COUNT 				{ $a.setAggregationType(AggregateExpression.EType.COUNT);  	}
			(DISTINCT 			{ $a.isDistinct(true);	}	)?  	                                     
			( e1=expression 	{ $a.setArgs(e1);		}
			| '*' 			
			)
		)
	| 	^(SUM					{ $a.setAggregationType(AggregateExpression.EType.SUM);  	}
			(DISTINCT 			{ $a.isDistinct(true);	}	)?  
			e2=expression		{ $a.setArgs(e2);		}
		)
	|	^(MIN 					{ $a.setAggregationType(AggregateExpression.EType.MIN);  	}
			(DISTINCT 			{ $a.isDistinct(true);	}	)? 
			e3=expression		{ $a.setArgs(e3);		}
		)
	|	^(MAX 					{ $a.setAggregationType(AggregateExpression.EType.MAX);  	}
			(DISTINCT 			{ $a.isDistinct(true);	}	)?  
			e4=expression		{ $a.setArgs(e4);		}
		)
	|	^(AVG 					{ $a.setAggregationType(AggregateExpression.EType.AVG);  	}
			(DISTINCT 			{ $a.isDistinct(true);	}	)?  
			e5=expression		{ $a.setArgs(e5);		}
		)
	|	^(SAMPLE 				{ $a.setAggregationType(AggregateExpression.EType.SAMPLE);  }
			(DISTINCT 			{ $a.isDistinct(true);	}	)?  
			e6=expression		{ $a.setArgs(e6);		}
		)
	| 	^(GROUP_CONCAT 			{ $a.setAggregationType(AggregateExpression.EType.GROUP_CONCAT); } 
			(DISTINCT 			{ $a.isDistinct(true);	}	)?
			e7=expression 		{ $a.setArgs(e7);		}
			(^(SEPARATOR s=string	{ $a.setSeparator(s);		})	)?
		)
	;
	
iRIFunction returns [FunctionCall f]
	:  	^(FUNCTION  
				i=iRIref 	{ $f = new FunctionCall(i); }
				(a=argList 	{ $f.addArguments(a); 		} )?
		) 
	;
	
rDFLiteral returns [StringLiteral sl] 
	:  	s=string 					{ $sl = new StringLiteral(s);     }
		( l=LANGTAG 				{ $sl.setLanguage($l.text);        }
		| ( '^^' i=iRIref ) 		{ $sl.setType(i);       }
		)?
	;
	
numericLiteral returns [Constant n]
	:  	n1=numericLiteralUnsigned	{ $n = n1; }
	| 	n2=numericLiteralPositive	{ $n = n2; }
	| 	n3=numericLiteralNegative	{ $n = n3; }
	;
	
numericLiteralUnsigned returns [Constant c]
	:  	^( BIG_INTEGER  i=INTEGER )					{ $c = new Constant($i.text, new BigInteger($i.text));		}
	| 	^( BIG_DECIMAL	d1=DECIMAL )				{ $c = new Constant($d1.text, new BigDecimal($d1.text));	}
	| 	^( DOUBLE		d2=DOUBLE )					{ $c = new Constant($d2.text, new Double($d2.text));		}
	;
  	
numericLiteralPositive returns [Constant c]
	:  	^( BIG_INTEGER  i=INTEGER_POSITIVE )		{ $c = new Constant($i.text, new BigInteger($i.text.substring(1)));		}
	| 	^( BIG_DECIMAL	d1=DECIMAL_POSITIVE )		{ $c = new Constant($d1.text, new BigDecimal($d1.text.substring(1)));	}
	| 	^( DOUBLE		d2=DOUBLE_POSITIVE )		{ $c = new Constant($d2.text, new Double($d2.text.substring(1)));		}
	;
	
numericLiteralNegative returns [Constant c]
	:  	^( BIG_INTEGER  i=INTEGER_NEGATIVE )		{ $c = new Constant($i.text, new BigInteger($i.text));		}
	| 	^( BIG_DECIMAL	d1=DECIMAL_NEGATIVE )		{ $c = new Constant($d1.text, new BigDecimal($d1.text));	}
	| 	^( DOUBLE		d2=DOUBLE_NEGATIVE )		{ $c = new Constant($d2.text, new Double($d2.text));		}
	;
	
booleanLiteral returns [Boolean b]
	:  	^(BOOLEAN TRUE)     { $b = Boolean.TRUE;  }
	|   ^(BOOLEAN FALSE)    { $b = Boolean.FALSE; }
	;
	
string returns [String s]
	:	^(STRING s1=STRING_LITERAL1)         { $s = new String($s1.text); }
	|	^(STRING s2=STRING_LITERAL2)         { $s = new String($s2.text); }
	|	^(STRING s3=STRING_LITERAL_LONG1)    { $s = new String($s3.text); }
	|	^(STRING s4=STRING_LITERAL_LONG2)    { $s = new String($s4.text); }
	;
	
iRIref  returns [IRI r]
	:	^(IRI i=IRI_REF)	{ $r = new IRI($i.text.substring(1, $i.text.length()-1)); 	}
	|   p=prefixedName		{ $r = new IRI(p); 				}
	;
	
prefixedName returns [String s]  
	:	^(PREFIXED_NAME n1=PNAME_LN)	{ $s = new String($n1.getText()); }
	|	^(PREFIXED_NS n2=PNAME_NS)		{ $s = new String($n2.getText()); }
	;
	
blankNode returns [BlankNode bn]
	:  	b=BLANK_NODE_LABEL  { $bn = new BlankNode($b.getText()); }
	|   ^(ANNON t=OPEN_SQ_BRACKET)				{ $bn = new BlankNode("b" + t.getTokenStartIndex()); }
	;
	
