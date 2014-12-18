BEGIN {
    delete predCounts;
    delete preds;
    pawkExt = (pawk == "yes")? "." part : "";
    currentEntity ="start token";

    if (typeTable != "") {
	type_cmd = "sort | uniq > " typeTable pawkExt;
	locale_cmd = "sort | uniq > " localeTable pawkExt;
    }

    delete written_multi_preds;
    if (multiValuePredicatesFile != "") {
	multi_cmd = "sort > " multiValuePredicatesFile pawkExt;
    }

    if (predicateTypeFile != "") {
	delete predicate_type_table;
	preds_output_file = predicateTypeFile pawkExt;
    }
}

function update_table(str) {
    for(g in graphs) {
	str = "";
	for (i in preds) {
	    if (graph_preds[i, g] == 1) {
		str = str "|" i ;
	    }
	}
	if (str in predCounts) {
	    predCounts[str]++;
	} else {
	    if (str != "") {
		predCounts[str] = 1;
	    }
	}
    }
}

function collectPredicateType(pred, db2Type) {
    if (pred in predicate_type_table) {
	if (predicate_type_table[pred] != db2Type) {
	    predicate_type_table[pred] = "mixed";
	}
    } else {
	predicate_type_table[pred] = db2Type;
    }
}

{
    if (pawk != "yes") {
	if (parse_for_elements($0, elts) == 0) {
	    print "bad triple: " $0 > "/dev/stderr";
	}
    }

    if (setsFor == "subject") {
	thisEntity = elts["subject"];
    } else {
	thisEntity = elts["object"];
    }

    if (fileType=="quad") {
	graph = elts["graph"];
	if (trimString(graph) == "") {
	    graph = "DEF";
	}
    } else {
	graph = "DEF";
    }

    if (thisEntity != currentEntity) {
	if (currentEntity != "start token") {
	    update_table();
	}
	delete preds;
	delete graphs;
	delete graph_preds;
	currentEntity = thisEntity;
    }

    # already saw this predicate for this entity, so it is multi valued
    if (graph_preds[elts["predicate"], graph] == 1 && multi_cmd != "") {
	if (! (elts["predicate"] in written_multi_preds)) {
	    written_multi_preds[elts["predicate"]] = 1;
	    print elts["predicate"] | multi_cmd;
	}
    }

    preds[elts["predicate"]] = 1;
    graphs[graph]=1;	
    graph_preds[elts["predicate"], graph] = 1; 

    if (typeTable != "") {
	if (parseLiteral(elts["object"], literal_elts) != 0) {

	    type_str=getType(literal_elts);
	    if (type_str != "" && !(type_str in type_codes)) {
		print type_str | type_cmd;
	    }

	    lang_str=getLanguage(literal_elts);
	    if (lang_str != "" && !(toupper(lang_str) in type_codes)) {
		print lang_str | locale_cmd;
	    }
	}
    }

    if (predicateTypeFile != "") {
	if (setsFor == "subject") {
	    tc=type_code(elts["object"]);
	} else {
	    tc=type_code(elts["subject"]);
	}
	if (is_number(tc) == 1) {
	    collectPredicateType(elts["predicate"], "decfloat");
	} else if (is_date(tc) == 1) {
	    collectPredicateType(elts["predicate"], "timestamp");
	} else {
	    collectPredicateType(elts["predicate"], "varchar");
	}
    }
}

END {
    update_table();
    for (i in predCounts) {
	n = split(i, arr, "|");
	if (pawk == "yes") {
	    print (n-1), predCounts[i], i > EdgeSetsFile "." part;
	} else {
	    print (n-1), predCounts[i], i;
	}
    }

    if (predicateTypeFile != "") {
	for(p in predicate_type_table) {
	    print p "\t" predicate_type_table[p] > preds_output_file;
	}
    }
}
