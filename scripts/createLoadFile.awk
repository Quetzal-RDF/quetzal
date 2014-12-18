#
# Assumes sorted nt file by subject or by object
# Reads all hashes for predicate first
# Writes it out using as subject graph and then predicate value pairs,
# placing predicates in the right position based on hashes
#
# Takes as an argument hashFile which contains the list of 3 hashes for the load
# Takes as argument fileType which indicates "quad" if the fileType is n-quads
#
#
BEGIN {
    columnDelimiter="\t";
    rowDelimiter="\n";

    if (dbEngine == "db2") {
	stringDelimiter="\a";
	nullIndicator = "";
    } else if (dbEngine == "postgresql") {
	stringDelimiter="\a";
	nullIndicator = "";
    } else if (dbEngine == "shark") {
    
#		TODO: REVIT: should we also specify a different stringDelimiter? 
    	
    	stringDelimiter="";
		nullIndicator = "\\N";
	}

    numColors = 0;
    while ((getline line < hashFile) > 0) {
	n = split(line, arr, " ");
	for (i = 2; i <= n; i++) {
	    if (arr[i] != "null") {
		hashes[arr[1]] = hashes[arr[1]] " " arr[i];
		if (numColors < arr[i]) {
		    numColors = arr[i];
		}
	    }
	}  
    }

    if (multiValuePredicatesFile != "") {
	while ((getline line < multiValuePredicatesFile) > 0) {
	    multi_preds[line] = 1;
	}
    }

    if (priorityPredicatesFile != "") {
	while ((getline line < priorityPredicatesFile) > 0) {
	    priority_preds["<" line ">"] = 1;
	}
    }

    delete cols;

    currSubj ="";
    id = 0;
    delete row;
    delete graphs;

    if (pawk == "yes") {
	primaryFile = primaryFile "." part;
	secondaryFile = secondaryFile "." part;
	predicateInfoFile = predicateInfoFile "." part;
    }
}

function printEntry(x,  a,str) {
    if (index(x, "\"") == 1 && parseLiteral(x, a) != 0) {
	str = getString(a);
    } else {
	str = fixBrackets(x);
	if (reverseUris != "") {
	    str = reverse_str(str);
	}
    }
    return hash_string(str,x);
}

function printSubject(subj,  i,j,k,spill,code) {
    for (k in graphs) {
	spill = (row[k] > 1)? 1: 0;
	for (i = 0; i < row[k]; i++) {
	    printf("%s%s%s%s%s%s%s%s%s", 
		   stringDelimiter, printEntry(subj), stringDelimiter,
		   columnDelimiter, 
		   stringDelimiter, printEntry(k), stringDelimiter, 
		   columnDelimiter, 
		   spill) > primaryFile ; 
	    if (setsFor=="object") {
		code = type_code(subj);
		printf("%s%s%s%s%s%s", 
		       columnDelimiter,
		       is_number(code)? as_number(subj): nullIndicator,
		       columnDelimiter, 
		       is_date(code)? as_date(subj): nullIndicator, 
		       columnDelimiter, 
		       code) > primaryFile;
	    }

	    for (j = 0; j <= (numColors * 2) + 1; j += 2) {
		if ((k, i, j) in cols) {
		    p = cols[k, i, j];
		    v = cols[k, i, j+1];
		    if ((p in multi_preds) && index(v, "lid:") != 1) {	   
			printSecondaryRow(subj, p, k, "fake_lid:" part ":" id, v);
			id++;
		    }
		}
	    }

	    if (setsFor=="object") {
		for (j = 0; j <= (numColors * 2) + 1; j++) {
		    printf("%s%s", 
			   columnDelimiter,
			   ((k, i, j) in cols)? stringDelimiter printEntry(cols[k, i, j]) stringDelimiter: nullIndicator) > primaryFile;
		}
	    } else {
		for (j = 0; j <= (numColors * 2) + 1; j++) {
		    printf("%s%s", 
			   columnDelimiter,
			   ((k, i, j) in cols)? stringDelimiter printEntry(cols[k, i, j]) stringDelimiter: nullIndicator) > primaryFile;
		    if (j%2 == 1) {
			printf("%s%s", 
			       columnDelimiter,
			       ((k, i, j) in cols)? type_code(cols[k, i, j]): nullIndicator) > primaryFile;			
		    }
		}
	    }
	    printf(rowDelimiter) > primaryFile;
	}
    }
}

function printSecondaryRow(entity, property, graph, lid, value) {
    printf("%s%s%s%s", stringDelimiter, printEntry(graph), stringDelimiter, columnDelimiter) > secondaryFile;

    if (dontUseLids != "yes") {
	printf("%s%s%s%s", stringDelimiter, lid, stringDelimiter, columnDelimiter) > secondaryFile;
    }

    printf("%s%s%s", stringDelimiter, printEntry(value), stringDelimiter) > secondaryFile;
    
    if (setsFor == "subject") {
	printf("%s%s", columnDelimiter, type_code(value)) > secondaryFile;
    }

    if (useEntityInSecondary=="yes") {
	printf("%s%s%s%s", columnDelimiter, stringDelimiter, printEntry(entity), stringDelimiter) > secondaryFile;
	if (setsFor == "object") {
	    printf("%s%s", columnDelimiter, type_code(entity)) > secondaryFile;
	}
    }

    if (usePropertyInSecondary=="yes") {
	printf("%s%s%s%s", columnDelimiter, stringDelimiter, printEntry(property), stringDelimiter) > secondaryFile;
    }

    if (setsFor == "subject") {
	printf("%s%s", columnDelimiter, rowDelimiter) > secondaryFile;
    } else {
	printf(rowDelimiter)  > secondaryFile;
    }
}

function doInsert(entity, predicate, value, graph,   done) {
    done = 0;
    
    # try inserting into any of the existing rows
    for (i = 0; i < row[graph]; i++)
    {
	if (tryInsert(entity, i, predicate, value, graph) == 1)
	{
	    done = 1;
	    break;
	}
    }

    # could not insert this data at all, have to add a new row
    if (done != 1) 
    {
	tryInsert(entity, row[graph], predicate, value, graph);
	row[graph]++;
    }
}

function tryInsert(entity, currentRow, predicate, value, graph,   n, c, i, filled, type) {
    if (! (predicate in hashes)) {
	print "fatal error: no hashes for predicate " predicate "(" currentRow "," entity "," value ")";
	return 0;
    }
    n = split(hashes[predicate], c, " ");
    # try inserting into cols at the specified location.  if its occupied with the same pred
    # we need to write to the secondary hash.  If not, we need to write to primary file
    filled = 0;
    for (i = 1; i <= n; i++) {
	if (!((graph, currentRow, 2*c[i]) in cols)) {
	    cols[graph, currentRow, 2*c[i]] = predicate;
	    cols[graph, currentRow, 2*c[i]+1] = value;
	    filled = 1;
	    break;
	} else {
	    # we have this exact same predicate already for this row
	    if (cols[graph, currentRow, 2*c[i]] == predicate) {

		if (multiValuePredicatesFile != "" && !(predicate in multi_preds)) {
		    print "fatal error: need lid for purportedly single-valued " predicate;
		    exit -1;
		}

		v = cols[graph, currentRow, 2*c[i]+1];
		if (index(v, "lid:") != 1) {
		    # current value is a single value, not an lid...

		    # make an lid
		    id++;	
		    thisLid = "lid:" part ":" id;
		    cols[graph, currentRow, 2*c[i]+1] = "lid:" part ":" id;

		    # save current value in secondary table
		    printSecondaryRow(entity, predicate, graph, thisLid, v);

		} else {   
		    # otherwise, we have an lid to use already
		    thisLid = v;
		}

                # put value into secondary table 
		printSecondaryRow(entity, predicate, graph, thisLid, value);
		filled=1;
		break;

	    # make space for priority predicates by evicting others
	    } else if ((predicate in priority_preds) && !(cols[graph, currentRow, 2*c[i]] in priority_preds)) {
		old_pred=cols[graph, currentRow, 2*c[i]];
		old_value=cols[graph, currentRow, 2*c[i]+1];

		cols[graph, currentRow, 2*c[i]] = predicate;
		cols[graph, currentRow, 2*c[i]+1] = value;
		filled = 1;

		doInsert(entity, old_pred, old_value, graph);

		break;
	    }
	}
    }

    # record hash and spill information per predicate
    if (filled == 1) {
	if (!(predicate in hash_count) || (i > hash_count[predicate])) {
	    hash_count[predicate] = i;
	}
	if (currentRow > 0) {
	    spills[predicate] = 1;
	}
    }

    return filled;
}

{
    if (pawk != "yes") {
	n = parse_for_elements($0, elts)
	if (n == 0) {
	    print "fatal error: cannot parse line " $0 > "/dev/stderr";
	    exit -1;
	}
    }

    object = elts["object"];
    graph = "DEF";
    if (fileType=="quad") {
	graph = elts["graph"];
	if (trimString(graph) == "") {
	    graph = "DEF";
	}
    }
    
    if (setsFor=="subject") {
	entity = elts["subject"];
	value = object;
    } else {
	entity = object;
	value = elts["subject"];
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

    doInsert(entity, elts["predicate"], value, graph);
}

END {
    printSubject(currSubj);

    if (predicateInfoFile != null) {
	for(predicate in hash_count) {
	    hc = hash_count[predicate];
	    if (predicate in spills) {
		s = spills[predicate];
	    } else {
		s = 0;
	    }

	    print substr(predicate,2,length(predicate)-2) "\t" hc "\t" s > predicateInfoFile;
	}
    }
}

