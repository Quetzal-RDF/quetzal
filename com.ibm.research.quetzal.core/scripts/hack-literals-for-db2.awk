{
    parse_for_elements($0, a);
    if (parseLiteral(a["object"], l) > 0) {
	newLit = gensub(/^[[:blank:]]+/, "", "g", gensub(/[[:blank:]]+$/, "", "g", getString(l)));
	print a["subject"] " " a["predicate"] " \"" newLit "\"" (getType(l) != ""? "^^<" getType(l) "> .": (getLanguage(l) != ""? "@" getLanguage(l) " .": " .")); 
    } else {
	print $0;
    }
}
