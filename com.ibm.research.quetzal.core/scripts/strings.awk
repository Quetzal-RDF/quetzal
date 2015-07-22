BEGIN {
    defaultType = "<http://untyped.literal>";
}

function reverse_str(str,  new_str, i) {
    new_str = "";
    for(i = length(str); i >= 1; i--) {
	new_str = new_str substr(str, i, 1);
    } 
    return new_str;
}

function trimString(str) {
    return gensub(/^[[:space:]]*/, "", "", gensub(/[[:space:]]*$/, "", "", str));
}

function fixBrackets(str) {
    if (index(str, "<") == 1) {
	return gensub(/^<([^>]*)+>$/, "\\1", "g", str);
    } else {
	return str;
    }
}

function parseLiteral(literal, result) {
    return match(literal, /^"(.*)"(\^\^<([^"]*)>|\@([^"]*))?$/, result);
}

function getString(a) {
    return a[1];
}

function getType(a) {
    return a[3];
}

function getLanguage(a) {
    return a[4];
}

function getTag(a) {
    if (getLanguage(a) != "") {
	return getLanguage(a);
    } else if (getType(a) != "") {
	return getType(a);
    } else {
	return defaultType;
    }
}


