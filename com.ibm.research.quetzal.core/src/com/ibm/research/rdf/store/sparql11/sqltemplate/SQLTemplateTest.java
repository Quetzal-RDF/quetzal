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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashSet;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.antlr.stringtemplate.language.AngleBracketTemplateLexer;

public class SQLTemplateTest {

	public static class Decl {
		String name;
		String type;
		public Decl(String name, String type) {this.name=name; this.type=type;}
		public String getName() {return name;}
		public String getType() {return type;}
	}

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {

		// Use the constructor that accepts a Reader
		StringTemplateGroup sql = new StringTemplateGroup(new FileReader("src/com/ibm/rdf/store/sparql11/sqltemplate/SQLTemplates"));
		StringTemplate t = sql.getInstanceOf("simple_ph_exp1");
		t.setAttribute("project", "entry as x");
		t.setAttribute("project", "val5 as y");
		t.setAttribute("target", "DPH");
		t.setAttribute("target", "Q1");
		t.setAttribute("predicate_constraint","pred1 = 'p1'");
		t.setAttribute("predicate_constraint","pred2 = 'p2'");
		t.setAttribute("predicate_constraint","pred3 = 'p3'");
		t.setAttribute("val_constraint","val1 = 'v1'");
		t.setAttribute("val_constraint","val2 = 'v2'");
		t.setAttribute("val_constraint","val3 = 'v3'");
		t.setAttribute("sep", " OR ");
		System.out.println(t);

		HashSet<String> values = new HashSet<String>();
		values.add("p1");
		values.add("p2");
		
		StringTemplate st = new StringTemplate("$items:{SELECT $it.(\"last\")$ FROM $it.(\"first\")$ \n}; separator=\" UNION \" $ ");
		st.setAttribute("items.{first,last}", "John", "Smith");
		st.setAttribute("items.{first,last}", "Baron", "Von Munchhausen");
		String expecting =
			"Smith, John\n" +
			"Von Munchhausen, Baron\n";
		System.out.println("Template \n"+st);
		System.out.println("Expected \n"+expecting);



		StringTemplateGroup group =
			new StringTemplateGroup(new FileReader("src/com/ibm/rdf/store/sparql11/sqltemplate/SQLTemplates"),
					AngleBracketTemplateLexer.class);
		StringTemplate f = group.getInstanceOf("file");
		f.setAttribute("variables.{decl,format}", new Decl("i","int"), "intdecl");
		f.setAttribute("variables.{decl,format}", new Decl("a","int-array"), "intarray");
		System.out.println("f="+f);

		
		StringTemplate f2 = group.getInstanceOf("file2");
		f2.setAttribute("variables.{decl,format}", values, "intdecl2");
		f2.setAttribute("variables.{decl,format}", values, "intarray2");
		System.out.println("f="+f2);
		
	}

}
