BEGIN {

    numColors = 0;

    while ((getline line < hashFunctions) > 0) {
	n = split(line, arr, " ");
	for (i = 2; i <= n; i++) {
	    hashes[arr[1]] = hashes[arr[1]] " " arr[i];
	    if (numColors < arr[i]) {
		numColors = arr[i];
	    }
	}  
    }
    # hashes are 0 based -- correct for that
    if (index(hashFunctions, "0_") > 0) {
	numColors += 1;
    }
    totalConflicts = 0;
    totalSpaceUsed = 0.0;
    FS = "|";
    totalLinesWithConflicts = 0;
    totalRecords = 0;
    sumLineLength = 0;
    linearProbingCost = 0;
}

function len(arr, k, i) {
    i = 0;
    for (k in arr) {
	i++;
    }
    return i;
}

{
    delete idOccurrencePerRow;
    currentLineConflicts = 0;
    # for all predicates on a line
    for (i = 2; i <= NF; i++) {
	k = 0;
	# for a given predicate, simulate an insert into a row across multiple hash functions
	n = split(hashes[$i], hashesForPred, " ");
	
	for (j = 1; j <= n; j++) {
	    if (! (hashesForPred[j] in idOccurrencePerRow)) {
		idOccurrencePerRow[hashesForPred[j]] = 1;
		k++;
		break;
	    }
	}
	# could not insert across all hash functions for this predicate, conflict
	if (k == 0) {
#	    print $0 " has conflicts for line:" NR " " $i " which has the following hashes:";
#	    for (m = 1; m <=n; m++) {
#		print hashesForPred[m];
#	    }
#	    print " ids seen so far: ";
#	    for (m in idOccurrencePerRow) {
#		print m;
#	    }
#	    print "incrementing current conflicts when k = 0" currentLineConflicts;
	    currentLineConflicts++;
	}
    }
    split($1, array, " ");
    if (array[1] > numColors) {
	linearProbingCost += array[2];
    }

    if (currentLineConflicts > 0) {
#	print NR " k:" k "currentLineConflicts:" currentLineConflicts;
#	print $0 " has " currentLineConflicts " at line: " NR " for num edgeSets: " array[2];
	totalLinesWithConflicts+= array[2];
	totalConflicts = totalConflicts + (currentLineConflicts * array[2]);
    }
    
    totalSpaceUsed += len(idOccurrencePerRow) * array[2];
    totalRecords += array[2];
    sumLineLength += array[1] * array[2];
}

END {
    print "Total conflicts:" totalConflicts;
    print "Total num of records with conflicts:" totalLinesWithConflicts;
    print "Percentage of rows with conflicts:" totalLinesWithConflicts / totalRecords;
    print "Total records:" totalRecords;
    print "Total space absolute:" totalSpaceUsed;
    print "numColors :" numColors " NR: " NR " total space rows * cols: " numColors * totalRecords;
    print "Total space used:" totalSpaceUsed / (numColors * totalRecords);
    print "Average line length:" sumLineLength / totalRecords;
    print "Linear probing:" linearProbingCost / totalRecords;
}