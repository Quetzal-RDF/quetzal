{ 
    parse_for_elements($0, elts);

    if (fileType == "quad") {
	found = elts["subject"] " " elts["predicate"] " " elts["object"] " " elts["graph"] " .";
    } else {
	found = elts["subject"] " " elts["predicate"] " " elts["object"] " .";
    }

    line = trimString($0);

    if (line != found) {
      print "bad line " $0 ": %%" line "%%" found "%%";
    }
}
