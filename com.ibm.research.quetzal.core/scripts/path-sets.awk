BEGIN {
    id=0;
    delete ids;
}

function get_id(x) {
    if (x in ids) {
	return ids[x];
    } else {
	ids[x] = id;
	return id++;
    }
}

BEGIN {
    triples = 0;
    type_pred = "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>";
    literal_id = get_id("literal");
}


{
    parse_for_elements($0, a);
    subj = a["subject"];
    pred = a["predicate"];
    obj = a["object"];
    if (pred == type_pred) {
	types[get_id(subj)][get_id(obj)] = 1;
    } else {
	if (parseLiteral(obj, b) < 1) {
	    edges[get_id(subj)][get_id(pred)][get_id(obj)] = 1;
	} else {
	    edges[get_id(subj)][get_id(pred)][literal_id] = 1;
	}
    }
    if ((++triples % 100000) == 0) {
	print "done " triples;
    }
}

function paths(path, x, n, f) {
    if (x in edges) {
	for(pred in edges[x]) {
	    for(obj in edges[x][pred]) {
		if (n == 0) {
		    @f(path " " pred);
		} else {
		    if (path == "") {
			paths(pred, obj, n-1, f);
		    } else {
			paths(path " " pred, obj, n-1, f);
		    }
		}
	    }
	}
    } else {
	@f(path);
    }
}

function add_key(x) {
    edge_keys[x] = 1;
}

END {
    delete edge_sets;

    entities=0;
    for(s in edges) {

	if (s in types) {
	    n = asorti(types[s], t);
	    key=t[1];
	    for(i = 2; i <= n; i++) {
		key = key " " t[i];
	    }
	} else {
	    key = "-1";
	}

	delete edge_keys;
	paths("", s, 2, "add_key");
	n = asorti(edge_keys, e);
	for(i = 1; i <= n; i++) {
	    key = key "|" e[i] "|";
	}

	if (key in edge_sets) {
	    edge_sets[key]++;
	} else {
	    edge_sets[key] = 1;
	}

	if ((++entities % 100000) == 0) {
	    print "done " entities;
	}
    }

    for(es in edge_sets) {
	print edge_sets[es] " " es;
    }
}

