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
