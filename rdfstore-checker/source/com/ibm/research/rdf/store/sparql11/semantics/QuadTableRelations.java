package com.ibm.research.rdf.store.sparql11.semantics;

import kodkod.ast.Expression;
import kodkod.ast.Relation;

public interface QuadTableRelations {

	public static final Relation quads = Relation.nary("quads", 4);

	public static final Relation desiredQuads = Relation.nary("desired_quads", 4);

	public static final Relation literalDatatypes = Relation.binary("literal_datatypes");
	
	public static final Relation literalLanguages = Relation.binary("literal_languages");
	
	public static final Relation languageCaseMatch = Relation.binary("language_case_match");

	public static final Relation literalValues = Relation.binary("literal_values");

	public static final Relation graphs = Relation.unary("all_graphs");

	public static final Relation subjects = Relation.unary("all_subjects");

	public static final Relation predicates = Relation.unary("all_predicates");

	public static final Relation objects = Relation.unary("all_objects");

	public static final Relation nodes = Relation.unary("all_nodes");

	public static final Relation booleanTypes = Relation.unary("boolean_types");

	public static final Relation numericTypes = Relation.unary("numeric_types");

	public static final Relation floatTypes = Relation.unary("float_types");

	public static final Relation stringTypes = Relation.unary("string_types");

	public static final Relation dateTypes = Relation.unary("date_types");

	public static final Relation blankNodes = Relation.unary("blank_nodes");

	public static final Relation blankNodeRenaming = Relation.binary("blank_node_renaming");
	
	public static final Relation NULL = Relation.unary("NULL");

	public static final Relation choice = Relation.unary("choice");
	
	public static final Relation stringOrder = Relation.binary("string_order");
	
	public static final Expression knownTypes = dateTypes.union(stringTypes).union(numericTypes).union(booleanTypes);
	
	public static final String NULL_atom = "NULL atom";

	public static final String defaultGraph = "Default Graph atom";

	public static final Relation emptyString = Relation.unary("empty");

	public static final Relation trueBoolean = Relation.unary("true boolean");

}
