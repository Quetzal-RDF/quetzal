package com.ibm.rdf.store.testSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.ibm.rdf.store.sparql11.JazzQueryUtilityTest;
import com.ibm.rdf.store.sparql11.LUBMQueryUtilityTest;
import com.ibm.rdf.store.sparql11.SP2QueryUtilityTest;
import com.ibm.rdf.store.sparql11.UOBMQueryUtilityTest;

@RunWith(Suite.class)
@Suite.SuiteClasses(
   { 
   JazzQueryUtilityTest.PSQLJazzHelix1.class, 
   JazzQueryUtilityTest.DB2JazzHelix1.class,
   LUBMQueryUtilityTest.PSQLLUBM10MHelix1.class,
   LUBMQueryUtilityTest.DB2LUBM10MHelix2.class,
//   LUBMQueryUtilityTest.PSQLLUBM10MHelix1.class,
//   LUBMQueryUtilityTest.DB2LUBMHelix1.class,
   SP2QueryUtilityTest.PSQLSP2B1MHelix1.class,
   SP2QueryUtilityTest.DB2SP2B1MHelix1.class,
   UOBMQueryUtilityTest.PSQLUOBM30MPropPathsExpHelix1.class,  //currently failing
   UOBMQueryUtilityTest.DB2UOBM30MPropPathsExpVM9_47_202_45.class,
   
   //property path tests
   // LUBMQueryUtilityTest.PSQLLUBMPropPathHelix1.class
   //LUBMQueryUtilityTest.DB2LUBMPropPathHelix1.class, 
   
   UOBMQueryUtilityTest.PSQLUOBM30PropPathsHelix.class, // currently failing
   UOBMQueryUtilityTest.DB2UOBM30PropPathsVM9_47_202_45.class,
 
   //
   })
public class TestSuiteAll
   {
   // the class remains empty,
   // used only as a holder for the above annotations
   }
