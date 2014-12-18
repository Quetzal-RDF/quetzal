
function read_outgoing_edges() {
    delete current_edges;

    delete a;
    parse_for_elements(current_triple, a);
    current_subject = a["subject"];
    current_edges[a["predicate"]][a["object"]] = 1;

    do {
	ret = getline current_triple < bySubjectFile;
	if (ret <= 0) {
	    return -1;
	}

	delete a;
	parse_for_elements(current_triple, a);

	if (a["subject"] != current_subject) {
	    break;
	}

	current_edges[a["predicate"]][a["object"]] = 1;
    } while (1==1);

    return 1;
}

BEGIN {
    getline current_triple < bySubjectFile;
    read_outgoing_edges();
}

{
    delete elts;
    parse_for_elements($0, elts);

    obj = elts["object"];

    while (obj > current_subject) {
	code = read_outgoing_edges();
	if (code < 0) {
	    next;
	}
    }

    if (obj == current_subject) {
	for(pred in current_edges) {
	    for (obj in current_edges[pred]) {
		print elts["subject"] " " substr(elts["predicate"], 1, length(elts["predicate"])-1) "," substr(pred, 2) " " obj;
	    }
	}
	
    } else if (obj < current_subject) {
	next;
    }
}