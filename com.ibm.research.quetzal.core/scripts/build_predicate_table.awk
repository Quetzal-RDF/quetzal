function file_type(filename) {
    match(filename, /^.*[.]([^0-9.]+)(.[0-9]+)?$/, a)
    return substr(filename, a[1, "start"], a[1, "length"]);
}

function get_pred(predicate) {
    match(predicate, /^[<]?([^>]+)[>]?$/, a)
    return substr(predicate, a[1, "start"], a[1, "length"]);
}

{
    type = file_type(FILENAME);
    pred = get_pred($1);
    if (type == "pred_info") {
	hashes = $2;
	spills = $3;

	if (!(pred in hash_count) || hash_count[pred]<hashes) {
	    hash_count[pred] = hashes;
	}

	if (!(pred in pred_spills) || pred_spills[pred]==0) {
	    pred_spills[pred] = spills;
	}

    } else if (type == "pred_types") {
	pred_type=$2;
	if (pred_type=="mixed") {
	    pred_types[pred]="mixed";
	} else if (! (pred in pred_types)) {
	    pred_types[pred]=pred_type;
	} else if (pred_type != pred_types[pred]) {
	    pred_types[pred]="mixed";
	}
	
    } else if (type == "hashes") {
	for(i = 2; i <= NF; i++) {
	    pred_hashes[pred, i-2] = $i;
	}
    
    } else if (type == "multi") {
	pred_multi[pred] = 1;
    }
}

END {
    for(pred in hash_count) {
	# pred
	printf("%s\t", hash_string(fixBrackets(pred),pred));
	
	# one-to-one
	if (pred_multi[pred]==1) {
	    printf("0\t");
	} else {
	    printf("1\t");
	}
	
	# spills
	if (pred_spills[pred]==1) {
	    printf("1\t");
	} else {
	    printf("0\t");
	}

	# hashes (3 is the max in our system)
	printf("%s\t", hash_count[pred]);
	for(i = 0; i < hash_count[pred]; i++) {
	    printf("%s\t", pred_hashes[pred, i]);
	}
	while (i++ <= 3) {
	    printf("-1\t");
	}

	# type
	print pred_types[pred];
    }
}
