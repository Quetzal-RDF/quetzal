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
   JazzQueryUtilityTest.DB2JazzHelix1.class,
   LUBMQueryUtilityTest.DB2LUBMHelix1.class,
   SP2QueryUtilityTest.DB2SP2B1MHelix1.class,
   UOBMQueryUtilityTest.DB2UOBM30MPropPathsExp.class,
   })
public class TestSuiteDB2
   {
   // the class remains empty,
   // used only as a holder for the above annotations
   }
