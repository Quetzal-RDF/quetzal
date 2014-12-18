{
    count=$1;
    total += count;
    elts = split($0, garbage, /\|/);

    for(i = 1; i <= elts; i++) {
	cumCounts[i] += count;
    }
}

END {
    for(i in cumCounts) {
	print i, cumCounts[i], cumCounts[i]/total;
    }
}
