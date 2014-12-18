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
 package com.ibm.rdf.store.testing;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;

public class RandomizedRepeatRunner extends BlockJUnit4ClassRunner {

	public RandomizedRepeatRunner(Class<?> klass) throws InitializationError {
		super(klass);
	}

	private static int getNumTimes(TestClass klass) {
		Annotation[] anns = klass.getAnnotations();
		for (int i = 0; i < anns.length; i++) {
			if (anns[i] instanceof RandomizedRepeat) {
				return ((RandomizedRepeat) anns[i]).value();
			}
		}
		return 1;
	}
	
	@Override
	protected List<FrameworkMethod> computeTestMethods() {
		List<FrameworkMethod> originalMethods = super.computeTestMethods();
		int numTimes = getNumTimes(getTestClass());

		List<FrameworkMethod> methods = new ArrayList<FrameworkMethod>(originalMethods.size()*numTimes);
		
		for(FrameworkMethod m : originalMethods) {
			for(int i = 0; i < numTimes; i++) {
				final int ii = i;
				methods.add(new FrameworkMethod(m.getMethod()) {

					@Override
					public String getName() {
						return super.getName() + " (" + ii + ")";
					}
					
				});
			}
		}
		Collections.shuffle(methods);
		return methods;
	}

  
}
