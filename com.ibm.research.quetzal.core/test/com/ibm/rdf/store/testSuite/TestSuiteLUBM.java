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

import com.ibm.rdf.store.sparql11.LUBMQueryUtilityTest;

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
