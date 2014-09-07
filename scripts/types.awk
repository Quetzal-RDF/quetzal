BEGIN {
    while ((getline line < typeCodeFile) > 0) {
	match(line, /^(.*[^[:blank:]])[[:blank:]]+([0-9]+)$/, a); 
	code = substr(line, a[2, "start"], a[2, "length"]);
	type = substr(line, a[1, "start"], a[1, "length"]); 
	type_codes[type] = code;
    }
}

function is_number(type) {
    return (type >= type_codes["DATATYPE_NUMERICS_IDS_START"] && type <= type_codes["DATATYPE_NUMERICS_IDS_END"])? 1: 0;
}

function as_number(entry, a) {
    if (is_number(type_code(entry)) != 0) {
	parseLiteral(entry, a);
	if (strtonum(getString(a) "1") != 0) {
	    return getString(a);
	}
    }
}

function is_date(type) {
    return (type == type_codes["http://www.w3.org/2001/XMLSchema#dateTime"])? 1: 0;
}

BEGIN {
    if (dbEngine == "postgresql") {
	date_cmd = "date --utc --file=- --iso-8601=ns 2>&1"
    } else {
	date_cmd = "date --utc --file=- +\"%F-%H.%M.%S.%N\" 2>&1"
    }
    PROCINFO[date_cmd, "pty"] = 1;
}

function as_date(entry, cmd, a) {
    if (is_date(type_code(entry)) != 0) {
	parseLiteral(entry, a);
	raw_date = getString(a);
	print raw_date |& date_cmd;
	date_cmd |& getline date;
	if (index(date, "invalid date") == 0) {
	    if (dbEngine == "postgresql") {
		return gensub(/,/, ".", "g", date);
	    } else {
		return substr(date, 1, length(date)-3);
	    }
	}
    }
}

function type_code(entry, stuff) {
    if (parseLiteral(entry, stuff) != 0) {
	if (getType(stuff) != "") {
	    return type_codes[getType(stuff)];
	} else if (getLanguage(stuff) != "") {
	    return type_codes[toupper(getLanguage(stuff))];
	} else {
	    return type_codes["SIMPLE_LITERAL_ID"];
	}
    } else {
	if (index(entry, "_:") == 1) {
	    return type_codes["BLANK_NODE_ID"];
	} else if (entry == "") {
	    return type_codes["NONE_ID"];
	} else if (index(entry, "lid:") == 1) {
	    return -1;
	} else {
	    return type_codes["IRI_ID"];
	}
    }
}

function checkForUnknownType(entry,stuff) {
	if (parseLiteral(entry, stuff) != 0) {
		if (getType(stuff) != "") {
		    if( type_codes[getType(stuff)] == "" ){
		    	return getType(stuff);
		    }
		}
	}
}

function checkForUnknownLanguage(entry,stuff) {
	if (parseLiteral(entry, stuff) != 0) {
		if (getLanguage(stuff) != "") {
			if( type_codes[getLanguage(stuff)] == "" ){
		    	return getLanguage(stuff);
		    }
		}
	}
}
