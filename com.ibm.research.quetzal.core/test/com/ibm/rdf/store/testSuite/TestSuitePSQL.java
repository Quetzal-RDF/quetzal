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
   LUBMQueryUtilityTest.PSQLLUBM10MHelix1.class,
   SP2QueryUtilityTest.PSQLSP2B1MHelix1.class,
   UOBMQueryUtilityTest.PSQLUOBM30MPropPathsExpHelix1.class,  //currently failing
   })
public class TestSuitePSQL
   {
   // the class remains empty,
   // used only as a holder for the above annotations
   }
