function computeAverage(triples, average, sum, count) {
    count = 0;
    sum = 0;
    average = 0;
    for (i in triples) {
	count++;
	sum = sum + triples[i];
    }
    average = sum / count;
    return average;
}

function computeStd(triples, average, std, sum, count) {
    sum = 0;
    count = 0;
    std = 0;
    for (i in triples) {
	count++;
	sum = sum + (triples[i] - average) * (triples[i] - average);
    }
    std = sqrt(sum / count);
    return std;
}

function printRange(triples, min, max) {
    min = triples[1];
    max = triples[1];
    for (i in triples) {
	if (triples[i] < min) {
	    min = triples[i];
	}
	if (triples[i] > max) {
	    max = triples[i];
	}
    }
    print "range:", min "-" max;
}

function increment(arr, key) {
    if (key in arr) {
	arr[key] = arr[key] + 1;
    } else {
	arr[key] = 1;
    }
}

BEGIN {
    delete entityTriples;
    delete entityPredicateTriples;
    delete strings;
    FS = "[|][|]";

}


{
    increment(entityTriples, $1);

    str = $1 "-" $2;
    increment(entityPredicateTriples, str);
    increment(strings, $1);
    increment(strings, $2);
    increment(strings, $3);
}

END {
    avg = computeAverage(entityTriples);
    print "Average entity:" avg;
    print "Std entity:" computeStd(entityTriples, avg);
    printRange(entityTriples);

    avg = computeAverage(entityPredicateTriples);
    print "Average entity-predicate:" avg;
    print "Std entity predicate:" computeStd(entityPredicateTriples, avg);
    printRange(entityPredicateTriples);

    delete newstrings;
    for (i in strings) {
	for (j = 0; j < strings[i]; j++) {
	    newstrings[i "-" j] = length(i);
	}
    }
    avg = computeAverage(newstrings);
    print "Average strings:" avg;
    print "Std strings:" computeStd(newstrings, avg);
    printRange(newstrings);
}
