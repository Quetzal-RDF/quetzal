{
    if (parse_for_elements($0, elts) > 0) {
	subject = elts["subject"];
	predicate = elts["predicate"];
	object = elts["object"];
	graph = elts["graph"];
	if (graph != "") {
	    line = subject " " predicate " " object " " graph " .";
	} else {
	    line = subject " " predicate " " object " .";
	}
	if (line != $0) {
	    print "line: " $0 " was parsed as: " line > "/dev/stderr";
	    exit -1;
	}
    } else {
	print "cannot parse line: " $0 > "/dev/stderr";
	exit -1;
    }
}
