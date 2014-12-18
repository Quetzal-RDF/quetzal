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
 package com.ibm.rdf.store.sparql11;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(
   { 
   UOBMQueryUtilityTest.PSQLUOBM30MALLPropPathsExpHelix1.class,  //currently failing
   UOBMQueryUtilityTest.DB2UOBM30MALLPropPathsExp.class,

   //property path tests   
   UOBMQueryUtilityTest.PSQLUOBM30MALLPropPathsHelix.class, // currently failing
   UOBMQueryUtilityTest.DB2UOBM30ALLPropPaths.class,
  
   })
public class TestSuiteUOBMALLQueries<D>
   {
   // the class remains empty,
   // used only as a holder for the above annotations
   }