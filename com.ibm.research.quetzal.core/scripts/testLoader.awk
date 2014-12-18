#
# Reads in a direct primary load file
# and a direct secondary load file
# ensure we have the right number of triples
#

function long_string(str) {
    if (str in string_table) {
	return string_table[str];
    } else { 
	return str;
    }
}

BEGIN {
    delete lids;
    delete triples;

    mappingFunctionsUsed=0;
    numTriples = 0;
    while ((getline line < predicateMappingFile) > 0) {
	n = split(line, arr, " ");
	for (i = 2; i <= n; i++) {
	    p = fixBrackets(arr[1]);
	    hashes[p] = hashes[p] " " arr[i];
	    if (numColors < arr[i]) {
		numColors = arr[i];
	    }
	}  
    }
    if (longStringFile != "") {
	while ((getline line < longStringFile) > 0) {
	    split(line, arr, /\t/);
	    string_table[arr[1]] = arr[2];
	}
    }
    while ((getline line < directFile) > 0) {
	n = split(line, arr, /\t/);
	for (i = 4; i < n; i+=2) {
	    if (arr[i] != "") {

		fn_count = split(hashes[arr[i]], fns, " ");
		for(fn = 1; fn <= fn_count; fn++) {
		    if (fns[fn] == (i - 4)/2) {
			if (mappingFunctionsUsed < fn) {
			    mappingFunctionsUsed = fn;
			}
			break;
		    }
		}

		if (index(arr[i+1],"lid:")==1) {
		    lids[arr[i+1]]= long_string(arr[1]) "\t" long_string(arr[2]) "\t" long_string(arr[i]);
		} else {
		    key=long_string(arr[1]) "\t" long_string(arr[2]) "\t" long_string(arr[i]) "\t" long_string(arr[i+1]);
		    triples[key] = 1;
		    numTriples++;
		}
	    }
	}
    }
    if (secondaryFile != "") {
	while ((getline line < secondaryFile) > 0) {
	    n = split(line, arr, "\t");
	    lid = trimString(arr[2]);
	    if (lid in lids) {
		triples[lids[lid] "\t" long_string(arr[3])] = 1;
		numTriples++;
	    } else {
		print "error: lid not found: '" lid "'";
	    }
	}
    }
}

function strip_brackets(str) {
    return gensub(/^<([^>]*)>$/, "\\1", "", str);
}

{
    parse_for_elements($0, elts);
    subject = fixBrackets(elts["subject"]);
    predicate = fixBrackets(elts["predicate"]);
    object = fixBrackets(stripQuotes(elts["object"]));
    if (fileType=="quad") {
	graph = fixBrackets(elts["graph"]);
    } else {
	graph = "DEF";
    }

    if (setsFor=="subject") {
	triple = subject "\t" graph "\t" predicate "\t" object; 
    } else {
	triple = object "\t" graph "\t" predicate "\t" subject; 
    }

    if (!(triple in triples)) {
	print subject;
	print predicate;
	print object;
	print graph;
	print "Error: cannot find '" triple "' in  triples ";
    }

    graphTriples++;  
}
END {
    print "Load file used " mappingFunctionsUsed " mapping function(s)";
    if (numTriples != graphTriples) {
	print "Error in loading: expected:" graphTriples "but got:" numTriples;
    } else {
	print "verified " numTriples " triples";
    }
}
