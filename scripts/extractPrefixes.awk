BEGIN {
    FS = "[|][|]";
}

function extractPrefix(str, arr, k) {
    if (index(str, "\"") == 1) {
	return;
    }
    if (index(str, "#") != 0) {
	split(str, arr, "#");
	sub(/</, "", arr[1]);
	print arr[1] "#";
    } else {
	n = split(str, arr, "/");
	prefix = "";
	for (k = 1; k < n; k++) {
	    prefix = prefix arr[k] "/";
	}
	sub(/</, "", prefix);
	if (prefix != "http://") {
	    print prefix;
	}
    }
}

{
    extractPrefix($1);
    extractPrefix($2);
    extractPrefix($3);
}