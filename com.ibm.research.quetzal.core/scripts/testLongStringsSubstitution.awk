BEGIN {
    count = 0;
    longStringCount = 0;
}

{
    if (fileType=="quad") {
	object = parse_for_object($0);
	graph = parse_for_graph($0);
    } else {
	object = parse_for_object($0);
	graph = "DEF";
    }
    subj = $1;
    if (length(subj) > cutoff || length(object) > cutoff || length($2) > cutoff || length(graph) > cutoff) {
	longStringCount++;
    }
    count++;
    
}
END {
    print "count:" count " long strings count " longStringCount;
}

