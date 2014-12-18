BEGIN {
    last_triple = "";
    if (direction == "subject") {
	value = "object";
	skip = 3;
	start = 4;
    } else {
	value = "subject";
	skip = 2;
	start = 5;
    }
}

function next_triple(file, elts,  line, stuff) {
    if (last_triple != "") {
	line = last_triple;
	last_triple = "";
    } else {
	if ((getline line < file ) < 0) {
	    return -1;
	}
    }
    
    split(line, stuff, "\t");
    elts["subject"] = (direction == "subject")? stuff[5]: stuff[3];
    elts["predicate"] = stuff[6];
    elts["object"] = (direction == "subject")? stuff[3]: stuff[4];

    return line;
}

function next_value(file, predicate, entity, first,  elts, line) {
    while ((line = next_triple(file, elts)) != -1 && (fixBrackets(elts[ direction ]) == entity || first==1)) {
	if (fixBrackets(elts[ direction ]) == entity && fixBrackets(elts["predicate"]) == predicate) {
	    last_triple = "";
	    return fixBrackets(elts[ value ]);
	}
    } 

    last_triple = line;
    return -1;
}

{
    pad = ((direction=="subject")?3:2)*addedCols;
    entity = $1;
    originalCols = NF;
    for(i = start; i <= originalCols; i += skip) {
	if ($i == predicate) {
	    if (index($(i+1), "lid:") == 1) {
		first = 1;
		second = 0;
		while ((target = next_value(secondaryFile, $i, entity, first)) != -1) {
		    if (first == 1) {
			$(i + 1) = target;
			if (direction == "subject") {
			    $(i + 2) = type_code(target);
			}
			first = 0;
			second = 1;
		    } else {
			if (second == 1) {
			    second = 0;
			    $NF = predicate;
			    pad -= 2;
			} else {
			    $(NF + 1) = predicate;
			    pad -= 3;
			}
			$(NF + 1) = target;
			if (direction == "subject") {
			    $(NF + 1) = type_code(target);
			}
		    }
		}
	    }
	}
    }

    if (pad > 0) {
	for(i = 0; i < pad; i++) {
	    $(NF + 1) = "";
	}
    }

    for(i = 1; i < NF; i++) {
	printf("%s\t", $i);
    }
    print $NF;

}
