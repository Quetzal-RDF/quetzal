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
   LUBMQueryUtilityTest.PSQLLUBM10MHelix1.class,
   LUBMQueryUtilityTest.DB2LUBM10MHelix1.class
   })
public class TestSuiteLUBM
   {
   // the class remains empty,
   // used only as a holder for the above annotations
   }
