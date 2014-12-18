BEGIN {
    prev = "";
    delete preds;
    delete predCounts;
    delete predsWithURIs;
    delete predsWithLiterals;
    print "sumoutedges", "singleValued", "multiValued", "maxMultiValued", "dist[5]", "dist[10]", "dist[20]", "dist[21]";
}

function processSubject(singleValued, multiValued, maxMultiValued, dist, sum) {
    singleValued = 0;
    multiValued = 0;
    maxMultiValued = 0;
    sum = 0;

    dist[5] = 0;
    dist[10] = 0;
    dist[20] = 0;
    dist[21] = 0;

    for (i in preds) {
	if (preds[i] > 1) {
	    multiValued++;

	    sum+=preds[i];
	    if (maxMultiValued < preds[i]) {
		maxMultiValued = preds[i];
	    }
	    if (preds[i] < 5) {
		dist[5]++;
	    } else if (preds[i] < 10) {
		dist[10]++;
	    } else if (preds[i] < 20) {
		dist[20]++;
	    } else {
		dist[21]++;
	    }
	} else {
	    singleValued++;
	    sum++;
	}
	if (predCounts[i] < preds[i]) {
	    predCounts[i] = preds[i];
	}
    }
    print sum, singleValued, multiValued, maxMultiValued, dist[5], dist[10], dist[20], dist[21]; 
}

{
    if (prev != "" && $1 != prev) {
	processSubject();
	delete preds;
    }

    prev = $1;

    if (index($3,"<") == 1) {
	predsWithURIs[$2]++;
    } else {
	predsWithLiterals[$2]++;
    }

    preds[$2]++;
    
}

END {
    processSubject();
    for (i in predsWithURIs) {
	if (i in predsWithLiterals) {
	    print "pred " i " has both uris and literals with counts:", predsWithURIs[i], predsWithLiterals[i];
	} else {
	    print "pred " i " only has uris with count:", predsWithURIs[i];
	}
    }
    for (i in predsWithLiterals) {
	if (!(i in predsWithURIs)) {
	    print "pred " i " only has literals with count:", predsWithLiterals[i];
	} 
    }
    for (pred in predCounts) {
	print "predicate " pred " has max degree " predCounts[pred];
    }
}
