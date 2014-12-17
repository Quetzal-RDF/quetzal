BEGIN {
    delete queryValues;
    FS=":";
}

{
    if ($2 in queryValues) {
	queryValues[$2] = queryValues[$2] "," $3;
    } else {
	queryValues[$2] = $3;
    }
}

END {
    for (q in queryValues) {
	split(queryValues[q], arr, ",");
	sum = 0;
	for (i=2; i <=numRuns; i++) {
	    sum += arr[i];
	} 
	avg = sum/7;
	sumsq = 0;
	for (i=2; i <=8; i++) {
	    sumsq = (arr[i] - avg) * (arr[i] - avg);
	}
	std = sqrt(sumsq / (7-1));
	print q, avg, std;
    }
}

