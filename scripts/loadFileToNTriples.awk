BEGIN {
    numTriples = 0;

    if (longStringFile != "") {
	while ((getline line < longStringFile) > 0) {
	    split(line, arr, /\t/);
	    string_table[arr[1]] = arr[2];
	}
    }

    if (typeFile != "") {
	while ((getline line < typeFile) > 0) {
	    match(line, /^(.*) ([1-9][0-9]+)$/, stuff);
	    type = substr(line, stuff[1, "start"], stuff[1, "length"]);
	    if (type != "DATATYPE_NUMERIC_IDS_START" &&
		type != "DATATYPE_NUMERIC_IDS_END" &&
		type != "USER_ID") 
	    {
		id = substr(line, stuff[2, "start"], stuff[2, "length"]);
		types[id] = type;
	    }
	}
    }
}

function long_string(str) {
    if (str in string_table) {
	return string_table[str];
    } else { 
	return str;
    }
}

function fix_hacked_literal(str, type) {
    str = long_string(str);
    if ((typesFile=="" && type==20) || 
	"IRI_ID" == types[type]) 
    {
	return add_brackets(str);
    } else if ((typesFile=="" && type==10000) || 
	       "SIMPLE_LITERAL_ID" == types[type]) 
    {
	return "\"" str "\"";
    } else if ((typesFile == "" && type == 1) ||
	       "BLANK_NODE_ID" == types[type])
    {
	return str;
    } else if (type in types) {
	typeStr = types[type];
	if (type > 10000) {
	    return "\"" str "\"@" typeStr;
	} else {
	    return "\"" str "\"^^<" typeStr ">";
	}
    }

    return "\"" str "\"";
}

function add_brackets(str) {
    str = long_string(str);
    if (index(str, "_:") != 1) {
	return "<" str ">";
    } else {
	return str;
    }
}
    
{
    n = split($0, arr, /\t/);
    for (i = (fileFor=="subj")? 4 : 7; i < n; i+=((fileFor=="subj")? 3: 2)) {
	if (arr[i] != "") {
	    if (fileFor=="subj") {
		if (index(arr[i+1],"lid:")==1) {
		    lids[arr[i+1]]= arr[1] "\t" arr[2] "\t" arr[i];
		} else {		    
		    print add_brackets(arr[1]) " " add_brackets(arr[i]) " " fix_hacked_literal(arr[i+1],arr[i+2]) " " ((arr[2]=="DEF")? "." : add_brackets(arr[2]) " .");
		}
	    } else {
		if (index(arr[i+1],"lid:")==1) {
		    lids[arr[i+1]]= arr[1] "\t" arr[2] "\t" arr[i] "\t" arr[6];
		} else {
		    print add_brackets(arr[i+1]) " " add_brackets(arr[i]) " " fix_hacked_literal(arr[1],arr[6]) " " ((arr[2]=="DEF")? "." : add_brackets(arr[2]) " .");
		}
	    }
	    numTriples++;
	}
    }
}


END {
    if (secondaryFile != "") {
	while ((getline line < secondaryFile) > 0) {
	    n = split(line, arr, "\t");
	    lid = trimString(arr[2]);
	    if (lid in lids) {
		k = split(lids[lid], arr2, "\t");
		if (fileFor=="subj") {
		    print add_brackets(arr2[1]) " " add_brackets(arr2[3]) " " fix_hacked_literal(arr[3],arr[4]) " " ((arr2[2]=="DEF")? "." : add_brackets(arr2[2]) " .");
		} else {
		    print add_brackets(arr[3]) " " add_brackets(arr2[3]) " " fix_hacked_literal(arr2[1],arr2[4]) " " ((arr2[2]=="DEF")? "." : add_brackets(arr2[2]) " .");
		}
		numTriples++;
	    } else if (index(lid, "fake_lid:") != 1) {
		print "error: lid not found: '" lid "'" > "/dev/stderr";
	    }
	}
    }
}
