#!/bin/bash

DIR=`dirname $0`

awk -f $DIR/strings.awk $DIR/parse.awk -f '{
    print find_subect($0);
    print find_predicate($0);
    print find_object($0);
    graph = find_graph($0);
    if (trimString(graph)) != "") {
      print graph;
    }
}' $1 | sort | uniq | awk 'BEGIN {
    delete lengths;
}

{
    lengths[sprintf("%25d", length($0))]++;
}

END {
    bins = asorti(lengths, sorted_lengths);
    
    total = 0;
    for(i = 1; i <= bins; i++) {
        total += lengths[sorted_lengths[i]];
    }

    cumulative = 0;
    for(i = 1; i <= bins; i++) {
        cumulative += lengths[sorted_lengths[i]];
	printf("%d %d %d %f\n", sorted_lengths[i], lengths[sorted_lengths[i]], cumulative, cumulative/total);
    }
}'


