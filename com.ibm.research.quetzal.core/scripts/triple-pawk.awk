BEGIN {
    total = 0;
    nl=length("\n");
    next_line_last = 0;
    last_entity = 0;
    current_entity = -1;
    if (last != "yes") {
	first_entity = "yes";
    } else {
	first_entity = "no";
    }
}

{
    total += (length($0) + nl);

    # except for the first chunk, the first line may be partial, so skip it
    if (FNR==1 && (last != "yes")) {
	next;
    }

    # parse triple to split by entity
    if (parse_for_elements($0, elts) == 0) {
	print "bad triple: " $0 > "/dev/stderr";
	next;
    }

    next_entity = (splitFor=="object")? elts["object"]: elts["subject"];
    if (current_entity == -1) {
	current_entity = next_entity;
    } else if (current_entity != next_entity) {
	if (last_entity == current_entity) {
	    exit(0);
	}
	first_entity = "no";
	current_entity = next_entity;
    }
    
    if (next_line_last==1) {
	last_entity=current_entity;
    }

    if (total > chunk) {	
	if (first_entity=="yes") {
	    # entire file is first entity, so is being skipped
	    exit(0);
	} else {
	    next_line_last = 1;
	}
    }

    if (first_entity=="yes") {
	next;
    }
}

