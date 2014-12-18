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
 package com.ibm.research.rdf.store.sparql11.sqltemplate;

import java.util.LinkedList;
import java.util.List;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.language.AngleBracketTemplateLexer;

public class StringTemplateTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
        StringTemplate query =
                new StringTemplate("SELECT $column$ FROM $table$;");
        //StringTemplate query2 = new StringTemplate("QS<sql_id> AS ( SELECT <project; separator=\",\"> FROM (VALUES <values:{<it:{(<it; separator=\",\">})}>)");
		String str = "QS<sql_id> AS ( SELECT <project; separator=\",\"> FROM (VALUES <values:{(<it; separator=\",\">)}>))";

        StringTemplate query2 = new StringTemplate(str, AngleBracketTemplateLexer.class);
        query.setAttribute("column", "subject");
        query.setAttribute("table", "emails");
        System.out.println("QUERY: "+query.toString());
        List<List<String>> l = new LinkedList<List<String>>();
        List<String> l1 = new LinkedList<String>();
        List<String> l2 = new LinkedList<String>();
        l1.add("foo");
        l1.add("bar");
        l2.add("foo1");
        l2.add("bar2");
        l.add(l1);
        l.add(l2);
        
        List<String> numLists = new LinkedList<String>();
        numLists.add("x");
        numLists.add("y");
        query2.setAttribute("sql_id", "1");
        query2.setAttribute("values", l);
        query2.setAttribute("project", numLists);
        System.out.println("QUERY: "+query2.toString());
        
        
	}

}
