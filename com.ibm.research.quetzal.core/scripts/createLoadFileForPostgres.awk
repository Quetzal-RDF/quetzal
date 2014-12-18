#
# Assumes sorted nt file by subject or by object
# Reads all hashes for predicate first
# Writes it out using as subject graph and then predicate value pairs,
# placing predicates in the right position based on hashes
#
# Takes as an argument hashFile which contains the hashes for each predicate
# Takes as argument fileType which indicates "quad" if the fileType is n-quads
#
#
BEGIN {
    columnDelimiter="|";
    rowDelimiter="\n";
    # hashes stores the 1 hash to store for each predicate
    # predicateOrder stores the set of predicates overloaded on a given column num
    delete hashes;
    delete predicateOrder;
    maxColumns = 0;

    while ((getline line < hashFile) > 0) {
	n = split(line, arr, " ");
	for (i = 2; i <= n; i++) {
	    hashes[arr[1]] = hashes[arr[1]] " " arr[i];
	    predicateOrder[arr[i]] = predicateOrder[arr[i]] " " arr[1];
	    if (arr[i] > maxColumns) {
		maxColumns = arr[i];
	    }
	}  
    }

    delete predicateObjects;
    delete predicateLiterals;

    currSubj ="";

 }

{
    n = parse_for_elements($0, elts)
    if (n == 0) {
	print "fatal error: cannot parse line " $0 > "/dev/stderr";
	exit -1;
    }

    if (index(elts["object"], "\"") == 1 && parseLiteral(elts["object"], a) != 0) {
	literal = getString(a);
	object = "";
    } else {
	object = fixBrackets(elts["object"]);
	literal = "";
    }
    
    entity = fixBrackets(elts["subject"]);
    
    if (entity != currSubj) {
	if(currSubj != "") printSubject(currSubj);
	delete predicateObjects;
	delete predicateLiterals;
	currSubj = entity;
    } else {
	predicate = elts["predicate"];
	if (object != "") {
	    predicateObjects[predicate] = predicateObjects[predicate] "," value;
	} else if (literal != "") {
	    predicateLiterals[predicate] = predicateLiterals[predicate] "," "\"" literal "\"";
	}
    }
}

function printSubject(subj) {
    printf("%s%s", subj, columnDelimiter) > primaryFile;
    for (i in predicateLiterals) {
	printf("%s=>%s", i, predicateLiterals[i]) > primaryFile;
    }
    for (i = 1; i <= maxColumns) {
	pred = predicateOrder[i];
	printf("%s|{%s}", pred, predicateObjects[pred]) > primaryFile;
	if (i != maxColumns) {
	    printf("|") > primaryFile;
	} else if (i == maxColumns) {
	    printf("\n") > primaryFile;
	}
    }
}

END {
    printSubject(currSubj);
}

