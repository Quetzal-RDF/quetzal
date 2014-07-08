BEGIN {
    delete entityToID;
    i = 0;
}

function addEntity(e) {
    if (! (e in entityToID) ) {
	entityToID[e] = i;
	i++;
    }
}
{
    parse_for_elements($0, elts);
    subject = elts["subject"];
    predicate = elts["predicate"];
    object = elts["object"];
    addEntity(subject);
    addEntity(predicate);
    addEntity(object);
    print entityToID[subject], entityToID[predicate], entityToID[object];
}