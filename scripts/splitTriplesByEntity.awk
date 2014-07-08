BEGIN {
    current_entity = "";
    current_graph = "";

    this_graph = "";
    this_entity = "";

    line = 0;
    total = 0;

    task_number = 0;

    delete counts;

    skip = 1;
    check = granularity * .01;
}

{ 
    if (line >= granularity) {
	if (no_skip != "yes" && line >= (granularity + check)) {
	    line = (granularity - skip*check);
	    skip = (skip * 1.1);
	    current_entity = "";
	    current_graph = "";
	} else {
	    parse_for_elements($0, elts);

	    if (splitBy == "subject") {
		this_entity = elts["subject"];
	    } else {
		this_entity = elts["object"];
	    }
	    
	    if (fileType == "quad") {
		this_graph = elts["graph"];
	    } else {
		this_graph = "DEF";
	    }
	    
	    if (current_entity == "") {
		current_entity = this_entity;
		current_graph = this_graph;
	    } else {
		if (this_entity != current_entity || this_graph != current_graph) {
		    current_entity = "";
		    current_graph = "";
		    line = 0;
		    skip = 1;
		    do {
			task_number++;
			if (task_number >= degree) {
			    task_number = 0;
			}
		    } while (counts[task_number] > ((1.1 * total) / degree));
		}
	    }
	}
    }
	
    print $0 > resultName "." task_number;
    line++;
    total++;
    counts[task_number]++;
}
