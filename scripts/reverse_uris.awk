BEGIN {
    prefixes["[[:blank:]]a[[:blank:]]"] = "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>";
}

function reverse_uri(uri) {
    if (match(uri, /^<[^>[:blank:]]*>$/) == 1) {
	return "<" reverse_str(substr(uri, 2, length(uri)-2)) ">";
    } else {
	return uri;
    }
}

/^(prefix|PREFIX)[[:blank:]]+[^[:blank:]]+[[:blank:]]*</ {
    match($0, /^(prefix|PREFIX)[[:blank:]]+([^[:blank:]]*)[[:blank:]]*(<[^>]*>)/, a);
    p = substr($0, a[2, "start"], a[2, "length"]);
    x = substr($0, a[3, "start"], a[3, "length"]);
    prefixes[p] = x;
    next;
}

{
    if (parse_for_elements($0, elts) > 0) {
	subject = reverse_uri(elts["subject"]);
	predicate = reverse_uri(elts["predicate"]);
	object = elts["object"];
#	if (parseLiteral(object, lit_elts) != 0) {
#	    if (getType(lit_elts) != "") {
#		object = "\"" getString(lit_elts) "\"^^" reverse_uri("<" getType(lit_elts) ">");
#	    } 
#	} else {
	    object = reverse_uri(elts["object"]);
#	}
	graph = reverse_uri(elts["graph"]);
	if (graph != "") {
	    if (pawk == "yes") {
		print subject " " predicate " " object " " graph " ." > file "." part;
	    } else {
		print subject " " predicate " " object " " graph " .";
	    }
	} else {
	    if (pawk == "yes") {
		print subject " " predicate " " object " ." > file "." part;
	    } else {
		print subject " " predicate " " object " .";
	    }
	}
    } else {
	str = $0;

	for(prefix in prefixes) {
	    result = "";

	    if (prefix == ":") {
		start="[[:blank:]]";
	    } else {
		start="\\<";
	    }

	    while ((offset = match(str, start "(" prefix ")([^()<[:blank:]\r]*)", a)) > 0) {
		result = result substr(str, 1, a[1,"start"]-1);
		uri=substr(prefixes[prefix], 1, length(prefixes[prefix])-1) "" substr(str, a[2, "start"], a[2, "length"]) ">";
		result = result uri;
		str = substr(str, offset + a[0, "length"]);
	    }
	    str = result str;
	}

	result = "";
	while (match(str, /(^|[^^])(<[^>[:blank:]]*>)/, a) > 0) {
	    offset = a[2, "start"];
	    result = result substr(str, 1, offset-1);
	    result = result reverse_uri(substr(str, offset, a[2, "length"]));
	    str = substr(str, offset + a[2, "length"]);
	}
	if (pawk == "yes") {
	    print result str > file "." part;
	} else {
	    print result str;
	}
    }
}
