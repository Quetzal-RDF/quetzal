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
	n = split(queryValues[q], arr, ",");
	sum = 0;
	for (i=2; i <= n; i++) {
	    sum += arr[i];
	} 
	avg = sum/ ((n-1) * 1.0);
	sumsq = 0;
	for (i=2; i <= n; i++) {
	    sumsq = (arr[i] - avg) * (arr[i] - avg);
	}
	std = sqrt(sumsq / ( (n-1) * 1.0));
	print q, avg, std;
    }
}

