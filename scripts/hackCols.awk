{
    if ($2 == "<http://com.ibm.rdf/column>") {
	split($3, arr, "\"");
	print $1, $2, "\"" arr[2]+1 "\"" "^^<http://www.w3.org/2001/XMLSchema#integer>", $4, $5;
    } else {
	print $0;
    }
}