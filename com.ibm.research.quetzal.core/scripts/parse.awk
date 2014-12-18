BEGIN {
    subject_element = 1;
    predicate_element = 4;
    object_element = 5;
    graph_element = 13;
}

function parse(nt_line, a) {
    return match(nt_line, /[[:blank:]]*((<[^>\n\r]*>)|(_:[A-Za-z0-9][_A-Za-z0-9-]*))[[:blank:]]+(<[^>\n\r]*>)[[:blank:]]+((<[^>\n\r]*>)|(_:[A-Za-z0-9][_A-Za-z0-9-]*)|(\"([^"]|\\\")*\"((@[A-Za-z][A-Za-z0-9-]*)|(\^\^<[^>\n\r]*>))?))[[:blank:]]*(<[^>\n\r]*>)?[[:blank:]]*\./, a);
}

function parse_for_elements(nt_line, a, b, x) {
    x = parse(nt_line, b);
    a["subject"] = substr(nt_line, b[subject_element, "start"], b[subject_element, "length"]);
    a["object"] = substr(nt_line, b[object_element, "start"], b[object_element, "length"]);
    a["predicate"] = substr(nt_line, b[predicate_element, "start"], b[predicate_element, "length"]);
    a["graph"] = substr(nt_line, b[graph_element, "start"], b[graph_element, "length"]);
    return x;
}

function parse_for_element(nt_line, element, a) {
    parse(nt_line, a);
    return substr(nt_line, a[element, "start"], a[element, "length"]);
}

function parse_for_subject(nt_line) {
    return parse_for_element(nt_line, subject_element);
}

function parse_for_predicate(nt_line) {
    return parse_for_element(nt_line, predicate_element);
}

function parse_for_object(nt_line) {
    return parse_for_element(nt_line, object_element);
}

function parse_for_graph(nt_line) {
    return parse_for_element(nt_line, graph_element);
}

