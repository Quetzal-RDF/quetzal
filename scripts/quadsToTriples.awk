{
    parse_for_elements($0, elts);
    if (pawk == "yes") {
	print elts["subject"] " " elts["predicate"] " " elts["object"] " ." > fileName "." part;
    } else {
	print elts["subject"] " " elts["predicate"] " " elts["object"] " ."
    }
}
