BEGIN {
    delete predToCols;
    max = 0;
    maxStr ="";
    while ((getline line < predicateMappingFile) > 0) {
	match(line, /[[:blank:]]*(<[^>\n\r]*>)[[:blank:]]*([0-9]*)/, arr);
	col = arr[2] * 1.0;
	predToCols[arr[1]]= col ;
	print "putting " col " in " arr[1];
	if (col > max) {
	    print " arr is greater than max" arr[2] " "  max;
	    max = col;
	    maxStr = arr[1];
	}
    }
    totalpreds = 0;
    for (i in predToCols) {
	totalPreds++;
    }
    print "max:" max, " maxstr:" maxStr;
    print "total preds:" totalPreds;
    sumConflicts = 0;
    total = 0;
}

{
    delete cols;
    edgeSet = $3;
    n = split(edgeSet, arr, "|");
#   for (i in cols) {
#	print "cols:" i;
#    }

    hasConflict = 0;
    for (j in arr) {
#	print "pred" arr[j];
	if (predToCols[arr[j]] in cols) {
#	    print "detected conflict on " $0 " for " arr[j];
	    hasConflict = 1;
#	    for (i in cols) {
#		print i;
#	    }
#	    print predToCols[arr[j]];
	    continue;
	}
	cols[predToCols[arr[j]]] = 1;
    }
    if (hasConflict == 1) {
	sumConflicts += $2;
    }
    total += $2;
}

END {
    print "total:" total, " sum conflicts:" sumConflicts;
    percent =  1.0 - (sumConflicts / (total * 1.0));
    print "covered:" percent;
}