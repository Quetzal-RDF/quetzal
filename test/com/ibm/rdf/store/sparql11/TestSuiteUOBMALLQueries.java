package com.ibm.rdf.store.sparql11;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(
   { 
   UOBMQueryUtilityTest.PSQLUOBM30MALLPropPathsExpHelix1.class,  //currently failing
   UOBMQueryUtilityTest.DB2UOBM30MALLPropPathsExpVM9_47_202_45.class,

   //property path tests
    
   UOBMQueryUtilityTest.PSQLUOBM30MALLPropPathsHelix.class, // currently failing
   UOBMQueryUtilityTest.DB2UOBM30ALLPropPathsVM9_47_202_45.class,
   })
public class TestSuiteUOBMALLQueries<D>
   {
   // the class remains empty,
   // used only as a holder for the above annotations
   }