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
   UOBMQueryUtilityTest.PSQLUOBM30MPropPathsExpHelix1.class, 
   UOBMQueryUtilityTest.DB2UOBM30MPropPathsExpVM9_47_202_45.class,
   
   //property path tests
   // LUBMQueryUtilityTest.PSQLLUBMPropPathHelix1.class
   //LUBMQueryUtilityTest.DB2LUBMPropPathHelix1.class, 
   
   UOBMQueryUtilityTest.PSQLUOBM30PropPathsHelix.class, 
   UOBMQueryUtilityTest.DB2UOBM30PropPathsVM9_47_202_45.class,
   })
public class TestSuiteUOBM
   {
   // the class remains empty,
   // used only as a holder for the above annotations
   }
