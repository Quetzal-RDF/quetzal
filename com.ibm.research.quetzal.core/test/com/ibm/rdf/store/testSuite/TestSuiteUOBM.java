/******************************************************************************
 * Copyright (c) 2015 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *****************************************************************************/
 package com.ibm.rdf.store.testSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.ibm.rdf.store.sparql11.UOBMQueryUtilityTest;

@RunWith(Suite.class)
@Suite.SuiteClasses(
   { 
   UOBMQueryUtilityTest.PSQLUOBM30MPropPathsExpHelix1.class, 
   UOBMQueryUtilityTest.DB2UOBM30MPropPathsExp.class,
   
   //property path tests
   // LUBMQueryUtilityTest.PSQLLUBMPropPathHelix1.class
   //LUBMQueryUtilityTest.DB2LUBMPropPathHelix1.class, 
   
   UOBMQueryUtilityTest.PSQLUOBM30PropPathsHelix.class, 
   UOBMQueryUtilityTest.DB2UOBM30PropPaths.class,
   })
public class TestSuiteUOBM
   {
   // the class remains empty,
   // used only as a holder for the above annotations
   }
