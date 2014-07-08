#
# Assumes sorted nt file by subject or by object
# Reads all hashes for predicate first
# Writes it out using as subject graph and then predicate value pairs,
# placing predicates in the right position based on hashes
#
# Takes as an argument hashFile which contains the list of 3 hashes for the load
# Takes as argument fileType which indicates "quad" if the fileType is n-quads
# Takes as an argument systemPredicates which indicates which predicates need their own dedicated columns
#
#
BEGIN {
    columnDelimiter="\t";
    rowDelimiter="\n";

    numColors = 0;
    subjectPrimaryFile="direct_primary.load";
    subjectSecondaryFile="direct_secondary.load"
    while ((getline line < hashFile) > 0) {
	n = split(line, arr, " ");
	for (i = 2; i <= n; i++) {
	    hashes[arr[1]] = hashes[arr[1]] " " arr[i];
	    if (numColors < arr[i]) {
		numColors = arr[i];
	    }
	}  
    }

    systemPredicates = 0;
    while ((getline line < systemPredicates) > 0) {
	n = split(line, arr, " ");
	systemPredicates++;
    }
    
    delete cols;

    currSubj ="";
    id = 0;
    delete row;
    delete graphs;
}

function fixBrackets(str) {
    return gensub(/^<([^>]*)+>$/, "\\1", "", str);
}

function trimString(str) {
    return gensub(/^[[:space:]]*/, "", "", gensub(/[[:space:]]*$/, "", "", str));
}

function printSubject(subj, i,j,k,spill) {
    for (k in graphs) {
	spill = (row[k] > 1)? 1: 0;
	for (i = 0; i < row[k]; i++) {
	    printf("%s%s%s%s%s%s", fixBrackets(subj), columnDelimiter, fixBrackets(k), columnDelimiter, spill, columnDelimiter) > subjectPrimaryFile ;
	    # system predicates are the first few columns in the order
            # provided to the coloring alg. Just write these out differently
            # meaning their values are written directly out
	    m = 2;
	    for (j = 2; j < (systemPredicates + 2); j++) {
		m = j+1;
		printf("%s%s", fixBrackets(cols[k, i, m]), columnDelimiter) > subjectPrimaryFile;
	    }
	    if (systemPredicates > 0) {
		m++;
	    }
	    for (j = m; j < (numColors+1) * 2; j++) {
		printf("%s%s", fixBrackets(cols[k, i, j]), columnDelimiter) > subjectPrimaryFile;
	    }
	    printf(rowDelimiter) > subjectPrimaryFile;
	}
    }
}

function find_object(nt_line, first, second, opt_graph,  len, rest, start, startLiteral, startObject, str) {
    rest = substr(nt_line, length(first)+length(second));
    startLiteral = index(rest, "\"");
    startObject = index(rest, "<");
    if (startLiteral > 0) {
	start = startLiteral;
    } else {
	start = startObject;
    }
    if (fileType=="quad") {
	len = index(rest, opt_graph)-start-1;
    } else {
	len = length(rest)-start-1;
    }
    # strip off quotes if they occur in a typedLiteral
    str = substr(rest, start, len);
    if (index(str,"\"") == 1 && match(str, "\\^\\^[^\"]*$") > 0) {
	str = substr(str, 2);
	gsub("\"\\^\\^", "^^", str);
    }

    return str;
}

function printSecondaryRow(graph, lid, value) {
    print(fixBrackets(graph) columnDelimiter lid columnDelimiter fixBrackets(value)) > subjectSecondaryFile;
}

function tryInsert(currentRow, predicate, value, graph, n, c, i, filled) {
    if (! (predicate in hashes)) {
	print "fatal error: no hashes for predicate " predicate;
	exit -1;
    }
    n = split(hashes[predicate], c, " ");
    # try inserting into cols at the specified location.  if its occupied with the same pred
    # we need to write to the secondary hash.  If not, we need to write to primary file
    filled = 0;
    for (i = 1; i <=n; i++) {
	if (!((graph, currentRow, 2*c[i]) in cols)) {
	    cols[graph, currentRow, 2*c[i]] = predicate;
	    cols[graph, currentRow, 2*c[i]+1] = value;
	    filled = 1;
	    break;
	} else {
	    # we have this exact same predicate already for this row
	    if (cols[graph, currentRow, 2*c[i]] == predicate) {
		v = cols[graph, currentRow, 2*c[i]+1];
		if (index(v, "lid:") != 1) {
		    # current value is a single value, not an lid...

		    # make an lid
		    id++;	
		    thisLid = "lid:" id;
		    cols[graph, currentRow, 2*c[i]+1] = "lid:" id;

		    # save current value in secondary table
		    printSecondaryRow(graph, thisLid, v);
		} else {   
		    # otherwise, we have an lid to use already
		    thisLid = v;
		}

                # put value into secondary table 
		printSecondaryRow(graph, thisLid, value);
		filled=1;
		break;
	    }
	}
    }
    return filled;
}

{
    graph = "";
    if (fileType=="quad") {
	object = find_object($0, $1, $2, $(NF-1));
	graph = $(NF-1);
    } else {
	object = find_object($0, $1, $2);
    }
    
    if (setsFor=="subject") {
	entity = $1;
	value = object;
    } else {
	entity = object;
	value = $1;
    }

    if (entity != currSubj) {
	if(currSubj != "")printSubject(currSubj);
	delete cols;
	delete graphs;
	delete row;
	currSubj = entity;
    }

    graphs[graph] = 1;

    if (! (graph in row)) {
	row[graph] = 1;
    }

    done = 0;
    # try inserting into any of the existing rows
    for (i = 0; i < row[graph]; i++) {
	if (tryInsert(i, $2, value, graph) == 1) {
	    done = 1;
	    break;
	}
    }
    if (done != 1) {
	# could not insert this data at all, have to add a new row
	tryInsert(row[graph], $2, value, graph);
	row[graph]++;
    }
}
END {
    printSubject(currSubj);
}

